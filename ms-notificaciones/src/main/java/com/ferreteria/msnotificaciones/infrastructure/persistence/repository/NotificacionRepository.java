package com.ferreteria.msnotificaciones.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.NotificacionEntity;

public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Long> {

    List<NotificacionEntity> findTop50ByRolDestinoOrderByFechaDesc(String rolDestino);
    List<NotificacionEntity> findTop50ByRolDestinoAndLeidaFalseOrderByFechaDesc(String rolDestino);
    List<NotificacionEntity> findTop50ByClienteIdAndRolDestinoIsNullOrderByFechaDesc(Long clienteId);
    List<NotificacionEntity> findTop50ByClienteIdAndRolDestinoIsNullAndLeidaFalseOrderByFechaDesc(Long clienteId);
    List<NotificacionEntity> findTop5ByTipoAndRolDestinoOrderByFechaDesc(String tipo, String rolDestino);
}
