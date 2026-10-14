package com.ferreteria.mscotizaciones.infrastructure.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.mscotizaciones.application.port.usecase.CotizacionUseCase;
import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;
import com.ferreteria.mscotizaciones.infrastructure.dto.CotizacionRequest;
import com.ferreteria.mscotizaciones.infrastructure.dto.CotizacionResponse;
import com.ferreteria.mscotizaciones.infrastructure.dto.ItemRequest;
import com.ferreteria.mscotizaciones.infrastructure.dto.RechazoRequest;
import com.ferreteria.mscotizaciones.infrastructure.mapper.CotizacionWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionUseCase cotizacionUseCase;
    private final CotizacionWebMapper cotizacionWebMapper;

    @GetMapping
    public ResponseEntity<List<CotizacionResponse>> listar(
            @RequestParam(required = false) EstadoCotizacion estado,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        List<Cotizacion> cotizaciones = cotizacionUseCase.listar(estado, clienteId, desde, hasta, rol, clienteIdToken);
        return ResponseEntity.ok(cotizacionWebMapper.toResponses(cotizaciones));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponse> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return responder(cotizacionUseCase.obtenerPorId(id, rol, clienteIdToken));
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> crear(
            @Valid @RequestBody CotizacionRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Nombre", required = false) String usuarioNombre,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        Cotizacion creada = cotizacionUseCase.crear(cotizacionWebMapper.toBean(request), usuarioId, decodificar(usuarioNombre), rol, clienteIdToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(cotizacionWebMapper.toResponse(creada));
    }

    @PostMapping("/simular")
    public ResponseEntity<CotizacionResponse> simular(
            @Valid @RequestBody CotizacionRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        Cotizacion simulada = cotizacionUseCase.simular(cotizacionWebMapper.toBean(request), rol, clienteIdToken);
        return ResponseEntity.ok(cotizacionWebMapper.toResponse(simulada));
    }

    @PutMapping("/{id}/items")
    public ResponseEntity<CotizacionResponse> editarItems(
            @PathVariable Long id,
            @RequestBody List<ItemRequest> items,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return responder(cotizacionUseCase.editarItems(id, cotizacionWebMapper.toItems(items), usuarioId, rol, clienteIdToken));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<CotizacionResponse> enviar(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Nombre", required = false) String usuarioNombre,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return responder(cotizacionUseCase.enviar(id, usuarioId, decodificar(usuarioNombre), rol, clienteIdToken));
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<CotizacionResponse> aprobar(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Nombre", required = false) String usuarioNombre,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return responder(cotizacionUseCase.aprobar(id, usuarioId, decodificar(usuarioNombre), rol, clienteIdToken));
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<CotizacionResponse> rechazar(
            @PathVariable Long id,
            @Valid @RequestBody RechazoRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Nombre", required = false) String usuarioNombre,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return responder(cotizacionUseCase.rechazar(id, request.motivo(), usuarioId, decodificar(usuarioNombre), rol, clienteIdToken));
    }

    @PostMapping("/{id}/reenviar")
    public ResponseEntity<CotizacionResponse> reenviar(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Nombre", required = false) String usuarioNombre,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return responderCreada(cotizacionUseCase.reenviar(id, usuarioId, decodificar(usuarioNombre), rol, clienteIdToken));
    }

    @PostMapping("/{id}/duplicar")
    public ResponseEntity<CotizacionResponse> duplicar(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-User-Nombre", required = false) String usuarioNombre,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return responderCreada(cotizacionUseCase.duplicar(id, usuarioId, decodificar(usuarioNombre), rol, clienteIdToken));
    }

    private String decodificar(String valor) {
        return valor == null ? null : URLDecoder.decode(valor, StandardCharsets.UTF_8);
    }

    private ResponseEntity<CotizacionResponse> responder(Optional<Cotizacion> cotizacion) {
        return cotizacion
                .map(c -> ResponseEntity.ok(cotizacionWebMapper.toResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<CotizacionResponse> responderCreada(Optional<Cotizacion> cotizacion) {
        return cotizacion
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(cotizacionWebMapper.toResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }
}
