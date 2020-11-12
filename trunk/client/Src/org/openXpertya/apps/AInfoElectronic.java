package org.openXpertya.apps;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import org.openXpertya.process.ElectronicEventListener;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class AInfoElectronic extends AInfoFiscalPrinter implements ElectronicEventListener {

	public AInfoElectronic(APanel parent, int windowNo, String title) {
		super(parent, windowNo, title);
		// TODO Auto-generated constructor stub
	}

	public AInfoElectronic(Frame frame, String title, String message, int messageType) {
		super(frame, title, message, messageType);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void jbInit() throws Exception {
		super.jbInit();
		getCustomizeButton().setVisible(false);
		getCustomizeButton().setText(Msg.getMsg(Env.getCtx(), "RegenerateCAE"));
		getCustomizeButton().setIcon(Env.getImageIcon("Online24.gif"));
		setReprintButtonActive(false);
	}
	
	@Override
	protected ConfirmPanel createConfirmPanel() {
    	return new ConfirmPanel(true,true,true,true,false,true,false,ConfirmPanel.ALIGN_RIGHT);
    }
	
	public JButton getCustomizeButton() {
		return confirmPanel.getCustomizeButton();
	}
	
	private void fireRegenerateCAEAction() {
		if (getDialogActionListener() != null) {
			((ElectronicDialogActionListener)getDialogActionListener()).actionReGenerateCAE();
		}
	}
	
	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( ConfirmPanel.A_CUSTOMIZE ))  {
			fireRegenerateCAEAction();
		}
		super.actionPerformed(e);
	}
	
	public interface ElectronicDialogActionListener extends DialogActionListener {
		
		/**
		 * Acción que indica que se debe regenerar el CAE
		 */
		public void actionReGenerateCAE();
	}

	@Override
	public void electronicStatus(int status) {
		if(ElectronicEventListener.GENERATING_CAE == status) {
			getCustomizeButton().setVisible(false);
			setPrintingStatus();
		}
		else if(ElectronicEventListener.GENERATING_CAE_OK == status) {
			actionEnded(true, null);
			//setVisible(false);
		}
		else if(ElectronicEventListener.GENERATING_CAE_ERROR == status) {
			getCustomizeButton().setVisible(true);
			setErrorStatus();
		}
	}

	@Override
	public void errorOcurred(final String errorTitle, final String errorDesc) {
		Runnable doErrorOcurred = new Runnable() {
			public void run() {
				setInfoMessage(errorTitle, errorDesc);
				setErrorStatus();
			}
		};
		invoke(doErrorOcurred, false);
	}
}
