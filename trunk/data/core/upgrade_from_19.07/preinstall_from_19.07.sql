-- ========================================================================================
-- PREINSTALL FROM 19.07
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20190808-1200 Incorporación del costo histórico en la funview v_product_movements
drop view v_product_movements;
DROP FUNCTION v_product_movements_filtered(integer, 
	timestamp without time zone, 
	timestamp without time zone, 
	integer, 
	integer);
DROP TYPE v_product_movements_type;

CREATE TYPE v_product_movements_type AS (tablename character varying(40), ad_client_id integer, ad_org_id integer,
orgvalue character varying(40), orgname character varying(60),
doc_id integer, documentno character varying(30), docstatus character(2),
description character varying(255), datetrx timestamp, m_product_id integer,
qty numeric(22,4), "type" character varying(40), aditionaltype character varying(60),
c_charge_id integer, chargename character varying(60), productname character varying(255),
productvalue character varying(40), m_warehouse_id integer, warehousevalue character varying(40),
warehousename character varying(60), m_warehouseto_id integer, warehousetovalue character varying(40),
warehousetoname character varying(60), c_bpartner_id integer, bpartnervalue character varying(40),
bpartnername character varying(60), m_product_category_id integer, productcategoryvalue character varying(40),
productcategoryname character varying(60), m_product_gamas_id integer, productgamasvalue character varying(100),
productgamasname character varying(60), m_product_lines_id integer, productlinesvalue character varying(40),
productlinesname character varying(60), order_documentno character varying(30), costprice numeric(22,2));

CREATE OR REPLACE FUNCTION v_product_movements_filtered(orgID integer, 
	datefrom timestamp without time zone, 
	dateto timestamp without time zone, 
	chargeID integer, 
	productLinesID integer)
  RETURNS SETOF v_product_movements_type AS
$BODY$
declare
  consulta varchar;
  
  whereclausegeneric varchar;
  whereclauseorg varchar;
  wheredatefromdatetrx varchar;
  wheredatetodatetrx varchar;
  wheredatefrommovementdate varchar;
  wheredatetomovementdate varchar;
  whereclausecharge varchar;
  whereclauseproductlines varchar;

  joinchargeop varchar;
  joincharge varchar;
  joinchargeline varchar;
  joinproductlines varchar;
  joinproductlinesop varchar;
  
  transferquery varchar;
  productchangequery varchar;
  inoutquery varchar;
  splittingquery varchar;
  inventoryquery varchar;
  movementquery varchar;
  
  adocument v_product_movements_type;
   
