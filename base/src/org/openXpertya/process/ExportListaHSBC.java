package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_BPartner_BankList;
import org.openXpertya.model.X_C_BankAccount;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.model.X_C_ElectronicPaymentBranch;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_C_Region;
import org.openXpertya.model.X_C_RetencionSchema;
import org.openXpertya.model.X_C_RetencionType;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.StringUtil;
import org.openXpertya.util.Util;

public class ExportListaHSBC extends ExportBankList {

	/** Tipo de Documento */
	private static Map<String, String> s_hsbc_autorizados;
	/** Tipos de Cuenta Bancaria */
	private static Map<String, String> s_hsbc_bankaccounttypes;
	static {
		// Tipos de Documento de Autorizados
		s_hsbc_autorizados = new HashMap<String, String>();
		s_hsbc_autorizados.put(X_C_BPartner.TAXIDTYPE_DNI, "50");
		s_hsbc_autorizados.put(X_C_BPartner.TAXIDTYPE_LC, "53");
		s_hsbc_autorizados.put(X_C_BPartner.TAXIDTYPE_LE, "52");
		s_hsbc_autorizados.put(X_C_BPartner.TAXIDTYPE_Pasaporte, "54");
		// Tipos de Cuenta Bancaria
		s_hsbc_bankaccounttypes = new HashMap<String, String>();
		s_hsbc_bankaccounttypes.put(X_C_BPartner_BankList.TRANSFERBANKACCOUNTTYPE_CurrentAccount, "01");
		s_hsbc_bankaccounttypes.put(X_C_BPartner_BankList.TRANSFERBANKACCOUNTTYPE_SavingsBank, "02");
	}
	
	/** Pago actual */
	private Integer currentPaymentID = null;

	/** Pago anterior */
	private Integer oldCurrentPaymentID = null;
	
	/** Factura actual */
	private Integer currentInvoiceID = null;

	/** Factura anterior */
	private Integer oldInvoiceID = null;

	/** Retención actual */
	private Integer currentRetentionID = null;

	/** Retención anterior */
	private Integer oldRetentionID = null;
	
	/** Cantidad total de líneas del archivo */
	protected int fileContentLength = 0;
	
	/** Contenido del archivo */
	protected StringBuffer fileContent = new StringBuffer();
	
	/** Contenido de registros DC para el pago actual */
	protected StringBuffer contentDC = new StringBuffer();
	
	/** Contenido de registros RE para el pago actual */
	protected StringBuffer contentRE = new StringBuffer();
	
	/** Formato de fechas MM/yyyy */
	protected DateFormat dateFormat_MM_yyyy = new SimpleDateFormat("MM/yyyy");
	
	public ExportListaHSBC(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LHSBC";
	}

	@Override
	protected String getFileHeader() {
		StringBuffer head = new StringBuffer("FH");
		head.append(getFieldSeparator());
		head.append(dateFormat_ddMMyyyy.format(getBankList().getDateTrx()));
		head.append(getFieldSeparator());
		head.append("PCBE");
		head.append(getFieldSeparator());
		head.append(fileContentLength);
		head.append(getFieldSeparator());
		head.append("A");
		head.append(getFieldSeparator());
		head.append("C");
		head.append(getFieldSeparator());
		head.append("AR");
		head.append(getFieldSeparator());
		head.append("HBAR");
		head.append(getFieldSeparator());
		// CUIT de la empresa
		MClient client = MClient.get(getCtx(), getBankList().getAD_Client_ID());
		head.append(client.getCUIT().replace("-", ""));
		head.append(getFieldSeparator());
		// Nombre del archivo
		head.append(getExportFile().getName());
		head.append(getFieldSeparator());
		return head.toString();
	}
	
