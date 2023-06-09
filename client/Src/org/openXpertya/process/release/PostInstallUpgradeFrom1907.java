package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.util.DB;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1907 extends PluginPostInstallProcess {

	/** UID del informe de Movimientos Valorizados Detallado */
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010117";
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_FILENAME = "ValuedMovementsDetail.jasper";
	
	/** UID del informe de Autorizaciones de Usuarios */
	protected final static String USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_UID = "T0122CORE-AD_Process-1010645-20190906165716";
	protected final static String USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_FILENAME = "UserAuthorizations.jasper";
	
	/** UID de la impresión de Solicitudes de NC */
	protected final static String CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010274";
	protected final static String CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_FILENAME = "rpt_SolicitudNC.jasper";
	
	/** UID del informe de gestión de Solicitudes de NC */
	protected final static String CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_UID = "CORE-AD_Process-1010649";
	protected final static String CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_FILENAME = "CreditRequestReport.jasper";
	
	/** UID de Impresión de FE */
	protected final static String FE_REPORT_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FE_REPORT_FILENAME = "rpt_Factura_Electronica.jasper";
	
	/** UID del Subreporte de Notas de Crédito en Impresión de OP */
	protected final static String OP_NC_REPORT_UID = "CORE-AD_JasperReport-1010277";
	protected final static String OP_NC_REPORT_FILENAME = "OrdenPago_NotasDeCredito.jasper";
	
	/** UID Impresión de OP */
	protected final static String OP_REPORT_UID = "CORE-AD_JasperReport-1000012";
	protected final static String OP_REPORT_FILENAME = "OrdenPago.jasper";
	
	/** Reporte de Cierre de Tarjetas */
	protected final static String CREDITCARD_CLOSE_REPORT_UID = "CORE-AD_Process-1010444";
	protected final static String DYNAMIC_CREDITCARD_CLOSE_REPORT_UID = "CORE-AD_Process-1010445";
	protected final static String CREDITCARD_CLOSE_REPORT_FILENAME = "CreditCardClose.jasper";
	
	/** Reporte de Cierre de Tarjetas - Subreporte de Cupones Duplicados */
	protected final static String CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME = "CreditCardReport-DuplicateSubreport.jasper";
	
	/** Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos */
	protected final static String CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME = "CreditCardReport-InvalidSubreport.jasper";
	
	/** Informe de Auditoría de Entidades Financieras */
	protected final static String EntidadFinancieraAudit_FILENAME = "EntidadFinancieraAudit.jasper";
	protected final static String EntidadFinancieraAudit_UID = "CORE-AD_Process-1010359";
	
	/** Listado de Cupones de Tarjeta */
	protected final static String ListadoCuponesTarjeta_FILENAME = "ListadoCuponesTarjeta.jasper";
	protected final static String ListadoCuponesTarjeta_UID = "CORE-AD_Process-1010405";

	/** Reporte de Remitos */
	protected final static String ReporteRemitos_FILENAME = "InOutReport.jasper";
	protected final static String ReporteRemitos_UID = "CORE-AD_Process-1010422";
	
	/** UID del informe de Análisis de Ventas por Esquema de Descuento */
	protected final static String SALES_ANALYSIS_BY_DISCOUNT_SCHEMA_JASPER_REPORT_UID = "TEHLBY-AD_Process-20200319164902392-923485";
	protected final static String SALES_ANALYSIS_BY_DISCOUNT_SCHEMA_JASPER_REPORT_FILENAME = "SalesByDiscountSchema.jasper";
	
	/** UID de la impresión de la Lista de Banco */
	protected final static String BANK_LIST_PRINT_JASPER_REPORT_UID = "CORE-AD_Process-1010549";
	protected final static String BANK_LIST_PRINT_JASPER_REPORT_FILENAME = "BankListPrint.jasper";
	
	/** UID del Reporte de Cuentas Contables de Artículos */
	protected final static String PRODUCT_ACCTS_JASPER_REPORT_UID = "TEHLBY-AD_Process-20200720171849643-475519";
	protected final static String PRODUCT_ACCTS_JASPER_REPORT_FILENAME = "ProductAccounts.jasper";
	
	/** UID del informe de Auditoría de Inventario Físico */
	protected final static String PIA_JASPER_REPORT_UID = "CORE-AD_Process-1010369";
	protected final static String PIA_JASPER_REPORT_FILENAME = "PhysicalInventoryAudit.jasper";
	
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
		
		// Reporte de Movimientos Valorizados Detallado
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_FILENAME)));
		
		// Autorizaciones de Usuarios
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_UID,
				USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_FILENAME)));


		// Impresión de SNCP
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_FILENAME)));
		
		// Informe de SNCP
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_UID,
				CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_FILENAME)));
		
		// Impresión de FE
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						FE_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(FE_REPORT_FILENAME)));
		
		// Subreporte de Notas de Crédito en Impresión de OP
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						OP_NC_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(OP_NC_REPORT_FILENAME)));
		
		// Impresión de OP
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						OP_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(OP_REPORT_FILENAME)));

		// Reportes de Cierre de Tarjetas
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		// Cierre de Tarjetas - Subreporte de Duplicados
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME)),
				false);
		
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME)),
				false);
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME)), 
				false);
		
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME)), 
				false);
		
		// Informe de Auditoría de Entidades Financieras
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				EntidadFinancieraAudit_UID,
				EntidadFinancieraAudit_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(EntidadFinancieraAudit_FILENAME)));
		
		// Listado de Cupones de Tarjeta
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ListadoCuponesTarjeta_UID,
				ListadoCuponesTarjeta_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ListadoCuponesTarjeta_FILENAME)));
		
		// Reporte de Remitos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ReporteRemitos_UID,
				ReporteRemitos_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ReporteRemitos_FILENAME)));
		
		// Masterizacion de org.libertya.core.micro.r2867.dev.libro_iva_digital 
		// Setear a vacío todo los puntos decimales que hayan quedado con un espacio vacío
		DB.executeUpdate("update ad_expformat_row " + 
						"set decimalpoint = null " + 
						"where decimalpoint = ' ' and nodecimalpoint = 'Y'", get_TrxName());
		
		// Análisis de Ventas por Esquema de Descuento
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				SALES_ANALYSIS_BY_DISCOUNT_SCHEMA_JASPER_REPORT_UID,
				SALES_ANALYSIS_BY_DISCOUNT_SCHEMA_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(SALES_ANALYSIS_BY_DISCOUNT_SCHEMA_JASPER_REPORT_FILENAME)));
		
		// Impresión de Lista de Banco
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				BANK_LIST_PRINT_JASPER_REPORT_UID,
				BANK_LIST_PRINT_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(BANK_LIST_PRINT_JASPER_REPORT_FILENAME)));
		
		// Cuentas Contables de Artículos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				PRODUCT_ACCTS_JASPER_REPORT_UID,
				PRODUCT_ACCTS_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(PRODUCT_ACCTS_JASPER_REPORT_FILENAME)));
		
		// Auditoría de Inventario Físico
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				PIA_JASPER_REPORT_UID,
				PIA_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(PIA_JASPER_REPORT_FILENAME)));
		
		return " ";
	}
	

}
