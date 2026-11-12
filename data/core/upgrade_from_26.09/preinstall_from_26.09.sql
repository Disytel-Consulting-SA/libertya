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

--20261104-1500 OP#8516 Saldo de facturas de proveedor con tasa de cambio propia: lo imputado en pesos se convierte con esa tasa (clientes y facturas sin tasa propia no cambian)
CREATE OR REPLACE FUNCTION getallocatedamt(p_c_invoice_id integer, p_c_currency_id integer, p_c_conversiontype_id integer, p_multiplierap integer, p_fechacorte timestamp without time zone, p_c_invoicepayschedule_id integer)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$  DECLARE 	v_MultiplierAP		NUMERIC := 1; 	v_PaidAmt			NUMERIC := 0; 	v_ConversionType_ID INTEGER := p_c_conversionType_ID; 	v_Currency_ID       INTEGER := p_c_currency_id; 	v_Temp     NUMERIC; 	v_SchedulesAmt NUMERIC; 	v_Diff NUMERIC; 	ar			RECORD; 	s			RECORD; 	v_DateAcct timestamp without time zone; 	schedule_founded boolean; v_PayRate NUMERIC; v_InvRate NUMERIC := NULL; v_CompanyCurr INTEGER; BEGIN 	 	IF (p_MultiplierAP IS NOT NULL) THEN 		v_MultiplierAP := p_MultiplierAP::numeric; 	END IF; 	 	SELECT DateAcct, CASE WHEN IsSOTrx = 'N' AND COALESCE(Cintolo_Exchange_Rate, 0) > 0 THEN Cintolo_Exchange_Rate ELSE NULL END 	       INTO v_DateAcct, v_InvRate 	FROM C_Invoice  	WHERE C_Invoice_ID = p_c_invoice_id; 	IF (v_InvRate IS NOT NULL) THEN 		SELECT acs.C_Currency_ID INTO v_CompanyCurr FROM C_Invoice i INNER JOIN AD_ClientInfo ci ON (ci.AD_Client_ID = i.AD_Client_ID) INNER JOIN C_AcctSchema acs ON (acs.C_AcctSchema_ID = ci.C_AcctSchema1_ID) WHERE i.C_Invoice_ID = p_c_invoice_id; 	END IF; 	FOR ar IN  		SELECT	a.AD_Client_ID, a.AD_Org_ID, 		al.Amount, al.DiscountAmt, al.WriteOffAmt, 		a.C_Currency_ID, a.DateTrx , al.C_Invoice_Credit_ID, al.C_Payment_ID, (SELECT pp.C_Currency_ID FROM C_Payment pp WHERE pp.C_Payment_ID = al.C_Payment_ID) AS PayCurrency_ID 		FROM	C_AllocationLine al 		INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID) 		WHERE	(al.C_Invoice_ID = p_C_Invoice_ID OR  				al.C_Invoice_Credit_ID = p_C_Invoice_ID )            	AND   a.IsActive='Y'           	AND   (p_fechacorte is null OR a.dateacct::date <= p_fechacorte::date) 	LOOP 	     		IF (p_C_Invoice_ID = ar.C_Invoice_Credit_ID) THEN 		   v_Temp := ar.Amount; 		ELSE 		   v_Temp := ar.Amount + ar.DisCountAmt + ar.WriteOffAmt; 		END IF; 		 		v_PayRate := NULL; IF (ar.C_Payment_ID IS NOT NULL AND ar.C_Currency_ID <> v_Currency_ID AND ar.PayCurrency_ID = v_Currency_ID AND p_C_Invoice_ID <> COALESCE(ar.C_Invoice_Credit_ID, 0)) THEN v_PayRate := payment_exchange_rate(ar.C_Payment_ID); END IF; IF (v_PayRate IS NOT NULL) THEN v_PaidAmt := v_PaidAmt + v_Temp / v_PayRate; ELSIF (v_InvRate IS NOT NULL AND ar.C_Currency_ID = v_CompanyCurr AND v_Currency_ID <> v_CompanyCurr AND p_C_Invoice_ID <> COALESCE(ar.C_Invoice_Credit_ID, 0)) THEN v_PaidAmt := v_PaidAmt + v_Temp / v_InvRate; ELSE v_PaidAmt := v_PaidAmt + currencyConvert(v_Temp, ar.C_Currency_ID, v_Currency_ID, v_DateAcct, v_ConversionType_ID, ar.AD_Client_ID, ar.AD_Org_ID); END IF; 	 	END LOOP; 	 	IF (p_c_invoicepayschedule_id > 0) THEN  		v_SchedulesAmt := 0; 		schedule_founded := false;         		FOR s IN  SELECT  ips.C_InvoicePaySchedule_ID, currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_DateAcct, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt 	         			FROM    C_InvoicePaySchedule ips 	         			INNER JOIN C_Invoice i on (ips.C_Invoice_ID = i.C_Invoice_ID) 		 			WHERE	ips.C_Invoice_ID = p_c_invoice_id AND   ips.IsValid='Y'         	 			ORDER BY ips.DueDate  		LOOP     			 			v_SchedulesAmt := v_SchedulesAmt + s.DueAmt; 			schedule_founded := s.C_InvoicePaySchedule_ID = p_c_invoicepayschedule_id; 			IF (schedule_founded) THEN 				 				v_Diff := v_PaidAmt - v_SchedulesAmt; 				 				 				IF (v_Diff >= 0) THEN 					v_PaidAmt := s.DueAmt; 				ELSE 					 					 					v_PaidAmt := s.DueAmt - abs(v_Diff); 					 					 					IF (v_PaidAmt <= 0) THEN 						v_PaidAmt := 0; 					END IF; 				END IF; 				EXIT; 			END IF; 		END LOOP; 		 		IF (NOT schedule_founded) THEN 			v_PaidAmt := 0; 		END IF; 	END IF; 	 	RETURN	v_PaidAmt * v_MultiplierAP; END; $function$

