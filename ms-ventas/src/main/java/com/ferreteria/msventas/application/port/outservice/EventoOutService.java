package com.ferreteria.msventas.application.port.outservice;

import com.ferreteria.msventas.domain.bean.Pedido;

public interface EventoOutService {

    void publicarCambioEstado(Pedido pedido);
}
