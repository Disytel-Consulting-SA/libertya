package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.Util;

public class PercepcionStandard extends AbstractPercepcionProcessor {

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
	 * Aplica y devuelve la percepción de este procesador
	 * 
	 * @param rate       tasa a aplicar
	 * @param taxBaseAmt importe base
	 * @param minimumAmt importe mínimo a comparar
	 * @return percepción aplicada
	 */
	protected Percepcion getApplyRate(BigDecimal rate, BigDecimal taxBaseAmt, BigDecimal minimumAmt) {
		Percepcion p = null;
		if(!Util.isEmpty(rate, true)) {
			if(Util.isEmpty(minimumAmt, true) || taxBaseAmt.compareTo(minimumAmt) >= 0) {
				BigDecimal taxAmt = MTax.calculateTax(taxBaseAmt, false, rate, getPercepcionData().getScale());
				p = new Percepcion();
				p.setTaxRate(rate);
				p.setTaxBaseAmt(taxBaseAmt);
				p.setTaxAmt(taxAmt);
				p.setTaxID(getPercepcionData().getTax().getID());
				System.out.print(p.getTaxAmt());
			}
		}
		// Controlar que el importe determinar (importe a percibir) sea mayor al importe
		// mínimo de percepción
		if(p != null && p.getTaxAmt().compareTo(getPercepcionData().getMinimumPercepcionAmt()) <= 0) {
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
		// Calcular la percepción
		return getApplyRate(perc);
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

}
