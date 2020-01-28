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

--20190821-1620 Índice que mejora la performance de insert en fact_acct
CREATE INDEX fact_acct_balance_std_index
  ON fact_acct_balance
  USING btree
  (ad_client_id, ad_org_id, c_acctschema_id, dateacct, account_id, postingtype);
  
--20190906-2000 Registro de autorizaciones de usuario
CREATE TABLE c_user_authorization
(
  c_user_authorization_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  ad_process_id integer,
  ad_user_login_id integer NOT NULL,
  ad_user_auth_id integer NOT NULL,
  operationlog character varying(1000),
  c_invoice_id integer,
  amount numeric(22,4),
  percentage numeric(8,4),
  authtime timestamp without time zone,
  CONSTRAINT c_user_authorization_key PRIMARY KEY (c_user_authorization_id),
  CONSTRAINT c_user_authorization_process FOREIGN KEY (ad_process_id)
      REFERENCES ad_process (ad_process_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT c_user_authorization_userauth FOREIGN KEY (ad_user_auth_id)
      REFERENCES ad_user (ad_user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT c_user_authorization_userlogin FOREIGN KEY (ad_user_login_id)
      REFERENCES ad_user (ad_user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT c_user_authorization_invoice FOREIGN KEY (c_invoice_id)
      REFERENCES c_invoice (c_invoice_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_user_authorization
  OWNER TO libertya;

--20190923-1010 Incremento de tamaño de la columna porcentaje
ALTER TABLE c_user_authorization  ALTER COLUMN percentage TYPE numeric(20,4)

--20191226-1250 Solicitudes de NC de Proveedor
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','c_invoice_orig_id','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','creditrequesttype','character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','pricereception','numeric(22,4)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','qtyreception','numeric(22,4)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','receptionamt','numeric(22,4)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','pricediff','numeric(22,4)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','qtydiff','numeric(22,4)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','diffamt','numeric(22,4)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice','c_order_orig_id','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('t_cuentacorriente','IncludeCreditNotesRequest','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20191018-1303 Los codigos MiPyME requieren al menos una longitud de 3. Se amplia a un valor mayor para soportar eventuales nuevos codigos de mayor longitud
alter table c_doctype alter column docsubtypecae type varchar(10);

--20191021-1400 Facturas de crédito
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','emitir_mi_pyme','character(1) NOT NULL DEFAULT ''N''::bpchar'));

CREATE TABLE i_padron_mipyme
(
  i_padron_mipyme_id integer NOT NULL,
  ad_client_id integer,
  ad_org_id integer,
  isactive character(1) DEFAULT 'Y'::bpchar,
  created timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer,
  updated timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer,
  i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
  i_errormsg character varying(2000),
  processing character(1),
  cuit character varying(20),
  denominacion character varying(60),
  actividad integer,
  fecha_inicio date,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT i_padron_mipyme_key PRIMARY KEY (i_padron_mipyme_id)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE i_padron_mipyme
  OWNER TO libertya;
  
--20191023-1225 Se exportan todas las letras de comprobantes de proveedores
CREATE OR REPLACE VIEW reginfo_compras_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar 
	AND (CASE WHEN i.issotrx = 'N' THEN i.docstatus IN ('CO','CL') ELSE i.docstatus IN ('CO','CL','VO','RE','??') END)
	AND ((i.issotrx = 'N' AND dt.transactiontypefrontliva is null) OR dt.transactiontypefrontliva = 'P')
	AND i.isactive = 'Y'::bpchar 
	AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
	AND dt.isfiscaldocument = 'Y'::bpchar 
	AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) 
	AND (t.rate > 0::numeric AND it.taxamt > 0::numeric OR t.rate = 0::numeric AND it.taxamt = 0::numeric);

ALTER TABLE reginfo_compras_alicuotas_v
  OWNER TO libertya;

--20191024-1610 Importación de Liquidaciones Cabal (CentralPOS)
CREATE SEQUENCE seq_i_cabalpayments
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000001
  CACHE 1;
ALTER TABLE seq_i_cabalpayments
  OWNER TO libertya;

CREATE TABLE i_cabalpayments
(
  i_cabalpayments_id integer NOT NULL DEFAULT nextval('seq_i_cabalpayments'::regclass),
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  i_errormsg character varying(2000),
  i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
  processing character(1),
  processed character(1) DEFAULT 'N'::bpchar,
  id character varying(32),
  archivo_id character varying(32),
  hash_modelo character varying(32),
  comercio character varying(32),
  numero_comercio character varying(32),
  moneda_pago character varying(32),
  importe_venta character varying(32),
  signo_importe_bruto character varying(32),
  importe_arancel character varying(32),
  signo_importe_arancel character varying(32),
  importe_iva_arancel character varying(32),
  signo_iva_sobre_arancel character varying(32),
  retencion_iva character varying(32),
  signo_retencion_iva character varying(32),
  retencion_ganancias character varying(32),
  signo_retencion_ganancias character varying(32),
  retencion_ingresos_brutos character varying(32),
  signo_retencion_ingresos_brutos character varying(32),
  percepcion_rg_3337 character varying(32),
  signo_percepcion_3337 character varying(32),
  importe_neto_final character varying(32),
  signo_importe_neto_final character varying(32),
  fecha_pago character varying(32),
  numero_liquidacion character varying(32),
  revisado character varying(32),
  costo_fin_cup character varying(32),
  CONSTRAINT cabalpayments_key PRIMARY KEY (i_cabalpayments_id),
  CONSTRAINT cabalpaymentsclient FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cabalpaymentsorg FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE i_cabalpayments
  OWNER TO libertya;
  
--20191101-1155 Incremento de tamaño de la columna balance ajustado
ALTER TABLE T_ACCT_Balance ALTER COLUMN balanceadjusted TYPE numeric(22,2);

--20191101-1830 Nuevas columnas para registrar el debe y haber ajustado por inflación
update ad_system set dummy = (SELECT addcolumnifnotexists('T_ACCT_Balance','debitadjusted','numeric(22,2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('T_ACCT_Balance','creditadjusted','numeric(22,2)'));

--20191120-115900 Tabla de prefijo de microcomponentes
CREATE TABLE ad_microcomponentprefix
(
  ad_microcomponentprefix_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  prefix varchar(10) NOT NULL,
  description varchar(2000) NOT NULL
);
ALTER TABLE ad_microcomponentprefix ADD CONSTRAINT microcomponent_key PRIMARY KEY (ad_microcomponentprefix_id);
ALTER TABLE ad_microcomponentprefix ADD CONSTRAINT microcomponent_prefix UNIQUE (prefix);

--20191420-141208 Ampliacion de prefijo en AD_Component
drop view ad_changelog_dev;
 
alter table ad_component alter column prefix type varchar(10);

-- Vista detallada de changelog (orientada al desarrollo de plugins)
CREATE OR REPLACE VIEW ad_changelog_dev AS 
	SELECT
		g.ad_changelog_id, 
		c.name AS client, 
		o.name AS organization, 
		g.isactive, 
		g.created,
		uc.name AS createdbyuser,
		g.updated, 
		uu.name AS updatedbyuser, 
		changeloggroup_id,
		operationtype,
		t.tablename,
		l.columnname,
		g.record_id,
		g.ad_componentobjectuid,
		g.oldvalue,
		g.newvalue,
		g.binaryvalue,
		p.prefix AS componentprefix,
		p.publicname AS componentname,
		v.version AS componentversion,
		g.ad_componentversion_id,
		g.createdby,
		g.updatedby,
		g.ad_session_id,
		g.ad_table_id,
		g.ad_column_id,
		g.iscustomization,
		g.redo,
		g.undo,
		g.trxname
	FROM ad_changelog g
	INNER JOIN ad_client c ON (g.ad_client_id = c.ad_client_id)
	INNER JOIN ad_org o ON (g.ad_org_id = o.ad_org_id)
	INNER JOIN ad_user uc ON (g.createdby = uc.ad_user_id)
	INNER JOIN ad_user uu ON (g.updatedby = uu.ad_user_id)
	INNER JOIN ad_table t ON (g.ad_table_id = t.ad_table_id)
	INNER JOIN ad_column l ON (g.ad_column_id = l.ad_column_id)
	INNER JOIN ad_componentversion v ON (g.ad_componentversion_id = v.ad_componentversion_id)
	INNER JOIN ad_component p ON (v.ad_component_id = p.ad_component_id)
	ORDER BY created DESC, changeloggroup_id DESC, ad_changelog_id DESC;

-- Nueva columna en ad_component para determinar si el componente a desarrollar es un micro componente
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_component','ismicrocomponent','character(1) NOT NULL DEFAULT ''N''::bpchar'));

-- Nueva columna en ad_changelog para almacenar el changelogUID 
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_changelog','changeloguid','varchar(100)'));
-- Nueva columna en ad_changelog para almacenar el changelogGroupUID 
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_changelog','changeloggroupuid','varchar(100)'));

-- Nuevas columnas para almacenar los first/last changeLogUID / changeLogGroupUID
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin','component_first_changelog_uid','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin','component_last_changelog_uid','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin','component_first_changelog_group_uid','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin','component_last_changelog_group_uid','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin_detail','component_first_changelog_uid','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin_detail','component_last_changelog_uid','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin_detail','component_first_changelog_group_uid','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_plugin_detail','component_last_changelog_group_uid','varchar(100)'));

-- 20191228-1238 Nueva funcion para bajar registros de replicacion 
/** Deshabilita registros pendientes a replicar si los mismos tienen UNICAMENTE como pendiente el host p_hostpos, 
    bajando a 0 la posicion en el repArray y cambiando el campo includeinreplication a N */
CREATE OR REPLACE FUNCTION replication_disable_records_for_host(p_hostpos int)
  RETURNS INTEGER AS
$BODY$
DECLARE
atable VARCHAR;
query VARCHAR;
cant int;
whereclause VARCHAR;
totalrecords int;
BEGIN
	totalrecords = 0;

	-- solo registros que tienen pendiente unicamente el host p_hostpos
	whereclause =   ' WHERE includeinreplication = ''Y'' ' ||
			' AND substring(reparray from ' || p_hostpos || ' for 1)  IN (''1'', ''3'', ''A'', ''a'') ' ||	-- la posicion buscada debe ser uno de los estados de replicacion pendiente
			' AND char_length(replace(replace(reparray, ''2'', ''''), ''0'', '''')) = 1 '; 			-- las demas posiciones deben estar replicadas o bien sin replicacion configurada


    FOR atable IN (    
		-- iterar por todas las tablas configuradas en replicacion
		SELECT lower(t.tablename)
		FROM ad_tablereplication tr
		INNER JOIN ad_table t ON tr.ad_table_id = t.ad_table_id
		UNION
		SELECT 'ad_changelog_replication'
	)
	LOOP
		-- si no hay registros que cumplen la condicion, omitir
		EXECUTE 'select count(1) from ' || atable || whereclause INTO cant;
		if cant <= 0 THEN
			continue;
		END IF;

		-- notificar el numero de registros a bajar
		totalrecords = totalrecords + cant;
		raise notice '% : %', atable, cant;
		query =    ' UPDATE ' || atable || ' SET reparray = ';
		if atable <> 'ad_changelog_replication' then
		    query = query || '''SET''||';
		end if;
		query = query ||'overlay(reparray placing ''0'' from ' || p_hostpos || ' for 1), includeinreplication = ''N'' ' || whereclause;
			
		--raise notice '%', query;
		EXECUTE query;
	END LOOP;
	return totalrecords;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
  
  
-- 20191203-094501 Incorporacion de columnas changeloguid y changeloggroupuid a la vista
drop view ad_changelog_dev;
CREATE OR REPLACE VIEW ad_changelog_dev AS 
	SELECT
		g.ad_changelog_id, 
		g.changeloggroup_id,
		g.changeloguid,
		g.changeloggroupuid,
		c.name AS client, 
		o.name AS organization, 
		g.isactive, 
		g.created,
		uc.name AS createdbyuser,
		g.updated, 
		uu.name AS updatedbyuser, 
		operationtype,
		t.tablename,
		l.columnname,
		g.record_id,
		g.ad_componentobjectuid,
		g.oldvalue,
		g.newvalue,
		g.binaryvalue,
		p.prefix AS componentprefix,
		p.publicname AS componentname,
		v.version AS componentversion,
		g.ad_componentversion_id,
		g.createdby,
		g.updatedby,
		g.ad_session_id,
		g.ad_table_id,
		g.ad_column_id,
		g.iscustomization,
		g.redo,
		g.undo,
		g.trxname
	FROM ad_changelog g
	INNER JOIN ad_client c ON (g.ad_client_id = c.ad_client_id)
	INNER JOIN ad_org o ON (g.ad_org_id = o.ad_org_id)
	INNER JOIN ad_user uc ON (g.createdby = uc.ad_user_id)
	INNER JOIN ad_user uu ON (g.updatedby = uu.ad_user_id)
	INNER JOIN ad_table t ON (g.ad_table_id = t.ad_table_id)
	INNER JOIN ad_column l ON (g.ad_column_id = l.ad_column_id)
	INNER JOIN ad_componentversion v ON (g.ad_componentversion_id = v.ad_componentversion_id)
	INNER JOIN ad_component p ON (v.ad_component_id = p.ad_component_id)
	ORDER BY created DESC, changeloggroup_id DESC, ad_changelog_id DESC;

-- Incorporacion de nuevas columnas component_first_changelog_uid, component_last_changelog_uid, component_first_changelog_group_uid, component_last_changelog_group_uid a la vista
drop view ad_plugin_v;
CREATE OR REPLACE VIEW ad_plugin_v AS 
 SELECT 	cv.name, 
		cv.ad_componentobjectuid, 
		cv.version, 
		p.created, 
		p.updated, 
		p.createdby, 
		p.updatedby, 
		p.component_export_date as export_date, 
		p.component_last_changelog as last_changelog, 
		p.component_first_changelog_uid as first_changelog_uid, 
		p.component_last_changelog_uid as last_changelog_uid, 
		p.component_first_changelog_group_uid as first_changelog_group_uid, 
		p.component_last_changelog_group_uid as last_changelog_group_uid
   FROM ad_plugin p 
   LEFT JOIN ad_componentversion cv ON p.ad_componentversion_id = cv.ad_componentversion_id
  ORDER BY p.created;
  
-- Incorporacion de las mismas nuevas columnas a la vista de detalle  
drop view ad_plugin_detail_v;
create view ad_plugin_detail_v as
select 	cv.name, 
	cv.ad_componentobjectuid, 
	pd.version, 
	pd.created, 
	pd.createdby,
	pd.component_export_date as export_date, 
	pd.component_first_changelog as first_changelog,
	pd.component_last_changelog as last_changelog, 
	pd.component_first_changelog_uid as first_changelog_uid, 
	pd.component_last_changelog_uid as last_changelog_uid, 
	pd.component_first_changelog_group_uid as first_changelog_group_uid, 
	pd.component_last_changelog_group_uid as last_changelog_group_uid,
	pd.install_details
from ad_plugin p
inner join ad_plugin_detail pd on p.ad_plugin_id = pd.ad_plugin_id
left join ad_componentversion cv on p.ad_componentversion_id = cv.ad_componentversion_id
order by pd.created asc;

--20191205-1235 Nueva columna con el tipo de documento
CREATE OR REPLACE VIEW c_paymentcoupon_v AS 
 SELECT p.c_payment_id,
    p.ad_client_id,
    p.ad_org_id,
    p.created,
    p.createdby,
    p.updated,
    p.updatedby,
    'Y'::character(1) AS isactive,
    efp.m_entidadfinanciera_id,
    p.m_entidadfinancieraplan_id,
    ccs.settlementno,
    p.c_invoice_id,
    p.creditcardnumber,
    p.couponnumber,
    p.c_bpartner_id,
    COALESCE(p.a_name, bp.name) AS a_name,
    p.datetrx,
    p.couponbatchnumber,
    p.payamt,
    p.c_currency_id,
    p.docstatus,
    cs.isreconciled,
    efp.cuotaspago AS totalallocations,
    ccs.paymentdate AS settlementdate,
    p.auditstatus,
    ''::character(1) AS reject,
    ''::character(1) AS unreject,
    ef.c_bpartner_id AS m_entidadfinanciera_bp_id,
    p.c_doctype_id
   FROM c_payment p
     JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
     LEFT JOIN m_entidadfinancieraplan efp ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id
     JOIN m_entidadfinanciera ef ON efp.m_entidadfinanciera_id = ef.m_entidadfinanciera_id
     LEFT JOIN c_couponssettlements cs ON p.c_payment_id = cs.c_payment_id
     LEFT JOIN c_creditcardsettlement ccs ON cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id
  WHERE p.tendertype = 'C'::bpchar;

ALTER TABLE c_paymentcoupon_v
  OWNER TO libertya;
  
--20191205-1235 Columna Procesado para los cierres fiscales
update ad_system set dummy = (SELECT addcolumnifnotexists('c_controlador_fiscal_closing_info','processed','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20191205-1235 Responsable de ventas por línea
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoiceline','salesrep_orig_id','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','salesrep_orig_id','integer'));

--View para el informe de Ventas por Vendedor
CREATE OR REPLACE VIEW c_invoice_sales_rep_orig_v AS 
 SELECT i.ad_client_id,
    i.ad_org_id,
    i.c_invoice_id,
    i.documentno,
    i.dateacct::date AS dateacct,
    i.dateinvoiced,
    i.grandtotal,
    il.salesrep_orig_id,
    u.name AS salesrep_name,
    sum(currencybase(il.linenetamt, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id))::numeric(20,2) AS salesrep_invoice_amt
   FROM c_invoiceline il
     JOIN c_invoice i ON i.c_invoice_id = il.c_invoice_id
     JOIN ad_user u ON u.ad_user_id = il.salesrep_orig_id
     JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
  WHERE (i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND dt.docbasetype = 'ARI'::bpchar AND dt.doctypekey::text !~~* 'CDN%'::text AND
        CASE
            WHEN 'Y'::text = ((( SELECT ad_preference.value
               FROM ad_preference
              WHERE ad_preference.attribute::text = 'LOCAL_AR'::text))::text) THEN dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND (dt.iselectronic IS NULL OR dt.iselectronic::text = 'N'::text OR dt.iselectronic::text = 'Y'::text AND i.cae IS NOT NULL)
            ELSE true
        END
  GROUP BY i.ad_client_id, i.ad_org_id, i.c_invoice_id, i.documentno, (i.dateacct::date), i.dateinvoiced, i.grandtotal, il.salesrep_orig_id, u.name;

ALTER TABLE c_invoice_sales_rep_orig_v
  OWNER TO libertya;

--20191218-1300 Nuevas columnas para registro del IVA 10.5 sobre importaciones Cabal CentralPOS
update ad_system set dummy = (SELECT addcolumnifnotexists('i_cabalpayments','iva_cf_alicuota_10_5','character varying(32)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('i_cabalpayments','signo_iva_cf_alicuota_10_5','character varying(32)'));

--20191219-1100 Views para exportaciones de percepciones y retenciones sufridas (SIFERE)
DROP VIEW rv_c_tax_iibb_sufridas;
 
CREATE OR REPLACE VIEW rv_c_tax_iibb_sufridas AS 
 SELECT it.ad_org_id,
    it.ad_client_id,
    COALESCE(rt.jurisdictioncode, r.jurisdictioncode) AS codigojurisdiccion,
    replace(bp.taxid::text, '-'::text, ''::text) AS cuit,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script,
    i.dateacct AS date,
    i.puntodeventa,
    lpad(i.puntodeventa::character varying::text, 4, '0'::text) AS numerodesucursal,
    i.numerocomprobante AS numerodeconstancia,
        CASE
            WHEN dt.doctypekey::text ~~ 'VCDN%'::text AND dt.iselectronic::text = 'N'::text THEN 'D'::bpchar
            WHEN dt.doctypekey::text ~~ 'VI%'::text AND dt.iselectronic::text = 'N'::text THEN 'F'::bpchar
            WHEN dt.doctypekey::text ~~ 'VCM%'::text AND dt.iselectronic::text = 'N'::text THEN 'C'::bpchar
            WHEN dt.doctypekey::text ~~ 'VCDN%'::text AND dt.iselectronic::text = 'Y'::text THEN 'I'::bpchar
            WHEN dt.doctypekey::text ~~ 'VI%'::text AND dt.iselectronic::text = 'Y'::text THEN 'E'::bpchar
            WHEN dt.doctypekey::text ~~ 'VCM%'::text AND dt.iselectronic::text = 'Y'::text THEN 'H'::bpchar
            ELSE 'O'::bpchar
        END AS tipocomprobante,
    lc.letra AS letracomprobante,
    (it.taxamt * dt.signo_issotrx::numeric * '-1'::integer::numeric)::numeric(20,2) AS importepercibido
   FROM c_invoicetax it
     JOIN c_tax t ON t.c_tax_id = it.c_tax_id
     JOIN c_invoice i ON it.c_invoice_id = i.c_invoice_id
     JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
     JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
     JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
     LEFT JOIN c_region r ON r.c_region_id = i.c_region_delivery_id
     LEFT JOIN c_region rt ON rt.c_region_id = t.c_region_id
  WHERE (dt.docbasetype = ANY (ARRAY['API'::bpchar, 'APC'::bpchar])) AND (i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND t.ispercepcion = 'Y'::bpchar AND t.perceptiontype = 'B'::bpchar;

ALTER TABLE rv_c_tax_iibb_sufridas
  OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_reten_iibb_sufridas AS 
 SELECT i.ad_org_id,
    i.ad_client_id,
    r.jurisdictioncode AS codigojurisdiccion,
    replace(bp.taxid::text, '-'::text, ''::text) AS cuit,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script,
    i.dateacct AS date,
    i.puntodeventa,
    lpad(i.puntodeventa::character varying::text, 4, '0'::text) AS numerodesucursal,
    i.numerocomprobante AS numerodeconstancia,
        CASE
            WHEN dt.doctypekey::text ~~ 'CDN%'::text AND dt.iselectronic::text = 'N'::text THEN 'D'::bpchar
            WHEN dt.doctypekey::text ~~ 'CI%'::text AND dt.iselectronic::text = 'N'::text THEN 'F'::bpchar
            WHEN dt.doctypekey::text ~~ 'CCN%'::text AND dt.iselectronic::text = 'N'::text THEN 'C'::bpchar
            WHEN dt.doctypekey::text ~~ 'CDN%'::text AND dt.iselectronic::text = 'Y'::text THEN 'I'::bpchar
            WHEN dt.doctypekey::text ~~ 'CI%'::text AND dt.iselectronic::text = 'Y'::text THEN 'E'::bpchar
            WHEN dt.doctypekey::text ~~ 'CCN%'::text AND dt.iselectronic::text = 'Y'::text THEN 'H'::bpchar
            ELSE 'O'::bpchar
        END AS tipocomprobante,
    lc.letra AS letracomprobante,
    (i.grandtotal * dt.signo_issotrx::numeric)::numeric(20,2) AS importeretenido,
    COALESCE(src.documentno, ( SELECT ip.documentno
           FROM c_allocationline al
             JOIN c_invoice ip ON ip.c_invoice_id = al.c_invoice_id
          WHERE al.c_allocationhdr_id = ri.c_allocationhdr_id
          ORDER BY al.created
         LIMIT 1)) AS orig_invoice_documentno,
    i.documentno
   FROM m_retencion_invoice ri
     JOIN c_invoice i ON i.c_invoice_id = ri.c_invoice_id
     JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
     JOIN c_retencionschema rs ON rs.c_retencionschema_id = ri.c_retencionschema_id
     JOIN c_retenciontype rt ON rt.c_retenciontype_id = rs.c_retenciontype_id
     JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
     LEFT JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
     LEFT JOIN c_region r ON r.c_region_id = rs.c_region_id
     LEFT JOIN c_invoice src ON src.c_invoice_id = ri.c_invoice_src_id
  WHERE rt.retentiontype = 'B'::bpchar AND rs.retencionapplication::text = 'S'::text AND (i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]));

ALTER TABLE rv_c_reten_iibb_sufridas
  OWNER TO libertya;
  
--20200115-1427 Function y soporte para visualizar log de AD_Changelog (usado en bitacora para auditoria)
-- Ejemplos de uso:
-- 		select * from v_audit_detail('where createdby = 101', 'order by ad_changelog_id desc', 'limit 18')
-- 		select * from v_audit_detail('where operationtype = ''M''', 'order by ad_changelog_id desc', 'limit 200')

-- Funcion que referencia al tipo
DROP FUNCTION IF EXISTS v_audit_detail(varchar, varchar, varchar);
-- Nuevo tipo para la funcion
DROP TYPE IF EXISTS audit_detail_type ;

 CREATE TYPE audit_detail_type AS (
	fecha    timestamp,
	usuario  varchar,
	registro varchar,
	campo    varchar,
	valor_previo varchar,
	valor_nuevo varchar

);


CREATE OR REPLACE FUNCTION get_record_reference(recordid int, tableid int)
RETURNS varchar AS
$BODY$
DECLARE
	atablename varchar;
	identifiercols varchar;
	aresult varchar;
	aquery varchar;
BEGIN
	BEGIN
		-- Determinar el nombre de la tabla
		SELECT INTO atablename tablename FROM AD_Table WHERE AD_Table_ID = tableid;

		-- Buscar los campos identificatorios
		select INTO identifiercols array_to_string(array_agg(columnname),'||''-''||') from ad_column where isidentifier = 'Y' AND ad_table_id = tableid;
		EXECUTE 'SELECT (' || identifiercols || ') FROM ' || atablename || ' WHERE ' || atablename  || '_ID = ' || recordid INTO aresult;
		
		return aresult;
	EXCEPTION 
		WHEN OTHERS THEN return recordid;
	END;
END
$BODY$
  LANGUAGE plpgsql VOLATILE;
ALTER FUNCTION get_record_reference(int, int)
  OWNER TO libertya;



CREATE OR REPLACE FUNCTION get_column_reference(avalue varchar, columnid int)
RETURNS varchar AS
$BODY$
DECLARE
	acolumn record;
	areference record;
	areftable record;
	areflist record;
	aquery varchar;
	aresult varchar;
	ref_info record;
	ref_table varchar;
	ref_column_key varchar;
	ref_column_val varchar;
	identifiercols varchar;
BEGIN
	BEGIN
		-- Determinar el tipo de columna para saber si es una referencia
		SELECT INTO acolumn * FROM AD_Column WHERE AD_Column_ID = columnid;

		-- Si no es una referencia, entonces devolver el valor directamente
		IF (acolumn.ad_reference_ID NOT IN (17, 18, 19, 30)) THEN
			return avalue;
		END IF;

		-- Si hay una referencia explicita, entonces usar dicha configuracion
		IF (acolumn.ad_reference_value_id IS NOT NULL) THEN
			-- Que tipo de referencia es? (lista, tabla)
			SELECT INTO areference * FROM AD_Reference WHERE ad_reference_id = acolumn.ad_reference_value_id;
			-- tabla?
			IF (areference.validationtype = 'T') THEN 
				-- Recuperar el ad_ref_table
				SELECT INTO areftable * FROM AD_Ref_Table WHERE AD_Reference_ID = areference.AD_reference_id;
				-- Armar el query para determinar el valor
				SELECT INTO ref_table tablename FROM AD_Table WHERE ad_table_id = areftable.ad_table_id;
				SELECT INTO ref_column_key columnname FROM AD_Column WHERE ad_column_id = areftable.ad_key;
				SELECT INTO ref_column_val columnname FROM AD_Column WHERE ad_column_id = areftable.ad_display;
				EXECUTE 'SELECT ' || ref_column_val || ' FROM ' || ref_table || ' WHERE ' || ref_column_key || ' = ' || avalue INTO aresult;
				return aresult;
			END IF;
			-- lista?
			IF (areference.validationtype = 'L') THEN 
				raise notice 'ES L';
				-- Recuperar el valor correcto de la ad_ref_list_trl
				SELECT INTO aresult rlt.name FROM AD_Ref_List rl INNER JOIN AD_Ref_List_Trl rlt ON rl.ad_ref_list_id = rlt.ad_ref_list_id WHERE AD_Reference_ID = areference.AD_Reference_id AND rl.value = avalue AND ad_language = 'es_AR';
				return aresult;
			END IF;
		-- No hay una referencia explicita, debe ser tabledir / busqueda		
		ELSE 
			-- Buscar la tabla referenciada (por ejemplo C_Invoice_ID -> C_Invoice)
			SELECT INTO ref_table substring(acolumn.columnname from 1 for (length(acolumn.columnname))-3) FROM AD_Column WHERE AD_Column_ID = columnid;
			-- Buscar los campos identificatorios
			select INTO identifiercols array_to_string(array_agg(columnname),'||''-''||') from ad_column where isidentifier = 'Y' AND ad_table_id = (Select ad_table_id from ad_table where tablename = ref_table);
			EXECUTE 'SELECT (' || identifiercols || ') FROM ' || ref_table || ' WHERE ' || acolumn.columnname  || ' = ' || avalue INTO aresult;
			return aresult;	
		END IF;
	EXCEPTION 
		WHEN OTHERS THEN return avalue;
	END;
END
$BODY$
  LANGUAGE plpgsql VOLATILE;
ALTER FUNCTION get_column_reference(varchar, int)
  OWNER TO libertya;




CREATE OR REPLACE FUNCTION v_audit_detail(whereclause varchar, orderclause varchar, limitclause varchar)
  RETURNS SETOF audit_detail_type AS
$BODY$
declare
	consulta varchar;
	aRecord audit_detail_type;
BEGIN
	consulta = 
		' select updated::timestamp(0) as fecha, ' ||
		' (select name from ad_user where ad_user_id = createdby) as usuario, ' ||
		' get_record_reference(record_id::int, ad_table_id::int), ' ||
		' (select ct.name from ad_column c inner join ad_column_trl ct on c.ad_column_id = ct.ad_column_id and ad_language = ''es_AR'' where c.ad_column_id = cl.ad_column_id) as campo, ' ||
		' get_column_reference(oldvalue, ad_column_id) as valor_previo, ' ||
		' get_column_reference(newvalue, ad_column_id) as valor_nuevo,' ||
		' ad_column_id,' ||
		' ad_table_id' ||
		' from ad_changelog cl ' ||
		coalesce(whereclause, '') || ' ' ||
		coalesce(orderclause, '') || ' ' ||
		coalesce(limitclause, '');
	FOR aRecord IN EXECUTE consulta LOOP
		return next aRecord;
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION v_audit_detail(varchar, varchar, varchar)
  OWNER TO libertya;
  
--20200117-0900 Función para conversión de unidades de medida
CREATE OR REPLACE FUNCTION uom_conversion(
    product_id integer,
    uom_from_id integer,
    uom_to_id integer,
    qty numeric)
  RETURNS numeric AS
$BODY$
DECLARE
	qtyconverted numeric;
	rate numeric;
	ufi integer;
	uti integer;
BEGIN
	qtyconverted = null;
	ufi = uom_from_id;
	
	--Si alguno de los UOMs parámetro es null, entonces se toma el del artículo
	IF (uom_from_id is null) THEN
		-- Unidad de medida actual del artículo parámetro
		SELECT c_uom_id INTO ufi
		FROM m_product
		where m_product_id = product_id;
	END IF;

	uti = uom_to_id;
	IF (uom_to_id is null) THEN
		-- Unidad de medida actual del artículo parámetro
		SELECT c_uom_id INTO uti
		FROM m_product
		where m_product_id = product_id;
	END IF;

	IF (ufi <> uti)
	THEN 
		-- Buscar la conversión del artículo para las unidades de medida correspondientes
		select dividerate into rate
		from C_UOM_Conversion
		where m_product_id = product_id and c_uom_id = ufi and c_uom_to_id = uti and isactive = 'Y' 
		order by created desc 
		limit 1;

		IF (rate is null) THEN
			select multiplyrate into rate
			from C_UOM_Conversion
			where m_product_id = product_id and c_uom_id = uti and c_uom_to_id = ufi and isactive = 'Y' 
			order by created desc 
			limit 1;
		END IF;

		IF (rate is not null and rate <> 0) THEN
			qtyconverted = qty * rate;
		END IF;
		
	ELSE
		qtyconverted = qty;
	END IF;
	
        RETURN qtyconverted;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION uom_conversion(integer, integer, integer, numeric)
  OWNER TO libertya;

  
 --20200120-094511 Correcciones sobre funcion de visualizacion registros de auditoria
CREATE OR REPLACE FUNCTION get_column_reference(avalue varchar, columnid int)
RETURNS varchar AS
$BODY$
DECLARE
	acolumn record;
	areference record;
	areftable record;
	areflist record;
	aquery varchar;
	aresult varchar;
	ref_info record;
	ref_table varchar;
	ref_column_key varchar;
	ref_column_val varchar;
	identifiercols varchar;
BEGIN
	BEGIN
		-- Determinar el tipo de columna para saber si es una referencia
		SELECT INTO acolumn * FROM AD_Column WHERE AD_Column_ID = columnid;

		-- Si no es una referencia, entonces devolver el valor directamente
		IF (acolumn.ad_reference_ID NOT IN (17, 18, 19, 30)) THEN
			return avalue;
		END IF;

		-- Si hay una referencia explicita, entonces usar dicha configuracion
		IF (acolumn.ad_reference_value_id IS NOT NULL) THEN
			-- Que tipo de referencia es? (lista, tabla)
			SELECT INTO areference * FROM AD_Reference WHERE ad_reference_id = acolumn.ad_reference_value_id;
			-- tabla?
			IF (areference.validationtype = 'T') THEN 
				-- Recuperar el ad_ref_table
				SELECT INTO areftable * FROM AD_Ref_Table WHERE AD_Reference_ID = areference.AD_reference_id;
				-- Armar el query para determinar el valor
				SELECT INTO ref_table tablename FROM AD_Table WHERE ad_table_id = areftable.ad_table_id;
				SELECT INTO ref_column_key columnname FROM AD_Column WHERE ad_column_id = areftable.ad_key;
				SELECT INTO ref_column_val columnname FROM AD_Column WHERE ad_column_id = areftable.ad_display;
				EXECUTE 'SELECT ' || ref_column_val || ' FROM ' || ref_table || ' WHERE ' || ref_column_key || ' = ' || avalue INTO aresult;
				return aresult;
			END IF;
			-- lista?
			IF (areference.validationtype = 'L') THEN 
				-- Recuperar el valor correcto de la ad_ref_list_trl
				SELECT INTO aresult rlt.name FROM AD_Ref_List rl INNER JOIN AD_Ref_List_Trl rlt ON rl.ad_ref_list_id = rlt.ad_ref_list_id WHERE AD_Reference_ID = areference.AD_Reference_id AND rl.value = avalue AND ad_language = 'es_AR';
				return aresult;
			END IF;
		-- No hay una referencia explicita, debe ser tabledir / busqueda		
		ELSE 
			-- Buscar la tabla referenciada (por ejemplo C_Invoice_ID -> C_Invoice)
			SELECT INTO ref_table substring(acolumn.columnname from 1 for (length(acolumn.columnname))-3) FROM AD_Column WHERE AD_Column_ID = columnid;
			-- Buscar los campos identificatorios
			select INTO identifiercols array_to_string(array_agg(columnname),'||''-''||') from ad_column where isidentifier = 'Y' AND ad_table_id = (Select ad_table_id from ad_table where tablename = ref_table);
			EXECUTE 'SELECT (' || identifiercols || ') FROM ' || ref_table || ' WHERE ' || acolumn.columnname  || ' = ' || avalue INTO aresult;
			return aresult;	
		END IF;
	EXCEPTION 
		WHEN OTHERS THEN return avalue;
	END;
END
$BODY$
  LANGUAGE plpgsql VOLATILE;
ALTER FUNCTION get_column_reference(varchar, int)
  OWNER TO libertya;
 
 
 CREATE OR REPLACE FUNCTION v_audit_detail(whereclause varchar, orderclause varchar, limitclause varchar)
  RETURNS SETOF audit_detail_type AS
$BODY$
declare
	consulta varchar;
	aRecord audit_detail_type;
BEGIN
	consulta = 
		' select updated::timestamp(0) as fecha, ' ||
		' (select name from ad_user where ad_user_id = cl.updatedby) as usuario, ' ||
		' get_record_reference(record_id::int, ad_table_id::int), ' ||
		' (select ct.name from ad_column c inner join ad_column_trl ct on c.ad_column_id = ct.ad_column_id and ad_language = ''es_AR'' where c.ad_column_id = cl.ad_column_id) as campo, ' ||
		' get_column_reference(oldvalue, ad_column_id) as valor_previo, ' ||
		' get_column_reference(newvalue, ad_column_id) as valor_nuevo,' ||
		' ad_column_id,' ||
		' ad_table_id' ||
		' from ad_changelog cl ' ||
		coalesce(whereclause, '') || ' ' ||
		coalesce(orderclause, '') || ' ' ||
		coalesce(limitclause, '');
	FOR aRecord IN EXECUTE consulta LOOP
		return next aRecord;
	END LOOP;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION v_audit_detail(varchar, varchar, varchar)
  OWNER TO libertya;

--20200123-0850 
--ID 19204480
--Evento 3770 - ¿Cómo se deben informar las percepciones de IVA en el Registro de Ventas?
--27/04/2015 12:00:00 a.m.

--Dado que no se encuentra prevista una columna como en el Registro de Compras, 
--para consignar las percepciones del IVA, las mismas serán incluidas dentro de las percepciones nacionales. 
--Fuente: https://www.afip.gob.ar/genericos/guiavirtual/consultas_detalle.aspx?id=19204480 
CREATE OR REPLACE VIEW reginfo_ventas_cbte_v AS 
 SELECT i.ad_client_id,
	i.ad_org_id,
	i.c_invoice_id,
	date_trunc('day'::text, i.dateacct) AS date,
	date_trunc('day'::text, i.dateacct) AS fechadecomprobante,
	gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva) AS tipodecomprobante,
	i.puntodeventa,
	i.numerocomprobante AS nrocomprobante,
	i.numerocomprobante AS nrocomprobantehasta,
        CASE
            WHEN bp.taxidtype = '99'::bpchar AND i.grandtotal > 1000::numeric THEN '96'::bpchar
            ELSE bp.taxidtype
        END::character(2) AS codigodoccomprador,
	gettaxid(bp.taxid, bp.taxidtype, bp.c_categoria_iva_id, i.nroidentificcliente, i.grandtotal)::character varying(20) AS nroidentificacioncomprador,
	bp.name AS nombrecomprador,
	currencyconvert(getgrandtotal(i.c_invoice_id, true), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal,
	0::numeric(20,2) AS impconceptosnoneto,
	0::numeric(20,2) AS imppercepnocategorizados,
	currencyconvert(getimporteoperacionexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas,
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar) + gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac,
	currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb,
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni,
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos,
	cu.wsfecode AS codmoneda,
	currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio,
        CASE
            WHEN getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva,
	getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion,
	currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos,
	NULL::timestamp without time zone AS fechavencimientopago
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
  WHERE
CASE
    WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
    ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
END AND (i.issotrx = 'Y'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'S'::bpchar) AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_ventas_cbte_v
  OWNER TO libertya;
  
--20200124-1430 Para comprobantes de compras B o C no se exportan las alícuotas de IVA
CREATE OR REPLACE VIEW reginfo_compras_alicuotas_v AS 
 SELECT i.ad_client_id,
    i.ad_org_id,
    i.c_invoice_id,
    date_trunc('day'::text, i.dateacct) AS date,
    date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante,
    gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::character varying(15) AS tipodecomprobante,
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa,
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante,
    bp.taxidtype AS codigodocvendedor,
    bp.taxid AS nroidentificacionvendedor,
    currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado,
    t.wsfecode AS alicuotaiva,
    currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
     JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
     LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
     JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
     JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
     JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND
        CASE
            WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
            ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
        END AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) 
        AND i.isactive = 'Y'::bpchar 
        AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
        AND dt.isfiscaldocument = 'Y'::bpchar 
        AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) 
        AND l.letra NOT IN ('B', 'C')
		AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text
        AND (t.rate > 0::numeric AND it.taxamt > 0::numeric OR t.rate = 0::numeric AND it.taxamt = 0::numeric);

ALTER TABLE reginfo_compras_alicuotas_v
  OWNER TO libertya;
  
--20200128-1150 El valor del crédito fiscal computable para letras B y C debe ser 0
CREATE OR REPLACE VIEW reginfo_compras_cbte_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN i.importclearance
            ELSE NULL::character varying
        END::character varying(30) AS despachoimportacion, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, bp.name AS nombrevendedor, currencyconvert(getgrandtotal(i.c_invoice_id, true), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal, 0::numeric(20,2) AS impconceptosnoneto, 0::numeric AS impopeexentas, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosvaloragregado, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos, cu.wsfecode AS codmoneda, currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, 
        CASE
            WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0::numeric
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva, 
        getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, 
        (CASE
            WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0
	    ELSE currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)
	END)::numeric(20,2)  AS impcreditofiscalcomputable, 
	currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, 
	NULL::character varying(20) AS cuitemisorcorredor, NULL::character varying(60) AS denominacionemisorcorredor, 0::numeric(20,2) AS ivacomision
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE 
CASE
    WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
    ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
END AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_compras_cbte_v
  OWNER TO libertya;