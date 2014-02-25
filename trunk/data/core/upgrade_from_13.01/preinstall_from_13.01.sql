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

--20130328-2335 Modificación a la vista de resumen de ventas diario
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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
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
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct
                           FROM c_invoice i
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
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal * dt.signo_issotrx)::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct
                   FROM c_invoice i
              JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
         JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
    JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   WHERE dt.doctypekey NOT IN ('RCI','RCR'))
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
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
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

-- 20130403-1029 Indice por performance
create index m_inoutline_c_orderline_id on m_inoutline (c_orderline_id);

-- 20130403-1029 Usar referencia de tipo tabledir en lugar de una vista, a fin de evitar datos erróneos en replicacion
update ad_column set ad_reference_id = 19, ad_reference_value_id = null, ad_val_rule_id = (select ad_val_rule_id from ad_val_rule where ad_componentobjectuid = 'CORE-AD_Val_Rule-167') where ad_componentobjectuid = 'CORE-AD_Column-3400';

-- 20130403-1033 Nuevas columnas para soporte LYWeb: lookup de direccion
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Country','ispostcodelookup', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Country','lookupclassname', 'character varying(255)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Country','lookupclientid', 'character varying(50)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Country','lookuppassword', 'character varying(50)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Country','lookupurl', 'character varying(100)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Country','allowcitiesoutoflist', 'character(1) DEFAULT ''Y''::bpchar'));
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Country','capturesequence', 'character varying(60)'));

-- 20130403-1033 Nuevos datos para soporte LYWeb: lookup de direccion
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AD';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AQ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@  @C@ @P@, @R@  @CO@' WHERE countrycode ='AR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ A-@P@ @C@ @CO@' WHERE countrycode ='AT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @R@ @P@ @CO@' WHERE countrycode ='AU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @P@ @CO@' WHERE countrycode ='AX';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='AZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BB';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BD';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ B-@P@ @C@ @CO@' WHERE countrycode ='BE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BJ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @P@ @CO@' WHERE countrycode ='BL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @R@ @C@, @P@ @CO@' WHERE countrycode ='BR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BV';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='BZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@, @R@ @P@ @CO@' WHERE countrycode ='CA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CD';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ CH-@P@ @C@ @CO@' WHERE countrycode ='CH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CN';
UPDATE C_Country SET capturesequence = '@CO@ @R!@ @C!@ @A1!@ @A2@ @A3@ @A4@ @P@' WHERE countrycode ='CO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CV';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CX';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='CZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ D-@P@ @R@ @C@ @CO@' WHERE countrycode ='DE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='DJ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='DK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='DM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='DO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='DZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='EC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='EE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='EG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='EH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ER';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ E-@P@ @C@ @R@ @CO@' WHERE countrycode ='ES';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ET';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='FI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='FJ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='FK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='FM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='FO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ F-@P@ @C@ @CO@' WHERE countrycode ='FR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GB';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GD';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @P@ @CO@' WHERE countrycode ='GG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GP';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GQ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='GY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='HK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='HM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='HN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='HR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='HT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='HU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ID';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @P@ @CO@' WHERE countrycode ='IM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IQ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='IT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @P@ @CO@' WHERE countrycode ='JE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='JM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='JO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @R@ @P@-@A@ @CO@' WHERE countrycode ='JP';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KP';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='KZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LB';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ L-@P@ @C@ @CO@' WHERE countrycode ='LU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LV';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='LY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MD';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@, @P@ @CO@' WHERE countrycode ='ME';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@ @P@ @CO@' WHERE countrycode ='MF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ML';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MP';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MQ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MV';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @R@ @P@ @CO@' WHERE countrycode ='MX';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='MZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @P@ @C@ @CO@' WHERE countrycode ='NL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NP';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='NZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='OM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='PY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='QA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='RE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='RO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@, @P@ @CO@' WHERE countrycode ='RS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @R@ @P@ @CO@' WHERE countrycode ='RU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='RW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SB';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SD';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @P@ @C@ @CO@' WHERE countrycode ='SI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SJ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ST';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SV';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='SZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TD';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TH';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TJ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TK';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TL';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TO';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TR';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TV';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TW';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='TZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='UA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='UG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='UM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@, @R@ @P@ @CO@' WHERE countrycode ='US';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='UY';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='UZ';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='VA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='VC';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='VE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='VG';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='VI';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='VN';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='VU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='WF';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='WS';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='YE';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='YT';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='YU';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ZA';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ZM';
UPDATE C_Country SET capturesequence = '@A1@ @A2@ @A3@ @A4@ @C@,  @P@ @CO@' WHERE countrycode ='ZW';

-- 20130410-1825: LYWeb: nuevas columnas
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Process_Para','readonlylogic', 'varchar(2000)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_UserMail','subject', 'varchar(255)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_UserMail','mailtext', 'varchar(2000)'));

-- 20130412-1930 Indice y constraint por performance en tabla e_electronicinvoice
create index e_electronicinvoice_key on e_electronicinvoice (e_electronicinvoice_id);
alter table e_electronicinvoice add constraint e_electronicinvoice_id_key PRIMARY KEY (e_electronicinvoice_id);

-- 20130412-1930 Indice y constraint por performance en tabla e_electronicinvoiceline
create index e_electronicinvoiceline_electronicinvoice on e_electronicinvoiceline (e_electronicinvoice_id);
alter table e_electronicinvoiceline add constraint e_electronicinvoiceline_key PRIMARY KEY (e_electronicinvoiceline_id);
alter table e_electronicinvoiceline add constraint eelectronicinvoice_eelectronicline FOREIGN KEY (e_electronicinvoice_id) REFERENCES e_electronicinvoice (e_electronicinvoice_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE;

--20130416-1030 Modificación de vista por mejoras en información de cuenta corriente mostrada
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
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND (i.initialcurrentaccountamt > 0) AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND (i.initialcurrentaccountamt > 0) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);

ALTER TABLE v_dailysales OWNER TO libertya;

--20130417-1254 Modificación de estado de documentos factura en estado Revertido a Anulado.
update c_invoice
set docstatus = 'VO'
where ad_client_id = 1010016 and docstatus = 'RE';

--20130424-1740 Modificación de vistas por nuevas modificaciones al reporte de declaracion de valores
DROP VIEW v_dailysales;
DROP VIEW c_pos_declaracionvalores_v;
DROP VIEW c_posjournalpayments_v;
DROP VIEW c_posjournalinvoices_v;

CREATE OR REPLACE VIEW c_posjournalinvoices_v AS 
 SELECT i.c_posjournal_id, ah.c_allocationhdr_id, ah.allocationtype, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.documentno, i.c_doctype_id, i.dateinvoiced, i.dateacct, i.c_bpartner_id, i.description, i.docstatus, i.processed, i.c_currency_id, i.grandtotal, sum(COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric(20,2)))::numeric(20,2) AS paidamt, dt.name, dt.docbasetype, dt.signo_issotrx, dt.isfiscaldocument, dt.isfiscal, i.fiscalalreadyprinted, ah.isactive as allocation_active, ah.created as allocation_created, ah.updated as allocation_updated, i.c_invoice_orig_id
   FROM c_invoice i
   INNER JOIN c_doctype as dt ON dt.c_doctype_id = i.c_doctypetarget_id
   LEFT JOIN c_allocationline al ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
  WHERE (ah.c_allocationhdr_id IS NULL OR (ah.allocationtype::text = ANY (ARRAY['STX'::character varying::text, 'MAN'::character varying::text, 'RC'::character varying::text]))) AND i.issotrx = 'Y'::bpchar
  GROUP BY i.documentno, i.c_posjournal_id, ah.c_allocationhdr_id, ah.allocationtype, i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.c_doctype_id, i.dateinvoiced, i.dateacct, i.c_bpartner_id, i.description, i.docstatus, i.processed, i.c_currency_id, i.grandtotal, dt.name, dt.docbasetype, dt.signo_issotrx, dt.isfiscaldocument, dt.isfiscal, i.fiscalalreadyprinted, ah.isactive, ah.created, ah.updated, i.c_invoice_orig_id
  ORDER BY i.documentno;

ALTER TABLE c_posjournalinvoices_v OWNER TO libertya;

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
                        END AS dateacct,
                        i.documentno as invoice_documentno,
                        i.grandtotal as invoice_grandtotal, 
                        ef.value as entidadfinanciera_value, 
                        ef.name as entidadfinanciera_name, 
                        bp.value as bp_entidadfinanciera_value, 
                        bp.name as bp_entidadfinanciera_name,
                        p.couponnumber as cupon,
                        p.creditcardnumber as creditcard,
                        dt.isfiscaldocument, 
                        dt.isfiscal, 
                        ic.fiscalalreadyprinted
                   FROM c_allocationline al
              LEFT JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
         LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
    LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera as ef on ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner as bp ON bp.c_bpartner_id = ef.c_bpartner_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
   LEFT JOIN c_doctype as dt ON dt.c_doctype_id = ic.c_doctypetarget_id
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
                 SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::unknown AS c_invoice_id, NULL::unknown AS c_payment_id, cl.c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CA'::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || '_#'::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::unknown AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc('day'::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, null as invoice_documentno, null as invoice_grandtotal, null as entidadfinanciera_value, null as entidadfinanciera_name, null as bp_entidadfinanciera_value, null as bp_entidadfinanciera_name, null as cupon, null as creditcard, null as isfiscaldocument, null as isfiscal, null as fiscalalreadyprinted
                   FROM c_cashline cl
              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
             WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
                      FROM c_allocationline al
                     WHERE al.c_cashline_id = cl.c_cashline_id)))
UNION ALL 
        ( SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::unknown AS c_invoice_id, p.c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, p.payamt AS amount, NULL::unknown AS c_cash_id, NULL::numeric(18,0) AS line, NULL::unknown AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, null as invoice_documentno, null as invoice_grandtotal, ef.value as entidadfinanciera_value, ef.name as entidadfinanciera_name, bp.value as bp_entidadfinanciera_value, bp.name as bp_entidadfinanciera_name, p.couponnumber as cupon, p.creditcardnumber as creditcard, null as isfiscaldocument, null as isfiscal, null as fiscalalreadyprinted
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
      LEFT JOIN m_entidadfinanciera as ef on ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
      LEFT JOIN c_bpartner as bp ON bp.c_bpartner_id = ef.c_bpartner_id
     WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
              FROM c_allocationline al
             WHERE al.c_payment_id = p.c_payment_id))
     ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

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
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND (i.initialcurrentaccountamt > 0) AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND (i.initialcurrentaccountamt > 0) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);

ALTER TABLE v_dailysales OWNER TO libertya;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN i.total - i.open
                                    WHEN (-1) THEN 0::numeric
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE dt.signo_issotrx
                                    WHEN 1 THEN 0::numeric
                                    WHEN (-1) THEN abs(i.total - i.open)
                                    ELSE NULL::numeric
                                END::numeric(22,2) AS egreso,
                                i.documentno as invoice_documentno,
                                total as invoice_grandtotal,
                                null as entidadfinanciera_value, 
				null as entidadfinanciera_name, 
				null as bp_entidadfinanciera_value, 
				null as bp_entidadfinanciera_name,
				null as cupon,
				null as creditcard,
				null as generated_invoice_documentno
                           FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open
                                   FROM c_posjournalinvoices_v i
                              JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                         JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                   ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2)) i
                      JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                UNION ALL 
                         SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                CASE p.isreceipt
                                    WHEN 'Y'::bpchar THEN p.total
                                    ELSE 0::numeric
                                END::numeric(22,2) AS ingreso, 
                                CASE p.isreceipt
                                    WHEN 'N'::bpchar THEN abs(p.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso,
                                invoice_documentno,
                                invoice_grandtotal,
                                entidadfinanciera_value, 
                                entidadfinanciera_name, 
                                bp_entidadfinanciera_value, 
                                bp_entidadfinanciera_name,
                                cupon,
				creditcard,
				null as generated_invoice_documentno
                           FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                                   FROM c_payment p
                              JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                         JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                    LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                   GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) p)
        UNION ALL 
                 SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                        CASE
                            WHEN length(c.description::text) > 0 THEN c.description
                            ELSE c.info
                        END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                        CASE sign(c.total)
                            WHEN (-1) THEN 0::numeric
                            ELSE c.total
                        END::numeric(22,2) AS ingreso, 
                        CASE sign(c.total)
                            WHEN (-1) THEN abs(c.total)
                            ELSE 0::numeric
                        END::numeric(22,2) AS egreso,
                        invoice_documentno,
                        invoice_grandtotal,
			entidadfinanciera_value, 
			entidadfinanciera_name, 
			bp_entidadfinanciera_value, 
			bp_entidadfinanciera_name,
			cupon, 
			creditcard,
			null as generated_invoice_documentno
                   FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                           FROM c_cashline cl
                      JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                 JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
      GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) c)
UNION ALL 
         SELECT i.ad_client_id, 
		i.ad_org_id, 
		i.c_posjournal_id, 
		i.ad_user_id, 
		i.c_currency_id, 
		i.dateinvoiced AS datetrx, 
		i.docstatus, 
		NULL::unknown AS category, 
		i.tendertype, 
		(i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, 
		i.c_charge_id, 
		i.chargename, 
		i.c_invoice_id AS doc_id, 
		i.total AS ingreso, 
		0 AS egreso, 
		invoice_documentno, 
		invoice_grandtotal, 
		entidadfinanciera_value, 
		entidadfinanciera_name, 
		bp_entidadfinanciera_value, 
		bp_entidadfinanciera_name, 
		cupon, 
		creditcard, 
		null as generated_invoice_documentno
           FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                   FROM c_invoice i
              JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
         JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
    JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
  GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, invoice_documentno, invoice_grandtotal, entidadfinanciera_value, entidadfinanciera_name, bp_entidadfinanciera_value, bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) i
      JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
     WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])
UNION ALL
select ji.ad_client_id, 
	ji.ad_org_id, 
	ji.c_posjournal_id, 
	pj.ad_user_id, 
	ji.c_currency_id, 
	ji.dateinvoiced AS datetrx, 
	ji.docstatus, 
	NULL::unknown AS category, 
	ji.docbasetype AS tendertype, 
	(ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, 
	null as c_charge_id, 
	null as chargename, 
	ji.c_invoice_id AS doc_id, 
	ji.signo_issotrx * ji.grandtotal as ingreso, 
	0::numeric(22,2) AS egreso, 
	ji.documentno as invoice_documentno, 
	ji.grandtotal as invoice_grandtotal, 
	null as entidadfinanciera_value, 
	null as entidadfinanciera_name, 
	null as bp_entidadfinanciera_value, 
	null as bp_entidadfinanciera_name,
	null as cupon,
	null as creditcard, 
	ic.documentno as generated_invoice_documentno
from c_posjournalinvoices_v as ji
inner join c_posjournal as pj on ji.c_posjournal_id = pj.c_posjournal_id
inner join c_allocationline as al on al.c_allocationhdr_id = ji.c_allocationhdr_id
inner join c_invoice as ic on al.c_invoice_credit_id = ic.c_invoice_id
where ji.docstatus IN ('VO','RE') and ji.allocation_active = 'Y' and (ji.isfiscal is null OR ji.isfiscal = 'N' OR (ji.isfiscal = 'Y' AND ji.fiscalalreadyprinted = 'Y'));

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--20130424-1945 Modificación de vista para tener en cuenta comprobantes anulados
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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone)) c
                        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND (i.initialcurrentaccountamt > 0) AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND (i.initialcurrentaccountamt > 0) AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);

ALTER TABLE v_dailysales OWNER TO libertya;

--20130507-1145 Creación de vista para Informe Resumen de Pedidos Pendientes de Facturación
-- View: RV_Order_pending
-- DROP VIEW RV_Order_pending;
CREATE OR REPLACE VIEW RV_Order_pending AS 
SELECT o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.dateordered::date AS dateordered, o.datepromised::date AS datepromised, o.c_bpartner_id, o.issotrx, currencyconvert(o.grandtotal, o.c_currency_id, 118, o.dateacct::timestamp with time zone, NULL::integer, o.ad_client_id, o.ad_org_id) AS totalOrdered, (CASE WHEN (ol.qtyordered > ol.qtyinvoiced AND ol.qtyinvoiced <> 0) THEN 'Y' ELSE 'N' END)::character(1) as IsParcial, currencyconvert(SUM(((ol.qtyordered - ol.qtyinvoiced) * (CASE WHEN pl.istaxincluded = 'Y' THEN ol.linenetamt ELSE ol.linetotalamt END) / (CASE WHEN ol.qtyordered = 0 THEN 1 ELSE ol.qtyordered END))), o.c_currency_id, 118, o.dateacct::timestamp with time zone, NULL::integer, o.ad_client_id, o.ad_org_id) AS totalPending
from c_orderline ol
JOIN c_order o ON o.c_order_id = ol.c_order_id AND (ol.qtyordered <> ol.qtyinvoiced) AND (o.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND ol.m_product_id IS NOT NULL
INNER JOIN M_PriceList pl ON (pl.M_PriceList_ID = o.M_PriceList_ID)
WHERE ol.ad_client_id IN (1010016,1010056,1010057)
GROUP BY o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.dateordered, o.datepromised, o.c_bpartner_id, o.issotrx, o.grandtotal, ol.qtyordered, ol.qtyinvoiced, o.c_currency_id, o.dateacct
ORDER BY o.dateordered desc;

ALTER TABLE RV_Order_pending OWNER TO libertya;
GRANT ALL ON TABLE RV_Order_pending TO libertya;

--20130509-1031 Modificaciones a vistas por fixes a reportes de resumen de ventas y declaración de valores
CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.total - i.open
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.total - i.open)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                           ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2)) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                                           FROM c_payment p
                                      JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                           GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) p)
                UNION ALL 
                         SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.total)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.total)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
               LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
              GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) c)
        UNION ALL 
                 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                           FROM c_invoice i
                      JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
      GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND ji.allocation_active = 'Y'::bpchar AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, coalesce(pjh.c_posjournal_id,pj.c_posjournal_id) as c_posjournal_id, coalesce(pjh.ad_user_id,pj.ad_user_id) as ad_user_id, coalesce(pjh.c_pos_id, pj.c_pos_id) as c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone)) c
                        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, coalesce(pjh.c_posjournal_id,pj.c_posjournal_id) as c_posjournal_id, coalesce(pjh.ad_user_id,pj.ad_user_id) as ad_user_id, coalesce(pjh.c_pos_id, pj.c_pos_id) as c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);

ALTER TABLE v_dailysales OWNER TO libertya;

-- 20130509-1232 - Nueva columna para especificar el tipo de documento por anulacion 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','C_ReverseDocType_ID', 'int'));

