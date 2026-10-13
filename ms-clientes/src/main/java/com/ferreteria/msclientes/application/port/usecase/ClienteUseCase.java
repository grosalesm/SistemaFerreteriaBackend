package com.ferreteria.msclientes.application.port.usecase;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoCliente;

public interface ClienteUseCase {

    List<Cliente> listar(TipoCliente tipoCliente, String texto);
    Optional<Cliente> obtenerPorId(Long idCliente);
    Optional<Cliente> obtenerPorId(Long idCliente, String rol, Long clienteIdToken);
    Cliente guardar(Cliente cliente);
    Optional<Cliente> actualizarContacto(Long idCliente, Cliente datos);
    Optional<Cliente> cambiarTipo(Long idCliente, TipoCliente tipoCliente);
    Optional<Cliente> cambiarEstado(Long idCliente, boolean activo);
}
