package com.quevedo.mercado.inventario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Envoltorio (wrapper) estandar para todas las respuestas de la API:
 * { "success": boolean, "data": T, "message": string, "meta": object?, "errors": array? }
 *
 * "data" y "message" siempre se serializan (incluso si "data" es null, por
 * ejemplo en una respuesta de error). "meta" (paginacion) y "errors"
 * (validacion) solo se incluyen cuando aplican, para no ensuciar el JSON
 * de las respuestas donde no tienen sentido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object meta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object errors;

    public static <T> ApiResponse<T> ok(T data, String message, Object meta) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .meta(meta)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ok(data, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(data)
                .message(message)
                .build();
    }

    /**
     * Respuesta 400 de validacion: data siempre null, errors con el detalle
     * por campo. Coincide con el contrato de ejemplo del enunciado.
     */
    public static <T> ApiResponse<T> validationError(String message, List<FieldErrorDTO> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .message(message)
                .errors(errors)
                .build();
    }

}
