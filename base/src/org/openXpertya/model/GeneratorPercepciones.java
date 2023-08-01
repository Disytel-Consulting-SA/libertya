package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.DiscountCalculator.ICreditDocument;
import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.plugin.common.PluginClassUtil;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;


public class GeneratorPercepciones {

	/** Logger */
	protected CLogger log = CLogger.getCLogger(AbstractPercepcionProcessor.class);

	/** Lista de relaciones entre Organizaciónes-Impuestos de Percepción */
	private List<MOrgPercepcion> orgPercepciones;
	
	/**
	 * Lista de procesadores de percepciones por relación
	 * Organizaciónes-Impuestos de Percepción
	 */
	private Map<Integer, AbstractPercepcionProcessor> percepcionProcessors;
	
	/** Documento a aplicar percepciones */
	private IDocument document;
	
	/** Tipo de Documento */
	private MDocType docType;
	
	/** Entidad comercial a la cual se aplica */
	private MBPartner bpartner;
	
	/** Categoría de IVA relacionada a la entidad comercial */
	private MCategoriaIva categoriaIVA;
	
	/** Contexto */
	private Properties ctx;
	
	/** Transacción */
	private String trxName;
	
	/** Instancia de TPV */
	private boolean isTPVInstance = false;
	
	/** Categoría de IVA relacionada a la organización */
	private MCategoriaIva orgCategoriaIVA = null;
	
	/**
	 * Elimina las percepciones de un documento en particular
	 * @param id id del documento
	 * @param trxName
	 * @throws Exception
	 */
	public static void deletePercepciones(Integer docID, boolean isSOTrx, String tableTaxName, String relatedDocColumnName, String trxName) throws Exception{
		if(isSOTrx){
			DB.executeUpdate(
					"DELETE FROM "+tableTaxName+" WHERE "+relatedDocColumnName+" = "
							+ docID
							+ " AND c_tax_id IN (SELECT c_tax_id FROM c_tax WHERE ispercepcion = 'Y')",
					trxName);
		}
	}
	
	/**
	 * Eliminar percepciones de factura parámetro
	 * @param invoice
	 * @param trxName trx específica, si es null se utiliza el trx de la factura parámetro
	 * @throws Exception
	 */
	public static void deletePercepciones(MInvoice invoice, String trxName) throws Exception{
		deletePercepciones(invoice.getID(), invoice.isSOTrx(), X_C_InvoiceTax.Table_Name, "c_invoice_id",
				Util.isEmpty(trxName, true) ? invoice.get_TrxName() : trxName);
	}
	
	/**
	 * Eliminar percepciones de pedido parámetro
	 * @param order
	 * @param trxName trx específica, si es null se utiliza el trx del pedido parámetro
	 * @throws Exception
	 */
	public static void deletePercepciones(MOrder order, String trxName) throws Exception{
		deletePercepciones(order.getID(), order.isSOTrx(), X_C_OrderTax.Table_Name, "c_order_id", 
				Util.isEmpty(trxName, true) ? order.get_TrxName() : trxName);
	}
	
	public GeneratorPercepciones(Properties ctx, String trxName){
		setCtx(ctx);
		setTrxName(trxName);
		setOrgPercepciones(new ArrayList<MOrgPercepcion>());
		setPercepcionProcessors(new HashMap<Integer, AbstractPercepcionProcessor>());
	}
	
	public GeneratorPercepciones(Properties ctx, IDocument document, String trxName){
		this(ctx,trxName);
		loadDocument(document);
	}
	
	/**
	 * Guarda el documento y adicionalmente la entidad comercial y la
	 * organización y sus datos relacionados necesarios
	 * 
	 * @param document
	 */
	public void loadDocument(IDocument document){
		setDocument(document);
		setDocType(Util.isEmpty(getDocument().getDocTypeID(), true) ? null
				: MDocType.get(getCtx(), getDocument().getDocTypeID()));
		loadBPartner(document.getBPartnerID());
		loadOrg(document.getOrgID());
	}
	
