package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MAcctBalance;
import org.openXpertya.model.MInflationIndex;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class AccountsGeneralBalance extends AccountsHierarchicalReport {

	/** Fecha inicial del rango de fechas de la transacción */
	protected Timestamp  p_DateAcct_From;
	/** Fecha final del rango de fechas de la transacción */
	protected Timestamp  p_DateAcct_To;
	/** Booleano que determina si actualizar el balance o no */
	protected boolean updateBalance = true;
	/** Booleano que determina si se debe mostrar el saldo aplicando el ajuste por inflación */
	protected boolean applyInflationIndexes = false;
	/** Tabla origen de los datos */
	protected String p_factAcctTable = "Fact_Acct";
	
	@Override
	protected boolean loadParameter(String name, ProcessInfoParameter param) {
		if( name.equalsIgnoreCase( "DateAcct" )) {
			p_DateAcct_From = ( Timestamp )param.getParameter();
			p_DateAcct_To = ( Timestamp )param.getParameter_To();
			return true;
		}
		if(name.equalsIgnoreCase( "ApplyInflationIndex" )){
			applyInflationIndexes = ((String)param.getParameter()).equals("Y");
			return true;
		}
		if( name.equalsIgnoreCase( "FactAcctTable" )) {
			p_factAcctTable = (String)param.getParameter();
			return true;
		}
		return false;
	}
	
	@Override
	protected String doIt() throws Exception {
		// Procesamiento de AccountsHierarchicalReport
		super.doIt();
		
		// Obtener el índice de inflación entre la fecha desde y hasta
		MInflationIndex inflationIndex = getInflationIndex();
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" UPDATE " + getReportTableName() + " t ");
		sql.append(" SET Debit = ( ");
		sql.append(" 	    SELECT COALESCE(SUM(thesum),0.0) AS Debit ");
		
		sql.append(" 	    FROM ( ").append(getSQLView("AmtAcctDr", false)).append(" ) v ");
		sql.append(" 	    WHERE  v.HierarchicalCode LIKE t.HierarchicalCode || '%' ");
		sql.append("     ), ");
		sql.append("     Credit = ( ");
		sql.append(" 	    SELECT COALESCE(SUM(thesum),0.0) AS Credit ");
		
		sql.append(" 	    FROM ( ").append(getSQLView("AmtAcctCr", false)).append(" ) v ");
		sql.append(" 	    WHERE  v.HierarchicalCode LIKE t.HierarchicalCode || '%' ");
		sql.append("     ) ");
		
		sql.append(" WHERE t.AD_PInstance_ID = ? ");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// Actualización de debe y haber de los registros existentes
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName(), true);
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
			
			// Actualizo el débito y crédito ajustado
			StringBuffer sqlUpdateBalance = new StringBuffer();
			sqlUpdateBalance.append(" UPDATE ").append(getReportTableName()).append(" t ");
			sqlUpdateBalance.append(" SET DebitAdjusted = Debit, CreditAdjusted = Credit ");
			sqlUpdateBalance.append(" WHERE AD_PInstance_ID = ? ");
			i = 1;
			pstmt = DB.prepareStatement(sqlUpdateBalance.toString(), get_TrxName(), true);
			pstmt.setInt     (i++, getAD_PInstance_ID());
			no = pstmt.executeUpdate();
			
			// Actualización de saldo
			if(updateBalance){
				// Actualizar el saldo ajustado
				if(applyInflationIndexes) {
					sqlUpdateBalance = new StringBuffer();
					sqlUpdateBalance.append(" UPDATE ").append(getReportTableName()).append(" t ");
					sqlUpdateBalance.append(" SET DebitAdjusted = DebitAdjusted + ");
					sqlUpdateBalance.append(" 	    (SELECT COALESCE(SUM(thesum),0.0) AS thesum ");
					
					sqlUpdateBalance.append(" 	    FROM ( ")
							.append(getSQLView("AmtAcctDr * (CASE WHEN "
									+ inflationIndex.getInflationIndex() + " = 0 THEN 0 ELSE ((inflationindex - "
									+ inflationIndex.getInflationIndex() + ") / " + inflationIndex.getInflationIndex()
									+ ") END)", true))
							.append(" ) v ");
					sqlUpdateBalance.append(" 	    WHERE  v.HierarchicalCode LIKE t.HierarchicalCode || '%' ");
					sqlUpdateBalance.append("     ), ");
					sqlUpdateBalance.append(" CreditAdjusted = CreditAdjusted + ");
					sqlUpdateBalance.append(" 	    (SELECT COALESCE(SUM(thesum),0.0) AS thesum ");
					
					sqlUpdateBalance.append(" 	    FROM ( ")
					.append(getSQLView("AmtAcctCr * (CASE WHEN "
									+ inflationIndex.getInflationIndex() + " = 0 THEN 0 ELSE ((inflationindex - "
									+ inflationIndex.getInflationIndex() + ") / " + inflationIndex.getInflationIndex()
									+ ") END) ", true))
							.append(" ) v ");
					sqlUpdateBalance.append(" 	    WHERE  v.HierarchicalCode LIKE t.HierarchicalCode || '%' ");
					sqlUpdateBalance.append("     ) ");
					sqlUpdateBalance.append(" WHERE AD_PInstance_ID = ? AND t.isadjustable = 'Y' ");
					pstmt = DB.prepareStatement(sqlUpdateBalance.toString(), get_TrxName(), true);
					// Parametros del primer sqlView
					i = 1;
					pstmt.setInt     (i++, getAD_PInstance_ID());
					i = pstmtSetParam(i, auxAD_Org_ID, pstmt);
					i = pstmtSetParam(i, p_DateAcct_From, pstmt);
					i = pstmtSetParam(i, p_DateAcct_To, pstmt);
					// Parametros del segundo sqlView
					pstmt.setInt     (i++, getAD_PInstance_ID());
					i = pstmtSetParam(i, auxAD_Org_ID, pstmt);
					i = pstmtSetParam(i, p_DateAcct_From, pstmt);
					i = pstmtSetParam(i, p_DateAcct_To, pstmt);
					pstmt.setInt(i++, getAD_PInstance_ID());
					no = pstmt.executeUpdate();
					log.fine("T_Acct_Balance Adjusted Amts update OK = " + no);
				}
				else {
					
				}
				// Actualización del SALDO en base al debe y el haber
				sqlUpdateBalance = new StringBuffer();
				sqlUpdateBalance.append(" UPDATE ").append(getReportTableName());
				sqlUpdateBalance.append(" SET Balance = Debit - Credit, ");
				sqlUpdateBalance.append(" BalanceAdjusted = DebitAdjusted - CreditAdjusted ");
				if (p_C_ElementValue_To_ID != null) {
					sqlUpdateBalance.append(", ");
					sqlUpdateBalance.append(" c_elementvalue_to_id = " + p_C_ElementValue_To_ID );
				}
				sqlUpdateBalance.append(" WHERE AD_PInstance_ID = ? ");
				pstmt = DB.prepareStatement(sqlUpdateBalance.toString(), get_TrxName(), true);
				i = 1;
				pstmt.setInt(i++, getAD_PInstance_ID());
				no = pstmt.executeUpdate();
				log.fine("T_Acct_Balance Balance update OK = " + no);
				
				// Ajustar el saldo del totalizado, sería el índice 0
				sqlUpdateBalance = new StringBuffer();
				sqlUpdateBalance.append(" UPDATE ").append(getReportTableName());
				sqlUpdateBalance.append(" SET BalanceAdjusted = (select sum(BalanceAdjusted) FROM ").append(getReportTableName()).append(" t JOIN c_elementvalue ev on ev.c_elementvalue_id = t.c_elementvalue_id WHERE AD_PInstance_ID = ? and subindex <> 0 and ev.issummary = 'N'), ");
				sqlUpdateBalance.append(" Balance = (select sum(Balance) FROM ").append(getReportTableName()).append(" t JOIN c_elementvalue ev on ev.c_elementvalue_id = t.c_elementvalue_id WHERE AD_PInstance_ID = ? and subindex <> 0 and ev.issummary = 'N') ");
				sqlUpdateBalance.append(" WHERE AD_PInstance_ID = ? and subindex = 0 ");
				pstmt = DB.prepareStatement(sqlUpdateBalance.toString(), get_TrxName(), true);
				i = 1;
				pstmt.setInt(i++, getAD_PInstance_ID());
				pstmt.setInt(i++, getAD_PInstance_ID());
				pstmt.setInt(i++, getAD_PInstance_ID());
				no = pstmt.executeUpdate();
				log.fine("T_Acct_Balance Balance Adjusted subindex 0 update OK = " + no);
			}
			
			if(p_C_ElementValue_To_ID != null)
				pstmt = DB.prepareStatement("UPDATE T_Acct_Balance SET C_ElementValue_ID = " + p_C_ElementValue_ID + ", C_ElementValue_To_ID = " + p_C_ElementValue_To_ID + " WHERE AD_PInstance_ID = " + getAD_PInstance_ID(), get_TrxName());
			else 
				pstmt = DB.prepareStatement("UPDATE T_Acct_Balance SET C_ElementValue_ID = " + p_C_ElementValue_ID + " WHERE AD_PInstance_ID = " + getAD_PInstance_ID(), get_TrxName());
			no = pstmt.executeUpdate();
			log.fine("T_Acct_Balance C_ElementValue_ID update OK = " + no);
			
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

	protected void initializeListElementValueID() throws Exception {
		// En caso de un rango de cuentas, realizo una query para averiguar los ids de los 
		// C_ElementValue_ID en cuestion y los cargo en la lista de Element Values --> listElementValueId
		listElementValueId = new ArrayList<Integer>();
		if (p_C_ElementValue_To_ID != null) {
			
			if (p_C_ElementValue_ID == null){
				// Tomo el primer C_ElementValue_ID
				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT ev.C_ElementValue_ID ");
				sql.append(" FROM AD_ClientInfo ci ");
				sql.append(" INNER JOIN C_AcctSchema_Element se ON (ci.C_AcctSchema1_ID = se.C_AcctSchema_ID) ");
				sql.append(" INNER JOIN C_Element e ON (se.C_Element_ID = e.C_Element_ID) ");
				sql.append(" INNER JOIN C_ElementValue ev ON (e.C_Element_ID = ev.C_Element_ID) ");
				sql.append(" WHERE se.ElementType = 'AC' AND ci.AD_Client_ID = ? ");
				sql.append(" ORDER BY value ASC ");
				sql.append(" LIMIT 1 ");
				
				p_C_ElementValue_ID = DB.getSQLValue(get_TrxName(), sql.toString(), getAD_Client_ID());
			}
			
			StringBuffer sql_range = new StringBuffer();
			
			sql_range.append("	SELECT C_ElementValue_ID");		
			sql_range.append("	FROM C_ElementValue");
			sql_range.append("	WHERE IsActive = 'Y'");
			sql_range.append("	AND Name BETWEEN");
				sql_range.append("	(SELECT name");
				sql_range.append("	FROM c_elementvalue");
				sql_range.append("	WHERE c_elementvalue_id= ").append(p_C_ElementValue_ID).append( ") AND");
				sql_range.append("	(SELECT name");
				sql_range.append("	FROM c_elementvalue");
				sql_range.append("	WHERE c_elementvalue_id= ").append(p_C_ElementValue_To_ID).append( " )");
			sql_range.append("Order By Value");
				
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = DB.prepareStatement(sql_range.toString(), get_TrxName());
				rs = pstmt.executeQuery();
				while(rs.next()){
					listElementValueId.add(rs.getInt("C_ElementValue_ID"));
				}
			}
			catch (SQLException e) {
				log.log(Level.SEVERE, "Query sql_range error", e);
				throw new Exception("@ProcessRunError@",e);
			}
			finally {
				try {
					if (rs != null) rs.close();
					if (pstmt != null) pstmt.close();
				} catch (Exception e) {}
			}			
		} else {
			// En caso de no ser un rango, tomo el id de la cuenta. 
			listElementValueId.add(p_C_ElementValue_ID);
		}
	}
	
	@Override
	protected void createReportLine(AccountElement accountElement) throws Exception {
		MAcctBalance line = new MAcctBalance(getCtx(), 0, get_TrxName());
		
		line.setAD_PInstance_ID(getAD_PInstance_ID());
		line.setSubindex(accountElement.subindex);
		line.setC_ElementValue_ID(accountElement.elementValueID);
		line.setAcct_Code(accountElement.code);
		line.setAcct_Description(accountElement.description);
		//line.setAD_Org_ID(accountElement.orgID);
		line.setAD_Org_ID(p_AD_Org_ID);
		line.setHierarchicalCode(accountElement.hierarchicalCode);
		line.setApplyInflationIndex(applyInflationIndexes);
		
		// Ajustar por índice de inflación
		line.setIsAdjustable(applyInflationIndexes?accountElement.adjustable:false);
		line.setFactAcctTable(p_factAcctTable);
		
		// El Debe y Haber se calculan masivamente en el doIt. 
		line.setDebit(null);
		line.setCredit(null);

		if (!line.save()) {
			log.severe("Cannot save X_T_Acct_Balance line. C_ElementValue_ID=" + line.getC_ElementValue_ID());
			throw new Exception("@ProcessRunError@");
		}
	}
	
	protected void clearDateAcct() {
		DB.executeUpdate("UPDATE AD_PInstance_Para SET p_date = null, p_date_to = null WHERE parametername = 'DateAcct' AND AD_PInstance_ID = " + getAD_PInstance_ID());		
	}
	
	/**
	 * Obtiene la view de la suma de la columna parámetro para realizar el update
	 * correspondiente
	 * 
	 * @param columnSum          columna del select a sumar
	 * @param withInflationIndex true si se debe incluir las porciones de índice de
	 *                           inflación, false caso contrario
	 * @return Consulta sql de suma de columna para un posterior update
	 */
	protected String getSQLView(String columnSum, boolean withInflationIndex) {
		StringBuffer sqlView = new StringBuffer();
		sqlView.append(" SELECT ev.C_ElementValue_ID, tb.C_ElementValue_To_ID, tb.HierarchicalCode, COALESCE(SUM("+columnSum+"),0) as thesum "); 
		sqlView.append(" FROM C_ElementValue ev ");
		sqlView.append(" LEFT JOIN "+p_factAcctTable+" fa ON (fa.Account_ID = ev.C_ElementValue_ID) ");
		sqlView.append(" INNER JOIN " + getReportTableName() + " tb ON (tb.C_ElementValue_ID = ev.C_ElementValue_ID AND tb.AD_PInstance_ID = ?) ");
		if(withInflationIndex){
			sqlView.append(" INNER JOIN c_period p on fa.DateAcct::date between startdate and enddate "); 
			sqlView.append(" INNER JOIN c_inflation_index ii on p.c_period_id = ii.c_period_id "); 
		}
		sqlView.append(" WHERE ev.IsActive = 'Y' ");
		sqlView.append("   AND fa.AD_Client_ID = ").append(getAD_Client_ID());
		sqlAppend     ("   AND fa.AD_Org_ID = ? ", p_AD_Org_ID > 0, sqlView);
		sqlAppend     ("   AND ?::date <= fa.DateAcct::date ", p_DateAcct_From, sqlView);
		sqlAppend     ("   AND fa.DateAcct::date <= ?::date ", p_DateAcct_To, sqlView);
		if(withInflationIndex){
			sqlView.append("   AND ev.isadjustable = 'Y' ");
		}
		sqlView.append(" GROUP BY ev.C_ElementValue_ID, tb.C_ElementValue_To_ID, tb.HierarchicalCode ");
		return sqlView.toString();
	}
	
	/**
	 * Realiza validaciones de índices de inflación
	 * @throws Exception
	 */
	protected void validationInflationIndex() throws Exception{
		if(applyInflationIndexes){
			// Fecha de inicio debe ser obligatoria
			if(p_DateAcct_From == null){
				throw new Exception(Msg.getMsg(getCtx(), "ApplyInflationIndexNotDateFrom"));
			}
			
			// Verificar que existan todos los períodos de inflación comprendidos entre la
			// fecha de inicio y fin
			// Si al menos 1 de los períodos no existe, entonces error
			String sql = "select distinct c_period_id, name, startdate " + 
					"from c_period p " + 
					"where ad_client_id = ? " +
					"	and startdate >= date_trunc('month', ?::date) ";
			if(p_DateAcct_To != null) {
				sql += "	and startdate <= date_trunc('month', ?::date) ";
			}
			sql += "except " + 
					"select p.c_period_id, p.name, p.startdate " + 
					"from c_inflation_index ii " + 
					"join c_period p on p.c_period_id = ii.c_period_id " + 
					"where p.ad_client_id = ? " + 
					"	and startdate >= date_trunc('month', ?::date) ";
			if(p_DateAcct_To != null) {
				sql += "	and startdate <= date_trunc('month', ?::date)";
			}
			sql += " ORDER BY startdate ";
					
			PreparedStatement ps = DB.prepareStatement(sql, get_TrxName(), true);
			int i = 1;
			ps.setInt(i++, getAD_Client_ID());
			ps.setTimestamp(i++, p_DateAcct_From);
			if(p_DateAcct_To != null) {
				ps.setTimestamp(i++, p_DateAcct_To);
			}
			ps.setInt(i++, getAD_Client_ID());
			ps.setTimestamp(i++, p_DateAcct_From);
			if(p_DateAcct_To != null) {
				ps.setTimestamp(i++, p_DateAcct_To);
			}
			ResultSet rs = ps.executeQuery();
			String periodNames = "";
			while(rs.next()) {
				periodNames += ","+rs.getString("name");
			}
			rs.close();
			ps.close();
			if(periodNames.length() > 0) {
				periodNames = periodNames.substring(1);
				throw new Exception(Msg.getMsg(getCtx(), "NoInflationIndexPeriods", new Object[] {periodNames}));
			}
		}
	}
	
	/**
	 * @return Índice de inflación entre las fechas parámetro
	 */
	protected MInflationIndex getInflationIndex() throws Exception{
		MInflationIndex fiFrom = null;
		if(applyInflationIndexes){
			// Validaciones de índices de inflación 
			validationInflationIndex();
			
			// Obtener el indice de inflación entre fecha desde y hasta
			int inflationIndexFromID = getInflationIndexID(p_DateAcct_From);
			if(inflationIndexFromID > 0){
				fiFrom = new MInflationIndex(getCtx(), inflationIndexFromID, get_TrxName());
			}
		}
		return fiFrom;
	}
	
	/**
	 * @param compareDate
	 *            fecha de comparación del período
	 * @return id del índice de inflación que incluye la fecha parámetro, -1 si no existe
	 */
	protected Integer getInflationIndexID(Timestamp compareDate){
		if(compareDate == null){
			return 0;
		}
		return DB.getSQLValue(get_TrxName(),
				"select ii.c_inflation_index_id from " + MInflationIndex.Table_Name
						+ " ii join c_period p on p.c_period_id = ii.c_period_id where ii.ad_client_id = "
						+ Env.getAD_Client_ID(getCtx()) + " and '" + Env.getDateFormatted(compareDate)
						+ "'::date between startdate::date and enddate::date ",
				true);
	}
	
	@Override
	protected String getReportTableName() {
		return "T_ACCT_Balance";
	}
}
