package com.ferreteria.msclientes.infrastructure.dto;

import java.time.LocalDateTime;

import com.ferreteria.msclientes.domain.bean.TipoCliente;
import com.ferreteria.msclientes.domain.bean.TipoDocumento;

public record ClienteResponse(
        Long idCliente,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombreRazonSocial,
        TipoCliente tipoCliente,
        String telefono,
        String email,
        String direccion,
        Boolean activo,
        LocalDateTime fechaRegistro) {
}
