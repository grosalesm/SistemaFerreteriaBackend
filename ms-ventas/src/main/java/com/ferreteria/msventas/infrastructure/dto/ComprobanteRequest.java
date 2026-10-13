package com.ferreteria.msventas.infrastructure.dto;

import com.ferreteria.msventas.domain.bean.TipoComprobante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComprobanteRequest(
        @NotNull(message = "El tipo de comprobante es obligatorio")
        TipoComprobante tipo,

        @NotBlank(message = "El documento del cliente es obligatorio")
        String documentoCliente,

        @Size(max = 150, message = "La razón social no puede superar 150 caracteres")
        String razonSocial) {
}
