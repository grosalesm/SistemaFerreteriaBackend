package com.ferreteria.msproductos.infrastructure.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ferreteria.msproductos.domain.exception.StockInsuficienteException.Faltante;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaMensaje(
        int status,
        String mensaje,
        LocalDateTime fecha,
        List<Faltante> faltantes) {

    public RespuestaMensaje(int status, String mensaje, LocalDateTime fecha) {
        this(status, mensaje, fecha, null);
    }
}
