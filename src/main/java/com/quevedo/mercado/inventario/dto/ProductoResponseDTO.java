package com.quevedo.mercado.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de salida que representa un producto en las respuestas de la API.
 * Se usa para no exponer directamente la entidad JPA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO implements Serializable {

    private Long id;
    private String nombre;
    private String categoria;
    private Integer stock;
    private BigDecimal precio;
    private Boolean activo;
    private OffsetDateTime creadoEn;

}
