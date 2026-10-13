package com.ferreteria.msproductos.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msproductos.application.port.outservice.CategoriaOutService;
import com.ferreteria.msproductos.application.port.outservice.ProductoOutService;
import com.ferreteria.msproductos.application.port.outservice.StockOutService;
import com.ferreteria.msproductos.application.port.usecase.ProductoUseCase;
import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.domain.bean.TipoCliente;
import com.ferreteria.msproductos.domain.constraint.ProductoConstraints;
import com.ferreteria.msproductos.infrastructure.dto.PrecioRequest;
import com.ferreteria.msproductos.infrastructure.dto.PrecioResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService implements ProductoUseCase {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final ProductoOutService productoOutService;
    private final CategoriaOutService categoriaOutService;
    private final StockOutService stockOutService;
    private final ProductoConstraints productoConstraints;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listar(Long idCategoria, String texto, boolean soloConStock,
                                 boolean incluirInactivos, String rol) {
        String filtro = texto == null ? "" : texto.trim();
        boolean verInactivos = incluirInactivos && productoConstraints.puedeListarInactivos(rol);
        List<Producto> productos = productoOutService.listar(idCategoria, filtro).stream()
                .filter(p -> verInactivos || Boolean.TRUE.equals(p.getActivo()))
                .toList();
        if (!soloConStock) {
            return productos;
        }
        Map<Long, Integer> disponiblePorProducto = stockOutService.listar(null).stream()
                .collect(Collectors.groupingBy(Stock::getIdProducto, Collectors.summingInt(Stock::disponible)));
        return productos.stream()
                .filter(p -> disponiblePorProducto.getOrDefault(p.getIdProducto(), 0) > 0)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> obtenerPorId(Long idProducto, String rol) {
        return productoOutService.obtenerPorId(idProducto)
                .filter(p -> Boolean.TRUE.equals(p.getActivo()) || productoConstraints.puedeVerInactivo(rol));
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        if (!productoConstraints.validarDatos(producto)) {
            throw new IllegalArgumentException("Datos inválidos: sku y nombre obligatorios, precios mayores a 0, "
                    + "precio contratista no mayor al minorista, stock mínimo >= 0 y descuento entre 0 y 50");
        }
        if (producto.getIdCategoria() == null || categoriaOutService.obtenerPorId(producto.getIdCategoria()).isEmpty()) {
            throw new IllegalArgumentException("La categoría no existe");
        }
        productoOutService.obtenerPorSku(producto.getSku())
                .filter(otro -> !Objects.equals(otro.getIdProducto(), producto.getIdProducto()))
                .ifPresent(otro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya está registrado");
                });

        if (producto.getIdProducto() == null) {
            Producto nuevo = productoOutService.insertar(producto);
            for (Almacen almacen : stockOutService.listarAlmacenes()) {
                stockOutService.crear(nuevo.getIdProducto(), almacen.getIdAlmacen());
            }
            return nuevo;
        }
        Producto actual = productoOutService.obtenerPorId(producto.getIdProducto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        producto.setActivo(actual.getActivo());
        return productoOutService.actualizar(producto.getIdProducto(), producto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Override
    @Transactional
    public Optional<Producto> cambiarEstado(Long idProducto, boolean activo) {
        return productoOutService.obtenerPorId(idProducto)
                .flatMap(producto -> {
                    producto.setActivo(activo);
                    return productoOutService.actualizar(idProducto, producto);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioResponse> calcularPrecios(PrecioRequest request) {
        return request.items().stream()
                .map(item -> calcular(item.idProducto(), item.cantidad(), request.tipoCliente(), request.idAlmacen()))
                .toList();
    }

    private PrecioResponse calcular(Long idProducto, Integer cantidad, TipoCliente tipoCliente, Long idAlmacen) {
        Producto producto = productoOutService.obtenerPorId(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto " + idProducto + " no encontrado"));
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalArgumentException("El producto " + producto.getNombre() + " está inactivo");
        }

        BigDecimal precio = productoConstraints.precioUnitario(producto, tipoCliente);
        BigDecimal porcentaje = productoConstraints.porcentajeAplicable(producto, cantidad);
        BigDecimal factor = BigDecimal.ONE.subtract(porcentaje.divide(CIEN));
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad))
                .multiply(factor)
                .setScale(2, RoundingMode.HALF_UP);
        int disponible = stockOutService.obtener(idProducto, idAlmacen).map(Stock::disponible).orElse(0);

        return new PrecioResponse(producto.getIdProducto(), producto.getSku(), producto.getNombre(),
                precio, porcentaje, subtotal, disponible);
    }
}
