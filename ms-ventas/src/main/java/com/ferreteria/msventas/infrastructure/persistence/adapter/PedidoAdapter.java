package com.ferreteria.msventas.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ferreteria.msventas.application.port.outservice.PedidoOutService;
import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.domain.bean.ItemPedido;
import com.ferreteria.msventas.domain.bean.Pago;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.infrastructure.mapper.PedidoMapper;
import com.ferreteria.msventas.infrastructure.persistence.entity.PedidoEntity;
import com.ferreteria.msventas.infrastructure.persistence.repository.ComprobanteRepository;
import com.ferreteria.msventas.infrastructure.persistence.repository.PagoRepository;
import com.ferreteria.msventas.infrastructure.persistence.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoAdapter implements PedidoOutService {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public List<Pedido> listar(EstadoPedido estado, Long clienteId, LocalDateTime desde, LocalDateTime hasta) {
        return pedidoMapper.toBeans(pedidoRepository.buscar(estado, clienteId, desde, hasta));
    }

    @Override
    public List<Pedido> listarPorEstados(List<EstadoPedido> estados) {
        return pedidoMapper.toBeans(pedidoRepository.findByEstadoInOrderByFechaPagoAsc(estados));
    }

    @Override
    public List<Pedido> listarPendientesVencidos(LocalDateTime fecha) {
        return pedidoMapper.toBeans(
                pedidoRepository.findByEstadoAndFechaLimitePagoBefore(EstadoPedido.PENDIENTE_PAGO, fecha));
    }

    @Override
    public Optional<Pedido> obtenerPorId(Long idPedido) {
        return pedidoRepository.findById(idPedido).map(this::completo);
    }

    @Override
    public boolean existePorCotizacionId(Long cotizacionId) {
        return pedidoRepository.existsByCotizacionId(cotizacionId);
    }

    @Override
    public Pedido insertar(Pedido pedido) {
        PedidoEntity entity = pedidoMapper.toEntity(pedido);
        entity.setIdPedido(null);
        entity.setCodigo(null);
        entity.getItems().forEach(item -> item.setIdItem(null));
        PedidoEntity guardado = pedidoRepository.save(entity);
        guardado.setCodigo(String.format("PED-%d-%06d", guardado.getFechaCreacion().getYear(), guardado.getIdPedido()));
        return pedidoMapper.toBean(pedidoRepository.save(guardado));
    }

    @Override
    public Optional<Pedido> actualizar(Long idPedido, Pedido pedido) {
        return pedidoRepository.findById(idPedido)
                .map(entity -> {
                    entity.setEstado(pedido.getEstado());
                    entity.setTipoEntrega(pedido.getTipoEntrega());
                    entity.setDireccionEntrega(pedido.getDireccionEntrega());
                    entity.setRepartidor(pedido.getRepartidor());
                    entity.setFechaPago(pedido.getFechaPago());
                    entity.setFechaPreparacion(pedido.getFechaPreparacion());
                    entity.setFechaDespacho(pedido.getFechaDespacho());
                    entity.setFechaEntrega(pedido.getFechaEntrega());
                    entity.setAlmaceneroId(pedido.getAlmaceneroId());
                    entity.setMotivoAnulacion(pedido.getMotivoAnulacion());
                    Map<Long, ItemPedido> items = pedido.getItems().stream()
                            .collect(Collectors.toMap(ItemPedido::getIdItem, Function.identity()));
                    entity.getItems().forEach(item -> item.setPreparado(items.get(item.getIdItem()).getPreparado()));
                    return completo(pedidoRepository.saveAndFlush(entity));
                });
    }

    @Override
    public Pago guardarPago(Pago pago) {
        return pedidoMapper.toPago(pagoRepository.saveAndFlush(pedidoMapper.toPagoEntity(pago)));
    }

    @Override
    public Comprobante guardarComprobante(Comprobante comprobante) {
        return pedidoMapper.toComprobante(comprobanteRepository.saveAndFlush(pedidoMapper.toComprobanteEntity(comprobante)));
    }

    @Override
    public int ultimoNumero(String serie) {
        return comprobanteRepository.ultimoNumero(serie);
    }

    private Pedido completo(PedidoEntity entity) {
        Pedido pedido = pedidoMapper.toBean(entity);
        pedido.setPago(pagoRepository.findByIdPedido(entity.getIdPedido()).map(pedidoMapper::toPago).orElse(null));
        pedido.setComprobante(comprobanteRepository.findByIdPedido(entity.getIdPedido())
                .map(pedidoMapper::toComprobante).orElse(null));
        return pedido;
    }
}
