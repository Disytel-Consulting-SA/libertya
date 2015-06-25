package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.List;

import org.openXpertya.util.Util;

public class DeclaracionValoresDTO {

	/** Usuario operador de la caja */
	private Integer userID;
	
	/** Lista de Cajas diarias a consultar */
	private List<Integer> journalIDs;
	
	/** Lista de ids de cajas diarias en formato array para consultas sql */
	private String journalIDsSQLArray;
	
	/** Fecha de inicio */
	private Timestamp dateFrom;
	
	/** Fecha de fin */
	private Timestamp dateTo;
	
	/** ID de Organización */
	private Integer orgID;
	
	/**
	 * Método que permite actualizar el valor del array sql a partir de los ids
	 * de las cajas diarias en la variable de instancia journalIDsSQLArray
	 */
	public void setJournalIDsSQLArray(){
		setJournalIDsSQLArray(Util.isEmpty(getJournalIDs()) ? "array[-1]"
				: "ARRAY" + getJournalIDs());
	}
	
	// Getters y Setters

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setJournalIDs(List<Integer> journalIDs) {
		this.journalIDs = journalIDs;
		setJournalIDsSQLArray();
	}

	public List<Integer> getJournalIDs() {
		return journalIDs;
	}

	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getOrgID() {
		return orgID;
	}

	public void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	public String getJournalIDsSQLArray() {
		return journalIDsSQLArray;
	}

	public void setJournalIDsSQLArray(String journalIDsSQLArray) {
		this.journalIDsSQLArray = journalIDsSQLArray;
	}
	
}
