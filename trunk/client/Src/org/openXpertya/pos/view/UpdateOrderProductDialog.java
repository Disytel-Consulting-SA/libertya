package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AUserAuth;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.DiscountCalculator.IDocumentLine.DiscountApplication;
import org.openXpertya.pos.ctrl.PoSModel;
import org.openXpertya.pos.model.OrderProduct;
import org.openXpertya.pos.model.User;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.UserAuthConstants;
import org.openXpertya.util.UserAuthData;


public class UpdateOrderProductDialog extends JDialog {

	private final int BUTTON_PANEL_WIDTH = 160;
	private final int BUTTON_WIDTH = 155;
	private final int DIALOG_WIDTH = 650;
	private final int DIALOG_HEIGHT = 250;
	
	private OrderProduct orderProduct;
	private PoSMainForm poS;
	private User user;
	private PoSMsgRepository msgRepository;
	
	private JPanel jContentPane = null;
	private CPanel cMainPanel = null;
	private CPanel cItemPanel = null;
	private CPanel cCmdPanel = null;
	private CLabel cProductLabel = null;
	private CLabel cProductDescLabel = null;
	private CLabel cProductPriceLabel = null;
	private CLabel cProductTaxedPriceLabel = null;
	private CLabel cProductTaxRateLabel = null;
	private VNumber cPriceListText = null;
	private VNumber cProductTaxedPriceText = null;
	private VNumber cDiscountAmtText = null;
	private CLabel cDiscountLabel = null;
	private VNumber cDiscountText = null;
	private CLabel cCountLabel = null;
	private CTextField cCountText = null;
	private CButton cOkButton = null;
	private CButton cRemoveButton = null;
	private CButton cCancelButton = null;
	private CLabel cApplicationLabel = null;
	private CPanel cApplicationPanel = null;
	private JRadioButton cToPriceRadio = null;
	private JRadioButton cBonusRadio = null;
	private ButtonGroup applicationGroup = null;
	private AUserAuth userAuthPanel = null;
	private String MSG_PRODUCT;
	private String MSG_OK;
	private String MSG_DELETE;
	private String MSG_CANCEL;
	private String MSG_PRICE;
	private String MSG_DISCOUNT;
	private String MSG_COUNT;
	private String MSG_NO_PRODUCT_PRICE;
	private String MSG_INVALID_PRODUCT_PRICE;
	private String MSG_INVALID_PRODUCT_COUNT;
	private String MSG_CONFIRM_DELETE_RPODUCT;
	private String MSG_UPDATE_ITEM;
	private String MSG_TAXRATE;
	private String MSG_TAXED_PRICE;
	private String MSG_APPLICATION;
	private String MSG_TO_PRICE;
	private String MSG_BONUS;
	private String MSG_PRICE_LIST;
	private String MSG_MANUAL_DISCOUNT;
	private String MSG_AMOUNT;
	private String MSG_SUPERVISOR_AUTH;
	private String MSG_SURPASS_MAX_QTY;
	private String MSG_INVALID_PRICE;
	private String MSG_INVALID_FINAL_PRICE;
	private String MSG_INVALID_COUNT;
	
	private final String CHANGE_FOCUS_USER_AUTH = "changeFocusUserAuth";
	
	private Map<String,KeyStroke> actionKeys;
	
	/**
	 * This is the default constructor
	 */
	public UpdateOrderProductDialog(OrderProduct orderProduct, PoSMainForm poS) {
		super();
		this.orderProduct = orderProduct;
		this.poS = poS;
		this.msgRepository = poS.getMsgRepository();
		initialize();
	}

	public UpdateOrderProductDialog() {
		super();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		initMsgs();
		keyBindingsInit();
		this.setSize(590,400);
		this.setTitle(MSG_UPDATE_ITEM);
		this.setResizable(false);
		this.setContentPane(getJContentPane());
	}
	
