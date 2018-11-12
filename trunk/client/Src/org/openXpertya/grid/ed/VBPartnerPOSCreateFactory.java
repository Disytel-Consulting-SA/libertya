package org.openXpertya.grid.ed;

import java.awt.Frame;

import org.compiere.swing.CDialog;

public class VBPartnerPOSCreateFactory extends VBPartnerQuickCreateFactory {

	public VBPartnerPOSCreateFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CDialog getCreateHandlerPanel(Frame frame, int WindowNo) {
		return new VBPartnerPOS(frame, WindowNo);
	}
	
}
