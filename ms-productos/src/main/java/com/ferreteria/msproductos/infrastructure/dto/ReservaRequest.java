package com.ferreteria.msproductos.infrastructure.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservaRequest(
        @NotNull(message = "La cotización es obligatoria")
        Long cotizacionId,

        @NotNull(message = "El almacén es obligatorio")
        Long idAlmacen,

        @NotEmpty(message = "Debe enviar al menos un item")
        List<@Valid Item> items) {

    public record Item(
            @NotNull(message = "El producto es obligatorio")
            Long idProducto,

            @NotNull(message = "La cantidad es obligatoria")
            @Positive(message = "La cantidad debe ser mayor a 0")
            Integer cantidad) {
    }
}
