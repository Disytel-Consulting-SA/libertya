package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.pos.model.EntidadFinancieraPlan;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.Env;

// import com.hipertehuelche.sucursales.model.LP_M_EntidadFinancieraPlan;

public class CreditCardPosnetDataDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int BUTTON_PANEL_WIDTH = 160;
	private final int BUTTON_WIDTH = 155;
	
	private final int CUSTOMER_ID_MAX_LENGTH = 11;
	
	private CPanel cMainPanel = null;
	private CPanel cCmdPanel = null;
	private CButton cClearButton = null;
	private CButton cCancelButton = null;
	private CPanel cUserInfoDataPanel;
	private CPanel cPosnetDataPanel;
	private CButton cOkButton = null;
	private CLabel cUserInfoLabel = null;
	private CLabel cCouponNumberLabel = null;
	private CLabel cBatchLabel = null;
	private CLabel cCuotasPosnetLabel = null;
	private CTextField cCouponNumberText = null;
	private CTextField cBatchText = null;
	private CTextField cCuotasPosnetText = null;
		
	private PoSMainForm poS;
	private boolean mandatoryData = false;
	private EntidadFinancieraPlan creditCardPlan;
	
	private String MSG_POSNET_DEBIT_INFO;
	private String MSG_USER_INFO;
	private String MSG_BATCH;
	private String MSG_COUPON_NUMBER;
	private String MSG_CUOTAS_POSNET;
	private String MSG_CLEAR;
	private String MSG_OK;
	private String MSG_CANCEL;
	
	// dREHER
	private boolean isOnLineMode = false;
	
	public boolean isOnLineMode() {
		return isOnLineMode;
	}

	public void setOnLineMode(boolean isOnLineMode) {
		this.isOnLineMode = isOnLineMode;
	}

	/**
	 * This method initializes 
	 * 
	 */
	public CreditCardPosnetDataDialog() {
		super();
		initialize();
	}

	public CreditCardPosnetDataDialog(PoSMainForm poS, boolean mandatoryData) {
		super();
		this.poS = poS;
		
		// Configuro modo de trabajo Clover...
		setOnLineMode(poS.isOnLineMode());
		
		this.creditCardPlan = poS.getSelectedCreditCardPlan();
		this.mandatoryData = mandatoryData;
		getPoS().setCreditCardPostnetValidated(false);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        initMsgs();
		this.setSize(new java.awt.Dimension(540,200));
        this.setResizable(false);
        this.setPreferredSize(new java.awt.Dimension(540,200));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getCMainPanel());
        this.setTitle(MSG_POSNET_DEBIT_INFO);
        commandClear();
        getCCouponNumberText().requestFocus();
	}
	
	private void initMsgs() {
		MSG_POSNET_DEBIT_INFO = getMsg("PosnetDebitInfo");
		MSG_USER_INFO = getCreditCardPlan().getUserMsg();
		MSG_COUPON_NUMBER = getMsg("CouponNumber");
		MSG_BATCH = getMsg("CouponBatchNumberShort");
		MSG_CUOTAS_POSNET = getMsg("CuotasPosnet");
		MSG_CLEAR = getMsg("Clear");
		MSG_OK = getMsg("OK");
		MSG_CANCEL = getMsg("Cancel");
	}
	
	/**
	 * This method initializes cCustomerDataPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCMainPanel() {
		if (cMainPanel == null) {
			cMainPanel = new CPanel();
			cMainPanel.setLayout(new BorderLayout());
			cMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			cMainPanel.add(getCCmdPanel(), java.awt.BorderLayout.EAST);
			cMainPanel.add(getCUserInfoDataPanel(), java.awt.BorderLayout.NORTH);
			cMainPanel.add(getCPosnetDataPanel(), java.awt.BorderLayout.CENTER);
		}
		return cMainPanel;
	}
	
	/**
	 * This method initializes cCommandPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCmdPanel() {
		if (cCmdPanel == null) {
			cCmdPanel = new CPanel();
			cCmdPanel.setPreferredSize(new java.awt.Dimension(BUTTON_PANEL_WIDTH,36));
			cCmdPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,5,0,0));
			cCmdPanel.add(getCOkButton(), null);
			cCmdPanel.add(getCClearButton(), null);
			cCmdPanel.add(getCCancelButton(), null);
		}
		return cCmdPanel;
	}

	/**
	 * This method initializes cPosnetDataPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCUserInfoDataPanel() {
		if (cUserInfoDataPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			cUserInfoLabel = new CLabel();
			cUserInfoLabel.setText(MSG_USER_INFO);
			cUserInfoDataPanel = new CPanel();
			cUserInfoDataPanel.setLayout(new GridBagLayout());
			cUserInfoDataPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(0,5,5,5)));
			cUserInfoDataPanel.add(cUserInfoLabel, gridBagConstraints);
		}
		return cUserInfoDataPanel;
	}
	
	/**
	 * This method initializes cPosnetDataPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCPosnetDataPanel() {
		if (cPosnetDataPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.insets = new java.awt.Insets(7,5,0,0);
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(7,5,0,0);
			gridBagConstraints4.gridx = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new java.awt.Insets(0,5,0,0);
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints2.gridy = 2;
			cCuotasPosnetLabel = new CLabel();
			cCuotasPosnetLabel.setText(MSG_CUOTAS_POSNET);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints1.gridy = 1;
			cBatchLabel = new CLabel();
			cBatchLabel.setText(MSG_BATCH);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 1;
			cCouponNumberLabel = new CLabel();
			cCouponNumberLabel.setText(MSG_COUPON_NUMBER);
			cPosnetDataPanel = new CPanel();
			cPosnetDataPanel.setLayout(new GridBagLayout());
			cPosnetDataPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(0,5,5,5)));
			cPosnetDataPanel.add(cCouponNumberLabel, gridBagConstraints);
			cPosnetDataPanel.add(cBatchLabel, gridBagConstraints1);
			cPosnetDataPanel.add(cCuotasPosnetLabel, gridBagConstraints2);
			cPosnetDataPanel.add(getCCouponNumberText(), gridBagConstraints3);
			cPosnetDataPanel.add(getCBatchText(), gridBagConstraints4);
			cPosnetDataPanel.add(getCCuotasPosnetText(), gridBagConstraints5);
		}
		return cPosnetDataPanel;
	}

	/**
	 * This method initializes cClearButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCClearButton() {
		if (cClearButton == null) {
			cClearButton = new CButton();
			cClearButton.setIcon(getPoS().getImageIcon("Reset16.gif"));
			cClearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					commandClear();
				}
			});
			cClearButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			KeyUtils.setButtonKey(cClearButton, true, KeyEvent.VK_F5);
			KeyUtils.setDefaultKey(cClearButton);
			KeyUtils.setButtonText(cClearButton, MSG_CLEAR);
			FocusUtils.addFocusHighlight(cClearButton);
		}
		return cClearButton;
	}

	/**
	 * This method initializes cCancelButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCCancelButton() {
		if (cCancelButton == null) {
			cCancelButton = new CButton();
			cCancelButton.setIcon(getPoS().getImageIcon("Cancel16.gif"));
			cCancelButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cCancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					commandCancel();
				}
			});
			KeyUtils.setCancelButtonKeys(cCancelButton);
			KeyUtils.setButtonText(cCancelButton, MSG_CANCEL);
			FocusUtils.addFocusHighlight(cCancelButton);
		}
		return cCancelButton;
	}

	/**
	 * This method initializes cOkButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCOkButton() {
		if (cOkButton == null) {
			cOkButton = new CButton();
			cOkButton.setText(MSG_OK);
			cOkButton.setIcon(getPoS().getImageIcon("Ok16.gif"));
			cOkButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cOkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					commandOk();
				}
			});
			KeyUtils.setOkButtonKeys(cOkButton);
			KeyUtils.setButtonText(cOkButton, MSG_OK);
			FocusUtils.addFocusHighlight(cOkButton);
		}
		return cOkButton;
	}

	/**
	 * This method initializes cNameText1	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCouponNumberText() {
		if (cCouponNumberText == null) {
			cCouponNumberText = new CTextField();
			cCouponNumberText.setText(getPoS().getCCouponNumberText().getText());
			cCouponNumberText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cCouponNumberText.setMandatory(isMandatoryData());
			FocusUtils.addFocusHighlight(cCouponNumberText);
		}
		return cCouponNumberText;
	}

	/**
	 * This method initializes cIdentificationText1	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCBatchText() {
		if (cBatchText == null) {
			cBatchText = new CTextField();
			cBatchText.setText(getPoS().getCCouponBatchNumberText().getText());
			cBatchText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cBatchText.setMandatory(isMandatoryData());
			cBatchText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char keyChar = e.getKeyChar();
					String idText = cBatchText.getText();
					
					if(idText != null && idText.length() >= CUSTOMER_ID_MAX_LENGTH){
						e.consume();
					}
					
					if(!Character.isDigit(keyChar)) {
						e.consume();
					}
				}				
			});
			FocusUtils.addFocusHighlight(cBatchText);
		}
		return cBatchText;
	}

	/**
	 * This method initializes cAddressText1	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCCuotasPosnetText() {
		if (cCuotasPosnetText == null) {
			cCuotasPosnetText = new CTextField();
			cCuotasPosnetText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cCuotasPosnetText.setMandatory(isMandatoryData());
			FocusUtils.addFocusHighlight(cCuotasPosnetText);
		}
		return cCuotasPosnetText;
	}

	/**
	 * @return Returns the poS.
	 */
	public PoSMainForm getPoS() {
		return poS;
	}
	
	protected String getMsg(String name) {
		return getPoS().getMsg(name);
	}
	
	protected BusinessPartner getBPartner() {
		return getPoS().getOrder().getBusinessPartner();
	}
	
	private void commandOk() {
		String errorFieldsMsg = "";
		String couponNumber = getCCouponNumberText().getText().trim();
		String batch = getCBatchText().getText().trim();
		String cuotasPosnet = getCCuotasPosnetText().getText().trim();
		
		couponNumber = (couponNumber.length() > 0 ? couponNumber : null);
		batch = (batch.length() > 0 ? batch : null);
		cuotasPosnet = (cuotasPosnet.length() > 0 ? cuotasPosnet : null);
		
		// Validación de datos obligatorios.
		if (isMandatoryData() && couponNumber == null)
			errorFieldsMsg += MSG_COUPON_NUMBER + ", "; 
		if (isMandatoryData() && batch == null)
			errorFieldsMsg += MSG_BATCH + ", "; 
		if (isMandatoryData() && cuotasPosnet == null)
			errorFieldsMsg += MSG_CUOTAS_POSNET;
		
		// Se borra la última coma en caso de exisitr
		errorFieldsMsg = (errorFieldsMsg.endsWith(", ")?errorFieldsMsg.substring(0, errorFieldsMsg.length()-2) : errorFieldsMsg);
		
		if (isMandatoryData() && errorFieldsMsg.length() > 0)
			errorMsg(getMsg("FillMandatory"),errorFieldsMsg);
		else if (!validateCuotas()) {
			errorMsg(getMsg("CuotasPosnetError"));
		} else {
			// Si los campos eran obligatorios y no hubo error entonces el coupon y lote se ingresaron
			// (se llenaron todos los campos), y validadas las cuotas posnet con lo paremetrizado 
			getPoS().getCCouponNumberText().setText(couponNumber);
			getPoS().getCCouponBatchNumberText().setText(batch);
			getPoS().setCreditCardPostnetValidated(true);
			closeDialog();
		}
	}
	
	private void commandCancel() {
		closeDialog();
	}
	
	/**
	 * Limpia los datos en los controles del cliente.
	 */
	private void commandClear() {
		getCCouponNumberText().setText(null);
		getCBatchText().setText(null);
		getCCuotasPosnetText().setText(null);
	}
	
	private void closeDialog() {
		dispose();
	}

	/**
	 * @return Returns the mandatoryData.
	 */
	public boolean isMandatoryData() {
		return mandatoryData;
	}
	
	private void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	private void errorMsg(String msg, String subMsg) {
		getPoS().errorMsg(msg,subMsg);
		
	}
	
	private MEntidadFinancieraPlan getCreditCardPlan() {
		return new MEntidadFinancieraPlan(Env.getCtx(), creditCardPlan.getEntidadFinancieraPlanID(), null);
	}

	private boolean validateCuotas() {
		Integer cuotasPlan = getCreditCardPlan().getCuotasPosnet();
		if (cuotasPlan <= 0) return true;
		Integer cuotasIngresadas;
		try { 
			cuotasIngresadas = Integer.valueOf(getCCuotasPosnetText().getText());
		} catch (Exception e) {
			return false; // si el datos no castea a entero, no es válido. 
		}
		return cuotasIngresadas.equals(cuotasPlan);
	}
	
}