	private void initMsgs() {
		MSG_PRODUCT = getMsg("M_Product_ID");
		MSG_OK = getMsg("OK");
		MSG_DELETE = getMsg("POSDelete");
		MSG_CANCEL = getMsg("Cancel");
		MSG_PRICE = getMsg("Price");
		MSG_DISCOUNT = getMsg("PercentDiscount");
		MSG_COUNT = getMsg("Qty");
		MSG_NO_PRODUCT_PRICE = getMsg("NoProductPriceError");
		MSG_INVALID_PRODUCT_PRICE = getMsg("InvalidProductPriceError");
		MSG_INVALID_PRODUCT_COUNT = getMsg("InvalidProductCountError");
		MSG_CONFIRM_DELETE_RPODUCT = getMsg("ConfirmDeleteProduct");
		MSG_UPDATE_ITEM = getMsg("ItemUpdate");
		MSG_TAXRATE = getMsg("Rate") + " %";
		MSG_TAXED_PRICE = getMsg("Price");
		MSG_APPLICATION = getMsg("Application");
		MSG_TO_PRICE = getMsg("ToPrice");
		MSG_BONUS = getMsg("Bonus");
		MSG_PRICE_LIST = getMsg("PriceList");
		MSG_MANUAL_DISCOUNT = getMsg("ManualDiscount");
		MSG_AMOUNT = getMsg("Amt");
		MSG_SUPERVISOR_AUTH = getMsg("SupervisorAuth");
		MSG_SURPASS_MAX_QTY = getMsg("SurpassMaxOrderLineQty");
		MSG_INVALID_COUNT = getMsg("InvalidQty");
		MSG_INVALID_PRICE = getMsg("InvalidPrice");
		MSG_INVALID_FINAL_PRICE = getMsg("InvalidFinalPrice");
	}

