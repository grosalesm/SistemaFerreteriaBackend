package com.ferreteria.msproductos.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferreteria.msproductos.infrastructure.persistence.entity.StockEntity;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    List<StockEntity> findByIdAlmacen(Long idAlmacen);

    List<StockEntity> findByIdProducto(Long idProducto);

    Optional<StockEntity> findByIdProductoAndIdAlmacen(Long idProducto, Long idAlmacen);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE stock SET cantidad_reservada = cantidad_reservada + :cant
            WHERE id_producto = :p AND id_almacen = :a AND cantidad_fisica - cantidad_reservada >= :cant
            """, nativeQuery = true)
    int reservar(@Param("p") Long idProducto, @Param("a") Long idAlmacen, @Param("cant") Integer cantidad);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE stock SET cantidad_reservada = cantidad_reservada - :cant
            WHERE id_producto = :p AND id_almacen = :a
            """, nativeQuery = true)
    int liberar(@Param("p") Long idProducto, @Param("a") Long idAlmacen, @Param("cant") Integer cantidad);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE stock SET cantidad_fisica = cantidad_fisica - :cant, cantidad_reservada = cantidad_reservada - :cant
            WHERE id_producto = :p AND id_almacen = :a
            """, nativeQuery = true)
    int consumir(@Param("p") Long idProducto, @Param("a") Long idAlmacen, @Param("cant") Integer cantidad);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE stock SET cantidad_fisica = cantidad_fisica + :cant
            WHERE id_producto = :p AND id_almacen = :a
            """, nativeQuery = true)
    int ingresar(@Param("p") Long idProducto, @Param("a") Long idAlmacen, @Param("cant") Integer cantidad);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE stock SET cantidad_fisica = :nueva
            WHERE id_producto = :p AND id_almacen = :a AND cantidad_reservada <= :nueva
            """, nativeQuery = true)
    int ajustar(@Param("p") Long idProducto, @Param("a") Long idAlmacen, @Param("nueva") Integer nuevaFisica);
}
