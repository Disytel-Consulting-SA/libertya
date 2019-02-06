package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.X_I_PaymentBankNews;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Proceso de importación de novedades de bancos 
 * permitiendo actualizar el estado de pagos.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class ImportPaymentNews extends SvrProcess {

	private boolean m_deleteOldImported;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (name.equals("DeleteOldImported")) {
				m_deleteOldImported = "Y".equals(para[i].getParameter());
			} else {
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		StringBuffer sql = null;
		int no = 0;

		if (m_deleteOldImported) {
			sql = new StringBuffer("DELETE " + X_I_PaymentBankNews.Table_Name + " WHERE I_IsImported = 'Y'");
			no = DB.executeUpdate(sql.toString());
			log.fine("Delete Old Imported =" + no);
		}
		
		//1 - Obtengo registros sin importar
		for (X_I_PaymentBankNews news : getRecords()) {
			//2 - Recupero la lista donde fue enviado el pago
			MBankList bankList = null;
			//2.1 - Si el banco manda referencia a la lista, caso Galicia, busco directamente.
			if (news.getList_Value() != null && !news.getList_Value().isEmpty()) {
				bankList = getBankList(news.getList_Value());
			//2.2 - Si el banco no manda ref. a la lista (caso Patagonia) busco por OP
			} else {
				bankList = getBankListByOP(news.getPayment_Order());
			}
			if (bankList == null) {
					news.setI_ErrorMsg(Msg.getMsg(getCtx(), "NotFoundPaymentList"));
			} else {
				//3 - Recupero el cheque (payment) a actualizar
				MPayment payment = getPayment(bankList, news);
				if (payment == null) {
					news.setI_ErrorMsg(Msg.getMsg(getCtx(), "CheckNotFound"));
				} else {
					//4 - Si tengo códigos de estado, como en el caso del galicia, busco por código el estado interno correspondiente
					int statusId = 0;
					// Tiene estado o mensaje de estado, entonces lo busco sino
					// es se toma como una novedad que no posee mensajes
					if (!Util.isEmpty(news.getPayment_Status(), true)
							|| !Util.isEmpty(news.getPayment_Status_Msg(), true)) {
						if (!Util.isEmpty(news.getPayment_Status(), true)) {
							statusId = getStatusIdByCode(news.getPayment_Status(), news.getC_Bank_ID());
						} else { //Caso contrario, busco por mensaje, como en el Patagonia
							statusId = getStatusIdByName(news.getPayment_Status_Msg(), news.getC_Bank_ID());
						}
					} 
					if (statusId < 0) {
						news.setI_ErrorMsg(Msg.getMsg(getCtx(), "StatusNotFound"));
					} else {
						//5 - Actualizo los datos del pago
						payment.setC_Bankpaymentstatus_ID(statusId);
						payment.setBank_Payment_Msg_Description(news.getPayment_Status_Msg_Description());
						payment.setBank_Payment_Date(news.getProcess_Date());
						payment.setBank_Payment_DocumentNo(news.getReceipt_Number());
						payment.setCheckNo(news.getCheckNo());
						if (!payment.save()) {
							news.setI_ErrorMsg(Msg.getMsg(getCtx(), "StatusNotFound"));
						}
						news.setI_IsImported(true);
						news.setProcessed(true);
						news.setI_ErrorMsg("");
						no++;
					}
				}
			}
			if (!news.save()) {
				throw new Exception(Msg.getMsg(getCtx(), "ImportPaymentNewsError"));
			}
		}

		return Msg.getMsg(getCtx(), "ImportCompleted") + ": " + no;
	}
	
	private List<X_I_PaymentBankNews> getRecords() {
		List<X_I_PaymentBankNews> list = new ArrayList<X_I_PaymentBankNews>();
		
		//Construyo la query
		String sql = "SELECT * " + 
					 "FROM I_PaymentBankNews " +
					 "WHERE " + 
					  "i_isimported = 'N'";
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new X_I_PaymentBankNews(getCtx(), rs, get_TrxName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	private MBankList getBankList(String value) {
		//Construyo la query
		String sql = "SELECT * " + 
					 "FROM C_BankList bl " +
					 "WHERE " + 
					  "bl.documentno = ?";
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setString(1, value);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				return new MBankList(getCtx(), rs, get_TrxName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private MBankList getBankListByOP(String value) {
		//Construyo la query
		String sql = "SELECT ah.c_banklist_id " + 
					 "FROM C_AllocationHdr ah " +
					 "WHERE " + 
					  "ah.documentno = ?";
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setString(1, value);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				return new MBankList(getCtx(), rs.getInt("c_banklist_id"), get_TrxName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private MPayment getPayment(String regNumber, int bankAccountId) {
		//Construyo la query
		String sql = "SELECT * " + 
					 "FROM C_Payment p " +
					 "WHERE " + 
					  "p.checkno = ? " +
					  "AND p.c_bankaccount_id = ?";
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setString(1, regNumber);
			ps.setInt(2, bankAccountId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				return new MPayment(getCtx(), rs, get_TrxName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private int getStatusIdByCode(String statusCode, int bankId) {
		//Construyo la query
		String sql = "SELECT a.C_BankPaymentStatus_ID AS statusId " + 
					 "FROM C_BankPaymentStatusAssociation a " +
					 "WHERE " + 
					  "a.value = ? " +
					  "AND a.c_bank_id = ?";
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setString(1, statusCode.trim());
			ps.setInt(2, bankId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("statusId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return -1;
	}
	
	private int getStatusIdByName(String statusName, int bankId) {
		//Construyo la query
		String sql = "SELECT a.C_BankPaymentStatus_ID AS statusId " + 
					 "FROM C_BankPaymentStatusAssociation a " +
					 "WHERE " + 
					  "a.name = ? " +
					  "AND a.c_bank_id = ?";
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setString(1, statusName);
			ps.setInt(2, bankId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("statusId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return -1;
	}
	
	protected MPayment getPayment(MBankList bankList, X_I_PaymentBankNews news){
		MPayment p = null;
		// Buscar por el numero de registro del pago dentro de la lista
		if(!Util.isEmpty(news.getRegister_Number(), true)){
			p = getPaymentByRegisterNo(bankList.getID(), news.getRegister_Number());
		}
		
		// Buscar por OP e importe
		if (p == null && !Util.isEmpty(news.getPayment_Order(), true)
				&& !Util.isEmpty(news.getPayment_Amount(), true)) {
			p = getPaymentByOP(news.getPayment_Order(), news.getPayment_Amount());
		}
		
		// Buscar por OP y número de cheque
		if (p == null && !Util.isEmpty(news.getPayment_Order(), true)
				&& !Util.isEmpty(news.getCheckNo(), true)) {
			p = getPaymentByOP(news.getPayment_Order(), news.getCheckNo());
		}
		return p;
	}

	protected MPayment getPaymentByRegisterNo(Integer bankListID, String register_number){
		MPayment payment = null;
		String sql = "SELECT c_payment_id "
					+ "FROM c_payment p "
					+ "WHERE banklist_registerno = '"+register_number+"'"
							+ " AND EXISTS (SELECT c_allocationline_id "
							+ 				"FROM c_allocationline al "
							+				"JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id "
							+ 				"WHERE ah.c_banklist_id = ? AND al.c_payment_id = p.c_payment_id) ";
		int payID = DB.getSQLValue(get_TrxName(), sql, bankListID);
		if(payID > 0){
			payment = new MPayment(getCtx(), payID, get_TrxName());
		}
		return payment;
	}
	
	protected MPayment getPaymentByOP(String payment_order, BigDecimal payment_amount){
		MPayment payment = null;
		String sql = "SELECT c_payment_id "
					+ "FROM c_payment p "
					+ "WHERE payamt = " +payment_amount
							+ " AND EXISTS (SELECT c_allocationline_id "
							+ 				"FROM c_allocationline al "
							+				"JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id "
							+ 				"WHERE ah.documentno = '"+payment_order+"' AND al.c_payment_id = p.c_payment_id) ";
		int payID = DB.getSQLValue(get_TrxName(), sql);
		if(payID > 0){
			payment = new MPayment(getCtx(), payID, get_TrxName());
		}
		return payment;
	}
	
	protected MPayment getPaymentByOP(String payment_order, String checkNo){
		MPayment payment = null;
		String sql = "SELECT c_payment_id "
					+ "FROM c_payment p "
					+ "WHERE checkno = '" + checkNo + "' "
							+ " AND EXISTS (SELECT c_allocationline_id "
							+ 				"FROM c_allocationline al "
							+				"JOIN c_allocationhdr ah on ah.c_allocationhdr_id = al.c_allocationhdr_id "
							+ 				"WHERE ah.documentno = '"+payment_order+"' AND al.c_payment_id = p.c_payment_id) ";
		int payID = DB.getSQLValue(get_TrxName(), sql);
		if(payID > 0){
			payment = new MPayment(getCtx(), payID, get_TrxName());
		}
		return payment;
	}
}
