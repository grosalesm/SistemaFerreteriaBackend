package com.ferreteria.msauth.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "El username es obligatorio")
        @Size(max = 50, message = "El username admite máximo 50 caracteres")
        String username,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100, message = "El nombre completo admite máximo 100 caracteres")
        String nombreCompleto,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        @Size(max = 120, message = "El email admite máximo 120 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @NotBlank(message = "El tipo de documento es obligatorio")
        String tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        String numeroDocumento,

        String telefono) {
}
