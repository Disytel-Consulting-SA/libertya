package org.openXpertya.model;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PercepcionPorLocalizacionDeCliente extends PercepcionStandard {
	public PercepcionPorLocalizacionDeCliente() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionPorLocalizacionDeCliente(PercepcionProcessorData percepcionData) {
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
		
		MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(),getPercepcionData().getBpartner().getPrimaryC_BPartner_Location_ID(), null);
		MLocation location = new MLocation(Env.getCtx(),bpLocation.getC_Location_ID(), null);
		
		if(!tipoDomicilio.equals(DOMICILIO_FACTURACION)) {
			
			MLocation loc = getLocation(tipoDomicilio);
			if(loc!=null)
				location = loc;
			else
				debug("applyDebitPerception. Como no encontro domicilio desde comprobante, lo toma desde la EC!");
		}
		
		int c_Region_BP_ID = location.getC_Region_ID();
		
		if (c_Region_Tax_ID == c_Region_BP_ID){
			p = super.applyDebitPerception();
		}
		
		return p;
	}
	
	@Override
	protected void debug(String s) {
		System.out.println("--> PercepcionPorLocalizacionDeCliente." + s);
	}
}
