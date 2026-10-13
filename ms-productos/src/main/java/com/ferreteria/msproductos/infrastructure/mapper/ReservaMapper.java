package com.ferreteria.msproductos.infrastructure.mapper;

import org.mapstruct.Mapper;

import com.ferreteria.msproductos.domain.bean.Reserva;
import com.ferreteria.msproductos.domain.bean.ReservaDetalle;
import com.ferreteria.msproductos.infrastructure.persistence.entity.ReservaDetalleEntity;
import com.ferreteria.msproductos.infrastructure.persistence.entity.ReservaEntity;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    Reserva toBean(ReservaEntity entity);
    ReservaEntity toEntity(Reserva reserva);
    ReservaDetalle toDetalle(ReservaDetalleEntity entity);
    ReservaDetalleEntity toDetalleEntity(ReservaDetalle detalle);
}
