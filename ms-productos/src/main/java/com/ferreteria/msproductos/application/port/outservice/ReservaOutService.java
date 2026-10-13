package com.ferreteria.msproductos.application.port.outservice;

import java.util.Optional;

import com.ferreteria.msproductos.domain.bean.EstadoReserva;
import com.ferreteria.msproductos.domain.bean.Reserva;

public interface ReservaOutService {

    Optional<Reserva> obtenerPorCotizacionId(Long cotizacionId);
    Reserva insertar(Reserva reserva);
    boolean cambiarEstadoSiActiva(Long idReserva, EstadoReserva nuevoEstado);
}
