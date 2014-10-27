package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.List;

public class DeclaracionValoresDTO {

	/** Usuario operador de la caja */
	private Integer userID;
	
	/** Lista de Cajas diarias a consultar */
	private List<Integer> journalIDs;
	
	/** Fecha de inicio */
	private Timestamp dateFrom;
	
	/** Fecha de fin */
	private Timestamp dateTo;
	
	/** ID de Organización */
	private Integer orgID;
	
	// Getters y Setters

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setJournalIDs(List<Integer> journalIDs) {
		this.journalIDs = journalIDs;
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
	
}
