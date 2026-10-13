package com.ferreteria.msproductos.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msproductos.infrastructure.persistence.entity.AlmacenEntity;

public interface AlmacenRepository extends JpaRepository<AlmacenEntity, Long> {
}
