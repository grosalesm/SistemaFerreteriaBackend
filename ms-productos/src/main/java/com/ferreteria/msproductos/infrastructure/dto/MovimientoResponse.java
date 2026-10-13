package com.ferreteria.msproductos.infrastructure.dto;

import java.time.LocalDateTime;

import com.ferreteria.msproductos.domain.bean.TipoMovimiento;

public record MovimientoResponse(
        Long idMovimiento,
        Long idProducto,
        Long idAlmacen,
        TipoMovimiento tipo,
        Integer cantidad,
        Integer saldoFisico,
        String referencia,
        LocalDateTime fecha) {
}
