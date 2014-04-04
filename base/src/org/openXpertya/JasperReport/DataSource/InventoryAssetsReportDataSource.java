package org.openXpertya.JasperReport.DataSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.amortization.AbstractAmortizationLineData;
import org.openXpertya.amortization.AbstractAmortizationProcessor;
import org.openXpertya.amortization.AbstractAmortizationProcessorFactory;
import org.openXpertya.util.DisplayType;

public class InventoryAssetsReportDataSource implements OXPJasperDataSource {
	
	/** Contexto */
	private Properties ctx;
	
	/** Nombre de transacción */
	private String trxName;	
	
	/** Fecha del reporte */
	private Timestamp date;
	
	/** Procesador de amortización */
	private AbstractAmortizationProcessor amortizationProcessor;
	
	/** Total de registros */
	private Integer totalRecords;
	
	/** Nro de registro actual de iteración */
	private Integer currentNext;
	
	/** Registro actual */
	private AbstractAmortizationLineData currentAmortizationLineData;
	
	/** Registros */
	private List<AbstractAmortizationLineData> amortizationLinesData;
	
	/**
	 * Booleano que determina si el reporte debe estar agrupado por artículo,
	 * fecha de inicio y costo
	 */
	private boolean grouped;
	
	/** Utilizado para mapear los campos con las invocaciones de los metodos */
	private HashMap<String, String> methodMapper;
	
	public InventoryAssetsReportDataSource(Properties ctx, Timestamp date, boolean grouped, String trxName){
		setCtx(ctx);
		setDate(date);
		setTrxName(trxName);
		setGrouped(grouped);
		initialize();
	}
	
	protected void initialize(){
		setMethodMapper(new HashMap<String, String>());
		getMethodMapper().put("M_PRODUCT_LINES_ID", "getProductLinesID");
		getMethodMapper().put("M_PRODUCT_LINES_NAME", "getProductLinesName");
		
		getMethodMapper().put("M_PRODUCT_LINES_VALUE", "getProductValue");
		
		getMethodMapper().put("M_PRODUCT_GAMAS_ID", "getProductGamasID");
		getMethodMapper().put("M_PRODUCT_GAMAS_NAME", "getProductGamasName");
		getMethodMapper().put("M_PRODUCT_CATEGORY_ID", "getProductCategoryID");
		getMethodMapper().put("M_PRODUCT_CATEGORY_NAME", "getProductCategoryName");
		getMethodMapper().put("ASSET_ID", "getAttributeSetInstanceID");		
		getMethodMapper().put("QTY", "getQty");
		getMethodMapper().put("DATE_FROM", "getDateFrom");
		getMethodMapper().put("COST", "getUnitCost");
		getMethodMapper().put("YEARLIFE", "getYearLife");		
		getMethodMapper().put("ALTA_AMT", "getAlta");
		getMethodMapper().put("BAJA_AMT", "getBaja");
		getMethodMapper().put("TOTAL_AMT", "getTotalCost");		
		getMethodMapper().put("INITIAL_AMORTIZATION_AMT", "getAmortizationInitialAmt");
		getMethodMapper().put("AMORTIZATION_AMT", "getAmortizationAmt");
		getMethodMapper().put("END_AMORTIZATION_AMT", "getAmortizationEndAmt");
		getMethodMapper().put("INITIAL_RESIDUAL_AMT", "getResidualInitialAmt");
		getMethodMapper().put("END_RESIDUAL_AMT", "getResidualEndAmt");
	}
	
	@Override
	public void loadData() throws Exception {
		// Cargar los datos de la amortización
		AbstractAmortizationProcessor amortizationProcessor = AbstractAmortizationProcessorFactory
				.getProcessor(getCtx(), getDate(), getTrxName());
		amortizationProcessor.doAmortization();
		setAmortizationProcessor(amortizationProcessor);
		setAmortizationLinesData(amortizationProcessor.getAmortizationLines());
		// Realizar el agrupamiento si es necesario
		doGrouping();
		setTotalRecords(getAmortizationLinesData().size());
		setCurrentNext(-1);
	}

	/**
	 * Actualizar la lista de líneas de amortización si se debe agrupar
	 */
	protected void doGrouping(){
		if(isGrouped()){
			Map<String, AbstractAmortizationLineData> amortizationLinesGrouped = new HashMap<String, AbstractAmortizationLineData>();
			String key = null;
			AbstractAmortizationLineData auxLineData = null;
			for (AbstractAmortizationLineData amortizationLineData : getAmortizationLinesData()) {
				// Armo la clave de la hash
				key = amortizationLineData.getProductID()
						+ "_"
						+ DisplayType.getDateFormat().format(
								amortizationLineData.getAsset().getDateFrom())
						+ "_"
						+ DisplayType.getNumberFormat(DisplayType.Number)
								.format(amortizationLineData.getAsset()
										.getCost());
				auxLineData = amortizationLinesGrouped.get(key);
				if(auxLineData != null){
					auxLineData = addAmounts(auxLineData, amortizationLineData);
				}
				else{
					auxLineData = amortizationLineData;
				}
				amortizationLinesGrouped.put(key, auxLineData);
			}
			setAmortizationLinesData(null);
			setAmortizationLinesData(new ArrayList<AbstractAmortizationLineData>(
					amortizationLinesGrouped.values()));
		}
	}

