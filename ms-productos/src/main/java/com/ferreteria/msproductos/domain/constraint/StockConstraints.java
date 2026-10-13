package com.ferreteria.msproductos.domain.constraint;

import com.ferreteria.msproductos.domain.bean.Stock;

public interface StockConstraints {

    boolean puedeAjustar(Stock stock, Integer nuevaFisica);
    boolean esCritico(Stock stock, Integer stockMinimo);
}
