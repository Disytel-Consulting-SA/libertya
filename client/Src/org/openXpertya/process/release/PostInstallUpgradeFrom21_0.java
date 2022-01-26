package org.openXpertya.process.release;

import org.openXpertya.process.PluginPostInstallProcess;

public class PostInstallUpgradeFrom21_0 extends PluginPostInstallProcess {
	
	/** Listado de OC */
	protected final static String LISTADO_DE_OC_JASPER_REPORT_UID = "CORE-AD_Process-1010433";
	protected final static String LISTADO_DE_OC_JASPER_REPORT_FILENAME = "PurchaseOrderReport.jasper";
	
	/** Listado de OC Vencidas o Sin Novedades */
	protected final static String ORDERS_DUE_JASPER_REPORT_UID = "CORE-AD_Process-1010432";
	protected final static String ORDERS_DUE_JASPER_REPORT_FILENAME = "ListOfPurchaseOrdersDue.jasper";
	
	/** Impresión de FE */
	protected final static String FE_REPORT_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FE_REPORT_FILENAME = "rpt_Factura_Electronica.jasper";
	
	/** Informe de Cobranza y Pagos */
	protected final static String COBRANZAS_PAGOS_REPORT_UID = "CORE-AD_Process-1010401";
	protected final static String COBRANZAS_PAGOS_REPORT_FILENAME = "InformeDeCobranzasYPagos.jasper";
	
	/** UID del Informe de Compras por Region */
	protected final static String COMPRAS_POR_REGION_REPORT_UID = "CORE-AD_Process-1010400";
	protected final static String COMPRAS_POR_REGION_REPORT_FILENAME = "ComprasPorRegion.jasper";
	
	/** Impresión de Facturas */
	protected final static String INV_REPORT_UID = "CORE-AD_JasperReport-1000021";
	protected final static String INV_REPORT_FILENAME = "rpt_FactA.jasper";
	
	/** Reporte de Cuenta Corriente por Entidad Comercial */
	protected final static String BALANCE_BY_BPARTNER_JASPER_REPORT_UID = "JACLBY-AD_JasperReport-20200413133217034-110405";
	protected final static String BALANCE_BY_BPARTNER_JASPER_REPORT_FILENAME = "CurrentAccountByBPartner.jasper";

	/** Detalle de Cobros/Pagos sin Conciliar */
	protected final static String UNRECONCILED_PAYMENTS_JASPER_REPORT_UID = "CORE-AD_Process-1010577";
	protected final static String UNRECONCILED_PAYMENTS_JASPER_REPORT_FILENAME = "UnreconciledPaymentsDetailed.jasper";
	
	/** Consulta de Despacho de Importación */
	protected final static String TABLE_CLEARANCE_JASPER_REPORT_UID = "JACLBY-AD_Process-20200601184841124-606998";
	protected final static String TABLE_CLEARANCE_JASPER_REPORT_FILENAME = "ImportClearanceReport.jasper";
	
	/** Impresión de Despacho de Importación */
	protected final static String IMPORT_CLEARANCE_PRINT_JASPER_REPORT_UID = "JACLBY-AD_JasperReport-20210430140236681-145033";
	protected final static String IMPORT_CLEARANCE_PRINT_JASPER_REPORT_FILENAME = "rpt_DespachoImportacion.jasper";
	
	/** Comprobante de Retención */
	public static String COMPROBANTE_RETENCION_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010081";
	public static String COMPROBANTE_RETENCION_JASPER_REPORT_FILENAME = "rpt_Comprobante_Retencion.jasper";
	
	/** Reporte de Saldos por Comercial */
	protected final static String BALANCE_BY_COMERCIAL_JASPER_REPORT_UID = "JACLBY4.7-AD_Process-20200408171313237-755685";
	protected final static String BALANCE_BY_COMERCIAL_JASPER_REPORT_FILENAME = "BalanceBySalesRep.jasper";
	
	/** Informe de Comparación de Precios */
	protected final static String PRICES_COMPARISON_JASPER_REPORT_UID = "JACAD1_RA1-AD_Process-20210127141500085-924635";
	protected final static String PRICES_COMPARISON_JASPER_REPORT_FILENAME = "PricesComparison.jasper";
	
	/** Listado de Precios Modificados */
	protected final static String PRICE_CHANGE_REPORT_JASPER_REPORT_UID = "JACAD1_RA2-AD_Process-20210202130311740-315374";
	protected final static String PRICE_CHANGE_REPORT_JASPER_REPORT_FILENAME = "PriceChangeReport.jasper";
	
	/** Informe Libro IVA */
	protected final static String LIVA_JASPER_REPORT_UID = "LIVA2CORE-AD_JasperReport-1010047-20121031201418";
	protected final static String LIVA_JASPER_REPORT_FILENAME = "InformeLibroIVA.jasper";
	
