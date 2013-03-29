-- ========================================================================================
-- PREINSTALL FROM 13.01
-- ========================================================================================
-- 	Consideraciones importantes:
--			1) NO hacer cambios en el archivo, realizar siempre APPENDs al final del mismo 
-- 			2) Recordar realizar las adiciones con un comentario con formato YYYYMMDD-HHMM
-- ========================================================================================

-- 20130129-1400 - Incorporaciones necesarias para desarrollo Libertya Web
CREATE TABLE libertya.ad_entitytype
(
  entitytype character varying(40) NOT NULL DEFAULT 'D'::character varying,
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  ad_entitytype_id numeric(10,0) NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT now(),
  createdby numeric(10,0) NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT now(),
  updatedby numeric(10,0) NOT NULL,
  name character varying(60) NOT NULL,
  description character varying(255),
  help character varying(2000),
  version character varying(20),
  modelpackage character varying(255),
  classpath character varying(255),
  processing character(1),
  CONSTRAINT ad_entitytype_pkey PRIMARY KEY (entitytype),
  CONSTRAINT ad_entitytype_isactive_check CHECK (isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))
);

CREATE TABLE libertya.pa_dashboardcontent
(
  pa_dashboardcontent_id numeric(10,0) NOT NULL,
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  created timestamp without time zone NOT NULL,
  createdby numeric(10,0) NOT NULL,
  updated timestamp without time zone NOT NULL,
  updatedby numeric(10,0) NOT NULL,
  isactive character(1) NOT NULL,
  name character varying(120) NOT NULL,
  ad_window_id int,
  description character varying(255),
  html text,
  line numeric,
  pa_goal_id numeric(10,0) null,
  columnno numeric(10,0) DEFAULT 1,
  zulfilepath character varying(255),
  iscollapsible character(1) NOT NULL DEFAULT 'Y'::bpchar,
  goaldisplay character(1) DEFAULT 'T'::bpchar,
  isopenbydefault character(1) DEFAULT 'Y'::bpchar,
  CONSTRAINT pa_dashboardcontent_pkey PRIMARY KEY (pa_dashboardcontent_id),
  CONSTRAINT adwindow_padashboardcontent FOREIGN KEY (ad_window_id)
      REFERENCES libertya.ad_window (ad_window_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
--  CONSTRAINT pagoal_padashboardcontent FOREIGN KEY (pa_goal_id)
--      REFERENCES libertya.pa_goal (pa_goal_id) MATCH SIMPLE
--      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT pa_dashboardcontent_isactive_check CHECK (isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])),
  CONSTRAINT pa_dashboardcontent_iscollapsible_check CHECK (iscollapsible = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])),
  CONSTRAINT pa_dashboardcontent_isopenbydefault_check CHECK (isopenbydefault = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))
);

CREATE TABLE libertya.pa_dashboardcontent_trl
(
  ad_client_id numeric(10,0) NOT NULL,
  ad_language character varying(6) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  created timestamp without time zone NOT NULL DEFAULT now(),
  createdby numeric(10,0) NOT NULL,
  description character varying(255),
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  istranslated character(1) NOT NULL,
  name character varying(60) NOT NULL,
  pa_dashboardcontent_id numeric(10,0) NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT now(),
  updatedby numeric(10,0) NOT NULL,
  CONSTRAINT pa_dashboardcontent_trl_pkey PRIMARY KEY (ad_language, pa_dashboardcontent_id),
  CONSTRAINT adlangu_padashboardcontenttrl FOREIGN KEY (ad_language)
      REFERENCES libertya.ad_language (ad_language) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT padashboardcontent_padashboard FOREIGN KEY (pa_dashboardcontent_id)
      REFERENCES libertya.pa_dashboardcontent (pa_dashboardcontent_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT pa_dashboardcontent_trl_isactive_check CHECK (isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])),
  CONSTRAINT pa_dashboardcontent_trl_istranslated_check CHECK (istranslated = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))
);

CREATE TABLE libertya.ad_sysconfig
(
  ad_sysconfig_id numeric(10,0) NOT NULL,
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  created timestamp without time zone NOT NULL,
  updated timestamp without time zone NOT NULL,
  createdby numeric(10,0) NOT NULL,
  updatedby numeric(10,0) NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  name character varying(50) NOT NULL,
  value character varying(255) NOT NULL,
  description character varying(255),
  entitytype character varying(40) NOT NULL DEFAULT 'U'::character varying,
  configurationlevel character(1) DEFAULT 'S'::bpchar,
  CONSTRAINT ad_sysconfig_pkey PRIMARY KEY (ad_sysconfig_id),
  CONSTRAINT entityt_adsysconfig FOREIGN KEY (entitytype)
      REFERENCES libertya.ad_entitytype (entitytype) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT ad_sysconfig_isactive_check CHECK (isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))
);


