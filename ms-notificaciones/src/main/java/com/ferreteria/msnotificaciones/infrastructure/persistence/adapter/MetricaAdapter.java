package com.ferreteria.msnotificaciones.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ferreteria.msnotificaciones.application.port.outservice.MetricaOutService;
import com.ferreteria.msnotificaciones.domain.bean.MetricaCliente;
import com.ferreteria.msnotificaciones.domain.bean.MetricaDiaria;
import com.ferreteria.msnotificaciones.domain.bean.MetricaProducto;
import com.ferreteria.msnotificaciones.infrastructure.mapper.NotificacionMapper;
import com.ferreteria.msnotificaciones.infrastructure.persistence.repository.MetricaClienteRepository;
import com.ferreteria.msnotificaciones.infrastructure.persistence.repository.MetricaDiariaRepository;
import com.ferreteria.msnotificaciones.infrastructure.persistence.repository.MetricaProductoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MetricaAdapter implements MetricaOutService {

    private final MetricaDiariaRepository metricaDiariaRepository;
    private final MetricaProductoRepository metricaProductoRepository;
    private final MetricaClienteRepository metricaClienteRepository;
    private final NotificacionMapper notificacionMapper;

    @Override
    public Optional<MetricaDiaria> obtenerDia(LocalDate fecha) {
        return metricaDiariaRepository.findByFecha(fecha).map(notificacionMapper::toDiaria);
    }

    @Override
    public List<MetricaDiaria> listarDias(LocalDate desde, LocalDate hasta) {
        return notificacionMapper.toDiarias(metricaDiariaRepository.findByFechaBetweenOrderByFecha(desde, hasta));
    }

    @Override
    public MetricaDiaria guardarDia(MetricaDiaria metrica) {
        return notificacionMapper.toDiaria(metricaDiariaRepository.save(notificacionMapper.toDiariaEntity(metrica)));
    }

    @Override
    public Optional<MetricaProducto> obtenerProducto(Long idProducto) {
        return metricaProductoRepository.findById(idProducto).map(notificacionMapper::toProducto);
    }

    @Override
    public MetricaProducto guardarProducto(MetricaProducto metrica) {
        return notificacionMapper.toProducto(metricaProductoRepository.save(notificacionMapper.toProductoEntity(metrica)));
    }

    @Override
    public List<MetricaProducto> topProductos() {
        return notificacionMapper.toProductos(metricaProductoRepository.findTop5ByOrderByUnidadesAprobadasDesc());
    }

    @Override
    public Optional<MetricaCliente> obtenerCliente(Long clienteId) {
        return metricaClienteRepository.findById(clienteId).map(notificacionMapper::toCliente);
    }

    @Override
    public MetricaCliente guardarCliente(MetricaCliente metrica) {
        return notificacionMapper.toCliente(metricaClienteRepository.save(notificacionMapper.toClienteEntity(metrica)));
    }

    @Override
    public List<MetricaCliente> topClientes() {
        return notificacionMapper.toClientes(metricaClienteRepository.findTop5ByOrderByMontoCompradoDesc());
    }
}
