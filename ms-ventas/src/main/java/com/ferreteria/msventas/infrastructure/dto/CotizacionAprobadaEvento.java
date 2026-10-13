package com.ferreteria.msventas.infrastructure.dto;

import java.math.BigDecimal;
import java.util.List;

public record CotizacionAprobadaEvento(
        Long cotizacionId,
        String codigo,
        Long clienteId,
        String clienteNombre,
        Long idAlmacen,
        BigDecimal total,
        List<Item> items) {

    public record Item(
            Long idProducto,
            String sku,
            String nombreProducto,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal) {
    }
}
