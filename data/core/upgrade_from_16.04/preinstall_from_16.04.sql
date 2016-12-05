-- ========================================================================================
-- PREINSTALL FROM 16.04
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20160523-1115 Funcionalidad de Listas de Bancos - Pagos electrónicos Galicia y Patagonia
CREATE TABLE  c_banklist (
  c_banklist_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_doctype_id integer NOT NULL,
  documentno character varying(30) NOT NULL,
  description character varying(255),
  datetrx timestamp without time zone NOT NULL,
  docstatus character(2) NOT NULL,
  docaction character(2) NOT NULL,
  isapproved character(1) NOT NULL DEFAULT 'N'::bpchar,
  processing character(1),
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  c_allocationhdr_id integer,
  dailyseqno numeric(18,0) NOT NULL DEFAULT 0,
  totalseqno numeric(18,0) NOT NULL DEFAULT 0,
  generatelist character(1),
  exportlist character(1),
  CONSTRAINT c_banklist_key PRIMARY KEY (c_banklist_id),
  CONSTRAINT banklist_allocationhdr FOREIGN KEY (c_allocationhdr_id)
      REFERENCES c_allocationhdr (c_allocationhdr_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT banklist_doctype FOREIGN KEY (c_doctype_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_banklist
  OWNER TO libertya;

CREATE TABLE  c_banklistline (
  c_banklistline_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_banklist_id integer NOT NULL,
  c_payment_id integer NOT NULL,
  line numeric(18,0) NOT NULL,
  description character varying(255),
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT c_banklistline_key PRIMARY KEY (c_banklistline_id),
  CONSTRAINT banklistline_checklist FOREIGN KEY (c_banklist_id)
      REFERENCES c_banklist (c_banklist_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT banklistline_payment FOREIGN KEY (c_payment_id)
      REFERENCES c_payment (c_payment_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_banklistline
  OWNER TO libertya;

ALTER TABLE c_doctype ADD COLUMN c_banklist_bank_id integer;

CREATE TABLE i_lista_galicia (
i_lista_galicia_id integer NOT NULL,
ad_client_id integer NOT NULL,
ad_org_id integer NOT NULL,
created	timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
createdby integer NOT NULL,
updated	timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
updatedby integer NOT NULL,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
i_errormsg character varying(2000),
i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
processing character(1),
processed character(1) DEFAULT 'N'::bpchar,
codigo character varying(2),
posicion character varying(3),
numero character varying(10),
estado character varying(2),
proveedor character varying(50),
cuit character varying(11),
vacio2 character varying(17),
direccion character varying(30),
localidad character varying(20),
cp character varying(6),
recibo character varying(15),
tipo_recibo character varying(2),
orden_de_pago character varying(10),
retirante character varying(30),
tipo_documento character varying(3),
documentno character varying(8),
condicion character(1),
sucursal character varying(3),
monto numeric(22,2),
lista character varying(8),
fecha_recepcion date,
fecha_emision date,
fecha_modificacion date,
fecha_vencimiento date,
referencia character varying(10),
comision char(1),
cuenta_especifica character varying(14),
fecha_pago date,
relleno	char(1),
vacio1 character varying(10),
CONSTRAINT i_lista_galicia_key PRIMARY KEY (i_lista_galicia_id)
);
ALTER TABLE i_lista_galicia
  OWNER TO libertya;

CREATE TABLE i_lista_patagonia (
i_lista_patagonia_id integer NOT NULL,
ad_client_id integer NOT NULL,
ad_org_id integer NOT NULL,
created	timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
createdby integer NOT NULL,
updated	timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
updatedby integer NOT NULL,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
i_errormsg character varying(2000),
i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
processing character(1),
processed character(1) DEFAULT 'N'::bpchar,
c_payment_id integer,
op_ref character varying(25),
beneficiario character varying(30),
f_emision date,
f_alta date,
f_vto_cpd date,
importe	numeric(22,2),
moneda character varying(3),
nro_chq_usado character varying(15),
nro_chq_reemp character varying(15),
motivo_reemp char(1),
CONSTRAINT i_lista_patagonia_key PRIMARY KEY (i_lista_patagonia_id)
);
ALTER TABLE i_lista_patagonia
  OWNER TO libertya;

CREATE TABLE i_lista_patagonia_novedades (
i_lista_patagonia_novedades_id integer not null,
ad_client_id integer NOT NULL,
ad_org_id integer NOT NULL,
created	timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
createdby integer NOT NULL,
updated	timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
updatedby integer NOT NULL,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
i_errormsg character varying(2000),
i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
processing character(1),
processed character(1) DEFAULT 'N'::bpchar,
constante character varying(2),
registro character varying(255),
fh_idarchivo character varying(20),
fh_horacreacion	character varying(6),
fh_nrosecuencial character varying(7),
fh_identificacion character varying(11),
fh_fechaproceso character varying(8),
fh_ainformante character varying(3),
fh_nroinformante character varying(7),
n1_idarchivorecibido character varying(20),
n1_referenciapago character varying(25),
n1_nropagosistema character varying(8),
n1_subnropago character varying(3),
n1_nroinstrumento character varying(15),
qn_nropagosistema character varying(8),
qn_subnropago character varying(3),
qn_estadopago character varying(60),
qn_eventos character varying(60),
t1_totalregpago character varying(10),
ft_totalarchivo character varying(10),
filename character varying(100),
CONSTRAINT i_lista_patagonia_novedades_key PRIMARY KEY (i_lista_patagonia_novedades_id)
);
ALTER TABLE i_lista_patagonia_novedades
  OWNER TO libertya;

CREATE TABLE c_banklist_config (
  c_banklist_config_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_doctype_id integer NOT NULL,
  clientacronym character varying(10),
  clientname character varying(40),
  registernumber character varying(60),
  sucursaldefault character varying(10),
  CONSTRAINT c_banklist_config_key PRIMARY KEY (c_banklist_config_id)
);
ALTER TABLE c_banklist_config
  OWNER TO libertya;

CREATE TABLE c_bpartner_banklist (
  c_bpartner_banklist_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_doctype_id integer NOT NULL,
  c_bpartner_id integer NOT NULL,
  nombre_retirante character varying(30),
  c_bankaccount_id integer,
  CONSTRAINT c_bpartner_banklist_key PRIMARY KEY (c_bpartner_banklist_id)
);
ALTER TABLE c_bpartner_banklist
  OWNER TO libertya;

ALTER TABLE c_bankaccount ADD COLUMN c_bankaccount_location_id integer;

CREATE OR REPLACE VIEW c_lista_galicia_payments AS 
SELECT p.ad_client_id, p.ad_org_id, p.created, p.createdby, p.updated, p.updatedby, 
	p.c_payment_id, p.documentno, p.docstatus, p.datetrx, p.dateacct, p.c_bankaccount_id, p.checkno, p.c_currency_id, p.payamt, 
	p.tendertype, p.c_bpartner_id, p.a_name, p.isreconciled, p.duedate, p.dateemissioncheck, p.checkstatus, p.rejecteddate, p.rejectedcomments,
	bl.c_banklist_id, bl.documentno as banklist_documentno, bl.docstatus as banklist_docstatus,
	(select ah.c_allocationhdr_id 
	from c_allocationhdr as ah 
	inner join c_allocationline as al on ah.c_allocationhdr_id = al.c_allocationhdr_id
	where ah.allocationtype = 'OP' and al.c_payment_id = p.c_payment_id and ah.docstatus NOT IN ('IP','DR')
	limit 1) as c_allocationhdr_id
FROM c_payment p 
inner join c_banklistline bll on bll.c_payment_id = p.c_payment_id
inner join c_banklist bl on bl.c_banklist_id = bll.c_banklist_id
inner join c_doctype dt on dt.c_doctype_id = bl.c_doctype_id
where dt.doctypekey = 'LG' and bl.docstatus not in ('IP','DR') and p.docstatus NOT IN ('IP','DR');

ALTER TABLE c_lista_galicia_payments
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_lista_galicia_notpayments AS 
SELECT p.ad_client_id, p.ad_org_id, p.created, p.createdby, p.updated, p.updatedby, 
	p.c_payment_id, p.documentno, p.docstatus, p.datetrx, p.dateacct, p.c_bankaccount_id, p.checkno, p.c_currency_id, p.payamt, 
	p.tendertype, p.c_bpartner_id, p.a_name, p.isreconciled, p.duedate, p.dateemissioncheck, p.checkstatus, p.rejecteddate, p.rejectedcomments,
	null as c_banklist_id, null as banklist_documentno, null as banklist_docstatus,
	(select ah.c_allocationhdr_id 
	from c_allocationhdr as ah 
	inner join c_allocationline as al on ah.c_allocationhdr_id = al.c_allocationhdr_id
	where ah.allocationtype = 'OP' and al.c_payment_id = p.c_payment_id and ah.docstatus NOT IN ('IP','DR')
	limit 1) as c_allocationhdr_id
FROM c_payment p 
where p.docstatus NOT IN ('IP','DR') and not exists (select c_payment_id from c_lista_galicia_payments lpp where lpp.c_payment_id = p.c_payment_id);

ALTER TABLE c_lista_galicia_notpayments
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_lista_patagonia_payments AS 
SELECT p.ad_client_id, p.ad_org_id, p.created, p.createdby, p.updated, p.updatedby, 
	p.c_payment_id, p.documentno, p.docstatus, p.datetrx, p.dateacct, p.c_bankaccount_id, p.checkno, p.c_currency_id, p.payamt, 
	p.tendertype, p.c_bpartner_id, p.a_name, p.isreconciled, p.duedate, p.dateemissioncheck, p.checkstatus, p.rejecteddate, p.rejectedcomments,
	bl.c_banklist_id, bl.documentno as banklist_documentno, bl.docstatus as banklist_docstatus,
	(select ah.c_allocationhdr_id 
	from c_allocationhdr as ah 
	inner join c_allocationline as al on ah.c_allocationhdr_id = al.c_allocationhdr_id
	where ah.allocationtype = 'OP' and al.c_payment_id = p.c_payment_id and ah.docstatus NOT IN ('IP','DR')
	limit 1) as c_allocationhdr_id
FROM c_payment p 
inner join c_banklistline bll on bll.c_payment_id = p.c_payment_id
inner join c_banklist bl on bl.c_banklist_id = bll.c_banklist_id
inner join c_doctype dt on dt.c_doctype_id = bl.c_doctype_id
where dt.doctypekey = 'LP' and bl.docstatus not in ('IP','DR') and p.docstatus NOT IN ('IP','DR');

ALTER TABLE c_lista_patagonia_payments
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_lista_patagonia_notpayments AS 
SELECT p.ad_client_id, p.ad_org_id, p.created, p.createdby, p.updated, p.updatedby, 
	p.c_payment_id, p.documentno, p.docstatus, p.datetrx, p.dateacct, p.c_bankaccount_id, p.checkno, p.c_currency_id, p.payamt, 
	p.tendertype, p.c_bpartner_id, p.a_name, p.isreconciled, p.duedate, p.dateemissioncheck, p.checkstatus, p.rejecteddate, p.rejectedcomments,
	null as c_banklist_id, null as banklist_documentno, null as banklist_docstatus,
	(select ah.c_allocationhdr_id 
	from c_allocationhdr as ah 
	inner join c_allocationline as al on ah.c_allocationhdr_id = al.c_allocationhdr_id
	where ah.allocationtype = 'OP' and al.c_payment_id = p.c_payment_id and ah.docstatus NOT IN ('IP','DR')
	limit 1) as c_allocationhdr_id
FROM c_payment p 
where p.docstatus NOT IN ('IP','DR') and not exists (select c_payment_id from c_lista_patagonia_payments lpp where lpp.c_payment_id = p.c_payment_id);

ALTER TABLE c_lista_patagonia_notpayments 
  OWNER TO libertya;
  
  --20160608-1120 Funcionalidad de Cierre de Tarjetas
CREATE TABLE C_CreditCard_Close
(
  C_CreditCard_Close_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  docaction character(2) NOT NULL,
  docstatus character(2) NOT NULL,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  datetrx date NOT NULL,
  updateCouponDetail character(1),
  description character varying(255),
  allowreopening character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT C_CreditCard_Close_pk PRIMARY KEY (C_CreditCard_Close_id ),
  CONSTRAINT fk_client_CreditCard_Close FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_org_CreditCard_Close FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE C_CreditCard_Close
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION isnumeric(text) RETURNS BOOLEAN AS $$
DECLARE x NUMERIC;
BEGIN
    x = $1::NUMERIC;
    RETURN TRUE;
EXCEPTION WHEN others THEN
    RETURN FALSE;
END;
$$
STRICT
LANGUAGE plpgsql IMMUTABLE;

CREATE TABLE C_CreditCard_CloseLine
(
  C_CreditCard_CloseLine_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  documentno character varying(30) NOT NULL,
  m_entidadfinancieraplan_id integer,
  couponnumber character varying(30),
  couponbatchnumber character varying(30),
  creditcardnumber character varying(20),
  payamt numeric(20,2) NOT NULL DEFAULT 0,
  description character varying(255),
  datetrx timestamp without time zone NOT NULL,
  c_posjournal_id integer,
  c_creditcard_close_id integer,
  c_payment_id integer NOT NULL,
  CONSTRAINT C_CreditCard_CloseLine_key PRIMARY KEY (C_CreditCard_CloseLine_id ),
  CONSTRAINT adorg_CCreditCardCloseLine FOREIGN KEY (ad_org_id)
	REFERENCES ad_org (ad_org_id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT CCreditCardClose_CCreditCardCloseLine FOREIGN KEY (C_CreditCard_Close_id)
	REFERENCES C_CreditCard_Close (C_CreditCard_Close_id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT CPayment_CCreditCardCloseLine FOREIGN KEY (C_Payment_id)
	REFERENCES C_Payment (C_Payment_id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE C_CreditCard_CloseLine
  OWNER TO libertya;

-- 20160610-2110 Nueva columna para permitir reutilizar Nro. de Documento en Recibos.
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Doctype','ReuseDocumentNo','character(1) default ''N''::bpchar'));

-- 20160622-1317 Cambiar el displayType a Search para las columnas Ref_Invoice_ID y Ref_InvoiceLine_ID en C_Invoice y C_InvoiceLine en lugar de Table a fin de mejorar los tiempos de apertura de la ventana de Facturas
UPDATE AD_Column 
SET 	AD_Reference_ID = (SELECT AD_Reference_ID FROM AD_Reference WHERE AD_ComponentObjectUID = 'CORE-AD_Reference-30')
WHERE AD_ComponentObjectUID IN ('CORE-AD_Column-10788', 'CORE-AD_Column-10805');

-- 20160622-1317 Cambiar el displayType a Search y definir la referencia hacia tabla C_Invoice para la columna C_Invoice_ID de las tablas invoiceline, invoicetax, invoicepayschedule y documentdiscount a fin de mejorar los tiempos de apertura de la ventana de Facturas
UPDATE AD_Column
SET 	AD_Reference_ID = (SELECT AD_Reference_ID FROM AD_Reference WHERE AD_ComponentObjectUID = 'CORE-AD_Reference-30'), 
	AD_Reference_value_ID = (SELECT AD_Reference_ID FROM AD_Reference WHERE AD_ComponentObjectUID = 'CORE-AD_Reference-336') 
WHERE AD_ComponentObjectUID IN ('CORE-AD_Column-3836', 'CORE-AD_Column-3851', 'CORE-AD_Column-8312', 'CORE-AD_Column-1014640');

--20160628-1435 Mejoras a la funcionalidad de cuentas corrientes
CREATE TYPE v_documents_org_type_condition AS (documenttable text, document_id int, ad_client_id int, ad_org_id int, 
					isactive char(1), created timestamp, createdby integer, updated timestamp, 
					updatedby int, c_bpartner_id int, c_doctype_id integer, signo_issotrx int, 
					doctypename varchar(60), doctypeprintname varchar(60), documentno varchar(60), 
					issotrx bpchar, docstatus character(2), datetrx timestamp, dateacct timestamp, 
					c_currency_id int, c_conversiontype_id int, amount numeric, 
					c_invoicepayschedule_id integer, duedate timestamp, truedatetrx timestamp, 
					socreditstatus char(1), c_order_id integer);

CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    whereclause1 varchar;
    whereclause2 varchar;
    whereclause3 varchar;
    advancedcondition varchar;
    adocument v_documents_org_type_condition;
   
BEGIN
    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

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
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus) ';
   
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

    end if;

    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
	--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
	advancedcondition = 'il.paymentrule is null OR ';
	if condition = 'B' then
		advancedcondition = '';
	end if;
	whereclause1 = ' (i.paymentrule = ''' || condition || ''') ';
	whereclause2 = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
	whereclause3 = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
    else
	whereclause1 = ' (1 = 1) ';
	whereclause2 = ' (1 = 1) ';
	whereclause3 = ' (1 = 1) ';
    end if;

    consulta = '

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id
                   FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclause1 || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id
                   FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
    ELSE 1 = 1
END 

AND ' || whereclause2 || '

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
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus, 0 as c_order_id
           FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)
    AND (CASE WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id
        THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
        ELSE 1 = 1 END)

    AND ' || whereclause3 || '

' || orderby3 || '

); ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character)
  OWNER TO libertya;

DROP VIEW rv_reportinvoices;

CREATE OR REPLACE VIEW rv_reportinvoices AS 
 SELECT cin.ad_client_id, cin.ad_org_id, cin.isactive, cin.created, cin.createdby, cin.updated, cin.updatedby, cin.c_bpartner_id, cbp.duns, cin.dateinvoiced, cin.issotrx, (((clc.letra::text || ' - '::text) || cin.puntodeventa::text) || ' - '::text) || cin.numerocomprobante::text AS nrodocument, cin.grandtotal, invoiceopen(cin.c_invoice_id, 0) AS saldo, cin.paymentrule
   FROM c_invoice cin
   LEFT JOIN c_letra_comprobante clc ON clc.c_letra_comprobante_id = cin.c_letra_comprobante_id
   LEFT JOIN ( SELECT c_bpartner.c_bpartner_id, c_bpartner.duns
      FROM c_bpartner) cbp ON cin.c_bpartner_id = cbp.c_bpartner_id;

ALTER TABLE rv_reportinvoices
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_invoice_allocation_v AS 
SELECT ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name AS allocation_doc_name, ah.documentno, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno AS invoice_documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name) AS customer, sum(al.amount) AS amount
FROM c_allocationhdr ah
LEFT JOIN c_doctype dt ON dt.c_doctype_id = ah.c_doctype_id
JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
GROUP BY ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name, ah.documentno, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name)
UNION
SELECT ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name AS allocation_doc_name, ah.documentno, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno AS invoice_documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name) AS customer, sum(al.amount) AS amount
FROM c_allocationhdr ah
LEFT JOIN c_doctype dt ON dt.c_doctype_id = ah.c_doctype_id
JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_credit_id
JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
GROUP BY ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name, ah.documentno, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name);

ALTER TABLE c_invoice_allocation_v
  OWNER TO libertya;

update ad_system set dummy = (SELECT addcolumnifnotexists('C_POS','allowcreditnotesearch','character(1) NOT NULL DEFAULT ''Y''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('T_EstadoDeCuenta','condition','character(1)'));
alter table T_CuentaCorriente rename column onlycurrentaccountdocuments to condition;
alter table T_BalanceReport rename column onlycurrentaccountdocuments to condition;

--20160704-1640 Nuevas denominaciones de $200 y $500 (aquí se eliminan en caso de existir para evitar erorres de duplicacion al intentar insertarlas en metadatos durante la instalacion de install.xml)
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_PY-1010848';
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_MX-1010848';
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_AR-1010848';
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_ES-1010848';
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_PY-1010849';
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_MX-1010849';
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_AR-1010849';
DELETE FROM AD_Ref_List_Trl WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List_Trl-es_ES-1010849';
DELETE FROM AD_Ref_List WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List-1010848';
DELETE FROM AD_Ref_List WHERE AD_ComponentObjectUID = 'CORE-AD_Ref_List-1010849';


--20160707-1109 Mejoras en performance al resumen de ventas diarias 
CREATE OR REPLACE FUNCTION v_dailysales_invoices_filtered(
    orgid integer,
    posid integer,
    userid integer,
    datefrom date,
    dateto date,
    invoicedatefrom date,
    invoicedateto date,
    addinvoicedate boolean)
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
  ' AND NOT (
  		EXISTS (
			SELECT * FROM (
				SELECT *
				FROM c_allocationline al
				WHERE i.c_invoice_id = al.c_invoice_id AND i.isvoidable = ''Y''::bpchar 
			) as FOO
			JOIN c_payment p ON p.c_payment_id = foo.c_payment_id
			JOIN c_cashline cl ON cl.c_payment_id = p.c_payment_id
		)
	);';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_dailysales_invoices_filtered(integer, integer, integer, date, date, date, date, boolean)
  OWNER TO libertya;

--20160826-1455 Nuevo campo para indicar el remito asociado a factura de fletes o transporte
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_Invoice','M_InOutTransport_ID','integer'));

--20160826-1710 Fix de condición sobre impresos fiscalmente cuando son comprobantes que requieren impresión fiscal en todas las views para la exportación CITI 
--reginfo_ventas_alicuotas_v
CREATE OR REPLACE VIEW reginfo_ventas_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateacct) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)) AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric) AND NOT (it.taxamt = 0::numeric AND t.rate <> 0::numeric) AND i.grandtotal <> 0.01;

ALTER TABLE reginfo_ventas_alicuotas_v
  OWNER TO libertya;

-- reginfo_ventas_cbte_v
CREATE OR REPLACE VIEW reginfo_ventas_cbte_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateacct) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, i.numerocomprobante AS nrocomprobantehasta, 
        CASE
            WHEN bp.taxidtype = '99'::bpchar AND i.grandtotal > 1000::numeric THEN '96'::bpchar
            ELSE bp.taxidtype
        END::character(2) AS codigodoccomprador, gettaxid(bp.taxid, bp.taxidtype, bp.c_categoria_iva_id, i.nroidentificcliente, i.grandtotal)::character varying(20) AS nroidentificacioncomprador, bp.name AS nombrecomprador, currencyconvert(i.grandtotal, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal, 0::numeric(20,2) AS impconceptosnoneto, 0::numeric(20,2) AS imppercepnocategorizados, currencyconvert(getimporteoperacionexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos, cu.wsfecode AS codmoneda, currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, 
        CASE
            WHEN getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva, getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, NULL::timestamp without time zone AS fechavencimientopago
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)) AND i.grandtotal <> 0.01;

ALTER TABLE reginfo_ventas_cbte_v
  OWNER TO libertya;

-- reginfo_compras_alicuotas_v
CREATE OR REPLACE VIEW reginfo_compras_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, gettipodecomprobante(dt.doctypekey, l.letra)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)) AND (l.letra <> ALL (ARRAY['B'::bpchar, 'C'::bpchar])) AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric) AND NOT (it.taxamt = 0::numeric AND t.rate <> 0::numeric);

ALTER TABLE reginfo_compras_alicuotas_v
  OWNER TO libertya;

-- reginfo_compras_cbte_v
CREATE OR REPLACE VIEW reginfo_compras_cbte_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, gettipodecomprobante(dt.doctypekey, l.letra)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN i.importclearance
            ELSE NULL::character varying
        END::character varying(30) AS despachoimportacion, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, bp.name AS nombrevendedor, currencyconvert(i.grandtotal, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal, 0::numeric(20,2) AS impconceptosnoneto, 
        CASE
            WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND i.importclearance IS NULL THEN 0::numeric
            ELSE currencyconvert(getimporteoperacionexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)
        END::numeric(20,2) AS impopeexentas, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosvaloragregado, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos, cu.wsfecode AS codmoneda, currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, 
        CASE
            WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra)::text <> '66'::text THEN 0::numeric
            ELSE 
            CASE
                WHEN getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
                ELSE getcantidadalicuotasiva(i.c_invoice_id)
            END
        END AS cantalicuotasiva, getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impcreditofiscalcomputable, currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, NULL::character varying(20) AS cuitemisorcorredor, NULL::character varying(60) AS denominacionemisorcorredor, 0::numeric(20,2) AS ivacomision
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar));

ALTER TABLE reginfo_compras_cbte_v
  OWNER TO libertya;

-- reginfo_compras_importaciones_v
CREATE OR REPLACE VIEW reginfo_compras_importaciones_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, i.importclearance AS despachoimportacion, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)) AND i.importclearance IS NOT NULL AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric) AND NOT (it.taxamt = 0::numeric AND t.rate <> 0::numeric);

ALTER TABLE reginfo_compras_importaciones_v
  OWNER TO libertya;

--20160905-1920 Nuevas y modificaciones de funciones que dan soporte a las correcciones de cuenta corriente
-- Función getallocatedamt(integer, integer, integer, integer, timestamp without time zone, integer)
CREATE OR REPLACE FUNCTION getallocatedamt(
    p_c_invoice_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer,
    p_multiplierap integer,
    p_fechacorte timestamp without time zone,
    p_c_invoicepayschedule_id integer)
  RETURNS numeric AS
$BODY$ 
DECLARE
	v_MultiplierAP		NUMERIC := 1;
	v_PaidAmt			NUMERIC := 0;
	v_ConversionType_ID INTEGER := p_c_conversionType_ID;
	v_Currency_ID       INTEGER := p_c_currency_id;
	v_Temp     NUMERIC;
	v_SchedulesAmt NUMERIC;
	v_Diff NUMERIC;
	ar			RECORD;
	s			RECORD;
	v_DateAcct timestamp without time zone;
	schedule_founded boolean;
BEGIN
	--	Default
	IF (p_MultiplierAP IS NOT NULL) THEN
		v_MultiplierAP := p_MultiplierAP::numeric;
	END IF;
	
	SELECT DateAcct
	       INTO v_DateAcct
	FROM C_Invoice 
	WHERE C_Invoice_ID = p_c_invoice_id;

	FOR ar IN 
		SELECT	a.AD_Client_ID, a.AD_Org_ID,
		al.Amount, al.DiscountAmt, al.WriteOffAmt,
		a.C_Currency_ID, a.DateTrx , al.C_Invoice_Credit_ID
		FROM	C_AllocationLine al
		INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	(al.C_Invoice_ID = p_C_Invoice_ID OR 
				al.C_Invoice_Credit_ID = p_C_Invoice_ID ) -- condicion no en Adempiere
          	AND   a.IsActive='Y'
          	AND   (p_fechacorte is null OR a.dateacct::date <= p_fechacorte::date)
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
				ar.C_Currency_ID, v_Currency_ID, v_DateAcct, v_ConversionType_ID, 
				ar.AD_Client_ID, ar.AD_Org_ID);

	--RAISE NOTICE ' C_Invoice_ID=% , PaidAmt=% , Allocation= % ',p_C_Invoice_ID, v_PaidAmt, v_Temp;
	END LOOP;

	--Si existe un payschedule del comprobante como parametro, entonces se devuelve el importe imputado de ese payschedule
	IF (p_c_invoicepayschedule_id > 0) THEN 
		v_SchedulesAmt := 0;
		schedule_founded := false;        
		FOR s IN  SELECT  ips.C_InvoicePaySchedule_ID, currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_DateAcct, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt 	        
			FROM    C_InvoicePaySchedule ips 	        
			INNER JOIN C_Invoice i on (ips.C_Invoice_ID = i.C_Invoice_ID) 		
			WHERE	ips.C_Invoice_ID = p_c_invoice_id AND   ips.IsValid='Y'         	
			ORDER BY ips.DueDate 
		LOOP    
			-- Acumulo los importes de cada schedule hasta llegar al c_invoicepayschedule_id parámetro
			v_SchedulesAmt := v_SchedulesAmt + s.DueAmt;
			schedule_founded := s.C_InvoicePaySchedule_ID = p_c_invoicepayschedule_id;
			IF (schedule_founded) THEN
				-- Si llegamos al parámetro, entonces se le resta el acumulado de schedules a lo imputado
				v_Diff := v_PaidAmt - v_SchedulesAmt;
				-- Si el importe resultante es:
				-- 1) >= 0: Significa que imputado hay mas que el acumulado, entonces lo imputado es el total de la cuota
				IF (v_Diff >= 0) THEN
					v_PaidAmt := s.DueAmt;
				ELSE
					-- 2) < 0: Significa que hay imputado algo o nada de la cuota 
					-- Al importe de la cuota, se le resta la diferencia anterior absoluta
					v_PaidAmt := s.DueAmt - abs(v_Diff);
					-- Si la diferencia es menor o igual a 0, significa que no hay nada imputado
					-- Caso contrario, lo pagado es dicha diferencia
					IF (v_PaidAmt <= 0) THEN
						v_PaidAmt := 0;
					END IF;
				END IF;
				EXIT;
			END IF;
		END LOOP;
		-- Si no se encontró el schedule, entonces el imputado es 0
		IF (NOT schedule_founded) THEN
			v_PaidAmt := 0;
		END IF;
	END IF;
	
	RETURN	v_PaidAmt * v_MultiplierAP;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getallocatedamt(integer, integer, integer, integer, timestamp without time zone, integer)
  OWNER TO libertya;

-- Función getallocatedamt(integer, integer, integer, integer, timestamp without time zone)
CREATE OR REPLACE FUNCTION getallocatedamt(
    p_c_invoice_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer,
    p_multiplierap integer,
    p_fechacorte timestamp without time zone)
  RETURNS numeric AS
$BODY$ 
BEGIN
	RETURN getallocatedamt(p_c_invoice_id, p_c_currency_id, p_c_conversiontype_id, p_multiplierap, p_fechacorte, 0);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getallocatedamt(integer, integer, integer, integer, timestamp without time zone)
  OWNER TO libertya;

-- Función getallocatedamt(integer, integer, integer, integer)
CREATE OR REPLACE FUNCTION getallocatedamt(
    p_c_invoice_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer,
    p_multiplierap integer)
  RETURNS numeric AS
$BODY$
BEGIN
	return getallocatedamt(p_c_invoice_id, p_c_currency_id, p_c_conversiontype_id, p_multiplierap, null::timestamp);
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getallocatedamt(integer, integer, integer, integer)
  OWNER TO libertya;

-- Función invoiceopen(integer, integer, integer, integer, timestamp)
CREATE OR REPLACE FUNCTION invoiceopen(
    p_c_invoice_id integer,
    p_c_invoicepayschedule_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer,
    p_dateto timestamp)
  RETURNS numeric AS
$BODY$ /*************************************************************************  * The contents of this file are subject to the Compiere License.  You may  * obtain a copy of the License at    http://www.compiere.org/license.html  * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either  * express or implied. See the License for details. Code: Compiere ERP+CRM  * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.  *  * converted to postgreSQL by Karsten Thiemann (Schaeffer AG),   * kthiemann@adempiere.org  *************************************************************************  ***  * Title:	Calculate Open Item Amount in Invoice Currency  * Description:  *	Add up total amount open for C_Invoice_ID if no split payment.  *  Grand Total minus Sum of Allocations in Invoice Currency  *  *  For Split Payments:  *  Allocate Payments starting from first schedule.  *  Cannot be used for IsPaid as mutating  *  * Test:  * 	SELECT C_InvoicePaySchedule_ID, DueAmt FROM C_InvoicePaySchedule WHERE C_Invoice_ID=109 ORDER BY DueDate;  * 	SELECT invoiceOpen (109, null) FROM AD_System; - converted to default client currency  * 	SELECT invoiceOpen (109, 11) FROM AD_System; - converted to default client currency  * 	SELECT invoiceOpen (109, 102) FROM AD_System;  * 	SELECT invoiceOpen (109, 103) FROM AD_System;  ***  * Pasado a Libertya a partir de Adempiere 360LTS  * - ids son de tipo integer, no numeric  * - TODO : tema de las zonas en los timestamp  * - Excepciones en SELECT INTO requieren modificador STRICT bajo PostGreSQL o usar  * NOT FOUND  * - Por ahora, el "ignore rounding" se hace como en libertya (-0.01,0.01),  * en vez de usar la precisión de la moneda  * - Se toma el tipo de conversion de la factura, auqneu esto es dudosamente correcto  * ya que otras funciones , en particular currencyBase nunca tiene en cuenta  * este valor  * - Como en Libertya se tiene en cuenta tambien C_Invoice_Credit_ID para calcular  * la cantidad alocada a una factura (aunque esto es medio dudoso....)  * - No se soporta la fecha como 3er parametro (en realidad, tampoco se esta  * usando actualmente, y se deberia poder resolver de otra manera)  * - Libertya parece tener un bug al filtrar por C_InvoicePaySchedule_ID al calcular  * el granTotal (el granTotal SIEMPRE es el total de la factura, tomada directamente  * de C_Invoice.GranTotal o a partir de la suma de los DueAmt en C_InvoicePaySchedule);  * se usa la sentencia como esta en Adempeire (esto es, solo se filtra por C_Invoice_ID)  * - Nuevo enfoque: NO se usa ni la vista C_Invoice_V ni multiplicadores  * se asume todo positivo...  * - El resultado SIEMPRE deberia ser positivo y en el intervalo [0..GrandTotal]  * - 03 julio: se pasa a usar getAllocatedAmt para hacer esta funcion consistente  * con invoicePaid  * - 03 julio: se pasa de usar STRICT a NOT FOUND; es mas eficiente  ************************************************************************/ 
DECLARE 	
v_Currency_ID		INTEGER := p_c_currency_id; 	
v_TotalOpenAmt  	NUMERIC := 0; 	
v_PaidAmt  	        NUMERIC := 0; 	
v_Remaining	        NUMERIC := 0;    	
v_Precision            	NUMERIC := 0;    	
v_Min            	NUMERIC := 0.01;     	
s			RECORD; 	
v_ConversionType_ID INTEGER := p_c_conversiontype_id;  	
v_Date timestamp with time zone := ('now'::text)::timestamp(6);                

BEGIN 	 	

SELECT	currencyConvert(GrandTotal, I.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, I.AD_Client_ID, I.AD_Org_ID) as GrandTotal, 	
	(SELECT StdPrecision FROM C_Currency C WHERE C.C_Currency_ID = I.C_Currency_ID) AS StdPrecision  	
	INTO v_TotalOpenAmt, v_Precision 	
FROM	C_Invoice I 
WHERE	I.C_Invoice_ID = p_C_Invoice_ID; 	

IF NOT FOUND THEN  
	RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID; 		
	RETURN NULL; 	
END IF; 	      	 	 	 	

v_PaidAmt := getAllocatedAmt(p_C_Invoice_ID,v_Currency_ID,v_ConversionType_ID,1,p_dateto); 

IF (p_C_InvoicePaySchedule_ID > 0) THEN 
	v_Remaining := v_PaidAmt;         
	FOR s IN  SELECT  ips.C_InvoicePaySchedule_ID, currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt 	        
		FROM    C_InvoicePaySchedule ips 	        
		INNER JOIN C_Invoice i on (ips.C_Invoice_ID = i.C_Invoice_ID) 		
		WHERE	ips.C_Invoice_ID = p_C_Invoice_ID AND   ips.IsValid='Y'         	
		ORDER BY ips.DueDate         
	LOOP             

		IF (s.C_InvoicePaySchedule_ID = p_C_InvoicePaySchedule_ID) THEN                 
			v_TotalOpenAmt := s.DueAmt - v_Remaining;                 
			IF (v_TotalOpenAmt < 0) THEN                     
				v_TotalOpenAmt := 0;                  
			END IF; 				
			EXIT;              
		ELSE                  
			v_Remaining := v_Remaining - s.DueAmt;                 
			IF (v_Remaining < 0) THEN         
				v_Remaining := 0;                 
			END IF;             
		END IF;         
	END LOOP;     
ELSE         
	v_TotalOpenAmt := v_TotalOpenAmt - v_PaidAmt;     
END IF; 	 	

IF (v_TotalOpenAmt >= -v_Min AND v_TotalOpenAmt <= v_Min) THEN 		
	v_TotalOpenAmt := 0; 	
END IF; 	 	

v_TotalOpenAmt := ROUND(COALESCE(v_TotalOpenAmt,0), v_Precision); 	

RETURN	v_TotalOpenAmt; 

END; 
$BODY$

  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer, integer, integer, timestamp)
  OWNER TO libertya;

-- Función invoiceopen(integer, integer, integer, integer)
CREATE OR REPLACE FUNCTION invoiceopen(
    p_c_invoice_id integer,
    p_c_invoicepayschedule_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer)
  RETURNS numeric AS
$BODY$ 
BEGIN 	 	
	return invoiceopen(p_c_invoice_id, p_c_invoicepayschedule_id, p_c_currency_id, p_c_conversiontype_id, null::timestamp);
END; 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer, integer, integer)
  OWNER TO libertya;

-- Función invoiceopen(integer, integer, timestamp)
CREATE OR REPLACE FUNCTION invoiceopen(
    p_c_invoice_id integer,
    p_c_invoicepayschedule_id integer,
    p_dateto timestamp)
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
	v_Currency_ID	    INTEGER;
	v_ConversionType_ID INTEGER; -- NO en Adempiere

BEGIN
	--	Get Currency, ConversionType
	SELECT	C_Currency_ID, C_ConversionType_ID
		INTO v_Currency_ID, v_ConversionType_ID
	FROM	C_Invoice I		--	NO se corrige por CM o SpliPayment; se usa directamente C_Inovoice y ningun multiplicador
	WHERE	I.C_Invoice_ID = p_C_Invoice_ID;

	IF NOT FOUND THEN
       	RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID;
		RETURN NULL;
	END IF;

	RETURN	invoiceOpen(p_c_invoice_id, p_c_invoicepayschedule_id, v_Currency_ID, v_ConversionType_ID, p_dateto);
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer, timestamp)
  OWNER TO libertya;

-- Función invoiceopen(integer, integer)
CREATE OR REPLACE FUNCTION invoiceopen(
    p_c_invoice_id integer,
    p_c_invoicepayschedule_id integer)
  RETURNS numeric AS
$BODY$
BEGIN
	RETURN	invoiceOpen(p_c_invoice_id, p_c_invoicepayschedule_id, null::timestamp);
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer)
  OWNER TO libertya;

-- Función cashlineavailable(integer, timestamp)
CREATE OR REPLACE FUNCTION cashlineavailable(
    p_c_cashline_id integer,
    p_date_to timestamp)
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
	v_DateAcct timestamp without time zone;
 
BEGIN
	IF (p_C_Cashline_id IS NULL OR p_C_Cashline_id = 0) THEN
		RETURN NULL;
	END IF;
	
	--	Get Currency and Amount
	SELECT	C_Currency_ID, Amount
		INTO v_Currency_ID, v_Amt
	FROM	C_CashLine    
	WHERE	C_CashLine_ID  = p_C_Cashline_id;

	SELECT DateAcct
	       INTO v_DateAcct
	FROM C_Cash c 
	INNER JOIN C_CashLine cl ON c.C_Cash_ID = cl.C_Cash_ID 
	WHERE C_CashLine_ID = p_C_Cashline_id;
	
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
          	AND (p_date_to IS NULL OR a.dateacct::date <= p_date_to::date)
	LOOP
        v_allocation := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cashlineavailable(integer, timestamp)
  OWNER TO libertya;

-- Función cashlineavailable(integer)
CREATE OR REPLACE FUNCTION cashlineavailable(p_c_cashline_id integer)
  RETURNS numeric AS
$BODY$ 
BEGIN
	RETURN cashlineavailable(p_c_cashline_id, null::timestamp);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cashlineavailable(integer)
  OWNER TO libertya;

-- Función paymentavailable(integer, timestamp)
CREATE OR REPLACE FUNCTION paymentavailable(p_c_payment_id integer, dateTo timestamp)
  RETURNS numeric AS
$BODY$
DECLARE
	v_Currency_ID		INTEGER;
	v_AvailableAmt		NUMERIC := 0;
   	v_IsReceipt         CHARACTER(1);
   	v_Amt               NUMERIC := 0;
   	r   			RECORD;
	v_Charge_ID INTEGER; 
	v_ConversionType_ID INTEGER; 
	
	v_DateAcct timestamp without time zone;
BEGIN
	BEGIN
	
	SELECT	C_Currency_ID, PayAmt, IsReceipt, 
			C_Charge_ID,C_ConversionType_ID, DateAcct
	  INTO	STRICT 
			v_Currency_ID, v_AvailableAmt, v_IsReceipt,
			v_Charge_ID,v_ConversionType_ID, v_DateAcct
	FROM	C_Payment     
	WHERE	C_Payment_ID = p_C_Payment_ID;
		EXCEPTION	
		WHEN OTHERS THEN
            	RAISE NOTICE 'PaymentAvailable - %', SQLERRM;
			RETURN NULL;
	END;
	
	IF (v_Charge_ID > 0 ) THEN 
	   RETURN 0;
	END IF;
	
	FOR r IN
		SELECT	a.AD_Client_ID, a.AD_Org_ID, al.Amount, a.C_Currency_ID, a.DateTrx
		FROM	C_AllocationLine al
	        INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
		WHERE	al.C_Payment_ID = p_C_Payment_ID
          	AND   a.IsActive='Y'
          	AND (dateTo IS NULL OR a.dateacct::date <= dateTo::date)
	LOOP
        v_Amt := currencyConvert(r.Amount, r.C_Currency_ID, v_Currency_ID, 
				v_DateAcct, v_ConversionType_ID, r.AD_Client_ID, r.AD_Org_ID);
	    v_AvailableAmt := v_AvailableAmt - v_Amt;
	END LOOP;
	
	IF (v_AvailableAmt < 0) THEN
		RAISE NOTICE 'Payment Available negative, correcting to zero - %',v_AvailableAmt ;
		v_AvailableAmt := 0;
	END IF;
	
	v_AvailableAmt :=  currencyRound(v_AvailableAmt,v_Currency_ID,NULL);
	RETURN	v_AvailableAmt;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION paymentavailable(integer, timestamp)
  OWNER TO libertya;

-- Función paymentavailable(integer)
CREATE OR REPLACE FUNCTION paymentavailable(p_c_payment_id integer)
  RETURNS numeric AS
$BODY$
BEGIN
	RETURN paymentavailable(p_c_payment_id, null::timestamp);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION paymentavailable(integer)
  OWNER TO libertya;

--DROP de función v_documents_org_filtered(integer, boolean, character) y tipo v_documents_org_type_condition 
DROP FUNCTION v_documents_org_filtered(integer, boolean, character);
DROP TYPE v_documents_org_type_condition;

-- Tipo v_documents_org_type_condition
CREATE TYPE v_documents_org_type_condition AS (documenttable text, document_id int, ad_client_id int, ad_org_id int, 
					isactive char(1), created timestamp, createdby integer, updated timestamp, 
					updatedby int, c_bpartner_id int, c_doctype_id integer, signo_issotrx int, 
					doctypename varchar(60), doctypeprintname varchar(60), documentno varchar(60), 
					issotrx bpchar, docstatus character(2), datetrx timestamp, dateacct timestamp, 
					c_currency_id int, c_conversiontype_id int, amount numeric, 
					c_invoicepayschedule_id integer, duedate timestamp, truedatetrx timestamp, 
					socreditstatus char(1), c_order_id integer, c_allocationhdr_id integer);

--Función v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    advancedcondition varchar;
    whereclauseConditionDebit varchar;
    whereclauseConditionCredit varchar;
    whereclauseDateTo varchar;
    selectallocationNull varchar;
    selectallocationPayment varchar;
    selectallocationCashline varchar;
    selectallocationCredit varchar;
    selectAllocationReferencePayment varchar;
    selectAllocationReferenceCashline varchar;
    selectAllocationReferenceCredit varchar;
    adocument v_documents_org_type_condition;
   
BEGIN
    whereclauseDateTo = ' ( 1 = 1 ) ';
    -- Armar la condición para fecha de corte
    if dateTo is not null then 
	whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';
    end if;
    
    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
	--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
	advancedcondition = 'il.paymentrule is null OR ';
	if condition = 'B' then
		advancedcondition = '';
	end if;
	whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') ';
	whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
    else
	whereclauseConditionDebit = ' (1 = 1) ';
	whereclauseConditionCredit = ' (1 = 1) ';
    end if;    

    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

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
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus) ';
	
	selectallocationNull = ' NULL::integer ';
	selectallocationPayment = selectallocationNull;
	selectallocationCashline = selectallocationNull;
	selectallocationCredit = selectallocationNull;
	
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

	selectAllocationReferencePayment = ' al.c_payment_id = p.c_payment_id ';
	selectAllocationReferenceCashline = ' al.c_cashline_id = cl.c_cashline_id ';
	selectAllocationReferenceCredit = ' al.c_invoice_credit_id = i.c_invoice_id ';

	selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	
    end if;    

    consulta = ' SELECT * FROM 

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, '
				|| selectallocationCredit || 
               ' FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclauseConditionDebit || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, '
		|| selectallocationPayment || 
                  ' FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id 
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
    ELSE 1 = 1
END 

AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL 
		OR bde.M_BoletaDeposito_ID IS NOT NULL 
		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'') 
	ELSE 1 = 1
	END) 

AND ' || whereclauseConditionCredit || '

' || orderby2 || '


)

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
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus, 0 as c_order_id, '
                || selectallocationCashline || 
       ' FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)
    AND (CASE WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id
        THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
        ELSE 1 = 1 END)

    AND ' || whereclauseConditionCredit || '

' || orderby3 || '

)) AS d  
WHERE ' || whereclauseDateTo || ' ; ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
  OWNER TO libertya;

-- Función v_documents_org_filtered(integer, boolean, character)
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
BEGIN
	return query select * from v_documents_org_filtered(bpartner, summaryonly, condition, null::timestamp);
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character)
  OWNER TO libertya;

  
-- 20160912-1017 Modificacion de rv_c_invoice: Actualizacion vista para agregar la fecha del remito.
-- Lamentablemente postgres 8.3x no soporta ampliacion de columnas en la vista (recién postgres 8.4x lo soporta),
-- y dado que rv_c_invoice es referenciada por varias otras vistas, es necesario hacer el drop y post-create de cada una de ellas
drop view rv_c_invoice_customerprodqtr;
drop view rv_c_invoice_customervendqtr;
drop view rv_c_invoice_day;
drop view rv_c_invoice_month;
drop view rv_c_invoice_prodmonth;
drop view rv_c_invoice_prodweek;
drop view rv_c_invoice_vendormonth;
drop view rv_c_invoice_week;
drop view rv_openitem;
drop view rv_c_invoiceline;
drop view rv_c_invoice;


CREATE OR REPLACE VIEW rv_c_invoice AS
SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.isprinted, i.isdiscountprinted, i.processing, i.processed, i.istransferred, i.ispaid, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, b.c_bp_group_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.ispayschedulevalid, loc.c_country_id, loc.c_region_id, loc.postal, loc.city, i.c_charge_id, 
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.chargeamt * (- 1::numeric)
            ELSE i.chargeamt
        END AS chargeamt, 
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.totallines * (- 1::numeric)
            ELSE i.totallines
        END AS totallines, 
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.grandtotal * (- 1::numeric)
            ELSE i.grandtotal
        END AS grandtotal, 
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN (-1)
            ELSE 1
        END AS multiplier, 
        getinoutsdocumentsnofrominvoice(i.c_invoice_id)::character varying(30) AS documentno_inout,
        mio.movementdate AS movementdate_inout
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctype_id = d.c_doctype_id
   JOIN c_bpartner b ON i.c_bpartner_id = b.c_bpartner_id
   JOIN c_bpartner_location bpl ON i.c_bpartner_location_id = bpl.c_bpartner_location_id
   JOIN c_location loc ON bpl.c_location_id = loc.c_location_id
   LEFT JOIN m_inout mio ON mio.documentno = getinoutsdocumentsnofrominvoice(i.c_invoice_id)::character varying(30) AND mio.ad_client_id = i.ad_client_id
  WHERE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]);


CREATE OR REPLACE VIEW rv_c_invoiceline AS 
 SELECT i.c_order_id, i.c_currency_id, il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, i.c_bp_group_id, il.m_product_id, p.m_product_category_id, i.dateinvoiced, i.dateacct, i.issotrx, i.c_doctype_id, i.docstatus, il.qtyinvoiced * i.multiplier::numeric AS qtyinvoiced, il.qtyentered * i.multiplier::numeric AS qtyentered, il.m_attributesetinstance_id, productattribute(il.m_attributesetinstance_id) AS productattribute, pasi.m_attributeset_id, pasi.m_lot_id, pasi.guaranteedate, pasi.lot, pasi.serno, il.pricelist, il.priceactual, il.pricelimit, il.priceentered, 
        CASE
            WHEN il.pricelist = 0::numeric THEN 0::numeric
            ELSE round((il.pricelist - il.priceactual) / il.pricelist * 100::numeric, 2)
        END AS discount, 
        CASE
            WHEN costprice.pricestd = 0::numeric THEN 0::numeric
            ELSE round((il.priceactual - costprice.pricestd) / costprice.pricestd * 100::numeric, 2)
        END AS margin, round(i.multiplier::numeric * il.linenetamt, 2) AS linenetamt, round(i.multiplier::numeric * il.pricelist * il.qtyinvoiced, 2) AS linelistamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN round(i.multiplier::numeric * il.linenetamt, 2)
            ELSE round(i.multiplier::numeric * il.pricelimit * il.qtyinvoiced, 2)
        END AS linelimitamt, round(i.multiplier::numeric * il.pricelist * il.qtyinvoiced - il.linenetamt, 2) AS linediscountamt, 
        CASE
            WHEN COALESCE(il.pricelimit, 0::numeric) = 0::numeric THEN 0::numeric
            ELSE round(i.multiplier::numeric * il.linenetamt - il.pricelimit * il.qtyinvoiced, 2)
        END AS lineoverlimitamt
   FROM rv_c_invoice i
   JOIN c_invoiceline il ON i.c_invoice_id = il.c_invoice_id
   LEFT JOIN m_product p ON il.m_product_id = p.m_product_id
   LEFT JOIN m_attributesetinstance pasi ON il.m_attributesetinstance_id = pasi.m_attributesetinstance_id
   LEFT JOIN ( SELECT pp.m_product_id, pp.pricestd
   FROM m_productprice pp
   JOIN m_pricelist_version plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id
   JOIN m_pricelist pl ON plv.m_pricelist_id = pl.m_pricelist_id AND pl.issopricelist = 'N'::bpchar AND pl.isdefault = 'Y'::bpchar) costprice ON p.m_product_id = costprice.m_product_id;

ALTER TABLE rv_c_invoiceline
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_customerprodqtr AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced, il.issotrx
   FROM rv_c_invoiceline il
  GROUP BY il.ad_client_id, il.ad_org_id, il.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying), il.issotrx, il.c_currency_id;

ALTER TABLE rv_c_invoice_customerprodqtr
  OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_customervendqtr AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_bpartner_id, po.c_bpartner_id AS vendor_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced
   FROM rv_c_invoiceline il
   JOIN m_product_po po ON il.m_product_id = po.m_product_id
  WHERE il.issotrx = 'Y'::bpchar
  GROUP BY il.ad_client_id, il.ad_org_id, il.c_bpartner_id, po.c_bpartner_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying), il.c_currency_id;

ALTER TABLE rv_c_invoice_customervendqtr
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_day AS 
 SELECT rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DD'::character varying) AS dateinvoiced, sum(rv_c_invoiceline.linenetamt) AS linenetamt, sum(rv_c_invoiceline.linelistamt) AS linelistamt, sum(rv_c_invoiceline.linelimitamt) AS linelimitamt, sum(rv_c_invoiceline.linediscountamt) AS linediscountamt, rv_c_invoiceline.c_currency_id, 
        CASE
            WHEN sum(rv_c_invoiceline.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(rv_c_invoiceline.linelistamt) - sum(rv_c_invoiceline.linenetamt)) / sum(rv_c_invoiceline.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(rv_c_invoiceline.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(rv_c_invoiceline.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(rv_c_invoiceline.linenetamt) - sum(rv_c_invoiceline.lineoverlimitamt)) / sum(rv_c_invoiceline.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, rv_c_invoiceline.issotrx
   FROM rv_c_invoiceline
  GROUP BY rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DD'::character varying), rv_c_invoiceline.issotrx, rv_c_invoiceline.c_currency_id;

ALTER TABLE rv_c_invoice_day
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_month AS 
 SELECT rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'MM'::character varying) AS dateinvoiced, sum(rv_c_invoiceline.linenetamt) AS linenetamt, sum(rv_c_invoiceline.linelistamt) AS linelistamt, sum(rv_c_invoiceline.linelimitamt) AS linelimitamt, sum(rv_c_invoiceline.linediscountamt) AS linediscountamt, rv_c_invoiceline.c_currency_id, 
        CASE
            WHEN sum(rv_c_invoiceline.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(rv_c_invoiceline.linelistamt) - sum(rv_c_invoiceline.linenetamt)) / sum(rv_c_invoiceline.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(rv_c_invoiceline.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(rv_c_invoiceline.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(rv_c_invoiceline.linenetamt) - sum(rv_c_invoiceline.lineoverlimitamt)) / sum(rv_c_invoiceline.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, rv_c_invoiceline.issotrx
   FROM rv_c_invoiceline
  GROUP BY rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'MM'::character varying), rv_c_invoiceline.issotrx, rv_c_invoiceline.c_currency_id;

ALTER TABLE rv_c_invoice_month
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_prodmonth AS 
 SELECT il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced, il.issotrx
   FROM rv_c_invoiceline il
  GROUP BY il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying), il.issotrx, il.c_currency_id;

ALTER TABLE rv_c_invoice_prodmonth
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_prodweek AS 
 SELECT il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'DY'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced, il.issotrx
   FROM rv_c_invoiceline il
  GROUP BY il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'DY'::character varying), il.issotrx, il.c_currency_id;

ALTER TABLE rv_c_invoice_prodweek
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_vendormonth AS 
 SELECT il.ad_client_id, il.ad_org_id, po.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced
   FROM rv_c_invoiceline il
   JOIN m_product_po po ON il.m_product_id = po.m_product_id
  WHERE il.issotrx = 'Y'::bpchar
  GROUP BY il.ad_client_id, il.ad_org_id, po.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying), il.c_currency_id;

ALTER TABLE rv_c_invoice_vendormonth
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_week AS 
 SELECT rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DY'::character varying) AS dateinvoiced, sum(rv_c_invoiceline.linenetamt) AS linenetamt, sum(rv_c_invoiceline.linelistamt) AS linelistamt, sum(rv_c_invoiceline.linelimitamt) AS linelimitamt, sum(rv_c_invoiceline.linediscountamt) AS linediscountamt, rv_c_invoiceline.c_currency_id, 
        CASE
            WHEN sum(rv_c_invoiceline.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(rv_c_invoiceline.linelistamt) - sum(rv_c_invoiceline.linenetamt)) / sum(rv_c_invoiceline.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(rv_c_invoiceline.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(rv_c_invoiceline.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(rv_c_invoiceline.linenetamt) - sum(rv_c_invoiceline.lineoverlimitamt)) / sum(rv_c_invoiceline.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, rv_c_invoiceline.issotrx
   FROM rv_c_invoiceline
  GROUP BY rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DY'::character varying), rv_c_invoiceline.issotrx, rv_c_invoiceline.c_currency_id;

ALTER TABLE rv_c_invoice_week
  OWNER TO libertya;


CREATE OR REPLACE VIEW rv_openitem AS 
         SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, p.netdays, i.dateinvoiced + ((p.netdays::text || ' days'::text)::interval) AS duedate, paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) AS daysdue, i.dateinvoiced + ((p.discountdays::text || ' days'::text)::interval) AS discountdate, round(i.grandtotal * p.discount / 100::numeric, 2) AS discountamt, i.grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, 0) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
           FROM rv_c_invoice i
      JOIN c_paymentterm p ON i.c_paymentterm_id = p.c_paymentterm_id
     WHERE invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid <> 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar
UNION 
         SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, to_days(ips.duedate) - to_days(i.dateinvoiced) AS netdays, ips.duedate, to_days(now()::timestamp without time zone) - to_days(ips.duedate) AS daysdue, ips.discountdate, ips.discountamt, ips.dueamt AS grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, ips.c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
           FROM rv_c_invoice i
      JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
     WHERE invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid = 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE rv_openitem
  OWNER TO libertya;


--Correccion en la funcion
CREATE OR REPLACE FUNCTION getinoutsdocumentsnofrominvoice(p_c_invoice_id integer) RETURNS character varying AS
$BODY$
DECLARE
	v_nro_Remito	  CHARACTER VARYING(30) := null;
   	v_IsSoTrx         CHARACTER(1);
	v_order_id	  INTEGER := 0;
BEGIN
	BEGIN
	SELECT	IsSoTrx, C_Order_ID
	  INTO	STRICT 
			v_IsSoTrx, v_order_id
	FROM	C_Invoice
	WHERE	C_Invoice_ID = p_c_invoice_id;
		EXCEPTION	--	No encontrado; posiblememte mal llamado
		WHEN OTHERS THEN
            	RAISE NOTICE 'InvoiceAvailable - %', SQLERRM;
			RETURN NULL;
	END;

	IF (v_IsSoTrx = 'N') THEN 
		BEGIN
		-- Si es issotrx = 'N' se debe verificar en MMatchPO, como último el remito puede estar relacionado al pedido mismo 
		SELECT	distinct io.documentno
		  INTO
				v_nro_Remito
		FROM       m_matchpo as mpo
		INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = mpo.m_inoutline_id)
		INNER JOIN c_invoiceline as il ON (il.c_invoiceline_id = mpo.c_invoiceline_id)
		INNER JOIN m_inout as io ON (io.m_inout_id = iol.m_inout_id)
		WHERE	il.c_invoice_id = p_c_invoice_id AND io.docstatus IN ('CO','CL')
		ORDER BY io.documentno DESC
		LIMIT 1;
			EXCEPTION	--	No encontrado; posiblememte mal llamado
			WHEN OTHERS THEN
			RAISE NOTICE 'InvoiceAvailable - %', SQLERRM;
				RETURN NULL;
		END;
	END IF;
	
	IF (v_nro_Remito IS NULL) THEN 
		BEGIN
		-- Obtener el remito directamente desde el pedido mismo, para los casos	también de issotrx = 'Y'
		SELECT	distinct documentno
		  INTO
				v_nro_Remito
		FROM       m_inout
		WHERE	c_invoice_id = p_c_invoice_id AND docstatus IN ('CO','CL')
		ORDER BY documentno DESC
		LIMIT 1;
			EXCEPTION	--	No encontrado; posiblememte mal llamado
			WHEN OTHERS THEN
			RAISE NOTICE 'InvoiceAvailable - %', SQLERRM;
				RETURN NULL;
		END;
	END IF;

	IF (v_nro_Remito IS NULL) THEN
		RETURN getInOutsDocumentsNoFromOrder(v_order_id);
	END IF;
	RETURN	v_nro_Remito;
END;

$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;  

--20160915-1955 Incorporación de funcionalidad de sólo lectura, callout y callout al cargar en parámetros de proceso
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Process_Para','isreadonly','character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Process_Para','callout','character varying(255)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Process_Para','calloutalsoonload','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20160920-1428 Nueva función para obtener el pendiente de OP y RC
CREATE OR REPLACE FUNCTION POCRAvailable(p_c_allocationhdr_id integer)
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
 * Title:	Calculate Open Allocation Amount

 ************************************************************************/
DECLARE
	v_OpenAmt		NUMERIC := 0;
    	r   			RECORD;
BEGIN   
	--	Calculate Open Amount
	FOR r IN
		SELECT	al.AD_Client_ID, al.AD_Org_ID, al.C_Payment_ID, al.C_CashLine_ID, al.C_Invoice_Credit_ID
			FROM	C_AllocationLine al
	         INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID=a.C_AllocationHdr_ID)
			WHERE (a.IsActive='Y') AND (a.C_AllocationHdr_ID=p_c_allocationhdr_id)
	LOOP
		IF (r.C_Payment_ID IS NOT NULL) THEN
			v_OpenAmt := v_OpenAmt + paymentavailable(r.C_Payment_ID);
		ELSIF (r.C_CashLine_ID IS NOT NULL) THEN
			v_OpenAmt := v_OpenAmt + abs(cashlineavailable(r.C_CashLine_ID));
		ELSIF (r.C_Invoice_Credit_ID IS NOT NULL) THEN
			v_OpenAmt := v_OpenAmt + invoiceopen(r.C_Invoice_Credit_ID, (SELECT ps.C_InvoicePaySchedule_ID FROM C_InvoicePaySchedule ps WHERE (r.C_Invoice_Credit_ID = ps.C_Invoice_ID) ORDER BY CREATED DESC LIMIT 1));
		ELSE
			v_OpenAmt := v_OpenAmt + 0; 
		END IF;
	END LOOP;
	--  NO en libertya:	Round to penny
	-- en vez se redondea usando la moneda
	--v_OpenAmt := currencyRound(COALESCE(v_OpenAmt,0),118,NULL); 
	RETURN	v_OpenAmt;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION POCRAvailable(integer)
  OWNER TO libertya;
  
--20160929-1120 Incorporación de Copiar Desde en Transferencias de Mercadería. Merge de Revision 1571
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_Transfer','CopyFrom','character(1)'));

--20160929-1155 Incorporación de copia de artículos entre proveedores. Merge de Revision 1571
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_BPartner','CopyVendorProducts','character(1)'));

--20161008-1500 Incorporación de campos que permiten exportar a archivo el valor real de una columna
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Field','exportrealvalue','character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_PrintFormatItem','exportrealvalue','character(1) NOT NULL DEFAULT ''N''::bpchar'));

DROP VIEW ad_field_v;
DROP VIEW ad_field_vt;

CREATE OR REPLACE VIEW ad_field_vt AS 
 SELECT trl.ad_language, t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, trl.name, trl.description, trl.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, 
        CASE
            WHEN f.defaultvalue IS NULL THEN c.defaultvalue
            ELSE f.defaultvalue
        END AS defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, 
        CASE
            WHEN f.ad_val_rule_id IS NULL THEN c.ad_val_rule_id
            ELSE f.ad_val_rule_id
        END AS ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fgt.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload, f.exportrealvalue
   FROM ad_field f
   JOIN ad_field_trl trl ON f.ad_field_id = trl.ad_field_id
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup_trl fgt ON f.ad_fieldgroup_id = fgt.ad_fieldgroup_id AND trl.ad_language::text = fgt.ad_language::text
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON COALESCE(f.ad_val_rule_id, c.ad_val_rule_id) = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;

ALTER TABLE ad_field_vt
  OWNER TO libertya;

CREATE OR REPLACE VIEW ad_field_v AS 
 SELECT t.ad_window_id, f.ad_tab_id, f.ad_field_id, tbl.ad_table_id, f.ad_column_id, f.name, f.description, f.help, f.isdisplayed, f.displaylogic, f.displaylength, f.seqno, f.sortno, f.issameline, f.isheading, f.isfieldonly, f.isreadonly, f.isencrypted AS isencryptedfield, f.obscuretype, c.columnname, c.columnsql, c.fieldlength, c.vformat, 
        CASE
            WHEN f.defaultvalue IS NULL THEN c.defaultvalue
            ELSE f.defaultvalue
        END AS defaultvalue, c.iskey, c.isparent, c.ismandatory, c.isidentifier, c.istranslated, c.ad_reference_value_id, c.callout, c.ad_reference_id, 
        CASE
            WHEN f.ad_val_rule_id IS NULL THEN c.ad_val_rule_id
            ELSE f.ad_val_rule_id
        END AS ad_val_rule_id, c.ad_process_id, c.isalwaysupdateable, c.readonlylogic, c.isupdateable, c.isencrypted AS isencryptedcolumn, c.isselectioncolumn, tbl.tablename, c.valuemin, c.valuemax, fg.name AS fieldgroup, vr.code AS validationcode, f.isdisplayedingrid, c.calloutalsoonload, f.exportrealvalue
   FROM ad_field f
   JOIN ad_tab t ON f.ad_tab_id = t.ad_tab_id
   LEFT JOIN ad_fieldgroup fg ON f.ad_fieldgroup_id = fg.ad_fieldgroup_id
   LEFT JOIN ad_column c ON f.ad_column_id = c.ad_column_id
   JOIN ad_table tbl ON c.ad_table_id = tbl.ad_table_id
   JOIN ad_reference r ON c.ad_reference_id = r.ad_reference_id
   LEFT JOIN ad_val_rule vr ON COALESCE(f.ad_val_rule_id, c.ad_val_rule_id) = vr.ad_val_rule_id
  WHERE f.isactive = 'Y'::bpchar AND c.isactive = 'Y'::bpchar;

ALTER TABLE ad_field_v
  OWNER TO libertya;

--20161010-2350 Incorporación de check que permite restringir la compra sólo a los artículos del proveedor
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_DocType','onlyvendorproducts','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20161018-1630 Impresión de cheques. Merge Revisión 1583
CREATE TABLE C_CheckPrinting(

c_checkprinting_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_bankaccount_id integer NOT NULL ,
c_doctype_id integer NOT NULL ,
markprinted character(1) ,
getlines character(1) ,
CONSTRAINT c_checkprinting_pkey PRIMARY KEY (c_checkprinting_id) ,
CONSTRAINT c_bankaccount_fk FOREIGN KEY (c_bankaccount_id) REFERENCES c_bankaccount (c_bankaccount_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT c_doctype_fk FOREIGN KEY (c_doctype_id) REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_CheckPrinting OWNER TO libertya;

CREATE TABLE C_CheckPrintingLines(

c_checkprintinglines_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_checkprinting_id integer NOT NULL ,
c_bpartner_id integer NOT NULL ,
c_currency_id integer NOT NULL ,
c_payment_id integer NOT NULL ,
print character(1) NOT NULL DEFAULT 'N'::bpchar ,
printed character(1) NOT NULL DEFAULT 'N'::bpchar ,
docstatus character(2) NOT NULL ,
checkno character varying(20) ,
description character varying(255) ,
tendertype character(1) NOT NULL ,
payamt numeric(20,2) NOT NULL DEFAULT 0 ,
dateemissioncheck timestamp without time zone ,
datetrx timestamp without time zone NOT NULL ,
CONSTRAINT c_checkprintinglines_key PRIMARY KEY (c_checkprintinglines_id) ,
CONSTRAINT adclient_fk FOREIGN KEY (ad_client_id) REFERENCES ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT adorg_fk FOREIGN KEY (ad_org_id) REFERENCES ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cbpartner_fk FOREIGN KEY (c_bpartner_id) REFERENCES c_bpartner (c_bpartner_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE  ,
CONSTRAINT ccheckprinting_fk FOREIGN KEY (c_checkprinting_id) REFERENCES c_checkprinting (c_checkprinting_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT ccurrency_fk FOREIGN KEY (c_currency_id) REFERENCES c_currency (c_currency_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cpayment_fk FOREIGN KEY (c_payment_id) REFERENCES c_payment (c_payment_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_CheckPrintingLines OWNER TO libertya;

--20161024-1230 Generación automática de OP. Merge de Revision 1547, 1557
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_bpartner','batch_payment_rule','character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_bpartner','c_bankaccount_id','integer'));
ALTER TABLE c_bpartner ADD CONSTRAINT bankaccount_cbpartner FOREIGN KEY (c_bankaccount_id) REFERENCES c_bankaccount (c_bankaccount_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE C_PaymentBatchPO(

c_paymentbatchpo_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_doctype_id integer NOT NULL ,
documentno character varying(30) NOT NULL ,
batchdate timestamp without time zone NOT NULL ,
description character varying(255) ,
paymentdaterule character varying(1) ,
adddays integer ,
paymentdate timestamp without time zone ,
grandtotal numeric(20,2) NOT NULL DEFAULT 0 ,
generatepaymentproposal character(1) NOT NULL DEFAULT 'N'::bpchar ,
removepaymentproposal character(1) ,
docstatus character(2) NOT NULL ,
docaction character(2) NOT NULL ,
processed character(1) NOT NULL DEFAULT 'N'::bpchar ,
generateelectronicpayments character(1) ,
c_doctypealloctarget_id integer NOT NULL DEFAULT 1010569 ,
CONSTRAINT c_paymentbatchpo_key PRIMARY KEY (c_paymentbatchpo_id) ,
CONSTRAINT cdoctype_paymentbatchpo FOREIGN KEY (c_doctype_id) REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cdoctypetarget_paymentbatchpo FOREIGN KEY (c_doctypealloctarget_id) REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_PaymentBatchPO OWNER TO libertya;

CREATE TABLE C_PaymentBatchPODetail(

c_paymentbatchpodetail_id integer NOT NULL ,
c_paymentbatchpo_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_bpartner_id integer NOT NULL ,
batch_payment_rule character(1) ,
c_bankaccount_id integer ,
c_bank_id integer ,
firstduedate timestamp without time zone ,
lastduedate timestamp without time zone ,
paymentdate timestamp without time zone ,
paymentamount numeric(20,2) ,
c_allocationhdr_id integer ,
CONSTRAINT c_paymentbatchpodetail_key PRIMARY KEY (c_paymentbatchpodetail_id) ,
CONSTRAINT bank_cpaymentbatchpodetail FOREIGN KEY (c_bank_id) REFERENCES c_bank (c_bank_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT bankaccount_cpaymentbatchpodetail FOREIGN KEY (c_bankaccount_id) REFERENCES c_bankaccount (c_bankaccount_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT callocation_cpaymentbatchpodetail FOREIGN KEY (c_allocationhdr_id) REFERENCES c_allocationhdr (c_allocationhdr_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cbpartner_cpaymentbatchpodetail FOREIGN KEY (c_bpartner_id) REFERENCES c_bpartner (c_bpartner_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cpaymentbatchpo_cpaymentbatchpodetail FOREIGN KEY (c_paymentbatchpo_id) REFERENCES c_paymentbatchpo (c_paymentbatchpo_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_PaymentBatchPODetail OWNER TO libertya;

CREATE TABLE C_PaymentBatchPOInvoices(

c_paymentbatchpoinvoices_id integer NOT NULL ,
c_paymentbatchpodetail_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_invoice_id integer NOT NULL ,
documentno character varying(30) NOT NULL ,
dateinvoiced timestamp without time zone NOT NULL ,
duedate timestamp without time zone NOT NULL ,
invoiceamount numeric(20,2) NOT NULL ,
openamount numeric(20,2) NOT NULL ,
paymentamount numeric(20,2) NOT NULL ,
c_invoicepayschedule_id integer ,
CONSTRAINT c_paymentbatchpoinvoices_key PRIMARY KEY (c_paymentbatchpoinvoices_id) ,
CONSTRAINT c_invoicepayschedule_cpaymentbatchpoinvoices FOREIGN KEY (c_invoicepayschedule_id) REFERENCES c_invoicepayschedule (c_invoicepayschedule_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cinvoice_cpaymentbatchpodetail FOREIGN KEY (c_invoice_id) REFERENCES c_invoice (c_invoice_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cpaymentbatchpodetail_cpaymentbatchpoinvoices FOREIGN KEY (c_paymentbatchpodetail_id) REFERENCES c_paymentbatchpodetail (c_paymentbatchpodetail_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE  );
ALTER TABLE C_PaymentBatchPOInvoices OWNER TO libertya;

--20161024-1930 Refactorización de pagos electrónicos - Merge de revision 1570
CREATE TABLE C_ElectronicPaymentBranch(

c_electronicpaymentbranch_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
value character varying(40) NOT NULL ,
name character varying(60) NOT NULL ,
c_bank_id integer ,
c_location_id integer ,
CONSTRAINT c_electronicpaymentbranch_key PRIMARY KEY (c_electronicpaymentbranch_id) ,
CONSTRAINT adclient_electronicpaymentbranch FOREIGN KEY (ad_client_id) REFERENCES ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT adorg_electronicpaymentbranch FOREIGN KEY (ad_org_id) REFERENCES ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_bankaccount','electronicpaymentsaccount','character(1) DEFAULT ''N'''));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_banklist_config','c_bank_id','integer'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_banklist_config','paymenttype','character varying(2)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_bpartner_banklist','c_electronicpaymentbranch_id','integer'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_banklist','c_bankaccount_id','integer NOT NULL'));

ALTER TABLE c_bpartner_banklist ALTER COLUMN c_doctype_id DROP NOT NULL;
   
ALTER TABLE c_banklistline
  DROP CONSTRAINT banklistline_checklist;

ALTER TABLE c_banklistline
  ADD CONSTRAINT banklistline_checklist FOREIGN KEY (c_banklist_id)
      REFERENCES c_banklist (c_banklist_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE;

CREATE OR REPLACE VIEW c_electronic_payments AS 
 SELECT p.ad_client_id, p.ad_org_id, p.created, p.createdby, p.updated, p.updatedby, dt.c_doctype_id, p.c_payment_id, p.documentno, p.docstatus, p.datetrx, p.dateacct, p.c_bankaccount_id, p.checkno, p.c_currency_id, p.payamt, p.tendertype, p.c_bpartner_id, p.a_name, p.isreconciled, p.duedate, p.dateemissioncheck, p.checkstatus, p.rejecteddate, p.rejectedcomments, bl.c_banklist_id, bl.documentno AS banklist_documentno, bl.docstatus AS banklist_docstatus, ( SELECT ah.c_allocationhdr_id
           FROM c_allocationhdr ah
      JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
     WHERE ah.allocationtype::text = 'OP'::text AND al.c_payment_id = p.c_payment_id AND (ah.docstatus <> ALL (ARRAY['IP'::bpchar, 'DR'::bpchar]))
    LIMIT 1) AS c_allocationhdr_id
   FROM c_payment p
   JOIN c_banklistline bll ON bll.c_payment_id = p.c_payment_id
   JOIN c_banklist bl ON bl.c_banklist_id = bll.c_banklist_id
   JOIN c_doctype dt ON dt.c_doctype_id = bl.c_doctype_id
  WHERE dt.docbasetype::text = 'BLB'::text AND (bl.docstatus <> ALL (ARRAY['IP'::bpchar, 'DR'::bpchar])) AND (p.docstatus <> ALL (ARRAY['IP'::bpchar, 'DR'::bpchar]));

CREATE OR REPLACE VIEW c_electronic_notpayments AS 
 SELECT p.ad_client_id, p.ad_org_id, p.created, p.createdby, p.updated, p.updatedby, p.c_payment_id, p.documentno, p.docstatus, p.datetrx, p.dateacct, p.c_bankaccount_id, p.checkno, p.c_currency_id, p.payamt, p.tendertype, p.c_bpartner_id, p.a_name, p.isreconciled, p.duedate, p.dateemissioncheck, p.checkstatus, p.rejecteddate, p.rejectedcomments, NULL::unknown AS c_banklist_id, NULL::unknown AS banklist_documentno, NULL::unknown AS banklist_docstatus, ( SELECT ah.c_allocationhdr_id
           FROM c_allocationhdr ah
      JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
     WHERE ah.allocationtype::text = 'OP'::text AND al.c_payment_id = p.c_payment_id AND (ah.docstatus <> ALL (ARRAY['IP'::bpchar, 'DR'::bpchar]))
    LIMIT 1) AS c_allocationhdr_id
   FROM c_payment p
  WHERE (p.docstatus <> ALL (ARRAY['IP'::bpchar, 'DR'::bpchar])) AND NOT (EXISTS ( SELECT lpp.c_payment_id
           FROM c_electronic_payments lpp
          WHERE lpp.c_payment_id = p.c_payment_id));

DROP VIEW c_lista_galicia_notpayments;
DROP VIEW c_lista_galicia_payments;
DROP VIEW c_lista_patagonia_notpayments;
DROP VIEW c_lista_patagonia_payments;

--20161024-2030 Merge de revisiones 1604 y 1609
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_BankListLine','C_AllocationHdr_ID','integer'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_allocationhdr','c_banklist_id','integer'));

CREATE OR REPLACE VIEW c_electronic_payments AS 
SELECT
   p.ad_client_id,
   p.ad_org_id,
   p.created,
   p.createdby,
   p.updated,
   p.updatedby,
   dt.c_doctype_id,
   p.c_payment_id,
   p.documentno,
   p.docstatus,
   p.datetrx,
   p.dateacct,
   p.c_bankaccount_id,
   p.checkno,
   p.c_currency_id,
   p.payamt,
   p.tendertype,
   p.c_bpartner_id,
   p.a_name,
   p.isreconciled,
   p.duedate,
   p.dateemissioncheck,
   p.checkstatus,
   p.rejecteddate,
   p.rejectedcomments,
   bl.c_banklist_id,
   bl.documentno AS banklist_documentno,
   bl.docstatus AS banklist_docstatus,
   ( SELECT
      ah.c_allocationhdr_id             
   FROM
      c_allocationhdr ah        
   JOIN
      c_allocationline al 
         ON ah.c_allocationhdr_id = al.c_allocationhdr_id       
   WHERE
      ah.allocationtype::text = 'OP'::text 
      AND al.c_payment_id = p.c_payment_id 
      AND (
         ah.docstatus <> ALL (
            ARRAY['IP'::bpchar, 'DR'::bpchar]
         )
      )      LIMIT 1) AS c_allocationhdr_id     
FROM
   c_payment p     
JOIN 
   c_allocationline al
      ON al.c_payment_id = p.c_payment_id
JOIN
   c_banklistline bll 
      ON bll.c_allocationhdr_id = al.c_allocationhdr_id
JOIN
   c_banklist bl 
      ON bl.c_banklist_id = bll.c_banklist_id     
JOIN
   c_doctype dt 
      ON dt.c_doctype_id = bl.c_doctype_id    
WHERE
   dt.docbasetype::text = 'BLB'::text 
   AND (
      bl.docstatus <> ALL (
         ARRAY['IP'::bpchar, 'DR'::bpchar]
      )
   ) 
   AND (
      p.docstatus <> ALL (
         ARRAY['IP'::bpchar, 'DR'::bpchar]
      )
   );
   
ALTER TABLE c_banklistline DROP COLUMN c_payment_id;
ALTER TABLE c_banklist DROP COLUMN c_allocationhdr_id; 

--20161025-1500 Merge de revision 1608
CREATE TABLE I_PaymentBankNews(

i_paymentbanknews_id integer NOT NULL ,
ad_client_id integer ,
ad_org_id integer ,
isactive character(1) DEFAULT 'Y'::bpchar ,
created timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer ,
updated timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer ,
list_type character(1) ,
list_value character varying(8) ,
payment_status character(2) ,
payment_status_msg character varying(60) ,
receipt_number character varying(15) ,
process_date timestamp without time zone ,
payment_order character varying(25) ,
c_bank_id integer ,
i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar ,
i_errormsg character varying(2000) ,
checkno character varying(20) ,
processing character(1) ,
processed character(1) DEFAULT 'N'::bpchar ,
register_number character varying(15) ,
CONSTRAINT i_paymentbanknews_key PRIMARY KEY (i_paymentbanknews_id) );

CREATE TABLE C_BankPaymentStatus(

c_bankpaymentstatus_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
value character varying(40) NOT NULL ,
name character varying(60) NOT NULL ,
CONSTRAINT c_bankpaymentstatus_key PRIMARY KEY (c_bankpaymentstatus_id) );

CREATE TABLE C_BankPaymentStatusAssociation(

c_bankpaymentstatusassociation_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_bank_id integer NOT NULL ,
value character varying(40) NOT NULL ,
name character varying(60) NOT NULL ,
c_bankpaymentstatus_id integer NOT NULL ,
CONSTRAINT c_bankpaymentstatusassociation_key PRIMARY KEY (c_bankpaymentstatusassociation_id) ,
CONSTRAINT c_bank_fk FOREIGN KEY (c_bank_id) REFERENCES c_bank (c_bank_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT c_bankpaymentstatus_fk FOREIGN KEY (c_bankpaymentstatus_id) REFERENCES c_bankpaymentstatus (c_bankpaymentstatus_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ImpFormat','AD_Process_ID','integer'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_Payment','Bank_Payment_DocumentNo','character varying(25)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_Payment','Bank_Payment_Date','timestamp without time zone'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_Payment','C_Bankpaymentstatus_ID','integer'));

--20161027-1600 Incorporación de los comprobantes con importe de impuesto 0 e iva distinto a exento, transformados a exentos en las vistas, para las exportaciones CITI
CREATE OR REPLACE FUNCTION getcodigooperacion(p_c_invoice_id integer)
  RETURNS character AS
 $BODY$ 
 DECLARE     	v_CodigoOperacion        	character(1); 
 BEGIN     
 SELECT CASE WHEN (COUNT(*) >= 1) THEN t.codigooperacion ELSE NULL END     INTO v_CodigoOperacion     
 FROM (SELECT CASE WHEN it.taxamt = 0 AND t.rate <> 0 AND te.c_tax_id > 0 THEN te.codigooperacion ELSE t.codigooperacion END as codigooperacion
	 FROM C_Invoicetax it     
	 INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID) 
	 LEFT JOIN (select * from c_tax where rate = 0 and isactive = 'Y' AND ispercepcion = 'N') as te on te.ad_client_id = it.ad_client_id    
	 WHERE (C_Invoice_ID = p_c_invoice_id) AND (t.rate = 0 OR (t.rate <> 0 AND it.taxamt = 0))) as t
 GROUP BY t.codigooperacion;     
 RETURN v_CodigoOperacion; 
 END; $BODY$ 
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getcodigooperacion(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION getimporteoperacionexentas(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ DECLARE     	v_Amount        	NUMERIC; 
BEGIN     
SELECT COALESCE(SUM(it.taxbaseamt), 0)     INTO v_Amount     
FROM C_Invoicetax it     
INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)     
WHERE (C_Invoice_ID = p_c_invoice_id) AND getcantidadalicuotasiva(p_c_invoice_id) > 1 AND (isPercepcion = 'N') AND (t.rate = 0 OR (t.rate <> 0 AND it.taxamt = 0));        
RETURN v_Amount; 
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getimporteoperacionexentas(integer)
  OWNER TO libertya;
  
CREATE OR REPLACE FUNCTION getimporteoperacionesexentas(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ DECLARE     	v_Amount        	NUMERIC; 
BEGIN     
SELECT COALESCE(SUM(it.TaxBaseAmt), 0) INTO v_Amount     
FROM C_Invoicetax it 
INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)     
WHERE (t.rate = 0 OR t.IsTaxExempt = 'Y' OR (t.rate <> 0 AND it.taxamt = 0)) AND C_Invoice_ID = p_c_invoice_id;          
RETURN v_Amount; 
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getimporteoperacionesexentas(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION getcantidadalicuotasiva(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ DECLARE     	v_Cant        	NUMERIC; 
BEGIN     
SELECT COUNT(distinct c_tax_id)     INTO v_Cant     
FROM (SELECT CASE WHEN it.taxamt = 0 AND t.rate <> 0 AND te.c_tax_id > 0 THEN te.c_tax_id ELSE t.c_tax_id END
	FROM C_Invoicetax it     
	INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)     
	LEFT JOIN (select * from c_tax where rate = 0 and isactive = 'Y' AND ispercepcion = 'N') as te on te.ad_client_id = it.ad_client_id
	WHERE (C_Invoice_ID = p_c_invoice_id) AND (t.isPercepcion = 'N')) taxes;
RETURN v_Cant; 
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getcantidadalicuotasiva(integer)
  OWNER TO libertya;
  
CREATE OR REPLACE VIEW reginfo_ventas_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, 
	date_trunc('day'::text, i.dateacct) AS fechadecomprobante, ei.codigo AS tipodecomprobante, 
	i.puntodeventa, i.numerocomprobante AS nrocomprobante, 
	currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, 
	CASE WHEN it.taxamt = 0 AND t.rate <> 0 AND te.c_tax_id > 0 THEN te.wsfecode ELSE t.wsfecode END AS alicuotaiva, 
	currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
   LEFT JOIN (select * from c_tax where rate = 0 and isactive = 'Y' AND ispercepcion = 'N') as te on te.ad_client_id = i.ad_client_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)) AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric) ;

ALTER TABLE reginfo_ventas_alicuotas_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_ventas_cbte_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, 
	date_trunc('day'::text, i.dateacct) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, 
	i.numerocomprobante AS nrocomprobantehasta, 
	CASE
            WHEN bp.taxidtype = '99'::bpchar AND i.grandtotal > 1000::numeric THEN '96'::bpchar
            ELSE bp.taxidtype
        END::character(2) AS codigodoccomprador, 
        gettaxid(bp.taxid, bp.taxidtype, bp.c_categoria_iva_id, i.nroidentificcliente, i.grandtotal)::character varying(20) AS nroidentificacioncomprador, 
        bp.name AS nombrecomprador, 
        currencyconvert(i.grandtotal, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal, 
	0::numeric(20,2) AS impconceptosnoneto, 
	0::numeric(20,2) AS imppercepnocategorizados, 
	currencyconvert(getimporteoperacionexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas, 
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac, 
	currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, 
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni, 
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos, 
	cu.wsfecode AS codmoneda, 
	currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, 
        CASE
            WHEN getimporteoperacionexentas(i.c_invoice_id) > 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva, 
        getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, 
        currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, 
	NULL::timestamp without time zone AS fechavencimientopago
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar));

ALTER TABLE reginfo_ventas_cbte_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_compras_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS date, 
	date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, 
	gettipodecomprobante(dt.doctypekey, l.letra)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, 
        currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, 
	CASE WHEN it.taxamt = 0 AND t.rate <> 0 AND te.c_tax_id > 0 THEN te.wsfecode ELSE t.wsfecode END AS alicuotaiva, 
	currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
   LEFT JOIN (select * from c_tax where rate = 0 and isactive = 'Y' AND ispercepcion = 'N') as te on te.ad_client_id = i.ad_client_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND (l.letra <> ALL (ARRAY['B'::bpchar, 'C'::bpchar])) AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric);

ALTER TABLE reginfo_compras_alicuotas_v
  OWNER TO libertya;
  
--20161028-1700 Funcionalidad para soporte de cajas diarias para compras, ventas o ambos
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('ad_clientinfo','posjournalapplication','character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_cash','validateposjournal','character(1) NOT NULL DEFAULT ''N''::bpchar'));

UPDATE ad_clientinfo
SET posjournalapplication = (CASE WHEN isposjournalactive = 'Y' THEN 'B' ELSE null END);

UPDATE c_cash c
SET validateposjournal = (CASE WHEN (SELECT posjournalapplication FROM ad_clientinfo ci WHERE ci.ad_client_id = c.ad_client_id) = 'B' 
								AND EXISTS (SELECT c_cashbook_id FROM c_cashbook cb WHERE cb.c_cashbook_id = c.c_cashbook_id AND cashbooktype = 'J') THEN 'Y' 
						ELSE 'N' END);
						
--20161101-1605 Eliminaciones de metadatos que no se eliminaron vía copytochangelog
delete from ad_field_trl
where ad_componentobjectuid in ('CORE-AD_Field_Trl-1017995-es_AR','CORE-AD_Field_Trl-1017995-es_ES',
				'CORE-AD_Field_Trl-1017995-es_MX','CORE-AD_Field_Trl-1017995-es_PY',
				'CORE-AD_Field_Trl-1018046-es_AR','CORE-AD_Field_Trl-1018046-es_ES',
				'CORE-AD_Field_Trl-1018046-es_MX','CORE-AD_Field_Trl-1018046-es_PY',
				'CORE-AD_Field_Trl-1018019-es_AR','CORE-AD_Field_Trl-1018019-es_ES',
				'CORE-AD_Field_Trl-1018019-es_MX','CORE-AD_Field_Trl-1018019-es_PY',
				'CORE-AD_Field_Trl-1018024-es_AR','CORE-AD_Field_Trl-1018024-es_ES',
				'CORE-AD_Field_Trl-1018024-es_MX','CORE-AD_Field_Trl-1018024-es_PY');

delete from ad_field
where ad_componentobjectuid in ('CORE-AD_Field-1018024','CORE-AD_Field-1017995', 'CORE-AD_Field-1018046',
				'CORE-AD_Field-1018019','SSTE2CORE-AD_Field-1018390-20161025150911',
				'SSTE2CORE-AD_Field-1018391-20161025150914');

delete from ad_column_trl
where ad_componentobjectuid in ('CORE-AD_Column_Trl-1017003-es_PY','CORE-AD_Column_Trl-1017003-es_ES',
				'CORE-AD_Column_Trl-1017003-es_AR','CORE-AD_Column_Trl-1017003-es_MX');

delete from ad_column
where ad_componentobjectuid in ('CORE-AD_Column-1016989','SSTE2CORE-AD_Column-1017469-20161024200223',
				'CORE-AD_Column-1017003','SSTE2CORE-AD_Column-1017314-20161025150429',
				'SSTE2CORE-AD_Column-1017319-20161025150445','SSTE2CORE-AD_Column-1017322-20161025150455',
				'SSTE2CORE-AD_Column-1017316-20161025150436');

delete from ad_menu_trl
where ad_componentobjectuid in ('CORE-AD_Menu_Trl-es_AR-1010568',
				'CORE-AD_Menu_Trl-es_ES-1010568',
				'CORE-AD_Menu_Trl-es_MX-1010568',
				'CORE-AD_Menu_Trl-es_PY-1010568');

delete from ad_menu
where ad_componentobjectuid in ('CORE-AD_Menu-1010568','SSTE2CORE-AD_Menu-1010597-20161025150820');

--20161102-1705 Modificación a la vista del informe detalles del pago
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
            ELSE (-1.0)
        END AS multiplierap, paymentallocated(c_payment.c_payment_id::numeric, c_payment.c_currency_id::numeric) AS allocatedamt, paymentavailable(c_payment.c_payment_id::numeric) AS availableamt, c_payment.isoverunderpayment, c_payment.isapproved, c_payment.r_pnref, c_payment.r_result, c_payment.r_respmsg, c_payment.r_authcode, c_payment.r_avsaddr, c_payment.r_avszip, c_payment.r_info, c_payment.processing, c_payment.oprocessing, c_payment.docstatus, c_payment.docaction, c_payment.isprepayment, c_payment.c_charge_id, c_payment.isreconciled, c_payment.isallocated, c_payment.isonline, c_payment.processed, c_payment.posted, c_payment.dateacct, b.name as bank
   FROM c_payment
   INNER JOIN C_BankAccount ba on ba.c_bankaccount_id = c_payment.c_bankaccount_id
   INNER JOIN C_Bank b on b.c_bank_id = ba.c_bank_id ;

ALTER TABLE rv_payment
  OWNER TO libertya;

--20161105-1910 Eliminación de entradas de menú que no se copiaron al changelog al realizar merge de requerimientos
DELETE FROM AD_TreeNodeMM
where ad_componentobjectuid IN ('SSTE2CORE-AD_TreeNodeMM-1010116-1010589-20161025151056',
				'CORE-AD_TreeNodeMM-1010115-1010568',
				'CORE-AD_TreeNodeMM-10-1010568',
				'CORE-AD_TreeNodeMM-1010115-1010567',
				'CORE-AD_TreeNodeMM-1010115-1010566',
				'CORE-AD_TreeNodeMM-106-1010115',
				'CORE-AD_TreeNodeMM-1010115-1010569',
				'CORE-AD_TreeNodeMM-1010115-1010572');

--20161108-1500 Permitir copia de registros
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Tab','allowcopyrecord','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

DROP VIEW ad_tab_v;
DROP VIEW ad_tab_vt;

CREATE OR REPLACE VIEW ad_tab_v AS 
 SELECT t.ad_tab_id, t.ad_window_id, t.ad_table_id, t.name, t.description, t.help, t.seqno, t.issinglerow, t.hastree, t.isinfotab, tbl.replicationtype, tbl.tablename, tbl.accesslevel, tbl.issecurityenabled, tbl.isdeleteable, tbl.ishighvolume, tbl.isview, 'N' AS hasassociation, t.istranslationtab, t.isreadonly, t.ad_image_id, t.tablevel, t.whereclause, t.orderbyclause, t.commitwarning, t.readonlylogic, t.displaylogic, t.ad_column_id, t.ad_process_id, t.issorttab, t.isinsertrecord, t.isadvancedtab, t.ad_columnsortorder_id, t.ad_columnsortyesno_id, t.included_tab_id, t.isprocessmsgshowdialog, t.isalwaysupdateable, t.allowcopyrecord
   FROM ad_tab t
   JOIN ad_table tbl ON t.ad_table_id = tbl.ad_table_id
  WHERE t.isactive = 'Y'::bpchar AND tbl.isactive = 'Y'::bpchar;

ALTER TABLE ad_tab_v
  OWNER TO libertya;


CREATE OR REPLACE VIEW ad_tab_vt AS 
 SELECT trl.ad_language, t.ad_tab_id, t.ad_window_id, t.ad_table_id, trl.name, trl.description, trl.help, t.seqno, t.issinglerow, t.hastree, t.isinfotab, tbl.replicationtype, tbl.tablename, tbl.accesslevel, tbl.issecurityenabled, tbl.isdeleteable, tbl.ishighvolume, tbl.isview, 'N' AS hasassociation, t.istranslationtab, t.isreadonly, t.ad_image_id, t.tablevel, t.whereclause, t.orderbyclause, trl.commitwarning, t.readonlylogic, t.displaylogic, t.ad_column_id, t.ad_process_id, t.issorttab, t.isinsertrecord, t.isadvancedtab, t.ad_columnsortorder_id, t.ad_columnsortyesno_id, t.included_tab_id, t.isprocessmsgshowdialog, t.isalwaysupdateable, t.allowcopyrecord
   FROM ad_tab t
   JOIN ad_table tbl ON t.ad_table_id = tbl.ad_table_id
   JOIN ad_tab_trl trl ON t.ad_tab_id = trl.ad_tab_id
  WHERE t.isactive = 'Y'::bpchar AND tbl.isactive = 'Y'::bpchar;

ALTER TABLE ad_tab_vt
  OWNER TO libertya;

--20161114-1713 Redefinición de las cadenas de autorizaciones

ALTER TABLE M_AuthorizationChain DROP CONSTRAINT cdoctype_mauthorizationchain;

ALTER TABLE M_AuthorizationChain ALTER COLUMN C_DocType_ID DROP NOT NULL;

ALTER TABLE M_AuthorizationChainLink ALTER COLUMN MinimumAmount DROP NOT NULL;

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_AuthorizationChainLink','ValidateDocumentAmount','character(1) NOT NULL default ''Y''::bpchar'));

CREATE TABLE libertya.m_authorizationchaindocumenttype
(
  m_authorizationchaindocumenttype_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  m_authorizationchain_id integer NOT NULL,
  C_DocType_ID integer NOT NULL,
 CONSTRAINT m_authorizationchaindocumenttype_key PRIMARY KEY (m_authorizationchaindocumenttype_id),
 CONSTRAINT mauthorizationchain_mauthorizationchaindocumenttype FOREIGN KEY (m_authorizationchain_id)
      REFERENCES libertya.m_authorizationchain (m_authorizationchain_id) MATCH SIMPLE
)
WITH (
  OIDS=TRUE
);
ALTER TABLE libertya.m_authorizationchaindocumenttype
  OWNER TO libertya;
  
--20161115-1903 Fixes a las vistas de exportaciones CITI
CREATE OR REPLACE VIEW reginfo_ventas_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, 
	date_trunc('day'::text, i.dateacct) AS fechadecomprobante, ei.codigo AS tipodecomprobante, 
	i.puntodeventa, i.numerocomprobante AS nrocomprobante, 
	currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, 
	CASE WHEN it.taxamt = 0 AND t.rate <> 0 AND te.c_tax_id > 0 THEN te.wsfecode ELSE t.wsfecode END AS alicuotaiva, 
	currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
   LEFT JOIN (select * from c_tax where rate = 0 and isactive = 'Y' AND ispercepcion = 'N') as te on te.ad_client_id = i.ad_client_id
  WHERE t.ispercepcion = 'N'::bpchar 
  AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) 
  AND i.issotrx = 'Y'::bpchar 
  AND i.isactive = 'Y'::bpchar 
  AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
  AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar));

ALTER TABLE reginfo_ventas_alicuotas_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_compras_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS date, 
	date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, 
	gettipodecomprobante(dt.doctypekey, l.letra)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, 
        currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, 
	CASE WHEN it.taxamt = 0 AND t.rate <> 0 AND te.c_tax_id > 0 THEN te.wsfecode ELSE t.wsfecode END AS alicuotaiva, 
	currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
   LEFT JOIN (select * from c_tax where rate = 0 and isactive = 'Y' AND ispercepcion = 'N') as te on te.ad_client_id = i.ad_client_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND (l.letra <> ALL (ARRAY['B'::bpchar, 'C'::bpchar]));

ALTER TABLE reginfo_compras_alicuotas_v
  OWNER TO libertya;

--20161123-1642 Correcciones a las vistas de exportación CITI
CREATE OR REPLACE FUNCTION getgrandtotal(
    invoiceid integer,
    netbased boolean)
  RETURNS numeric AS
$BODY$
DECLARE
	total	NUMERIC := 0;
	r	record;
BEGIN
	if (invoiceID > 0)
	then 
		select into r i.c_invoice_id, i.grandtotal, sum((CASE WHEN tc.ismanual = 'N' THEN it.taxbaseamt ELSE 0 END) + it.taxamt) as grandtotal_netbased
			from c_invoice i
			inner join c_invoicetax it on it.c_invoice_id = i.c_invoice_id
			inner join c_tax t on t.c_tax_id = it.c_tax_id
			inner join c_taxcategory tc on tc.c_taxcategory_id = t.c_taxcategory_id
			where i.c_invoice_id = invoiceID
			group by i.c_invoice_id, i.grandtotal;
		if(netbased)
		then
			total = r.grandtotal_netbased;
		else
			total = r.grandtotal;
		end if;
	end if;
	return total; 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getgrandtotal(integer, boolean)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION getimporteoperacionexentas(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ DECLARE     	v_Amount        	NUMERIC; 
BEGIN     
SELECT COALESCE(SUM(it.taxbaseamt), 0)     INTO v_Amount     
FROM C_Invoicetax it     
INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)     
WHERE (C_Invoice_ID = p_c_invoice_id) AND (isPercepcion = 'N') AND (t.rate = 0 OR (t.rate <> 0 AND it.taxamt = 0));        
RETURN v_Amount; 
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getimporteoperacionexentas(integer)
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_ventas_cbte_v AS 
SELECT ad_client_id, ad_org_id, c_invoice_id, date, fechadecomprobante, tipodecomprobante, puntodeventa, nrocomprobante, nrocomprobantehasta, 
	CASE
            WHEN taxidtype = '99'::bpchar AND grandtotal > 1000::numeric THEN '96'::bpchar
            ELSE taxidtype
        END::character(2) AS codigodoccomprador, 
        gettaxid(taxid, taxidtype, c_categoria_iva_id, nroidentificcliente, grandtotal)::character varying(20) AS nroidentificacioncomprador, 
	nombrecomprador, 
        currencyconvert(grandtotal, c_currency_id, 118, fechadecomprobante::timestamp with time zone, NULL::integer, ad_client_id, ad_org_id)::numeric(20,2) AS imptotal,
        impconceptosnoneto,
        imppercepnocategorizados,
        impopeexentas,
        imppercepopagosdeimpunac,
        imppercepiibb,
        imppercepimpumuni,
        impimpuinternos,
        codmoneda,
        tipodecambio,
        cantalicuotasiva,
        codigooperacion,
        impotrostributos,
        fechavencimientopago
FROM (SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, 
	date_trunc('day'::text, i.dateacct) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, 
	i.numerocomprobante AS nrocomprobantehasta, 
        bp.name AS nombrecomprador, 
        bp.taxidtype,
        bp.taxid,
        bp.c_categoria_iva_id, i.nroidentificcliente,
        i.c_currency_id, 
	0::numeric(20,2) AS impconceptosnoneto, 
	0::numeric(20,2) AS imppercepnocategorizados, 
	getgrandtotal(i.c_invoice_id, true) as grandtotal,
	currencyconvert(getimporteoperacionexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas, 
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac, 
	currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, 
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni, 
	currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos, 
	cu.wsfecode AS codmoneda, 
	currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, 
        CASE
            WHEN getimporteoperacionexentas(i.c_invoice_id) > 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva, 
        getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, 
        currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, 
	NULL::timestamp without time zone AS fechavencimientopago
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR (dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar))) as t;

ALTER TABLE reginfo_ventas_cbte_v
  OWNER TO libertya;


CREATE OR REPLACE VIEW reginfo_compras_cbte_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, gettipodecomprobante(dt.doctypekey, l.letra)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN i.importclearance
            ELSE NULL::character varying
        END::character varying(30) AS despachoimportacion, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, bp.name AS nombrevendedor, currencyconvert(getgrandtotal(i.c_invoice_id, true), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal, 0::numeric(20,2) AS impconceptosnoneto, 
        CASE
            WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND i.importclearance IS NULL THEN 0::numeric
            ELSE currencyconvert(getimporteoperacionexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)
        END::numeric(20,2) AS impopeexentas, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosvaloragregado, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos, cu.wsfecode AS codmoneda, currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, 
        CASE
            WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra)::text <> '66'::text THEN 0::numeric
            ELSE 
            CASE
                WHEN getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
                ELSE getcantidadalicuotasiva(i.c_invoice_id)
            END
        END AS cantalicuotasiva, getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impcreditofiscalcomputable, currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, NULL::character varying(20) AS cuitemisorcorredor, NULL::character varying(60) AS denominacionemisorcorredor, 0::numeric(20,2) AS ivacomision
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_compras_cbte_v
  OWNER TO libertya;
  

--20161129-1047 Incorporacinoes Sur Software
CREATE TABLE libertya.i_padron_caba_regimen_general
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
ALTER TABLE libertya.i_padron_caba_regimen_general
  OWNER TO libertya;
GRANT ALL ON TABLE libertya.i_padron_caba_regimen_general TO libertya;

CREATE INDEX i_padron_caba_regimen_general_cuit
  ON libertya.i_padron_caba_regimen_general
  USING btree
  (cuit);
  
ALTER TABLE c_bpartner
	ADD COLUMN builtcabajurisdiction character(1) NOT NULL DEFAULT 'N';

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Org_Percepcion','UseCABAJurisdiction','character(1)'));

--20161129-1100 Flag que determina líneas de caja generadas automáticamente
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_CashLine','automaticgenerated','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20161130-1345 Merge de Revision 1679 - Carga de Liquidaciones de Tarjetas
CREATE TABLE C_CreditCardSettlement(

c_creditcardsettlement_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
m_entidadfinanciera_id integer NOT NULL ,
paymentdate timestamp without time zone ,
payment character varying(256) ,
amount numeric(24,2) ,
netamount numeric(24,2) ,
withholding numeric(24,2) ,
perception numeric(24,2) ,
expenses numeric(24,2) ,
couponstotalamount numeric(24,2) ,
docstatus character(2) NOT NULL ,
isreconciled character(1) NOT NULL DEFAULT 'N'::bpchar ,
posted character(1) ,
docaction character(2) NOT NULL ,
selectallcoupons character(1) ,
unselectallcoupons character(1) ,
c_currency_id integer ,
settlementno character varying(24) ,
ivaamount numeric(24,2) ,
commissionamount numeric(24,2) ,
reconcilecoupons character(1) ,
CONSTRAINT creditcardsettlement_key PRIMARY KEY (c_creditcardsettlement_id) ,
CONSTRAINT fkclient FOREIGN KEY (ad_client_id) REFERENCES ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkcurrency FOREIGN KEY (c_currency_id) REFERENCES c_currency (c_currency_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkentidadfinanciera FOREIGN KEY (m_entidadfinanciera_id) REFERENCES m_entidadfinanciera (m_entidadfinanciera_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkorg FOREIGN KEY (ad_org_id) REFERENCES ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_CreditCardSettlement
  OWNER TO libertya;

CREATE TABLE C_ExpenseConcepts(

c_expenseconcepts_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
concepttype character(1) ,
amount numeric(24,2) ,
c_creditcardsettlement_id integer NOT NULL ,
CONSTRAINT expenseconcepts_key PRIMARY KEY (c_expenseconcepts_id) ,
CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) REFERENCES c_creditcardsettlement (c_creditcardsettlement_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_ExpenseConcepts
  OWNER TO libertya;

CREATE TABLE C_CreditCardCouponFilter(

c_creditcardcouponfilter_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_creditcardsettlement_id integer NOT NULL ,
c_currency_id integer NOT NULL ,
trxdatefrom timestamp without time zone ,
trxdateto timestamp without time zone ,
m_entidadfinanciera_id integer NOT NULL ,
m_entidadfinancieraplan_id integer NOT NULL ,
paymentbatch character varying(24) ,
isprocessed character(1) NOT NULL DEFAULT 'N'::bpchar ,
process character(1) ,
CONSTRAINT creditcardcouponfilter_key PRIMARY KEY (c_creditcardcouponfilter_id) ,
CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) REFERENCES c_creditcardsettlement (c_creditcardsettlement_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkcurrency FOREIGN KEY (c_currency_id) REFERENCES c_currency (c_currency_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkentidadfinanciera FOREIGN KEY (m_entidadfinanciera_id) REFERENCES m_entidadfinanciera (m_entidadfinanciera_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkentidadfinancieraplan FOREIGN KEY (m_entidadfinancieraplan_id) REFERENCES m_entidadfinancieraplan (m_entidadfinancieraplan_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_CreditCardCouponFilter
  OWNER TO libertya;

CREATE TABLE C_CouponsSettlements(

c_couponssettlements_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
include character(1) NOT NULL DEFAULT 'N'::bpchar ,
m_entidadfinanciera_id integer NOT NULL ,
m_entidadfinancieraplan_id integer NOT NULL ,
trxdate timestamp without time zone ,
amount numeric(24,2) ,
couponno character varying(24) ,
creditcardno character varying(24) ,
allocationnumber numeric(18) NOT NULL ,
paymentbatch character varying(24) ,
c_creditcardsettlement_id integer NOT NULL ,
c_currency_id integer NOT NULL ,
c_creditcardcouponfilter_id integer NOT NULL ,
c_payment_id integer NOT NULL ,
CONSTRAINT couponssettlements_key PRIMARY KEY (c_couponssettlements_id) ,
CONSTRAINT fkcreditcardcouponfilter FOREIGN KEY (c_creditcardcouponfilter_id) REFERENCES c_creditcardcouponfilter (c_creditcardcouponfilter_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) REFERENCES c_creditcardsettlement (c_creditcardsettlement_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkcurrency FOREIGN KEY (c_currency_id) REFERENCES c_currency (c_currency_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkentidadfinanciera FOREIGN KEY (m_entidadfinanciera_id) REFERENCES m_entidadfinanciera (m_entidadfinanciera_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkentidadfinancieraplan FOREIGN KEY (m_entidadfinancieraplan_id) REFERENCES m_entidadfinancieraplan (m_entidadfinancieraplan_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkpayment FOREIGN KEY (c_payment_id) REFERENCES c_payment (c_payment_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_CouponsSettlements
  OWNER TO libertya;

CREATE TABLE C_WithholdingSettlement(

c_withholdingsettlement_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_retenciontype_id integer NOT NULL ,
c_region_id integer ,
amount numeric(24,2) ,
c_creditcardsettlement_id integer NOT NULL ,
CONSTRAINT withholdingsettlement_key PRIMARY KEY (c_withholdingsettlement_id) ,
CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) REFERENCES c_creditcardsettlement (c_creditcardsettlement_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkregion FOREIGN KEY (c_region_id) REFERENCES c_region (c_region_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkretenciontype FOREIGN KEY (c_retenciontype_id) REFERENCES c_retenciontype (c_retenciontype_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_WithholdingSettlement
  OWNER TO libertya;

CREATE TABLE C_PerceptionsSettlement(

c_perceptionssettlement_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_taxcategory_id integer NOT NULL ,
c_creditcardsettlement_id integer NOT NULL ,
internalno character varying(24) NOT NULL ,
amount numeric(24,2) ,
CONSTRAINT perceptionssettlement_key PRIMARY KEY (c_perceptionssettlement_id) ,
CONSTRAINT perceptionssettlementclient FOREIGN KEY (ad_client_id) REFERENCES ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT perceptionssettlementcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) REFERENCES c_creditcardsettlement (c_creditcardsettlement_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT perceptionssettlementorg FOREIGN KEY (ad_org_id) REFERENCES ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT perceptionssettlementtaxcategory FOREIGN KEY (c_taxcategory_id) REFERENCES c_taxcategory (c_taxcategory_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_PerceptionsSettlement
  OWNER TO libertya;

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_Payment','AuditStatus','character(2)'));

CREATE OR REPLACE VIEW c_paymentcoupon_v AS
SELECT
	p.c_payment_id,
	p.ad_client_id,
	p.ad_org_id,
	p.created,
	p.createdby,
	p.updated,
	p.updatedby,
	efp.m_entidadfinanciera_id,
	p.m_entidadfinancieraplan_id,
	ccs.settlementno,
	p.c_invoice_id,
	'Y'::character(1) AS isactive,
	p.creditcardnumber,
	p.couponnumber,
	p.c_bpartner_id,
	p.duedate,
	p.dateacct,
	p.datetrx,
	p.couponbatchnumber,
	p.payamt,
	p.c_currency_id,
	p.docstatus,
	p.isreconciled,
	ccs.paymentdate AS settlementdate,
	efp.cuotaspago AS totalallocations,
	p.auditstatus
FROM
	libertya.c_payment p
	JOIN libertya.m_entidadfinancieraplan efp
		ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id
	LEFT JOIN libertya.c_couponssettlements cs
		ON p.c_payment_id = cs.c_payment_id
	LEFT JOIN libertya.c_creditcardsettlement ccs
		ON cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id
WHERE
	p.tendertype = 'C'::bpchar;
ALTER TABLE c_paymentcoupon_v
  OWNER TO libertya;

CREATE TABLE C_IVASettlements(

c_ivasettlements_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
c_creditcardsettlement_id integer NOT NULL ,
c_taxcategory_id integer NOT NULL ,
amount numeric(24,2) ,
CONSTRAINT ivasettlements_key PRIMARY KEY (c_ivasettlements_id) ,
CONSTRAINT ivasettlementsclient FOREIGN KEY (ad_client_id) REFERENCES ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT ivasettlementscreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) REFERENCES c_creditcardsettlement (c_creditcardsettlement_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT ivasettlementsorg FOREIGN KEY (ad_org_id) REFERENCES ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT ivasettlementstaxcategory FOREIGN KEY (c_taxcategory_id) REFERENCES c_taxcategory (c_taxcategory_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_IVASettlements
  OWNER TO libertya;

CREATE TABLE C_CardSettlementConcepts(

c_cardsettlementconcepts_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
m_product_id integer NOT NULL ,
value character varying(40) NOT NULL ,
name character varying(255) NOT NULL ,
type character(2) NOT NULL ,
CONSTRAINT cardsettlementconcepts_key PRIMARY KEY (c_cardsettlementconcepts_id) ,
CONSTRAINT cardsettlementconceptsclient FOREIGN KEY (ad_client_id) REFERENCES ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cardsettlementconceptsorg FOREIGN KEY (ad_org_id) REFERENCES ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT cardsettlementconceptsproduct FOREIGN KEY (m_product_id) REFERENCES m_product (m_product_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_CardSettlementConcepts
  OWNER TO libertya;

CREATE TABLE C_CommissionConcepts(

c_commissionconcepts_id integer NOT NULL ,
ad_client_id integer NOT NULL ,
ad_org_id integer NOT NULL ,
isactive character(1) NOT NULL DEFAULT 'Y'::bpchar ,
created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
createdby integer NOT NULL ,
updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone ,
updatedby integer NOT NULL ,
concepttype character(1) ,
amount numeric(24,2) ,
c_creditcardsettlement_id integer NOT NULL ,
CONSTRAINT commissionconcepts_key PRIMARY KEY (c_commissionconcepts_id) ,
CONSTRAINT commissionconceptsclient FOREIGN KEY (ad_client_id) REFERENCES ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT commissionconceptsorg FOREIGN KEY (ad_org_id) REFERENCES ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  ,
CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) REFERENCES c_creditcardsettlement (c_creditcardsettlement_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION  );
ALTER TABLE C_CommissionConcepts
  OWNER TO libertya;

--20161205-1300 Fix a la funview para que devuelva los cashlines que están asociados a una factura pero sin allocation
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$
declare
    consulta varchar;
    orderby1 varchar;
    orderby2 varchar;
    orderby3 varchar;
    leftjoin1 varchar;
    leftjoin2 varchar;
    advancedcondition varchar;
    advancedconditioncashlineexception varchar;
    whereclauseConditionDebit varchar;
    whereclauseConditionCredit varchar;
    whereclauseConditionCreditCashException varchar;
    whereclauseDateTo varchar;
    selectallocationNull varchar;
    selectallocationPayment varchar;
    selectallocationCashline varchar;
    selectallocationCredit varchar;
    selectAllocationReferencePayment varchar;
    selectAllocationReferenceCashline varchar;
    selectAllocationReferenceCredit varchar;
    adocument v_documents_org_type_condition;
   
BEGIN
    whereclauseDateTo = ' ( 1 = 1 ) ';
    -- Armar la condición para fecha de corte
    if dateTo is not null then 
		whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';
    end if;
    
    --Si no se deben mostrar todos, entonces agregar la condicion por la forma de pago
    if condition <> 'A' then
		--Si se debe mostrar sólo efectivo, entonces no se debe mostrar los anticipos, si o si debe tener una factura asociada
		advancedcondition = 'il.paymentrule is null OR ';
		advancedconditioncashlineexception = 'ic.paymentrule is null OR ';
		if condition = 'B' then
			advancedcondition = '';
			advancedconditioncashlineexception = '';
		end if;
		whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') ';
		whereclauseConditionCreditCashException = ' (' || advancedconditioncashlineexception || ' ic.paymentrule = ''' || condition || ''') ';
	else
		whereclauseConditionDebit = ' (1 = 1) ';
		whereclauseConditionCredit = ' (1 = 1) ';
		whereclauseConditionCreditCashException = ' (1 = 1) '; 
    end if;    

    -- recuperar informacion minima indispensable si summaryonly es true.  en caso de ser false, debe joinearse/ordenarse, etc.
    if summaryonly = false then

        orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,
                 CASE
                     WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                     ELSE i.dateinvoiced
                 END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';

        orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id), p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';

        orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id), cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) ';
	
		selectallocationNull = ' NULL::integer ';
		selectallocationPayment = selectallocationNull;
		selectallocationCashline = selectallocationNull;
		selectallocationCredit = selectallocationNull;
	
    else
        orderby1 = '';
        orderby2 = '';
        orderby3 = '';

		selectAllocationReferencePayment = ' al.c_payment_id = p.c_payment_id ';
		selectAllocationReferenceCashline = ' al.c_cashline_id = cl.c_cashline_id ';
		selectAllocationReferenceCredit = ' al.c_invoice_credit_id = i.c_invoice_id ';
	
		selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
		selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id ';
	
    end if;    

    consulta = ' SELECT * FROM 

        (        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
                        CASE
                            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
                            ELSE i.dateinvoiced
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, '
				|| selectallocationCredit || 
               ' FROM c_invoice_v i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')
    LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
    WHERE 

' || whereclauseConditionDebit || '
' || orderby1 || '

    )
        UNION ALL
                ( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, COALESCE(il.ad_org_id, p.ad_org_id) AS ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, '
		|| selectallocationPayment || 
                  ' FROM c_payment p
              JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
         JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ')
	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id 
	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id
	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id
	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID
	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id
  WHERE 
CASE
    WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
    ELSE 1 = 1
END 

AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL 
		OR bde.M_BoletaDeposito_ID IS NOT NULL 
		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'') 
	ELSE 1 = 1
	END) 

AND ' || whereclauseConditionCredit || '

' || orderby2 || '


)

UNION ALL

        ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id) AS ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,
                CASE
                    WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id
                    ELSE ic.c_bpartner_id
                END AS c_bpartner_id, dt.c_doctype_id,
                CASE
                    WHEN cl.amount < 0.0 THEN 1
                    ELSE (-1)
                END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,
                CASE
                    WHEN cl.amount < 0.0 THEN ''N''::bpchar
                    ELSE ''Y''::bpchar
                END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) AS socreditstatus, 0 as c_order_id, '
                || selectallocationCashline || 
       ' FROM c_cashline cl
      JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
         FROM c_doctype d
        WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id
   LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
   LEFT JOIN c_bpartner bp3 ON ic.c_bpartner_id = bp3.c_bpartner_id
  WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')
        WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')
        WHEN ic.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')
        ELSE 1 = 2 END)
    AND (CASE WHEN (il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id) OR (ic.ad_org_id IS NOT NULL AND ic.ad_org_id <> cl.ad_org_id)
        THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar])
        ELSE 1 = 1 END)

    AND (' || whereclauseConditionCredit || ' OR ' || whereclauseConditionCreditCashException || ' )

' || orderby3 || '

)) AS d  
WHERE ' || whereclauseDateTo || ' ; ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone)
  OWNER TO libertya;