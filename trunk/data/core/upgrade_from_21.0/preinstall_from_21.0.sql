-- ========================================================================================
-- PREINSTALL FROM 21.0
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20210118-1435 Mejoras a la vista del libro mayor para que tenga prioridad la descripción del asiento contable antes de la descripción creada en ejecución
CREATE OR REPLACE VIEW v_diariomayor AS 
 SELECT fa.ad_client_id,
    fa.ad_org_id,
    fa.created,
    fa.createdby,
    fa.updated,
    fa.updatedby,
    ev.c_elementvalue_id,
    fa.dateacct,
    fa.journalno,
    ev.value,
    (ev.value::text || ' '::text) || ev.name::text AS name,
        CASE
	    WHEN fa.description is not null then fa.description
            WHEN t.name::text = 'Invoice'::text AND di.issotrx = 'N'::bpchar THEN ((('Referencia: '::text || di.poreference::text) || ' / '::text) || di.documentno::text)::character varying
            WHEN t.name::text = 'Invoice'::text AND di.issotrx = 'Y'::bpchar THEN ('Factura: '::text || di.documentno::text)::character varying
            WHEN t.name::text = 'Allocation'::text THEN ((dah.documentno::text || ' | '::text) || fa.description::text)::character varying
            WHEN t.name::text = 'GL Journal'::text THEN ((dglb.documentno::text || ' | '::text) || dglb.description::text)::character varying
            WHEN t.name::text = 'Payment'::text THEN ((dp.documentno::text || ' | '::text) || dp.description::text)::character varying
            ELSE ev.name
        END AS description,
    fa.amtacctdr AS debe,
    fa.amtacctcr AS haber,
    fa.amtacctdr - fa.amtacctcr AS saldo,
    t.name AS tablename,
    ttrl.name AS tr_tablename,
    fa.record_id,
        CASE
            WHEN t.name::text = 'Invoice'::text THEN di.documentno
            WHEN t.name::text = 'Allocation'::text THEN dah.documentno
            WHEN t.name::text = 'GL Journal'::text THEN dglb.documentno
            WHEN t.name::text = 'Payment'::text THEN dp.documentno
            ELSE NULL::character varying
        END AS documentno,
        CASE
            WHEN t.name::text = 'Invoice'::text THEN di.c_bpartner_id
            WHEN t.name::text = 'Payment'::text THEN dp.c_bpartner_id
            WHEN t.name::text = 'Allocation'::text THEN ( SELECT min(al.c_bpartner_id) AS min
               FROM c_allocationline al
              WHERE al.c_allocationhdr_id = dah.c_allocationhdr_id)
            ELSE NULL::integer
        END AS c_bpartner_id,
        CASE
            WHEN t.name::text = 'Invoice'::text THEN di.dateinvoiced
            WHEN t.name::text = 'Allocation'::text THEN dah.datetrx
            WHEN t.name::text = 'GL Journal'::text THEN dglb.datedoc
            WHEN t.name::text = 'Payment'::text THEN dp.datetrx
            ELSE NULL::timestamp without time zone
        END AS datedoc
   FROM c_elementvalue ev,
    ad_table t,
    fact_acct fa
     JOIN ad_table_trl ttrl ON fa.ad_table_id = ttrl.ad_table_id AND ttrl.ad_language::text = 'es_ES'::text
     LEFT JOIN c_invoice di ON fa.record_id = di.c_invoice_id::numeric
     LEFT JOIN c_allocationhdr dah ON fa.record_id = dah.c_allocationhdr_id::numeric
     LEFT JOIN c_payment dp ON fa.record_id = dp.c_payment_id::numeric
     LEFT JOIN gl_journal dgl ON fa.record_id = dgl.gl_journal_id::numeric
     LEFT JOIN gl_journalbatch dglb ON dgl.gl_journalbatch_id = dglb.gl_journalbatch_id
  WHERE fa.account_id = ev.c_elementvalue_id AND t.ad_table_id = fa.ad_table_id
  ORDER BY ev.name, fa.dateacct;

ALTER TABLE v_diariomayor
  OWNER TO libertya;

--20210217-1241 Cambio de lookups en vistas C_POSJournalInvoices_V C_POSJournalPayments_V y C_POSJournalPayments_V_Simple de TableDir a Busqueda para reduccion en tiempos de respuesta al abrir la ventana
update ad_column set ad_reference_id = 30 where ad_componentobjectuid in ('CORE-AD_Column-1014931', 'CORE-AD_Column-1014922', 'CORE-AD_Column-1014921', 'CORE-AD_Column-1014939', 'CORE-AD_Column-1014932', 'CORE-AD_Column-1014930', 'CORE-AD_Column-1016938', 'CORE-AD_Column-1016929', 'CORE-AD_Column-1016928', 'CORE-AD_Column-1016946', 'CORE-AD_Column-1016939',	'CORE-AD_Column-1016937', 'CORE-AD_Column-1014899', 'CORE-AD_Column-1014911', 'CORE-AD_Column-1014900', 'CORE-AD_Column-1014902');

--20210414-1700 Se invierte el signo en los movimientos bancarios
DROP VIEW c_payment_movements_v;

CREATE OR REPLACE VIEW c_payment_movements_v AS 
 SELECT p.c_payment_id,
    p.ad_client_id,
    p.ad_org_id,
    p.isactive,
    p.created,
    p.createdby,
    p.updated,
    p.updatedby,
    p.documentno,
    p.datetrx,
    p.isreceipt,
    p.c_doctype_id,
    p.trxtype,
    p.c_bankaccount_id,
    p.c_bpartner_id,
    p.c_invoice_id,
    p.c_bp_bankaccount_id,
    p.c_paymentbatch_id,
    p.tendertype,
    p.creditcardtype,
    p.creditcardnumber,
    p.creditcardvv,
    p.creditcardexpmm,
    p.creditcardexpyy,
    p.micr,
    p.routingno,
    p.accountno,
    p.checkno,
    p.a_name,
    p.a_street,
    p.a_city,
    p.a_state,
    p.a_zip,
    p.a_ident_dl,
    p.a_ident_ssn,
    p.a_email,
    p.voiceauthcode,
    p.orig_trxid,
    p.ponum,
    p.c_currency_id,
    p.payamt,
    p.discountamt,
    p.writeoffamt,
    p.taxamt,
    p.isapproved,
    p.r_pnref,
    p.r_result,
    p.r_respmsg,
    p.r_authcode,
    p.r_avsaddr,
    p.r_avszip,
    p.r_info,
    p.processing,
    p.oprocessing,
    p.docstatus,
    p.docaction,
    p.isreconciled,
    p.isallocated,
    p.isonline,
    p.processed,
    p.posted,
    p.isoverunderpayment,
    p.overunderamt,
    p.a_country,
    p.c_project_id,
    p.isselfservice,
    p.chargeamt,
    p.c_charge_id,
    p.isdelayedcapture,
    p.r_authcode_dc,
    p.r_cvv2match,
    p.r_pnref_dc,
    p.swipe,
    p.ad_orgtrx_id,
    p.c_campaign_id,
    p.c_activity_id,
    p.user1_id,
    p.user2_id,
    p.c_conversiontype_id,
    p.description,
    p.dateacct,
    p.c_order_id,
    p.isprepayment,
    p.ref_payment_id,
    p.checked,
    p.m_boletadeposito_id,
    p.issotrx,
    p.a_bank,
    p.a_cuit,
    p.duedate,
    p.couponnumber,
    p.m_entidadfinancieraplan_id,
    p.authcode,
    p.authmatch,
    p.c_posjournal_id,
    p.posnet,
    p.c_pospaymentmedium_id,
    p.c_bank_id,
    p.original_ref_payment_id,
    p.dateemissioncheck,
    p.couponbatchnumber,
    p.checkstatus,
    p.rejecteddate,
    p.rejectedcomments,
    p.bank_payment_documentno,
    p.bank_payment_date,
    p.c_bankpaymentstatus_id,
    p.auditstatus,
    p.ismanual,
    p.accounting_c_charge_id,
	CASE
            WHEN dt.signo_issotrx = 1 THEN p.payamt * -1
            ELSE p.payamt
        END AS payamtsign
   FROM c_payment p
     JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id;

