package org.openXpertya.apps;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.AUPUserAuthModel;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.UserAuthData;
import org.openXpertya.util.Util;

public class AUPUserAuth extends AUserAuth {

	// Mensajes
	public static final String MSG_USER = Msg.getMsg(Env.getCtx(), "POSUser");
	
	
	/** Componentes para nombre de usuario */
	private JTextField fUserName;
	private JLabel lUserName;
	
	
	public AUPUserAuth(){
		super(new AUPUserAuthModel());
	}
	
	@Override
	public JPanel getAuthorizationPanel() {
		JPanel panel = new CPanel();
		
		// Nombre Usuario
		
		GridBagConstraints constraintFUserName = new GridBagConstraints();
		constraintFUserName.fill = java.awt.GridBagConstraints.NONE;
		constraintFUserName.gridy = 0;
		constraintFUserName.anchor = java.awt.GridBagConstraints.WEST;
		constraintFUserName.insets = new java.awt.Insets(3,5,0,0);
		constraintFUserName.gridx = 1;
		
		GridBagConstraints constraintLUserName = new GridBagConstraints();
		constraintLUserName.gridx = 0;
		constraintLUserName.insets = new java.awt.Insets(3,0,0,0);
		constraintLUserName.anchor = java.awt.GridBagConstraints.WEST;
		constraintLUserName.gridy = 0;
		
		// Password 
		
		GridBagConstraints constraintFPassword = new GridBagConstraints();
		constraintFPassword.gridx = 3;
		constraintFPassword.insets = new java.awt.Insets(3,5,0,0);
		constraintFPassword.anchor = java.awt.GridBagConstraints.WEST;
		constraintFPassword.gridy = 0;
		constraintFPassword.gridwidth = 2;
		
		GridBagConstraints constraintLPassword = new GridBagConstraints();
		constraintLPassword.gridx = 2;
		constraintLPassword.anchor = java.awt.GridBagConstraints.WEST;
		constraintLPassword.insets = new java.awt.Insets(3,14,0,0);
		constraintLPassword.gridy = 0;
		
		panel.setLayout(new GridBagLayout());
		panel.add(getlPassword(), constraintLPassword);
		panel.add(getfPassword(), constraintFPassword);
		panel.add(getlUserName(), constraintLUserName);
		panel.add(getfUserName(), constraintFUserName);
		
		return panel;
	}	
	
	@Override
	public UserAuthData getUserAuthData(UserAuthData userAuthData) {
		UserAuthData authData = userAuthData;
		if(authData == null){
			authData = new UserAuthData(); 
		}
		authData.setPassword(getfPassword().getText());
		authData.setUserName(getfUserName().getText());
		return authData;
	}
	
	protected void setfUserName(JTextField fUserName) {
		this.fUserName = fUserName;
	}

	protected JTextField getfUserName() {
		if(fUserName == null){
			fUserName = new CTextField();
			fUserName.setPreferredSize(getPasswordDimension());
			fUserName.setMinimumSize(getPasswordDimension());
			FocusUtils.addFocusHighlight(fUserName);
		}
		return fUserName;
	}

	protected void setlUserName(JLabel lUserName) {
		this.lUserName = lUserName;
	}

	protected JLabel getlUserName() {
		if(lUserName == null){
			lUserName = new CLabel(
					Util.isEmpty(getShortcutLabel(), true) ? MSG_USER
							: MSG_USER + " " + getShortcutLabel());
		}
		return lUserName;
	}

	@Override
	public void setFocus() {
		getfUserName().requestFocus();
	}
	
	@Override
	public void clear(){
		super.clear();
		getfUserName().setText(null);
	}
}
