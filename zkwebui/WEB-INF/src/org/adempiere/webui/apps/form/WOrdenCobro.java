package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.webui.apps.form.WOrdenPago.FacturasModel;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Datebox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.compiere.swing.CComboBox;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.apps.form.VModelHelper;
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
import org.zkoss.zul.Panel;
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

	private Label lblRetencSchema = new Label();
	private Label lblRetencFecha = new Label();
	private Label lblRetencImporte = new Label();
	private Label lblRetencNroRetenc = new Label();
	protected Label lblTenderType;
	protected Label lblDiscountAmt;
	protected Label lblPaymentDiscount;
	protected Label lblPaymentToPayDiscount;
	protected Label lblCreditCardBank;
	protected Label lblCreditCardNo;
	protected Label lblCreditCardCouponNo;
	protected Label lblCuotasCount;
	protected Label lblCuotaAmt;
	protected Label lblCreditCardPlan;
	protected Label lblCreditCardAmt;
	protected Label lblBPartnerDiscount;
	protected Label lblPOS = new Label();
	protected Label lblOrgCharge = new Label();
	protected Label lblGroupingAmt = new Label();

	protected Label lblCreditCardReceiptMedium = new Label();
	protected Label lblCheckReceiptMedium = new Label();
	protected Label lblCreditReceiptMedium = new Label();
	protected Label lblCashReceiptMedium = new Label();
	protected Label lblTransferReceiptMedium = new Label();
	protected Label lblRetencionReceiptMedium = new Label();
	protected Label lblPagoAdelantadoReceiptMedium = new Label();
	private Datebox retencFecha;
	private WSearchEditor retencSchema;
	protected WSearchEditor bPartnerDiscount;
	protected Textbox txtPOS;
	protected Textbox txtOrgCharge;
	protected Textbox txtGroupingAmt;
	protected Combobox cboTenderType;
	private Textbox txtRetencImporte = new Textbox();
	private Textbox txtRetencNroRetenc;
	protected Textbox txtDiscountAmt;
	protected Textbox txtPaymentDiscount;
	protected Textbox txtPaymentToPayDiscount;
	protected WTableDirEditor cboCreditCardBank;
	protected Textbox txtCreditCardNo;
	protected Textbox txtCreditCardCouponNo;
	protected Textbox txtCuotasCount;
	protected Textbox txtCuotaAmt;
	protected Textbox txtCreditCardAmt = new Textbox();
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
	protected WTableDirEditor createChequeChequeraLookup() {
		// return VComponentsFactory.VLookupFactory("C_BankAccount_ID", "C_BankAccount", m_WindowNo, DisplayType.TableDir, getModel().getChequeChequeraSqlValidation());
        MLookupInfo infoTransf = VComponentsFactory.MLookupInfoFactory(Env.getCtx(),m_WindowNo, 0, 3077, DisplayType.TableDir, m_model.getChequeChequeraSqlValidation());
		MLookup lookupTransf = new MLookup(infoTransf, 0);
		return new WTableDirEditor( "C_BankAccount_ID",false,false,true,lookupTransf );
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
		lblChequeChequera.setText(Msg.translate(m_ctx, "C_BankAccount_ID"));

		// Retencions
		lblRetencSchema.setValue(Msg.translate(m_ctx, "C_Withholding_ID"));
		lblRetencNroRetenc.setValue(Msg.translate(m_ctx, "RetencionNumber"));
		lblRetencImporte.setValue(Msg.getElement(m_ctx, "Amount"));
		lblRetencFecha.setValue(Msg.translate(m_ctx, "Date"));

		//
		radPayTypeStd.setValue(Msg.translate(m_ctx, "StandardCustomerPayment"));
		radPayTypeAdv.setValue(Msg.translate(m_ctx, "AdvancedCustomerPayment"));
		lblMedioPago2.setValue(Msg.translate(m_ctx, "CustomerTenderType"));

		checkPayAll.setText(Msg.getMsg(m_ctx, "ReceiptAll") /* + " "
				+ KeyUtils.getKeyStr(getActionKeys().get(GOTO_PAYALL)) */ );

		// FEDE:TODO esto quizás debe estar en zkinit
		tabPaymentSelection = new Tab(Msg.getMsg(Env.getCtx(), "CustomerPaymentSelection"));  // jTabbedPane1.setTitleAt(0, Msg.translate(m_ctx, "CustomerPaymentSelection"));
		tabPaymentRule = new Tab(Msg.getMsg(Env.getCtx(), "CustomerPaymentRule")); // jTabbedPane1.setTitleAt(1, Msg.translate(m_ctx, "CustomerPaymentRule"));
		//
		
		lblBPartnerDiscount.setValue(Msg.translate(m_ctx, "M_DiscountSchema_ID"));
		lblPOS.setValue(Msg.translate(m_ctx, "RealPOS"));
		lblOrgCharge.setValue(Msg.translate(m_ctx, "OrgCharge"));
		lblGroupingAmt.setValue(Msg.translate(m_ctx, "GroupingAmt"));

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

		// FEDE:TODO ver si es necesario especificar algo de esto
//		tblFacturas
//				.getColumnModel()
//				.getColumn(tblFacturas.getColumnModel().getColumnCount() - 4)
//				.setCellRenderer(
//						new MyNumberTableCellRenderer(getModel()
//								.getNumberFormat()));
//
//		tblFacturas.getColumnModel()
//				.getColumn(tblFacturas.getColumnModel().getColumnCount() - 3)
//				.setCellRenderer(new VCellRenderer(DisplayType.YesNo));

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
//		initFormattedTextField((JFormattedTextField) txtRetencImporte);
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
		
		// FEDE:TODO MIGRAR DISEÑO DESDE VOrdenCobro
		
		Tabpanel tabpanel = new Tabpanel();
    	tabpanel.setHeight("150px");

		lblEfectivoLibroCaja.setText("LIBRO DE CAJA");
		lblEfectivoImporte.setText("IMPORTE");
		txtEfectivoImporte.setText("0");
		cboCashReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_Cash);
		cboCashReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_EFECTIVO, cboCashReceiptMedium);  
    	txtEfectivoImporte.setText("0");
	
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblEfectivoLibroCaja.rightAlign());
		row.appendChild(efectivoLibroCaja.getComponent());
		row.appendChild(lblEfectivoImporte.rightAlign());
		row.appendChild(txtEfectivoImporte);
		
		tabpanel.appendChild(gridpanel);
        return tabpanel;

	}

	@Override
	protected Tabpanel  createTransferTab() {
		
		// FEDE:TODO MIGRAR DISEÑO DESDE VOrdenCobro		
		
		Tabpanel tabpanel = new Tabpanel();
		lblTransfCtaBancaria.setText("CUENTA BANCARIA");
		lblTransfNroTransf.setText("NRO TRANSFERENCIA");
		lblTransfImporte.setText("IMPORTE");
		lblTransfFecha.setText("FECHA");
		txtTransfImporte.setText("0");
		cboTransferReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_DirectDeposit);
		cboTransferReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_TRANSFERENCIA, cboTransferReceiptMedium);

        lblTransfCtaBancaria.setText("CUENTA BANCARIA");
        lblTransfNroTransf.setText("NRO TRANSFERENCIA");
        lblTransfImporte.setText("IMPORTE");
        lblTransfFecha.setText("FECHA");
        txtTransfImporte.setText("0");

    	tabpanel.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblTransfCtaBancaria.rightAlign());
		row.appendChild(transfCtaBancaria.getComponent());
		row.appendChild(lblTransfNroTransf.rightAlign());
		row.appendChild(txtTransfNroTransf);
		Row row2 = rows.newRow();
		row2.appendChild(lblTransfImporte.rightAlign());
		row2.appendChild(txtTransfImporte);
		row2.appendChild(lblTransfFecha.rightAlign());
		row2.appendChild(transFecha.getComponent());
		
		txtTransfImporte.setText("0");
		
		tabpanel.appendChild(gridpanel);

		return tabpanel; // jPanel6;
	}

	@Override
	protected Tabpanel createCheckTab() {
		
		// FEDE:TODO MIGRAR DISEÑO DESDE VOrdenCobro
		
		Tabpanel tabpanel = new Tabpanel();
		
        lblChequeChequera.setText("CHEQUERA");
        lblChequeNroCheque.setText("NUMERO DE CHEQUE");
        lblChequeImporte.setText("IMPORTE");
        lblChequeFechaEmision.setText("FECHA EMISION");
        lblChequeFechaPago.setText("FECHA PAGO");
        lblChequeALaOrden.setText(getModel().isSOTrx()?"LIBRADOR":"A LA ORDEN");
        lblChequeBanco.setText("BANCO");
        lblChequeCUITLibrador.setText("CUIT LIBRADOR");
        lblChequeDescripcion.setText("DESCRIPCION");      

    	tabpanel.setHeight("150px");
    	
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblChequeChequera.rightAlign());
		row.appendChild(chequeChequera.getComponent());
		row.appendChild(lblChequeNroCheque.rightAlign());
		row.appendChild(txtChequeNroCheque);
		Row row2 = rows.newRow();
		row2.appendChild(lblChequeImporte.rightAlign());
		row2.appendChild(txtChequeImporte);
		Row row3 = rows.newRow();
		row3.appendChild(lblChequeFechaEmision.rightAlign());
		row3.appendChild(chequeFechaEmision.getComponent());
		row3.appendChild(lblChequeFechaPago.rightAlign());
		row3.appendChild(chequeFechaPago.getComponent());
		Row row4 = rows.newRow();
		row4.appendChild(lblChequeALaOrden.rightAlign());
		row4.appendChild(txtChequeALaOrden);
		row4.appendChild(lblChequeBanco.rightAlign());
		row4.appendChild(txtChequeBanco);
		Row row5 = rows.newRow();
		row5.appendChild(lblChequeCUITLibrador.rightAlign());
		row5.appendChild(txtChequeCUITLibrador);
		row5.appendChild(lblChequeDescripcion.rightAlign());
		row5.appendChild(txtChequeDescripcion);

		tabpanel.appendChild(gridpanel);
 
		
		
		lblChequeChequera.setText("CHEQUERA");
		lblChequeNroCheque.setText("NUMERO DE CHEQUE");
		lblChequeImporte.setText("IMPORTE");
		lblChequeFechaEmision.setText("FECHA EMISION");
		lblChequeFechaPago.setText("FECHA PAGO");
		lblChequeALaOrden.setText(getModel().isSOTrx() ? "LIBRADOR"
				: "A LA ORDEN");
		lblChequeBanco.setText("BANCO");
		lblChequeCUITLibrador.setText("CUIT LIBRADOR");
		lblChequeDescripcion.setText("DESCRIPCION");
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
		if (cboCheckReceiptMedium.getSelectedIndex() > 0 && cboCheckReceiptMedium.getModel().getElementAt(cboCheckReceiptMedium.getSelectedIndex()) != null)
			updateBank((MPOSPaymentMedium) cboCheckReceiptMedium.getSelectedItem().getValue());

		return tabpanel; 
	}

	@Override
	protected Tabpanel  createCreditTab() {
		
		// FEDE:TODO MIGRAR DISEÑO DESDE VOrdenCobro
		
		Tabpanel tabpanel = new Tabpanel();
		lblCreditInvoice.setText("CREDITO");
		lblCreditAvailable.setText("DISPONIBLE");
		lblCreditImporte.setText("IMPORTE");
		txtCreditAvailable.setText("0");
		txtCreditImporte.setText("0");
		cboCreditReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_CreditNote);
		cboCreditReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_CREDITO, cboCreditReceiptMedium);

    	tabpanel.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblCreditInvoice.rightAlign());
		row.appendChild(creditInvoice.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(lblCreditAvailable.rightAlign());
		row2.appendChild(txtCreditAvailable);
		Row row3 = rows.newRow();
		row3.appendChild(lblCreditImporte.rightAlign());
		row3.appendChild(txtCreditImporte);
		
        txtCreditAvailable.setText("0");        
        txtCreditImporte.setText("0");
		
		tabpanel.appendChild(gridpanel);
		return tabpanel; // jPanel11;
	}

	private Tabpanel  createRetencionTab() {
		panelRetenc = new Tabpanel();
//		panelRetenc.setOpaque(false);

		lblRetencSchema = new Label();
		lblRetencSchema.setValue("RETENCION");
		lblRetencNroRetenc = new Label();
		lblRetencNroRetenc.setText("NRO RETENCION");
		lblRetencImporte = new Label();
		lblRetencImporte.setText("IMPORTE");
		lblRetencFecha = new Label();
		lblRetencFecha.setText("FECHA");
		txtRetencImporte = new Textbox();
		txtRetencImporte.setText("0");
//		initFormattedTextField((JFormattedTextField) txtRetencImporte);
		txtRetencNroRetenc = new Textbox();
		MLookupInfo infoRetencSchema = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, 1003040, DisplayType.Search, getCobroModel().getRetencionSqlValidation());
		MLookup lookupRetencSchema = new MLookup(infoRetencSchema, 0);
		retencSchema = new WSearchEditor("C_DocType_ID", false, false, true, lookupRetencSchema);

		
		retencFecha = new Datebox(); 
		cboRetencionReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_Retencion);
		cboRetencionReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());

		
		// FEDE:TODO completar
		
		return panelRetenc;
	}

	protected Tabpanel  createCreditCardTab() {
		
		// FEDE:TODO pendiente a migrar de VOrdenCobro
		
		panelCreditCard = new Tabpanel();
		lblCreditCardPlan = new Label(getMsg("CreditCardPlan"));
		lblCreditCardBank = new Label(getMsg("C_Bank_ID"));
		lblCreditCardNo = new Label(getMsg("CreditCardNumber"));
		lblCreditCardCouponNo = new Label(getMsg("CouponNumber"));
		lblCuotasCount = new Label(getMsg("CuotasCount"));
		lblCuotaAmt = new Label(getMsg("CuotaAmt"));
		lblCreditCardAmt = new Label(getMsg("Amt"));
		lblCreditCardReceiptMedium = new Label();
		MLookup lookupCreditCardBank = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 1014558, DisplayType.List); // columna Bank de C_POSPaymentMedium
		cboCreditCardBank = new WTableDirEditor("Bank", false, false, true, lookupCreditCardBank);
		
		txtCreditCardNo = new Textbox();
		txtCreditCardCouponNo = new Textbox();
		txtCuotasCount = new Textbox();
		txtCuotaAmt = new Textbox();
