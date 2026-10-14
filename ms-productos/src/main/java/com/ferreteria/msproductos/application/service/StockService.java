package com.ferreteria.msproductos.application.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msproductos.application.port.outservice.EventoOutService;
import com.ferreteria.msproductos.application.port.outservice.ProductoOutService;
import com.ferreteria.msproductos.application.port.outservice.StockOutService;
import com.ferreteria.msproductos.application.port.usecase.StockUseCase;
import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.MovimientoStock;
import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.domain.bean.TipoMovimiento;
import com.ferreteria.msproductos.domain.constraint.StockConstraints;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService implements StockUseCase {

    private final StockOutService stockOutService;
    private final ProductoOutService productoOutService;
    private final EventoOutService eventoOutService;
    private final StockConstraints stockConstraints;

    @Override
    @Transactional(readOnly = true)
    public List<Almacen> listarAlmacenes() {
        return stockOutService.listarAlmacenes();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> listar(Long idAlmacen) {
        Map<Long, Boolean> activos = productoOutService.listar(null, "").stream()
                .collect(Collectors.toMap(Producto::getIdProducto, p -> Boolean.TRUE.equals(p.getActivo())));
        List<Stock> stocks = stockOutService.listar(idAlmacen);
        stocks.forEach(s -> s.setProductoActivo(activos.getOrDefault(s.getIdProducto(), false)));
        return stocks;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> listarPorProducto(Long idProducto) {
        return stockOutService.listarPorProducto(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> listarCriticos() {
        Map<Long, Producto> productos = productoOutService.listar(null, "").stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Function.identity()));
        return stockOutService.listar(null).stream()
                .filter(stock -> {
                    Producto producto = productos.get(stock.getIdProducto());
                    return producto != null
                            && Boolean.TRUE.equals(producto.getActivo())
                            && stockConstraints.esCritico(stock, producto.getStockMinimo());
                })
                .peek(stock -> stock.setProductoActivo(true))
                .toList();
    }

    @Override
    @Transactional
    public Stock ajustar(Long idProducto, Long idAlmacen, Integer nuevaCantidad, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo del ajuste es obligatorio");
        }
        Stock stock = obtenerOCrear(idProducto, idAlmacen);
        if (!stockConstraints.puedeAjustar(stock, nuevaCantidad)) {
            throw new IllegalArgumentException("La nueva cantidad no puede ser menor a lo reservado ("
                    + stock.getCantidadReservada() + ")");
        }
        if (stockOutService.ajustar(idProducto, idAlmacen, nuevaCantidad) == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El stock cambió mientras se ajustaba, intente nuevamente");
        }
        registrarMovimiento(TipoMovimiento.AJUSTE, idProducto, idAlmacen,
                nuevaCantidad - stock.getCantidadFisica(), "AJUSTE: " + motivo.trim());
        verificarStockCritico(idProducto, idAlmacen);
        return stockOutService.obtener(idProducto, idAlmacen).orElseThrow();
    }

    @Override
    @Transactional
    public Stock ingresar(Long idProducto, Long idAlmacen, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a ingresar debe ser mayor a 0");
        }
        obtenerOCrear(idProducto, idAlmacen);
        stockOutService.ingresar(idProducto, idAlmacen, cantidad);
        registrarMovimiento(TipoMovimiento.INGRESO, idProducto, idAlmacen, cantidad, "INGRESO de mercadería");
        return stockOutService.obtener(idProducto, idAlmacen).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoStock> kardex(Long idProducto) {
        return stockOutService.kardex(idProducto);
    }

    @Override
    @Transactional
    public void verificarStockCritico(Long idProducto, Long idAlmacen) {
        Producto producto = productoOutService.obtenerPorId(idProducto).orElse(null);
        Stock stock = stockOutService.obtener(idProducto, idAlmacen).orElse(null);
        if (producto != null && stock != null && stockConstraints.esCritico(stock, producto.getStockMinimo())) {
            eventoOutService.publicarStockCritico(producto, stock);
        }
    }

    private Stock obtenerOCrear(Long idProducto, Long idAlmacen) {
        if (productoOutService.obtenerPorId(idProducto).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        if (stockOutService.obtenerAlmacen(idAlmacen).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Almacén no encontrado");
        }
        return stockOutService.obtener(idProducto, idAlmacen)
                .orElseGet(() -> stockOutService.crear(idProducto, idAlmacen));
    }

    private void registrarMovimiento(TipoMovimiento tipo, Long idProducto, Long idAlmacen,
                                     Integer cantidad, String referencia) {
        stockOutService.registrarMovimiento(
                new MovimientoStock(null, idProducto, idAlmacen, tipo, cantidad, null, referencia, null));
    }
}
