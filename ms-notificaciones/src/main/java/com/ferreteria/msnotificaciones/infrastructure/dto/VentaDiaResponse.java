package com.ferreteria.msnotificaciones.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaDiaResponse(
        LocalDate fecha,
        BigDecimal monto,
        Integer pedidos) {
}