//		initFormattedTextField(txtCuotaAmt);
		txtCuotaAmt.setReadonly(true);
		txtCreditCardAmt = new Textbox();
		txtCreditCardAmt.addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (arg0.getData() != null) {
					refreshPaymentMediumAmountInfo((MPOSPaymentMedium) cboCreditCardReceiptMedium.getModel().getElementAt(cboCreditCardReceiptMedium.getSelectedIndex()));
				}
			}
		});
		
		txtCreditCardAmt.addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (arg0.getData() != null) {
					refreshPaymentMediumAmountInfo((MPOSPaymentMedium) cboCreditCardReceiptMedium.getModel().getElementAt(cboCreditCardReceiptMedium.getSelectedIndex()));
				}
			}
		});

		cboCreditCardReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_CreditCard);
		cboCreditCardReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
		cboEntidadFinancieraPlans = new Combobox();
//		cboEntidadFinancieraPlans.setPreferredSize(new Dimension(
//				cboEntidadFinancieraPlans.getPreferredSize().width, 20));
//		cboEntidadFinancieraPlans.setMandatory(true);
		cboEntidadFinancieraPlans.addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (arg0.getData() != null) {
					loadPlanInfo((MEntidadFinancieraPlan) cboEntidadFinancieraPlans.getSelectedItem().getValue());
				}
			}
		});
		
		txtCreditCardAmt.setValue(getModel().getSaldoMediosPago().toString());

		panelCreditCard.setHeight("150px");
		
    	Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		
    	Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblCreditInvoice.rightAlign());
		row.appendChild(creditInvoice.getComponent());
		Row row2 = rows.newRow();
		row2.appendChild(lblCreditAvailable.rightAlign());
		row2.appendChild(txtCreditAvailable);
		Row row3 = rows.newRow();
		row3.appendChild(lblCreditImporte.rightAlign());
		row3.appendChild(txtCreditImporte);
		
        txtCreditAvailable.setText("0");        
        txtCreditImporte.setText("0");
		
        panelCreditCard.appendChild(gridpanel);
		return panelCreditCard;
	}

	
	@Override
	protected Tabpanel  createPagoAdelantadoTab() {
		panelPagoAdelantado = new Tabpanel();
//		panelPagoAdelantado.setOpaque(false);

		lblPagoAdelantadoCash = new Label();
		lblPagoAdelantadoPago = new Label();
		lblPagoAdelantadoCash.setText(getModel().isSOTrx() ? "COBRO" : "PAGO");
		lblPagoAdelantadoPago.setText(getModel().isSOTrx() ? "COBRO" : "PAGO");
		lblPagoAdelantadoImporte = new Label();
		lblPagoAdelantadoImporte.setValue("IMPORTE");
		txtPagoAdelantadoImporte = new Textbox();
		txtPagoAdelantadoImporte.setValue("0");
//		initFormattedTextField((JFormattedTextField) txtPagoAdelantadoImporte);
		MLookupInfo infoPago = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, 5043, DisplayType.Search, getModel().getPagoAdelantadoSqlValidation());
		Lookup lookupPago = new MLookup(infoPago, 0);
		pagoAdelantado = new WSearchEditor("C_Payment_ID", false, false, true, lookupPago);
			
        MLookupInfo infoCash = VComponentsFactory.MLookupInfoFactory( Env.getCtx(),m_WindowNo, 0, 5283, DisplayType.Search, getModel().getCashAnticipadoSqlValidation());
		Lookup lookupCash = new MLookup(infoCash, 0);
		cashAdelantado = new WSearchEditor("C_CashLine_ID", false, false, true, lookupCash);

		lblPagoAdelantadoType = new Label();
		lblPagoAdelantadoType.setText("TIPO");
		cboPagoAdelantadoType = new Combobox();
        cboPagoAdelantadoType.appendItem(getMsg("Payment"));
        cboPagoAdelantadoType.appendItem(getMsg("Cash"));		

		// Por defecto pago.
		cboPagoAdelantadoType
				.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX);
