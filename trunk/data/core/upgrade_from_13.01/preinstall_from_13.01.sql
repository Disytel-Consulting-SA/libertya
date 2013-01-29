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


