package com.ferreteria.mscotizaciones.application.port.outservice;

import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;

public interface EventoOutService {

    void publicarCambioEstado(Cotizacion cotizacion);
}
