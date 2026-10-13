package com.ferreteria.mscotizaciones.domain.constraint;

import java.util.List;

import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.ItemCotizacion;

public interface CotizacionConstraints {

    boolean validarItems(List<ItemCotizacion> items);
    boolean puedeEditar(Cotizacion cotizacion);
    boolean puedeEnviar(Cotizacion cotizacion);
    boolean puedeAprobar(Cotizacion cotizacion);
    boolean puedeRechazar(Cotizacion cotizacion);
    boolean puedeReenviar(Cotizacion cotizacion);
    boolean puedeAcceder(Cotizacion cotizacion, String rol, Long clienteIdToken);
    void calcularTotales(Cotizacion cotizacion);
}
