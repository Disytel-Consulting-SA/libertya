package org.openXpertya.amortization;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MAmortization;
import org.openXpertya.model.MAmortizationLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.util.AssetDTO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ITime;
import org.openXpertya.util.Util;

public abstract class AbstractAmortizationProcessor {
	
	/** Contexto */
	private Properties ctx;
	
	/** Nombre de la transacción actual */
	private String trxName;
	
	/** Lista de líneas de detalle de la amortización */
	private List<AbstractAmortizationLineData> amortizationLines;
	
	/** Amortización (Cabecera) */
	private MAmortization amortization;
	
	/** Amortización (Cabecera) anterior a la actual */
	private MAmortization beforeAmortization;
	
	/** Tiempo del período de la amortización */
	private ITime timePeriod;
	
	/** Fecha final del cálculo de la amortización */
	private Date endAmortizationDate;
	
	/** Fecha del proceso */
	private Date processDate;
	
	// Constructores
	
	public AbstractAmortizationProcessor(Properties ctx, String trxName) {
		setCtx(ctx);
		setTrxName(trxName);
		setAmortizationLines(new ArrayList<AbstractAmortizationLineData>());
	}
	
	public AbstractAmortizationProcessor(Properties ctx, MAmortization amortization, String trxName) {
		this(ctx,trxName);
		setAmortization(amortization);
		initializeAmortizationData();
	}
	
	public AbstractAmortizationProcessor(Properties ctx, Timestamp amortizationDate, String trxName) {
		this(ctx,trxName);
		setProcessDate(amortizationDate);
		initializeFromDate();
		initializeAmortizationData();
	}
	
	public AbstractAmortizationProcessor(Properties ctx, Integer amortizationID, String trxName) {
		this(ctx,new MAmortization(ctx, amortizationID, trxName),trxName);
	}

	/**
	 * Inicialización de datos en base a la fecha de amortización y de
	 * procesamiento
	 */
	protected void initializeFromDate(){
		setAmortization(MAmortization.get(getCtx(), null, new Timestamp(
				getProcessDate().getTime()), getTrxName()));
		initializeAmortizationData();
	}
	
	/**
	 * Inicialización de datos relativos a la amortización
	 */
	protected void initializeAmortizationData(){
		// Período de tiempo de la amortización actual
		// Si la fecha de procesamiento es null, significa que estamos vía una
		// amortización, por lo tanto la determino por ella, sino por la fecha
		// de procesamiento
		if(getProcessDate() == null){
			setTimePeriod(MAmortization
					.getITime(getCtx(),
							MAmortization.getPeriodValueID(getAmortization()),
							getTrxName()));
			setProcessDate(getTimePeriod().getDateTo());
		}
		else{
			setTimePeriod(MAmortization.getITime(getCtx(), getProcessDate(),
					getTrxName()));
		}
		// Fecha de fin del período de la amortización actual
		setEndAmortizationDate(getTimePeriod().getDateTo());
		// Amortización anterior a la actual
		setBeforeAmortization(MAmortization.get(getCtx(), getTimePeriod(), -1,
				getTrxName()));
		// Carga inicial de las líneas de amortización
		doLoading();
	}
	
	/**
	 * Realiza la carga inicial de  
	 */
	protected void doLoading(){
		// Pre-load amortization en la subclase
		preLoadAmortizationLinesData();
		// Load
		loadAmortizationLinesData();
		// Post-load amortization en la subclase
		postLoadAmortizationLinesData();
	}
	
