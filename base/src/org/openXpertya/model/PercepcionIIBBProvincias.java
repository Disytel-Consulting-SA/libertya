package org.openXpertya.model;

import org.openXpertya.util.Util;

public class PercepcionIIBBProvincias extends PercepcionStandard {
	
	public PercepcionIIBBProvincias() {
		// TODO Auto-generated constructor stub
	}

	public PercepcionIIBBProvincias(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Percepcion applyDebitPerception() {
		Percepcion p = null;
		if(getPercepcionData().isConvenioMultilateral()){
			if(getPercepcionData().getBpartner().getIsConvenioMultilateral() != null){
				p = super.applyDebitPerception();
			}
		}
		else{
			if(getPercepcionData().getBpartner().getIsConvenioMultilateral() == null){
				p = super.applyDebitPerception();
			}
		}
		return p;
	}
	
	@Override
	public Percepcion applyCreditPerception() {
		Percepcion p = null;
		// Si posee un documento relacionado, se verifica si se aplicó esta percepción,
		// en ese caso se debe verificar la configuración de devolución de percepciones
		// Si no posee documento relacionado, queda como estaba antes, aplicando la percepción si así se requiere 
		if(getPercepcionData().getRelatedDocument() == null) {
			p = applyDebitPerception();
		}
		else {
			// Iterar por las percepciones aplicadas, verificar si se aplicó esta y dar la
			// alícuota en ese caso
			for (DocumentTax documentTax : getPercepcionData().getRelatedDocument().getAppliedPercepciones()) {
				if(documentTax.getTaxID() == getPercepcionData().getTax().getID()) {
					// Encontramos que se aplicó esta percepción, entonces verificar por config si
					// se debe aplicar
					if(((getPercepcionData().isVoiding() && getPercepcionData().isAllowTotalReturn())
							|| (!getPercepcionData().isVoiding() && getPercepcionData().isAllowPartialReturn()))
							&& ((!getPercepcionData().isConvenioMultilateral() 
									&& Util.isEmpty(getPercepcionData().getBpartner().getIsConvenioMultilateral(), true))
								|| (getPercepcionData().isConvenioMultilateral() 
										&& !Util.isEmpty(getPercepcionData().getBpartner().getIsConvenioMultilateral(), true)))) {
						p = getApplyRate(documentTax.getTaxRate());
					}
					break;
				}
			}
		}
		return p;
	}
}
