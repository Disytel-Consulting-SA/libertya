package org.openXpertya.process;

import java.util.Properties;

public class MassiveUpdateBPartnerCreditStatus extends MassiveUpdateBPartnerBalance {

	public MassiveUpdateBPartnerCreditStatus(Properties ctx, String trxName) {
		super(ctx, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean isUpdateBalance(){
		return false;
	}
	
	protected boolean isUpdateStatus(){
		return true;
	}
}
