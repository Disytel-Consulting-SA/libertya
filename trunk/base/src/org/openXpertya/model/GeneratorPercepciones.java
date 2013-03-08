package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.DiscountCalculator.ICreditDocument;
import org.openXpertya.model.DiscountCalculator.IDocument;
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
	
	/**
	 * Elimina las percepciones de una factura particular
	 * @param invoiceID
	 * @param trxName
	 * @throws Exception
	 */
	public static void deletePercepciones(Integer invoiceID, String trxName) throws Exception{
		DB.executeUpdate(
				"DELETE FROM c_invoicetax WHERE c_invoice_id = "
						+ invoiceID
						+ " AND c_tax_id IN (SELECT c_tax_id FROM c_tax WHERE ispercepcion = 'Y')",
				trxName);
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
		for (MOrgPercepcion orgPercepcion : getOrgPercepciones()) {
			data = new PercepcionProcessorData();
			data.setDocument(getDocument());
			data.setBpartner(getBpartner());
			data.setCategoriaIVA(getCategoriaIVA());
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
				&& (getCategoriaIVA() != null && getCategoriaIVA().isPercepcionLiable())
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
	public List<MTax> getDebitApplyPercepciones() throws Exception{
		List<MTax> percepciones = new ArrayList<MTax>();
		if(!isApplyPercepcion()){
			return percepciones;
		}
		BigDecimal percepcionPerc, exencionPerc;
		MTax tax;
		for (MOrgPercepcion orgPercepcion : getOrgPercepciones()) {
			// Porcentaje de percepción
			percepcionPerc = getPercepcionProcessors().get(
					orgPercepcion.getID()).getPercepcionPercToApply();
			// Porcentaje de exención de la entidad comercial
			exencionPerc = MBPartner.getPercepcionExencionMultiplierRate(
					getBpartner().getID(), orgPercepcion.getID(),
					Env.getDate(), 2, getTrxName());
			// Porcentaje del total a aplicar
			percepcionPerc = percepcionPerc.multiply(exencionPerc);
			// Impuesto
			if(percepcionPerc.compareTo(BigDecimal.ZERO) > 0){
				tax = new MTax(getCtx(), orgPercepcion.getC_Tax_ID(), getTrxName());
				tax.setRate(percepcionPerc);
				percepciones.add(tax);
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
	public List<MTax> getCreditApplyPercepciones() throws Exception{
		ICreditDocument creditDocument = (ICreditDocument)getDocument();
		IDocument debitDocument = creditDocument.getDebitRelatedDocument();
		List<MTax> percepciones = new ArrayList<MTax>();
		MTax tax;
		// Si posee un débito relacionado, entonces aplico las percepciones del
		// débito, sino las percepciones comunes
		if(debitDocument != null){
			List<DocumentTax> appliedPercepciones = debitDocument.getAppliedPercepciones();
			for (DocumentTax documentTax : appliedPercepciones) {
				tax = new MTax(getCtx(), documentTax.getTaxID(), getTrxName());
				tax.setRate(documentTax.getTaxRate());
				percepciones.add(tax);
			}
		}
		else{
			percepciones = getDebitApplyPercepciones();
		}
		return percepciones;
	}
	
	public List<MTax> getApplyPercepciones() throws Exception{
		List<MTax> percepciones = new ArrayList<MTax>();
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
		deletePercepciones(invoice.getID(), getTrxName());
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
			deletePercepciones(invoice.getID(), getTrxName());
			return;
		}
		// Poner todas las percepciones en 0
		String sql = "UPDATE c_invoicetax SET taxamt = 0, taxbaseamt = 0 WHERE c_invoice_id = "
				+ invoice.getID()
				+ " AND c_tax_id IN (SELECT distinct c_tax_id FROM ad_org_percepcion WHERE ad_org_id = "
				+ invoice.getAD_Org_ID()+")";
		DB.executeUpdate(sql, getTrxName());
		// Obtener las percepciones a percibir de la organización
		List<MTax> percepciones = getApplyPercepciones();
		// Recorrer las percepciones y agregarlas a las facturas
		BigDecimal percepcionAmt;
		BigDecimal invoiceNetTotalAmt = invoice.getTotalLinesNetWithoutDocumentDiscount();
		if (invoice.isPerceptionsIncluded()){
			invoiceNetTotalAmt = invoice.getTotalLinesNetPerceptionIncludedWithoutDocumentDiscount();
		}
		Integer scale = MCurrency.getStdPrecision(getCtx(),
				invoice.getC_Currency_ID(), getTrxName());
		MInvoiceTax invoiceTax;
		for (MTax percepcion : percepciones) {
			// Calcular el monto de percepción
			percepcionAmt = percepcion.calculateTax(invoiceNetTotalAmt, false,
					scale);
			// Verificar si existe ese impuesto cargado en la factura, si es así
			// actualizarlo, sino crear uno nuevo
			invoiceTax = MInvoiceTax.get(getCtx(), invoice.getID(),
					percepcion.getID(), getTrxName());
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
				invoiceTax.setC_Tax_ID(percepcion.getID());
				invoiceTax.setTaxAmt(invoiceTax.getTaxAmt().add(percepcionAmt));
				invoiceTax.setTaxBaseAmt(invoiceNetTotalAmt);
				if (invoiceTax.getAD_Org_ID() == 0){
					invoiceTax.setAD_Org_ID(invoice.getAD_Org_ID());
				}
				if(!invoiceTax.save()){
					throw new Exception("ERROR updating percepcion invoice tax");
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
		List<MTax> percepciones = getApplyPercepciones();
		BigDecimal totalRates = BigDecimal.ZERO;
		for (MTax percepcion : percepciones) {
				totalRates = totalRates.add(percepcion.getRate());
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

}
