# ZK WebUI Modern Theme

Este entregable agrega un theme alternativo `modern` para `zkwebui` sin modificar el theme actual `default`.

## Archivos

- `zkwebui/theme/modern/css/theme.css.dsp`
- `zkwebui/theme/modern/css/themeie.css.dsp`
- `zkwebui/theme/modern/login-bottom.zul`
- `zkwebui/theme/modern/login-info.zul`
- `zkwebui/theme/modern/login-links.zul`
- `zkwebui/theme/modern/vendor-logo.zul`
- `zkwebui/theme/modern/version-info.zul`

## Activacion

El theme activo se resuelve por `MSysConfig` usando la clave `ZK_THEME`.

En base de datos:

```sql
UPDATE AD_SysConfig
SET Value = 'modern'
WHERE Name = 'ZK_THEME';
```

Si no existe:

```sql
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
VALUES (<nuevo_id>, 0, 0, 'Y', NOW(), 100, NOW(), 100, 'ZK_THEME', 'modern', 'Theme activo del cliente web ZK', 'D');
```

## Build

Desde el repo:

```powershell
cd zkwebui
ant war
```

Eso genera:

- `zkwebui/dist/webui.war`
- `lib/webui.war`

## Build completo de Libertya

Si compilás con `utils_dev/Compilar.bat`, el error final del ZIP no implica que la compilacion del codigo haya fallado.

El problema está en `install/build.xml`, que arma el ZIP final usando la variable de entorno `INSTALACION_EXPORT`. En Windows, `utils_dev/VariablesCompilacion.bat` no la define por defecto.

El fallo típico es este:

```text
Problem creating zip: ...\install\${env.INSTALACION_EXPORT}\ServidorOXP_V25.0.zip
```

La causa es que `INSTALACION_EXPORT` no existe o apunta a una ruta inexistente.

Solucion minima antes de compilar:

```powershell
$env:INSTALACION_EXPORT = 'C:\LibertyaExport'
New-Item -ItemType Directory -Force -Path $env:INSTALACION_EXPORT
cd C:\Users\ignac\git\libertya\utils_dev
.\Compilar.bat
```

Alternativamente, podés agregar estas líneas a tu `utils_dev/VariablesCompilacion.bat` local:

```bat
@SET INSTALACION_EXPORT=C:\LibertyaExport
@IF NOT EXIST %INSTALACION_EXPORT% MKDIR %INSTALACION_EXPORT%
```

Aunque el ZIP falle, los artefactos compilados suelen quedar generados igual en:

- `install/compilacion/ServidorOXP`
- `install/compilacion/ServidorOXP/lib/webui.war`

## Deploy en servidor Tomcat de pruebas

Si el servidor de pruebas usa una instalacion `ServidorOXP` con Tomcat:

1. Hacer backup del `webui.war` actual en `<OXP_HOME>\tomcat\webapps`.
2. Copiar el `webui.war` nuevo a `<OXP_HOME>\tomcat\webapps\webui.war`.
3. Eliminar la carpeta expandida `<OXP_HOME>\tomcat\webapps\webui` si existe.
4. Reiniciar Tomcat.

Comandos ejemplo:

```powershell
Copy-Item C:\ruta\servidor\tomcat\webapps\webui.war C:\ruta\servidor\tomcat\webapps\webui.war.bak
Copy-Item C:\Users\ignac\git\libertya\zkwebui\dist\webui.war C:\ruta\servidor\tomcat\webapps\webui.war -Force
Remove-Item C:\ruta\servidor\tomcat\webapps\webui -Recurse -Force -ErrorAction SilentlyContinue
```

## Deploy en servidor JBoss viejo

Para JBoss no conviene copiar directamente `zkwebui/dist/webui.war`.

Ese WAR es el artefacto base de `zkwebui`, pero Libertya después lo reprocesa en `install/ServidorOXP/build.xml` para el despliegue real:

- recompone `webui.war`
- le agrega jars de Libertya necesarios para ese runtime
- copia `webui.war` al `deploy` del servidor
- para JBoss además despliega `OXPRoot.ear`

Los targets relevantes son:

- `setupDeploy`: actualiza y copia `lib/webui.war`
- `setupDeployJBoss`: copia `lib/OXPRoot.ear`

### Flujo recomendado para JBoss

1. Compilar el repo.
2. Ir a `install/compilacion/ServidorOXP`.
3. Crear `LibertyaEnv.properties` a partir de `LibertyaEnvTemplate.properties`.
4. Configurar al menos:
   `OXP_HOME`, `JAVA_HOME`, `TIPO_APPS_OXP=jboss`, `DEPLOY_APPS_OXP=<ruta_jboss>\server\openXpertya\deploy`.
5. Ejecutar `ConfigurarAuto.bat` desde esa carpeta.
6. Eso corre el target `setup` y deja copiados los artefactos al `deploy` del JBoss configurado.

Ejemplo:

```powershell
cd C:\Users\ignac\git\libertya\install\compilacion\ServidorOXP
Copy-Item .\LibertyaEnvTemplate.properties .\LibertyaEnv.properties
notepad .\LibertyaEnv.properties
.\ConfigurarAuto.bat
```

### Qué archivos se actualizan en JBoss

Para el cliente web ZK, el artefacto principal es:

- `webui.war`

Para el backend web asociado, el deploy estándar de Libertya además actualiza:

- `OXPRoot.ear`

Si tus cambios están limitados a `zkwebui` y no tocaste módulos de backend, normalmente alcanza con redeployar `webui.war`. Pero en este trabajo ya hubo cambios Java dentro de `zkwebui`, así que no alcanza con copiar solo CSS.

### Iteración rápida para tema visual

Si el objetivo es solo probar CSS/ZUL y tu JBoss ya tiene un `webui.war` funcionando, tenés dos opciones:

1. Reemplazar el `webui.war` procesado que queda en `install/compilacion/ServidorOXP/lib/webui.war` despues de correr `ConfigurarAuto.bat`.
2. Si el despliegue está expandido, actualizar solo los archivos del theme dentro del despliegue expandido.

Para pruebas repetidas, la opcion 1 es la más segura.

### Reemplazo manual en JBoss

Secuencia segura:

```powershell
Stop-Service <servicio_jboss>
Copy-Item C:\ruta\jboss\server\openXpertya\deploy\webui.war C:\ruta\jboss\server\openXpertya\deploy\webui.war.bak
Copy-Item C:\Users\ignac\git\libertya\install\compilacion\ServidorOXP\lib\webui.war C:\ruta\jboss\server\openXpertya\deploy\webui.war -Force
Start-Service <servicio_jboss>
```

Si querés seguir el flujo completo de Libertya, además actualizá:

```powershell
Copy-Item C:\Users\ignac\git\libertya\install\compilacion\ServidorOXP\lib\OXPRoot.ear C:\ruta\jboss\server\openXpertya\deploy\OXPRoot.ear -Force
```

En JBoss viejo conviene detener el servidor antes del reemplazo para evitar despliegues parciales o caché de clases.

## Validacion visual recomendada

Despues de activar `ZK_THEME=modern` validar al menos:

1. Login principal:
   `usuario`, `password`, `boton ingresar`, mensaje de error y selector de idioma.
2. Pantalla de seleccion de rol:
   `rol`, `cliente`, `organizacion`, `deposito`.
3. Shell principal:
   header, logo, acciones de usuario, panel de menu, buscador, tabs y toolbar superior.
4. Home/dashboard:
   cards, paneles y espaciados.
5. Ventana transaccional:
   toolbar, tabs laterales, grilla, inputs, combo, datebox, popup y status bar.

## Nota de alcance

Este paso solo aísla el rediseño en un theme nuevo y deja la base para iterar visualmente. No incluye upgrade de ZK.