	/**
	 * @param rs
	 *            result set actual
	 * @return línea del archivo ID
	 * @throws Exception
	 */
	protected String getIDLine(ResultSet rs) throws Exception{
		StringBuffer idLine = new StringBuffer();

		// Línea ID
		String accountno = rs.getString("accountno") != null ? rs.getString("accountno").replace("-", "") : ""; 
		idLine.append("ID");
		idLine.append(getFieldSeparator());
		idLine.append("I");
		idLine.append(getFieldSeparator());
		idLine.append("CC");
		idLine.append(getFieldSeparator());
		idLine.append("ARS");
		idLine.append(getFieldSeparator());
		idLine.append("N");
		idLine.append(getFieldSeparator());
		// Cuenta Corriente de la Empresa. Formato SSSTNNNNND, es el
		// número de la cuenta bancaria del pago
		idLine.append(StringUtil.trimStrict(accountno, 10));
		idLine.append(getFieldSeparator());
		// Fecha de Emisión de Pagos del Banco, fecha actual + 1
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(Env.getDate().getTime());
		today.add(Calendar.DATE, 1);
		idLine.append(dateFormat_ddMMyyyy.format(today.getTime()));
		idLine.append(getFieldSeparator());
		idLine.append(rs.getBigDecimal("payamt"));
		idLine.append(getFieldSeparator());
		idLine.append(rs.getString("allocationdocumentno"));
		idLine.append(getFieldSeparator());
		idLine.append(rs.getString("taxid").replace("-", ""));
		idLine.append(getFieldSeparator());
		idLine.append(dateFormat_ddMMyyyy.format(rs.getTimestamp("allocationdate")));
		idLine.append(getFieldSeparator());
		idLine.append("Y");
		idLine.append(getFieldSeparator());
		// No a la orden, en el formato se establece que siempre se debe
		// enviar Y, pero hay un check posible a utilizarse en la relación
		// entre la EC y la sucursal distribuidora, tabla
		// C_BPartner_BankList 
		idLine.append("Y");
		idLine.append(getFieldSeparator());
		// Tipo de Pago
		idLine.append(getTipoDePago(rs.getString("tendertype")));
		idLine.append(getFieldSeparator());
		idLine.append(rs.getString("sucursal"));
		idLine.append(getFieldSeparator());
		idLine.append(rs.getString("a_name") == null?"":StringUtil.trimStrict(rs.getString("a_name"), 60));
		idLine.append(getFieldSeparator());
		idLine.append(rs.getString("paydescription") == null?"":StringUtil.trimStrict(rs.getString("paydescription"), 120));
		idLine.append(getFieldSeparator());
		// CBU de la cuenta del proveedor para las transferencias
		idLine.append(rs.getString("cbu"));
		idLine.append(getFieldSeparator());
		// 01 = CC | 02 = CA (Cuenta Corriente o Caja de Ahorro) del proveedor
		idLine.append(rs.getString("transferbankaccounttype") == null ? "CC"
				: s_hsbc_bankaccounttypes.get(rs.getString("transferbankaccounttype")));
		idLine.append(getFieldSeparator());
		idLine.append("02");
		idLine.append(getFieldSeparator());
		idLine.append(rs.getString("a_cuit").replace("-", ""));
		idLine.append(getFieldSeparator());
		// Concepto de la transferencia, se deja como VAR (Varios)
		idLine.append(rs.getString("transferconcept") == null ? "VAR" : rs.getString("transferconcept"));
		idLine.append(getFieldSeparator());
		idLine.append(getRowSeparator());

		fileContentLength++;

		return idLine.toString();
	}
	
	/**
	 * @param rs
	 *            result set actual
	 * @return línea del archivo DC
	 * @throws Exception
	 */
	protected String getDCLine(ResultSet rs) throws Exception{
		StringBuffer dcLine = new StringBuffer();

		// Línea DC
		dcLine.append("DC");
		dcLine.append(getFieldSeparator());
		dcLine.append("I");
		dcLine.append(getFieldSeparator());
		dcLine.append(rs.getString("factura"));
		dcLine.append(getFieldSeparator());
		// Año de emisión de la factura
		Calendar cal = Calendar.getInstance();
		Timestamp dateInvoiced = rs.getTimestamp("dateinvoiced");
		cal.setTimeInMillis(dateInvoiced.getTime());
		dcLine.append(cal.get(Calendar.YEAR));
		dcLine.append(getFieldSeparator());
		// Fecha de vencimiento de la factura
		dcLine.append(dateFormat_ddMMyyyy.format(rs.getTimestamp("invoice_duedate")));
		dcLine.append(getFieldSeparator());
		dcLine.append(dateFormat_ddMMyyyy.format(dateInvoiced));
		dcLine.append(getFieldSeparator());
		dcLine.append(rs.getString("description") == null?"":StringUtil.trimStrict(rs.getString("description"),20));
		dcLine.append(getFieldSeparator());
		dcLine.append(rs.getBigDecimal("grandtotal"));
		dcLine.append(getFieldSeparator());
		dcLine.append(rs.getBigDecimal("grandtotal").signum() >= 0?"+":"-");
		dcLine.append(getFieldSeparator());
		dcLine.append("$");
		dcLine.append(getFieldSeparator());
		dcLine.append(getRowSeparator());

		fileContentLength++;

		return dcLine.toString();
	}
	
