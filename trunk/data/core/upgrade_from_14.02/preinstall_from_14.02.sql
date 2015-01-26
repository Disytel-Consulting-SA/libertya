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

--20140501-1500 Retenciones CABA
update ad_system set dummy = (SELECT addcolumnifnotexists('C_RetSchema_Config','padrontype', 'character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_RetSchema_Config','orden', 'integer NOT NULL DEFAULT 1'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','C_Region_Delivery_ID', 'integer'));

update c_retschema_config
set padrontype = 'B', orden = 1
where ad_client_id = 1010016 and name = 'P';

--20140507-0130 Nueva vista para incorporar los inventarios que no se registran en transaction por cantidad 0
CREATE OR REPLACE VIEW v_product_movements_detailed AS 
 SELECT t.movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, w.m_warehouse_id, w.value AS warehouse_value, w.name AS warehouse_name, t.receiptvalue, t.movementdate, t.doctypename, t.documentno, t.docstatus, t.m_product_id, t.product_value, t.product_name, t.qty, t.c_invoice_id, i.documentno AS invoice_documentno
   FROM (        (        (        (        (         SELECT 'M_InOut' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
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
       WHERE tr.m_transfer_id IS NULL AND sp.m_splitting_id IS NULL AND pc.m_productchange_id IS NULL
       UNION ALL
SELECT 'M_Inventory' AS movement_table, i.ad_client_id, i.ad_org_id, il.m_locator_id, 
                                                CASE WHEN (il.qtycount - il.qtybook) >= 0 THEN 'Y'::text
                                                    ELSE 'N'::text
                                                END AS receiptvalue, i.movementdate, dt.name AS doctypename, i.documentno, i.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(il.qtycount - il.qtybook) AS qty, NULL::unknown AS c_invoice_id
                                           FROM m_inventory i
                                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                                 JOIN m_product p ON p.m_product_id = il.m_product_id
                       JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
                  LEFT JOIN m_transfer tr ON tr.m_inventory_id = i.m_inventory_id
             LEFT JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
        LEFT JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id
       WHERE NOT EXISTS (SELECT m_transaction_id FROM m_transaction t WHERE il.m_inventoryline_id = t.m_inventoryline_id) AND tr.m_transfer_id IS NULL AND sp.m_splitting_id IS NULL AND pc.m_productchange_id IS NULL)
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
       JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id)
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

--20140508-1245 Check para validar la longitud de la secuencia
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','validateseqlength', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20140508-2345 Incorporación de nueva parametrización en la Compañía para permitir o no CUITs únicos en EC
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','UniqueCuit', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20140512-1030 Incorporación de nueva parametrización en proveedores para configurar el monto mínimo de compra (en ordenes de compra)
update ad_system set dummy = (SELECT addcolumnifnotexists('C_BPartner','minimumpurchasedamt', 'numeric(22,9) NOT NULL DEFAULT 0'));

--20140520-2330 Incremento del tamaño del campo Name a 255 para las tablas M_Product y  M_Product_Trl. Se hace asi porque al modificar la longitud desde la sintaxis de postgresql se debían eliminar varias vistas del CORE y con posibilidad de errores en instancias custom de clientes  
UPDATE pg_attribute
SET atttypmod = 259
WHERE attrelid = 'm_product'::regclass AND attname = 'name';

UPDATE pg_attribute
SET atttypmod = 259
WHERE attrelid = 'm_product_trl'::regclass AND attname = 'name';

--20140524-2145 Incorporación de nuevas columnas para exportación RG1361
update ad_system set dummy = (SELECT addcolumnifnotexists('e_electronicinvoice','otros', 'numeric(20,2) NOT NULL DEFAULT 0'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_electronicinvoice','sumotros', 'numeric(20,2) NOT NULL DEFAULT 0'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_electronicinvoice','otros', 'numeric(20,2) NOT NULL DEFAULT 0'));

--20140529-1635 Nueva columna para indicar si se desean chequear las referencias
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_TableReplication','CheckReferences', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20140529-1635 Cambio en trigger de replicacion. Validar las referencias de una tabla unicamente si asi esta configurado para dicha tabla
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
	recordColumns RECORD;
	columnValue varchar;
	isValid integer;
	v_valueDetail varchar;
	v_nameDetail varchar;
	v_columnname varchar;
	v_tableid int; 
	v_tablename varchar;
	v_referenceStr varchar;
	v_referencedTableStr varchar;
	v_referencedTableID int;
	v_existsValueField int;
	v_existsNameField int;
	shouldcheckreferences boolean;
	checkreferencestableconf character;
BEGIN 
	-- se deberan verificar referencias a registros fuera del esquema de replicacion? (inicialmente no)
	shouldcheckreferences := false;

	-- estamos en una accion de eliminacion?
	IF (TG_OP = 'DELETE') THEN

		-- Checkear switch maestro de replicacion
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;

		-- Se repArray es nulo o vacio, no hay mas que hacer dado que es un registro fuera de replicacion
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;

		-- El registro fue replicado? Si no lo fue puede ser eliminado, pero en caso contrario hay que registrar su eliminacion
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN

			-- Recuperar el repArray de la tabla en cuestion
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
			v_newRepArray := replace(v_newRepArray, '3', '1');
			v_newRepArray := replace(v_newRepArray, '2', '0');

			-- Insertar en la tabla de eliminaciones
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues, includeInReplication)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',now(),OLD.CreatedBy,now(),OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null,'Y';
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	-- estamos en una accion de insercion o actualizacion?
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Checkear switch maestro de replicacion		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;

		-- El uso de SKIP se supone para omitir acciones posteriores en eliminacion (dado que setea repArray en NULL)
		IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		END IF;

		-- Verificar si hay que generar el retrieveUID. Ejemplo: h1_291
		IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			-- Primeramente intentar utilizar el AD_ComponentObjectUID (registro perteneciente a un componente)
			BEGIN
				IF NEW.AD_ComponentObjectUID IS NOT NULL AND NEW.AD_ComponentObjectUID <> '' THEN
					NEW.retrieveUID = NEW.AD_ComponentObjectUID;
				END IF;
			EXCEPTION
				WHEN OTHERS THEN
					-- Do nothing
			END;
			-- Si el registro no pertenece a un componente, generar el retrieveUID
		        IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
				-- Obtener posicion de host
				SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
				IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
				-- Obtener siguiente valor para la tabla dada
				SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
				IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
					NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq || '_' || lower(TG_ARGV[1]);
				END IF;		
			END IF;

		-- Si estamos insertando...
		IF (TG_OP = 'INSERT') THEN

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- Recuperar el repArray
				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;

				-- Si es nulo o vacio no hacer nada mas
				IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
					RETURN NEW;
				END IF;

				-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
				NEW.repArray := replace(v_newRepArray, '3', '1');
				NEW.repArray := replace(NEW.repArray, '2', '0');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 1)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('1' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				ELSE
					NEW.repArray := NULL;
				END IF;
			END IF;
			
		-- Si estamos actualizando...
		ELSEIF (TG_OP = 'UPDATE') THEN 

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.		
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- El repArray no esta seteado todavia? (Caso: modificacion de un registro preexistente)
				IF (OLD.repArray IS NULL OR OLD.repArray = '0') THEN

					-- Recuperar el repArray
					SELECT INTO v_newRepArray replicationArray 
					FROM ad_tablereplication 
					WHERE ad_table_ID = TG_ARGV[0]::int;

					-- Cambiar 2 (recibir) por 0 (sin accion)
					NEW.repArray := replace(v_newRepArray, '2', '0');
					-- Cambiar 1 (enviar) y 3 (bidireccional) por 2 (replicado)
					NEW.repArray := replace(NEW.repArray, '1', '2');
					NEW.repArray := replace(NEW.repArray, '3', '2');
				END IF;

				-- Cambiar los 2 (replicado) por 3 (modificado).
				-- Adicionalmente para JMS: 4 (espera ack) por 5 (cambios luego de ack)
				NEW.repArray := replace(NEW.repArray, '2', '3');
				NEW.repArray := replace(NEW.repArray, '4', '5');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 3)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('3' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				END IF;
			END IF;
		END IF;
	END IF;

	IF (shouldcheckreferences = true) THEN

		-- Verificar si la tabla tiene configurado que hay que chequear referencias
		SELECT into checkreferencestableconf CheckReferences FROM ad_tablereplication WHERE AD_table_id = TG_ARGV[0]::int;
		IF (checkreferencestableconf = 'N') THEN
			return NEW;
		END IF;

		-- Validar referencias iterando todas las columnas de la tabla
		FOR recordColumns IN
			SELECT isc.column_name, isc.data_type, c.ad_column_id, t.tablename, t.ad_table_id
			FROM information_schema.columns isc
			INNER JOIN ad_table t ON lower(isc.table_name) = lower(t.tablename)
			INNER JOIN ad_column c ON lower(isc.column_name) = lower(c.columnname) AND t.ad_table_id = c.ad_table_id
			WHERE table_name = quote_ident(TG_TABLE_NAME)
			AND isc.data_type = 'integer'
			AND isc.column_name not in ('retrieveuid', 'reparray', 'datelastsentjms', 'includeinreplication')
		LOOP
			-- Obtener el value de la columna y verificar si es referencia valida.  En caso de no serlo, presentar error correspondiente
			EXECUTE 'SELECT (' || quote_literal(NEW) || '::' || TG_RELID::regclass || ').' || quote_ident(recordColumns.column_name) INTO columnValue;
			SELECT INTO isValid replication_is_valid_reference(recordColumns.ad_column_id, columnvalue);
			IF isValid = 0 THEN

				-- valores por defecto
				v_valueDetail := '';
				v_nameDetail := '';
				v_referenceStr := '';
				
				-- recuperar el nombre de la columna y tabla para brindar un mensaje mas intuitivo al usuario
				v_columnname := recordColumns.column_name;
				v_tablename := recordColumns.tablename;

				BEGIN 
					-- recuperar el nombre o value del registro referenciado a fin de mejorar la legibilidad del mensaje de error
					SELECT INTO v_referencedTableStr replication_get_referenced_table(recordColumns.ad_column_id);
					SELECT INTO v_referencedTableID AD_Table_ID FROM AD_Table WHERE tablename ilike v_referencedTableStr;

					-- ver si existe las columnas value y name
					SELECT INTO v_existsValueField Count(1) FROM AD_Column WHERE columnname ilike 'Value' AND AD_Table_ID = v_referencedTableID;
					SELECT INTO v_existsNameField Count(1) FROM AD_Column WHERE columnname ilike 'Name' AND AD_Table_ID = v_referencedTableID;

					-- cargar value y name
					IF v_existsValueField = 1 THEN
						EXECUTE 'SELECT value FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_valueDetail;
						v_referenceStr := v_valueDetail;
					END IF;
					IF v_existsNameField = 1 THEN
						EXECUTE 'SELECT name FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_nameDetail;
						v_referenceStr := v_referenceStr || ' ' || v_nameDetail;
					END IF;
				EXCEPTION
					WHEN OTHERS THEN
						-- do nothing
				END;
				
				-- concatenar acordemente para mensaje de retorno
				RAISE EXCEPTION 'Validacion de replicación - La columna: % (%) de la tabla: % (%) referencia al registro: % (%), fuera del sistema de replicacion.', v_columnname, recordColumns.ad_column_id, v_tablename, recordColumns.ad_table_id, v_referenceStr, columnvalue;
			END IF;
		END LOOP;

	END IF;
	RETURN NEW;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

--20140606-1121 Desactivar las alertas que actualmente no tienen utilidad  
UPDATE AD_Alert SET isactive = 'N' WHERE AD_Alert_ID IN (100, 1000001);

--20140606-1212 Reducir la frecuencia del procesador de alertas
UPDATE ad_alertprocessor SET frequency = 10 WHERE ad_alertprocessor_id = 100 AND frequency = 1;

--20140608-2000 Incorporación de nueva configuración de límite de cuit de cheque por compañía
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','cuitcontrolchecklimit', 'numeric(20,2) NOT NULL DEFAULT 0'));

--20140611-1640 Bug fix a la vista de detalle de movimientos por artículo para remitos con signo cambiado
DROP VIEW v_product_movements_detailed;

CREATE OR REPLACE VIEW v_product_movements_detailed AS 
 SELECT t.movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, w.m_warehouse_id, w.value AS warehouse_value, w.name AS warehouse_name, t.receiptvalue, t.movementdate, t.doctypename, t.documentno, t.docstatus, t.m_product_id, t.product_value, t.product_name, t.qty, t.c_invoice_id, i.documentno AS invoice_documentno
   FROM (        (        (        (        (        (         SELECT 'M_InOut' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                                                                CASE dt.signo_issotrx
                                                                    WHEN 1 THEN (CASE abs(t.movementqty) WHEN t.movementqty THEN 'Y'::text ELSE 'N'::text END)
                                                                    ELSE (CASE abs(t.movementqty) WHEN t.movementqty THEN 'Y'::text ELSE 'N'::text END)
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
                                         SELECT 'M_Inventory' AS movement_table, i.ad_client_id, i.ad_org_id, il.m_locator_id, 
                                                CASE
                                                    WHEN (il.qtycount - il.qtybook) >= 0::numeric THEN 'Y'::text
                                                    ELSE 'N'::text
                                                END AS receiptvalue, i.movementdate, dt.name AS doctypename, i.documentno, i.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(il.qtycount - il.qtybook) AS qty, NULL::unknown AS c_invoice_id
                                           FROM m_inventory i
                                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                                 JOIN m_product p ON p.m_product_id = il.m_product_id
                            JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
                       LEFT JOIN m_transfer tr ON tr.m_inventory_id = i.m_inventory_id
                  LEFT JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
             LEFT JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id
            WHERE NOT (EXISTS ( SELECT t.m_transaction_id
                     FROM m_transaction t
                    WHERE il.m_inventoryline_id = t.m_inventoryline_id)) AND tr.m_transfer_id IS NULL AND sp.m_splitting_id IS NULL AND pc.m_productchange_id IS NULL)
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
       JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id)
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

--20140618-1008 Eliminar funcion invoiceopen incorrectamente creada en schema public (si es que existe)
DROP FUNCTION IF EXISTS public.invoiceopen(integer, integer);

--20140625-0956 Nueva tabla para soporte de nuevo formato de padrones de percepciones y retenciones de ARBA
CREATE TABLE i_padron_sujeto_aux_new
(
  regimen character(1),
  fecha_publicacion character varying(8),
  fecha_desde character varying(8),
  fecha_hasta character varying(8),
  cuit character varying(11),
  tipo_contr_insc character(1),
  alta_baja character(1),
  cbio_alicuota character(1),
  alicuota character varying(6),
  nro_grupo integer
)
WITH (
  OIDS=TRUE
);
ALTER TABLE i_padron_sujeto_aux_new OWNER TO libertya;

--20140627-1750 Nueva columna a la tabla de nuevo formato de padrones ya que el archivo exportado contiene un ; adicional
update ad_system set dummy = (SELECT addcolumnifnotexists('i_padron_sujeto_aux_new','last_column', 'character(1)'));

--20140704-1640 Nueva columna para indicar el código de jurisdicción.
update ad_system set dummy = (SELECT addcolumnifnotexists('c_region', 'jurisdictioncode', 'integer'));

--20140704-1640 Nueva columna para indicar a que tipo pertence la percepción.
update ad_system set dummy = (SELECT addcolumnifnotexists('c_tax', 'perceptiontype', 'character(1)'));

--20140704-1640 Vista para obtener las percepciones IIBB sufridas
CREATE OR REPLACE VIEW rv_c_tax_iibb_sufridas AS 
SELECT it.ad_org_id,
       it.ad_client_id,
       r.jurisdictioncode AS codigojurisdiccion,
       bp.taxid AS cuit,
       it.created AS date,
       lpad(cast(i.puntodeventa as varchar), 4, '0') AS numerodesucursal,
       i.numerocomprobante AS numerodeconstancia,
       CASE
            WHEN dt.doctypekey = 'VDN' THEN 'D'::bpchar
            WHEN dt.doctypekey = 'VI' THEN 'F'::bpchar
            WHEN dt.doctypekey = 'VCN' THEN 'C'::bpchar
            ELSE 'O'::bpchar
       END AS TipoComprobante, 
       lc.letra AS letracomprobante,
       it.taxamt AS importepercibido
FROM c_invoicetax it
JOIN c_tax t ON t.c_tax_id = it.c_tax_id
JOIN c_invoice i ON it.c_invoice_id = i.c_invoice_id
JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
JOIN c_region r ON r.c_region_id = i.c_region_id
JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
WHERE (dt.docbasetype = ANY (ARRAY['API'::bpchar, 'APC'::bpchar]))
  AND t.ispercepcion = 'Y'::bpchar
  AND t.perceptiontype = 'B'::bpchar;

ALTER TABLE rv_c_tax_iibb_sufridas
  OWNER TO libertya;
  
--20140704-1720 Vista para obtener las percepciones IIBB Buenos Aires Emitidas
CREATE OR REPLACE VIEW rv_c_tax_iibb_emitidas_bsas AS 
SELECT it.ad_org_id,
       it.ad_client_id,
       bp.taxid AS CUIT,
       it.created AS date,
       CASE
            WHEN dt.doctypekey like 'CDN%' THEN 'D'::bpchar
            WHEN dt.doctypekey like 'CI%' THEN 'F'::bpchar
            WHEN dt.doctypekey like 'CCN%' THEN 'C'::bpchar
            ELSE 'O'::bpchar
       END as TipoComprobante,
       lc.letra AS LetraComprobante,
       lpad(cast(i.puntodeventa as varchar), 8, '0') AS NumeroSucursal,
       lpad(cast(i.numerocomprobante as varchar), 8, '0') AS NumeroEmision,
       it.taxbaseamt AS MontoImponible,
       it.taxamt AS ImportePercepcion,
       'A' AS TipoDeOperacion
FROM C_Invoicetax it
INNER JOIN c_tax t ON (t.c_tax_id = it.c_tax_id)
INNER JOIN c_invoice i ON (it.c_invoice_id = i.c_invoice_id)
INNER JOIN c_letra_comprobante lc ON (lc.c_letra_comprobante_id = i.c_letra_comprobante_id)
INNER JOIN c_doctype dt ON (dt.c_doctype_id = i.c_doctype_id)
INNER JOIN c_region r ON (r.c_region_id = i.c_region_id)
INNER JOIN c_bpartner bp ON (bp.c_bpartner_id = i.c_bpartner_id)
WHERE (dt.docbasetype IN ('ARI',
                          'ARC')
       AND (t.ispercepcion = 'Y')
       AND (t.perceptiontype = 'B')
       AND (r.c_region_id = 1000083));
       
--20140707-1021 Nueva columna para indicar a que tipo pertence la retencion.
update ad_system set dummy = (SELECT addcolumnifnotexists('c_retenciontype', 'retentiontype', 'character(1)'));

--20140707-1022 Vista para obtener las retenciones de IIBB Buenos Aires emitidas
CREATE OR REPLACE VIEW RV_C_Invoice_Reten_IIBB_BsAs_Emitidas AS 
SELECT ri.ad_org_id,
       ri.ad_client_id,
       bp.taxid AS CUIT,
       0 AS ImporteRetencion,
       ri.created AS date,
       iPagada.puntodeventa AS NumeroSucursal,
       iPagada.numerocomprobante as NumeroEmision,
       'A' AS TipoOperacion
FROM m_retencion_invoice ri
INNER JOIN c_invoice i ON (i.c_invoice_id = ri.c_invoice_id)
INNER JOIN c_allocationline al ON (al.c_allocationhdr_id = ri.c_allocationhdr_id)
INNER JOIN c_invoice iPagada ON (iPagada.c_invoice_id = al.c_invoice_id)
INNER JOIN c_bpartner bp ON (bp.c_bpartner_id = i.c_bpartner_id)
INNER JOIN c_retencionschema rs ON (rs.c_retencionschema_id = ri.c_retencionschema_id)
INNER JOIN c_retenciontype rt ON (rt.c_retenciontype_id = rs.c_retenciontype_id)
INNER JOIN c_doctype dt ON (dt.c_doctype_id = i.c_doctype_id)
INNER JOIN ad_clientinfo ci ON (ci.ad_client_id = ri.ad_client_id)
WHERE ((rt.retentiontype = 'B')
       AND (dt.docbasetype IN ('API',
                               'APC',
                               'APP'))
       AND (i.c_region_id = 1000083));

ALTER TABLE RV_C_Invoice_Reten_IIBB_BsAs_Emitidas
  OWNER TO libertya;
  
-- 20140717-1008 Habilitar el seteo manual de datenextrun para uso en schedule semanal. Omitir además tipos de schedule no implementados en código. 
update ad_field set isreadonly = 'N' where ad_componentobjectuid = 'CORE-AD_Field-9438';
update ad_field set isreadonly = 'Y' where ad_componentobjectuid = 'CORE-AD_Field-10051';

--20140718-1135 Incorporación de nro de pedido a la vista de movimientos por artículo
DROP VIEW v_product_movements;

CREATE OR REPLACE VIEW v_product_movements AS 
 SELECT m.tablename, m.ad_client_id, m.ad_org_id, o.value AS orgvalue, o.name AS orgname, m.doc_id, m.documentno, m.docstatus, m.description, m.datetrx, m.m_product_id, m.qty, m.type, m.aditionaltype, m.c_charge_id, c.name AS chargename, p.name AS productname, p.value AS productvalue, w.m_warehouse_id, w.value AS warehousevalue, w.name AS warehousename, wt.m_warehouse_id AS m_warehouseto_id, wt.value AS warehousetovalue, wt.name AS warehousetoname, bp.c_bpartner_id, bp.value AS bpartnervalue, bp.name AS bpartnername, pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, COALESCE(pg.value, 'SD'::character varying) AS productgamasvalue, COALESCE(pg.name, 'SIN DESCRIPCION'::character varying) AS productgamasname, COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, COALESCE(pl.value, 'SD'::character varying) AS productlinesvalue, COALESCE(pl.name, 'SIN DESCRIPCION'::character varying) AS productlinesname, order_documentno
   FROM (        (        (        (        (         SELECT 'M_Transfer' AS tablename, t.ad_client_id, t.ad_org_id, t.m_transfer_id AS doc_id, t.documentno, t.docstatus, t.description, t.datetrx, tl.m_product_id, tl.qty, t.transfertype AS type, t.movementtype AS aditionaltype, t.c_charge_id, t.m_warehouse_id, t.m_warehouseto_id, t.c_bpartner_id, null::character varying(30) as order_documentno
                                                   FROM m_transfer t
                                              JOIN m_transferline tl ON tl.m_transfer_id = t.m_transfer_id
                                        UNION ALL 
                                                 SELECT 'M_ProductChange' AS tablename, pc.ad_client_id, pc.ad_org_id, pc.m_productchange_id AS doc_id, pc.documentno, pc.docstatus, pc.description, pc.datetrx, il.m_product_id, il.qtyinternaluse * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, il.c_charge_id, pc.m_warehouse_id, NULL::unknown AS m_warehouseto_id, NULL::unknown AS c_bpartner_id, null::character varying(30) as order_documentno
                                                   FROM m_productchange pc
                                              JOIN m_inventoryline il ON il.m_inventory_id = pc.m_inventory_id)
                                UNION ALL 
                                         SELECT 'M_InOut' AS tablename, io.ad_client_id, io.ad_org_id, io.m_inout_id AS doc_id, io.documentno, io.docstatus, io.description, io.movementdate AS datetrx, iol.m_product_id, iol.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, io.m_warehouse_id, NULL::unknown AS m_warehouseto_id, io.c_bpartner_id, o.documentno as order_documentno
                                           FROM m_inout io
                                      JOIN m_inoutline iol ON iol.m_inout_id = io.m_inout_id
                                 JOIN c_doctype dt ON dt.c_doctype_id = io.c_doctype_id
                                 LEFT JOIN c_order o ON o.c_order_id = io.c_order_id)
                        UNION ALL 
                                 SELECT 'M_Splitting' AS tablename, s.ad_client_id, s.ad_org_id, s.m_splitting_id AS doc_id, s.documentno, s.docstatus, s.comments AS description, s.datetrx, il.m_product_id, il.qtyinternaluse * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, il.c_charge_id, s.m_warehouse_id, NULL::unknown AS m_warehouseto_id, NULL::unknown AS c_bpartner_id, null::character varying(30) as order_documentno
                                   FROM m_splitting s
                              JOIN m_inventoryline il ON il.m_inventory_id = s.m_inventory_id)
                UNION ALL 
                         SELECT 'M_Inventory' AS tablename, i.ad_client_id, i.ad_org_id, i.m_inventory_id AS doc_id, i.documentno, i.docstatus, i.description, i.movementdate AS datetrx, il.m_product_id, il.qtycount AS qty, dt.name AS type, i.inventorykind AS aditionaltype, i.c_charge_id, i.m_warehouse_id, NULL::unknown AS m_warehouseto_id, NULL::unknown AS c_bpartner_id, null::character varying(30) as order_documentno
                           FROM m_inventory i
                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                 JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
                WHERE NOT (EXISTS ( SELECT m_transfer.m_inventory_id
                         FROM m_transfer
                        WHERE m_transfer.m_inventory_id = i.m_inventory_id)) AND NOT (EXISTS ( SELECT m_productchange.m_inventory_id
                         FROM m_productchange
                        WHERE m_productchange.m_inventory_id = i.m_inventory_id OR m_productchange.void_inventory_id = i.m_inventory_id)) AND NOT (EXISTS ( SELECT s.m_inventory_id
                         FROM m_splitting s
                        WHERE s.m_inventory_id = i.m_inventory_id OR s.void_inventory_id = i.m_inventory_id)))
        UNION ALL 
                 SELECT 'M_Movement' AS tablename, m.ad_client_id, m.ad_org_id, m.m_movement_id AS doc_id, m.documentno, m.docstatus, m.description, m.movementdate AS datetrx, ml.m_product_id, ml.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, w.m_warehouse_id, wt.m_warehouse_id AS m_warehouseto_id, NULL::unknown AS c_bpartner_id, null::character varying(30) as order_documentno
                   FROM m_movement m
              JOIN m_movementline ml ON ml.m_movement_id = m.m_movement_id
         JOIN m_locator l ON l.m_locator_id = ml.m_locator_id
    JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id
   JOIN m_locator lt ON lt.m_locator_id = ml.m_locatorto_id
   JOIN m_warehouse wt ON wt.m_warehouse_id = lt.m_warehouse_id
   JOIN c_doctype dt ON dt.c_doctype_id = m.c_doctype_id) m
   JOIN m_product p ON p.m_product_id = m.m_product_id
   JOIN ad_org o ON o.ad_org_id = m.ad_org_id
   JOIN m_warehouse w ON w.m_warehouse_id = m.m_warehouse_id
   JOIN m_product_category pc ON pc.m_product_category_id = p.m_product_category_id
   LEFT JOIN m_product_gamas pg ON pg.m_product_gamas_id = pc.m_product_gamas_id
   LEFT JOIN m_product_lines pl ON pl.m_product_lines_id = pg.m_product_lines_id
   LEFT JOIN m_warehouse wt ON wt.m_warehouse_id = m.m_warehouseto_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = m.c_bpartner_id
   LEFT JOIN c_charge c ON c.c_charge_id = m.c_charge_id;

ALTER TABLE v_product_movements OWNER TO libertya;

--20140724-0000 Incorporación de funcionalidad de Retiro de Efectivo en Tarjetas de Crédito en TPV
update ad_system set dummy = (SELECT addcolumnifnotexists('c_pos', 'isallowcreditcardcashretirement', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_pos', 'creditcardcashretirement_doctype_id', 'integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_pos', 'creditcardcashretirement_product_id', 'integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_pos', 'creditcardcashretirement_charge_id', 'integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice', 'isvoidable', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20140724-0045 Modificación de vistas del Reportes Resumen de Ventas por incorporación de funcionalidad de Retiro de Efectivo en Tarjetas de Crédito en TPV
DROP VIEW v_dailysales_invoices;

CREATE OR REPLACE VIEW v_dailysales_invoices AS 
 SELECT 'I'::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   WHERE NOT EXISTS (SELECT c_allocationline_id FROM c_allocationline al INNER JOIN c_payment p on p.c_payment_id = al.c_payment_id INNER JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = 'Y');

ALTER TABLE v_dailysales_invoices OWNER TO libertya;

DROP VIEW v_dailysales_v2;

CREATE OR REPLACE VIEW v_dailysales_v2 AS 
        (        (         SELECT 'P'::character varying AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) AND NOT EXISTS (SELECT c2.c_payment_id FROM c_cashline c2 WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = 'Y')
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar AND NOT EXISTS (SELECT c2.c_payment_id FROM c_cashline c2 WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = 'Y'))
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
  WHERE al.c_invoice_id = i.c_invoice_id));

ALTER TABLE v_dailysales_v2 OWNER TO libertya;

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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) AND NOT EXISTS (SELECT c2.c_payment_id FROM c_cashline c2 WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = 'Y')
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
                          LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                          WHERE NOT EXISTS (SELECT c_allocationline_id FROM c_allocationline al INNER JOIN c_payment p on p.c_payment_id = al.c_payment_id INNER JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = 'Y'))
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar AND NOT EXISTS (SELECT c2.c_payment_id FROM c_cashline c2 WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = 'Y'))) 
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

--20140724-0050 Modificación de vistas del reporte Declaración de Valores por incorporación de funcionalidad de Retiro de Efectivo en Tarjetas de Crédito en TPV
DROP VIEW c_pos_declaracionvalores_ventas;

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
  WHERE ah.isactive = 'Y'::bpchar AND NOT EXISTS (SELECT p.c_payment_id FROM c_payment p INNER JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id INNER JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id WHERE i.isvoidable = 'Y')
  GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
  ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id;

ALTER TABLE c_pos_declaracionvalores_ventas OWNER TO libertya;

DROP VIEW c_pos_declaracionvalores_voided;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_voided AS 
 SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, (ji.signo_issotrx::numeric * ji.grandtotal) + coalesce((select sum(al2.changeamt) from c_allocationline al2 inner join c_payment as p2 on p2.c_payment_id = al2.c_payment_id inner join c_cashline cl on cl.c_payment_id = p2.c_payment_id where al2.c_invoice_id = ji.c_invoice_id and tendertype = 'C' and al2.isactive = 'N'),0) AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name AS posname
   FROM c_posjournalinvoices_v ji
   JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar) AND (NOT EXISTS (SELECT p.c_payment_id FROM c_payment p INNER JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id INNER JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id WHERE i.isvoidable = 'Y'));

ALTER TABLE c_pos_declaracionvalores_voided OWNER TO libertya;

DROP VIEW c_pos_declaracionvalores_v;
DROP VIEW c_pos_declaracionvalores_payments;
DROP VIEW c_posjournal_c_payment_v;

CREATE OR REPLACE VIEW c_posjournal_c_payment_v AS 
         SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, p.tendertype::character varying AS tendertype, p.documentno, p.description, ((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, ah.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, al.changeamt
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
UNION ALL 
        ( SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::integer AS c_invoice_id, p.c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, p.payamt AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, 0 as changeamt
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
    FROM c_allocationline al
   WHERE al.c_payment_id = p.c_payment_id))
  ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournal_c_payment_v OWNER TO libertya;


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
   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount + (CASE WHEN p.tendertype = 'C' THEN pjp.changeamt ELSE 0 END))::numeric(22,2) AS total, pjp.invoice_documentno, (pjp.invoice_grandtotal+ (CASE WHEN p.tendertype = 'C' THEN pjp.changeamt ELSE 0 END))::numeric(20,2) as invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname
           FROM c_payment p
      JOIN c_posjournal_c_payment_v pjp ON pjp.c_payment_id = p.c_payment_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
   LEFT JOIN c_invoice i on i.c_invoice_id = pjp.c_invoice_id
   WHERE i.c_invoice_id is null OR (NOT EXISTS (SELECT cl.c_cashline_id FROM c_cashline cl WHERE cl.c_payment_id = p.c_payment_id and i.isvoidable = 'Y'))
  GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.changeamt, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) p;

ALTER TABLE c_pos_declaracionvalores_payments OWNER TO libertya;
  
CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
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
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
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
                      GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) p)
                UNION ALL 
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
         GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) c)
        UNION ALL 
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
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name AS posname
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--20140724-1134 Issue Google Code GC-24: No pueden utilizarse instrucciones SQL como lógica predeterminada en parámetros de informes
alter table ad_process_para alter column defaultvalue type character varying(2000);

