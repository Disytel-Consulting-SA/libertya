package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PercepcionPadronChubutComRivInscriptos extends PercepcionStandard {

	public PercepcionPadronChubutComRivInscriptos() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionPadronChubutComRivInscriptos(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BigDecimal getPercepcionPercToApply() {
		
		// -------------------------------------------------------------------------------------------
		// Buscarlo por el cuit en el padron excento en primer lugar, de encontrarlo DEVOLVER CERO
		// porque quiere decir que NO tiene que calcular percepcion
		// dREHER
		BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
						getPercepcionData().getBpartner().getTaxID(), new Timestamp(getPercepcionData().getDocument().getDate().getTime()),
						X_C_BPartner_Padron_BsAs.PADRONTYPE_PadronComodoroRivadaviaExentos, null);
		if(perc != null){
			System.out.println("El cliente CUIT=" + getPercepcionData().getBpartner().getTaxID() + " figura en el padron COMODORO EXCENTO, no realizar percepcion!");
			return Env.ZERO;
		}
		// ------------------------------------------------------------------------------------------
		
		// Buscarlo por el cuit
		perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
				getPercepcionData().getBpartner().getTaxID(), new Timestamp(getPercepcionData().getDocument().getDate().getTime()),
				X_C_BPartner_Padron_BsAs.PADRONTYPE_PadronComodoroRivadaviaInscriptos, null);
		if(perc == null){
			perc = super.getPercepcionPercToApply();
			System.out.println("El cliente CUIT=" + getPercepcionData().getBpartner().getTaxID() + " NO figura en el padron COMODORO INSCRIPTO, toma percepcion gral:" + perc + " %");
		}else
			System.out.println("El cliente CUIT=" + getPercepcionData().getBpartner().getTaxID() + " figura en el padron COMODORO INSCRIPTO, toma percepcion gral:" + perc + " %");
		
		return perc;
	}
	
	@Override
	public BigDecimal getMinimumNetAmount() {
		// Buscarlo por el cuit
		BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
				getPercepcionData().getBpartner().getTaxID(), new Timestamp(getPercepcionData().getDocument().getDate().getTime()),
				X_C_BPartner_Padron_BsAs.PADRONTYPE_PadronComodoroRivadaviaInscriptos, null);
		if(perc == null){
			return super.getMinimumNetAmount();
		}
		return getRegisterMinimumNetAmount(X_C_BPartner_Padron_BsAs.PADRONTYPE_PadronComodoroRivadaviaInscriptos);
	}
	
	public BigDecimal getRegisterMinimumNetAmount(String padronType) {
		MOrgPercepcionConfig percepcionConfig = MOrgPercepcionConfig.getOrgPercepcionConfig(Env.getCtx(), getPercepcionData().getDocument().getOrgID(), padronType, null);
		if (percepcionConfig != null){
			return percepcionConfig.getMinimumNetAmount();
		}
		return super.getMinimumNetAmount();
	}

}
