package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCreditCardSettlement;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.X_C_CardSettlementConcepts;
import org.openXpertya.model.X_C_CommissionConcepts;
import org.openXpertya.model.X_C_ExpenseConcepts;
import org.openXpertya.model.X_C_IVASettlements;
import org.openXpertya.model.X_C_PerceptionsSettlement;
import org.openXpertya.model.X_C_RetencionSchema;
import org.openXpertya.model.X_C_RetencionType;
import org.openXpertya.model.X_C_WithholdingSettlement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class Doc_CreditCardSettlement extends Doc {

	/** Impuestos */
	private List<DocTax> taxes;
	/** Percepciones */
	private List<DocTax> perceptions;
	/** Retenciones */
	private List<DocLine> retentions;
	/** Comisiones */
	private List<DocLine> comissions;
	/** Otros Conceptos */
	private List<DocLine> expenses;
	/** Cuenta Bancaria del Payment generado por la liquidación */
	private Integer bankAccountPaymentGeneratedID = 0;
	
	public Doc_CreditCardSettlement(MAcctSchema[] ass) {
		super(ass);
		// TODO Auto-generated constructor stub
	}
	
	protected Doc_CreditCardSettlement( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }

	@Override
	protected boolean loadDocumentDetails(ResultSet rs) {
		try {
			MCreditCardSettlement ccs = new MCreditCardSettlement(getCtx(), getRecord_ID(), m_trxName);
			MBPartner bp = new MBPartner(getCtx(), ccs.getC_BPartner_ID(), m_trxName);
			// Fecha contable es la fecha de pago
			p_vo.DateAcct = rs.getTimestamp("PaymentDate");
			p_vo.DateDoc = rs.getTimestamp("PaymentDate");
			//  Cuenta bancaria de la EC de la liquidación
			// Se obtiene de la misma forma que al completar la liquidación
			p_vo.C_BankAccount_ID = ccs.getBankAccountID(bp);
			// Cuenta bancaria es la cuenta que se asoció al pago creado, si es que existe alguno
			int bankAccountPaymentID = rs.getInt("C_Payment_ID");
			if(!Util.isEmpty(bankAccountPaymentID, true)){
				MPayment p = new MPayment(getCtx(), bankAccountPaymentID, m_trxName);
				bankAccountPaymentGeneratedID = p.getC_BankAccount_ID();
			}
			
			p_vo.Amounts[ Doc.AMTTYPE_Gross ] = rs.getBigDecimal( "Amount" );
			p_vo.Amounts[ Doc.AMTTYPE_Net ] = rs.getBigDecimal( "NetAmount" );
			
			// Tipo de documento Liquidación de Tarjetas
			p_vo.DocumentType = DOCTYPE_CreditCardSettlement;
			loadDocumentType();
			
			loadIVAs();
			loadPerceptions();
			loadWithholding();
			loadComissions();
			loadExpenses();
			
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		
		return true;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public BigDecimal getBalance() {
		// La validación de importes se realiza al completar una liquidación,
		// por lo tanto, se omite el cálculo. En el caso que deba realizarse, el
		// cálculo del saldo es el mismo que se realiza al completar: 
		
		// Saldo = Bruto - (Neto + IVAs + Percepciones + Retenciones + Comisiones + Otros Gastos)
		return BigDecimal.ZERO;
	}

	/**
	 * Cargar impuestos a contabilizar para esta liquidación en base a la tabla
	 * de detalle
	 * 
	 * @param childTableName
	 *            tabla de detalle donde buscar los impuestos
	 * @return lista de impuestos a contabilizar
	 */
	public List<DocTax> getTaxes(String childTableName){
		List<DocTax> docTaxes = new ArrayList<DocTax>();
		String sql = "SELECT t.c_tax_id, t.name, t.rate, s.amount FROM " + childTableName + " s INNER JOIN C_Tax t on t.c_tax_id = s.c_tax_id WHERE s.C_CreditCardSettlement_ID = ? ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, m_trxName);
			ps.setInt(1, getRecord_ID());
			rs = ps.executeQuery();
			while(rs.next()){
				docTaxes.add(new DocTax(rs.getInt("C_Tax_ID"), rs.getString("Name"), rs.getBigDecimal("Rate"),
						BigDecimal.ZERO, rs.getBigDecimal("Amount")));
			}
		} catch (Exception e) {
			log.severe("Error obtaining taxes from table " + childTableName + " : " + e.getMessage());
		} finally{
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				log.severe("Error obtaining taxes from table " + childTableName + " : " + e2.getMessage());
			}
		}
		return docTaxes;
	}
	
	/**
	 * Cargar los impuestos a contabilizar
	 */
	private void loadIVAs(){
		taxes = getTaxes(X_C_IVASettlements.Table_Name);
	}
	
	/**
	 * Cargar las percepciones a contabilizar
	 */
	private void loadPerceptions(){
		perceptions = getTaxes(X_C_PerceptionsSettlement.Table_Name);
	}
	
	/**
	 * Cargar líneas con artículos a contabilizar para esta liquidación en base
	 * a la consulta parámetro
	 * 
	 * @param sql
	 *            consulta con el detalle de la líneas
	 * @param childKeyColumnName
	 *            nombre de la columna clave del detalle
	 * @return lista de líneas con artículos a contabilizar
	 */
	private List<DocLine> getLines(String sql, String childKeyColumnName){
		List<DocLine> docLines = new ArrayList<DocLine>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		DocLine line;
		try {
			ps = DB.prepareStatement(sql, m_trxName);
			ps.setInt(1, getRecord_ID());
			rs = ps.executeQuery();
			while(rs.next()){
				line = new DocLine(p_vo.DocumentType, getRecord_ID(), rs.getInt(childKeyColumnName), m_trxName);
				line.loadAttributes(rs, p_vo);
				line.setAmount(rs.getBigDecimal("Amount"), null);
				docLines.add(line);
			}
		} catch (Exception e) {
			log.severe("Error obtaining lines from key " + childKeyColumnName + " : " + e.getMessage());
		} finally{
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				log.severe("Error obtaining lines from key " + childKeyColumnName + " : " + e2.getMessage());
			}
		}
		return docLines;
	}
	
	/**
	 * Carga retenciones
	 */
	private void loadWithholding(){
		String sql = "SELECT w.ad_org_id, w.C_WithholdingSettlement_ID, rt.m_product_id, w.amount "
					+ "FROM "+X_C_WithholdingSettlement.Table_Name+" w "
					+ "INNER JOIN "+X_C_RetencionSchema.Table_Name+" rs ON rs.C_RetencionSchema_ID = w.C_RetencionSchema_ID "
					+ "INNER JOIN "+X_C_RetencionType.Table_Name+" rt ON rt.C_RetencionType_ID = rs.C_RetencionType_ID "
					+ "WHERE w.C_CreditCardSettlement_ID = ? ";
		retentions = getLines(sql, X_C_WithholdingSettlement.Table_Name+"_ID");
	}
	
	/**
	 * Cargar líneas de conceptos con artículos a contabilizar para esta liquidación en base
	 * a la consulta parámetro
	 * 
	 * @param sql
	 *            consulta con el detalle de la líneas
	 * @param childKeyColumnName
	 *            nombre de la columna clave del detalle
	 * @return lista de líneas con artículos a contabilizar
	 */
	private List<DocLine> getConceptsLines(String conceptTableName){
		String sql = "SELECT cc.ad_org_id, "+conceptTableName+"_ID, csc.m_product_id, cc.amount "
				+ "FROM "+conceptTableName+" cc "
				+ "INNER JOIN "+X_C_CardSettlementConcepts.Table_Name+" csc ON csc.C_CardSettlementConcepts_ID = cc.C_CardSettlementConcepts_ID "
				+ "WHERE cc.C_CreditCardSettlement_ID = ? ";
		return getLines(sql, conceptTableName+"_ID");
	}
	
	/**
	 * Cargar Comisiones
	 */
	private void loadComissions(){
		comissions = getConceptsLines(X_C_CommissionConcepts.Table_Name);
	}
	
	/**
	 * Cargar Gastos
	 */
	private void loadExpenses(){
		expenses = getConceptsLines(X_C_ExpenseConcepts.Table_Name);
	}
	
	/**
	 * Crear las líneas referente a impuestos
	 * 
	 * @param fact
	 *            hecho actual
	 * @param acctType
	 *            tipo de cuenta contable a impactar de cada impuesto
	 * @param docTaxes
	 *            lista de impuestos
	 */
	private void createTaxesFactLines(Fact fact, int acctType, List<DocTax> docTaxes){
		for (DocTax docTax : docTaxes) {
			fact.createLine(null, docTax.getAccount(acctType, fact.getAcctSchema()), p_vo.C_Currency_ID,
					docTax.getAmount(), null);
		}
	}
	
	/**
	 * Crear las líneas referente a artículos
	 * 
	 * @param fact
	 *            hecho actual
	 * @param acctType
	 *            tipo de cuenta contable a impactar de cada líneas
	 * @param docLines
	 *            lista de líneas
	 */
	private void createFactLines(Fact fact, int acctType, List<DocLine> docLines){
		for (DocLine docLine : docLines) {
			fact.createLine(docLine, docLine.getProductInfoAccount(acctType, fact.getAcctSchema()), p_vo.C_Currency_ID,
					docLine.getAmount(), null);
		}
	}
	
	@Override
	public Fact createFact(MAcctSchema as) {
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		// Bruto
		// Se contabiliza sobre la cuenta bancaria de la EF asociada a
		// la EC de la liquidación
		fact.createLine(null, getAccount(Doc.ACCTTYPE_BankInTransit, as),
				p_vo.C_Currency_ID, null, getAmount(Doc.AMTTYPE_Gross));
		// Neto
		// Se contabiliza sobre la cuenta del payment generado por la
		// liquidación. Se asume que es un Cobro ya que el payment de la
		// liquidación se genera con tipo de documento de cobros
		if(!Util.isEmpty(bankAccountPaymentGeneratedID, true)){
			p_vo.C_BankAccount_ID = bankAccountPaymentGeneratedID;
			fact.createLine(null, getAccount(Doc.ACCTTYPE_UnallocatedCash, as),
					p_vo.C_Currency_ID, getAmount(Doc.AMTTYPE_Net), null);
		}
		// IVAs
		createTaxesFactLines(fact, DocTax.ACCTTYPE_TaxCredit, taxes);
		// Percepciones
		createTaxesFactLines(fact, DocTax.ACCTTYPE_TaxCredit, perceptions);
		// Retenciones
		createFactLines(fact, ProductInfo.ACCTTYPE_P_Revenue, retentions);
		// Comisiones
		createFactLines(fact, ProductInfo.ACCTTYPE_P_Expense, comissions);
		// Gastos
		createFactLines(fact, ProductInfo.ACCTTYPE_P_Expense, expenses);
			
		return fact;
	}

	@Override
	public String applyCustomSettings(Fact fact, int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
