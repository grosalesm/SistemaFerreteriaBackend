package com.ferreteria.msproductos.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.infrastructure.dto.ProductoRequest;
import com.ferreteria.msproductos.infrastructure.dto.ProductoResponse;

@Mapper(componentModel = "spring")
public interface ProductoWebMapper {

    @Mapping(target = "idProducto", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Producto toBean(ProductoRequest request);

    @Mapping(target = "disponible", expression = "java(totalDisponible(stocks))")
    @Mapping(target = "disponiblePorAlmacen", source = "stocks")
    ProductoResponse toResponse(Producto producto, List<Stock> stocks);

    @Mapping(target = "disponible", expression = "java(stock.disponible())")
    ProductoResponse.DisponibleAlmacen toDisponible(Stock stock);

    default Integer totalDisponible(List<Stock> stocks) {
        return stocks.stream().mapToInt(Stock::disponible).sum();
    }
}
