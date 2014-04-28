-- ========================================================================================
-- PREINSTALL FROM 14.02
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20140310-2252 Incorporación de clave de entidad financiera a la vista correspondiente
DROP VIEW v_dailysales;

CREATE OR REPLACE VIEW v_dailysales AS 
        (        (        (        (        (         SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                            ELSE i.c_invoice_id
                                                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                                            ELSE pjp.c_invoice_credit_id
                                                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                                            ELSE p.c_pospaymentmedium_id
                                                        END AS c_pospaymentmedium_id, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                                            ELSE ppm.name
                                                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
                                        UNION ALL 
                                                 SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_invoice i
                                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                    LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
                                                 FROM ( SELECT 
                                                              CASE
                                                                  WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                  ELSE i.c_invoice_id
                                                              END AS c_invoice_id, pjp.amount
                                                         FROM c_posjournalpayments_v pjp
                                                    JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                                               JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                          JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                                     JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                                JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
                           LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                      LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
                 LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
            LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
       LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
                                                GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
                               JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                          JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                     LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                    WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
                                UNION ALL 
                                         SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                           FROM c_invoice i
                                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                  LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id)
                        UNION ALL 
                                 SELECT 'PCA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                            ELSE i.c_invoice_id
                                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                            ELSE pjp.c_invoice_credit_id
                                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                            ELSE p.c_pospaymentmedium_id
                                        END AS c_pospaymentmedium_id, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                            ELSE ppm.name
                                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                   FROM c_posjournalpayments_v pjp
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
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL))
                UNION ALL 
                         SELECT 'NCC' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                CASE
                                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                    ELSE i.paymentrule
                                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN i.paymentrule::text = 'P'::text THEN NULL::integer
                                    ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                       FROM ad_ref_list
                                      WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                     LIMIT 1)
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                    WHEN i.paymentrule::text = 'P'::text THEN NULL::character varying
                                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                    ELSE i.paymentrule
                                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                 JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE dt.docbasetype = 'ARC'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_credit_id = i.c_invoice_id)))
        UNION ALL 
                 SELECT 'PA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                            ELSE i.c_invoice_id
                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                            ELSE pjp.c_invoice_credit_id
                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                            ELSE p.c_pospaymentmedium_id
                        END AS c_pospaymentmedium_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                            ELSE ppm.name
                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                   FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar)
UNION ALL 
         SELECT 'ND' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                CASE
                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                    ELSE i.paymentrule
                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                   FROM ad_ref_list
                  WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                 LIMIT 1) AS c_pospaymentmedium_id, 
                CASE
                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                    ELSE i.paymentrule
                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE "position"(dt.doctypekey::text, 'CDN'::text) = 1 AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id));

ALTER TABLE v_dailysales OWNER TO libertya;

-- 20140403: Calendario de pedidos
CREATE OR REPLACE VIEW V_OrdersSchedule AS (
SELECT o.ad_client_id, o.ad_org_id, o.c_order_id, o.documentno as orderdocumentno, o.issotrx, o.c_bpartner_id, o.datepromised::date, o.created::date, ol.c_orderline_id, ol.line, ol.m_product_id, ol.qtyentered, ol.c_uom_id, o.m_warehouse_id
FROM C_Order o
INNER JOIN C_OrderLine ol on ol.C_Order_ID = o.C_Order_ID
WHERE o.docstatus in ('CO', 'CL')
);

--20140403-1725 Flag para actualizar las cantidades pedidas del pedido 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','updateorderqty', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20140408-1000 Nuevas estructuras para registrar el log de comandos fiscales 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Controlador_Fiscal','logtyperecorded', 'character(1)'));

