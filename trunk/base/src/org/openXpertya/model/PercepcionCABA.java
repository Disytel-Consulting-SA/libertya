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
		BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
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
		return perc;
	}
}
