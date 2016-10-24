package org.openXpertya.process;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ExportListaPatagonia extends ExportBankList {

	private Integer acumAmt = 0;

	public ExportListaPatagonia(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LP";
	}

	@Override
	protected String getFilePath() {
		StringBuffer filepath = new StringBuffer(getExportFormat().getFileName());
		// Armar el nombre del archivo
		String filename = "PO";
		filename += getBankListConfig().getRegisterNumber();
		filename += fillField(String.valueOf(getBankList().getDailySeqNo().intValue()), "0", MExpFormatRow.ALIGNMENT_Right, 3, null);
		filepath.append(filename);
		filepath.append(".").append(getFileExtension());
		return filepath.toString();
	}

	@Override
	protected String getFileHeader() {
		StringBuffer header = new StringBuffer("FHPO");
		header.append(getBankListConfig().getRegisterNumber());
		header.append(dateFormat_yyyyMMdd.format(getBankList().getDateTrx()));
		header.append(fillField(String.valueOf(getBankList().getDailySeqNo().intValue()), "0", MExpFormatRow.ALIGNMENT_Right, 3, null));
		header.append(dateFormat_HHmmss.format(getBankList().getDateTrx()));
		header.append(fillField(String.valueOf(getBankList().getTotalSeqNo().intValue()), "0", MExpFormatRow.ALIGNMENT_Right, 7, null));
		header.append("ORDENDEPAGO");
		header.append("0000000"); // Nro de convenio por default
		header.append(dateFormat_yyyyMMdd.format(getBankList().getDateTrx()));
		header.append("S");
		header.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 20, null));
		header.append("ADH");
		header.append(getBankListConfig().getRegisterNumber());
		header.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 80, null));
		return header.toString();
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	ahb.c_allocationhdr_id, ");
		sql.append("	ahb.documentno, ");
		sql.append("	bp.name, ");
		sql.append("	p.datetrx, ");
		sql.append("	p.dateacct, ");
		sql.append("	p.payamt, ");
		sql.append("	c.iso_code, ");
		sql.append("	Translate(bp.taxid, '-', '') AS taxid, ");
		sql.append("	ba.sucursal, ");
		sql.append("	COALESCE( ");
		sql.append("		(SELECT accountno ");
		sql.append("		FROM c_bp_bankaccount AS bpa ");
		sql.append("		WHERE bpa.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("		ORDER BY accountno DESC ");
		sql.append("		LIMIT  1 ");
		sql.append("		), ' ' ");
		sql.append("	) AS cbu, ");
		sql.append("	COALESCE( ");
		sql.append("		(SELECT email ");
		sql.append("		FROM c_bpartner_location AS bpl ");
		sql.append("		WHERE bpl.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("		ORDER BY email DESC ");
		sql.append("		LIMIT  1 ");
		sql.append("		), ' ' ");
		sql.append("	) AS email, ");
		sql.append("	COALESCE( ");
		sql.append("		(SELECT COUNT(*) ");
		sql.append("		FROM m_retencion_invoice ri ");
		sql.append("		WHERE ri.c_allocationhdr_id = ahb.c_allocationhdr_id ");
		sql.append("		), 0 ");
		sql.append("	) ret, ");
		sql.append("	(SELECT ");
		sql.append("		Array_agg( ");
		sql.append("			Coalesce( ");
		sql.append("				CASE WHEN ddt.docbasetype = 'API' ");
		sql.append("				THEN d.documentno ELSE c.documentno ");
		sql.append("				END, '') ");
		sql.append("			) AS documentno ");
		sql.append("	FROM ");
		sql.append("		c_allocationhdr ah ");
		sql.append("		INNER JOIN c_allocationline AS al ");
		sql.append("			ON al.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("		LEFT JOIN c_invoice AS d ");
		sql.append("			ON d.c_invoice_id = al.c_invoice_id ");
		sql.append("		LEFT JOIN c_doctype AS ddt ");
		sql.append("			ON ddt.c_doctype_id = d.c_doctypetarget_id ");
		sql.append("		LEFT JOIN c_invoice AS c ");
		sql.append("			ON c.c_invoice_id = al.c_invoice_credit_id ");
		sql.append("		LEFT JOIN c_doctype AS cdt ");
		sql.append("			ON cdt.c_doctype_id = c.c_doctypetarget_id ");
		sql.append("	WHERE ");
		sql.append("		ah.c_allocationhdr_id = ahb.c_allocationhdr_id ");
		sql.append("	LIMIT  4 ");
		sql.append("	) invoices ");
		sql.append("FROM ");
		sql.append("	c_electronic_payments lpp ");
		sql.append("	INNER JOIN c_allocationhdr AS ahb ");
		sql.append("		ON ahb.c_allocationhdr_id = lpp.c_allocationhdr_id ");
		sql.append("	INNER JOIN c_bpartner AS bp ");
		sql.append("		ON bp.c_bpartner_id = lpp.c_bpartner_id ");
		sql.append("	INNER JOIN c_payment AS p ");
		sql.append("		ON p.c_payment_id = lpp.c_payment_id ");
		sql.append("	INNER JOIN c_currency AS c ");
		sql.append("		ON c.c_currency_id = p.c_currency_id ");
		sql.append("	INNER JOIN c_bankaccount AS ba ");
		sql.append("		ON ba.c_bankaccount_id = p.c_bankaccount_id ");
		sql.append("WHERE ");
		sql.append("	lpp.c_banklist_id = ? ");

		return sql.toString();
	}

	@Override
	protected void writeRow(ResultSet rs) throws Exception {
		StringBuffer row = new StringBuffer("PO");
		Integer payAmt = rs.getBigDecimal("payamt").abs().multiply(Env.ONEHUNDRED).intValue();
		row.append(fillField(rs.getString("documentno").replace(getOpPrefix(), "").replace(getOpSuffix(), ""), " ", MExpFormatRow.ALIGNMENT_Right, 25, null));
		row.append(fillField("FACs:/" + rs.getString("invoices").replace("{", "").replace("}", "").replace(",", "/"), "0", MExpFormatRow.ALIGNMENT_Right, 105, null));
		row.append(dateFormat_yyyyMMdd.format(rs.getTimestamp("datetrx")));
		row.append("002");
		row.append(fillField(String.valueOf(payAmt), "0", MExpFormatRow.ALIGNMENT_Right, 15, null));
		row.append(rs.getString("iso_code"));
		row.append(dateFormat_yyyyMMdd.format(rs.getTimestamp("dateacct")));
		row.append("S");
		row.append("N");
		row.append("S");
		row.append(rs.getInt("ret") > 0 ? "ICA" : "NEC");
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 80, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 80, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 80, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 160, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 25, null));
		row.append(fillField(rs.getString("name"), " ", MExpFormatRow.ALIGNMENT_Left, 60, null));
		row.append("011");
		row.append(rs.getString("taxid"));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 120, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 15, null));
		row.append(fillField(rs.getString("email"), " ", MExpFormatRow.ALIGNMENT_Left, 70, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 25, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 25, null));
		row.append(Util.isEmpty(rs.getString("email")) ? "   " : "EML");
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 5, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 35, null));
		row.append(fillField((Util.isEmpty(rs.getString("cbu")) ? "0" : rs.getString("cbu")), "0", MExpFormatRow.ALIGNMENT_Right, 22, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 2, null));
		row.append(rs.getString("iso_code"));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 35, null));
		row.append("BCO");
		row.append(fillField(Util.isEmpty(rs.getString("sucursal")) ? " " : rs.getString("sucursal"), " ", MExpFormatRow.ALIGNMENT_Right, 8, null));
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 5, null));
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 22, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 2, null));
		row.append(rs.getString("iso_code"));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 35, null));
		acumAmt += payAmt;
		write(row.toString());
	}

	@Override
	protected String getFileFooter() {
		StringBuffer footer = new StringBuffer("FT");
		footer.append(fillField(String.valueOf(acumAmt), "0", MExpFormatRow.ALIGNMENT_Right, 25, null));
		footer.append(fillField(String.valueOf(getExportedLines() + 2), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		return footer.toString();
	}

}
