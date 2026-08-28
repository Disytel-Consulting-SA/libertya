---
name: exportplugin
description: Exportar componentes Libertya con org.openXpertya.plugin.install.ExportPlugin y devinfo.properties. Usar cuando el usuario pida generar un .jar de componente, exportar metadata, crear un patch por rango de AD_ChangeLog_ID, revisar un devinfo.properties de exportacion, o diagnosticar errores del PluginExporter.
---

# ExportPlugin

## Overview

Usar esta skill para generar jars de componentes Libertya mediante `ExportPlugin.java`, sin abrir Eclipse y sin compilar el proyecto.
El exportador se ejecuta desde el deploy local de Libertya, toma un `devinfo.properties` como descriptor, exporta metadata/preinstall/binarios/clases segun propiedades, y crea el `.jar` final.

## Fuentes Relevantes

- Wrapper Linux: `utils/PluginExporter.sh`
- Clase principal: `base/src/org/openXpertya/plugin/install/ExportPlugin.java`
- Descriptores existentes: `data/core/upgrade_from_*/devinfo.properties`
- Builders generados: `preinstall.sql`, `install.xml`, `postinstall.xml`, `manifest.properties`

## Reglas Operativas

- No ejecutar `Compilar.sh`, `ant` ni builds del repo para exportar metadata.
- Preferir una copia temporal del `devinfo.properties` antes que modificar el archivo versionado.
- Crear la copia temporal en el mismo directorio que el `devinfo.properties` original: algunas propiedades se resuelven relativas a ese directorio.
- Usar directorios de salida unicos en `/tmp` para evitar mezclar exportaciones anteriores.
- No cambiar `ExportAndDisableInvalidEntries` a `Y` sin pedir confirmacion; si ya esta activo en el descriptor, avisar que puede desactivar entradas inconsistentes de `AD_Changelog`.

## Prerequisitos

- Debe existir un deploy local usable, normalmente en `/ServidorOXP`.
- `OXP_HOME` puede apuntar a otro deploy; si no se define, `utils/PluginExporter.sh` usa `/ServidorOXP`.
- `java` debe poder ejecutar las clases del deploy.
- `jar` debe estar disponible en `PATH`, porque `ExportPlugin` crea el archivo con `jar -cf`.
- La base configurada en `devinfo.properties` debe estar accesible con `DBHost`, `DBPort`, `DBName`, `DBUser`, `DBPass`.

## Comando Base

Ejecutar:

```bash
OXP_HOME=/ServidorOXP ./utils/PluginExporter.sh /ruta/absoluta/o/relativa/devinfo.properties
```

Si el wrapper falla por classpath incompleto, por ejemplo con `NoClassDefFoundError`, usar el classpath completo del deploy:

```bash
OXP_HOME="${OXP_HOME:-/ServidorOXP}"
java -Dfile.encoding=UTF-8 \
  -classpath "$OXP_HOME/lib/*:$OXP_HOME/jboss/server/openXpertya/lib/*" \
  org.openXpertya.plugin.install.ExportPlugin \
  /ruta/al/devinfo.properties
```

## Propiedades Del devinfo

Conexion:

- `DBHost`, `DBPort`, `DBName`, `DBUser`, `DBPass`: conexion PostgreSQL de desarrollo.

Contenido:

- `IncludeComponentExport=Y`: exporta metadata a `preinstall.sql`, `install.xml`, `postinstall.xml` y `manifest.properties`.
- `IncludeClassesAndLibs=Y|N`: incluye clases compiladas y librerias; para LY Core normalmente es `N`.
- `IncludeReports=Y|N`: copia `CreateJarBinariesLocation` dentro de `binarios/` del jar.

Exportacion:

- `ExportComponentVersionID`: `AD_ComponentVersion_ID` a exportar.
- `ExportDirectory`: directorio de trabajo donde se generan los archivos antes del jar.
- `ExportDirectoryEmptyFirst=Y`: borra `ExportDirectory` antes de exportar.
- `ExportProcessID`: `AD_Process_ID` custom de post-install; vacio usa el proceso default.
- `ExportChangelogFromID`: changelog inicial inclusivo; `0` significa sin filtro inferior.
- `ExportChangelogToID`: changelog final inclusivo; `0` significa sin filtro superior y toma el ultimo.
- `ExportFromUserID`: limita por `AD_User_ID`; `0` significa sin filtro.
- `ExportAsPatch=Y|N`: setea `PATCH = Y` en el manifest cuando el jar complementa un release previo de la misma version.
- `ExportAndValidateConsistency=Y|N`: valida consistencia entre changelog y metadata actual.
- `ExportAndDisableInvalidEntries=Y|N`: desactiva entradas inconsistentes si la validacion las detecta.

Generacion del jar:

- `CreateJarTargetDir`: directorio donde queda el `.jar`.
- `CreateJarForceFileName`: nombre forzado; si esta vacio, se genera desde metadata.
- `CreateJarForcePackageName`: prefijo de paquete forzado para el nombre si corresponde.
- `CreateJarPreinstallFile`: archivo preinstall relativo al directorio del `devinfo.properties`; se copia como `preinstall.sql`.
- `CreateJarBinariesLocation`: directorio relativo para reportes/binarios; se copia como `binarios/`.
- `CreateJarClassesLocation`, `CreateJarLibsLocation`: ubicaciones relativas de clases/libs cuando `IncludeClassesAndLibs=Y`.
- `CreateJarSkipFiles`: lista separada por comas de archivos a excluir al copiar clases/libs/binarios.
- `PostBuildCopyJarToLocation`: copia opcional del jar final a otra ubicacion.
- `PostBuildExecuteScriptFile`, `PostBuildExecuteScriptDir`: script opcional a ejecutar al final.
- `ProjectVersionControl=git|svn`: usado para agregar revision al nombre del jar cuando se incluyen reportes o clases/libs.

## Exportar Un Release Completo

1. Localizar el descriptor correcto, por ejemplo `data/core/upgrade_from_26.05/devinfo.properties`.
2. Revisar conexion y `ExportComponentVersionID`.
3. Usar `ExportChangelogFromID=0`, `ExportChangelogToID=0` y `ExportAsPatch=N`.
4. Ejecutar `utils/PluginExporter.sh <devinfo.properties>`.
5. Informar el jar final indicado por la salida `Jar final generado en:`.

## Exportar Un Patch Por Changelog

Usar este flujo cuando un cliente ya instalo un jar intermedio de la misma version y necesita solo los cambios posteriores.

1. Tomar el ultimo changelog instalado desde el nombre del jar o desde el manifest del jar instalado. Ejemplo: `org.libertya.core_v26.08_c2819008_r157eef3.jar` indica `2819008`.
2. Calcular `ExportChangelogFromID = ultimo_instalado + 1`. Para `2819008`, usar `2819009`.
3. Crear una copia temporal del `devinfo.properties` en el mismo directorio y agregar overrides al final:

```properties
ExportAsPatch=Y
ExportChangelogFromID=2819009
ExportChangelogToID=0
ExportDirectory=/tmp/core26.08_patch_2819009_export
ExportDirectoryEmptyFirst=Y
CreateJarTargetDir=/tmp/core26.08_patch_2819009_release
CreateJarForceFileName=
```

4. Ejecutar el exportador con esa copia temporal.
5. El manifest del jar debe mostrar `PATCH = Y`, `FIRST_CHANGELOG = <desde>` y `LAST_CHANGELOG = <ultimo exportado>`.

## Consultas Utiles

Ver componente/version:

```bash
PGPASSWORD=<password> psql -h <host> -p <port> -U <user> -d <db> -Atc \
"select cv.ad_componentversion_id, cv.name, cv.version, cv.currentdevelopment, c.prefix, c.packagename
 from ad_componentversion cv
 join ad_component c on c.ad_component_id = cv.ad_component_id
 where cv.ad_componentversion_id = <id>;"
```

Ver rango disponible:

```bash
PGPASSWORD=<password> psql -h <host> -p <port> -U <user> -d <db> -Atc \
"select min(ad_changelog_id), max(ad_changelog_id), count(*)
 from ad_changelog
 where ad_componentversion_id = <id>
   and ad_changelog_id >= <desde>;"
```

Verificar jar:

```bash
jar tf <archivo.jar> | sort
unzip -p <archivo.jar> manifest.properties
```

## Comportamiento Importante

- Antes de exportar, `ExportPlugin` desactiva temporalmente `currentdevelopment` del `AD_ComponentVersion_ID` si estaba en `Y`, y lo restaura en `finally`.
- Si el proceso aborta de forma externa, verificar manualmente que `currentdevelopment` haya vuelto al valor esperado.
- El rango de changelog es inclusivo: `ExportChangelogFromID` usa `>=` y `ExportChangelogToID` usa `<=`.
- `ExportChangelogToID=0` se convierte en sin limite superior para la exportacion, pero el nombre del jar usa el mayor `AD_ChangeLog_ID` del componente.
- El jar se crea desde el contenido de `ExportDirectory`; si `CreateJarTargetDir` es distinto, se mueve alli.
- El nombre default es `<package>_v<version>_c<changelog>[_r<revision>].jar`. Para componente `CORE`, el package default del nombre es `org.libertya.core`.
- El sufijo `_r<revision>` se agrega cuando `IncludeClassesAndLibs=Y` o `IncludeReports=Y`.
- Un warning `Directorio binarios omitido (no encontrado)` no necesariamente invalida el jar si no hay binarios/reportes para esa version.

## Cierre Esperado

Al terminar, reportar:

- Path completo del jar.
- Rango exportado segun `manifest.properties`.
- Si el manifest tiene `PATCH = Y` o no.
- Cualquier warning relevante.
- Si se pudo verificar que `currentdevelopment` quedo restaurado.
