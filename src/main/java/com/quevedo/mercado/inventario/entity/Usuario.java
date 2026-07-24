package com.quevedo.mercado.inventario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un usuario de autenticacion, persistido en la
 * tabla "usuarios" (creada por schema.sql y poblada por data.sql con
 * contrasenas ya cifradas con BCrypt).
 *
 * Los roles del usuario viven en una tabla separada ("usuario_roles") y se
 * consultan mediante UsuarioRepository, sin mapearlos como una relacion JPA,
 * para mantener el modelo simple y evitar ambiguedades de validacion de
 * esquema con Hibernate (ddl-auto=validate).
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /** Contrasena ya cifrada con BCrypt (nunca en texto plano). */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

}
