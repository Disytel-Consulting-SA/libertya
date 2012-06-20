package org.openXpertya.apps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.compiere.swing.CPanel;
import org.openXpertya.util.AUKUserAuthModel;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.UserAuthData;
import org.openXpertya.util.Util;

public class AUKUserAuth extends AUserAuth {

	// Mensajes
	public static final String MSG_UK = Msg.getMsg(Env.getCtx(), "UniqueKey");
	
	
	public AUKUserAuth(){
		super(new AUKUserAuthModel());
	}
	
	@Override
	public JPanel getAuthorizationPanel() {
		JPanel panel = new CPanel();
		
		// Clave única
		
		GridBagConstraints constraintFPassword = new GridBagConstraints();
		constraintFPassword.fill = java.awt.GridBagConstraints.NONE;
		constraintFPassword.gridy = 0;
		constraintFPassword.anchor = java.awt.GridBagConstraints.WEST;
		constraintFPassword.insets = new java.awt.Insets(3,5,0,0);
		constraintFPassword.gridx = 1;
		
		GridBagConstraints constraintLPassword = new GridBagConstraints();
		constraintLPassword.gridx = 0;
		constraintLPassword.insets = new java.awt.Insets(3,0,0,0);
		constraintLPassword.anchor = java.awt.GridBagConstraints.WEST;
		constraintLPassword.gridy = 0;
		
		// La longitud de la clave es el doble del width establecido por default
		Dimension stringDim = new Dimension(getPasswordDimension().width * 2,
				getPasswordDimension().height); 
		getfPassword().setPreferredSize(stringDim);
		getfPassword().setMinimumSize(stringDim);
		
		getlPassword().setText(
				Util.isEmpty(getShortcutLabel(), true) ? MSG_UK : MSG_UK + " "
						+ getShortcutLabel());
		
		panel.setLayout(new GridBagLayout());
		panel.add(getlPassword(), constraintLPassword);
		panel.add(getfPassword(), constraintFPassword);
		
		return panel;
	}

	@Override
	public UserAuthData getUserAuthData(UserAuthData userAuthData) {
		UserAuthData authData = userAuthData;
		if(authData == null){
			authData = new UserAuthData(); 
		}
		authData.setPassword(getfPassword().getText());
		return authData;
	}

	@Override
	public void setFocus() {
		getfPassword().requestFocus();
	}	

}
