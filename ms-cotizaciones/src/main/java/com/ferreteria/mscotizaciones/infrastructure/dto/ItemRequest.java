package com.ferreteria.mscotizaciones.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemRequest(
        @NotNull(message = "El producto es obligatorio")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad) {
}
