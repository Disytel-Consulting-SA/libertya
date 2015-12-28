package org.openXpertya.pos.ctrl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.FiscalDocumentPrintListener;
import org.openXpertya.pos.exceptions.InsufficientBalanceException;
import org.openXpertya.pos.exceptions.InsufficientCreditException;
import org.openXpertya.pos.exceptions.InvalidOrderException;
import org.openXpertya.pos.exceptions.InvalidPaymentException;
import org.openXpertya.pos.exceptions.InvalidProductException;
import org.openXpertya.pos.exceptions.PosException;
import org.openXpertya.pos.exceptions.UserException;
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.pos.model.DiscountSchema;
import org.openXpertya.pos.model.EntidadFinanciera;
import org.openXpertya.pos.model.Location;
import org.openXpertya.pos.model.Order;
import org.openXpertya.pos.model.Organization;
import org.openXpertya.pos.model.PaymentMedium;
import org.openXpertya.pos.model.PaymentTerm;
import org.openXpertya.pos.model.PriceList;
import org.openXpertya.pos.model.PriceListVersion;
import org.openXpertya.pos.model.Product;
import org.openXpertya.pos.model.ProductList;
import org.openXpertya.pos.model.Tax;
import org.openXpertya.pos.model.User;
import org.openXpertya.print.fiscal.FiscalPrinterEventListener;
import org.openXpertya.process.DocActionStatusListener;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;

public abstract class PoSConnectionState {

	private PoSConfig posConfig = null;
	
	protected CLogger log = CLogger.getCLogger(PoSConnectionState.class);
	
	public abstract void completeOrder(Order order, Set <Integer> ordersId) throws PosException, InsufficientCreditException, InsufficientBalanceException, InvalidPaymentException, InvalidProductException ;
	
	public abstract boolean balanceValidate(Order order);
	
	public abstract ProductList searchProduct(String code);
	
	public abstract User searchUser(String name, String password) throws UserException;
	
	public abstract User getUser(int userID);
	
	public abstract BigDecimal currencyConvert(BigDecimal amount, int fromCurrencyId);
	
	public abstract BigDecimal currencyConvert(BigDecimal amount, int fromCurrencyId, int toCurrency);
	
	public abstract BusinessPartner getBPartner(int bPartnerID);
	
	public abstract List<Location> getBPartnerLocations(int bPartnerID);
	
	public abstract boolean productStockValidate(int productId, BigDecimal count, int attributeSetInstanceID);
	
	public abstract int getOrgCityId();
	
	public abstract List<EntidadFinanciera> getEntidadesFinancieras();
	
	public abstract List<PoSConfig> getPoSConfigs();
	
	public abstract Product getProduct(int productId, int attributeSetInstanceId);
	
	public abstract void reloadPoSConfig(int windowNo);
	
	public abstract Tax getProductTax(int productId);
	
	public abstract Tax getProductTax(int productId, int locationID);
	
	public abstract Order loadOrder(int orderId, boolean loadLines) throws InvalidOrderException, PosException;

	public abstract void loadOrderLines(Order order);
	
	public abstract void validatePoSConfig() throws PosException;
	
	public abstract User getCurrentUser();
	
	public abstract List<PriceList> getPriceLists();
	
	public abstract PriceList getCurrentPriceList(int windowNo);
	
	public abstract void updatePriceList(PriceList newPriceList, int windowNo);

	public abstract void updatePriceList(Integer priceListID, int windowNo);
	
	public abstract PriceListVersion getCurrentPriceListVersion(PriceList priceList, int windowNo);
	
	public abstract BigDecimal getProductPrice(Product product,PriceListVersion priceListVersion);
	
	public abstract void updateBPartner(BusinessPartner bpartner, int windowNo);
	
	public abstract DiscountSchema getDiscountSchema(int discountSchemaID);
	
	public abstract List<Integer> getVendors(int productID);

	public abstract List<PaymentMedium> getPaymentMediums();
	
