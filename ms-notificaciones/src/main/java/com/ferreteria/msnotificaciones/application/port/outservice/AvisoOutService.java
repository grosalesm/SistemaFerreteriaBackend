package com.ferreteria.msnotificaciones.application.port.outservice;

import com.ferreteria.msnotificaciones.domain.bean.Notificacion;

public interface AvisoOutService {

    void enviar(Notificacion notificacion);
}
