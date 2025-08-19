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
  
-- ### MERGE 2024-05-17 org.libertya.core.micro.r3019.dev.jacofer_11b_cot_arba upgrade_from_1.0
CREATE TABLE libertya.m_shipper_vehicle (
    m_shipper_vehicle_id integer NOT NULL,
    ad_client_id integer NOT NULL,
    ad_org_id integer NOT NULL,
    isactive character(1) DEFAULT 'Y'::bpchar NOT NULL,
    created timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone NOT NULL,
    createdby integer NOT NULL,
    updated timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone NOT NULL,
    updatedby integer NOT NULL,
    m_shipper_id integer NOT NULL,
    vehicle_name character varying(40) NOT NULL,
    description character varying(255),
    license character varying(7) NOT NULL,
    trailer character(1) DEFAULT 'N'::bpchar NOT NULL
);

ALTER TABLE libertya.m_shipper_vehicle OWNER TO libertya;

ALTER TABLE ONLY libertya.m_shipper_vehicle
    ADD CONSTRAINT m_shipper_vehicle_key PRIMARY KEY (m_shipper_vehicle_id);




-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_0.0

ALTER TABLE c_invoice 
	ADD Cintolo_Adjustment_Clause character(1) NOT NULL DEFAULT 'N'::bpchar,
	ADD Cintolo_Exchange_Rate numeric(20,2),
	ADD cintolo_apply_exchange_dif character(1) NOT NULL DEFAULT 'N'::bpchar,
	ADD cintolo_exchange_dif_receipt INTEGER,
	ADD cintolo_Adjustment_Clause_Currency INTEGER;

ALTER TABLE c_bpartner
	ADD cintolo_point_of_sale INTEGER, -- Dif de cambio punto de venta
	ADD cintolo_percentage_limit numeric(20,2), -- Dif de cambio límite porcentual (Numero)
  	ADD cintolo_amount_limit numeric(20,2), -- Dif de cambio límite importe (Numero)
 	ADD cintolo_currency_limit INTEGER, -- Dif de cambio límite moneda (Ref: C_Currency)
 	ADD cintolo_acumulate_exchange_dif character(1) NOT NULL DEFAULT 'N'::bpchar, -- Acumular diferencia de cambio (SI/NO)
	ADD cintolo_checks_limit numeric(20,2); -- Limite de Cheques (Numerico, Para la pestaña Cliente)

-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_0.0

