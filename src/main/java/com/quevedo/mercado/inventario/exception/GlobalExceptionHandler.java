package com.quevedo.mercado.inventario.exception;

import com.quevedo.mercado.inventario.dto.ApiResponse;
import com.quevedo.mercado.inventario.dto.FieldErrorDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Manejador centralizado de excepciones para toda la API.
 * Traduce cada tipo de excepcion al codigo HTTP y al formato
 * { success, data, message, meta } acordado para las respuestas.
 *
 * Nota: los rechazos de autenticacion (401) y de autorizacion por rol
 * insuficiente (403) que ocurren dentro de la cadena de filtros de Spring
 * Security (antes de llegar al DispatcherServlet) NO pasan por aqui;
 * esos casos se resuelven en JwtAuthenticationEntryPoint y
 * CustomAccessDeniedHandler respectivamente. Este handler cubre ademas
 * los casos en que una AccessDeniedException se origina dentro de la
 * capa de aplicacion (por ejemplo, con @PreAuthorize en un servicio).
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDTO> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> FieldErrorDTO.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError("Error de validacion", errores));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex) {
        // Ultima linea de defensa: si algun dato invalido llegara a violar un
        // CHECK de la base de datos (por ejemplo stock >= 0 o precio >= 0.01)
        // pese a haber pasado la validacion de Bean Validation, se traduce
        // igualmente a 400 en vez de un 500 generico.
        log.warn("Violacion de integridad de datos: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>error("Los datos enviados violan una restriccion de la base de datos", null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>error(ex.getMessage(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMalformedJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>error("El cuerpo de la solicitud no es un JSON valido", null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String mensaje = "El parametro '" + ex.getName() + "' tiene un formato invalido";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>error(mensaje, null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Object>error(ex.getMessage(), null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Object>error("Usuario o contrasena incorrectos", null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Object>error("Acceso denegado: no cuenta con el rol necesario para esta operacion", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        log.error("Error inesperado procesando la solicitud", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>error("Ocurrio un error interno en el servidor", null));
    }

}