CREATE TABLE c_controlador_fiscal_log
(
  c_controlador_fiscal_log_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_controlador_fiscal_id integer NOT NULL,
  c_invoice_id integer,
  command character varying(100),
  response character varying(255),
  logtype character(1) NOT NULL,
  CONSTRAINT c_controlador_fiscal_log_key PRIMARY KEY (c_controlador_fiscal_log_id),
  CONSTRAINT adclient_c_controlador_fiscal_log FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT adorg_c_controlador_fiscal_log FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT controladorfiscal_c_controlador_fiscal_log FOREIGN KEY (c_controlador_fiscal_id)
      REFERENCES c_controlador_fiscal (c_controlador_fiscal_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cinvoice_c_controlador_fiscal_log FOREIGN KEY (c_invoice_id)
      REFERENCES c_invoice (c_invoice_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c_controlador_fiscal_log OWNER TO libertya;

--20140409-0900 Nueva columna para registrar si un esquema de retención es manual
update ad_system set dummy = (SELECT addcolumnifnotexists('C_RetencionSchema ','ismanual ', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20140409-0900 Permite que la columna c_retencionprocessor sea null para poder crear retenciones manuales
ALTER TABLE c_retencionschema ALTER COLUMN c_retencionprocessor_id DROP NOT NULL;

--20140409-1030 Nueva columna para registrar el tipo del asiento contable
update ad_system set dummy = (SELECT addcolumnifnotexists('fact_acct', 'TypeFactAcct', 'character varying(2)'));

--20140409-1545 Fix para que aparezcan las anulaciones de cuenta corriente
CREATE OR REPLACE VIEW v_dailysales AS 
        (        (        (        (        (         SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                            ELSE i.c_invoice_id
                                                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                                            ELSE pjp.c_invoice_credit_id
                                                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                                            ELSE p.c_pospaymentmedium_id
                                                        END AS c_pospaymentmedium_id, 
                                                        CASE
                                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                                            ELSE ppm.name
                                                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
                                        UNION ALL 
                                                 SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_invoice i
                                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                    LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
                                                 FROM ( SELECT 
                                                              CASE
                                                                  WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                  ELSE i.c_invoice_id
                                                              END AS c_invoice_id, pjp.amount
                                                         FROM c_posjournalpayments_v pjp
                                                    JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                                               JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                          JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                                     JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                                JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
                           LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                      LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
                 LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
            LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
       LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
                                                GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
                               JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                          JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                     LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                    WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
                                UNION ALL 
                                         SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                           FROM c_invoice i
                                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                  LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id)
                        UNION ALL 
                                 SELECT 'PCA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                            ELSE i.c_invoice_id
                                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                            ELSE pjp.c_invoice_credit_id
                                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                            ELSE p.c_pospaymentmedium_id
                                        END AS c_pospaymentmedium_id, 
                                        CASE
                                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                            ELSE ppm.name
                                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                   FROM c_posjournalpayments_v pjp
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
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL))
                UNION ALL 
                         SELECT 'NCC' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                CASE
                                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                    ELSE i.paymentrule
                                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN i.paymentrule::text = 'P'::text THEN NULL::integer
                                    ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                       FROM ad_ref_list
                                      WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                     LIMIT 1)
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                    WHEN i.paymentrule::text = 'P'::text THEN NULL::character varying
                                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                    ELSE i.paymentrule
                                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                 JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE dt.docbasetype = 'ARC'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_credit_id = i.c_invoice_id)))
        UNION ALL 
                 SELECT 'PA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                            ELSE i.c_invoice_id
                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                            ELSE pjp.c_invoice_credit_id
                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                            ELSE p.c_pospaymentmedium_id
                        END AS c_pospaymentmedium_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                            WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                            WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                            ELSE ppm.name
                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                   FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar)
UNION ALL 
         SELECT 'ND' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                CASE
                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                    ELSE i.paymentrule
                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                   FROM ad_ref_list
                  WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                 LIMIT 1) AS c_pospaymentmedium_id, 
                CASE
                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                    ELSE i.paymentrule
                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE "position"(dt.doctypekey::text, 'CDN'::text) = 1 AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id))
