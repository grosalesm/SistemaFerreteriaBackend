package com.ferreteria.msauth.application.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msauth.application.port.outservice.UsuarioOutService;
import com.ferreteria.msauth.application.port.usecase.AuthUseCase;
import com.ferreteria.msauth.domain.bean.Rol;
import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.domain.constraint.UsuarioConstraints;
import com.ferreteria.msauth.infrastructure.client.ClienteClient;
import com.ferreteria.msauth.infrastructure.client.ClienteClient.ClienteRequest;
import com.ferreteria.msauth.infrastructure.client.ClienteClient.ClienteResponse;
import com.ferreteria.msauth.infrastructure.config.JwtUtil;
import com.ferreteria.msauth.infrastructure.dto.LoginRequest;
import com.ferreteria.msauth.infrastructure.dto.LoginResponse;
import com.ferreteria.msauth.infrastructure.dto.RegistroRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UsuarioOutService usuarioOutService;
    private final UsuarioConstraints usuarioConstraints;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ClienteClient clienteClient;

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioOutService.obtenerPorUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuario o contraseña incorrectos"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario está inactivo");
        }

        String token = jwtUtil.generarToken(usuario);
        return new LoginResponse(token, usuario.getIdUsuario(), usuario.getNombreCompleto(), usuario.getRol());
    }

    @Override
    public Usuario registro(RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setNombreCompleto(request.nombreCompleto());
        usuario.setEmail(request.email());
        usuario.setRol(Rol.CLIENTE);

        if (!usuarioConstraints.validarDatos(usuario)) {
            throw new IllegalArgumentException("Username, nombre completo y email válido son obligatorios");
        }
        // Se valida antes de llamar a ms-clientes para no dejar clientes creados sin usuario
        if (usuarioOutService.obtenerPorUsername(usuario.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya está registrado");
        }
        if (usuarioOutService.obtenerPorEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        // Si ms-clientes no responde, el fallback lanza 503 y el usuario no se crea
        ClienteResponse cliente = clienteClient.crearCliente(new ClienteRequest(
                request.tipoDocumento(),
                request.numeroDocumento(),
                request.nombreCompleto(),
                request.telefono(),
                request.email()));

        usuario.setClienteId(cliente.id());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        return usuarioOutService.insertar(usuario);
    }
}
