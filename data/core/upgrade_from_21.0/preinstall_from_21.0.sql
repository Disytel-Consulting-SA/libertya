-- ========================================================================================
-- PREINSTALL FROM 21.0
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20210118-1435 Mejoras a la vista del libro mayor para que tenga prioridad la descripción del asiento contable antes de la descripción creada en ejecución
CREATE OR REPLACE VIEW v_diariomayor AS 
 SELECT fa.ad_client_id,
    fa.ad_org_id,
    fa.created,
    fa.createdby,
    fa.updated,
    fa.updatedby,
    ev.c_elementvalue_id,
    fa.dateacct,
    fa.journalno,
    ev.value,
    (ev.value::text || ' '::text) || ev.name::text AS name,
        CASE
	    WHEN fa.description is not null then fa.description
            WHEN t.name::text = 'Invoice'::text AND di.issotrx = 'N'::bpchar THEN ((('Referencia: '::text || di.poreference::text) || ' / '::text) || di.documentno::text)::character varying
            WHEN t.name::text = 'Invoice'::text AND di.issotrx = 'Y'::bpchar THEN ('Factura: '::text || di.documentno::text)::character varying
            WHEN t.name::text = 'Allocation'::text THEN ((dah.documentno::text || ' | '::text) || fa.description::text)::character varying
            WHEN t.name::text = 'GL Journal'::text THEN ((dglb.documentno::text || ' | '::text) || dglb.description::text)::character varying
            WHEN t.name::text = 'Payment'::text THEN ((dp.documentno::text || ' | '::text) || dp.description::text)::character varying
            ELSE ev.name
        END AS description,
    fa.amtacctdr AS debe,
    fa.amtacctcr AS haber,
    fa.amtacctdr - fa.amtacctcr AS saldo,
    t.name AS tablename,
    ttrl.name AS tr_tablename,
    fa.record_id,
        CASE
            WHEN t.name::text = 'Invoice'::text THEN di.documentno
            WHEN t.name::text = 'Allocation'::text THEN dah.documentno
            WHEN t.name::text = 'GL Journal'::text THEN dglb.documentno
            WHEN t.name::text = 'Payment'::text THEN dp.documentno
            ELSE NULL::character varying
        END AS documentno,
        CASE
            WHEN t.name::text = 'Invoice'::text THEN di.c_bpartner_id
            WHEN t.name::text = 'Payment'::text THEN dp.c_bpartner_id
            WHEN t.name::text = 'Allocation'::text THEN ( SELECT min(al.c_bpartner_id) AS min
               FROM c_allocationline al
              WHERE al.c_allocationhdr_id = dah.c_allocationhdr_id)
            ELSE NULL::integer
        END AS c_bpartner_id,
        CASE
            WHEN t.name::text = 'Invoice'::text THEN di.dateinvoiced
            WHEN t.name::text = 'Allocation'::text THEN dah.datetrx
            WHEN t.name::text = 'GL Journal'::text THEN dglb.datedoc
            WHEN t.name::text = 'Payment'::text THEN dp.datetrx
            ELSE NULL::timestamp without time zone
        END AS datedoc
   FROM c_elementvalue ev,
    ad_table t,
    fact_acct fa
     JOIN ad_table_trl ttrl ON fa.ad_table_id = ttrl.ad_table_id AND ttrl.ad_language::text = 'es_ES'::text
     LEFT JOIN c_invoice di ON fa.record_id = di.c_invoice_id::numeric
     LEFT JOIN c_allocationhdr dah ON fa.record_id = dah.c_allocationhdr_id::numeric
     LEFT JOIN c_payment dp ON fa.record_id = dp.c_payment_id::numeric
     LEFT JOIN gl_journal dgl ON fa.record_id = dgl.gl_journal_id::numeric
     LEFT JOIN gl_journalbatch dglb ON dgl.gl_journalbatch_id = dglb.gl_journalbatch_id
  WHERE fa.account_id = ev.c_elementvalue_id AND t.ad_table_id = fa.ad_table_id
  ORDER BY ev.name, fa.dateacct;

ALTER TABLE v_diariomayor
  OWNER TO libertya;

