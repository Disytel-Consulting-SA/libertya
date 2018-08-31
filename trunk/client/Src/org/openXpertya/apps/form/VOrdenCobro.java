package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.compiere.swing.CComboBox;
import org.openXpertya.apps.form.VOrdenCobroModel.OpenInvoicesCustomerReceiptsTableModel;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPago;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCheque;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoCredito;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoEfectivo;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoTarjetaCredito;
import org.openXpertya.apps.form.VOrdenPagoModel.MedioPagoTransferencia;
import org.openXpertya.grid.ed.VCellRenderer;
import org.openXpertya.grid.ed.VComboBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.pos.view.KeyUtils;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

public class VOrdenCobro extends VOrdenPago {

	protected static final String GOTO_TENDER_TYPE = "GOTO_TENDER_TYPE";

	private static Map<String, String> msgChanges;
	static {
		msgChanges = new HashMap<String, String>();
		msgChanges.put("EmitPayment", "EmitReceipt");
		msgChanges.put("AdvancedPayment", "AdvancedCustomerPayment");
		msgChanges.put("Payment", "CustomerPayment");
	}

	private javax.swing.JLabel lblRetencSchema;
	private javax.swing.JLabel lblRetencFecha;
	private javax.swing.JLabel lblRetencImporte;
	private javax.swing.JLabel lblRetencNroRetenc;
	protected javax.swing.JLabel lblTenderType;
	protected javax.swing.JLabel lblDiscountAmt;
	protected javax.swing.JLabel lblPaymentDiscount;
	protected javax.swing.JLabel lblPaymentToPayDiscount;
	protected javax.swing.JLabel lblCreditCardBank;
	protected javax.swing.JLabel lblCreditCardNo;
	protected javax.swing.JLabel lblCreditCardCouponNo;
	protected javax.swing.JLabel lblCuotasCount;
	protected javax.swing.JLabel lblCuotaAmt;
	protected javax.swing.JLabel lblCreditCardPlan;
	protected javax.swing.JLabel lblCreditCardAmt;
	protected javax.swing.JLabel lblBPartnerDiscount;
	protected javax.swing.JLabel lblPOS = new javax.swing.JLabel();
	protected javax.swing.JLabel lblOrgCharge = new javax.swing.JLabel();
	protected javax.swing.JLabel lblGroupingAmt = new javax.swing.JLabel();

	protected javax.swing.JLabel lblCreditCardReceiptMedium = new javax.swing.JLabel();
	protected javax.swing.JLabel lblCheckReceiptMedium = new javax.swing.JLabel();
	protected javax.swing.JLabel lblCreditReceiptMedium = new javax.swing.JLabel();
	protected javax.swing.JLabel lblCashReceiptMedium = new javax.swing.JLabel();
	protected javax.swing.JLabel lblTransferReceiptMedium = new javax.swing.JLabel();
	protected javax.swing.JLabel lblRetencionReceiptMedium = new javax.swing.JLabel();
	protected javax.swing.JLabel lblPagoAdelantadoReceiptMedium = new javax.swing.JLabel();
	private VDate retencFecha;
	private VLookup retencSchema;
	protected VLookup bPartnerDiscount;
	protected VNumber txtPOS;
	protected VNumber txtOrgCharge;
	protected VNumber txtGroupingAmt;
	protected CComboBox cboTenderType;
	private JFormattedTextField txtRetencImporte;
	private javax.swing.JTextField txtRetencNroRetenc;
	protected JFormattedTextField txtDiscountAmt;
	protected javax.swing.JTextField txtPaymentDiscount;
	protected JFormattedTextField txtPaymentToPayDiscount;
	protected VLookup cboCreditCardBank;
	protected javax.swing.JTextField txtCreditCardNo;
	protected javax.swing.JTextField txtCreditCardCouponNo;
	protected javax.swing.JTextField txtCuotasCount;
	protected JFormattedTextField txtCuotaAmt;
	protected VNumber txtCreditCardAmt;
	private javax.swing.JPanel panelRetenc;
	protected javax.swing.JPanel panelTenderType;
	protected javax.swing.JPanel panelPaymentDiscount;
	protected javax.swing.JPanel panelSummary;
	protected javax.swing.JPanel panelCreditCard;

	private int m_retencTabIndex = -1;
	protected int m_creditCardTabIndex = -1;

	protected Map<String, List<Integer>> tenderTypesTabRelations;
	protected Map<Integer, String> tabsTenderTypeRelations;

	protected Map<String, ValueNamePair> tenderTypesComboValues;
	protected Map<Integer, CComboBox> tenderTypeIndexsCombos = new HashMap<Integer, CComboBox>();

	protected CComboBox cboCreditCardReceiptMedium;
	protected CComboBox cboCheckReceiptMedium;
	protected CComboBox cboCreditReceiptMedium;
	protected CComboBox cboCashReceiptMedium;
	protected CComboBox cboTransferReceiptMedium;
	protected CComboBox cboRetencionReceiptMedium;
	protected CComboBox cboPagoAdelantadoReceiptMedium;

	protected CComboBox cboEntidadFinancieraPlans;

	private PaymentMediumItemListener paymentMediumItemListener;

	public VOrdenCobro() {
		super();
		setModel(new VOrdenCobroModel());
		setPaymentMediumItemListener(new PaymentMediumItemListener());
	}

	@Override
	protected VLookup createChequeChequeraLookup() {
		return VComponentsFactory.VLookupFactory("C_BankAccount_ID",
				"C_BankAccount", m_WindowNo, DisplayType.TableDir, getModel()
						.getChequeChequeraSqlValidation());
	}

	@Override
	protected void chequeraChange(PropertyChangeEvent e) {
		// No se debe obtener el número de cheque automáticamente, se ingresa
		// manualmente.
	}

	@Override
	protected void initTranslations() {
		super.initTranslations();
		// Cheques
		lblChequeChequera.setText(Msg.translate(m_ctx, "C_BankAccount_ID"));

		// Retencions
		lblRetencSchema.setText(Msg.translate(m_ctx, "C_Withholding_ID"));
		lblRetencNroRetenc.setText(Msg.translate(m_ctx, "RetencionNumber"));
		lblRetencImporte.setText(Msg.getElement(m_ctx, "Amount"));
		lblRetencFecha.setText(Msg.translate(m_ctx, "Date"));

		//
		radPayTypeStd.setText(Msg.translate(m_ctx, "StandardCustomerPayment"));
		radPayTypeAdv.setText(Msg.translate(m_ctx, "AdvancedCustomerPayment"));
		lblMedioPago2.setText(Msg.translate(m_ctx, "CustomerTenderType"));

		checkPayAll.setText(Msg.getMsg(m_ctx, "ReceiptAll") + " "
				+ KeyUtils.getKeyStr(getActionKeys().get(GOTO_PAYALL)));

		//
		jTabbedPane1.setTitleAt(0,
				Msg.translate(m_ctx, "CustomerPaymentSelection"));
		jTabbedPane1.setTitleAt(1, Msg.translate(m_ctx, "CustomerPaymentRule"));

		lblBPartnerDiscount
				.setText(Msg.translate(m_ctx, "M_DiscountSchema_ID"));
		lblPOS.setText(Msg.translate(m_ctx, "RealPOS"));
		lblOrgCharge.setText(Msg.translate(m_ctx, "OrgCharge"));
		lblGroupingAmt.setText(Msg.translate(m_ctx, "GroupingAmt"));

		String msgReceiptMedium = Msg.translate(m_ctx, "ReceiptMedium");
		lblCreditCardReceiptMedium.setText(msgReceiptMedium);
		lblCheckReceiptMedium.setText(msgReceiptMedium);
		lblCreditReceiptMedium.setText(msgReceiptMedium);
		lblCashReceiptMedium.setText(msgReceiptMedium);
		lblTransferReceiptMedium.setText(msgReceiptMedium);
		lblRetencionReceiptMedium.setText(msgReceiptMedium);
		lblPagoAdelantadoReceiptMedium.setText(msgReceiptMedium);
		lblTenderType.setText(lblTenderType.getText() + " "
				+ KeyUtils.getKeyStr(getActionKeys().get(GOTO_TENDER_TYPE)));

	}

