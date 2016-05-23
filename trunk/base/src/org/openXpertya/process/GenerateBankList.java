package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBankList;
import org.openXpertya.model.MBankListLine;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MSequence;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

public class GenerateBankList extends AbstractSvrProcess {

	/**
	 * Elimina las líneas de la lista del banco parámetro
	 * @throws Exception
	 */
	protected void deleteBankListLines() throws Exception{
		DB.executeUpdate("DELETE FROM C_BankListLine WHERE C_BankList_ID = " + getRecord_ID(), get_TrxName());
	}
	
	@Override
	protected String doIt() throws Exception {
		MDocType opDocType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago, get_TrxName());
		if(opDocType == null){
			throw new Exception("No existe el tipo de documento Orden de Pago");
		}
		// Eliminar las líneas de la lista actual para recargarlas
		deleteBankListLines();
		MBankList bankList = new MBankList(getCtx(), getRecord_ID(), get_TrxName());
		MAllocationHdr op = new MAllocationHdr(getCtx(), (Integer) getParametersValues().get("C_ALLOCATIONHDR_ID"),
				get_TrxName());
		// Obtener prefijo y sufijo de la secuencia de la OP
		String opPrefix = MSequence.getPrefix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opPrefix = opPrefix == null?"":opPrefix;
		String opSuffix = MSequence.getSuffix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opSuffix = opSuffix == null?"":opSuffix;
		
		// Obtener los cheques que se encuentran en ordenes de pago con nro
		// menor a la OP parámetro y que no estén en listas completas
		String sql = "select distinct p.c_payment_id "
					+ "from c_allocationhdr as ah "
					+ "inner join c_allocationline as al on al.c_allocationhdr_id = ah.c_allocationhdr_id "
					+ "inner join c_payment as p on p.c_payment_id = al.c_payment_id "
					+ "inner join c_bankaccount as ba on ba.c_bankaccount_id = p.c_bankaccount_id "
					+ "inner join c_bank as b on b.c_bank_id = ba.c_bank_id "
					+ "inner join c_doctype as dt on dt.c_banklist_bank_id = b.c_bank_id and dt.c_doctype_id = ? "
					+ "inner join c_bpartner_banklist as bpbl on bpbl.c_bpartner_id = p.c_bpartner_id "
					+ "where p.ad_client_id = ? "
					+ "			and p.tendertype = 'K' "
					+ "			and p.isreceipt = 'N' "
					+ "			and p.docstatus IN ('CO','CL') "
					+ "			and ah.docstatus IN ('CO','CL') "
					+ "			and ah.allocationtype = 'OP' "
					+ "			and bpbl.isactive = 'Y' "
					+ "			and bpbl.c_doctype_id = ? "
					+ "			and not exists (select bll.c_banklistline_id "
					+ "							from c_banklistline bll "
					+ "							inner join c_banklist bl on bl.c_banklist_id = bll.c_banklist_id "
					+ "							where bll.c_payment_id = p.c_payment_id and bl.docstatus IN ('CO','CL') and bl.c_doctype_id = ?) "
					+ "			and to_number(replace(replace(ah.documentno,'" + opPrefix + "',''), '" + opSuffix
					+ "', ''),'999999999999999999') <= to_number(replace(replace('" + op.getDocumentNo() + "','"
					+ opPrefix + "',''),'" + opSuffix + "',''),'999999999999999999')";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer checkCount = 0;
		MBankListLine bankListLine;
		BigDecimal line = new BigDecimal(10);
		try {
			ps = DB.prepareStatement(sql, get_TrxName(), true);
			ps.setInt(1, bankList.getC_DocType_ID());
			ps.setInt(2, op.getAD_Client_ID());
			ps.setInt(3, bankList.getC_DocType_ID());
			ps.setInt(4, bankList.getC_DocType_ID());
			rs = ps.executeQuery();
			while(rs.next()){
				bankListLine = new MBankListLine(getCtx(), 0, get_TrxName());
				bankListLine.setC_BankList_ID(bankList.getID());
				bankListLine.setC_Payment_ID(rs.getInt("c_payment_id"));
				bankListLine.setLine(line);
				if(!bankListLine.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				checkCount++;
				line.add(new BigDecimal(10));
			}
			// Actualizar la OP en la lista del banco y los nros de secuencia
			bankList.setC_AllocationHdr_ID(op.getID());
			bankList.setDailySeqNo(new BigDecimal(
					MBankList.getSeqNo(getCtx(), bankList.getC_DocType_ID(), bankList.getDateTrx(), get_TrxName())+1));
			bankList.setTotalSeqNo(new BigDecimal(
					MBankList.getSeqNo(getCtx(), bankList.getC_DocType_ID(), null, get_TrxName())+1));
		} catch (Exception e) {
			throw e;
		} finally {
			try{
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
		return "Cheques incorporados a la lista: "+checkCount;
	}

}
