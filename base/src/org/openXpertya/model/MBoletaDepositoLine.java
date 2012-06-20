package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/** 
 *  Linea de boleta de deposito
 *  
 *  Una linea de boleta de deposito referencia al valor 
 *  depositado en la cuenta bancaria
 *  @author Jorge Vidal - Disytel
 *  @version 1.9
 * 
 */
public class MBoletaDepositoLine extends X_M_BoletaDepositoLine {

	/** Pago (Cheque) de esta línea */
	private MPayment payment;
	/** Boleta de Depósito a la que pertenece esta línea */
	private MBoletaDeposito boletaDeposito;
	
	public MBoletaDepositoLine(Properties ctx, int M_BoletaDepositoLine_ID, String trxName) {
		super(ctx, M_BoletaDepositoLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBoletaDepositoLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean afterSave(boolean newRecord, boolean success) {
		// Revisar consistencia con la boleta de deposito
		if (getPayment_Amt().compareTo(BigDecimal.ZERO) > 0) {
			getBoletaDeposito().checkLines();
		}
		return success;
	}

	/*----------------------------------
	 @Author: Jorge Vidal - Disytel 
	 @Fecha: 06/09/2006
	 @Comentario: Asigna la boleta en el pago
	 @Parametros:
	 -------------------------------------------*/
	public boolean limpiarPago() {
		boolean ret = false;
		// Asignar el pago a la boleta
		if (getC_Payment_ID() != 0) {
			if (getPayment().getM_BoletaDeposito_ID() != 0) {
				getPayment().setM_BoletaDeposito_ID(0);
				ret = getPayment().save();
			}
		}
		return ret;
	}

	protected boolean beforeSave(boolean newRecord) {
		// Se limpia la referencia al pago si este cambió
		if (is_ValueChanged("C_Payment_ID")) {
			payment = null;
		}
		
		// El cheque debe ser un recibo (IsReceipt = 'Y')
		if (!getPayment().isReceipt()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "CheckMustBeReceiptError"));
			return false;
		}
		
		// Se verifica si el cheque fue agregado previamente en otra línea de esta boleta.
		if (newRecord && checkInBoleta()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "CheckAlreadyInBoleta"));
			return false;
		}
		
		// La moneda del Cheque debe ser igual a la moneda de la boleta.
		if (getPayment().getC_Currency_ID() != getBoletaDeposito().getC_Currency_ID()) {
			log.saveError("SaveError", 
				Msg.getMsg(getCtx(), "InvalidPaymentCurrencyForBoleta", 
					new Object[] { 
						MCurrency.getISO_Code(getCtx(), getPayment().getC_Currency_ID()),	
						MCurrency.getISO_Code(getCtx(), getBoletaDeposito().getC_Currency_ID())
					}
				)
			);
			return false;
		}
		
		return true;
	}

	/*----------------------------------
	 @Author: Jorge Vidal - Disytel 
	 @Fecha: 06/09/2006
	 @Comentario: Eliminar la referencia del pago a la boleta de deposito
	 @Parametros:
	 -------------------------------------------*/
	/*public boolean delete() {

		limpiarPago();
		return super.delete();
	}*/
	
	protected boolean afterDelete (boolean success) {
		if(success)
			limpiarPago();
		return success;
	} 	//	afterDelete

	/**
	 * @return Devuelve el pago (Cheque) asociado a esta línea de boleta
	 */
	public MPayment getPayment() {
		if (payment == null) {
			payment = new MPayment(getCtx(), getC_Payment_ID(), get_TrxName());
		}
		return payment;
	}
	
	/**
	 * @return Devuelve la boleta de depósito que contiene esta línea.
	 */
	public MBoletaDeposito getBoletaDeposito() {
		if (boletaDeposito == null) {
			boletaDeposito = new MBoletaDeposito(getCtx(), getM_BoletaDeposito_ID(), get_TrxName());
		}
		return boletaDeposito;
	}
	
	/**
	 * Verifica si el cheque asociado a esta línea ya fue asignado previamente
	 * a otra línea de la boleta de depósito.
	 * @return
	 */
	private boolean checkInBoleta() {
		String sql = 
			" SELECT COUNT(*) " +
			" FROM M_BoletaDepositoLine " +
			" WHERE C_Payment_ID = ? " +
			"   AND M_BoletaDeposito_ID = ?";
			
		Long count = (Long)DB.getSQLObject(get_TrxName(), sql, 
			new Object[] { getC_Payment_ID(), getM_BoletaDeposito_ID()});
		
		return count != null && count > 0;
	}
	
	/**
	 * @return Devuelve el Cheque Contra-Movimiento generado a partir
	 * del Cheque original asociado a esta línea.
	 */
	public MPayment getReversalPayment() {
		MPayment reversal = null;
		if (getC_Reverse_Payment_ID() > 0) {
			reversal = new MPayment(getCtx(), getC_Reverse_Payment_ID(), get_TrxName());
		}
		return reversal;
	}
	
	/**
	 * @return Devuelve el Cheque de entrada a la cuenta destino generado
	 * a partir del cheque original asociado a esta línea.
	 */
	public MPayment getDepoPayment() {
		MPayment depo = null;
		if (getC_Depo_Payment_ID() > 0) {
			depo = new MPayment(getCtx(), getC_Depo_Payment_ID(), get_TrxName());
		}
		return depo;
	}
}
