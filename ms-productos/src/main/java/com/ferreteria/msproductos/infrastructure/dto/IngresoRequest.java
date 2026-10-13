package com.ferreteria.msproductos.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngresoRequest(
        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "El almacén es obligatorio")
        Long idAlmacen,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad) {
}
