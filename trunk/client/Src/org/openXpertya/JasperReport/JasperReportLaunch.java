package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Clase abstracta que contiene funcionalidad de ayuda para la creación e instanciación
 * de reportes Jasper.<br>
 * <ul>
 * 	<li>Implementa el método <code>prepare()</code> para obtener todos los parámetros del
 *      proceso y colocarlos en una Map, para luego ser accesibles mediante <code>getParameterValue(name)</code>
 *      por las subclases.
 *  </li>
 *  <li>Carga la instancia de <code>MJasperReport</code> según la configuración del proceso.</li> 
 *  <li>Invoca el método <code>loadReportParameters()</code> en el cual las subclases cargan
 *      los parámetros específicos del reporte</li>
 *  <li>Invoca el método <code>createReportDataSource()</code> para obtener la instancia del Data Source
 *      específica de la subclase</li>    
 *  </ul>    
 * @author Franco Bonafine - Disytel
 * @date 11/12/2008
 */
public abstract class JasperReportLaunch extends SvrProcess {

	/** Parámetros del proceso OXP */
	private Map<String, Object> parametersValues;
	/** Dato Info de los Parámetros del proceso OXP */
	private Map<String, String> parametersInfo;
	/** Wrapper del Jasper Report relacionado con el Proceso OXP 
	 * Este objeto es instanciado a partir del metadato JasperReport configurado
	 * en el informe y proceso, dentro del método prepare() */
	private MJasperReport reportWrapper;
	
	/**
	 * Obtiene y agrega todos los parámetros que necesita el reporte.
	 * @throws Exception En caso de producirse algún error en la carga de algún parámetro
	 * que no permita continuar con la presentación del reporte. La excepción corta la 
	 * ejecución del reporte y este no será mostrado.
	 */
	protected abstract void loadReportParameters() throws Exception;
	
	/**
	 * Crea la instancia del Data Source que utilizará el reporte. 
	 * @return Devuelve la instancia del DataSource creada.
	 */
	protected abstract OXPJasperDataSource createReportDataSource();
	