UNION ALL 
SELECT 'CAIA' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) * -1 AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_invoice i
                                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                    LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
                                                 FROM ( SELECT 
                                                              CASE
                                                                  WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                  ELSE i.c_invoice_id
                                                              END AS c_invoice_id, pjp.amount
                                                         FROM c_posjournalpayments_v pjp
                                                    JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                                               JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                          JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                                     JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                                JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
                           LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                      LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
                 LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
            LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
       LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
                                                GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
                               JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                          JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                     LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                    WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND i.docstatus IN ('VO','RE');

ALTER TABLE v_dailysales OWNER TO libertya;

--20140410-1640 Incorporación de columna Processed a la línea de Transferencias de mercadería. Actualización de columna en base a la cabecera para las transferencias existentes 
update ad_system set dummy = (SELECT addcolumnifnotexists('m_transferline','processed', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

update m_transferline as tl
set processed = (SELECT processed FROM m_transfer as t WHERE t.m_transfer_id = tl.m_transfer_id);

--20140414-1945 Mejoras de performance al reporte Resumen de Ventas
DROP VIEW v_dailysales;

CREATE OR REPLACE VIEW v_dailysales AS 
(        (        (         SELECT 'P'::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                    ELSE i.c_invoice_id
                                                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                                                    ELSE pjp.c_invoice_credit_id
                                                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                                                    ELSE p.c_pospaymentmedium_id
                                                                END AS c_pospaymentmedium_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                                                    ELSE ppm.name
                                                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                                           FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
                        UNION ALL 
                                 SELECT 'NCC'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                        CASE
                                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                            ELSE i.paymentrule
                                        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                        CASE
                                            WHEN i.paymentrule::text = 'P'::text THEN NULL::integer
                                            ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                               FROM ad_ref_list
                                              WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                             LIMIT 1)
                                        END AS c_pospaymentmedium_id, 
                                        CASE
                                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                            WHEN i.paymentrule::text = 'P'::text THEN NULL::character varying
                                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                            ELSE i.paymentrule
                                        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                   FROM c_invoice i
                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
               JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
          LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
         WHERE dt.docbasetype = 'ARC'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
                  FROM c_allocationline al
                 WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
                  FROM c_allocationline al
                 WHERE al.c_invoice_credit_id = i.c_invoice_id)))
                UNION ALL 
                         SELECT 'PA'::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar)
        UNION ALL 
                 SELECT 'ND'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                        CASE
                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                            ELSE i.paymentrule
                        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                           FROM ad_ref_list
                          WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                         LIMIT 1) AS c_pospaymentmedium_id, 
                        CASE
                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                            ELSE i.paymentrule
                        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE "position"(dt.doctypekey::text, 'CDN'::text) = 1 AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id)));
ALTER TABLE v_dailysales OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales_invoices AS 
SELECT 'I'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_invoice i
                                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                               JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                          LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id;
ALTER TABLE v_dailysales_invoices OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales_current_account AS 
SELECT 'CAI'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                           FROM c_invoice i
                                                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                                 JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                            LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
                                                         FROM ( SELECT 
                                                                      CASE
                                                                          WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                          ELSE i.c_invoice_id
                                                                      END AS c_invoice_id, pjp.amount
                                                                 FROM c_posjournalpayments_v pjp
                                                            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                                                       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                                  JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                                             JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                                        JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
                                   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                              LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
                         LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
                    LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
               LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
          LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
     LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
                                                        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
                                       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                                  JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                             LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                            WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar]))
UNION ALL
SELECT 'CAIA'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::integer AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, pjp.amount
                 FROM c_posjournalpayments_v pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar]));
ALTER TABLE v_dailysales_current_account OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales_current_account_payments AS 
SELECT 'PCA'::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                    ELSE i.c_invoice_id
                                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                                    ELSE pjp.c_invoice_credit_id
                                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                                    ELSE p.c_pospaymentmedium_id
                                                END AS c_pospaymentmedium_id, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                                    ELSE ppm.name
                                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                           FROM c_posjournalpayments_v pjp
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
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);
ALTER TABLE v_dailysales_current_account_payments OWNER TO libertya;

--20140414-2315 Incorporación de columna c_elementvalue_to_id a la tabla t_acct_balance.
update ad_system set dummy = (SELECT addcolumnifnotexists('t_acct_balance','c_elementvalue_to_id', 'integer'));