//		cboPagoAdelantadoType.setPreferredSize(new Dimension(200, 20));
		cboPagoAdelantadoType.addEventListener("onChange", new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				updatePagoAdelantadoTab();
			}
		});
		
		cboPagoAdelantadoReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt);
//		pagoAdelantadoTypePanel = new Panel();
//		pagoAdelantadoTypePanel.setLayout(new BorderLayout());
		txtPagoAdelantadoAvailable = new Textbox();
		txtPagoAdelantadoAvailable.setReadonly(true);
		lblPagoAdelantadoAvailable = new Label();
		lblPagoAdelantadoAvailable.setText("PENDIENTE");
		tenderTypeIndexsCombos.put(TAB_INDEX_PAGO_ADELANTADO,
				cboPagoAdelantadoReceiptMedium);
		cboPagoAdelantadoReceiptMedium.addEventListener("onChange", getPaymentMediumItemListener());
	
        // Por defecto pago
        cboPagoAdelantadoType.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX); 
        cboPagoAdelantadoType.addEventListener("onChange", new EventListener() {
        	public void onEvent(Event arg0) throws Exception {
				updatePagoAdelantadoTab();
			}
		});
        txtPagoAdelantadoAvailable = new Textbox();
        txtPagoAdelantadoAvailable.setReadonly(true); 
        lblPagoAdelantadoAvailable = new Label();
        lblPagoAdelantadoAvailable.setText("PENDIENTE");
		
        Grid gridpanel = GridFactory.newGridLayout();
		gridpanel.setWidth("100%");
		panelPagoAdelantado.setHeight("150px"); 
		
		Rows rows = gridpanel.newRows();
		Row row = rows.newRow();
		row.appendChild(lblPagoAdelantadoType.rightAlign());
		row.appendChild(cboPagoAdelantadoType);
		
		// Uno de los dos (efectivo o pago)
		Row row2a = rows.newRow();
		row2a.appendChild(lblPagoAdelantadoPago.rightAlign());
		row2a.appendChild(pagoAdelantado.getComponent());
		Row row2b = rows.newRow();
		row2b.appendChild(lblPagoAdelantadoCash.rightAlign());
		row2b.appendChild(cashAdelantado.getComponent());
		
		Row row3 = rows.newRow();
		row3.appendChild(lblPagoAdelantadoAvailable.rightAlign());
		row3.appendChild(txtPagoAdelantadoAvailable);
		Row row4 = rows.newRow();
		row4.appendChild(lblPagoAdelantadoImporte.rightAlign());
		row4.appendChild(txtPagoAdelantadoImporte);
		
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
		
