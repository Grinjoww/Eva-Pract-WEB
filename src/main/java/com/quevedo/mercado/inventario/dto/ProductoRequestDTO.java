package com.quevedo.mercado.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de entrada para el alta de un producto (POST /api/v1/productos).
 * Todas las restricciones se validan automaticamente gracias a @Valid
 * en el controlador; cualquier violacion es capturada por
 * GlobalExceptionHandler y devuelta como HTTP 400 con el detalle por campo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {

    @NotBlank(message = "no debe estar vacio")
    @Size(max = 100, message = "no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "no debe estar vacio")
    @Size(max = 50, message = "no puede superar los 50 caracteres")
    private String categoria;

    @NotNull(message = "es obligatorio")
    @Min(value = 0, message = "debe ser mayor o igual a 0")
    private Integer stock;

    @NotNull(message = "es obligatorio")
    @DecimalMin(value = "0.01", message = "debe ser mayor o igual a 0.01")
    @Digits(integer = 8, fraction = 2, message = "debe tener maximo 8 digitos enteros y 2 decimales")
    private BigDecimal precio;

}
