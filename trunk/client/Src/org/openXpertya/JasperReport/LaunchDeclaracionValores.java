package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCashDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCheckDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCreditNoteDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCuentaCorrienteDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresCuponDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresDTO;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresTransferDataSource;
import org.openXpertya.JasperReport.DataSource.DeclaracionValoresVentasDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperEmptyDataSource;
import org.openXpertya.JasperReport.DataSource.ValoresDataSource;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchDeclaracionValores extends JasperReportLaunch {

	/** Caja Diaria */
	private MPOSJournal posJournal;
	
	/** Data Transfer Object para enviar a los Data Sources del reporte */
	private DeclaracionValoresDTO valoresDTO;
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new OXPJasperEmptyDataSource();
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		initialize();
		// Crear el dto para enviar los datos a los datasources
		Integer userID = getUserID();
		List<Integer> journalIDs = getJournalIDs();
		DeclaracionValoresDTO valoresDTO = new DeclaracionValoresDTO();
		valoresDTO.setJournalIDs(journalIDs);
		valoresDTO.setUserID(userID);
		setValoresDTO(valoresDTO);
		// Obtener los subreportes junto con sus datasources
		// Datasources
		//////////////////////////////////////
		// Data Source de Ventas
		DeclaracionValoresVentasDataSource ventaDS = getVentaDataSource();
		// Data Source de Efectivo
		DeclaracionValoresCashDataSource cashDS = getCashDataSource();
		// Data Source de Cheque
		DeclaracionValoresCheckDataSource checkDS = getCheckDataSource();
		// Data Source de Transferencia
		DeclaracionValoresTransferDataSource transferDS = getTransferDataSource();
		// Data Source de Cupon
		DeclaracionValoresCuponDataSource cuponDS = getCuponDataSource();
		// Data Source de NC
		DeclaracionValoresCreditNoteDataSource creditNoteDS = getCreditNoteDataSource();
		// Data Source de Cuenta corriente
		DeclaracionValoresCuentaCorrienteDataSource ccDS = getCuentaCorrienteDataSource();
		// Data Source de Valores
		ValoresDataSource valoresDS = getValoresDataSource();
		//////////////////////////////////////
		// Subreporte de totales por tipo de transacción
		MJasperReport trxSubreport = getDeclaracionValoresSubreport(); 
		// Se agrega el informe compilado como parámetro.
		addReportParameter("COMPILED_SUBREPORT_TYPE_VALUES", new ByteArrayInputStream(trxSubreport.getBinaryData()));
		// Se agregan los datasources de los subreportes
		addReportParameter("SUBREPORT_SALE_DATASOURCE", ventaDS);
		addReportParameter("SUBREPORT_CASH_DATASOURCE", cashDS);
		addReportParameter("SUBREPORT_CHECKS_DATASOURCE", checkDS);
		addReportParameter("SUBREPORT_TRANSFER_DATASOURCE", transferDS);
		addReportParameter("SUBREPORT_CUPON_DATASOURCE", cuponDS);
		addReportParameter("SUBREPORT_CREDIT_NOTE_DATASOURCE", creditNoteDS);
		addReportParameter("SUBREPORT_CURRENTACCOUNT_DATASOURCE", ccDS);
		//////////////////////////////////////
		// Subreporte de valores
		MJasperReport valoresSubreport = getValoresSubreport(); 
		// Se agrega el informe compilado como parámetro.
		addReportParameter("COMPILED_SUBREPORT_VALORES", new ByteArrayInputStream(valoresSubreport.getBinaryData()));
		// Se agregan los datasources de los subreportes
		addReportParameter("SUBREPORT_VALORES_DATASOURCE", valoresDS);
		//////////////////////////////////////
		// Parámetros adicionales
		addReportParameter("TITLE", "Declaracion de Valores");
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
		if(!Util.isEmpty(userID, true)){
			addReportParameter("USER_NAME", JasperReportsUtil.getUserName(
					getCtx(), userID, get_TrxName()));
		}
		if(journalIDs.size() == 1){
			addReportParameter("TPV_NAME",
					JasperReportsUtil.getPODisplayByIdentifiers(getCtx(),
							journalIDs.get(0), X_C_POSJournal.Table_ID,
							get_TrxName()));
		}
		addReportParameter("SHOW_CURRENT_ACCOUNT", isShowCurrentAccount());
		addReportParameter("SHOW_CURRENT_ACCOUNT_TRUE_DESCRIPTION", Msg.getMsg(getCtx(), "ShowCurrentAccount"));
		addReportParameter("SHOW_CURRENT_ACCOUNT_FALSE_DESCRIPTION", Msg.getMsg(getCtx(), "DoNotShowCurrentAccount"));
		addReportParameter("SHOW_DETAILS", isShowDetail());
		addReportParameter("SHOW_DETAILS_TRUE_DESCRIPTION", Msg.getMsg(getCtx(), "ShowDetails"));
		addReportParameter("SHOW_DETAILS_FALSE_DESCRIPTION", Msg.getMsg(getCtx(), "DoNotShowDetails"));
	}

	
	protected void initialize(){
		if (!Util.isEmpty(getTable_ID(), true)
				&& !Util.isEmpty(getRecord_ID(), true)) {
			M_Table table = M_Table.get(getCtx(), getTable_ID());
			setPosJournal((MPOSJournal)table.getPO(getRecord_ID(), get_TrxName()));
		}
	}
	
	protected Boolean isShowDetail(){
		Boolean showDetail = false;
		if(getPosJournal() != null){
			showDetail = true;
		}
		else{
			showDetail = getParameterValue("ShowDetail") != null ? getParameterValue(
					"ShowDetail").equals("Y")
					: showDetail;		
		}
		return showDetail;
	}
	
	protected List<Integer> getJournalIDs(){
		List<Integer> journalIDs = new ArrayList<Integer>();
		if(getPosJournal() != null){
			journalIDs.add(getPosJournal().getID());
		}
		else {
			journalIDs = getJournalsFromValues();
		}
		return journalIDs;
	}
	
	protected List<Integer> getJournalsFromValues(){
		List<Integer> journalsIDs = new ArrayList<Integer>();
		StringBuffer where = new StringBuffer(" (1=1) ");
		Timestamp dateFrom = getDateFrom();
		Timestamp dateTo = getDateTo();
		Integer orgID = getOrgID();
		Integer posID = (Integer)getParameterValue("C_POS_ID");
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
			where.append(" AND (datetrx >= date_trunc('day',?::date)) ");
			params.add(dateFrom);
		}
		if(dateTo != null){
			where.append(" AND (datetrx <= date_trunc('day',?::date)) ");
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
		Timestamp date = null;
		if(getPosJournal() != null){
			date = getPosJournal().getDateTrx();
		}
		else{
			date = (Timestamp)getParameterValue("DateFrom") != null?(Timestamp)getParameterValue("DateFrom"):date;		
		}
		return date;
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("DateFrom_To");
	}
	
	protected Integer getUserID(){
		Integer userID = null;
		if(getPosJournal() != null){
			userID = getPosJournal().getAD_User_ID();
		}
		else{
			userID = getParameterValue("AD_User_ID") != null?(Integer)getParameterValue("AD_User_ID"):userID;		
		}
		return userID;
	}
	
	protected Boolean isShowCurrentAccount(){
		Boolean showCurrentAccount = false;
		if(getPosJournal() != null){
			showCurrentAccount = true;
		}
		else{
			showCurrentAccount = getParameterValue("ShowCurrentAccount") != null ? getParameterValue(
					"ShowCurrentAccount").equals("Y")
					: showCurrentAccount;		
		}
		return showCurrentAccount;
	}
	
	protected Integer getOrgID(){
		Integer orgID = null;
		if(getPosJournal() != null){
			orgID = getPosJournal().getAD_Org_ID();
		}
		else{
			orgID = getParameterValue("AD_Org_ID") != null?(Integer)getParameterValue("AD_Org_ID"):orgID;		
		}
		return orgID;
	}
	
	protected DeclaracionValoresVentasDataSource getVentaDataSource() throws Exception{
		DeclaracionValoresVentasDataSource ventaDS = new DeclaracionValoresVentasDataSource(
				getValoresDTO(), get_TrxName());
		return (DeclaracionValoresVentasDataSource)loadDSData(ventaDS);
	}
	
	protected DeclaracionValoresCashDataSource getCashDataSource() throws Exception{
		DeclaracionValoresCashDataSource cashDS = new DeclaracionValoresCashDataSource(
				getValoresDTO(), get_TrxName());
		return (DeclaracionValoresCashDataSource)loadDSData(cashDS);
	}
	
	protected DeclaracionValoresCheckDataSource getCheckDataSource() throws Exception{
		DeclaracionValoresCheckDataSource checkDS = new DeclaracionValoresCheckDataSource(
				getValoresDTO(), get_TrxName());
		return (DeclaracionValoresCheckDataSource)loadDSData(checkDS);
	}
	
	protected DeclaracionValoresTransferDataSource getTransferDataSource() throws Exception{
		DeclaracionValoresTransferDataSource transferDS = new DeclaracionValoresTransferDataSource(
				getValoresDTO(), get_TrxName());
		return (DeclaracionValoresTransferDataSource)loadDSData(transferDS);
	}
	
	protected DeclaracionValoresCuponDataSource getCuponDataSource() throws Exception{
		DeclaracionValoresCuponDataSource cuponDS = new DeclaracionValoresCuponDataSource(
				getValoresDTO(), get_TrxName());
		return (DeclaracionValoresCuponDataSource)loadDSData(cuponDS);
	}
	
	protected DeclaracionValoresCreditNoteDataSource getCreditNoteDataSource() throws Exception{
		DeclaracionValoresCreditNoteDataSource creditNoteDS = new DeclaracionValoresCreditNoteDataSource(
				getValoresDTO(), get_TrxName());
		return (DeclaracionValoresCreditNoteDataSource)loadDSData(creditNoteDS);
	}
	
	protected DeclaracionValoresCuentaCorrienteDataSource getCuentaCorrienteDataSource() throws Exception{
		DeclaracionValoresCuentaCorrienteDataSource ccDS = new DeclaracionValoresCuentaCorrienteDataSource(
				getValoresDTO(), get_TrxName());
		return (DeclaracionValoresCuentaCorrienteDataSource)loadDSData(ccDS);
	}
	
	protected ValoresDataSource getValoresDataSource() throws Exception{
		ValoresDataSource valoresDS = new ValoresDataSource(
				getValoresDTO(), get_TrxName());
		return (ValoresDataSource)loadDSData(valoresDS);
	}
	
	protected DeclaracionValoresDataSource loadDSData(DeclaracionValoresDataSource ds) throws Exception{
		ds.loadData();
		return ds;
	}
	
	/**
	 * @return el subreporte de totales por tipo
	 */
	protected MJasperReport getDeclaracionValoresSubreport() throws Exception{
		return getJasperReport(getCtx(), "DeclaracionValores-Subreporte", get_TrxName());
	}
	
	/**
	 * @return el subreporte de valores
	 */
	protected MJasperReport getValoresSubreport() throws Exception{
		return getJasperReport(getCtx(), "Valores-Subreporte", get_TrxName());
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
