-- ========================================================================================
-- PREINSTALL FROM 18.06
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20180814-1125 Nueva columna para habilitar/inhabilitar entidades comerciales para operar en el sistema
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','trxenabled','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

update ad_client
set modelvalidationclasses = modelvalidationclasses || ';' || 'org.openXpertya.model.BusinessPartnerValidator'
where position('org.openXpertya.model.BusinessPartnerValidator' in modelvalidationclasses) = 0;

update ad_client
set modelvalidationclasses = replace(modelvalidationclasses,';;',';');

--20180818-2045 El informe de Declaración de Valores o Cierre de Caja para tarjetas debe mostrar el total del mismo ya que es un cupón devuelto por el posnet 
CREATE OR REPLACE FUNCTION c_posjournal_c_payment_v_filtered(posjournalids anyarray)
  RETURNS SETOF c_posjournal_c_payment_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_c_payment_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND p.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, p.tendertype::character varying AS tendertype, p.documentno, p.description, ((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text))::character varying AS info, COALESCE(currencyconvert((case when ah.allocationtype in (''OPA'',''RCA'') or p.tendertype = ''C'' then p.payamt else al.amount + al.discountamt + al.writeoffamt end), (case when ah.allocationtype in (''OPA'',''RCA'') then p.c_currency_id else ah.c_currency_id end), ah.c_currency_id, ah.dateacct, null::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, (case when ah.allocationtype in (''OPA'',''RCA'') then ''Anticipo'' else i.documentno end) AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, al.changeamt, ah.c_currency_id
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   WHERE 1=1 ' || wherePosJournalIDs || ' AND NOT EXISTS (SELECT ala.c_payment_id FROM c_allocationline as ala INNER JOIN c_allocationhdr as aha on aha.c_allocationhdr_id = ala.c_allocationhdr_id WHERE ala.c_payment_id = p.c_payment_id AND aha.allocationtype in (''OPA'',''RCA'') AND aha.c_allocationhdr_id <> ah.c_allocationhdr_id) ' || 
   ' UNION ALL 
        ( SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::integer AS c_invoice_id, p.c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text)))::character varying(255) AS info, p.payamt AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, 0 AS changeamt, p.c_currency_id
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  WHERE 1=1 ' || wherePosJournalIDs || 
  ' AND NOT (EXISTS ( SELECT al.c_allocationline_id
    FROM c_allocationline al
   WHERE al.c_payment_id = p.c_payment_id))
  ORDER BY p.tendertype::character varying(2), p.documentno);';

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_payment_v_filtered(anyarray)
  OWNER TO libertya;
  
--20180824-1900 Fixes a las funciones de pendientes de entrega
CREATE OR REPLACE FUNCTION getqtyreserved(
    clientid integer,
    orgid integer,
    locatorid integer,
    productid integer,
    dateto date)
  RETURNS numeric AS
$BODY$
/***********
Obtiene la cantidad reservada a fecha de corte. Si no hay fecha de corte, entonces se devuelven los pendientes actuales.
Por lo pronto no se utiliza el pendiente a fecha de corte ya que primero deberíamos analizar e implementar 
una forma en la que se determine cuando un pedido fue completo, anulado, etc.
*/
DECLARE
reserved numeric;
BEGIN
reserved := 0;
--Si no hay fecha de corte o es mayor o igual a la fecha actual, entonces se suman las cantidades reservadas de los pedidos
--if ( dateTo is null OR dateTo >= current_date ) THEN
SELECT INTO reserved coalesce(sum(ol.qtyreserved),0)
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_doctype dto on dto.c_doctype_id = o.c_doctypetarget_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid 
and ol.qtyreserved <> 0
and o.docstatus in ('CO','CL')
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and (dateto is null or o.dateordered::date <= dateto::date)
and o.issotrx = 'Y'
and dto.doctypekey <> 'SOSOT';
/*ELSE
SELECT INTO reserved coalesce(sum(qty),0)
from (
-- Cantidad pedida a fecha de corte
select coalesce(sum(ol.qtyordered),0) as qty
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_doctype dt on dt.c_doctype_id = o.c_doctypetarget_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and o.issotrx = 'Y'
and dt.doctypekey NOT IN ('SOSOT')
and o.dateordered::date <= dateTo::date
and o.dateordered::date <= current_date
union all
-- Notas de crédito con (o sin) el check Actualizar Cantidades de Pedido
select coalesce(sum(il.qtyinvoiced),0) as qty
from c_invoiceline il
inner join c_invoice i on i.c_invoice_id = il.c_invoice_id
inner join c_doctype dt on dt.c_doctype_id = i.c_doctypetarget_id
inner join c_orderline ol on ol.c_orderline_id = il.c_orderline_id
inner join c_order o on o.c_order_id = ol.c_order_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and i.issotrx = 'Y'
and il.m_inoutline_id is null
and dt.signo_issotrx = '-1'
and o.dateordered::date <= dateTo::date
and i.dateinvoiced::date > dateTo::date
and i.dateinvoiced::date <= current_date
union all
--En transaction las salidas son negativas y las entradas positivas
select coalesce(sum(t.movementqty),0) as qty
from m_transaction t
inner join m_inoutline iol on iol.m_inoutline_id = t.m_inoutline_id
inner join m_inout io on io.m_inout_id = iol.m_inout_id
inner join c_doctype dt on dt.c_doctype_id = io.c_doctype_id
inner join c_orderline ol on ol.c_orderline_id = iol.c_orderline_id
inner join c_order o on o.c_order_id = ol.c_order_id
where t.ad_client_id = clientid
and t.ad_org_id = orgid
and t.m_product_id = productid
and t.m_locator_id = locatorid
and dt.reservestockmanagment = 'Y'
and o.dateordered::date <= dateTo::date
and t.movementdate::date <= dateTo::date
and t.movementdate::date <= current_date
union all
--Cantidades transferidas
select coalesce(sum(ol.qtyordered * -1),0) as qty
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_orderline rl on rl.c_orderline_id = ol.ref_orderline_id
inner join c_order r on r.c_order_id = rl.c_order_id
inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and o.issotrx = 'Y'
and dt.doctypekey IN ('SOSOT')
and r.dateordered::date <= dateTo::date
and o.dateordered::date <= dateTo::date
and o.dateordered::date <= current_date
) todo;
END IF;*/

