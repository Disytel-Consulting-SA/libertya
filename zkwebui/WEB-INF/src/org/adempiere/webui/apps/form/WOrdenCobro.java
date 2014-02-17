package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.webui.component.ComboItem;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.compiere.swing.CComboBox;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.apps.form.VOrdenCobroModel;
import org.openXpertya.apps.form.VOrdenCobroModel.OpenInvoicesCustomerReceiptsTableModel;
import org.openXpertya.apps.form.VOrdenPagoModel;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPago;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCheque;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCredito;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoEfectivo;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoTarjetaCredito;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoTransferencia;
import org.openXpertya.model.Lookup;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Space;


public class WOrdenCobro extends WOrdenPago {

	protected static final String GOTO_TENDER_TYPE = "GOTO_TENDER_TYPE";

	private static Map<String, String> msgChanges;
	static {
		msgChanges = new HashMap<String, String>();
		msgChanges.put("EmitPayment", "EmitReceipt");
		msgChanges.put("AdvancedPayment", "AdvancedCustomerPayment");
		msgChanges.put("Payment", "CustomerPayment");
	}

	protected Label lblTenderType;
	protected Label lblCreditCardReceiptMedium = new Label();
	protected Label lblCheckReceiptMedium = new Label();
	protected Label lblCreditReceiptMedium = new Label();
	protected Label lblCashReceiptMedium = new Label();
	protected Label lblTransferReceiptMedium = new Label();
	protected Label lblRetencionReceiptMedium = new Label();
	protected Label lblPagoAdelantadoReceiptMedium = new Label();
	protected Label lblCreditCardPlan;
	private WDateEditor retencFecha;
	private WSearchEditor retencSchema;
	protected WSearchEditor bPartnerDiscount;
	protected WStringEditor txtPOS;
	protected WStringEditor txtOrgCharge;
	protected WStringEditor txtGroupingAmt;
	protected Combobox cboTenderType;
	private WStringEditor txtRetencImporte = new WStringEditor();
	private WStringEditor txtRetencNroRetenc;
	protected WStringEditor txtDiscountAmt;
	protected WStringEditor txtPaymentDiscount;
	protected WStringEditor txtPaymentToPayDiscount;
	protected WTableDirEditor cboCreditCardBank;
	protected WStringEditor txtCreditCardNo;
	protected WStringEditor txtCreditCardCouponNo;
	protected WStringEditor txtCuotasCount;
	protected WStringEditor txtCuotaAmt;
	protected WStringEditor txtCreditCardAmt = new WStringEditor();
	private Tabpanel panelRetenc;
	protected Panelchildren panelSummary;
	protected Tabpanel panelCreditCard;

	private int m_retencTabIndex = -1;
	protected int m_creditCardTabIndex = -1;

	protected Map<String, List<Integer>> tenderTypesTabRelations;
	protected Map<Integer, String> tabsTenderTypeRelations;

	protected Map<String, ValueNamePair> tenderTypesComboValues;
	protected Map<Integer, Combobox> tenderTypeIndexsCombos = new HashMap<Integer, Combobox>();

	protected Combobox cboCreditCardReceiptMedium = new Combobox();
	protected Combobox cboCheckReceiptMedium = new Combobox();
	protected Combobox cboCreditReceiptMedium = new Combobox();
	protected Combobox cboCashReceiptMedium = new Combobox();
	protected Combobox cboTransferReceiptMedium = new Combobox();
	protected Combobox cboRetencionReceiptMedium = new Combobox();
	protected Combobox cboPagoAdelantadoReceiptMedium = new Combobox();

	protected Combobox cboEntidadFinancieraPlans;

	private PaymentMediumItemListener paymentMediumItemListener;

	public WOrdenCobro() {
		super();
		setModel(new VOrdenCobroModel());
		setActualizarNrosChequera(false);
		setPaymentMediumItemListener(new PaymentMediumItemListener());
	}

	@Override
	protected WSearchEditor createChequeChequeraLookup() {
		// return VComponentsFactory.VLookupFactory("C_BankAccount_ID", "C_BankAccount", m_WindowNo, DisplayType.TableDir, getModel().getChequeChequeraSqlValidation());
        MLookupInfo infoTransf = VComponentsFactory.MLookupInfoFactory(Env.getCtx(),m_WindowNo, 0, "C_BankAccount_ID", "C_BankAccount", DisplayType.TableDir, m_model.getChequeChequeraSqlValidation());
		MLookup lookupTransf = new MLookup(infoTransf, 0);
		WSearchEditor editor = new WSearchEditor( "C_BankAccount_ID",false,false,true,lookupTransf );
		addPopupMenu(editor, true, true, false);
		return editor;
	}

	@Override
	protected void chequeraChange(ValueChangeEvent e) {
		// No se debe obtener el número de cheque automáticamente, se ingresa
		// manualmente.
	}

	@Override
	protected void initTranslations() {
		super.initTranslations();
		// Cheques
		chequeChequera.getLabel().setText(Msg.translate(m_ctx, "C_BankAccount_ID"));

		// Retencions
		retencSchema.getLabel().setValue(Msg.translate(m_ctx, "C_Withholding_ID"));
		txtRetencNroRetenc.getLabel().setValue(Msg.translate(m_ctx, "RetencionNumber"));
		txtRetencImporte.getLabel().setValue(Msg.getElement(m_ctx, "Amount"));
		retencFecha.getLabel().setValue(Msg.translate(m_ctx, "Date"));

		//
		radPayTypeStd.setValue(Msg.translate(m_ctx, "StandardCustomerPayment"));
		radPayTypeAdv.setValue(Msg.translate(m_ctx, "AdvancedCustomerPayment"));
		txtMedioPago2.getLabel().setValue(Msg.translate(m_ctx, "CustomerTenderType"));

		checkPayAll.setText(Msg.getMsg(m_ctx, "ReceiptAll") /* + " "
				+ KeyUtils.getKeyStr(getActionKeys().get(GOTO_PAYALL)) */ );

		tabPaymentSelection = new Tab(Msg.getMsg(Env.getCtx(), "CustomerPaymentSelection"));  // jTabbedPane1.setTitleAt(0, Msg.translate(m_ctx, "CustomerPaymentSelection"));
		tabPaymentRule = new Tab(Msg.getMsg(Env.getCtx(), "CustomerPaymentRule")); // jTabbedPane1.setTitleAt(1, Msg.translate(m_ctx, "CustomerPaymentRule"));
		
		bPartnerDiscount.getLabel().setValue(Msg.translate(m_ctx, "M_DiscountSchema_ID"));
		txtPOS.getLabel().setValue(Msg.translate(m_ctx, "RealPOS"));
		txtOrgCharge.getLabel().setValue(Msg.translate(m_ctx, "OrgCharge"));
		txtGroupingAmt.getLabel().setValue(Msg.translate(m_ctx, "GroupingAmt"));

		String msgReceiptMedium = Msg.translate(m_ctx, "ReceiptMedium");
		lblCreditCardReceiptMedium.setValue(msgReceiptMedium);
		lblCheckReceiptMedium.setValue(msgReceiptMedium);
		lblCreditReceiptMedium.setValue(msgReceiptMedium);
		lblCashReceiptMedium.setValue(msgReceiptMedium);
		lblTransferReceiptMedium.setValue(msgReceiptMedium);
		lblRetencionReceiptMedium.setValue(msgReceiptMedium);
		lblPagoAdelantadoReceiptMedium.setValue(msgReceiptMedium);
		lblTenderType.setValue(lblTenderType.getValue() /* + " "
				+ KeyUtils.getKeyStr(getActionKeys().get(GOTO_TENDER_TYPE)) */ );

	}

