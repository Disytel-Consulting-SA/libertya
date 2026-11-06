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
