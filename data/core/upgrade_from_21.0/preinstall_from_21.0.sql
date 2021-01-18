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

