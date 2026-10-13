package com.ferreteria.msnotificaciones.domain.constraint;

import java.util.List;

import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.infrastructure.dto.EventoNegocio;

public interface NotificacionConstraints {

    String CLIENTE = "CLIENTE";

    List<String> destinos(String routingKey);
    Notificacion armarMensaje(String routingKey, EventoNegocio evento);
    boolean debeContarse(String routingKey);
    String claveEvento(String routingKey, EventoNegocio evento);
    boolean puedeVer(Notificacion notificacion, String rol, Long clienteIdToken);
}
