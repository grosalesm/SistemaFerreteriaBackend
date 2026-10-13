package com.ferreteria.msnotificaciones.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.MetricaDiariaEntity;

public interface MetricaDiariaRepository extends JpaRepository<MetricaDiariaEntity, Long> {

    Optional<MetricaDiariaEntity> findByFecha(LocalDate fecha);
    List<MetricaDiariaEntity> findByFechaBetweenOrderByFecha(LocalDate desde, LocalDate hasta);
}
