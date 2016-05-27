package org.openXpertya.util;

import java.util.ArrayList;
import java.util.List;

public class HTMLMsg {

	public static final String TAG_OPEN = "<";
	public static final String TAG_OPEN_CLOSE_ELEMENT = "</";
	public static final String TAG_CLOSE = ">";
	public static final String BLANK = " ";
	public static final String ID = "id";
	public static final String EQUAL = "=";
	public static final String TAG_P_ELEMENT = "p";
	
	/** Listas */
	private List<HTMLList> lists;
	
	/** Mensaje para la cabecera del mensaje */
	private String headerMsg;
	
	/** Mensaje para el pie del mensaje */
	private String footerMsg;
	
	public HTMLMsg() {
		setLists(new ArrayList<HTMLList>());
	}

	/**
	 * Agrega una lista al mensaje html
	 * 
	 * @param list
	 *            lista
	 */
	public void addList(HTMLList list){
		if(list != null){
			getLists().add(list);
		}
	}

	/**
	 * Agrega un elemento a la lista parámetro
	 * 
	 * @param list
	 *            lista
	 * @param element
	 *            elemento de lista
	 */
	public void addListElement(HTMLList list, HTMLListElement element){
		if (list != null && element != null) {
			list.addListElement(element);
		}
	}
	
	/**
	 * Crea una lista html
	 * 
	 * @param id
	 *            id de la lista
	 * @param type
	 *            tipo de lista
	 * @return instancia de la clase {@link HTMLList}
	 */
	public HTMLList createList(String id, String type){
		return new HTMLList(id, type);
	}

	/**
	 * Crea una lista html
	 * 
	 * @param id
	 *            id de la lista
	 * @param type
	 *            tipo de lista
	 * @param msg
	 *            mensaje de la cabecera de la lista
	 * @return instancia de la clase {@link HTMLList}
	 */
	public HTMLList createList(String id, String type, String msg){
		return new HTMLList(id, type, msg);
	}

	/**
	 * Crea un elemento de lista con el id y value parámetro, el value es lo que
	 * se coloca dentro del PDATA
	 * 
	 * @param id
	 *            id de la lista
	 * @param value
	 *            valor PDATA del elemento
	 * @return un nuevo elemento de lista 
	 */
	public HTMLListElement createListElement(String id, String value){
		return new HTMLListElement(id, value);
	}

	/**
	 * Crea un elemento de lista con el id y value parámetro, el value es lo que
	 * se coloca dentro del PDATA. Luego se agrega el elemento creado dentro de
	 * la lista parámetro
	 * 
	 * @param id
	 *            id del elemento de lista a crear
	 * @param value
	 *            valor PDATA del elemento a crear
	 * @param list
	 *            lista destino del elemento creado
	 */
	public void createAndAddListElement(String id, String value, HTMLList list){
		list.addListElement(createListElement(id, value));
	}
	
	/**
	 * @param pdata
	 *            data del elemento p en la parte PDATA del elemento
	 * @return el string que representa un tag html p junto con su PDATA
	 */
	public String toStringPElement(String pdata){
		StringBuilder pBuild = new StringBuilder();
		pBuild.append(TAG_OPEN).append(TAG_P_ELEMENT).append(TAG_CLOSE);
		pBuild.append(pdata);
		pBuild.append(TAG_OPEN_CLOSE_ELEMENT).append(TAG_P_ELEMENT)
				.append(TAG_CLOSE);
		return pBuild.toString();
	}
	
	

	@Override
	public String toString(){
		StringBuilder strBuild = new StringBuilder();
		if(!Util.isEmpty(getHeaderMsg())){
			strBuild.append(toStringPElement(getHeaderMsg()));
		}
		for (HTMLList list : getLists()) {
			strBuild.append(list.toString());
		}
		if(!Util.isEmpty(getFooterMsg())){
			strBuild.append(toStringPElement(getFooterMsg()));
		}
		return strBuild.toString();
	}
	
	
	
	public void setLists(List<HTMLList> lists) {
		this.lists = lists;
	}

	public List<HTMLList> getLists() {
		return lists;
	}

	/**
	 * @param headerMsg the headerMsg to set
	 */
	public void setHeaderMsg(String headerMsg) {
		this.headerMsg = headerMsg;
	}

	/**
	 * @return the headerMsg
	 */
	public String getHeaderMsg() {
		return headerMsg;
	}

