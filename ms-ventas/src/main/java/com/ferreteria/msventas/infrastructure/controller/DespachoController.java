package com.ferreteria.msventas.infrastructure.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msventas.application.port.usecase.PedidoUseCase;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.infrastructure.dto.ChecklistRequest;
import com.ferreteria.msventas.infrastructure.dto.DespachoRequest;
import com.ferreteria.msventas.infrastructure.dto.PedidoResponse;
import com.ferreteria.msventas.infrastructure.mapper.PedidoWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DespachoController {

    private final PedidoUseCase pedidoUseCase;
    private final PedidoWebMapper pedidoWebMapper;

    @GetMapping("/api/despachos/pendientes")
    public ResponseEntity<List<PedidoResponse>> pendientes() {
        return ResponseEntity.ok(pedidoWebMapper.toResponses(pedidoUseCase.listarPendientesDespacho()));
    }

    @PostMapping("/api/pedidos/{id}/preparar")
    public ResponseEntity<PedidoResponse> preparar(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long almaceneroId) {
        return responder(pedidoUseCase.preparar(id, almaceneroId));
    }

    @PutMapping("/api/pedidos/{id}/checklist")
    public ResponseEntity<PedidoResponse> checklist(
            @PathVariable Long id,
            @Valid @RequestBody ChecklistRequest request) {
        return responder(pedidoUseCase.marcarChecklist(id, request.itemsPreparados()));
    }

    @PostMapping("/api/pedidos/{id}/despachar")
    public ResponseEntity<PedidoResponse> despachar(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) DespachoRequest request) {
        return responder(pedidoUseCase.despachar(id, request == null ? null : request.repartidor()));
    }

    @PostMapping("/api/pedidos/{id}/entregar")
    public ResponseEntity<PedidoResponse> entregar(@PathVariable Long id) {
        return responder(pedidoUseCase.entregar(id));
    }

    private ResponseEntity<PedidoResponse> responder(Optional<Pedido> pedido) {
        return pedido
                .map(p -> ResponseEntity.ok(pedidoWebMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }
}
