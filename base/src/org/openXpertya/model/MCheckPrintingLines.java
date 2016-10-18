package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

/**
 * Linea a imprimir, correspondiente a un pago mediante cheque.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class MCheckPrintingLines extends X_C_CheckPrintingLines {
	private static final long serialVersionUID = 1L;

	/** Cabecera. */
	private MCheckPrinting MCheckPrinting;

	/** Pago correspondiente. */
	private MPayment m_payment;

	/**
	 * Constructor.
	 * @param ctx
	 * @param C_CheckPrintingLines_ID
	 * @param trxName
	 */
	public MCheckPrintingLines(Properties ctx, int C_CheckPrintingLines_ID, String trxName) {
		super(ctx, C_CheckPrintingLines_ID, trxName);
	}

	/**
	 * Constructor.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MCheckPrintingLines(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		int lines = getMCheckPrinting().getLinesReadyToPrint();

		// Si la linea está marcada para imprimir, debo verificar la cantidad
		// de lineas que esten marcadas para el mismo documento cabecera.
		if (isPrint()) {
			if (lines > org.openXpertya.model.MCheckPrinting.MAX_LINES) {
				log.severe(Msg.getMsg(getCtx(), "CheckPrintingLinesOutOfRange"));
				return false;
			}
		}
		return super.beforeSave(newRecord);
	}

	// Getters & Setters:

	/** @return los centavos del monto total del cheque. */
	public int getPayAmtCents() {
		MPayment payment = getPayment();
		BigDecimal payAmt = payment.getPayAmt().abs();
		BigDecimal result = payAmt.remainder(BigDecimal.ONE).multiply(new BigDecimal(100));
		return result.intValue();
	}

	/** @return el pago correspondiente. */
	public MPayment getPayment() {
		if (m_payment != null) {
			return m_payment;
		}
		m_payment = new MPayment(getCtx(), getC_Payment_ID(), get_TrxName());
		return m_payment;
	}

	/** @return MCheckPrinting cabecera. */
	public MCheckPrinting getMCheckPrinting() {
		if (MCheckPrinting != null) {
			return MCheckPrinting;
		}
		int chpid = getC_Checkprinting_ID();
		MCheckPrinting checkPrinting = new MCheckPrinting(getCtx(), chpid, get_TrxName());
		return checkPrinting;
	}

	public void setMCheckPrinting(MCheckPrinting mCheckPrinting) {
		MCheckPrinting = mCheckPrinting;
	}

}
