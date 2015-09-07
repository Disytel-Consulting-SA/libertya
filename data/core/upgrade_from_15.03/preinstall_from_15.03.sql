-- ========================================================================================
-- PREINSTALL FROM 15.03
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20150317-1110 Incorporación de nuevas columnas a la vista de detalle de movimientos de artículo
DROP VIEW v_product_movements_detailed;

CREATE OR REPLACE VIEW v_product_movements_detailed AS 
 SELECT t.movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, w.m_warehouse_id, w.value AS warehouse_value, w.name AS warehouse_name, t.receiptvalue, t.movementdate, t.doctypename, t.documentno, t.docstatus, t.m_product_id, t.product_value, t.product_name, t.qty, t.c_invoice_id, i.documentno AS invoice_documentno, t.created, t.updated
   FROM (        (        (        (        (        (         SELECT 'M_InOut' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                                                                CASE dt.signo_issotrx
                                                                    WHEN 1 THEN 
                                                                    CASE abs(t.movementqty)
                                                                        WHEN t.movementqty THEN 'Y'::text
                                                                        ELSE 'N'::text
                                                                    END
                                                                    ELSE 
                                                                    CASE abs(t.movementqty)
                                                                        WHEN t.movementqty THEN 'Y'::text
                                                                        ELSE 'N'::text
                                                                    END
                                                                END AS receiptvalue, t.movementdate, dt.name AS doctypename, io.documentno, io.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, ( SELECT i.c_invoice_id
                                                                   FROM c_order o
                                                              JOIN c_invoice i ON i.c_order_id = o.c_order_id
                                                             WHERE o.c_order_id = io.c_order_id
                                                            LIMIT 1) AS c_invoice_id, io.created, io.updated
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
                                                                END AS receiptvalue, t.movementdate, dt.name AS doctypename, m.documentno, m.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id, m.created, m.updated
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
                                                        END AS receiptvalue, t.movementdate, dt.name AS doctypename, i.documentno, i.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id, i.created, i.updated
                                                   FROM m_transaction t
                                              JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
                                         JOIN m_product p ON p.m_product_id = t.m_product_id
                                    JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
                               JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
                          LEFT JOIN m_transfer tr ON tr.m_inventory_id = i.m_inventory_id
                     LEFT JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
                     LEFT JOIN m_splitting spv ON spv.void_inventory_id = i.m_inventory_id
                LEFT JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id
                LEFT JOIN m_productchange pcv ON pcv.void_inventory_id = i.m_inventory_id
               WHERE tr.m_transfer_id IS NULL AND sp.m_splitting_id IS NULL AND pc.m_productchange_id IS NULL AND spv.m_splitting_id IS NULL AND pcv.m_productchange_id IS NULL)
                                UNION ALL 
                                         SELECT 'M_Inventory' AS movement_table, i.ad_client_id, i.ad_org_id, il.m_locator_id, 
                                                CASE
                                                    WHEN (il.qtycount - il.qtybook) >= 0::numeric THEN 'Y'::text
                                                    ELSE 'N'::text
                                                END AS receiptvalue, i.movementdate, dt.name AS doctypename, i.documentno, i.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(il.qtycount - il.qtybook) AS qty, NULL::unknown AS c_invoice_id, i.created, i.updated
                                           FROM m_inventory i
                                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                                 JOIN m_product p ON p.m_product_id = il.m_product_id
                            JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
                       LEFT JOIN m_transfer tr ON tr.m_inventory_id = i.m_inventory_id
                  LEFT JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
                  LEFT JOIN m_splitting spv ON spv.void_inventory_id = i.m_inventory_id
             LEFT JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id
             LEFT JOIN m_productchange pcv ON pcv.void_inventory_id = i.m_inventory_id
            WHERE NOT (EXISTS ( SELECT t.m_transaction_id
                     FROM m_transaction t
                    WHERE il.m_inventoryline_id = t.m_inventoryline_id)) AND tr.m_transfer_id IS NULL AND sp.m_splitting_id IS NULL AND pc.m_productchange_id IS NULL AND spv.m_splitting_id IS NULL AND pcv.m_productchange_id IS NULL)
                        UNION ALL 
                                 SELECT 'M_Transfer' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                                        CASE abs(t.movementqty)
                                            WHEN t.movementqty THEN 'Y'::text
                                            ELSE 'N'::text
                                        END AS receiptvalue, t.movementdate, tr.transfertype AS doctypename, tr.documentno, tr.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id, tr.created, tr.updated
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
                                END AS receiptvalue, t.movementdate, 'M_Splitting_ID' AS doctypename, sp.documentno, sp.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id, coalesce(sp.created,spv.created) as created, coalesce(sp.updated,spv.updated) as updated
                           FROM m_transaction t
                      JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
                 JOIN m_product p ON p.m_product_id = t.m_product_id
            JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
       LEFT JOIN m_splitting sp ON sp.m_inventory_id = i.m_inventory_id
       LEFT JOIN m_splitting spv ON spv.void_inventory_id = i.m_inventory_id
       WHERE sp.m_splitting_id IS NOT NULL OR spv.m_splitting_id IS NOT NULL)
        UNION ALL 
                 SELECT 'M_ProductChange' AS movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, 
                        CASE abs(t.movementqty)
                            WHEN t.movementqty THEN 'Y'::text
                            ELSE 'N'::text
                        END AS receiptvalue, t.movementdate, 'M_ProductChange_ID' AS doctypename, pc.documentno, pc.docstatus, p.m_product_id, p.value AS product_value, p.name AS product_name, abs(t.movementqty) AS qty, NULL::unknown AS c_invoice_id, coalesce(pc.created,pcv.created) as created, coalesce(pc.updated,pcv.updated) as updated
                   FROM m_transaction t
              JOIN m_inventoryline il ON il.m_inventoryline_id = t.m_inventoryline_id
         JOIN m_product p ON p.m_product_id = t.m_product_id
    JOIN m_inventory i ON i.m_inventory_id = il.m_inventory_id
   LEFT JOIN m_productchange pc ON pc.m_inventory_id = i.m_inventory_id
   LEFT JOIN m_productchange pcv ON pcv.void_inventory_id = i.m_inventory_id
   WHERE pc.m_productchange_id IS NOT NULL OR pcv.m_productchange_id IS NOT NULL) t
   JOIN m_locator l ON l.m_locator_id = t.m_locator_id
   JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = t.c_invoice_id;

ALTER TABLE v_product_movements_detailed OWNER TO libertya;

--20150401-0133 Función de actualización masiva de stock
CREATE OR REPLACE FUNCTION update_stock(clientID integer, orgID integer)
  RETURNS void AS
$BODY$
/***********
Actualiza el stock de los depósitos de la compañía y organización parametro, 
siempre y cuando existan los regitros en m_storage
*/
DECLARE
	r record;
BEGIN
	-- Pisar el stock de todos los artículos dentro de las ubicaciones de cada organización
	update m_storage as s
	set qtyonhand = coalesce((select sum(t.movementqty) as qty
				from m_transaction as t 
				where t.m_locator_id = s.m_locator_id and t.m_product_id = s.m_product_id),0)
	where ad_client_id = clientID
		and (orgID = 0 OR ad_org_id = orgID);
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION update_stock(integer, integer) OWNER TO libertya;

--20150401-1915 Fix para que no muestre las ND en los bloques de cuentas corrientes
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND "position"(dt.doctypekey::text, 'CDN'::text) < 1 
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND "position"(dt.doctypekey::text, 'CDN'::text) < 1 AND (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar]));

ALTER TABLE v_dailysales_current_account OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales AS 
(((((( SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = 'Y'::bpchar))
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND "position"(dt.doctypekey::text, 'CDN'::text) < 1)
UNION ALL 
 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
   JOIN c_payment p ON p.c_payment_id = al.c_payment_id
   JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id
  WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = 'Y'::bpchar)))
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar AND NOT (EXISTS ( SELECT c2.c_payment_id
   FROM c_cashline c2
  WHERE c2.c_payment_id = pjp.c_payment_id AND i.isvoidable = 'Y'::bpchar)))
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND "position"(dt.doctypekey::text, 'CDN'::text) < 1 AND (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar]));

ALTER TABLE v_dailysales OWNER TO libertya;

