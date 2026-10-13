package com.ferreteria.mscotizaciones.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ferreteria.mscotizaciones.domain.bean.Canal;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;

public record CotizacionResponse(
        Long idCotizacion,
        String codigo,
        Long clienteId,
        String clienteNombre,
        String tipoCliente,
        Long vendedorId,
        Canal canal,
        Long idAlmacen,
        EstadoCotizacion estado,
        BigDecimal subtotal,
        BigDecimal descuentoTotal,
        BigDecimal igv,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaVencimiento,
        LocalDateTime fechaAprobacion,
        String motivoRechazo,
        Long cotizacionOrigenId,
        List<ItemResponse> items,
        List<HistorialResponse> historial) {
}