--20140414-2315 Incorporación de columna c_elementvalue_to_id a la tabla T_SumsAndBalance.
update ad_system set dummy = (SELECT addcolumnifnotexists('T_SumsAndBalance','c_elementvalue_to_id', 'integer'));

--20140415-1147 Nuevas vistas para mejorar la performance del reporte Declaración de Valores
CREATE OR REPLACE VIEW c_pos_declaracionvalores_ventas AS 
SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.ca_amount
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.ca_amount)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.c_invoice_id, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, COALESCE(ds.amount, 0::numeric)::numeric(22,2) AS ca_amount, i.allocation_active, pos.c_pos_id, pos.name AS posname
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
                            JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                       LEFT JOIN ( SELECT al.c_invoice_id, ah.c_posjournal_id, sum(al.amount) AS amount
                                    FROM c_allocationhdr ah
                               JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
                              WHERE ah.isactive = 'Y'::bpchar
                              GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
                  LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                 ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id;
ALTER TABLE c_pos_declaracionvalores_ventas OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_payments AS 
SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::text AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount)::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname
                                           FROM c_payment p
                                      JOIN c_posjournal_c_payment_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                            JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
                       LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                      GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) p;
ALTER TABLE c_pos_declaracionvalores_payments OWNER TO libertya;


CREATE OR REPLACE VIEW c_pos_declaracionvalores_cash AS 
SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.c_invoice_id, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno, c.allocation_active, c.c_pos_id, c.posname
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount)::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournal_c_cash_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
               JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
          LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
         GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) c;
ALTER TABLE c_pos_declaracionvalores_cash OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_voided AS 
SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name AS posname
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);
ALTER TABLE c_pos_declaracionvalores_voided OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_credit AS 
SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id AS c_invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id AS invoice_id, pos.c_pos_id, pos.name AS posname
                           FROM c_invoice i
                      JOIN c_posjournal_c_invoice_credit_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
       JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
  GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]);
ALTER TABLE c_pos_declaracionvalores_credit OWNER TO libertya;

--20140415-1630 Restauración de vista v_dailysales anterior por rotura de otras funcionalidades y creación de una nueva con los nuevos cambios performantes
DROP VIEW v_dailysales;

CREATE OR REPLACE VIEW v_dailysales AS 
        (        (        (        (        (        (         SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                    ELSE i.c_invoice_id
                                                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                                                    ELSE pjp.c_invoice_credit_id
                                                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                                                    ELSE p.c_pospaymentmedium_id
                                                                END AS c_pospaymentmedium_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                                                    ELSE ppm.name
                                                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                                           FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
                                                UNION ALL 
                                                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                           FROM c_invoice i
                                                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                                 JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                            LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
                                                         FROM ( SELECT 
                                                                      CASE
                                                                          WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                          ELSE i.c_invoice_id
                                                                      END AS c_invoice_id, pjp.amount
                                                                 FROM c_posjournalpayments_v pjp
                                                            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                                                       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                                  JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                                             JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                                        JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
                                   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                              LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
                         LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
                    LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
               LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
          LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
     LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
                                                        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
                                       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                                  JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                             LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                            WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
                                        UNION ALL 
                                                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_invoice i
                                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                                    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                               JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                          LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id)
                                UNION ALL 
                                         SELECT 'PCA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                    ELSE i.c_invoice_id
                                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                                    ELSE pjp.c_invoice_credit_id
                                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                                    ELSE p.c_pospaymentmedium_id
                                                END AS c_pospaymentmedium_id, 
                                                CASE
                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                                    ELSE ppm.name
                                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                           FROM c_posjournalpayments_v pjp
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
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL))
                        UNION ALL 
                                 SELECT 'NCC' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                        CASE
                                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                            ELSE i.paymentrule
                                        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                        CASE
                                            WHEN i.paymentrule::text = 'P'::text THEN NULL::integer
                                            ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                               FROM ad_ref_list
                                              WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                             LIMIT 1)
                                        END AS c_pospaymentmedium_id, 
                                        CASE
                                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                            WHEN i.paymentrule::text = 'P'::text THEN NULL::character varying
                                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                            ELSE i.paymentrule
                                        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                   FROM c_invoice i
                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
               JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
          LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
         WHERE dt.docbasetype = 'ARC'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
                  FROM c_allocationline al
                 WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
                  FROM c_allocationline al
                 WHERE al.c_invoice_credit_id = i.c_invoice_id)))
                UNION ALL 
                         SELECT 'PA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar)
        UNION ALL 
                 SELECT 'ND' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                        CASE
                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                            ELSE i.paymentrule
                        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                           FROM ad_ref_list
                          WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                         LIMIT 1) AS c_pospaymentmedium_id, 
                        CASE
                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                            ELSE i.paymentrule
                        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE "position"(dt.doctypekey::text, 'CDN'::text) = 1 AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id)))
