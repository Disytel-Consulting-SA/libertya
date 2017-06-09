package org.openXpertya.grid;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.X_C_CreditCardSettlement;
import org.openXpertya.model.X_M_BoletaDeposito;
import org.openXpertya.model.X_M_BoletaDepositoLine;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public abstract class CreateFromStatementData{
	
	/** Cuenta Bancaria */
	private Integer bankAccountID = null;
	/** Fecha a convertir el importe de los payments */
	private Timestamp statementDate = null;
	/** Agrupar */
	private boolean grouped = false;
	/** Número de Lote de Cupones */
	private String couponBatchNo = null;
	/** Filtro de fecha desde y hasta */
	private Timestamp dateTrxFrom = null;
	private Timestamp dateTrxTo = null;
	
	@Override
	public String toString(){
		return Msg.translate(Env.getCtx(), getMsg());
	}
	
	public String getQuery(){
		StringBuffer sql = new StringBuffer(); 
        sql.append(getSelectSQL())           
           .append("FROM C_BankAccount ba ")
           .append("INNER JOIN C_Payment p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) ")
           .append("INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) ")
           .append("INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) ")
           .append("LEFT JOIN ")
						.append(X_M_BoletaDeposito.Table_Name).append(" bdb ON (p.C_Payment_ID=bdb.C_Boleta_Payment_ID) ")
           .append("LEFT JOIN ")
						.append(X_M_BoletaDepositoLine.Table_Name).append(" blec ON (p.C_Payment_ID=blec.C_Depo_Payment_ID) ")
		   .append("LEFT JOIN ")
						.append(X_M_BoletaDeposito.Table_Name).append(" bdec ON (blec.m_boletadeposito_id=bdec.m_boletadeposito_id) ")
		   .append("LEFT JOIN ")
						.append(X_C_CreditCardSettlement.Table_Name).append(" ccs ON (p.C_Payment_ID=ccs.C_Payment_ID) ")
           .append("WHERE p.Processed='Y' ")
           .append(  "AND p.IsReconciled='N' ")
           .append(  "AND p.DocStatus IN ('CO','CL','RE','VO') ")
           .append(  "AND p.PayAmt<>0 ")
           .append(  "AND p.C_BankAccount_ID=? ")
				.append(isAllowCouponBatchNoFilter() && !Util.isEmpty(getCouponBatchNo(), true)
						? " AND p.couponbatchnumber=? " : "")
		   .append(getDateTrxFrom() != null?" AND p.datetrx::date >= ?::date ":"")
		   .append(getDateTrxTo() != null?" AND p.datetrx::date <= ?::date ":"")
           .append(  "AND NOT EXISTS ")
           .append(      "(SELECT * FROM C_BankStatementLine l ")
           // Voided Bank Statements have 0 StmtAmt
           .append(       "WHERE p.C_Payment_ID=l.C_Payment_ID AND l.StmtAmt <> 0)")
           .append(getWhereClauseSQL() != null?getWhereClauseSQL():"")
           .append(getGroupBySQL() != null?getGroupBySQL():"")
           .append(getOrderBySQL() != null?getOrderBySQL():"");
        
        return sql.toString();
	}
	
	protected String getSelectSQL(){
		StringBuffer sql = new StringBuffer();
		if(isAllowGrouped() && isGrouped()){
			sql.append("SELECT ")
			   .append(   "p.DateTrx::date as DateTrx, ")
			   .append(   "null as C_Payment_ID, ")
			   .append(   "null as DocumentNo, ")
	           .append(   "bp.Name AS BPartnerName, ")
	           .append(   "p.C_Currency_ID, ")
	           .append(   "c.ISO_Code, ")
	           .append(   "p.duedate, ")
	           .append(   "p.tendertype, ")
	           .append(   "null as c_creditcardsettlement_id, ")
		       .append(   "null as creditcardsettlement_documentno, ")
		       .append(   "null as m_boletadeposito_id, ")
		       .append(   "null as boletadeposito_documentno, ")
	           .append(   "SUM(p.PayAmt) as PayAmt , ")
	           .append(   "SUM(currencyConvert(p.PayAmt,p.C_Currency_ID,ba.C_Currency_ID,?,null,p.AD_Client_ID,p.AD_Org_ID)) AS ConvertedAmt ");
		}
		else{
			sql.append("SELECT ")
	        .append(   "p.DateTrx, ")
	        .append(   "p.C_Payment_ID, ")
	        .append(   "p.DocumentNo, ")
	        .append(   "p.C_Currency_ID, ")
	        .append(   "c.ISO_Code, ")
	        .append(   "p.PayAmt, ")
	        .append(   "p.duedate, ")
	        .append(   "p.tendertype, ")
	        .append(   "currencyConvert(p.PayAmt,p.C_Currency_ID,ba.C_Currency_ID,?,null,p.AD_Client_ID,p.AD_Org_ID) AS ConvertedAmt, ")
	        .append(   "bp.Name AS BPartnerName, ")
	        .append(   "ccs.c_creditcardsettlement_id, ")
	        .append(   "ccs.settlementno as creditcardsettlement_documentno, ")
	        .append(   "coalesce(bdb.m_boletadeposito_id, bdec.m_boletadeposito_id) as m_boletadeposito_id, ")
	        .append(   "coalesce(bdb.documentno, bdec.documentno) as boletadeposito_documentno ");
		}
		
		return sql.toString();
	}
	
	protected String getGroupBySQL(){
		return isAllowGrouped() && isGrouped()
				? " GROUP BY p.DateTrx::date, bp.Name, p.C_Currency_ID, c.ISO_Code, p.duedate, p.tendertype " : "";
	}
	
	protected String getOrderBySQL(){
		return " ORDER BY p.DateTrx::date, p.tendertype ";
	}
	
	public Object[] getSQLParams(){
		List<Object> params = new ArrayList<Object>();
		params.add(getStatementDate());
		params.add(getBankAccountID());
		if(isAllowCouponBatchNoFilter() && !Util.isEmpty(getCouponBatchNo(), true)){
			params.add(getCouponBatchNo());
		}
		if(getDateTrxFrom() != null){
			params.add(getDateTrxFrom());
		}
		if(getDateTrxTo() != null){
			params.add(getDateTrxTo());
		}
		addSQLParams(params);
		return params.toArray();
	}

	//*************************************** 
	//*			Métodos Abstractos			*
	//***************************************
	
	/** Cláusulas SQL de obtención de datos dependiendo el caso a filtrar */
	public abstract String getWhereClauseSQL();
	
	/** Parámetros adicionales a la consulta */
	public abstract List<Object> addSQLParams(List<Object> params);
	
	/** Mensaje a mostrar por cada estrategia de datos */
	public abstract String getMsg();
	
	/** Boolean que determina si este origen de datos es posible agrupar */
	public abstract boolean isAllowGrouped();
	
	/**
	 * Boolean que determina si este origen de datos es posible filtrar por
	 * número de lote de cupón
	 */
	public abstract boolean isAllowCouponBatchNoFilter();


	//*************************************** 
	//*			Getters y Setters			*
	//***************************************
	
	public boolean isGrouped() {
		return grouped;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	public String getCouponBatchNo() {
		return couponBatchNo;
	}

	public void setCouponBatchNo(String couponBatchNo) {
		this.couponBatchNo = couponBatchNo;
	}

	public Integer getBankAccountID() {
		return bankAccountID;
	}

	public void setBankAccountID(Integer bankAccountID) {
		this.bankAccountID = bankAccountID;
	}

	public Timestamp getStatementDate() {
		return statementDate;
	}

	public void setStatementDate(Timestamp statementDate) {
		this.statementDate = statementDate;
	}

	public Timestamp getDateTrxFrom() {
		return dateTrxFrom;
	}

	public void setDateTrxFrom(Timestamp dateTrxFrom) {
		this.dateTrxFrom = dateTrxFrom;
	}

	public Timestamp getDateTrxTo() {
		return dateTrxTo;
	}

	public void setDateTrxTo(Timestamp dateTrxTo) {
		this.dateTrxTo = dateTrxTo;
	}
}
