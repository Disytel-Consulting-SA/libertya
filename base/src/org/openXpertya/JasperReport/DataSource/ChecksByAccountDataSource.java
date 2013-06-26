package org.openXpertya.JasperReport.DataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ChecksByAccountDataSource implements OXPJasperDataSource {

	/** Transacción */
	private String trxName;
	
	/** Contexto */
	private Properties ctx;
	
	/** Data Source: Cheques a imprimir */
	private List<CheckDTO> checks;
	
	/** Fecha */
	private Timestamp date;
	
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
	
	private Map<String, String> methodMapper;
	
	public ChecksByAccountDataSource(Properties ctx, Integer bankAccountID, Timestamp date, String dateOrder, String trxName) {
		setCtx(ctx);
		setBankAccountID(bankAccountID);
		setDate(date);
		setDateOrder(dateOrder);
		setDateColumnName(dateOrder.equals("D")?"duedate":"datetrx");
		setTrxName(trxName);
		initMethodMapper();
	}

	public void initMethodMapper(){
		setMethodMapper(new HashMap<String, String>());
		getMethodMapper().put("DATE", "getDate");
		getMethodMapper().put("DOCUMENTNO", "getDocumentNo");
		getMethodMapper().put("DESCRIPTION", "getDescription");
		getMethodMapper().put("OUTAMT", "getOutAmt");
		getMethodMapper().put("INAMT", "getInAmt");
		getMethodMapper().put("BANK_ACCOUNT", "getBankAccount");
		getMethodMapper().put("BALANCE", "getBalance");
	}
	
	protected String getQuery(String dateCompareOperator) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(getDateColumnName()).append(", ");
		sql.append("	p.documentno, " +
					"	ba.description as bank_account, " +
					"	bp.name as description, " +
					"	abs((CASE WHEN p.isreceipt = 'Y' THEN 0 ELSE p.payamt END)) as outamt, " +
					"	abs((CASE WHEN p.isreceipt = 'Y' THEN p.payamt ELSE 0 END)) as inamt " +
					"FROM c_payment as p " +
					"INNER JOIN c_bankaccount as ba ON ba.c_bankaccount_id = p.c_bankaccount_id " +
					"INNER JOIN c_bpartner as bp ON bp.c_bpartner_id = p.c_bpartner_id " +
					"WHERE p.ad_client_id = ? AND p.docstatus IN ('CO','CL') ");
		sql.append(" AND ");
		sql.append(getDateColumnName());
		sql.append("::date ");
		sql.append(dateCompareOperator);
		sql.append(" ?::date ");
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
		PreparedStatement ps = DB.prepareStatement(getQuery(">"), getTrxName(), true);
		int i = 1;
		ps.setInt(i++, Env.getAD_Client_ID(getCtx()));
		ps.setTimestamp(i++, getDate());
		if(!Util.isEmpty(getBankAccountID(), true)){
			ps.setInt(i++, getBankAccountID());
		}
		ResultSet rs = ps.executeQuery();
		BigDecimal balanceAux = getInitialBalance();
		while(rs.next()){
			balanceAux = balanceAux.add(rs.getBigDecimal("inAmt").subtract(
					rs.getBigDecimal("outAmt")));
			getChecks()
					.add(new CheckDTO(rs.getTimestamp(getDateColumnName()), rs
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
		sql.append(getQuery("<"));
		sql.append(" ) as c ");
		PreparedStatement ps = DB.prepareStatement(sql.toString(), getTrxName(), true);
		int i = 1;
		ps.setInt(i++, Env.getAD_Client_ID(getCtx()));
		ps.setTimestamp(i++, getDate());
		if(!Util.isEmpty(getBankAccountID(), true)){
			ps.setInt(i++, getBankAccountID());
		}
		ResultSet rs = ps.executeQuery();
		BigDecimal balance = BigDecimal.ZERO;
		if(rs.next()){
			balance = rs.getBigDecimal("balance");
		}
		getChecks().add(
				new CheckDTO(rs.getTimestamp("maxDate"), null, null,
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
		if(fieldName.equalsIgnoreCase("DATE")){
			output = getCurrentCheck().getDate();
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

	protected Timestamp getDate() {
		return date;
	}

	protected void setDate(Timestamp date) {
		this.date = date;
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

	protected Map<String, String> getMethodMapper() {
		return methodMapper;
	}

	protected void setMethodMapper(Map<String, String> methodMapper) {
		this.methodMapper = methodMapper;
	}

	private class CheckDTO{
		private Timestamp date;
		private String documentNo;
		private String bankAccount;
		private String description;
		private BigDecimal outAmt;
		private BigDecimal inAmt;
		private BigDecimal balance;
		
		public CheckDTO(){}
		
		public CheckDTO(Timestamp date, String documentNo, String bankAccount,
				String description, BigDecimal outAmt, BigDecimal inAmt, BigDecimal balance) {
			this.setDate(date);
			this.setBankAccount(bankAccount);
			this.setDescription(description);
			this.setDocumentNo(documentNo);
			this.setInAmt(inAmt);
			this.setOutAmt(outAmt);
			this.setBalance(balance);
		}

		public Timestamp getDate() {
			return date;
		}

		public void setDate(Timestamp date) {
			this.date = date;
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
	}

}