	/**
	 * Carga de la organización y las percepciones
	 * @param orgID
	 */
	public void loadOrg(Integer orgID){
		setOrgPercepciones(MOrgPercepcion.getOrgPercepciones(getCtx(), orgID,
				getTrxName()));
		MOrgInfo oi = MOrgInfo.get(getCtx(), orgID);
		if(!Util.isEmpty(oi.getC_Categoria_IVA_ID(), true)) {
			setOrgCategoriaIVA(new MCategoriaIva(getCtx(), oi.getC_Categoria_IVA_ID(), getTrxName()));
		}
		createPercepcionProcessors();
	}
	
	/**
	 * Crea los procesadores de percepción
	 */
	public void createPercepcionProcessors(){
		Map<Integer, AbstractPercepcionProcessor> percepcionProcessors = new HashMap<Integer, AbstractPercepcionProcessor>();
		PercepcionProcessorData data;
		MTax tax = null;
		AbstractPercepcionProcessor percepcionProcessor;
		Integer scale = MCurrency.getStdPrecision(getCtx(), getDocument().getCurrencyID(), getTrxName());
		for (MOrgPercepcion orgPercepcion : getOrgPercepciones()) {
			data = (PercepcionProcessorData)PluginClassUtil.get(PercepcionProcessorData.class);
			data.setDocument(getDocument());
			data.setBpartner(getBpartner());
			data.setCategoriaIVA(getCategoriaIVA());
			data.setAlicuota(orgPercepcion.getAlicuota());
			data.setConvenioMultilateral(orgPercepcion.isConvenioMultilateral());
			data.setUseCABAJurisdiction(orgPercepcion.isUseCABAJurisdiction());
			data.setAllowPartialReturn(orgPercepcion.isPartialReturn());
			data.setAllowTotalReturn(orgPercepcion.isTotalReturn());
			data.setMinimumNetAmt(orgPercepcion.getMinimumNetAmount());
			data.setScale(scale);
			data.setMinimumNetAmtByPadronType(MOrgPercepcionConfig.getOrgPercepcionConfigData(getCtx(),
					orgPercepcion.getAD_Org_ID(), getTrxName()));
			data.setMinimumPercepcionAmt(orgPercepcion.getMinimumPercepcionAmt());
			tax = new MTax(getCtx(), orgPercepcion.getC_Tax_ID(), getTrxName());
			data.setTax(tax);
			try{
				percepcionProcessor = AbstractPercepcionProcessor
						.getPercepcionProcessor(getCtx(),
								orgPercepcion.getC_RetencionProcessor_ID(),
								getTrxName());
				percepcionProcessor.setPercepcionData(data);
				percepcionProcessors.put(orgPercepcion.getID(), percepcionProcessor);
			} catch(Exception e){
				log.severe("Error al crear el procesador de percepcion");
			}
		}
		setPercepcionProcessors(percepcionProcessors);
	}
	
	/**
	 * Actualizar los procesadores de percepción al actualizar la entidad
	 * comercial
	 */
	public void updatePercepcionProcessors(){
		for (AbstractPercepcionProcessor percepcionProcessor : getPercepcionProcessors().values()) {
			percepcionProcessor.getPercepcionData().setBpartner(getBpartner());
			percepcionProcessor.getPercepcionData().setCategoriaIVA(getCategoriaIVA());
		}
	}
	
	/**
	 * Carga de la entidad comercial
	 * @param bpartnerID
	 */
	public void loadBPartner(Integer bpartnerID){
		setBpartner(new MBPartner(getCtx(), bpartnerID, getTrxName()));
		if(!Util.isEmpty(getBpartner().getC_Categoria_Iva_ID(), true)){
			setCategoriaIVA(new MCategoriaIva(getCtx(), getBpartner()
					.getC_Categoria_Iva_ID(), getTrxName()));
		}
		updatePercepcionProcessors();
	}	
	
