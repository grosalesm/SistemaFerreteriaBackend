package com.ferreteria.msproductos.application.port.usecase;

import com.ferreteria.msproductos.domain.bean.Reserva;

public interface ReservaUseCase {

    Reserva reservar(Reserva reserva);
    boolean liberar(Long cotizacionId);
    boolean consumir(Long cotizacionId);
}
