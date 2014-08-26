package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.model.DiscountCalculator.IDocument;

public class PercepcionProcessorData {

	/** Documento */
	private IDocument document;
	
	/** Entidad Comercial */
	private MBPartner bpartner;
	
	/** Categoría de Impuesto */
	private MCategoriaIva categoriaIVA;
	
	/** Impuesto */
	private MTax tax;
	
	/** Alicuota de la config de la percepción */
	private BigDecimal alicuota;
	
	/** Es Convenio Multilateral */
	private boolean isConvenioMultilateral;
	
	public PercepcionProcessorData() {
		
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public MBPartner getBpartner() {
		return bpartner;
	}

	public void setBpartner(MBPartner bpartner) {
		this.bpartner = bpartner;
	}

	public MCategoriaIva getCategoriaIVA() {
		return categoriaIVA;
	}

	public void setCategoriaIVA(MCategoriaIva categoriaIVA) {
		this.categoriaIVA = categoriaIVA;
	}

	public MTax getTax() {
		return tax;
	}

	public void setTax(MTax tax) {
		this.tax = tax;
	}

	public BigDecimal getAlicuota() {
		return alicuota;
	}

	public void setAlicuota(BigDecimal alicuota) {
		this.alicuota = alicuota;
	}

	public boolean isConvenioMultilateral() {
		return isConvenioMultilateral;
	}

	public void setConvenioMultilateral(boolean isConvenioMultilateral) {
		this.isConvenioMultilateral = isConvenioMultilateral;
	}

}
