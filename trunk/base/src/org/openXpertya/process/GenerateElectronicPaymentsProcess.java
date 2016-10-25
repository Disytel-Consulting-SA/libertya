package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.openXpertya.model.MBankAccount;
import org.openXpertya.model.MBankList;
import org.openXpertya.model.MBankListLine;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPaymentBatchPO;
import org.openXpertya.model.MPaymentBatchPODetail;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class GenerateElectronicPaymentsProcess extends SvrProcess {

	Boolean completeList = false;
	MPaymentBatchPO paymentBatch = null;
	
	@Override
	protected void prepare() {
		//Lote de pagos
		paymentBatch = new MPaymentBatchPO(getCtx(), getRecord_ID(), get_TrxName());
		
		//Parámetros
		ProcessInfoParameter[] para = getParameter();	
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );

			String name = para[ i ].getParameterName();

			if( para[ i ].getParameter() == null ) {
				;
			} else if( name.equalsIgnoreCase( "completeList" )) {
				completeList = "Y".equals((String)para[i].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		String response = Msg.getMsg(getCtx(), "PaymentListGenerated");
		/* 1 - Recupero los detalles agrupados por cuenta bancaria, solo de aquellas marcadas como
		       de pago electrónico.*/ 
		Map<Integer, List<MPaymentBatchPODetail>> details = getDetails();
		
		// 2 - Por cada banco, creo la Lista de Pagos y asocio los pagos
		for (Entry<Integer, List<MPaymentBatchPODetail>> data : details.entrySet()) {
			MBankList bankList = generateBankList(data.getKey(), data.getValue());
			response += " " + bankList.getDocumentNo();
			
			// 3 - Según parámetro, completo las listas de pagos electrónicos
			if (completeList) {
				if (bankList.completeIt() != DocAction.STATUS_Completed) {
					throw new Exception(Msg.getMsg(getCtx(), "PaymentBatchPOEPListGenerationError") + ": " + bankList.getProcessMsg());
				}
			}
		}
		
		return response;
	}
	
	private MBankList generateBankList(Integer bankAccountId, List<MPaymentBatchPODetail> details) throws Exception {
		//Datos
		MBankAccount bankAccount = new MBankAccount(getCtx(), bankAccountId, get_TrxName());
		MBankList bankList = new MBankList(getCtx(), 0, get_TrxName());
		
		//Creo la Cabecera de la Lista
		MDocType docType = getDocumentType(bankAccount.getC_Bank_ID());
		if (docType == null) {
			throw new Exception(Msg.getMsg(getCtx(), "PaymentBatchPOEPListDocTypeError"));
		}
		bankList.setC_DocType_ID(docType.getID());
		bankList.setDateTrx(new Timestamp(new Date().getTime()));
		bankList.setDescription(Msg.getMsg(getCtx(), "PaymentBatchPOElectronicPaymentListDesc") + " " + paymentBatch.getDocumentNo());
		bankList.setC_BankAccount_ID(bankAccountId);
		bankList.setDocStatus(DocAction.STATUS_Drafted);
		bankList.setDocAction(DocAction.ACTION_Complete);
		if (!bankList.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "PaymentBatchPOEPListGenerationError") + ": " + bankList.getProcessMsg());
		}
		
		//Lineas de la lista
		BigDecimal lineNo = new BigDecimal(10); 
		for (MPaymentBatchPODetail detail : details) {
			MBankListLine line = new MBankListLine(getCtx(), 0, get_TrxName());
			line.setC_BankList_ID(bankList.getID());
			line.setLine(lineNo);
			line.setDescription(Msg.getMsg(getCtx(), "PaymentBatchPOElectronicPaymentListDesc") + " " + paymentBatch.getDocumentNo());
			line.setC_AllocationHdr_ID(detail.getC_AllocationHdr_ID());
			if (!line.save()) {
				throw new Exception(Msg.getMsg(getCtx(), "PaymentBatchPOEPListGenerationError") + ": " + line.getProcessMsg());
			}
		}
		
		return bankList;
	}
	
	private Map<Integer, List<MPaymentBatchPODetail>> getDetails() {
		Map<Integer, List<MPaymentBatchPODetail>> map = new HashMap<Integer, List<MPaymentBatchPODetail>>();
		
		for (MPaymentBatchPODetail detail : paymentBatch.getBatchDetails()) {
			MBankAccount account = new MBankAccount(getCtx(), detail.getC_BankAccount_ID(), get_TrxName());
			if (account.isElectronicPaymentsAccount()) {
				if (map.get(detail.getC_BankAccount_ID()) != null) {
					map.get(detail.getC_BankAccount_ID()).add(detail);
				} else {
					List<MPaymentBatchPODetail> list = new ArrayList<MPaymentBatchPODetail>();
					list.add(detail);
					map.put(detail.getC_BankAccount_ID(), list);
				}
			}
		}
		
		return map;
	}
	
	private MDocType getDocumentType(Integer bankId) {
		//Construyo la query
		String sql = "SELECT dt.* " + 
					 "FROM C_DocType dt " +
					 "INNER JOIN C_BankList_Config bc ON dt.C_DocType_ID = bc.C_DocType_ID " +
					 "WHERE " + 
					  "bc.C_Bank_ID = ? ";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setInt(1, bankId);
			rs = ps.executeQuery();
			while (rs.next()) {
				return new MDocType(getCtx(), rs, get_TrxName());
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
	
}
