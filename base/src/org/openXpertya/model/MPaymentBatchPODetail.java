package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MPaymentBatchPODetail extends X_C_PaymentBatchPODetail {

	public MPaymentBatchPODetail(Properties ctx, int C_PaymentBatchPODetail_ID, String trxName) {
		super(ctx, C_PaymentBatchPODetail_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MPaymentBatchPODetail(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6711833591437403382L;
	
	private BigDecimal oldPaymentAmount = null; 
	
	protected boolean beforeSave(boolean newRecord) {
		MBPartner bPartner = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
		if (bPartner.getBatch_Payment_Rule() == null) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "BatchPaymentRuleNotSet"));
			return false;
		}
			
		if (!newRecord)
			oldPaymentAmount = (BigDecimal)get_ValueOld("PaymentAmount");
		else 
			oldPaymentAmount = new BigDecimal(0);
		return true;
	}
	
	protected boolean afterSave(boolean newRecord, boolean success) {
		if( !success ) {
            return success;
        }
		
		if( newRecord || is_ValueChanged( "PaymentAmount" )) {
			//4-Modifico valor del campo asociado al botón que dispara este proceso para activar
			//  la lógica de solo lectura y no poder ejecutarlo nuevmente si no se eliminan los detalles generados
			MPaymentBatchPO paymentBatch = new MPaymentBatchPO(getCtx(), this.getC_PaymentBatchPO_ID(), get_TrxName());  
			paymentBatch.setGeneratePaymentProposal("Y");
			//Actualizo el importe total en la cabecera
			paymentBatch.setGrandTotal(paymentBatch.getGrandTotal().add(getPaymentAmount()).subtract(oldPaymentAmount));
			if (!paymentBatch.save()) {
				throw new IllegalArgumentException(Msg.getMsg(getCtx(), "PaymentBatchPODetailGenerationError") + ": " + paymentBatch.getProcessMsg());
			}
		}
		
		return true;
	}
	
	protected boolean afterDelete( boolean success ) {
        if( !success ) {
            return success;
        }

        MPaymentBatchPO paymentBatch = new MPaymentBatchPO(getCtx(), this.getC_PaymentBatchPO_ID(), get_TrxName());
        paymentBatch.setGrandTotal(paymentBatch.getGrandTotal().subtract(getPaymentAmount()));
        if (!paymentBatch.save()) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "PaymentBatchPODetailGenerationError") + ": " + paymentBatch.getProcessMsg());
		}
        
        return true;
    }

	protected List<MPaymentBatchPOInvoices> getInvoices() {
		List<MPaymentBatchPOInvoices> detailInvoices = new ArrayList<MPaymentBatchPOInvoices>();
		//Construyo la query
		String sql = "SELECT * " + 
					 "FROM C_PaymentBatchPOInvoices " +
					 "WHERE " + 
					  "c_paymentbatchpodetail_id = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setInt(1, this.getID());
			rs = ps.executeQuery();
			while (rs.next()) {
				detailInvoices.add(new MPaymentBatchPOInvoices(getCtx(), rs, get_TrxName()));
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
		return detailInvoices;
	}
}
