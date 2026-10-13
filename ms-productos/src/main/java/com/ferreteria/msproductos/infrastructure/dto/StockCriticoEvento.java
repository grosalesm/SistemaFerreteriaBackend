package com.ferreteria.msproductos.infrastructure.dto;

public record StockCriticoEvento(
        Long idProducto,
        String nombre,
        Long idAlmacen,
        Integer disponible,
        Integer stockMinimo) {
}
