package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Util;


public abstract class DeclaracionValoresDataSource extends QueryDataSource {

	/** Datos necesarios para los datos */
	private DeclaracionValoresDTO valoresDTO;
	
	/** Contexto */
	private Properties ctx;
	
	public DeclaracionValoresDataSource(String trxName) {
		super(trxName);
	}
	
	public DeclaracionValoresDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String trxName) {
		super(trxName);
		setCtx(ctx);
		setValoresDTO(valoresDTO);
	}
	
	protected String getStdSelect(boolean withWhereClause){
		String select = "SELECT * FROM c_pos_declaracionvalores_v";
		if(withWhereClause){
			select += " WHERE ";
		}
		return select;
	}
	
	protected String getStdWhereClause(boolean withTenderType){
		return getStdWhereClause(withTenderType, null);
	}
	
	protected String getStdWhereClause(boolean withTenderType, String tableAlias){
		return getStdWhereClause(withTenderType, tableAlias, true);
	}

	protected String getStdWhereClause(boolean withTenderType, String tableAlias, boolean addDocStatus){
		StringBuffer stdWhere = new StringBuffer();
		if(!Util.isEmpty(tableAlias, true)){
			tableAlias += ".";
		}
		else{
			tableAlias = "";
		}
		if(addDocStatus){
			stdWhere.append(tableAlias).append("docstatus IN ('CO','CL') AND ");
		}
		if(!getValoresDTO().getJournalIDs().isEmpty()){
			stdWhere.append(tableAlias).append("c_posjournal_id IN ").append(getValoresDTO().getJournalIDs().toString()
					.replaceAll("]", ")").replaceAll("\\[", "("));
		}
		if (!Util.isEmpty(getValoresDTO().getUserID(), true)) {
			stdWhere.append(" AND ("+tableAlias+"ad_user_id = ?) ");
		}
		if(withTenderType){
			stdWhere.append(" AND ("+tableAlias+"tendertype IN (").append(getTenderType()).append(")) ");
		}
		return Util.removeInitialAND(stdWhere.toString());
	}
	
	protected Object[] getStdWhereClauseParams(){
		List<Object> params = new ArrayList<Object>();
		if (!Util.isEmpty(getValoresDTO().getUserID(), true)) {
			params.add(getValoresDTO().getUserID());
		}
		return params.toArray();
	}
	
	protected String getStdQuery(boolean withTenderType){
		StringBuffer sql = new StringBuffer(getStdSelect(true));
		sql.append(getStdWhereClause(withTenderType));
		return sql.toString();
	}
	
	/**
	 * @return Tipo de pago/cobro de la subclase particular
	 */
	protected abstract String getTenderType();
	
	protected void setValoresDTO(DeclaracionValoresDTO valoresDTO) {
		this.valoresDTO = valoresDTO;
	}

	protected DeclaracionValoresDTO getValoresDTO() {
		return valoresDTO;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}
}
