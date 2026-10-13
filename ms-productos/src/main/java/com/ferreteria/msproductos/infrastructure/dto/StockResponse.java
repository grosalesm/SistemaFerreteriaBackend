package com.ferreteria.msproductos.infrastructure.dto;

public record StockResponse(
        Long idStock,
        Long idProducto,
        Long idAlmacen,
        Integer cantidadFisica,
        Integer cantidadReservada,
        Integer disponible) {
}
