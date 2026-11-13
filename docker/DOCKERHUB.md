# Libertya ERP

**Libertya ERP** es un ERP libre y de código abierto: stock, clientes y
proveedores, ventas, compras, tesorería, contabilidad y puntos de venta. Se
instala con Docker Compose usando imágenes ya construidas, sin compilar nada.

## Ediciones

| Edición | Para quién | Imágenes |
|---|---|---|
| **LOC_AR** | Empresas argentinas (incluye factura electrónica ARCA) | `disytel/libertya-ar` + `disytel/libertya-db-ar` |
| **Internacional** | Sin localización argentina | `disytel/libertya-i` + `disytel/libertya-db-i` |

Cada edición usa dos imágenes: el servidor de aplicaciones y la base de datos
(PostgreSQL 16). Se levantan juntas con el `docker-compose.yml` de abajo.

## Requisitos

- Un **servidor Linux** con Docker Compose v2. Docker Desktop (Windows/macOS) no
  sirve: los contenedores usan la red del servidor.
- 4 GB de RAM y ~5 GB de disco libres.
- Libres los puertos 8080, 8443, 1098, 1099, 4444, 4445, 8083 y 5439.

## Instalación

### 1. Creá la carpeta y el `docker-compose.yml`

```bash
mkdir libertya-erp && cd libertya-erp
```

Creá ahí un archivo `docker-compose.yml` (por ejemplo con `nano docker-compose.yml`)
y pegá este contenido:

```yaml
# Libertya ERP — versión 26.05

# Configuración: reemplazá lo que sigue a `:-`, o definí la variable en un
# archivo .env en esta misma carpeta (el .env tiene prioridad).
x-libertya:
  # IP o nombre del servidor, como lo ven las PCs con el cliente Swing.
  host: &host ${LIBERTYA_HOST:-localhost}
  # Contraseñas de la base. Se fijan en el PRIMER arranque.
  db-password: &db_password ${LIBERTYA_DB_PASSWORD:-libertya}        # usuario libertya
  postgres-password: &postgres_password ${POSTGRES_PASSWORD:-libertya} # superusuario postgres

services:

  db:
    # Edición (-ar Argentina / -i Internacional) y versión: iguales en las dos imágenes.
    image: disytel/libertya-db-ar:26.05
    restart: unless-stopped
    network_mode: host
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: *postgres_password
      LIBERTYA_DB_NAME: libertya
      LIBERTYA_DB_USER: libertya
      LIBERTYA_DB_PASS: *db_password
      PGPORT: "5439"
    volumes:
      - libertya_pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -p 5439"]
      interval: 5s
      timeout: 5s
      retries: 12

  app:
    image: disytel/libertya-ar:26.05
    restart: unless-stopped
    network_mode: host
    depends_on:
      db:
        condition: service_healthy
    environment:
      OXP_HOME: /ServidorOXP
      SERVIDOR_APPS_OXP: *host
      SERVIDOR_BD_OXP: *host   # el cliente Swing se conecta directo a la base
      PUERTO_BD_OXP: "5439"
      NOMBRE_BD_OXP: libertya
      USUARIO_BD_OXP: libertya
      PASSWD_BD_OXP: *db_password
      SYSTEM_BD_OXP: *postgres_password
      PUERTO_WEB_OXP: "8080"
      PUERTO_SSL_OXP: "8443"
      PUERTO_JNP_OXP: "1099"
      KEYSTOREPASS_OXP: libertya
      OPCIONES_JAVA_OXP: "-Xms512M -Xmx1024M -XX:MaxPermSize=512M -Dfile.encoding=UTF-8"
      WAIT_FOR_DB: "true"
      WAIT_FOR_DB_TIMEOUT: "60"
    volumes:
      - libertya_data:/ServidorOXP/data

volumes:
  libertya_pgdata:
  libertya_data:
```

### 2. Ajustá el compose antes del primer arranque

Antes de guardarlo, revisá estos valores. Es todo lo que se puede cambiar:

| Qué | Dónde | Por defecto |
|---|---|---|
| **Edición** | En las dos líneas `image:`: `-ar` (Argentina) o `-i` (Internacional) | `-ar` |
| **Versión** | En las dos líneas `image:`: el tag después de `:` | `26.05` (última estable) |
| **IP del servidor** | `LIBERTYA_HOST`. Hace falta para usar el cliente Swing desde otras PCs | `localhost` |
| **Contraseña de la base** | `LIBERTYA_DB_PASSWORD` | `libertya` |
| **Contraseña del superusuario** | `POSTGRES_PASSWORD` | `libertya` |
| **Puerto web** | `PUERTO_WEB_OXP` | `8080` |
| **Memoria del servidor** | El `-Xmx` de `OPCIONES_JAVA_OXP` | `1024M` |

