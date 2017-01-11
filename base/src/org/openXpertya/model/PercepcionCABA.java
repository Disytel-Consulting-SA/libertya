package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.Env;

public class PercepcionCABA extends PercepcionStandard {

	private BigDecimal minimumNetAmount = null;
	private BigDecimal percepcionPercToApply = null;
	private String arcibaNormCode = null;

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
	public BigDecimal getPercepcionPercToApply() {
		if (percepcionPercToApply == null) {
			loadData();
		}
		return percepcionPercToApply;
	}

	// Se retorna el código de norma para Exportación ARCIBA
	// FIX - Sería conveniente que el código de norma fuera una columna del padrón
	@Override
	public String getArcibaNormCode() {
		if (arcibaNormCode == null) {
			loadData();
		}
		return arcibaNormCode;
	}

	@Override
	public BigDecimal getMinimumNetAmount() {
		if (minimumNetAmount == null) {
			loadData();
		}
		return minimumNetAmount;
	}

	public void loadData() {
		int c_Region_Tax_ID = getPercepcionData().getTax().getC_Region_ID();

		int BPartnerLocationID = getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID();
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), BPartnerLocationID, null);
		MLocation location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
		int c_Region_BP_ID = location.getC_Region_ID();
		
		/*Utilizo la funcionalidad de Jurisdicción CABA solo si está activa en la configuración de percepciones
		 * de la organización y el cliente tiene el Check activo 
		 */		
		boolean applyCABAJurisdiction = getPercepcionData().isUseCABAJurisdiction() && getPercepcionData().getBpartner().isBuiltCabaJurisdiction(); 
		
		/*
		 * Aplico la percepciones si corresponde por la región, o si tiene aplica la Jurisdicción CABA
		 */
		if (c_Region_Tax_ID == c_Region_BP_ID || applyCABAJurisdiction) {
			// PADRON DE ALTO RIESGO
			percepcionPercToApply = getPerception(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA);
			if (percepcionPercToApply == null) {
				// PADRON DE REGIMENES GENERALES
				percepcionPercToApply = getPerception(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeRegímenesGenerales);
				if (percepcionPercToApply == null) {
					// PADRON DE REGIMEN SIMPLIFICADO
					percepcionPercToApply = getPerception(MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA);
					if (percepcionPercToApply == null) {
						percepcionPercToApply = getPerception(null);
						minimumNetAmount = super.getMinimumNetAmount();
						arcibaNormCode = codigo_De_Norma_Estandar;
					} else {
						// PADRON DE REGIMEN SIMPLIFICADO
						arcibaNormCode = codigo_De_Norma_Regimen_Simplificado;
						minimumNetAmount = getRegisterMinimumNetAmount(MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA);
					}
				} else {
					// PADRON DE REGIMENES GENERALES
					arcibaNormCode = codigo_De_Norma_Regimen_General;
					minimumNetAmount = getRegisterMinimumNetAmount(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeRegímenesGenerales);
				}
			} else {
				// PADRON DE ALTO RIESGO
				arcibaNormCode = codigo_De_Norma_Padron_Alto_Riesgo;
				minimumNetAmount = getRegisterMinimumNetAmount(MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA);
			}
		}
	}

	private BigDecimal getPerception(String padronType) {
		if (padronType == null) {
			return super.getPercepcionPercToApply();
		}
		String taxID = getPercepcionData().getBpartner().getTaxID();
		return MBPartnerPadronBsAs.getBPartnerPerc("percepcion", taxID, Env.getDate(), padronType, null);
	}

	public BigDecimal getRegisterMinimumNetAmount(String padronType) {
		int orgID = getPercepcionData().getDocument().getOrgID();
		MOrgPercepcionConfig percepcionConfig = MOrgPercepcionConfig.getOrgPercepcionConfig(Env.getCtx(), orgID, padronType, null);
		if (percepcionConfig != null) {
			return percepcionConfig.getMinimumNetAmount();
		}
		return super.getMinimumNetAmount();
	}

}
