package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MBankList extends X_C_BankList implements DocAction {

	public static Integer getSeqNo(Properties ctx, Integer docTypeID, Timestamp date, String trxName){
		Integer seqNo = 0;
		String sql = "SELECT count(*)"
					+ " FROM "+Table_Name
					+ " WHERE ad_client_id = ?"
					+ " 		AND docstatus IN ('CO','CL') "
					+ " 		AND c_doctype_id = ?";
		if(date != null){
			sql += " AND datetrx::date = ?::date";
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName, true);
			int i = 1;
			ps.setInt(i++, Env.getAD_Client_ID(ctx));
			ps.setInt(i++, docTypeID);
			if(date != null){
				ps.setTimestamp(i++, date);
			}
			rs = ps.executeQuery();
			if(rs.next()){
				seqNo = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return seqNo;
	}
	
	public MBankList(Properties ctx, int C_BankList_ID, String trxName) {
		super(ctx, C_BankList_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBankList(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Actualizar el número de secuencia de las lista
		/*try{
			updateSeqNo();
		} catch(Exception e){
			setProcessMsg(e.getMessage());
			return false;
		}*/
		// Número de documento de la lista galicia
		MDocType docType = new MDocType(getCtx(), getC_DocType_ID(), get_TrxName());
		if ((newRecord || is_ValueChanged("DateTrx"))
				&& MDocType.DOCTYPE_Lista_Galicia.equals(docType.getDocTypeKey())) {
			X_C_BankList_Config bankListConfig = (X_C_BankList_Config) PO.findFirst(getCtx(),
					X_C_BankList_Config.Table_Name, "ad_client_id = ? and isactive = 'Y' and c_doctype_id = ?",
					new Object[] { Env.getAD_Client_ID(getCtx()), getC_DocType_ID() }, null, get_TrxName());
			String prefix = "";
			if(bankListConfig != null){
				prefix = Util.isEmpty(bankListConfig.getClientAcronym())?"":bankListConfig.getClientAcronym();
			}
			String sql = "SELECT max(documentno) documentno FROM c_banklist WHERE c_doctype_id = ? AND docstatus IN ('CO','CL') and ad_client_id = ? and extract('month' from datetrx) = extract('month' from ?::date) and extract('year' from datetrx) = extract('year' from ?::date)"
					+ (newRecord ? "" : " and c_banklist_id <> " + getID());
			DateFormat dateFormat_MMyyyy = new SimpleDateFormat("MMyyyy");
			DateFormat dateFormat_yy = new SimpleDateFormat("yy");
			PreparedStatement ps = null;
			ResultSet rs = null;
			String lastDocumentno = null;
			String documentno;
			Integer seq = Integer.parseInt(dateFormat_yy.format(getDateTrx()));
			try{
				ps = DB.prepareStatement(sql, get_TrxName(), true);
				ps.setInt(1, getC_DocType_ID());
				ps.setInt(2, getAD_Client_ID());
				ps.setTimestamp(3, getDateTrx());
				ps.setTimestamp(4, getDateTrx());
				rs = ps.executeQuery();
				if(rs.next()){
					lastDocumentno = rs.getString("documentno");
				}
				if(!Util.isEmpty(lastDocumentno, true)){
					seq = Integer.parseInt(
							lastDocumentno.substring(lastDocumentno.length() - 3, lastDocumentno.length() - 1));
					seq++;
				}
				documentno = prefix+dateFormat_MMyyyy.format(getDateTrx())+(seq < 10?"0":"")+seq;
			} catch(Exception e){
				log.saveError("SaveError", e.getMessage());
				return false;
			} finally{
				try {
					if(ps != null) ps.close();
					if(rs != null) rs.close();
				} catch (Exception e2) {
					log.saveError("SaveError", e2.getMessage());
					return false;
				}
			}
			setDocumentNo(documentno);
		}
		return true;
	}
	
	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		boolean status = engine.processIt(action, getDocAction(), log);
		return status;
	}

	@Override
	public boolean unlockIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean invalidateIt() {
		// TODO Auto-generated method stub
		return false;
	}

	private HTMLMsg checksInOtherBankList(){
		String sql = "select distinct bl.line, bl.c_payment_id, p.documentno, p.checkno "
					+ "from (select c_payment_id "
					+ "			from c_banklistline "
					+ "			where c_banklist_id = " + getID()
					+ "			intersect "
					+ "			select c_payment_id "
					+ "			from c_banklistline bll "
					+ "			inner join c_banklist bl on bl.c_banklist_id = bll.c_banklist_id "
					+ "			where bl.c_banklist_id <> "+getID()+" and bl.docstatus in ('CO','CL') and bl.c_doctype_id = ?) as c "
					+ "inner join c_banklistline as bl on c.c_payment_id = bl.c_payment_id "
					+ "inner join c_payment as p on p.c_payment_id = c.c_payment_id "
					+ "where bl.c_banklist_id = " + getID()
					+ " order by bl.line ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		HTMLMsg msg = new HTMLMsg();
		HTMLMsg.HTMLList checkList = msg.createList("checkList", "ul");
		try {
			ps = DB.prepareStatement(sql, get_TrxName(), true);
			ps.setInt(1, getC_DocType_ID());
			rs = ps.executeQuery();
			while(rs.next()){
				msg.createAndAddListElement(rs.getString("line"),
						rs.getString("line") + " : " + rs.getString("documentno"), checkList);
			}
			if(checkList.getElements().size() > 0){
				checkList.setMsg(
						Msg.getMsg(getCtx(), "ChecksInOtherBankList", new Object[] { checkList.getElements().size() }));
				msg.addList(checkList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return msg;
	}
	
	@Override
	public String prepareIt() {
		// Validar que los cheques que se encuentran en esta lista no estén en
		// otra, en ese caso listar los cheques duplicados en otras listas del
		// mismo tipo
		HTMLMsg msg = checksInOtherBankList();
		if(msg != null && msg.toString() != null && msg.toString().length() > 0){
			setProcessMsg(msg.toString());
			return DocAction.STATUS_Invalid;
		}
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rejectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Actualiza los nros de secuencia totales y diarios de las listas en Borrador
	 * @throws Exception
	 */
	private void updateSeqNo() throws Exception{
		BigDecimal totalSeqNo = new BigDecimal(
				MBankList.getSeqNo(getCtx(), getC_DocType_ID(), null, get_TrxName()) + 1);
		setTotalSeqNo(totalSeqNo);
		setDailySeqNo(new BigDecimal(
				MBankList.getSeqNo(getCtx(), getC_DocType_ID(), getDateTrx(), get_TrxName())+1));
	}
	
	@Override
	public String completeIt() {
		setProcessed(true);
		setDocAction(DOCACTION_Close);

		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean postIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean voidIt() {
		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}

	@Override
	public boolean closeIt() {
		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reActivateIt() {
		setProcessed(false);
		return true;
	}

	public void setProcessed(boolean processed) {
		super.setProcessed(processed);

		if (getID() == 0) {
			return;
		}

		String set = "SET Processed='" + (processed ? "Y" : "N")
				+ "' WHERE C_BankList_ID=" + getID();
		int noLine = DB.executeUpdate("UPDATE C_BankListLine " + set,
				get_TrxName());
	}
	
	@Override
	public String getSummary() {
		return getDocumentNo() + ". " + getDescription();
	}

	@Override
	public int getDoc_User_ID() {
		return getCreatedBy();
	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return null;
	}
}
