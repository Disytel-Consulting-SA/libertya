package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.compiere.swing.CButton;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextPane;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AUserAuth;
import org.openXpertya.images.ImageFactory;
import org.openXpertya.pos.model.AuthManager;
import org.openXpertya.pos.model.AuthOperation;
import org.openXpertya.pos.model.User;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.UserAuthData;

public class AuthorizationDialog extends CDialog {

	private final int BUTTON_PANEL_WIDTH = 160;
	private final int BUTTON_WIDTH = 155;
	
	private String MSG_OK;
	private String MSG_CANCEL;
	
	/** Manager de autorización */
	private AuthManager authManager;
	
	/** Formulario del TPV para tener relacionado */
	private PoSMainForm posMainForm;
	
	/** Autorización */
	private AUserAuth userAuth;
	
	/** Paneles necesarios */
	private CPanel cMainPanel;
	private CPanel cCmdPanel;
	private CTextPane cOperationsPane;
	
	/** Botones */
	private CButton cOkButton;
	private CButton cCancelButton;
	
	/** Descripción de las operaciones a autorizar */
	private String operationsDescription;
	
	/** Resultado de la validación */
	private CallResult authorizeResult;
	
	/** Momento y autorización actual */
	private String currentAuthorizationMoment;
	
	/** Nombres de operaciones actuales a autorizar */
	private List<String> currentAuthorizationOperations;
	
	/** Operaciones actuales a autorizar */
	private List<AuthOperation> currentAuthOperations;
	
	public AuthorizationDialog(PoSMainForm posMainForm) {
		setAuthManager(new AuthManager());
		setPosMainForm(posMainForm);
		initMsgs();
	}
	
	private void initMsgs(){
		MSG_OK = getPosMainForm().getMsg("OK");
		MSG_CANCEL = getPosMainForm().getMsg("Cancel");
	}
	
	private void initComponents(){
		initialize();
		setContentPane(getCMainPanel());
		pack();
	}
	
	private void initialize(){
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		reset();
	}
	
	public void reset(){
		cMainPanel = null;
		cOperationsPane = null;
		cCmdPanel = null;
		cOkButton = null;
		cCancelButton = null;
		setUserAuth(AUserAuth.get());
	}
	
	public void addAuthOperation(AuthOperation authOperation){
		getAuthManager().addAuthOperation(authOperation);
	}
	
	public void removeAuthOperation(AuthOperation authOperation){
		getAuthManager().removeAuthOperation(authOperation);
	}
	
	public boolean authorizeOperation(String authorizationMoment){
		Set<String> operationsToAuth = new HashSet<String>();
		List<AuthOperation> authOperations = new ArrayList<AuthOperation>(); 
		setCurrentAuthorizationMoment(authorizationMoment);
		boolean authorized = true;
		HTMLMsg listOperations = new HTMLMsg();
		HTMLMsg.HTMLList authList = listOperations.createList("authorize",
				"ul", "Las siguientes operaciones requieren autorizacion:");
		for (AuthOperation authOperation : getAuthManager().getOperations(authorizationMoment)) {
			if(!authOperation.isAuthorized()){
				listOperations.createAndAddListElement("", authOperation.getOpDescription(), authList);
				operationsToAuth.add(authOperation.getOperationType());
				authOperations.add(authOperation);
			}
		}
		if(authList.getElements().size() > 0){
			listOperations.addList(authList);
			setCurrentAuthorizationOperations(new ArrayList<String>(operationsToAuth));
			setCurrentAuthOperations(authOperations);
			setOperationsDescription(listOperations.toString());
			initComponents();
			setFocus();
			AEnv.positionCenterScreen(this);
			setModal(true);
			setVisible(true);
		}
		else{
			setOperationsDescription(null);
			setCurrentAuthorizationMoment(null);
			setCurrentAuthorizationOperations(null);
			setCurrentAuthOperations(null);
		}
		return authorized;
	}
	
	/**
	 * This method initializes cMainPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CTextPane getCOperationsPane() {
		if (cOperationsPane == null) {
			cOperationsPane = new CTextPane();
			cOperationsPane.setMinimumSize(new Dimension(getUserAuth()
					.getAuthPanel().getPreferredSize().width
					+ getCCmdPanel().getPreferredSize().width, getUserAuth()
					.getAuthPanel().getPreferredSize().height
					+ getCCmdPanel().getPreferredSize().height));
			cOperationsPane.setOpaque(false);
			cOperationsPane.setText(getOperationsDescription());
		}
		return cOperationsPane;
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
			cMainPanel.add(getCOperationsPane(), java.awt.BorderLayout.NORTH);
			cMainPanel.add(getUserAuth().getAuthPanel(), java.awt.BorderLayout.CENTER);
			cMainPanel.add(getCCmdPanel(), java.awt.BorderLayout.EAST);
		}
		return cMainPanel;
	}
	
	/**
	 * This method initializes cCmdPanel	
	 * 	
	 * @return org.compiere.swing.CPanel	
	 */
	private CPanel getCCmdPanel() {
		if (cCmdPanel == null) {
			cCmdPanel = new CPanel();
			cCmdPanel.setPreferredSize(new java.awt.Dimension(BUTTON_PANEL_WIDTH,65));
			cCmdPanel.add(getCOkButton(), null);
			cCmdPanel.add(getCCancelButton(), null);
		}
		return cCmdPanel;
	}
	
