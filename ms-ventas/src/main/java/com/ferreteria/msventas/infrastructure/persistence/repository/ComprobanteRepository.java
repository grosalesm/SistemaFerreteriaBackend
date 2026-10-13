package com.ferreteria.msventas.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferreteria.msventas.infrastructure.persistence.entity.ComprobanteEntity;

public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, Long> {

    Optional<ComprobanteEntity> findByIdPedido(Long idPedido);
    @Query("SELECT COALESCE(MAX(c.numero), 0) FROM ComprobanteEntity c WHERE c.serie = :serie")
    int ultimoNumero(@Param("serie") String serie);
}
