package com.quevedo.mercado.inventario.exception;

/**
 * Se lanza cuando se busca un recurso (por ejemplo, un Producto por id)
 * que no existe. GlobalExceptionHandler la traduce a HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
