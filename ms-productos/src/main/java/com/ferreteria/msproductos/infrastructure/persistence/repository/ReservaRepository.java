package com.ferreteria.msproductos.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferreteria.msproductos.domain.bean.EstadoReserva;
import com.ferreteria.msproductos.infrastructure.persistence.entity.ReservaEntity;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    Optional<ReservaEntity> findByCotizacionId(Long cotizacionId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE ReservaEntity r SET r.estado = :nuevoEstado
            WHERE r.idReserva = :idReserva AND r.estado = com.ferreteria.msproductos.domain.bean.EstadoReserva.ACTIVA
            """)
    int cambiarEstadoSiActiva(@Param("idReserva") Long idReserva, @Param("nuevoEstado") EstadoReserva nuevoEstado);
}
