package com.ferreteria.msproductos.infrastructure.dto;

import java.util.List;

import com.ferreteria.msproductos.domain.bean.TipoCliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrecioRequest(
        @NotNull(message = "El tipo de cliente es obligatorio")
        TipoCliente tipoCliente,

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
