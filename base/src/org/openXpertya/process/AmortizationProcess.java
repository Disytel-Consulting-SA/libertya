package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openXpertya.amortization.AbstractAmortizationLineData;
import org.openXpertya.amortization.AbstractAmortizationProcessor;
import org.openXpertya.amortization.AbstractAmortizationProcessorFactory;
import org.openXpertya.model.X_M_AmortizationLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.SQLBuilder;


public class AmortizationProcess extends SvrProcess {
	
	/** Amortización actual */
	private Integer amortizationID;
	
	/** SQL builder */
	private SQLBuilder sqlBuilder;
	
	@Override
	protected void prepare() {
		setAmortizationID(getRecord_ID());
		setSqlBuilder(new SQLBuilder(X_M_AmortizationLine.Table_ID,
				X_M_AmortizationLine.Table_Name));
		getSqlBuilder().addColumnNameValue("m_amortizationline_id",
				"nextval('seq_m_amortizationline')");
		getSqlBuilder().addColumnNameValue("isactive", "'Y'");
		getSqlBuilder().addColumnNameValue("created", "now()");
		getSqlBuilder().addColumnNameValue("createdby",
				String.valueOf(Env.getAD_User_ID(getCtx())));
		getSqlBuilder().addColumnNameValue("updated", "now()");
		getSqlBuilder().addColumnNameValue("updatedby",
				String.valueOf(Env.getAD_User_ID(getCtx())));
		getSqlBuilder().addColumnNameValue("processed","'Y'");
		
	}

	@Override
	protected String doIt() throws Exception {
		AbstractAmortizationProcessor amortizationProcessor = AbstractAmortizationProcessorFactory
				.getProcessor(getCtx(), getAmortizationID(), get_TrxName());
		amortizationProcessor.doAmortization();
		getSqlBuilder().addColumnNameValue(
				"ad_client_id",
				String.valueOf(amortizationProcessor.getAmortization()
						.getAD_Client_ID()));
		getSqlBuilder().addColumnNameValue(
				"ad_org_id",
				String.valueOf(amortizationProcessor.getAmortization()
						.getAD_Org_ID()));
		getSqlBuilder()
				.addColumnNameValue(
						"m_amortization_id",
						String.valueOf(getAmortizationID()));
		PreparedStatement ps = null;
		ResultSet rs = null;
		int inserts = 0;
		try{
			// Itero por los datos de las líneas de amortizaciones 
			for (AbstractAmortizationLineData amortizationLineData : amortizationProcessor.getAmortizationLines()) {
				getSqlBuilder().addColumnNameValue("m_product_id",
						String.valueOf(amortizationLineData.getProductID()));
				getSqlBuilder().addColumnNameValue("M_AttributeSetInstance_ID",
						String.valueOf(amortizationLineData.getAttributeSetInstanceID()));
				getSqlBuilder().addColumnNameValue("unitcost",
						String.valueOf(amortizationLineData.getUnitCost()));
				getSqlBuilder().addColumnNameValue("qty",
						String.valueOf(amortizationLineData.getQty()));
				getSqlBuilder().addColumnNameValue("totalcost",
						String.valueOf(amortizationLineData.getTotalCost()));
				getSqlBuilder().addColumnNameValue("iniperiodamortizationamt",
						String.valueOf(amortizationLineData.getAmortizationInitialAmt()));
				getSqlBuilder().addColumnNameValue("endperiodamortizationamt",
						String.valueOf(amortizationLineData.getAmortizationEndAmt()));
				getSqlBuilder().addColumnNameValue("amortizationamt",
						String.valueOf(amortizationLineData.getAmortizationAmt()));
				getSqlBuilder().addColumnNameValue("iniperiodresidualamt",
						String.valueOf(amortizationLineData.getResidualInitialAmt()));
				getSqlBuilder().addColumnNameValue("endperiodresidualamt",
						String.valueOf(amortizationLineData.getResidualEndAmt()));
				getSqlBuilder().addColumnNameValue("residualamt",
						String.valueOf(amortizationLineData.getResidualAmt()));
				getSqlBuilder().addColumnNameValue("altaamt",
						String.valueOf(amortizationLineData.getAlta()));
				getSqlBuilder().addColumnNameValue("bajaamt",
						String.valueOf(amortizationLineData.getBaja()));
				ps = DB.prepareStatement(getSqlBuilder().makeSQLInsert(), get_TrxName());
				inserts += ps.executeUpdate();
			}
			// Coloco como procesado la amortización
			DB.executeUpdate(
					"UPDATE m_amortization SET processed = 'Y', runamortizationprocess = 'Y' WHERE m_amortization_id = "
							+ getAmortizationID(), get_TrxName());
		} catch(SQLException sqle){
			throw new Exception(sqle.getMessage());
		} finally{
			try{
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(rs != null){
					rs.close();
					rs = null;
				}
			} catch(Exception e2){
				e2.printStackTrace();
			}
		}
		return inserts+" @AmortizationLinesInserted@";
	}

	protected void setAmortizationID(Integer amortizationID) {
		this.amortizationID = amortizationID;
	}

	protected Integer getAmortizationID() {
		return amortizationID;
	}

	protected void setSqlBuilder(SQLBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}

	protected SQLBuilder getSqlBuilder() {
		return sqlBuilder;
	}

}
