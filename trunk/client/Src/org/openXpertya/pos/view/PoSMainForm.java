package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AInfoFiscalPrinter;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.apps.SwingWorker;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.grid.VTable;
import org.openXpertya.grid.ed.VCheckBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.grid.ed.VPasswordSimple;
import org.openXpertya.images.ImageFactory;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.FiscalDocumentPrint;
import org.openXpertya.model.MCategoriaIva;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.pos.ctrl.AddPOSPaymentValidations;
import org.openXpertya.pos.ctrl.PoSConfig;
import org.openXpertya.pos.ctrl.PoSModel;
import org.openXpertya.pos.exceptions.FiscalPrintException;
import org.openXpertya.pos.exceptions.InsufficientBalanceException;
import org.openXpertya.pos.exceptions.InsufficientCreditException;
import org.openXpertya.pos.exceptions.InvalidOrderException;
import org.openXpertya.pos.exceptions.InvalidPaymentException;
import org.openXpertya.pos.exceptions.InvalidProductException;
import org.openXpertya.pos.exceptions.InvoiceCreateException;
import org.openXpertya.pos.exceptions.PosException;
import org.openXpertya.pos.exceptions.ProductAddValidationFailed;
import org.openXpertya.pos.model.AuthOperation;
import org.openXpertya.pos.model.BankTransferPayment;
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.pos.model.CashPayment;
import org.openXpertya.pos.model.CheckPayment;
import org.openXpertya.pos.model.CreditCard;
import org.openXpertya.pos.model.CreditCardMaskManager;
import org.openXpertya.pos.model.CreditCardPayment;
import org.openXpertya.pos.model.CreditNotePayment;
import org.openXpertya.pos.model.CreditPayment;
import org.openXpertya.pos.model.DiscountSchema;
import org.openXpertya.pos.model.EntidadFinanciera;
import org.openXpertya.pos.model.EntidadFinancieraPlan;
import org.openXpertya.pos.model.IPaymentMediumInfo;
import org.openXpertya.pos.model.Location;
import org.openXpertya.pos.model.Order;
import org.openXpertya.pos.model.OrderProduct;
import org.openXpertya.pos.model.Payment;
import org.openXpertya.pos.model.PaymentMedium;
import org.openXpertya.pos.model.PaymentTerm;
import org.openXpertya.pos.model.Product;
import org.openXpertya.pos.model.ProductList;
import org.openXpertya.pos.model.User;
import org.openXpertya.pos.view.table.PaymentTableModel;
import org.openXpertya.pos.view.table.ProductTableModel;
import org.openXpertya.pos.view.table.TableUtils;
import org.openXpertya.process.DocActionStatusEvent;
import org.openXpertya.process.DocActionStatusListener;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.MeasurableTask;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeStatsLogger;
import org.openXpertya.util.UserAuthConstants;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;
import org.openXpertya.utils.Disposable;
import org.openXpertya.utils.LYCloseWindowAdapter;

public class PoSMainForm extends CPanel implements FormPanel, ASyncProcess, Disposable {

	// --------------------------------------------------
	// Constantes de Tamaños de Componentes
	// --------------------------------------------------
	private final int S_MINIMIZED_WIDTH = 795;
	private final int S_MINIMIZED_HEIGHT = 710;
	private final int S_TENDERTYPE_PANEL_WIDTH = 325;
	
	private final int S_PAYMENT_FIELD_WIDTH = 176;
	private final int S_PAYMENT_INFO_FIELD_WIDTH = 140;
	
	private final int S_PAYMENT_ACTION_BUTTON_WIDTH = 140;
	private final int S_PAYMENT_ACTION_BUTTON_HEIGHT = 30;
	// --------------------------------------------------
	

	private final Date TODAY = new Date(System.currentTimeMillis());
	private final String STATUS_DB_SEPARATOR = " | ";

	private String MSG_NO_POS_CONFIG = null;
	
	private static CLogger log = CLogger.getCLogger(PoSMainForm.class);
	
	private DateFormat dateFormat;
	private NumberFormat amountFormat; 
	private NumberFormat priceFormat;
	
	private final String UPDATE_ORDER_PRODUCT_ACTION = "updOrdProdAction";
	private final String GOTO_PAYMENTS_ACTION = "gotoPaymAction";
	private final String PAY_ORDER_ACTION = "payOrdAction";	
	private final String ADD_PAYMENT_ACTION = "addPaymentAction";
	private final String SET_CUSTOMER_DATA_ACTION = "setCusDataAction";
	private final String SET_BPARTNER_INFO_ACTION = "setBPInfoAction";
	private final String GOTO_ORDER = "gotoOrder";
	private final String ADD_ORDER_ACTION = "addOrder";
	private final String MOVE_ORDER_PRODUCT_FORWARD = "moveOrderProductForward";
	private final String MOVE_ORDER_PRODUCT_BACKWARD = "moveOrderProductBackward";
	private final String CHANGE_FOCUS_PRODUCT_ORDER = "changeFocusProductOrder";
	private final String MOVE_PAYMENT_FORWARD = "movePaymentForward";
	private final String MOVE_PAYMENT_BACKWARD = "movePaymentBackward";
	private final String REMOVE_PAYMENT_ACTION = "removePaymentAction";
	private final String CHANGE_FOCUS_CUSTOMER_AMOUNT = "changeFocusCustomerAmount";
	private final String CHANGE_FOCUS_GENERAL_DISCOUNT = "changeFocusGeneralDiscount";
	private final String CANCEL_ORDER = "cancelOrder";
	private final String GOTO_INSERT_CARD = "gotoInsertCard";
	
	private PoSModel model;
	
	private FormFrame frame;
	private int windowNo = 0;
	private TableUtils orderTableUtils;
	private TableUtils paymentsTableUtils;
	
	private Map<String,KeyStroke> actionKeys;
	private PoSComponentFactory componentFactory;
	private PoSConfigDialog poSConfigDialog;
	private boolean posConfigError = false;
	private PoSMsgRepository msgRepository;
	private FocusTraversalPolicy compFocusTraversalPolicy = new ComponentsFocusTraversalPolicy();
	private FocusTraversalPolicy oldFocusTraversalPolicy;
	private Product loadedProduct = null;
	
	private StatusBar statusBar = new StatusBar();
	private CTabbedPane cPosTab = null;
	private CPanel cOrderPanel = null;
	private CPanel cOrderTopPanel = null;
	private CPanel cProductInPanel = null;
	private CPanel cPaymentPanel = null;
	private CPanel cTotalPanel = null;
	private CLabel cProductCodeLabel = null;
	private CTextField cProductCodeText = null;
	private CLabel cCountLabel = null;
	private CLabel cProductNameLabel = null;
	private CLabel cProductNameDetailLabel = null;
	private CLabel cTotalAmountLabel = null;
	private CPanel cOrderCenterPanel = null;
	private CScrollPane cOrderTableScrollPane = null;
	private JTable cOrderTable = null;
	private CPanel cCommandPanel = null;
	private CPanel cCommandInfoPanel = null;
	private CPanel cPayCommandPanel = null;
	private CButton cPayButton = null;
	private CPanel cCountPanel = null;
	private CTextField cCountText = null;
	private CButton cIncCountButton = null;
	private CButton cDecCountButton = null;
	private CPanel cPaymentTopPanel = null;
	private CPanel cClientPanel = null;
	private CLabel cClientNameLabel = null;
	private VLookup cClientText = null;
	private CLabel cClientLocationLabel = null;
	private CComboBox cClientLocationCombo = null;
	private CLabel cClientTaxId = null;
	private CTextField cTaxIdText = null;
	private CPanel cPaymentCenterPanel = null;
	private CPanel cClientBorderPanel = null;
	private CPanel cTenderTypes = null;
	private CPanel cAddTenderTypePanel = null;
	private CPanel cTenderTypeGridPanel = null;
	private CPanel cSelectTenderTypePanel = null;
	private CLabel cTenderTypeLabel = null;
	private CComboBox cTenderTypeCombo = null;
	private CLabel cAmountLabel = null;
	private CPanel cSelectTenderTypeContentPanel = null;
	private CPanel cCreditCardParamsPanel = null;
	private CLabel cCreditCardLabel = null;
	private CComboBox cCreditCardCombo = null;
	private CLabel cCreditCardNumberLabel = null;
	private CTextField cCreditCardNumberText = null;
	private CLabel cCouponNumberLabel = null;
	private CTextField cCouponNumberText = null;
	private CLabel cPosnetLabel = null;
	private CTextField cPosnetText = null;
	private CPanel cCheckParamsPanel = null;
	private CLabel cBankLabel = null;
	private VLookup cBankCombo = null;
	private CLabel cCheckNumberLabel = null;
	private CTextField cCheckNumberText = null;
	private CLabel cCheckEmissionDateLabel = null;
	private VDate cCheckEmissionDate = null;
	private CLabel cCheckAcctDateLabel = null;
	private VDate cCheckAcctDate = null;
	private VNumber cAmountText = null;
	private CPanel cCashParamsPanel = null;
	private CLabel cConvertedAmountLabel = null;
	private VNumber cConvertedAmountText = null;
	private CPanel cCreditParamsPanel = null;
	private CLabel cPaymentTermLabel = null;
	private CComboBox cPaymentTermCombo = null;
	private CPanel cCurrencyPanel = null;
	private CLabel cCurrencyLabel = null;
	private VLookup cCurrencyCombo = null;
	private CButton cAddTenderTypeButton = null;
	private CPanel cPaymentBottomPanel = null;
	private CPanel cPaymentFinishPanel = null;
	private CPanel cPaymentTotalBorderPanel = null;
	private CPanel cPaymentTotalPanel = null;
	private CLabel cToPayLabel = null;
	private VNumber cToPayText = null;
	private CLabel cPaidLabel = null;
	private VNumber cPaidText = null;
	private CLabel cBalanceLabel = null;
	private VNumber cBalanceText = null;
	private CLabel cChangeLabel = null;
	private VNumber cChangeText = null;
	private CPanel cPaymentCommandPanel = null;
	private CButton cFinishPayButton = null;
	private CScrollPane cTenderTypesTableScrollPane = null;
	private VTable cPaymentsTable = null;
	private CButton cRemovePaymentButton = null;
	private CPanel cTenderTypesTablePanel = null;
	private CLabel cSelectedTenderTypesLabel = null;
	private CPanel cTenderTypeParamsContentPanel = null;
	private CLabel cAddTenderTypeLabel = null;
	private CPanel cProductCodePanel;
	private VLookup cProductLookup;

	private CPanel cLoadOrderPanel = null;
	private CLabel cLoadOrderLabel = null;
	private VLookup cOrderLookup = null;
	private CLabel cOrderCustomerLabel = null;
	private CLabel cOrderDateLabel = null;
	private CTextField cOrderDateText = null;
	private CTextField cOrderCustomerText = null;
	private CLabel cCustomerDescriptionLabel = null;
	private CTextField cCustomerDescriptionText = null;
	private CButton cCustomerDataButton = null;
	private CPanel cBuyerPanel = null;
	private CButton cAddOrderButton = null;
	private CComboBox cPaymentMediumCombo = null;
	private CLabel cPaymentMediumLabel = null;
	private CComboBox cCreditCardPlanCombo = null;
	private CLabel cCreditCardPlanLabel = null;
	private CLabel cCardLabel = null;
	private VPasswordSimple cCardText = null;
	private JSeparator cCardSeparator = null;
	private VLookup cBankAccountCombo = null;
	private CLabel cBankAccountLabel = null;
	private CTextField cCheckBankText = null;
	private CLabel cCheckCUITLabel = null;
	private CTextField cCheckCUITText = null;
	private CLabel cCreditNoteLabel = null;
	private VLookup cCreditNoteSearch = null;
	private CLabel cCreditNoteAvailableLabel = null;
	private VNumber cCreditNoteAvailableText = null;
	private CLabel cCreditNoteBalanceLabel = null;
	private VNumber cCreditNoteBalanceText = null;
	private CLabel cCreditNoteCashReturnLabel = null;
	private VCheckBox cCreditNoteCashReturnCheck = null;
	private CLabel cCreditNoteCashReturnAmtLabel = null;
	private VNumber cCreditNoteCashReturnAmtText = null;
	private CPanel cCreditNoteParamsPanel = null;
	private CPanel cTransferParamsPanel = null;
	private CLabel cTransferNumberLabel = null;
	private CTextField cTransferNumberText = null;
	private VDate cTransferDate = null;
	private CLabel cTransferDateLabel = null;
	private CLabel cTransferAccountLabel = null;
	private CPanel cPaymentMediumInfoPanel = null;
	private CLabel cPaymentDiscountLabel = null;
	private CTextField cPaymentDiscountText = null;
	private CLabel cPaymentDiscountAmountLabel = null;
	private VNumber cPaymentDiscountAmount = null;
	private CLabel cPaymentToPayAmtLabel = null;
	private VNumber cPaymentToPayAmt = null;
	private CPanel cCreditCardInfoPanel = null;
	private CLabel cCreditCardCuotasLabel = null;
	private VNumber cCreditCardCuotas = null;
	private CLabel cCreditCardCuotaAmtLabel = null;
	private VNumber cCreditCardCuotaAmt = null;
	private CLabel cBPartnerDiscountLabel = null;
	private CTextField cBPartnerDiscountText = null;
	private CLabel cOrderTotalLabel = null;
	private VNumber cOrderTotalAmt = null;
	private CLabel cDocumentDiscountLabel = null;
	private VNumber cDocumentDiscountAmt = null;
	private CLabel cGeneralDiscountLabel = null;
	private VNumber cGeneralDiscountPercText = null;
	
	
//	private AUserAuth cCashRetunAuthPanel = null;
	private AuthorizationDialog authDialog = null;
	private AuthOperation manualDiscountAuthOperation = null;
	private LYCloseWindowAdapter closeWindowAdapter = null;
	private AddPOSPaymentValidations extraPOSPaymentAddValidations = null;
	
	private String MSG_ORDER;
	private String MSG_PAYMENT;
	private String MSG_CODE;
	private String MSG_TOTAL;
	private String MSG_PRODUCT_INCOME;
	private String MSG_PRODUCT_NOT_FOUND;
	private String MSG_COUNT;
	private String MSG_PRODUCT;
	private String MSG_UNIT_PRICE;
	private String MSG_UPDATE_PRODUCT;
	private String MSG_PAY;
	private String MSG_LOCATION;
	private String MSG_CLIENT;
	private String MSG_ADD_PAYMENT;
	private String MSG_AMOUNT;
	private String MSG_TYPE;
	private String MSG_CHECK;
	private String MSG_CREDIT;
	private String MSG_CREDIT_BALANCE;
	private String MSG_CASH_RETURNING;
	private String MSG_AMT_TO_RETURN;
	private String MSG_CREDIT_CARD;
	private String MSG_CASH;
	private String MSG_COUPON_NUMBER;
	private String MSG_CARD_NUMBER;
	private String MSG_ACCT_DATE;
	private String MSG_EMISSION_DATE;
	private String MSG_CHECK_NUMBER;
	private String MSG_BANK;
	private String MSG_CONVERTED_AMOUNT;
	private String MSG_CURRENCY;
	private String MSG_ADD;
	private String MSG_CHANGE;
	private String MSG_BALANCE;
	private String MSG_PAID;
	private String MSG_TO_PAY;
	private String MSG_DELETE;
	private String MSG_PAYMENTS_SELECTED;
	private String MSG_NO_PRODUCT_ERROR;
	private String MSG_NO_BPARTNER_LOCATION_ERROR;
	private String MSG_NO_AMOUNT_ERROR;
	private String MSG_INVALID_PAYMENT_AMOUNT_ERROR;
	private String MSG_NO_CURRENCY_CONVERT_ERROR;
	private String MSG_INVALID_CREDIT_AMOUNT_ERROR;
	private String MSG_RETURN_CASH_AMOUNT_ERROR;
	private String MSG_RETURN_CASH_POSITIVE_AMOUNT_ERROR;
	private String MSG_CREDIT_NOTE_REPEATED_ERROR;
	private String MSG_INVALID_CARD_AMOUNT_ERROR;
	private String MSG_NO_BPARTNER_ERROR;
	private String MSG_NO_LOCATION_ERROR;
	private String MSG_POS_ORDER_STATUS;
	private String MSG_POS_PAYMENT_STATUS;
	private String MSG_INSUFFICIENT_CREDIT_ERROR;
	private String MSG_BALANCE_ERROR;
	private String MSG_INVALID_PAYMENT_ERROR;
	private String MSG_FATAL_ERROR;
	private String MSG_TAXRATE;
	private String MSG_TAXID;
	private String MSG_NO_PRICE_LIST_FOR_PRODUCT_ERROR;
	private String MSG_INVALID_ORDER;
	private String MSG_LOAD_CUSTOMER_ORDER;
	private String MSG_CUSTOMER;
	private String MSG_DATE;
	private String MSG_INVOICE_CREATE_ERROR;
	private String MSG_CANT_CREATE_TICKET_ERROR;
	private String MSG_POS_CONFIG_ERROR;
	private String MSG_QTY;
	private String MSG_CUSTOMER_DESCRIPTION;
	private String MSG_CUSTOMER_IDENTIFICATION;
	private String MSG_PRICE_LIST;
	private String MSG_CHECKOUT_IN;
	private String MSG_PAYMENT_MEDIUM;
	private String MSG_NO_PAYMENT_MEDIUM_ERROR;
	private String MSG_CREDIT_CARD_PLAN;
	private String MSG_NO_CREDIT_CARD_PLAN_ERROR;
	private String MSG_BANK_ACCOUNT;
	private String MSG_NO_BANK_ACCOUNT_ERROR;
	private String MSG_CHECK_CUIT;
	private String MSG_FILL_MANDATORY;
	private String MSG_INVALID_CHECK_ACCTDATE;
	private String MSG_AVAILABLE_AMT;
	private String MSG_AMOUNT_GREATHER_THAN_AVAILABLE;
	private String MSG_TRANSFER_NUMBER;
	private String MSG_NOT_NEED_PAYMENTS_ERROR;
	private String MSG_PAYMENT_AMT_SURPLUS_ERROR;
	private String MSG_TRANSFER;
	private String MSG_DISCOUNT;
	private String MSG_PAYMENT_TOPAY_AMOUNT;
	private String MSG_CUOTAS;
	private String MSG_CUOTA_AMOUNT;
	private String MSG_DISCOUNT_SHORT;
	private String MSG_NONE;
	private String MSG_PAYMENTS_SUMMARY;
	private String MSG_PRICE;
	private String MSG_FINAL_PRICE;
	private String MSG_CHANGE_PRODUCT_ORDER;
	private String MSG_PAYMENT_TERM;
	private String MSG_NO_PAYMENTTERM;
	private String MSG_NO_POSJOURNAL;
	private String MSG_RETRY_VOID_INVOICE;
	private String MSG_RETRY_VOID_INVOICE_INFO;
	private String MSG_RETRY_VOID_INVOICE_INFO_POS_JOURNAL;
	private String MSG_VOID_INVOICE_OK;
	private String MSG_SUPERVISOR_AUTH;
	private String MSG_DISCOUNT_GENERAL_SHORT;
	private String MSG_DISCOUNT_GENERAL;
	private String MSG_CONFIRM_CANCEL_ORDER;
	private String MSG_CANCEL_ORDER;
	private String MSG_NEXT_INVOICE_DOCUMENTNO;
	private String MSG_NO_BEFORE_CHECK_DEADLINES;
	private String MSG_CHECK_DEADLINE_REQUIRED;
	private String MSG_INSERT_CARD;
	private String MSG_CLOSE_POS_ORDERLINES;
	private String MSG_POSNET;
	private String MSG_NO_AUTHORIZATION;
		
	/**
	 * This method initializes 
	 * 
	 */
	public PoSMainForm() {
		super();
		this.model = new PoSModel();
		this.model.setProcessListener(this);
		this.msgRepository = PoSMsgRepository.getInstance();
		setAuthDialog(new AuthorizationDialog(this));
		setExtraPOSPaymentAddValidations(new AddPOSPaymentValidations());
		initMsgs();
	}

