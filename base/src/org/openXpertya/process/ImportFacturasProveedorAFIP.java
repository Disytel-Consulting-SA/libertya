package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCategoriaIva;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoiceTax;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MTax;
import org.openXpertya.model.MVendorImportInvoice;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_I_Vendor_Invoice_Import;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

public class ImportFacturasProveedorAFIP extends AbstractImportProcess {

	/** Nombre de Preferences para los datos por defecto */
	private static final String DEFAULT_ORG_PREFERENCE_NAME = "Default_Org_Vendors";
	private static final String DEFAULT_DOCTYPE_PREFERENCE_NAME = "Default_Doctype_Vendors";
	private static final String DEFAULT_PRODUCT_PREFERENCE_NAME = "Default_Product_Vendors";
	public static final String DEFAULT_TOLERANCE_PREFERENCE_NAME = "Default_Tolerance_Vendors";
	private static final String DEFAULT_NOGRAVADOTAX_PREFERENCE_NAME = "Default_NoGravadoTax_Vendors";
	
	/**
	 * Cantidad de días posteriores a fecha de factura para obtener la fecha de
	 * vencimiento de CAE
	 */
	protected static Integer DAYS_TO_VTO_CAE = 10;
	
	/** Datos por defecto */
	private MOrg defaultOrg;
	private MProduct defaultProduct;
	private BigDecimal tolerance;
	private MTax noGravadoTax;
	
	/** Categoría de IVA de la Compañía */
	private MCategoriaIva ciClient;
	
	/** Caché de entidades comerciales por tipo y nro de identificación */
	protected Map<String, MBPartner> cacheBP = new HashMap<String, MBPartner>();
	
	/** Caché de monedas */
	protected Map<String, Integer> cacheCurrency = new HashMap<String, Integer>();
	
	/** Caché de tarifas de compra por Organización */
	protected Map<Integer, MPriceList> cachePriceList = new HashMap<Integer, MPriceList>();
	
	/** Articulos por Entidad Comercial */
	protected Map<Integer, Integer> cacheProductBP = new HashMap<Integer, Integer>();
	
	/** Caché para Doctypes */
	protected Map<Integer, Integer> cacheDocType = new HashMap<Integer, Integer>();
	
	protected MOrg getDefaultOrg() {
		return defaultOrg;
	}

	protected void setDefaultOrg(MOrg defaultOrg) {
		this.defaultOrg = defaultOrg;
	}

	protected MProduct getDefaultProduct() {
		return defaultProduct;
	}

	protected void setDefaultProduct(MProduct defaultProduct) {
		this.defaultProduct = defaultProduct;
	}

	protected BigDecimal getTolerance() {
		return tolerance;
	}

	protected void setTolerance(BigDecimal tolerance) {
		this.tolerance = tolerance;
	}

	protected MTax getNoGravadoTax() {
		return noGravadoTax;
	}

	protected void setNoGravadoTax(MTax noGravadoTax) {
		this.noGravadoTax = noGravadoTax;
	}

	protected MCategoriaIva getCiClient() {
		return ciClient;
	}

	protected void setCiClient(MCategoriaIva ciClient) {
		this.ciClient = ciClient;
	}

	public ImportFacturasProveedorAFIP() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void prepareImport() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeImport() throws Exception {
		// Carga los datos por defecto
		loadDefaultOrg();
		loadDefaultProduct();
		loadDefaultTolerance();
		loadDefaultNoGravadoTax();
		loadClientCategoriaIVA();
		loadPriceList();
	}

	/***
	 * Carga de la organización por defecto
	 * @throws Exception en caso de error
	 */
	protected void loadDefaultOrg() throws Exception {
		setDefaultOrg((MOrg) getPreferenceValue(DEFAULT_ORG_PREFERENCE_NAME, MOrg.Table_Name, "value"));
	}


	/**
	 * Carga del artículo por defecto
	 * @throws Exception en caso de error
	 */
	protected void loadDefaultProduct() throws Exception {
		setDefaultProduct((MProduct) getPreferenceValue(DEFAULT_PRODUCT_PREFERENCE_NAME, MProduct.Table_Name, "value"));
	}

