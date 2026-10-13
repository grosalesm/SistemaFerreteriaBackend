package com.ferreteria.msproductos.infrastructure.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @NotBlank(message = "El SKU es obligatorio")
        @Size(max = 30, message = "El SKU admite máximo 30 caracteres")
        String sku,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre admite máximo 150 caracteres")
        String nombre,

        @Size(max = 300, message = "La descripción admite máximo 300 caracteres")
        String descripcion,

        @NotNull(message = "La categoría es obligatoria")
        Long idCategoria,

        @NotBlank(message = "La unidad de medida es obligatoria")
        @Size(max = 20, message = "La unidad de medida admite máximo 20 caracteres")
        String unidadMedida,

        @NotNull(message = "El precio minorista es obligatorio")
        BigDecimal precioMinorista,

        @NotNull(message = "El precio contratista es obligatorio")
        BigDecimal precioContratista,

        @NotNull(message = "La cantidad mínima para descuento es obligatoria")
        @PositiveOrZero(message = "La cantidad mínima para descuento no puede ser negativa")
        Integer cantidadMinimaDescuento,

        @NotNull(message = "El porcentaje de descuento es obligatorio")
        BigDecimal porcentajeDescuento,

        @NotNull(message = "El stock mínimo es obligatorio")
        Integer stockMinimo) {
}
