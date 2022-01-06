package org.openXpertya.process;

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.util.CLogger;

public class AddLinesQuicker_PO extends AddLinesQuicker {

	@Override
	protected void createLine(QuickDocumentLine quickDocumentLine) throws Exception {
		MOrderLine ol = new MOrderLine(getCtx(), 0, get_TrxName());
		ol.setC_Order_ID(getHeaderRecordID());
		ol.setM_Product_ID(quickDocumentLine.getProductID());
		ol.setQty(quickDocumentLine.getQty());
		ol.setHeaderInfo((MOrder)getHeaderPO());
		ol.setPrice();
		if(!ol.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}


}
