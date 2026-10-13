package com.ferreteria.msproductos.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msproductos.domain.bean.Categoria;
import com.ferreteria.msproductos.infrastructure.dto.CategoriaRequest;
import com.ferreteria.msproductos.infrastructure.dto.CategoriaResponse;

@Mapper(componentModel = "spring")
public interface CategoriaWebMapper {

    @Mapping(target = "idCategoria", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Categoria toBean(CategoriaRequest request);
    CategoriaResponse toResponse(Categoria categoria);
    List<CategoriaResponse> toResponses(List<Categoria> categorias);
}
