-- Nueva tabla: Cierres de Almacenes
CREATE TABLE m_warehouse_close
(
  m_warehouse_close_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  docaction character(2) NOT NULL,
  docstatus character(2) NOT NULL,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  m_warehouse_id integer NOT NULL,
  datetrx date NOT NULL,
  description character varying(255),
  CONSTRAINT m_warehouse_close_pk PRIMARY KEY (m_warehouse_close_id),
  CONSTRAINT fk_client_warehouse_close FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_org_warehouse_close FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_warehouse_warehouse_close FOREIGN KEY (m_warehouse_id)
      REFERENCES m_warehouse (m_warehouse_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=TRUE);

-- Nuevo campo en la pestaña para mostrar mensajes processMsg en ventana Dialog 
ALTER TABLE ad_tab ADD COLUMN isprocessmsgshowdialog character(1) NOT NULL DEFAULT 'N'::bpchar;

-- Modificar la view para obtener este nuevo campo. Para este se debe eliminar y volver a crear
-- Eliminar
DROP VIEW ad_tab_v;
-- Recrear
CREATE OR REPLACE VIEW ad_tab_v AS 
	SELECT t.ad_tab_id, t.ad_window_id, t.ad_table_id, t.name, t.description, t.help, t.seqno, t.issinglerow, t.hastree, t.isinfotab, tbl.replicationtype, tbl.tablename, tbl.accesslevel, tbl.issecurityenabled, tbl.isdeleteable, tbl.ishighvolume, tbl.isview, 'N' AS hasassociation, t.istranslationtab, t.isreadonly, t.ad_image_id, t.tablevel, t.whereclause, t.orderbyclause, t.commitwarning, t.readonlylogic, t.displaylogic, t.ad_column_id, t.ad_process_id, t.issorttab, t.isinsertrecord, t.isadvancedtab, t.ad_columnsortorder_id, t.ad_columnsortyesno_id, t.included_tab_id, t.isprocessmsgshowdialog
	FROM ad_tab t
	JOIN ad_table tbl ON t.ad_table_id = tbl.ad_table_id
	WHERE t.isactive = 'Y'::bpchar AND tbl.isactive = 'Y'::bpchar;
	
-- Modificar la misma vista anterior, pero con las traducciones. Realizar las mismas operaciones anteriores
-- Eliminar 
DROP VIEW ad_tab_vt;
 -- Recrear
CREATE OR REPLACE VIEW ad_tab_vt AS 
	SELECT trl.ad_language, t.ad_tab_id, t.ad_window_id, t.ad_table_id, trl.name, trl.description, trl.help, t.seqno, t.issinglerow, t.hastree, t.isinfotab, tbl.replicationtype, tbl.tablename, tbl.accesslevel, tbl.issecurityenabled, tbl.isdeleteable, tbl.ishighvolume, tbl.isview, 'N' AS hasassociation, t.istranslationtab, t.isreadonly, t.ad_image_id, t.tablevel, t.whereclause, t.orderbyclause, trl.commitwarning, t.readonlylogic, t.displaylogic, t.ad_column_id, t.ad_process_id, t.issorttab, t.isinsertrecord, t.isadvancedtab, t.ad_columnsortorder_id, t.ad_columnsortyesno_id, t.included_tab_id, t.isprocessmsgshowdialog
	FROM ad_tab t
	JOIN ad_table tbl ON t.ad_table_id = tbl.ad_table_id
	JOIN ad_tab_trl trl ON t.ad_tab_id = trl.ad_tab_id
	WHERE t.isactive = 'Y'::bpchar AND tbl.isactive = 'Y'::bpchar;

-- Nuevo campo en la info de la compañía para llevar control de cierres de depósitos
ALTER TABLE ad_clientinfo ADD COLUMN iswarehouseclosecontrol character(1) NOT NULL DEFAULT 'N'::bpchar;
	
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

-- Nuevo campo en facturas: Crear Línea de Caja
ALTER TABLE c_invoice ADD COLUMN CreateCashLine character(1) NOT NULL DEFAULT 'Y'::bpchar;
	
-- Nueva tabla: Formatos de exportación
CREATE TABLE ad_electronicinvoiceformat
(
  ad_electronicinvoiceformat_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  nombreformato character varying(32)
);

-- Nueva tabla: Cabecera de archivos de formato
CREATE TABLE ad_electronicinvoiceformathdr
(
  ad_electronicinvoiceformathdr_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  expfilename character varying(50),
  distinto character varying(50),
  ad_electronicinvoiceformat_id integer NOT NULL,
  tipo character(1),
  iscondicional character(1) DEFAULT 'N'::bpchar,
  campo1 character varying(20),
  campo2 character varying(20)
);

-- Nueva tabla: Lineas del archivo de formato
CREATE TABLE ad_electronicinvoiceformatline
(
  ad_electronicinvoiceformatline_id integer NOT NULL,
  ad_electronicinvoiceformathdr_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  secuencia integer,
  longitud integer,
  relleno character(1),
  nombre_campo character varying(30),
  punto_decimal character(1),
  isleftalign character(1) DEFAULT 'N'::bpchar
);

-- Nueva tabla: Cabecera de facturas listas para la exportación
CREATE TABLE e_electronicinvoice
(
  e_electronicinvoice_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  dateinvoiced timestamp without time zone,
  tipo_comprobante integer,
  isfiscal character(1) DEFAULT 'Y'::bpchar,
  puntodeventa integer,
  numerodedocumento character varying(30),
  cant_hojas integer,
  doc_identificatorio_comprador integer,
  identif_comprador integer,
  "name" character varying(60),
  grandtotal numeric(20,2) DEFAULT 0,
  taxbaseamt numeric(20,2) DEFAULT 0,
  totallines numeric(20,2) DEFAULT 0,
  taxamt numeric(20,2) DEFAULT 0,
  tipo_responsable integer,
  cod_moneda character(3),
  multiplyrate numeric(24,6) DEFAULT 0,
  cant_alicuotas_iva integer,
  cod_operacion character(1),
  cai character varying(14),
  datecai timestamp without time zone,
  issotrx character(1),
  c_invoice_id integer,
  cuit character varying(128),
  numerocomprobante integer,
  nombrecli character varying(40),
  description character varying(255),
  cod_aduana integer,
  cod_destinacion character(4),
  doc_identificatorio_vendedor integer,
  identif_vendedor integer,
  cod_jurisdiccion_iibb integer,
  datevoid timestamp without time zone,
  nrodespacho integer,
  digverifnrodespacho character(1),
  fechadespachoplaza timestamp without time zone,
  rni numeric(20,2),
  operacionesexentas numeric(20,2),
  importepercepciones numeric(20,2),
  percepcionesiibb numeric(20,2),
  impuestosmunicipales numeric(20,2),
  impuestosinternos numeric(20,2),
  transporte numeric(20,2)
);

-- Nueva tabla: Lineas de facturas listas para la exportación
CREATE TABLE e_electronicinvoiceline
(
  e_electronicinvoiceline_id integer NOT NULL,
  e_electronicinvoice_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  unidad_medida character varying(2),
  linenetamt numeric(20,2) DEFAULT 0,
  importe_bonificacion numeric(20,2) DEFAULT 0,
  linetotalamt numeric(20,2) DEFAULT 0,
  rate numeric(24,6),
  istaxexempt character(1) DEFAULT 'N'::bpchar,
  indica_anulacion character(1),
  diseno_libre character varying(200),
  qtyinvoiced numeric(22,4)
);

-- Nueva tabla: Referencias para datos de AFIP y Libertya
CREATE TABLE e_electronicinvoiceref
(
  e_electronicinvoiceref_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  tabla_ref character varying(4),
  codigo character varying(15),
  descripcion character varying(30),
  desde timestamp without time zone,
  hasta timestamp without time zone,
  persona character varying(10),
  clave_busqueda character varying(50)
);

-- Nueva tabla: Tabla temporal donde se procesan todos los datos a exportar
CREATE TABLE t_electronicinvoice
(
  ad_pinstance_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  datetrx timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  dateinvoiced character varying(8),
  tipo_comprobante integer,
  fiscal character(1),
  puntodeventa integer,
  numerodedocumento character varying(30),
  cant_hojas integer,
  doc_identificatorio_comprador integer,
  identif_comprador integer,
  "name" character varying(60),
  taxbaseamt numeric(20,2),
  totallines numeric(20,2),
  taxamt numeric(20,2),
  tipo_responsable integer,
  cod_moneda character(3),
  multiplyrate numeric(24,6),
  cant_alicuotas_iva integer,
  cod_operacion character(1),
  cai character varying(14),
  datecai character varying(8),
  tipo character(1),
  c_invoice_id integer,
  cuit character varying(128),
  numerocomprobante integer,
  nombrecli character varying(40),
  description character varying(255),
  cod_aduana integer,
  cod_destinacion character(4),
  doc_identificatorio_vendedor integer,
  identif_vendedor integer,
  cod_jurisdiccion_iibb integer,
  t_electronicinvoice_id integer NOT NULL,
  factcantregtipo1 integer,
  linenetamt numeric(20,2),
  updatedby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  tiporeg1 integer,
  tiporeg2 integer,
  ventacantregtipo1 integer,
  compracantregtipo1 integer,
  datevoid character varying(8),
  unidad_medida character varying(2),
  factgrandtotal numeric(20,2),
  ventagrandtotal numeric(20,2),
  compragrandtotal numeric(20,2),
  qtyinvoiced numeric(22,4),
  nrodespacho integer,
  digverifnrodespacho character(1),
  fechadespachoplaza character varying(8),
  importe_bonificacion numeric(20,2),
  linetotalamt numeric(20,2),
  diseno_libre character varying(200),
  indica_anulacion character(1),
  periodo character varying(6),
  anio character varying(4),
  taxexempt character(1),
  rni numeric(20,2),
  operacionesexentas numeric(20,2),
  importepercepciones numeric(20,2),
  percepcionesiibb numeric(20,2),
  impuestosmunicipales numeric(20,2),
  impuestosinternos numeric(20,2),
  transporte numeric(20,2),
  grandtotal numeric(20,2),
  facttaxbaseamt numeric(20,2),
  ventataxbaseamt numeric(20,2),
  comprataxbaseamt numeric(20,2),
  facttotallines numeric(20,2),
  ventatotallines numeric(20,2),
  compratotallines numeric(20,2),
  facttaxamt numeric(20,2),
  ventataxamt numeric(20,2),
  comprataxamt numeric(20,2),
  factoperacionesexentas numeric(20,2),
  ventaoperacionesexentas numeric(20,2),
  compraoperacionesexentas numeric(20,2),
  factimportepercepciones numeric(20,2),
  ventaimportepercepciones numeric(20,2),
  compraimportepercepciones numeric(20,2),
  factpercepcionesiibb numeric(20,2),
  ventapercepcionesiibb numeric(20,2),
  comprapercepcionesiibb numeric(20,2),
  factimpuestosmunicipales numeric(20,2),
  ventaimpuestosmunicipales numeric(20,2),
  compraimpuestosmunicipales numeric(20,2),
  factimpuestosinternos numeric(20,2),
  ventaimpuestosinternos numeric(20,2),
  compraimpuestosinternos numeric(20,2),
  rate numeric(24,6),
  factrni numeric(20,2),
  ventarni numeric(20,2),
  comprarni numeric(20,2),
  jurimpuestosmunicipales character varying(40)
);

-- Nuevo Campo agregado
ALTER TABLE e_electronicinvoice ADD COLUMN jurimpuestosmunicipales character varying(40);

-- Nuevos campos para poder bitacorear las nuevas entradas
ALTER TABLE ad_electronicinvoiceformat ADD COLUMN ad_componentobjectuid character varying(100);
ALTER TABLE ad_electronicinvoiceformathdr ADD COLUMN ad_componentobjectuid character varying(100);
ALTER TABLE ad_electronicinvoiceformatline ADD COLUMN ad_componentobjectuid character varying(100);
ALTER TABLE e_electronicinvoiceref ADD COLUMN ad_componentobjectuid character varying(100);
