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
	
	@Override
	public BigDecimal getMinimumNetAmount() {
		BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",	getPercepcionData().getBpartner().getID(), Env.getDate(), MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs, null);
		if(perc == null){
			return super.getMinimumNetAmount();
		}
		return getRegisterMinimumNetAmount(MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs);
	}
	
	public BigDecimal getRegisterMinimumNetAmount(String padronType) {
		MOrgPercepcionConfig percepcionConfig = MOrgPercepcionConfig.getOrgPercepcionConfig(Env.getCtx(), getPercepcionData().getDocument().getOrgID(), padronType, null);
		if (percepcionConfig != null){
			return percepcionConfig.getMinimumNetAmount();
		}
		return super.getMinimumNetAmount();
	}
}
