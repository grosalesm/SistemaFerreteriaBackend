package com.ferreteria.msclientes.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoDocumento;
import com.ferreteria.msclientes.infrastructure.dto.ClienteInternoRequest;
import com.ferreteria.msclientes.infrastructure.dto.ClienteRequest;
import com.ferreteria.msclientes.infrastructure.dto.ClienteResponse;
import com.ferreteria.msclientes.infrastructure.dto.ClienteResumenResponse;

@Mapper(componentModel = "spring")
public interface ClienteWebMapper {

    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "tipoCliente", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    Cliente toBean(ClienteRequest request);

    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "tipoCliente", ignore = true)
    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    Cliente toBean(ClienteInternoRequest request);

    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponses(List<Cliente> clientes);

    @Mapping(target = "id", source = "idCliente")
    ClienteResumenResponse toResumen(Cliente cliente);

    default TipoDocumento toTipoDocumento(String tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.isBlank()) {
            return null;
        }
        try {
            return TipoDocumento.valueOf(tipoDocumento.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de documento no válido: " + tipoDocumento);
        }
    }
}
