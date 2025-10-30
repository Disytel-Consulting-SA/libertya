package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
	public Percepcion applyDebitPerception() {
		// Buscarlo por el cuit
		Percepcion p = null;
		
		/**
		 * Aca se debe verificar el tipo de domicilio a tomar desde la AD_Org_Percepcion
		 * TODO: ver si aplica sobre este tipo de percepcion
		 * dREHER FEb'25 
		 */
		
		int c_Region_Tax_ID = getPercepcionData().getTax().getC_Region_ID();
		
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
				debug("Coinciden las regiones, calculo percepcion! RegionID:" + c_Region_BP_ID);
				p = super.applyDebitPerception();
			}else
				debug("NO Coinciden las regiones, busca por padron! TaxRegionID:" + c_Region_Tax_ID + " - BPRegionID:" + c_Region_BP_ID);
			
		}
		
		// Si debe priorizar domicilio y NO encontro coincidencia, verificar padron
		if(p==null) {
			BigDecimal perc = MBPartnerPadronBsAs.getBPartnerPerc("percepcion",
					getPercepcionData().getBpartner().getTaxID(), new Timestamp(getPercepcionData().getDocument().getDate().getTime()),
					MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs, null);
			debug("NO encontro coincidencia por domicilio, verifica padron. Alicuota:" + perc);
			if(perc == null){
				p = super.applyDebitPerception();
				debug("No encontro porcentaje por padron, aplica segun config de EC/OrgPerc/Tax!");
			}
			else {
				p = getApplyRate(perc, getPercepcionData().getDocument().getTaxBaseAmt(),
						getPercepcionData().getMinimumNetAmtBy(MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs, true));
				debug("Encontro porcentaje en el padron, aplica percepcion segun la data del mismo.");
			}
		}
		
		return p;
	}
	
	// dREHER Feb '25
	protected void debug(String s) {
		System.out.println("--> PercepcionPadronBsAsStandard." + s);
	}
}
