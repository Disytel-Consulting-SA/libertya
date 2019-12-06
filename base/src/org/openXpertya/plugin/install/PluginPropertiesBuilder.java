package org.openXpertya.plugin.install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.openXpertya.model.MProcess;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PluginPropertiesBuilder extends PluginDocumentBuilder {

	// Constantes
	
	protected static final String NUMERAL = "# ";
	
	// Variables de instancia
		
	/** Archivo */
	
	private File properties;
	
	/** Salida Writer */
	
	private PrintWriter writer;
	
	/** Proceso custom post install */
	
	private MProcess customProcess;
	
	/** Variable indicando si es un patch */
	
	private boolean patch = false;
	
	/** Variable indicando si es un micro componente */
	
	private boolean microComponent = false;
	
	/** Variable indicando primer changelog exportado */
	
	private int changelogIDFrom = -1;
	
	/** Variable indicando ultimo changelog exportado */
	
	private int changelogIDTo = -1;
	
	/** Variable indicando primer changelogUID exportado */
	
	private String changeLogUIDFrom = "";
	
	/** Variable indicando ultimo changelogUID exportado */
	
	private String changeLogUIDTo = "";
	
	/** Variable indicando primer changelogGroupUID exportado */
	
	private String changeLogGroupUIDFrom = "";
	
	/** Variable indicando ultimo changelogGroupUID exportado */
	
	private String changeLogGroupUIDTo = "";
	
	
	// Constructores
	
	public PluginPropertiesBuilder(String path, String fileName, Integer componentVersionID, String trxName) {
		super(path, fileName, componentVersionID, trxName);
	}
	
	public PluginPropertiesBuilder(String path, String fileName, Integer componentVersionID, MProcess customProcess, String trxName) {
		super(path, fileName, componentVersionID, trxName);
		setCustomProcess(customProcess);
	}
	
	public PluginPropertiesBuilder(String path, String fileName, Integer componentVersionID, MProcess customProcess, boolean isPatch, String trxName) {
		this(path, fileName, componentVersionID, customProcess, trxName);
		setPatch(isPatch);
	}
	
	// Varios
	
	/**
	 * Imprime una property del tipo key = value dentro del archivo
	 * @param key clave
	 * @param value valor
	 */
	private void setProperty(String key, String value){
		getWriter().println(key+" = "+value);
	}
	
	/**
	 * Imprime un comentario dentro del archivo
	 * @param comment comentario
	 */
	private void setComment(String comment){
		getWriter().println(NUMERAL+comment);
	}
	
	// Heredados

	@Override
	protected void createDocument() throws Exception {
		setProperties(new File(getDestinyFilePath()));
		FileWriter fw = new FileWriter(getProperties());
	    BufferedWriter bw = new BufferedWriter(fw);
	    setWriter(new PrintWriter(bw));	    
	}

	@Override
	protected void fillDocument() throws Exception {
		setComment("Manifest del plugin "+getComponent().getPublicName());
		setProperty(PluginConstants.PROP_VERSION, getComponentVersion().getVersion());
		setProperty(PluginConstants.PROP_PREFIX, getComponent().getPrefix());
		setProperty(PluginConstants.PROP_PACKAGENAME, getComponent().getPackageName());
		setProperty(PluginConstants.PROP_PUBLICNAME, getComponent().getPublicName());
		setProperty(PluginConstants.PROP_AUTHOR, getComponent().getAuthor());
		setProperty(PluginConstants.PROP_CORELEVEL, String.valueOf(getComponent().getCoreLevel()));
		if(isPatch())
			setProperty(PluginConstants.PROP_PATCH, "Y");
		if(getComponent().isMicroComponent()) 
			setProperty(PluginConstants.PROP_MICRO_COMPONENT, "Y");
		setProperty(PluginConstants.PROP_FIRST_CHANGELOG, Integer.toString(changelogIDFrom));
		setProperty(PluginConstants.PROP_LAST_CHANGELOG, Integer.toString(changelogIDTo));
		setProperty(PluginConstants.PROP_FIRST_CHANGELOG_UID, changeLogUIDFrom);
		setProperty(PluginConstants.PROP_LAST_CHANGELOG_UID, changeLogUIDTo);
		setProperty(PluginConstants.PROP_FIRST_CHANGELOG_GROUP_UID, changeLogGroupUIDFrom);
		setProperty(PluginConstants.PROP_LAST_CHANGELOG_GROUP_UID, changeLogGroupUIDTo);
		setProperty(PluginConstants.PROP_EXPORT_TIMESTAMP, Env.getDateTime("yyyy/MM/dd-HH:mm:ss.SSS"));
		
		if(getCustomProcess() != null){
			getWriter().println();
			setComment("Proceso de instalacion custom");
			// Si no existe clase, no sigo
			if(Util.isEmpty(getCustomProcess().getClassname())){
				throw new Exception("No existe clase configurada para el proceso parametro");
			}
			setProperty(PluginConstants.PROP_INSTALLPROCESS, getCustomProcess().getAD_ComponentObjectUID());
		}
		getWriter().println();
		setComment("AUTOGENERADO - NO MODIFICAR");
		setProperty(PluginConstants.PROP_COMPONENTUID, getComponent().getAD_ComponentObjectUID());
		setProperty(PluginConstants.PROP_COMPONENTVERSIONUID, getComponentVersion().getAD_ComponentObjectUID());
		
		// Dejar template listo para incorpora a core en caso de ser necesario
		getWriter().println();
		setComment("DESCOMENTAR SI SE DESEA INCORPORAR A OTRO COMPONENTE (POR EJEMPLO CORE). INDICAR COMPONENT Y COMPONENTVERSION ADECUADOS!");
		setComment(PluginConstants.PROP_COPY_TO_CHANGELOG + " = Y");
		setComment(PluginConstants.PROP_MAP_TO_COMPONENT_UID + " = ");
		setComment(PluginConstants.PROP_MAP_TO_COMPONENTVERSION_UID + " = ");
		// Si es un desarrollo anterior a micro componentes, al llevar a core será necesario mapear los UIDs
		setComment("DESCOMENTAR SI SE DESEA MAPEAR LOS AD_ComponentObjectUIDs (ejemplo FOO2CORE) EN COPYTOCHANGELOG. DESACTIVADO POR DEFECTO (MICROCOMPONENTS YA NO LO UTILIZA)");
		setComment(PluginConstants.PROP_MAP_UIDS + " = Y");
		
	}

	@Override
	protected void saveDocument() throws Exception {
		getWriter().close();
	}
	
	
	// Getters y Setters

	protected void setCustomProcess(MProcess customProcess) {
		this.customProcess = customProcess;
	}

	protected MProcess getCustomProcess() {
		return customProcess;
	}

	protected void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	protected PrintWriter getWriter() {
		return writer;
	}

	protected void setProperties(File properties) {
		this.properties = properties;
	}

	protected File getProperties() {
		return properties;
	}

	protected void setPatch(boolean patch) {
		this.patch = patch;
	}

	protected boolean isPatch() {
		return patch;
	}
	
	public int getChangelogIDTo() {
		return changelogIDTo;
	}

	public void setChangelogIDTo(int changelogIDTo) {
		this.changelogIDTo = changelogIDTo;
	}

	public int getChangelogIDFrom() {
		return changelogIDFrom;
	}

	public void setChangelogIDFrom(int changelogIDFrom) {
		this.changelogIDFrom = changelogIDFrom;
	}
	
	public String getChangeLogUIDTo() {
		return changeLogUIDTo;
	}

	public void setChangeLogUIDTo(String changeLogUIDTo) {
		this.changeLogUIDTo = changeLogUIDTo;
	}

	public String getChangeLogUIDFrom() {
		return changeLogUIDFrom;
	}

	public void setChangeLogUIDFrom(String changeLogUIDFrom) {
		this.changeLogUIDFrom = changeLogUIDFrom;
	}
	
	public String getChangeLogGroupUIDTo() {
		return changeLogGroupUIDTo;
	}

	public void setChangeLogGroupUIDTo(String changeLogGroupUIDTo) {
		this.changeLogGroupUIDTo = changeLogGroupUIDTo;
	}

	public String getChangeLogGroupUIDFrom() {
		return changeLogGroupUIDFrom;
	}

	public void setChangeLogGroupUIDFrom(String changeLogGroupUIDFrom) {
		this.changeLogGroupUIDFrom = changeLogGroupUIDFrom;
	}
}
