package org.openXpertya.plugin.common;

import java.util.Vector;

import javax.swing.JMenuItem;

import org.openXpertya.grid.ed.VLookup;

public interface PluginLookupInterface {

	/**
	 * Este metodo debera devolver las entradas de menu contextual correspondientes
	 * @return
	 */
	public Vector<JMenuItem> getBPartnerLookupEntries();
	
	/**
	 * Este metodo ejecuta las acciones correspondientes
	 * @return
	 */
	public void doBPartnerLookupAction(JMenuItem clickedItem, boolean newRecord, VLookup aLookup);
	
}
