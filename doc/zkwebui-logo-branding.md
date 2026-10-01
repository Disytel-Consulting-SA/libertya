# Logos, íconos y theme en zkwebui (theme `modern`)

Cómo configurar el theme, los logos y el ícono de la pestaña del navegador (favicon) de la versión web de Libertya, según si la instancia es **Libertya ERP** o **Libertya Next**. Aplica solo al theme `modern`; no aplica a POS, que es una aplicación distinta.

## Claves de configuración

Todo se resuelve en tiempo de ejecución vía `ThemeManager` (`zkwebui/WEB-INF/src/org/adempiere/webui/theme/ThemeManager.java`), que lee la tabla `AD_SysConfig`:

| Qué | Dónde se ve | Clave `AD_SysConfig` | Default si no está seteada |
|---|---|---|---|
| Theme | Toda la web | `ZK_THEME` | `default` (hay que setear `modern`) |
| Logo grande | Login y selección de rol | `ZK_LOGO_LARGE` | `theme/modern/images/login-logo.png` (wordmark **ERP**) |
| Logo chico | Header/topbar ya logueado | `ZK_LOGO_SMALL` (fallback legado: `WEBUI_LOGOURL`) | `theme/modern/images/header-logo.png` (isotipo compartido) |
| Favicon | Ícono de la pestaña del navegador | `ZK_BROWSER_ICON` | `theme/modern/images/icon.png` (isotipo compartido) |
| Título | Texto de la pestaña del navegador | `ZK_BROWSER_TITLE` | `Libertya` |

**Qué cambia entre ERP y Next:** solo el logo grande. El logo chico y el favicon usan el mismo isotipo para toda la familia de productos: la guía de marca Libertya v2.0 define un único isotipo compartido, porque a ese tamaño (~34px en el header, 16px el favicon) el acento bicolor que diferencia cada wordmark no se lee. Igualmente, ambos scripts de abajo setean las tres claves de forma explícita, así la configuración queda a la vista y se puede apuntar a otra imagen si en el futuro se define un ícono por marca.

**Ojo con `WEBUI_LOGOURL`:** es una clave legada que muchas bases traen activa apuntando a un logo viejo (por ejemplo `/images/AD10030.png`). Mientras no exista `ZK_LOGO_SMALL`, el header muestra esa imagen en lugar del isotipo. Los scripts de abajo crean `ZK_LOGO_SMALL`, que tiene prioridad, así que no hace falta tocar `WEBUI_LOGOURL`.

## Cómo se carga la configuración

- **No hay ventana en el ERP para `AD_SysConfig`**: la tabla existe pero no tiene pestaña ni entrada de menú. La configuración se carga por SQL, con los scripts de esta página.
- **Nivel System**: las claves se leen con `AD_Client_ID = 0` y `AD_Org_ID = 0`. Una instalación = una marca, lo que encaja con el modelo habitual donde cada cliente de Next corre su propia instancia/DB. Si alguna vez una misma instalación necesitara mostrar Next para un cliente y ERP para otro, habría que pasar el cliente real a `ThemeManager` y usar `MSysConfig.getValue(clave, default, AD_Client_ID, AD_Org_ID)`.
- **Hay que reiniciar el servidor de aplicaciones (JBoss)** después de correr cualquier script: `MSysConfig` guarda los valores en una caché en memoria sin vencimiento, así que el cambio no se ve hasta reiniciar.
- **El navegador cachea el favicon** por su cuenta: después de reiniciar, recargar con Ctrl+Shift+R o probar en una ventana privada.

Los scripts son idempotentes: si la clave ya existe (activa o no) la actualizan y la activan; si no existe, la crean. Los IDs se toman del rango System (< 1.000.000) y al final se ajusta `AD_Sequence` para que el próximo ID generado por el sistema no choque.

## Script: instancia Libertya ERP

```sql
BEGIN;

-- Theme modern
UPDATE AD_SysConfig SET Value = 'modern', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_THEME' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_THEME', 'modern', 'Theme activo del cliente web ZK', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_THEME' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Logo grande: wordmark Libertya ERP
UPDATE AD_SysConfig SET Value = '/theme/modern/images/login-logo.png', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_LOGO_LARGE' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_LOGO_LARGE', '/theme/modern/images/login-logo.png', 'Logo grande de login/rol - Libertya ERP', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_LOGO_LARGE' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Logo chico del header: isotipo
UPDATE AD_SysConfig SET Value = '/theme/modern/images/header-logo.png', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_LOGO_SMALL' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_LOGO_SMALL', '/theme/modern/images/header-logo.png', 'Logo chico del header - isotipo Libertya', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_LOGO_SMALL' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Favicon: isotipo
UPDATE AD_SysConfig SET Value = '/theme/modern/images/icon.png', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_BROWSER_ICON' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_BROWSER_ICON', '/theme/modern/images/icon.png', 'Favicon de la version web - isotipo Libertya', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_BROWSER_ICON' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Dejar la secuencia System por delante de los IDs usados
UPDATE AD_Sequence
SET CurrentNextSys = GREATEST(CurrentNextSys,
    (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000))
WHERE Name = 'AD_SysConfig';

COMMIT;
```

