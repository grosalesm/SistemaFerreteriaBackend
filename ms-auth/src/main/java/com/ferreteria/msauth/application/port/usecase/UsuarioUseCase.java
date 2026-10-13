package com.ferreteria.msauth.application.port.usecase;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msauth.domain.bean.Usuario;

public interface UsuarioUseCase {

    List<Usuario> listar();

    Optional<Usuario> obtenerPorId(Long idUsuario);

    Usuario guardar(Usuario usuario);

    Optional<Usuario> cambiarEstado(Long idUsuario, boolean activo, Long idSolicitante);
}
