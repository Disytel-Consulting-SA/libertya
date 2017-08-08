package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MBankList extends X_C_BankList implements DocAction {
	private static final long serialVersionUID = 1L;

	public static Integer getSeqNo(Properties ctx, Integer docTypeID, Timestamp date, String trxName) {
		Integer seqNo = 0;

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	COUNT(*) ");
		sql.append("FROM ");
		sql.append("	" + Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	ad_client_id = ? ");
		sql.append("	AND docstatus IN ('CO','CL') ");
		sql.append("	AND c_doctype_id = ? ");

		sql.append((date != null) ? " AND datetrx::date = ?::date" : "");

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), trxName, true);
			int i = 1;
			ps.setInt(i++, Env.getAD_Client_ID(ctx));
			ps.setInt(i++, docTypeID);
			if (date != null) {
				ps.setTimestamp(i++, date);
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				seqNo = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return seqNo;
	}

	public MBankList(Properties ctx, int C_BankList_ID, String trxName) {
		super(ctx, C_BankList_ID, trxName);
	}

	public MBankList(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		MDocType docType = new MDocType(getCtx(), getC_DocType_ID(), get_TrxName());
		if (newRecord) {
			try {
				//Para crear nueva lista, la anterior debe completarse o eliminarse.
				  /*Como la especificación del banco requiere secuencialidad en las listas (ambos bancos)
			        para chequeos que hacen ellos, no se permite generar más de una lista para el mismo banco
				    en estado borrador (esto es para garantizar la secuencialidad de los nros.)*/
				if (checkDraftList(docType)) {
					log.saveError("SaveError", Msg.getMsg(getCtx(), "DraftListValidationError"));
					return false;
				};
				
				// Número de documento de la lista
				setDocumentNo(getDocumentNumber(docType));
				setDailySeqNo(new BigDecimal(MBankList.getSeqNo(getCtx(), getC_DocType_ID(), getDateTrx(), get_TrxName()) + 1));
			} catch (Exception e) { 
				log.saveError("SaveError", e.getMessage());
				return false;
			}
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
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public String prepareIt() {
		String ordersInOtherCompleteList = "";
		
		//Valido que la OP no esté en otra lista que ya fue completada, en caso de que esté muestro lista de OP
		for (MBankListLine line : getBankListLines()) {
			MAllocationHdr hdr = new MAllocationHdr(getCtx(), line.getC_AllocationHdr_ID(), get_TrxName());
			if (hdr.getC_BankList_ID() != 0) {
				ordersInOtherCompleteList += hdr.getDocumentNo() + " ";
			}
		}
		if (!"".equals(ordersInOtherCompleteList)) {
			setProcessMsg(Msg.getMsg(getCtx(), "AllocationsInOtherCompleteLists") + ": " + ordersInOtherCompleteList);
			return DocAction.STATUS_Invalid;
		}
		
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}
	
	/**
	 * ID de lista de banco
	 * @param bankListID id de la lista de banco
	 * @return true si pudo actualizar los allocations, false caso contrario
	 */
	protected boolean updateAllocations(Integer bankListID){
		try {
			
			//Actualizo Órdenes de Pago con la referencia a la lista
			for (MBankListLine line : getBankListLines()) {
				MAllocationHdr hdr = new MAllocationHdr(getCtx(), line.getC_AllocationHdr_ID(), get_TrxName());
				hdr.setC_BankList_ID(bankListID);
				if (!hdr.save()) {
					throw new Exception("@AllocationSaveError@: "
							+ CLogger.retrieveErrorAsString());
				}
			}
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return false;
		}
		return true;
	}

	@Override
	public String completeIt() {
		if(!updateAllocations(getID())){
			return DOCSTATUS_Invalid;
		}
		
		setProcessed(true);
		setDocAction(DOCACTION_Close);

		return DocAction.STATUS_Completed;
	}
	
	private List<MBankListLine> getBankListLines() {
		List<MBankListLine> list = new ArrayList<MBankListLine>();
		
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	C_BankListLine ");
		sql.append("WHERE ");
		sql.append("	C_BankList_ID = ? ");

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, getID());
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new MBankListLine(getCtx(), rs, get_TrxName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return list;
	}

	@Override
	public boolean postIt() {
		return false;
	}

	@Override
	public boolean voidIt() {
		if(!updateAllocations(0)){
			return false;
		}
		
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
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
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
		String set = "SET Processed='" + (processed ? "Y" : "N") + "' WHERE C_BankList_ID=" + getID();
		DB.executeUpdate("UPDATE C_BankListLine " + set, get_TrxName());
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
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return null;
	}
	
	private boolean checkDraftList(MDocType docType) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(*) ");
		sql.append("FROM C_BankList ");
		sql.append("WHERE C_DocType_ID = ? ");
		sql.append(" AND DocStatus NOT IN ('CO', 'CL', 'VO')");

		int linesCount = DB.getSQLValue(get_TrxName(), sql.toString(), docType.getID());
		return linesCount > 0;
	}
	
	private String getDocumentNumber(MDocType docType) throws Exception {
		String whereClause = "AD_Client_Id = ? and isActive = 'Y' and C_DocType_Id = ?";
		Object[] whereParams = new Object[] { Env.getAD_Client_ID(getCtx()), getC_DocType_ID() };
		X_C_BankList_Config bankListConfig = (X_C_BankList_Config) PO.findFirst(getCtx(), X_C_BankList_Config.Table_Name, whereClause, whereParams, null, get_TrxName());
		String prefix = "";
		if (bankListConfig != null) {
			prefix = Util.isEmpty(bankListConfig.getClientAcronym()) ? "" : bankListConfig.getClientAcronym();
		}

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	MAX(documentno) documentno ");
		sql.append("FROM ");
		sql.append("	c_banklist ");
		sql.append("WHERE ");
		sql.append("	c_doctype_id = ? ");
		sql.append("	AND docstatus IN ('CO','CL','VO','RE') ");
		sql.append("	AND ad_client_id = ? ");

		PreparedStatement ps = null;
		ResultSet rs = null;
		String lastDocumentno = null;
		String documentno = "";
		Integer seq = 1;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName(), true);
			ps.setInt(1, getC_DocType_ID());
			ps.setInt(2, getAD_Client_ID());
			rs = ps.executeQuery();
			if (rs.next()) {
				lastDocumentno = rs.getString("documentno");
			}
			
			//Nro. doc Galicia según especificación:
			  /*EESSSSSS (EE: Sigla de la empresa, SSSSSS: Secuencial). 
				No se debe repetir.
				La sigla la suministrará el Banco al momento de alta en el servicio.
				El secuencial deberá ser controlado por el cliente, deberá comenzar de 1 e incrementarse en 1 para cada lista enviada.
		      */
			if (MDocType.DOCTYPE_Lista_Galicia.equals(docType.getDocTypeKey())) {
				if (!Util.isEmpty(lastDocumentno, true)) {
					seq = Integer.parseInt(lastDocumentno.substring(lastDocumentno.length() - 5, lastDocumentno.length()));
					seq++;
				}
				documentno = prefix + String.format("%06d", seq);
			}
			
			//Nro. doc Patagonia según especificación
			  /*Nro. secuencial permanente de 1 a N por cada envió que se haga de este tipo de archivo. 
			   * Se usaría para control de correlatividad de envíos y recepciones.
			   * Longitud: 7 
			   */
			if (MDocType.DOCTYPE_Lista_Patagonia.equals(docType.getDocTypeKey())) {
				if (!Util.isEmpty(lastDocumentno, true)) {
					seq = Integer.parseInt(lastDocumentno);
					seq++;
				}
				documentno = String.format("%07d", seq);
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
				throw e2;
			}
		}
		return documentno;
	}

}
