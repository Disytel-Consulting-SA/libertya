package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MAcctBalance;
import org.openXpertya.util.DB;

public class AccountsGeneralBalance extends AccountsHierarchicalReport {

	/** Fecha inicial del rango de fechas de la transacción */
	protected Timestamp  p_DateAcct_From;
	/** Fecha final del rango de fechas de la transacción */
	protected Timestamp  p_DateAcct_To;
	/** Booleano que determina si actualizar el balance o no */
	protected boolean updateBalance = true; 
	
	@Override
	protected boolean loadParameter(String name, ProcessInfoParameter param) {
		if( name.equalsIgnoreCase( "DateAcct" )) {
			p_DateAcct_From = ( Timestamp )param.getParameter();
			p_DateAcct_To = ( Timestamp )param.getParameter_To();
			return true;
		}
		return false;
	}
	
	@Override
	protected String doIt() throws Exception {
		// Procesamiento de AccountsHierarchicalReport
		super.doIt();
		
		StringBuffer sqlView = new StringBuffer();
		sqlView.append(" SELECT ev.C_ElementValue_ID, tb.HierarchicalCode, COALESCE(SUM(fa.AmtAcctDr),0) as AmtAcctDr, COALESCE(SUM(fa.AmtAcctCr),0) as AmtAcctCr "); 
		sqlView.append(" FROM C_ElementValue ev ");
		sqlView.append(" LEFT JOIN Fact_Acct fa ON (fa.Account_ID = ev.C_ElementValue_ID) ");
		sqlView.append(" INNER JOIN " + getReportTableName() + " tb ON (tb.C_ElementValue_ID = ev.C_ElementValue_ID AND tb.AD_PInstance_ID = ?) ");
		sqlView.append(" WHERE ev.IsActive = 'Y' ");
		sqlAppend     ("   AND fa.AD_Org_ID = ? ", p_AD_Org_ID > 0, sqlView);
		sqlAppend     ("   AND ? <= fa.DateAcct ", p_DateAcct_From, sqlView);
		sqlAppend     ("   AND fa.DateAcct <= ? ", p_DateAcct_To, sqlView);
		sqlView.append(" GROUP BY ev.C_ElementValue_ID, tb.HierarchicalCode ");
		
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" UPDATE " + getReportTableName() + " t ");
		sql.append(" SET Debit = ( ");
		sql.append(" 	    SELECT COALESCE(SUM(AmtAcctDr),0.0) AS Debit ");
		sql.append(" 	    FROM ( ").append(sqlView).append(" ) v ");
		sql.append(" 	    WHERE  v.HierarchicalCode LIKE t.HierarchicalCode || '%' ");
		sql.append("     ), ");
		sql.append("     Credit = ( ");
		sql.append(" 	    SELECT COALESCE(SUM(AmtAcctCr),0.0) AS Credit ");
		sql.append(" 	    FROM ( ").append(sqlView).append(" ) v ");
		sql.append(" 	    WHERE  v.HierarchicalCode LIKE t.HierarchicalCode || '%' ");
		sql.append("     ) ");
		sql.append(" WHERE t.AD_PInstance_ID = ? ");
		
		
		StringBuffer sqlUpdateBalance = new StringBuffer();
		sqlUpdateBalance.append(" UPDATE ").append(getReportTableName());
		sqlUpdateBalance.append(" SET Balance = Debit - Credit ");
		sqlUpdateBalance.append(" WHERE AD_PInstance_ID = ? ");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// Actualización de debe y haber de los registros existentes
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			Integer auxAD_Org_ID = p_AD_Org_ID == 0 ? null : p_AD_Org_ID;
			int i = 1;
			// Parametros del primer sqlView
			pstmt.setInt     (i++, getAD_PInstance_ID());
			i = pstmtSetParam(i, auxAD_Org_ID, pstmt);
			i = pstmtSetParam(i, p_DateAcct_From, pstmt);
			i = pstmtSetParam(i, p_DateAcct_To, pstmt);
			// Parametros del segundo sqlView
			pstmt.setInt     (i++, getAD_PInstance_ID());
			i = pstmtSetParam(i, auxAD_Org_ID, pstmt);
			i = pstmtSetParam(i, p_DateAcct_From, pstmt);
			i = pstmtSetParam(i, p_DateAcct_To, pstmt);
			// Parámetros de sql
			pstmt.setInt(i++, getAD_PInstance_ID());
			
			int no = pstmt.executeUpdate();
			log.fine("T_Acct_Balance Debit/Credit update OK = " + no);
			
			// Actualización de saldo
			if(updateBalance){
				// Actualización del saldo en base al debe y el haber
				pstmt = DB.prepareStatement(sqlUpdateBalance.toString(), get_TrxName());
				i = 1;
				pstmt.setInt(i++, getAD_PInstance_ID());
				no = pstmt.executeUpdate();
				log.fine("T_Acct_Balance Balance update OK = " + no);
			}
			
	    } catch (SQLException e) {
			log.log(Level.SEVERE, "Calculate Debit/Credit T_Acct_Balance error", e);
			throw new Exception("@ProcessRunError@",e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}

		clearDateAcct();
		
		return null;
	}

	@Override
	protected void createReportLine(AccountElement accountElement) throws Exception {
		MAcctBalance line = new MAcctBalance(getCtx(), 0, get_TrxName());
		
		line.setAD_PInstance_ID(getAD_PInstance_ID());
		line.setSubindex(accountElement.subindex);
		line.setC_ElementValue_ID(accountElement.elementValueID);
		line.setAcct_Code(accountElement.code);
		line.setAcct_Description(accountElement.description);
		line.setAD_Org_ID(accountElement.orgID);
		line.setHierarchicalCode(accountElement.hierarchicalCode);
		// El Debe y Haber se calculan masivamente en el doIt. 
		line.setDebit(null);
		line.setCredit(null);

		if (!line.save()) {
			log.severe("Cannot save X_T_Acct_Balance line. C_ElementValue_ID=" + line.getC_ElementValue_ID());
			throw new Exception("@ProcessRunError@");
		}
		
		//System.out.println("(" + internalCode + ") " + accountElement.description);
	}
	
	protected void clearDateAcct() {
		DB.executeUpdate("UPDATE AD_PInstance_Para SET p_date = null, p_date_to = null WHERE parametername = 'DateAcct' AND AD_PInstance_ID = " + getAD_PInstance_ID());		
	}
	
	@Override
	protected String getReportTableName() {
		return "T_ACCT_Balance";
	}
}