--20150408-1615 Incorporación de nueva funcionalidad de confección de folletos
CREATE TABLE m_brochure
(
  m_brochure_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  documentno character varying(30) NOT NULL,
  description character varying(255),
  datefrom date not null,
  dateto date not null,
  processing character(1),
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  docaction character(2) NOT NULL,
  docstatus character(2) NOT NULL,
  CONSTRAINT m_brochure_key PRIMARY KEY (m_brochure_id)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE m_brochure OWNER TO libertya;

CREATE TABLE m_brochureline
(
  m_brochureline_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  m_brochure_id integer NOT NULL,
  line numeric(18,0) NOT NULL,   
  m_product_id integer NOT NULL,
  description character varying(255), 
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT m_brochureline_key PRIMARY KEY (m_brochureline_id),
  CONSTRAINT m_brochureline_m_brochure_fk FOREIGN KEY (m_brochure_id)
      REFERENCES m_brochure (m_brochure_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT m_brochureline_m_product_fk FOREIGN KEY (m_product_id)
      REFERENCES m_product (m_product_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE m_brochureline OWNER TO libertya;

--20150414 1247 Nueva columna para acceso concurrente
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_Process','ConcurrentExecution','character(1) default ''N''::bpchar'));

--20150515-0030 Nueva columna para límite de cuit por compañía (registro Org *)
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_Role','controlcuitlimitclient','numeric(9,2) NOT NULL DEFAULT 0'));

-- 20150526-1240 Cambios a v_document_org a fin de minimizar los tiempos de respuesta
-- Importante: no se utiliza returns table dado que postgres 8.3 no soporta esa sintaxis.
-- 				Es por esto que se utiliza returns SETOF type.

-- Creacion del tipo
CREATE TYPE v_documents_org_type AS (documenttable text, document_id int, ad_client_id int, ad_org_id int, isactive char(1), created timestamp, createdby integer, updated timestamp, updatedby int, c_bpartner_id int, c_doctype_id integer, signo_issotrx int, doctypename varchar(60), doctypeprintname varchar(60), documentno varchar(60), issotrx bpchar, docstatus character(2), datetrx timestamp, dateacct timestamp, c_currency_id int, c_conversiontype_id int, amount numeric, c_invoicepayschedule_id integer, duedate timestamp, truedatetrx timestamp, initialcurrentaccount numeric, socreditstatus char(1));

-- creacion de la funview
create or replace function v_documents_org_filtered(bpartner int, summaryonly boolean)
returns SETOF v_documents_org_type
as
$body$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    initialcam varchar;
    adocument v_documents_org_type;
   
BEGIN
    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, i.initialcurrentaccountamt, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, COALESCE(i.initialcurrentaccountamt, 0.00), bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE il.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(i.initialcurrentaccountamt, 0.00), COALESCE(bp.socreditstatus, bp2.socreditstatus) ';

        leftjoin1 = ' LEFT JOIN ( SELECT di.c_payment_id, sum(di.initialcurrentaccountamt) AS initialcurrentaccountamt
                 FROM ( SELECT DISTINCT al.c_payment_id, al.c_invoice_id, i.initialcurrentaccountamt
                         FROM c_allocationline al
                JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id and (' || $1 || ' = -1 or i.c_bpartner_id = ' || $1 || ')
                   ORDER BY al.c_payment_id, al.c_invoice_id, i.initialcurrentaccountamt) di
                GROUP BY di.c_payment_id) i ON i.c_payment_id = p.c_payment_id ';

        leftjoin2 = '  LEFT JOIN ( SELECT di.c_cashline_id, sum(di.initialcurrentaccountamt) AS initialcurrentaccountamt
                FROM ( SELECT DISTINCT lc.c_cashline_id, lc.c_invoice_id, i.initialcurrentaccountamt
                    FROM c_allocationline lc
                   JOIN c_invoice i ON i.c_invoice_id = lc.c_invoice_id
                 WHERE (' || $1 || ' = -1 or i.c_bpartner_id = ' || $1 || ')
                  ORDER BY lc.c_cashline_id, lc.c_invoice_id, i.initialcurrentaccountamt) di
               GROUP BY di.c_cashline_id) i ON i.c_cashline_id = cl.c_cashline_id ';

        initialcam = ' i.initialcurrentaccountamt ';
   
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';
        leftjoin1 = '';
        leftjoin2 = '';
        initialcam = '0';

    end if;

    consulta = '

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, ' || initialcam || ', bp.socreditstatus
                   FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id

' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, COALESCE(' || initialcam || ', 0.00) AS initialcurrentaccountamt, bp.socreditstatus
                   FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')


' || leftjoin1 || '

   LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
  WHERE
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
    ELSE 1 = 1
END


' || orderby2 || '


))

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE il.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(' || initialcam ||', 0.00) AS initialcurrentaccountamt, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus
           FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id


' || leftjoin2 || '


   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)
    AND (CASE WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id
        THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
        ELSE 1 = 1 END)


' || orderby3 || '

); ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$body$
language plpgsql;

-- Se regenera v_documents_org simplemente apuntando a v_documents_org_filtered con los argumentos necesarios para que muestre todo el detalle 
drop view v_documents_org;
create or replace view v_documents_org as select * from v_documents_org_filtered(-1, false);

--20150602-2300 Nuevas funciones que optimizan las vistas del informe de resumen de ventas
--Tipo para la vista c_posjournalpayments_v
CREATE TYPE c_posjournalpayments_v_type AS (c_allocationhdr_id integer, c_allocationline_id integer, ad_client_id integer, ad_org_id integer, 
						isactive character(1), created timestamp, createdby integer, updated timestamp, 
						updatedby integer, c_invoice_id integer, c_payment_id integer, c_cashline_id integer, 
						c_invoice_credit_id integer, tendertype character varying(2), documentno character varying(30), 
						description character varying(255), info character varying(255), amount numeric(20,2), 
						c_cash_id integer, line numeric(18,0), c_doctype_id integer, checkno character varying(20), 
						a_bank character varying(255), transferno character varying(20), creditcardtype character(1), 
						m_entidadfinancieraplan_id integer, m_entidadfinanciera_id integer, couponnumber character varying(30), 
						allocationdate timestamp, docstatus character(2), dateacct date, invoice_documentno  character varying, 
						invoice_grandtotal numeric, entidadfinanciera_value character varying, 
						entidadfinanciera_name character varying, bp_entidadfinanciera_value character varying, 
						bp_entidadfinanciera_name character varying, cupon character varying, 
						creditcard character varying, isfiscaldocument character(1), isfiscal character(1), 
						fiscalalreadyprinted character(1), allocationdateacct timestamp);

--Función para la vista c_posjournalpayments_v						
CREATE OR REPLACE FUNCTION c_posjournalpayments_v_filtered(orgID integer, dateFrom date, dateTo date)
  RETURNS SETOF c_posjournalpayments_v_type AS
