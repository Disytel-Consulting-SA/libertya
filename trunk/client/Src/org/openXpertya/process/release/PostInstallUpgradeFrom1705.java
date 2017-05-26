package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1705 extends PluginPostInstallProcess {

	/** UID del Listado de Cupones Totalizados por Estado */
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010181-20170125125731";
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_FILENAME = "CouponListByStatus.jasper";
	
	/** UID del Listado de Liquidaciones */
	protected final static String SETTLEMENT_LIST_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010180-20170125125715";
	protected final static String SETTLEMENT_LIST_JASPER_REPORT_FILENAME = "SettlementList.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		/*
		 * Actualizacion de binarios
		 * """"""""""""""""""""""""" 
		 * Utilizar SIEMPRE los métodos MJasperReport.updateBinaryData() y MProcess.addAttachment() 
		 * para la carga de informes tipo Jasper, el primero para la carga en AD_JasperReport y el 
		 * segundo en reportes dinámicos, los cuales van adjuntos en el informe/proceso correspondiente.
		 */
		
		// Listado de Cupones Totalizados por Estado
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					COUPON_LIST_BY_STATUS_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(COUPON_LIST_BY_STATUS_JASPER_REPORT_FILENAME)));
		
		// Listado de Liquidaciones
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					SETTLEMENT_LIST_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(SETTLEMENT_LIST_JASPER_REPORT_FILENAME)));
		
		return " ";
	}

}