	/**
	 * @param rs
	 *            result set actual
	 * @return línea del archivo RE
	 * @throws Exception
	 */
	protected String getRELine(ResultSet rs) throws Exception{
		StringBuffer reLine = new StringBuffer();
		boolean isIIBB = X_C_RetencionType.RETENTIONTYPE_IngresosBrutos.equals(rs.getString("retentiontype"));
		// Línea RE
		reLine.append("RE");
		reLine.append(getFieldSeparator());
		reLine.append("I");
		reLine.append(getFieldSeparator());
		reLine.append(getRetencionType(rs.getString("retentiontype")));
		reLine.append(getFieldSeparator());
		reLine.append(rs.getString("retentionno"));
		reLine.append(getFieldSeparator());
		reLine.append(StringUtil.trimStrict(rs.getString("retentionname"),15));
		reLine.append(getFieldSeparator());
		// Código Oficial de la Retención
		// Por lo pronto se envía el ID del esquema de retención
		// Si es IIBB va 0000
		reLine.append(isIIBB ? "0000" : StringUtil.trimStrict(String.valueOf(rs.getInt("c_retencionschema_id")),4));
		reLine.append(getFieldSeparator());
		// Descripción Oficial de la Retención
		// Por lo pronto se envía el nombre del esquema de retención
		reLine.append(StringUtil.trimStrict(rs.getString("retentionname"),20));
		reLine.append(getFieldSeparator());
		// Base Imponible
		reLine.append(rs.getBigDecimal("baseimponible_amt"));
		reLine.append(getFieldSeparator());
		// Importe de Retención
		reLine.append(rs.getBigDecimal("amt_retenc"));
		reLine.append(getFieldSeparator());
		reLine.append("$");
		reLine.append(getFieldSeparator());
		reLine.append(dateFormat_MM_yyyy.format(rs.getTimestamp("retentiondate")));
		reLine.append(getFieldSeparator());
		// Resolución DGR
		reLine.append(isIIBB ? StringUtil.trimStrict(rs.getString("retentionname"), 15) : "");
		reLine.append(getFieldSeparator());
		// Provincia
		reLine.append(isIIBB ? StringUtil.trimStrict(rs.getString("retencion_provincia"), 20) : "");
		reLine.append(getFieldSeparator());
		// Porcentaje de Retención
		reLine.append(isIIBB ? rs.getBigDecimal("retencion_percent") : "");
		reLine.append(getFieldSeparator());
		// Alícuota de Retención
		reLine.append(isIIBB ? rs.getBigDecimal("retencion_percent") : "");
		reLine.append(getFieldSeparator());
		// Monto acumulado de Retención
		reLine.append(isIIBB ? "" : rs.getBigDecimal("retenciones_ant_acumuladas_amt"));
		reLine.append(getFieldSeparator());
		// Monto de pagos acumulados por el mismo concepto dentro del mes
		reLine.append(isIIBB ? "" : rs.getBigDecimal("pagos_ant_acumulados_amt"));
		reLine.append(getFieldSeparator());
		// Observaciones
		reLine.append("");
		reLine.append(getFieldSeparator());
		reLine.append(StringUtil.trimStrict(rs.getString("src_documentno") != null ? rs.getString("src_documentno")
				: (rs.getString("invoices") != null ? rs.getString("invoices").replace("{", "").replace("}", "")
						.replace(",", "/").replace("\"", "") : ""),1599));
		reLine.append(getFieldSeparator());
		reLine.append(getRowSeparator());

		fileContentLength++;

		return reLine.toString();
	}
	
