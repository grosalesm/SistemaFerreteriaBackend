package com.ferreteria.msproductos.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.application.port.outservice.StockOutService;
import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.MovimientoStock;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.infrastructure.mapper.StockMapper;
import com.ferreteria.msproductos.infrastructure.persistence.entity.AlmacenEntity;
import com.ferreteria.msproductos.infrastructure.persistence.entity.MovimientoStockEntity;
import com.ferreteria.msproductos.infrastructure.persistence.entity.StockEntity;
import com.ferreteria.msproductos.infrastructure.persistence.repository.AlmacenRepository;
import com.ferreteria.msproductos.infrastructure.persistence.repository.MovimientoStockRepository;
import com.ferreteria.msproductos.infrastructure.persistence.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockAdapter implements StockOutService {

    private final StockRepository stockRepository;
    private final AlmacenRepository almacenRepository;
    private final MovimientoStockRepository movimientoStockRepository;
    private final StockMapper stockMapper;

    @Override
    public List<Almacen> listarAlmacenes() {
        return stockMapper.toAlmacenes(almacenRepository.findAll(Sort.by("idAlmacen")));
    }

    @Override
    public Optional<Almacen> obtenerAlmacen(Long idAlmacen) {
        return almacenRepository.findById(idAlmacen).map(stockMapper::toAlmacen);
    }

    @Override
    public Almacen insertarAlmacen(Almacen almacen) {
        AlmacenEntity entity = stockMapper.toAlmacenEntity(almacen);
        entity.setIdAlmacen(null);
        return stockMapper.toAlmacen(almacenRepository.save(entity));
    }

    @Override
    public List<Stock> listar(Long idAlmacen) {
        if (idAlmacen == null) {
            return stockMapper.toBeans(stockRepository.findAll(Sort.by("idProducto", "idAlmacen")));
        }
        return stockMapper.toBeans(stockRepository.findByIdAlmacen(idAlmacen));
    }

    @Override
    public List<Stock> listarPorProducto(Long idProducto) {
        return stockMapper.toBeans(stockRepository.findByIdProducto(idProducto));
    }

    @Override
    public Optional<Stock> obtener(Long idProducto, Long idAlmacen) {
        return stockRepository.findByIdProductoAndIdAlmacen(idProducto, idAlmacen).map(stockMapper::toBean);
    }

    @Override
    public Stock crear(Long idProducto, Long idAlmacen) {
        StockEntity entity = new StockEntity(null, idProducto, idAlmacen, 0, 0);
        return stockMapper.toBean(stockRepository.save(entity));
    }

    @Override
    public int reservar(Long idProducto, Long idAlmacen, Integer cantidad) {
        return stockRepository.reservar(idProducto, idAlmacen, cantidad);
    }

    @Override
    public int liberar(Long idProducto, Long idAlmacen, Integer cantidad) {
        return stockRepository.liberar(idProducto, idAlmacen, cantidad);
    }

    @Override
    public int consumir(Long idProducto, Long idAlmacen, Integer cantidad) {
        return stockRepository.consumir(idProducto, idAlmacen, cantidad);
    }

    @Override
    public int ingresar(Long idProducto, Long idAlmacen, Integer cantidad) {
        return stockRepository.ingresar(idProducto, idAlmacen, cantidad);
    }

    @Override
    public int ajustar(Long idProducto, Long idAlmacen, Integer nuevaFisica) {
        return stockRepository.ajustar(idProducto, idAlmacen, nuevaFisica);
    }

    @Override
    public void registrarMovimiento(MovimientoStock movimiento) {
        MovimientoStockEntity entity = stockMapper.toMovimientoEntity(movimiento);
        entity.setIdMovimiento(null);
        entity.setSaldoFisico(stockRepository
                .findByIdProductoAndIdAlmacen(movimiento.getIdProducto(), movimiento.getIdAlmacen())
                .map(StockEntity::getCantidadFisica)
                .orElse(0));
        entity.setFecha(LocalDateTime.now());
        movimientoStockRepository.save(entity);
    }

    @Override
    public List<MovimientoStock> kardex(Long idProducto) {
        return stockMapper.toMovimientos(
                movimientoStockRepository.findByIdProductoOrderByFechaAscIdMovimientoAsc(idProducto));
    }
}