;


--20261105-1000 OP#8516 La clausula de ajuste solo se muestra en facturas en moneda local (en moneda extranjera la dif. de cambio se sugiere siempre)
UPDATE AD_Field
SET DisplayLogic = '@C_Currency_ID@=@$C_Currency_ID@',
    Updated = now()
WHERE AD_Tab_ID = 263
  AND AD_Column_ID = (SELECT c.AD_Column_ID FROM AD_Column c
                      WHERE c.ColumnName = 'Cintolo_Adjustment_Clause'
                        AND c.AD_Table_ID = (SELECT AD_Table_ID FROM AD_Table WHERE TableName = 'C_Invoice'))
  AND DisplayLogic IS DISTINCT FROM '@C_Currency_ID@=@$C_Currency_ID@';

-- 20261029-1348 OP#8493 Rendimiento de cuenta corriente: en c_alldocumentscc_v las ramas de pagos y de lineas de caja agregaban
-- las lineas de c_allocation_detail_v de TODA la base (calculando currencyconvert por linea) y recien despues se unian al cliente.
-- Se pasan a LEFT JOIN LATERAL correlacionado por pago/linea de caja, de modo que solo se agreguen las lineas del documento.
-- No cambia columnas ni resultado de la vista.
CREATE OR REPLACE VIEW c_alldocumentscc_v AS
 SELECT c.c_currency_id,
        CASE
            WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
            ELSE COALESCE(ps.dueamt, i.grandtotal)
        END AS amount,
        CASE
            WHEN dt.signo_issotrx = 1 AND dt.doctypekey::text <> 'RTI'::text THEN
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN dt.signo_issotrx = 1 AND dt.doctypekey::text <> 'RTI'::text THEN 0::numeric
            ELSE
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
        END AS credit,
    dt.name AS tipo_doc,
    i.documentno,
    i.dateinvoiced AS datetrx,
    i.dateacct,
    i.c_doctypetarget_id AS c_doctype_id,
    'C_Invoice'::text AS documenttable,
    i.c_invoice_id AS document_id,
    invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
    i.created,
    i.c_bpartner_id,
    i.ad_org_id,
    i.ad_client_id,
    i.issotrx,
    ps.c_invoicepayschedule_id,
    COALESCE(ps.duedate, i.dateacct) AS duedate
   FROM c_invoice i
     JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
     JOIN c_currency c ON i.c_currency_id = c.c_currency_id
     LEFT JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id
  WHERE (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTIXX'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND i.processed = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR dt.issotrx = 'Y'::bpchar AND dt.isfiscaldocument = 'Y'::bpchar AND i.docstatus = 'VO'::bpchar)
UNION ALL
 SELECT DISTINCT ah.c_currency_id,
    ah.grandtotal - COALESCE(alc.creditamt, 0::numeric) AS amount,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'CRR'::text AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'VPR'::text THEN 0::numeric
            ELSE
            CASE
                WHEN COALESCE(dt.issotrx, i.issotrx) = 'N'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text = 'VPR'::text THEN 0::numeric
                ELSE ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
            END
        END AS debit,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'CRR'::text AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'VPR'::text THEN ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
            ELSE
            CASE
                WHEN COALESCE(dt.issotrx, i.issotrx) = 'N'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text = 'VPR'::text THEN ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
                ELSE 0::numeric
            END
        END AS credit,
    COALESCE(dt.name, 'Asignación Manual'::character varying) AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    0 AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    COALESCE(dt.issotrx, i.issotrx) AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     LEFT JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN ( SELECT al2.c_allocationhdr_id,
            sum(al2.amount) AS creditamt
           FROM c_allocationline al2
             JOIN c_invoice i2 ON al2.c_invoice_credit_id = i2.c_invoice_id
             JOIN c_doctype dt2 ON i2.c_doctypetarget_id = dt2.c_doctype_id AND (dt2.doctypekey::text <> ALL (ARRAY['RTR'::text, 'RCR'::text, 'RTI'::text, 'RCI'::text]))
          WHERE al2.c_invoice_credit_id IS NOT NULL
          GROUP BY al2.c_allocationhdr_id) alc ON alc.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
     LEFT JOIN c_payment cp ON cp.c_payment_id = al.c_payment_id
     LEFT JOIN c_doctype cd ON cd.c_doctype_id = cp.c_doctype_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RC'::character varying::text, 'OP'::character varying::text, 'MAN'::character varying::text])) AND (ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)) > 0::numeric AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
UNION ALL
 SELECT ah.c_currency_id,
    ah.grandtotal AS amount,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN COALESCE(sum(alf.montosaldado), 0::numeric)
            ELSE ah.grandtotal
        END AS debit,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN ah.grandtotal
            ELSE COALESCE(sum(alf.montosaldado), 0::numeric)
        END AS credit,
    dt.name::text || ' Adelantado'::text AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    ah.grandtotal - COALESCE(sum(alf.montosaldado), 0::numeric) AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
     LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
     LEFT JOIN ( SELECT li.c_allocation_detail_v_id,
            li.c_allocationhdr_id,
            li.ad_client_id,
            li.ad_org_id,
            li.isactive,
            li.created,
            li.createdby,
            li.updated,
            li.updatedby,
            li.fecha,
            li.factura,
            li.c_currency_id,
            li.montofactura,
            li.pagonro,
            li.tipo,
            li.cash,
            li.montosaldado,
            li.payamt,
            li.c_allocationline_id,
            li.c_invoice_id,
            li.paydescription,
            li.payment_medium_name,
            li.pay_currency_id,
            li.c_bankaccount_id,
            li.numerocomprobante,
            li.puntodeventa,
            li.c_letra_comprobante_id,
            li.doctypekey,
            li.doctypename,
            li.paymentrule,
            li.c_region_delivery_id,
            li.netamount,
            li.c_paymentterm_id,
            li.dateinvoiced,
            li.c_invoice_credit_id,
            li.credit_doctypekey,
            li.credit_doctypename,
            li.credit_numerocomprobante,
            li.credit_puntodeventa,
            li.credit_letra_comprobante_id,
            li.credit_netamount,
            li.c_cashline_id,
            li.cashname,
            li.c_payment_id,
            li.accountno,
            li.checkno,
            li.a_name,
            li.a_bank,
            li.a_cuit,
            li.duedate,
            li.dateemissioncheck,
            li.checkstatus,
            li.creditcardnumber,
            li.couponbatchnumber,
            li.couponnumber,
            li.m_entidadfinancieraplan_id,
            li.m_entidadfinanciera_id,
            li.posnet,
            li.micr,
            li.isreconciled,
            li.creditdate,
            li.creditdocumentno,
            lh.c_allocationhdr_id,
            lh.ad_client_id,
            lh.ad_org_id,
            lh.isactive,
            lh.created,
            lh.createdby,
            lh.updated,
            lh.updatedby,
            lh.documentno,
            lh.description,
            lh.datetrx,
            lh.dateacct,
            lh.c_currency_id,
            lh.approvalamt,
            lh.ismanual,
            lh.docstatus,
            lh.docaction,
            lh.isapproved,
            lh.processing,
            lh.processed,
            lh.posted,
            lh.c_bpartner_id,
            lh.allocationtype,
            lh.retencion_amt,
            lh.grandtotal,
            lh.allocationaction,
            lh.actiondetail,
            lh.c_posjournal_id,
            lh.c_doctype_id,
            lh.c_banklist_id,
            lh.datetrx
           FROM c_allocation_detail_v li
             JOIN c_allocationhdr lh ON li.c_allocationhdr_id = lh.c_allocationhdr_id
          WHERE lh.processed = 'Y'::bpchar AND (lh.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) alf(c_allocation_detail_v_id, c_allocationhdr_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, fecha, factura, c_currency_id, montofactura, pagonro, tipo, cash, montosaldado, payamt, c_allocationline_id, c_invoice_id, paydescription, payment_medium_name, pay_currency_id, c_bankaccount_id, numerocomprobante, puntodeventa, c_letra_comprobante_id, doctypekey, doctypename, paymentrule, c_region_delivery_id, netamount, c_paymentterm_id, dateinvoiced, c_invoice_credit_id, credit_doctypekey, credit_doctypename, credit_numerocomprobante, credit_puntodeventa, credit_letra_comprobante_id, credit_netamount, c_cashline_id, cashname, c_payment_id, accountno, checkno, a_name, a_bank, a_cuit, duedate, dateemissioncheck, checkstatus, creditcardnumber, couponbatchnumber, couponnumber, m_entidadfinancieraplan_id, m_entidadfinanciera_id, posnet, micr, isreconciled, creditdate, creditdocumentno, c_allocationhdr_id_1, ad_client_id_1, ad_org_id_1, isactive_1, created_1, createdby_1, updated_1, updatedby_1, documentno, description, datetrx, dateacct, c_currency_id_1, approvalamt, ismanual, docstatus, docaction, isapproved, processing, processed, posted, c_bpartner_id, allocationtype, retencion_amt, grandtotal, allocationaction, actiondetail, c_posjournal_id, c_doctype_id, c_banklist_id, datetrx_1) ON (alf.c_payment_id = p.c_payment_id OR alf.c_cashline_id = cl.c_cashline_id OR alf.c_invoice_credit_id = ic.c_invoice_id) AND alf.c_allocationline_id <> al.c_allocationline_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
  GROUP BY ah.c_currency_id, ah.grandtotal, ah.documentno, ah.datetrx, ah.c_doctype_id, ah.c_allocationhdr_id, dt.name, dt.issotrx, ah.created, ah.c_bpartner_id, ah.ad_org_id, ah.ad_client_id
UNION ALL
 SELECT p.c_currency_id,
    p.payamt AS amount,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN dt.issotrx = 'Y'::bpchar THEN
                CASE
                    WHEN dt.doctypekey::text = 'CRR'::text THEN p.payamt
                    ELSE COALESCE(al.montosaldado, 0::numeric)
                END
                ELSE
                CASE
                    WHEN dt.doctypekey::text <> 'VPR'::text THEN p.payamt
                    ELSE COALESCE(al.montosaldado, 0::numeric)
                END
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN dt.issotrx = 'Y'::bpchar THEN
                CASE
                    WHEN dt.doctypekey::text = 'CRR'::text THEN COALESCE(al.montosaldado, 0::numeric)
                    ELSE p.payamt
                END
                ELSE
                CASE
                    WHEN dt.doctypekey::text = 'VPR'::text THEN p.payamt
                    ELSE COALESCE(al.montosaldado, 0::numeric)
                END
            END
            ELSE 0::numeric
        END AS credit,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN TRIM(BOTH FROM dt.name)
            ELSE
            CASE
                WHEN p.isreceipt = 'N'::bpchar THEN 'Pago (Cargo)'::text
                ELSE 'Cobro (Cargo)'::text
            END
        END AS tipo_doc,
    p.documentno,
    p.datetrx,
    p.dateacct,
    p.c_doctype_id,
    'C_Payment'::text AS documenttable,
    p.c_payment_id AS document_id,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN p.payamt - COALESCE(al.montosaldado, 0::numeric)
            ELSE 0::numeric
        END AS openamt,
    p.created,
    p.c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_payment p
     JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
     LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
     LEFT JOIN LATERAL ( SELECT d.c_payment_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          WHERE d.c_payment_id = p.c_payment_id
          GROUP BY d.c_payment_id, d.isactive) al ON al.c_payment_id = p.c_payment_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_payment_id IS NULL OR dt.doctypekey::text = 'CRR'::text OR dt.doctypekey::text = 'VPR'::text OR al.montosaldado > 0::numeric AND al.montosaldado < p.payamt) AND (al.cant IS NULL OR al.cant = 0 OR dt.doctypekey::text = 'CRR'::text OR dt.doctypekey::text = 'VPR'::text)
UNION ALL
 SELECT p.c_currency_id,
    abs(p.amount) AS amount,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN p.amount > 0::numeric THEN abs(COALESCE(al.montosaldado, 0::numeric)) * 1::numeric
                ELSE abs(p.amount) * 1::numeric
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN p.amount > 0::numeric THEN abs(p.amount)
                ELSE abs(COALESCE(al.montosaldado, 0::numeric))
            END
            ELSE 0::numeric
        END AS credit,
    'Linea de caja'::character varying::text ||
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN ''::text
            ELSE ' (Cargo)'::text
        END AS tipo_doc,
    c.name AS documentno,
    c.statementdate AS datetrx,
    c.dateacct,
    NULL::integer AS c_doctype_id,
    'C_CashLine'::text AS documenttable,
    p.c_cashline_id AS document_id,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN abs(p.amount) - COALESCE(al.montosaldado, 0::numeric)
            ELSE 0::numeric
        END AS openamt,
    p.created,
    p.c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
        CASE
            WHEN p.amount > 0::numeric THEN 'Y'::text
            ELSE 'N'::text
        END AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_cashline p
     JOIN c_cash c ON p.c_cash_id = c.c_cash_id
     LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
     LEFT JOIN LATERAL ( SELECT d.c_cashline_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          WHERE d.c_cashline_id = p.c_cashline_id
          GROUP BY d.c_cashline_id, d.isactive) al ON al.c_cashline_id = p.c_cashline_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_cashline_id IS NULL OR al.montosaldado < abs(p.amount)) AND (al.cant IS NULL OR al.cant = 0)
