package com.ferreteria.msproductos.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AjusteRequest(
        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "El almacén es obligatorio")
        Long idAlmacen,

        @NotNull(message = "La nueva cantidad es obligatoria")
        @PositiveOrZero(message = "La nueva cantidad no puede ser negativa")
        Integer nuevaCantidad,

        String motivo) {
}
