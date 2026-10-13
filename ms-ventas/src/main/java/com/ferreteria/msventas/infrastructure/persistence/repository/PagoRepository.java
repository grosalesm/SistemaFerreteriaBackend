package com.ferreteria.msventas.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferreteria.msventas.infrastructure.persistence.entity.PagoEntity;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    Optional<PagoEntity> findByIdPedido(Long idPedido);
}
