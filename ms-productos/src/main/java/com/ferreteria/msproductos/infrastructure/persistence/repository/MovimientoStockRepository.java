package com.ferreteria.msproductos.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msproductos.infrastructure.persistence.entity.MovimientoStockEntity;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStockEntity, Long> {

    List<MovimientoStockEntity> findByIdProductoOrderByFechaAscIdMovimientoAsc(Long idProducto);
}
