# Docker — Libertya

Estructura del directorio:

```
docker/
├── .gitignore               ← excluye artefactos de build (ZIP, dump, JARs de plugins)
├── README.md
├── app/
│   ├── Dockerfile           ← imagen del servidor Libertya (JBoss)
│   ├── docker-entrypoint.sh ← genera LibertyaEnv.properties y arranca JBoss
│   └── plugins/             ← JARs extra a incluir en /ServidorOXP/lib/plugins/
│       └── .gitkeep         ← vacío = flavour i; con JARs = flavour ar u otros
├── db/
│   ├── Dockerfile           ← imagen PostgreSQL con BD base restaurada
│   └── init-libertya-db.sh  ← script de inicialización del volumen
└── compose/
    ├── docker-compose.26.05i.yml    ← Internacional v26.05
    ├── docker-compose.26.05ar.yml   ← LOC_AR v26.05
    └── ...                          ← un archivo por versión × flavour
```

Los artefactos de build (ZIP del release, dumps SQL, JARs de plugins) **no se versionan** en git.
Deben copiarse localmente antes de ejecutar `docker build` (ver instrucciones abajo).

---

## Convención de nombres

| Componente | Flavour Internacional | Flavour LOC_AR |
|---|---|---|
| Imagen app | `disytel/libertya-i:<ver>` | `disytel/libertya-ar:<ver>` |
| Imagen db | `disytel/libertya-db-i:<ver>` | `disytel/libertya-db-ar:<ver>` |
| Docker Compose | `docker-compose.<ver>i.yml` | `docker-compose.<ver>ar.yml` |
| Dump SQL | `dump_libertya_<ver>i.sql` | `dump_libertya_<ver>ar.sql` |

---

## Requisitos previos

- Docker Engine 24+ y Docker Compose v2 (`docker compose`)
- Para construir las imágenes:
  - ZIP del release: `ServidorOXP_V<version>.zip` (generado por el CI)
  - Dump SQL según flavour (generado por el equipo de desarrollo)
  - Para LOC_AR: JAR del plugin LYEI (ej. `org.libertya.locale.ar.electronicInvoice2.5final_7e01ac7.jar`)

---

## Construir las imágenes

### Flavour Internacional (`i`)

#### 1. Imagen de base de datos

```sh
cd docker/db
cp /ruta/al/dump_libertya_26.05i.sql .
docker build \
  --build-arg DUMP_FILE=dump_libertya_26.05i.sql \
  -t disytel/libertya-db-i:26.05 \
  -t disytel/libertya-db-i:latest \
  .
```

#### 2. Imagen de aplicación

La carpeta `app/plugins/` debe estar vacía (solo el `.gitkeep`).

```sh
cd docker/app
cp /ruta/al/ServidorOXP_V26.05.zip .
docker build \
  --build-arg ZIP_FILE=ServidorOXP_V26.05.zip \
  -t disytel/libertya-i:26.05 \
  -t disytel/libertya-i:latest \
  .
```

---

### Flavour LOC_AR (`ar`)

#### 1. Imagen de base de datos

```sh
cd docker/db
cp /ruta/al/dump_libertya_26.05ar.sql .
docker build \
  --build-arg DUMP_FILE=dump_libertya_26.05ar.sql \
  -t disytel/libertya-db-ar:26.05 \
  -t disytel/libertya-db-ar:latest \
  .
```

#### 2. Imagen de aplicación

Copiar el JAR del plugin LYEI en `app/plugins/` antes del build y limpiarlo después:

```sh
cd docker/app
cp /ruta/al/ServidorOXP_V26.05.zip .
cp /ruta/al/org.libertya.locale.ar.electronicInvoice2.5final_7e01ac7.jar plugins/
docker build \
  --build-arg ZIP_FILE=ServidorOXP_V26.05.zip \
  -t disytel/libertya-ar:26.05 \
  -t disytel/libertya-ar:latest \
  .
rm plugins/*.jar
```

> **Importante:** limpiar `plugins/` después del build AR para no contaminar un build `i` posterior.

> **Nota sobre el build:** el Dockerfile instala el JDK completo (no solo JRE) porque
> `ConfigurarAuto.sh` necesita `jarsigner` para firmar los JARs durante la configuración inicial.

---

## Publicar en Docker Hub

```sh
# Internacional
docker push disytel/libertya-db-i:26.05
docker push disytel/libertya-db-i:latest
docker push disytel/libertya-i:26.05
docker push disytel/libertya-i:latest

# LOC_AR
docker push disytel/libertya-db-ar:26.05
docker push disytel/libertya-db-ar:latest
docker push disytel/libertya-ar:26.05
docker push disytel/libertya-ar:latest
```

---

## Levantar con docker compose

```sh
# Internacional v26.05
docker compose -f docker/compose/docker-compose.26.05i.yml up -d

# LOC_AR v26.05
docker compose -f docker/compose/docker-compose.26.05ar.yml up -d
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

Si el servidor corre en otra máquina, cambiar estas variables en el compose correspondiente:

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