	@Override
	protected void prepare() {
		// Se crea el Map de parámetros
		parametersValues = new HashMap<String, Object>();
		setParametersInfo(new HashMap<String, String>());
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName().toUpperCase();
			// Valores del parámetro
			Object value = para[i].getParameter();
			// OXP retorna los enteros como BigDecimal. Para los campos que son IDs
			// se cambia el valor a un entero.
			if (name.endsWith("_ID"))
				value = ((BigDecimal)value).intValue();
			
			// Se guarda el valor del parámetro.
			getParametersValues().put(name , value);
			// Se obtiene el valor de fin de rango para determinar si el parámetro es un rango.
			Object value_to = para[i].getParameter_To();
			// Si es un rango, se guardan el valor del fin del rango (concatenando un _TO al nombre). 
			if (value_to != null) 
				getParametersValues().put(name + "_TO", value_to);
			// Guardar el dato info del parámetro y del parámetro TO
			getParametersInfo().put(name, para[i].getInfo());
			if(!Util.isEmpty(para[i].getInfo_To())){
				getParametersInfo().put(name + "_TO", para[i].getInfo_To());
			}
		}
		// Se carga la instancia del wrapper del reporte jasper.
		loadReportWrapper();
	}
	
	@Override
	protected String doIt() throws Exception {
		// Se cargan los parámetros específicos del reporte.
		loadReportParameters();
		// Se crea el data source para el reporte y se cargan los datos del mismo.
		OXPJasperDataSource dataSource = createReportDataSource();
		dataSource.loadData();
		// Se rellena el reporte con el data source y se muestra.
		try {
			getReportWrapper().fillReport(dataSource);
			getReportWrapper().showReport(getProcessInfo());
		} catch (RuntimeException e) {
			throw new Exception ("@JasperReportFillError@", e);
		}
		return null;
	}

	/**
	 * @return the parametersValues
	 */
	protected Map<String, Object> getParametersValues() {
		return parametersValues;
	}
	
	/**
	 * Obtiene el valor de un parámetro del proceso.<br>
	 * Para los parámetros que son Rangos, los nombres que se deben utilizar para obtener los
	 * valores del rango son:<br>
	 * <ul>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]     : para el valor inicial del rango.</li>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]_TO  : para el valor final del rango. </li>
	 * </ul>
	 * donde [NOMBRE_PARAMETRO_PROCESO] es el nombre definido en los metadatos del parámetro
	 * del proceso.<br>
	 * Ej:<ul> 
	 * <li>"DateTrx" (limite inferior del rango)</li>
	 * <li>"DateTrx_To" (limite superior del rango)</li>
	 * </ul>
	 * Este método no es sensible a mayúsculas y minúsculas.
	 * @param name Nombre del parámetro.
	 * @param defaultValue Valor retornado en caso de que el parámetro no exista.
	 * @return El valor de parámetro en caso de ser distinto de <code>null</code>, o
	 * el <code>defaultValue</code> en caso contrario.
	 */
	protected Object getParameterValue(String name, Object defaultValue) {
		Object value = getParametersValues().get(name.toUpperCase());
		return value != null ? value : defaultValue; 
	}
	
	/**
	 * Obtiene el valor de un parámetro del proceso.<br>
	 * Para los parámetros que son Rangos, los nombres que se deben utilizar para obtener los
	 * valores del rango son:<br>
	 * <ul>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]_FROM: para el valor inicial del rango.</li>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]_TO  : para el valor final del rango. </li>
	 * </ul>
	 * donde [NOMBRE_PARAMETRO_PROCESO] es el nombre definido en los metadatos del parámetro
	 * del proceso.<br>
	 * Ej: DateTrx_From, DateTrx_To<br>
	 * Este método no es sensible a mayúsculas y minúsculas.
	 * @param name Nombre del parámetro.
	 * @return El valor de parámetro indicado, o <code>null</code> en caso de que
	 * el parámetro no exista.
	 */
	protected Object getParameterValue(String name) {
		return getParameterValue(name, null); 
	}
	
	/**
	 * Crea la instancia de MJasperReport según la configuración del proceso OXP.
	 */
	private void loadReportWrapper() {
		// Se obtiene el informe y proceso
		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		// Si no es un informe jasper entonces no se crea nada. (esto no debería suceder)
		if(proceso.isJasperReport() != true)
			return;
		// Se instancia el MJasperReport a partir del ID configurado en el proceso.
		int jasperReportID = proceso.getAD_JasperReport_ID();
		setReportWrapper(new MJasperReport(getCtx(), jasperReportID, get_TrxName()));
	}

	/**
	 * @return the reportWrapper
	 */
	protected MJasperReport getReportWrapper() {
		if (reportWrapper == null)
			loadReportWrapper();
		return reportWrapper;
	}

	/**
	 * @param reportWrapper the reportWrapper to set
	 */
	private void setReportWrapper(MJasperReport reportWrapper) {
		this.reportWrapper = reportWrapper;
	}
	
	/**
	 * Agrega un parámetro al reporte Jasper.
	 * @param name Nombre del parámetro.
	 * @param value Valor del parámetro.
	 */
	protected void addReportParameter(String name, Object value) {
		getReportWrapper().addParameter(name, value);
	}

	/**
	 * Obtiene el dato info de un parámetro del proceso.<br>
	 * Para los parámetros que son Rangos, los nombres que se deben utilizar para obtener los
	 * valores del rango son:<br>
	 * <ul>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]     : para el valor inicial del rango.</li>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]_TO  : para el valor final del rango. </li>
	 * </ul>
	 * donde [NOMBRE_PARAMETRO_PROCESO] es el nombre definido en los metadatos del parámetro
	 * del proceso.<br>
	 * Ej:<ul> 
	 * <li>"DateTrx" (limite inferior del rango)</li>
	 * <li>"DateTrx_To" (limite superior del rango)</li>
	 * </ul>
	 * Este método no es sensible a mayúsculas y minúsculas.
	 * @param name Nombre del parámetro.
	 * @param defaultValue Valor retornado en caso de que el parámetro no exista.
	 * @return El info de parámetro en caso de ser distinto de <code>null</code>, o
	 * el <code>defaultValue</code> en caso contrario.
	 */
	protected String getParameterInfo(String name, String defaultInfo) {
		String info = getParametersInfo().get(name.toUpperCase());
		return info != null ? info : defaultInfo; 
	}
	
	/**
	 * Obtiene el dato info de un parámetro del proceso.<br>
	 * Para los parámetros que son Rangos, los nombres que se deben utilizar para obtener los
	 * valores del rango son:<br>
	 * <ul>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]_FROM: para el valor inicial del rango.</li>
	 * <li>[NOMBRE_PARAMETRO_PROCESO]_TO  : para el valor final del rango. </li>
	 * </ul>
	 * donde [NOMBRE_PARAMETRO_PROCESO] es el nombre definido en los metadatos del parámetro
	 * del proceso.<br>
	 * Ej: DateTrx_From, DateTrx_To<br>
	 * Este método no es sensible a mayúsculas y minúsculas.
	 * @param name Nombre del parámetro.
	 * @return El info del parámetro indicado, o <code>null</code> en caso de que
	 * el parámetro no exista.
	 */
	protected String getParameterInfo(String name) {
		return getParameterInfo(name, null); 
	}
	
	/**
	 * @return Retorna el MJasperReport con el nombre indicado.
	 */
	protected MJasperReport getJasperReport(Properties ctx, String name, String trxName) throws Exception {
		Integer jasperReport_ID = 
			(Integer)DB.getSQLObject(get_TrxName(), "SELECT AD_JasperReport_ID FROM AD_JasperReport WHERE Name ilike ?", new Object[] { name });
		if(jasperReport_ID == null || jasperReport_ID == 0)
			throw new Exception("Jasper Report "+name+" not found");
		
		MJasperReport jasperReport = new MJasperReport(ctx, jasperReport_ID, get_TrxName());
		return jasperReport;
	}
	
	
	protected void setParametersInfo(Map<String, String> parametersInfo) {
		this.parametersInfo = parametersInfo;
	}

	protected Map<String, String> getParametersInfo() {
		return parametersInfo;
	}
}
