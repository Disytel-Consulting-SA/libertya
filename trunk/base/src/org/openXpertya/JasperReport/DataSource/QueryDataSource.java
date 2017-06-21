package org.openXpertya.JasperReport.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.util.DB;

/**
 * Data Source que carga los datos del reporte a partir de una consulta SQL.
 * @author Franco Bonafine - Disytel
 * @date 12/12/2008
 */
public abstract class QueryDataSource implements OXPJasperDataSource {

	/** Lista de registros resultantes de la consulta. Cada registro es representado
	 * por una Map que como clave contiene el nombre de la columna, y como valor
	 * el valor propiamente dicho */
	private List<Map<String, Object>> records = new ArrayList<Map<String,Object>>();
	/** Registro Actual */
	private int currentRecordIndex = -1; // -1 porque lo primero que se hace es un ++
	/** Nombre de la transacción de BD sobre la cual se efectua la consulta */
	private String trxName;
	
	/**
	 * Constructor de la clase.
	 * @param trxName Transacción de BD a utilizar para efectuar la consulta.
	 */
	public QueryDataSource(String trxName) {
		super();
		this.trxName = trxName;
	}
	
	/**
	 * @return Devuelve la consulta SQL cuyos resultados serán los datos del Data Source.
	 */
	protected abstract String getQuery();
	
	/**
	 * @return Devuelve un arreglo de objetos que contiene los valores de los parámetros
	 * de la consulta SQL, en el orden que aparecen en la consulta.
	 */
	protected abstract Object[] getParameters();
	
	protected PreparedStatement pstmt = null;
	
	public void loadData() throws Exception {
		ResultSet rs = null;
		try{
			// Se crea la consulta y se asignan los parámetros.
			pstmt = DB.prepareStatement(getQuery(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
					getTrxName(), isQueryNoConvert());
			int i = 1;
			for (Object parameterValue : getParameters()) {
				pstmt.setObject(i++, parameterValue);				
			}
			// Se ejecuta la consulta y se obtienen los metados del ResultSet, a fin
			// de obtener los nombres de columnas para ir creando cada registro en una
			// Map que como clave tiene el nombre de la columna del resultado.
			rs = pstmt.executeQuery();
			ResultSetMetaData rsMeta = rs.getMetaData();
			int columnsCount = rsMeta.getColumnCount();
			// Map que representa el registro.
			Map<String, Object> record;
			while (rs.next()) {
				// Se crea un nuevo registro y se cargan los valores en la Map.
				record = new HashMap<String, Object>();
				for(int columnIndex = 1; columnIndex <= columnsCount; columnIndex++ ) {
					record.put(
						rsMeta.getColumnName(columnIndex).toUpperCase(),
						rs.getObject(columnIndex));
				}
				// Se agrega el nuevo registro al final de la lista de registros.
				getRecords().add(record);
			}
		} catch (SQLException e) {
			throw new Exception("@LoadReportDataError@", e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (SQLException e) {}
		}
	}

	public Object getFieldValue(JRField field) throws JRException {
		// Obtiene el valor del campo del registro actual, a partir del nombre del
		// campo del reporte jasper.
		return getCurrentRecord().get(field.getName().toUpperCase());
	}

	public boolean next() throws JRException {
		currentRecordIndex++;
		if (currentRecordIndex >= getRecords().size())
			return false;
		return true;
	}

	/**
	 * @return the records
	 */
	private List<Map<String, Object>> getRecords() {
		return records;
	}

	/**
	 * @return the trxName
	 */
	protected String getTrxName() {
		return trxName;
	}

	/**
	 * @return the currentRecordIndex
	 */
	private int getCurrentRecordIndex() {
		return currentRecordIndex;
	}
	
	/**
	 * @return Devuelve el registro actual.
	 */
	protected Map<String, Object> getCurrentRecord() {
		return getRecords().get(getCurrentRecordIndex());
	}
	
	/**
	 * @return true si no se debe pasar la query por el convert, false si se
	 *         debe pasar por él. Por defecto sí se pasa por el convert
	 */
	protected boolean isQueryNoConvert(){
		return false;
	}

	public PreparedStatement getPstmt() {
		return pstmt;
	}
	
	
}
