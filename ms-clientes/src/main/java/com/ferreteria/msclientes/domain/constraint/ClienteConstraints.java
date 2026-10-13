package com.ferreteria.msclientes.domain.constraint;

import com.ferreteria.msclientes.domain.bean.Cliente;

public interface ClienteConstraints {

    boolean validarDatos(Cliente cliente);
    boolean puedeSerContratista(Cliente cliente);
    boolean puedeAcceder(Long idCliente, String rol, Long clienteIdToken);
}
