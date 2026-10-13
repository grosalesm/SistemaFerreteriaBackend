package com.ferreteria.msventas.application.port.outservice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.domain.bean.Pago;
import com.ferreteria.msventas.domain.bean.Pedido;

public interface PedidoOutService {

    List<Pedido> listar(EstadoPedido estado, Long clienteId, LocalDateTime desde, LocalDateTime hasta);
    List<Pedido> listarPorEstados(List<EstadoPedido> estados);
    List<Pedido> listarPendientesVencidos(LocalDateTime fecha);
    Optional<Pedido> obtenerPorId(Long idPedido);
    boolean existePorCotizacionId(Long cotizacionId);
    Pedido insertar(Pedido pedido);
    Optional<Pedido> actualizar(Long idPedido, Pedido pedido);
    Pago guardarPago(Pago pago);
    Comprobante guardarComprobante(Comprobante comprobante);
    int ultimoNumero(String serie);
}
