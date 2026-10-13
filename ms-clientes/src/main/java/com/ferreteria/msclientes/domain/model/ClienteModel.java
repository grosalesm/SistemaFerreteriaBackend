package com.ferreteria.msclientes.domain.model;

import org.springframework.stereotype.Component;

import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoDocumento;
import com.ferreteria.msclientes.domain.constraint.ClienteConstraints;

@Component
public class ClienteModel implements ClienteConstraints {

    @Override
    public boolean validarDatos(Cliente cliente) {
        if (cliente.getNombreRazonSocial() == null || cliente.getNombreRazonSocial().isBlank()) {
            return false;
        }
        String numero = cliente.getNumeroDocumento();
        if (numero == null) {
            return false;
        }
        if (cliente.getTipoDocumento() == TipoDocumento.DNI) {
            return numero.matches("\\d{8}");
        }
        if (cliente.getTipoDocumento() == TipoDocumento.RUC) {
            return numero.matches("\\d{11}");
        }
        return false; // sin tipo de documento
    }

    @Override
    public boolean puedeSerContratista(Cliente cliente) {
        return cliente.getTipoDocumento() == TipoDocumento.RUC;
    }

    @Override
    public boolean puedeAcceder(Long idCliente, String rol, Long clienteIdToken) {
        if (rol == null) {
            return false;
        }
        if (!"CLIENTE".equals(rol)) {
            return true; // ADMIN, VENDEDOR, ALMACENERO, CAJERO
        }
        return idCliente.equals(clienteIdToken);
    }
}
