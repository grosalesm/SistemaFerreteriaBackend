package com.ferreteria.msventas.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.infrastructure.persistence.entity.PedidoEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    @Query("""
            SELECT p FROM PedidoEntity p
            WHERE (:estado IS NULL OR p.estado = :estado)
              AND (:clienteId IS NULL OR p.clienteId = :clienteId)
              AND p.fechaCreacion >= :desde AND p.fechaCreacion < :hasta
            ORDER BY p.idPedido DESC
            """)
    List<PedidoEntity> buscar(@Param("estado") EstadoPedido estado, @Param("clienteId") Long clienteId,
                              @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
    List<PedidoEntity> findByEstadoInOrderByFechaPagoAsc(List<EstadoPedido> estados);
    List<PedidoEntity> findByEstadoAndFechaLimitePagoBefore(EstadoPedido estado, LocalDateTime fecha);
    boolean existsByCotizacionId(Long cotizacionId);
}
