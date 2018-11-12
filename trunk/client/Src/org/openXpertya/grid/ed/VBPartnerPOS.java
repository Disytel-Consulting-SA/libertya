package org.openXpertya.grid.ed;

import java.awt.Frame;

import org.openXpertya.model.MCategoriaIva;

public class VBPartnerPOS extends VBPartnerQuick {

	public VBPartnerPOS(Frame frame, int WindowNo) {
		super(frame, WindowNo);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void postInit(){
		super.postInit();
		fCategoriaIVA.setReadWrite(false);
		// CONSUMIDOR FINAL
		MCategoriaIva cfiva = MCategoriaIva.get(getCtx(), MCategoriaIva.CONSUMIDOR_FINAL, getTrxName());
		fCategoriaIVA.setValue(cfiva.getID());
	}
}
