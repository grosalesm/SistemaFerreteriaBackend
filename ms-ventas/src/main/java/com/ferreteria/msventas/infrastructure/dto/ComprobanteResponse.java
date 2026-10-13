package com.ferreteria.msventas.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ferreteria.msventas.domain.bean.TipoComprobante;

public record ComprobanteResponse(
        Long idComprobante,
        Long idPedido,
        TipoComprobante tipo,
        String serie,
        Integer numero,
        String numeroComprobante,
        String documentoCliente,
        String razonSocial,
        BigDecimal subtotal,
        BigDecimal igv,
        BigDecimal total,
        LocalDateTime fechaEmision) {
}
