package com.ferreteria.msnotificaciones.infrastructure.dto;

import java.math.BigDecimal;

public record TopClienteResponse(
        Long clienteId,
        String nombre,
        Integer compras,
        BigDecimal montoComprado) {
}