	/**
	 * Carga de los DTO de las líneas a amortizar
	 */
	protected void loadAmortizationLinesData(){
		boolean existsBeforeAmortization = getBeforeAmortization() != null;
		// Consulta para obtener los bienes de uso que contienen instancias
		StringBuffer sql = new StringBuffer("select distinct p.m_product_id, p.product_name, p.product_value, category_id as m_product_category_id, p.category_name, gamas_id as m_product_gamas_id, p.gamas_name, lines_id as m_product_lines_id, p.lines_name, instances.m_attributesetinstance_id, instances.attributesetinstancename, p.m_attributeset_id, product_life as yearlife, product_amortizationperc as amortizationperc ");
		if(existsBeforeAmortization){
			sql.append(", m_amortizationline_id ");
		}
		sql.append("from (SELECT (CASE coalesce(iol.m_product_id,0) WHEN 0 THEN il.m_product_id ELSE iol.m_product_id END) as m_product_id, masi.m_attributesetinstance_id, masi.description as attributesetinstancename, masi.m_attributeset_id ");
		if(existsBeforeAmortization){
			sql.append(", m_amortizationline_id ");
		}
		sql.append("from m_attributesetinstance as masi " +
					"left join m_inoutline as iol ON iol.m_attributesetinstance_id = masi.m_attributesetinstance_id " +
					"left join m_inventoryline as il ON il.m_attributesetinstance_id = masi.m_attributesetinstance_id ");
		if(existsBeforeAmortization){
			sql.append(" left join (select * from m_amortizationline as al where al.m_amortization_id = "+getBeforeAmortization().getID()+") as al ON masi.m_attributesetinstance_id = al.m_attributesetinstance_id ");
		}
		sql.append(" where (masi.m_attributesetinstance_id is not null AND masi.m_attributesetinstance_id <> 0) AND masi.ad_client_id = "+Env.getAD_Client_ID(getCtx())+") as instances ");
		sql.append("inner join (select *, p.name as product_name, p.value AS product_value, pc.m_product_category_id as category_id, pc.name as category_name, pg.m_product_gamas_id as gamas_id,pg.name as gamas_name, pl.m_product_lines_id as lines_id, pl.name as lines_name, p.yearlife as product_life,p.amortizationperc as product_amortizationperc  " +
								"from m_product as p " +
								"inner join m_product_category as pc on pc.m_product_category_id = p.m_product_category_id " +
								"left join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id " +
								"left join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id) as p ON p.m_product_id = instances.m_product_id " +
				"where p.producttype = '"+MProduct.PRODUCTTYPE_Assets+"' AND (instances.m_product_id is not null OR instances.m_product_id <> 0) " +
				"order by lines_id, gamas_id, category_id, p.m_product_id, instances.m_attributesetinstance_id");
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<AbstractAmortizationLineData> amortizationLinesData = new ArrayList<AbstractAmortizationLineData>();
		AbstractAmortizationLineData amortizationLineData = null;
		try {
			ps = DB.prepareStatement(sql.toString(), getTrxName());
			rs = ps.executeQuery();
			while (rs.next()) {
				amortizationLineData = getAmortizationLineData();
				// Pre-inicialización de clase con la data
				amortizationLineData = preLoadAmortizationLineData(amortizationLineData, rs);
				// Inicialización propia las clases con la data
				amortizationLineData
						.setAmortizationID(getAmortization() != null ? getAmortization()
								.getID() : 0);
				amortizationLineData.setProductID(rs.getInt("m_product_id"));
				amortizationLineData.setProductName(rs.getString("product_name"));
				amortizationLineData.setProductValue(rs.getString("product_value"));
				amortizationLineData.setProductCategoryID(rs.getInt("m_product_category_id"));
				amortizationLineData.setProductCategoryName(rs.getString("category_name"));
				amortizationLineData.setProductGamasID(rs.getInt("m_product_gamas_id"));
				amortizationLineData.setProductGamasName(rs.getString("gamas_name"));
				amortizationLineData.setProductLinesID(rs.getInt("m_product_lines_id"));
				amortizationLineData.setProductLinesName(rs.getString("lines_name"));
				amortizationLineData.setAttributeSetInstanceID(rs.getInt("m_attributesetinstance_id"));
				amortizationLineData.setAttributeSetInstanceName(rs.getString("attributeSetInstanceName"));
				amortizationLineData.setAttributeSetID(rs.getInt("m_attributeset_id"));
				amortizationLineData.setYearLife(rs.getInt("yearlife"));
				// Línea de Amortización anterior
				if (existsBeforeAmortization
						&& !Util.isEmpty(rs.getInt("m_amortizationline_id"),
								true)) {
					amortizationLineData
							.setBeforeAmortizationLine(new MAmortizationLine(
									getCtx(), rs
											.getInt("m_amortizationline_id"),
									getTrxName()));
				}
				// Obtener el costo, fecha de alta y fecha de baja dentro del bien de uso
				amortizationLineData.setQty(new BigDecimal(1));
				amortizationLineData.setAsset(AssetDTO.createFrom(getCtx(),
						amortizationLineData, getTrxName()));
				// Post-inicialización de clase con la data
				postLoadAmortizationLineData(amortizationLineData, rs);
				amortizationLinesData.add(amortizationLineData);
			}
			setAmortizationLines(amortizationLinesData);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(rs != null){
					rs.close();
					rs = null;
				}
			} catch(Exception e2){
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * Resetea y limpia variables, entre otras operaciones de limpieza
	 */
	public void reset(){
		// Limpieza local
		setAmortization(null);
		setBeforeAmortization(null);
		setEndAmortizationDate(null);
		setCtx(null);
		setTimePeriod(null);
		setTrxName(null);
		setAmortizationLines(null);
		// Limpieza de cada subclase
		clear();
	}

	/**
	 * Pre-carga de las líneas de amortización que implementa cada subclase
	 */
	protected abstract void preLoadAmortizationLinesData();
	
	/**
	 * Post-carga de las líneas de amortización que implementa cada subclase
	 */
	protected abstract void postLoadAmortizationLinesData();

	/**
	 * Pre-carga de cada línea de amortización que implementa cada subclase
	 * 
	 * @param amortizationLineData
	 *            línea de amortización actual
	 * @param rs
	 *            result set actual con el resultado de la consulta
	 * @return mismo dato parámetro pero modificado
	 */
	protected abstract AbstractAmortizationLineData preLoadAmortizationLineData(AbstractAmortizationLineData amortizationLineData, ResultSet rs) throws Exception;
	
	/**
	 * Post-carga de cada línea de amortización que implementa cada subclase
	 * 
	 * @param amortizationLineData
	 *            línea de amortización actual
	 * @param rs
	 *            result set actual con el resultado de la consulta
	 * @return mismo dato parámetro pero modificado
	 */
	protected abstract AbstractAmortizationLineData postLoadAmortizationLineData(AbstractAmortizationLineData amortizationLineData, ResultSet rs) throws Exception;
	
	/**
	 * Método que permite obtener datos de la amortización de cada subclase
	 * 
	 * @return clase con información de detalle de amortizaciones
	 */
	protected abstract AbstractAmortizationLineData getAmortizationLineData();
	
	/**
	 * Método que se ejecuta para que se realice la amortización
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	public abstract void doAmortization() throws Exception;

	/**
	 * Método que debe implementar cada subclase para limpiar variables y demás
	 * almacenamientos
	 */
	protected abstract void clear();
	
	// Getters y setters
	
	public void setAmortization(MAmortization amortization) {
		this.amortization = amortization;
	}

	public MAmortization getAmortization() {
		return amortization;
	}

	public void setAmortizationLines(List<AbstractAmortizationLineData> amortizationLines) {
		this.amortizationLines = amortizationLines;
	}

	public List<AbstractAmortizationLineData> getAmortizationLines() {
		return amortizationLines;
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

	protected void setTimePeriod(ITime timePeriod) {
		this.timePeriod = timePeriod;
	}

	protected ITime getTimePeriod() {
		return timePeriod;
	}

	public void setEndAmortizationDate(Date endAmortizationDate) {
		this.endAmortizationDate = endAmortizationDate;
	}

	public Date getEndAmortizationDate() {
		return endAmortizationDate;
	}

	protected void setBeforeAmortization(MAmortization beforeAmortization) {
		this.beforeAmortization = beforeAmortization;
	}

	protected MAmortization getBeforeAmortization() {
		return beforeAmortization;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public Date getProcessDate() {
		return processDate;
	}	
}
