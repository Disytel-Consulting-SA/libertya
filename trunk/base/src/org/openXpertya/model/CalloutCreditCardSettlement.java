package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.DB;

/**
 * Callout para Cupones en liquidación.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class CalloutCreditCardSettlement extends CalloutEngine {

	/**
	 * Al seleccionar una Entidad Comercial, se debe asignar la
	 * organización de la misma a la cabecera de la liquidación.
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

		if (value != null && (Integer) value > 0) {

			Integer C_BPartner_ID = (Integer) value;
			StringBuffer sql = new StringBuffer();
			String trxName = null;

			sql.append("SELECT ");
			sql.append("	AD_Org_ID ");
			sql.append("FROM ");
			sql.append("	" + X_C_BPartner.Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	C_BPartner_ID = " + C_BPartner_ID);

			int AD_Org_ID = DB.getSQLValue(trxName, sql.toString());

			if (AD_Org_ID > 0) {
				mTab.setValue("AD_Org_ID", AD_Org_ID);
			}
		}
		setCalloutActive(false);
		return "";
	}

}
