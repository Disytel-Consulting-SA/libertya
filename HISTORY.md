# HISTORY

## 2026-04-14

- Se integró `origin/dev` en `feature/lby-next-webui` y se resolvieron conflictos de merge en `.gitignore`, `VPluginInstallerUtils` y `preinstall_from_25.0.sql`.

## 2026-04-01

- Se ajustó la pestaña `Pago` en formularios de cobros/pagos para permitir scroll interno cuando el acordeón supera el alto visible.
- Se quitó la altura fija de los paneles del acordeón de medios de cobro para que cada opción se adapte a la cantidad real de campos, especialmente en cheque, retención y tarjeta.
- Se reestilizaron los botones de acción de la pestaña `Pago` con variantes redondeadas del theme `modern`, usando dorado para acciones principales y oscuro para acciones secundarias.
- Se redistribuyeron simétricamente los campos superiores de `Órdenes de pago` y `Recibos de clientes`, con anchos consistentes para labels, campos, fechas, combos y editores de búsqueda.
- Se actualizó `AGENTS.md` para exigir la actualización de `HISTORY.md` en cada cambio efectivo, separado por fecha.
- Se puso al día esta bitácora con los cambios recientes del theme `modern`.

## 2026-03-31

- Se ajustó el `Modo Grilla` de ventanas dinámicas para eliminar el celeste legacy en la selección y en la paginación inferior, alineándolo con la paleta negro/dorado del theme `modern`.
- Se reestilizó la tabla de facturas de `Recibos de clientes` para unificar encabezados, filas, hover, importes y controles internos con el nuevo lenguaje visual.
- Se corrigieron los anchos de `Punto de Venta`, `Cargo de Org.` y `Cantidad` en `Recibos de clientes` para evitar que queden sobredimensionados.
- Se agregaron clases estructurales en formularios Java de `zkwebui` para aislar grids, acordeones, botones y bloques de layout del flujo de cobros/pagos sin afectar otras pantallas.

## 2026-03-30

- Se inició y consolidó el theme `modern` para `zkwebui`, como alternativa al theme legacy.
- Se rediseñaron las pantallas de ingreso y selección de perfil con nueva paleta negro/dorado, tipografía moderna y branding `Libertya Next`.
- Se incorporó el logo `Libertya Next` al flujo visual del cliente web.
- Se ajustaron barra superior, tabs, listado de ventanas, menú principal, menú lateral de ventanas y barra inferior para alinearlos con el nuevo theme.
- Se modernizaron formularios y controles: `combobox`, grupos de campos, botones laterales de editores, labels y varios estados `hover` y `selected`.
- Se estilizó la ventana `WRecordInfo` con header oscuro, botón de acción acorde al theme y cierre integrado.
- Se corrigieron casos donde ZK legacy seguía imponiendo estilos celestes mediante overrides más específicos sobre el DOM real renderizado.
- Se ajustó `ADWindowPanel` para corregir la altura fija de la toolbar interna de ventanas.
- Se documentó la activación y el despliegue del theme en `doc/zkwebui-modern-theme.md`.
- Se actualizó `.gitignore` para ignorar carpetas y salidas generadas por compilación como `**/compilacion/`, `zkwebui/WEB-INF/classes/`, `zkwebui/dist/`, `install/lib/` e `interfaces/classes/`.
- Se corrigieron las rutas del theme `modern` para cargar correctamente recursos bajo el contexto `/webui` y se agregó `themesaf.css.dsp` para evitar errores `404` y problemas de MIME en hojas de estilo específicas del navegador.
