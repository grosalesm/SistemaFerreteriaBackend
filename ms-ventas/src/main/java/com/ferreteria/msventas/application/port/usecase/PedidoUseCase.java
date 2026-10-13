package com.ferreteria.msventas.application.port.usecase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.domain.bean.Pago;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.domain.bean.TipoEntrega;

public interface PedidoUseCase {

    List<Pedido> listar(EstadoPedido estado, Long clienteId, LocalDate desde, LocalDate hasta, String rol, Long clienteIdToken);
    List<Pedido> listarPorCliente(Long clienteId, String rol, Long clienteIdToken);
    List<Pedido> listarPendientesDespacho();
    Optional<Pedido> obtenerPorId(Long idPedido, String rol, Long clienteIdToken);
    Optional<Comprobante> obtenerComprobante(Long idPedido, String rol, Long clienteIdToken);
    boolean crearDesdeCotizacion(Pedido datos);
    Optional<Pedido> registrarPago(Long idPedido, Pago pago, TipoEntrega tipoEntrega, String direccionEntrega, Long cajeroId);
    Optional<Comprobante> emitirComprobante(Long idPedido, Comprobante datos);
    Optional<Pedido> preparar(Long idPedido, Long almaceneroId);
    Optional<Pedido> marcarChecklist(Long idPedido, List<Long> itemsPreparados);
    Optional<Pedido> despachar(Long idPedido, String repartidor);
    Optional<Pedido> entregar(Long idPedido);
    Optional<Pedido> anular(Long idPedido, String motivo);
    int vencerPedidos();
}
