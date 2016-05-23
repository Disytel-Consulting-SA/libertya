package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ExportListaGalicia extends ExportBankList {

	/** Provincias */
	private static Map<String, String> provincias;
	static{
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
	
	/** Fecha de acreditación */
	private Date dateEmission;
	
	public ExportListaGalicia(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
		// TODO Auto-generated constructor stub
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
		if(calendarDateTrx.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			deltaDate = 2;
		}
		else if(calendarDateTrx.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			deltaDate = 1;
		}
		calendarDateTrx.add(Calendar.DATE, deltaDate);
		// Armar el string
		StringBuffer header = new StringBuffer("PCD");
		header.append(getBankList().getDocumentNo());
		header.append(dateFormat_ddMMyyyy.format(getBankList().getDateTrx()));
		header.append(getBankListConfig().getRegisterNumber());
		header.append(fillField(getBankListConfig().getClientName(), " ", MExpFormatRow.ALIGNMENT_Left, 40, null));
		header.append(String.valueOf(DB.getSQLValue(get_TrxName(),
				"SELECT count(*) FROM c_banklistline WHERE c_banklist_id = ?", getBankList().getID())));
		header.append(String.valueOf(DB.getSQLValueBD(get_TrxName(),
				"SELECT sum(p.payamt) FROM c_banklistline as bll INNER JOIN c_payment as p ON p.c_payment_id = bll.c_payment_id WHERE c_banklist_id = ?",
				getBankList().getID()).abs().multiply(Env.ONEHUNDRED).intValue()));
		header.append(dateFormat_ddMMyyyy.format(calendarDateTrx.getTime()));
		header.append(dateFormat_ddMMyyyy.format(calendarDateTrx.getTime()));
		header.append(getBankListConfig().getSucursalDefault());
		header.append(fillField(" ",  " ", MExpFormatRow.ALIGNMENT_Right, 54, null));
		header.append("001"); // Moneda Pesos
		header.append(fillField(" ",  " ", MExpFormatRow.ALIGNMENT_Right, 22, null));
		header.append("GOF");
		header.append(fillField(" ",  " ", MExpFormatRow.ALIGNMENT_Right, 123, null));
		calendarDateTrx.add(Calendar.DATE, 1);
		dateEmission = calendarDateTrx.getTime();
		return header.toString();
	}

	@Override 
	protected String getQuery(){
		StringBuffer query = new StringBuffer();
		query.append("select distinct lgp.c_payment_id, lgp.c_bpartner_id, lgp.payamt, coalesce(p.a_name,bp.name) as name, translate(coalesce(p.a_cuit, bp.taxid),'-','') as cuit, (select l.address1 from c_bpartner_location bpl inner join c_location l on l.c_location_id = bpl.c_location_id where bpl.c_bpartner_id = bp.c_bpartner_id and bpl.isactive = 'Y' order by bpl.updated desc limit 1) as address, (select l.city from c_bpartner_location bpl inner join c_location l on l.c_location_id = bpl.c_location_id where bpl.c_bpartner_id = bp.c_bpartner_id and bpl.isactive = 'Y' order by bpl.updated desc limit 1) as city , (select l.postal from c_bpartner_location bpl inner join c_location l on l.c_location_id = bpl.c_location_id where bpl.c_bpartner_id = bp.c_bpartner_id and bpl.isactive = 'Y' order by bpl.updated desc limit 1) as postal, ah.c_allocationhdr_id, ah.documentno, ah.datetrx as allocationdate, p.duedate, ba.sucursal ");
		query.append("from c_lista_galicia_payments lgp ");
		query.append("inner join c_payment p on p.c_payment_id = lgp.c_payment_id ");
		query.append("inner join c_bpartner bp on bp.c_bpartner_id = p.c_bpartner_id ");
		query.append("inner join c_allocationhdr ah on ah.c_allocationhdr_id = lgp.c_allocationhdr_id ");
		query.append(
				"inner join c_bpartner_banklist as bpbl on bpbl.c_bpartner_id = bp.c_bpartner_id and bpbl.isactive = 'Y' and bpbl.c_doctype_id = ")
				.append(getBankList().getC_DocType_ID());
		query.append("left join c_bankaccount as ba on ba.c_bankaccount_id = bpbl.c_bankaccount_id ");
		query.append("where lgp.c_banklist_id = ? ");
		return query.toString();
	}
	
	protected void writeLine(ResultSet rs) throws Exception{
		checkCount++;
		// Escribir línea del cheque
		writeCheckLine(rs);
		// Separador de filas
		writeRowSeparator();
		// Un registro mas
		setExportedLines(getExportedLines()+1);
		// Escribir OP del cheque
		writeOP(rs);
		// Escribir facturas
		writeInvoices(rs);
		// Escribir retenciones
		writeRetenciones(rs);
	}
	
	protected void writeCheckLine(ResultSet rs) throws Exception{
		StringBuffer row = new StringBuffer("PD");
		row.append(fillField(String.valueOf(checkCount), "0", MExpFormatRow.ALIGNMENT_Right, 6, null));
		Integer payAmt = rs.getBigDecimal("payamt").abs().multiply(Env.ONEHUNDRED).intValue();
		row.append(fillField(String.valueOf(payAmt), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		row.append(fillField(rs.getString("sucursal"), " ", MExpFormatRow.ALIGNMENT_Left, 3, null));
		row.append(fillField(rs.getString("name"), " ", MExpFormatRow.ALIGNMENT_Left, 50, null));
		row.append(fillField((Util.isEmpty(rs.getString("address"))?"A":rs.getString("address")), " ", MExpFormatRow.ALIGNMENT_Left, 30, null));
		row.append(fillField((Util.isEmpty(rs.getString("city"))?"C":rs.getString("city")), " ", MExpFormatRow.ALIGNMENT_Left, 20, null));
		row.append(fillField((Util.isEmpty(rs.getString("postal"))?"P":rs.getString("postal")), " ", MExpFormatRow.ALIGNMENT_Left, 6, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 15, null));
		row.append(rs.getString("cuit"));
		row.append("03");
		row.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 11, null));
		row.append("2");
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 25, null));
		row.append("2");
		row.append("0");
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 16, null));
		row.append(fillField(rs.getString("documentno"), " ", MExpFormatRow.ALIGNMENT_Left, 35, null));
		row.append("001");
		Date dueDate = rs.getDate("duedate");
		if(dueDate.before(getBankList().getDateTrx())){
			dueDate = dateEmission;
		}
		row.append(dateFormat_ddMMyyyy.format(dateEmission));
		row.append("01");
		row.append("0");
		row.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 14, null));
		write(row.toString());
	}
	
	protected void writeOP(ResultSet rs) throws Exception{
		PreparedStatement ps = null;
		ResultSet rsop = null;
		StringBuffer sql = new StringBuffer("select distinct ah.c_allocationhdr_id, ah.documentno, sum(al.amount) as total, sum(CASE WHEN ddt.docbasetype = 'API' THEN al.amount ELSE 0 END) as debitos, sum(CASE WHEN cdt.docbasetype = 'APC' THEN al.amount ELSE 0 END) as creditos ");
		sql.append("from c_allocationhdr ah ");
		sql.append("inner join c_allocationline as al on al.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("left join c_invoice as d on d.c_invoice_id = al.c_invoice_id ");
		sql.append("left join c_doctype as ddt on ddt.c_doctype_id = d.c_doctypetarget_id ");
		sql.append("left join c_invoice as c on c.c_invoice_id = al.c_invoice_credit_id ");
		sql.append("left join c_doctype as cdt on cdt.c_doctype_id = c.c_doctypetarget_id ");
		sql.append("where ah.c_allocationhdr_id = ? ");
		sql.append("group by ah.c_allocationhdr_id, ah.documentno ");
		
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, rs.getInt("c_allocationhdr_id"));
			rsop = ps.executeQuery();
			while(rsop.next()){
				StringBuffer op = new StringBuffer("O1");
				op.append(fillField(rsop.getString("documentno"), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
				Integer debitos = rsop.getBigDecimal("debitos").abs().multiply(Env.ONEHUNDRED).intValue();
				Integer creditos = rsop.getBigDecimal("creditos").abs().multiply(Env.ONEHUNDRED).intValue();
				Integer total = rsop.getBigDecimal("total").abs().multiply(Env.ONEHUNDRED).intValue();
				op.append(fillField(String.valueOf(debitos), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append(fillField(String.valueOf(debitos), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append(fillField(String.valueOf(creditos), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append(fillField(String.valueOf(total), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
				op.append("0");
				op.append(fillField("OBS", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
				op.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 209, null));
				write(op.toString());
				// Separador de filas
				writeRowSeparator();
				// Un registro mas
				setExportedLines(getExportedLines()+1);
			}
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rsop != null)rsop.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
	
	protected void writeInvoices(ResultSet rs) throws Exception{
		StringBuffer sql = new StringBuffer("select distinct i.c_invoice_id ,i.documentno, i.numerocomprobante, i.dateinvoiced, i.grandtotal, coalesce(ir.amt,0) as retencion, coalesce(ir.perc,0) as retencion_porc, coalesce(ir.base,0) as retencion_base, coalesce(ir.orden,1) as orden "
										+ "from (select CASE WHEN ddt.docbasetype = 'API' "
										+ "						THEN al.c_invoice_id "
										+ "						ELSE al.c_invoice_credit_id "
										+ "				END as c_invoice_id, ah.documentno "
										+ "			from c_allocationhdr ah "
										+ "			inner join c_allocationline as al on al.c_allocationhdr_id = ah.c_allocationhdr_id "
										+ "			left join c_invoice as d on d.c_invoice_id = al.c_invoice_id "
										+ "			left join c_doctype as ddt on ddt.c_doctype_id = d.c_doctypetarget_id "
										+ "			left join c_invoice as c on c.c_invoice_id = al.c_invoice_credit_id "
										+ "			left join c_doctype as cdt on cdt.c_doctype_id = c.c_doctypetarget_id "
										+ "			where ah.c_allocationhdr_id = ?) as ia "
										+ "inner join c_invoice as i on i.c_invoice_id = ia.c_invoice_id "
										+ "left join (select distinct al.c_invoice_id, 0 as orden, amt_retenc as amt, retencion_percent as perc, baseimponible_amt as base "
										+ "				from m_retencion_invoice ri "
										+ "				inner join c_invoice i on i.c_invoice_id = ri.c_invoice_id "
										+ "				inner join c_allocationline al on al.c_invoice_credit_id = i.c_invoice_id "
										+ "				inner join c_retencionschema rs on rs.c_retencionschema_id = ri.c_retencionschema_id "
										+ "				inner join c_retenciontype rt on rt.c_retenciontype_id = rs.c_retenciontype_id "
										+ "				left join c_region r on r.c_region_id = rs.c_region_id "
										+ "				where ri.c_allocationhdr_id = ? and i.docstatus in ('CO','CL') and retentiontype = 'B' and r.ad_componentobjectuid = 'CORE-C_Region-1000083') as ir on ir.c_invoice_id = i.c_invoice_id "
										+ "order by orden, i.documentno");
		PreparedStatement ps = null;
		ResultSet rsfc = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, rs.getInt("c_allocationhdr_id"));
			ps.setInt(2, rs.getInt("c_allocationhdr_id"));
			rsfc = ps.executeQuery();
			int i = 1;
			while(rsfc.next()){
				// Comprobante 1
				StringBuffer fc = new StringBuffer("FC");
				fc.append(fillField(String.valueOf(i), "0", MExpFormatRow.ALIGNMENT_Right, 3, null));
				fc.append("FC");
				fc.append(fillField(String.valueOf(rsfc.getInt("numerocomprobante")), "0", MExpFormatRow.ALIGNMENT_Right, 12, null));
				fc.append(fillField("Factura de Proveedor", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
				fc.append(dateFormat_ddMMyyyy.format(rsfc.getTimestamp("dateinvoiced")));
				
				Integer total = rsfc.getBigDecimal("grandtotal").abs().multiply(Env.ONEHUNDRED).intValue();
				Integer retencion = rsfc.getBigDecimal("retencion").abs().multiply(Env.ONEHUNDRED).intValue();
				Integer retencion_base = rsfc.getBigDecimal("retencion_base").abs().multiply(Env.ONEHUNDRED).intValue();
				
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
				setExportedLines(getExportedLines()+1);
				i++;
			}
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rsfc != null)rsfc.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
	
	protected void writeRetenciones(ResultSet rs) throws Exception{
		StringBuffer sql = new StringBuffer("select distinct CASE WHEN retentiontype = 'I' THEN '01' "
				+ "										WHEN retentiontype = 'G' THEN '02' "
				+ "										WHEN retentiontype = 'B' THEN '03' "
				+ "										WHEN retentiontype = 'S' THEN '04' "
				+ "										ELSE '00' END as tipo, "
				+ "								CASE WHEN retentiontype = 'I' THEN '1' "
				+ "									WHEN retentiontype = 'G' THEN '2' "
				+ "									WHEN retentiontype = 'B' THEN '1' "
				+ "									WHEN retentiontype = 'S' THEN '1' "
				+ "									ELSE '0' END as impuesto, "
				+ "								coalesce(r.ad_componentobjectuid,'0') as provincia, "
				+ "								i.documentno, "
				+ "								iibb, "
				+ "								i.grandtotal, "
				+ "								ri.amt_retenc as retencion, "
				+ "								CASE WHEN ri.baseimponible_amt <= 0 THEN ri.pago_actual_amt ELSE ri.baseimponible_amt END as base, "
				+ "								ri.retencion_percent as perc, "
				+ "								ri.importe_no_imponible_amt as noimponible, "
				+ "								rs.name as esquema "
				+ "from m_retencion_invoice ri "
				+ "inner join c_invoice i on i.c_invoice_id = ri.c_invoice_id "
				+ "inner join c_bpartner bp on bp.c_bpartner_id = i.c_bpartner_id "
				+ "inner join c_retencionschema rs on rs.c_retencionschema_id = ri.c_retencionschema_id "
				+ "inner join c_retenciontype rt on rt.c_retenciontype_id = rs.c_retenciontype_id "
				+ "left join c_region r on r.c_region_id = rs.c_region_id "
				+ "where ri.c_allocationhdr_id = ? and i.docstatus in ('CO','CL') ");
		PreparedStatement ps = null;
		ResultSet rsre = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, rs.getInt("c_allocationhdr_id"));
			rsre = ps.executeQuery();
			while(rsre.next()){
				// Escribir retención
				writeRetencion(rs,rsre);
				// Separador de filas
				writeRowSeparator();
				// Un registro mas
				setExportedLines(getExportedLines()+1);
				// Escribir paraḿetros retención
				writeRetencionParams(rs,rsre);
				// Separador de filas
				writeRowSeparator();
				// Un registro mas
				setExportedLines(getExportedLines()+1);
			}
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rsre != null)rsre.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
	
	private void writeRetencion(ResultSet rs, ResultSet rsre) throws Exception{
		StringBuffer re = new StringBuffer("C1");
		re.append(rsre.getString("tipo"));
		re.append(rsre.getString("impuesto"));
		re.append(provincias.get(rsre.getString("provincia")));
		re.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
		re.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 4, null));
		if(rsre.getString("documentno").length() <= 10){
			re.append(fillField(rsre.getString("documentno"), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		}
		else{
			re.append(rsre.getString("documentno").substring(0, 10));
		}
		re.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 20, null));
		re.append("01");
		re.append(rsre.getString("iibb"));
		re.append("02");
		re.append(
				fillField(
						rs.getString("documentno").replace(getOpPrefix(), "").replace(getOpSuffix(), ""),
						"0", MExpFormatRow.ALIGNMENT_Right, 30, null));
		re.append(dateFormat_ddMMyyyy.format(rs.getTimestamp("allocationdate")));
		
		Integer total = rsre.getBigDecimal("grandtotal").abs().multiply(Env.ONEHUNDRED).intValue();
		re.append(fillField(String.valueOf(total), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		re.append(dateFormat_MMyyyy.format(rs.getTimestamp("allocationdate")));
		re.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 137, null));
		write(re.toString());
	}
	
	private void writeRetencionParams(ResultSet rs, ResultSet rsre) throws Exception{
		StringBuffer rp = new StringBuffer("C2");
		rp.append(fillField("0", "0", MExpFormatRow.ALIGNMENT_Right, 4, null));
		if(rsre.getString("documentno").length() <= 10){
			rp.append(fillField(rsre.getString("documentno"), "0", MExpFormatRow.ALIGNMENT_Right, 10, null));
		}
		else{
			rp.append(rsre.getString("documentno").substring(0, 10));
		}
		if(rsre.getString("esquema").length() <= 30){
			rp.append(fillField(rsre.getString("esquema"), " ", MExpFormatRow.ALIGNMENT_Right, 30, null));
		}
		else{
			rp.append(rsre.getString("esquema").substring(0, 30));
		}
		Integer noimponible = rsre.getBigDecimal("noimponible").abs().multiply(Env.ONEHUNDRED).intValue();
		Integer retencion = rsre.getBigDecimal("retencion").abs().multiply(Env.ONEHUNDRED).intValue();
		Integer retencion_base = rsre.getBigDecimal("base").abs().multiply(Env.ONEHUNDRED).intValue();
		Integer retencion_perc = rsre.getBigDecimal("perc").abs().multiply(Env.ONEHUNDRED).intValue();
		
		rp.append("03");
		rp.append("1");
		rp.append(fillField(String.valueOf(retencion_base), "0", MExpFormatRow.ALIGNMENT_Right, 17, null));
		rp.append("02");
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
		rp.append(fillField(" ", " ", MExpFormatRow.ALIGNMENT_Right, 134, null));
		write(rp.toString());
	}
	
	@Override
	protected String getFileFooter() {
		// TODO Auto-generated method stub
		return null;
	}	

}
