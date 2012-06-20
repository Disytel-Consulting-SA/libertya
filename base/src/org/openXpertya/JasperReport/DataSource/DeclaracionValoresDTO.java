package org.openXpertya.JasperReport.DataSource;

import java.util.List;

public class DeclaracionValoresDTO {

	
	/** Usuario operador de la caja */
	private Integer userID;
	
	/** Lista de Cajas diarias a consultar */
	private List<Integer> journalIDs;
	
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
	
}