	public void init(int WindowNo, FormFrame frame) {
		setWindowNo(WindowNo);
		setFrame(frame);
		getFrame().setSize(S_MINIMIZED_WIDTH, S_MINIMIZED_HEIGHT);
		getFrame().setPreferredSize(new Dimension(S_MINIMIZED_WIDTH, S_MINIMIZED_HEIGHT));
		try {
			getFrame().getContentPane().add(this,BorderLayout.CENTER);
			getFrame().getContentPane().add(getStatusBar(),BorderLayout.SOUTH);
			this.revalidate();
			this.repaint();
			setComponentFactory(new OnlinePoSComponentFactory(getWindowNo(), getModel()));
			dateFormat = getComponentFactory().getDateFormat(DisplayType.Date);
			amountFormat = getComponentFactory().getNumberFormat(DisplayType.Amount);
			priceFormat = getComponentFactory().getNumberFormat(DisplayType.CostPrice);
			
			initPoSConfig();
			if(!isPosConfigError()) {
				getFrame().setMaximize(true);
				reloadPoSConfig();
				initialize();
				initBusinessLogic();
			// Si hubo un error al obtener la configuracion del TPV se
			// se cierra la ventana.	
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						getFrame().setVisible(false);
					}
					
				});
			}
			
			// Necesario hacerlo aquí porque se requiere el windowsNo para que
			// se comporte correctamente el foco
			createInfoFiscalPrinter();
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "PoSMainForm.init", e);
		}
		Env.setContext(Env.getCtx(), getWindowNo(), "IsSOTrx", "Y");
		
		TimeStatsLogger.endTask(MeasurableTask.POS_INIT);
	}
	
	private void initPoSConfig() {
		List<PoSConfig> posConfigs = getModel().getPoSConfigs();
		int posConfigCount = posConfigs.size();
		PoSConfig poSConfig = null;
		// No existe configuracion de TPV
		if(posConfigCount == 0) {
			log.severe("No existe una configuración de TPV para el usuario.");
			String msg;
			if (getModel().isPOSJournalActivated()) {
				msg = MSG_NO_POSJOURNAL;
			} else {
				msg = MSG_NO_POS_CONFIG;
			}
			errorMsg(msg);
			setPosConfigError(true);
			return;

		// Existe mas de una configuracion de TPV
		} else if(posConfigCount > 1) {
			poSConfigDialog = new PoSConfigDialog(getComponentFactory(), getMsgRepository());
			poSConfigDialog.setModal(true);
			AEnv.positionCenterScreen(poSConfigDialog);
			poSConfigDialog.setVisible(true);
			if(poSConfigDialog.isCanceled()) {
				log.fine("Seleccion de configuracion de TPV cancelada.");
				setPosConfigError(true);
				return;
			}

			poSConfig = poSConfigDialog.getPoSConfig();
			log.finer("Configuracion de TPV seleccionada: " + poSConfig);

		// Existe solo una configuracion de TPV.
		} else if(posConfigCount == 1) {
			poSConfig = posConfigs.get(0);
			log.finer("Configuracion de TPV: " + poSConfig);
		}
		getModel().setPoSConfig(poSConfig);
		
		try {
			// Verificar si se debe autorizar el inicio del TPV
			initialAuthorization();
			
			// Se valida la configuración del TPV.
			getModel().validatePoSConfig();
		
		} catch (PosException e) {
			setPosConfigError(true);
			errorMsg(MSG_POS_CONFIG_ERROR, getMsg(e.getMessage()));
		}		
	}
	
	private void reloadPoSConfig(){
		getModel().reloadPoSConfig(getWindowNo());
		updateStatusDB();
	}
	
	public void updateStatusDB(){
		StringBuffer status = new StringBuffer();
		String documentNo = getModel().getNextInvoiceDocumentNo(); 
		if(!Util.isEmpty(documentNo, true)){
			// Siguiente nro de factura
			status.append(MSG_NEXT_INVOICE_DOCUMENTNO+" : "+documentNo);
			// Separador
			status.append(STATUS_DB_SEPARATOR);
		}
		// Tarifa
		status.append(MSG_PRICE_LIST+" : "+getModel().getPriceList().getName());
		getStatusBar().setStatusDB(status.toString());
	}
	
	
	public void initialAuthorization() throws PosException{
		if(getModel().getPoSConfig().isInitialAuthorization()){
			AuthOperation authOperation = new AuthOperation(
					UserAuthConstants.POS_INIT_UID,
					getMsgRepository()
							.getMsg(UserAuthConstants
									.getProcessValue(UserAuthConstants.POS_INIT_UID)),
					UserAuthConstants.POS_INIT_MOMENT);
			getAuthDialog().addAuthOperation(authOperation);
			getAuthDialog().authorizeOperation(UserAuthConstants.POS_INIT_MOMENT);
			CallResult result = getAuthDialog().getAuthorizeResult(true);
			if(result == null){
				throw new PosException(getMsg("NoInitialAuthorization"));
			}
			if(result.isError()){
				if(!Util.isEmpty(result.getMsg(), true)){
					throw new PosException(result.getMsg());
				}
				throw new PosException(getMsg("NoInitialAuthorization"));
			}
		}
	}
	
	private void initBusinessLogic() {
		loadBPartner(getModel().getDefaultBPartner());
		selectTenderType(MPOSPaymentMedium.TENDERTYPE_Cash);
	}
	
	private void initMsgs() {
		MSG_NO_POS_CONFIG = getMsg("NoPOSConfig");
		MSG_ORDER = getMsg("C_Order_ID");
		MSG_PAYMENT = getMsg("CustomerPayment");
		MSG_CODE = getMsg("Product");
		MSG_PRODUCT_INCOME = getMsg("ProductIncome");
		MSG_TOTAL = getMsg("Total");
		MSG_PRODUCT_NOT_FOUND = getMsg("ProductNotFound");
		MSG_COUNT = getMsg("POSProductQty");
		MSG_PRODUCT = getMsg("Description");
		MSG_UNIT_PRICE = getMsg("UnitPrice");
		MSG_UPDATE_PRODUCT = getMsg("UpdatePOSProduct");
		MSG_PAY = getMsg("Receive");
		MSG_LOCATION = getMsg("Address");
		MSG_CLIENT = getMsg("Customer");
		MSG_ADD_PAYMENT = getMsg("AddCustomerPayment");
		MSG_AMOUNT = getMsg("Amt");
		MSG_TYPE = getMsg("Type");
		MSG_CHECK = getMsg("Check");
		MSG_CREDIT = getMsg("Credit");
		MSG_CREDIT_BALANCE = getMsg("CreditBalance");
		MSG_CASH_RETURNING = getMsg("CashReturning");
		MSG_AMT_TO_RETURN = getMsg("AmtToReturn");
		MSG_CREDIT_CARD = getMsg("CreditCard");
		MSG_CASH = getMsg("Cash");
		MSG_COUPON_NUMBER = getMsg("CouponNumber");
		MSG_CARD_NUMBER = getMsg("CreditCardNumber");
		MSG_ACCT_DATE = getMsg("AcctDate");
		MSG_EMISSION_DATE = getMsg("EmissionDate");
		MSG_CHECK_NUMBER = getMsg("CheckNo");
		MSG_BANK = getMsg("C_Bank_ID");
		MSG_CONVERTED_AMOUNT = getMsg("ConvertedAmount");
		MSG_CURRENCY = getMsg("C_Currency_ID");
		MSG_ADD = getMsg("Add");
		MSG_CHANGE = getMsg("Change");
		MSG_BALANCE = getMsg("Balance");
		MSG_PAID = getMsg("Received");
		MSG_TO_PAY = getMsg("ToReceive");
		MSG_DELETE = getMsg("POSDelete");
		MSG_TAXID = getMsg("TaxID");
		MSG_PAYMENTS_SELECTED = getMsg("CustomerPaymentsSelected");
		MSG_NO_PRODUCT_ERROR = getMsg("NoProductsError");
		MSG_NO_BPARTNER_LOCATION_ERROR = getMsg("NoBPartnerLocationError");
		MSG_NO_AMOUNT_ERROR = getMsg("NoAmountError");
		MSG_INVALID_PAYMENT_AMOUNT_ERROR = getMsg("InvalidPaymentAmountError");
		MSG_NO_CURRENCY_CONVERT_ERROR = getMsg("NoCurrencyConvertError");
		MSG_INVALID_CREDIT_AMOUNT_ERROR = getMsg("InvalidCreditAmountError");
		MSG_RETURN_CASH_AMOUNT_ERROR = getMsg("CashReturnedExceedsCNBalanceAmt");
		MSG_RETURN_CASH_POSITIVE_AMOUNT_ERROR = getMsg("CashReturnedMustBeHigherToZero");
		MSG_CREDIT_NOTE_REPEATED_ERROR = getMsg("CreditNoteRepeated");
		MSG_INVALID_CARD_AMOUNT_ERROR = getMsg("InvalidCardAmountError");
		MSG_NO_BPARTNER_ERROR = getMsg("NoBPartnerError");
		MSG_NO_LOCATION_ERROR = getMsg("NoLocationError");
		MSG_POS_ORDER_STATUS = getMsg("POSOrderStatus");
		MSG_POS_PAYMENT_STATUS = getMsg("POSPaymentStatus");
		MSG_INSUFFICIENT_CREDIT_ERROR = getMsg("InsufficientCreditError");
		MSG_BALANCE_ERROR = getMsg("BalanceError");
		MSG_INVALID_PAYMENT_ERROR = getMsg("InvalidPaymentError");
		MSG_FATAL_ERROR = getMsg("POSFatalError");
		MSG_NO_PRICE_LIST_FOR_PRODUCT_ERROR = getMsg("NoPriceListForProductError");
		MSG_TAXRATE = getMsg("Rate");
		MSG_INVALID_ORDER = getMsg("InvalidOrderError");
		MSG_LOAD_CUSTOMER_ORDER = getMsg("LoadCustomerOrder");
		MSG_CUSTOMER = getMsg("Customer");
		MSG_DATE = getMsg("Date");
		MSG_INVOICE_CREATE_ERROR = getMsg("POSInvoiceCreateError");
		MSG_CANT_CREATE_TICKET_ERROR = getMsg("CantCreatePOSTicket");
		MSG_POS_CONFIG_ERROR = getMsg("POSConfigError");
		MSG_QTY = getMsg("Qty");
		MSG_CUSTOMER_DESCRIPTION = getMsg("Buyer");
		MSG_CUSTOMER_IDENTIFICATION = getMsg("CustomerIdentification");
		MSG_PRICE_LIST = getMsg("M_PriceList_ID");
		MSG_CHECKOUT_IN = getMsg("CheckoutIn");
		MSG_PAYMENT_MEDIUM = getMsg("ReceiptMedium");
		MSG_NO_PAYMENT_MEDIUM_ERROR = getMsg("NoPaymentMediumError");
		MSG_CREDIT_CARD_PLAN = getMsg("CreditCardPlan");
		MSG_NO_CREDIT_CARD_PLAN_ERROR = getMsg("NoCreditCardPlanError");
		MSG_BANK_ACCOUNT = getMsg("C_BankAccount_ID");
		MSG_NO_BANK_ACCOUNT_ERROR = getMsg("NoBankAccountError");
		MSG_CHECK_CUIT = getMsg("CUITLibrador");
		MSG_FILL_MANDATORY = getMsg("FillMandatory");
		MSG_INVALID_CHECK_ACCTDATE = getMsg("InvalidCheckDueDateError");
		MSG_AVAILABLE_AMT = getMsg("OpenAmt");
		MSG_AMOUNT_GREATHER_THAN_AVAILABLE = getMsg("AmountGreatherThanAvalaibleError");
		MSG_TRANSFER_NUMBER = getMsg("TransferNumber");
		MSG_NOT_NEED_PAYMENTS_ERROR = getMsg("NotNeedPaymentsError");
		MSG_PAYMENT_AMT_SURPLUS_ERROR = getMsg("PaymentAmtSurplusError");
		MSG_TRANSFER = getMsg("Transfer");
		MSG_DISCOUNT = getMsg("DiscountCharge");
		MSG_PAYMENT_TOPAY_AMOUNT = getMsg("DiscountedChargedToPayAmt");
		MSG_CUOTAS = getMsg("CuotasCount");
		MSG_CUOTA_AMOUNT = getMsg("CuotaAmt");
		MSG_DISCOUNT_SHORT = getMsg("DiscountChargeShort");
		MSG_NONE = getMsg("None");
		MSG_PAYMENTS_SUMMARY = getMsg("ReceiptsSummary");
		MSG_PRICE = getMsg("Price");
		MSG_FINAL_PRICE = getMsg("FinalPrice");
		MSG_CHANGE_PRODUCT_ORDER = getMsg("ChangeProductOrder");
		MSG_PAYMENT_TERM = getMsg("C_PaymentTerm_ID");
		MSG_NO_PAYMENTTERM = getMsg("NoPaymentTerm");
		MSG_NO_POSJOURNAL = getMsg("POSTerminalPOSJournalRequired");
		MSG_RETRY_VOID_INVOICE = getMsg("RetryVoidInvoice");
		MSG_RETRY_VOID_INVOICE_INFO = getMsg("RetryVoidInvoiceInfo");
		MSG_RETRY_VOID_INVOICE_INFO_POS_JOURNAL = getMsg("RetryVoidInvoiceInfoPosJournal");
		MSG_VOID_INVOICE_OK = getMsg("InvoiceVoidOK");
		MSG_SUPERVISOR_AUTH = getMsg("SupervisorAuth");
		MSG_DISCOUNT_GENERAL_SHORT = getMsg("GeneralDiscountChargeShort");
		MSG_DISCOUNT_GENERAL = getMsg("GeneralDiscountCharge");
		MSG_CONFIRM_CANCEL_ORDER = getMsg("ConfirmCancelOrder");
		MSG_CANCEL_ORDER = getMsg("POSCancelOrder");
		MSG_NEXT_INVOICE_DOCUMENTNO = getMsg("NextInvoiceDocumentNoShort");
		MSG_NO_BEFORE_CHECK_DEADLINES = getMsg("NoBeforeCheckDeadLines");
		MSG_CHECK_DEADLINE_REQUIRED = getMsg("DeletingPaymentCheckDeadLineRequired");
		MSG_INSERT_CARD = getMsg("InsertCard");
		MSG_CLOSE_POS_ORDERLINES = getMsg("POSNoCloseWithOrderLines");
		MSG_POSNET = getMsg("Posnet");
		MSG_NO_AUTHORIZATION = getMsg("NoAuthorization"); 
		
		// Estos mensajes no se asignan a variables de instancias dado que son mensajes
		// devueltos por el modelo del TPV, pero se realiza la invocación a getMsg(...) para
		// obtenerlos desde la BD y que queden cacheados en el Repositorio de mensajes.
		// De esta forma se preserva la lógica para Modo Offline de TPV, la cual tiene como
		// propósito cargar en memoria todos los mensajes a utilizar.
		getMsg("InsufficientStockError");
		getMsg("MustSetAttrSetInstance");
		getMsg("POSMasiMandatoryError");
		getMsg("WarehouseCheckoutProductNotAllowed");
		getMsg("CantAddProduct");
		getMsg("POSCheckoutProductNotAllowed");
		getMsg("POSProductMustBeSold");
		getMsg("POSCustomerOrderAlreadyAdded");
		getMsg("PriceUnderZero");
	}

	public void dispose() {
		getFrame().dispose();
//		if (getFrame() != null) {
//			getFrame().setVisible(false);
//			//getFrame().dispose();
//		}
		setFrame(null);
		updateCloseApp("Y");
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		keyBindingsInit();
		this.setLayout(new BorderLayout());
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
        this.add(getCPosTab(), java.awt.BorderLayout.CENTER);
        oldFocusTraversalPolicy = getFrame().getFocusTraversalPolicy();
        getFrame().setFocusTraversalPolicy(compFocusTraversalPolicy);
        getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		updateCloseApp("Y");
		closeWindowAdapter = new LYCloseWindowAdapter(this, true);
		LYCloseWindowAdapter.setMsg(MSG_CLOSE_POS_ORDERLINES);
		getFrame().addWindowListener(closeWindowAdapter);
		setManualDiscountAuthOperation(new AuthOperation(
				UserAuthConstants.POS_MANUAL_GENERAL_DISCOUNT_UID,
				MSG_DISCOUNT_GENERAL, UserAuthConstants.POS_FINISH_MOMENT));
		getManualDiscountAuthOperation().setLazyAuthorization(true);
	}
	
	private void keyBindingsInit() {
		// Deshabilito el F10 que algunos look and feel y 
		// técnicas de focos asignan al primer componente menú de la barra de menú 
		getFrame().getJMenuBar().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
              KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10,0), "none");
		// Se asignan las teclas shorcut de las acciones.
		setActionKeys(new HashMap<String,KeyStroke>());
		getActionKeys().put(UPDATE_ORDER_PRODUCT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		getActionKeys().put(GOTO_PAYMENTS_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		getActionKeys().put(PAY_ORDER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		getActionKeys().put(ADD_PAYMENT_ACTION,KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
		getActionKeys().put(SET_CUSTOMER_DATA_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		getActionKeys().put(SET_BPARTNER_INFO_ACTION,KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		getActionKeys().put(GOTO_ORDER,KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		getActionKeys().put(ADD_ORDER_ACTION,KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		getActionKeys().put(MOVE_ORDER_PRODUCT_FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		getActionKeys().put(MOVE_ORDER_PRODUCT_BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		getActionKeys().put(CHANGE_FOCUS_PRODUCT_ORDER, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		getActionKeys().put(MOVE_PAYMENT_FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		getActionKeys().put(MOVE_PAYMENT_BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		getActionKeys().put(REMOVE_PAYMENT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		getActionKeys().put(CHANGE_FOCUS_CUSTOMER_AMOUNT, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		getActionKeys().put(CHANGE_FOCUS_GENERAL_DISCOUNT,KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		getActionKeys().put(CANCEL_ORDER,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		getActionKeys().put(GOTO_INSERT_CARD, KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		
		// Accion: Abrir el dialogo para modificar el producto del pedido.
        getActionMap().put(UPDATE_ORDER_PRODUCT_ACTION,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					openUpdateOrderProductDialog();
				}
        	}
        );

		// Accion: completar el pago de la orden.
        getActionMap().put(PAY_ORDER_ACTION,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					if(getCFinishPayButton().isEnabled())
						completeOrder();
				}
        	}
        );

        // Accion: Cambiar a la pestaña de seleccion de medios de pago.
        getActionMap().put(GOTO_PAYMENTS_ACTION,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					if(getCPayButton().isEnabled())
						goToPayments();
				}
        	}
        );
        
		// Accion: Agregar el pago con el importe ingresado.
       getActionMap().put(ADD_PAYMENT_ACTION,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					if(getCAmountText().getValue() != null)
						addPayment();
				}
        	}
        );

		// Accion: Abrir el diálogo para ingresar/editar los datos del comprador.
       getActionMap().put(SET_CUSTOMER_DATA_ACTION,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
        			openCustomerDataDialog();
        		}
        	}
        );
       
		// Accion: Abrir el diálogo para editar los datos del cliente, la tarifa, etc.       
       getActionMap().put(SET_BPARTNER_INFO_ACTION, 
    		   new AbstractAction() {
   					public void actionPerformed(ActionEvent e) {
   						openBPInfoDialog();
   					}
   				}
       	);
       
       // Accion: Ir a la pestaña de Pedido desde la pestaña Cobro.
       getActionMap().put(GOTO_ORDER,
    		   new AbstractAction() {
    	   			public void actionPerformed(ActionEvent e) {
    	   				getCPosTab().setSelectedIndex(0);
    	   			}
       			}
       );

       // Accion: Agregar el pedido de cliente precargado al pedido del TPV
       getActionMap().put(ADD_ORDER_ACTION,
    		   new AbstractAction() {
    	   			public void actionPerformed(ActionEvent e) {
    	   				addCustomerOrder();
    	   			}
       			}
       );
       
       // Accion: Mover hacia adelante la selección de la grilla del pedido
		getActionMap().put(MOVE_ORDER_PRODUCT_FORWARD, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				moveTableSelection(getCOrderTable(), true);
			}
		});

       // Accion: Mover hacia atras la selección de la grilla del pedido
		getActionMap().put(MOVE_ORDER_PRODUCT_BACKWARD, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				moveTableSelection(getCOrderTable(), false);
			}
		});
		
	    // Accion: Cambiar el foco para ingreso de Artículo o Pedido.
		getActionMap().put(CHANGE_FOCUS_PRODUCT_ORDER, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (getCProductCodeText().hasFocus() || getCCountText().hasFocus()) {
					getCOrderLookup().requestFocus();
				} else {
					getCProductCodeText().requestFocus();
				}
			} 
		});
		
	    // Accion: Mover hacia adelante la selección de la grilla de pagos
		getActionMap().put(MOVE_PAYMENT_FORWARD, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				moveTableSelection(getCPaymentsTable(), true);
			}
		});

       // Accion: Mover hacia atras la selección de la grilla de pagos
		getActionMap().put(MOVE_PAYMENT_BACKWARD, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				moveTableSelection(getCPaymentsTable(), false);
			}
		});
		
		// Accion: Quitar el pago seleccionado de la grilla.
		getActionMap().put(REMOVE_PAYMENT_ACTION,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					removePayment();
				}
        	}
        );
		
	    // Accion: Cambiar el foco para ingreso de EC o Importe.
		getActionMap().put(CHANGE_FOCUS_CUSTOMER_AMOUNT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!getCClientText().hasFocus()) {
					getCClientText().requestFocus();
				} else {
					getCAmountText().requestFocus(); 
				}
			}
		});
		
		// Accion: Cambiar el foco para ingreso de Descuento/Recargo general
		getActionMap().put(CHANGE_FOCUS_GENERAL_DISCOUNT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				getCGeneralDiscountPercText().requestFocus();
			}
		});
		
		// Accion: Cancelar el pedido, eliminar todas las líneas
		getActionMap().put(CANCEL_ORDER, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancelOrder();
			}
		});
		
		// Accion: Cambiar el foco para ingreso de tarjeta de crédito
		getActionMap().put(GOTO_INSERT_CARD, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				getCCardText().requestFocus();
			}
		});
		
		// Acciones habilitadas al inicio.
		setActionEnabled(GOTO_INSERT_CARD, false);
		setActionEnabled(UPDATE_ORDER_PRODUCT_ACTION,true);
		setActionEnabled(GOTO_PAYMENTS_ACTION,true);
		setActionEnabled(PAY_ORDER_ACTION,false);
		setActionEnabled(ADD_PAYMENT_ACTION,false);
		setActionEnabled(SET_CUSTOMER_DATA_ACTION,false);
		setActionEnabled(SET_BPARTNER_INFO_ACTION,true);
		setActionEnabled(CHANGE_FOCUS_CUSTOMER_AMOUNT, false);
		setActionEnabled(GOTO_ORDER,false);
		setActionEnabled(ADD_ORDER_ACTION,true);
		setActionEnabled(MOVE_PAYMENT_FORWARD, false);
		setActionEnabled(MOVE_PAYMENT_BACKWARD, false);
		setActionEnabled(MOVE_ORDER_PRODUCT_FORWARD, true);
		setActionEnabled(MOVE_ORDER_PRODUCT_BACKWARD, true);
		setActionEnabled(CHANGE_FOCUS_PRODUCT_ORDER, true);
		setActionEnabled(CHANGE_FOCUS_GENERAL_DISCOUNT, false);
		setActionEnabled(CANCEL_ORDER, true);
	}

	private void setActionEnabled(String action, boolean enabled) {
		String kAction = (enabled?action:"none");
        KeyStroke keyStroke = getActionKeys().get(action);
        
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            	keyStroke, kAction);
	}
	
	/**
	 * This method initializes cPosTab	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private CTabbedPane getCPosTab() {
		if (cPosTab == null) {
			cPosTab = new CTabbedPane();
			cPosTab.setPreferredSize(new java.awt.Dimension(750,580));
			cPosTab.addTab(MSG_ORDER, null, getCOrderPanel(), null);
			cPosTab.addTab(MSG_PAYMENT, null, getCPaymentPanel(), null);

			cPosTab.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent event) {
					selectTab(getCPosTab().getSelectedIndex());
				}
			});

			cPosTab.setEnabledAt(1,false);
			selectTab(0);
		}
		return cPosTab;
	}

	/**
	 * This method initializes cTabPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCOrderPanel() {
		if (cOrderPanel == null) {
			cOrderPanel = new CPanel();
			cOrderPanel.setLayout(new BorderLayout());
			cOrderPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			cOrderPanel.add(getCOrderTopPanel(), java.awt.BorderLayout.NORTH);
			cOrderPanel.add(getCOrderCenterPanel(), java.awt.BorderLayout.CENTER);
		}
		return cOrderPanel;
	}

	/**
	 * This method initializes cProductInPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCOrderTopPanel() {
		if (cOrderTopPanel == null) {
			cOrderTopPanel = new CPanel();
			cOrderTopPanel.setPreferredSize(new java.awt.Dimension(0,90));
			cOrderTopPanel.setLayout(new BoxLayout(getCOrderTopPanel(), BoxLayout.X_AXIS));
			cOrderTopPanel.add(getCProductInPanel(), null);
			cOrderTopPanel.add(getCTotalPanel(), null);
		}
		return cOrderTopPanel;
	}

	/**
	 * This method initializes cProductInPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private CPanel getCProductInPanel() {
		if (cProductInPanel == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 3;
			gridBagConstraints22.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints22.gridy = 0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.gridwidth = 3;
			gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints21.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints21.gridy = 1;
			cProductNameDetailLabel = new CLabel();
			cProductNameDetailLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints11.gridy = 1;
			cProductNameLabel = new CLabel();
			cProductNameLabel.setText(MSG_PRODUCT);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints2.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints2.gridy = 0;
			cCountLabel = new CLabel();
			cCountLabel.setText(MSG_QTY);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints.gridy = 0;
			cProductCodeLabel = new CLabel();
			cProductCodeLabel.setText(MSG_CODE);
			cProductInPanel = new CPanel();
			cProductInPanel.setLayout(new GridBagLayout());
			cProductInPanel.setName("");
			cProductInPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1), MSG_PRODUCT_INCOME, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12), new java.awt.Color(51,51,51)), javax.swing.BorderFactory.createEmptyBorder(0,10,5,10)));
			cProductInPanel.add(cProductCodeLabel, gridBagConstraints);
			//cProductInPanel.add(getCProductCodeText(), gridBagConstraints1);
			cProductInPanel.add(getCProductCodePanel(), gridBagConstraints1);
			cProductInPanel.add(cCountLabel, gridBagConstraints2);
			cProductInPanel.add(cProductNameLabel, gridBagConstraints11);
			cProductInPanel.add(cProductNameDetailLabel, gridBagConstraints21);
			cProductInPanel.add(getCCountPanel(), gridBagConstraints22);
		}
		return cProductInPanel;
	}
	
	/**
	 * This method initializes cPaymentPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private CPanel getCPaymentPanel() {
		if (cPaymentPanel == null) {
			cPaymentPanel = new CPanel();
			cPaymentPanel.setLayout(new BorderLayout());
			cPaymentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			cPaymentPanel.add(getCPaymentTopPanel(), java.awt.BorderLayout.NORTH);
			cPaymentPanel.add(getCPaymentCenterPanel(), java.awt.BorderLayout.CENTER);
			cPaymentPanel.add(getCPaymentBottomPanel(), java.awt.BorderLayout.SOUTH);
		}
		return cPaymentPanel;
	}

	/**
	 * This method initializes cTotalPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCTotalPanel() {
		if (cTotalPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 0;
			cTotalAmountLabel = new CLabel();
			cTotalAmountLabel.setText("0.00");
			cTotalAmountLabel.setFontBold(true);
			cTotalAmountLabel.setFont(new Font(null, Font.BOLD, 20));
			cTotalPanel = new CPanel();
			cTotalPanel.setLayout(new GridBagLayout());
			cTotalPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(0,3,0,0), javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1), MSG_TOTAL, javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12), new java.awt.Color(51,51,51))));
			cTotalPanel.setPreferredSize(new java.awt.Dimension(180,100));
			cTotalPanel.setMaximumSize(new java.awt.Dimension(180,100));
			cTotalPanel.add(cTotalAmountLabel, gridBagConstraints4);
		}
		return cTotalPanel;
	}

	/**
	 * This method initializes cProductCodeText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCProductCodeText() {
		if (cProductCodeText == null) {
			cProductCodeText = new CTextField();
			cProductCodeText.setPreferredSize(new java.awt.Dimension(200,20));
			cProductCodeText.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String code = getCProductCodeText().getText().trim();
					searchProduct(code);
				}
			});
			FocusUtils.addFocusHighlight(cProductCodeText);
		}
		return cProductCodeText;
	}
	
	/**
	 * This method initializes cProductCodePanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCProductCodePanel() {
		if (cProductCodePanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new java.awt.Insets(5,0,5,0);
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 0;
			cProductCodePanel = new CPanel();
			cProductCodePanel.setLayout(new GridBagLayout());
			cProductCodePanel.add(getCProductCodeText(), gridBagConstraints1);
			cProductCodePanel.add(getCProductLookup(), gridBagConstraints2);
		}
		return cProductCodePanel;
	}
	
	private VLookup getCProductLookup() {
		if(cProductLookup == null) {
			cProductLookup = getComponentFactory().createProductSearch();
			((VPoSLookup)cProductLookup).setCustom(true);
			cProductLookup.setPreferredSize(new java.awt.Dimension(22,20));
			cProductLookup.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					reloadPoSConfig();
					if (e.getSource() instanceof CButton) {
						String code = getCProductCodeText().getText();
						if (code != null && code.length() > 0)
							searchProduct(code);
						else
							((VPoSLookup)cProductLookup).openInfoProduct();
					}
				}
				
			});
			cProductLookup.addVetoableChangeListener(new VetoableChangeListener() {

				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					Object value = event.getNewValue();
					if(value != null) {
						TimeStatsLogger.beginTask(MeasurableTask.POS_LOAD_PRODUCT_FROM_INFOPRODUCT);
						
						int productId = ((Integer)value).intValue();
						int masiId = ((VPoSLookup)cProductLookup).getAttributeSetInstanceID();
						Product product = getModel().getProduct(productId, masiId);
						if(product == null)
							errorMsg(MSG_NO_PRICE_LIST_FOR_PRODUCT_ERROR);
						else {
							setLoadedProduct(product);
							getCProductCodeText().setValue(product.getCode());
							getCProductNameDetailLabel().setText(product.getDescription());
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									getCCountText().requestFocus();
								}
							});
							getCCountText().requestFocus();
						}
						TimeStatsLogger.endTask(MeasurableTask.POS_LOAD_PRODUCT_FROM_INFOPRODUCT);
					} else {
						getCProductCodeText().setValue(null);
						getCProductCodeText().requestFocus();
					}
					
				}
				
			});
			cProductLookup.getM_text().addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					getCProductCodeText().requestFocus();
				}
			});
			// Solo se muestra el botón del lookup si el perfil logueado tiene permisos para
			// ver el buscado de artículos. 
			cProductLookup.setVisible(getModel().isUserCanAccessInfoProduct());
		}	
		return cProductLookup;
	}

	/**
	 * This method initializes cOrderCenterPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCOrderCenterPanel() {
		if (cOrderCenterPanel == null) {
			cOrderCenterPanel = new CPanel();
			cOrderCenterPanel.setLayout(new BoxLayout(getCOrderCenterPanel(), BoxLayout.X_AXIS));
			cOrderCenterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,0,0,0));
			cOrderCenterPanel.add(getCOrderTableScrollPane(), null);
			cOrderCenterPanel.add(getCCommandPanel(), null);
		}
		return cOrderCenterPanel;
	}

	/**
	 * This method initializes cOrderTableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getCOrderTableScrollPane() {
		if (cOrderTableScrollPane == null) {
			cOrderTableScrollPane = new CScrollPane();
			cOrderTableScrollPane.setViewportView(getCOrderTable());
		}
		return cOrderTableScrollPane;
	}

	/**
	 * This method initializes cOrderTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getCOrderTable() {
		if (cOrderTable == null) {
			cOrderTable = new MiniTable();
			cOrderTable.setRowSelectionAllowed(true);
			
			// Creo el Modelo de la tabla.
			ProductTableModel orderTableModel = new ProductTableModel();
			// Se vincula la lista de productos en la orden con el table model
			// para que se muestren en la tabla.
			orderTableModel.setOrderProducts(getOrder().getOrderProducts());
			orderTableModel.addColumName(MSG_COUNT);
			orderTableModel.addColumName(MSG_PRODUCT);
			orderTableModel.addColumName(MSG_TAXRATE);
			orderTableModel.addColumName(MSG_UNIT_PRICE);
			orderTableModel.addColumName(MSG_PRICE);
			orderTableModel.addColumName(MSG_FINAL_PRICE);
			orderTableModel.addColumName(MSG_CHECKOUT_IN);
			cOrderTable.setModel(orderTableModel);
			
			// Configuracion del renderizado de la tabla.

			// Renderer de cantidad.
			cOrderTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
				
				public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
					JLabel cmp = (JLabel)super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
					cmp.setHorizontalAlignment(JLabel.CENTER);
					return cmp;
				}
				
			});
			
			// Renderer de importes por defecto.
			cOrderTable.setDefaultRenderer(BigDecimal.class, getNumberCellRendered(amountFormat));
			cOrderTable.setFocusable(false);

			// Renderer de tasa de impuesto.
			cOrderTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {

				public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
					JLabel cmp = (JLabel)super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
					NumberFormat format = NumberFormat.getNumberInstance();
					format.setMaximumFractionDigits(1);
					format.setMinimumFractionDigits(1);
					String number = format.format(Double.parseDouble(cmp.getText())) + "%";
					cmp.setText(number);
					cmp.setHorizontalAlignment(JLabel.CENTER);
					return cmp;
				}
				
			});

			// Renderer de precio unitario
			cOrderTable.getColumnModel().getColumn(3).setCellRenderer(getNumberCellRendered(priceFormat));
			
			// Renderer de precio de línea sin descuentos
			cOrderTable.getColumnModel().getColumn(4).setCellRenderer(getNumberCellRendered(amountFormat));

			// Renderer de Precio Final de línea con descuentos
			cOrderTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
					JLabel cmp = (JLabel)super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
					//BigDecimal amount = scaleAmount(((BigDecimal)arg1));
					BigDecimal amount = ((BigDecimal)arg1);
					cmp.setText(amountFormat.format(amount));
					cmp.setHorizontalAlignment(JLabel.RIGHT);
					Font f = cmp.getFont();
					cmp.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
					return cmp;
				};
			});

			// Renderer de Lugar de Retiro.
			cOrderTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {

				public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
					JLabel cmp = (JLabel)super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
					cmp.setHorizontalAlignment(JLabel.CENTER);
					return cmp;
				}
				
			});
			
			// Funcionalidades extras.
			ArrayList minWidth = new ArrayList();
			minWidth.add(35);
			minWidth.add(0);
			minWidth.add(60);
			minWidth.add(75);
			minWidth.add(75);
			minWidth.add(75);
			minWidth.add(75);
			
			// Se wrapea la tabla con funcionalidad extra.
			setOrderTableUtils(new TableUtils(minWidth,cOrderTable));
			getOrderTableUtils().autoResizeTable();
			getOrderTableUtils().removeSorting();
		}
		return cOrderTable;
	}

	/**
	 * This method initializes cCommandPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCommandPanel() {
		if (cCommandPanel == null) {
			cCommandPanel = new CPanel();
			cCommandPanel.setLayout(new BoxLayout(getCCommandPanel(), BoxLayout.Y_AXIS));
			cCommandPanel.setMaximumSize(new java.awt.Dimension(180,400));
			cCommandPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,5,5,0));
			cCommandPanel.setPreferredSize(new java.awt.Dimension(180,0));
			cCommandPanel.setMaximumSize(new java.awt.Dimension(180,600));
			cCommandPanel.add(getCLoadOrderPanel(), null);
			cCommandPanel.add(getCCommandInfoPanel(), null);
			cCommandPanel.add(getCPayCommandPanel(), null);
		}
		return cCommandPanel;
	}

	/**
	 * This method initializes cCommandInfoPanel1	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCommandInfoPanel() {
		if (cCommandInfoPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.weighty = 0.0D;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			JLabel cCmdUpdateOrderProductLabel = new CLabel();
			cCmdUpdateOrderProductLabel.setText(KeyUtils.getKeyStr(getActionKeys().get(UPDATE_ORDER_PRODUCT_ACTION)) + " = " + MSG_UPDATE_PRODUCT);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.weightx = 0.0D;
			gridBagConstraints1.weighty = 0.0D;
			gridBagConstraints1.gridy = 0;
			JLabel cCmdGotoPaymentsLabel = new CLabel();
			cCmdGotoPaymentsLabel.setText(KeyUtils.getKeyStr(getActionKeys().get(GOTO_PAYMENTS_ACTION)) + " = " + MSG_PAY);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.weighty = 0.0D;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			JLabel cCmdSetBpartnerInfoLabel = new CLabel();
			cCmdSetBpartnerInfoLabel.setText(KeyUtils.getKeyStr(getActionKeys().get(SET_BPARTNER_INFO_ACTION)) + " = " + MSG_CUSTOMER +"/"+MSG_PRICE_LIST);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.weightx = 0.0D;
			gridBagConstraints4.weighty = 0.0D;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			JLabel cCmdChangeProductOrderLabel = new CLabel();
			cCmdChangeProductOrderLabel.setText(KeyUtils.getKeyStr(getActionKeys().get(CHANGE_FOCUS_PRODUCT_ORDER)) + " = " + MSG_CHANGE_PRODUCT_ORDER);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.weightx = 0.0D;
			gridBagConstraints5.weighty = 0.0D;
			gridBagConstraints5.gridy = 4;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			JLabel cCmdCancelOrderInfoLabel = new CLabel();
			cCmdCancelOrderInfoLabel.setText(KeyUtils.getKeyStr(getActionKeys().get(CANCEL_ORDER)) + " = " + MSG_CANCEL_ORDER);
			
			
			cCommandInfoPanel = new CPanel();
			cCommandInfoPanel.setLayout(new GridBagLayout());
			cCommandInfoPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
			cCommandInfoPanel.setPreferredSize(new java.awt.Dimension(180,500));
			cCommandInfoPanel.setName("cCommandInfoPanel");
			cCommandInfoPanel.add(cCmdChangeProductOrderLabel, gridBagConstraints1);
			cCommandInfoPanel.add(cCmdUpdateOrderProductLabel, gridBagConstraints2);
			cCommandInfoPanel.add(cCmdSetBpartnerInfoLabel, gridBagConstraints3);
			cCommandInfoPanel.add(cCmdGotoPaymentsLabel, gridBagConstraints4);
			cCommandInfoPanel.add(cCmdCancelOrderInfoLabel, gridBagConstraints5);
		}
		return cCommandInfoPanel;
	}

	/**
	 * This method initializes cPayCommandPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPayCommandPanel() {
		if (cPayCommandPanel == null) {
			cPayCommandPanel = new CPanel();
			cPayCommandPanel.setName("cPayCommandPanel");
			cPayCommandPanel.add(getCPayButton(), null);
		}
		return cPayCommandPanel;
	}

	/**
	 * This method initializes cPayButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCPayButton() {
		if (cPayButton == null) {
			cPayButton = new CButton();
			cPayButton.setIcon(getImageIcon("Caunt24.gif"));
			cPayButton.setText(MSG_PAY + " " + KeyUtils.getKeyStr(getActionKeys().get(GOTO_PAYMENTS_ACTION)));
			cPayButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					goToPayments();
				}
			});
			KeyUtils.setDefaultKey(cPayButton);
			FocusUtils.addFocusHighlight(cPayButton);
		}
		return cPayButton;
	}

	/**
	 * This method initializes cCountPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCountPanel() {
		if (cCountPanel == null) {
			cCountPanel = new CPanel();
			cCountPanel.setLayout(new BoxLayout(getCCountPanel(), BoxLayout.X_AXIS));
			cCountPanel.add(getCCountText(), null);
			cCountPanel.add(getCIncCountButton(), null);
			cCountPanel.add(getCDecCountButton(), null);
		}
		return cCountPanel;
	}

	/**
	 * This method initializes cCountText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCountText() {
		if (cCountText == null) {
			cCountText = new CTextField();
			cCountText.setText("1");
			cCountText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char keyChar = e.getKeyChar();
					String countStr = cCountText.getText();
  
					// Si es punto y ya existe uno dentro del string, entonces consumo
					if(keyChar == '.' && countStr.indexOf('.') > -1){
						e.consume();
					}					
					if(!Character.isDigit(keyChar) && keyChar != '.') {
						e.consume();
					}
					if((!Character.isDigit(e.getKeyChar()) && countStr.length() == 0)) {
						e.consume();
						cCountText.setText("1");
						cCountText.selectAll();
					}
				}

				@Override
				public void keyReleased(KeyEvent event) {
					String countStr = cCountText.getText();
					if(countStr.equals("0")) {
						cCountText.setText("1");
						cCountText.selectAll();
					}
				}
				
				
			});
			cCountText.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent event) {
					cCountText.selectAll();
				}

				public void focusLost(FocusEvent event) {
					
				}
			});
			cCountText.setPreferredSize(new java.awt.Dimension(50,20));
			cCountText.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getLoadedProduct() != null) {
						addOrderProduct(getLoadedProduct());
					} else {
						getCProductCodeText().requestFocus();
					}
				}
			});
			FocusUtils.addFocusHighlight(cCountText);
		}
		return cCountText;
	}

	/**
	 * This method initializes cIncCountButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private CButton getCIncCountButton() {
		if (cIncCountButton == null) {
			cIncCountButton = new CButton();
			//cIncCountButton.setText("+");
			cIncCountButton.setIcon(getImageIcon("Plus16.gif"));
			cIncCountButton.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			cIncCountButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					String countStr = cCountText.getText();
					int count;
					if(countStr.equals("")) 
						count = 1;
					else {
						count = Integer.parseInt(countStr);
						count++;
					}
					cCountText.setText(String.valueOf(count));
				}
			});
			cIncCountButton.setPreferredSize(new java.awt.Dimension(41,20));
		}
		return cIncCountButton;
	}

	/**
	 * This method initializes cDecCountButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private CButton getCDecCountButton() {
		if (cDecCountButton == null) {
			cDecCountButton = new CButton();
			cDecCountButton.setPreferredSize(new java.awt.Dimension(41,20));
			cDecCountButton.setIcon(getImageIcon("Minus16.gif"));
			cDecCountButton.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					String countStr = cCountText.getText();
					int count;
					if(countStr.equals("")) 
						count = 1;
					else {
						count = Integer.parseInt(countStr);
						if(count > 1)
							count--;
					}
					cCountText.setText(String.valueOf(count));
				}
			});
		}
		return cDecCountButton;
	}

	/**
	 * This method initializes cPaymentTopPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentTopPanel() {
		if (cPaymentTopPanel == null) {
			cPaymentTopPanel = new CPanel();
			cPaymentTopPanel.setLayout(new BoxLayout(getCPaymentTopPanel(), BoxLayout.X_AXIS));
			cPaymentTopPanel.add(getCClientBorderPanel(), null);
		}
		return cPaymentTopPanel;
	}

	/**
	 * This method initializes cClientPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCClientPanel() {
		if (cClientPanel == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridy = 0;
			gridBagConstraints17.weightx = 1.0;
			gridBagConstraints17.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints17.gridwidth = 1;
			gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints17.gridx = 3;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints16.gridwidth = 1;
			gridBagConstraints16.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints16.gridx = 2;
			cBPartnerDiscountLabel = new CLabel();
			cBPartnerDiscountLabel.setText(MSG_DISCOUNT_SHORT);
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			//gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridy = 2;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints15.gridwidth = 1;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints15.gridx = 4;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridy = 2;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints14.gridwidth = 3; //3
			gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints13.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints13.gridy = 2;
			cCustomerDescriptionLabel = new CLabel();
			cCustomerDescriptionLabel.setText(MSG_CUSTOMER_DESCRIPTION);
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.gridx = 3;
			//gridBagConstraints12.gridwidth = 2; //no value
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints10.gridy = 1;
			cClientTaxId = new CLabel();
			cClientTaxId.setText(MSG_TAXID);
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			cClientLocationLabel = new CLabel();
			cClientLocationLabel.setText(MSG_LOCATION);
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints7.gridwidth = 1;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 0;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridy = 0;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints18.gridwidth = 1;
			gridBagConstraints18.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints18.gridx = 4;
			cGeneralDiscountLabel = new CLabel();
			cGeneralDiscountLabel.setText("%"
					+ " "
					+ MSG_DISCOUNT_GENERAL_SHORT
					+ " "
					+ KeyUtils.getKeyStr(getActionKeys().get(
							CHANGE_FOCUS_GENERAL_DISCOUNT)));
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridy = 0;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints19.gridwidth = 1;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints19.gridx = 5;
			cClientNameLabel = new CLabel();
			cClientNameLabel.setText(MSG_CLIENT + " " + KeyUtils.getKeyStr(getActionKeys().get(CHANGE_FOCUS_CUSTOMER_AMOUNT)));
			cClientPanel = new CPanel();
			cClientPanel.setLayout(new GridBagLayout());
			cClientPanel.add(cClientNameLabel, gridBagConstraints3);
			cClientPanel.add(getCClientText(), gridBagConstraints7);
			cClientPanel.add(cBPartnerDiscountLabel, gridBagConstraints16);
			cClientPanel.add(getCBPartnerDiscountText(), gridBagConstraints17);
			cClientPanel.add(cGeneralDiscountLabel, gridBagConstraints18);
			cClientPanel.add(getCGeneralDiscountPercText(), gridBagConstraints19);
			cClientPanel.add(cClientLocationLabel, gridBagConstraints8);
			cClientPanel.add(getCClientLocationCombo(), gridBagConstraints9);
			cClientPanel.add(cClientTaxId, gridBagConstraints10);
			cClientPanel.add(getCTaxIdText(), gridBagConstraints12);
			cClientPanel.add(cCustomerDescriptionLabel, gridBagConstraints13);
			//cClientPanel.add(getCCustomerDescriptionText(), gridBagConstraints14);
			cClientPanel.add(getCBuyerPanel(), gridBagConstraints14);
			//cClientPanel.add(getCCustomerDataButton(), gridBagConstraints15);
		}
		return cClientPanel;
	}
	
	/**
	 * This method initializes cBuyerPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCBuyerPanel() {
		if (cBuyerPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new Insets(0, 5, 0 ,0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 0;
			cBuyerPanel = new CPanel();
			cBuyerPanel.setLayout(new GridBagLayout());
			cBuyerPanel.add(getCCustomerDescriptionText(), gridBagConstraints);
			cBuyerPanel.add(getCCustomerDataButton(), gridBagConstraints1);
		}
		return cBuyerPanel;
	}
	
	/**
	 * This method initializes cOkButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCCustomerDataButton() {
		if (cCustomerDataButton == null) {
			cCustomerDataButton = new CButton();
			cCustomerDataButton.setText("Datos " + KeyUtils.getKeyStr(getActionKeys().get(SET_CUSTOMER_DATA_ACTION)));
			cCustomerDataButton.setIcon(getImageIcon("BPartner10.gif"));
			cCustomerDataButton.setToolTipText(getMsg(MSG_CUSTOMER_IDENTIFICATION) + " " + KeyUtils.getKeyStr(getActionKeys().get(SET_CUSTOMER_DATA_ACTION)));
			cCustomerDataButton.setPreferredSize(new java.awt.Dimension(125,21));
			cCustomerDataButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openCustomerDataDialog();
				}
			});
			FocusUtils.addFocusHighlight(cCustomerDataButton);
			KeyUtils.setDefaultKey(cCustomerDataButton);
		}
		return cCustomerDataButton;
	}


	/**
	 * This method initializes cClientText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VLookup getCClientText() {
		if (cClientText == null) {
			cClientText = getComponentFactory().createBPartnerSearch();
			cClientText.setPreferredSize(new java.awt.Dimension(250,20));
			cClientText.addVetoableChangeListener(new VetoableChangeListener() {

				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					String pName = event.getPropertyName();
					Object pValue = event.getNewValue();
					// El valor del componente cambio.
					if(pName == "C_BPartner_ID") {
						if(pValue == null) {
							getOrder().setBusinessPartner(null);
							getCClientLocationCombo().removeAllItems();
						} else {
							TimeStatsLogger.beginTask(MeasurableTask.POS_LOAD_BPARTNER);
							int bPartnerID = ((Integer)pValue).intValue();
							loadBPartner(bPartnerID);
						}
						refreshPaymentMediumInfo();
						TimeStatsLogger.endTask(MeasurableTask.POS_LOAD_BPARTNER);
					}
				}
			});
			FocusUtils.addFocusHighlight(cClientText);
		}
		return cClientText;
	}

	/**
	 * This method initializes cClientText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCBPartnerDiscountText() {
		if (cBPartnerDiscountText == null) {
			cBPartnerDiscountText = new CTextField();
			cBPartnerDiscountText.setPreferredSize(new java.awt.Dimension(150,20));
			cBPartnerDiscountText.setReadWrite(false);
		}
		return cBPartnerDiscountText;
	}

	/**
	 * This method initializes cClientLocationCombo	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private CComboBox getCClientLocationCombo() {
		if (cClientLocationCombo == null) {
			cClientLocationCombo = new CComboBox();
			cClientLocationCombo.setPreferredSize(new java.awt.Dimension(250,20));
			cClientLocationCombo.setMandatory(true);
			cClientLocationCombo.addActionListener(new java.awt.event.ActionListener() {
				
				public void actionPerformed(java.awt.event.ActionEvent e) {
					BusinessPartner bPartner = getOrder().getBusinessPartner();
					if(bPartner != null) {
						Location location  = (Location)getCClientLocationCombo().getSelectedItem();
						if(location != null) {
							bPartner.setLocation(location);
							recalculateOrderTotal();
							setCustomerDataDescriptionText();
						}
						
					}
				}
			});
			cClientLocationCombo.setBackground(true);
			FocusUtils.addFocusHighlight(cClientLocationCombo);
		}
		return cClientLocationCombo;
	}

	/**
	 * This method initializes cTaxIdText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCTaxIdText() {
		if (cTaxIdText == null) {
			cTaxIdText = new CTextField();
			cTaxIdText.setPreferredSize(new java.awt.Dimension(150,20));
			cTaxIdText.setReadWrite(false);
		}
		return cTaxIdText;
	}

	/**
	 * This method initializes cCustomerDescriptionText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCustomerDescriptionText() {
		if (cCustomerDescriptionText == null) {
			cCustomerDescriptionText = new CTextField();
			cCustomerDescriptionText.setPreferredSize(new java.awt.Dimension(150,20));
			cCustomerDescriptionText.setReadWrite(false);
		}
		return cCustomerDescriptionText;
	}
	
	
	protected VNumber getCGeneralDiscountPercText(){
		if (cGeneralDiscountPercText == null) {
			cGeneralDiscountPercText = new VNumber();
			cGeneralDiscountPercText.setDisplayType(DisplayType.Number);
			cGeneralDiscountPercText.setPreferredSize(new java.awt.Dimension(70,20));
			cGeneralDiscountPercText.setValue(0);
			cGeneralDiscountPercText.setMandatory(true);
			cGeneralDiscountPercText.addVetoableChangeListener(new VetoableChangeListener() {

				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					// Agregar o Actualizar el descuento manual general por el valor del porcentaje del monto
					BigDecimal percentage = (BigDecimal) event
									.getNewValue() == null ? BigDecimal.ZERO
									: (BigDecimal) event.getNewValue();
					getOrder().updateManualGeneralDiscount(percentage);
					// Actualizar autorización de descuento manual general
					updateManualDiscountAuthorization(percentage);
					// Actualizar descuentos
					getOrder().updateDiscounts();
					// Actualiza la tabla de pagos
					updatePaymentsTable();
					// Refrescar los medios de pago
					refreshPaymentMediumInfo();
					// Actualizar el estado de la factura
					updatePaymentsStatus();
				}
				
			});
//			cGeneralDiscountPercText.addAction("updateAmount", KeyStroke.getKeyStroke(
//					KeyEvent.VK_ENTER, 0), new AbstractAction() {
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					if (getCCreditNoteCashReturnAmtText().getValue() == null) {
//						getCCreditNoteCashReturnAmtText().setValue(getCCreditNoteBalanceText().getValue());
//					}
//				}
//			});
			FocusUtils.addFocusHighlight(cGeneralDiscountPercText);
		}
		return cGeneralDiscountPercText;
	}

	/**
	 * This method initializes cPaymentCenterPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentCenterPanel() {
		if (cPaymentCenterPanel == null) {
			cPaymentCenterPanel = new CPanel();
			//cPaymentCenterPanel.setLayout(new BoxLayout(getCPaymentCenterPanel(), BoxLayout.X_AXIS));
			cPaymentCenterPanel.setLayout(new BorderLayout());
			cPaymentCenterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,0,0,0));
			cPaymentCenterPanel.add(getCTenderTypes(), BorderLayout.CENTER);
		}
		return cPaymentCenterPanel;
	}

	/**
	 * This method initializes cClientFitPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCClientBorderPanel() {
		if (cClientBorderPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setHgap(0);
			flowLayout.setVgap(0);
			cClientBorderPanel = new CPanel();
			cClientBorderPanel.setPreferredSize(new java.awt.Dimension(558,115));
			cClientBorderPanel.setLayout(flowLayout);
			cClientBorderPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1), MSG_CLIENT, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12), new java.awt.Color(51,51,51)), javax.swing.BorderFactory.createEmptyBorder(0,10,0,10)));
			cClientBorderPanel.add(getCClientPanel(), null);
		}
		return cClientBorderPanel;
	}

	/**
	 * This method initializes cTenderTypes	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCTenderTypes() {
		if (cTenderTypes == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weightx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.anchor = GridBagConstraints.CENTER;
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weightx = 1;
			cTenderTypes = new CPanel();
			cTenderTypes.setLayout(new GridBagLayout());
			cTenderTypes.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
			cTenderTypes.add(getCAddTenderTypePanel(), gridBagConstraints1);
			cTenderTypes.add(getCTenderTypeGridPanel(), gridBagConstraints2);
		}
		return cTenderTypes;
	}

	/**
	 * This method initializes cSelectTenderTypePanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCAddTenderTypePanel() {
		if (cAddTenderTypePanel == null) {
			GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
			gridBagConstraints49.gridx = 0;
			gridBagConstraints49.insets = new java.awt.Insets(0,0,10,0);
			gridBagConstraints49.gridy = 0;
			gridBagConstraints49.gridwidth = 2;
			cAddTenderTypeLabel = new CLabel();
			cAddTenderTypeLabel.setText(MSG_ADD_PAYMENT);
			cAddTenderTypeLabel.setFontBold(true);
			GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
			gridBagConstraints50.gridx = 0;
			gridBagConstraints50.insets = new java.awt.Insets(5,0,0,0);
			gridBagConstraints50.gridy = 2;
			gridBagConstraints50.gridwidth = 2;
			GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
			gridBagConstraints48.gridy = 1;
			gridBagConstraints48.gridx = 0;
			gridBagConstraints48.anchor = GridBagConstraints.NORTH;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridy = 1;
			gridBagConstraints51.gridx = 1;
			gridBagConstraints51.anchor = GridBagConstraints.NORTH;

			cAddTenderTypePanel = new CPanel();
			cAddTenderTypePanel.setLayout(new GridBagLayout());
			cAddTenderTypePanel.add(cAddTenderTypeLabel, gridBagConstraints49);
			cAddTenderTypePanel.add(getCSelectTenderTypeContentPanel(), gridBagConstraints48);
			cAddTenderTypePanel.add(getCPaymentMediumInfoPanel(), gridBagConstraints51);
			cAddTenderTypePanel.add(getCAddTenderTypeButton(), gridBagConstraints50);
			
		}
		return cAddTenderTypePanel;
	}
	
	/**
	 * This method initializes cPaymentMediumInfoPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentMediumInfoPanel() {
		if (cPaymentMediumInfoPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(3, 0, 0, 0);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(9, 0, 0, 0);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(9, 0, 0, 0);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(10, 0, 4, 0);

			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 4;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.insets = new Insets(0, 0, 4, 0);
			
			cPaymentDiscountLabel = new CLabel();
			cPaymentDiscountLabel.setText(MSG_DISCOUNT);
			cPaymentToPayAmtLabel = new CLabel();
			cPaymentToPayAmtLabel.setText(MSG_PAYMENT_TOPAY_AMOUNT);
			cPaymentMediumInfoPanel = new CPanel();
			cPaymentMediumInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,6,0,0), BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(0,5,5,5))));
			cPaymentMediumInfoPanel.setLayout(new GridBagLayout());
			cPaymentMediumInfoPanel.add(cPaymentDiscountLabel, gridBagConstraints1);
			cPaymentMediumInfoPanel.add(getCPaymentDiscountText(), gridBagConstraints2);
			cPaymentMediumInfoPanel.add(cPaymentToPayAmtLabel, gridBagConstraints3);
			cPaymentMediumInfoPanel.add(getCPaymentToPayAmt(), gridBagConstraints4);
			cPaymentMediumInfoPanel.add(getCCreditCardInfoPanel(), gridBagConstraints5);
		}
		return cPaymentMediumInfoPanel;
	}

	/**
	 * This method initializes cCreditCardInfoPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCreditCardInfoPanel() {
		if (cCreditCardInfoPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(7, 0, 0, 0);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(9, 0, 0, 0);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.insets = new Insets(9, 0, 0, 0);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(10, 0, 0, 0);

			cCreditCardCuotasLabel = new CLabel();
			cCreditCardCuotasLabel.setText(MSG_CUOTAS);
			cCreditCardCuotaAmtLabel = new CLabel();
			cCreditCardCuotaAmtLabel.setText(MSG_CUOTA_AMOUNT);
			cCreditCardInfoPanel = new CPanel();
			//cCreditCardInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,5,0,0), BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(0,5,5,5))));
			cCreditCardInfoPanel.setLayout(new GridBagLayout());
			cCreditCardInfoPanel.add(cCreditCardCuotasLabel, gridBagConstraints1);
			cCreditCardInfoPanel.add(getCCreditCardCuotas(), gridBagConstraints2);
			cCreditCardInfoPanel.add(cCreditCardCuotaAmtLabel, gridBagConstraints3);
			cCreditCardInfoPanel.add(getCCreditCardCuotaAmt(), gridBagConstraints4);
		}
		return cCreditCardInfoPanel;
	}

	
	/**
	 * This method initializes cPaymentDiscountText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCPaymentDiscountText() {
		if (cPaymentDiscountText == null) {
			cPaymentDiscountText = new CTextField();
			cPaymentDiscountText.setReadWrite(false);
			cPaymentDiscountText.setPreferredSize(new Dimension(S_PAYMENT_FIELD_WIDTH,20));
		}
		return cPaymentDiscountText;
	}

	/**
	 * This method initializes cPaymentToPayAmt	
	 * 	
	 * @return org.openXpertya.grid.ed.VNumber	
	 */
	private VNumber getCPaymentToPayAmt() {
		if (cPaymentToPayAmt == null) {
			cPaymentToPayAmt = new VNumber();
			cPaymentToPayAmt.setDisplayType(DisplayType.Amount);
			cPaymentToPayAmt.setReadWrite(false);
			cPaymentToPayAmt.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cPaymentToPayAmt.setValue(null);
		}
		return cPaymentToPayAmt;
	}
	
	/**
	 * This method initializes cCreditCardCuotas	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCCreditCardCuotas() {
		if (cCreditCardCuotas == null) {
			cCreditCardCuotas = new VNumber();
			cCreditCardCuotas.setReadWrite(false);
			cCreditCardCuotas.setPreferredSize(new Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCreditCardCuotas.setDisplayType(DisplayType.Integer);
		}
		return cCreditCardCuotas;
	}

	/**
	 * This method initializes cCreditCardCuotaAmt	
	 * 	
	 * @return org.openXpertya.grid.ed.VNumber	
	 */
	private VNumber getCCreditCardCuotaAmt() {
		if (cCreditCardCuotaAmt == null) {
			cCreditCardCuotaAmt = new VNumber();
			cCreditCardCuotaAmt.setDisplayType(DisplayType.Amount);
			cCreditCardCuotaAmt.setReadWrite(false);
			cCreditCardCuotaAmt.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCreditCardCuotaAmt.setValue(null);
		}
		return cCreditCardCuotaAmt;
	}


	/**
	 * This method initializes cTenderTypeGridPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCTenderTypeGridPanel() {
		if (cTenderTypeGridPanel == null) {
			cTenderTypeGridPanel = new CPanel();
			//cTenderTypeGridPanel.add(gegtCCheckParamsPanel(), null);
			cTenderTypeGridPanel.setLayout(new BorderLayout());
			cTenderTypeGridPanel.add(getCTenderTypesTablePanel(), java.awt.BorderLayout.CENTER);
		}
		return cTenderTypeGridPanel;
	}

	/**
	 * This method initializes cSelectTenderTypePanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCSelectTenderTypePanel() {
		if (cSelectTenderTypePanel == null) {
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
			gridBagConstraints37.gridx = 0;
			gridBagConstraints37.gridwidth = 2;
			gridBagConstraints37.gridy = 2;
			gridBagConstraints37.anchor = java.awt.GridBagConstraints.EAST;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.gridx = 1;
			gridBagConstraints20.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints20.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints20.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints20.gridy = 3;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints19.gridy = 3;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
			cAmountLabel = new CLabel();
			cAmountLabel.setText(MSG_AMOUNT + " " + KeyUtils.getKeyStr(getActionKeys().get(CHANGE_FOCUS_CUSTOMER_AMOUNT)));
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 1;
			gridBagConstraints18.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints18.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints18.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints18.gridy = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints17.gridy = 1;
			cPaymentMediumLabel = new CLabel();
			cPaymentMediumLabel.setText(MSG_PAYMENT_MEDIUM);
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints15.gridx = 1;
			gridBagConstraints15.gridy = 0;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints15.insets = new java.awt.Insets(0,10,0,0);
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints14.gridy = 0;
			gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 0;
			cTenderTypeLabel = new CLabel();
			cTenderTypeLabel.setText(MSG_TYPE);
			cSelectTenderTypePanel = new CPanel();
			cSelectTenderTypePanel.setLayout(new GridBagLayout());
			cSelectTenderTypePanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,105));
			cSelectTenderTypePanel.add(cTenderTypeLabel, gridBagConstraints14);
			cSelectTenderTypePanel.add(getCTenderTypeCombo(), gridBagConstraints15);
			cSelectTenderTypePanel.add(cPaymentMediumLabel, gridBagConstraints17);
			cSelectTenderTypePanel.add(getCPaymentMediumCombo(), gridBagConstraints18);
			cSelectTenderTypePanel.add(cAmountLabel, gridBagConstraints19);
			cSelectTenderTypePanel.add(getCAmountText(), gridBagConstraints20);
			cSelectTenderTypePanel.add(getCCurrencyPanel(), gridBagConstraints37);
			
		}
		return cSelectTenderTypePanel;
	}

	/**
	 * This method initializes cTenderTypeCombo	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private CComboBox getCTenderTypeCombo() {
		if (cTenderTypeCombo == null) {
			cTenderTypeCombo = getComponentFactory().createTenderTypeCombo();
			cTenderTypeCombo.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cTenderTypeCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					ValueNamePair tenderType = (ValueNamePair) e.getItem();
					loadTenderType(tenderType.getValue());
				}
			});
			
			// Se selecciona efectivo por defecto.
			//selectTenderType(MPOSPaymentMedium.TENDERTYPE_Cash);
			cTenderTypeCombo.setMandatory(true);
			FocusUtils.addFocusHighlight(cTenderTypeCombo);
		}
		return cTenderTypeCombo;
	}

	/**
	 * This method initializes cSelectTenderTypeContentPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCSelectTenderTypeContentPanel() {
		if (cSelectTenderTypeContentPanel == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setHgap(0);
			flowLayout1.setVgap(0);
			cSelectTenderTypeContentPanel = new CPanel();
			cSelectTenderTypeContentPanel.setLayout(flowLayout1);
			//cSelectTenderTypeContentPanel.setPreferredSize(new java.awt.Dimension(300,200));
			cSelectTenderTypeContentPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,300));
			cSelectTenderTypeContentPanel.add(getCSelectTenderTypePanel(), null);
			cSelectTenderTypeContentPanel.add(getCTenderTypeParamsContentPanel(), null);
		}
		return cSelectTenderTypeContentPanel;
	}

	/**
	 * This method initializes cTenderTypeParamsPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCreditCardParamsPanel() {
		if (cCreditCardParamsPanel == null) {
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints28.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints28.gridy = 3;
			gridBagConstraints28.weightx = 1.0;
			gridBagConstraints28.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints28.gridx = 1;
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.gridx = 0;
			gridBagConstraints27.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints27.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints27.gridy = 3;
			cCouponNumberLabel = new CLabel();
			cCouponNumberLabel.setText(MSG_COUPON_NUMBER);
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints26.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints26.gridy = 2;
			gridBagConstraints26.weightx = 1.0;
			gridBagConstraints26.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints26.gridx = 1;
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints25.gridy = 2;
			cCreditCardNumberLabel = new CLabel();
			cCreditCardNumberLabel.setText(MSG_CARD_NUMBER);
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 0;
			gridBagConstraints23.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints23.gridy = 1;
			cBankLabel = new CLabel();
			cBankLabel.setText(MSG_BANK);
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints20.gridy = 0;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints20.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints19.gridy = 0;
			cCreditCardPlanLabel = new CLabel();
			cCreditCardPlanLabel.setText(MSG_CREDIT_CARD_PLAN);
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints30.gridy = 6;
			gridBagConstraints30.weightx = 1.0;
			gridBagConstraints30.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints30.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints30.gridx = 1;
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.gridx = 0;
			gridBagConstraints29.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints29.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints29.gridy = 6;
			cCardLabel = new CLabel();
			cCardLabel
					.setText(MSG_INSERT_CARD
							+ " "
							+ KeyUtils.getKeyStr(getActionKeys().get(
									GOTO_INSERT_CARD)));
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridwidth = 2;
			gridBagConstraints31.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints31.gridy = 5;
			cCardSeparator = new JSeparator();
			cCardSeparator.setPreferredSize(new Dimension(S_TENDERTYPE_PANEL_WIDTH,5));
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 0;
			gridBagConstraints32.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints32.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints32.gridy = 4;
			cPosnetLabel = new CLabel();
			cPosnetLabel.setText(MSG_POSNET);
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints33.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints33.gridy = 4;
			gridBagConstraints33.weightx = 1.0;
			gridBagConstraints33.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints33.gridx = 1;
			cCreditCardPlanLabel = new CLabel();
			cCreditCardPlanLabel.setText(MSG_CREDIT_CARD_PLAN);
			cCreditCardParamsPanel = new CPanel();
			cCreditCardParamsPanel.setLayout(new GridBagLayout());
			//cCreditCardParamsPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,155)); //107
			cCreditCardParamsPanel.add(cCreditCardPlanLabel, gridBagConstraints19);
			cCreditCardParamsPanel.add(getCCreditCardPlanCombo(), gridBagConstraints20);
			cCreditCardParamsPanel.add(cBankLabel, gridBagConstraints23);
			// Acá va el banco (ver mas adelante)
			cCreditCardParamsPanel.add(cCreditCardNumberLabel, gridBagConstraints25);
			cCreditCardParamsPanel.add(getCCreditCardNumberText(), gridBagConstraints26);
			cCreditCardParamsPanel.add(cCouponNumberLabel, gridBagConstraints27);
			cCreditCardParamsPanel.add(getCCouponNumberText(), gridBagConstraints28);
			cCreditCardParamsPanel.add(cPosnetLabel, gridBagConstraints32);
			cCreditCardParamsPanel.add(getCPosnetText(), gridBagConstraints33);			
			cCreditCardParamsPanel.add(cCardSeparator, gridBagConstraints31);
			cCreditCardParamsPanel.add(cCardLabel, gridBagConstraints29);
			cCreditCardParamsPanel.add(getCCardText(), gridBagConstraints30);
		}
		// El combo de banco siempre se agrega fuera del If ya que el combo
		// es compartido con el panel de Cheques. Un componente solo puede estar
		// contenido en un panel a la vez, con lo cual cada vez que se muestra el panel
		// de Cheque o Tarjeta hay que agregar el combo al panel mostrado 
		GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
		gridBagConstraints24.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints24.gridy = 1;
		gridBagConstraints24.weightx = 1.0;
		gridBagConstraints24.insets = new java.awt.Insets(7,10,0,0);
		gridBagConstraints24.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints24.gridx = 1;
		cCreditCardParamsPanel.add(getCBankCombo(), gridBagConstraints24);
		return cCreditCardParamsPanel;
	}

	/**
	 * This method initializes cCreditNoteParamsPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCreditNoteParamsPanel() {
		if (cCreditNoteParamsPanel == null) {
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints24.gridy = 1;
			gridBagConstraints24.weightx = 1.0;
			gridBagConstraints24.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints24.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 0;
			gridBagConstraints23.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints23.gridy = 1;
			cCreditNoteAvailableLabel = new CLabel();
			cCreditNoteAvailableLabel.setText(MSG_AVAILABLE_AMT);
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints20.gridy = 0;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints20.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints19.gridy = 0;
			cCreditNoteLabel = new CLabel();
			cCreditNoteLabel.setText(MSG_CREDIT);
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints25.gridy = 2;
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints26.gridy = 2;
			gridBagConstraints26.weightx = 1.0;
			gridBagConstraints26.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints26.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints26.gridx = 1;
			cCreditNoteBalanceLabel = new CLabel();
			cCreditNoteBalanceLabel.setText(MSG_CREDIT_BALANCE);
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.gridx = 1;
			gridBagConstraints27.insets = new java.awt.Insets(7,40,0,0);
			gridBagConstraints27.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints27.gridy = 3;
			cCreditNoteCashReturnLabel = new CLabel();
			cCreditNoteCashReturnLabel.setText(MSG_CASH_RETURNING);
			getCCreditNoteCashReturnCheck().setText(cCreditNoteCashReturnLabel.getText());
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.gridx = 0;
			gridBagConstraints28.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints28.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints28.gridy = 4;
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints29.gridy = 4;
			gridBagConstraints29.weightx = 1.0;
			gridBagConstraints29.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints29.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints29.gridx = 1;
			cCreditNoteCashReturnAmtLabel = new CLabel();
			cCreditNoteCashReturnAmtLabel.setText(MSG_AMT_TO_RETURN);
			cCreditNoteCashReturnAmtLabel.setVisible(false);
//			GridBagConstraints userAuthConstraints = new GridBagConstraints();
//			userAuthConstraints.gridx = 0;
//			userAuthConstraints.insets = new java.awt.Insets(7,0,0,0);
//			userAuthConstraints.anchor = java.awt.GridBagConstraints.WEST;
//			userAuthConstraints.gridy = 5;
//			userAuthConstraints.gridwidth = 2;
			cCreditNoteParamsPanel = new CPanel();
			cCreditNoteParamsPanel.setLayout(new GridBagLayout());
			//cCreditNoteParamsPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,155)); // 47
			cCreditNoteParamsPanel.add(cCreditNoteLabel, gridBagConstraints19);
			cCreditNoteParamsPanel.add(getCCreditNoteSearch(), gridBagConstraints20);
			cCreditNoteParamsPanel.add(cCreditNoteAvailableLabel, gridBagConstraints23);
			cCreditNoteParamsPanel.add(getCCreditNoteAvailableText(), gridBagConstraints24);
			// Saldo de la NC
			cCreditNoteParamsPanel.add(cCreditNoteBalanceLabel, gridBagConstraints25);
			cCreditNoteParamsPanel.add(getCCreditNoteBalanceText(), gridBagConstraints26);
//			// Devolución en efectivo
			cCreditNoteParamsPanel.add(getCCreditNoteCashReturnCheck(), gridBagConstraints27);
			cCreditNoteParamsPanel.add(cCreditNoteCashReturnAmtLabel, gridBagConstraints28);
			cCreditNoteParamsPanel.add(getCCreditNoteCashReturnAmtText(), gridBagConstraints29);
//			// Panel de autorización
//			cCreditNoteParamsPanel.add(getCCashRetunAuthPanel().getAuthPanel(), userAuthConstraints);
		}
		return cCreditNoteParamsPanel;
	}
	
	/**
	 * This method initializes cCreditNoteSearch	
	 * 	
	 * @return VLookup	
	 */
	private VLookup getCCreditNoteSearch() {
		if (cCreditNoteSearch == null) {
			cCreditNoteSearch = getComponentFactory().createCreditNoteSearch();
			cCreditNoteSearch.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCreditNoteSearch.addVetoableChangeListener(new VetoableChangeListener() {

				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					Integer invoiceID = (Integer)event.getNewValue();
					if (invoiceID != null && invoiceID > 0) {
						loadCreditNote(invoiceID);
					} else {			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
					gridBagConstraints24.fill = java.awt.GridBagConstraints.NONE;
					gridBagConstraints24.gridy = 1;
					gridBagConstraints24.weightx = 1.0;
					gridBagConstraints24.insets = new java.awt.Insets(7,10,0,0);
					gridBagConstraints24.anchor = java.awt.GridBagConstraints.EAST;
					gridBagConstraints24.gridx = 1;
					GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
					gridBagConstraints23.gridx = 0;
					gridBagConstraints23.insets = new java.awt.Insets(7,0,0,0);
					gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
					gridBagConstraints23.gridy = 1;
					cTransferDateLabel = new CLabel();
					cTransferDateLabel.setText(MSG_DATE);

						getCCreditNoteAvailableText().setValue(null);
						updateCreditNoteBalance();
					}
				}
				
			});
			cCreditNoteSearch.setMandatory(true);
			FocusUtils.addFocusHighlight(cCreditNoteSearch);
		}
		return cCreditNoteSearch;
	}
	
	private VNumber getCCreditNoteBalanceText() {
		if (cCreditNoteBalanceText == null) {
			cCreditNoteBalanceText = new VNumber();
			cCreditNoteBalanceText.setDisplayType(DisplayType.Amount);
			cCreditNoteBalanceText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCreditNoteBalanceText.setReadWrite(false);
			cCreditNoteBalanceText.setValue(null);
		}
		return cCreditNoteBalanceText;
	}
	
	
	private VCheckBox getCCreditNoteCashReturnCheck(){
		if (cCreditNoteCashReturnCheck == null) {
			cCreditNoteCashReturnCheck = new VCheckBox();
			cCreditNoteCashReturnCheck.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					VCheckBox checkToCompare = arg0 == null ? cCreditNoteCashReturnCheck
							: ((VCheckBox) arg0.getSource());
					boolean cashReturnSelected = checkToCompare.isSelected();
					getCCreditNoteCashReturnAmtText().setVisible(cashReturnSelected);
					cCreditNoteCashReturnAmtLabel.setVisible(cashReturnSelected);
					BigDecimal amtToReturn = (BigDecimal) getCCreditNoteBalanceText()
							.getValue();
					getCCreditNoteCashReturnAmtText().setValue(
							cashReturnSelected ? amtToReturn : BigDecimal.ZERO);
//					manageMaxCashReturnValue((BigDecimal) getCCreditNoteCashReturnAmtText()
//							.getValue());
				}
			});
			cCreditNoteCashReturnCheck.addAction("setSelected",
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							cCreditNoteCashReturnCheck
									.setSelected(!((VCheckBox) arg0.getSource())
											.isSelected());
							fireActionPerformed(cCreditNoteCashReturnCheck.getActionListeners(), null);
						}
					});
			FocusUtils.addFocusHighlight(cCreditNoteCashReturnCheck);
		}
		return cCreditNoteCashReturnCheck;
	}
	
	
	private VNumber getCCreditNoteCashReturnAmtText(){
		if (cCreditNoteCashReturnAmtText == null) {
			cCreditNoteCashReturnAmtText = new VNumber();
			cCreditNoteCashReturnAmtText.setDisplayType(DisplayType.Amount);
			cCreditNoteCashReturnAmtText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCreditNoteCashReturnAmtText.setValue(0);
			cCreditNoteCashReturnAmtText.setVisible(false);
			cCreditNoteCashReturnAmtText.setMandatory(true);
			cCreditNoteCashReturnAmtText.addAction("updateAmount", KeyStroke.getKeyStroke(
					KeyEvent.VK_ENTER, 0), new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (getCCreditNoteCashReturnAmtText().getValue() == null) {
						getCCreditNoteCashReturnAmtText().setValue(getCCreditNoteBalanceText().getValue());
					}
				}
			});
			FocusUtils.addFocusHighlight(cCreditNoteCashReturnAmtText);
		}
		return cCreditNoteCashReturnAmtText;
	}
	
	/**
	 * This method initializes cTransferParamsPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCTransferParamsPanel() {
		if (cTransferParamsPanel == null) {
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints24.gridy = 2;
			gridBagConstraints24.weightx = 1.0;
			gridBagConstraints24.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints24.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 0;
			gridBagConstraints23.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints23.gridy = 2;
			cTransferNumberLabel = new CLabel();
			cTransferNumberLabel.setText(MSG_TRANSFER_NUMBER);
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints22.gridy = 1;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints22.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints22.gridx = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints21.gridy = 1;
			cTransferDateLabel = new CLabel();
			cTransferDateLabel.setText(MSG_DATE);
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints19.gridy = 0;
			cTransferAccountLabel = new CLabel();
			cTransferAccountLabel.setText(MSG_BANK_ACCOUNT);
			cTransferParamsPanel = new CPanel();
			cTransferParamsPanel.setLayout(new GridBagLayout());
			//cTransferParamsPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,155)); //74
			cTransferParamsPanel.add(cTransferAccountLabel, gridBagConstraints19);
			cTransferParamsPanel.add(cTransferDateLabel, gridBagConstraints21);
			cTransferParamsPanel.add(getCTransferDate(), gridBagConstraints22);
			cTransferParamsPanel.add(cTransferNumberLabel, gridBagConstraints23);
			cTransferParamsPanel.add(getCTransferNumberText(), gridBagConstraints24);
		}
		// El combo de cuenta bancaria siempre se agrega fuera del If ya que el combo
		// es compartido con el panel de Cheques. Un componente solo puede estar
		// contenido en un panel a la vez, con lo cual cada vez que se muestra el panel
		// de Cheque o Transferencia hay que agregar el combo al panel mostrado 
		GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
		gridBagConstraints20.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints20.gridy = 0;
		gridBagConstraints20.weightx = 1.0;
		gridBagConstraints20.insets = new java.awt.Insets(0,10,0,0);
		gridBagConstraints20.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints20.gridx = 1;
		cTransferParamsPanel.add(getCBankAccountCombo(), gridBagConstraints20);
		return cTransferParamsPanel;
	}

	/**
	 * This method initializes cAmountText	
	 * 	
	 * @return org.openXpertya.grid.ed.VNumber	
	 */
	private VNumber getCAmountText() {
		if (cAmountText == null) {
			cAmountText = new VNumber();
			cAmountText.setDisplayType(DisplayType.Amount);
			cAmountText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cAmountText.setMandatory(true);
			cAmountText.addVetoableChangeListener(new VetoableChangeListener() {

				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					updateConvertedAmount();
					refreshPaymentMediumInfo();
				}
				
			});
			cAmountText.setValue(null);
			cAmountText.addAction("updateAmount", KeyStroke.getKeyStroke(
					KeyEvent.VK_ENTER, 0), new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (getCAmountText().getValue() == null) {
						getCAmountText().setValue(getCPaymentToPayAmt().getValue());
					}
				}
			});
			FocusUtils.addFocusHighlight(cAmountText);
		}
		return cAmountText;
	}

	
	/**
	 * This method initializes cCreditCardCombo	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private CComboBox getCCreditCardCombo() {
		// Este combo fue quitado de la interfaz luego de la implementación de
		// medios de pago. Ahora se muestra el combo que contiene los planes de
		// tarjeta en vez de este que mostraba las tarjetas propiamente dichas
		if (cCreditCardCombo == null) {
			java.awt.Dimension size = new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20);
			cCreditCardCombo = getComponentFactory().createCreditCardCombo();
			cCreditCardCombo.setPreferredSize(size);
			cCreditCardCombo.setMinimumSize(size);
			cCreditCardCombo.setMandatory(true);
		}
		return cCreditCardCombo;
	}

	/**
	 * This method initializes cCreditCardPlanCombo	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private CComboBox getCCreditCardPlanCombo() {
		if (cCreditCardPlanCombo == null) {
			java.awt.Dimension size = new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20);
			cCreditCardPlanCombo = new CComboBox();
			cCreditCardPlanCombo.setPreferredSize(size);
			cCreditCardPlanCombo.setMinimumSize(size);
			cCreditCardPlanCombo.setMandatory(true);
			cCreditCardPlanCombo.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					loadPaymentMediumInfo();
				}
			});
			FocusUtils.addFocusHighlight(cCreditCardPlanCombo);
		}
		return cCreditCardPlanCombo;
	}

	
	/**
	 * This method initializes cCreditCardNumberText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCreditCardNumberText() {
		if (cCreditCardNumberText == null) {
			cCreditCardNumberText = new CTextField();
			cCreditCardNumberText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCreditCardNumberText.setMinimumSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cCreditCardNumberText);
		}
		return cCreditCardNumberText;
	}

	/**
	 * This method initializes cCouponNumberText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCouponNumberText() {
		if (cCouponNumberText == null) {
			cCouponNumberText = new CTextField();
			cCouponNumberText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCouponNumberText.setMinimumSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cCouponNumberText);
		}
		return cCouponNumberText;
	}
	
	/**
	 * This method initializes cCardText
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private JTextField getCCardText(){
		if (cCardText == null) {
			cCardText = new VPasswordSimple();
			cCardText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCardText.setMinimumSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCardText.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String creditCardStr = getCCardText().getText();
					Object selectedOld = getCPaymentMediumCombo().getSelectedItem();
					// Obtener la info de los componentes que están actualmente
					// en los campos para setearlos luego
					String bank = (String)getCBankCombo().getValue();
					String posnet = (String)getCPosnetText().getValue();
					String coupon = (String)getCCouponNumberText().getValue();
					BigDecimal amt = (BigDecimal)getCAmountText().getValue(); 
					// Obtener la clase para parsear el nombre y el nro de
					// tarjeta a partir del string devuelto por el lector de
					// tarjetas
					CreditCard creditCard = CreditCardMaskManager.getCreditCard(creditCardStr);
					// Setear el nombre del cliente y el nro de tarjeta a partir
					// del string del lector
					String customerName = null;
					String creditCardNo = null;
					if(creditCard != null){
						creditCard.loadFields();
						customerName = creditCard.getCustomerName();
						creditCardNo = creditCard.getCreditCardNo();
					}
					getCCreditCardNumberText().setText(creditCardNo);
					getOrder().getBusinessPartner().setCustomerName(customerName);
					// Obtener las entidades financieras que respetan el
					// patrón de máscara ingresado
					List<EntidadFinanciera> financieras = getModel()
							.getEntidadesFinancieras(creditCardStr);
					loadTenderTypePaymentMediums(
							MPOSPaymentMedium.TENDERTYPE_CreditCard,
							financieras);
					if(financieras.isEmpty()){
						selectedOld = selectedOld == null ? getCPaymentMediumCombo()
								.getItemAt(0) : selectedOld;
						getCPaymentMediumCombo().setSelectedItem(selectedOld);
					}
					setCustomerDataDescriptionText();
					// Setear los valores que estaban seteados previamente a
					// insertar la tarjeta
					getCBankCombo().setValue(bank);
					getCPosnetText().setValue(posnet);
					getCCouponNumberText().setValue(coupon);
					getCAmountText().setValue(amt); 
				}
			});
			FocusUtils.addFocusHighlight(cCardText);
		}
		return cCardText;
	}

	private CTextField getCPosnetText() {
		if (cPosnetText == null) {
			cPosnetText = new CTextField();
			cPosnetText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cPosnetText.setMinimumSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cPosnetText);
		}
		return cPosnetText;
	} 
	
	/**
	 * This method initializes cCheckParamsPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCheckParamsPanel() {
		if (cCheckParamsPanel == null) {
			GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
			gridBagConstraints38.gridx = 1;
			gridBagConstraints38.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints38.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints38.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints38.gridy = 5;
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
			gridBagConstraints37.gridx = 0;
			gridBagConstraints37.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints37.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints37.gridy = 5;
			cCheckCUITLabel = new CLabel();
			cCheckCUITLabel.setText(MSG_CHECK_CUIT);
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.gridx = 0;
			gridBagConstraints35.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints35.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints35.gridy = 4;
			cBankLabel = new CLabel();
			cBankLabel.setText(MSG_BANK);
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.gridx = 1;
			gridBagConstraints34.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints34.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints34.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints34.gridy = 3;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints33.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints33.gridy = 3;
			cCheckAcctDateLabel = new CLabel();
			cCheckAcctDateLabel.setText(MSG_ACCT_DATE);
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 1;
			gridBagConstraints32.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints32.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints32.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints32.gridy = 2;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints31.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints31.gridy = 2;
			cCheckEmissionDateLabel = new CLabel();
			cCheckEmissionDateLabel.setText(MSG_EMISSION_DATE);
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints30.gridy = 1;
			gridBagConstraints30.weightx = 1.0;
			gridBagConstraints30.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints30.insets = new java.awt.Insets(7,10,0,0);
			gridBagConstraints30.gridx = 1;
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.gridx = 0;
			gridBagConstraints29.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints29.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints29.gridy = 1;
			cCheckNumberLabel = new CLabel();
			cCheckNumberLabel.setText(MSG_CHECK_NUMBER);
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.gridx = 0;
			gridBagConstraints27.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints27.gridy = 0;
			cBankAccountLabel = new CLabel();
			cBankAccountLabel.setText(MSG_BANK_ACCOUNT);
			cCheckParamsPanel = new CPanel();
			cCheckParamsPanel.setLayout(new GridBagLayout());
			//cCheckParamsPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,155));
			cCheckParamsPanel.add(cBankAccountLabel, gridBagConstraints27);
			cCheckParamsPanel.add(cCheckNumberLabel, gridBagConstraints29);
			cCheckParamsPanel.add(getCCheckNumberText(), gridBagConstraints30);
			cCheckParamsPanel.add(cCheckEmissionDateLabel, gridBagConstraints31);
			cCheckParamsPanel.add(getCCheckEmissionDate(), gridBagConstraints32);
			cCheckParamsPanel.add(cCheckAcctDateLabel, gridBagConstraints33);
			cCheckParamsPanel.add(getCCheckAcctDate(), gridBagConstraints34);
			cCheckParamsPanel.add(cBankLabel, gridBagConstraints35);
			if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
				cCheckParamsPanel.add(cCheckCUITLabel, gridBagConstraints37);
				cCheckParamsPanel.add(getCCheckCUITText(), gridBagConstraints38);
			}
		}
		// El combo de cuenta bancaria siempre se agrega fuera del If ya que el combo
		// es compartido con el panel de Transferencias. Un componente solo puede estar
		// contenido en un panel a la vez, con lo cual cada vez que se muestra el panel
		// de Cheque o Transferencia hay que agregar el combo al panel mostrado 
		GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
		gridBagConstraints28.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints28.gridy = 0;
		gridBagConstraints28.weightx = 1.0;
		gridBagConstraints28.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints28.insets = new java.awt.Insets(0,10,0,0);
		gridBagConstraints28.gridx = 1;
		cCheckParamsPanel.add(getCBankAccountCombo(), gridBagConstraints28);
		// Idem Combo de Bancos (compartido con tarjetas)
		GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
		gridBagConstraints36.gridx = 1;
		gridBagConstraints36.insets = new java.awt.Insets(7,10,0,0);
		gridBagConstraints36.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints36.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints36.gridy = 4;
		cCheckParamsPanel.add(getCBankCombo(), gridBagConstraints36);
		return cCheckParamsPanel;
	}

	/**
	 * This method initializes cBankCombo	
	 * 	
	 * @return VLookup	
	 */
	private VLookup getCBankCombo() {
		if (cBankCombo == null) {
			cBankCombo = getComponentFactory().createBankCombo();
			cBankCombo.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cBankCombo.setMinimumSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cBankCombo.setMandatory(true);
			FocusUtils.addFocusHighlight(cBankCombo);
		}
		return cBankCombo;
	}

	/**
	 * This method initializes cBankAccountCombo	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private VLookup getCBankAccountCombo() {
		if (cBankAccountCombo == null) {
			cBankAccountCombo = getComponentFactory().createBankAccountCombo();
			cBankAccountCombo.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cBankAccountCombo.setMandatory(true);
			FocusUtils.addFocusHighlight(cBankAccountCombo);
		}
		return cBankAccountCombo;
	}
	
	/**
	 * This method initializes cCheckNumberText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCheckNumberText() {
		if (cCheckNumberText == null) {
			cCheckNumberText = new CTextField();
			cCheckNumberText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cCheckNumberText);
		}
		return cCheckNumberText;
	}

	/**
	 * This method initializes cTransferNumberText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCTransferNumberText() {
		if (cTransferNumberText == null) {
			cTransferNumberText = new CTextField();
			cTransferNumberText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cTransferNumberText);
		}
		return cTransferNumberText;
	}
	
	/**
	 * This method initializes cCheckBankText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCheckBankText() {
		if (cCheckBankText == null) {
			cCheckBankText = new CTextField();
			cCheckBankText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cCheckBankText);
		}
		return cCheckBankText;
	}

	/**
	 * This method initializes cCheckCUITText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCheckCUITText() {
		if (cCheckCUITText == null) {
			cCheckCUITText = new CTextField();
			cCheckCUITText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			FocusUtils.addFocusHighlight(cCheckCUITText);
		}
		return cCheckCUITText;
	}

	/**
	 * This method initializes cCheckEmissionDate	
	 * 	
	 * @return org.openXpertya.grid.ed.VDate	
	 */
	private VDate getCCheckEmissionDate() {
		if (cCheckEmissionDate == null) {
			cCheckEmissionDate = new VDate();
			cCheckEmissionDate.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCheckEmissionDate.setMandatory(true);
			cCheckEmissionDate.setValue(TODAY);
			FocusUtils.addFocusHighlight(cCheckEmissionDate);
		}
		return cCheckEmissionDate;
	}

	/**
	 * This method initializes cTransferDate	
	 * 	
	 * @return org.openXpertya.grid.ed.VDate	
	 */
	private VDate getCTransferDate() {
		if (cTransferDate == null) {
			cTransferDate = new VDate();
			cTransferDate.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cTransferDate.setMandatory(true);
			cTransferDate.setValue(TODAY);
			FocusUtils.addFocusHighlight(cTransferDate);
		}
		return cTransferDate;
	}
	
	/**
	 * This method initializes cCheckAcctDate	
	 * 	
	 * @return org.openXpertya.grid.ed.VDate	
	 */
	private VDate getCCheckAcctDate() {
		if (cCheckAcctDate == null) {
			cCheckAcctDate = new VDate();
			cCheckAcctDate.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCheckAcctDate.setMandatory(true);
			FocusUtils.addFocusHighlight(cCheckAcctDate);
		}
		return cCheckAcctDate;
	}

	/**
	 * This method initializes cCreditNoteAvailableText	
	 * 	
	 * @return org.openXpertya.grid.ed.VNumber	
	 */
	private VNumber getCCreditNoteAvailableText() {
		if (cCreditNoteAvailableText == null) {
			cCreditNoteAvailableText = new VNumber();
			cCreditNoteAvailableText.setDisplayType(DisplayType.Amount);
			cCreditNoteAvailableText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cCreditNoteAvailableText.setReadWrite(false);
			cCreditNoteAvailableText.setValue(null);
		}
		return cCreditNoteAvailableText;
	}

	
	private CPanel getCCreditParamsPanel(){
		if (cCreditParamsPanel == null) {
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints36.gridy = 0;
			gridBagConstraints36.weightx = 1.0;
			gridBagConstraints36.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints36.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints36.gridx = 1;
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.gridx = 0;
			gridBagConstraints35.gridy = 0;
			cPaymentTermLabel = new CLabel();
			cPaymentTermLabel.setText(MSG_PAYMENT_TERM);
			cCreditParamsPanel = new CPanel();
			cCreditParamsPanel.setLayout(new GridBagLayout());
			//cCashParamsPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,155)); //20
			cCreditParamsPanel.add(cPaymentTermLabel, gridBagConstraints35);
			cCreditParamsPanel.add(getCPaymentTermCombo(), gridBagConstraints36);
		}
		return cCreditParamsPanel;
	}
	
	
	private CComboBox getCPaymentTermCombo(){
		if(cPaymentTermCombo == null){
			java.awt.Dimension size = new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20);
			cPaymentTermCombo = new CComboBox();
			cPaymentTermCombo.setPreferredSize(size);
			cPaymentTermCombo.setMinimumSize(size);
			cPaymentTermCombo.setMandatory(true);
		}
		return cPaymentTermCombo;
	}
	
	/**
	 * This method initializes cCashParamsPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCashParamsPanel() {
		if (cCashParamsPanel == null) {
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints36.gridy = 0;
			gridBagConstraints36.weightx = 1.0;
			gridBagConstraints36.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints36.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints36.gridx = 1;
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.gridx = 0;
			gridBagConstraints35.gridy = 0;
			cConvertedAmountLabel = new CLabel();
			cConvertedAmountLabel.setText(MSG_CONVERTED_AMOUNT);
			cCashParamsPanel = new CPanel();
			cCashParamsPanel.setLayout(new GridBagLayout());
			//cCashParamsPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,155)); //20
			cCashParamsPanel.add(cConvertedAmountLabel, gridBagConstraints35);
			cCashParamsPanel.add(getCConvertedAmountText(), gridBagConstraints36);
		}
		return cCashParamsPanel;
	}

	/**
	 * This method initializes cConvertedAmountText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCConvertedAmountText() {
		if (cConvertedAmountText == null) {
			cConvertedAmountText = new VNumber();
			cConvertedAmountText.setReadWrite(false);
			cConvertedAmountText.setDisplayType(DisplayType.Amount);
			cConvertedAmountText.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
		}
		return cConvertedAmountText;
	}

	/**
	 * This method initializes cCurrencyPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCurrencyPanel() {
		if (cCurrencyPanel == null) {
			GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
			gridBagConstraints39.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints39.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints39.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints39.gridx = 1;
			gridBagConstraints39.gridy = 0;
			gridBagConstraints39.weightx = 1.0;
			GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
			gridBagConstraints38.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints38.gridy = 0;
			gridBagConstraints38.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints38.gridx = 0;
			cCurrencyLabel = new CLabel();
			cCurrencyLabel.setText(MSG_CURRENCY);
			cCurrencyPanel = new CPanel();
			cCurrencyPanel.setLayout(new GridBagLayout());
			cCurrencyPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,27));
			cCurrencyPanel.setVisible(true);
			cCurrencyPanel.add(cCurrencyLabel, gridBagConstraints38);
			cCurrencyPanel.add(getCCurrencyCombo(), gridBagConstraints39);
		}
		return cCurrencyPanel;
	}

	/**
	 * This method initializes cCurrencyCombo2	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private VLookup getCCurrencyCombo() {
		if (cCurrencyCombo == null) {
			cCurrencyCombo = getComponentFactory().createCurrencyCombo();
			cCurrencyCombo.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			// Selecciono por defecto la moneda de la compañia.
			cCurrencyCombo.setValue(getModel().getCompanyCurrencyID());
			cCurrencyCombo.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					updateConvertedAmount();
				}
				
			});
			FocusUtils.addFocusHighlight(cCurrencyCombo);
		}
		return cCurrencyCombo;
	}

	/**
	 * This method initializes cAddTenderTypeButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCAddTenderTypeButton() {
		if (cAddTenderTypeButton == null) {
			cAddTenderTypeButton = new CButton();
			cAddTenderTypeButton.setText(MSG_ADD+" "+ KeyUtils.getKeyStr(getActionKeys().get(ADD_PAYMENT_ACTION)));
			cAddTenderTypeButton.setIcon(getImageIcon("Save16.gif"));
			cAddTenderTypeButton.setPreferredSize(new java.awt.Dimension(S_PAYMENT_ACTION_BUTTON_WIDTH,S_PAYMENT_ACTION_BUTTON_HEIGHT));
			cAddTenderTypeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addPayment();
				}
			});
			KeyUtils.setDefaultKey(cAddTenderTypeButton);
			FocusUtils.addFocusHighlight(cAddTenderTypeButton);
		}
		return cAddTenderTypeButton;
	}

	/**
	 * This method initializes cPaymentBottomPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentBottomPanel() {
		if (cPaymentBottomPanel == null) {
			cPaymentBottomPanel = new CPanel();
			cPaymentBottomPanel.setLayout(new BoxLayout(getCPaymentBottomPanel(), BoxLayout.Y_AXIS));
			cPaymentBottomPanel.setPreferredSize(new java.awt.Dimension(0,100));
			cPaymentBottomPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,0,5,0));
			cPaymentBottomPanel.add(getCPaymentFinishPanel(), null);
		}
		return cPaymentBottomPanel;
	}

	/**
	 * This method initializes cPaymentFinishPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentFinishPanel() {
		if (cPaymentFinishPanel == null) {
			cPaymentFinishPanel = new CPanel();
			cPaymentFinishPanel.setLayout(new BoxLayout(getCPaymentFinishPanel(), BoxLayout.X_AXIS));
			cPaymentFinishPanel.add(getCPaymentTotalBorderPanel(), null);
			cPaymentFinishPanel.add(getCPaymentCommandPanel(), null);
		}
		return cPaymentFinishPanel;
	}

	/**
	 * This method initializes cPaymentTotalPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentTotalBorderPanel() {
		if (cPaymentTotalBorderPanel == null) {
			FlowLayout flowLayout3 = new FlowLayout();
			flowLayout3.setHgap(0);
			flowLayout3.setVgap(0);
			cPaymentTotalBorderPanel = new CPanel();
			cPaymentTotalBorderPanel.setPreferredSize(new java.awt.Dimension(360,90));
			cPaymentTotalBorderPanel.setLayout(flowLayout3);
			cPaymentTotalBorderPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1), MSG_PAYMENTS_SUMMARY, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12), new java.awt.Color(51,51,51)), javax.swing.BorderFactory.createEmptyBorder(0,10,1,10)));
			cPaymentTotalBorderPanel.add(getCPaymentTotalPanel(), null);
		}
		return cPaymentTotalBorderPanel;
	}

	/**
	 * This method initializes cPaymentTotalPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentTotalPanel() {
		if (cPaymentTotalPanel == null) {
			final int vspace = 1;
			final int hspace = 22;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints12.gridy = 2;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new java.awt.Insets(vspace,10,0,0);
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.gridx = 3;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.insets = new java.awt.Insets(vspace,hspace,0,0);
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 2;
			cChangeLabel = new CLabel();
			cChangeLabel.setText(MSG_CHANGE);
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.insets = new java.awt.Insets(vspace,10,0,0);
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 3;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 2;
			gridBagConstraints9.insets = new java.awt.Insets(vspace,hspace,0,0);
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.gridy = 1;
			cBalanceLabel = new CLabel();
			cBalanceLabel.setText(MSG_BALANCE);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 3;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 2;
			gridBagConstraints7.insets = new java.awt.Insets(0,hspace,0,0);
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.gridy = 0;
			cPaidLabel = new CLabel();
			cPaidLabel.setText(MSG_PAID);

			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(vspace,10,0,0);
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new java.awt.Insets(vspace,0,0,0);
			gridBagConstraints5.gridy = 2;
			cToPayLabel = new CLabel();
			cToPayLabel.setText(MSG_TO_PAY);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(vspace,10,0,0);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new java.awt.Insets(vspace,0,0,0);
			gridBagConstraints3.gridy = 1;
			cDocumentDiscountLabel = new CLabel();
			cDocumentDiscountLabel.setText(MSG_DISCOUNT_SHORT);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.insets = new java.awt.Insets(0,10,0,0);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			cOrderTotalLabel = new CLabel();
			cOrderTotalLabel.setText(MSG_TOTAL);
			
			cPaymentTotalPanel = new CPanel();
			cPaymentTotalPanel.setLayout(new GridBagLayout());
			//cPaymentTotalPanel.setPreferredSize(new java.awt.Dimension(340,75));
			cPaymentTotalPanel.add(cOrderTotalLabel, gridBagConstraints1);
			cPaymentTotalPanel.add(getCOrderTotalAmt(), gridBagConstraints2);
			cPaymentTotalPanel.add(cDocumentDiscountLabel, gridBagConstraints3);
			cPaymentTotalPanel.add(getCDocumentDiscountAmt(), gridBagConstraints4);
			cPaymentTotalPanel.add(cToPayLabel, gridBagConstraints5);
			cPaymentTotalPanel.add(getCToPayText(), gridBagConstraints6);
			cPaymentTotalPanel.add(cPaidLabel, gridBagConstraints7);
			cPaymentTotalPanel.add(getCPaidText(), gridBagConstraints8);
			cPaymentTotalPanel.add(cBalanceLabel, gridBagConstraints9);
			cPaymentTotalPanel.add(getCBalanceText(), gridBagConstraints10);
			cPaymentTotalPanel.add(cChangeLabel, gridBagConstraints11);
			cPaymentTotalPanel.add(getCChangeText(), gridBagConstraints12);
		}
		return cPaymentTotalPanel;
	}

	/**
	 * This method initializes cToPayText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCToPayText() {
		if (cToPayText == null) {
			cToPayText = new VNumber();
			cToPayText.setPreferredSize(new java.awt.Dimension(70,20));
			cToPayText.setReadWrite(false);
			cToPayText.setDisplayType(DisplayType.Amount);
		}
		return cToPayText;
	}

	/**
	 * This method initializes cPaidText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCPaidText() {
		if (cPaidText == null) {
			cPaidText = new VNumber();
			cPaidText.setPreferredSize(new java.awt.Dimension(70,20));
			cPaidText.setReadWrite(false);
			cPaidText.setDisplayType(DisplayType.Amount);
		}
		return cPaidText;
	}

	/**
	 * This method initializes cBalanceText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCBalanceText() {
		if (cBalanceText == null) {
			cBalanceText = new VNumber();
			cBalanceText.setPreferredSize(new java.awt.Dimension(70,20));
			cBalanceText.setReadWrite(false);
			cBalanceText.setDisplayType(DisplayType.Amount);
		}
		return cBalanceText;
	}

	/**
	 * This method initializes cChangeText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VNumber getCChangeText() {
		if (cChangeText == null) {
			cChangeText = new VNumber();
			cChangeText.setPreferredSize(new java.awt.Dimension(70,20));
			cChangeText.setReadWrite(false);
			cChangeText.setDisplayType(DisplayType.Amount);
		}
		return cChangeText;
	}
	
	/**
	 * This method initializes cOrderTotalAmt	
	 * 	
	 * @return org.openXpertya.grid.VNumber	
	 */
	private VNumber getCOrderTotalAmt() {
		if (cOrderTotalAmt == null) {
			cOrderTotalAmt = new VNumber();
			cOrderTotalAmt.setPreferredSize(new java.awt.Dimension(70,20));
			cOrderTotalAmt.setReadWrite(false);
			cOrderTotalAmt.setDisplayType(DisplayType.Amount);
		}
		return cOrderTotalAmt;
	}
	
	/**
	 * This method initializes cDocumentDiscountAmt	
	 * 	
	 * @return org.openXpertya.grid.VNumber	
	 */
	private VNumber getCDocumentDiscountAmt() {
		if (cDocumentDiscountAmt == null) {
			cDocumentDiscountAmt = new VNumber();
			cDocumentDiscountAmt.setPreferredSize(new java.awt.Dimension(70,20));
			cDocumentDiscountAmt.setReadWrite(false);
			cDocumentDiscountAmt.setDisplayType(DisplayType.Amount);
		}
		return cDocumentDiscountAmt;
	}
	

	/**
	 * This method initializes cPaymentCommandPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPaymentCommandPanel() {
		if (cPaymentCommandPanel == null) {
			cPaymentCommandPanel = new CPanel();
			cPaymentCommandPanel.setLayout(new FlowLayout());
			cPaymentCommandPanel.setPreferredSize(new java.awt.Dimension(100,50));
			cPaymentCommandPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30,0,30,0));
			cPaymentCommandPanel.add(getCFinishPayButton(), null);
		}
		return cPaymentCommandPanel;
	}

	/**
	 * This method initializes cFinishPayButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCFinishPayButton() {
		if (cFinishPayButton == null) {
			cFinishPayButton = new CButton();
			cFinishPayButton.setIcon(getImageIcon("Process24.gif"));
			cFinishPayButton.setText(MSG_PAY + " " + KeyUtils.getKeyStr(getActionKeys().get(PAY_ORDER_ACTION)));
			//cFinishPayButton.setPreferredSize(new java.awt.Dimension(100,26));
			cFinishPayButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if(!getFrame().isBusy()){
						completeOrder();
					}
				}
				
			});
			KeyUtils.setDefaultKey(cFinishPayButton);
			FocusUtils.addFocusHighlight(cFinishPayButton);
		}
		return cFinishPayButton;
	}

	/**
	 * This method initializes cTenderTypesTableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private CScrollPane getCTenderTypesTableScrollPane() {
		if (cTenderTypesTableScrollPane == null) {
			cTenderTypesTableScrollPane = new CScrollPane();
			//cTenderTypesTableScrollPane.setPreferredSize(new java.awt.Dimension(190,200));
			cTenderTypesTableScrollPane.setPreferredSize(new java.awt.Dimension(210,300));
			cTenderTypesTableScrollPane.setViewportView(getCPaymentsTable());
		}
		return cTenderTypesTableScrollPane;
	}

	/**
	 * This method initializes cPaymentsTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private VTable getCPaymentsTable() {
		if (cPaymentsTable == null) {
			cPaymentsTable = new VTable();
			cPaymentsTable.setRowSelectionAllowed(true);
			
			// Creo el Modelo de la tabla.
			PaymentTableModel paymentTableModel = new PaymentTableModel();
			// Se vincula la lista de pagos en la orden con el table model
			// para que se muestren en la tabla.
			paymentTableModel.setPayments(getOrder().getPayments());
			paymentTableModel.addColumName(MSG_TYPE);
			paymentTableModel.addColumName(MSG_AMOUNT);
			cPaymentsTable.setModel(paymentTableModel);
						
			// Configuracion del renderizado de la tabla.
			cPaymentsTable.setFocusable(false);
			cPaymentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			// Renderer de String centrado.
			TableCellRenderer centeredCellRender = new DefaultTableCellRenderer() {

				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
					JLabel label = (JLabel)super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,col);
					label.setHorizontalAlignment(JLabel.CENTER);
					return label;
				}
				
			};
			cPaymentsTable.setDefaultRenderer(String.class, centeredCellRender);

			// Renderer de importes.
			cPaymentsTable.setDefaultRenderer(Number.class, getNumberCellRendered(amountFormat));
		
			ArrayList minWidth = new ArrayList();
			minWidth.add(130);
			minWidth.add(80);
			// Se wrapea la tabla con funcionalidad extra.
			setPaymentsTableUtils(new TableUtils(minWidth,cPaymentsTable));
			getPaymentsTableUtils().autoResizeTable();
			getPaymentsTableUtils().removeSorting();
		}
		return cPaymentsTable;
	}

	/**
	 * This method initializes cRemovePaymentButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCRemovePaymentButton() {
		if (cRemovePaymentButton == null) {
			cRemovePaymentButton = new CButton();
			cRemovePaymentButton.setText(MSG_DELETE + " " + KeyUtils.getKeyStr(getActionKeys().get(REMOVE_PAYMENT_ACTION)));
			cRemovePaymentButton.setIcon(getImageIcon("Delete16.gif"));
			cRemovePaymentButton.setPreferredSize(new java.awt.Dimension(S_PAYMENT_ACTION_BUTTON_WIDTH, S_PAYMENT_ACTION_BUTTON_HEIGHT));
			cRemovePaymentButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					removePayment();
				}
				
			});
			cRemovePaymentButton.setEnabled(false);
			KeyUtils.setDefaultKey(cRemovePaymentButton);
			FocusUtils.addFocusHighlight(cRemovePaymentButton);
		}
		return cRemovePaymentButton;
	}

	/**
	 * This method initializes cTenderTypesTablePanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCTenderTypesTablePanel() {
		if (cTenderTypesTablePanel == null) {
			GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
			gridBagConstraints47.gridx = 0;
			gridBagConstraints47.insets = new java.awt.Insets(0,0,10,0);
			gridBagConstraints47.gridy = 0;
			cSelectedTenderTypesLabel = new CLabel();
			cSelectedTenderTypesLabel.setText(MSG_PAYMENTS_SELECTED);
			cSelectedTenderTypesLabel.setFontBold(true);
			GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
			gridBagConstraints46.gridx = 0;
			gridBagConstraints46.insets = new java.awt.Insets(5,0,0,0);
			gridBagConstraints46.gridy = 2;
			GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
			gridBagConstraints45.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints45.gridy = 1;
			gridBagConstraints45.gridx = 0;
			cTenderTypesTablePanel = new CPanel();
			cTenderTypesTablePanel.setLayout(new GridBagLayout());
			cTenderTypesTablePanel.add(getCTenderTypesTableScrollPane(), gridBagConstraints45);
			cTenderTypesTablePanel.add(getCRemovePaymentButton(), gridBagConstraints46);
			cTenderTypesTablePanel.add(cSelectedTenderTypesLabel, gridBagConstraints47);
		}
		return cTenderTypesTablePanel;
	}

	/**
	 * This method initializes cTenderTypeParamsContentPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCTenderTypeParamsContentPanel() {
		if (cTenderTypeParamsContentPanel == null) {
			cTenderTypeParamsContentPanel = new CPanel();
			cTenderTypeParamsContentPanel.setLayout(new BorderLayout());
			//cTenderTypeParamsContentPanel.setLayout(new GridBagLayout());
			cTenderTypeParamsContentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6,0,0,0));
			cTenderTypeParamsContentPanel.setPreferredSize(new java.awt.Dimension(S_TENDERTYPE_PANEL_WIDTH,180));
		}
		return cTenderTypeParamsContentPanel;
	}
	
	/**
	 * This method initializes cPaymentMediumCombo	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private CComboBox getCPaymentMediumCombo() {
		if (cPaymentMediumCombo == null) {
			cPaymentMediumCombo = new CComboBox();
			cPaymentMediumCombo.setPreferredSize(new java.awt.Dimension(S_PAYMENT_FIELD_WIDTH,20));
			cPaymentMediumCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					loadPaymentMedium(false);
				}

			});
			cPaymentMediumCombo.setMandatory(true);
			FocusUtils.addFocusHighlight(cPaymentMediumCombo);
		}
		return cPaymentMediumCombo;
	}

	/**
	 * Dispara el evento action performed con el evento parámetro a todos los
	 * listeners parámetro
	 * 
	 * @param listeners
	 * @param event
	 */
	protected void fireActionPerformed(ActionListener[] listeners, ActionEvent event){
		for (ActionListener actionListener : listeners) {
			actionListener.actionPerformed(event);
		}
	}
	
	// ***********************************************************************************
	// **  PANEL DE CARGA PEDIDO
	// ***********************************************************************************
	/**
	 * This method initializes cLoadOrderPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCLoadOrderPanel() {
		if (cLoadOrderPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints6.gridy = 4;
			gridBagConstraints6.weightx = 1.0D;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.insets = new java.awt.Insets(10,0,3,0);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 3;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new java.awt.Insets(3,5,0,1);
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new java.awt.Insets(0,5,0,1);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints3.gridy = 2;
			cOrderDateLabel = new CLabel();
			cOrderDateLabel.setText(MSG_DATE + ":");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(3,0,0,0);
			gridBagConstraints2.gridy = 3;
			cOrderCustomerLabel = new CLabel();
			cOrderCustomerLabel.setText(MSG_CUSTOMER + ":");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.insets = new java.awt.Insets(3,0,5,0);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			cLoadOrderLabel = new CLabel();
			cLoadOrderLabel.setText(MSG_ORDER);
			cLoadOrderPanel = new CPanel();
			cLoadOrderPanel.setLayout(new GridBagLayout());
			cLoadOrderPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1), MSG_LOAD_CUSTOMER_ORDER, javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12), new java.awt.Color(51,51,51)), javax.swing.BorderFactory.createEmptyBorder(0,5,5,5)));
			cLoadOrderPanel.setPreferredSize(new java.awt.Dimension(180,165));
			cLoadOrderPanel.add(cLoadOrderLabel, gridBagConstraints);
			cLoadOrderPanel.add(getCOrderLookup(), gridBagConstraints1);
			cLoadOrderPanel.add(cOrderCustomerLabel, gridBagConstraints2);
			cLoadOrderPanel.add(cOrderDateLabel, gridBagConstraints3);
			cLoadOrderPanel.add(getCOrderDateText(), gridBagConstraints4);
			cLoadOrderPanel.add(getCOrderCustomerText(), gridBagConstraints5);
			cLoadOrderPanel.add(getCAddOrderButton(), gridBagConstraints6);
		}
		return cLoadOrderPanel;
	}

	/**
	 * This method initializes cOrderLookup	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private VLookup getCOrderLookup() {
		if (cOrderLookup == null) {
			cOrderLookup = getComponentFactory().createOrderSearch();
			cOrderLookup.setMandatory(false);
			cOrderLookup.setBackground(false);
			cOrderLookup.setPreferredSize(new Dimension(100,20));
//			cOrderLookup.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					((VLookup)cOrderLookup).actionPerformed(e);
//				}
//			});
			cOrderLookup.addVetoableChangeListener(new VetoableChangeListener() {

				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					Integer orderID = (Integer)event.getNewValue();
					if(orderID != null && orderID > 0) {
						TimeStatsLogger.beginTask(MeasurableTask.POS_SEARCH_CUSTOMER_ORDER);
						loadCustomerOrder(orderID);
						setFocus(getCAddOrderButton());
						TimeStatsLogger.endTask(MeasurableTask.POS_SEARCH_CUSTOMER_ORDER);
					} else {
						clearCustomerOrder();
					}
				}
			});
			FocusUtils.addFocusHighlight(cOrderLookup);

		}
		return cOrderLookup;
	}

	/**
	 * This method initializes cOrderDateText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCOrderDateText() {
		if (cOrderDateText == null) {
			cOrderDateText = new CTextField();
			cOrderDateText.setReadWrite(false);
			cOrderDateText.setPreferredSize(new Dimension(100,20));
			cOrderDateText.addVetoableChangeListener(new VetoableChangeListener() {

				private SimpleDateFormat dateFormat = DisplayType.getDateFormat(DisplayType.Date);
				
				public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					Object value = event.getNewValue();
					if(value != null && value instanceof Date) {
						getCOrderDateText().setValue(dateFormat.format((Date)value));
					}
				}
				
			});
		}
		return cOrderDateText;
	}

	/**
	 * This method initializes cOrderCustomerText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCOrderCustomerText() {
		if (cOrderCustomerText == null) {
			cOrderCustomerText = new CTextField();
			cOrderCustomerText.setReadWrite(false);
			cOrderCustomerText.setPreferredSize(new Dimension(100,20));
			cOrderCustomerText.setMinimumSize(new Dimension(100,20));
		}
		return cOrderCustomerText;
	}
	
	/**
	 * This method initializes cAddOrderButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCAddOrderButton() {
		if (cAddOrderButton == null) {
			cAddOrderButton = new CButton();
			cAddOrderButton.setIcon(getImageIcon("Save16.gif"));
			cAddOrderButton.setText(MSG_ADD + " " + KeyUtils.getKeyStr(getActionKeys().get(ADD_ORDER_ACTION)));
			cAddOrderButton.setEnabled(false);
			cAddOrderButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addCustomerOrder();
				}
			});
			KeyUtils.setDefaultKey(cAddOrderButton);
			FocusUtils.addFocusHighlight(cAddOrderButton);
		}
		return cAddOrderButton;
	}


	private void setCurrencySelectionEnabled(boolean enabled) {
		getCCurrencyCombo().setReadWrite(enabled);
		if(!enabled)
			getCCurrencyCombo().setValue(getModel().getCompanyCurrencyID());
	}

	/**
	 * @return Devuelve model.
	 */
	public PoSModel getModel() {
		return model;
	}

	/**
	 * @param model Fija o asigna model.
	 */
	public void setModel(PoSModel model) {
		this.model = model;
	}

	/**
	 * @return Devuelve cProductNameDetailLabel.
	 */
	private CLabel getCProductNameDetailLabel() {
		return cProductNameDetailLabel;
	}
	
	private boolean addOrderProduct(Product product) {
		TimeStatsLogger.beginTask(MeasurableTask.POS_ADD_PRODUCT);
		BigDecimal count = new BigDecimal(getCCountText().getText());
		boolean result = false;

		try {
			// Agrega el artículo al pedido
			OrderProduct newOrderProduct = getModel().addOrderProduct(product, count);
			// Actualiza la grilla del pedido
			getOrderTableModel().fireTableDataChanged();
			getOrderTableUtils().refreshTable();
			getOrderTableUtils().setSelection(newOrderProduct);
			// Actualiza montos
			updateTotalAmount();
			getCCountText().setValue(1);
			// Actualiza componentes gráficos
			setLoadedProduct(null);
			getCProductCodeText().setValue(null);
			getCProductCodeText().requestFocus();
			getCProductNameDetailLabel().setText("");
			getCProductLookup().setValue(null);
			result = true;
			
			if (getOrder().getOrderProducts().size() == 1) {
				getStatusBar().setStatusLine(MSG_POS_ORDER_STATUS);
			}
			
			TimeStatsLogger.endTask(MeasurableTask.POS_ADD_PRODUCT);
		
		} catch (ProductAddValidationFailed e) {
			// Se muestra un mensaje de error descriptivo.
			String msg = getMsg("CantAddProduct") + " '" + e.getProduct().getDescription() + "':";
			String description = e.getMessage();
			// Si la excepción tiene título y descripción se muestran estos datos
			if (e.getMessage() != null && e.getDescription() != null) {
				msg = e.getMessage();
				description = e.getDescription();
			}
			errorMsg(msg, getMsg(description));
		}

		updateAllowClose();
		
		return result;
	}
	
	private void searchProduct(String code) {
		TimeStatsLogger.beginTask(MeasurableTask.POS_SEARCH_PRODUCT);
		TimeStatsLogger.beginTask(MeasurableTask.POS_SEARCH_PRODUCT_SHOW_INFOPRODUCT);

		boolean noCode = code == null || code.length() == 0;
		// Si se encuentra un artículo se asigna a esta variable.
		Product product = null;
		// Indica si es necesario abrir o no el buscador de artículos.
		boolean openInfo = false;
		
		ProductList productList;
		if (noCode)
			productList = new ProductList();
		else
			productList = getModel().searchProduct(code); 
		
		getCProductNameDetailLabel().setForeground(Color.BLACK);
		getCProductNameDetailLabel().setText("");

		// 1. No se ingresó un texto de búsqueda, se abre el buscador de artículos.
		if (noCode) {
			openInfo = true;
			
		// 2. No se encontraron artículos en la búsqueda, se informa al usuario mediante un
		// mensaje
		} else if (productList.isEmpty()) {
			log.fine("Product not found: code=" + code);
			getCProductNameDetailLabel().setText(MSG_PRODUCT_NOT_FOUND);
			getCProductNameDetailLabel().setForeground(Color.RED);
			getCProductCodeText().setText("");
		
		// 3. Se encontró un único artículo (ya sea por match exacto o parcial), se
		// carga ese artículo al pedido	
		} else if (productList.size() == 1) {
			product = productList.firstProduct();
		
		// 4. Se encontró mas de un artículo...	
		} else {
			// Si hay solo 1 resultado exacto, se toma ese artículo
			if (productList.getExactProductCount() == 1) {
				product = productList.getExactProduct();
			// Si hay mas de un resultado exacto pero el usuario no tiene acceso al buscador
			// de artículos, entonces se obtiene el primer artículo resultante por matching
			// exacto. (si hay exactos están al principio de la lista).	
			} else if (productList.hasExactProduct() && !getModel().isUserCanAccessInfoProduct()) {
				product = productList.firstProduct();
			
			// Aquí se sabe que hay mas de 1 resultado, 0 o mas de un exacto. En este
			// caso se abre el buscador de artículos.	
			} else {
				openInfo = true;
			}
		}
		
		TimeStatsLogger.endTask(MeasurableTask.POS_SEARCH_PRODUCT);
		
		// Si se encontró un artículo se carga al pedido
		if (product != null) {
			log.finer("Product found: " + product);
			// Setea la descripcion del producto en el label y se guarda en la
			// variable de artículo cargado para ser agregado al pedido.
			getCProductNameDetailLabel().setText(product.getDescription());
			setLoadedProduct(product);
			// Transfiere el foco al Text de Cantidad
			getCCountText().requestFocus();
		
		// Si se determinó abrir el buscador (porque hay mas de un artículo encontrado) solo
		// se abre si el usuario tiene acceso al mismo.	
		} else if (openInfo && getModel().isUserCanAccessInfoProduct()) {
			VPoSLookup productLookup = (VPoSLookup)getCProductLookup();
			productLookup.openInfoProduct(productList.getProducts());
		}
	}
	
	private void updateTotalAmount() {
		BigDecimal total = getOrder().getTotalAmount();
		//getCTotalAmountLabel().setText(total.toString());
		getCTotalAmountLabel().setText(amountFormat.format(total));
	}

	/**
	 * @return Devuelve cTotalAmountLabel.
	 */
	private CLabel getCTotalAmountLabel() {
		return cTotalAmountLabel;
	}

	/**
	 * @return Devuelve frame.
	 */
	private FormFrame getFrame() {
		return frame;
	}

	/**
	 * @return Devuelve windowNo.
	 */
	public int getWindowNo() {
		return windowNo;
	}

	/**
	 * @param frame Fija o asigna frame.
	 */
	private void setFrame(FormFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param windowNo Fija o asigna windowNo.
	 */
	private void setWindowNo(int windowNo) {
		this.windowNo = windowNo;
	}

	/**
	 * @return Devuelve statusBar.
	 */
	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * @param statusBar Fija o asigna statusBar.
	 */
	private void setStatusBar(StatusBar statusBar) {
		this.statusBar = statusBar;
	}
	
	private void warn(String title, String msg) {
		ADialog.warn(getWindowNo(),getFrame(),title,msg);
	}

	/**
	 * @return Devuelve orderTableUtils.
	 */
	private TableUtils getOrderTableUtils() {
		return orderTableUtils;
	}

	/**
	 * @param orderTableUtils Fija o asigna orderTableUtils.
	 */
	private void setOrderTableUtils(TableUtils orderTableUtils) {
		this.orderTableUtils = orderTableUtils;
	}

	/**
	 * @return Devuelve tenderTypeTableUtils.
	 */
	private TableUtils getPaymentsTableUtils() {
		return paymentsTableUtils;
	}

	/**
	 * @param tenderTypeTableUtils Fija o asigna tenderTypeTableUtils.
	 */
	private void setPaymentsTableUtils(TableUtils tenderTypeTableUtils) {
		this.paymentsTableUtils = tenderTypeTableUtils;
	}
	
	private void openUpdateOrderProductDialog() {
		if(!hasOrderProducts())
			return;
		OrderProduct orderProduct = (OrderProduct)getOrderTableUtils().getSelection();
		if(orderProduct != null) {
			UpdateOrderProductDialog dialog = new UpdateOrderProductDialog(orderProduct,this);
			AEnv.positionCenterScreen(dialog);
			dialog.setModal(true);
			dialog.setVisible(true);
		}
	}
	
	private void openCustomerDataDialog() {
		if (getOrder().getBusinessPartner() == null) 
			return;
		
		// TODO: FB. Falta definir cuando es obligatorio la identificación del comprador
		// y cuando no para ver cual es el valor del parámetro mandatoryData en la instanciación
		// del diálogo.
		CustomerDataDialog dialog = new CustomerDataDialog(this, false);
		AEnv.positionCenterScreen(dialog);
		dialog.setModal(true);
		dialog.setVisible(true);
		setCustomerDataDescriptionText();
	}
	
	private void setCustomerDataDescriptionText() {
		String data = "";
		if (getOrder().getBusinessPartner() != null) {
			data = getOrder().getBusinessPartner().getCustomerDescription();
		}
		getCCustomerDescriptionText().setText(data);
	}
	
	protected void updateOrderProduct(OrderProduct orderProduct) {
		//getModel().calculateOrderProductTax(orderProduct);
		getOrder().updateOrderProduct();
		getOrderTableUtils().refreshTable();
		getOrderTableUtils().setSelection(orderProduct);
		updateTotalAmount();
	}
	
	protected void removeOrderProduct(OrderProduct orderProduct) {
		getOrder().removeOrderProduct(orderProduct);
		getOrderTableUtils().refreshTable();
		getCProductNameDetailLabel().setText("");
		updateTotalAmount();
		updateAllowClose();
	}
	
	private boolean hasOrderProducts() {
		return getCOrderTable().getRowCount() > 0;
	}
	
	private void goToPayments() {
		if(!hasOrderProducts()) {
			errorMsg(MSG_NO_PRODUCT_ERROR);
		} else {
			TimeStatsLogger.beginTask(MeasurableTask.POS_GOTO_PAYMENTS);
			
			getCPosTab().setEnabledAt(1,true);
			getCPosTab().setSelectedIndex(1);
			updatePaymentsStatus();
			
			TimeStatsLogger.endTask(MeasurableTask.POS_GOTO_PAYMENTS);
		}
	}

	protected void cancelOrder(){
		if(hasOrderProducts() && askMsg(MSG_CONFIRM_CANCEL_ORDER)){
			// Si el TPV está configurado para permitir modificaciones de precios
			// entonces no se validan los datos de usuario.
			if (!getModel().priceModifyAllowed()) {
				// Validar permisos de usuario
				AuthOperation authOperation = new AuthOperation(
						UserAuthConstants.POS_CANCEL_ORDER_UID,
						MSG_CANCEL_ORDER,
						UserAuthConstants.POS_CANCEL_ORDER_MOMENT);
				getAuthDialog().addAuthOperation(authOperation);
				getAuthDialog().authorizeOperation(UserAuthConstants.POS_CANCEL_ORDER_MOMENT);
				CallResult result = getAuthDialog().getAuthorizeResult(true);
				if(result == null){
					return;
				}
				if(result.isError()){
					if(!Util.isEmpty(result.getMsg(), true)){
						errorMsg(result.getMsg());
					}
					return;
				}
			}
			// Nuevo pedido
			newOrder();
		}
	}	
	
	/**
	 * @return Devuelve actionKeys.
	 */
	private Map<String, KeyStroke> getActionKeys() {
		return actionKeys;
	}

	/**
	 * @param actionKeys Fija o asigna actionKeys.
	 */
	private void setActionKeys(Map<String, KeyStroke> actionKeys) {
		this.actionKeys = actionKeys;
	}

	/**
	 * @return Devuelve componentFactory.
	 */
	protected PoSComponentFactory getComponentFactory() {
		return componentFactory;
	}

	/**
	 * @param componentFactory Fija o asigna componentFactory.
	 */
	protected void setComponentFactory(PoSComponentFactory componentFactory) {
		this.componentFactory = componentFactory;
	}

	protected boolean loadBPartner(int bPartnerID) {
		boolean load = true;
		// Solo se carga el cliente si el pedido no tiene asignado un cliente
		// o el cliente asignado al pedido es distinto al que se quiere cargar.
		if(getOrder().getBusinessPartner() == null || 
		   getOrder().getBusinessPartner().getId() != bPartnerID) {
			
			BusinessPartner bp = getModel().getBPartner(bPartnerID);
			getOrder().setOtherTaxes(getModel().getOtherTaxes(bp));
			getOrder().setBusinessPartner(bp);
		}
		
		getCTaxIdText().setText("");
		getCCustomerDescriptionText().setText("");
		setCustomerDataVisible(getOrder().getBusinessPartner().getIVACategory() == MCategoriaIva.CONSUMIDOR_FINAL);
		// Se carga el combo de direcciones.
		getCClientLocationCombo().removeAllItems();
		List<Location> bpLocations = getModel().getBPartnerLocations(bPartnerID);
		if(bpLocations.isEmpty()) {
			errorMsg(MSG_NO_BPARTNER_LOCATION_ERROR);
			getCClientText().setValue(null);
			getOrder().setBusinessPartner(null);
			load = false;
		} else {
			for (Location location : bpLocations) {
				getCClientLocationCombo().addItem(location);
			}
			getCClientLocationCombo().setBackground(false);
			//Se carga el text del taxid.
			getCTaxIdText().setText(getOrder().getBusinessPartner().getTaxId());
		 	getCClientText().setBackground(false);
		 	refreshBPartnerCreditPaymentMedium(getSelectedTenderType());
		 	refreshBPartnerDiscount();
			loadPaymentMediumInfo();
		 	refreshPaymentTerm(getSelectedPaymentMedium());
		 	// Actualizar los productos de la orden 
		 	// por el posible esquema de descuento nuevo 
		 	// de la entidad comercial modificada
		 	refreshOrderProductsTable();
		 	updatePaymentsTable();
		}
		setCustomerDataDescriptionText();
		updateStatusDB();
		return load;
	}
	
	private void refreshBPartnerDiscount() {
	 	// Se muestra el esquema de descuento si tiene y es aplicable.
		DiscountSchema discountSchema = getOrder().getBusinessPartner()
				.getDiscountSchema();
		if (!getOrder().isBPartnerDiscountApplicable()) {
			discountSchema = null;
		}
	 	getCBPartnerDiscountText().setText(
				getDiscountSchemaDescription(discountSchema));
	}
	
	private void setCustomerDataVisible(boolean visible) {
		//getCCustomerDescriptionText().setVisible(visible);
		//getCCustomerDataButton().setVisible(visible);
		setActionEnabled(SET_CUSTOMER_DATA_ACTION, visible);
		cCustomerDescriptionLabel.setVisible(visible);
		getCBuyerPanel().setVisible(visible);
	}
	
	private void updateCheckTenderTypeComponents() {
		showTenderTypeParamsPanel(getCCheckParamsPanel(), null);
		getCAmountText().setValue(null);
		getCBankAccountCombo().setValue(
				getModel().getPoSConfig().getCheckBankAccountID());
	}
	
	private void updateCreditTenderTypeComponents() {
		showTenderTypeParamsPanel(getCCreditParamsPanel(), null);
		BigDecimal toPay =  getOrder().getToPayAmount(getSelectedPaymentMedium());

		if(toPay.compareTo(BigDecimal.ZERO) > 0)
			getCAmountText().setValue(toPay);		
	}
	
	private void updateCashTenderTypeComponents() {
		showTenderTypeParamsPanel(getCCashParamsPanel(), null);
		getCAmountText().setValue(null);
	}
	
	private void updateCreditCardTenderTypeComponents() {
		showTenderTypeParamsPanel(getCCreditCardParamsPanel(), getCCreditCardInfoPanel());
		getCCreditCardInfoPanel().setVisible(true);
		EntidadFinancieraPlan currentPlan = getSelectedCreditCardPlan();
		getCAmountText().setReadWrite(currentPlan != null);
		if (currentPlan == null) {
			getCAmountText().setValue(null);
		}
	}
	
	private void updateCreditNoteTenderTypeComponents() {
		showTenderTypeParamsPanel(getCCreditNoteParamsPanel(), null);
		getCAmountText().setValue(null);
	}

	private void updateTransferTenderTypeComponents() {
		showTenderTypeParamsPanel(getCTransferParamsPanel(), null);
		getCAmountText().setValue(null);
		getCBankAccountCombo().setValue(null);
	}

	private void addPayment() {
		TimeStatsLogger.beginTask(MeasurableTask.POS_ADD_PAYMENT);
		
		String tenderType = getSelectedTenderType();
		BigDecimal amount = (BigDecimal)getCAmountText().getValue();
		int currencyId = ((Integer)getCCurrencyCombo().getValue()).intValue();
		PaymentMedium paymentMedium = getSelectedPaymentMedium(); 
		CallResult extraValidationsResult = null; 
		
		// Se exije un medio de pago
		if (paymentMedium == null) {
			errorMsg(MSG_NO_PAYMENT_MEDIUM_ERROR);
			return;
		// El importe no puede ser null
		} else if (amount == null) {
			errorMsg(MSG_NO_AMOUNT_ERROR);
			return;
		// El importe no puede ser menor o igual que cero
		} else if (amount.compareTo(new BigDecimal(0.0)) <= 0) {
			errorMsg(MSG_INVALID_PAYMENT_AMOUNT_ERROR);
			return;
		// Si el balance es positivo implica que no hay mas nada que pagar (ya se
		// han agregado medios de pago que cubren el importe total del pedido)	
		} else if (getOrder().getBalance().compareTo(BigDecimal.ZERO) >= 0) {
			errorMsg(MSG_NOT_NEED_PAYMENTS_ERROR);
			return;
		}
		
		Payment payment = null;

		if(MPOSPaymentMedium.TENDERTYPE_Cash.equals(tenderType)) {
			BigDecimal convertedAmt = (BigDecimal)getCConvertedAmountText().getValue();
			// Se valida que haya una tasa de conversión para la moneda.
			if(convertedAmt == null) {
				errorMsg(MSG_NO_CURRENCY_CONVERT_ERROR);
				return;
			}
			extraValidationsResult = getExtraPOSPaymentAddValidations().validateCashPayment(this);
			if(extraValidationsResult.isError()){
				errorMsg(extraValidationsResult.getMsg());
				return;
			}
			payment = new CashPayment(convertedAmt);
			payment.setTypeName(MSG_CASH);
		
			// Se limpian los campos para ingresar un nuevo pago.
			getCConvertedAmountText().setValue(null);
			
		} else if(MPOSPaymentMedium.TENDERTYPE_Check.equals(tenderType)) {
			String bankName = getSelectedBankName();
			String checkNumber = getCCheckNumberText().getText();
			Timestamp emissionDate = getCCheckEmissionDate().getTimestamp();
			Timestamp acctDate = getCCheckAcctDate().getTimestamp();
			Integer bankAccountID = (Integer)getCBankAccountCombo().getValue();
			String cuitLibrador = getCCheckCUITText().getText().trim();
			
			// La cuenta bancaria destino es obligatoria
			if (bankAccountID == null || bankAccountID == 0) {
				errorMsg(MSG_NO_BANK_ACCOUNT_ERROR);
				return;
			} else if (bankName == null) {
				errorMsg(MSG_FILL_MANDATORY, MSG_BANK);
				return;
			} else if (emissionDate == null) {
				errorMsg(MSG_FILL_MANDATORY, MSG_EMISSION_DATE);
				return;
			} else if (!validateCheckAcctDate(acctDate, paymentMedium)) {
				return;
			}
			// Si el cheque debe validar los plazos anteriores, se debe
			// verificar que existan los cheques con los plazos anteriores
			// agregados actualmente a la compra
			else if (paymentMedium.isValidationBeforeCheckDeadLines()
					&& !getModel().existsBeforeCheckDeadLinesFor(paymentMedium)) {
				errorMsg(MSG_NO_BEFORE_CHECK_DEADLINES);
				return;
			}
			// Si el control por CUIT está activado, entonces el CUIT y el nro
			// de cheque son obligatorios
			else if(getModel().isCheckCUITControlActivated()){
				if(Util.isEmpty(checkNumber, true)){
					errorMsg(MSG_FILL_MANDATORY, MSG_CHECK_NUMBER);
					return;
				}
				if(Util.isEmpty(cuitLibrador, true)){
					errorMsg(MSG_FILL_MANDATORY, MSG_CHECK_CUIT);
					return;
				}
			}
			extraValidationsResult = getExtraPOSPaymentAddValidations().validateCheckPayment(this, amount);
			if(extraValidationsResult.isError()){
				errorMsg(extraValidationsResult.getMsg());
				return;
			}
			
			//emissionDate = (emissionDate == null? now : emissionDate);
			//acctDate = (acctDate == null? now : acctDate);
			cuitLibrador = (cuitLibrador.length() == 0 ? null : cuitLibrador);
			
			payment = new CheckPayment(bankName, checkNumber, emissionDate,
					acctDate, bankAccountID, paymentMedium.getCheckDeadLine());
			payment.setTypeName(MSG_CHECK);
			((CheckPayment)payment).setCuitLibrador(cuitLibrador); // Si locale AR no está activo esto es null;
			
			// Se limpian los campos para ingresar un nuevo pago.
			getCCheckNumberText().setText("");
			getCCheckEmissionDate().setValue(TODAY);
			getCCheckAcctDate().setValue(null);
			getCCheckBankText().setText("");
			getCCheckCUITText().setText("");
			clearBankCombo();
		
		} else if(MPOSPaymentMedium.TENDERTYPE_Credit.equals(tenderType)) {
			// Esquema de vencimientos
			PaymentTerm pt = (PaymentTerm)getCPaymentTermCombo().getSelectedItem();
			// EL importe no puede superar el saldo.
			if(!validatePaymentAmount(amount, null)) {
				errorMsg(MSG_INVALID_CREDIT_AMOUNT_ERROR);
				return;
			}
			if(pt == null){
				errorMsg(MSG_NO_PAYMENTTERM);
				return;
			}
			extraValidationsResult = getExtraPOSPaymentAddValidations().validateCreditPayment(this);
			if(extraValidationsResult.isError()){
				errorMsg(extraValidationsResult.getMsg());
				return;
			}
			payment = new CreditPayment(pt);
			payment.setTypeName(MSG_CREDIT);
		
		} else if(MPOSPaymentMedium.TENDERTYPE_CreditCard.equals(tenderType)) {
			String bankName = getSelectedBankName();
			// -> Reemplazado por EntidadFinanciera Plan
			// EntidadFinanciera entidadFinanciera = (EntidadFinanciera)getCCreditCardCombo().getValue();
			// <-
			EntidadFinancieraPlan creditCardPlan = getSelectedCreditCardPlan();
			String posnet = getCPosnetText().getText();
			String creditCardNumber = getCCreditCardNumberText().getText();
			String couponNumber = getCCouponNumberText().getText();
			// Es necesario un plan de tarjeta
			if (getSelectedCreditCardPlan() == null) {
				errorMsg(MSG_NO_CREDIT_CARD_PLAN_ERROR);
				return;
			// El banco es requerido
			} else if (bankName == null) {
				errorMsg(MSG_FILL_MANDATORY, MSG_BANK);
				return;
			// El importe no puede superar el saldo pendiente del pedido
			} else if (!validatePaymentAmount(amount, null)) {
				errorMsg(MSG_INVALID_CARD_AMOUNT_ERROR);
				return;
			}
			extraValidationsResult = getExtraPOSPaymentAddValidations()
					.validateCreditCardPayment(this, creditCardNumber,
							couponNumber);
			if(extraValidationsResult.isError()){
				errorMsg(extraValidationsResult.getMsg());
				return;
			}
			
			payment = new CreditCardPayment(creditCardPlan, creditCardNumber, couponNumber, bankName, posnet);
						
			payment.setTypeName(MSG_CREDIT_CARD);
			
			// Se limpian los campos para ingresar un nuevo pago.
			getCCreditCardNumberText().setText("");
			getCCouponNumberText().setText("");
			getCCardText().setText("");
			clearBankCombo();

		} else if (MPOSPaymentMedium.TENDERTYPE_CreditNote.equals(tenderType)) {
			BigDecimal availableAmt = (BigDecimal)getCCreditNoteAvailableText().getValue();
			Integer invoiceID = (Integer)getCCreditNoteSearch().getValue();
			BigDecimal balanceAmt = (BigDecimal)getCCreditNoteBalanceText().getValue();
			boolean returnCash = getCCreditNoteCashReturnCheck().isSelected();
			BigDecimal returnCashAmt = (BigDecimal)getCCreditNoteCashReturnAmtText().getValue();
			
			// La nota de crédito es obligatoria
			if (invoiceID == null || invoiceID == 0) {
				errorMsg(MSG_FILL_MANDATORY, MSG_CREDIT);
				return;
			// La nota de crédito no debe ser una que ya agregó
			} else if(getOrder().existsCreditNote(invoiceID)){
				errorMsg(MSG_CREDIT_NOTE_REPEATED_ERROR);
				return;
			// El importe no puede ser mayor al monto pendiente de la nota de crédito
			} else if (amount.compareTo(availableAmt) > 0) {
				errorMsg(MSG_AMOUNT_GREATHER_THAN_AVAILABLE);
				getCAmountText().setValue(availableAmt);
				return;
			// El importe no puede superar el saldo pendiente del pedido
			} else if (!validatePaymentAmount(amount, availableAmt)) {
				errorMsg(MSG_INVALID_CREDIT_AMOUNT_ERROR);
				return;
			// La devolución en efectivo debe ser mayor a 0
			} else if (returnCash
					&& (returnCashAmt == null || returnCashAmt
							.compareTo(BigDecimal.ZERO) <= 0)) {
				errorMsg(MSG_RETURN_CASH_POSITIVE_AMOUNT_ERROR);
				return;
			// La devolución en efectivo debe ser menor o igual al saldo que queda de la NC
			} else if (returnCash && returnCashAmt != null
					&& returnCashAmt.compareTo(balanceAmt) > 0) {
				errorMsg(MSG_RETURN_CASH_AMOUNT_ERROR);
				return;
			// Si la devolución en efectivo se debe controlar y supera el máximo permitido, entonces se debe autorizar
			} else if (getModel().isControlCashReturns()
					&& returnCash
					&& (returnCashAmt != null && getModel()
							.isCashReturnedSurpassMax(returnCashAmt))) {
				AuthOperation authOperation = new AuthOperation(
						UserAuthConstants.POS_CN_MAX_CASH_RETURN_UID,
						getMsgRepository()
								.getMsg(UserAuthConstants
										.getProcessValue(UserAuthConstants.POS_CN_MAX_CASH_RETURN_UID)),
						UserAuthConstants.POS_ADD_PAYMENT_MOMENT);
				getAuthDialog().addAuthOperation(authOperation);
				getAuthDialog().authorizeOperation(UserAuthConstants.POS_ADD_PAYMENT_MOMENT);
				CallResult result = getAuthDialog().getAuthorizeResult(true);
				if(result == null){
					return;
				}
				if(result.isError()){
					if(!Util.isEmpty(result.getMsg(), true)){
						errorMsg(result.getMsg());
					}
					return;
				}
			}
			extraValidationsResult = getExtraPOSPaymentAddValidations()
					.validateCreditNotePayment(this, balanceAmt, returnCash);
			if(extraValidationsResult.isError()){
				errorMsg(extraValidationsResult.getMsg());
				return;
			}
			
			payment = new CreditNotePayment(invoiceID, availableAmt, balanceAmt, returnCash, returnCashAmt);
			payment.setTypeName(paymentMedium.getTenderTypeName());

			// Se limpian los campos para ingresar un nuevo pago.
			getCCreditNoteSearch().setValue(null);
			getCCreditNoteAvailableText().setValue(null);
			getCCreditNoteCashReturnCheck().setSelected(false);
			fireActionPerformed(getCCreditNoteCashReturnCheck().getActionListeners(), null);
		} else if (MPOSPaymentMedium.TENDERTYPE_DirectDeposit.equals(tenderType)) {
			Timestamp transferDate = getCTransferDate().getTimestamp();
			Integer bankAccountID = (Integer)getCBankAccountCombo().getValue();
			String transferNumber = getCTransferNumberText().getText().trim();

			transferNumber = transferNumber.length() == 0 ? null : transferNumber;
			
			// La cuenta bancaria destino es obligatoria
			if (bankAccountID == null || bankAccountID == 0) {
				errorMsg(MSG_NO_BANK_ACCOUNT_ERROR);
				return;
			// Fecha obligatoria
			} else if (transferDate == null) {
				errorMsg(MSG_FILL_MANDATORY, MSG_DATE);
				return;
			// El importe no puede superar el saldo pendiente del pedido
			} else if (!validatePaymentAmount(amount, null)) {
				errorMsg(MSG_PAYMENT_AMT_SURPLUS_ERROR);
				return;
			}
			extraValidationsResult = getExtraPOSPaymentAddValidations().validateDirectDepositPayment(this);
			if(extraValidationsResult.isError()){
				errorMsg(extraValidationsResult.getMsg());
				return;
			}
			
			payment = new BankTransferPayment(transferNumber, bankAccountID, transferDate);
			payment.setTypeName(MSG_TRANSFER);
			
			// Se limpian los campos para ingresar un nuevo pago.
			getCTransferDate().setValue(TODAY);
			getCBankAccountCombo().setValue(null);
			getCTransferNumberText().setText("");
		}
		
		payment.setTenderType(tenderType);
		payment.setCurrencyId(currencyId);
		payment.setAmount(amount);
		// Se asocia el medio de pago con el pago concreto.
		payment.setPaymentMedium(paymentMedium);
		
		// Se agrega el pago a la orden actual.
		getOrder().addPayment(payment);
		getPaymentsTableUtils().setSelection(payment);
		
		// Se limpian los campos para ingresar un nuevo pago.
		getCAmountText().setValue(null);
		getCPaymentToPayAmt().setValue(null);
		
		getCRemovePaymentButton().
			setEnabled(getOrder().hasPayments());
		
		// Se actualizan los totales y la info del medio de pago actual
		updatePaymentsTable();
		updatePaymentsStatus();
		refreshPaymentMediumInfo();
		refreshBPartnerDiscount();
		// Se actualiza el esquema de vencimientos
		refreshPaymentTerm(getSelectedPaymentMedium());
		getCTenderTypeCombo().requestFocus();
		TimeStatsLogger.endTask(MeasurableTask.POS_ADD_PAYMENT);
	}
	
	private void updatePaymentsTable() {
		getPaymentTableModel().fireTableDataChanged();
		getPaymentsTableUtils().refreshTable();
		if(getPaymentsTableUtils().getSelection() == null)
			getPaymentsTableUtils().selectFirst();
	}

	private String getSelectedTenderType() {
		return ((ValueNamePair)getCTenderTypeCombo().getSelectedItem()).getValue();
	}
	
	private PaymentTableModel getPaymentTableModel() {
		return ((PaymentTableModel)getCPaymentsTable().getModel());
	}
	
	private ProductTableModel getOrderTableModel() {
		return ((ProductTableModel)getCOrderTable().getModel());
	}
	
	private void removePayment() {
		removePayment((Payment)getPaymentsTableUtils().getSelection());
	}
	
	private void removePayment(Payment payment) {
		if(payment != null) {
			// Validaciones para eliminación de cheques
			if(MPOSPaymentMedium.TENDERTYPE_Check.equals(payment.getTenderType())){
				// Es posible eliminar el cheque si existe más de uno con
				// el mismo plazo
				if(getModel().getCheckDeadLineCount((CheckPayment)payment, false) <= 0){
					// Si es el único cheque con ese plazo, no es posible
					// eliminar este cheque si su plazo es requerido
					// obligatoriamente para otro cheque cargado
					if(getModel().isCheckDeadLineRequired((CheckPayment)payment)){
						errorMsg(MSG_CHECK_DEADLINE_REQUIRED);
						return;
					}
				}	
			}
			getOrder().removePayment(payment);
			getPaymentTableModel().fireTableDataChanged();
			getPaymentsTableUtils().refreshTable();
			
			// Se actualizan los totales y la info del medio de pago actual.
			updatePaymentsStatus();
			refreshPaymentMediumInfo();
			refreshBPartnerDiscount();
			// Se actualiza el esquema de vencimientos
			refreshPaymentTerm(getSelectedPaymentMedium());
			getPaymentsTableUtils().selectFirst();
		}
		getCRemovePaymentButton().setEnabled(getOrder().hasPayments());
	}
	
	private void completeOrder() {
		TimeStatsLogger.beginTask(MeasurableTask.POS_SAVE_DOCUMENTS);
		TimeStatsLogger.beginTask(MeasurableTask.POS_COMPLETE_ORDER);
		
		if(getFrame().isBusy()){
			return;
		}
		
		if(getOrder().getBusinessPartner() == null) {
			errorMsg(MSG_NO_BPARTNER_ERROR);
			return;
		}
		if(getOrder().getBusinessPartner().getLocationId() == 0) {
			errorMsg(MSG_NO_LOCATION_ERROR);
			return;
		}
		// Actualizar autorización de descuento manual general
		updateManualDiscountAuthorization((BigDecimal)getCGeneralDiscountPercText().getValue());
		// Autorizaciones al finalizar la venta
		getAuthDialog().authorizeOperation(UserAuthConstants.POS_FINISH_MOMENT);
		CallResult result = getAuthDialog().getAuthorizeResult(true);
		if (result != null && result.isError()) {
			if(!Util.isEmpty(result.getMsg(), true)){
				errorMsg(result.getMsg());
			}
			return;
		}
		
		//final Waiting waitingDialog = new Waiting(getFrame(),waitMsg + "...",false,60);
		
		SwingWorker worker = new SwingWorker() {
			
			private final String FISCAL_PRINT_ERROR = "FiscalPrintError";
			private String errorMsg = null;
			private String errorDesc = null;
				
			@Override
			public Object construct() {
				errorMsg = null;
				errorDesc = null;
				try {
					getModel().setDocActionStatusListener(docActionStatusListener);
					getModel().setFiscalPrintListeners(infoFiscalPrinter, infoFiscalPrinter);
					CPreparedStatement.setNoConvertSQL(true);
					getModel().completeOrder();
				} catch (InsufficientCreditException e) {
					errorMsg = MSG_INSUFFICIENT_CREDIT_ERROR;
					errorDesc = Util.isEmpty(e.getMessage()) ? e.getCause()
							.getMessage() : e.getMessage();
				} catch (InsufficientBalanceException e) {
					errorMsg = MSG_BALANCE_ERROR;
				} catch ( InvalidPaymentException e) {
					errorMsg = MSG_INVALID_PAYMENT_ERROR;
				} catch (InvalidProductException e) {
					errorMsg = MSG_NO_PRODUCT_ERROR;
				} catch (InvoiceCreateException e) {
					errorMsg = MSG_CANT_CREATE_TICKET_ERROR + ". " + MSG_INVOICE_CREATE_ERROR;
					errorDesc = getMsg(e.getMessage());
				} catch (FiscalPrintException e) {
					errorMsg = FISCAL_PRINT_ERROR;
				} catch (PosException e) {
					errorMsg = MSG_FATAL_ERROR;
					if (e.getMessage() != null && e.getMessage().length() > 0)
						errorDesc = getMsg(e.getMessage());
					e.printStackTrace();
				}
				finally
				{
					CPreparedStatement.setNoConvertSQL(false);
				}
				return errorMsg == null;
			}

			@Override
			public void finished() {
				boolean success = (Boolean)getValue();
				boolean fiscalPrintError = errorMsg != null && errorMsg.equals(FISCAL_PRINT_ERROR);
				if(success) {
					newOrder();
				} else if (!fiscalPrintError) {
					if(errorDesc == null)
						errorMsg(errorMsg);
					else
						errorMsg(errorMsg, errorDesc);
					//waitingDialog.doNotWait();
					//waitingDialog.setVisible(false);
					//getFrame().setEnabled(true);
				}
				if (!fiscalPrintError) {
					getFrame().setBusy(false);
					mNormal();
				}
			}
		};

		mWait();
		String waitMsg = getMsg("PrintingTicket") + ", " + getMsg("PleaseWait");
		getFrame().setBusyMessage(waitMsg);
		getFrame().setBusyTimer(4);
		getFrame().setBusy(true);
		
		//getFrame().setEnabled(false);
		//waitingDialog.setVisible(true);
		//waitingDialog.repaint();
		

		worker.start();
	}	
	
	private void newOrder() {
		if (infoFiscalPrinter != null) {
			infoFiscalPrinter.setVisible(false);
			infoFiscalPrinter.clearDetail();
		}
		getManualDiscountAuthOperation().setAuthorized(true);
		getAuthDialog().markAuthorized(UserAuthConstants.POS_FINISH_MOMENT, true);
		getModel().newOrder();
		loadBPartner(getModel().getDefaultBPartner());
		getOrderTableUtils().refreshTable();
		getPaymentsTableUtils().refreshTable();
		selectTab(0);
		clearComponent(getCClientText());
		clearComponent(getCClientLocationCombo());
		clearComponent(getCTaxIdText());
		clearComponent(getCAmountText());
		selectTenderType(MPOSPaymentMedium.TENDERTYPE_Cash);
		getCProductNameDetailLabel().setText("");
		getCCountText().setText("1");
		updateTotalAmount();
		updatePaymentsStatus();
		getCOrderLookup().setValue(null);
		getCOrderCustomerText().setText("");
		getCOrderDateText().setText("");
		getCGeneralDiscountPercText().setValue(BigDecimal.ZERO);
		updateAllowClose();
		updateStatusDB();
		TimeStatsLogger.endTask(MeasurableTask.POS_COMPLETE_ORDER);
	}
	
	protected Order getOrder() {
		return getModel().getOrder();
	}
	
	private void updatePaymentsStatus() {
		getModel().balanceValidate();
		BigDecimal totalAmt = getOrder().getOrderProductsTotalAmt();
		BigDecimal documentDiscountAmt = getOrder().getTotalDocumentDiscount().negate();
		BigDecimal toPayAmt = getOrder().getTotalAmount();
		BigDecimal paidAmt = getOrder().getPaidAmount();
		BigDecimal balance = getOrder().getBalance();
		BigDecimal change = getOrder().getTotalChangeAmt();
//		BigDecimal cashAmt = getOrder().getCashPaidAmount();
//		
//		if(balance.compareTo(BigDecimal.ZERO) > 0) {
//			change = paidAmt.subtract(toPayAmt);
//			if(change.compareTo(cashAmt) > 0)
//				change = cashAmt;
//		}
//		// Agregarle al cambio en efectivo, la devolución de efectivo de NC
//		change = change.add(getOrder().getCreditNoteChangeAmount());
		
		getCOrderTotalAmt().setValue(totalAmt);
		getCDocumentDiscountAmt().setValue(documentDiscountAmt);
		getCToPayText().setValue(toPayAmt);
		getCPaidText().setValue(paidAmt);
		getCBalanceText().setValue(balance);
		getCChangeText().setValue(change);
		updateTotalAmount();
		
		getCFinishPayButton().setEnabled(getModel().balanceValidate());
		updateStatusDB();
		setActionEnabled(PAY_ORDER_ACTION,true);
	}
	
	private void updateConvertedAmount() {
		BigDecimal amount = (BigDecimal)getCAmountText().getValue();
		if(MPOSPaymentMedium.TENDERTYPE_Cash.equals(getSelectedTenderType())) {
			int fromCurrencyId = ((Integer)getCCurrencyCombo().getValue()).intValue();
			BigDecimal convertedAmt = getModel().currencyConvert(amount,fromCurrencyId);
			
			getCConvertedAmountText().setValue(convertedAmt);
		}
	}
	
	private void selectTab(int index) {
		// Pestaña de Pedido
		if(index == 0) {
			getCPosTab().setEnabledAt(1,false);
			setActionEnabled(GOTO_INSERT_CARD, false);
			setActionEnabled(UPDATE_ORDER_PRODUCT_ACTION,true);
			setActionEnabled(GOTO_PAYMENTS_ACTION,true);
			setActionEnabled(PAY_ORDER_ACTION,false);
			setActionEnabled(ADD_PAYMENT_ACTION,false);
			setActionEnabled(SET_CUSTOMER_DATA_ACTION,false);
			setActionEnabled(SET_BPARTNER_INFO_ACTION,true);
			setActionEnabled(GOTO_ORDER,false);
			setActionEnabled(CHANGE_FOCUS_GENERAL_DISCOUNT, false);
			setActionEnabled(ADD_ORDER_ACTION,true);
			setActionEnabled(MOVE_ORDER_PRODUCT_FORWARD, true);
			setActionEnabled(MOVE_ORDER_PRODUCT_BACKWARD, true);
			setActionEnabled(CHANGE_FOCUS_PRODUCT_ORDER, true);
			setActionEnabled(REMOVE_PAYMENT_ACTION, false);
			setActionEnabled(CHANGE_FOCUS_CUSTOMER_AMOUNT, false);
			setActionEnabled(CANCEL_ORDER, true);
			getStatusBar().setStatusLine(MSG_POS_ORDER_STATUS);
			getCPosTab().setTitleAt(0, MSG_ORDER);
			// Muestra el panel de total dentro del panel superior del pedido.
			getCPaymentTopPanel().remove(getCTotalPanel());
			getCOrderTopPanel().add(getCTotalPanel());
			
		// Pestaña de Pago	
		} else { // getCPosTab().getSelectedIndex()==1
			getCPosTab().setEnabledAt(1,true);
			setActionEnabled(UPDATE_ORDER_PRODUCT_ACTION,false);
			setActionEnabled(GOTO_PAYMENTS_ACTION,false);
			setActionEnabled(PAY_ORDER_ACTION,true);
			setActionEnabled(ADD_PAYMENT_ACTION,true);
			setActionEnabled(SET_CUSTOMER_DATA_ACTION,true);
			setActionEnabled(SET_BPARTNER_INFO_ACTION,false);
			setActionEnabled(GOTO_ORDER,true);
			setActionEnabled(ADD_ORDER_ACTION,false);
			setActionEnabled(CHANGE_FOCUS_PRODUCT_ORDER, false);
			setActionEnabled(MOVE_PAYMENT_FORWARD, true);
			setActionEnabled(MOVE_PAYMENT_BACKWARD, true);
			setActionEnabled(REMOVE_PAYMENT_ACTION, true);
			setActionEnabled(CHANGE_FOCUS_CUSTOMER_AMOUNT, true);
			setActionEnabled(CHANGE_FOCUS_GENERAL_DISCOUNT, true);
			setActionEnabled(CANCEL_ORDER, true);
			setActionEnabled(GOTO_INSERT_CARD, true);
			getStatusBar().setStatusLine(MSG_POS_PAYMENT_STATUS);
			// Se carga el cliente que tenga asignado el pedido. (pedidos pre creados)
			if(getOrder().getBusinessPartner() != null) {
				getCClientText().setValue(getOrder().getBusinessPartner().getId());
				loadBPartner(getOrder().getBusinessPartner().getId());
			// Se carga el ciente por defecto de la configuración del TPV
			} else {
				getCClientText().setValue(getModel().getDefaultBPartner());
				loadBPartner(getModel().getDefaultBPartner());
			}
			getCPosTab().setTitleAt(0, MSG_ORDER+" " + KeyUtils.getKeyStr(getActionKeys().get(GOTO_ORDER)));
			// Actualiza la info adicional del medio de pago porque puede haber cambiado
			// algún artículo y es necesario recalcular descuentos y cuotas.
			refreshPaymentMediumInfo();
			getCTenderTypeCombo().requestFocus();
			// Muestra el panel del total dentro del panel superior de pagos
			getCOrderTopPanel().remove(getCTotalPanel());
			getCPaymentTopPanel().add(getCTotalPanel());
			Dimension dim = new Dimension(getCTotalPanel().getWidth(), getCClientBorderPanel().getHeight());
			getCTotalPanel().setPreferredSize(dim);
			getCTotalPanel().setMaximumSize(dim);
			
		}
		if(getCPosTab().getSelectedIndex() != index) {
			getCPosTab().setSelectedIndex(index);
		}
	}

	private boolean isPosConfigError() {
		return posConfigError;
	}

	private void setPosConfigError(boolean posConfigError) {
		this.posConfigError = posConfigError;
	}

	protected PoSMsgRepository getMsgRepository() {
		return msgRepository;
	}

	protected void setMsgRepository(PoSMsgRepository msgRepository) {
		this.msgRepository = msgRepository;
	}
	
	public String getMsg(String name) {
		return getMsgRepository().getMsg(name);
	}
	
	protected ImageIcon getImageIcon(String name) {
		//String path = "/org/openXpertya/images/" + name;
		//return new ImageIcon(getClass().getResource(path));
		return ImageFactory.getImageIcon(name);
	}
	
	protected void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	protected void errorMsg(String msg, String subMsg) {
		ADialog.error(getWindowNo(),this,msg,subMsg);
	}
	
	protected boolean askMsg(String msg, String subMsg) {
		return ADialog.ask(getWindowNo(),this,msg,subMsg);
	}

	protected boolean askMsg(String msg) {
		return askMsg(msg,null);
	}

	protected void clearComponent(JComponent component) {
		if(component.getClass().equals(VLookup.class)) {
			VLookup lookup = (VLookup) component;
			lookup.setValue(null);
			if(lookup.isMandatory())
				lookup.setBackground(true);
		} else if(component.getClass().equals(CComboBox.class)) {
			CComboBox combo = (CComboBox) component;
			combo.removeAllItems();
			if(combo.isMandatory())
				combo.setBackground(true);
		} else if(component.getClass().equals(CTextField.class)) {
			CTextField text = (CTextField) component;
			text.setText("");
			if(text.isMandatory())
				text.setBackground(true);
		} else if(component.getClass().equals(VNumber.class)) {
			VNumber number = (VNumber) component;
			number.setValue(null);
		}

	}
	
	/**
	 * Carga el pedido de cliente y muestra los datos del encabezado en los controles de la
	 * interfaz. No carga las líneas del pedido de cliente al pedido de TPV, esto se realiza
	 * luego mediante el método {@link #addCustomerOrder()}
	 * @param orderID ID del pedido de cliente
	 */
	private void loadCustomerOrder(int orderID) {
		try {
			mWait();
			Order customerOrder = getModel().loadCustomerOrder(orderID);
			setOrderDate(customerOrder.getDate());
			getCOrderCustomerText().setValue(customerOrder.getBusinessPartner().getName());
			getCOrderCustomerText().setCaretPosition(0);
			getCAddOrderButton().setEnabled(true);
			
		} catch (InvalidOrderException e) {
			errorMsg(MSG_INVALID_ORDER, e.getDescriptionMsg());
		} catch (PosException e) {
			errorMsg(MSG_INVALID_ORDER);
		} finally {
			mNormal();
		}
	}
	
	protected void refreshOrderProductsTable() {
		getOrderTableModel().fireTableDataChanged();
		getOrderTableUtils().refreshTable();
		updateTotalAmount();
	}
	
	protected TableCellRenderer getNumberCellRendered(final NumberFormat numberFormat) {
		TableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
				JLabel cmp = (JLabel)super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
				BigDecimal amount = (BigDecimal)arg1;
				cmp.setText(numberFormat.format(amount));
				cmp.setHorizontalAlignment(JLabel.RIGHT);
				return cmp;
			};
		};
		return amountRenderer;
	}

	private void setOrderDate(Date date) {
		getCOrderDateText().setValue(dateFormat.format(date));
	}

    private void mWait() {
    	getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    
    private void mNormal() {
    	getFrame().setCursor(Cursor.getDefaultCursor());
    }
    
    private void recalculateOrderTotal() {
    	getModel().recalculateOrderTotal();
    	updatePaymentsStatus();
    	updateTotalAmount();
    }
    
    private void openBPInfoDialog(){
    	BPartnerDialog dialog = new BPartnerDialog(this, true);
    	AEnv.positionCenterScreen(dialog);
		dialog.setModal(true);
		dialog.setVisible(true);
    }
    
  
    protected DocActionStatusListener docActionStatusListener = new DocActionStatusListener() {

		public void docActionStatusChanged(DocActionStatusEvent event) {
			// Evento: ImpresiÃ³n fiscal de documento. 
			if(event.getDocActionStatus() == DocActionStatusEvent.ST_FISCAL_PRINT_DOCUMENT) {
		    	FiscalDocumentPrint fdp = (FiscalDocumentPrint)event.getParameter(0);
		    	// Se setea  la ventana de informaciÃ³n tanto como listener de estado de la 
		    	// impresora como el estado de la impresiÃ³n del documento.
		    	fdp.addDocumentPrintListener(infoFiscalPrinter);
		    	fdp.setPrinterEventListener(infoFiscalPrinter);
		    	// Se efectÃºa la referencia cruzada entre el Impresor y la
		    	// ventana de informaciÃ³n.
		    	infoFiscalPrinter.setFiscalDocumentPrint(fdp);
		    	// Se muestra la ventana en el thread de Swing.
		    	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						infoFiscalPrinter.setVisible(true);
					}
		    	});
			}
			
		}
    	
    };
    
	// Se crea la ventana que muestra el estado de la impresora fiscal.
	protected AInfoFiscalPrinter infoFiscalPrinter = null;


    /**
     * Agrega el pedido de cliente actualmente cargado al pedido del TPV (agrega los artículos
     * del pedido de cliente al pedido del TPV).
     */
    private void addCustomerOrder() {
    	if (getModel().getCustomerOrder() == null) {
    		return;
    	}
    	TimeStatsLogger.beginTask(MeasurableTask.POS_ADD_CUSTOMER_ORDER);
    	try {
			mWait();
    		Integer newBPartnerID = null;
			// Si el pedido que se agrega tiene una EC diferente a la actual entonces
    		// se guarda el ID de esa EC para luego asignarla como EC actual.
    		if (getModel().getCustomerOrder().getBusinessPartner().getId() != getOrder()
					.getBusinessPartner().getId()) {
				newBPartnerID = getModel().getCustomerOrder()
						.getBusinessPartner().getId(); 
    		}
    		if (getModel().isCopyRep()==true&&getModel().validateCopyEntity()==true) {
    			getOrder().setOrderRep(getModel().getCustomerOrder().getOrderRep());    			
    			getModel().setCopyRep(false);
    		}
			getModel().addCustomerOrder();
    		if (newBPartnerID != null) {
    			loadBPartner(newBPartnerID);
    		}
	    	clearCustomerOrder();
	    	refreshOrderProductsTable();
    	} catch (InvalidOrderException e) {
    		errorMsg(e.getDescriptionMsg());
    		clearCustomerOrder();
    	} finally {
    		mNormal();
    		setFocus(getCOrderLookup());
	    	TimeStatsLogger.endTask(MeasurableTask.POS_ADD_CUSTOMER_ORDER);
    	}
    }
    
    /**
     * Limpia los controles que muestran la información de un pedido de cliente
     * cargado.
     */
    private void clearCustomerOrder() {
		getCOrderDateText().setValue(null);
		getCOrderCustomerText().setValue(null);
		getCOrderLookup().setValue(null);
		getCAddOrderButton().setEnabled(false);
		getModel().clearCustomerOrder();
    }
    
    /**
     * Reliza el requerimiento de foco de un component bajo el hilo
     * de Swing.
     * @param component Componente al cual se le quiere dar el foco. 
     */
    private void setFocus(final JComponent component) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				component.requestFocus();
			}
		});
    }
    
    /**
     * Selecciona un tipo de pago en el combo de tipos de pagos
     * @param selectTenderType Tipo de pago a seleccionar
     */
    private void selectTenderType(String selectTenderType) {
    	boolean found = false;
    	for(int i = 0; i < getCTenderTypeCombo().getItemCount(); i++) {
    		ValueNamePair tenderType = (ValueNamePair)getCTenderTypeCombo().getItemAt(i);
    		if (selectTenderType.equals(tenderType.getValue())) {
    			getCTenderTypeCombo().setSelectedIndex(i);
    			found = true;
    			break;
    		}
    	}
		// Si no se encontró el tipo de pago seleccionado, se selecciona el
		// primero que exista en el combo
    	if(!found && getCTenderTypeCombo().getItemCount() > 0){
    		ValueNamePair tenderType = (ValueNamePair)getCTenderTypeCombo().getItemAt(0);
    		selectTenderType = tenderType.getValue();
    		getCTenderTypeCombo().setSelectedIndex(0);
    	}
    	
    	// Carga el tipo de pago seleccionado (tal como se hace cuando el usuario
    	// modifica el valor en el combo manualmente)
    	loadTenderType(selectTenderType);
    }
    
    /**
     * Realiza la carga de un tipo de pago presentando los controles 
     * personalizados del tipo en la interfaz gráfica.
     * @param tenderType Tipo de pago seleccionado.
     */
    private void loadTenderType(String tenderType) {
		// Obtiene el panel que muestra los parámetros personalizados del tipo de pago
    	// y quita todos los controles actuales para insertar los del tipo seleccionado.
    	JPanel paramsPanel = getCTenderTypeParamsContentPanel();
		paramsPanel.removeAll();
		setCurrencySelectionEnabled(false);

		// Carga todos los medios de pago que sean del tipo de pago seleccionado	
    	loadTenderTypePaymentMediums(tenderType);
		
    	// Realiza la carga del medio de pago seleccionado por defecto en el combo (siempre
    	// se selecciona el primero automáticamente si la lista contiene alguno)
    	loadPaymentMedium(true);

    	Env.setContext(Env.getCtx(), windowNo, "TenderType", tenderType);
    	
		// Refresca la interfaz gráfica.
		paramsPanel.repaint();
		paramsPanel.revalidate();
    }
    
    /**
     * Carga todos los medios de pago que sean del tipo de pago parámetro
     * @param tenderType tipo de pago
     */
    private void loadTenderTypePaymentMediums(String tenderType){
    	// Carga todos los medios de pago que sean del tipo de pago seleccionado	
    	getCPaymentMediumCombo().removeAllItems();
    	List<PaymentMedium> paymentMediums = getModel().getPaymentMediums();
    	for (PaymentMedium paymentMedium : paymentMediums) {
			if (paymentMedium.getTenderType().equals(tenderType)) {
				getCPaymentMediumCombo().addItem(paymentMedium);
			}
		}
    	// Verificar las configuraciones adicionales de entidad comercial
    	refreshBPartnerCreditPaymentMedium(tenderType);
    }

	/**
	 * Carga todos los medios de pago que sean del tipo de pago parámetro y
	 * posean alguna de las entidades financieras de la lista parámetro
	 * 
	 * @param tenderType
	 *            tipo de pago
	 * @param entidadesFinancieras
	 *            entidades financieras a verificar, si la lista está vacía,
	 *            carga los del tipo de pago parámetro
	 */
    private void loadTenderTypePaymentMediums(String tenderType, List<EntidadFinanciera> entidadesFinancieras){
    	if(entidadesFinancieras.isEmpty()){
    		loadTenderTypePaymentMediums(tenderType);
    	}
    	else{
	    	getCPaymentMediumCombo().removeAllItems();
	    	List<PaymentMedium> paymentMediums = getModel().getPaymentMediums();
	    	for (PaymentMedium paymentMedium : paymentMediums) {
				// El medio de pago tiene el mismo tipo de pago y la entidad
				// financiera que contiene existe en la lista parámetro
				if (paymentMedium.getTenderType().equals(tenderType)
						&& paymentMedium.getEntidadFinanciera() != null
						&& entidadesFinancieras.contains(paymentMedium.getEntidadFinanciera())) {
					getCPaymentMediumCombo().addItem(paymentMedium);
				}
			}
    	}
    }
    
    /**
     * Actualiza los componentes que contienen los parámetros específicos para un
     * tipo de pago determinado.
     * @param tenderType Tipo de pago seleccionado.
     */
    private void updateTenderTypeComponents(String tenderType) {
    	// Realiza las actividades necesarias para cada tipo de pago
		if (MPOSPaymentMedium.TENDERTYPE_Check.equals(tenderType)) {
			updateCheckTenderTypeComponents();
		} else if (MPOSPaymentMedium.TENDERTYPE_Credit.equals(tenderType)) {
			updateCreditTenderTypeComponents();
		} else if (MPOSPaymentMedium.TENDERTYPE_CreditCard.equals(tenderType)) {
			updateCreditCardTenderTypeComponents();
		} else if (MPOSPaymentMedium.TENDERTYPE_Cash.equals(tenderType)) {
			updateCashTenderTypeComponents();
		} else if (MPOSPaymentMedium.TENDERTYPE_CreditNote.equals(tenderType)) {
			updateCreditNoteTenderTypeComponents();
		} else if (MPOSPaymentMedium.TENDERTYPE_DirectDeposit.equals(tenderType)) {
			updateTransferTenderTypeComponents();
		}
    }
    
    /**
     * Carga los parámetros del medio de pago seleccionado. Si no hay ningún medio
     * de pago seleccionado en el combo el método no realiza ningún cambio.
     */
    private void loadPaymentMedium(boolean updateComponents) {
    	PaymentMedium paymentMedium = getSelectedPaymentMedium();
    	getCAmountText().setReadWrite(paymentMedium != null);
    	clearPaymentMediumInfo(updateComponents);
    	
    	// Nada que cargar
    	if (paymentMedium == null) {
    		return;
    	}
    	// Asigna la moneda
    	getCCurrencyCombo().setValue(paymentMedium.getCurrencyID());
    	
 	   	// Si es un medio de pago de Tarjeta de Crédito entonces se cargan
    	// en el combo de planes todos los planes asociados a la tarjeta. 
    	// Los planes están asociados al medio de pago. 
    	// Además del posnet del TPV config
    	if (paymentMedium.isCreditCard()) {
    		getCCreditCardPlanCombo().removeAllItems();
    		for (EntidadFinancieraPlan plan : paymentMedium.getCreditCardPlans()) {
				getCCreditCardPlanCombo().addItem(plan);
			}
    		// Posnet
    		getCPosnetText().setValue(getModel().getPoSConfig().getPosnet());
    	}
    	// Para Tarjetas y Cheques se carga el Banco asociado al MP en el combo de Bancos.
    	// Si tiene banco el MP entonces no puede ser modificado por el usuario.
    	if ((paymentMedium.isCheck() || paymentMedium.isCreditCard())
    			&& paymentMedium.hasBank()) {
    		getCBankCombo().setValue(paymentMedium.getBank());
    		getCBankCombo().setReadWrite(false);
    	// Si no tiene banco el combo es editable y deberá elegir una opción.
    	} else {
    		getCBankCombo().setValue(null);
    		getCBankCombo().setReadWrite(true);
    	}
    
    	// Para Crédito se muestra la información de esquemas de vencimiento
    	if(paymentMedium.isCredit()){
			// Refrescar el esquema de vencimientos
    		refreshPaymentTerm(paymentMedium);
    	}
    	
    	// Muestra la información adicional del medio de pago.
    	loadPaymentMediumInfo();
    	
    	// Actualiza los parámetros específicos del tipo de pago que tiene
    	// el medio de pago seleccionado.
    	if(updateComponents){
    		updateTenderTypeComponents(paymentMedium.getTenderType());
    	}
    }
    
    /**
     * Borra el contenido de los controles que muestran la información
     * adicional de los medios de pago.
     */
    private void clearPaymentMediumInfo(boolean updateComponents) {
    	getCPaymentDiscountText().setText("");
    	getCPaymentToPayAmt().setValue(null);
    	getCCreditCardCuotas().setValue(null);
    	getCCreditCardCuotaAmt().setValue(null);
    	if(updateComponents){
    		getCCreditCardInfoPanel().setVisible(false);
    	}
    }

	/**
	 * Carga la información adicional de un medio de pago en los componentes
	 * gráficos correspondientes.
	 * 
	 * @param paymentMedium
	 */
    private void loadPaymentMediumInfo() {
    	PaymentMedium paymentMedium = getSelectedPaymentMedium();
    	if (paymentMedium == null) {
    		return;
    	}
    	
    	// Obtiene el esquema de descuento asociado al MP
    	DiscountSchema discountSchema = paymentMedium.getDiscountSchema();
    	
    	// Si el MP es tarjeta de crédito, entonces el esquema de descuento se obtiene
    	// desde el plan de tarjeta seleccionado actualmente.
		if (paymentMedium.isCreditCard()) {
			EntidadFinancieraPlan currentPlan = getSelectedCreditCardPlan();
			if (currentPlan != null) {
				discountSchema = currentPlan.getDiscountSchema();
				
				// Muestra los datos específicos de tarjetas de crédito
				getCCreditCardCuotas().setValue(currentPlan.getCoutasPago());
				getCCreditCardCuotaAmt().setValue(BigDecimal.ZERO);
			} else {
				return;
			}
		}
		
		// Muestra el esquema de descuento y monto final del pago (cero por defecto)
		if (!getOrder().isPaymentMediumDiscountApplicable()) {
			discountSchema = null;
		}
		
		// Indica al pedido para que asuma o no que existe un descuento general
		// (que todavia no ha sido agregado). Este llamado además recalculará
		// todos los descuentos. Con esto tenemos el cuenta el caso de que la EC
		// tenga descuento pero que tenga prioridad el descuento del medio de
		// cobro.
		getOrder().setAssumeGeneralDiscountAdded(discountSchema != null);
		
		getCPaymentDiscountText().setText(getDiscountSchemaDescription(discountSchema));
		getCPaymentDiscountText().setCaretPosition(0);
		getCPaymentToPayAmt().setValue(null);

		// Actualiza los datos adicionales del medio de pago
		refreshPaymentMediumInfo();
		// Actualiza los datos del Dto de EC y los totales (ya que es posible
		// que se hayan modificado los descuentos)
		refreshBPartnerDiscount();
		updatePaymentsStatus();
    }

	/**
	 * Recalcula descuentos / recargos y toda la información adicional del medio
	 * de pago para ser mostrada en los componentes gráficos
	 */
    private void refreshPaymentMediumInfo() {
    	PaymentMedium paymentMedium = getSelectedPaymentMedium();
    	if (paymentMedium == null) {
    		return;
    	}
    	
		// Calcula el importe a pagar (aplicando descuentos / recargos del
		// medio de pago actualmente seleccionad) y lo muestra en el
		// componente.
		BigDecimal paymentToPayAmt = getOrder().getToPayAmount(
				getSelectedPaymentMediumInfo());
		
		getCPaymentToPayAmt()
				.setValue(paymentToPayAmt.compareTo(BigDecimal.ZERO) > 0 
							? paymentToPayAmt
							: null);

		// Si es un pago con tarjeta de crédito se calcula y muestra el importe
		// de cada cuota.
		if (paymentMedium.isCreditCard()) {
			EntidadFinancieraPlan plan = getSelectedCreditCardPlan();
			getCAmountText().setValue(
					getCAmountText().getValue() != null ? getCAmountText()
							.getValue()
							: (paymentToPayAmt != null ? paymentToPayAmt
									: null));
			
			// El importe de la cuota se calcula en base al importe del pago
			// ingresado por el usuario
			BigDecimal cuotaAmt = getPaymentAmount().divide(new BigDecimal(plan
					.getCoutasPago()), 10, BigDecimal.ROUND_HALF_UP);
			getCCreditCardCuotaAmt().setValue(cuotaAmt);
		}
		// Si estamos pagando con un crédito, entonces actualizo el saldo que
		// quedará del crédito seleccionado
		if(paymentMedium.isCreditNote()){
			updateCreditNoteBalance();
		}
    }
    
    /**
     * Selecciona el esquema de vencimientos inicial default
     */
    private void loadDefaultPaymentTerm(){
    	getCPaymentTermCombo().setSelectedItem(getModel().getDefaultInitialPaymentTerm());
    }
    
    /**
     * Actualiza el esquema de vencimientos
     */
    private void refreshPaymentTerm(PaymentMedium paymentMedium){
		getCPaymentTermCombo().removeAllItems();
		for (PaymentTerm paymentTerm : getModel().getPaymentTerms(paymentMedium)) {
			getCPaymentTermCombo().addItem(paymentTerm);
		}
		getCPaymentTermCombo().setReadWrite(true);
    }
    
    
    /**
     * @return El medio de pago seleccionado en el ComboBox.
     */
    protected PaymentMedium getSelectedPaymentMedium() {
    	return (PaymentMedium)getCPaymentMediumCombo().getSelectedItem();
    }
    
    /**
     * @return El plan de tarjeta seleccionado en el ComboBox
     * (solo para medios de pago de tipo tarjeta)
     */
    protected EntidadFinancieraPlan getSelectedCreditCardPlan() {
    	return (EntidadFinancieraPlan)getCCreditCardPlanCombo().getSelectedItem();
    }

	/**
	 * @return <ul>
	 *         <li>El valor de {@link #getSelectedCreditCardPlan()} si el medio
	 *         de pago actual es de tipo Tarjeta de Crédito</li>
	 *         <li>El valor de {@link #getSelectedPaymentMedium()} si el medio
	 *         de pago actual es de cualquier otro tipo</li>
	 *         </ul>
	 */
    protected IPaymentMediumInfo getSelectedPaymentMediumInfo() {
    	IPaymentMediumInfo value = null;
    	PaymentMedium paymentMedium = getSelectedPaymentMedium();
    	if (paymentMedium != null) {
    		if (paymentMedium.isCreditCard()) {
    			value = getSelectedCreditCardPlan();
    		} else {
    			value = paymentMedium;
    		}
    	}
    	return value;
    }
    
    /**
     * Valida la fecha de acreditación de un Cheque
     * @param acctDate Fecha de acreditación
     * @param paymentMedium Medio de pago seleccionado
     * @return <code>true</code> si la fecha es válida, <code>false</code> en caso
     * contrario 
     */
    private boolean validateCheckAcctDate(Date acctDate, PaymentMedium paymentMedium) {
		// Se  debe ingresar la fecha de vencimiento
    	if (acctDate == null) {
			errorMsg(MSG_FILL_MANDATORY, MSG_ACCT_DATE);
			return false;
		}
		
    	// La fecha de vto no puede ser mayor a la fecha actual sumada la cantidad de días
    	// del plazo de cobro del medio de pago.
    	// Condición: 
    	//      Fecha Vto <= Fecha Actual + Días Plazo Cobro 
		Calendar auxCalendar = Calendar.getInstance();
		auxCalendar.setTime(TODAY);
		auxCalendar.add(Calendar.DATE, paymentMedium.getCheckDeadLine());
		Date maxAcctDate = auxCalendar.getTime();
		
		if (acctDate.compareTo(maxAcctDate) > 0) {
			errorMsg(MSG_INVALID_CHECK_ACCTDATE + ":", 
				DisplayType.getDateFormat(DisplayType.Date).format(maxAcctDate));
			return false;
		}

    	return true;
    }
    
    /**
     * Carga los datos adicionales de una nota de crédito seleccionada
     * @param invoiceID ID de la nota de crédito
     */
    private void loadCreditNote(int invoiceID) {
    	// Carga el campo con el importe pendiente del crédito.
    	BigDecimal availableAmt = getModel().getCreditAvailableAmt(invoiceID);
    	availableAmt = availableAmt == null ? BigDecimal.ZERO : availableAmt;
    	getCCreditNoteAvailableText().setValue(availableAmt);
    	// Carga el campo de importe con el importe pendiente en caso de que el usuario
    	// no haya ingresado un valor aún
    	BigDecimal amount = (BigDecimal)getCAmountText().getValue();
    	if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
    		// Si el pendiente es mayor al saldo entonces se carga el saldo
    		// en el campo de importe.
    		if (availableAmt.compareTo(getOrder().getBalance().negate()) <= 0) {
    			getCAmountText().setValue(availableAmt);
    		} else {
    			if (getOrder().getBalance().negate().compareTo(BigDecimal.ZERO) > 0) {
    				getCAmountText().setValue(getOrder().getBalance().negate());
    			} else {
    				getCAmountText().setValue(null);
    			}
    		}
    	}
		// Agregar el saldo del crédito que quedará al final de la operación
		updateCreditNoteBalance();
    }
    
    /**
     * Valida si el monto de un pago no supera el monto a pagar del pedido. En caso de
     * superarlo asigna el monto a pagar o el paymentLimitAmount en el campo de importe
     * según corresponda
     * @param amount Importe del pago en cuestión
     * @param paymentLimitAmount Importe límite del pago. Este es el importe máximo que
     * puede usarse para el pago. Por ejemplo en Notas de Crédito, este valor sería
     * el monto pendiente de asignación. Si el pago no tiene límite este parámetro debe
     * ser <code>null</code>. Si el importe del pago supera el monto a pagar y el
     * pago tiene un límite, se asigna este límite al campo de importe. Si no tiene límite
     * se asigna entonces el monto a pagar del pedido.
     * @return Devuelve <code>true</code> si el importe es válido, <code>false</code>
     * en caso contrario.
     */
    private boolean validatePaymentAmount(BigDecimal amount, BigDecimal paymentLimitAmount) {
    	boolean valid = true;
		// El importe no puede superar el pendiente a pagar según el medio de pago.
		//BigDecimal toPay = getOrder().getToPayAmount();
    	BigDecimal toPay = getPaymentToPayAmt();
		if(amount.compareTo(toPay) > 0) {
			if (paymentLimitAmount == null || toPay.compareTo(paymentLimitAmount) <= 0) {
				getCAmountText().setValue(toPay);
			} else {
				getCAmountText().setValue(paymentLimitAmount);
			}
			valid = false;
			refreshPaymentMediumInfo();
		}
    	return valid;
    }
    
    protected void updateCreditNoteBalance(){
    	BigDecimal availableCreditNoteAmt = (BigDecimal)getCCreditNoteAvailableText().getValue();
    	BigDecimal amt = (BigDecimal) getCAmountText().getValue();
		BigDecimal balanceCreditNote = (availableCreditNoteAmt == null || availableCreditNoteAmt
				.compareTo(BigDecimal.ZERO) == 0) || amt == null ? null
				: availableCreditNoteAmt.subtract(amt);
    	// Agregar el saldo del crédito que quedará al final de la operación
		getCCreditNoteBalanceText().setValue(
				balanceCreditNote == null ? null : (balanceCreditNote
						.compareTo(BigDecimal.ZERO) >= 0 ? balanceCreditNote
						: BigDecimal.ZERO));
		getCCreditNoteCashReturnCheck().setVisible(
				balanceCreditNote != null
						&& balanceCreditNote.compareTo(BigDecimal.ZERO) > 0);
		getCCreditNoteCashReturnAmtText().setValue(balanceCreditNote);
    }
    
    /**
     * @return Devuelve el nombre del banco seleccionado en el Combo de bancos.
     *  Si no hay ningún banco seleccionado devuelve null
     */
    protected String getSelectedBankName() {
    	String bankName = null;
    	if (getCBankCombo().getValue() != null) {
    		bankName = getCBankCombo().getDisplay();
    	}
    	return bankName;
    }
    
    /**
     * Limpia la selección del combo de bancos dependiendo del medio de pago
     * seleccionado.
     */
    private void clearBankCombo() {
    	PaymentMedium paymentMedium = getSelectedPaymentMedium();
    	if (paymentMedium == null || !paymentMedium.hasBank()) {
    		getCBankCombo().setValue(null);
    	}
    }

	/**
	 * Muestra el panel de parámetros de un medio de pago junto con un panel de
	 * información adicional (opcional)
	 * 
	 * @param paramsPanel Panel de parámetros a mostrar
	 * @param infoPanel Panel de información adicional. Puede ser <code>null</code>
	 */
    private void showTenderTypeParamsPanel(JPanel paramsPanel, JPanel infoPanel) {
		getCTenderTypeParamsContentPanel().add(paramsPanel, BorderLayout.NORTH);
		if (infoPanel != null) {
			infoPanel.setVisible(true);
		}
    }

    /**
     * Devuelve la descripción a mostrar para un esquema de descuento. Si el esquema es <code>null</code> devuelve un String especial.
     * @param discountSchema
     * @return
     */
    private String getDiscountSchemaDescription(DiscountSchema discountSchema) {
    	return discountSchema == null ? " - " + MSG_NONE + " - " : discountSchema.getName();
    }

	/**
	 * @return El importe del pago ingresado por el usuario. Si el
	 *         usuario no ingresó ningún importe devuelve cero.
	 */
    protected BigDecimal getPaymentAmount() {
    	BigDecimal amount = (BigDecimal)getCAmountText().getValue();
    	return amount == null ? BigDecimal.ZERO : amount;
    }
    
	/**
	 * @return El importe a pagar según el medio de pago actual
	 */
    public BigDecimal getPaymentToPayAmt() {
    	BigDecimal amount = (BigDecimal)getCPaymentToPayAmt().getValue();
    	return amount == null ? BigDecimal.ZERO : amount;
    }
    
    private Product getLoadedProduct() {
		return loadedProduct;
	}

	private void setLoadedProduct(Product loadedProduct) {
		this.loadedProduct = loadedProduct;
	}

	/**
	 * Realiza el movimiento hacia adelante o atrás de la selección de una
	 * grilla.
	 * 
	 * @param forward
	 *            <code>true</code> para avanzar una fila, <code>false</code>
	 *            para retroceder.
	 */
	private void moveTableSelection(JTable table, boolean forward) {
		if (table.getRowCount() == 0) {
			return;
		}
		int srow = table.getSelectedRow();
		if (srow == -1) {
			srow = 0;
		} else {
			srow = forward ? srow+1 : srow-1;
			if (srow < 0) {
				srow = table.getRowCount() - 1;
			} else if (srow >= table.getRowCount()) {
				srow = 0;
			}
		}
		table.setRowSelectionInterval(srow, srow);
	}

	private void doRequestFocus(final JComponent component) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				component.requestFocus();
				
			}
		});
	}
	
	@Override
	public void unlockUI(ProcessInfo pi) {
		getFrame().setBusy(false);
	}
	
	@Override
	public void lockUI(ProcessInfo pi) {
		getFrame().setBusy(true);
	}
	
	@Override
	public boolean isUILocked() {
		return false;
	}
	
	@Override
	public void executeASync(ProcessInfo pi) {
		
	}
	
	protected void createInfoFiscalPrinter() {
		infoFiscalPrinter = new AInfoFiscalPrinter(
				null,
				getWindowNo(),
				Msg.parseTranslation(Env.getCtx(),"@PrintingFiscalDocument@")		
		);
		
		infoFiscalPrinter
				.setDialogActionListener(new AInfoFiscalPrinter.DialogActionListener() {
			
			@Override
			public void actionVoidPerformed() {
				// Anulación de los documentos.
				voidDocuments();
			}
			
			@Override
			public void actionReprintFinished() {
				// Al finalizar una reimpresión de ticket, se
				// reestablece la interfaz para un nuevo pedido
				newOrder();
				getFrame().setBusy(false);
				mNormal();
			}
		});
		
		infoFiscalPrinter.setReprintButtonActive(true);
		infoFiscalPrinter.setVoidButtonActive(true);
		infoFiscalPrinter.setOkButtonActive(false);
	}

	/**
	 * Invoca la anulación de los documentos generados debido a un error en la
	 * impresión fiscal
	 */
	private void voidDocuments() {
		SwingWorker worker = new SwingWorker() {
			
			private String errorMsg = null;
			
			@Override
			public Object construct() {
				try {
					// Si no está autorizado para anular comprobantes, entonces
					// que autorice
					if(!getModel().getPoSConfig().isVoidDocuments()){
						AuthOperation authOperation = new AuthOperation(
								UserAuthConstants.POS_VOID_DOCUMENTS_UID,
								getMsgRepository()
										.getMsg(UserAuthConstants
												.getProcessValue(UserAuthConstants.POS_VOID_DOCUMENTS_UID)),
								UserAuthConstants.POS_VOID_DOCUMENT);
						getAuthDialog().addAuthOperation(authOperation);
						getAuthDialog().authorizeOperation(UserAuthConstants.POS_VOID_DOCUMENT);
						CallResult result = getAuthDialog().getAuthorizeResult(true);
						if(result == null){
							return false;
						}
						if(result.isError()){
							if(!Util.isEmpty(result.getMsg(), true)){
								errorMsg(result.getMsg());
							}
							return false;
						}
					}
					getModel().voidDocuments();
				} catch (PosException e) {
					errorMsg = e.getMessage();
				}
				return errorMsg == null;
			}

			@Override
			public void finished() {
				boolean success = (Boolean)getValue();
				if (!success) {
					if(!Util.isEmpty(errorMsg, true)){
						errorMsg(errorMsg);
					}
					
					if (askMsg(MSG_RETRY_VOID_INVOICE, MSG_RETRY_VOID_INVOICE_INFO
							+ (getModel().getPoSConfig().isPosJournal() ? " "
									+ MSG_RETRY_VOID_INVOICE_INFO_POS_JOURNAL
									: ""))) {
						// Re intenta anular los documentos.
						voidDocuments();
					} else {
						newOrder();
						getFrame().setBusy(false);
						mNormal();
					}
				} else {
					newOrder();
					getFrame().setBusy(false);
					mNormal();
					getStatusBar().setStatusLine(MSG_VOID_INVOICE_OK);
				}
			}
		};
		
		String waitMsg = getMsg("VoidingInvoice") + ", " + getMsg("PleaseWait");
		getFrame().setBusyMessage(waitMsg);
		getFrame().setBusyTimer(4);
		getFrame().setBusy(true);

		worker.start();
	}
	