return reserved;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getqtyreserved(integer, integer, integer, integer, date)
  OWNER TO libertya;
  
 --Update reserved dateto
CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer,
    dateto date)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados. 
IMPORTANTE: No funciona para artículos que no son ITEMS (Stockeables)
*/
BEGIN
	update m_storage s
	set qtyreserved = getqtyreserved(s.ad_client_id, s.ad_org_id, s.m_locator_id, s.m_product_id, dateto)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer, date)
  OWNER TO libertya;
  
-- Update reserved 
CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados. 
IMPORTANTE: No funciona para artículos que no son ITEMS (Stockeables)
*/
BEGIN
	perform update_reserved(clientid, orgid, productid, null::date);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer)
  OWNER TO libertya;
  
--20180905-1015 Mejoras a las funciones de pendientes de pago y factura para que tome el signo del importe original
CREATE OR REPLACE FUNCTION paymentavailable(
    p_c_payment_id integer,
    dateto timestamp without time zone)
  RETURNS numeric AS
$BODY$
DECLARE
v_Currency_ID INTEGER;
v_AvailableAmt NUMERIC := 0;
  v_IsReceipt CHARACTER(1);
  v_Amt NUMERIC := 0;
  v_PayAmt NUMERIC := 0;
  r RECORD;
v_Charge_ID INTEGER;
v_ConversionType_ID INTEGER;
v_allocatedAmt NUMERIC; -- candida alocada total convertida a la moneda de la linea
v_DateAcct timestamp without time zone;
BEGIN
BEGIN

SELECT C_Currency_ID, PayAmt, IsReceipt,
C_Charge_ID,C_ConversionType_ID, DateAcct
INTO STRICT
v_Currency_ID, v_PayAmt, v_IsReceipt,
v_Charge_ID,v_ConversionType_ID, v_DateAcct
FROM C_Payment
WHERE C_Payment_ID = p_C_Payment_ID;
EXCEPTION
WHEN OTHERS THEN
  RAISE NOTICE 'PaymentAvailable - %', SQLERRM;
RETURN NULL;
END;

IF (v_Charge_ID > 0 ) THEN
RETURN 0;
END IF;

v_allocatedAmt := 0;
FOR r IN
SELECT a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
FROM C_AllocationLine al
INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
WHERE al.C_Payment_ID = p_C_Payment_ID
  AND a.IsActive='Y'
  AND (dateTo IS NULL OR a.dateacct::date <= dateTo::date)
LOOP
v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID,v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
v_allocatedAmt := v_allocatedAmt + v_Amt;
END LOOP;

-- esto supone que las alocaciones son siempre no negativas; si esto no pasa, se van a retornar valores que no van a tener sentido
v_AvailableAmt := ABS(v_PayAmt) - abs(v_allocatedAmt);
-- v_AvailableAmt aca DEBE ser NO Negativo si admeas, las suma de las alocaciones nunca superan el monto del pago
-- de cualquiera manera, por "seguridad", si el valor es negativo, se corrige a cero
IF (v_AvailableAmt < 0) THEN
RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ;
v_AvailableAmt := 0;
END IF;

--  el resultado debe ser 0 o de lo contrario tener el mismo signo que el payment
IF (v_PayAmt < 0) THEN
	v_AvailableAmt := v_AvailableAmt * -1::numeric;
END IF;

v_AvailableAmt := currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
RETURN v_AvailableAmt;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION paymentavailable(integer, timestamp without time zone)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION invoiceopen(
    p_c_invoice_id integer,
    p_c_invoicepayschedule_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer,
    p_dateto timestamp without time zone)
  RETURNS numeric AS
$BODY$ /*************************************************************************  * The contents of this file are subject to the Compiere License.  You may  * obtain a copy of the License at    http://www.compiere.org/license.html  * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either  * express or implied. See the License for details. Code: Compiere ERP+CRM  * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.  *  * converted to postgreSQL by Karsten Thiemann (Schaeffer AG),   * kthiemann@adempiere.org  *************************************************************************  ***  * Title:	Calculate Open Item Amount in Invoice Currency  * Description:  *	Add up total amount open for C_Invoice_ID if no split payment.  *  Grand Total minus Sum of Allocations in Invoice Currency  *  *  For Split Payments:  *  Allocate Payments starting from first schedule.  *  Cannot be used for IsPaid as mutating  *  * Test:  * 	SELECT C_InvoicePaySchedule_ID, DueAmt FROM C_InvoicePaySchedule WHERE C_Invoice_ID=109 ORDER BY DueDate;  * 	SELECT invoiceOpen (109, null) FROM AD_System; - converted to default client currency  * 	SELECT invoiceOpen (109, 11) FROM AD_System; - converted to default client currency  * 	SELECT invoiceOpen (109, 102) FROM AD_System;  * 	SELECT invoiceOpen (109, 103) FROM AD_System;  ***  * Pasado a Libertya a partir de Adempiere 360LTS  * - ids son de tipo integer, no numeric  * - TODO : tema de las zonas en los timestamp  * - Excepciones en SELECT INTO requieren modificador STRICT bajo PostGreSQL o usar  * NOT FOUND  * - Por ahora, el "ignore rounding" se hace como en libertya (-0.01,0.01),  * en vez de usar la precisión de la moneda  * - Se toma el tipo de conversion de la factura, auqneu esto es dudosamente correcto  * ya que otras funciones , en particular currencyBase nunca tiene en cuenta  * este valor  * - Como en Libertya se tiene en cuenta tambien C_Invoice_Credit_ID para calcular  * la cantidad alocada a una factura (aunque esto es medio dudoso....)  * - No se soporta la fecha como 3er parametro (en realidad, tampoco se esta  * usando actualmente, y se deberia poder resolver de otra manera)  * - Libertya parece tener un bug al filtrar por C_InvoicePaySchedule_ID al calcular  * el granTotal (el granTotal SIEMPRE es el total de la factura, tomada directamente  * de C_Invoice.GranTotal o a partir de la suma de los DueAmt en C_InvoicePaySchedule);  * se usa la sentencia como esta en Adempeire (esto es, solo se filtra por C_Invoice_ID)  * - Nuevo enfoque: NO se usa ni la vista C_Invoice_V ni multiplicadores  * se asume todo positivo...  * - El resultado SIEMPRE deberia ser positivo y en el intervalo [0..GrandTotal]  * - 03 julio: se pasa a usar getAllocatedAmt para hacer esta funcion consistente  * con invoicePaid  * - 03 julio: se pasa de usar STRICT a NOT FOUND; es mas eficiente  ************************************************************************/ 
DECLARE 	
v_Currency_ID		INTEGER := p_c_currency_id; 	
v_GrandTotal	  	NUMERIC := 0; 	
v_TotalOpenAmt  	NUMERIC := 0; 	
v_PaidAmt  	        NUMERIC := 0; 	
v_Remaining	        NUMERIC := 0;    	
v_Precision            	NUMERIC := 0;    	
v_Min            	NUMERIC := 0.01;     	
s			RECORD; 	
v_ConversionType_ID INTEGER := p_c_conversiontype_id;  	
v_Date timestamp with time zone := ('now'::text)::timestamp(6);                