	@Override
	protected void fillDocument() throws Exception{
		// Cargar todo el documento en las variables y luego impactarla en el
		// archivo ya que es necesario saber la cantidad de lineas que posee el
		// archivo como dato en el header 
		
		// Ejecutar la query
		PreparedStatement ps = DB.prepareStatement(getQuery(), get_TrxName(), true);
		// Agregar los parámetros
		setWhereClauseParams(ps);
		ResultSet rs = ps.executeQuery();
		// Iterar por los resultados
		while(rs.next()){
			// Realizar la importación
			doExport(rs);
		}
		
		// Realiza operaciones luego de la iteración de registros
		doPostExport(rs);
		
		rs.close();
		ps.close();
		
		// Escribir el archivo
		writeAllFile();
	}
	
	protected void writeAllFile() throws Exception{
		// Se suma 1 por la cabecera
		fileContentLength++;
		write(getFileHeader());
		write(getRowSeparator());
		write(fileContent.toString());
		setExportedLines(fileContentLength);
	}
	
	protected void doExport(ResultSet rs) throws Exception{
		// El contenido del archivo tiene la siguiente estructura:
		// Por cada pago (registro ID) se agregan los comprobantes (registro DC)
		// y sus retenciones (registro RE)
		// ID
		// DC
		// ...
		// RE
		// ...
		// ...
		// DC
		// ...
		// RE
		// ...
		// ...
		// ID
		// ...
		
		currentPaymentID = rs.getInt("c_payment_id");
		
		// Registro ID - Obtener los payments y OPs de la lista
		// Si la iteración anterior no posee un payment anterior significa que
		// es la primera y si el ID actual no coincide con el anterior se debe a
		// que es nuevo, se inserta en el contenido
		if(Util.isEmpty(oldCurrentPaymentID, true) || oldCurrentPaymentID.intValue() != currentPaymentID.intValue()){
			fileContent.append(contentDC);
			fileContent.append(contentRE);
			fileContent.append(getIDLine(rs));
			oldCurrentPaymentID = currentPaymentID;
			contentDC = new StringBuffer();
			contentRE = new StringBuffer();
		}
		
		currentInvoiceID = rs.getInt("c_invoice_id");
		
		// Registro DC - Obtener los comprobantes de cada OP
		// Si la iteración anterior no posee un invoice significa que es la
		// primera y si el ID actual no coincide con el anterior se debe a que
		// es nuevo, se inserta siempre que exista en el result set
		if (!Util.isEmpty(currentInvoiceID, true) && 
				(Util.isEmpty(oldInvoiceID, true) || oldInvoiceID.intValue() != currentInvoiceID.intValue())) {
			contentDC.append(getDCLine(rs));
			oldInvoiceID = currentInvoiceID;
		}
		
		currentRetentionID = rs.getInt("retencion_id");
		
		// Regitro RE - Obtener las retenciones de cada OP
		// Si la iteración anterior no posee una retención significa que es la
		// primera y si el ID actual no coincide con el anterior se debe a que
		// es nuevo, se inserta siempre que exista en el result set
		if (!Util.isEmpty(currentRetentionID, true) && 
				(Util.isEmpty(oldRetentionID, true) || oldRetentionID.intValue() != currentRetentionID.intValue())) {
			contentRE.append(getRELine(rs));
			oldRetentionID = currentRetentionID;
		}
	}
	
	protected void doPostExport(ResultSet rs) throws Exception{
		// Si quedó información en los contenidos DC o RE, lo agrego al
		// contenido global
		if(!Util.isEmpty(contentDC.toString(), true)){
			fileContent.append(contentDC);
			contentDC = new StringBuffer();
		}
		if(!Util.isEmpty(contentRE.toString(), true)){
			fileContent.append(contentRE);
			contentDC = new StringBuffer();
		}
	}
	
	@Override
	protected String getQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT ");
		sql.append("	lgp.c_bpartner_id, ");
		sql.append("	bp.value, ");
		sql.append("	bp.name, ");
		sql.append("	bp.taxid, ");
		sql.append("	bp.iibb, ");
		sql.append("	epb.value as sucursal, ");
		
