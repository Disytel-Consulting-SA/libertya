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