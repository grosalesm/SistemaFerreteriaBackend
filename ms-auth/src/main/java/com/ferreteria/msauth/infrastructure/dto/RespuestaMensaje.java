package com.ferreteria.msauth.infrastructure.dto;

import java.time.LocalDateTime;

public record RespuestaMensaje(
        int status,
        String mensaje,
        LocalDateTime fecha) {
}