BEGIN 	 	

SELECT	currencyConvert(GrandTotal, I.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, I.AD_Client_ID, I.AD_Org_ID) as GrandTotal, 	
	(SELECT StdPrecision FROM C_Currency C WHERE C.C_Currency_ID = I.C_Currency_ID) AS StdPrecision  	
	INTO v_TotalOpenAmt, v_Precision 	
FROM	C_Invoice I 
WHERE	I.C_Invoice_ID = p_C_Invoice_ID; 	

IF NOT FOUND THEN  
	RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID; 		
	RETURN NULL; 	
END IF; 	      	 	 	 	

v_GrandTotal := v_TotalOpenAmt;
v_PaidAmt := getAllocatedAmt(p_C_Invoice_ID,v_Currency_ID,v_ConversionType_ID,1,p_dateto); 

IF (p_C_InvoicePaySchedule_ID > 0) THEN 
	v_Remaining := abs(v_PaidAmt);         
	FOR s IN  SELECT  ips.C_InvoicePaySchedule_ID, currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt 	        
		FROM    C_InvoicePaySchedule ips 	        
		INNER JOIN C_Invoice i on (ips.C_Invoice_ID = i.C_Invoice_ID) 		
		WHERE	ips.C_Invoice_ID = p_C_Invoice_ID AND   ips.IsValid='Y'         	
		ORDER BY ips.DueDate         
	LOOP             

		IF (s.C_InvoicePaySchedule_ID = p_C_InvoicePaySchedule_ID) THEN                 
			v_TotalOpenAmt := abs(s.DueAmt) - abs(v_Remaining);
			IF (v_TotalOpenAmt < 0) THEN                     
				v_TotalOpenAmt := 0;                  
			END IF; 				
			EXIT;              
		ELSE                  
			v_Remaining := abs(v_Remaining) - abs(s.DueAmt);     
			IF (v_Remaining < 0) THEN         
				v_Remaining := 0;                 
			END IF;             
		END IF;         
	END LOOP;     
ELSE         
	v_TotalOpenAmt := abs(v_TotalOpenAmt) - abs(v_PaidAmt);     
END IF; 	 	

IF (v_TotalOpenAmt >= -v_Min AND v_TotalOpenAmt <= v_Min) THEN 		
	v_TotalOpenAmt := 0; 	
END IF; 	 	

--  el resultado debe ser 0 o de lo contrario tener el mismo signo que el comprobante 
IF (v_GrandTotal < 0) THEN
	v_TotalOpenAmt := v_TotalOpenAmt * -1::numeric;
END IF;

v_TotalOpenAmt := ROUND(COALESCE(v_TotalOpenAmt,0), v_Precision);

RETURN	v_TotalOpenAmt; 

END; 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer, integer, integer, timestamp without time zone)
  OWNER TO libertya;

--20180912-1355 Mejoras a las funciones de actualización y obtención de cantidades reservadas
CREATE OR REPLACE FUNCTION getqtyreserved(
    clientid integer,
    orgid integer,
    locatorid integer,
    productid integer,
    dateto date)
  RETURNS numeric AS
$BODY$
/***********
Obtiene la cantidad reservada a fecha de corte. Si no hay fecha de corte, entonces se devuelven los pendientes actuales.
Por lo pronto no se utiliza el pendiente a fecha de corte ya que primero deberíamos analizar e implementar 
una forma en la que se determine cuando un pedido fue completo, anulado, etc.
*/
DECLARE
reserved numeric;
BEGIN
reserved := 0;
--Si no hay fecha de corte o es mayor o igual a la fecha actual, entonces se suman las cantidades reservadas de los pedidos
--if ( dateTo is null OR dateTo >= current_date ) THEN
SELECT INTO reserved coalesce(sum(ol.qtyreserved),0)
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_doctype dto on dto.c_doctype_id = o.c_doctypetarget_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid 
and ol.qtyreserved > 0
and o.docstatus in ('CO','CL')
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and (dateto is null or o.dateordered::date <= dateto::date)
and o.issotrx = 'Y'
and dto.doctypekey <> 'SOSOT';
/*ELSE
SELECT INTO reserved coalesce(sum(qty),0)
from (
-- Cantidad pedida a fecha de corte
select coalesce(sum(ol.qtyordered),0) as qty
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_doctype dt on dt.c_doctype_id = o.c_doctypetarget_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and o.issotrx = 'Y'
and dt.doctypekey NOT IN ('SOSOT')
and o.dateordered::date <= dateTo::date
and o.dateordered::date <= current_date
union all
-- Notas de crédito con (o sin) el check Actualizar Cantidades de Pedido
select coalesce(sum(il.qtyinvoiced),0) as qty
from c_invoiceline il
inner join c_invoice i on i.c_invoice_id = il.c_invoice_id
inner join c_doctype dt on dt.c_doctype_id = i.c_doctypetarget_id
inner join c_orderline ol on ol.c_orderline_id = il.c_orderline_id
inner join c_order o on o.c_order_id = ol.c_order_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and i.issotrx = 'Y'
and il.m_inoutline_id is null
and dt.signo_issotrx = '-1'
and o.dateordered::date <= dateTo::date
and i.dateinvoiced::date > dateTo::date
and i.dateinvoiced::date <= current_date
union all
--En transaction las salidas son negativas y las entradas positivas
select coalesce(sum(t.movementqty),0) as qty
from m_transaction t
inner join m_inoutline iol on iol.m_inoutline_id = t.m_inoutline_id
inner join m_inout io on io.m_inout_id = iol.m_inout_id
inner join c_doctype dt on dt.c_doctype_id = io.c_doctype_id
inner join c_orderline ol on ol.c_orderline_id = iol.c_orderline_id
inner join c_order o on o.c_order_id = ol.c_order_id
where t.ad_client_id = clientid
and t.ad_org_id = orgid
and t.m_product_id = productid
and t.m_locator_id = locatorid
and dt.reservestockmanagment = 'Y'
and o.dateordered::date <= dateTo::date
and t.movementdate::date <= dateTo::date
and t.movementdate::date <= current_date
union all
--Cantidades transferidas
select coalesce(sum(ol.qtyordered * -1),0) as qty
from c_orderline ol
inner join c_order o on o.c_order_id = ol.c_order_id
inner join c_orderline rl on rl.c_orderline_id = ol.ref_orderline_id
inner join c_order r on r.c_order_id = rl.c_order_id
inner join c_doctype dt on dt.c_doctype_id = o.c_doctype_id
inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
where o.ad_client_id = clientid
and o.ad_org_id = orgid
and o.processed = 'Y' 
and ol.m_product_id = productid
and l.m_locator_id = locatorid
and o.issotrx = 'Y'
and dt.doctypekey IN ('SOSOT')
and r.dateordered::date <= dateTo::date
and o.dateordered::date <= dateTo::date
and o.dateordered::date <= current_date
) todo;
END IF;*/

