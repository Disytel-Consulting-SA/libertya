package org.openXpertya.process.release;

import org.openXpertya.process.PluginPostInstallProcess;

public class PostInstallUpgradeFrom25_0 extends PluginPostInstallProcess {
	
	/** PostInstall jaclby23 Merge reconstruido */
	protected final static String REMITO_COMPRA_UID = "JACLBY-AD_JasperReport-20200806160946818-723528";
	protected final static String REMITO_COMPRA_FILENAME = "rpt_Remito_Compra.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		// Remito de compra
		updateReport(REMITO_COMPRA_UID, REMITO_COMPRA_FILENAME);
		
		return "";
	}
	
}
