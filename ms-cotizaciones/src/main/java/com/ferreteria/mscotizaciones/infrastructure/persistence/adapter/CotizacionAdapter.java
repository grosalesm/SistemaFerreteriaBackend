package com.ferreteria.mscotizaciones.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ferreteria.mscotizaciones.application.port.outservice.CotizacionOutService;
import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;
import com.ferreteria.mscotizaciones.infrastructure.mapper.CotizacionMapper;
import com.ferreteria.mscotizaciones.infrastructure.persistence.entity.CotizacionEntity;
import com.ferreteria.mscotizaciones.infrastructure.persistence.repository.CotizacionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CotizacionAdapter implements CotizacionOutService {

    private final CotizacionRepository cotizacionRepository;
    private final CotizacionMapper cotizacionMapper;

    @Override
    public List<Cotizacion> listar(EstadoCotizacion estado, Long clienteId, LocalDateTime desde, LocalDateTime hasta) {
        return cotizacionMapper.toBeans(cotizacionRepository.buscar(estado, clienteId, desde, hasta));
    }

    @Override
    public List<Cotizacion> listarEnviadasVencidas(LocalDateTime fecha) {
        return cotizacionMapper.toBeans(
                cotizacionRepository.findByEstadoAndFechaVencimientoBefore(EstadoCotizacion.ENVIADA, fecha));
    }

    @Override
    public Optional<Cotizacion> obtenerPorId(Long idCotizacion) {
        return cotizacionRepository.findById(idCotizacion).map(cotizacionMapper::toBean);
    }

    @Override
    public Cotizacion insertar(Cotizacion cotizacion) {
        CotizacionEntity entity = cotizacionMapper.toEntity(cotizacion);
        entity.setIdCotizacion(null);
        entity.setCodigo(null);
        entity.getItems().forEach(item -> item.setIdItem(null));
        entity.getHistorial().forEach(historial -> historial.setIdHistorial(null));
        CotizacionEntity guardada = cotizacionRepository.save(entity);
        guardada.setCodigo(String.format("COT-%d-%06d", guardada.getFechaCreacion().getYear(), guardada.getIdCotizacion()));
        return cotizacionMapper.toBean(cotizacionRepository.save(guardada));
    }

    @Override
    public Optional<Cotizacion> actualizar(Long idCotizacion, Cotizacion cotizacion) {
        return cotizacionRepository.findById(idCotizacion)
                .map(entity -> {
                    entity.setClienteNombre(cotizacion.getClienteNombre());
                    entity.setTipoCliente(cotizacion.getTipoCliente());
                    entity.setIdAlmacen(cotizacion.getIdAlmacen());
                    entity.setEstado(cotizacion.getEstado());
                    entity.setSubtotal(cotizacion.getSubtotal());
                    entity.setDescuentoTotal(cotizacion.getDescuentoTotal());
                    entity.setIgv(cotizacion.getIgv());
                    entity.setTotal(cotizacion.getTotal());
                    entity.setFechaVencimiento(cotizacion.getFechaVencimiento());
                    entity.setFechaAprobacion(cotizacion.getFechaAprobacion());
                    entity.setMotivoRechazo(cotizacion.getMotivoRechazo());
                    if (cotizacion.getItems().stream().anyMatch(item -> item.getIdItem() == null)) {
                        entity.getItems().clear();
                        entity.getItems().addAll(cotizacionMapper.toItemEntities(cotizacion.getItems()));
                    }
                    cotizacion.getHistorial().stream()
                            .filter(historial -> historial.getIdHistorial() == null)
                            .map(cotizacionMapper::toHistorialEntity)
                            .forEach(entity.getHistorial()::add);
                    return cotizacionMapper.toBean(cotizacionRepository.saveAndFlush(entity));
                });
    }
}
