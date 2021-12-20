package org.openXpertya.model;

import org.openXpertya.util.Env;

public class PercepcionPorLocalizacionDeCliente extends PercepcionStandard {
	public PercepcionPorLocalizacionDeCliente() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionPorLocalizacionDeCliente(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Percepcion applyDebitPerception() {
		Percepcion p = null;
		int c_Region_Tax_ID = getPercepcionData().getTax().getC_Region_ID();
		
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(),getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID(), null);
		MLocation location = new MLocation(Env.getCtx(),bpLocation.getC_Location_ID(), null);
		int c_Region_BP_ID = location.getC_Region_ID();
		
		if (c_Region_Tax_ID == c_Region_BP_ID){
			p = super.applyDebitPerception();
		}
		
		return p;
	}
}
