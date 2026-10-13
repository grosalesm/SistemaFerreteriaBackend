package com.ferreteria.msproductos.application.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msproductos.application.port.outservice.CategoriaOutService;
import com.ferreteria.msproductos.application.port.usecase.CategoriaUseCase;
import com.ferreteria.msproductos.domain.bean.Categoria;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {

    private final CategoriaOutService categoriaOutService;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaOutService.listar();
    }

    @Override
    @Transactional
    public Categoria guardar(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        if (categoriaOutService.obtenerPorNombre(categoria.getNombre().trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La categoría ya existe");
        }
        categoria.setNombre(categoria.getNombre().trim());
        return categoriaOutService.insertar(categoria);
    }
}