	/** Subreporte Informe Libro IVA Manuales */
	protected final static String LIVA_SUBREPORT_MANUAL_JASPER_REPORT_UID = "LIVARPENH-AD_JasperReport-20210831110740889-608253";
	protected final static String LIVA_SUBREPORT_MANUAL_JASPER_REPORT_FILENAME = "SubReport_TaxInformeLibroIva_Manuales.jasper";
	
	/** Reporte Libro Diario General */
	protected final static String LIDIGEN_JASPER_REPORT_UID = "LIDIGEN-AD_Process-20200915121504378-687567";
	protected final static String LIDIGEN_JASPER_REPORT_FILENAME = "LibroDiarioGeneral.jasper";
	
	/** Informe de Libro de IVA Ventas Detallado */
	protected final static String LIVADET_VENTAS_JASPER_REPORT_UID = "LIVADET-AD_Process-20210510104140000-804554";
	protected final static String LIVADET_VENTAS_JASPER_REPORT_FILENAME = "Libro_IVA_Sales_Detailed.jasper";
	
	/** Informe de Libro de IVA Compras Detallado */
	protected final static String LIVADET_COMPRAS_JASPER_REPORT_UID = "LIVADET-AD_Process-20210510104521071-681043";
	protected final static String LIVADET_COMPRAS_JASPER_REPORT_FILENAME = "Libro_IVA_Purchase_Detailed.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		// Listado de OC
		updateReport(LISTADO_DE_OC_JASPER_REPORT_UID, LISTADO_DE_OC_JASPER_REPORT_FILENAME);
		
		// Listado de OC Vencidas o Sin Novedades
		updateReport(ORDERS_DUE_JASPER_REPORT_UID, ORDERS_DUE_JASPER_REPORT_FILENAME);
		
		// Impresión de FE
		updateReport(FE_REPORT_UID, FE_REPORT_FILENAME);
		
		// Informe de Cobranza y Pagos
		updateReport(COBRANZAS_PAGOS_REPORT_UID, COBRANZAS_PAGOS_REPORT_FILENAME);
		
		// Compras por Región
		updateReport(COMPRAS_POR_REGION_REPORT_UID, COMPRAS_POR_REGION_REPORT_FILENAME);
		
		// Impresión de Facturas
		updateReport(INV_REPORT_UID, INV_REPORT_FILENAME);
		
		// Reporte de Cuenta Corriente por Entidad Comercial
		updateReport(BALANCE_BY_BPARTNER_JASPER_REPORT_UID, BALANCE_BY_BPARTNER_JASPER_REPORT_FILENAME);
		
		// Detalle de Cobros/Pagos sin Conciliar
		updateReport(UNRECONCILED_PAYMENTS_JASPER_REPORT_UID, UNRECONCILED_PAYMENTS_JASPER_REPORT_FILENAME);
		
		// Consulta de Despacho de Importación
		updateReport(TABLE_CLEARANCE_JASPER_REPORT_UID, TABLE_CLEARANCE_JASPER_REPORT_FILENAME);
		
		// Impresión de Despacho de Importación
		updateReport(IMPORT_CLEARANCE_PRINT_JASPER_REPORT_UID, IMPORT_CLEARANCE_PRINT_JASPER_REPORT_FILENAME);
		
		// Comprobante de Retención
		updateReport(COMPROBANTE_RETENCION_JASPER_REPORT_UID, COMPROBANTE_RETENCION_JASPER_REPORT_FILENAME);

		// Reporte de Saldos por Comercial
		updateReport(BALANCE_BY_COMERCIAL_JASPER_REPORT_UID, BALANCE_BY_COMERCIAL_JASPER_REPORT_FILENAME);

		// Informe de Comparación de Precios
		updateReport(PRICES_COMPARISON_JASPER_REPORT_UID, PRICES_COMPARISON_JASPER_REPORT_FILENAME);
		
		// Listado de Precios Modificados
		updateReport(PRICE_CHANGE_REPORT_JASPER_REPORT_UID, PRICE_CHANGE_REPORT_JASPER_REPORT_FILENAME);
		
		// Libro IVA
		updateReport(LIVA_JASPER_REPORT_UID, LIVA_JASPER_REPORT_FILENAME);
		updateReport(LIVA_SUBREPORT_MANUAL_JASPER_REPORT_UID, LIVA_SUBREPORT_MANUAL_JASPER_REPORT_FILENAME);
		
		// Reporte Libro Diario General
		updateReport(LIDIGEN_JASPER_REPORT_UID, LIDIGEN_JASPER_REPORT_FILENAME);
		
		/** Informe de Libro de IVA Ventas Detallado */
		updateReport(LIVADET_VENTAS_JASPER_REPORT_UID, LIVADET_VENTAS_JASPER_REPORT_FILENAME);
		
		/** Informe de Libro de IVA Compras Detallado */
		updateReport(LIVADET_COMPRAS_JASPER_REPORT_UID, LIVADET_COMPRAS_JASPER_REPORT_FILENAME);
		
		return " ";
	}

}