	/**
	 * Suma los montos de una linea de amortización sobre otra y la retorna
	 * modificada
	 * 
	 * @param amortizationLineDataSrc
	 *            línea de amortización donde impactarán las sumas
	 * @param amortizationLineDataFrom
	 *            línea de amortización desde donde se obtienen los adicionales
	 * @return línea de amortización con las sumas de los montos
	 */
	protected AbstractAmortizationLineData addAmounts(AbstractAmortizationLineData amortizationLineDataSrc, AbstractAmortizationLineData amortizationLineDataFrom){
		amortizationLineDataSrc.setQty(amortizationLineDataSrc.getQty().add(
				amortizationLineDataFrom.getQty()));
		amortizationLineDataSrc.setAlta(amortizationLineDataSrc.getAlta().add(
				amortizationLineDataFrom.getAlta()));
		amortizationLineDataSrc.setAmortizationAmt(amortizationLineDataSrc
				.getAmortizationAmt().add(
						amortizationLineDataFrom.getAmortizationAmt()));
		amortizationLineDataSrc.setAmortizationEndAmt(amortizationLineDataSrc
				.getAmortizationEndAmt().add(
						amortizationLineDataFrom.getAmortizationEndAmt()));
		amortizationLineDataSrc
				.setAmortizationInitialAmt(amortizationLineDataSrc
						.getAmortizationInitialAmt().add(
								amortizationLineDataFrom
										.getAmortizationInitialAmt()));
		amortizationLineDataSrc.setBaja(amortizationLineDataSrc.getBaja().add(
				amortizationLineDataFrom.getBaja()));
		amortizationLineDataSrc.setResidualAmt(amortizationLineDataSrc
				.getResidualAmt()
				.add(amortizationLineDataFrom.getResidualAmt()));
		amortizationLineDataSrc.setResidualEndAmt(amortizationLineDataSrc
				.getResidualEndAmt().add(
						amortizationLineDataFrom.getResidualEndAmt()));
		amortizationLineDataSrc.setResidualInitialAmt(amortizationLineDataSrc
				.getResidualInitialAmt().add(
						amortizationLineDataFrom.getResidualInitialAmt()));
		return amortizationLineDataSrc;
	}
	
	@Override
	public boolean next() throws JRException {
		setCurrentNext(getCurrentNext()+1);
		
		if (getCurrentNext() >= getTotalRecords() )	{
			return false;
		}
		
		setCurrentAmortizationLineData(getAmortizationLinesData().get(getCurrentNext()));
		return true;
	}
	
	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		Object value = null;
		if(arg0.getName().equalsIgnoreCase("ASSET_NAME")){
			String name = getCurrentAmortizationLineData().getProductName();
			name = getCurrentAmortizationLineData().getProductName();
			if(!isGrouped()){
				name += " "+getCurrentAmortizationLineData().getAttributeSetInstanceName();
			}
			value = name;
		}
		else{
			String name = null;
			Class<?> clazz = null;
			Method method = null;
			try
			{
				// Invocar al metodo segun el campo correspondiente
				name = arg0.getName().toUpperCase();
			    clazz = Class.forName("org.openXpertya.amortization.AbstractAmortizationLineData");
			    method = clazz.getMethod(getMethodMapper().get(name));
			    value = (Object) method.invoke(getCurrentAmortizationLineData());
			}
			catch (ClassNotFoundException e) { 
				throw new JRException("No se ha podido obtener el valor del campo " + name); 
			}
			catch (NoSuchMethodException e) { 
				throw new JRException("No se ha podido invocar el metodo " + getMethodMapper().get(name)); 
			}
			catch (InvocationTargetException e) { 
				throw new JRException("Excepcion al invocar el método " + getMethodMapper().get(name)); 
			}
			catch (Exception e) { 
				throw new JRException("Excepcion general al acceder al campo " + name); 
			}
		}
		return value;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	protected String getTrxName() {
		return trxName;
	}

	protected void setDate(Timestamp date) {
		this.date = date;
	}

	protected Timestamp getDate() {
		return date;
	}

	protected void setAmortizationProcessor(AbstractAmortizationProcessor amortizationProcessor) {
		this.amortizationProcessor = amortizationProcessor;
	}

	protected AbstractAmortizationProcessor getAmortizationProcessor() {
		return amortizationProcessor;
	}

	protected void setCurrentNext(Integer currentNext) {
		this.currentNext = currentNext;
	}

	protected Integer getCurrentNext() {
		return currentNext;
	}

	protected void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	protected Integer getTotalRecords() {
		return totalRecords;
	}

	protected void setCurrentAmortizationLineData(
			AbstractAmortizationLineData currentAmortizationLineData) {
		this.currentAmortizationLineData = currentAmortizationLineData;
	}

	protected AbstractAmortizationLineData getCurrentAmortizationLineData() {
		return currentAmortizationLineData;
	}

	protected void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	protected boolean isGrouped() {
		return grouped;
	}

	public void setAmortizationLinesData(List<AbstractAmortizationLineData> amortizationLinesData) {
		this.amortizationLinesData = amortizationLinesData;
	}

	public List<AbstractAmortizationLineData> getAmortizationLinesData() {
		return amortizationLinesData;
	}

	protected void setMethodMapper(HashMap<String, String> methodMapper) {
		this.methodMapper = methodMapper;
	}

	protected HashMap<String, String> getMethodMapper() {
		return methodMapper;
	}
}