$BODY$
declare
	consulta varchar;
	whereAllocationDate varchar;
	whereCashLineDate varchar;
	wherePaymentDate varchar;
	whereAllocationOrg varchar;
	whereCashLineOrg varchar;
	wherePaymentOrg varchar;
	whereAllocation varchar;
	whereCashLine varchar;
	wherePayment varchar;
	adocument c_posjournalpayments_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	
	-- Filtro de organización
	whereAllocationOrg = '(' || orgID || ' = -1 OR ah.ad_org_id = ' || orgID || ')';
	whereCashLineOrg = '(' || orgID || ' = -1 OR cl.ad_org_id = ' || orgID || ')';
	wherePaymentOrg = '(' || orgID || ' = -1 OR p.ad_org_id = ' || orgID || ')';
	
	-- Filtro de fechas
	whereAllocationDate = '';
	whereCashLineDate = '';	
	wherePaymentDate = '';
	
	if dateFrom is not null then 
		whereAllocationDate = ' AND date_trunc(''day'', ah.datetrx) >= date_trunc(''day'', ''' || dateFrom || '''::date) ';
		whereCashLineDate = ' AND date_trunc(''day'', c.dateacct) >= date_trunc(''day'', ''' || dateFrom || '''::date) ';
		wherePaymentDate = ' AND date_trunc(''day'', p.dateacct) >= date_trunc(''day'', ''' || dateFrom || '''::date) ';
	end if;

	if dateTo is not null then 
		whereAllocationDate = whereAllocationDate || ' AND date_trunc(''day'', ah.datetrx) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
		whereCashLineDate = whereCashLineDate || ' AND date_trunc(''day'', c.dateacct) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
		wherePaymentDate = wherePaymentDate || ' AND date_trunc(''day'', p.dateacct) <= date_trunc(''day'', ''' || dateTo || '''::date) ';
	end if;

	-- Condición std para cada union
	whereAllocation = whereAllocationOrg || whereAllocationDate;
	whereCashLine = whereCashLineOrg || whereCashLineDate;
	wherePayment = wherePaymentOrg || wherePaymentDate;
	
	-- Consulta
	consulta = 
	'(        ( SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
                            WHEN al.c_cashline_id IS NOT NULL THEN ''CA''::character varying
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ''CR''::character varying
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
                            WHEN al.c_payment_id IS NOT NULL THEN ((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text))::character varying
                            WHEN al.c_cashline_id IS NOT NULL THEN ((c.name::text || ''_''::text) || cl.line::text)::character varying
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
                            ELSE NULL::character varying
                        END::character varying(255) AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, ic.c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, 
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
                        END AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, dt.isfiscaldocument, dt.isfiscal, ic.fiscalalreadyprinted, date_trunc(''day''::text, ah.datetrx) AS allocationdateacct
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
   WHERE ' || whereAllocation || ')
        UNION ALL 
                 SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::unknown AS c_invoice_id, NULL::unknown AS c_payment_id, cl.c_cashline_id, NULL::unknown AS c_invoice_credit_id, ''CA''::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || ''_#''::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::unknown AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc(''day''::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted, date_trunc(''day''::text, c.dateacct) AS allocationdateacct
                   FROM c_cashline cl
              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
         LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
        WHERE al.c_allocationline_id IS NULL AND ' || whereCashLine || ')
UNION ALL 
        ( SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::unknown AS c_invoice_id, p.c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text)))::character varying(255) AS info, p.payamt AS amount, NULL::unknown AS c_cash_id, NULL::numeric(18,0) AS line, NULL::unknown AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted, date_trunc(''day''::text, p.dateacct) AS allocationdateacct
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
  WHERE al.c_allocationline_id IS NULL AND ' || wherePayment || ');';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournalpayments_v_filtered(integer, date, date) OWNER TO libertya;

--Tipo para las vistas del reporte resumen de ventas
CREATE TYPE v_dailysales_type AS (trxtype character varying, ad_client_id integer, ad_org_id integer, 
                                c_invoice_id integer, datetrx timestamp without time zone, c_payment_id integer, c_cashline_id integer, 
                                c_invoice_credit_id integer, tendertype character varying, documentno character varying(30), 
                                description character varying(255), info character varying, amount numeric, c_bpartner_id integer, 
                                name character varying(60), c_bp_group_id integer, groupname character varying(60), 
                                c_categoria_iva_id integer, categorianame character varying(40), c_pospaymentmedium_id integer, 
                                pospaymentmediumname character varying, m_entidadfinanciera_id integer, entidadfinancieraname character varying, 
                                entidadfinancieravalue character varying, m_entidadfinancieraplan_id integer, planname character varying, 
                                docstatus character(2), issotrx character(1), dateacct date, invoicedateacct date, c_posjournal_id integer, 
                                ad_user_id integer, c_pos_id integer, isfiscal character(1), fiscalalreadyprinted character(1));

--Función para la vista v_dailysales
CREATE OR REPLACE FUNCTION v_dailysales_filtered(orgID integer, posID integer, userID integer, dateFrom date, dateTo date, invoiceDateFrom date, invoiceDateTo date, addInvoiceDate boolean)
  RETURNS SETOF v_dailysales_type AS
$BODY$
declare
	consulta varchar;
	dateFromPOSJournalPayments varchar;
	dateToPOSJournalPayments varchar;
	dateFromInvoicePOSJournalPayments varchar;
	dateToInvoicePOSJournalPayments varchar;
	orgIDPOSJournalPayments integer;
	posIDPOSJournalPayments integer;
	userIDPOSJournalPayments integer;
	adocument v_dailysales_type;
BEGIN
	-- Armado del llamado a la cada función que ejecuta partes de la vista v_dailysales
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	dateFromInvoicePOSJournalPayments = (CASE WHEN invoiceDateFrom is null THEN 'null::date' ELSE '''' || invoiceDateFrom || '''::date' END);
	dateToInvoicePOSJournalPayments = (CASE WHEN invoiceDateTo is null THEN 'null::date' ELSE '''' || invoiceDateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posIDPOSJournalPayments = (CASE WHEN posID is null THEN -1 ELSE posID END);
	userIDPOSJournalPayments = (CASE WHEN userID is null THEN -1 ELSE userID END);

	-- Armar la consulta
	consulta = 'select distinct * 
	from (select *
		from v_dailysales_v2_filtered(' || orgIDPOSJournalPayments || ', ' || posIDPOSJournalPayments || ', ' || userIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ', ' || dateFromInvoicePOSJournalPayments || ', ' || dateToInvoicePOSJournalPayments || ', ' || addInvoiceDate || ')
		union all
		select *
		from v_dailysales_current_account_payments_filtered(' || orgIDPOSJournalPayments || ', ' || posIDPOSJournalPayments || ', ' || userIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')
		union all
		select *
		from v_dailysales_current_account_filtered(' || orgIDPOSJournalPayments || ', ' || posIDPOSJournalPayments || ', ' || userIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ', ' || dateFromInvoicePOSJournalPayments || ', ' || dateToInvoicePOSJournalPayments || ', ' || addInvoiceDate || ')
		union all
		select *
		from v_dailysales_invoices_filtered(' || orgIDPOSJournalPayments || ', ' || posIDPOSJournalPayments || ', ' || userIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ', ' || dateFromInvoicePOSJournalPayments || ', ' || dateToInvoicePOSJournalPayments || ', ' || addInvoiceDate || ')) as t;';

raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_filtered(integer, integer, integer, date, date, date, date, boolean) OWNER TO libertya;

--Función para la vista v_dailysales_v2
CREATE OR REPLACE FUNCTION v_dailysales_v2_filtered(orgID integer, posID integer, userID integer, dateFrom date, dateTo date, invoiceDateFrom date, invoiceDateTo date, addInvoiceDate boolean)
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
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
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
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
                                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
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
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_v2_filtered(integer, integer, integer, date, date, date, date, boolean) OWNER TO libertya;

--Función para la vista v_dailysales_invoices
CREATE OR REPLACE FUNCTION v_dailysales_invoices_filtered(orgID integer, posID integer, userID integer, dateFrom date, dateTo date, invoiceDateFrom date, invoiceDateTo date, addInvoiceDate boolean)
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
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate || whereDateInvoices || wherePOSInvoices || whereUserInvoices;

	-- Armar la consulta
	consulta = 'SELECT ''I''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, dtc.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dtc.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dtc.c_doctype_id AS c_pospaymentmedium_id, dtc.name AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE ' || whereClauseStd ||
  ' AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
   JOIN c_payment p ON p.c_payment_id = al.c_payment_id
   JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id
  WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = ''Y''::bpchar));';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_invoices_filtered(integer, integer, integer, date, date, date, date, boolean) OWNER TO libertya;

--Función para la vista v_dailysales_current_account
CREATE OR REPLACE FUNCTION v_dailysales_current_account_filtered(orgID integer, posID integer, userID integer, dateFrom date, dateTo date, invoiceDateFrom date, invoiceDateTo date, addInvoiceDate boolean)
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
			 ' AND dt.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
			 ' ) ';

	-- Agregar las condiciones anteriores
	whereClauseStd = whereClauseStd || whereInvoiceDate;

	-- Armado del llamado a la función que ejecuta la vista filtrada c_posjournalpayments_v
	dateFromPOSJournalPayments = (CASE WHEN dateFrom is null THEN 'null::date' ELSE '''' || dateFrom || '''::date' END);
	dateToPOSJournalPayments = (CASE WHEN dateTo is null THEN 'null::date' ELSE '''' || dateTo || '''::date' END);
	orgIDPOSJournalPayments = (CASE WHEN orgID is null THEN -1 ELSE orgID END);
	posJournalPaymentsFrom = 'c_posjournalpayments_v_filtered(' || orgIDPOSJournalPayments || ', ' || dateFromPOSJournalPayments || ', ' || dateToPOSJournalPayments || ')';

	-- Armar la consulta
	consulta = 'SELECT ''CAI''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, pjp.amount
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
         SELECT ''CAIA''::character varying AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc(''day''::text, i.dateinvoiced) AS datetrx, NULL::integer AS c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CC'' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::integer AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::integer AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS entidadfinancieravalue, NULL::integer AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
         FROM ( SELECT 
                      CASE
                          WHEN (dt.docbasetype = ANY (ARRAY[''ARC''::bpchar, ''APC''::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                          ELSE i.c_invoice_id
                      END AS c_invoice_id, pjp.amount
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
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_filtered(integer, integer, integer, date, date, date, date, boolean) OWNER TO libertya;

--Función para la vista v_dailysales_current_account_payments
CREATE OR REPLACE FUNCTION v_dailysales_current_account_payments_filtered(orgID integer, posID integer, userID integer, dateFrom date, dateTo date)
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
			 ' AND dtc.doctypekey not in (''RTR'', ''RTI'', ''RCR'', ''RCI'') ' || 
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
        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_current_account_payments_filtered(integer, integer, integer, date, date) OWNER TO libertya;

-- Las vistas del resumen de ventas se regeneran en base a las funciones optimizadas
--Eliminación de vistas
DROP VIEW v_dailysales;
DROP VIEW v_dailysales_v2;
DROP VIEW v_dailysales_invoices;
DROP VIEW v_dailysales_current_account;
DROP VIEW v_dailysales_current_account_payments;
DROP VIEW c_posjournalpayments_v;

--Nueva vista c_posjournalpayments_v
CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
SELECT *
FROM c_posjournalpayments_v_filtered(-1, null::date, null::date);
ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

--Nueva vista v_dailysales_invoices
CREATE OR REPLACE VIEW v_dailysales_invoices AS 
SELECT *
FROM v_dailysales_invoices_filtered(-1,-1,-1,null::date,null::date,null::date,null::date,false);
ALTER TABLE v_dailysales_invoices OWNER TO libertya;

--Nueva vista v_dailysales
CREATE OR REPLACE VIEW v_dailysales AS 
SELECT *
FROM v_dailysales_filtered(-1,-1,-1,null::date,null::date,null::date,null::date,false);
ALTER TABLE v_dailysales OWNER TO libertya;

--Nueva vista v_dailysales_v2
CREATE OR REPLACE VIEW v_dailysales_v2 AS 
SELECT *
FROM v_dailysales_v2_filtered(-1,-1,-1,null::date,null::date,null::date,null::date,false);
ALTER TABLE v_dailysales_v2 OWNER TO libertya;

--Nueva vista v_dailysales_current_account
CREATE OR REPLACE VIEW v_dailysales_current_account AS 
SELECT *
FROM v_dailysales_current_account_filtered(-1,-1,-1,null::date,null::date,null::date,null::date,false);
ALTER TABLE v_dailysales_current_account OWNER TO libertya;

--Nueva vista v_dailysales_current_account_payments
CREATE OR REPLACE VIEW v_dailysales_current_account_payments AS 
SELECT *
FROM v_dailysales_current_account_payments_filtered(-1,-1,-1,null::date,null::date);
ALTER TABLE v_dailysales_current_account_payments OWNER TO libertya;

--20150625-1212 Mejoras de tiempo del informe de Declaración de Valores utilizando funviews
--Tipo para las funviews c_posjournalinvoices_v
CREATE TYPE c_posjournalinvoices_v_type AS (c_posjournal_id integer, c_allocationhdr_id integer, allocationtype character varying(50), 
						c_invoice_id integer, ad_client_id integer, ad_org_id integer, isactive character(1), 
						created timestamp without time zone, createdby integer, updated timestamp without time zone, 
						updatedby integer, documentno character varying(30), c_doctype_id integer, 
						dateinvoiced timestamp without time zone, dateacct timestamp without time zone, 
						c_bpartner_id integer, description character varying(255), docstatus character(2), 
						processed character(1), c_currency_id integer, grandtotal numeric(20,2), paidamt numeric(20,2), 
						name character varying(60), docbasetype character(3), signo_issotrx integer, 
						isfiscaldocument character(1), isfiscal character(1), fiscalalreadyprinted character(1), 
						allocation_active character(1), allocation_created timestamp without time zone, 
						allocation_updated timestamp without time zone, c_invoice_orig_id integer);

--Tipo para la funview c_posjournal_c_payment_v
CREATE TYPE c_posjournal_c_payment_v_type AS (c_allocationhdr_id integer, c_allocationline_id integer, ad_client_id integer, ad_org_id integer, 
					isactive character(1), created timestamp without time zone, createdby integer, updated timestamp without time zone, 
					updatedby integer, c_invoice_id integer, c_payment_id integer, c_cashline_id integer, 
					c_invoice_credit_id integer, tendertype character varying, documentno character varying, 
					description character varying(255), info character varying, amount numeric(20,2), c_cash_id integer, 
					line numeric(18,0), c_doctype_id integer, checkno character varying, a_bank character varying, 
					transferno character varying, creditcardtype character(1), m_entidadfinancieraplan_id integer, 
					m_entidadfinanciera_id integer,  couponnumber character varying, allocationdate timestamp without time zone, 
					docstatus character(2), dateacct date, invoice_documentno character varying(30), invoice_grandtotal numeric(20,2), 
					entidadfinanciera_value character varying, entidadfinanciera_name character varying, 
					bp_entidadfinanciera_value character varying, bp_entidadfinanciera_name character varying, 
					cupon character varying, creditcard character varying, isfiscaldocument character(1), 
					isfiscal character(1), fiscalalreadyprinted character(1), changeamt numeric);

--Tipo para las funviews c_posjournal_*_v
CREATE TYPE c_posjournal_v_type AS (c_allocationhdr_id integer, c_allocationline_id integer, ad_client_id integer, ad_org_id integer, 
					isactive character(1), created timestamp without time zone, createdby integer, updated timestamp without time zone, 
					updatedby integer, c_invoice_id integer, c_payment_id integer, c_cashline_id integer, 
					c_invoice_credit_id integer, tendertype character varying, documentno character varying, 
					description character varying(255), info character varying, amount numeric(20,2), c_cash_id integer, 
					line numeric(18,0), c_doctype_id integer, checkno character varying, a_bank character varying, 
					transferno character varying, creditcardtype character(1), m_entidadfinancieraplan_id integer, 
					m_entidadfinanciera_id integer,  couponnumber character varying, allocationdate timestamp without time zone, 
					docstatus character(2), dateacct date, invoice_documentno character varying(30), invoice_grandtotal numeric(20,2), 
					entidadfinanciera_value character varying, entidadfinanciera_name character varying, 
					bp_entidadfinanciera_value character varying, bp_entidadfinanciera_name character varying, 
					cupon character varying, creditcard character varying, isfiscaldocument character(1), 
					isfiscal character(1), fiscalalreadyprinted character(1));

--Tipo para las funviews c_pos_declaracionvalores_*
CREATE TYPE c_pos_declaracionvalores_type AS (ad_client_id integer, ad_org_id integer, c_posjournal_id integer, ad_user_id integer, 
						c_currency_id integer, datetrx date, docstatus character(2), category varchar, 
						tendertype character(3), description text, c_charge_id integer, chargename character varying(60), 
						doc_id integer, ingreso numeric(22,2), egreso numeric(22,2), c_invoice_id integer, 
						invoice_documentno character varying(30), invoice_grandtotal numeric(22,2), 
						entidadfinanciera_value varchar, entidadfinanciera_name varchar, 
						bp_entidadfinanciera_value varchar, bp_entidadfinanciera_name varchar, cupon varchar, 
						creditcard varchar, generated_invoice_documentno varchar, allocation_active character(1), 
						c_pos_id integer, posname character varying(60));

--Funview c_posjournalinvoices_v_filtered(anyarray, boolean)
CREATE OR REPLACE FUNCTION c_posjournalinvoices_v_filtered(posjournalIDs anyarray, addWhereStd boolean)
  RETURNS SETOF c_posjournalinvoices_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	whereClauseStd varchar;
	adocument c_posjournalinvoices_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND i.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = '';
	if addWhereStd then
		whereClauseStd = 'AND i.docstatus in (''CO'',''CL'')';
	end if;
	whereClauseStd = whereClauseStd || wherePosJournalIDs;
	
	-- Consulta
	consulta = 
	'SELECT i.c_posjournal_id, ah.c_allocationhdr_id, ah.allocationtype, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.documentno, i.c_doctype_id, i.dateinvoiced, i.dateacct, i.c_bpartner_id, i.description, i.docstatus, i.processed, i.c_currency_id, i.grandtotal, sum(COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric(20,2)))::numeric(20,2) AS paidamt, dt.name, dt.docbasetype, dt.signo_issotrx, dt.isfiscaldocument, dt.isfiscal, i.fiscalalreadyprinted, ah.isactive AS allocation_active, ah.created AS allocation_created, ah.updated AS allocation_updated, i.c_invoice_orig_id
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
   LEFT JOIN c_allocationline al ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
  WHERE i.issotrx = ''Y''::bpchar ' || whereClauseStd ||
  ' AND (ah.c_allocationhdr_id IS NULL OR (ah.allocationtype::text = ANY (ARRAY[''STX''::character varying::text, ''MAN''::character varying::text, ''RC''::character varying::text])))
  GROUP BY i.documentno, i.c_posjournal_id, ah.c_allocationhdr_id, ah.allocationtype, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_doctype_id, i.dateinvoiced, i.dateacct, i.c_bpartner_id, i.description, i.docstatus, i.processed, i.c_currency_id, i.grandtotal, dt.name, dt.docbasetype, dt.signo_issotrx, dt.isfiscaldocument, dt.isfiscal, i.fiscalalreadyprinted, ah.isactive, ah.created, ah.updated, i.c_invoice_orig_id
  ORDER BY i.documentno;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournalinvoices_v_filtered(anyarray, boolean) OWNER TO libertya;

--Funview c_posjournalinvoices_v_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_posjournalinvoices_v_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_posjournalinvoices_v_type AS
$BODY$
declare
	consulta varchar;
	parameterFunctionCall varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournalinvoices_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Parametros de llamada a funview
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;

	parameterFunctionCall = parameterFunctionCall || ',false';
	
	-- Consulta
	consulta = 
	'select * from c_posjournalinvoices_v_filtered(' || parameterFunctionCall || ')';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournalinvoices_v_filtered(anyarray) OWNER TO libertya;

--Funview c_posjournalinvoices_v_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_posjournal_c_payment_v_filtered(posjournalIDs anyarray)
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
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, p.tendertype::character varying AS tendertype, p.documentno, p.description, ((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text))::character varying AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, al.changeamt
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   WHERE 1=1 ' || wherePosJournalIDs || 
   ' UNION ALL 
        ( SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::integer AS c_invoice_id, p.c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text)))::character varying(255) AS info, p.payamt AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, 0 AS changeamt
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
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_payment_v_filtered(anyarray) OWNER TO libertya;

--Funview c_posjournal_c_cash_v_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_posjournal_c_cash_v_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_posjournal_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND c.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, ''CA''::character varying AS tendertype, NULL::character varying AS documentno, cl.description, ((c.name::text || ''_''::text) || cl.line::text)::character varying AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, NULL::integer AS c_doctype_id, NULL::character varying AS checkno, NULL::character varying AS a_bank, NULL::character varying AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying AS couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   WHERE 1=1 ' || wherePosJournalIDs || 
   ' UNION ALL 
         SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::integer AS c_invoice_id, NULL::integer AS c_payment_id, cl.c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CA''::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || ''_''::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::integer AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc(''day''::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted
           FROM c_cashline cl
      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
     WHERE 1=1 '  || wherePosJournalIDs || 
     ' AND NOT (EXISTS ( SELECT al.c_allocationline_id
              FROM c_allocationline al
             WHERE al.c_cashline_id = cl.c_cashline_id));';

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_cash_v_filtered(anyarray) OWNER TO libertya;

--Funview c_posjournal_c_invoice_credit_v_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_posjournal_c_invoice_credit_v_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_posjournal_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND ah.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, ''CR''::character varying AS tendertype, ic.documentno, ic.description, ic.documentno AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::integer AS line, ic.c_doctype_id, NULL::character varying AS checkno, NULL::character varying AS a_bank, NULL::character varying AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying AS couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, ic.docstatus, ic.dateacct::date AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, dt.isfiscaldocument, dt.isfiscal, ic.fiscalalreadyprinted
   FROM c_allocationline al
   JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
   JOIN c_doctype dt ON dt.c_doctype_id = ic.c_doctypetarget_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   WHERE 1=1 ' || wherePosJournalIDs;

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_invoice_credit_v_filtered(anyarray) OWNER TO libertya;

--Funview c_pos_declaracionvalores_ventas_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_ventas_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalInvoicesIDs varchar;
	wherePosJournalAllocationsIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalInvoicesFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalInvoicesIDs = '';
	wherePosJournalAllocationsIDs = '';
	if existsPosJournalIDs then
		wherePosJournalInvoicesIDs = ' AND i.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
		wherePosJournalAllocationsIDs = ' AND ah.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'i.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalInvoicesFunctionCall = 'c_posjournalinvoices_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || '' ''::text) || COALESCE(i.description, ''''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
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
           FROM ' || posJournalInvoicesFunctionCall || ' i
      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
   LEFT JOIN ( SELECT al.c_invoice_id, ah.c_posjournal_id, sum(al.amount) AS amount
    FROM c_allocationhdr ah
   JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
  WHERE ' || whereClauseStd || wherePosJournalAllocationsIDs ||
  ' AND ah.isactive = ''Y''::bpchar AND i.isvoidable = ''N''::bpchar
  GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
   WHERE '  || whereClauseStd || wherePosJournalInvoicesIDs ||
  ' ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_ventas_filtered(anyarray) OWNER TO libertya;

--Funview c_pos_declaracionvalores_cash_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_cash_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalCashIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalCashFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalCashIDs = '';
	if existsPosJournalIDs then
		wherePosJournalCashIDs = ' AND pj.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'cl.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalCashFunctionCall = 'c_posjournal_c_cash_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
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
   JOIN ' || posJournalCashFunctionCall || ' pjp ON pjp.c_cashline_id = cl.c_cashline_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
   WHERE ' || whereClauseStd || wherePosJournalCashIDs ||
   ' GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) c;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_cash_filtered(anyarray) OWNER TO libertya;

--Funview c_pos_declaracionvalores_payments_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_payments_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalPaymentsIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalPaymentsFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalPaymentsIDs = '';
	if existsPosJournalIDs then
		wherePosJournalPaymentsIDs = ' AND pj.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'p.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalPaymentsFunctionCall = 'c_posjournal_c_payment_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::text AS category, p.tendertype, (p.documentno::text || '' ''::text) || COALESCE(p.description, ''''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
        CASE p.isreceipt
            WHEN ''Y''::bpchar THEN p.total
            ELSE 0::numeric
        END::numeric(22,2) AS ingreso, 
        CASE p.isreceipt
            WHEN ''N''::bpchar THEN abs(p.total)
            ELSE 0::numeric
        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname, p.m_entidadfinanciera_id
   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount + 
                CASE
                    WHEN p.tendertype = ''C''::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(22,2) AS total, pjp.invoice_documentno, (pjp.invoice_grandtotal + 
                CASE
                    WHEN p.tendertype = ''C''::bpchar THEN pjp.changeamt
                    ELSE 0::numeric
                END)::numeric(20,2) AS invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname, pjp.m_entidadfinanciera_id
           FROM c_payment p
      JOIN ' || posJournalPaymentsFunctionCall || ' pjp ON pjp.c_payment_id = p.c_payment_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
   LEFT JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
  WHERE ' || whereClauseStd || wherePosJournalPaymentsIDs ||
  ' AND (i.c_invoice_id IS NULL OR NOT (EXISTS ( SELECT cl.c_cashline_id
   FROM c_cashline cl
  WHERE cl.c_payment_id = p.c_payment_id AND i.isvoidable = ''Y''::bpchar)))
  GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.changeamt, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name, pjp.m_entidadfinanciera_id) p;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_payments_filtered(anyarray) OWNER TO libertya;