--20130509 - 1730 Creación de vista para Informe Resumen de Pedidos Pendientes de Facturación
-- View: RV_Order_pending
-- DROP VIEW RV_Order_pending;
CREATE OR REPLACE VIEW RV_Order_pending AS 
SELECT o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.dateordered::date AS dateordered, o.datepromised::date AS datepromised, o.c_bpartner_id, o.issotrx, currencyconvert(o.grandtotal, o.c_currency_id, 118, o.dateacct::timestamp with time zone, NULL::integer, o.ad_client_id, o.ad_org_id) AS totalOrdered, (CASE WHEN (ol.qtyordered > ol.qtyinvoiced AND ol.qtyinvoiced <> 0) THEN 'Y' ELSE 'N' END)::character(1) as IsParcial, currencyconvert(SUM(((ol.qtyordered - ol.qtyinvoiced) * (CASE WHEN pl.istaxincluded = 'Y' THEN ol.linenetamt ELSE ol.linetotalamt END) / (CASE WHEN ol.qtyordered = 0 THEN 1 ELSE ol.qtyordered END))), o.c_currency_id, 118, o.dateacct::timestamp with time zone, NULL::integer, o.ad_client_id, o.ad_org_id) AS totalPending
from c_orderline ol
JOIN c_order o ON o.c_order_id = ol.c_order_id AND (ol.qtyordered <> ol.qtyinvoiced) AND (o.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND ol.m_product_id IS NOT NULL
INNER JOIN M_PriceList pl ON (pl.M_PriceList_ID = o.M_PriceList_ID)
GROUP BY o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.dateordered, o.datepromised, o.c_bpartner_id, o.issotrx, o.grandtotal, ol.qtyordered, ol.qtyinvoiced, o.c_currency_id, o.dateacct;

ALTER TABLE RV_Order_pending OWNER TO libertya;
GRANT ALL ON TABLE RV_Order_pending TO libertya;

--20130509 - 1730 Creación de vista para Informe Detalle de Pedidos Pendientes de Facturación
-- View: RV_Order_pending_detail
-- DROP VIEW RV_Order_pending_detail;
CREATE OR REPLACE VIEW RV_Order_pending_detail AS 
SELECT o.ad_client_id, o.ad_org_id, o.isactive, o.created, o.createdby, o.updated, o.updatedby, o.c_order_id, o.documentno, o.dateordered::date AS dateordered, o.datepromised::date AS datepromised, o.c_bpartner_id, o.issotrx, ol.m_product_id, ol.qtyordered, ol.qtyinvoiced, currencyconvert(((ol.qtyordered - ol.qtyinvoiced) * (CASE WHEN pl.istaxincluded = 'Y' THEN ol.linenetamt ELSE ol.linetotalamt END) / (CASE WHEN ol.qtyordered = 0 THEN 1 ELSE ol.qtyordered END)), o.c_currency_id, 118, o.dateacct::timestamp with time zone, NULL::integer, o.ad_client_id, o.ad_org_id) AS totalPending
from c_orderline ol
JOIN c_order o ON o.c_order_id = ol.c_order_id AND (ol.qtyordered <> ol.qtyinvoiced) AND (o.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND ol.m_product_id IS NOT NULL
INNER JOIN M_PriceList pl ON (pl.M_PriceList_ID = o.M_PriceList_ID);

ALTER TABLE RV_Order_pending_detail OWNER TO libertya;
GRANT ALL ON TABLE RV_Order_pending_detail TO libertya;


--20130510 - 1700 Modificación de vista de movimientos por artículo para soporte de nuevo reporte Movimientos Valorizados y fixes
DROP VIEW v_product_movements;

CREATE OR REPLACE VIEW v_product_movements AS 
 SELECT m.tablename, m.ad_client_id, m.ad_org_id, o.value AS orgvalue, o.name AS orgname, m.doc_id, m.documentno, m.docstatus, m.description, m.datetrx, m.m_product_id, m.qty, m.type, m.aditionaltype, m.c_charge_id, m.chargename, p.name AS productname, p.value AS productvalue, w.m_warehouse_id, w.value as warehousevalue, w.name as warehousename, wt.m_warehouse_id as m_warehouseto_id, wt.value as warehousetovalue, wt.name as warehousetoname, bp.c_bpartner_id, bp.value as bpartnervalue, bp.name as bpartnername
   FROM (        (        (        (        (        (        (         SELECT 'M_Transfer' AS tablename, t.ad_client_id, t.ad_org_id, t.m_transfer_id as doc_id, t.documentno, t.docstatus, t.description, t.datetrx, tl.m_product_id, tl.qty, t.transfertype AS type, t.movementtype AS aditionaltype, c.c_charge_id, c.name AS chargename, t.m_warehouse_id, t.m_warehouseto_id, t.c_bpartner_id
                                                                   FROM m_transfer t                                                                   
                                                              JOIN m_transferline tl ON tl.m_transfer_id = t.m_transfer_id
                                                         LEFT JOIN c_charge c ON c.c_charge_id = t.c_charge_id
                                                        UNION ALL 
                                                                 SELECT 'M_ProductChange' AS tablename, pc.ad_client_id, pc.ad_org_id, pc.m_productchange_id as doc_id, pc.documentno, pc.docstatus, pc.description, pc.datetrx, pc.m_product_id, pc.productqty AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, pc.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                                                                   FROM m_productchange pc)
                                                UNION ALL 
                                                         SELECT 'M_ProductChange' AS tablename, pc.ad_client_id, pc.ad_org_id, pc.m_productchange_id as doc_id, pc.documentno, pc.docstatus, pc.description, pc.datetrx, pc.m_product_to_id AS m_product_id, pc.productqty * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, pc.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                                                           FROM m_productchange pc)
                                        UNION ALL 
                                                 SELECT 'M_InOut' AS tablename, io.ad_client_id, io.ad_org_id, io.m_inout_id as doc_id, io.documentno, io.docstatus, io.description, io.movementdate AS datetrx, iol.m_product_id, iol.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, io.m_warehouse_id, null as m_warehouseto_id, io.c_bpartner_id
                                                   FROM m_inout io
                                              JOIN m_inoutline iol ON iol.m_inout_id = io.m_inout_id
                                         JOIN c_doctype dt ON dt.c_doctype_id = io.c_doctype_id)
                                UNION ALL 
                                         SELECT 'M_Splitting' AS tablename, s.ad_client_id, s.ad_org_id, s.m_splitting_id as doc_id, s.documentno, s.docstatus, s.comments AS description, s.datetrx, s.m_product_id, s.productqty * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, s.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                                           FROM m_splitting s)
                        UNION ALL 
                                 SELECT 'M_Splitting' AS tablename, s.ad_client_id, s.ad_org_id, s.m_splitting_id as doc_id, s.documentno, s.docstatus, s.comments AS description, s.datetrx, sl.m_product_to_id AS m_product_id, sl.productqty AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, s.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                                   FROM m_splitting s
                              JOIN m_splittingline sl ON sl.m_splitting_id = s.m_splitting_id)
                UNION ALL 
                         SELECT 'M_Inventory' AS tablename, i.ad_client_id, i.ad_org_id, i.m_inventory_id as doc_id, i.documentno, i.docstatus, i.description, i.movementdate AS datetrx, il.m_product_id, il.qtycount AS qty, dt.name AS type, i.inventorykind AS aditionaltype, c.c_charge_id, c.name AS chargename, i.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                           FROM m_inventory i
                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                 JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
            LEFT JOIN c_charge c ON c.c_charge_id = i.c_charge_id
           WHERE NOT (EXISTS ( SELECT m_transfer.m_inventory_id
				FROM m_transfer
				WHERE m_transfer.m_inventory_id = i.m_inventory_id)) 
		AND NOT (EXISTS ( SELECT m_productchange.m_inventory_id
				FROM m_productchange
				WHERE m_productchange.m_inventory_id = i.m_inventory_id OR m_productchange.void_inventory_id = i.m_inventory_id)) 
		AND NOT (EXISTS ( SELECT s.m_inventory_id
				FROM m_splitting s
				WHERE s.m_inventory_id = i.m_inventory_id OR s.void_inventory_id = i.m_inventory_id)))
        UNION ALL 
                 SELECT 'M_Movement' AS tablename, m.ad_client_id, m.ad_org_id, m.m_movement_id as doc_id, m.documentno, m.docstatus, m.description, m.movementdate AS datetrx, ml.m_product_id, ml.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, w.m_warehouse_id, wt.m_warehouse_id as m_warehouseto_id, null as c_bpartner_id
                   FROM m_movement m
              JOIN m_movementline ml ON ml.m_movement_id = m.m_movement_id
              INNER JOIN m_locator as l ON l.m_locator_id = ml.m_locator_id
              INNER JOIN m_warehouse as w on w.m_warehouse_id = l.m_warehouse_id
              INNER JOIN m_locator as lt ON lt.m_locator_id = ml.m_locatorto_id
              INNER JOIN m_warehouse as wt on wt.m_warehouse_id = lt.m_warehouse_id
         JOIN c_doctype dt ON dt.c_doctype_id = m.c_doctype_id) m
   JOIN m_product p ON p.m_product_id = m.m_product_id
   JOIN ad_org o ON o.ad_org_id = m.ad_org_id
   INNER JOIN m_warehouse as w ON w.m_warehouse_id = m.m_warehouse_id
   LEFT JOIN m_warehouse as wt ON wt.m_warehouse_id = m.m_warehouseto_id
   LEFT JOIN c_bpartner as bp ON bp.c_bpartner_id = m.c_bpartner_id;

ALTER TABLE v_product_movements OWNER TO libertya;

--20130513 - 1215 Fixes a vista
DROP VIEW v_product_movements;

CREATE OR REPLACE VIEW v_product_movements AS 
 SELECT m.tablename, m.ad_client_id, m.ad_org_id, o.value AS orgvalue, o.name AS orgname, m.doc_id, m.documentno, m.docstatus, m.description, m.datetrx, m.m_product_id, m.qty, m.type, m.aditionaltype, m.c_charge_id, c.name as chargename, p.name AS productname, p.value AS productvalue, w.m_warehouse_id, w.value as warehousevalue, w.name as warehousename, wt.m_warehouse_id as m_warehouseto_id, wt.value as warehousetovalue, wt.name as warehousetoname, bp.c_bpartner_id, bp.value as bpartnervalue, bp.name as bpartnername
   FROM (        (        (        (        (         SELECT 'M_Transfer' AS tablename, t.ad_client_id, t.ad_org_id, t.m_transfer_id as doc_id, t.documentno, t.docstatus, t.description, t.datetrx, tl.m_product_id, tl.qty, t.transfertype AS type, t.movementtype AS aditionaltype, t.c_charge_id, t.m_warehouse_id, t.m_warehouseto_id, t.c_bpartner_id
                                                                   FROM m_transfer t                                                                   
                                                              JOIN m_transferline tl ON tl.m_transfer_id = t.m_transfer_id
                                                        UNION ALL 
                                                                 SELECT 'M_ProductChange' AS tablename, pc.ad_client_id, pc.ad_org_id, pc.m_productchange_id as doc_id, pc.documentno, pc.docstatus, pc.description, pc.datetrx, il.m_product_id, (il.qtyinternaluse * -1) AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, il.c_charge_id, pc.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                                                                   FROM m_productchange pc
                                                                   INNER JOIN m_inventoryline as il on il.m_inventory_id = pc.m_inventory_id)
                                        UNION ALL 
                                                 SELECT 'M_InOut' AS tablename, io.ad_client_id, io.ad_org_id, io.m_inout_id as doc_id, io.documentno, io.docstatus, io.description, io.movementdate AS datetrx, iol.m_product_id, iol.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, io.m_warehouse_id, null as m_warehouseto_id, io.c_bpartner_id
                                                   FROM m_inout io
                                              JOIN m_inoutline iol ON iol.m_inout_id = io.m_inout_id
                                         JOIN c_doctype dt ON dt.c_doctype_id = io.c_doctype_id)
                                UNION ALL 
                                         SELECT 'M_Splitting' AS tablename, s.ad_client_id, s.ad_org_id, s.m_splitting_id as doc_id, s.documentno, s.docstatus, s.comments AS description, s.datetrx, il.m_product_id, (il.qtyinternaluse * -1) AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, il.c_charge_id, s.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                                           FROM m_splitting s
                                           INNER JOIN m_inventoryline as il on il.m_inventory_id = s.m_inventory_id)
                UNION ALL 
                         SELECT 'M_Inventory' AS tablename, i.ad_client_id, i.ad_org_id, i.m_inventory_id as doc_id, i.documentno, i.docstatus, i.description, i.movementdate AS datetrx, il.m_product_id, il.qtycount AS qty, dt.name AS type, i.inventorykind AS aditionaltype, i.c_charge_id, i.m_warehouse_id, null as m_warehouseto_id, null as c_bpartner_id
                           FROM m_inventory i
                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                 JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
           WHERE NOT (EXISTS ( SELECT m_transfer.m_inventory_id
				FROM m_transfer
				WHERE m_transfer.m_inventory_id = i.m_inventory_id)) 
		AND NOT (EXISTS ( SELECT m_productchange.m_inventory_id
				FROM m_productchange
				WHERE m_productchange.m_inventory_id = i.m_inventory_id OR m_productchange.void_inventory_id = i.m_inventory_id)) 
		AND NOT (EXISTS ( SELECT s.m_inventory_id
				FROM m_splitting s
				WHERE s.m_inventory_id = i.m_inventory_id OR s.void_inventory_id = i.m_inventory_id)))
        UNION ALL 
                 SELECT 'M_Movement' AS tablename, m.ad_client_id, m.ad_org_id, m.m_movement_id as doc_id, m.documentno, m.docstatus, m.description, m.movementdate AS datetrx, ml.m_product_id, ml.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, w.m_warehouse_id, wt.m_warehouse_id as m_warehouseto_id, null as c_bpartner_id
                   FROM m_movement m
              JOIN m_movementline ml ON ml.m_movement_id = m.m_movement_id
              INNER JOIN m_locator as l ON l.m_locator_id = ml.m_locator_id
              INNER JOIN m_warehouse as w on w.m_warehouse_id = l.m_warehouse_id
              INNER JOIN m_locator as lt ON lt.m_locator_id = ml.m_locatorto_id
              INNER JOIN m_warehouse as wt on wt.m_warehouse_id = lt.m_warehouse_id
         JOIN c_doctype dt ON dt.c_doctype_id = m.c_doctype_id) m
   JOIN m_product p ON p.m_product_id = m.m_product_id
   JOIN ad_org o ON o.ad_org_id = m.ad_org_id
   INNER JOIN m_warehouse as w ON w.m_warehouse_id = m.m_warehouse_id
   LEFT JOIN m_warehouse as wt ON wt.m_warehouse_id = m.m_warehouseto_id
   LEFT JOIN c_bpartner as bp ON bp.c_bpartner_id = m.c_bpartner_id
   LEFT JOIN c_charge as c ON c.c_charge_id = m.c_charge_id;

ALTER TABLE v_product_movements OWNER TO libertya;

--20130514-1640 Fixes a vista por signos en cashlines
DROP VIEW c_pos_declaracionvalores_v;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.total - i.open
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.total - i.open)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                           ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2)) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                                           FROM c_payment p
                                      JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                           GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) p)
                UNION ALL 
                         SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
               LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
              GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount) c)
        UNION ALL 
                 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard
                           FROM c_invoice i
                      JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
      GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND ji.allocation_active = 'Y'::bpchar AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--20130520-1230 Nueva columna para monto límite de control de cuit de cheques por perfil
ALTER TABLE ad_role ADD COLUMN controlcuitlimit numeric(9,2) NOT NULL DEFAULT 0;

--20130520-1428 Nueva columna para especificar si el smtp es ssl
ALTER TABLE ad_client ADD COLUMN usessl CHARACTER(1) default 'N';

--20130521-0000 Nueva columna para agregar una descripción de documento que se imprimirá en el ticket fiscal
ALTER TABLE c_invoice ADD COLUMN fiscaldescription character varying(255);

--20130521-1136 Nuevo indice para mejorar el tiempo de respuesta en los InfoBPartner
CREATE INDEX ad_user_bpartner ON ad_user USING btree (c_bpartner_id);

--20130522-1635 Modificación de vista de reporte Resumen de Ventas para tomar los cobros de facturas en cuenta corriente con allocations positivos, al igual que el invoiceopen
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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND hdr.isactive = 'Y') c
                        GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL);

ALTER TABLE v_dailysales OWNER TO libertya;

--20130523-1220 Cambiados a null los campos del dashboard
UPDATE PA_DASHBOARDCONTENT SET HTML = null;

-- 20130524-1130 Incorporación de funcion getLabelDueDate utilizada en reportes
CREATE OR REPLACE FUNCTION getLabelDueDate(fecha_corte date, fecha_vencimiento date)
  RETURNS character varying AS
$BODY$
DECLARE
	days		INTEGER := fecha_vencimiento - fecha_corte;
BEGIN
	IF (days <= 0) THEN
		RETURN 'A Vencer';
	ELSIF ((days > 0) AND (days <= 30)) THEN
		RETURN 'D. Vencida 30';
	ELSIF ((days > 30) AND (days <= 60)) THEN
		RETURN 'D. Vencida 60';
	ELSIF ((days > 60) AND (days <= 90)) THEN
		RETURN 'D. Vencida 90';
	ELSIF ((days > 90) AND (days <= 120)) THEN
		RETURN 'D. Vencida 120';
	ELSIF ((days > 120) AND (days <= 150)) THEN
		RETURN 'D. Vencida 150';
	ELSE
		RETURN 'D. Vencida 150+'; 
	END IF;
END; $BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getLabelDueDate(date, date) OWNER TO libertya;

-- 20130524-1130 Incorporación de columna DateEmissionCheck a Pagos
ALTER TABLE C_Payment ADD COLUMN DateEmissionCheck timestamp without time zone NULL;

--20130528-1440 Mejoras y fixes a la vista del informe de declaración de valores
DROP VIEW c_pos_declaracionvalores_v;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.total - i.open
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.total - i.open)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.c_invoice_id, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno, allocation_active
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS open, allocation_active
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                           ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), currencybase(inv.initialcurrentaccountamt, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2)) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, allocation_active
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive as allocation_active, pjp.c_invoice_id
                                           FROM c_payment p
                                      JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                           GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id) p)
                UNION ALL 
                         SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.c_invoice_id, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno, allocation_active
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive as allocation_active, pjp.c_invoice_id
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
               LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
              GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id) c)
        UNION ALL 
                 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, allocation_active
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive as allocation_active, pjp.c_invoice_id as invoice_id
                           FROM c_invoice i
                      JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
      GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, allocation_active
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

-- 20130529-1421-Correccion en OP/RC para multimoneda.  La preferencia DIF_CAMBIO_PTO_VENTA tiene que ser independiente por client
update ad_preference set ad_client_id = 1010016 where ad_componentobjectuid = 'CORE-AD_Preference-1011156';

--20130530-1430 Nuevo campo para registrar el nro de lote del cupon 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Payment','couponbatchnumber', 'character varying(30)'));

--20130531-0045 Funcion para retornar el nro. de remito asociado al pedido recibido por parametro.
CREATE OR REPLACE FUNCTION getinoutsdocumentsnofromorder(p_c_order_id integer)
  RETURNS character varying AS
$BODY$
DECLARE
	v_nro_Remito	  CHARACTER VARYING(30) := null;
   	v_IsSoTrx         CHARACTER(1);
BEGIN
	BEGIN
	SELECT	IsSoTrx
	  INTO	STRICT 
			v_IsSoTrx
	FROM	C_Order
	WHERE	C_Order_ID = p_c_order_id;
		EXCEPTION	--	No encontrado; posiblememte mal llamado
		WHEN OTHERS THEN
            	RAISE NOTICE 'OrderAvailable - %', SQLERRM;
			RETURN NULL;
	END;

	IF (v_IsSoTrx = 'N') THEN 
		BEGIN
		-- Si es issotrx = 'N' se debe verificar en MMatchPO, como último el remito puede estar relacionado al pedido mismo 
		SELECT	distinct io.documentno
		  INTO
				v_nro_Remito
		FROM       m_matchpo as mpo
		INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = mpo.m_inoutline_id)
		INNER JOIN c_orderline as ol ON (ol.c_orderline_id = mpo.c_orderline_id)
		INNER JOIN m_inout as io ON (io.m_inout_id = iol.m_inout_id)
		WHERE	ol.c_order_id = p_c_order_id AND io.docstatus IN ('CO','CL')
		ORDER BY io.documentno DESC
		LIMIT 1;
			EXCEPTION	--	No encontrado; posiblememte mal llamado
			WHEN OTHERS THEN
			RAISE NOTICE 'OrderAvailable - %', SQLERRM;
				RETURN NULL;
		END;
	END IF;
	
	IF (v_nro_Remito IS null) THEN 
		BEGIN
		-- Obtener el remito directamente desde el pedido mismo, para los casos	también de issotrx = 'Y'
		SELECT	distinct documentno
		  INTO
				v_nro_Remito
		FROM       m_inout
		WHERE	c_order_id = p_c_order_id AND docstatus IN ('CO','CL')
		ORDER BY documentno DESC
		LIMIT 1;
			EXCEPTION	--	No encontrado; posiblememte mal llamado
			WHEN OTHERS THEN
			RAISE NOTICE 'OrderAvailable - %', SQLERRM;
				RETURN NULL;
		END;
	END IF;

	RETURN	v_nro_Remito;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getinoutsdocumentsnofromorder(integer) OWNER TO libertya;

--20130531-0048 Funcion para retornar el nro. de remito asociado a la factura recibida por parametro.
CREATE OR REPLACE FUNCTION getinoutsdocumentsnofrominvoice(p_c_invoice_id integer)
  RETURNS character varying AS
$BODY$
DECLARE
	v_nro_Remito	  CHARACTER VARYING(30) := null;
   	v_IsSoTrx         CHARACTER(1);
	v_order_id	  INTEGER := 0;
BEGIN
	BEGIN
	SELECT	IsSoTrx, C_Order_ID
	  INTO	STRICT 
			v_IsSoTrx, v_order_id
	FROM	C_Invoice
	WHERE	C_Invoice_ID = p_c_invoice_id;
		EXCEPTION	--	No encontrado; posiblememte mal llamado
		WHEN OTHERS THEN
            	RAISE NOTICE 'InvoiceAvailable - %', SQLERRM;
			RETURN NULL;
	END;

	IF (v_IsSoTrx = 'N') THEN 
		BEGIN
		-- Si es issotrx = 'N' se debe verificar en MMatchPO, como último el remito puede estar relacionado al pedido mismo 
		SELECT	distinct io.documentno
		  INTO
				v_nro_Remito
		FROM       m_matchpo as mpo
		INNER JOIN m_inoutline as iol ON (iol.m_inoutline_id = mpo.m_inoutline_id)
		INNER JOIN c_invoiceline as il ON (il.c_invoiceline_id = minv.c_invoiceline_id)
		INNER JOIN m_inout as io ON (io.m_inout_id = iol.m_inout_id)
		WHERE	il.c_invoice_id = p_c_invoice_id AND io.docstatus IN ('CO','CL')
		ORDER BY io.documentno DESC
		LIMIT 1;
			EXCEPTION	--	No encontrado; posiblememte mal llamado
			WHEN OTHERS THEN
			RAISE NOTICE 'InvoiceAvailable - %', SQLERRM;
				RETURN NULL;
		END;
	END IF;
	
	IF (v_nro_Remito IS NULL) THEN 
		BEGIN
		-- Obtener el remito directamente desde el pedido mismo, para los casos	también de issotrx = 'Y'
		SELECT	distinct documentno
		  INTO
				v_nro_Remito
		FROM       m_inout
		WHERE	c_invoice_id = p_c_invoice_id AND docstatus IN ('CO','CL')
		ORDER BY documentno DESC
		LIMIT 1;
			EXCEPTION	--	No encontrado; posiblememte mal llamado
			WHEN OTHERS THEN
			RAISE NOTICE 'InvoiceAvailable - %', SQLERRM;
				RETURN NULL;
		END;
	END IF;

	IF (v_nro_Remito IS NULL) THEN
		RETURN getInOutsDocumentsNoFromOrder(v_order_id);
	END IF;
	RETURN	v_nro_Remito;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getinoutsdocumentsnofrominvoice(integer) OWNER TO libertya;

--20130531-0225 Modificación de Vista rv_c_invoice que requirio el borrado de vistas dependientes.
DROP VIEW rv_openitem;
DROP VIEW rv_c_invoice_week;
DROP VIEW rv_c_invoice_vendormonth;
DROP VIEW rv_c_invoice_prodweek;
DROP VIEW rv_c_invoice_prodmonth;
DROP VIEW rv_c_invoice_month;
DROP VIEW rv_c_invoice_day;
DROP VIEW rv_c_invoice_customervendqtr;
DROP VIEW rv_c_invoice_customerprodqtr;
DROP VIEW rv_c_invoiceline;
DROP VIEW rv_c_invoice;

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
            WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN (-1)
            ELSE 1
        END AS multiplier,
        getinoutsdocumentsnofrominvoice(i.c_invoice_id)::CHARACTER VARYING(30) AS documentNo_InOut
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctype_id = d.c_doctype_id
   JOIN c_bpartner b ON i.c_bpartner_id = b.c_bpartner_id
   JOIN c_bpartner_location bpl ON i.c_bpartner_location_id = bpl.c_bpartner_location_id
   JOIN c_location loc ON bpl.c_location_id = loc.c_location_id
  WHERE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]);

ALTER TABLE rv_c_invoice OWNER TO libertya;
GRANT ALL ON TABLE rv_c_invoice TO libertya;

CREATE OR REPLACE VIEW rv_c_invoiceline AS 
 SELECT i.c_order_id, i.c_currency_id, il.ad_client_id, il.ad_org_id, il.c_invoiceline_id, i.c_invoice_id, i.salesrep_id, i.c_bpartner_id, i.c_bp_group_id, il.m_product_id, p.m_product_category_id, i.dateinvoiced, i.dateacct, i.issotrx, i.c_doctype_id, i.docstatus, il.qtyinvoiced * i.multiplier::numeric AS qtyinvoiced, il.qtyentered * i.multiplier::numeric AS qtyentered, il.m_attributesetinstance_id, productattribute(il.m_attributesetinstance_id) AS productattribute, pasi.m_attributeset_id, pasi.m_lot_id, pasi.guaranteedate, pasi.lot, pasi.serno, il.pricelist, il.priceactual, il.pricelimit, il.priceentered, 
        CASE
            WHEN il.pricelist = 0::numeric THEN 0::numeric
            ELSE round((il.pricelist - il.priceactual) / il.pricelist * 100::numeric, 2)
        END AS discount, 
        CASE
            WHEN costprice.pricestd = 0::numeric THEN 0::numeric
            ELSE round((il.priceactual - costprice.pricestd) / costprice.pricestd * 100::numeric, 2)
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
   LEFT JOIN ( SELECT pp.m_product_id, pp.pricestd
   FROM m_productprice pp
   JOIN m_pricelist_version plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id
   JOIN m_pricelist pl ON plv.m_pricelist_id = pl.m_pricelist_id AND pl.issopricelist = 'N'::bpchar AND pl.isdefault = 'Y'::bpchar) costprice ON p.m_product_id = costprice.m_product_id;

ALTER TABLE rv_c_invoiceline OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_customerprodqtr AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced, il.issotrx
   FROM rv_c_invoiceline il
  GROUP BY il.ad_client_id, il.ad_org_id, il.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying), il.issotrx, il.c_currency_id;

