package org.openXpertya.JasperReport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.JasperReport.DataSource.DeclaracionValoresDTO;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.RegisteredDocumentsDataSource;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchRegisteredDocuments extends JasperReportLaunch {

	/** Data Transfer Object para enviar a los Data Sources del reporte */
	private DeclaracionValoresDTO valoresDTO;
	
	@Override
	protected void loadReportParameters() throws Exception {
		List<Integer> journalIDs = getJournalsFromValues();
		if(Util.isEmpty(journalIDs)){
			throw new Exception(Msg.getMsg(getCtx(), "POSJournalInexistent"));
		}
		DeclaracionValoresDTO valoresDTO = new DeclaracionValoresDTO();
		valoresDTO.setJournalIDs(journalIDs);
		setValoresDTO(valoresDTO);
		// Parámetros adicionales
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
		addReportParameter("MSG_ALL_POS_JOURNAL", getAllPosJournalMsg());
		addReportParameter("FILTER_OPTION", getFilterOption());
		addReportParameter("FILTER_OPTION_NAME", getFilterOptionName());
		if(journalIDs.size() == 1){
			addReportParameter("POS_JOURNAL_NAME",
					JasperReportsUtil.getPODisplayByIdentifiers(getCtx(),
							journalIDs.get(0), X_C_POSJournal.Table_ID,
							get_TrxName()));
		}

	}

	protected String getAllPosJournalMsg(){
		return Msg.getMsg(getCtx(), "AllPOSJournals");
	}
	
	protected String getFilterOption(){
		return (String)getParameterValue("Filter");
	}
	
	protected String getFilterOptionName(){
		return JasperReportsUtil.getListName(getCtx(),
				getFilterOptionReferenceID(),
				(String) getParameterValue("Filter"));
	}
	
	protected Integer getFilterOptionReferenceID(){
		return DB
				.getSQLValue(
						get_TrxName(),
						"SELECT ad_reference_id FROM ad_reference WHERE name = 'Registered Documents Types' LIMIT 1");
	}
	
	protected List<Integer> getJournalsFromValues(){
		List<Integer> journalsIDs = new ArrayList<Integer>();
		StringBuffer where = new StringBuffer(" (ad_org_id = ?) ");
		Timestamp dateFrom = getDateFrom();
		Timestamp dateTo = getDateTo();
		Integer orgID = Env.getAD_Org_ID(getCtx());
		Integer posID = (Integer)getParameterValue("C_POS_ID");
		List<Object> params = new ArrayList<Object>();
		params.add(orgID);
		if(posID != null){
			where.append(" AND (c_pos_id = ?) ");
			params.add(posID);
		}
		if(dateFrom != null){
			where.append(" AND (date_trunc('day',datetrx) >= date_trunc('day',?::date)) ");
			params.add(dateFrom);
		}
		if(dateTo != null){
			where.append(" AND (date_trunc('day',datetrx) <= date_trunc('day',?::date)) ");
			params.add(dateTo);
		}
		
		if(!params.isEmpty()){
			List<PO> journals = PO.find(getCtx(), X_C_POSJournal.Table_Name,
					where.toString(), params.toArray(), null, get_TrxName());
			for (PO journal : journals) {
				journalsIDs.add(journal.getID());
			}
		}
		
		return journalsIDs;
	}
	
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected Timestamp getDateTo(){
		Timestamp dateTo = (Timestamp)getParameterValue("Date_To");
		if(dateTo == null){
			dateTo = Env.getDate();
		}
		return dateTo;
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new RegisteredDocumentsDataSource(getCtx(), getValoresDTO(),
				getFilterOption(), get_TrxName());
	}
	
	protected void setValoresDTO(DeclaracionValoresDTO valoresDTO) {
		this.valoresDTO = valoresDTO;
	}

	protected DeclaracionValoresDTO getValoresDTO() {
		return valoresDTO;
	}

}
