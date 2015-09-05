package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MAmortization;
import org.openXpertya.model.X_C_Period;
import org.openXpertya.model.X_C_Year;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ITime;

public class Doc_Amortization extends Doc {
	
	/** Montos agrupados entre cuentas contables realizadas de amortización */
	// NO UTILIZADO POR AHORA YA QUE SINO SE PIERDEN LAS REFERENCIAS A LINEAS Y ARTICULOS
	private Map<Integer, BigDecimal> realizedAmts;
	
	private MAmortization amortization;
	
	private ITime timePeriod;
	
	protected Doc_Amortization( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
        setRealizedAmts(new HashMap<Integer, BigDecimal>());
    }

	@Override
	protected boolean loadDocumentDetails(ResultSet rs) {
		try{
			// Amortization
			setAmortization(new MAmortization(getCtx(), getRecord_ID(), getTrxName()));
			// Período
			Integer periodID = 0;
			String periodTableName = null;
			if(getAmortization().getC_Period_ID() > 0){
				periodID = getAmortization().getC_Period_ID();
				periodTableName = X_C_Period.Table_Name;
			}
			else{
				periodID = getAmortization().getC_Year_ID();
				periodTableName = X_C_Year.Table_Name;
			}
			setTimePeriod(MAmortization.getITime(getCtx(), periodID, periodTableName, getTrxName()));
			
			// Cargar el tipo de documento ya que es requerido por las líneas
			if( p_vo.C_DocType_ID == 0 ) {
	            p_vo.C_DocType_ID = rs.getInt( "C_DocTypeTarget_ID" );
	        }
			loadDocumentType();

			// La fecha de contabilización es el último día del período
	        if( p_vo.DateAcct == null ) {
	        	p_vo.DateAcct = new Timestamp(getTimePeriod().getDateTo().getTime());
	        }

	        if( p_vo.DateDoc == null ) {
	        	p_vo.DateDoc = rs.getTimestamp("amortizationdate");
	        }
		} catch( SQLException e ) {
            log.log( Level.SEVERE,"amortization loadDocumentDetails",e );
        }
		
		p_lines = loadLines();
		if(p_lines == null || p_lines.length <= 0){
			log.log(Level.SEVERE,"no amortization lines");
			return false;
		}
		return true;
	}
	
	/**
	 * @return líneas de amortización
	 */
	protected DocLine_Amortization[] loadLines(){
		DocLine_Amortization[] lines;
		List<DocLine_Amortization> list_lines = new ArrayList<DocLine_Amortization>();
		String sql = "SELECT * FROM m_amortizationline WHERE m_amortization_id = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		DocLine_Amortization line = null;
		Integer lineID;
		try {
			ps = DB.prepareStatement(sql, getTrxName());
			ps.setInt(1, getRecord_ID());
			rs = ps.executeQuery();
			while(rs.next()){
				lineID = rs.getInt("M_AmortizationLine_ID");
				line = new DocLine_Amortization(p_vo.DocumentType, getRecord_ID(), lineID, getTrxName());
				line.loadAttributes(rs, p_vo);
				line.setAmount(rs.getBigDecimal("AmortizationAmt"), rs.getBigDecimal("AmortizationAmt"));
				list_lines.add(line);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE,"amortization loadLines",e );
            return null;
		} finally{
			try {
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(rs != null){
					rs.close();
					rs = null;
				}
			} catch (Exception e2) {
				log.log(Level.SEVERE,"amortization loadLines",e2 );
	            return null;
			}
		}
		lines = new DocLine_Amortization[list_lines.size()];
		list_lines.toArray(lines);
		list_lines = null;
		return lines;
	}
	
	@Override
	public BigDecimal getBalance() {
		return Env.ZERO;
	}

	@Override
	public Fact createFact(MAcctSchema as) {
		Fact fact = new Fact( this,as,Fact.POST_Actual );
		DocLine_Amortization amortizationLine = null;
		for (DocLine docLine : p_lines) {
			amortizationLine = (DocLine_Amortization)docLine; 
			// Crédito, cuenta de amortización
			fact.createLine(amortizationLine, amortizationLine.getAccount(
					ProductInfo.ACCTTYPE_P_Amortization, as),
					p_vo.C_Currency_ID, BigDecimal.ZERO, amortizationLine
							.getAmtSourceCr());
			// Débito, cuenta resultado de amortización
			fact.createLine(amortizationLine, amortizationLine.getAccount(
					ProductInfo.ACCTTYPE_P_Amortization_Realized, as),
					p_vo.C_Currency_ID, amortizationLine.getAmtSourceDr(),
					BigDecimal.ZERO);
		}
		return fact;
	}

	@Override
	public String applyCustomSettings( Fact fact, int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setRealizedAmts(Map<Integer, BigDecimal> realizedAmts) {
		this.realizedAmts = realizedAmts;
	}

	protected Map<Integer, BigDecimal> getRealizedAmts() {
		return realizedAmts;
	}

	protected void setAmortization(MAmortization amortization) {
		this.amortization = amortization;
	}

	protected MAmortization getAmortization() {
		return amortization;
	}

	protected void setTimePeriod(ITime timePeriod) {
		this.timePeriod = timePeriod;
	}

	protected ITime getTimePeriod() {
		return timePeriod;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
}
