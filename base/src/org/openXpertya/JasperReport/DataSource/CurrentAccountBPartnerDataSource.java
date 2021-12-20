package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.cc.CurrentAccountQuery;
import org.openXpertya.model.MCurrency;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class CurrentAccountBPartnerDataSource extends QueryDataSource {

	protected static final String DOC_INVOICE = "C_Invoice";
	protected static final String DOC_PAYMENT = "C_Payment";
	protected static final String DOC_CASHLINE = "C_CashLine";
	protected static final String DOC_ALLOCATIONHDR = "C_AllocationHdr";
	
	/** Contexto actual */
	private Properties ctx;
	
	/** ID de la instancia de este proceso */
	private int pInstanceID;
	
	/** Query de cuenta corriente actual */
	private CurrentAccountQuery actualCAQ;
	
	/** Insert Masivo */
	private StringBuffer massiveInsert = null;
	
	/** Fecha Desde */
	private Timestamp dateFrom = null;
	
	/** Fecha Hasta */
	private Timestamp dateTo = null;
	
	/** Entidad Comercial parámetro */
	private Integer bpParamID = null;
	
	/** Tipo de Cuenta */
	private String accountType = null;
	
	/** Balance acumulado por BP */
	private BigDecimal acumBalance = null;
	
	/** Moneda de la compañía */
	private int clientCurrencyID;
	
	/** Signo de documentos que son débitos (depende de p_AccountType) */
	private int debit_signo_issotrx;
	/** Signo de documentos que son créditos (depende de p_AccountType) */
	private int credit_signo_isotrx;
	
	public CurrentAccountBPartnerDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public CurrentAccountBPartnerDataSource(Properties ctx, Integer pInstanceID, Timestamp dateFrom, Timestamp dateTo,
			String accountType, Integer bPartnerID, String trxName) {
		super(trxName);
		setCtx(ctx);
		setpInstanceID(pInstanceID);
		setMassiveInsert(new StringBuffer());
		setClientCurrencyID(Env.getC_Currency_ID(getCtx()));
		setAccountType(accountType);
		setBpParamID(bPartnerID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setDebit_signo_issotrx(1);
		setCredit_signo_isotrx(-1);
	}

	@Override
	public void loadData() throws Exception {
		// Cargar la información de la cuenta corriente de todas las EC
		loadCurrentAccountData();
		
		// Realizar la carga de la info basado en la tabla temporal de manera standard
		// de esta clase
		super.loadData();
	}
	
	/**
	 * Cargamos la info en la tabla t_cuentacorriente a partir de la info de cada EC
	 */
	public void loadCurrentAccountData() throws Exception {
		// El procedimiento se realiza de forma iterativa por EC
		PreparedStatement ps = DB.prepareStatement(getBPQuery(), getTrxName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			loadCurrentAccountDataForCurrentBP(rs.getInt("c_bpartner_id"));
		}
		// Cargar la info en la tabla
		if(getMassiveInsert().length() > 0) {
			int no = DB.executeUpdate(getMassiveInsert().toString(), getTrxName());
			if(no == 0) {
				throw new Exception("Error insertando datos en la tabla temporal");
			}
		}
		
		ps.close();
		rs.close();
	}
	
	/**
	 * Carga la info en la tabla t_cuentacorriente a partir de la info de la EC
	 * actual
	 * 
	 * @param bpartnerID entidad comercial actual
	 */
	protected void loadCurrentAccountDataForCurrentBP(int bPartnerID) throws Exception {
		setActualCAQ(new CurrentAccountQuery(getCtx(), null, null, false, getDateFrom(), getDateTo(), "A", bPartnerID,
				getAccountType(), true));
		int subIndex = 0;
		// Obtener el saldo acumulado a fecha desde
		subIndex = makeInsertInitialAcumBalance(subIndex);
		// Obtener los movimientos de la EC
		makeInsertAllDocuments(subIndex);
	}
	
	/**
	 * Saldo acumulado a la fecha de inicio
	 * 
	 * @param subIndex índice actual
	 * @throws Exception en caso de error
	 */
	protected int makeInsertInitialAcumBalance(int subIndex) throws Exception {
		setAcumBalance(BigDecimal.ZERO);
		if(getDateFrom() == null) {
			return subIndex;
		}
			
		PreparedStatement ps = DB.prepareStatement(getActualCAQ().getAcumBalanceQuery(), getTrxName(), true);
		int i = 1;
		// Parámetros de sqlDoc
		ps.setInt(i++, getDebit_signo_issotrx());
		ps.setInt(i++, getCredit_signo_isotrx());
		ps.setInt(i++, getClientCurrencyID());
		ps.setInt(i++, Env.getAD_Client_ID(getCtx()));
		ps.setInt(i++, getActualCAQ().getbPartnerID());
		ps.setTimestamp(i++, getDateFrom());
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			if(!Util.isEmpty(rs.getBigDecimal("Debit"), true) || 
					!Util.isEmpty(rs.getBigDecimal("Credit"), true)) {
				setAcumBalance(rs.getBigDecimal("Debit").subtract(rs.getBigDecimal("Credit")));
				subIndex++;
				getMassiveInsert().append("INSERT INTO T_CUENTACORRIENTE (SUBINDICE, IncludeOpenOrders, ShowDetailedReceiptsPayments, AD_CLIENT_ID, AD_ORG_ID, AD_PINSTANCE_ID, ISO_CODE, AMOUNT, DEBE, HABER, SALDO, NUMEROCOMPROBANTE, C_BPARTNER_ID, ACCOUNTTYPE, DATETRX, DATEACCT, C_DOCTYPE_ID, C_INVOICE_ID, C_PAYMENT_ID, C_CASHLINE_ID, Condition, IncludeCreditNotesRequest, openamt) "
						+ " VALUES ("
						+ subIndex
						+ ", '"
						+ "N"
						+ "', '"
						+ "N"
						+ "', "
						+ Env.getAD_Client_ID(getCtx())
						+ ", "
						+ Env.getAD_Org_ID(getCtx())
						+ " , "
						+ getpInstanceID()
						+ " ,'"
						+ MCurrency.getISO_Code(getCtx(), getClientCurrencyID())
						+ "', "
						+ "null"
						+ ", "
						+ rs.getBigDecimal("Debit")
						+ ", "
						+ rs.getBigDecimal("Credit")
						+ ", "
						+ getAcumBalance()
						+ ", '"
						+ "Saldo inicial"
						+ "', "
						+ getActualCAQ().getbPartnerID()
						+ ", '"
						+ getAccountType()
						+ "', '"
						+ getDateFrom()
						+ "', '"
						+ getDateFrom()
						+ "', "
						+ " null, null, null, null"
						+ " , "
						+ "'A'"
						+ ", "
						+ "'N'"
						+ ", "
						+ rs.getBigDecimal("openamt")
						+ "); ");
			}
		}
		ps.close();
		rs.close();
		return subIndex;
	}
	
	protected void makeInsertAllDocuments(int subIndex) throws Exception {
		PreparedStatement ps = DB.prepareStatement(getActualCAQ().getQuery(), getTrxName(), true);
		int i = 1;
		// Parámetros de sqlDoc
		ps.setInt(i++, getDebit_signo_issotrx());
		ps.setInt(i++, getCredit_signo_isotrx());
		ps.setInt(i++, getClientCurrencyID());
		ps.setInt(i++, Env.getAD_Client_ID(getCtx()));
		ps.setInt(i++, getActualCAQ().getbPartnerID());
		
		if(getDateFrom() != null) {
			ps.setTimestamp(i++, getDateFrom());
		}
		
		if(getDateTo() != null) {
			ps.setTimestamp(i++, getDateTo());
		}
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			setAcumBalance(getAcumBalance().add(rs.getBigDecimal("Debit").subtract(rs.getBigDecimal("Credit"))));
			subIndex++;
			getMassiveInsert().append("INSERT INTO T_CUENTACORRIENTE (SUBINDICE, IncludeOpenOrders, ShowDetailedReceiptsPayments, AD_CLIENT_ID, AD_ORG_ID, AD_PINSTANCE_ID, ISO_CODE, AMOUNT, DEBE, HABER, SALDO, NUMEROCOMPROBANTE, C_BPARTNER_ID, ACCOUNTTYPE, DATETRX, DATEACCT, C_DOCTYPE_ID, C_INVOICE_ID, C_PAYMENT_ID, C_CASHLINE_ID, c_allocationhdr_id, Condition, IncludeCreditNotesRequest, openamt) "
					+ " VALUES ("
					+ subIndex
					+ ", '"
					+ "N"
					+ "', '"
					+ "N"
					+ "', "
					+ Env.getAD_Client_ID(getCtx())
					+ ", "
					+ Env.getAD_Org_ID(getCtx())
					+ " , "
					+ getpInstanceID()
					+ " ,'"
					+ MCurrency.getISO_Code(getCtx(), rs.getInt("C_Currency_ID"))
					+ "', "
					+ rs.getBigDecimal("Amount")
					+ ", "
					+ rs.getBigDecimal("Debit")
					+ ", "
					+ rs.getBigDecimal("Credit")
					+ ", "
					+ getAcumBalance()
					+ ", '"
					+ rs.getString("DocumentNo")
					+ "', "
					+ getActualCAQ().getbPartnerID()
					+ ", '"
					+ getAccountType()
					+ "', '"
					+ rs.getTimestamp("DateTrx")
					+ "', '"
					+ rs.getTimestamp("DateTrx")
					+ "', "
					+ rs.getInt("C_DocType_ID")
					+ ", "
					+ (DOC_INVOICE.equals(rs.getString("documenttable")) ? rs.getString("document_id") : "NULL")
					+ ", "
					+ (DOC_PAYMENT.equals(rs.getString("documenttable")) ? rs.getString("document_id") : "NULL")
					+ ", "
					+ (DOC_CASHLINE.equals(rs.getString("documenttable")) ? rs.getString("document_id") : "NULL")
					+ " , "
					+ (DOC_ALLOCATIONHDR.equals(rs.getString("documenttable")) ? rs.getString("document_id") : "NULL")
					+ " , "
					+ "'A'"
					+ ", "
					+ "'N'"
					+ ", "
					+ rs.getBigDecimal("openamt")
					+ "); ");
		}
		ps.close();
		rs.close();
	}
	
	/**
	 * @return sql de las entidades comerciales
	 */
	protected String getBPQuery() {
		return 	" Select distinct c_bpartner_id " +
				" from c_bpartner " +
				" where isactive = 'Y' " +
				" and " + getAccountTypeClause() + " = 'Y' " +
				" and ad_client_id = " + Env.getAD_Client_ID(getCtx()) +
				(!Util.isEmpty(getBpParamID(), true)?" AND c_bpartner_id = " + getBpParamID():"");	
	}
	
	private String getAccountTypeClause() {
		return accountType.equalsIgnoreCase("C")?"isCustomer":"isVendor";
	}
	
	protected BigDecimal getAccountTypeSign(){
		return accountType.equalsIgnoreCase("C")?BigDecimal.ONE:BigDecimal.ONE.negate();
	}
	
	@Override
	protected String getQuery() {
		String sql = "select *, bp.value as bpartner_value, bp.name as bpartner_name, dt.name as tipo "
				+ "from t_cuentacorriente cc "
				+ "join c_bpartner bp on bp.c_bpartner_id = cc.c_bpartner_id "
				+ "left join c_doctype dt on dt.c_doctype_id = cc.c_doctype_id "
				+ "where cc.ad_pinstance_id = ? "
				+ "order by bp.name, subindice ";
		return sql;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] {getpInstanceID()};
	}

	public int getpInstanceID() {
		return pInstanceID;
	}

	public void setpInstanceID(int pInstanceID) {
		this.pInstanceID = pInstanceID;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected StringBuffer getMassiveInsert() {
		return massiveInsert;
	}

	protected void setMassiveInsert(StringBuffer massiveInsert) {
		this.massiveInsert = massiveInsert;
	}

	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getBpParamID() {
		return bpParamID;
	}

	public void setBpParamID(Integer bpParamID) {
		this.bpParamID = bpParamID;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	protected CurrentAccountQuery getActualCAQ() {
		return actualCAQ;
	}

	protected void setActualCAQ(CurrentAccountQuery actualCAQ) {
		this.actualCAQ = actualCAQ;
	}

	protected BigDecimal getAcumBalance() {
		return acumBalance;
	}

	protected void setAcumBalance(BigDecimal acumBalance) {
		this.acumBalance = acumBalance;
	}

	protected int getClientCurrencyID() {
		return clientCurrencyID;
	}

	protected void setClientCurrencyID(int clientCurrencyID) {
		this.clientCurrencyID = clientCurrencyID;
	}

	protected int getDebit_signo_issotrx() {
		return debit_signo_issotrx;
	}

	protected void setDebit_signo_issotrx(int debit_signo_issotrx) {
		this.debit_signo_issotrx = debit_signo_issotrx;
	}

	protected int getCredit_signo_isotrx() {
		return credit_signo_isotrx;
	}

	protected void setCredit_signo_isotrx(int credit_signo_isotrx) {
		this.credit_signo_isotrx = credit_signo_isotrx;
	}

}
