package com.ferreteria.mscotizaciones.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.HistorialEstado;
import com.ferreteria.mscotizaciones.domain.bean.ItemCotizacion;
import com.ferreteria.mscotizaciones.infrastructure.persistence.entity.CotizacionEntity;
import com.ferreteria.mscotizaciones.infrastructure.persistence.entity.HistorialEstadoEntity;
import com.ferreteria.mscotizaciones.infrastructure.persistence.entity.ItemCotizacionEntity;

@Mapper(componentModel = "spring")
public interface CotizacionMapper {

    Cotizacion toBean(CotizacionEntity entity);
    List<Cotizacion> toBeans(List<CotizacionEntity> entities);
    CotizacionEntity toEntity(Cotizacion cotizacion);
    @Mapping(target = "aviso", ignore = true)
    ItemCotizacion toItem(ItemCotizacionEntity entity);
    ItemCotizacionEntity toItemEntity(ItemCotizacion item);
    List<ItemCotizacionEntity> toItemEntities(List<ItemCotizacion> items);
    HistorialEstado toHistorial(HistorialEstadoEntity entity);
    HistorialEstadoEntity toHistorialEntity(HistorialEstado historial);
}
