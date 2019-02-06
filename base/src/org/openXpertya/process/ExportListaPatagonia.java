package org.openXpertya.process;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_BPartner_BankList;
import org.openXpertya.model.X_C_Currency;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.model.X_C_ElectronicPaymentBranch;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ExportListaPatagonia extends ExportBankList {

	/** Importe acumulado (total) */
	private Integer acumAmt = 0;

	public ExportListaPatagonia(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LP";
	}
	
	/**
	 * Devuelve una secuencia de caracteres conformada por un dato con ceros delante.
	 * @param data dato al final de la cadena.
	 * @param length longitud de la cadena resultante.
	 * @return Cadena de caracteres conformada por ceros y el dato ingresado, al final.
	 */
	private String zeroFill(String data, int length) {
		return fillField(data, "0", MExpFormatRow.ALIGNMENT_Right, length, null);
	}

	@Override
	protected String getFilePath() {
		StringBuffer filepath = new StringBuffer(getExportFormat().getFileName());
		// Armar el nombre del archivo
		String filename = "PO";
		filename += zeroFill(getBankListConfig().getRegisterNumber(), 7);
		filename += fillField(String.valueOf(getBankList().getDailySeqNo().intValue()), "0", MExpFormatRow.ALIGNMENT_Right, 3, null);
		filepath.append(filename);
		filepath.append(".").append(getFileExtension());
		return filepath.toString();
	}

	@Override
	protected String getFileHeader() {
		// Registro ID
		StringBuffer header = new StringBuffer("FH");
		// Id de Archivo
		header.append("PO");
		header.append(fillField(getBankListConfig().getRegisterNumber(), "0", MExpFormatRow.ALIGNMENT_Right, 7, null));
		header.append(dateFormat_yyyyMMdd.format(getBankList().getDateTrx()));
		header.append(fillField(String.valueOf(getBankList().getDailySeqNo().intValue()), "0", MExpFormatRow.ALIGNMENT_Right, 3, null));
		// Hora de creación del archivo
		header.append(dateFormat_HHmmss.format(getBankList().getDateTrx()));
		// Nro. secuencial del archivo p/adherente
		header.append(fillField(String.valueOf(getBankList().getTotalSeqNo().intValue()), "0", MExpFormatRow.ALIGNMENT_Right, 7, null));
		// Identificación de archivo
		header.append("ORDENDEPAGO");
		// Nro.de Convenio
		header.append("0000000"); // Nro. de convenio por default
		// Fecha del lote
		header.append(dateFormat_yyyyMMdd.format(getBankList().getDateTrx()));
		// Ejecución inmediata. Actualmente sin uso.
		header.append("S");
		// Espacio en blanco
		header.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 20, null));
		// Informante. ‘ADH’ – Adherente
		header.append("ADH");
		// Nro de informante
		header.append(fillField(getBankListConfig().getRegisterNumber(), "0", MExpFormatRow.ALIGNMENT_Right, 7, null));
		// Espacio en blanco
		header.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 80, null));
		return header.toString();
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	ahb.c_allocationhdr_id, ");
		sql.append("	ahb.documentno, ");
		sql.append("	(CASE WHEN p.a_name IS NOT NULL AND length(trim(p.a_name)) > 0 THEN p.a_name ELSE bp.name END) AS name, ");
		sql.append("	p.datetrx, ");
		sql.append("	p.c_payment_id, ");
		sql.append("	p.duedate as paymentduedate, ");
		sql.append("	p.dateemissioncheck, ");
		sql.append("	CASE ");
		sql.append("		WHEN (p.duedate > current_date) THEN p.duedate ");
		sql.append("		ELSE current_date + CAST('1 days' AS INTERVAL) ");
		sql.append("	END as duedate, ");
		sql.append("	p.dateacct, ");
		sql.append("	p.payamt, ");
		sql.append("	c.iso_code, ");
		sql.append("	Translate(bp.taxid, '-', '') AS taxid, ");
		sql.append("	ba.value as sucursal, ");
		sql.append("	COALESCE( ");
		sql.append("	  (SELECT email FROM ad_user u ");
		sql.append("	   WHERE u.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	   ORDER BY u.updated desc LIMIT 1 ");
		sql.append("		), ' ' ");
		sql.append("	) AS email, ");
		sql.append("	COALESCE( ");
		sql.append("		(SELECT accountno ");
		sql.append("		FROM c_bp_bankaccount AS bpa ");
		sql.append("		WHERE bpa.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("		ORDER BY accountno DESC ");
		sql.append("		LIMIT  1 ");
		sql.append("		), ' ' ");
		sql.append("	) AS cbu, ");
		sql.append("	COALESCE( ");
		sql.append("		(SELECT COUNT(*) ");
		sql.append("		FROM m_retencion_invoice ri ");
		sql.append("		WHERE ri.c_allocationhdr_id = ahb.c_allocationhdr_id ");
		sql.append("		), 0 ");
		sql.append("	) ret, ");
		sql.append("	(SELECT ");
		sql.append("		Array_agg( ");
		sql.append("			COALESCE( ");
		sql.append("				CASE WHEN ddt.docbasetype = 'API' ");
		sql.append("				THEN d.documentno ELSE c.documentno ");
		sql.append("				END, '') ");
		sql.append("			) AS documentno ");
		sql.append("	FROM ");
		sql.append("		" + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("		INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("			ON al.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("		LEFT JOIN " + X_C_Invoice.Table_Name + " AS d ");
		sql.append("			ON d.c_invoice_id = al.c_invoice_id ");
		sql.append("		LEFT JOIN " + X_C_DocType.Table_Name + " AS ddt ");
		sql.append("			ON ddt.c_doctype_id = d.c_doctypetarget_id ");
		sql.append("		LEFT JOIN " + X_C_Invoice.Table_Name + " AS c ");
		sql.append("			ON c.c_invoice_id = al.c_invoice_credit_id ");
		sql.append("		LEFT JOIN " + X_C_DocType.Table_Name + " AS cdt ");
		sql.append("			ON cdt.c_doctype_id = c.c_doctypetarget_id ");
		sql.append("	WHERE ");
		sql.append("		ah.c_allocationhdr_id = ahb.c_allocationhdr_id ");
		sql.append("	LIMIT 4 ");
		sql.append("	) invoices, ");
		sql.append("	bpbl.nottoorder ");
		sql.append("FROM ");
		sql.append("	c_electronic_payments lpp "); // Vista
		sql.append("	INNER JOIN " + X_C_AllocationHdr.Table_Name + " AS ahb ");
		sql.append("		ON ahb.c_allocationhdr_id = lpp.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_BPartner.Table_Name + " AS bp ");
		sql.append("		ON bp.c_bpartner_id = lpp.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " AS p ");
		sql.append("		ON p.c_payment_id = lpp.c_payment_id ");
		sql.append("	INNER JOIN " + X_C_Currency.Table_Name + " AS c ");
		sql.append("		ON c.c_currency_id = p.c_currency_id ");
		sql.append("	INNER JOIN " + X_C_BPartner_BankList.Table_Name + " bpbl ");
		sql.append("		ON bpbl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_ElectronicPaymentBranch.Table_Name + " AS ba ");
		sql.append("		ON ba.c_electronicpaymentbranch_id = bpbl.c_electronicpaymentbranch_id ");
		sql.append("WHERE ");
		sql.append("	lpp.c_banklist_id = ? ");

		return sql.toString();
	}

	@Override
	protected void writeRow(ResultSet rs) throws Exception {
		Integer payAmt = rs.getBigDecimal("payamt").abs().multiply(Env.ONEHUNDRED).intValue();
		String documentno = rs.getString("documentno");
		String invoices = "FACs/" + rs.getString("invoices").replace("{", "").replace("}", "").replace(",", "/").replace("\"", "");
		String cbu = Util.isEmpty(rs.getString("cbu"), true) ? "0" : rs.getString("cbu").trim();
		String branch = Util.isEmpty(rs.getString("sucursal")) ? " " : rs.getString("sucursal");
		
		// Registro ID
		StringBuffer row = new StringBuffer("PO");
		// Referencia del cliente
		row.append(fillField(documentno, " ", MExpFormatRow.ALIGNMENT_Left, 25, null));
		// Motivo del pago
		row.append(fillField(invoices, " ", MExpFormatRow.ALIGNMENT_Left, 105, null));
		// Fecha de ejecución de la orden
		row.append(dateFormat_yyyyMMdd.format(rs.getTimestamp("dateemissioncheck")));
		// Tipo de pago o medio de ejecución para concretarlo
		row.append("002");
		// Importe a pagar
		row.append(fillField(String.valueOf(payAmt), "0", MExpFormatRow.ALIGNMENT_Right, 15, null));
		// Moneda del pago (Codigo ISO de la divisa)
		row.append(rs.getString("iso_code"));
		// Fecha de vencimiento de CHPD
		row.append(dateFormat_yyyyMMdd.format(getNextWorkingDay(rs.getTimestamp("duedate")).getTime()));
		// Requerir Recibo oficial del Beneficiario en pagos con cheques
		row.append("S");
		// Cláusula No a la Orden
		row.append(rs.getString("nottoorder").equals("Y")?"S":"N");
		// Incluir firma en la impresión de cheques y CADJ
		row.append("S");
		// Acompañamiento de Comprobantes Adjuntos
		row.append(rs.getInt("ret") > 0 ? "ICA" : "NEC");
		// Texto referencial #1 asociado a la orden
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 80, null));
		// Texto referencial #2 asociado a la orden
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 80, null));
		// Texto referencial #3 asociado a la orden
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 80, null));
		// Instrucciones para el Customer Service del Banco
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 160, null));
		// Nro. de Beneficiario
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Left, 24, null));
		// Espacio en blanco
		row.append(" ");
		// Nombre del Beneficiario o proveedor
		row.append(fillField(rs.getString("name"), " ", MExpFormatRow.ALIGNMENT_Left, 60, null));
		// Tipo de documento del Beneficiario. 011 - CUIT
		row.append("011");
		// Nro. de CUIT/CUIL/CDI del Beneficiario, asignado por la AFIP
		row.append(rs.getString("taxid"));
		// Domicilio del Beneficiario. Actualmente no aplica.
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 120, null));
		// Código postal del domicilio del Beneficiario. Actualmente no aplica.
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 15, null));
		// Email del Beneficiario
		row.append(fillField(rs.getString("email"), " ", MExpFormatRow.ALIGNMENT_Left, 70, null));
		// Fax del Beneficiario
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 25, null));
		// Medios de comunicación con el beneficiario
		row.append(Util.isEmpty(rs.getString("email")) ? "   " : "EML");
		// Espacio en cero
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Left, 5, null));
		// Espacio en blanco
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 35, null));
		// CBU de la cuenta del Beneficiario a acreditar
		row.append(fillField(cbu, "0", MExpFormatRow.ALIGNMENT_Right, 22, null));
		// Sistema de la cta. a acreditar
		row.append("CC");
		// Moneda de la cuenta a acreditar en banco. (Codigo ISO de la divisa)
		row.append(rs.getString("iso_code"));
		// Espacio en blanco
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Left, 35, null));
		// Canal de entrega de pagos en cheques
		row.append("BCO");
		// Sucursal a la cual enviar cheque y sus comprobantes adjuntos
		row.append(fillField(branch, " ", MExpFormatRow.ALIGNMENT_Left, 8, null));
		// Espacio en cero
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 5, null));
		// CBU de la cuenta del adherente a ser debitada
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 22, null));
		// Sistema de la cta. a debitar
		row.append("CC");
		// Moneda de la cuenta a debitar. (Codigo ISO de la divisa)
		row.append(rs.getString("iso_code"));
		// Espacio en blanco.
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 35, null));
		
		acumAmt += payAmt;
		write(row.toString());
	}

	@Override
	protected String getFileFooter() {
		// Registro ID
		StringBuffer footer = new StringBuffer("FT");
		// Total de importes a pagar
		footer.append(fillField(String.valueOf(acumAmt), "0", MExpFormatRow.ALIGNMENT_Right, 25, null));
		// Total de registros del archivo
		footer.append(fillField(String.valueOf(getExportedLines() + 2), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		// Clave Checksum
		footer.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 8, null));
		// Espacio en blanco
		footer.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 40, null));
		return footer.toString();
	}

	@Override
	protected void validate() throws Exception {
		BankListConfigFieldsException blcfe = new BankListConfigFieldsException(getCtx(),
				getBankList().getC_DocType_ID(), new ArrayList<String>());
		if(Util.isEmpty(getBankListConfig().getRegisterNumber(), true)){
			blcfe.addField("RegisterNumber");
		}
		if(blcfe.getFields().size() > 0){
			throw blcfe;
		}
	}

}
