package com.ferreteria.msproductos.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.MovimientoStock;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.infrastructure.dto.AlmacenResponse;
import com.ferreteria.msproductos.infrastructure.dto.MovimientoResponse;
import com.ferreteria.msproductos.infrastructure.dto.StockResponse;

@Mapper(componentModel = "spring")
public interface StockWebMapper {

    @Mapping(target = "disponible", expression = "java(stock.disponible())")
    StockResponse toResponse(Stock stock);
    List<StockResponse> toResponses(List<Stock> stocks);
    AlmacenResponse toAlmacenResponse(Almacen almacen);
    List<AlmacenResponse> toAlmacenResponses(List<Almacen> almacenes);
    MovimientoResponse toMovimientoResponse(MovimientoStock movimiento);
    List<MovimientoResponse> toMovimientoResponses(List<MovimientoStock> movimientos);
}
