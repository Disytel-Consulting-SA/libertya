# AGENTS.md

Esta guia define como debe trabajar Codex (y agentes compatibles) en este repositorio.

## Que es este proyecto

Libertya es un ERP libre y open source para empresas argentinas. Cubre productos/depositos, clientes/proveedores, ventas/cuentas corrientes, compras/pagos, tesoreria, contabilidad, POS, factura electronica AFIP e integracion CRM/BI. El codigo esta mayormente en espanol (nombres de variables, comentarios y etiquetas de UI). Version actual: 25.0.

## Build system

El build usa **Apache Ant** invocado por scripts de shell. Se requiere Java 11 (OpenJDK).

**Build completo:**
```sh
cd utils_dev && ./Compilar.sh
```

⚠️ **Regla operativa para agentes (obligatoria):**
- Nunca intentar compilar el proyecto desde el agente.
- Nunca ejecutar `Compilar.sh` (ni el de `utils_dev/` ni los de cada modulo).
- Nunca ejecutar `ant` para build en este repositorio.
- La compilacion la realiza exclusivamente el usuario de forma manual.

`Compilar.sh` sourcea `VariablesCompilacion.sh` (define `JAVA_HOME`, `OXP_HOME`, `JJ_PASSWORD`, rutas de keystore, classpath con JARs de Ant) y ejecuta `ant clean && ant complete`.

**Variables de entorno clave** (se pueden overridear antes de compilar):
- `JAVA_HOME` - path al JDK 11
- `OXP_HOME` - destino de despliegue (default `/ServidorOXP`)
- `JJ_PASSWORD` - password de keystore (default `openxp`)
- `ROOT_OXP` - raiz para layout de instalacion (default `/`)
- `INSTALACION_OXP` / `INSTALACION_EXPORT` - directorios de salida de instalacion (default `/install`)

**Build por modulo:** cada modulo (por ejemplo, `base/`, `client/`) tiene su propio `build.xml` y `Compilar.sh`.

## Running tests

Los tests usan JUnit. El componente `lyrestapi` (REST API complementaria, clonada durante CI) usa Gradle:
```sh
./gradlew clean test --info
```

Las fuentes de tests principales estan en `test/` y `base/src/test/`. No hay un runner standalone para core modules; los tests se ejecutan via Jenkins (`Jenkinsfile`).

CI usa PostgreSQL en puerto `5434` (`libertya_test` / `libertya` / `libertya`).

## Arquitectura de alto nivel

Libertya implementa una arquitectura ERP **3-tier client-server**.

### Modulos principales

| Modulo | Rol |
|--------|-----|
| `base/` | Motor core: objetos de modelo (`M*`), abstraccion de DB y logica de negocio. La mayor parte del dominio vive en `base/src/org/openXpertya/model/`. |
| `client/` | GUI de escritorio (Java Swing), incluyendo cliente POS. |
| `serverRoot/` | Motor ERP server-side que hospeda la capa de aplicacion. |
| `serverApps/` | Componentes adicionales de aplicacion server-side. |
| `interfaces/` | Contratos API / interfaces entre modulos. |
| `extend/` | Framework de plugins/extensiones. |
| `zkwebui/` | Cliente web sobre framework ZK. |
| `manufacturing/` | Modulo de manufactura. |
| `convert/` | Utilidades de conversion/migracion de datos. |
| `looks/` | Look-and-feel UI (CLooks). |
| `lib/` | JARs de terceros (PostgreSQL/Oracle/Sybase drivers, Jasper Reports, iText, ZXing, etc.). |
| `tools/` | Herramientas de build (Ant JARs, JUnit 5, XDoclet). |
| `data/` | Scripts SQL de upgrade/migracion por version (`upgrade_from_X.Y/`). |
| `db/` | Scripts de inicializacion de base de datos. |
| `jboss/` | Configuracion de application server JBoss/WildFly. |

### Patrones clave

- **Model classes** (`M*`): casi cada entidad de negocio tiene una clase `M*.java` en `base/src/org/openXpertya/model/`.
- **Process classes**: operaciones batch/background viven en clases `*Process` o `*Callout` en `base/` y `client/`.
- **DB layer**: `org.openXpertya.db` y `org.openXpertya.dbPort` abstraen DB para PostgreSQL, Oracle y Sybase.
- **Plugin system**: `client/Src/org/openXpertya/plugin/` concentra extensiones pluggables de UI/logica.
- **Reports**: integracion Jasper Reports; archivos `.jasper` compilados son artefactos binarios y pueden aparecer en changelogs.

### Branches y despliegue

- `dev` -> despliegue automatico a QA via Jenkins al pasar build.
- `master` -> despliegues productivos (manual trigger o stage aparte).

### Flujo Git obligatorio

- Es obligatorio usar la skill `openproject-git-pr-conventions` para todo flujo de ramas, commits y pull requests.

`data/core/upgrade_from_22.0/devinfo.properties` se actualiza en CI para inyectar metadata de build (branch, commit hash) en la instancia desplegada.

## Uso de subagentes (Codex)

Si el entorno de Codex tiene subagentes habilitados, usarlos para paralelizar trabajo no bloqueante y acotar contexto:

- `explorer`: preguntas puntuales de codebase, ubicacion de codigo, impacto y lectura rapida.
- `worker`: implementaciones acotadas con ownership claro de archivos/modulos.
- `default`: tareas generales cuando no aplica otro rol.

Reglas operativas:
- Definir ownership explicito por archivo/modulo antes de delegar.
- Evitar solapamiento de escritura entre subagentes.
- No revertir cambios de otros agentes o del usuario.
- Mantener trabajo local en paralelo mientras subagentes corren.
- Integrar resultados con validacion final (build/tests relevantes).

## Skills disponibles

Las skills estan en `.agents/skills/` (ademas pueden estar symlinkeadas en otras carpetas de tooling). Deben invocarse cuando el usuario las pida explicitamente o cuando la tarea matchee su descripcion.

| Skill | Cuando usarla |
|-------|---------------|
| `find-skills` | Buscar e instalar nuevas skills del ecosistema |
| `conventional-commit` | Al crear commits: generar mensajes con formato Conventional Commits (`feat`/`fix`/`chore`, etc.) |
| `requesting-code-review` | Antes de mergear ramas: pedir una revision estructurada del codigo |
| `openproject-git-pr-conventions` | Al crear ramas/commits/PRs con OpenProject: fuerza `dev` como base, naming `feature/op-<wp>` o `fix/op-<wp>`, y PR a `dev` con `OP#<wp>` obligatorio |
| `postgresql-database-engineering` | Trabajo con queries, indices, esquemas o performance en PostgreSQL |
| `database-migrations-sql-migrations` | Crear scripts SQL de migracion en `data/` para nuevas versiones |

## Mantenimiento de este archivo

`AGENTS.md` debe mantenerse sincronizado con las convenciones activas del repo.

Actualizar este archivo en el mismo cambio/PR cuando se detecten cambios importantes en:

- Flujo de build/test/CI (scripts, versiones de Java, runners, puertos/credenciales de entorno de test).
- Convenciones de branching/deploy.
- Estructura de modulos o paths relevantes.
- Politicas de code review/commits.
- Catalogo de skills en `.agents/skills/` (alta, baja, rename o cambio de proposito).

Chequeo rapido sugerido antes de cerrar una tarea:
```sh
find .agents/skills -maxdepth 2 -type f -name 'SKILL.md' | sort
```
Si el resultado no coincide con la tabla de skills de este archivo, actualizar `AGENTS.md`.
