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