package com.ferreteria.msventas.infrastructure.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msventas.application.port.usecase.PedidoUseCase;
import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.infrastructure.dto.AnulacionRequest;
import com.ferreteria.msventas.infrastructure.dto.ComprobanteRequest;
import com.ferreteria.msventas.infrastructure.dto.ComprobanteResponse;
import com.ferreteria.msventas.infrastructure.dto.PagoRequest;
import com.ferreteria.msventas.infrastructure.dto.PedidoResponse;
import com.ferreteria.msventas.infrastructure.mapper.PedidoWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoUseCase pedidoUseCase;
    private final PedidoWebMapper pedidoWebMapper;

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar(
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return ResponseEntity.ok(pedidoWebMapper.toResponses(
                pedidoUseCase.listar(estado, clienteId, desde, hasta, rol, clienteIdToken)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> historialCliente(
            @PathVariable Long clienteId,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return ResponseEntity.ok(pedidoWebMapper.toResponses(pedidoUseCase.listarPorCliente(clienteId, rol, clienteIdToken)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return pedidoUseCase.obtenerPorId(id, rol, clienteIdToken)
                .map(p -> ResponseEntity.ok(pedidoWebMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/pago")
    public ResponseEntity<PedidoResponse> pagar(
            @PathVariable Long id,
            @Valid @RequestBody PagoRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long cajeroId) {
        return pedidoUseCase.registrarPago(id, pedidoWebMapper.toPago(request), request.tipoEntrega(),
                        request.direccionEntrega(), cajeroId)
                .map(p -> ResponseEntity.ok(pedidoWebMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comprobante")
    public ResponseEntity<ComprobanteResponse> emitirComprobante(
            @PathVariable Long id,
            @Valid @RequestBody ComprobanteRequest request) {
        return pedidoUseCase.emitirComprobante(id, pedidoWebMapper.toComprobante(request))
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(pedidoWebMapper.toComprobanteResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<ComprobanteResponse> obtenerComprobante(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return pedidoUseCase.obtenerComprobante(id, rol, clienteIdToken)
                .map(c -> ResponseEntity.ok(pedidoWebMapper.toComprobanteResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<PedidoResponse> anular(
            @PathVariable Long id,
            @Valid @RequestBody AnulacionRequest request) {
        return pedidoUseCase.anular(id, request.motivo())
                .map(p -> ResponseEntity.ok(pedidoWebMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }
}
