package com.ferreteria.msauth.domain.constraint;

import com.ferreteria.msauth.domain.bean.Usuario;

public interface UsuarioConstraints {

    boolean validarDatos(Usuario usuario);

    boolean esRolInterno(Usuario usuario);

    boolean puedeCambiarEstado(Long idUsuario, Long idSolicitante);
}