return reserved;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getqtyreserved(integer, integer, integer, integer, date)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer,
    dateto date)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados. 
IMPORTANTE: No funciona para artículos que no son ITEMS (Stockeables)
*/
BEGIN
	--Seteamos a 0 todo
	update m_storage s
	set qtyreserved = 0
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid);
		
	--Actualizamos el reservado
	update m_storage s
	set qtyreserved = getqtyreserved(clientid, s.ad_org_id, s.m_locator_id, s.m_product_id, dateto)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid)
		and exists (select ol.c_orderline_id
				from c_orderline ol
				join c_order o on o.c_order_id = ol.c_order_id
				join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
				join m_locator l on l.m_warehouse_id = w.m_warehouse_id
				where l.m_locator_id = s.m_locator_id 
					and ol.m_product_id = s.m_product_id 
					and ol.qtyreserved > 0);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer, date)
  OWNER TO libertya;
  
--20180928-1810 Cuando tenemos extracash en los allocations, se duplican los importes para tarjetas
CREATE OR REPLACE FUNCTION c_posjournal_c_payment_v_filtered(posjournalids anyarray)
  RETURNS SETOF c_posjournal_c_payment_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_c_payment_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND p.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, p.tendertype::character varying AS tendertype, p.documentno, p.description, ((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text))::character varying AS info, COALESCE(currencyconvert((case when ah.allocationtype in (''OPA'',''RCA'') then p.payamt else al.amount + al.discountamt + al.writeoffamt end), (case when ah.allocationtype in (''OPA'',''RCA'') then p.c_currency_id else ah.c_currency_id end), ah.c_currency_id, ah.dateacct, null::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, (case when ah.allocationtype in (''OPA'',''RCA'') then ''Anticipo'' else i.documentno end) AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, al.changeamt, ah.c_currency_id
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   WHERE 1=1 ' || wherePosJournalIDs || ' AND NOT EXISTS (SELECT ala.c_payment_id FROM c_allocationline as ala INNER JOIN c_allocationhdr as aha on aha.c_allocationhdr_id = ala.c_allocationhdr_id WHERE ala.c_payment_id = p.c_payment_id AND aha.allocationtype in (''OPA'',''RCA'') AND aha.c_allocationhdr_id <> ah.c_allocationhdr_id) ' || 
   ' UNION ALL 
        ( SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::integer AS c_invoice_id, p.c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text)))::character varying(255) AS info, p.payamt AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, 0 AS changeamt, p.c_currency_id
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  WHERE 1=1 ' || wherePosJournalIDs || 
  ' AND NOT (EXISTS ( SELECT al.c_allocationline_id
    FROM c_allocationline al
   WHERE al.c_payment_id = p.c_payment_id))
  ORDER BY p.tendertype::character varying(2), p.documentno);';

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_payment_v_filtered(anyarray)
  OWNER TO libertya;
  
--20181003-1245 Códigos o Cupones Promocionales 
update ad_system set dummy = (SELECT addcolumnifnotexists('c_promotion','promotiontype','character(1)'));

UPDATE c_promotion
set promotiontype = 'G'
where ad_client_id = 1010016;

ALTER TABLE c_promotion ALTER COLUMN promotiontype SET NOT NULL;
update ad_system set dummy = (SELECT addcolumnifnotexists('c_promotion','maxpromotionalcodes','integer NOT NULL DEFAULT 0'));

update ad_system set dummy = (SELECT addcolumnifnotexists('m_discountconfig','maxpromotionalcoupons','integer NOT NULL DEFAULT 0'));