CREATE TABLE libertya.ad_userquery
(
  ad_userquery_id numeric(10,0) NOT NULL,
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT now(),
  createdby numeric(10,0) NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT now(),
  updatedby numeric(10,0) NOT NULL,
  name character varying(60) NOT NULL,
  description character varying(255),
  ad_user_id int,
  ad_table_id int NOT NULL,
  code character varying(2000),
  ad_tab_id int,
  CONSTRAINT ad_userquery_pkey PRIMARY KEY (ad_userquery_id),
  CONSTRAINT adtab_aduserquery FOREIGN KEY (ad_tab_id)
      REFERENCES libertya.ad_tab (ad_tab_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT adtable_aduserquery FOREIGN KEY (ad_table_id)
      REFERENCES libertya.ad_table (ad_table_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT aduser_aduserquery FOREIGN KEY (ad_user_id)
      REFERENCES libertya.ad_user (ad_user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT ad_userquery_isactive_check CHECK (isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))
);


CREATE TABLE libertya.ad_searchdefinition
(
  ad_client_id numeric(10,0) NOT NULL,
  ad_column_id int,
  ad_org_id numeric(10,0) NOT NULL,
  ad_searchdefinition_id int NOT NULL,
  ad_table_id int NOT NULL,
  ad_window_id int NOT NULL,
  created timestamp without time zone,
  createdby numeric(10,0),
  datatype character varying(1) NOT NULL,
  description character varying(255),
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  name character varying(60),
  query character varying(2000),
  searchtype character varying(1) NOT NULL,
  transactioncode character varying(8),
  updated timestamp without time zone NOT NULL,
  updatedby numeric(10,0) NOT NULL,
  po_window_id int,
  isdefault character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT ad_searchdefinition_pkey PRIMARY KEY (ad_searchdefinition_id),
  CONSTRAINT adcolumn_adsearchdefinition FOREIGN KEY (ad_column_id)
      REFERENCES libertya.ad_column (ad_column_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT adtable_adsearchdefinition FOREIGN KEY (ad_table_id)
      REFERENCES libertya.ad_table (ad_table_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT adwindow_adsearchdefinition FOREIGN KEY (ad_window_id)
      REFERENCES libertya.ad_window (ad_window_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT powindow_adsearchdefinition FOREIGN KEY (po_window_id)
      REFERENCES libertya.ad_window (ad_window_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT ad_searchdefinition_isactive_check CHECK (isactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])),
  CONSTRAINT ad_searchdefinition_isdefault_check CHECK (isdefault = ANY (ARRAY['Y'::bpchar, 'N'::bpchar]))
);

ALTER TABLE ad_printformat add column jasperprocess_id int null;
ALTER TABLE ad_process add column ad_form_id int null;
ALTER TABLE c_acctschema add column AD_OrgOnly_ID int null;
ALTER TABLE c_invoice add column m_rma_id int null;
ALTER TABLE AD_ROLE ADD COLUMN confirmqueryrecords numeric(10,0) NOT NULL DEFAULT 0;
ALTER TABLE AD_ROLE ADD COLUMN maxqueryrecords numeric(10,0) NOT NULL DEFAULT 0;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_account character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_asset character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_bpartner character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_cashjournal character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_inout character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_invoice character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_order character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_payment character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_product character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_resource character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_schedule character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_mrp character(1) NOT NULL DEFAULT 'Y'::bpchar;
ALTER TABLE AD_ROLE ADD COLUMN allow_info_crp character(1) NOT NULL DEFAULT 'Y'::bpchar;

INSERT INTO ad_entitytype (entitytype, ad_client_id, ad_org_id, ad_entitytype_id, isactive, created, createdby, updated, updatedby, name, description, help, version, modelpackage, classpath, processing)
VALUES ('D',0,0,10,'Y',now(),100,now(),0,'Dictionary','Application Dictionary Ownership ** System Maintained **','The entity is owned by the Application Dictionary.','','','','N');

INSERT INTO PA_Dashboardcontent (pa_dashboardcontent_id, ad_client_id, ad_org_id, created, createdby, updated, updatedby, isactive, name, ad_window_id, description, html, line, pa_goal_id, columnno, zulfilepath, iscollapsible, goaldisplay, isopenbydefault)
VALUES (50000,0,0,now(),0,now(),0,'Y','Activities',null,'Workflow activities, notices and requests','',0,null,0,'/zul/activities.zul','Y','T','Y');
INSERT INTO PA_Dashboardcontent (pa_dashboardcontent_id, ad_client_id, ad_org_id, created, createdby, updated, updatedby, isactive, name, ad_window_id, description, html, line, pa_goal_id, columnno, zulfilepath, iscollapsible, goaldisplay, isopenbydefault)
VALUES (50001,0,0,now(),0,now(),0,'Y','Favourites',null,'User favourities','',1,null,0,'/zul/favourites.zul','Y','T','Y');
INSERT INTO PA_Dashboardcontent (pa_dashboardcontent_id, ad_client_id, ad_org_id, created, createdby, updated, updatedby, isactive, name, ad_window_id, description, html, line, pa_goal_id, columnno, zulfilepath, iscollapsible, goaldisplay, isopenbydefault)
VALUES (50002,0,0,now(),0,now(),0,'Y','Views',null,'Info views','',2,null,0,'/zul/views.zul','Y','T','Y');
INSERT INTO PA_Dashboardcontent (pa_dashboardcontent_id, ad_client_id, ad_org_id, created, createdby, updated, updatedby, isactive, name, ad_window_id, description, html, line, pa_goal_id, columnno, zulfilepath, iscollapsible, goaldisplay, isopenbydefault)
VALUES (50003,0,0,now(),0,now(),0,'Y','Performance',null,'Performance meters','',0,null,2,'/zul/performance.zul','Y','T','Y');
INSERT INTO PA_Dashboardcontent (pa_dashboardcontent_id, ad_client_id, ad_org_id, created, createdby, updated, updatedby, isactive, name, ad_window_id, description, html, line, pa_goal_id, columnno, zulfilepath, iscollapsible, goaldisplay, isopenbydefault)
VALUES (50004,11,0,now(),0,now(),0,'Y','Calendar',null,'Google Calendar','',0,null,1,'/zul/calendar.zul','Y','T','Y');

INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_MX',0,now(),0,'Actividades de flujo de trabajo, avisos y solicitudes','Y','Y','Actividades',50000,'2011-08-25 09:13:42',0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_MX',0,now(),0,'Favoritos de usuario','Y','Y','Favoritos',50001,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_MX',0,now(),0,'Vistas de Información','Y','Y','Vistas',50002,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_MX',0,now(),0,'Indicadores de Desempeño','Y','Y','Desempeño',50003,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (11,'es_MX',0,now(),0,'Calendario de Google','Y','Y','Calendario',50004,now(),0);

INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_AR',0,now(),0,'Actividades de flujo de trabajo, avisos y solicitudes','Y','Y','Actividades',50000,'2011-08-25 09:13:42',0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_AR',0,now(),0,'Favoritos de usuario','Y','Y','Favoritos',50001,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_AR',0,now(),0,'Vistas de Información','Y','Y','Vistas',50002,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_AR',0,now(),0,'Indicadores de Desempeño','Y','Y','Desempeño',50003,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (11,'es_AR',0,now(),0,'Calendario de Google','Y','Y','Calendario',50004,now(),0);

INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_PY',0,now(),0,'Actividades de flujo de trabajo, avisos y solicitudes','Y','Y','Actividades',50000,'2011-08-25 09:13:42',0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_PY',0,now(),0,'Favoritos de usuario','Y','Y','Favoritos',50001,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_PY',0,now(),0,'Vistas de Información','Y','Y','Vistas',50002,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_PY',0,now(),0,'Indicadores de Desempeño','Y','Y','Desempeño',50003,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (11,'es_PY',0,now(),0,'Calendario de Google','Y','Y','Calendario',50004,now(),0);

INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_ES',0,now(),0,'Actividades de flujo de trabajo, avisos y solicitudes','Y','Y','Actividades',50000,'2011-08-25 09:13:42',0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_ES',0,now(),0,'Favoritos de usuario','Y','Y','Favoritos',50001,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_ES',0,now(),0,'Vistas de Información','Y','Y','Vistas',50002,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (0,'es_ES',0,now(),0,'Indicadores de Desempeño','Y','Y','Desempeño',50003,now(),0);
INSERT INTO pa_dashboardcontent_trl (ad_client_id, ad_language, ad_org_id, created, createdby, description, isactive, istranslated, name, pa_dashboardcontent_id, updated, updatedby)
VALUES (11,'es_ES',0,now(),0,'Calendario de Google','Y','Y','Calendario',50004,now(),0);

INSERT INTO AD_SYSCONFIG (ad_sysconfig_id, ad_client_id, ad_org_id, created, updated, createdby, updatedby, isactive, name, value, description, entitytype, configurationlevel)
VALUES (50018,0,0,now(),now(),100,100,'Y','ZK_PAGING_SIZE','100','Default paging size for grid view in zk webui','D','S');
INSERT INTO AD_SYSCONFIG (ad_sysconfig_id, ad_client_id, ad_org_id, created, updated, createdby, updatedby, isactive, name, value, description, entitytype, configurationlevel)
VALUES (50019,0,0,now(),now(),100,100,'Y','ZK_GRID_EDIT_MODELESS','N','Grid view will enter in edit mode','D','S');
INSERT INTO AD_SYSCONFIG (ad_sysconfig_id, ad_client_id, ad_org_id, created, updated, createdby, updatedby, isactive, name, value, description, entitytype, configurationlevel)
VALUES (50020,0,0,now(),now(),100,100,'Y','ZK_DASHBOARD_REFRESH_INTERVAL','60000','Milliseconds of wait to run the dashboard refresh on zk webui client','D','S');
INSERT INTO AD_SYSCONFIG (ad_sysconfig_id, ad_client_id, ad_org_id, created, updated, createdby, updatedby, isactive, name, value, description, entitytype, configurationlevel)
VALUES (50021,0,0,now(),now(),100,100,'Y','ZK_DESKTOP_CLASS','org.adempiere.webui.desktop.DefaultDesktop','package+classname of zk desktop class','D','S');
INSERT INTO AD_SYSCONFIG (ad_sysconfig_id, ad_client_id, ad_org_id, created, updated, createdby, updatedby, isactive, name, value, description, entitytype, configurationlevel)
VALUES (50025,0,0,now(),now(),100,100,'Y','WEBUI_LOGOURL','/images/AD10030.png','url for the logo in zkwebui','D','S');
INSERT INTO AD_SYSCONFIG (ad_sysconfig_id, ad_client_id, ad_org_id, created, updated, createdby, updatedby, isactive, name, value, description, entitytype, configurationlevel)
VALUES (50037,0,0,now(),now(),100,100,'Y','ZK_LOGIN_ALLOW_REMEMBER_ME','U','Allow remember me on zkwebui - allowed values [U]ser / [P]assword / [N]one','D','S');
INSERT INTO AD_SYSCONFIG (ad_sysconfig_id, ad_client_id, ad_org_id, created, updated, createdby, updatedby, isactive, name, value, description, entitytype, configurationlevel)
VALUES (50050,0,0,now(),now(),100,100,'Y','ZK_ROOT_FOLDER_BROWSER','/opt/adempiere/current/data','Root Folder to be used when opening server files.','D','S');

-- 20130204-1135 Nuevas modificaciones a funcionalidad de replicacion
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
	recordColumns RECORD;
	columnValue varchar;
	isValid integer;
	v_valueDetail varchar;
	v_nameDetail varchar;
	v_columnname varchar;
        v_tableid int; 
        v_tablename varchar;
        v_referenceStr varchar;
	v_referencedTableStr varchar;
	v_referencedTableID int;
	v_existsValueField int;
	v_existsNameField int;
	shouldcheckreferences boolean;
BEGIN 
	-- se deberan verificar referencias a registros fuera del esquema de replicacion? (inicialmente no)
	shouldcheckreferences := false;

	-- estamos en una accion de eliminacion?
	IF (TG_OP = 'DELETE') THEN

		-- Checkear switch maestro de replicacion
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;

		-- Se repArray es nulo o vacio, no hay mas que hacer dado que es un registro fuera de replicacion
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;

		-- El registro fue replicado? Si no lo fue puede ser eliminado, pero en caso contrario hay que registrar su eliminacion
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN

			-- Recuperar el repArray de la tabla en cuestion
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
			v_newRepArray := replace(v_newRepArray, '3', '1');
			v_newRepArray := replace(v_newRepArray, '2', '0');

			-- Insertar en la tabla de eliminaciones
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues, includeInReplication)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',now(),OLD.CreatedBy,now(),OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null,'Y';
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	-- estamos en una accion de insercion o actualizacion?
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Checkear switch maestro de replicacion		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;

		-- El uso de SKIP se supone para omitir acciones posteriores en eliminacion (dado que setea repArray en NULL)
		IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		END IF;

		-- Verificar si hay que generar el retrieveUID. Ejemplo: h1_291
		IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			-- Obtener posicion de host
			SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
			IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
			-- Obtener siguiente valor para la tabla dada
			SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
			IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
			NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq || '_' || lower(TG_ARGV[1]);
		END IF;		

		-- Si estamos insertando...
		IF (TG_OP = 'INSERT') THEN

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- Recuperar el repArray
				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;

				-- Si es nulo o vacio no hacer nada mas
				IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
					RETURN NEW;
				END IF;

				-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
				NEW.repArray := replace(v_newRepArray, '3', '1');
				NEW.repArray := replace(NEW.repArray, '2', '0');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 1)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('1' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				ELSE
					NEW.repArray := NULL;
				END IF;
			END IF;
			
		-- Si estamos actualizando...
		ELSEIF (TG_OP = 'UPDATE') THEN 

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.		
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- El repArray no esta seteado todavia? (Caso: modificacion de un registro preexistente)
				IF (OLD.repArray IS NULL OR OLD.repArray = '0') THEN

					-- Recuperar el repArray
					SELECT INTO v_newRepArray replicationArray 
					FROM ad_tablereplication 
					WHERE ad_table_ID = TG_ARGV[0]::int;

					-- Cambiar 2 (recibir) por 0 (sin accion)
					NEW.repArray := replace(v_newRepArray, '2', '0');
					-- Cambiar 1 (enviar) y 3 (bidireccional) por 2 (replicado)
					NEW.repArray := replace(NEW.repArray, '1', '2');
					NEW.repArray := replace(NEW.repArray, '3', '2');
				END IF;

				-- Cambiar los 2 (replicado) por 3 (modificado).
				-- Adicionalmente para JMS: 4 (espera ack) por 5 (cambios luego de ack)
				NEW.repArray := replace(NEW.repArray, '2', '3');
				NEW.repArray := replace(NEW.repArray, '4', '5');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 3)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('3' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				END IF;
			END IF;
		END IF;
	END IF;

	IF (shouldcheckreferences = true) THEN

		-- Validar referencias iterando todas las columnas de la tabla
		FOR recordColumns IN
			SELECT isc.column_name, isc.data_type, c.ad_column_id, t.tablename, t.ad_table_id
			FROM information_schema.columns isc
			INNER JOIN ad_table t ON lower(isc.table_name) = lower(t.tablename)
			INNER JOIN ad_column c ON lower(isc.column_name) = lower(c.columnname) AND t.ad_table_id = c.ad_table_id
			WHERE table_name = quote_ident(TG_TABLE_NAME)
			AND isc.data_type = 'integer'
			AND isc.column_name not in ('retrieveuid', 'reparray', 'datelastsentjms', 'includeinreplication')
		LOOP
			-- Obtener el value de la columna y verificar si es referencia valida.  En caso de no serlo, presentar error correspondiente
			EXECUTE 'SELECT (' || quote_literal(NEW) || '::' || TG_RELID::regclass || ').' || quote_ident(recordColumns.column_name) INTO columnValue;
			SELECT INTO isValid replication_is_valid_reference(recordColumns.ad_column_id, columnvalue);
			IF isValid = 0 THEN

				-- valores por defecto
				v_valueDetail := '';
				v_nameDetail := '';
				v_referenceStr := '';
				
				-- recuperar el nombre de la columna y tabla para brindar un mensaje mas intuitivo al usuario
				v_columnname := recordColumns.column_name;
				v_tablename := recordColumns.tablename;

				BEGIN 
					-- recuperar el nombre o value del registro referenciado a fin de mejorar la legibilidad del mensaje de error
					SELECT INTO v_referencedTableStr replication_get_referenced_table(recordColumns.ad_column_id);
					SELECT INTO v_referencedTableID AD_Table_ID FROM AD_Table WHERE tablename ilike v_referencedTableStr;

					-- ver si existe las columnas value y name
					SELECT INTO v_existsValueField Count(1) FROM AD_Column WHERE columnname ilike 'Value' AND AD_Table_ID = v_referencedTableID;
					SELECT INTO v_existsNameField Count(1) FROM AD_Column WHERE columnname ilike 'Name' AND AD_Table_ID = v_referencedTableID;

					-- cargar value y name
					IF v_existsValueField = 1 THEN
						EXECUTE 'SELECT value FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_valueDetail;
						v_referenceStr := v_valueDetail;
					END IF;
					IF v_existsNameField = 1 THEN
						EXECUTE 'SELECT name FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_nameDetail;
						v_referenceStr := v_referenceStr || ' ' || v_nameDetail;
					END IF;
				EXCEPTION
					WHEN OTHERS THEN
						-- do nothing
				END;
				
				-- concatenar acordemente para mensaje de retorno
				RAISE EXCEPTION 'Validacion de replicación - La columna: % (%) de la tabla: % (%) referencia al registro: % (%), fuera del sistema de replicacion.', v_columnname, recordColumns.ad_column_id, v_tablename, recordColumns.ad_table_id, v_referenceStr, columnvalue;
			END IF;
		END LOOP;

	END IF;
	RETURN NEW;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

ALTER TABLE ad_replicationhost ADD column username varchar null;
ALTER TABLE ad_replicationhost ADD column password varchar null;

-- 20130205-1105 - Incorporaciones faltantes necesarias para desarrollo Libertya Web 
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_Role','connectionprofile', 'character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_Role','userdiscount', 'numeric(22,0)'));

-- 20130214-1430 Nueva vista para el reporte de existencias por producto adicionando información del artículo como proveedor, precio de costo y venta
CREATE OR REPLACE VIEW rv_storage_product_plus AS 
SELECT ad_client_id, ad_org_id, m_product_id, value, name, m_product_category_id, m_warehouse_id, qtyonhand, qtyreserved, qtyavailable, qtyordered, (SELECT c_bpartner_id FROM m_product_po po WHERE po.m_product_id = p.m_product_id ORDER BY iscurrentvendor LIMIT 1) as c_bpartner_id, coalesce((SELECT pp.pricelist FROM m_productprice as pp INNER JOIN m_pricelist_version as plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id INNER JOIN m_pricelist as pl ON pl.m_pricelist_id = plv.m_pricelist_id WHERE pl.issopricelist = 'Y' AND pl.isactive = 'Y' AND plv.isactive = 'Y' AND pp.isactive = 'Y' AND (pl.ad_org_id = p.ad_org_id OR pl.ad_org_id = 0) AND pp.m_product_id = p.m_product_id ORDER BY pl.ad_org_id DESC, plv.validfrom DESC LIMIT 1),0) as sales_pricelist, coalesce((SELECT pp.pricelist FROM m_productprice as pp INNER JOIN m_pricelist_version as plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id INNER JOIN m_pricelist as pl ON pl.m_pricelist_id = plv.m_pricelist_id WHERE pl.issopricelist = 'N' AND pl.isactive = 'Y' AND plv.isactive = 'Y' AND pp.isactive = 'Y' AND (pl.ad_org_id = p.ad_org_id OR pl.ad_org_id = 0) AND pp.m_product_id = p.m_product_id ORDER BY pl.ad_org_id DESC, plv.validfrom DESC LIMIT 1),0) as cost_pricelist
 FROM (SELECT s.ad_client_id, s.ad_org_id, s.m_product_id, s.value, s.name, s.m_product_category_id, s.m_warehouse_id, sum(s.qtyonhand) AS qtyonhand, sum(s.qtyreserved) AS qtyreserved, sum(s.qtyavailable) AS qtyavailable, sum(s.qtyordered) AS qtyordered
   FROM ( SELECT s.ad_client_id, s.ad_org_id, s.m_product_id, p.value, p.name, p.m_product_category_id, l.m_warehouse_id, s.qtyonhand, s.qtyreserved, s.qtyonhand - s.qtyreserved AS qtyavailable, s.qtyordered
           FROM m_storage s
      JOIN m_locator l ON s.m_locator_id = l.m_locator_id
   JOIN m_product p ON s.m_product_id = p.m_product_id) s
  GROUP BY s.ad_client_id, s.ad_org_id, s.m_product_id, s.value, s.name, s.m_product_category_id, s.m_warehouse_id) as p;

ALTER TABLE rv_storage_product_plus OWNER TO libertya;

--20130215-1314 Nueva columna que permite registrar el medio de cobro a crédito 
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice','c_pospaymentmedium_credit_id', 'integer'));

--20130222-1125 Incorporación de columna netamount a la tabla C_Invoice
update ad_system set dummy = (SELECT addcolumnifnotexists('c_invoice','netamount', 'numeric(20,2) NOT NULL DEFAULT 0'));

--20130303-1050 Funcion para determinar el nro de registros, pendientes de replicar
CREATE TYPE rep_count AS (tablename varchar, recordcount int);
CREATE OR REPLACE FUNCTION replication_record_count(p_clientid integer, p_xtraclause character varying)
  RETURNS SETOF rep_count AS
$BODY$
DECLARE
	atable varchar;
	tablenames varchar;
	astatus rep_count;
	statuses record;
	xtraclause varchar;
BEGIN
	xtraclause := ' 1 = 1 ';
	IF p_xtraclause is not null THEN
		xtraclause := p_xtraclause;
	END IF;

	tablenames := '';
	FOR atable IN (select table_name from information_schema.columns where lower(column_name) = 'reparray' and table_schema = 'libertya' order by table_name)  LOOP
		FOR astatus IN EXECUTE 'SELECT ''' || atable || ''' as tablename, count(1) as records FROM ' || atable || ' WHERE ad_client_id = ' || p_clientid || '  AND ' || xtraclause || ' GROUP BY tablename ' LOOP
			return next astatus;
		END LOOP;
	END LOOP;
END
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION replication_record_count(integer, character varying) OWNER TO libertya;

-- 20130308:1114 Ampliacion en validacion de referencias para soportar tablas no incluidas en replicacion, pero que contienen AD_ComponentObjectUID
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

        -- si la tabla destino directamente no tiene la columna retrieveuid, 
        -- verificar si tiene la ad_componentobjectuid, sino entonces devolver que es invalido
        SELECT INTO isValid count(1) FROM information_schema.columns
            WHERE lower(column_name) = 'retrieveuid' AND lower(table_name) = lower(targetTableName);
        IF isValid = 0 THEN
	    SELECT INTO isValid count(1) FROM information_schema.columns
                WHERE lower(column_name) = 'ad_componentobjectuid' AND lower(table_name) = lower(targetTableName);
            IF isValid = 0 THEN
		return 0;
	    END IF;
	    -- Tiene un dato seteado en ad_componentobjectuid?
	    EXECUTE 'select count(1) FROM ' || targetTableName ||
			' WHERE ' || targetTableName || '_ID = ' || column_data::int ||
			' AND ( ad_componentobjectuid is NOT null )' INTO isValid;
		return isValid;
        END IF;

	-- comparar el replicationArray de la tabla origen y de la tabla destino:
	-- unicamente si son iguales se podrá replicar, en caso contrario devolver que no
	-- contemplar caso de tablas bidireccionales, aqui solo importa que exista envio hacia el otro host
	-- (reemplazar 3 (bidireccional) por 1 (enviar)
	SELECT INTO sourceRepArray replace(replicationArray, '3', '1') FROM AD_TableReplication where ad_table_id = sourceTableID;
	SELECT INTO targetRepArray replace(replicationArray, '3', '1') FROM AD_TableReplication where ad_table_id = targetTableID;

	-- si los repArray de las tablas origen y destino son iguales, entonces todo bien
	IF sourceRepArray = targetRepArray THEN
		return 1;
	END IF;

	-- si los repArray de las tablas origen y destino son diferentes (y la destino esta marcada para replicar),
	-- entonces no puede referenciarse el registro, ya que existirá en algunos hosts y en otros no.
	IF sourceRepArray <> targetRepArray AND (position('1' in targetRepArray) > 0) THEN
		return 0;
	END IF;

	-- si son diferentes porque se debe a que la tabla destino tiene 
	-- repArray con posiciones de replicación (1),habra que analizar el registro en cuestión:

        -- hay que ver si se esta referenciando a 
		-- 1) un registro ya existente en el core (retrieveuid debe iniciar con o),
		-- 2) o bien a un registro proveniente de otra sucursal (no generado localmente)
        EXECUTE 'select count(1) FROM ' || targetTableName ||
            ' WHERE ' || targetTableName || '_ID = ' || column_data::int ||
            ' AND ( retrieveuid NOT ilike ''h' || hostID::varchar || '_%'' )' INTO isValid;       
       
        return isValid;
    END IF;
   
    return 1;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_is_valid_reference(integer, character varying) OWNER TO libertya;

-- 20130311-1020 Si retrieveUID es null, cargar valor de AD_ComponentObjectUID (en caso de que este exista y no sea null)
CREATE OR REPLACE FUNCTION replication_event()
  RETURNS trigger AS
$BODY$
DECLARE 
	found integer; 
	replicationPos integer;
	v_newRepArray varchar; 
	aKeyColumn varchar;
	repSeq bigint;
	shouldReplicate varchar;
	recordColumns RECORD;
	columnValue varchar;
	isValid integer;
	v_valueDetail varchar;
	v_nameDetail varchar;
	v_columnname varchar;
        v_tableid int; 
        v_tablename varchar;
        v_referenceStr varchar;
	v_referencedTableStr varchar;
	v_referencedTableID int;
	v_existsValueField int;
	v_existsNameField int;
	shouldcheckreferences boolean;
BEGIN 
	-- se deberan verificar referencias a registros fuera del esquema de replicacion? (inicialmente no)
	shouldcheckreferences := false;

	-- estamos en una accion de eliminacion?
	IF (TG_OP = 'DELETE') THEN

		-- Checkear switch maestro de replicacion
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN OLD;
		END IF;

		-- Se repArray es nulo o vacio, no hay mas que hacer dado que es un registro fuera de replicacion
		IF (OLD.repArray IS NULL OR OLD.repArray = '') THEN
			RETURN OLD;
		END IF;

		-- El registro fue replicado? Si no lo fue puede ser eliminado, pero en caso contrario hay que registrar su eliminacion
		IF replication_is_record_replicated(OLD.repArray) = 1 THEN

			-- Recuperar el repArray de la tabla en cuestion
			SELECT INTO v_newRepArray replicationArray 
			FROM ad_tablereplication 
			WHERE ad_table_ID = TG_ARGV[0]::int;

			-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
			v_newRepArray := replace(v_newRepArray, '3', '1');
			v_newRepArray := replace(v_newRepArray, '2', '0');

			-- Insertar en la tabla de eliminaciones
			IF v_newRepArray IS NOT NULL AND v_newRepArray <> '' THEN
				INSERT INTO ad_changelog_replication (AD_Changelog_Replication_ID, AD_Client_ID, AD_Org_ID, isActive, Created, CreatedBy, Updated, UpdatedBy, AD_Table_ID, retrieveUID, operationtype, binaryvalue, reparray, columnvalues, includeInReplication)
				SELECT nextval('seq_ad_changelog_replication'),OLD.AD_Client_ID,OLD.AD_Org_ID,'Y',now(),OLD.CreatedBy,now(),OLD.UpdatedBy,TG_ARGV[0]::int,OLD.retrieveUID,'I',null,v_newRepArray,null,'Y';
			END IF;
		END IF;	
		
		RETURN OLD;
	END IF;
	-- estamos en una accion de insercion o actualizacion?
	IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

		-- Checkear switch maestro de replicacion		
		SELECT INTO shouldReplicate VALUE FROM AD_PREFERENCE WHERE ATTRIBUTE = 'ReplicationEventsActive';
		IF (shouldReplicate <> 'Y') THEN
			RETURN NEW;
		END IF;

		-- El uso de SKIP se supone para omitir acciones posteriores en eliminacion (dado que setea repArray en NULL)
		IF (NEW.repArray = 'SKIP') THEN
			NEW.repArray := NULL;
			return NEW;
		END IF;

		-- Verificar si hay que generar el retrieveUID. Ejemplo: h1_291
		IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
			-- Primeramente intentar utilizar el AD_ComponentObjectUID (registro perteneciente a un componente)
			BEGIN
				IF NEW.AD_ComponentObjectUID IS NOT NULL AND NEW.AD_ComponentObjectUID <> '' THEN
					NEW.retrieveUID = NEW.AD_ComponentObjectUID;
				END IF;
			EXCEPTION
				WHEN OTHERS THEN
					-- Do nothing
			END;
			-- Si el registro no pertenece a un componente, generar el retrieveUID
		        IF NEW.retrieveUID IS NULL OR NEW.retrieveUID = '' THEN 
				-- Obtener posicion de host
				SELECT INTO replicationPos replicationArrayPos FROM AD_ReplicationHost WHERE thisHost = 'Y'; 
				IF replicationPos IS NULL THEN RAISE EXCEPTION 'Configuracion de Hosts incompleta: Ninguna sucursal tiene marca de Este Host'; END IF; 
				-- Obtener siguiente valor para la tabla dada
				SELECT INTO repseq nextVal('repseq_' || TG_ARGV[1]);
				IF repseq IS NULL THEN RAISE EXCEPTION 'No hay definida una secuencia de replicacion para la tabla %', TG_ARGV[1]; END IF;
					NEW.retrieveUID := 'h'::varchar || replicationPos::varchar || '_' || repseq || '_' || lower(TG_ARGV[1]);
				END IF;		
			END IF;

		-- Si estamos insertando...
		IF (TG_OP = 'INSERT') THEN

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- Recuperar el repArray
				SELECT INTO v_newRepArray replicationArray 
				FROM ad_tablereplication 
				WHERE ad_table_ID = TG_ARGV[0]::int;

				-- Si es nulo o vacio no hacer nada mas
				IF v_newRepArray IS NULL OR v_newRepArray = '' THEN
					RETURN NEW;
				END IF;

				-- Cambiar 3 (replicacion bidireccional) por 1 (enviar); y 2 (recibir) por 0 (sin accion)
				NEW.repArray := replace(v_newRepArray, '3', '1');
				NEW.repArray := replace(NEW.repArray, '2', '0');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 1)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('1' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				ELSE
					NEW.repArray := NULL;
				END IF;
			END IF;
			
		-- Si estamos actualizando...
		ELSEIF (TG_OP = 'UPDATE') THEN 

			-- Si se indico el repArray con SET, entonces se esta configurando el registro manualmente.  No hacer nada mas.		
			IF (substr(NEW.repArray, 1, 3) = 'SET') THEN
				NEW.repArray := substr(NEW.repArray, 4, length(NEW.repArray)-3);
			ELSE
				-- El repArray no esta seteado todavia? (Caso: modificacion de un registro preexistente)
				IF (OLD.repArray IS NULL OR OLD.repArray = '0') THEN

					-- Recuperar el repArray
					SELECT INTO v_newRepArray replicationArray 
					FROM ad_tablereplication 
					WHERE ad_table_ID = TG_ARGV[0]::int;

					-- Cambiar 2 (recibir) por 0 (sin accion)
					NEW.repArray := replace(v_newRepArray, '2', '0');
					-- Cambiar 1 (enviar) y 3 (bidireccional) por 2 (replicado)
					NEW.repArray := replace(NEW.repArray, '1', '2');
					NEW.repArray := replace(NEW.repArray, '3', '2');
				END IF;

				-- Cambiar los 2 (replicado) por 3 (modificado).
				-- Adicionalmente para JMS: 4 (espera ack) por 5 (cambios luego de ack)
				NEW.repArray := replace(NEW.repArray, '2', '3');
				NEW.repArray := replace(NEW.repArray, '4', '5');
				-- Si el registro deberá replicar hacia otros hosts (hay al menos un 3)
				-- entonces debe incluirse en replicacion y hay que check referencias
				IF (position('3' in NEW.repArray) > 0) THEN
					NEW.includeInReplication = 'Y';
					shouldcheckreferences := true;
				END IF;
			END IF;
		END IF;
	END IF;

	IF (shouldcheckreferences = true) THEN

		-- Validar referencias iterando todas las columnas de la tabla
		FOR recordColumns IN
			SELECT isc.column_name, isc.data_type, c.ad_column_id, t.tablename, t.ad_table_id
			FROM information_schema.columns isc
			INNER JOIN ad_table t ON lower(isc.table_name) = lower(t.tablename)
			INNER JOIN ad_column c ON lower(isc.column_name) = lower(c.columnname) AND t.ad_table_id = c.ad_table_id
			WHERE table_name = quote_ident(TG_TABLE_NAME)
			AND isc.data_type = 'integer'
			AND isc.column_name not in ('retrieveuid', 'reparray', 'datelastsentjms', 'includeinreplication')
		LOOP
			-- Obtener el value de la columna y verificar si es referencia valida.  En caso de no serlo, presentar error correspondiente
			EXECUTE 'SELECT (' || quote_literal(NEW) || '::' || TG_RELID::regclass || ').' || quote_ident(recordColumns.column_name) INTO columnValue;
			SELECT INTO isValid replication_is_valid_reference(recordColumns.ad_column_id, columnvalue);
			IF isValid = 0 THEN

				-- valores por defecto
				v_valueDetail := '';
				v_nameDetail := '';
				v_referenceStr := '';
				
				-- recuperar el nombre de la columna y tabla para brindar un mensaje mas intuitivo al usuario
				v_columnname := recordColumns.column_name;
				v_tablename := recordColumns.tablename;

				BEGIN 
					-- recuperar el nombre o value del registro referenciado a fin de mejorar la legibilidad del mensaje de error
					SELECT INTO v_referencedTableStr replication_get_referenced_table(recordColumns.ad_column_id);
					SELECT INTO v_referencedTableID AD_Table_ID FROM AD_Table WHERE tablename ilike v_referencedTableStr;

					-- ver si existe las columnas value y name
					SELECT INTO v_existsValueField Count(1) FROM AD_Column WHERE columnname ilike 'Value' AND AD_Table_ID = v_referencedTableID;
					SELECT INTO v_existsNameField Count(1) FROM AD_Column WHERE columnname ilike 'Name' AND AD_Table_ID = v_referencedTableID;

					-- cargar value y name
					IF v_existsValueField = 1 THEN
						EXECUTE 'SELECT value FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_valueDetail;
						v_referenceStr := v_valueDetail;
					END IF;
					IF v_existsNameField = 1 THEN
						EXECUTE 'SELECT name FROM ' || v_referencedTableStr || ' WHERE ' || v_referencedTableStr || '_ID = ' || columnvalue || '::int' INTO v_nameDetail;
						v_referenceStr := v_referenceStr || ' ' || v_nameDetail;
					END IF;
				EXCEPTION
					WHEN OTHERS THEN
						-- do nothing
				END;
				
				-- concatenar acordemente para mensaje de retorno
				RAISE EXCEPTION 'Validacion de replicación - La columna: % (%) de la tabla: % (%) referencia al registro: % (%), fuera del sistema de replicacion.', v_columnname, recordColumns.ad_column_id, v_tablename, recordColumns.ad_table_id, v_referenceStr, columnvalue;
			END IF;
		END LOOP;

	END IF;
	RETURN NEW;
END; 
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION replication_event() OWNER TO libertya;

-- 20130311-1200 Nueva vista para movimientos de productos detallados
CREATE OR REPLACE VIEW v_product_movements_detailed AS 
select t.movement_table, 
	t.ad_client_id, 
	t.ad_org_id, 
	t.m_locator_id, 
	w.m_warehouse_id, 
	w.value as warehouse_value, 
	w.name as warehouse_name, 
	t.receiptvalue, 
	t.movementdate, 
	t.doctypename, 
	t.documentno, 
	t.docstatus, 
	t.m_product_id,
	t.product_value, 
	t.product_name, 
	t.qty, 
	t.c_invoice_id, 
	i.documentno as invoice_documentno
FROM (
select 'M_InOut' as movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, (CASE dt.signo_issotrx WHEN 1 THEN 'Y' ELSE 'N' END) as receiptvalue, t.movementdate, dt.name as doctypename, io.documentno, io.docstatus, p.m_product_id, p.value as product_value, p.name as product_name, abs(t.movementqty) as qty, (select i.c_invoice_id from c_order as o inner join c_invoice as i on i.c_order_id = o.c_order_id WHERE o.c_order_id = io.c_order_id limit 1) as c_invoice_id
from m_transaction as t
inner join m_inoutline as iol on iol.m_inoutline_id = t.m_inoutline_id
inner join m_product as p on p.m_product_id = t.m_product_id
inner join m_inout as io on io.m_inout_id = iol.m_inout_id
inner join c_doctype as dt on dt.c_doctype_id = io.c_doctype_id
UNION ALL
select 'M_Movement' as movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, (CASE abs(t.movementqty) WHEN t.movementqty THEN 'Y' ELSE 'N' END) as receiptvalue, t.movementdate, dt.name as doctypename, m.documentno, m.docstatus, p.m_product_id, p.value as product_value, p.name as product_name, abs(t.movementqty) as qty, null as c_invoice_id
from m_transaction as t
inner join m_movementline as ml on ml.m_movementline_id = t.m_movementline_id
inner join m_product as p on p.m_product_id = t.m_product_id
inner join m_movement as m on m.m_movement_id = ml.m_movement_id
inner join c_doctype as dt on dt.c_doctype_id = m.c_doctype_id
UNION ALL
select 'M_Inventory' as movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, (CASE abs(t.movementqty) WHEN t.movementqty THEN 'Y' ELSE 'N' END) as receiptvalue, t.movementdate, dt.name as doctypename, i.documentno, i.docstatus, p.m_product_id, p.value as product_value, p.name as product_name, abs(t.movementqty) as qty, null as c_invoice_id
from m_transaction as t
inner join m_inventoryline as il on il.m_inventoryline_id = t.m_inventoryline_id
inner join m_product as p on p.m_product_id = t.m_product_id
inner join m_inventory as i on i.m_inventory_id = il.m_inventory_id
inner join c_doctype as dt on dt.c_doctype_id = i.c_doctype_id
left join m_transfer as tr on tr.m_inventory_id = i.m_inventory_id
left join m_splitting as sp on sp.m_inventory_id = i.m_inventory_id
where tr.m_transfer_id is null and sp.m_splitting_id is null
UNION ALL
select 'M_Transfer' as movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, (CASE abs(t.movementqty) WHEN t.movementqty THEN 'Y' ELSE 'N' END) as receiptvalue, t.movementdate, transfertype as doctypename, tr.documentno, tr.docstatus, p.m_product_id, p.value as product_value, p.name as product_name, abs(t.movementqty) as qty, null as c_invoice_id
from m_transaction as t
inner join m_inventoryline as il on il.m_inventoryline_id = t.m_inventoryline_id
inner join m_product as p on p.m_product_id = t.m_product_id
inner join m_inventory as i on i.m_inventory_id = il.m_inventory_id
inner join m_transfer as tr on tr.m_inventory_id = i.m_inventory_id
UNION ALL
select 'M_Splitting' as movement_table, t.ad_client_id, t.ad_org_id, t.m_locator_id, (CASE abs(t.movementqty) WHEN t.movementqty THEN 'Y' ELSE 'N' END) as receiptvalue, t.movementdate, 'M_Splitting_ID' as doctypename, sp.documentno, sp.docstatus, p.m_product_id, p.value as product_value, p.name as product_name, abs(t.movementqty) as qty, null as c_invoice_id
from m_transaction as t
inner join m_inventoryline as il on il.m_inventoryline_id = t.m_inventoryline_id
inner join m_product as p on p.m_product_id = t.m_product_id
inner join m_inventory as i on i.m_inventory_id = il.m_inventory_id
inner join m_splitting as sp on sp.m_inventory_id = i.m_inventory_id) as t
INNER JOIN m_locator as l on l.m_locator_id = t.m_locator_id
INNER JOIN m_warehouse as w ON w.m_warehouse_id = l.m_warehouse_id
LEFT JOIN c_invoice as i ON i.c_invoice_id = t.c_invoice_id;

ALTER TABLE v_product_movements_detailed OWNER TO libertya;

-- 20130313-1448 Incorporación de nueva columna en el tipo de documento para permitir o no entregar mercadería devuelta
 update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','allowdeliveryreturned', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));
 
-- 20130315-1415 Nueva columna para soport LYWeb
ALTER TABLE AD_Process ADD COLUMN showHelp CHAR(1) DEFAULT 'Y';

-- 20130323-0119 Vista modificada para soporte de cajas diarias
DROP VIEW v_dailysales;

CREATE OR REPLACE VIEW v_dailysales AS 
        (        (         SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                                    ELSE p.c_pospaymentmedium_id
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                                    ELSE ppm.name
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
                 JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
                           FROM c_invoice i
                           LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
                      JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                 LEFT JOIN ( SELECT c.c_invoice_id, sum(c.amount) AS amount
                              FROM ( SELECT 
                                           CASE
                                               WHEN (dt.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                               ELSE i.c_invoice_id
                                           END AS c_invoice_id, pjp.amount
                                      FROM c_posjournalpayments_v pjp
                                 JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                            JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
                       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                  JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
             JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
        LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND hdr.isactive = 'Y'::bpchar) c
                             GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
                   FROM c_invoice i
                   LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
    JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id)
UNION ALL 
         SELECT 'PCA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                    ELSE i.c_invoice_id
                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                    ELSE pjp.c_invoice_credit_id
                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.c_doctype_id
                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.c_doctype_id
                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.c_pospaymentmedium_id
                    ELSE p.c_pospaymentmedium_id
                END AS c_pospaymentmedium_id, 
                CASE
                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dtc.docbasetype::character varying
                    WHEN (dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN dt.docbasetype::character varying
                    WHEN pjp.c_cashline_id IS NOT NULL THEN ppmc.name
                    ELSE ppm.name
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
      LEFT JOIN c_posjournal as pj on pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);

ALTER TABLE v_dailysales OWNER TO libertya;

-- 20130324-0040 Vista fixeada para los payments sin allocation
CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
        (        ( SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
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
                            WHEN al.c_cashline_id IS NOT NULL THEN ((c.name::text || '_#'::text) || cl.line::text)::character varying
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.documentno
                            ELSE NULL::character varying
                        END::character varying(255) AS info, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, cl.c_cash_id, cl.line, ic.c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, ah.datetrx) AS allocationdate, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.docstatus
                            WHEN al.c_cashline_id IS NOT NULL THEN cl.docstatus
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.docstatus
                            ELSE NULL::character(2)
                        END AS docstatus, 
                        CASE
                            WHEN al.c_payment_id IS NOT NULL THEN p.dateacct::date
                            WHEN al.c_cashline_id IS NOT NULL THEN c.dateacct::date
                            WHEN al.c_invoice_credit_id IS NOT NULL THEN ic.dateacct::date
                            ELSE NULL::date
                        END AS dateacct
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
END::character varying(30))
        UNION ALL 
                 SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::unknown AS c_invoice_id, NULL::unknown AS c_payment_id, cl.c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CA'::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || '_#'::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::unknown AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc('day'::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct
                   FROM c_cashline cl
              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
             WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
                      FROM c_allocationline al
                     WHERE al.c_cashline_id = cl.c_cashline_id)))
UNION ALL 
        ( SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::unknown AS c_invoice_id, p.c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, p.payamt AS amount, NULL::unknown AS c_cash_id, NULL::numeric(18,0) AS line, NULL::unknown AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
      where NOT (EXISTS ( SELECT al.c_allocationline_id
                      FROM c_allocationline al
                     WHERE al.c_payment_id = p.c_payment_id))
     ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

-- 20130328-1410 Se insertaron las columnas para exportación ARCIBA
ALTER TABLE C_Tax ADD COLUMN arcibanormcode character varying(10);
ALTER TABLE C_InvoiceTax ADD COLUMN arcibanormcode character varying(10);
ALTER TABLE C_InvoiceTax ADD COLUMN rate numeric(24,6);

-- 20130328-2021 Nueva estructura para guardar horas y hora de inicio y fin para el reporte de Ventas por Horario
CREATE TABLE c_salesbyhour_hours
(
  c_salesbyhour_hours_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  hour integer NOT NULL,
  date timestamp NOT NULL,
  date_to timestamp NOT NULL,
  CONSTRAINT c_salesbyhour_hours_key PRIMARY KEY (c_salesbyhour_hours_id)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_salesbyhour_hours OWNER TO libertya;

CREATE SEQUENCE seq_c_salesbyhour_hours
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1000000
  CACHE 1;
ALTER TABLE seq_c_salesbyhour_hours OWNER TO libertya;

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 7, '2013-03-01 07:00:00'::timestamp, '2013-03-01 07:59:00'::timestamp);
            
INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 8, '2013-03-01 08:00:00'::timestamp, '2013-03-01 08:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 9, '2013-03-01 09:00:00'::timestamp, '2013-03-01 09:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 10, '2013-03-01 10:00:00'::timestamp, '2013-03-01 10:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 11, '2013-03-01 11:00:00'::timestamp, '2013-03-01 11:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 12, '2013-03-01 12:00:00'::timestamp, '2013-03-01 12:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 13, '2013-03-01 13:00:00'::timestamp, '2013-03-01 13:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 14, '2013-03-01 14:00:00'::timestamp, '2013-03-01 14:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 15, '2013-03-01 15:00:00'::timestamp, '2013-03-01 15:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 16, '2013-03-01 16:00:00'::timestamp, '2013-03-01 16:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 17, '2013-03-01 17:00:00'::timestamp, '2013-03-01 17:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 18, '2013-03-01 18:00:00'::timestamp, '2013-03-01 18:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 19, '2013-03-01 19:00:00'::timestamp, '2013-03-01 19:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 20, '2013-03-01 20:00:00'::timestamp, '2013-03-01 20:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 21, '2013-03-01 21:00:00'::timestamp, '2013-03-01 21:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 22, '2013-03-01 22:00:00'::timestamp, '2013-03-01 22:59:00'::timestamp);

INSERT INTO c_salesbyhour_hours(
            c_salesbyhour_hours_id, ad_client_id, ad_org_id, isactive, created, 
            createdby, updated, updatedby, "hour", "date", date_to)
    VALUES (nextval('seq_c_salesbyhour_hours'), 1010016, 0, 'Y', now(), 
            104, now(), 104, 23, '2013-03-01 23:00:00'::timestamp, '2013-03-01 23:59:00'::timestamp);

-- 20130328-2021 Modificación a la vista de detalle de allocation line para mostrar el nombre del paymentn medium
DROP VIEW c_allocation_detail_v;

CREATE OR REPLACE VIEW c_allocation_detail_v AS 
 SELECT ah.c_allocationhdr_id AS c_allocation_detail_v_id, ah.c_allocationhdr_id, ah.ad_client_id, ah.ad_org_id, ah.isactive, ah.created, ah.createdby, ah.updated, ah.updatedby, ah.datetrx AS fecha, i.documentno AS factura, COALESCE(i.c_currency_id, p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS c_currency_id, i.grandtotal AS montofactura, 
        CASE
            WHEN p.documentno IS NOT NULL THEN p.documentno
            ELSE 
            CASE
                WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
                ELSE NULL::character varying
            END
        END AS pagonro, 
        CASE
            WHEN p.tendertype IS NOT NULL THEN p.tendertype
            WHEN p.tendertype IS NULL THEN 'CA'::bpchar
            ELSE NULL::bpchar
        END AS tipo, 
        CASE
            WHEN cl.c_cashline_id IS NOT NULL THEN 'Y'::text
            WHEN cl.c_cashline_id IS NULL THEN 'N'::text
            ELSE NULL::text
        END AS cash, COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric(20,2)) AS montosaldado, abs(COALESCE(p.payamt, cl.amount, credit.grandtotal, 0::numeric(20,2))) AS payamt, al.c_allocationline_id, i.c_invoice_id, 
        CASE
            WHEN p.documentno IS NOT NULL THEN p.documentno
            ELSE 
            CASE
                WHEN al.c_invoice_credit_id IS NOT NULL THEN ((dt.printname::text || ' :'::text) || credit.documentno::text)::character varying
                WHEN al.c_cashline_id IS NOT NULL THEN cl.description
                ELSE NULL::character varying
            END
        END AS paydescription,
        CASE
            WHEN al.c_payment_id IS NOT NULL THEN pppm.name::character varying
            WHEN al.c_cashline_id IS NOT NULL THEN cppm.name::character varying
            WHEN al.c_invoice_credit_id IS NOT NULL THEN dt.name::character varying
            ELSE NULL::character varying
        END AS payment_medium_name
   FROM c_allocationhdr ah
   JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice credit ON al.c_invoice_credit_id = credit.c_invoice_id
   LEFT JOIN c_doctype dt ON credit.c_doctype_id = dt.c_doctype_id
   LEFT JOIN c_pospaymentmedium as cppm on cppm.c_pospaymentmedium_id = cl.c_pospaymentmedium_id
   LEFT JOIN c_pospaymentmedium as pppm on pppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
  ORDER BY ah.c_allocationhdr_id, al.c_allocationline_id;

ALTER TABLE c_allocation_detail_v OWNER TO libertya;