		sql.append("	ba.accountno, ");
		sql.append("	bpbl.cbu, ");
		sql.append("	bpbl.transferbankaccounttype, ");
		sql.append("	bpbl.transferconcept, ");
		
		sql.append("	ah.c_allocationhdr_id, ");
		sql.append("	ah.documentno as allocationdocumentno, ");
		sql.append("	ah.dateacct as allocationdate, ");
		sql.append("	p.c_payment_id, ");
		sql.append("	p.datetrx, ");
		sql.append("	p.payamt, ");
		sql.append("	p.c_payment_id, ");
		sql.append("	p.duedate, ");
		sql.append("	p.tendertype, ");
		sql.append("	p.description as paydescription, ");
		sql.append("	coalesce(p.a_name, bp.name) as a_name, ");
		sql.append("	case when p.tendertype = 'A' then bp.taxid else p.a_cuit end as a_cuit, ");
		
		sql.append("	i.c_invoice_id, ");
		sql.append("	i.documentno as factura, ");
		sql.append("	i.dateinvoiced, ");
		sql.append("	i.description, ");
		sql.append("	i.grandtotal, ");
		sql.append("	(select duedate "
						+ "from c_invoicepayschedule ips "
						+ "where ips.c_invoice_id = i.c_invoice_id "
						+ "order by duedate desc "
						+ "limit 1) as invoice_duedate, ");
		
