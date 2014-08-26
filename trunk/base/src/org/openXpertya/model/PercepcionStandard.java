package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

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
		BigDecimal perc = getPercepcionData().getAlicuota();
		if(Util.isEmpty(perc, true)){
			perc = getPercepcionData().getTax().getRate();
		}
		return perc;
	}

	@Override
	public String getArcibaNormCode() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public BigDecimal getMinimumNetAmount() {
		MOrgPercepcion orgPercepcion = MOrgPercepcion.getOrgPercepcion(Env.getCtx(), getPercepcionData().getDocument().getOrgID(), getPercepcionData().getTax().getC_Tax_ID(), null);
		return orgPercepcion.getMinimumNetAmount();
	}

}
