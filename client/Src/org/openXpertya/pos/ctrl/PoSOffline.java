package org.openXpertya.pos.ctrl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.FiscalDocumentPrint;
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

public class PoSOffline extends PoSConnectionState {

	@Override
	public void completeOrder(Order order, Set <Integer> ordersId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProductList searchProduct(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User searchUser(String name, String password) throws UserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BusinessPartner getBPartner(int bPartnerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Location> getBPartnerLocations(int bPartnerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getClientCurrencyID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean balanceValidate(Order order) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BigDecimal currencyConvert(BigDecimal amount, int fromCurrencyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean productStockValidate(int productId, BigDecimal count, int attributeSetInstanceID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getOrgCityId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<EntidadFinanciera> getEntidadesFinancieras() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PoSConfig> getPoSConfigs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product getProduct(int productId, int attributeSetInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reloadPoSConfig(int windowNo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tax getProductTax(int productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order loadOrder(int orderId, boolean loadLines) throws PosException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validatePoSConfig() throws PosException {
		getPoSCOnfig().validateOffline();		
	}

	@Override
	public User getCurrentUser() {
		return null;
	}

	@Override
	public Tax getProductTax(int productId, int locationID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PriceList> getPriceLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PriceList getCurrentPriceList(int windowNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePriceList(PriceList newPriceList, int windowNo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PriceListVersion getCurrentPriceListVersion(PriceList priceList,
			int windowNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getProductPrice(Product product,
			PriceListVersion priceListVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBPartner(BusinessPartner bpartner, int windowNo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DiscountSchema getDiscountSchema(int discountSchemaID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getVendors(int productID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadOrderLines(Order order) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<PaymentMedium> getPaymentMediums() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getCreditAvailableAmount(int invoiceID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentTerm> getPaymentTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentTerm getPaymentTerm(int paymentTermID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentMedium getPaymentMedium(Integer paymentMediumID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void voidDocuments() throws PosException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getUser(int userID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getMaxOrderLineQty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNextInvoiceDocumentNo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCheckDeadLineInRange(Integer deadline,
			Integer beforeCheckDeadLineFrom, Integer beforeCheckDeadLineTo,
			Integer actualCheckDeadLine) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Tax> getOtherTaxes(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tax getTax(Integer taxID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCheckCUITControlActivated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCreditNotesAvailables(Integer bpartnerID,
			boolean excludeCreditNotes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Organization getOrganization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tax> loadBPOtherTaxes(BusinessPartner bp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePriceList(Integer priceListID, int windowNo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean reprintInvoice(Order order, FiscalDocumentPrint fdp) {
		// TODO Auto-generated method stub
		return false;
	}
}