ALTER TABLE c_payment_movements_v
  OWNER TO libertya;
  
--20210513-1325 Las retenciones sufridas no van negativas
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
    i.grandtotal AS importeretenido,
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
  
--20210622-0943 Ampliar capacidad de log del scheduler
alter table ad_schedulerlog  alter column summary type varchar(10000);

--20210623-1415 Nueva columna para registrar el código de categoría de iva para impresoras fiscales 
update ad_system set dummy = (SELECT addcolumnifnotexists('c_categoria_iva','fiscalprintercodigo','integer'));

update c_categoria_iva
set fiscalprintercodigo = codigo;

update c_categoria_iva
set fiscalprintercodigo = 2
where codigo = 5;

--20210729-0857 Nuevos campos para definición manual de comprobante original
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','OrigInvTipo','varchar(10)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','OrigInvPtoVta','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','OrigInvNro','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','OrigInvFecha','date'));

--20210921-1900 Nueva columna para relacionar un artículo en proveedor para creación de comprobantes automáticos
-- Merge de Micro TEHLBY2 versión 4.0
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','m_product_related_id','integer'));
ALTER TABLE c_bpartner ADD CONSTRAINT productrelated_cbpartner 
	FOREIGN KEY (m_product_related_id) 
	REFERENCES m_product (m_product_id) 
	MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
	
--20211005-1430 Botón para Perfil Sólo Lectura
-- Merge de Micro RORO versión 1.0
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_role','applyreadonly','character(1)'));

--20211220 Merge de Micro IBTUCUMAN 1.0
--Retención/Percepción IIBB Tucumán. Devolución de Percepciones
alter table c_bpartner alter column isconveniomultilateral drop not null;
alter table c_bpartner alter column isconveniomultilateral drop default;

update c_bpartner
set isconveniomultilateral = 'G'
where isconveniomultilateral = 'Y';

update c_bpartner
set isconveniomultilateral = null
where isconveniomultilateral = 'N';

update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','c_region_sede_id','integer'));

