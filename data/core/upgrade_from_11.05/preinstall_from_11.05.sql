-- ========================================================================================
-- PREINSTALL FROM 11.05. 
-- ========================================================================================
-- 	Consideraciones importantes:
--			1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 			2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

-- 20110719-1647 Incorporación de lógica de despliegue a campos parámetros de un proceso
ALTER TABLE ad_process_para ADD COLUMN displaylogic character varying(1000);

-- 20110720-1550 Columna para especificar si el GenerateModel debe crear los metodos insertDirect
alter table ad_table add column generateDirectMethods character(1) default 'N';

-- 20110722-1510 Modificación a vista para que no tome el join obligatorio de familias
CREATE OR REPLACE VIEW rv_rotationreport AS 
 SELECT o.ad_org_id, pg.m_product_gamas_id, pl.m_product_lines_id, io.issotrx, o.ad_client_id, o.dateordered AS datefrom, o.dateordered AS dateto, o.dateinvoiced AS dateinvoicedfrom, o.dateinvoiced AS dateinvoicedto, io.movementdate AS movementdatefrom, io.movementdate AS movementdateto, il.m_product_id, p.m_product_category_id, ol.m_inout_id, io.movementtype, ol.m_inoutline_id, 
        CASE
            WHEN io.issotrx = 'Y'::bpchar THEN sum(ol.movementqty)
            ELSE 0::numeric
        END AS movementin, 
        CASE
            WHEN io.issotrx = 'N'::bpchar THEN sum(ol.movementqty)
            ELSE 0::numeric
        END AS movementout
   FROM c_invoice o
   JOIN c_invoiceline il ON o.c_invoice_id = il.c_invoice_id
   JOIN m_product p ON p.m_product_id = il.m_product_id
   JOIN m_product_category pc ON pc.m_product_category_id = p.m_product_category_id
   JOIN c_bpartner b ON b.c_bpartner_id = o.c_bpartner_id
   LEFT JOIN m_product_gamas pg ON pg.m_product_gamas_id = pc.m_product_gamas_id
   LEFT JOIN m_product_lines pl ON pl.m_product_lines_id = pg.m_product_lines_id
   JOIN m_inoutline ol ON ol.m_product_id = p.m_product_id
   JOIN m_inout io ON io.m_inout_id = ol.m_inout_id
  GROUP BY o.ad_client_id, o.ad_org_id, pg.m_product_gamas_id, pl.m_product_lines_id, io.issotrx, o.dateordered, o.dateinvoiced, io.movementdate, il.m_product_id, p.m_product_category_id, ol.m_inout_id, io.movementtype, ol.m_inoutline_id;

ALTER TABLE rv_rotationreport OWNER TO libertya;

-- 20110727-1616 Modificación de compañía y organización para las traducciones de los elementos del formato de impresión del informe Informe del Valor del Inventario ya que se encontraba con compañía y organización Wide Systems.
UPDATE AD_PrintFormatItem_trl
SET ad_client_id = 0, ad_org_id = 0
WHERE AD_PrintFormatItem_ID IN (SELECT AD_PrintFormatItem_ID
				FROM AD_PrintFormatItem as pfi
				WHERE AD_PrintFormat_ID=1000756);

-- 20110727-1720 Incorporación de nueva columna para mostrar o no los registros sin precio
ALTER TABLE t_inventoryvalue ADD COLUMN ShowNoPrice character(1);

-- 20110728-1643 Nueva vista corrigiendo algunos errores encontrados en la vista original rv_rotationreport
CREATE OR REPLACE VIEW rv_rotationreport_v2 AS 
 SELECT io.ad_org_id, pg.m_product_gamas_id, pl.m_product_lines_id, io.issotrx, io.ad_client_id, io.movementdate AS movementdatefrom, io.movementdate AS movementdateto, ol.m_product_id, p.m_product_category_id, io.m_inout_id, io.movementtype, ol.m_inoutline_id, 
        CASE
            WHEN charat(io.movementtype::character varying, 1)::text = '-'::text THEN sum(ol.movementqty)
            ELSE 0::numeric
        END AS movementout, 
        CASE
            WHEN charat(io.movementtype::character varying, 1)::text = '+'::text THEN sum(ol.movementqty)
            ELSE 0::numeric
        END AS movementin
   FROM m_inout io
   JOIN m_inoutline ol ON ol.m_inout_id = io.m_inout_id
   JOIN m_product p ON p.m_product_id = ol.m_product_id
   JOIN m_product_category pc ON pc.m_product_category_id = p.m_product_category_id
   JOIN c_bpartner b ON b.c_bpartner_id = io.c_bpartner_id
   LEFT JOIN m_product_gamas pg ON pg.m_product_gamas_id = pc.m_product_gamas_id
   LEFT JOIN m_product_lines pl ON pl.m_product_lines_id = pg.m_product_lines_id
  GROUP BY io.ad_client_id, io.ad_org_id, pg.m_product_gamas_id, pl.m_product_lines_id, io.issotrx, io.movementdate, ol.m_product_id, p.m_product_category_id, io.m_inout_id, io.movementtype, ol.m_inoutline_id;

ALTER TABLE rv_rotationreport_v2 OWNER TO libertya;

-- 20110801-1649 Extracto Bancario. Parche. Borrado del parámetro DoImport creado por el patch generado ya que esta versión crea nuevamente el parámetro
DELETE FROM ad_process_para WHERE ad_componentobjectuid = 'PEXB-AD_Process_Para-1010492';

-- 20110803-1701 Nueva columna en el reporte Informe de Saldos para poder filtrar por grupo de entidad comercial
ALTER TABLE T_BalanceReport ADD COLUMN c_bp_group_id integer;

-- 20110804-1501 Contemplar la posibilidad de mostrar los pedidos no facturados en el informe de cuenta corriente
ALTER TABLE T_CuentaCorriente ADD COLUMN includeopenorders varchar(1);

-- 20110812-1350 No se permitian nombres de clases/packages cortos en procesos
ALTER TABLE ad_process ALTER classname TYPE character varying(255);

