package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.Env;

public class PercepcionPadronBsAsStandard extends PercepcionStandard {

	public PercepcionPadronBsAsStandard() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionPadronBsAsStandard(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BigDecimal getPercepcionPercToApply() {
		BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
				getPercepcionData().getBpartner().getID(), Env.getDate(),
				MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs, null);
		if(perc == null){
			perc = super.getPercepcionPercToApply();
		}
		return perc;
	}
}
