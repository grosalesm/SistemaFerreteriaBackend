package com.ferreteria.msnotificaciones.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ferreteria.msnotificaciones.domain.bean.MetricaCliente;
import com.ferreteria.msnotificaciones.domain.bean.MetricaDiaria;
import com.ferreteria.msnotificaciones.domain.bean.MetricaProducto;
import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.MetricaClienteEntity;
import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.MetricaDiariaEntity;
import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.MetricaProductoEntity;
import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.NotificacionEntity;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    Notificacion toBean(NotificacionEntity entity);
    List<Notificacion> toBeans(List<NotificacionEntity> entities);
    NotificacionEntity toEntity(Notificacion notificacion);
    MetricaDiaria toDiaria(MetricaDiariaEntity entity);
    List<MetricaDiaria> toDiarias(List<MetricaDiariaEntity> entities);
    MetricaDiariaEntity toDiariaEntity(MetricaDiaria metrica);
    MetricaProducto toProducto(MetricaProductoEntity entity);
    List<MetricaProducto> toProductos(List<MetricaProductoEntity> entities);
    MetricaProductoEntity toProductoEntity(MetricaProducto metrica);
    MetricaCliente toCliente(MetricaClienteEntity entity);
    List<MetricaCliente> toClientes(List<MetricaClienteEntity> entities);
    MetricaClienteEntity toClienteEntity(MetricaCliente metrica);
}
