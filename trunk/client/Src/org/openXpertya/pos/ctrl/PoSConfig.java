package org.openXpertya.pos.ctrl;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MRole;
import org.openXpertya.pos.exceptions.PosException;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class PoSConfig {

	private String name;
	
	// Desde las opciones de configuracion de TPV
	private int cashBookID; // -
	private Integer priceListID = null; // -
	private int warehouseID; // -
	private int orderDocTypeID; // -
	private int invoiceDocTypeID; // -
	private int inoutDocTypeID; // -
	private int bPartnerCashTrxID; // -
	private int supervisorRoleID; // - Nuevo -> AD_Role_ID
	private int checkBankAccountID; // Nuevo
	private int exemptTaxId;
	private int paymentTermID;
	private String printerName;
	private boolean createInvoice;
	private int posNumber;
	private boolean priceListWithTax;
	private boolean modifyPrice;
	private int currentUserID;
	private boolean sellWithoutStock;
	private boolean searchByValue;
	private boolean searchByValueLike;
	private boolean searchByUPC;
	private boolean searchByUPCLike;
	private boolean searchByName;
	private boolean searchByNameLike;
	private boolean searchToday;
	private boolean copyEntity;
	private boolean deliverOrderInWarehouse;
	private boolean printWarehouseDeliverDocument;
	private int cashID;
	private boolean controlCashReturns;
	private BigDecimal maxCashReturnWithoutAuth;
	private boolean initialAuthorization;
	private boolean lockedClosed;
	private boolean printCurrentAccountDocument;
	private String posnet;
	private boolean authorizeManualGeneralDiscount;
	private boolean voidDocuments;
	
	// Del sistema
	private int currencyID;
	private boolean userCanAccessInfoProduct;
	private boolean posJournal = false;
	private int stdPrecision = 2;
	private int costingPrecision = 4;

	public PoSConfig(MPOS pos) {
		setCurrencyID(Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID"));
		MCurrency currency = MCurrency.get(pos.getCtx(), getCurrencyID());
		stdPrecision = currency.getStdPrecision();
		costingPrecision = currency.getCostingPrecision();
		
		// TODO: Que hago con Country, Region del Tax ?
		setExemptTaxId(DB.getSQLValue(null, MRole.getDefault().addAccessSQL(" SELECT C_Tax_ID FROM C_Tax WHERE isTaxExempt='Y' ", "C_Tax", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO)));
		
		// TODO: Esto debe ser "Efectivo".
		setPaymentTermID(DB.getSQLValue(null, "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y' ", Env.getAD_Client_ID(Env.getCtx())));
		
		// Usuario actualmente conectado al sistema
		setCurrentUserID(Env.getAD_User_ID(Env.getCtx()));
		
		// Indicador si el usuario puede ver los buscadores de artículos
		setUserCanAccessInfoProduct(MRole.getDefault(Env.getCtx(), true).isInfoProductAccess());
		
		if(pos != null) {
			setCashBookID(pos.getC_CashBook_ID());
			setPriceListID(pos.getM_PriceList_ID());
			setBPartnerCashTrxID(pos.getC_BPartnerCashTrx_ID());
			setWarehouseID(pos.getM_Warehouse_ID());
			setSupervisorRoleID(pos.getAD_Role_ID());
			setCheckBankAccountID(pos.getC_BankAccount_ID());
			setName(pos.getName());
			setPrinterName(pos.getPrinterName());
			
			setOrderDocTypeID(pos.getC_OrderDocType_ID());
			setInvoiceDocTypeID(pos.getC_InvoiceDocType_ID());
			setInoutDocTypeID(pos.getC_InoutDocType_ID());
			setCreateInvoice(pos.isCreateInvoice());
			setPosNumber(pos.getPOSNumber());
			setModifyPrice(pos.isModifyPrice());
			setSellWithoutStock(pos.isSaleWithoutStock());
			
			// Search By ...
			setSearchByValue(pos.isSearchByValue());
			setSearchByValueLike(pos.isSearchByValueLike());
			setSearchByUPC(pos.isSearchByUPC());
			setSearchByUPCLike(pos.isSearchByUPCLike());
			setSearchByName(pos.isSearchByName());
			setSearchByNameLike(pos.isSearchByNameLike());
			
			setDeliverOrderInWarehouse(pos.isDeliverOrderInWarehouse());
			setPrintWarehouseDeliverDocument(pos.isPrintWarehouseDeliverDocument());
			setPrintCurrentAccountDocument(pos.isPrintCurrentAccountDocument());
			setControlCashReturns(pos.isReturnedCashInCNControl());
			setMaxCashReturnWithoutAuth(pos.getMaxReturnedCashInCN());
			
			setSearchToday(pos.isSearchToday());
			setCopyEntity(pos.isCopyEntity());
			setInitialAuthorization(pos.isInitialPOSAuthorization());
			setLockedClosed(pos.isLockedClosed());
			setPosnet(pos.getPosnet());
			setAuthorizeManualGeneralDiscount(pos.isAuthorizeGeneralManualDiscount());
			
			setVoidDocuments(pos.isVoidDocuments());
		}	
	}
	
	public PoSConfig(MPOSJournal journal) {
		this(journal.getPOS());
		setCashID(journal.getC_Cash_ID());
		posJournal = true;
	}
	
	public void validateOnline() throws PosException {
		Properties ctx = Env.getCtx();
		
		// Validación de dirección de EC para cálculos de impuestos.
		
		MBPartner bPartner = new MBPartner(ctx, getBPartnerCashTrxID(), null);
		MBPartnerLocation[] locs = bPartner.getLocations(false);
		if (locs.length == 0) {
			throw new PosException("POSCashBPNoLocationError");
		}

	}
	
	public void validateOffline() throws PosException {
		// Nada que validar aun
	}
	
	/**
	 * @return Devuelve bPartnerCashTrxID.
	 */
	public int getBPartnerCashTrxID() {
		return bPartnerCashTrxID;
	}

	/**
	 * @param partnerCashTrxID Fija o asigna bPartnerCashTrxID.
	 */
	public void setBPartnerCashTrxID(int partnerCashTrxID) {
		bPartnerCashTrxID = partnerCashTrxID;
	}

	/**
	 * @return Devuelve cashBookID.
	 */
	public int getCashBookID() {
		return cashBookID;
	}

	/**
	 * @param cashBookID Fija o asigna cashBookID.
	 */
	public void setCashBookID(int cashBookID) {
		this.cashBookID = cashBookID;
	}

	/**
	 * @return Devuelve priceListID.
	 */
	public int getPriceListID() {
		return priceListID;
	}

	/**
	 * @param priceListID Fija o asigna priceListID.
	 */
	public void setPriceListID(int priceListID) {
		if ((this.priceListID == null && priceListID != 0)
				|| (this.priceListID != priceListID)) {
			this.priceListID = priceListID;
			setPriceListWithTax("Y"
					.equals(DB
							.getSQLValueString(
									null,
									"SELECT IsTaxIncluded FROM M_PriceList WHERE M_PriceList_ID = ?",
									priceListID)));
		}
	}

	/**
	 * @return Devuelve warehouseID.
	 */
	public int getWarehouseID() {
		return warehouseID;
	}

	/**
	 * @param warehouseID Fija o asigna warehouseID.
	 */
	public void setWarehouseID(int warehouseID) {
		this.warehouseID = warehouseID;
	}

	/**
	 * @return Devuelve supervisorRoleID.
	 */
	public int getSupervisorRoleID() {
		return supervisorRoleID;
	}

	/**
	 * @param supervisorRoleID Fija o asigna supervisorRoleID.
	 */
	public void setSupervisorRoleID(int supervisorRoleID) {
		this.supervisorRoleID = supervisorRoleID;
	}

	/**
	 * @return Devuelve si hay que crear un Albarán/Remito.
	 */
	public boolean isCreateInOut() {
		return getInoutDocTypeID() != 0;
	}

	/**
	 * 
	 * @return Devuelve si hay que crear una factura
	 */
	public boolean isCreateInvoice() {
		return createInvoice;
	}
	
	/**
	 * 
	 * @return Devuelve la moneda configurada en la terminal del punto de venta.
	 */
	public int getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(int currencyID) {
		this.currencyID = currencyID;
	}

	/**
	 * @return Devuelve checkBankAccountID.
	 */
	public int getCheckBankAccountID() {
		return checkBankAccountID;
	}

	/**
	 * @param checkBankAccountID Fija o asigna checkBankAccountID.
	 */
	public void setCheckBankAccountID(int checkBankAccountID) {
		this.checkBankAccountID = checkBankAccountID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	

	/**
	 * @return Devuelve exemptTaxId.
	 */
	public int getExemptTaxId() {
		return exemptTaxId;
	}

	/**
	 * @param exemptTaxId Fija o asigna exemptTaxId.
	 */
	public void setExemptTaxId(int exemptTaxId) {
		this.exemptTaxId = exemptTaxId;
	}

	/**
	 * @return Devuelve paymentTermID.
	 */
	public int getPaymentTermID() {
		return paymentTermID;
	}

	/**
	 * @param paymentTermID Fija o asigna paymentTermID.
	 */
	public void setPaymentTermID(int paymentTermID) {
		this.paymentTermID = paymentTermID;
	}

	/**
	 * @return Devuelve printerName.
	 */
	public String getPrinterName() {
		return printerName;
	}

	/**
	 * @param printerName Fija o asigna printerName.
	 */
	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	/**
	 * @return Devuelve orderDocTypeID.
	 */
	public int getOrderDocTypeID() {
		return orderDocTypeID;
	}

	/**
	 * @param orderDocTypeID Fija o asigna orderDocTypeID.
	 */
	public void setOrderDocTypeID(int orderDocTypeID) {
		this.orderDocTypeID = orderDocTypeID;
	}

	/**
	 * @return Devuelve invoiceDocTypeID.
	 */
	public int getInvoiceDocTypeID() {
		return invoiceDocTypeID;
	}

	/**
	 * @param invoiceDocTypeID Fija o asigna invoiceDocTypeID.
	 */
	public void setInvoiceDocTypeID(int invoiceDocTypeID) {
		this.invoiceDocTypeID = invoiceDocTypeID;
	}

	/**
	 * @return Devuelve doctypeDocTypeID.
	 */
	public int getInoutDocTypeID() {
		return inoutDocTypeID;
	}

	/**
	 * @param doctypeDocTypeID Fija o asigna doctypeDocTypeID.
	 */
	public void setInoutDocTypeID(int doctypeDocTypeID) {
		this.inoutDocTypeID = doctypeDocTypeID;
	}

	/**
	 * @param isCreateInvoice The isCreateInvoice to set.
	 */
	public void setCreateInvoice(boolean createInvoice) {
		this.createInvoice = createInvoice;
	}

	/**
	 * @return Returns the posNumber.
	 */
	public int getPosNumber() {
		return posNumber;
	}

	/**
	 * @param posNumber The posNumber to set.
	 */
	public void setPosNumber(int posNumber) {
		this.posNumber = posNumber;
	}

	/**
	 * @return Returns the priceListWithTax.
	 */
	public boolean isPriceListWithTax() {
		return priceListWithTax;
	}

	/**
	 * @param priceListWithTax The priceListWithTax to set.
	 */
	private void setPriceListWithTax(boolean priceListWithTax) {
		this.priceListWithTax = priceListWithTax;
	}

	/**
	 * @return Returns the modifyPrice.
	 */
	public boolean isModifyPrice() {
		return modifyPrice;
	}

	/**
	 * @param modifyPrice The modifyPrice to set.
	 */
	public void setModifyPrice(boolean modifyPrice) {
		this.modifyPrice = modifyPrice;
	}

	/**
	 * @return Returns the currentUserID.
	 */
	public int getCurrentUserID() {
		return currentUserID;
	}

	/**
	 * @param currentUserID The currentUserID to set.
	 */
	public void setCurrentUserID(int currentUserID) {
		this.currentUserID = currentUserID;
	}

	/**
	 * @return Returns the sellWithoutStock.
	 */
	public boolean isSellWithoutStock() {
		return sellWithoutStock;
	}

	/**
	 * @param sellWithoutStock The sellWithoutStock to set.
	 */
	public void setSellWithoutStock(boolean sellWithoutStock) {
		this.sellWithoutStock = sellWithoutStock;
	}

	/**
	 * @return the userCanAccessInfoProduct
	 */
	public boolean isUserCanAccessInfoProduct() {
		return userCanAccessInfoProduct;
	}

	/**
	 * @param userCanAccessInfoProduct the userCanAccessInfoProduct to set
	 */
	public void setUserCanAccessInfoProduct(boolean userCanAccessInfoProduct) {
		this.userCanAccessInfoProduct = userCanAccessInfoProduct;
	}

	/**
	 * @return the searchByValue
	 */
	public boolean isSearchByValue() {
		return searchByValue;
	}

	/**
	 * @param searchByValue the searchByValue to set
	 */
	public void setSearchByValue(boolean searchByValue) {
		this.searchByValue = searchByValue;
	}

	/**
	 * @return the searchByValueLike
	 */
	public boolean isSearchByValueLike() {
		return searchByValueLike;
	}

	/**
	 * @param searchByValueLike the searchByValueLike to set
	 */
	public void setSearchByValueLike(boolean searchByValueLike) {
		this.searchByValueLike = searchByValueLike;
	}

	/**
	 * @return the searchByUPC
	 */
	public boolean isSearchByUPC() {
		return searchByUPC;
	}

	/**
	 * @param searchByUPC the searchByUPC to set
	 */
	public void setSearchByUPC(boolean searchByUPC) {
		this.searchByUPC = searchByUPC;
	}

	/**
	 * @return the searchByUPCLike
	 */
	public boolean isSearchByUPCLike() {
		return searchByUPCLike;
	}

	/**
	 * @param searchByUPCLike the searchByUPCLike to set
	 */
	public void setSearchByUPCLike(boolean searchByUPCLike) {
		this.searchByUPCLike = searchByUPCLike;
	}

	/**
	 * @return the searchByName
	 */
	public boolean isSearchByName() {
		return searchByName;
	}

	/**
	 * @param searchByName the searchByName to set
	 */
	public void setSearchByName(boolean searchByName) {
		this.searchByName = searchByName;
	}

	/**
	 * @return the searchByNameLike
	 */
	public boolean isSearchByNameLike() {
		return searchByNameLike;
	}

	/**
	 * @param searchByNameLike the searchByNameLike to set
	 */
	public void setSearchByNameLike(boolean searchByNameLike) {
		this.searchByNameLike = searchByNameLike;
	}

	/**
	 * @return Indica si se ha configurado la búsqueda de artículos por UPC
	 * ya sea de forma exacta y/o parcial. 
	 */
	public boolean isSearchByUPCConfigured() {
		return isSearchByUPC() || isSearchByUPCLike();
	}

	/**
	 * @return Indica si se ha configurado la búsqueda de artículos por Clave de Búsqueda
	 * ya sea de forma exacta y/o parcial. 
	 */
	public boolean isSearchByValueConfigured() {
		return isSearchByValue() || isSearchByValueLike();
	}

	/**
	 * @return Indica si se ha configurado la búsqueda de artículos por Nombre
	 * ya sea de forma exacta y/o parcial. 
	 */
	public boolean isSearchByNameConfigured() {
		return isSearchByName() || isSearchByNameLike();
	}

	/**
	 * @return the deliverOrderInWarehouse
	 */
	public boolean isDeliverOrderInWarehouse() {
		return deliverOrderInWarehouse;
	}

	/**
	 * @param deliverOrderInWarehouse the deliverOrderInWarehouse to set
	 */
	public void setDeliverOrderInWarehouse(boolean deliverOrderInWarehouse) {
		this.deliverOrderInWarehouse = deliverOrderInWarehouse;
	}

	/**
	 * @return the printWarehouseDeliverDocument
	 */
	public boolean isPrintWarehouseDeliverDocument() {
		return printWarehouseDeliverDocument;
	}

	/**
	 * @param printWarehouseDeliverDocument the printWarehouseDeliverDocument to set
	 */
	public void setPrintWarehouseDeliverDocument(
			boolean printWarehouseDeliverDocument) {
		this.printWarehouseDeliverDocument = printWarehouseDeliverDocument;
	}

	/**
	 * @return el valor de cashID
	 */
	public int getCashID() {
		return cashID;
	}

	/**
	 * @param cashID el valor de cashID a asignar
	 */
	public void setCashID(int cashID) {
		this.cashID = cashID;
	}
	
	/**
	 * @return el valor de posJournal
	 */
	public boolean isPosJournal() {
		return posJournal;
	}

	/**
	 * @return el valor de stdPrecision
	 */
	public int getStdPrecision() {
		return stdPrecision;
	}

	/**
	 * @return el valor de costingPrecision
	 */
	public int getCostingPrecision() {
		return costingPrecision;
	}

	public void setControlCashReturns(boolean controlCashReturns) {
		this.controlCashReturns = controlCashReturns;
	}

	public boolean isControlCashReturns() {
		return controlCashReturns;
	}

	public void setMaxCashReturnWithoutAuth(BigDecimal maxCashReturnWithoutAuth) {
		this.maxCashReturnWithoutAuth = maxCashReturnWithoutAuth;
	}

	public BigDecimal getMaxCashReturnWithoutAuth() {
		return maxCashReturnWithoutAuth;
	}
	
	/**
	 * @return the searchToday
	 */
	public boolean isSearchToday() {
		return searchToday;
	}

	/**
	 * @param searchToday the searchToday to set
	 */
	public void setSearchToday(boolean searchToday) {
		this.searchToday = searchToday;
	}
	
	/**
	 * @return the copyEntity
	 */
	public boolean isCopyEntity() {
		return copyEntity;
	}

	/**
	 * @param copyEntity the copyEntity to set
	 */
	public void setCopyEntity(boolean copyEntity) {
		this.copyEntity = copyEntity;
	}

	public void setInitialAuthorization(boolean initialAuthorization) {
		this.initialAuthorization = initialAuthorization;
	}

	public boolean isInitialAuthorization() {
		return initialAuthorization;
	}

	public void setLockedClosed(boolean lockedClosed) {
		this.lockedClosed = lockedClosed;
	}

	public boolean isLockedClosed() {
		return lockedClosed;
	}

	public void setPrintCurrentAccountDocument(boolean printCurrentAccountDocument) {
		this.printCurrentAccountDocument = printCurrentAccountDocument;
	}

	public boolean isPrintCurrentAccountDocument() {
		return printCurrentAccountDocument;
	}

	public void setPosnet(String posnet) {
		this.posnet = posnet;
	}

	public String getPosnet() {
		return posnet;
	}

	public void setAuthorizeManualGeneralDiscount(
			boolean authorizeManualGeneralDiscount) {
		this.authorizeManualGeneralDiscount = authorizeManualGeneralDiscount;
	}

	public boolean isAuthorizeManualGeneralDiscount() {
		return authorizeManualGeneralDiscount;
	}

	public boolean isVoidDocuments() {
		return voidDocuments;
	}

	public void setVoidDocuments(boolean voidDocuments) {
		this.voidDocuments = voidDocuments;
	}
}
