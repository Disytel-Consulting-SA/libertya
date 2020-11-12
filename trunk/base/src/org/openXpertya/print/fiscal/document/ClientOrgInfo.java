package org.openXpertya.print.fiscal.document;

import java.sql.Timestamp;

public class ClientOrgInfo {

	/** Nombre de la compañía */
	private String clientName;
	/** Nombre de la organización */
	private String orgName;
	/** Dirección */
	private String address;
	/** Ciudad */
	private String city;
	/** Provincia */
	private String regionName;
	/** CUIT */
	private String cuit;
	/** Ingresos Brutos */
	private String iibb;
	/** Nombre de Categoría de IVA de la compañía */
	private String categoriaIVA;
	/** Inicio de Actividades */
	private Timestamp activityStartDate;
	
	public ClientOrgInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getIIBB() {
		return iibb;
	}

	public void setIIBB(String iibb) {
		this.iibb = iibb;
	}

	public String getCategoriaIVA() {
		return categoriaIVA;
	}

	public void setCategoriaIVA(String categoriaIVA) {
		this.categoriaIVA = categoriaIVA;
	}

	public Timestamp getActivityStartDate() {
		return activityStartDate;
	}

	public void setActivityStartDate(Timestamp activityStartDate) {
		this.activityStartDate = activityStartDate;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

}