	@Override
	protected void customInitComponents() {
		super.customInitComponents();

		//
		tblFacturas
				.getColumnModel()
				.getColumn(tblFacturas.getColumnModel().getColumnCount() - 4)
				.setCellRenderer(
						new MyNumberTableCellRenderer(getModel()
								.getNumberFormat()));

		tblFacturas.getColumnModel()
				.getColumn(tblFacturas.getColumnModel().getColumnCount() - 3)
				.setCellRenderer(new VCellRenderer(DisplayType.YesNo));

		cboCurrency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					updateTenderTypeCombo();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		ocultarCanje();
		initFormattedTextField((JFormattedTextField) txtRetencImporte);
	}

	protected void ocultarCanje() {
		if (!getCobroModel().showColumnChange()) {
			tblFacturas.getColumnModel().removeColumn(
					tblFacturas.getColumnModel().getColumn(
							tblFacturas.getColumnModel().getColumnCount() - 3));
		}
	}

	@Override
	protected void addCustomPaymentTabs(JTabbedPane tabbedPane) {
		// Agregado de pestaña de medio de cobro en retenciones.
		tabbedPane.addTab(Msg.translate(m_ctx, "C_Withholding_ID"),
				createRetencionTab());
		m_retencTabIndex = tabbedPane.indexOfComponent(panelRetenc);
		tenderTypeIndexsCombos.put(m_retencTabIndex, cboRetencionReceiptMedium);
		// Incorporación de pestaña de medio de cobro tarjeta de crédito
		tabbedPane.addTab(Msg.translate(m_ctx, "CreditCard"),
				createCreditCardTab());
		m_creditCardTabIndex = tabbedPane.indexOfComponent(panelCreditCard);
		tenderTypeIndexsCombos.put(m_creditCardTabIndex,
				cboCreditCardReceiptMedium);
	}

	@Override
	protected void addCustomOperationAfterTabsDefinition() {
		// Inicializar las relaciones de los tender types con las pestañas
		initializeTenderTypeTabsRelations();
		// Habilito/Deshabilito las pestañas que no esten relacionadas con el
		// tender type actual
		processPaymentTabs(
				jTabbedPane2,
				cboTenderType.getSelectedItem() == null ? null
						: ((ValueNamePair) cboTenderType.getSelectedItem())
								.getValue());
		// Selecciono la primer pestaña de pagos que se encuentre habilitada
		selectPaymentTab(jTabbedPane2);
		// Selecciono uno por default
		if (cboTenderType.getItemCount() > 0) {
			cboTenderType.setSelectedIndex(0);
		}
	}

	@Override
	protected JComponent createInvoicesPanel() {
		txtOrgCharge = new VNumber();
		txtOrgCharge.setValue(BigDecimal.ZERO);
		txtOrgCharge.setReadWrite(false);
		txtPOS = new VNumber();
		txtPOS.setDisplayType(DisplayType.Integer);
		// Setear el valor del punto de venta
		txtPOS.addVetoableChangeListener(new VetoableChangeListener() {
			@Override
			public void vetoableChange(PropertyChangeEvent arg0)
					throws PropertyVetoException {
				getCobroModel().setPOS((Integer) txtPOS.getValue());
			}
		});
		// Monto de agrupación de la entidad comercial
		txtGroupingAmt = new VNumber();
		txtGroupingAmt.setDisplayType(DisplayType.Amount);
		txtGroupingAmt.setValue(BigDecimal.ZERO);
		txtGroupingAmt.addVetoableChangeListener(new VetoableChangeListener() {

			@Override
			public void vetoableChange(PropertyChangeEvent arg0)
					throws PropertyVetoException {
				updateGroupingAmt(false);
			}
		});
		// Crear el panel
		org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel3Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel3Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jScrollPane1,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												647, Short.MAX_VALUE)
										.add(jPanel3Layout
												.createSequentialGroup()
												.add(radPayTypeStd)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(radPayTypeAdv)
												.addContainerGap(20,
														Short.MAX_VALUE)
												.add(lblGroupingAmt)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(txtGroupingAmt,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														110,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(lblPOS)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(txtPOS,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														100,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addContainerGap(20,
														Short.MAX_VALUE)
												.add(checkPayAll,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														135,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(org.jdesktop.layout.GroupLayout.TRAILING,
												jPanel3Layout
														.createSequentialGroup()
														.add(rInvoiceAll)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(rInvoiceDate)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(invoiceDatePick,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																110,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED,
																179,
																Short.MAX_VALUE)
														.add(lblOrgCharge)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(txtOrgCharge,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																95,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(lblTotalPagar1)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(txtTotalPagar1,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																95,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)))
								.addContainerGap()));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel3Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel3Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(radPayTypeStd)
										.add(radPayTypeAdv)
										.add(lblGroupingAmt)
										.add(txtGroupingAmt,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(lblPOS)
										.add(txtPOS,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(checkPayAll))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jScrollPane1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										260, 440)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel3Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblTotalPagar1)
										.add(rInvoiceAll)
										.add(rInvoiceDate)
										.add(lblOrgCharge,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(txtOrgCharge,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(txtTotalPagar1,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(invoiceDatePick,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))));
		return jPanel3;
	}

