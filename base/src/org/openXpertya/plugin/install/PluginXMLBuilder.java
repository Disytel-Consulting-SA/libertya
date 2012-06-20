package org.openXpertya.plugin.install;

import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Util;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public abstract class PluginXMLBuilder extends PluginDocumentBuilder{

	// CONSTANTES
	
	protected static final String DEFAULT_ROOT_NODE_NAME = "root";
	
	// Variables de instancia
	
	/** Documento */
	
	private Document doc;
	
	/** Nombre del nodo root del xml */
	
	private Node rootNode;
		
	// Constructores
	public PluginXMLBuilder(String trxName) {
		super(trxName);
	}
	
	public PluginXMLBuilder(String path, String fileName, Integer componentVersionID, String trxName){
		super(path, fileName, componentVersionID, trxName);
	}
	
	// Varios XML

	
	/**
	 * Crea el nodo root. 
	 * Realiza llamada a las subclases para obtener el nombre del nodo root, 
	 * en caso que sea null o string vacío, se coloca un nombre por defecto, 
	 * ese nombre es "root".  
	 */
	private void createRootNode(){
		// Obtengo el nombre del nodo root de la subclase
		String rootNodeName = getRootNodeName();
		if(Util.isEmpty(rootNodeName)){
			rootNodeName = DEFAULT_ROOT_NODE_NAME;
		}
		setRootNode(createElement(rootNodeName));
		// Agrego el nodo root al documento
		addNode(getRootNode(),getDoc());
	}
	
	
	// Métodos Heredados
	
	@Override
	protected void createDocument() throws Exception{
		// Creación de documento
		DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dBF.newDocumentBuilder();
		setDoc(builder.newDocument());
		// Creo el root node
		createRootNode();
	}
	
	
	@Override
	protected void saveDocument() throws Exception{
		// Crea la fuente xml del documento
		Source xmlSource = new DOMSource(getDoc());
		Result result = new StreamResult(new FileOutputStream(getDestinyFilePath()));
		// Transformación del archivo
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("indent", "yes");

		transformer.transform(xmlSource, result);
	}
	
	
	// Usables por subclases
	
	/**
	 * Crea un nodo de tipo elemento
	 * @param tagName nombre del tag
	 * @return nodo elemento creado
	 */
	protected Element createElement(String tagName){
		return getDoc().createElement(tagName); 
	}	
	
	/**
	 * Crea un nodo de tipo atributo
	 * @param name nombre del atributo
	 * @return nodo atributo creado
	 */
	protected Attr createAttribute(String name){
		return getDoc().createAttribute(name);	
	}
	
	/**
	 * Crea un nodo de texto, 
	 * usualmente usado por los tipos #PCDATA
	 * @param text texto a insertar en el nodo texto
	 * @return el nodo texto creado
	 */
	protected Text createTextNode(String text){
		return getDoc().createTextNode(text);
	}
	
	/**
	 * Crea una sección CDATA a partir de la data parámetro.
	 * @param data data a agregar en la sección
	 * @return sección CDATA creada
	 */
	protected CDATASection createCDATANode(String data){
		return getDoc().createCDATASection(data);
	}
	
	/**
	 * Agrega un nodo hijo a un padre
	 * @param child nodo hijo
	 * @param parent nodo padre
	 */
	protected void addNode(Node child, Node parent){
		parent.appendChild(child);
	}
	
	/**
	 * Crea un nodo de tipo atributo con el nombre parámetro, 
	 * le setea el valor pasado como parámetro y
	 * lo agrega al nodo padre parámetro.
	 * @param attrName nombre del atributo
	 * @param value valor del atributo
	 * @param parent nodo padre
	 */
	protected void setAttribute(String attrName, String value, Element parent){
		parent.setAttribute(attrName, value);
	}
	
	
	// Varios Usables Core Model
	
	/**
	 * Determina si el tipo de dato es una referencia a una tabla o 
	 * no es un tipo de dato pero termina en _ID y el substring que queda 
	 * desplazando el _ID es un nombre de tabla. 
	 * @param element change log element
	 * @return true si cumple lo anterior, false cc
	 */
	protected boolean isTableReference(ChangeLogElement element){
		boolean isReference = false;
		if(DisplayType.isTableReference(element.getAD_Reference_ID())){
			isReference = true;
		}
		else if((DisplayType.ID != element.getAD_Reference_ID())
					&& !element.isKey()
					&& element.getColumnName().toUpperCase().endsWith("_ID")){
			String tablename = element.getColumnName().substring(0,element.getColumnName().lastIndexOf("_"));
			String sql = "SELECT count(*) FROM ad_table WHERE upper(tablename) = upper(?)";
			isReference = DB.getSQLValue(trxName, sql, tablename) > 0;
		}
		return isReference;
	}
		
	
	/*
	 * ---------------------------------------------------
	 * 				MÉTODOS ABSTRACTOS 
	 * ---------------------------------------------------
	 */
	
	/**
	 * @return el nombre del nodo root del documento xml
	 */
	protected abstract String getRootNodeName();
	
	
	/*
	 * ---------------------------------------------------
	 */

	// Getters y Setters
	

	private void setDoc(Document doc) {
		this.doc = doc;
	}

	
	public Document getDoc() {
		return doc;
	}


	private void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}


	protected Node getRootNode() {
		return rootNode;
	}
}
