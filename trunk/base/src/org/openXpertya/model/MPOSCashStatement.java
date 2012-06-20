package org.openXpertya.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MPOSCashStatement extends X_C_POSCashStatement {

	private static CLogger s_log = CLogger.getCLogger(MPOSCashStatement.class);
	
	private MPOSJournal posJournal = null;
	
	/**
	 * Contructor de PO
	 * @param ctx
	 * @param C_POSCashStatement_ID
	 * @param trxName
	 */
	public MPOSCashStatement(Properties ctx, int C_POSCashStatement_ID,
			String trxName) {
		super(ctx, C_POSCashStatement_ID, trxName);
		
		if (C_POSCashStatement_ID == 0) {
			setQty(0);
		}
	}

	/**
	 * Constructor de PO
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MPOSCashStatement(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Determina el valor numérico del valor de efectivo parámetro
	 * 
	 * @param cashValue
	 *            String con la denominación. Debe ser alguno de CASHVALUE_XXX
	 * @return {@link BigDecimal} con el importe representado.
	 * @throws NumberFormatException
	 */
	public static BigDecimal getCashValue(String cashValue) throws NumberFormatException{
		BigDecimal divisor= null;
		char type = cashValue.charAt(0);
		if (type == 'C') {
			divisor = new BigDecimal(100);
		} else if (type == 'U') {
			divisor = BigDecimal.ONE;
		} else {
			s_log.warning("Unknown Cash Value. CashValue=" + cashValue);
			return BigDecimal.ZERO;
		}
		
		return new BigDecimal(cashValue.substring(1, 4)).setScale(2).divide(
				divisor, 2, RoundingMode.HALF_UP);
	}
	
	/**
	 * Calcula el importe que representa una determinada cantidad de una
	 * denominación de efectivo.
	 * 
	 * @param cashValue
	 *            String con la denominación. Debe ser alguno de CASHVALUE_XXX
	 * @param qty
	 *            Cantidad de unidades de la denominación indicada.
	 * @return {@link BigDecimal} con el importe representado.
	 */
	public static BigDecimal getCashAmount(String cashValue, int qty) {
		BigDecimal amount = BigDecimal.ZERO;
		try {
			amount = getCashValue(cashValue).multiply(new BigDecimal(qty));
		} catch (NumberFormatException e) {
			s_log.warning("Invalid Cash Value Format. Cannot parse BigDecimal. CashValue="
					+ cashValue);
			return BigDecimal.ZERO;
		}
		return amount;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// Solo un registro por denominación de dinero.
		if (!validateUniqueCashValue()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "DuplicatedCashTypeError"));
			return false;
		}
		
		// La cantidad debe ser mayor que cero.
		if (getQty() <= 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(),
					"ValueMustBeGreatherThanZero",
					new Object[] { Msg.translate(getCtx(), "Qty") }));
			return false;
		}
		
		// Setea el importe que representa esta declaración de valores.
		setAmount(getCashAmount(getCashValue(), getQty()));
		
		return true;
	}

	/**
	 * Para una misma caja diaria solo puede haber un registro por denominación
	 * de valor. Este método se encarga de validar este hecho.
	 * 
	 * @return 
	 */
	private boolean validateUniqueCashValue() {
		String sql =
			"SELECT COUNT(*) " +
			"FROM C_POSCashStatement " +
			"WHERE CashValue = ? " +
			  "AND C_POSJournal_ID = ? " +
			  "AND C_POSCashStatement_ID <> ?";
		long count = (Long) DB.getSQLObject(get_TrxName(), sql, new Object[] {
				getCashValue(), getC_POSJournal_ID(),
				getC_POSCashStatement_ID() });
		return count == 0;
	}

	/**
	 * @return Devuelve la caja diaria a la cual pertenece esta declaración de
	 *         valores en caja
	 */
	public MPOSJournal getPOSJournal() {
		if (posJournal == null) {
			posJournal = new MPOSJournal(getCtx(), getC_POSJournal_ID(), get_TrxName());
		}
		return posJournal;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		if (success) {
			getPOSJournal().setCashStatementAmt();
			success = getPOSJournal().save();
		}
		return success;
	}

	@Override
	protected boolean afterDelete(boolean success) {
		if (success) {
			getPOSJournal().setCashStatementAmt();
			success = getPOSJournal().save();
		}
		return success;
	}

	
	
}
