package org.openXpertya.process;

import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.Env;

public class UpdateBPartnerBalance extends SvrProcess {

	/** Entidad Comercial */
	
	private MBPartner bpartner; 
	
	@Override
	protected void prepare() {
		bpartner = new MBPartner(getCtx(), getRecord_ID(), get_TrxName());
	}
	
	@Override
	protected String doIt() throws Exception {
		// Obtengo el managaer actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager();
		// Obtengo la organización
		MOrg org = new MOrg(getCtx(),Env.getAD_Org_ID(getCtx()), get_TrxName());
		// Actualizo el saldo y estado de la entidad comercial
		CallResult result = manager.updateBalanceAndStatus(getCtx(), org,
				bpartner, get_TrxName());
		if (result.isError()) {
			throw new Exception(result.getMsg());
		}
		return "@Terminated@";
	}
}
