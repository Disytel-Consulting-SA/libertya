package org.openXpertya.plugin.install;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.openXpertya.OpenXpertya;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.MProcess;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Secure;
import org.openXpertya.util.Util;

public class ExportPlugin extends SvrProcess{

	// Variables de instancia
	
	private static Object object;

	/** Versión de componente */
	
	private Integer componentVersionID;
	
	/** Directorio destino de los archivos */
	
	private String directoryPath;
	
	/** Id de Proceso custom */
	
	private Integer processID;
	
	/** AD_ChangeLog_ID inicial */
	
	private Integer changeLogIDFrom = null;  
	
	/** AD_ChangeLog_ID fin */
	
	private Integer changeLogIDTo = null;
	
	/** changeLogUID inicial */
	
	private String changeLogUIDFrom = null;  
	
	/** changeLogUID final */
	
	private String changeLogUIDTo = null;
	
	/** Usuario registrado en registros del changelog */
	
	private Integer userID = null;
	
	/** Patch */
	
	private boolean patch = false;
	
	/** Builders de archivos */
	
	private List<PluginDocumentBuilder> builders;
	
	/** Proceso */
	MProcess process = null;
	
	/** Checkear si la los metadatos en el changelog coincide los metadatos */
	protected boolean validateChangelogConsistency = false;
	
	/** Deshabilitar las entradas del changelog inconsistentes con los metadatos */
	protected boolean disableInconsistentChangelog = false;
	
	/** El jar final fue copiado a destino definido por el desarrollador bajo PostBuildCopyJarToLocation */
	protected static boolean postBuildjarCopied = false;
	
	/** El script post build fue ejecutado PostBuildCopyJarToLocation */
	protected static boolean postBuildScriptExecuted = false; 
	
