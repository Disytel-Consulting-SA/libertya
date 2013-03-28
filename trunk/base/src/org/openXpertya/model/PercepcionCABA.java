package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.Env;

public class PercepcionCABA extends PercepcionStandard {
	
	private BigDecimal percepcionPercToApply = null;
	private String arcibaNormCode = null;
	
	final String codigo_De_Norma_Regimen_General = "14";
	final String codigo_De_Norma_Padron_Alto_Riesgo = "16";
	final String codigo_De_Norma_Regimen_Simplificado = "18";
	
	public PercepcionCABA() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionCABA(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BigDecimal getPercepcionPercToApply() {
		if (percepcionPercToApply == null){
			loadData();
		}
		return percepcionPercToApply;
	}
	
	public void loadData() {
		int c_Region_Tax_ID = getPercepcionData().getTax().getC_Region_ID();
		
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(),getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID(), null);
		MLocation location = new MLocation(Env.getCtx(),bpLocation.getC_Location_ID(), null);
		int c_Region_BP_ID = location.getC_Region_ID();
		
		if (c_Region_Tax_ID == c_Region_BP_ID){
			percepcionPercToApply = MBPartnerPadronBsAs.getBPartnerPerc("percepcion", getPercepcionData().getBpartner().getID(), Env.getDate(), MBPartnerPadronBsAs.PADRONTYPE_PadrónDeAltoRiesgoCABA, null);
			if(percepcionPercToApply == null){
				percepcionPercToApply = MBPartnerPadronBsAs.getBPartnerPerc("percepcion", getPercepcionData().getBpartner().getID(), Env.getDate(), MBPartnerPadronBsAs.PADRONTYPE_RégimenSimplificadoCABA, null);
				if(percepcionPercToApply == null){
					percepcionPercToApply = super.getPercepcionPercToApply();
					arcibaNormCode = codigo_De_Norma_Regimen_General;
				}
				else{ arcibaNormCode = codigo_De_Norma_Regimen_Simplificado; }
			}
			else{ arcibaNormCode = codigo_De_Norma_Padron_Alto_Riesgo; }
		}
	}

	// Se retorna el código de norma para Exportación ARCIBA
	// FIX - Sería conveniente que el código de norma fuera una columna del padrón
	@Override
	public String getArcibaNormCode() {
		if (arcibaNormCode == null){
			loadData();
		}
		return arcibaNormCode;
	}
}