	public abstract BigDecimal getCreditAvailableAmount(int invoiceID);
	
	public abstract List<PaymentTerm> getPaymentTerms();
	
	public abstract PaymentTerm getPaymentTerm(int paymentTermID);
	
	public abstract PaymentMedium getPaymentMedium(Integer paymentMediumID);
	
	public abstract void voidDocuments() throws PosException;
	
	public abstract Integer getMaxOrderLineQty();
	
	public abstract String getNextInvoiceDocumentNo();
	
	public abstract List<Tax> getOtherTaxes(IDocument document);
	
	public abstract Tax getTax(Integer taxID);
	
	public abstract boolean isCheckCUITControlActivated();
	
	public abstract boolean hasCreditNotesAvailables(Integer bpartnerID, boolean excludeCreditNotes);
	
	public abstract Organization getOrganization();
	
	public abstract List<Tax> loadBPOtherTaxes(BusinessPartner bp);
	
	public abstract boolean reprintInvoice(Order order, FiscalDocumentPrint fdp);
	
	public abstract boolean addSecurityValidationToCN();
	
	/**
	 * @param checkDeadLineToCompare
	 *            plazo a comparar
	 * @param checkDeadLineFrom
	 *            plazo inicial del rango de comparación
	 * @param checkDeadLineTo
	 *            plazo final del rango de comparación o null en caso que no
	 *            posea
	 * @param checkDeadLineActual
	 *            este plazo se utiliza cuando el plazo final es null, ese plazo
	 *            debería ser el plazo del medio de pago y se toma dentro del
	 *            rango cuando es estrictamente menos a éste
	 * @return true si el plazo checkDeadLineToCompare se encuentra dentro del
	 *         rango establecido, false caso contrario
	 */
	public abstract boolean isCheckDeadLineInRange(Integer deadline,
			Integer beforeCheckDeadLineFrom, Integer beforeCheckDeadLineTo,
			Integer actualCheckDeadLine); 
	
	public int getClientCurrencyID() {
		return getPoSCOnfig().getCurrencyID();
	}
	
	public PoSConfig getPoSCOnfig() {
		return posConfig;
	}
	
	public void setPoSCOnfig(PoSConfig posConfig) {
		this.posConfig = posConfig;
	}
	
	public DocActionStatusListener getDocActionStatusListener() {
		return docActionStatusListener;
	}
	public void setDocActionStatusListener(DocActionStatusListener dasl) {
		docActionStatusListener = dasl;
	}
	private DocActionStatusListener docActionStatusListener;
	private FiscalDocumentPrintListener fiscalDocumentPrintListener;
	private FiscalPrinterEventListener fiscalPrinterEventListener;

	/**
	 * @return the fiscalDocumentPrintListener
	 */
	public FiscalDocumentPrintListener getFiscalDocumentPrintListener() {
		return fiscalDocumentPrintListener;
	}

	/**
	 * @param fiscalDocumentPrintListener the fiscalDocumentPrintListener to set
	 */
	public void setFiscalDocumentPrintListener(
			FiscalDocumentPrintListener fiscalDocumentPrintListener) {
		this.fiscalDocumentPrintListener = fiscalDocumentPrintListener;
	}

	/**
	 * @return the fiscalPrinterEventListener
	 */
	public FiscalPrinterEventListener getFiscalPrinterEventListener() {
		return fiscalPrinterEventListener;
	}

	/**
	 * @param fiscalPrinterEventListener the fiscalPrinterEventListener to set
	 */
	public void setFiscalPrinterEventListener(
			FiscalPrinterEventListener fiscalPrinterEventListener) {
		this.fiscalPrinterEventListener = fiscalPrinterEventListener;
	}
	
	private ASyncProcess processListener = null;

	/**
	 * @return el valor de processListener
	 */
	public ASyncProcess getProcessListener() {
		return processListener;
	}

	/**
	 * @param processListener el valor de processListener a asignar
	 */
	public void setProcessListener(ASyncProcess processListener) {
		this.processListener = processListener;
	}
}