	/**
	 * @return true si se debe aplicar las percepciones para este documento,
	 *         false caso contrario
	 */
	public boolean isApplyPercepcion() {
		return getDocument() != null 
				&& getDocument().isSOTrx()
				&& getDocument().isApplyPercepcion()
				&& CalloutInvoiceExt.ComprobantesFiscalesActivos()
				&& (getOrgCategoriaIVA() == null || getOrgCategoriaIVA().isPercepcionLiable()) 
				&& (getCategoriaIVA() != null && getCategoriaIVA().isPercepcionLiable())
				&& getDocType() != null 
				&& getDocType().isApplyPerception()
				&& (isTPVInstance() || (getDocType() != null && (!MDocType.DOCTYPE_Retencion_InvoiceCustomer
						.equals(getDocType().getDocTypeKey()) && !MDocType.DOCTYPE_Retencion_ReceiptCustomer
						.equals(getDocType().getDocTypeKey()))));
	}
	
	/**
	 * Obtener las tasas de impuesto que se aplican para esta organización 
	 * @param ctx
	 * @param orgID
	 * @param trxName
	 * @return
	 * @throws Exception
	 */
	public List<Percepcion> getDebitApplyPercepciones() throws Exception{
		List<Percepcion> percepciones = new ArrayList<Percepcion>();
		if(!isApplyPercepcion()){
			return percepciones;
		}
		Percepcion percepcion;
		for (MOrgPercepcion orgPercepcion : getOrgPercepciones()) {
			// Aplicar la percepción
			
			// Determina si es convenio multilateral
			boolean ECesCM = getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().getBpartner().getIIBBType() != null && getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().getBpartner().getIIBBType().equals(MBPartner.IIBBTYPE_ConvenioMultilateral);
			
			// Si la Entidad Comercial es IIBB del tipo Convenio multilateral y la Percepcion de la ORG tambien lo es
			// o NO lo es en la Entidad Comercial y tampoco lo es en la Organizacion
			if ((!orgPercepcion.isConvenioMultilateral() && !ECesCM) || (orgPercepcion.isConvenioMultilateral() && ECesCM)) {
				percepcion = getPercepcionProcessors().get(orgPercepcion.getID()).applyDebitPerception();			
				// Impuesto
				if(percepcion != null && percepcion.getTaxAmt().compareTo(BigDecimal.ZERO) > 0){
					percepcion.orgPercepcionID = orgPercepcion.getID();
					percepciones.add(percepcion);
				}
			}
		}
		return percepciones;
	}
	
	/**
	 * Para documentos de créditos, las percepciones a aplicar difieren de los
	 * débitos. Si un documento de crédito es de anulación o devolución de un
	 * débito, entonces se deben aplicar los mismo porcentajes de percepciones
	 * que se aplicaron en el débito. En cambio, si no posee débito relacionado,
	 * por ejemplo una NC creada sin relación, entonces se deben aplicar las
	 * percepciones básicas de un débito
	 * 
	 * @return lista de percepciones a aplicar al crédito
	 * @throws Exception
	 */
	public List<Percepcion> getCreditApplyPercepciones() throws Exception{
		ICreditDocument creditDocument = (ICreditDocument)getDocument();
		IDocument debitDocument = creditDocument.getDebitRelatedDocument();
		List<Percepcion> percepciones = new ArrayList<Percepcion>();
		if(!isApplyPercepcion()){
			return percepciones;
		}
		Percepcion percepcion;
		for (MOrgPercepcion orgPercepcion : getOrgPercepciones()) {
			// Enviar al procesador de percepciones los datos de débito relacionado y si es
			// por anulación de comprobante, los importes mínimos no se controlan ya que
			// sino no aplica para devoluciones parciales
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setMinimumNetAmt(BigDecimal.ZERO);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setMinimumNetAmtByPadronType(null);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setMinimumPercepcionAmt(BigDecimal.ZERO);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setRelatedDocument(debitDocument);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData()
					.setVoiding(creditDocument.isVoiding());
			// Percepción aplicada
			percepcion = getPercepcionProcessors().get(orgPercepcion.getID()).applyCreditPerception();
			// Impuesto
			if(percepcion != null && percepcion.getTaxAmt().compareTo(BigDecimal.ZERO) > 0){
				percepcion.orgPercepcionID = orgPercepcion.getID();
				percepciones.add(percepcion);
			}
		}
		return percepciones;
	}
	
