package com.ferreteria.msauth.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msauth.application.port.usecase.AuthUseCase;
import com.ferreteria.msauth.application.port.usecase.UsuarioUseCase;
import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.infrastructure.dto.LoginRequest;
import com.ferreteria.msauth.infrastructure.dto.LoginResponse;
import com.ferreteria.msauth.infrastructure.dto.RegistroRequest;
import com.ferreteria.msauth.infrastructure.dto.UsuarioResponse;
import com.ferreteria.msauth.infrastructure.mapper.UsuarioWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioWebMapper usuarioWebMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registro(@Valid @RequestBody RegistroRequest request) {
        Usuario usuario = authUseCase.registro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioWebMapper.toResponse(usuario));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@RequestHeader("X-User-Id") Long idUsuario) {
        return usuarioUseCase.obtenerPorId(idUsuario)
                .map(usuario -> ResponseEntity.ok(usuarioWebMapper.toResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }
}
