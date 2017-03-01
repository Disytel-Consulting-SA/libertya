package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Callout para Entidades Financieras.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class CalloutEntidadFinanciera extends CalloutEngine {

	/**
	 * Al seleccionar una entidad comercial, se deben autocompletar los 
	 * campos Cuenta Bancaria, Región y Número de Establecimiento, sólo 
	 * si existe otra entidad financiera con la misma entidad comercial 
	 * referenciada, y con dichos campos completos.
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String bPartner(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (isCalloutActive()) {
			return "";
		}
		setCalloutActive(true);
		if (value != null) {
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	EstablishmentNumber, ");
			sql.append("	C_BankAccount_ID, ");
			sql.append("	C_Region_ID ");
			sql.append("FROM ");
			sql.append("	" + MEntidadFinanciera.Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	C_BPartner_ID = ? ");

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				pstmt = DB.prepareStatement(sql.toString());
				pstmt.setInt(1, (Integer) value);
				rs = pstmt.executeQuery();

				if (rs.next()) {
					String en = rs.getString(1);
					if (en != null && !en.trim().isEmpty()) {
						mTab.setValue("EstablishmentNumber", rs.getString(1));
					}
					int C_BankAccount_ID = rs.getInt(2);
					if (C_BankAccount_ID > 0) {
						mTab.setValue("C_BankAccount_ID", C_BankAccount_ID);
					}
					int C_Region_ID = rs.getInt(3);
					if (C_Region_ID > 0) {
						mTab.setValue("C_Region_ID", C_Region_ID);
					}
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, sql.toString(), e);
			} finally {
				try {
					pstmt.close();
					rs.close();
				} catch (Exception e) {
					log.log(Level.SEVERE, "Cannot close statement or resultset");
				}
			}
		} else {
			mTab.setValue("EstablishmentNumber", null);
			mTab.setValue("C_BankAccount_ID", null);
			mTab.setValue("C_Region_ID", null);
		}
		setCalloutActive(false);
		return "";
	}

}
