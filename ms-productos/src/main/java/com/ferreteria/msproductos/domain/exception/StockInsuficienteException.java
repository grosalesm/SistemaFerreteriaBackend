package com.ferreteria.msproductos.domain.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class StockInsuficienteException extends RuntimeException {

    private final List<Faltante> faltantes;

    public StockInsuficienteException(List<Faltante> faltantes) {
        super("Stock insuficiente para " + faltantes.size() + " producto(s)");
        this.faltantes = faltantes;
    }

    public record Faltante(Long idProducto, String nombre, Integer solicitado, Integer disponible) {
    }
}
