package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_BankAccount;
import org.openXpertya.model.X_C_BankListLine;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ExportListaGaliciaECheck extends ExportBankList {

	/** Formato de fechas dd/MM/yyyy */
	protected DateFormat dateFormat_local_ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
	
	/** Fecha de emisión o generación de pagos del banco al proveedor */
	protected Timestamp paymentDate = Env.getDate();
	
	public ExportListaGaliciaECheck(Properties ctx, MBankList bankList, String trxName) {
		super(ctx, bankList, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getBankListExportFormatValue() {
		return "LGECHECK";
	}

	@Override
	protected String getFileName() {
		// Obtener el substring que corresponde al directorio ya que el nombre del
		// archivo hay que armarlo
		Integer indexSlash = 0;
		if(getExportFormat().getFileName().contains("\\")) {
			indexSlash = getExportFormat().getFileName().lastIndexOf("\\");
		}
		else {
			indexSlash = getExportFormat().getFileName().lastIndexOf("/");
		}
		String dir = getExportFormat().getFileName().substring(0, indexSlash+1);
		// Armar el nombre del archivo
		MClientInfo ci = MClientInfo.get(getCtx(), Env.getAD_Client_ID(getCtx()));
		StringBuffer fileName = new StringBuffer("GAL_");
		fileName.append("PP_");
		fileName.append(ci.getCUIT().replace("-", "")+"_");
		fileName.append(getBankListConfig().getRegisterNumber()+"_");
		fileName.append(dateFormat_yyyyMMdd.format(Env.getDate())+"_");
		fileName.append(getDayExportedBankListNo() + "_");
		fileName.append("ENV");
		return dir+fileName;
	}
	
	@Override
	protected String getFileHeader() {
		StringBuffer fh = new StringBuffer();
		fh.append(getBankListConfig().getRegisterNumber());
		fh.append(getFieldSeparator());
		fh.append(getDayExportedBankListNo());
		fh.append(getFieldSeparator());
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	COUNT(DISTINCT p.c_payment_id) ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankListLine.Table_Name + " AS bll ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("		ON bll.C_AllocationHdr_ID = al.C_AllocationHdr_ID ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " AS p ");
		sql.append("		ON p.c_payment_id = al.c_payment_id ");
		sql.append("WHERE ");
		sql.append("	c_banklist_id = ?");

		Integer dp = DB.getSQLValue(get_TrxName(), sql.toString(), getBankList().getID());

		sql = new StringBuffer();
		sql.append("SELECT sum(p.payamt) ");
		sql.append("FROM ");
		sql.append("(SELECT ");
		sql.append("	distinct p.c_payment_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankListLine.Table_Name + " AS bll ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("		ON bll.C_AllocationHdr_ID = al.C_AllocationHdr_ID ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " AS p ");
		sql.append("		ON p.c_payment_id = al.c_payment_id ");
		sql.append("WHERE ");
		sql.append("	c_banklist_id = ?) as b ");
		sql.append(" JOIN c_payment p ON p.c_payment_id = b.c_payment_id ");

		BigDecimal res = DB.getSQLValueBD(get_TrxName(), sql.toString(), getBankList().getID());
		
		fh.append(String.valueOf(res));
		fh.append(getFieldSeparator());
		fh.append(String.valueOf(dp));
		fh.append(getFieldSeparator());

		return fh.toString();
	}

	@Override
	protected String getQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT distinct ");
		sql.append("	p.c_payment_id,  ");
		sql.append("	ba.accountno,  ");
		sql.append("	p.payamt,  ");
		sql.append("	ba.accountno,  ");
		sql.append("	bp.c_bpartner_id,  ");
		sql.append("	bp.value as bpartner_value,  ");
		sql.append("	bp.name as bpartner_name,  ");
		sql.append("	Translate(COALESCE(bp.taxid, p.a_cuit), '-', '') AS cuit, ");
		sql.append("	ah.documentno as ordenpago,  ");
		sql.append("	p.duedate,  ");
		sql.append("	p.duedate as paymentduedate,  ");
		sql.append("	p.description,  ");
		sql.append("	COALESCE( ");
		sql.append("	  (SELECT email FROM ad_user u ");
		sql.append("	   WHERE u.c_bpartner_id = bp.c_bpartner_id AND email is not null ");
		sql.append("	   ORDER BY u.updated desc LIMIT 1 ");
		sql.append("		), ' ' ");
		sql.append("	) AS email ");
		sql.append("FROM ");
		sql.append("	c_electronic_payments lgp"); // Vista
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " p ");
		sql.append("		ON p.c_payment_id = lgp.c_payment_id ");
		sql.append("	INNER JOIN " + X_C_BPartner.Table_Name + " bp ");
		sql.append("		ON bp.c_bpartner_id = p.c_bpartner_id ");
		sql.append("	INNER JOIN " + X_C_BankAccount.Table_Name + " ba ");
		sql.append("		ON ba.c_bankaccount_id = p.c_bankaccount_id ");
		sql.append("	INNER JOIN " + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("		ON ah.c_allocationhdr_id = lgp.c_allocationhdr_id ");
		sql.append("WHERE ");
		sql.append("	lgp.c_banklist_id = ?");
		return sql.toString();
	}
	
	@Override
	protected void writeRow(ResultSet rs) throws Exception {
		StringBuffer fh = new StringBuffer();
		fh.append("EQA");
		fh.append(getFieldSeparator());
		fh.append(fillField(rs.getString("accountno"), "0", MExpFormatRow.ALIGNMENT_Right, 12, null));
		fh.append(getFieldSeparator());
		fh.append(rs.getBigDecimal("payamt"));
		fh.append(getFieldSeparator());
		fh.append(dateFormat_local_ddMMyyyy.format(paymentDate));
		fh.append(getFieldSeparator());
		fh.append(dateFormat_local_ddMMyyyy.format(rs.getTimestamp("duedate")));
		fh.append(getFieldSeparator());
		
		fh.append(truncField(rs.getString("bpartner_name"), 50));
		fh.append(getFieldSeparator());
		fh.append(rs.getString("cuit"));
		fh.append(getFieldSeparator());
		fh.append(rs.getString("ordenpago").replace(getOpPrefix(), "").replace(getOpSuffix(), ""));
		fh.append(getFieldSeparator());
		fh.append("VARIOS");
		fh.append(getFieldSeparator());
		fh.append(truncField(rs.getString("description"), 30));
		fh.append(getFieldSeparator());
		fh.append(truncField(rs.getString("email"), 150));
		fh.append(getFieldSeparator());
		write(fh.toString());
	}
	
	@Override
	protected String getFileFooter() {
		// TODO Auto-generated method stub
		return null;
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

	/**
	 * @return Obtiene el número de lista exportado el día de la fecha en formato NNN
	 */
	protected String getDayExportedBankListNo() {
		return fillField(String.valueOf(getDayExportNo()), "0", MExpFormatRow.ALIGNMENT_Right, 3, null);
	}
}
