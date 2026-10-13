package com.ferreteria.msproductos.application.port.usecase;

import java.util.List;

import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.MovimientoStock;
import com.ferreteria.msproductos.domain.bean.Stock;

public interface StockUseCase {

    List<Almacen> listarAlmacenes();
    List<Stock> listar(Long idAlmacen);
    List<Stock> listarPorProducto(Long idProducto);
    List<Stock> listarCriticos();
    Stock ajustar(Long idProducto, Long idAlmacen, Integer nuevaCantidad, String motivo);
    Stock ingresar(Long idProducto, Long idAlmacen, Integer cantidad);
    List<MovimientoStock> kardex(Long idProducto);
    void verificarStockCritico(Long idProducto, Long idAlmacen);
}
