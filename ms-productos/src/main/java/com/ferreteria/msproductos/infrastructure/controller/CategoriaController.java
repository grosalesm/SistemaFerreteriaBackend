package com.ferreteria.msproductos.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msproductos.application.port.usecase.CategoriaUseCase;
import com.ferreteria.msproductos.domain.bean.Categoria;
import com.ferreteria.msproductos.infrastructure.dto.CategoriaRequest;
import com.ferreteria.msproductos.infrastructure.dto.CategoriaResponse;
import com.ferreteria.msproductos.infrastructure.mapper.CategoriaWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaUseCase categoriaUseCase;
    private final CategoriaWebMapper categoriaWebMapper;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaWebMapper.toResponses(categoriaUseCase.listar()));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> insertar(@Valid @RequestBody CategoriaRequest request) {
        Categoria creada = categoriaUseCase.guardar(categoriaWebMapper.toBean(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaWebMapper.toResponse(creada));
    }
}
