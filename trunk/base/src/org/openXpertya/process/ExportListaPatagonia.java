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
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LP";
	}

	@Override
	protected String getFilePath(){
		StringBuffer filepath = new StringBuffer(getExportFormat().getFileName());
		// Armar el nombre del archivo
		String filename = "PO";
		filename += getBankListConfig().getRegisterNumber();
		filename += fillField(String.valueOf(getBankList().getDailySeqNo().intValue()), "0",
				MExpFormatRow.ALIGNMENT_Right, 3, null);
		filepath.append(filename);
		filepath.append(".").append(getFileExtension());
		return filepath.toString();
	}
	
	@Override
	protected String getFileHeader() {
		StringBuffer header = new StringBuffer("FHPO");
		header.append(getBankListConfig().getRegisterNumber());
		header.append(dateFormat_yyyyMMdd.format(getBankList().getDateTrx()));
		header.append(fillField(String.valueOf(getBankList().getDailySeqNo().intValue()), "0",
				MExpFormatRow.ALIGNMENT_Right, 3, null));
		header.append(dateFormat_HHmmss.format(getBankList().getDateTrx()));
		header.append(fillField(String.valueOf(getBankList().getTotalSeqNo().intValue()), "0",
				MExpFormatRow.ALIGNMENT_Right, 7, null));
		header.append("ORDENDEPAGO");
		header.append("0000000"); // Nro de convenio por default
		header.append(dateFormat_yyyyMMdd.format(getBankList().getDateTrx()));
		header.append("S");
		header.append(fillField(" ",  " ", MExpFormatRow.ALIGNMENT_Right, 20, null));
		header.append("ADH");
		header.append(getBankListConfig().getRegisterNumber());
		header.append(fillField(" ",  " ", MExpFormatRow.ALIGNMENT_Right, 80, null));
		return header.toString();
	}

	@Override 
	protected String getQuery(){
		StringBuffer query = new StringBuffer();
		query.append("select distinct ahb.c_allocationhdr_id, ahb.documentno, bp.name, p.datetrx, p.dateacct, p.payamt, c.iso_code, translate(bp.taxid,'-','') as taxid, ba.sucursal, ");
		query.append("coalesce((select accountno from c_bp_bankaccount as bpa where bpa.c_bpartner_id = bp.c_bpartner_id order by accountno desc limit 1),' ') as cbu, ");
		query.append("coalesce((select email from c_bpartner_location as bpl where bpl.c_bpartner_id = bp.c_bpartner_id order by email desc limit 1),' ') as email, ");
		query.append("coalesce((select count(*) from m_retencion_invoice ri where ri.c_allocationhdr_id = ahb.c_allocationhdr_id),0) ret, ");
		query.append("(select array_agg(coalesce(CASE WHEN ddt.docbasetype = 'API' THEN d.documentno ELSE c.documentno END,'')) as documentno "
					+ "from c_allocationhdr ah "
					+ "inner join c_allocationline as al on al.c_allocationhdr_id = ah.c_allocationhdr_id "
					+ "left join c_invoice as d on d.c_invoice_id = al.c_invoice_id "
					+ "left join c_doctype as ddt on ddt.c_doctype_id = d.c_doctypetarget_id "
					+ "left join c_invoice as c on c.c_invoice_id = al.c_invoice_credit_id "
					+ "left join c_doctype as cdt on cdt.c_doctype_id = c.c_doctypetarget_id "
					+ "where ah.c_allocationhdr_id = ahb.c_allocationhdr_id "
					+ "limit 4) invoices ");
		query.append("from c_lista_patagonia_payments lpp ");
		query.append("inner join c_allocationhdr as ahb on ahb.c_allocationhdr_id = lpp.c_allocationhdr_id ");
		query.append("inner join c_bpartner as bp on bp.c_bpartner_id = lpp.c_bpartner_id ");
		query.append("inner join c_payment as p on p.c_payment_id = lpp.c_payment_id ");
		query.append("inner join c_currency as c on c.c_currency_id = p.c_currency_id ");
		query.append("inner join c_bankaccount as ba on ba.c_bankaccount_id = p.c_bankaccount_id ");
		query.append("where lpp.c_banklist_id = ? ");
		return query.toString();
	}
	
	@Override
	protected void writeRow(ResultSet rs) throws Exception{
		StringBuffer row = new StringBuffer("PO");
		Integer payAmt = rs.getBigDecimal("payamt").abs().multiply(Env.ONEHUNDRED).intValue();
		row.append(fillField(rs.getString("documentno").replace(getOpPrefix(), "")
				.replace(getOpSuffix(), ""), " ", MExpFormatRow.ALIGNMENT_Right, 25, null));
		row.append(fillField("FACs:/"+rs.getString("invoices").replace("{", "").replace("}", "").replace(",", "/"), "0", MExpFormatRow.ALIGNMENT_Right, 105, null));
		row.append(dateFormat_yyyyMMdd.format(rs.getTimestamp("datetrx")));
		row.append("002");
		row.append(fillField(String.valueOf(payAmt), "0", MExpFormatRow.ALIGNMENT_Right, 15, null));
		row.append(rs.getString("iso_code"));
		row.append(dateFormat_yyyyMMdd.format(rs.getTimestamp("dateacct")));
		row.append("S");
		row.append("N");
		row.append("S");
		row.append(rs.getInt("ret") > 0?"ICA":"NEC");
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
		row.append(Util.isEmpty(rs.getString("email"))?"   ":"EML");
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 5, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 35, null));
		row.append(fillField((Util.isEmpty(rs.getString("cbu")) ? "0" : rs.getString("cbu")), "0",
				MExpFormatRow.ALIGNMENT_Right, 22, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 2, null));
		row.append(rs.getString("iso_code"));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 35, null));
		row.append("BCO");
		row.append(fillField(Util.isEmpty(rs.getString("sucursal"))?" ":rs.getString("sucursal"), " ", MExpFormatRow.ALIGNMENT_Right, 8, null));
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
		footer.append(fillField(String.valueOf(getExportedLines()+2), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		return footer.toString();
	}
	
}
