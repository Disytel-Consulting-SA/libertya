package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_BPartner_BankList;
import org.openXpertya.model.X_C_BPartner_Location;
import org.openXpertya.model.X_C_BankListLine;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.model.X_C_ElectronicPaymentBranch;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.model.X_C_Location;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_C_Region;
import org.openXpertya.model.X_C_RetencionSchema;
import org.openXpertya.model.X_C_RetencionType;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ExportListaGalicia extends ExportBankList {

	/** Provincias */
	private static Map<String, String> provincias;
	static {
		provincias = new HashMap<String, String>();
		provincias.put("0", "0000");
		provincias.put("CORE-C_Region-1000082", "0001");
		provincias.put("CORE-C_Region-1000083", "0002");
		provincias.put("CORE-C_Region-1000084", "0003");
		provincias.put("CORE-C_Region-1000087", "0004");
		provincias.put("CORE-C_Region-1000088", "0005");
		provincias.put("CORE-C_Region-1000085", "0006");
		provincias.put("CORE-C_Region-1000086", "0007");
		provincias.put("CORE-C_Region-1000089", "0008");
		provincias.put("CORE-C_Region-1000090", "0009");
		provincias.put("CORE-C_Region-1000091", "0010");
		provincias.put("CORE-C_Region-1000092", "0011");
		provincias.put("CORE-C_Region-1000093", "0012");
		provincias.put("CORE-C_Region-1000094", "0013");
		provincias.put("CORE-C_Region-1000095", "0014");
		provincias.put("CORE-C_Region-1000096", "0015");
		provincias.put("CORE-C_Region-1000097", "0016");
		provincias.put("CORE-C_Region-1000098", "0017");
		provincias.put("CORE-C_Region-1000099", "0018");
		provincias.put("CORE-C_Region-1000100", "0019");
		provincias.put("CORE-C_Region-1000101", "0020");
		provincias.put("CORE-C_Region-1000102", "0021");
		provincias.put("CORE-C_Region-1000103", "0022");
		provincias.put("CORE-C_Region-1000105", "0023");
		provincias.put("CORE-C_Region-1000104", "0040");
	}

	/** Contador de Cheques */
	private int checkCount = 0;

	public ExportListaGalicia(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LG";
	}

	@Override
	protected String getFileHeader() {
		// Control de fin de semana
		Calendar calendarDateTrx = Calendar.getInstance();
		calendarDateTrx.setTime(getBankList().getDateTrx());
		calendarDateTrx.add(Calendar.DATE, 2);
		int deltaDate = 0;
		if (calendarDateTrx.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			deltaDate = 2;
		} else if (calendarDateTrx.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			deltaDate = 1;
		}
		calendarDateTrx.add(Calendar.DATE, deltaDate);
		// Armar el string
		StringBuffer header = new StringBuffer("PC"); // Código de registro.
		header.append("D"); // Tipo de lista. Por el momento siempre es D.
		header.append(getBankList().getDocumentNo()); // Identificación de la Lista.
		header.append(dateFormat_ddMMyyyy.format(getBankList().getDateTrx())); // Fecha del proceso.
		header.append(getBankListConfig().getRegisterNumber()); // Cantidad de Registros
		header.append(fillField(getBankListConfig().getClientName(), " ", MExpFormatRow.ALIGNMENT_Right, 40, null));

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	COUNT(DISTINCT p.payamt) ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankListLine.Table_Name + " AS bll ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("		ON bll.C_AllocationHdr_ID = al.C_AllocationHdr_ID ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " AS p ");
		sql.append("		ON p.c_payment_id = al.c_payment_id ");
		sql.append("WHERE ");
		sql.append("	c_banklist_id = ?");

		header.append(fillField(String.valueOf(DB.getSQLValue(get_TrxName(), sql.toString(), getBankList().getID())), "0", MExpFormatRow.ALIGNMENT_Right, 6, null));

		sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	SUM(DISTINCT p.payamt) ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankListLine.Table_Name + " AS bll ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("		ON bll.C_AllocationHdr_ID = al.C_AllocationHdr_ID ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " AS p ");
		sql.append("		ON p.c_payment_id = al.c_payment_id ");
		sql.append("WHERE ");
		sql.append("	c_banklist_id = ?");

		BigDecimal res = DB.getSQLValueBD(get_TrxName(), sql.toString(), getBankList().getID());

		header.append(fillField(String.valueOf(res.abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN)), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));

		header.append(dateFormat_ddMMyyyy.format(calendarDateTrx.getTime()));
		header.append(dateFormat_ddMMyyyy.format(calendarDateTrx.getTime()));
		header.append(getBankListConfig().getSucursalDefault());
		header.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 54, null));
		header.append("001"); // Moneda Pesos
		header.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 22, null));
		header.append("GOF");
		header.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 123, null));
		calendarDateTrx.add(Calendar.DATE, 1);
		return header.toString();
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	ah.c_allocationhdr_id, ");
		sql.append("	p.c_payment_id, ");
		sql.append("	lgp.c_bpartner_id, ");
		sql.append("	lgp.payamt, ");
		sql.append("	(CASE WHEN p.a_name IS NOT NULL AND length(trim(p.a_name)) > 0 THEN p.a_name ELSE bp.name END) AS name, ");
		sql.append("	Translate(COALESCE(bp.taxid, p.a_cuit), '-', '') AS cuit, ");
		sql.append("	(SELECT ");
		sql.append("		l.address1 ");
		sql.append("	FROM ");
		sql.append("		" + X_C_BPartner_Location.Table_Name + " bpl ");
		sql.append("		INNER JOIN " + X_C_Location.Table_Name + " l ");
		sql.append("			ON l.c_location_id = bpl.c_location_id ");
		sql.append("	WHERE ");
		sql.append("		bpl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("		AND bpl.isactive = 'Y' ");
		sql.append("	ORDER BY ");
		sql.append("		bpl.updated DESC ");
		sql.append("	LIMIT  1 ");
		sql.append("	) AS address, ");
		sql.append("	(SELECT ");
		sql.append("		l.city ");
		sql.append("	FROM ");
		sql.append("		" + X_C_BPartner_Location.Table_Name + " bpl ");
		sql.append("		INNER JOIN " + X_C_Location.Table_Name + " l ");
		sql.append("			ON l.c_location_id = bpl.c_location_id ");
		sql.append("	WHERE ");
		sql.append("		bpl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("		AND bpl.isactive = 'Y' ");
		sql.append("	ORDER BY ");
		sql.append("		bpl.updated DESC ");
		sql.append("	LIMIT  1 ");
		sql.append("	) AS city, ");
		sql.append("	(SELECT l.postal ");
		sql.append("	FROM ");
		sql.append("		" + X_C_BPartner_Location.Table_Name + " bpl ");
		sql.append("		INNER JOIN " + X_C_Location.Table_Name + " l ");
		sql.append("			ON l.c_location_id = bpl.c_location_id ");
		sql.append("	WHERE ");
		sql.append("		bpl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("		AND bpl.isactive = 'Y' ");
		sql.append("	ORDER BY ");
		sql.append("		bpl.updated DESC ");
		sql.append("	LIMIT  1 ");
		sql.append("	) AS postal, ");
		sql.append("	ah.c_allocationhdr_id, ");
		sql.append("	ah.documentno, ");
		sql.append("	ah.datetrx AS allocationdate, ");
		sql.append("	p.duedate as paymentduedate, ");
		sql.append("	CASE ");
		sql.append("		WHEN (p.duedate > current_date) THEN p.duedate ");
		sql.append("		ELSE current_date + CAST('1 days' AS INTERVAL) ");
		sql.append("	END as duedate, ");
		sql.append("	ba.value as sucursal, ");
		sql.append("	bpbl.nombre_retirante ");
		sql.append("FROM ");
		sql.append("	c_electronic_payments lgp"); // Vista
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " p ");
		sql.append("		ON p.c_payment_id = lgp.c_payment_id ");
		sql.append("	INNER JOIN " + X_C_BPartner.Table_Name + " bp ");
		sql.append("		ON bp.c_bpartner_id = p.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("		ON ah.c_allocationhdr_id = lgp.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_BPartner_BankList.Table_Name + " bpbl ");
		sql.append("		ON bpbl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_ElectronicPaymentBranch.Table_Name + " AS ba ");
		sql.append("		ON ba.c_electronicpaymentbranch_id = bpbl.c_electronicpaymentbranch_id ");
		sql.append("WHERE ");
		sql.append("	lgp.c_banklist_id = ? ");

		return sql.toString();
	}

	protected void writeLine(ResultSet rs) throws Exception {
		checkCount++;
		// Escribir línea del cheque
		writeCheckLine(rs);
		// Separador de filas
		writeRowSeparator();
		// Un registro mas
		setExportedLines(getExportedLines() + 1);
		// Escribir OP del cheque
		writeOP(rs);
		// Escribir facturas
		writeInvoices(rs);
		// Escribir retenciones
		writeRetenciones(rs);
	}

	protected void writeCheckLine(ResultSet rs) throws Exception {
		BigDecimal payAmt = rs.getBigDecimal("payamt").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
		String address = Util.isEmpty(rs.getString("address"), true) ? "A" : rs.getString("address");
		String city = Util.isEmpty(rs.getString("city"), true) ? "C" : rs.getString("city");
		String postal = Util.isEmpty(rs.getString("postal"), true) ? "P" : rs.getString("postal");
		String cuit = Util.isEmpty(rs.getString("cuit"), true) ? "" : rs.getString("cuit");

		StringBuffer row = new StringBuffer("PD");
		// Número del registro
		row.append(fillField(String.valueOf(checkCount), "0", MExpFormatRow.ALIGNMENT_Right, 6, null));
		// Importe del cheque
		row.append(fillField(String.valueOf(payAmt), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		// Sucursal distribuidora
		row.append(fillField(rs.getString("sucursal"), " ", MExpFormatRow.ALIGNMENT_Left, 3, null));
		// Nombre/R. Social beneficiario
		row.append(fillField(rs.getString("name"), " ", MExpFormatRow.ALIGNMENT_Left, 50, null));
		// Dirección beneficiario
		row.append(fillField(address, " ", MExpFormatRow.ALIGNMENT_Left, 30, null));
		// Localidad beneficiario
		row.append(fillField(city, " ", MExpFormatRow.ALIGNMENT_Left, 20, null));
		// Código postal beneficiario
		row.append(fillField(postal, " ", MExpFormatRow.ALIGNMENT_Left, 6, null));
		// Número recibo a requerir. Completar si se conoce.
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 15, null));
		// CUIT del beneficiario
		row.append(fillField(cuit, " ", MExpFormatRow.ALIGNMENT_Right, 11, null));
		// Tipo de Documento a requerir
		row.append("03");
		// Teléfono del Beneficiario
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		// Nombre del Retirante
		row.append(fillField(rs.getString("nombre_retirante"), " ", MExpFormatRow.ALIGNMENT_Left, 30, null));
		// Tipo y Nro. de Documento del Retirante
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 11, null));
		// Condición del cheque
		row.append("2");
		// Espacio en blanco
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 25, null));
		// Código de Aviso
		row.append("2");
		// Código de Recibo
		row.append("0");
		// Espacio en blanco
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 16, null));
		// Orden de Pago
		row.append(fillField(rs.getString("documentno").replace(getOpPrefix(), "").replace(getOpSuffix(), ""), " ",
				MExpFormatRow.ALIGNMENT_Right, 35, null));
		// Moneda
		row.append("001");
		// Fecha de disposición de fondos
		row.append(dateFormat_ddMMyyyy.format(getNextWorkingDay(rs.getTimestamp("duedate")).getTime()));
		// Código de Provincia del Beneficiario
		row.append("01");
		// Información Compra
		row.append("0");
		// Destino de Comprobantes, Marca de Autogestión, Espacio libre
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 14, null));

		write(row.toString());
	}

	protected void writeOP(ResultSet rs) throws Exception {
		PreparedStatement ps = null;
		ResultSet rsop = null;
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	ah.c_allocationhdr_id, ");
		sql.append("	ah.documentno, ");
		sql.append("	SUM(al.amount) AS total, ");
		sql.append("	SUM(CASE WHEN ddt.docbasetype = 'API' THEN al.amount ELSE 0 END) AS debitos, ");
		sql.append("	SUM(CASE WHEN cdt.docbasetype = 'APC' THEN al.amount ELSE 0 END) AS creditos ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("		ON al.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("	LEFT JOIN " + X_C_Invoice.Table_Name + " AS d ");
		sql.append("		ON d.c_invoice_id = al.c_invoice_id ");
		sql.append("	LEFT JOIN " + X_C_DocType.Table_Name + " AS ddt ");
		sql.append("		ON ddt.c_doctype_id = d.c_doctypetarget_id ");
		sql.append("	LEFT JOIN " + X_C_Invoice.Table_Name + " AS c ");
		sql.append("		ON c.c_invoice_id = al.c_invoice_credit_id ");
		sql.append("	LEFT JOIN " + X_C_DocType.Table_Name + " AS cdt ");
		sql.append("		ON cdt.c_doctype_id = c.c_doctypetarget_id ");
		sql.append("WHERE ");
		sql.append("	ah.c_allocationhdr_id = ? ");
		sql.append("GROUP BY ");
		sql.append("	ah.c_allocationhdr_id, ");
		sql.append("	ah.documentno ");

		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, rs.getInt("c_allocationhdr_id"));
			rsop = ps.executeQuery();
			String documentno;
			while (rsop.next()) {
				StringBuffer op = new StringBuffer("O1");
				documentno = rsop.getString("documentno").replace(getOpPrefix(), "").replace(getOpSuffix(), "");
				op.append(fillField(documentno, "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
				BigDecimal debitos = rsop.getBigDecimal("debitos").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
				BigDecimal creditos = rsop.getBigDecimal("creditos").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
				BigDecimal total = rsop.getBigDecimal("total").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
				op.append(fillField(String.valueOf(debitos), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append(fillField(String.valueOf(debitos), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append(fillField(String.valueOf(creditos), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append(fillField(String.valueOf(total), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append("0");
				op.append(fillField("OBS", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
				op.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 209, null));
				write(op.toString());
				// Separador de filas
				writeRowSeparator();
				// Un registro mas
				setExportedLines(getExportedLines() + 1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rsop != null)
					rsop.close();
			} catch (Exception e2) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	protected void writeInvoices(ResultSet rs) throws Exception {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	i.c_invoice_id, ");
		sql.append("	i.documentno, ");
		sql.append("	i.numerocomprobante, ");
		sql.append("	i.dateinvoiced, ");
		sql.append("	i.grandtotal, ");
		sql.append("	COALESCE(ir.amt,0) AS retencion, ");
		sql.append("	COALESCE(ir.perc,0) AS retencion_porc, ");
		sql.append("	COALESCE(ir.base,0) AS retencion_base, ");
		sql.append("	COALESCE(ir.orden,1) AS orden ");
		sql.append("FROM (");
		sql.append("		SELECT ");
		sql.append("			CASE WHEN ddt.docbasetype = 'API' THEN al.c_invoice_id ");
		sql.append("				 ELSE al.c_invoice_credit_id END as c_invoice_id, ");
		sql.append("			ah.documentno ");
		sql.append("		FROM ");
		sql.append("			" + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("			INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("				ON al.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("			LEFT JOIN " + X_C_Invoice.Table_Name + " AS d ");
		sql.append("				ON d.c_invoice_id = al.c_invoice_id ");
		sql.append("			LEFT JOIN " + X_C_DocType.Table_Name + " AS ddt ");
		sql.append("				ON ddt.c_doctype_id = d.c_doctypetarget_id ");
		sql.append("			LEFT JOIN " + X_C_Invoice.Table_Name + " AS c ");
		sql.append("				ON c.c_invoice_id = al.c_invoice_credit_id ");
		sql.append("			LEFT JOIN " + X_C_DocType.Table_Name + " AS cdt ");
		sql.append("				ON cdt.c_doctype_id = c.c_doctypetarget_id ");
		sql.append("		WHERE ");
		sql.append("			ah.c_allocationhdr_id = ? ");
		sql.append("	) AS ia ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " AS i ");
		sql.append("		ON i.c_invoice_id = ia.c_invoice_id ");
		sql.append("	LEFT JOIN ( ");
		sql.append("		SELECT DISTINCT ");
		sql.append("			al.c_invoice_id, ");
		sql.append("			0 AS orden, ");
		sql.append("			amt_retenc AS amt, ");
		sql.append("			retencion_percent AS perc, ");
		sql.append("			baseimponible_amt AS base ");
		sql.append("		FROM ");
		sql.append("			" + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("			INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("				ON i.c_invoice_id = ri.c_invoice_id ");
		sql.append("			INNER JOIN " + X_C_AllocationLine.Table_Name + " al ");
		sql.append("				ON al.c_invoice_credit_id = i.c_invoice_id ");
		sql.append("			INNER JOIN " + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("				ON rs.c_retencionschema_id = ri.c_retencionschema_id ");
		sql.append("			INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("				ON rt.c_retenciontype_id = rs.c_retenciontype_id ");
		sql.append("			LEFT JOIN " + X_C_Region.Table_Name + " r ");
		sql.append("				ON r.c_region_id = rs.c_region_id ");
		sql.append("		WHERE ");
		sql.append("			ri.c_allocationhdr_id = ? ");
		sql.append("			AND i.docstatus IN ('CO','CL') ");
		sql.append("			AND retentiontype = 'B' ");
		sql.append("			AND r.ad_componentobjectuid = 'CORE-C_Region-1000083' ");
		sql.append("	) AS ir ");
		sql.append("		ON ir.c_invoice_id = i.c_invoice_id ");
		sql.append("ORDER BY ");
		sql.append("	orden, ");
		sql.append("	i.documentno ");

		PreparedStatement ps = null;
		ResultSet rsfc = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, rs.getInt("c_allocationhdr_id"));
			ps.setInt(2, rs.getInt("c_allocationhdr_id"));
			rsfc = ps.executeQuery();
			int i = 1;
			while (rsfc.next()) {
				// Comprobante 1
				StringBuffer fc = new StringBuffer("FC");
				fc.append(fillField(String.valueOf(i), "0", MExpFormatRow.ALIGNMENT_Right, 3, null));
				fc.append("FC");
				fc.append(fillField(String.valueOf(rsfc.getInt("numerocomprobante")), "0", MExpFormatRow.ALIGNMENT_Right, 12, null));
				fc.append(fillField("Factura de Proveedor", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
				fc.append(dateFormat_ddMMyyyy.format(rsfc.getTimestamp("dateinvoiced")));

				BigDecimal total = rsfc.getBigDecimal("grandtotal").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
				BigDecimal retencion = rsfc.getBigDecimal("retencion").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
				BigDecimal retencion_base = rsfc.getBigDecimal("retencion_base").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);

				fc.append(fillField(String.valueOf(total), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				fc.append(fillField(String.valueOf(retencion), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				fc.append(fillField(String.valueOf(retencion_base), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));

				// Comprobante 2
				fc.append("  ");
				fc.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 12, null));
				fc.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
				fc.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 8, null));
				fc.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				fc.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				fc.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 17, null));

				// Final
				fc.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 6, null));
				fc.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 6, null));
				fc.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 97, null));

				write(fc.toString());
				// Separador de filas
				writeRowSeparator();
				// Un registro mas
				setExportedLines(getExportedLines() + 1);
				i++;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rsfc != null)
					rsfc.close();
			} catch (Exception e2) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	protected void writeRetenciones(ResultSet rs) throws Exception {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	CASE WHEN retentiontype = 'I' THEN '01' ");
		sql.append("		WHEN retentiontype = 'G' THEN '02' ");
		sql.append("		WHEN retentiontype = 'B' THEN '03' ");
		sql.append("		WHEN retentiontype = 'S' THEN '04' ");
		sql.append("	ELSE '00' END AS tipo, ");
		sql.append("	CASE WHEN retentiontype = 'I' THEN '1' ");
		sql.append("		WHEN retentiontype = 'G' THEN '2' ");
		sql.append("		WHEN retentiontype = 'B' THEN '1' ");
		sql.append("		WHEN retentiontype = 'S' THEN '1' ");
		sql.append("	ELSE '0' END AS impuesto, ");
		sql.append("	COALESCE(r.ad_componentobjectuid,'0') AS provincia, ");
		sql.append("	i.documentno, ");
		sql.append("	iibb, ");
		sql.append("	i.grandtotal, ");
		sql.append("	ri.amt_retenc AS retencion, ");
		sql.append("	CASE WHEN ri.baseimponible_amt <= 0 THEN ri.pago_actual_amt ");
		sql.append("	ELSE ri.baseimponible_amt END AS base, ");
		sql.append("	ri.retencion_percent AS perc, ");
		sql.append("	ri.importe_no_imponible_amt AS noimponible, ");
		sql.append("	rs.name AS esquema ");
		sql.append("FROM ");
		sql.append("	" + X_M_Retencion_Invoice.Table_Name + " ri ");
		sql.append("	INNER JOIN " + X_C_Invoice.Table_Name + " i ");
		sql.append("		ON i.c_invoice_id = ri.c_invoice_id ");
		sql.append("	INNER JOIN " + X_C_BPartner.Table_Name + " bp ");
		sql.append("		ON bp.c_bpartner_id = i.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_RetencionSchema.Table_Name + " rs ");
		sql.append("		ON rs.c_retencionschema_id = ri.c_retencionschema_id ");
		sql.append("	INNER JOIN " + X_C_RetencionType.Table_Name + " rt ");
		sql.append("		ON rt.c_retenciontype_id = rs.c_retenciontype_id ");
		sql.append("	LEFT JOIN " + X_C_Region.Table_Name + " r ");
		sql.append("		ON r.c_region_id = rs.c_region_id ");
		sql.append("WHERE ");
		sql.append("	ri.c_allocationhdr_id = ? ");
		sql.append("	AND i.docstatus IN ('CO','CL') ");

		PreparedStatement ps = null;
		ResultSet rsre = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, rs.getInt("c_allocationhdr_id"));
			rsre = ps.executeQuery();
			while (rsre.next()) {
				// Escribir retención
				writeRetencion(rs, rsre);
				// Separador de filas
				writeRowSeparator();
				// Un registro mas
				setExportedLines(getExportedLines() + 1);
				// Escribir paraḿetros retención
				writeRetencionParams(rs, rsre);
				// Separador de filas
				writeRowSeparator();
				// Un registro mas
				setExportedLines(getExportedLines() + 1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rsre != null)
					rsre.close();
			} catch (Exception e2) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	private void writeRetencion(ResultSet rs, ResultSet rsre) throws Exception {
		StringBuffer re = new StringBuffer("C1");
		re.append(rsre.getString("tipo"));
		re.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 20, null));
		re.append(rsre.getString("impuesto"));
		re.append(provincias.get(rsre.getString("provincia"))); 
		re.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
		re.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 4, null));
		if (rsre.getString("documentno").length() <= 10) {
			re.append(fillField(rsre.getString("documentno"), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		} else {
			re.append(rsre.getString("documentno").substring(0, 10));
		}
		re.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 20, null));
		re.append("01");
		re.append(fillField(rsre.getString("iibb"), " ", MExpFormatRow.ALIGNMENT_Left, 20, null));
		re.append("02");
		String tmp = rs.getString("documentno").replace(getOpPrefix(), "").replace(getOpSuffix(), "");
		re.append(fillField(tmp, " ", MExpFormatRow.ALIGNMENT_Left, 35, null));
		re.append(dateFormat_ddMMyyyy.format(rs.getTimestamp("allocationdate")));

		BigDecimal total = rsre.getBigDecimal("grandtotal").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
		re.append(fillField(String.valueOf(total), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		re.append(dateFormat_MMyyyy.format(rs.getTimestamp("allocationdate")));
		re.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 137, null));
		write(re.toString());
	}

	private void writeRetencionParams(ResultSet rs, ResultSet rsre) throws Exception {
		StringBuffer rp = new StringBuffer("C2");
		rp.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 4, null));
		if (rsre.getString("documentno").length() <= 10) {
			rp.append(fillField(rsre.getString("documentno"), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		} else {
			rp.append(rsre.getString("documentno").substring(0, 10));
		}
		if (rsre.getString("esquema").length() <= 30) {
			rp.append(fillField(rsre.getString("esquema"), " ", MExpFormatRow.ALIGNMENT_Left, 30, null));
		} else {
			rp.append(rsre.getString("esquema").substring(0, 30));
		}
		BigDecimal noimponible = rsre.getBigDecimal("noimponible").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
		BigDecimal retencion = rsre.getBigDecimal("retencion").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
		BigDecimal retencion_base = rsre.getBigDecimal("base").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
		BigDecimal retencion_perc = rsre.getBigDecimal("perc").abs().multiply(Env.ONEHUNDRED).setScale(0, BigDecimal.ROUND_DOWN);

		rp.append("03");
		rp.append("1");
		rp.append(fillField(String.valueOf(retencion_base), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		rp.append("01");
		rp.append("1");
		rp.append(fillField(String.valueOf(noimponible), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		rp.append("07");
		rp.append("1");
		rp.append(fillField(String.valueOf(retencion_perc), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		rp.append("04");
		rp.append("1");
		rp.append(fillField(String.valueOf(retencion), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));

		rp.append("  ");
		rp.append(" ");
		rp.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		rp.append("  ");
		rp.append(" ");
		rp.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		rp.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 154, null));
		write(rp.toString());
	}

	@Override
	protected String getFileFooter() {
		return null;
	}

	@Override
	protected void validate() throws Exception{
		BankListConfigFieldsException blcfe = new BankListConfigFieldsException(getCtx(),
				getBankList().getC_DocType_ID(), new ArrayList<String>());
		if(Util.isEmpty(getBankListConfig().getRegisterNumber(), true)){
			blcfe.addField("RegisterNumber");
		}
		if(Util.isEmpty(getBankListConfig().getClientName(), true)){
			blcfe.addField("ClientName");
		}
		if(Util.isEmpty(getBankListConfig().getSucursalDefault(), true)){
			blcfe.addField("SucursalDefault");
		}
		if(blcfe.getFields().size() > 0){
			throw blcfe;
		}
	}

}
