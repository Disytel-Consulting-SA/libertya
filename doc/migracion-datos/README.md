# Planillas modelo de migración de datos a Libertya (OP#8600)

| Archivo | Contenido |
|---|---|
| `Libertya_Migracion_1_Maestros.xlsx` | Datos maestros y relevamiento manual. Se completa primero. |
| `Libertya_Migracion_2_Saldos.xlsx` | Saldos para la puesta en marcha. Usa los códigos de Maestros. |
| `mapeo_libertya.md` | Destino propuesto en Libertya de cada columna (uso interno). |

## Modificar las planillas

No editar los `.xlsx` a mano: cambiar las columnas en `generador/especificacion.py` y regenerar
(requiere Python 3 y `openpyxl`):

```sh
python3 generador/generar.py
```

El comando sobrescribe los dos `.xlsx` y `mapeo_libertya.md`. Las fórmulas se calculan al abrir el archivo en Excel.
