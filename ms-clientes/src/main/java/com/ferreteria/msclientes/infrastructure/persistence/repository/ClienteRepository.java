package com.ferreteria.msclientes.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferreteria.msclientes.domain.bean.TipoCliente;
import com.ferreteria.msclientes.infrastructure.persistence.entity.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByNumeroDocumento(String numeroDocumento);

    @Query("""
            SELECT c FROM ClienteEntity c
            WHERE (:tipoCliente IS NULL OR c.tipoCliente = :tipoCliente)
              AND (LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR c.numeroDocumento LIKE CONCAT('%', :texto, '%'))
            ORDER BY c.nombreRazonSocial
            """)
    List<ClienteEntity> buscar(@Param("tipoCliente") TipoCliente tipoCliente, @Param("texto") String texto);
}
