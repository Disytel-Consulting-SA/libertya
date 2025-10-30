package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PercepcionStandard extends AbstractPercepcionProcessor {

	/** Tipos de domicilios dREHER Feb'25 */
	protected final String DOMICILIO_FACTURACION = "F";
	protected final String DOMICILIO_ENTREGA = "E";
	protected final String DOMICILIO_DESTINO_FINAL = "L";
	
	public PercepcionStandard() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PercepcionStandard(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Aplica y devuelve la percepción de este procesador
	 * 
	 * @param rate       tasa a aplicar
	 * @return percepción aplicada
	 */
	protected Percepcion getApplyRate(BigDecimal rate) {
		return getApplyRate(rate, getPercepcionData().getDocument().getTaxBaseAmt());
	}
	
	/**
	 * Aplica y devuelve la percepción de este procesador
	 * 
	 * @param rate       tasa a aplicar
	 * @param taxBaseAmt importe base
	 * @return percepción aplicada
	 */
	protected Percepcion getApplyRate(BigDecimal rate, BigDecimal taxBaseAmt) {
		return getApplyRate(rate, taxBaseAmt, getPercepcionData().getMinimumNetAmt());
	}
	
	/**
	 * Debug solo mostrar datos por consola
	 * dREHER
	 */
	protected void debug(String s) {
		System.out.println("====> PercepcionStandard." + s);
	}
	
	/**
	 * Aplica y devuelve la percepción de este procesador
	 * 
	 * @param rate       tasa a aplicar
	 * @param taxBaseAmt importe base
	 * @param minimumAmt importe mínimo a comparar
	 * @return percepción aplicada
	 */
	protected Percepcion getApplyRate(BigDecimal rate, BigDecimal taxBaseAmt, BigDecimal minimumAmt) {
		Percepcion p = null;
		
		debug("getApplyRate. rate=" + rate + " taxBaseAmt=" + taxBaseAmt + " minimumAmt=" + minimumAmt + " escala=" + getPercepcionData().getScale());
		
		if(!Util.isEmpty(rate, true)) {
			if(Util.isEmpty(minimumAmt, true) || taxBaseAmt.compareTo(minimumAmt) >= 0) {
				BigDecimal taxAmt = MTax.calculateTax(taxBaseAmt, false, rate, getPercepcionData().getScale());
				p = new Percepcion();
				p.setTaxRate(rate);
				p.setTaxBaseAmt(taxBaseAmt);
				p.setTaxAmt(taxAmt);
				p.setTaxID(getPercepcionData().getTax().getID());
				
				// dREHER
				debug("getApplyRate. TaxAmt (Percepcion)=" + p.getTaxAmt());
			}
		}
		// Controlar que el importe determinar (importe a percibir) sea mayor al importe
		// mínimo de percepción
		if(p != null && p.getTaxAmt().compareTo(getPercepcionData().getMinimumPercepcionAmt()) <= 0) {
			
			// dREHER
			debug("getApplyRate. TaxAmt=" + p.getTaxAmt() + " es menor al minimo a percibir=" + getPercepcionData().getMinimumPercepcionAmt() + " NO aplica percepcion!");
			p = null;
		}
		return p;
	}
	
	@Override
	public Percepcion applyDebitPerception() {
		BigDecimal perc = getPercepcionData().getAlicuota();
		if(Util.isEmpty(perc, true)){
			perc = getPercepcionData().getTax().getRate();
		}
		
		debug("applyDebitPerception. Alicuota:" + perc);
		
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
			
			debug("Prioriza domicilio, debe tomar primero la verificacion de regiones. Region Impuesto:" + c_Region_Tax_ID +
					" Region BP:" + c_Region_BP_ID);
			
			if (c_Region_Tax_ID == c_Region_BP_ID) {
				// Calcular la percepción
				p = getApplyRate(perc);
			}
			
		}else // NO Considerar domicilio, calcular directamente
			p = getApplyRate(perc);
		
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
					if((getPercepcionData().isVoiding() && getPercepcionData().isAllowTotalReturn())
							|| (!getPercepcionData().isVoiding() && getPercepcionData().isAllowPartialReturn())) {
						p = getApplyRate(documentTax.getTaxRate());
					}					
					break;
				}
			}
		}
		return p;
	}
	
	// dREHER
	public BigDecimal getPercepcionPercToApply() {
		BigDecimal perc = getPercepcionData().getAlicuota();
		if(Util.isEmpty(perc, true)){
			perc = getPercepcionData().getTax().getRate();
		}
		return perc;
	}
	
	// dREHER
	public BigDecimal getMinimumNetAmount() {
		MOrgPercepcion orgPercepcion = MOrgPercepcion.getOrgPercepcion(Env.getCtx(), getPercepcionData().getDocument().getOrgID(), getPercepcionData().getTax().getC_Tax_ID(), null);
		return orgPercepcion.getMinimumNetAmount();
	}

	/**
	 * Busca la localizacion segun el tipo de domicilio configurado en Org/Percepcion y el comprobante sobre
	 * el cual se esta calculando la percepcion
	 * 
	 * @param tipoDomicilio
	 * @return localizacion segun tipo de domicilio y comprobante evaluado
	 * @author dREHER Feb '25
	 */
	@Override
	public MLocation getLocation(String tipoDomicilio) {

		MLocation location = null;
		int C_Invoice_ID = getPercepcionData().getDocument().getC_Invoice_ID();
		MInvoice invoice = new MInvoice(Env.getCtx(), C_Invoice_ID, getPercepcionData().getDocument().getTrxName()); 
		
		String situacionEntrega = null;
		
		if(tipoDomicilio.equals(DOMICILIO_ENTREGA)) {
			if(invoice.get_Value("Cintolo_Delivery_Location")!=null) {
				MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), (Integer)invoice.get_Value("Cintolo_Delivery_Location"), null);
				location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
				situacionEntrega = invoice.get_Value("Cintolo_DeliveryViaRule")!=null?(String)invoice.get_Value("Cintolo_DeliveryViaRule"):null;
			}else {
				if(!Util.isEmpty(invoice.getC_Order_ID(), true)) {
					MOrder order = new MOrder(Env.getCtx(), invoice.getC_Order_ID(), null);
					if(order.get_Value("Cintolo_Delivery_Location")!=null) {
						MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), (Integer)order.get_Value("Cintolo_Delivery_Location"), null);
						location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
						situacionEntrega = order.get_Value("Cintolo_DeliveryViaRule")!=null?(String)order.get_Value("Cintolo_DeliveryViaRule"):null;
					}else {
						if(!Util.isEmpty(order.getRef_Order_ID(),true)) {
							order = new MOrder(Env.getCtx(), order.getRef_Order_ID(), null);
							if(order.get_Value("Cintolo_Delivery_Location")!=null) {
								MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), (Integer)order.get_Value("Cintolo_Delivery_Location"), null);
								location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
								situacionEntrega = order.get_Value("Cintolo_DeliveryViaRule")!=null?(String)order.get_Value("Cintolo_DeliveryViaRule"):null;
							}
						}
					}
				}
			}
			
			if(location==null) {
				
				debug("No encontro domicilio de entrega segun comprobante, lo busca en la EC");
				
				int C_BPartner_ID = getPercepcionData().getBpartner().getC_BPartner_ID();
				int C_BPartner_Location_ID = DB.getSQLValue(null, "SELECT C_BPartner_Location_ID FROM C_BPartner_Location " +
						" WHERE C_BPartner_ID=? AND IsActive='Y' AND Cintolo_Shippment_Direction='Y'", C_BPartner_ID);
				if(C_BPartner_Location_ID > 0) {
					MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), C_BPartner_Location_ID, null);
					location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
				}
				
			}

			debug("getLocation. Toma el domicilio de entrega:" + location);
			
		}else { 
			if(tipoDomicilio.equals(DOMICILIO_DESTINO_FINAL)) {
				if(invoice.get_Value("Cintolo_Final_Destine")!= null) {
					MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), (Integer)invoice.get_Value("Cintolo_Final_Destine"), null);
					location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
					situacionEntrega = invoice.get_Value("Cintolo_DeliveryViaRule")!=null?(String)invoice.get_Value("Cintolo_DeliveryViaRule"):null;
				}else {
					if(!Util.isEmpty(invoice.getC_Order_ID(), true)) {
						MOrder order = new MOrder(Env.getCtx(), invoice.getC_Order_ID(), null);
						if(order.get_Value("Cintolo_Final_Destine")!=null) {
							MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), (Integer)order.get_Value("Cintolo_Final_Destine"), null);
							location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
							situacionEntrega = order.get_Value("Cintolo_DeliveryViaRule")!=null?(String)order.get_Value("Cintolo_DeliveryViaRule"):null;
						}else {
							if(!Util.isEmpty(order.getRef_Order_ID(),true)) {
								order = new MOrder(Env.getCtx(), order.getRef_Order_ID(), null);
								if(order.get_Value("Cintolo_Final_Destine")!=null) {
									MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), (Integer)order.get_Value("Cintolo_Final_Destine"), null);
									location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
									situacionEntrega = order.get_Value("Cintolo_DeliveryViaRule")!=null?(String)order.get_Value("Cintolo_DeliveryViaRule"):null;
								}
							}
						}
					}
				}
				
				if(location==null) {
					
					debug("No encontro destino final segun comprobante, lo busca en la EC");
					
					int C_BPartner_ID = getPercepcionData().getBpartner().getC_BPartner_ID();
					int C_BPartner_Location_ID = DB.getSQLValue(null, "SELECT C_BPartner_Location_ID FROM C_BPartner_Location " +
											" WHERE C_BPartner_ID=? AND IsActive='Y' AND Cintolo_Shippment_Direction='Y' AND COALESCE(Cintolo_Shipper,0)>0", C_BPartner_ID);
					if(C_BPartner_Location_ID > 0) {
						MBPartnerLocation bpLocation = new MBPartnerLocation(Env.getCtx(), C_BPartner_Location_ID, null);
						location = new MLocation(Env.getCtx(), bpLocation.getC_Location_ID(), null);
					}
					
				}
				
				debug("getLocation. Toma el domicilio de destino final:" + location);
				
			}else
				debug("getLocation. No se encontro tipo de domicilio pedido/o.despacho/factura!");
		}
		
		if(!Util.isEmpty(situacionEntrega, true)) {
			// dREHER Feb '25 
			// Si retira el cliente, debe tomar el domicilio de la organizacion
			if(situacionEntrega.equals(MOrder.DELIVERYVIARULE_Pickup)) {
				
				int AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
				if(AD_Org_ID == 0)
					AD_Org_ID = getPercepcionData().getDocument().getOrgID();
				
				MOrgInfo oi = MOrgInfo.get(Env.getCtx(), AD_Org_ID);
				location = new MLocation(Env.getCtx(), oi.getC_Location_ID(), null);
				
				debug("getLocation. Como retira el cliente, debe tomar la localizacion de la organizacion:" + location);
			}
		}

		return location;
	}
	
	/**
	 * Busca si existe configuracion de alicuota percepcion cliente para la region indicada
	 * @param c_BPartner_ID
	 * @param c_Region_ID
	 * @return alicuota desde percepcion cliente
	 * @author dREHER
	 */
	public BigDecimal getAlicuotaFromEC(int c_BPartner_ID, int c_Region_ID) {
		BigDecimal alicuota = DB.getSQLValueBD(null, "SELECT alicuota FROM c_bpartner_percepcion WHERE C_BPartner_ID=" + c_BPartner_ID +
				" AND COALESCE(C_Region_ID,0)=? AND IsActive='Y'", c_Region_ID);
		debug("getAlicuotaFromEC. Busca alicuota en la entidad comercial/region:" + alicuota);
		
		return alicuota;
	}

}
