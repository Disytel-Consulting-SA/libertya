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