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
