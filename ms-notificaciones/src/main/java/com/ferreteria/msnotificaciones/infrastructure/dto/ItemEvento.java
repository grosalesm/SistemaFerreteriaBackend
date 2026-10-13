package com.ferreteria.msnotificaciones.infrastructure.dto;

import java.math.BigDecimal;

public record ItemEvento(
        Long idProducto,
        String sku,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {
}
