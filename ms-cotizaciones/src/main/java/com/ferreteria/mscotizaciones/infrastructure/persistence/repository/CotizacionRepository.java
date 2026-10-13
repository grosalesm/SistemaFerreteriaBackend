package com.ferreteria.mscotizaciones.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;
import com.ferreteria.mscotizaciones.infrastructure.persistence.entity.CotizacionEntity;

public interface CotizacionRepository extends JpaRepository<CotizacionEntity, Long> {

    @Query("""
            SELECT c FROM CotizacionEntity c
            WHERE (:estado IS NULL OR c.estado = :estado)
              AND (:clienteId IS NULL OR c.clienteId = :clienteId)
              AND c.fechaCreacion >= :desde AND c.fechaCreacion < :hasta
            ORDER BY c.idCotizacion DESC
            """)
    List<CotizacionEntity> buscar(@Param("estado") EstadoCotizacion estado, @Param("clienteId") Long clienteId,
                                  @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    List<CotizacionEntity> findByEstadoAndFechaVencimientoBefore(EstadoCotizacion estado, LocalDateTime fecha);
}
