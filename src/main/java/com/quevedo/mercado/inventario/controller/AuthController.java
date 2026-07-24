package com.quevedo.mercado.inventario.controller;

import com.quevedo.mercado.inventario.dto.ApiResponse;
import com.quevedo.mercado.inventario.dto.LoginRequestDTO;
import com.quevedo.mercado.inventario.dto.LoginResponseDTO;
import com.quevedo.mercado.inventario.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint auxiliar de autenticacion, necesario para poder probar de punta
 * a punta la proteccion JWT de los endpoints de productos sin depender de
 * un servidor de identidad externo. Emite tokens firmados con los roles
 * del usuario autenticado (ver SecurityConfig para los usuarios de prueba).
 *
 * No forma parte de los 3 endpoints solicitados en el enunciado, pero es
 * el mecanismo que permite generar los tokens Bearer que dichos endpoints
 * exigen.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Autentica al usuario y, de ser exitoso, emite un token JWT (Bearer)
     * firmado con los roles del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtUtil.generateToken(request.getUsername(), roles);

        LoginResponseDTO body = LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .username(request.getUsername())
                .roles(roles)
                .expiraEnMs(jwtUtil.getExpirationMs())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(body, "Inicio de sesion exitoso"));
    }

}