	/**
	 * Para documentos de créditos, las percepciones a aplicar difieren de los
	 * débitos. Si un documento de crédito es de anulación o devolución de un
	 * débito, entonces se deben aplicar los mismo porcentajes de percepciones
	 * que se aplicaron en el débito. En cambio, si no posee débito relacionado,
	 * por ejemplo una NC creada sin relación, entonces se deben aplicar las
	 * percepciones básicas de un débito
	 * 
	 * @return lista de percepciones a aplicar al crédito
	 * @throws Exception
	 */
	public List<Percepcion> getCreditApplyPercepcionesFromVoid() throws Exception{
		IDocument debitDocument = (IDocument)getDocument();
		ICreditDocument creditDocument = (ICreditDocument)debitDocument.getCreditRelatedDocument(); 
		//ICreditDocument creditDocument = (ICreditDocument)getDocument();
		//IDocument debitDocument = creditDocument.getDebitRelatedDocument();
		List<Percepcion> percepciones = new ArrayList<Percepcion>();
		if(!isApplyPercepcion()){
			return percepciones;
		}
		Percepcion percepcion;
		for (MOrgPercepcion orgPercepcion : getOrgPercepciones()) {
			// Enviar al procesador de percepciones los datos de débito relacionado y si es
			// por anulación de comprobante, los importes mínimos no se controlan ya que
			// sino no aplica para devoluciones parciales
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setMinimumNetAmt(BigDecimal.ZERO);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setMinimumNetAmtByPadronType(null);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setMinimumPercepcionAmt(BigDecimal.ZERO);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData().setRelatedDocument(creditDocument);
			getPercepcionProcessors().get(orgPercepcion.getID()).getPercepcionData()
					.setVoiding(debitDocument.isVoiding());
			// Percepción aplicada
			percepcion = getPercepcionProcessors().get(orgPercepcion.getID()).applyCreditPerception();
			// Impuesto
			if(percepcion != null && percepcion.getTaxAmt().compareTo(BigDecimal.ZERO) > 0){
				percepcion.orgPercepcionID = orgPercepcion.getID();
				percepciones.add(percepcion);
			}
		}
		return percepciones;
	}	
	
	public List<Percepcion> getApplyPercepciones() throws Exception{
		List<Percepcion> percepciones = new ArrayList<Percepcion>();
		if(getDocument() != null){
			percepciones = getDocument().getApplyPercepcion(this);
		}
		return percepciones;
	}
	
	
	
	/**
	 * Recalcula las percepciones en de la factura
	 * @param invoice
	 * @throws Exception
	 */
	public void recalculatePercepciones(MInvoice invoice) throws Exception{
		// Eliminar las percepciones de la factura
		deletePercepciones(invoice, getTrxName());
		// Calcular las percepciones para esta factura
		calculatePercepciones(invoice);
	}
	
