package org.openXpertya.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.ProcessParameter;
import org.openXpertya.model.MComponent;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.MField;
import org.openXpertya.model.MFieldVO;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.POInfo;
import org.openXpertya.model.X_AD_Plugin;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.plugin.install.PluginXMLUpdater;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.replication.ReplicationCache;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
import org.openXpertya.utils.JarHelper;


public class VPluginInstallerUtils  {
	
	/** Componente que está siendo instalado */
	protected static MComponent component = null;

	/** Versión de componente que está siendo instalada */
	protected static MComponentVersion componentVersion = null;

	/** Plugin que se esta registrando o actualizando */
	protected static X_AD_Plugin plugin = null;

	/** Numero de errores en instalacion */
	protected static int installErrorsLength = 0;
	
	/** Numero de errores en post-instalacion */
	protected static int postInstallErrorsLength = 0;

	/** Trx a usar en la instalacion */
	protected static String m_trx;
	
	/**
	 * Realiza todos los pasos de la instalación.  Pre-actual-Post.  
	 * @param jarURL ubicacion del Jar
	 * @param m_ctx Entorno
	 * @param m_trx nombre de la transaccion
	 * @param m_component_props propiedades del Jar
	 * @param owner gestor Swing o null si es invocado desde terminal
	 * @return resultado del ProcessInfo de PostInstall
	 * @throws Exception en caso de error
	 */
	public static ProcessInfo performInstall(String jarURL, Properties m_ctx, Properties m_component_props, VPluginInstaller owner, PluginInstaller consoleOwner) throws Exception 
	{
		/* Iniciar la transacción y setear componente global */
		m_trx = Trx.createTrxName();
		Trx.getTrx(m_trx).start();
		PluginUtils.startInstalation(m_trx);
		installErrorsLength = 0;
		postInstallErrorsLength = 0;
		
		/* Instalacion por etapas: pre - install - post */
		PluginUtils.appendStatus(" === Instalando Componente y Version. Registrando Plugin === ");
		createComponentAndVersion(m_ctx, m_component_props);

		/* Preinstalacion - Sentencias SQL */
		PluginUtils.appendStatus(" === Ejecutando sentencias de preinstalacion === ");
		doPreInstall(m_ctx, jarURL, PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_PREINSTALL);
		
		/* Comprobar secuencia por modificaciones a nivel SQL */
		PluginUtils.appendStatus(" === Comprobando secuencias de nuevos componentes - preinstalacion === ");
		sequenceCheck(m_ctx);
		
		/* Instalacion - Carga de metadatos */
		PluginUtils.appendStatus(" === Insertando metadatos de instalación === ");
		doInstall(m_ctx, jarURL, PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_INSTALL);
		// Almacenar la longitud del log de errores luego de la instalacion
		if (PluginUtils.getErrorStatus() != null && PluginUtils.getErrorStatus().length()>0)
			installErrorsLength = PluginUtils.getErrorStatus().length();
		
		/* Comprobar secuencia por modificaciones relacionadas en metadatos */
		PluginUtils.appendStatus(" === Comprobando secuencias de nuevos componentes - metadatos === ");
		sequenceCheck(m_ctx);

		/* Información adicional del plugin */
		PluginUtils.appendStatus(" === Registrando informacion adicional del componente === ");
		setAditionalValues(m_ctx, m_component_props);
		
		/* PostInstalacion - Invocar proceso genérico o ad-hoc */
		PluginUtils.appendStatus(" === Disparando proceso de postinstalación === ");
		return doPostInstall(m_ctx, jarURL, PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_POSTINSTALL, m_component_props, owner, consoleOwner);
	}
	

	
	/**
	 * Finalización del proceso de instalación de un plugin (con existencia de postInstall)
	 * Debe manejar las excepciones correspondientes
	 */
	public static void performInstallFinalize(ProcessInfo pi, VPluginInstaller invoker, Properties m_component_props) throws Exception
	{
		// Hubo errores en instalacion o en postinstalacion?
		boolean errorsOnInstall = false;
		boolean errorsOnPostInstall = false; 
		boolean errors = false;
		
		/* Indicar que no hubo post instalacion */
		if (pi == null)
			PluginUtils.appendStatus("Sin post instalacion");
		
		/* Contemplar error al ejecutar el proceso de postInstall */
		if (pi != null && pi.isError()) {
			errorsOnPostInstall = true;
			String errStatus = " Error al ejecutar postInstall - " + pi.getSummary();
			PluginUtils.appendStatus(errStatus, true, true, true, true);
			PluginUtils.appendError(errStatus);
		}

		// Almacenar la longitud del log de errores luego de la post-instalacion
		if (PluginUtils.getErrorStatus() != null && PluginUtils.getErrorStatus().length()>0)
			postInstallErrorsLength = PluginUtils.getErrorStatus().length();

		// Hubo errores en instalacion o en postinstalacion?
		errorsOnInstall = installErrorsLength > 0;
		errorsOnPostInstall = errorsOnPostInstall || (postInstallErrorsLength - installErrorsLength > 0); 
		errors = errorsOnInstall || errorsOnPostInstall;
		
		/* Si hubo errores, solo commitear si corresponde */
		if (errors && !invoker.confirmCommit(errorsOnInstall, errorsOnPostInstall)) {
			invoker.handleException("Instalación cancelada.", new Exception(" Instalación cancelada debido a errores en: " + (errorsOnInstall?"INSTALL":"") + " " + (errorsOnPostInstall?"POSTINSTALL":"")));
			return;
		}
		
		/* Finalizar la transacción y resetear componente global */
		if (m_trx!=null) {
			Trx.getTrx(m_trx).commit();
			Trx.getTrx(m_trx).close();
			m_trx=null;
		}
		PluginUtils.stopInstalation();
	
		/* Informar todo OK */
		PluginUtils.appendStatus(" === Instalación finalizada " + (errors?"con errores":"") + " === ");
		writeInstallLog(m_component_props);
	}

	
	
