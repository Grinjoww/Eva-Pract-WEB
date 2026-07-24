package com.quevedo.mercado.inventario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de humo (smoke test): verifica que el contexto de Spring
 * levanta correctamente con toda la configuracion (JPA, Security,
 * Redis/Cache, JWT) cableada.
 *
 * Requiere que PostgreSQL y Redis esten disponibles (ver docker-compose.yml)
 * ya que el arranque del contexto valida la conexion a ambos.
 */
@SpringBootTest
class InventarioMercadoApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto de Spring falla al levantar, este test falla.
    }

}
