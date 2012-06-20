-- ========================================================================================
-- PREINSTALL FROM 10.09. 
-- ========================================================================================
-- 	Consideraciones importantes:
--			1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 			2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

-- 20101018-0909 La vista incluia facturas anuladas o revertidas.
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
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN -1
            ELSE 1
        END AS multiplier
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctype_id = d.c_doctype_id
   JOIN c_bpartner b ON i.c_bpartner_id = b.c_bpartner_id
   JOIN c_bpartner_location bpl ON i.c_bpartner_location_id = bpl.c_bpartner_location_id
   JOIN c_location loc ON bpl.c_location_id = loc.c_location_id
  WHERE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]);

-- 20101019-1200 Cajas Diarias
CREATE TABLE c_posjournal
(
  c_posjournal_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_pos_id integer NOT NULL,
  ad_user_id integer NOT NULL,
  c_cash_id integer,
  datetrx timestamp without time zone NOT NULL,
  docstatus character(2) NOT NULL,
  action character(2) NOT NULL,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,

  CONSTRAINT c_posjournal_key PRIMARY KEY (c_posjournal_id),
  CONSTRAINT client_posjournal FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT org_posjournal FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT pos_posjournal FOREIGN KEY (c_pos_id)
      REFERENCES c_pos (c_pos_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT user_posjournal FOREIGN KEY (ad_user_id)
      REFERENCES ad_user (ad_user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cash_posjournal FOREIGN KEY (c_cash_id)
      REFERENCES c_cash (c_cash_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE c_poscashstatement
(
  c_poscashstatement_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_posjournal_id integer NOT NULL,
  qty integer NOT NULL DEFAULT 0,
  cashvalue character varying(4) NOT NULL,
  c_currency_id integer NOT NULL,

  CONSTRAINT c_poscashstatement_key PRIMARY KEY (c_poscashstatement_id),
  CONSTRAINT client_poscashstatement FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT org_poscashstatement FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT posjournal_poscashstatement FOREIGN KEY (c_posjournal_id)
      REFERENCES c_posjournal (c_posjournal_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT currency_poscashstatement FOREIGN KEY (c_currency_id)
      REFERENCES c_currency (c_currency_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE C_POS ADD COLUMN OperationMode character varying(1) NOT NULL DEFAULT 'S'::bpchar;
ALTER TABLE C_POS ALTER COLUMN SalesRep_ID DROP NOT NULL;

-- FIN -> 20101019-1200 Cajas Diarias

-- 20101029-1459 Los parametros de los procesos tenian un limite de 60 caracteres!
ALTER TABLE ad_pinstance_para ALTER p_string TYPE character varying(255);
ALTER TABLE ad_pinstance_para ALTER p_string_to TYPE character varying(255);
ALTER TABLE ad_pinstance_para ALTER info TYPE character varying(255);
ALTER TABLE ad_pinstance_para ALTER info_to TYPE character varying(255);

-- 20101101-12:32 Nuevos campos en ad_plugin
alter table ad_plugin add column component_export_date varchar(30);
alter table ad_plugin add column component_last_changelog varchar(30);

-- 20101103-13:08 Nuevos campos en c_bpartner para copia de entidades comerciales y actualizacion del saldo
ALTER TABLE c_bpartner ADD COLUMN copyfrom character(1);
ALTER TABLE c_bpartner ADD COLUMN updatebalance character(1);

-- 20101105-10:06 Nuevo campo para impresion de reportes por tipo de documento
ALTER TABLE C_DocType ADD COLUMN AD_Process_ID int null;

-- 20101108-10:01 Ultimo changelog replicado del host origen
ALTER TABLE AD_REPLICATIONHOST ADD COLUMN LASTCHANGELOG_ID INT NULL;

-- 20101108-12:47 Registración de flag para actualizar el saldo de la entidad comercial al completar la caja 
ALTER TABLE c_cashline ADD COLUMN updatebpbalance character(1) NOT NULL DEFAULT 'Y'::bpchar;

-- 20101110-14:58 Tipo de Libro de Caja
ALTER TABLE C_CashBook ADD COLUMN CashBookType character(1) DEFAULT 'G'::bpchar;
UPDATE C_CashBook SET CashBookType = 'G';
ALTER TABLE C_CashBook ALTER COLUMN CashBookType SET NOT NULL;

-- 20101110-15:00 Corrección de organización en el CashBook Standard.
UPDATE C_CashBook SET AD_Org_ID = 1010053 WHERE C_CashBook_ID=1010024;

-- 20101118-12:00 Nueva columna en tabla de Caja Diaria
ALTER TABLE C_POSJournal ADD COLUMN CashTransfer character(1);

-- 20101125-10:05 Ampliaciones a Líneas de Caja
ALTER TABLE C_CashLine ADD COLUMN DocStatus character(2); -- NOT NULL
ALTER TABLE C_CashLine ADD COLUMN DocAction character(2); -- NOT NULL
ALTER TABLE C_CashLine ADD COLUMN Processing character(1);  
UPDATE C_CashLine cl SET DocStatus = 'DR', DocAction = 'CO' WHERE (SELECT c.DocStatus FROM C_Cash c WHERE c.C_Cash_ID = cl.C_Cash_ID) = 'DR';
UPDATE C_CashLine cl SET DocStatus = 'CO', DocAction = 'CL', Processed = 'Y' WHERE (SELECT c.DocStatus FROM C_Cash c WHERE c.C_Cash_ID = cl.C_Cash_ID) = 'CO';
UPDATE C_CashLine cl SET DocStatus = 'CL', DocAction = (SELECT c.DocAction FROM C_Cash c WHERE c.C_Cash_ID = cl.C_Cash_ID), Processed = 'Y' WHERE (SELECT c.DocStatus FROM C_Cash c WHERE c.C_Cash_ID = cl.C_Cash_ID) = 'CL';
ALTER TABLE C_CashLine ALTER COLUMN DocStatus SET NOT NULL;
ALTER TABLE C_CashLine ALTER COLUMN DocAction SET NOT NULL;

-- 20101125-10:05 Referencia al pago por transferencia a cta. bancaria
ALTER TABLE C_CashLine ADD COLUMN C_Payment_ID integer;
ALTER TABLE c_cashline
  ADD CONSTRAINT payment_ccashline FOREIGN KEY (c_payment_id)
  REFERENCES c_payment (c_payment_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
-- 20101125-10:05 Referencia al Libro de Caja para transferencia entre cajas
ALTER TABLE C_CashLine ADD COLUMN TransferCash_ID integer;
ALTER TABLE c_cashline
  ADD CONSTRAINT transfercash_ccashline FOREIGN KEY (transfercash_id)
  REFERENCES c_cash (c_cash_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
-- 20101125-10:05 Referencia a la línea de caja generada por una transferencia entre cajas
ALTER TABLE C_CashLine ADD COLUMN TransferCashLine_ID integer;
ALTER TABLE c_cashline
  ADD CONSTRAINT transfercashline_ccashline FOREIGN KEY (transfercashline_id)
  REFERENCES c_cashline (c_cashline_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- 20101126-11:08 La columna ad_org_id estaba incorrectamente definida como numeric
ALTER TABLE c_production_orderline ALTER ad_org_id TYPE integer;

-- 20101126-12:09 Tabla auxiliar de registros de transacciones para control centralizado de cuentas corrientes
CREATE TABLE c_centralaux
(
  c_centralaux_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  documentuid character varying(255),
  documentrecord_id integer,
  registertype character(2) NOT NULL,
  authcode character varying(255),
  datetrx date,
  duedate date,
  amt numeric(22,9),
  sign integer,
  docstatus character(2),
  c_bpartner_id integer,
  bpartneruid character varying(255),
  paymentrule character varying(2),
  confirmed character(1) NOT NULL DEFAULT 'N'::bpchar,
  prepayment character(1) NOT NULL DEFAULT 'N'::bpchar,
  tendertype character varying(2),
  reconciled character(1) NOT NULL DEFAULT 'N'::bpchar,
  transactiontype character(1),
  doctype character(1),
  documentno character varying(30),
  CONSTRAINT c_centralaux_key PRIMARY KEY (c_centralaux_id)
)
WITH (OIDS=TRUE);
ALTER TABLE c_centralaux OWNER TO libertya;

-- 20101126-12:09 Tabla de configuración de central para control centralizado de cuentas corrientes
CREATE TABLE c_centralconfiguration
(
  c_centralconfiguration_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  ad_replicationhost_id integer NOT NULL,
  alternativecentraladdress character varying(255),
  isremotecreditcontrol character(1) NOT NULL DEFAULT 'Y'::bpchar,
  isremoteobtaincontrol character(1) NOT NULL DEFAULT 'Y'::bpchar, 
  iscontrolactivated character(1) NOT NULL DEFAULT 'N'::bpchar, 
  manageactivation character(1),
  CONSTRAINT c_centralconfiguration_key PRIMARY KEY (c_centralconfiguration_id),
  CONSTRAINT c_centralconfiguration_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_centralconfiguration_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_centralconfiguration_replicationhost FOREIGN KEY (ad_replicationhost_id)
      REFERENCES ad_replicationhost (ad_replicationhost_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=TRUE);
ALTER TABLE c_centralconfiguration OWNER TO libertya;

-- 20101126-12:10 Tabla de excepciones de crédito 
CREATE TABLE c_creditexception
(
  c_creditexception_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  c_bpartner_id integer NOT NULL,
  exceptiontype character(1) NOT NULL,
  creditlimitexception numeric(22,2) DEFAULT 0,
  creditstatusexception character(1),
  exceptionstartdate date NOT NULL,
  exceptionenddate date NOT NULL,
  CONSTRAINT c_creditexception_key PRIMARY KEY (c_creditexception_id),
  CONSTRAINT c_creditexception_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_creditexception_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT c_creditexception_bpartner FOREIGN KEY (c_bpartner_id)
      REFERENCES c_bpartner (c_bpartner_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=TRUE);
ALTER TABLE c_creditexception OWNER TO libertya;

-- 20101202-13:40 Nuevas columnas para registrar el tipo de documento en la tabla centralizada de transacciones para cuentas corrientes. Necesarias para agregar nuevos registros desde la ventana 
ALTER TABLE c_centralaux ADD COLUMN c_doctype_id integer;
ALTER TABLE c_centralaux ADD COLUMN doctypekey character varying(40);

-- 20101202-13:41 Nuevas columnas para registrar un código de autorización en facturas, payments y cashlines
ALTER TABLE c_invoice ADD authcode character varying(255);
ALTER TABLE c_cashline ADD authcode character varying(255);
ALTER TABLE c_payment ADD authcode character varying(255);

-- 20101203-13:30 Columna para marcar misma línea a los parámetros de informe/proceso 
ALTER TABLE ad_process_para ADD COLUMN sameline character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20101203-13:50 Funcion que verifica si una columna de una tabla ya existe antes de crearla a nivel SQL
CREATE OR REPLACE FUNCTION addcolumnifnotexists(tablename varchar, columnname varchar, columnprops varchar)
  RETURNS numeric AS
$BODY$
DECLARE
	existe integer;
BEGIN
	SELECT into existe COUNT(1) 
	FROM information_schema.columns 
	WHERE lower(table_name) = lower(tablename)
	AND lower(column_name) = lower(columnname);

	IF (existe = 0) THEN
		EXECUTE 'ALTER TABLE ' || tablename || 
			' ADD COLUMN ' || columnname || 
			' ' || columnprops;
		RETURN 1;
	END IF;

	RETURN 0;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
  
-- 20101203-13:55 Nueva columna en m_product_gamas
SELECT addcolumnifnotexists('m_product_gamas', 'value', 'varchar(100) null'); 

-- 20101206-13:44 Nuevas columnas para hacer matching con las transacciones no confirmadas de cuenta corriente en la central
ALTER TABLE c_invoice ADD authmatch character(1) DEFAULT 'Y'::bpchar;
ALTER TABLE c_cashline ADD authmatch character(1) DEFAULT 'Y'::bpchar;
ALTER TABLE c_payment ADD authmatch character(1) DEFAULT 'Y'::bpchar;

-- 20101206-16:50 Referencia al allocation creado por pago a factura
ALTER TABLE C_CashLine ADD COLUMN C_AllocationHdr_ID integer;
ALTER TABLE C_CashLine
  ADD CONSTRAINT allocation_ccashline FOREIGN KEY (c_allocationhdr_id)
  REFERENCES c_allocationhdr (c_allocationhdr_id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- 20101213-16:51 Contexto de uso del medio de pago
ALTER TABLE c_pospaymentmedium ADD COLUMN context character(1);

-- 20101216-09:38 Validaciones para replicacion
-- funcion que retorna si una columna es de tipo referencia, y a que tabla referencia
CREATE OR REPLACE FUNCTION replication_get_referenced_table(p_columnid integer)
  RETURNS varchar AS
$BODY$
DECLARE
columnType int;
tableCount int;
colName varchar;
columnTrail varchar;
refTableName varchar;
columnIsKey character(1);
refValueID int;
BEGIN
    columnType := -1;
   
    -- es de tipo referencia?
    SELECT INTO columnType c.AD_Reference_ID
    FROM AD_Column c
    INNER JOIN AD_Reference r ON c.AD_Reference_ID = r.AD_Reference_ID
    WHERE c.AD_Column_ID = p_columnID
    AND c.AD_Reference_ID IN (18,19,21,25,30,31,33,35);

    -- si ya la encontró, entonces es referencia
    IF columnType != -1 THEN

        -- ver que referencia tiene seteada
        SELECT INTO refValueID ad_reference_value_id FROM ad_column WHERE AD_Column_ID = p_columnid;

        -- leer la tabla referenciada
        SELECT INTO refTableName t.tablename
        FROM ad_ref_table rt
        INNER JOIN ad_table t ON rt.ad_table_id = t.ad_table_id
        WHERE ad_reference_id = refValueID;

        IF refTableName IS NOT NULL AND refTableName != '' THEN
            return refTableName;
        END IF;
    END IF;

    -- si no es referencia, ver si termina en _ID   
    SELECT INTO columnType, colName, columnIsKey AD_Reference_ID, columnname, isKey
    FROM AD_Column
    WHERE AD_Column_ID = p_columnID;
    columnTrail := substring(colName from (select char_length(colName)-2) for 3);
   
    -- si termina en _ID, y no es parte de la clave, o la clave,
    -- verificar si existe una tabla a la que referencia la columna
    IF columnTrail = '_ID' AND columnIsKey = 'N' AND columnType != 13 THEN
        refTableName = substring(colName from 1 for (select char_length(colName))-3);
        SELECT INTO tableCount count(1) FROM ad_table WHERE upper(tablename) = upper(refTableName);

        IF tableCount IS NOT NULL AND tableCount > 0 THEN
            return refTableName;
        END IF;
    END IF;

    return '';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
 

-- funcion que determina si una columna de una tabla de replicación puede referenciar a otra columna de otra tabla
-- parametros: p_columnid es el AD_Column_ID, y column_data es el registro
 CREATE OR REPLACE FUNCTION replication_is_valid_reference(p_columnid integer, column_data varchar)
  RETURNS integer AS
$BODY$
DECLARE
targetTableName varchar;
targetTableID int;
sourceTableID int;
isValid int;
colName varchar;
hostID int;
sourceRepArray varchar;
targetRepArray varchar;
BEGIN
   
    -- si la columna es AD_Language, omitir cualquier tipo de validacion
    SELECT INTO colName columnname FROM AD_Column WHERE AD_Column_ID = p_columnID;
    IF colName = 'AD_Language' THEN
        RETURN 1;   
    END IF;
   
    -- ver si el campo es una referencia
    select into targetTableName replication_get_referenced_table(p_columnID);

    -- si es una referencia, verificar el bitacoreo en la tabla referenciada
    IF targetTableName != '' THEN
       
		-- si el valor es 0, entonces es una referencia valida (seria como null para LY)
		IF column_data = '0' THEN
			return 1;
		END IF;

        -- recuperar el identificador de la tabla destino
        SELECT into targetTableID AD_Table_ID FROM AD_Table WHERE upper(tablename) = upper(targetTableName);
        -- recuperar el identificador de la tabla origen
        SELECT into sourceTableID AD_Table_ID FROM AD_Column WHERE AD_Column_ID = p_columnid;
           
        -- ver si la tabla destino es bitacoreada
        SELECT INTO hostID replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y';
        IF hostID IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF;

        -- si la tabla destino directamente no tiene la columna retrieveuid, entonces devolver que es invalido
        SELECT INTO isValid count(1) FROM information_schema.columns
            WHERE lower(column_name) = 'retrieveuid' AND lower(table_name) = lower(targetTableName);
        IF isValid = 0 THEN
            return 0;
        END IF;

		-- comparar el replicationArray de la tabla origen y de la tabla destino:
		-- unicamente si son iguales se podrá replicar, en caso contrario devolver que no
		SELECT INTO sourceRepArray replicationArray FROM AD_TableReplication where ad_table_id = sourceTableID;
		SELECT INTO targetRepArray replicationArray FROM AD_TableReplication where ad_table_id = targetTableID;

		-- si los repArray de las tablas origen y destino son iguales, entonces todo bien
		IF sourceRepArray = targetRepArray THEN
			return 1;
		END IF;

		-- si los repArray de las tablas origen y destino son diferentes (y la destino es diferente a 0),
		-- entonces no puede referenciarse el registro, ya que existirá en algunos hosts y en otros no.
		IF sourceRepArray <> targetRepArray AND targetRepArray <> '0' THEN
			return 0;
		END IF;

		-- si son diferentes porque se debe a que la tabla destino tiene 
		-- repArray = 0,habra que analizar el registro en cuestión:

        -- hay que ver si se esta referenciando a 
		-- 1) un registro ya existente en el core (retrieveuid debe iniciar con o),
		-- 2) o bien a un registro proveniente de otra sucursal (no generado localmente)
        EXECUTE 'select count(1) FROM ' || targetTableName ||
            ' WHERE ' || targetTableName || '_ID = ' || column_data::int ||
            ' AND ( retrieveuid ilike ''o%'' OR retrieveuid NOT ilike ''h' || hostID::varchar || '_%'' )' INTO isValid;       
       
        return isValid;
    END IF;
   
    return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
 
-- incorporacion de la validacion por referencias
CREATE OR REPLACE FUNCTION replication_xml_append_column(columnid integer, columnvalue character varying)
  RETURNS character varying AS
$BODY$
DECLARE
    newValue varchar;
    nullY varchar;
    isValid int;
BEGIN
    SELECT INTO isValid replication_is_valid_reference(columnid, columnvalue);
    IF isValid = 0 THEN
        RAISE EXCEPTION 'La columna % referencia al registro % fuera del sistema de replicacion', columnid, columnvalue;
    END IF;

   
    nullY := '';
    newValue := columnValue;
    IF newValue IS NULL THEN
        newValue :='';
        nullY := 'null="Y"';
    END IF;
   
    return '<column id="' || columnID::varchar || '" value="' || newValue || '" ' || nullY || '/>';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION replication_xml_append_column(integer, character varying) OWNER TO libertya;
-- 20101216-09:38 FIN Validaciones para replicacion

-- 20101216-15:03 Valor por defecto necesario 
ALTER TABLE ad_replicationhost ALTER COLUMN thishost SET DEFAULT 'N';

-- 20101217-13:48 Correccion en funcion de replicacion
-- funcion que determina si una columna de una tabla de replicación puede referenciar a otra columna de otra tabla
-- parametros: p_columnid es el AD_Column_ID, y column_data es el registro
 CREATE OR REPLACE FUNCTION replication_is_valid_reference(p_columnid integer, column_data varchar)
  RETURNS integer AS
$BODY$
DECLARE
targetTableName varchar;
targetTableID int;
sourceTableID int;
isValid int;
colName varchar;
hostID int;
sourceRepArray varchar;
targetRepArray varchar;
viewTable varchar;
BEGIN
   
    -- si la columna es AD_Language, omitir cualquier tipo de validacion
    SELECT INTO colName columnname FROM AD_Column WHERE AD_Column_ID = p_columnID;
    IF colName = 'AD_Language' THEN
        RETURN 1;   
    END IF;
   
    -- ver si el campo es una referencia
    select into targetTableName replication_get_referenced_table(p_columnID);

    -- si es una referencia, verificar el bitacoreo en la tabla referenciada
    IF targetTableName != '' THEN
       
    	-- verificar si la tabla destino es simplemente una vista
		select INTO viewTable isview from ad_table where tablename = targetTableName;
		IF viewTable = 'Y' THEN
			return 1;
		end if;
		
		-- si el valor es 0, entonces es una referencia valida (seria como null para LY)
		IF column_data = '0' THEN
			return 1;
		END IF;

        -- recuperar el identificador de la tabla destino
        SELECT into targetTableID AD_Table_ID FROM AD_Table WHERE upper(tablename) = upper(targetTableName);
        -- recuperar el identificador de la tabla origen
        SELECT into sourceTableID AD_Table_ID FROM AD_Column WHERE AD_Column_ID = p_columnid;
           
        -- ver si la tabla destino es bitacoreada
        SELECT INTO hostID replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y';
        IF hostID IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF;

        -- si la tabla destino directamente no tiene la columna retrieveuid, entonces devolver que es invalido
        SELECT INTO isValid count(1) FROM information_schema.columns
            WHERE lower(column_name) = 'retrieveuid' AND lower(table_name) = lower(targetTableName);
        IF isValid = 0 THEN
            return 0;
        END IF;

		-- comparar el replicationArray de la tabla origen y de la tabla destino:
		-- unicamente si son iguales se podrá replicar, en caso contrario devolver que no
		SELECT INTO sourceRepArray replicationArray FROM AD_TableReplication where ad_table_id = sourceTableID;
		SELECT INTO targetRepArray replicationArray FROM AD_TableReplication where ad_table_id = targetTableID;

		-- si los repArray de las tablas origen y destino son iguales, entonces todo bien
		IF sourceRepArray = targetRepArray THEN
			return 1;
		END IF;

		-- si los repArray de las tablas origen y destino son diferentes (y la destino es diferente a 0),
		-- entonces no puede referenciarse el registro, ya que existirá en algunos hosts y en otros no.
		IF sourceRepArray <> targetRepArray AND targetRepArray <> '0' THEN
			return 0;
		END IF;

		-- si son diferentes porque se debe a que la tabla destino tiene 
		-- repArray = 0,habra que analizar el registro en cuestión:

        -- hay que ver si se esta referenciando a 
		-- 1) un registro ya existente en el core (retrieveuid debe iniciar con o),
		-- 2) o bien a un registro proveniente de otra sucursal (no generado localmente)
        EXECUTE 'select count(1) FROM ' || targetTableName ||
            ' WHERE ' || targetTableName || '_ID = ' || column_data::int ||
            ' AND ( retrieveuid ilike ''o%'' OR retrieveuid NOT ilike ''h' || hostID::varchar || '_%'' )' INTO isValid;       
       
        return isValid;
    END IF;
   
    return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
-- 20101217-13:48 FIN Correccion en funcion de replicacion
  
-- 20101226-18:48 se incorpora la validacion por isnull de column_data casi al principio del proceso
  CREATE OR REPLACE FUNCTION replication_is_valid_reference(p_columnid integer, column_data character varying)
  RETURNS integer AS
$BODY$
DECLARE
targetTableName varchar;
targetTableID int;
sourceTableID int;
isValid int;
colName varchar;
hostID int;
sourceRepArray varchar;
targetRepArray varchar;
BEGIN

	-- En caso de que no presente un dato, entonces omitir cualquier validación
	IF column_data IS NULL THEN
		RETURN 1;
	END IF;

    -- si la columna es AD_Language, omitir cualquier tipo de validacion
    SELECT INTO colName columnname FROM AD_Column WHERE AD_Column_ID = p_columnID;
    IF colName = 'AD_Language' THEN
        RETURN 1;   
    END IF;
   
    -- ver si el campo es una referencia
    select into targetTableName replication_get_referenced_table(p_columnID);

    -- si es una referencia, verificar el bitacoreo en la tabla referenciada
    IF targetTableName != '' THEN
       
		-- si el valor es 0, entonces es una referencia valida (seria como null para LY)
		IF column_data = '0' THEN
			return 1;
		END IF;

        -- recuperar el identificador de la tabla destino
        SELECT into targetTableID AD_Table_ID FROM AD_Table WHERE upper(tablename) = upper(targetTableName);
        -- recuperar el identificador de la tabla origen
        SELECT into sourceTableID AD_Table_ID FROM AD_Column WHERE AD_Column_ID = p_columnid;
           
        -- ver si la tabla destino es bitacoreada
        SELECT INTO hostID replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y';
        IF hostID IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF;

        -- si la tabla destino directamente no tiene la columna retrieveuid, entonces devolver que es invalido
        SELECT INTO isValid count(1) FROM information_schema.columns
            WHERE lower(column_name) = 'retrieveuid' AND lower(table_name) = lower(targetTableName);
        IF isValid = 0 THEN
            return 0;
        END IF;

		-- comparar el replicationArray de la tabla origen y de la tabla destino:
		-- unicamente si son iguales se podrá replicar, en caso contrario devolver que no
		SELECT INTO sourceRepArray replicationArray FROM AD_TableReplication where ad_table_id = sourceTableID;
		SELECT INTO targetRepArray replicationArray FROM AD_TableReplication where ad_table_id = targetTableID;

		-- si los repArray de las tablas origen y destino son iguales, entonces todo bien
		IF sourceRepArray = targetRepArray THEN
			return 1;
		END IF;

		-- si los repArray de las tablas origen y destino son diferentes (y la destino es diferente a 0),
		-- entonces no puede referenciarse el registro, ya que existirá en algunos hosts y en otros no.
		IF sourceRepArray <> targetRepArray AND targetRepArray <> '0' THEN
			return 0;
		END IF;

		-- si son diferentes porque se debe a que la tabla destino tiene 
		-- repArray = 0,habra que analizar el registro en cuestión:

        -- hay que ver si se esta referenciando a 
		-- 1) un registro ya existente en el core (retrieveuid debe iniciar con o),
		-- 2) o bien a un registro proveniente de otra sucursal (no generado localmente)
        EXECUTE 'select count(1) FROM ' || targetTableName ||
            ' WHERE ' || targetTableName || '_ID = ' || column_data::int ||
            ' AND ( retrieveuid ilike ''o%'' OR retrieveuid NOT ilike ''h' || hostID::varchar || '_%'' )' INTO isValid;       
       
        return isValid;
    END IF;
   
    return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

-- 20110104-13:17 Actualización de todos los contextos de uso configurados en instancias existentes como 'Ambos'.
UPDATE c_pospaymentmedium
SET context = 'B';

-- 20110104-13:18 El contexto de uso del medio de pago con tipo de medio de pago Crédito debe estar configurado como 'Sólo TPV'.
UPDATE c_pospaymentmedium
SET context = 'P'
WHERE tendertype = 'CR';

-- 20110104-13:20 Not null columna de contexto de uso. 
ALTER TABLE c_pospaymentmedium ALTER COLUMN context SET NOT NULL;

-- 20110104-13:21 Agregar columna para configurar el contexto de uso del esquema de descuento de la entidad comercial.
ALTER TABLE c_bpartner ADD COLUMN discountcontext character(1);

-- 20110104-13:23 Seteo del contexto de uso del esquema de descuento de entidad comercial en la Compra cuando existe un esquema de descuento configurado. 
UPDATE c_bpartner 
SET discountcontext = 'B' --Seteo todas a Bill ('Compra') 
WHERE m_discountschema_id IS NOT NULL;

-- 20110108-13:48 - correccion sobre funcion
CREATE OR REPLACE FUNCTION replication_is_valid_reference(p_columnid integer, column_data varchar)
RETURNS integer AS
$BODY$
DECLARE
targetTableName varchar;
targetTableID int;
sourceTableID int;
isValid int;
colName varchar;
hostID int;
sourceRepArray varchar;
targetRepArray varchar;
viewTable varchar;
BEGIN

	-- En caso de que no presente un dato, entonces omitir cualquier validación
	IF column_data IS NULL THEN
		RETURN 1;
	END IF;

    -- si la columna es AD_Language, omitir cualquier tipo de validacion
    SELECT INTO colName columnname FROM AD_Column WHERE AD_Column_ID = p_columnID;
    IF colName = 'AD_Language' THEN
        RETURN 1;   
    END IF;
   
    -- ver si el campo es una referencia
    select into targetTableName replication_get_referenced_table(p_columnID);

    -- si es una referencia, verificar el bitacoreo en la tabla referenciada
    IF targetTableName != '' THEN
       
    	-- verificar si la tabla destino es simplemente una vista
		select INTO viewTable isview from ad_table where tablename = targetTableName;
		IF viewTable = 'Y' THEN
			return 1;
		end if;
		
		-- si el valor es 0, entonces es una referencia valida (seria como null para LY)
		IF column_data = '0' THEN
			return 1;
		END IF;

        -- recuperar el identificador de la tabla destino
        SELECT into targetTableID AD_Table_ID FROM AD_Table WHERE upper(tablename) = upper(targetTableName);
        -- recuperar el identificador de la tabla origen
        SELECT into sourceTableID AD_Table_ID FROM AD_Column WHERE AD_Column_ID = p_columnid;
           
        -- ver si la tabla destino es bitacoreada
        SELECT INTO hostID replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y';
        IF hostID IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF;

        -- si la tabla destino directamente no tiene la columna retrieveuid, entonces devolver que es invalido
        SELECT INTO isValid count(1) FROM information_schema.columns
            WHERE lower(column_name) = 'retrieveuid' AND lower(table_name) = lower(targetTableName);
        IF isValid = 0 THEN
            return 0;
        END IF;

		-- comparar el replicationArray de la tabla origen y de la tabla destino:
		-- unicamente si son iguales se podrá replicar, en caso contrario devolver que no
		SELECT INTO sourceRepArray replicationArray FROM AD_TableReplication where ad_table_id = sourceTableID;
		SELECT INTO targetRepArray replicationArray FROM AD_TableReplication where ad_table_id = targetTableID;

		-- si los repArray de las tablas origen y destino son iguales, entonces todo bien
		IF sourceRepArray = targetRepArray THEN
			return 1;
		END IF;

		-- si los repArray de las tablas origen y destino son diferentes (y la destino es diferente a 0),
		-- entonces no puede referenciarse el registro, ya que existirá en algunos hosts y en otros no.
		IF sourceRepArray <> targetRepArray AND targetRepArray <> '0' THEN
			return 0;
		END IF;

		-- si son diferentes porque se debe a que la tabla destino tiene 
		-- repArray = 0,habra que analizar el registro en cuestión:

        -- hay que ver si se esta referenciando a 
		-- 1) un registro ya existente en el core (retrieveuid debe iniciar con o),
		-- 2) o bien a un registro proveniente de otra sucursal (no generado localmente)
        EXECUTE 'select count(1) FROM ' || targetTableName ||
            ' WHERE ' || targetTableName || '_ID = ' || column_data::int ||
            ' AND ( retrieveuid ilike ''o%'' OR retrieveuid NOT ilike ''h' || hostID::varchar || '_%'' )' INTO isValid;       
       
        return isValid;
    END IF;
   
    return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
  
-- mejoras en la respuesta a usuarios al momento de validar la incorporacion de una columna a la bitacora
  CREATE OR REPLACE FUNCTION replication_xml_append_column(columnid integer, columnvalue character varying)
  RETURNS character varying AS
$BODY$
DECLARE
    newValue varchar;
    nullY varchar;
    isValid int;
    v_columnname varchar;
    v_tablename varchar;
    v_tableid int; 
    v_existsValueField int;
    v_existsNameField int;
    v_valueDetail varchar;
    v_nameDetail varchar;
    v_referenceStr varchar;
    v_referencedTableStr varchar;
    v_referencedTableID int;
BEGIN
    SELECT INTO isValid replication_is_valid_reference(columnid, columnvalue);
    IF isValid = 0 THEN

	-- valores por defecto
	v_valueDetail := '';
	v_nameDetail := '';

	-- recuperar el nombre de la columna y tabla para brindar un mensaje mas intuitivo al usuario
	SELECT INTO v_columnname, v_tableid columnname, AD_Table_ID FROM AD_Column WHERE AD_Column_ID = columnid;
	SELECT INTO v_tablename tablename FROM AD_Table WHERE AD_Table_ID = v_tableid;

	BEGIN 
		-- recuperar el nombre o value del registro referenciado a fin de mejorar la legibilidad del mensaje de error
		SELECT INTO v_referencedTableStr replication_get_referenced_table(columnid);
		SELECT INTO v_referencedTableID AD_Table_ID FROM AD_Table WHERE tablename ilike v_referencedTableStr;

		SELECT INTO v_existsValueField Count(1) FROM AD_Column WHERE columnname ilike 'Value' AND AD_Table_ID = v_referencedTableID;
		SELECT INTO v_existsNameField Count(1) FROM AD_Column WHERE columnname ilike 'Name' AND AD_Table_ID = v_referencedTableID;

		-- cargar value y name
		IF v_existsValueField = 1 THEN
			EXECUTE 'SELECT value FROM ' || v_referencedTableStr || ' WHERE ' || v_columnname || ' = ' || columnvalue || '::int' INTO v_valueDetail;
			v_referenceStr := v_valueDetail;
		END IF;
		IF v_existsNameField = 1 THEN
			EXECUTE 'SELECT name FROM ' || v_referencedTableStr || ' WHERE ' || v_columnname || ' = ' || columnvalue || '::int' INTO v_nameDetail;
			v_referenceStr := v_referenceStr || '(' || v_nameDetail || ')';
		END IF;
	EXCEPTION
		WHEN OTHERS THEN
			-- do nothing
	END;
	
	-- concatenar acordemente para mensaje de retorno
        RAISE EXCEPTION 'Validacion de replicación - La columna: % (%) de la tabla: % (%) referencia al registro: % (%) fuera del sistema de replicacion', v_columnname, columnid, v_tablename, v_tableid, v_referenceStr, columnvalue;
    END IF;
   
    nullY := '';
    newValue := columnValue;
    IF newValue IS NULL THEN
        newValue :='';
        nullY := 'null="Y"';
    END IF;
   
    return '<column id="' || columnID::varchar || '" value="' || newValue || '" ' || nullY || '/>';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;

-- 20110110-16:07 - ampliacion a la vista para el informe de retenciones
DROP VIEW m_retencion_invoice_v;
CREATE OR REPLACE VIEW m_retencion_invoice_v AS 
 SELECT ri.m_retencion_invoice_id, ri.ad_client_id, ri.ad_org_id, ri.c_retencionschema_id, ri.c_invoice_id, ri.c_allocationhdr_id, ri.c_invoiceline_id, ri.c_invoice_retenc_id, ri.amt_retenc, ri.c_currency_id, ri.pagos_ant_acumulados_amt, ri.retenciones_ant_acumuladas_amt, ri.pago_actual_amt, ri.retencion_percent, ri.importe_no_imponible_amt, ri.base_calculo_percent, ri.issotrx, ri.baseimponible_amt, ri.importe_determinado_amt, rs.c_retenciontype_id, i.c_bpartner_id, rs.retencionapplication, bp.taxid, i.dateinvoiced
   FROM c_retencionschema rs
   JOIN c_retenciontype rt ON rs.c_retenciontype_id = rt.c_retenciontype_id
   JOIN m_retencion_invoice ri ON rs.c_retencionschema_id = ri.c_retencionschema_id
   JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id;

-- 20110119-13:32 - Tipo de contexto del descuento
ALTER TABLE m_discountschema ADD COLUMN discountcontexttype character(1) NOT NULL DEFAULT 'C'::bpchar;

-- 20110119-13:34 - Tipo de Documento General para créditos y débitos para descuentos y recargos
ALTER TABLE m_discountconfig ADD COLUMN creditdocumenttype character varying(4);
ALTER TABLE m_discountconfig ADD COLUMN debitdocumenttype character varying(4);

-- 20110119-13:35 - Tipo de Documento específicos para créditos y débitos para descuentos y recargos
ALTER TABLE m_discountconfig ADD COLUMN credit_doctype_id integer;
ALTER TABLE m_discountconfig ADD COLUMN debit_doctype_id integer;

-- 20110119-13:35 - Artículos de configuración para los documentos de crédito y débito para descuentos y recargos de esquemas de descuento de entidades comerciales 
ALTER TABLE m_discountconfig ADD COLUMN bpartner_discountproduct_id integer;
ALTER TABLE m_discountconfig ADD COLUMN bpartner_surchargeproduct_id integer;

-- 20110119-13:38 - Artículos de configuración para los documentos de crédito y débito para descuentos y recargos de esquemas de descuento de medios de pago
ALTER TABLE m_discountconfig ADD COLUMN paymentmedium_discountproduct_id integer;
ALTER TABLE m_discountconfig ADD COLUMN paymentmedium_surchargeproduct_id integer;

-- 20110119-13:39 - Artículos de configuración para los documentos de crédito y débito para descuentos y recargos de esquemas de vencimiento
ALTER TABLE m_discountconfig ADD COLUMN paymentterm_discountproduct_id integer;
ALTER TABLE m_discountconfig ADD COLUMN paymentterm_surchargeproduct_id integer;

-- 20110119-13:39 - Artículos de configuración para los documentos de crédito y débito para descuentos y recargos de cargos fijos de organización
ALTER TABLE m_discountconfig ADD COLUMN charge_discountproduct_id integer;
ALTER TABLE m_discountconfig ADD COLUMN charge_surchargeproduct_id integer;

-- 20110119-13:41 - Tipo de aplicación del descuento/recargo y días de gracia 2 (tolerancia al vencimiento) en esquemas de vencimiento y esquema de pagos
ALTER TABLE c_paymentterm ADD COLUMN discountapplicationtype character(1);
ALTER TABLE c_paymentterm ADD COLUMN discountapplicationtype2 character(1);
ALTER TABLE c_payschedule ADD COLUMN discountapplicationtype character(1);
ALTER TABLE c_paymentterm ADD COLUMN gracedays2 numeric(18,0) NOT NULL DEFAULT 0;

-- 20110119-13:42 - Monto de Cargo por cobro de facturas vencidas
ALTER TABLE ad_orginfo ADD COLUMN overdueinvoicescharge numeric(22,2) NOT NULL DEFAULT 0;

-- 20110119-13:43 - Situación actual del estado del crédito
ALTER TABLE c_bpartner ADD COLUMN secondarycreditstatus character varying(2) NOT NULL DEFAULT 'OK'::character varying;

-- 20110119-13:46 - Monto mínimo desde el cual se considera que el cliente tiene saldo 0, o sea, si el saldo < = monto mínimo se considera que tiene crédito
ALTER TABLE c_bpartner ADD COLUMN creditminimumamt numeric(22,2) NOT NULL DEFAULT 0;

-- 20110119-13:47 - Agrupa o no facturas la entidad comercial, esto significa que a la hora de cobrar no puede seleccionar qué facturas pagar sino que ingresando un monto se debe saldar desde la más vieja a las más nueva.
ALTER TABLE c_bpartner ADD COLUMN isgroupinvoices character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20110119-13:47 - Agregar un medio de pago a la tabla de esquemas de vencimientos para poder filtrarlos en el TPV
ALTER TABLE c_paymentterm ADD COLUMN c_pospaymentmedium_id integer;

-- 20110119-13:48 - Agregar medio de pago de la entidad comercial para poder filtrar los esquemas de vencimientos
ALTER TABLE c_bpartner ADD COLUMN c_pospaymentmedium_id integer;

-- 20110124-15:30 - Libro de Caja: Referencia a la Caja Diaria
ALTER TABLE C_Cash ADD COLUMN C_POSJournal_ID integer;
ALTER TABLE C_Cash
  ADD CONSTRAINT posjournal_cash FOREIGN KEY (C_POSJournal_ID)
  REFERENCES C_POSJournal (C_POSJournal_ID) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- 20110124-15:30 - Facturas: Referencia a la Caja Diaria
ALTER TABLE C_Invoice ADD COLUMN C_POSJournal_ID integer;
ALTER TABLE C_Invoice
  ADD CONSTRAINT posjournal_invoice FOREIGN KEY (C_POSJournal_ID)
  REFERENCES C_POSJournal (C_POSJournal_ID) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- 20110124-15:30 - Pagos: Referencia a la Caja Diaria
ALTER TABLE C_Payment ADD COLUMN C_POSJournal_ID integer;
ALTER TABLE C_Payment
  ADD CONSTRAINT posjournal_payment FOREIGN KEY (C_POSJournal_ID)
  REFERENCES C_POSJournal (C_POSJournal_ID) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- 20110124-15:30 - Asignaciones: Referencia a la Caja Diaria
ALTER TABLE C_AllocationHdr ADD COLUMN C_POSJournal_ID integer;
ALTER TABLE C_AllocationHdr
  ADD CONSTRAINT posjournal_allocationhdr FOREIGN KEY (C_POSJournal_ID)
  REFERENCES C_POSJournal (C_POSJournal_ID) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

-- 20110124-15:31 - Vista: Facturas emitidas en la Caja Diaria 
CREATE OR REPLACE VIEW c_posjournalinvoices_v AS 
 SELECT i.c_posjournal_id, ah.c_allocationhdr_id, ah.allocationtype, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.documentno, i.c_doctype_id, i.dateinvoiced, i.dateacct, i.c_bpartner_id, i.description, i.docstatus, i.processed, i.c_currency_id, i.grandtotal, sum(COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric(20,2)))::numeric(20,2) AS paidamt
   FROM c_invoice i
   LEFT JOIN c_allocationline al ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
  WHERE (ah.c_allocationhdr_id IS NULL OR (ah.allocationtype::text = ANY (ARRAY['STX'::character varying, 'MAN'::character varying, 'RC'::character varying]::text[]))) AND i.issotrx = 'Y'::bpchar
  GROUP BY i.documentno, i.c_posjournal_id, ah.c_allocationhdr_id, ah.allocationtype, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_doctype_id, i.dateinvoiced, i.dateacct, i.c_bpartner_id, i.description, i.docstatus, i.processed, i.c_currency_id, i.grandtotal
  ORDER BY i.documentno;
ALTER TABLE c_posjournalinvoices_v OWNER TO libertya;

-- 20110124-15:31 - Vista: Cobros de facturas emitidas en la Caja Diaria
CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
 SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
            WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
            WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
            ELSE NULL::character varying
        END::character varying(2) AS tendertype, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.documentno
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
            ELSE NULL::character varying
        END::character varying(30) AS documentno, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN p.description
            WHEN al.c_cashline_id IS NOT NULL THEN cl.description
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.description
            ELSE NULL::character varying
        END::character varying(255) AS description, 
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN ((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying
            WHEN al.c_cashline_id IS NOT NULL THEN ((c.name::text || '_#'::text) || cl.line)::character varying
            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
            ELSE NULL::character varying
        END::character varying(255) AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, ic.c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber
   FROM c_allocationline al
   LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  ORDER BY 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.tendertype::character varying
    WHEN al.c_cashline_id IS NOT NULL THEN 'CA'::character varying
    WHEN al.c_invoice_credit_id IS NOT NULL THEN 'CR'::character varying
    ELSE NULL::character varying
END::character varying(2), 
CASE
    WHEN al.c_payment_id IS NOT NULL THEN p.documentno
    WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
    ELSE NULL::character varying
END::character varying(30);
ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

-- 20110124-15:35 - Corrección de cobros
CREATE TABLE c_paymentfix
(
  c_paymentfix_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  documentno character varying(30) NOT NULL,
  description character varying(255),
  c_allocationhdr_id integer NOT NULL,
  c_invoice_id integer,
  voidedamt numeric(20,2) NOT NULL DEFAULT 0,
  allocatedamt numeric(20,2) NOT NULL DEFAULT 0,
  balance numeric(20,2) NOT NULL DEFAULT 0,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  process character(1),
  CONSTRAINT c_paymentfix_key PRIMARY KEY (c_paymentfix_id),
  CONSTRAINT allocationhdr_paymentfix FOREIGN KEY (c_allocationhdr_id)
      REFERENCES c_allocationhdr (c_allocationhdr_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT client_paymentfix FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT invoice_paymentfix FOREIGN KEY (c_invoice_id)
      REFERENCES c_invoice (c_invoice_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT org_paymentfix FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c_paymentfix OWNER TO libertya;

-- 20110124-15:35 - Línea de Corrección de cobros
CREATE TABLE c_paymentfixline
(
  c_paymentfixline_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_paymentfix_id integer NOT NULL,
  "action" character(1) NOT NULL,
  c_allocationline_id integer,
  documenttype character(1),
  c_payment_id integer,
  c_cashline_id integer,
  payamt numeric(20,2) NOT NULL DEFAULT 0,
  CONSTRAINT c_paymentfixline_key PRIMARY KEY (c_paymentfixline_id),
  CONSTRAINT allocationline_paymentfixline FOREIGN KEY (c_allocationline_id)
      REFERENCES c_allocationline (c_allocationline_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cashline_paymentfixline FOREIGN KEY (c_cashline_id)
      REFERENCES c_cashline (c_cashline_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT client_paymentfixline FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT org_paymentfixline FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT payment_paymentfixline FOREIGN KEY (c_payment_id)
      REFERENCES c_payment (c_payment_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT paymentfix_paymentfixline FOREIGN KEY (c_paymentfix_id)
      REFERENCES c_paymentfix (c_paymentfix_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c_paymentfixline OWNER TO libertya;

-- 20110124-15:36 - Activación de Cajas Diarias en la  Info de la Compañía
ALTER TABLE ad_clientinfo ADD COLUMN isposjournalactive character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20110125-16:30 - Tabla temporal de facturas
CREATE TABLE t_invoice
(
  ad_pinstance_id integer NOT NULL,
  c_invoice_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  issotrx character(1) NOT NULL DEFAULT 'Y'::bpchar,
  documentno character varying(30) NOT NULL,
  docstatus character(2) NOT NULL,
  docaction character(2) NOT NULL,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  posted character(1) NOT NULL DEFAULT 'N'::bpchar,
  c_doctype_id integer NOT NULL,
  c_doctypetarget_id integer NOT NULL,
  c_order_id integer,
  description character varying(255),
  isapproved character(1) NOT NULL DEFAULT 'Y'::bpchar,
  istransferred character(1) NOT NULL DEFAULT 'N'::bpchar,
  isprinted character(1) NOT NULL DEFAULT 'N'::bpchar,
  salesrep_id integer,
  dateinvoiced timestamp without time zone NOT NULL,
  dateprinted timestamp without time zone,
  dateacct timestamp without time zone NOT NULL,
  c_bpartner_id integer NOT NULL,
  c_bpartner_location_id integer NOT NULL,
  c_currency_id integer NOT NULL,
  paymentrule character varying(2) NOT NULL,
  c_paymentterm_id integer NOT NULL,
  c_charge_id integer,
  chargeamt numeric(20,2) DEFAULT 0,
  totallines numeric(20,2) NOT NULL DEFAULT 0,
  grandtotal numeric(20,2) NOT NULL DEFAULT 0,
  m_pricelist_id integer NOT NULL,
  istaxincluded character(1) NOT NULL DEFAULT 'N'::bpchar,
  c_campaign_id integer,
  c_project_id integer,
  ispaid character(1) NOT NULL DEFAULT 'N'::bpchar,
  c_cashline_id integer,
  c_conversiontype_id integer,
  ispayschedulevalid character(1) NOT NULL DEFAULT 'N'::bpchar,
  ref_invoice_id integer,
  c_letra_comprobante_id integer,
  datecai timestamp without time zone,
  numerocomprobante integer,
  puntodeventa integer,
  cai character varying(14),
  cuit character varying(20),
  numerodedocumento character varying(30),
  c_region_id integer,
  c_invoice_orig_id integer,
  nombrecli character varying(40),
  caja character varying(2),
  invoice_adress character varying(120),
  fiscalalreadyprinted character(1) NOT NULL DEFAULT 'N'::bpchar,
  nroidentificcliente character varying(120),
  cae character varying(14),
  vtocae timestamp without time zone,
  idcae character varying(15),
  caecbte integer,
  caeerror character varying(255),
  createcashline character(1) NOT NULL DEFAULT 'Y'::bpchar,
  iscopy character(1) NOT NULL DEFAULT 'N'::bpchar,
  authcode character varying(255),
  authmatch character(1) DEFAULT 'Y'::bpchar,
  doctypename character varying(100),
  doctypeprintname character varying(100),
  docbasetype character(3),
  CONSTRAINT adpinstance_tinvoice FOREIGN KEY (ad_pinstance_id)
      REFERENCES ad_pinstance (ad_pinstance_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (OIDS=FALSE);
ALTER TABLE t_invoice OWNER TO libertya;

-- 20110126-0922 : Corrección de Vista - M por margen 
CREATE OR REPLACE VIEW rv_c_invoiceline AS 
 SELECT i.c_order_id, i.c_currency_id, il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, i.c_bp_group_id, il.m_product_id, p.m_product_category_id, i.dateinvoiced, i.dateacct, i.issotrx, i.c_doctype_id, i.docstatus, il.qtyinvoiced * i.multiplier::numeric AS qtyinvoiced, il.qtyentered * i.multiplier::numeric AS qtyentered, il.m_attributesetinstance_id, productattribute(il.m_attributesetinstance_id) AS productattribute, pasi.m_attributeset_id, pasi.m_lot_id, pasi.guaranteedate, pasi.lot, pasi.serno, il.pricelist, il.priceactual, il.pricelimit, il.priceentered, 
        CASE
            WHEN il.pricelist = 0::numeric THEN 0::numeric
            ELSE round((il.pricelist - il.priceactual) / il.pricelist * 100::numeric, 2)
        END AS discount, 
        CASE
            WHEN costPrice.priceStd = 0::numeric THEN 0::numeric
            ELSE round((il.priceactual - costPrice.priceStd) / costPrice.priceStd * 100::numeric, 2)
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
   LEFT JOIN
   (
	SELECT pp.m_product_id, pp.pricestd
	FROM m_productprice pp 
	INNER JOIN m_pricelist_version plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id
	INNER JOIN m_pricelist pl ON plv.m_pricelist_id = pl.m_pricelist_id AND pl.issopricelist = 'N' AND pl.isdefault = 'Y'
   ) as costPrice ON p.m_product_id = costPrice.m_product_id;

-- 20110127-15:30 Restricción de lugar de retiro de artículos
ALTER TABLE c_doctype ADD COLUMN ischeckoutplacerestricted character(1) NOT NULL DEFAULT 'N'::bpchar;   

-- 20110128-15:15 Fecha de validez para presupuestos
ALTER TABLE c_order ADD COLUMN validto timestamp without time zone;
-- 20110128-15:15 Importe total de la línea del pedido (con impuestos)
ALTER TABLE c_orderline ADD COLUMN linetotalamt numeric(20,2) NOT NULL DEFAULT 0;

-- 20110131-11:23 Impresión fiscal remota
ALTER TABLE c_controlador_fiscal ADD COLUMN isremote character(1) NOT NULL DEFAULT 'N'::bpchar;

-- 20110202-14:23 Nuevos campos para la importación de precios 
ALTER TABLE I_ProductPrice ADD COLUMN m_discountschemaline_id integer;
ALTER TABLE I_ProductPrice ADD COLUMN previouspricelist numeric(22,4);
ALTER TABLE I_ProductPrice ADD COLUMN previouspricestd numeric(22,4);
ALTER TABLE I_ProductPrice ADD COLUMN previouspricelimit numeric(22,4);
ALTER TABLE I_ProductPrice ADD COLUMN stock numeric(22,4);
ALTER TABLE I_ProductPrice ADD COLUMN variationpricelist numeric(22,4);
ALTER TABLE I_ProductPrice ADD COLUMN variationpricestd numeric(22,4);
ALTER TABLE I_ProductPrice ADD COLUMN variationpricelimit numeric(22,4);
ALTER TABLE I_ProductPrice ADD COLUMN productupc varchar(30);
ALTER TABLE I_ProductPrice ADD COLUMN instanceupc varchar(30);
ALTER TABLE I_ProductPrice ADD COLUMN attribute1value varchar(30);
ALTER TABLE I_ProductPrice ADD COLUMN attribute2value varchar(30);

-- 20110203-11:15 Default value en el campo key
ALTER TABLE i_productprice
   ALTER COLUMN i_productprice_id SET DEFAULT nextval('seq_I_ProductPrice');
 ALTER TABLE i_productprice ADD COLUMN m_attributesetinstance_id int;
 
-- 20110216-12:06 Columna descripción y línea para línea de corrección de cobros
ALTER TABLE c_paymentfixline ADD COLUMN description character varying(255);
ALTER TABLE c_paymentfixline ADD COLUMN line numeric(18,0) NOT NULL DEFAULT 0;

-- 20110216-16:49 Columnas de importes para declaración de valores en caja
ALTER TABLE c_poscashstatement ADD COLUMN amount numeric(20,2) NOT NULL DEFAULT 0;
ALTER TABLE c_posjournal ADD COLUMN cashstatementamt numeric(20,2) NOT NULL DEFAULT 0;

-- 20110216-18:30 Libro de caja destino para transferir efectivo al cerrar la caja diaria
ALTER TABLE c_posjournal ADD COLUMN c_cashtarget_id integer;
 
-- 20110401-14:15 Quitar restriccion incorrecta
ALTER TABLE i_productprice ALTER COLUMN m_discountschemaline_id DROP NOT NULL;

-- 20110420-11:01 nuevos campos en ventana de remito
alter table m_inout add column inoutdate timestamp without time zone NULL;
alter table m_inout add column receptiondate timestamp without time zone NULL;
alter table m_inout add column inoutreceptiondate timestamp without time zone NULL;

-- 20110426-11:13 Incorporación de columna value a las líneas de artículos
ALTER TABLE m_product_lines ADD COLUMN value character varying(40);

-- 20110426-11:14 Actualización del value por el nombre
UPDATE m_product_lines
SET value = name;

-- 20110426-11:16 Seteo de la columna value a NOT NULL
ALTER TABLE m_product_lines ALTER COLUMN value SET NOT NULL;

-- 20110427-15:07 nuevos campo de configuracion de impresora fiscal
ALTER TABLE C_Controlador_Fiscal ADD COLUMN OnPrintProductFormat varchar(2) NOT NULL DEFAULT 'NV';
ALTER TABLE C_Controlador_Fiscal ADD COLUMN OnPrintUseProductReference char(1) NOT NULL DEFAULT 'N';  

-- 20110502-11:50 Campo para registrar el precio de costo para las lineas de facturas
ALTER TABLE c_invoiceline ADD COLUMN costprice numeric(20,2) NOT NULL DEFAULT 0;

-- 20110502-11:52 Campo para registrar el proveedor del producto de las lineas de facturas
ALTER TABLE c_invoiceline ADD COLUMN c_bpartner_vendor_id integer;

-- 20110516-13:59 Actualización de version a nivel BBDD. 
UPDATE ad_system SET version = '16-05-2011' WHERE ad_system_id = 0;

-- 20110524-14:22 Corrección del nombre de la columna Action en C_POSJournal. Se debe llamar DocAction
ALTER TABLE C_POSJournal RENAME COLUMN Action TO DocAction;

-- 20110524-17:38 Actualiza el contexto de uso a financieros a los esquemas de descuento que están relacionados a medios de pago y a planes de entidades financieras  donde el esquema de descuento se configura en esos planes y no en el propio medio de pago
UPDATE m_discountschema
SET discountcontexttype = 'F'
WHERE m_discountschema_id IN (SELECT m_discountschema_id
				FROM c_pospaymentmedium
				WHERE m_discountschema_id is not null
				UNION 
				SELECT plan.m_discountschema_id
				FROM c_pospaymentmedium as pm
				INNER JOIN m_entidadfinanciera as ef on pm.m_entidadfinanciera_id = ef.m_entidadfinanciera_id
				INNER JOIN m_entidadfinancieraplan as plan on plan.m_entidadfinanciera_id = ef.m_entidadfinanciera_id
				WHERE plan.m_discountschema_id is not null);

-- 20110526-09:15 Nueva tabla temporal para informe de saldos 
CREATE TABLE T_BalanceReport
(
	ad_pinstance_id integer NOT NULL,
	ad_client_id integer NOT NULL,
	ad_org_id integer NULL,
	subindice integer,
	c_bpartner_id integer,
	observaciones varchar(255),
	credit numeric(10,2) DEFAULT 0,
	debit numeric(10,2) DEFAULT 0,
	balance numeric(10,2) DEFAULT 0,
	date_oldest_open_invoice timestamp,
	date_newest_open_invoice timestamp,
	datecreated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
	sortcriteria varchar(2) null,
	scope varchar(2) null
);
			
-- 20110527-0936 - Detalle en informe de cuenta corriente
ALTER TABLE t_cuentacorriente ALTER COLUMN c_invoice_id DROP DEFAULT;
ALTER TABLE t_cuentacorriente ALTER COLUMN c_payment_id DROP DEFAULT;
alter table t_cuentacorriente add column c_cashline_id int null;
ALTER TABLE AD_PrintFormatItem ADD COLUMN OverrideValue varchar(100) null;

-- 20110527-1346 - Detalle en informe de imputaciones
ALTER TABLE T_Allocation_Report ADD COLUMN C_Invoice_ID INT NULL;
ALTER TABLE T_Allocation_Report ADD COLUMN C_Payment_ID INT NULL;
ALTER TABLE T_Allocation_Report ADD COLUMN C_CashLine_ID INT NULL;

-- 20110530-10:42 Actualización de version a nivel BBDD. 
UPDATE ad_system SET version = '30-05-2011' WHERE ad_system_id = 0;

-- 20110627-09:32 Nueva columna que almacena el monto abierto actual
alter table t_allocation_report add column currentopenamt numeric(20,2);

-- 20110628-16:22 Factura Electrónica. Nuevo campo para códigos de monedas de AFIP
ALTER TABLE c_currency ADD COLUMN WSFECode character(3);
-- 20110628-16:22 Valores para el campo WSFE de Currency.
update c_currency set WSFECode = 'PES' where ISO_Code = 'ARS';
update c_currency set WSFECode = 'DOL' where ISO_Code = 'USD';
update c_currency set WSFECode = '011' where ISO_Code = 'UYU';
update c_currency set WSFECode = '032' where ISO_Code = 'COP';
update c_currency set WSFECode = '018' where ISO_Code = 'CAD';
update c_currency set WSFECode = '060' where ISO_Code = 'EUR';
update c_currency set WSFECode = '012' where ISO_Code = 'BRL';
-- 20110628-16:33 Factura Electrónica. Nuevo campo para códigos de impuestos de AFIP
ALTER TABLE c_tax ADD COLUMN WSFECode integer;
-- 20110628-16:33 Factura Electrónica. Valores para el campo WSFE de Tax.
update c_tax set WSFECode = 3 where ad_componentobjectuid = 'CORE-C_Tax-1010087';
update c_tax set WSFECode = 4 where ad_componentobjectuid = 'CORE-C_Tax-1010086';
update c_tax set WSFECode = 5 where ad_componentobjectuid = 'CORE-C_Tax-1010085';

-- 20110629-16:26 Bug fix en la view v_documents: Se tomaba el join de factura y tipo de documento mediante el campo c_doctype_id, a diferencia de c_doctypetarget_id. Además se devolvía el estado de la caja a diferencia del de la línea de caja. 
CREATE OR REPLACE VIEW v_documents AS 
( SELECT 'C_Invoice' AS documenttable, i.c_invoice_id AS document_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_bpartner_id, i.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, i.documentno, i.issotrx, i.docstatus, 
        CASE
            WHEN i.c_invoicepayschedule_id IS NOT NULL THEN ips.duedate
            ELSE i.dateinvoiced
        END AS datetrx, i.dateacct, i.c_currency_id, i.c_conversiontype_id, abs(i.grandtotal) AS amount, i.c_invoicepayschedule_id, ips.duedate
   FROM c_invoice_v i
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   LEFT JOIN c_invoicepayschedule ips ON i.c_invoicepayschedule_id = ips.c_invoicepayschedule_id
UNION ALL 
 SELECT 'C_Payment' AS documenttable, p.c_payment_id AS document_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, p.c_bpartner_id, p.c_doctype_id, dt.signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, p.documentno, p.issotrx, p.docstatus, p.datetrx, p.dateacct, p.c_currency_id, p.c_conversiontype_id, p.payamt AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate
   FROM c_payment p
   JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id)
UNION ALL 
 SELECT 'C_CashLine' AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, 
        CASE
            WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id
            ELSE i.c_bpartner_id
        END AS c_bpartner_id, dt.c_doctype_id, 
        CASE
            WHEN cl.amount < 0.0 THEN 1
            ELSE -1
        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, 
        CASE
            WHEN cl.amount < 0.0 THEN 'N'::bpchar
            ELSE 'Y'::bpchar
        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname
      FROM c_doctype d
     WHERE d.doctypekey::text = 'CMC'::text) dt ON cl.ad_client_id = dt.ad_client_id
   LEFT JOIN c_invoice i ON cl.c_invoice_id = i.c_invoice_id;

ALTER TABLE v_documents OWNER TO libertya;

-- 20110629-16:30 Bug fix en la view c_invoice_v: Se tomaba el join de factura y tipo de documento mediante el campo c_doctype_id, a diferencia de c_doctypetarget_id.
CREATE OR REPLACE VIEW c_invoice_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, NULL::"unknown" AS c_invoicepayschedule_id, i.chargeamt * d.signo_issotrx::numeric AS chargeamt, i.totallines, i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal, d.signo_issotrx::numeric AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  WHERE i.ispayschedulevalid <> 'Y'::bpchar
UNION ALL 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, ips.c_invoicepayschedule_id, NULL::"unknown" AS chargeamt, NULL::"unknown" AS totallines, ips.dueamt AS grandtotal, d.signo_issotrx AS multiplier, 
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap, d.docbasetype
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
   JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE c_invoice_v OWNER TO libertya;

-- 20110629-16:32 Procedimiento Currency base pasado a plpgsql. Procedimiento proporcionado por Javier Ader .
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
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION libertya.currencybase(numeric, integer, timestamp with time zone, integer, integer) OWNER TO libertya;

-- 20110629-16:36 Procedimiento Currency convert pasado a plpgsql. Procedimiento proporcionado por Javier Ader .
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
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION libertya.currencyconvert(numeric, integer, integer, timestamp with time zone, integer, integer, integer) OWNER TO libertya;

-- 20110629-16:37 Procedimiento Currency rate pasado a plpgsql. Procedimiento proporcionado por Javier Ader .
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
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION libertya.currencyrate(integer,integer, timestamp with time zone, integer, integer, integer) OWNER TO libertya;

-- 20110629-16:38 Nuevo Procedimiento Currency round utilizado por las funciones currencyxxx pasadas a plpgsql. Procedimiento proporcionado por Javier Ader .
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
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION libertya.currencyround(numeric, integer, character varying) OWNER TO libertya;

-- 20110706-14:44 Nuevo Procedimiento round que toma numeric como parámetros y castea el segundo a integer. Procedimiento proporcionado por Javier Ader .
CREATE OR REPLACE FUNCTION libertya.round(numeric, numeric)
  RETURNS numeric AS
$BODY$
 BEGIN
	RETURN ROUND($1, cast($2 as integer));
 END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
ALTER FUNCTION libertya.round(numeric, numeric) OWNER TO libertya;

-- 20110706-15:17 Actualización de vista para que tome el docstatus de la línea de caja.
CREATE OR REPLACE VIEW c_op_vendor_payments_v AS 
( SELECT cp.ad_client_id, cp.c_bpartner_id, cp.datetrx, cp.payamt, cp.c_currency_id
   FROM c_payment cp
  WHERE cp.isreceipt = 'N'::bpchar AND (cp.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
UNION ALL 
 SELECT cl.ad_client_id, cl.c_bpartner_id, c.statementdate AS datetrx, abs(cl.amount) AS payamt, cl.c_currency_id
   FROM c_cashline cl
   JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
  WHERE cl.amount < 0::numeric AND (cl.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
UNION ALL 
 SELECT ri.ad_client_id, i.c_bpartner_id, i.dateinvoiced AS datetrx, ri.amt_retenc AS payamt, ri.c_currency_id
   FROM m_retencion_invoice ri
   JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id
  WHERE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]);

ALTER TABLE c_op_vendor_payments_v OWNER TO libertya;

-- 20110708-10:21 Actualización de version a nivel BBDD. 
UPDATE ad_system SET version = '31-05-2011' WHERE ad_system_id = 0;
-- ========================================================================================
-- FIN DE PREINSTALL FROM 10.09. 
-- ========================================================================================
