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
  
-- 20161206-0956 Aporte Sur Soft. Optimizacion en tiempo de respuesta de la vista
CREATE OR REPLACE VIEW rv_c_invoice AS 
SELECT DISTINCT 
	ON(c_invoice_id,
	ad_client_id,
	ad_org_id,
	isactive,
	created,
	createdby,
	updated,
	updatedby,
	issotrx,
	i.documentno,
	docstatus,
	docaction,
	isprinted,
	isdiscountprinted,
	processing,
	processed,
	istransferred,
	ispaid,
	c_doctype_id,
	c_doctypetarget_id,
	c_order_id,
	description,
	isapproved,
	salesrep_id,
	dateinvoiced,
	dateprinted,
	dateacct,
	c_bpartner_id,
	c_bpartner_location_id,
	ad_user_id,
	c_bp_group_id,
	poreference,
	dateordered,
	c_currency_id,
	c_conversiontype_id,
	paymentrule,
	c_paymentterm_id,
	m_pricelist_id,
	c_campaign_id,
	c_project_id,
	c_activity_id,
	ispayschedulevalid,
	c_country_id,
	c_region_id,
	postal,
	city,
	c_charge_id,
	chargeamt,
	totallines,
	grandtotal,
	multiplier)
	
	i.c_invoice_id,
	i.ad_client_id,
	i.ad_org_id,
	i.isactive,
	i.created,
	i.createdby,
	i.updated,
	i.updatedby,
	i.issotrx,
	i.documentno,
	i.docstatus,
	i.docaction,
	i.isprinted,
	i.isdiscountprinted,
	i.processing,
	i.processed,
	i.istransferred,
	i.ispaid,
	i.c_doctype_id,
	i.c_doctypetarget_id,
	i.c_order_id,
	i.description,
	i.isapproved,
	i.salesrep_id,
	i.dateinvoiced,
	i.dateprinted,
	i.dateacct,
	i.c_bpartner_id,
	i.c_bpartner_location_id,
	i.ad_user_id,
	b.c_bp_group_id,
	i.poreference,
	i.dateordered,
	i.c_currency_id,
	i.c_conversiontype_id,
	i.paymentrule,
	i.c_paymentterm_id,
	i.m_pricelist_id,
	i.c_campaign_id,
	i.c_project_id,
	i.c_activity_id,
	i.ispayschedulevalid,
	loc.c_country_id,
	loc.c_region_id,
	loc.postal,
	loc.city,
	i.c_charge_id,
	CASE
		WHEN
			charat(d.docbasetype::character varying, 3)::text = 'C'::text 
		THEN
			i.chargeamt * ( - 1::numeric) 
		ELSE
			i.chargeamt 
	END
	AS chargeamt, 
	CASE
		WHEN
			charat(d.docbasetype::character varying, 3)::text = 'C'::text 
		THEN
			i.totallines * ( - 1::numeric) 
		ELSE
			i.totallines 
	END
	AS totallines, 
	CASE
		WHEN
			charat(d.docbasetype::character varying, 3)::text = 'C'::text 
		THEN
			i.grandtotal * ( - 1::numeric) 
		ELSE
			i.grandtotal 
	END
	AS grandtotal, 
	CASE
		WHEN
			charat(d.docbasetype::character varying, 3)::text = 'C'::text 
		THEN
( - 1) 
		ELSE
			1 
	END
	AS multiplier,
	COALESCE(mio1.documentno, mio2.documentno, mio3.documentno) AS documentno_inout,
	COALESCE(mio1.movementdate, mio2.movementdate, mio3.movementdate) AS movementdate_inout
FROM
	c_invoice i 
	JOIN
		c_doctype d 
		ON i.c_doctype_id = d.c_doctype_id 
	JOIN
		c_bpartner b 
		ON i.c_bpartner_id = b.c_bpartner_id 
	JOIN
		c_bpartner_location bpl 
		ON i.c_bpartner_location_id = bpl.c_bpartner_location_id 
	JOIN
		c_location loc 
		ON bpl.c_location_id = loc.c_location_id 
	LEFT JOIN
		c_invoiceline il
		ON il.c_invoice_id = i.c_invoice_id
	LEFT JOIN
		m_inoutline miol1
		ON miol1.c_orderline_id = il.c_orderline_id
	LEFT JOIN
		c_orderline ol1
		ON ol1.c_orderline_id = il.c_orderline_id
	LEFT JOIN
		c_order o1
		ON o1.c_order_id = ol1.c_order_id
	LEFT JOIN
		m_inout mio3
		ON mio3.c_order_id = o1.c_order_id
	LEFT JOIN
		m_inout mio1
		ON mio1.c_order_id = i.c_order_id
	LEFT JOIN
		m_inoutline miol2
		ON miol2.m_inoutline_id = il.m_inoutline_id
	LEFT JOIN
		m_inout mio2
		ON mio2.m_inout_id = miol2.m_inout_id
WHERE
	i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
ORDER BY c_invoice_id,
	ad_client_id,
	ad_org_id,
	isactive,
	created,
	createdby,
	updated,
	updatedby,
	issotrx,
	i.documentno,
	docstatus,
	docaction,
	isprinted,
	isdiscountprinted,
	processing,
	processed,
	istransferred,
	ispaid,
	c_doctype_id,
	c_doctypetarget_id,
	c_order_id,
	description,
	isapproved,
	salesrep_id,
	dateinvoiced,
	dateprinted,
	dateacct,
	c_bpartner_id,
	c_bpartner_location_id,
	ad_user_id,
	c_bp_group_id,
	poreference,
	dateordered,
	c_currency_id,
	c_conversiontype_id,
	paymentrule,
	c_paymentterm_id,
	m_pricelist_id,
	c_campaign_id,
	c_project_id,
	c_activity_id,
	ispayschedulevalid,
	c_country_id,
	c_region_id,
	postal,
	city,
	c_charge_id,
	chargeamt,
	totallines,
	grandtotal,
	multiplier, 
	mio1.documentno DESC,
	mio1.movementdate DESC,
	mio2.documentno DESC,
	mio2.movementdate DESC,
	mio3.documentno DESC,
	mio3.movementdate DESC;

--20161207-1007 Nueva tabla para registrar detalles de instalacion de componentes
create table ad_plugin_detail (
ad_plugin_detail_id INTEGER NOT NULL,
  ad_client_id INTEGER NOT NULL,
  ad_org_id INTEGER NOT NULL,
  isactive CHARACTER(1) NOT NULL DEFAULT 'Y'::bpchar,
  created TIMESTAMP WITHOUT TIME zone NOT NULL DEFAULT ('now'::text)::TIMESTAMP(6) WITH TIME zone,
  createdby INTEGER NOT NULL,
  updated TIMESTAMP WITHOUT TIME zone NOT NULL DEFAULT ('now'::text)::TIMESTAMP(6) WITH TIME zone,
  updatedby INTEGER NOT NULL,
  ad_plugin_id integer NOT NULL,
  version varchar(10),
  component_export_date varchar(30),
  component_first_changelog varchar(50),
  component_last_changelog varchar(50),
  install_details varchar,
CONSTRAINT ad_plugin_detail_key PRIMARY KEY (ad_plugin_detail_id),
CONSTRAINT plugindetail_plugin FOREIGN KEY (ad_plugin_id) REFERENCES ad_plugin (ad_plugin_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--20161207-1207 Fix a la columna alicuotas de iva ya que no contemplaba operaciones exentas
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
        getcantidadalicuotasiva(i.c_invoice_id) AS cantalicuotasiva, 
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
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva, getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impcreditofiscalcomputable, currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, NULL::character varying(20) AS cuitemisorcorredor, NULL::character varying(60) AS denominacionemisorcorredor, 0::numeric(20,2) AS ivacomision
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_compras_cbte_v
  OWNER TO libertya;
  
--20161207-1338 Nuevas vistas para simplificar la recuperacion de informacion de plugins
create view ad_plugin_v as
select cv.name, cv.ad_componentobjectuid, cv.version, p.created, p.updated, p.createdby, p.updatedby, p.component_export_date, p.component_last_changelog
from ad_componentversion cv
inner join ad_plugin p on p.ad_componentversion_id = cv.ad_componentversion_id
order by p.created asc;

create view ad_plugin_detail_v as
select cv.name, cv.ad_componentobjectuid, pd.version, pd.created, pd.createdby, pd.component_export_date, pd.component_first_changelog, pd.component_last_changelog, pd.install_details
from ad_componentversion cv
inner join ad_plugin p on p.ad_componentversion_id = cv.ad_componentversion_id
left join ad_plugin_detail pd on p.ad_plugin_id = pd.ad_plugin_id
order by pd.created asc;

--20161220-1057 Nueva funcion para realizar un shifting sobre secuencias fisicas
/**
 * Incrementa shiftcount las secuencias fisicas que respeten el nombre criteria
 */
CREATE OR REPLACE FUNCTION shift_sequences(criteria varchar, shiftcount integer)
  RETURNS integer AS
$BODY$
DECLARE
	asequence varchar;
	newvalue integer;
	modifiedcount integer;
BEGIN
	IF (shiftcount <= 0) THEN
		raise exception 'El desplazamiento debe ser mayor a cero';
	END IF;
	
	modifiedcount := 0;
	FOR asequence IN (SELECT c.relname FROM pg_class c WHERE c.relkind = 'S' AND c.relname ilike ''||criteria order by c.relname) LOOP
		SELECT INTO newvalue nextval(asequence) + shiftcount;
		EXECUTE 'ALTER SEQUENCE ' || asequence || ' RESTART WITH ' || newvalue;
		modifiedcount := modifiedcount + 1;
	END LOOP;
	RETURN modifiedcount;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION shift_sequences(criteria varchar, shiftcount integer)
  OWNER TO libertya;

--20161220-1103 Correcciones a las exportaciones citi ventas 
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
LANGUAGE plpgsql VOLATILE;
ALTER FUNCTION getcantidadalicuotasiva(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION getimporteoperacionexentas(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ 
DECLARE
    	v_Amount        	NUMERIC;
BEGIN
    SELECT COALESCE(SUM(it.taxbaseamt), 0)
    INTO v_Amount
    FROM C_Invoicetax it
    INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)
    WHERE (C_Invoice_ID = p_c_invoice_id) AND (isPercepcion = 'N') AND (getcantidadalicuotasiva(p_c_invoice_id) > 1) AND ((t.rate = 0) OR (t.rate <> 0 AND it.taxamt = 0));   

    RETURN v_Amount;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getimporteoperacionexentas(integer)
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_ventas_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric);

ALTER TABLE reginfo_ventas_alicuotas_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_ventas_cbte_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateacct) AS fechadecomprobante, ei.codigo AS tipodecomprobante, i.puntodeventa, i.numerocomprobante AS nrocomprobante, i.numerocomprobante AS nrocomprobantehasta, 
        CASE
            WHEN bp.taxidtype = '99'::bpchar AND i.grandtotal > 1000::numeric THEN '96'::bpchar
            ELSE bp.taxidtype
        END::character(2) AS codigodoccomprador, gettaxid(bp.taxid, bp.taxidtype, bp.c_categoria_iva_id, i.nroidentificcliente, i.grandtotal)::character varying(20) AS nroidentificacioncomprador, bp.name AS nombrecomprador, currencyconvert(getgrandtotal(i.c_invoice_id, true), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal, 0::numeric(20,2) AS impconceptosnoneto, 0::numeric(20,2) AS imppercepnocategorizados, currencyconvert(getimporteoperacionexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac, currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni, currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos, cu.wsfecode AS codmoneda, currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio, 
        CASE
            WHEN getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva, getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, NULL::timestamp without time zone AS fechavencimientopago
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN e_electronicinvoiceref ei ON dt.doctypekey::text ~~* (ei.clave_busqueda::text || '%'::text) AND ei.tabla_ref::text = 'TCOM'::text
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'Y'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_ventas_cbte_v
  OWNER TO libertya;
  
--20161220-1248 Incorporación de mismas correcciones a las exportaciones CITI Ventas a las vistas de CITI Compras
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
            ELSE getcantidadalicuotasiva(i.c_invoice_id)
        END AS cantalicuotasiva, getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion, currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impcreditofiscalcomputable, currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos, NULL::character varying(20) AS cuitemisorcorredor, NULL::character varying(60) AS denominacionemisorcorredor, 0::numeric(20,2) AS ivacomision
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
  WHERE (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE reginfo_compras_cbte_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_compras_importaciones_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateacct) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, i.importclearance AS despachoimportacion, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND i.importclearance IS NOT NULL AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric);

ALTER TABLE reginfo_compras_importaciones_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW reginfo_compras_alicuotas_v AS 
 SELECT i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS date, date_trunc('day'::text, i.dateinvoiced) AS fechadecomprobante, gettipodecomprobante(dt.doctypekey, l.letra)::character varying(15) AS tipodecomprobante, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.puntodeventa
        END AS puntodeventa, 
        CASE
            WHEN gettipodecomprobante(dt.doctypekey, l.letra)::text = '66'::text THEN 0
            ELSE i.numerocomprobante
        END AS nrocomprobante, bp.taxidtype AS codigodocvendedor, bp.taxid AS nroidentificacionvendedor, currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impnetogravado, 
        t.wsfecode AS alicuotaiva, currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impuestoliquidado
   FROM c_invoice i
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
   LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'N'::bpchar AND (i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar, 'VO'::bpchar, 'RE'::bpchar])) AND i.issotrx = 'N'::bpchar AND i.isactive = 'Y'::bpchar AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND dt.isfiscaldocument = 'Y'::bpchar AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND (l.letra <> ALL (ARRAY['B'::bpchar, 'C'::bpchar]))  AND (getimporteoperacionexentas(i.c_invoice_id) <> 0::numeric AND t.rate <> 0::numeric OR getimporteoperacionexentas(i.c_invoice_id) = 0::numeric);

ALTER TABLE reginfo_compras_alicuotas_v
  OWNER TO libertya;

--20161223-1000 Merge de Revision 1709
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_CreditCardSettlement','Processing','character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_CreditCardSettlement','Processed','character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_CreditCardSettlement','IsApproved','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

DROP VIEW C_PaymentCoupon_V;

CREATE VIEW C_PaymentCoupon_V AS
SELECT p.c_payment_id, p.ad_client_id, p.ad_org_id, p.created, p.createdby, p.updated, p.updatedby, efp.m_entidadfinanciera_id, p.m_entidadfinancieraplan_id, ccs.settlementno, p.c_invoice_id, 'Y'::character(1) AS isactive, p.creditcardnumber, p.couponnumber, p.c_bpartner_id, p.duedate, p.dateacct, p.datetrx, p.couponbatchnumber, p.payamt, p.c_currency_id, p.docstatus, p.isreconciled, ccs.paymentdate AS settlementdate, efp.cuotaspago AS totalallocations, p.auditstatus FROM (((c_payment p JOIN m_entidadfinancieraplan efp ON ((p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id))) LEFT JOIN c_couponssettlements cs ON ((p.c_payment_id = cs.c_payment_id))) LEFT JOIN c_creditcardsettlement ccs ON ((cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id))) WHERE (p.tendertype = 'C'::bpchar);
ALTER TABLE C_PaymentCoupon_V OWNER TO libertya;

