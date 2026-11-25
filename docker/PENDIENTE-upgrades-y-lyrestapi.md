# Pendiente: upgrades de versión y lyrestapi en Docker

> **Estado:** análisis del 2026-11-25. **No implementado.** Se postergó para avanzar
> con el deploy de la 26.05 en Docker. Este documento junta lo verificado y el
> diseño propuesto para retomarlo sin repetir el análisis.

Cubre dos temas:

1. [Cómo actualizar de versión una instalación Docker sin perder la base](#1-upgrades-de-versión).
2. [Cómo sumar lyrestapi a una instalación Docker](#2-lyrestapi).

---

## 1. Upgrades de versión

### El problema

Los binarios se actualizan cambiando el tag de `app` y haciendo `docker compose pull`.
La base no: la imagen `db` restaura su dump **solo cuando el volumen está vacío**,
así que cambiar su tag no actualiza nada. La metadata se actualiza con
`PluginInstaller` y los jars `upgrade_from_*` / patches, y hoy la imagen no tiene
una forma prevista de hacerlo.

### Lo que se verificó

| Tema | Resultado | Dónde |
|---|---|---|
| Contenido de los jars de upgrade | `org.libertya.core.upgrade_26.05_26.09.jar` y el patch `core_v26.09_c2819611_2819663_patch.jar` traen solo `manifest.properties`, `preinstall.sql`, `install.xml` y `postinstall.xml`. **No tocan el filesystem de `/ServidorOXP`**: el postinstall también escribe en la base (los `.jasper` van a `AD_JasperReport.BinaryData`) | [`PluginPostInstallProcess.updateBinaryData`](../base/src/org/openXpertya/process/PluginPostInstallProcess.java) |
| PluginInstaller por terminal | Corre sin interfaz (`utils/PluginInstaller.sh` → `CallClass.sh` → `PluginInstaller.main`). Parámetros: jar, commit con errores en install (Y/N), commit con errores en postinstall (Y/N) | [`PluginInstaller.java`](../client/Src/org/openXpertya/apps/form/PluginInstaller.java) |
| Atomicidad | Pre, install y post corren en **una sola transacción**. Con `N N`, si hay errores hace rollback. El DDL de PostgreSQL es transaccional, así que la base no queda a medias | [`VPluginInstallerUtils.performInstall` / `handleException`](../client/Src/org/openXpertya/apps/form/VPluginInstallerUtils.java) |
| Doble instalación | `validatePlugin` rechaza una versión no-patch ya instalada. En los patches, `doPreInstall` saltea los bloques de preinstall con timestamp anterior al último export instalado | `VPluginInstallerUtils.validatePlugin` / `doPreInstall` |
| Versión registrada en la base 26.05 | En `dump_libertya_26.05ar.sql` y `26.05i.sql`, la última versión de CORE en `AD_ComponentVersion` es **26.05** (id 1010409). La 26.09 (`CORE-AD_ComponentVersion-1010412`, la que crea el jar de upgrade) no está | dumps de `db/` |
| `AD_Plugin` en la base 26.05 | **No hay fila de CORE**; solo figuran L_AR 2.0 y LYEI 2.5. La detección de versión tiene que mirar `AD_ComponentVersion`, no `AD_Plugin` | dumps de `db/` |
| Log de instalación | Se escribe en la raíz de `OXP_HOME` (`Component_<prefix>_install_<fecha>.log`). En Docker queda fuera del volumen y se pierde al recrear el contenedor | `VPluginInstallerUtils.writeInstallLog` |
| Requisitos dentro del contenedor | `app` ya tiene Java, `OXP_HOME` y el `OpenXpertya.properties` que genera `ConfigurarAuto.sh` en el entrypoint. Un contenedor creado con `--entrypoint` distinto **no** tiene esa config | [`app/docker-entrypoint.sh`](app/docker-entrypoint.sh) |

Conclusión: los binarios viajan en la imagen y la metadata es un proceso contra la
base, sin pisarse entre sí. El problema se resuelve sin rediseñar nada.

### Opciones evaluadas

**A. Un Dockerfile por cada upgrade — descartada.** Mete una operación que se corre
una sola vez dentro de una imagen, multiplica imágenes y no aporta nada.

**B. Manual con `cp` + `exec` — funciona hoy, sin cambios.** Es el plan de
emergencia y sirve para aplicar un patch de metadata suelto:

```bash
docker compose exec -T db pg_dump -U postgres libertya > backup_pre_upgrade.sql
docker compose cp <jar> app:/tmp/
docker compose exec app bash -c 'cd /ServidorOXP/utils && ./PluginInstaller.sh /tmp/<jar> N N'
docker compose exec app bash -c 'cp /ServidorOXP/Component_*.log /ServidorOXP/data/'   # conservar el log
docker compose restart app   # el servidor cachea metadata
```

Limitaciones: corre con los binarios de la versión que está levantada y con JBoss
atendiendo usuarios. En un cambio de versión, el orden que se elija deja un rato
con metadata nueva y binarios viejos, o al revés.

**C. Upgrades dentro de la imagen nueva + modo `upgrade` en el entrypoint — recomendada.**
Es el patrón habitual: las migraciones viajan con el código que las necesita
(Nextcloud, GitLab, Odoo, Django).

### Diseño propuesto (opción C)

**Imagen (`app/Dockerfile`):** sumar `COPY upgrades/ ${OXP_HOME}/upgrades/`. La
carpeta `upgrades/` del contexto de build (no versionada, como `plugins/`) trae la
**cadena completa** desde la primera versión publicada en Docker: 26.05→26.09,
patches de 26.09, 26.09→26.10, etc. Pesan ~200 KB cada uno, y así se pueden
saltear versiones.

**Entrypoint (`app/docker-entrypoint.sh`):**

1. Los pasos 1 a 4 de hoy no cambian: esperar la base, generar `LibertyaEnv.properties`
   y el keystore, y correr `ConfigurarAuto.sh`.
2. Si `$1 = upgrade`, en lugar de arrancar JBoss:
   - Detectar la versión de CORE instalada con `AD_ComponentVersion` (ver la tabla
     de arriba: `AD_Plugin` no sirve en una base recién creada desde el dump).
   - Aplicar en orden los jars pendientes con `PluginInstaller.sh <jar> N N`. Para
     los patches, decidir si ya se aplicaron con `AD_Plugin.component_export_date` /
     `component_last_changelog`.
   - Hacer lo mismo con los jars de metadata de los plugins de la edición (LYEI en
     `-ar`) si traen upgrade propio.
   - Copiar los logs a `/ServidorOXP/data/upgrade-logs/`.
   - Terminar con un código de error si algo falla, sin arrancar JBoss.
3. Opcional: `AUTO_UPGRADE=true` para que lo haga solo al arrancar. **Por defecto
   apagado**: en producción conviene hacer el backup antes y leer el log.

**Sincronizar `data/`:** el volumen `libertya_data` tapa lo que la imagen nueva trae
en `/ServidorOXP/data`. En la 26.05 eso es `afip-openssl.cnf`, `import/PLAN_CTA_*`,
`sqlj.sql` y los PDF de LYEI. El modo `upgrade` tiene que copiar al volumen los
archivos del release que hayan cambiado, sin tocar los datos del usuario (por
ejemplo, los certificados de ARCA en `data/facturaElectronica`).

**Procedimiento para el usuario:**

```bash
docker compose exec -T db pg_dump -U postgres libertya > backup_pre_<ver>.sql
# en el compose, cambiar SOLO el tag de app (y el de lyrestapi si se usa)
docker compose pull app
docker compose stop app
docker compose run --rm app upgrade
docker compose up -d
```

`docker compose run app upgrade` le pasa `upgrade` como argumento al `ENTRYPOINT`.
Los compose actuales ya alcanzan: una instalación 26.05 se puede actualizar con la
imagen 26.09 sin rehacer su compose.

### Otros cambios necesarios

- **El tag de `db` no se cambia en un upgrade.** Después del primer arranque, la
  imagen `db` es solo un `postgres:16`. Si un `libertya-db-*` futuro se construye
  sobre otra versión mayor de PostgreSQL, cambiarle el tag deja la base sin arrancar
  (archivos de datos incompatibles). Hay que corregir `DOCKERHUB.md`, que hoy dice
  que las dos imágenes tienen que tener la misma versión: eso vale solo para
  instalaciones nuevas.
- **Keystore en un volumen.** Hoy se genera en `/ServidorOXP/keystore`, fuera de los
  volúmenes, y se regenera cada vez que se recrea el contenedor. Con el autofirmado
  no pasa nada; con un certificado comprado se pierde. Opciones: volumen propio o
  moverlo a `data/`.
- **Imágenes de hotfix.** Entre versiones van a salir fixes de código Java que
  requieren binarios nuevos (por ejemplo `26.05.1`). Hoy armar una imagen es manual
  (ZIP + `docker build` + `push`). Hay que definir quién la arma y en cuánto tiempo.
- **Docs:** actualizar `DOCKERHUB.md` (procedimiento de upgrade, regla del tag de
  `db`) y la sección *Pendiente* de [`README.md`](README.md).

### Prueba antes de implementar

1. Levantar una 26.05 de prueba con los compose de `compose/` y cargar datos.
2. Aplicarle el jar 26.05→26.09 y el patch con la opción B.
3. Corre con los binarios 26.05, pero sirve para confirmar que la metadata entra
   limpia sobre la base de Docker, que es la parte de más riesgo.

Los jars de ese ensayo están como copia local en `docs/` del repo, que no se versiona.

---

## 2. lyrestapi

[lyrestapi](https://github.com/Disytel-Consulting-SA/lyrestapi) es una aplicación
**Spring Boot 2.7 / Java 8** que se conecta directo a PostgreSQL. No se instala
dentro de JBoss. Ya tiene `Dockerfile`, `DockerCreateImage.sh` y
`DockerRunContainer.sh`, y se configura por variables de entorno (`DB_HOST`,
`DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS`, `CLIENT_ID`, `SERVER_PORT`).

Las dos formas son viables con el Libertya dockerizado.

### Opción A: servicio en el compose (recomendada)

Agregarlo como servicio opcional con un `profile`, para que solo se levante si se
pide (`docker compose --profile api up -d`):

```yaml
  lyrestapi:
    image: disytel/lyrestapi:26.05   # a publicar; hoy se arma local con DockerCreateImage.sh
    profiles: ["api"]
    restart: unless-stopped
    network_mode: host
    depends_on:
      db:
        condition: service_healthy
    environment:
      DB_HOST: localhost
      DB_PORT: "5439"
      DB_NAME: libertya
      DB_USER: libertya
      DB_PASS: *db_password
      CLIENT_ID: "1010016"           # AD_Client_ID de la compañía
      SERVER_PORT: "8090"            # NO 8080: lo usa el web de Libertya
```

### Opción B: `java -jar` en el mismo servidor

Con `network_mode: host`, la base de Docker escucha en el 5439 del servidor:

```bash
DB_HOST=localhost DB_PORT=5439 DB_NAME=libertya DB_USER=libertya DB_PASS=<pass> \
CLIENT_ID=<AD_Client_ID> SERVER_PORT=8090 \
java -Dloader.path=libs/JasperReports-ngroovy.jar -jar lyrestapi-1.0.0.jar
```

Requiere Java 8 instalado en el servidor (el de la imagen de Libertya no se puede
usar desde afuera) y un unit de systemd para el arranque automático. Se pueden
tomar como modelo los de `libertyad` en [`utils/linux`](../utils/linux).

### Puntos a resolver en cualquiera de las dos

1. **Puerto:** por defecto usa el 8080, igual que el web de Libertya. Con red de
   host chocan.
2. **Versión acoplada:** el fat jar trae adentro el `OXP.jar`/`OXPXLib.jar` contra el
   que se compiló. La imagen de lyrestapi tiene que armarse con la **misma versión**
   que `app` y actualizarse junto con ella en cada upgrade (mismo tag). Ver
   `docs/compatibilidad-core.md` en el repo de lyrestapi.
3. **Plugins (edición `-ar`):** el jar de LYEI vive dentro de la imagen `app`
   (`/ServidorOXP/lib/plugins`). Según la doc de lyrestapi (§6.6 de
   `compatibilidad-core.md`), lo que no está en `loader.path` no se ejecuta, y no da
   ningún aviso. **No verificado:** si se crean facturas por la API y tienen que
   pedir CAE, probablemente haga falta sumar el plugin al `loader.path`
   (`-Dloader.path=libs/JasperReports-ngroovy.jar,plugins/`). En la imagen de
   lyrestapi para `-ar` conviene incluir los mismos jars de `app/plugins/`.
4. **Imagen base:** el `Dockerfile` usa `openjdk:8-jdk-alpine`, que ya no recibe
   actualizaciones. Para producción, pasar a `eclipse-temurin:8-jre`.
5. **Publicación:** no hay imagen en Docker Hub. Para incluirlo en los compose de
   distribución habría que publicar `disytel/lyrestapi:<ver>` (o `-ar`/`-i` si los
   plugins lo requieren) en el mismo proceso que las imágenes de Libertya.
