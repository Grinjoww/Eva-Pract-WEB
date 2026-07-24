package com.quevedo.mercado.inventario.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quevedo.mercado.inventario.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Se invoca cuando un usuario esta autenticado (token JWT valido) pero
 * no posee el rol requerido para el endpoint solicitado.
 * Devuelve HTTP 403 con el formato estandar { success, data, message, meta }.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                        HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Object> body = ApiResponse.error(
                "Acceso denegado: su rol no tiene permisos para realizar esta operacion", null);

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}
