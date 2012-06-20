package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.pos.ctrl.PoSConfig;

public class PoSConfigDialog extends JDialog {

	private boolean canceled = true;
	private PoSComponentFactory componentFactory;
	private PoSConfig poSConfig = null; 
	private PoSMsgRepository msgRepository;
	
	private JPanel jContentPane = null;
	private CPanel cMainPanel = null;
	private CLabel cMultipleConfigLabel = null;
	private CComboBox cPoSConfigCombo = null;
	private CPanel cCommandPanel = null;
	private CButton cOkButton = null;
	private CButton cCancelButton = null;
	private String MSG_POS_CONFIG;
	private String MSG_SELECT_POS_CONFIG;
	private String MSG_OK;
	private String MSG_CANCEL;
	/**
	 * This is the default constructor
	 */
	public PoSConfigDialog() {
		super();
		initialize();
	}

	public PoSConfigDialog(PoSComponentFactory componentFactory, PoSMsgRepository msgRepository) {
		super();
		this.componentFactory = componentFactory;
		this.msgRepository = msgRepository; 
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		initMsgs();
		this.setSize(400, 150);
		this.setResizable(false);
		this.setTitle(MSG_POS_CONFIG);
		this.setContentPane(getJContentPane());
	}
	
	private void initMsgs() {
		MSG_POS_CONFIG = getMsg("POSConfig");
		MSG_SELECT_POS_CONFIG = getMsg("SelectPOSConfig");
		MSG_OK = getMsg("OK");
		MSG_CANCEL = getMsg("Cancel");
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
			jContentPane.setPreferredSize(new java.awt.Dimension(300,110));
			jContentPane.setOpaque(false);
			jContentPane.add(getCMainPanel(), java.awt.BorderLayout.CENTER);
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
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new java.awt.Insets(10,0,0,0);
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new java.awt.Insets(0,20,0,20);
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new java.awt.Insets(0,0,10,0);
			gridBagConstraints.gridy = 0;
			cMultipleConfigLabel = new CLabel();
			cMultipleConfigLabel.setText(MSG_SELECT_POS_CONFIG);
			cMainPanel = new CPanel();
			cMainPanel.setLayout(new GridBagLayout());
			cMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
			cMainPanel.add(cMultipleConfigLabel, gridBagConstraints);
			cMainPanel.add(getCPoSConfigCombo(), gridBagConstraints1);
			cMainPanel.add(getCCommandPanel(), gridBagConstraints2);
		}
		return cMainPanel;
	}

	/**
	 * This method initializes cPoSConfigCombo	
	 * 	
	 * @return org.compiere.swing.CComboBox	
	 */
	private CComboBox getCPoSConfigCombo() {
		if (cPoSConfigCombo == null) {
			cPoSConfigCombo = getComponentFactory().createPoSConfigCombo();
			cPoSConfigCombo.setMandatory(true);
			cPoSConfigCombo.setPreferredSize(new java.awt.Dimension(31,20));
		}
		return cPoSConfigCombo;
	}

	/**
	 * This method initializes cCommandPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCommandPanel() {
		if (cCommandPanel == null) {
			cCommandPanel = new CPanel();
			cCommandPanel.add(getCOkButton(), null);
			cCommandPanel.add(getCCancelButton(), null);
		}
		return cCommandPanel;
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
			cOkButton.setPreferredSize(new java.awt.Dimension(90,26));
			cOkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setPoSConfig((PoSConfig)getCPoSConfigCombo().getSelectedItem());
					setCanceled(false);
					setVisible(false);
				}
			});
		}
		return cOkButton;
	}

	/**
	 * This method initializes cCancelButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCCancelButton() {
		if (cCancelButton == null) {
			cCancelButton = new CButton();
			cCancelButton.setText(MSG_CANCEL);
			cCancelButton.setPreferredSize(new java.awt.Dimension(90,26));
			cCancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setCanceled(true);
					setPoSConfig(null);
					setVisible(false);
				}
			});
		}
		return cCancelButton;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	private PoSComponentFactory getComponentFactory() {
		return componentFactory;
	}

	private void setComponentFactory(PoSComponentFactory componentFactory) {
		this.componentFactory = componentFactory;
	}

	public PoSConfig getPoSConfig() {
		return poSConfig;
	}

	public void setPoSConfig(PoSConfig poSConfig) {
		this.poSConfig = poSConfig;
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

}