Los valores de `x-libertya` (IP y contraseñas) se cambian reemplazando lo que
sigue a `:-`, por ejemplo `${LIBERTYA_HOST:-localhost}` →
`${LIBERTYA_HOST:-192.168.1.100}`. También se pueden poner en un archivo `.env`
en la misma carpeta (`LIBERTYA_HOST=192.168.1.100`).

Tené en cuenta:

- **Las dos imágenes tienen que tener la misma edición y versión.**
- **Cambiá las contraseñas.** La base queda accesible desde la red (puerto 5439,
  lo necesita el cliente Swing). Usá solo letras, números, `-`, `_` y `.`;
  `openssl rand -hex 16` genera una al azar.
- **Las contraseñas se fijan en el primer arranque.** Si después las cambiás solo
  en el compose, la app deja de conectar (ver "Cambiar las contraseñas" más
  abajo).
- **La edición y la versión son para una instalación nueva.** No las cambies en
  una instalación en uso ni uses el tag `latest`: la base no se actualiza sola.

### 3. Levantá Libertya

```bash
docker compose up -d
docker compose logs -f app
```

La primera vez tarda varios minutos (descarga ~1,3 GB y crea la base). Está
listo cuando el log muestra **`Started in`**. Con `Ctrl+C` salís del log sin
detener nada.

### 4. Ingresá

- **Web:** `http://IP_DEL_SERVIDOR:8080/webui`
- **Cliente Swing:** servidor = el valor de `LIBERTYA_HOST`, puerto `1099`.
- **Usuario inicial:** `AdminLibertya`, contraseña `AdminLibertya`. Cambiala
  después del primer ingreso.

## Firewall

Si el servidor tiene firewall (en AlmaLinux / Rocky / RHEL viene firewalld
activo), abrí los puertos:

```bash
sudo firewall-cmd --permanent --add-port={8080,8443}/tcp                       # web
sudo firewall-cmd --permanent --add-port={1098,1099,4444,4445,8083,5439}/tcp   # cliente Swing
sudo firewall-cmd --reload
```

## Comandos útiles

Siempre desde la carpeta `libertya-erp`:

```bash
docker compose ps             # estado
docker compose logs -f app    # logs del servidor
docker compose down           # detener (no borra datos)
docker compose up -d          # arrancar, o aplicar cambios del compose
docker compose down -v        # ⚠️ borra TODOS los datos (instalación limpia)
```

Libertya vuelve a arrancar solo cuando se reinicia el servidor.

**Backup:**

```bash
docker compose exec -T db pg_dump -U postgres libertya > backup.sql
```

**Restaurar un backup** (reemplaza todos los datos actuales):

```bash
docker compose stop app
docker compose exec -T db psql -U postgres -c 'DROP DATABASE libertya;' -c 'CREATE DATABASE libertya OWNER libertya;'
docker compose exec -T db psql -U postgres -d libertya < backup.sql
docker compose start app
```

**Cambiar las contraseñas** con Libertya ya instalado. Primero se cambian en la
base y después en el compose:

```bash
docker compose exec db psql -U postgres -c '\password libertya'
docker compose exec db psql -U postgres -c '\password postgres'
# poné las mismas contraseñas en el compose (o en el .env) y aplicá:
docker compose up -d
```

## Problemas frecuentes

- **El log dice `password authentication failed`:** se cambiaron las contraseñas
  en el compose después del primer arranque. Volvé a poner las anteriores o
  cambialas como se indica arriba.
- **El cliente Swing no conecta:** `LIBERTYA_HOST` tiene que ser la IP del
  servidor (no `localhost`) y el firewall tiene que dejar pasar sus puertos.
- **El puerto 8080 está ocupado:** cambiá `PUERTO_WEB_OXP`. Como los contenedores
  usan la red del servidor, no sirve agregar `ports:`.

## Soporte

**Libertya ERP** es un producto desarrollado por **Disytel**. Código fuente:
[github.com/Disytel-Consulting-SA/libertya](https://github.com/Disytel-Consulting-SA/libertya).
