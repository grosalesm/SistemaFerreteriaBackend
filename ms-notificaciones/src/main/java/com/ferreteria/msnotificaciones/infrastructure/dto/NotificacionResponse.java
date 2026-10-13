package com.ferreteria.msnotificaciones.infrastructure.dto;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long idNotificacion,
        String tipo,
        String titulo,
        String mensaje,
        String rolDestino,
        Long clienteId,
        Long referenciaId,
        Boolean leida,
        LocalDateTime fecha) {
}