--20160104-0900 Contexto de aplicación de esquemas de vencimientos (Ventas, Compras, Ambos)
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_PaymentTerm','applicationcontext','character(1)'));

UPDATE C_PaymentTerm
SET applicationcontext = 'B';

ALTER TABLE C_PaymentTerm ALTER COLUMN applicationcontext SET NOT NULL;

--20170110 Incorporacion Sur Software.  Funcion faltante para la importacion de padrones 
-- Función que copia los registros del nuevo padrón de Regímenes Generales de CABA
-- desde la tabla i_padron_caba_regimen_general a la tabla c_bpartner_padron_bsas
CREATE OR REPLACE FUNCTION update_padron_from_i_padron_caba_regimen_general(
    p_ad_org_id integer,
    p_ad_client_id integer,
    p_ad_user_id integer,
    p_padrontype character,
    p_offset integer,
    p_chunksize integer)
  RETURNS void AS
$BODY$
DECLARE
	aux RECORD;
BEGIN

	FOR AUX IN
		SELECT * FROM i_padron_caba_regimen_general
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_padron_from_i_padron_caba_regimen_general(integer, integer, integer, character, integer, integer)
  OWNER TO libertya;

--20170111-1530 Creación de una clave para el tipo de documento Asiento ya que no tenía ninguno asociado  
update c_doctype
set doctypekey = 'JB'
where ad_componentobjectuid = 'CORE-C_DocType-1010506';

--20171213--1048 Nueva función para obtener el descuento/recargo para una factura tal como se hace en la ventana de Recibo de Clientes
CREATE OR REPLACE FUNCTION libertya.getDiscount(C_Invoice_ID integer, C_PaySchedule_ID integer, invoiceDate Timestamp, dueDate Timestamp, compareDate Timestamp, baseAmt numeric)
  RETURNS numeric AS
$BODY$
/***
 Tomar la configuración de descuentos/recargos desde el esquema de
 vencimientos o del esquema de pago
**/
DECLARE
	fromPaySchedule record;
	fromPaymentTerm record;
	discountApplicationType varchar;
	discountPerc numeric := 0.0;
	toleranceDays integer := 0;
	discountDays integer := 0;
	discount numeric := 0;
	diffToUse integer := 0;
	diffInvoicedDays integer := 0;
	diffDueDays integer := 0;
	daysToBeat integer := 0;
	dailyIncreaseDays integer := 0;
	constApplicationType varchar;
BEGIN
	--Obtengo el value de la lista de referencia "Daily increase"
	SELECT value 
	   INTO constApplicationType
	FROM AD_Ref_List
	WHERE ad_componentobjectuid = 'CORE-AD_Ref_List-1010471';
	
	/* Días de diferencia entre la fecha de comparación y la fecha de
	   vencimiento. Si es positivo la fecha de comparación es mayor a la de
	   vencimiento, 0 es igual y negativo lo contrario */
	diffDueDays =  compareDate::date - dueDate::date;

	/* Días de diferencia entre la fecha de comparación y la fecha de
   	   facturación. Si es positivo la fecha de comparación es mayor a la de
   	   facturación, 0 es igual y negativo lo contrario */
	diffInvoicedDays = compareDate::date - invoiceDate::date;
	-- Lo tomo del esquema de pagos, sino del esquema de vencimientos
	IF (C_PaySchedule_ID > 0) 
	THEN 
	    SELECT *
	      INTO fromPaySchedule
	    FROM C_PaySchedule vp
	    WHERE  vp.C_PaySchedule_ID=C_PaySchedule_ID;

	    IF (fromPaySchedule.c_payschedule_id IS NOT NULL)
	    THEN
		discountApplicationType = fromPaySchedule.discountApplicationType;
		discountPerc = fromPaySchedule.Discount;
		discountDays = fromPaySchedule.DiscountDays;
		toleranceDays = fromPaySchedule.GraceDays;
		diffToUse = diffDueDays;
	    END IF;
	ELSE
		/* Tomar descuento 1 o 2? Eso se determina las fechas parámetro, si
		   la fecha de comparación está después de la de vencimiento,
		   significa que estoy vencido por lo que se debe tomar el descuento
		   2, sino se toma descuento 1 */
		SELECT discountApplicationType as dat, discountApplicationType2 as dat2, 
			Discount as d, Discount2 as d2, DiscountDays as dd, DiscountDays2 as dd2, 
			GraceDays as gd, GraceDays2 as gd2
		INTO fromPaymentTerm
		FROM C_Invoice i
		INNER JOIN C_Paymentterm pt ON pt.C_Paymentterm_ID = i.C_Paymentterm_ID
			WHERE  i.C_Invoice_ID=C_Invoice_ID;

		IF (diffDueDays > 0)
		THEN
			discountApplicationType = fromPaymentTerm.dat2;
			discountPerc = fromPaymentTerm.d2;
			discountDays = fromPaymentTerm.dd2;
			toleranceDays = fromPaymentTerm.gd2;
			diffToUse = diffDueDays;
		ELSIF (diffInvoicedDays > 0)
		THEN
			discountApplicationType = fromPaymentTerm.dat;
			discountPerc = fromPaymentTerm.d;
			discountDays = fromPaymentTerm.dd;
			toleranceDays = fromPaymentTerm.gd;
			diffToUse = diffInvoicedDays;
		END IF;
	END IF;
	-- Si hay descuento aplicable entonces aplico
	IF (discountApplicationType IS NOT NULL) 
	THEN
		/* Si es incremento diario se debe multiplicar por la cantidad de
		   días de diferencia, sino es de una vez el descuento
		   Primero verifico si lo puedo aplicar, dependiendo los días
		   Si la diferencia de días es mayor a la cantidad de días de
		   descuento + los días de tolerancia, entonces se aplica */
		daysToBeat = discountDays + toleranceDays;
		IF (diffToUse > daysToBeat)
		THEN
			IF (discountApplicationType = constApplicationType)
			THEN
				dailyIncreaseDays = diffToUse;
			ELSE
				dailyIncreaseDays = 1;
			END IF;
			/* Saco el monto de descuento/recargo, descuentos son positivos,
			   recargos negativos */
			discount = ((baseAmt*discountPerc)/100)*dailyIncreaseDays;
		END IF;
	END IF;
	RETURN discount;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getDiscount(C_Invoice_ID integer, C_PaySchedule_ID integer, invoiceDate Timestamp, dueDate Timestamp, compareDate Timestamp, baseAmt numeric)
  OWNER TO libertya;
  
--20170118-0850 Nueva columna para determinar el comprobante original de cada retención realizada sobre cada comprobante
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_retencion_invoice','c_invoice_src_id','integer'));

--20170119-1330 Incremento de tamaño para la columna Nro de cuenta de cuentas bancarias de la EC
alter table c_bp_bankaccount ALTER COLUMN accountno TYPE character varying(60); 


--20170120 Incorporacion Sur Software.  Fix a funciones para la importacion de padrones
CREATE OR REPLACE FUNCTION update_padron_from_i_padron_bs_as(
    p_ad_org_id integer,
    p_ad_client_id integer,
    p_ad_user_id integer,
    p_padrontype character,
    p_offset integer,
    p_chunksize integer)
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
			FECHA_DESDE = to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')::timestamp without time zone 
			, FECHA_HASTA = to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')::timestamp without time zone 
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
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')::timestamp without time zone
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
				, to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')::timestamp without time zone     
				, to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')::timestamp without time zone           
				, to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')::timestamp without time zone           
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_padron_from_i_padron_bs_as(integer, integer, integer, character, integer, integer)
  OWNER TO libertya;


CREATE OR REPLACE FUNCTION update_padron_from_i_padron_caba_alto_riesgo(
    p_ad_org_id integer,
    p_ad_client_id integer,
    p_ad_user_id integer,
    p_padrontype character,
    p_offset integer,
    p_chunksize integer)
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
			FECHA_DESDE = to_timestamp(aux.FECHA_DESDE , 'DDMMYYYY')::timestamp without time zone
			, FECHA_HASTA = to_timestamp(aux.FECHA_HASTA , 'DDMMYYYY')::timestamp without time zone
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
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION , 'DDMMYYYY')::timestamp without time zone
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
				, to_timestamp(aux.FECHA_PUBLICACION::text , 'DDMMYYYY')::timestamp without time zone
				, to_timestamp(aux.FECHA_DESDE::text , 'DDMMYYYY')::timestamp without time zone      
				, to_timestamp(aux.FECHA_HASTA::text , 'DDMMYYYY')::timestamp without time zone
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_padron_from_i_padron_caba_alto_riesgo(integer, integer, integer, character, integer, integer)
  OWNER TO libertya;


CREATE OR REPLACE FUNCTION update_padron_from_i_padron_caba_regimen_general(
    p_ad_org_id integer,
    p_ad_client_id integer,
    p_ad_user_id integer,
    p_padrontype character,
    p_offset integer,
    p_chunksize integer)
  RETURNS void AS
$BODY$
DECLARE
	aux RECORD;
BEGIN

	FOR AUX IN
		SELECT * FROM i_padron_caba_regimen_general
		ORDER BY cuit
		OFFSET p_offset
		LIMIT p_chunksize
	LOOP
		UPDATE
			c_bpartner_padron_bsas padron
		SET
			FECHA_DESDE = to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')::timestamp without time zone
			, FECHA_HASTA = to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')::timestamp without time zone
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
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')::timestamp without time zone
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
				, to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')::timestamp without time zone
				, to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')::timestamp without time zone      
				, to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')::timestamp without time zone
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_padron_from_i_padron_caba_regimen_general(integer, integer, integer, character, integer, integer)
  OWNER TO libertya;


CREATE OR REPLACE FUNCTION update_padron_from_i_padron_caba_regimen_simplificado(
    p_ad_org_id integer,
    p_ad_client_id integer,
    p_ad_user_id integer,
    p_padrontype character,
    p_offset integer,
    p_chunksize integer)
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
			FECHA_DESDE = to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')::timestamp without time zone
			, FECHA_HASTA = to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')::timestamp without time zone
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
			AND padron.FECHA_PUBLICACION = to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')::timestamp without time zone
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
				, to_timestamp(aux.FECHA_PUBLICACION, 'DDMMYYYY')::timestamp without time zone
				, to_timestamp(aux.FECHA_DESDE, 'DDMMYYYY')::timestamp without time zone      
				, to_timestamp(aux.FECHA_HASTA, 'DDMMYYYY')::timestamp without time zone
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
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_padron_from_i_padron_caba_regimen_simplificado(integer, integer, integer, character, integer, integer)
  OWNER TO libertya;
  
--20170125-0940 Merge de revision 1739
CREATE SEQUENCE seq_i_trailerparticipants
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000001
  CACHE 1;

