package org.openXpertya.process.release;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MDiscountConfig;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPaymentTerm;
import org.openXpertya.model.PO;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1009 extends PluginPostInstallProcess {

	/** UID del reporte de Libro de IVA */
	protected final static String LIBRO_IVA_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010024";
	protected final static String LIBRO_IVA_JASPER_REPORT_FILENAME = "LibroDeIVA.jasper";

	/** UID de Artículos para las configuraciones de descuentos */
	protected final static String PAYMENTMEDIUM_DISCOUNT_PRODUCT_UID = "CORE-M_Product-1015415";
	protected final static String PAYMENTMEDIUM_SURCHARGE_PRODUCT_UID = "CORE-M_Product-1015416";

	protected final static String PAYMENTTERM_DISCOUNT_PRODUCT_UID = "CORE-M_Product-1015417";
	protected final static String PAYMENTTERM_SURCHARGE_PRODUCT_UID = "CORE-M_Product-1015418";

	protected final static String ORGCHARGE_DISCOUNT_PRODUCT_UID = "CORE-M_Product-1015419";
	protected final static String ORGCHARGE_SURCHARGE_PRODUCT_UID = "CORE-M_Product-1015420";

	protected final static String BPARTNER_DISCOUNT_PRODUCT_UID = "CORE-M_Product-1015413";
	protected final static String BPARTNER_SURCHARGE_PRODUCT_UID = "CORE-M_Product-1015414";

	protected String doIt() throws Exception {
		super.doIt();

		// Actualizar informe binario para reporte de libro de IVA
		MJasperReport.updateBinaryData(get_TrxName(), getCtx(),
				LIBRO_IVA_JASPER_REPORT_UID, JarHelper.readBinaryFromJar(
						jarFileURL,
						getBinaryFileURL(LIBRO_IVA_JASPER_REPORT_FILENAME)));

		// Actualizar las configuraciones de descuentos que existan con los
		// artículos default creados para los distintos tipos de descuentos y
		// recargos y tipos de documento necesarios
		updateDiscountConfigs();

		// Actualizar los programas de pagos de las facturas que tienen un
		// esquema de vencimiento configurado, no poseen esquemas de pago y el
		// pendiente de la factura es mayor a 0
		updateInvoicesPaySchedule();
		
		return "";
	}

	protected void prepare() {
		super.prepare();
	}

	/**
	 * Actualización de todas las configuraciones de descuentos existentes sobre
	 * los artículos y tipos de doc a configurar para los distintos documentos
	 * para descuentos y recargos
	 * 
	 * @throws Exception
	 *             en caso de que no existan los artículos con los UID asignados
	 *             o al guardar dichos descuentos
	 */
	private void updateDiscountConfigs() throws Exception {
		// Obtener las configuraciones de descuento existentes
		List<PO> discountConfigs = PO.find(getCtx(), "M_DiscountConfig",
				"isactive = 'Y'", null, null, get_TrxName());
		MDiscountConfig config;
		// Iterar por todas las configs y actualizar los tipos de documento y
		// artículos necesarios
		for (PO po : discountConfigs) {
			config = (MDiscountConfig) po;
			// Actualizar los tipos de docs generales
			config.setCreditDocumentType(MDiscountConfig.CREDITDOCUMENTTYPE_CreditNote);
			config.setDebitDocumentType(MDiscountConfig.DEBITDOCUMENTTYPE_Invoice);
			// Obtener los artículos a partir de los uids y asignarlos a las
			// configuraciones
			// Descuentos de Entidad Comercial
			config.setBPartner_DiscountProduct_ID(getProductByUID(BPARTNER_DISCOUNT_PRODUCT_UID));
			config.setBPartner_SurchargeProduct_ID(getProductByUID(BPARTNER_SURCHARGE_PRODUCT_UID));
			// Descuentos de Medios de Pago
			config.setPaymentMedium_DiscountProduct_ID(getProductByUID(PAYMENTMEDIUM_DISCOUNT_PRODUCT_UID));
			config.setPaymentMedium_SurchargeProduct_ID(getProductByUID(PAYMENTMEDIUM_SURCHARGE_PRODUCT_UID));
			// Descuentos de Esquemas de Vencimientos
			config.setPaymentTerm_DiscountProduct_ID(getProductByUID(PAYMENTTERM_DISCOUNT_PRODUCT_UID));
			config.setPaymentTerm_SurchargeProduct_ID(getProductByUID(PAYMENTTERM_SURCHARGE_PRODUCT_UID));
			// Descuentos de Cargo de Organización
			config.setCharge_DiscountProduct_ID(getProductByUID(ORGCHARGE_DISCOUNT_PRODUCT_UID));
			config.setCharge_SurchargeProduct_ID(getProductByUID(ORGCHARGE_SURCHARGE_PRODUCT_UID));
			if (!config.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}

	}

	/**
	 * Obtener el id del artículo que posee ese UID parámetro
	 * 
	 * @param componentObjectUID
	 *            UID
	 * @return id de artículo
	 * @throws Exception
	 *             en caso de que no se encuentre el artículo con el UID
	 *             parámetro
	 */
	private int getProductByUID(String componentObjectUID) throws Exception {
		// Obtener el id del artículo con ese UID
		int productID = DB
				.getSQLValue(
						get_TrxName(),
						"SELECT m_product_id FROM m_product WHERE ad_componentobjectUID = ?",
						componentObjectUID);
		// Si no existe tirar excepción
		if (productID <= 0) {
			throw new Exception(Msg.translate(getCtx(),
					"@ProductNotFound@. @BarCode@ : " + componentObjectUID));
		}
		return productID;
	}

	/**
	 * Crea los esquemas de pagos para las facturas que no poseen, esto permite
	 * que no se rompa en algunas partes del código. Las facturas a las que se
	 * les crea el esquema de pagos son aquellas que no poseen esquema de pago,
	 * tienen un esquema de vencimiento configurado y el pendiente es mayor a 0.
	 */
	private void updateInvoicesPaySchedule() throws Exception{
		// Obtener las facturas con esquema de vencimiento configurado,
		// pendientes de pago y que no tengan esquemas de pagos
		String sql = "SELECT distinct i.c_invoice_id " +
					 "FROM c_invoice as i " +
					 "LEFT JOIN c_invoicepayschedule as ips ON ips.c_invoice_id = i.c_invoice_id " +
					 "WHERE i.issotrx = 'Y' " +
					 	"AND ips.c_invoicepayschedule_id is null " +
					 	"AND invoiceopen(i.c_invoice_id,0) > 0 " +
					 	"AND i.c_paymentterm_id is not null " +
					 	"AND i.paymentrule = '"+MInvoice.PAYMENTRULE_OnCredit+"'";
		MInvoice invoice = null;
		MPaymentTerm pt = null;
		Map<Integer, MPaymentTerm> pts = new HashMap<Integer, MPaymentTerm>();
		PreparedStatement ps = DB.prepareStatement(sql, get_TrxName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			// Instanciar la factura
			invoice = new MInvoice(getCtx(), rs.getInt("c_invoice_id"), get_TrxName());
			// Obtengo el esquema de vencimientos de la caché
			pt = pts.get(invoice.getC_PaymentTerm_ID());
			// Si no se pudo obtener, la instancio y la guardo en la caché 
			if(pt == null){
				pt = new MPaymentTerm(getCtx(),
						invoice.getC_PaymentTerm_ID(), get_TrxName());
				pts.put(pt.getID(), pt);
			}
			// Aplicar el esquema de vencimientos para esta factura
			pt.apply(invoice);
			// Guarda las facturas
			if(!invoice.save()){
				throw new Exception("Error saving invoice "+invoice.toString());
			}
		}
	}
	
}