ALTER TABLE rv_c_invoice_customerprodqtr OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_customervendqtr AS 
 SELECT il.ad_client_id, il.ad_org_id, il.c_bpartner_id, po.c_bpartner_id AS vendor_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced
   FROM rv_c_invoiceline il
   JOIN m_product_po po ON il.m_product_id = po.m_product_id
  WHERE il.issotrx = 'Y'::bpchar
  GROUP BY il.ad_client_id, il.ad_org_id, il.c_bpartner_id, po.c_bpartner_id, firstof(il.dateinvoiced::timestamp with time zone, 'Q'::character varying), il.c_currency_id;

ALTER TABLE rv_c_invoice_customervendqtr OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_day AS 
 SELECT rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DD'::character varying) AS dateinvoiced, sum(rv_c_invoiceline.linenetamt) AS linenetamt, sum(rv_c_invoiceline.linelistamt) AS linelistamt, sum(rv_c_invoiceline.linelimitamt) AS linelimitamt, sum(rv_c_invoiceline.linediscountamt) AS linediscountamt, rv_c_invoiceline.c_currency_id, 
        CASE
            WHEN sum(rv_c_invoiceline.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(rv_c_invoiceline.linelistamt) - sum(rv_c_invoiceline.linenetamt)) / sum(rv_c_invoiceline.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(rv_c_invoiceline.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(rv_c_invoiceline.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(rv_c_invoiceline.linenetamt) - sum(rv_c_invoiceline.lineoverlimitamt)) / sum(rv_c_invoiceline.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, rv_c_invoiceline.issotrx
   FROM rv_c_invoiceline
  GROUP BY rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DD'::character varying), rv_c_invoiceline.issotrx, rv_c_invoiceline.c_currency_id;

ALTER TABLE rv_c_invoice_day OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_month AS 
 SELECT rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'MM'::character varying) AS dateinvoiced, sum(rv_c_invoiceline.linenetamt) AS linenetamt, sum(rv_c_invoiceline.linelistamt) AS linelistamt, sum(rv_c_invoiceline.linelimitamt) AS linelimitamt, sum(rv_c_invoiceline.linediscountamt) AS linediscountamt, rv_c_invoiceline.c_currency_id, 
        CASE
            WHEN sum(rv_c_invoiceline.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(rv_c_invoiceline.linelistamt) - sum(rv_c_invoiceline.linenetamt)) / sum(rv_c_invoiceline.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(rv_c_invoiceline.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(rv_c_invoiceline.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(rv_c_invoiceline.linenetamt) - sum(rv_c_invoiceline.lineoverlimitamt)) / sum(rv_c_invoiceline.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, rv_c_invoiceline.issotrx
   FROM rv_c_invoiceline
  GROUP BY rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'MM'::character varying), rv_c_invoiceline.issotrx, rv_c_invoiceline.c_currency_id;

ALTER TABLE rv_c_invoice_month OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_prodmonth AS 
 SELECT il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced, il.issotrx
   FROM rv_c_invoiceline il
  GROUP BY il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying), il.issotrx, il.c_currency_id;

ALTER TABLE rv_c_invoice_prodmonth OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_prodweek AS 
 SELECT il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'DY'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced, il.issotrx
   FROM rv_c_invoiceline il
  GROUP BY il.ad_client_id, il.ad_org_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'DY'::character varying), il.issotrx, il.c_currency_id;

ALTER TABLE rv_c_invoice_prodweek OWNER TO libertya;

CREATE OR REPLACE VIEW rv_c_invoice_vendormonth AS 
 SELECT il.ad_client_id, il.ad_org_id, po.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying) AS dateinvoiced, sum(il.linenetamt) AS linenetamt, sum(il.linelistamt) AS linelistamt, sum(il.linelimitamt) AS linelimitamt, sum(il.linediscountamt) AS linediscountamt, il.c_currency_id, 
        CASE
            WHEN sum(il.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(il.linelistamt) - sum(il.linenetamt)) / sum(il.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(il.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(il.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(il.linenetamt) - sum(il.lineoverlimitamt)) / sum(il.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, sum(il.qtyinvoiced) AS qtyinvoiced
   FROM rv_c_invoiceline il
   JOIN m_product_po po ON il.m_product_id = po.m_product_id
  WHERE il.issotrx = 'Y'::bpchar
  GROUP BY il.ad_client_id, il.ad_org_id, po.c_bpartner_id, il.m_product_category_id, firstof(il.dateinvoiced::timestamp with time zone, 'MM'::character varying), il.c_currency_id;

ALTER TABLE rv_c_invoice_vendormonth OWNER TO libertya;


CREATE OR REPLACE VIEW rv_c_invoice_week AS 
 SELECT rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DY'::character varying) AS dateinvoiced, sum(rv_c_invoiceline.linenetamt) AS linenetamt, sum(rv_c_invoiceline.linelistamt) AS linelistamt, sum(rv_c_invoiceline.linelimitamt) AS linelimitamt, sum(rv_c_invoiceline.linediscountamt) AS linediscountamt, rv_c_invoiceline.c_currency_id, 
        CASE
            WHEN sum(rv_c_invoiceline.linelistamt) = 0::numeric THEN 0::numeric
            ELSE round((sum(rv_c_invoiceline.linelistamt) - sum(rv_c_invoiceline.linenetamt)) / sum(rv_c_invoiceline.linelistamt) * 100::numeric, 2)
        END AS linediscount, sum(rv_c_invoiceline.lineoverlimitamt) AS lineoverlimitamt, 
        CASE
            WHEN sum(rv_c_invoiceline.linenetamt) = 0::numeric THEN 0::numeric
            ELSE 100::numeric - round((sum(rv_c_invoiceline.linenetamt) - sum(rv_c_invoiceline.lineoverlimitamt)) / sum(rv_c_invoiceline.linenetamt) * 100::numeric, 2)
        END AS lineoverlimit, rv_c_invoiceline.issotrx
   FROM rv_c_invoiceline
  GROUP BY rv_c_invoiceline.ad_client_id, rv_c_invoiceline.ad_org_id, rv_c_invoiceline.salesrep_id, firstof(rv_c_invoiceline.dateinvoiced::timestamp with time zone, 'DY'::character varying), rv_c_invoiceline.issotrx, rv_c_invoiceline.c_currency_id;

ALTER TABLE rv_c_invoice_week OWNER TO libertya;

CREATE OR REPLACE VIEW rv_openitem AS 
 SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, p.netdays, i.dateinvoiced + ((p.netdays::text || ' days'::text)::interval) AS duedate, paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) AS daysdue, i.dateinvoiced + ((p.discountdays::text || ' days'::text)::interval) AS discountdate, round(i.grandtotal * p.discount / 100::numeric, 2) AS discountamt, i.grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, 0) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
   FROM rv_c_invoice i
   JOIN c_paymentterm p ON i.c_paymentterm_id = p.c_paymentterm_id
  WHERE invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid <> 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar
UNION 
 SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, to_days(ips.duedate) - to_days(i.dateinvoiced) AS netdays, ips.duedate, to_days(now()) - to_days(ips.duedate) AS daysdue, ips.discountdate, ips.discountamt, ips.dueamt AS grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, ips.c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
   FROM rv_c_invoice i
   JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
  WHERE invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid = 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE rv_openitem OWNER TO libertya;

--20130531 - 0245 Incorporación de nuevas columnas en la tabla T_CUENTACORRIENTE
ALTER TABLE T_CUENTACORRIENTE ADD COLUMN C_AllocationHdr_ID integer;

--20130531 - 0245 Contemplar la posibilidad de mostrar los pedidos no facturados en el informe de cuenta corriente
ALTER TABLE T_CuentaCorriente ADD COLUMN ShowDetailedReceiptsPayments varchar(1);

-- 20130605 - 1510 Nuevas columnas en el tipo de documento 
ALTER TABLE c_doctype ADD COLUMN cai character varying(14);
ALTER TABLE c_doctype ADD COLUMN datecai timestamp without time zone;

-- 20130605 - 1530 Nuevas columnas en T_ElectronicInvoice
ALTER TABLE T_ElectronicInvoice ADD COLUMN importe_ajuste numeric(20,2);

--20130607-1800
DROP VIEW c_pos_declaracionvalores_v;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.ca_amount
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.ca_amount)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.c_invoice_id, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, COALESCE(ds.amount, 0::numeric)::numeric(22,2) AS ca_amount, i.allocation_active
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                            LEFT JOIN (select c_invoice_id, c_posjournal_id, sum(amount) as amount
					from c_allocationhdr as ah
					inner join c_allocationline as al on al.c_allocationhdr_id = ah.c_allocationhdr_id
					where ah.isactive = 'Y'
					group by c_invoice_id, c_posjournal_id) as ds on ds.c_invoice_id = inv.c_invoice_id and pj.c_posjournal_id = ds.c_posjournal_id
                       LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                      ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id
                                           FROM c_payment p
                                      JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                           GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id) p)
                UNION ALL 
                         SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.c_invoice_id, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno, c.allocation_active
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive AS allocation_active, pjp.c_invoice_id
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
               LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
              GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id) c)
        UNION ALL 
                 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id AS c_invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id AS invoice_id
                           FROM c_invoice i
                      JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
      GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--20130611-1320 Mejoras a la vista de detalle de allocations y nueva vista para el informe de Asignaciones de Factura
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
            WHEN al.c_invoice_credit_id IS NOT NULL THEN 'N'::bpchar
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
            WHEN al.c_payment_id IS NOT NULL THEN pppm.name
            WHEN al.c_cashline_id IS NOT NULL THEN cppm.name
            WHEN al.c_invoice_credit_id IS NOT NULL THEN dt.name
            ELSE NULL::character varying
        END AS payment_medium_name, COALESCE(p.c_currency_id, cl.c_currency_id, credit.c_currency_id) AS pay_currency_id
   FROM c_allocationhdr ah
   JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_invoice credit ON al.c_invoice_credit_id = credit.c_invoice_id
   LEFT JOIN c_doctype dt ON credit.c_doctype_id = dt.c_doctype_id
   LEFT JOIN c_pospaymentmedium cppm ON cppm.c_pospaymentmedium_id = cl.c_pospaymentmedium_id
   LEFT JOIN c_pospaymentmedium pppm ON pppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
  ORDER BY ah.c_allocationhdr_id, al.c_allocationline_id;

ALTER TABLE c_allocation_detail_v OWNER TO libertya;

CREATE OR REPLACE VIEW c_invoice_allocation_v AS 
 SELECT ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name AS allocation_doc_name, ah.documentno, ah.datetrx, ah.isactive, ah.docstatus, al.c_invoice_id, i.documentno AS invoice_documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name) AS customer, sum(al.amount) AS amount
   FROM c_allocationhdr ah
   LEFT JOIN c_doctype dt ON dt.c_doctype_id = ah.c_doctype_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
  GROUP BY ah.ad_client_id, ah.ad_org_id, ah.c_allocationhdr_id, ah.c_doctype_id, dt.name, ah.documentno, ah.datetrx, ah.isactive, ah.docstatus, al.c_invoice_id, i.documentno, i.dateinvoiced, i.grandtotal, bp.c_bpartner_id, bp.value, bp.name, COALESCE(i.nombrecli, bp.name);

ALTER TABLE c_invoice_allocation_v OWNER TO libertya;

--20130618-1650 Incorporación de nueva columna para permitir mostrar en los tipos de dato Tabla los identificadores de la tabla correspondiente
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_Ref_Table','isdisplayidentifiers', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20130618-1650 Incorporación de configuración de cajas diarias al realizar anulaciones globales de factura
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','voidinginvoiceposjournalconfig', 'character(1)'));
update ad_system set dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','voidinginvoicepaymentsposjournalconfig', 'character(1)'));

UPDATE ad_clientinfo
SET voidinginvoiceposjournalconfig = 'U'
WHERE ad_client_id = 1010016;

UPDATE ad_clientinfo
SET voidinginvoicepaymentsposjournalconfig = 'P'
WHERE ad_client_id = 1010016;

--20130619-1602 LYWeb nueva columna utilizada en el visor de cuentas
ALTER TABLE c_acctschema_element ADD COLUMN ad_column_id numeric(10,0);

--20130626-1625 Creación función SQL getInvoiceAmt utilizada en informe Estado de Cuenta EC
CREATE OR REPLACE FUNCTION getInvoiceAmt(p_c_invoice_id integer, p_c_currency_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_TotalAmt			NUMERIC := 0;
BEGIN
	SELECT 
	(CASE
	WHEN d.documenttable = 'C_Invoice' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount + (CASE WHEN al.c_invoice_credit_id IS NULL THEN 0.0 ELSE (al.writeoffamt + al.discountamt) END )) END) FROM C_AllocationLine al WHERE ((al.c_invoice_id = d.document_id) OR (al.c_invoice_credit_id = d.document_id)) AND (al.isactive = 'Y'))
	WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isactive = 'Y'))
	ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isactive = 'Y')) END)
	+
	(SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN 
	invoiceOpen(d.document_id, (SELECT C_InvoicePaySchedule_ID FROM c_invoicepayschedule ips WHERE ips.c_invoice_id = d.document_id))
	WHEN d.documenttable = 'C_CashLine' THEN
	cashlineavailable(d.document_id)
	ELSE paymentavailable(d.document_id) END, d.c_currency_id, p_c_currency_id, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id))
	* SIGN(d.amount)::numeric AS Debit
	INTO v_TotalAmt
	FROM V_Documents d
	WHERE d.DocStatus IN ('CO','CL', 'RE', 'VO') AND document_ID = p_c_invoice_id;

	RETURN v_TotalAmt;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getInvoiceAmt(integer, integer) OWNER TO libertya;