BEGIN
  whereclausegeneric = ' WHERE (1 = 1) ';
  -- Condiciones y joins
  whereclauseorg = '';
  whereclausecharge = '';
  whereclauseproductlines = '';
  wheredatefromdatetrx = '';
  wheredatetodatetrx = '';
  wheredatefrommovementdate = '';
  wheredatetomovementdate = '';

  joinchargeop = ' LEFT ';
  joinproductlinesop = ' LEFT ';
  
  IF(orgID is not null and orgID > 0) 
  THEN whereclauseorg = ' AND d.ad_org_id = ' || orgID || ' ';
  END IF;

  IF(datefrom is not null) 
  THEN 
	wheredatefromdatetrx = ' AND ''' || datefrom || '''::date <= d.datetrx::date ';
	wheredatefrommovementdate = ' AND ''' || datefrom || '''::date <= d.movementdate::date ';
  END IF;

  IF(dateto is not null) 
  THEN 
	wheredatetodatetrx = ' AND ''' || dateto || '''::date >= d.datetrx::date ';
	wheredatetomovementdate = ' AND ''' || dateto || '''::date >= d.movementdate::date ';
  END IF;

  IF(chargeID is not null and chargeID > 0) 
  THEN 
	whereclausecharge = ' AND c.c_charge_id = ' || chargeID || ' ';
	joinchargeop = ' INNER ';
  END IF;
  
  IF(productLinesID is not null and productLinesID > 0) 
  THEN 
	whereclauseproductlines = ' AND pl.m_product_lines_id = ' || productLinesID || ' ';
	joinproductlinesop = ' INNER ';
  END IF;

  -- Joins
  joincharge = joinchargeop || ' JOIN c_charge c ON c.c_charge_id = d.c_charge_id ';
  joinchargeline = joinchargeop || ' JOIN c_charge c ON c.c_charge_id = l.c_charge_id ';
  
  joinproductlines = ' INNER JOIN m_product p ON p.m_product_id = l.m_product_id
			INNER JOIN m_product_category pc ON pc.m_product_category_id = p.m_product_category_id '
			|| joinproductlinesop || ' JOIN m_product_gamas pg ON pg.m_product_gamas_id = pc.m_product_gamas_id '
			|| joinproductlinesop || ' JOIN m_product_lines pl ON pl.m_product_lines_id = pg.m_product_lines_id ';
  
  -- Transferencias
  transferquery = ' ( SELECT ''M_Transfer'' AS tablename, d.ad_client_id, d.ad_org_id, d.m_transfer_id AS doc_id, 
			d.documentno, d.docstatus, d.description, d.datetrx, 
			p.m_product_id, p.value as productvalue, p.name as productname,
			l.qtyinternaluse * (-1)::numeric as qty, d.transfertype AS type, d.movementtype AS aditionaltype, 
			c.c_charge_id, c.name as chargename, d.m_warehouse_id, 
			d.m_warehouseto_id, bp.c_bpartner_id, bp.value as bpartnervalue, bp.name as bpartnername, 
			pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, 
			COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, 
			COALESCE(pg.value, ''SD''::character varying) AS productgamasvalue, 
			COALESCE(pg.name, ''SIN DESCRIPCION''::character varying) AS productgamasname, 
			COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, 
			COALESCE(pl.value, ''SD''::character varying) AS productlinesvalue, 
			COALESCE(pl.name, ''SIN DESCRIPCION''::character varying) AS productlinesname, 
			NULL::character varying(30) AS order_documentno,
			l.cost::numeric(22,2) as costprice
		 FROM m_transfer d
		 JOIN m_inventoryline l ON l.m_inventory_id = d.m_inventory_id 
		 JOIN c_bpartner bp ON bp.c_bpartner_id = d.c_bpartner_id ' 
		 || joincharge 
		 || joinproductlines 
		 || whereclausegeneric  
		 || whereclauseorg 
		 || wheredatefromdatetrx 
		 || wheredatetodatetrx 
		 || whereclausecharge  
		 || whereclauseproductlines || 
		 ' ) ';
		 
  -- Cambio de Artículo
  productchangequery = ' ( SELECT ''M_ProductChange'' AS tablename, d.ad_client_id, d.ad_org_id, d.m_productchange_id AS doc_id, 
			d.documentno, d.docstatus, d.description, d.datetrx, 
			p.m_product_id, p.value as productvalue, p.name as productname,
			l.qtyinternaluse * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, 
			c.c_charge_id, c.name as chargename, d.m_warehouse_id, NULL::unknown AS m_warehouseto_id, 
			NULL::unknown AS c_bpartner_id, NULL::unknown as bpartnervalue, NULL::unknown as bpartnername, 
			pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, 
			COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, 
			COALESCE(pg.value, ''SD''::character varying) AS productgamasvalue, 
			COALESCE(pg.name, ''SIN DESCRIPCION''::character varying) AS productgamasname, 
			COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, 
			COALESCE(pl.value, ''SD''::character varying) AS productlinesvalue, 
			COALESCE(pl.name, ''SIN DESCRIPCION''::character varying) AS productlinesname, 
			NULL::character varying(30) AS order_documentno,
			l.cost::numeric(22,2) as costprice
		FROM m_productchange d
		JOIN m_inventoryline l ON l.m_inventory_id = d.m_inventory_id '
		|| joinchargeline
		|| joinproductlines 
		|| whereclausegeneric 
		|| whereclauseorg 
		|| wheredatefromdatetrx
		|| wheredatetodatetrx
		|| whereclausecharge 
		|| whereclauseproductlines ||
		' ) ';

  -- Remitos
  inoutquery = '';
  IF(chargeID is null or chargeID <= 0) 
  THEN 
  inoutquery = ' UNION ALL  
		( SELECT ''M_InOut'' AS tablename, d.ad_client_id, d.ad_org_id, d.m_inout_id AS doc_id, d.documentno, 
			d.docstatus, d.description, d.movementdate AS datetrx, 
			p.m_product_id, p.value as productvalue, p.name as productname, l.movementqty AS qty, 
			dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, 
			NULL::unknown as chargename, d.m_warehouse_id, NULL::unknown AS m_warehouseto_id, 
			bp.c_bpartner_id, bp.value as bpartnervalue, bp.name as bpartnername, 
			pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, 
			COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, 
			COALESCE(pg.value, ''SD''::character varying) AS productgamasvalue, 
			COALESCE(pg.name, ''SIN DESCRIPCION''::character varying) AS productgamasname, 
			COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, 
			COALESCE(pl.value, ''SD''::character varying) AS productlinesvalue, 
			COALESCE(pl.name, ''SIN DESCRIPCION''::character varying) AS productlinesname, 
			o.documentno AS order_documentno,
			l.costprice::numeric(22,2) as costprice
		FROM m_inout d 
		JOIN m_inoutline l ON l.m_inout_id = d.m_inout_id
                JOIN c_doctype dt ON dt.c_doctype_id = d.c_doctype_id 
                JOIN c_bpartner bp ON bp.c_bpartner_id = d.c_bpartner_id 
                LEFT JOIN c_order o ON o.c_order_id = d.c_order_id ' 
                || joinproductlines 
		|| whereclausegeneric 
		|| whereclauseorg 
		|| wheredatefrommovementdate
		|| wheredatetomovementdate
		|| whereclauseproductlines || 
                ' ) ';
  END IF;

  --Fraccionamientos
  splittingquery = ' ( SELECT ''M_Splitting'' AS tablename, d.ad_client_id, d.ad_org_id, d.m_splitting_id AS doc_id, 
			d.documentno, d.docstatus, d.comments AS description, d.datetrx, 
			p.m_product_id, p.value as productvalue, p.name as productname,
			l.qtyinternaluse * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, 
			l.c_charge_id, c.name as chargename, d.m_warehouse_id, NULL::unknown AS m_warehouseto_id, 
			NULL::unknown AS c_bpartner_id, NULL::unknown as bpartnervalue, NULL::unknown as bpartnername, 
			pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, 
			COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, 
			COALESCE(pg.value, ''SD''::character varying) AS productgamasvalue, 
			COALESCE(pg.name, ''SIN DESCRIPCION''::character varying) AS productgamasname, 
			COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, 
			COALESCE(pl.value, ''SD''::character varying) AS productlinesvalue, 
			COALESCE(pl.name, ''SIN DESCRIPCION''::character varying) AS productlinesname, 
			NULL::character varying(30) AS order_documentno,
			l.cost::numeric(22,2) as costprice
                FROM m_splitting d 
                JOIN m_inventoryline l ON l.m_inventory_id = d.m_inventory_id '
                || joinchargeline 
		|| joinproductlines 
		|| whereclausegeneric 
		|| whereclauseorg 
		|| wheredatefromdatetrx
		|| wheredatetodatetrx
		|| whereclausecharge 
		|| whereclauseproductlines ||  
                   ' ) ';

  -- Inventario
  inventoryquery = ' ( SELECT ''M_Inventory'' AS tablename, d.ad_client_id, d.ad_org_id, d.m_inventory_id AS doc_id, 
			d.documentno, d.docstatus, d.description, d.movementdate AS datetrx, 
			p.m_product_id, p.value as productvalue, p.name as productname,
			l.qtycount AS qty, dt.name AS type, d.inventorykind AS aditionaltype, 
			d.c_charge_id, c.name as chargename, d.m_warehouse_id, NULL::unknown AS m_warehouseto_id, 
			NULL::unknown AS c_bpartner_id, NULL::unknown as bpartnervalue, NULL::unknown as bpartnername, 
			pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, 
			COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, 
			COALESCE(pg.value, ''SD''::character varying) AS productgamasvalue, 
			COALESCE(pg.name, ''SIN DESCRIPCION''::character varying) AS productgamasname, 
			COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, 
			COALESCE(pl.value, ''SD''::character varying) AS productlinesvalue, 
			COALESCE(pl.name, ''SIN DESCRIPCION''::character varying) AS productlinesname, 
			NULL::character varying(30) AS order_documentno,
			l.cost::numeric(22,2) as costprice
                FROM m_inventory d
                JOIN m_inventoryline l ON d.m_inventory_id = l.m_inventory_id
                JOIN c_doctype dt ON dt.c_doctype_id = d.c_doctype_id ' 
                || joincharge 
		|| joinproductlines 
		|| whereclausegeneric 
		|| whereclauseorg 
		|| wheredatefrommovementdate
		|| wheredatetomovementdate
		|| whereclausecharge 
		|| whereclauseproductlines ||
                ' 	AND NOT (EXISTS ( SELECT m_transfer.m_inventory_id
					FROM m_transfer
					WHERE m_transfer.m_inventory_id = d.m_inventory_id)) 
			AND NOT (EXISTS ( SELECT m_productchange.m_inventory_id 
						FROM m_productchange
						WHERE m_productchange.m_inventory_id = d.m_inventory_id 
							OR m_productchange.void_inventory_id = d.m_inventory_id)) 
			AND NOT (EXISTS ( SELECT s.m_inventory_id 
						FROM m_splitting s
						WHERE s.m_inventory_id = d.m_inventory_id 
							OR s.void_inventory_id = d.m_inventory_id)) 
		) ';

  -- Movimientos de mercadería
  movementquery = '';
  IF(chargeID is null or chargeID <= 0) 
  THEN 
  movementquery = ' UNION ALL 
		( SELECT ''M_Movement'' AS tablename, d.ad_client_id, d.ad_org_id, d.m_movement_id AS doc_id, 
			d.documentno, d.docstatus, d.description, d.movementdate AS datetrx, 
			p.m_product_id, p.value as productvalue, p.name as productname,
			l.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, 
			NULL::unknown AS c_charge_id, NULL::unknown as chargename, 
			w.m_warehouse_id, wt.m_warehouse_id AS m_warehouseto_id, 
			NULL::unknown AS c_bpartner_id, NULL::unknown as bpartnervalue, NULL::unknown as bpartnername, 
			pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, 
			COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, 
			COALESCE(pg.value, ''SD''::character varying) AS productgamasvalue, 
			COALESCE(pg.name, ''SIN DESCRIPCION''::character varying) AS productgamasname, 
			COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, 
			COALESCE(pl.value, ''SD''::character varying) AS productlinesvalue, 
			COALESCE(pl.name, ''SIN DESCRIPCION''::character varying) AS productlinesname, 
			NULL::character varying(30) AS order_documentno,
			null::numeric(22,2) as costprice
                FROM m_movement d
                JOIN m_movementline l ON l.m_movement_id = d.m_movement_id
                JOIN m_locator lo ON lo.m_locator_id = l.m_locator_id
                JOIN m_warehouse w ON w.m_warehouse_id = lo.m_warehouse_id
                JOIN m_locator lot ON lot.m_locator_id = l.m_locatorto_id
                JOIN m_warehouse wt ON wt.m_warehouse_id = lot.m_warehouse_id
                JOIN c_doctype dt ON dt.c_doctype_id = d.c_doctype_id ' 
                || joinproductlines 
		|| whereclausegeneric 
		|| whereclauseorg 
		|| wheredatefrommovementdate
		|| wheredatetomovementdate 
		|| whereclauseproductlines || 
		' ) ';
  END IF;
  
  -- Armar la consulta
  consulta = ' SELECT l.tablename, l.ad_client_id, l.ad_org_id, o.value AS orgvalue, o.name AS orgname, 
			l.doc_id, l.documentno, l.docstatus, l.description, l.datetrx, l.m_product_id, l.qty, l.type, 
			l.aditionaltype, l.c_charge_id, l.chargename, productname, productvalue, 
			w.m_warehouse_id, w.value AS warehousevalue, w.name AS warehousename, 
			wt.m_warehouse_id AS m_warehouseto_id, wt.value AS warehousetovalue, wt.name AS warehousetoname, 
			l.c_bpartner_id, l.bpartnervalue, l.bpartnername, 
			l.m_product_category_id, l.productcategoryvalue, l.productcategoryname, 
			l.m_product_gamas_id, l.productgamasvalue, l.productgamasname, 
			l.m_product_lines_id, l.productlinesvalue, l.productlinesname, 
			l.order_documentno, l.costprice

   FROM ( '
   || transferquery ||
   ' UNION ALL '
   || productchangequery
   || inoutquery || 
   ' UNION ALL ' 
   || splittingquery || 
   ' UNION ALL ' 
   || inventoryquery
   || movementquery || 
   ' ) l 
   JOIN ad_org o ON o.ad_org_id = l.ad_org_id 
   JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id 
   LEFT JOIN m_warehouse wt ON wt.m_warehouse_id = l.m_warehouseto_id ; ';

--raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE;
ALTER FUNCTION v_product_movements_filtered(integer, 
	timestamp without time zone, 
	timestamp without time zone, 
	integer, 
	integer)
  OWNER TO libertya;
  
create or replace view v_product_movements as 
select * 
from v_product_movements_filtered(-1,null,null,-1,-1);