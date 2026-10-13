package com.ferreteria.msventas.infrastructure.dto;

import jakarta.validation.constraints.Size;

public record DespachoRequest(
        @Size(max = 100, message = "El repartidor no puede superar 100 caracteres")
        String repartidor) {
}
