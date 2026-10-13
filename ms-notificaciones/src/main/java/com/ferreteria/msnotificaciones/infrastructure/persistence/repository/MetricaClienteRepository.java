package com.ferreteria.msnotificaciones.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.MetricaClienteEntity;

public interface MetricaClienteRepository extends JpaRepository<MetricaClienteEntity, Long> {

    List<MetricaClienteEntity> findTop5ByOrderByMontoCompradoDesc();
}
