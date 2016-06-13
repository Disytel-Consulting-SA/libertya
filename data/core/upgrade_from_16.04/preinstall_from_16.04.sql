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
