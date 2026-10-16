package com.ferreteria.msclientes.application.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msclientes.application.port.outservice.ClienteOutService;
import com.ferreteria.msclientes.application.port.usecase.ClienteUseCase;
import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoCliente;
import com.ferreteria.msclientes.domain.constraint.ClienteConstraints;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private final ClienteOutService clienteOutService;
    private final ClienteConstraints clienteConstraints;

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listar(TipoCliente tipoCliente, String texto) {
        String filtro = texto == null ? "" : texto.trim();
        return clienteOutService.listar(tipoCliente, filtro);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long idCliente) {
        return clienteOutService.obtenerPorId(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorNumeroDocumento(String numeroDocumento) {
        return clienteOutService.obtenerPorNumeroDocumento(numeroDocumento);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long idCliente, String rol, Long clienteIdToken) {
        if (!clienteConstraints.puedeAcceder(idCliente, rol, clienteIdToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene acceso a este cliente");
        }
        return clienteOutService.obtenerPorId(idCliente);
    }

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        if (!clienteConstraints.validarDatos(cliente)) {
            throw new IllegalArgumentException(
                    "Datos inválidos: el nombre es obligatorio y el documento debe tener 8 dígitos (DNI) u 11 dígitos (RUC)");
        }
        clienteOutService.obtenerPorNumeroDocumento(cliente.getNumeroDocumento())
                .filter(otro -> !Objects.equals(otro.getIdCliente(), cliente.getIdCliente()))
                .ifPresent(otro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El documento ya está registrado");
                });

        if (cliente.getIdCliente() == null) {
            return clienteOutService.insertar(cliente);
        }
        return actualizar(cliente);
    }

    @Override
    @Transactional
    public Optional<Cliente> actualizarContacto(Long idCliente, Cliente datos) {
        return clienteOutService.obtenerPorId(idCliente)
                .map(cliente -> {
                    cliente.setTelefono(datos.getTelefono());
                    cliente.setEmail(datos.getEmail());
                    cliente.setDireccion(datos.getDireccion());
                    return guardar(cliente);
                });
    }

    @Override
    @Transactional
    public Optional<Cliente> cambiarTipo(Long idCliente, TipoCliente tipoCliente) {
        return clienteOutService.obtenerPorId(idCliente)
                .flatMap(cliente -> {
                    if (tipoCliente == TipoCliente.CONTRATISTA && !clienteConstraints.puedeSerContratista(cliente)) {
                        throw new IllegalArgumentException("Solo un cliente con RUC puede ser CONTRATISTA");
                    }
                    cliente.setTipoCliente(tipoCliente);
                    return clienteOutService.actualizar(idCliente, cliente);
                });
    }

    @Override
    @Transactional
    public Optional<Cliente> cambiarEstado(Long idCliente, boolean activo) {
        return clienteOutService.obtenerPorId(idCliente)
                .flatMap(cliente -> {
                    cliente.setActivo(activo);
                    return clienteOutService.actualizar(idCliente, cliente);
                });
    }

    private Cliente actualizar(Cliente cliente) {
        Cliente actual = clienteOutService.obtenerPorId(cliente.getIdCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        // El tipo y el estado se cambian con sus propios endpoints
        cliente.setTipoCliente(actual.getTipoCliente());
        cliente.setActivo(actual.getActivo());
        if (cliente.getTipoCliente() == TipoCliente.CONTRATISTA && !clienteConstraints.puedeSerContratista(cliente)) {
            throw new IllegalArgumentException("Un cliente CONTRATISTA debe tener RUC");
        }

        return clienteOutService.actualizar(cliente.getIdCliente(), cliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }
}
