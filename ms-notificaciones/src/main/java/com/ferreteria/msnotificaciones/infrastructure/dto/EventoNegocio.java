package com.ferreteria.msnotificaciones.infrastructure.dto;

import java.math.BigDecimal;
import java.util.List;

public record EventoNegocio(
        Long cotizacionId,
        Long pedidoId,
        String codigo,
        Long clienteId,
        String clienteNombre,
        BigDecimal total,
        String estado,
        List<ItemEvento> items,
        Long idProducto,
        String nombre,
        Long idAlmacen,
        Integer disponible,
        Integer stockMinimo) {
}
