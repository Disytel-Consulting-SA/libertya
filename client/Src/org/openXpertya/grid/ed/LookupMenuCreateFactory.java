package org.openXpertya.grid.ed;

import java.awt.Frame;

import org.compiere.swing.CDialog;

public abstract class LookupMenuCreateFactory {

	public abstract CDialog getCreateHandlerPanel(Frame frame,int WindowNo);
}
