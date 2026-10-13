package com.ferreteria.msclientes.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.infrastructure.persistence.entity.ClienteEntity;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    Cliente toBean(ClienteEntity entity);
    ClienteEntity toEntity(Cliente cliente);
    List<Cliente> toBeans(List<ClienteEntity> entities);
}
