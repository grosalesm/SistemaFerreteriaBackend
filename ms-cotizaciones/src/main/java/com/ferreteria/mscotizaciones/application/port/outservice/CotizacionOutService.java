package com.ferreteria.mscotizaciones.application.port.outservice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;

public interface CotizacionOutService {

    List<Cotizacion> listar(EstadoCotizacion estado, Long clienteId, LocalDateTime desde, LocalDateTime hasta);
    List<Cotizacion> listarEnviadasVencidas(LocalDateTime fecha);
    Optional<Cotizacion> obtenerPorId(Long idCotizacion);
    Cotizacion insertar(Cotizacion cotizacion);
    Optional<Cotizacion> actualizar(Long idCotizacion, Cotizacion cotizacion);
}