--Funview c_pos_declaracionvalores_credit_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_credit_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalCreditIDs varchar;
	whereClauseStd varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalCreditFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalCreditIDs = '';
	if existsPosJournalIDs then
		wherePosJournalCreditIDs = ' AND pj.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	-- Condición std
	whereClauseStd = 'i.docstatus in (''CO'',''CL'')';

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalCreditFunctionCall = 'c_posjournal_c_invoice_credit_v_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	'SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || '' ''::text) || COALESCE(i.description, ''''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id AS c_invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id AS invoice_id, pos.c_pos_id, pos.name AS posname
           FROM c_invoice i
      JOIN ' || posJournalCreditFunctionCall || ' pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
   JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
   JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
   WHERE ' || whereClauseStd || wherePosJournalCreditIDs ||
  ' GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) i
   JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_credit_filtered(anyarray) OWNER TO libertya;

--Funview c_pos_declaracionvalores_voided_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_voided_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalAllocationsIDs varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posJournalInvoicesFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Filtro de cajas diarias
	wherePosJournalAllocationsIDs = '';
	if existsPosJournalIDs then
		wherePosJournalAllocationsIDs = ' AND ji.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;

	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posJournalInvoicesFunctionCall = 'c_posjournalinvoices_v_filtered(' || parameterFunctionCall || ', false)';
	
	-- Consulta
	consulta = 
	'SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || '' ''::text) || COALESCE(ji.description, ''''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal + COALESCE(( SELECT sum(al2.changeamt) AS sum
           FROM c_allocationline al2
      JOIN c_payment p2 ON p2.c_payment_id = al2.c_payment_id
   JOIN c_cashline cl ON cl.c_payment_id = p2.c_payment_id
  WHERE al2.c_invoice_id = ji.c_invoice_id AND p2.tendertype = ''C''::bpchar AND al2.isactive = ''N''::bpchar), 0::numeric) AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name AS posname
   FROM ' || posJournalInvoicesFunctionCall || ' ji
   JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY[''VO''::bpchar, ''RE''::bpchar])) ' || wherePosJournalAllocationsIDs || 
  ' AND (ji.isfiscal IS NULL OR ji.isfiscal = ''N''::bpchar OR ji.isfiscal = ''Y''::bpchar AND ji.fiscalalreadyprinted = ''Y''::bpchar) AND i.isvoidable = ''N''::bpchar;';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_voided_filtered(anyarray) OWNER TO libertya;

