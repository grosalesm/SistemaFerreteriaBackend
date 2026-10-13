package com.ferreteria.mscotizaciones.infrastructure.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CotizacionRequest(
        Long clienteId,

        @NotNull(message = "El almacén es obligatorio")
        Long idAlmacen,

        @NotEmpty(message = "Debe enviar al menos un producto")
        List<@Valid ItemRequest> items) {
}
