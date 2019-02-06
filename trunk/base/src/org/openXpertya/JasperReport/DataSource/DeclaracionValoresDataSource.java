package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class DeclaracionValoresDataSource extends QueryDataSource {

	/** Datos necesarios para los datos */
	private DeclaracionValoresDTO valoresDTO;
	
	/** Contexto */
	private Properties ctx;

	
	public DeclaracionValoresDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresDataSource(Properties ctx,	DeclaracionValoresDTO valoresDTO, String trxName) {
		super(trxName);
		setCtx(ctx);
		setValoresDTO(valoresDTO);
	}
	
	protected String getDSFunView(String funViewName){
		return funViewName+"("+getValoresDTO().getJournalIDsSQLArray()+")";
	}
	
	public String getDSDataTable(){
		return getDSFunView("c_pos_declaracionvalores_v_filtered");
	}
	
	@Override
	protected String getQuery() {
		return null;
	}

	@Override
	protected Object[] getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeclaracionValoresDTO getValoresDTO() {
		return valoresDTO;
	}

	public void setValoresDTO(DeclaracionValoresDTO valoresDTO) {
		this.valoresDTO = valoresDTO;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
