package com.ferreteria.msnotificaciones.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.MetricaProductoEntity;

public interface MetricaProductoRepository extends JpaRepository<MetricaProductoEntity, Long> {

    List<MetricaProductoEntity> findTop5ByOrderByUnidadesAprobadasDesc();
}
