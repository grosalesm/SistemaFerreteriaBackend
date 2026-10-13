package com.ferreteria.msnotificaciones.application.port.usecase;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.infrastructure.dto.EventoNegocio;

public interface NotificacionUseCase {

    boolean procesar(String routingKey, EventoNegocio evento);
    List<Notificacion> listar(boolean soloNoLeidas, String rol, Long clienteIdToken);
    Optional<Notificacion> marcarLeida(Long idNotificacion, String rol, Long clienteIdToken);
}
