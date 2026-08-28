-- ========================================================================================
-- PREINSTALL FROM 26.05
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

-- 20260603-1638: Agregar campos faltantes para Hoja de ruta (relacionado a merge org.libertya.core.micro.r3019.dev.jacofer_11b_cot_arba)

update ad_system set dummy = (SELECT addcolumnifnotexists('m_jacofer_roadmap','m_shipper_id','integer NOT NULL'));
update ad_system set dummy = (SELECT addcolumnifnotexists('m_jacofer_roadmap','m_shipper_vehicle_id','integer NOT NULL'));
update ad_system set dummy = (SELECT addcolumnifnotexists('m_jacofer_roadmap','m_shipper_trailer_id','integer'));

-- 20260624-0847 Generacion de UIDs para entradas ya existentes en AD_Attachment
-- UIDs para attachments -> ad_process
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-1' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010327');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-2' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010328');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-3' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010330');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-4' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010338');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-5' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010341');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-6' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010342');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-7' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010343');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-8' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010344');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-9' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010345');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-10' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010346');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-11' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010347');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-12' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010358');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-13' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010360');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-14' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010361');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-15' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010362');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-16' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010373');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-17' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010374');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-18' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010380');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-19' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010381');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-20' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010392');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-21' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010394');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-22' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010414');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-23' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010399');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-24' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010400');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-25' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010401');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-26' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010426');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-27' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010379');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-28' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010427');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-29' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010428');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-30' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010429');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-31' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010421');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-32' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010324');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-33' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010446');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-34' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010430');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-35' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010433');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-36' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010415');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-37' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010432');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-38' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010431');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-39' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010500');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-40' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010424');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-41' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010422');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-42' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010550');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-43' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'RPRT2CORE-AD_Process-1010526-20170210194428');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-44' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010576');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-45' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010607');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-46' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010608');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-47' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010615');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-48' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'TACC2CORE-AD_Process-1010626-20190401164139');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-49' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010444');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-50' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010445');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-51' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010444');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-52' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010445');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-53' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010444');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-54' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010445');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-55' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010359');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-56' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010405');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-57' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'TEHLBY-AD_Process-20200319164902392-923485');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-58' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010549');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-59' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'TEHLBY-AD_Process-20200720171849643-475519');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-60' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010369');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-61' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'CORE-AD_Process-1010577');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-62' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'JACLBY-AD_Process-20200601184841124-606998');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-63' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'JACLBY4.7-AD_Process-20200408171313237-755685');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-64' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'JACAD1_RA1-AD_Process-20210127141500085-924635');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-65' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'JACAD1_RA2-AD_Process-20210202130311740-315374');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-66' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'LIDIGEN-AD_Process-20200915121504378-687567');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-67' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'LIVADET-AD_Process-20210510104140000-804554');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-68' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'LIVADET-AD_Process-20210510104521071-681043');
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-69' where ad_table_id = 284 and record_id = (select ad_process_id from ad_process where ad_componentobjectuid = 'INFODESCAR-AD_Process-20201027165059682-999619');
-- UIDs para attachments -> ad_process_para
update ad_attachment set ad_componentobjectuid = 'CORE-AD_Attachment-20260624-70' where ad_table_id = 285 and record_id = (select ad_process_para_id from ad_process_para where ad_componentobjectuid = 'CORE-AD_Process_Para-1010812');


-- 20260624-1153 Ajustes de traducciones Centro de Costos en ventana principal de Proyecto
UPDATE ad_window_trl wt
SET
    name = 'Centro de costos',
    description = 'Mantener Centros de costos',
    help = 'La ventana de centros de costos es usada para definir los centros de costos que serán monitoreados por medio de documentos de la aplicación.'
FROM ad_window w
WHERE w.ad_window_id = wt.ad_window_id
  AND w.ad_componentobjectuid = 'CORE-AD_Window-130'
  AND wt.ad_language ILIKE 'es_%';

UPDATE ad_menu_trl mt
SET
    name = 'Centro de costos',
    description = 'Mantener Centros de costos'
