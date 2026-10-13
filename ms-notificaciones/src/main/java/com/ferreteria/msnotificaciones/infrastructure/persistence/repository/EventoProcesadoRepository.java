package com.ferreteria.msnotificaciones.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.EventoProcesadoEntity;

public interface EventoProcesadoRepository extends JpaRepository<EventoProcesadoEntity, String> {
}
