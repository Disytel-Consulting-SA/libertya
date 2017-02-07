package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ChecksByAccountDataSource implements OXPJasperDataSource {

	/** Transacción */
	private String trxName;
	
	/** Contexto */
	private Properties ctx;
	
	/** Data Source: Cheques a imprimir */
	private List<CheckDTO> checks;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Tipo de fecha: Emisión o Vencimiento */
	private String dateOrder;
	
	/** Nombre de la columna a utilizar dependiendo el tipo de fecha */
	private String dateColumnName;
	
	/** Cuenta Bancaria */
	private Integer bankAccountID;
	
	/** Índice actual del data source */
	private Integer currentCheckIndex;
	
	/** Total de cheques */
	private Integer totalChecks;
	
	/** Cheque actual */
	private CheckDTO currentCheck;
	
	/** Saldo inicial */
	private BigDecimal initialBalance;
	
	public ChecksByAccountDataSource(Properties ctx, Integer bankAccountID, Timestamp dateFrom, Timestamp dateTo, String dateOrder, String trxName) {
		setCtx(ctx);
		setBankAccountID(bankAccountID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setDateOrder(dateOrder);
		setDateColumnName(dateOrder.equals("D")?"duedate":"datetrx");
		setTrxName(trxName);
	}
	
	protected String getQuery(String dateCompareOperator, boolean addDateTo) {
		StringBuffer sql = new StringBuffer("SELECT o.value orgvalue, o.name orgname, p.datetrx, " +
					"	p.duedate, " +
					"	p.documentno, " +
					"	ba.description as bank_account, " +
					"	bp.name as description, " +
					"	abs((CASE WHEN p.isreceipt = 'Y' THEN 0 ELSE p.payamt END)) as outamt, " +
					"	abs((CASE WHEN p.isreceipt = 'Y' THEN p.payamt ELSE 0 END)) as inamt " +
					"FROM c_payment as p " +
					"INNER JOIN c_bankaccount as ba ON ba.c_bankaccount_id = p.c_bankaccount_id " +
					"INNER JOIN c_bpartner as bp ON bp.c_bpartner_id = p.c_bpartner_id " +
					"INNER JOIN ad_org as o ON o.ad_org_id = p.ad_org_id " +
					"WHERE p.ad_client_id = ? AND p.tendertype = 'K' AND p.docstatus IN ('CO','CL') ");
		sql.append(" AND ");
		sql.append(getDateColumnName());
		sql.append("::date ");
		sql.append(dateCompareOperator);
		sql.append(" ?::date ");
		if(addDateTo && getDateTo() != null){
			sql.append(" AND ").append(getDateColumnName()).append("::date ").append(" <= ?::date ");
		}
		sql.append(Util.isEmpty(getBankAccountID(), true) ? ""
				: " AND p.c_bankaccount_id = ? ");
		sql.append(" ORDER BY ");
		sql.append(getDateColumnName());
		sql.append(" ,p.documentno ");
		return sql.toString();
	}
	
	
	public void loadData() throws Exception {
		setChecks(new ArrayList<ChecksByAccountDataSource.CheckDTO>());
		// Agregar el saldo inicial
		initialBalance();
		// Data Source
		PreparedStatement ps = DB.prepareStatement(getQuery(">=", true), getTrxName(), true);
		int i = 1;
		ps.setInt(i++, Env.getAD_Client_ID(getCtx()));
		ps.setTimestamp(i++, getDateFrom());
		if(getDateTo() != null){
			ps.setTimestamp(i++, getDateTo());
		}
		if(!Util.isEmpty(getBankAccountID(), true)){
			ps.setInt(i++, getBankAccountID());
		}
		ResultSet rs = ps.executeQuery();
		BigDecimal balanceAux = getInitialBalance();
		while(rs.next()){
			balanceAux = balanceAux.add(rs.getBigDecimal("inAmt").subtract(
					rs.getBigDecimal("outAmt")));
			getChecks()
					.add(new CheckDTO(rs.getString("orgvalue"), rs
							.getString("orgname"), rs
							.getTimestamp("datetrx"), rs
							.getTimestamp("duedate"), rs
							.getString("documentno"), rs
							.getString("bank_account"), rs
							.getString("description"), rs
							.getBigDecimal("outAmt"), rs.getBigDecimal("inAmt"), 
							balanceAux));
		}
		rs.close();
		ps.close();
		setTotalChecks(getChecks().size());
		setCurrentCheckIndex(-1);
	}

	private void initialBalance() throws Exception{
		// Armar la query para la suma del saldo inicial
		StringBuffer sql = new StringBuffer(
				"SELECT coalesce(sum(inAmt-outAmt),0)::numeric(22,2) as balance, MAX("
						+ getDateColumnName() + ") as maxDate FROM (");
		sql.append(getQuery("<", false));
		sql.append(" ) as c ");
		PreparedStatement ps = DB.prepareStatement(sql.toString(), getTrxName(), true);
		int i = 1;
		ps.setInt(i++, Env.getAD_Client_ID(getCtx()));
		ps.setTimestamp(i++, getDateFrom());
		if(!Util.isEmpty(getBankAccountID(), true)){
			ps.setInt(i++, getBankAccountID());
		}
		ResultSet rs = ps.executeQuery();
		BigDecimal balance = BigDecimal.ZERO;
		if(rs.next()){
			balance = rs.getBigDecimal("balance");
		}
		getChecks().add(
				new CheckDTO(null, null, rs.getTimestamp("maxDate"), null, null, null,
						"Saldo inicial", BigDecimal.ZERO, BigDecimal.ZERO,
						balance));
		setInitialBalance(balance);
		rs.close();
		ps.close();
	}
	
	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		Object output = null;
		String fieldName = arg0.getName();
		if(fieldName.equalsIgnoreCase("ORGVALUE")){
			output = getCurrentCheck().getOrgValue();
		}
		else if (fieldName.equalsIgnoreCase("ORGNAME")){
			output = getCurrentCheck().getOrgName();
		}
		else if (fieldName.equalsIgnoreCase("DATETRX")){
			output = getCurrentCheck().getDateTrx();
		}
		else if(fieldName.equalsIgnoreCase("DUEDATE")){
			output = getCurrentCheck().getDueDate();
		}
		else if(fieldName.equalsIgnoreCase("DOCUMENTNO")){
			output = getCurrentCheck().getDocumentNo();
		}
		else if(fieldName.equalsIgnoreCase("DESCRIPTION")){
			output = getCurrentCheck().getDescription();
		}
		else if(fieldName.equalsIgnoreCase("OUTAMT")){
			output = getCurrentCheck().getOutAmt();
		}
		else if(fieldName.equalsIgnoreCase("INAMT")){
			output = getCurrentCheck().getInAmt();
		}
		else if(fieldName.equalsIgnoreCase("BANK_ACCOUNT")){
			output = getCurrentCheck().getBankAccount();
		}
		else if(fieldName.equalsIgnoreCase("BALANCE")){
			output = getCurrentCheck().getBalance();
		}
		return output;
	}

	@Override
	public boolean next() throws JRException {
		setCurrentCheckIndex(getCurrentCheckIndex() + 1);
		if(getCurrentCheckIndex() < getTotalChecks()){
			setCurrentCheck(getChecks().get(getCurrentCheckIndex()));
			return true;
		}
		return false;
	}

	protected String getTrxName() {
		return trxName;
	}

	protected void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected List<CheckDTO> getChecks() {
		return checks;
	}

	protected void setChecks(List<CheckDTO> checks) {
		this.checks = checks;
	}

	protected String getDateOrder() {
		return dateOrder;
	}

	protected void setDateOrder(String dateOrder) {
		this.dateOrder = dateOrder;
	}

	protected String getDateColumnName() {
		return dateColumnName;
	}

	protected void setDateColumnName(String dateColumnName) {
		this.dateColumnName = dateColumnName;
	}

	protected Integer getBankAccountID() {
		return bankAccountID;
	}

	protected void setBankAccountID(Integer bankAccountID) {
		this.bankAccountID = bankAccountID;
	}
	
	protected Integer getCurrentCheckIndex() {
		return currentCheckIndex;
	}

	protected void setCurrentCheckIndex(Integer currentCheckIndex) {
		this.currentCheckIndex = currentCheckIndex;
	}

	protected CheckDTO getCurrentCheck() {
		return currentCheck;
	}

	protected void setCurrentCheck(CheckDTO currentCheck) {
		this.currentCheck = currentCheck;
	}

	protected Integer getTotalChecks() {
		return totalChecks;
	}

	protected void setTotalChecks(Integer totalChecks) {
		this.totalChecks = totalChecks;
	}

	protected BigDecimal getInitialBalance() {
		return initialBalance;
	}

	protected void setInitialBalance(BigDecimal initialBalance) {
		this.initialBalance = initialBalance;
	}
	
	protected Timestamp getDateFrom() {
		return dateFrom;
	}

	protected void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	protected Timestamp getDateTo() {
		return dateTo;
	}

	protected void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	private class CheckDTO{
		private String orgValue;
		private String orgName;
		private Timestamp dateTrx;
		private Timestamp dueDate;
		private String documentNo;
		private String bankAccount;
		private String description;
		private BigDecimal outAmt;
		private BigDecimal inAmt;
		private BigDecimal balance;
		
		public CheckDTO(){}
		
		public CheckDTO(String orgValue, String orgName, Timestamp dateTrx, Timestamp dueDate, 
				String documentNo, String bankAccount, String description, BigDecimal outAmt, 
				BigDecimal inAmt, BigDecimal balance) {
			this.setOrgValue(orgValue);
			this.setOrgName(orgName);
			this.setDateTrx(dateTrx);
			this.setDueDate(dueDate);
			this.setBankAccount(bankAccount);
			this.setDescription(description);
			this.setDocumentNo(documentNo);
			this.setInAmt(inAmt);
			this.setOutAmt(outAmt);
			this.setBalance(balance);
		}

		public Timestamp getDateTrx() {
			return dateTrx;
		}

		public void setDateTrx(Timestamp dateTrx) {
			this.dateTrx = dateTrx;
		}

		public Timestamp getDueDate() {
			return dueDate;
		}

		public void setDueDate(Timestamp dueDate) {
			this.dueDate = dueDate;
		}
		
		public String getDocumentNo() {
			return documentNo;
		}

		public void setDocumentNo(String documentNo) {
			this.documentNo = documentNo;
		}

		public String getBankAccount() {
			return bankAccount;
		}

		public void setBankAccount(String bankAccount) {
			this.bankAccount = bankAccount;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public BigDecimal getOutAmt() {
			return outAmt;
		}

		public void setOutAmt(BigDecimal outAmt) {
			this.outAmt = outAmt;
		}

		public BigDecimal getInAmt() {
			return inAmt;
		}

		public void setInAmt(BigDecimal inAmt) {
			this.inAmt = inAmt;
		}

		public BigDecimal getBalance() {
			return balance;
		}

		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}

		public String getOrgValue() {
			return orgValue;
		}

		public void setOrgValue(String orgValue) {
			this.orgValue = orgValue;
		}

		public String getOrgName() {
			return orgName;
		}

		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}
	}

}