--Funview c_pos_declaracionvalores_v_filtered(anyarray)
CREATE OR REPLACE FUNCTION c_pos_declaracionvalores_v_filtered(posjournalIDs anyarray)
  RETURNS SETOF c_pos_declaracionvalores_type AS
$BODY$
declare
	consulta varchar;
	selectQuery varchar;
	existsPosJournalIDs boolean;
	parameterFunctionCall varchar;
	posDeclaracionValoresCreditFunctionCall varchar;
	posDeclaracionValoresPaymentFunctionCall varchar;
	posDeclaracionValoresCashFunctionCall varchar;
	posDeclaracionValoresVentasFunctionCall varchar;
	posDeclaracionValoresVoidedFunctionCall varchar;
	adocument c_pos_declaracionvalores_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;
	
	-- Armado del parámetro a la función c_posjournalinvoices_v_filtered
	parameterFunctionCall = 'array[-1]';
	if existsPosJournalIDs then
		parameterFunctionCall = 'ARRAY[' || array_to_string(posjournalIDs, ',') || ']';
	end if;

	--Armado de select
	selectQuery = ' select * from ';
	
	-- Armado de llamado a función c_posjournalinvoices_v_filtered
	posDeclaracionValoresCreditFunctionCall = 'c_pos_declaracionvalores_credit_filtered(' || parameterFunctionCall || ')';
	posDeclaracionValoresPaymentFunctionCall = 'c_pos_declaracionvalores_payments_filtered(' || parameterFunctionCall || ')';
	posDeclaracionValoresCashFunctionCall = 'c_pos_declaracionvalores_cash_filtered(' || parameterFunctionCall || ')';
	posDeclaracionValoresVentasFunctionCall = 'c_pos_declaracionvalores_ventas_filtered(' || parameterFunctionCall || ')';
	posDeclaracionValoresVoidedFunctionCall = 'c_pos_declaracionvalores_voided_filtered(' || parameterFunctionCall || ')';
	
	-- Consulta
	consulta = 
	     selectQuery || posDeclaracionValoresVentasFunctionCall ||
	' UNION ALL 
	' || selectQuery || posDeclaracionValoresPaymentFunctionCall ||
	' UNION ALL 
	' || selectQuery || posDeclaracionValoresCashFunctionCall ||
	' UNION ALL 
	' || selectQuery || posDeclaracionValoresCreditFunctionCall ||
	' UNION ALL 
        ' || selectQuery || posDeclaracionValoresVoidedFunctionCall;

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_pos_declaracionvalores_v_filtered(anyarray) OWNER TO libertya;

