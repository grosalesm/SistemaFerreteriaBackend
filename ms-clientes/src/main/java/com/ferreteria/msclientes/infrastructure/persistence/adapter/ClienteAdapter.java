package com.ferreteria.msclientes.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ferreteria.msclientes.application.port.outservice.ClienteOutService;
import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoCliente;
import com.ferreteria.msclientes.infrastructure.mapper.ClienteMapper;
import com.ferreteria.msclientes.infrastructure.persistence.entity.ClienteEntity;
import com.ferreteria.msclientes.infrastructure.persistence.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClienteAdapter implements ClienteOutService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public List<Cliente> listar(TipoCliente tipoCliente, String texto) {
        return clienteMapper.toBeans(clienteRepository.buscar(tipoCliente, texto));
    }

    @Override
    public Optional<Cliente> obtenerPorId(Long idCliente) {
        return clienteRepository.findById(idCliente).map(clienteMapper::toBean);
    }

    @Override
    public Optional<Cliente> obtenerPorNumeroDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumento(numeroDocumento).map(clienteMapper::toBean);
    }

    @Override
    public Cliente insertar(Cliente cliente) {
        ClienteEntity entity = clienteMapper.toEntity(cliente);
        entity.setIdCliente(null);
        entity.setTipoCliente(TipoCliente.MINORISTA);
        entity.setActivo(true);
        entity.setFechaRegistro(LocalDateTime.now());
        return clienteMapper.toBean(clienteRepository.save(entity));
    }

    @Override
    public Optional<Cliente> actualizar(Long idCliente, Cliente cliente) {
        return clienteRepository.findById(idCliente)
                .map(entity -> {
                    entity.setTipoDocumento(cliente.getTipoDocumento());
                    entity.setNumeroDocumento(cliente.getNumeroDocumento());
                    entity.setNombreRazonSocial(cliente.getNombreRazonSocial());
                    entity.setTipoCliente(cliente.getTipoCliente());
                    entity.setTelefono(cliente.getTelefono());
                    entity.setEmail(cliente.getEmail());
                    entity.setDireccion(cliente.getDireccion());
                    entity.setActivo(cliente.getActivo());
                    return clienteMapper.toBean(clienteRepository.save(entity));
                });
    }
}