	/**
	 * Inserta o actualiza las entradas en las tablas: AD_Plugin, AD_Component y AD_ComponentVersion
	 * 
	 * En caso de realizar copia al changelog pueden presentarse los siguientes escenarios:
	 * 		1) Copiar al changelog sobre un componente existente y mapeando los valores acordemente al componente ya existente
	 * 			En este caso hay que especificar CopyToChangelog = Y y los UIDs del Component y ComponentVersion a mapear.
	 * 			Con estos datos, los UIDs se mapearan a un nuevo valor (ej. FOO2CORE)
	 * 		2) Copiar al changelog instalando el plugin especificado en el properties del componente a instalar
	 * 			Este es un caso mas tradicional donde no se realiza mapeo alguno
	 * 			En este caso solo hay que especificar CopyToChangelog = Y
	 */
	public static void createComponentAndVersion(Properties ctx, Properties m_component_props) throws Exception
	{
		/* Entrada en AD_Component: Columna AD_ComponentObjectUID */
		String componentEntry = (String)m_component_props.get(PluginConstants.PROP_COMPONENTUID);
		/* Entrada en AD_ComponentVersion: Columna AD_ComponentObjectUID */
		String componentVersionEntry = (String)m_component_props.get(PluginConstants.PROP_COMPONENTVERSIONUID);
		
		/* Redefiniciones especiales en caso que se este instalando un plugin para copia al changelog */
		boolean copyToChangelog = false;
		/* Mapear a un componente existente un instalar sobre uno nuevo? */
		boolean mapToComponent = false;
		/* Validar que se esten especificando correctamente los valores de mapeo */
		int countCheck = 0;
		if ("Y".equals((String)m_component_props.get(PluginConstants.PROP_COPY_TO_CHANGELOG)))
		{
			/* Setear en el contexto que hay que copiar al changelog la instalacion */
			copyToChangelog = true;
			Env.setContext(ctx, PluginConstants.PROP_COPY_TO_CHANGELOG, "Y");					
			/* Si hay definido un mapeo del componente en el manifest de la instalacion, utilizar dicho mapeo 
			 * (tienen que especificarse tanto el UID del component como el UID componentversion) */ 
			if (m_component_props.get(PluginConstants.PROP_MAP_TO_COMPONENT_UID) != null) { 
				componentEntry = (String)m_component_props.get(PluginConstants.PROP_MAP_TO_COMPONENT_UID);
				countCheck++;
			}
			if (m_component_props.get(PluginConstants.PROP_MAP_TO_COMPONENTVERSION_UID) != null) {
				componentVersionEntry = (String)m_component_props.get(PluginConstants.PROP_MAP_TO_COMPONENTVERSION_UID);
				countCheck++;
			}
			/* Si countCheck = 0, no mapear a un componente => instalar uno nuevo de manera tradicional 
			 * Si countCheck = 2, mapear a un componente => utilizar la info existente del componente
			 * Si countCheck = 1, se especificó solo un dato (component o version) => ERROR */
			if (countCheck == 2) {
				mapToComponent = true;
				Env.setContext(ctx, PluginConstants.MAP_TO_COMPONENT, "Y");		
			}
			if (countCheck == 1)
				throw new Exception("Para instalar copiando al chenglog mapeando valores a un componente existente, es necesario especificar tanto el UID del Componente como el UID del ComponentVersion a utilizar");
		}
		
		/* Registros de cada tabla (recuperar el ID de ambos) */
		int componentID = DB.getSQLValue(m_trx, " SELECT AD_Component_ID FROM AD_Component WHERE AD_ComponentObjectUID = ?", componentEntry);
		int componentVersionID = DB.getSQLValue(m_trx, " SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = ?", componentVersionEntry);
		
		/* Insertar/actualizar el Component */ 
		component = new MComponent(ctx, (componentID==-1?0:componentID), m_trx);
		if (!copyToChangelog || !mapToComponent)
		{
			component.setPublicName((String)m_component_props.get(PluginConstants.PROP_PUBLICNAME));
			component.setPrefix((String)m_component_props.get(PluginConstants.PROP_PREFIX));
			component.setAuthor((String)m_component_props.get(PluginConstants.PROP_AUTHOR));
			component.setPackageName((String)m_component_props.get(PluginConstants.PROP_PACKAGENAME));
			component.setCoreLevel(Integer.parseInt((String)m_component_props.get(PluginConstants.PROP_CORELEVEL)));
			component.setAD_ComponentObjectUID(componentEntry);
			
			if (!component.save())
				throw new Exception(" - Error al intentar registrar el componente: " + component.getPublicName());
		}
		
		/* Insertar/actualizar el ComponentVersion */
		componentVersion = new MComponentVersion(ctx, (componentVersionID==-1?0:componentVersionID), m_trx);
		if (!copyToChangelog || !mapToComponent)
		{
			componentVersion.setVersion((String)m_component_props.get(PluginConstants.PROP_VERSION));
			componentVersion.setAD_ComponentObjectUID(componentVersionEntry);
			componentVersion.setAD_Component_ID(component.getAD_Component_ID());

			if (!componentVersion.save())
				throw new Exception(" - Error al intentar registrar la version: " + componentVersion.getName());
		}

		/* Registrar/actualizar el plugin */
		POInfo.clearKey(DB.getSQLValue(m_trx, " SELECT AD_Table_ID FROM AD_Table WHERE tableName = ?", "AD_Plugin"));
		int pluginID = DB.getSQLValue(m_trx, " SELECT P.AD_Plugin_ID FROM " + getGeneralPluginQuery() + " WHERE C.AD_Component_ID = ?", component.getAD_Component_ID());
		plugin = new X_AD_Plugin(ctx, (pluginID==-1?0:pluginID), m_trx); 
		if (!copyToChangelog || !mapToComponent)
		{
			plugin.setAD_ComponentVersion_ID(componentVersion.getAD_ComponentVersion_ID());
			
			if (!plugin.save())
				throw new Exception(" - Error al intentar registrar el plugin ");
		}
		
		/* Incorporacion de informacion adicional en caso de copiar al changelog */
		if (copyToChangelog)
		{
			/* Setear en el contexto: el ID local de la version del componente y el prefijo del componente que se utilizara;  */
			Env.setContext(ctx, PluginConstants.INSTALLED_COMPONENTVERSION_ID, componentVersion.getAD_ComponentVersion_ID());
			Env.setContext(ctx, PluginConstants.PROP_PREFIX, component.getPrefix());

			/* Si estamos mapeando a otro componente, guardar el prefijo original (para incluirlo como parte del UID) */
			if (mapToComponent)
			{
				Env.setContext(ctx, PluginConstants.COMPONENT_SOURCE_PREFIX, (String)m_component_props.get(PluginConstants.PROP_PREFIX));
				ReplicationCache.mappedUIDs = new HashMap<String, String>();
			}
		}
	}
	
	
	/**
	 * Incorpora información adicional a las entradas vacias de las tablas: AD_Plugin, AD_Component y AD_ComponentVersion
	 * (esto lo realiza posterior a la ejecucion de preinstall e install a fin de contar con los posibles nuevos campos) 
	 */
	public static void setAditionalValues(Properties ctx, Properties m_component_props) throws Exception
	{
		// Fecha de exportación del componente y ultimo changelog relacionado (lo vuelvo a instanciar para recuperar las nuevas columnas)
		// Primeramente limpiar de la cache poinfo la estructura actual de ad_plugin
		POInfo.clearKey(DB.getSQLValue(m_trx, " SELECT AD_Table_ID FROM AD_Table WHERE tableName = ?", "AD_Plugin"));
		plugin = new X_AD_Plugin(ctx, plugin.getAD_Plugin_ID(), m_trx);
		plugin.setComponent_Export_Date((String)m_component_props.get(PluginConstants.PROP_EXPORT_TIMESTAMP));
		if (m_component_props.get(PluginConstants.PROP_LAST_CHANGELOG) != null && !"-1".equals((String)m_component_props.get(PluginConstants.PROP_LAST_CHANGELOG)))
				plugin.setComponent_Last_Changelog((String)m_component_props.get(PluginConstants.PROP_LAST_CHANGELOG));
		if (!plugin.save())
			throw new Exception(" - Error al intentar registrar información adicional del plugin ");
	}
	
