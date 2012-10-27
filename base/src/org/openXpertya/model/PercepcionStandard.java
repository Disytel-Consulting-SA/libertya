package org.openXpertya.model;

import java.math.BigDecimal;

public class PercepcionStandard extends AbstractPercepcionProcessor {

	public PercepcionStandard() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionStandard(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BigDecimal getPercepcionPercToApply() {
		return getPercepcionData().getTax().getRate();
	}

}