CREATE TABLE c_promotion_code_batch
(
c_promotion_code_batch_id integer NOT NULL,
ad_client_id integer NOT NULL,
ad_org_id integer NOT NULL,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
createdby integer NOT NULL,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
updatedby integer NOT NULL,
datetrx timestamp without time zone NOT NULL,
description character varying(255),
voidbatch character(1) NOT NULL DEFAULT 'N'::bpchar,
processed character(1) NOT NULL DEFAULT 'N'::bpchar,
documentno character varying(30) NOT NULL,
CONSTRAINT c_promotion_code_batch_key PRIMARY KEY (c_promotion_code_batch_id),
CONSTRAINT client_promotion_code_batch FOREIGN KEY (ad_client_id)
REFERENCES ad_client (ad_client_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT org_promotion_code_batch FOREIGN KEY (ad_org_id)
REFERENCES ad_org (ad_org_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE c_promotion_code_batch
OWNER TO libertya;

CREATE TABLE c_promotion_code
(
c_promotion_code_id integer NOT NULL,
ad_client_id integer NOT NULL,
ad_org_id integer NOT NULL,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
createdby integer NOT NULL,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
updatedby integer NOT NULL,
c_promotion_id integer NOT NULL,
code character varying(255) NOT NULL,
validfrom timestamp without time zone NOT NULL,
validto timestamp without time zone,
c_promotion_code_batch_id integer NOT NULL,
used character(1) NOT NULL DEFAULT 'N'::bpchar,
processed character(1) NOT NULL DEFAULT 'N'::bpchar,
c_invoice_id integer,
CONSTRAINT c_promotion_code_key PRIMARY KEY (c_promotion_code_id),
CONSTRAINT c_promotion_code_invoice FOREIGN KEY (c_invoice_id)
REFERENCES c_invoice (c_invoice_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT client_promotion_code FOREIGN KEY (ad_client_id)
REFERENCES ad_client (ad_client_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT org_promotion_code FOREIGN KEY (ad_org_id)
REFERENCES ad_org (ad_org_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT promotion_promotion_code FOREIGN KEY (c_promotion_id)
REFERENCES c_promotion (c_promotion_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT promotion_promotion_code_batch FOREIGN KEY (c_promotion_code_batch_id)
REFERENCES c_promotion_code_batch (c_promotion_code_batch_id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
OIDS=FALSE
);
ALTER TABLE c_promotion_code
OWNER TO libertya;

--20181105-1630 Agregar o no validación de acceso a organizaciones para Crear Desde de Facturas
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role','addsecurityvalidation_createfrominvoice','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20181105-1725 Agregar filtro de tipos de documento electrónicos en Resumen de Ventas
CREATE OR REPLACE FUNCTION v_dailysales_v2_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereDatePayments varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	wherePOSPayments varchar;
	whereUserInvoices varchar;
	whereUserPayments varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDatePayments = '';
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDatePayments = ' AND date_trunc(''day'', pjp.allocationdate) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDatePayments = whereDatePayments || ' AND date_trunc(''day'', pjp.allocationdate) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSPayments = ' AND (' || posID || ' = -1 OR COALESCE(pjh.c_pos_id, pj.c_pos_id) = ' || posID || ')';
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserPayments = ' AND (' || userID || ' = -1 OR COALESCE(pjh.ad_user_id, pj.ad_user_id) = ' || userID || ')';
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND (dtc.iselectronic is null OR dtc.iselectronic = ''N'' OR (dtc.iselectronic = ''Y'' AND i.cae is not null)) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate;

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = '(        (         SELECT ''P''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM ' || posJournalPaymentsFrom || ' pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND (date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) OR (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]))) AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = ''Y''::bpchar))
                UNION ALL 
                         SELECT ''NCC''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                CASE
                                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                                    ELSE i.paymentrule
                                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN i.paymentrule::text = ''P''::text THEN NULL::integer
                                    ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                       FROM ad_ref_list
                                      WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                     LIMIT 1)
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                                    WHEN i.paymentrule::text = ''P''::text THEN NULL::character varying
                                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                                    ELSE i.paymentrule
                                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                 JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND dtc.docbasetype = ''ARC''::bpchar AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_credit_id = i.c_invoice_id)))
        UNION ALL 
                 SELECT ''PA''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                            ELSE i.c_invoice_id
                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                            ELSE pjp.c_invoice_credit_id
                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                            ELSE p.c_pospaymentmedium_id
                        END AS c_pospaymentmedium_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                            ELSE ppm.name
                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                   FROM ' || posJournalPaymentsFrom || ' pjp
              JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
         LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
    JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND (date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = ''N''::bpchar AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = ''Y''::bpchar)))
UNION ALL 
         SELECT ''ND''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                CASE
                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                    ELSE i.paymentrule
                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                   FROM ad_ref_list
                  WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                 LIMIT 1) AS c_pospaymentmedium_id, 
                CASE
                    WHEN i.paymentrule::text = ''T''::text OR i.paymentrule::text = ''Tr''::text THEN ''A''::character varying
                    WHEN i.paymentrule::text = ''B''::text THEN ''CA''::character varying
                    WHEN i.paymentrule::text = ''K''::text THEN ''C''::character varying
                    WHEN i.paymentrule::text = ''P''::text THEN ''CC''::character varying
                    WHEN i.paymentrule::text = ''S''::text THEN ''K''::character varying
                    ELSE i.paymentrule
                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND "position"(dtc.doctypekey::text, ''CDN''::text) = 1 AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id));';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_v2_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--Invoices
CREATE OR REPLACE FUNCTION v_dailysales_invoices_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	whereUserInvoices varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND (dtc.iselectronic is null OR dtc.iselectronic = ''N'' OR (dtc.iselectronic = ''Y'' AND i.cae is not null)) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate || whereDateInvoices || wherePOSInvoices || whereUserInvoices;

	-- Armar la consulta
	consulta = 'SELECT ''I''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, dtc.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dtc.c_doctype_id AS c_pospaymentmedium_id, dtc.name AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd ||
  ' AND NOT (
  		EXISTS (
			SELECT * FROM (
				SELECT *
				FROM c_allocationline al
				WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = ''Y''::bpchar 
			) as FOO
			JOIN c_payment p ON p.c_payment_id = foo.c_payment_id
			JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id
		)
	);';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_invoices_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--Current account filtered