UNION ALL 
         SELECT 'CAIA' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, pjp.amount
                 FROM c_posjournalpayments_v pjp
            JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
       JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar]));

ALTER TABLE v_dailysales OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales_v2 AS 
(        (        (         SELECT 'P'::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                                                    ELSE i.c_invoice_id
                                                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                                                    ELSE pjp.c_invoice_credit_id
                                                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                                                    ELSE p.c_pospaymentmedium_id
                                                                END AS c_pospaymentmedium_id, 
                                                                CASE
                                                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                                                    ELSE ppm.name
                                                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                                           FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
                        UNION ALL 
                                 SELECT 'NCC'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                        CASE
                                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                            ELSE i.paymentrule
                                        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                        CASE
                                            WHEN i.paymentrule::text = 'P'::text THEN NULL::integer
                                            ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                               FROM ad_ref_list
                                              WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                             LIMIT 1)
                                        END AS c_pospaymentmedium_id, 
                                        CASE
                                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                            WHEN i.paymentrule::text = 'P'::text THEN NULL::character varying
                                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                            ELSE i.paymentrule
                                        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                   FROM c_invoice i
                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
               JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
          LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
         WHERE dt.docbasetype = 'ARC'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
                  FROM c_allocationline al
                 WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
                  FROM c_allocationline al
                 WHERE al.c_invoice_credit_id = i.c_invoice_id)))
                UNION ALL 
                         SELECT 'PA'::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, ef.value AS entidadfinancieravalue, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar)
        UNION ALL 
                 SELECT 'ND'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                        CASE
                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                            ELSE i.paymentrule
                        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                           FROM ad_ref_list
                          WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                         LIMIT 1) AS c_pospaymentmedium_id, 
                        CASE
                            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                            ELSE i.paymentrule
                        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE "position"(dt.doctypekey::text, 'CDN'::text) = 1 AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id)));
ALTER TABLE v_dailysales_v2 OWNER TO libertya;

