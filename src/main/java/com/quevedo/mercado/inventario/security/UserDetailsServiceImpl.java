package com.quevedo.mercado.inventario.security;

import com.quevedo.mercado.inventario.entity.Usuario;
import com.quevedo.mercado.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacion de UserDetailsService que carga usuarios y roles desde
 * PostgreSQL (tablas "usuarios" y "usuario_roles", ver schema.sql /
 * data.sql), en lugar de usuarios hardcodeados en el codigo.
 *
 * Los roles ya vienen con el prefijo "ROLE_" desde la base de datos, por lo
 * que se usan directamente como GrantedAuthority (no se pasa por
 * User.builder().roles(...), que anadiria el prefijo por segunda vez).
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = usuarioRepository.findRolesByUsuarioId(usuario.getId()).stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(authorities)
                .build();
    }

}
