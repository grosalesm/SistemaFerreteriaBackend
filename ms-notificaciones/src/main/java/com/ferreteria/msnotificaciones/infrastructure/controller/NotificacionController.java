package com.ferreteria.msnotificaciones.infrastructure.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msnotificaciones.application.port.usecase.NotificacionUseCase;
import com.ferreteria.msnotificaciones.infrastructure.dto.NotificacionResponse;
import com.ferreteria.msnotificaciones.infrastructure.mapper.NotificacionWebMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionUseCase notificacionUseCase;
    private final NotificacionWebMapper notificacionWebMapper;

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> listar(
            @RequestParam(defaultValue = "false") boolean soloNoLeidas,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return ResponseEntity.ok(notificacionWebMapper.toResponses(
                notificacionUseCase.listar(soloNoLeidas, rol, clienteIdToken)));
    }

    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificacionResponse> marcarLeida(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return notificacionUseCase.marcarLeida(id, rol, clienteIdToken)
                .map(n -> ResponseEntity.ok(notificacionWebMapper.toResponse(n)))
                .orElse(ResponseEntity.notFound().build());
    }
}
