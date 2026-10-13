package com.ferreteria.msnotificaciones.application.port.outservice;

import java.util.List;
import java.util.Optional;

import com.ferreteria.msnotificaciones.domain.bean.Notificacion;

public interface NotificacionOutService {

    Notificacion guardar(Notificacion notificacion);
    Optional<Notificacion> obtenerPorId(Long idNotificacion);
    List<Notificacion> listarPorRol(String rol, boolean soloNoLeidas);
    List<Notificacion> listarPorCliente(Long clienteId, boolean soloNoLeidas);
    List<Notificacion> ultimasAlertasStock();
    boolean eventoProcesado(String clave);
    void registrarEvento(String clave);
}
