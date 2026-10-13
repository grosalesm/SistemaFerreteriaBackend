package com.ferreteria.msproductos.application.port.outservice;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.MovimientoStock;
import com.ferreteria.msproductos.domain.bean.Stock;

public interface StockOutService {

    List<Almacen> listarAlmacenes();
    Optional<Almacen> obtenerAlmacen(Long idAlmacen);
    Almacen insertarAlmacen(Almacen almacen);
    List<Stock> listar(Long idAlmacen);
    List<Stock> listarPorProducto(Long idProducto);
    Optional<Stock> obtener(Long idProducto, Long idAlmacen);
    Stock crear(Long idProducto, Long idAlmacen);
    int reservar(Long idProducto, Long idAlmacen, Integer cantidad);
    int liberar(Long idProducto, Long idAlmacen, Integer cantidad);
    int consumir(Long idProducto, Long idAlmacen, Integer cantidad);
    int ingresar(Long idProducto, Long idAlmacen, Integer cantidad);
    int ajustar(Long idProducto, Long idAlmacen, Integer nuevaFisica);
    void registrarMovimiento(MovimientoStock movimiento);
    List<MovimientoStock> kardex(Long idProducto);
}
