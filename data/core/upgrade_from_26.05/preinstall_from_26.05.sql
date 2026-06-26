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
