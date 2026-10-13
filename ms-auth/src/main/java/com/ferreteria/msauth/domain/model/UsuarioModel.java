package com.ferreteria.msauth.domain.model;

import org.springframework.stereotype.Component;

import com.ferreteria.msauth.domain.bean.Rol;
import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.domain.constraint.UsuarioConstraints;

@Component
public class UsuarioModel implements UsuarioConstraints {

    @Override
    public boolean validarDatos(Usuario usuario) {
        return noVacio(usuario.getUsername())
                && noVacio(usuario.getNombreCompleto())
                && noVacio(usuario.getEmail())
                && usuario.getEmail().contains("@")
                && usuario.getRol() != null;
    }

    @Override
    public boolean esRolInterno(Usuario usuario) {
        return usuario.getRol() != null && usuario.getRol() != Rol.CLIENTE;
    }

    @Override
    public boolean puedeCambiarEstado(Long idUsuario, Long idSolicitante) {
        // Un admin no puede desactivarse a sí mismo
        return !idUsuario.equals(idSolicitante);
    }

    private boolean noVacio(String valor) {
        return valor != null && !valor.isBlank();
    }
}
