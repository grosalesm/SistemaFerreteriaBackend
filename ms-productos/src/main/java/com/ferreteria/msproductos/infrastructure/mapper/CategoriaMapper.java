package com.ferreteria.msproductos.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ferreteria.msproductos.domain.bean.Categoria;
import com.ferreteria.msproductos.infrastructure.persistence.entity.CategoriaEntity;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toBean(CategoriaEntity entity);
    CategoriaEntity toEntity(Categoria categoria);
    List<Categoria> toBeans(List<CategoriaEntity> entities);
}