FROM ad_menu m
WHERE m.ad_menu_id = mt.ad_menu_id
  AND m.ad_componentobjectuid = 'CORE-AD_Menu-116'
  AND mt.ad_language ILIKE 'es_%';

UPDATE ad_tab_trl tt
SET
    name = 'Centro de costos',
    description = 'Definir Centro de costos',
    help = 'La pestaña de centros de costos es usada para definir el valor, nombre y descripción de cada centro de costos. También define y hace seguimiento a los montos asignados, comprometidos y usados.'
FROM ad_tab t
WHERE t.ad_tab_id = tt.ad_tab_id
  AND t.ad_componentobjectuid = 'CORE-AD_Tab-157'
  AND tt.ad_language ILIKE 'es_%';

UPDATE ad_table_trl tt
SET
    name = 'Centro de costos'
FROM ad_table t
WHERE t.ad_table_id = tt.ad_table_id
  AND t.ad_componentobjectuid = 'CORE-AD_Table-203'
  AND tt.ad_language ILIKE 'es_%';

-- 20260626-1208 Configuracion para permitir servicios repetidos en pedidos a proveedor
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','AllowDuplicateService','character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE AD_ClientInfo SET AllowDuplicateService = 'N' WHERE AllowDuplicateService IS NULL;

--20260724-0922 Cambiar ad_client.modelvalidationclasses del tipo de varchar(255) a text para evitar limitaciones en la longitud de las clases definidas.  No definir un limite de 255 en metadatos.
--Estas ampliaciones tambien se realizaron a nivel CORE, pero se incluyen aqui en caso de estar usando una version previa de CORE.
ALTER TABLE ad_client ALTER COLUMN modelvalidationclasses TYPE text;
update ad_column set fieldlength = -1 where ad_componentobjectuid = 'CORE-AD_Column-13058';

--20260729-1130 Agregar columna en configuracion de impuestos NO aplica retenciones
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Tax','IsNoAplicaRetencion','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20260729-1530 Agregar columna en esquema de retenciones en las lineas de facturas
update ad_system set dummy = (SELECT addcolumnifnotexists('C_InvoiceLine','C_RetencionSchema_ID','integer'));

