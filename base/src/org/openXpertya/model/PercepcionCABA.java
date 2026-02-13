package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PercepcionCABA extends PercepcionStandard {

	final String codigo_De_Norma_Regimen_General = "29";
	final String codigo_De_Norma_Padron_Alto_Riesgo = "16";
	final String codigo_De_Norma_Regimen_Simplificado = "18";
	final String codigo_De_Norma_Estandar = "29";

	public PercepcionCABA() {
		super();
	}

	public PercepcionCABA(PercepcionProcessorData percepcionData) {
		super(percepcionData);
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
		
		/*Utilizo la funcionalidad de Jurisdicción CABA solo si está activa en la configuración de percepciones
		 * de la organización y el cliente tiene el Check activo 
		 */		
		boolean applyCABAJurisdiction = getPercepcionData().isUseCABAJurisdiction() && getPercepcionData().getBpartner().isBuiltCabaJurisdiction(); 
		String arcNormCode = null;
		
		debug("applyCABAJurisdiction. Org/Perc Juris:" + getPercepcionData().isUseCABAJurisdiction() + " - BP IsCABAJurisdiction:" + getPercepcionData().getBpartner().isBuiltCabaJurisdiction());
		 
		/**
		 * Si la config de la org/percepcion indica que debe priorizar por domicilio, debe comparar el domicilio primero y ver si corresponde aplicar
		 * dREHER Feb '25
		 */
		boolean isPriorizaDomicilio = getPercepcionData().isPriorizaDomicilio();
		if(isPriorizaDomicilio) {
			
			debug("Prioriza domicilio, debe tomar primero la verificacion de coincidencia de regiones.");
			debug("Region Tax:" + c_Region_Tax_ID + " Region BP:" + c_Region_BP_ID);
			if (c_Region_Tax_ID == c_Region_BP_ID || applyCABAJurisdiction) {
				debug("Corresponde percepcion por coincidencia de regiones o aplica jurisdiccion CABA!");
				p = super.applyDebitPerception();
				arcNormCode = codigo_De_Norma_Estandar;
			}else
				debug("No coinciden regiones y no aplica jurisdiccion CABA, buscar por padrones...");
			
		}
		
		if(p == null){

			// PADRON DE ALTO RIESGO
			BigDecimal perc = getPerception(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA);
			debug("Alicuota padron alto riesgo CABA:" + perc);
			if (perc == null) {
				// PADRON DE REGIMENES GENERALES
				perc = getPerception(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeRegímenesGenerales);
				debug("Alicuota regimen general CABA:" + perc);
				if (perc == null) {
					// PADRON DE REGIMEN SIMPLIFICADO
					perc = getPerception(MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA);
					debug("Alicuota regimen simplificado CABA:" + perc);
					if (perc == null) {
						/*
						 * Aplico la percepciones si corresponde por la región, o si tiene aplica la Jurisdicción CABA
						 */
						if (c_Region_Tax_ID == c_Region_BP_ID || applyCABAJurisdiction) {
							debug("Aplica regimenes, Region Tax:" + c_Region_Tax_ID + " Region BP:" + c_Region_BP_ID);
							p = super.applyDebitPerception();
							arcNormCode = codigo_De_Norma_Estandar;
						}
					} else {
						// PADRON DE REGIMEN SIMPLIFICADO
						p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(), getPercepcionData()
								.getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA, true));
						arcNormCode = codigo_De_Norma_Regimen_Simplificado;
						debug("Se calculo por padron regimen simplificado CABA.");
					}
				} else {
					// PADRON DE REGIMENES GENERALES
					p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(), getPercepcionData()
							.getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeRegímenesGenerales, true));
					arcNormCode = codigo_De_Norma_Regimen_General;
					debug("Se calculo por padron regimenes generales CABA.");
				}
			} else {
				// PADRON DE ALTO RIESGO
				p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(), getPercepcionData()
						.getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA, true));
				arcNormCode = codigo_De_Norma_Padron_Alto_Riesgo;
				debug("Se calculo por padron alto riesgo CABA.");
			}
		}
		
		/**
		 * Si llego nulo hasta es que no aplico en el padron, verificar si debe aplicar segun domicilio
		 */
		if(p == null) {
			if(isPriorizaDomicilio)
				debug("Prioriza padron pero no encontro la EC, entonces verifica si corresponde por domicilio.");
			
			if (c_Region_Tax_ID == c_Region_BP_ID || applyCABAJurisdiction) {
				p = super.applyDebitPerception();
				debug("Corresponde por domicilio/jurisdiccion CABA, toma alicuota segun EC/org/impuesto");
				arcNormCode = codigo_De_Norma_Estandar;
			}
		}
		
		// Código de Norma Arciba
		if(p != null) {
			p.arcibaNormCode = arcNormCode;
		}
		return p;
	}

	private BigDecimal getPerception(String padronType) {
		String taxID = getPercepcionData().getBpartner().getTaxID();
		
		/**
		 * Si la entidad comercial tiene configurada una alicuota en percepcion de esta misma region, tomarla desde ahi
		 * dREHER Feb '25
		 */
		
		BigDecimal alicuota = MBPartnerPadronBsAs.getBPartnerPerc("percepcion", taxID,
				new Timestamp(getPercepcionData().getDocument().getDate().getTime()), padronType, null);
		if(!Util.isEmpty(alicuota, true)) {
			debug("getPerception. Encontro alicuota en el padron, toma esta:" + alicuota);
			return alicuota;
		}
		
		// En caso de no encontra alicuota, devolver null para continuar con el siguiente padron segun prioridades
		// debug("getPerception. Como no encontro alicuota en el padron, busca por EC/OrgPerc/Tax...");
		// alicuota = getPercepcionData().getAlicuota();
		
		debug("getPerception. Como no encontro alicuota en el padron, busca en siguiente padron por prioridad. Padron tipo:" + padronType);
		
		return null; // alicuota; 
	}
	
	// dREHER Feb '25
	protected void debug(String s) {
		System.out.println("--> PercepcionCABA." + s);
	}

}
