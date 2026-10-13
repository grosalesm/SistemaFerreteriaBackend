package com.ferreteria.msclientes.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msclientes.application.port.usecase.ClienteUseCase;
import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.infrastructure.dto.ClienteInternoRequest;
import com.ferreteria.msclientes.infrastructure.dto.ClienteResumenResponse;
import com.ferreteria.msclientes.infrastructure.mapper.ClienteWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/clientes")
@RequiredArgsConstructor
public class ClienteInternoController {

    private final ClienteUseCase clienteUseCase;
    private final ClienteWebMapper clienteWebMapper;

    @PostMapping
    public ResponseEntity<ClienteResumenResponse> crear(@Valid @RequestBody ClienteInternoRequest request) {
        Cliente cliente = clienteWebMapper.toBean(request);
        Cliente creado = clienteUseCase.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteWebMapper.toResumen(creado));
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<ClienteResumenResponse> resumen(@PathVariable Long id) {
        return clienteUseCase.obtenerPorId(id)
                .map(cliente -> ResponseEntity.ok(clienteWebMapper.toResumen(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }
}
