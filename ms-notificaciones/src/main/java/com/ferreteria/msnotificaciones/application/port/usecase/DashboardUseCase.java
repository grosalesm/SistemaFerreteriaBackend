package com.ferreteria.msnotificaciones.application.port.usecase;

import java.time.LocalDate;
import java.util.List;

import com.ferreteria.msnotificaciones.domain.bean.MetricaCliente;
import com.ferreteria.msnotificaciones.domain.bean.MetricaDiaria;
import com.ferreteria.msnotificaciones.domain.bean.MetricaProducto;
import com.ferreteria.msnotificaciones.infrastructure.dto.ResumenResponse;

public interface DashboardUseCase {

    ResumenResponse resumen();
    List<MetricaDiaria> ventasPorDia(LocalDate desde, LocalDate hasta);
    List<MetricaProducto> topProductos();
    List<MetricaCliente> topClientes();
}
