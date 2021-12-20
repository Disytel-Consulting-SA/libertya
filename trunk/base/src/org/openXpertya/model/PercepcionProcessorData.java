package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Map;

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

	/** Utilizar Jurisdicción CABA. */
	private boolean isUseCABAJurisdiction;
	
	/**
	 * Documento relacionado, utilizado para créditos en devoluciones totales o
	 * parciales
	 */
	private IDocument relatedDocument = null;
		
	/**
	 * Flag que determina si estamos anulando, utilizado para créditos para
	 * determinar si es una devolución total o parcial
	 */
	private boolean isVoiding = false;
	
	/**
	 * Flag que determina si la config permite devoluciones parciales de
	 * percepciones
	 */
	private boolean allowPartialReturn;
	
	/**
	 * Flag que determina si la config permite devoluciones totales (por anulación)
	 * de percepciones
	 */
	private boolean allowTotalReturn;
	
	/** Escala de numeración para el cálculo */
	private int scale;
	
	/** Importe neto mínimo */
	private BigDecimal minimumNetAmt;
	
	/** Datos de configuración de importe neto mínimo por padrón */
	private Map<String, BigDecimal> minimumNetAmtByPadronType;
	
	/** Importe mínimo de percepción */
	private BigDecimal minimumPercepcionAmt;
	
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

	public boolean isUseCABAJurisdiction() {
		return isUseCABAJurisdiction;
	}

	public void setUseCABAJurisdiction(boolean isUseCABAJurisdiction) {
		this.isUseCABAJurisdiction = isUseCABAJurisdiction;
	}

	public IDocument getRelatedDocument() {
		return relatedDocument;
	}

	public void setRelatedDocument(IDocument relatedDocument) {
		this.relatedDocument = relatedDocument;
	}

	public boolean isVoiding() {
		return isVoiding;
	}

	public void setVoiding(boolean isVoiding) {
		this.isVoiding = isVoiding;
	}

	public boolean isAllowPartialReturn() {
		return allowPartialReturn;
	}

	public void setAllowPartialReturn(boolean allowPartialReturn) {
		this.allowPartialReturn = allowPartialReturn;
	}

	public boolean isAllowTotalReturn() {
		return allowTotalReturn;
	}

	public void setAllowTotalReturn(boolean allowTotalReturn) {
		this.allowTotalReturn = allowTotalReturn;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public BigDecimal getMinimumNetAmt() {
		return minimumNetAmt;
	}

	public void setMinimumNetAmt(BigDecimal minimumNetAmt) {
		this.minimumNetAmt = minimumNetAmt;
	}

	public Map<String, BigDecimal> getMinimumNetAmtByPadronType() {
		return minimumNetAmtByPadronType;
	}

	public void setMinimumNetAmtByPadronType(Map<String, BigDecimal> minimumNetAmtByPadronType) {
		this.minimumNetAmtByPadronType = minimumNetAmtByPadronType;
	}

	public BigDecimal getMinimumNetAmtBy(String padronType, boolean getConfigParent) {
		BigDecimal mna = getMinimumNetAmtByPadronType().get(padronType);
		if(mna == null && getConfigParent) {
			mna = getMinimumNetAmt();
		}
		return mna;
	}

	public BigDecimal getMinimumPercepcionAmt() {
		return minimumPercepcionAmt;
	}

	public void setMinimumPercepcionAmt(BigDecimal minimumPercepcionAmt) {
		this.minimumPercepcionAmt = minimumPercepcionAmt;
	}
}
