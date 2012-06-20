-- ========================================================================================
-- PREINSTALL FROM 11.10
-- ========================================================================================
-- 	Consideraciones importantes:
--			1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 			2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

-- 20111014-1400 Incorporación de nueva columna para registrar la guía de transporte
ALTER TABLE m_transfer ADD COLUMN transport_guide character varying(100);

-- 20111014-1600 Incorporación de nueva columna para registrar el formulario papel
ALTER TABLE m_inventory ADD COLUMN paper_form character varying(100);

-- 20111014-1754 Fix a la vista ya que la posición del - o + es la segunda del string
CREATE OR REPLACE VIEW rv_rotationreport_v2 AS 
 SELECT io.ad_org_id, pg.m_product_gamas_id, pl.m_product_lines_id, io.issotrx, io.ad_client_id, io.movementdate AS movementdatefrom, io.movementdate AS movementdateto, ol.m_product_id, p.m_product_category_id, io.m_inout_id, io.movementtype, ol.m_inoutline_id, 
        CASE
            WHEN charat(io.movementtype::character varying, 2)::text = '-'::text THEN sum(ol.movementqty)
            ELSE 0::numeric
        END AS movementout, 
        CASE
            WHEN charat(io.movementtype::character varying, 2)::text = '+'::text THEN sum(ol.movementqty)
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

-- 20111014-1802 Incorporación de nueva columna para registrar el nro pre-impreso
ALTER TABLE m_inout ADD COLUMN preprinted_docNo character varying(100);

-- 20111019-0927 Nueva vista con los movimientos de mercadería existentes, utilizada por la impresión de Cierre de Almacén
CREATE OR REPLACE VIEW v_movements AS 
(((( SELECT 'M_InOut'::character varying AS tablename, io.ad_client_id, io.ad_org_id, io.m_inout_id AS movement_document_id, io.documentno, io.movementdate::date AS movement_date, io.m_warehouse_id, 0 AS m_warehouseto_id, io.c_bpartner_id, io.description, io.docstatus, io.c_charge_id, io.c_doctype_id, dc.name AS doctypename
   FROM m_inout io
   LEFT JOIN c_doctype dc ON dc.c_doctype_id = io.c_doctype_id
UNION ALL 
 SELECT 'M_Inventory'::character varying AS tablename, i.ad_client_id, i.ad_org_id, i.m_inventory_id AS movement_document_id, i.documentno, i.movementdate::date AS movement_date, i.m_warehouse_id, 0 AS m_warehouseto_id, 0 AS c_bpartner_id, i.description, i.docstatus, i.c_charge_id, i.c_doctype_id, dc.name AS doctypename
   FROM m_inventory i
   LEFT JOIN c_doctype dc ON dc.c_doctype_id = i.c_doctype_id)
UNION ALL 
 SELECT 'M_ProductChange'::character varying AS tablename, pc.ad_client_id, pc.ad_org_id, pc.m_productchange_id AS movement_document_id, pc.documentno, pc.datetrx::date AS movement_date, pc.m_warehouse_id, 0 AS m_warehouseto_id, 0 AS c_bpartner_id, pc.description, pc.docstatus, 0 AS c_charge_id, 0 AS c_doctype_id, 'M_ProductChange_ID' AS doctypename
   FROM m_productchange pc)
UNION ALL 
 SELECT 'M_Splitting'::character varying AS tablename, s.ad_client_id, s.ad_org_id, s.m_splitting_id AS movement_document_id, s.documentno, s.datetrx::date AS movement_date, s.m_warehouse_id, 0 AS m_warehouseto_id, 0 AS c_bpartner_id, s.comments AS description, s.docstatus, 0 AS c_charge_id, 0 AS c_doctype_id, 'M_Splitting_ID' AS doctypename
   FROM m_splitting s)
UNION ALL 
 SELECT 'M_Transfer'::character varying AS tablename, t.ad_client_id, t.ad_org_id, t.m_transfer_id AS movement_document_id, t.documentno, t.datetrx::date AS movement_date, t.m_warehouse_id, t.m_warehouseto_id, 0 AS c_bpartner_id, t.description, t.docstatus, t.c_charge_id, 0 AS c_doctype_id, t.transfertype AS doctypename
   FROM m_transfer t)
UNION ALL 
 SELECT 'M_Movement'::character varying AS tablename, m.ad_client_id, m.ad_org_id, m.m_movement_id AS movement_document_id, m.documentno, m.movementdate::date AS movement_date, ws.m_warehouse_id, ws.m_warehouseto_id, 0 AS c_bpartner_id, m.description, m.docstatus, 0 AS c_charge_id, m.c_doctype_id, dc.name AS doctypename
   FROM m_movement m
   JOIN ( SELECT DISTINCT l.m_warehouse_id, ld.m_warehouse_id AS m_warehouseto_id, ml.m_movement_id
           FROM m_movementline ml
      JOIN m_locator l ON l.m_locator_id = ml.m_locator_id
   JOIN m_locator ld ON ld.m_locator_id = ml.m_locatorto_id
  ORDER BY l.m_warehouse_id, ld.m_warehouse_id, ml.m_movement_id) ws ON ws.m_movement_id = m.m_movement_id
   LEFT JOIN c_doctype dc ON dc.c_doctype_id = m.c_doctype_id;

ALTER TABLE v_movements OWNER TO libertya;

-- 20111019-1540 Nuevas columnas para la funcionalidad de habilitación de tipos de documento para TPV junto con sus días de habilitación
ALTER TABLE c_doctype ADD COLUMN enabledinpos character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE c_doctype ADD COLUMN posenabledue character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE c_doctype ADD COLUMN posenableduedays INTEGER NOT NULL DEFAULT 0;

-- 20111021-1522 Nuevas columnas para la funcionalidad de vencimientos de claves
ALTER TABLE ad_user ADD COLUMN lastpasswordchangedate date;
ALTER TABLE ad_clientinfo ADD COLUMN passwordexpirationactive character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE ad_clientinfo ADD COLUMN passwordexpirationdays INTEGER NOT NULL DEFAULT 0;

-- 20111031-1722 Nueva columna para la funcionalidad de claves únicas a partir de la clave propia del usuario
ALTER TABLE ad_clientinfo ADD COLUMN uniquekeyactive character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20111115-1400 Nuevas columnas de configuración del TPV para devoluciones en efectivo de notas de crédito
ALTER TABLE c_pos ADD COLUMN returnedcashincncontrol character(1) NOT NULL DEFAULT 'N'::bpchar;
ALTER TABLE c_pos ADD COLUMN maxreturnedcashincn numeric(22,2) NOT NULL DEFAULT 0;
