-- ========================================================================================
-- PREINSTALL FROM 22.0
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20220915-1036 Nuevos indices para informe de balance
CREATE INDEX fact_acct_dateacct_date ON fact_acct ((dateacct::date));
update ad_system set dummy = (SELECT addindexifnotexists('c_elementvalue_active', 'c_elementvalue', 'isactive', 'where isactive = ''Y'''));
update ad_system set dummy = (SELECT addindexifnotexists('t_acct_balance_ev', 't_acct_balance', 'c_elementvalue_id'));
update ad_system set dummy = (SELECT addindexifnotexists('t_acct_balance_pi', 't_acct_balance', 'ad_pinstance_id'));
update ad_system set dummy = (SELECT addindexifnotexists('t_acct_balance_hc', 't_acct_balance', 'HierarchicalCode'));

--20221111-1100 Compatibilidad con Postgres 12+.  
--				La version anterior de addindexifnotexists se basaba en columnas oid, las cuales no son soportaedas a partir de Postgres 12+.
--				De todas maneras las versiones recientes de postgres ya soportan el modificador IF NOT EXISTS,
--				con lo cual el uso de esta function en los preinstalls ya no sería necesario.
CREATE OR REPLACE FUNCTION addindexifnotexists(
    indexname character varying,
    tablename character varying,
    columnname character varying,
    whereclause character varying)
  RETURNS numeric AS
$BODY$
DECLARE
	existe integer;
	psqlversion integer;
BEGIN

	-- Validar version de postgres (9.4 o inferiores no soportan IF NOT EXIST en la creacion de indices)
	select into psqlversion substring((select version()), 12, (select position('.' in replace(lower((select version())), 'postgresql ', '')))-1)::integer;
	if (psqlversion < 10) then

		-- Creacion de indice mediante validacion de existencia previa 
		select into existe count(1)
		from
		    pg_class t,
		    pg_class i,
		    pg_index ix,
		    pg_attribute a
		where
		    t.oid = ix.indrelid
		    and i.oid = ix.indexrelid
		    and a.attrelid = t.oid
		    and a.attnum = ANY(ix.indkey)
		    and t.relkind = 'r'
		    and lower(i.relname) = lower(indexname)
		    and lower(t.relname) = lower(tablename)
		    and lower(a.attname) = lower(columnname);
	
		IF (existe = 0) THEN
			EXECUTE 'CREATE INDEX ' || indexname || 
				' ON ' || tablename || 
				' (' || columnname || ') ' || 
				whereclause;
			RETURN 1;
		END IF;
	
		RETURN 0;
	
	else
		-- Creacion de indice mediante nueva sintaxis soportada IF NOT EXISTS
		EXECUTE 'CREATE INDEX IF NOT EXISTS ' || indexname || ' ON ' || tablename || ' (' || columnname || ') ' || whereclause;
	end if;
	RETURN 1;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION addindexifnotexists(character varying, character varying, character varying, character varying)
  OWNER TO libertya;
   
--20230223-1158 Desactivacion de registros pendientes de replicacion con un age superior al umbral especificado
/** 
 * Desactiva registros si ya tienen cierto age considerable (a definir por el usuario)
 * 
 * Forma de uso:
 * 	select * from replication_disable_old_records(1010016, '1 month'); 
 * 
 * Desactivara todos los registros de compañia 0 y la definida por parametro
 * cuyo campo updated tenga una antiguedad mayor a un mes
 */
CREATE OR REPLACE FUNCTION replication_disable_old_records(p_clientid integer, p_age character varying)
  RETURNS int AS
$BODY$
DECLARE
	atable varchar;
	query varchar;
