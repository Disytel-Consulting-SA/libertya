package org.openXpertya.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.MExpFormat;
import org.openXpertya.model.MExpFormatRow;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MProcessPara;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.X_AD_ExpFormat;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class ExportProcess extends SvrProcess {
	
	/** Separador de líneas */
	protected static final String ROW_SEPARATOR_UNIX = "\n";
	protected static final String ROW_SEPARATOR_WINDOWS = "\r\n";
	/** Codificación */
	protected static final String ENCODE_UTF8 = "UTF-8";
	protected static final String ENCODE_8859_1 = "8859_1";

	/** Extensión del archivo exportado */
	protected static final String FILE_EXTENSION_CSV = "csv";
	protected static final String FILE_EXTENSION_TXT = "txt";
	/** Formato de exportación */
	private MExpFormat exportFormat;
	/** Caracter separador de campos */
	private String fieldSeparator;
	/** Columnas del formato de exportación */
	private List<MExpFormatRow> exportFormatRows;
	/** Columnas del formato de exportación */
	private Map<Integer, String> exportFormatColumns;
	/** Nombre de parámetros del proceso */
	private List<String> parametersNames;
	/** Parámetros del proceso */
	private Map<String, Object> parametersValues;
	/** Dato Info de los Parámetros del proceso */
	private Map<String, String> parametersInfo;
	/**
	 * Parámetros de la cláusula where de la consulta sobre la tabla de
	 * exportación
	 */
	private List<Object> whereClauseParams;
	/** Archivo a exportar */
	private File exportFile;
	/** Buffer de escritura del archivo */
	private Writer fileWriter;
	/** Cantidad de líneas exportadas */
	private int exportedLines = 0;
	/** Campos de secuencia */
	private Map<String, Integer> sequenceNumbers = new HashMap<String, Integer>();
	
	@Override
	protected void prepare() {
		// Formato de exportación
		MProcess process = MProcess.get(getCtx(), getProcessInfo()
				.getAD_Process_ID());
		if(!Util.isEmpty(process.getAD_ExpFormat_ID(), true)){
			setExportFormat(new MExpFormat(getCtx(),
					process.getAD_ExpFormat_ID(), get_TrxName()));
		}
		// Se crea el Map de parámetros
		setParametersValues(new HashMap<String, Object>());
		setParametersInfo(new HashMap<String, String>());
		setParametersNames(new ArrayList<String>());
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			getParametersNames().add(name);
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
	}

	@Override
	protected String doIt() throws Exception {
		if(getExportFormat() == null){
			throw new Exception("Export format inexistent");
		}
		// Cargar separador de campos
		loadFieldSeparator();
		// Cargar campos del formato de exportación
		loadExpFormatRows();
		// Crear el archivo
		createDocument();
		// Cargar el archivo
		fillDocument();
		// Cerrar el archivo
		saveDocument();
		// Mensaje de cantidad de filas de exportaciones
		return getMsg();
	}
	
	/**
	 * Cargar los nombres de las columnas para que no haya que buscarlos por
	 * cada iteración de fila a exportar
	 */
	protected void loadExpFormatRows(){
		setExportFormatRows(getExportFormat().getRows());
		Map<Integer, String> columns = new HashMap<Integer, String>();
		String columnName;
		for (MExpFormatRow exportFormatRow : getExportFormatRows()) {
			columnName = M_Column.getColumnName(getCtx(),exportFormatRow.getAD_Column_ID());
			if(!Util.isEmpty(columnName, true)){
				columns.put(exportFormatRow.getAD_Column_ID(),columnName);
			}
		}
		setExportFormatColumns(columns);
	}
	
	/**
	 * @return extensión del archivo resultante (sin el .)
	 */
	protected String getFileExtension(){
		if (getExportFormat().getExtension() != null){
			if (this.getExportFormat().getExtension().equals(X_AD_ExpFormat.EXTENSION_CSV)) return FILE_EXTENSION_CSV;
			else
				if (this.getExportFormat().getExtension().equals(X_AD_ExpFormat.EXTENSION_TXT)) return FILE_EXTENSION_TXT;
		}
		return FILE_EXTENSION_CSV;
	}
	
	/**
	 * @return separador de línea
	 */
	public String getRowSeparator() {
		if (this.getExportFormat().getEndLineType() != null)
			if (this.getExportFormat().getEndLineType().equals(X_AD_ExpFormat.ENDLINETYPE_Unix)) return ROW_SEPARATOR_UNIX;
			else 
				if (this.getExportFormat().getEndLineType().equals(X_AD_ExpFormat.ENDLINETYPE_Windows)) return ROW_SEPARATOR_WINDOWS;
		return ROW_SEPARATOR_UNIX;
	}
	
	/**
	 * @return Codificación del archivo
	 */
	public String getEncodingType() {
		if (this.getExportFormat().getEncodingType() != null){
			if (this.getExportFormat().getEncodingType().equals(X_AD_ExpFormat.ENCODINGTYPE_8859_1)) return ENCODE_8859_1;
			else
				if (this.getExportFormat().getEndLineType().equals(X_AD_ExpFormat.ENCODINGTYPE_UTF_8)) return ENCODE_UTF8;
		}
		return ENCODE_UTF8;
	}

	/**
	 * @return Path absoluto del archivo exportado
	 */
	protected String getFilePath(){
		StringBuffer filepath = new StringBuffer(getExportFormat().getFileName());
		if(getExportFormat().isConcatenateTimestamp()){
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					getExportFormat().getTimestampPattern());
			filepath.append("_")
					.append(dateFormat.format(new Timestamp(System
							.currentTimeMillis())));
		}
		filepath.append(".").append(getFileExtension());
		return filepath.toString();
	}
	
	/**
	 * Creación del documento de exportación
	 * 
	 * @throws Exception
	 *             en caso de error en la creación del archivo
	 */
	protected void createDocument() throws IOException {
		setExportFile(new File(getFilePath()));
		
	    FileOutputStream fos = new FileOutputStream(getFilePath());
	    OutputStreamWriter osw = new OutputStreamWriter(fos, this.getEncodingType());
	    BufferedWriter bw = new BufferedWriter(osw);
	    
	    setFileWriter(new PrintWriter(bw));	
	}
	

	protected void fillDocument() throws Exception{
		// Ejecutar la query
		PreparedStatement ps = DB.prepareStatement(getQuery(), get_TrxName(), true);
		// Agregar los parámetros
		setWhereClauseParams(ps);
		ResultSet rs = ps.executeQuery();
		// Iterar por los resultados
		while(rs.next()){
			// Escribe al línea en el archivo
			writeLine(rs);
		}
	}
	
	protected String getQuery(){
		// SELECT
		StringBuffer sql = new StringBuffer("SELECT DISTINCT ");
		String columnName;
		// Concatenar los nombres de las columnas del select
		for (MExpFormatRow expFormatRow : getExportFormatRows()) {
			columnName = getExportFormatColumns().get(
					expFormatRow.getAD_Column_ID());
			if(!Util.isEmpty(columnName, true)){
				sql.append(columnName).append(",");
			}
		}
		sql = sql.deleteCharAt(sql.lastIndexOf(","));
		// FROM
		sql.append(" FROM ");
		sql.append(M_Table.getTableName(getCtx(), getExportFormat()
				.getAD_Table_ID()));
		// WHERE
		sql.append(" WHERE ");
		sql.append(getWhereClause());
		// ORDER BY 
		sql.append(getOrderByClause());
		return sql.toString();
	}
	
	protected String getWhereClause(){
		StringBuffer whereClause = new StringBuffer();
		MProcess process = MProcess.get(getCtx(), getProcessInfo().getAD_Process_ID());
		MProcessPara processPara;
		setWhereClauseParams(new ArrayList<Object>());
		for (String paramName : getParametersNames()) {
			whereClause.append(" ( ");
			whereClause.append(paramName);
			
			processPara = process.getParameter(paramName); 
			// Tratamiento especial para los parámetros de tipo rango
			if(processPara.isRange()){
				if(getParametersValues().get(paramName+"_TO") != null){
					whereClause.append(" between ");
					if(DisplayType.Date == processPara.getAD_Reference_ID()){
						whereClause.append(" ?::date and ?::date ");
					}
					else{
						whereClause.append(" ? and ? ");
					}
					getWhereClauseParams().add(getParametersValues().get(paramName));
					getWhereClauseParams().add(getParametersValues().get(paramName+"_TO"));
				}
				else{
					whereClause.append(" >= ? ");
					getWhereClauseParams().add(getParametersValues().get(paramName));
				}
			}
			else{
				whereClause.append(" = ? ");
				getWhereClauseParams().add(getParametersValues().get(paramName));
			}
			
			whereClause.append(" ) ");
			whereClause.append(" AND ");
		}
		whereClause.append(" ad_client_id = ").append(
				Env.getAD_Client_ID(getCtx()));
		// Levantar el filtro sql definido
		String sqlFilter = getExportFormat().getFilterSQL();
		if(!Util.isEmpty(sqlFilter, true)){
			whereClause.append(" AND ");
			whereClause.append(sqlFilter);
		}
		return whereClause.toString();
	}
	
	protected String getOrderByClause(){
		StringBuffer orderByClause = new StringBuffer();
		List<MExpFormatRow> orderRows = getExportFormat().getOrderRows();
		for (MExpFormatRow mExpFormatRow : orderRows) {
			orderByClause.append(getExportFormatColumns().get(
					mExpFormatRow.getAD_Column_ID()));
			orderByClause.append(!Util.isEmpty(
					mExpFormatRow.getOrderDirection(), true)
					&& MExpFormatRow.ORDERDIRECTION_Descending
							.equals(mExpFormatRow.getOrderDirection()) ? " desc "
					: "");
			orderByClause.append(",");
		}
		if(!Util.isEmpty(orderByClause.toString(), true)){
			orderByClause = orderByClause.deleteCharAt(orderByClause.lastIndexOf(","));
		}
		return orderByClause.length() > 0 ? " ORDER BY "
				+ orderByClause.toString() : "";
	}
	
	protected void setWhereClauseParams(PreparedStatement ps) throws SQLException{
		for (int i = 1; i <= getWhereClauseParams().size(); i++) {
			ps.setObject(i, getWhereClauseParams().get(i-1));
		}
	}
	
	protected void saveDocument() throws Exception {
		// Cierro el archivo
		getFileWriter().close();
	}
	
	protected void writeLine(ResultSet rs) throws Exception{
		// Obtener el string de cada columna a exportar
		writeRow(rs);		
		// Meter el separador de línea
		writeRowSeparator();
		// Aumentar la cantidad de líneas exportadas
		setExportedLines(getExportedLines()+1);
	}
	
	protected void writeRow(ResultSet rs) throws Exception{
		// Iterar por las columnas del formato y obtener el nombre de la columna
		StringBuffer row = new StringBuffer();
		String columnName;
		for (MExpFormatRow expFormatRow : getExportFormatRows()) {
			columnName = getExportFormatColumns().get(expFormatRow.getAD_Column_ID());
			row.append(formatDataColumn(expFormatRow, Util.isEmpty(columnName,
					true) ? null : rs.getObject(columnName)));
			row.append(getFieldSeparator());
		}
		if(!Util.isEmpty(getFieldSeparator(), true)){
			row = new StringBuffer(row.substring(0,
					row.lastIndexOf(getFieldSeparator())));
		}
		write(row.toString());
	}
	
	protected void write(String line) throws Exception{
		if(line != null) {
			getFileWriter().write(line);
		}
	}
	
	protected String formatDataColumn(MExpFormatRow expFormatRow, Object value){
		String data = new String();
		if(MExpFormatRow.DATATYPE_Constant.equals(expFormatRow.getDataType())){
			data = expFormatRow.getConstantValue();
		}
		else if (expFormatRow.isSeqNumber()) {
			Integer seqNumber = getSequenceNumbers().get(expFormatRow.getName());
			if(seqNumber == null){
				seqNumber = expFormatRow.getInitialSeqNumber();
			}
			else{
				seqNumber += expFormatRow.getSeqIncrement();
			}
			data = String.valueOf(seqNumber);
			getSequenceNumbers().put(expFormatRow.getName(), seqNumber);
		}
		else if(value != null){
			if(MExpFormatRow.DATATYPE_Date.equals(expFormatRow.getDataType())){
				SimpleDateFormat dateFormat = new SimpleDateFormat(expFormatRow.getDataFormat());
				data = dateFormat.format((Timestamp)value);
			} 
			else if(MExpFormatRow.DATATYPE_Number.equals(expFormatRow.getDataType())){
				data = String.valueOf(value);
				if (expFormatRow.getDecimalPoint() != null
						&& !expFormatRow.getDecimalPoint().equals(".")) {
					data = data.replace(".", expFormatRow.getDecimalPoint());
				}
				if (expFormatRow.isNoDecimalPoint()) {
					data = data.replace(".", "");
				}
			}
			else if(MExpFormatRow.DATATYPE_String.equals(expFormatRow.getDataType())){
				data = (String)value;
			}
		}
		return applyColumnFormat(expFormatRow, data);
	}
	
	protected String applyColumnFormat(MExpFormatRow expFormatRow, String data){
		// Si está separado por longitudes fijas entonces aplicar las
		// condiciones configuradas en la columna
		String newData = data;
		if (MExpFormat.FORMATTYPE_FixedPosition.equals(getExportFormat()
				.getFormatType()) && expFormatRow.getLength() > 0) {
			if(newData.length() >= expFormatRow.getLength()){
				newData = newData.substring(0, expFormatRow.getLength());
			}
			else{
				// Rellenar con el caracter de relleno si es que posee uno
				// configurado
				String fillCharacter = Util.isEmpty(
						expFormatRow.getFillCharacter(), true) ? " "
						: expFormatRow.getFillCharacter().trim();
				String alignment = Util.isEmpty(expFormatRow.getAlignment(),
						true) ? MExpFormatRow.ALIGNMENT_Left : expFormatRow
						.getAlignment();
				newData = fillField(newData, fillCharacter, alignment, expFormatRow.getLength(),
						expFormatRow.getNegative_Position());
			}
		}
		return newData;
	}
	
	/***
	 * Rellena el campo con el caracter de relleno
	 * @param data
	 * @param fillCharacter
	 * @param alignment
	 * @param fieldLength
	 * @param negativePosition
	 * @return
	 */
	protected String fillField(String data, String fillCharacter, String alignment, Integer fieldLength, String negativePosition){
		String newData = data;
		int dataLength = data.length();
		StringBuffer filling = new StringBuffer();
		while ((dataLength+filling.length()) < fieldLength) {
			filling.append(fillCharacter);
		}
		newData = (MExpFormatRow.ALIGNMENT_Left.equals(alignment) ? newData
				+ filling.toString()
				: filling.toString() + newData);
		// Posición del negativo
		if(!Util.isEmpty(negativePosition, true)){
			if (data.contains("-")
					&& (alignment != null && alignment.equals(MExpFormatRow.ALIGNMENT_Right)) 
					&& negativePosition.equals(MExpFormatRow.NEGATIVE_POSITION_BeforeFilling)) {
				newData = newData.replace("-", fillCharacter);
				newData = newData.replaceFirst(fillCharacter, "-");
			}
		}
		return newData;
	}
	
	/**
	 * Cargar el separador de campos del formato
	 */
	private void loadFieldSeparator(){
		String separator = "";
		if(MExpFormat.FORMATTYPE_TabSeparated.equals(getExportFormat()
				.getFormatType())){
			separator = "	";
		}
		else if(!Util.isEmpty(getExportFormat().getDelimiter())){
			separator = getExportFormat().getDelimiter();
		}
		setFieldSeparator(separator);
	}
	
	protected void writeRowSeparator() throws IOException{
		getFileWriter().write(getRowSeparator());
	}
	
	
	protected String getMsg(){
		return Msg.getMsg(getCtx(), "ExportedRecords") + ": "
				+ getExportedLines();
	}
	
	protected Map<String, Object> getParametersValues() {
		return parametersValues;
	}

	protected void setParametersValues(Map<String, Object> parametersValues) {
		this.parametersValues = parametersValues;
	}

	protected Map<String, String> getParametersInfo() {
		return parametersInfo;
	}

	protected void setParametersInfo(Map<String, String> parametersInfo) {
		this.parametersInfo = parametersInfo;
	}

	protected MExpFormat getExportFormat() {
		return exportFormat;
	}

	protected void setExportFormat(MExpFormat exportFormat) {
		this.exportFormat = exportFormat;
	}

	protected List<MExpFormatRow> getExportFormatRows() {
		return exportFormatRows;
	}

	protected void setExportFormatRows(List<MExpFormatRow> exportFormatRows) {
		this.exportFormatRows = exportFormatRows;
	}

	protected File getExportFile() {
		return exportFile;
	}

	protected void setExportFile(File exportFile) {
		this.exportFile = exportFile;
	}

	protected Writer getFileWriter() {
		return fileWriter;
	}

	protected void setFileWriter(Writer fileWriter) {
		this.fileWriter = fileWriter;
	}

	protected Map<Integer, String> getExportFormatColumns() {
		return exportFormatColumns;
	}

	protected void setExportFormatColumns(Map<Integer, String> exportFormatColumns) {
		this.exportFormatColumns = exportFormatColumns;
	}

	protected void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	protected String getFieldSeparator() {
		return fieldSeparator;
	}

	protected List<Object> getWhereClauseParams() {
		return whereClauseParams;
	}

	protected void setWhereClauseParams(List<Object> whereClauseParams) {
		this.whereClauseParams = whereClauseParams;
	}

	protected int getExportedLines() {
		return exportedLines;
	}

	protected void setExportedLines(int exportedLines) {
		this.exportedLines = exportedLines;
	}

	protected List<String> getParametersNames() {
		return parametersNames;
	}

	protected void setParametersNames(List<String> parametersNames) {
		this.parametersNames = parametersNames;
	}

	protected Map<String, Integer> getSequenceNumbers() {
		return sequenceNumbers;
	}

	protected void setSequenceNumbers(Map<String, Integer> sequenceNumbers) {
		this.sequenceNumbers = sequenceNumbers;
	}
}
