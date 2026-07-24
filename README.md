# Inventario - Mercado Municipal de Quevedo

Servicio web REST con Spring Boot 3 (Java 21 LTS) para la gestion de
inventario del Mercado Municipal de Quevedo. Expone el recurso `Producto`
paginado bajo un contrato JSON uniforme, valida la entrada en el servidor,
aplica borrado logico, integra el patron cache-aside con Redis y protege
sus operaciones con JWT.

> Examen Parcial - Aplicaciones Web - Ingenieria de Software (Redisenio) - UTEQ

## Datos del estudiante

| Campo | Valor |
|---|---|
| Apellidos y nombres | **Mariscal Cabrera Jaime Josué** |
| Cedula | **1250710835** |
| Paralelo | **A** |
| Version de Java | 21 LTS (Eclipse Temurin) |
| Version de Spring Boot | 3.3.4 |

## Contenido

- [1. Requisitos previos](#1-requisitos-previos)
- [2. Arranque en un solo comando (Docker)](#2-arranque-en-un-solo-comando-docker)
- [3. Usuarios semilla y como obtener el token JWT](#3-usuarios-semilla-y-como-obtener-el-token-jwt)
- [4. Endpoints de la API](#4-endpoints-de-la-api)
- [5. Coleccion de pruebas (docs/requests.http)](#5-coleccion-de-pruebas-docsrequestshttp)
- [6. Compilar el informe tecnico (LaTeX)](#6-compilar-el-informe-tecnico-latex)
- [7. Desarrollo local alternativo (IntelliJ, sin Docker para la app)](#7-desarrollo-local-alternativo-intellij-sin-docker-para-la-app)
- [8. Patron cache-aside con Redis](#8-patron-cache-aside-con-redis)
- [9. Estructura del repositorio](#9-estructura-del-repositorio)
- [10. Notas de diseno](#10-notas-de-diseno)

---

## 1. Requisitos previos

Solo se necesita **Docker Desktop** (o Docker Engine + el plugin
`docker compose`) instalado y en ejecucion. No hace falta instalar Java,
Maven, PostgreSQL ni Redis manualmente: todo se construye y se levanta
dentro de contenedores.

Para el desarrollo local opcional en IntelliJ (seccion 7) si hace falta
JDK 21 e IntelliJ IDEA.

## 2. Arranque en un solo comando (Docker)

```bash
git clone https://github.com/USUARIO/inventario-mercado-APELLIDO.git
cd inventario-mercado-APELLIDO

# Variables de entorno (el secreto JWT NUNCA se sube al repositorio)
cp .env.example .env

# Levanta PostgreSQL, Redis y la aplicacion (construye la imagen la primera vez)
docker compose up -d --build
```

Verificar que los tres contenedores esten activos y saludables:

```bash
docker compose ps
```

Debe verse `mercado-quevedo-postgres`, `mercado-quevedo-redis` y
`mercado-quevedo-app` en estado `Up` (los dos primeros ademas `healthy`).
La API queda disponible en:

```
http://localhost:8080/api/v1/productos
```

Al arrancar, la aplicacion ejecuta automaticamente `schema.sql` (crea las
tablas `productos`, `usuarios` y `usuario_roles` con sus restricciones
`CHECK`) y `data.sql` (siembra los dos usuarios de prueba). Ambos son
idempotentes: reiniciar los contenedores sin borrar el volumen no falla.

Para detener todo:

```bash
docker compose down
```

Para reiniciar completamente desde cero (borra tambien los datos):

```bash
docker compose down -v
docker compose up -d --build
```

## 3. Usuarios semilla y como obtener el token JWT

Los usuarios se crean automaticamente en PostgreSQL por `db/seed.sql`
(contrasenas cifradas con BCrypt, nunca en texto plano):

| Usuario | Contrasena | Roles |
|---|---|---|
| `admin` | `Admin123*` | `ROLE_ADMIN`, `ROLE_USER` |
| `usuario` | `Usuario123*` | `ROLE_USER` |

**Obtener un token:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123*"}'
```

Respuesta:

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tipo": "Bearer",
    "username": "admin",
    "roles": ["ROLE_ADMIN", "ROLE_USER"],
    "expiraEnMs": 3600000
  },
  "message": "Inicio de sesion exitoso"
}
```

Usa ese `token` en el header `Authorization: Bearer <token>` para los
endpoints protegidos.

## 4. Endpoints de la API

Todas las respuestas comparten la envoltura:
`{ success, data, message, meta?, errors? }` (`meta` solo en el listado
paginado, `errors` solo en las respuestas 400 de validacion).

| Metodo | Ruta | Rol requerido | Descripcion |
|---|---|---|---|
| `GET` | `/api/v1/productos?page=&size=&sort=` | `ROLE_USER` | Lista productos activos, paginado |
| `POST` | `/api/v1/productos` | `ROLE_ADMIN` | Crea un producto (valida con `@Valid`) |
| `DELETE` | `/api/v1/productos/{id}` | `ROLE_ADMIN` | Baja logica (`activo=false`) |
| `POST` | `/api/v1/auth/login` | publico | Autentica y emite el token JWT |

Codigos de estado:

| Escenario | Codigo |
|---|---|
| Operacion exitosa | `200` / `201` |
| Body invalido (`nombre` vacio, `precio < 0.01`, `stock < 0`) | `400` con `errors: [{field, message}]` |
| Sin token, o token invalido/expirado | `401` |
| Token valido pero rol insuficiente | `403` |
| `id` inexistente en `DELETE` | `404` |

## 5. Coleccion de pruebas (docs/requests.http)

El archivo [`docs/requests.http`](docs/requests.http) contiene una
peticion por cada uno de los 5 requisitos y por cada codigo de error
exigido (incluye cache hit/miss). Es compatible con:

- El **HTTP Client** integrado de IntelliJ IDEA (abrir el archivo y hacer
  clic en el icono ▶ junto a cada peticion).
- La extension **REST Client** de VS Code.

Flujo sugerido: ejecutar las dos peticiones de login, copiar cada
`data.token` en los placeholders `TOKEN_ADMIN` / `TOKEN_USER`, y luego
ejecutar el resto de peticiones en orden.

## 6. Compilar el informe tecnico (LaTeX)

El informe fuente esta en [`docs/informe/informe.tex`](docs/informe/informe.tex)
con su bibliografia en `docs/informe/referencias.bib`.

**Dependencias:** una distribucion de LaTeX con `pdflatex` y `bibtex`
(por ejemplo, en Ubuntu/Debian: `texlive-latex-base`
`texlive-latex-extra` `texlive-fonts-recommended`; en Windows,
[MiKTeX](https://miktex.org/) instala los paquetes faltantes
automaticamente la primera vez que se compila).

**Cadena de compilacion** (motor `pdflatex`, procesador `bibtex`, 4 pasadas
en total para resolver indice, citas y referencias cruzadas):

```bash
cd docs/informe
pdflatex informe && bibtex informe && pdflatex informe && pdflatex informe
```

El resultado es `docs/informe/informe.pdf`. Esta cadena ya fue verificada:
las 4 pasadas terminan con codigo de salida 0 y sin advertencias de
citas/referencias indefinidas.

> **Antes de la entrega final:** completar en la caratula del informe
> (primera pagina de `informe.tex`) los apellidos, nombres, cedula,
> paralelo, fecha del examen y la URL real del repositorio publico.

## 7. Desarrollo local alternativo (IntelliJ, sin Docker para la app)

Si se prefiere ejecutar la aplicacion directamente desde IntelliJ (por
ejemplo, para depurar) en lugar de dentro de Docker:

1. Levantar solo la base de datos y Redis: `docker compose up -d postgres redis`.
2. Abrir el proyecto en IntelliJ (`File > Open` sobre `pom.xml`) y esperar
   a que Maven descargue las dependencias.
3. Definir la variable de entorno `JWT_SECRET` en la configuracion de
   ejecucion (`Run > Edit Configurations > Environment variables`), con el
   mismo valor que tengas en tu `.env`.
4. Ejecutar `InventarioMercadoApplication`. Por defecto se conecta a
   `localhost:5432` / `localhost:6379` (ver `application.yml`).

## 8. Patron cache-aside con Redis

- `GET /api/v1/productos` esta anotado con `@Cacheable("productos")`: la
  primera vez para una combinacion de `page/size/sort` es un **cache
  miss** (consulta PostgreSQL y guarda el resultado en Redis); las
  siguientes peticiones identicas son un **cache hit** (se sirven desde
  Redis sin tocar la base de datos) hasta que expire el TTL (10 minutos).
- `POST /api/v1/productos` y `DELETE /api/v1/productos/{id}` estan
  anotados con `@CacheEvict("productos", allEntries=true)`: cualquier alta
  o baja invalida todo el cache de listados.
- Verificacion: los logs de la app muestran `Cache MISS -> consultando...`
  solo cuando realmente se consulta la base de datos. Tambien se puede
  inspeccionar Redis directamente:
  ```bash
  docker exec -it mercado-quevedo-redis redis-cli KEYS "productos*"
  ```

## 9. Estructura del repositorio

```
inventario-mercado-<apellido>/
|-- README.md
|-- LICENSE
|-- .gitignore
|-- .env.example
|-- docker-compose.yml          # app + PostgreSQL + Redis, un solo comando
|-- Dockerfile                  # build multi-stage (Maven -> JRE 21)
|-- pom.xml
|-- db/
|   |-- schema.sql               # DDL de referencia (identico al de src/main/resources)
|   `-- seed.sql                 # usuarios ROLE_USER y ROLE_ADMIN (hash BCrypt)
|-- docs/
|   |-- requests.http            # coleccion de pruebas (los 5 requisitos + errores)
|   |-- capturas/                # evidencia (200/400/401/403/404, cache hit/miss)
|   `-- informe/
|       |-- informe.tex          # informe tecnico (criterio de piso P2)
|       |-- referencias.bib
|       `-- informe.pdf          # version ya compilada, de referencia
`-- src/                         # codigo fuente Spring Boot 3
    |-- main/java/.../config/       # SecurityConfig, RedisCacheConfig
    |-- main/java/.../security/     # JWT: filtro, utilidades, entry point 401, access denied 403
    |-- main/java/.../controller/   # ProductoController, AuthController
    |-- main/java/.../service/      # logica de negocio + cache-aside
    |-- main/java/.../repository/   # Spring Data JPA (Producto, Usuario)
    |-- main/java/.../entity/       # Producto, Usuario
    |-- main/java/.../dto/          # ApiResponse, DTOs de entrada/salida
    |-- main/java/.../mapper/
    |-- main/java/.../exception/    # GlobalExceptionHandler
    `-- main/resources/
        |-- application.yml
        |-- schema.sql               # se ejecuta automaticamente al arrancar
        `-- data.sql                 # se ejecuta automaticamente al arrancar
```

## 10. Notas de diseno

- **Esquema gestionado por SQL, no por Hibernate:** `spring.jpa.hibernate.ddl-auto=validate`.
  El esquema real lo crea `schema.sql` (con los `CHECK` de `stock`/`precio`
  incluidos); Hibernate solo valida que las entidades JPA coincidan.
- **Usuarios en base de datos, no hardcodeados:** `UserDetailsServiceImpl`
  carga usuarios y roles desde PostgreSQL (`usuarios` / `usuario_roles`),
  con contrasenas cifradas en BCrypt sembradas por `seed.sql`.
- **Secreto JWT fuera del repositorio:** `application.yml` NO tiene un
  valor por defecto para `jwt.secret`; se exige por variable de entorno
  (`.env`, no versionado). Si falta, el arranque falla explicitamente en
  vez de exponer un secreto embebido.
- **Contrato de error uniforme:** `data` y `message` siempre se
  serializan (incluso si `data` es `null`); `meta` solo aparece en el
  listado paginado y `errors` (con `field`/`message` por cada violacion)
  solo en las respuestas 400 de validacion.
- **`BIGSERIAL` -> `GenerationType.IDENTITY`:** Hibernate 6 + PostgreSQL
  traduce esta estrategia en una columna `BIGINT GENERATED BY DEFAULT AS
  IDENTITY`, el equivalente moderno recomendado a `BIGSERIAL`.
- **Cache de `Page<T>`:** el servicio devuelve y cachea un DTO propio
  (`PageResultDTO`), no directamente un `Page` de Spring Data (cuya
  implementacion `PageImpl` no serializa de forma confiable a JSON en Redis).


