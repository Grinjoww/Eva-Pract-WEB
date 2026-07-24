package com.quevedo.mercado.inventario.repository;

import com.quevedo.mercado.inventario.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Devuelve unicamente los productos activos (no eliminados logicamente),
     * de forma paginada y ordenada segun el Pageable recibido.
     */
    Page<Producto> findByActivoTrue(Pageable pageable);

}
