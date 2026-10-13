package com.ferreteria.msproductos.application.port.usecase;

import java.util.List;

import com.ferreteria.msproductos.domain.bean.Categoria;

public interface CategoriaUseCase {

    List<Categoria> listar();
    Categoria guardar(Categoria categoria);
}
