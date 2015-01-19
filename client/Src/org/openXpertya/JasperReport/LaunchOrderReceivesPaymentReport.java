package org.openXpertya.JasperReport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.JasperReport.DataSource.DeclaracionValoresDTO;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.OrderReceivesPaymentReportDataSource;
import org.openXpertya.model.MBPGroup;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchOrderReceivesPaymentReport extends JasperReportLaunch {

	/** Caja Diaria */
	private MPOSJournal posJournal;
	
	/** Data Transfer Object para enviar a los Data Sources del reporte */
	private DeclaracionValoresDTO valoresDTO;
	
	@Override
	protected void loadReportParameters() throws Exception {
		List<Integer> journalIDs = getJournalIDs();
		if(Util.isEmpty(journalIDs)){
			throw new Exception(Msg.getMsg(getCtx(), "POSJournalInexistent"));
		}
		DeclaracionValoresDTO valoresDTO = new DeclaracionValoresDTO();
		valoresDTO.setJournalIDs(journalIDs);
		valoresDTO.setDateFrom(getDateFrom());
		valoresDTO.setDateTo(getDateTo());
		valoresDTO.setOrgID(getOrgID());
		setValoresDTO(valoresDTO);
		// Fecha
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
		// POS
		Integer posID = getPOSID();
		if(!Util.isEmpty(posID, true)){
			MPOS pos = MPOS.get(getCtx(), posID);
			addReportParameter("POSNAME",pos.getName());
		}
		// Grupo de EC 
		if(!Util.isEmpty(getBPGroupID(), true)){
			MBPGroup group = MBPGroup.get(getCtx(), getBPGroupID());
			addReportParameter("BP_GROUP_VALUE", group.getValue());
			addReportParameter("BP_GROUP_NAME", group.getName());
		}
		// Transacción de ventas
		addReportParameter("ISSOTRX", getIsSOTrx());
		// Mostrar adelantados
		addReportParameter("SHOWPREPAYMENTS", getShowPrePayment());
	}

	protected Integer getBPGroupID(){
		return (Integer)getParameterValue("C_BP_Group_ID");
	}
	
	protected String getIsSOTrx(){
		return (String)getParameterValue("IsSOTrx");
	}
	
	protected String getShowPrePayment(){
		return (String)getParameterValue("ShowPrePayments");
	}
	
	protected List<Integer> getJournalIDs(){
		return getJournalsFromValues();
	}
	
	protected Integer getPOSID(){
		return (Integer)getParameterValue("C_POS_ID");
	}
	
	protected List<Integer> getJournalsFromValues(){
		List<Integer> journalsIDs = new ArrayList<Integer>();
		StringBuffer where = new StringBuffer(" (1=1) ");
		Timestamp dateFrom = getDateFrom();
		Timestamp dateTo = getDateTo();
		Integer orgID = getOrgID();
		Integer posID = getPOSID();
		Boolean isRange = getParameterValue("IsRange") == null ? false
				: getParameterValue("IsRange").equals("Y");
		List<Object> params = new ArrayList<Object>();
		if(orgID != null){
			where.append(" AND (ad_org_id = ?) ");
			params.add(orgID);
		}
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
		else if(!isRange){
			where.append(" AND (datetrx <= date_trunc('day',?::date)) ");
			params.add(dateFrom);
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
		return (Timestamp) getParameterValue("DateFrom");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("DateFrom_To");
	}
	
	protected Integer getOrgID(){
		return getParameterValue("AD_Org_ID") != null ? (Integer) getParameterValue("AD_Org_ID")
				: Env.getAD_Org_ID(getCtx());
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new OrderReceivesPaymentReportDataSource(getCtx(), getOrgID(),
				getIsSOTrx().equals("Y"), getShowPrePayment().equals("Y"), getValoresDTO(), 
				getPOSID(),	getDateFrom(), getDateTo(), getBPGroupID(), get_TrxName());
	}

	protected void setPosJournal(MPOSJournal posJournal) {
		this.posJournal = posJournal;
	}

	protected MPOSJournal getPosJournal() {
		return posJournal;
	}
	
	protected void setValoresDTO(DeclaracionValoresDTO valoresDTO) {
		this.valoresDTO = valoresDTO;
	}

	protected DeclaracionValoresDTO getValoresDTO() {
		return valoresDTO;
	}
}
