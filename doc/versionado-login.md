# Versionado y visualizacion de version en login (Swing y Web)

## Objetivo

Documentar como se define y muestra la version de Libertya en runtime para:

- Login Swing
- Login Web (ZK)

Tambien documentar la convencion de versionado y como usar `BUILD_INFO.properties`
para builds `dev`, `release` y casos manuales especiales.

## Convencion de version

### Version de release

La version funcional de Libertya se define en el archivo:

- `base/src/org/openXpertya/VERSION`

Formato esperado:

- `YY.MM`
- Ejemplo: `26.05` (mayo 2026)

### Version visible en login

La version visible se construye en runtime con esta regla:

- Release: `26.05`
- Dev: `26.05-dev-<commit>`

Ejemplos:

- `Libertya Version 26.05`
- `Libertya Version 26.05-dev-1403ec19`

## Fuente de datos en runtime

La clase central es:

- `base/src/org/openXpertya/OpenXpertya.java`

### Orden de lectura de version release

`OpenXpertya` carga la version en este orden:

1. `${OXP_HOME}/VERSION` (archivo externo en instancia)
2. Recurso empaquetado `VERSION` dentro de `Base.jar`
3. Fallback interno (`25.0`) si no encuentra nada

Esto permite:

- mantener una version por defecto en el repo
- overridear por archivo externo en una instancia puntual

## BUILD_INFO.properties

### Ubicacion

- `${OXP_HOME}/BUILD_INFO.properties`

Si no existe en `${OXP_HOME}`, el runtime intenta fallback en este orden:

1. Path explícito por JVM: `-DBUILD_INFO_FILE=/ruta/al/BUILD_INFO.properties`
2. `${OXP_HOME}/BUILD_INFO.properties`
3. `${Ini.findOXPHome()}/BUILD_INFO.properties`
4. `${user.dir}/BUILD_INFO.properties`
5. `./BUILD_INFO.properties`

### Claves relevantes

- `branch`
- `channel`
- `commit`

Tambien pueden existir otras claves de trazabilidad, por ejemplo:

- `jenkins_build`
- `built_at`

### Reglas de interpretacion

La version visual `release` o `dev` se decide asi:

1. Si `channel=dev` -> se muestra sufijo `-dev` (y commit si existe)
2. Si `channel=release` -> se muestra version limpia (sin `-dev`)
3. Si `channel` no existe y `branch=dev` -> se muestra como dev
4. Si `channel` no existe y `branch` es distinto de `dev` -> release

Esto permite forzar manualmente un build limpio aunque venga de `dev`.

Si `commit` no esta presente y el build es `dev`, el resultado sera:

- `26.05-dev`

## Jenkins

En el pipeline se inyecta `BUILD_INFO.properties` en el zip final con:

- `branch=${env.BRANCH_NAME}`
- `channel=${env.BRANCH_NAME == 'dev' ? 'dev' : 'release'}`
- `commit=${env.LIBERTYA_COMMIT}`

Archivo:

- `Jenkinsfile`

## Que se muestra en cada login

### Swing

Archivo:

- `client/Src/org/openXpertya/apps/ALogin.java`

La etiqueta principal usa:

- `OpenXpertya.getDisplayVersionLabel()`

El tooltip usa:

- `OpenXpertya.getBuildVersionLabel()`

### Web

Archivo:

- `zkwebui/theme/default/version-info.zul`

Formato actual:

- `Libertya ${mainVersion} | DB ${dbVersion} | Build ${vendorVersion} | ${dbInfo}`

Donde:

- `mainVersion` sale de `OpenXpertya.getDisplayVersionLabel()`
- `vendorVersion` sale de `OpenXpertya.getBuildVersionLabel()`
- `dbVersion` y `dbInfo` se conservan como hasta ahora

## Diferencia entre "Version" y "Build"

- `Version`: sale de `VERSION` + regla `dev/release`
- `Build`: sale del `Implementation-Version` del manifest del jar (ejemplo: `25.0 20260429-1239`)

`OpenXpertya.getBuildVersionLabel()` limpia prefijos como `Versi\u00f3n ` o `Version ` para mostrar un valor compacto.

## Caso manual especial (sin sufijo dev)

Si se compila manualmente y se necesita mostrar version limpia, editar:

- `${OXP_HOME}/BUILD_INFO.properties`

Ejemplo:

```properties
branch=dev
channel=release
commit=1403ec19
```

Resultado visual:

- `Libertya Version 26.05`

Si se quisiera mostrar como dev:

```properties
branch=dev
channel=dev
commit=1403ec19
```

Resultado visual:

- `Libertya Version 26.05-dev-1403ec19`

Nota operativa:

- `BUILD_INFO.properties` se carga en memoria al iniciar la aplicacion.
- Si se edita el archivo, se debe reiniciar el servicio/instancia para ver el cambio.
- El parser normaliza BOM UTF-8 en claves/valores para evitar problemas de lectura.

## Checklist para nuevo release

1. Actualizar `base/src/org/openXpertya/VERSION` (ejemplo `26.06`)
2. Generar build con Jenkins
3. Verificar en login web/swing que la version visible sea la esperada
4. Verificar que el campo `Build` siga mostrando metadata de build
5. Verificar que DB y DB info se muestren correctamente
