package org.openXpertya.model;

import java.math.BigDecimal;

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
	public BigDecimal getPercepcionPercToApply() {
		//MTax tax = new MTax(Env.getCtx(), getPercepcionData().getTax().getC_Tax_ID(), null);
		int c_Region_Tax_ID = getPercepcionData().getTax().getC_Region_ID();
		
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(),getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID(), null);
		MLocation location = new MLocation(Env.getCtx(),bpLocation.getC_Location_ID(), null);
		int c_Region_BP_ID = location.getC_Region_ID();
		
		BigDecimal perc = BigDecimal.ZERO;
		if (c_Region_Tax_ID == c_Region_BP_ID){
			perc = super.getPercepcionPercToApply();
		}
		
		return perc;
	}
}