UNION ALL
 SELECT c.c_currency_id,
        CASE
            WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
            ELSE COALESCE(ps.dueamt, i.grandtotal)
        END AS amount,
        CASE
            WHEN dt.signo_issotrx = 1 THEN
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 0::numeric
            ELSE
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
        END AS credit,
    dt.name AS tipo_doc,
    i.documentno,
    i.dateinvoiced AS datetrx,
    i.dateacct,
    i.c_doctypetarget_id AS c_doctype_id,
    'C_Invoice'::text AS documenttable,
    i.c_invoice_id AS document_id,
    invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
    i.created,
    i.c_bpartner_id,
    i.ad_org_id,
    i.ad_client_id,
    i.issotrx,
    ps.c_invoicepayschedule_id,
    COALESCE(ps.duedate, i.dateacct) AS duedate
   FROM c_invoice i
     JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
     JOIN c_currency c ON i.c_currency_id = c.c_currency_id
     LEFT JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id
  WHERE (dt.doctypekey::text = ANY (ARRAY['RTR'::text, 'RCR'::text])) AND i.processed = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR dt.issotrx = 'Y'::bpchar AND dt.isfiscaldocument = 'Y'::bpchar AND i.docstatus = 'VO'::bpchar) AND invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) > 0::numeric AND NOT (i.c_invoice_id IN ( SELECT al.c_invoice_credit_id
           FROM c_allocationline al
             JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
          WHERE (ah.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND al.c_invoice_credit_id IS NOT NULL));

-- 20261112-1530 OP#8206 Cuenta corriente: las ventas en efectivo cobradas por Libro de Caja no se descuentan del saldo.
-- Las lineas de caja de tipo Invoice no tienen c_bpartner_id propio (el informe filtra por cliente y las descartaba) y quedaban
-- ocultas por una allocation MAN con total 0, que tampoco se muestra (grandtotal - creditamt > 0). Cambios solo en la rama de lineas de caja:
-- 1) cliente = COALESCE(linea de caja, factura de la linea); 2) las allocations MAN con total 0 no cuentan como "saldadas"
--    (montosaldado_visible) para decidir si se muestra la linea ni para el debito. openamt no cambia.
CREATE OR REPLACE VIEW c_alldocumentscc_v AS
 SELECT c.c_currency_id,
        CASE
            WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
            ELSE COALESCE(ps.dueamt, i.grandtotal)
        END AS amount,
        CASE
            WHEN dt.signo_issotrx = 1 AND dt.doctypekey::text <> 'RTI'::text THEN
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN dt.signo_issotrx = 1 AND dt.doctypekey::text <> 'RTI'::text THEN 0::numeric
            ELSE
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
        END AS credit,
    dt.name AS tipo_doc,
    i.documentno,
    i.dateinvoiced AS datetrx,
    i.dateacct,
    i.c_doctypetarget_id AS c_doctype_id,
    'C_Invoice'::text AS documenttable,
    i.c_invoice_id AS document_id,
    invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
    i.created,
    i.c_bpartner_id,
    i.ad_org_id,
    i.ad_client_id,
    i.issotrx,
    ps.c_invoicepayschedule_id,
    COALESCE(ps.duedate, i.dateacct) AS duedate
   FROM c_invoice i
     JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
     JOIN c_currency c ON i.c_currency_id = c.c_currency_id
     LEFT JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id
  WHERE (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTIXX'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND i.processed = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR dt.issotrx = 'Y'::bpchar AND dt.isfiscaldocument = 'Y'::bpchar AND i.docstatus = 'VO'::bpchar)
UNION ALL
 SELECT DISTINCT ah.c_currency_id,
    ah.grandtotal - COALESCE(alc.creditamt, 0::numeric) AS amount,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'CRR'::text AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'VPR'::text THEN 0::numeric
            ELSE
            CASE
                WHEN COALESCE(dt.issotrx, i.issotrx) = 'N'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text = 'VPR'::text THEN 0::numeric
                ELSE ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
            END
        END AS debit,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'CRR'::text AND COALESCE(cd.doctypekey, ''::character varying)::text <> 'VPR'::text THEN ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
            ELSE
            CASE
                WHEN COALESCE(dt.issotrx, i.issotrx) = 'N'::bpchar AND COALESCE(cd.doctypekey, ''::character varying)::text = 'VPR'::text THEN ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
                ELSE 0::numeric
            END
        END AS credit,
    COALESCE(dt.name, 'Asignación Manual'::character varying) AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    0 AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    COALESCE(dt.issotrx, i.issotrx) AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     LEFT JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN ( SELECT al2.c_allocationhdr_id,
            sum(al2.amount) AS creditamt
           FROM c_allocationline al2
             JOIN c_invoice i2 ON al2.c_invoice_credit_id = i2.c_invoice_id
             JOIN c_doctype dt2 ON i2.c_doctypetarget_id = dt2.c_doctype_id AND (dt2.doctypekey::text <> ALL (ARRAY['RTR'::text, 'RCR'::text, 'RTI'::text, 'RCI'::text]))
          WHERE al2.c_invoice_credit_id IS NOT NULL
          GROUP BY al2.c_allocationhdr_id) alc ON alc.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
     LEFT JOIN c_payment cp ON cp.c_payment_id = al.c_payment_id
     LEFT JOIN c_doctype cd ON cd.c_doctype_id = cp.c_doctype_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RC'::character varying::text, 'OP'::character varying::text, 'MAN'::character varying::text])) AND (ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)) > 0::numeric AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
UNION ALL
 SELECT ah.c_currency_id,
    ah.grandtotal AS amount,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN COALESCE(sum(alf.montosaldado), 0::numeric)
            ELSE ah.grandtotal
        END AS debit,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN ah.grandtotal
            ELSE COALESCE(sum(alf.montosaldado), 0::numeric)
        END AS credit,
    dt.name::text || ' Adelantado'::text AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    ah.grandtotal - COALESCE(sum(alf.montosaldado), 0::numeric) AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
     LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
     LEFT JOIN ( SELECT li.c_allocation_detail_v_id,
            li.c_allocationhdr_id,
            li.ad_client_id,
            li.ad_org_id,
            li.isactive,
            li.created,
            li.createdby,
            li.updated,
            li.updatedby,
            li.fecha,
            li.factura,
            li.c_currency_id,
            li.montofactura,
            li.pagonro,
            li.tipo,
            li.cash,
            li.montosaldado,
            li.payamt,
            li.c_allocationline_id,
            li.c_invoice_id,
            li.paydescription,
            li.payment_medium_name,
            li.pay_currency_id,
            li.c_bankaccount_id,
            li.numerocomprobante,
            li.puntodeventa,
            li.c_letra_comprobante_id,
            li.doctypekey,
            li.doctypename,
            li.paymentrule,
            li.c_region_delivery_id,
            li.netamount,
            li.c_paymentterm_id,
            li.dateinvoiced,
            li.c_invoice_credit_id,
            li.credit_doctypekey,
            li.credit_doctypename,
            li.credit_numerocomprobante,
            li.credit_puntodeventa,
            li.credit_letra_comprobante_id,
            li.credit_netamount,
            li.c_cashline_id,
            li.cashname,
            li.c_payment_id,
            li.accountno,
            li.checkno,
            li.a_name,
            li.a_bank,
            li.a_cuit,
            li.duedate,
            li.dateemissioncheck,
            li.checkstatus,
            li.creditcardnumber,
            li.couponbatchnumber,
            li.couponnumber,
            li.m_entidadfinancieraplan_id,
            li.m_entidadfinanciera_id,
            li.posnet,
            li.micr,
            li.isreconciled,
            li.creditdate,
            li.creditdocumentno,
            lh.c_allocationhdr_id,
            lh.ad_client_id,
            lh.ad_org_id,
            lh.isactive,
            lh.created,
            lh.createdby,
            lh.updated,
            lh.updatedby,
            lh.documentno,
            lh.description,
            lh.datetrx,
            lh.dateacct,
            lh.c_currency_id,
            lh.approvalamt,
            lh.ismanual,
            lh.docstatus,
            lh.docaction,
            lh.isapproved,
            lh.processing,
            lh.processed,
            lh.posted,
            lh.c_bpartner_id,
            lh.allocationtype,
            lh.retencion_amt,
            lh.grandtotal,
            lh.allocationaction,
            lh.actiondetail,
            lh.c_posjournal_id,
            lh.c_doctype_id,
            lh.c_banklist_id,
            lh.datetrx
           FROM c_allocation_detail_v li
             JOIN c_allocationhdr lh ON li.c_allocationhdr_id = lh.c_allocationhdr_id
          WHERE lh.processed = 'Y'::bpchar AND (lh.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) alf(c_allocation_detail_v_id, c_allocationhdr_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, fecha, factura, c_currency_id, montofactura, pagonro, tipo, cash, montosaldado, payamt, c_allocationline_id, c_invoice_id, paydescription, payment_medium_name, pay_currency_id, c_bankaccount_id, numerocomprobante, puntodeventa, c_letra_comprobante_id, doctypekey, doctypename, paymentrule, c_region_delivery_id, netamount, c_paymentterm_id, dateinvoiced, c_invoice_credit_id, credit_doctypekey, credit_doctypename, credit_numerocomprobante, credit_puntodeventa, credit_letra_comprobante_id, credit_netamount, c_cashline_id, cashname, c_payment_id, accountno, checkno, a_name, a_bank, a_cuit, duedate, dateemissioncheck, checkstatus, creditcardnumber, couponbatchnumber, couponnumber, m_entidadfinancieraplan_id, m_entidadfinanciera_id, posnet, micr, isreconciled, creditdate, creditdocumentno, c_allocationhdr_id_1, ad_client_id_1, ad_org_id_1, isactive_1, created_1, createdby_1, updated_1, updatedby_1, documentno, description, datetrx, dateacct, c_currency_id_1, approvalamt, ismanual, docstatus, docaction, isapproved, processing, processed, posted, c_bpartner_id, allocationtype, retencion_amt, grandtotal, allocationaction, actiondetail, c_posjournal_id, c_doctype_id, c_banklist_id, datetrx_1) ON (alf.c_payment_id = p.c_payment_id OR alf.c_cashline_id = cl.c_cashline_id OR alf.c_invoice_credit_id = ic.c_invoice_id) AND alf.c_allocationline_id <> al.c_allocationline_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
  GROUP BY ah.c_currency_id, ah.grandtotal, ah.documentno, ah.datetrx, ah.c_doctype_id, ah.c_allocationhdr_id, dt.name, dt.issotrx, ah.created, ah.c_bpartner_id, ah.ad_org_id, ah.ad_client_id
UNION ALL
 SELECT p.c_currency_id,
    p.payamt AS amount,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN dt.issotrx = 'Y'::bpchar THEN
                CASE
                    WHEN dt.doctypekey::text = 'CRR'::text THEN p.payamt
                    ELSE COALESCE(al.montosaldado, 0::numeric)
                END
                ELSE
                CASE
                    WHEN dt.doctypekey::text <> 'VPR'::text THEN p.payamt
                    ELSE COALESCE(al.montosaldado, 0::numeric)
                END
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN dt.issotrx = 'Y'::bpchar THEN
                CASE
                    WHEN dt.doctypekey::text = 'CRR'::text THEN COALESCE(al.montosaldado, 0::numeric)
                    ELSE p.payamt
                END
                ELSE
                CASE
                    WHEN dt.doctypekey::text = 'VPR'::text THEN p.payamt
                    ELSE COALESCE(al.montosaldado, 0::numeric)
                END
            END
            ELSE 0::numeric
        END AS credit,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN TRIM(BOTH FROM dt.name)
            ELSE
            CASE
                WHEN p.isreceipt = 'N'::bpchar THEN 'Pago (Cargo)'::text
                ELSE 'Cobro (Cargo)'::text
            END
        END AS tipo_doc,
    p.documentno,
    p.datetrx,
    p.dateacct,
    p.c_doctype_id,
    'C_Payment'::text AS documenttable,
    p.c_payment_id AS document_id,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN p.payamt - COALESCE(al.montosaldado, 0::numeric)
            ELSE 0::numeric
        END AS openamt,
    p.created,
    p.c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_payment p
     JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
     LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
     LEFT JOIN LATERAL ( SELECT d.c_payment_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          WHERE d.c_payment_id = p.c_payment_id
          GROUP BY d.c_payment_id, d.isactive) al ON al.c_payment_id = p.c_payment_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_payment_id IS NULL OR dt.doctypekey::text = 'CRR'::text OR dt.doctypekey::text = 'VPR'::text OR al.montosaldado > 0::numeric AND al.montosaldado < p.payamt) AND (al.cant IS NULL OR al.cant = 0 OR dt.doctypekey::text = 'CRR'::text OR dt.doctypekey::text = 'VPR'::text)
UNION ALL
 SELECT p.c_currency_id,
    abs(p.amount) AS amount,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN p.amount > 0::numeric THEN abs(COALESCE(al.montosaldado_visible, 0::numeric)) * 1::numeric
                ELSE abs(p.amount) * 1::numeric
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN
            CASE
                WHEN p.amount > 0::numeric THEN abs(p.amount)
                ELSE abs(COALESCE(al.montosaldado_visible, 0::numeric))
            END
            ELSE 0::numeric
        END AS credit,
    'Linea de caja'::character varying::text ||
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN ''::text
            ELSE ' (Cargo)'::text
        END AS tipo_doc,
    c.name AS documentno,
    c.statementdate AS datetrx,
    c.dateacct,
    NULL::integer AS c_doctype_id,
    'C_CashLine'::text AS documenttable,
    p.c_cashline_id AS document_id,
        CASE
            WHEN COALESCE(ch.c_charge_id, 0) = 0 THEN abs(p.amount) - COALESCE(al.montosaldado, 0::numeric)
            ELSE 0::numeric
        END AS openamt,
    p.created,
    COALESCE(p.c_bpartner_id, ic.c_bpartner_id) AS c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
        CASE
            WHEN p.amount > 0::numeric THEN 'Y'::text
            ELSE 'N'::text
        END AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_cashline p
     JOIN c_cash c ON p.c_cash_id = c.c_cash_id
     LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
     LEFT JOIN c_invoice ic ON ic.c_invoice_id = p.c_invoice_id
     LEFT JOIN LATERAL ( SELECT d.c_cashline_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            sum(d.montosaldado) FILTER (WHERE NOT (COALESCE(ah.allocationtype::text, ''::text) = 'MAN'::text AND COALESCE(ah.grandtotal, 1::numeric) = 0::numeric)) AS montosaldado_visible,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          WHERE d.c_cashline_id = p.c_cashline_id
          GROUP BY d.c_cashline_id, d.isactive) al ON al.c_cashline_id = p.c_cashline_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_cashline_id IS NULL OR COALESCE(al.montosaldado_visible, 0::numeric) < abs(p.amount)) AND (al.cant IS NULL OR al.cant = 0)
