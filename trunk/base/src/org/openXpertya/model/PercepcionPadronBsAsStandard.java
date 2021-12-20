package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
	public Percepcion applyDebitPerception() {
		// Buscarlo por el cuit
		Percepcion p = null;
		BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
				getPercepcionData().getBpartner().getTaxID(), new Timestamp(getPercepcionData().getDocument().getDate().getTime()),
				MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs, null);
		if(perc == null){
			p = super.applyDebitPerception();
		}
		else {
			p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(),
					getPercepcionData().getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs, true));
		}
		return p;
	}
}
