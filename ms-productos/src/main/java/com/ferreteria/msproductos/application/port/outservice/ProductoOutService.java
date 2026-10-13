package com.ferreteria.msproductos.application.port.outservice;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msproductos.domain.bean.Producto;

public interface ProductoOutService {

    List<Producto> listar(Long idCategoria, String texto);
    Optional<Producto> obtenerPorId(Long idProducto);
    Optional<Producto> obtenerPorSku(String sku);
    Producto insertar(Producto producto);
    Optional<Producto> actualizar(Long idProducto, Producto producto);
}
