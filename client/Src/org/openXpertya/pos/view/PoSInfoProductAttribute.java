package org.openXpertya.pos.view;

import java.awt.Frame;

import org.openXpertya.apps.search.InfoProductAttribute;

public class PoSInfoProductAttribute extends InfoProductAttribute {

	public PoSInfoProductAttribute(Frame frame, boolean modal, int WindowNo, int M_Warehouse_ID, int M_PriceList_ID, String value, boolean multiSelection, String whereClause) {
		super(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value,
				multiSelection, whereClause);
		getPickPriceList().setEnabled(false);
		getPickWarehouse().setEnabled(false);
		//executeQuery();
	}
}
