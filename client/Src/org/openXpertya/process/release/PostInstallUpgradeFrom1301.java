package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1301 extends PluginPostInstallProcess {

	/** UID del informe de movimientos de compra/venta por artículo */
	protected final static String PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010090";
	protected final static String PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_FILENAME = "ProductSalesPurchaseMovements.jasper";
	
	protected String doIt() throws Exception {
		super.doIt();
		
		// Impresión de Transferencia de Mercadería
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_FILENAME)));
		
		return " ";
	}

}
