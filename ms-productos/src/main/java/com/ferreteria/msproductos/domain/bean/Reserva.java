package com.ferreteria.msproductos.domain.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reserva implements Serializable {

    private Long idReserva;
    private Long cotizacionId;
    private Long idAlmacen;
    private EstadoReserva estado;
    private LocalDateTime fechaCreacion;
    private List<ReservaDetalle> detalles = new ArrayList<>();
}
