package com.ferreteria.msproductos.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msproductos.infrastructure.persistence.entity.CategoriaEntity;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {

    Optional<CategoriaEntity> findByNombreIgnoreCase(String nombre);
}
