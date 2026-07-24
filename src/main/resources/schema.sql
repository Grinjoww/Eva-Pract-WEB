-- Esquema de base de datos del Sistema de Inventario - Mercado Municipal de Quevedo.
-- Ejecutado automaticamente por Spring Boot al arrancar (spring.sql.init.mode=always),
-- ANTES de que Hibernate valide el mapeo JPA (spring.jpa.hibernate.ddl-auto=validate).
--
-- Este mismo archivo tambien vive en /db/schema.sql en la raiz del repositorio,
-- como referencia de documentacion independiente del codigo fuente.

CREATE TABLE IF NOT EXISTS usuarios (
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT      NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol        VARCHAR(20) NOT NULL,
    PRIMARY KEY (usuario_id, rol)
);

CREATE TABLE IF NOT EXISTS productos (
    id         BIGSERIAL PRIMARY KEY,
    nombre     VARCHAR(100)   NOT NULL,
    categoria  VARCHAR(50)    NOT NULL,
    stock      INTEGER        NOT NULL CHECK (stock >= 0),
    precio     DECIMAL(10, 2) NOT NULL CHECK (precio >= 0.01),
    activo     BOOLEAN        NOT NULL DEFAULT TRUE,
    creado_en  TIMESTAMPTZ    NOT NULL DEFAULT now()
);