	/**
	 * Calcula y guarda las percepciones para el documento
	 * @param invoice
	 * @throws Exception
	 */
	public void calculatePercepciones(MInvoice invoice) throws Exception{
		if(!isApplyPercepcion()){
			// Eliminar las percepciones de la factura
			deletePercepciones(invoice, getTrxName());
			return;
		}
		// Poner todas las percepciones en 0
		String sql = "UPDATE c_invoicetax SET taxamt = 0, taxbaseamt = 0 WHERE c_invoice_id = "
				+ invoice.getID()
				+ " AND c_tax_id IN (SELECT distinct c_tax_id FROM ad_org_percepcion WHERE ad_org_id = "
				+ invoice.getAD_Org_ID()+")";
		DB.executeUpdate(sql, getTrxName());
		// Obtener las percepciones a percibir de la organización
		List<Percepcion> percepciones = getApplyPercepciones();
		// Recorrer las percepciones y agregarlas a las facturas
		BigDecimal percepcionAmt;
		MInvoiceTax invoiceTax;
		BigDecimal exencionPerc;
		for (Percepcion percepcion : percepciones) {
			// Porcentaje de exención de la entidad comercial
			exencionPerc = MBPartner.getPercepcionExencionMultiplierRate(
					getBpartner().getID(), percepcion.orgPercepcionID,
					Env.getDate(), 2, getTrxName());
			// Porcentaje del total a aplicar
			percepcionAmt = percepcion.getTaxAmt().multiply(exencionPerc);
			// Verificar si existe ese impuesto cargado en la factura, si es así
			// actualizarlo, sino crear uno nuevo
			invoiceTax = MInvoiceTax.get(getCtx(), invoice.getID(),
					percepcion.getTaxID(), getTrxName());
			// Si el monto de percepción es 0 y existe el impuesto agregado a la
			// factura entonces se elimina
			if(percepcionAmt.compareTo(BigDecimal.ZERO) == 0){
				if(invoiceTax != null){
					invoiceTax.delete(true);
				}
			}
			// Si el monto es distinto de 0 y existe el impuesto agregado,
			// entonces lo actualizo. En el caso que no exista ninguno se crea 
			else{
				// Si no existe ninguna, la agrego
				if(invoiceTax == null){
					invoiceTax = new MInvoiceTax(getCtx(), 0, getTrxName());
				}
				invoiceTax.setC_Invoice_ID(invoice.getID());
				invoiceTax.setC_Tax_ID(percepcion.getTaxID());
				invoiceTax.setTaxAmt(invoiceTax.getTaxAmt().add(percepcionAmt));
				invoiceTax.setTaxBaseAmt(percepcion.getTaxBaseAmt());
				invoiceTax.setRate(percepcion.getTaxRate());
				invoiceTax.setArcibaNormCode(percepcion.arcibaNormCode);
				
				if (invoiceTax.getAD_Org_ID() == 0){
					invoiceTax.setAD_Org_ID(invoice.getAD_Org_ID());
				}
				if(!invoiceTax.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		}
	}
	
	/**
	 * Recalcula las percepciones en el pedido
	 * @param pedido
	 * @throws Exception
	 */
	public void recalculatePercepciones(MOrder order) throws Exception{
		// Eliminar las percepciones pedido
		deletePercepciones(order, getTrxName());
		// Calcular las percepciones para este pedido
		calculatePercepciones(order);
	}
	
	/**
	 * Calcula y guarda las percepciones para el documento
	 * @param order
	 * @throws Exception
	 */
	public void calculatePercepciones(MOrder order) throws Exception{
		if(!isApplyPercepcion()){
			// Eliminar las percepciones de la factura
			deletePercepciones(order, getTrxName());
			return;
		}
		// Poner todas las percepciones en 0
		String sql = "UPDATE c_ordertax SET taxamt = 0, taxbaseamt = 0 WHERE c_order_id = "
				+ order.getID()
				+ " AND c_tax_id IN (SELECT distinct c_tax_id FROM ad_org_percepcion WHERE ad_org_id = "
				+ order.getAD_Org_ID()+")";
		DB.executeUpdate(sql, getTrxName());
		// Obtener las percepciones a percibir de la organización
		List<Percepcion> percepciones = getApplyPercepciones();
		// Recorrer las percepciones y agregarlas a las facturas
		BigDecimal percepcionAmt;
		MOrderTax orderTax;
		BigDecimal exencionPerc;
		for (Percepcion percepcion : percepciones) {
			// Porcentaje de exención de la entidad comercial
			exencionPerc = MBPartner.getPercepcionExencionMultiplierRate(
					getBpartner().getID(), percepcion.orgPercepcionID,
					Env.getDate(), 2, getTrxName());
			// Porcentaje del total a aplicar
			percepcionAmt = percepcion.getTaxAmt().multiply(exencionPerc);
			// Verificar si existe ese impuesto cargado en la factura, si es así
			// actualizarlo, sino crear uno nuevo
			orderTax = MOrderTax.get(getCtx(), order.getID(),
					percepcion.getTaxID(), getTrxName());
			// Si el monto de percepción es 0 y existe el impuesto agregado a la
			// factura entonces se elimina
			if(percepcionAmt.compareTo(BigDecimal.ZERO) == 0){
				if(orderTax != null){
					orderTax.delete(true);
				}
			}
			// Si el monto es distinto de 0 y existe el impuesto agregado,
			// entonces lo actualizo. En el caso que no exista ninguno se crea 
			else{
				// Si no existe ninguna, la agrego
				if(orderTax == null){
					orderTax = new MOrderTax(getCtx(), 0, getTrxName());
				}
				orderTax.setC_Order_ID(order.getID());
				orderTax.setC_Tax_ID(percepcion.getTaxID());
				orderTax.setTaxAmt(orderTax.getTaxAmt().add(percepcionAmt));
				orderTax.setTaxBaseAmt(percepcion.getTaxBaseAmt());
				orderTax.setRate(percepcion.getTaxRate());
				orderTax.setArcibaNormCode(percepcion.arcibaNormCode);
				
				if (orderTax.getAD_Org_ID() == 0){
					orderTax.setAD_Org_ID(order.getAD_Org_ID());
				}
				if(!orderTax.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		}
	}
	
	/**
	 * Calcula y guarda las percepciones para el documento
	 * @param invoice
	 * @throws Exception
	 */
	public BigDecimal totalPercepcionesRate() throws Exception{
		// Obtener las percepciones a percibir de la organización
		List<Percepcion> percepciones = getApplyPercepciones();
		BigDecimal totalRates = BigDecimal.ZERO;
		for (Percepcion percepcion : percepciones) {
				totalRates = totalRates.add(percepcion.getTaxRate());
		}
		return totalRates;
	}
	
	public List<MOrgPercepcion> getOrgPercepciones() {
		return orgPercepciones;
	}

	public void setOrgPercepciones(List<MOrgPercepcion> orgPercepciones) {
		this.orgPercepciones = orgPercepciones;
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public MBPartner getBpartner() {
		return bpartner;
	}

	public void setBpartner(MBPartner bpartner) {
		this.bpartner = bpartner;
	}

	public MCategoriaIva getCategoriaIVA() {
		return categoriaIVA;
	}

	public void setCategoriaIVA(MCategoriaIva categoriaIVA) {
		this.categoriaIVA = categoriaIVA;
	}

	public Map<Integer, AbstractPercepcionProcessor> getPercepcionProcessors() {
		return percepcionProcessors;
	}

	public void setPercepcionProcessors(Map<Integer, AbstractPercepcionProcessor> percepcionProcessors) {
		this.percepcionProcessors = percepcionProcessors;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public boolean isTPVInstance() {
		return isTPVInstance;
	}

	public void setTPVInstance(boolean isTPVInstance) {
		this.isTPVInstance = isTPVInstance;
	}

	public MDocType getDocType() {
		return docType;
	}

	public void setDocType(MDocType docType) {
		this.docType = docType;
	}

	public MCategoriaIva getOrgCategoriaIVA() {
		return orgCategoriaIVA;
	}

	public void setOrgCategoriaIVA(MCategoriaIva orgCategoriaIVA) {
		this.orgCategoriaIVA = orgCategoriaIVA;
	}

}
