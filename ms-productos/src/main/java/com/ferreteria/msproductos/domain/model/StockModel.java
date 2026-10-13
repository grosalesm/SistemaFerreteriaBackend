package com.ferreteria.msproductos.domain.model;

import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.domain.constraint.StockConstraints;

@Component
public class StockModel implements StockConstraints {

    @Override
    public boolean puedeAjustar(Stock stock, Integer nuevaFisica) {
        return nuevaFisica != null && nuevaFisica >= stock.getCantidadReservada();
    }

    @Override
    public boolean esCritico(Stock stock, Integer stockMinimo) {
        return stock.disponible() <= stockMinimo;
    }
}
