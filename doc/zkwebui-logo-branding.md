# Logos de marca en zkwebui (theme `modern`)

Cómo configurar qué logo se muestra según si la instancia es **Libertya ERP** o **Libertya Next**. Aplica solo al theme `modern` (`ZK_THEME=modern`); no aplica a POS, que quedó fuera de alcance de este trabajo por ser una aplicación distinta.

## Los 3 logos y sus claves de configuración

Se resuelven en tiempo de ejecución vía `ThemeManager` (`zkwebui/WEB-INF/src/org/adempiere/webui/theme/ThemeManager.java`), que lee la tabla `AD_SysConfig`:

| Logo | Dónde se ve | Clave `AD_SysConfig` | Default si no está seteada |
|---|---|---|---|
| Grande | Login y selección de rol | `ZK_LOGO_LARGE` | `theme/modern/images/login-logo.png` (wordmark **ERP**) |
| Chico | Header/topbar ya logueado | `ZK_LOGO_SMALL` (fallback legado: `WEBUI_LOGOURL`) | `theme/modern/images/header-logo.png` (isotipo compartido) |
| Favicon | Ícono de la pestaña del navegador | `ZK_BROWSER_ICON` | `theme/modern/images/icon.png` (isotipo compartido) |

**Importante:** el logo chico y el favicon **no distinguen marca**. La guía de marca Libertya v2.0 define un único isotipo compartido para toda la familia de productos (ERP/Next/POS) — a ese tamaño (~34px en el header, 16px el favicon) el acento bicolor que diferencia cada wordmark no se lee con nitidez. Por diseño, solo el logo grande cambia entre ERP y Next.

## Alcance de la configuración

Estas claves se leen a **nivel System** (`AD_Client_ID = 0`), no por cliente/org dentro de la misma instalación. Esto encaja con el modelo habitual de Libertya, donde cada cliente que contrata Next corre su propia instancia/DB dedicada: se configura una vez por instalación y aplica a toda ella.

Si en algún momento una misma instalación necesitara mostrar Next para un `AD_Client_ID` y ERP para otro, este mecanismo no alcanza tal cual está — `ThemeManager` llama a `MSysConfig.getValue(clave, default)`, que fija `AD_Client_ID=0, AD_Org_ID=0`. Habría que pasar el cliente real a `ThemeManager` y usar `MSysConfig.getValue(clave, default, AD_Client_ID, AD_Org_ID)`.

## Configurar una instancia como Libertya ERP (default)

No hace falta hacer nada: si no existe (o está inactiva) la fila `ZK_LOGO_LARGE`, se usa el wordmark ERP por defecto.

Si existía una fila `ZK_LOGO_LARGE` apuntando a Next y hay que volver a ERP:

```sql
UPDATE AD_SysConfig SET IsActive = 'N' WHERE Name = 'ZK_LOGO_LARGE';
```

## Configurar una instancia como Libertya Next

```sql
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
VALUES (<nuevo_id>, 0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_LOGO_LARGE', '/theme/modern/images/login-logo-next.png',
    'Logo grande de login/rol - variante Libertya Next', 'D');
```

Si ya existe la fila (por ejemplo inactiva de una configuración previa), simplemente reactivarla y/o actualizar el `Value`:

```sql
UPDATE AD_SysConfig
SET Value = '/theme/modern/images/login-logo-next.png', IsActive = 'Y'
WHERE Name = 'ZK_LOGO_LARGE';
```

También se puede cargar desde la ventana **Configurador del Sistema** del ERP en lugar de SQL directo.

## Assets de origen

Los `.png` del theme salen del kit de marca oficial (`libertya_logos_kit`, entregado en `.zip` por diseño), sin recomprimir ni recortar:

| Archivo en `zkwebui/theme/modern/images/` | Origen en el kit |
|---|---|
| `login-logo.png` | `libertya_erp_original_transparente.png` |
| `login-logo-next.png` | `libertya_next_original_transparente.png` |
| `header-logo.png` | `libertya_isotipo_transparente.png` |
| `icon.png` | `libertya_isotipo_transparente.png`, redimensionado a 16×16 |

El kit también incluye variantes `dark`/`mono` de cada logo y un `libertya_pos_*` (wordmark POS) sin usar todavía, por si en el futuro se decide traer este mismo esquema de theme a la aplicación POS.
