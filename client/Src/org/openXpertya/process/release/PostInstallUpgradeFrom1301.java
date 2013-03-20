package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1301 extends PluginPostInstallProcess {

	/** UID del informe de movimientos de compra/venta por artículo */
	protected final static String PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010090";
	protected final static String PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_FILENAME = "ProductSalesPurchaseMovements.jasper";
	
	/** UID del informe de Maestras de Compras */
	protected final static String PURCHASE_MASTER_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010091";
	protected final static String PURCHASE_MASTER_JASPER_REPORT_FILENAME = "PurchaseMasterReport.jasper";
	
	/** UID del informe de Movimientos de artículo detallado */
	protected final static String PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010092";
	protected final static String PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_FILENAME = "ProductMovementsWithStockBalance.jasper";
	
	/** UID del subreporte del informe de Movimientos de artículo detallado */
	protected final static String PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010093";
	protected final static String PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_FILENAME = "ProductMovementsWithStockBalance_Subreport.jasper";
	
	/** UID de la impresión de Fraccionamiento */
	protected final static String SPLITTING_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010094";
	protected final static String SPLITTING_JASPER_REPORT_FILENAME = "Product Splitting.jasper";
	
	/** UID del informe de Precios Actualizados */
	protected final static String UPDATED_PRICES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010095";
	protected final static String UPDATED_PRICES_JASPER_REPORT_FILENAME = "UpdatedPrices.jasper";
	
	protected String doIt() throws Exception {
		super.doIt();
		
		// Movimientos de venta/compra de artículo
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_FILENAME)));
		
		// Maestra de Compras
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PURCHASE_MASTER_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PURCHASE_MASTER_JASPER_REPORT_FILENAME)));
		
		// Movimientos de artículo detallado
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_FILENAME)));
		
		// Subreporte de Movimientos de artículo detallado
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_FILENAME)));
		
		// Impresión de Fraccionamiento
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						SPLITTING_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(SPLITTING_JASPER_REPORT_FILENAME)));
		
		// Informe de Precios actualizados
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						UPDATED_PRICES_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(UPDATED_PRICES_JASPER_REPORT_FILENAME)));
		
		return " ";
	}

}
