package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.POCRGenerator;
import org.openXpertya.model.POCRGenerator.POCRType;
import org.openXpertya.model.X_C_CashLine;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class AutomaticAllocationGenerator extends SvrProcess {

	/** Entidad Comercial */
	private Integer bpartnerID;
	
	/** Transacción de ventas */
	private boolean isSOTrx = true;
	
	/** Asociación entre entidades comerciales y facturas pendientes a cobrar */
	private Map<Integer, List<AllocationGenerator.Invoice>> debits;
	
	/** Asociación entre entidades comerciales y cobros pendientes de imputar */
	private Map<Integer, List<AllocationGenerator.Document>> credits;
	
	/** Generador de imputaciones */
	private POCRGenerator allocGenerator;
	
	/** Tipo de documento Recibo de Cliente */
	private MDocType docType;
	
	/** Tipo de Allocation */
	private String allocType;
	
	/** Descripción de los recibos de cliente generados automáticamente */
	private String hdrDescription;
	
	/** Nros de Documento de las imputaciones generadas */
	private List<String> allocationsGenerated = new ArrayList<String>();
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) ;
            else if( name.equalsIgnoreCase( "C_BPartner_ID" )) {
            	bpartnerID = ((BigDecimal)para[ i ].getParameter()).intValue();
            }
            else if( name.equalsIgnoreCase( "IsSOTrx" )) {
            	isSOTrx = ((String)para[ i ].getParameter()).equals("Y");
            }
		}
		setAllocGenerator(new POCRGenerator(getCtx(),
				(isSOTrx() ? POCRType.CUSTOMER_RECEIPT : POCRType.PAYMENT_ORDER),
				get_TrxName()));
		setDocType(isSOTrx() ? MDocType.getDocType(getCtx(),
				MDocType.DOCTYPE_Recibo_De_Cliente, get_TrxName()) : MDocType
				.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago,
						get_TrxName()));
		setHdrDescription(Msg.getMsg(getCtx(),
				isSOTrx() ? "AutomaticGeneratedCustomerReceipt"
						: "AutomaticGeneratedPaymentOrder"));
		setAllocType(isSOTrx() ? MAllocationHdr.ALLOCATIONTYPE_CustomerReceipt
				: MAllocationHdr.ALLOCATIONTYPE_PaymentOrder);
	}

	/**
	 * Inicializar las facturas a cobrar
	 */
	protected void initializeDebits(){
		StringBuffer sql = new StringBuffer("SELECT i.ad_org_id, i.c_bpartner_id, i.c_invoice_id, i.c_currency_id, i.dateinvoiced, open " +
					 "FROM (SELECT i.ad_org_id, i.c_bpartner_id, i.c_invoice_id, i.c_currency_id, i.dateinvoiced, i.created, invoiceopen(i.c_invoice_id, 0) as open " +
					 "		FROM c_invoice as i " +
					 "		INNER JOIN c_doctype as dt ON dt.c_doctype_id = i.c_doctypetarget_id " +
					 "		INNER JOIN ad_orginfo as oi ON oi.ad_org_id = i.ad_org_id " +
					 "		WHERE i.ad_client_id = ? AND oi.allowautomaticallocation = 'Y' AND i.docstatus IN ('CO','CL') AND i.issotrx = '"+(isSOTrx()?"Y":"N")+"' AND dt.docbasetype = '"+(isSOTrx()?"ARI":"API")+"' ");
		if(!Util.isEmpty(bpartnerID, true)){
			sql.append(" AND i.c_bpartner_id = ? ");
		}
		sql.append(") as i " +
					"WHERE open > 0 " +
					"ORDER BY i.ad_org_id, i.c_bpartner_id, i.dateinvoiced, i.created");
		PreparedStatement ps = null;
		ResultSet rs = null;
		setDebits(new HashMap<Integer, List<AllocationGenerator.Invoice>>());
		List<AllocationGenerator.Invoice> invs;
		AllocationGenerator.Invoice inv;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			int i = 1;
			ps.setInt(i++, Env.getAD_Client_ID(getCtx()));
			if(!Util.isEmpty(bpartnerID, true)){
				ps.setInt(i++, bpartnerID);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				inv = (AllocationGenerator.Invoice) createDocument(
						X_C_Invoice.Table_Name, rs.getInt("c_invoice_id"),
						rs.getInt("ad_org_id"), rs.getInt("c_currency_id"),
						rs.getTimestamp("dateinvoiced"),
						rs.getBigDecimal("open"));
				invs = getDebits().get(rs.getInt("c_bpartner_id"));
				if(invs == null){
					invs = new ArrayList<AllocationGenerator.Invoice>();
				}
				invs.add(inv);
				getDebits().put(rs.getInt("c_bpartner_id"), invs);
			}
		} catch (Exception e) {
			log.severe(e.getMessage());
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				log.severe(e2.getMessage());
			}
		}
	}
	
	/**
	 * Inicializar las facturas a cobrar
	 */
	protected void initializeCredits(){
		boolean hasBP = !Util.isEmpty(bpartnerID, true);
		StringBuffer sql = new StringBuffer("SELECT document, ad_org_id, c_bpartner_id, documentid, c_currency_id, datetrx, created, open " +
											"FROM (SELECT 'C_Invoice' as document,i.ad_org_id, i.c_bpartner_id, i.c_invoice_id as documentid, i.c_currency_id, i.dateinvoiced as datetrx, i.created, invoiceopen(i.c_invoice_id, 0) as open " +
													"FROM c_invoice as i " +
													"INNER JOIN c_doctype as dt ON dt.c_doctype_id = i.c_doctypetarget_id " +
													"INNER JOIN ad_orginfo as oi ON oi.ad_org_id = i.ad_org_id " +
													"WHERE i.ad_client_id = ? AND oi.allowautomaticallocation = 'Y' AND i.issotrx = '"+(isSOTrx()?"Y":"N")+"' AND dt.docbasetype = '"+(isSOTrx()?"ARC":"APC")+"' AND i.docstatus IN ('CO','CL')");
		if(hasBP){
			sql.append(" AND i.c_bpartner_id = ? ");
		}
		sql.append("UNION ALL ");
		sql.append("SELECT 'C_Payment' as document,p.ad_org_id, p.c_bpartner_id, p.c_payment_id as documentid, p.c_currency_id, p.datetrx as datetrx, p.created, paymentavailable(p.c_payment_id) as open " +
					"FROM c_payment as p " +
					"INNER JOIN ad_orginfo as oi ON oi.ad_org_id = p.ad_org_id " +
					"WHERE p.ad_client_id = ? AND oi.allowautomaticallocation = 'Y' AND p.isreceipt = '"+(isSOTrx()?"Y":"N")+"' AND p.docstatus IN ('CO','CL') ");
		if(hasBP){
			sql.append(" AND p.c_bpartner_id = ? ");
		}
		sql.append("UNION ALL ");
		sql.append("SELECT 'C_CashLine' as document,cl.ad_org_id, cl.c_bpartner_id, cl.c_cashline_id as documentid, cl.c_currency_id, c.statementdate as datetrx, cl.created, abs(cashlineavailable(cl.c_cashline_id)) as open " +
					"FROM c_cashline as cl " +
					"INNER JOIN c_cash as c ON c.c_cash_id = cl.c_cash_id " +
					"INNER JOIN ad_orginfo as oi ON oi.ad_org_id = cl.ad_org_id " +
					"WHERE cl.ad_client_id = ? AND oi.allowautomaticallocation = 'Y' AND cl.amount "+(isSOTrx()?">":"<")+" 0 AND cl.docstatus IN ('CO','CL') ");
		if(hasBP){
			sql.append(" AND cl.c_bpartner_id = ? ");
		}
		sql.append(") as p ");
		sql.append("WHERE open > 0 ");
		sql.append("ORDER BY ad_org_id, c_bpartner_id, datetrx, created");
		PreparedStatement ps = null;
		ResultSet rs = null;
		setCredits(new HashMap<Integer, List<AllocationGenerator.Document>>());
		List<AllocationGenerator.Document> pays;
		AllocationGenerator.Document pay;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			int i = 1;
			Integer clientID = Env.getAD_Client_ID(getCtx());
			ps.setInt(i++, clientID);
			if(hasBP){
				ps.setInt(i++, bpartnerID);
			}
			ps.setInt(i++, clientID);
			if(hasBP){
				ps.setInt(i++, bpartnerID);
			}
			ps.setInt(i++, clientID);
			if(hasBP){
				ps.setInt(i++, bpartnerID);
			}
			rs = ps.executeQuery();
			while(rs.next()){
				pay = createDocument(rs.getString("document"),
						rs.getInt("documentid"), rs.getInt("ad_org_id"),
						rs.getInt("c_currency_id"), rs.getTimestamp("datetrx"),
						rs.getBigDecimal("open"));
				pays = getCredits().get(rs.getInt("c_bpartner_id"));
				if(pays == null){
					pays = new ArrayList<AllocationGenerator.Document>();
				}
				pays.add(pay);
				getCredits().put(rs.getInt("c_bpartner_id"), pays);
			}
		} catch (Exception e) {
			log.severe(e.getMessage());
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				log.severe(e2.getMessage());
			}
		}
	}
	
	/**
	 * 
	 * @param tableName
	 * @param docID
	 * @param orgID
	 * @param currencyID
	 * @param date
	 * @param amount
	 * @return El documento (factura, payment o cashline) dependiendo el nombre
	 *         de la tabla parámetro
	 */
	private AllocationGenerator.Document createDocument(String tableName, Integer docID, Integer orgID, Integer currencyID, Timestamp date, BigDecimal amount){
		AllocationGenerator.Document document = null;
		if(X_C_Invoice.Table_Name.equalsIgnoreCase(tableName)){
			document = getAllocGenerator().new Invoice(docID);
		}
		else if(X_C_Payment.Table_Name.equalsIgnoreCase(tableName)){
			document = getAllocGenerator().new Payment(docID);
		}
		else if(X_C_CashLine.Table_Name.equalsIgnoreCase(tableName)){
			document = getAllocGenerator().new CashLine(docID);
		}
		document.orgID = orgID;
		document.currencyId = currencyID;
		document.date = date;
		document.amount = amount;
		return document;
	}
	
	@Override
	protected String doIt() throws Exception {
		// Inicializar las facturas de las entidades comerciales a cobrar
		initializeDebits();
		// Inicializar los cobros de las entidades comerciales a imputar
		initializeCredits();
		AllocationGenerator.Document payMatch = null;
		// Iterar por las entidades comerciales
		for (Integer bpartnerID : getDebits().keySet()) {
			// Iterar por los débitos a imputar y por cada una crear un
			// allocation
			for (AllocationGenerator.Invoice debit : getDebits().get(bpartnerID)) {
				// Busco pagos hasta imputar el pendiente de la factura
				BigDecimal availableAmt = debit.getAvailableAmt();
				BigDecimal realAllocAmt;
				// Busco el pago que matchea con los datos parámetro
				payMatch = findCreditMatch(debit.orgID, bpartnerID, debit.currencyId);
				// Si existe al menos un pago a imputar, entonces creo la
				// cabecera de allocation
				if(payMatch != null){
					getAllocGenerator().setTrxName(get_TrxName());
					getAllocGenerator().createAllocationHdr(getAllocType());
					getAllocGenerator().getAllocationHdr().setC_DocType_ID(
							getDocType().getID());
					getAllocGenerator().getAllocationHdr().setC_Currency_ID(
							debit.currencyId);
					
				}
				while(payMatch != null && availableAmt.compareTo(BigDecimal.ZERO) > 0){
					// Si el pago es mayor o igual al pendiente del débito,
					// entonces acaba el pendiente del débito y se debe sumar el
					// monto imputado del crédito teniendo en cuenta este
					realAllocAmt = payMatch.getAvailableAmt().compareTo(
							availableAmt) >= 0 ? availableAmt : payMatch
							.getAvailableAmt();
					availableAmt = availableAmt.subtract(realAllocAmt);
					// Monto imputado en la línea del allocation, se los sumo al
					// pago y a la factura para variar el disponible
					payMatch.setAmountAllocated(payMatch.getAmountAllocated()
							.add(realAllocAmt));
					debit.setAmountAllocated(debit.getAmountAllocated().add(realAllocAmt));
					// Agrego el crédito
					getAllocGenerator().addCreditDocument(payMatch.getId(),
							realAllocAmt, payMatch.type);
					// Si el débito sigue teniendo disponible, entonces busco un
					// pago con disponible
					if(availableAmt.compareTo(BigDecimal.ZERO) > 0){
						payMatch = findCreditMatch(debit.orgID, bpartnerID, debit.currencyId);
					}
				}
				// Pasar todo lo imputado del débito para agregarlo como débito
				// al allocation y completar el allocation
				if(getAllocGenerator().getAllocationHdr() != null){
					getAllocGenerator().addDebitDocument(debit.getId(),
							debit.getAmountAllocated(), debit.type);
					// Actualizar la cabecera del allocation con información
					// relevante
					Timestamp date = new Timestamp(System.currentTimeMillis());
					getAllocGenerator().getAllocationHdr().setDescription(getHdrDescription());
					getAllocGenerator().getAllocationHdr().setC_BPartner_ID(bpartnerID);
					getAllocGenerator().getAllocationHdr().setDateAcct(date);
					getAllocGenerator().getAllocationHdr().setDateTrx(date);
					getAllocGenerator().getAllocationHdr().setIsManual(false);
					// Generar las líneas del allocation, actualizar la cabecera
					// y completar el allocation
					getAllocGenerator().generateLines();
					getAllocGenerator().getAllocationHdr().updateTotalByLines();
					if(!getAllocGenerator().getAllocationHdr().save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					getAllocGenerator().completeAllocation();
					getAllocationsGenerated().add(
							getAllocGenerator().getAllocationHdr()
									.getDocumentNo());
					getAllocGenerator().reset();
				}
			}
		}
		return getMsg();
	}

	/**
	 * @param orgID
	 * @param bpartnerID
	 * @param currencyID
	 * @return un crédito que cumpla con los criterios parámetro
	 */
	protected AllocationGenerator.Document findCreditMatch(Integer orgID, Integer bpartnerID, Integer currencyID){
		AllocationGenerator.Document match = null;
		List<AllocationGenerator.Document> bpCredits = getCredits().get(bpartnerID);
		AllocationGenerator.Document credit;
		if(bpCredits != null){
			// Busco el crédito que matchee con los datos parámetro y tenga disponible
			boolean matched = false;
			boolean matchOrg = !Util.isEmpty(orgID, true);
			boolean matchCurrency = !Util.isEmpty(currencyID, true);
			for (int i = 0; i < bpCredits.size() && !matched; i++) {
				credit = bpCredits.get(i); 
				// Si tiene disponible y matchea con los parámetros, sale
				if((credit.getAvailableAmt().compareTo(BigDecimal.ZERO) > 0)
						&& (matchOrg && orgID.intValue() == credit.orgID.intValue())
						&& (matchCurrency && currencyID.intValue() == credit.currencyId.intValue())){
					match = credit;
					matched = true;
				}
			}
		}
		return match;
	}
	
	public String getMsg(){
		StringBuffer msg = new StringBuffer();
		if (getAllocationsGenerated() != null
				&& getAllocationsGenerated().size() > 0) {
			// Creo el mensaje con la lista de allocations generated
			msg.append(Msg.getMsg(getCtx(), "AllocationsGenerated"));
			msg.append(":");
			for (String allocDocumentNo : getAllocationsGenerated()) {
				msg.append(" "+allocDocumentNo+", ");
			}
			msg.deleteCharAt(msg.lastIndexOf(","));
		}
		else{
			msg.append(Msg.getMsg(getCtx(),
					"NotExistsDebitsCreditsToAllocate"));
		}
		return msg.toString();
	}
	
	protected Map<Integer, List<AllocationGenerator.Invoice>> getDebits() {
		return debits;
	}

	protected void setDebits(Map<Integer, List<AllocationGenerator.Invoice>> debits) {
		this.debits = debits;
	}

	protected POCRGenerator getAllocGenerator() {
		return allocGenerator;
	}

	protected void setAllocGenerator(POCRGenerator allocGenerator) {
		this.allocGenerator = allocGenerator;
	}

	protected Map<Integer, List<AllocationGenerator.Document>> getCredits() {
		return credits;
	}

	protected void setCredits(Map<Integer, List<AllocationGenerator.Document>> credits) {
		this.credits = credits;
	}

	protected MDocType getDocType() {
		return docType;
	}

	protected void setDocType(MDocType docType) {
		this.docType = docType;
	}

	protected String getHdrDescription() {
		return hdrDescription;
	}

	protected void setHdrDescription(String hdrDescription) {
		this.hdrDescription = hdrDescription;
	}

	protected boolean isSOTrx() {
		return isSOTrx;
	}

	protected void setSOTrx(boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}

	protected String getAllocType() {
		return allocType;
	}

	protected void setAllocType(String allocType) {
		this.allocType = allocType;
	}

	protected List<String> getAllocationsGenerated() {
		return allocationsGenerated;
	}

	protected void setAllocationsGenerated(List<String> allocationsGenerated) {
		this.allocationsGenerated = allocationsGenerated;
	}

}