--Eliminación de vistas
DROP VIEW c_pos_declaracionvalores_v;
DROP VIEW c_pos_declaracionvalores_voided;
DROP VIEW c_pos_declaracionvalores_credit;
DROP VIEW c_pos_declaracionvalores_payments;
DROP VIEW c_pos_declaracionvalores_cash;
DROP VIEW c_pos_declaracionvalores_ventas;
DROP VIEW c_posjournal_c_invoice_credit_v;
DROP VIEW c_posjournal_c_cash_v;
DROP VIEW c_posjournal_c_payment_v;
DROP VIEW c_posjournalinvoices_v;

--Recreación de vistas utilizando las funviews
--View c_pos_declaracionvalores_v 
CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
 select * from c_pos_declaracionvalores_v_filtered(array[-1]);
ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--View c_pos_declaracionvalores_voided 
CREATE OR REPLACE VIEW c_pos_declaracionvalores_voided AS 
 select * from c_pos_declaracionvalores_voided_filtered(array[-1]);
ALTER TABLE c_pos_declaracionvalores_voided OWNER TO libertya;

--View c_pos_declaracionvalores_credit
CREATE OR REPLACE VIEW c_pos_declaracionvalores_credit AS 
 select * from c_pos_declaracionvalores_credit_filtered(array[-1]);
ALTER TABLE c_pos_declaracionvalores_credit OWNER TO libertya;

--View c_pos_declaracionvalores_payments 
CREATE OR REPLACE VIEW c_pos_declaracionvalores_payments AS 
 select * from c_pos_declaracionvalores_payments_filtered(array[-1]);
ALTER TABLE c_pos_declaracionvalores_payments OWNER TO libertya;

--View c_pos_declaracionvalores_cash 
CREATE OR REPLACE VIEW c_pos_declaracionvalores_cash AS 
 select * from c_pos_declaracionvalores_cash_filtered(array[-1]);
ALTER TABLE c_pos_declaracionvalores_cash OWNER TO libertya;

--View c_pos_declaracionvalores_ventas 
CREATE OR REPLACE VIEW c_pos_declaracionvalores_ventas AS 
 select * from c_pos_declaracionvalores_ventas_filtered(array[-1]);
ALTER TABLE c_pos_declaracionvalores_ventas OWNER TO libertya;

--View c_posjournal_c_invoice_credit_v 
CREATE OR REPLACE VIEW c_posjournal_c_invoice_credit_v AS 
 select * from c_posjournal_c_invoice_credit_v_filtered(array[-1]);
ALTER TABLE c_posjournal_c_invoice_credit_v OWNER TO libertya;

--View c_posjournal_c_cash_v 
CREATE OR REPLACE VIEW c_posjournal_c_cash_v AS 
 select * from c_posjournal_c_cash_v_filtered(array[-1]);
ALTER TABLE c_posjournal_c_cash_v OWNER TO libertya;

--View c_posjournal_c_payment_v 
CREATE OR REPLACE VIEW c_posjournal_c_payment_v AS 
 select * from c_posjournal_c_payment_v_filtered(array[-1]);
ALTER TABLE c_posjournal_c_payment_v OWNER TO libertya;

--View c_posjournalinvoices_v
CREATE OR REPLACE VIEW c_posjournalinvoices_v AS 
 select * from c_posjournalinvoices_v_filtered(array[-1]);
ALTER TABLE c_posjournalinvoices_v OWNER TO libertya;

--20150708-1511 Mejora de performance al abrir ventana de Facturas
UPDATE AD_Column SET AD_Reference_ID = 30 WHERE AD_ComponentObjectUID = 'CORE-AD_Column-10788';

--20150710-1251 Mejora de performance al abrir ventana de Facturas (cont)
UPDATE AD_Column SET AD_Reference_ID = 30 WHERE AD_ComponentObjectUID = 'CORE-AD_Column-10805';

--20150728-1850 Incremento de tamaño de la columna DocumentNote de la tabla C_DocType
UPDATE pg_attribute
SET atttypmod = 2504
WHERE attrelid = 'c_doctype'::regclass AND attname = 'documentnote';

UPDATE ad_column
SET fieldlength = 2500, ad_reference_id = 14
where ad_componentobjectuid IN ('CORE-AD_Column-3025');

--20150804 2030 Nuevas columnas para Formato de Exportación
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ExpFormat','EncodingType','character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ExpFormat','EndLineType','character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ExpFormat','Extension','character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ExpFormat_Row','NoDecimalPoint','character(1) default ''N''::bpchar'));
UPDATE AD_ExpFormat SET encodingtype = 'U', endlinetype='U', extension = 'C';

--20150804 2100 Nueva función utilizada en Exportación - Régimen de Información
CREATE OR REPLACE FUNCTION getimporteoperacionesexentas(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    	v_Amount        	NUMERIC;
BEGIN
    SELECT COALESCE(SUM(it.TaxBaseAmt), 0)
    INTO v_Amount
    FROM C_Invoicetax it
    INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)
    WHERE (t.rate = 0 OR t.IsTaxExempt = 'Y') AND C_Invoice_ID = p_c_invoice_id;
    
    RETURN v_Amount;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getimporteoperacionesexentas(integer) OWNER TO libertya;

