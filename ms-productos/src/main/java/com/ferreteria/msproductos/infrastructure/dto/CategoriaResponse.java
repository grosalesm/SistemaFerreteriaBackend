package com.ferreteria.msproductos.infrastructure.dto;

public record CategoriaResponse(
        Long idCategoria,
        String nombre,
        Boolean activo) {
}
