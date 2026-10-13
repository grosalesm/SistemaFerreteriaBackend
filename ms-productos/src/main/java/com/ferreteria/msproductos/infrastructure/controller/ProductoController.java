package com.ferreteria.msproductos.infrastructure.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.ferreteria.msproductos.application.port.usecase.ProductoUseCase;
import com.ferreteria.msproductos.application.port.usecase.StockUseCase;
import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.infrastructure.dto.ProductoRequest;
import com.ferreteria.msproductos.infrastructure.dto.ProductoResponse;
import com.ferreteria.msproductos.infrastructure.mapper.ProductoWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoUseCase productoUseCase;
    private final StockUseCase stockUseCase;
    private final ProductoWebMapper productoWebMapper;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(@RequestParam(required = false) Long idCategoria,
                                                         @RequestParam(required = false) String texto,
                                                         @RequestParam(defaultValue = "false") boolean soloConStock,
                                                         @RequestParam(defaultValue = "false") boolean incluirInactivos,
                                                         @RequestHeader(value = "X-User-Role", required = false) String rol) {
        List<Producto> productos = productoUseCase.listar(idCategoria, texto, soloConStock, incluirInactivos, rol);
        Map<Long, List<Stock>> stockPorProducto = stockUseCase.listar(null).stream()
                .collect(Collectors.groupingBy(Stock::getIdProducto));
        List<ProductoResponse> respuesta = productos.stream()
                .map(p -> productoWebMapper.toResponse(p, stockPorProducto.getOrDefault(p.getIdProducto(), List.of())))
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id,
                                                         @RequestHeader(value = "X-User-Role", required = false) String rol) {
        return productoUseCase.obtenerPorId(id, rol)
                .map(p -> ResponseEntity.ok(productoWebMapper.toResponse(p, stockUseCase.listarPorProducto(id))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> insertar(@Valid @RequestBody ProductoRequest request) {
        Producto creado = productoUseCase.guardar(productoWebMapper.toBean(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoWebMapper.toResponse(creado, stockUseCase.listarPorProducto(creado.getIdProducto())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductoRequest request) {
        Producto producto = productoWebMapper.toBean(request);
        producto.setIdProducto(id);
        Producto actualizado = productoUseCase.guardar(producto);
        return ResponseEntity.ok(productoWebMapper.toResponse(actualizado, stockUseCase.listarPorProducto(id)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProductoResponse> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return productoUseCase.cambiarEstado(id, activo)
                .map(p -> ResponseEntity.ok(productoWebMapper.toResponse(p, stockUseCase.listarPorProducto(id))))
                .orElse(ResponseEntity.notFound().build());
    }
}