CREATE OR REPLACE FUNCTION v_dailysales_current_account_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDateInvoices varchar;
	whereInvoiceDate varchar;
	wherePOSInvoices varchar;
	whereUserInvoices varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;
	
	-- Fecha de factura
	whereInvoiceDate = '';
	if addInvoiceDate then
		if invoiceDateFrom is not null then
			whereInvoiceDate = ' AND date_trunc(''day'', i.dateacct) >= date_trunc(''day'', '''|| invoiceDateFrom || '''::date)';
		end if;
		if invoiceDateTo is not null then
			whereInvoiceDate = whereInvoiceDate || ' AND date_trunc(''day'', i.dateacct) <= date_trunc(''day'', ''' || invoiceDateTo || '''::date) ';
		end if;
	end if;

	-- Fechas para allocations y facturas
	whereDateInvoices = '';
	if dateFrom is not null then
		whereDateInvoices = ' AND date_trunc(''day''::text, i.dateinvoiced) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDateInvoices = whereDateInvoices || ' AND date_trunc(''day''::text, i.dateinvoiced) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSInvoices = ' AND (' || posID || ' = -1 OR pj.c_pos_id = ' || posID || ')';

	-- Usuario
	whereUserInvoices = ' AND (' || userID || ' = -1 OR pj.ad_user_id = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dt.isfiscaldocument = ''Y'' ' || 
			 ' AND (dt.isfiscal is null OR dt.isfiscal = ''N'' OR (dt.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND (dt.iselectronic is null OR dt.iselectronic = ''N'' OR (dt.iselectronic = ''Y'' AND i.cae is not null)) ' ||
			 ' AND dt.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dt.transactiontypefrontliva is null OR dt.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate;

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = 'SELECT ''CAI''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount
                 FROM ' || posJournalPaymentsFrom || ' pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) AND hdr.isactive = ''Y''::bpchar OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND hdr.isactive = ''N''::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND "position"(dt.doctypekey::text, ''CDN''::text) < 1
UNION ALL 
         SELECT ''CAIA''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id)::numeric(20,2) - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::integer AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount
                 FROM ' || posJournalPaymentsFrom || ' pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND date_trunc(''day''::text, i.dateacct) = date_trunc(''day''::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])) AND hdr.isactive = ''Y''::bpchar OR (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) AND hdr.isactive = ''N''::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd || whereDateInvoices || whereUserInvoices || wherePOSInvoices ||
  ' AND (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND "position"(dt.doctypekey::text, ''CDN''::text) < 1 AND (i.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar]));';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--Current accounts payment
CREATE OR REPLACE FUNCTION v_dailysales_current_account_payments_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	whereDatePayments varchar;
	wherePOSPayments varchar;
	whereUserPayments varchar;
	whereOrg varchar;
	whereClauseStd varchar;
	posJournalPaymentsFrom varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado de las condiciones en base a los parámetros
	-- Organización
	whereOrg = '';
	if orgID is not null AND orgID > 0 THEN
		whereOrg = ' AND i.ad_org_id = ' || orgID;
	END IF;

	-- Fechas para allocations y facturas
	whereDatePayments = '';
	if dateFrom is not null then
		whereDatePayments = ' AND date_trunc(''day'', pjp.allocationdate) >= date_trunc(''day'', ''' || dateFrom || '''::date)';
	end if;

	if dateTo is not null then
		whereDatePayments = whereDatePayments || ' AND date_trunc(''day'', pjp.allocationdate) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;
	
	-- TPV
	wherePOSPayments = ' AND (' || posID || ' = -1 OR COALESCE(pjh.c_pos_id, pj.c_pos_id) = ' || posID || ')';

	-- Usuario
	whereUserPayments = ' AND (' || userID || ' = -1 OR COALESCE(pjh.ad_user_id, pj.ad_user_id) = ' || userID || ')';

	-- Condiciones básicas del reporte
	whereClauseStd = ' ( i.issotrx = ''Y'' ' ||
			 whereOrg || 
			 ' AND (i.docstatus = ''CO'' or i.docstatus = ''CL'' or i.docstatus = ''RE'' or i.docstatus = ''VO'' OR i.docstatus = ''??'') ' ||
			 ' AND dtc.isfiscaldocument = ''Y'' ' || 
			 ' AND (dtc.isfiscal is null OR dtc.isfiscal = ''N'' OR (dtc.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'')) ' ||
			 ' AND (dtc.iselectronic is null OR dtc.iselectronic = ''N'' OR (dtc.iselectronic = ''Y'' AND i.cae is not null)) ' ||
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' AND (dtc.transactiontypefrontliva is null OR dtc.transactiontypefrontliva = ''S'') ' || 
			 ' ) ';

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = 'SELECT ''PCA''::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
            ELSE i.c_invoice_id
        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
            ELSE pjp.c_invoice_credit_id
        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, currencybase(pjp.amount, i.c_currency_id, pjp.dateacct, pjp.ad_client_id, pjp.ad_org_id)::numeric(20,2) as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
            ELSE p.c_pospaymentmedium_id
        END AS c_pospaymentmedium_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
            WHEN (dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
            ELSE ppm.name
        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM ' || posJournalPaymentsFrom || ' pjp
   JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE ' || whereClauseStd || whereDatePayments || whereUserPayments || wherePOSPayments ||
  ' AND date_trunc(''day''::text, i.dateacct) <> date_trunc(''day''::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = ''Y''::bpchar AND ((dtc.docbasetype <> ALL (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) OR (dtc.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_payments_filtered(integer, integer, integer, date, date)
  OWNER TO libertya;
  
--20181115-1545 Nueva función para determinar la cantidad facturada en devoluciones
CREATE OR REPLACE FUNCTION getInvoicedQtyReturned(orderlineID integer)
  RETURNS numeric AS
$BODY$
DECLARE
	qty numeric;
BEGIN
	select into qty sum(il.qtyinvoiced)
	from c_orderline as ol 
	inner join m_inoutline as iol on iol.c_orderline_id = ol.c_orderline_id 
	inner join m_inout as io on io.m_inout_id = iol.m_inout_id 
	inner join c_doctype as dt on dt.c_doctype_id = io.c_doctype_id 
	inner join c_invoiceline as il on il.m_inoutline_id = iol.m_inoutline_id 
	inner join c_invoice as i on i.c_invoice_id = il.c_invoice_id 
	where ol.c_orderline_id = orderlineID AND dt.doctypekey = 'DC' and io.docstatus IN ('CL','CO') and i.docstatus IN ('CL','CO');

	if (qty is null) then qty = 0; end if;
	
	return qty;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getInvoicedQtyReturned(integer)
  OWNER TO libertya;
  
--20181128-1325 Incorporación de permisos por perfil para visualizar las pestañas Pendente de Recibir y de Entrega en ventana Historial, accesible desde los Info Product e Info BPartner
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role','allow_info_product_reserved_tab','character(1) NOT NULL DEFAULT ''Y''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role','allow_info_product_ordered_tab','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20181128-1355 Incorporación de permisos por perfil para visualizar las opciones de Creación y Actualización de Entidades Comerciales en el campo
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role','lookup_allow_bpartner_create_menu','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20181129-1730 Funview correspondiente a la view v_product_movements_detailed. Mejoras de performance
--TYPE v_product_movements_detailed_type
CREATE TYPE v_product_movements_detailed_type AS (movement_table text, ad_client_id integer, ad_org_id integer, 
		m_locator_id integer, m_warehouse_id integer, warehouse_value varchar(40), warehouse_name varchar(60), 
		receiptvalue text, movementdate timestamp, doctypename varchar(60), documentno varchar(60), 
		docstatus character(2), m_product_id integer, product_value varchar(40), product_name varchar(60), 
		qty numeric(22,4), c_order_id integer, created timestamp, updated timestamp);
	
--FUNCTION v_product_movements_detailed_filtered(integer)
CREATE OR REPLACE FUNCTION v_product_movements_detailed_filtered(productID integer)
  RETURNS SETOF v_product_movements_detailed_type AS
$BODY$
declare
	consulta varchar;
	productCondition varchar;
	adocument v_product_movements_detailed_type;
BEGIN
	-- Armar la condición por el artículo
	productCondition = '(' || $1 || ' <= 0 or m_product_id = ' || $1 || ')';
	-- Armar la consulta
	consulta = ' SELECT t.movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, w.m_warehouse_id, w.value AS warehouse_value, w.name AS warehouse_name, t.receiptvalue, t.movementdate, t.doctypename, t.documentno, t.docstatus, t.m_product_id, p.value as product_value, p.name as product_name, t.qty, t.c_order_id, t.created, t.updated
from (

SELECT t.ad_client_id, t.ad_org_id, t.m_locator_id, 
	t.movementdate, t.m_product_id, io.c_order_id, 
	CASE 
	WHEN t.movementqty > 0 THEN ''Y''::text
	ELSE ''N''::text
	END AS receiptvalue,
	abs(t.movementqty) AS qty, 
	CASE 
	WHEN iol.m_inoutline_id IS NOT NULL THEN ''M_InOut'' 
	WHEN ml.m_movementline_id IS NOT NULL THEN ''M_Movement'' 
	WHEN tr.m_transfer_id IS NOT NULL THEN ''M_Transfer''
	WHEN (sp.m_splitting_id IS NOT NULL OR spv.m_splitting_id IS NOT NULL) THEN ''M_Splitting''
	WHEN (pc.m_productchange_id IS NOT NULL OR pcv.m_productchange_id IS NOT NULL) THEN ''M_ProductChange''
	ELSE ''M_Inventory''
	END AS movement_table, 
	CASE 
	WHEN iol.m_inoutline_id IS NOT NULL THEN dtio.name
	WHEN ml.m_movementline_id IS NOT NULL THEN dtm.name
	WHEN tr.m_transfer_id IS NOT NULL THEN tr.transfertype
	WHEN (sp.m_splitting_id IS NOT NULL OR spv.m_splitting_id IS NOT NULL) THEN ''M_Splitting_ID''
	WHEN (pc.m_productchange_id IS NOT NULL OR pcv.m_productchange_id IS NOT NULL) THEN ''M_ProductChange_ID''
	ELSE dti.name
	END AS doctypename,
	CASE 
	WHEN iol.m_inoutline_id IS NOT NULL THEN io.documentno
	WHEN ml.m_movementline_id IS NOT NULL THEN m.documentno
	WHEN tr.m_transfer_id IS NOT NULL THEN tr.documentno
	WHEN sp.m_splitting_id IS NOT NULL THEN sp.documentno
	WHEN spv.m_splitting_id IS NOT NULL THEN spv.documentno
	WHEN pc.m_productchange_id IS NOT NULL THEN pc.documentno
	WHEN pcv.m_productchange_id IS NOT NULL THEN pcv.documentno
	ELSE i.documentno
	END AS documentno,
	CASE 
	WHEN iol.m_inoutline_id IS NOT NULL THEN io.docstatus
	WHEN ml.m_movementline_id IS NOT NULL THEN m.docstatus
	WHEN tr.m_transfer_id IS NOT NULL THEN tr.docstatus
	WHEN sp.m_splitting_id IS NOT NULL THEN sp.docstatus
	WHEN spv.m_splitting_id IS NOT NULL THEN spv.docstatus
	WHEN pc.m_productchange_id IS NOT NULL THEN pc.docstatus
	WHEN pcv.m_productchange_id IS NOT NULL THEN pcv.docstatus
	ELSE i.docstatus
	END AS docstatus,
	CASE 
	WHEN iol.m_inoutline_id IS NOT NULL THEN io.created 
	WHEN ml.m_movementline_id IS NOT NULL THEN m.created 
	WHEN tr.m_transfer_id IS NOT NULL THEN tr.created
	WHEN sp.m_splitting_id IS NOT NULL THEN sp.created
	WHEN spv.m_splitting_id IS NOT NULL THEN spv.created
	WHEN pc.m_productchange_id IS NOT NULL THEN pc.created 
	WHEN pcv.m_productchange_id IS NOT NULL THEN pcv.created
	ELSE i.created
	END AS created,
	CASE 
	WHEN iol.m_inoutline_id IS NOT NULL THEN io.updated 
	WHEN ml.m_movementline_id IS NOT NULL THEN m.updated 
	WHEN tr.m_transfer_id IS NOT NULL THEN tr.updated
	WHEN sp.m_splitting_id IS NOT NULL THEN sp.updated
	WHEN spv.m_splitting_id IS NOT NULL THEN spv.updated
	WHEN pc.m_productchange_id IS NOT NULL THEN pc.updated
	WHEN pcv.m_productchange_id IS NOT NULL THEN pcv.updated
	ELSE i.updated
	END AS updated
FROM (SELECT *
	FROM m_transaction
	WHERE ' || productCondition || ') t
LEFT JOIN m_inoutline iol ON iol.m_inoutline_id = t.m_inoutline_id
LEFT JOIN m_inout io ON io.m_inout_id = iol.m_inout_id
LEFT JOIN c_doctype dtio ON dtio.c_doctype_id = io.c_doctype_id

LEFT JOIN m_movementline ml ON ml.m_movementline_id = t.m_movementline_id
LEFT JOIN m_movement m ON m.m_movement_id = ml.m_movement_id
LEFT JOIN c_doctype dtm ON dtm.c_doctype_id = m.c_doctype_id

LEFT JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
LEFT JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
LEFT JOIN c_doctype dti ON dti.c_doctype_id = i.c_doctype_id

LEFT JOIN m_transfer tr ON tr.m_inventory_id = i.m_inventory_id
LEFT JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
LEFT JOIN m_splitting spv ON spv.void_inventory_id = i.m_inventory_id
LEFT JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id
LEFT JOIN m_productchange pcv ON pcv.void_inventory_id = i.m_inventory_id

UNION ALL

SELECT i.ad_client_id, i.ad_org_id, il.m_locator_id, 
	i.movementdate, il.m_product_id, NULL::integer AS c_order_id,
	CASE
	WHEN (il.qtycount - il.qtybook) >= 0::numeric THEN ''Y''::text
	ELSE ''N''::text
	END AS receiptvalue,
	abs(il.qtycount - il.qtybook) AS qty,
	''M_Inventory'' AS movement_table, 
	dt.name AS doctypename, i.documentno, i.docstatus, i.created, i.updated
FROM m_inventory i
JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
WHERE ' || productCondition || ' 
	and NOT EXISTS ( SELECT t.m_transaction_id
			  FROM m_transaction t
			  WHERE il.m_inventoryline_id = t.m_inventoryline_id)
) as t
JOIN m_product p on p.m_product_id = t.m_product_id
JOIN m_locator l ON l.m_locator_id = t.m_locator_id
JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id; ';

FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_product_movements_detailed_filtered(integer)
  OWNER TO libertya;

--VIEW v_product_movements_detailed
DROP VIEW v_product_movements_detailed;

CREATE OR REPLACE VIEW v_product_movements_detailed AS 
 select *
 from v_product_movements_detailed_filtered(-1);

ALTER TABLE v_product_movements_detailed
  OWNER TO libertya;
  
--20181204-1305 Nueva columna para permitir que las unidades de medida sean seleccionables para artículos
update ad_system set dummy = (SELECT addcolumnifnotexists('c_uom','productselectable','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--201812041339 Nueva función para redireccionar un UOM a otro junto con todos sus artículos y lineas de pedido, remito y factura
CREATE OR REPLACE FUNCTION redirect_um(clientID integer, orgID integer, x12de355_from varchar, x12de355_to varchar, postproductselectable boolean)
RETURNS void AS
$BODY$
/**
* Redireccionar desde la UM from a la UM to:
* - Los artículos 
* - Las líneas de remitos 
* - Las líneas de facturas
* - Las líneas de pedidos
* Luego se deja activo o desactivo la UM from dependiendo el parámetro postinactive.
*/
DECLARE
	productID integer;
	uomfromid integer;
	uomtoid integer;
	inoutlines integer;
	invoicelines integer;
	orderlines integer;
	products integer;
	uomactive varchar; 
BEGIN
	-- Obtener el ID del UOM desde en base al símbolo parámetro
	SELECT c_uom_id INTO uomfromid
	FROM c_uom 
	WHERE (ad_client_id = clientID OR ad_client_id = 0) and x12de355 = x12de355_from
	order by ad_client_id desc limit 1;

	IF uomfromid IS NULL OR uomfromid = 0 THEN 
		RAISE NOTICE 'Imposible determinar Unidad de Medida, x12de355 %', x12de355_from;
		RETURN;
	END IF;

	-- Obtener el ID del UOM hasta en base al símbolo parámetro
	SELECT c_uom_id INTO uomtoid 
	FROM c_uom 
	WHERE (ad_client_id = clientID OR ad_client_id = 0) and x12de355 = x12de355_to
	order by ad_client_id desc limit 1;
	
	IF uomtoid IS NULL OR uomtoid = 0 THEN 
		RAISE NOTICE 'Imposible determinar Unidad de Medida, x12de355 %', x12de355_to;
		RETURN;
	END IF;
	
	-- Se modifican las líneas de remitos, facturas y pedidos 
	-- para los artículos de la UOM desde que poseen esa UOM en la línea
	FOR productID IN 
		SELECT m_product_id 
		FROM m_product 
		WHERE ad_client_id = clientID and c_uom_id = uomfromid
		ORDER BY m_product_id
	LOOP
		RAISE NOTICE 'Articulo %', productID;
		
		-- Líneas de remito
		UPDATE m_inoutline il 
		SET c_uom_id = uomtoid
		WHERE m_product_id = productID AND c_uom_id = uomfromid AND ad_org_id = orgID 
			AND EXISTS (SELECT m_inout_id 
					FROM m_inout i 
					WHERE i.m_inout_id = il.m_inout_id and i.docstatus = 'CO');

		GET DIAGNOSTICS inoutlines = ROW_COUNT;
		RAISE NOTICE 'Lineas de remito actualizadas %', inoutlines;
		
		-- Líneas de factura
		UPDATE c_invoiceline il
		SET c_uom_id = uomtoid
		WHERE m_product_id = productID AND c_uom_id = uomfromid AND ad_org_id = orgID 
			AND EXISTS (SELECT c_invoice_id 
					FROM c_invoice i 
					WHERE i.c_invoice_id = il.c_invoice_id and i.docstatus = 'CO');

		GET DIAGNOSTICS invoicelines = ROW_COUNT;
		RAISE NOTICE 'Lineas de factura actualizadas %', invoicelines;
		
		-- Líneas de pedido
		UPDATE c_orderline il 
		SET c_uom_id = uomtoid
		WHERE m_product_id = productID AND c_uom_id = uomfromid AND ad_org_id = orgID 
			AND EXISTS (SELECT c_order_id 
					FROM c_order i 
					WHERE i.c_order_id = il.c_order_id and i.docstatus = 'CO');

		GET DIAGNOSTICS orderlines = ROW_COUNT;
		RAISE NOTICE 'Lineas de pedido actualizadas %', orderlines;
	END LOOP;

	-- Actualizar los artículos
	UPDATE m_product
	SET c_uom_id = uomtoid
	WHERE c_uom_id = uomfromid;

	-- Articulos actualizados
	GET DIAGNOSTICS products = ROW_COUNT;
	RAISE NOTICE 'Articulos actualizados %', products;

	-- Activar/Desactivar UM
	uomactive = 'N';
	IF postproductselectable THEN uomactive = 'Y'; END IF;

	UPDATE c_uom
	SET productselectable = uomactive
	WHERE c_uom_id = uomfromid;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION redirect_um(integer, integer, varchar, varchar, boolean)
  OWNER TO libertya;
  
-- 20181207-1153 Indice para bpartners en allocations
update ad_system set dummy = (SELECT addindexifnotexists('c_allocationhdr_bpartner','c_allocationhdr','c_bpartner_id'));

--20181221-1050 La unicidad de liquidaciones debe incluir la fecha de pago
ALTER TABLE c_creditcardsettlement DROP CONSTRAINT uniquecreditcardsettlement;
ALTER TABLE c_creditcardsettlement ADD CONSTRAINT uniquecreditcardsettlement UNIQUE (settlementno, c_bpartner_id, paymentdate);