	/**
	 * Carga la tolerancia de centavos por defecto de importes
	 * @throws Exception en caso de error
	 */
	protected void loadDefaultTolerance() throws Exception {
		String tolPref = getPreferenceValue(DEFAULT_TOLERANCE_PREFERENCE_NAME);
		setTolerance(Util.isEmpty(tolPref, true)?BigDecimal.ZERO:new BigDecimal(tolPref));
	}
	
	/**
	 * Carga el impuesto no gravado por defecto
	 * @throws Exception en caso de error
	 */
	protected void loadDefaultNoGravadoTax() throws Exception {
		setNoGravadoTax((MTax) getPreferenceValue(DEFAULT_NOGRAVADOTAX_PREFERENCE_NAME, MTax.Table_Name, "name"));
	}
	
	/**
	 * Carga la categoría de IVA de la compañía
	 * @throws Exception en caso de que la compañía no tenga asociada una categoría de iva
	 */
	protected void loadClientCategoriaIVA() throws Exception {
		MClient c = MClient.get(getCtx());
		Integer cc = c.getCategoriaIva();
		if(Util.isEmpty(cc, true)) {
			throw new Exception("La compañia no tiene asignada una categoria de iva");
		}
		setCiClient(new MCategoriaIva(getCtx(), cc, get_TrxName()));
	}
	
	/**
	 * Carga la tarifa de compras
	 * @throws Exception en caso de que no se pueda obtener una tarifa de compra
	 */
	protected void loadPriceList() throws Exception {
		MPriceList pl = getPriceList(getOrgID(0));
		if(pl == null) {
			throw new Exception("No fue posible obtener una tarifa de compra para la importacion.");
		}
		cachePriceList.put(getOrgID(0), pl);
	}
	
	/**
	 * @param preferenceName nombre de la preference
	 * @return el valor de la preference
	 */
	protected String getPreferenceValue(String preferenceName) {
		return MPreference.searchCustomPreferenceValue(preferenceName, Env.getAD_Client_ID(getCtx()),
				Env.getAD_Org_ID(getCtx()), Env.getAD_User_ID(getCtx()), false);
	}
	
	/**
	 * Buscar la preference por el nombre
	 * 
	 * @param preferenceName   nombre de la preference
	 * @param tableName        nombre de la tabla
	 * @param columnSearchName nombre de la columna de búsqueda de la tabla
	 * @return el PO relacionado a la preference, nombre de tabla e id de la columna
	 *         de búsqueda
	 */
	protected PO getPreferenceValue(String preferenceName, String tableName, String columnSearchName) {
		String defaultValue = getPreferenceValue(preferenceName);
		if (!Util.isEmpty(defaultValue, true)) {
			Integer recordID = DB
					.getSQLValue(
							get_TrxName(), "SELECT " + tableName + "_id FROM " + tableName
									+ " WHERE ad_client_id = ? AND " + columnSearchName + " = '" + defaultValue + "'",
							Env.getAD_Client_ID(getCtx()));
			if (!Util.isEmpty(recordID, true)) {
				M_Table table = M_Table.get(getCtx(), tableName);
				return table.getPO(recordID, get_TrxName());
			}
		}
		return null;
	}

	@Override
	protected String importRecord(PO importPO) throws Exception {
		X_I_Vendor_Invoice_Import vim = (X_I_Vendor_Invoice_Import) importPO;
		// Buscar la entidad comercial
		MBPartner bp = null;
		try {
			bp = findBP(vim);
		} catch(Exception e) {
			return e.getMessage();
		}

		// dREHER controlo que la organizacion del comprobante sea distinta de CERO...
		int orgID = getOrgID(bp.getC_BPartner_ID());
		if(orgID<=0) {
			orgID = vim.getAD_Org_ID();
			if(orgID<=0)
				orgID = bp.getAD_Org_ID();

			if(orgID <=0)
				return "La organizacion del comprobante NO puede ser CERO (*) - EC.Org=0/Importacion.Org=0/Preferencia.Org=0";

			setDefaultOrg(new MOrg(getCtx(), orgID, get_TrxName()));
		}

		// Crear la factura
		MInvoice invoice = null;
		try {
			invoice = createInvoice(vim, bp);
		} catch(Exception e) {
			return e.getMessage();
		}
		
		// Asociar la factura creada al registro de importación
		vim.setC_Invoice_ID(invoice.getID());
		vim.set_TrxName(get_TrxName());
				
		// Crear las líneas si es posible
		try {
			createInvoiceLine(vim, vim.getnetogravado(), vim.getiva(), invoice, bp);
			if(!Util.isEmpty(vim.getimporteopexentas(), true)) {
				createInvoiceLine(vim, vim.getimporteopexentas(), BigDecimal.ZERO, invoice, bp);
			}
		} catch(Exception e) {
			return e.getMessage();
		}
	
		// Crear el impuesto para no gravado
		try {
			createInvoiceTax(vim, vim.getnetonogravado(), invoice);
		} catch(Exception e) {
			return e.getMessage();
		}
		
		// Otros tributos tiene algun valor?
		if(vim.getotros_tributos().compareTo(BigDecimal.ZERO) != 0) {			
			try {
				createInvoiceTax(vim, vim.getnetogravado(), invoice);
			} catch(Exception e) {
				return e.getMessage();
			}
		}
		
		return IMPORT_OK;
	}

