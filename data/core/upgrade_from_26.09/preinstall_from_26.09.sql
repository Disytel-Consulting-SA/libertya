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

--20261103-1700 OP#8516 Campo Tasa de cambio en la ventana de facturas de proveedor (Invoice (Vendor))
INSERT INTO AD_Field (
    AD_Field_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy,
    Name, Description, IsCentrallyMaintained, AD_Tab_ID, AD_Column_ID,
    IsDisplayed, DisplayLogic, DisplayLength, IsReadOnly, SeqNo, IsSameLine,
    IsHeading, IsFieldOnly, IsEncrypted, EntityType, IsDisplayedInGrid, ExportRealValue,
    AD_ComponentObjectUID, AD_ComponentVersion_ID
)
SELECT
    nextval('seq_ad_field'), 0, 0, 'Y',
    now(), 0, now(), 0,
    'Tasa de cambio', 'Tasa de cambio del comprobante (moneda de la compania por cada unidad de moneda extranjera)', 'N', t.AD_Tab_ID, c.AD_Column_ID,
    'Y', '@C_Currency_ID@!@$C_Currency_ID@', 20, 'N', 275, 'Y',
    'N', 'N', 'N', 'D', 'N', 'N',
    'CORE-AD_Field-20261103170000000-' || t.AD_Tab_ID,
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
FROM AD_Tab t
INNER JOIN AD_Column c ON (c.AD_Table_ID = t.AD_Table_ID AND c.ColumnName = 'Cintolo_Exchange_Rate')
WHERE t.AD_Tab_ID = 290
  AND t.AD_Table_ID = (SELECT AD_Table_ID FROM AD_Table WHERE TableName = 'C_Invoice')
  AND NOT EXISTS (
      SELECT 1 FROM AD_Field f
      WHERE f.AD_Tab_ID = t.AD_Tab_ID AND f.AD_Column_ID = c.AD_Column_ID
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
    'CORE-AD_Field_Trl-8516-' || f.AD_Tab_ID || '-' || l.AD_Language || '-tasa',
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
FROM AD_Field f
INNER JOIN AD_Column c ON (c.AD_Column_ID = f.AD_Column_ID AND c.ColumnName = 'Cintolo_Exchange_Rate')
CROSS JOIN AD_Language l
WHERE f.AD_Tab_ID = 290
  AND f.AD_ComponentObjectUID = 'CORE-AD_Field-20261103170000000-290'
  AND l.IsActive = 'Y' AND l.IsSystemLanguage = 'Y'
  AND NOT EXISTS (
      SELECT 1 FROM AD_Field_Trl t
      WHERE t.AD_Field_ID = f.AD_Field_ID AND t.AD_Language = l.AD_Language
  );

--20261104-1000 OP#8516 Funciones con tasa de cambio propia de pagos en moneda extranjera (anticipos). Solo cambia el resultado para pagos cuya asignacion de origen tiene ExchangeRate
CREATE OR REPLACE FUNCTION payment_exchange_rate(p_c_payment_id integer)
  RETURNS numeric AS
$BODY$
/* OP#8516 Tasa de cambio propia de un pago en moneda extranjera: la de la asignacion (OP o recibo adelantado) que lo origino.
   Es nula si el pago no la tiene (todos los pagos anteriores), en cuyo caso se valoriza como siempre con la tasa de la tabla. */
DECLARE
	v_rate numeric;
BEGIN
	SELECT ah.ExchangeRate INTO v_rate
	FROM C_AllocationLine al
	INNER JOIN C_AllocationHdr ah ON (ah.C_AllocationHdr_ID = al.C_AllocationHdr_ID)
	WHERE al.C_Payment_ID = p_c_payment_id
	  AND ah.IsActive = 'Y' AND ah.DocStatus IN ('CO','CL')
	  AND ah.AllocationType IN ('OPA','RCA')
	  AND ah.ExchangeRate IS NOT NULL AND ah.ExchangeRate > 0
	ORDER BY ah.Created
	LIMIT 1;
	RETURN v_rate;
END;
$BODY$
  LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION libertya.paymentallocated(p_c_payment_id integer, p_c_currency_id integer, p_dateto timestamp without time zone)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$ /*************************************************************************  * The contents of this file are subject to the Compiere License.  You may  * obtain a copy of the License at    http://www.compiere.org/license.html  * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either  * express or implied. See the License for details. Code: Compiere ERP+CRM  * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.  *  * converted to postgreSQL by Karsten Thiemann (Schaeffer AG),   * kthiemann@adempiere.org  *************************************************************************  * Title:	Calculate Allocated Payment Amount in Payment Currency  * Description:          SELECT paymentAllocated(C_Payment_ID,C_Currency_ID), PayAmt, IsAllocated     FROM C_Payment_v      WHERE C_Payment_ID<1000000;          UPDATE C_Payment_v      SET IsAllocated=CASE WHEN paymentAllocated(C_Payment_ID, C_Currency_ID)=PayAmt THEN 'Y' ELSE 'N' END     WHERE C_Payment_ID>=1000000;  ****  *-Pasado a Liberya a partir en Adempiere 360LTS  *-ids son enteros  *-se asume que todos los montos son no negativos  *-no se utilza la vista C_Payment_V y no se corrige por AP/CM (de todas maneras  * Libertya actual no estaba usando esto y la vista siempre retorna 1 y multiplicaba  * por este monto)  *-no se utiliza multiplicadores: retorna siempre algo en el entorno [0..PayAmt]  *-la obtencion del si esta asociado a un cargo y la monto del pago es obtenido  *  en una sola setencia sql  *-se utiliza el redondeo por moneda en vez del redondeo a 2 (aunque esto  * ultimo tiene su sentido teniendo en cuenta que liberya maneja solo 2 decimales)  ************************************************************************/ DECLARE 	v_AllocatedAmt		NUMERIC := 0;     	v_PayAmt        	NUMERIC;     	r RECORD; v_PayRate NUMERIC; v_PayCurrency INTEGER; BEGIN          SELECT        INTO v_PayAmt MAX(PayAmt)      FROM C_Payment      WHERE C_Payment_ID=p_C_Payment_ID AND C_Charge_ID > 0;          IF (v_PayAmt IS NOT NULL) THEN         RETURN v_PayAmt;     END IF;      	 	v_PayRate := payment_exchange_rate(p_C_Payment_ID); SELECT C_Currency_ID INTO v_PayCurrency FROM C_Payment WHERE C_Payment_ID = p_C_Payment_ID; FOR r IN SELECT a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx 			FROM	C_AllocationLine al 	          INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID) 			WHERE	al.C_Payment_ID = p_C_Payment_ID           	AND   a.IsActive='Y'           	AND (p_dateto IS NULL OR a.dateacct::date <= p_dateto::date) 	LOOP 		IF (v_PayRate IS NOT NULL AND r.C_Currency_ID <> v_PayCurrency AND p_C_Currency_ID = v_PayCurrency) THEN v_AllocatedAmt := v_AllocatedAmt + r.Amount / v_PayRate; ELSE v_AllocatedAmt := v_AllocatedAmt + currencyConvert(r.Amount, r.C_Currency_ID, p_C_Currency_ID, r.DateTrx, null, r.AD_Client_ID, r.AD_Org_ID); END IF; 	END LOOP; 	 	 	v_AllocatedAmt := currencyRound(COALESCE(v_AllocatedAmt,0),p_c_currency_id,NULL);  	RETURN	v_AllocatedAmt; END; $function$;

