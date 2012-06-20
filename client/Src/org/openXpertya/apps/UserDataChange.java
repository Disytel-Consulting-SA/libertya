package org.openXpertya.apps;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CPassword;
import org.openXpertya.images.ImageFactory;
import org.openXpertya.model.MUser;
import org.openXpertya.pos.view.KeyUtils;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;


public class UserDataChange extends JDialog {

	private final int BUTTON_PANEL_WIDTH = 160;
	private final int BUTTON_WIDTH = 140;
	private final int FIELD_WIDTH = 200; 
	
	private JPanel jContentPane = null;
	private CPanel cMainPanel = null;
	private CPanel cDescriptionPanel = null;
	private CPanel cDataPanel = null;
	private CPanel cCmdPanel = null;
	private CLabel cDescriptionLabel = null;
	private CLabel cCurrentPasswordLabel = null;
	private CPassword cCurrentPasswordText = null;
	private CLabel cNewPasswordLabel = null;
	private CPassword cNewPasswordText = null;
	private CLabel cRepeatPasswordLabel = null;
	private CButton cOkButton = null;
	private CButton cCancelButton = null;
	private CPassword cRepeatPasswordText = null;
	private String description = null;

	private Properties ctx = Env.getCtx();
	
	private List<UserDataChangeListener> userDataListeners;
	
	/**
	 * This is the default constructor
	 */
	public UserDataChange() {
		this(null);
	}
	