--20130626-1625 Incoporación de columnas a la tabla t_estadodecuenta utilizada en informe Estado de Cuenta EC
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('t_estadodecuenta','grandtotalmulticurrency', 'numeric'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('t_estadodecuenta','paidamtmulticurrency', 'numeric'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('t_estadodecuenta','openamtmulticurrency', 'numeric'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('t_estadodecuenta','conversionrate', 'numeric'));

-- 20130702-1035 LYWeb: Nuevas vistas requeridas para el InfoProductPanel
CREATE OR REPLACE VIEW m_product_stock_v AS 
 SELECT ms.isactive, ms.created, ms.createdby, ms.updated, ms.updatedby, mp.value, mp.help, ms.qtyonhand - ms.qtyreserved AS qtyavailable, ms.qtyonhand, ms.qtyreserved, mp.description, mw.name AS warehouse, mw.m_warehouse_id, mw.ad_client_id, mw.ad_org_id, mp.documentnote
   FROM m_storage ms
   JOIN m_product mp ON ms.m_product_id = mp.m_product_id
   JOIN m_locator ml ON ms.m_locator_id = ml.m_locator_id
   JOIN m_warehouse mw ON ml.m_warehouse_id = mw.m_warehouse_id
  ORDER BY mw.name;

CREATE OR REPLACE VIEW m_product_substituterelated_v AS 
 SELECT s.ad_client_id, s.ad_org_id, s.isactive, s.created, s.createdby, s.updated, s.updatedby, s.m_product_id, s.substitute_id, 'S' AS rowtype, mp.name, sum(ms.qtyonhand - ms.qtyreserved) AS qtyavailable, sum(ms.qtyonhand) AS qtyonhand, sum(ms.qtyreserved) AS qtyreserved, round(max(mpr.pricestd), 0) AS pricestd, mpr.m_pricelist_version_id, mw.m_warehouse_id, org.name AS orgname
   FROM m_substitute s
   JOIN m_storage ms ON ms.m_product_id = s.substitute_id
   JOIN m_product mp ON ms.m_product_id = mp.m_product_id
   JOIN m_locator ml ON ms.m_locator_id = ml.m_locator_id
   JOIN m_warehouse mw ON ml.m_warehouse_id = mw.m_warehouse_id
   JOIN m_productprice mpr ON ms.m_product_id = mpr.m_product_id
   JOIN ad_org org ON org.ad_org_id = mw.ad_org_id
  GROUP BY s.ad_client_id, s.ad_org_id, s.isactive, s.created, s.createdby, s.updated, s.updatedby, s.m_product_id, s.substitute_id, mw.m_warehouse_id, mpr.m_pricelist_version_id, org.name, mp.name
UNION 
 SELECT r.ad_client_id, r.ad_org_id, r.isactive, r.created, r.createdby, r.updated, r.updatedby, r.m_product_id, r.relatedproduct_id AS substitute_id, 'R' AS rowtype, mp.name, sum(ms.qtyonhand - ms.qtyreserved) AS qtyavailable, sum(ms.qtyonhand) AS qtyonhand, sum(ms.qtyreserved) AS qtyreserved, round(max(mpr.pricestd), 0) AS pricestd, mpr.m_pricelist_version_id, mw.m_warehouse_id, org.name AS orgname
   FROM m_relatedproduct r
   JOIN m_storage ms ON ms.m_product_id = r.relatedproduct_id
   JOIN m_product mp ON ms.m_product_id = mp.m_product_id
   JOIN m_locator ml ON ms.m_locator_id = ml.m_locator_id
   JOIN m_warehouse mw ON ml.m_warehouse_id = mw.m_warehouse_id
   JOIN m_productprice mpr ON ms.m_product_id = mpr.m_product_id
   JOIN ad_org org ON org.ad_org_id = mw.ad_org_id
  GROUP BY r.ad_client_id, r.ad_org_id, r.isactive, r.created, r.createdby, r.updated, r.updatedby, r.m_product_id, r.relatedproduct_id, mw.m_warehouse_id, mpr.m_pricelist_version_id, org.name, mp.name;

--20130704-1623 Actualización de la cantidad reservada del storage en base a la cantidad pedida - cantidad entregada - cantidad transferida de pedidos de ventas y la cantidad pedida del storage en base a la cantidad pedida - cantidad entregada - cantidad transferida de pedidos de compras
UPDATE m_storage as s
SET qtyreserved = coalesce((SELECT sum(ol.qtyordered - ol.qtydelivered - ol.qtytransferred) 
				FROM c_orderline as ol 
				INNER JOIN c_order as o ON o.c_order_id = ol.c_order_id 
				INNER JOIN c_doctype as dt ON dt.c_doctype_id = o.c_doctypetarget_id
				INNER JOIN m_warehouse as w ON w.m_warehouse_id = o.m_warehouse_id
				INNER JOIN m_locator as l ON l.m_warehouse_id = w.m_warehouse_id
				WHERE ol.ad_client_id = s.ad_client_id AND o.docstatus IN ('CO','CL') AND doctypekey = 'SOSO' AND ol.m_product_id = s.m_product_id AND s.m_locator_id = l.m_locator_id),0),
    qtyordered = coalesce((SELECT sum(ol.qtyordered - ol.qtydelivered - ol.qtytransferred) 
				FROM c_orderline as ol 
				INNER JOIN c_order as o ON o.c_order_id = ol.c_order_id 
				INNER JOIN c_doctype as dt ON dt.c_doctype_id = o.c_doctypetarget_id
				INNER JOIN m_warehouse as w ON w.m_warehouse_id = o.m_warehouse_id
				INNER JOIN m_locator as l ON l.m_warehouse_id = w.m_warehouse_id
				WHERE ol.ad_client_id = s.ad_client_id AND o.docstatus IN ('CO','CL') AND doctypekey = 'POO' AND ol.m_product_id = s.m_product_id AND s.m_locator_id = l.m_locator_id),0)
WHERE s.m_attributesetinstance_id = 0;

--20130704-1623 Actualización de la cantidad reservada de las líneas del pedido a 0 ya que se modificó la lógica de reapertura de pedidos
UPDATE c_orderline ol
SET qtyreserved = 0
WHERE EXISTS (SELECT c_order_id FROM c_order as o WHERE o.c_order_id = ol.c_order_id AND o.docstatus IN ('IP','??'));

--20130704-1820 Nueva columna para que los parámetros de procesos puedan ser encriptados
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('ad_process_para','isencrypted', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20130704-1910 Nueva columna para configurar enviar el comando de cancelación antes de imprimir a la impresora fiscal
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_controlador_fiscal','cmdcancelbeforeprintdocument', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20130709-1925 Nueva columna para registrar el costo en algunos movimientos de mercadería
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_productchange','cost', 'numeric(11,2) NOT NULL DEFAULT 0'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_productchange','costto', 'numeric(11,2) NOT NULL DEFAULT 0'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_splitting','cost', 'numeric(11,2) NOT NULL DEFAULT 0'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_splittingline','cost', 'numeric(11,2) NOT NULL DEFAULT 0'));

--20130710-1526 Nueva columna en m_product
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_product','deliverytime_promised', 'numeric(18,0)'));

--20130718-0240 Modificaciones a la vista de declaración de valores para que permita devolver el id y nombre del C_POS
DROP VIEW c_pos_declaracionvalores_v;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.ca_amount
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.ca_amount)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.c_invoice_id, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, COALESCE(ds.amount, 0::numeric)::numeric(22,2) AS ca_amount, i.allocation_active, pos.c_pos_id, pos.name as posname
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                      INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
                                 JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                            LEFT JOIN ( SELECT al.c_invoice_id, ah.c_posjournal_id, sum(al.amount) AS amount
                                         FROM c_allocationhdr ah
                                    JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
                                   WHERE ah.isactive = 'Y'::bpchar
                                   GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
                       LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                      ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name as posname
                                           FROM c_payment p
                                      JOIN c_posjournalpayments_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                                 INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                           GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) p)
                UNION ALL 
                         SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.c_invoice_id, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno, c.allocation_active, c.c_pos_id, c.posname
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name as posname
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournalpayments_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
                    INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
               LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
              GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) c)
        UNION ALL 
                 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id AS c_invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id AS invoice_id, pos.c_pos_id, pos.name as posname
                           FROM c_invoice i
                      JOIN c_posjournalpayments_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
            INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
      GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name as posname
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
      INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--20130723-1500 Mejoras al informe de saldos bancarios
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('t_bankbalances','tendertype', 'character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('t_bankbalances','description', 'character varying(255)'));
ALTER TABLE t_bankbalances ALTER COLUMN duedate DROP NOT NULL;

DROP VIEW v_bankbalances;

CREATE OR REPLACE VIEW v_bankbalances AS 
         SELECT bsl.ad_client_id, bsl.ad_org_id, bsl.isactive, bs.c_bankaccount_id, '@StatementLine@'::character varying AS documenttype, bsl.description::text::character varying AS documentno, bs.statementdate AS datetrx, bsl.dateacct AS duedate, bs.docstatus, '' AS ischequesencartera, 
                CASE
                    WHEN bsl.stmtamt < 0.0 THEN abs(bsl.stmtamt)
                    ELSE 0.0
                END AS debit, 
                CASE
                    WHEN bsl.stmtamt >= 0.0 THEN abs(bsl.stmtamt)
                    ELSE 0.0
                END AS credit, bsl.isreconciled, null as tendertype, bsl.description
           FROM c_bankstatementline bsl
      JOIN c_bankstatement bs ON bsl.c_bankstatement_id = bs.c_bankstatement_id
   LEFT JOIN c_bpartner bp ON bsl.c_bpartner_id = bp.c_bpartner_id
UNION 
         SELECT p.ad_client_id, p.ad_org_id, p.isactive, p.c_bankaccount_id, dt.name AS documenttype, COALESCE(
                CASE
                    WHEN p.couponnumber IS NOT NULL AND btrim(p.couponnumber::text) <> ''::text THEN p.couponnumber
                    WHEN p.checkno IS NOT NULL AND btrim(p.checkno::text) <> ''::text THEN p.checkno
                    ELSE p.documentno
                END, bp.name) AS documentno, p.datetrx, COALESCE(p.duedate, p.dateacct) AS duedate, p.docstatus, ba.ischequesencartera, 
                CASE
                    WHEN dt.signo_issotrx = 1 THEN abs(p.payamt)
                    ELSE 0.0
                END AS debit, 
                CASE
                    WHEN dt.signo_issotrx = (-1) THEN 
                    CASE
                        WHEN ba.ischequesencartera = 'Y'::bpchar THEN p.payamt
                        ELSE abs(p.payamt)
                    END
                    ELSE 0.0
                END AS credit, p.isreconciled, p.tendertype, coalesce(p.a_name, bp.name) || coalesce((CASE WHEN p.description IS NOT NULL AND trim(p.description) <> '' THEN ' - '||p.description ELSE NULL END),'') as description
           FROM c_payment p
      JOIN c_doctype dt ON p.c_doctype_id = dt.c_doctype_id
   JOIN c_bpartner bp ON p.c_bpartner_id = bp.c_bpartner_id
   JOIN c_bankaccount ba ON p.c_bankaccount_id = ba.c_bankaccount_id
  WHERE NOT (EXISTS ( SELECT bsl.c_bankstatementline_id, bsl.ad_client_id, bsl.ad_org_id, bsl.isactive, bsl.created, bsl.createdby, bsl.updated, bsl.updatedby, bsl.c_bankstatement_id, bsl.line, bsl.description, bsl.isreversal, bsl.c_payment_id, bsl.valutadate, bsl.dateacct, bsl.c_currency_id, bsl.trxamt, bsl.stmtamt, bsl.c_charge_id, bsl.chargeamt, bsl.interestamt, bsl.memo, bsl.referenceno, bsl.ismanual, bsl.efttrxid, bsl.efttrxtype, bsl.eftmemo, bsl.eftpayee, bsl.eftpayeeaccount, bsl.createpayment, bsl.statementlinedate, bsl.eftstatementlinedate, bsl.eftvalutadate, bsl.eftreference, bsl.eftcurrency, bsl.eftamt, bsl.eftcheckno, bsl.matchstatement, bsl.c_bpartner_id, bsl.c_invoice_id, bsl.processed, bsl.m_boletadeposito_id, bsl.isreconciled, bs.c_bankstatement_id, bs.ad_client_id, bs.ad_org_id, bs.isactive, bs.created, bs.createdby, bs.updated, bs.updatedby, bs.c_bankaccount_id, bs.name, bs.description, bs.ismanual, bs.statementdate, bs.beginningbalance, bs.endingbalance, bs.statementdifference, bs.createfrom, bs.processing, bs.processed, bs.posted, bs.eftstatementreference, bs.eftstatementdate, bs.matchstatement, bs.isapproved, bs.docstatus, bs.docaction, bsr.c_bankstatline_reconcil_id, bsr.ad_client_id, bsr.ad_org_id, bsr.isactive, bsr.created, bsr.createdby, bsr.updated, bsr.updatedby, bsr.c_bankstatementline_id, bsr.c_payment_id, bsr.m_boletadeposito_id, bsr.c_currency_id, bsr.trxamt, bsr.referenceno, bsr.ismanual, bsr.processed, bsr.isreconciled, bsr.processing, bsr.docstatus, bsr.docaction
    FROM c_bankstatementline bsl
   JOIN c_bankstatement bs ON bsl.c_bankstatement_id = bs.c_bankstatement_id
   LEFT JOIN c_bankstatline_reconcil bsr ON bsl.c_bankstatementline_id::numeric = bsr.c_bankstatementline_id
  WHERE (bs.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND (bsl.c_payment_id = p.c_payment_id OR bsr.c_payment_id = p.c_payment_id::numeric)));

ALTER TABLE v_bankbalances OWNER TO libertya;

--20130724-2000 Mejoras a la vista
DROP VIEW c_invoice_bydoctype_v;

CREATE OR REPLACE VIEW c_invoice_bydoctype_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, i.chargeamt, (i.chargeamt * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS chargeamt_withsign, (i.chargeamt * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS chargeamt_withmultiplier, i.totallines, (i.totallines * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS totallines_withsign, (i.totallines * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS totallines_withmultiplier, i.grandtotal, (i.grandtotal * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS grandtotal_withsign, (i.grandtotal * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS grandtotal_withmultiplier, d.signo_issotrx, 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END AS multiplier, d.docbasetype, d.doctypekey, d.name as doctypename
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id;

ALTER TABLE c_invoice_bydoctype_v OWNER TO libertya;

--20130731-0000 Mejoras de performance a los reportes de declaración de valores y declaración de valores por organización, entre otros
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
                        END AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, dt.isfiscaldocument, dt.isfiscal, ic.fiscalalreadyprinted
                   FROM c_allocationline al
              INNER JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
         LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
    LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
   LEFT JOIN c_doctype dt ON dt.c_doctype_id = ic.c_doctypetarget_id
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
                 SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::unknown AS c_invoice_id, NULL::unknown AS c_payment_id, cl.c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CA'::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || '_#'::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::unknown AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc('day'::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted
                   FROM c_cashline cl
              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
             WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
                      FROM c_allocationline al
                     WHERE al.c_cashline_id = cl.c_cashline_id)))
UNION ALL 
        ( SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::unknown AS c_invoice_id, p.c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, p.payamt AS amount, NULL::unknown AS c_cash_id, NULL::numeric(18,0) AS line, NULL::unknown AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
    FROM c_allocationline al
   WHERE al.c_payment_id = p.c_payment_id))
  ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

CREATE OR REPLACE VIEW c_posjournal_c_payment_v AS 
SELECT al.c_allocationhdr_id, 
	al.c_allocationline_id, 
	al.ad_client_id, 
	al.ad_org_id, 
	al.isactive, 
	al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
	p.tendertype::character varying AS tendertype, 
	p.documentno AS documentno, 
	p.description AS description, 
	((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text))::character varying AS info, 
	COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, 
	null::integer as c_cash_id, 
	null::numeric(18,0) as line, 
	null::integer as c_doctype_id, 
	p.checkno, 
	p.a_bank, 
	p.checkno AS transferno, 
	p.creditcardtype, 
	p.m_entidadfinancieraplan_id, 
	ep.m_entidadfinanciera_id, 
	p.couponnumber, 
	date_trunc('day'::text, ah.datetrx) AS allocationdate, 
	p.docstatus AS docstatus, 
	p.dateacct::date AS dateacct, 
	i.documentno AS invoice_documentno, 
	i.grandtotal AS invoice_grandtotal, 
	ef.value AS entidadfinanciera_value, 
	ef.name AS entidadfinanciera_name, 
	bp.value AS bp_entidadfinanciera_value, 
	bp.name AS bp_entidadfinanciera_name, 
	p.couponnumber AS cupon, 
	p.creditcardnumber AS creditcard, 
	null::bpchar as isfiscaldocument, 
	null::bpchar as isfiscal, 
	null::bpchar as fiscalalreadyprinted
FROM c_allocationline al
INNER JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
INNER JOIN c_payment p ON al.c_payment_id = p.c_payment_id
LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
UNION ALL 
        ( SELECT NULL::integer AS c_allocationhdr_id, 
		NULL::integer AS c_allocationline_id, 
		p.ad_client_id, 
		p.ad_org_id, 
		p.isactive, 
		p.created, 
		p.createdby, 
		p.updated, 
		p.updatedby, 
		NULL::integer AS c_invoice_id, 
		p.c_payment_id, 
		NULL::integer AS c_cashline_id, 
		NULL::integer AS c_invoice_credit_id, 
		p.tendertype::character varying(2) AS tendertype, 
		p.documentno, 
		p.description, 
		(((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, 
		p.payamt AS amount, 
		NULL::integer AS c_cash_id, 
		NULL::numeric(18,0) AS line, 
		NULL::integer AS c_doctype_id, 
		p.checkno, 
		p.a_bank, 
		p.checkno AS transferno, 
		p.creditcardtype, 
		p.m_entidadfinancieraplan_id, 
		ep.m_entidadfinanciera_id, 
		p.couponnumber, 
		date_trunc('day'::text, p.datetrx) AS allocationdate, 
		p.docstatus, 
		p.dateacct::date AS dateacct, 
		NULL::character varying(30) AS invoice_documentno, 
		NULL::numeric(20,2) AS invoice_grandtotal, 
		ef.value AS entidadfinanciera_value, 
		ef.name AS entidadfinanciera_name, 
		bp.value AS bp_entidadfinanciera_value, 
		bp.name AS bp_entidadfinanciera_name, 
		p.couponnumber AS cupon, 
		p.creditcardnumber AS creditcard, 
		NULL::bpchar AS isfiscaldocument, 
		NULL::bpchar AS isfiscal, 
		NULL::bpchar AS fiscalalreadyprinted
           FROM c_payment p
      LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
    FROM c_allocationline al
   WHERE al.c_payment_id = p.c_payment_id))
  ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournal_c_payment_v OWNER TO libertya;



CREATE OR REPLACE VIEW c_posjournal_c_cash_v AS 
SELECT al.c_allocationhdr_id, 
	al.c_allocationline_id, 
	al.ad_client_id, 
	al.ad_org_id, 
	al.isactive, 
	al.created, 
	al.createdby, 
	al.updated, 
	al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
        'CA'::character varying AS tendertype, 
        NULL::character varying AS documentno, 
        cl.description AS description, 
        ((c.name::text || '_#'::text) || cl.line::text)::character varying as info, 
        COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, 
	cl.c_cash_id, 
	cl.line, 
	null::integer as c_doctype_id, 
	null::character varying as checkno, 
	null::character varying as a_bank, 
	null::character varying AS transferno, 
	null::character(1) as creditcardtype, 
	null::integer as m_entidadfinancieraplan_id, 
	null::integer as m_entidadfinanciera_id, 
	null::character varying as couponnumber, 
	date_trunc('day'::text, ah.datetrx) AS allocationdate, 
        cl.docstatus AS docstatus, 
        c.dateacct::date AS dateacct, 
        i.documentno AS invoice_documentno, 
        i.grandtotal AS invoice_grandtotal, 
        null::character varying as entidadfinanciera_value, 
        null::character varying as entidadfinanciera_name, 
        null::character varying as bp_entidadfinanciera_value, 
        null::character varying as bp_entidadfinanciera_name, 
        null::character varying AS cupon, 
        null::character varying AS creditcard, 
        null::bpchar as isfiscaldocument, 
        null::bpchar as isfiscal, 
        null::bpchar as fiscalalreadyprinted
FROM c_allocationline al
INNER JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
INNER JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
INNER JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
UNION ALL 
                 SELECT NULL::integer AS c_allocationhdr_id, 
			NULL::integer AS c_allocationline_id, 
			cl.ad_client_id, 
			cl.ad_org_id, 
			cl.isactive, 
			cl.created, 
			cl.createdby, 
			cl.updated, 
			cl.updatedby, 
			NULL::integer AS c_invoice_id, 
			NULL::integer AS c_payment_id, 
			cl.c_cashline_id, 
			NULL::integer AS c_invoice_credit_id, 
			'CA'::character varying(2) AS tendertype, 
			NULL::character varying(30) AS documentno, 
			cl.description, 
			(((c.name::text || '_#'::text) || cl.line::text))::character varying(255) AS info, 
			cl.amount, 
			cl.c_cash_id, 
			cl.line, 
			NULL::integer AS c_doctype_id, 
			NULL::character varying(20) AS checkno, 
			NULL::character varying(255) AS a_bank, 
			NULL::character varying(20) AS transferno, 
			NULL::character(1) AS creditcardtype, 
			NULL::integer AS m_entidadfinancieraplan_id, 
			NULL::integer AS m_entidadfinanciera_id, 
			NULL::character varying(30) AS couponnumber, 
			date_trunc('day'::text, c.statementdate) AS allocationdate, 
			cl.docstatus, 
			c.dateacct::date AS dateacct, 
			NULL::character varying(30) AS invoice_documentno, 
			NULL::numeric(20,2) AS invoice_grandtotal, 
			NULL::character varying AS entidadfinanciera_value, 
			NULL::character varying AS entidadfinanciera_name, 
			NULL::character varying AS bp_entidadfinanciera_value, 
			NULL::character varying AS bp_entidadfinanciera_name, 
			NULL::character varying AS cupon, 
			NULL::character varying AS creditcard, 
			NULL::bpchar AS isfiscaldocument, 
			NULL::bpchar AS isfiscal, 
			NULL::bpchar AS fiscalalreadyprinted
                   FROM c_cashline cl
              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
             WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
                      FROM c_allocationline al
                     WHERE al.c_cashline_id = cl.c_cashline_id));

ALTER TABLE c_posjournal_c_cash_v OWNER TO libertya;


CREATE OR REPLACE VIEW c_posjournal_c_invoice_credit_v AS 

SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
	'CR'::character varying AS tendertype, 
        ic.documentno AS documentno, 
        ic.description AS description, 
        ic.documentno AS info, 
        COALESCE(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, NULL::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id), 0::numeric)::numeric(20,2) AS amount, 
        null::integer as c_cash_id, 
        null::integer as line, 
        ic.c_doctype_id, 
        null::character varying as checkno, 
        null::character varying as a_bank, 
        null::character varying AS transferno, 
        null::character(1) as creditcardtype, 
        null::integer as m_entidadfinancieraplan_id, 
        null::integer as m_entidadfinanciera_id, 
        null::character varying as couponnumber, 
        date_trunc('day'::text, ah.datetrx) AS allocationdate, 
	ic.docstatus AS docstatus, 
	ic.dateacct::date AS dateacct, 
	i.documentno AS invoice_documentno, 
	i.grandtotal AS invoice_grandtotal, 
	null::character varying AS entidadfinanciera_value, 
	null::character varying AS entidadfinanciera_name, 
	null::character varying as bp_entidadfinanciera_value, 
	null::character varying as bp_entidadfinanciera_name, 
	null::character varying as cupon, 
	null::character varying as creditcard, 
	dt.isfiscaldocument, 
	dt.isfiscal, 
	ic.fiscalalreadyprinted
FROM c_allocationline al
INNER JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
INNER JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
INNER JOIN c_doctype dt ON dt.c_doctype_id = ic.c_doctypetarget_id
LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id;

ALTER TABLE c_posjournal_c_invoice_credit_v OWNER TO libertya;


DROP VIEW c_pos_declaracionvalores_v;

CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.ca_amount
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.ca_amount)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.c_invoice_id, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, COALESCE(ds.amount, 0::numeric)::numeric(22,2) AS ca_amount, i.allocation_active, pos.c_pos_id, pos.name as posname
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                      INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
                                 JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                            LEFT JOIN ( SELECT al.c_invoice_id, ah.c_posjournal_id, sum(al.amount) AS amount
                                         FROM c_allocationhdr ah
                                    JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
                                   WHERE ah.isactive = 'Y'::bpchar
                                   GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
                       LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                      ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, p.c_currency_id, p.datetrx::timestamp with time zone, p.ad_client_id, p.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name as posname
                                           FROM c_payment p
                                      JOIN c_posjournal_c_payment_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                                 INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
                            LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                           GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) p)
                UNION ALL 
                         SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.c_invoice_id, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno, c.allocation_active, c.c_pos_id, c.posname
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, cl.c_currency_id, c.statementdate::timestamp with time zone, cl.ad_client_id, cl.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name as posname
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournal_c_cash_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
                    INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
               LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
              GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) c)
        UNION ALL 
                 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id AS c_invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id AS invoice_id, pos.c_pos_id, pos.name as posname
                           FROM c_invoice i
                      JOIN c_posjournal_c_invoice_credit_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
            INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
       LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
      GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name as posname
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
      INNER JOIN c_pos as pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;


CREATE INDEX cinvoice_posjournal
  ON c_invoice
  USING btree
  (c_posjournal_id);

CREATE INDEX callocationhdr_posjournal
  ON c_allocationhdr
  USING btree
  (c_posjournal_id);
  
CREATE INDEX cpayment_posjournal
  ON c_payment
  USING btree
  (c_posjournal_id);

CREATE INDEX ccashline_posjournal
  ON c_cashline
  USING btree
  (c_posjournal_id);

CREATE INDEX ccash_posjournal
  ON c_cash
  USING btree
  (c_posjournal_id);

CREATE INDEX c_allocationline_active
  ON c_allocationline
  USING btree
  (c_allocationhdr_id)
  WHERE isactive = 'Y'::bpchar;

--20130808-1522 Funciones por migración de requerimiento de Organizaciones Carpeta creadas por Jorge Dreher
-- Devuelve Y si es hijo, N si no lo es
-- dREHER
-- jorge.dreher@gmail.com

CREATE OR REPLACE FUNCTION libertya.getisnodechild(p_ad_client_id integer, p_ad_org_padre_id integer, p_ad_org_hijo_id integer, p_type_tree varchar)
  RETURNS char AS
$BODY$

DECLARE
	v_Items	NUMERIC := 0;
	v_IsChild CHAR := 'N';
BEGIN

-- inicio

SELECT 

 COUNT(n.*) INTO v_Items

 FROM AD_Tree AS t

 LEFT JOIN AD_TreeNode AS n ON t.AD_Tree_ID = n.AD_Tree_ID
 
 WHERE t.AD_Client_ID = p_ad_client_id -- 1010016 libertya default
	AND t.TreeType=p_type_tree
	AND n.Parent_ID=p_ad_org_padre_id -- ID del registro a buscar, este es el ID del treeNode padre
	AND n.Node_ID=p_ad_org_hijo_id; -- ID del registro a buscar, este el el ID del treeNode hijo

-- fin

IF v_Items ISNULL THEN
	v_Items := 0;
END IF;

IF v_Items > 0 THEN
   v_IsChild := 'Y';
END IF;
	
RETURN v_IsChild;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getisnodechild(integer, integer, integer, varchar) OWNER TO libertya;
 
-- Devuelve ID del node padre
-- dREHER
-- jorge.dreher@gmail.com

CREATE OR REPLACE FUNCTION libertya.getnodepadre(p_ad_client_id integer, p_ad_org_hijo_id integer, p_type_tree character varying)
  RETURNS NUMERIC AS
$BODY$

DECLARE
	v_NodePadre_ID NUMERIC := 0;
BEGIN

-- inicio

SELECT 

 n.Parent_ID AS NodoPadre INTO v_NodePadre_ID
 
 FROM AD_Tree AS t

 LEFT JOIN AD_TreeNode AS n ON t.AD_Tree_ID = n.AD_Tree_ID

 WHERE t.AD_Client_ID = p_ad_client_id  -- default libertya client 1010016 -- AD_Client_ID
	AND t.TreeType = p_type_tree  -- tipo Organizacion 'OO'
	AND n.Node_ID = p_ad_org_hijo_id;  -- 1010095; -- ID del registro a buscar, este el el ID del treeNode hijo, trae el padre

-- fin

IF v_NodePadre_ID ISNULL THEN
	v_NodePadre_ID := -1;
END IF;

RETURN v_NodePadre_ID;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION libertya.getnodepadre(integer, integer, character varying) OWNER TO libertya;

--20130809-1150 Nuevas columnas para configuración de categoría de iva y cuit por organización creadas por Jorge Dreher
ALTER TABLE AD_OrgInfo ADD COLUMN C_Categoria_IVA_ID numeric(10);
ALTER TABLE AD_OrgInfo ADD COLUMN CUIT character varying(13);

--20130820-2025 Incorporación de nueva columna a la vista 
DROP VIEW c_invoice_bydoctype_v;

