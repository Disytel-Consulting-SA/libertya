package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBankAccount;
import org.openXpertya.model.MBankList;
import org.openXpertya.model.MBankListLine;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_BankAccount;
import org.openXpertya.model.X_C_BankListLine;
import org.openXpertya.model.X_C_BankList_Config;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class GenerateBankList extends AbstractSvrProcess {

	/** Asociación entre tipo de lista con tipo de pago */
	protected static Map<String, String> tenderTypes;
	static{
		tenderTypes = new HashMap<String, String>();
		tenderTypes.put(X_C_BankList_Config.PAYMENTTYPE_ElectronicCheck, X_C_Payment.TENDERTYPE_Check);
		tenderTypes.put(X_C_BankList_Config.PAYMENTTYPE_ElectronicTransfer, X_C_Payment.TENDERTYPE_DirectDeposit);
	}
	
	/**
	 * Elimina las líneas de la lista del banco parámetro
	 * @throws Exception
	 */
	protected void deleteBankListLines() throws Exception {
		String tableName = X_C_BankListLine.Table_Name;
		DB.executeUpdate("DELETE FROM " + tableName + " WHERE C_BankList_ID = " + getRecord_ID(), get_TrxName());
	}

	@Override
	protected String doIt() throws Exception {
		MDocType opDocType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago, get_TrxName());
		if (opDocType == null) {
			throw new Exception("No existe el tipo de documento Orden de Pago");
		}
		// Eliminar las líneas de la lista actual para recargarlas
		deleteBankListLines();
		MBankList bankList = new MBankList(getCtx(), getRecord_ID(), get_TrxName());
		MBankAccount ba = MBankAccount.get(getCtx(), bankList.getC_BankAccount_ID());
		// Obtener la forma de pago de los payments dependiendo la config de
		// esta lista de banco
		Integer bankListConfigID = DB.getSQLValue(get_TrxName(),
				"SELECT C_BankList_Config_ID FROM C_BankList_Config blc WHERE blc.C_Bank_ID = ? AND blc.C_DocType_ID = ? ORDER BY created DESC LIMIT 1",
				ba.getC_Bank_ID(), bankList.getC_DocType_ID());
		String pt = X_C_BankList_Config.PAYMENTTYPE_ElectronicCheck;
		if(bankListConfigID > 0){
			X_C_BankList_Config blc = new X_C_BankList_Config(getCtx(), bankListConfigID, get_TrxName());
			pt = blc.getPaymentType();
		}
		String tt = tenderTypes.get(pt);
		MAllocationHdr op = new MAllocationHdr(getCtx(), (Integer) getParametersValues().get("C_ALLOCATIONHDR_ID"), get_TrxName());
		// Obtener prefijo y sufijo de la secuencia de la OP
		String opPrefix = MSequence.getPrefix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opPrefix = opPrefix == null ? "" : opPrefix;
		String opSuffix = MSequence.getSuffix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opSuffix = opSuffix == null ? "" : opSuffix;

		final String maxDocumentNo = "999999999999999999";

		// Obtener los cheques que se encuentran en ordenes de pago con nro
		// menor a la OP parámetro y que no estén en listas completas
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	ah.c_allocationhdr_id ");
		sql.append("FROM  ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " AS ah ");
		sql.append("	INNER JOIN " + X_C_AllocationLine.Table_Name + " AS al ");
		sql.append("		ON al.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " AS p ");
		sql.append("		ON p.c_payment_id = al.c_payment_id ");
		sql.append(" 	INNER JOIN " + X_C_BankAccount.Table_Name + " AS ba ");
		sql.append("		ON ba.c_bankaccount_id = p.c_bankaccount_id ");
		sql.append("WHERE ");
		sql.append("	p.ad_client_id = ? ");
		sql.append("	AND p.tendertype = '"+tt+"' ");
		sql.append("	AND p.isreceipt = 'N' ");
		sql.append("	AND p.docstatus IN ('CO','CL') ");
		sql.append("	AND ah.docstatus IN ('CO','CL') ");
		sql.append("	AND ah.allocationtype IN ('OP','OPA') ");
		sql.append("	AND p.c_bankaccount_id = ? ");
		sql.append("    AND ah.c_banklist_id IS NULL ");
		sql.append("	AND to_number(replace(replace(ah.documentno, ?, ''), ?, ''), ?) <= ");
		sql.append("		to_number(replace(replace(?, ?, ''), ?, ''), ? ) ");
		sql.append("	AND ah.c_allocationhdr_id NOT IN ( ");
		sql.append("			SELECT DISTINCT  ");
		sql.append("			  ahr.c_allocationhdr_id "); 
		sql.append("			FROM c_allocationhdr ahr ");
		sql.append("			  INNER JOIN c_allocationline ali ON ahr.c_allocationhdr_id = ali.c_allocationhdr_id ");
		sql.append("			  LEFT JOIN c_payment pa ON ali.c_payment_id = pa.c_payment_id ");
		sql.append("	WHERE ahr.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append("	AND (ali.c_cashline_id IS NOT NULL OR pa.c_bankaccount_id != ?) ");
		sql.append("	) ");

		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer checkCount = 0;
		MBankListLine bankListLine;
		BigDecimal line = new BigDecimal(10);
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, op.getAD_Client_ID());
			ps.setInt(2, bankList.getC_BankAccount_ID());
			ps.setString(3, opPrefix);
			ps.setString(4, opSuffix);
			ps.setString(5, maxDocumentNo);
			ps.setString(6, op.getDocumentNo());
			ps.setString(7, opPrefix);
			ps.setString(8, opSuffix);
			ps.setString(9, maxDocumentNo);
			ps.setInt(10, bankList.getC_BankAccount_ID());
			

			rs = ps.executeQuery();
			while (rs.next()) {
				bankListLine = new MBankListLine(getCtx(), 0, get_TrxName());
				bankListLine.setC_BankList_ID(bankList.getID());
				bankListLine.setC_AllocationHdr_ID(rs.getInt(1));
				bankListLine.setLine(line);
				if (!bankListLine.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				checkCount++;
				line = line.add(new BigDecimal(10));
			}
			// Actualizar la OP en la lista del banco y los nros de secuencia
			bankList.setDailySeqNo(new BigDecimal(MBankList.getSeqNo(getCtx(), bankList.getC_DocType_ID(), bankList.getDateTrx(), get_TrxName()) + 1));
			bankList.setTotalSeqNo(new BigDecimal(MBankList.getSeqNo(getCtx(), bankList.getC_DocType_ID(), null, get_TrxName()) + 1));
			if(!bankList.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e2) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return "Ordenes de Pago incorporadas a la lista: " + checkCount;
	}

}
