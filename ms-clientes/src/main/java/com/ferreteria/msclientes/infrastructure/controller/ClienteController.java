package com.ferreteria.msclientes.infrastructure.controller;

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

import com.ferreteria.msclientes.application.port.usecase.ClienteUseCase;
import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoCliente;
import com.ferreteria.msclientes.infrastructure.dto.ClienteRequest;
import com.ferreteria.msclientes.infrastructure.dto.ClienteResponse;
import com.ferreteria.msclientes.infrastructure.mapper.ClienteWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteUseCase clienteUseCase;
    private final ClienteWebMapper clienteWebMapper;

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar(@RequestParam(required = false) TipoCliente tipoCliente,
                                                        @RequestParam(required = false) String texto) {
        return ResponseEntity.ok(clienteWebMapper.toResponses(clienteUseCase.listar(tipoCliente, texto)));
    }

    @GetMapping("/me")
    public ResponseEntity<ClienteResponse> me(@RequestHeader("X-Cliente-Id") Long clienteId) {
        return clienteUseCase.obtenerPorId(clienteId)
                .map(cliente -> ResponseEntity.ok(clienteWebMapper.toResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<ClienteResponse> actualizarMe(@RequestHeader("X-Cliente-Id") Long clienteId,
                                                        @Valid @RequestBody ClienteRequest request) {
        Cliente datos = clienteWebMapper.toBean(request);
        return clienteUseCase.actualizarContacto(clienteId, datos)
                .map(cliente -> ResponseEntity.ok(clienteWebMapper.toResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String rol,
            @RequestHeader(value = "X-Cliente-Id", required = false) Long clienteIdToken) {
        return clienteUseCase.obtenerPorId(id, rol, clienteIdToken)
                .map(cliente -> ResponseEntity.ok(clienteWebMapper.toResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> insertar(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteWebMapper.toBean(request);
        Cliente creado = clienteUseCase.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteWebMapper.toResponse(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteWebMapper.toBean(request);
        cliente.setIdCliente(id);
        Cliente actualizado = clienteUseCase.guardar(cliente);
        return ResponseEntity.ok(clienteWebMapper.toResponse(actualizado));
    }

    @PatchMapping("/{id}/tipo")
    public ResponseEntity<ClienteResponse> cambiarTipo(@PathVariable Long id,
                                                       @RequestParam TipoCliente tipoCliente) {
        return clienteUseCase.cambiarTipo(id, tipoCliente)
                .map(cliente -> ResponseEntity.ok(clienteWebMapper.toResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ClienteResponse> cambiarEstado(@PathVariable Long id,
                                                         @RequestParam boolean activo) {
        return clienteUseCase.cambiarEstado(id, activo)
                .map(cliente -> ResponseEntity.ok(clienteWebMapper.toResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }
}