CREATE TABLE i_trailerparticipants
(
  i_trailerparticipants_id integer NOT NULL DEFAULT nextval('seq_i_trailerparticipants'),
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
  tipo_registro character varying(32),
  nombre_archivo character varying(32),
  comercio_centralizador character varying(32),
  moneda character varying(32),
  grupo_presentacion character varying(32),
  plazo_pago character varying(32),
  tipo_plazo_pago character varying(32),
  fecha_presentacion character varying(32),
  fecha_vencimiento_clearing character varying(32),
  producto character varying(32),
  comercio_participante character varying(32),
  entidad_pagadora character varying(32),
  sucursal_pagadora character varying(32),
  numero_liquidacion character varying(32),
  total_importe_total character varying(32),
  total_importe_total_signo character varying(32),
  total_importe_sin_dto character varying(32),
  total_importe_sin_dto_signo character varying(32),
  total_importe_final character varying(32),
  total_importe_final_signo character varying(32),
  aranceles_cto_fin character varying(32),
  aranceles_cto_fin_signo character varying(32),
  retenciones_fiscales character varying(32),
  retenciones_fiscales_signo character varying(32),
  otros_debitos character varying(32),
  otros_debitos_signo character varying(32),
  otros_creditos character varying(32),
  otros_creditos_signo character varying(32),
  neto_comercios character varying(32),
  neto_comercios_signo character varying(32),
  total_registros_detalle character varying(32),
  monto_pend_cuotas character varying(32),
  monto_pend_cuotas_signo character varying(32),
  subtipo_registro character varying(32),
  iva_aranceles_ri character varying(32),
  iva_aranceles_ri_signo character varying(32),
  impuesto_deb_cred character varying(32),
  impuesto_deb_cred_signo character varying(32),
  iva_dto_pago_anticipado character varying(32),
  iva_dto_pago_anticipado_signo character varying(32),
  ret_iva_ventas character varying(32),
  ret_iva_ventas_signo character varying(32),
  percepc_iva_r3337 character varying(32),
  percepc_iva_r3337_signo character varying(32),
  ret_imp_ganancias character varying(32),
  ret_imp_ganancias_signo character varying(32),
  ret_imp_ingresos_brutos character varying(32),
  ret_imp_ingresos_brutos_signo character varying(32),
  percep_ingr_brutos character varying(32),
  percep_ingr_brutos_signo character varying(32),
  iva_servicios character varying(32),
  iva_servicios_signo character varying(32),
  categoria_iva character varying(32),
  imp_sintereses_ley_25063 character varying(32),
  imp_sintereses_ley_25063_signo character varying(32),
  arancel character varying(32),
  arancel_signo character varying(32),
  costo_financiero character varying(32),
  costo_financiero_signo character varying(32),
  revisado character varying(32),
  hash_modelo character varying(32),
  provincia_ing_brutos character varying(32),

  CONSTRAINT trailerparticipants_key PRIMARY KEY (i_trailerparticipants_id),
  CONSTRAINT trailerparticipants_unique_id UNIQUE (id),
  CONSTRAINT trailerparticipantsclient FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT trailerparticipantsorg FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE i_trailerparticipants
  OWNER TO libertya;

CREATE SEQUENCE seq_i_amexpaymentsandtaxes
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000001
  CACHE 1;

CREATE TABLE i_amexpaymentsandtaxes
(
  i_amexpaymentsandtaxes_id integer NOT NULL DEFAULT nextval('seq_i_amexpaymentsandtaxes'),
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
  num_est character varying(32),
  fecha_pago character varying(32),
  num_sec_pago character varying(32),
  tipo_reg character varying(32),
  importe_pago character varying(32),
  cod_banco character varying(32),
  cod_suc_banc character varying(32),
  num_cuenta_banc character varying(32),
  nom_est character varying(32),
  cod_moneda character varying(32),
  importe_deuda_ant character varying(32),
  imp_bruto_est character varying(32),
  imp_desc_pago character varying(32),
  imp_tot_impuestos character varying(32),
  imp_tot_desc_acel character varying(32),
  imp_neto_ajuste character varying(32),
  estado_pago character varying(32),

  cod_imp character varying(32),
  cod_imp_desc character varying(32),
  base_imp character varying(32),
  fecha_imp character varying(32),
  porc_imp character varying(32),
  importe_imp character varying(32),

  CONSTRAINT amexpaymentsandtaxes_key PRIMARY KEY (i_amexpaymentsandtaxes_id),
  CONSTRAINT amexpaymentsandtaxes_unique_id UNIQUE (id),
  CONSTRAINT amexpaymentsandtaxesclient FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT amexpaymentsandtaxesorg FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE i_amexpaymentsandtaxes
  OWNER TO libertya;

CREATE SEQUENCE seq_i_naranjapayments
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000001
  CACHE 1;

CREATE TABLE i_naranjapayments
(
  i_naranjapayments_id integer NOT NULL DEFAULT nextval('seq_i_naranjapayments'),
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
  comercio character varying(32),
  fecha_presentacion character varying(32),
  numero_recap character varying(32),
  cupon character varying(32),
  tarjeta character varying(32),
  fecha_compra character varying(32),
  moneda character varying(32),
  plan character varying(32),
  compra character varying(32),
  entrega character varying(32),
  fecha_cuota character varying(32),
  importe_cuota character varying(32),
  numero_cuota character varying(32),
  tipo_mov character varying(32),
  estado character varying(32),
  descripcion character varying(32),
  codigo_aut character varying(32),
  tipo_op character varying(32),
  numero_devolucion character varying(32),
  tipo_cd character varying(32),
  nro_terminal character varying(32),
  nro_lote character varying(32),
  codigo_especial character varying(32),
  nro_debito character varying(32),
  marca character varying(32),
  num_cliente character varying(32),
  fecha_pago character varying(32),
  bines character varying(32),
  revisado character varying(32),
  fecha_proceso character varying(32),
  nro_liquidacion character varying(32),
  cuit_ag_retencion character varying(32),
  signo_total_descuentos character varying(32),
  total_descuentos character varying(32),
  signo_ret_iva_140 character varying(32),
  retencion_iva_140 character varying(32),
  signo_ret_ganancias character varying(32),
  retencion_ganancias character varying(32),
  signo_ret_iva_3130 character varying(32),
  retencion_3130 character varying(32),
  signo_base_imponible character varying(32),
  base_imponible_ing_bru character varying(32),
  alicuota_ing_brutos character varying(32),
  signo_ret_ing_brutos character varying(32),
  ret_ingresos_brutos character varying(32),
  signo_retencion_municipal character varying(32),
  retencion_municipal character varying(32),
  signo_dbtos_cdtos character varying(32),
  debitos_creditos character varying(32),
  signo_percepcion_1135 character varying(32),
  percepcion_1135 character varying(32),
  signo_liq_negativa_ant character varying(32),
  liq_negativa_dia_ant character varying(32),
  signo_embargo_y_cesiones character varying(32),
  embargo_y_cesiones character varying(32),
  signo_neto character varying(32),
  neto character varying(32),
  rubro character varying(32),
  signo_total_otros_debitos character varying(32),
  importe_total_otros_debitos character varying(32),
  signo_total_creditos character varying(32),
  importe_total_creditos character varying(32),
  signo_total_anticipos character varying(32),
  importe_total_anticipos character varying(32),
  signo_total_cheque_diferido character varying(32),
  importe_total_cheque_diferido character varying(32),
  signo_ara_vto character varying(32),
  importe_ara_vto character varying(32),
  signo_int_z_vto character varying(32),
  imp_int_z_vto character varying(32),
  sig_int_plan_esp_vto character varying(32),
  imp_int_plan_esp_vto character varying(32),
  sig_acre_liq_ant_vto character varying(32),
  imp_acre_liq_ant_vto character varying(32),
  sig_iva_21_vto character varying(32),
  imp_iva_21_vto character varying(32),
  sig_perc_iva_vto character varying(32),
  imp_perc_iva_vto character varying(32),
  sig_perc_iibb_vto character varying(32),
  imp_perc_iibb_vto character varying(32),
  signo_ara_facturado_30 character varying(32),
  importe_ara_facturado_30 character varying(32),
  signo_int_z_facturado_30 character varying(32),
  imp_int_z_facturado_30 character varying(32),
  sig_int_plan_esp_facturado_30 character varying(32),
  imp_int_plan_esp_facturado_30 character varying(32),
  sig_acre_liq_ant_facturado_30 character varying(32),
  imp_acre_liq_ant_facturado_30 character varying(32),
  sig_iva_21_facturado_30 character varying(32),
  imp_iva_21_facturado_30 character varying(32),
  sig_perc_iva_facturado_30 character varying(32),
  imp_perc_iva_facturado_30 character varying(32),
  sig_perc_iibb_facturado_30 character varying(32),
  imp_perc_iibb_facturado_30 character varying(32),
  signo_ara_facturado_60 character varying(32),
  importe_ara_facturado_60 character varying(32),
  signo_int_z_facturado_60 character varying(32),
  imp_int_z_facturado_60 character varying(32),
  sig_int_plan_esp_facturado_60 character varying(32),
  imp_int_plan_esp_facturado_60 character varying(32),
  sig_acre_liq_ant_facturado_60 character varying(32),
  imp_acre_liq_ant_facturado_60 character varying(32),
  sig_iva_21_facturado_60 character varying(32),
  imp_iva_21_facturado_60 character varying(32),
  sig_perc_iva_facturado_60 character varying(32),
  imp_perc_iva_facturado_60 character varying(32),
  sig_perc_iibb_facturado_60 character varying(32),
  imp_perc_iibb_facturado_60 character varying(32),
  signo_ara_facturado_90 character varying(32),
  importe_ara_facturado_90 character varying(32),
  signo_int_z_facturado_90 character varying(32),
  imp_int_z_facturado_90 character varying(32),
  sig_int_plan_esp_facturado_90 character varying(32),
  imp_int_plan_esp_facturado_90 character varying(32),
  sig_acre_liq_ant_facturado_90 character varying(32),
  imp_acre_liq_ant_facturado_90 character varying(32),
  sig_iva_21_facturado_90 character varying(32),
  imp_iva_21_facturado_90 character varying(32),
  sig_perc_iva_facturado_90 character varying(32),
  imp_perc_iva_facturado_90 character varying(32),
  sig_perc_iibb_facturado_90 character varying(32),
  imp_perc_iibb_facturado_90 character varying(32),
  signo_ara_facturado_120 character varying(32),
  importe_ara_facturado_120 character varying(32),
  signo_int_z_facturado_120 character varying(32),
  imp_int_z_facturado_120 character varying(32),
  sig_int_plan_esp_facturado_120 character varying(32),
  imp_int_plan_esp_facturado_120 character varying(32),
  sig_acre_liq_ant_facturado_120 character varying(32),
  imp_acre_liq_ant_facturado_120 character varying(32),
  sig_iva_21_facturado_120 character varying(32),
  imp_iva_21_facturado_120 character varying(32),
  sig_perc_iva_facturado_120 character varying(32),
  imp_perc_iva_facturado_120 character varying(32),
  sig_perc_iibb_facturado_120 character varying(32),
  imp_perc_iibb_facturado_120 character varying(32),

  CONSTRAINT naranjapayments_key PRIMARY KEY (i_naranjapayments_id),
  CONSTRAINT naranjapayments_unique_id UNIQUE (id),
  CONSTRAINT naranjapaymentsclient FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT naranjapaymentsorg FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE i_naranjapayments
  OWNER TO libertya;

CREATE SEQUENCE seq_i_visapayments
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000001
  CACHE 1;

CREATE TABLE i_visapayments
(
  i_visapayments_id integer NOT NULL DEFAULT nextval('seq_i_visapayments'),
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
  empresa character varying(32),
  fpres character varying(32),
  tipo_reg character varying(32),
  moneda character varying(32),
  num_com character varying(32),
  num_est character varying(32),
  nroliq character varying(32),
  fpag character varying(32),
  tipoliq character varying(32),
  impbruto character varying(32),
  signo_1 character varying(32),
  impret character varying(32),
  signo_2 character varying(32),
  impneto character varying(32),
  signo_3 character varying(32),
  retesp character varying(32),
  signo_4 character varying(32),
  retiva_esp character varying(32),
  signo_5 character varying(32),
  percep_ba character varying(32),
  signo_6 character varying(32),
  retiva_d1 character varying(32),
  signo_7 character varying(32),
  retiva_d2 character varying(32),
  signo_8 character varying(32),
  cargo_pex character varying(32),
  signo_9 character varying(32),
  retiva_pex1 character varying(32),
  signo_10 character varying(32),
  retiva_pex2 character varying(32),
  signo_11 character varying(32),
  costo_cuoemi character varying(32),
  signo_12 character varying(32),
  retiva_cuo1 character varying(32),
  signo_13 character varying(32),
  retiva_cuo2 character varying(32),
  signo_14 character varying(32),
  imp_serv character varying(32),
  signo_15 character varying(32),
  iva1_xlj character varying(32),
  signo_16 character varying(32),
  iva2_xlj character varying(32),
  signo_17 character varying(32),
  cargo_edc_e character varying(32),
  signo_18 character varying(32),
  iva1_edc_e character varying(32),
  signo_19 character varying(32),
  iva2_edc_e character varying(32),
  signo_20 character varying(32),
  cargo_edc_b character varying(32),
  signo_21 character varying(32),
  iva1_edc_b character varying(32),
  signo_22 character varying(32),
  iva2_edc_b character varying(32),
  signo_23 character varying(32),
  cargo_cit_e character varying(32),
  signo_24 character varying(32),
  iva1_cit_e character varying(32),
  signo_25 character varying(32),
  iva2_cit_e character varying(32),
  signo_26 character varying(32),
  cargo_cit_b character varying(32),
  signo_27 character varying(32),
  iva1_cit_b character varying(32),
  signo_28 character varying(32),
  iva2_cit_b character varying(32),
  signo_29 character varying(32),
  ret_iva character varying(32),
  signo_30 character varying(32),
  ret_gcias character varying(32),
  signo_31 character varying(32),
  ret_ingbru character varying(32),
  signo_32 character varying(32),
  aster character varying(32),
  casacta character varying(32),
  tipcta character varying(32),
  ctabco character varying(32),
  cf_exento_iva character varying(32),
  signo_04_1 character varying(32),
  ley_25063 character varying(32),
  signo_04_2 character varying(32),
  ali_ingbru character varying(32),
  dto_campania character varying(32),
  signo_04_3 character varying(32),
  iva1_dto_campania character varying(32),
  signo_04_4 character varying(32),
  ret_ingbru2 character varying(32),
  signo_04_5 character varying(32),
  ali_ingbru2 character varying(32),
  tasa_pex character varying(32),
  cargo_x_liq character varying(32),
  signo_04_8 character varying(32),
  iva1_cargo_x_liq character varying(32),
  signo_04_9 character varying(32),
  dealer character varying(32),
  imp_db_cr character varying(32),
  signo_04_10 character varying(32),
  cf_no_reduce_iva character varying(32),
  signo_04_11 character varying(32),
  percep_ib_agip character varying(32),
  signo_04_12 character varying(32),
  alic_percep_ib_agip character varying(32),
  reten_ib_agip character varying(32),
  signo_04_13 character varying(32),
  alic_reten_ib_agip character varying(32),
  subtot_retiva_rg3130 character varying(32),
  signo_04_14 character varying(32),
  prov_ingbru character varying(32),
  adic_plancuo character varying(32),
  signo_04_15 character varying(32),
  iva1_ad_plancuo character varying(32),
  signo_04_16 character varying(32),
  adic_opinter character varying(32),
  signo_04_17 character varying(32),
  iva1_ad_opinter character varying(32),
  signo_04_18 character varying(32),
  adic_altacom character varying(32),
  signo_04_19 character varying(32),
  iva1_ad_altacom character varying(32),
  signo_04_20 character varying(32),
  adic_cupmanu character varying(32),
  signo_04_21 character varying(32),
  iva1_ad_cupmanu character varying(32),
  signo_04_22 character varying(32),
  adic_altacom_bco character varying(32),
  signo_04_23 character varying(32),
  iva1_ad_altacom_bco character varying(32),
  signo_04_24 character varying(32),
  revisado character varying(32),
  CONSTRAINT visapayments_key PRIMARY KEY (i_visapayments_id),
  CONSTRAINT visapayments_unique_id UNIQUE (id),
  CONSTRAINT visapaymentsclient FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT visapaymentsorg FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE i_visapayments
  OWNER TO libertya;

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_EntidadFinanciera','EstablishmentNumber','character varying(45)'));

--20170125-1300 Merge de Revision 1749
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_CreditCardSettlement','CreditCardType','character(2)'));

DROP VIEW libertya.c_paymentcoupon_v;

CREATE OR REPLACE VIEW libertya.c_paymentcoupon_v AS
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
	p.auditstatus,
	ah.documentno AS allocationnumber
FROM
	libertya.c_payment p
	JOIN libertya.m_entidadfinancieraplan efp
		ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id
	LEFT JOIN libertya.c_couponssettlements cs
		ON p.c_payment_id = cs.c_payment_id
	LEFT JOIN libertya.c_creditcardsettlement ccs
		ON cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id
	LEFT JOIN libertya.c_allocationline al
		ON p.c_payment_id = al.c_payment_id
	LEFT JOIN libertya.c_allocationhdr ah
		ON al.c_allocationhdr_id = ah.c_allocationhdr_id;
ALTER TABLE c_paymentcoupon_v OWNER TO libertya;

--20170127-1430 Nuevas columnas para obtener cadenas de autorización al importar facturas
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('i_invoice','authorizationchainvalue','character varying(40)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('i_invoice','m_authorizationchain_id','integer'));

--20170130-1600 Merge de Revisiones 1758 y 1760
ALTER TABLE libertya.c_creditcardcouponfilter
   ALTER COLUMN m_entidadfinanciera_id DROP NOT NULL;

ALTER TABLE libertya.c_creditcardcouponfilter
   ALTER COLUMN m_entidadfinancieraplan_id DROP NOT NULL;

ALTER TABLE libertya.c_ivasettlements
  DROP CONSTRAINT ivasettlementstaxcategory;

ALTER TABLE libertya.c_ivasettlements RENAME c_taxcategory_id  TO c_tax_id;

ALTER TABLE libertya.c_ivasettlements
  ADD CONSTRAINT ivasettlementstax FOREIGN KEY (c_tax_id) REFERENCES libertya.c_tax (c_tax_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE libertya.c_couponssettlements
   ALTER COLUMN c_payment_id DROP NOT NULL;

ALTER TABLE libertya.c_perceptionssettlement
  DROP CONSTRAINT perceptionssettlementtaxcategory;

ALTER TABLE libertya.c_perceptionssettlement RENAME c_taxcategory_id  TO c_tax_id;

ALTER TABLE libertya.c_perceptionssettlement
  ADD CONSTRAINT perceptionstax FOREIGN KEY (c_tax_id) REFERENCES libertya.c_tax (c_tax_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE c_couponssettlements
  DROP COLUMN allocationnumber;

ALTER TABLE c_couponssettlements
  ADD COLUMN allocationnumber character varying(30);

ALTER TABLE libertya.c_commissionconcepts
  DROP COLUMN concepttype;

ALTER TABLE libertya.c_commissionconcepts
  ADD COLUMN c_cardsettlementconcepts_id integer NOT NULL;

ALTER TABLE libertya.c_commissionconcepts
  ADD CONSTRAINT commisioncardsettlementconcepts FOREIGN KEY (c_cardsettlementconcepts_id) REFERENCES libertya.c_cardsettlementconcepts (c_cardsettlementconcepts_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE libertya.c_expenseconcepts
  DROP COLUMN concepttype;

ALTER TABLE libertya.c_expenseconcepts
  ADD COLUMN c_cardsettlementconcepts_id integer NOT NULL;

ALTER TABLE libertya.c_expenseconcepts
  ADD CONSTRAINT dkcardsettlementconcepts FOREIGN KEY (c_cardsettlementconcepts_id) REFERENCES libertya.c_cardsettlementconcepts (c_cardsettlementconcepts_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--20170202-1415 Mejoras de performance al Reporte de Compras
CREATE OR REPLACE FUNCTION get_bpartner_product_po(productID integer)
  RETURNS integer AS
  --Obtiene el proveedor actual del artículo parámetro
$BODY$
DECLARE
  bpid integer := 0;
BEGIN
  -- Busco el proveedor activo ordenado por currentvendor y fecha de actualización en caso que ninguno esté marcado como proveedor actual
  SELECT INTO bpid c_bpartner_id
  FROM m_product_po po
  WHERE po.m_product_id = productID AND po.isactive = 'Y'
  ORDER BY po.iscurrentvendor desc, po.updated desc
  LIMIT 1;

  IF(bpid is null)
  THEN bpid = 0;
  END IF;

  RETURN bpid;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION get_bpartner_product_po(integer)
  OWNER TO libertya;

--20170203-1445 Mejoras de performance a los reportes que utilizan la vista v_product_movements
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
productlinesname character varying(60), order_documentno character varying(30));

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
			l.qty, d.transfertype AS type, d.movementtype AS aditionaltype, 
			c.c_charge_id, c.name as chargename, d.m_warehouse_id, 
			d.m_warehouseto_id, bp.c_bpartner_id, bp.value as bpartnervalue, bp.name as bpartnername, 
			pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, 
			COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, 
			COALESCE(pg.value, ''SD''::character varying) AS productgamasvalue, 
			COALESCE(pg.name, ''SIN DESCRIPCION''::character varying) AS productgamasname, 
			COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, 
			COALESCE(pl.value, ''SD''::character varying) AS productlinesvalue, 
			COALESCE(pl.name, ''SIN DESCRIPCION''::character varying) AS productlinesname, 
			NULL::character varying(30) AS order_documentno
		 FROM m_transfer d
		 JOIN m_transferline l ON l.m_transfer_id = d.m_transfer_id 
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
			NULL::character varying(30) AS order_documentno 
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
			o.documentno AS order_documentno
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
			NULL::character varying(30) AS order_documentno 
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
			NULL::character varying(30) AS order_documentno 
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
			NULL::character varying(30) AS order_documentno 
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
			l.order_documentno

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

drop view v_product_movements;

create or replace view v_product_movements as 
select * 
from v_product_movements_filtered(-1,null,null,-1,-1);

--20170209-1214 Merge de Revisión 1778
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -
--  - - - Trigger que setea Estado Auditoría "Pendiente de Cierre" por    - - - -
--  - - - a los pagos con tarjetas									      - - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -

CREATE OR REPLACE FUNCTION setDefaultAuditStatus()
  RETURNS trigger AS
$BODY$
BEGIN
IF NEW.tendertype = 'C' THEN
	NEW.auditstatus = 'CP';
END IF;
RETURN NEW;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION setDefaultAuditStatus()
  OWNER TO libertya;



CREATE TRIGGER "setDefaultAuditStatusTrg" BEFORE INSERT
   ON c_payment FOR EACH ROW
   EXECUTE PROCEDURE libertya.setdefaultauditstatus();

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -
--  - - - Indice para referencia a pagos desde los cupones en liquidación - - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -

CREATE INDEX paymentindex
	ON libertya.c_couponssettlements (c_payment_id);

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -
--  - - - - - Se agrega referencia a C_payment para las liquidaciones - - - - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_creditcardsettlement','c_payment_id','integer'));

ALTER TABLE c_creditcardsettlement
  ADD CONSTRAINT fkpayment FOREIGN KEY (c_payment_id)
  REFERENCES c_payment (c_payment_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-- - - - Se modifican todas las claves foráneas de las tablas de la ventana de -
-- - - - liquidacion de tarjetas de crédito, a fin de permitir la eliminación  -
-- - - - en cascada. - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_ivasettlements
  DROP CONSTRAINT ivasettlementscreditcardsettlement;

ALTER TABLE libertya.c_ivasettlements
  ADD CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) 
  REFERENCES libertya.c_creditcardsettlement (c_creditcardsettlement_id)
  ON UPDATE NO ACTION ON DELETE CASCADE;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_commissionconcepts
  DROP CONSTRAINT fkcreditcardsettlement;

ALTER TABLE libertya.c_commissionconcepts
  ADD CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) 
  REFERENCES libertya.c_creditcardsettlement (c_creditcardsettlement_id)
  ON UPDATE NO ACTION ON DELETE CASCADE;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_withholdingsettlement
  DROP CONSTRAINT fkcreditcardsettlement;

ALTER TABLE libertya.c_withholdingsettlement
  ADD CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) 
  REFERENCES libertya.c_creditcardsettlement (c_creditcardsettlement_id)
  ON UPDATE NO ACTION ON DELETE CASCADE;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_perceptionssettlement
  DROP CONSTRAINT perceptionssettlementcreditcardsettlement;

ALTER TABLE libertya.c_perceptionssettlement
  ADD CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) 
  REFERENCES libertya.c_creditcardsettlement (c_creditcardsettlement_id)
  ON UPDATE NO ACTION ON DELETE CASCADE;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_expenseconcepts
  DROP CONSTRAINT fkcreditcardsettlement;

ALTER TABLE libertya.c_expenseconcepts
  ADD CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) 
  REFERENCES libertya.c_creditcardsettlement (c_creditcardsettlement_id)
  ON UPDATE NO ACTION ON DELETE CASCADE;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_creditcardcouponfilter
  DROP CONSTRAINT fkcreditcardsettlement;

ALTER TABLE libertya.c_creditcardcouponfilter
  ADD CONSTRAINT fkcreditcardsettlement FOREIGN KEY (c_creditcardsettlement_id) 
  REFERENCES libertya.c_creditcardsettlement (c_creditcardsettlement_id)
  ON UPDATE NO ACTION ON DELETE CASCADE;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_couponssettlements
  DROP CONSTRAINT fkcreditcardcouponfilter;

ALTER TABLE libertya.c_couponssettlements
  ADD CONSTRAINT fkcreditcardcouponfilter FOREIGN KEY (c_creditcardcouponfilter_id)
  REFERENCES libertya.c_creditcardcouponfilter (c_creditcardcouponfilter_id)
  ON UPDATE NO ACTION ON DELETE CASCADE;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-- - - - Se agrega un campo a la entidad financiera, para indicar el servicio  -
-- - - - financiero (Visa, Amex, FirstData, etc) - - - - - - - - - - - - - - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_entidadfinanciera','financingservice','character(2)'));

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-- - - - Se agrega un campo "Conciliado" al cupón dentro de la liquidación - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_couponssettlements','isreconciled','character(1) NOT NULL DEFAULT ''N''::bpchar'));
  
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -
-- - - - Se agrega un campo a la vista, para asignarlo a un boton, se quitaron  -
-- - - - otros que no se utilizaban, permitiendo quitar joins, y se mejoró la - -
-- - - - condición de match - - - - - - - - - - - - - - - - - - - - - - - - - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  -

DROP VIEW libertya.c_paymentcoupon_v;

CREATE OR REPLACE VIEW libertya.c_paymentcoupon_v AS 
	SELECT
		p.c_payment_id,
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
		p.datetrx,
		p.couponbatchnumber,
		p.payamt,
		p.c_currency_id,
		p.docstatus,
		cs.isreconciled,
		efp.cuotaspago AS totalallocations,
		ccs.paymentdate AS settlementdate,
		p.auditstatus,
		''::character(1) AS reject
	FROM
		libertya.c_payment p
		LEFT JOIN libertya.m_entidadfinancieraplan efp
			ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id
		LEFT JOIN libertya.c_couponssettlements cs
			ON p.c_payment_id = cs.c_payment_id
		LEFT JOIN libertya.c_creditcardsettlement ccs
			ON cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id
	WHERE
		p.tendertype = 'C';

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-- - - Se cambia la referencia a Entidad Financiera, por Entidad Comercial - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

ALTER TABLE libertya.c_creditcardsettlement
  DROP CONSTRAINT fkentidadfinanciera;

ALTER TABLE libertya.c_creditcardsettlement
  DROP COLUMN m_entidadfinanciera_id;

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_creditcardsettlement','c_bpartner_id','integer NOT NULL'));

ALTER TABLE libertya.c_creditcardsettlement
  ADD CONSTRAINT fkbpartner FOREIGN KEY (c_bpartner_id)
  REFERENCES libertya.c_bpartner (c_bpartner_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
-- - - - Se agrega a los filtros de la liquidación, la entidad comercial - - -
-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_creditcardcouponfilter','c_bpartner_id','integer'));

ALTER TABLE libertya.c_creditcardcouponfilter
  ADD CONSTRAINT fkbpartner FOREIGN KEY (c_bpartner_id)
  REFERENCES libertya.c_bpartner (c_bpartner_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;
  
--20170210-1620 Nueva tabla para registrar los datos CAI de cada entidad comercial
CREATE TABLE c_bpartner_cai
(
  c_bpartner_cai_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_bpartner_id integer NOT NULL,
  cai character varying(14) NOT NULL,
  datecai timestamp without time zone NOT NULL,
  posnumber integer NOT NULL,
  CONSTRAINT c_bpartner_cai_key PRIMARY KEY (c_bpartner_cai_id),
  CONSTRAINT c_bpartner_cai_bpartner FOREIGN KEY (c_bpartner_id)
  REFERENCES c_bpartner (c_bpartner_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_cai_client FOREIGN KEY (ad_client_id)
  REFERENCES ad_client (ad_client_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_bpartner_cai_org FOREIGN KEY (ad_org_id)
  REFERENCES ad_org (ad_org_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c_bpartner_cai
  OWNER TO libertya;
  
--20170301-1015 Merge de Revisión 1797
ALTER TABLE c_couponssettlements DROP COLUMN allocationnumber;

--20170523-1328 PATCH necesario por potencial secuencia inexistente (postgres 8.4 no soporta IF NOT EXISTS)
CREATE OR REPLACE FUNCTION addsequenceifnotexists (
    sequencename character varying,
    sequenceprops character varying)
  RETURNS numeric AS
$BODY$
DECLARE
	existe integer;
BEGIN
	SELECT into existe COUNT(1) 
	FROM information_schema.sequences
	WHERE lower(sequence_name) = lower(sequencename);
	
	IF (existe = 0) THEN
		EXECUTE 'CREATE SEQUENCE ' || sequencename || ' ' || sequenceprops;
		RETURN 1;
	END IF;

	RETURN 0;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION addsequenceifnotexists(character varying, character varying)
  OWNER TO libertya;
  
update ad_system set dummy = (SELECT addsequenceifnotexists('seq_c_externalservice', 'INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1010001 CACHE 1'));
--FIN PATCH


--20170301-1300 Merge de Revisión 1798
CREATE TABLE c_externalserviceattributes
(
  c_externalserviceattributes_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_externalservice_id integer,
  value character varying(128) NOT NULL,
  name character varying(128),
  description character varying(255),
  CONSTRAINT externalserviceattributes_key PRIMARY KEY (c_externalserviceattributes_id),
  CONSTRAINT externalserviceattributesclient FOREIGN KEY (ad_client_id)
    REFERENCES libertya.ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT externalserviceattributesorg FOREIGN KEY (ad_org_id)
    REFERENCES libertya.ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT externalserviceattributesexternalservice FOREIGN KEY (c_externalservice_id)
    REFERENCES libertya.c_externalservice (c_externalservice_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);
ALTER TABLE c_externalserviceattributes
  OWNER TO libertya;

CREATE SEQUENCE libertya.seq_c_externalserviceattributes
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000001
  CACHE 1;
  
CREATE SEQUENCE libertya.seq_i_firstdatatraileranddetail
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000001
  CACHE 1;

CREATE TABLE i_firstdatatraileranddetail
(
  i_firstdatatraileranddetail_id integer NOT NULL DEFAULT nextval('libertya.seq_i_firstdatatraileranddetail'::regclass),
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
  tipo_registro character varying(32),
  nombre_archivo character varying(32),
  comercio_centralizador character varying(32),
  moneda character varying(32),
  grupo_presentacion character varying(32),
  plazo_pago character varying(32),
  tipo_plazo_pago character varying(32),
  fecha_presentacion character varying(32),
  fecha_vencimiento_clearing character varying(32),
  producto character varying(32),
  comercio_participante character varying(32),
  entidad_pagadora character varying(32),
  sucursal_pagadora character varying(32),
  numero_liquidacion character varying(32),
  total_importe_total character varying(32),
  total_importe_total_signo character varying(32),
  total_importe_sin_dto character varying(32),
  total_importe_sin_dto_signo character varying(32),
  total_importe_final character varying(32),
  total_importe_final_signo character varying(32),
  aranceles_cto_fin character varying(32),
  aranceles_cto_fin_signo character varying(32),
  retenciones_fiscales character varying(32),
  retenciones_fiscales_signo character varying(32),
  otros_debitos character varying(32),
  otros_debitos_signo character varying(32),
  otros_creditos character varying(32),
  otros_creditos_signo character varying(32),
  neto_comercios character varying(32),
  neto_comercios_signo character varying(32),
  total_registros_detalle character varying(32),
  monto_pend_cuotas character varying(32),
  monto_pend_cuotas_signo character varying(32),
  subtipo_registro character varying(32),
  iva_aranceles_ri character varying(32),
  iva_aranceles_ri_signo character varying(32),
  impuesto_deb_cred character varying(32),
  impuesto_deb_cred_signo character varying(32),
  iva_dto_pago_anticipado character varying(32),
  iva_dto_pago_anticipado_signo character varying(32),
  ret_iva_ventas character varying(32),
  ret_iva_ventas_signo character varying(32),
  percepc_iva_r3337 character varying(32),
  percepc_iva_r3337_signo character varying(32),
  ret_imp_ganancias character varying(32),
  ret_imp_ganancias_signo character varying(32),
  ret_imp_ingresos_brutos character varying(32),
  ret_imp_ingresos_brutos_signo character varying(32),
  percep_ingr_brutos character varying(32),
  percep_ingr_brutos_signo character varying(32),
  iva_servicios character varying(32),
  iva_servicios_signo character varying(32),
  categoria_iva character varying(32),
  imp_sintereses_ley_25063 character varying(32),
  imp_sintereses_ley_25063_signo character varying(32),
  arancel character varying(32),
  arancel_signo character varying(32),
  costo_financiero character varying(32),
  costo_financiero_signo character varying(32),
  revisado character varying(32),
  hash_modelo character varying(32),
  provincia_ing_brutos character varying(32),
  fecha_operacion character varying(32),
  codigo_movimiento character varying(32),
  codigo_origen character varying(32),
  caja_nro_cinta_posnet character varying(32),
  caratula_terminal_posnet character varying(32),
  resumen_lote_posnet character varying(32),
  cupon_cupon_posnet character varying(32),
  cuotas_plan character varying(32),
  cuota_vigente character varying(32),
  porc_desc character varying(32),
  marca_error character varying(32),
  tipo_plan_cuotas character varying(32),
  nro_tarjeta character varying(32),
  motivo_rechazo_1 character varying(32),
  motivo_rechazo_2 character varying(32),
  motivo_rechazo_3 character varying(32),
  motivo_rechazo_4 character varying(32),
  fecha_present_original character varying(32),
  motivo_reversion character varying(32),
  tipo_operacion character varying(32),
  marca_campana character varying(32),
  codigo_cargo_pago character varying(32),
  entidad_emisora character varying(32),
  importe_arancel character varying(32),
  importe_arancel_signo character varying(32),
  iva_arancel character varying(32),
  iva_arancel_signo character varying(32),
  promocion_cuotas_alfa character varying(32),
  tna character varying(32),
  importe_costo_financiero character varying(32),
  importe_costo_financiero_signo character varying(32),
  iva_costo_financiero character varying(32),
  iva_costo_financiero_signo character varying(32),
  porcentaje_tasa_directa character varying(32),
  importe_costo_tasa_dta character varying(32),
  importe_costo_tasa_dta_signo character varying(32),
  iva_costo_tasa_dta character varying(32),
  iva_costo_tasa_dta_signo character varying(32),
  nro_autoriz character varying(32),
  alicuota_iva_fo character varying(32),
  marca_cashback character varying(32),
  importe_total character varying(32),
  importe_total_signo character varying(32),

  CONSTRAINT firstdatatraileranddetail_key PRIMARY KEY (i_firstdatatraileranddetail_id),
  CONSTRAINT firstdatatraileranddetailclient FOREIGN KEY (ad_client_id)
    REFERENCES libertya.ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT firstdatatraileranddetailorg FOREIGN KEY (ad_org_id)
    REFERENCES libertya.ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE i_firstdatatraileranddetail
  OWNER TO libertya;

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_entidadfinanciera','c_region_id','integer'));

ALTER TABLE m_entidadfinanciera
  ADD CONSTRAINT entidadfinancieraregion FOREIGN KEY (c_region_id) 
  REFERENCES c_region (c_region_id)
  ON UPDATE NO ACTION ON DELETE NO ACTION;

----------------------------------------------------------------------
---------- Modificación de tablas y/o vistas
----------------------------------------------------------------------

INSERT INTO c_externalservice (c_externalservice_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, ad_componentobjectuid, value, url, username, password, timeout, port) VALUES (nextval('seq_c_externalservice'), 1010016, 0, 'Y', '2017-02-14 10:20:18.511889', 1010717, '2017-02-15 16:56:17.362804', 1010717, 'Central Pos - Visa', 'Configuración de Conexión con la API de Central Pos', null, 'Central Pos - Visa', 'https://liquidacion.centralpos.com/api/v1/visa/pagos', null, null, 10000, 0);
INSERT INTO c_externalservice (c_externalservice_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, ad_componentobjectuid, value, url, username, password, timeout, port) VALUES (nextval('seq_c_externalservice'), 1010016, 0, 'Y', '2017-02-14 14:18:06.438737', 1010717, '2017-02-20 10:53:31.014661', 1010717, 'Central Pos - Amex', 'Configuración de Conexión con la API de Central Pos', null, 'Central Pos - Amex', 'https://liquidacion.centralpos.com/api/v1/amex/pagos', null, null, 10000, 0);
INSERT INTO c_externalservice (c_externalservice_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, ad_componentobjectuid, value, url, username, password, timeout, port) VALUES (nextval('seq_c_externalservice'), 1010016, 0, 'Y', '2017-02-14 14:26:18.081836', 1010717, '2017-02-20 10:53:40.372518', 1010717, 'Central Pos - Naranja', 'Configuración de Conexión con la API de Central Pos', null, 'Central Pos - Naranja', 'https://liquidacion.centralpos.com/api/v1/naranja/cupones', null, null, 10000, 0);
INSERT INTO c_externalservice (c_externalservice_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description, ad_componentobjectuid, value, url, username, password, timeout, port) VALUES (nextval('seq_c_externalservice'), 1010016, 0, 'Y', '2017-02-14 14:27:42.255858', 1010717, '2017-02-20 10:53:45.245823', 1010717, 'Central Pos - FirstData', 'Configuración de Conexión con la API de Central Pos', null, 'Central Pos - FirstData', 'https://liquidacion.centralpos.com/api/v1/firstdata/trailer-participantes', null, null, 10000, 0);

INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:51:59.6775', 1010717, '2017-02-15 12:23:48.530876', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), '100', 'Elementos por Página', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:53:01.801653', 1010717, '2017-02-15 12:23:55.227087', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), 'https://liquidacion.centralpos.com/api/v1/auth/login', 'URL Login', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:52:05.279296', 1010717, '2017-02-15 12:24:05.296348', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), '100', 'Elementos por Página', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:58:43.465863', 1010717, '2017-02-15 12:24:13.101356', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), 'https://liquidacion.centralpos.com/api/v1/auth/login', 'URL Login', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 17:04:30.44652', 1010717, '2017-02-15 12:24:20.553311', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), 'https://liquidacion.centralpos.com/api/v1/amex/impuestos', 'URL Impuestos', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:52:10.577212', 1010717, '2017-02-15 12:24:32.467882', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), '100', 'Elementos por Página', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:59:17.752203', 1010717, '2017-02-15 12:24:42.211211', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), 'https://liquidacion.centralpos.com/api/v1/auth/login', 'URL Login', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 17:06:40.636472', 1010717, '2017-02-15 12:24:52.204501', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), 'https://liquidacion.centralpos.com/api/v1/naranja/headers', 'URL Headers', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:52:16.544699', 1010717, '2017-02-15 12:25:13.287727', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), '100', 'Elementos por Página', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 14:59:26.766437', 1010717, '2017-02-15 12:25:20.703193', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), 'https://liquidacion.centralpos.com/api/v1/auth/login', 'URL Login', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:41:32.164247', 1010717, '2017-02-16 15:42:09.344558', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'IVA', 'Percepción');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:41:12.725329', 1010717, '2017-02-16 15:42:34.032986', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'IVA 10.5', 'Categ. de Impuesto');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:41:24.398294', 1010717, '2017-02-16 15:42:36.058575', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'IVA 21', 'Categ. de Impuesto');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:41:07.910172', 1010717, '2017-02-16 15:46:11.382262', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Importe Arancel', 'Comisión');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:41:01.37173', 1010717, '2017-02-16 15:46:25.153487', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Cargo adic por op internacionales', 'Otros Conceptos');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:40:56.216762', 1010717, '2017-02-16 15:46:46.799924', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Cargo adic por planes cuotas', 'Otros Conceptos');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:40:54.100695', 1010717, '2017-02-16 15:47:05.200153', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Costo plan acelerado cuotas', 'Otros Conceptos');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:40:45.952298', 1010717, '2017-02-16 15:47:07.431846', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Dto por ventas de campañas', 'Otros Conceptos');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:40:39.909383', 1010717, '2017-02-16 15:47:24.577121', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Ret Ganancias', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:40:34.865921', 1010717, '2017-02-16 15:47:26.711228', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Ret IVA', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 15:40:26.51381', 1010717, '2017-02-16 15:47:28.335794', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Visa' limit 1), NULL, 'Ret IIBB', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:15:35.227653', 1010717, '2017-02-16 17:15:35.228817', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), NULL, 'Imp total desc aceleracion', 'Otros Conceptos');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:16:11.296722', 1010717, '2017-02-16 17:16:11.297652', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), NULL, 'Importe Descuento', 'Comisión');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:18:41.655438', 1010717, '2017-02-16 17:18:41.656098', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), NULL, 'Retencion Ganancias', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:18:26.644715', 1010717, '2017-02-16 17:18:44.159658', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), NULL, 'Retencion IVA', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:19:45.104485', 1010717, '2017-02-16 17:19:45.106767', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), NULL, 'Percepcion IVA', 'Percepción');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:20:59.278118', 1010717, '2017-02-16 17:20:59.279115', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), NULL, 'Retencion IVA', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:21:16.807992', 1010717, '2017-02-16 17:21:16.808615', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), NULL, 'Retencion IIBB', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:21:30.22005', 1010717, '2017-02-16 17:21:30.220799', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), NULL, 'Retencion Ganancias', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:24:56.857436', 1010717, '2017-02-16 17:24:56.858368', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), NULL, 'Comisiones - Conceptos fact a descontar mes pago', 'Comisión');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:25:09.43474', 1010717, '2017-02-16 17:25:09.435451', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), NULL, 'Gastos - Conceptos fact a descontar mes pago', 'Otros Conceptos');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:25:35.827578', 1010717, '2017-02-16 17:25:35.828324', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), NULL, 'IVA 21', 'Categoría de Impuesto');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:31:19.739017', 1010717, '2017-02-16 17:31:19.739868', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), NULL, 'Ret Ganancias e Ing Brutos', 'Retención');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:31:38.470488', 1010717, '2017-02-16 17:31:38.471302', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), NULL, 'Percepcion IVA', 'Percepción');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:31:58.540236', 1010717, '2017-02-16 17:31:58.540961', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), NULL, 'Arancel', 'Otros Conceptos');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-16 17:32:25.964729', 1010717, '2017-02-16 17:32:25.965447', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), NULL, 'Costo Financiero', 'Comisión');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-20 11:12:16.069595', 1010717, '2017-02-20 11:12:16.070473', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Amex' limit 1), NULL, 'IVA 21', 'Categoría de Impuesto');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-20 11:12:38.653004', 1010717, '2017-02-20 11:12:38.653779', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), NULL, 'IVA 21', 'Categoría de Impuesto');
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-20 17:35:42.901619', 1010717, '2017-02-21 09:56:27.553305', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - FirstData' limit 1), 'https://liquidacion.centralpos.com/api/v1/firstdata/liquidacion-participantes', 'URL detalle liq', NULL);
INSERT INTO c_externalserviceattributes (c_externalserviceattributes_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_externalservice_id, name, value, description) VALUES (nextval('seq_c_externalserviceattributes'), 1010016, 0, 'Y', '2017-02-14 17:12:57.074976', 1010717, '2017-02-21 11:59:46.305476', 1010717, (select c_externalservice_id from c_externalservice where value = 'Central Pos - Naranja' limit 1), 'https://liquidacion.centralpos.com/api/v1/naranja/conceptos-facturados-meses', 'URL Conceptos', NULL);

--20170303-1015 Nueva configuración por perfil que restringe creación de OPA
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('ad_role','isallowopa','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20170303-1245 Incorporación de nueva columna cuenta bancaria al detalle de los allocations
DROP VIEW c_allocation_detail_v;

CREATE OR REPLACE VIEW c_allocation_detail_v AS 
 SELECT ah.c_allocationhdr_id AS c_allocation_detail_v_id, ah.c_allocationhdr_id, ah.ad_client_id, ah.ad_org_id, ah.isactive, ah.created, ah.createdby, ah.updated, ah.updatedby, ah.datetrx AS fecha, i.documentno AS factura, COALESCE(i.c_currency_id, p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS c_currency_id, i.grandtotal AS montofactura, 
        CASE
            WHEN p.documentno IS NOT NULL THEN p.documentno
            ELSE 
            CASE
                WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
                ELSE NULL::character varying
            END
        END AS pagonro, 
        CASE
            WHEN al.c_invoice_credit_id IS NOT NULL THEN 'N'::bpchar
            WHEN p.tendertype IS NOT NULL THEN p.tendertype
            WHEN p.tendertype IS NULL THEN 'CA'::bpchar
            ELSE NULL::bpchar
        END AS tipo, 
        CASE
            WHEN cl.c_cashline_id IS NOT NULL THEN 'Y'::text
            WHEN cl.c_cashline_id IS NULL THEN 'N'::text
            ELSE NULL::text
        END AS cash, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric(20,2)) AS montosaldado, abs(COALESCE(p.payamt, cl.amount, credit.grandtotal, 0::numeric(20,2))) AS payamt, al.c_allocationline_id, i.c_invoice_id, 
        CASE
            WHEN p.documentno IS NOT NULL THEN p.documentno
            ELSE 
            CASE
                WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
                WHEN al.c_cashline_id IS NOT NULL THEN cl.description
                ELSE NULL::character varying
            END
        END AS paydescription, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN pppm.name
            WHEN al.c_cashline_id IS NOT NULL THEN cppm.name
            WHEN al.c_invoice_credit_id IS NOT NULL THEN dt.name
            ELSE NULL::character varying
        END AS payment_medium_name, COALESCE(p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS pay_currency_id,
        p.c_bankaccount_id
   FROM c_allocationhdr ah
   JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice credit ON al.c_invoice_credit_id = credit.c_invoice_id
   LEFT JOIN c_doctype dt ON credit.c_doctype_id = dt.c_doctype_id
   LEFT JOIN c_pospaymentmedium cppm ON cppm.c_pospaymentmedium_id = cl.c_pospaymentmedium_id
   LEFT JOIN c_pospaymentmedium pppm ON pppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
  ORDER BY ah.c_allocationhdr_id, al.c_allocationline_id;

ALTER TABLE c_allocation_detail_v
  OWNER TO libertya;
  
--20170307-2018 Incorporación de tabla AD_Role en el changelog
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('ad_role','ad_componentversion_id','integer'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('ad_role','ad_componentobjectuid','character varying(100)'));

update ad_role
set ad_componentobjectuid = 'CORE-AD_Role-'||ad_role_id, ad_componentversion_id = (select ad_componentversion_id from ad_componentversion where ad_componentobjectuid = 'CORE-AD_ComponentVersion-1010064');

--20170307-2315 Función que permite cargar un binario a partir de un archivo
create or replace function bytea_import(p_path text, p_result out bytea) 
                   language plpgsql as $$
declare
  l_oid oid;
  r record;
begin
  p_result := '';
  select lo_import(p_path) into l_oid;
  for r in ( select data 
             from pg_largeobject 
             where loid = l_oid 
             order by pageno ) loop
    p_result = p_result || r.data;
  end loop;
  perform lo_unlink(l_oid);
end;$$;
alter function bytea_import(text, out bytea)
owner to libertya;

--20170308-1806 Modificación de la traducción del tipo de documento Transferencia entre cuentas
update c_doctype_trl
set name = 'Transferencia Entre Cuentas', printname = 'Transferencia Entre Cuentas'
where ad_componentobjectuid in ('CORE-C_DocType_Trl-1010516-es_ES',
'CORE-C_DocType_Trl-1010516-es_AR',
'CORE-C_DocType_Trl-1010516-es_MX',
'CORE-C_DocType_Trl-1010516-es_PY');

--Actualización del tipo de documento transferencia saliente por transferencia entre cuentas
update ad_clientinfo
set c_outgoingtransfer_dt_id = (select c_doctype_id from c_doctype where ad_componentobjectuid = 'CORE-C_DocType-1010516')
where c_outgoingtransfer_dt_id = (select c_doctype_id from c_doctype where ad_componentobjectuid = 'CORE-C_DocType-1010515');

update c_payment
set c_doctype_id = (select c_doctype_id from c_doctype where ad_componentobjectuid = 'CORE-C_DocType-1010516')
where c_doctype_id = (select c_doctype_id from c_doctype where ad_componentobjectuid = 'CORE-C_DocType-1010515');

--20170310-1700 Incorporación de flag isprinted a la importación de facturas que permiteindicar facturas ya impresas fiscalmente
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('i_invoice','isprinted','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20170315-1633 Nueva columna de asignación relacionada al payment
DROP VIEW rv_payment;

CREATE OR REPLACE VIEW rv_payment AS 
 SELECT c_payment.c_payment_id, c_payment.ad_client_id, c_payment.ad_org_id, c_payment.isactive, c_payment.created, 
	c_payment.createdby, c_payment.updated, c_payment.updatedby, c_payment.documentno, c_payment.datetrx, 
	c_payment.isreceipt, c_payment.c_doctype_id, c_payment.trxtype, c_payment.c_bankaccount_id, 
	c_payment.c_bpartner_id, c_payment.c_invoice_id, c_payment.c_bp_bankaccount_id, c_payment.c_paymentbatch_id, 
	c_payment.tendertype, c_payment.creditcardtype, c_payment.creditcardnumber, c_payment.creditcardvv, 
	c_payment.creditcardexpmm, c_payment.creditcardexpyy, c_payment.micr, c_payment.routingno, c_payment.accountno, 
	c_payment.checkno, c_payment.a_name, c_payment.a_street, c_payment.a_city, c_payment.a_state, c_payment.a_zip, 
	c_payment.a_ident_dl, c_payment.a_ident_ssn, c_payment.a_email, c_payment.voiceauthcode, c_payment.orig_trxid, 
	c_payment.ponum, c_payment.c_currency_id, c_payment.c_conversiontype_id, 
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
        END AS multiplierap, 
        paymentallocated(c_payment.c_payment_id::numeric, c_payment.c_currency_id::numeric) AS allocatedamt, 
        paymentavailable(c_payment.c_payment_id::numeric) AS availableamt, 
        c_payment.isoverunderpayment, c_payment.isapproved, c_payment.r_pnref, c_payment.r_result, c_payment.r_respmsg, 
        c_payment.r_authcode, c_payment.r_avsaddr, c_payment.r_avszip, c_payment.r_info, c_payment.processing, 
        c_payment.oprocessing, c_payment.docstatus, c_payment.docaction, c_payment.isprepayment, c_payment.c_charge_id, 
        c_payment.isreconciled, c_payment.isallocated, c_payment.isonline, c_payment.processed, c_payment.posted, 
        c_payment.dateacct,
        (select ah.c_allocationhdr_id 
	from c_allocationhdr ah 
	inner join c_allocationline al on al.c_allocationhdr_id = ah.c_allocationhdr_id
	where ah.docstatus in ('CO','CL') and al.c_payment_id = c_payment.c_payment_id
	order by ah.updated desc
	limit 1) as c_allocationhdr_id
   FROM c_payment;
ALTER TABLE rv_payment
  OWNER TO libertya;
  
--20170317-0725 Nuevo boton para disparar proceso que simule la instalacion de un componente en desarrollo
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ComponentVersion','simulatecvinstallation','char(1)'));

--20170320-1640 Mejora al acceso de campos por perfil que permite tener configurado una validación para sólo lectura o visible
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Field_Access','ad_val_rule_id','integer'));

--20170321-1930 Incorporación de configuración en tipo de documento para omitir todas las operaciones de cuentas corrientes
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_doctype','skipcurrentaccounts','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20170330-1200 Fun view c_invoice_allocation 
CREATE TYPE c_invoice_allocations_type AS (ad_client_id integer, ad_org_id integer, c_allocationhdr_id integer, 
c_doctype_id integer, allocation_doc_name character varying(60), documentno character varying(30), 
allocationtype character varying(50), description character varying(255), datetrx timestamp without time zone, 
isactive character(1), docstatus character(2), c_invoice_id integer, invoice_documentno character varying(30),
dateinvoiced timestamp without time zone, grandtotal numeric(20,2), c_bpartner_id integer, 
value character varying(40), name character varying(60), customer character varying(60), amount numeric(20,2));


CREATE OR REPLACE FUNCTION c_invoice_allocations_filtered(invoiceID integer)
  RETURNS SETOF c_invoice_allocations_type AS
$BODY$
declare
	consulta varchar;
	whereInvoice varchar;
	whereStd varchar;
	adocument c_invoice_allocations_type;
BEGIN
	whereStd = ' (1=1) ';
	-- Armado de las condiciones en base a los parámetros
	-- Invoice
	whereInvoice = '';
	if invoiceID is not null AND invoiceID > 0 THEN
		whereInvoice = ' AND i.c_invoice_id = ' || invoiceID;
	END IF;
	
	-- Armar la consulta
	consulta = ' SELECT ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name AS allocation_doc_name,
ah.documentno, ah.allocationtype, ah.description, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno AS invoice_documentno,
i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name,
COALESCE(i.nombrecli, bp.name)::character varying(60) AS customer, sum(al.amount) AS amount
  FROM c_allocationhdr ah
  LEFT JOIN c_doctype dt ON dt.c_doctype_id = ah.c_doctype_id
  JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
  JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
  JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
  WHERE ' || whereStd || whereInvoice || '
  GROUP BY ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name, ah.documentno, ah.allocationtype, ah.description, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name)
UNION
  SELECT ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name AS allocation_doc_name,
ah.documentno, ah.allocationtype, ah.description, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno AS invoice_documentno,
i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name,
COALESCE(i.nombrecli, bp.name)::character varying(60)AS customer, sum(al.amount) AS amount
  FROM c_allocationhdr ah
  LEFT JOIN c_doctype dt ON dt.c_doctype_id = ah.c_doctype_id
  JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
  JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_credit_id
  JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
  WHERE ' || whereStd || whereInvoice || '
  GROUP BY ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name, ah.documentno, ah.allocationtype, ah.description, ah.datetrx, ah.isactive, ah.docstatus, i.c_invoice_id, i.documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name) ';

-- raise notice '%', consulta;
FOR adocument IN EXECUTE consulta LOOP
	return next adocument;
END LOOP;

END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION c_invoice_allocations_filtered(integer)
  OWNER TO libertya;

drop view c_invoice_allocation_v;

create or replace view c_invoice_allocation_v as 
select * 
from c_invoice_allocations_filtered(-1);

--20170331-1048 Nuevas tabla y columna para control de periodo por punto de venta. IMPORTANTE: Incorporaciones omitidas previamente, que corresponden a la revision r1831 del dia 20170313
CREATE TABLE c_posperiodcontrol
(
  c_posperiodcontrol_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_periodcontrol_id integer,
  c_doctype_id integer,
  periodstatus character(1),
  periodaction character(1) NOT NULL,
  processing character(1),
  CONSTRAINT posperiodcontrol_key PRIMARY KEY (c_posperiodcontrol_id),
  CONSTRAINT posperiodcontrolclient FOREIGN KEY (ad_client_id)
    REFERENCES libertya.ad_client (ad_client_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT posperiodcontrolorg FOREIGN KEY (ad_org_id)
    REFERENCES libertya.ad_org (ad_org_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT posperiodcontrolperiodcontrol FOREIGN KEY (c_periodcontrol_id)
    REFERENCES libertya.c_periodcontrol (c_periodcontrol_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT posperiodcontroldoctype FOREIGN KEY (c_doctype_id)
    REFERENCES libertya.c_doctype (c_doctype_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

update ad_system set dummy = (SELECT addcolumnifnotexists('c_periodcontrol','doctypecontrol','character(1) default ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_periodcontrol','doctypeprocessing','character(1)'));

--20170331-1145 Columna faltante en tabla de detalle de lote de pagos
update ad_system set dummy = (SELECT addcolumnifnotexists('C_PaymentBatchPODetail','c_order_id','integer'));

--20170331-1155 Setear a null la configuración de forma de pago de lote de pagos cuando no posee cuenta bancaria
update c_bpartner
set Batch_Payment_Rule = null
where Batch_Payment_Rule IS NOT NULL AND C_BankAccount_ID IS NULL;

--20170404-1330 Refactor de cadenas de autorización en base al estado de autorización
update ad_system set dummy = (SELECT addcolumnifnotexists('M_AuthorizationChainDocumentType','notauthorizationstatus','character(2)'));

UPDATE M_AuthorizationChainDocumentType
SET notauthorizationstatus = 'WC';

ALTER TABLE M_AuthorizationChainDocumentType ALTER COLUMN notauthorizationstatus SET NOT NULL;

update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','authorizationchainstatus','character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Order','authorizationchainstatus','character(1)'));

UPDATE C_Invoice
SET authorizationchainstatus = 'P'
WHERE authorizationchainstatus is null and m_authorizationchain_id is not null and docstatus = 'WC';

UPDATE C_Invoice
SET authorizationchainstatus = 'A'
WHERE authorizationchainstatus is null and m_authorizationchain_id is not null and docstatus IN ('CO','CL','RE','VO');

UPDATE C_Order
SET authorizationchainstatus = 'P'
WHERE authorizationchainstatus is null and m_authorizationchain_id is not null and docstatus = 'WC';

UPDATE C_Order
SET authorizationchainstatus = 'A'
WHERE authorizationchainstatus is null and m_authorizationchain_id is not null and docstatus IN ('CO','CL','RE','VO');


DROP VIEW v_projectedpayments;
DROP VIEW v_documents;
DROP VIEW rv_bpartneropen;
DROP VIEW c_invoiceline_v;
DROP VIEW c_invoice_v;

CREATE OR REPLACE VIEW c_invoice_v AS
  SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.chargeamt * d.signo_issotrx::numeric AS chargeamt, i.totallines, i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal, d.signo_issotrx::numeric AS multiplier,
  CASE
  WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
  ELSE 1::numeric
  END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange, i.initialcurrentaccountamt, i.dateacct AS duedate, i.M_AuthorizationChain_ID, i.authorizationchainstatus
  FROM c_invoice i
  JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  WHERE i.ispayschedulevalid <> 'Y'::bpchar
UNION ALL
  SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, ips.c_invoicepayschedule_id, NULL::unknown AS chargeamt, NULL::unknown AS totallines, ips.dueamt AS grandtotal, d.signo_issotrx AS multiplier,
  CASE
  WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
  ELSE 1::numeric
  END AS multiplierap, d.docbasetype, i.notexchangeablecredit, i.isexchange, i.initialcurrentaccountamt, ips.duedate, i.M_AuthorizationChain_ID, i.authorizationchainstatus
  FROM c_invoice i
  JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE c_invoice_v
  OWNER TO libertya;

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

ALTER TABLE c_invoiceline_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW rv_bpartneropen AS
  SELECT i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_currency_id, i.grandtotal * i.multiplierap AS amt, invoiceopen(i.c_invoice_id, i.c_invoicepayschedule_id) * i.multiplierap AS openamt, i.dateinvoiced AS datedoc, COALESCE(daysbetween(now(), ips.duedate::timestamp with time zone), paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now())) AS daysdue
  FROM c_invoice_v i
  LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  WHERE i.ispaid = 'N'::bpchar AND (i.docstatus = 'CO'::bpchar OR i.docstatus = 'CL'::bpchar)
UNION
  SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_currency_id, p.payamt * p.multiplierap * (- 1::numeric) AS amt, paymentavailable(p.c_payment_id) * p.multiplierap * (- 1::numeric) AS openamt, p.datetrx AS datedoc, NULL::unknown AS daysdue
  FROM c_payment_v p
  WHERE p.isallocated = 'N'::bpchar AND p.c_bpartner_id IS NOT NULL AND (p.docstatus = 'CO'::bpchar OR p.docstatus = 'CL'::bpchar);

ALTER TABLE rv_bpartneropen
  OWNER TO libertya;

CREATE OR REPLACE VIEW v_documents AS
  ( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus,
  CASE
  WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
  ELSE i.dateinvoiced
  END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, i.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced AS truedatetrx, i.initialcurrentaccountamt, bp.socreditstatus
  FROM c_invoice_v i
  JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
  JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
  LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
  UNION ALL
  ( SELECT DISTINCT 'C_Payment'::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, bp.socreditstatus
  FROM c_payment p
  JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
  JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
  LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id
  LEFT JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
  ORDER BY 'C_Payment'::text, p.c_payment_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, COALESCE(i.initialcurrentaccountamt, 0.00), bp.socreditstatus))
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
  END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus
  FROM c_cashline cl
  JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
  LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id
  JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
  FROM c_doctype d
  WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
  LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id
  LEFT JOIN c_bpartner bp2 ON i.c_bpartner_id = bp2.c_bpartner_id;

ALTER TABLE v_documents
  OWNER TO libertya;

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

ALTER TABLE v_projectedpayments
  OWNER TO libertya;

--20170405-1555 Nueva columna banco
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
        END AS multiplierap, paymentallocated(c_payment.c_payment_id::numeric, c_payment.c_currency_id::numeric) AS allocatedamt, paymentavailable(c_payment.c_payment_id::numeric) AS availableamt, c_payment.isoverunderpayment, c_payment.isapproved, c_payment.r_pnref, c_payment.r_result, c_payment.r_respmsg, c_payment.r_authcode, c_payment.r_avsaddr, c_payment.r_avszip, c_payment.r_info, c_payment.processing, c_payment.oprocessing, c_payment.docstatus, c_payment.docaction, c_payment.isprepayment, c_payment.c_charge_id, c_payment.isreconciled, c_payment.isallocated, c_payment.isonline, c_payment.processed, c_payment.posted, c_payment.dateacct, ( SELECT ah.c_allocationhdr_id
           FROM c_allocationhdr ah
      JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     WHERE (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND al.c_payment_id = c_payment.c_payment_id
     ORDER BY ah.updated DESC
    LIMIT 1) AS c_allocationhdr_id, b.name as bank
   FROM c_payment
   INNER JOIN C_BankAccount ba on ba.c_bankaccount_id = c_payment.c_bankaccount_id
   INNER JOIN C_Bank b on b.c_bank_id = ba.c_bank_id ;

ALTER TABLE rv_payment
  OWNER TO libertya;
  
--20170406-1415 Fecha de inicio de actividades de IIBB
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_clientinfo','iibbdate','timestamp without time zone'));

--20170410-1040 Incorporación de columnas relacionadas con el tipo de documento del payment
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
  END AS multiplierap, paymentallocated(c_payment.c_payment_id::numeric, c_payment.c_currency_id::numeric) AS allocatedamt, paymentavailable(c_payment.c_payment_id::numeric) AS availableamt, c_payment.isoverunderpayment, c_payment.isapproved, c_payment.r_pnref, c_payment.r_result, c_payment.r_respmsg, c_payment.r_authcode, c_payment.r_avsaddr, c_payment.r_avszip, c_payment.r_info, c_payment.processing, c_payment.oprocessing, c_payment.docstatus, c_payment.docaction, c_payment.isprepayment, c_payment.c_charge_id, c_payment.isreconciled, c_payment.isallocated, c_payment.isonline, c_payment.processed, c_payment.posted, c_payment.dateacct, ( SELECT ah.c_allocationhdr_id
  FROM c_allocationhdr ah
  JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
  WHERE (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND al.c_payment_id = c_payment.c_payment_id
  ORDER BY ah.updated DESC
  LIMIT 1) AS c_allocationhdr_id, b.name as bank, dt.doctypekey, dt.docbasetype
  FROM c_payment
  INNER JOIN C_DocType dt on dt.c_doctype_id = c_payment.c_doctype_id
  INNER JOIN C_BankAccount ba on ba.c_bankaccount_id = c_payment.c_bankaccount_id
  INNER JOIN C_Bank b on b.c_bank_id = ba.c_bank_id ;

ALTER TABLE rv_payment
  OWNER TO libertya;

--Asignación de la vista RV_Payment Only Vendor Payments al formato de impresión del reporte de Detalle de Pagos para utilizar funciones por columna
update ad_printformat
set AD_ReportView_ID= (select AD_ReportView_ID
from AD_ReportView
where ad_componentobjectuid = 'CORE-AD_ReportView-156')
where ad_componentobjectuid = 'CORE-AD_PrintFormat-1010891';

--20170410-1145 Nuevo campo Impuesto en Letra Acepta IVA
update ad_system set dummy = (SELECT addcolumnifnotexists('c_letra_acepta_iva','c_tax_id','integer'));

--20170413-1345 Nuevo campo Total de lista de pago electrónico
update ad_system set dummy = (SELECT addcolumnifnotexists('c_banklist','banklisttotal','numeric(20,2) NOT NULL DEFAULT 0'));

--20170417-1300 Nuevas columnas para el detalle de allocations y nueva view creada para los débitos
DROP VIEW c_allocation_detail_v;

CREATE OR REPLACE VIEW c_allocation_detail_v AS
 SELECT ah.c_allocationhdr_id AS c_allocation_detail_v_id,
ah.c_allocationhdr_id,
ah.ad_client_id,
ah.ad_org_id,
ah.isactive,
ah.created,
ah.createdby,
ah.updated,
ah.updatedby,
ah.datetrx AS fecha,
i.documentno AS factura,
COALESCE(i.c_currency_id, p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS c_currency_id,
i.grandtotal AS montofactura,
  CASE
  WHEN p.documentno IS NOT NULL THEN p.documentno
  ELSE
  CASE
  WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
  ELSE NULL::character varying
  END
  END AS pagonro,
  CASE
  WHEN al.c_invoice_credit_id IS NOT NULL THEN 'N'::bpchar
  WHEN p.tendertype IS NOT NULL THEN p.tendertype
  WHEN p.tendertype IS NULL THEN 'CA'::bpchar
  ELSE NULL::bpchar
  END AS tipo,
  CASE
  WHEN cl.c_cashline_id IS NOT NULL THEN 'Y'::text
  WHEN cl.c_cashline_id IS NULL THEN 'N'::text
  ELSE NULL::text
  END AS cash,
  COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric(20,2)) AS montosaldado,
  abs(COALESCE(p.payamt, cl.amount, credit.grandtotal, 0::numeric(20,2))) AS payamt,
  al.c_allocationline_id,
  i.c_invoice_id,
  CASE
  WHEN p.documentno IS NOT NULL THEN p.documentno
  ELSE
  CASE
  WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
  WHEN al.c_cashline_id IS NOT NULL THEN cl.description
  ELSE NULL::character varying
  END
  END AS paydescription,
  CASE
  WHEN al.c_payment_id IS NOT NULL THEN pppm.name
  WHEN al.c_cashline_id IS NOT NULL THEN cppm.name
  WHEN al.c_invoice_credit_id IS NOT NULL THEN dt.name
  ELSE NULL::character varying
  END AS payment_medium_name,
  COALESCE(p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS pay_currency_id,
  p.c_bankaccount_id,
  i.numerocomprobante,
  i.puntodeventa,
  i.c_letra_comprobante_id,
  ddt.doctypekey,
  ddt.name as doctypename,
  i.paymentrule,
  i.c_region_delivery_id,
  i.netamount,
  i.c_paymentterm_id,
  i.dateinvoiced,
  al.c_invoice_credit_id,
  dt.doctypekey as credit_doctypekey,
  dt.name as credit_doctypename,
  credit.numerocomprobante as credit_numerocomprobante,
  credit.puntodeventa as credit_puntodeventa,
  credit.c_letra_comprobante_id as credit_letra_comprobante_id,
  credit.netamount as credit_netamount,
  al.c_cashline_id,
  c.name as cashname,
  al.c_payment_id,
p.accountno,
p.checkno,
  p.a_name,
  p.a_bank,
  p.a_cuit,
  p.duedate,
  p.dateemissioncheck,
  p.checkstatus,
  p.creditcardnumber,
p.couponbatchnumber,
p.couponnumber,
p.m_entidadfinancieraplan_id,
efp.m_entidadfinanciera_id,
p.posnet,
p.micr,
p.isreconciled,
CASE
  WHEN al.c_payment_id IS NOT NULL THEN p.datetrx
  WHEN al.c_cashline_id IS NOT NULL THEN c.statementdate
  WHEN al.c_invoice_credit_id IS NOT NULL THEN credit.dateinvoiced
  ELSE null::timestamp
  END AS creditdate,
  CASE
  WHEN al.c_payment_id IS NOT NULL THEN p.documentno
  WHEN al.c_cashline_id IS NOT NULL THEN '# '||cl.line
  WHEN al.c_invoice_credit_id IS NOT NULL THEN credit.documentno
  ELSE null::character varying
  END AS creditdocumentno
  FROM c_allocationhdr ah
  JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
  LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
  LEFT JOIN c_doctype ddt ON i.c_doctypetarget_id = ddt.c_doctype_id
  LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
  LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = p.m_entidadfinancieraplan_id
  LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
  LEFT JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
  LEFT JOIN c_invoice credit ON al.c_invoice_credit_id = credit.c_invoice_id
  LEFT JOIN c_doctype dt ON credit.c_doctypetarget_id = dt.c_doctype_id
  LEFT JOIN c_pospaymentmedium cppm ON cppm.c_pospaymentmedium_id = cl.c_pospaymentmedium_id
  LEFT JOIN c_pospaymentmedium pppm ON pppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
  ORDER BY ah.c_allocationhdr_id, al.c_allocationline_id;

ALTER TABLE c_allocation_detail_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_allocation_detail_debits_v AS
SELECT ah.c_allocationhdr_id AS c_allocation_detail_debits_v_id,
ah.c_allocationhdr_id,
ah.ad_client_id,
ah.ad_org_id,
ah.isactive,
ah.created,
ah.createdby,
ah.updated,
ah.updatedby,
al.c_invoice_id,
i.documentno,
i.c_currency_id,
i.numerocomprobante,
i.puntodeventa,
i.c_letra_comprobante_id,
dt.doctypekey,
dt.name as doctypename,
i.paymentrule,
i.c_region_delivery_id,
i.netamount,
i.c_paymentterm_id,
i.dateinvoiced,
i.grandtotal,
sum(al.amount) as montosaldado
FROM c_allocationhdr ah
INNER JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
INNER JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
INNER JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
GROUP BY ah.c_allocationhdr_id,
ah.ad_client_id,
ah.ad_org_id,
ah.isactive,
ah.created,
ah.createdby,
ah.updated,
ah.updatedby,
al.c_invoice_id,
i.documentno,
i.c_currency_id,
i.numerocomprobante,
i.puntodeventa,
i.c_letra_comprobante_id,
dt.doctypekey,
dt.name,
i.paymentrule,
i.c_region_delivery_id,
i.netamount,
i.c_paymentterm_id,
i.dateinvoiced,
i.grandtotal;
ALTER TABLE c_allocation_detail_debits_v
  OWNER TO libertya;

--20170419-1015 View c_allocation_detail_debits_v modificada y nueva c_allocation_detail_credits_v
DROP VIEW c_allocation_detail_debits_v;

CREATE OR REPLACE VIEW c_allocation_detail_debits_v AS
SELECT ah.c_allocationhdr_id AS c_allocation_detail_debits_v_id,
ah.c_allocationhdr_id,
ah.ad_client_id,
ah.ad_org_id,
ah.isactive,
ah.created,
ah.createdby,
ah.updated,
ah.updatedby,
al.c_invoice_id,
i.documentno,
i.c_currency_id,
i.numerocomprobante,
i.puntodeventa,
i.c_letra_comprobante_id,
dt.doctypekey,
dt.name as doctypename,
i.paymentrule,
i.c_region_delivery_id,
i.netamount,
i.c_paymentterm_id,
i.dateinvoiced,
i.grandtotal,
sum(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id)) as montosaldado
FROM c_allocationhdr ah
INNER JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
INNER JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
INNER JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
GROUP BY ah.c_allocationhdr_id,
ah.ad_client_id,
ah.ad_org_id,
ah.isactive,
ah.created,
ah.createdby,
ah.updated,
ah.updatedby,
al.c_invoice_id,
i.documentno,
i.c_currency_id,
i.numerocomprobante,
i.puntodeventa,
i.c_letra_comprobante_id,
dt.doctypekey,
dt.name,
i.paymentrule,
i.c_region_delivery_id,
i.netamount,
i.c_paymentterm_id,
i.dateinvoiced,
i.grandtotal;
ALTER TABLE c_allocation_detail_debits_v
  OWNER TO libertya;

CREATE OR REPLACE VIEW c_allocation_detail_credits_v AS
SELECT  c_allocation_detail_credits_v_id,
c_allocationhdr_id,
ad_client_id,
ad_org_id,
isactive,
created,
createdby,
updated,
updatedby,
fecha,
c_currency_id,
tipo,
cash,
payamt,
payment_medium_name,
pay_currency_id,
c_bankaccount_id,
c_invoice_credit_id,
credit_doctypekey,
credit_doctypename,
credit_numerocomprobante,
credit_puntodeventa,
credit_letra_comprobante_id,
credit_netamount,
c_cashline_id,
cashname,
c_payment_id,
accountno,
checkno,
a_name,
a_bank,
a_cuit,
duedate,
dateemissioncheck,
checkstatus,
creditcardnumber,
couponbatchnumber,
couponnumber,
m_entidadfinancieraplan_id,
m_entidadfinanciera_id,
posnet,
micr,
isreconciled,
creditdate,
creditdocumentno,
SUM(COALESCE(currencyconvert(amount + discountamt + writeoffamt, c_currency_id, pay_currency_id, NULL::timestamp with time zone, NULL::integer, ad_client_id, ad_org_id), 0::numeric(20,2))) AS montosaldado
 FROM (SELECT ah.c_allocationhdr_id AS c_allocation_detail_credits_v_id,
ah.c_allocationhdr_id,
ah.ad_client_id,
ah.ad_org_id,
ah.isactive,
ah.created,
ah.createdby,
ah.updated,
ah.updatedby,
ah.datetrx AS fecha,
ah.c_currency_id,
  CASE
  WHEN al.c_invoice_credit_id IS NOT NULL THEN 'N'::bpchar
  WHEN p.tendertype IS NOT NULL THEN p.tendertype
  WHEN p.tendertype IS NULL THEN 'CA'::bpchar
  ELSE NULL::bpchar
  END AS tipo,
  CASE
  WHEN cl.c_cashline_id IS NOT NULL THEN 'Y'::text
  WHEN cl.c_cashline_id IS NULL THEN 'N'::text
  ELSE NULL::text
  END AS cash,
  abs(COALESCE(p.payamt, cl.amount, credit.grandtotal, 0::numeric(20,2))) AS payamt,
  CASE
  WHEN al.c_payment_id IS NOT NULL THEN pppm.name
  WHEN al.c_cashline_id IS NOT NULL THEN cppm.name
  WHEN al.c_invoice_credit_id IS NOT NULL THEN dt.name
  ELSE NULL::character varying
  END AS payment_medium_name,
  COALESCE(p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS pay_currency_id,
  p.c_bankaccount_id,
  al.c_invoice_credit_id,
  dt.doctypekey as credit_doctypekey,
  dt.name as credit_doctypename,
  credit.numerocomprobante as credit_numerocomprobante,
  credit.puntodeventa as credit_puntodeventa,
  credit.c_letra_comprobante_id as credit_letra_comprobante_id,
  credit.netamount as credit_netamount,
  al.c_cashline_id,
  c.name as cashname,
  al.c_payment_id,
  p.accountno,
  p.checkno,
  p.a_name,
  p.a_bank,
  p.a_cuit,
  p.duedate,
  p.dateemissioncheck,
  p.checkstatus,
  p.creditcardnumber,
  p.couponbatchnumber,
  p.couponnumber,
  p.m_entidadfinancieraplan_id,
  efp.m_entidadfinanciera_id,
  p.posnet,
  p.micr,
  p.isreconciled,
CASE
  WHEN al.c_payment_id IS NOT NULL THEN p.datetrx
  WHEN al.c_cashline_id IS NOT NULL THEN c.statementdate
  WHEN al.c_invoice_credit_id IS NOT NULL THEN credit.dateinvoiced
  ELSE null::timestamp
  END AS creditdate,
  CASE
  WHEN al.c_payment_id IS NOT NULL THEN p.documentno
  WHEN al.c_cashline_id IS NOT NULL THEN '# '||cl.line
  WHEN al.c_invoice_credit_id IS NOT NULL THEN credit.documentno
  ELSE null::character varying
  END AS creditdocumentno,
  al.amount,
  al.discountamt, 
  al.writeoffamt
  FROM c_allocationhdr ah
  JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
  LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
  LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = p.m_entidadfinancieraplan_id
  LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
  LEFT JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
  LEFT JOIN c_invoice credit ON al.c_invoice_credit_id = credit.c_invoice_id
  LEFT JOIN c_doctype dt ON credit.c_doctypetarget_id = dt.c_doctype_id
  LEFT JOIN c_pospaymentmedium cppm ON cppm.c_pospaymentmedium_id = cl.c_pospaymentmedium_id
  LEFT JOIN c_pospaymentmedium pppm ON pppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id) AS c
  GROUP BY c_allocation_detail_credits_v_id,
c_allocationhdr_id,
ad_client_id,
ad_org_id,
isactive,
created,
createdby,
updated,
updatedby,
fecha,
c_currency_id,
tipo,
cash,
payamt,
payment_medium_name,
pay_currency_id,
c_bankaccount_id,
c_invoice_credit_id,
credit_doctypekey,
credit_doctypename,
credit_numerocomprobante,
credit_puntodeventa,
credit_letra_comprobante_id,
credit_netamount,
c_cashline_id,
cashname,
c_payment_id,
accountno,
checkno,
a_name,
a_bank,
a_cuit,
duedate,
dateemissioncheck,
checkstatus,
creditcardnumber,
couponbatchnumber,
couponnumber,
m_entidadfinancieraplan_id,
m_entidadfinanciera_id,
posnet,
micr,
isreconciled,
creditdate,
creditdocumentno
ORDER BY c_allocationhdr_id, c_payment_id, c_cashline_id, c_invoice_credit_id;

ALTER TABLE c_allocation_detail_credits_v
  OWNER TO libertya;

--20170420-1400 Nueva columna en lista de banco para registrar el total de pagos electrónicos de la OP relacionada
update ad_system set dummy = (SELECT addcolumnifnotexists('c_banklistline','electronicpaymenttotal','numeric(20,2) NOT NULL DEFAULT 0'));

--20170426-1200 Funciones para actualizar las cantidades pendientes en storage
CREATE OR REPLACE FUNCTION update_reserved(
    clientid integer,
    orgid integer,
    productid integer)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad reservada de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades reservadas se obtienen de pedidos procesados.
*/
BEGIN
	update m_storage s
	set qtyreserved = coalesce((select sum(ol.qtyordered - ol.qtydelivered) as qtypending
					from c_orderline ol
					inner join c_order o on o.c_order_id = ol.c_order_id
					inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
					inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
					where ol.qtyordered <> ol.qtydelivered 
						and o.processed = 'Y' 
						and s.m_product_id = ol.m_product_id
						and s.m_locator_id = l.m_locator_id
						and o.issotrx = 'Y'),0)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid)
		and s.m_locator_id IN (select defaultLocator 
					from (select m_warehouse_id, max(m_locator_id) as defaultLocator
						from m_locator l
						where l.isdefault = 'Y' and l.isactive = 'Y'
						GROUP by m_warehouse_id) as dl);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_reserved(integer, integer, integer)
  OWNER TO libertya;


CREATE OR REPLACE FUNCTION update_ordered(
    clientid integer,
    orgid integer,
    productid integer)
  RETURNS void AS
$BODY$
/***********
Actualiza la cantidad pendiente pedida de los depósitos de la compañía, organización y artículo parametro, 
siempre y cuando existan los regitros en m_storage 
y sólo sobre locators marcados como default ya que asi se realiza al procesar pedidos.
Las cantidades pendientes se obtienen de pedidos procesados.
*/
BEGIN
	update m_storage s
	set qtyordered = coalesce((select sum(ol.qtyordered - ol.qtydelivered) as qtypending
					from c_orderline ol
					inner join c_order o on o.c_order_id = ol.c_order_id
					inner join m_warehouse w on w.m_warehouse_id = o.m_warehouse_id
					inner join m_locator l on l.m_warehouse_id = w.m_warehouse_id
					where ol.qtyordered <> ol.qtydelivered 
						and o.processed = 'Y' 
						and s.m_product_id = ol.m_product_id
						and s.m_locator_id = l.m_locator_id
						and o.issotrx = 'N'),0)
	where ad_client_id = clientid
		and (orgid = 0 or ad_org_id = orgid)
		and (productid = 0 or m_product_id = productid)
		and s.m_locator_id IN (select defaultLocator 
					from (select m_warehouse_id, max(m_locator_id) as defaultLocator
						from m_locator l
						where l.isdefault = 'Y' and l.isactive = 'Y'
						GROUP by m_warehouse_id) as dl);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_ordered(integer, integer, integer)
  OWNER TO libertya;

--20170426-1310 Nueva columna en impresión de cheques para registrar la fecha
update ad_system set dummy = (SELECT addcolumnifnotexists('C_CheckPrinting','datetrx','timestamp without time zone'));

update C_CheckPrinting
set datetrx = created;

ALTER TABLE C_CheckPrinting ALTER COLUMN datetrx SET NOT NULL;

--20170427-1015 Nueva columna para validar CAI Obligatorio
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','ismandatorycai','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20170502-1400 Nueva columna para permitir lotes fuera de fecha, caso contrario se setea automáticamente la fecha actual 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','allowotherbatchpaymentdate','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20170508-1035 Nuevas columnas para registrar changelog sobre las tablas C_ExternalService y C_ExternalServiceAttributes
update ad_system set dummy = (SELECT addcolumnifnotexists('C_ExternalService','ad_componentversion_id','integer'));

update ad_system set dummy = (SELECT addcolumnifnotexists('C_ExternalServiceAttributes','ad_componentversion_id','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_ExternalServiceAttributes','ad_componentobjectuid','character varying(100)'));

-- Nueva columna ID de Tipo de Documento
DROP VIEW c_allocation_detail_debits_v;

CREATE OR REPLACE VIEW c_allocation_detail_debits_v AS
 SELECT ah.c_allocationhdr_id AS c_allocation_detail_debits_v_id, ah.c_allocationhdr_id, ah.ad_client_id, ah.ad_org_id, ah.isactive, ah.created, ah.createdby, ah.updated, ah.updatedby, al.c_invoice_id, i.documentno, i.c_currency_id, i.numerocomprobante, i.puntodeventa, i.c_letra_comprobante_id, dt.c_doctype_id, dt.doctypekey, dt.name AS doctypename, i.paymentrule, i.c_region_delivery_id, i.netamount, i.c_paymentterm_id, i.dateinvoiced, i.grandtotal, sum(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id)) AS montosaldado
  FROM c_allocationhdr ah
  JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
  JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
  JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
  GROUP BY ah.c_allocationhdr_id, ah.ad_client_id, ah.ad_org_id, ah.isactive, ah.created, ah.createdby, ah.updated, ah.updatedby, al.c_invoice_id, i.documentno, i.c_currency_id, i.numerocomprobante, i.puntodeventa, i.c_letra_comprobante_id, dt.c_doctype_id, dt.doctypekey, dt.name, i.paymentrule, i.c_region_delivery_id, i.netamount, i.c_paymentterm_id, i.dateinvoiced, i.grandtotal;

ALTER TABLE c_allocation_detail_debits_v
  OWNER TO libertya;

-- 20170511-1526 Ampliacion de los decimales en tasas de conversion para mayor precision  
alter table c_conversion_rate alter column multiplyrate type numeric(24,15);
alter table c_conversion_rate alter column dividerate type numeric(24,15);

--20170516-1151 Bloqueos de Secuencias de Tipo de Documento
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','lockseq','character(1) NOT NULL DEFAULT ''N''::bpchar'));

CREATE TABLE ad_sequence_lock
(
  ad_sequence_lock_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  ad_sequence_id integer NOT NULL,
  c_doctype_id integer,
  ad_table_id integer,
  record_id numeric(18,0),
  description character varying(255),
  CONSTRAINT ad_sequence_lock_key PRIMARY KEY (ad_sequence_lock_id),
  CONSTRAINT ad_sequence_lock_seq FOREIGN KEY (ad_sequence_id)
  REFERENCES ad_sequence (ad_sequence_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_sequence_lock_table FOREIGN KEY (ad_table_id)
  REFERENCES ad_table (ad_table_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_sequence_lock_doctype FOREIGN KEY (c_doctype_id)
  REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_sequence_lock_client FOREIGN KEY (ad_client_id)
  REFERENCES ad_client (ad_client_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_sequence_lock_org FOREIGN KEY (ad_org_id)
  REFERENCES ad_org (ad_org_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE ad_sequence_lock
  OWNER TO libertya;
  
--20170517-1255 Merge de ajustes a ventana cupones de tarjetas
DROP VIEW c_paymentcoupon_v;

CREATE 
OR REPLACE VIEW libertya.c_paymentcoupon_v AS 
SELECT
   p.c_payment_id,
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
   coalesce(p.a_name, bp.name) as a_name,
   p.datetrx,
   p.couponbatchnumber,
   p.payamt,
   p.c_currency_id,
   p.docstatus,
   cs.isreconciled,
   efp.cuotaspago AS totalallocations,
   ccs.paymentdate AS settlementdate,
   p.auditstatus,
   ''::character(1) AS reject 
FROM
   c_payment p 
   INNER JOIN
	c_bpartner bp 
	ON p.c_bpartner_id = bp.c_bpartner_id
   LEFT JOIN
      m_entidadfinancieraplan efp 
      ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id 
   LEFT JOIN
      c_couponssettlements cs 
      ON p.c_payment_id = cs.c_payment_id 
   LEFT JOIN
      c_creditcardsettlement ccs 
      ON cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id 
WHERE
   p.tendertype = 'C'::bpchar;
   
--20170518-1120 Merge de Revisión 1841
DROP VIEW c_paymentcoupon_v;

CREATE 
OR REPLACE VIEW libertya.c_paymentcoupon_v AS 
SELECT
   p.c_payment_id,
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
   coalesce(p.a_name, bp.name) as a_name,
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
   ''::character(1) AS unreject
FROM
   c_payment p 
   INNER JOIN
	c_bpartner bp 
	ON p.c_bpartner_id = bp.c_bpartner_id
   LEFT JOIN
      m_entidadfinancieraplan efp 
      ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id 
   LEFT JOIN
      c_couponssettlements cs 
      ON p.c_payment_id = cs.c_payment_id 
   LEFT JOIN
      c_creditcardsettlement ccs 
      ON cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id 
WHERE
   p.tendertype = 'C'::bpchar;

--20170518-1250 Merge de revision 1951
update ad_system set dummy = (SELECT addcolumnifnotexists('c_couponssettlements','a_name','character varying(60)'));


--20170519-1122 Nuevas tablas a ser bitacoreadas
-- Configuración de medios de cobro
update ad_system set dummy = (SELECT addcolumnifnotexists('C_POSPaymentMedium','AD_ComponentObjectUID','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_POSPaymentMedium','AD_ComponentVersion_ID','integer'));
-- Lista de Precio
update ad_system set dummy = (SELECT addcolumnifnotexists('M_PriceList','AD_ComponentObjectUID','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_PriceList','AD_ComponentVersion_ID','integer'));
-- Version de Lista de Precio
update ad_system set dummy = (SELECT addcolumnifnotexists('M_PriceList_Version','AD_ComponentObjectUID','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_PriceList_Version','AD_ComponentVersion_ID','integer'));
-- Configuracion de Descuentos
update ad_system set dummy = (SELECT addcolumnifnotexists('M_DiscountConfig','AD_ComponentObjectUID','varchar(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_DiscountConfig','AD_ComponentVersion_ID','integer'));

--20170519-1145 Merge de revision 1953
update ad_system set dummy = (SELECT addcolumnifnotexists('c_couponssettlements','processed','character(1) NOT NULL DEFAULT ''N'''));

--20170519-1424 Merge de Ajustes en Ventana de Cupones
DROP VIEW libertya.c_paymentcoupon_v;

CREATE OR REPLACE VIEW libertya.c_paymentcoupon_v AS
SELECT
   p.c_payment_id,
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
   coalesce(p.a_name, bp.name) as a_name,
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
   ef.c_bpartner_id AS m_entidadfinanciera_bp_id 
FROM
   c_payment p 
   INNER JOIN
	c_bpartner bp 
	ON p.c_bpartner_id = bp.c_bpartner_id
   LEFT JOIN
      m_entidadfinancieraplan efp 
      ON p.m_entidadfinancieraplan_id = efp.m_entidadfinancieraplan_id 
   INNER JOIN 
      m_entidadfinanciera ef
      ON efp.m_entidadfinanciera_id = ef.m_entidadfinanciera_id
   LEFT JOIN
      c_couponssettlements cs 
      ON p.c_payment_id = cs.c_payment_id 
   LEFT JOIN
      c_creditcardsettlement ccs 
      ON cs.c_creditcardsettlement_id = ccs.c_creditcardsettlement_id 
WHERE
   p.tendertype = 'C'::bpchar;
   
--20170519-1600 Merge de Revisiones 1916 y 1929
update ad_system set dummy = (SELECT addcolumnifnotexists('c_withholdingsettlement','c_retencionschema_id','integer'));

ALTER TABLE c_withholdingsettlement DROP COLUMN c_retenciontype_id;

--20170519-1650 Merge de Revisión 1847
ALTER TABLE libertya.c_creditcardsettlement ADD CONSTRAINT uniquecreditcardsettlement UNIQUE (settlementno, c_bpartner_id);

--20170522-2000 Incorporación de rango de numeración de cheques en chequeras
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bankaccountdoc','startno','numeric(18,0)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bankaccountdoc','endno','numeric(18,0)'));

--20170523-1115 Chequeras por usuario
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bankaccountdoc','isuserassigned','character(1) NOT NULL DEFAULT ''N''::bpchar'));

CREATE TABLE c_bankaccountdocuser
(
  c_bankaccountdocuser_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_bankaccountdoc_id integer NOT NULL,
  ad_user_id integer NOT NULL,
  CONSTRAINT c_bankaccountdocuser_pkey PRIMARY KEY (c_bankaccountdocuser_id),
  CONSTRAINT c_bankaccountdocuser_c_bankaccountdoc FOREIGN KEY (c_bankaccountdoc_id)
      REFERENCES c_bankaccountdoc (c_bankaccountdoc_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT c_bankaccountdocuser_ad_user FOREIGN KEY (ad_user_id)
      REFERENCES ad_user (ad_user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_bankaccountdocuser
  OWNER TO libertya;
  
 --20170524-1148 Versionado de BBDD para release
 UPDATE ad_system SET version = '23-05-2017' WHERE ad_system_id = 0;
 