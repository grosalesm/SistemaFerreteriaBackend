package com.ferreteria.msproductos.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferreteria.msproductos.infrastructure.persistence.entity.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    Optional<ProductoEntity> findBySku(String sku);

    @Query("""
            SELECT p FROM ProductoEntity p
            WHERE (:idCategoria IS NULL OR p.idCategoria = :idCategoria)
              AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :texto, '%')))
            ORDER BY p.nombre
            """)
    List<ProductoEntity> buscar(@Param("idCategoria") Long idCategoria, @Param("texto") String texto);
}
