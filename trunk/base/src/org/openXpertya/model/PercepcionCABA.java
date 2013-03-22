package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.Env;

public class PercepcionCABA extends PercepcionStandard {
	
	public PercepcionCABA() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionCABA(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BigDecimal getPercepcionPercToApply() {
		int c_Region_Tax_ID = getPercepcionData().getTax().getC_Region_ID();
		
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(),getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID(), null);
		MLocation location = new MLocation(Env.getCtx(),bpLocation.getC_Location_ID(), null);
		int c_Region_BP_ID = location.getC_Region_ID();
		
		BigDecimal perc = BigDecimal.ZERO;
		if (c_Region_Tax_ID == c_Region_BP_ID){
			perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
					getPercepcionData().getBpartner().getID(), Env.getDate(),
					MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA, null);
			if(perc == null){
				perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
						getPercepcionData().getBpartner().getID(), Env.getDate(),
						MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA, null);
			}
			if(perc == null){
				perc = super.getPercepcionPercToApply();
			}
		}
		return perc;
	}
}