BEGIN
	FOR atable IN (select table_name from information_schema.columns where lower(column_name) = 'reparray' and table_schema = 'libertya' order by table_name)  loop
		begin
			query = 'UPDATE ' || atable || ' SET reparray = ''SET''||reparray, includeinreplication = ''D''  WHERE ad_client_id IN (0, ' || p_clientid || ') AND includeinreplication = ''Y'' and age(now(), updated) > ''' || p_age || '''';
			--raise notice '%', query;
			execute query;
		exception when others then
			-- probablemente el campo updated no existe en la tabla
			-- raise notice 'Error en tabla %', atable;
		end;
	END LOOP;
	return 0;
END
$BODY$
  LANGUAGE 'plpgsql' volatile;
  
  
--20230228-1016 Desactivacion de registros pendientes de replicacion con un age superior al umbral especificado. Version mejorada que incluye numero de registros modificados
/** 
 * Desactiva registros si ya tienen cierto age considerable (a definir por el usuario)
 * 
 * Forma de uso:
 * 	select * from replication_disable_old_records(1010016, '1 month'); 
 * 
 * Desactivara todos los registros de compañia 0 y la definida por parametro
 * cuyo campo updated tenga una antiguedad mayor a un mes
 */
CREATE OR REPLACE FUNCTION replication_disable_old_records(p_clientid integer, p_age character varying)
  RETURNS int AS
$BODY$
DECLARE
	atable varchar;
	query varchar;
	whereclause VARCHAR;
	cant int;
	totalrecords int;
begin
	totalrecords = 0;
	whereclause = ' WHERE ad_client_id IN (0, ' || p_clientid || ') AND includeinreplication = ''Y'' and age(now(), updated) > ''' || p_age || ''''; 
	
	FOR atable IN (    
		-- iterar por todas las tablas configuradas en replicacion
		SELECT lower(t.tablename)
		FROM ad_tablereplication tr
		INNER JOIN ad_table t ON tr.ad_table_id = t.ad_table_id
		UNION
		SELECT 'ad_changelog_replication'
	)
	loop
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
		begin
			query = query || 'reparray, includeinreplication = ''D'' ' || whereclause;  
			--raise notice '%', query;
			execute query;
		exception when others then
			-- probablemente el campo updated no existe en la tabla
			-- raise notice 'Error en tabla %', atable;
		end;
	END LOOP;
	return totalrecords;
END
$BODY$
  LANGUAGE 'plpgsql' volatile;

-- 20230316-1600 MERGE: 20220418-1000 Se agregan a las tablas C_Order, C_Invoice y M_InOut los campos necesarios para persistir los datos principales de la EC asociada, para evitar problemas al la hora de imprimir el documento cuando cuando se modifican datos de la EC.

update ad_system set dummy = (SELECT addcolumnifnotexists('C_Order','Direccion','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Order','Localidad','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Order','Provincia','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Order','CP','CHARACTER VARYING(8)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Order','Cat_IVA_ID','INTEGER'));

update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','Direccion','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','Localidad','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','Provincia','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','CP','CHARACTER VARYING(8)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Invoice','Cat_IVA_ID','INTEGER'));

update ad_system set dummy = (SELECT addcolumnifnotexists('M_InOut','NombreCli','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_InOut','NroIdentificCliente','CHARACTER VARYING(120)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_InOut','Direccion','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_InOut','Localidad','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_InOut','Provincia','CHARACTER VARYING(40)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_InOut','CP','CHARACTER VARYING(8)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('M_InOut','Cat_IVA_ID','INTEGER'));

-- 20230316-1600 MERGE: 20220510-0930 Fix para que se muestre correctamente la suma de alícuotas en Libro IVA Compras CPBT.
UPDATE e_electronicinvoiceref
SET codigo = 'C'
WHERE tabla_ref = 'TCOM' AND clave_busqueda = 'TIPO_CREDITO_FISCAL_COMPUTABLE';

-- 20230316-1600 MERGE: 20220601 Se agrega en la tabla C_Tax el campo isNoGravado para indicar si el impuesto es No Gravado.
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_tax','isNoGravado','character(1) NOT NULL DEFAULT ''N''::bpchar'));

-- 20230316-1600 MERGE: 20220715 Fix para que Otros Impuestos BsAs sea categorizado como Otros Impuestos y aparezca en el campo 'Otros Tributos'
UPDATE C_Tax
SET taxareatype = 'P'
WHERE name = 'Otros impuestos BsAs';

-- 20230316-1600 MERGE: 20220726 Rework para las funciones involucradas en las vistas reginfo para la exportación de libros IVA
CREATE OR REPLACE FUNCTION libertya.getimportenogravado(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ 
DECLARE v_Amount NUMERIC; 
BEGIN     
	SELECT COALESCE(SUM(CASE WHEN tc.ismanual = 'Y' THEN it.TaxAmt ELSE it.taxbaseamt END), 0) 
	INTO v_Amount     
	FROM c_invoicetax it 
	JOIN c_invoice i ON it.c_invoice_id = i.c_invoice_id 
	JOIN c_tax t ON t.c_tax_id = it.c_tax_id
	JOIN c_taxcategory tc ON tc.c_taxcategory_id = t.c_taxcategory_id
	WHERE it.c_invoice_id = p_c_invoice_id 
		AND t.isnogravado = 'Y' 
		AND t.ispercepcion = 'N';   
	RETURN v_Amount; 
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getimportenogravado(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.getimporteoperacionesexentas(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$
DECLARE v_Amount NUMERIC;
BEGIN     
	SELECT COALESCE(SUM(it.TaxBaseAmt), 0) INTO v_Amount
	FROM C_Invoicetax it
	INNER JOIN C_Tax t ON t.C_Tax_ID = it.C_Tax_ID
	WHERE C_Invoice_ID = p_c_invoice_id 
		AND t.IsTaxExempt = 'Y';
	RETURN v_Amount;
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getimporteoperacionesexentas(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.getperceptionamt(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ 
DECLARE v_Amount NUMERIC; 
BEGIN     
	SELECT COALESCE(SUM(it.TaxAmt), 0) INTO v_Amount     
	FROM c_invoicetax it 
	JOIN c_tax t ON t.c_tax_id = it.c_tax_id 
	INNER JOIN c_taxcategory tc ON t.c_taxcategory_id = tc.c_taxcategory_id 
	WHERE it.c_invoice_id = p_c_invoice_id 
		AND tc.ismanual = 'Y' 
		AND t.ispercepcion = 'Y';
	RETURN v_Amount; 
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getperceptionamt(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.gettaxamountbyperceptiontype(
    p_c_invoice_id integer,
    p_perception_type character)
  RETURNS numeric AS
$BODY$
DECLARE	v_Amount NUMERIC;
BEGIN
    SELECT COALESCE(SUM(it.TaxAmt), 0) INTO v_Amount
    FROM C_Invoicetax it
    WHERE C_Invoice_ID = p_c_invoice_id 
   		AND C_Tax_ID IN (SELECT C_Tax_ID FROM C_Tax WHERE perceptionType = p_perception_type);   
    RETURN v_Amount;
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.gettaxamountbyperceptiontype(integer, character)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.gettaxamountbyareatype(
    p_c_invoice_id integer,
    p_tax_area_type character)
  RETURNS numeric AS
$BODY$
DECLARE	v_Amount NUMERIC;
BEGIN
    SELECT COALESCE(SUM(it.TaxAmt), 0) INTO v_Amount
    FROM C_Invoicetax it
    -- No hay que tener en cuenta los impuestos de IIBB (perceptionType = 'B') ni IVA (perceptionType = 'I')
	WHERE C_Invoice_ID = p_c_invoice_id 
   		AND C_Tax_ID IN (	SELECT C_Tax_ID 
   						 	FROM C_Tax 
   						 	WHERE taxareatype = p_tax_area_type 
   								AND (perceptionType IS NULL OR perceptionType NOT IN ('B', 'I'))
   						);   
    RETURN v_Amount;
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.gettaxamountbyareatype(integer, character)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.getcantidadalicuotasiva(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$ 
DECLARE v_Cant NUMERIC;
BEGIN     
	SELECT COUNT(*)	INTO v_Cant
	FROM C_Invoicetax it
	INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)
	INNER JOIN C_TaxCategory tc ON (t.C_TaxCategory_ID = tc.C_TaxCategory_ID)
	WHERE
		it.C_Invoice_ID = p_c_invoice_id
		AND t.isPercepcion = 'N'
		AND t.isnogravado = 'N'
		AND t.istaxexempt = 'N'
		AND tc.ismanual = 'N';
	RETURN v_Cant;
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getcantidadalicuotasiva(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.getcodigooperacion(p_c_invoice_id integer)
  RETURNS character AS
$BODY$
DECLARE v_CodigoOperacion CHARACTER(1);   
BEGIN       
	SELECT 
		CASE 
			WHEN (COUNT(*) >= 1) THEN t.codigooperacion 
			ELSE NULL 
		END INTO v_CodigoOperacion       
	FROM (
		SELECT 
			CASE 
				WHEN it.taxamt = 0 AND t.rate <> 0 AND te.c_tax_id > 0 THEN te.codigooperacion 
				ELSE t.codigooperacion END AS codigooperacion 	 
		FROM C_Invoicetax it
		INNER JOIN C_Tax t ON (t.C_Tax_ID = it.C_Tax_ID)  	 
		LEFT JOIN (
					SELECT * 
					FROM c_tax 
					WHERE rate = 0 
						AND isactive = 'Y' 
						AND ispercepcion = 'N'
					) AS te ON te.ad_client_id = it.ad_client_id
		WHERE (C_Invoice_ID = p_c_invoice_id) 
			AND (t.rate = 0 OR (t.rate <> 0 AND it.taxamt = 0))
			AND (t.rate > 0 and it.taxamt > 0 or t.rate = 0 and it.taxamt = 0)
	) AS t  
	GROUP BY t.codigooperacion;       
   	RETURN v_CodigoOperacion;   
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getcodigooperacion(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.getimporteotrostributos(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$
DECLARE v_Amount NUMERIC;
BEGIN
    SELECT COALESCE(SUM(it.TaxAmt), 0)
    INTO v_Amount
    FROM C_Invoicetax it
    WHERE C_Invoice_ID = p_c_invoice_id 
    	AND C_Tax_ID IN (SELECT C_Tax_ID 
    					 FROM C_Tax 
						 WHERE taxareatype NOT IN ('N', 'M', 'I') 
							AND (perceptionType IS NULL OR perceptionType NOT IN ('B', 'I'))
						);   
    RETURN v_Amount;
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getimporteotrostributos(integer)
  OWNER TO libertya;

CREATE OR REPLACE FUNCTION libertya.getcreditofiscalcomputable(p_c_invoice_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    v_Amount NUMERIC;
	v_TipoCreditoFiscal TEXT;
BEGIN
    SELECT codigo INTO v_TipoCreditoFiscal
	FROM e_electronicinvoiceref
	WHERE tabla_ref = 'TCOM'
		AND clave_busqueda = 'TIPO_CREDITO_FISCAL_COMPUTABLE'
	LIMIT 1;
	
	IF (v_TipoCreditoFiscal = 'C'::TEXT) THEN
	    SELECT COALESCE(SUM(it.TaxAmt), 0) INTO v_Amount
		FROM c_invoicetax it
		JOIN c_tax t ON t.c_tax_id = it.c_tax_id
		JOIN c_taxcategory tc ON tc.c_taxcategory_id = t.c_taxcategory_id
		WHERE it.C_Invoice_ID = p_c_invoice_id
			AND t.isPercepcion = 'N'
			AND t.isnogravado = 'N'
			AND t.istaxexempt = 'N'
			AND tc.ismanual = 'N';
	ELSE
    	v_Amount := 0;
	END IF;
	RETURN v_Amount;
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getcreditofiscalcomputable(integer)
  OWNER TO libertya;

-- 20230316-1600 MERGE: 20220726 Rework de las vistas reginfo para la exportación de libros IVA

DROP VIEW IF EXISTS libertya.reginfo_compras_cbte_v;
DROP VIEW IF EXISTS libertya.reginfo_compras_cbte_importacion_v;
DROP VIEW IF EXISTS libertya.reginfo_compras_cbte_exportacion_v;

CREATE OR REPLACE VIEW libertya.reginfo_compras_cbte_v AS 
SELECT 
	i.ad_client_id,
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
    CASE
        WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN i.importclearance
        ELSE NULL::character varying
    END::character varying(30) AS despachoimportacion,
    bp.taxidtype AS codigodocvendedor,
    bp.taxid AS nroidentificacionvendedor,
    bp.name AS nombrevendedor,
    currencyconvert(getgrandtotal(i.c_invoice_id, true), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal,
    currencyconvert(getImporteNoGravado(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impconceptosnoneto,
	currencyconvert(getimporteoperacionesexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas,
    currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosvaloragregado,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac,
    currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos,
    cu.wsfecode AS codmoneda,
    currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio,
    CASE
        WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0::numeric
        ELSE getcantidadalicuotasiva(i.c_invoice_id)
    END AS cantalicuotasiva,
    getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion,
    CASE
        WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0::numeric
        ELSE currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)
    END::numeric(20,2) AS impcreditofiscalcomputable,
    currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos,
    NULL::character varying(20) AS cuitemisorcorredor,
    NULL::character varying(60) AS denominacionemisorcorredor,
    0::numeric(20,2) AS ivacomision
FROM c_invoice i
JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
JOIN c_categoria_iva ci ON bp.c_categoria_iva_id = ci.c_categoria_iva_id 
WHERE
	CASE
	    WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
	    ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
	END 
	AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) 
	AND (i.isactive = 'Y'::bpchar) 
	AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
	AND (dt.isfiscaldocument = 'Y'::bpchar)
	AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)
;

CREATE OR REPLACE VIEW libertya.reginfo_compras_cbte_importacion_v AS 
SELECT 
	i.ad_client_id,
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
    CASE
        WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN i.importclearance
        ELSE NULL::character varying
    END::character varying(30) AS despachoimportacion,
    bp.taxidtype AS codigodocvendedor,
    bp.taxid AS nroidentificacionvendedor,
    bp.name AS nombrevendedor,
    currencyconvert(getgrandtotal(i.c_invoice_id, true), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal,
    currencyconvert(getImporteNoGravado(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impconceptosnoneto,
	currencyconvert(getimporteoperacionesexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas,
    currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosvaloragregado,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac,
    currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos,
    cu.wsfecode AS codmoneda,
    currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio,
    CASE
        WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0::numeric
        ELSE getcantidadalicuotasiva(i.c_invoice_id)
    END AS cantalicuotasiva,
    getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion,
    CASE
        WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0::numeric
        ELSE currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)
    END::numeric(20,2) AS impcreditofiscalcomputable,
    currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos,
    NULL::character varying(20) AS cuitemisorcorredor,
    NULL::character varying(60) AS denominacionemisorcorredor,
    0::numeric(20,2) AS ivacomision
FROM c_invoice i
JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
JOIN c_categoria_iva ci ON bp.c_categoria_iva_id = ci.c_categoria_iva_id 
WHERE
	CASE
	    WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
	    ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
	END 
	AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) 
	AND i.isactive = 'Y'::bpchar 
	AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
	AND dt.isfiscaldocument = 'Y'::bpchar 
	AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)
	AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::TEXT
;
    
CREATE OR REPLACE VIEW libertya.reginfo_compras_cbte_exportacion_v AS 
SELECT 
	i.ad_client_id,
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
    CASE
        WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN i.importclearance
        ELSE NULL::character varying
    END::character varying(30) AS despachoimportacion,
    bp.taxidtype AS codigodocvendedor,
    bp.taxid AS nroidentificacionvendedor,
    bp.name AS nombrevendedor,
    currencyconvert(getgrandtotal(i.c_invoice_id, true), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imptotal,
    currencyconvert(getImporteNoGravado(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impconceptosnoneto,
	currencyconvert(getimporteoperacionesexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas,
    currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosvaloragregado,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac,
    currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos,
    cu.wsfecode AS codmoneda,
    currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio,
    CASE
        WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0::numeric
        ELSE getcantidadalicuotasiva(i.c_invoice_id)
    END AS cantalicuotasiva,
    getcodigooperacion(i.c_invoice_id)::character varying(1) AS codigooperacion,
    CASE
        WHEN (l.letra = ANY (ARRAY['B'::bpchar, 'C'::bpchar])) AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text THEN 0::numeric
        ELSE currencyconvert(getcreditofiscalcomputable(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)
    END::numeric(20,2) AS impcreditofiscalcomputable,
    currencyconvert(getimporteotrostributos(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impotrostributos,
    NULL::character varying(20) AS cuitemisorcorredor,
    NULL::character varying(60) AS denominacionemisorcorredor,
    0::numeric(20,2) AS ivacomision
FROM c_invoice i
JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
JOIN c_currency cu ON cu.c_currency_id = i.c_currency_id
JOIN c_categoria_iva ci ON bp.c_categoria_iva_id = ci.c_categoria_iva_id 
WHERE
	CASE
	    WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
	    ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
	END 
	AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) 
	AND i.isactive = 'Y'::bpchar 
	AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
	AND dt.isfiscaldocument = 'Y'::bpchar 
	AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)
	AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::TEXT
;

CREATE OR REPLACE VIEW libertya.reginfo_compras_alicuotas_v AS 
SELECT 
	i.ad_client_id,
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
WHERE 
    CASE
        WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
        ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
    END
    AND i.isactive = 'Y'::bpchar 
    AND t.ispercepcion = 'N'::bpchar
	AND t.istaxexempt = 'N'::bpchar
	AND t.isnogravado = 'N'::bpchar
    AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) 
    AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
    AND dt.isfiscaldocument = 'Y'::bpchar 
    AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) 
    AND (l.letra <> ALL (ARRAY['B'::bpchar, 'C'::bpchar])) 
    AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::text 
    AND (t.rate > 0::numeric AND it.taxamt > 0::numeric OR t.rate = 0::numeric AND it.taxamt = 0::numeric)
;      

CREATE OR REPLACE VIEW libertya.reginfo_compras_alicuotas_importacion_v AS 
SELECT 
	i.ad_client_id,
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
    CASE
        WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN i.importclearance
        ELSE NULL::character varying
    END::character varying(30) AS despachoimportacion,
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
WHERE
	CASE
	    WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
	    ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
	END 
	AND i.isactive = 'Y'::bpchar 
	AND t.ispercepcion = 'N'::bpchar
	AND t.istaxexempt = 'N'::bpchar
	AND t.isnogravado = 'N'::bpchar
	AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) 
	AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
	AND dt.isfiscaldocument = 'Y'::bpchar 
	AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)
	AND (t.rate > 0::numeric AND it.taxamt > 0::numeric OR t.rate = 0::numeric AND it.taxamt = 0::numeric)
	AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::TEXT
;

CREATE OR REPLACE VIEW libertya.reginfo_compras_alicuotas_exportacion_v AS 
SELECT 
	i.ad_client_id,
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
    CASE
        WHEN gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text = '66'::text THEN i.importclearance
        ELSE NULL::character varying
    END::character varying(30) AS despachoimportacion,
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
WHERE
    CASE
        WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
        ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
    END
    AND i.isactive = 'Y'::bpchar 
    AND t.ispercepcion = 'N'::bpchar
	AND t.istaxexempt = 'N'::bpchar
	AND t.isnogravado = 'N'::bpchar
    AND (i.issotrx = 'N'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'P'::bpchar) 
    AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
    AND dt.isfiscaldocument = 'Y'::bpchar 
    AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) 
    AND (l.letra <> ALL (ARRAY['B'::bpchar, 'C'::bpchar])) 
    AND (t.rate > 0::numeric AND it.taxamt > 0::numeric OR t.rate = 0::numeric AND it.taxamt = 0::numeric)
	AND gettipodecomprobante(dt.doctypekey, l.letra, i.issotrx, dt.transactiontypefrontliva)::text <> '66'::TEXT
;

CREATE OR REPLACE VIEW libertya.reginfo_ventas_cbte_v AS 
SELECT 
	i.ad_client_id,
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
    currencyconvert(getImporteNoGravado(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impconceptosnoneto,
    0::numeric(20,2) AS imppercepnocategorizados,
    currencyconvert(getimporteoperacionesexentas(i.c_invoice_id), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impopeexentas,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'N'::bpchar) + gettaxamountbyperceptiontype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepopagosdeimpunac,
    currencyconvert(gettaxamountbyperceptiontype(i.c_invoice_id, 'B'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepiibb,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'M'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS imppercepimpumuni,
    currencyconvert(gettaxamountbyareatype(i.c_invoice_id, 'I'::bpchar), i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(20,2) AS impimpuinternos,
    cu.wsfecode AS codmoneda,
    currencyrate(i.c_currency_id, 118, i.dateacct::timestamp with time zone, NULL::integer, i.ad_client_id, i.ad_org_id)::numeric(10,6) AS tipodecambio,
    CASE
        WHEN getimporteoperacionesexentas(i.c_invoice_id) <> 0::numeric THEN getcantidadalicuotasiva(i.c_invoice_id) - 1::numeric
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
	END 
	AND i.isactive = 'Y'::bpchar 
	AND (i.issotrx = 'Y'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'S'::bpchar) 
	AND (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) 
	AND dt.isfiscaldocument = 'Y'::bpchar 
	AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)
;
  
CREATE OR REPLACE VIEW libertya.reginfo_ventas_alicuotas_v AS 
SELECT
	i.ad_client_id,
	i.ad_org_id,
	i.c_invoice_id,
	date_trunc('day'::TEXT, i.dateinvoiced) AS date,
	date_trunc('day'::TEXT, i.dateinvoiced) AS fechadecomprobante,
	gettipodecomprobante(dt.doctypekey,
	l.letra,
	i.issotrx,
	dt.transactiontypefrontliva) AS tipodecomprobante,
	i.puntodeventa,
	i.numerocomprobante AS nrocomprobante,
	currencyconvert(it.taxbaseamt, 	i.c_currency_id, 118, i.dateacct::timestamp WITH time ZONE, NULL::integer, i.ad_client_id, i.ad_org_id)::NUMERIC(20, 2) AS impnetogravado,
	t.wsfecode AS alicuotaiva,
	currencyconvert(it.taxamt, i.c_currency_id, 118, i.dateacct::timestamp WITH time ZONE, NULL::integer, i.ad_client_id, i.ad_org_id)::NUMERIC(20, 2) AS impuestoliquidado
FROM c_invoice i
JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
LEFT JOIN c_letra_comprobante l ON l.c_letra_comprobante_id = i.c_letra_comprobante_id
JOIN c_invoicetax it ON i.c_invoice_id = it.c_invoice_id
JOIN c_tax t ON t.c_tax_id = it.c_tax_id
WHERE
	CASE
		WHEN i.issotrx = 'N'::bpchar THEN i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
		ELSE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar, 'RE'::bpchar, '??'::bpchar])
	END
	AND i.isactive = 'Y' 
	AND t.ispercepcion = 'N'
	AND t.istaxexempt = 'N'
	AND t.isnogravado = 'N'
	AND (i.issotrx = 'Y'::bpchar AND dt.transactiontypefrontliva IS NULL OR dt.transactiontypefrontliva = 'S'::bpchar)
	AND (dt.doctypekey::TEXT <> ALL (ARRAY['RTR'::CHARACTER VARYING::TEXT, 'RTI'::CHARACTER VARYING::TEXT, 'RCR'::CHARACTER VARYING::TEXT, 'RCI'::CHARACTER VARYING::TEXT]))
	AND dt.isfiscaldocument = 'Y'::bpchar
	AND (dt.isfiscal IS NULL OR dt.isfiscal = 'N'::bpchar OR dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar)
;

-- 20230717-1126 Fix al problema de lista vacia en seleccion de conjunto de atributos
DROP FUNCTION bompricelimit(integer, integer, integer);
CREATE OR REPLACE FUNCTION bompricelimit(
    pm_product_id integer,
    pm_pricelist_version_id integer,
    pm_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    IF (pm_attributesetinstance_id = 0) THEN
	select bomPriceLimit(PM_Product_ID,PM_PriceList_Version_ID) into i_price;
    else	
	SELECT pricelimit into i_price FROM M_ProductPriceInstance pi
    WHERE pi.M_PriceList_Version_ID=pm_priceList_version_ID AND pi.M_Product_ID=pm_product_id AND pi.M_AttributeSetInstance_ID=pm_attributesetinstance_id;
	IF (i_price ISNULL) THEN
		select bomPriceLimit(PM_Product_ID,PM_PriceList_Version_ID) into i_price;
	END IF;	
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION bompricelimit(integer, integer, integer)
  OWNER TO libertya;

DROP FUNCTION bompricelist(integer, integer, integer);
CREATE OR REPLACE FUNCTION bompricelist(
    pm_product_id integer,
    pm_pricelist_version_id integer,
    pm_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    IF (pm_attributesetinstance_id = 0) THEN
	select bomPriceList(PM_Product_ID,PM_PriceList_Version_ID) into i_price;
    else	
	SELECT pricelist into i_price FROM M_ProductPriceInstance pi
    WHERE pi.M_PriceList_Version_ID=pm_pricelist_version_id AND pi.M_Product_ID=pm_product_id AND pi.M_AttributeSetInstance_ID=pm_attributesetinstance_id;
	IF (i_price ISNULL) THEN
		select bomPriceList(PM_Product_ID,PM_PriceList_Version_ID) into i_price;
	END IF;	
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION bompricelist(integer, integer, integer)
  OWNER TO libertya;

DROP FUNCTION bompricestd(integer, integer, integer);
CREATE OR REPLACE FUNCTION bompricestd(
    pm_product_id integer,
    pm_pricelist_version_id integer,
    pm_attributesetinstance_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
    i_price NUMERIC;
BEGIN
    IF (pm_attributesetinstance_id = 0) THEN
	select bomPriceStd(PM_Product_ID,PM_PriceList_Version_ID) into i_price;
    else	
	SELECT pricestd into i_price FROM M_ProductPriceInstance pi
    WHERE pi.M_PriceList_Version_ID=pm_pricelist_version_id AND pi.M_Product_ID=pm_product_id AND pi.M_AttributeSetInstance_ID=pm_attributesetinstance_id;
	IF (i_price ISNULL) THEN
		select bomPriceStd(PM_Product_ID,PM_PriceList_Version_ID) into i_price;
	END IF;	
    END IF;
    return i_price;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION bompricestd(integer, integer, integer)
  OWNER TO libertya;
  
-- 20230728-1330 MERGE: Cuando la EC tiene configurado ocultar descuento linea FC (se agrega columna para configuracion)
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_bpartner', 'isocultardesctolineafc', 'character(1)'));

-- 20240229-0915 Nueva funcion para bajar registros de replicacion SIN modificar includeinreplication 
/** Pasa a 0 la posicion en el repArray a los registros pendientes a replicar si los mismos tienen  
    como pendiente el host p_hostpos, pero sin cambiar el campo includeinreplication a N */
CREATE OR REPLACE FUNCTION replication_lower_records_for_host(p_hostpos int)
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

	-- registros pendientes que que tienen pendiente el host p_hostpos (sin importar las demas posiciones)
	whereclause =   ' WHERE includeinreplication = ''Y'' ' ||
			' AND substring(reparray from ' || p_hostpos || ' for 1)  IN (''1'', ''3'', ''A'', ''a'') '; 	-- la posicion buscada debe ser uno de los estados de replicacion pendiente

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
		query = query ||'overlay(reparray placing ''0'' from ' || p_hostpos || ' for 1) ' || whereclause;
			
		--raise notice '%', query;
		EXECUTE query;
	END LOOP;
	return totalrecords;

END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE;
 
-- 20240412-0945 Los campos Usuario y Ventana de valores predeterminados eran de tipo Tabledir, lo cual complicaba su uso cuando el numero de entradas es elevado. Se cambian a Busqueda
-- Tipo de dato de AD_Preference.AD_User_ID pasa a ser Busqueda en lugar de TableDir
update ad_column set ad_reference_id = 30 where ad_componentobjectuid = 'CORE-AD_Column-1471' and ad_reference_id = 19;
-- Tipo de dato de AD_Preference.AD_Window_ID pasa a ser Busqueda en lugar de TableDir
update ad_column set ad_reference_id = 30 where ad_componentobjectuid = 'CORE-AD_Column-1757' and ad_reference_id = 19;


--MERGE Marca en facturas 2024-04-25
--20220817-1756 Se agrega entrada en AD_Preference para permitir seteo de 'print_mark_in_invoices' Y/N
INSERT INTO libertya.AD_Preference (ad_preference_id,isactive,ad_client_id,ad_org_id,createdby,updatedby,value,attribute,updated) 
VALUES ((select coalesce(max(ad_preference_id)+1,1) from libertya.AD_Preference where ad_preference_id < 100000),'Y',0,0,0,0,'N','print_mark_in_invoices',NOW());

--MERGE JACLBY23 upgrade_from_3.0 2024-05-06
-- 20230316 Fix en las retenciones de IIBB sufridas para que muestre correctamente el campo fecha (el cual es dateinvoiced, y no dateacct).

DROP VIEW libertya.rv_c_reten_iibb_sufridas;

CREATE OR REPLACE VIEW libertya.rv_c_reten_iibb_sufridas AS 
SELECT 
	i.ad_org_id,
  i.ad_client_id,
  r.jurisdictioncode AS codigojurisdiccion,
  replace(bp.taxid::text, '-'::text, ''::text) AS cuit,
  ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script,
  i.dateacct AS date,
  i.dateinvoiced,
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
  i.grandtotal::numeric(20,2) AS importeretenido,
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
WHERE rt.retentiontype = 'B'::bpchar 
	AND rs.retencionapplication::text = 'S'::text 
	AND (i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]));

-- 20230316 Modificación Reporte Informe de retenciones – Filtro por fecha y columnas

DROP VIEW libertya.m_retencion_invoice_v;

CREATE OR REPLACE VIEW libertya.m_retencion_invoice_v
AS 
SELECT
	DISTINCT ri.m_retencion_invoice_id,
	ri.ad_client_id,
	ri.ad_org_id,
	ri.c_retencionschema_id,
	ri.c_invoice_id,
	ri.c_allocationhdr_id,
	ri.c_invoiceline_id,
	ri.c_invoice_retenc_id,
	ri.amt_retenc,
	ri.c_currency_id,
	ri.pagos_ant_acumulados_amt,
	ri.retenciones_ant_acumuladas_amt,
	ri.pago_actual_amt,
	ri.retencion_percent,
	ri.importe_no_imponible_amt,
	ri.base_calculo_percent,
	ri.issotrx,
	ri.baseimponible_amt,
	ri.importe_determinado_amt,
	rs.c_retenciontype_id,
	i.c_bpartner_id,
	rs.retencionapplication,
	bp.taxid,
	i.dateinvoiced,
	i.dateacct,
	i.documentno AS retencion_documentno,
	iv.documentno,
	iv.dateinvoiced AS fecha,
	iv.grandtotal,
	iv.c_project_id,
	iv.totallines
FROM c_retencionschema rs
JOIN c_retenciontype rt ON rs.c_retenciontype_id = rt.c_retenciontype_id
JOIN m_retencion_invoice ri ON rs.c_retencionschema_id = ri.c_retencionschema_id
JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id
JOIN c_bpartner bp ON i.c_bpartner_id = bp.c_bpartner_id
JOIN c_allocationhdr a ON ri.c_allocationhdr_id = a.c_allocationhdr_id
JOIN c_allocationline al ON a.c_allocationhdr_id = al.c_allocationhdr_id
LEFT JOIN c_invoice iv ON iv.c_invoice_id = ((SELECT allo.c_invoice_id FROM c_allocationline allo WHERE allo.c_allocationhdr_id = al.c_allocationhdr_id ORDER BY allo.c_invoice_id DESC LIMIT 1))
WHERE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])
	AND i.dateinvoiced >= '2021-10-02'
ORDER BY
	ri.m_retencion_invoice_id,
	ri.ad_client_id,
	ri.ad_org_id,
	ri.c_retencionschema_id,
	ri.c_invoice_id,
	ri.c_allocationhdr_id,
	ri.c_invoiceline_id,
	ri.c_invoice_retenc_id,
	ri.amt_retenc,
	ri.c_currency_id,
	ri.pagos_ant_acumulados_amt,
	ri.retenciones_ant_acumuladas_amt,
	ri.pago_actual_amt,
	ri.retencion_percent,
	ri.importe_no_imponible_amt,
	ri.base_calculo_percent,
	ri.issotrx,
	ri.baseimponible_amt,
	ri.importe_determinado_amt,
	rs.c_retenciontype_id,
	i.c_bpartner_id,
	rs.retencionapplication,
	bp.taxid,
	i.dateinvoiced,
	i.dateacct,
	iv.documentno,
	iv.dateinvoiced,
	iv.grandtotal,
	iv.c_project_id,
	iv.totallines,
	i.documentno;

-- ### MERGE 2024-05-17 org.libertya.core.micro.r3019.dev.jacofer_11b_cot_arba upgrade_from_0.0
--20200109-1645 Hojas de Ruta
CREATE TABLE m_jacofer_roadmap
(
  m_jacofer_roadmap_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  documentno character varying(30) NOT NULL,
  movementdate timestamp without time zone NOT NULL,
  description character varying(255),
  total_packagesqty numeric(20,2) NOT NULL DEFAULT 0,
  total_capacity numeric(20,2) NOT NULL DEFAULT 0,
  total_weight numeric(20,2) NOT NULL DEFAULT 0,
  addinouts character(1),
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  processing character(1),
  docstatus character(2) NOT NULL,
  docaction character(2) NOT NULL,
  CONSTRAINT jacofer_roadmap_key PRIMARY KEY (m_jacofer_roadmap_id),
  CONSTRAINT jacofer_roadmap_fk_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT jacofer_roadmap_fk_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE m_jacofer_roadmap
  OWNER TO libertya;
 
CREATE TABLE m_jacofer_roadmapline
(
  m_jacofer_roadmapline_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  m_jacofer_roadmap_id integer NOT NULL,
  line numeric(18,0),
  m_inout_id integer NOT NULL,
  includeinout character(1) NOT NULL DEFAULT 'Y'::bpchar,
  processed character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT jacofer_roadmapline_key PRIMARY KEY (m_jacofer_roadmapline_id),
  CONSTRAINT jacofer_roadmapline_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT jacofer_roadmapline_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT jacofer_roadmapline_roadmap FOREIGN KEY (m_jacofer_roadmap_id)
      REFERENCES m_jacofer_roadmap (m_jacofer_roadmap_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE m_jacofer_roadmapline
  OWNER TO libertya;

update ad_system set dummy = (SELECT addcolumnifnotexists('m_inout','jacofer_packagesqty','numeric(20,2) NOT NULL DEFAULT 0'));
update ad_system set dummy = (SELECT addcolumnifnotexists('m_inout','jacofer_capacity','numeric(20,2) NOT NULL DEFAULT 0'));
update ad_system set dummy = (SELECT addcolumnifnotexists('m_inout','jacofer_weight','numeric(20,2) NOT NULL DEFAULT 0'));

-- ### MERGE 2024-05-17 org.libertya.core.micro.r3019.dev.jacofer_11b_cot_arba upgrade_from_0.0
-- 20220728 Se agregan los campos de Código Arba para las tablas M_Product, C_UOM y C_Region
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_Product','Jacofer_CodigoArba','VARCHAR(32)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_UOM','Jacofer_CodigoArba','VARCHAR(32)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_Region','Jacofer_CodigoArba','VARCHAR(32)'));

-- ### MERGE 2024-05-17 org.libertya.core.micro.r3019.dev.jacofer_11b_cot_arba upgrade_from_0.0
-- 20220728 Modificaciones en Transportistas para saber si es Transporte Propio.
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_Shipper','Jacofer_isTransportePropio','CHARACTER(1) NOT NULL DEFAULT ''N''::BPCHAR'));

-- ### MERGE 2024-05-17 org.libertya.core.micro.r3019.dev.jacofer_11b_cot_arba upgrade_from_0.0
-- 20220804 Función que obtiene un dato fijo dado un servicio externo y la key de un atributo.
CREATE OR REPLACE FUNCTION libertya.getexternalserviceattribute(
    p_externalservice_value character,
    p_externalserviceattributes_value character)
  RETURNS character AS
$BODY$
DECLARE	v_Name CHARACTER(30);
BEGIN
	SELECT esa.name INTO v_Name
	FROM c_externalserviceattributes esa
	INNER JOIN c_externalservice es ON es.c_externalservice_id = esa.c_externalservice_id 
	WHERE es.value = p_externalservice_value
		AND esa.value = p_externalserviceattributes_value;
    RETURN v_Name;
END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getexternalserviceattribute(character, character)
  OWNER TO libertya;
  



