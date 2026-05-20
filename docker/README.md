# Docker — Libertya

Estructura del directorio:

```
docker/
├── .gitignore               ← excluye artefactos de build (ZIP, dump)
├── docker-compose.yml       ← archivo distribuible para usuarios finales
├── README.md
├── app/
│   ├── Dockerfile           ← imagen del servidor Libertya (JBoss)
│   └── docker-entrypoint.sh ← genera LibertyaEnv.properties y arranca JBoss
└── db/
    ├── Dockerfile           ← imagen PostgreSQL con BD base restaurada
    └── init-libertya-db.sh  ← script de inicialización del volumen
```

Los artefactos de build (ZIP del release, dump SQL) **no se versionan** en git.
Deben copiarse localmente antes de ejecutar `docker build` (ver instrucciones abajo).

---

## Requisitos previos

- Docker Engine 24+ y Docker Compose v2 (`docker compose`)
- Para construir las imágenes:
  - ZIP del release: `ServidorOXP_V<version>.zip` (generado por el CI)
  - Dump SQL de la BD base: `dump_libertya_core_v<version>.sql` (generado por el equipo de desarrollo)

---

## Construir las imágenes

### 1. Imagen de base de datos

El dump debe ser generado con `pg_dump` en formato texto plano desde la BD de referencia:

```sh
pg_dump -d <nombre_bd_release> -f dump_libertya_core_v26.05.sql
```

Luego construir la imagen:

```sh
cd docker/db
cp /ruta/al/dump_libertya_core_v26.05.sql .
docker build \
  --build-arg DUMP_FILE=dump_libertya_core_v26.05.sql \
  -t disytel/libertya-db:26.05 \
  .
```

### 2. Imagen de aplicación

```sh
cd docker/app
cp /ruta/al/ServidorOXP_V26.05.zip .
docker build \
  --build-arg ZIP_FILE=ServidorOXP_V26.05.zip \
  -t disytel/libertya:26.05 \
  .
```

> **Nota:** El build instala el JDK completo (no solo JRE) porque `ConfigurarAuto.sh`
> necesita `jarsigner` para firmar los JARs durante la configuración inicial.

---

## Publicar en Docker Hub

```sh
docker push disytel/libertya-db:26.05
docker push disytel/libertya:26.05

# Mover el tag "latest" al release actual
docker tag disytel/libertya-db:26.05 disytel/libertya-db:latest
docker tag disytel/libertya:26.05    disytel/libertya:latest
docker push disytel/libertya-db:latest
docker push disytel/libertya:latest
```

---

## Levantar con docker compose

### Uso local (cliente Java en la misma máquina)

```sh
cd docker
docker compose up -d
```

El servicio `app` espera a que `db` pase el healthcheck antes de arrancar.
La primera vez que se levanta con un volumen vacío, la BD tarda varios minutos
en inicializarse (restauración del dump). Las siguientes veces arranca en segundos.

Puertos expuestos al host:

| Puerto host | Servicio |
|---|---|
| `8080` | Web UI (zkwebui) |
| `8443` | Web UI HTTPS |
| `1099` | JNP/RMI (cliente Java/Swing) |
| `5439` | PostgreSQL (acceso directo a la BD) |

Conectar el cliente Java/Swing a `localhost:1099`.

### Uso en servidor remoto

Si el servidor corre en otra máquina, hay que cambiar dos variables en `docker-compose.yml`:

```yaml
environment:
  SERVIDOR_APPS_OXP: "192.168.1.100"   # IP o hostname del servidor, accesible desde el cliente
  SERVIDOR_BD_OXP:   "192.168.1.100"   # ídem para la conexión directa a PostgreSQL
```

---

## Nota sobre puertos internos vs externos

El cliente Java/Swing corre **fuera** de Docker y se conecta usando los puertos
mapeados en el host. Por eso `SERVIDOR_BD_OXP` y `SERVIDOR_APPS_OXP` deben apuntar
al host (o IP del servidor), no al nombre interno del contenedor.

Las variables `DB_INTERNAL_HOST` y `DB_INTERNAL_PORT` existen para que el entrypoint
pueda esperar la disponibilidad de la BD usando la red interna de Docker,
independientemente de lo que diga `SERVIDOR_BD_OXP`:

```
Cliente Java/Swing (fuera de Docker)
       │  localhost:1099 (JNP)
       │  localhost:5439 (PostgreSQL)
       ▼
 ┌─────────────────────────────────┐
 │  Host                           │
 │  puerto 5439 ──► db:5432        │
 │  puerto 1099 ──► app:1099       │
 │                                 │
 │  contenedor app                 │
 │    wait usa db:5432 (interno)   │
 │    LibertyaEnv usa localhost    │
 └─────────────────────────────────┘
```

---

## Variables de entorno — imagen `app`

| Variable | Default | Descripción |
|---|---|---|
| `SERVIDOR_BD_OXP` | `localhost` | Host de PostgreSQL visto desde el cliente externo |
| `PUERTO_BD_OXP` | `5439` | Puerto de PostgreSQL visto desde el cliente externo |
| `NOMBRE_BD_OXP` | `libertya` | Nombre de la base de datos |
| `USUARIO_BD_OXP` | `libertya` | Usuario de BD |
| `PASSWD_BD_OXP` | `libertya` | Contraseña de BD |
| `SYSTEM_BD_OXP` | `libertya` | Contraseña del superusuario postgres (uso interno) |
| `DB_INTERNAL_HOST` | `db` | Host interno Docker para el wait de disponibilidad |
| `DB_INTERNAL_PORT` | `5432` | Puerto interno Docker para el wait de disponibilidad |
| `SERVIDOR_APPS_OXP` | `localhost` | Hostname del servidor de apps (usado por RMI) |
| `PUERTO_WEB_OXP` | `8080` | Puerto HTTP |
| `PUERTO_SSL_OXP` | `8443` | Puerto HTTPS |
| `PUERTO_JNP_OXP` | `1099` | Puerto JNP/RMI |
| `KEYSTOREPASS_OXP` | `libertya` | Contraseña del keystore SSL |
| `OPCIONES_JAVA_OXP` | `-Xms512M -Xmx1024M ...` | Opciones JVM |
| `WAIT_FOR_DB` | `true` | Esperar disponibilidad de PostgreSQL al arrancar |
| `WAIT_FOR_DB_TIMEOUT` | `60` | Segundos máximos de espera por la BD |

## Variables de entorno — imagen `db`

| Variable | Default | Descripción |
|---|---|---|
| `POSTGRES_USER` | `postgres` | Superusuario de PostgreSQL |
| `POSTGRES_PASSWORD` | `postgres` | Contraseña del superusuario |
| `LIBERTYA_DB_NAME` | `libertya` | Nombre de la base de datos Libertya |
| `LIBERTYA_DB_USER` | `libertya` | Usuario propietario de la BD |
| `LIBERTYA_DB_PASS` | `libertya` | Contraseña del usuario Libertya |