	// Heredados
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name;
        for( int i = 0;i < para.length;i++ ) {
        	name = para[ i ].getParameterName();
        	if(name.equalsIgnoreCase("AD_ComponentVersion_ID")){
        		setComponentVersionID(para[i].getParameterAsInt());
        	}
        	else if(name.equalsIgnoreCase("Directory")){
        		setDirectoryPath(String.valueOf(para[i].getParameter()));
        	}
        	else if(name.equalsIgnoreCase("AD_Process_ID")){
        		setProcessID(para[i].getParameterAsInt());
        	}
        	else if(name.equalsIgnoreCase("AD_ChangeLog_ID")){
        		setChangeLogIDFrom(para[i].getParameterAsInt());
        		setChangeLogIDTo(para[i].getParameter_ToAsInt());
        	}
        	else if(name.equalsIgnoreCase("AD_User_ID")){
        		setUserID(para[i].getParameterAsInt());
        	}
        	else if(name.equalsIgnoreCase("Patch")){
        		setPatch(String.valueOf(para[i].getParameter()).equalsIgnoreCase("Y"));
        	}
        	else if(name.equalsIgnoreCase("ValidateChangelogConsistency")){
        		validateChangelogConsistency = String.valueOf(para[i].getParameter()).equalsIgnoreCase("Y");
        	}
        	else if(name.equalsIgnoreCase("DisableInconsistentChangelog")){
        		disableInconsistentChangelog = String.valueOf(para[i].getParameter()).equalsIgnoreCase("Y");
        	}
        }
	}


	@Override
	protected String doIt() throws Exception {
		MComponentVersion currentComponent = MComponentVersion.getCurrentComponentVersion(getCtx()!=null?getCtx():Env.getCtx(), get_TrxName());
		if(currentComponent != null){
			throw new Exception(Msg.getMsg(getCtx(), "ExistCurrentPlugin"));
		}
		// Inicializar los builders xml
		initBuilders();
		// Ejecuto los builders
		int i=0;
		for (PluginDocumentBuilder docBuilder : getBuilders()) {
			// Al builder del properties debo indicarle el changelog
			if (++i == 4) {
				// Changelog IDs
				((PluginPropertiesBuilder)docBuilder).setChangelogIDTo(getLastChangelog());
				((PluginPropertiesBuilder)docBuilder).setChangelogIDFrom(getFirstChangelog());
				// Changelog UIDs
				((PluginPropertiesBuilder)docBuilder).setChangeLogUIDTo(getLastChangeLogUID());
				((PluginPropertiesBuilder)docBuilder).setChangeLogUIDFrom(getFirstChangeLogUID());
				// ChangelogGroup UIDs
				((PluginPropertiesBuilder)docBuilder).setChangeLogGroupUIDTo(getLastChangeLogGroupUID());
				((PluginPropertiesBuilder)docBuilder).setChangeLogGroupUIDFrom(getFirstChangeLogGroupUID());
			}
			// Genero el documento
			docBuilder.generateDocument();
		}
		

		
		
		// Mensaje final de proceso
		return getMsg();
	}
	
	
	// Varios
	
	/**
	 * Inicializa los builders de los archivos xml
	 */
	private void initBuilders(){
		process = null;
		if(getProcessID() != null){
			process = new MProcess(Env.getCtx(), getProcessID(), null);
		}
		builders = new ArrayList<PluginDocumentBuilder>();
		// Preinstall - 0
		builders.add(new PluginSQLBuilder(getDirectoryPath(), PluginConstants.FILENAME_PREINSTALL, getComponentVersionID(), getChangeLogIDFrom(), getChangeLogIDTo(), getUserID(), get_TrxName()));
		// Install - 1
		builders.add(new PluginInstallBuilder(getDirectoryPath(), PluginConstants.FILENAME_INSTALL, getComponentVersionID(), getChangeLogIDFrom(), getChangeLogIDTo(), getUserID(), get_TrxName(), validateChangelogConsistency, disableInconsistentChangelog));
		// PostInstall - 2
		builders.add(new PostInstallBuilder(getDirectoryPath(), PluginConstants.FILENAME_POSTINSTALL, getComponentVersionID(), getChangeLogIDFrom(), getChangeLogIDTo(), getUserID(), get_TrxName(), validateChangelogConsistency, disableInconsistentChangelog));
		// Manifest - 3
		builders.add(new PluginPropertiesBuilder(getDirectoryPath(), PluginConstants.PLUGIN_MANIFEST, getComponentVersionID(), process, isPatch(), get_TrxName()));		
	}

	/**
	 * Determina el mayor de los changelogs - cual es el ultimo changelog del export
	 * @return
	 */
	protected int getLastChangelog() 
	{
		int lastChangelogID_install = ((ChangeLogXMLBuilder)(builders.get(1))).getLastChangelogID();
		int lastChangelogID_postInstall = ((ChangeLogXMLBuilder)(builders.get(2))).getLastChangelogID();
		if (lastChangelogID_install >= lastChangelogID_postInstall)
			return lastChangelogID_install;
		return lastChangelogID_postInstall;
	}
	
	/**
	 * Retorna el ultimo changelogUID del export
	 */
	protected String getLastChangeLogUID() 
	{
		return DB.getSQLValueString(get_TrxName(), "SELECT changeLogUID FROM AD_Changelog WHERE AD_Changelog_ID = " + getLastChangelog());
	}
	
	/**
	 * Retorna el ultimo changelogGroupUID del export
	 */
	protected String getLastChangeLogGroupUID() 
	{
		return DB.getSQLValueString(get_TrxName(), "SELECT changeLogGroupUID FROM AD_Changelog WHERE AD_Changelog_ID = " + getLastChangelog());
	}
	
	/**
	 * Determina el menor de los changelogs - cual es el primer changelog del export
	 * Considera la eventual posibilidad de que el install o el postInstall sea -1
	 * @return
	 */
	protected int getFirstChangelog() 
	{
		int firstChangelogID_install = ((ChangeLogXMLBuilder)(builders.get(1))).getFirstChangelogID();
		int firstChangelogID_postInstall = ((ChangeLogXMLBuilder)(builders.get(2))).getFirstChangelogID();
		if (firstChangelogID_install == -1 && firstChangelogID_postInstall == -1)
			return -1;
		return Math.min((firstChangelogID_install<=0	?Integer.MAX_VALUE:firstChangelogID_install), 
						(firstChangelogID_postInstall<=0?Integer.MAX_VALUE:firstChangelogID_postInstall));  
	}
	
	/**
	 * Retorna el primer changelogUID del export
	 */
	protected String getFirstChangeLogUID() 
	{
		return DB.getSQLValueString(get_TrxName(), "SELECT changeLogUID FROM AD_Changelog WHERE AD_Changelog_ID = " + getFirstChangelog());
	}
	
	/**
	 * Retorna el primer changelogGroupUID del export
	 */
	protected String getFirstChangeLogGroupUID() 
	{
		return DB.getSQLValueString(get_TrxName(), "SELECT changeLogGroupUID FROM AD_Changelog WHERE AD_Changelog_ID = " + getFirstChangelog());
	}
	
	/**
	 * @return mensaje final del proceso
	 */
	private String getMsg(){
		StringBuffer msg = new StringBuffer();
		msg.append("Exportacion de plugin realizada correctamente dentro del directorio ");
		msg.append(getDirectoryPath());		
		return msg.toString();
	}


	// Getters y Setters
	
	protected void setComponentVersionID(Integer componentVersionID) {
		this.componentVersionID = componentVersionID;
	}


	protected Integer getComponentVersionID() {
		return componentVersionID;
	}


	protected void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}


	protected String getDirectoryPath() {
		return directoryPath;
	}


	protected void setBuilders(List<PluginDocumentBuilder> builders) {
		this.builders = builders;
	}


	protected List<PluginDocumentBuilder> getBuilders() {
		return builders;
	}


	protected void setProcessID(Integer processID) {
		this.processID = processID;
	}


	protected Integer getProcessID() {
		return processID;
	}


	protected void setChangeLogIDFrom(Integer changeLogIDFrom) {
		if (changeLogIDFrom == 0) {
			changeLogIDFrom = null;
		}
		this.changeLogIDFrom = changeLogIDFrom;
	}


	protected Integer getChangeLogIDFrom() {
		return changeLogIDFrom;
	}


	protected void setChangeLogIDTo(Integer changeLogIDTo) {
		if (changeLogIDTo == 0) {
			changeLogIDTo = null;
		}
		this.changeLogIDTo = changeLogIDTo;
	}


	protected Integer getChangeLogIDTo() {
		return changeLogIDTo;
	}


	protected void setUserID(Integer userID) {
		if (userID == 0) {
			userID = null;
		}
		this.userID = userID;
	}


	protected Integer getUserID() {
		return userID;
	}


	protected void setPatch(boolean patch) {
		this.patch = patch;
	}


	protected boolean isPatch() {
		return patch;
	}
	

	protected boolean isValidateChangelogConsistency() {
		return validateChangelogConsistency;
	}


	protected void setValidateChangelogConsistency(boolean validateChangelogConsistency) {
		this.validateChangelogConsistency = validateChangelogConsistency;
	}
	
	protected boolean isDisableInconsistentChangelog() {
		return disableInconsistentChangelog;
	}


	protected void setDisableInconsistentChangelog(boolean disableInconsistentChangelog) {
		this.disableInconsistentChangelog = disableInconsistentChangelog;
	}

	
	
	/* ================================================ INVOCACION DESDE TERMINAL ================================================ */
	
	/** Contenido de propiedades almacenado en archivo devinfo.properties */
	protected static Properties props;
	
	/** Directorio base determinado a partir de la ubicacion del archivo devinfo.properties */
	protected static String baseDir; 

	/** Nombre definitivo del archivo a crear */
	protected static StringBuffer fileName = null;
	
	protected static HashMap<String, String> defaults = new HashMap<String, String>();
	
	public static void main(String[] args) {

		try {
			loadDefaults();
			
			validateArguments(args);
			
			loadProps(args);
			
			setConnection();

			startEnvironment();
			
			showInfo();
			
			executeExport();
			
			copyFiles();
			
			createJar();
			
			copyJar();
			
			callScript();
			
			finished();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	protected static void loadDefaults() {
		defaults.put("CreateJarBinariesLocation", 		"binarios");
		defaults.put("CreateJarLibsLocation", 			".."+File.separator+".."+File.separator+"lib");
		defaults.put("CreateJarClassesLocation", 		".."+File.separator+".."+File.separator+"bin");
		defaults.put("CreateJarServerRootLibLocation", 	".."+File.separator+".."+File.separator+"lib_serverRoot");
		defaults.put("CreateJarServerRootLocation", 	".."+File.separator+".."+File.separator+"bin_serverRoot");
		defaults.put("CreateJarOXPRootBaseLibLocation",	".."+File.separator+".."+File.separator+"lib_oxpRootBase");
		defaults.put("CreateJarOXPRootBaseLocation", 	".."+File.separator+".."+File.separator+"bin_oxpRootBase");
		defaults.put("CreateJarWebUILibLocation", 		".."+File.separator+".."+File.separator+"lib_webui");
		defaults.put("CreateJarWebUILocation", 			".."+File.separator+".."+File.separator+"bin_webui");
	}

	protected static void validateArguments(String[] args) {
		if (args.length == 0) {
			System.out.println("Se requiere un descriptor devinfo.properties (junto a su full path)");
			System.exit(1);
		}
	}
	
	protected static void loadProps(String[] args) throws Exception {
		props = Util.loadProperties(args[0]);
		baseDir = args[0].substring(0, args[0].lastIndexOf(File.separator)+1);
		
		// Debe solicitarse la generacion de un Jar conteniendo export de metadatos o de compilacion. En caso contrario no hay nada por hacer 
		if (!"Y".equalsIgnoreCase(prop("IncludeComponentExport")) && !"Y".equalsIgnoreCase(prop("IncludeClassesAndLibs"))) {
			System.err.println("No se solicito export de datos ni compilacion. Nada que hacer");
			System.exit(1);
		}			
	}
	
	protected static void startEnvironment() {
		Env.setContext(Env.getCtx(), "#AD_Client_ID", 0);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", 0);
		if (!OpenXpertya.startupEnvironment( true )) {
			System.err.println("Error al iniciar. Validar conexion.");
			System.exit(1);
		}
	}
	
	protected static void setConnection() {
		// Al especificar la conexion ya no es necesario el uso 
		//		System.setProperty("PropertyFile", "/tmp/Libertya.properties");
		//		Ini.setShowLicenseDialog(false);
		Ini.getProperties().put(Ini.P_CONNECTION, 
				Secure.CLEARTEXT + 
				"CConnection["
				+ "name=localhost{DEVELOPMENT-DEVELOPMENT},"
				+ "AppsHost=localhost,"		
				+ "AppsPort=1099,"
				+ "RMIoverHTTP=false,"
				+ "type=PostgreSQL,"
				+ "DBhost="+props.getProperty("DBHost")+","
				+ "DBport="+props.getProperty("DBPort")+","
				+ "DBname="+props.getProperty("DBName")+","
				+ "BQ=false,"
				+ "FW=false,"
				+ "FWhost=,"
				+ "FWport=0,"
				+ "UID="+props.getProperty("DBUser")+","
				+ "PWD="+props.getProperty("DBPass")+"]");
	}
	
	protected static void showInfo() {
		System.out.println("=== PLUGIN EXPORTER ===");
		System.out.println(DB.getDatabaseInfo());
		System.out.println("Config:");
		props.keySet().stream().sorted().forEach( key -> {
				StringBuffer conf = new StringBuffer();
				conf.append("  ").append(key).append("=").append(props.get(key));
				System.out.println(conf);
			}
		);
		System.out.println();
	}
	
	protected static void executeExport() throws Exception {
		// Incluir la exportacion del componente?
		if (!"Y".equalsIgnoreCase(prop("IncludeComponentExport"))) {
			// Solo crear el directorio de exportacion 
			if (shouldcreateDir("ExportDirectory")) {
				FileUtils.forceMkdir(file("ExportDirectory"));
			}
			return;
		} 
		
		// Version de componente a exportar
		int componentVersionID = Integer.parseInt(props.getProperty("ExportComponentVersionID"));
		
		// Se encuentra actualmente en desarrollo? Desactivar
		boolean currentDevelopment = "Y".equalsIgnoreCase(DB.getSQLValueString(null, "SELECT currentdevelopment FROM AD_ComponentVersion WHERE AD_ComponentVersion_ID = ?", componentVersionID));
		if (currentDevelopment) {
			System.out.println(" Deteniendo el desarrollo del componente temporalmente... ");
			DB.executeUpdate("UPDATE AD_ComponentVersion SET currentdevelopment = 'N' where AD_ComponentVersion_ID = " + componentVersionID);
		}
		
		try {
			System.out.println("===========");
			System.out.println(DB.getSQLValueString(null, "SELECT 'Exportando: ' || name || '...' FROM AD_ComponentVersion WHERE AD_ComponentVersion_ID = ?", componentVersionID));
			System.out.println("===========");
			System.out.println();
			
			ExportPlugin ep = new ExportPlugin();
			ep.setComponentVersionID(Integer.parseInt(prop("ExportComponentVersionID")));
			ep.setDirectoryPath(prop("ExportDirectory"));
			if (!Util.isEmpty(prop("ExportProcessID"),true) && Integer.parseInt(prop("ExportProcessID")) > 0)
				ep.setProcessID(Integer.parseInt(prop("ExportProcessID")));
			ep.setChangeLogIDFrom(Integer.parseInt(prop("ExportChangelogFromID")));
			ep.setChangeLogIDTo(Integer.parseInt(prop("ExportChangelogToID")));
			ep.setUserID(Integer.parseInt(prop("ExportFromUserID")));
			ep.setPatch("Y".equalsIgnoreCase(prop("ExportAsPatch")));
			ep.setValidateChangelogConsistency("Y".equalsIgnoreCase(prop("ExportAndValidateConsistency")));
			ep.setDisableInconsistentChangelog("Y".equalsIgnoreCase(prop("ExportAndDisableInvalidEntries")));
			ep.doIt();
		} catch (Exception e) {
			throw e;
		} finally {
			// Dejar en desarrollo tal como estaba (solo si corresponde) 
			if (currentDevelopment) {
				System.out.println(" Reactivando el desarrollo del componente... ");
				DB.executeUpdate("UPDATE AD_ComponentVersion SET currentdevelopment = 'Y' where AD_ComponentVersion_ID = " + componentVersionID);
			}
		}
	}
	
	protected static void copyFiles() throws Exception {
		// Pisado de preinstall
		if (!Util.isEmpty(prop("CreateJarPreinstallFile"))) {
			copyFileContents("CreateJarPreinstallFile", "", "preinstall.sql");
		}
		
		// Copia de reportes/binarios
		if ("Y".equalsIgnoreCase(prop("IncludeReports"))) {
			copyDirContents("CreateJarBinariesLocation", "binarios");			
		}

		// Copia de compilacion y librerias externas
		if ("Y".equalsIgnoreCase(prop("IncludeClassesAndLibs"))) {
			// Librerias externas
			copyDirContents("CreateJarLibsLocation", "lib");			
			// Clases compiladas
			copyDirContents("CreateJarClassesLocation", "");
			// Archivos con destino OXPRoot.jar
			copyDirContents("CreateJarServerRootLibLocation", "serverRoot" + File.separator + "lib");			
			copyDirContents("CreateJarServerRootLocation", "serverRoot");
			// Archivos con destino OXPRootBase.war
			copyDirContents("CreateJarOXPRootBaseLibLocation", "oxpRootBase" + File.separator + "lib");			
			copyDirContents("CreateJarOXPRootBaseLocation", "oxpRootBase");
			// Archivos con destino webui.war
			copyDirContents("CreateJarWebUILibLocation", "webui" + File.separator + "WEB-INF" + File.separator + "lib");
			copyDirContents("CreateJarWebUILocation", "webui");
		}
	}
	
	protected static void copyFileContents(String property, String exportSubDir,  String targetFileName) throws Exception {
		if (prop(property)==null || prop(property).trim().length()==0) {
			System.out.println("WARNING: Propiedad " + property + " no definida");
			return;
		}
		if (file(baseDir, prop(property)).exists()) {
			FileUtils.copyFile(file(baseDir, prop(property)), file(prop("ExportDirectory"), exportSubDir, targetFileName));
		} else {
			System.out.println("WARNING: Archivo " + prop(property) + " omitido (no encontrado)");
		}
	}

	protected static void copyDirContents(String property, String exportSubDir) throws Exception {
		if (prop(property)==null || prop(property).trim().length()==0) {
			System.out.println("WARNING: Propiedad " + property + " no definida");
			return;
		}
		if (file(baseDir, prop(property)).isDirectory() && file(baseDir, prop(property)).exists()) {
			FileUtils.copyDirectory(file(baseDir, prop(property)), file(prop("ExportDirectory"), exportSubDir));
		} else {
			System.out.println("WARNING: Directorio " + prop(property) + " omitido (no encontrado)");
		}
	}
	
	protected static void createJar() throws Exception {
		Process process = Runtime.getRuntime().exec(getJarCreationCommand(getJarFileName()), null, file(prop("ExportDirectory")));
		process.waitFor();
		if (process.exitValue() > 0) {
			throw new Exception("Error en creacion de jar: " + inputStreamToString(process.getErrorStream())) ;
		}
		moveJarToDestination();
	}
	
	protected static void copyJar() throws Exception {
		if (Util.isEmpty(prop("PostBuildCopyJarToLocation")))
			return;
		try {
			File source = file(prop("CreateJarTargetDir"), fileName.toString());
			File target = file(prop("PostBuildCopyJarToLocation"), fileName.toString());
			if (target.exists()) {
	            FileUtils.forceDelete(target);
			}
			FileUtils.copyFile(source, target);
			postBuildjarCopied = true;
		} catch (Exception e) {
			System.out.println("WARNING: Error en PostBuildCopyJarToLocation: " + e.getMessage());
		}
	}
	
	
	protected static void callScript() throws Exception {
		if (Util.isEmpty(prop("PostBuildExecuteScriptFile")) || Util.isEmpty(prop("PostBuildExecuteScriptDir"))) 
			return;
		try {
			System.out.println("Ejecutando " + prop("PostBuildExecuteScriptFile"));
			Process process = Runtime.getRuntime().exec(new String[] {prop("PostBuildExecuteScriptFile")}, null, file(prop("PostBuildExecuteScriptDir")));
			process.waitFor();
			if (process.exitValue() > 0) {
				System.out.println("WARNING: Error en PostBuildExecuteScript: " + inputStreamToString(process.getErrorStream())) ;
				return;
			}
		} catch (Exception e) {
			System.out.println("WARNING: Exception en PostBuildExecuteScript: " + e.getMessage()) ;
			return;
		}
		postBuildScriptExecuted = true;
	}

	
	protected static void moveJarToDestination() throws Exception {
		// Si es directorio de export de componente y el de creacion de jar es el mismo, no hay mas nada que hacer, en caso contrario mover el archivo 
		if (!(prop("CreateJarTargetDir")).equals(prop("ExportDirectory"))) {
			File target = file(prop("CreateJarTargetDir"), fileName.toString());
	        if (target.exists()) {
	            FileUtils.forceDelete(target);
	        }
			FileUtils.moveFileToDirectory(file(prop("ExportDirectory"), fileName.toString()), file(prop("CreateJarTargetDir")), shouldcreateDir("CreateJarTargetDir"));
		}
	}
	
	protected static final void finished() {
		System.out.println();
		System.out.println("Archivos exportados a: " + prop("ExportDirectory"));
		System.out.println("Jar final generado en: " + prop("CreateJarTargetDir"));
		
		if (postBuildjarCopied)
			System.out.println("Jar final copiado en: " + prop("PostBuildCopyJarToLocation"));
		
		if (postBuildScriptExecuted)
			System.out.println("Ejecutado script: " + prop("PostBuildExecuteScriptFile"));
		
		
		System.out.println();
		System.out.println("===========");
		System.out.println("Finalizado!");
		System.out.println("===========");
		System.out.println();
	}
	
	// === Helper methods ===
	
	protected static File file(String... args) {
		StringBuffer buf = new StringBuffer();
		for (String arg : args) {
			if (arg==null || arg.trim().length()==0)
				continue;
			buf.append(arg).append(arg.endsWith(File.separator)?"":File.separator);
		}
		return new File(buf.toString().substring(0, buf.length()-1));
	}
	
	protected static String prop(String key) {
		// Se encuentra en el properties?
		if (props.getProperty(key)!=null)
			return props.getProperty(key);
		// Se encuentra en defaults?
		return defaults.get(key);
	}
	
	protected static boolean shouldcreateDir(String dirName) {
		 File dir = file(prop(dirName));
		 return !(dir.exists() && dir.isDirectory()); 
	}
	
	protected static String[] getJarCreationCommand(String fileName) throws Exception {
		if (System.getProperty("os.name")==null)
			throw new Exception("Imposible determinar os.name");
		// windows
		if(System.getProperty("os.name").toLowerCase().contains("windows")){
			return new String[] {"cmd", "/c", "jar -cf " + fileName + " *"};
		}
		// OS
		return new String[] {"sh", "-c", "jar -cf " + fileName + " *"};
	}
	
	protected static String inputStreamToString(InputStream inputStream) throws Exception {
	    StringBuffer sb = new StringBuffer();
	    Scanner scanner = new Scanner(inputStream);
	    while (scanner.hasNextLine()) {
	    	sb.append(scanner.nextLine());	
	    }
	    return sb.toString();
	}
	
	protected static String getJarFileName() throws Exception {
		if (fileName!=null)
			return fileName.toString();
		
		// Se forzo un nombre en particular para el jar?
		if (!Util.isEmpty(prop("CreateJarForceFileName"), true)) {
			fileName = new StringBuffer(prop("CreateJarForceFileName"));
			return fileName.toString();
		}
		
		// Nombre principal - Si se definio un component version, se intenta generarlo desde los metadats
		fileName = new StringBuffer(DB.getSQLValueString(null, "select case when c.prefix = 'CORE' then 'org.libertya.core' ELSE c.packagename end || '_v' || cv.version from ad_component c inner join ad_componentversion cv on c.ad_component_id  = cv.ad_component_id where cv.ad_componentversion_id = ? ", Integer.parseInt(props.getProperty("ExportComponentVersionID"))));
		if (fileName.length()==0) {
			fileName.append("component");
		}
				
		// Changelog
		if ("Y".equalsIgnoreCase(prop("IncludeComponentExport"))) {
			// Determinar hasta que changelog se exporto, considerando que en devinfo puede haber un valor distinto a cero
			int maxLog = DB.getSQLValue(null, "SELECT max(ad_changelog_id) from ad_changelog where ad_componentversion_id = " + Integer.parseInt(props.getProperty("ExportComponentVersionID")));
			if (Integer.parseInt(prop("ExportChangelogToID")) > 0 && Integer.parseInt(prop("ExportChangelogToID")) < maxLog) {
				maxLog = Integer.parseInt(prop("ExportChangelogToID"));
			}
			fileName = fileName.append("_c"+maxLog);
		}

		// Revision svn / git
		if ("Y".equalsIgnoreCase(prop("IncludeClassesAndLibs")) || "Y".equalsIgnoreCase(prop("IncludeReports"))) {
			String revision = getRevision();	
			fileName = fileName.append("_r"+revision);			
		}
		return fileName.append(".jar").toString();
	}
	
	
	protected static String getRevision() throws Exception {
		// Git: Se toma la revision 
		if (isGit()) {
			return getCommitRevision(baseDir);
		}
		
		// SVN
		// Podria ser que un proyecto solo ciertos subdirectorios del proyecto
		// son actualizados mediante update/pull.  Por consiguiente, la revision
		// podria llegar a variar segun la ubicacion.  Es por esto que se toma
		// la mayor revision entre los principales directorios del proyecto.
		String root   = baseDir + ".." + File.separator + "..";
		String data   = root 	+ File.separator + "data";
		String src    = root 	+ File.separator + "src";
		String lib    = root 	+ File.separator + "lib";
		String[] dirs = new String[]{baseDir, root, data, src, lib};
		
		int maxRev = -1;
		for (String dir : dirs) {
			int revision = Integer.parseInt(getCommitRevision(dir));
			if (revision > maxRev) {
				maxRev = revision;
			}
		}
		if (maxRev == -1) {
			return "UNDEFINED";
		}
		return Integer.toString(maxRev);
	}

	
	protected static String getCommitRevision(String dir) throws Exception {
		File location = file(dir);
		if (!location.exists())
			return "-1";
		Process process = Runtime.getRuntime().exec(getVersioningCommand(), null, location);
		process.waitFor();
		if (process.exitValue() > 0) {
			return "-1";
		}
		return inputStreamToString(process.getInputStream());
	}
		
	
	protected static String[] getVersioningCommand() throws Exception {
		if (System.getProperty("os.name")==null)
			throw new Exception("Imposible determinar os.name");
		// windows
		if(System.getProperty("os.name").toLowerCase().contains("windows")){
			if (isSVN())
				return new String[] {"cmd", "/c", "svn info --show-item revision"};
			if (isGit())
				return new String[] {"cmd", "/c", "git rev-parse --short=7 HEAD"};
		}
		// Otro OS
		if (isSVN())
			return new String[] {"sh", "-c", "svn info --show-item revision"};
		if (isGit())
			return new String[] {"sh", "-c", "git rev-parse --short=7 HEAD"};
		throw new Exception("Indicar git o svn en propiedad ProjectVersionControl");
	}
	
	protected static boolean isGit() {
		return "git".equalsIgnoreCase(prop("ProjectVersionControl"));
	}
	
	protected static boolean isSVN() {
		return "svn".equalsIgnoreCase(prop("ProjectVersionControl"));
	}
}




