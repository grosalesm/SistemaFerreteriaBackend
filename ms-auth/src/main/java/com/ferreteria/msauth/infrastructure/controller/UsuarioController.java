package com.ferreteria.msauth.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msauth.application.port.usecase.UsuarioUseCase;
import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.infrastructure.dto.UsuarioRequest;
import com.ferreteria.msauth.infrastructure.dto.UsuarioResponse;
import com.ferreteria.msauth.infrastructure.mapper.UsuarioWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioWebMapper usuarioWebMapper;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioWebMapper.toResponses(usuarioUseCase.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return usuarioUseCase.obtenerPorId(id)
                .map(usuario -> ResponseEntity.ok(usuarioWebMapper.toResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> insertar(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioWebMapper.toBean(request);
        Usuario creado = usuarioUseCase.guardar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioWebMapper.toResponse(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioWebMapper.toBean(request);
        usuario.setIdUsuario(id);
        Usuario actualizado = usuarioUseCase.guardar(usuario);
        return ResponseEntity.ok(usuarioWebMapper.toResponse(actualizado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Long id,
                                                         @RequestParam boolean activo,
                                                         @RequestHeader("X-User-Id") Long idSolicitante) {
        return usuarioUseCase.cambiarEstado(id, activo, idSolicitante)
                .map(usuario -> ResponseEntity.ok(usuarioWebMapper.toResponse(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }
}
