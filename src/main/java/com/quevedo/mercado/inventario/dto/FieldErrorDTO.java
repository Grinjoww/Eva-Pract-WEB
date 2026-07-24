package com.quevedo.mercado.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un error de validacion asociado a un campo especifico del
 * DTO de entrada. Se usa dentro de la lista "errors" de la respuesta 400,
 * con las claves "field" y "message" tal como exige el contrato de la API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDTO {

    private String field;
    private String message;

}