--20140423-0030 Incorporación de cambio de artículo a la vista de movimientos detallado de artículos
CREATE OR REPLACE VIEW v_product_movements_detailed AS 
 SELECT t.movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, w.m_warehouse_id, w.value AS warehouse_value, w.name AS warehouse_name, t.receiptvalue, t.movementdate, t.doctypename, t.documentno, t.docstatus, t.m_product_id, t.product_value, t.product_name, t.qty, t.c_invoice_id, i.documentno AS invoice_documentno
   FROM (        (        (        (         SELECT 'M_InOut' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                                                CASE dt.signo_issotrx
                                                    WHEN 1 THEN 'Y'::text
                                                    ELSE 'N'::text
                                                END AS receiptvalue, t.movementdate, dt.name AS doctypename, io.documentno, io.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, ( SELECT i.c_invoice_id
                                                   FROM c_order o
                                              JOIN c_invoice i ON i.c_order_id = o.c_order_id
                                             WHERE o.c_order_id = io.c_order_id
                                            LIMIT 1) AS c_invoice_id
                                           FROM m_transaction t
                                      JOIN m_inoutline iol ON iol.m_inoutline_id = t.m_inoutline_id
                                 JOIN m_product p ON p.m_product_id = t.m_product_id
                            JOIN m_inout io ON io.m_inout_id = iol.m_inout_id
                       JOIN c_doctype dt ON dt.c_doctype_id = io.c_doctype_id
                                UNION ALL 
                                         SELECT 'M_Movement' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                                                CASE abs(t.movementqty)
                                                    WHEN t.movementqty THEN 'Y'::text
                                                    ELSE 'N'::text
                                                END AS receiptvalue, t.movementdate, dt.name AS doctypename, m.documentno, m.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id
                                           FROM m_transaction t
                                      JOIN m_movementline ml ON ml.m_movementline_id = t.m_movementline_id
                                 JOIN m_product p ON p.m_product_id = t.m_product_id
                            JOIN m_movement m ON m.m_movement_id = ml.m_movement_id
                       JOIN c_doctype dt ON dt.c_doctype_id = m.c_doctype_id)
                        UNION ALL 
                                 SELECT 'M_Inventory' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                                        CASE abs(t.movementqty)
                                            WHEN t.movementqty THEN 'Y'::text
                                            ELSE 'N'::text
                                        END AS receiptvalue, t.movementdate, dt.name AS doctypename, i.documentno, i.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id
                                   FROM m_transaction t
                              JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
                         JOIN m_product p ON p.m_product_id = t.m_product_id
                    JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
               JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
          LEFT JOIN m_transfer tr ON tr.m_inventory_id = i.m_inventory_id
     LEFT JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
     LEFT JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id
    WHERE tr.m_transfer_id IS NULL AND sp.m_splitting_id IS NULL AND pc.m_productchange_id IS NULL)
                UNION ALL 
                         SELECT 'M_Transfer' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                                CASE abs(t.movementqty)
                                    WHEN t.movementqty THEN 'Y'::text
                                    ELSE 'N'::text
                                END AS receiptvalue, t.movementdate, tr.transfertype AS doctypename, tr.documentno, tr.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id
                           FROM m_transaction t
                      JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
                 JOIN m_product p ON p.m_product_id = t.m_product_id
            JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
       JOIN m_transfer tr ON tr.m_inventory_id = i.m_inventory_id)
        UNION ALL 
                 SELECT 'M_Splitting' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                        CASE abs(t.movementqty)
                            WHEN t.movementqty THEN 'Y'::text
                            ELSE 'N'::text
                        END AS receiptvalue, t.movementdate, 'M_Splitting_ID' AS doctypename, sp.documentno, sp.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id
                   FROM m_transaction t
              JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
         JOIN m_product p ON p.m_product_id = t.m_product_id
    JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
   JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
   UNION ALL 
                 SELECT 'M_ProductChange' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                        CASE abs(t.movementqty)
                            WHEN t.movementqty THEN 'Y'::text
                            ELSE 'N'::text
                        END AS receiptvalue, t.movementdate, 'M_ProductChange_ID' AS doctypename, pc.documentno, pc.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id
                   FROM m_transaction t
              JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
         JOIN m_product p ON p.m_product_id = t.m_product_id
    JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
   JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id) t
   JOIN m_locator l ON l.m_locator_id = t.m_locator_id
   JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = t.c_invoice_id;

ALTER TABLE v_product_movements_detailed OWNER TO libertya;

-- 20140424-0952 Indice faltante en tabla de eliminiacion de registros para replicación
DROP INDEX IF EXISTS ad_changelog_replication_retrieveuid;
CREATE INDEX ad_changelog_replication_retrieveuid ON ad_changelog_replication USING btree (retrieveuid );

--20140428-0900 Incorporación de nueva columna para configurar artículos inventariados
update ad_system set dummy = (SELECT addcolumnifnotexists('M_Product','isinventoried', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20140428-1900 Incorporación de nueva columna para omitir las acciones de cierre de depósito como el cambio de fecha a la fecha actual y la validación del cierre abierto para el día actual
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','iswarehouseclosurecontrol', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));