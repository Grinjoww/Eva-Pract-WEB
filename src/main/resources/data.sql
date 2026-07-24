-- Datos semilla del Sistema de Inventario - Mercado Municipal de Quevedo.
-- Ejecutado automaticamente por Spring Boot al arrancar (spring.sql.init.mode=always),
-- justo despues de schema.sql. Usa ON CONFLICT DO NOTHING para ser idempotente
-- (no falla si el contenedor se reinicia sin recrear el volumen).
--
-- Este mismo archivo tambien vive en /db/seed.sql en la raiz del repositorio.
--
-- Contrasenas en texto plano (solo para pruebas locales, NUNCA se guardan asi
-- en la base de datos, unicamente su hash BCrypt):
--   admin   / Admin123*    -> ROLE_ADMIN y ROLE_USER
--   usuario / Usuario123*  -> ROLE_USER

INSERT INTO usuarios (username, password) VALUES
    ('admin',   '$2b$10$B4HUB1gXFYYiSZu7C4QG1.cFuf2dH6/7pkLSzqk.9EzD5LCDgDq8W'),
    ('usuario', '$2b$10$ZoNYz3Y/.pXKxVyxyyli7.9gHuh9fDAn1yGVDzlrODFbSowJRVOIK')
ON CONFLICT (username) DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ROLE_ADMIN' FROM usuarios WHERE username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ROLE_USER' FROM usuarios WHERE username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, rol)
SELECT id, 'ROLE_USER' FROM usuarios WHERE username = 'usuario'
ON CONFLICT DO NOTHING;
