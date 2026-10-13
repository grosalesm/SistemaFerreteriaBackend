package com.ferreteria.msauth.application.port.outservice;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msauth.domain.bean.Usuario;

public interface UsuarioOutService {

    List<Usuario> listar();

    Optional<Usuario> obtenerPorId(Long idUsuario);

    Optional<Usuario> obtenerPorUsername(String username);

    Optional<Usuario> obtenerPorEmail(String email);

    Usuario insertar(Usuario usuario);

    Optional<Usuario> actualizar(Long idUsuario, Usuario usuario);
}
