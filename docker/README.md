# Docker — Libertya

Este README es para quien **construye y publica** las imágenes. La guía de
instalación para usuarios finales es la página de `disytel/libertya-i` en Docker
Hub, y su fuente es [`DOCKERHUB.md`](DOCKERHUB.md) (ver
[Documentación de Docker Hub](#documentación-de-docker-hub)).

Estructura del directorio:

```
docker/
├── .gitignore               ← excluye artefactos de build (ZIP, dump, JARs de plugins)
├── DOCKERHUB.md             ← guía de instalación (Overview de disytel/libertya-i)
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

## Levantar con docker compose (desde el repo)

```sh
# Internacional v26.05
docker compose -f docker/compose/docker-compose.26.05i.yml up -d

# LOC_AR v26.05
docker compose -f docker/compose/docker-compose.26.05ar.yml up -d
```

Los compose de `compose/` son los mismos que se publican en Docker Hub. La
configuración que normalmente se cambia está al principio, en la sección
`x-libertya`, y cada valor se escribe una sola vez (los dos servicios lo
referencian con anclas YAML):

| Variable | Default | Se usa en |
|---|---|---|
| `LIBERTYA_HOST` | `localhost` | `SERVIDOR_APPS_OXP` y `SERVIDOR_BD_OXP` (app) |
| `LIBERTYA_DB_PASSWORD` | `libertya` | `LIBERTYA_DB_PASS` (db) y `PASSWD_BD_OXP` (app) |
| `POSTGRES_PASSWORD` | `libertya` | `POSTGRES_PASSWORD` (db) y `SYSTEM_BD_OXP` (app) |

Se pueden cambiar editando el valor que sigue a `:-`, o con variables de entorno
o un `.env` junto al compose. Las contraseñas solo se aplican la primera vez,
cuando se inicializa el volumen de la base.

El servicio `app` espera a que `db` pase el healthcheck antes de arrancar. La
primera vez, con el volumen vacío, la BD tarda varios minutos en inicializarse
(restauración del dump). Las siguientes veces arranca en segundos. Los dos
servicios tienen `restart: unless-stopped`, así que vuelven a levantar solos
cuando se reinicia el servidor.

> El nombre del proyecto compose (y por lo tanto el de los volúmenes) sale de la
> carpeta donde está el archivo: levantando desde el repo queda `compose`; en una
> instalación siguiendo la guía de Docker Hub queda `libertya-erp`.

---

## Red

Los dos servicios usan `network_mode: host`: escuchan directamente en los puertos
del servidor, sin mapeo. Los motivos:

- **RMI de JBoss.** El cliente Swing se conecta a JBoss por RMI. Además del JNP
  (1099) usa 1098, 4444, 4445 y 8083, y los stubs RMI llevan la dirección de
  `java.rmi.server.hostname` (= `SERVIDOR_APPS_OXP`). Con red bridge habría que
  mapear todos esos puertos y cuidar que las direcciones coincidan.
- **El cliente Swing se conecta directo a PostgreSQL.** Al conectarse, le pide al
  servidor (EJB `Status`) host, puerto, usuario y contraseña de la base y abre su
  propia conexión JDBC (ver `CConnection.updateInfoFromServer`). Por eso:
  - `SERVIDOR_BD_OXP` tiene que ser una dirección a la que lleguen las PCs con
    cliente Swing (no `localhost` ni un nombre interno de Docker). En el compose
    es el mismo valor que `SERVIDOR_APPS_OXP` (`LIBERTYA_HOST`).
  - El puerto de la base (5439) tiene que quedar abierto a la red.

```
PC con cliente Swing                      Servidor (red host)
─────────────────────                     ──────────────────────────────────
                      RMI 1098/1099/4444/4445/8083
  cliente Swing  ───────────────────────►  app (JBoss)
        │                                    │ JDBC (SERVIDOR_BD_OXP:5439)
        │             JDBC 5439              ▼
        └─────────────────────────────────►  db (PostgreSQL)

Navegador ───── HTTP 8080 / HTTPS 8443 ───►  app (JBoss) → /webui
```

| Puerto | Servicio | Lo usa |
|---|---|---|
| `8080` | HTTP (interfaz web en `/webui`) | Navegador |
| `8443` | HTTPS | Navegador |
| `1099` | JNP (naming) | Cliente Swing |
| `1098`, `4444`, `4445`, `8083` | RMI naming, invokers JRMP y pooled, class loading | Cliente Swing |
| `5439` | PostgreSQL | App y cliente Swing |

### `SYSTEM_BD_OXP` y `POSTGRES_PASSWORD`

`ConfigurarAuto.sh` (SilentSetup) prueba conectarse como `postgres` con
`SYSTEM_BD_OXP` y aborta si no puede (`ConfigPostgreSQL.test`). Por eso tiene que
coincidir con el `POSTGRES_PASSWORD` de la imagen db; en el compose salen de la
misma variable.

Ojo al probar con `LIBERTYA_HOST=localhost`: el `pg_hba.conf` que genera la imagen
oficial de postgres deja `trust` para las conexiones desde `127.0.0.1`, así que
una contraseña incorrecta no se detecta. Recién con la IP del servidor PostgreSQL
valida la contraseña.

---

## Documentación de Docker Hub

La guía de instalación es una sola, para las dos ediciones: [`DOCKERHUB.md`](DOCKERHUB.md)
es el texto del **Overview** de `disytel/libertya-i` en Docker Hub. Se pega tal
cual: repo → pestaña *General* → *Repository overview* → *Edit*. El límite de
Docker Hub es de 25.000 caracteres.

Los otros tres repos (`disytel/libertya-ar`, `disytel/libertya-db-ar` y
`disytel/libertya-db-i`) tienen un texto corto que remite a `disytel/libertya-i`.
Ese texto se mantiene directamente en Docker Hub y no está en el repo.

La guía incluye el compose **entero** para copiar y pegar, con la edición LOC_AR
por defecto; el usuario elige la edición y la versión en las dos líneas `image:`.
Ese compose tiene que ser equivalente a los de `compose/` (el de `ar` tal cual, el
de `i` cambiando `-ar` por `-i` en las imágenes). Para verificarlo:

```sh
cd docker
# el compose es el único bloque ```yaml de la guía
awk '/^```yaml$/{f=1;next} /^```$/{if(f)exit} f' DOCKERHUB.md > /tmp/hub-ar.yml
sed 's#/libertya-db-ar:#/libertya-db-i:#; s#/libertya-ar:#/libertya-i:#' \
  /tmp/hub-ar.yml > /tmp/hub-i.yml
for fl in ar i; do
  diff <(docker compose -p x -f /tmp/hub-$fl.yml config) \
       <(docker compose -p x -f compose/docker-compose.26.05$fl.yml config) \
    && echo "$fl: OK"
done
```

### Publicar una versión nueva

1. Construir y publicar las cuatro imágenes con el tag nuevo y con `latest` (ver
   arriba).
2. Crear `compose/docker-compose.<ver>ar.yml` y `compose/docker-compose.<ver>i.yml`
   a partir de los anteriores: cambiar el tag en las dos líneas `image:` y la
   versión del encabezado.
3. En `DOCKERHUB.md`: actualizar el compose embebido (tag de las dos `image:` y
   versión del encabezado) y la versión por defecto de la tabla del paso 2.
   Correr la verificación de arriba con los compose nuevos.
4. Pegar `DOCKERHUB.md` en el Overview de `disytel/libertya-i`.

### Pendiente: actualizar instalaciones existentes

La imagen `db` solo restaura su dump cuando el volumen está vacío. Si en una
instalación en uso se cambia el tag, la base no se actualiza y el servidor nuevo
queda corriendo contra una base de la versión anterior. Hoy no hay en Docker un
equivalente a correr el PluginInstaller con los `upgrade_from_*`, por eso la guía
de Docker Hub indica no cambiar el tag de una instalación en uso.

---

## Variables de entorno — imagen `app`

Los defaults son los del `docker-entrypoint.sh` (el compose define los suyos).

| Variable | Default | Descripción |
|---|---|---|
| `SERVIDOR_BD_OXP` | `db` | Host de PostgreSQL. Lo usa la app y **también se le pasa al cliente Swing**: con red host, la IP del servidor |
| `PUERTO_BD_OXP` | `5432` | Puerto de PostgreSQL (el compose usa `5439`) |
| `NOMBRE_BD_OXP` | `libertya` | Nombre de la base de datos |
| `USUARIO_BD_OXP` | `libertya` | Usuario de BD |
| `PASSWD_BD_OXP` | `libertya` | Contraseña de BD. Tiene que coincidir con `LIBERTYA_DB_PASS` de la imagen db |
| `SYSTEM_BD_OXP` | `libertya` | Contraseña del superusuario `postgres`. Tiene que coincidir con `POSTGRES_PASSWORD` de la imagen db |
| `SERVIDOR_APPS_OXP` | `localhost` | Hostname/IP del servidor accesible desde el cliente Swing (`java.rmi.server.hostname`) |
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
| `POSTGRES_PASSWORD` | — (obligatoria) | Contraseña del superusuario (el compose usa `libertya`) |
| `LIBERTYA_DB_NAME` | `libertya` | Nombre de la base de datos Libertya |
| `LIBERTYA_DB_USER` | `libertya` | Usuario propietario de la BD |
| `LIBERTYA_DB_PASS` | `libertya` | Contraseña del usuario Libertya |
| `PGPORT` | `5432` | Puerto de PostgreSQL (el compose usa `5439`) |
