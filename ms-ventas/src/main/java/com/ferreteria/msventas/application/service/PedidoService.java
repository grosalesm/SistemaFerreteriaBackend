package com.ferreteria.msventas.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msventas.application.port.outservice.EventoOutService;
import com.ferreteria.msventas.application.port.outservice.PedidoOutService;
import com.ferreteria.msventas.application.port.usecase.PedidoUseCase;
import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.domain.bean.ItemPedido;
import com.ferreteria.msventas.domain.bean.Pago;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.domain.bean.TipoComprobante;
import com.ferreteria.msventas.domain.bean.TipoEntrega;
import com.ferreteria.msventas.domain.constraint.PedidoConstraints;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService implements PedidoUseCase {

    private static final LocalDate FECHA_MINIMA = LocalDate.of(2000, 1, 1);
    private static final LocalDate FECHA_MAXIMA = LocalDate.of(2999, 12, 31);

    private final PedidoOutService pedidoOutService;
    private final EventoOutService eventoOutService;
    private final PedidoConstraints pedidoConstraints;

    @Value("${venta.horas-limite-pago}")
    private int horasLimitePago;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listar(EstadoPedido estado, Long clienteId, LocalDate desde, LocalDate hasta,
                               String rol, Long clienteIdToken) {
        Long filtroCliente = clienteId;
        if (esCliente(rol)) {
            validarCliente(clienteIdToken, clienteIdToken);
            filtroCliente = clienteIdToken;
        }
        LocalDateTime inicio = (desde == null ? FECHA_MINIMA : desde).atStartOfDay();
        LocalDateTime fin = (hasta == null ? FECHA_MAXIMA : hasta).plusDays(1).atStartOfDay();
        return pedidoOutService.listar(estado, filtroCliente, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId, String rol, Long clienteIdToken) {
        if (esCliente(rol)) {
            validarCliente(clienteId, clienteIdToken);
        }
        return pedidoOutService.listar(null, clienteId, FECHA_MINIMA.atStartOfDay(), FECHA_MAXIMA.atStartOfDay());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPendientesDespacho() {
        return pedidoOutService.listarPorEstados(List.of(EstadoPedido.PAGADO, EstadoPedido.EN_PREPARACION));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorId(Long idPedido, String rol, Long clienteIdToken) {
        return obtenerConAcceso(idPedido, rol, clienteIdToken);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comprobante> obtenerComprobante(Long idPedido, String rol, Long clienteIdToken) {
        return obtenerConAcceso(idPedido, rol, clienteIdToken).map(Pedido::getComprobante);
    }

    @Override
    @Transactional
    public boolean crearDesdeCotizacion(Pedido datos) {
        if (pedidoOutService.existePorCotizacionId(datos.getCotizacionId())) {
            return false;
        }
        LocalDateTime ahora = LocalDateTime.now();
        datos.setEstado(EstadoPedido.PENDIENTE_PAGO);
        datos.setFechaCreacion(ahora);
        datos.setFechaLimitePago(ahora.plusHours(horasLimitePago));
        datos.getItems().forEach(item -> item.setPreparado(false));
        eventoOutService.publicarCambioEstado(pedidoOutService.insertar(datos));
        return true;
    }

    @Override
    @Transactional
    public Optional<Pedido> registrarPago(Long idPedido, Pago pago, TipoEntrega tipoEntrega, String direccionEntrega,
                                          Long cajeroId) {
        return pedidoOutService.obtenerPorId(idPedido).map(pedido -> {
            validarEstado(pedidoConstraints.puedePagar(pedido), "Solo se puede pagar un pedido en PENDIENTE_PAGO");
            validarEstado(pedidoConstraints.montoValido(pedido, pago.getMonto()),
                    "El pago es al contado: el monto debe ser igual al total " + pedido.getTotal());
            validarEstado(pedidoConstraints.pagoValido(pago.getMetodo(), pago.getNumeroOperacion()),
                    "El número de operación es obligatorio para pagos con " + pago.getMetodo());
            boolean sinDireccion = direccionEntrega == null || direccionEntrega.isBlank();
            validarEstado(tipoEntrega != TipoEntrega.DELIVERY || !sinDireccion,
                    "La dirección de entrega es obligatoria para DELIVERY");

            LocalDateTime ahora = LocalDateTime.now();
            pago.setIdPago(null);
            pago.setIdPedido(idPedido);
            pago.setCajeroId(cajeroId);
            pago.setFecha(ahora);
            pago.setDevuelto(false);
            pedidoOutService.guardarPago(pago);

            pedido.setTipoEntrega(tipoEntrega);
            pedido.setDireccionEntrega(tipoEntrega == TipoEntrega.DELIVERY ? direccionEntrega.trim() : null);
            pedido.setFechaPago(ahora);
            pedido.setEstado(EstadoPedido.PAGADO);
            return guardarYPublicar(pedido);
        });
    }

    @Override
    @Transactional
    public Optional<Comprobante> emitirComprobante(Long idPedido, Comprobante datos) {
        return pedidoOutService.obtenerPorId(idPedido).map(pedido -> {
            validarEstado(pedidoConstraints.puedeEmitirComprobante(pedido), "Solo se emite comprobante a un pedido pagado");
            if (pedido.getComprobante() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El pedido ya tiene el comprobante "
                        + pedido.getComprobante().getSerie() + "-" + pedido.getComprobante().getNumero());
            }
            validarEstado(pedidoConstraints.documentoValido(datos.getTipo(), datos.getDocumentoCliente()),
                    "La BOLETA requiere DNI de 8 dígitos y la FACTURA RUC de 11 dígitos");

            String serie = datos.getTipo() == TipoComprobante.BOLETA ? "B001" : "F001";
            Comprobante comprobante = new Comprobante();
            comprobante.setIdPedido(idPedido);
            comprobante.setTipo(datos.getTipo());
            comprobante.setSerie(serie);
            comprobante.setNumero(pedidoOutService.ultimoNumero(serie) + 1);
            comprobante.setDocumentoCliente(datos.getDocumentoCliente());
            comprobante.setRazonSocial(datos.getRazonSocial() == null || datos.getRazonSocial().isBlank()
                    ? pedido.getClienteNombre() : datos.getRazonSocial().trim());
            comprobante.setFechaEmision(LocalDateTime.now());
            pedidoConstraints.calcularImportes(comprobante, pedido.getTotal());
            return pedidoOutService.guardarComprobante(comprobante);
        });
    }

    @Override
    @Transactional
    public Optional<Pedido> preparar(Long idPedido, Long almaceneroId) {
        return pedidoOutService.obtenerPorId(idPedido).map(pedido -> {
            validarEstado(pedidoConstraints.puedePreparar(pedido), "Solo se puede preparar un pedido PAGADO");
            pedido.setAlmaceneroId(almaceneroId);
            pedido.setFechaPreparacion(LocalDateTime.now());
            pedido.setEstado(EstadoPedido.EN_PREPARACION);
            return guardarYPublicar(pedido);
        });
    }

    @Override
    @Transactional
    public Optional<Pedido> marcarChecklist(Long idPedido, List<Long> itemsPreparados) {
        return pedidoOutService.obtenerPorId(idPedido).map(pedido -> {
            validarEstado(pedido.getEstado() == EstadoPedido.EN_PREPARACION,
                    "El checklist solo se marca en pedidos EN_PREPARACION");
            Set<Long> idsDelPedido = pedido.getItems().stream().map(ItemPedido::getIdItem).collect(Collectors.toSet());
            validarEstado(idsDelPedido.containsAll(itemsPreparados), "Algún item no pertenece al pedido");
            pedido.getItems().stream()
                    .filter(item -> itemsPreparados.contains(item.getIdItem()))
                    .forEach(item -> item.setPreparado(true));
            return guardar(pedido);
        });
    }

    @Override
    @Transactional
    public Optional<Pedido> despachar(Long idPedido, String repartidor) {
        return pedidoOutService.obtenerPorId(idPedido).map(pedido -> {
            if (repartidor != null && !repartidor.isBlank()) {
                pedido.setRepartidor(repartidor.trim());
            }
            validarEstado(pedidoConstraints.puedeDespachar(pedido),
                    "Para despachar el pedido debe estar EN_PREPARACION, con todos los items preparados "
                            + "y con repartidor si es DELIVERY");
            pedido.setFechaDespacho(LocalDateTime.now());
            pedido.setEstado(EstadoPedido.DESPACHADO);
            return guardarYPublicar(pedido);
        });
    }

    @Override
    @Transactional
    public Optional<Pedido> entregar(Long idPedido) {
        return pedidoOutService.obtenerPorId(idPedido).map(pedido -> {
            validarEstado(pedidoConstraints.puedeEntregar(pedido), "Solo se puede entregar un pedido DESPACHADO");
            pedido.setFechaEntrega(LocalDateTime.now());
            pedido.setEstado(EstadoPedido.ENTREGADO);
            return guardarYPublicar(pedido);
        });
    }

    @Override
    @Transactional
    public Optional<Pedido> anular(Long idPedido, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de anulación es obligatorio");
        }
        return pedidoOutService.obtenerPorId(idPedido).map(pedido -> {
            validarEstado(pedidoConstraints.puedeAnular(pedido), "Solo se puede anular un pedido PENDIENTE_PAGO o PAGADO");
            if (pedido.getPago() != null) {
                pedido.getPago().setDevuelto(true);
                pedidoOutService.guardarPago(pedido.getPago());
            }
            pedido.setMotivoAnulacion(motivo.trim());
            pedido.setEstado(EstadoPedido.ANULADO);
            return guardarYPublicar(pedido);
        });
    }

    @Override
    @Transactional
    public int vencerPedidos() {
        List<Pedido> vencidos = pedidoOutService.listarPendientesVencidos(LocalDateTime.now());
        for (Pedido pedido : vencidos) {
            pedido.setEstado(EstadoPedido.VENCIDO);
            guardarYPublicar(pedido);
        }
        return vencidos.size();
    }

    private Optional<Pedido> obtenerConAcceso(Long idPedido, String rol, Long clienteIdToken) {
        Optional<Pedido> pedido = pedidoOutService.obtenerPorId(idPedido);
        if (pedido.isPresent() && !pedidoConstraints.puedeAcceder(pedido.get(), rol, clienteIdToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene acceso a este pedido");
        }
        return pedido;
    }

    private Pedido guardarYPublicar(Pedido pedido) {
        Pedido guardado = guardar(pedido);
        eventoOutService.publicarCambioEstado(guardado);
        return guardado;
    }

    private Pedido guardar(Pedido pedido) {
        return pedidoOutService.actualizar(pedido.getIdPedido(), pedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    private void validarCliente(Long clienteId, Long clienteIdToken) {
        if (clienteIdToken == null || !Objects.equals(clienteId, clienteIdToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puede consultar sus propios pedidos");
        }
    }

    private void validarEstado(boolean permitido, String mensaje) {
        if (!permitido) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private boolean esCliente(String rol) {
        return "CLIENTE".equals(rol);
    }
}