CREATE OR REPLACE FUNCTION libertya.paymentavailable(p_c_payment_id integer, dateto timestamp without time zone)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$ DECLARE v_Currency_ID INTEGER; v_AvailableAmt NUMERIC := 0;   v_IsReceipt CHARACTER(1);   v_Amt NUMERIC := 0;   v_PayAmt NUMERIC := 0;   r RECORD; v_Charge_ID INTEGER; v_ConversionType_ID INTEGER; v_allocatedAmt NUMERIC;  v_DateAcct timestamp without time zone; v_PayRate NUMERIC; BEGIN BEGIN SELECT C_Currency_ID, PayAmt, IsReceipt, C_Charge_ID,C_ConversionType_ID, DateAcct INTO STRICT v_Currency_ID, v_PayAmt, v_IsReceipt, v_Charge_ID,v_ConversionType_ID, v_DateAcct FROM C_Payment WHERE C_Payment_ID = p_C_Payment_ID; EXCEPTION WHEN OTHERS THEN   RAISE NOTICE 'PaymentAvailable - %', SQLERRM; RETURN NULL; END; IF (v_Charge_ID > 0 ) THEN RETURN 0; END IF; v_allocatedAmt := 0; v_PayRate := payment_exchange_rate(p_C_Payment_ID); FOR r IN SELECT a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx FROM C_AllocationLine al INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID) WHERE al.C_Payment_ID = p_C_Payment_ID   AND a.IsActive='Y'   AND (dateTo IS NULL OR a.dateacct::date <= dateTo::date) LOOP IF (v_PayRate IS NOT NULL AND r.C_Currency_ID <> v_Currency_ID) THEN v_Amt := r.Amount / v_PayRate; ELSE v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID); END IF; v_allocatedAmt := v_allocatedAmt + v_Amt; END LOOP; v_AvailableAmt := ABS(v_PayAmt) - abs(v_allocatedAmt); IF (v_AvailableAmt < 0) THEN RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ; v_AvailableAmt := 0; END IF; IF (v_PayAmt < 0) THEN 	v_AvailableAmt := v_AvailableAmt * -1::numeric; END IF; v_AvailableAmt := currencyRound(v_AvailableAmt,v_Currency_ID,NULL); RETURN v_AvailableAmt; END; $function$;

