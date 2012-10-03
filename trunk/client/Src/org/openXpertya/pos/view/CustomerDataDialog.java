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
import org.openXpertya.pos.model.BusinessPartner;
import org.openXpertya.swing.util.FocusUtils;

public class CustomerDataDialog extends JDialog {

	private final int BUTTON_PANEL_WIDTH = 160;
	private final int BUTTON_WIDTH = 155;
	
	private final int CUSTOMER_ID_MAX_LENGTH = 11;
	
	private CPanel cMainPanel = null;
	private CPanel cCmdPanel = null;
	private CButton cClearButton = null;
	private CButton cCancelButton = null;
	private CPanel cCustomerDataPanel;
	private CButton cOkButton = null;
	private CLabel cNameLabel = null;
	private CLabel cIdentificationLabel = null;
	private CLabel cAddressLabel = null;
	private CTextField cNameText = null;
	private CTextField cIdentificationText = null;
	private CTextField cAddressText = null;

	private PoSMainForm poS;
	private boolean mandatoryData = false; 
	
	private String MSG_CUSTOMER_IDENTIFICATION;
	private String MSG_ADDRESS;
	private String MSG_NAME;
	private String MSG_CUSTOMER_IDENTIFICATION_NUMBER;
	private String MSG_CLEAR;
	private String MSG_OK;
	private String MSG_CANCEL;
	
	
	/**
	 * This method initializes 
	 * 
	 */
	public CustomerDataDialog() {
		super();
		initialize();
	}

	public CustomerDataDialog(PoSMainForm poS, boolean mandatoryData) {
		super();
		this.poS = poS;
		this.mandatoryData = mandatoryData;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        initMsgs();
		this.setSize(new java.awt.Dimension(540,148));
        this.setResizable(false);
        this.setPreferredSize(new java.awt.Dimension(540,148));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setContentPane(getCMainPanel());
        this.setTitle(MSG_CUSTOMER_IDENTIFICATION);
        setComponentsEnabled();
	}
	
	private void initMsgs() {
		MSG_CUSTOMER_IDENTIFICATION = getMsg("CustomerIdentification");
		MSG_ADDRESS = getMsg("Address");
		MSG_NAME = getMsg("Name");
		MSG_CUSTOMER_IDENTIFICATION_NUMBER = getMsg("CustomerIdentificationNumber");
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
			cMainPanel.add(getCCustomerDataPanel(), java.awt.BorderLayout.CENTER);
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
	 * This method initializes cCustomerDataPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCustomerDataPanel() {
		if (cCustomerDataPanel == null) {
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
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new java.awt.Insets(0,5,0,0);
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints2.gridy = 2;
			cAddressLabel = new CLabel();
			cAddressLabel.setText(MSG_ADDRESS);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.insets = new java.awt.Insets(7,0,0,0);
			gridBagConstraints1.gridy = 1;
			cIdentificationLabel = new CLabel();
			cIdentificationLabel.setText(MSG_CUSTOMER_IDENTIFICATION_NUMBER);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			cNameLabel = new CLabel();
			cNameLabel.setText(MSG_NAME);
			cCustomerDataPanel = new CPanel();
			cCustomerDataPanel.setLayout(new GridBagLayout());
			cCustomerDataPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(0,5,5,5)));
			cCustomerDataPanel.add(cNameLabel, gridBagConstraints);
			cCustomerDataPanel.add(cIdentificationLabel, gridBagConstraints1);
			cCustomerDataPanel.add(cAddressLabel, gridBagConstraints2);
			cCustomerDataPanel.add(getCNameText(), gridBagConstraints3);
			cCustomerDataPanel.add(getCIdentificationText(), gridBagConstraints4);
			cCustomerDataPanel.add(getCAddressText(), gridBagConstraints5);
		}
		return cCustomerDataPanel;
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
	private CTextField getCNameText() {
		if (cNameText == null) {
			cNameText = new CTextField();
			cNameText.setText(getBPartner().getCustomerName());
			cNameText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cNameText.setMandatory(isMandatoryData());
			FocusUtils.addFocusHighlight(cNameText);
		}
		return cNameText;
	}

	/**
	 * This method initializes cIdentificationText1	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCIdentificationText() {
		if (cIdentificationText == null) {
			cIdentificationText = new CTextField();
			cIdentificationText.setText(getBPartner().getCustomerIdentification());
			cIdentificationText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cIdentificationText.setMandatory(isMandatoryData());
			cIdentificationText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char keyChar = e.getKeyChar();
					String idText = cIdentificationText.getText();
					
					if(idText != null && idText.length() >= CUSTOMER_ID_MAX_LENGTH){
						e.consume();
					}
					
					if(!Character.isDigit(keyChar)) {
						e.consume();
					}
				}				
			});
			FocusUtils.addFocusHighlight(cIdentificationText);
		}
		return cIdentificationText;
	}

	/**
	 * This method initializes cAddressText1	
	 * 	
	 * @return org.compiere.swing.CTextField	
	 */
	private CTextField getCAddressText() {
		if (cAddressText == null) {
			cAddressText = new CTextField();
			cAddressText.setText(getBPartner().getCustomerAddress());
			cAddressText.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			cAddressText.setMandatory(isMandatoryData());
			FocusUtils.addFocusHighlight(cAddressText);
		}
		return cAddressText;
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
		String name = getCNameText().getText().trim();
		String identification = getCIdentificationText().getText().trim();
		String address = getCAddressText().getText().trim();
		boolean identified = false;
		
		name = (name.length() > 0 ? name : null);
		identification = (identification.length() > 0 ? identification : null);
		address = (address.length() > 0 ? address : null);
		
		// Validación de datos obligatorios.
		if (isMandatoryData() && name == null)
			errorFieldsMsg += MSG_NAME + ", "; 
		if (isMandatoryData() && identification == null)
			errorFieldsMsg += MSG_CUSTOMER_IDENTIFICATION_NUMBER + ", "; 
		if (isMandatoryData() && address == null)
			errorFieldsMsg += MSG_ADDRESS;
		
		// Se borra la última coma en caso de exisitr
		errorFieldsMsg = (errorFieldsMsg.endsWith(", ")?errorFieldsMsg.substring(0, errorFieldsMsg.length()-2) : errorFieldsMsg);
		
		identified = name != null || identification != null || address != null;
		
		if (isMandatoryData() && errorFieldsMsg.length() > 0)
			errorMsg(getMsg("FillMandatory"),errorFieldsMsg);
		else {
			// Si los campos eran obligatorios y no hubo error entonces el comprador
			// está identificado (se llenaron todos los campos).
			identified = identified || isMandatoryData();
			getBPartner().setCustomerName(name);
			getBPartner().setCustomerIdentification(identification);
			getBPartner().setCustomerAddress(address);
			getBPartner().setCustomerIdentified(identified);
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
		getCNameText().setText(null);
		getCIdentificationText().setText(null);
		getCAddressText().setText(null);
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
	
	private void setComponentsEnabled() {
		getCClearButton().setEnabled(!getBPartner().isCustomerSynchronized());
		//getCCancelButton().setEnabled(!getBPartner().isCustomerSynchronized());
		getCAddressText().setEditable(true);
		getCIdentificationText().setEditable(!getBPartner().isCustomerSynchronized());
		getCNameText().setEditable(!getBPartner().isCustomerSynchronized());
	}
	

}  //  @jve:decl-index=0:visual-constraint="10,10"
