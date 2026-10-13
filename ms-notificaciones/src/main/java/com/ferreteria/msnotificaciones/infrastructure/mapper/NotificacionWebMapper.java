package com.ferreteria.msnotificaciones.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msnotificaciones.domain.bean.MetricaCliente;
import com.ferreteria.msnotificaciones.domain.bean.MetricaDiaria;
import com.ferreteria.msnotificaciones.domain.bean.MetricaProducto;
import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.infrastructure.dto.NotificacionResponse;
import com.ferreteria.msnotificaciones.infrastructure.dto.TopClienteResponse;
import com.ferreteria.msnotificaciones.infrastructure.dto.TopProductoResponse;
import com.ferreteria.msnotificaciones.infrastructure.dto.VentaDiaResponse;

@Mapper(componentModel = "spring")
public interface NotificacionWebMapper {

    NotificacionResponse toResponse(Notificacion notificacion);
    List<NotificacionResponse> toResponses(List<Notificacion> notificaciones);
    @Mapping(target = "monto", source = "montoVendido")
    @Mapping(target = "pedidos", source = "pedidosPagados")
    VentaDiaResponse toVentaDia(MetricaDiaria metrica);
    List<VentaDiaResponse> toVentasDia(List<MetricaDiaria> metricas);
    TopProductoResponse toTopProducto(MetricaProducto metrica);
    List<TopProductoResponse> toTopProductos(List<MetricaProducto> metricas);
    TopClienteResponse toTopCliente(MetricaCliente metrica);
    List<TopClienteResponse> toTopClientes(List<MetricaCliente> metricas);
}
