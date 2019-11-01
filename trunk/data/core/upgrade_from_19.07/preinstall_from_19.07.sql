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