--20210217-1241 Cambio de lookups en vistas C_POSJournalInvoices_V C_POSJournalPayments_V y C_POSJournalPayments_V_Simple de TableDir a Busqueda para reduccion en tiempos de respuesta al abrir la ventana
update ad_column set ad_reference_id = 30 where ad_componentobjectuid in ('CORE-AD_Column-1014931', 'CORE-AD_Column-1014922', 'CORE-AD_Column-1014921', 'CORE-AD_Column-1014939', 'CORE-AD_Column-1014932', 'CORE-AD_Column-1014930', 'CORE-AD_Column-1016938', 'CORE-AD_Column-1016929', 'CORE-AD_Column-1016928', 'CORE-AD_Column-1016946', 'CORE-AD_Column-1016939',	'CORE-AD_Column-1016937', 'CORE-AD_Column-1014899', 'CORE-AD_Column-1014911', 'CORE-AD_Column-1014900', 'CORE-AD_Column-1014902');

--20210414-1700 Se invierte el signo en los movimientos bancarios
DROP VIEW c_payment_movements_v;

CREATE OR REPLACE VIEW c_payment_movements_v AS 
 SELECT p.c_payment_id,
    p.ad_client_id,
    p.ad_org_id,
    p.isactive,
    p.created,
    p.createdby,
    p.updated,
    p.updatedby,
    p.documentno,
    p.datetrx,
    p.isreceipt,
    p.c_doctype_id,
    p.trxtype,
    p.c_bankaccount_id,
    p.c_bpartner_id,
    p.c_invoice_id,
    p.c_bp_bankaccount_id,
    p.c_paymentbatch_id,
    p.tendertype,
    p.creditcardtype,
    p.creditcardnumber,
    p.creditcardvv,
    p.creditcardexpmm,
    p.creditcardexpyy,
    p.micr,
    p.routingno,
    p.accountno,
    p.checkno,
    p.a_name,
    p.a_street,
    p.a_city,
    p.a_state,
    p.a_zip,
    p.a_ident_dl,
    p.a_ident_ssn,
    p.a_email,
    p.voiceauthcode,
    p.orig_trxid,
    p.ponum,
    p.c_currency_id,
    p.payamt,
    p.discountamt,
    p.writeoffamt,
    p.taxamt,
    p.isapproved,
    p.r_pnref,
    p.r_result,
    p.r_respmsg,
    p.r_authcode,
    p.r_avsaddr,
    p.r_avszip,
    p.r_info,
    p.processing,
    p.oprocessing,
    p.docstatus,
    p.docaction,
    p.isreconciled,
    p.isallocated,
    p.isonline,
    p.processed,
    p.posted,
    p.isoverunderpayment,
    p.overunderamt,
    p.a_country,
    p.c_project_id,
    p.isselfservice,
    p.chargeamt,
    p.c_charge_id,
    p.isdelayedcapture,
    p.r_authcode_dc,
    p.r_cvv2match,
    p.r_pnref_dc,
    p.swipe,
    p.ad_orgtrx_id,
    p.c_campaign_id,
    p.c_activity_id,
    p.user1_id,
    p.user2_id,
    p.c_conversiontype_id,
    p.description,
    p.dateacct,
    p.c_order_id,
    p.isprepayment,
    p.ref_payment_id,
    p.checked,
    p.m_boletadeposito_id,
    p.issotrx,
    p.a_bank,
    p.a_cuit,
    p.duedate,
    p.couponnumber,
    p.m_entidadfinancieraplan_id,
    p.authcode,
    p.authmatch,
    p.c_posjournal_id,
    p.posnet,
    p.c_pospaymentmedium_id,
    p.c_bank_id,
    p.original_ref_payment_id,
    p.dateemissioncheck,
    p.couponbatchnumber,
    p.checkstatus,
    p.rejecteddate,
    p.rejectedcomments,
    p.bank_payment_documentno,
    p.bank_payment_date,
    p.c_bankpaymentstatus_id,
    p.auditstatus,
    p.ismanual,
    p.accounting_c_charge_id,
	CASE
            WHEN dt.signo_issotrx = 1 THEN p.payamt * -1
            ELSE p.payamt
        END AS payamtsign
   FROM c_payment p
     JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id;

ALTER TABLE c_payment_movements_v
  OWNER TO libertya;