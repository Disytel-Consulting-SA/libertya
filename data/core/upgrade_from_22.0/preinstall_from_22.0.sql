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