		sql.append("	ri.c_invoice_id as retencion_id, ");
		sql.append("	rt.retentiontype, ");
		sql.append("	cr.documentno as retentionno, ");
		sql.append("	cr.description as retention_descr, ");
		sql.append("	cr.dateacct as retentiondate, ");
		sql.append("	rs.c_retencionschema_id, ");
		sql.append("	rs.name as retentionname, ");
		sql.append("	ri.baseimponible_amt, ");
		sql.append("	ri.amt_retenc, ");
		sql.append("	ri.pagos_ant_acumulados_amt, ");
		sql.append("	ri.retenciones_ant_acumuladas_amt, ");
		sql.append("	ri.retencion_percent, ");
		sql.append("	rr.name as retencion_provincia, ");
		sql.append("	rr.jurisdictioncode, ");
		sql.append("	si.documentno as src_documentno, ");
		sql.append("		(SELECT Array_agg( distinct ");
		sql.append("			COALESCE( ");
		sql.append("				CASE WHEN ddt.docbasetype = 'API' ");
		sql.append("				THEN d.documentno ELSE c.documentno ");
		sql.append("				END, '') ");
		sql.append("			) AS documentno ");
		sql.append("	FROM ");
		sql.append("		" + X_C_AllocationHdr.Table_Name + " ahi ");
		sql.append("		INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("			ON al.c_allocationhdr_id = ahi.c_allocationhdr_id ");
		sql.append("		LEFT JOIN " + X_C_Invoice.Table_Name + " AS d ");
		sql.append("			ON d.c_invoice_id = al.c_invoice_id ");
		sql.append("		LEFT JOIN " + X_C_DocType.Table_Name + " AS ddt ");
		sql.append("			ON ddt.c_doctype_id = d.c_doctypetarget_id ");
		sql.append("		LEFT JOIN " + X_C_Invoice.Table_Name + " AS c ");
		sql.append("			ON c.c_invoice_id = al.c_invoice_credit_id ");
		sql.append("		LEFT JOIN " + X_C_DocType.Table_Name + " AS cdt ");
		sql.append("			ON cdt.c_doctype_id = c.c_doctypetarget_id ");
		sql.append("	WHERE ");
		sql.append("		ah.c_allocationhdr_id = ahi.c_allocationhdr_id ");
		sql.append("	) as invoices ");
		
		sql.append("FROM ");
		sql.append("	c_electronic_payments lgp"); // Vista
		sql.append("	INNER JOIN " + X_C_BPartner.Table_Name + " bp ");
		sql.append("		ON bp.c_bpartner_id = lgp.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_BPartner_BankList.Table_Name + " bpbl ");
		sql.append("		ON bpbl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_ElectronicPaymentBranch.Table_Name + " AS epb ");
		sql.append("		ON epb.c_electronicpaymentbranch_id = bpbl.c_electronicpaymentbranch_id ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " p ");
		sql.append("		ON p.c_payment_id = lgp.c_payment_id ");
		sql.append("	INNER JOIN " + X_C_BankAccount.Table_Name + " ba ");
		sql.append("		ON ba.c_bankaccount_id = p.c_bankaccount_id ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " al ");
		sql.append("		ON al.c_payment_id = p.c_payment_id ");
		sql.append("	INNER JOIN " + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("		ON ah.c_allocationhdr_id = al.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("		ON i.c_invoice_id = al.c_invoice_id ");
		sql.append("	LEFT JOIN " + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("		ON ri.c_allocationhdr_id = ah.c_allocationhdr_id "); // Retención
		sql.append("	LEFT JOIN " + X_C_Invoice.Table_Name + " cr ");
		sql.append("		ON cr.c_invoice_id = ri.c_invoice_id "); 
		sql.append("	LEFT JOIN " + X_C_Invoice.Table_Name + " si ");
		sql.append("		ON si.c_invoice_id = ri.c_invoice_src_id "); // Factura origen de la Retención, sólo válido para IIBB por cada factura
		sql.append("	LEFT JOIN " + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("		ON rs.C_RetencionSchema_id = ri.C_RetencionSchema_id "); 
		sql.append("	LEFT JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rt.c_retenciontype_id = rs.c_retenciontype_id "); 
		sql.append("	LEFT JOIN " + X_C_Region.Table_Name + " rr ");
		sql.append("		ON rr.c_region_id = rs.c_region_id ");
		
		sql.append("WHERE ");
		sql.append("	lgp.c_banklist_id = ? ");
		sql.append("ORDER BY ah.documentno, p.c_payment_id, i.c_invoice_id, ri.c_invoice_id ");
		
		return sql.toString();
	}
	
	/**
	 * Tipo de Pago que espera el archivo
	 * @param tendertype tipo de pago de la base
	 * @return N = Cheque Común | D = Cheque Diferido | T = Transferencia
	 */
	private String getTipoDePago(String tendertype){
		// Por lo pronto en el sistema, el concepto de cheque común y cheque diferido creería que se tratan de la misma manera, se envía cheque común 
		String tipo = "C";
		if(MPayment.TENDERTYPE_DirectDeposit.equals(tendertype)){
			tipo = "T";
		}
		return tipo;
	}
	
	/**
	 * @param retentionType
	 *            tipo de retención de LY
	 * @return el tipo de retención según la lista HSBC
	 */
	private String getRetencionType(String retentionType){
		String retType = retentionType;
		if (!Util.isEmpty(retentionType) 
				&& !(X_C_RetencionType.RETENTIONTYPE_Ganancias.equals(retentionType)
				|| X_C_RetencionType.RETENTIONTYPE_IngresosBrutos.equals(retentionType)
				|| X_C_RetencionType.RETENTIONTYPE_IVA.equals(retentionType))) {
			retType = "O";
		}
		return retType;
	}
	
	@Override
	protected String getFileFooter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validate() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected Map<String, String> initInvalidCaracters(){
		Map<String, String> invalids = super.initInvalidCaracters();
		invalids.put("|", null);
		invalids.put("¬", null);
		invalids.put("!", null);
		invalids.put("#", null);
		invalids.put("&", null);
		invalids.put("(", null);
		invalids.put("(", null);
		invalids.put("?", null);
		invalids.put("'", null);
		invalids.put("¿", null);
		invalids.put("¡", null);
		invalids.put("{", null);
		invalids.put("}", null);
		invalids.put("[", null);
		invalids.put("]", null);
		invalids.put("^", null);
		invalids.put("~", null);
		invalids.put("¨", null);
		invalids.put("*", null);
		invalids.put("ñ", null);
		invalids.put("°", null);
		invalids.put("`", null);
		invalids.put("\\", null);
		invalids.put("á", "a");
		invalids.put("é", "e");
		invalids.put("í", "i");
		invalids.put("ó", "o");
		invalids.put("ú", "u");
		return invalids;
	}
}
