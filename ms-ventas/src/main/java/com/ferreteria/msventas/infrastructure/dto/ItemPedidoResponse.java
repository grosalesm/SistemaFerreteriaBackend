package com.ferreteria.msventas.infrastructure.dto;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long idItem,
        Long idProducto,
        String sku,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        Boolean preparado) {
}
