package com.ferreteria.mscotizaciones.infrastructure.dto;

import java.time.LocalDateTime;

import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;

public record HistorialResponse(
        Long idHistorial,
        EstadoCotizacion estadoAnterior,
        EstadoCotizacion estadoNuevo,
        Long usuarioId,
        String usuarioNombre,
        LocalDateTime fecha,
        String comentario) {
}
