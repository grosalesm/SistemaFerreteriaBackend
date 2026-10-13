package com.ferreteria.msauth.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msauth.domain.bean.Rol;
import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.infrastructure.dto.UsuarioRequest;
import com.ferreteria.msauth.infrastructure.dto.UsuarioResponse;

@Mapper(componentModel = "spring")
public interface UsuarioWebMapper {

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Usuario toBean(UsuarioRequest request);

    UsuarioResponse toResponse(Usuario usuario);

    List<UsuarioResponse> toResponses(List<Usuario> usuarios);

    // MapStruct usa este método para convertir el rol (String) del request
    default Rol toRol(String rol) {
        try {
            return Rol.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }
    }
}
