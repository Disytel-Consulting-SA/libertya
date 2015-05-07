package org.openXpertya.process;


public class MassiveUpdateBPartnerCreditStatus extends MassiveUpdateBPartnerBalance {

	protected boolean isUpdateBalance(){
		return false;
	}
	
	protected boolean isUpdateStatus(){
		return true;
	}
}