	@Override
	protected void customInitComponents() {
		super.customInitComponents();

		cboCurrency.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent evt) {
				try {
					updateTenderTypeCombo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}
		});

		ocultarCanje();
	}

	protected void ocultarCanje() {
		// Lo hace el renderer mediante shouldHideColumn
	}
	
	protected boolean shouldHideColumn(int columnNo) { 
		if (m_model == null || m_model.m_facturasTableModel == null)
			return false;
		// Ocultar la columna Canje
		return columnNo == listModel.getColumnCount() - 3;
	}
	
	@Override
	protected void addCustomPaymentTabs() {
		if (mpTabs == null || mpTabpanels == null)
			return;
		
		// Agregado de pestaña de medio de cobro en retenciones.
		Tab tab = new Tab(getMsg("C_Withholding_ID"));
		mpTabs.appendChild(tab);
		mpTabpanels.appendChild(createRetencionTab());
		// Determinar posicion de la pestaña
		int i = 0;
		for (Object child : mpTabpanels.getChildren()) {
			if (child == panelRetenc)
				m_retencTabIndex = i;
			i++;
		}
		tenderTypeIndexsCombos.put(m_retencTabIndex, cboRetencionReceiptMedium);
		
		// Agregado de pestaña de medio de cobro en retenciones.
		Tab tab2 = new Tab(getMsg("CreditCard"));
		mpTabs.appendChild(tab2);
		mpTabpanels.appendChild(createCreditCardTab());
		// Determinar posicion de la pestaña
		i = 0;
		for (Object child : mpTabpanels.getChildren()) {
			if (child == panelCreditCard)
				m_creditCardTabIndex = i;
			i++;
		}
		tenderTypeIndexsCombos.put(m_creditCardTabIndex, cboCreditCardReceiptMedium);
	}

	@Override
	protected void addCustomOperationAfterTabsDefinition() {
		// Inicializar las relaciones de los tender types con las pestañas
		initializeTenderTypeTabsRelations();
		// Por defecto deshabilitar todas las pestañas
		int tabCount = mpTabs.getChildren().size();
		for (int i = 0; i < tabCount; i++) {
			((Tab)(mpTabs.getChildren().get(i))).setDisabled(true);
		}
		// Habilito/Deshabilito las pestañas que no esten relacionadas con el
		// tender type actual
		if (cboTenderType.getSelectedIndex() >= 0) {
			processPaymentTabs(
					cboTenderType.getValue() == null ? null
							: ((ValueNamePair)cboTenderType.getSelectedItem().getValue()).getValue() );
			// Selecciono la primer pestaña de pagos que se encuentre habilitada
		}
		selectPaymentTab();
		// Selecciono uno por default
		if (cboTenderType.getItemCount() > 0) {
			cboTenderType.setSelectedIndex(0);
		}
	}



	@Override
	protected Tabpanel  createCashTab() {

		Tabpanel tabpanel = new Tabpanel();
    	tabpanel.setHeight("150px");

		efectivoLibroCaja.getLabel().setText("LIBRO DE CAJA");
		txtEfectivoImporte.getLabel().setText("IMPORTE");
		txtEfectivoImporte.setValue("0");
		cboCashReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_Cash);
		cboCashReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_EFECTIVO, cboCashReceiptMedium);  
    	txtEfectivoImporte.setValue("0");
	
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
    	Row row0 = rows.newRow();
    	row0.appendChild(lblCashReceiptMedium.rightAlign());
    	row0.appendChild(cboCashReceiptMedium);
		Row row = rows.newRow();
		row.appendChild(efectivoLibroCaja.getLabel().rightAlign());
		row.appendChild(efectivoLibroCaja.getComponent());
		row.appendChild(txtEfectivoImporte.getLabel().rightAlign());
		row.appendChild(txtEfectivoImporte.getComponent());
		
		tabpanel.appendChild(gridpanel);
        return tabpanel;

	}

	@Override
	protected Tabpanel  createTransferTab() {

		Tabpanel tabpanel = new Tabpanel();
		transfCtaBancaria.getLabel().setText("CUENTA BANCARIA");
		txtTransfNroTransf.getLabel().setText("NRO TRANSFERENCIA");
		txtTransfImporte.getLabel().setText("IMPORTE");
		transFecha.getLabel().setText("FECHA");
		txtTransfImporte.setValue("0");
		cboTransferReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_DirectDeposit);
		cboTransferReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_TRANSFERENCIA, cboTransferReceiptMedium);

    	tabpanel.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();

    	Row row0 = rows.newRow();
		row0.appendChild(lblTransferReceiptMedium.rightAlign());
		row0.appendChild(cboTransferReceiptMedium);
		Row row1 = rows.newRow();
		row1.appendChild(transfCtaBancaria.getLabel().rightAlign());
		row1.appendChild(transfCtaBancaria.getComponent());
		row1.appendChild(txtTransfNroTransf.getLabel().rightAlign());
		row1.appendChild(txtTransfNroTransf.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(txtTransfImporte.getLabel().rightAlign());
		row2.appendChild(txtTransfImporte.getComponent());
		row2.appendChild(transFecha.getLabel().rightAlign());
		row2.appendChild(transFecha.getComponent());
		
		txtTransfImporte.setValue("0");
		
		tabpanel.appendChild(gridpanel);

		return tabpanel; // jPanel6;
	}

	@Override
	protected Tabpanel createCheckTab() {
		
		Tabpanel tabpanel = new Tabpanel();
		
        chequeChequera.getLabel().setText("CHEQUERA");
        txtChequeNroCheque.getLabel().setText("NUMERO DE CHEQUE");
        txtChequeImporte.getLabel().setText("IMPORTE");
        chequeFechaEmision.getLabel().setText("FECHA EMISION");
        chequeFechaPago.getLabel().setText("FECHA PAGO");
        txtChequeALaOrden.getLabel().setText(getModel().isSOTrx()?"LIBRADOR":"A LA ORDEN");
        txtChequeBanco.getLabel().setText("BANCO");
        txtChequeCUITLibrador.getLabel().setText("CUIT LIBRADOR");
        txtChequeDescripcion.getLabel().setText("DESCRIPCION");      
 
		cboCheckReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_Check);
		cboCheckReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		chequeFechaEmision.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent evt) {
				if (cboCheckReceiptMedium.getSelectedItem() != null) {
					updateFechaPagoCheque((MPOSPaymentMedium) cboCheckReceiptMedium.getSelectedItem().getValue());
				}
			}
		});
		tenderTypeIndexsCombos.put(TAB_INDEX_CHEQUE, cboCheckReceiptMedium);
		if (cboCheckReceiptMedium.getSelectedIndex() > 0 && cboCheckReceiptMedium.getSelectedItem().getValue() != null)
			updateBank((MPOSPaymentMedium) cboCheckReceiptMedium.getSelectedItem().getValue());

    	tabpanel.setHeight("150px");
    	
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblCheckReceiptMedium.rightAlign());
		row.appendChild(cboCheckReceiptMedium);
		row.appendChild(chequeChequera.getLabel().rightAlign());
		row.appendChild(chequeChequera.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(txtChequeNroCheque.getLabel().rightAlign());
		row2.appendChild(txtChequeNroCheque.getComponent());
		row2.appendChild(txtChequeImporte.getLabel().rightAlign());
		row2.appendChild(txtChequeImporte.getComponent());
		Row row3 = rows.newRow();
		row3.appendChild(chequeFechaEmision.getLabel().rightAlign());
		row3.appendChild(chequeFechaEmision.getComponent());
		row3.appendChild(chequeFechaPago.getLabel().rightAlign());
		row3.appendChild(chequeFechaPago.getComponent());
		Row row4 = rows.newRow();
		row4.appendChild(txtChequeALaOrden.getLabel().rightAlign());
		row4.appendChild(txtChequeALaOrden.getComponent());
		row4.appendChild(txtChequeBanco.getLabel().rightAlign());
		row4.appendChild(txtChequeBanco.getComponent());
		Row row5 = rows.newRow();
		row5.appendChild(txtChequeCUITLibrador.getLabel().rightAlign());
		row5.appendChild(txtChequeCUITLibrador.getComponent());
		row5.appendChild(txtChequeDescripcion.getLabel().rightAlign());
		row5.appendChild(txtChequeDescripcion.getComponent());

		tabpanel.appendChild(gridpanel);
		
		return tabpanel; 
	}

	@Override
	protected Tabpanel  createCreditTab() {
		
		creditInvoice.getLabel().setText("CREDITO");
		txtCreditAvailable.getLabel().setText("DISPONIBLE");
		txtCreditImporte.getLabel().setText("IMPORTE");
		txtCreditAvailable.setValue("0");
		txtCreditImporte.setValue("0");
		cboCreditReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_CreditNote);
		cboCreditReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_CREDITO, cboCreditReceiptMedium);

		Tabpanel tabpanel = new Tabpanel();
    	tabpanel.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row0 = rows.newRow();
		row0.appendChild(lblCreditReceiptMedium.rightAlign());
		row0.appendChild(cboCreditReceiptMedium);
		row0.appendChild(creditInvoice.getLabel().rightAlign());
		row0.appendChild(creditInvoice.getComponent());
		Row row1 = rows.newRow();
		row1.appendChild(txtCreditAvailable.getLabel().rightAlign());
		row1.appendChild(txtCreditAvailable.getComponent());
		row1.appendChild(txtCreditImporte.getLabel().rightAlign());
		row1.appendChild(txtCreditImporte.getComponent());
		
        txtCreditAvailable.setValue("0");        
        txtCreditImporte.setValue("0");
		
		tabpanel.appendChild(gridpanel);
		return tabpanel; // jPanel11;
	}

	private Tabpanel  createRetencionTab() {
		panelRetenc = new Tabpanel();
		panelRetenc.setHeight("150px");
		retencFecha = new WDateEditor(); 
		retencFecha.getLabel().setText("FECHA");
		txtRetencImporte = new WStringEditor();
		txtRetencImporte.getLabel().setText("IMPORTE");
		txtRetencImporte.setValue("0");
		txtRetencNroRetenc = new WStringEditor();
		txtRetencNroRetenc.getLabel().setText("NRO RETENCION");
		MLookupInfo infoRetencSchema = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_RetencionSchema_ID", "C_RetencionSchema", DisplayType.Search, getCobroModel().getRetencionSqlValidation());
		MLookup lookupRetencSchema = new MLookup(infoRetencSchema, 0);
		retencSchema = new WSearchEditor("C_RetencionSchema_ID", false, false, true, lookupRetencSchema);
		addPopupMenu(retencSchema, true, true, false);
		retencSchema.getLabel().setValue("RETENCION");
		
		cboRetencionReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_Retencion);
		cboRetencionReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());

    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblRetencionReceiptMedium.rightAlign());
		row.appendChild(cboRetencionReceiptMedium);
		row.appendChild(retencSchema.getLabel().rightAlign());
		row.appendChild(retencSchema.getComponent());
		
		Row row2 = rows.newRow();
		row2.appendChild(txtRetencNroRetenc.getLabel().rightAlign());
		row2.appendChild(txtRetencNroRetenc.getComponent());
		row2.appendChild(txtRetencImporte.getLabel().rightAlign());
		row2.appendChild(txtRetencImporte.getComponent());
		Row row3 = rows.newRow();
		row3.appendChild(retencFecha.getLabel().rightAlign());
		row3.appendChild(retencFecha.getComponent());
		
		panelRetenc.appendChild(gridpanel);
		
		return panelRetenc;
	}

	protected Tabpanel  createCreditCardTab() {

		panelCreditCard = new Tabpanel();
		
		txtCreditCardNo = new WStringEditor();
		txtCreditCardCouponNo = new WStringEditor();
		txtCuotasCount = new WStringEditor();
		txtCuotaAmt = new WStringEditor();
		txtCreditCardAmt = new WStringEditor();
		
		lblCreditCardPlan = new Label(getMsg("CreditCardPlan"));
		txtCreditCardNo.getLabel().setValue(getMsg("CreditCardNumber"));
		txtCreditCardCouponNo.getLabel().setValue(getMsg("CouponNumber"));
		txtCuotasCount.getLabel().setValue(getMsg("CuotasCount"));
		txtCuotaAmt.getLabel().setValue(getMsg("CuotaAmt"));
		txtCreditCardAmt.getLabel().setValue(getMsg("Amt"));
		lblCreditCardReceiptMedium = new Label();
		MLookup lookupCreditCardBank = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "Bank", "C_POSPaymentMedium", DisplayType.List); // columna Bank de C_POSPaymentMedium
		cboCreditCardBank = new WTableDirEditor("Bank", false, false, true, lookupCreditCardBank);
		cboCreditCardBank.getLabel().setValue(getMsg("C_Bank_ID"));
		txtCuotaAmt.setReadWrite(false);
		addPopupMenu(cboCreditCardBank, true, true, false);
		
		txtCreditCardAmt.getComponent().addEventListener("onChanging", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (arg0.getTarget() != null) {
					refreshPaymentMediumAmountInfo((MPOSPaymentMedium) cboCreditCardReceiptMedium.getSelectedItem().getValue());
				}
			}
		});
		
		txtCreditCardAmt.getComponent().addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (arg0.getTarget() != null) {
					refreshPaymentMediumAmountInfo((MPOSPaymentMedium) cboCreditCardReceiptMedium.getSelectedItem().getValue());
				}
			}
		});

		cboCreditCardReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_CreditCard);
		cboCreditCardReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		cboEntidadFinancieraPlans = new Combobox();