	/**
	 * This method initializes cOkButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCOkButton() {
		if (cOkButton == null) {
			cOkButton = new CButton();
			cOkButton.setIcon(ImageFactory.getImageIcon("Ok16.gif"));
			cOkButton.setPreferredSize(new java.awt.Dimension(BUTTON_WIDTH,26));
			cOkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Autorización de usuario
					Integer userID = getUserAuth().getUserID();
					User user = null;
					if(userID != null){
						user = getPosMainForm().getUser(userID);
					}
					UserAuthData authData = new UserAuthData();
					authData.setForPOS(getPosMainForm() != null);
					authData.setAuthOperations(getCurrentAuthorizationOperations());
					authData.setPosSupervisor(user == null? false : user.isPoSSupervisor());
					setAuthorizeResult(getUserAuth().validateAuthorization(authData));
					if(getAuthorizeResult() != null && !getAuthorizeResult().isError()){
						markAuthorized(getCurrentAuthorizationMoment(), true);
						setVisible(false);
					}
					else{
						if(getAuthorizeResult() != null && getAuthorizeResult().isError()){
							errorMsg(getAuthorizeResult().getMsg());
						}
					}
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
	 * This method initializes cCancelButton	
	 * 	
	 * @return org.compiere.swing.CButton	
	 */
	private CButton getCCancelButton() {
		if (cCancelButton == null) {
			cCancelButton = new CButton();
			cCancelButton.setIcon(ImageFactory.getImageIcon("Cancel16.gif"));
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

	private void cancel() {
		setAuthorizeResult(null);
		cancelCurrentAuthorizationOperation();
		setVisible(false);
	}
	
	private void markAuthorized(String authorizationMoment, boolean authorized){
		for (AuthOperation authOperation : getAuthManager().getOperations(authorizationMoment)) {
			authOperation.setAuthorized(authorized);
		}
	}
	
	private void cancelCurrentAuthorizationOperation(){
		for (AuthOperation authOp : getCurrentAuthOperations()) {
			removeAuthOperation(authOp);
		}
	}
	
	public void setFocus(){
		getUserAuth().setFocus();
	}
	
	protected void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	protected void errorMsg(String msg, String subMsg) {
		ADialog.error(0,this,msg,subMsg);
	}
	
	public CallResult getAuthorizeResult(boolean reset) {
		CallResult result = authorizeResult;
		if(reset){
			authorizeResult = null; 
		}
		return result;
	}
	
	public void setAuthManager(AuthManager authManager) {
		this.authManager = authManager;
	}

	public AuthManager getAuthManager() {
		return authManager;
	}

	protected void setUserAuth(AUserAuth userAuth) {
		this.userAuth = userAuth;
	}

	protected AUserAuth getUserAuth() {
		return userAuth;
	}

	public void setPosMainForm(PoSMainForm posMainForm) {
		this.posMainForm = posMainForm;
	}

	public PoSMainForm getPosMainForm() {
		return posMainForm;
	}

	public void setAuthorizeResult(CallResult authorizeResult) {
		this.authorizeResult = authorizeResult;
	}

	public CallResult getAuthorizeResult() {
		return authorizeResult;
	}

	protected void setOperationsDescription(String operationsDescription) {
		this.operationsDescription = operationsDescription;
	}

	protected String getOperationsDescription() {
		return operationsDescription;
	}

	public void setCurrentAuthorizationMoment(String currentAuthorizationMoment) {
		this.currentAuthorizationMoment = currentAuthorizationMoment;
	}

	public String getCurrentAuthorizationMoment() {
		return currentAuthorizationMoment;
	}

	
	public void setCurrentAuthorizationOperations(
			List<String> currentAuthorizationOperations) {
		this.currentAuthorizationOperations = currentAuthorizationOperations;
	}

	public List<String> getCurrentAuthorizationOperations() {
		return currentAuthorizationOperations;
	}

	protected void setCurrentAuthOperations(List<AuthOperation> currentAuthOperations) {
		this.currentAuthOperations = currentAuthOperations;
	}

	protected List<AuthOperation> getCurrentAuthOperations() {
		return currentAuthOperations;
	}
}
