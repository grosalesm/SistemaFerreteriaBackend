package com.ferreteria.msproductos.infrastructure.dto;

import java.math.BigDecimal;

public record PrecioResponse(
        Long idProducto,
        String sku,
        String nombre,
        BigDecimal precioUnitario,
        BigDecimal porcentajeDescuento,
        BigDecimal subtotal,
        Integer disponible) {
}
