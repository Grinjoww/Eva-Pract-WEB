package com.quevedo.mercado.inventario.mapper;

import com.quevedo.mercado.inventario.dto.ProductoRequestDTO;
import com.quevedo.mercado.inventario.dto.ProductoResponseDTO;
import com.quevedo.mercado.inventario.entity.Producto;

/**
 * Conversion manual (sin MapStruct) entre la entidad Producto y sus DTOs.
 * Al ser un mapeo simple, se prefiere esta clase utilitaria estatica
 * para no anadir una dependencia adicional al proyecto.
 */
public final class ProductoMapper {

    private ProductoMapper() {
    }

    public static Producto toEntity(ProductoRequestDTO dto) {
        return Producto.builder()
                .nombre(dto.getNombre())
                .categoria(dto.getCategoria())
                .stock(dto.getStock())
                .precio(dto.getPrecio())
                .activo(Boolean.TRUE)
                .build();
    }

    public static ProductoResponseDTO toResponseDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .stock(producto.getStock())
                .precio(producto.getPrecio())
                .activo(producto.getActivo())
                .creadoEn(producto.getCreadoEn())
                .build();
    }

}
