package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MPaymentBatchPO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class RemovePaymentProposalProcess extends SvrProcess {

	MPaymentBatchPO paymentBatch = null;
	
	@Override
	protected void prepare() {
		//Lote de pagos
		paymentBatch = new MPaymentBatchPO(getCtx(), getRecord_ID(), get_TrxName());
	}

	@Override
	protected String doIt() throws Exception {
		//Construyo la query
		String sql = "DELETE FROM c_paymentbatchpodetail WHERE c_paymentbatchpo_id = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setInt(1, getRecord_ID());
			ps.executeUpdate();
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
		
		//  Modifico valor del campo asociado al botón que dispara este proceso para desactivar
		//  la lógica de solo lectura y poder ejecutar nuevmente la generación de detalles
		paymentBatch.setGeneratePaymentProposal("N");
		paymentBatch.setGrandTotal(new BigDecimal(0));
		if (!paymentBatch.save()) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "PaymentBatchPODetailRemoveError") + ": " + paymentBatch.getProcessMsg());
		}
		
		return Msg.getMsg(getCtx(), "PaymentBatchPODetailRemoveOK");
	}

	

}
