package com.ferreteria.msproductos.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.application.port.outservice.ProductoOutService;
import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.infrastructure.mapper.ProductoMapper;
import com.ferreteria.msproductos.infrastructure.persistence.entity.ProductoEntity;
import com.ferreteria.msproductos.infrastructure.persistence.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductoAdapter implements ProductoOutService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<Producto> listar(Long idCategoria, String texto) {
        return productoMapper.toBeans(productoRepository.buscar(idCategoria, texto));
    }

    @Override
    public Optional<Producto> obtenerPorId(Long idProducto) {
        return productoRepository.findById(idProducto).map(productoMapper::toBean);
    }

    @Override
    public Optional<Producto> obtenerPorSku(String sku) {
        return productoRepository.findBySku(sku).map(productoMapper::toBean);
    }

    @Override
    public Producto insertar(Producto producto) {
        ProductoEntity entity = productoMapper.toEntity(producto);
        entity.setIdProducto(null);
        entity.setActivo(true);
        entity.setFechaCreacion(LocalDateTime.now());
        return productoMapper.toBean(productoRepository.save(entity));
    }

    @Override
    public Optional<Producto> actualizar(Long idProducto, Producto producto) {
        return productoRepository.findById(idProducto)
                .map(entity -> {
                    entity.setSku(producto.getSku());
                    entity.setNombre(producto.getNombre());
                    entity.setDescripcion(producto.getDescripcion());
                    entity.setIdCategoria(producto.getIdCategoria());
                    entity.setUnidadMedida(producto.getUnidadMedida());
                    entity.setPrecioMinorista(producto.getPrecioMinorista());
                    entity.setPrecioContratista(producto.getPrecioContratista());
                    entity.setCantidadMinimaDescuento(producto.getCantidadMinimaDescuento());
                    entity.setPorcentajeDescuento(producto.getPorcentajeDescuento());
                    entity.setStockMinimo(producto.getStockMinimo());
                    entity.setActivo(producto.getActivo());
                    return productoMapper.toBean(productoRepository.save(entity));
                });
    }
}