CREATE OR REPLACE VIEW c_invoice_bydoctype_v AS 
 SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.processing, i.processed, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.istransferred, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.c_charge_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.isprinted, i.isdiscountprinted, i.ispaid, i.isindispute, i.ispayschedulevalid, i.chargeamt, (i.chargeamt * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS chargeamt_withsign, (i.chargeamt * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS chargeamt_withmultiplier, i.totallines, (i.totallines * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS totallines_withsign, (i.totallines * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS totallines_withmultiplier, i.grandtotal, (i.grandtotal * d.signo_issotrx::numeric(9,2))::numeric(20,2) AS grandtotal_withsign, (i.grandtotal * 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END)::numeric(20,2) AS grandtotal_withmultiplier, d.signo_issotrx, 
        CASE
            WHEN "substring"(d.docbasetype::text, 3, 3) = 'I'::text THEN 1::numeric
            ELSE - 1::numeric
        END AS multiplier, d.docbasetype, d.doctypekey, d.name AS doctypename, i.nombrecli
   FROM c_invoice i
   JOIN c_doctype d ON i.c_doctypetarget_id = d.c_doctype_id;

ALTER TABLE c_invoice_bydoctype_v OWNER TO libertya;

--20130828-0000 Nueva tabla de personalización de puntos de venta por letra por configuración de tpv
CREATE TABLE c_posletter
(
  c_posletter_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  c_pos_id integer NOT NULL,
  letter character(1) NOT NULL,
  posnumber integer NOT NULL,
  CONSTRAINT c_posletter_key PRIMARY KEY (c_posletter_id),
  CONSTRAINT pos_cposletter FOREIGN KEY (c_pos_id)
      REFERENCES c_pos (c_pos_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=TRUE
);
ALTER TABLE c_posletter OWNER TO libertya; 

-- 20130828-0713 Fix en referencia de c_bpartner hacia c_paymentterm.  Referenciaba registro de ad_client = 1000008
update c_bpartner set c_paymentterm_id = (select c_paymentterm_id from c_paymentterm where ad_componentobjectuid = 'CORE-C_PaymentTerm-1010083') where ad_componentobjectuid = 'CORE-C_BPartner-1012145' and c_paymentterm_id = 1000073;

--20130828-1310 Nueva configuración de TPV que permite generar el remito en estado borrador
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_pos','draftedinout', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20130902-1138 Tipos de documento que deberian tener org = 0 en lugar de 1010053
update c_doctype set ad_org_id = 0 where ad_org_id = 1010053 and ad_componentobjectuid in 
('CORE-C_DocType-1010537', 'CORE-C_DocType-1010538', 'CORE-C_DocType-1010550',
'CORE-C_DocType-1010572', 'CORE-C_DocType-1010573', 'CORE-C_DocType-1010574',
'CORE-C_DocType-1010575', 'CORE-C_DocType-1010576', 'CORE-C_DocType-1010577');

--201309002-1142 Secuencias que deberian tener org = 0 en lugar de 1010053
update ad_sequence set ad_org_id = 0 where ad_org_id = 1010053 and ad_componentobjectuid in
('CORE-AD_Sequence-1011740', 'CORE-AD_Sequence-1011812', 'CORE-AD_Sequence-1011861', 
'CORE-AD_Sequence-1011857', 'CORE-AD_Sequence-1011858', 'CORE-AD_Sequence-1011862', 
'CORE-AD_Sequence-1011739');

--20130912-1625 Nuevas columnas en Procesador de Retenciones / Percepciones
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_RetencionProcessor','SupportRegister', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('C_RetencionProcessor','ProcessorType', 'character(1)'));

--20130912-1655 Nueva columna para establecer el monto neto mínimo a aplicar en la percepción
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Org_Percepcion','MinimumNetAmount', 'numeric(20,2) NOT NULL DEFAULT 0'));

--20130912-1710 Nueva tabla para poder configurar el monto neto mínimo a aplicar en la percepción por padrón.
CREATE TABLE ad_org_percepcion_config
(
  ad_org_percepcion_config_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  ad_org_percepcion_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  padrontype character(1),
  minimumnetamount numeric(20,2) NOT NULL DEFAULT 0,
  CONSTRAINT ad_org_percepcion_config_key PRIMARY KEY (ad_org_percepcion_config_id),
  CONSTRAINT ad_org_percepcion FOREIGN KEY (ad_org_percepcion_id)
      REFERENCES ad_org_percepcion (ad_org_percepcion_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_org_percepcion_client FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ad_org_percepcion_org FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ad_org_percepcion_config OWNER TO libertya;

--20130920-1630 Nueva columna en la config de la compañía para configurar si la caja diaria debe estar abierta para cobros/pagos y sus allocations
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ClientInfo','paymentsposjournalopen', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20130920-1630 Nueva columna para transformar pestañas como siempre actualizables
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Tab','isalwaysupdateable', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

DROP VIEW ad_tab_v;
DROP VIEW ad_tab_vt;

CREATE OR REPLACE VIEW ad_tab_v AS 
 SELECT t.ad_tab_id, t.ad_window_id, t.ad_table_id, t.name, t.description, t.help, t.seqno, t.issinglerow, t.hastree, t.isinfotab, tbl.replicationtype, tbl.tablename, tbl.accesslevel, tbl.issecurityenabled, tbl.isdeleteable, tbl.ishighvolume, tbl.isview, 'N' AS hasassociation, t.istranslationtab, t.isreadonly, t.ad_image_id, t.tablevel, t.whereclause, t.orderbyclause, t.commitwarning, t.readonlylogic, t.displaylogic, t.ad_column_id, t.ad_process_id, t.issorttab, t.isinsertrecord, t.isadvancedtab, t.ad_columnsortorder_id, t.ad_columnsortyesno_id, t.included_tab_id, t.isprocessmsgshowdialog, t.isalwaysupdateable
   FROM ad_tab t
   JOIN ad_table tbl ON t.ad_table_id = tbl.ad_table_id
  WHERE t.isactive = 'Y'::bpchar AND tbl.isactive = 'Y'::bpchar;

ALTER TABLE ad_tab_v OWNER TO libertya;

CREATE OR REPLACE VIEW ad_tab_vt AS 
 SELECT trl.ad_language, t.ad_tab_id, t.ad_window_id, t.ad_table_id, trl.name, trl.description, trl.help, t.seqno, t.issinglerow, t.hastree, t.isinfotab, tbl.replicationtype, tbl.tablename, tbl.accesslevel, tbl.issecurityenabled, tbl.isdeleteable, tbl.ishighvolume, tbl.isview, 'N' AS hasassociation, t.istranslationtab, t.isreadonly, t.ad_image_id, t.tablevel, t.whereclause, t.orderbyclause, trl.commitwarning, t.readonlylogic, t.displaylogic, t.ad_column_id, t.ad_process_id, t.issorttab, t.isinsertrecord, t.isadvancedtab, t.ad_columnsortorder_id, t.ad_columnsortyesno_id, t.included_tab_id, t.isprocessmsgshowdialog, t.isalwaysupdateable
   FROM ad_tab t
   JOIN ad_table tbl ON t.ad_table_id = tbl.ad_table_id
   JOIN ad_tab_trl trl ON t.ad_tab_id = trl.ad_tab_id
  WHERE t.isactive = 'Y'::bpchar AND tbl.isactive = 'Y'::bpchar;

--20130926-1200 Nueva tabla para poder configurar los datos de un Servicio Externo.
CREATE TABLE C_ExternalService
(
  c_externalservice_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  "name" character varying(60) NOT NULL,
  description character varying(255),
  ad_componentobjectuid character varying(100),
  "value" character varying(40) NOT NULL,
  url character varying(255) NOT NULL,
  username character varying(60),
  password character varying(60),
  timeout integer,
  port integer,
  CONSTRAINT c_externalservice_key PRIMARY KEY (c_externalservice_id)
)
WITH (
  OIDS=TRUE
);
ALTER TABLE C_ExternalService OWNER TO libertya;

-- 20130930:1424 Flexibilizacion en la validacion por caso que repArrays no coincidan
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
	-- si no hay un valor de replicacion en el targetRepArray, entoces todo bien	
	IF sourceRepArray <> targetRepArray AND (position('1' in targetRepArray) = 0) THEN
		return 1;
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

--20131014-1400 Nuevas tablas para formatos de exportación y columna para configurar procesos con dichos formatos
CREATE TABLE ad_expformat
(
  ad_expformat_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  value character varying(40) NOT NULL,
  name character varying(60) NOT NULL,
  description character varying(255),
  ad_table_id integer NOT NULL,
  formattype character(1) NOT NULL,
  delimiter character(1),
  filename character varying(100) NOT NULL,
  concatenatetimestamp character(1) NOT NULL DEFAULT 'N'::bpchar,
  timestamppattern character varying(40),
  ad_componentobjectuid character varying(100),
  ad_componentversion_id integer,
  CONSTRAINT ad_expformat_key PRIMARY KEY (ad_expformat_id),
  CONSTRAINT adclient_adexpformat FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT adorg_adexpformat FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT adtable_adexpformat FOREIGN KEY (ad_table_id)
      REFERENCES ad_table (ad_table_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE ad_expformat OWNER TO libertya;

CREATE TABLE ad_expformat_row
(
  ad_expformat_row_id integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) with time zone,
  updatedby integer NOT NULL,
  ad_expformat_id integer NOT NULL,
  seqno numeric(18,0) NOT NULL,
  name character varying(60) NOT NULL,
  ad_column_id integer,
  length integer,
  datatype character(1) NOT NULL,
  dataformat character varying(20),
  decimalpoint character(1),
  constantvalue character varying(60),
  fillcharacter character(1),
  alignment character(1),
  orderseqno integer,
  orderdirection character(1),
  ad_componentobjectuid character varying(100),
  ad_componentversion_id integer,
  isorderfield character(1) NOT NULL DEFAULT 'N'::bpchar,
  CONSTRAINT ad_expformat_row_key PRIMARY KEY (ad_expformat_row_id),
  CONSTRAINT adclient_adexpformatrow FOREIGN KEY (ad_client_id)
      REFERENCES ad_client (ad_client_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT adcolumn_adexpformatrow FOREIGN KEY (ad_column_id)
      REFERENCES ad_column (ad_column_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT adexpformat_adexpformatrow FOREIGN KEY (ad_expformat_id)
      REFERENCES ad_expformat (ad_expformat_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT adorg_adexpformatrow FOREIGN KEY (ad_org_id)
      REFERENCES ad_org (ad_org_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE ad_expformat_row OWNER TO libertya;

UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Process','ad_expformat_id', 'integer'));
ALTER TABLE ad_process ADD CONSTRAINT adexpformat_adprocess FOREIGN KEY (ad_expformat_id)
      REFERENCES ad_expformat (ad_expformat_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
--20131014-1400 Nueva vista para exportar percepciones
CREATE OR REPLACE VIEW c_invoice_percepciones_v AS 
 SELECT i.ad_client_id, i.ad_org_id, dt.c_doctype_id, dt.name AS doctypename, 
        CASE
            WHEN dt.signo_issotrx = 1 THEN 'F'::text
            ELSE 'C'::text
        END AS doctypechar, i.c_invoice_id, i.documentno, date_trunc('day'::text, i.dateinvoiced) AS dateinvoiced, lc.letra, i.puntodeventa, i.numerocomprobante, i.grandtotal, bp.c_bpartner_id, bp.value AS bpartner_value, bp.name AS bpartner_name, bp.taxid, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script, COALESCE(i.nombrecli, bp.name) AS nombrecli, COALESCE(i.nroidentificcliente, bp.taxid) AS nroidentificcliente, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS nroidentificcliente_with_script, t.c_tax_id, t.name AS percepcionname, it.taxbaseamt, it.taxamt
   FROM c_invoicetax it
   JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id
   JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND i.issotrx = 'Y'::bpchar;

ALTER TABLE c_invoice_percepciones_v OWNER TO libertya; 

--20131015-1225 Modificaciones a la vista de percepciones para informe
DROP VIEW c_invoice_percepciones_v;

CREATE OR REPLACE VIEW c_invoice_percepciones_v AS 
 SELECT i.ad_client_id, i.ad_org_id, dt.c_doctype_id, dt.name AS doctypename, 
        CASE
            WHEN dt.signo_issotrx = 1 THEN 'F'::text
            ELSE 'C'::text
        END AS doctypechar, 
        CASE
            WHEN substring(dt.doctypekey from 1 for 2) = 'CI' THEN 'F'::text
            WHEN substring(dt.doctypekey from 1 for 2) = 'CC' THEN 'NC'::text
            ELSE 'ND'::text
        END AS doctypenameshort,
        i.c_invoice_id, i.documentno, date_trunc('day'::text, i.dateinvoiced) AS dateinvoiced, lc.letra, i.puntodeventa, i.numerocomprobante, i.grandtotal, bp.c_bpartner_id, bp.value AS bpartner_value, bp.name AS bpartner_name, bp.taxid, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script, COALESCE(i.nombrecli, bp.name) AS nombrecli, COALESCE(i.nroidentificcliente, bp.taxid) AS nroidentificcliente, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS nroidentificcliente_with_script, t.c_tax_id, t.name AS percepcionname, it.taxbaseamt, it.taxamt
   FROM c_invoicetax it
   JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id
   JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND i.issotrx = 'Y'::bpchar;

ALTER TABLE c_invoice_percepciones_v OWNER TO libertya;

--20131015-1230 Nuevo columna de botón para copiar parámetros de un proceso a otro
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_Process','copyparameters', 'character(1)'));

--20131015-2015 Fix a vista de declaración de valores ya que convertía el monto del allocation en la moneda del cobro cuando en realidad el allocation está en la moneda base
CREATE OR REPLACE VIEW c_pos_declaracionvalores_v AS 
        (        (        (         SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, dt.docbasetype AS tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN i.ca_amount
                                            WHEN (-1) THEN 0::numeric
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE dt.signo_issotrx
                                            WHEN 1 THEN 0::numeric
                                            WHEN (-1) THEN abs(i.ca_amount)
                                            ELSE NULL::numeric
                                        END::numeric(22,2) AS egreso, i.c_invoice_id, i.documentno AS invoice_documentno, i.total AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                                   FROM ( SELECT DISTINCT i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2) AS total, COALESCE(ds.amount, 0::numeric)::numeric(22,2) AS ca_amount, i.allocation_active, pos.c_pos_id, pos.name AS posname
                                           FROM c_posjournalinvoices_v i
                                      JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                 JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
                            JOIN c_invoice inv ON i.c_invoice_id = inv.c_invoice_id
                       LEFT JOIN ( SELECT al.c_invoice_id, ah.c_posjournal_id, sum(al.amount) AS amount
                                    FROM c_allocationhdr ah
                               JOIN c_allocationline al ON al.c_allocationhdr_id = ah.c_allocationhdr_id
                              WHERE ah.isactive = 'Y'::bpchar
                              GROUP BY al.c_invoice_id, ah.c_posjournal_id) ds ON ds.c_invoice_id = inv.c_invoice_id AND pj.c_posjournal_id = ds.c_posjournal_id
                  LEFT JOIN c_charge ch ON ch.c_charge_id = inv.c_charge_id
                 ORDER BY i.ad_client_id, i.ad_org_id, i.documentno, inv.description, i.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, currencybase(i.grandtotal, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2), COALESCE(ds.amount, 0::numeric)::numeric(22,2), i.allocation_active, pos.c_pos_id, pos.name) i
                              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
                        UNION ALL 
                                 SELECT p.ad_client_id, p.ad_org_id, p.c_posjournal_id, p.ad_user_id, p.c_currency_id, p.datetrx, p.docstatus, NULL::unknown AS category, p.tendertype, (p.documentno::text || ' '::text) || COALESCE(p.description, ''::character varying)::text AS description, p.c_charge_id, p.chargename, p.c_payment_id AS doc_id, 
                                        CASE p.isreceipt
                                            WHEN 'Y'::bpchar THEN p.total
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS ingreso, 
                                        CASE p.isreceipt
                                            WHEN 'N'::bpchar THEN abs(p.total)
                                            ELSE 0::numeric
                                        END::numeric(22,2) AS egreso, p.c_invoice_id, p.invoice_documentno, p.invoice_grandtotal, p.entidadfinanciera_value, p.entidadfinanciera_name, p.bp_entidadfinanciera_value, p.bp_entidadfinanciera_name, p.cupon, p.creditcard, NULL::unknown AS generated_invoice_documentno, p.allocation_active, p.c_pos_id, p.posname
                                   FROM ( SELECT p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date AS datetrx, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount)::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname
                                           FROM c_payment p
                                      JOIN c_posjournal_c_payment_v pjp ON pjp.c_payment_id = p.c_payment_id
                                 JOIN c_posjournal pj ON pj.c_posjournal_id = p.c_posjournal_id
                            JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
                       LEFT JOIN c_charge ch ON ch.c_charge_id = p.c_charge_id
                      GROUP BY p.ad_client_id, p.ad_org_id, p.c_payment_id, p.c_posjournal_id, pj.ad_user_id, p.c_currency_id, p.datetrx::date, p.docstatus, p.documentno, p.description, p.isreceipt, p.tendertype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) p)
                UNION ALL 
                         SELECT c.ad_client_id, c.ad_org_id, c.c_posjournal_id, c.ad_user_id, c.c_currency_id, c.datetrx, c.docstatus, c.cashtype AS category, c.tendertype, 
                                CASE
                                    WHEN length(c.description::text) > 0 THEN c.description
                                    ELSE c.info
                                END AS description, c.c_charge_id, c.chargename, c.c_cashline_id AS doc_id, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN 0::numeric
                                    ELSE c.total
                                END::numeric(22,2) AS ingreso, 
                                CASE sign(c.amount)
                                    WHEN (-1) THEN abs(c.total)
                                    ELSE 0::numeric
                                END::numeric(22,2) AS egreso, c.c_invoice_id, c.invoice_documentno, c.invoice_grandtotal, c.entidadfinanciera_value, c.entidadfinanciera_name, c.bp_entidadfinanciera_value, c.bp_entidadfinanciera_name, c.cupon, c.creditcard, NULL::unknown AS generated_invoice_documentno, c.allocation_active, c.c_pos_id, c.posname
                           FROM ( SELECT cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date AS datetrx, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name AS chargename, sum(pjp.amount)::numeric(22,2) as total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive AS allocation_active, pjp.c_invoice_id, pos.c_pos_id, pos.name AS posname
                                   FROM c_cashline cl
                              JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
                         JOIN c_posjournal_c_cash_v pjp ON pjp.c_cashline_id = cl.c_cashline_id
                    JOIN c_posjournal pj ON pj.c_posjournal_id = c.c_posjournal_id
               JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
          LEFT JOIN c_charge ch ON ch.c_charge_id = cl.c_charge_id
         GROUP BY cl.ad_client_id, cl.ad_org_id, cl.c_cashline_id, c.c_posjournal_id, pj.ad_user_id, cl.c_currency_id, c.statementdate::date, cl.docstatus, cl.description, pjp.info, pjp.tendertype, cl.cashtype, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, cl.amount, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) c)
        UNION ALL 
                 SELECT i.ad_client_id, i.ad_org_id, i.c_posjournal_id, i.ad_user_id, i.c_currency_id, i.dateinvoiced AS datetrx, i.docstatus, NULL::unknown AS category, i.tendertype, (i.documentno::text || ' '::text) || COALESCE(i.description, ''::character varying)::text AS description, i.c_charge_id, i.chargename, i.c_invoice_id AS doc_id, i.total AS ingreso, 0 AS egreso, i.invoice_id AS c_invoice_id, i.invoice_documentno, i.invoice_grandtotal, i.entidadfinanciera_value, i.entidadfinanciera_name, i.bp_entidadfinanciera_value, i.bp_entidadfinanciera_name, i.cupon, i.creditcard, NULL::unknown AS generated_invoice_documentno, i.allocation_active, i.c_pos_id, i.posname
                   FROM ( SELECT i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date AS dateinvoiced, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name AS chargename, sum(currencybase(pjp.amount, i.c_currency_id, i.dateinvoiced::timestamp with time zone, i.ad_client_id, i.ad_org_id)::numeric(22,2))::numeric(22,2) AS total, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive AS allocation_active, pjp.c_invoice_id AS invoice_id, pos.c_pos_id, pos.name AS posname
                           FROM c_invoice i
                      JOIN c_posjournal_c_invoice_credit_v pjp ON pjp.c_invoice_credit_id = i.c_invoice_id
                 JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pjp.c_allocationhdr_id
            JOIN c_posjournal pj ON pj.c_posjournal_id = ah.c_posjournal_id
       JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   LEFT JOIN c_charge ch ON ch.c_charge_id = i.c_charge_id
  GROUP BY i.ad_client_id, i.ad_org_id, i.documentno, i.description, ah.c_posjournal_id, pj.ad_user_id, i.c_invoice_id, i.c_currency_id, i.docstatus, pjp.tendertype, i.dateinvoiced::date, i.c_bpartner_id, i.c_doctype_id, ch.c_charge_id, ch.name, pjp.invoice_documentno, pjp.invoice_grandtotal, pjp.entidadfinanciera_value, pjp.entidadfinanciera_name, pjp.bp_entidadfinanciera_value, pjp.bp_entidadfinanciera_name, pjp.cupon, pjp.creditcard, pjp.isactive, pjp.c_invoice_id, pos.c_pos_id, pos.name) i
              JOIN c_doctype dt ON i.c_doctype_id = dt.c_doctype_id
             WHERE i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar]))
UNION ALL 
         SELECT ji.ad_client_id, ji.ad_org_id, ji.c_posjournal_id, pj.ad_user_id, ji.c_currency_id, ji.dateinvoiced AS datetrx, ji.docstatus, NULL::unknown AS category, ji.docbasetype AS tendertype, (ji.documentno::text || ' '::text) || COALESCE(ji.description, ''::character varying)::text AS description, NULL::unknown AS c_charge_id, NULL::unknown AS chargename, ji.c_invoice_id AS doc_id, ji.signo_issotrx::numeric * ji.grandtotal AS ingreso, 0::numeric(22,2) AS egreso, ji.c_invoice_id, ji.documentno AS invoice_documentno, ji.grandtotal AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, ic.documentno AS generated_invoice_documentno, ji.allocation_active, pos.c_pos_id, pos.name AS posname
           FROM c_posjournalinvoices_v ji
      JOIN c_posjournal pj ON ji.c_posjournal_id = pj.c_posjournal_id
   JOIN c_pos pos ON pos.c_pos_id = pj.c_pos_id
   JOIN c_allocationline al ON al.c_allocationhdr_id = ji.c_allocationhdr_id
   JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
  WHERE (ji.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (ji.isfiscal IS NULL OR ji.isfiscal = 'N'::bpchar OR ji.isfiscal = 'Y'::bpchar AND ji.fiscalalreadyprinted = 'Y'::bpchar);

ALTER TABLE c_pos_declaracionvalores_v OWNER TO libertya;

--20131021-1045 Incorporación de nuevas columnas a las líneas de reglas de precios para soportar incluir impuesto de artículo en el precio y filtro para comprados y vendidos
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_DiscountSchemaLine','limit_addproducttax', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_DiscountSchemaLine','list_addproducttax', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_DiscountSchemaLine','std_addproducttax', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('M_DiscountSchemaLine','soldpurchasedoption', 'character(1)'));

--20131021-1815 Incorporación de nueva columna a la columna del formato de exportación para el lugar de colocación del signo negativo para números
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('AD_ExpFormat_Row','negative_position', 'character(1)'));

DROP VIEW c_invoice_percepciones_v;

CREATE OR REPLACE VIEW c_invoice_percepciones_v AS 
 SELECT i.ad_client_id, i.ad_org_id, dt.c_doctype_id, dt.name AS doctypename, 
        CASE
            WHEN dt.signo_issotrx = 1 THEN 'F'::text
            ELSE 'C'::text
        END AS doctypechar, 
        CASE
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CI'::text THEN 'F'::text
            WHEN "substring"(dt.doctypekey::text, 1, 2) = 'CC'::text THEN 'NC'::text
            ELSE 'ND'::text
        END AS doctypenameshort, i.c_invoice_id, i.documentno, date_trunc('day'::text, i.dateinvoiced) AS dateinvoiced, lc.letra, i.puntodeventa, i.numerocomprobante, i.grandtotal, bp.c_bpartner_id, bp.value AS bpartner_value, bp.name AS bpartner_name, bp.taxid, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS taxid_with_script, COALESCE(i.nombrecli, bp.name) AS nombrecli, COALESCE(i.nroidentificcliente, bp.taxid) AS nroidentificcliente, ((("substring"(replace(bp.taxid::text, '-'::text, ''::text), 1, 2) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 3, 8)) || '-'::text) || "substring"(replace(bp.taxid::text, '-'::text, ''::text), 11, 1) AS nroidentificcliente_with_script, t.c_tax_id, t.name AS percepcionname, it.taxbaseamt, it.taxamt, (it.taxbaseamt * dt.signo_issotrx)::numeric(20,2) as taxbaseamt_with_sign, (it.taxamt * dt.signo_issotrx)::numeric(20,2) as taxamt_with_sign
   FROM c_invoicetax it
   JOIN c_invoice i ON i.c_invoice_id = it.c_invoice_id
   JOIN c_letra_comprobante lc ON lc.c_letra_comprobante_id = i.c_letra_comprobante_id
   JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_tax t ON t.c_tax_id = it.c_tax_id
  WHERE t.ispercepcion = 'Y'::bpchar AND ((i.docstatus = ANY (ARRAY['CL'::bpchar, 'CO'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND dt.isfiscal = 'Y'::bpchar AND i.fiscalalreadyprinted = 'Y'::bpchar) AND i.issotrx = 'Y'::bpchar;

ALTER TABLE c_invoice_percepciones_v OWNER TO libertya;

--20131024-1230 Mejoras y fixes a la vista de resumen de ventas
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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR (pjp.c_invoice_credit_id IS NOT NULL AND cc.docstatus IN ('CO','CL')))
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
UNION ALL
SELECT 'NCC' AS trxtype, 
	i.ad_client_id, 
	i.ad_org_id, 
	i.c_invoice_id, 
	date_trunc('day'::text, i.dateinvoiced) AS datetrx, 
	NULL::unknown AS c_payment_id, 
	NULL::unknown AS c_cashline_id, 
	NULL::unknown AS c_invoice_credit_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN 'CC' 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS tendertype, 
	i.documentno, 
	i.description, 
	NULL::unknown AS info, 
	i.grandtotal * dt.signo_issotrx::numeric AS amount, 
	bp.c_bpartner_id, 
	bp.name, 
	bp.c_bp_group_id, 
	bpg.name AS groupname, 
	bp.c_categoria_iva_id, 
	ci.name AS categorianame, 
	(SELECT ad_ref_list_id FROM ad_ref_list WHERE ad_reference_id = 195 and value = i.paymentrule LIMIT 1) AS c_pospaymentmedium_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN 'CC' 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS pospaymentmediumname, 
	NULL::unknown AS m_entidadfinanciera_id, 
	NULL::unknown AS entidadfinancieraname, 
	NULL::unknown AS m_entidadfinancieraplan_id, 
	NULL::unknown AS planname, 
	i.docstatus, 
	i.issotrx, 
	i.dateacct::date AS dateacct, 
	i.dateacct::date AS invoicedateacct, 
	pj.c_posjournal_id, 
	pj.ad_user_id, 
	pj.c_pos_id, 
	dt.isfiscal, 
	i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   WHERE dt.docbasetype = 'ARC'
	AND (i.docstatus IN ('CO','CL') OR (i.docstatus IN ('VO','RE') AND EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_id = i.c_invoice_id)))
	AND NOT EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_credit_id = i.c_invoice_id)
UNION ALL
SELECT 'PA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * -1 as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'
UNION ALL
SELECT 'ND' AS trxtype, 
	i.ad_client_id, 
	i.ad_org_id, 
	i.c_invoice_id, 
	date_trunc('day'::text, i.dateinvoiced) AS datetrx, 
	NULL::unknown AS c_payment_id, 
	NULL::unknown AS c_cashline_id, 
	NULL::unknown AS c_invoice_credit_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN 'CC' 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS tendertype, 
	i.documentno, 
	i.description, 
	NULL::unknown AS info, 
	i.grandtotal * dt.signo_issotrx::numeric AS amount, 
	bp.c_bpartner_id, 
	bp.name, 
	bp.c_bp_group_id, 
	bpg.name AS groupname, 
	bp.c_categoria_iva_id, 
	ci.name AS categorianame, 
	(SELECT ad_ref_list_id FROM ad_ref_list WHERE ad_reference_id = 195 and value = i.paymentrule LIMIT 1) AS c_pospaymentmedium_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN 'CC' 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS pospaymentmediumname, 
	NULL::unknown AS m_entidadfinanciera_id, 
	NULL::unknown AS entidadfinancieraname, 
	NULL::unknown AS m_entidadfinancieraplan_id, 
	NULL::unknown AS planname, 
	i.docstatus, 
	i.issotrx, 
	i.dateacct::date AS dateacct, 
	i.dateacct::date AS invoicedateacct, 
	pj.c_posjournal_id, 
	pj.ad_user_id, 
	pj.c_pos_id, 
	dt.isfiscal, 
	i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   WHERE position('CDN' in dt.doctypekey) = 1
	AND (i.docstatus IN ('CO','CL') OR (i.docstatus IN ('VO','RE') AND EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_credit_id = i.c_invoice_id)))
	AND NOT EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_id = i.c_invoice_id);

ALTER TABLE v_dailysales OWNER TO libertya;

--20131025-1650 Fix a vista de resumen de ventas
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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR (pjp.c_invoice_credit_id IS NOT NULL AND cc.docstatus IN ('CO','CL')))
                UNION ALL 
                         SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
        UNION ALL 
                 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
           FROM c_posjournalpayments_v pjp
      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL)
UNION ALL
SELECT 'NCC' AS trxtype, 
	i.ad_client_id, 
	i.ad_org_id, 
	i.c_invoice_id, 
	date_trunc('day'::text, i.dateinvoiced) AS datetrx, 
	NULL::unknown AS c_payment_id, 
	NULL::unknown AS c_cashline_id, 
	NULL::unknown AS c_invoice_credit_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN 'CC' 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS tendertype, 
	i.documentno, 
	i.description, 
	NULL::unknown AS info, 
	i.grandtotal * dt.signo_issotrx::numeric AS amount, 
	bp.c_bpartner_id, 
	bp.name, 
	bp.c_bp_group_id, 
	bpg.name AS groupname, 
	bp.c_categoria_iva_id, 
	ci.name AS categorianame, 
	(CASE WHEN i.paymentrule = 'P' THEN NULL ELSE (SELECT ad_ref_list_id FROM ad_ref_list WHERE ad_reference_id = 195 and value = i.paymentrule LIMIT 1) END) AS c_pospaymentmedium_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN NULL 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS pospaymentmediumname, 
	NULL::unknown AS m_entidadfinanciera_id, 
	NULL::unknown AS entidadfinancieraname, 
	NULL::unknown AS m_entidadfinancieraplan_id, 
	NULL::unknown AS planname, 
	i.docstatus, 
	i.issotrx, 
	i.dateacct::date AS dateacct, 
	i.dateacct::date AS invoicedateacct, 
	pj.c_posjournal_id, 
	pj.ad_user_id, 
	pj.c_pos_id, 
	dt.isfiscal, 
	i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   WHERE dt.docbasetype = 'ARC'
	AND (i.docstatus IN ('CO','CL') OR (i.docstatus IN ('VO','RE') AND EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_id = i.c_invoice_id)))
	AND NOT EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_credit_id = i.c_invoice_id)
UNION ALL
SELECT 'PA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                                    ELSE i.c_invoice_id
                                END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                                CASE
                                    WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                                    ELSE pjp.c_invoice_credit_id
                                END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * -1 as amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
                                END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                           FROM c_posjournalpayments_v pjp
                      JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                 LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
            JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
       JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'
UNION ALL
SELECT 'ND' AS trxtype, 
	i.ad_client_id, 
	i.ad_org_id, 
	i.c_invoice_id, 
	date_trunc('day'::text, i.dateinvoiced) AS datetrx, 
	NULL::unknown AS c_payment_id, 
	NULL::unknown AS c_cashline_id, 
	NULL::unknown AS c_invoice_credit_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN 'CC' 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS tendertype, 
	i.documentno, 
	i.description, 
	NULL::unknown AS info, 
	i.grandtotal * dt.signo_issotrx::numeric AS amount, 
	bp.c_bpartner_id, 
	bp.name, 
	bp.c_bp_group_id, 
	bpg.name AS groupname, 
	bp.c_categoria_iva_id, 
	ci.name AS categorianame, 
	(SELECT ad_ref_list_id FROM ad_ref_list WHERE ad_reference_id = 195 and value = i.paymentrule LIMIT 1) AS c_pospaymentmedium_id, 
	(CASE WHEN i.paymentrule = 'T' OR i.paymentrule = 'Tr' THEN 'A' 
		WHEN i.paymentrule = 'B' THEN 'CA' 
		WHEN i.paymentrule = 'K' THEN 'C' 
		WHEN i.paymentrule = 'P' THEN 'CC' 
		WHEN i.paymentrule = 'S' THEN 'K' 
		ELSE i.paymentrule END) AS pospaymentmediumname, 
	NULL::unknown AS m_entidadfinanciera_id, 
	NULL::unknown AS entidadfinancieraname, 
	NULL::unknown AS m_entidadfinancieraplan_id, 
	NULL::unknown AS planname, 
	i.docstatus, 
	i.issotrx, 
	i.dateacct::date AS dateacct, 
	i.dateacct::date AS invoicedateacct, 
	pj.c_posjournal_id, 
	pj.ad_user_id, 
	pj.c_pos_id, 
	dt.isfiscal, 
	i.fiscalalreadyprinted
                   FROM c_invoice i
              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
         JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
    JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   WHERE position('CDN' in dt.doctypekey) = 1
	AND (i.docstatus IN ('CO','CL') OR (i.docstatus IN ('VO','RE') AND EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_credit_id = i.c_invoice_id)))
	AND NOT EXISTS (SELECT c_allocationline_id FROM c_allocationline al WHERE al.c_invoice_id = i.c_invoice_id);

ALTER TABLE v_dailysales OWNER TO libertya;

--20131030-1130 Incorporación de columnas utilizadas en Exportación RG1361
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('e_electronicinvoice','docbasetype', 'character varying(10)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('e_electronicinvoice','totalsign', 'numeric(22,0)'));

--20131031-1920 Incorporación de nuevo campo a la impresora fiscal para confirmar impresión o no en caso de error
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_controlador_fiscal','askwhenerror', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20131111-1340 Incorporación de nuevo campo para tipo de cuenta del informe de saldos
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('t_balancereport','accounttype', 'character(1)'));

--20131111-1500 DELETEs por parche de requerimiento a cliente del tipo de cuenta del informe de saldos
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_PY-1010852';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_MX-1010852';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_AR-1010852';
DELETE FROM AD_Process_Para_Trl WHERE ad_componentobjectuid = 'CORE-AD_Process_Para_Trl-es_ES-1010852';
DELETE FROM AD_Process_Para WHERE ad_componentobjectuid = 'CORE-AD_Process_Para-1010852';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016341-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016341-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016341-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016341-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1016341';

--20131101-1610 Incorporación de la columna Stock Disponible para la Venta a la tabla Almacén.
update ad_system set dummy = (SELECT addcolumnifnotexists('M_Warehouse','StockAvailableForSale', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20131111-1700 Incorporación de nuevo campo a la factura y remito para configurar el tipo de impresión del documento: Original, Duplicado y Triplicado
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('c_invoice','printtype', 'character(1)'));
UPDATE ad_system SET dummy = (SELECT addcolumnifnotexists('m_inout','printtype', 'character(1)'));

--20131111-1800 DELETEs por parche de requerimiento a cliente del tipo de impresión del documento: Original, Duplicado y Triplicado
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017632-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017632-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017632-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017632-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1017632';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016344-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016344-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016344-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016344-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1016344';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017631-es_PY';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017631-es_MX';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017631-es_AR';
DELETE FROM AD_Field_Trl WHERE ad_componentobjectuid = 'CORE-AD_Field_Trl-1017631-es_ES';
DELETE FROM AD_Field WHERE ad_componentobjectuid = 'CORE-AD_Field-1017631';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_PY-1010695';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_MX-1010695';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_AR-1010695';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_ES-1010695';
DELETE FROM AD_Ref_List WHERE ad_componentobjectuid = 'CORE-AD_Ref_List-1010695';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_PY-1010694';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_MX-1010694';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_AR-1010694';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_ES-1010694';
DELETE FROM AD_Ref_List WHERE ad_componentobjectuid = 'CORE-AD_Ref_List-1010694';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_PY-1010693';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_MX-1010693';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_AR-1010693';
DELETE FROM AD_Ref_List_Trl WHERE ad_componentobjectuid = 'CORE-AD_Ref_List_Trl-es_ES-1010693';
DELETE FROM AD_Ref_List WHERE ad_componentobjectuid = 'CORE-AD_Ref_List-1010693';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_PY-1010235';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_MX-1010235';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_AR-1010235';
DELETE FROM AD_Reference_Trl WHERE ad_componentobjectuid = 'CORE-AD_Reference_Trl-es_ES-1010235';
DELETE FROM AD_Reference WHERE ad_componentobjectuid = 'CORE-AD_Reference-1010235';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016343-es_PY';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016343-es_MX';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016343-es_AR';
DELETE FROM AD_Column_Trl WHERE ad_componentobjectuid = 'CORE-AD_Column_Trl-1016343-es_ES';
DELETE FROM AD_Column WHERE ad_componentobjectuid = 'CORE-AD_Column-1016343';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011411-es_PY';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011411-es_MX';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011411-es_AR';
DELETE FROM AD_Element_Trl WHERE ad_componentobjectuid = 'CORE-AD_Element_Trl-1011411-es_ES';
DELETE FROM AD_Element WHERE ad_componentobjectuid = 'CORE-AD_Element-1011411';

-- 20131112-1300 Omitir validaciones para columna M_Transfer.M_Inventory_ID 
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
	-- Tambien se omite validacion para la columna M_Transfer.M_Inventory_ID, debido a que
	-- por mas que esta columna viaje, luego en el destino es convertida a null
	IF column_data IS NULL OR p_columnid = 1014174 THEN
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
	-- si no hay un valor de replicacion en el targetRepArray, entoces todo bien	
	IF sourceRepArray <> targetRepArray AND (position('1' in targetRepArray) = 0) THEN
		return 1;
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

-- 20131113-1750 Creación de la función infoproductbomqty utilizada en la ventana InfoProduct. 
CREATE OR REPLACE FUNCTION infoproductbomqty(bomqtyname character varying, m_product_id numeric, m_warehouse_ids character varying, m_locator_id numeric)
  RETURNS numeric AS
$BODY$
DECLARE
	m_Warehouse_ID integer;
	aux_m_warehouse_ids character varying;
	totalQty numeric = 0;
BEGIN
    aux_m_warehouse_ids := m_warehouse_ids;
    WHILE (char_length(aux_m_warehouse_ids) > 0) LOOP
	IF (position('-' in m_warehouse_ids) = 0) THEN
		m_Warehouse_ID := m_warehouse_ids;
		aux_m_warehouse_ids := '';
	ELSE
		m_Warehouse_ID := cast(substring(aux_m_warehouse_ids from 1 for position('-' in aux_m_warehouse_ids)-1) as int);
	END IF;
	aux_m_warehouse_ids :=  substring(aux_m_warehouse_ids from position('-' in aux_m_warehouse_ids)+1 for char_length(aux_m_warehouse_ids));

	IF (bomqtyname = 'bomQtyOnHand') THEN
		totalQty := totalQty + bomQtyOnHand(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
	ELSIF (bomqtyname = 'bomQtyAvailable') THEN
		totalQty := totalQty + bomQtyAvailable(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
	ELSIF (bomqtyname = 'bomQtyReserved') THEN
		totalQty := totalQty + bomQtyReserved(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
	ELSIF (bomqtyname = 'bomQtyOrdered') THEN
		totalQty := totalQty + bomQtyOrdered(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
	ELSE
		RETURN 0;
	END IF;
    END LOOP;
    RETURN totalQty;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION infoproductbomqty(character varying, numeric, character varying, numeric) OWNER TO libertya;

-- 20131113-1750 Modificación de la vista rv_storage_product_plus para que solo tenga en cuenta aquellos almacenes marcados como Disponibles para la Venta. 
CREATE OR REPLACE VIEW rv_storage_product_plus AS 
 SELECT p.ad_client_id, p.ad_org_id, p.m_product_id, p.value, p.name, p.m_product_category_id, p.m_warehouse_id, p.qtyonhand, p.qtyreserved, p.qtyavailable, p.qtyordered, ( SELECT po.c_bpartner_id
           FROM m_product_po po
          WHERE po.m_product_id = p.m_product_id
          ORDER BY po.iscurrentvendor
         LIMIT 1) AS c_bpartner_id, COALESCE(( SELECT pp.pricelist
           FROM m_productprice pp
      JOIN m_pricelist_version plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id
   JOIN m_pricelist pl ON pl.m_pricelist_id = plv.m_pricelist_id
  WHERE pl.issopricelist = 'Y'::bpchar AND pl.isactive = 'Y'::bpchar AND plv.isactive = 'Y'::bpchar AND pp.isactive = 'Y'::bpchar AND (pl.ad_org_id = p.ad_org_id OR pl.ad_org_id = 0) AND pp.m_product_id = p.m_product_id
  ORDER BY pl.ad_org_id DESC, plv.validfrom DESC
 LIMIT 1), 0::numeric) AS sales_pricelist, COALESCE(( SELECT pp.pricelist
           FROM m_productprice pp
      JOIN m_pricelist_version plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id
   JOIN m_pricelist pl ON pl.m_pricelist_id = plv.m_pricelist_id
  WHERE pl.issopricelist = 'N'::bpchar AND pl.isactive = 'Y'::bpchar AND plv.isactive = 'Y'::bpchar AND pp.isactive = 'Y'::bpchar AND (pl.ad_org_id = p.ad_org_id OR pl.ad_org_id = 0) AND pp.m_product_id = p.m_product_id
  ORDER BY pl.ad_org_id DESC, plv.validfrom DESC
 LIMIT 1), 0::numeric) AS cost_pricelist
   FROM ( SELECT s.ad_client_id, s.ad_org_id, s.m_product_id, s.value, s.name, s.m_product_category_id, s.m_warehouse_id, sum(s.qtyonhand) AS qtyonhand, sum(s.qtyreserved) AS qtyreserved, sum(s.qtyavailable) AS qtyavailable, sum(s.qtyordered) AS qtyordered
           FROM ( SELECT s.ad_client_id, s.ad_org_id, s.m_product_id, p.value, p.name, p.m_product_category_id, l.m_warehouse_id, s.qtyonhand, s.qtyreserved, s.qtyonhand - s.qtyreserved AS qtyavailable, s.qtyordered
                   FROM m_storage s
              JOIN m_locator l ON s.m_locator_id = l.m_locator_id
              JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id
         JOIN m_product p ON s.m_product_id = p.m_product_id 
         WHERE (w.StockAvailableForSale = 'Y')) s
          GROUP BY s.ad_client_id, s.ad_org_id, s.m_product_id, s.value, s.name, s.m_product_category_id, s.m_warehouse_id) p;

ALTER TABLE rv_storage_product_plus OWNER TO libertya;

-- 20131113-1750 Modificación de la vista rv_storage para que solo tenga en cuenta aquellos almacenes marcados como Disponibles para la Venta.
CREATE OR REPLACE VIEW rv_storage AS 
 SELECT s.ad_client_id, s.ad_org_id, s.m_product_id, p.value, p.name, p.description, p.upc, p.sku, p.c_uom_id, p.m_product_category_id, p.classification, p.weight, p.volume, p.versionno, p.guaranteedays, p.guaranteedaysmin, s.m_locator_id, l.m_warehouse_id, l.x, l.y, l.z, s.qtyonhand, s.qtyreserved, s.qtyonhand - s.qtyreserved AS qtyavailable, s.qtyordered, s.datelastinventory, s.m_attributesetinstance_id, asi.m_attributeset_id, asi.serno, asi.lot, asi.m_lot_id, asi.guaranteedate, 
        CASE
            WHEN asi.guaranteedate IS NULL THEN NULL::integer
            ELSE daysbetween(asi.guaranteedate::timestamp with time zone, now())
        END AS shelflifedays, 
        CASE
            WHEN asi.guaranteedate IS NULL THEN NULL::numeric
            ELSE daysbetween(asi.guaranteedate::timestamp with time zone, now())::numeric - p.guaranteedaysmin
        END AS goodfordays, 
        CASE
            WHEN asi.guaranteedate IS NULL THEN NULL::numeric
            WHEN p.guaranteedays = 0::numeric THEN NULL::numeric
            ELSE round(daysbetween(asi.guaranteedate::timestamp with time zone, now())::numeric / p.guaranteedays * 100::numeric, 0)
        END AS shelfliferemainingpct
   FROM m_storage s
   JOIN m_locator l ON s.m_locator_id = l.m_locator_id
   JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id
   JOIN m_product p ON s.m_product_id = p.m_product_id
   LEFT JOIN m_attributesetinstance asi ON s.m_attributesetinstance_id = asi.m_attributesetinstance_id
   WHERE (w.StockAvailableForSale = 'Y');

ALTER TABLE rv_storage OWNER TO libertya;

-- 20131122-1815 Incorporación de nueva columna en C_Controlador_Fiscal para permitir imprimir el campo descripción de línea en el ticket fiscal
update ad_system set dummy = (SELECT addcolumnifnotexists('C_Controlador_Fiscal','OnlyLineDescription', 'character(1) NOT NULL DEFAULT ''N''::bpchar'));

--20131127-1330 Las pestañas van todas como no Siempre Actualizables por un error en el preinstall al agregar la columna
update ad_tab
set isalwaysupdateable = 'N';

-- 20131129-1200 Incorporación de nueva columna en importador de inventario para poder actualizar uno existente
update ad_system set dummy = (SELECT addcolumnifnotexists('I_Inventory','Inventory_DocumentNo', 'character varying(30)'));

--20131204-0851 UnattendedUpgrader - Actualizador de instancias LY desatendido.  Tablas de configuracion
CREATE TABLE AD_UnattendedUpgrade (
  AD_UnattendedUpgrade_ID integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  version varchar(100) not NULL,
  description varchar(255) NULL,
  directory varchar(255) NOT NULL,
  scheduledFor timestamp NOT NULL,
  CONSTRAINT AD_UnattendedUpgrade_key PRIMARY KEY (AD_UnattendedUpgrade_ID)
);

CREATE TABLE AD_UnattendedUpgradeHost (
  AD_UnattendedUpgradeHost_ID integer NOT NULL,
  ad_client_id integer NOT NULL,
  ad_org_id integer NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y'::bpchar,
  created timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  createdby integer NOT NULL,
  updated timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp(6) WITH time zone,
  updatedby integer NOT NULL,
  AD_UnattendedUpgrade_ID integer NOT NULL,
  status character(1) not null default 'P',
  upgraded timestamp NULL,
  errorMsg varchar(500) null,
  CONSTRAINT AD_UnattendedUpgradeHost_key PRIMARY KEY (AD_UnattendedUpgradeHost_ID),
  CONSTRAINT UnattendedUpgradeHeader FOREIGN KEY (AD_UnattendedUpgrade_ID) REFERENCES AD_UnattendedUpgrade (AD_UnattendedUpgrade_ID) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- 20131204-1236 UnattendedUpgrader - Actualizador de instancias LY desatendido.  Ampliacion de las tablas
ALTER TABLE AD_UnattendedUpgrade ADD COLUMN ScheduleUpgradeAllHosts Character(1);

-- 20131205-1600 Nuevos índices a la tabla de padron bs as para mejoras de performance 
CREATE INDEX c_bpartner_padron_bsas_cuit
  ON c_bpartner_padron_bsas
  USING btree
  (cuit);

CREATE INDEX c_bpartner_padron_bsas_bpartner_id
  ON c_bpartner_padron_bsas
  USING btree
  (c_bpartner_id);

-- 20131206-1330 Incorporación de nueva tabla utilizada al cargar padron de retenciones y percepciones.
CREATE TABLE i_padron_sujeto_aux
(
  fecha_publicacion character varying(8),
  fecha_desde character varying(8),
  fecha_hasta character varying(8),
  cuit character varying(11),
  tipo_contr_insc character(1),
  alta_baja character(1),
  cbio_alicuota character(1),
  percepcion character varying(6),
  retencion character varying(6),
  nro_grupo_ret integer,
  nro_grupo_per integer
)
WITH (
  OIDS=TRUE
);
ALTER TABLE i_padron_sujeto_aux OWNER TO libertya;

--20131210-0105 Mejoras a la vista en la parte de cuenta corriente
DROP VIEW v_dailysales;

CREATE OR REPLACE VIEW v_dailysales AS 
        (        (        (        (        (         SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
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
                                                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_posjournalpayments_v pjp
                                              JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                                         LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                                    JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
                               JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                          JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                     JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
                LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
           LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
      LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
                                        UNION ALL 
                                                 SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                                   FROM c_invoice i
                                              LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus IN ('CO','CL') AND hdr.isactive = 'Y'::bpchar) OR (i.docstatus IN ('VO','RE') AND hdr.isactive = 'N'::bpchar))) c
                                                GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
                               JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
                          JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
                     LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
                    WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
                                UNION ALL 
                                         SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                                           FROM c_invoice i
                                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
                                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                                   FROM c_posjournalpayments_v pjp
                              JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
                         LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                    JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
               JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
          JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
     JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL))
                UNION ALL 
                         SELECT 'NCC' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                                CASE
                                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                    ELSE i.paymentrule
                                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
                                CASE
                                    WHEN i.paymentrule::text = 'P'::text THEN NULL::integer
                                    ELSE ( SELECT ad_ref_list.ad_ref_list_id
                                       FROM ad_ref_list
                                      WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                                     LIMIT 1)
                                END AS c_pospaymentmedium_id, 
                                CASE
                                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                                    WHEN i.paymentrule::text = 'P'::text THEN NULL::character varying
                                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                                    ELSE i.paymentrule
                                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
                           FROM c_invoice i
                      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
                 JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
            JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
       JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE dt.docbasetype = 'ARC'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
          FROM c_allocationline al
         WHERE al.c_invoice_credit_id = i.c_invoice_id)))
        UNION ALL 
                 SELECT 'PA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
                            ELSE i.c_invoice_id
                        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
                        CASE
                            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
                            ELSE pjp.c_invoice_credit_id
                        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
                        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
                   FROM c_posjournalpayments_v pjp
              JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
         LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
    JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar)
UNION ALL 
         SELECT 'ND' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
                CASE
                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                    ELSE i.paymentrule
                END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
                   FROM ad_ref_list
                  WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
                 LIMIT 1) AS c_pospaymentmedium_id, 
                CASE
                    WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
                    WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
                    WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
                    WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
                    WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
                    ELSE i.paymentrule
                END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
           FROM c_invoice i
      LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE "position"(dt.doctypekey::text, 'CDN'::text) = 1 AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id));

ALTER TABLE v_dailysales OWNER TO libertya;

-- 20131212-1513 UnattendedUpgrader - Actualizador de instancias LY desatendido.  Ampliacion de las tablas
ALTER TABLE AD_UnattendedUpgradeHost ADD COLUMN AD_TargetOrg_ID int NOT NULL;

-- 20131216-0901 Incorporación de nuevas columnas a la vista de movimientos de artículos
DROP VIEW v_product_movements;

CREATE OR REPLACE VIEW v_product_movements AS 
 SELECT m.tablename, m.ad_client_id, m.ad_org_id, o.value AS orgvalue, o.name AS orgname, m.doc_id, m.documentno, m.docstatus, m.description, m.datetrx, m.m_product_id, m.qty, m.type, m.aditionaltype, m.c_charge_id, c.name AS chargename, p.name AS productname, p.value AS productvalue, w.m_warehouse_id, w.value AS warehousevalue, w.name AS warehousename, wt.m_warehouse_id AS m_warehouseto_id, wt.value AS warehousetovalue, wt.name AS warehousetoname, bp.c_bpartner_id, bp.value AS bpartnervalue, bp.name AS bpartnername, pc.m_product_category_id, pc.value AS productcategoryvalue, pc.name AS productcategoryname, COALESCE(pg.m_product_gamas_id, 0) AS m_product_gamas_id, COALESCE(pg.value, 'SD'::character varying) AS productgamasvalue, COALESCE(pg.name, 'SIN DESCRIPCION'::character varying) AS productgamasname, COALESCE(pl.m_product_lines_id, 0) AS m_product_lines_id, COALESCE(pl.value, 'SD'::character varying) AS productlinesvalue, COALESCE(pl.name, 'SIN DESCRIPCION'::character varying) AS productlinesname
   FROM (        (        (        (        (         SELECT 'M_Transfer' AS tablename, t.ad_client_id, t.ad_org_id, t.m_transfer_id AS doc_id, t.documentno, t.docstatus, t.description, t.datetrx, tl.m_product_id, tl.qty, t.transfertype AS type, t.movementtype AS aditionaltype, t.c_charge_id, t.m_warehouse_id, t.m_warehouseto_id, t.c_bpartner_id
                                                   FROM m_transfer t
                                              JOIN m_transferline tl ON tl.m_transfer_id = t.m_transfer_id
                                        UNION ALL 
                                                 SELECT 'M_ProductChange' AS tablename, pc.ad_client_id, pc.ad_org_id, pc.m_productchange_id AS doc_id, pc.documentno, pc.docstatus, pc.description, pc.datetrx, il.m_product_id, il.qtyinternaluse * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, il.c_charge_id, pc.m_warehouse_id, NULL::unknown AS m_warehouseto_id, NULL::unknown AS c_bpartner_id
                                                   FROM m_productchange pc
                                              JOIN m_inventoryline il ON il.m_inventory_id = pc.m_inventory_id)
                                UNION ALL 
                                         SELECT 'M_InOut' AS tablename, io.ad_client_id, io.ad_org_id, io.m_inout_id AS doc_id, io.documentno, io.docstatus, io.description, io.movementdate AS datetrx, iol.m_product_id, iol.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, io.m_warehouse_id, NULL::unknown AS m_warehouseto_id, io.c_bpartner_id
                                           FROM m_inout io
                                      JOIN m_inoutline iol ON iol.m_inout_id = io.m_inout_id
                                 JOIN c_doctype dt ON dt.c_doctype_id = io.c_doctype_id)
                        UNION ALL 
                                 SELECT 'M_Splitting' AS tablename, s.ad_client_id, s.ad_org_id, s.m_splitting_id AS doc_id, s.documentno, s.docstatus, s.comments AS description, s.datetrx, il.m_product_id, il.qtyinternaluse * (-1)::numeric AS qty, NULL::unknown AS type, NULL::unknown AS aditionaltype, il.c_charge_id, s.m_warehouse_id, NULL::unknown AS m_warehouseto_id, NULL::unknown AS c_bpartner_id
                                   FROM m_splitting s
                              JOIN m_inventoryline il ON il.m_inventory_id = s.m_inventory_id)
                UNION ALL 
                         SELECT 'M_Inventory' AS tablename, i.ad_client_id, i.ad_org_id, i.m_inventory_id AS doc_id, i.documentno, i.docstatus, i.description, i.movementdate AS datetrx, il.m_product_id, il.qtycount AS qty, dt.name AS type, i.inventorykind AS aditionaltype, i.c_charge_id, i.m_warehouse_id, NULL::unknown AS m_warehouseto_id, NULL::unknown AS c_bpartner_id
                           FROM m_inventory i
                      JOIN m_inventoryline il ON i.m_inventory_id = il.m_inventory_id
                 JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id
                WHERE NOT (EXISTS ( SELECT m_transfer.m_inventory_id
                         FROM m_transfer
                        WHERE m_transfer.m_inventory_id = i.m_inventory_id)) AND NOT (EXISTS ( SELECT m_productchange.m_inventory_id
                         FROM m_productchange
                        WHERE m_productchange.m_inventory_id = i.m_inventory_id OR m_productchange.void_inventory_id = i.m_inventory_id)) AND NOT (EXISTS ( SELECT s.m_inventory_id
                         FROM m_splitting s
                        WHERE s.m_inventory_id = i.m_inventory_id OR s.void_inventory_id = i.m_inventory_id)))
        UNION ALL 
                 SELECT 'M_Movement' AS tablename, m.ad_client_id, m.ad_org_id, m.m_movement_id AS doc_id, m.documentno, m.docstatus, m.description, m.movementdate AS datetrx, ml.m_product_id, ml.movementqty AS qty, dt.name AS type, NULL::unknown AS aditionaltype, NULL::unknown AS c_charge_id, w.m_warehouse_id, wt.m_warehouse_id AS m_warehouseto_id, NULL::unknown AS c_bpartner_id
                   FROM m_movement m
              JOIN m_movementline ml ON ml.m_movement_id = m.m_movement_id
         JOIN m_locator l ON l.m_locator_id = ml.m_locator_id
    JOIN m_warehouse w ON w.m_warehouse_id = l.m_warehouse_id
   JOIN m_locator lt ON lt.m_locator_id = ml.m_locatorto_id
   JOIN m_warehouse wt ON wt.m_warehouse_id = lt.m_warehouse_id
   JOIN c_doctype dt ON dt.c_doctype_id = m.c_doctype_id) m
   JOIN m_product p ON p.m_product_id = m.m_product_id
   JOIN ad_org o ON o.ad_org_id = m.ad_org_id
   JOIN m_warehouse w ON w.m_warehouse_id = m.m_warehouse_id
   JOIN m_product_category pc ON pc.m_product_category_id = p.m_product_category_id
   LEFT JOIN m_product_gamas pg ON pg.m_product_gamas_id = pc.m_product_gamas_id
   LEFT JOIN m_product_lines pl ON pl.m_product_lines_id = pg.m_product_lines_id
   LEFT JOIN m_warehouse wt ON wt.m_warehouse_id = m.m_warehouseto_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = m.c_bpartner_id
   LEFT JOIN c_charge c ON c.c_charge_id = m.c_charge_id;

ALTER TABLE v_product_movements OWNER TO libertya;

--20131216-1553 Referencia al C_Order desde el M_Transfer
ALTER TABLE M_Transfer ADD COLUMN C_Order_ID INT NULL;

--20131217-1215 Fix a la función ya que traía más de un resultado la subquery de invoicepayschedule
CREATE OR REPLACE FUNCTION getinvoiceamt(p_c_invoice_id integer, p_c_currency_id integer)
  RETURNS numeric AS
$BODY$
DECLARE
	v_TotalAmt			NUMERIC := 0;
BEGIN
	SELECT 
	(CASE
	WHEN d.documenttable = 'C_Invoice' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount + (CASE WHEN al.c_invoice_credit_id IS NULL THEN 0.0 ELSE (al.writeoffamt + al.discountamt) END )) END) FROM C_AllocationLine al WHERE ((al.c_invoice_id = d.document_id) OR (al.c_invoice_credit_id = d.document_id)) AND (al.isactive = 'Y'))
	WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isactive = 'Y'))
	ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isactive = 'Y')) END)
	+
	(SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN 
	invoiceOpen(d.document_id, d.c_invoicepayschedule_id)
	WHEN d.documenttable = 'C_CashLine' THEN
	cashlineavailable(d.document_id)
	ELSE paymentavailable(d.document_id) END, d.c_currency_id, p_c_currency_id, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id))
	* SIGN(d.amount)::numeric AS Debit
	INTO v_TotalAmt
	FROM V_Documents d
	WHERE d.DocStatus IN ('CO','CL', 'RE', 'VO') AND document_ID = p_c_invoice_id;
	RETURN v_TotalAmt;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION getinvoiceamt(integer, integer) OWNER TO libertya;

