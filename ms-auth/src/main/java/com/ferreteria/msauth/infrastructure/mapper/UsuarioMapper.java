package com.ferreteria.msauth.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.infrastructure.persistence.entity.UsuarioEntity;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toBean(UsuarioEntity entity);

    UsuarioEntity toEntity(Usuario usuario);

    List<Usuario> toBeans(List<UsuarioEntity> entities);
}