//	protected void manageMaxCashReturnValue(BigDecimal cashReturnAmt){
//		getCCashRetunAuthPanel().getAuthPanel().setVisible(
//				getModel().isCashReturnedSurpassMax(cashReturnAmt));
//	}
	
	public User getUser(int userID){
		return getModel().getUser(userID);
	}

	public void setAuthDialog(AuthorizationDialog authorizationDialog) {
		this.authDialog = authorizationDialog;
	}

	public AuthorizationDialog getAuthDialog() {
		return authDialog;
	}
	
	public void updateAllowClose(){
		updateCloseApp(getModel().getConfig().isLockedClosed()
				&& getOrder().getOrderProducts().size() > 0 ? "N" : "Y");
	}
	
	public void updateCloseApp(String value){
		if (getModel().getConfig() != null
				&& getModel().getConfig().isLockedClosed()) {
			// Setear el valor para poder cerrar la aplicación y esta ventana
			Env.setContext(Env.getCtx(), Env.CLOSE_APPS_PROP_NAME, value);
			// Actualizar el menú habilitando o no la opción para cerrar la
			// ventana y la aplicación
//			getFrame().getJMenuBar().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
//		              KeyStroke.getKeyStroke(KeyEvent.VK_X,Event.ALT_MASK), (value.equals("N")?"none":"End"));
//			getFrame().getJMenuBar().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
//		              KeyStroke.getKeyStroke(KeyEvent.VK_X,Event.SHIFT_MASK + Event.ALT_MASK), (value.equals("N")?"none":"Exit"));
		}
	}

	public class ComponentsFocusTraversalPolicy extends FocusTraversalPolicy{

		@Override
		public Component getComponentAfter(Container arg0, Component arg1) {
			Component result = null;
			if(arg1.equals(getCProductCodeText())){
				result = getCCountText();
			}
			else if(arg1.equals(getCCountText())){
				result = getCProductCodeText();
			}
			else if(arg1.equals(getCAddTenderTypeButton())){
				if(getCRemovePaymentButton().isEnabled()){
					result = getCRemovePaymentButton();
				}
				else{
					result = getCTenderTypeCombo(); 
				}
			}
			else if(arg1.equals(getCRemovePaymentButton())){
				if(getCFinishPayButton().isEnabled()){
					result = getCFinishPayButton();
				}
				else{
					result = getCTenderTypeCombo();
				}
			}
			else{
				result = oldFocusTraversalPolicy.getComponentAfter(arg0, arg1);
			}
			return result;
		}

		@Override
		public Component getComponentBefore(Container arg0, Component arg1) {
			Component result = null;
			if(arg1.equals(getCProductCodeText())){
				result = getCCountText();
			}
			else if(arg1.equals(getCCountText())){
				result = getCProductCodeText();
			}
			else if(arg1.equals(getCAddTenderTypeButton())){
				if(getCRemovePaymentButton().isEnabled()){
					result = getCRemovePaymentButton();
				}
				else{
					result = getCTenderTypeCombo(); 
				}
			}
			else if(arg1.equals(getCRemovePaymentButton())){
				result = getCAddTenderTypeButton();
			}
			else{
				result = oldFocusTraversalPolicy.getComponentBefore(arg0, arg1);
			}
			return result;
		}

		@Override
		public Component getDefaultComponent(Container arg0) {
			return getCProductCodeText();
		}

		@Override
		public Component getFirstComponent(Container arg0) {
			return getCProductCodeText();
		}

		@Override
		public Component getLastComponent(Container arg0) {
			return null;
		}
    	
    }

	@Override
	public Container getContainerForMsg() {
		return this;
	}

	public void setExtraPOSPaymentAddValidations(
			AddPOSPaymentValidations extraPOSPaymentAddValidations) {
		this.extraPOSPaymentAddValidations = extraPOSPaymentAddValidations;
	}

	public AddPOSPaymentValidations getExtraPOSPaymentAddValidations() {
		return extraPOSPaymentAddValidations;
	}

	/**
	 * Agrega o elimina la autorización del descuento manual general dependiendo
	 * si el valor es distinto de 0 o 0 respectivamente
	 * @param percentage
	 */
	protected void updateManualDiscountAuthorization(BigDecimal percentage){
		if(getModel().getPoSConfig().isAuthorizeManualGeneralDiscount()){
			if(percentage.compareTo(BigDecimal.ZERO) == 0){
				getAuthDialog().removeAuthOperation(getManualDiscountAuthOperation());
			}
			else if(percentage.compareTo(BigDecimal.ZERO) != 0){
				getManualDiscountAuthOperation().setAuthorized(false);
				getAuthDialog().removeAuthOperation(getManualDiscountAuthOperation());
				getAuthDialog().addAuthOperation(getManualDiscountAuthOperation());
			}
		}
	}

	protected void setManualDiscountAuthOperation(
			AuthOperation manualDiscountAuthOperation) {
		this.manualDiscountAuthOperation = manualDiscountAuthOperation;
	}

	protected AuthOperation getManualDiscountAuthOperation() {
		return manualDiscountAuthOperation;
	}
	
	/**
	 * Actividades necesarias por medio de pago a crédito en base a
	 * configuraciones de la entidad comercial
	 * 
	 * @param tenderType
	 */
	protected void refreshBPartnerCreditPaymentMedium(String tenderType){
		if (MPOSPaymentMedium.TENDERTYPE_Credit.equals(tenderType)){
			getCPaymentMediumCombo().removeAllItems();
			getCPaymentMediumCombo().addItem(
					getOrder().getBusinessPartner().getPaymentMedium());
		}
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