	public UserDataChange(String description) {
		super();
		setDescription(description);
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle(getMsg("ChangeUserPassword"));
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setUserDataListeners(new ArrayList<UserDataChangeListener>());
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
			jContentPane.setPreferredSize(new java.awt.Dimension(520,140));
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
			if(!Util.isEmpty(getDescription(), true)){
				cMainPanel.add(getCDescriptionPanel(), java.awt.BorderLayout.NORTH);
			}
			cMainPanel.add(getCDataPanel(), java.awt.BorderLayout.CENTER);
			cMainPanel.add(getCCmdPanel(), java.awt.BorderLayout.EAST);
		}
		return cMainPanel;
	}

	private CPanel getCDescriptionPanel(){
		if(cDescriptionPanel == null){
			cDescriptionPanel = new CPanel();
			cDescriptionLabel = new CLabel(getDescription());
			cDescriptionPanel.add(cDescriptionLabel);
		}
		return cDescriptionPanel;
	}
	
	/**
	 * This method initializes cDataPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCDataPanel() {
		if (cDataPanel == null) {
			final int V_SPAN = 3;

			cCurrentPasswordLabel = new CLabel();
			cCurrentPasswordLabel.setText(getMsg("CurrentPassword"));
			GridBagConstraints gbc02 = new GridBagConstraints();
			gbc02.gridx = 1;
			gbc02.insets = new java.awt.Insets(0,5,10,0);
			gbc02.gridy = 0;
			gbc02.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gbc01 = new GridBagConstraints();
			gbc01.gridx = 0;
			gbc01.anchor = java.awt.GridBagConstraints.WEST;
			gbc01.gridy = 0;
			gbc01.insets = new java.awt.Insets(0,0,10,0);
			
			cNewPasswordLabel = new CLabel();
			cNewPasswordLabel.setText(getMsg("NewPassword"));
			GridBagConstraints gbc04 = new GridBagConstraints();
			gbc04.fill = GridBagConstraints.HORIZONTAL;
			gbc04.gridy = 1;
			gbc04.anchor = java.awt.GridBagConstraints.WEST;
			gbc04.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc04.gridx = 1;
			GridBagConstraints gbc03 = new GridBagConstraints();
			gbc03.gridx = 0;
			gbc03.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc03.anchor = java.awt.GridBagConstraints.WEST;
			gbc03.gridy = 1;

			cRepeatPasswordLabel = new CLabel();
			cRepeatPasswordLabel.setText(getMsg("RepeatPassword"));
			GridBagConstraints gbc06 = new GridBagConstraints();
			gbc06.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbc06.gridy = 2;
			gbc06.anchor = java.awt.GridBagConstraints.WEST;
			gbc06.insets = new java.awt.Insets(V_SPAN,5,0,0);
			gbc06.gridx = 1;
			GridBagConstraints gbc05 = new GridBagConstraints();
			gbc05.gridx = 0;
			gbc05.insets = new java.awt.Insets(V_SPAN,0,0,0);
			gbc05.anchor = java.awt.GridBagConstraints.WEST;
			gbc05.gridy = 2;
			
			cDataPanel = new CPanel();
			cDataPanel.setLayout(new GridBagLayout());
			cDataPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
			// Contraseña Actual
			cDataPanel.add(cCurrentPasswordLabel, gbc01);
			cDataPanel.add(getCCurrentPasswordText(), gbc02);
			// Contraeña Nueva
			cDataPanel.add(cNewPasswordLabel, gbc03);
			cDataPanel.add(getCNewPasswordText(), gbc04);
			// Repetir Contraseña
			cDataPanel.add(cRepeatPasswordLabel, gbc05);
			cDataPanel.add(getCRepeatPasswordText(), gbc06);
		
		}
		return cDataPanel;
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
			cCmdPanel.add(getCCancelButton(), null);
		}
		return cCmdPanel;
	}

	private CPassword getCCurrentPasswordText() {
		if (cCurrentPasswordText == null) {
			cCurrentPasswordText = new CPassword();
			cCurrentPasswordText.setPreferredSize(new java.awt.Dimension(FIELD_WIDTH,20));
		}
		return cCurrentPasswordText;
	}

	/**
	 * This method initializes cNewPasswordText	
	 * 	
	 * @return org.compiere.swing.CPassword	
	 */
	private CPassword getCNewPasswordText() {
		if (cNewPasswordText == null) {
			cNewPasswordText = new CPassword();
			cNewPasswordText.setPreferredSize(new java.awt.Dimension(FIELD_WIDTH,20));
		}
		return cNewPasswordText;
	}

	/**
	 * This method initializes cOkButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCOkButton() {
		if (cOkButton == null) {
			cOkButton = new CButton();
			cOkButton.setIcon(getImageIcon("Save16.gif"));
			cOkButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cOkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					changePassword();
				}
			});
			cOkButton.setText(getMsg("SaveShort"));
			KeyUtils.setButtonKey(cOkButton, true, KeyEvent.VK_ENTER);
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
			cCancelButton.setIcon(getImageIcon("Cancel16.gif"));
			cCancelButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cCancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel();
				}
			});
			cCancelButton.setText(getMsg("Cancel"));
			KeyUtils.setButtonKey(cCancelButton, true, KeyEvent.VK_ESCAPE);
		}
		return cCancelButton;
	}

	/**
	 * This method initializes cRepeatPasswordText	
	 * 	
	 * @return org.compiere.swing.CPassword	
	 */
	private CPassword getCRepeatPasswordText() {
		if (cRepeatPasswordText == null) {
			cRepeatPasswordText = new CPassword();
			cRepeatPasswordText.setPreferredSize(new java.awt.Dimension(FIELD_WIDTH,20));
		}
		return cRepeatPasswordText;
	}

	private void changePassword() {
		String currentPassword = getCCurrentPasswordText().getDisplay();
		String newPassword = getCNewPasswordText().getDisplay();
		String repeatPassword = getCRepeatPasswordText().getDisplay();
		
		if (currentPassword == null || currentPassword.trim().length() == 0) {
			errorMsg("CurrentPasswordBlankError");
			return;
		} else if(newPassword == null || newPassword.trim().length() == 0) {
			errorMsg("NewPasswordBlankError");
			return;
		} else if(repeatPassword == null || repeatPassword.trim().length() == 0) {
			errorMsg("RepeatPasswordBlankError");
			return;
		} else if (!newPassword.equals(repeatPassword)) {
			errorMsg("NewPasswordNotMatchError");
			return;
		} else if (currentPassword.equals(newPassword)) {
			errorMsg("EqualCurrentAndNewPassword");
			return;
		}

		String currentUserPassword = MUser.getCurrentPassword(ctx, Env.getAD_User_ID(ctx), null);
		if (!currentPassword.equals(currentUserPassword)) {
			errorMsg("InvalidCurrentPasswordError");
			return;
		}
		
		MUser.clearCache();
		MUser currentUser = MUser.get(ctx);
		currentUser.setPassword(newPassword);
		if (!currentUser.save()) {
			errorMsg("UserDataSaveError", CLogger.retrieveErrorAsString());
		} else {
			ADialog.info(0, this, "PasswordChangedSuccessful");
			setVisible(false);
			fireUserDataListener(new UserDataChangeEvent(this, true));
		}
	}
	
	protected String getMsg(String name) {
		return Msg.translate(Env.getCtx(), name);
	}
	
	private void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	private void errorMsg(String msg, String subMsg) {
		ADialog.error(0, this, msg, subMsg);
	}
	
	private ImageIcon getImageIcon(String name) {
		return ImageFactory.getImageIcon(name);
	}
	
	private void cancel() {
		setVisible(false);
		fireUserDataListener(new UserDataChangeEvent(this, false));
	}

	private void setUserDataListeners(List<UserDataChangeListener> userDataListeners) {
		this.userDataListeners = userDataListeners;
	}

	private List<UserDataChangeListener> getUserDataListeners() {
		return userDataListeners;
	}
	

	public void addUserDataListener(UserDataChangeListener userDataListener){
		getUserDataListeners().add(userDataListener);
	}
	
	private void fireUserDataListener(UserDataChangeEvent event){
		for (UserDataChangeListener userDataListener : getUserDataListeners()) {
			userDataListener.userDataChanged(event);
		}
	}

	private void setDescription(String description) {
		this.description = description;
	}

	private String getDescription() {
		return description;
	}
}

