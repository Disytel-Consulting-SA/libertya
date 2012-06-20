package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1110 extends PluginPostInstallProcess {

	/** UID de la impresión de Transferencia de Mercadería */
	protected final static String MATERIAL_TRANSFER_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010044";
	protected final static String MATERIAL_TRANSFER_JASPER_REPORT_FILENAME = "MaterialTransfer.jasper";
	
	/** UID de la impresión de Cierre de Almacén */
	protected final static String WAREHOUSE_CLOSE_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010045";
	protected final static String WAREHOUSE_CLOSE_JASPER_REPORT_FILENAME = "WarehouseClose.jasper";
	
	protected String doIt() throws Exception {
		super.doIt();
		
		// Impresión de Transferencia de Mercadería
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						MATERIAL_TRANSFER_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(MATERIAL_TRANSFER_JASPER_REPORT_FILENAME)));
		
		// Impresión de Cierre de Almacén
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				WAREHOUSE_CLOSE_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(WAREHOUSE_CLOSE_JASPER_REPORT_FILENAME)));

		return " ";
	}
	
}
