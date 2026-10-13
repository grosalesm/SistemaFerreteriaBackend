package com.ferreteria.msproductos.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductoResponse(
        Long idProducto,
        String sku,
        String nombre,
        String descripcion,
        Long idCategoria,
        String unidadMedida,
        BigDecimal precioMinorista,
        BigDecimal precioContratista,
        Integer cantidadMinimaDescuento,
        BigDecimal porcentajeDescuento,
        Integer stockMinimo,
        Boolean activo,
        LocalDateTime fechaCreacion,
        Integer disponible,
        List<DisponibleAlmacen> disponiblePorAlmacen) {

    public record DisponibleAlmacen(Long idAlmacen, Integer disponible) {
    }
}
