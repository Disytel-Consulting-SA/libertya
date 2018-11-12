package org.openXpertya.grid.ed;

import java.awt.Frame;

import org.compiere.swing.CDialog;

public class VBPartnerCreateFactory extends LookupMenuCreateFactory {

	@Override
	public CDialog getCreateHandlerPanel(Frame frame,int WindowNo) {
		return new VBPartner(frame, WindowNo);
	}

}
