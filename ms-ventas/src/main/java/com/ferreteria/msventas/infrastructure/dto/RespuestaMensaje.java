package com.ferreteria.msventas.infrastructure.dto;

import java.time.LocalDateTime;

public record RespuestaMensaje(
        int status,
        String mensaje,
        LocalDateTime fecha) {
}
