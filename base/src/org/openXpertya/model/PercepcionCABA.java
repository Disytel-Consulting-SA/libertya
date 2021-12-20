package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.util.Env;

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

		int BPartnerLocationID = getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID();
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), BPartnerLocationID, null);
		MLocation location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
		int c_Region_BP_ID = location.getC_Region_ID();
		
		/*Utilizo la funcionalidad de Jurisdicción CABA solo si está activa en la configuración de percepciones
		 * de la organización y el cliente tiene el Check activo 
		 */		
		boolean applyCABAJurisdiction = getPercepcionData().isUseCABAJurisdiction() && getPercepcionData().getBpartner().isBuiltCabaJurisdiction(); 
		String arcNormCode = null;
		
		// PADRON DE ALTO RIESGO
		BigDecimal perc = getPerception(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA);
		if (perc == null) {
			// PADRON DE REGIMENES GENERALES
			perc = getPerception(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeRegímenesGenerales);
			if (perc == null) {
				// PADRON DE REGIMEN SIMPLIFICADO
				perc = getPerception(MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA);
				if (perc == null) {
					/*
					 * Aplico la percepciones si corresponde por la región, o si tiene aplica la Jurisdicción CABA
					 */
					if (c_Region_Tax_ID == c_Region_BP_ID || applyCABAJurisdiction) {
						p = super.applyDebitPerception();
						arcNormCode = codigo_De_Norma_Estandar;
					}
				} else {
					// PADRON DE REGIMEN SIMPLIFICADO
					p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(), getPercepcionData()
							.getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA, true));
					arcNormCode = codigo_De_Norma_Regimen_Simplificado;
				}
			} else {
				// PADRON DE REGIMENES GENERALES
				p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(), getPercepcionData()
						.getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeRegímenesGenerales, true));
				arcNormCode = codigo_De_Norma_Regimen_General;
			}
		} else {
			// PADRON DE ALTO RIESGO
			p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(), getPercepcionData()
					.getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA, true));
			arcNormCode = codigo_De_Norma_Padron_Alto_Riesgo;
		}
		// Código de Norma Arciba
		if(p != null) {
			p.arcibaNormCode = arcNormCode;
		}
		return p;
	}

	private BigDecimal getPerception(String padronType) {
		String taxID = getPercepcionData().getBpartner().getTaxID();
		return MBPartnerPadronBsAs.getBPartnerPerc("percepcion", taxID,
				new Timestamp(getPercepcionData().getDocument().getDate().getTime()), padronType, null);
	}

}
