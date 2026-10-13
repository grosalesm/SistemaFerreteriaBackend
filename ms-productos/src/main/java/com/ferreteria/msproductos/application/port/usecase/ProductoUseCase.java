package com.ferreteria.msproductos.application.port.usecase;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.infrastructure.dto.PrecioRequest;
import com.ferreteria.msproductos.infrastructure.dto.PrecioResponse;

public interface ProductoUseCase {

    List<Producto> listar(Long idCategoria, String texto, boolean soloConStock, boolean incluirInactivos, String rol);
    Optional<Producto> obtenerPorId(Long idProducto, String rol);
    Producto guardar(Producto producto);
    Optional<Producto> cambiarEstado(Long idProducto, boolean activo);
    List<PrecioResponse> calcularPrecios(PrecioRequest request);
}
