-- ========================================================================================
-- PREINSTALL FROM 18.06
-- ========================================================================================
-- Consideraciones importantes:
--	1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 	2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

--20180814-1125 Nueva columna para habilitar/inhabilitar entidades comerciales para operar en el sistema
update ad_system set dummy = (SELECT addcolumnifnotexists('c_bpartner','trxenabled','character(1) NOT NULL DEFAULT ''Y''::bpchar'));

update ad_client
set modelvalidationclasses = modelvalidationclasses || ';' || 'org.openXpertya.model.BusinessPartnerValidator'
where position('org.openXpertya.model.BusinessPartnerValidator' in modelvalidationclasses) = 0;

update ad_client
set modelvalidationclasses = replace(modelvalidationclasses,';;',';');