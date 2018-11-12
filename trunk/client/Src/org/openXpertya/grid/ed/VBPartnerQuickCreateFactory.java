package org.openXpertya.grid.ed;

import java.awt.Frame;

import org.compiere.swing.CDialog;

public class VBPartnerQuickCreateFactory extends VBPartnerCreateFactory {

	public VBPartnerQuickCreateFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CDialog getCreateHandlerPanel(Frame frame, int WindowNo) {
		return new VBPartnerQuick(frame, WindowNo);
	}

}