CREATE TABLE C_Cintolo_Exchange_Dif_Settings (
	C_Cintolo_Exchange_Dif_Settings_ID integer NOT NULL,
 	ad_client_id integer NOT NULL,
  	ad_org_id integer NOT NULL,
  	isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  	created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  	createdby integer NOT NULL,
  	updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  	updatedby integer NOT NULL,
  	
  	m_product_id INTEGER NOT NULL, -- Producto (TableDir)
  	point_of_sale INTEGER NOT NULL, -- Punto de Venta (Entero)
  	c_doctype_id INTEGER NOT NULL, -- Tipo Doc ajustes interno (TableDir) 
  	debit_pricelist INTEGER NOT NULL, -- Tarifa Debito (Ref: M_PriceList_ID)
  	credit_pricelist INTEGER NOT NULL, -- Tarifa Credito (Ref: M_PriceList_ID)
  	percentage_limit numeric(20,2) NOT NULL, -- Dif de cambio límite porcentual (Numero, Default: 3)
  	amount_limit numeric(20,2) NOT NULL, -- Dif de cambio límite importe (Numero, Default: 30)
 	c_currency_id INTEGER NOT NULL, -- Dif de cambio límite moneda (TableDir, Default: USD)
  
  	CONSTRAINT C_Cintolo_Exchange_Dif_Settings_Key PRIMARY KEY (C_Cintolo_Exchange_Dif_Settings_ID)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE C_Cintolo_Exchange_Dif_Settings
  OWNER TO libertya;
  
-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_0.0  
-- 20230913-1435 
CREATE OR REPLACE VIEW libertya.c_invoice_v
AS SELECT i.c_invoice_id,
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
    i.processing,
    i.processed,
    i.c_doctype_id,
    i.c_doctypetarget_id,
    i.c_order_id,
    i.description,
    i.isapproved,
    i.istransferred,
    i.salesrep_id,
    i.dateinvoiced,
    i.dateprinted,
    i.dateacct,
    i.c_bpartner_id,
    i.c_bpartner_location_id,
    i.ad_user_id,
    i.poreference,
    i.dateordered,
    i.c_currency_id,
    i.c_conversiontype_id,
    i.paymentrule,
    i.c_paymentterm_id,
    i.c_charge_id,
    i.m_pricelist_id,
    i.c_campaign_id,
    i.c_project_id,
    i.c_activity_id,
    i.isprinted,
    i.isdiscountprinted,
    i.ispaid,
    i.isindispute,
    i.ispayschedulevalid,
    NULL::integer AS c_invoicepayschedule_id,
    i.chargeamt * d.signo_issotrx::numeric AS chargeamt,
    i.totallines,
    i.grandtotal * d.signo_issotrx::numeric * d.signo_issotrx::numeric AS grandtotal,
    d.signo_issotrx::numeric AS multiplier,
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap,
    d.docbasetype,
    i.notexchangeablecredit,
    i.isexchange,
    i.initialcurrentaccountamt,
    i.dateacct AS duedate,
    i.m_authorizationchain_id,
    i.authorizationchainstatus,
    i.cintolo_adjustment_clause,
    i.cintolo_exchange_rate,
    i.cintolo_adjustment_clause_currency 
   FROM c_invoice i
     JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
  WHERE i.ispayschedulevalid <> 'Y'::bpchar
UNION ALL
 SELECT i.c_invoice_id,
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
    i.processing,
    i.processed,
    i.c_doctype_id,
    i.c_doctypetarget_id,
    i.c_order_id,
    i.description,
    i.isapproved,
    i.istransferred,
    i.salesrep_id,
    i.dateinvoiced,
    i.dateprinted,
    i.dateacct,
    i.c_bpartner_id,
    i.c_bpartner_location_id,
    i.ad_user_id,
    i.poreference,
    i.dateordered,
    i.c_currency_id,
    i.c_conversiontype_id,
    i.paymentrule,
    i.c_paymentterm_id,
    i.c_charge_id,
    i.m_pricelist_id,
    i.c_campaign_id,
    i.c_project_id,
    i.c_activity_id,
    i.isprinted,
    i.isdiscountprinted,
    i.ispaid,
    i.isindispute,
    i.ispayschedulevalid,
    ips.c_invoicepayschedule_id,
    NULL::numeric AS chargeamt,
    NULL::numeric AS totallines,
    ips.dueamt AS grandtotal,
    d.signo_issotrx AS multiplier,
        CASE
            WHEN "substring"(d.docbasetype::text, 2, 2) = 'P'::text THEN - 1::numeric
            ELSE 1::numeric
        END AS multiplierap,
    d.docbasetype,
    i.notexchangeablecredit,
    i.isexchange,
    i.initialcurrentaccountamt,
    ips.duedate,
    i.m_authorizationchain_id,
    i.authorizationchainstatus,
    i.cintolo_adjustment_clause,
    i.cintolo_exchange_rate,
    i.cintolo_adjustment_clause_currency 
   FROM c_invoice i
     JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id
     JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE i.ispayschedulevalid = 'Y'::bpchar AND ips.isvalid = 'Y'::bpchar;
  
-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_0.0
-- 20230914-1517
ALTER TABLE libertya.c_payment ADD cintolo_ref_invoiceline_id int4 NULL;
  


-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0
--20230825-1819 Campos para cuentas bancarias plazo fijo
ALTER TABLE IF EXISTS libertya.c_bankaccount
    ADD COLUMN fixedtermaccount character(1) NOT NULL DEFAULT 'N',
    ADD COLUMN c_charge_interest_id integer;
    
-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0
--20230828_0859 Tablas para registrar plazos fijos
CREATE TABLE IF NOT EXISTS libertya.c_fixedterm
(
    c_fixedterm_id integer NOT NULL,
    ad_client_id integer NOT NULL,
    ad_org_id integer NOT NULL,
    isactive character(1) COLLATE pg_catalog."default" NOT NULL DEFAULT 'Y'::bpchar,
    created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
    createdby integer NOT NULL,
    updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
    updatedby integer NOT NULL,
	trxdate timestamp without time zone NOT NULL,
	duedate timestamp without time zone NOT NULL,
	c_bank_id integer NOT NULL,
	c_bankaccount_id integer NOT NULL,
	c_bankaccountfixedterm_id integer NOT NULL,
    initialamount numeric(24,6) NOT NULL DEFAULT 0,
	term integer NOT NULL,
	tna numeric(24,6) NOT NULL DEFAULT 0,
	tnaterm integer NOT NULL,
	returnamt numeric(24,6) NOT NULL DEFAULT 0,
	retentionamt numeric(24,6),
	netamt numeric(24,6),
	tea numeric(24,6),
	certificate character varying(40),
	constitute character(1) NOT NULL DEFAULT 'N'::bpchar,
	constituted character(1) NOT NULL DEFAULT 'N'::bpchar,
	c_banktransferconstitution_id integer,
	accredit character(1) NOT NULL DEFAULT 'N'::bpchar,
	accredited character(1) NOT NULL DEFAULT 'N'::bpchar,
	c_paymentinterest_id integer,
	c_banktransferaccreditation_id integer,
    CONSTRAINT c_fixedterm_key PRIMARY KEY (c_fixedterm_id),
    CONSTRAINT cfixedterm_cbank FOREIGN KEY (c_bank_id)
        REFERENCES libertya.c_bank (c_bank_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
	CONSTRAINT cfixedterm_cbankaccount FOREIGN KEY (c_bankaccount_id)
        REFERENCES libertya.c_bankaccount (c_bankaccount_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
	CONSTRAINT cfixedterm_cbankaccountfixedterm FOREIGN KEY (c_bankaccountfixedterm_id)
        REFERENCES libertya.c_bankaccount (c_bankaccount_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
	CONSTRAINT cfixedterm_c_banktransferconstitution FOREIGN KEY (c_banktransferconstitution_id)
        REFERENCES libertya.c_banktransfer (c_banktransfer_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
	CONSTRAINT cfixedterm_c_banktransferaccreditation FOREIGN KEY (c_banktransferaccreditation_id)
        REFERENCES libertya.c_banktransfer (c_banktransfer_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
	CONSTRAINT cfixedterm_c_paymeninterest FOREIGN KEY (c_paymentinterest_id)
        REFERENCES libertya.c_payment (c_payment_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS libertya.c_fixedtermretention
(
    c_fixedtermretention_id integer NOT NULL,
    ad_client_id integer NOT NULL,
    ad_org_id integer NOT NULL,
    isactive character(1) COLLATE pg_catalog."default" NOT NULL DEFAULT 'Y'::bpchar,
    created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
    createdby integer NOT NULL,
    updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
    updatedby integer NOT NULL,
	c_fixedterm_id integer NOT NULL,
	c_retencionschema_id integer NOT NULL,
	retentionamt numeric(24,6),
	m_retencion_invoice_id integer,
    CONSTRAINT c_fixedtermretention_key PRIMARY KEY (c_fixedtermretention_id),
    CONSTRAINT cfixedtermretention_cfixedterm FOREIGN KEY (c_fixedterm_id)
        REFERENCES libertya.c_fixedterm (c_fixedterm_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
	CONSTRAINT cfixedtermretention_cretencionschema FOREIGN KEY (c_retencionschema_id)
        REFERENCES libertya.c_retencionschema (c_retencionschema_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
	CONSTRAINT cfixedtermretention_mretencioninvoice FOREIGN KEY (m_retencion_invoice_id)
        REFERENCES libertya.m_retencion_invoice (m_retencion_invoice_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0
--20230828_2150 Agrego columna Entidad Comercial en bancos para operaciones (depósitos / payments)
ALTER TABLE IF EXISTS libertya.c_bank ADD COLUMN c_bpartner_id integer; 

-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0
--20230828_2214 Agrego columna Moneda en los Plazos fijos, para validar con cuentas bancarias y para las operaciones generadas
ALTER TABLE IF EXISTS libertya.c_fixedterm ADD COLUMN c_currency_id integer NOT NULL DEFAULT 118;

-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0
--20230830_1229 Agrego columnas para referencia al recibo por retenciones y para los botones de anular
ALTER TABLE IF EXISTS libertya.c_fixedterm
	ADD COLUMN c_allocationretentionhdr_id integer,
    ADD COLUMN voidconstitute character(1) NOT NULL DEFAULT 'N',
    ADD COLUMN voidaccredit character(1) NOT NULL DEFAULT 'N';

-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0
--20230830_1829 Elimino array_agg de postgres 12 porque sino da error 
DROP AGGREGATE IF EXISTS libertya.array_agg(anyelement);

-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0    
--20230830_1858 Campos para importación extractos Galicia
ALTER TABLE IF EXISTS libertya.i_bankstatement
    ADD COLUMN origen character varying(40),
    ADD COLUMN balance numeric(20, 2);

-- ### MERGE 2024-06-03 org.libertya.core.micro.r3032.dev.cintolo upgrade_from_1.0
--20230831_0907 Campos para importación extractos Santander
ALTER TABLE IF EXISTS libertya.i_bankstatement
    ADD COLUMN sucursal character varying(60),
    ADD COLUMN codigooperativo character varying(30);
    

-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221108-14:00 secuencia para tabla de importacion de liquidacion de tarjetas
CREATE SEQUENCE libertya.seq_i_fideliusliquidaciones
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1000001;

-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221108-14:01 tabla de importacion de liquidacion de tarjetas	
CREATE TABLE libertya.i_fideliusliquidaciones (
	i_fideliusliquidaciones_id int4 NOT NULL DEFAULT nextval('seq_i_fideliusliquidaciones'::regclass),
	ad_client_id int4 NOT NULL,
	ad_org_id int4 NOT NULL,
	isactive bpchar(1) NOT NULL DEFAULT 'Y'::bpchar,
	created timestamp NOT NULL DEFAULT 'now'::text::timestamp(6) with time zone,
	createdby int4 NOT NULL,
	updated timestamp NOT NULL DEFAULT 'now'::text::timestamp(6) with time zone,
	updatedby int4 NOT NULL,
	i_errormsg varchar(2000) NULL,
	i_isimported bpchar(1) NOT NULL DEFAULT 'N'::bpchar,
	processing bpchar(1) NULL,
	processed bpchar(1) NULL DEFAULT 'N'::bpchar,
	id varchar(32) NULL,
	archivo_id varchar(32) NULL,
	empresa varchar(32) NULL,
	fpag varchar(32) NULL,
	fpres varchar(32) NULL,
	fant varchar(32) NULL,
	nroliq varchar(32) NULL,
	anticipo varchar(32) NULL,
	tarjeta varchar(32) NULL,
	bancopag varchar(32) NULL,
	num_com varchar(32) NULL,
	pciaiibb varchar(32) NULL,
	impbruto varchar(32) NULL,
	impneto varchar(32) NULL,
	totdesc varchar(32) NULL,
	promo varchar(32) NULL,
	arancel varchar(32) NULL,
	iva_arancel varchar(32) NULL,
	cfo_total varchar(32) NULL,
	cfo_21 varchar(32) NULL,
	cfo_105 varchar(32) NULL,
	cfo_adel varchar(32) NULL,
	iva_cfo21 varchar(32) NULL,
	iva_cfo105 varchar(32) NULL,
	iva_adel21 varchar(32) NULL,
	iva_total varchar(32) NULL,
	ret_iibb varchar(32) NULL,
	ret_ibsirtac varchar(32) NULL,
	ret_iva varchar(32) NULL,
	ret_gcia varchar(32) NULL,
	perc_iva varchar(32) NULL,
	perc_iibb varchar(32) NULL,
	ret_munic varchar(32) NULL,
	liq_anttn varchar(32) NULL,
	perc_1135tn varchar(32) NULL,
	dto_financ varchar(32) NULL,
	iva_dtofinanc varchar(32) NULL,
	deb_cred varchar(32) NULL,
	saldos varchar(32) NULL,
	otros_costos varchar(32) NULL,
	iva_otros varchar(32) NULL,
	plan_a1218 varchar(32) NULL,
	iva_plana1218 varchar(32) NULL,
	porc_ivaplana1218 varchar(32) NULL,
	cuit varchar(32) NULL,
	revisado varchar(32) NULL,
	CONSTRAINT fideliusliquidaciones_key PRIMARY KEY (i_fideliusliquidaciones_id),
	CONSTRAINT fideliusliquidaciones_unique_id UNIQUE (id)
);


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221108-14:01 foreign keys para tabla de importacion de cupones de tarjetas
ALTER TABLE libertya.i_fideliusliquidaciones ADD CONSTRAINT fideliusliquidacionesclient FOREIGN KEY (ad_client_id) REFERENCES libertya.ad_client(ad_client_id);
ALTER TABLE libertya.i_fideliusliquidaciones ADD CONSTRAINT fideliusliquidacionesorg FOREIGN KEY (ad_org_id) REFERENCES libertya.ad_org(ad_org_id);


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221108-14:02 secuencia para tabla de importacion de liquidacion de tarjetas
CREATE SEQUENCE libertya.seq_i_fideliuscupones
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1000001;
	
	
-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221108-14:02 tabla de importacion de cupones de tarjetas	
CREATE TABLE libertya.i_fideliuscupones (
	i_fideliuscupones_id int4 NOT NULL DEFAULT nextval('seq_i_fideliuscupones'::regclass),
	ad_client_id int4 NOT NULL,
	ad_org_id int4 NOT NULL,
	isactive bpchar(1) NOT NULL DEFAULT 'Y'::bpchar,
	created timestamp NOT NULL DEFAULT 'now'::text::timestamp(6) with time zone,
	createdby int4 NOT NULL,
	updated timestamp NOT NULL DEFAULT 'now'::text::timestamp(6) with time zone,
	updatedby int4 NOT NULL,
	i_errormsg varchar(2000) NULL,
	i_isimported bpchar(1) NOT NULL DEFAULT 'N'::bpchar,
	processing bpchar(1) NULL,
	processed bpchar(1) NULL DEFAULT 'N'::bpchar,
	id varchar(32) NULL,
	archivo_id varchar(32) NULL,
	empresa varchar(32) NULL,
	fvta varchar(32) NULL,
	fpag varchar(32) NULL,
	fant varchar(32) NULL,
	nroliq varchar(32) NULL,
	nroequipo varchar(32) NULL,
	nomequipo varchar(32) NULL,
	nrolote varchar(32) NULL,
	nrocupon varchar(32) NULL,
	tarjeta varchar(32) NULL,
	ult4tarjeta varchar(32) NULL,
	autorizacion varchar(32) NULL,
	cuotas varchar(32) NULL,
	imp_vta varchar(32) NULL,
	extra_cash varchar(32) NULL,
	num_com varchar(32) NULL,
	bancopag varchar(32) NULL,
	rechazo varchar(32) NULL,
	arancel varchar(32) NULL,
	iva_arancel varchar(32) NULL,
	cfo varchar(32) NULL,
	iva_cfo varchar(32) NULL,
	alic_ivacfo varchar(32) NULL,
	tipo_oper varchar(32) NULL,
	id_unico varchar(32) NULL,
	revisado varchar(32) NULL,
	is_reconciled bpchar(1) NULL DEFAULT 'N'::bpchar,
	CONSTRAINT fideliuscupones_key PRIMARY KEY (i_fideliuscupones_id),
	CONSTRAINT fideliuscupones_unique_id UNIQUE (id)
);


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221108-17:00 foreign keys para tabla de importacion de cupones de tarjetas
ALTER TABLE libertya.i_fideliuscupones ADD CONSTRAINT fideliuscuponesclient FOREIGN KEY (ad_client_id) REFERENCES libertya.ad_client(ad_client_id);
ALTER TABLE libertya.i_fideliuscupones ADD CONSTRAINT fideliuscuponesorg FOREIGN KEY (ad_org_id) REFERENCES libertya.ad_org(ad_org_id);

-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221117-15:30 se agrega columna ImportCupFidelius a la tabla C_CreditCardCouponFilter
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('libertya.C_CreditCardCouponFilter','importcuponesfidelius','character(1)'));


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221118-16:30 se agrega columna de ID de cupones libertya en liquidacion
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('libertya.I_FideliusCupones','c_couponssettlements_id','numeric'));

-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_0.0
--20221122-18:05 se agrega columna de ID liquidaciones en cupones
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('libertya.I_FideliusCupones','i_fideliusliquidaciones_id','numeric'));


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
--20240603-8:50 secuencia para cupones pendientes
CREATE SEQUENCE libertya.seq_i_fideliuspendientes
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1000001
	NO CYCLE;
	
-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
--20240603-8:50 cupones pendientes
CREATE TABLE libertya.i_fideliuspendientes (
	i_fideliuspendientes_id int4 DEFAULT nextval('seq_i_fideliuspendientes'::regclass) NOT NULL,
	ad_client_id int4 NOT NULL,
	ad_org_id int4 NOT NULL,
	isactive bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	created timestamp DEFAULT 'now'::text::timestamp(6) with time zone NOT NULL,
	createdby int4 NOT NULL,
	updated timestamp DEFAULT 'now'::text::timestamp(6) with time zone NOT NULL,
	updatedby int4 NOT NULL,
	i_errormsg varchar(2000) NULL,
	i_isimported bpchar(1) DEFAULT 'N'::bpchar NOT NULL,
	processing bpchar(1) NULL,
	processed bpchar(1) DEFAULT 'N'::bpchar NULL,
	id varchar(32) NULL,
	archivo_id varchar(32) NULL,
	fechaoper varchar(32) NULL,
	horaoper varchar(32) NULL,
	nroterminal varchar(32) NULL,
	equipo varchar(32) NULL,
	nombre_comerc varchar(32) NULL,
	tipotrx varchar(32) NULL,
	id_clover varchar(32) NULL,
	codcom varchar(32) NULL,
	nrolote varchar(32) NULL,
	ticket varchar(32) NULL,
	codaut varchar(32) NULL,
	factura varchar(32) NULL,
	tarjeta varchar(32) NULL,
	nrotarjeta varchar(32) NULL,
	cuota_tipeada varchar(32) NULL,
	importe varchar(32) NULL,
	montosec varchar(32) NULL,
	fechapagoest varchar(32) null,
	CONSTRAINT fideliuspendientes_key PRIMARY KEY (i_fideliuspendientes_id),
	CONSTRAINT fideliuspendientes_unique_id UNIQUE (id)
);

-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
--20240603-8:50 indices de cupones pendientes
ALTER TABLE libertya.i_fideliuspendientes ADD CONSTRAINT fideliuspendientesclient FOREIGN KEY (ad_client_id) REFERENCES libertya.ad_client(ad_client_id);
ALTER TABLE libertya.i_fideliuspendientes ADD CONSTRAINT fideliuspendientesorg FOREIGN KEY (ad_org_id) REFERENCES libertya.ad_org(ad_org_id);


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
--20240603-9:00 nombre de comercio
update ad_system set dummy = (SELECT addcolumnifnotexists('ad_orginfo','nombrecomercio','varchar(32)')); 


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
DROP INDEX IF EXISTS i_fideliusliquidaciones_nroliq_idx;
DROP INDEX IF EXISTS  i_fideliuscupones_nroliq_idx;
DROP INDEX IF EXISTS  i_fideliuscupones_nroliqfec_idx;
DROP INDEX IF EXISTS  i_fideliuspendientes_id_idx;
--20240710-09:00
DROP INDEX IF EXISTS  i_fideliuspendientes_match_idx;
DROP INDEX IF EXISTS  i_fideliuscupones_match_idx;


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
--20240708-13:00
CREATE INDEX i_fideliusliquidaciones_nroliq_idx ON libertya.i_fideliusliquidaciones (nroliq,num_com,tarjeta);
CREATE INDEX i_fideliuscupones_nroliq_idx ON libertya.i_fideliuscupones (nroliq,num_com,tarjeta,nrolote,extra_cash,nrocupon);
CREATE INDEX i_fideliuscupones_nroliqfec_idx ON libertya.i_fideliuscupones (nroliq,fvta,nrocupon,tarjeta,num_com);
CREATE INDEX i_fideliuspendientes_id_idx ON libertya.i_fideliuspendientes (id,codcom,nrotarjeta);
--20240710-09:00
CREATE INDEX i_fideliuspendientes_match_idx ON libertya.i_fideliuspendientes (fechaoper,importe,nrolote,ticket,cuota_tipeada);
CREATE INDEX i_fideliuscupones_match_idx ON libertya.i_fideliuscupones (fvta,imp_vta,nrolote,nrocupon,cuotas);


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
--20240711-14:00
update ad_system set dummy = (SELECT addcolumnifnotexists('c_couponssettlements','isrefused','character'));


-- ### MERGE 2024-07-30 org.libertya.core.micro.r3025.dev_fidelius upgrade_from_1.2
--20240712-13:30
INSERT INTO ad_preference  (ad_preference_id, ad_client_id, ad_org_id, createdby, updatedby, "attribute", value)
      VALUES (nextval('seq_ad_preference'), 1010016, 0, 0, 0, 'ImpCupPend_FromCierreTarjeta', 'Y'); 
INSERT INTO ad_preference  (ad_preference_id, ad_client_id, ad_org_id, createdby, updatedby, "attribute", value)
      VALUES (nextval('seq_ad_preference'), 1010016, 0, 0, 0, 'DaysFromBackCreditCardClose', '3'); 
INSERT INTO ad_preference  (ad_preference_id, ad_client_id, ad_org_id, createdby, updatedby, "attribute", value)
      VALUES (nextval('seq_ad_preference'), 1010016, 0, 0, 0, 'DaysToBackCreditCardClose', '0'); 
INSERT INTO ad_preference  (ad_preference_id, ad_client_id, ad_org_id, createdby, updatedby, "attribute", value)
      VALUES (nextval('seq_ad_preference'), 1010016, 0, 0, 0, 'DaysFromBackCreditCardCloseProcess', '3'); 



-- ### MERGE 2024-08-01 org.libertya.core.micro.r3000.dev.trxclover upgrade_from_0.0
--20230919-1818 agrega columnas para configuracion del POS - Clover 

update ad_system set dummy = (SELECT addcolumnifnotexists('C_POS','port_clover','numeric(10)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_POS','ip_clover','varchar(32)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_POS','authtoken_clover','varchar(255)'));

-- ### MERGE 2024-08-01 org.libertya.core.micro.r3000.dev.trxclover upgrade_from_0.0
--20230926-1706 agrega columnas para configuracion del POS - Clover 

update ad_system set dummy = (SELECT addcolumnifnotexists('C_POS','isintegra_clover','character'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_POS','isalter_clover','character'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_POS','isonline_clover','character'));

-- ### MERGE 2024-08-01 org.libertya.core.micro.r3000.dev.trxclover upgrade_from_0.0
-- 20230928-1552 agrega las columnas para los certificados Clover en los servicios externos
update ad_system set dummy = (SELECT addcolumnifnotexists('C_ExternalService','testcrt','bytea'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_ExternalService','productioncrt','bytea'));

-- ### MERGE 2024-08-01 org.libertya.core.micro.r3000.dev.trxclover upgrade_from_0.0
-- 20231003-1607 agrega columna del codigo Clover (Symbol) para las tarjetas
update ad_system set dummy = (SELECT addcolumnifnotexists('M_EntidadFinanciera','cardsymbolclover','varchar(30)'));

-- ### MERGE 2024-08-01 org.libertya.core.micro.r3000.dev.trxclover upgrade_from_0.0
-- 20231004-0900 Configurar valores por defecto de codigos Clover para las entidades financieras:
UPDATE M_EntidadFinanciera SET CardSymbolClover='V1' WHERE Name LIKE '%VISA%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='V3' WHERE Name LIKE '%ELECTRON%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='CA' WHERE Name LIKE '%CABAL%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='DI' WHERE Name LIKE '%DINERS%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='MC,M_' WHERE Name LIKE '%MASTERCARD%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='NA,NC' WHERE Name LIKE '%NATIVA%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='TN,NV' WHERE Name LIKE '%NARANJA%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='MA' WHERE Name LIKE '%MAESTRO%' AND IsActive='Y';
UPDATE M_EntidadFinanciera SET CardSymbolClover='AX,AM,X1,X3' WHERE Name LIKE '%AMEX%' AND IsActive='Y';


-- ### MERGE 2024-08-01 org.libertya.core.micro.r3000.dev.trxclover upgrade_from_1.0
-- 20240313-17:05
update ad_system set dummy = (SELECT addcolumnifnotexists('m_entidadfinanciera','numerocomercioclover','varchar(45)'));


-- ### MERGE 2024-08-01 org.libertya.core.micro.r2993.dev.import_facturas_proveedor_afip upgrade_from_0.0
-- tomar en cuenta que en produccion NO hay nada de esto instalado, pero si en preprod, por ende por ahora 
-- en preprod solo se agrega la columna:
-- update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','update_unpaid_invoices','character(1)'));


-- ### MERGE 2024-08-01 org.libertya.core.micro.r2993.dev.import_facturas_proveedor_afip upgrade_from_0.0
--20210921-1240 Nueva tabla para registrar la importación de facturas de proveedor AFIP
CREATE TABLE i_vendor_invoice_import
(
  i_vendor_invoice_import_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) DEFAULT 'Y'::bpchar,
  created timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer,
  updated timestamp without time zone DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer,
  c_invoice_id integer,
  i_isimported character(1) NOT NULL DEFAULT 'N'::bpchar,
  i_errormsg character varying(2000),
  processing character(1),
  processed character(1) DEFAULT 'N'::bpchar,
  fecha timestamp without time zone,
  tipocomprobante integer,
  puntodeventa integer,
  numerocomprobantedesde integer,
  numerocomprobantehasta integer,
  cae character varying(14),
  tipoidentificacion character(2),
  numeroidentificacion character varying(20),
  razonsocial character varying(60),
  tipocambio numeric(10,6),
  moneda character(3),
  netogravado numeric(20,2),
  netonogravado numeric(20,2),
  importeopexentas numeric(20,2),
  iva numeric(20,2),
  total numeric(20,2),
  CONSTRAINT i_vendor_invoice_import_key PRIMARY KEY (i_vendor_invoice_import_id),
  CONSTRAINT i_vendor_invoice_import_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT i_vendor_invoice_import_invoice FOREIGN KEY (c_invoice_id)
      REFERENCES c_invoice (c_invoice_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT i_vendor_invoice_import_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE i_vendor_invoice_import
  OWNER TO libertya;
  
  --Nuevas columna para configurar los proveedores que permiten precarga de facturas
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','allowpreloadvendorinvoices','character(1)'));
--Nueva columna para marcar las facturas precargadas
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice','preloadinvoice','character(1)'));

-- ### MERGE 2024-08-01 org.libertya.core.micro.r2993.dev.import_facturas_proveedor_afip upgrade_from_0.0
--20211016-2030 Eliminar la constraint para que se permita eliminar facturas en borrador
alter table i_vendor_invoice_import drop CONSTRAINT i_vendor_invoice_import_invoice;

-- ### MERGE 2024-08-01 org.libertya.core.micro.r2993.dev.import_facturas_proveedor_afip upgrade_from_0.0
--20220624-1030 Nueva columna para controlar el proceso de actualizacion de vencimientos
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','update_unpaid_invoices','character(1)'));



-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.0
--20230105-1636 agrega columnas para botones de gestion de comprobantes de ventas Reimpresion en Fiscal, Gestionar CAE 
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('C_Invoice','lyeimanageelectronicinvoice','character(1)'));
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('C_Invoice','managefiscalinvoice','character(1)'));

-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.0
--20230203-1811 agrega columnas para plan de contingencia CAEA (en configuracion de TPV)
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('C_POS','iscontingencia','character(1)'));
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('C_POS','ptovtacontingencia','numeric(10)'));

-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.1
--20230309-1656 agrega columna NO gravado para impuestos
update libertya.ad_system set dummy = (SELECT libertya.addcolumnifnotexists('c_tax','isnogravado','character(1)'));

-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.7
--20240115-17:30 Seteo propertie para configurar maxima cantidad de dias de comprobantes de venta en borrador 
INSERT INTO libertya.ad_preference (ad_preference_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_window_id, ad_user_id, "attribute", value)
VALUES(nextval('seq_ad_preference'), 1010016, 0, 'Y', current_timestamp, 100, current_timestamp, 100, NULL, NULL, 'Dias_ControlFCBorrador', '365');


-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.8
--20240315-1726 agrega columna para control de miPyme en facturacion
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','ismipyme','character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','mipymeupdated','timestamp'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','mipymeamount',' numeric(20, 2)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','validmipyme','character(1)'));

-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.8
--20240319-13:30 Seteo propertie para configurar si debe o no controlar FC MiPyme 
INSERT INTO libertya.ad_preference (ad_preference_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_window_id, ad_user_id, "attribute", value)
VALUES(nextval('seq_ad_preference'), 1010016, 0, 'Y', current_timestamp, 100, current_timestamp, 100, NULL, NULL, 'Validar_FC_MiPyme', 'Y');

-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.8
--2024-05-07 12:00
update ad_system set dummy = (SELECT addcolumnifnotexists('c_externalservice','validatecrt','varchar(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_externalservice','testcrtstatus','varchar(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_externalservice','productioncrtstatus','varchar(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_externalservice','homota','bytea'));
update ad_system set dummy = (SELECT addcolumnifnotexists('c_externalservice','prodta','bytea'));

-- ### MERGE 2024-8-13 org.libertya.core.micro.r3000.dev.facturacion 1.8
--20240507-18:00 Seteo propertie para configurar dias de vencimiento el control de FC MiPyme 
INSERT INTO libertya.ad_preference (ad_preference_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, ad_window_id, ad_user_id, "attribute", value)
VALUES(nextval('seq_ad_preference'), 1010016, 0, 'Y', current_timestamp, 100, current_timestamp, 100, NULL, NULL, 'DiasPlazoActMiPyme', '365');


-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_0.0
 --20231117-0953 Nueva vista para cuentas corrientes
CREATE OR REPLACE VIEW libertya.c_alldocumentscc_v AS  
SELECT  -- FACTURAS Y NOTAS DE CREDITO  
	c.c_currency_id,
	ps.dueamt AS amount,
	CASE WHEN i.issotrx = 'Y' 
		THEN 
			CASE 
				WHEN dt.signo_issotrx = 1 THEN ps.dueamt
				ELSE ps.dueamt - invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id)
			END
		ELSE 
			CASE 
				WHEN dt.signo_issotrx = 1 THEN ps.dueamt
				ELSE 0
			END
	END AS debit,
	CASE WHEN i.issotrx = 'Y' 
		THEN 
			CASE 
				WHEN dt.signo_issotrx = 1 THEN 0 
				ELSE ps.dueamt
			END
		ELSE 
			CASE 
				WHEN dt.signo_issotrx = 1 THEN ps.dueamt - invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id)
				ELSE ps.dueamt
			END
	END AS credit,
	dt.name AS tipo_doc,
	i.documentno,
	i.dateinvoiced AS datetrx,
	i.c_doctypetarget_id AS c_doctype_id,
	'C_Invoice' AS documenttable,
	i.c_invoice_id AS document_id,
	invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
	i.created AS created,
	i.c_bpartner_id,
	i.ad_org_id,
	i.ad_client_id,
	i.issotrx,
	ps.c_invoicepayschedule_id,
	ps.duedate 
FROM c_invoice i 
JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id 
JOIN c_currency c ON i.c_currency_id = c.c_currency_id 
JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id 
WHERE dt.doctypekey::TEXT <> ALL (ARRAY['RTR'::CHARACTER VARYING, 'RTI'::CHARACTER VARYING, 'RCR'::CHARACTER VARYING,'RCI'::CHARACTER VARYING]::TEXT[])
  AND i.processed = 'Y'
  AND i.docstatus IN ('CO','CL','VO')
UNION ALL 
SELECT DISTINCT -- RECIBOS/OP NORMALES
	ah.c_currency_id,
	ah.grandtotal AS amount,
	CASE 
		WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y' THEN 0
		ELSE ah.grandtotal
	END AS debit,
	CASE 
		WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y' THEN ah.grandtotal
		ELSE 0
	END AS credit,
	COALESCE(dt.name,'Imputación Manual') AS tipo_doc,
	ah.documentno,
	ah.datetrx,
	ah.c_doctype_id,
	'C_AllocationHdr' AS documenttable,
	ah.c_allocationhdr_id AS document_id,
	0 AS openamt,
	ah.created AS created,
	ah.c_bpartner_id,
	ah.ad_org_id,
	ah.ad_client_id,
	COALESCE(dt.issotrx, i.issotrx) AS issotrx,
	NULL::integer AS c_invoicepayschedule_id,
	NULL::timestamp without time zone AS duedate 
FROM c_allocationhdr ah
LEFT JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id 
JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id 
LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
WHERE ah.allocationtype IN ('RC','OP','MAN')
  AND ah.processed = 'Y'
  AND ah.docstatus IN ('CO','CL')
UNION ALL 
SELECT  -- RECIBOS/OP ADELANTADOS
	ah.c_currency_id,
	ah.grandtotal AS amount,
	CASE 
		WHEN dt.issotrx = 'Y' THEN COALESCE(sum(alf.montosaldado),0)
		ELSE ah.grandtotal
	END AS debit,
	CASE 
		WHEN dt.issotrx = 'Y' THEN ah.grandtotal
		ELSE COALESCE(sum(alf.montosaldado),0)
	END AS credit,
	dt.name || ' Adelantado' AS tipo_doc,
	ah.documentno AS documentno,
	ah.datetrx,
	ah.c_doctype_id,
	'C_AllocationHdr' AS documenttable,
	ah.c_allocationhdr_id AS document_id,
	ah.grandtotal - COALESCE(sum(alf.montosaldado),0) AS openamt,
	ah.created AS created,
	ah.c_bpartner_id,
	ah.ad_org_id,
	ah.ad_client_id,
	dt.issotrx,
	NULL AS c_invoicepayschedule_id,
	NULL AS duedate
FROM c_allocationhdr ah
JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id 
JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id 
LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id 
LEFT JOIN 
(SELECT li.*
 FROM c_allocation_detail_v li
 JOIN c_allocationhdr lh ON li.c_allocationhdr_id = lh.c_allocationhdr_id
 WHERE lh.processed = 'Y' AND lh.docstatus IN ('CO','CL')
) as alf ON (alf.c_payment_id = p.c_payment_id OR alf.c_cashline_id = cl.c_cashline_id) AND alf.c_allocationline_id != al.c_allocationline_id 
LEFT JOIN c_payment pf ON alf.c_payment_id = pf.c_payment_id 
LEFT JOIN c_cashline clf ON alf.c_cashline_id = clf.c_cashline_id 
WHERE ah.allocationtype IN ('RCA','OPA')
  AND ah.processed = 'Y'
  AND ah.docstatus IN ('CO','CL')
GROUP BY 
	ah.c_currency_id,
	ah.grandtotal,
	ah.documentno,
	ah.datetrx,
	ah.c_doctype_id,
	ah.c_allocationhdr_id,
	dt.name,
	dt.issotrx,
	ah.created,
	ah.c_bpartner_id,
	ah.ad_org_id,
	ah.ad_client_id,
	dt.issotrx 
UNION ALL 	
SELECT  -- MEDIOS DE COBRO / PAGO SIN IMPUTACION 
	p.c_currency_id,
	p.payamt AS amount,
	CASE 
		WHEN dt.issotrx = 'Y' THEN COALESCE(al.montosaldado,0)
		ELSE p.payamt
	END AS debit,
	CASE 
		WHEN dt.issotrx = 'Y' THEN p.payamt
		ELSE COALESCE(al.montosaldado,0)
	END AS credit,
	dt.name AS tipo_doc,
	p.documentno ,
	p.datetrx,
	p.c_doctype_id,
	'C_Payment' AS documenttable,
	p.c_payment_id AS document_id,
	p.payamt - COALESCE(al.montosaldado,0) AS openamt,
	p.created AS created,
	p.c_bpartner_id,
	p.ad_org_id,
	p.ad_client_id,
	dt.issotrx,
	NULL AS c_invoicepayschedule_id,
	NULL AS duedate
FROM c_payment p 
JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id 
LEFT JOIN (
	SELECT 
		d.c_payment_id,
		sum(d.montosaldado) AS montosaldado,
		count(aha.c_allocationhdr_id) AS cant
	FROM c_allocation_detail_v d 
	LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND aha.allocationtype IN ('RCA','OPA') AND aha.processed = 'Y' AND aha.docstatus IN ('CO','CL')
	LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND ah.allocationtype NOT IN ('RCA','OPA') AND ah.processed = 'Y' AND ah.docstatus IN ('CO','CL') 
	GROUP BY 
		d.c_payment_id
) AS al ON al.c_payment_id = p.c_payment_id 
WHERE p.processed = 'Y'
  AND p.docstatus IN ('CO','CL')
  AND (al.c_payment_id IS NULL OR (al.montosaldado > 0 AND al.montosaldado < p.payamt))
  AND (al.cant IS NULL OR al.cant = 0)
UNION ALL 
SELECT  -- LINEAS DE CAJA SIN IMPUTACION 
	p.c_currency_id,
	abs(p.amount) AS amount,
	CASE 
		WHEN p.amount > 0 THEN COALESCE(al.montosaldado,0)
		ELSE p.amount 
	END AS debit,
	CASE 
		WHEN p.amount > 0 THEN p.amount
		ELSE COALESCE(al.montosaldado,0)
	END AS credit,
	'Linea de caja' AS tipo_doc,
	c.name AS documentno,
	c.statementdate AS datetrx,
	NULL AS c_doctype_id,
	'C_CashLine' AS documenttable,
	p.c_cashline_id AS document_id,
	ABS(p.amount) - COALESCE(al.montosaldado,0) AS openamt,
	p.created AS created,
	p.c_bpartner_id,
	p.ad_org_id,
	p.ad_client_id,
	CASE WHEN p.amount > 0 THEN 'Y' ELSE 'N' END AS issotrx,
	NULL AS c_invoicepayschedule_id,
	NULL AS duedate
FROM c_cashline p 
JOIN c_cash c ON p.c_cash_id = c.c_cash_id
LEFT JOIN (
	SELECT 
		d.c_cashline_id,
		sum(d.montosaldado) AS montosaldado,
		count(aha.c_allocationhdr_id) AS cant
	FROM c_allocation_detail_v d 
	LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND aha.allocationtype IN ('RCA','OPA') AND aha.processed = 'Y' AND aha.docstatus IN ('CO','CL')
	LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND ah.allocationtype NOT IN ('RCA','OPA') AND ah.processed = 'Y' AND ah.docstatus IN ('CO','CL') 
	GROUP BY 
		d.c_cashline_id
) AS al ON al.c_cashline_id = p.c_cashline_id  
WHERE p.processed = 'Y'
  AND p.docstatus IN ('CO','CL')
  AND (al.c_cashline_id IS NULL OR al.montosaldado < abs(p.amount))
  AND (al.cant IS NULL OR al.cant = 0)
;

-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_0.0
--20231128-1633 ID Moneda en T_CuentaCorriente
ALTER TABLE libertya.t_cuentacorriente ADD c_currency_id integer NULL;

-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_0.0
--20231129-2053 ID Moneda en T_BalanceReport
ALTER TABLE libertya.t_balancereport ADD c_currency_id integer NULL;

-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_0.0
--20231130-1022 Ajuste vista rv_invoice del reporte estado de cuenta
CREATE OR REPLACE VIEW libertya.rv_c_invoice
AS SELECT DISTINCT ON (i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.isprinted, i.isdiscountprinted, i.processing, i.processed, i.istransferred, i.ispaid, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, b.c_bp_group_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.ispayschedulevalid, loc.c_country_id, loc.c_region_id, loc.postal, loc.city, i.c_charge_id, (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.chargeamt * (- 1::numeric)
            ELSE i.chargeamt
        END), (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.totallines * (- 1::numeric)
            ELSE i.totallines
        END), (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.grandtotal * (- 1::numeric)
            ELSE i.grandtotal
        END), (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN '-1'::integer
            ELSE 1
        END)) i.c_invoice_id,
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
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN '-1'::integer
            ELSE 1
        END AS multiplier,
    COALESCE(mio1.documentno, mio2.documentno, mio3.documentno) AS documentno_inout,
    COALESCE(mio1.movementdate, mio2.movementdate, mio3.movementdate) AS movementdate_inout
   FROM c_invoice i
     JOIN c_doctype d ON i.c_doctype_id = d.c_doctype_id
     JOIN c_bpartner b ON i.c_bpartner_id = b.c_bpartner_id
     JOIN c_bpartner_location bpl ON i.c_bpartner_location_id = bpl.c_bpartner_location_id
     JOIN c_location loc ON bpl.c_location_id = loc.c_location_id
     LEFT JOIN c_invoiceline il ON il.c_invoice_id = i.c_invoice_id
     LEFT JOIN m_inoutline miol1 ON miol1.c_orderline_id = il.c_orderline_id
     LEFT JOIN c_orderline ol1 ON ol1.c_orderline_id = il.c_orderline_id
     LEFT JOIN c_order o1 ON o1.c_order_id = ol1.c_order_id
     LEFT JOIN m_inout mio3 ON mio3.c_order_id = o1.c_order_id
     LEFT JOIN m_inout mio1 ON mio1.c_order_id = i.c_order_id
     LEFT JOIN m_inoutline miol2 ON miol2.m_inoutline_id = il.m_inoutline_id
     LEFT JOIN m_inout mio2 ON mio2.m_inout_id = miol2.m_inout_id
  WHERE ((i.issotrx = 'Y' AND i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar, 'VO'::bpchar])) OR (i.issotrx = 'N' AND i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
  ORDER BY i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.isprinted, i.isdiscountprinted, i.processing, i.processed, i.istransferred, i.ispaid, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, b.c_bp_group_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.ispayschedulevalid, loc.c_country_id, loc.c_region_id, loc.postal, loc.city, i.c_charge_id, (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.chargeamt * (- 1::numeric)
            ELSE i.chargeamt
        END), (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.totallines * (- 1::numeric)
            ELSE i.totallines
        END), (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.grandtotal * (- 1::numeric)
            ELSE i.grandtotal
        END), (
        CASE
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN '-1'::integer
            ELSE 1
        END), mio1.documentno DESC, mio1.movementdate DESC, mio2.documentno DESC, mio2.movementdate DESC, mio3.documentno DESC, mio3.movementdate DESC;


-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_2.0
--20240123-1038 Ajustes en la vista Cta. Cte. según feedback Roberto
DROP VIEW libertya.c_alldocumentscc_v;

CREATE OR REPLACE VIEW libertya.c_alldocumentscc_v
AS SELECT c.c_currency_id,
    ps.dueamt AS amount,
        CASE
            WHEN dt.signo_issotrx = 1 THEN ps.dueamt
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 0::numeric
            ELSE ps.dueamt
        END AS credit,
    dt.name AS tipo_doc,
    i.documentno,
    i.dateinvoiced AS datetrx,
    i.dateacct,
    i.c_doctypetarget_id AS c_doctype_id,
    'C_Invoice'::text AS documenttable,
    i.c_invoice_id AS document_id,
    invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
    i.created,
    i.c_bpartner_id,
    i.ad_org_id,
    i.ad_client_id,
    i.issotrx,
    ps.c_invoicepayschedule_id,
    ps.duedate
   FROM c_invoice i
     JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
     JOIN c_currency c ON i.c_currency_id = c.c_currency_id
     JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id
  WHERE (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND i.processed = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (dt.issotrx = 'Y'::bpchar AND dt.isfiscaldocument = 'Y' AND i.docstatus = 'VO'::bpchar))
UNION ALL
 SELECT DISTINCT ah.c_currency_id,
    ah.grandtotal - COALESCE(alc.creditamt,0) AS amount,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar THEN 0::numeric
            ELSE ah.grandtotal - COALESCE(alc.creditamt,0)
        END AS debit,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar THEN ah.grandtotal - COALESCE(alc.creditamt,0)
            ELSE 0::numeric
        END AS credit,
    COALESCE(dt.name, 'Imputación Manual'::character varying) AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    0 AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    COALESCE(dt.issotrx, i.issotrx) AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN (SELECT c_allocationhdr_id, c_invoice_credit_id, sum(amount) AS creditamt FROM c_allocationline GROUP BY c_allocationhdr_id, c_invoice_credit_id) as alc ON alc.c_allocationhdr_id = ah.c_allocationhdr_id AND alc.c_invoice_credit_id IS NOT NULL
     LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RC'::character varying::text, 'OP'::character varying::text, 'MAN'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
UNION ALL
 SELECT ah.c_currency_id,
    ah.grandtotal AS amount,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN COALESCE(sum(alf.montosaldado), 0::numeric)
            ELSE ah.grandtotal
        END AS debit,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN ah.grandtotal
            ELSE COALESCE(sum(alf.montosaldado), 0::numeric)
        END AS credit,
    dt.name::text || ' Adelantado'::text AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    ah.grandtotal - COALESCE(sum(alf.montosaldado), 0::numeric) AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
     LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
     LEFT JOIN ( SELECT *
           FROM c_allocation_detail_v li
             JOIN c_allocationhdr lh ON li.c_allocationhdr_id = lh.c_allocationhdr_id
          WHERE lh.processed = 'Y'::bpchar AND (lh.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) alf ON (alf.c_payment_id = p.c_payment_id OR alf.c_cashline_id = cl.c_cashline_id OR alf.c_invoice_credit_id = ic.c_invoice_id) AND alf.c_allocationline_id <> al.c_allocationline_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
  GROUP BY ah.c_currency_id, ah.grandtotal, ah.documentno, ah.datetrx, ah.c_doctype_id, ah.c_allocationhdr_id, dt.name, dt.issotrx, ah.created, ah.c_bpartner_id, ah.ad_org_id, ah.ad_client_id
UNION ALL
 SELECT p.c_currency_id,
    p.payamt AS amount,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN COALESCE(al.montosaldado, 0::numeric)
            ELSE p.payamt
        END AS debit,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN p.payamt
            ELSE COALESCE(al.montosaldado, 0::numeric)
        END AS credit,
    dt.name AS tipo_doc,
    p.documentno,
    p.datetrx,
    p.dateacct,
    p.c_doctype_id,
    'C_Payment'::text AS documenttable,
    p.c_payment_id AS document_id,
    p.payamt - COALESCE(al.montosaldado, 0::numeric) AS openamt,
    p.created,
    p.c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_payment p
     JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
     LEFT JOIN ( SELECT d.c_payment_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          GROUP BY d.c_payment_id, d.isactive) al ON al.c_payment_id = p.c_payment_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_payment_id IS NULL OR al.montosaldado > 0::numeric AND al.montosaldado < p.payamt) AND (al.cant IS NULL OR al.cant = 0)
UNION ALL
 SELECT p.c_currency_id,
    abs(p.amount) AS amount,
        CASE
            WHEN p.amount > 0::numeric THEN COALESCE(al.montosaldado, 0::numeric)
            ELSE p.amount
        END AS debit,
        CASE
            WHEN p.amount > 0::numeric THEN p.amount
            ELSE COALESCE(al.montosaldado, 0::numeric)
        END AS credit,
    'Linea de caja'::character varying AS tipo_doc,
    c.name AS documentno,
    c.statementdate AS datetrx,
    c.dateacct,
    NULL::integer AS c_doctype_id,
    'C_CashLine'::text AS documenttable,
    p.c_cashline_id AS document_id,
    abs(p.amount) - COALESCE(al.montosaldado, 0::numeric) AS openamt,
    p.created,
    p.c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
        CASE
            WHEN p.amount > 0::numeric THEN 'Y'::text
            ELSE 'N'::text
        END AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_cashline p
     JOIN c_cash c ON p.c_cash_id = c.c_cash_id
     LEFT JOIN ( SELECT d.c_cashline_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          GROUP BY d.c_cashline_id, d.isactive) al ON al.c_cashline_id = p.c_cashline_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_cashline_id IS NULL OR al.montosaldado < abs(p.amount)) AND (al.cant IS NULL OR al.cant = 0);

-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_2.0
-- Permissions

ALTER TABLE libertya.c_alldocumentscc_v OWNER TO libertya;
GRANT ALL ON TABLE libertya.c_alldocumentscc_v TO libertya;


-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_3.0
--20240603-1612 Ajustes en la vista Cta. Cte. según feedback Roberto
DROP VIEW libertya.c_alldocumentscc_v;

-- Se hizo un ajuste cambiando la columna jacofer_realdatetrx por datetrx porque esa columna dependia del custom jacofer
CREATE OR REPLACE VIEW libertya.c_alldocumentscc_v
AS SELECT c.c_currency_id,
    ps.dueamt AS amount,
        CASE
            WHEN dt.signo_issotrx = 1 THEN ps.dueamt
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 0::numeric
            ELSE ps.dueamt
        END AS credit,
    dt.name AS tipo_doc,
    i.documentno,
    i.dateinvoiced AS datetrx,
    i.dateacct,
    i.c_doctypetarget_id AS c_doctype_id,
    'C_Invoice'::text AS documenttable,
    i.c_invoice_id AS document_id,
    invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
    i.created,
    i.c_bpartner_id,
    i.ad_org_id,
    i.ad_client_id,
    i.issotrx,
    ps.c_invoicepayschedule_id,
    ps.duedate
   FROM c_invoice i
     JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
     JOIN c_currency c ON i.c_currency_id = c.c_currency_id
     JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id
  WHERE (dt.doctypekey::text <> ALL (ARRAY['RTR'::character varying::text, 'RTI'::character varying::text, 'RCR'::character varying::text, 'RCI'::character varying::text])) AND i.processed = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR dt.issotrx = 'Y'::bpchar AND dt.isfiscaldocument = 'Y'::bpchar AND i.docstatus = 'VO'::bpchar)
UNION ALL
 SELECT DISTINCT ah.c_currency_id,
    ah.grandtotal - COALESCE(alc.creditamt, 0::numeric) AS amount,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar THEN 0::numeric
            ELSE ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
        END AS debit,
        CASE
            WHEN COALESCE(dt.issotrx, i.issotrx) = 'Y'::bpchar THEN ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)
            ELSE 0::numeric
        END AS credit,
    COALESCE(dt.name, 'Imputación Manual'::character varying) AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    0 AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    COALESCE(dt.issotrx, i.issotrx) AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     LEFT JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN ( SELECT al2.c_allocationhdr_id,
            al2.c_invoice_credit_id,
            sum(al2.amount) AS creditamt
           FROM c_allocationline al2
             JOIN c_invoice i2 ON al2.c_invoice_credit_id = i2.c_invoice_id
             JOIN c_doctype dt2 ON i2.c_doctypetarget_id = dt2.c_doctype_id AND (dt2.doctypekey::text <> ALL (ARRAY['RTR'::text, 'RCR'::text, 'RTI'::text, 'RCI'::text]))
          GROUP BY al2.c_allocationhdr_id, al2.c_invoice_credit_id) alc ON alc.c_allocationhdr_id = ah.c_allocationhdr_id AND alc.c_invoice_credit_id IS NOT NULL
     LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RC'::character varying::text, 'OP'::character varying::text, 'MAN'::character varying::text])) AND (ah.grandtotal - COALESCE(alc.creditamt, 0::numeric)) > 0::numeric AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
UNION ALL
 SELECT ah.c_currency_id,
    ah.grandtotal AS amount,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN COALESCE(sum(alf.montosaldado), 0::numeric)
            ELSE ah.grandtotal
        END AS debit,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN ah.grandtotal
            ELSE COALESCE(sum(alf.montosaldado), 0::numeric)
        END AS credit,
    dt.name::text || ' Adelantado'::text AS tipo_doc,
    ah.documentno,
    ah.datetrx,
    ah.dateacct,
    ah.c_doctype_id,
    'C_AllocationHdr'::text AS documenttable,
    ah.c_allocationhdr_id AS document_id,
    ah.grandtotal - COALESCE(sum(alf.montosaldado), 0::numeric) AS openamt,
    ah.created,
    ah.c_bpartner_id,
    ah.ad_org_id,
    ah.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_allocationhdr ah
     JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id
     JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
     LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
     LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
     LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
     LEFT JOIN ( SELECT li.c_allocation_detail_v_id,
            li.c_allocationhdr_id,
            li.ad_client_id,
            li.ad_org_id,
            li.isactive,
            li.created,
            li.createdby,
            li.updated,
            li.updatedby,
            li.fecha,
            li.factura,
            li.c_currency_id,
            li.montofactura,
            li.pagonro,
            li.tipo,
            li.cash,
            li.montosaldado,
            li.payamt,
            li.c_allocationline_id,
            li.c_invoice_id,
            li.paydescription,
            li.payment_medium_name,
            li.pay_currency_id,
            li.c_bankaccount_id,
            li.numerocomprobante,
            li.puntodeventa,
            li.c_letra_comprobante_id,
            li.doctypekey,
            li.doctypename,
            li.paymentrule,
            li.c_region_delivery_id,
            li.netamount,
            li.c_paymentterm_id,
            li.dateinvoiced,
            li.c_invoice_credit_id,
            li.credit_doctypekey,
            li.credit_doctypename,
            li.credit_numerocomprobante,
            li.credit_puntodeventa,
            li.credit_letra_comprobante_id,
            li.credit_netamount,
            li.c_cashline_id,
            li.cashname,
            li.c_payment_id,
            li.accountno,
            li.checkno,
            li.a_name,
            li.a_bank,
            li.a_cuit,
            li.duedate,
            li.dateemissioncheck,
            li.checkstatus,
            li.creditcardnumber,
            li.couponbatchnumber,
            li.couponnumber,
            li.m_entidadfinancieraplan_id,
            li.m_entidadfinanciera_id,
            li.posnet,
            li.micr,
            li.isreconciled,
            li.creditdate,
            li.creditdocumentno,
            lh.c_allocationhdr_id,
            lh.ad_client_id,
            lh.ad_org_id,
            lh.isactive,
            lh.created,
            lh.createdby,
            lh.updated,
            lh.updatedby,
            lh.documentno,
            lh.description,
            lh.datetrx,
            lh.dateacct,
            lh.c_currency_id,
            lh.approvalamt,
            lh.ismanual,
            lh.docstatus,
            lh.docaction,
            lh.isapproved,
            lh.processing,
            lh.processed,
            lh.posted,
            lh.c_bpartner_id,
            lh.allocationtype,
            lh.retencion_amt,
            lh.grandtotal,
            lh.allocationaction,
            lh.actiondetail,
            lh.c_posjournal_id,
            lh.c_doctype_id,
            lh.c_banklist_id,
            lh.datetrx
           FROM c_allocation_detail_v li
             JOIN c_allocationhdr lh ON li.c_allocationhdr_id = lh.c_allocationhdr_id
          WHERE lh.processed = 'Y'::bpchar AND (lh.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))) alf(c_allocation_detail_v_id, c_allocationhdr_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, fecha, factura, c_currency_id, montofactura, pagonro, tipo, cash, montosaldado, payamt, c_allocationline_id, c_invoice_id, paydescription, payment_medium_name, pay_currency_id, c_bankaccount_id, numerocomprobante, puntodeventa, c_letra_comprobante_id, doctypekey, doctypename, paymentrule, c_region_delivery_id, netamount, c_paymentterm_id, dateinvoiced, c_invoice_credit_id, credit_doctypekey, credit_doctypename, credit_numerocomprobante, credit_puntodeventa, credit_letra_comprobante_id, credit_netamount, c_cashline_id, cashname, c_payment_id, accountno, checkno, a_name, a_bank, a_cuit, duedate, dateemissioncheck, checkstatus, creditcardnumber, couponbatchnumber, couponnumber, m_entidadfinancieraplan_id, m_entidadfinanciera_id, posnet, micr, isreconciled, creditdate, creditdocumentno, c_allocationhdr_id_1, ad_client_id_1, ad_org_id_1, isactive_1, created_1, createdby_1, updated_1, updatedby_1, documentno, description, datetrx, dateacct, c_currency_id_1, approvalamt, ismanual, docstatus, docaction, isapproved, processing, processed, posted, c_bpartner_id, allocationtype, retencion_amt, grandtotal, allocationaction, actiondetail, c_posjournal_id, c_doctype_id, c_banklist_id, datetrx) ON (alf.c_payment_id = p.c_payment_id OR alf.c_cashline_id = cl.c_cashline_id OR alf.c_invoice_credit_id = ic.c_invoice_id) AND alf.c_allocationline_id <> al.c_allocationline_id
  WHERE (ah.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
  GROUP BY ah.c_currency_id, ah.grandtotal, ah.documentno, ah.datetrx, ah.c_doctype_id, ah.c_allocationhdr_id, dt.name, dt.issotrx, ah.created, ah.c_bpartner_id, ah.ad_org_id, ah.ad_client_id
UNION ALL
 SELECT p.c_currency_id,
    p.payamt AS amount,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN COALESCE(al.montosaldado, 0::numeric)
            ELSE p.payamt
        END AS debit,
        CASE
            WHEN dt.issotrx = 'Y'::bpchar THEN p.payamt
            ELSE COALESCE(al.montosaldado, 0::numeric)
        END AS credit,
    dt.name AS tipo_doc,
    p.documentno,
    p.datetrx,
    p.dateacct,
    p.c_doctype_id,
    'C_Payment'::text AS documenttable,
    p.c_payment_id AS document_id,
    p.payamt - COALESCE(al.montosaldado, 0::numeric) AS openamt,
    p.created,
    p.c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
    dt.issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_payment p
     JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
     LEFT JOIN ( SELECT d.c_payment_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          GROUP BY d.c_payment_id, d.isactive) al ON al.c_payment_id = p.c_payment_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_payment_id IS NULL OR al.montosaldado > 0::numeric AND al.montosaldado < p.payamt) AND (al.cant IS NULL OR al.cant = 0)
