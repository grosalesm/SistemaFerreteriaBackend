package com.ferreteria.msauth.application.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msauth.application.port.outservice.UsuarioOutService;
import com.ferreteria.msauth.application.port.usecase.UsuarioUseCase;
import com.ferreteria.msauth.domain.bean.Rol;
import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.domain.constraint.UsuarioConstraints;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioUseCase {

    private final UsuarioOutService usuarioOutService;
    private final UsuarioConstraints usuarioConstraints;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioOutService.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorId(Long idUsuario) {
        return usuarioOutService.obtenerPorId(idUsuario);
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (!usuarioConstraints.validarDatos(usuario)) {
            throw new IllegalArgumentException("Username, nombre completo, email válido y rol son obligatorios");
        }
        if (!usuarioConstraints.esRolInterno(usuario)) {
            throw new IllegalArgumentException("Solo se pueden registrar usuarios con rol interno");
        }
        validarDuplicados(usuario);

        if (usuario.getIdUsuario() == null) {
            return insertar(usuario);
        }
        return actualizar(usuario);
    }

    @Override
    @Transactional
    public Optional<Usuario> cambiarEstado(Long idUsuario, boolean activo, Long idSolicitante) {
        if (!usuarioConstraints.puedeCambiarEstado(idUsuario, idSolicitante)) {
            throw new IllegalArgumentException("No puede cambiar el estado de su propio usuario");
        }
        return usuarioOutService.obtenerPorId(idUsuario)
                .flatMap(usuario -> {
                    usuario.setActivo(activo);
                    return usuarioOutService.actualizar(idUsuario, usuario);
                });
    }

    private Usuario insertar(Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioOutService.insertar(usuario);
    }

    private Usuario actualizar(Usuario usuario) {
        Usuario actual = usuarioOutService.obtenerPorId(usuario.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (actual.getRol() == Rol.CLIENTE) {
            throw new IllegalArgumentException("Los usuarios CLIENTE no se editan desde aquí");
        }

        // Si la contraseña viene vacía se conserva la actual
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            usuario.setPassword(actual.getPassword());
        } else {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        usuario.setClienteId(actual.getClienteId());
        usuario.setActivo(actual.getActivo());

        return usuarioOutService.actualizar(usuario.getIdUsuario(), usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void validarDuplicados(Usuario usuario) {
        usuarioOutService.obtenerPorUsername(usuario.getUsername())
                .filter(otro -> !Objects.equals(otro.getIdUsuario(), usuario.getIdUsuario()))
                .ifPresent(otro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya está registrado");
                });
        usuarioOutService.obtenerPorEmail(usuario.getEmail())
                .filter(otro -> !Objects.equals(otro.getIdUsuario(), usuario.getIdUsuario()))
                .ifPresent(otro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
                });
    }
}
