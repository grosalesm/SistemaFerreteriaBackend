package com.ferreteria.mscotizaciones.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.mscotizaciones.application.port.outservice.CotizacionOutService;
import com.ferreteria.mscotizaciones.application.port.outservice.EventoOutService;
import com.ferreteria.mscotizaciones.application.port.usecase.CotizacionUseCase;
import com.ferreteria.mscotizaciones.domain.bean.Canal;
import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;
import com.ferreteria.mscotizaciones.domain.bean.HistorialEstado;
import com.ferreteria.mscotizaciones.domain.bean.ItemCotizacion;
import com.ferreteria.mscotizaciones.domain.constraint.CotizacionConstraints;
import com.ferreteria.mscotizaciones.infrastructure.client.ClienteClient;
import com.ferreteria.mscotizaciones.infrastructure.client.ClienteClient.ClienteResumen;
import com.ferreteria.mscotizaciones.infrastructure.client.ProductoClient;
import com.ferreteria.mscotizaciones.infrastructure.client.ProductoClient.PrecioRequest;
import com.ferreteria.mscotizaciones.infrastructure.client.ProductoClient.PrecioResponse;
import com.ferreteria.mscotizaciones.infrastructure.client.ProductoClient.ReservaRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CotizacionService implements CotizacionUseCase {

    private static final LocalDate FECHA_MINIMA = LocalDate.of(2000, 1, 1);
    private static final LocalDate FECHA_MAXIMA = LocalDate.of(2999, 12, 31);

    private final CotizacionOutService cotizacionOutService;
    private final EventoOutService eventoOutService;
    private final CotizacionConstraints cotizacionConstraints;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    @Value("${cotizacion.dias-vigencia}")
    private int diasVigencia;

    @Override
    @Transactional(readOnly = true)
    public List<Cotizacion> listar(EstadoCotizacion estado, Long clienteId, LocalDate desde, LocalDate hasta,
                                   String rol, Long clienteIdToken) {
        Long filtroCliente = esCliente(rol) ? clienteIdToken : clienteId;
        LocalDateTime inicio = (desde == null ? FECHA_MINIMA : desde).atStartOfDay();
        LocalDateTime fin = (hasta == null ? FECHA_MAXIMA : hasta).plusDays(1).atStartOfDay();
        return cotizacionOutService.listar(estado, filtroCliente, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cotizacion> obtenerPorId(Long idCotizacion, String rol, Long clienteIdToken) {
        return obtenerConAcceso(idCotizacion, rol, clienteIdToken);
    }

    @Override
    @Transactional
    public Cotizacion crear(Cotizacion datos, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken) {
        Cotizacion cotizacion = armar(datos, usuarioId, rol, clienteIdToken);
        return guardarBorrador(cotizacion, usuarioId, usuarioNombre, "Cotización creada");
    }

    @Override
    public Cotizacion simular(Cotizacion datos, String rol, Long clienteIdToken) {
        return armar(datos, null, rol, clienteIdToken);
    }

    @Override
    @Transactional
    public Optional<Cotizacion> editarItems(Long idCotizacion, List<ItemCotizacion> items, Long usuarioId,
                                            String rol, Long clienteIdToken) {
        return obtenerConAcceso(idCotizacion, rol, clienteIdToken).map(cotizacion -> {
            validarEstado(cotizacionConstraints.puedeEditar(cotizacion), "Solo se pueden editar cotizaciones en BORRADOR");
            asignarCliente(cotizacion);
            cotizarItems(cotizacion, items);
            return guardar(cotizacion);
        });
    }

    @Override
    @Transactional
    public Optional<Cotizacion> enviar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken) {
        return obtenerConAcceso(idCotizacion, rol, clienteIdToken).map(cotizacion -> {
            validarEstado(cotizacionConstraints.puedeEnviar(cotizacion), "Solo se puede enviar una cotización en BORRADOR con productos");
            cotizacion.setFechaVencimiento(LocalDateTime.now().plusDays(diasVigencia));
            cambiarEstado(cotizacion, EstadoCotizacion.ENVIADA, usuarioId, usuarioNombre, null);
            Cotizacion guardada = guardar(cotizacion);
            eventoOutService.publicarCambioEstado(guardada);
            return guardada;
        });
    }

    @Override
    @Transactional
    public Optional<Cotizacion> aprobar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken) {
        return obtenerConAcceso(idCotizacion, rol, clienteIdToken).map(cotizacion -> {
            validarEstado(cotizacionConstraints.puedeAprobar(cotizacion), "Solo se puede aprobar una cotización ENVIADA y vigente");
            obtenerClienteActivo(cotizacion.getClienteId());

            List<ProductoClient.Item> items = cotizacion.getItems().stream()
                    .map(i -> new ProductoClient.Item(i.getIdProducto(), i.getCantidad()))
                    .toList();
            productoClient.reservar(new ReservaRequest(cotizacion.getIdCotizacion(), cotizacion.getIdAlmacen(), items));

            cotizacion.setFechaAprobacion(LocalDateTime.now());
            cambiarEstado(cotizacion, EstadoCotizacion.APROBADA, usuarioId, usuarioNombre, null);
            Cotizacion guardada;
            try {
                guardada = guardar(cotizacion);
            } catch (RuntimeException e) {
                liberarReserva(cotizacion.getIdCotizacion());
                throw e;
            }
            eventoOutService.publicarCambioEstado(guardada);
            return guardada;
        });
    }

    @Override
    @Transactional
    public Optional<Cotizacion> rechazar(Long idCotizacion, String motivo, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");
        }
        return obtenerConAcceso(idCotizacion, rol, clienteIdToken).map(cotizacion -> {
            validarEstado(cotizacionConstraints.puedeRechazar(cotizacion), "Solo se puede rechazar una cotización ENVIADA");
            cotizacion.setMotivoRechazo(motivo.trim());
            cambiarEstado(cotizacion, EstadoCotizacion.RECHAZADA, usuarioId, usuarioNombre, motivo.trim());
            Cotizacion guardada = guardar(cotizacion);
            eventoOutService.publicarCambioEstado(guardada);
            return guardada;
        });
    }

    @Override
    @Transactional
    public Optional<Cotizacion> reenviar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken) {
        return obtenerConAcceso(idCotizacion, rol, clienteIdToken).map(origen -> {
            validarEstado(cotizacionConstraints.puedeReenviar(origen), "Solo se puede reenviar una cotización VENCIDA o RECHAZADA");
            return copiar(origen, usuarioId, usuarioNombre, rol, clienteIdToken, "Reenvío de " + origen.getCodigo());
        });
    }

    @Override
    @Transactional
    public Optional<Cotizacion> duplicar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken) {
        return obtenerConAcceso(idCotizacion, rol, clienteIdToken)
                .map(origen -> copiar(origen, usuarioId, usuarioNombre, rol, clienteIdToken, "Duplicado de " + origen.getCodigo()));
    }

    @Override
    @Transactional
    public int vencerCotizaciones() {
        List<Cotizacion> vencidas = cotizacionOutService.listarEnviadasVencidas(LocalDateTime.now());
        for (Cotizacion cotizacion : vencidas) {
            cambiarEstado(cotizacion, EstadoCotizacion.VENCIDA, null, null, "Vencida automáticamente");
            eventoOutService.publicarCambioEstado(guardar(cotizacion));
        }
        return vencidas.size();
    }

    @Override
    @Transactional
    public boolean marcarConvertida(Long cotizacionId, Long pedidoId) {
        Cotizacion cotizacion = cotizacionOutService.obtenerPorId(cotizacionId).orElse(null);
        if (cotizacion == null || cotizacion.getEstado() != EstadoCotizacion.APROBADA) {
            return false;
        }
        cambiarEstado(cotizacion, EstadoCotizacion.CONVERTIDA, null, null, "Convertida en el pedido " + pedidoId);
        guardar(cotizacion);
        return true;
    }

    private Optional<Cotizacion> obtenerConAcceso(Long idCotizacion, String rol, Long clienteIdToken) {
        Optional<Cotizacion> cotizacion = cotizacionOutService.obtenerPorId(idCotizacion);
        if (cotizacion.isPresent() && !cotizacionConstraints.puedeAcceder(cotizacion.get(), rol, clienteIdToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene acceso a esta cotización");
        }
        return cotizacion;
    }

    private Cotizacion armar(Cotizacion datos, Long usuarioId, String rol, Long clienteIdToken) {
        Cotizacion cotizacion = new Cotizacion();
        if (esCliente(rol)) {
            if (clienteIdToken == null) {
                throw new IllegalArgumentException("El usuario no está asociado a un cliente");
            }
            cotizacion.setClienteId(clienteIdToken);
            cotizacion.setCanal(Canal.ANDROID);
        } else {
            if (datos.getClienteId() == null) {
                throw new IllegalArgumentException("El cliente es obligatorio");
            }
            cotizacion.setClienteId(datos.getClienteId());
            cotizacion.setVendedorId(usuarioId);
            cotizacion.setCanal(Canal.WEB);
        }
        cotizacion.setIdAlmacen(datos.getIdAlmacen());
        asignarCliente(cotizacion);
        cotizarItems(cotizacion, datos.getItems());
        return cotizacion;
    }

    private Cotizacion copiar(Cotizacion origen, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken, String comentario) {
        Cotizacion datos = new Cotizacion();
        datos.setClienteId(origen.getClienteId());
        datos.setIdAlmacen(origen.getIdAlmacen());
        datos.setItems(origen.getItems().stream()
                .map(i -> new ItemCotizacion(null, i.getIdProducto(), null, null, i.getCantidad(), null, null, null, null))
                .collect(Collectors.toList()));
        Cotizacion nueva = armar(datos, usuarioId, rol, clienteIdToken);
        nueva.setCotizacionOrigenId(origen.getIdCotizacion());
        return guardarBorrador(nueva, usuarioId, usuarioNombre, comentario);
    }

    private void asignarCliente(Cotizacion cotizacion) {
        ClienteResumen cliente = obtenerClienteActivo(cotizacion.getClienteId());
        cotizacion.setClienteNombre(cliente.nombreRazonSocial());
        cotizacion.setTipoCliente(cliente.tipoCliente());
    }

    private ClienteResumen obtenerClienteActivo(Long clienteId) {
        ClienteResumen cliente = clienteClient.obtenerResumen(clienteId);
        if (!Boolean.TRUE.equals(cliente.activo())) {
            throw new IllegalArgumentException("El cliente " + clienteId + " está inactivo");
        }
        return cliente;
    }

    private void cotizarItems(Cotizacion cotizacion, List<ItemCotizacion> items) {
        if (!cotizacionConstraints.validarItems(items)) {
            throw new IllegalArgumentException("Debe enviar de 1 a 50 productos, sin repetir y con cantidad mayor a 0");
        }
        List<ProductoClient.Item> pedidos = items.stream()
                .map(i -> new ProductoClient.Item(i.getIdProducto(), i.getCantidad()))
                .toList();
        Map<Long, PrecioResponse> precios = productoClient
                .calcularPrecios(new PrecioRequest(cotizacion.getTipoCliente(), cotizacion.getIdAlmacen(), pedidos))
                .stream()
                .collect(Collectors.toMap(PrecioResponse::idProducto, Function.identity()));

        List<ItemCotizacion> cotizados = new ArrayList<>();
        for (ItemCotizacion item : items) {
            PrecioResponse precio = precios.get(item.getIdProducto());
            String aviso = precio.disponible() < item.getCantidad()
                    ? "Stock insuficiente en el almacén: disponible " + precio.disponible()
                    : null;
            cotizados.add(new ItemCotizacion(null, precio.idProducto(), precio.sku(), precio.nombre(), item.getCantidad(),
                    precio.precioUnitario(), precio.porcentajeDescuento(), precio.subtotal(), aviso));
        }
        cotizacion.setItems(cotizados);
        cotizacionConstraints.calcularTotales(cotizacion);
    }

    private Cotizacion guardarBorrador(Cotizacion cotizacion, Long usuarioId, String usuarioNombre, String comentario) {
        cotizacion.setFechaCreacion(LocalDateTime.now());
        cambiarEstado(cotizacion, EstadoCotizacion.BORRADOR, usuarioId, usuarioNombre, comentario);
        return guardar(cotizacion);
    }

    private void cambiarEstado(Cotizacion cotizacion, EstadoCotizacion nuevoEstado, Long usuarioId, String usuarioNombre, String comentario) {
        cotizacion.getHistorial().add(new HistorialEstado(null, cotizacion.getEstado(), nuevoEstado, usuarioId, usuarioNombre,
                LocalDateTime.now(), comentario));
        cotizacion.setEstado(nuevoEstado);
    }

    private Cotizacion guardar(Cotizacion cotizacion) {
        Map<Long, String> avisos = cotizacion.getItems().stream()
                .filter(i -> i.getAviso() != null)
                .collect(Collectors.toMap(ItemCotizacion::getIdProducto, ItemCotizacion::getAviso));
        Cotizacion guardada = cotizacion.getIdCotizacion() == null
                ? cotizacionOutService.insertar(cotizacion)
                : cotizacionOutService.actualizar(cotizacion.getIdCotizacion(), cotizacion)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cotización no encontrada"));
        guardada.getItems().forEach(i -> i.setAviso(avisos.get(i.getIdProducto())));
        return guardada;
    }

    private void liberarReserva(Long cotizacionId) {
        try {
            productoClient.liberarReserva(cotizacionId);
        } catch (RuntimeException e) {
            log.warn("No se pudo liberar la reserva de la cotización {}: {}", cotizacionId, e.getMessage());
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