	/**
	 * @param footerMsg the footerMsg to set
	 */
	public void setFooterMsg(String footerMsg) {
		this.footerMsg = footerMsg;
	}
	
	/**
	 * @return the footerMsg
	 */
	public String getFooterMsg() {
		return footerMsg;
	}

	/**
	 * Lista HTML con su cabecera y elementos
	 * 
	 * @author Equipo de Desarrollo - Disytel
	 * 
	 */
	public class HTMLList{
		
		private HTMLListHeader header;
		private List<HTMLListElement> elements;
		
		public HTMLList(String listID, String listType){
			setHeader(new HTMLListHeader(listID, listType));
			setElements(new ArrayList<HTMLListElement>());
		}

		public HTMLList(String listID, String listType, String msg){
			this(listID, listType);
			setMsg(msg);
		}
		
		/**
		 * Agrega el elemento parámetro a la lista
		 * 
		 * @param element
		 *            elemento de lista
		 */
		public void addListElement(HTMLListElement element){
			getElements().add(element);
		}
		
		public void setHeader(HTMLListHeader header) {
			this.header = header;
		}

		public HTMLListHeader getHeader() {
			return header;
		}

		public void setElements(List<HTMLListElement> elements) {
			this.elements = elements;
		}

		public List<HTMLListElement> getElements() {
			return elements;
		}
		
		public void setMsg(String msg){
			getHeader().setMsg(msg);
		}
		
		@Override
		public String toString(){
			StringBuffer listStr = new StringBuffer();
			listStr.append(getHeader().getStringOpenList());
			for (HTMLListElement element : getElements()) {
				listStr.append(element.toString());
			}
			listStr.append(getHeader().getStringCloseList());
			return listStr.toString();
		}
	}
	
	/**
	 * Clase con las propiedades para crear una cabecera de lista html
	 * 
	 * @author Equipo de Desarrollo - Disytel
	 * 
	 */
	public class HTMLListHeader{
		public static final String UL_LIST_TYPE = "ul";
		public static final String OL_LIST_TYPE = "ll";
		
		private String type;
		private String id;
		private String msg;
		
		public HTMLListHeader(String id, String type){
			setId(id);
			setType(type);
		}
		
		public HTMLListHeader(String id, String type, String msg){
			this(id, type);
			setMsg(msg);
		}

		/**
		 * @return el string de apertura de la lista 
		 */
		public String getStringOpenList(){
			StringBuffer openTag = new StringBuffer();
			if(!Util.isEmpty(getMsg())){
				openTag.append(getMsg());
			}
			openTag.append(TAG_OPEN).append(getType());
			if(!Util.isEmpty(getId())){
				openTag.append(BLANK).append(ID).append(EQUAL).append(getId());
			}			
			openTag.append(TAG_CLOSE);			
			return openTag.toString();
		}
		
		/**
		 * @return el string de apertura de la lista 
		 */
		public String getStringCloseList(){
			return TAG_OPEN_CLOSE_ELEMENT + getType() + TAG_CLOSE;
		}
		
		public void setType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		/**
		 * @param msg the msg to set
		 */
		public void setMsg(String msg) {
			this.msg = msg;
		}

		/**
		 * @return the msg
		 */
		public String getMsg() {
			return msg;
		}	
	}

	/**
	 * Clase con las propiedades de elementos de una lista
	 * 
	 * @author Equipo de Desarrollo - Disytel
	 * 
	 */
	public class HTMLListElement{
		public static final String TAG_LIST_ELEM = "li"; 
		
		private String id;
		private String value;

		public HTMLListElement(String id, String value){
			setId(id);
			setValue(value);
		}
		
		public void setId(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
		
		@Override
		public String toString(){
			StringBuffer listElem = new StringBuffer();
			listElem.append(TAG_OPEN).append(TAG_LIST_ELEM);
			if(!Util.isEmpty(getId())){
				listElem.append(BLANK).append(ID).append(EQUAL).append(getId());
			}
			listElem.append(TAG_CLOSE);
			if(!Util.isEmpty(getValue())){
				listElem.append(getValue());	
			}
			listElem.append(TAG_OPEN_CLOSE_ELEMENT).append(TAG_LIST_ELEM)
					.append(TAG_CLOSE);
			return listElem.toString();
		}
	}
	
}
