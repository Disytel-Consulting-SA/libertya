package org.openXpertya.model;

import org.openXpertya.util.Env;
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
		int c_Region_Tax_ID = getPercepcionData().getTax().getC_Region_ID();
		
		/**
		 * Aca se debe verificar el tipo de domicilio a tomar desde la AD_Org_Percepcion
		 * TODO: ver si aplica sobre este tipo de percepcion
		 * dREHER FEb'25 
		 */
		
		String tipoDomicilio = getPercepcionData().getTipoDomicilio();
		debug("applyDebitPerception. tipoDomicilio: " + tipoDomicilio);

		int BPartnerLocationID = getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID();
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), BPartnerLocationID, null);
		MLocation location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
		
		
		if(!tipoDomicilio.equals(DOMICILIO_FACTURACION)) {
			
			MLocation loc = getLocation(tipoDomicilio);
			if(loc!=null)
				location = loc;
			else
				debug("applyDebitPerception. Como no encontro domicilio desde comprobante, lo toma desde la EC!");
		}
		
		int c_Region_BP_ID = location.getC_Region_ID();
		
		/**
		 * Si la config de la org/percepcion indica que debe priorizar por domicilio, debe comparar el domicilio primero y ver si corresponde aplicar
		 * dREHER Feb '25
		 */
		boolean isPriorizaDomicilio = getPercepcionData().isPriorizaDomicilio();
		if(isPriorizaDomicilio) {
			
			debug("Prioriza domicilio, debe tomar primero la verificacion de si coinciden las regiones.");
			if (c_Region_Tax_ID == c_Region_BP_ID) {
				p = super.applyDebitPerception();
			}
			
		}

		// Si no pudo aplicar por provincia priorizando domicilio, buscar en config de EC
		if(p == null) {
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
		}
		
		if(p == null) {
			debug("NO Prioriza domicilio, pero tampoco encontro coincidencia de convenio, ver si coinciden las regiones.");
			if (c_Region_Tax_ID == c_Region_BP_ID) {
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