	/**
	 * Entrada principal a la ejecución del proceso de PreInstalación
	 * @throws Exception
	 */
	public static void doPreInstall(Properties ctx, String jarURL, String fileURL) throws Exception
	{
		/* Toma el archivo SQL correspondiente e impacta en la base de datos */
		ArrayList<String> sqls = JarHelper.readPreinstallSQLSentencesFromJar(jarURL, fileURL);
		int iter = 1;
		for (String sql : sqls)
			if (sql != null && sql.length() > 0) {
				PluginUtils.appendStatus("[" + (iter++) + "] Sentencias SQL de preinstalación", true, false, false, true);
				PluginXMLUpdater.executeUpdate(sql, m_trx);
		}
	}
	
	/** 
	 * Entrada principal a la ejecución del proceso de Instalación
  	 * @throws Exception
	 */
	public static void doInstall(Properties ctx, String jarURL, String fileURL) throws Exception
	{
		/* Toma el archivo XML correspondiente, genera las sentencias SQL correspondientes e impacta en la base de datos */
		String xml = JarHelper.readFromJar(jarURL, fileURL, "", null);
		if (xml != null && xml.length() > 0)
		{
			PluginXMLUpdater uploaderMetaData = new PluginXMLUpdater(xml, m_trx, false);
			uploaderMetaData.processChangeLog();
		}
	}
	