update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner_percepcion','alicuota','numeric(9,2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner_retencion','alicuota','numeric(9,2)'));

update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner_padron_bsas','coeficiente','numeric(8,4)'));

update ad_system set dummy = (SELECT addcolumnifnotexists('ad_org_percepcion','partialreturn','character(1) NOT NULL DEFAULT ''Y''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_org_percepcion','totalreturn','character(1) NOT NULL DEFAULT ''Y''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_org_percepcion','minimumpercepcionamt','numeric(20,2) NOT NULL DEFAULT 0'));

update ad_system set dummy = (SELECT addcolumnifnotexists('ad_clientinfo','conveniomultilateral','character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_clientinfo','c_region_sede_id','integer'));

--20211220 Merge de Micro IBTUCUMAN 2.0
--Retención/Percepción IIBB Tucumán. Devolución de Percepciones
CREATE OR REPLACE VIEW c_retenciones_tucuman_contribuyentes_v AS
select ic.ad_client_id, ic.ad_org_id, ic.dateinvoiced as "date", bp.c_bpartner_id, bp.name, bp.taxidtype, bp.taxid,
	l.address1, l.city, coalesce(bpr.name, l.regionname) as region, l.postal 
from c_retencionschema rs
join c_region r on r.c_region_id = rs.c_region_id
join m_retencion_invoice ri on ri.c_retencionschema_id = rs.c_retencionschema_id
join c_invoice ic on ic.c_invoice_id = ri.c_invoice_id
join c_bpartner bp on bp.c_bpartner_id = ic.c_bpartner_id
join c_bpartner_location bpl on bpl.c_bpartner_location_id = ic.c_bpartner_location_id
join c_location l on l.c_location_id = bpl.c_location_id
left join c_region bpr on bpr.c_region_id = l.c_region_id
where r.jurisdictioncode = '924' and rs.retencionapplication = 'E' and ic.docstatus in ('CO','CL');

ALTER TABLE c_retenciones_tucuman_contribuyentes_v
  OWNER TO libertya;
  
CREATE OR REPLACE VIEW c_retenciones_tucuman_v AS 
 SELECT r.ad_client_id,
    r.ad_org_id,
    r.c_invoice_id,
    r.dateinvoiced AS date,
    r.taxidtype,
    r.taxid,
    '99'::character varying AS tipodecomprobante,
    ''::character(1) as letra,
    1 as puntodeventa,
    to_number(regexp_replace(documentno, '[^0-9]+', '', 'g'),'99999999')::integer as numerocomprobante,
    r.documentno,
    r.baseimponible_amt::numeric(15,2) as baseimponible_amt,
    r.retencion_percent::numeric(6,3) as retencion_percent,
    r.amt_retenc::numeric(15,2) as amt_retenc
   FROM ( SELECT bp.c_bpartner_id,
            bp.value,
            bp.name,
            bp.taxidtype,
            bp.taxid,
            ic.ad_client_id,
            ic.ad_org_id,
            ri.c_retencionschema_id,
            ic.c_invoice_id,
            ri.c_allocationhdr_id,
            ri.amt_retenc,
            ri.retencion_percent,
            ri.baseimponible_amt,
            ic.dateinvoiced,
            ah.documentno
            FROM c_retencionschema rs
             JOIN c_region r_1 ON r_1.c_region_id = rs.c_region_id
             JOIN m_retencion_invoice ri ON ri.c_retencionschema_id = rs.c_retencionschema_id
             JOIN c_allocationhdr ah on ah.c_allocationhdr_id = ri.c_allocationhdr_id
             JOIN c_invoice ic ON ic.c_invoice_id = ri.c_invoice_id
             join c_bpartner bp on bp.c_bpartner_id = ic.c_bpartner_id
          WHERE r_1.jurisdictioncode = 924 AND rs.retencionapplication::text = 'E'::text AND (ic.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) r;

ALTER TABLE c_retenciones_tucuman_v
  OWNER TO libertya;


--20211220 Merge de micro JACLBY 1.0
--Pasaje del micro JACLBY3.13 a JACLBY
update ad_system set dummy = (SELECT addcolumnifnotexists('m_product','sales_order_min','numeric(22,4) DEFAULT 0'));
update ad_system set dummy = (SELECT addcolumnifnotexists('m_product','sales_order_pack','numeric(22,4) DEFAULT 0'));

--Pasaje de micro IOREEXOR a JACLBY
update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','inout_allow_greater_qtyordered','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--Gestión de CAI
CREATE TABLE c_cai
(
  c_cai_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  cai character varying(14) NOT NULL,
  validfrom timestamp without time zone NOT NULL,
  datecai timestamp without time zone NOT NULL,
  CONSTRAINT c_cai_key PRIMARY KEY (c_cai_id)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_cai
  OWNER TO libertya;

CREATE TABLE c_cai_doctype
(
  c_cai_doctype_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_cai_id integer NOT NULL,
  c_doctype_id integer NOT NULL,
  CONSTRAINT c_cai_doctype_key PRIMARY KEY (c_cai_doctype_id),
  CONSTRAINT cai_doctype_cai FOREIGN KEY (c_cai_id)
      REFERENCES c_cai (c_cai_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT cai_doctype_doctype FOREIGN KEY (c_doctype_id)
      REFERENCES c_doctype (c_doctype_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_cai_doctype
  OWNER TO libertya;

update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','caicontrol','character(1) NOT NULL DEFAULT ''N''::bpchar'));

update ad_system set dummy = (SELECT addcolumnifnotexists('m_inout','cai','character varying(14)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('m_inout','datecai','timestamp without time zone'));

--Nueva columna para registrar el descuento manual general aplicado desde el campo de descuento de la cabecera de la factura
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoiceline','manualgeneraldiscountamt','numeric(20,2)'));

--Modifico nombres variable y parámetro de la función para que no de error
DROP FUNCTION libertya.getdiscount(integer,integer,timestamp without time zone,timestamp without time zone,timestamp without time zone,numeric);

CREATE OR REPLACE FUNCTION libertya.getdiscount(v_c_invoice_id integer, v_c_payschedule_id integer, invoicedate timestamp without time zone, duedate timestamp without time zone, comparedate timestamp without time zone, baseamt numeric) RETURNS numeric AS
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
	IF (v_C_PaySchedule_ID > 0) 
	THEN 
	    SELECT *
	      INTO fromPaySchedule
	    FROM C_PaySchedule vp
	    WHERE  vp.C_PaySchedule_ID=v_C_PaySchedule_ID;

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
		SELECT pt.discountApplicationType as dat, pt.discountApplicationType2 as dat2, 
			pt.Discount as d, pt.Discount2 as d2, pt.DiscountDays as dd, pt.DiscountDays2 as dd2, 
			pt.GraceDays as gd, pt.GraceDays2 as gd2
		INTO fromPaymentTerm
		FROM C_Invoice i
		INNER JOIN C_Paymentterm pt ON pt.C_Paymentterm_ID = i.C_Paymentterm_ID
			WHERE  i.C_Invoice_ID=v_c_invoice_id;

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
LANGUAGE plpgsql VOLATILE;
 ALTER FUNCTION libertya.getdiscount(integer, integer, timestamp without time zone, timestamp without time zone, timestamp without time zone, numeric)
  OWNER TO libertya;
  
--Nueva columna para registrar el importe pendiente de cada transacción
ALTER TABLE t_cuentacorriente ADD COLUMN openamt numeric(22,2) default 0;

--Nueva columna para registrar la secuencia única
alter table c_doctype add column docnosequence_unique_id integer;

--Bug fixes a la view de comprobantes con percepciones
DROP VIEW c_invoice_percepciones_v;

CREATE OR REPLACE VIEW c_invoice_percepciones_v AS 
 SELECT i.ad_client_id,
    i.ad_org_id,
    dt.c_doctype_id,
    dt.name AS doctypename,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 'F'::text
            ELSE 'C'::text
        END AS doctypechar,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'F'::text
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'NC'::text
            ELSE 'ND'::text
        END AS doctypenameshort,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'T'::character(1)
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'R'::character(1)
            ELSE 'D'::character(1)
        END AS doctypenameshort_aditional,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 1
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 102
            ELSE 2
        END AS tipo_de_documento_reg_neuquen,
    dt.docbasetype,
    i.c_invoice_id,
    i.documentno,
    date_trunc('day'::text, i.dateinvoiced) AS dateinvoiced,
    date_trunc('day'::text, i.dateacct) AS dateacct,
    date_trunc('day'::text, i.dateinvoiced) AS date,
    lc.letra,
    i.puntodeventa,
    i.numerocomprobante,
    i.grandtotal,
    bp.c_bpartner_id,
    bp.value AS bpartner_value,
    bp.name AS bpartner_name,
    replace(bp.taxid::text, '-'::text, ''::text) AS taxid,
    bp.iibb,
        CASE
            WHEN length(bp.iibb::text) > 7 THEN 1
            ELSE 0
        END AS tipo_contribuyente,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script,
    COALESCE(i.nombrecli, bp.name) AS nombrecli,
    COALESCE(i.nroidentificcliente, bp.taxid) AS nroidentificcliente,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS nroidentificcliente_with_script,
    ( SELECT l.address1
           FROM c_bpartner_location bpl
             JOIN c_location l ON l.c_location_id = bpl.c_location_id
          WHERE bpl.c_bpartner_id = bp.c_bpartner_id
          ORDER BY bpl.updated DESC
         LIMIT 1) AS address1,
    t.c_tax_id,
    t.name AS percepcionname,
    it.taxbaseamt,
    it.taxamt,
    (it.taxbaseamt * dt.signo_issotrx::numeric)::numeric(20,2) AS taxbaseamt_with_sign,
    (it.taxamt * dt.signo_issotrx::numeric)::numeric(20,2) AS taxamt_with_sign,
        CASE
            WHEN it.taxbaseamt <> 0::numeric THEN it.taxamt * 100::numeric / it.taxbaseamt
            ELSE 0::numeric
        END::numeric(20,2) AS alicuota,
    lo.city AS org_city,
    lo.postal AS org_postal_code,
    r.jurisdictioncode,
    translate(i.documentno::text, lc.letra::text, ''::text)::character varying(30) AS documentno_without_letter,
        CASE
            WHEN i.issotrx = 'Y'::bpchar THEN 'E'::text
            ELSE 'S'::text
        END AS aplicacion,
        cu.iso_code,
        i.issotrx,
        CASE
            WHEN it.taxbaseamt <> 0::numeric THEN it.taxamt * 100::numeric / it.taxbaseamt
            ELSE 0::numeric
        END::numeric(20,2) AS rate
   FROM c_invoicetax it
     JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id
     JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
     JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
     JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
     JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
     JOIN c_tax t ON t.c_tax_id = it.c_tax_id
     JOIN ad_orginfo oi ON oi.ad_org_id = i.ad_org_id
     LEFT JOIN c_location lo ON lo.c_location_id = oi.c_location_id
     LEFT JOIN c_region r ON r.c_region_id = lo.c_region_id
  WHERE t.ispercepcion = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_invoice_percepciones_v
  OWNER TO libertya;
  
--Incorporación de columnas para exportación de percepciones Misiones
DROP VIEW c_invoice_percepciones_v;

CREATE OR REPLACE VIEW c_invoice_percepciones_v AS 
 SELECT i.ad_client_id,
    i.ad_org_id,
    dt.c_doctype_id,
    dt.name AS doctypename,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 'F'::text
            ELSE 'C'::text
        END AS doctypechar,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'F'::text
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'NC'::text
            ELSE 'ND'::text
        END AS doctypenameshort,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'T'::character(1)
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'R'::character(1)
            ELSE 'D'::character(1)
        END AS doctypenameshort_aditional,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 1
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 102
            ELSE 2
        END AS tipo_de_documento_reg_neuquen,
    dt.docbasetype,
    i.c_invoice_id,
    i.documentno,
    date_trunc('day'::text, i.dateinvoiced) AS dateinvoiced,
    date_trunc('day'::text, i.dateacct) AS dateacct,
    date_trunc('day'::text, i.dateinvoiced) AS date,
    lc.letra,
    i.puntodeventa,
    i.numerocomprobante,
    i.grandtotal,
    bp.c_bpartner_id,
    bp.value AS bpartner_value,
    bp.name AS bpartner_name,
    replace(bp.taxid::text, '-'::text, ''::text) AS taxid,
    bp.iibb,
        CASE
            WHEN length(bp.iibb::text) > 7 THEN 1
            ELSE 0
        END AS tipo_contribuyente,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script,
    COALESCE(i.nombrecli, bp.name) AS nombrecli,
    COALESCE(i.nroidentificcliente, bp.taxid) AS nroidentificcliente,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS nroidentificcliente_with_script,
    ( SELECT l.address1
           FROM c_bpartner_location bpl
             JOIN c_location l ON l.c_location_id = bpl.c_location_id
          WHERE bpl.c_bpartner_id = bp.c_bpartner_id
          ORDER BY bpl.updated DESC
         LIMIT 1) AS address1,
    t.c_tax_id,
    t.name AS percepcionname,
    it.taxbaseamt,
    it.taxamt,
    (it.taxbaseamt * dt.signo_issotrx::numeric)::numeric(20,2) AS taxbaseamt_with_sign,
    (it.taxamt * dt.signo_issotrx::numeric)::numeric(20,2) AS taxamt_with_sign,
        CASE
            WHEN it.taxbaseamt <> 0::numeric THEN it.taxamt * 100::numeric / it.taxbaseamt
            ELSE 0::numeric
        END::numeric(20,2) AS alicuota,
    lo.city AS org_city,
    lo.postal AS org_postal_code,
    r.jurisdictioncode,
    translate(i.documentno::text, lc.letra::text, ''::text)::character varying(30) AS documentno_without_letter,
        CASE
            WHEN i.issotrx = 'Y'::bpchar THEN 'E'::text
            ELSE 'S'::text
        END AS aplicacion,
        cu.iso_code,
        i.issotrx,
        CASE
            WHEN it.taxbaseamt <> 0::numeric THEN it.taxamt * 100::numeric / it.taxbaseamt
            ELSE 0::numeric
        END::numeric(20,2) AS rate,
     ((CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'FA'::text
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'NC'::text
            ELSE 'ND'::text
        END) || '_' || lc.letra)::character(4) AS tipo_documento_misiones,
        (translate(lpad(to_char(puntodeventa,'9999'), 8, '0'::character),' ','0') ||
	translate(lpad(to_char(numerocomprobante,'99999999'), 12, '0'::character),' ','0'))::character(20) as nro_documento_misiones
     FROM c_invoicetax it
     JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id
     JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
     JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
     JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
     JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
     JOIN c_tax t ON t.c_tax_id = it.c_tax_id
     JOIN ad_orginfo oi ON oi.ad_org_id = i.ad_org_id
     LEFT JOIN c_location lo ON lo.c_location_id = oi.c_location_id
     LEFT JOIN c_region r ON r.c_region_id = lo.c_region_id
  WHERE t.ispercepcion = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_invoice_percepciones_v
  OWNER TO libertya;

--Despacho de Importación
CREATE TABLE m_import_clearance
(
  m_import_clearance_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  m_product_id integer NOT NULL,
  movementdate timestamp without time zone NOT NULL,
  clearancenumber character varying(255) NOT NULL,
  qty numeric(22,4) NOT NULL DEFAULT 0,
  qtyused numeric(22,4) NOT NULL DEFAULT 0, 
  CONSTRAINT m_import_clearance_key PRIMARY KEY (m_import_clearance_id), 
  CONSTRAINT import_clearance_product FOREIGN KEY (m_product_id)
	REFERENCES m_product (m_product_id) MATCH SIMPLE 
	ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=TRUE
);
ALTER TABLE m_import_clearance OWNER TO libertya;

--Relación entre despacho de importación e inventario
CREATE TABLE m_import_clearance_inventory
(
  m_import_clearance_inventory_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  m_import_clearance_id integer NOT NULL,
  m_inventoryline_id integer NOT NULL,
  qty numeric(22,4) NOT NULL DEFAULT 0,
  CONSTRAINT m_import_clearance_inventory_key PRIMARY KEY (m_import_clearance_inventory_id), 
  CONSTRAINT m_import_clearance_inventory_inventoryline_fk FOREIGN KEY (m_inventoryline_id)
	REFERENCES m_inventoryline (m_inventoryline_id) MATCH SIMPLE 
	ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT m_import_clearance_inventory_import_clearance_fk FOREIGN KEY (m_import_clearance_id)
	REFERENCES m_import_clearance (m_import_clearance_id) MATCH SIMPLE 
	ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
OIDS=TRUE
);
ALTER TABLE m_import_clearance_inventory OWNER TO libertya;

--Número de despacho para remitos de entrada
update ad_system set dummy = (SELECT addcolumnifnotexists('m_inout','clearancenumber','character varying(255)'));

--Número de despacho sobre la línea de remito
update ad_system set dummy = (SELECT addcolumnifnotexists('m_inoutline','m_import_clearance_id','integer'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoiceline','m_import_clearance_id','integer'));

--Despacho de Importación Activado
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_clientinfo','importclearanceactive','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20211220- Merge de Micro JACLBY 2.0
--Nuevas columnas para incorporar el descuento manual general de cabecera en Pedidos
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','ManualGeneralDiscount','numeric(9,2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_orderline','ManualGeneralDiscountAmt','numeric(20,2)'));

--Nueva columna con la clave de búsqueda del artículo
DROP VIEW rv_lista_de_precios;

CREATE OR REPLACE VIEW rv_lista_de_precios AS 
 SELECT p.ad_client_id,
    p.ad_org_id,
    pv.m_pricelist_version_id,
    p.m_pricelist_id,
    pr.name AS producto,
    pp.pricelist,
    pp.pricestd,
    pp.pricelimit,
    pr.value AS productvalue
   FROM m_pricelist p
     JOIN m_pricelist_version pv ON p.m_pricelist_id = pv.m_pricelist_id
     JOIN m_productprice pp ON pv.m_pricelist_version_id = pp.m_pricelist_version_id
     JOIN m_product pr ON pp.m_product_id = pr.m_product_id;

ALTER TABLE rv_lista_de_precios
  OWNER TO libertya;

--Informe de Despacho de Importación
CREATE OR REPLACE VIEW c_invoice_with_line_detailed_v AS 
 SELECT i.c_invoice_id,
    i.ad_client_id,
    i.ad_org_id,
    i.issotrx,
    i.documentno,
    i.docstatus,
    i.c_doctype_id,
    i.c_doctypetarget_id,
    i.c_order_id,
    i.description,
    i.salesrep_id,
    i.dateinvoiced,
    i.dateacct,
    i.c_bpartner_id,
    i.c_currency_id,
    i.paymentrule,
    i.c_paymentterm_id,
    i.m_pricelist_id,
    i.totallines,
    i.grandtotal,
    CASE
        WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
        ELSE - 1::numeric
    END AS multiplier,
    il.c_invoiceline_id,
    il.m_product_id,
    il.qtyentered,
    il.qtyinvoiced, 
    il.priceentered,
    il.priceactual,
    il.pricelist,
    il.linenetamt,
    il.linenetamount,
    il.taxamt,
    il.linetotalamt
   FROM c_invoice i
     JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
     JOIN c_invoiceline il ON il.c_invoice_id = i.c_invoice_id;

ALTER TABLE c_invoice_with_line_detailed_v
  OWNER TO libertya;

--Nuevas columnas en impuesto de pedido para que se corresponda con lo que posee en impuesto de factura
update ad_system set dummy = (SELECT addcolumnifnotexists('c_ordertax','arcibanormcode','character varying(10)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_ordertax','rate','numeric(24,6)'));

--Número de despacho de importación
DROP VIEW c_invoice_with_line_detailed_v ;

CREATE OR REPLACE VIEW c_invoice_with_line_detailed_v AS 
 SELECT i.c_invoice_id,
    i.ad_client_id,
    i.ad_org_id,
    i.issotrx,
    i.documentno,
    i.importclearance,
    i.docstatus,
    i.c_doctype_id,
    i.c_doctypetarget_id,
    i.c_order_id,
    i.description,
    i.salesrep_id,
    i.dateinvoiced,
    i.dateacct,
    i.c_bpartner_id,
    i.c_currency_id,
    i.paymentrule,
    i.c_paymentterm_id,
    i.m_pricelist_id,
    i.totallines,
    i.grandtotal,
    CASE
        WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
        ELSE - 1::numeric
    END AS multiplier,
    il.c_invoiceline_id,
    il.m_product_id,
    il.qtyentered,
    il.qtyinvoiced, 
    il.priceentered,
    il.priceactual,
    il.pricelist,
    il.linenetamt,
    il.linenetamount,
    il.taxamt,
    il.linetotalamt
   FROM c_invoice i
     JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
     JOIN c_invoiceline il ON il.c_invoice_id = i.c_invoice_id;

ALTER TABLE c_invoice_with_line_detailed_v
  OWNER TO libertya;

--Flag para aplicar esquemas de vencimiento en recibos 
update ad_system set dummy = (SELECT addcolumnifnotexists('m_discountconfig','applypaymentterm','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--Nueva columna Regla de envío de mercadería
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice','deliveryviarule','character(1)'));

--Soporte para rechazo de cheque de proveedor
update ad_system set dummy = (SELECT addcolumnifnotexists('c_payment','c_invoice_check_rejected_id','integer'));

-- Actualizar las preference existentes para ventas
update ad_preference 
set attribute = attribute || '_Sales'
where attribute = 'RejectedCheckDocTypeKey';

--Configuración por tipo de documento para aplicar retenciones. Micro TEHLBY2 7.0
update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','applyretention','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--Configuración por tipo de documento No Facturable 
update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','notinvoiceable','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--Migración del fix de signos en el informe saldos bancarios. Revisión CORE 2952
DROP VIEW c_payment_movements_v;

CREATE OR REPLACE VIEW c_payment_movements_v AS 
 SELECT p.c_payment_id,
    p.ad_client_id,
    p.ad_org_id,
    p.isactive,
    p.created,
    p.createdby,
    p.updated,
    p.updatedby,
    p.documentno,
    p.datetrx,
    p.isreceipt,
    p.c_doctype_id,
    p.trxtype,
    p.c_bankaccount_id,
    p.c_bpartner_id,
    p.c_invoice_id,
    p.c_bp_bankaccount_id,
    p.c_paymentbatch_id,
    p.tendertype,
    p.creditcardtype,
    p.creditcardnumber,
    p.creditcardvv,
    p.creditcardexpmm,
    p.creditcardexpyy,
    p.micr,
    p.routingno,
    p.accountno,
    p.checkno,
    p.a_name,
    p.a_street,
    p.a_city,
    p.a_state,
    p.a_zip,
    p.a_ident_dl,
    p.a_ident_ssn,
    p.a_email,
    p.voiceauthcode,
    p.orig_trxid,
    p.ponum,
    p.c_currency_id,
    p.payamt,
    p.discountamt,
    p.writeoffamt,
    p.taxamt,
    p.isapproved,
    p.r_pnref,
    p.r_result,
    p.r_respmsg,
    p.r_authcode,
    p.r_avsaddr,
    p.r_avszip,
    p.r_info,
    p.processing,
    p.oprocessing,
    p.docstatus,
    p.docaction,
    p.isreconciled,
    p.isallocated,
    p.isonline,
    p.processed,
    p.posted,
    p.isoverunderpayment,
    p.overunderamt,
    p.a_country,
    p.c_project_id,
    p.isselfservice,
    p.chargeamt,
    p.c_charge_id,
    p.isdelayedcapture,
    p.r_authcode_dc,
    p.r_cvv2match,
    p.r_pnref_dc,
    p.swipe,
    p.ad_orgtrx_id,
    p.c_campaign_id,
    p.c_activity_id,
    p.user1_id,
    p.user2_id,
    p.c_conversiontype_id,
    p.description,
    p.dateacct,
    p.c_order_id,
    p.isprepayment,
    p.ref_payment_id,
    p.checked,
    p.m_boletadeposito_id,
    p.issotrx,
    p.a_bank,
    p.a_cuit,
    p.duedate,
    p.couponnumber,
    p.m_entidadfinancieraplan_id,
    p.authcode,
    p.authmatch,
    p.c_posjournal_id,
    p.posnet,
    p.c_pospaymentmedium_id,
    p.c_bank_id,
    p.original_ref_payment_id,
    p.dateemissioncheck,
    p.couponbatchnumber,
    p.checkstatus,
    p.rejecteddate,
    p.rejectedcomments,
    p.bank_payment_documentno,
    p.bank_payment_date,
    p.c_bankpaymentstatus_id,
    p.auditstatus,
    p.ismanual,
    p.accounting_c_charge_id,
	CASE
            WHEN dt.signo_issotrx = 1 THEN p.payamt * -1
            ELSE p.payamt
        END AS payamtsign
   FROM c_payment p
     JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id;

ALTER TABLE c_payment_movements_v
  OWNER TO libertya;
  
--Si el total del comprobante es 0.01 se omite el mínimo del invoiceopen (0.01)
CREATE OR REPLACE FUNCTION invoiceopen(
    p_c_invoice_id integer,
    p_c_invoicepayschedule_id integer,
    p_c_currency_id integer,
    p_c_conversiontype_id integer,
    p_dateto timestamp without time zone)
  RETURNS numeric AS
$BODY$ /*************************************************************************  * The contents of this file are subject to the Compiere License.  You may  * obtain a copy of the License at    http://www.compiere.org/license.html  * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either  * express or implied. See the License for details. Code: Compiere ERP+CRM  * Copyright (C) 1999-2001 Jorg Janke, ComPiere, Inc. All Rights Reserved.  *  * converted to postgreSQL by Karsten Thiemann (Schaeffer AG),   * kthiemann@adempiere.org  *************************************************************************  ***  * Title:	Calculate Open Item Amount in Invoice Currency  * Description:  *	Add up total amount open for C_Invoice_ID if no split payment.  *  Grand Total minus Sum of Allocations in Invoice Currency  *  *  For Split Payments:  *  Allocate Payments starting from first schedule.  *  Cannot be used for IsPaid as mutating  *  * Test:  * 	SELECT C_InvoicePaySchedule_ID, DueAmt FROM C_InvoicePaySchedule WHERE C_Invoice_ID=109 ORDER BY DueDate;  * 	SELECT invoiceOpen (109, null) FROM AD_System; - converted to default client currency  * 	SELECT invoiceOpen (109, 11) FROM AD_System; - converted to default client currency  * 	SELECT invoiceOpen (109, 102) FROM AD_System;  * 	SELECT invoiceOpen (109, 103) FROM AD_System;  ***  * Pasado a Libertya a partir de Adempiere 360LTS  * - ids son de tipo integer, no numeric  * - TODO : tema de las zonas en los timestamp  * - Excepciones en SELECT INTO requieren modificador STRICT bajo PostGreSQL o usar  * NOT FOUND  * - Por ahora, el "ignore rounding" se hace como en libertya (-0.01,0.01),  * en vez de usar la precisión de la moneda  * - Se toma el tipo de conversion de la factura, auqneu esto es dudosamente correcto  * ya que otras funciones , en particular currencyBase nunca tiene en cuenta  * este valor  * - Como en Libertya se tiene en cuenta tambien C_Invoice_Credit_ID para calcular  * la cantidad alocada a una factura (aunque esto es medio dudoso....)  * - No se soporta la fecha como 3er parametro (en realidad, tampoco se esta  * usando actualmente, y se deberia poder resolver de otra manera)  * - Libertya parece tener un bug al filtrar por C_InvoicePaySchedule_ID al calcular  * el granTotal (el granTotal SIEMPRE es el total de la factura, tomada directamente  * de C_Invoice.GranTotal o a partir de la suma de los DueAmt en C_InvoicePaySchedule);  * se usa la sentencia como esta en Adempeire (esto es, solo se filtra por C_Invoice_ID)  * - Nuevo enfoque: NO se usa ni la vista C_Invoice_V ni multiplicadores  * se asume todo positivo...  * - El resultado SIEMPRE deberia ser positivo y en el intervalo [0..GrandTotal]  * - 03 julio: se pasa a usar getAllocatedAmt para hacer esta funcion consistente  * con invoicePaid  * - 03 julio: se pasa de usar STRICT a NOT FOUND; es mas eficiente  ************************************************************************/ 
DECLARE 	
v_Currency_ID		INTEGER := p_c_currency_id; 	
v_GrandTotal	  	NUMERIC := 0; 	
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

v_GrandTotal := v_TotalOpenAmt;
v_PaidAmt := getAllocatedAmt(p_C_Invoice_ID,v_Currency_ID,v_ConversionType_ID,1,p_dateto); 

IF (p_C_InvoicePaySchedule_ID > 0) THEN 
	v_Remaining := abs(v_PaidAmt);         
	FOR s IN  SELECT  ips.C_InvoicePaySchedule_ID, currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt 	        
		FROM    C_InvoicePaySchedule ips 	        
		INNER JOIN C_Invoice i on (ips.C_Invoice_ID = i.C_Invoice_ID) 		
		WHERE	ips.C_Invoice_ID = p_C_Invoice_ID AND   ips.IsValid='Y'         	
		ORDER BY ips.DueDate         
	LOOP             

		IF (s.C_InvoicePaySchedule_ID = p_C_InvoicePaySchedule_ID) THEN                 
			v_TotalOpenAmt := abs(s.DueAmt) - abs(v_Remaining);
			IF (v_TotalOpenAmt < 0) THEN                     
				v_TotalOpenAmt := 0;                  
			END IF; 				
			EXIT;              
		ELSE                  
			v_Remaining := abs(v_Remaining) - abs(s.DueAmt);     
			IF (v_Remaining < 0) THEN         
				v_Remaining := 0;                 
			END IF;             
		END IF;         
	END LOOP;     
ELSE         
	v_TotalOpenAmt := abs(v_TotalOpenAmt) - abs(v_PaidAmt);     
END IF; 	 	

IF (v_TotalOpenAmt >= -v_Min AND v_TotalOpenAmt <= v_Min
	AND v_GrandTotal < -v_Min AND v_GrandTotal > v_Min) THEN 		
	v_TotalOpenAmt := 0; 	
END IF; 	 	

--  el resultado debe ser 0 o de lo contrario tener el mismo signo que el comprobante 
IF (v_GrandTotal < 0) THEN
	v_TotalOpenAmt := v_TotalOpenAmt * -1::numeric;
END IF;

v_TotalOpenAmt := ROUND(COALESCE(v_TotalOpenAmt,0), v_Precision);

RETURN	v_TotalOpenAmt; 

END; 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer, integer, integer, timestamp without time zone)
  OWNER TO libertya;

--Corrección del date rate de la view de percepciones
DROP VIEW c_invoice_percepciones_v;

CREATE OR REPLACE VIEW c_invoice_percepciones_v AS 
 SELECT i.ad_client_id,
    i.ad_org_id,
    dt.c_doctype_id,
    dt.name AS doctypename,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 'F'::text
            ELSE 'C'::text
        END AS doctypechar,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'F'::text
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'NC'::text
            ELSE 'ND'::text
        END AS doctypenameshort,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'T'::character(1)
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'R'::character(1)
            ELSE 'D'::character(1)
        END AS doctypenameshort_aditional,
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 1
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 102
            ELSE 2
        END AS tipo_de_documento_reg_neuquen,
    dt.docbasetype,
    i.c_invoice_id,
    i.documentno,
    date_trunc('day'::text, i.dateinvoiced) AS dateinvoiced,
    date_trunc('day'::text, i.dateacct) AS dateacct,
    date_trunc('day'::text, i.dateinvoiced) AS date,
    lc.letra,
    i.puntodeventa,
    i.numerocomprobante,
    currencyconvert(i.grandtotal, i.c_currency_id, 118, i.dateacct::timestamp with time zone, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id) AS grandtotal,
    bp.c_bpartner_id,
    bp.value AS bpartner_value,
    bp.name AS bpartner_name,
    replace(bp.taxid::text, '-'::text, ''::text) AS taxid,
    replace(bp.iibb::text, '-'::text, ''::text) AS iibb,
        CASE
            WHEN length(bp.iibb::text) > 7 THEN 1
            ELSE 0
        END AS tipo_contribuyente,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script,
    COALESCE(i.nombrecli, bp.name) AS nombrecli,
    COALESCE(i.nroidentificcliente, bp.taxid) AS nroidentificcliente,
    ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS nroidentificcliente_with_script,
    ( SELECT l.address1
           FROM c_bpartner_location bpl
             JOIN c_location l ON l.c_location_id = bpl.c_location_id
          WHERE bpl.c_bpartner_id = bp.c_bpartner_id
          ORDER BY bpl.updated DESC
         LIMIT 1) AS address1,
    t.c_tax_id,
    t.name AS percepcionname,
    currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id) AS taxbaseamt,
    currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id) AS taxamt,
    (currencyconvert(it.taxbaseamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id) * dt.signo_issotrx::numeric)::numeric(20,2) AS taxbaseamt_with_sign,
    (currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp with time zone, i.c_conversiontype_id, i.ad_client_id, i.ad_org_id) * dt.signo_issotrx::numeric)::numeric(20,2) AS taxamt_with_sign,
        CASE
            WHEN it.taxbaseamt <> 0::numeric THEN it.taxamt * 100::numeric / it.taxbaseamt
            ELSE 0::numeric
        END::numeric(20,2) AS alicuota,
    lo.city AS org_city,
    lo.postal AS org_postal_code,
    r.jurisdictioncode,
    translate(i.documentno::text, lc.letra::text, ''::text)::character varying(30) AS documentno_without_letter,
        CASE
            WHEN i.issotrx = 'Y'::bpchar THEN 'E'::text
            ELSE 'S'::text
        END AS aplicacion,
    c.iso_code,
    (case when it.taxbaseamt = 0 then t.rate else coalesce(it.rate, it.taxamt/it.taxbaseamt * 100) end)::numeric(6,2) as rate,
    i.issotrx,
        CASE
            WHEN i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]) THEN 'R'::text
            ELSE 'A'::text
        END::character(1) AS tipo_registro_la_pampa
   FROM c_invoicetax it
     JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id
     JOIN c_currency c ON c.c_currency_id = i.c_currency_id
     JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
     JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
     JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
     JOIN c_tax t ON t.c_tax_id = it.c_tax_id
     JOIN ad_orginfo oi ON oi.ad_org_id = i.ad_org_id
     LEFT JOIN c_location lo ON lo.c_location_id = oi.c_location_id
     LEFT JOIN c_region r ON r.c_region_id = lo.c_region_id
  WHERE t.ispercepcion = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_invoice_percepciones_v
  OWNER TO libertya; 
  
--Soporte para comprobantes anulados (C_Invoice) con CAE o Impresos Fiscales
CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone,
    orgid integer,
    accounttype character,
    addstatusclause boolean,
    addstatusclauseinvoiceregistered boolean
    )
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$ declare     consulta varchar;     orderby1 varchar;     orderby2 varchar;     orderby3 varchar;     leftjoin1 varchar;     
leftjoin2 varchar;     advancedcondition varchar;     advancedconditioncashlineexception varchar;     whereclauseConditionDebit varchar;     
whereclauseConditionCredit varchar;     whereclauseConditionCreditCashException varchar;     whereclauseDateTo varchar;     
selectallocationNull varchar;     selectallocationPayment varchar;     selectallocationCashline varchar;     selectallocationCredit varchar;     
selectAllocationReferencePayment varchar;     selectAllocationReferenceCashline varchar;     selectAllocationReferenceCredit varchar;     
adocument v_documents_org_type_condition;     whereclauseOrgPayment varchar;     whereclauseOrgCashline varchar;     selectOrgPayment varchar;     
selectOrgCashline varchar;     whereclauseAccountTypeStd varchar;     whereclauseAccountTypeCashLine varchar;     
whereclauseDocStatusPayment varchar;     whereclauseDocStatusCashLine varchar;     whereclauseDocStatusInvoice varchar;     
whereclauseDocStatusAllocation varchar; 

BEGIN     

whereclauseDateTo = ' ( 1 = 1 ) ';          
if dateTo is not null then  		
	whereclauseDateTo = ' dateacct::date <= ''' || dateTo || '''::date ';     
end if;               

if condition <> 'A' then 		 		
	advancedcondition = 'il.paymentrule is null OR '; 		
	advancedconditioncashlineexception = ' (1=1) '; 		
	if condition = 'B' then 			
		advancedcondition = ''; 			
		advancedconditioncashlineexception = ' (1=2) '; 		
	end if; 		
	whereclauseConditionDebit = ' (i.paymentrule = ''' || condition || ''') '; 		
	whereclauseConditionCredit = ' (' || advancedcondition || ' il.paymentrule = ''' || condition || ''') '; 		
	whereclauseConditionCreditCashException = '( CASE WHEN il.paymentrule is not null THEN il.paymentrule = ''' || condition || ''' WHEN ic.paymentrule is not null THEN ic.paymentrule = ''' || condition || ''' ELSE '|| advancedconditioncashlineexception ||' END ) '; 	
else 		
	whereclauseConditionDebit = ' (1 = 1) '; 		
	whereclauseConditionCredit = ' (1 = 1) '; 		
	whereclauseConditionCreditCashException = ' (1 = 1) ';      
end if;         

whereclauseOrgPayment = ' (1 = 1) ';     
whereclauseOrgCashline = ' (1 = 1) ';     
selectOrgPayment = ' p.ad_org_id ';     
selectOrgCashline = ' cl.ad_org_id ';          
if (orgid is not null and orgid > 0) then 	
	whereclauseOrgPayment = ' CASE WHEN il.ad_org_id IS NOT NULL AND il.ad_org_id <> p.ad_org_id THEN p.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]) ELSE 1 = 1 END '; 	
	whereclauseOrgCashline = ' (CASE WHEN (il.ad_org_id IS NOT NULL AND il.ad_org_id <> cl.ad_org_id) OR (ic.ad_org_id IS NOT NULL AND ic.ad_org_id <> cl.ad_org_id) THEN cl.docstatus = ANY (ARRAY[''CO''::bpchar, ''CL''::bpchar]) ELSE 1 = 1 END) '; 	
	selectOrgPayment = ' COALESCE(il.ad_org_id, p.ad_org_id) ';         
	selectOrgCashline = ' COALESCE(il.ad_org_id, ic.ad_org_id, cl.ad_org_id) ';     
end if;          

whereclauseAccountTypeStd = ' (1=1) ';     
whereclauseAccountTypeCashLine = ' (1=1) ';          
if (accountType is not null and accountType <> 'B') then 	
	if (accountType = 'V') then  		
		whereclauseAccountTypeStd = ' bp.isvendor = ''Y'' '; 		
		whereclauseAccountTypeCashLine = ' (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN bp.isvendor = ''Y''  							WHEN il.c_bpartner_id IS NOT NULL THEN bp2.isvendor = ''Y''  							WHEN ic.c_bpartner_id IS NOT NULL THEN bp3.isvendor = ''Y''  							ELSE 1 = 2 END) '; 	
	else  		
		whereclauseAccountTypeStd = ' bp.iscustomer = ''Y'' ';  		
		whereclauseAccountTypeCashLine = ' (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN bp.iscustomer = ''Y''  							WHEN il.c_bpartner_id IS NOT NULL THEN bp2.iscustomer = ''Y''  							WHEN ic.c_bpartner_id IS NOT NULL THEN bp3.iscustomer = ''Y''  							ELSE 1 = 2 END) '; 	
	end if;     
end if;     

whereclauseDocStatusPayment = ' (1=1) ';     
whereclauseDocStatusCashLine = ' (1=1) ';     
whereclauseDocStatusInvoice = ' (1=1) ';     
whereclauseDocStatusAllocation = ' (1=1) ';    
 
if addstatusclause then 	
	whereclauseDocStatusPayment = ' p.docstatus IN (''CO'',''CL'',''WC'') '; 	
	whereclauseDocStatusCashLine = ' cl.docstatus IN (''CO'',''CL'',''WC'') '; 	
	whereclauseDocStatusInvoice = ' i.docstatus IN (''CO'',''CL'',''WC'') '; 	
	whereclauseDocStatusAllocation = ' ah.docstatus IN (''CO'',''CL'') ';     
end if;               

if addstatusclauseinvoiceregistered then
	whereclauseDocStatusInvoice = ' (i.docstatus IN (''CO'',''CL'',''WC'') 
						OR (i.docstatus IN (''VO'',''RE'') 
							AND ( (dt.isfiscal = ''Y'' AND i.fiscalalreadyprinted = ''Y'') 
								OR (dt.iselectronic = ''Y'' AND i.cae is not null) ) ) ) ';	
end if;

if summaryonly = false then         
	orderby1 = ' ORDER BY ''C_Invoice''::text, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, i.documentno, i.issotrx, i.docstatus,                  CASE                      WHEN ips.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate                      ELSE i.dateinvoiced                  END, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal, ips.c_invoicepayschedule_id, ips.duedate, i.dateinvoiced, bp.socreditstatus ';         
	orderby2 = ' ORDER BY ''C_Payment''::text, p.c_payment_id, p.ad_client_id, ' || selectOrgPayment || ', p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name, dt.printname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt, NULL::integer, p.duedate, bp.socreditstatus ';         
	orderby3 = ' ORDER BY ''C_CashLine''::text, cl.c_cashline_id, cl.ad_client_id, ' || selectOrgCashline || ', cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,                 CASE                     WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id 		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id                     ELSE ic.c_bpartner_id                 END, dt.c_doctype_id,                 CASE                     WHEN cl.amount < 0.0 THEN 1                     ELSE (-1)                 END, dt.name, dt.printname, ''@line@''::text || cl.line::character varying::text,                 CASE                     WHEN cl.amount < 0.0 THEN ''N''::bpchar                     ELSE ''Y''::bpchar                 END, cl.docstatus, c.statementdate, c.dateacct, cl.c_currency_id, NULL::integer, abs(cl.amount), NULL::timestamp without time zone, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) '; 	 	
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
	selectallocationPayment = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE ' || whereclauseDocStatusAllocation || ' and allocationtype <> ''MAN'' AND ah.dateacct::date = p.dateacct::date AND ' || selectAllocationReferencePayment || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id '; 	selectallocationCashline = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE ' || whereclauseDocStatusAllocation || ' and allocationtype <> ''MAN'' AND ah.dateacct::date = c.dateacct::date AND ' || selectAllocationReferenceCashline || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id '; 	selectallocationCredit = ' (SELECT ah.c_allocationhdr_id FROM c_allocationline al INNER JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id WHERE ' || whereclauseDocStatusAllocation || ' and allocationtype <> ''MAN'' AND ah.dateacct::date = i.dateacct::date AND ' || selectAllocationReferenceCredit || ' AND ' || whereclauseDateTo || ' ORDER BY ah.created LIMIT 1) as c_allocationhdr_id '; 	     
end if;         

consulta = ' SELECT * FROM          
(        ( SELECT DISTINCT ''C_Invoice''::text AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
                        CASE                             WHEN ips.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate                             
                        ELSE i.dateinvoiced                         
                        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, i.grandtotal AS amount, ips.c_invoicepayschedule_id, ips.duedate, 
			i.dateinvoiced AS truedatetrx, bp.socreditstatus, i.c_order_id, ' 				
			|| selectallocationCredit ||                 
	' FROM c_invoice i               
	JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id          
	JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id and (' || $1 || ' = -1  or bp.c_bpartner_id = ' || $1 || ')     
	LEFT JOIN c_invoicepayschedule ips ON ips.c_invoice_id = i.c_invoice_id 
	WHERE  ' || whereclauseDocStatusInvoice || ' AND ' || whereclauseConditionDebit || '  AND ' || whereclauseAccountTypeStd || ' ' || orderby1 || '     )         
	UNION ALL                 
	( SELECT DISTINCT ''C_Payment''::text AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, ' || selectOrgPayment || ', p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, p.duedate, p.datetrx AS truedatetrx, bp.socreditstatus, 0 as c_order_id, ' 		|| selectallocationPayment ||                    ' FROM c_payment p               JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id          JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or p.c_bpartner_id = ' || $1 || ') 	LEFT JOIN c_allocationline al ON al.c_payment_id = p.c_payment_id  	LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id 	LEFT JOIN M_BoletaDepositoLine bdlr on bdlr.c_reverse_payment_id = p.c_payment_id 	LEFT JOIN M_BoletaDeposito bdr on bdr.M_BoletaDeposito_ID = bdlr.M_BoletaDeposito_ID 	LEFT JOIN M_BoletaDepositoLine bdle on bdle.c_depo_payment_id = p.c_payment_id 	LEFT JOIN M_BoletaDeposito bde on bde.M_BoletaDeposito_ID = bdle.M_BoletaDeposito_ID 	LEFT JOIN M_BoletaDeposito bddb on bddb.c_boleta_payment_id = p.c_payment_id   WHERE  ' || whereclauseDocStatusPayment || ' AND ' || whereclauseOrgPayment || ' AND (CASE WHEN bdr.M_BoletaDeposito_ID IS NOT NULL  		OR bde.M_BoletaDeposito_ID IS NOT NULL  		OR bddb.M_BoletaDeposito_ID IS NOT NULL THEN p.docstatus NOT IN (''CO'',''CL'')  	ELSE 1 = 1 	END)  AND ' || whereclauseConditionCredit || ' AND ' || whereclauseAccountTypeStd || ' ' || orderby2 || ' ) UNION ALL         ( SELECT DISTINCT ''C_CashLine''::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, ' || selectOrgCashline || ', cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby,                 CASE                     WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id 		    WHEN il.c_bpartner_id IS NOT NULL THEN il.c_bpartner_id                     ELSE ic.c_bpartner_id                 END AS c_bpartner_id, dt.c_doctype_id,                 CASE                     WHEN cl.amount < 0.0 THEN 1                     ELSE (-1)                 END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, ''@line@''::text || cl.line::character varying::text AS documentno,                 CASE                     WHEN cl.amount < 0.0 THEN ''N''::bpchar                     ELSE ''Y''::bpchar                 END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, COALESCE(bp.socreditstatus, bp2.socreditstatus, bp3.socreditstatus) AS socreditstatus, 0 as c_order_id, '                 || selectallocationCashline ||         ' FROM ( 		select cl.* FROM c_cashline cl  		where (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || ' 		union  		select cl.* FROM c_cashline cl  		JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || ' 		union  		select cl.* FROM c_cashline cl  		JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id 		JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ') AND ' || whereclauseDocStatusCashLine || '        ) as cl       JOIN c_cash c ON cl.c_cash_id = c.c_cash_id    LEFT JOIN c_bpartner bp ON cl.c_bpartner_id = bp.c_bpartner_id AND (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')    JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname          FROM c_doctype d         WHERE d.doctypekey::text = ''CMC''::text) dt ON cl.ad_client_id = dt.ad_client_id    LEFT JOIN c_allocationline al ON al.c_cashline_id = cl.c_cashline_id    LEFT JOIN c_invoice il ON il.c_invoice_id = al.c_invoice_id AND (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')    LEFT JOIN c_bpartner bp2 ON il.c_bpartner_id = bp2.c_bpartner_id    LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id AND (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')    LEFT JOIN c_bpartner bp3 ON ic.c_bpartner_id = bp3.c_bpartner_id   WHERE (CASE WHEN cl.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or cl.c_bpartner_id = ' || $1 || ')         WHEN il.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or il.c_bpartner_id = ' || $1 || ')         WHEN ic.c_bpartner_id IS NOT NULL THEN (' || $1 || ' = -1 or ic.c_bpartner_id = ' || $1 || ')         ELSE 1 = 2 END)     AND ' || whereclauseOrgCashline || '     AND (' || whereclauseConditionCreditCashException || ' )     AND ' || whereclauseAccountTypeCashLine || ' ' || orderby3 || ' )) AS d   WHERE ' || whereclauseDateTo || ' ; '; FOR adocument IN EXECUTE consulta LOOP 	return next adocument; END LOOP; END $BODY$


  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone, integer, character, boolean, boolean)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION v_documents_org_filtered(
    bpartner integer,
    summaryonly boolean,
    condition character,
    dateto timestamp without time zone,
    orgid integer,
    accounttype character,
    addstatusclause boolean)
  RETURNS SETOF v_documents_org_type_condition AS
$BODY$ 
BEGIN
return query select * from v_documents_org_filtered(bpartner, summaryonly, condition, dateto, orgid, accounttype, addstatusclause, false); 
END 
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION v_documents_org_filtered(integer, boolean, character, timestamp without time zone, integer, character, boolean)
  OWNER TO libertya;

--Función que retorna el importe de percepciones configurado para devolución parcial o no
CREATE OR REPLACE FUNCTION getperceptionamtreturn(
    p_c_invoice_id integer,
    ispartialreturn character)
  RETURNS numeric AS
$BODY$
DECLARE
    	v_Amount        	NUMERIC;
BEGIN
	-- Sumar el importe de percepciones 
	select coalesce(sum(taxamt),0) into v_Amount
	from c_invoicetax as it 
	inner join c_tax as t on t.c_tax_id = it.c_tax_id 
	where it.c_invoice_id = p_c_invoice_id AND t.ispercepcion = 'Y'
		and exists (select op.ad_org_percepcion_id 
			from ad_org_percepcion op 
			where partialreturn = ispartialreturn and op.c_tax_id = t.c_tax_id);
	    
	RETURN v_Amount;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION getperceptionamtreturn(integer, character)
  OWNER TO libertya;

--20220106- Merge de Micro QUICKER 
--Nueva columna para incorporar el boton para agregar lineas rapido
update ad_system set dummy = (SELECT addcolumnifnotexists('c_order','addlinesquicker','character(1)'));
