package org.adempiere.webui.window;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WPasswordEditor;
import org.openXpertya.apps.UserDataChange;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Space;

public class WUserDataChange extends Window  implements EventListener {

	protected int m_WindowNo = 0;
	
	// Labels
	Label currentPasswordLabel;
	Label newPasswordLabel;
	Label repeatPasswordLabel;
	
	// Fields
	WPasswordEditor currentPasswordText;
	WPasswordEditor newPasswordText;
	WPasswordEditor repeatPasswordText;
	
	// Buttons
	Button okButton;
	
	public WUserDataChange() {
		this(0);
	}
	
	public WUserDataChange(int m_WindowNo) {
		
		this.m_WindowNo = m_WindowNo;
		initComponents();
    	
	}
	
	protected void initComponents() {

		// Titulo y tamaño de la ventana
		this.setTitle(getMsg("ChangeUserPassword"));
		this.setWidth("500px");
		this.setHeight("130px");
		this.setClosable(true);
		
		// Panel principal				
		Grid mainPanel = new Grid();
    	mainPanel.setHeight("100%");
    	mainPanel.setWidth("100%");
    	this.appendChild(mainPanel);
    	
    	// Instanciacion de los componentes
    	currentPasswordLabel = new Label(getMsg("CurrentPassword"));
    	newPasswordLabel = new Label(getMsg("NewPassword"));
    	repeatPasswordLabel = new Label(getMsg("RepeatPassword"));
    	
    	currentPasswordText = new WPasswordEditor();
    	newPasswordText = new WPasswordEditor();
    	repeatPasswordText = new WPasswordEditor();
    	
    	okButton = new Button(getMsg("SaveShort"));
    	okButton.addEventListener(Events.ON_CLICK, this);
    	
    	// Incorporacion de los componentes al panel
    	Rows rows = mainPanel.newRows();
    	Row row = rows.newRow();
    	row.appendChild(currentPasswordLabel.rightAlign());
    	row.appendChild(currentPasswordText.getComponent());
    	
    	row = rows.newRow();
    	row.appendChild(newPasswordLabel.rightAlign());
    	row.appendChild(newPasswordText.getComponent());    	
    	
    	row = rows.newRow();
    	row.appendChild(repeatPasswordLabel.rightAlign());
    	row.appendChild(repeatPasswordText.getComponent());
    	
    	row = rows.newRow();
    	row.appendChild(new Space());
    	row.appendChild(okButton);
    	
	}
	
	protected String getMsg(String name) {
		return Msg.translate(Env.getCtx(), name);
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName())) {
			if (event.getTarget()==okButton) {
				changePassword();
			}
		}
	}
	
	protected void changePassword() {
		String currentPassword = currentPasswordText.getDisplay();
		String newPassword = newPasswordText.getDisplay();
		String repeatPassword = repeatPasswordText.getDisplay();
		
		String errorMsg = UserDataChange.tryPasswordChange(currentPassword, newPassword, repeatPassword, Env.getCtx());
		if (errorMsg != null) {
			FDialog.error(m_WindowNo, errorMsg);
		} else {
			FDialog.warn(m_WindowNo, "PasswordChangedSuccessful");
			setVisible(false);
		}
		
	}
	
}
