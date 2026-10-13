package com.ferreteria.msauth.infrastructure.dto;

import com.ferreteria.msauth.domain.bean.Rol;

public record LoginResponse(
        String token,
        Long idUsuario,
        String nombreCompleto,
        Rol rol) {
}
