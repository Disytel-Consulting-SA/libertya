package org.openXpertya.apps;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.compiere.swing.CLabel;
import org.compiere.swing.CPassword;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.AUserAuthModel;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.UserAuthData;

public abstract class AUserAuth {
	
	public static final String MSG_PASSWORD = Msg.getMsg(Env.getCtx(), "Password");
	
	public static final Dimension DEFAULT_PASSWORD_DIMENSION = new java.awt.Dimension(100,20);
	
	/**
	 * @return la subclase a partir de la configuración de la compañía del
	 *         contexto
	 */
	public static AUserAuth get(){
		// Verifico si la compañía posee claves únicas activas, en ese caso
		// obtengo la clase para autorización con claves únicas, sino
		// autorización por usuario y clave
		Integer clientID = Env.getAD_Client_ID(Env.getCtx());
		MClientInfo clientInfo = MClientInfo.get(Env.getCtx(), clientID);
		AUserAuth userAuth = clientInfo.isUniqueKeyActive()?new AUKUserAuth():new AUPUserAuth();
		return userAuth;
	}

	/**
	 * @return el panel con los componentes para autorización
	 */
	public static JPanel getPanel(){
		return getPanel(null, null);
	}

	/**
	 * @param borde
	 *            borde del panel
	 * @return el panel con los componentes para autorización
	 */
	public static JPanel getPanel(Dimension passwordDimension, Border border){
		AUserAuth userAuth = get();
		Dimension dim = new java.awt.Dimension(100,20);
		if(border != null){
			userAuth.setPanelBorder(border);	
		}
		if(passwordDimension != null){
			dim = passwordDimension;
		}
		userAuth.setPasswordDimension(dim);
		return userAuth.getAuthPanel();
	}
	
	/** Panel de autorización */
	private JPanel authPanel;
	
	/** Componente para clave */
	private JTextField fPassword;
	private JLabel lPassword;
	
	/** Borde del panel */
	private Border panelBorder;
	
	/** Shortcut para el label cuando tiene el foco el panel de autorización */
	private String shortcutLabel;
	
	/** Dimensión del texto del password */
	private Dimension passwordDimension;
	
	/** Modelo */
	private AUserAuthModel userAuthModel;
	
	
	public AUserAuth(AUserAuthModel userAuthModel){
		setUserAuthModel(userAuthModel);
	}
	
	// Métodos abstractos

	/**
	 * Valida la autorización del usuario a partir de los datos de autorización
	 * del usuario
	 * 
	 * @param userAuthData
	 *            datos de usuario necesarios para verificar si está autorizado
	 *            para realizar las operaciones correspondientes
	 * @return resultado de la llamada
	 */
	public CallResult validateAuthorization(UserAuthData userAuthData){
		return getUserAuthModel().validateAuthorization(getUserAuthData(userAuthData));
	}
	
	/**
	 * @return id de usuario 
	 */
	public Integer getUserID(){
		return getUserAuthModel().getUserID(getUserAuthData(null));
	}
	
	/**
	 * Limpia los componentes de usuario y password
	 */
	public void clear(){
		getfPassword().setText(null);
	}
	
	/**
	 * @return el panel de autorización de usuario dependiendo cada subclase
	 */	
	public abstract JPanel getAuthorizationPanel();
	
	/**
	 * @param userAuthData
	 *            datos de usuario necesarios para verificar si está autorizado
	 *            para realizar las operaciones correspondientes
	 * @return datos de autorización de usuario útiles
	 */
	public abstract UserAuthData getUserAuthData(UserAuthData userAuthData);
	
	/**
	 * Setea el foco al componente adecuado
	 */
	public abstract void setFocus();
	
	// Getters y Setters
	
	public void setAuthPanel(JPanel authPanel) {
		this.authPanel = authPanel;
	}

	public JPanel getAuthPanel() {
		if(authPanel == null){
			authPanel = getAuthorizationPanel();
			if(getPanelBorder() != null){
				authPanel.setBorder(getPanelBorder());
			}
		}
		return authPanel;
	}

	protected void setfPassword(JTextField fPassword) {
		this.fPassword = fPassword;
	}

	protected JTextField getfPassword() {
		if(fPassword == null){
			fPassword = new CPassword();			
			fPassword.setPreferredSize(getPasswordDimension());
			fPassword.setMinimumSize(getPasswordDimension());
			FocusUtils.addFocusHighlight(fPassword);
		}
		return fPassword;
	}

	protected void setlPassword(JLabel lPassword) {
		this.lPassword = lPassword;
	}

	protected JLabel getlPassword() {
		if(lPassword == null){
			lPassword = new CLabel(MSG_PASSWORD);
		}
		return lPassword;
	}

	protected void setPanelBorder(Border panelBorder) {
		this.panelBorder = panelBorder;
	}

	protected Border getPanelBorder() {
		return panelBorder;
	}

	protected void setPasswordDimension(Dimension passwordDimension) {
		this.passwordDimension = passwordDimension;
	}

	protected Dimension getPasswordDimension() {
		if(passwordDimension == null){
			passwordDimension = DEFAULT_PASSWORD_DIMENSION;
		}
		return passwordDimension;
	}

	protected void setUserAuthModel(AUserAuthModel userAuthModel) {
		this.userAuthModel = userAuthModel;
	}

	protected AUserAuthModel getUserAuthModel() {
		return userAuthModel;
	}

	public void setShortcutLabel(String shortcutLabel) {
		this.shortcutLabel = shortcutLabel;
	}

	public String getShortcutLabel() {
		return shortcutLabel;
	}
}
