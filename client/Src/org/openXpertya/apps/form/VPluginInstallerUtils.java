package org.openXpertya.apps.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.model.MComponent;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProcessAccess;
import org.openXpertya.model.PO;
import org.openXpertya.model.POInfo;
import org.openXpertya.model.X_AD_Plugin;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.install.PluginXMLUpdater;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.util.DB;
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
	
	/**
	 * Inserta o actualiza las entradas en las tablas: AD_Plugin, AD_Component y AD_ComponentVersion
	 */
	public static void createComponentAndVersion(Properties ctx, String trxName, Properties m_component_props) throws Exception
	{
		/* Entrada en AD_Component: Columna AD_ComponentObjectUID */
		String componentEntry = (String)m_component_props.get(PluginConstants.PROP_COMPONENTUID);
		/* Entrada en AD_ComponentVersion: Columna AD_ComponentObjectUID */
		String componentVersionEntry = (String)m_component_props.get(PluginConstants.PROP_COMPONENTVERSIONUID);
		
		/* Registros de cada tabla */
		int componentID = DB.getSQLValue(trxName, " SELECT AD_Component_ID FROM AD_Component WHERE AD_ComponentObjectUID = ?", componentEntry);
		int componentVersionID = DB.getSQLValue(trxName, " SELECT AD_ComponentVersion_ID FROM AD_ComponentVersion WHERE AD_ComponentObjectUID = ?", componentVersionEntry);
		
		/* Insertar/actualizar el Component */ 
		component = new MComponent(ctx, (componentID==-1?0:componentID), trxName);
		component.setPublicName((String)m_component_props.get(PluginConstants.PROP_PUBLICNAME));
		component.setPrefix((String)m_component_props.get(PluginConstants.PROP_PREFIX));
		component.setAuthor((String)m_component_props.get(PluginConstants.PROP_AUTHOR));
		component.setPackageName((String)m_component_props.get(PluginConstants.PROP_PACKAGENAME));
		component.setCoreLevel(Integer.parseInt((String)m_component_props.get(PluginConstants.PROP_CORELEVEL)));
		component.setAD_ComponentObjectUID(componentEntry);
		
		if (!component.save())
			throw new Exception(" - Error al intentar registrar el componente: " + component.getPublicName());
		
		/* Insertar/actualizar el ComponentVersion */
		componentVersion = new MComponentVersion(ctx, (componentVersionID==-1?0:componentVersionID), trxName);
		componentVersion.setVersion((String)m_component_props.get(PluginConstants.PROP_VERSION));
		componentVersion.setAD_ComponentObjectUID(componentVersionEntry);
		componentVersion.setAD_Component_ID(component.getAD_Component_ID());

		if (!componentVersion.save())
			throw new Exception(" - Error al intentar registrar la version: " + componentVersion.getName());

		/* Registrar/actualizar el plugin */
		POInfo.clearKey(DB.getSQLValue(trxName, " SELECT AD_Table_ID FROM AD_Table WHERE tableName = ?", "AD_Plugin"));
		int pluginID = DB.getSQLValue(trxName, " SELECT P.AD_Plugin_ID FROM " + getGeneralPluginQuery() + " WHERE C.AD_Component_ID = ?", component.getAD_Component_ID());
		plugin = new X_AD_Plugin(ctx, (pluginID==-1?0:pluginID), trxName); 
		plugin.setAD_ComponentVersion_ID(componentVersion.getAD_ComponentVersion_ID());
		
		if (!plugin.save())
			throw new Exception(" - Error al intentar registrar el plugin ");
	}
	
	
	/**
	 * Incorpora información adicional a las entradas vacias de las tablas: AD_Plugin, AD_Component y AD_ComponentVersion
	 * (esto lo realiza posterior a la ejecucion de preinstall e install a fin de contar con los posibles nuevos campos) 
	 */
	public static void setAditionalValues(Properties ctx, String trxName, Properties m_component_props) throws Exception
	{
		// Fecha de exportación del componente y ultimo changelog relacionado (lo vuelvo a instanciar para recuperar las nuevas columnas)
		// Primeramente limpiar de la cache poinfo la estructura actual de ad_plugin
		POInfo.clearKey(DB.getSQLValue(trxName, " SELECT AD_Table_ID FROM AD_Table WHERE tableName = ?", "AD_Plugin"));
		plugin = new X_AD_Plugin(ctx, plugin.getAD_Plugin_ID(), trxName);
		plugin.setComponent_Export_Date((String)m_component_props.get(PluginConstants.PROP_EXPORT_TIMESTAMP));
		plugin.setComponent_Last_Changelog((String)m_component_props.get(PluginConstants.PROP_LAST_CHANGELOG));
		if (!plugin.save())
			throw new Exception(" - Error al intentar registrar información adicional del plugin ");
	}
	
	/**
	 * Entrada principal a la ejecución del proceso de PreInstalación
	 * @throws Exception
	 */
	public static void doPreInstall(Properties ctx, String trxName, String jarURL, String fileURL) throws Exception
	{
		/* Toma el archivo SQL correspondiente e impacta en la base de datos */
		String sql = JarHelper.readFromJar(jarURL, fileURL, "\n", "--");
		if (sql != null && sql.length() > 0)
			PluginXMLUpdater.executeUpdate(sql, trxName);
	}
	
	/** 
	 * Entrada principal a la ejecución del proceso de Instalación
  	 * @throws Exception
	 */
	public static void doInstall(Properties ctx, String trxName, String jarURL, String fileURL) throws Exception
	{
		/* Toma el archivo XML correspondiente, genera las sentencias SQL correspondientes e impacta en la base de datos */
		String xml = JarHelper.readFromJar(jarURL, fileURL, "", null);
		if (xml != null && xml.length() > 0)
		{
			PluginXMLUpdater uploaderMetaData = new PluginXMLUpdater(xml, trxName, false);
			uploaderMetaData.processChangeLog();
		}
	}
	
	/** 
	 * Entrada principal a la ejecución del proceso de PostInstalación
  	 * @throws Exception
	 */
	public static boolean doPostInstall(Properties ctx, String trxName, String jarURL, String fileURL, Properties props, VPluginInstaller installer) throws Exception
	{
		/* Toma el archivo XML correspondiente, genera las sentencias SQL correspondientes e impacta en la base de datos */
		String xml = JarHelper.readFromJar(jarURL, fileURL, "", null);
		if (xml != null && xml.length() > 0)
		{
			/* Determinar el proceso a invocar */
			int postInstallProcessId = getPostInstallProcessID(ctx, trxName, props);
			
			if (postInstallProcessId <= 0)
				throw new Exception (" PostInstall process not found!");
			
			/* Insertar el parametro para el XML y la ubicación del Jar */
	        ProcessInfo pi = new ProcessInfo( " Post Instalacion ", postInstallProcessId);
	        ProcessInfoParameter xtraParamXMLContent = new ProcessInfoParameter(PluginConstants.XML_CONTENT_PARAMETER_NAME, xml, null, null, null);
	        ProcessInfoParameter xtraParamJARLocation = new ProcessInfoParameter(PluginConstants.JAR_FILE_URL, jarURL, null, null, null);
	        pi.setParameter(addToArray(pi.getParameter(), xtraParamXMLContent));
	        pi.setParameter(addToArray(pi.getParameter(), xtraParamJARLocation));
	        
	        /* Invocar la ejecución del proceso, si el mismo devuelve null es porque se cancelo en los parametros */
	        ProcessCtl worker = ProcessCtl.process(installer, installer.getM_WindowNo(), pi, Trx.getTrx(trxName));
	        if (worker == null)
	        	throw new Exception (" Instalacion cancelada en post configuracion! ");
	        
	        return true;
	        
		}
		return false;
	}
	
	
	/**
	 * Dispara el proceso de comprobar secuencias a fin de generar las mismas para las nuevas tablas
	 */
	public static void sequenceCheck(Properties ctx, String trxName) throws Exception
	{
		/* Determinar el proceso a invocar */
		int sequenceCheckProcessId = DB.getSQLValue(trxName, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE CLASSNAME = 'org.openXpertya.process.SequenceCheck' ");
		
		if (sequenceCheckProcessId <= 0)
			throw new Exception (" SequenceCheck process not found!");
		
		/* ejecutar el proceso, si hay un error propagar la excepcion */
		HashMap<String, Object> params = new HashMap<String, Object>();
		ProcessInfo pi = MProcess.execute(ctx, sequenceCheckProcessId, params, trxName);
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
	protected static int getPostInstallProcessID(Properties ctx, String trxName, Properties m_component_props) throws Exception
	{
		/* Process ID por defecto (org.openXpertya.process.PluginPostInstallProcess) */
		int defaultProcessID = DB.getSQLValue(trxName, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE Value = '" + PluginConstants.POST_INSTALL_PROCESS_NAME + "' ");
		
		/* Verificar si tiene definido un proceso.  Si no es así, debe devolver la clase por defecto */
		String aCustomProcess = (String)m_component_props.get(PluginConstants.PROP_INSTALLPROCESS);
		if (aCustomProcess == null)
			return defaultProcessID;
		
		/* Devolver el ID del proceso a partir del OUID (si existe) */
		defaultProcessID = DB.getSQLValue(trxName, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE AD_ComponentObjectUID = '" + m_component_props.get(PluginConstants.PROP_INSTALLPROCESS) + "' ");
		if (defaultProcessID < 1)
			throw new Exception( " Error al determinar el ID del proceso de postinstalacion ");
		
		return defaultProcessID; 
	}	
	
	/**
	 * Incorpora un elemento s al array array
	 * @param array
	 * @param s
	 * @return
	 */
	private static ProcessInfoParameter[] addToArray(ProcessInfoParameter[] array, ProcessInfoParameter s)
	{
		/* Ya tenía parametros?  Si es null se debe a que no contenia parametros */
		ProcessInfoParameter[] ans = new ProcessInfoParameter[array==null ? 1 : array.length + 1];
		
		/* Si hay un único parametro, asignarlo a ans y devolverlo */
		if (ans.length == 1)
		{
			ans[0] = s;
			return ans;
		}
			
		/*  En caso contrario, concatear este ultimo */
		System.arraycopy(array, 0, ans, 0, array.length);
		ans[ans.length - 1] = s;
		return ans;
	}

	
}
