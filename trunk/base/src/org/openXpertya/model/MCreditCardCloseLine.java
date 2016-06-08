package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
public class MCreditCardCloseLine extends X_C_CreditCard_CloseLine {
	
	public MCreditCardCloseLine(Properties ctx, int C_CreditCard_CloseLine_ID,
			String trxName) {
		super(ctx, C_CreditCard_CloseLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCreditCardCloseLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		if ((!newRecord)
				&& (is_ValueChanged("CouponNumber")
						|| is_ValueChanged("CouponBatchNumber")
						|| is_ValueChanged("CreditCardNumber") 
						|| is_ValueChanged("M_EntidadFinancieraPlan_ID"))) {
			//Actualizo el Payment
			String sql = "UPDATE C_Payment SET CouponNumber = '"+ getCouponNumber() + "'"
		 			+ ", CouponBatchNumber = '" + getCouponBatchNumber()  + "'"
		 			+ ", CreditCardNumber = '" + getCreditCardNumber() + "'"
		 			+ ", M_EntidadFinancieraPlan_ID = " + getM_EntidadFinancieraPlan_ID()
		 			+ " WHERE C_Payment_ID =" + getC_Payment_ID()
		 			+ " AND AD_Org_ID = " + getAD_Org_ID();
			int count = DB.executeUpdate(sql,get_TrxName());
			if (count <= 0){
				log.saveError("SaveError", "Error al actualizar el cupón");
				return false;
			}
		}
		return true;
	}

}
