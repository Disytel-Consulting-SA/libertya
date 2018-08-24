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