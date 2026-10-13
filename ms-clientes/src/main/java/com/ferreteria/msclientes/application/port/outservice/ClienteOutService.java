package com.ferreteria.msclientes.application.port.outservice;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoCliente;

public interface ClienteOutService {

    List<Cliente> listar(TipoCliente tipoCliente, String texto);
    Optional<Cliente> obtenerPorId(Long idCliente);
    Optional<Cliente> obtenerPorNumeroDocumento(String numeroDocumento);
    Cliente insertar(Cliente cliente);
    Optional<Cliente> actualizar(Long idCliente, Cliente cliente);
}