	/**
	 * @return entidad comercial para este registro de importación
	 * @throws Exception en caso de error
	 */
	protected MBPartner findBP(X_I_Vendor_Invoice_Import vim) throws Exception {
		MBPartner bp = cacheBP.get(getKeyBP(vim));
		if(bp != null) {
			return bp;
		}
		// Busco las entidades comerciales de este registro de importación
		List<PO> bps = searchBP(vim);
		String msg = null;
		// Validar la entidad comercial, cantidades, configuraciones, etc.
		if (!Util.isEmpty(bps)) {
			if (bps.size() == 1) {
				bp = (MBPartner) bps.get(0);
				if (!bp.isAllowPreloadVendorInvoices()) {
					msg = "El proveedor con numero de identificacion " + vim.getnumeroidentificacion() + " (" + bp.getValue()
							+ " - " + bp.getName() + ") no permite la precarga de facturas.";
				}
			} else {
				msg = "Existe mas de un proveedor con el numero de identificacion " + vim.getnumeroidentificacion();
			}
		} else {
			msg = "No existe proveedor con el numero de identificacion " + vim.getnumeroidentificacion();
		}
		
		// Si tenemos mensaje de error, elevar excepción
		if(msg != null) {
			throw new Exception(msg);
		}
		cacheBP.put(getKeyBP(vim), bp);
		log.info("Entidad Comercial Encontrada "+bp.getName());
		return bp;
	}

	/**
	 * @param vim registro de importación actual
	 * @return la clave para determinar unicidad de entidades comerciales
	 */
	protected String getKeyBP(X_I_Vendor_Invoice_Import vim) {
		return vim.getnumeroidentificacion();
	}
	
	/**
	 * @param vim registro de importación
	 * @return lista de todas las EC
	 */
	protected List<PO> searchBP(X_I_Vendor_Invoice_Import vim) throws Exception {
		return PO.find(getCtx(), X_C_BPartner.Table_Name,
				"ad_client_id = ? and isvendor = 'Y' and taxid = '"
						+ vim.getnumeroidentificacion() + "' and isactive = 'Y' and trxenabled = 'Y' " ,
				new Object[] { Env.getAD_Client_ID(getCtx()) }, null, get_TrxName());
	}

	/**
	 * @param bpID id de la entidad comercial
	 * @return el id de la organización a asociar en el comprobante
	 */
	protected Integer getOrgID(Integer bpID) {
		return getDefaultOrg() != null ? getDefaultOrg().getID() : 0;
	}
	
	/**
	 * @return el id del tipo de documento a asociar en el comprobante
	 */
	protected Integer getDocTypeID(X_I_Vendor_Invoice_Import vim) {
		// Obtener el tipo de documento 
		Integer dtID = cacheDocType.get(vim.getTipoComprobante());
		if(dtID == null) {
			dtID = getDocTypeByTipoComprobante(vim.getTipoComprobante());
			cacheDocType.put(vim.getTipoComprobante(), dtID);
		}
		return dtID;
	}
	
