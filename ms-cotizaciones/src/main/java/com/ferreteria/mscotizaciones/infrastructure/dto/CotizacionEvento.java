package com.ferreteria.mscotizaciones.infrastructure.dto;

import java.math.BigDecimal;

public record CotizacionEvento(
        Long cotizacionId,
        String codigo,
        Long clienteId,
        String clienteNombre,
        BigDecimal total,
        String estado) {
}