--20150804 2100 Nueva función utilizada en Exportación - Régimen de Información
CREATE OR REPLACE FUNCTION getimportepercepcionesiibb(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    	v_Amount        	NUMERIC;
BEGIN
    SELECT COALESCE(SUM(it.TaxAmt), 0)
    INTO v_Amount
    FROM C_Invoicetax it
    WHERE C_Invoice_ID = p_c_invoice_id AND C_Tax_ID IN (SELECT C_Tax_ID FROM C_Tax Where perceptionType = 'B');   

    RETURN v_Amount;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getimportepercepcionesiibb(integer) OWNER TO libertya;

--20150804 2100 Nueva función utilizada en Exportación - Régimen de Información
CREATE OR REPLACE FUNCTION getcantidadalicuotasiva(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    	v_Cant        	NUMERIC;
BEGIN
    SELECT COUNT(*)
    INTO v_Cant
    FROM C_Invoicetax it
    INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)
    WHERE (C_Invoice_ID = p_c_invoice_id) AND (isPercepcion = 'N');

    RETURN v_Cant;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getcantidadalicuotasiva(integer) OWNER TO libertya;

--20150804 2100 Nueva vista utilizada en Exportación - Régimen de Información
CREATE OR REPLACE VIEW reginfo_ventas_cbte_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, i.numerocomprobante AS nrocomprobantehasta, bp.taxidtype AS codigodoccomprador, bp.taxid AS nroidentificacioncomprador, bp.name AS nombrecomprador, currencyconvert(i.grandtotal, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal, currencyconvert(i.grandtotal - i.netamount, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impconceptosnoneto, 0::numeric(20,2) AS imppercepnocategorizados, currencyconvert(getimporteoperacionesexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas, 0::numeric(20,2) AS imppercepopagosdeimpunac, currencyconvert(getimportepercepcionesiibb(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, 0::numeric(20,2) AS imppercepimpumuni, 0::numeric(20,2) AS impimpuinternos, cu.wsfecode AS codmoneda, currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, getcantidadalicuotasiva(i.c_invoice_id) AS cantalicuotasiva, NULL::character varying(1) AS codigooperacion, 0::numeric(20,2) AS impotrostributos, NULL::timestamp without time zone AS fechavencimientopago
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_ventas_cbte_v OWNER TO libertya;

--20150804 2230 Nueva vista utilizada en Exportación - Régimen de Información
CREATE OR REPLACE VIEW reginfo_ventas_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_ventas_alicuotas_v OWNER TO libertya;

--20150812-1555 Correcciones para cobros adelantados para el informe de Declaración de Valores
-- Function: c_posjournal_c_cash_v_filtered(anyarray)

-- DROP FUNCTION c_posjournal_c_cash_v_filtered(anyarray);

CREATE OR REPLACE FUNCTION c_posjournal_c_cash_v_filtered(posjournalids anyarray)
  RETURNS SETOF c_posjournal_v_type AS
$BODY$
declare
	consulta varchar;
	wherePosJournalIDs varchar;
	existsPosJournalIDs boolean;
	adocument c_posjournal_v_type;
BEGIN
	-- Armado de condiciones de filtro parámetro
	existsPosJournalIDs = posjournalIDs is not null and array_length(posjournalIDs,1) > 0 and posjournalIDs[1] <> -1;

	-- Filtro de cajas diarias
	wherePosJournalIDs = '';
	if existsPosJournalIDs then
		wherePosJournalIDs = ' AND c.c_posjournal_id IN ('|| array_to_string(posjournalIDs, ',') || ')';
	end if;
	
	consulta =  
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, ''CA''::character varying AS tendertype, NULL::character varying AS documentno, cl.description, ((c.name::text || ''_''::text) || cl.line::text)::character varying AS info, COALESCE(currencyconvert((case when ah.allocationtype in (''OPA'',''RCA'') then cl.amount else al.amount + al.discountamt + al.writeoffamt end), ah.c_currency_id, (case when ah.allocationtype in (''OPA'',''RCA'') then cl.c_currency_id else i.c_currency_id end), NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, NULL::integer AS c_doctype_id, NULL::character varying AS checkno, NULL::character varying AS a_bank, NULL::character varying AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying AS couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, (case when ah.allocationtype in (''OPA'',''RCA'') then ''Anticipo'' else i.documentno end) AS invoice_documentno, i.grandtotal AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   WHERE 1=1 ' || wherePosJournalIDs || ' AND NOT EXISTS (SELECT ala.c_cashline_id FROM c_allocationline as ala INNER JOIN c_allocationhdr as aha on aha.c_allocationhdr_id = ala.c_allocationhdr_id WHERE ala.c_cashline_id = cl.c_cashline_id AND aha.allocationtype in (''OPA'',''RCA'') AND aha.c_allocationhdr_id <> ah.c_allocationhdr_id) ' ||
   ' UNION ALL 
         SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::integer AS c_invoice_id, NULL::integer AS c_payment_id, cl.c_cashline_id, NULL::integer AS c_invoice_credit_id, ''CA''::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || ''_''::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::integer AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::integer AS m_entidadfinancieraplan_id, NULL::integer AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc(''day''::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, NULL::character varying AS entidadfinanciera_value, NULL::character varying AS entidadfinanciera_name, NULL::character varying AS bp_entidadfinanciera_value, NULL::character varying AS bp_entidadfinanciera_name, NULL::character varying AS cupon, NULL::character varying AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted
           FROM c_cashline cl
      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
     WHERE 1=1 '  || wherePosJournalIDs || 
     ' AND NOT (EXISTS ( SELECT al.c_allocationline_id
              FROM c_allocationline al
             WHERE al.c_cashline_id = cl.c_cashline_id));';

	--raise notice '%', consulta;
	FOR adocument IN EXECUTE consulta LOOP
		return next adocument;
	END LOOP;
END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_cash_v_filtered(anyarray) OWNER TO libertya;

-- Function: c_posjournal_c_payment_v_filtered(anyarray)

-- DROP FUNCTION c_posjournal_c_payment_v_filtered(anyarray);

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
	'SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, p.tendertype::character varying AS tendertype, p.documentno, p.description, ((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text))::character varying AS info, COALESCE(currencyconvert((case when ah.allocationtype in (''OPA'',''RCA'') then p.payamt else al.amount + al.discountamt + al.writeoffamt end), ah.c_currency_id, (case when ah.allocationtype in (''OPA'',''RCA'') then p.c_currency_id else i.c_currency_id end), NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, ah.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, (case when ah.allocationtype in (''OPA'',''RCA'') then ''Anticipo'' else i.documentno end) AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, al.changeamt
           FROM c_allocationline al
      JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   WHERE 1=1 ' || wherePosJournalIDs || ' AND NOT EXISTS (SELECT ala.c_payment_id FROM c_allocationline as ala INNER JOIN c_allocationhdr as aha on aha.c_allocationhdr_id = ala.c_allocationhdr_id WHERE ala.c_payment_id = p.c_payment_id AND aha.allocationtype in (''OPA'',''RCA'') AND aha.c_allocationhdr_id <> ah.c_allocationhdr_id) ' || 
   ' UNION ALL 
        ( SELECT NULL::integer AS c_allocationhdr_id, NULL::integer AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::integer AS c_invoice_id, p.c_payment_id, NULL::integer AS c_cashline_id, NULL::integer AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || ''_''::text) || to_char(p.datetrx, ''DD/MM/YYYY''::text)))::character varying(255) AS info, p.payamt AS amount, NULL::integer AS c_cash_id, NULL::numeric(18,0) AS line, NULL::integer AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc(''day''::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::character varying(30) AS invoice_documentno, NULL::numeric(20,2) AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::bpchar AS isfiscaldocument, NULL::bpchar AS isfiscal, NULL::bpchar AS fiscalalreadyprinted, 0 AS changeamt
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
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_posjournal_c_payment_v_filtered(anyarray) OWNER TO libertya;

-- 20150823-1645 Nueva columna para indicar el tipo de exportación al exterior (Facturación Electrónica).
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_OrgInfo', 'exporttypefe', 'character(1)'));
UPDATE AD_OrgInfo SET exporttypefe = '4';

-- 20150823-1645 Nueva columna para indicar el tipo de exportación al exterior (Facturación Electrónica).
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ClientInfo', 'exporttypefe', 'character(1)'));
UPDATE AD_ClientInfo SET exporttypefe = '4';

-- 20150823-1645 Nueva columna para indicar si se tiene permiso de embarque para exportación al exterior (Facturación Electrónica).
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_OrgInfo','shipmentpermitfe', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

-- 20150823-1645 Nueva columna para indicar si se tiene permiso de embarque para exportación al exterior (Facturación Electrónica).
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','shipmentpermitfe', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

-- 20150823-1715 Nueva columna para indicar el código de país que asigna la AFIP (Facturación Electrónica).
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_country','countrycodefe', 'character varying(10)'));
-- 401	ALBANIA
UPDATE c_country SET countrycodefe = '401' WHERE c_country_id = 111;
-- 200	ARGENTINA
UPDATE c_country SET countrycodefe = '200' WHERE c_country_id = 119;
-- 202	BOLIVIA
UPDATE c_country SET countrycodefe = '202' WHERE c_country_id = 135;
-- 203	BRASIL
UPDATE c_country SET countrycodefe = '203' WHERE c_country_id = 139;
-- 204	CANADA
UPDATE c_country SET countrycodefe = '204' WHERE c_country_id = 109;	
-- 205	COLOMBIA
UPDATE c_country SET countrycodefe = '205' WHERE c_country_id = 156;	
-- 206	COSTA RICA
UPDATE c_country SET countrycodefe = '206' WHERE c_country_id = 161;
-- 207	CUBA
UPDATE c_country SET countrycodefe = '207' WHERE c_country_id = 164;
-- 208	CHILE
UPDATE c_country SET countrycodefe = '208' WHERE c_country_id = 152;
-- 210	ECUADOR
UPDATE c_country SET countrycodefe = '210' WHERE c_country_id = 171;	
-- 211	EL SALVADOR
UPDATE c_country SET countrycodefe = '211' WHERE c_country_id = 173;		
-- 212	ESTADOS UNIDOS
UPDATE c_country SET countrycodefe = '212' WHERE c_country_id = 100;
-- 213	GUATEMALA
UPDATE c_country SET countrycodefe = '213' WHERE c_country_id = 197;
-- 214	GUYANA
UPDATE c_country SET countrycodefe = '214' WHERE c_country_id = 200;	
-- 215	HAITI
UPDATE c_country SET countrycodefe = '215' WHERE c_country_id = 201;
-- 216	HONDURAS
UPDATE c_country SET countrycodefe = '216' WHERE c_country_id = 204;
-- 217	JAMAICA
UPDATE c_country SET countrycodefe = '217' WHERE c_country_id = 215;
-- 218	MEXICO
UPDATE c_country SET countrycodefe = '218' WHERE c_country_id = 247;
-- 219	NICARAGUA
UPDATE c_country SET countrycodefe = '219' WHERE c_country_id = 265;
-- 220	PANAMA
UPDATE c_country SET countrycodefe = '220' WHERE c_country_id = 274;
-- 221	PARAGUAY
UPDATE c_country SET countrycodefe = '221' WHERE c_country_id = 276;
-- 222	PERU
UPDATE c_country SET countrycodefe = '222' WHERE c_country_id = 277;
-- 223	PUERTO RICO
UPDATE c_country SET countrycodefe = '223' WHERE c_country_id = 282;
-- 224	TRINIDAD Y TOBAGO
UPDATE c_country SET countrycodefe = '224' WHERE c_country_id = 324;
-- 225	URUGUAY
UPDATE c_country SET countrycodefe = '225' WHERE c_country_id = 336;
-- 226	VENEZUELA
UPDATE c_country SET countrycodefe = '226' WHERE c_country_id = 339;
-- 310	CHINA
UPDATE c_country SET countrycodefe = '310' WHERE c_country_id = 153;
-- 313	TAIWAN
UPDATE c_country SET countrycodefe = '313' WHERE c_country_id = 316;
-- 315	INDIA
UPDATE c_country SET countrycodefe = '315' WHERE c_country_id = 208;
-- 320	JAPON
UPDATE c_country SET countrycodefe = '320' WHERE c_country_id = 216;
-- 406	BELGICA
UPDATE c_country SET countrycodefe = '406' WHERE c_country_id = 103;
-- 407	BULGARIA
UPDATE c_country SET countrycodefe = '407' WHERE c_country_id = 142;
-- 410	ESPAÑA
UPDATE c_country SET countrycodefe = '410' WHERE c_country_id = 106;
-- 412	FRANCIA
UPDATE c_country SET countrycodefe = '412' WHERE c_country_id = 102;
-- 413	GRECIA
UPDATE c_country SET countrycodefe = '413' WHERE c_country_id = 192;
-- 415	IRLANDA
UPDATE c_country SET countrycodefe = '415' WHERE c_country_id = 212;
-- 417	ITALIA
UPDATE c_country SET countrycodefe = '417' WHERE c_country_id = 214;
-- 424	POLONIA
UPDATE c_country SET countrycodefe = '424' WHERE c_country_id = 280;
-- 425	PORTUGAL
UPDATE c_country SET countrycodefe = '425' WHERE c_country_id = 281;
-- 429	SUECIA
UPDATE c_country SET countrycodefe = '429' WHERE c_country_id = 313;
-- 436	TURQUIA
UPDATE c_country SET countrycodefe = '436' WHERE c_country_id = 326;
-- 444	RUSIA
UPDATE c_country SET countrycodefe = '444' WHERE c_country_id = 286;
-- 445	UCRANIA
UPDATE c_country SET countrycodefe = '445' WHERE c_country_id = 331;

-- 20150823-1735 Nueva columna para indicar el código de unidad de medida que asigna la AFIP (Facturación Electrónica).
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_UOM','uomcodefe', 'character varying(10)'));
-- 1	kg
UPDATE C_UOM SET uomcodefe = '1' WHERE name = 'kg';
-- 5	LT
UPDATE C_UOM SET uomcodefe = '5' WHERE name = 'LT';
-- 7	Unidad
UPDATE C_UOM SET uomcodefe = '7' WHERE name = 'Unidad';
-- 2	Metro
UPDATE C_UOM SET uomcodefe = '2' WHERE name = 'Metro';
-- 20	Centimetro
UPDATE C_UOM SET uomcodefe = '20' WHERE name = 'Centimetro';
-- 14	Gramo
UPDATE C_UOM SET uomcodefe = '14' WHERE name = 'Gramo';
-- 3	Metro Cuadrado
UPDATE C_UOM SET uomcodefe = '3' WHERE name = 'Metro Cuadrado';

-- 20150824-2345 Se elimina el filtro por docstatus.
CREATE OR REPLACE FUNCTION v_documents_org_filtered(bpartner integer, summaryonly boolean)
  RETURNS SETOF v_documents_org_type AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    initialcam varchar;
    adocument v_documents_org_type;
   
BEGIN
    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, i.initialcurrentaccountamt, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, COALESCE(i.initialcurrentaccountamt, 0.00), bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE il.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(i.initialcurrentaccountamt, 0.00), COALESCE(bp.socreditstatus, bp2.socreditstatus) ';

        leftjoin1 = ' LEFT JOIN ( SELECT di.c_payment_id, sum(di.initialcurrentaccountamt) AS initialcurrentaccountamt
                 FROM ( SELECT DISTINCT al.c_payment_id, al.c_invoice_id, i.initialcurrentaccountamt
                         FROM c_allocationline al
                JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id and (' || $1 || ' = -1 or i.c_bpartner_id = ' || $1 || ')
                   ORDER BY al.c_payment_id, al.c_invoice_id, i.initialcurrentaccountamt) di
                GROUP BY di.c_payment_id) i ON i.c_payment_id = p.c_payment_id ';

        leftjoin2 = '  LEFT JOIN ( SELECT di.c_cashline_id, sum(di.initialcurrentaccountamt) AS initialcurrentaccountamt
                FROM ( SELECT DISTINCT lc.c_cashline_id, lc.c_invoice_id, i.initialcurrentaccountamt
                    FROM c_allocationline lc
                   JOIN c_invoice i ON i.c_invoice_id = lc.c_invoice_id
                 WHERE (' || $1 || ' = -1 or i.c_bpartner_id = ' || $1 || ')
                  ORDER BY lc.c_cashline_id, lc.c_invoice_id, i.initialcurrentaccountamt) di
               GROUP BY di.c_cashline_id) i ON i.c_cashline_id = cl.c_cashline_id ';

        initialcam = ' i.initialcurrentaccountamt ';
   
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';
        leftjoin1 = '';
        leftjoin2 = '';
        initialcam = '0';

    end if;

    consulta = '

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, ' || initialcam || ', bp.socreditstatus
                   FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id

' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, COALESCE(' || initialcam || ', 0.00) AS initialcurrentaccountamt, bp.socreditstatus
                   FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')


' || leftjoin1 || '

   LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id

' || orderby2 || '


))

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
                    ELSE il.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(' || initialcam ||', 0.00) AS initialcurrentaccountamt, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus
           FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id


' || leftjoin2 || '


   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)

' || orderby3 || '

); ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean) OWNER TO libertya;

--20150825-1437 Nueva columna para permitir, o no, pagos/recibos anticipados
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_BPartner','allowadvancedpaymentreceipts','character(1) NOT NULL default ''Y''::bpchar'));

--20150825-1500 Nueva columna para permitir, o no, pagos/cobros parciales
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_BPartner','allowpartialpayment','character(1) NOT NULL default ''Y''::bpchar'));

--20150907-0100 Nueva columna de configuración para tipos de documento para validar números de documento únicos 
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_DocType','uniquedocumentno','character(1) NOT NULL default ''N''::bpchar'));