	/**
	 * @param bpID id de la entidad comercial
	 * @return el id del artículo a asociar en el comprobante
	 */
	protected Integer getProductID(MBPartner bp) {
		Integer pID = cacheProductBP.get(bp.getID());
		if(pID != null) {
			return pID;
		}
		if(!Util.isEmpty(bp.getM_Product_Related_ID(), true)) {
			cacheProductBP.put(bp.getID(), bp.getM_Product_Related_ID());
			return bp.getM_Product_Related_ID();
		}
		return getDefaultProduct() != null ? getDefaultProduct().getID() : 0;
	}
	
	/**
	 * @return el id del impuesto no gravado a asociar en el comprobante
	 */
	protected Integer getNoGravadoTaxID() {
		return getNoGravadoTax() != null ? getNoGravadoTax().getID() : 0;
	}
	
	/**
	 * Crear la factura
	 * 
	 * @param vim registro de importación
	 * @param bp  entidad comercial
	 * @return factura generada
	 * @throws Exception
	 */
	protected MInvoice createInvoice(X_I_Vendor_Invoice_Import vim, MBPartner bp) throws Exception {
		Integer orgID = getOrgID(bp.getID());
		
		MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
		invoice.setIsSOTrx(false);
		invoice.setBPartner(bp);
		invoice.setCUIT(bp.getTaxID());
		invoice.setAD_Org_ID(orgID);
		Integer dt = getDocTypeID(vim);
		if(dt <= 0) {
			throw new Exception("No existe el tipo de documento referente al codigo " + vim.getTipoComprobante());
		}
		invoice.setC_DocTypeTarget_ID(dt);
		invoice.setC_DocType_ID(dt);
		Integer letraID = getLetra(bp.getC_Categoria_Iva_ID());
		if(Util.isEmpty(letraID, true)){
			throw new Exception(Msg.getMsg(getCtx(), "LetraCalculationError"));
		}
		invoice.setC_Letra_Comprobante_ID(letraID);
		invoice.setPuntoDeVenta(
				vim.getPuntoDeVenta() > 9999 ? (vim.getPuntoDeVenta() - ((vim.getPuntoDeVenta() / 10000) * 10000))
						: vim.getPuntoDeVenta());
		invoice.setNumeroComprobante(vim.getnumerocomprobantedesde());
		// Si existe una factura de proveedor con mismo punto de venta y nro de
		// comprobante para el proveedor en cuestión, no seguir e informar el error
		if(isRepeatInvoice(invoice)) {
			throw new Exception("Comprobante ya existente para el proveedor, punto de venta y numero de comprobante.");
		}
		invoice.setDateInvoiced(vim.getFecha());
		invoice.setDateAcct(vim.getFecha());
		invoice.setcae(vim.getcae());
		invoice.setvtocae(getVtoCAE(vim.getFecha()));
		MPriceList pl = getPriceList(orgID);
		if(pl == null) {
			MOrg o = MOrg.get(getCtx(), orgID);
			throw new Exception("No se pudo encontrar la tarifa de compra relacionada a la organización " + o.getValue()
					+ " - " + o.getName());
		}
		invoice.setM_PriceList_ID(pl.getID());
		invoice.setNetAmount(vim.getimporteopexentas().add(vim.getnetogravado()));
		invoice.setGrandTotal(vim.gettotal());
		invoice.setPreloadInvoice(true);
		Integer mID = getMonedaID(vim.getMoneda().trim());
		if(mID == null) {
			throw new Exception("No se pudo encontrar moneda a partir del dato "+vim.getMoneda());
		}
		invoice.setC_Currency_ID(mID);
		invoice.setDescription("Factura generada por importacion de facturas de provedoor AFIP"
				+ (Env.getC_Currency_ID(getCtx()) == mID ? ""
						: " | Moneda " + vim.getMoneda() + " - Tipo de Cambio " + vim.gettipocambio()));
		setCustomData(invoice, vim);
		if(!invoice.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		log.info("Factura creada "+invoice.getDocumentNo());
		return invoice;
	}
	
	/**
	 * @param orgID id de la organización
	 * @return tarifa de compra relacionada con la organización
	 */
	protected MPriceList getPriceList(Integer orgID) {
		MPriceList pl = cachePriceList.get(orgID); 
		if(pl != null) {
			return pl;
		}
		pl = MPriceList.getPriceList(getCtx(), orgID, false, get_TrxName());
		if(pl == null) {
			pl = MPriceList.getPriceList(getCtx(), 0, false, get_TrxName());
		}
		if(pl != null) {
			cachePriceList.put(orgID, pl);
		}
		return pl;
	}
	
	/**
	 * Obtiene la fecha de vencimiento de CAE
	 * 
	 * @param dateinvoiced fecha de factura
	 * @return la fecha de vencimiento de cae para esta fecha de factura
	 */
	protected Timestamp getVtoCAE(Timestamp dateinvoiced) {
		Calendar cdi = Calendar.getInstance();
		cdi.setTimeInMillis(dateinvoiced.getTime());
		cdi.add(Calendar.DATE, DAYS_TO_VTO_CAE);
		return new Timestamp(cdi.getTimeInMillis());
	}
	
	/**
	 * Obtener el id de la moneda basado en el dato a comparar
	 * 
	 * @param moneda dato a comparar, puede ser el código iso o el símbolo
	 * @return id de la moneda o null si no existe
	 */
	protected Integer getMonedaID(String moneda) {
		// Si está en la caché se devuelve
		Integer monedaID = cacheCurrency.get(moneda);
		if(monedaID != null) {
			return monedaID;
		}
		MCurrency c = getMonedaBy("cursymbol", moneda);
		if(c == null) {
			c = getMonedaBy("iso_code", moneda);
		}
		
		if(c != null) {
			monedaID = c.getID();
			cacheCurrency.put(moneda, monedaID);
		}
		
		return monedaID;
	}
	
	/**
	 * Obtener la moneda basado en la columna y valor parámetro
	 * 
	 * @param columnName  nombre de columna a comparar
	 * @param columnValue valor de columna a comparar
	 * @return moneda basado en las condiciones, null si no se encuentra
	 */
	protected MCurrency getMonedaBy(String columnName, String columnValue) {
		return (MCurrency) PO.findFirst(getCtx(), MCurrency.Table_Name,
				"isactive = 'Y' and " + columnName + " = '" + columnValue + "'", null, null, get_TrxName());
	}
	
	/**
	 * @param vendorCategoriaIVAID id de la categoría de iva del proveedor
	 * @return letra basados en la categoría de iva de la compañía y del proveedor
	 *         parámetro
	 */
	protected Integer getLetra(Integer vendorCategoriaIVAID) {
		// Se obtiene el ID de la letra del comprobante a partir de las categorias de IVA.
		return CalloutInvoiceExt.darLetraComprobante(ciClient.getID(), vendorCategoriaIVAID);
	}
	
	/**
	 * Verifica si ya que existe una factura con los mismos datos que la importada
	 * 
	 * @param invoice factura actual
	 * @return true si existe, false caso contrario
	 */
	protected boolean isRepeatInvoice(MInvoice invoice) {
		return PO.existRecordFor(getCtx(), MInvoice.Table_Name,
				"c_bpartner_id = ? and puntodeventa = ? and NumeroComprobante = ?",
				new Object[] { invoice.getC_BPartner_ID(), invoice.getPuntoDeVenta(), invoice.getNumeroComprobante() },
				get_TrxName());
	}
	
	/**
	 * Creación de línea de la factura
	 * 
	 * @param vim        registro de importación
	 * @param taxBaseAmt importe base a comparar
	 * @param taxAmt     importe de impuesto
	 * @param invoice    factura
	 * @return línea de factura creada o null caso que no se haya creado
	 * @throws Exception en caso de error
	 */
	protected MInvoiceLine createInvoiceLine(X_I_Vendor_Invoice_Import vim, BigDecimal taxBaseAmt, BigDecimal taxAmt, MInvoice invoice, MBPartner bp) throws Exception {
		if(Util.isEmpty(taxBaseAmt, true)) {
			vim.setI_ErrorMsg((vim.getI_ErrorMsg() != null ? vim.getI_ErrorMsg() : "")
					+ "El importe base para calcular la tasa no es correcto. ");
			return null;
		}
		// Verificar si podemos encontrar una tasa de iva
		Integer taxID = obtainTax(taxBaseAmt, taxAmt);
		if(taxID <= 0) {
			vim.setI_ErrorMsg((vim.getI_ErrorMsg() != null ? vim.getI_ErrorMsg() : "")
					+ "No fue posible determinar el impuesto en base al importe neto e iva, se ha creado solo la cabecera no asi la linea. ");
			return null;
		}
		
		// Crear la línea
		MInvoiceLine invoiceLine = new MInvoiceLine(invoice);
		invoiceLine.setM_Product_ID(getProductID(bp));
		invoiceLine.setQty(BigDecimal.ONE);
		invoiceLine.setPriceEntered(taxBaseAmt);
		invoiceLine.setPriceList(taxBaseAmt);
		invoiceLine.setPriceActual(taxBaseAmt);
		invoiceLine.setC_Tax_ID(taxID);
		invoiceLine.setTaxAmt(taxAmt);
		if(!invoiceLine.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		log.info("Línea creada "+invoiceLine.getLine());
		return invoiceLine;
	}
	
	/**
	 * Obtener el ID del impuesto si es posible obtenerlo basado en el importe base
	 * y el de impuesto
	 * 
	 * @param taxBaseAmt importe base
	 * @param taxAmt     importe del impuesto
	 * @return id del impuesto obtenido, -1 caso contrario
	 */
	protected Integer obtainTax(BigDecimal taxBaseAmt, BigDecimal taxAmt) {
		// Verificar si es posible crear la línea dependiendo si concuerda el importe
		// neto con el de iva
		BigDecimal rate = null;
		if(!Util.isEmpty(taxBaseAmt, true)) {
			rate = taxAmt.multiply(Env.ONEHUNDRED).divide(taxBaseAmt, 2, BigDecimal.ROUND_HALF_UP);
		}
		// Si no se pudo obtener una tasa entre los datos, 
		if(rate == null) {
			return -1;
		}
		// Si la tasa es null no se crea la línea ya que no se puede determinar el
		// impuesto
		BigDecimal minRateByTolerance = rate.subtract(getTolerance());
		BigDecimal maxRateByTolerance = rate.add(getTolerance());
		return DB.getSQLValue(get_TrxName(),
				"SELECT c_tax_id FROM c_tax t JOIN c_taxcategory tc ON tc.c_taxcategory_id = t.c_taxcategory_id WHERE t.ad_client_id = ? AND tc.ismanual = 'N' AND rate between "
						+ minRateByTolerance + " and " + maxRateByTolerance + " and t.isactive = 'Y'", Env.getAD_Client_ID(getCtx()));
	}
	
	
	//duplicado de metodo obtainTax
	//se setea ismanual = Y y perceptiontype != B para que no me traiga ingresos brutos
	protected Integer obtainTaxManual(BigDecimal taxBaseAmt, BigDecimal taxAmt) {
		// Verificar si es posible crear la línea dependiendo si concuerda el importe
		// neto con el de iva
		BigDecimal rate = null;
		if(!Util.isEmpty(taxBaseAmt, true)) {
			rate = taxAmt.multiply(Env.ONEHUNDRED).divide(taxBaseAmt, 2, BigDecimal.ROUND_HALF_UP);
		}
		// Si no se pudo obtener una tasa entre los datos, 
		if(rate == null) {
			return -1;
		}
		// Si la tasa es null no se crea la línea ya que no se puede determinar el
		// impuesto
		BigDecimal minRateByTolerance = rate.subtract(getTolerance());
		BigDecimal maxRateByTolerance = rate.add(getTolerance());
		return DB.getSQLValue(get_TrxName(),
				"SELECT c_tax_id FROM c_tax t JOIN c_taxcategory tc ON tc.c_taxcategory_id = t.c_taxcategory_id WHERE t.ad_client_id = ? AND tc.ismanual = 'Y' AND perceptiontype <> 'B' AND rate between "
						+ minRateByTolerance + " and " + maxRateByTolerance + " and t.isactive = 'Y'", Env.getAD_Client_ID(getCtx()));
	}
	
	/**
	 * Crea el impuesto de factura
	 * 
	 * @param vim        registro de importación
	 * @param taxBaseAmt importe base
	 * @param invoice    factura actual
	 * @return impuesto de la factura creado, null caso contrario
	 * @throws Exception en caso de error
	 */
	protected MInvoiceTax createInvoiceTax(X_I_Vendor_Invoice_Import vim, BigDecimal taxBaseAmt, MInvoice invoice) throws Exception {
		MInvoiceTax it = null;
		if(!Util.isEmpty(taxBaseAmt, true)) {
			if(getNoGravadoTaxID() <= 0) {
				vim.setI_ErrorMsg((vim.getI_ErrorMsg() != null ? vim.getI_ErrorMsg() : "")
						+ "No existe impuesto por defecto configurado para registrar el importe no gravado. ");
				return null;
			}
			// Crear el impuesto de la factura
			it = new MInvoiceTax(getCtx(), 0, get_TrxName());
			it.setC_Invoice_ID(invoice.getID());
			it.setC_Tax_ID(getNoGravadoTaxID());
			it.setTaxBaseAmt(taxBaseAmt);
			if(!it.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			log.info("Impuesto de factura creado para la base "+taxBaseAmt);
		}
		return it;
	}
	
	//el base es el gravado
	protected MInvoiceTax createInvoiceTaxOtrosTributos(X_I_Vendor_Invoice_Import vim, BigDecimal taxBaseAmt, MInvoice invoice) throws Exception {
		MInvoiceTax it = null;
		if(!Util.isEmpty(taxBaseAmt, true)) {
			if(obtainTaxManual(taxBaseAmt, vim.getotros_tributos()) <= 0) {
				vim.setI_ErrorMsg((vim.getI_ErrorMsg() != null ? vim.getI_ErrorMsg() : "")
						+ "No existe impuesto por defecto configurado para registrar el importe de otros tributos. ");
				return null;
			}
			// Crear el impuesto de la factura
			it = new MInvoiceTax(getCtx(), 0, get_TrxName());
			it.setC_Invoice_ID(invoice.getID());
			it.setC_Tax_ID(obtainTaxManual(taxBaseAmt, vim.getotros_tributos()));
			it.setTaxBaseAmt(taxBaseAmt);
			if(!it.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			log.info("Impuesto de factura creado para la base "+taxBaseAmt);
		}
		return it;
	}
	
	@Override
	protected String afterImport() throws Exception {
		Trx.getTrx(get_TrxName()).commit();
		cacheBP = null;
		cacheCurrency = null;
		cacheDocType = null;
		cachePriceList = null;
		cacheProductBP = null;
		return "";
	}

	@Override
	protected void onError(Exception e) {
		// TODO Auto-generated method stub
		Trx.getTrx(get_TrxName()).rollback();		
	}

	@Override
	public void markImported(PO importPO) {
		importPO.set_ValueOfColumn(column_IsImported_ID, true);
		importPO.set_ValueOfColumn(column_Processed_ID, true);
		// Se incrementa la cantidad de líneas importadas.
		importedLines++;
	}

	@Override
	protected String getDeleteOldImportedAditionalWhereClause() {
		return " AND EXISTS (select c_invoice_id from c_invoice i where i.c_invoice_id = "
				+ MVendorImportInvoice.Table_Name + ".c_invoice_id AND docstatus NOT IN ('DR','IP'))";
	}
	
	/**
	 * Buscar el tipo de documento de proveedor referente al tipo de comprobante del
	 * archivo
	 * 
	 * @param tipoComprobante tipo de comprobante del archivo
	 * @return el id del tipo de documento de proveedor referente al tipo del
	 *         archivo o -1 si no se encuentra
	 */
	protected Integer getDocTypeByTipoComprobante(Integer tipoComprobante) {
		String sql = "select c_doctype_id " + 
				"from c_doctype dt " + 
				"where dt.ad_client_id = ? and issotrx = 'N' and isactive = 'Y' " + 
				"	and exists (select clave_busqueda " + 
				"			from e_electronicinvoiceref " + 
				"			where tabla_ref = 'TCOM' and position(dt.doctypekey in clave_busqueda) > 0 " + 
				"				and codigo = '"+tipoComprobante+"') " + 
				"	and docbasetype in ('API','APC') ";
		return DB.getSQLValue(get_TrxName(), sql, Env.getAD_Client_ID(getCtx()));
	}
	
	/**
	 * Setear datos en factura por las subclases
	 * @param invoice factura
	 */
	protected void setCustomData(MInvoice invoice, X_I_Vendor_Invoice_Import vim) {
		
	}
}
