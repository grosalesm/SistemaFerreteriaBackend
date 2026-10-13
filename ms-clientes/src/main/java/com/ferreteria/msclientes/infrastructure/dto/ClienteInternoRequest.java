package com.ferreteria.msclientes.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ClienteInternoRequest(
        String tipoDocumento,

        String numeroDocumento,

        @Size(max = 150, message = "El nombre o razón social admite máximo 150 caracteres")
        String nombreRazonSocial,

        @Size(max = 20, message = "El teléfono admite máximo 20 caracteres")
        String telefono,

        @Email(message = "El email no es válido")
        @Size(max = 120, message = "El email admite máximo 120 caracteres")
        String email) {
}