--20131223-2305 Modificaciones por errores en el bloque PCA de la vista V_DailySales
DROP VIEW v_dailysales;
DROP VIEW c_posjournalpayments_v;

CREATE OR REPLACE VIEW c_posjournalpayments_v AS 
(( SELECT al.c_allocationhdr_id, al.c_allocationline_id, al.ad_client_id, al.ad_org_id, al.isactive, al.created, al.createdby, al.updated, al.updatedby, al.c_invoice_id, al.c_payment_id, al.c_cashline_id, al.c_invoice_credit_id, 
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
        END AS dateacct, i.documentno AS invoice_documentno, i.grandtotal AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, dt.isfiscaldocument, dt.isfiscal, ic.fiscalalreadyprinted, date_trunc('day'::text, ah.datetrx) AS allocationdateacct
   FROM c_allocationline al
   JOIN c_allocationhdr ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id
   LEFT JOIN c_invoice i ON al.c_invoice_id = i.c_invoice_id
   LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
   LEFT JOIN c_cashline cl ON al.c_cashline_id = cl.c_cashline_id
   LEFT JOIN c_cash c ON cl.c_cash_id = c.c_cash_id
   LEFT JOIN c_invoice ic ON al.c_invoice_credit_id = ic.c_invoice_id
   LEFT JOIN c_doctype dt ON dt.c_doctype_id = ic.c_doctypetarget_id
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
 SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, NULL::unknown AS c_invoice_id, NULL::unknown AS c_payment_id, cl.c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CA'::character varying(2) AS tendertype, NULL::character varying(30) AS documentno, cl.description, (((c.name::text || '_#'::text) || cl.line::text))::character varying(255) AS info, cl.amount, cl.c_cash_id, cl.line, NULL::unknown AS c_doctype_id, NULL::character varying(20) AS checkno, NULL::character varying(255) AS a_bank, NULL::character varying(20) AS transferno, NULL::character(1) AS creditcardtype, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS m_entidadfinanciera_id, NULL::character varying(30) AS couponnumber, date_trunc('day'::text, c.statementdate) AS allocationdate, cl.docstatus, c.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, NULL::unknown AS entidadfinanciera_value, NULL::unknown AS entidadfinanciera_name, NULL::unknown AS bp_entidadfinanciera_value, NULL::unknown AS bp_entidadfinanciera_name, NULL::unknown AS cupon, NULL::unknown AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted, date_trunc('day'::text, c.dateacct) AS allocationdate
   FROM c_cashline cl
   JOIN c_cash c ON c.c_cash_id = cl.c_cash_id
  WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
      FROM c_allocationline al
     WHERE al.c_cashline_id = cl.c_cashline_id)))
