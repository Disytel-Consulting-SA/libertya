-- ========================================================================================
-- PREINSTALL FROM 26.09
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

-- 20260831-0846: Preinstall from 26.09

--20260929-1700 Registrar callouts de tasa de cambio en facturas (moneda, fechas, tipo de conversion y clausula de ajuste)
UPDATE ad_column c
SET callout = CASE WHEN COALESCE(TRIM(c.callout), '') = '' THEN v.callout ELSE c.callout || ';' || v.callout END,
    updated = now()
FROM (VALUES
    ('C_Currency_ID', 'org.openXpertya.model.CalloutInvoiceExt.C_Currency_ID'),
    ('DateInvoiced', 'org.openXpertya.model.CalloutInvoiceExt.DateInvoiced'),
    ('DateAcct', 'org.openXpertya.model.CalloutInvoiceExt.DateAcct'),
    ('C_ConversionType_ID', 'org.openXpertya.model.CalloutInvoiceExt.C_ConversionType_ID'),
    ('Cintolo_Adjustment_Clause', 'org.openXpertya.model.CalloutInvoiceExt.Cintolo_Adjustment_Clause'),
    ('Cintolo_Adjustment_Clause_Currency', 'org.openXpertya.model.CalloutInvoiceExt.Cintolo_Adjustment_Clause_Currency')
) AS v(columnname, callout)
WHERE c.ad_table_id = (SELECT ad_table_id FROM ad_table WHERE tablename = 'C_Invoice')
  AND c.columnname = v.columnname
  AND COALESCE(c.callout, '') NOT LIKE '%' || v.callout || '%';

--20261103-1130 OP#8516 Tasa de cambio en la cabecera de recibos y ordenes de pago (C_AllocationHdr)
update ad_system set dummy = (SELECT addcolumnifnotexists('C_AllocationHdr','ExchangeRate','numeric(20,4)'));

INSERT INTO AD_Element (
    AD_Element_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy,
    ColumnName, EntityType, Name, PrintName, Description, AD_ComponentObjectUID, AD_ComponentVersion_ID
)
SELECT
    nextval('seq_ad_element'), 0, 0, 'Y',
    now(), 0, now(), 0,
    'ExchangeRate', 'D', 'Tasa de cambio', 'Tasa de cambio',
    'Tasa de cambio del comprobante (moneda de la compania por cada unidad de moneda extranjera)',
    'CORE-AD_Element-20261103113000000-851600',
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
WHERE NOT EXISTS (SELECT 1 FROM AD_Element WHERE ColumnName = 'ExchangeRate');

INSERT INTO AD_Column (
    AD_Column_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy,
    Name, Description, Help, Version, EntityType, ColumnName,
    AD_Table_ID, AD_Reference_ID, FieldLength,
    IsKey, IsParent, IsMandatory, IsUpdateable, IsIdentifier, IsTranslated, IsEncrypted,
    IsSelectionColumn, AD_Element_ID, IsSyncDatabase, IsAlwaysUpdateable, CalloutAlsoOnLoad, IsLink,
    AD_ComponentObjectUID, AD_ComponentVersion_ID
)
SELECT
    nextval('seq_ad_column'), 0, 0, 'Y',
    now(), 0, now(), 0,
    'Tasa de cambio',
    'Tasa de cambio del comprobante (moneda de la compania por cada unidad de moneda extranjera). Si esta vacia se utiliza la tasa de la tabla de tasas de cambio a la fecha del comprobante.',
    NULL, 0, 'D', 'ExchangeRate',
    t.AD_Table_ID, 22, 20,
    'N', 'N', 'N', 'Y', 'N', 'N', 'N',
    'N', (SELECT AD_Element_ID FROM AD_Element WHERE ColumnName = 'ExchangeRate' ORDER BY AD_Element_ID LIMIT 1), 'N', 'N', 'N', 'N',
    'CORE-AD_Column-20261103113000000-851601',
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
FROM AD_Table t
WHERE t.TableName = 'C_AllocationHdr'
  AND NOT EXISTS (
      SELECT 1 FROM AD_Column c
      WHERE c.AD_Table_ID = t.AD_Table_ID AND c.ColumnName = 'ExchangeRate'
  );

