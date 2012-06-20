package org.openXpertya.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.amortization.AbstractAmortizationLineData;

public class AssetDTO {

	/**
	 * Creo el asset dto a partir de datos de una línea de amortización
	 * 
	 * @param ctx
	 *            contexto actual
	 * @param amortizationLineData
	 *            datos de línea de amortización
	 * @param trxName
	 *            transacción actual
	 * @return asset creado
	 * @throws Exception
	 *             en caso de error
	 */
	public static AssetDTO createFrom(Properties ctx, AbstractAmortizationLineData amortizationLineData, String trxName) throws Exception{
		String sql = "SELECT ai.m_attributesetinstance_id, ai.m_attribute_id, seqno, serno, value, valuenumber, valuedate " +
					 "FROM m_attributeinstance as ai " +
					 "INNER JOIN m_attributesetinstance as asi ON asi.m_attributesetinstance_id = ai.m_attributesetinstance_id " +
					 "INNER JOIN m_attributeuse as au ON ai.m_attribute_id = au.m_attribute_id " +
					 "WHERE asi.m_attributesetinstance_id = ? " +
					 "ORDER BY seqno";
		PreparedStatement ps = DB.prepareStatement(sql, trxName);
		ps.setInt(1, amortizationLineData.getAttributeSetInstanceID());
		ResultSet rs = ps.executeQuery();
		AssetDTO asset = new AssetDTO();
		asset.setProductID(amortizationLineData.getProductID());
		asset.setAttributeSetInstanceID(amortizationLineData.getAttributeSetInstanceID());
		asset.setCtx(ctx);
		asset.setTrxName(trxName);
		while (rs.next()) {
			asset.setValueFromSeqNo(rs.getInt("seqno"),
					rs.getBigDecimal("valuenumber"),
					rs.getTimestamp("valuedate"));
			asset.setSerialNo(rs.getString("serno"));
		}
		rs.close();
		ps.close();
		rs = null;
		ps = null;
		return asset;
	}
	
	
	// Variables de instancia
	
	/** Fecha de alta */
	private Timestamp dateFrom;
	
	/** Fecha de baja */
	private Timestamp dateTo;
	
	/** Costo */
	private BigDecimal cost;
	
	/** Nro de serie */
	private String serialNo;
	
	/** ID del artículo */
	private Integer productID;
	
	/** Contexto actual */
	private Properties ctx;
	
	/** Transacción actual */
	private String trxName;
	
	/** Instancia del conjunto de atributos */
	private Integer attributeSetInstanceID = 0;
	
	public AssetDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Obtengo el valor del bien de uso dependiendo del nro de secuencia del
	 * atributo
	 * 
	 * @param seqNo
	 *            nro de secuencia del atributo
	 * @return valor a partir del nro de secuencia
	 */
	public Object getValueFromSeqNo(Integer seqNo){
		Object value = null;
		switch (seqNo) {
		case 10:
			value = getDateFrom();
			break;
		case 20:
			value = getDateTo();
			break;
		case 30:
			value = getCost();
			break;
		default:
			value = null;
			break;
		}
		return value;
	}

	/**
	 * Setea la variable de instancia con el valor a partir del nro de secuencia
	 * del uso del atributo
	 * 
	 * @param seqNo
	 *            nro de secuencia del atributo
	 * @param valueNumber
	 *            valor numérico de la instancia
	 * @param valueDate
	 *            valor en fecha de la instancia
	 */
	public void setValueFromSeqNo(Integer seqNo, BigDecimal valueNumber, Timestamp valueDate){
		switch (seqNo) {
		case 10:
			setDateFrom(valueDate);
			break;
		case 20:
			setDateTo(valueDate);
			break;
		case 30:
			setCost(valueNumber);
			break;
		}
	}
	
	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setProductID(Integer productID) {
		this.productID = productID;
	}

	public Integer getProductID() {
		return productID;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setAttributeSetInstanceID(Integer attributeSetInstanceID) {
		this.attributeSetInstanceID = attributeSetInstanceID;
	}

	public Integer getAttributeSetInstanceID() {
		return attributeSetInstanceID;
	}	
}
