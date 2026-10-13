package com.ferreteria.msproductos.infrastructure.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ferreteria.msproductos.domain.bean.EstadoReserva;

public record ReservaResponse(
        Long idReserva,
        Long cotizacionId,
        Long idAlmacen,
        EstadoReserva estado,
        LocalDateTime fechaCreacion,
        List<Detalle> detalles) {

    public record Detalle(Long idProducto, Integer cantidad) {
    }
}