UNION ALL
 SELECT p.c_currency_id,
    abs(p.amount) AS amount,
        CASE
            WHEN p.amount > 0::numeric THEN COALESCE(al.montosaldado, 0::numeric) * '-1'::integer::numeric
            ELSE p.amount * '-1'::integer::numeric
        END AS debit,
        CASE
            WHEN p.amount > 0::numeric THEN p.amount
            ELSE COALESCE(al.montosaldado, 0::numeric)
        END AS credit,
    'Linea de caja'::character varying AS tipo_doc,
    c.name AS documentno,
    c.statementdate AS datetrx,
    c.dateacct,
    NULL::integer AS c_doctype_id,
    'C_CashLine'::text AS documenttable,
    p.c_cashline_id AS document_id,
    abs(p.amount) - COALESCE(al.montosaldado, 0::numeric) AS openamt,
    p.created,
    p.c_bpartner_id,
    p.ad_org_id,
    p.ad_client_id,
        CASE
            WHEN p.amount > 0::numeric THEN 'Y'::text
            ELSE 'N'::text
        END AS issotrx,
    NULL::integer AS c_invoicepayschedule_id,
    NULL::timestamp without time zone AS duedate
   FROM c_cashline p
     JOIN c_cash c ON p.c_cash_id = c.c_cash_id
     LEFT JOIN ( SELECT d.c_cashline_id,
            d.isactive,
            sum(d.montosaldado) AS montosaldado,
            count(aha.c_allocationhdr_id) AS cant
           FROM c_allocation_detail_v d
             LEFT JOIN c_allocationhdr aha ON d.c_allocationhdr_id = aha.c_allocationhdr_id AND (aha.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND aha.processed = 'Y'::bpchar AND (aha.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
             LEFT JOIN c_allocationhdr ah ON d.c_allocationhdr_id = ah.c_allocationhdr_id AND (ah.allocationtype::text <> ALL (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]))
          GROUP BY d.c_cashline_id, d.isactive) al ON al.c_cashline_id = p.c_cashline_id AND al.isactive = 'Y'::bpchar
  WHERE p.processed = 'Y'::bpchar AND (p.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (al.c_cashline_id IS NULL OR al.montosaldado < abs(p.amount)) AND (al.cant IS NULL OR al.cant = 0)
UNION ALL
 SELECT c.c_currency_id,
    ps.dueamt AS amount,
        CASE
            WHEN dt.signo_issotrx = 1 THEN ps.dueamt
            ELSE 0::numeric
        END AS debit,
        CASE
            WHEN dt.signo_issotrx = 1 THEN 0::numeric
            ELSE ps.dueamt
        END AS credit,
    dt.name AS tipo_doc,
    i.documentno,
    i.dateinvoiced AS datetrx,
    i.dateacct,
    i.c_doctypetarget_id AS c_doctype_id,
    'C_Invoice'::text AS documenttable,
    i.c_invoice_id AS document_id,
    invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) AS openamt,
    i.created,
    i.c_bpartner_id,
    i.ad_org_id,
    i.ad_client_id,
    i.issotrx,
    ps.c_invoicepayschedule_id,
    ps.duedate
   FROM c_invoice i
     JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
     JOIN c_currency c ON i.c_currency_id = c.c_currency_id
     JOIN c_invoicepayschedule ps ON ps.c_invoice_id = i.c_invoice_id
  WHERE (dt.doctypekey::text = ANY (ARRAY['RTR'::text, 'RCR'::text])) AND i.processed = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR dt.issotrx = 'Y'::bpchar AND dt.isfiscaldocument = 'Y'::bpchar AND i.docstatus = 'VO'::bpchar) AND invoiceopen(i.c_invoice_id, ps.c_invoicepayschedule_id) > 0::numeric AND NOT (i.c_invoice_id IN ( SELECT al.c_invoice_credit_id
           FROM c_allocationline al
             JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
          WHERE (ah.allocationtype::text = ANY (ARRAY['RCA'::character varying::text, 'OPA'::character varying::text])) AND ah.processed = 'Y'::bpchar AND (ah.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND al.c_invoice_credit_id IS NOT NULL));
  
-- ### MERGE 2024-10-02 org.libertya.core.micro.r3019.dev.jacofer_14_cc upgrade_from_3.0
--20240624-1633 Ajuste para corregir texto de movimientos sin tipo de documento
UPDATE c_doctype 
SET name = 'Efectivo/Otro'
WHERE c_doctype_id = 0;


-- Fix Import facturas proveedor afip
update ad_system set dummy = (SELECT addcolumnifnotexists('I_Vendor_Invoice_Import','otros_tributos','NUMERIC(20,2)'));

-- 20241126-1005 Nuevas funciones para simplificar la actualizacion de reportes jasper, ya sea en tabla ad_jasperreport o bajo ad_attachment de un ad_process
/**
 * Actualiza el campo binarydata de un registro de la tabla ad_jasperreport
 * 
 * Recibe 2 argumentos
 *   - La ruta al archivo .jasper
 *   - El UID del ad_jasperreport a actualizar
 *  
 * Ejemplos de uso:
 *   - select * from updatejasper('/tmp/Inventory.jasper', 'CORE-AD_JasperReport-1010069') -- OK: Devuelve 1 (registro actualizado)
 *   - select * from updatejasper('/tmp/Inventory.jasper', 'XXX') -- OK: Devuelve 0 (registros actualizados)
 *   - select * from updatejasper('/tmp/XXX', 'CORE-AD_JasperReport-1010069') -- ERROR: no existe el archivo
 */
CREATE OR REPLACE FUNCTION updatejasper(filepath VARCHAR, jasperreportuid VARCHAR)
RETURNS INTEGER AS $$
DECLARE
    registros_actualizados INTEGER;
BEGIN
    -- Actualiza el campo binarydata con el contenido del archivo proporcionado
    UPDATE ad_jasperreport
    SET binarydata = bytea_import(filepath)::bytea
    WHERE ad_componentobjectuid = jasperreportuid;
    
    -- Obtiene el número de filas afectadas
    GET DIAGNOSTICS registros_actualizados = ROW_COUNT;

    -- Retorna el número de registros actualizados
    RETURN registros_actualizados;
END;
$$ LANGUAGE plpgsql;

/**
 * Actualiza el adjunto de un informe/proceso, eliminando el actual y creando uno nuevo
 * 
 * Recibe 2 argumentos
 *   - La ruta al archivo a adjuntar, el cual DEBE estar en formato ZIP
 *   - El UID del ad_process que referenciará el ad_attachment a crear
 *  
 * Ejemplos de uso:
 *   - select * from updateattachment('/tmp/PhysicalInventoryAudit.zip', 'CORE-AD_Process-1010369') -- OK: Devuelve 1
 *   - select * from updateattachment('/tmp/PhysicalInventoryAudit.zip', 'XXX') -- ERROR: no existe el proceso
 *   - select * from updateattachment('/tmp/XXX', 'CORE-AD_Process-1010369') -- ERROR: no existe el archivo 
 */
CREATE OR REPLACE FUNCTION updateattachment(filepath VARCHAR, processuid VARCHAR)
RETURNS INTEGER AS $$
DECLARE
    registros_insertados INTEGER;
BEGIN
    -- Elimina cualquier entrada previa en ad_attachment para el proceso identificado
    DELETE FROM ad_attachment
    WHERE ad_table_id = (
        SELECT ad_table_id
        FROM ad_table
        WHERE tablename = 'AD_Process'
        LIMIT 1
    )
    AND record_id = (
        SELECT ad_process_id
        FROM ad_process
        WHERE ad_componentobjectuid = processuid
        LIMIT 1
    );

    -- Inserta una nueva entrada en ad_attachment
    INSERT INTO ad_attachment (
        ad_attachment_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby,
        ad_table_id, record_id, title, binarydata
    )
    VALUES (
        NEXTVAL('seq_ad_attachment'), -- ID único
        0, 0, -- Cliente y organización
        'Y', -- Activo
        NOW(), 0, -- Creación (fecha y usuario)
        NOW(), 0, -- Actualización (fecha y usuario)
        (SELECT ad_table_id FROM ad_table WHERE tablename = 'AD_Process' LIMIT 1),
        (SELECT ad_process_id FROM ad_process WHERE ad_componentobjectuid = processuid LIMIT 1),
        'zip', -- Título
        bytea_import(filepath)::bytea -- Datos binarios
    );

    -- Obtiene el número de filas afectadas por la última operación (INSERT)
    GET DIAGNOSTICS registros_insertados = ROW_COUNT;

    -- Retorna el número de registros insertados
    RETURN registros_insertados;
END;
$$ LANGUAGE plpgsql;

-- 2024-12-16
-- Se agrega columna faltante para micro de facturacion/ocultar descuento en lineas de facturas
update ad_system set dummy = (SELECT addcolumnifnotexists('C_BPartner','isocultardesctolineafc','character(1)'));

-- 2025-06-03 Fix en default value para columna C_CashBook.C_Currency_ID.  Faltaba el @ inicial en la expresion
update ad_column set defaultvalue = '@' || defaultvalue where ad_componentobjectuid = 'CORE-AD_Column-5521' and defaultvalue ilike 'SQL%';

-- 2025-07-23 Al eliminar una organizacion tambien deben eliminarse los accesos por usuario
ALTER TABLE ad_user_orgaccess DROP CONSTRAINT IF EXISTS adorg_aduserorgaccess;
ALTER TABLE ad_user_orgaccess ADD CONSTRAINT adorg_aduserorgaccess FOREIGN KEY (ad_org_id) REFERENCES ad_org(ad_org_id) ON DELETE CASCADE;

-- 2025-08-19 Fix: A field with precision 12, scale 2 must round to an absolute value less than 10^10 en ejecucion del informe de saldos
ALTER TABLE t_balancereport ALTER COLUMN credit TYPE numeric(22, 2) USING credit::numeric(22, 2);
ALTER TABLE t_balancereport ALTER COLUMN debit TYPE numeric(22, 2) USING debit::numeric(22, 2);
ALTER TABLE t_balancereport ALTER COLUMN balance TYPE numeric(22, 2) USING balance::numeric(22, 2);
ALTER TABLE t_balancereport ALTER COLUMN duedebt TYPE numeric(22, 2) USING duedebt::numeric(22, 2);
ALTER TABLE t_balancereport ALTER COLUMN actualbalance TYPE numeric(22, 2) USING actualbalance::numeric(22, 2);
ALTER TABLE t_balancereport ALTER COLUMN chequesencartera TYPE numeric(22, 2) USING chequesencartera::numeric(22, 2);
ALTER TABLE t_balancereport ALTER COLUMN generalbalance TYPE numeric(22, 2) USING generalbalance::numeric(22, 2);
