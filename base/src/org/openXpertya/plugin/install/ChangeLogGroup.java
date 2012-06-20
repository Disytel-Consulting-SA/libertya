package org.openXpertya.plugin.install;

import java.util.ArrayList;
import java.util.List;


public class ChangeLogGroup {

	// Variables de instancia
	
	/** Tabla */
	
	private String tableName;
	
	
	/** UID */
	
	private String ad_componentObjectUID;
	
	/** Operación del grupo de log */
	
	private String operation;
	
	/** Elementos del grupo */
	
	private List<ChangeLogElement> elements;
	
	
	// Constructores

	public ChangeLogGroup(int ad_table_id, String ad_componentObjectUID, String operation, String tableName){
		this.tableName = tableName;
		setAd_componentObjectUID(ad_componentObjectUID);
		setOperation(operation);
		setElements(new ArrayList<ChangeLogElement>());
	}

	// Varios
	
	/**
	 * Agrego un elemento al grupo de changelog.
	 * @param element elemento a agregar
	 */
	public void addElement(ChangeLogElement element){
		getElements().add(element);
	}
	
	@Override
	public String toString(){
		return tableName+"_"+getAd_componentObjectUID()+"_"+getOperation();
	}
	

	public void setAd_componentObjectUID(String ad_componentObjectUID) {
		this.ad_componentObjectUID = ad_componentObjectUID;
	}


	public String getAd_componentObjectUID() {
		return ad_componentObjectUID;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getOperation() {
		return operation;
	}

	public void setElements(List<ChangeLogElement> elements) {
		this.elements = elements;
	}

	public List<ChangeLogElement> getElements() {
		return elements;
	}

	public String getTableName() {
		return tableName;
	}
}