CREATE OR REPLACE FUNCTION libertya.getallocatedamt(p_c_invoice_id integer, p_c_currency_id integer, p_c_conversiontype_id integer, p_multiplierap integer, p_fechacorte timestamp without time zone, p_c_invoicepayschedule_id integer)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$  DECLARE 	v_MultiplierAP		NUMERIC := 1; 	v_PaidAmt			NUMERIC := 0; 	v_ConversionType_ID INTEGER := p_c_conversionType_ID; 	v_Currency_ID       INTEGER := p_c_currency_id; 	v_Temp     NUMERIC; 	v_SchedulesAmt NUMERIC; 	v_Diff NUMERIC; 	ar			RECORD; 	s			RECORD; 	v_DateAcct timestamp without time zone; 	schedule_founded boolean; v_PayRate NUMERIC; BEGIN 	 	IF (p_MultiplierAP IS NOT NULL) THEN 		v_MultiplierAP := p_MultiplierAP::numeric; 	END IF; 	 	SELECT DateAcct 	       INTO v_DateAcct 	FROM C_Invoice  	WHERE C_Invoice_ID = p_c_invoice_id; 	FOR ar IN  		SELECT	a.AD_Client_ID, a.AD_Org_ID, 		al.Amount, al.DiscountAmt, al.WriteOffAmt, 		a.C_Currency_ID, a.DateTrx , al.C_Invoice_Credit_ID, al.C_Payment_ID, (SELECT pp.C_Currency_ID FROM C_Payment pp WHERE pp.C_Payment_ID = al.C_Payment_ID) AS PayCurrency_ID 		FROM	C_AllocationLine al 		INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID) 		WHERE	(al.C_Invoice_ID = p_C_Invoice_ID OR  				al.C_Invoice_Credit_ID = p_C_Invoice_ID )            	AND   a.IsActive='Y'           	AND   (p_fechacorte is null OR a.dateacct::date <= p_fechacorte::date) 	LOOP 	     		IF (p_C_Invoice_ID = ar.C_Invoice_Credit_ID) THEN 		   v_Temp := ar.Amount; 		ELSE 		   v_Temp := ar.Amount + ar.DisCountAmt + ar.WriteOffAmt; 		END IF; 		 		v_PayRate := NULL; IF (ar.C_Payment_ID IS NOT NULL AND ar.C_Currency_ID <> v_Currency_ID AND ar.PayCurrency_ID = v_Currency_ID AND p_C_Invoice_ID <> COALESCE(ar.C_Invoice_Credit_ID, 0)) THEN v_PayRate := payment_exchange_rate(ar.C_Payment_ID); END IF; IF (v_PayRate IS NOT NULL) THEN v_PaidAmt := v_PaidAmt + v_Temp / v_PayRate; ELSE v_PaidAmt := v_PaidAmt + currencyConvert(v_Temp, ar.C_Currency_ID, v_Currency_ID, v_DateAcct, v_ConversionType_ID, ar.AD_Client_ID, ar.AD_Org_ID); END IF; 	 	END LOOP; 	 	IF (p_c_invoicepayschedule_id > 0) THEN  		v_SchedulesAmt := 0; 		schedule_founded := false;         		FOR s IN  SELECT  ips.C_InvoicePaySchedule_ID, currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_DateAcct, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt 	         			FROM    C_InvoicePaySchedule ips 	         			INNER JOIN C_Invoice i on (ips.C_Invoice_ID = i.C_Invoice_ID) 		 			WHERE	ips.C_Invoice_ID = p_c_invoice_id AND   ips.IsValid='Y'         	 			ORDER BY ips.DueDate  		LOOP     			 			v_SchedulesAmt := v_SchedulesAmt + s.DueAmt; 			schedule_founded := s.C_InvoicePaySchedule_ID = p_c_invoicepayschedule_id; 			IF (schedule_founded) THEN 				 				v_Diff := v_PaidAmt - v_SchedulesAmt; 				 				 				IF (v_Diff >= 0) THEN 					v_PaidAmt := s.DueAmt; 				ELSE 					 					 					v_PaidAmt := s.DueAmt - abs(v_Diff); 					 					 					IF (v_PaidAmt <= 0) THEN 						v_PaidAmt := 0; 					END IF; 				END IF; 				EXIT; 			END IF; 		END LOOP; 		 		IF (NOT schedule_founded) THEN 			v_PaidAmt := 0; 		END IF; 	END IF; 	 	RETURN	v_PaidAmt * v_MultiplierAP; END; $function$;