--20260730-1200 Backfill de C_DocType.InOut_Allow_Greater_QtyOrdered: el merge "Pasaje de micro IOREEXOR a JACLBY" en preinstall_from_21.0.sql quedo con un comentario de bloque mal formado (sin -HHMM) y el bloque completo se salteo en bases cuyo AD_Plugin.component_export_date ya habia superado esa fecha, dejando la columna faltante
update ad_system set dummy = (SELECT addcolumnifnotexists('c_doctype','inout_allow_greater_qtyordered','character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20260804-1400 nueva funcion sql para parsear tasas de retencion/percepcion
CREATE OR REPLACE FUNCTION parse_alicuota_padron(p_valor text)
  RETURNS numeric AS
$BODY$
BEGIN
	IF p_valor IS NULL OR trim(p_valor) = '' THEN
		RETURN 0;
	ELSIF position(',' in p_valor) > 0 THEN
		-- Trae coma decimal explicita: se usa tal cual
		RETURN replace(p_valor, ',', '.')::numeric;
	ELSIF position('.' in p_valor) > 0 THEN
		-- Trae punto decimal explicito: se usa tal cual
		RETURN p_valor::numeric;
	ELSE
		-- Sin separador: se asume que los ultimos 2 digitos son decimales
		RETURN p_valor::numeric / 100;
	END IF;
END;
$BODY$
  LANGUAGE plpgsql IMMUTABLE;
ALTER FUNCTION parse_alicuota_padron(text)
  OWNER TO libertya;
  
--20260804-1402 fix parseo funcion para padron perc bs as  
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
			, PERCEPCION = (CASE aux.regimen WHEN 'P' THEN parse_alicuota_padron(aux.alicuota) ELSE padron.percepcion END)
			, RETENCION = (CASE aux.regimen WHEN 'R' THEN parse_alicuota_padron(aux.alicuota) ELSE padron.retencion END)
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
				, (CASE aux.regimen WHEN 'P' THEN parse_alicuota_padron(aux.alicuota) ELSE 0 END)
				, (CASE aux.regimen WHEN 'R' THEN parse_alicuota_padron(aux.alicuota) ELSE 0 END)
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

--20260804-1403 fix parseo funcion para padron caba alto riesgo
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
			, PERCEPCION = parse_alicuota_padron(aux.PERCEPCION)
			, RETENCION = parse_alicuota_padron(aux.RETENCION)
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
				, parse_alicuota_padron(aux.PERCEPCION)
				, parse_alicuota_padron(aux.RETENCION)
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
  
--20260804-1403 fix parseo funcion para padron caba general
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
			, PERCEPCION = parse_alicuota_padron(aux.PERCEPCION)
			, RETENCION = parse_alicuota_padron(aux.RETENCION)
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
				, parse_alicuota_padron(aux.PERCEPCION)
				, parse_alicuota_padron(aux.RETENCION)
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

--20260804-1404 fix parseo funcion para padron caba regimen simplificado
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
			, PERCEPCION = parse_alicuota_padron(aux.PERCEPCION)
			, RETENCION = parse_alicuota_padron(aux.RETENCION)
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
				, parse_alicuota_padron(aux.PERCEPCION)
				, parse_alicuota_padron(aux.RETENCION)
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

--20260804-1610 Remover preferencias personales WebUI exportadas por error en core 26.05
DELETE FROM ad_preference p
WHERE p.ad_componentobjectuid IN (
	'CORE-AD_Preference-20260518095035923-386798',
	'CORE-AD_Preference-20260518095035969-868815',
	'CORE-AD_Preference-20260518095036000-679065',
	'CORE-AD_Preference-20260518095036039-003161',
	'CORE-AD_Preference-20260518095036072-336964',
	'CORE-AD_Preference-20260518095036101-046814',
	'CORE-AD_Preference-20260518095036134-560011',
	'CORE-AD_Preference-20260518095036161-854751',
	'CORE-AD_Preference-20260518095036183-118156',
	'CORE-AD_Preference-20260518095036206-446841',
	'CORE-AD_Preference-20260518095036228-678755'
)
AND p.ad_user_id IS NOT NULL
AND p.ad_window_id IS NULL
AND p.attribute IN (
	'Language',
	'Role',
	'Client',
	'Organization',
	'Warehouse',
	'AutoCommit',
	'AutoNew',
	'WindowTabPlacement',
	'WindowTabCollapsible',
	'MenuCollapsed',
	'CompactMode'
)
AND EXISTS (
	SELECT 1
	FROM ad_preference p2
	WHERE p2.ad_preference_id <> p.ad_preference_id
		AND p2.ad_user_id = p.ad_user_id
		AND p2.attribute = p.attribute
		AND p2.ad_window_id IS NULL
);

--20260811-1340 alta del formato de importacion para tasas de cambio
INSERT INTO AD_ImpFormat (
    AD_ImpFormat_ID, AD_Client_ID, AD_Org_ID, IsActive,
    Created, CreatedBy, Updated, UpdatedBy,
    Name, Description, AD_Table_ID, FormatType, Processing
) VALUES (
    nextval('seq_ad_impformat'), 0, 0, 'Y',
    now(), 0, now(), 0,
    'Cotizaciones de Moneda (CSV)',
    'Formato de importación de tasas de cambio hacia la tabla staging I_Conversion_Rate. Ejecutar luego el proceso Import Conversion Rate.',
    (SELECT AD_Table_ID FROM AD_Table WHERE TableName = 'I_Conversion_Rate'),
    'C', 'N'
);
INSERT INTO AD_ImpFormat_Row (
    AD_ImpFormat_Row_ID,
    AD_Client_ID,
    AD_Org_ID,
    IsActive,
    Created,
    CreatedBy,
    Updated,
    UpdatedBy,
    AD_ImpFormat_ID,
    SeqNo,
    Name,
    AD_Column_ID,
    StartNo,
    EndNo,
    DataType,
    DataFormat,
    DecimalPoint,
    DivideBy100
)
VALUES
(
    nextval('seq_ad_impformat_row'),
    0,
    0,
    'Y',
    now(),
    0,
    now(),
    0,
    currval('seq_ad_impformat'),
    10,
    'Moneda Origen (ISO)',
    (
        SELECT c.AD_Column_ID
        FROM AD_Column c
        JOIN AD_Table t ON t.AD_Table_ID = c.AD_Table_ID
        WHERE t.TableName = 'I_Conversion_Rate'
          AND c.ColumnName = 'ISO_Code'
    ),
    1, 0, 'S', NULL, '.', 'N'
),
(
    nextval('seq_ad_impformat_row'),
    0,
    0,
    'Y',
    now(),
    0,
    now(),
    0,
    currval('seq_ad_impformat'),
    20,
    'Moneda Destino (ISO)',
    (
        SELECT c.AD_Column_ID
        FROM AD_Column c
        JOIN AD_Table t ON t.AD_Table_ID = c.AD_Table_ID
        WHERE t.TableName = 'I_Conversion_Rate'
          AND c.ColumnName = 'ISO_Code_To'
    ),
    2, 0, 'S', NULL, '.', 'N'
),
(
    nextval('seq_ad_impformat_row'),
    0,
    0,
    'Y',
    now(),
    0,
    now(),
    0,
    currval('seq_ad_impformat'),
    30,
    'Tipo de Conversión',
    (
        SELECT c.AD_Column_ID
        FROM AD_Column c
        JOIN AD_Table t ON t.AD_Table_ID = c.AD_Table_ID
        WHERE t.TableName = 'I_Conversion_Rate'
          AND c.ColumnName = 'ConversionTypeValue'
    ),
    3, 0, 'S', NULL, '.', 'N'
),
(
    nextval('seq_ad_impformat_row'),
    0,
    0,
    'Y',
    now(),
    0,
    now(),
    0,
    currval('seq_ad_impformat'),
    40,
    'Fecha Desde',
    (
        SELECT c.AD_Column_ID
        FROM AD_Column c
        JOIN AD_Table t ON t.AD_Table_ID = c.AD_Table_ID
        WHERE t.TableName = 'I_Conversion_Rate'
          AND c.ColumnName = 'ValidFrom'
    ),
    4, 0, 'D', 'dd-MM-yyyy', '.', 'N'
),
(
    nextval('seq_ad_impformat_row'),
    0,
    0,
    'Y',
    now(),
    0,
    now(),
    0,
    currval('seq_ad_impformat'),
    50,
    'Fecha Hasta',
    (
        SELECT c.AD_Column_ID
        FROM AD_Column c
        JOIN AD_Table t ON t.AD_Table_ID = c.AD_Table_ID
        WHERE t.TableName = 'I_Conversion_Rate'
          AND c.ColumnName = 'ValidTo'
    ),
    5, 0, 'D', 'dd-MM-yyyy', '.', 'N'
),
(
    nextval('seq_ad_impformat_row'),
    0,
    0,
    'Y',
    now(),
    0,
    now(),
    0,
    currval('seq_ad_impformat'),
    60,
    'Tasa Multiplicadora',
    (
        SELECT c.AD_Column_ID
        FROM AD_Column c
        JOIN AD_Table t ON t.AD_Table_ID = c.AD_Table_ID
        WHERE t.TableName = 'I_Conversion_Rate'
          AND c.ColumnName = 'MultiplyRate'
    ),
    6, 0, 'N', NULL, '.', 'N'
),
(
    nextval('seq_ad_impformat_row'),
    0,
    0,
    'Y',
    now(),
    0,
    now(),
    0,
    currval('seq_ad_impformat'),
    70,
    'Tasa Divisora',
    (
        SELECT c.AD_Column_ID
        FROM AD_Column c
        JOIN AD_Table t ON t.AD_Table_ID = c.AD_Table_ID
        WHERE t.TableName = 'I_Conversion_Rate'
          AND c.ColumnName = 'DivideRate'
    ),
    7, 0, 'N', NULL, '.', 'N'
);


--20260828-1056 Versionado de BBDD para release
UPDATE ad_system SET version = '28-08-2026' WHERE ad_system_id = 0;

