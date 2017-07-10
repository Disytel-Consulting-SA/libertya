package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MEntidadFinanciera extends X_M_EntidadFinanciera {
	private static final long serialVersionUID = 1L;

	public MEntidadFinanciera(Properties ctx, int M_EntidadFinanciera_ID, String trxName) {
		super(ctx, M_EntidadFinanciera_ID, trxName);
	}

	public MEntidadFinanciera(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

//		if (!validEstablishmentNumber()) {
//			log.saveError("SaveError", Msg.getMsg(getCtx(), "WrongEstablishmentNumberErr"));
//			return false;
//		}
//
//		if (!validBankAccount()) {
//			log.saveError("SaveError", Msg.getMsg(getCtx(), "WrongBankAccountErr"));
//			return false;
//		}

		if (!validRegion()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "WrongRegionErr"));
			return false;
		}

		return true;
	}

	/**
	 * Validación de número de establecimiento. Se permite un
	 * único número de establecimiento por entidad comercial.
	 * @return true si el número de establecimiento es válido, caso contrario, false
	 */
	private boolean validEstablishmentNumber() {
		if (getEstablishmentNumber() != null && !getEstablishmentNumber().isEmpty()) {
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	EstablishmentNumber ");
			sql.append("FROM ");
			sql.append("	" + Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	C_BPartner_ID = ? ");
			sql.append("	AND M_EntidadFinanciera_ID != ? ");
			

			String en = DB.getSQLValueString(get_TrxName(), sql.toString(), getC_BPartner_ID(), getID());

			return ((en == null || en.trim().isEmpty()) || en.equals(getEstablishmentNumber()));
		}
		return true;
	}

	/**
	 * Validación de cuenta bancaria. Se permite una
	 * única cuenta bancaria por entidad comercial.
	 * @return true si la cuenta bancaria es válida, caso contrario, false
	 */
	private boolean validBankAccount() {
		if (getC_BankAccount_ID() > 0) {
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	C_BankAccount_ID ");
			sql.append("FROM ");
			sql.append("	" + Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	C_BPartner_ID = ? ");
			sql.append("	AND M_EntidadFinanciera_ID != ? ");

			int C_BankAccount_ID = DB.getSQLValue(get_TrxName(), sql.toString(), getC_BPartner_ID(), getID());

			return (C_BankAccount_ID <= 0 || C_BankAccount_ID == getC_BankAccount_ID());
		}
		return true;
	}

	/**
	 * Validación de región. Se permite un único 
	 * valor para la Región según entidad comercial.
	 * @return true si la región es válida, caso contrario, false
	 */
	private boolean validRegion() {
		if (getC_Region_ID() > 0) {
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	C_Region_ID ");
			sql.append("FROM ");
			sql.append("	" + Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	C_BPartner_ID = ? ");
			sql.append("	AND M_EntidadFinanciera_ID != ? ");

			int C_Region_ID = DB.getSQLValue(get_TrxName(), sql.toString(), getC_BPartner_ID(), getID());

			return (C_Region_ID <= 0 || C_Region_ID == getC_Region_ID());
		}
		return true;
	}

}