	@Override
	protected void createLeftUpStaticPanel() {
		org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(
				jPanel9);
		jPanel9.setLayout(jPanel9Layout);
		jPanel9Layout
				.setHorizontalGroup(jPanel9Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel9Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(lblBPartner).add(lblClient)
										.add(lblDocumentNo)
										.add(lblDateTrx))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(cboClient, 0, 234, Short.MAX_VALUE)
										.add(BPartnerSel,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												234, Short.MAX_VALUE)
										.add(fldDocumentNo,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												234, Short.MAX_VALUE)
										.add(dateTrx,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												234, Short.MAX_VALUE)
										)
								.addContainerGap()));
		jPanel9Layout
				.setVerticalGroup(jPanel9Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel9Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblClient)
										.add(cboClient,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblBPartner)
										.add(BPartnerSel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblDocumentNo)
										.add(fldDocumentNo,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblDateTrx)
										.add(dateTrx,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
	}

	@Override
	protected void createRightUpStaticPanel() {
		// Descuento de entidad comercial
		lblBPartnerDiscount = new javax.swing.JLabel();
		bPartnerDiscount = VComponentsFactory.VLookupFactory(
				"M_DiscountSchema_ID", "M_DiscountSchema", m_WindowNo,
				DisplayType.Search);
		bPartnerDiscount.setReadWrite(false);

		jPanel10.setOpaque(false);
		org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(
				jPanel10);
		jPanel10.setLayout(jPanel10Layout);
		jPanel10Layout
				.setHorizontalGroup(jPanel10Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel10Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel10Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(lblOrg).add(lblBPartnerDiscount)
										.add(lblDocumentType).add(lblPaymentRule))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel10Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(cboOrg, 0, 234, Short.MAX_VALUE)
										.add(bPartnerDiscount,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												234, Short.MAX_VALUE)
										.add(cboDocumentType, 0, 234,
												Short.MAX_VALUE)
										.add(cboPaymentRule, 0, 234,
												Short.MAX_VALUE))
								.addContainerGap()));
		jPanel10Layout
				.setVerticalGroup(jPanel10Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel10Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel10Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblOrg)
										.add(cboOrg,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel10Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblBPartnerDiscount)
										.add(bPartnerDiscount,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel10Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblDocumentType)
										.add(cboDocumentType,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel10Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPaymentRule)
										.add(cboPaymentRule,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))));
	}

	@Override
	protected JComponent createCashTab() {
		jPanel5.setOpaque(false);
		lblEfectivoLibroCaja.setText("LIBRO DE CAJA");
		lblEfectivoImporte.setText("IMPORTE");
		txtEfectivoImporte.setText("0");
		cboCashReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_Cash);
		cboCashReceiptMedium.addItemListener(getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_EFECTIVO, cboCashReceiptMedium);

		org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout
				.setHorizontalGroup(jPanel5Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel5Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel5Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblCashReceiptMedium)
										.add(lblEfectivoImporte)
										.add(lblEfectivoLibroCaja))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel5Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(efectivoLibroCaja,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												193, Short.MAX_VALUE)
										.add(txtEfectivoImporte,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												193, Short.MAX_VALUE)
										.add(cboCashReceiptMedium,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												193, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel5Layout
				.setVerticalGroup(jPanel5Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel5Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel5Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCashReceiptMedium)
										.add(cboCashReceiptMedium,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel5Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblEfectivoLibroCaja)
										.add(efectivoLibroCaja,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel5Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblEfectivoImporte)
										.add(txtEfectivoImporte,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(118, Short.MAX_VALUE)));
		return jPanel5;
	}

	@Override
	protected JComponent createTransferTab() {
		jPanel6.setOpaque(false);
		lblTransfCtaBancaria.setText("CUENTA BANCARIA");
		lblTransfNroTransf.setText("NRO TRANSFERENCIA");
		lblTransfImporte.setText("IMPORTE");
		lblTransfFecha.setText("FECHA");
		txtTransfImporte.setText("0");
		cboTransferReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_DirectDeposit);
		cboTransferReceiptMedium
				.addItemListener(getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_TRANSFERENCIA,
				cboTransferReceiptMedium);

		org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(
				jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout
				.setHorizontalGroup(jPanel6Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel6Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblTransferReceiptMedium)
										.add(lblTransfFecha)
										.add(lblTransfImporte)
										.add(lblTransfNroTransf)
										.add(lblTransfCtaBancaria))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtTransfImporte,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtTransfNroTransf,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(transfCtaBancaria,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(transFecha,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(cboTransferReceiptMedium,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel6Layout
				.setVerticalGroup(jPanel6Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel6Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblTransferReceiptMedium)
										.add(cboTransferReceiptMedium,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblTransfCtaBancaria)
										.add(transfCtaBancaria,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblTransfNroTransf)
										.add(txtTransfNroTransf,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblTransfImporte)
										.add(txtTransfImporte,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblTransfFecha)
										.add(transFecha,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(66, Short.MAX_VALUE)));
		return jPanel6;
	}

	@Override
	protected JComponent createCheckTab() {
		jPanel7.setOpaque(false);
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
		cboCheckReceiptMedium.addItemListener(getPaymentMediumItemListener());
		chequeFechaEmision
				.addVetoableChangeListener(new VetoableChangeListener() {

					@Override
					public void vetoableChange(PropertyChangeEvent arg0)
							throws PropertyVetoException {
						if (cboCheckReceiptMedium.getSelectedItem() != null) {
							updateFechaPagoCheque((MPOSPaymentMedium) cboCheckReceiptMedium
									.getSelectedItem());
						}
					}
				});
		tenderTypeIndexsCombos.put(TAB_INDEX_CHEQUE, cboCheckReceiptMedium);
		updateBank((MPOSPaymentMedium) cboCheckReceiptMedium.getValue());
		org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(
				jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout
				.setHorizontalGroup(jPanel7Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel7Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblCheckReceiptMedium)
										.add(lblChequeCUITLibrador)
										.add(lblChequeBanco)
										.add(lblChequeALaOrden)
										.add(lblChequeChequera)
										.add(lblChequeNroCheque)
										.add(lblChequeImporte)
										.add(lblChequeFechaEmision)
										.add(lblChequeFechaPago)
										.add(lblChequeDescripcion))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtChequeCUITLibrador,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(txtChequeBanco,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(cboChequeBancoID,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(txtChequeNroCheque,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(txtChequeALaOrden,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(txtChequeImporte,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(chequeChequera,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(chequeFechaEmision,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(chequeFechaPago,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(txtChequeDescripcion,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(cboCheckReceiptMedium,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel7Layout
				.setVerticalGroup(jPanel7Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel7Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCheckReceiptMedium)
										.add(cboCheckReceiptMedium,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeChequera)
										.add(chequeChequera,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeNroCheque)
										.add(txtChequeNroCheque,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeImporte)
										.add(txtChequeImporte,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeFechaEmision)
										.add(chequeFechaEmision,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeFechaPago)
										.add(chequeFechaPago,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeALaOrden)
										.add(txtChequeALaOrden,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeBanco)
										.add(txtChequeBanco,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(cboChequeBancoID,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeCUITLibrador)
										.add(txtChequeCUITLibrador,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblChequeDescripcion)
										.add(txtChequeDescripcion,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								// .addContainerGap(25, Short.MAX_VALUE)
								.addContainerGap()));
		return jPanel7;
	}

	@Override
	protected JComponent createCreditTab() {
		jPanel11.setOpaque(false);
		lblCreditInvoice.setText("CREDITO");
		lblCreditAvailable.setText("DISPONIBLE");
		lblCreditImporte.setText("IMPORTE");
		txtCreditAvailable.setText("0");
		txtCreditImporte.setText("0");
		cboCreditReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_CreditNote);
		cboCreditReceiptMedium.addItemListener(getPaymentMediumItemListener());
		tenderTypeIndexsCombos.put(TAB_INDEX_CREDITO, cboCreditReceiptMedium);

		org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(
				jPanel11);
		jPanel11.setLayout(jPanel11Layout);
		jPanel11Layout
				.setHorizontalGroup(jPanel11Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel11Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel11Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblCreditReceiptMedium)
										.add(lblCreditImporte)
										.add(lblCreditAvailable)
										.add(lblCreditInvoice))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel11Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtCreditImporte,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtCreditAvailable,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(creditInvoice,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(cboCreditReceiptMedium,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel11Layout
				.setVerticalGroup(jPanel11Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel11Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel11Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditReceiptMedium)
										.add(cboCreditReceiptMedium,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel11Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditInvoice)
										.add(creditInvoice,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel11Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditAvailable)
										.add(txtCreditAvailable,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel11Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditImporte)
										.add(txtCreditImporte,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(66, Short.MAX_VALUE)));
		return jPanel11;
	}

	private JComponent createRetencionTab() {
		panelRetenc = new javax.swing.JPanel();
		panelRetenc.setOpaque(false);

		lblRetencSchema = new JLabel();
		lblRetencSchema.setText("RETENCION");
		lblRetencNroRetenc = new JLabel();
		lblRetencNroRetenc.setText("NRO RETENCION");
		lblRetencImporte = new JLabel();
		lblRetencImporte.setText("IMPORTE");
		lblRetencFecha = new JLabel();
		lblRetencFecha.setText("FECHA");
		txtRetencImporte = new JFormattedTextField();
		txtRetencImporte.setText("0");
		initFormattedTextField((JFormattedTextField) txtRetencImporte);
		txtRetencNroRetenc = new JTextField();
		retencSchema = VComponentsFactory
				.VLookupFactory("C_RetencionSchema_ID", "C_RetencionSchema",
						m_WindowNo, DisplayType.Search, getCobroModel()
								.getRetencionSqlValidation());
		retencFecha = VComponentsFactory.VDateFactory();
		cboRetencionReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_Retencion);
		cboRetencionReceiptMedium
				.addItemListener(getPaymentMediumItemListener());

		org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(
				panelRetenc);
		panelRetenc.setLayout(jPanel6Layout);
		jPanel6Layout
				.setHorizontalGroup(jPanel6Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel6Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblRetencionReceiptMedium)
										.add(lblRetencFecha)
										.add(lblRetencImporte)
										.add(lblRetencNroRetenc)
										.add(lblRetencSchema))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtRetencImporte,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtRetencNroRetenc,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(retencSchema,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(retencFecha,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(cboRetencionReceiptMedium,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel6Layout
				.setVerticalGroup(jPanel6Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel6Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblRetencionReceiptMedium)
										.add(cboRetencionReceiptMedium,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblRetencSchema)
										.add(retencSchema,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblRetencNroRetenc)
										.add(txtRetencNroRetenc,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblRetencImporte)
										.add(txtRetencImporte,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel6Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblRetencFecha)
										.add(retencFecha,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(66, Short.MAX_VALUE)));

		return panelRetenc;
	}

	protected JComponent createCreditCardTab() {
		panelCreditCard = new javax.swing.JPanel();
		lblCreditCardPlan = new javax.swing.JLabel(getMsg("CreditCardPlan"));
		lblCreditCardBank = new javax.swing.JLabel(getMsg("C_Bank_ID"));
		lblCreditCardNo = new javax.swing.JLabel(getMsg("CreditCardNumber"));
		lblCreditCardCouponNo = new javax.swing.JLabel(getMsg("CouponNumber"));
		lblCuotasCount = new javax.swing.JLabel(getMsg("CuotasCount"));
		lblCuotaAmt = new javax.swing.JLabel(getMsg("CuotaAmt"));
		lblCreditCardAmt = new javax.swing.JLabel(getMsg("Amt"));
		lblCreditCardReceiptMedium = new javax.swing.JLabel();
		cboCreditCardBank = VComponentsFactory.VLookupFactory("Bank",
				"C_POSPaymentMedium", m_WindowNo, DisplayType.List);
		txtCreditCardNo = new javax.swing.JTextField();
		txtCreditCardCouponNo = new javax.swing.JTextField();
		txtCuotasCount = new javax.swing.JTextField();
		txtCuotaAmt = new JFormattedTextField();
		initFormattedTextField(txtCuotaAmt);
		txtCuotaAmt.setEditable(false);
		txtCreditCardAmt = new VNumber();
		txtCreditCardAmt
				.addVetoableChangeListener(new VetoableChangeListener() {

					@Override
					public void vetoableChange(PropertyChangeEvent arg0)
							throws PropertyVetoException {
						if (arg0.getNewValue() != null) {
							refreshPaymentMediumAmountInfo((MPOSPaymentMedium) cboCreditCardReceiptMedium
									.getSelectedItem());
						}
					}
				});
		txtCreditCardAmt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() != null) {
					refreshPaymentMediumAmountInfo((MPOSPaymentMedium) cboCreditCardReceiptMedium
							.getSelectedItem());
				}
			}
		});
		cboCreditCardReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_CreditCard);
		cboCreditCardReceiptMedium
				.addItemListener(getPaymentMediumItemListener());
		cboEntidadFinancieraPlans = new CComboBox();
		cboEntidadFinancieraPlans.setPreferredSize(new Dimension(
				cboEntidadFinancieraPlans.getPreferredSize().width, 20));
		cboEntidadFinancieraPlans.setMandatory(true);
		cboEntidadFinancieraPlans.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem() != null) {
					loadPlanInfo((MEntidadFinancieraPlan) e.getItem());
				}
			}
		});
		txtCreditCardAmt.setValue(getModel().getSaldoMediosPago());
		org.jdesktop.layout.GroupLayout jPanelCreditCardLayout = new org.jdesktop.layout.GroupLayout(
				panelCreditCard);
		panelCreditCard.setLayout(jPanelCreditCardLayout);
		jPanelCreditCardLayout
				.setHorizontalGroup(jPanelCreditCardLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelCreditCardLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblCreditCardReceiptMedium)
										.add(lblCreditCardPlan)
										.add(lblCreditCardBank)
										.add(lblCreditCardNo)
										.add(lblCreditCardCouponNo)
										.add(lblCreditCardAmt)
										.add(lblCuotasCount).add(lblCuotaAmt))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(cboCreditCardReceiptMedium,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(cboEntidadFinancieraPlans,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(cboCreditCardBank,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtCreditCardNo,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtCreditCardCouponNo,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtCreditCardAmt,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtCuotasCount,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtCuotaAmt,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))
								.addContainerGap()));
		jPanelCreditCardLayout
				.setVerticalGroup(jPanelCreditCardLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelCreditCardLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditCardReceiptMedium)
										.add(cboCreditCardReceiptMedium,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditCardPlan)
										.add(cboEntidadFinancieraPlans,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditCardBank)
										.add(cboCreditCardBank,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditCardNo)
										.add(txtCreditCardNo,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditCardCouponNo)
										.add(txtCreditCardCouponNo,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCreditCardAmt)
										.add(txtCreditCardAmt,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCuotasCount)
										.add(txtCuotasCount,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelCreditCardLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCuotaAmt)
										.add(txtCuotaAmt,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(66, Short.MAX_VALUE)));
		return panelCreditCard;
	}

	@Override
	protected JComponent createPagoAdelantadoTab() {
		panelPagoAdelantado = new javax.swing.JPanel();
		panelPagoAdelantado.setOpaque(false);

		lblPagoAdelantado = new JLabel();
		lblPagoAdelantado.setText(getModel().isSOTrx() ? "COBRO" : "PAGO");
		lblPagoAdelantadoImporte = new JLabel();
		lblPagoAdelantadoImporte.setText("IMPORTE");
		txtPagoAdelantadoImporte = new JFormattedTextField();
		txtPagoAdelantadoImporte.setText("0");
		initFormattedTextField((JFormattedTextField) txtPagoAdelantadoImporte);
		pagoAdelantado = VComponentsFactory.VLookupFactory("C_Payment_ID",
				"C_Payment", m_WindowNo, DisplayType.Search, getModel()
						.getPagoAdelantadoSqlValidation());

		cashAdelantado = VComponentsFactory.VLookupFactory("C_CashLine_ID",
				"C_CashLine", m_WindowNo, DisplayType.Search, getModel()
						.getCashAnticipadoSqlValidation());
		lblPagoAdelantadoType = new JLabel();
		lblPagoAdelantadoType.setText("TIPO");
		cboPagoAdelantadoType = new VComboBox(new Object[] { getMsg("Payment"),
				getMsg("Cash") });
		// Por defecto pago.
		cboPagoAdelantadoType
				.setSelectedIndex(PAGO_ADELANTADO_TYPE_PAYMENT_INDEX);
		cboPagoAdelantadoType.setPreferredSize(new Dimension(200, 20));
		cboPagoAdelantadoType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updatePagoAdelantadoTab();
			}
		});
		cboPagoAdelantadoReceiptMedium = createPaymentMediumCombo(MPOSPaymentMedium.TENDERTYPE_AdvanceReceipt);
		pagoAdelantadoTypePanel = new JPanel();
		pagoAdelantadoTypePanel.setLayout(new BorderLayout());
		txtPagoAdelantadoAvailable = new JFormattedTextField();
		txtPagoAdelantadoAvailable.setEditable(false);
		lblPagoAdelantadoAvailable = new JLabel();
		lblPagoAdelantadoAvailable.setText("PENDIENTE");
		tenderTypeIndexsCombos.put(TAB_INDEX_PAGO_ADELANTADO,
				cboPagoAdelantadoReceiptMedium);
		cboPagoAdelantadoReceiptMedium
				.addItemListener(getPaymentMediumItemListener());

		org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(
				panelPagoAdelantado);
		panelPagoAdelantado.setLayout(jPanel7Layout);
		jPanel7Layout
				.setHorizontalGroup(jPanel7Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel7Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblPagoAdelantadoReceiptMedium)
										.add(lblPagoAdelantadoType)
										.add(lblPagoAdelantado)
										.add(lblPagoAdelantadoAvailable)
										.add(lblPagoAdelantadoImporte))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(cboPagoAdelantadoType,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(pagoAdelantadoTypePanel,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(txtPagoAdelantadoAvailable,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(txtPagoAdelantadoImporte,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE)
										.add(cboPagoAdelantadoReceiptMedium,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												165, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel7Layout
				.setVerticalGroup(jPanel7Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel7Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPagoAdelantadoReceiptMedium)
										.add(cboPagoAdelantadoReceiptMedium,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPagoAdelantadoType)
										.add(cboPagoAdelantadoType,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPagoAdelantado)
										.add(pagoAdelantadoTypePanel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPagoAdelantadoAvailable)
										.add(txtPagoAdelantadoAvailable,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPagoAdelantadoImporte)
										.add(txtPagoAdelantadoImporte,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE))));

		updatePagoAdelantadoTab();
		return panelPagoAdelantado;
	}

	@Override
	protected void createCamProyPanel() {
		panelCamProy.setOpaque(false);
		org.jdesktop.layout.GroupLayout panelCamProyLayout = new org.jdesktop.layout.GroupLayout(
				panelCamProy);
		panelCamProy.setLayout(panelCamProyLayout);
		panelCamProyLayout
				.setHorizontalGroup(panelCamProyLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(panelCamProyLayout
								.createSequentialGroup()
								.add(panelCamProyLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(lblCampaign).add(lblProject))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(panelCamProyLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(cboCampaign, 0, 239,
												Short.MAX_VALUE)
										.add(cboProject, 0, 239,
												Short.MAX_VALUE))));
		panelCamProyLayout
				.setVerticalGroup(panelCamProyLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(panelCamProyLayout
								.createSequentialGroup()
								.add(panelCamProyLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCampaign)
										.add(cboCampaign,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(panelCamProyLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblProject)
										.add(cboProject,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))));
	}

	@Override
	protected void createPaymentTab() {
		// Creo el panel de tipo de pago
		createTenderTypePanel();
		// Creo el panel de descuento/recargo como el que se encuentra en el TPV
		createPaymentMediumDiscountPanel();
		// Creo el texto de descuento/recargo del documento
		createDocumentDiscountComponents();
		// Creo el panel con el resumen
		createSummaryPanel();

		org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout
				.setHorizontalGroup(jPanel4Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel4Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel4Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jTabbedPane2,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.add(panelCamProy,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.add(panelTenderType,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.add(panelPaymentDiscount,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
								// .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED,
										10, 10)
								.add(jPanel4Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jPanel4Layout
												.createSequentialGroup()
												.add(cmdGrabar)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(cmdEliminar)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(cmdEditar)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED,
														org.jdesktop.layout.LayoutStyle.RELATED,
														Short.MAX_VALUE)
												.add(lblTotalPagar2)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(txtTotalPagar2,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														100, Short.MAX_VALUE)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED))												
										.add(panelSummary,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.add(jScrollPane2,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												100, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel4Layout
				.setVerticalGroup(jPanel4Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel4Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel4Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(jPanel4Layout
												.createSequentialGroup()
												.add(jScrollPane2,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(jPanel4Layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.VERTICAL)
														.add(cmdGrabar)
														.add(cmdEliminar)
														.add(cmdEditar)
														.add(lblTotalPagar2)
														.add(txtTotalPagar2,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(panelSummary))
										.add(org.jdesktop.layout.GroupLayout.LEADING,
												jPanel4Layout
														.createSequentialGroup()
														.add(panelCamProy,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(panelTenderType,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(jTabbedPane2,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																330, 330)
														.add(panelPaymentDiscount,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))));
	}

	/**
	 * Creo el panel de tipo de pago
	 */
	protected void createTenderTypePanel() {
		panelTenderType = new javax.swing.JPanel();
		panelTenderType.setOpaque(false);
		lblTenderType = new javax.swing.JLabel(Msg.translate(m_ctx,
				"TenderType"));
		cboTenderType = createTenderTypeCombo();
		cboTenderType.setMandatory(true);
		cboTenderType.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				ValueNamePair tenderType = (ValueNamePair) arg0.getItem();
				processPaymentTabs(jTabbedPane2, tenderType.getValue());
				selectPaymentTab(jTabbedPane2);
				// Actualizar panel de resumen total
				updateSummaryInfo();
				// Actualizar panel A Cobrar
				
				updatePaymentMediumCombo(tenderType.getValue());
				
				MPOSPaymentMedium selectedPaymentMedium = (MPOSPaymentMedium) tenderTypeIndexsCombos.get(jTabbedPane2.getSelectedIndex()).getSelectedItem();
				
				// Si estamos editando no debemos actualizar la info del medio
				// de pago,
				// sino si
				updateDiscount(selectedPaymentMedium);
			}
		});
		// Selecciono uno por default
		// cboTenderType.setSelectedItem(MPOSPaymentMedium.TENDERTYPE_Cash);
		// Layout del panel
		org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(
				panelTenderType);
		panelTenderType.setLayout(jPanel7Layout);
		jPanel7Layout
				.setHorizontalGroup(jPanel7Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel7Layout
								.createSequentialGroup()
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblCurrency).add(lblTenderType))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(cboCurrency,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(cboTenderType,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))));
		jPanel7Layout
				.setVerticalGroup(jPanel7Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel7Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblCurrency)
										.add(cboCurrency,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel7Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblTenderType)
										.add(cboTenderType,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)));
	}

	@Override
	protected void createButtonsPanel() {
		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				jPanel2Layout
						.createSequentialGroup()
						.addContainerGap(424, Short.MAX_VALUE)
						.add(cmdCancel)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED)
						.add(cmdProcess).addContainerGap()));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel2Layout
								.createSequentialGroup()
								.add(jPanel2Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(cmdProcess).add(cmdCancel))));
	}

	@Override
	protected void addPanelsToFrame() {
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				m_frame.getContentPane());
		m_frame.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)
				.add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)
				.add(jTabbedPane1,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 672,
						Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(org.jdesktop.layout.GroupLayout.TRAILING,
						layout.createSequentialGroup()
								.add(jPanel1,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(jTabbedPane1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel2,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
	}

	/**
	 * Crea los componentes para descuento/recargo del documento
	 */
	protected void createDocumentDiscountComponents() {
		lblDiscountAmt = new javax.swing.JLabel(Msg.translate(m_ctx,
				"DiscountCharge"));
		txtDiscountAmt = new JFormattedTextField();
		initFormattedTextField((JFormattedTextField) txtDiscountAmt);
		txtDiscountAmt.setHorizontalAlignment(JTextField.LEFT);
		txtDiscountAmt.setEditable(false);
	}

	/**
	 * Crea el panel de descuentos por medio de pago
	 */
	protected void createPaymentMediumDiscountPanel() {
		panelPaymentDiscount = new javax.swing.JPanel();
		panelPaymentDiscount.setOpaque(false);
		panelPaymentDiscount.setBorder(BorderFactory.createEtchedBorder());

		lblPaymentDiscount = new javax.swing.JLabel(Msg.translate(m_ctx,
				"DiscountCharge"));
		lblPaymentToPayDiscount = new javax.swing.JLabel(Msg.translate(m_ctx,
				"DiscountedChargedToPayAmt"));
		txtPaymentDiscount = new JTextField();
		txtPaymentDiscount.setEditable(false);
		txtPaymentToPayDiscount = new JFormattedTextField();
		initFormattedTextField((JFormattedTextField) txtPaymentToPayDiscount);
		txtPaymentToPayDiscount.setHorizontalAlignment(JTextField.LEFT);
		txtPaymentToPayDiscount.setEditable(false);

		org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(
				panelPaymentDiscount);
		panelPaymentDiscount.setLayout(jPanel8Layout);
		jPanel8Layout
				.setHorizontalGroup(jPanel8Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel8Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel8Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblPaymentDiscount)
										.add(lblPaymentToPayDiscount))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel8Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtPaymentDiscount,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtPaymentToPayDiscount,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))
								.addContainerGap()));
		jPanel8Layout
				.setVerticalGroup(jPanel8Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel8Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel8Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPaymentDiscount)
										.add(txtPaymentDiscount,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel8Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblPaymentToPayDiscount)
										.add(txtPaymentToPayDiscount,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
	}

	/**
	 * Crea el panel de resumen de pagos
	 */
	protected void createSummaryPanel() {
		panelSummary = new javax.swing.JPanel();
		panelSummary.setOpaque(false);
		// panelSummary.setBorder(BorderFactory.createEtchedBorder());

		org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(
				panelSummary);
		panelSummary.setLayout(jPanel9Layout);
		jPanel9Layout
				.setHorizontalGroup(jPanel9Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel9Layout
								.createSequentialGroup()
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblDifCambio)
										.add(lblMedioPago2)
										.add(lblRetenciones2))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtDifCambio,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtMedioPago2,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtRetenciones2,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										30, 30)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING)
										.add(lblDiscountAmt)
										.add(lblSaldo))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(txtDiscountAmt,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE)
										.add(txtSaldo,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												160, Short.MAX_VALUE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		jPanel9Layout
				.setVerticalGroup(jPanel9Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel9Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblDifCambio)
										.add(txtDifCambio,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblMedioPago2)
										.add(txtMedioPago2,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(lblDiscountAmt)
										.add(txtDiscountAmt,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel9Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(lblRetenciones2)
										.add(txtRetenciones2,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(lblSaldo)
										.add(txtSaldo,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
	}

	/**
	 * @return combo con los tender type disponibles
	 */
	protected CComboBox createTenderTypeCombo() {
		CComboBox tenderTypeCombo = new CComboBox();
		// Obtiene los tipos de pago disponibles a partir del contexto Recibos
		// de Cliente, en realidad se excluye tpv para obtener solo Recibos de
		// Cliente y ambos
		int currencyID = ( (Integer) cboCurrency.getValue() == null) ? m_C_Currency_ID : (Integer) cboCurrency.getValue();
		
		List<ValueNamePair> list = MPOSPaymentMedium.getTenderTypesByContextOfUse(Env.getCtx(), MPOSPaymentMedium.CONTEXT_POSOnly, true, true, null, currencyID);
		tenderTypesComboValues = new HashMap<String, ValueNamePair>();
		// Se agregan al combo
		for (ValueNamePair tenderType : list) {
			tenderTypeCombo.addItem(tenderType);
			tenderTypesComboValues.put(tenderType.getValue(), tenderType);
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
			cboTenderType.addItem(tenderType);
			tenderTypesComboValues.put(tenderType.getValue(), tenderType);
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
	protected void processPaymentTabs(JTabbedPane tabbedPane, String tenderType) {
		List<Integer> indexRelations = new ArrayList<Integer>();
		if (tenderType != null) {
			// Obtengo la lista de índices de pestañas relacionadas con el tipo
			// de
			// pago parámetro
			indexRelations = tenderTypesTabRelations.get(tenderType);
		}
		int tabCount = tabbedPane.getTabCount();
		// Itero por las pestañas y habilito/deshabilito las correspondientes
		for (int i = 0; i < tabCount; i++) {
			// Si el índice actual se encuentra en la lista obtenida
			// anteriormente entonces lo habilito
			tabbedPane.setEnabledAt(i, indexRelations.contains(i));
		}
		tabbedPane.repaint();
		panelPaymentDiscount.repaint();
	}

	/**
	 * Selecciona la primer pestaña que se encuentre habilitada del tabbed pane
	 * parámetro
	 * 
	 * @param tabbedPane
	 *            panel de pestañas
	 */
	protected void selectPaymentTab(JTabbedPane tabbedPane) {
		int tabCount = tabbedPane.getTabCount();
		boolean found = false;
		// Itero por todas las pestañas y selecciono la primera que encuentro
		// habilitada
		int i;
		for (i = 0; i < tabCount && !found; i++) {
			found = tabbedPane.isEnabledAt(i);
		}
		// Si encontré alguna entonces la selecciono
		if (found) {
			tabbedPane.setSelectedIndex(i - 1);
		}
		tabbedPane.repaint();
		panelPaymentDiscount.repaint();
		m_frame.repaint();
	}

	private VOrdenCobroModel getCobroModel() {
		return (VOrdenCobroModel) getModel();
	}

	@Override
	protected void clearMediosPago() {
		super.clearMediosPago();
		if (panelCreditCard != null) {
			txtCreditCardAmt.setValue(0);
			txtCreditCardCouponNo.setText("");
			txtCreditCardNo.setText("");
			txtCuotaAmt.setValue(0);
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
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboRetencionReceiptMedium
				.getSelectedItem();
		saveBasicValidation(paymentMedium, null);
		Integer retencionSchemaID = (Integer) retencSchema.getValue();
		String retencionNumber = txtRetencNroRetenc.getText().trim();
		BigDecimal amount = null;
		try {
			amount = numberParse(txtRetencImporte.getText());
		} catch (Exception e) {
			throw new Exception("@Invalid@ @Amount@");
		}
		// Se agrega la retención como medio de cobro.
		// La fecha es la fecha de la RC
		getCobroModel().addRetencion(retencionSchemaID, retencionNumber,
				amount, getModel().m_fechaTrx, getC_Campaign_ID(), getC_Project_ID());
	}

	protected void saveCreditCardMedioPago() throws Exception {
		// Obtengo la data de la interfaz gráfica
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCreditCardReceiptMedium
				.getSelectedItem();
		MEntidadFinancieraPlan plan = (MEntidadFinancieraPlan) cboEntidadFinancieraPlans
				.getSelectedItem();
		saveBasicValidation(paymentMedium, plan);
		getCobroModel().addCreditCard(
				paymentMedium,
				plan,
				txtCreditCardNo.getText(),
				txtCreditCardCouponNo.getText(),
				(BigDecimal) txtCreditCardAmt.getValue(),
				(String) cboCreditCardBank.getValue(),
				Util.isEmpty(txtCuotasCount.getText()) ? 0 : Integer
						.parseInt(txtCuotasCount.getText()),
				(BigDecimal) txtCuotaAmt.getValue(), getC_Campaign_ID(),
				getC_Project_ID(), (Integer) cboCurrency.getValue());
	}

	@Override
	protected MedioPagoCheque saveCheckMedioPago() throws Exception {
		// Obtengo la data de la interfaz gráfica
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCheckReceiptMedium
				.getSelectedItem();
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
				chequeFechaEmision.getTimestamp(),
				chequeFechaPago.getTimestamp(), txtChequeALaOrden.getText(),
				getBankName(), txtChequeCUITLibrador.getText(),
				txtChequeDescripcion.getText(), getC_Campaign_ID(),
				getC_Project_ID(), monedaOriginalID, cboChequeBancoID!=null?(Integer)cboChequeBancoID.getValue():null);
		return null;
	}

	@Override
	protected MedioPagoEfectivo saveCashMedioPago() throws Exception {
		MedioPagoEfectivo mpe = super.saveCashMedioPago();
		// Validación para que el monto ingresado no supere el a pagar
		// toPayValidation();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCashReceiptMedium
				.getSelectedItem();
		saveBasicValidation(paymentMedium, null);
		mpe.setPaymentMedium(paymentMedium);
		mpe.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpe.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpe;
	}

	@Override
	protected MedioPagoTransferencia saveTransferMedioPago() throws Exception {
		MedioPagoTransferencia mpt = super.saveTransferMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboTransferReceiptMedium
				.getSelectedItem();
		saveBasicValidation(paymentMedium, null);
		mpt.setPaymentMedium(paymentMedium);
		mpt.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mpt.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mpt;
	}

	@Override
	protected ArrayList<MedioPagoCredito> saveCreditMedioPago() throws Exception {
		ArrayList<MedioPagoCredito> mpcs = super.saveCreditMedioPago();
		
		for (MedioPagoCredito mpc : mpcs) {
			MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboCreditReceiptMedium
					.getSelectedItem();
			saveBasicValidation(paymentMedium, null);
			mpc.setPaymentMedium(paymentMedium);
			mpc.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
			mpc.setMonedaOriginalID((Integer) cboCurrency.getValue());
		}
		return mpcs;
	}

	@Override
	protected MedioPago savePagoAdelantadoMedioPago() throws Exception {
		MedioPago mp = super.savePagoAdelantadoMedioPago();
		MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) cboPagoAdelantadoReceiptMedium
				.getSelectedItem();
		saveBasicValidation(paymentMedium, null);
		mp.setPaymentMedium(paymentMedium);
		mp.setDiscountSchemaToApply(getCobroModel().getCurrentGeneralDiscount());
		mp.setMonedaOriginalID((Integer) cboCurrency.getValue());
		
		return mp;
	}

	@Override
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
	public String getMsg(String name) {
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
	protected void updateDependent() {
		updateCustomPaymentsTabsState();
	}

	@Override
	protected void loadMedioPago(VOrdenPagoModel.MedioPago mp) {
		if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_TARJETACREDITO)) {
			jTabbedPane2.setSelectedIndex(m_creditCardTabIndex);
			MedioPagoTarjetaCredito tarjeta = (MedioPagoTarjetaCredito) mp;
			cboCreditCardReceiptMedium.setSelectedItem(tarjeta
					.getPaymentMedium());
			cboEntidadFinancieraPlans.setSelectedItem(tarjeta
					.getEntidadFinancieraPlan());
			txtCreditCardCouponNo.setText(tarjeta.getCouponNo());
			txtCreditCardNo.setText(tarjeta.getCreditCardNo());
			txtCreditCardAmt.setValue(tarjeta.getImporte());
			txtCuotasCount.setText(""
					+ tarjeta.getEntidadFinancieraPlan().getCuotasPago());
			txtCuotaAmt.setValue(tarjeta.getCuotaAmt());
			cboCreditCardBank.setValue(tarjeta.getBank());
		} else {
			super.loadMedioPago(mp);
			CComboBox comboReceiptMedium = tenderTypeIndexsCombos
					.get(jTabbedPane2.getSelectedIndex());
			comboReceiptMedium.setSelectedItem(mp.getPaymentMedium());
			comboReceiptMedium.repaint();
			jTabbedPane2.repaint();
		}
		// Selección automática de pestañas a partir del tender type
		int indexSelected = jTabbedPane2.getSelectedIndex();
		cboTenderType.setSelectedItem(tenderTypesComboValues
				.get(tabsTenderTypeRelations.get(indexSelected)));
		jTabbedPane2.setSelectedIndex(indexSelected);
	}

	/**
	 * Crea un combo con los medios de pago relacionados con el tipo de medio de
	 * pago parámetro. Lista vacía si no existen medios de pago configurados
	 * 
	 * @param tenderType
	 *            tipo de medio de pago
	 * @return combo con los medios de pago configurados
	 */
	protected CComboBox createPaymentMediumCombo(String tenderType) {
		int currencyID = ( (Integer) cboCurrency.getValue() == null) ? m_C_Currency_ID : (Integer) cboCurrency.getValue(); 
		
		// Creo la lista a partir del tipo de medio de pago
		List<MPOSPaymentMedium> mediums = getCobroModel().getPaymentMediums(
				tenderType, currencyID);
		CComboBox combo = null;
		if (mediums != null) {
			combo = new CComboBox(mediums.toArray());
		} else {
			combo = new CComboBox();
		}
		combo.setMandatory(true);
		combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 20));
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
		tenderTypeIndexsCombos.get(jTabbedPane2.getSelectedIndex()).removeAllItems();		
		
		// Creo la lista a partir del tipo de medio de pago
		List<MPOSPaymentMedium> mediums = getCobroModel().getPaymentMediums(tenderType, (Integer) cboCurrency.getValue());
		
		if (mediums != null) {
			for(MPOSPaymentMedium medium: mediums){
				tenderTypeIndexsCombos.get(jTabbedPane2.getSelectedIndex()).addItem(medium);
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
					cboEntidadFinancieraPlans.addItem(mEntidadFinancieraPlan);
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

		MPOSPaymentMedium mp = (MPOSPaymentMedium) cboCreditCardReceiptMedium.getSelectedItem();
		if (mp != null){
			if (!Util.isEmpty(mp.getBank(), true)) {
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
			txtCuotasCount.setEditable(false);
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
				getModel().AD_Org_ID, m_ctx)) : null);

		// Si es un pago con tarjeta de crédito se calcula y muestra el importe
		// de cada cuota.
		if (paymentMedium != null
				&& paymentMedium.getTenderType().equals(
						MPOSPaymentMedium.TENDERTYPE_CreditCard)) {
			MEntidadFinancieraPlan plan = getSelectedPlan();
			if (paymentToPayAmt != null) {
				txtCuotaAmt.setValue(paymentToPayAmt);
			}
			// El importe de la cuota se calcula en base al importe del pago
			// ingresado por el usuario
			if (plan != null) {
				BigDecimal amt = txtCreditCardAmt.getValue() == null ? BigDecimal.ZERO
						: (BigDecimal) txtCreditCardAmt.getValue();
				BigDecimal cuotaAmt = amt.divide(
						new BigDecimal(plan.getCuotasPago()), 10,
						BigDecimal.ROUND_HALF_UP);
				txtCuotaAmt.setValue(cuotaAmt);
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
		return (MEntidadFinancieraPlan) cboEntidadFinancieraPlans
				.getSelectedItem();
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
				txtChequeBanco.setEditable(false);
			} else {
				txtChequeBanco.setEditable(true);
				txtChequeBanco.setText("");
			}
		}
		else{
			if (paymentMedium.getC_Bank_ID() != 0) {
				cboChequeBancoID.setValue(paymentMedium.getC_Bank_ID());
				cboChequeBancoID.setReadWrite(false);
			} else {
				txtChequeBanco.setEditable(true);
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
			txtPOS.setValue(pos);
			txtPOS.setReadWrite(!existsPOS);
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
		Date fechaEmi = chequeFechaEmision.getTimestamp();
		if (fechaEmi == null) {
			fechaEmi = new Date();
			chequeFechaEmision.setValue(fechaEmi);
		}
		chequeFechaPago.setValue(getCobroModel().getFechaPagoCheque(fechaEmi,
				paymentMedium));
	}

	@Override
	protected void updateTotalAPagar1() {
		// Actualizo el cargo de la organización para facturas vencidas
		updateOverdueInvoicesCharge();
		BigDecimal total = getModel().getSumaTotalPagarFacturas();
		txtTotalPagar1.setValue(total);
		txtTotalPagar1.setText(numberFormat(total));
	}

    protected void updateCustomPaymentsTabsState(){
    	if(jTabbedPane2.getSelectedIndex() == TAB_INDEX_CHEQUE){
    		if(cboCheckReceiptMedium.getItemCount() > 0){
            	cboCheckReceiptMedium.setSelectedIndex(0);
            	loadCheckInfo((MPOSPaymentMedium) cboCheckReceiptMedium.getSelectedItem());
            }
    	}
    	else if(jTabbedPane2.getSelectedIndex() == m_creditCardTabIndex){
    		if(cboCreditCardReceiptMedium.getItemCount() > 0){
    			cboCreditCardReceiptMedium.setSelectedIndex(0);
    			loadPlans((MPOSPaymentMedium) cboCreditCardReceiptMedium
    					.getSelectedItem());
    		}
    	}
    	else if(jTabbedPane2.getSelectedIndex() == TAB_INDEX_EFECTIVO){
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
			value = (ValueNamePair)cboTenderType.getItemAt(i);
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
		if (jTabbedPane1.getSelectedIndex() == 1)
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
			BigDecimal amt = txtGroupingAmt.getValue() != null ? (BigDecimal) txtGroupingAmt
					.getValue() : BigDecimal.ZERO;
			getCobroModel().updateGroupingAmtInvoices(amt);
			repaint();
			tblFacturas.repaint();
		}
	}

	protected void setDefaultGroupAmtValue() {
		setGroupingAmt(getCobroModel().getDefaultGroupingValue());
	}

	protected void setGroupingAmt(BigDecimal groupingAmt) {
		txtGroupingAmt.setValue(groupingAmt);
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
		MPOSPaymentMedium selectedPaymentMedium = (MPOSPaymentMedium) tenderTypeIndexsCombos
				.get(jTabbedPane2.getSelectedIndex()).getSelectedItem();
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
		MPOSPaymentMedium selectedPaymentMedium = (MPOSPaymentMedium) tenderTypeIndexsCombos
				.get(jTabbedPane2.getSelectedIndex()).getSelectedItem();
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
		repaint();
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
		txtOrgCharge.setValue(getCobroModel().getOrgCharge());
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
		tblFacturas.repaint();
	}

    @Override
    protected void updateCustomPayAmt(BigDecimal amt){
    	Integer tabIndexSelected = jTabbedPane2.getSelectedIndex();
    	if(tabIndexSelected.equals(m_retencTabIndex)){
    		txtRetencImporte.setValue(amt);
    	}
    	else if(tabIndexSelected.equals(m_creditCardTabIndex)){
    		txtCreditCardAmt.setValue(amt);
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

	private class PaymentMediumItemListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getItem() == null)
				return;
			MPOSPaymentMedium paymentMedium = (MPOSPaymentMedium) e.getItem();
			loadPaymentMediumInfo(paymentMedium);
			updateDiscount(paymentMedium);
			updateBank(paymentMedium);
		}
	}

	@Override
	protected void customKeyBindingsInit() {
		getActionKeys().put(GOTO_TENDER_TYPE,
				KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));

		// Accion: Seleccionar el combo de tipo de pago
		m_frame.getRootPane().getActionMap()
				.put(GOTO_TENDER_TYPE, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						cboTenderType.requestFocus();
					}
				});
	}

	@Override
	protected void customUpdateCaptions() {
		if (jTabbedPane1.getSelectedIndex() == 0) {
			setActionEnabled(GOTO_TENDER_TYPE, false);
			setActionEnabled(GOTO_BPARTNER, true);
		} else if (jTabbedPane1.getSelectedIndex() == 1) {
			setActionEnabled(GOTO_TENDER_TYPE, true);
		}
	}

	public String getBankName() {
		if (txtChequeBanco.isVisible())
			return txtChequeBanco.getText();
		else if(cboChequeBancoID.getValue() == null)
				return null;
			else
				return getModel().getBank((Integer) cboChequeBancoID.getValue());		
	}
	
	
	@Override
    protected CallResult validateDebitNote() {
		return new CallResult();
	}
	
	@Override
	protected boolean isPrintRetentions(){
		return false;
	}

	@Override
	protected void doBPartnerValidations(){
		// Por lo pronto no existen validaciones de clientes
	}
}
