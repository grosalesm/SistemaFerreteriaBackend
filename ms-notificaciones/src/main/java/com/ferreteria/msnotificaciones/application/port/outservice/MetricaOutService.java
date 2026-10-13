package com.ferreteria.msnotificaciones.application.port.outservice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ferreteria.msnotificaciones.domain.bean.MetricaCliente;
import com.ferreteria.msnotificaciones.domain.bean.MetricaDiaria;
import com.ferreteria.msnotificaciones.domain.bean.MetricaProducto;

public interface MetricaOutService {

    Optional<MetricaDiaria> obtenerDia(LocalDate fecha);
    List<MetricaDiaria> listarDias(LocalDate desde, LocalDate hasta);
    MetricaDiaria guardarDia(MetricaDiaria metrica);
    Optional<MetricaProducto> obtenerProducto(Long idProducto);
    MetricaProducto guardarProducto(MetricaProducto metrica);
    List<MetricaProducto> topProductos();
    Optional<MetricaCliente> obtenerCliente(Long clienteId);
    MetricaCliente guardarCliente(MetricaCliente metrica);
    List<MetricaCliente> topClientes();
}
