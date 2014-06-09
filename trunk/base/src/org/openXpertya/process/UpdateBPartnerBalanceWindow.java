package org.openXpertya.process;

public class UpdateBPartnerBalanceWindow extends UpdateBPartnerBalance {

	protected void prepare() {
		setBpartnerID(getRecord_ID());
		super.prepare();
	}

}
