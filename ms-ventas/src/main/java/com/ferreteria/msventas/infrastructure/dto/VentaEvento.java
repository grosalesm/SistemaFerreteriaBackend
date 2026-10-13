package com.ferreteria.msventas.infrastructure.dto;

import java.math.BigDecimal;

public record VentaEvento(
        Long pedidoId,
        Long cotizacionId,
        String codigo,
        Long clienteId,
        String clienteNombre,
        BigDecimal total,
        String estado) {
}