UNION ALL 
( SELECT NULL::unknown AS c_allocationhdr_id, NULL::unknown AS c_allocationline_id, p.ad_client_id, p.ad_org_id, p.isactive, p.created, p.createdby, p.updated, p.updatedby, NULL::unknown AS c_invoice_id, p.c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, p.tendertype::character varying(2) AS tendertype, p.documentno, p.description, (((p.documentno::text || '_'::text) || to_char(p.datetrx, 'DD/MM/YYYY'::text)))::character varying(255) AS info, p.payamt AS amount, NULL::unknown AS c_cash_id, NULL::numeric(18,0) AS line, NULL::unknown AS c_doctype_id, p.checkno, p.a_bank, p.checkno AS transferno, p.creditcardtype, p.m_entidadfinancieraplan_id, ep.m_entidadfinanciera_id, p.couponnumber, date_trunc('day'::text, p.datetrx) AS allocationdate, p.docstatus, p.dateacct::date AS dateacct, NULL::unknown AS invoice_documentno, NULL::unknown AS invoice_grandtotal, ef.value AS entidadfinanciera_value, ef.name AS entidadfinanciera_name, bp.value AS bp_entidadfinanciera_value, bp.name AS bp_entidadfinanciera_name, p.couponnumber AS cupon, p.creditcardnumber AS creditcard, NULL::unknown AS isfiscaldocument, NULL::unknown AS isfiscal, NULL::unknown AS fiscalalreadyprinted, date_trunc('day'::text, p.dateacct) AS allocationdate
   FROM c_payment p
   LEFT JOIN m_entidadfinancieraplan ep ON p.m_entidadfinancieraplan_id = ep.m_entidadfinancieraplan_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = ep.m_entidadfinanciera_id
   LEFT JOIN c_bpartner bp ON bp.c_bpartner_id = ef.c_bpartner_id
  WHERE NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_payment_id = p.c_payment_id))
  ORDER BY p.tendertype::character varying(2), p.documentno);

ALTER TABLE c_posjournalpayments_v OWNER TO libertya;

CREATE OR REPLACE VIEW v_dailysales AS 
((((( SELECT 'P' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
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
        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM c_posjournalpayments_v pjp
   JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL) AND (pjp.c_invoice_credit_id IS NULL OR pjp.c_invoice_credit_id IS NOT NULL AND (cc.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])))
UNION ALL 
 SELECT 'CAI' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 'CC' AS tendertype, i.documentno, i.description, NULL::unknown AS info, (i.grandtotal - COALESCE(cobros.amount, 0::numeric))::numeric(20,2) AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, NULL::unknown AS c_pospaymentmedium_id, NULL::unknown AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
  WHERE date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) AND hdr.isactive = 'Y'::bpchar OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND hdr.isactive = 'N'::bpchar)) c
  GROUP BY c.c_invoice_id) cobros ON cobros.c_invoice_id = i.c_invoice_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE (cobros.amount IS NULL OR i.grandtotal <> cobros.amount) AND i.initialcurrentaccountamt > 0::numeric AND (dt.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])))
UNION ALL 
 SELECT 'I' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, dt.docbasetype AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, dt.c_doctype_id AS c_pospaymentmedium_id, dt.name AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
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
        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM c_posjournalpayments_v pjp
   JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE date_trunc('day'::text, i.dateacct) <> date_trunc('day'::text, pjp.allocationdateacct::timestamp with time zone) AND i.initialcurrentaccountamt > 0::numeric AND hdr.isactive = 'Y'::bpchar AND ((dtc.docbasetype <> ALL (ARRAY['ARC'::bpchar, 'APC'::bpchar])) OR (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL))
UNION ALL 
 SELECT 'NCC' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
        CASE
            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
            ELSE i.paymentrule
        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
        CASE
            WHEN i.paymentrule::text = 'P'::text THEN NULL::integer
            ELSE ( SELECT ad_ref_list.ad_ref_list_id
               FROM ad_ref_list
              WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
             LIMIT 1)
        END AS c_pospaymentmedium_id, 
        CASE
            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
            WHEN i.paymentrule::text = 'P'::text THEN NULL::character varying
            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
            ELSE i.paymentrule
        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE dt.docbasetype = 'ARC'::bpchar AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id)))
UNION ALL 
 SELECT 'PA' AS trxtype, pjp.ad_client_id, pjp.ad_org_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN pjp.c_invoice_credit_id
            ELSE i.c_invoice_id
        END AS c_invoice_id, pjp.allocationdate AS datetrx, pjp.c_payment_id, pjp.c_cashline_id, 
        CASE
            WHEN (dtc.docbasetype = ANY (ARRAY['ARC'::bpchar, 'APC'::bpchar])) AND pjp.c_invoice_credit_id IS NOT NULL THEN i.c_invoice_id
            ELSE pjp.c_invoice_credit_id
        END AS c_invoice_credit_id, pjp.tendertype, pjp.documentno, pjp.description, pjp.info, pjp.amount * (-1)::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, 
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
        END AS pospaymentmediumname, pjp.m_entidadfinanciera_id, ef.name AS entidadfinancieraname, pjp.m_entidadfinancieraplan_id, efp.name AS planname, pjp.docstatus, i.issotrx, pjp.dateacct, i.dateacct::date AS invoicedateacct, COALESCE(pjh.c_posjournal_id, pj.c_posjournal_id) AS c_posjournal_id, COALESCE(pjh.ad_user_id, pj.ad_user_id) AS ad_user_id, COALESCE(pjh.c_pos_id, pj.c_pos_id) AS c_pos_id, dtc.isfiscal, i.fiscalalreadyprinted
   FROM c_posjournalpayments_v pjp
   JOIN c_invoice i ON i.c_invoice_id = pjp.c_invoice_id
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dtc ON i.c_doctypetarget_id = dtc.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   JOIN c_allocationhdr hdr ON hdr.c_allocationhdr_id = pjp.c_allocationhdr_id
   LEFT JOIN c_posjournal pjh ON pjh.c_posjournal_id = hdr.c_posjournal_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
   LEFT JOIN c_payment p ON p.c_payment_id = pjp.c_payment_id
   LEFT JOIN c_pospaymentmedium ppm ON ppm.c_pospaymentmedium_id = p.c_pospaymentmedium_id
   LEFT JOIN c_cashline c ON c.c_cashline_id = pjp.c_cashline_id
   LEFT JOIN c_pospaymentmedium ppmc ON ppmc.c_pospaymentmedium_id = c.c_pospaymentmedium_id
   LEFT JOIN m_entidadfinanciera ef ON ef.m_entidadfinanciera_id = pjp.m_entidadfinanciera_id
   LEFT JOIN m_entidadfinancieraplan efp ON efp.m_entidadfinancieraplan_id = pjp.m_entidadfinancieraplan_id
   LEFT JOIN c_invoice cc ON cc.c_invoice_id = pjp.c_invoice_credit_id
   LEFT JOIN c_doctype dt ON cc.c_doctypetarget_id = dt.c_doctype_id
  WHERE (date_trunc('day'::text, i.dateacct) = date_trunc('day'::text, pjp.dateacct::timestamp with time zone) OR i.initialcurrentaccountamt = 0::numeric) AND hdr.isactive = 'N'::bpchar)
UNION ALL 
 SELECT 'ND' AS trxtype, i.ad_client_id, i.ad_org_id, i.c_invoice_id, date_trunc('day'::text, i.dateinvoiced) AS datetrx, NULL::unknown AS c_payment_id, NULL::unknown AS c_cashline_id, NULL::unknown AS c_invoice_credit_id, 
        CASE
            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
            ELSE i.paymentrule
        END AS tendertype, i.documentno, i.description, NULL::unknown AS info, i.grandtotal * dt.signo_issotrx::numeric AS amount, bp.c_bpartner_id, bp.name, bp.c_bp_group_id, bpg.name AS groupname, bp.c_categoria_iva_id, ci.name AS categorianame, ( SELECT ad_ref_list.ad_ref_list_id
           FROM ad_ref_list
          WHERE ad_ref_list.ad_reference_id = 195 AND ad_ref_list.value::text = i.paymentrule::text
         LIMIT 1) AS c_pospaymentmedium_id, 
        CASE
            WHEN i.paymentrule::text = 'T'::text OR i.paymentrule::text = 'Tr'::text THEN 'A'::character varying
            WHEN i.paymentrule::text = 'B'::text THEN 'CA'::character varying
            WHEN i.paymentrule::text = 'K'::text THEN 'C'::character varying
            WHEN i.paymentrule::text = 'P'::text THEN 'CC'::character varying
            WHEN i.paymentrule::text = 'S'::text THEN 'K'::character varying
            ELSE i.paymentrule
        END AS pospaymentmediumname, NULL::unknown AS m_entidadfinanciera_id, NULL::unknown AS entidadfinancieraname, NULL::unknown AS m_entidadfinancieraplan_id, NULL::unknown AS planname, i.docstatus, i.issotrx, i.dateacct::date AS dateacct, i.dateacct::date AS invoicedateacct, pj.c_posjournal_id, pj.ad_user_id, pj.c_pos_id, dt.isfiscal, i.fiscalalreadyprinted
   FROM c_invoice i
   LEFT JOIN c_posjournal pj ON pj.c_posjournal_id = i.c_posjournal_id
   JOIN c_doctype dt ON i.c_doctypetarget_id = dt.c_doctype_id
   JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id
   JOIN c_bp_group bpg ON bpg.c_bp_group_id = bp.c_bp_group_id
   LEFT JOIN c_categoria_iva ci ON ci.c_categoria_iva_id = bp.c_categoria_iva_id
  WHERE "position"(dt.doctypekey::text, 'CDN'::text) = 1 AND ((i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar])) OR (i.docstatus = ANY (ARRAY['VO'::bpchar, 'RE'::bpchar])) AND (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_credit_id = i.c_invoice_id))) AND NOT (EXISTS ( SELECT al.c_allocationline_id
   FROM c_allocationline al
  WHERE al.c_invoice_id = i.c_invoice_id));

ALTER TABLE v_dailysales OWNER TO libertya;

-- Desactivar por el momento el dashboard de actividades, dado que resta completar algunas funcionalidades
UPDATE pa_dashboardcontent SET isactive = 'N' WHERE name = 'Activities';