//		cboEntidadFinancieraPlans.setMandatory(true);	FEDE:TODO
		cboEntidadFinancieraPlans.addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (arg0.getTarget() != null) {
					loadPlanInfo((MEntidadFinancieraPlan) cboEntidadFinancieraPlans.getSelectedItem().getValue());
				}
			}
		});
		
		txtCreditCardAmt.setValue(getModel().numberFormat(getModel().getSaldoMediosPago()));
		panelCreditCard.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblCreditCardReceiptMedium.rightAlign());
		row.appendChild(cboCreditCardReceiptMedium);
		row.appendChild(lblCreditCardPlan.rightAlign());
		row.appendChild(cboEntidadFinancieraPlans);
		Row row2 = rows.newRow();
		row2.appendChild(cboCreditCardBank.getLabel().rightAlign());
		row2.appendChild(cboCreditCardBank.getComponent());
		row2.appendChild(txtCreditCardNo.getLabel().rightAlign());
		row2.appendChild(txtCreditCardNo.getComponent());
		Row row3 = rows.newRow();
		row3.appendChild(txtCreditCardCouponNo.getLabel().rightAlign());
		row3.appendChild(txtCreditCardCouponNo.getComponent());
		row3.appendChild(txtCreditCardAmt.getLabel().rightAlign());
		row3.appendChild(txtCreditCardAmt.getComponent());
		Row row4 = rows.newRow();
		row4.appendChild(txtCuotasCount.getLabel().rightAlign());
		row4.appendChild(txtCuotasCount.getComponent());
		row4.appendChild(txtCuotaAmt.getLabel().rightAlign());
		row4.appendChild(txtCuotaAmt.getComponent());
		
        panelCreditCard.appendChild(gridpanel);
		return panelCreditCard;
	}

	
	@Override
	protected Tabpanel  createPagoAdelantadoTab() {
		panelPagoAdelantado = new Tabpanel();

		txtPagoAdelantadoImporte = new WStringEditor();
		txtPagoAdelantadoImporte.getLabel().setValue("IMPORTE");
		txtPagoAdelantadoImporte.setValue("0");
		MLookupInfo infoPago = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_Payment_ID", "C_Payment", DisplayType.Search, getModel().getPagoAdelantadoSqlValidation());
		Lookup lookupPago = new MLookup(infoPago, 0);
		pagoAdelantado = new WSearchEditor("C_Payment_ID", false, false, true, lookupPago);
		addPopupMenu(pagoAdelantado, true, true, false);
		
        MLookupInfo infoCash = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, "C_CashLine_ID", "C_CashLine", DisplayType.Search, getModel().getCashAnticipadoSqlValidation());
		Lookup lookupCash = new MLookup(infoCash, 0);
		cashAdelantado = new WSearchEditor("C_CashLine_ID", false, false, true, lookupCash);
		addPopupMenu(cashAdelantado, true, true, false);
		cashAdelantado.getLabel().setText(getModel().isSOTrx() ? "COBRO" : "PAGO");
		pagoAdelantado.getLabel().setText(getModel().isSOTrx() ? "COBRO" : "PAGO");
		
		lblPagoAdelantadoType = new Label();
		lblPagoAdelantadoType.setText("TIPO");
		cboPagoAdelantadoType = new Combobox();
        cboPagoAdelantadoType.appendItem(getMsg("Payment"));
        cboPagoAdelantadoType.appendItem(getMsg("Cash"));		

		// Por defecto pago.
		cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX);
		cboPagoAdelantadoType.addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				updatePagoAdelantadoTab();
			}
		});
		
		cboPagoAdelantadoReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt);
		txtPagoAdelantadoAvailable = new WStringEditor();
		txtPagoAdelantadoAvailable.setReadWrite(false);
		txtPagoAdelantadoAvailable.getLabel().setText("PENDIENTE");
		tenderTypeIndexsCombos.put(TAB_INDEX_PAGO_ADELANTADO, cboPagoAdelantadoReceiptMedium);
		cboPagoAdelantadoReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
	
        // Por defecto pago
        cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX); 
        cboPagoAdelantadoType.addEventListener("onChange", new EventListener() {
        	public void onEvent(Event arg0) throws Exception {
				updatePagoAdelantadoTab();
			}
		});
        txtPagoAdelantadoAvailable = new WStringEditor();
        txtPagoAdelantadoAvailable.setReadWrite(false); 
        txtPagoAdelantadoAvailable.getLabel().setText("PENDIENTE");
		
        Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		panelPagoAdelantado.setHeight("150px"); 
		
		Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblPagoAdelantadoReceiptMedium.rightAlign());
		row.appendChild(cboPagoAdelantadoReceiptMedium);
		row.appendChild(lblPagoAdelantadoType.rightAlign());
		row.appendChild(cboPagoAdelantadoType);
		
		// Uno de los dos (efectivo o pago)
		Row row2a = rows.newRow();
		row2a.appendChild(pagoAdelantado.getLabel().rightAlign());
		row2a.appendChild(pagoAdelantado.getComponent());
		Row row2b = rows.newRow();
		row2b.appendChild(cashAdelantado.getLabel().rightAlign());
		row2b.appendChild(cashAdelantado.getComponent());
		
		Row row3 = rows.newRow();
		row3.appendChild(txtPagoAdelantadoAvailable.getLabel().rightAlign());
		row3.appendChild(txtPagoAdelantadoAvailable.getComponent());
		Row row4 = rows.newRow();
		row4.appendChild(txtPagoAdelantadoImporte.getLabel().rightAlign());
		row4.appendChild(txtPagoAdelantadoImporte.getComponent());
		
        updatePagoAdelantadoTab();
        panelPagoAdelantado.appendChild(gridpanel);
		

		updatePagoAdelantadoTab();
		return panelPagoAdelantado;
	}


	@Override
	protected void createPaymentTab() {
		// Creo el panel de tipo de pago
		createTenderTypePanel();
		// Creo el panel de descuento/recargo como el que se encuentra en el TPV
		createPaymentMediumDiscountPanel(null);
		// Creo el texto de descuento/recargo del documento
		createDocumentDiscountComponents();

		super.createPaymentTab();
	}

	/**
	 * Aquí se crean los componentes y definen metodos para tipo de pago.  
	 * Luego se incorpora al panel de Proyecto/Campaña.  Ver addFieldsToCampProy()
	 */
	protected void createTenderTypePanel() {

		lblTenderType = new Label(Msg.translate(m_ctx, "TenderType"));
		cboTenderType = createTenderTypeCombo();
//		cboTenderType.setMandatory(true);		// TODO:FEDE ver
		cboTenderType.addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				ValueNamePair tenderType = (ValueNamePair)(((Combobox)arg0.getTarget()).getSelectedItem().getValue()); // .getItem();
				processPaymentTabs(tenderType.getValue());
				selectPaymentTab();
				// Actualizar panel de resumen total
				updateSummaryInfo();
				// Actualizar panel A Cobrar
				
				updatePaymentMediumCombo(tenderType.getValue());
				
				if (mpTabbox.getSelectedIndex() < 0)
					return;
				if (tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()) == null)
					return;
				if (tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()).getSelectedItem() == null)
					return;
				MPOSPaymentMedium selectedPaymentMedium = (MPOSPaymentMedium)tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()).getSelectedItem().getValue();
				
				// Si estamos editando no debemos actualizar la info del medio
				// de pago, sino si
				updateDiscount(selectedPaymentMedium);
			}
		});
		
		// Selecciono uno por default
		if (cboTenderType.getItemCount() > 0)
			cboTenderType.setSelectedIndex(0);  // setSelectedItem(MPOSPaymentMedium.TENDERTYPE_Cash);
		// Layout del panel
	}


	/**
	 * Crea los componentes para descuento/recargo del documento
	 */
	protected void createDocumentDiscountComponents() {
		txtDiscountAmt = new WStringEditor();
		txtDiscountAmt.getLabel().setValue(Msg.translate(m_ctx, "DiscountCharge"));
		txtDiscountAmt.setReadWrite(false);
	}

	/**
	 * Crea el panel de descuentos por medio de pago.  Redefinido desde createPaymentTab()
	 */
	protected void createPaymentMediumDiscountPanel(Rows rows) {
		txtPaymentDiscount = new WStringEditor();
		txtPaymentToPayDiscount = new WStringEditor();
		txtPaymentDiscount.getLabel().setValue(Msg.translate(m_ctx, "DiscountCharge"));
		txtPaymentToPayDiscount.getLabel().setValue(Msg.translate(m_ctx, "DiscountedChargedToPayAmt"));

		txtPaymentDiscount.setReadWrite(false);
		
		txtPaymentToPayDiscount.setReadWrite(false);

		if (rows == null)	// invocacion al iniciar la ventana 
			return;
		Row row = rows.newRow();
		row.appendChild(txtPaymentDiscount.getLabel().rightAlign());
		row.appendChild(txtPaymentDiscount.getComponent());
		row.appendChild(txtPaymentToPayDiscount.getLabel().rightAlign());
		row.appendChild(txtPaymentToPayDiscount.getComponent());
	}

	
	/**
	 * Permite agregar campos en el tab summary
	 */
	protected void addSummaryCustomFields(Rows rows) {
		Row row = rows.newRow();
		row.appendChild(txtDiscountAmt.getLabel().rightAlign());
		row.appendChild(txtDiscountAmt.getComponent());
	}
	

	/**
	 * @return combo con los tender type disponibles
	 */
	protected Combobox createTenderTypeCombo() {
		Combobox tenderTypeCombo = new Combobox();
		// Obtiene los tipos de pago disponibles a partir del contexto Recibos
		// de Cliente, en realidad se excluye tpv para obtener solo Recibos de
		// Cliente y ambos
		int currencyID = ( (Integer) cboCurrency.getValue() == null) ? m_C_Currency_ID : (Integer) cboCurrency.getValue();
		
		List<ValueNamePair> list = MPOSPaymentMedium.getTenderTypesByContextOfUse(Env.getCtx(), MPOSPaymentMedium.CONTEXT_POSOnly, true, true, null, currencyID);
		tenderTypesComboValues = new HashMap<String, ValueNamePair>();
		// Se agregan al combo
		for (ValueNamePair tenderType : list) {
			tenderTypeCombo.appendItem(tenderType.getName(), tenderType); 
			tenderTypesComboValues.put(tenderType.getName(), tenderType);
		}
		return tenderTypeCombo;
	}

	/**
	 * @return combo con los tender type disponibles
	 */
	protected void updateTenderTypeCombo() {
		cboTenderType.removeAllItems();

		// Obtiene los tipos de pago disponibles a partir del contexto Recibos
		// de Cliente, en realidad se excluye tpv para obtener solo Recibos de
		// Cliente y ambos
		List<ValueNamePair> list = MPOSPaymentMedium
				.getTenderTypesByContextOfUse(Env.getCtx(),
						MPOSPaymentMedium.CONTEXT_POSOnly, true, true, null,
						(Integer) cboCurrency.getValue());
		tenderTypesComboValues = new HashMap<String, ValueNamePair>();
		// Se agregan al combo
		for (ValueNamePair tenderType : list) {
			cboTenderType.appendItem(tenderType.getName(), tenderType);
			tenderTypesComboValues.put(tenderType.getName(), tenderType);
		}
		removeTenderTypesValuesByCustomConditions();
	}

	/**
	 * Inicializo las relaciones de los tender types con las pestañas y con
	 * respecto a los tipos de medio de pago
	 */
	protected void initializeTenderTypeTabsRelations() {
		// Inicializar la asociación con pestañas
		tenderTypesTabRelations = new HashMap<String, List<Integer>>();
		tenderTypesTabRelations.put(MPOSPaymentMedium.TENDERTYPE_Cash,
				Arrays.asList(TAB_INDEX_EFECTIVO));
		tenderTypesTabRelations.put(MPOSPaymentMedium.TENDERTYPE_Check,
				Arrays.asList(TAB_INDEX_CHEQUE));
		tenderTypesTabRelations.put(MPOSPaymentMedium.TENDERTYPE_DirectDeposit,
				Arrays.asList(TAB_INDEX_TRANSFERENCIA));
		tenderTypesTabRelations.put(MPOSPaymentMedium.TENDERTYPE_CreditCard,
				Arrays.asList(m_creditCardTabIndex));
		tenderTypesTabRelations.put(MPOSPaymentMedium.TENDERTYPE_CreditNote,
				Arrays.asList(TAB_INDEX_CREDITO));
		tenderTypesTabRelations.put(MPOSPaymentMedium.TENDERTYPE_Retencion,
				Arrays.asList(m_retencTabIndex));
		tenderTypesTabRelations.put(
				MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt,
				Arrays.asList(TAB_INDEX_PAGO_ADELANTADO));
		// Asociación de pestañas con tender type
		tabsTenderTypeRelations = new HashMap<Integer, String>();
		tabsTenderTypeRelations.put(TAB_INDEX_EFECTIVO,
				MPOSPaymentMedium.TENDERTYPE_Cash);
		tabsTenderTypeRelations.put(TAB_INDEX_CHEQUE,
				MPOSPaymentMedium.TENDERTYPE_Check);
		tabsTenderTypeRelations.put(TAB_INDEX_TRANSFERENCIA,
				MPOSPaymentMedium.TENDERTYPE_DirectDeposit);
		tabsTenderTypeRelations.put(TAB_INDEX_PAGO_ADELANTADO,
				MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt);
		tabsTenderTypeRelations.put(m_creditCardTabIndex,
				MPOSPaymentMedium.TENDERTYPE_CreditCard);
		tabsTenderTypeRelations.put(TAB_INDEX_CREDITO,
				MPOSPaymentMedium.TENDERTYPE_CreditNote);
		tabsTenderTypeRelations.put(m_retencTabIndex,
				MPOSPaymentMedium.TENDERTYPE_Retencion);
	}

	/**
	 * Habilita las pestañas que estén relacionadas con el tender type
	 * parámetro. Deshabilita las pestañas que no estén relacionadas. Las
	 * pestañas se obtienen del tabbed pane parámetro.
	 * 
	 * @param tenderType
	 *            tipo de pago
	 * @param tabbedPane
	 *            panel de pestañas
	 */
	protected void processPaymentTabs(String tenderType) {
		List<Integer> indexRelations = new ArrayList<Integer>();
		if (tenderType != null) {
			// Obtengo la lista de índices de pestañas relacionadas con el tipo de pago parámetro
			indexRelations = tenderTypesTabRelations.get(tenderType);
		}
		int tabCount = mpTabs.getChildren().size();
		// Itero por las pestañas y habilito/deshabilito las correspondientes
		for (int i = 0; i < tabCount; i++) {
			((Tab)(mpTabs.getChildren().get(i))).setDisabled(true);
			// Si el índice actual se encuentra en la lista obtenida
			// anteriormente entonces lo habilito
			if (indexRelations.contains(i))
				((Tab)(mpTabs.getChildren().get(i))).setDisabled(false);
		}
	}

	/**
	 * Selecciona la primer pestaña que se encuentre habilitada del tabbed pane
	 * parámetro
	 * 
	 * @param tabbedPane
	 *            panel de pestañas
	 */
	protected void selectPaymentTab() {
		if (mpTabs == null || mpTabbox == null)
			return;
		int tabCount = mpTabs.getChildren().size();
		boolean found = false;
		// Itero por todas las pestañas y selecciono la primera que encuentro
		// habilitada
		int i;
		for (i = 0; i < tabCount && !found; i++) {
			found = !((Tab)(mpTabs.getChildren().get(i))).isDisabled();
		}
		// Si encontré alguna entonces la selecciono
		if (found) {
			mpTabbox.setSelectedIndex(i - 1); 
		}
	}

	private VOrdenCobroModel getCobroModel() {
		return (VOrdenCobroModel) getModel();
	}

	@Override
	protected void clearMediosPago() {
		super.clearMediosPago();
		if (panelCreditCard != null) {
			txtCreditCardAmt.setValue(Integer.toString(0));
			txtCreditCardCouponNo.setValue("");
			txtCreditCardNo.setValue("");
			txtCuotaAmt.setValue(Integer.toString(0));
		}
		if (retencSchema == null)
			return;

		Date d = new Date();
		// Retenciones
		retencSchema.setValue(null);
		txtRetencImporte.setValue("0");
		txtRetencNroRetenc.setValue("");
		retencFecha.setValue(d);
		// Caja efectivo de la caja diaria si es que existe
		setCashJournalToComponent();
	}

	/**
	 * Validaciones básica para el medios de pago al guardar uno nuevo
	 * 
	 * @param paymentMedium
	 *            medio de pago
	 * @param plan
	 *            plan de entidad financiera para medios de pago tarjeta de
	 *            crédito
	 * @throws Exception
	 *             en caso de error en las validaciones
	 */
	protected void saveBasicValidation(MPOSPaymentMedium paymentMedium,
			MEntidadFinancieraPlan plan) throws Exception {
		// Debe existir al menos un medio de cobro configurado
		if (paymentMedium == null) {
			throw new Exception(getMsg("NoPaymentMediumError"));
		}
		// Si el medio de cobro es tarjeta de crédito, debe existir un plan
		// configurado
		if (paymentMedium != null
				&& paymentMedium.getTenderType().equals(
						MPOSPaymentMedium.TENDERTYPE_CreditCard)
				&& plan == null) {
			throw new Exception(getMsg("NoCreditCardPlanError"));
		}
	}

	@Override
	protected void cmdCustomSaveMedioPago(int tabIndex) throws Exception {
		if (tabIndex == m_retencTabIndex) {
			saveRetencionMedioPago();
		} else if (tabIndex == m_creditCardTabIndex) {
			saveCreditCardMedioPago();
		}

		else
			super.cmdCustomSaveMedioPago(tabIndex);
	}

	private void saveRetencionMedioPago() throws Exception {
		// Obtengo los datos de la interfaz
		if (cboRetencionReceiptMedium == null || cboRetencionReceiptMedium.getSelectedIndex() < 0 )
			throw new Exception("@Invalid@ @"+Msg.translate(m_ctx, "ReceiptMedium")+"@");
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboRetencionReceiptMedium.getSelectedItem().getValue();  
		saveBasicValidation(paymentMedium, null);
		Integer retencionSchemaID = (Integer) retencSchema.getValue();
		String retencionNumber = txtRetencNroRetenc.getValue().toString().trim();
		BigDecimal amount = null;
		try {
			amount = numberParse(txtRetencImporte.getValue().toString());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Amount@");
		}
		Timestamp retencionDate = null;
		try {
			retencionDate = new Timestamp(((Date)retencFecha.getValue()).getTime());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @"+retencFecha.getLabel().getValue()+"@");
		}
		
		// Se agrega la retención como medio de cobro.
		getCobroModel().addRetencion(retencionSchemaID, retencionNumber,
				amount, retencionDate, getC_Campaign_ID(), getC_Project_ID());
	}

	protected void saveCreditCardMedioPago() throws Exception {
		// Obtengo la data de la interfaz gráfica
		if (cboCreditCardReceiptMedium == null || cboCreditCardReceiptMedium.getSelectedIndex() < 0 )
			throw new Exception("@Invalid@ @"+Msg.translate(m_ctx, "ReceiptMedium")+"@");
		if (cboEntidadFinancieraPlans == null || cboEntidadFinancieraPlans.getSelectedIndex() < 0 )
			throw new Exception("@Invalid@ @"+Msg.translate(m_ctx, "CreditCardPlan")+"@");
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCreditCardReceiptMedium.getSelectedItem().getValue(); 
		MEntidadFinancieraPlan plan = (MEntidadFinancieraPlan)cboEntidadFinancieraPlans.getSelectedItem().getValue(); 
		saveBasicValidation(paymentMedium, plan);
		getCobroModel().addCreditCard(
				paymentMedium,
				plan,
				txtCreditCardNo.getValue().toString(),
				txtCreditCardCouponNo.getValue().toString(),
				getModel().numberParse(txtCreditCardAmt.getValue().toString()),
				(String) cboCreditCardBank.getValue(),
				Util.isEmpty(txtCuotasCount.getValue().toString()) ? 0 : Integer
						.parseInt(txtCuotasCount.getValue().toString()),
				getModel().numberParse(txtCuotaAmt.getValue().toString()), getC_Campaign_ID(),
				getC_Project_ID(), (Integer) cboCurrency.getValue());
	}

	@Override
	protected MedioPagoCheque saveCheckMedioPago() throws Exception {
		// Obtengo la data de la interfaz gráfica
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCheckReceiptMedium.getSelectedItem().getValue();
		saveBasicValidation(paymentMedium, null);
		
		if (paymentMedium.isMandatoryBank() && Util.isEmpty(getBankName(), true)) {
			throw new Exception(getMsg("Bank"));
		}
		
		BigDecimal amount = null;
		Integer monedaOriginalID;
		try {
			amount = numberParse(txtChequeImporte.getValue().toString());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Amount@");
		}
		try {
			monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		try {
			new Timestamp(((Date)chequeFechaEmision.getValue()).getTime());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @EmittingDate@");
		}
		try {
			new Timestamp(((Date)chequeFechaPago.getValue()).getTime());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @PayDate@");
		}
		getCobroModel().addCheck(paymentMedium,
				(Integer) chequeChequera.getValue(),
				txtChequeNroCheque.getValue().toString(), amount,
				new Timestamp(((Date)chequeFechaEmision.getValue()).getTime()), // chequeFechaEmision.getTimestamp(),
				new Timestamp(((Date)chequeFechaPago.getValue()).getTime()), // chequeFechaPago.getTimestamp(), 
				txtChequeALaOrden.getValue().toString(),
				getBankName(), txtChequeCUITLibrador.getValue().toString(),
				txtChequeDescripcion.getValue().toString(), getC_Campaign_ID(),
				getC_Project_ID(), monedaOriginalID);
		return null;
	}

	@Override
	protected MedioPagoEfectivo saveCashMedioPago() throws Exception {
		// Validación para que el monto ingresado no supere el a pagar
		// toPayValidation();
		if (cboCashReceiptMedium == null || cboCashReceiptMedium.getSelectedIndex() < 0)
			throw new Exception("@Invalid@ @"+Msg.translate(m_ctx, "ReceiptMedium")+"@");
		MedioPagoEfectivo mpe = super.saveCashMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCashReceiptMedium.getSelectedItem().getValue(); 
		saveBasicValidation(paymentMedium, null);
		mpe.setPaymentMedium(paymentMedium);
		mpe.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpe.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpe;
	}

	@Override
	protected MedioPagoTransferencia saveTransferMedioPago() throws Exception {
		if (cboTransferReceiptMedium == null || cboTransferReceiptMedium.getSelectedIndex() < 0)
			throw new Exception("@Invalid@ @"+Msg.translate(m_ctx, "ReceiptMedium")+"@");
		try {
			new Timestamp(((Date)transFecha.getValue()).getTime());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Date@");
		}
		MedioPagoTransferencia mpt = super.saveTransferMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboTransferReceiptMedium.getSelectedItem().getValue();
		saveBasicValidation(paymentMedium, null);
		mpt.setPaymentMedium(paymentMedium);
		mpt.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpt.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpt;
	}

	@Override
	protected MedioPagoCredito saveCreditMedioPago() throws Exception {
		if (cboCreditReceiptMedium == null || cboCreditReceiptMedium.getSelectedIndex() < 0)
			throw new Exception("@Invalid@ @"+Msg.translate(m_ctx, "ReceiptMedium")+"@");
		MedioPagoCredito mpc = super.saveCreditMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCreditReceiptMedium.getSelectedItem().getValue();
		saveBasicValidation(paymentMedium, null);
		mpc.setPaymentMedium(paymentMedium);
		mpc.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpc.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpc;
	}

	@Override
	protected MedioPago savePagoAdelantadoMedioPago() throws Exception {
		if (cboPagoAdelantadoReceiptMedium == null || cboPagoAdelantadoReceiptMedium.getSelectedIndex() < 0)
			throw new Exception("@Invalid@ @"+Msg.translate(m_ctx, "ReceiptMedium")+"@");
		MedioPago mp = super.savePagoAdelantadoMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboPagoAdelantadoReceiptMedium.getSelectedItem().getValue();
		saveBasicValidation(paymentMedium, null);
		mp.setPaymentMedium(paymentMedium);
		mp.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mp.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mp;
	}

	protected boolean canEditTreeNode(MyTreeNode treeNode) {
		return treeNode.isMedioPago() || treeNode.isRetencion();
	}

	@Override
	protected void cmdEditMedioPago(MyTreeNode tn) {
		if (!(tn.getUserObject() instanceof RetencionProcessor)) {
			super.cmdEditMedioPago(tn);
			return;
		}

		RetencionProcessor retencion = (RetencionProcessor) tn.getUserObject();
		getCobroModel().removeRetencion(retencion);
		loadRetencion(retencion);
	}

	@Override
	protected void cmdDeleteMedioPago(MyTreeNode tn) {
		if (!(tn.getUserObject() instanceof RetencionProcessor)) {
			super.cmdDeleteMedioPago(tn);
			return;
		}

		RetencionProcessor retencion = (RetencionProcessor) tn.getUserObject();
		if (confirmDeleteMP(retencion))
			getCobroModel().removeRetencion(retencion);
	}

	private void loadRetencion(RetencionProcessor retencion) {
		retencSchema.setValue(retencion.getRetencionSchema()
				.getC_RetencionSchema_ID());
		txtRetencNroRetenc.setValue(retencion.getRetencionNumber());
		txtRetencImporte
				.setValue(getModel().numberFormat(retencion.getAmount()));
		retencFecha.setValue(retencion.getDateTrx());
	}

	@Override
	protected String getMsg(String name) {
		if (msgChanges.containsKey(name))
			name = msgChanges.get(name);
		return super.getMsg(name);
	}

	@Override
	protected void updatePaymentsTabsState() {
		removeTenderTypesValuesByCustomConditions();
		updateCustomPaymentsTabsState();
		updatePayAmt(getModel().getSaldoMediosPago());
	}

	@Override
	protected void loadMedioPago(VOrdenPagoModel.MedioPago mp) {
		if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_TARJETACREDITO)) {
			mpTabbox.setSelectedIndex(m_creditCardTabIndex); // jTabbedPane2.setSelectedIndex(m_creditCardTabIndex);
			MedioPagoTarjetaCredito tarjeta = (MedioPagoTarjetaCredito) mp;
			cboCreditCardReceiptMedium.setSelectedItemValue(tarjeta.getPaymentMedium());
			cboEntidadFinancieraPlans.setSelectedItemValue(tarjeta.getEntidadFinancieraPlan());
			txtCreditCardCouponNo.setValue(tarjeta.getCouponNo());
			txtCreditCardNo.setValue(tarjeta.getCreditCardNo());
			txtCreditCardAmt.setValue(getModel().numberFormat(tarjeta.getImporte()));
			txtCuotasCount.setValue(""
					+ tarjeta.getEntidadFinancieraPlan().getCuotasPago());
			txtCuotaAmt.setValue(getModel().numberFormat(tarjeta.getCuotaAmt()));
			cboCreditCardBank.setValue(tarjeta.getBank());
		} else {
			super.loadMedioPago(mp);
			Combobox comboReceiptMedium = tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()); // .get(jTabbedPane2.getSelectedIndex());
			comboReceiptMedium.setSelectedItemValue(mp.getPaymentMedium());
		}
		// Selección automática de pestañas a partir del tender type
		int indexSelected = mpTabbox.getSelectedIndex(); // jTabbedPane2.getSelectedIndex();
		int cboIdx = -1, i = 0;
		for (i=0; i < cboTenderType.getItemCount() && cboIdx == -1; i++)
			if (tabsTenderTypeRelations.get(indexSelected).equals(((ValueNamePair)(cboTenderType.getItemAtIndex(i).getValue())).getValue()) ) 
				cboIdx = i;
		cboTenderType.setSelectedIndex(cboIdx);
		mpTabbox.setSelectedIndex(indexSelected);
	}

	/**
	 * Crea un combo con los medios de pago relacionados con el tipo de medio de
	 * pago parámetro. Lista vacía si no existen medios de pago configurados
	 * 
	 * @param tenderType
	 *            tipo de medio de pago
	 * @return combo con los medios de pago configurados
	 */
	protected Combobox createPaymentMediumCombo(String tenderType) {
		int currencyID = ( (Integer) cboCurrency.getValue() == null) ? m_C_Currency_ID : (Integer) cboCurrency.getValue(); 
		
		// Creo la lista a partir del tipo de medio de pago
		List<MPOSPaymentMedium> mediums = getCobroModel().getPaymentMediums(
				tenderType, currencyID);
		Combobox combo = new Combobox();
		if (mediums != null) {
			for (MPOSPaymentMedium medium : mediums)
				combo.appendItem(medium.getName(), medium);
		}
//		combo.setMandatory(true);		// FEDE:TODO
		
		return combo;
	}
	
	/**
	 * Crea un combo con los medios de pago relacionados con el tipo de medio de
	 * pago parámetro. Lista vacía si no existen medios de pago configurados
	 * 
	 * @param tenderType
	 *            tipo de medio de pago
	 * @return combo con los medios de pago configurados
	 */
	protected void updatePaymentMediumCombo(String tenderType) {
		tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()).removeAllItems();  // jTabbedPane2.getSelectedIndex()		
		
		// Creo la lista a partir del tipo de medio de pago
		List<MPOSPaymentMedium> mediums = getCobroModel().getPaymentMediums(tenderType, (Integer) cboCurrency.getValue());
		
		if (mediums != null) {
			for(MPOSPaymentMedium medium: mediums){
				tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()).appendItem(medium.getName(), medium); 
			}
			//combo = new CComboBox(mediums.toArray());
		} 
	}
	
	/**
	 * Cargo los planes de la entidad financiera del medio de pago parámetro
	 * 
	 * @param paymentMedium
	 *            medio de pago
	 */
	protected void loadPlans(MPOSPaymentMedium paymentMedium) {
		cboEntidadFinancieraPlans.removeAllItems();
		if (paymentMedium.getM_EntidadFinanciera_ID() > 0) {
			// Obtener los planes de la entidad financiera configurada en el
			// medio de pago
			List<MEntidadFinancieraPlan> plans = getCobroModel().getPlans(
					paymentMedium.getM_EntidadFinanciera_ID());
			if (plans != null && plans.size() > 0) {
				for (MEntidadFinancieraPlan mEntidadFinancieraPlan : plans) {
					cboEntidadFinancieraPlans.appendItem(mEntidadFinancieraPlan.getName(), mEntidadFinancieraPlan); // addItem(mEntidadFinancieraPlan);
				}
				cboEntidadFinancieraPlans.setSelectedIndex(0);
				loadPlanInfo((MEntidadFinancieraPlan) cboEntidadFinancieraPlans.getSelectedItem().getValue());
			}
		}
	}

	/**
	 * Carga la info relacionado con este plan
	 * 
	 * @param plan
	 *            plan
	 */
	protected void loadPlanInfo(MEntidadFinancieraPlan plan) {
		// Para Tarjetas y Cheques se carga el Banco asociado al MP en el combo
		// de Bancos.
		// Si tiene banco el MP entonces no puede ser modificado por el usuario.
		// Obtengo el medio de pago seleccionado

		MPOSPaymentMedium mp = (MPOSPaymentMedium) cboCreditCardReceiptMedium.getSelectedItem().getValue();
		if (mp != null){
			if (mp.getBank() != null) {
				cboCreditCardBank.setValue(mp.getBank());
				cboCreditCardBank.setReadWrite(false);
				// Si no tiene banco el combo es editable y deberá elegir una
				// opción.
			} else {
				cboCreditCardBank.setValue(null);
				cboCreditCardBank.setReadWrite(true);
			}
			// Cuotas
			txtCuotasCount.setValue("" + plan.getCuotasPago());
			txtCuotasCount.setReadWrite(false);
			// Actualizar datos de plan y medio de pago
			updateDiscount(mp);
			refreshPaymentMediumAmountInfo(mp);
		}		
	}

	/**
	 * Recalcula descuentos / recargos y toda la información adicional del medio
	 * de pago para ser mostrada en los componentes gráficos
	 * 
	 * @param paymentMedium
	 *            medio de pago
	 */
	private void refreshPaymentMediumAmountInfo(MPOSPaymentMedium paymentMedium) {
		// Determinar el esquema de descuento dependiendo el medio de pago
		// parámetro
		MDiscountSchema discountSchema = null;
		if (paymentMedium != null) {
			// Si el medio de pago es de tipo tarjeta de crédito se debe tomar
			// el descuento del plan seleccionado
			if (paymentMedium.getTenderType().equals(
					MPOSPaymentMedium.TENDERTYPE_CreditCard)) {
				MEntidadFinancieraPlan plan = getSelectedPlan();
				discountSchema = getCobroModel().getDiscountFrom(plan);
			} else {
				discountSchema = getCobroModel().getDiscountFrom(paymentMedium);
			}
		}
		// Calcula el importe a pagar (aplicando descuentos / recargos del
		// medio de pago actualmente seleccionado) y lo muestra en el
		// componente.
		BigDecimal paymentToPayAmt = getCobroModel().getToPayAmount(discountSchema);
		paymentToPayAmt = getModel().scaleAmount(
				paymentToPayAmt,
				paymentMedium != null ? paymentMedium.getC_Currency_ID() : Env
						.getContextAsInt(m_ctx, "$C_Currency_ID"),
				BigDecimal.ROUND_HALF_EVEN);

		txtPaymentToPayDiscount.setValue(paymentToPayAmt
				.compareTo(BigDecimal.ZERO) > 0 ? (MCurrency.currencyConvert(
				paymentToPayAmt, m_C_Currency_ID,
				((Integer) cboCurrency.getValue()),
				new Timestamp(System.currentTimeMillis()),
				getModel().AD_Org_ID, m_ctx)).toString() : null);

		// Si es un pago con tarjeta de crédito se calcula y muestra el importe
		// de cada cuota.
		if (paymentMedium != null
				&& paymentMedium.getTenderType().equals(
						MPOSPaymentMedium.TENDERTYPE_CreditCard)) {
			MEntidadFinancieraPlan plan = getSelectedPlan();
			if (paymentToPayAmt != null) {
				txtCuotaAmt.setValue(getModel().numberFormat(paymentToPayAmt));
			}
			// El importe de la cuota se calcula en base al importe del pago
			// ingresado por el usuario
			if (plan != null) {
				try {
					BigDecimal amt = txtCreditCardAmt.getValue() == null ? BigDecimal.ZERO
							: getModel().numberParse(txtCreditCardAmt.getValue().toString());
					BigDecimal cuotaAmt = amt.divide(
							new BigDecimal(plan.getCuotasPago()), 10,
							BigDecimal.ROUND_HALF_UP);
					txtCuotaAmt.setValue(getModel().numberFormat(cuotaAmt));
				}
				catch (Exception e) { e.printStackTrace(); }
			}
		}
		// Refrescar el monto de la pestaña con el total a pagar
		
		if (paymentMedium != null){ 
			if(!paymentMedium.getTenderType().equals(MPOSPaymentMedium.TENDERTYPE_CreditCard)) {
				updatePayAmt(m_model.getSaldoMediosPago());
			}
		}
	}

	/**
	 * @return El medio de pago seleccionado en el ComboBox.
	 */
	protected MPOSPaymentMedium getSelectedPaymentMedium(CComboBox comboMP) {
		return (MPOSPaymentMedium) comboMP.getSelectedItem();
	}

	/**
	 * @return El plan seleccionado en el ComboBox (solo para medios de pago de
	 *         tipo tarjeta)
	 */
	protected MEntidadFinancieraPlan getSelectedPlan() {
		if (cboEntidadFinancieraPlans.getSelectedIndex() >= 0)
			return (MEntidadFinancieraPlan) cboEntidadFinancieraPlans.getSelectedItem().getValue();
		return null;
	}

	/**
	 * Cargo la info en la interfaz del medio pago de cheque seleccionado
	 * 
	 * @param paymentMedium
	 *            medio de pago
	 */
	protected void loadCheckInfo(MPOSPaymentMedium paymentMedium) {
		if(!paymentMedium.isNormalizedBank()){
			if (!Util.isEmpty(paymentMedium.getBank())) {
				txtChequeBanco.setValue(getCobroModel().getBankName(
						paymentMedium.getBank()));
				txtChequeBanco.setReadWrite(false);
			} else {
				txtChequeBanco.setReadWrite(true);
				txtChequeBanco.setValue("");
			}
		}
		else{
			if (paymentMedium.getC_Bank_ID() != 0) {
				cboChequeBancoID.setValue(paymentMedium.getC_Bank_ID());
				cboChequeBancoID.setReadWrite(false);
			} else {
				txtChequeBanco.setReadWrite(true);
				txtChequeBanco.setValue("");
			}
		}
		// Obtengo la configuración de días del medio de pago para la fecha de
		// vencimiento pago del cheque
		updateFechaPagoCheque(paymentMedium);
	}

	protected void updateBank(MPOSPaymentMedium paymentMedium) {
		cboChequeBancoID.setVisible(paymentMedium != null && paymentMedium.isNormalizedBank());
		txtChequeBanco.setVisible(paymentMedium == null	|| !paymentMedium.isNormalizedBank());
	}

	@Override
	protected void updateOrg(Integer AD_Org_ID) {
		super.updateOrg(AD_Org_ID);
		setPOS();
		updateOverdueInvoicesCharge();
		updateGroupingAmt(true);
	}

	/**
	 * Setea el punto de venta en base a la configuración de cajas diarias
	 * actual y se coloca como sólo lectura, si no se puede obtener, se debe
	 * dejar editable para ingresarlo manualmente.
	 */
	public void setPOS() {
		if (getCobroModel().LOCALE_AR_ACTIVE) {
			Integer pos = getCobroModel().getPOS();
			boolean existsPOS = !Util.isEmpty(pos, true);
			if (pos!=null)
				txtPOS.setValue(Integer.toString(pos));
			txtPOS.setReadWrite(!existsPOS);
			// Mensaje dialog en caso que sea posible obtener automáticamente un
			// nro de pto de venta pero no existe ninguno
			// Comentado para que no muestre el cartel al iniciar la ventana
			// if (!existsPOS && getCobroModel().mustGettingPOSNumber()) {
			// showInfo(VOrdenCobroModel.POS_ERROR_MSG);
			// }
		} else {
			txtPOS.getLabel().setVisible(false);
			txtPOS.setVisible(false);
		}
	}

	/**
	 * Actualizo la fecha de pago del cheque a partir de lo que contiene la
	 * fecha de emisión
	 * 
	 * @param paymentMedium
	 */
	protected void updateFechaPagoCheque(MPOSPaymentMedium paymentMedium) {
		Date fechaEmi = null; 
		if (chequeFechaEmision.getValue() == null) {
			fechaEmi = new Date();
			chequeFechaEmision.setValue(fechaEmi);
		} else {
			fechaEmi = new Timestamp(((Date)chequeFechaEmision.getValue()).getTime());  // chequeFechaEmision.getTimestamp();
		}
		chequeFechaPago.setValue(getCobroModel().getFechaPagoCheque(fechaEmi,
				paymentMedium));
	}

	@Override
	protected void updateTotalAPagar1() {
		// Actualizo el cargo de la organización para facturas vencidas
		updateOverdueInvoicesCharge();
		BigDecimal total = getModel().getSumaTotalPagarFacturas();
		txtTotalPagar1.setValue(numberFormat(total));
	}

    protected void updateCustomPaymentsTabsState(){
    	if(mpTabbox.getSelectedIndex() == TAB_INDEX_CHEQUE){
    		if(cboCheckReceiptMedium.getItemCount() > 0){
            	cboCheckReceiptMedium.setSelectedIndex(0);
            	loadCheckInfo((MPOSPaymentMedium) cboCheckReceiptMedium.getSelectedItem().getValue());
            }
    	}
    	else if(mpTabbox.getSelectedIndex() == m_creditCardTabIndex){
    		if(cboCreditCardReceiptMedium.getItemCount() > 0){
    			cboCreditCardReceiptMedium.setSelectedIndex(0);
    			loadPlans((MPOSPaymentMedium) cboCreditCardReceiptMedium.getSelectedItem().getValue());
    		}
    	}
    	else if(mpTabbox.getSelectedIndex() == TAB_INDEX_EFECTIVO){
			// Cargar el libro de caja por defecto de la caja diaria en caso que
			// se encuentre
    		setCashJournalToComponent();
    	}
    }
    
    /**
     * Elimina tender types del combo a partir de condiciones custom
     */
    protected void removeTenderTypesValuesByCustomConditions(){
    	int itemsCount = cboTenderType.getItemCount();
    	ValueNamePair value;
    	List<ValueNamePair> itemsToRemove = new ArrayList<ValueNamePair>();
    	for (int i = 0; i < itemsCount; i++) {
			value = (ValueNamePair)cboTenderType.getItemAtIndex(i).getValue();
			// Si el tender type es credito, nota de credito o cobro anticipado
			// y no es un pago normal (o sea, es una RCA) entonces los elimino
			// del combo para no generar problemas
			if (!getModel().isNormalPayment()
					&& (value.getValue().equals(
							MPOSPaymentMedium.TENDERTYPE_Credit)
							|| value.getValue().equals(
									MPOSPaymentMedium.TENDERTYPE_CreditNote) || value
							.getValue()
							.equals(MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt))) {
				itemsToRemove.add(value);
			}
		}
		// Itero por los ítems a eliminar y los elimino
		for (ValueNamePair item : itemsToRemove) {
			cboTenderType.removeItem(item);
		}
	}

	protected void updateDiscount(MPOSPaymentMedium paymentMedium) {
		// Obtener el descuento
		MDiscountSchema discountSchema = getSelectedPaymentMediumDiscountSchema(paymentMedium);
		// Determino si se puede aplicar el esquema para refrescar la interfaz
		// gráfica
		boolean isApplicable = discountSchema != null
				&& getCobroModel().isPaymentMediumDiscountApplicable(
						discountSchema.getDiscountContextType());
		// Actualizo el esquema de descuento general actual
		getCobroModel().updateGeneralDiscount(
				isApplicable ? discountSchema : null);
		// Componente de esquema de descuento actual de medio de pago
		txtPaymentDiscount.setValue(isApplicable ? discountSchema.toString()
				: null);

		// Indica al modelo que asuma o no que existe un descuento general
		// (que todavia no ha sido agregado). Este llamado además recalculará
		// todos los descuentos. Con esto tenemos el cuenta el caso de que la EC
		// tenga descuento pero que tenga prioridad el descuento del medio de
		// cobro.
		getCobroModel().setAssumeGeneralDiscountAdded(isApplicable);

		// Calcular el monto a pagar
		refreshPaymentMediumAmountInfo(paymentMedium);

		// Actualiza el Dto de EC y los totales (ya que se pueden haber
		// modificado por el recálculo de descuentos según prioridades).
		customUpdateBPartnerRelatedComponents(false);
		updateSummaryInfo();
	}

	protected MDiscountSchema getSelectedPaymentMediumDiscountSchema(
			MPOSPaymentMedium paymentMedium) {
		MDiscountSchema value = null;
		if (paymentMedium != null) {
			if (paymentMedium.getTenderType().equals(
					MPOSPaymentMedium.TENDERTYPE_CreditCard)) {
				value = getCobroModel().getDiscountFrom(getSelectedPlan());
			} else {
				value = getCobroModel().getDiscountFrom(paymentMedium);
			}
		}
		return value;
	}

	@Override
	protected void customUpdateBPartnerRelatedComponents(boolean loadingBP) {
		// Actualizar el descuento de la entidad comercial
		MDiscountSchema discountSchema = getCobroModel()
				.getbPartnerDiscountSchema(true);
		bPartnerDiscount.setValue(discountSchema == null ? null
				: discountSchema.getID());
		// Actualizar el monto de agrupación
		updateGroupingAmt(loadingBP);
	}

	/**
	 * Actualizar el monto de agrupacióngetTotalPaymentTermDiscount()
	 * 
	 * @param untilActualDueDate
	 *            true si se debe ingresar como monto inicial el total abierto
	 *            de facturas hasta que la fecha de vencimiento sea mayor a la
	 *            actual
	 */
	protected void updateGroupingAmt(boolean untilActualDueDate) {
		if (tabbox.getSelectedIndex() == 1)  // if (jTabbedPane1.getSelectedIndex() == 1)
			return;
		if (getModel().getBPartner() != null
				&& getModel().getBPartner().isGroupInvoices()
				&& radPayTypeStd.isSelected()) {
			txtGroupingAmt.getLabel().setVisible(true);
			txtGroupingAmt.setVisible(true);
			((OpenInvoicesCustomerReceiptsTableModel) getModel().m_facturasTableModel)
					.setAllowManualAmtEditable(false);
			rInvoiceDate.setVisible(false);
			rInvoiceAll.setVisible(false);
			invoiceDatePick.setVisible(false);
		} else {
			txtGroupingAmt.getLabel().setVisible(false);
			txtGroupingAmt.setVisible(false);
			((OpenInvoicesCustomerReceiptsTableModel) getModel().m_facturasTableModel)
					.setAllowManualAmtEditable(true);
			rInvoiceDate.setVisible(true);
			rInvoiceAll.setVisible(true);
			invoiceDatePick.setVisible(true);
		}
		// Actualizar el monto manual de las facturas
		updateGroupingAmtInvoices(untilActualDueDate);
		// Actualización del cargo si es necesario
		updateOverdueInvoicesCharge();
		// Actualizar el total a pagar
		updateTotalAPagar1();
		// Actualizar modelo
		resetModel();
	}

	/**
	 * Actualiza los montos manuales de las facturas informadas en base al monto
	 * de agrupación
	 * 
	 * @param untilActualDueDate
	 *            true si se debe ingresar como monto inicial el total abierto
	 *            de facturas hasta que la fecha de vencimiento sea mayor a la
	 *            actual
	 */
	protected void updateGroupingAmtInvoices(boolean untilActualDueDate) {
		if (radPayTypeStd.isSelected() && getModel().getBPartner() != null
				&& getModel().getBPartner().isGroupInvoices()) {
			// Obtener el monto para actualizar los montos manuales de las
			// facturas
			// El monto es lo que se encuentra en el text field de grupo,
			// restando
			// el cargo si es que existe
			// Actualizar los montos manuales
			if (untilActualDueDate) {
				setDefaultGroupAmtValue();
			}
			BigDecimal amt = BigDecimal.ZERO; 
			try {
				amt = (txtGroupingAmt.getValue() != null && txtGroupingAmt.getValue().toString().length() > 0 ? new BigDecimal(txtGroupingAmt.getValue().toString()) : BigDecimal.ZERO);
			}
			catch (Exception e) { e.printStackTrace(); } 
			getCobroModel().updateGroupingAmtInvoices(amt);
			// Actualizar modelo
			resetModel();		
		}
	}

	protected void setDefaultGroupAmtValue() {
		setGroupingAmt(getCobroModel().getDefaultGroupingValue());
	}

	protected void setGroupingAmt(BigDecimal groupingAmt) {
		txtGroupingAmt.setValue(groupingAmt.toString());
	}

	protected void loadPaymentMediumInfo(MPOSPaymentMedium paymentMedium) {
		String tenderType = paymentMedium.getTenderType();
		if (tenderType.equals(MPOSPaymentMedium.TENDERTYPE_Check)) {
			loadCheckInfo(paymentMedium);
		} else if (tenderType.equals(MPOSPaymentMedium.TENDERTYPE_CreditCard)) {
			loadPlans(paymentMedium);
		}
	}

	@Override
	protected void updateComponentsPreProcesar() {
		// Verificar si hay un tab seleccionado
		if (mpTabbox.getSelectedIndex() < 0)
			return;
		// Verificar que se recupere un valor correcto
		if (tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()) == null)
			return;
		if (tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()).getSelectedItem() == null)
			return;
		MPOSPaymentMedium selectedPaymentMedium = (MPOSPaymentMedium)(tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()).getSelectedItem().getValue());

		if (selectedPaymentMedium != null) {
			loadPaymentMediumInfo(selectedPaymentMedium);
		}
		// Actualizar a pagar
		refreshPaymentMediumAmountInfo(selectedPaymentMedium);
		// Actualizar componentes de descuentos
		updateDiscount(selectedPaymentMedium);
	}

	@Override
	protected void updateSummaryInfo() {
		// Actualizo el componente visual con el total de descuentos
		txtDiscountAmt.setValue(numberFormat(getCobroModel().getSumaDescuentos()
				.negate()));
		// Actualizo el resumen como se realiza normalmente
		super.updateSummaryInfo();
	}

	@Override
	protected void updateCustomInfoAfterMedioPago(Integer medioPagoAction) {
		// Actualizar descuento de entidad comercial
		getCobroModel().updateAditionalInfo();
		customUpdateBPartnerRelatedComponents(false);
		getCobroModel().applyBPartnerDiscount();
		// Actualizar descuentos en total
		getCobroModel().updateDiscounts();
		// Actualizar panel de resumen total
		updateSummaryInfo();
		// Actualizar panel A Cobrar
		MPOSPaymentMedium selectedPaymentMedium = (MPOSPaymentMedium)tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()).getSelectedItem().getValue();
		// Si estamos editando no debemos actualizar la info del medio de pago,
		// sino si
		updateDiscount(selectedPaymentMedium);
		if (medioPagoAction != null && medioPagoAction != MEDIOPAGO_ACTION_EDIT) {
			loadPaymentMediumInfo(selectedPaymentMedium);
			if (selectedPaymentMedium != null && selectedPaymentMedium.getTenderType().equals(MPOSPaymentMedium.TENDERTYPE_CreditCard)) {
				updatePayAmt(m_model.getSaldoMediosPago());
				refreshPaymentMediumAmountInfo(selectedPaymentMedium);
			}
		}
