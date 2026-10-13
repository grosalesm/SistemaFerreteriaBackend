package com.ferreteria.msproductos.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.application.port.outservice.ReservaOutService;
import com.ferreteria.msproductos.domain.bean.EstadoReserva;
import com.ferreteria.msproductos.domain.bean.Reserva;
import com.ferreteria.msproductos.infrastructure.mapper.ReservaMapper;
import com.ferreteria.msproductos.infrastructure.persistence.entity.ReservaEntity;
import com.ferreteria.msproductos.infrastructure.persistence.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservaAdapter implements ReservaOutService {

    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;

    @Override
    public Optional<Reserva> obtenerPorCotizacionId(Long cotizacionId) {
        return reservaRepository.findByCotizacionId(cotizacionId).map(reservaMapper::toBean);
    }

    @Override
    public Reserva insertar(Reserva reserva) {
        ReservaEntity entity = reservaMapper.toEntity(reserva);
        entity.setIdReserva(null);
        entity.setEstado(EstadoReserva.ACTIVA);
        entity.setFechaCreacion(LocalDateTime.now());
        entity.getDetalles().forEach(detalle -> detalle.setIdDetalle(null));
        return reservaMapper.toBean(reservaRepository.save(entity));
    }

    @Override
    public boolean cambiarEstadoSiActiva(Long idReserva, EstadoReserva nuevoEstado) {
        return reservaRepository.cambiarEstadoSiActiva(idReserva, nuevoEstado) == 1;
    }
}
