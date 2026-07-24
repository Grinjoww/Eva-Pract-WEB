package com.quevedo.mercado.inventario.repository;

import com.quevedo.mercado.inventario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    /**
     * Consulta nativa a la tabla "usuario_roles" (no mapeada como entidad
     * JPA a proposito, ver Usuario.java). Devuelve los roles ya con el
     * prefijo "ROLE_" tal como estan almacenados por data.sql, listos para
     * convertirse directamente en GrantedAuthority.
     */
    @Query(value = "SELECT rol FROM usuario_roles WHERE usuario_id = :usuarioId", nativeQuery = true)
    List<String> findRolesByUsuarioId(@Param("usuarioId") Long usuarioId);

}
