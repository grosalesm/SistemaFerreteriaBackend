package com.ferreteria.msclientes.infrastructure.dto;

import com.ferreteria.msclientes.domain.bean.TipoCliente;

public record ClienteResumenResponse(
        Long id,
        String nombreRazonSocial,
        TipoCliente tipoCliente,
        Boolean activo) {
}