-- 20110815-1126 Insert masivo de traducciones para la localización Paraguay
INSERT INTO AD_Message_Trl SELECT ad_message_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, msgtext, msgtip, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Message_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Menu_Trl SELECT ad_menu_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Menu_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Tab_Trl SELECT ad_tab_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, commitwarning, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Tab_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Task_Trl SELECT ad_task_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Task_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Reference_Trl SELECT ad_reference_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Reference_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Field_Trl SELECT ad_field_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Field_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_WF_Node_Trl SELECT 'es_PY', ad_wf_node_id, ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_WF_Node_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Window_Trl SELECT ad_window_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Window_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Workflow_Trl SELECT ad_workflow_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Workflow_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Ref_List_Trl SELECT ad_ref_list_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Ref_List_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Element_Trl SELECT ad_element_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", printname, description, help, po_name, po_printname, po_description, po_help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Element_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Process_Para_Trl SELECT ad_process_para_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Process_Para_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Process_Trl SELECT ad_process_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Process_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_DocType_Trl SELECT c_doctype_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", printname, documentnote, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_DocType_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_ElementValue_Trl SELECT c_elementvalue_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, istranslated FROM C_ElementValue_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_PaymentTerm_Trl SELECT c_paymentterm_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, documentnote, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_PaymentTerm_Trl WHERE ad_language = 'es_AR';
INSERT INTO M_Product_Trl SELECT m_product_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", documentnote, istranslated, description, replace(ad_componentobjectuid,'es_AR','es_PY') FROM M_Product_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_DunningLevel_Trl SELECT 'es_PY', c_dunninglevel_id, ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, printname, note, istranslated FROM C_DunningLevel_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_UOM_Trl SELECT c_uom_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, uomsymbol, "name", description, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_UOM_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_Greeting_Trl SELECT 'es_PY', c_greeting_id, ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", greeting, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_Greeting_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_TaxCategory_Trl SELECT c_taxcategory_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_TaxCategory_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Form_Trl SELECT ad_form_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Form_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_FieldGroup_Trl SELECT ad_fieldgroup_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_FieldGroup_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Desktop_Trl SELECT ad_desktop_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, (select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023'),replace(ad_componentobjectuid,'es_AR','es_PY') FROM AD_Desktop_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Workbench_Trl SELECT ad_workbench_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", description, help, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Workbench_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_PrintFormatItem_Trl SELECT ad_printformatitem_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, printname, istranslated, printnamesuffix, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_PrintFormatItem_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_Tax_Trl SELECT c_tax_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, istranslated, "name", description, taxindicator, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_Tax_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_PrintLabelLine_Trl SELECT ad_printlabelline_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, printname, istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_PrintLabelLine_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_Country_Trl SELECT c_country_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, istranslated, "name", description, regionname, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_Country_Trl WHERE ad_language = 'es_AR';
INSERT INTO C_Currency_Trl SELECT c_currency_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, istranslated, cursymbol, description, replace(ad_componentobjectuid,'es_AR','es_PY') FROM C_Currency_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Table_Trl SELECT ad_table_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Table_Trl WHERE ad_language = 'es_AR';
INSERT INTO AD_Column_Trl SELECT ad_column_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, "name", istranslated, replace(ad_componentobjectuid,'es_AR','es_PY'),(select ad_componentversion_id FROM ad_componentversion WHERE ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010023') FROM AD_Column_Trl WHERE ad_language = 'es_AR';
INSERT INTO W_Store_Trl SELECT w_store_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, istranslated, webinfo, webparam1, webparam2, webparam3, webparam4, webparam5, webparam6, emailheader, emailfooter FROM W_Store_Trl WHERE ad_language = 'es_AR';
INSERT INTO W_MailMsg_Trl SELECT w_mailmsg_id, 'es_PY', ad_client_id, ad_org_id, isactive, now(), createdby, now(), updatedby, istranslated, subject, message, message2, message3 FROM W_MailMsg_Trl WHERE ad_language = 'es_AR';

-- 20110816-14:33 Nuevas columnas a la tabla temporal del reporte Estado de Cta de EC
ALTER TABLE t_estadodecuenta ADD COLUMN showdocuments character varying(1);
ALTER TABLE t_estadodecuenta ADD COLUMN salesrep_id integer;

-- 20110816-14:33 Borrado de registros que ha creado el patch que arregla el informe de estado de cta. Si no se ha instalado el patch estas sentencias no tienen efecto.
DELETE FROM ad_process_para WHERE ad_componentobjectuid = 'PCTA-AD_Process_Para-1010492';
DELETE FROM ad_process_para WHERE ad_componentobjectuid = 'PCTA-AD_Process_Para-1010493';
DELETE FROM ad_process_para WHERE ad_componentobjectuid = 'PCTA-AD_Process_Para-1010494';
DELETE FROM ad_preference WHERE ad_componentobjectuid = 'PCTA-AD_Reference-1010178';

-- 20110905-1453 Nuevos índices de mejora de performance al eliminar registros con FK referenciadas
CREATE INDEX c_invoice_ref_invoice
  ON c_invoice
  USING btree
  (ref_invoice_id);

CREATE INDEX c_cashline_invoice_id
  ON c_cashline
  USING btree
  (c_invoice_id);

-- 20110905-1725 Vista para que muestre bien los importes de las facturas en el informe de ventas
CREATE OR REPLACE VIEW c_invoice_bydoctype_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, 
	i.chargeamt,
	(i.chargeamt * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS chargeamt_withsign, 
	(i.chargeamt * (CASE WHEN substring(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric ELSE -1::numeric END))::numeric(20,2) AS chargeamt_withmultiplier, 
	i.totallines, 
	(i.totallines * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS totallines_withsign, 
	(i.totallines * (CASE WHEN substring(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric ELSE -1::numeric END))::numeric(20,2) AS totallines_withmultiplier, 
	i.grandtotal, 
	(i.grandtotal * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS grandtotal_withsign,
	(i.grandtotal * (CASE WHEN substring(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric ELSE -1::numeric END))::numeric(20,2) AS grandtotal_withmultiplier, 
	d.signo_issotrx, 
	(CASE WHEN substring(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric ELSE -1::numeric END) as multiplier,d.docbasetype 
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id;
ALTER TABLE c_invoice_bydoctype_v OWNER TO libertya; 

-- 20110913-0934 TPV Performance: Reduccion considerable de tiempos de respuesta en consultas de precio
CREATE INDEX m_product_m_product_id_id ON m_product (m_product_id);
CREATE INDEX m_productprice_m_pricelist_version_id_m_product_id ON m_productprice (m_pricelist_version_id, m_product_id);
CREATE INDEX m_productprice_m_pricelist_version_id_pp ON m_productprice (m_pricelist_version_id);
CREATE INDEX m_pricelist_version_m_pricelist_version_id ON m_pricelist_version (m_pricelist_version_id);
CREATE INDEX m_pricelist_version_m_pricelist_version_id_m_pricelist_id ON m_pricelist_version (m_pricelist_version_id, m_pricelist_id);
CREATE INDEX m_pricelist_m_pricelist_id ON m_pricelist (m_pricelist_id);

-- 20110913-1730 Descuento manual general para la ventana de facturas
ALTER TABLE c_invoice ADD COLUMN manualgeneraldiscount numeric(9,2) NOT NULL DEFAULT 0;

-- 20110914-1330 Nuevas columnas a la tabla de importación de EC
ALTER TABLE i_bpartner ADD COLUMN c_categoria_iva_id integer;
ALTER TABLE i_bpartner ADD COLUMN c_categoria_iva_codigo integer;
ALTER TABLE i_bpartner ADD COLUMN isprospect character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE i_bpartner ADD COLUMN isvendor character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE i_bpartner ADD COLUMN iscustomer character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE i_bpartner ADD COLUMN isemployee character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE i_bpartner ADD COLUMN socreditstatus character(1) DEFAULT 'D'::bpchar;
ALTER TABLE i_bpartner ADD COLUMN so_creditlimit numeric(20,2) DEFAULT 0;
ALTER TABLE i_bpartner ADD COLUMN salesrep_id integer;
ALTER TABLE i_bpartner ADD COLUMN salesrep_name character varying(60);

-- 20110915-1324 Funcion para estimar el numero de tuplas resultante de una query 
CREATE OR REPLACE FUNCTION count_estimate(query text) RETURNS integer AS $$
DECLARE
    rec   record;
    rows  integer;
BEGIN
    FOR rec IN EXECUTE 'EXPLAIN ' || query LOOP
        rows := substring(rec."QUERY PLAN" FROM ' rows=([[:digit:]]+)');
        EXIT WHEN rows IS NOT NULL;
    END LOOP;
 
    RETURN rows;
END;
$$ LANGUAGE plpgsql VOLATILE STRICT;

-- 20110915-1657 Incorporación de nueva columna saldo para el Informe de Balance
ALTER TABLE T_ACCT_Balance ADD COLUMN balance numeric(22,2);

-- 20110916-12:55 Nuevas columnas e índices a la tabla de importación de artículos
ALTER TABLE i_product ADD COLUMN c_taxcategory_id integer;
ALTER TABLE i_product ADD COLUMN c_taxcategory_name character varying(60);
ALTER TABLE i_product
  ADD CONSTRAINT taxcategory_iproduct FOREIGN KEY (c_taxcategory_id)
      REFERENCES c_taxcategory (c_taxcategory_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE i_product ADD COLUMN checkoutplace character(1) DEFAULT 'B'::bpchar;
ALTER TABLE i_product ADD COLUMN issold character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE i_product ADD COLUMN ispurchased character(1) NOT NULL DEFAULT 'Y'::bpchar;
CREATE INDEX i_product_value  ON libertya.i_product (value);
CREATE INDEX i_product_upc   ON libertya.i_product (upc);
CREATE INDEX i_product_bp_vendorpno ON libertya.i_product (c_bpartner_id, vendorproductno);
CREATE INDEX i_product_producttype  ON libertya.i_product (producttype);
CREATE INDEX i_product_c_bpartner_id  ON libertya.i_product (c_bpartner_id);

-- 20110919-1455 Aumento del tamaño de columnas para esquemas de retenciones 
ALTER TABLE c_retencionschema ALTER COLUMN value TYPE character varying(40);
ALTER TABLE c_retencionschema ALTER COLUMN name TYPE character varying(80);

-- 20110928-0830 Modificaciones de vistas rv_openitem y rv_payment para el informe de vencimientos
DROP VIEW rv_openitem;

CREATE OR REPLACE VIEW rv_openitem AS 
 SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, p.netdays, i.dateinvoiced + (p.netdays::text || ' days'::text)::interval AS duedate, paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) AS daysdue, i.dateinvoiced + (p.discountdays::text || ' days'::text)::interval AS discountdate, round(i.grandtotal * p.discount / 100::numeric, 2) AS discountamt, i.grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, 0) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, NULL::"unknown" AS c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
   FROM rv_c_invoice i
   JOIN c_paymentterm p ON i.c_paymentterm_id = p.c_paymentterm_id
  WHERE invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid <> 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar
UNION 
 SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, to_days(ips.duedate) - to_days(i.dateinvoiced) AS netdays, ips.duedate, to_days(now()) - to_days(ips.duedate) AS daysdue, ips.discountdate, ips.discountamt, ips.dueamt AS grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, ips.c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
   FROM rv_c_invoice i
   JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid = 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE rv_openitem OWNER TO libertya;

DROP VIEW rv_payment;

CREATE OR REPLACE VIEW rv_payment AS 
 SELECT c_payment.c_payment_id, c_payment.ad_client_id, c_payment.ad_org_id, c_payment.isactive, c_payment.created, c_payment.createdby, c_payment.updated, c_payment.updatedby, c_payment.documentno, c_payment.datetrx, c_payment.isreceipt, c_payment.c_doctype_id, c_payment.trxtype, c_payment.c_bankaccount_id, c_payment.c_bpartner_id, c_payment.c_invoice_id, c_payment.c_bp_bankaccount_id, c_payment.c_paymentbatch_id, c_payment.tendertype, c_payment.creditcardtype, c_payment.creditcardnumber, c_payment.creditcardvv, c_payment.creditcardexpmm, c_payment.creditcardexpyy, c_payment.micr, c_payment.routingno, c_payment.accountno, c_payment.checkno, c_payment.a_name, c_payment.a_street, c_payment.a_city, c_payment.a_state, c_payment.a_zip, c_payment.a_ident_dl, c_payment.a_ident_ssn, c_payment.a_email, c_payment.voiceauthcode, c_payment.orig_trxid, c_payment.ponum, c_payment.c_currency_id, c_payment.c_conversiontype_id, 
        CASE c_payment.isreceipt
            WHEN 'Y'::bpchar THEN c_payment.payamt
            ELSE c_payment.payamt * (- 1::numeric)
        END AS payamt, 
        CASE c_payment.isreceipt
            WHEN 'Y'::bpchar THEN c_payment.discountamt
            ELSE c_payment.discountamt * (- 1::numeric)
        END AS discountamt, 
        CASE c_payment.isreceipt
            WHEN 'Y'::bpchar THEN c_payment.writeoffamt
            ELSE c_payment.writeoffamt * (- 1::numeric)
        END AS writeoffamt, 
        CASE c_payment.isreceipt
            WHEN 'Y'::bpchar THEN c_payment.taxamt
            ELSE c_payment.taxamt * (- 1::numeric)
        END AS taxamt, 
        CASE c_payment.isreceipt
            WHEN 'Y'::bpchar THEN c_payment.overunderamt
            ELSE c_payment.overunderamt * (- 1::numeric)
        END AS overunderamt, 
        CASE c_payment.isreceipt
            WHEN 'Y'::bpchar THEN 1.0
            ELSE -1.0
        END AS multiplierap, paymentallocated(c_payment.c_payment_id::numeric, c_payment.c_currency_id::numeric) AS allocatedamt, paymentavailable(c_payment.c_payment_id::numeric) AS availableamt, c_payment.isoverunderpayment, c_payment.isapproved, c_payment.r_pnref, c_payment.r_result, c_payment.r_respmsg, c_payment.r_authcode, c_payment.r_avsaddr, c_payment.r_avszip, c_payment.r_info, c_payment.processing, c_payment.oprocessing, c_payment.docstatus, c_payment.docaction, c_payment.isprepayment, c_payment.c_charge_id, c_payment.isreconciled, c_payment.isallocated, c_payment.isonline, c_payment.processed, c_payment.posted, c_payment.dateacct
   FROM c_payment;

ALTER TABLE rv_payment OWNER TO libertya;


-- 20110926-1009 Nueva columna para disparar callout al cargar registro existente
ALTER TABLE ad_column ADD COLUMN calloutalsoonload character(1) DEFAULT 'N';

-- Inclusion de nueva columna en vista ad_field_v
DROP VIEW ad_field_v;
CREATE OR REPLACE VIEW ad_field_v AS 
 SELECT t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, f.name, f.description, f.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, c.defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, c.ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fg.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload
   FROM ad_field f
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup fg ON f.ad_fieldgroup_id = fg.ad_fieldgroup_id
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON c.ad_val_rule_id = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;
  
-- Inclusion de nueva columna en vista ad_field_vt
DROP VIEW ad_field_vt;
CREATE OR REPLACE VIEW ad_field_vt AS 
 SELECT trl.ad_language, t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, trl.name, trl.description, trl.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, c.defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, c.ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fgt.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload
   FROM ad_field f
   JOIN ad_field_trl trl ON f.ad_field_id = trl.ad_field_id
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup_trl fgt ON f.ad_fieldgroup_id = fgt.ad_fieldgroup_id AND trl.ad_language::text = fgt.ad_language::text
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON c.ad_val_rule_id = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;

-- Soporte para jasper dinámicos
alter table ad_process add column jasperreport varchar(255);
alter table ad_process add column dynamicreport character(1) default 'N';

-- ===================================================================================
-- ============= 20110930-13:49 CONVERSION DE FUNCIONES PLJAVA A PLPGSQL =============
-- =================================================================================== 
-- ROUND.sql 
CREATE OR REPLACE FUNCTION libertya.round(numeric, numeric)
  RETURNS numeric AS
$BODY$
 BEGIN
	RETURN ROUND($1, cast($2 as integer));
 END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.round(numeric, numeric) OWNER TO libertya;-- end ROUND.sql 
-- Currency  
--------------------------------------------------- 
-- Currency\currencyRound.sql 
CREATE OR REPLACE FUNCTION libertya.currencyround(p_amount numeric, p_curto_id integer, p_costing character varying)
  RETURNS numeric AS
$BODY$
	
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 ***
 * Title:	Round amount for Traget Currency
 * Description:
 *		Round Amount using Costing or Standard Precision
 *		Returns unmodified amount if currency not found
 * Test:
 *		SELECT currencyRound(currencyConvert(100,116,100,null,null),100,null) FROM AD_System => 64.72 
 ***
 * Pasado a Libertya a partir de Adempiere 342 (360 LTS usa variables locales enteras)
 * - ids son de tipo integer, no numeric
 ************************************************************************/
	
	
DECLARE
	v_StdPrecision		NUMERIC; -- int en 360 LTS,  pero siguen siendo numeric en c_currency
	v_CostPrecision		NUMERIC; -- int en 360 LTS,  pero siguen siendo numeric en c_currency

BEGIN
	--	Nothing to convert
	IF (p_Amount IS NULL OR p_CurTo_ID IS NULL) THEN
		RETURN p_Amount;
	END IF;

	--	Ger Precision
	SELECT	MAX(StdPrecision), MAX(CostingPrecision)
	  INTO	v_StdPrecision, v_CostPrecision
	FROM	C_Currency
	  WHERE	C_Currency_ID = p_CurTo_ID;
	--	Currency Not Found
	IF (v_StdPrecision IS NULL) THEN
		RETURN p_Amount;
	END IF;

	IF (p_Costing = 'Y') THEN
		RETURN ROUND (p_Amount, v_CostPrecision);
	END IF;

	RETURN ROUND (p_Amount, v_StdPrecision);
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.currencyround(numeric, integer, character varying) OWNER TO libertya;
-- end Currency\currencyRound.sql 
-- Currency\currencyRate.sql 

CREATE OR REPLACE FUNCTION libertya.currencyrate(p_curfrom_id integer, p_curto_id integer, p_convdate timestamp with time zone, p_conversiontype_id integer, p_client_id integer, p_org_id integer)
  RETURNS numeric AS
$BODY$
	
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 ***
 * Title:	Return Conversion Rate
 * Description:
 *		from CurrencyFrom_ID to CurrencyTo_ID
 *		Returns NULL, if rate not found
 * Test
 *		SELECT currencyrate(116, 100, null, null, null, null) FROM AD_System;  => .647169
 ***
 * Pasado a Libertya a partir de Adempiere 360LTS
 * - ids son de tipo integer, no numeric
 * - SELECT .... INTO requiere modificador STRICT para disparar excecpiones
 * - se deja el manejo especial conversion entre EURO-EMU, pero hay que notar que es 
 *  esta hecho de manera ineficiente
 * - se soporta el uso de la conversion inversa tal como en Libertya; pero no exactamente
 * de la misma manera: no se usa divideRate, si no 1/multiplyRate
 * - TODO : aca hay problemas casi seguro con los tipos de timestamp; se estan mezclando
 * with time zone and without time zone
 ************************************************************************/
	
	
DECLARE
	--	Currency From variables
	cf_IsEuro		CHAR(1);
	cf_IsEMUMember		CHAR(1);
	cf_EMUEntryDate		timestamp with time zone;
	cf_EMURate		NUMERIC;
	--	Currency To variables
	ct_IsEuro		CHAR(1);
	ct_IsEMUMember		CHAR(1);
	ct_EMUEntryDate	DATE;
	ct_EMURate		NUMERIC;
	--	Triangle
	v_CurrencyFrom		INTEGER;
	v_CurrencyTo		INTEGER;
	v_CurrencyEuro		INTEGER;
	--
	v_ConvDate		timestamp with time zone := now();
	v_ConversionType_ID	INTEGER := 0;
	v_Rate			NUMERIC;
	c			RECORD;			

BEGIN
--	No Conversion
	IF (p_CurFrom_ID = p_CurTo_ID) THEN
		RETURN 1;
	END IF;
	--	Default Date Parameter
	IF (p_ConvDate IS NOT NULL) THEN
		v_ConvDate := p_ConvDate;   --  SysDate
	END IF;
    --  Default Conversion Type
	IF (p_ConversionType_ID IS NULL OR p_ConversionType_ID = 0) THEN
		BEGIN
		    SELECT C_ConversionType_ID 
		      INTO STRICT v_ConversionType_ID
		    FROM C_ConversionType 
		    WHERE IsDefault='Y'
		      AND AD_Client_ID IN (0,p_Client_ID)
		    ORDER BY AD_Client_ID DESC
		    LIMIT 1;
		EXCEPTION WHEN OTHERS THEN
		    RAISE NOTICE 'Conversion Type Not Found';
		END;
    ELSE
        	v_ConversionType_ID := p_ConversionType_ID;
	END IF;

	--	Get Currency Info
	SELECT	MAX(IsEuro), MAX(IsEMUMember), MAX(EMUEntryDate), MAX(EMURate)
	  INTO	cf_IsEuro, cf_IsEMUMember, cf_EMUEntryDate, cf_EMURate
	FROM		C_Currency
	  WHERE	C_Currency_ID = p_CurFrom_ID;
	-- Not Found
	IF (cf_IsEuro IS NULL) THEN
		RAISE NOTICE 'From Currency Not Found';
		RETURN NULL;
	END IF;
	SELECT	MAX(IsEuro), MAX(IsEMUMember), MAX(EMUEntryDate), MAX(EMURate)
	  INTO	ct_IsEuro, ct_IsEMUMember, ct_EMUEntryDate, ct_EMURate
	FROM		C_Currency
	  WHERE	C_Currency_ID = p_CurTo_ID;
	-- Not Found
	IF (ct_IsEuro IS NULL) THEN
		RAISE NOTICE 'To Currency Not Found';
		RETURN NULL;
	END IF;

	--	Fixed - From Euro to EMU
	IF (cf_IsEuro = 'Y' AND ct_IsEMUMember ='Y' AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN ct_EMURate;
	END IF;

	--	Fixed - From EMU to Euro
	IF (ct_IsEuro = 'Y' AND cf_IsEMUMember ='Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
		RETURN 1 / cf_EMURate;
	END IF;

	--	Fixed - From EMU to EMU
	IF (cf_IsEMUMember = 'Y' AND cf_IsEMUMember ='Y'
			AND v_ConvDate >= cf_EMUEntryDate AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN ct_EMURate / cf_EMURate;
	END IF;

	--	Flexible Rates
	v_CurrencyFrom := p_CurFrom_ID;
	v_CurrencyTo := p_CurTo_ID;

	-- if EMU Member involved, replace From/To Currency
	IF ((cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate)
	  OR (ct_isEMUMember = 'Y' AND v_ConvDate >= ct_EMUEntryDate)) THEN
		SELECT	MAX(C_Currency_ID)
		  INTO	v_CurrencyEuro
		FROM		C_Currency
		WHERE	IsEuro = 'Y';
		-- Conversion Rate not Found
		IF (v_CurrencyEuro IS NULL) THEN
			RAISE NOTICE 'Euro Not Found';
			RETURN NULL;
		END IF;
		IF (cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
			v_CurrencyFrom := v_CurrencyEuro;
		ELSE
			v_CurrencyTo := v_CurrencyEuro;
		END IF;
	END IF;

	--	Get Rate

	BEGIN
		FOR c IN SELECT	MultiplyRate
			FROM	C_Conversion_Rate
			WHERE	C_Currency_ID=v_CurrencyFrom AND C_Currency_ID_To=v_CurrencyTo
			  AND	C_ConversionType_ID=v_ConversionType_ID
			  AND	v_ConvDate BETWEEN ValidFrom AND ValidTo
			  AND	AD_Client_ID IN (0,p_Client_ID) AND AD_Org_ID IN (0,p_Org_ID)
			ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC
			LIMIT 1  -- Ader: mejora de performance
		LOOP
			v_Rate := c.MultiplyRate;
			EXIT;	--	only first
		END LOOP;
		IF (v_Rate IS NOT NULL) THEN
		     EXIT; -- Sale del BEGIN END
		END IF;	 
		-- ELSE v_Rate IS NULL : se busca la conversion inversa invirtiendo to-from  y diviendo a 1 por el resultado
		FOR c IN SELECT	MultiplyRate
			FROM	C_Conversion_Rate
			WHERE	C_Currency_ID=v_CurrencyTo AND C_Currency_ID_To=v_CurrencyFrom
			  AND	C_ConversionType_ID=v_ConversionType_ID
			  AND	v_ConvDate BETWEEN ValidFrom AND ValidTo
			  AND	AD_Client_ID IN (0,p_Client_ID) AND AD_Org_ID IN (0,p_Org_ID)
			ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC
			LIMIT 1  -- Ader: mejora de performance
		LOOP
			v_Rate := 1::numeric/c.MultiplyRate; -- Null en divisor genera Null
			EXIT;	--	only first
		END LOOP;
	END;
	--	Not found
	IF (v_Rate IS NULL) THEN
		RAISE NOTICE 'Conversion Rate Not Found';
		RETURN NULL;
	END IF;

	--	Currency From was EMU
	IF (cf_isEMUMember = 'Y' AND v_ConvDate >= cf_EMUEntryDate) THEN
		RETURN v_Rate / cf_EMURate;
	END IF;

	--	Currency To was EMU
	IF (ct_isEMUMember = 'Y' AND v_ConvDate >= ct_EMUEntryDate) THEN
		RETURN v_Rate * ct_EMURate;
	END IF;

	RETURN v_Rate;

EXCEPTION WHEN OTHERS THEN
	RAISE NOTICE '%', SQLERRM;
	RETURN NULL;

	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.currencyrate(integer,integer, timestamp with time zone, integer, integer, integer) OWNER TO libertya;
-- end Currency\currencyRate.sql 
-- Currency\currencyConvert.sql 

CREATE OR REPLACE FUNCTION libertya.currencyconvert(p_amount numeric, p_curfrom_id integer, p_curto_id integer, p_convdate timestamp with time zone, p_conversiontype_id integer, p_client_id integer, p_org_id integer)
  RETURNS numeric AS
$BODY$
	
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 ***
 * Title:	Convert Amount (using IDs)
 * Description:
 *		from CurrencyFrom_ID to CurrencyTo_ID
 *		Returns NULL, if conversion not found
 *		Standard Rounding
 * Test:
 *	SELECT currencyConvert(100,116,100,null,null,null,null) FROM AD_System;  => 64.72
 ***
 * Pasado a Libertya a partir de Adempiere 360LTS
 * - ids son de tipo integer, no numeric
 * - TODO: chequear el tema de incompatibilidades de timestampo with time zone y without time
 * zone
 ************************************************************************/	
	
	
DECLARE
	v_Rate				NUMERIC;

BEGIN
	--	Return Amount
		IF (p_Amount = 0 OR p_CurFrom_ID = p_CurTo_ID) THEN
			RETURN p_Amount;
		END IF;
		--	Return NULL
		IF (p_Amount IS NULL OR p_CurFrom_ID IS NULL OR p_CurTo_ID IS NULL) THEN
			RETURN NULL;
		END IF;
	
		--	Get Rate
		v_Rate := currencyRate (p_CurFrom_ID, p_CurTo_ID, p_ConvDate, p_ConversionType_ID, p_Client_ID, p_Org_ID);
		IF (v_Rate IS NULL) THEN
			RETURN NULL;
		END IF;
	
		--	Standard Precision
	RETURN currencyRound(p_Amount * v_Rate, p_CurTo_ID, null);
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.currencyconvert(numeric, integer, integer, timestamp with time zone, integer, integer, integer) OWNER TO libertya;
-- end Currency\currencyConvert.sql 
-- Currency\currencybase.sql 

CREATE OR REPLACE FUNCTION libertya.currencybase(p_amount numeric, p_curfrom_id integer, p_convdate timestamp with time zone, p_client_id integer, p_org_id integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 * 
 ***
 * Title:	Convert Amount to Base Currency of Client
 * Description:
 *		Get CurrencyTo from Client
 *		Returns NULL, if conversion not found
 *		Standard Rounding
 * Test:
 *		SELECT currencyBase(100,116,null,11,null) FROM AD_System; => 64.72
 ***
 * Pasado a Libertya a partir de Adempiere 360LTS
 * - ids son de tipo integer, no numeric
 * - TODO : tema de las zonas en los timestamp
 ************************************************************************/
DECLARE
	v_CurTo_ID	INTEGER;
BEGIN
	--	Get Currency
	SELECT	MAX(ac.C_Currency_ID)
	  INTO	v_CurTo_ID
	FROM	AD_ClientInfo ci, C_AcctSchema ac
	WHERE	ci.C_AcctSchema1_ID=ac.C_AcctSchema_ID
	  AND	ci.AD_Client_ID=p_Client_ID;
	--	Same as Currency_Conversion - if currency/rate not found - return 0
	IF (v_CurTo_ID IS NULL) THEN
		RETURN NULL;
	END IF;
	--	Same currency
	IF (p_CurFrom_ID = v_CurTo_ID) THEN
		RETURN p_Amount;
	END IF;

	RETURN currencyConvert (p_Amount, p_CurFrom_ID, v_CurTo_ID, p_ConvDate, null, p_Client_ID, p_Org_ID);
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.currencybase(numeric, integer, timestamp with time zone, integer, integer) OWNER TO libertya;
-- end Currency\currencybase.sql 
-- OpenXpertya  
--------------------------------------------------- 
-- OpenXpertya\trunc.sql 
CREATE OR REPLACE FUNCTION libertya.trunc(datetime timestamp with time zone)
  RETURNS timestamp with time zone AS
$BODY$
/*************************************************************************
TEST:
select N.now , trunc(N.now) as nowTrunc
 from (SELECT now() as now ) N

debe retornar algo como 
"2011-06-26 18:43:28.906-03" "2011-06-26 00:00:00-03"
NOTA: version PLJava anterior tiene un bug y no elimina correctamnete
la parte horaria (retornaria "2011-06-26 01:00:00-03" en el ejemplo
anterior)
*/
DECLARE
	v_datetime		timestamp with time zone; 
BEGIN
	-- a diferencia de Adempiere, libertya soporta datetime null, y en ese caso defaultea a "now"
	-- agregado
	IF (datetime IS NULL) THEN
	  v_datetime := now();
	ELSE
      v_datetime := datetime;	
	END IF;

   -- simplemente castea a Date; y en el retorno se castea implicitamente a timestamp with time zone		

	RETURN CAST(v_datetime AS DATE);

END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.trunc(timestamp with time zone) OWNER TO libertya;-- end OpenXpertya\trunc.sql 
-- OpenXpertya\daysBetween.sql 
CREATE OR REPLACE FUNCTION libertya.daysbetween(p_date1 timestamp with time zone, p_date2 timestamp with time zone)
  RETURNS integer AS
$BODY$
/*************************************************************************
NOTA: version anterior de PL/Java parece tener un error en esta funcion

select daysBetween('2011-05-24 20:36:29.046'::timestamp with time zone,
 '2011-05-24'::timestamp with time zone)
 
esta retornando -1 (deberia retorna 0).
NOTA 2: a diferencia de Adempiere, Libertya retorna un numero negativo
solo si la primer fecha no es menor que la segunda; se mantiene
la semantica actual de Libertya (invirtiendo simplemente la resta), 
pero se deja constancia de esta discrepancia)

TESTS:
select dateInvoiced,
'2008-01-03'::timestamp with time zone,
daysBetween(DateInvoiced,'2008-01-03'::timestamp with time zone) as DayPL
from c_invoice

order by dateInvoiced;

select daysBetween('2008-01-03'::timestamp with time zone,
'2009-01-03'::timestamp with time zone)
-- 366 porque 2008 es bisiesto 
 
*/

BEGIN

	-- resta en Ademeire
	--RETURN CAST(p_date1 AS DATE) - CAST(p_date2 as DATE);
	-- resta en libertya para mantener la semantica actual
	RETURN CAST(p_date2 AS DATE) - CAST(p_date1 as DATE);


END;


$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.daysbetween(timestamp with time zone, timestamp with time zone) OWNER TO libertya;
-- end OpenXpertya\daysBetween.sql 
-- OpenXpertya\addDays.sql 
CREATE OR REPLACE FUNCTION libertya.adddays(datetime timestamp with time zone, days integer)
  RETURNS timestamp with time zone AS
$BODY$
/******************************************************
-si datetime es null, es tratado como now()
-si days es null es tratado como 0
-suma (o resta) dias a datetime, previamente truncando las horas,min,seg; por lo
tanto el resultado siempre es una fecha al comienzo exacto del dia.
TEST:
select now(),addDays(now(), 5),addDays(now(),-2)

debe retornar algo como 
"2011-06-27 18:07:21.375-03", "2011-07-02 00:00:00-03","2011-06-25 00:00:00-03"

*/
DECLARE 
duration VARCHAR;
v_datetime TIMESTAMP WITH TIME ZONE := now();
v_days INTEGER := 0; 
BEGIN
    -- no en Adempiere;
    IF (datetime IS NOT NULL) THEN
		v_datetime := datetime;
	END IF;
    IF (days IS NOT NULL) THEN
		v_days := days;
	END IF;	

	-- esto en adempiere	
	--if datetime is null or days is null then
	--	return null;
	--end if;

	duration = v_days || ' day';	 

	return date_trunc('day',v_datetime) + cast(duration as interval);

END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.adddays(timestamp with time zone, integer) OWNER TO libertya;-- end OpenXpertya\addDays.sql 
-- OpenXpertya\firstOf.sql 
CREATE OR REPLACE FUNCTION libertya.firstof(datetime timestamp with time zone, xx character varying)
  RETURNS timestamp with time zone AS
$BODY$
/***********
-si datetime es NULL , se retorna null
-posibles valores de xx:
'DD' = dia (trunca al dia)
'DY' = semana (trunca al comienzo de la semana; la semana comienza en domingo no en
           en lunes; esto por compatibilidad)
'Q'  = cuatrimestre/cuarto de año (1ero de enero=1 o 1ro de abril=4 o 1ro julio=7 
				o 1ro octubre=10)
'MM' = mes (primero del mes)
otro (incluyendo null) = dia

TEST:
select now(), firstOf(now(),'DD'),firstOf(now(),'DY'),firstOf(now(),'MM'),firstOf(now(),'Q')		   
*/
DECLARE
datepart VARCHAR;
v_datetime TIMESTAMP WITH TIME ZONE;
offset INTEGER;
BEGIN

	IF (datetime IS NULL) THEN
		RETURN NULL;
	END IF;
	--datepart := xx;
	offset := 0;
	
	IF (xx IN ('DD')) THEN
	    datepart := 'day';
	ELSEIF (xx IN ('DY')) THEN
		datepart := 'week';
		offset := -1; -- para compatibilidad, para que retorne domingo, y no lunes
	ELSEIF (xx IN ('MM')) THEN
		datepart := 'month';
	ELSEIF (xx IN ('Q') )	THEN
	    datepart := 'quarter';
	ELSE -- defulat, NULL o desconocido
	    datepart := 'day';
	END IF;

	v_datetime := date_trunc(datepart, datetime); 
-- casting implicito en retorno timestamp with time zone; deberia normalizar a una sola zona horaria	
RETURN cast(v_datetime as date) + offset;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.firstof(timestamp with time zone, character varying) OWNER TO libertya;
-- end OpenXpertya\firstOf.sql 
-- OpenXpertya\charAt.sql 
CREATE OR REPLACE FUNCTION libertya.charat(character varying, integer)
  RETURNS character varying AS
/***
Esta funcion tenia un bug en PL/Java, o por lo menos no retorna
lo que se supone que deberia.
Compoartimiento actual (lo indices comienzan en 1,no en 0)
charAt('ASD',1) = 'A'
charAt('ASD',2) = 'S'
charAt('ASD',3) = 'D'
charAt('ASD',4) = ''

Comportamiento anterior
charAt('ASD',1) = 'SD'
charAt('ASD',2) = 'D'
charAt('ASD',3) = '' 
charAt('ASD',4) = <NULL>

Esta funcion es usada por vistas rv_c_invoice y rv_c_invoicetax
para deteminar el multiplicador. Como ahora el charAt es calculado
correctamente estas vistas (y sus dependientes) van a cambiar sus
resultados para ciertos tipos de docuemento: los que tienen
un docBaseType con un 'C' en el tercer caracter y estan asociados
a facturas.

*********/  
$BODY$

 BEGIN

 RETURN SUBSTR($1, $2, 1);

 END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.charat(character varying, integer) OWNER TO libertya;-- end OpenXpertya\charAt.sql 
-- OpenXpertya\nextID.sql 
CREATE OR REPLACE FUNCTION libertya.nextid(p_ad_sequence_id integer, 
p_system character varying)
  RETURNS integer AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2005 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 ***
 * Title:	Get Next ID - no Commit
 * Description: Returns the next id of the sequence.
 * Test:
 *	select * from nextid((select ad_sequence_id from ad_sequence where name = 'Test')::Integer, 'Y'::Varchar);
 * 
 ************************************************************************/
DECLARE
o_NextID INTEGER := NULL;
BEGIN
    IF (p_System = 'Y') THEN
		--RAISE NOTICE 'system';
        SELECT CurrentNextSys::Integer
            INTO o_NextID
        FROM AD_Sequence
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
        --
        UPDATE AD_Sequence
          SET CurrentNextSys = CurrentNextSys + IncrementNo
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
    ELSE
        SELECT CurrentNext::Integer
            INTO o_NextID
        FROM AD_Sequence
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
        --
        UPDATE AD_Sequence
          SET CurrentNext = CurrentNext + IncrementNo
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
    END IF;

	RETURN o_NextID;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.nextid(integer, character varying) OWNER TO libertya;
-- end OpenXpertya\nextID.sql 
-- OpenXpertya\nextBusinessDay.sql 
CREATE OR REPLACE FUNCTION libertya.nextbusinessday(p_date timestamp with time zone)
  RETURNS timestamp with time zone AS
$BODY$
/**
Basado en Adempeire 360LTS (con varias diferencias...)
Retorna la misma fecha (truncada a 0 horas) si no es ni sabado ni domingo; si es sabado o
domingo retorna la fecha del siguiente lunes

TEST:
select 
nextBusinessDay('2011-06-24'::timestamp with time zone),
nextBusinessDay('2011-06-25'::timestamp with time zone),
nextBusinessDay('2011-06-26'::timestamp with time zone),
nextBusinessDay('2011-06-27'::timestamp with time zone)
Tiene que retornar 24,27,27 y 27 de Junio de 2011 (con zona horaria default)
*/
DECLARE
	v_nextDate	timestamp with time zone;
	v_offset	integer	:= 0;
	v_Saturday	integer	:= TO_CHAR(TO_DATE('2000-01-01', 'YYYY-MM-DD'), 'D')::integer;
	v_Sunday	integer	:= (case when v_Saturday = 7 then 1 else v_Saturday + 1 end);
	v_offsetInterval interval;
--	v_isHoliday	boolean	:= true;
--	nbd C_NonBusinessDay%ROWTYPE;
BEGIN
 
    IF (p_date IS NOT NULL) THEN
		v_nextDate := trunc(p_date);
    ELSE
	    v_nextDate := trunc(now());
	END IF;

	SELECT	CASE TO_CHAR(v_nextDate,'D')::integer
					WHEN v_Saturday THEN 2
					WHEN v_Sunday THEN 1
					ELSE 0
				END INTO v_offset;
	v_offsetInterval := (v_offset || ' day')::interval;
	v_nextDate := v_nextDate + v_offsetInterval;

	RETURN v_nextDate;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.nextbusinessday(timestamp with time zone) OWNER TO libertya;
-- end OpenXpertya\nextBusinessDay.sql 
-- Payment  
--------------------------------------------------- 
-- Payment\PaymentAllocated.sql 
CREATE OR REPLACE FUNCTION libertya.paymentallocated(p_c_payment_id integer, p_c_currency_id integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 * Title:	Calculate Allocated Payment Amount in Payment Currency
 * Description:
    --
    SELECT paymentAllocated(C_Payment_ID,C_Currency_ID), PayAmt, IsAllocated
    FROM C_Payment_v 
    WHERE C_Payment_ID<1000000;
    --
    UPDATE C_Payment_v 
    SET IsAllocated=CASE WHEN paymentAllocated(C_Payment_ID, C_Currency_ID)=PayAmt THEN 'Y' ELSE 'N' END
    WHERE C_Payment_ID>=1000000;
 ****
 *-Pasado a Liberya a partir en Adempiere 360LTS
 *-ids son enteros
 *-se asume que todos los montos son no negativos
 *-no se utilza la vista C_Payment_V y no se corrige por AP/CM (de todas maneras
 * Libertya actual no estaba usando esto y la vista siempre retorna 1 y multiplicaba
 * por este monto)
 *-no se utiliza multiplicadores: retorna siempre algo en el entorno [0..PayAmt]
 *-la obtencion del si esta asociado a un cargo y la monto del pago es obtenido
 *  en una sola setencia sql
 *-se utiliza el redondeo por moneda en vez del redondeo a 2 (aunque esto
 * ultimo tiene su sentido teniendo en cuenta que liberya maneja solo 2 decimales)
 ************************************************************************/
DECLARE
	v_AllocatedAmt		NUMERIC := 0;
    	v_PayAmt        	NUMERIC;
    	r   			RECORD;
BEGIN
    --  Charge - nothing available
    SELECT 
      INTO v_PayAmt MAX(PayAmt) 
    FROM C_Payment 
    WHERE C_Payment_ID=p_C_Payment_ID AND C_Charge_ID > 0;
    
    IF (v_PayAmt IS NOT NULL) THEN
        RETURN v_PayAmt;
    END IF;
    
	--	Calculate Allocated Amount
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
			FROM	C_AllocationLine al
	          INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
			WHERE	al.C_Payment_ID = p_C_Payment_ID
          	AND   a.IsActive='Y'
	LOOP
		v_AllocatedAmt := v_AllocatedAmt
			+ currencyConvert(r.Amount, r.C_Currency_ID, 
			p_C_Currency_ID, r.DateTrx, null, r.AD_Client_ID, r.AD_Org_ID);
	END LOOP;
	--  NO en libertya:	Round to penny
	-- en vez se redondea usando la moneda
	v_AllocatedAmt := currencyRound(COALESCE(v_AllocatedAmt,0),p_c_currency_id,NULL); 
	RETURN	v_AllocatedAmt;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.paymentallocated(integer, integer) OWNER TO libertya;
-- end Payment\PaymentAllocated.sql 
-- Payment\PaymentAvailable.sql 
CREATE OR REPLACE FUNCTION libertya.paymentavailable(p_c_payment_id integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 * Title:	Calculate Available Payment Amount in Payment Currency
 * Description:
 *		similar to C_Invoice_Open
 ****
 *-Pasado a Liberya a partir en Adempiere 360LTS
 *-ids son enteros
 *-se asume que todos los montos son no negativos
 *-no se utilza la vista C_Payment_V y no se corrige por AP/CM (de todas maneras
 * Libertya actual no estaba usando esto y la vista siempre retorna 1 y multiplicaba
 * por este monto)
 *-no se utiliza multiplicadores: retorna siempre algo en el entorno [0..PayAmt]
 *-la obtencion del si esta asociado a un cargo y la monto del pago es obtenido
 *  en una sola setencia sql
 *- TODO: posiblemente refactorizar el calculo de alocacion a otra funcion
 *- no se deja el ingoreRounding de Adempiere (tal vez deberia ir...) y
 * se llama a currencyRound como en libertya (no deberia ser necesario; ya que
 * todos los montos intermedios son redondeados a la misma moneda; todos menos
 * PayAmt que se podria suponer ya esta redondeado de antemano) 
 ************************************************************************/
DECLARE
	v_Currency_ID		INTEGER;
	v_AvailableAmt		NUMERIC := 0;
   	v_IsReceipt         CHARACTER(1);
   	v_Amt               NUMERIC := 0;
   	r   			RECORD;
	v_Charge_ID INTEGER; -- no en Adempiere
	v_ConversionType_ID INTEGER; -- no en Adempiere; puede ser null (igual es dudoso este tipo de conversion, por que lo 
	--que se conveirte al moneda del pago son las lineas de alocacion, las cuales en otros calculos usan posiblemente otro tipo de conversion)

BEGIN

	BEGIN
	--	Get Currency, PayAmt,IsReceipt (not used, only for debug) and C_Charge_ID
	SELECT	C_Currency_ID, PayAmt, IsReceipt, 
			C_Charge_ID,C_ConversionType_ID
	  INTO	STRICT 
			v_Currency_ID, v_AvailableAmt, v_IsReceipt,
			v_Charge_ID,v_ConversionType_ID
	FROM	C_Payment     --NO CORREGIDO por AP/AR
	WHERE	C_Payment_ID = p_C_Payment_ID;
		EXCEPTION	--	No encontrado; posiblememte mal llamado
		WHEN OTHERS THEN
            	RAISE NOTICE 'PaymentAvailable - %', SQLERRM;
			RETURN NULL;
	END;
	
	IF (v_Charge_ID > 0 ) THEN -- mayor que cero, por lo tanto no null 
	   RETURN 0;
	END IF;
--  DBMS_OUTPUT.PUT_LINE('== C_Payment_ID=' || p_C_Payment_ID || ', PayAmt=' || v_AvailableAmt || ', Receipt=' || v_IsReceipt);

	--	Calculate Allocated Amount
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
		FROM	C_AllocationLine al
	        INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	al.C_Payment_ID = p_C_Payment_ID
          	AND   a.IsActive='Y'
	LOOP
        v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				r.DateTrx, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
	    v_AvailableAmt := v_AvailableAmt - v_Amt;
--      DBMS_OUTPUT.PUT_LINE('  Allocation=' || a.Amount || ' - Available=' || v_AvailableAmt);
	END LOOP;
	
	IF (v_AvailableAmt < 0) THEN
		RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ;
		v_AvailableAmt := 0;
	END IF;
	
	-- siguiente NO en Libertya
	--	Ignore Rounding; 
	--IF (v_AvailableAmt BETWEEN -0.00999 AND 0.00999) THEN
	--	v_AvailableAmt := 0;
	--END IF;
	--	Round to penny
	--v_AvailableAmt := ROUND(COALESCE(v_AvailableAmt,0), 2);
	
	-- redondeo de moneda
	v_AvailableAmt :=  currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
	RETURN	v_AvailableAmt;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.paymentavailable(integer) OWNER TO libertya;
-- end Payment\PaymentAvailable.sql 
-- CashLine  
--------------------------------------------------- 
-- CashLine\cashLineAvailable.sql 
CREATE OR REPLACE FUNCTION libertya.cashlineavailable(p_c_cashline_id integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
-Retorna NULL si parametro es null o si la linea no existe
-Retorna la cantidad disponible de la linea para alocacion futuras usando el mismo signo 
 que la linea, esto es, si C_CashLine.Amt <0 , se retorna 0 o un numero
 negativo; si C_CashLine.amt >0 , se retrona cero o un numero positivo.
-la cantidad disponible inicial de una linea de caja es C_CashLine.Amt
 (esto es, no se tiene en cuenta ni C_CashLine.DiscountAmt ni 
 C_CashLine.WriteoffAmt) 
-asume que las alocaciones son no negativas y solo se consideran aquellas
 lineas de alocacion que pertenecen a una cabecera de alocacion (C_AllocationHdr)
 activa (esta es la unica condicion que se aplica)
- se considera como monto de alocacion con respecto a la linea de caja 
  a C_AllocationLine.Amount (esto es, no se tiene en cuenta C_AllocationLine.WriteOff ni
  C_AllocationLine.Discount)
  
TEST: 
-- montos de lienas, monto disponible, y alocaciones relacionadas cada una de las lineas de caja
-- Availabe DEBE ser cero o tener el mismo signo que Amount,
-- si se usa una sola moneda, entonces 
-- (suma de AmountAllocatedInAlocLine en AH activas) + ABS(Available) debe ser iugal a  ABS(Amoumt) 
select cl.c_cashLine_id,cl.amount, 
cashLineAvailable(cl.c_cashLine_id) as Available
,al.c_allocationLine_id ,
al.amount as AmountAllocatedInAlocLine,
cl.c_currency_id as currencyCashLine,
ah.c_currency_id as currencyAlloc,
ah.isActive as AHActive
from 
c_cashLine cl left join c_allocationLine al on
  (al.c_cashLine_id = cl.c_cashLine_id)
left join 
C_AllocationHDR ah on (ah.C_allocationHdr_id = al.C_allocationHdr_id)

order by cl.c_cashLine_id;
  
************************************************************************/
DECLARE
	v_Currency_ID		INTEGER;
	v_Amt               NUMERIC;
   	r   			RECORD;
	v_ConversionType_ID INTEGER := 0; -- actuamente, tal como en PL/java se usa siempre 0, no se toma desde cashLine
	v_allocation NUMERIC;
	v_allocatedAmt NUMERIC;	-- candida alocada total convertida a la moneda de la linea 
	v_AvailableAmt		NUMERIC := 0;
 
BEGIN
	IF (p_C_Cashline_id IS NULL OR p_C_Cashline_id = 0) THEN
		RETURN NULL;
	END IF;
	
	--	Get Currency and Amount
	SELECT	C_Currency_ID, Amount
		INTO v_Currency_ID, v_Amt 
	FROM	C_CashLine    
	WHERE	C_CashLine_ID  = p_C_Cashline_id;

	IF NOT FOUND THEN
	  RETURN NULL;
	END IF;
	
	-- Calculate Allocated Amount
	-- input: p_C_Cashline_id,v_Currency_ID,v_ConversionType_ID
	--output: v_allocatedAmt
	v_allocatedAmt := 0.00;
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
		FROM	C_AllocationLine al
	        INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	al.C_CashLine_ID = p_C_Cashline_id
          	AND   a.IsActive='Y'
	LOOP
        v_allocation := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				r.DateTrx, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
	    v_allocatedAmt := v_allocatedAmt + v_allocation;
	END LOOP;

	-- esto supone que las alocaciones son siempre no negativas; si esto no pasa, se van a retornar valores que no van a tener sentido
	v_AvailableAmt := ABS(v_Amt) - v_allocatedAmt;
	-- v_AvailableAmt aca DEBE ser NO Negativo si admeas, las suma de las alocaciones nunca superan el monto de la linea
	-- de cualquiera manera, por "seguridad", si el valor es negativo, se corrige a cero
    IF (v_AvailableAmt < 0) THEN
		RAISE NOTICE 'CashLine Available negative, correcting to zero - %',v_AvailableAmt ;
		v_AvailableAmt := 0.00;
    END IF;	
	--  el resultado debe ser 0 o de lo contrario tener el mismo signo que la linea; 
	IF (v_Amt < 0) THEN
		v_AvailableAmt := v_AvailableAmt * -1::numeric;
	END IF; 
	-- redondeo de moneda
	v_AvailableAmt :=  currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
	RETURN	v_AvailableAmt;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.cashlineavailable(integer) OWNER TO libertya;
-- end CashLine\cashLineAvailable.sql 
-- PaymentTerm  
--------------------------------------------------- 
-- PaymentTerm\lastDayOfMonth.sql 
CREATE OR REPLACE FUNCTION libertya.lastDayOfMonth(datetime timestamp with time zone)
  RETURNS integer AS
$BODY$
/***********
Retonar el numero del ultimo dia del mes (o lo que es lo mismo, la cantidad
de dias en el mes) al que pertenece datetime; esto es rertonra 28, 29 (febrero
anio bisiesto),30 o 31

-si dateTime es null -> null
TEst:

select lastDayOfMonth('2008-02-15'::timestamp with time zone)
--> 29 (2008 es bisiesto)
select lastDayOfMonth('2007-02-15'::timestamp with time zone)
--> 28 (2007 es no es bisisieto)
select lastDayOfMonth('2008-01-15'::timestamp with time zone)
--> 31 (enero tiene 31)	
select lastDayOfMonth('2008-04-15'::timestamp with time zone)
--> 30 abril tiene 30	   
*/
BEGIN

 RETURN 
 extract (day from 
		(
			(date_trunc('month', datetime) + '1 month'::interval ) - '1 day'::interval)
		)::integer;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.lastDayOfMonth(timestamp with time zone) OWNER TO libertya;
-- end PaymentTerm\lastDayOfMonth.sql 
-- PaymentTerm\calculateDateDue.sql 
CREATE OR REPLACE FUNCTION libertya.calculateDateDue
(docDate timestamp with time zone, fixMonthDay integer,fixMonthOffset integer,
fixMonthCutoff integer)
  RETURNS timestamp with time zone AS
$BODY$
/**
Params: si nulls, son cosiderados como 0; NO USAR de esta maneraa:
fixMonthOffset: 0 = "del mes corriente", 1= "del siuiente mes" ,etc; la diferencia
 final entre meses netos se determina entre fixMonthCutoff y docDate)
fixMonthDay : 31 o mas: siempre a en el ultimo dia del mes (dependiente del mes
 puede ser 28,29,30,31); 0: INCORRECTO (se considera a partir de 1; por ej 1="primero
 del mes"); fija dia final de la fecha (por ej, si 7; siemrpe el dia 7); el mes
 final es determinado en conjunto entre fixMonthOffset y fixMonthCuttOff
fixMonthCutoff: debe ser mayor que 0. Dia del mes (no inclusive) a partir del cual se considera
que hay que agregar otro mes neto; por ej si toma valor 20 y la docDate tiene fecha
21 de enero, el mes final no va a ser calculado a partir de fixMonthOffset si no
de fixMonthOffset + 1 . Puede tomar un valor mayor o iugal 31 (se va a truncar 
al ultimo dia del mes de docDate si lo excede); no es un error, pero en ese
caso nunca va a afectar a offset de mes final: fixMonthCutoff => 31,
fixMonthOffset = 1,  fixMonthDay ; docDate = X de enero -> 7 de febrero,
sin importar X

NOTA: si los parametros de entrada generar una fecha final menor a docDate, se
retorna esta ultima (esto es, el resultado nunca es menor a docDate).
*/
DECLARE
v_fixMonthDay integer := COALESCE(fixMonthDay,0::integer);
v_fixMonthOffset integer := COALESCE(fixMonthOffset,0::integer);
v_fixMonthCutoff integer := COALESCE(fixMonthCutoff,0::integer);
v_docDate timestamp with time zone;

calDueDate	 timestamp with time zone;
maxDayCut		integer; -- 28,29,30 o 31, mayor dia en el mes  al que pertenece de docDate
maxDay          integer; -- 28,29,30 o 31, mayor ida en el mes perteneciente al mes final
BEGIN
	IF (docDate IS NULL) THEN
		RETURN NULL;
	END IF;
	v_docDate := TRUNC(docDate);
	
	calDueDate := v_docDate; 
	-- dia maximo de mes de la fecha de documento; en el ejemplo docDate es 15 de diciembre, que tiene 31
	-- por lo tanto, maxDayCut seria 31 (si hubiese sido 12 de febrero de 2010, maxDayCut seria 28)
	maxDayCut := lastDayOfMonth(v_docDate);

	-- v_FixMonthCutoff no puede superar el maximo dia del mes
	IF (v_FixMonthCutoff > maxDayCut) THEN -- v_FixMonthCutoff  29,30 o 31
		v_FixMonthCutoff := maxDayCut;
	END IF;
	
	-- aplica primero v_FixMonthCutoff:
	--docDate = 15 diciembre, v_FixMonthCutoff = 20  -> calDueDate = 20 diciembre
	--docDate = 21 diciembre, v_FixMonthCutoff = 20  -> calDueDate = 20 diciembre 
	calDueDate := date_trunc('month',calDueDate) + 
			(((v_FixMonthCutoff-1)|| ' days')::interval);

	
	-- si la fecha luego de aplicar fixMonthCutoff es menor a docDate (puede darse en el else anteior), se considera
	--  un mes mas
	-- docDate = 15 diciembre; v_FixMonthCutoff = 20; v_fixMonthOffset = 1 -> v_fixMonthOffset = 1 (iugal)
	-- docDate = 21 diciembre; v_FixMonthCutoff = 20; v_fixMonthOffset = 1 -> v_fixMonthOffset = 2
	IF (v_docDate > calDueDate) THEN
		v_fixMonthOffset := v_fixMonthOffset + 1;
		--raise notice 'incrementando FixMonthOffset: %' , v_FixMonthOffset;
	END IF;
	-- docDate = 15 diciembre; v_FixMonthCutoff = 20; v_fixMonthOffset = 1 ->  calDueDate = 20 enero
	-- docDate = 21 diciembre; v_FixMonthCutoff = 20; v_fixMonthOffset = 2 -> calDueDate = 20 febrero
	calDueDate := calDueDate + (v_fixMonthOffset || ' month')::interval;
	
	-- finalmente v_fixMonthDay toma prioridad sobre el dia del mes final, pero previamente hay que tratar casos especiales.
	-- Igual, es muy raro que esto pase, en particular la segunda condicion (que dicho sea de paso no tiene mucho sentido), salvo
	-- que se tenga una configuracion medio rara (por ej, a pagar el 31 del mes siguiente)
	maxDay := lastDayOfMonth(calDueDate);
	IF (  
		(v_fixMonthDay > maxDay)    -- por ej: 32 -> 28 
		 OR (v_fixMonthDay >= 30 AND MaxDay > v_fixMonthDay) --	30 -> 31
	   ) THEN  	
	 v_fixMonthDay := maxDay;		   
	END IF;
	--aplica la v_fixMonthDay
	-- docDate = 15 diciembre; v_FixMonthCutoff = 20; v_fixMonthOffset = 1;v_fixMonthDay = 7 ->  calDueDate = 7 enero
	-- docDate = 21 diciembre; v_FixMonthCutoff = 20; v_fixMonthOffset = 2 ;v_fixMonthDay = 7-> calDueDate = 7 febrero
	
	calDueDate := date_trunc('month',calDueDate) + 
			(((v_fixMonthDay-1)|| ' days')::interval);
			
	-- finalmente, esto es nuevo: la fecha final , no deberia ser inferior a la del documento, salvo que se hayan especifico
    -- incorrectamente los parametros (por ej v_fixMonthDay = 0); en tal caso defaulte a docDate
    IF 	(v_docDate > calDueDate) THEN
	    calDueDate := v_docDate;
		--raise notice 'docDate %  mayor que calDueDate % , revirtiendo a docDate ' , 
		--	v_docDate,calDueDate;	
	END IF ;
	
	RETURN calDueDate;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.calculateDateDue(timestamp with time zone,integer,integer,integer) OWNER TO libertya;
-- end PaymentTerm\calculateDateDue.sql 
-- PaymentTerm\paymentTermDueDays.sql 
CREATE OR REPLACE FUNCTION libertya.paymenttermduedays(paymentterm_id integer, 
docdate timestamp with time zone, paydate timestamp with time zone)
  RETURNS integer AS
$BODY$
/*************************************************************************
 * Nota: version en Adempeire parece tener comentarios incorrectos (el tema de los
 positivos, negativos).
 Input:
 paymentterm_id : id del termino de pago (si 0 o null, se retonrna 0)
 DocDate: fecha de base de documento (si null,  se retorna 0)
 paydate: dia de pago; si null, se toma now()
 Output:
 Retorna la cantidad de dias vencidos del termino de pago a la fecha payDate
 -Postivo: el termino de pago se vencio X dias antes que payDate 
		(ESTA VENCIDO con respecto a payDate)
 -Cero : el termino de pago se vence exactamente en payDate
 -Negativo: el termino de pago se vence abs(X) dias despues de PayDate
		(NO ESTA VENCIDO con respecto a payDate)
 
TEST:
select i.dateInvoiced, 

p.isDueFixed, p.netDays,p.FixMonthDay,p.FixMonthOffset,p.FixMonthCutoff,
paymenttermduedays(i.c_paymentterm_id,i.dateInvoiced,'2008-01-03'::timestamp with time zone)
as diasVencido
,
'2008-01-03'::timestamp with time zone -
(paymenttermduedays(i.c_paymentterm_id,i.dateInvoiced,'2008-01-03'::timestamp with time zone)
 || ' day')::interval as fechaDeVencimientoReal
from 
c_invoice i left join c_paymentTerm p on (i.c_paymentTerm_id = p.c_paymentTerm_id)

 ************************************************************************/
DECLARE
 	Days			integer := 0;
	DueDate			timestamp with time zone; -- fecha de vencimiento calculado a partir de termino de pago y docDate
	v_PayDate		timestamp with time zone; -- fecha a partir de la cual se compara los dias aduedados (basado en payDate si no null)
	p   			RECORD;
BEGIN

    IF PaymentTerm_ID = 0 OR PaymentTerm_ID IS NULL OR DocDate IS NULL THEN
	    RETURN 0;
	END IF;

	IF PayDate IS NULL THEN
	    v_PayDate := TRUNC(now());
	ELSE
        v_PayDate := TRUNC(PayDate);	
	END IF;

	-- no STRICT; vamos a usar FOUND, ya que las bloques con excpeciones son costosas
	SELECT * INTO 	p 
		FROM C_PaymentTerm 
		WHERE C_PaymentTerm_ID = PaymentTerm_ID;
	-- no se encontro el paymentTerm	
	IF NOT FOUND THEN -- no deberia pasar
		--RAISE NOTICE 'PaymentTerm desconocido - %', PaymentTerm_ID;
		RETURN 0; -- igual que PL/Java
    END IF;

	IF (p.IsDueFixed = 'Y') THEN
		DueDate	:= calculateDateDue(DocDate, 
				p.FixMonthDay::integer,
				P.FixMonthOffset::integer,
				P.FixMonthCutoff::integer);
	ELSE
		DueDate := TRUNC(DocDate) + 
				(COALESCE(p.NetDays::integer,0) || ' day')::interval;
	END IF;
	
	IF DueDate IS NULL THEN -- no deberia pasar ya que ya se trato en NOT FOUND; igual, por las dudas
	  --RAISE NOTICE 'DueDate null, retronando 0';
	  RETURN 0;
	END IF;

	--PL/java return OpenXpertya.getDaysBetween( DueDate,PayDate )
	Days := daysBetween(DueDate,v_PayDate);

    -- en Adempeire (deberia ser equivalente)	
	--Days := EXTRACT(day from (TRUNC(v_PayDate) - DueDate));
	RETURN Days;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.paymenttermduedays(integer, timestamp with time zone, timestamp with time zone) OWNER TO libertya;
-- end PaymentTerm\paymentTermDueDays.sql 
-- PaymentTerm\paymentTermDueDate.sql 
CREATE OR REPLACE FUNCTION libertya.paymenttermduedate(paymentterm_id integer, 
docdate timestamp with time zone)
  RETURNS timestamp with time zone AS
$BODY$
/*************************************************************************
 Calcula la fecha de vencimiento de un termino de pago dada
 una fecha de documento base.
 Casos especiales (reflejan mal uso de la funcion)
 Si PaymentTerm_ID es 0 o null -> null
 Si DocDate es null -> null
 Si no, si no se encuentra el C_PaymentenTerm asociado -> docDate (como en Pl/java)
 ************************************************************************/
DECLARE
	DueDate			timestamp with time zone := TRUNC(DocDate);
	p   			RECORD;
BEGIN
	IF PaymentTerm_ID = 0 OR PaymentTerm_ID IS NULL OR DocDate IS NULL THEN
	    RETURN NULL; -- posiblmente, para que sea equivalente con paymentTermDueDays aca habria que retornr dueDate
	END IF;
	
	
	-- no STRICT; vamos a usar FOUND, ya que las bloques con excpeciones son costosas
	SELECT * INTO 	p 
		FROM C_PaymentTerm 
		WHERE C_PaymentTerm_ID = PaymentTerm_ID;
	-- no se encontro el paymentTerm	
	IF NOT FOUND THEN -- no deberia pasar
		--RAISE NOTICE 'PaymentTerm desconocido - %', PaymentTerm_ID;
		RETURN DueDate; -- igual que PL/Java
    END IF;
	
	IF (p.IsDueFixed = 'Y') THEN
		DueDate	:= calculateDateDue(DocDate, 
				p.FixMonthDay::integer,
				P.FixMonthOffset::integer,
				P.FixMonthCutoff::integer);
	ELSE
		DueDate := TRUNC(DocDate) + 
				(COALESCE(p.NetDays::integer,0) || ' day')::interval;
	END IF;

	RETURN DueDate;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.paymenttermduedate(integer, timestamp with time zone) OWNER TO libertya;
-- end PaymentTerm\paymentTermDueDate.sql 
-- PaymentTerm\paymentTermDiscount.sql 
CREATE OR REPLACE FUNCTION libertya.paymenttermdiscount(amount numeric, 
paymentterm_id integer, docdate timestamp with time zone, paydate timestamp with time zone)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 * Title:	Calculate Discount
 * Description:
 *	Calculate the allowable Discount Amount of the Payment Term
 *
 *	Test:	SELECT paymenttermDiscount(110, 103, 106, now(), now()) FROM TEST; => 2.20
 ******
 Trasladado a Libertya a partir de version Adempiere 360 LTS
 Notas:
 -la funcion invocada nextBusinessDay(timestamp, cient_id) NO existe
  en Libertya; solo existe nextBusinessDay(timestamp); esta utlima no tiene
  en cuenta la tabla C_NonBussinesDay (feriados no "tradicionales"); 
  por ahora, se deja tal como esta  en Libertya, pero se deja registrada la diferencia
 - Adempiere casi seguro tiene un bug, ya que suma directamete algo de tipo
 timestamp a algo numeric; estas sumas solo son validas para tipos datos date
 y integer:
select now() + (1::numeric) -> Error 
select now()::date + (1::numeric) -> Error
select now()::date + (1::numeric)::integer -> ok; proximo dia

-TODO: hacer una mejor descripcion del funcionamiento; por lo pronto 
-para que tengan sentido los dos descuentos DiscountDays < DiscountDays2; si
 DiscountDays2>DiscountDays, es como si DiscountDay2 no existiera (nunca
 se va a aplicar Discount2)
-GraceDays no parece tener mucho sentido; en realidad, este campo parece
 haber sido creado para "dunning letters" (avisos de deuda...).  
-tanto DiscountDay como DiscountDay2 tienen sentido que sean postivos como negativos
asi como Discount y Discount2 los, pero en general:
-DiscountDay(2) negativo: se aplica si el pago se realiza DiscountDay dias antes de lo
 "pactado" (lo pactado es representado por docdate), por lo tanto
 tiene sentido, que este caso Discount que represente un descuento real 
 (supongo que positivo, hay que ver el uso que se le da a este resultado)
-DiscountDay(2) positivo: se splica si el pago se realiza antes de DiscountDay dias
 despues de lo pactado (se atraso, pero no tanto). De nuevo SOLO tiene sentido 
 un valor de Discount que represente un descuento real. En cualquiera de los casos
 (dias positivos como negativos), el pago es esta realizando antes de determinada 
 fecha, por lo tanto no tiene sentido un valor para Discount que represente un recargo
 ("si pago hoy me cobran un recargo 10, si pago mañana no...")

 De todas maneras, es posible modelar un recargo por pago "tardio", pero 
solo uno y forzando DiscountDay2. La idea es: si se paga despues de DiscountDay
se aplica el recargo de DiscountDay2; pera para que esto ultmo
funcione en general, basta con que por ej
DiscountDay = 10 (descuento por ej 0)
DiscountDay2 = "infinito" (digamos 10000)
En este caso si paga antes de pasado 10 dias de lo pactado se aplica "un descuento"
de cero (ni descunto ni recargo); si no (pago despues de los 10 dias) se aplica el 
Discount2 "SIEMPRE" ya que DicountDay2 tiene un numero muy grande (el dia de pago
dificilmente sea 10000 dias despues de lo pactado....). Solo en este caso
tiene sentido representar un recargo, y en particular el recargo va a estar
siempre representado por Discount2 (supongo que por un valor negativo).

TEST:

select i.dateInvoiced, i.grandTotal, --usado amount en este caso
paymentTermDueDate(p.c_paymentTerm_id, i.dateInvoiced) as fechaPactada,
-- usados por paymentTermDiscount
p.DiscountDays, 
p.Discount,
p.DiscountDays2,
p.Discount2,
p.IsNextBusinessDay,
p.GraceDays,
paymentTermDiscount(
i.grandTotal, -- amount
p.c_paymentTerm_ID,
paymentTermDueDate(p.c_paymentTerm_ID, i.dateInvoiced), -- "docDate": fecha pactada de pago dada 
			-- por el mismo termino de pago, NO necesariamente dateInvoiced
now() -- cual seria el descuento si lo pagaria hoy?
) as Descuento , -- un porcentaje de amount (i.granTotal en este caso)

p.Ad_client_ID,-- no usado actualmente, posiblemente si en el futuro
-- "informativos": algunos de los restantes son usados por  paymenttermduedate
p.*

from 
C_Invoice i inner join C_PaymentTerm p 
on (i.C_PaymentTerm_ID = p.C_PaymentTerm_ID)

 ************************************************************************/

DECLARE
	DiscountCalc		NUMERIC := 0; -- necesario renombrarlo para que no haya problemas con el select explicito
	Discount1Date		timestamp with time zone;
	Discount2Date		timestamp with time zone;
	p   			RECORD;
BEGIN
	--	No Data - No Discount
	IF (Amount IS NULL OR PaymentTerm_ID IS NULL OR PaymentTerm_ID = 0 
		OR DocDate IS NULL) THEN
		RETURN 0;  
	END IF;

	IF (Amount = 0 ) THEN
		RETURN 0;
	END IF; 
	-- cambiada la condicion del select para lograr el cast a integer; date + numeric -> error
	FOR p IN 
		SELECT	DiscountDays::integer as DiscountDays, -- para poder sumar a las fechas
				DiscountDays2::integer as DiscountDays2,
				GraceDays::integer as GraceDays,
				Discount,  -- es correcto dejarlo en numeric
				Discount2, -- idem anterior
				IsNextBusinessDay,
				AD_Client_ID::integer -- no se usa, se deja por si en el futuro se mejora nextBussinesDay
		FROM	C_PaymentTerm
		WHERE	C_PaymentTerm_ID = PaymentTerm_ID
	LOOP	--	for convineance only
		Discount1Date := TRUNC(DocDate::date + p.DiscountDays + p.GraceDays);
		Discount2Date := TRUNC(DocDate::date + p.DiscountDays2 + p.GraceDays);

		--	Next Business Day
		IF (p.IsNextBusinessDay='Y') THEN
			-- siguiente en Adempiere; ad_client_id para calcular las dios no laboralbles, no solo sabados y domingos
			--Discount1Date := nextBusinessDay(Discount1Date, p.AD_Client_ID);
			--Discount2Date := nextBusinessDay(Discount2Date, p.AD_Client_ID);
			-- en Libertya no se soporta el segundo parametro por ahora
			Discount1Date := nextBusinessDay(Discount1Date);
			Discount2Date := nextBusinessDay(Discount2Date);
		END IF;

		--	Discount 1 : esto deberia ser equivalente a IF (!PayDate.after( Discount1Date )), salvo que mcuho mas legible
		IF (Discount1Date >= TRUNC(PayDate)) THEN
			DiscountCalc := Amount * p.Discount / 100;
			RAISE NOTICE 'Aplicando Desc1 % para PayTerm % ',p.Discount, PaymentTerm_ID;
		--	Discount 2 : IF (!PayDate.after( Discount2Date ) ) : idem
		ELSIF (Discount2Date >= TRUNC(PayDate)) THEN
			DiscountCalc := Amount * p.Discount2 / 100;
			RAISE NOTICE 'Aplicando Desc2 % para PayTerm % ',p.Discount2, PaymentTerm_ID;
		END IF;	
	END LOOP;
	--
    RETURN ROUND(COALESCE(DiscountCalc,0), 2);	--	fixed rounding
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.paymenttermdiscount(numeric, integer, timestamp with time zone, timestamp with time zone) OWNER TO libertya;
-- end PaymentTerm\paymentTermDiscount.sql 
-- Invoice  
--------------------------------------------------- 
-- Invoice\getAllocatedAmt.sql 
CREATE OR REPLACE FUNCTION libertya.getallocatedamt(p_c_invoice_id integer, 
p_c_currency_id integer, p_c_conversionType_ID integer,
p_multiplierap integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * PL/Java:getAllocatedAmt( int C_Invoice_ID,int C_Currency_ID,
 * int C_ConversionType_ID,BigDecimal MultiplierAP ):
 * Funcion "privada"; usar invovicePaid o invoiceOpen
 * Title:	Calculate Paid/Allocated amount in Currency
 * Desc: suma todas las lineas de alocacion asociadas a la factura
 * (pertenecientes a una cabecera de alocacion activa) de manera
 * directa (pago a la factura,C_AllocationLine.C_Invoice_ID) o 
 * indirecta (se uso la factura para pagar otra (C_AllocationLine.C_Invoice_Credit_ID).
 * Los montos de alocación son convertidos a p_c_currency_id, y la sumatoria
 * final es multiplicado por p_multiplierap (tipicamente, usar 1); no se hace un 
 * redondeo final por moneda.
 * En caso de alacacion directa el monto considerado es:
 * ar.Amount + ar.DisCountAmt + ar.WriteOffAmt
 * En caso de alocacion indirecta el monto considerado es:
 * ar.Amount
 * Siendo ar la linea de alocacion asociada 
 * Test:
    SELECT C_Invoice_ID, IsPaid, IsSOTrx, GrandTotal, 
    getAllocatedAmt (C_Invoice_ID, C_Currency_ID, C_conversionType_ID , 1)
    FROM C_Invoice;
 *	
 ************************************************************************/
DECLARE
	v_MultiplierAP		NUMERIC := 1;
	v_PaidAmt			NUMERIC := 0;
	v_ConversionType_ID INTEGER := p_c_conversionType_ID;
	v_Currency_ID       INTEGER := p_c_currency_id;
	v_Temp     NUMERIC;
	ar			RECORD;

BEGIN
	--	Default
	IF (p_MultiplierAP IS NOT NULL) THEN
		v_MultiplierAP := p_MultiplierAP::numeric;
	END IF;
	--	Calculate Allocated Amount
	-- INPUTS:
	-- p_C_Invoice_ID: el id de la factura, para econtrar la lineas de alocacion asociadas directamente o como pago (via C_Invoice_Credit_ID) ; 
	--v_Currency_ID :la moneda a convertir los montos de las alocaciones(que va  a ser el mismo que la factura)
	--v_ConversionType_ID: el tipo de conversión de moneda, puede ser null (tambien el mismo especificado en la factura)
 	--OUTPUTS:
	--v_PaidAmt : la cantidad alocada para la factura, convertida v_Currency_ID
	-- Basicamente : es la suma de los montos de las lineas asociadas a la factura  convertidos previamente
	-- a la moneda de la factura (hay un pequeño detalle para las entradas en las que la factura es usada como pago....)
	FOR ar IN 
		SELECT	a.AD_Client_ID, a.AD_Org_ID,
		al.Amount, al.DiscountAmt, al.WriteOffAmt,
		a.C_Currency_ID, a.DateTrx , al.C_Invoice_Credit_ID
		FROM	C_AllocationLine al
		INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	(al.C_Invoice_ID = p_C_Invoice_ID OR 
				al.C_Invoice_Credit_ID = p_C_Invoice_ID ) -- condicion no en Adempiere
          	AND   a.IsActive='Y'
	LOOP
	    -- Agregado, para facturas como pago
		IF (p_C_Invoice_ID = ar.C_Invoice_Credit_ID) THEN
		   v_Temp := ar.Amount;
		ELSE
		   v_Temp := ar.Amount + ar.DisCountAmt + ar.WriteOffAmt;
		END IF;
		-- Se asume que este v_Temp es no negativo
		v_PaidAmt := v_PaidAmt
        -- Allocation
			+ currencyConvert(v_Temp,
				ar.C_Currency_ID, v_Currency_ID, ar.DateTrx, v_ConversionType_ID, 
				ar.AD_Client_ID, ar.AD_Org_ID);
      	--RAISE NOTICE ' C_Invoice_ID=% , PaidAmt=% , Allocation= % ',p_C_Invoice_ID, v_PaidAmt, v_Temp;
	END LOOP;
	RETURN	v_PaidAmt * v_MultiplierAP;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.getallocatedamt(integer, integer, integer, integer) OWNER TO libertya;
-- end Invoice\getAllocatedAmt.sql 
-- Invoice\invoicePaid.sql 
CREATE OR REPLACE FUNCTION libertya.invoicepaid(p_c_invoice_id integer, 
p_c_currency_id integer, 
p_multiplierap integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 ***
 * Title:	Calculate Paid/Allocated amount in Currency
 * Description:
 *	Add up total amount paid for for C_Invoice_ID.
 *  Split Payments are ignored.
 *  all allocation amounts  converted to invoice C_Currency_ID
 *	round it to the nearest cent
 *	and adjust for CreditMemos by using C_Invoice_v
 *  and for Payments with the multiplierAP (-1, 1)
 *
 *
 * Test:
    SELECT C_Invoice_ID, IsPaid, IsSOTrx, GrandTotal, 
    invoicePaid (C_Invoice_ID, C_Currency_ID, 1)
    FROM C_Invoice;
 *	
 ************************************************************************/
DECLARE
	v_MultiplierAP		INTEGER := 1;
	v_PaymentAmt		NUMERIC := 0;
BEGIN
    IF (p_c_invoice_id IS NULL OR p_c_currency_id IS NULL) THEN
		RETURN NULL;
	END IF;
	--	Default
	IF (p_MultiplierAP IS NOT NULL) THEN
		v_MultiplierAP := p_MultiplierAP;
	END IF;
	--	Calculate Allocated Amount
	v_PaymentAmt := getAllocatedAmt(p_C_Invoice_ID,p_c_currency_id,0,v_MultiplierAP);

	v_PaymentAmt := currencyRound(v_PaymentAmt, p_c_currency_id, NULL);
	RETURN	v_PaymentAmt;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.invoicepaid(integer, integer, integer) OWNER TO libertya;
-- end Invoice\invoicePaid.sql 
-- Invoice\invoiceOpen-nueva.sql 
CREATE OR REPLACE FUNCTION libertya.invoiceopen(p_c_invoice_id integer, p_c_invoicepayschedule_id integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 ***
 * Title:	Calculate Open Item Amount in Invoice Currency
 * Description:
 *	Add up total amount open for C_Invoice_ID if no split payment.
 *  Grand Total minus Sum of Allocations in Invoice Currency
 *
 *  For Split Payments:
 *  Allocate Payments starting from first schedule.
 *  Cannot be used for IsPaid as mutating
 *
 * Test:
 * 	SELECT C_InvoicePaySchedule_ID, DueAmt FROM C_InvoicePaySchedule WHERE C_Invoice_ID=109 ORDER BY DueDate;
 * 	SELECT invoiceOpen (109, null) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 11) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 102) FROM AD_System;
 * 	SELECT invoiceOpen (109, 103) FROM AD_System;
 ***
 * Pasado a Libertya a partir de Adempiere 360LTS
 * - ids son de tipo integer, no numeric
 * - TODO : tema de las zonas en los timestamp
 * - Excepciones en SELECT INTO requieren modificador STRICT bajo PostGreSQL o usar
 * NOT FOUND
 * - Por ahora, el "ignore rounding" se hace como en libertya (-0.01,0.01),
 * en vez de usar la precisión de la moneda
 * - Se toma el tipo de conversion de la factura, auqneu esto es dudosamente correcto
 * ya que otras funciones , en particular currencyBase nunca tiene en cuenta
 * este valor
 * - Como en Libertya se tiene en cuenta tambien C_Invoice_Credit_ID para calcular
 * la cantidad alocada a una factura (aunque esto es medio dudoso....)
 * - No se soporta la fecha como 3er parametro (en realidad, tampoco se esta
 * usando actualmente, y se deberia poder resolver de otra manera)
 * - Libertya parece tener un bug al filtrar por C_InvoicePaySchedule_ID al calcular
 * el granTotal (el granTotal SIEMPRE es el total de la factura, tomada directamente
 * de C_Invoice.GranTotal o a partir de la suma de los DueAmt en C_InvoicePaySchedule);
 * se usa la sentencia como esta en Adempeire (esto es, solo se filtra por C_Invoice_ID)
 * - Nuevo enfoque: NO se usa ni la vista C_Invoice_V ni multiplicadores
 * se asume todo positivo...
 * - El resultado SIEMPRE deberia ser positivo y en el intervalo [0..GrandTotal]
 * - 03 julio: se pasa a usar getAllocatedAmt para hacer esta funcion consistente
 * con invoicePaid
 * - 03 julio: se pasa de usar STRICT a NOT FOUND; es mas eficiente
 ************************************************************************/
DECLARE
	v_Currency_ID		INTEGER;
	v_TotalOpenAmt  	NUMERIC := 0;
	v_PaidAmt  	        NUMERIC := 0;
	v_Remaining	        NUMERIC := 0;
   	v_Precision            	NUMERIC := 0;
   	v_Min            	NUMERIC := 0.01; -- en Adempiere inferido desde Currency
   	s			RECORD;
	v_ConversionType_ID INTEGER; -- NO en Adempiere

BEGIN
	--	Get Currency, GranTotal, MulitplerAP , MutiplierCM, ConversionType, and precision 
	SELECT	C_Currency_ID, GrandTotal, C_ConversionType_ID,
	(SELECT StdPrecision FROM C_Currency C WHERE C.C_Currency_ID = I.C_Currency_ID)
		AS StdPrecision 
	INTO v_Currency_ID, v_TotalOpenAmt, v_ConversionType_ID,v_Precision
	FROM	C_Invoice I		--	NO se corrige por CM o SpliPayment; se usa directamente C_Inovoice y ningun multiplicador
	WHERE	I.C_Invoice_ID = p_C_Invoice_ID;

	IF NOT FOUND THEN
       	RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID;
		RETURN NULL;
	END IF;

	-- se saca lo siguiente para hacerlo tal como lo hace Libertya; igual tiene cierto sentido usar estos limites, sal vo que en Libertya
    -- se usa 2 decimales en todos los montos....	
	--SELECT 1/10^v_Precision INTO v_Min;

	
	--	Calculate Allocated Amount : SIEMPRE 1 como multiplicador
	v_PaidAmt := getAllocatedAmt(p_C_Invoice_ID,v_Currency_ID,v_ConversionType_ID,1);

    --  Do we have a Payment Schedule ?
    IF (p_C_InvoicePaySchedule_ID > 0) THEN --   if not valid = lists invoice amount
        v_Remaining := v_PaidAmt;
        FOR s IN 
        	SELECT  C_InvoicePaySchedule_ID, DueAmt
	        FROM    C_InvoicePaySchedule
		WHERE	C_Invoice_ID = p_C_Invoice_ID
	        AND   IsValid='Y'
        	ORDER BY DueDate
        LOOP
            IF (s.C_InvoicePaySchedule_ID = p_C_InvoicePaySchedule_ID) THEN
                v_TotalOpenAmt := s.DueAmt - v_Remaining;
                IF (v_TotalOpenAmt < 0) THEN
                    v_TotalOpenAmt := 0; -- Pagado totalmente
                END IF;
				EXIT; -- se sale del loop, ya que ya se encontro
            ELSE -- calculate amount, which can be allocated to next schedule
                v_Remaining := v_Remaining - s.DueAmt;
                IF (v_Remaining < 0) THEN
                    v_Remaining := 0;
                END IF;
            END IF;
        END LOOP;
    ELSE
        v_TotalOpenAmt := v_TotalOpenAmt - v_PaidAmt;
    END IF;
--  RAISE NOTICE ''== Total='' || v_TotalOpenAmt;

	--	Ignore Rounding
	IF (v_TotalOpenAmt >= -v_Min AND v_TotalOpenAmt <= v_Min) THEN
		v_TotalOpenAmt := 0;
	END IF;

	--	Round to currency precision
	v_TotalOpenAmt := ROUND(COALESCE(v_TotalOpenAmt,0), v_Precision);
	RETURN	v_TotalOpenAmt;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.invoiceopen(integer, integer) OWNER TO libertya;
-- end Invoice\invoiceOpen-nueva.sql 
-- Invoice\invoiceDiscount-sugerida.sql 
CREATE OR REPLACE FUNCTION libertya.invoicediscount(p_c_invoice_id integer, 
p_paydate timestamp with time zone, p_c_invoicepayschedule_id integer)
  RETURNS numeric AS
$BODY$
/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 ***
 * Title:	Calculate Payment Discount Amount
 * Description:
 *			- Calculate discountable amount (i.e. with or without tax)
 *			- Calculate and return payment discount
 * Test:
 * 		select invoiceDiscount(109, now(), 103) from ad_system; => 0
 ***********
 *NOTAS:
 *-Basado en Adempiere 360LTS pero su funcionamiento difiere:
 *1)lo comparacion de fechas en caos paySchedule es "si antes o justo en det. fecha";
 *  la forma actual no tiene sentido para moelar descuento
 *2) En caso de usar C_PaymetTerm para calular el descuento, se toma como
 * fecha base , la fecha que se supone que deberia pagarse, la cual no necesariamente
 * es no DateInvoced, si no la determinada por paymentTermDueDate
 ************************************************************************/
DECLARE
	v_Amount		NUMERIC;
	v_IsDiscountLineAmt	CHAR(1);
	v_GrandTotal		NUMERIC;
	v_TotalLines		NUMERIC;
	v_C_PaymentTerm_ID	integer;
	v_DocDate		timestamp with time zone;
	v_PayDate		timestamp with time zone := now();
    v_IsPayScheduleValid    CHAR(1);

BEGIN
	IF (p_c_invoice_id IS NULL OR p_c_invoice_id = 0) THEN
		return null;
	END IF;
	
	SELECT 	ci.IsDiscountLineAmt, i.GrandTotal, i.TotalLines,
		i.C_PaymentTerm_ID, i.DateInvoiced, i.IsPayScheduleValid
	INTO 	v_IsDiscountLineAmt, v_GrandTotal, v_TotalLines,
		v_C_PaymentTerm_ID, v_DocDate, v_IsPayScheduleValid
	FROM 	AD_ClientInfo ci, C_Invoice i
	WHERE 	ci.AD_Client_ID=i.AD_Client_ID
	  AND 	i.C_Invoice_ID=p_C_Invoice_ID;
	
	IF NOT FOUND THEN -- no deberia pasar, salvo que este mal invocada
		RETURN null; -- igual que PL/Java
    END IF;	
	--	What Amount is the Discount Base?
 	IF (v_IsDiscountLineAmt = 'Y') THEN
		v_Amount := v_TotalLines;
	ELSE
		v_Amount := v_GrandTotal;
	END IF;

	--	Anything to discount?
	IF (v_Amount = 0) THEN
		RETURN 0;
   	END IF;
	
	IF (p_PayDate IS NOT NULL) THEN
		v_PayDate := p_PayDate;
  	END IF;
	v_PayDate := TRUNC(v_PayDate);

    --  Valid Payment Schedule
    IF (v_IsPayScheduleValid='Y' AND p_C_InvoicePaySchedule_ID > 0) THEN
        SELECT COALESCE(MAX(DiscountAmt),0)
          INTO v_Amount
        FROM C_InvoicePaySchedule
        WHERE C_InvoicePaySchedule_ID=p_C_InvoicePaySchedule_ID
          AND TRUNC(DiscountDate) >= v_PayDate; -- 1) SE INVIERTE La comparacion
        --
        RETURN v_Amount;
    END IF;

	--2) se toma como fecha base para el calculo del descuento la fecha que se debe pagar, la cual no necesariamente
	-- es DateInvoiced (PaymentTerm es el que detemina cuando hay que pagar)
	v_DocDate := paymentTermDueDate(v_C_PaymentTerm_ID, v_DocDate);
	--	return discount amount	
	RETURN paymentTermDiscount (v_Amount, v_C_PaymentTerm_ID, v_DocDate, p_PayDate);

END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.invoicediscount(integer, timestamp with time zone, integer) OWNER TO libertya;
-- end Invoice\invoiceDiscount-sugerida.sql 
-- BOM  
--------------------------------------------------- 
-- BOM\BOMPriceLimit-2int-conCorreccionNull.sql 
-- Function: libertya.bompricelimit(integer, integer)

-- DROP FUNCTION libertya.bompricelimit(integer, integer);

CREATE OR REPLACE FUNCTION libertya.bompricelimit(product_id integer, pricelist_version_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Price	NUMERIC;
	v_ProductPrice	NUMERIC;
	bom RECORD;

BEGIN
    -- Ader: modificado para chequee si realmente no existe la entrada correspondiente; antes COALESCE (SUM(PriceLimit), 0)
	--	Try to get price from PriceList directly
	SELECT	SUM(PriceLimit)
      	INTO	v_Price
   	FROM	M_ProductPrice
	WHERE M_PriceList_Version_ID=PriceList_Version_ID AND M_Product_ID=Product_ID;

	--	No Price - Check if BOM
	-- Ader: cambiado para que chequee el null y solo en ese caso pasar a la def recursiva; antes v_Price = 0
	IF (v_Price IS NULL) THEN
	    v_Price := 0.0000; -- inicizalizacion requerida porque ahora es null, el punto decimal para que sea identico a lo anterior
		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=Product_ID
		LOOP
			v_ProductPrice := bomPriceLimit (bom.M_ProductBOM_ID, PriceList_Version_ID);
			v_Price := v_Price + (bom.BOMQty * v_ProductPrice);
		END LOOP;
	END IF;
	--
	RETURN v_Price;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bompricelimit(integer, integer) OWNER TO libertya;
-- end BOM\BOMPriceLimit-2int-conCorreccionNull.sql 
-- BOM\BOMPriceLimit-3int.sql 
CREATE OR REPLACE FUNCTION libertya.bompricelimit(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
BEGIN
    --- se dehabilita los precios para instancias , simplemente no usando m_attributesetinstance_id
    RETURN bomPriceLimit(M_Product_ID,M_PriceList_Version_ID);
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bompricelimit(integer, integer, integer) OWNER TO libertya;-- end BOM\BOMPriceLimit-3int.sql 
-- BOM\BOMPriceList-2int.sql 
-- Function: libertya.bompricelist(integer, integer)

-- DROP FUNCTION libertya.bompricelist(integer, integer);

CREATE OR REPLACE FUNCTION libertya.bompricelist(product_id integer, pricelist_version_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Price	NUMERIC;
	v_ProductPrice	NUMERIC;
	bom RECORD;

BEGIN
	--	Try to get price from pricelist directly
	SELECT	COALESCE (SUM(PriceList), 0)
	INTO	v_Price
	FROM	M_ProductPrice
	WHERE M_PriceList_Version_ID=PriceList_Version_ID AND M_Product_ID=Product_ID;

	--	No Price - Check if BOM
	IF (v_Price = 0) THEN
		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=Product_ID
		LOOP
			v_ProductPrice := bomPriceList (bom.M_ProductBOM_ID, PriceList_Version_ID);
			v_Price := v_Price + (bom.BOMQty * v_ProductPrice);
		END LOOP;
	END IF;
	--
	RETURN v_Price;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bompricelist(integer, integer) OWNER TO libertya;
-- end BOM\BOMPriceList-2int.sql 
-- BOM\BOMPriceList-3int.sql 
CREATE OR REPLACE FUNCTION libertya.bompricelist(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
BEGIN
    -- se deshabilita los precios para instancias simplemente no usando  m_attributesetinstance_id 
    RETURN bomPriceList(M_Product_ID,M_PriceList_Version_ID);
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bompricelist(integer, integer, integer) OWNER TO libertya;
-- end BOM\BOMPriceList-3int.sql 
-- BOM\BOMPriceStd-2int.sql 
-- Function: libertya.bompricestd(integer, integer)

-- DROP FUNCTION libertya.bompricestd(integer, integer);

CREATE OR REPLACE FUNCTION libertya.bompricestd(product_id integer, pricelist_version_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Price	NUMERIC;
	v_ProductPrice	NUMERIC;
	bom RECORD;

BEGIN
	--	Try to get price from PriceList directly
	SELECT	COALESCE(SUM(PriceStd), 0)
	INTO	v_Price
	FROM	M_ProductPrice
	WHERE M_PriceList_Version_ID=PriceList_Version_ID AND M_Product_ID=Product_ID;

	--	No Price - Check if BOM
	IF (v_Price = 0) THEN
		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=Product_ID
		LOOP
			v_ProductPrice := bompricestd (bom.M_ProductBOM_ID, PriceList_Version_ID);
			v_Price := v_Price + (bom.BOMQty * v_ProductPrice);
		END LOOP;
	END IF;
	--
	RETURN v_Price;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bompricestd(integer, integer) OWNER TO libertya;
-- end BOM\BOMPriceStd-2int.sql 
-- BOM\BOMPriceStd-3int.sql 
CREATE OR REPLACE FUNCTION libertya.bompricestd(m_product_id integer, m_pricelist_version_id integer, m_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
BEGIN
    -- se deshabilitan los precios en instancias simplemente no usnado m_attributesetinstance_id
    RETURN bomPriceStd(M_Product_ID,M_PriceList_Version_ID);
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bompricestd(integer, integer, integer) OWNER TO libertya;
-- end BOM\BOMPriceStd-3int.sql 
-- BOM\BOMQtyReserved-3-int.sql 
--- basado en Adempiere 3.4.2
CREATE OR REPLACE FUNCTION libertya.bomqtyreserved(p_product_id integer, p_warehouse_id integer, p_locator_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Warehouse_ID		INTEGER;
	v_Quantity		NUMERIC := 99999;	--	unlimited
	v_IsBOM			CHAR(1);
	v_IsStocked		CHAR(1);
	v_ProductType		CHAR(1);
	v_ProductQty		NUMERIC;
	v_StdPrecision		NUMERIC;
	bom RECORD;

BEGIN
	
	--	Check Parameters
		v_Warehouse_ID := p_Warehouse_ID;
		IF (v_Warehouse_ID IS NULL) THEN
			IF (p_Locator_ID IS NULL) THEN
				RETURN 0;
			ELSE
				SELECT 	MAX(M_Warehouse_ID) INTO v_Warehouse_ID
				FROM	M_Locator
				WHERE	M_Locator_ID=p_Locator_ID;
			END IF;
		END IF;
		IF (v_Warehouse_ID IS NULL) THEN
			RETURN 0;
		END IF;
	--	DBMS_OUTPUT.PUT_LINE('Warehouse=' || v_Warehouse_ID);
	
		--	Check, if product exists and if it is stocked
		BEGIN
			SELECT	IsBOM, ProductType, IsStocked
			  INTO STRICT	v_IsBOM, v_ProductType, v_IsStocked
			FROM M_Product
			WHERE M_Product_ID=p_Product_ID;
			--
		
		EXCEPTION	--	not found
			WHEN OTHERS THEN
				RETURN 0;
		END;
	
		-- --	No reservation for non-stocked
		IF (v_IsBOM='N' AND (v_ProductType<>'I' OR v_IsStocked='N')) THEN
			RETURN 0; -- a diferencia de OnHand, aca se retorna 0 (en OnHand unlimited)
		--	Stocked item
		ELSIF (v_IsStocked='Y') THEN
			--	Get ProductQty
			-- NOTA: Adempiere solo toma la suma, si importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
			-- la condicion siguiente para respetar la fomra de Liberyta
			IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
				SELECT 	COALESCE(SUM(QtyReserved), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND s.M_Locator_ID=p_locator_id;
			-- suma en todas las locaciones del almacen	
			ELSE
				SELECT 	COALESCE(SUM(QtyReserved), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
							AND l.M_Warehouse_ID=v_Warehouse_ID);
			END IF;	
			--
			RETURN v_ProductQty;
		END IF;
	
		--	Go though BOM

		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM, p.IsStocked, p.ProductType
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=p_Product_ID
		LOOP
			--	Stocked Items "leaf node"
			IF (bom.ProductType = 'I' AND bom.IsStocked = 'Y') THEN
				--	Get ProductQty
				-- NOTA idem anterior (aunque en productos BOM tal vez no tenga mucho sentido...): Adempiere solo toma la suma, si
				--importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
				-- la condicion siguiente para respetar la fomra de Liberyta
				IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
					SELECT 	COALESCE(SUM(QtyReserved), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE M_Product_ID=p_Product_ID
					AND s.M_Locator_ID=p_locator_id;
				-- suma en todas las locaciones del almacen	
				ELSE
					SELECT 	COALESCE(SUM(QtyReserved), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE 	M_Product_ID=bom.M_ProductBOM_ID
					AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
						AND l.M_Warehouse_ID=v_Warehouse_ID);
				END IF;		
				--	Get Rounding Precision
				SELECT 	COALESCE(MAX(u.StdPrecision), 0)
				  INTO	v_StdPrecision
				FROM 	C_UOM u, M_Product p 
				WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=bom.M_ProductBOM_ID;
				--	How much can we make with this product
				v_ProductQty := ROUND (v_ProductQty/bom.BOMQty, v_StdPrecision);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			--	Another BOM
			ELSIF (bom.IsBOM = 'Y') THEN
				v_ProductQty := bomQtyReserved (bom.M_ProductBOM_ID, v_Warehouse_ID, p_Locator_ID);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			END IF;
		END LOOP;	--	BOM
	
	
		IF (v_Quantity > 0) THEN
			--	Get Rounding Precision for Product
			SELECT 	COALESCE(MAX(u.StdPrecision), 0)
			  INTO	v_StdPrecision
			FROM 	C_UOM u, M_Product p 
			WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=p_Product_ID;
			--
			RETURN ROUND (v_Quantity, v_StdPrecision);
		END IF;
	RETURN 0;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bomqtyreserved(integer, integer, integer) OWNER TO libertya;
-- end BOM\BOMQtyReserved-3-int.sql 
-- BOM\BOMQtyOrdered-3-int.sql 
--- basado en Adempiere 3.4.2
CREATE OR REPLACE FUNCTION libertya.bomqtyordered(p_product_id integer, p_warehouse_id integer, p_locator_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Warehouse_ID		INTEGER;
	v_Quantity		NUMERIC := 99999;	--	unlimited
	v_IsBOM			CHAR(1);
	v_IsStocked		CHAR(1);
	v_ProductType		CHAR(1);
	v_ProductQty		NUMERIC;
	v_StdPrecision		NUMERIC;
	bom RECORD;

BEGIN
	
	--	Check Parameters
		v_Warehouse_ID := p_Warehouse_ID;
		IF (v_Warehouse_ID IS NULL) THEN
			IF (p_Locator_ID IS NULL) THEN
				RETURN 0;
			ELSE
				SELECT 	MAX(M_Warehouse_ID) INTO v_Warehouse_ID
				FROM	M_Locator
				WHERE	M_Locator_ID=p_Locator_ID;
			END IF;
		END IF;
		IF (v_Warehouse_ID IS NULL) THEN
			RETURN 0;
		END IF;
	--	DBMS_OUTPUT.PUT_LINE('Warehouse=' || v_Warehouse_ID);
	
		--	Check, if product exists and if it is stocked
		BEGIN
			SELECT	IsBOM, ProductType, IsStocked
			  INTO STRICT	v_IsBOM, v_ProductType, v_IsStocked
			FROM M_Product
			WHERE M_Product_ID=p_Product_ID;
			--
		
		EXCEPTION	--	not found
			WHEN OTHERS THEN
				RETURN 0;
		END;
	
		-- --	No reservation for non-stocked
		IF (v_IsBOM='N' AND (v_ProductType<>'I' OR v_IsStocked='N')) THEN
			RETURN 0; -- a diferencia de OnHand, aca se retorna 0 (en OnHand unlimited)
		--	Stocked item
		ELSIF (v_IsStocked='Y') THEN
			--	Get ProductQty
			-- NOTA: Adempiere solo toma la suma, si importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
			-- la condicion siguiente para respetar la fomra de Liberyta
			IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
				SELECT 	COALESCE(SUM(QtyOrdered), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND s.M_Locator_ID=p_locator_id;
			-- suma en todas las locaciones del almacen	
			ELSE
				SELECT 	COALESCE(SUM(QtyOrdered), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
							AND l.M_Warehouse_ID=v_Warehouse_ID);
			END IF;	
			--
			RETURN v_ProductQty;
		END IF;
	
		--	Go though BOM

		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM, p.IsStocked, p.ProductType
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=p_Product_ID
		LOOP
			--	Stocked Items "leaf node"
			IF (bom.ProductType = 'I' AND bom.IsStocked = 'Y') THEN
				--	Get ProductQty
				-- NOTA idem anterior (aunque en productos BOM tal vez no tenga mucho sentido...): Adempiere solo toma la suma, si
				--importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
				-- la condicion siguiente para respetar la fomra de Liberyta
				IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
					SELECT 	COALESCE(SUM(QtyOrdered), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE M_Product_ID=p_Product_ID
					AND s.M_Locator_ID=p_locator_id;
				-- suma en todas las locaciones del almacen	
				ELSE
					SELECT 	COALESCE(SUM(QtyOrdered), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE 	M_Product_ID=bom.M_ProductBOM_ID
					AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
						AND l.M_Warehouse_ID=v_Warehouse_ID);
				END IF;		
				--	Get Rounding Precision
				SELECT 	COALESCE(MAX(u.StdPrecision), 0)
				  INTO	v_StdPrecision
				FROM 	C_UOM u, M_Product p 
				WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=bom.M_ProductBOM_ID;
				--	How much can we make with this product
				v_ProductQty := ROUND (v_ProductQty/bom.BOMQty, v_StdPrecision);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			--	Another BOM
			ELSIF (bom.IsBOM = 'Y') THEN
				v_ProductQty := bomQtyOrdered (bom.M_ProductBOM_ID, v_Warehouse_ID, p_Locator_ID);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			END IF;
		END LOOP;	--	BOM
	
	
		IF (v_Quantity > 0) THEN
			--	Get Rounding Precision for Product
			SELECT 	COALESCE(MAX(u.StdPrecision), 0)
			  INTO	v_StdPrecision
			FROM 	C_UOM u, M_Product p 
			WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=p_Product_ID;
			--
			RETURN ROUND (v_Quantity, v_StdPrecision);
		END IF;
	RETURN 0;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bomqtyordered(integer, integer, integer) OWNER TO libertya;
-- end BOM\BOMQtyOrdered-3-int.sql 
-- BOM\BOMQtyOnHand-3-int.sql 
--- basado en Adempiere
CREATE OR REPLACE FUNCTION libertya.bomqtyonhand(p_product_id integer, p_warehouse_id integer, p_locator_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Warehouse_ID		INTEGER;
	v_Quantity		NUMERIC := 99999;	--	unlimited
	v_IsBOM			CHAR(1);
	v_IsStocked		CHAR(1);
	v_ProductType		CHAR(1);
	v_ProductQty		NUMERIC;
	v_StdPrecision		NUMERIC;
	bom RECORD;

BEGIN
	
	--	Check Parameters
		v_Warehouse_ID := p_Warehouse_ID;
		IF (v_Warehouse_ID IS NULL) THEN
			IF (p_Locator_ID IS NULL) THEN
				RETURN 0;
			ELSE
				SELECT 	MAX(M_Warehouse_ID) INTO v_Warehouse_ID
				FROM	M_Locator
				WHERE	M_Locator_ID=p_Locator_ID;
			END IF;
		END IF;
		IF (v_Warehouse_ID IS NULL) THEN
			RETURN 0;
		END IF;
	--	DBMS_OUTPUT.PUT_LINE('Warehouse=' || v_Warehouse_ID);
	
		--	Check, if product exists and if it is stocked
		BEGIN
			SELECT	IsBOM, ProductType, IsStocked
			  INTO STRICT	v_IsBOM, v_ProductType, v_IsStocked
			FROM M_Product
			WHERE M_Product_ID=p_Product_ID;
			--
		
		EXCEPTION	--	not found
			WHEN OTHERS THEN
				RETURN 0;
		END;
	
		-- Unlimited capacity if no item : Nota; aca Libertya retonra 0; Adempiere 3.6.0 retorna ilimitado, lo cual tiene sentido
		IF (v_IsBOM='N' AND (v_ProductType<>'I' OR v_IsStocked='N')) THEN
			RETURN v_Quantity; -- unlimited; tomando la nueva forma
		--	Stocked item
		ELSIF (v_IsStocked='Y') THEN
			--	Get ProductQty
			-- NOTA: Adempiere solo toma la suma, si importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
			-- la condicion siguiente para respetar la fomra de Liberyta
			IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
				SELECT 	COALESCE(SUM(QtyOnHand), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND s.M_Locator_ID=p_locator_id;
			-- suma en todas las locaciones del almacen	
			ELSE
				SELECT 	COALESCE(SUM(QtyOnHand), 0)
					INTO	v_ProductQty
				FROM 	M_Storage s
				WHERE M_Product_ID=p_Product_ID
				AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
							AND l.M_Warehouse_ID=v_Warehouse_ID);
			END IF;	
			--
			RETURN v_ProductQty;
		END IF;
	
		--	Go though BOM

		FOR bom IN  
			SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM, p.IsStocked, p.ProductType
			FROM M_Product_BOM b, M_Product p
			WHERE b.M_ProductBOM_ID=p.M_Product_ID
		  	AND b.M_Product_ID=p_Product_ID
		LOOP
			--	Stocked Items "leaf node"
			IF (bom.ProductType = 'I' AND bom.IsStocked = 'Y') THEN
				--	Get ProductQty
				-- NOTA idem anterior (aunque en productos BOM tal vez no tenga mucho sentido...): Adempiere solo toma la suma, si
				--importar si M_Locator_ID es distinto de Cero; Libertya, no; agregmos
				-- la condicion siguiente para respetar la fomra de Liberyta
				IF (p_locator_id IS NOT NULL AND p_locator_id <>0) THEN
					SELECT 	COALESCE(SUM(QtyOnHand), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE M_Product_ID=p_Product_ID
					AND s.M_Locator_ID=p_locator_id;
				-- suma en todas las locaciones del almacen	
				ELSE
					SELECT 	COALESCE(SUM(QtyOnHand), 0)
						INTO	v_ProductQty
					FROM 	M_Storage s
					WHERE 	M_Product_ID=bom.M_ProductBOM_ID
					AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID
						AND l.M_Warehouse_ID=v_Warehouse_ID);
				END IF;		
				--	Get Rounding Precision
				SELECT 	COALESCE(MAX(u.StdPrecision), 0)
				  INTO	v_StdPrecision
				FROM 	C_UOM u, M_Product p 
				WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=bom.M_ProductBOM_ID;
				--	How much can we make with this product
				v_ProductQty := ROUND (v_ProductQty/bom.BOMQty, v_StdPrecision);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			--	Another BOM
			ELSIF (bom.IsBOM = 'Y') THEN
				v_ProductQty := bomQtyOnHand (bom.M_ProductBOM_ID, v_Warehouse_ID, p_Locator_ID);
				--	How much can we make overall
				IF (v_ProductQty < v_Quantity) THEN
					v_Quantity := v_ProductQty;
				END IF;
			END IF;
		END LOOP;	--	BOM
	
	
		IF (v_Quantity > 0) THEN
			--	Get Rounding Precision for Product
			SELECT 	COALESCE(MAX(u.StdPrecision), 0)
			  INTO	v_StdPrecision
			FROM 	C_UOM u, M_Product p 
			WHERE 	u.C_UOM_ID=p.C_UOM_ID AND p.M_Product_ID=p_Product_ID;
			--
			RETURN ROUND (v_Quantity, v_StdPrecision);
		END IF;
	RETURN 0;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bomqtyonhand(integer, integer, integer) OWNER TO libertya;
-- end BOM\BOMQtyOnHand-3-int.sql 
-- BOM\BOMQtyAvailable-3-int.sql 
CREATE OR REPLACE FUNCTION libertya.bomqtyavailable(product_id integer, warehouse_id integer, locator_id integer)
  RETURNS numeric AS
$BODY$
BEGIN
	
		RETURN bomQtyOnHand(Product_ID, Warehouse_ID, Locator_ID)
		- bomQtyReserved(Product_ID, Warehouse_ID, Locator_ID);
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bomqtyavailable(integer, integer, integer) OWNER TO libertya;
-- end BOM\BOMQtyAvailable-3-int.sql 
-- Account  
--------------------------------------------------- 
-- Account\acctBalance.sql 
CREATE OR REPLACE FUNCTION libertya.acctbalance(p_account_id integer, 
p_amtdr numeric, p_amtcr numeric)
  RETURNS numeric AS
$BODY$
/*************************************
TEST:
select name,AccountType,AccountSign, 
acctBalance(C_ElementValue_ID,100.00,75.00) as Balance -- 25 o -25 dep. de AccountType y AccountSign 
FROM C_ElementValue
order by AccountSign,AccountType,C_ElementValue_ID

*/
DECLARE
	v_balance	NUMERIC;
	v_AccountType   CHARACTER(1);
   	v_AccountSign   CHARACTER(1);
	v_AmtDr NUMERIC := COALESCE(p_amtdr,0.00);
	v_AmtCr NUMERIC := COALESCE(p_amtcr,0.00);
BEGIN
	    v_balance := v_AmtDr - v_AmtCr;
	    --  
	    IF (p_Account_ID > 0) THEN
	        SELECT AccountType, AccountSign
	          INTO v_AccountType, v_AccountSign
	        FROM C_ElementValue
	        WHERE C_ElementValue_ID=p_Account_ID;
	   --   DBMS_OUTPUT.PUT_LINE('Type=' || v_AccountType || ' - Sign=' || v_AccountSign);
	   
			IF NOT FOUND THEN
				RETURN v_balance;
			END IF;
	        --  Natural Account Sign
	        IF (v_AccountSign='N') THEN
	            IF (v_AccountType IN ('A','E')) THEN
	                v_AccountSign := 'D';
	            ELSE
	                v_AccountSign := 'C';
	            END IF;
	        --  DBMS_OUTPUT.PUT_LINE('Type=' || v_AccountType || ' - Sign=' || v_AccountSign);
	        END IF;
	        --  Debit Balance
	        IF (v_AccountSign = 'C') THEN
	            v_balance := v_AmtCr - v_AmtDr;
	        END IF;
	    END IF;
	    --
	    RETURN v_balance;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.acctbalance(integer, numeric, numeric) OWNER TO libertya;
-- end Account\acctBalance.sql 
-- BPartner  
--------------------------------------------------- 
-- BPartner\bpartnerRemitLocation.sql 
CREATE OR REPLACE FUNCTION libertya.bpartnerremitlocation(p_c_bpartner_id integer)
  RETURNS integer AS
$BODY$
/*********
Notas: no tiene en cuenta si la C_BPartner_Location esta inactiva;
tal vez habria que orderna por Active Desc y luego por IsRemitTo Desc; de esta
manera primero se da prioridad a las activas y despues a las IsRemitTo
*/
DECLARE
	v_C_Location_ID	INTEGER := 0; -- asi se retorna lo mismo que en PL/Java de Libertya
	l RECORD;

BEGIN
	FOR l IN 
		SELECT	IsRemitTo, C_Location_ID
		FROM	C_BPartner_Location
		WHERE	C_BPartner_ID=p_C_BPartner_ID
		ORDER BY IsRemitTo DESC
	LOOP -- el loop es innecesario, pero bueno
		IF (v_C_Location_ID = 0) THEN
			v_C_Location_ID := l.C_Location_ID;
		END IF;
	END LOOP;
	RETURN v_C_Location_ID;
	
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.bpartnerremitlocation(integer) OWNER TO libertya;
-- end BPartner\bpartnerRemitLocation.sql 
-- Product (restantes en BOM)  
--------------------------------------------------- 
-- Product\productAttribute.sql 
CREATE OR REPLACE FUNCTION libertya.productattribute(p_m_attributesetinstance_id integer)
  RETURNS character varying AS
$BODY$

/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CRM
 * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.
 *
 * converted to postgreSQL by Karsten Thiemann (Schaeffer AG), 
 * kthiemann@adempiere.org
 *************************************************************************
 * Title: Return Instance Attribute Info
 * Description:
 *  
 * Test:
    SELECT ProductAttribute (M_AttributeSetInstance_ID) 
    FROM M_InOutLine WHERE M_AttributeSetInstance_ID > 0
    --
    SELECT p.Name
    FROM C_InvoiceLine il LEFT OUTER JOIN M_Product p ON (il.M_Product_ID=p.M_Product_ID);
    SELECT p.Name || ProductAttribute (il.M_AttributeSetInstance_ID) 
    FROM C_InvoiceLine il LEFT OUTER JOIN M_Product p ON (il.M_Product_ID=p.M_Product_ID);
 
 *****
 Version LIbertya: nunca retorna null; a lo sumo ''; primero va el lote,
 despues el numero de serie (prefijado por #), luego al fecha gatantizada (solo dias)
 (todos estos campos opcionales); luego la concatenacion de Atributos:valor;
 finalmente, si la descripcin es no vacia, se parentiza
 
 
 ************************************************************************/

	
DECLARE

    v_Name          VARCHAR(2000) := '';
    v_NameAdd       VARCHAR(2000) := '';
    --
    v_Lot           character varying(40);
    v_SerNo       	character varying(40);
    v_GuaranteeDate timestamp with time zone;
    
    r   RECORD;
    --

BEGIN
    --  Get Product Attribute Set Instance
    IF (p_M_AttributeSetInstance_ID > 0) THEN
        SELECT asi.Lot, asi.SerNo, asi.GuaranteeDate
          INTO v_Lot, v_SerNo, v_GuaranteeDate
        FROM M_AttributeSetInstance asi
          INNER JOIN M_AttributeSet a ON (asi.M_AttributeSet_ID=a.M_AttributeSet_ID)
        WHERE asi.M_AttributeSetInstance_ID=p_M_AttributeSetInstance_ID;
        --
        IF (v_Lot IS NOT NULL AND LENGTH(v_Lot)>0) THEN
            v_NameAdd := v_NameAdd || v_Lot || ' ';
        END IF;

        IF (v_SerNo IS NOT NULL AND LENGTH(v_SerNo)>0) THEN
            v_NameAdd := v_NameAdd || '#' || v_SerNo || ' ';
        END IF;
        IF (v_GuaranteeDate IS NOT NULL) THEN
			-- TODO: esta concatenacion directa de fecha posiblemente no sea la mas conveniente, como fix, se castea
			-- a Date (esto hace que no aparezcan ni horas, ni mintutos, etc, ni zona horaria)
            v_NameAdd := v_NameAdd || v_GuaranteeDate::Date || ' ';
        END IF;
        --
        
        FOR r IN
	     SELECT ai.Value, a.Name
	        FROM M_AttributeInstance ai
	        INNER JOIN M_Attribute a 
				ON (ai.M_Attribute_ID=a.M_Attribute_ID	AND a.IsInstanceAttribute='Y')
        	WHERE ai.M_AttributeSetInstance_ID=p_M_AttributeSetInstance_ID
    	LOOP
            v_NameAdd := v_NameAdd || r.Name || ':' || r.Value || ' ';
        END LOOP;
        --
        IF (LENGTH(v_NameAdd) > 0) THEN
            v_Name := v_Name || ' (' || TRIM(v_NameAdd) || ')';
		ELSE 
			v_Name := ''; -- como en Libertya
        END IF;
    END IF;
    RETURN v_Name;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION libertya.productattribute(integer) OWNER TO libertya;
-- end Product\productAttribute.sql 
---- Fin de script generado automaticamente 

-- Eliminacion de Funciones deprecadas
drop function charat("char", integer);
drop FUNCTION documentno(integer);
drop function get_classpath(character varying);
drop function install_jar(character varying, character varying, boolean);
drop FUNCTION openxpertyaproperties();
drop FUNCTION openxpertyaproperty(character varying);
drop FUNCTION openxpertyaversion();
drop function openxpproperties();
drop FUNCTION openxpproperty(p_key character varying);
drop FUNCTION openxpversion();
drop FUNCTION remove_jar(character varying, boolean);
drop FUNCTION replace_jar(character varying, character varying, boolean);
drop FUNCTION set_classpath(character varying, character varying);
drop FUNCTION getmacaddresses();

-- ===================================================================================
-- =========== FIN 20110930-13:49 CONVERSION DE FUNCIONES PLJAVA A PLPGSQL ===========
-- =================================================================================== 

-- 20110930-1815 Columnas adicionales para configuración de impresión jasper al tipo de documento, vista previa o no y el nombre de la impresora
ALTER TABLE c_doctype ADD COLUMN isprintpreview character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE c_doctype ADD COLUMN printername character varying(60);

-- 20111012-1242 Actualización de version a nivel BBDD. 
UPDATE ad_system SET version = '12-10-2011' WHERE ad_system_id = 0;
-- ========================================================================================
-- FIN DE PREINSTALL FROM 11.10. 
-- ========================================================================================
