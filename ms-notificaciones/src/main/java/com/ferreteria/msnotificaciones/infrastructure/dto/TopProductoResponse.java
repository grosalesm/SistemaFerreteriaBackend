package com.ferreteria.msnotificaciones.infrastructure.dto;

import java.math.BigDecimal;

public record TopProductoResponse(
        Long idProducto,
        String nombre,
        Integer unidadesAprobadas,
        BigDecimal montoAprobado) {
}
