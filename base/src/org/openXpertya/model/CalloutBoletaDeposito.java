package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class CalloutBoletaDeposito extends CalloutEngine {

	public CalloutBoletaDeposito() {
		super();
	}

	/*------------------------------------------
	 @Author: Jorge Vidal - Disytel 
	 @Fecha: 06/09/2006
	 @Comentario: Set Payment ammount to BoletaDepositoLine
	 @Parametros:
	 -------------------------------------------*/
	public String paymentChange(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		String trxName = null;
		Integer C_Payment_ID = (Integer) value;
		Integer boletaDepositoID = (Integer) mTab.getValue("M_BoletaDeposito_ID");
		// Si se seleccionó un pago...
		if (C_Payment_ID != null && C_Payment_ID > 0) {
			MPayment payment = new MPayment(ctx, C_Payment_ID.intValue(), trxName);
			MBoletaDeposito boleta = new MBoletaDeposito(ctx, boletaDepositoID, trxName);

			mTab.setValue("Payment_Amt", payment.getPayAmt());
			mTab.setValue("C_Currency_ID",new Integer(payment.getC_Currency_ID()));
			Env.setContext(ctx, WindowNo, "Payment_Amt", payment.getPayAmt()
					.toString());
			mTab.setValue("DueDate", payment.getDueDate());
			
			// Si la fecha de vencimiento del cheque es mayor que la fecha de la boleta
			// se agrega un mensaje de advertencia al guardar el registro.
			if (payment.getDueDate() != null && boleta.getFechaDeposito().compareTo(payment.getDueDate()) < 0) {
				String msg = Msg.translate(ctx, "PaymentDueDateBoletaWarning");
				mTab.setCurrentRecordWarning(msg);
			} else {
				mTab.clearCurrentRecordWarning();
			}
				
		} else {
			mTab.setValue("Payment_Amt", Env.ZERO);
			mTab.setValue("DueDate", null);
			Env.setContext(ctx, WindowNo, "Payment_Amt", Env.ZERO.toString());
			mTab.clearCurrentRecordWarning();
		}
		return "";
	}
	
	public String boleta(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (isCalloutActive()) {
			return "";
		}
		setCalloutActive(true);
		
		Integer boletaDepositoID = (Integer) value;
		if (boletaDepositoID != null && boletaDepositoID > 0) {
			// Se borra la EC del contexto a fin de que el InfoPayment no filtre automáticamente
			// por la EC de la boleta, ya que en este caso se buscan cheques de cualquier EC. 
			Env.setContext(ctx, WindowNo, "C_BPartner_ID", (String)null);
		}
		
		setCalloutActive(false);
		return "";
	}
	
	public String checkAccount(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (isCalloutActive()) {
			return "";
		}
		setCalloutActive(true);
		
		Integer bankAccountID = (Integer) value;
		if (bankAccountID == null) {
			bankAccountID = 0;
		}
		
		Env.setContext(ctx, WindowNo, "C_BankAccount_Filter_ID", bankAccountID);

		setCalloutActive(false);
		return "";
	}
}
