package com.quevedo.mercado.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Punto de entrada del microservicio de Inventario del Mercado Municipal de Quevedo.
 *
 * Modulo responsable de la gestion de productos del mercado central:
 * alta, baja logica (soft delete) y listado paginado, protegido con JWT
 * y con cache-aside sobre Redis para el listado de productos.
 */
@SpringBootApplication
@EnableCaching
public class InventarioMercadoApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioMercadoApplication.class, args);
    }

}