UNION ALL
 SELECT c.c_currency_id,
        CASE
            WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
            ELSE COALESCE(ps.dueamt, i.grandtotal)
        END AS amount,
        CASE
            WHEN dt.signo_issotrx = 1 THEN
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 0::numeric
            ELSE
            CASE
                WHEN COALESCE(ps.dueamt, 0::numeric) = 0::numeric THEN i.grandtotal
                ELSE COALESCE(ps.dueamt, i.grandtotal)
            END
        END AS credit,
    dt.name AS tipo_doc,
    i.documentno,
    i.dateinvoiced AS datetrx,
    i.dateacct,
    i.c_doctypetarget_id AS c_doctype_id,
    'C_Invoice'::text AS documenttable,
    i.c_invoice_id AS document_id,
    invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
    i.created,
    i.c_bpartner_id,
    i.ad_org_id,
    i.ad_client_id,
    i.issotrx,
    ps.c_invoicepayschedule_id,
    COALESCE(ps.duedate, i.dateacct) AS duedate
   FROM c_invoice i
     JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
     JOIN c_currency c ON i.c_currency_id = c.c_currency_id
     LEFT JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id
  WHERE (dt.doctypekey::text = ANY (ARRAY['RTR'::text, 'RCR'::text])) AND i.processed = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR dt.issotrx = 'Y'::bpchar AND dt.isfiscaldocument = 'Y'::bpchar AND i.docstatus = 'VO'::bpchar) AND invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) > 0::numeric AND NOT (i.c_invoice_id IN ( SELECT al.c_invoice_credit_id
           FROM c_allocationline al
             JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
          WHERE (ah.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND al.c_invoice_credit_id IS NOT NULL));