## Script: instancia Libertya Next

Igual al de ERP; lo único que cambia es el logo grande (`login-logo-next.png`).

```sql
BEGIN;

-- Theme modern
UPDATE AD_SysConfig SET Value = 'modern', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_THEME' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_THEME', 'modern', 'Theme activo del cliente web ZK', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_THEME' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Logo grande: wordmark Libertya Next
UPDATE AD_SysConfig SET Value = '/theme/modern/images/login-logo-next.png', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_LOGO_LARGE' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_LOGO_LARGE', '/theme/modern/images/login-logo-next.png', 'Logo grande de login/rol - Libertya Next', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_LOGO_LARGE' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Logo chico del header: isotipo
UPDATE AD_SysConfig SET Value = '/theme/modern/images/header-logo.png', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_LOGO_SMALL' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_LOGO_SMALL', '/theme/modern/images/header-logo.png', 'Logo chico del header - isotipo Libertya', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_LOGO_SMALL' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Favicon: isotipo
UPDATE AD_SysConfig SET Value = '/theme/modern/images/icon.png', IsActive = 'Y', Updated = NOW(), UpdatedBy = 100
WHERE Name = 'ZK_BROWSER_ICON' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
INSERT INTO AD_SysConfig (AD_SysConfig_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy, Name, Value, Description, EntityType)
SELECT (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000),
    0, 0, 'Y', NOW(), 100, NOW(), 100,
    'ZK_BROWSER_ICON', '/theme/modern/images/icon.png', 'Favicon de la version web - isotipo Libertya', 'D'
WHERE NOT EXISTS (SELECT 1 FROM AD_SysConfig WHERE Name = 'ZK_BROWSER_ICON' AND AD_Client_ID = 0 AND AD_Org_ID = 0);

-- Dejar la secuencia System por delante de los IDs usados
UPDATE AD_Sequence
SET CurrentNextSys = GREATEST(CurrentNextSys,
    (SELECT COALESCE(MAX(AD_SysConfig_ID), 0) + 1 FROM AD_SysConfig WHERE AD_SysConfig_ID < 1000000))
WHERE Name = 'AD_SysConfig';

COMMIT;
```

## Usar otro ícono o logo

Para apuntar a una imagen propia, cambiar el `Value` de la clave correspondiente por su ruta dentro de la webapp (tiene que estar incluida en `webui.war`) y reiniciar el JBoss. Por ejemplo, para el favicon:

```sql
UPDATE AD_SysConfig SET Value = '/theme/modern/images/<mi-icono>.png', Updated = NOW()
WHERE Name = 'ZK_BROWSER_ICON' AND AD_Client_ID = 0 AND AD_Org_ID = 0;
```

Para volver al default del theme, desactivar la clave (`IsActive = 'N'`) y reiniciar.

## Verificar la configuración actual

```sql
SELECT AD_SysConfig_ID, Name, Value, IsActive
FROM AD_SysConfig
WHERE AD_Client_ID = 0
  AND (Name LIKE 'ZK\_THEME' OR Name LIKE 'ZK\_LOGO%' OR Name LIKE 'ZK\_BROWSER%' OR Name = 'WEBUI_LOGOURL')
ORDER BY Name;
```

## Assets de origen

Los `.png` del theme salen del kit de marca oficial (`libertya_logos_kit`, entregado en `.zip` por diseño), sin recomprimir ni recortar:

| Archivo en `zkwebui/theme/modern/images/` | Origen en el kit |
|---|---|
| `login-logo.png` | `libertya_erp_original_transparente.png` |
| `login-logo-next.png` | `libertya_next_original_transparente.png` |
| `header-logo.png` | `libertya_isotipo_transparente.png` |
| `icon.png` | `libertya_isotipo_transparente.png`, redimensionado a 16×16 |

El kit también incluye variantes `dark`/`mono` de cada logo y un `libertya_pos_*` (wordmark POS) sin usar todavía, por si en el futuro se decide traer este mismo esquema de theme a la aplicación POS.
