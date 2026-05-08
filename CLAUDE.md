# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What is this project

Libertya is a free, open-source ERP (Enterprise Resource Planning) system for Argentine businesses. It handles products/warehouses, customers/suppliers, sales/AR, purchasing/AP, treasury, accounting, POS terminals, AFIP electronic invoicing, and CRM/BI integration. The codebase is primarily in Spanish (variable names, comments, UI labels). Current version: 25.0.

## Build system

The build uses **Apache Ant** invoked via shell scripts. Java 11 (OpenJDK) is required.

**Full build:**
```sh
cd utils_dev && ./Compilar.sh
```

⚠️ **Mandatory operational rule for agents:**
- Never try to compile this project from the agent.
- Never run `Compilar.sh` (neither `utils_dev/Compilar.sh` nor module-level `Compilar.sh` scripts).
- Never run `ant` for build tasks in this repository.
- Compilation is performed manually by the user only.

This sources `VariablesCompilacion.sh` (sets `JAVA_HOME`, `OXP_HOME`, `JJ_PASSWORD`, keystore paths, classpath with Ant JARs) then runs `ant clean && ant complete`.

**Key environment variables** (override before building if needed):
- `JAVA_HOME` — path to JDK 11
- `OXP_HOME` — deployment target directory (default `/ServidorOXP`)
- `JJ_PASSWORD` — keystore password (default `openxp`)
- `ROOT_OXP` — root for install layout (default `/`)
- `INSTALACION_OXP` / `INSTALACION_EXPORT` — output install dirs (default `/install`)

**Per-module build:** each module (e.g. `base/`, `client/`) has its own `build.xml` and `Compilar.sh`.

## Running tests

Tests use JUnit. The `lyrestapi` component (a companion REST API, cloned during CI) uses Gradle:
```sh
./gradlew clean test --info
```

The main test sources are under `test/` and `base/src/test/`. There is no standalone test runner script for the core modules; tests run through the Jenkins CI pipeline (`Jenkinsfile`).

CI uses a PostgreSQL database on port `5434` (`libertya_test` / `libertya` / `libertya`).

## High-level architecture

Libertya is a **3-tier client–server ERP**:

### Core modules

| Module | Role |
|--------|------|
| `base/` | Core engine: model objects (`M*` classes), DB abstraction, business logic. Most of the domain lives here (`base/src/org/openXpertya/model/`). |
| `client/` | Desktop GUI (Java Swing). Also contains the POS terminal client. |
| `serverRoot/` | Server-side ERP engine that hosts the application tier. |
| `serverApps/` | Additional server-side application components. |
| `interfaces/` | API contracts / inter-module interfaces. |
| `extend/` | Plugin/extension framework. |
| `zkwebui/` | Web client built on the ZK framework. |
| `manufacturing/` | Manufacturing module. |
| `convert/` | Data conversion/migration utilities. |
| `looks/` | UI look-and-feel (CLooks). |
| `lib/` | Third-party JARs (PostgreSQL/Oracle/Sybase drivers, Jasper Reports, iText, ZXing, etc.). |
| `tools/` | Build-time tools (Ant JARs, JUnit 5, XDoclet). |
| `data/` | SQL upgrade/migration scripts organised by version (`upgrade_from_X.Y/`). |
| `db/` | Database initialisation scripts. |
| `jboss/` | JBoss/WildFly application server configuration. |

### Key patterns

- **Model classes** (`M*`): nearly every business entity has a corresponding `M*.java` in `base/src/org/openXpertya/model/`. These wrap DB rows and contain business logic.
- **Process classes**: batch/background operations live in `*Process` or `*Callout` classes throughout `base/` and `client/`.
- **DB layer**: `org.openXpertya.db` and `org.openXpertya.dbPort` provide the database abstraction; supports PostgreSQL, Oracle, and Sybase.
- **Plugin system**: `client/Src/org/openXpertya/plugin/` hosts pluggable UI/logic extensions.
- **Reports**: Jasper Reports integration; `.jasper` compiled report files are treated as binary artifacts and can appear in changelogs.

### Branches & deployment

- `dev` → auto-deployed to QA environment via Jenkins after successful build.
- `master` → production deployments (manual trigger or separate pipeline stage).

### Mandatory Git workflow

- It is mandatory to use the `openproject-git-pr-conventions` skill for all branch, commit, and pull request workflows.

The `data/core/upgrade_from_22.0/devinfo.properties` file is updated by CI to stamp build metadata (branch, commit hash) into the deployed instance.

## Available skills

Skills are located in `.agents/skills/` and symlinked into `.claude/skills/`. Use them via `/skill-name` or when the task matches their description. When new skills are added to `.agents/skills/`, they must also be referenced here.

| Skill | Cuándo usarlo |
|-------|---------------|
| `find-skills` | Buscar e instalar nuevas skills del ecosistema |
| `conventional-commit` | Al crear commits: genera mensajes con formato Conventional Commits (feat/fix/chore/etc.) |
| `requesting-code-review` | Antes de mergear ramas: solicita una revisión estructurada del código |
| `openproject-git-pr-conventions` | Para ramas/commits/PRs con OpenProject: base `dev`, naming `feature/op-<wp>` o `fix/op-<wp>`, PR a `dev` con `OP#<wp>` obligatorio |
| `postgresql-database-engineering` | Al trabajar con queries, índices, esquemas o performance en PostgreSQL |
| `database-migrations-sql-migrations` | Al crear scripts SQL de migración en `data/` para nuevas versiones |
