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