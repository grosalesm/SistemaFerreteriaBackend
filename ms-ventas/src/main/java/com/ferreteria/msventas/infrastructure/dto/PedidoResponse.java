package com.ferreteria.msventas.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.domain.bean.MetodoPago;
import com.ferreteria.msventas.domain.bean.TipoEntrega;

public record PedidoResponse(
        Long idPedido,
        String codigo,
        Long cotizacionId,
        String codigoCotizacion,
        Long clienteId,
        String clienteNombre,
        Long idAlmacen,
        EstadoPedido estado,
        BigDecimal total,
        TipoEntrega tipoEntrega,
        String direccionEntrega,
        String repartidor,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaLimitePago,
        LocalDateTime fechaPago,
        LocalDateTime fechaPreparacion,
        LocalDateTime fechaDespacho,
        LocalDateTime fechaEntrega,
        Long almaceneroId,
        String motivoAnulacion,
        List<ItemPedidoResponse> items,
        PagoResponse pago,
        ComprobanteResponse comprobante) {

    public record PagoResponse(
            Long idPago,
            MetodoPago metodo,
            BigDecimal monto,
            String numeroOperacion,
            Long cajeroId,
            LocalDateTime fecha,
            Boolean devuelto) {
    }
}
