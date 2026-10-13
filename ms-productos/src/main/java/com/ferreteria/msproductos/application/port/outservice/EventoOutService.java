package com.ferreteria.msproductos.application.port.outservice;

import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.Stock;

public interface EventoOutService {

    void publicarStockCritico(Producto producto, Stock stock);
}
