package com.quevedo.mercado.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de salida devuelto tras un login exitoso, con el token JWT
 * y los roles otorgados al usuario autenticado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String tipo;
    private String username;
    private List<String> roles;
    private long expiraEnMs;

}
