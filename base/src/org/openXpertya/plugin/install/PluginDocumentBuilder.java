package org.openXpertya.plugin.install;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.MComponent;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

public abstract class PluginDocumentBuilder {

	// Constantes

	protected static final String SEPARATOR = File.separator;
	
	// Variables de clase
	
	protected CLogger log = CLogger.getCLogger("PluginDocumentBuilder_"+System.currentTimeMillis());
	
	// Variables de instancia
	
	/** Path destino del archivo generado */
	
	private String path;
	
	/** Directorio destino */
	
	private File dir;
	
	/** Nombre del archivo a generar */
	
	private String fileName;
	
	/** Versión del componente */
	
	private MComponentVersion componentVersion;
	
	/** Componente */
	
	private MComponent component;
	

	/** Transacción a utilizar */
	
	protected String trxName;
	
	// Constructores
	
	/**
	 * Constructor basico para compatibilidad con replicacion
	 */
	public PluginDocumentBuilder(int array, String trxName)
	{
		super();
		this.trxName = trxName;
	}
	
	
	/**
	 * Constructor para logica de componentes (exportacion de plugin)
	 * @param path
	 * @param fileName
	 * @param componentVersionID
	 */
	public PluginDocumentBuilder(String path, String fileName,Integer componentVersionID, String trxName){
		setPath(path);
		setFileName(fileName);
		setDir(getDirectory(path));
		initComponents(componentVersionID);
		this.trxName = trxName;
	}
	
	/**
	 * Inicialización de componentes
	 * @param componentVersionID versión del componente
	 */
	private void initComponents(Integer componentVersionID){
		setComponentVersion(new MComponentVersion(Env.getCtx(), componentVersionID, null));
		setComponent(new MComponent(Env.getCtx(), getComponentVersion().getAD_Component_ID(), null));
		setUID(getComponentVersion());
		setUID(getComponent());
	}
	
	/**
	 * Seteo el uid del po parámetro
	 * @param po
	 */
	private void setUID(PO po){
		// Si no tiene uno seteado, le seteo uno 
		if(po.get_ValueAsString("AD_ComponentObjectUID").length() == 0){
			List<String> uidValues = new ArrayList<String>();
			uidValues.add(getComponent().getPrefix());
			uidValues.add(po.get_TableName());
			uidValues.add(String.valueOf(po.getID()));
			po.set_ValueOfColumn("AD_ComponentObjectUID", PO.makeUID(uidValues));
			if(!po.save()){
				log.severe("Error al actualizar campo AD_ComponentObjectUID para el registro "+po.toString());
			}
		}
	}	
	
	/**
	 * Si no existe el directorio se crea.
	 * @param path path 
	 * @return Directorio del path parámetro  
	 */
	private File getDirectory(String path){
		File directory = new File(path);
		if(!directory.exists()){
			directory.mkdirs();
		}
		return directory;		
	}
	
	/**
	 * @return path destino junto con el nombre del archivo, 
	 * por ejemplo si el path destino es /Destino 
	 * y el nombre del archivo es prueba.xml, 
	 * entonces el metodo retorna /Destino/prueba.xml 
	 */
	protected String getDestinyFilePath(){
		if(getPath().lastIndexOf(SEPARATOR) != (getPath().length()-1)){
			setPath(getPath()+SEPARATOR);
		}
		return getPath()+getFileName();
	}
	
	
	/**
	 * Genera el xml, lo creo y lo guarda dentro del path destino y 
	 * con el nombre de archivo configurados.
	 * @throws Exception 
	 */
	public void generateDocument() throws Exception{
		// Creo el documento
		createDocument();
		// Generación del documento a cargo de las subclases
		fillDocument();
		// Guardar el archivo xml
		saveDocument();
	}
	
	
	/*
	 * ---------------------------------------------------
	 * 				MÉTODOS ABSTRACTOS 
	 * ---------------------------------------------------
	 */
	
	/**
	 * Genera el xml, lo creo y lo guarda dentro del path destino y 
	 * con el nombre de archivo configurados.
	 * @throws Exception 
	 */
	protected abstract void createDocument() throws Exception;
	
	/**
	 * Genera el xml, lo creo y lo guarda dentro del path destino y 
	 * con el nombre de archivo configurados.
	 * @throws Exception 
	 */
	protected abstract void fillDocument() throws Exception;
	
	/**
	 * Genera el xml, lo creo y lo guarda dentro del path destino y 
	 * con el nombre de archivo configurados.
	 * @throws Exception 
	 */
	protected abstract void saveDocument() throws Exception;
	
	/*
	 * ---------------------------------------------------
	 */
	
	
	// Getters y Setters
	
	public void setPath(String path) {
		this.path = path;
	}


	public String getPath() {
		return path;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFileName() {
		return fileName;
	}

	protected void setDir(File dir) {
		this.dir = dir;
	}

	protected File getDir() {
		return dir;
	}

	protected void setComponentVersion(MComponentVersion componentVersion) {
		this.componentVersion = componentVersion;
	}

	protected MComponentVersion getComponentVersion() {
		return componentVersion;
	}

	protected void setComponent(MComponent component) {
		this.component = component;
	}

	protected MComponent getComponent() {
		return component;
	}
}
