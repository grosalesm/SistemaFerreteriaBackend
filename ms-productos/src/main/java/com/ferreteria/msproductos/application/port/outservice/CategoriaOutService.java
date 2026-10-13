package com.ferreteria.msproductos.application.port.outservice;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msproductos.domain.bean.Categoria;

public interface CategoriaOutService {

    List<Categoria> listar();
    Optional<Categoria> obtenerPorId(Long idCategoria);
    Optional<Categoria> obtenerPorNombre(String nombre);
    Categoria insertar(Categoria categoria);
}
