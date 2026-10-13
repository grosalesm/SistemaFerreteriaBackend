package com.ferreteria.msnotificaciones.infrastructure.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResumenResponse(
        BigDecimal montoHoy,
        Integer pedidosHoy,
        BigDecimal montoUltimos7Dias,
        Integer pedidosUltimos7Dias,
        BigDecimal montoMes,
        Integer pedidosMes,
        Integer cotizacionesEnviadasMes,
        BigDecimal tasaConversionMes,
        List<NotificacionResponse> alertasStock) {
}