--20140725-0100 Nueva tabla temporal para reporte de exportación de artículos
CREATE TABLE t_product
(
  ad_pinstance_id integer NOT NULL,
  m_product_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  "value" character varying(40) NOT NULL,
  "name" character varying(255) NOT NULL,
  description character varying(255),
  upc character varying(30),
  ispurchased character(1) NOT NULL DEFAULT 'Y'::bpchar,
  issold character(1) NOT NULL DEFAULT 'Y'::bpchar,
  orgvalue character varying(40),
  orgname character varying(60),
  c_uom_id integer,
  x12de355 character varying(4),
  uomsymbol character varying(10),
  m_product_category_id integer,
  productcategory_value character varying(40),
  productcategory_name character varying(60),
  m_product_gamas_id integer,
  productgamas_value character varying(100),
  productgamas_name character varying(60),
  m_product_lines_id integer,
  productlines_value character varying(40),
  productlines_name character varying(60),
  m_product_family_id integer,
  productfamily_value character varying(60),
  productfamily_name character varying(60),
  c_taxcategory_id integer,
  c_taxcategory_name character varying(60),
  producttype character(1) NOT NULL DEFAULT 'I'::bpchar,
  checkoutplace character(1) NOT NULL DEFAULT 'B'::bpchar,
  c_bpartner_id integer,
  bpartner_value character varying(40),
  bpartner_name character varying(60),
  CONSTRAINT adpinstance_tproduct FOREIGN KEY (ad_pinstance_id)
      REFERENCES ad_pinstance (ad_pinstance_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE t_product OWNER TO libertya;

--20140725-1455 Incorporación de branch GC_33 realizado por Saulo Gil: Modificación de importación de padrón por cambios en los formatos
CREATE INDEX i_padron_sujeto_aux_new_cuit
  ON libertya.i_padron_sujeto_aux_new
  USING btree
  (cuit);

CREATE OR REPLACE FUNCTION libertya.update_bpartner_padron_bsas(p_ad_org_id integer, p_ad_client_id integer, p_ad_user_id integer, p_padrontype char(1), p_offset integer, p_chunksize integer)
RETURNS void AS
$BODY$
DECLARE
	aux RECORD;
BEGIN

	FOR AUX IN
		SELECT * FROM i_padron_sujeto_aux_new
		ORDER BY cuit
		OFFSET p_offset
		LIMIT p_chunksize
	LOOP
		UPDATE
			c_bpartner_padron_bsas padron
		SET
			FECHA_DESDE = to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY') 
			, FECHA_HASTA = to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY') 
			, TIPO_CONTR_INSC = aux.TIPO_CONTR_INSC 
			, ALTA_BAJA = aux.ALTA_BAJA 
			, CBIO_ALICUOTA = aux.CBIO_ALICUOTA 
			, PERCEPCION = (CASE aux.regimen WHEN 'P' THEN to_number(aux.alicuota , '9999999D99') ELSE padron.percepcion END) 
			, RETENCION = (CASE aux.regimen WHEN 'R' THEN to_number(aux.alicuota, '9999999D99') ELSE padron.retencion END) 
			, NRO_GRUPO_RET = (CASE aux.regimen WHEN 'R' THEN aux.NRO_GRUPO ELSE padron.NRO_GRUPO_RET END)
			, NRO_GRUPO_PER = (CASE aux.regimen WHEN 'P' THEN aux.NRO_GRUPO ELSE padron.NRO_GRUPO_PER END)
			, ISACTIVE = 'Y' 
			, UPDATED = CURRENT_DATE 
			, UPDATEDBY = p_ad_user_id
		WHERE
			padron.CUIT = aux.CUIT
			AND padron.padrontype = p_padrontype   	
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')
			AND AD_CLIENT_ID = p_ad_client_id
			AND AD_ORG_ID = p_ad_org_id			
			AND (
				( 
					aux.regimen = 'R' 
					AND (padron.NRO_GRUPO_RET = aux.NRO_GRUPO  OR padron.NRO_GRUPO_RET = 0)
				)
				OR 
				( 
					aux.regimen = 'P' 
					AND (padron.NRO_GRUPO_PER = aux.NRO_GRUPO OR padron.NRO_GRUPO_PER = 0) 
				)
			)
		;

		IF FOUND = FALSE THEN
			INSERT
			INTO c_bpartner_padron_bsas
			(      
				c_bpartner_padron_bsas_ID
				, FECHA_PUBLICACION        
				, FECHA_DESDE              
				, FECHA_HASTA              
				, CUIT                     
				, TIPO_CONTR_INSC          
				, ALTA_BAJA                
				, CBIO_ALICUOTA            
				, PERCEPCION               
				, RETENCION                
				, NRO_GRUPO_RET            
				, NRO_GRUPO_PER            
				, AD_CLIENT_ID             
				, AD_ORG_ID                
				, ISACTIVE                 
				, CREATED                  
				, UPDATED                  
				, CREATEDBY                
				, UPDATEDBY                
				, padrontype                 
			) 
			VALUES
			( 
				nextval('seq_c_bpartner_padron_bsas')     
				, to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')     
				, to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')           
				, to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')           
				, aux.CUIT
				, aux.TIPO_CONTR_INSC                                 
				, aux.ALTA_BAJA                                       
				, aux.CBIO_ALICUOTA                                   
				, to_number((CASE aux.regimen WHEN 'P' THEN aux.alicuota ELSE '0,00' END), '9999999D99')             
				, to_number((CASE aux.regimen WHEN 'R' THEN aux.alicuota ELSE '0,00' END), '9999999D99')              
				, (CASE aux.regimen WHEN 'R' THEN aux.NRO_GRUPO ELSE 0 END)
				, (CASE aux.regimen WHEN 'P' THEN aux.NRO_GRUPO ELSE 0 END)
				, p_ad_client_id
				, p_ad_org_id
				, 'Y'                                             
				, CURRENT_DATE                                    
				, CURRENT_DATE                                    
				, p_ad_user_id
				, p_ad_user_id
				, p_padrontype
			);
		END IF;
	END LOOP;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--20140804-2230 Eliminar función update_bpartner_padron_bsas si es que existe
DROP FUNCTION IF EXISTS libertya.update_bpartner_padron_bsas(p_ad_org_id integer, p_ad_client_id integer, p_ad_user_id integer, p_padrontype char(1), p_offset integer, p_chunksize integer);
--20140804-2230 Eliminar tabla i_padron_sujeto_aux si es que existe
DROP TABLE IF EXISTS libertya.i_padron_sujeto_aux;
--20140804-2230 Eliminar tabla i_padron_sujeto_aux_new si es que existe
DROP TABLE IF EXISTS libertya.i_padron_sujeto_aux_new;

--20140804-2230 Nueva tabla para soporte de nuevo formato de padrones de percepciones y retenciones de ARBA
CREATE TABLE i_padron_bs_as
(
  regimen character(1),
  fecha_publicacion character varying(8),
  fecha_desde character varying(8),
  fecha_hasta character varying(8),
  cuit character varying(11),
  tipo_contr_insc character(1),
  alta_baja character(1),
  cbio_alicuota character(1),
  alicuota character varying(6),
  nro_grupo integer,
  last_column character(1)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE i_padron_bs_as OWNER TO libertya;

--20140804-2230 Crear índice i_padron_bs_as_cuit
CREATE INDEX i_padron_bs_as_cuit
  ON libertya.i_padron_bs_as
  USING btree
  (cuit);

--20140804-2230 Tabla para soporte de formato de padrón ALTO RIESGO - AGIP
CREATE TABLE i_padron_caba_alto_riesgo
(
  fecha_publicacion character varying(10),
  fecha_desde character varying(10),
  fecha_hasta character varying(10),
  cuit character varying(11),
  tipo_contr_insc character(1),
  alta_baja character(1),
  cbio_alicuota character(1),
  percepcion character varying(6),
  retencion character varying(6),
  nro_grupo_ret integer,
  nro_grupo_per integer,
  name_entidad_comercial character varying(255)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE i_padron_caba_alto_riesgo OWNER TO libertya;
GRANT ALL ON TABLE i_padron_caba_alto_riesgo TO libertya;

--20140804-2230 Incorporación de índice a la tabla i_padron_caba_alto_riesgo_cuit
CREATE INDEX i_padron_caba_alto_riesgo_cuit
  ON libertya.i_padron_caba_alto_riesgo
  USING btree
  (cuit);

--20140804-2230 Tabla para soporte de formato de padrón REGIMEN SIMPLICADO - AGIP
CREATE TABLE i_padron_caba_regimen_simplificado
(
  fecha_publicacion character varying(10),
  fecha_desde character varying(10),
  fecha_hasta character varying(10),
  cuit character varying(11),
  tipo_contr_insc character(1),
  alta_baja character(1),
  cbio_alicuota character(1),
  percepcion character varying(6),
  retencion character varying(6),
  nro_grupo_ret integer,
  nro_grupo_per integer,
  name_entidad_comercial character varying(255)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE i_padron_caba_regimen_simplificado OWNER TO libertya;
GRANT ALL ON TABLE i_padron_caba_regimen_simplificado TO libertya;

--20140804-2230 Incorporación de índice a la tabla i_padron_caba_regimen_simplificado
CREATE INDEX i_padron_caba_regimen_simplificado_cuit
  ON libertya.i_padron_caba_regimen_simplificado
  USING btree
  (cuit);

--20140804-2230 Crear función update_bpartner_padron_bsas from i_padron_bs_as
CREATE OR REPLACE FUNCTION libertya.update_padron_from_i_padron_bs_as(p_ad_org_id integer, p_ad_client_id integer, p_ad_user_id integer, p_padrontype char(1), p_offset integer, p_chunksize integer)
RETURNS void AS
$BODY$
DECLARE
	aux RECORD;
BEGIN

	FOR AUX IN
		SELECT * FROM i_padron_bs_as
		ORDER BY cuit
		OFFSET p_offset
		LIMIT p_chunksize
	LOOP
		UPDATE
			c_bpartner_padron_bsas padron
		SET
			FECHA_DESDE = to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY') 
			, FECHA_HASTA = to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY') 
			, TIPO_CONTR_INSC = aux.TIPO_CONTR_INSC 
			, ALTA_BAJA = aux.ALTA_BAJA 
			, CBIO_ALICUOTA = aux.CBIO_ALICUOTA 
			, PERCEPCION = (CASE aux.regimen WHEN 'P' THEN to_number(aux.alicuota , '9999999D99') ELSE padron.percepcion END) 
			, RETENCION = (CASE aux.regimen WHEN 'R' THEN to_number(aux.alicuota, '9999999D99') ELSE padron.retencion END) 
			, NRO_GRUPO_RET = (CASE aux.regimen WHEN 'R' THEN aux.NRO_GRUPO ELSE padron.NRO_GRUPO_RET END)
			, NRO_GRUPO_PER = (CASE aux.regimen WHEN 'P' THEN aux.NRO_GRUPO ELSE padron.NRO_GRUPO_PER END)
			, ISACTIVE = 'Y' 
			, UPDATED = CURRENT_DATE 
			, UPDATEDBY = p_ad_user_id
		WHERE
			padron.CUIT = aux.CUIT
			AND padron.padrontype = p_padrontype   	
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')
			AND AD_CLIENT_ID = p_ad_client_id
			AND AD_ORG_ID = p_ad_org_id			
			AND (
				( 
					aux.regimen = 'R' 
					AND (padron.NRO_GRUPO_RET = aux.NRO_GRUPO  OR padron.NRO_GRUPO_RET = 0)
				)
				OR 
				( 
					aux.regimen = 'P' 
					AND (padron.NRO_GRUPO_PER = aux.NRO_GRUPO OR padron.NRO_GRUPO_PER = 0) 
				)
			)
		;

		IF FOUND = FALSE THEN
			INSERT
			INTO c_bpartner_padron_bsas
			(      
				c_bpartner_padron_bsas_ID
				, FECHA_PUBLICACION        
				, FECHA_DESDE              
				, FECHA_HASTA              
				, CUIT                     
				, TIPO_CONTR_INSC          
				, ALTA_BAJA                
				, CBIO_ALICUOTA            
				, PERCEPCION               
				, RETENCION                
				, NRO_GRUPO_RET            
				, NRO_GRUPO_PER            
				, AD_CLIENT_ID             
				, AD_ORG_ID                
				, ISACTIVE                 
				, CREATED                  
				, UPDATED                  
				, CREATEDBY                
				, UPDATEDBY                
				, padrontype                 
			) 
			VALUES
			( 
				nextval('seq_c_bpartner_padron_bsas')     
				, to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')     
				, to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')           
				, to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')           
				, aux.CUIT
				, aux.TIPO_CONTR_INSC                                 
				, aux.ALTA_BAJA                                       
				, aux.CBIO_ALICUOTA                                   
				, to_number((CASE aux.regimen WHEN 'P' THEN aux.alicuota ELSE '0,00' END), '9999999D99')             
				, to_number((CASE aux.regimen WHEN 'R' THEN aux.alicuota ELSE '0,00' END), '9999999D99')              
				, (CASE aux.regimen WHEN 'R' THEN aux.NRO_GRUPO ELSE 0 END)
				, (CASE aux.regimen WHEN 'P' THEN aux.NRO_GRUPO ELSE 0 END)
				, p_ad_client_id
				, p_ad_org_id
				, 'Y'                                             
				, CURRENT_DATE                                    
				, CURRENT_DATE                                    
				, p_ad_user_id
				, p_ad_user_id
				, p_padrontype
			);
		END IF;
	END LOOP;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--20140804-2230 Crear función update_bpartner_padron_bsas from i_padron_caba_alto_riesgo
CREATE OR REPLACE FUNCTION libertya.update_padron_from_i_padron_caba_alto_riesgo(p_ad_org_id integer, p_ad_client_id integer, p_ad_user_id integer, p_padrontype char(1), p_offset integer, p_chunksize integer)
RETURNS void AS
$BODY$
DECLARE
	aux RECORD;
BEGIN

	FOR AUX IN
		SELECT * FROM i_padron_caba_alto_riesgo
		ORDER BY cuit
		OFFSET p_offset
		LIMIT p_chunksize
	LOOP
		UPDATE
			c_bpartner_padron_bsas padron
		SET
			FECHA_DESDE = to_timestamp(to_number(aux.FECHA_DESDE , '99999999999')::text, 'DDMMYYYY')
			, FECHA_HASTA = to_timestamp(to_number(aux.FECHA_HASTA , '99999999999')::text, 'DDMMYYYY')
			, TIPO_CONTR_INSC = aux.TIPO_CONTR_INSC 
			, ALTA_BAJA = aux.ALTA_BAJA 
			, CBIO_ALICUOTA = aux.CBIO_ALICUOTA 
			, PERCEPCION = to_number(aux.PERCEPCION , '9999999D99') 
			, RETENCION = to_number(aux.RETENCION , '9999999D99') 
			, NRO_GRUPO_RET = aux.NRO_GRUPO_RET
			, NRO_GRUPO_PER = aux.NRO_GRUPO_PER
			, ISACTIVE = 'Y' 
			, UPDATED = CURRENT_DATE 
			, UPDATEDBY = p_ad_user_id
		WHERE
			padron.CUIT = aux.CUIT
			AND padron.padrontype = p_padrontype   	
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')
			AND AD_CLIENT_ID = p_ad_client_id
			AND AD_ORG_ID = p_ad_org_id
		;

		IF FOUND = FALSE THEN
			INSERT
			INTO c_bpartner_padron_bsas
			(      
				c_bpartner_padron_bsas_ID
				, FECHA_PUBLICACION        
				, FECHA_DESDE              
				, FECHA_HASTA              
				, CUIT                     
				, TIPO_CONTR_INSC          
				, ALTA_BAJA                
				, CBIO_ALICUOTA            
				, PERCEPCION               
				, RETENCION                
				, NRO_GRUPO_RET            
				, NRO_GRUPO_PER            
				, AD_CLIENT_ID             
				, AD_ORG_ID                
				, ISACTIVE                 
				, CREATED                  
				, UPDATED                  
				, CREATEDBY                
				, UPDATEDBY                
				, padrontype                 
			) 
			VALUES
			( 
				nextval('seq_c_bpartner_padron_bsas')     
				, to_timestamp(to_number(aux.FECHA_PUBLICACION , '99999999999')::text, 'DDMMYYYY')
				, to_timestamp(to_number(aux.FECHA_DESDE , '99999999999')::text, 'DDMMYYYY')      
				, to_timestamp(to_number(aux.FECHA_HASTA , '99999999999')::text, 'DDMMYYYY')
				, aux.CUIT
				, aux.TIPO_CONTR_INSC                                 
				, aux.ALTA_BAJA                                       
				, aux.CBIO_ALICUOTA                                   
				, to_number(aux.PERCEPCION , '9999999D99')       
				, to_number(aux.RETENCION , '9999999D99')          
				, aux.NRO_GRUPO_RET
				, aux.NRO_GRUPO_PER
				, p_ad_client_id
				, p_ad_org_id
				, 'Y'                                             
				, CURRENT_DATE                                    
				, CURRENT_DATE                                    
				, p_ad_user_id
				, p_ad_user_id
				, p_padrontype
			);
		END IF;
	END LOOP;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--20140804-2230 Crear función update_bpartner_padron_bsas from i_padron_regimen_simplificado
CREATE OR REPLACE FUNCTION libertya.update_padron_from_i_padron_caba_regimen_simplificado(p_ad_org_id integer, p_ad_client_id integer, p_ad_user_id integer, p_padrontype char(1), p_offset integer, p_chunksize integer)
RETURNS void AS
$BODY$
DECLARE
	aux RECORD;
BEGIN

	FOR AUX IN
		SELECT * FROM i_padron_caba_regimen_simplificado
		ORDER BY cuit
		OFFSET p_offset
		LIMIT p_chunksize
	LOOP
		UPDATE
			c_bpartner_padron_bsas padron
		SET
			FECHA_DESDE = to_timestamp(to_number(aux.FECHA_DESDE , '99999999999')::text, 'DDMMYYYY')
			, FECHA_HASTA = to_timestamp(to_number(aux.FECHA_HASTA , '99999999999')::text, 'DDMMYYYY')
			, TIPO_CONTR_INSC = aux.TIPO_CONTR_INSC 
			, ALTA_BAJA = aux.ALTA_BAJA 
			, CBIO_ALICUOTA = aux.CBIO_ALICUOTA 
			, PERCEPCION = to_number(aux.PERCEPCION , '9999999D99') 
			, RETENCION = to_number(aux.RETENCION , '9999999D99') 
			, NRO_GRUPO_RET = aux.NRO_GRUPO_RET
			, NRO_GRUPO_PER = aux.NRO_GRUPO_PER
			, ISACTIVE = 'Y' 
			, UPDATED = CURRENT_DATE 
			, UPDATEDBY = p_ad_user_id
		WHERE
			padron.CUIT = aux.CUIT
			AND padron.padrontype = p_padrontype   	
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')
			AND AD_CLIENT_ID = p_ad_client_id
			AND AD_ORG_ID = p_ad_org_id
		;

		IF FOUND = FALSE THEN
			INSERT
			INTO c_bpartner_padron_bsas
			(      
				c_bpartner_padron_bsas_ID
				, FECHA_PUBLICACION        
				, FECHA_DESDE              
				, FECHA_HASTA              
				, CUIT                     
				, TIPO_CONTR_INSC          
				, ALTA_BAJA                
				, CBIO_ALICUOTA            
				, PERCEPCION               
				, RETENCION                
				, NRO_GRUPO_RET            
				, NRO_GRUPO_PER            
				, AD_CLIENT_ID             
				, AD_ORG_ID                
				, ISACTIVE                 
				, CREATED                  
				, UPDATED                  
				, CREATEDBY                
				, UPDATEDBY                
				, padrontype                 
			) 
			VALUES
			( 
				nextval('seq_c_bpartner_padron_bsas')     
				, to_timestamp(to_number(aux.FECHA_PUBLICACION , '99999999999')::text, 'DDMMYYYY')
				, to_timestamp(to_number(aux.FECHA_DESDE , '99999999999')::text, 'DDMMYYYY')      
				, to_timestamp(to_number(aux.FECHA_HASTA , '99999999999')::text, 'DDMMYYYY')
				, aux.CUIT
				, aux.TIPO_CONTR_INSC                                 
				, aux.ALTA_BAJA                                       
				, aux.CBIO_ALICUOTA                                   
				, to_number(aux.PERCEPCION , '9999999D99')       
				, to_number(aux.RETENCION , '9999999D99')          
				, aux.NRO_GRUPO_RET
				, aux.NRO_GRUPO_PER
				, p_ad_client_id
				, p_ad_org_id
				, 'Y'                                             
				, CURRENT_DATE                                    
				, CURRENT_DATE                                    
				, p_ad_user_id
				, p_ad_user_id
				, p_padrontype
			);
		END IF;
	END LOOP;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE;

--20140808-2045 Crear función calculateCostOrderPrice
CREATE OR REPLACE FUNCTION calculateCostOrderPrice(p_m_product_id integer, p_fecha_corte timestamp with time zone, p_ad_client_id integer, p_ad_org_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Amount		NUMERIC;
BEGIN
	IF (p_fecha_corte IS NULL OR p_m_product_id = 0 OR p_ad_client_id = 0 OR p_ad_org_id = 0) THEN
		return null;
	END IF;

	SELECT currencyconvert(priceactual, o.c_currency_id, 118, o.dateacct::timestamp with time zone, NULL::integer, o.ad_client_id, o.ad_org_id) AS precio
	INTO 	v_Amount
	FROM C_Order o 
	INNER JOIN C_OrderLine ol ON (o.C_Order_ID = ol.C_Order_ID) 
	WHERE (ol.M_Product_ID=p_m_product_id) AND (o.IsSoTrx = 'N') AND (o.DocStatus IN ('CO','CL')) AND (ol.DateOrdered::date <= p_fecha_corte::date) AND (o.AD_Client_ID = p_ad_client_id) AND (o.AD_Org_ID = p_ad_org_id)
	ORDER BY ol.DateOrdered DESC LIMIT 1;

	IF NOT FOUND THEN 
		SELECT COALESCE(pp.pricestd, 0) 
		INTO 	v_Amount
		FROM M_ProductPrice pp 
		WHERE (pp.M_Product_ID=p_m_product_id) 
		     AND (pp.M_PriceList_Version_ID = (SELECT M_PriceList_Version_ID 
						       FROM M_PriceList_Version plv 
						       INNER JOIN M_PriceList pl ON plv.M_PriceList_ID = pl.M_PriceList_ID 
						       WHERE (IsSoPriceList = 'N') AND (plv.AD_Org_ID = p_ad_org_id) 
						       ORDER BY plv.Created 
						       DESC LIMIT 1));
		IF NOT FOUND THEN 
			RETURN null; 
		END IF;	
	END IF;	
	
        RETURN v_Amount;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION calculateCostOrderPrice(integer, timestamp with time zone, integer, integer) OWNER TO libertya;

--20140818-1640 Crear función calculateCostOrderPrice
CREATE OR REPLACE FUNCTION calculateCostOrderPrice(p_m_product_id integer, p_fecha_corte timestamp with time zone, p_ad_client_id integer, p_ad_org_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Amount		NUMERIC;
BEGIN
	IF (p_fecha_corte IS NULL OR p_m_product_id = 0 OR p_ad_client_id = 0 OR p_ad_org_id = 0) THEN
		return null;
	END IF;

	SELECT currencyconvert(priceactual, o.c_currency_id, 118, o.dateacct::timestamp with time zone, NULL::integer, o.ad_client_id, o.ad_org_id) AS precio
	INTO 	v_Amount
	FROM C_Order o 
	INNER JOIN C_OrderLine ol ON (o.C_Order_ID = ol.C_Order_ID) 
	WHERE (ol.M_Product_ID=p_m_product_id) AND (o.IsSoTrx = 'N') AND (o.DocStatus IN ('CO','CL')) AND ((ol.DateOrdered + (ol.created - date_trunc('DAY',ol.created))) <= p_fecha_corte) AND (o.AD_Client_ID = p_ad_client_id) AND (o.AD_Org_ID = p_ad_org_id)
	ORDER BY ol.DateOrdered DESC LIMIT 1;

	IF NOT FOUND THEN 
		SELECT COALESCE(pp.pricestd, 0) 
		INTO 	v_Amount
		FROM M_ProductPrice pp 
		WHERE (pp.M_Product_ID=p_m_product_id) 
		     AND (pp.M_PriceList_Version_ID = (SELECT M_PriceList_Version_ID 
						       FROM M_PriceList_Version plv 
						       INNER JOIN M_PriceList pl ON plv.M_PriceList_ID = pl.M_PriceList_ID 
						       WHERE (IsSoPriceList = 'N') AND (plv.AD_Org_ID = p_ad_org_id) 
						       ORDER BY plv.Created 
						       DESC LIMIT 1));
		IF NOT FOUND THEN 
			RETURN null; 
		END IF;	
	END IF;	
	
        RETURN v_Amount;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION calculateCostOrderPrice(integer, timestamp with time zone, integer, integer) OWNER TO libertya;

--20140821-2315 Nuevas incorporaciones a las reglas de precios por nuevo proceso de importación de precios
ALTER TABLE m_discountschemaline ADD COLUMN "name" character varying(255);

UPDATE m_discountschemaline dsl
SET name = (SELECT name FROM m_discountschema ds WHERE ds.m_discountschema_id = dsl.m_discountschema_id) || '_' || seqno;

ALTER TABLE m_discountschemaline ALTER COLUMN "name" SET NOT NULL;

ALTER TABLE m_discountschemaline ADD COLUMN description character varying(255);
ALTER TABLE m_discountschemaline ADD COLUMN m_product_lines_id integer;

CREATE TABLE m_discountschemaline_application
(
  m_discountschemaline_application_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  m_discountschemaline_id integer NOT NULL,
  CONSTRAINT m_discountschemaline_application_key PRIMARY KEY (m_discountschemaline_application_id),
  CONSTRAINT m_discountschemaline_application_discountschemaline_fk FOREIGN KEY (m_discountschemaline_id)
      REFERENCES m_discountschemaline (m_discountschemaline_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE m_discountschemaline_application OWNER TO libertya;


CREATE TABLE m_discountschemaline_exception
(
  m_discountschemaline_exception_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  m_discountschemaline_id integer NOT NULL,
  CONSTRAINT m_discountschemaline_exception_key PRIMARY KEY (m_discountschemaline_exception_id),
  CONSTRAINT m_discountschemaline_exception_discountschemaline_fk FOREIGN KEY (m_discountschemaline_id)
      REFERENCES m_discountschemaline (m_discountschemaline_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE m_discountschemaline_exception OWNER TO libertya;


CREATE OR REPLACE VIEW m_discountschemaline_org_application_v AS 
select *
from (select distinct o.ad_client_id, o.ad_org_id, o.value as orgvalue, o.name as orgname, dsl.m_discountschemaline_id, dsl.name, dsl.m_discountschema_id
	from ad_org as o, m_discountschemaline dsl
	where o.ad_client_id = dsl.ad_client_id and dsl.isactive = 'Y') as a
where not exists (select m_discountschemaline_id 
			from m_discountschemaline_exception as dsle 
			where dsle.ad_org_id = a.ad_org_id and dsle.m_discountschemaline_id = a.m_discountschemaline_id and isactive = 'Y') 		

	and (not exists (select m_discountschemaline_id 
			from m_discountschemaline_application dsla 
			where dsla.m_discountschemaline_id = a.m_discountschemaline_id and isactive = 'Y')

	or exists (select m_discountschemaline_id 
			from m_discountschemaline_application dsla 
			where dsla.m_discountschemaline_id = a.m_discountschemaline_id and (dsla.ad_org_id = 0 or dsla.ad_org_id = a.ad_org_id) and isactive = 'Y'));
ALTER TABLE m_discountschemaline_org_application_v OWNER TO libertya;

--20140826-1700 Ampliaciones a la configuración de percepciones de iib de provincias
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_org_percepcion', 'alicuota', 'numeric(9,2) NOT NULL DEFAULT 0'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_org_percepcion', 'isconveniomultilateral', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner', 'isconveniomultilateral', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20140827-0900 Columnas faltantes de commit 1028. Incorporaciones de límite y restricción en la entidad financiera
update ad_system set dummy = (SELECT addcolumnifnotexists('m_entidadfinanciera', 'isallowcreditcardcashretirement', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('m_entidadfinanciera', 'creditcardcashretirementlimit', 'numeric(9,2) NOT NULL DEFAULT 0'));

--20140827-1200 Mejoras a vistas del reporte de Declaración de Valores por baja de performance en base de datos con cantidad alta de registros
CREATE INDEX c_cashline_payment_id
  ON c_cashline
  USING btree
  (c_payment_id);

DROP VIEW c_pos_declaracionvalores_ventas;

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
   INNER JOIN c_invoice as i on i.c_invoice_id = al.c_invoice_id
  WHERE ah.isactive = 'Y'::bpchar and i.isvoidable = 'N'
  GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
  ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id;
ALTER TABLE c_pos_declaracionvalores_ventas OWNER TO libertya;

DROP VIEW c_pos_declaracionvalores_voided;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_voided AS 
 SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, (ji.signo_issotrx::numeric * ji.grandtotal) + coalesce((select sum(al2.changeamt) from c_allocationline al2 inner join c_payment as p2 on p2.c_payment_id = al2.c_payment_id inner join c_cashline cl on cl.c_payment_id = p2.c_payment_id where al2.c_invoice_id = ji.c_invoice_id and tendertype = 'C' and al2.isactive = 'N'),0) AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name AS posname
   FROM c_posjournalinvoices_v ji
   JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   INNER JOIN c_invoice as i on i.c_invoice_id = al.c_invoice_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar) AND i.isvoidable = 'N';

ALTER TABLE c_pos_declaracionvalores_voided OWNER TO libertya;

--20140905-0250 Vista de pedidos
CREATE OR REPLACE VIEW v_order as
select o.ad_client_id, 
	o.ad_org_id, 
	o.c_order_id, 
	o.documentno,
	o.c_doctypetarget_id,
	o.issotrx,
	o.c_bpartner_id,
	o.dateordered,
	o.grandtotal,
	(CASE WHEN qtyordered <> qtydelivered THEN 'Y' ELSE 'N' END) as partial,
	(select movementdate 
	from m_inout io
	inner join c_doctype dt on io.c_doctype_id = dt.c_doctype_id
	where io.c_order_id = o.c_order_id and docstatus in ('CO','CL') and (CASE WHEN o.issotrx = 'Y' THEN dt.signo_issotrx = -1 ELSE dt.signo_issotrx = 1 END)
	order by io.updated desc
	limit 1) as lastmovementdate
from (select o.ad_client_id, 
	o.ad_org_id, 
	o.c_order_id, 
	o.documentno,
	o.c_doctypetarget_id,
	o.issotrx,
	o.c_bpartner_id,
	o.grandtotal,
	o.dateordered,
	sum(qtyordered) as qtyordered,
	sum(qtyordered) as qtyreserved,
	sum(qtydelivered) as qtydelivered
from c_order o
inner join c_orderline ol on o.c_order_id = ol.c_order_id
where docstatus in ('CO','CL')
group by o.ad_client_id, 
	o.ad_org_id, 
	o.c_order_id, 
	o.documentno,
	o.c_doctypetarget_id,
	o.issotrx,
	o.c_bpartner_id,
	o.grandtotal,
	o.dateordered) as o
order by o.dateordered;
ALTER TABLE v_order OWNER TO libertya;

--20140908-2340 Vista de pedidos modificada la columna de parcial tomando en cuenta los valores de una lista
DROP VIEW v_order;

CREATE OR REPLACE VIEW v_order AS 
 SELECT o.ad_client_id, o.ad_org_id, o.c_order_id, o.documentno, o.c_doctypetarget_id, o.issotrx, o.c_bpartner_id, date_trunc('day'::text, o.dateordered) AS dateordered, o.grandtotal, 
        (CASE
            WHEN o.qtydelivered = 0 THEN 'N'
            WHEN o.qtyordered <> o.qtydelivered THEN 'P'
            ELSE 'C'
        END)::text AS partial, ( SELECT io.movementdate
           FROM m_inout io
      JOIN c_doctype dt ON io.c_doctype_id = dt.c_doctype_id
     WHERE io.c_order_id = o.c_order_id AND (io.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND 
           CASE
               WHEN o.issotrx = 'Y'::bpchar THEN dt.signo_issotrx = (-1)
               ELSE dt.signo_issotrx = 1
           END
     ORDER BY io.updated DESC
    LIMIT 1) AS lastmovementdate
   FROM ( SELECT o.ad_client_id, o.ad_org_id, o.c_order_id, o.documentno, o.c_doctypetarget_id, o.issotrx, o.c_bpartner_id, o.grandtotal, o.dateordered, sum(ol.qtyordered) AS qtyordered, sum(ol.qtyordered) AS qtyreserved, sum(ol.qtydelivered) AS qtydelivered
           FROM c_order o
      JOIN c_orderline ol ON o.c_order_id = ol.c_order_id
     WHERE o.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
     GROUP BY o.ad_client_id, o.ad_org_id, o.c_order_id, o.documentno, o.c_doctypetarget_id, o.issotrx, o.c_bpartner_id, o.grandtotal, o.dateordered) o
  ORDER BY o.dateordered;

ALTER TABLE v_order OWNER TO libertya;

--20141004-0237 Incorporación de columnas necesarias para el proceso de rechazo de cheques
ALTER TABLE c_payment ADD COLUMN checkstatus character(1);
ALTER TABLE c_payment ADD COLUMN rejecteddate timestamp without time zone;
ALTER TABLE c_payment ADD COLUMN rejectedcomments character varying(1000);
ALTER TABLE ad_clientinfo ADD COLUMN m_product_rejectedcheck_id integer; 

--20141007-1350 Ampliar longitud de column value en AD_Preference (originalmente en apenas 60 caracteres)
ALTER TABLE AD_Preference ALTER COLUMN Value TYPE VARCHAR(1024);

--20141008-0400 Incorporación de configuración de acceso con seguridad de usuario para los campos de nota de crédito de TPV, Nota de Crédito en OP/RC y Crear Desde de Remitos
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role', 'addsecurityvalidation_pos_nc', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role', 'addsecurityvalidation_oprc_nc', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role', 'addsecurityvalidation_createfromshipment', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20141008-2131 Nueva vista con mejoras a la query de cuenta corriente, se muestran las transacciones de la organización parámetro
CREATE OR REPLACE VIEW v_documents_org AS 
        (         SELECT distinct 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx
                   FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
        UNION ALL 
                 SELECT distinct 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, coalesce(i.ad_org_id, p.ad_org_id) as ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx
                   FROM c_payment p
                   INNER JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
                   LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
                   LEFT JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
		WHERE (CASE WHEN i.ad_org_id is not null AND i.ad_org_id <> p.ad_org_id THEN p.docstatus IN ('CO','CL') ELSE 1=1 END))
UNION ALL 
         SELECT distinct 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, coalesce(i.ad_org_id, cl.ad_org_id) as ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE i.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id, 
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
                CASE
                    WHEN cl.amount < 0.0 THEN 'N'::bpchar
                    ELSE 'Y'::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx
           FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
              FROM c_doctype d
             WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id
   WHERE (CASE WHEN i.ad_org_id is not null AND i.ad_org_id <> cl.ad_org_id THEN cl.docstatus IN ('CO','CL') ELSE 1=1 END);
ALTER TABLE v_documents_org OWNER TO libertya;

--20141010-2320 Incorporación de columna para registrar una descripción de la línea del allocation 
update ad_system set dummy = (SELECT addcolumnifnotexists('c_allocationline', 'line_description', 'character varying(255)'));
UPDATE c_allocationline al
SET line_description = (SELECT documentno || '_' || to_char(datetrx, 'YYYYMMDD') 
			FROM c_allocationhdr ah
			WHERE ah.c_allocationhdr_id = al.c_allocationhdr_id) || '_' || amount;

--20141010-2320 Mejora de performance a la vista c_posjournalpayments_v
CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
        (        ( SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
                            WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
                            ELSE NULL::character varying
                        END::character varying(2) AS tendertype, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.documentno
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
                            ELSE NULL::character varying
                        END::character varying(30) AS documentno, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.description
                            WHEN al.c_cashline_id IS NOT NULL THEN cl.description
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.description
                            ELSE NULL::character varying
                        END::character varying(255) AS description, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN ((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying
                            WHEN al.c_cashline_id IS NOT NULL THEN ((c.name::text || '_#'::text) || cl.line::text)::character varying
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
                            ELSE NULL::character varying
                        END::character varying(255) AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, ic.c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, ah.datetrx) AS allocationdate, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.docstatus
                            WHEN al.c_cashline_id IS NOT NULL THEN cl.docstatus
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.docstatus
                            ELSE NULL::character(2)
                        END AS docstatus, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.dateacct::date
                            WHEN al.c_cashline_id IS NOT NULL THEN c.dateacct::date
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.dateacct::date
                            ELSE NULL::date
                        END AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, dt.isfiscaldocument, dt.isfiscal, ic.fiscalalreadyprinted, date_trunc('day'::text, ah.datetrx) AS allocationdateacct
                   FROM c_allocationline al
              JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
         LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
    LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
   LEFT JOIN c_doctype dt ON dt.c_doctype_id = ic.c_doctypetarget_id
  ORDER BY 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
    WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
    WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
    ELSE NULL::character varying
END::character varying(2), 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.documentno
    WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
    ELSE NULL::character varying
END::character varying(30))
        UNION ALL 
                 SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::unknown AS c_invoice_id, NULL::unknown AS c_payment_id, cl.c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CA'::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || '_#'::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::unknown AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc('day'::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted, date_trunc('day'::text, c.dateacct) AS allocationdateacct
                   FROM c_cashline cl
              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
		LEFT JOIN c_allocationline al on al.c_cashline_id = cl.c_cashline_id
		where c_allocationline_id is null)
UNION ALL 
        ( SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::unknown AS c_invoice_id, p.c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, p.payamt AS amount, NULL::unknown AS c_cash_id, NULL::numeric(18,0) AS line, NULL::unknown AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted, date_trunc('day'::text, p.dateacct) AS allocationdateacct
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  LEFT JOIN c_allocationline al on al.c_payment_id = p.c_payment_id
  where c_allocationline_id is null 
  ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

--20141011-0240 Incorporación de fecha de recepción a la factura y fecha de aplicación (fecha de factura o recepción) para aplicar el esquema de vencimientos
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice', 'daterecepted', 'timestamp without time zone'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_paymentterm', 'applicationdate', 'character(1) NOT NULL DEFAULT ''I'''));

--20141016-01:40 Nuevo parámetro para mostrar solo los comprobantes en cuenta corriente
--20141016-01:40 Eliminación de vistas
DROP VIEW v_projectedpayments;
DROP VIEW v_documents_org;
DROP VIEW v_documents;
DROP VIEW rv_bpartneropen;
DROP VIEW c_invoiceline_v;
DROP VIEW c_invoice_v;

--20141016-01:40 Creación de vista c_invoice_v
CREATE OR REPLACE VIEW c_invoice_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.chargeamt * d.signo_issotrx::numeric AS chargeamt, i.totallines, i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal, d.signo_issotrx::numeric AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange, i.initialcurrentaccountamt
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  WHERE i.ispayschedulevalid <> 'Y'::bpchar
UNION ALL 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, ips.c_invoicepayschedule_id, NULL::unknown AS chargeamt, NULL::unknown AS totallines, ips.dueamt AS grandtotal, d.signo_issotrx AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange, i.initialcurrentaccountamt
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
   JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE c_invoice_v OWNER TO libertya;

--20141016-01:40 Creación de vista c_invoiceline_v
CREATE OR REPLACE VIEW c_invoiceline_v AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, il.m_product_id, i.documentno, i.dateinvoiced, i.dateacct, i.issotrx, i.docstatus, round(i.multiplier * il.linenetamt, 2) AS linenetamt, round(i.multiplier * il.pricelist * il.qtyinvoiced, 2) AS linelistamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN round(i.multiplier * il.linenetamt, 2)
            ELSE round(i.multiplier * il.pricelimit * il.qtyinvoiced, 2)
        END AS linelimitamt, round(i.multiplier * il.pricelist * il.qtyinvoiced - il.linenetamt, 2) AS linediscountamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round(i.multiplier * il.linenetamt - il.pricelimit * il.qtyinvoiced, 2)
        END AS lineoverlimitamt, il.qtyinvoiced, il.qtyentered, il.line, il.c_orderline_id, il.c_uom_id
   FROM c_invoice_v i, c_invoiceline il
  WHERE i.c_invoice_id = il.c_invoice_id;

ALTER TABLE c_invoiceline_v OWNER TO libertya;

--20141016-01:40 Creación de vista rv_bpartneropen
CREATE OR REPLACE VIEW rv_bpartneropen AS 
 SELECT i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_currency_id, i.grandtotal * i.multiplierap AS amt, invoiceopen(i.c_invoice_id, i.c_invoicepayschedule_id) * i.multiplierap AS openamt, i.dateinvoiced AS datedoc, COALESCE(daysbetween(now(), ips.duedate::timestamp with time zone), paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now())) AS daysdue
   FROM c_invoice_v i
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  WHERE i.ispaid = 'N'::bpchar AND (i.docstatus = 'CO'::bpchar OR i.docstatus = 'CL'::bpchar)
UNION 
 SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_currency_id, p.payamt * p.multiplierap * (- 1::numeric) AS amt, paymentavailable(p.c_payment_id) * p.multiplierap * (- 1::numeric) AS openamt, p.datetrx AS datedoc, NULL::unknown AS daysdue
   FROM c_payment_v p
  WHERE p.isallocated = 'N'::bpchar AND p.c_bpartner_id IS NOT NULL AND (p.docstatus = 'CO'::bpchar OR p.docstatus = 'CL'::bpchar);

ALTER TABLE rv_bpartneropen OWNER TO libertya;

--20141016-01:40 Creación de vista v_documents
CREATE OR REPLACE VIEW v_documents AS 
( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, i.initialcurrentaccountamt
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
UNION ALL 
 SELECT 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, 0.00 as initialcurrentaccountamt
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id)
UNION ALL 
 SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, 0.00 as initialcurrentaccountamt
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id;

ALTER TABLE v_documents OWNER TO libertya;

--20141016-01:40 Creación de vista v_documents_org
CREATE OR REPLACE VIEW v_documents_org AS 
(( SELECT DISTINCT 'C_Invoice'::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, i.initialcurrentaccountamt
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  ORDER BY 'C_Invoice'::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus, 
CASE
    WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
    ELSE i.dateinvoiced
END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced)
UNION ALL 
( SELECT DISTINCT 'C_Payment'::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(i.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, coalesce(i.initialcurrentaccountamt,0.00) as initialcurrentaccountamt
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
   LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
  WHERE 
CASE
    WHEN i.ad_org_id IS NOT NULL AND i.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
    ELSE 1 = 1
END
  ORDER BY 'C_Payment'::text, p.c_payment_id, p.ad_client_id, COALESCE(i.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, p.datetrx))
UNION ALL 
( SELECT DISTINCT 'C_CashLine'::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(i.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, coalesce(i.initialcurrentaccountamt,0.00) as initialcurrentaccountamt
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id
  WHERE 
CASE
    WHEN i.ad_org_id IS NOT NULL AND i.ad_org_id <> cl.ad_org_id THEN cl.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
    ELSE 1 = 1
END
  ORDER BY 'C_CashLine'::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(i.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
CASE
    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
    ELSE i.c_bpartner_id
END, dt.c_doctype_id, 
CASE
    WHEN cl.amount < 0.0 THEN 1
    ELSE (-1)
END, dt.name, dt.printname, '@line@'::text || cl.line::character varying::text, 
CASE
    WHEN cl.amount < 0.0 THEN 'N'::bpchar
    ELSE 'Y'::bpchar
END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::integer, NULL::timestamp without time zone, c.statementdate);

ALTER TABLE v_documents_org OWNER TO libertya;

--20141016-01:40 Creación de vista v_projectedpayments
CREATE OR REPLACE VIEW v_projectedpayments AS 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, v.doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount * v.signo_issotrx::numeric * (-1)::numeric AS amount, v.c_invoicepayschedule_id, v.duedate, i.ispaid, NULL::character varying(20) AS checkno, invoiceopen(i.c_invoice_id, 0) * v.signo_issotrx::numeric * (-1)::numeric AS openamount, i.c_invoice_id, NULL::integer AS c_payment_id, ip.duedate AS filterdate
   FROM v_documents v
   JOIN c_invoice i ON i.c_invoice_id = v.document_id
   JOIN c_invoicepayschedule ip ON ip.c_invoice_id = v.document_id AND ip.c_invoicepayschedule_id = v.c_invoicepayschedule_id
  WHERE i.issotrx = 'N'::bpchar AND v.documenttable = 'C_Invoice'::text AND (v.docstatus = 'CO'::bpchar OR v.docstatus = 'CL'::bpchar)
UNION ALL 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, 'Cheque' AS doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount * v.signo_issotrx::numeric * (-1)::numeric AS amount, v.c_invoicepayschedule_id, v.duedate, NULL::character(1) AS ispaid, p.checkno, paymentavailable(p.c_payment_id) * v.signo_issotrx::numeric * (-1)::numeric AS openamount, NULL::integer AS c_invoice_id, p.c_payment_id, p.duedate AS filterdate
   FROM v_documents v
   JOIN c_payment p ON p.c_payment_id = v.document_id
  WHERE p.issotrx = 'N'::bpchar AND p.tendertype = 'K'::bpchar AND p.datetrx < p.duedate AND (v.docstatus = 'CO'::bpchar OR v.docstatus = 'CL'::bpchar);

ALTER TABLE v_projectedpayments OWNER TO libertya;

--20141016-01:40 Creación de columna en la tabla temporal de cuenta corriente para el nuevo filtro
ALTER TABLE t_cuentacorriente ADD COLUMN OnlyCurrentAccountDocuments character(1) NOT NULL DEFAULT 'N'::bpchar;

--20141016-0907 Columna para nuevo boton que sirve para crear transferencias a partir de pedidos
update ad_system set dummy = (SELECT addcolumnifnotexists('m_transfer', 'crearpedidodesde', 'character(1)'));

--20141016-1002 Columna para a Actualizar Precios con Factura de Compra
update ad_system set dummy = (SELECT addcolumnifnotexists('m_pricelist', 'ActualizarPreciosconFacturadeCompra', 'character(1)'));

--20141016-1002 Columna para indicar Fecha de TC para Actualizar Precios
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice', 'FechadeTCparaActualizarPrecios', 'timestamp without time zone'));

--20141021-1500 Nuevas incorporaciones al Informe de Saldos por filtros nuevos y datos a visualizar en el reporte
update ad_system set dummy = (SELECT addcolumnifnotexists('t_balancereport', 'onlycurrentaccounts', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_balancereport', 'valuefrom', 'character varying(42)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_balancereport', 'valueto', 'character varying(42)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_balancereport', 'duedebt', 'numeric(12,2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_balancereport', 'actualbalance', 'numeric(12,2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_balancereport', 'chequesencartera', 'numeric(12,2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_balancereport', 'generalbalance', 'numeric(12,2)'));

--20141021-1500 Modificación del tamaño de los campos numéricos por error de limitación
ALTER TABLE t_balancereport ALTER COLUMN debit TYPE numeric(12,2);
ALTER TABLE t_balancereport ALTER COLUMN credit TYPE numeric(12,2);
ALTER TABLE t_balancereport ALTER COLUMN balance TYPE numeric(12,2);

--20141021-1500 Eliminación de vistas dependientes por modificación de vista c_invoice_v
DROP VIEW v_projectedpayments;
DROP VIEW v_documents_org;
DROP VIEW v_documents;
DROP VIEW rv_bpartneropen;
DROP VIEW c_invoiceline_v;
DROP VIEW c_invoice_v;

--20141021-1500 Creación vista c_invoice_v
CREATE OR REPLACE VIEW c_invoice_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.chargeamt * d.signo_issotrx::numeric AS chargeamt, i.totallines, i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal, d.signo_issotrx::numeric AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange, i.initialcurrentaccountamt, i.dateacct as duedate
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  WHERE i.ispayschedulevalid <> 'Y'::bpchar
UNION ALL 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, ips.c_invoicepayschedule_id, NULL::unknown AS chargeamt, NULL::unknown AS totallines, ips.dueamt AS grandtotal, d.signo_issotrx AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange, i.initialcurrentaccountamt, ips.duedate
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
   JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE c_invoice_v OWNER TO libertya;

--20141021-1500 Creación vista c_invoiceline_v
CREATE OR REPLACE VIEW c_invoiceline_v AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, il.m_product_id, i.documentno, i.dateinvoiced, i.dateacct, i.issotrx, i.docstatus, round(i.multiplier * il.linenetamt, 2) AS linenetamt, round(i.multiplier * il.pricelist * il.qtyinvoiced, 2) AS linelistamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN round(i.multiplier * il.linenetamt, 2)
            ELSE round(i.multiplier * il.pricelimit * il.qtyinvoiced, 2)
        END AS linelimitamt, round(i.multiplier * il.pricelist * il.qtyinvoiced - il.linenetamt, 2) AS linediscountamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round(i.multiplier * il.linenetamt - il.pricelimit * il.qtyinvoiced, 2)
        END AS lineoverlimitamt, il.qtyinvoiced, il.qtyentered, il.line, il.c_orderline_id, il.c_uom_id
   FROM c_invoice_v i, c_invoiceline il
  WHERE i.c_invoice_id = il.c_invoice_id;

ALTER TABLE c_invoiceline_v OWNER TO libertya;

--20141021-1500 Creación vista rv_bpartneropen
CREATE OR REPLACE VIEW rv_bpartneropen AS 
 SELECT i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_currency_id, i.grandtotal * i.multiplierap AS amt, invoiceopen(i.c_invoice_id, i.c_invoicepayschedule_id) * i.multiplierap AS openamt, i.dateinvoiced AS datedoc, COALESCE(daysbetween(now(), ips.duedate::timestamp with time zone), paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now())) AS daysdue
   FROM c_invoice_v i
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  WHERE i.ispaid = 'N'::bpchar AND (i.docstatus = 'CO'::bpchar OR i.docstatus = 'CL'::bpchar)
UNION 
 SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_currency_id, p.payamt * p.multiplierap * (- 1::numeric) AS amt, paymentavailable(p.c_payment_id) * p.multiplierap * (- 1::numeric) AS openamt, p.datetrx AS datedoc, NULL::unknown AS daysdue
   FROM c_payment_v p
  WHERE p.isallocated = 'N'::bpchar AND p.c_bpartner_id IS NOT NULL AND (p.docstatus = 'CO'::bpchar OR p.docstatus = 'CL'::bpchar);

ALTER TABLE rv_bpartneropen OWNER TO libertya;

--20141021-1500 Creación vista v_documents
CREATE OR REPLACE VIEW v_documents AS 
        (         SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, i.initialcurrentaccountamt, bp.socreditstatus
                   FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
              INNER JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
         LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
        UNION ALL 
                 SELECT DISTINCT 'C_Payment'::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, bp.socreditstatus
                   FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
              INNER JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
         LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
    LEFT JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id)
UNION ALL 
         SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE i.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id, 
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
                CASE
                    WHEN cl.amount < 0.0 THEN 'N'::bpchar
                    ELSE 'Y'::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, coalesce(bp.socreditstatus, bp2.socreditstatus) as socreditstatus
           FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
      LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
              FROM c_doctype d
             WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_bpartner bp2 ON i.c_bpartner_id = bp2.c_bpartner_id;

ALTER TABLE v_documents OWNER TO libertya;

--20141021-1500 Creación vista v_documents_org
CREATE OR REPLACE VIEW v_documents_org AS 
        (        ( SELECT DISTINCT 'C_Invoice'::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, i.initialcurrentaccountamt, bp.socreditstatus
                   FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
              INNER JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
         LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
        ORDER BY 'C_Invoice'::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus, 
              CASE
                  WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                  ELSE i.dateinvoiced
              END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced)
        UNION ALL 
                ( SELECT DISTINCT 'C_Payment'::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(i.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, bp.socreditstatus
                   FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
              INNER JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
         LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
    LEFT JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
   WHERE 
         CASE
             WHEN i.ad_org_id IS NOT NULL AND i.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
             ELSE 1 = 1
         END
   ORDER BY 'C_Payment'::text, p.c_payment_id, p.ad_client_id, COALESCE(i.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate))
UNION ALL 
        ( SELECT DISTINCT 'C_CashLine'::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(i.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE i.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id, 
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
                CASE
                    WHEN cl.amount < 0.0 THEN 'N'::bpchar
                    ELSE 'Y'::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, coalesce(bp.socreditstatus, bp2.socreditstatus) as socreditstatus
           FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
      LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
              FROM c_doctype d
             WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_bpartner bp2 ON i.c_bpartner_id = bp2.c_bpartner_id
  WHERE 
 CASE
     WHEN i.ad_org_id IS NOT NULL AND i.ad_org_id <> cl.ad_org_id THEN cl.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
     ELSE 1 = 1
 END
  ORDER BY 'C_CashLine'::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(i.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
 CASE
     WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
     ELSE i.c_bpartner_id
 END, dt.c_doctype_id, 
 CASE
     WHEN cl.amount < 0.0 THEN 1
     ELSE (-1)
 END, dt.name, dt.printname, '@line@'::text || cl.line::character varying::text, 
 CASE
     WHEN cl.amount < 0.0 THEN 'N'::bpchar
     ELSE 'Y'::bpchar
 END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone);

ALTER TABLE v_documents_org OWNER TO libertya;

--20141021-1500 Creación vista v_projectedpayments
CREATE OR REPLACE VIEW v_projectedpayments AS 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, v.doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount * v.signo_issotrx::numeric * (-1)::numeric AS amount, v.c_invoicepayschedule_id, v.duedate, i.ispaid, NULL::character varying(20) AS checkno, invoiceopen(i.c_invoice_id, 0) * v.signo_issotrx::numeric * (-1)::numeric AS openamount, i.c_invoice_id, NULL::integer AS c_payment_id, ip.duedate AS filterdate
   FROM v_documents v
   JOIN c_invoice i ON i.c_invoice_id = v.document_id
   JOIN c_invoicepayschedule ip ON ip.c_invoice_id = v.document_id AND ip.c_invoicepayschedule_id = v.c_invoicepayschedule_id
  WHERE i.issotrx = 'N'::bpchar AND v.documenttable = 'C_Invoice'::text AND (v.docstatus = 'CO'::bpchar OR v.docstatus = 'CL'::bpchar)
UNION ALL 
 SELECT v.documenttable, v.document_id, v.ad_client_id, v.ad_org_id, v.isactive, v.created, v.createdby, v.updated, v.updatedby, v.c_bpartner_id, v.c_doctype_id, v.signo_issotrx, 'Cheque' AS doctypename, v.doctypeprintname, v.documentno, v.issotrx, v.docstatus, v.datetrx, v.dateacct, v.c_currency_id, v.c_conversiontype_id, v.amount * v.signo_issotrx::numeric * (-1)::numeric AS amount, v.c_invoicepayschedule_id, v.duedate, NULL::character(1) AS ispaid, p.checkno, paymentavailable(p.c_payment_id) * v.signo_issotrx::numeric * (-1)::numeric AS openamount, NULL::integer AS c_invoice_id, p.c_payment_id, p.duedate AS filterdate
   FROM v_documents v
   JOIN c_payment p ON p.c_payment_id = v.document_id
  WHERE p.issotrx = 'N'::bpchar AND p.tendertype = 'K'::bpchar AND p.datetrx < p.duedate AND (v.docstatus = 'CO'::bpchar OR v.docstatus = 'CL'::bpchar);

ALTER TABLE v_projectedpayments OWNER TO libertya;

--20141023-1430 Incorporación de configuración que permite o no buscar pagos/cobros sin asignar en OP/RC 
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner', 'searchunallocatedpayments', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20141028-2345 Incorporación de columna m_entidadfinanciera_id
DROP VIEW c_pos_declaracionvalores_payments;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_payments AS 
 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::text AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
        CASE p.isreceipt
            WHEN 'Y'::bpchar THEN p.total
            ELSE 0::numeric
        END::numeric(22,2) AS ingreso, 
        CASE p.isreceipt
            WHEN 'N'::bpchar THEN abs(p.total)
            ELSE 0::numeric
        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname, p.m_entidadfinanciera_id
   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount + 
                CASE
                    WHEN p.tendertype = 'C'::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(22,2) AS total, pjp.invoice_documentno, (pjp.invoice_grandtotal + 
                CASE
                    WHEN p.tendertype = 'C'::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(20,2) AS invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname, pjp.m_entidadfinanciera_id
           FROM c_payment p
      JOIN c_posjournal_c_payment_v pjp ON pjp.c_payment_id = p.c_payment_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
  WHERE i.c_invoice_id IS NULL OR NOT (EXISTS ( SELECT cl.c_cashline_id
   FROM c_cashline cl
  WHERE cl.c_payment_id = p.c_payment_id AND i.isvoidable = 'Y'::bpchar))
  GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.changeamt, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name, pjp.m_entidadfinanciera_id) p;

ALTER TABLE c_pos_declaracionvalores_payments OWNER TO libertya;

--20141106-1330 Incorporación de columna isvoidable a la vista c_invoice_bydoctype_v para que no se visualicen en el informe de ventas los documentos anulables como por ejemplo los de retiro de efectivo por tarjeta 
DROP VIEW c_invoice_bydoctype_v;

CREATE OR REPLACE VIEW c_invoice_bydoctype_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, i.chargeamt, (i.chargeamt * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS chargeamt_withsign, (i.chargeamt * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS chargeamt_withmultiplier, i.totallines, (i.totallines * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS totallines_withsign, (i.totallines * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS totallines_withmultiplier, i.grandtotal, (i.grandtotal * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS grandtotal_withsign, (i.grandtotal * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS grandtotal_withmultiplier, d.signo_issotrx, 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END AS multiplier, d.docbasetype, d.doctypekey, d.name AS doctypename, i.nombrecli, i.isvoidable
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id;

ALTER TABLE c_invoice_bydoctype_v OWNER TO libertya;

--20141111-1215 Cambio en trigger de replicacion. En la modificacion de un registro, si la longitud del reparray del registro es menor que el de la configuracion, se completan las celdas adecuadamente 
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
	recordColumns RECORD;
	columnValue varchar;
	isValid integer;
	v_valueDetail varchar;
	v_nameDetail varchar;
	v_columnname varchar;
	v_tableid int; 
	v_tablename varchar;
	v_referenceStr varchar;
	v_referencedTableStr varchar;
	v_referencedTableID int;
	v_existsValueField int;
	v_existsNameField int;
	shouldcheckreferences boolean;
	checkreferencestableconf character;
BEGIN 
	-- se deberan verificar referencias a registros fuera del esquema de replicacion? (inicialmente no)
	shouldcheckreferences := false;

	-- estamos en una accion de eliminacion?
	IF (TG_OP = 'DELETE') THEN

		-- Checkear switch maestro de replicacion
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;

		-- Se repArray es nulo o vacio, no hay mas que hacer dado que es un registro fuera de replicacion
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;

		-- El registro fue replicado? Si no lo fue puede ser eliminado, pero en caso contrario hay que registrar su eliminacion
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN

			-- Recuperar el repArray de la tabla en cuestion
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
			v_newRepArray := replace(v_newRepArray, '3', '1');
			v_newRepArray := replace(v_newRepArray, '2', '0');

			-- Insertar en la tabla de eliminaciones
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues, includeInReplication)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',now(),OLD.CreatedBy,now(),OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null,'Y';
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	-- estamos en una accion de insercion o actualizacion?
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Checkear switch maestro de replicacion		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;

		-- El uso de SKIP se supone para omitir acciones posteriores en eliminacion (dado que setea repArray en NULL)
		IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		END IF;

		-- Verificar si hay que generar el retrieveUID. Ejemplo: h1_291
		IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			-- Primeramente intentar utilizar el AD_ComponentObjectUID (registro perteneciente a un componente)
			BEGIN
				IF NEW.AD_ComponentObjectUID IS NOT NULL AND NEW.AD_ComponentObjectUID <> '' THEN
					NEW.retrieveUID = NEW.AD_ComponentObjectUID;
				END IF;
			EXCEPTION
				WHEN OTHERS THEN
					-- Do nothing
			END;
			-- Si el registro no pertenece a un componente, generar el retrieveUID
		        IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
				-- Obtener posicion de host
				SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
				IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
				-- Obtener siguiente valor para la tabla dada
				SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
				IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
					NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq || '_' || lower(TG_ARGV[1]);
				END IF;		
			END IF;

		-- Si estamos insertando...
		IF (TG_OP = 'INSERT') THEN

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- Recuperar el repArray
				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;

				-- Si es nulo o vacio no hacer nada mas
				IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
					RETURN NEW;
				END IF;

				-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
				NEW.repArray := replace(v_newRepArray, '3', '1');
				NEW.repArray := replace(NEW.repArray, '2', '0');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 1)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('1' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				ELSE
					NEW.repArray := NULL;
				END IF;
			END IF;
			
		-- Si estamos actualizando...
		ELSEIF (TG_OP = 'UPDATE') THEN 

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.		
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE

				-- Recuperar el repArray
				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;

				-- El repArray no esta seteado todavia? (Caso: modificacion de un registro preexistente)
				IF (OLD.repArray IS NULL OR OLD.repArray = '0') THEN

					-- Cambiar 2 (recibir) por 0 (sin accion)
					NEW.repArray := replace(v_newRepArray, '2', '0');
					-- Cambiar 1 (enviar) y 3 (bidireccional) por 2 (replicado)
					NEW.repArray := replace(NEW.repArray, '1', '2');
					NEW.repArray := replace(NEW.repArray, '3', '2');
				ELSE
					-- El repArray ya fue seteado anteriormente, hay que verificar si la configuracion de repArray fue ampliada
					-- El v_newRepArray tiene una longitud MAYOR que el reparray existente actualmente? Completar con la configuracion
					IF (length(v_newRepArray) > length(NEW.repArray)) THEN
						NEW.repArray := rpad(NEW.repArray, length(v_newRepArray), substr(replace(replace(v_newRepArray, '3', '1'), '2', '0'), length(NEW.repArray)+1));
					END IF;
				END IF;

				-- Cambiar los 2 (replicado) por 3 (modificado).
				-- Adicionalmente para JMS: 4 (espera ack) por 5 (cambios luego de ack)
				NEW.repArray := replace(NEW.repArray, '2', '3');
				NEW.repArray := replace(NEW.repArray, '4', '5');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 3)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('3' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				END IF;
			END IF;
		END IF;
	END IF;

	IF (shouldcheckreferences = true) THEN

		-- Verificar si la tabla tiene configurado que hay que chequear referencias
		SELECT into checkreferencestableconf CheckReferences FROM ad_tablereplication WHERE AD_table_id = TG_ARGV[0]::int;
		IF (checkreferencestableconf = 'N') THEN
			return NEW;
		END IF;

		-- Validar referencias iterando todas las columnas de la tabla
		FOR recordColumns IN
			SELECT isc.column_name, isc.data_type, c.ad_column_id, t.tablename, t.ad_table_id
			FROM information_schema.columns isc
			INNER JOIN ad_table t ON lower(isc.table_name) = lower(t.tablename)
			INNER JOIN ad_column c ON lower(isc.column_name) = lower(c.columnname) AND t.ad_table_id = c.ad_table_id
			WHERE table_name = quote_ident(TG_TABLE_NAME)
			AND isc.data_type = 'integer'
			AND isc.column_name not in ('retrieveuid', 'reparray', 'datelastsentjms', 'includeinreplication')
		LOOP
			-- Obtener el value de la columna y verificar si es referencia valida.  En caso de no serlo, presentar error correspondiente
			EXECUTE 'SELECT (' || quote_literal(NEW) || '::' || TG_RELID::regclass || ').' || quote_ident(recordColumns.column_name) INTO columnValue;
			SELECT INTO isValid replication_is_valid_reference(recordColumns.ad_column_id, columnvalue);
			IF isValid = 0 THEN

				-- valores por defecto
				v_valueDetail := '';
				v_nameDetail := '';
				v_referenceStr := '';
				
				-- recuperar el nombre de la columna y tabla para brindar un mensaje mas intuitivo al usuario
				v_columnname := recordColumns.column_name;
				v_tablename := recordColumns.tablename;

				BEGIN 
					-- recuperar el nombre o value del registro referenciado a fin de mejorar la legibilidad del mensaje de error
					SELECT INTO v_referencedTableStr replication_get_referenced_table(recordColumns.ad_column_id);
					SELECT INTO v_referencedTableID AD_Table_ID FROM AD_Table WHERE tablename ilike v_referencedTableStr;

					-- ver si existe las columnas value y name
					SELECT INTO v_existsValueField Count(1) FROM AD_Column WHERE columnname ilike 'Value' AND AD_Table_ID = v_referencedTableID;
					SELECT INTO v_existsNameField Count(1) FROM AD_Column WHERE columnname ilike 'Name' AND AD_Table_ID = v_referencedTableID;

					-- cargar value y name
					IF v_existsValueField = 1 THEN
						EXECUTE 'SELECT value FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_valueDetail;
						v_referenceStr := v_valueDetail;
					END IF;
					IF v_existsNameField = 1 THEN
						EXECUTE 'SELECT name FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_nameDetail;
						v_referenceStr := v_referenceStr || ' ' || v_nameDetail;
					END IF;
				EXCEPTION
					WHEN OTHERS THEN
						-- do nothing
				END;
				
				-- concatenar acordemente para mensaje de retorno
				RAISE EXCEPTION 'Validacion de replicación - La columna: % (%) de la tabla: % (%) referencia al registro: % (%), fuera del sistema de replicacion.', v_columnname, recordColumns.ad_column_id, v_tablename, recordColumns.ad_table_id, v_referenceStr, columnvalue;
			END IF;
		END LOOP;

	END IF;
	RETURN NEW;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

--20141114-1045 Incorporación de flag para determinar si la EC puede poseer un cuit repetido
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner', 'ismulticuit', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_bpartner', 'ismulticuit', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20141117-1130 Incorporación que permite buscar por nro de identificación a las entidades comerciales en el importador de facturas
update ad_system set dummy = (SELECT addcolumnifnotexists('i_invoice', 'taxid', 'character varying(20)'));

--20141121-0200 Fix a la vista por duplicados
CREATE OR REPLACE VIEW v_documents_org AS 
        (        ( SELECT DISTINCT 'C_Invoice'::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, i.initialcurrentaccountamt, bp.socreditstatus
                   FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
   ORDER BY 'C_Invoice'::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus, 
         CASE
             WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
             ELSE i.dateinvoiced
         END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced)
        UNION ALL 
                ( SELECT DISTINCT 'C_Payment'::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, 
	COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, 
	p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, 
	p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, 
	p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, 
	COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, bp.socreditstatus
FROM c_payment p
JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
LEFT JOIN (SELECT c_payment_id, sum(initialcurrentaccountamt) as initialcurrentaccountamt
		FROM (SELECT distinct al.c_payment_id, al.c_invoice_id, i.initialcurrentaccountamt
			FROM c_allocationline al 
			INNER JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id) as di
		GROUP BY c_payment_id) as i on i.c_payment_id = p.c_payment_id
LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
WHERE 
    CASE
        WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
        ELSE 1 = 1
    END
  ORDER BY 'C_Payment'::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate))
UNION ALL 
        ( SELECT DISTINCT 'C_CashLine'::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE il.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id, 
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
                CASE
                    WHEN cl.amount < 0.0 THEN 'N'::bpchar
                    ELSE 'Y'::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus
           FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN (SELECT c_cashline_id, sum(initialcurrentaccountamt) as initialcurrentaccountamt
		FROM (SELECT distinct lc.c_cashline_id, lc.c_invoice_id, i.initialcurrentaccountamt
			FROM c_cashline lc 
			INNER JOIN c_invoice i ON i.c_invoice_id = lc.c_invoice_id) as di
		GROUP BY c_cashline_id) as i on i.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON cl.c_invoice_id = il.c_invoice_id
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id THEN cl.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
    ELSE 1 = 1
END
  ORDER BY 'C_CashLine'::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
CASE
    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
    ELSE il.c_bpartner_id
END, dt.c_doctype_id, 
CASE
    WHEN cl.amount < 0.0 THEN 1
    ELSE (-1)
END, dt.name, dt.printname, '@line@'::text || cl.line::character varying::text, 
CASE
    WHEN cl.amount < 0.0 THEN 'N'::bpchar
    ELSE 'Y'::bpchar
END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone);

ALTER TABLE v_documents_org OWNER TO libertya;

--20141223-1235 Incorporación de flag en la unidad de medida para permitir o no decimales en las cantidades de líneas de pedido, facturas y remitos.
update ad_system set dummy = (SELECT addcolumnifnotexists('c_uom', 'allowdecimals', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20150126-1330 Fix a la vista de cuenta corriente de organización ya que en efectivo no estaba trayendo realmente las facturas que pagaba una línea en efectivo cuando eran varias
CREATE OR REPLACE VIEW v_documents_org AS 
(( SELECT DISTINCT 'C_Invoice'::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, i.initialcurrentaccountamt, bp.socreditstatus
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  ORDER BY 'C_Invoice'::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus, 
CASE
    WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
    ELSE i.dateinvoiced
END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, i.initialcurrentaccountamt, bp.socreditstatus)
UNION ALL 
( SELECT DISTINCT 'C_Payment'::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, bp.socreditstatus
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
   JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
   LEFT JOIN ( SELECT di.c_payment_id, sum(di.initialcurrentaccountamt) AS initialcurrentaccountamt
   FROM ( SELECT DISTINCT al.c_payment_id, al.c_invoice_id, i.initialcurrentaccountamt
           FROM c_allocationline al
      JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
     ORDER BY al.c_payment_id, al.c_invoice_id, i.initialcurrentaccountamt) di
  GROUP BY di.c_payment_id) i ON i.c_payment_id = p.c_payment_id
   LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
    ELSE 1 = 1
END
  ORDER BY 'C_Payment'::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, p.datetrx, COALESCE(i.initialcurrentaccountamt, 0.00), bp.socreditstatus))
UNION ALL 
( SELECT DISTINCT 'C_CashLine'::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE il.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE (-1)
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
   FROM c_doctype d
  WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN ( SELECT di.c_cashline_id, sum(di.initialcurrentaccountamt) AS initialcurrentaccountamt
   FROM ( SELECT DISTINCT lc.c_cashline_id, lc.c_invoice_id, i.initialcurrentaccountamt
           FROM c_cashline lc
      JOIN c_invoice i ON i.c_invoice_id = lc.c_invoice_id
     ORDER BY lc.c_cashline_id, lc.c_invoice_id, i.initialcurrentaccountamt) di
  GROUP BY di.c_cashline_id) i ON i.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id THEN cl.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
    ELSE 1 = 1
END
  ORDER BY 'C_CashLine'::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
CASE
    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
    ELSE il.c_bpartner_id
END, dt.c_doctype_id, 
CASE
    WHEN cl.amount < 0.0 THEN 1
    ELSE (-1)
END, dt.name, dt.printname, '@line@'::text || cl.line::character varying::text, 
CASE
    WHEN cl.amount < 0.0 THEN 'N'::bpchar
    ELSE 'Y'::bpchar
END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, NULL::integer, c.statementdate, COALESCE(i.initialcurrentaccountamt, 0.00), COALESCE(bp.socreditstatus, bp2.socreditstatus));

ALTER TABLE v_documents_org OWNER TO libertya;