-- Campo de solo lectura en las ventanas que muestran la cabecera de recibos, ordenes de pago y asignaciones
INSERT INTO AD_Field (
    AD_Field_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy,
    Name, Description, IsCentrallyMaintained, AD_Tab_ID, AD_Column_ID,
    IsDisplayed, DisplayLength, IsReadOnly, SeqNo, IsSameLine,
    IsHeading, IsFieldOnly, IsEncrypted, EntityType, IsDisplayedInGrid, ExportRealValue,
    AD_ComponentObjectUID, AD_ComponentVersion_ID
)
SELECT
    nextval('seq_ad_field'), 0, 0, 'Y',
    now(), 0, now(), 0,
    'Tasa de cambio', 'Tasa de cambio del comprobante', 'N', t.AD_Tab_ID, c.AD_Column_ID,
    'Y', 20, 'Y', 135, 'N',
    'N', 'N', 'N', 'D', 'Y', 'N',
    'CORE-AD_Field-20261103113000000-' || t.AD_Tab_ID,
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
FROM AD_Tab t
INNER JOIN AD_Column c ON (c.AD_Table_ID = t.AD_Table_ID AND c.ColumnName = 'ExchangeRate')
WHERE t.AD_Table_ID = (SELECT AD_Table_ID FROM AD_Table WHERE TableName = 'C_AllocationHdr')
  AND t.TabLevel = 0
  AND NOT EXISTS (
      SELECT 1 FROM AD_Field f
      WHERE f.AD_Tab_ID = t.AD_Tab_ID AND f.AD_Column_ID = c.AD_Column_ID
  );

--20261103-1530 OP#8516 Traducciones del elemento y de los campos de la tasa de cambio (necesarias para que el modelo y las ventanas vean la columna en idiomas no base)
INSERT INTO AD_Element_Trl (
    AD_Element_ID, AD_Language, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy,
    Name, PrintName, Description, IsTranslated,
    AD_ComponentObjectUID, AD_ComponentVersion_ID
)
SELECT
    e.AD_Element_ID, l.AD_Language, 0, 0, 'Y',
    now(), 0, now(), 0,
    e.Name, e.PrintName, e.Description, 'N',
    'CORE-AD_Element_Trl-8516-' || l.AD_Language,
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
FROM AD_Element e
CROSS JOIN AD_Language l
WHERE e.ColumnName = 'ExchangeRate'
  AND l.IsActive = 'Y' AND l.IsSystemLanguage = 'Y'
  AND NOT EXISTS (
      SELECT 1 FROM AD_Element_Trl t
      WHERE t.AD_Element_ID = e.AD_Element_ID AND t.AD_Language = l.AD_Language
  );

INSERT INTO AD_Field_Trl (
    AD_Field_ID, AD_Language, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy,
    Name, Description, IsTranslated,
    AD_ComponentObjectUID, AD_ComponentVersion_ID
)
SELECT
    f.AD_Field_ID, l.AD_Language, 0, 0, 'Y',
    now(), 0, now(), 0,
    f.Name, f.Description, 'N',
    'CORE-AD_Field_Trl-8516-' || f.AD_Tab_ID || '-' || l.AD_Language,
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
FROM AD_Field f
INNER JOIN AD_Column c ON (c.AD_Column_ID = f.AD_Column_ID AND c.ColumnName = 'ExchangeRate')
INNER JOIN AD_Table tb ON (tb.AD_Table_ID = c.AD_Table_ID AND tb.TableName = 'C_AllocationHdr')
CROSS JOIN AD_Language l
WHERE l.IsActive = 'Y' AND l.IsSystemLanguage = 'Y'
  AND NOT EXISTS (
      SELECT 1 FROM AD_Field_Trl t
      WHERE t.AD_Field_ID = f.AD_Field_ID AND t.AD_Language = l.AD_Language
  );