//		panelTenderType.setOpaque(false);
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
				
				updatePaymentMediumCombo(tenderType.getName());
				
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
		cboTenderType.setSelectedIndex(0);  // setSelectedItem(MPOSPaymentMedium.TENDERTYPE_Cash);
		// Layout del panel
	}

	protected void createButtonsPanel() {
//		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
//				jPanel2);
//		jPanel2.setLayout(jPanel2Layout);
//		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
//				org.jdesktop.layout.GroupLayout.LEADING).add(
//				org.jdesktop.layout.GroupLayout.TRAILING,
//				jPanel2Layout
//						.createSequentialGroup()
//						.addContainerGap(424, Short.MAX_VALUE)
//						.add(cmdCancel)
//						.addPreferredGap(
//								org.jdesktop.layout.LayoutStyle.RELATED)
//						.add(cmdProcess).addContainerGap()));
//		jPanel2Layout
//				.setVerticalGroup(jPanel2Layout
//						.createParallelGroup(
//								org.jdesktop.layout.GroupLayout.LEADING)
//						.add(jPanel2Layout
//								.createSequentialGroup()
//								.add(jPanel2Layout
//										.createParallelGroup(
//												org.jdesktop.layout.GroupLayout.BASELINE)
//										.add(cmdProcess).add(cmdCancel))));
	}


	protected void addPanelsToFrame() {
//		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
//				m_frame.getContentPane());
//		m_frame.getContentPane().setLayout(layout);
//		layout.setHorizontalGroup(layout
//				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//				.add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//						Short.MAX_VALUE)
//				.add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//						Short.MAX_VALUE)
//				.add(jTabbedPane1,
//						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 672,
//						Short.MAX_VALUE));
//		layout.setVerticalGroup(layout
//				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
//				.add(org.jdesktop.layout.GroupLayout.TRAILING,
//						layout.createSequentialGroup()
//								.add(jPanel1,
//										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
//								.add(jTabbedPane1,
//										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
//								.addPreferredGap(
//										org.jdesktop.layout.LayoutStyle.RELATED)
//								.add(jPanel2,
//										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
	}

	/**
	 * Crea los componentes para descuento/recargo del documento
	 */
	protected void createDocumentDiscountComponents() {
		lblDiscountAmt = new Label(Msg.translate(m_ctx, "DiscountCharge"));
		txtDiscountAmt = new Textbox();
		txtDiscountAmt.setReadonly(true);
	}

	/**
	 * Crea el panel de descuentos por medio de pago.  Redefinido desde createPaymentTab()
	 */
	protected void createPaymentMediumDiscountPanel(Rows rows) {
		lblPaymentDiscount = new Label(Msg.translate(m_ctx, "DiscountCharge"));
		lblPaymentToPayDiscount = new Label(Msg.translate(m_ctx, "DiscountedChargedToPayAmt"));
		txtPaymentDiscount = new Textbox();
		txtPaymentDiscount.setReadonly(true);
		txtPaymentToPayDiscount = new Textbox();
		txtPaymentToPayDiscount.setReadonly(true);

		if (rows == null)	// invocacion al iniciar la ventana 
			return;
		Row row = rows.newRow();
		row.appendChild(lblPaymentDiscount.rightAlign());
		row.appendChild(txtPaymentDiscount);
		row.appendChild(lblPaymentToPayDiscount.rightAlign());
		row.appendChild(txtPaymentToPayDiscount);
	}

	
	/**
	 * Permite agregar campos en el tab summary
	 */
	protected void addSummaryCustomFields(Rows rows) {
		Row row = rows.newRow();
		row.appendChild(lblDiscountAmt.rightAlign());
		row.appendChild(txtDiscountAmt);
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
			// Obtengo la lista de índices de pestañas relacionadas con el tipo
			// de
			// pago parámetro
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
//		tabbedPane.repaint();
//		panelPaymentDiscount.repaint();
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
//		tabbedPane.repaint();
//		panelPaymentDiscount.repaint();
//		m_frame.repaint();
	}

	private VOrdenCobroModel getCobroModel() {
		return (VOrdenCobroModel) getModel();
	}

	@Override
	protected void clearMediosPago() {
		super.clearMediosPago();
		if (panelCreditCard != null) {
			txtCreditCardAmt.setValue(Integer.toString(0));
			txtCreditCardCouponNo.setText("");
			txtCreditCardNo.setText("");
			txtCuotaAmt.setValue(Integer.toString(0));
		}
		if (retencSchema == null)
			return;

		Date d = new Date();
		// Retenciones
		retencSchema.setValue(null);
		txtRetencImporte.setText("0");
		txtRetencNroRetenc.setText("");
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
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboRetencionReceiptMedium.getModel().getElementAt(cboRetencionReceiptMedium.getSelectedIndex());  
		saveBasicValidation(paymentMedium, null);
		Integer retencionSchemaID = (Integer) retencSchema.getValue();
		String retencionNumber = txtRetencNroRetenc.getText().trim();
		BigDecimal amount = null;
		try {
			amount = numberParse(txtRetencImporte.getText());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Amount@");
		}
		Timestamp retencionDate = new Timestamp(((Date)retencFecha.getValue()).getTime()); // retencFecha.getTimestamp();
		// Se agrega la retención como medio de cobro.
		getCobroModel().addRetencion(retencionSchemaID, retencionNumber,
				amount, retencionDate, getC_Campaign_ID(), getC_Project_ID());
	}

	protected void saveCreditCardMedioPago() throws Exception {
		// Obtengo la data de la interfaz gráfica
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCreditCardReceiptMedium.getModel().getElementAt(cboCreditCardReceiptMedium.getSelectedIndex()); 
		MEntidadFinancieraPlan plan = (MEntidadFinancieraPlan) cboEntidadFinancieraPlans.getModel().getElementAt(cboEntidadFinancieraPlans.getSelectedIndex()); 
		saveBasicValidation(paymentMedium, plan);
		getCobroModel().addCreditCard(
				paymentMedium,
				plan,
				txtCreditCardNo.getText(),
				txtCreditCardCouponNo.getText(),
				new BigDecimal(txtCreditCardAmt.getValue()),
				(String) cboCreditCardBank.getValue(),
				Util.isEmpty(txtCuotasCount.getText()) ? 0 : Integer
						.parseInt(txtCuotasCount.getText()),
				new BigDecimal(txtCuotaAmt.getValue()), getC_Campaign_ID(),
				getC_Project_ID(), (Integer) cboCurrency.getValue());
	}

	@Override
	protected MedioPagoCheque saveCheckMedioPago() throws Exception {
		// Obtengo la data de la interfaz gráfica
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCheckReceiptMedium.getModel().getElementAt(cboCheckReceiptMedium.getSelectedIndex());
		saveBasicValidation(paymentMedium, null);
		
		if (paymentMedium.isMandatoryBank() && Util.isEmpty(getBankName(), true)) {
			throw new Exception(getMsg("Bank"));
		}
		
		BigDecimal amount = null;
		Integer monedaOriginalID;
		try {
			amount = numberParse(txtChequeImporte.getText());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Amount@");
		}
		try {
			monedaOriginalID = (Integer) cboCurrency.getValue();
		} catch (Exception e) {
			throw new Exception(cboCurrency.getValue().toString());
		}
		getCobroModel().addCheck(paymentMedium,
				(Integer) chequeChequera.getValue(),
				txtChequeNroCheque.getText(), amount,
				new Timestamp(((Date)chequeFechaEmision.getValue()).getTime()), // chequeFechaEmision.getTimestamp(),
				new Timestamp(((Date)chequeFechaPago.getValue()).getTime()), // chequeFechaPago.getTimestamp(), 
				txtChequeALaOrden.getText(),
				getBankName(), txtChequeCUITLibrador.getText(),
				txtChequeDescripcion.getText(), getC_Campaign_ID(),
				getC_Project_ID(), monedaOriginalID);
		return null;
	}

	@Override
	protected MedioPagoEfectivo saveCashMedioPago() throws Exception {
		MedioPagoEfectivo mpe = super.saveCashMedioPago();
		// Validación para que el monto ingresado no supere el a pagar
		// toPayValidation();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCashReceiptMedium.getModel().getElementAt(cboCashReceiptMedium.getSelectedIndex()); 
		saveBasicValidation(paymentMedium, null);
		mpe.setPaymentMedium(paymentMedium);
		mpe.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpe.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpe;
	}

	@Override
	protected MedioPagoTransferencia saveTransferMedioPago() throws Exception {
		MedioPagoTransferencia mpt = super.saveTransferMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboTransferReceiptMedium.getModel().getElementAt(cboTransferReceiptMedium.getSelectedIndex());
		saveBasicValidation(paymentMedium, null);
		mpt.setPaymentMedium(paymentMedium);
		mpt.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpt.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpt;
	}

	@Override
	protected MedioPagoCredito saveCreditMedioPago() throws Exception {
		MedioPagoCredito mpc = super.saveCreditMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCreditReceiptMedium.getModel().getElementAt(cboCreditReceiptMedium.getSelectedIndex());
		saveBasicValidation(paymentMedium, null);
		mpc.setPaymentMedium(paymentMedium);
		mpc.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpc.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpc;
	}

	@Override
	protected MedioPago savePagoAdelantadoMedioPago() throws Exception {
		MedioPago mp = super.savePagoAdelantadoMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboPagoAdelantadoReceiptMedium.getModel().getElementAt(cboPagoAdelantadoReceiptMedium.getSelectedIndex());
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
		txtRetencNroRetenc.setText(retencion.getRetencionNumber());
		txtRetencImporte
				.setText(getModel().numberFormat(retencion.getAmount()));
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
			txtCreditCardCouponNo.setText(tarjeta.getCouponNo());
			txtCreditCardNo.setText(tarjeta.getCreditCardNo());
			txtCreditCardAmt.setValue(tarjeta.getImporte().toString());
			txtCuotasCount.setText(""
					+ tarjeta.getEntidadFinancieraPlan().getCuotasPago());
			txtCuotaAmt.setValue(tarjeta.getCuotaAmt().toString());
			cboCreditCardBank.setValue(tarjeta.getBank());
		} else {
			super.loadMedioPago(mp);
			Combobox comboReceiptMedium = tenderTypeIndexsCombos.get(mpTabbox.getSelectedIndex()); // .get(jTabbedPane2.getSelectedIndex());
			comboReceiptMedium.setSelectedItemValue(mp.getPaymentMedium());
		}
		// Selección automática de pestañas a partir del tender type
		int indexSelected = mpTabbox.getSelectedIndex(); // jTabbedPane2.getSelectedIndex();
		cboTenderType.setSelectedIndex(indexSelected); // .setSelectedItem(tenderTypesComboValues.get(tabsTenderTypeRelations.get(indexSelected)));
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
//		combo.setMandatory(true);
//		combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 20));
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

		MPOSPaymentMedium mp = (MPOSPaymentMedium) cboCreditCardReceiptMedium.getModel().getElementAt(cboCreditCardReceiptMedium.getSelectedIndex());
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
			txtCuotasCount.setText("" + plan.getCuotasPago());
			txtCuotasCount.setReadonly(true);
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
				txtCuotaAmt.setValue(paymentToPayAmt.toString());
			}
			// El importe de la cuota se calcula en base al importe del pago
			// ingresado por el usuario
			if (plan != null) {
				BigDecimal amt = txtCreditCardAmt.getValue() == null ? BigDecimal.ZERO
						: new BigDecimal(txtCreditCardAmt.getValue());
				BigDecimal cuotaAmt = amt.divide(
						new BigDecimal(plan.getCuotasPago()), 10,
						BigDecimal.ROUND_HALF_UP);
				txtCuotaAmt.setValue(cuotaAmt.toString());
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
		return (MEntidadFinancieraPlan) cboEntidadFinancieraPlans.getModel().getElementAt(cboEntidadFinancieraPlans.getSelectedIndex());
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
				txtChequeBanco.setText(getCobroModel().getBankName(
						paymentMedium.getBank()));
				txtChequeBanco.setReadonly(true);
			} else {
				txtChequeBanco.setReadonly(false);
				txtChequeBanco.setText("");
			}
		}
		else{
			if (paymentMedium.getC_Bank_ID() != 0) {
				cboChequeBancoID.setValue(paymentMedium.getC_Bank_ID());
				cboChequeBancoID.setReadWrite(false);
			} else {
				txtChequeBanco.setReadonly(false);
				txtChequeBanco.setText("");
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
			txtPOS.setValue(Integer.toString(pos));
			txtPOS.setReadonly(existsPOS);  // txtPOS.setReadWrite(!existsPOS);
			// Mensaje dialog en caso que sea posible obtener automáticamente un
			// nro de pto de venta pero no existe ninguno
			// Comentado para que no muestre el cartel al iniciar la ventana
			// if (!existsPOS && getCobroModel().mustGettingPOSNumber()) {
			// showInfo(VOrdenCobroModel.POS_ERROR_MSG);
			// }
		} else {
			lblPOS.setVisible(false);
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
		txtTotalPagar1.setText(numberFormat(total));
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
		txtPaymentDiscount.setText(isApplicable ? discountSchema.toString()
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
			lblGroupingAmt.setVisible(true);
			txtGroupingAmt.setVisible(true);
			((OpenInvoicesCustomerReceiptsTableModel) getModel().m_facturasTableModel)
					.setAllowManualAmtEditable(false);
			rInvoiceDate.setVisible(false);
			rInvoiceAll.setVisible(false);
			invoiceDatePick.setVisible(false);
		} else {
			lblGroupingAmt.setVisible(false);
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
			BigDecimal amt = txtGroupingAmt.getValue() != null ? new BigDecimal(txtGroupingAmt.getValue()) : BigDecimal.ZERO;
			getCobroModel().updateGroupingAmtInvoices(amt);
//			repaint();
//			tblFacturas.repaint();
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
		txtDiscountAmt.setText(numberFormat(getCobroModel().getSumaDescuentos()
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
			lblOrgCharge.setVisible(true);
			txtOrgCharge.setVisible(true);
		} else {
			lblOrgCharge.setVisible(false);
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
		txtGroupingAmt.setReadonly(isSelected);
//		tblFacturas.repaint();
		resetModel();
	}

    @Override
    protected void updateCustomPayAmt(BigDecimal amt){
    	Integer tabIndexSelected = mpTabbox.getSelectedIndex();		// jTabbedPane2.getSelectedIndex();
    	if(tabIndexSelected.equals(m_retencTabIndex)){
    		txtRetencImporte.setValue(amt.toString());
    	}
    	else if(tabIndexSelected.equals(m_creditCardTabIndex)){
    		txtCreditCardAmt.setValue(amt.toString());
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
			if (arg0.getData() == null)
				return;
			MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium)arg0.getData();
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
			return txtChequeBanco.getText();
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
		lblBPartnerDiscount = new Label();
		MLookup lookupBPDiscount = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 6581, DisplayType.Search);
		bPartnerDiscount = new WSearchEditor ("M_DiscountSchema_ID", false, false, true, lookupBPDiscount);
		bPartnerDiscount.setReadWrite(false);

    	Rows rows = jPanel1.newRows();
    	Row row = rows.newRow();
    	row.appendChild(lblClient.rightAlign());
    	row.appendChild(cboClient.getComponent());
    	
    	row.appendChild(new Space());
    	row.appendChild(lblOrg.rightAlign());
    	row.appendChild(cboOrg.getComponent());
    	row.appendChild(new Space());
    	
    	row = rows.newRow();
    	row.appendChild(lblBPartner.rightAlign());
    	row.appendChild(BPartnerSel.getComponent());
    	row.appendChild(new Space());
    	row.appendChild(lblBPartnerDiscount.rightAlign());
    	row.appendChild(bPartnerDiscount.getComponent());
    	row.appendChild(new Space());
    	
    	row = rows.newRow();
    	row.appendChild(lblDocumentNo.rightAlign());
    	row.appendChild(fldDocumentNo);
    	txtDescription.setWidth("100%");
    	row.appendChild(new Space());

    	row.appendChild(lblDocumentType.rightAlign());
    	row.appendChild(cboDocumentType.getComponent());
    	row.appendChild(new Space());

    }
    
    
	/**
	 * Agrega campo de PayAll (u otros en subclase)
	 */
	protected void createPayAllDiv(Div divCheckPayAll) {
		divCheckPayAll.setAlign("end");
		divCheckPayAll.appendChild(lblPOS);
		divCheckPayAll.appendChild(txtPOS);
		divCheckPayAll.appendChild(checkPayAll);
	}
    
	/**
	 * Seteo de campos en la part inferior derecha
	 */
	protected void setDivTxtTotal(Div divTxtEfectivoImporte) {
		
		divTxtEfectivoImporte.setAlign("end");
		divTxtEfectivoImporte.appendChild(lblOrgCharge);
		divTxtEfectivoImporte.appendChild(txtOrgCharge);
		divTxtEfectivoImporte.appendChild(lblTotalPagar1);
		divTxtEfectivoImporte.appendChild(txtTotalPagar1);		
	}

	
	protected void initComponents() {
		
		// Inicializaciones especificas para WOrdenCobro
		txtOrgCharge = new Textbox();
		txtOrgCharge.setValue(BigDecimal.ZERO.toString());
		txtOrgCharge.setReadonly(true);
		txtPOS = new Textbox();
		// Setear el valor del punto de venta
		txtPOS.addEventListener("onChange", new EventListener() {			
			@Override
			public void onEvent(Event arg0) throws Exception {
				getCobroModel().setPOS(Integer.parseInt(txtPOS.getValue()));
				
			}
		});

		// Monto de agrupación de la entidad comercial
		txtGroupingAmt = new Textbox();
		txtGroupingAmt.setValue(BigDecimal.ZERO.toString());
		txtGroupingAmt.addEventListener("onChange", new EventListener() {		
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
}
