package com.ferreteria.msproductos.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.infrastructure.persistence.entity.ProductoEntity;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    Producto toBean(ProductoEntity entity);
    ProductoEntity toEntity(Producto producto);
    List<Producto> toBeans(List<ProductoEntity> entities);
}