--20261104-1100 OP#8516 Vinculo del asiento del mayor con la orden de pago o recibo que lo genero (diferencia de cambio de anticipos)
update ad_system set dummy = (SELECT addcolumnifnotexists('GL_Journal','C_AllocationHdr_ID','integer'));

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
    'Asignacion', 'Asignacion (orden de pago o recibo) que genero el asiento', NULL, 0, 'D', 'C_AllocationHdr_ID',
    t.AD_Table_ID, 19, 22,
    'N', 'N', 'N', 'Y', 'N', 'N', 'N',
    'N', (SELECT AD_Element_ID FROM AD_Element WHERE ColumnName = 'C_AllocationHdr_ID' ORDER BY AD_Element_ID LIMIT 1), 'N', 'N', 'N', 'N',
    'CORE-AD_Column-20261104110000000-851602',
    (SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = 'CORE-AD_ComponentVersion-1010413')
FROM AD_Table t
WHERE t.TableName = 'GL_Journal'
  AND NOT EXISTS (
      SELECT 1 FROM AD_Column c
      WHERE c.AD_Table_ID = t.AD_Table_ID AND c.ColumnName = 'C_AllocationHdr_ID'
  );


--20261104-1200 OP#8516 Tasa de cambio propia de la factura: la ventana la propone al cambiar moneda, fecha o tipo de conversion, y se muestra en facturas de cliente en moneda extranjera
UPDATE AD_Column
SET Callout = CASE WHEN Callout IS NULL OR Callout = '' THEN 'org.openXpertya.model.CalloutInvoiceExt.' || ColumnName
                   ELSE Callout || ';org.openXpertya.model.CalloutInvoiceExt.' || ColumnName END,
    Updated = now()
WHERE AD_Table_ID = (SELECT AD_Table_ID FROM AD_Table WHERE TableName = 'C_Invoice')
  AND ColumnName IN ('C_Currency_ID', 'DateInvoiced', 'DateAcct', 'C_ConversionType_ID')
  AND (Callout IS NULL OR Callout NOT LIKE '%CalloutInvoiceExt.' || ColumnName || '%');

UPDATE AD_Field
SET DisplayLogic = '@Cintolo_Adjustment_Clause@=''Y'' | @C_Currency_ID@!@$C_Currency_ID@',
    Updated = now()
WHERE AD_Tab_ID = 263
  AND AD_Column_ID = (SELECT c.AD_Column_ID FROM AD_Column c
                      WHERE c.ColumnName = 'Cintolo_Exchange_Rate'
                        AND c.AD_Table_ID = (SELECT AD_Table_ID FROM AD_Table WHERE TableName = 'C_Invoice'))
  AND DisplayLogic = '@Cintolo_Adjustment_Clause@=''Y'' | @C_Currency_ID@ = 100';
