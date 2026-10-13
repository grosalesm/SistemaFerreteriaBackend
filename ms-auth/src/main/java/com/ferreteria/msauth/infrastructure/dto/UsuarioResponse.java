package com.ferreteria.msauth.infrastructure.dto;

import java.time.LocalDateTime;

import com.ferreteria.msauth.domain.bean.Rol;

public record UsuarioResponse(
        Long idUsuario,
        String username,
        String nombreCompleto,
        String email,
        Rol rol,
        Long clienteId,
        Boolean activo,
        LocalDateTime fechaCreacion) {
}
