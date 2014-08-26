package org.openXpertya.model;

import java.math.BigDecimal;

public class PercepcionIIBBProvincias extends PercepcionStandard {
	
	public PercepcionIIBBProvincias() {
		// TODO Auto-generated constructor stub
	}

	public PercepcionIIBBProvincias(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BigDecimal getPercepcionPercToApply() {
		BigDecimal rate = BigDecimal.ZERO;
		if(getPercepcionData().isConvenioMultilateral()){
			if(getPercepcionData().getBpartner().isConvenioMultilateral()){
				rate = super.getPercepcionPercToApply();
			}
		}
		else{
			if(!getPercepcionData().getBpartner().isConvenioMultilateral()){
				rate = super.getPercepcionPercToApply();
			}
		}
		return rate;
	}
	
}