--20140107-1300 Nueva columna para agregar leyendas al pie del ticket fiscal
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','fiscalprintingfooterlegends', 'character varying(500)'));

--20140116-1530 Nueva columna para permitir o no la copia de pedidos a partir de presupuestos vencidos
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','allowproposaldue', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20140121-12:49 Funcion para el calculo de Costo_Fifo
CREATE OR REPLACE FUNCTION cost_fifo(p_productID integer, p_date timestamp)
  RETURNS numeric AS
$BODY$

DECLARE
	v_qtyOut NUMERIC;
	v_qtyIn NUMERIC;
	v_retValue NUMERIC;
	r RECORD;
BEGIN
	IF (p_productID IS NULL OR p_productID = 0) THEN
		RETURN NULL;
	END IF;

	v_retValue := 0;

	-- Determinar cantidad vendida
	SELECT INTO v_qtyOut COALESCE(sum(il.qtyEntered),0)
	FROM C_Order o 
	INNER JOIN C_OrderLine ol ON (o.C_Order_ID = ol.C_Order_ID) 
	INNER JOIN M_InOut i ON (o.C_Order_ID = i.C_Order_ID) 
	INNER JOIN M_InOutLine il ON (il.M_InOut_ID = i.M_InOut_ID) 
	WHERE (il.qtyentered > 0) 
	AND (i.docstatus IN ('CL', 'CO', 'VO', 'RE')) 
	AND (i.Issotrx = 'Y') 
	AND (i.movementdate <= p_date)
	AND (ol.M_Product_ID=p_productID);

	-- Sumatoria de cantidad comprada hasta alcanzar las vendidas
	v_qtyIn := 0;
	FOR r IN	
		SELECT il.qtyEntered, ol.priceActual, o.c_currency_id, o.dateacct, o.ad_client_id, o.ad_org_id
		FROM C_Order o 
		INNER JOIN C_OrderLine ol ON (o.C_Order_ID = ol.C_Order_ID) 
		INNER JOIN M_InOut i ON (o.C_Order_ID = i.C_Order_ID) 
		INNER JOIN M_InOutLine il ON (il.M_InOut_ID = i.M_InOut_ID) 
		WHERE (il.qtyentered > 0) 
		AND (i.docstatus IN ('CL', 'CO', 'VO', 'RE')) 
		AND (i.Issotrx = 'N') 
		AND (ol.M_Product_ID=p_productID)
		AND (i.movementdate <= p_date)
		ORDER BY il.created ASC
	LOOP
		-- Ir sumando las cantidades vendidas
		v_retValue := r.priceActual;
		v_qtyIn := v_qtyIn + r.qtyEntered;
		-- Al alcanzar la cantidad vendida, devolver el valor del articulo. TODO: deshardcode 118
		IF v_qtyIn = 0 OR v_qtyIn > v_qtyOut THEN
			 SELECT into v_retValue currencyconvert(v_retValue, r.c_currency_id, 118, r.dateacct::timestamp with time zone, NULL::integer, r.ad_client_id, r.ad_org_id );
			 RETURN v_retValue;
		END IF;
	END LOOP;

	
	RETURN	v_retValue;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION cost_fifo(integer, timestamp)
  OWNER TO libertya;

--20140121-1600 Nueva columna para gestionar reservados de pedidos y stock, por lo pronto para tipos de documento con base Remito de Salida y signo positivo (1), por ejemplo Devoluciones de Clientes. 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','reservestockmanagment', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20140122-1352 Nueva columna indicar si el remito debe utilizar el mismo almacen que el pedido 
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','useOrderWarehouse', 'character(1) NOT NULL DEFAULT ''Y''::bpchar'));

--20140204-1045 Nueva columna para registrar la última factura impresa por el controlador fiscal
update ad_system set dummy = (SELECT addcolumnifnotexists('C_DocType','c_invoice_id', 'integer'));
ALTER TABLE c_doctype ADD CONSTRAINT lastfiscalprintedinvoice FOREIGN KEY (c_invoice_id)
      REFERENCES c_invoice (c_invoice_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
 --20140207-0955 Nueva columna para registrar tipo del documento    
 ALTER TABLE i_gljournal ADD COLUMN doctypekey character varying(40);
 
 --20140207-0955 Nueva columna para la fecha de vencimiento    
 ALTER TABLE t_cuentacorriente ADD COLUMN duedate timestamp without time zone;
 
 -- 20140113 Agrego campo debe y haber a tabla i_bankstatement
ALTER TABLE i_bankstatement ADD COLUMN debe numeric(20,2) DEFAULT 0;
ALTER TABLE i_bankstatement ADD COLUMN haber numeric(20,2) DEFAULT 0;

--20140116 Agrego campo chargevalue a tabla i_bankstatement
ALTER TABLE i_bankstatement ADD COLUMN chargevalue character varying(60);

--20140210-1115 Incorporación de campo descripción para, en principio, registrar las transacciones creadas y detectar posibles problemas
update ad_system set dummy = (SELECT addcolumnifnotexists('M_Transaction','description', 'character varying(255)'));

--20140211-1255 Cambio en la vista rv_openitem para que sea compatible con las distintas versiones de PostgreSQL
CREATE OR REPLACE VIEW libertya.rv_openitem AS 
         SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, p.netdays, i.dateinvoiced + ((p.netdays::text || ' days'::text)::interval) AS duedate, libertya.paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) AS daysdue, i.dateinvoiced + ((p.discountdays::text || ' days'::text)::interval) AS discountdate, round(i.grandtotal * p.discount / 100::numeric, 2) AS discountamt, i.grandtotal, libertya.invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, libertya.invoiceopen(i.c_invoice_id, 0) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, NULL::unknown AS c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
           FROM libertya.rv_c_invoice i
      JOIN libertya.c_paymentterm p ON i.c_paymentterm_id = p.c_paymentterm_id
     WHERE libertya.invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid <> 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar
UNION 
         SELECT i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id, i.c_order_id, i.c_bpartner_id, i.issotrx, i.dateinvoiced, libertya.to_days(ips.duedate) - libertya.to_days(i.dateinvoiced) AS netdays, ips.duedate, libertya.to_days(now()::timestamp without time zone) - libertya.to_days(ips.duedate) AS daysdue, ips.discountdate, ips.discountamt, ips.dueamt AS grandtotal, libertya.invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, libertya.invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, ips.c_invoicepayschedule_id, i.c_paymentterm_id, i.c_doctypetarget_id, i.docstatus
           FROM libertya.rv_c_invoice i
      JOIN libertya.c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id
     WHERE libertya.invoiceopen(i.c_invoice_id, 0) <> 0::numeric AND i.ispayschedulevalid = 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar AND ips.isvalid = 'Y'::bpchar;

ALTER TABLE libertya.rv_openitem
  OWNER TO libertya;

-- 20140214-1525 Permitir opcion RememberMe en la ventana de login
UPDATE ad_sysconfig SET value = 'P' where name = 'ZK_LOGIN_ALLOW_REMEMBER_ME';

-- 20140219-1235 Correccion en funcion para el calculo de facturas anuladas (signo negativo) y con c_invoicepayschedule 
CREATE OR REPLACE FUNCTION invoiceopen(p_c_invoice_id integer, p_c_invoicepayschedule_id integer)
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
 * Title:	Calculate Open Item Amount in Invoice Currency
 * Description:
 *	Add up total amount open for C_Invoice_ID if no split payment.
 *  Grand Total minus Sum of Allocations in Invoice Currency
 *
 *  For Split Payments:
 *  Allocate Payments starting from first schedule.
 *  Cannot be used for IsPaid as mutating
 *
 * Test:
 * 	SELECT C_InvoicePaySchedule_ID, DueAmt FROM C_InvoicePaySchedule WHERE C_Invoice_ID=109 ORDER BY DueDate;
 * 	SELECT invoiceOpen (109, null) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 11) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 102) FROM AD_System;
 * 	SELECT invoiceOpen (109, 103) FROM AD_System;
 ***
 * Pasado a Libertya a partir de Adempiere 360LTS
 * - ids son de tipo integer, no numeric
 * - TODO : tema de las zonas en los timestamp
 * - Excepciones en SELECT INTO requieren modificador STRICT bajo PostGreSQL o usar
 * NOT FOUND
 * - Por ahora, el "ignore rounding" se hace como en libertya (-0.01,0.01),
 * en vez de usar la precisión de la moneda
 * - Se toma el tipo de conversion de la factura, auqneu esto es dudosamente correcto
 * ya que otras funciones , en particular currencyBase nunca tiene en cuenta
 * este valor
 * - Como en Libertya se tiene en cuenta tambien C_Invoice_Credit_ID para calcular
 * la cantidad alocada a una factura (aunque esto es medio dudoso....)
 * - No se soporta la fecha como 3er parametro (en realidad, tampoco se esta
 * usando actualmente, y se deberia poder resolver de otra manera)
 * - Libertya parece tener un bug al filtrar por C_InvoicePaySchedule_ID al calcular
 * el granTotal (el granTotal SIEMPRE es el total de la factura, tomada directamente
 * de C_Invoice.GranTotal o a partir de la suma de los DueAmt en C_InvoicePaySchedule);
 * se usa la sentencia como esta en Adempeire (esto es, solo se filtra por C_Invoice_ID)
 * - Nuevo enfoque: NO se usa ni la vista C_Invoice_V ni multiplicadores
 * se asume todo positivo...
 * - El resultado SIEMPRE deberia ser positivo y en el intervalo [0..GrandTotal]
 * - 03 julio: se pasa a usar getAllocatedAmt para hacer esta funcion consistente
 * con invoicePaid
 * - 03 julio: se pasa de usar STRICT a NOT FOUND; es mas eficiente
 ************************************************************************/
DECLARE
	v_Currency_ID		INTEGER;
	v_TotalOpenAmt  	NUMERIC := 0;
	v_PaidAmt  	        NUMERIC := 0;
	v_Remaining	        NUMERIC := 0;
   	v_Precision            	NUMERIC := 0;
   	v_Min            	NUMERIC := 0.01; -- en Adempiere inferido desde Currency
   	s			RECORD;
	v_ConversionType_ID INTEGER; -- NO en Adempiere

BEGIN
	--	Get Currency, GranTotal, MulitplerAP , MutiplierCM, ConversionType, and precision 
	SELECT	C_Currency_ID, GrandTotal, C_ConversionType_ID,
	(SELECT StdPrecision FROM C_Currency C WHERE C.C_Currency_ID = I.C_Currency_ID)
		AS StdPrecision 
	INTO v_Currency_ID, v_TotalOpenAmt, v_ConversionType_ID,v_Precision
	FROM	C_Invoice I		--	NO se corrige por CM o SpliPayment; se usa directamente C_Inovoice y ningun multiplicador
	WHERE	I.C_Invoice_ID = p_C_Invoice_ID;

	IF NOT FOUND THEN
       	RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID;
		RETURN NULL;
	END IF;

	-- se saca lo siguiente para hacerlo tal como lo hace Libertya; igual tiene cierto sentido usar estos limites, sal vo que en Libertya
    -- se usa 2 decimales en todos los montos....	
	--SELECT 1/10^v_Precision INTO v_Min;

	
	--	Calculate Allocated Amount : SIEMPRE 1 como multiplicador
	v_PaidAmt := getAllocatedAmt(p_C_Invoice_ID,v_Currency_ID,v_ConversionType_ID,1);

    --  Do we have a Payment Schedule ?
    IF (p_C_InvoicePaySchedule_ID > 0) THEN --   if not valid = lists invoice amount
        v_Remaining := v_PaidAmt;
        FOR s IN 
        	SELECT  C_InvoicePaySchedule_ID, DueAmt, sign(DueAmt) as signo
	        FROM    C_InvoicePaySchedule
		WHERE	C_Invoice_ID = p_C_Invoice_ID
	        AND   IsValid='Y'
        	ORDER BY DueDate
        LOOP
            IF (s.C_InvoicePaySchedule_ID = p_C_InvoicePaySchedule_ID) THEN
                v_TotalOpenAmt := abs(s.DueAmt) - v_Remaining;
                IF (v_TotalOpenAmt < 0) THEN
                    v_TotalOpenAmt := 0; -- Pagado totalmente
                END IF;
                v_TotalOpenAmt := s.signo * v_TotalOpenAmt;
		EXIT; -- se sale del loop, ya que ya se encontro
            ELSE -- calculate amount, which can be allocated to next schedule
                v_Remaining := v_Remaining - abs(s.DueAmt);
                IF (v_Remaining < 0) THEN
                    v_Remaining := 0;
                END IF;
            END IF;
        END LOOP;
    ELSE
        v_TotalOpenAmt := v_TotalOpenAmt - v_PaidAmt;
    END IF;
--  RAISE NOTICE ''== Total='' || v_TotalOpenAmt;

	--	Ignore Rounding
	IF (v_TotalOpenAmt >= -v_Min AND v_TotalOpenAmt <= v_Min) THEN
		v_TotalOpenAmt := 0;
	END IF;

	--	Round to currency precision
	v_TotalOpenAmt := ROUND(COALESCE(v_TotalOpenAmt,0), v_Precision);
	RETURN	v_TotalOpenAmt;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer)
  OWNER TO libertya;
  
-- 20140221-1715 Ampliacion de invoiceopen para soporte multimoneda
CREATE OR REPLACE FUNCTION invoiceopen(p_c_invoice_id integer, p_c_invoicepayschedule_id integer, p_c_currency_id integer, p_c_conversiontype_id integer)
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
 * Title:	Calculate Open Item Amount in Invoice Currency
 * Description:
 *	Add up total amount open for C_Invoice_ID if no split payment.
 *  Grand Total minus Sum of Allocations in Invoice Currency
 *
 *  For Split Payments:
 *  Allocate Payments starting from first schedule.
 *  Cannot be used for IsPaid as mutating
 *
 * Test:
 * 	SELECT C_InvoicePaySchedule_ID, DueAmt FROM C_InvoicePaySchedule WHERE C_Invoice_ID=109 ORDER BY DueDate;
 * 	SELECT invoiceOpen (109, null) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 11) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 102) FROM AD_System;
 * 	SELECT invoiceOpen (109, 103) FROM AD_System;
 ***
 * Pasado a Libertya a partir de Adempiere 360LTS
 * - ids son de tipo integer, no numeric
 * - TODO : tema de las zonas en los timestamp
 * - Excepciones en SELECT INTO requieren modificador STRICT bajo PostGreSQL o usar
 * NOT FOUND
 * - Por ahora, el "ignore rounding" se hace como en libertya (-0.01,0.01),
 * en vez de usar la precisión de la moneda
 * - Se toma el tipo de conversion de la factura, auqneu esto es dudosamente correcto
 * ya que otras funciones , en particular currencyBase nunca tiene en cuenta
 * este valor
 * - Como en Libertya se tiene en cuenta tambien C_Invoice_Credit_ID para calcular
 * la cantidad alocada a una factura (aunque esto es medio dudoso....)
 * - No se soporta la fecha como 3er parametro (en realidad, tampoco se esta
 * usando actualmente, y se deberia poder resolver de otra manera)
 * - Libertya parece tener un bug al filtrar por C_InvoicePaySchedule_ID al calcular
 * el granTotal (el granTotal SIEMPRE es el total de la factura, tomada directamente
 * de C_Invoice.GranTotal o a partir de la suma de los DueAmt en C_InvoicePaySchedule);
 * se usa la sentencia como esta en Adempeire (esto es, solo se filtra por C_Invoice_ID)
 * - Nuevo enfoque: NO se usa ni la vista C_Invoice_V ni multiplicadores
 * se asume todo positivo...
 * - El resultado SIEMPRE deberia ser positivo y en el intervalo [0..GrandTotal]
 * - 03 julio: se pasa a usar getAllocatedAmt para hacer esta funcion consistente
 * con invoicePaid
 * - 03 julio: se pasa de usar STRICT a NOT FOUND; es mas eficiente
 ************************************************************************/
DECLARE
	v_Currency_ID		INTEGER := p_c_currency_id;
	v_TotalOpenAmt  	NUMERIC := 0;
	v_PaidAmt  	        NUMERIC := 0;
	v_Remaining	        NUMERIC := 0;
   	v_Precision            	NUMERIC := 0;
   	v_Min            	NUMERIC := 0.01; -- en Adempiere inferido desde Currency
   	s			RECORD;
	v_ConversionType_ID INTEGER := p_c_conversiontype_id; -- NO en Adempiere
	v_Date timestamp with time zone := ('now'::text)::timestamp(6);               
BEGIN
	--	Get GranTotal, And Precision 
	SELECT	currencyConvert(GrandTotal, I.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, I.AD_Client_ID, I.AD_Org_ID) as GrandTotal,
	(SELECT StdPrecision FROM C_Currency C WHERE C.C_Currency_ID = I.C_Currency_ID)
		AS StdPrecision 
	INTO v_TotalOpenAmt, v_Precision
	FROM	C_Invoice I		--	NO se corrige por CM o SpliPayment; se usa directamente C_Inovoice y ningun multiplicador
	WHERE	I.C_Invoice_ID = p_C_Invoice_ID;

	IF NOT FOUND THEN
       	RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID;
		RETURN NULL;
	END IF;

	-- se saca lo siguiente para hacerlo tal como lo hace Libertya; igual tiene cierto sentido usar estos limites, sal vo que en Libertya
    -- se usa 2 decimales en todos los montos....	
	--SELECT 1/10^v_Precision INTO v_Min;

	
	--	Calculate Allocated Amount : SIEMPRE 1 como multiplicador
	v_PaidAmt := getAllocatedAmt(p_C_Invoice_ID,v_Currency_ID,v_ConversionType_ID,1);

    --  Do we have a Payment Schedule ?
    IF (p_C_InvoicePaySchedule_ID > 0) THEN --   if not valid = lists invoice amount
        v_Remaining := v_PaidAmt;
        FOR s IN 
        	SELECT  ips.C_InvoicePaySchedule_ID, currencyConvert(ips.DueAmt, i.c_currency_id, v_Currency_ID, v_Date, v_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID) as DueAmt
	        FROM    C_InvoicePaySchedule ips
	        INNER JOIN C_Invoice i on (ips.C_Invoice_ID = i.C_Invoice_ID)
		WHERE	ips.C_Invoice_ID = p_C_Invoice_ID
	        AND   ips.IsValid='Y'
        	ORDER BY ips.DueDate
        LOOP
            IF (s.C_InvoicePaySchedule_ID = p_C_InvoicePaySchedule_ID) THEN
                v_TotalOpenAmt := s.DueAmt - v_Remaining;
                IF (v_TotalOpenAmt < 0) THEN
                    v_TotalOpenAmt := 0; -- Pagado totalmente
                END IF;
				EXIT; -- se sale del loop, ya que ya se encontro
            ELSE -- calculate amount, which can be allocated to next schedule
                v_Remaining := v_Remaining - s.DueAmt;
                IF (v_Remaining < 0) THEN
                    v_Remaining := 0;
                END IF;
            END IF;
        END LOOP;
    ELSE
        v_TotalOpenAmt := v_TotalOpenAmt - v_PaidAmt;
    END IF;
--  RAISE NOTICE ''== Total='' || v_TotalOpenAmt;

	--	Ignore Rounding
	IF (v_TotalOpenAmt >= -v_Min AND v_TotalOpenAmt <= v_Min) THEN
		v_TotalOpenAmt := 0;
	END IF;

	--	Round to currency precision
	v_TotalOpenAmt := ROUND(COALESCE(v_TotalOpenAmt,0), v_Precision);
	RETURN	v_TotalOpenAmt;
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer) OWNER TO libertya;




CREATE OR REPLACE FUNCTION invoiceopen(p_c_invoice_id integer, p_c_invoicepayschedule_id integer)
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
 * Title:	Calculate Open Item Amount in Invoice Currency
 * Description:
 *	Add up total amount open for C_Invoice_ID if no split payment.
 *  Grand Total minus Sum of Allocations in Invoice Currency
 *
 *  For Split Payments:
 *  Allocate Payments starting from first schedule.
 *  Cannot be used for IsPaid as mutating
 *
 * Test:
 * 	SELECT C_InvoicePaySchedule_ID, DueAmt FROM C_InvoicePaySchedule WHERE C_Invoice_ID=109 ORDER BY DueDate;
 * 	SELECT invoiceOpen (109, null) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 11) FROM AD_System; - converted to default client currency
 * 	SELECT invoiceOpen (109, 102) FROM AD_System;
 * 	SELECT invoiceOpen (109, 103) FROM AD_System;
 ***
 * Pasado a Libertya a partir de Adempiere 360LTS
 * - ids son de tipo integer, no numeric
 * - TODO : tema de las zonas en los timestamp
 * - Excepciones en SELECT INTO requieren modificador STRICT bajo PostGreSQL o usar
 * NOT FOUND
 * - Por ahora, el "ignore rounding" se hace como en libertya (-0.01,0.01),
 * en vez de usar la precisión de la moneda
 * - Se toma el tipo de conversion de la factura, auqneu esto es dudosamente correcto
 * ya que otras funciones , en particular currencyBase nunca tiene en cuenta
 * este valor
 * - Como en Libertya se tiene en cuenta tambien C_Invoice_Credit_ID para calcular
 * la cantidad alocada a una factura (aunque esto es medio dudoso....)
 * - No se soporta la fecha como 3er parametro (en realidad, tampoco se esta
 * usando actualmente, y se deberia poder resolver de otra manera)
 * - Libertya parece tener un bug al filtrar por C_InvoicePaySchedule_ID al calcular
 * el granTotal (el granTotal SIEMPRE es el total de la factura, tomada directamente
 * de C_Invoice.GranTotal o a partir de la suma de los DueAmt en C_InvoicePaySchedule);
 * se usa la sentencia como esta en Adempeire (esto es, solo se filtra por C_Invoice_ID)
 * - Nuevo enfoque: NO se usa ni la vista C_Invoice_V ni multiplicadores
 * se asume todo positivo...
 * - El resultado SIEMPRE deberia ser positivo y en el intervalo [0..GrandTotal]
 * - 03 julio: se pasa a usar getAllocatedAmt para hacer esta funcion consistente
 * con invoicePaid
 * - 03 julio: se pasa de usar STRICT a NOT FOUND; es mas eficiente
 ************************************************************************/
DECLARE
	v_Currency_ID	    INTEGER;
	v_ConversionType_ID INTEGER; -- NO en Adempiere

BEGIN
	--	Get Currency, ConversionType
	SELECT	C_Currency_ID, C_ConversionType_ID
		INTO v_Currency_ID, v_ConversionType_ID
	FROM	C_Invoice I		--	NO se corrige por CM o SpliPayment; se usa directamente C_Inovoice y ningun multiplicador
	WHERE	I.C_Invoice_ID = p_C_Invoice_ID;

	IF NOT FOUND THEN
       	RAISE NOTICE 'Invoice no econtrada - %', p_C_Invoice_ID;
		RETURN NULL;
	END IF;

	RETURN	invoiceOpen(p_c_invoice_id, p_c_invoicepayschedule_id, v_Currency_ID, v_ConversionType_ID);
END;

$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION invoiceopen(integer, integer) OWNER TO libertya;
 
-- Versionado de BBDD
UPDATE ad_system SET version = '24-02-2014' WHERE ad_system_id = 0;