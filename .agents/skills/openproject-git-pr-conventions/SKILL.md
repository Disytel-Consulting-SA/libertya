---
name: openproject-git-pr-conventions
description: Usar para flujo de ramas, commits y pull requests en Libertya con OpenProject. Fuerza ramas desde dev con formato feature/op-<op-work-package> o fix/op-<op-work-package>, PR solo hacia dev, y OP#<op-work-package> obligatorio en titulo y primera linea de descripcion del PR. Requiere confirmacion del usuario luego de probar antes de abrir PR.
---

# OpenProject Git/PR Conventions (Libertya)

## Cuando usar esta skill

Usar cuando el usuario pida:
- Crear rama de trabajo.
- Preparar o crear commits.
- Abrir pull request.
- Publicar cambios a GitHub.

## Reglas obligatorias

1. La rama base siempre es `dev`.
2. Las ramas nuevas solo pueden ser:
   - `feature/op-<op-work-package>`
   - `fix/op-<op-work-package>`
3. Si falta `op-work-package`, pedirlo al usuario antes de crear la rama.
4. El PR siempre debe apuntar a `dev`.
5. Nunca abrir PR a `master`.
6. El titulo del PR debe empezar con `OP#<op-work-package>`.
7. La descripcion del PR debe tener en la primera linea `OP#<op-work-package>`.
8. Despues de la primera linea, incluir un resumen muy breve de lo implementado.
9. Antes de abrir PR, pedir al usuario que pruebe la funcionalidad y esperar confirmacion explicita.
10. Sin confirmacion explicita de prueba, no abrir PR.

## Convenciones de commit

1. Mantener commits pequenos y con un unico objetivo funcional.
2. Usar formato Conventional Commits para el titulo del commit (`feat`, `fix`, `chore`, etc.).
3. Si se conoce el work package, incluir `OP#<op-work-package>` en el cuerpo o footer del commit.

## Datos minimos requeridos

Antes de crear rama o PR, confirmar:
- Tipo de rama: `feature` o `fix`.
- Codigo de OpenProject: `<op-work-package>`.
- Resumen breve para el PR.

## Flujo operativo

1. Verificar que la base sea `dev`.
2. Si falta `op-work-package` o tipo de rama, solicitarlo.
3. Crear rama con formato obligatorio.
4. Implementar cambios y preparar commit(s).
5. Pedir al usuario que pruebe la funcionalidad.
6. Esperar confirmacion del usuario.
7. Recien entonces hacer push y abrir PR a `dev`.

## Plantillas

### Nombre de rama

```text
feature/op-<op-work-package>
fix/op-<op-work-package>
```

### Titulo de PR

```text
OP#<op-work-package> <resumen breve>
```

### Descripcion de PR

```text
OP#<op-work-package>
<resumen muy breve de lo implementado>
```

## Checklist rapido

- [ ] Rama creada desde `dev`.
- [ ] Nombre de rama cumple formato.
- [ ] PR con base `dev`.
- [ ] PR no apunta a `master`.
- [ ] Titulo del PR empieza con `OP#<op-work-package>`.
- [ ] Primera linea de descripcion contiene `OP#<op-work-package>`.
- [ ] Usuario confirmo pruebas funcionales antes de abrir PR.