//		repaint();
	}

	@Override
	protected void makeOperationsBeforePreProcesar() throws Exception {
		updateOverdueInvoicesCharge();
	}

	@Override
	protected void updateCustomTipoPagoChange() {
		updateGroupingAmt(false);
		if (radPayTypeStd.isSelected()) {
			txtOrgCharge.getLabel().setVisible(true);
			txtOrgCharge.setVisible(true);
		} else {
			txtOrgCharge.getLabel().setVisible(false);
			txtOrgCharge.setVisible(false);
		}
	}

	/**
	 * Verifico si es posible cobrar cargos de organización por cobro de
	 * facturas vencidas
	 */
	protected void updateOverdueInvoicesCharge() {
		getCobroModel().updateOverdueInvoicesCharge();
		txtOrgCharge.setValue(getCobroModel().getOrgCharge().toString());
	}

	@Override
	protected void updatePayAllInvoices(boolean toPayMoment) {
		boolean isSelected = checkPayAll.isSelected();
		BigDecimal amt = getModel().updatePayAllInvoices(isSelected,
				toPayMoment);
		if (isSelected) {
			setGroupingAmt(amt);
		} else {
			updateGroupingAmt(false);
		}
		// Actualización del cargo si es necesario
		updateOverdueInvoicesCharge();
		// Actualizar el total a pagar
		updateTotalAPagar1();
		txtGroupingAmt.setReadWrite(!isSelected);
		resetModel();
	}

    @Override
    protected void updateCustomPayAmt(BigDecimal amt){
    	Integer tabIndexSelected = mpTabbox.getSelectedIndex();		// jTabbedPane2.getSelectedIndex();
    	if(tabIndexSelected.equals(m_retencTabIndex)){
    		txtRetencImporte.setValue(numberFormat(amt));
    	}
    	else if(tabIndexSelected.equals(m_creditCardTabIndex)){
    		txtCreditCardAmt.setValue(numberFormat(amt));
    		refreshPaymentMediumAmountInfo((MPOSPaymentMedium) cboCreditCardReceiptMedium.getSelectedItem().getValue());
    	}
    }
    
    @Override
    protected void reset(){
    	super.reset();
    	updateGroupingAmt(false);
    	setPOS();
    }
    
    protected void setCashJournalToComponent(){
		efectivoLibroCaja.setValue(getCobroModel().getCashID());
    }
    
    public void setPaymentMediumItemListener(PaymentMediumItemListener paymentMediumItemListener) {
		this.paymentMediumItemListener = paymentMediumItemListener;
	}

	public PaymentMediumItemListener getPaymentMediumItemListener() {
		return paymentMediumItemListener;
	}

	private class PaymentMediumItemListener implements EventListener {

		@Override
		public void onEvent(Event arg0) throws Exception {
			if (arg0.getTarget() == null)
				return;
			MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium)(((ComboItem)(((Combobox)arg0.getTarget()).getSelectedItem())).getValue());
			loadPaymentMediumInfo(paymentMedium);
			updateDiscount(paymentMedium);
			updateBank(paymentMedium);
		}
	}

	@Override
	protected void customKeyBindingsInit() {
//		getActionKeys().put(GOTO_TENDER_TYPE,
//				KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
//
//		// Accion: Seleccionar el combo de tipo de pago
//		m_frame.getRootPane().getActionMap()
//				.put(GOTO_TENDER_TYPE, new AbstractAction() {
//					public void actionPerformed(ActionEvent e) {
//						cboTenderType.getComponent().focus();  // cboTenderType.requestFocus();
//					}
//				});
	}

	@Override
	protected void customUpdateCaptions() {
//		if (jTabbedPane1.getSelectedIndex() == 0) {
//			setActionEnabled(GOTO_TENDER_TYPE, false);
//			setActionEnabled(GOTO_BPARTNER, true);
//		} else if (jTabbedPane1.getSelectedIndex() == 1) {
//			setActionEnabled(GOTO_TENDER_TYPE, true);
//		}
	}

	public String getBankName() {
		if (txtChequeBanco.isVisible())
			return txtChequeBanco.getValue().toString();
		else if(cboChequeBancoID.getValue() == null)
				return null;
			else
				return getModel().getBank((Integer) cboChequeBancoID.getValue());		
	}


    /**
     * Agrega los campos superiores en la pestaña de facturas a pagar
     */
    protected void createPaymentSelectionTopFields() {

    	// Descuento de entidad comercial
		MLookup lookupBPDiscount = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, "M_DiscountSchema_ID", "M_DiscountSchema", DisplayType.Search);
		bPartnerDiscount = new WSearchEditor ("M_DiscountSchema_ID", false, false, true, lookupBPDiscount);
		addPopupMenu(bPartnerDiscount, true, true, false);
		bPartnerDiscount.setReadWrite(false);

    	Rows rows = jPanel1.newRows();
    	Row row = rows.newRow();
    	row.appendChild(cboClient.getLabel().rightAlign());
    	row.appendChild(cboClient.getComponent());
    	
    	row.appendChild(new Space());
    	row.appendChild(cboOrg.getLabel().rightAlign());
    	row.appendChild(cboOrg.getComponent());
    	row.appendChild(new Space());
    	
    	row = rows.newRow();
    	row.appendChild(BPartnerSel.getLabel().rightAlign());
    	row.appendChild(BPartnerSel.getComponent());
    	row.appendChild(new Space());
    	row.appendChild(bPartnerDiscount.getLabel().rightAlign());
    	row.appendChild(bPartnerDiscount.getComponent());
    	row.appendChild(new Space());
    	
    	row = rows.newRow();
    	row.appendChild(fldDocumentNo.getLabel().rightAlign());
    	row.appendChild(fldDocumentNo.getComponent());
    	txtDescription.getComponent().setWidth("100%");
    	row.appendChild(new Space());

    	row.appendChild(cboDocumentType.getLabel().rightAlign());
    	row.appendChild(cboDocumentType.getComponent());
    	row.appendChild(new Space());

    }
    
    
	/**
	 * Agrega campo de PayAll (u otros en subclase)
	 */
	protected void createPayAllDiv(Div divCheckPayAll) {
		divCheckPayAll.setAlign("end");
		divCheckPayAll.appendChild(txtGroupingAmt.getLabel());
		divCheckPayAll.appendChild(txtGroupingAmt.getComponent());
		divCheckPayAll.appendChild(txtPOS.getLabel());
		divCheckPayAll.appendChild(txtPOS.getComponent());
		divCheckPayAll.appendChild(checkPayAll);
	}
    
	/**
	 * Seteo de campos en la part inferior derecha
	 */
	protected void setDivTxtTotal(Div divTxtEfectivoImporte) {
		
		divTxtEfectivoImporte.setAlign("end");
		divTxtEfectivoImporte.appendChild(txtOrgCharge.getLabel());
		divTxtEfectivoImporte.appendChild(txtOrgCharge.getComponent());
		divTxtEfectivoImporte.appendChild(txtTotalPagar1.getLabel());
		divTxtEfectivoImporte.appendChild(txtTotalPagar1.getComponent());		
	}

	
	protected void initComponents() {
		
		// Inicializaciones especificas para WOrdenCobro
		txtOrgCharge = new WStringEditor();
		txtOrgCharge.setValue(BigDecimal.ZERO.toString());
		txtOrgCharge.setReadWrite(false);
		txtPOS = new WStringEditor();
		// Setear el valor del punto de venta
		txtPOS.getComponent().addEventListener("onChange", new EventListener() {			
			@Override
			public void onEvent(Event arg0) throws Exception {
				getCobroModel().setPOS(Integer.parseInt(txtPOS.getValue().toString()));
				
			}
		});

		// Monto de agrupación de la entidad comercial
		txtGroupingAmt = new WStringEditor();
		txtGroupingAmt.setValue(BigDecimal.ZERO.toString());
		txtGroupingAmt.getComponent().addEventListener("onChange", new EventListener() {		
			@Override
			public void onEvent(Event arg0) throws Exception {
				updateGroupingAmt(false);
				
			}
		});

		
		// Demas inicializaciones
		super.initComponents();
	}
	
	
	/**
	 * Fuerza el reseteo del modelo de la Grid en base a los datos del modelo de VOrdenPagoModel
	 */
	protected void resetModel() {
		super.resetModel();
		ocultarCanje();		
	}
	
	/**
	 * Si hay que agregar mas campos, se deberá recibir Rows rows, en lugar de Row row
	 * @param row
	 */
	protected void addFieldsToCampProy(Row row) {
		row.appendChild(lblTenderType.rightAlign());
		row.appendChild(cboTenderType);
	}
	
	
    protected String getReportName() {
    	return "Recibo de Cliente";
    }
}
