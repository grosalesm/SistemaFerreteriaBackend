package com.ferreteria.msproductos.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.MovimientoStock;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.infrastructure.persistence.entity.AlmacenEntity;
import com.ferreteria.msproductos.infrastructure.persistence.entity.MovimientoStockEntity;
import com.ferreteria.msproductos.infrastructure.persistence.entity.StockEntity;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "productoActivo", ignore = true)
    Stock toBean(StockEntity entity);
    List<Stock> toBeans(List<StockEntity> entities);
    Almacen toAlmacen(AlmacenEntity entity);
    AlmacenEntity toAlmacenEntity(Almacen almacen);
    List<Almacen> toAlmacenes(List<AlmacenEntity> entities);
    MovimientoStock toMovimiento(MovimientoStockEntity entity);
    MovimientoStockEntity toMovimientoEntity(MovimientoStock movimiento);
    List<MovimientoStock> toMovimientos(List<MovimientoStockEntity> entities);
}