	/** 
	 * Entrada principal a la ejecución del proceso de PostInstalación
  	 * @throws Exception
	 */
	public static ProcessInfo doPostInstall(Properties ctx, String jarURL, String fileURL, Properties props, VPluginInstaller installer, PluginInstaller consoleOwner) throws Exception
	{
		/* Toma el archivo XML correspondiente, genera las sentencias SQL correspondientes e impacta en la base de datos */
		String xml = JarHelper.readFromJar(jarURL, fileURL, "", null);
		if (xml != null && xml.length() > 0)
		{
			/* Determinar el proceso a invocar */
			int postInstallProcessId = getPostInstallProcessID(ctx, props);
			
			if (postInstallProcessId <= 0)
				throw new Exception (" PostInstall process not found!");
			
			/* Insertar el parametro para el XML y la ubicación del Jar (estos dos parametros no son definidos a nivel medatados) */
	        ProcessInfo pi = new ProcessInfo( " Post Instalacion ", postInstallProcessId);
	        ProcessInfoParameter xtraParamXMLContent = new ProcessInfoParameter(PluginConstants.XML_CONTENT_PARAMETER_NAME, xml, null, null, null);
	        ProcessInfoParameter xtraParamJARLocation = new ProcessInfoParameter(PluginConstants.JAR_FILE_URL, jarURL, null, null, null);
	        pi.setParameter(ProcessInfoUtil.addToArray(pi.getParameter(), xtraParamXMLContent));
	        pi.setParameter(ProcessInfoUtil.addToArray(pi.getParameter(), xtraParamJARLocation));
	        
	        /* Si installer no es null, entonces la invocación es gestionada desde una ventana => Asincrónico a fin de requerir eventuales parámetros adicionales definidos en metadatos */
	        if (installer != null) {
		        /* Invocar la ejecución del proceso, si el mismo devuelve null es porque se cancelo en los parametros */
		        ProcessCtl worker = ProcessCtl.process(installer, installer.getM_WindowNo(), pi, Trx.getTrx(m_trx));
		        if (worker == null)
		        	throw new Exception (" Instalacion cancelada en post configuracion! ");
	        }
	        /* Si installer es null, entonces la invocacion no es gestionada desde una ventana, sino desde terminal => Sincronico (Y CON SOPORTE BASICO PARA PARAMS ADICIONALES!) */
	        else {
	        	// Incorporar eventuales parametros específicos del proceso a ejecutar
	        	PreparedStatement pstmt = ProcessParameter.GetProcessParameters(postInstallProcessId);
	        	ResultSet rs = pstmt.executeQuery();
	        	while (rs.next()) {
	        		// Recuperar parametro pasado como argumento desde la terminal 
	        		String paramName = rs.getString("Name");
	        		Object paramValue = createParamValue(consoleOwner.getAdditionalParams().get(paramName), rs.getInt("AD_Reference_ID"));
	                if (paramValue == null)
	                	continue;
	                // TODO: parameter_To, info_To? Ver ProcessParameter.saveParameters como referencia.
	        		ProcessInfoParameter aParam = new ProcessInfoParameter(paramName, paramValue, null, null, null);
	        		pi.setParameter(ProcessInfoUtil.addToArray(pi.getParameter(), aParam));
	        	}
	        	MProcess process = new MProcess(ctx, postInstallProcessId, m_trx);
	        	MProcess.execute(ctx, process, pi, m_trx);
	        }
	        return pi;
	        
		}
		return null;
	}
		
	
	/**
	 * Dispara el proceso de comprobar secuencias a fin de generar las mismas para las nuevas tablas
	 */
	public static void sequenceCheck(Properties ctx) throws Exception
	{
		/* Determinar el proceso a invocar */
		int sequenceCheckProcessId = DB.getSQLValue(m_trx, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE CLASSNAME = 'org.openXpertya.process.SequenceCheck' ");
		
		if (sequenceCheckProcessId <= 0)
			throw new Exception (" SequenceCheck process not found!");
		
		/* ejecutar el proceso, si hay un error propagar la excepcion */
		HashMap<String, Object> params = new HashMap<String, Object>();
		ProcessInfo pi = MProcess.execute(ctx, sequenceCheckProcessId, params, m_trx);
		if (pi.isError())
			throw new Exception( " Error al comprobar secuencia: " + pi.getSummary());
	}
	
	
	/**
	 * Todas las validaciones sobre el plugin deberian ir aca
	 * @return un string vacio si no hay problemas, o el mensaje de error en caso contrario
	 */
	public static String validatePlugin(Properties props, Properties m_ctx) 
	{
		/* El manifest contiene la estructura necesaria? */
		if (!props.containsKey(PluginConstants.PROP_VERSION) || props.get(PluginConstants.PROP_VERSION) == null || !props.containsKey(PluginConstants.PROP_PREFIX) || props.get(PluginConstants.PROP_PREFIX) == null)
			return "Archivo manifest incorrecto"; 
	
		/* Ya se encuentra instalada la versión del plugin y el componente a instalar no es un patch? */
		int res = DB.getSQLValue(null, " SELECT COUNT(1) FROM " + getGeneralPluginQuery() + "  WHERE P.ISACTIVE = 'Y' AND CV.VERSION = '" + props.get(PluginConstants.PROP_VERSION) + "' AND C.PREFIX = '" + props.get(PluginConstants.PROP_PREFIX) + "'");
		if (!props.containsKey(PluginConstants.PROP_PATCH) && res > 0)
			return "El plugin seleccionado ya se encuentra instalado";

		MComponentVersion currentComponent = MComponentVersion.getCurrentComponentVersion(m_ctx, null);
		if(currentComponent != null)
			return "Debe detener el desarrollo del plugin actualmente en generación: " + currentComponent.getName();
		
		return "";
	}
	
	
	/**
	 * Para consultas generales que involucran Plugin (P), Component (C) y ComponentVersion (CV)
	 * @return el string correspondiente
	 */
	private static String getGeneralPluginQuery()
	{
		return " AD_PLUGIN P INNER JOIN AD_COMPONENTVERSION CV ON P.AD_COMPONENTVERSION_ID = CV.AD_COMPONENTVERSION_ID INNER JOIN AD_COMPONENT C ON CV.AD_COMPONENT_ID = C.AD_COMPONENT_ID "; 
	}
	

	/** 
	 * Determina qué clase a instanciar según el manifest
	 * @param m_component_props
	 * @return
	 */
	protected static int getPostInstallProcessID(Properties ctx, Properties m_component_props) throws Exception
	{
		/* Process ID por defecto (org.openXpertya.process.PluginPostInstallProcess) */
		int defaultProcessID = DB.getSQLValue(m_trx, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE Value = '" + PluginConstants.POST_INSTALL_PROCESS_NAME + "' ");
		
		/* Verificar si tiene definido un proceso.  Si no es así, debe devolver la clase por defecto */
		String aCustomProcess = (String)m_component_props.get(PluginConstants.PROP_INSTALLPROCESS);
		if (aCustomProcess == null)
			return defaultProcessID;
		
		/* Devolver el ID del proceso a partir del OUID (si existe) */
		defaultProcessID = DB.getSQLValue(m_trx, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE AD_ComponentObjectUID = '" + m_component_props.get(PluginConstants.PROP_INSTALLPROCESS) + "' ");
		if (defaultProcessID < 1)
			throw new Exception( " Error al determinar el ID del proceso de postinstalacion ");
		
		return defaultProcessID; 
	}	


	/**
	 * Retorna el valor del parametro creado segun el tipo de dato (displayType)
	 */
	protected static Object createParamValue(String value, int displayType) {
		Object retValue = null;
		// Imposible hacer mucho mas si el value es null
		if (value == null)
			return null;
		// Instanciar segun tipo
        if  (String.class == DisplayType.getClass(displayType, false))
        	retValue = value;
        else if (Integer.class == DisplayType.getClass(displayType, false))
        	retValue = Integer.valueOf(value);
        else if (BigDecimal.class == DisplayType.getClass(displayType, false))
        	retValue = new BigDecimal(value);
        else if (Timestamp.class == DisplayType.getClass(displayType, false)) 
        	retValue = Timestamp.valueOf(value);
        else if (byte[].class == DisplayType.getClass(displayType, false))
        	retValue = value.getBytes(); 
        // Retornar valor
        return retValue;
	}
		
	/**
	 * En caso de error al realizar la instalación rollbackear la trx, detener la instalacion e informar 
	 */
	public static void handleException(String msg, Exception e, Properties props)
	{
		/* Error en algún punto, rollback (si hay trx activa) e informar al usuario */
		if (m_trx!=null) {
			Trx.getTrx(m_trx).rollback();
			Trx.getTrx(m_trx).close();
			m_trx=null;
		}
		PluginUtils.stopInstalation();
		PluginUtils.appendStatus(msg + e.getMessage());
		writeInstallLog(props);
	}

	
	/**
	 * Almacena en archivo correspondiente el log de instalacion 
	 */
	protected static void writeInstallLog(Properties props)
	{
		try {
			String prefix = (String)props.get(PluginConstants.PROP_PREFIX);
			String fileName = "Component_" + prefix + "_install_" + Env.getDateTime("yyyyMMdd_HHmmss") + ".log";
			PluginUtils.writeInstallLog(OpenXpertya.getOXPHome(), fileName);
		} catch (Exception e) {
			System.out.println("Error en escritura de log: " + e);
		}
	}
}
