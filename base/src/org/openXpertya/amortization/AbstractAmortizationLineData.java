package org.openXpertya.amortization;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.model.MAmortizationLine;
import org.openXpertya.util.AssetDTO;
import org.openXpertya.util.Env;

public class AbstractAmortizationLineData {

	/** ID del artículo */
	private Integer productID;
	
	/** Nombre del artículo */
	private String productName;
	
	/** ID de la instancia del conjunto de atributos */
	private Integer attributeSetInstanceID;
	
	/** Descripción de la instancia de atributos */
	private String attributeSetInstanceName;
	
	/** ID de la subfamilia */
	private Integer productCategoryID;	
	
	/** Nombre de la subfamilia */
	private String productCategoryName;
	
	/** ID de la familia */
	private Integer productGamasID;
	
	/** Nombre de la familia */
	private String productGamasName;
	
	/** ID de la línea de artículo */
	private Integer productLinesID;
	
	/** Nombre de la línea de artículo */
	private String productLinesName;
	
	/** Amortización anterior para este bien de uso */
	private MAmortizationLine beforeAmortizationLine;
	
	/** ID de amortización */
	private Integer amortizationID;
	
	/** Monto de amortización */
	private BigDecimal amortizationAmt;
	
	/** Monto de amortización inicial del período */
	private BigDecimal amortizationInitialAmt;
	
	/** Monto de amortización final del período */
	private BigDecimal amortizationEndAmt;
	
	/** Valor residual */
	private BigDecimal residualAmt;
	
	/** Valor residual inicial del período */
	private BigDecimal residualInitialAmt;
	
	/** Valor residual final del período */
	private BigDecimal residualEndAmt;
	
	/** Años de vida útil */
	private Integer yearLife;
	
	/** Asset con datos de los atributos de la instancia */
	private AssetDTO asset;
	
	/** Costo unitario */
	private BigDecimal unitCost;
	
	/** Cantidad */
	private BigDecimal qty;
	
	/** Costo total = unitCost * qty */
	private BigDecimal totalCost;
	
	/** ID del conjunto de atributos */
	private Integer attributeSetID;
	
	/**
	 * Este monto es positivo cuando el bien entró como alta en este período. El
	 * monto que posee es el total = costo * qty.
	 */
	private BigDecimal alta;
	
	/**
	 * Este monto es positivo cuando el bien salió como baja en este período. El
	 * monto que posee es el total = costo * qty.
	 */
	private BigDecimal baja;
	
	public AbstractAmortizationLineData() {
		setQty(Env.ONE);
		setUnitCost(BigDecimal.ZERO);
		setAlta(BigDecimal.ZERO);
		setBaja(BigDecimal.ZERO);
	}

	public void setProductID(Integer productID) {
		this.productID = productID;
	}

	public Integer getProductID() {
		return productID;
	}

	public void setAttributeSetInstanceID(Integer attributeSetInstanceID) {
		this.attributeSetInstanceID = attributeSetInstanceID;
	}

	public Integer getAttributeSetInstanceID() {
		return attributeSetInstanceID;
	}

	public void setBeforeAmortizationLine(MAmortizationLine beforeAmortizationLine) {
		this.beforeAmortizationLine = beforeAmortizationLine;
	}

	public MAmortizationLine getBeforeAmortizationLine() {
		return beforeAmortizationLine;
	}

	public void setAmortizationID(Integer amortizationID) {
		this.amortizationID = amortizationID;
	}

	public Integer getAmortizationID() {
		return amortizationID;
	}

	public void setAmortizationAmt(BigDecimal amortizationAmt) {
		this.amortizationAmt = amortizationAmt;
	}

	public BigDecimal getAmortizationAmt() {
		return amortizationAmt;
	}

	public void setAmortizationInitialAmt(BigDecimal amortizationInitialAmt) {
		this.amortizationInitialAmt = amortizationInitialAmt;
	}

	public BigDecimal getAmortizationInitialAmt() {
		return amortizationInitialAmt;
	}

	public void setAmortizationEndAmt(BigDecimal amortizationEndAmt) {
		this.amortizationEndAmt = amortizationEndAmt;
	}

	public BigDecimal getAmortizationEndAmt() {
		return amortizationEndAmt;
	}

	public void setResidualAmt(BigDecimal residualAmt) {
		this.residualAmt = residualAmt;
	}

	public BigDecimal getResidualAmt() {
		return residualAmt;
	}

	public void setResidualInitialAmt(BigDecimal residualInitialAmt) {
		this.residualInitialAmt = residualInitialAmt;
	}

	public BigDecimal getResidualInitialAmt() {
		return residualInitialAmt;
	}

	public void setResidualEndAmt(BigDecimal residualEndAmt) {
		this.residualEndAmt = residualEndAmt;
	}

	public BigDecimal getResidualEndAmt() {
		return residualEndAmt;
	}

	public void setYearLife(Integer yearLife) {
		this.yearLife = yearLife;
	}

	public Integer getYearLife() {
		return yearLife;
	}

	public void setAsset(AssetDTO asset) {
		this.asset = asset;
		setUnitCost(asset.getCost());
	}

	public AssetDTO getAsset() {
		return asset;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
		setTotalCost(getQty() != null && getUnitCost() != null ? getUnitCost()
				.multiply(getQty()) : BigDecimal.ZERO);
	}

	public BigDecimal getUnitCost() {
		return unitCost;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
		setTotalCost(getQty() != null && getUnitCost() != null ? getUnitCost()
				.multiply(getQty()) : BigDecimal.ZERO);
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setAttributeSetID(Integer attributeSetID) {
		this.attributeSetID = attributeSetID;
	}

	public Integer getAttributeSetID() {
		return attributeSetID;
	}

	public void setAlta(BigDecimal alta) {
		this.alta = alta;
	}

	public BigDecimal getAlta() {
		return alta;
	}

	public void setBaja(BigDecimal baja) {
		this.baja = baja;
	}

	public BigDecimal getBaja() {
		return baja;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return productName;
	}

	public void setAttributeSetInstanceName(String attributeSetInstanceName) {
		this.attributeSetInstanceName = attributeSetInstanceName;
	}

	public String getAttributeSetInstanceName() {
		return attributeSetInstanceName;
	}

	public void setProductCategoryID(Integer productCategoryID) {
		this.productCategoryID = productCategoryID;
	}

	public Integer getProductCategoryID() {
		return productCategoryID;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductGamasID(Integer productGamasID) {
		this.productGamasID = productGamasID;
	}

	public Integer getProductGamasID() {
		return productGamasID;
	}

	public void setProductGamasName(String productGamasName) {
		this.productGamasName = productGamasName;
	}

	public String getProductGamasName() {
		return productGamasName;
	}

	public void setProductLinesID(Integer productLinesID) {
		this.productLinesID = productLinesID;
	}

	public Integer getProductLinesID() {
		return productLinesID;
	}

	public void setProductLinesName(String productLinesName) {
		this.productLinesName = productLinesName;
	}

	public String getProductLinesName() {
		return productLinesName;
	}

	public Timestamp getDateFrom(){
		return getAsset().getDateFrom();
	}
	
	public Timestamp getDateTo(){
		return getAsset().getDateTo();
	}
}
