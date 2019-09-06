package org.openXpertya.pos.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import org.openXpertya.apps.AuthContainer;
import org.openXpertya.images.ImageFactory;
import org.openXpertya.pos.model.AuthManager;
import org.openXpertya.pos.model.AuthOperation;
import org.openXpertya.pos.model.User;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.swing.util.FocusUtils;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.UserAuthData;
import org.openXpertya.util.UserAuthorizationOperation;

public class AuthorizationDialog extends CDialog {

	private final int BUTTON_PANEL_WIDTH = 160;
	private final int BUTTON_WIDTH = 155;
	
	private String MSG_OK;
	private String MSG_CANCEL;
	
	/** Manager de autorización */
	private AuthManager authManager;
	
	/** Contenedor de la autoización */
	private AuthContainer authContainer;
	
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
	private List<UserAuthorizationOperation> currentAuthorizationOperations;
	
	/** Operaciones actuales a autorizar */
	private List<AuthOperation> currentAuthOperations;
	
	public AuthorizationDialog(AuthContainer authContainer) {
		setAuthManager(new AuthManager());
		setAuthContainer(authContainer);
		setUserAuth(AUserAuth.get());
		getUserAuth().setPasswordActionListener(this);
		initMsgs();
	}
	
	private void initMsgs(){
		MSG_OK = getAuthContainer().getMsg("OK");
		MSG_CANCEL = getAuthContainer().getMsg("Cancel");
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
		getUserAuth().clear();
	}
	
	public void addAuthOperation(AuthOperation authOperation){
		getAuthManager().addAuthOperation(authOperation);
	}
	
	public void removeAuthOperation(AuthOperation authOperation){
		getAuthManager().removeAuthOperation(authOperation);
	}
	
	public boolean existsAuthOperation(AuthOperation authOperation){
		return getAuthManager().existsAuthOperation(authOperation);
	}
	
	public boolean authorizeOperation(String authorizationMoment){
		Set<UserAuthorizationOperation> operationsToAuth = new HashSet<UserAuthorizationOperation>();
		List<AuthOperation> authOperations = new ArrayList<AuthOperation>(); 
		setCurrentAuthorizationMoment(authorizationMoment);
		boolean authorized = true;
		HTMLMsg listOperations = new HTMLMsg();
		HTMLMsg.HTMLList authList = listOperations.createList("authorize",
				"ul", "Las siguientes operaciones requieren autorizacion:");
		for (AuthOperation authOperation : getAuthManager().getOperations(authorizationMoment)) {
			if(!authOperation.isAuthorized()){
				listOperations.createAndAddListElement("", authOperation.getOpDescription(), authOperation.isSevere(),
						authList);
				operationsToAuth.add(new UserAuthorizationOperation(authOperation.getOperationType(),
						authOperation.getOperationLog(),authOperation.isMustSave(), authOperation.getAuthTime(),
						authOperation.getAmount(), authOperation.getPercentage()));
				authOperations.add(authOperation);
			}
		}
		if(authList.getElements().size() > 0){
			listOperations.addList(authList);
			setCurrentAuthorizationOperations(new ArrayList<UserAuthorizationOperation>(operationsToAuth));
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
			cOkButton.addActionListener(this);
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
			cCancelButton.addActionListener(this);			
			KeyUtils.setCancelButtonKeys(cCancelButton);
			KeyUtils.setButtonText(cCancelButton, MSG_CANCEL);			
			cCancelButton.setToolTipText(MSG_CANCEL);
			FocusUtils.addFocusHighlight(cCancelButton);
		}
		return cCancelButton;
	}

	private void cancel() {
		CallResult result = new CallResult();
		result.setError(true);
		setAuthorizeResult(result);
		cancelCurrentAuthorizationOperation();
		setVisible(false);
	}
	
	public void markAuthorized(String authorizationMoment, boolean authorized){
		for (AuthOperation authOperation : getAuthManager().getOperations(authorizationMoment)) {
			if(!authOperation.isLazyAuthorization()){
				authOperation.setAuthorized(authorized);
			}
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
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cCancelButton){
			cancel();
		}
		else{
			ok();
		}
	}
	
	public void ok(){
		// Autorización de usuario
		Integer userID = getUserAuth().getUserID();
		User user = null;
		if(userID != null){
			user = getAuthContainer().getUser(userID);
		}
		UserAuthData authData = new UserAuthData();
		authData.setForPOS(getAuthContainer() != null);
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
	
	/**
	 * Datos de autorizaciones ya realizadas de usuario
	 * 
	 * @param userAuthData
	 *            datos de usuario
	 */
	public void addAuthorizationDone(UserAuthData userAuthData){
		getUserAuth().manageDoneAuthorizations(userAuthData);
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

	public void setAuthContainer(AuthContainer authContainer) {
		this.authContainer = authContainer;
	}

	public AuthContainer getAuthContainer() {
		return authContainer;
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
			List<UserAuthorizationOperation> currentAuthorizationOperations) {
		this.currentAuthorizationOperations = currentAuthorizationOperations;
	}

	public List<UserAuthorizationOperation> getCurrentAuthorizationOperations() {
		return currentAuthorizationOperations;
	}

	protected void setCurrentAuthOperations(List<AuthOperation> currentAuthOperations) {
		this.currentAuthOperations = currentAuthOperations;
	}

	protected List<AuthOperation> getCurrentAuthOperations() {
		return currentAuthOperations;
	}
}