	private void keyBindingsInit(){
		// Se asignan las teclas shorcut de las acciones.
		setActionKeys(new HashMap<String,KeyStroke>());
		getActionKeys().put(CHANGE_FOCUS_USER_AUTH, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		
		
		// Se crean las acciones pertinentes
        getRootPane().getActionMap().put(CHANGE_FOCUS_USER_AUTH,
        	new AbstractAction() {
        		public void actionPerformed(ActionEvent e) {
					userAuthPanel.setFocus();
				}
        	}
        );
		
		// Se habilitan las necesarias
		setActionEnabled(CHANGE_FOCUS_USER_AUTH, true);
	}
	
	private void setActionEnabled(String action, boolean enabled) {
		String kAction = (enabled?action:"none");
        KeyStroke keyStroke = getActionKeys().get(action);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				keyStroke, kAction);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new CPanel();
			jContentPane.setLayout(new BorderLayout());
			//jContentPane.setPreferredSize(new java.awt.Dimension(590,110));
			jContentPane.setPreferredSize(new java.awt.Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
			jContentPane.add(getCMainPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.setOpaque(false);
		}
		return jContentPane;
	}

	/**
	 * This method initializes cMainPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCMainPanel() {
		if (cMainPanel == null) {
			cMainPanel = new CPanel();
			cMainPanel.setLayout(new BorderLayout());
			cMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			cMainPanel.setOpaque(false);
			cMainPanel.add(getCItemPanel(), java.awt.BorderLayout.CENTER);
			cMainPanel.add(getCCmdPanel(), java.awt.BorderLayout.EAST);
		}
		return cMainPanel;
	}

	/**
	 * This method initializes cItemPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCItemPanel() {
		if (cItemPanel == null) {
			final int V_SPAN = 3;
			
			GridBagConstraints gbc0603 = new GridBagConstraints();
			gbc0603.gridx = 3;
			gbc0603.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0603.anchor = java.awt.GridBagConstraints.WEST;
			gbc0603.gridy = 6;
			gbc0603.gridwidth = 2;
			GridBagConstraints gbc0602 = new GridBagConstraints();
			gbc0602.gridx = 2;
			gbc0602.anchor = java.awt.GridBagConstraints.WEST;
			gbc0602.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0602.gridy = 6;
			
			GridBagConstraints gbc0601 = new GridBagConstraints();
			gbc0601.fill = java.awt.GridBagConstraints.NONE;
			gbc0601.gridy = 6;
			gbc0601.anchor = java.awt.GridBagConstraints.WEST;
			gbc0601.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0601.gridx = 1;
			GridBagConstraints gbc0600 = new GridBagConstraints();
			gbc0600.gridx = 0;
			gbc0600.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0600.anchor = java.awt.GridBagConstraints.WEST;
			gbc0600.gridy = 6;
			
			GridBagConstraints gbc0503 = new GridBagConstraints();
			gbc0503.gridx = 3;
			gbc0503.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0503.anchor = java.awt.GridBagConstraints.WEST;
			gbc0503.gridy = 5;
			gbc0503.gridwidth = 2;
			GridBagConstraints gbc0502 = new GridBagConstraints();
			gbc0502.gridx = 2;
			gbc0502.anchor = java.awt.GridBagConstraints.WEST;
			gbc0502.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0502.gridy = 5;
			
			GridBagConstraints gbc0501 = new GridBagConstraints();
			gbc0501.fill = java.awt.GridBagConstraints.NONE;
			gbc0501.gridy = 5;
			gbc0501.anchor = java.awt.GridBagConstraints.WEST;
			gbc0501.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0501.gridx = 1;
			GridBagConstraints gbc0500 = new GridBagConstraints();
			gbc0500.gridx = 0;
			gbc0500.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0500.anchor = java.awt.GridBagConstraints.WEST;
			gbc0500.gridy = 5;
			gbc0500.gridwidth = 4;
			gbc0500.insets = new java.awt.Insets(10,0,3,0);
			
			GridBagConstraints gbc0303 = new GridBagConstraints();
			gbc0303.gridx = 3;
			gbc0303.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0303.anchor = java.awt.GridBagConstraints.WEST;
			gbc0303.gridy = 3;
			gbc0303.gridwidth = 2;
			GridBagConstraints gbc0302 = new GridBagConstraints();
			gbc0302.gridx = 2;
			gbc0302.anchor = java.awt.GridBagConstraints.WEST;
			gbc0302.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0302.gridy = 3;
			gbc0302.gridwidth = 3;
			cApplicationLabel = new CLabel();
			cApplicationLabel.setText(MSG_APPLICATION);
			
			GridBagConstraints gbc0401 = new GridBagConstraints();
			gbc0401.fill = java.awt.GridBagConstraints.NONE;
			gbc0401.gridy = 4;
			gbc0401.weightx = 1.0;
			gbc0401.anchor = java.awt.GridBagConstraints.WEST;
			gbc0401.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0401.gridx = 1;
			GridBagConstraints gbc0400 = new GridBagConstraints();
			gbc0400.gridx = 0;
			gbc0400.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0400.anchor = java.awt.GridBagConstraints.WEST;
			gbc0400.gridy = 4;
			
			GridBagConstraints gbc0201 = new GridBagConstraints();
			gbc0201.fill = java.awt.GridBagConstraints.NONE;
			gbc0201.gridy = 2;
			gbc0201.weightx = 1.0;
			gbc0201.anchor = java.awt.GridBagConstraints.WEST;
			gbc0201.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0201.gridx = 1;
			GridBagConstraints gbc0200 = new GridBagConstraints();
			gbc0200.gridx = 0;
			gbc0200.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0200.anchor = java.awt.GridBagConstraints.WEST;
			gbc0200.gridy = 2;
			gbc0200.gridwidth = 4;
			gbc0200.insets = new java.awt.Insets(10,0,3,0);
			cProductTaxedPriceLabel = new CLabel();
			cProductTaxedPriceLabel.setText(MSG_TAXED_PRICE);
			GridBagConstraints gbc0103 = new GridBagConstraints();
			gbc0103.gridx = 3;
			gbc0103.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0103.anchor = java.awt.GridBagConstraints.WEST;
			gbc0103.gridy = 1;
			GridBagConstraints gbc0102 = new GridBagConstraints();
			gbc0102.gridx = 2;
			gbc0102.anchor = java.awt.GridBagConstraints.WEST;
			gbc0102.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0102.gridy = 1;
			cProductTaxRateLabel = new CLabel();
			cProductTaxRateLabel.setText(MSG_TAXRATE);
			GridBagConstraints gbc0403 = new GridBagConstraints();
			gbc0403.fill = java.awt.GridBagConstraints.NONE;
			gbc0403.gridy = 4;
			gbc0403.weightx = 1.0;
			gbc0403.anchor = java.awt.GridBagConstraints.WEST;
			gbc0403.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0403.gridwidth = 3;
			gbc0403.gridx = 3;
			GridBagConstraints gbc0402 = new GridBagConstraints();
			gbc0402.gridx = 2;
			gbc0402.anchor = java.awt.GridBagConstraints.WEST;
			gbc0402.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0402.gridy = 4;
			//gbc0402.gridwidth = 3;
			GridBagConstraints gbc0301 = new GridBagConstraints();
			gbc0301.fill = java.awt.GridBagConstraints.NONE;
			gbc0301.gridy = 3;
			gbc0301.weightx = 1.0;
			gbc0301.anchor = java.awt.GridBagConstraints.WEST;
			gbc0301.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0301.gridx = 1;
			GridBagConstraints gbc0300 = new GridBagConstraints();
			gbc0300.gridx = 0;
			gbc0300.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0300.anchor = java.awt.GridBagConstraints.WEST;
			gbc0300.gridy = 3;
			GridBagConstraints gbc0205 = new GridBagConstraints();
			gbc0205.fill = java.awt.GridBagConstraints.NONE;
			gbc0205.gridy = 2;
			gbc0205.weightx = 1.0;
			gbc0205.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0205.anchor = java.awt.GridBagConstraints.WEST;
			gbc0205.gridx = 5;
			GridBagConstraints gbc0204 = new GridBagConstraints();
			gbc0204.gridx = 4;
			gbc0204.insets = new java.awt.Insets(V_SPAN,10,0,0);
			gbc0204.anchor = java.awt.GridBagConstraints.WEST;
			gbc0204.gridy = 2;
			cCountLabel = new CLabel();
			cCountLabel.setText(MSG_COUNT);
			GridBagConstraints gbc0203 = new GridBagConstraints();
			gbc0203.gridx = 3;
			gbc0203.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0203.anchor = java.awt.GridBagConstraints.WEST;
			gbc0203.gridy = 2;
			GridBagConstraints gbc0202 = new GridBagConstraints();
			gbc0202.gridx = 2;
			gbc0202.anchor = java.awt.GridBagConstraints.WEST;
			gbc0202.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0202.gridy = 2;
			cDiscountLabel = new CLabel();
			cDiscountLabel.setText(MSG_DISCOUNT);
			GridBagConstraints gbc0101 = new GridBagConstraints();
			gbc0101.gridx = 1;
			gbc0101.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc0101.anchor = java.awt.GridBagConstraints.WEST;
			gbc0101.gridy = 1;
			GridBagConstraints gbc0100 = new GridBagConstraints();
			gbc0100.gridx = 0;
			gbc0100.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc0100.anchor = java.awt.GridBagConstraints.WEST;
			gbc0100.gridy = 1;
			cProductPriceLabel = new CLabel();
			cProductPriceLabel.setText(MSG_PRICE);
			GridBagConstraints gbc0001 = new GridBagConstraints();
			gbc0001.gridx = 1;
			gbc0001.insets = new java.awt.Insets(0,5,5,0);
			gbc0001.gridwidth = 5;
			gbc0001.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbc0001.gridy = 0;
			cProductDescLabel = new CLabel();
			cProductDescLabel.setText(getOrderProduct().getProduct().getDescription());
			cProductDescLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			GridBagConstraints gbc0000 = new GridBagConstraints();
			gbc0000.gridx = 0;
			gbc0000.anchor = java.awt.GridBagConstraints.WEST;
			gbc0000.gridy = 0;
			gbc0000.insets = new java.awt.Insets(0,0,5,0);
			cProductLabel = new CLabel();
			cProductLabel.setText(MSG_PRODUCT + ":");
			cProductLabel.setFontBold(true);
			CLabel cPriceListLabel = new CLabel();
			cPriceListLabel.setText(MSG_PRICE_LIST);
			CLabel cManualDiscountTitleLabel = new CLabel();
			cManualDiscountTitleLabel.setText(MSG_MANUAL_DISCOUNT);
			cManualDiscountTitleLabel.setFontBold(true);
			CLabel cDiscountAmtLabel = new CLabel();
			cDiscountAmtLabel.setText(MSG_AMOUNT);
			CLabel cAuthTitleLabel = new CLabel();
			cAuthTitleLabel.setText(MSG_SUPERVISOR_AUTH);
			cAuthTitleLabel.setFontBold(true);			
						
			cItemPanel = new CPanel();
			cItemPanel.setLayout(new GridBagLayout());
			cItemPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
			cItemPanel.add(cProductLabel, gbc0000);
			cItemPanel.add(cProductDescLabel, gbc0001);
			//cItemPanel.add(cProductPriceLabel, gridBagConstraints2);
			//cItemPanel.add(getCProductPriceText(), gridBagConstraints3);
			//cItemPanel.add(cProductTaxRateLabel, gridBagConstraints12);
			//cItemPanel.add(getCProductTaxRateText(), gridBagConstraints13);
			cItemPanel.add(cProductTaxedPriceLabel, gbc0100);
			cItemPanel.add(getCProductTaxedPriceText(), gbc0101);
			cItemPanel.add(cCountLabel, gbc0102);
			cItemPanel.add(getCCountText(), gbc0103);
			cItemPanel.add(cManualDiscountTitleLabel, gbc0200);
			cItemPanel.add(cPriceListLabel, gbc0300);
			cItemPanel.add(getCPriceListText(), gbc0301);
			cItemPanel.add(getCApplicationPanel(), gbc0302);
			cItemPanel.add(cDiscountAmtLabel, gbc0402);
			cItemPanel.add(getCDiscountAmtText(), gbc0403);
			cItemPanel.add(cDiscountLabel, gbc0400);
			cItemPanel.add(getCDiscountText(), gbc0401);
			cItemPanel.add(cAuthTitleLabel, gbc0500);
			
			GridBagConstraints userAuthConstraints = new GridBagConstraints();
			userAuthConstraints.gridx = 0;
			userAuthConstraints.insets = new java.awt.Insets(V_SPAN,5,0,0);
			userAuthConstraints.anchor = java.awt.GridBagConstraints.WEST;
			userAuthConstraints.gridy = 6;
			userAuthConstraints.gridwidth = 6;
			
			// Obtener la instancia del panel de autorización y el panel
			userAuthPanel = AUserAuth.get();
			userAuthPanel.setShortcutLabel(KeyUtils.getKeyStr(getActionKeys()
					.get(CHANGE_FOCUS_USER_AUTH)));
			cItemPanel.add(userAuthPanel.getAuthPanel(), userAuthConstraints);
			
			//cItemPanel.add(cApplicationLabel, gridBagConstraints18);
			updateDiscountComponents();
			updateDiscountAmtText();
		}
		return cItemPanel;
	}

	/**
	 * This method initializes cCmdPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCmdPanel() {
		if (cCmdPanel == null) {
			cCmdPanel = new CPanel();
			cCmdPanel.setPreferredSize(new java.awt.Dimension(BUTTON_PANEL_WIDTH,36));
			cCmdPanel.add(getCOkButton(), null);
			cCmdPanel.add(getCDeleteButton(), null);
			cCmdPanel.add(getCCancelButton(), null);
		}
		return cCmdPanel;
	}

	/**
	 * This method initializes cPriceListText	
	 * 	
	 * @return org.openXpertya.grid.ed.VNumber	
	 */
	private VNumber getCPriceListText() {
		if (cPriceListText == null) {
			cPriceListText = new VNumber();
			cPriceListText.setPreferredSize(new java.awt.Dimension(100,20));
			cPriceListText.setMandatory(false);
			cPriceListText.setReadWrite(false);
			cPriceListText.setDisplayType(DisplayType.CostPrice);
			cPriceListText.setValue(getOrderProduct().getTaxedPriceList());
		}
		return cPriceListText;
	}
	
	private VNumber getCDiscountAmtText() {
		if (cDiscountAmtText == null) {
			cDiscountAmtText = new VNumber();
			cDiscountAmtText.setDisplayType(DisplayType.Amount);
			cDiscountAmtText.setPreferredSize(new java.awt.Dimension(100,20));
			cDiscountAmtText.setMandatory(false);
			cDiscountAmtText.setReadWrite(false);
		}
		return cDiscountAmtText;
	}
	
	private VNumber getCProductTaxedPriceText() {
		if (cProductTaxedPriceText == null) {
			cProductTaxedPriceText = new VNumber();
			cProductTaxedPriceText.setDisplayType(DisplayType.CostPrice);
			cProductTaxedPriceText.setPreferredSize(new java.awt.Dimension(100,20));
			cProductTaxedPriceText.setValue(getOrderProduct().getTaxedPrice());
			cProductTaxedPriceText.setMandatory(true);
			cProductTaxedPriceText.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					BigDecimal taxedPrice = (BigDecimal)cProductTaxedPriceText.getValue();
					if(taxedPrice != null) {
						OrderProduct op = getOrderProduct(); 
						// Se recalcula el descuento
						BigDecimal price = op.getPrice(taxedPrice);
						BigDecimal discount = op.scalePrice(op.calculateDiscount(price));
						
						getCDiscountText().setValue(discount); 
						updateDiscountAmtText();
					}
				}
				
			});
			FocusUtils.addFocusHighlight(cProductTaxedPriceText);
		}
		return cProductTaxedPriceText;
	}

	/**
	 * This method initializes cDiscountText	
	 * 	
	 * @return org.openXpertya.grid.ed.VNumber	
	 */
	private VNumber getCDiscountText() {
		if (cDiscountText == null) {
			cDiscountText = new VNumber();
			cDiscountText.setDisplayType(DisplayType.CostPrice);
			//cDiscountText.setPreferredSize(new java.awt.Dimension(70,20));
			cDiscountText.setPreferredSize(new java.awt.Dimension(100,20));
			cDiscountText.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					BigDecimal discount = (BigDecimal)cDiscountText.getValue();
					if(discount != null) {
						OrderProduct op = getOrderProduct(); 
						// Se recalcula el importe
						BigDecimal taxedPriceList = op.getTaxedPrice(op.getPriceList());
						BigDecimal taxedPrice = op.scalePrice(taxedPriceList.subtract(taxedPriceList.multiply(discount.divide(new BigDecimal(100),10,BigDecimal.ROUND_HALF_UP))));
						getCProductTaxedPriceText().setValue(taxedPrice);
						updateDiscountAmtText();
					}
				}
				
			});
			cDiscountText.setValue(getOrderProduct().getDiscount());
			FocusUtils.addFocusHighlight(cDiscountText);
		}
		return cDiscountText;
	}

	/**
	 * This method initializes cCountText	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCountText() {
		if (cCountText == null) {
			cCountText = new CTextField();
			cCountText.setPreferredSize(new java.awt.Dimension(50,20));
			cCountText.setText(String.valueOf(getOrderProduct().getCount()));
			cCountText.setMandatory(true);
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
					if (countStr.length() > 1 && countStr.startsWith("0")
							&& !countStr.startsWith("0.")) {
						cCountText.setText("0."
								+ countStr.substring(1, countStr.length()));
					}
					if(countStr.startsWith(".")){
						cCountText.setText("0"+countStr);
					}
					updateDiscountAmtText();
				}
				
				
			});
			cCountText.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent event) {
					cCountText.selectAll();
				}

				public void focusLost(FocusEvent event) {
					
				}
			});
			cCountText.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					updateDiscountAmtText();
				}
				
			});
			FocusUtils.addFocusHighlight(cCountText);
		}
		return cCountText;
	}

	/**
	 * This method initializes cOkButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCOkButton() {
		if (cOkButton == null) {
			cOkButton = new CButton();
			cOkButton.setIcon(getImageIcon("Ok16.gif"));
			cOkButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cOkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					updateOrderProduct();
				}
			});
			KeyUtils.setOkButtonKeys(cOkButton);
			KeyUtils.setButtonText(cOkButton, MSG_OK);
			cOkButton.setToolTipText(MSG_OK);
			FocusUtils.addFocusHighlight(cOkButton);
		}
		return cOkButton;
	}

	/**
	 * This method initializes cDeleteButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCDeleteButton() {
		if (cRemoveButton == null) {
			cRemoveButton = new CButton();
			cRemoveButton.setIcon(getImageIcon("Delete16.gif"));
			cRemoveButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cRemoveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					removeOrderProduct();
				}
			});
			
			KeyUtils.setRemoveButtonKeys(cRemoveButton);
			KeyUtils.setButtonText(cRemoveButton, MSG_DELETE);
			FocusUtils.addFocusHighlight(cRemoveButton);
		}
		return cRemoveButton;
	}

	/**
	 * This method initializes cCancelButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCCancelButton() {
		if (cCancelButton == null) {
			cCancelButton = new CButton();
			cCancelButton.setIcon(getImageIcon("Cancel16.gif"));
			cCancelButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cCancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel();
				}
			});
			
			KeyUtils.setCancelButtonKeys(cCancelButton);
			KeyUtils.setButtonText(cCancelButton, MSG_CANCEL);			
			cCancelButton.setToolTipText(MSG_CANCEL);
			FocusUtils.addFocusHighlight(cCancelButton);
		}
		return cCancelButton;
	}
	
	/**
	 * This method initializes cApplicationPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCApplicationPanel() {
		if (cApplicationPanel == null) {
			cApplicationPanel = new CPanel();
			cApplicationPanel.setLayout(new FlowLayout());
			cApplicationPanel.add(getCToPriceRadio());
			cApplicationPanel.add(getCBonusRadio());
			cApplicationPanel.setBorder(null);
			applicationGroup = new ButtonGroup();
			applicationGroup.add(getCToPriceRadio());
			applicationGroup.add(getCBonusRadio());
			if (getOrderProduct().getDiscount() != null
					&& getOrderProduct().getDiscount().compareTo(
							BigDecimal.ZERO) != 0) {
				if (getOrderProduct().getLineBonusAmt().compareTo(BigDecimal.ZERO) > 0) {
					applicationGroup.setSelected(getCBonusRadio().getModel(), true);
				} else {
					applicationGroup.setSelected(getCToPriceRadio().getModel(), true);				
				}
			}
			// Seleccionar uno por defecto
			if(applicationGroup.getSelection() == null){
				applicationGroup.setSelected(getCToPriceRadio().getModel(), true);
			}
		}
		return cApplicationPanel;
	}

	/**
	 * This method initializes cToPriceRadio	
	 * 	
	 * @return {@link JRadioButton}	
	 */
	private JRadioButton getCToPriceRadio() {
		if (cToPriceRadio == null) {
			cToPriceRadio = new JRadioButton();
			cToPriceRadio.setText(MSG_TO_PRICE);
			FocusUtils.addFocusHighlight(cToPriceRadio);
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
			String actionName = "setSelectedToPriceRadio";
			Action action = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					cToPriceRadio.setSelected(true);
					fireActionPerformed(cToPriceRadio.getActionListeners(), null);
				}
			};
			cToPriceRadio.getInputMap().put(keyStroke, actionName);
			cToPriceRadio.getActionMap().put(actionName, action);
		}
		return cToPriceRadio;
	}

	/**
	 * This method initializes cBonusRadio	
	 * 	
	 * @return {@link JRadioButton}	
	 */
	private JRadioButton getCBonusRadio() {
		if (cBonusRadio == null) {
			cBonusRadio = new JRadioButton();
			cBonusRadio.setText(MSG_BONUS);
			FocusUtils.addFocusHighlight(cBonusRadio);
			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
			String actionName = "setSelectedBonusRadio";
			Action action = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					cBonusRadio.setSelected(true);
					fireActionPerformed(cBonusRadio.getActionListeners(), null);
				}
			};
			cBonusRadio.getInputMap().put(keyStroke, actionName);
			cBonusRadio.getActionMap().put(actionName, action);
		}
		return cBonusRadio;
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
	
	private CPanel getCDiscountTitlePanel() {
		CPanel panel = new CPanel();
		panel.add(new CLabel("Descuento Manual"));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return panel;
	}

	private void updateDiscountComponents(){
		boolean isManualDiscountApplicable = getOrderProduct().getOrder()
				.isManualDiscountApplicable(getOrderProduct());
		getCBonusRadio().setEnabled(isManualDiscountApplicable);
		getCToPriceRadio().setEnabled(isManualDiscountApplicable);
		getCDiscountText().setReadWrite(isManualDiscountApplicable);
		getCProductTaxedPriceText().setReadWrite(isManualDiscountApplicable);
	}
	
	/**
	 * @return Devuelve orderProduct.
	 */
	public OrderProduct getOrderProduct() {
		return orderProduct;
	}

	/**
	 * @param orderProduct Fija o asigna orderProduct.
	 */
	public void setOrderProduct(OrderProduct orderProduct) {
		this.orderProduct = orderProduct;
	}

	/**
	 * @return Devuelve model.
	 */
	public PoSModel getModel() {
		return getPoS().getModel();
	}

	/**
	 * @return Devuelve poS.
	 */
	public PoSMainForm getPoS() {
		return poS;
	}

	/**
	 * @param poS Fija o asigna poS.
	 */
	public void setPoS(PoSMainForm poS) {
		this.poS = poS;
	}

	private boolean validateUpdate() {
		return true;
	}
	
	private void updateDiscountAmtText(){
		BigDecimal manualDiscount = (BigDecimal) (getCDiscountText().getValue() == null ? BigDecimal.ZERO
				: getCDiscountText().getValue());
		BigDecimal priceList = (BigDecimal)getCPriceListText().getValue();
		priceList = priceList == null
				|| manualDiscount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
				: priceList;
		BigDecimal price = (BigDecimal)getCProductTaxedPriceText().getValue();
		price = price == null || manualDiscount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO: price;
		getCDiscountAmtText().setValue(
				getOrderProduct().getPricesDiff(priceList, price).multiply(
						new BigDecimal((String)getCCountText().getValue())));
	}
	
	private boolean validateUserAccess() {
		// Si el TPV está configurado para permitir modificaciones de precios
		// entonces no se validan los datos de usuario.
		if (getModel().priceModifyAllowed()) {
			setUser(getModel().getCurrentUser());
			return true;
		}
		// Autorización de usuario
		// Obtengo el usuario del TPV
		Integer userID = userAuthPanel.getUserID();
		if(userID != null){
			setUser(getModel().getUser(userAuthPanel.getUserID()));
		}
		// Validar autorización al cambio de precio de línea
		UserAuthData authData = new UserAuthData();
		authData.setForPOS(true);
		List<String> operations = new ArrayList<String>();
		operations.add(UserAuthConstants.POS_MODIFY_PRICE_ORDER_PRODUCT_UID);
		authData.setAuthOperations(operations);
		authData.setPosSupervisor(getUser() == null ? false : getUser()
				.isPoSSupervisor());
		CallResult result = userAuthPanel.validateAuthorization(authData);
		if(result.isError()){
			errorMsg(result.getMsg());
		}
		
		return !result.isError();
	}
	
	private void updateOrderProduct() {
		if(!validateUserAccess())
			return;
		
		boolean error = false;
		StringBuffer errorMsg = new StringBuffer("");
		
		BigDecimal taxedPrice = (BigDecimal)getCProductTaxedPriceText().getValue();
		BigDecimal discount = (BigDecimal)getCDiscountText().getValue();
		BigDecimal count = new BigDecimal(getCCountText().getText());
	
		// Valido que el se haya ingresado un precio.
		if(taxedPrice == null) {
			error = true;
			errorMsg.append("").
					 append(MSG_NO_PRODUCT_PRICE);
		}
		// Usuario habilitado para vender por debajo del precio limite.
		BigDecimal limitPrice1 = getOrderProduct().getProduct().getLimitPrice();
		BigDecimal limitPrice = getOrderProduct().getTaxedLimitPrice();
		//BigDecimal price = getOrderProduct().getPrice(taxedPrice);
		BigDecimal price = taxedPrice;
		
		if(price != null && !getUser().isOverwriteLimitPrice() && price.compareTo(limitPrice) < 0) {
			error = true;
			errorMsg.append(" ").
					 append(MSG_INVALID_PRODUCT_PRICE).append(limitPrice.setScale(2,BigDecimal.ROUND_HALF_DOWN));
		}
		
		// El precio debe ser mayor a 0
		BigDecimal scaledprice = getOrderProduct().scalePrice(price); 
		if(scaledprice.compareTo(BigDecimal.ZERO) <= 0){
			error = true;
			errorMsg.append(" ").append(MSG_INVALID_PRICE);
		}
		
		// Descuento entre 0 y 100.
		/*
		if(discount != null && discount.compareTo(new BigDecimal(100)) > 0 || discount.compareTo(BigDecimal.ZERO) < 0) {
			error = true;
			errorMsg.append("\n").
					 append(MSG_INVALID_PRODUCT_DISCOUNT);
		}
		*/
		
		// Cantidad mayor que cero.
		BigDecimal scaledQty = getOrderProduct().scaleAmount(count);
		if(scaledQty.compareTo(BigDecimal.ZERO) <= 0) {
			error = true;
			errorMsg.append(" ").append(MSG_INVALID_COUNT);
		}
		
		// El precio final de línea (precio * cantidad) debe ser válido
		BigDecimal scaledTotalLine = getOrderProduct().scaleAmount(scaledprice.multiply(scaledQty));
		if(scaledTotalLine.compareTo(BigDecimal.ZERO) <= 0){
			error = true;
			errorMsg.append(" ").append(MSG_INVALID_FINAL_PRICE);
		}

		// Cantidad supera el máximo
		if(getPoS().getModel().countSurpassMax(count)){
			error = true;
			errorMsg.append(" ").append(MSG_SURPASS_MAX_QTY);
		}
		
		if(error) {
			errorMsg(errorMsg.toString());
		} else {

			// Al setear el descuento el producto recalcula el precio.
			getOrderProduct().setDiscount(discount, getDiscountApplication());
			getOrderProduct().setCount(count);
			
			getPoS().updateOrderProduct(getOrderProduct());
			setVisible(false);
		}
	}
	
	private void removeOrderProduct() {
		if(!validateUserAccess())
			return;
		
		if(getPoS().askMsg(MSG_CONFIRM_DELETE_RPODUCT)) {
			getPoS().removeOrderProduct(getOrderProduct());
			setVisible(false);
		}
	}

	/**
	 * @return Devuelve user.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user Fija o asigna user.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	protected PoSMsgRepository getMsgRepository() {
		return msgRepository;
	}

	protected void setMsgRepository(PoSMsgRepository msgRepository) {
		this.msgRepository = msgRepository;
	}
	
	protected String getMsg(String name) {
		return getMsgRepository().getMsg(name);
	}
	
	private void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	private void errorMsg(String msg, String subMsg) {
		getPoS().errorMsg(msg,subMsg);
		
	}
	
	private ImageIcon getImageIcon(String name) {
		return getPoS().getImageIcon(name);
	}
	
	private ButtonGroup getApplicationGroup() {
		return applicationGroup;
	}
	
	private void cancel() {
		setVisible(false);
	}
	
	private DiscountApplication getDiscountApplication() {
		if (getApplicationGroup().getSelection() == cToPriceRadio.getModel()) {
			return DiscountApplication.ToPrice;
		} else {
			return DiscountApplication.Bonus;
		}
	}

	protected void setActionKeys(Map<String,KeyStroke> actionKeys) {
		this.actionKeys = actionKeys;
	}

	protected Map<String,KeyStroke> getActionKeys() {
		return actionKeys;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
