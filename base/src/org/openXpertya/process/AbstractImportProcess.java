package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

/**
 * <p>
 * Esta clase contiene la funcionalidad común a todos los procesos de importación.
 * A la hora de realizar un nuevo proceso que realice la importación de datos, se
 * puede especializar esta clase a fin de evitar recodificar un conjunto de operaciones
 * básicas que siempre se realizan en este tipo de procesos.
 * Esta clase implementa los métodos <code>prepare()</code> y <code>doIt()</code> de la
 * clase <code>SvrProcess</code> y declara  nuevos métodos abstractos que deben implementar
 * las subclases, incluyendo la funcionalidad especializada tanto para la preparación como
 * para la ejecución del proceso.
 * </p><p>
 * <b>IMPORTANTE:</b> es necesario que la tabla de importación que utilice el proceso contenga
 * como mínimo los campos:</p>
 * <ul>
 * 	<li>I_IsImported</li>
 * 	<li>I_ErrorMsg</li>
 * 	<li>Processed</li>
 * </ul>
 * <p>dado que los utiliza en la administración de los registros de importación de dicha tabla. <br>
 * Los métodos mínimos que deben implementar las subclases son:
 * <ol>
 * 	<li><b><code>protected void prepareImport()</code></b>: este método es el análogo al <code>SvrProcess.prepareIt()</code>
 *  y debe implementar la obtención de parámetros personalizados del proceso, junto con
 *  cualquier otra inicialización que se necesite.</li>
 * 	<li><b><code>protected void beforeImport()</code></b>: Este método es invocado antes de comenzar
 *  con la importación de los registros. Aquí se deben validar los registros que se
 *  cargaron en la tabla de importación a fin de filtrar registros que son erróneos.
 *  Comúnmente este método es utilizado para obtener los valores de IDs de ciertos campos
 *  a partir de valores de texto cargados desde los archivos. Ej: ID de producto a partir
 *  de la clave de búsqueda que se obtuvo desde el archivo de importación.
 *  <br>Para esta actividad se puede utilizar el método<br>
 *  <code>protected int setImportError(String whereClause, String errorMsg)</code> el
 *  cual marca con error todos los registros que cumplan con la <code>whereClause</code>,
 *  agregando en el campo I_ErrorMsg del registro el <code>errorMsg</code> enviado como
 *  parámetro. Este mensaje es traducido mediante el método <code>Msg.translate(...)</code>
 *  </li>
 * 	<li><b><code>protected String importRecord(PO importPO)</code></b>: una vez finalizada la 
 *  ejecución del <code>beforeImport()</code>, se recorren todos los registros aptos
 *  para ser importados y por cada uno de ellos, se invoca este método habiendo
 *  instanciado el PO correspondiente al registro de importación. Dentro de este método
 *  NO se deberían manipular atributos del <code>importPO</code>, ya que los mismos
 *  se administran en esta clase. El proceso que implementa este método solo se debe
 *  encargar de importar el registro hacia donde sea necesario, y retornar el string
 *  <code>AbstractImportProcess.IMPORT_OK</code> en caso de que la importación fuese
 *  correcta, o un mensaje de error en caso de que fuese falsa.</li>
 *  <li><b><code>protected String afterImport()</code></b>: finalmente, luego de la importación
 *  de todos los registros, se invoca este método en el cual las subclases puede efectuar
 *  actividades correspondientes al final del procesamiento. Además, si este método retorna
 *  un mensaje en String, el mismo será el utilizado para mostrar como resultado de la
 *  ejecución del procesamiento. En caso de que se retorne <code>null</code> o un string
 *  vacio, el mensaje que se mostrará sera el mensaje por defecto de esta clase.</li>
 * </ol>
 * <p>Otro método importante el cual podría sobreescribir las subclases es el método
 * que retorna el filtro SQL que se aplicará en todas las consultas a la tabla de importación:
 * </p>
 * <ul><li><b><code>protected String getSecuritySQLCheck()</code></b></li></ul>
 * <p>Por defecto, este método solo retorna la validación de la compañía, con lo cual
 * solo se obtendrá una independencia por compañía a la hora de relizar las consultas 
 * para la importación. Las subclases podrían agregar condiciones extras, como por ejemplo
 * validación de usuario, o registros activos, etc.</p>
 * <code>
 * 
 * @author Franco Bonafine
 * @date 19/06/2008
 */
public abstract class AbstractImportProcess extends SvrProcess {

	protected static String IMPORT_OK = "OK";
		
	/** Parámetro que indica si se deben borrar los registros ya importados
	 * que aún existen en la tabla de importación */
	private boolean m_deleteOldImported = false;

	/** PO de tabla de importación */
	private M_Table importTable;
	/** ID de la columna I_IsImported de la tabla de importación */
	private int column_IsImported_ID;
	/** ID de la columna I_ErrorMsg de la tabla de importación */
	private int column_ErrorMsg_ID;
	/** ID de la columna Processed de la tabla de importación */
	private int column_Processed_ID;

	/** Colección de parámetros del proceso que indexada por <Nombre,Valor> */
	private Map<String, Object> parametersValues;
	/** Chequeo de compañía para consultas SQL */
	private String clientSQLCheck;
	/** Chequeo de usuario para consultas SQL */
	private String userSQLCheck;
	/** Cantidad de líneas importadas */
	private int importedLines = 0;
	/** Cantidad total de líneas */
	private int totalLines = 0;
	/** Cantidad de líneas con error */
	private int errorLines = 0;
				
	/**
	 * Preparación especializada del proceso de importación. Aquí se debe incorporar
	 * la funcionalidad personalizada de la importación, como por ejemplo la asignación
	 * de parámetros específicos del proceso. Este método es invocado al final de la
	 * ejecución del método <code>SvrProcess.prepareIt()</code>.
	 */
	protected abstract void prepareImport();
	
	/**
	 * Este método es ejecutado luego del <code>prepareImport()</code>. Aquí es posible
	 * realizar las actualizaciones necesarias a los registros de importación. Las mas
	 * comunes son la obtención de valores para campos que se encuentran vacios a partir 
	 * de la carga del archivo, pero que son necesarios para la importación. Ej: obtención
	 * de IDs a partir de campos de texto cargados del archivo.
	 * A partir de estas actualizaciones, se pueden marcar registros como erróneos,
	 * evitando así que se tengan en cuenta posteriormente en la efectivización
	 * de la importación. (ver método <code>setImportError(String whereClause, String errorMsg)</code>).
	 * @throws Exception En caso de producirse algún error se lanza una excepción para
	 * interrumpir el proceso de importación.
	 */
	protected abstract void beforeImport() throws Exception;
	
	/**
	 * Importación de un registro. Este método es invocado sucesivamente para importar
	 * los registros que no contienen algún error. El mismo debe implementar la funcionalidad
	 * de importación en su(s) respectiva(s) tabla(s), sin tener que preocuparse por
	 * la actualización de los campos de la tabla de importación. (estos campos los administra
	 * ésta clase abstracta).
	 * @param importPO PO que representa el registro de importación. (X_I_nombre).
	 * @return En caso de no poderse importar el registro, retorna un 
	 * <code>String</code> que contiene el mensaje de error.
	 * En caso de que la importación se haga satisfactoriamente, retorna la constante
	 * <code>AbstractImportProcess.IMPORT_OK</code>. 
	 * @throws Exception en caso de error en la importación.
	 */
	protected abstract String importRecord(PO importPO) throws Exception;
	
	/**
	 * Este método es invocado luego de realizar la importación de todos los registros
	 * que no tuvieron error. El mismo se utiliza para realizar cualquier actividad posterior
	 * a la importación, y además, permite personalizar el mensaje de retorno del proceso
	 * general, retornando un mensaje propio.
	 * @return Un mensaje de información general del resultado del proceso. Solo se mostrara
	 * este mensaje en caso de que sea distinto de <code>null</code> y <code>""</code>.
	 * En estos dos últimos casos, el mensaje de resultados del procesamiento será el
	 * mensaje por defector codificado en <code>AbstractImportProcess</code> 
	 * @throws Exception En caso de producirse algún error en el procesamiento.
	 */
	protected abstract String afterImport() throws Exception;
	
	@Override
	protected void prepare() {
		// Se crea el Map de parámetros
		parametersValues = new HashMap<String, Object>();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			getParametersValues().put(name, para[i].getParameter());
		}

		// Se asignan los parámetros de importación generales.
		m_deleteOldImported = "Y".equals((String)getParameterValue("DeleteOldImported","N"));
		
		// Se obtiene el PO de la tabla de importación.
		setImportTable(new M_Table(getCtx(), getTable_ID(), null));
		// Se obtiene el ID de las columnas relevantes del objeto de importación, a fin
		// de utilizarlos para el seteo correcto de los valores de estos campos.
		column_IsImported_ID = getImportTable().getColumn("I_IsImported").getID();
		column_ErrorMsg_ID = getImportTable().getColumn("I_ErrorMsg").getID();
		column_Processed_ID = getImportTable().getColumn("Processed").getID();
		
		// Se invoca la preparación especializada de la subclase.
		prepareImport();
		
		// Se asigna los filtros de seguridad.
		// Esto se realiza luego de la preparación específica a fin de permitir 
		// asignar el valor del filtro en dicho método a las subclases.
		
		// Compañía
		setClientSQLCheck(" AD_Client_ID = " + getAD_Client_ID() + " ");
		// Usuario
		setUserSQLCheck(" CreatedBy = " + getAD_User_ID() + " ");
	}

	@Override
	protected String doIt() throws Exception {
		int no;
		
		// Se calcula la cantidad de líneas a importar.
		calculateTotalLines();
				
		// Se borran los registros previamente importados en caso de que así lo indique el parámetro.
        if(isDeleteOldImported()) {
            no = deleteOldImported();
        	log.info( "Delete Old Imported =" + no );
        }
        
		// Resetea los datos en la tabla de importación
		no = resetDefaultData();
        log.info ("doIt - Reset=" + no);
        
        String processMsg = null;
        try {
            // Se ejecutan las acciones previas a la importación específicas de la
            // implementación concreta.
            beforeImport();

        	// Se realiza la importación de los registros.
            importRecords();
            
            // Se ejecutan las acciones posteriores a la importación, específicas
            // de la implementación concreta.
            processMsg = afterImport();
        	
        } catch (Exception e) {
        	updateErrorLines();
        	// Se relanza una excepción con el mensaje de error detallado.
        	throw new Exception(getResultMsg(null,true) + e.getMessage(), e);
        } 

       	// Se actualizan las tuplas con error indicando que no fueron importadas.
       	updateErrorLines();
		return getResultMsg(processMsg);
	}
	
	/**
	 * Asigna un mensaje de error a las tuplas que cumplen con una clausula
	 * where determinada, dentro de la tabla de importación.
	 * @param whereClause Cláusula de filtro.
	 * @param errorMsg Mensaje a agregar. Se permiten claves de AD_Message.
	 * @return Cantidad de filas actualizadas.
	 */
	protected int setImportError(String whereClause, String errorMsg) {
		StringBuffer sql = new StringBuffer(
				" UPDATE " + getImportTableName() +
				" SET I_IsImported = 'E', " +
				"     I_ErrorMsg = COALESCE(I_ErrorMsg,'') || " + getSQLErrorMsg(errorMsg) +
				" WHERE I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());
		
		// Se agrega la cláusula where en caso de que exista.
		if (whereClause != null && whereClause.length() > 0)
			sql.append(" AND ").append(whereClause);

		return DB.executeUpdate (sql.toString ());
	}
	
	/**
	 * Retorna el conjunto de registros que son válidos para la importación.
	 * @param whereClause Filtro personalizado.
	 * @return <code>ResultSet</code> con los registros a importar.
	 * @throws SQLException En caso de producirse un error en la realización de la
	 * consulta SQL.
	 */
	private ResultSet getRecordsToImport(String whereClause) throws SQLException {
		// Consulta por defecto. Solo filtra los registros no importados.
		StringBuffer sql = new StringBuffer(
			" SELECT * FROM " + getImportTableName() +
			" WHERE I_IsImported = 'N' AND ").append(getSecuritySQLCheck());

		// Asignación de filtro personalizado en caso de existir.
		if (whereClause != null && whereClause.length() > 0)
			sql.append(" AND ").append(whereClause);
		
		// Se crea la sentencia y se realiza la consulta para obtener el ResultSet.
		PreparedStatement pstmt = DB.prepareStatement(sql.toString());
		return pstmt.executeQuery();
	}

	/**
	 * Retorna el conjunto de registros que son válidos para la importación.
	 * @return <code>ResultSet</code> con los registros a importar.
	 * @throws SQLException En caso de producirse un error en la realización de la
	 * consulta SQL.
	 */
	private ResultSet getRecordsToImport() throws SQLException {
		return getRecordsToImport(null);
	}
	
	/**
	 * Borra los registros previamente importados de la tabla de importación. 
	 */
	protected int deleteOldImported() {
        StringBuffer sql = new StringBuffer(
        		" DELETE FROM " + getImportTableName() +  
        		" WHERE I_IsImported='Y' AND ").append(getSecuritySQLCheck());
        
        return DB.executeUpdate( sql.toString());
	}
	
	/**
	 * Actualiza las tuplas que produjeron error en la importación, indicando
	 * que no fueron importadas.
	 */
	private int updateErrorLines() {
		int qty;
		StringBuffer sql = new StringBuffer (
				" UPDATE " + getImportTableName() +
				" SET I_IsImported = 'N', Updated=SysDate " +
				" WHERE I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());
			
		qty = DB.executeUpdate(sql.toString());
		setErrorLines(qty);
		return qty; 
	}
	
	/**
	 * Calcula la cantidad de lineas que se van a importar y asigna el número
	 * al atributo <code>totalLines</code>
	 */
	private void calculateTotalLines() {
		StringBuffer sql = new StringBuffer(
				" SELECT COUNT(*) " +
				" FROM " + getImportTableName() +
				" WHERE I_IsImported <> 'Y' AND ").append(getSecuritySQLCheck());
		setTotalLines(DB.getSQLValue(null, sql.toString()));
	}
	
	/**
	 * Asigna valores por defecto a las columnas de los registros que no contienen
	 * algún valor.
	 */
	private int resetDefaultData() {
		StringBuffer sql = new StringBuffer (
				" UPDATE " + getImportTableName() + 
			    " SET AD_Client_ID = COALESCE (AD_Client_ID,").append (getAD_Client_ID()).append (")," + 
			    "     AD_Org_ID    = COALESCE (AD_Org_ID,").append (getAD_Org_ID()).append (")," +
			    "     IsActive     = COALESCE (IsActive, 'Y')," + 
			    "     Created      = COALESCE (Created, SysDate)," + 
			    "     CreatedBy    = COALESCE (CreatedBy, 0)," + 
			    "     Updated      = COALESCE (Updated, SysDate)," + 
			    "     UpdatedBy    = COALESCE (UpdatedBy, 0)," + 
			    "     I_ErrorMsg   = NULL," + 
			    "     I_IsImported = 'N' " + 
			    " WHERE (I_IsImported <> 'Y' OR I_IsImported IS NULL) AND ").append(getSecuritySQLCheck());
			
		return DB.executeUpdate (sql.toString ());
	}
	
	/**
	 * Recorre todos los registros válidos obteniendo el PO de cada uno, e invocando
	 * al método específico que realiza la actividad de importación.
	 */
	private void importRecords() throws Exception {
		// Se obtienen los registros válidos a importar.
		ResultSet rsImport = getRecordsToImport();
		// Se recorren los registros a importar...
		while (rsImport.next ()) {
			// Se obtiene el PO del registro actual a importar...
			PO importPO = getImportTable().getPO(rsImport, null);
			// Se invoca el método abstracto que implementan las subclases
			// en el cual deben realizar la actividad de importación del registro.
			String importMsg = importRecord(importPO);
			if (importMsg.equals(IMPORT_OK)) {
				// Si la importación fué correcta se setean los valores de los
				// campos del registro de importación.
				importPO.set_ValueOfColumn(column_IsImported_ID, true);
				importPO.set_ValueOfColumn(column_ErrorMsg_ID, null);
				importPO.set_ValueOfColumn(column_Processed_ID, true);
				// Se incrementa la cantidad de líneas importadas.
				importedLines++;
			} else {
				importPO.set_ValueOfColumn(column_IsImported_ID, false);
				importPO.set_ValueOfColumn(column_ErrorMsg_ID, importMsg);
			}
			// Se guardan los cambios en el registro de importación.
			if(!importPO.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}				
		}
	}
	
	/**
	 * Retorna un mensaje de error formateado a partir de una clave de AD_Message.
	 * @param name AD_Message a buscar.
	 */
	protected String getSQLErrorMsg(String name) {
		String msg = Msg.translate(getCtx(), name);
		String sep;
		if (msg != null) {
			msg = msg.trim();
			if (msg.endsWith("."))
				sep = " ";
			else
				sep = ". ";
			msg = "'" + msg + sep + "'";
		} else
			msg = "''";
		return msg;
	}
	
	/**
	 * Retorna el mensaje de resultados del proceso. Si <code>processMsg</code> contiene
	 * algo se retorna este mensaje.
	 */
	private String getResultMsg(String processMsg, boolean error) {
		String msgKey = (error ? "ImportError" : "ImportCompleted");
		StringBuffer msg = new StringBuffer(Msg.translate(getCtx(), msgKey)).append(" - ");
		if (processMsg != null && processMsg.length() > 0)
			msg.append(processMsg);
		else {
			msg.append(Msg.getMsg(getCtx(), "ImportResult", new Object[] {
				getTotalLines(),
				getImportedLines(),
				//(getTotalLines() - getImportedLines())
				getErrorLines()
			}));
		}
		if (error)
			msg.append(" ERROR: ");
		return msg.toString(); 
	}
	
	private String getResultMsg(String processMsg) {
		return getResultMsg(processMsg, false);
	}

	/**
	 * @return Returns the m_deleteOldImported.
	 */
	protected boolean isDeleteOldImported() {
		return m_deleteOldImported;
	}

	/**
	 * @param oldImported The m_deleteOldImported to set.
	 */
	protected void setDeleteOldImported(boolean oldImported) {
		m_deleteOldImported = oldImported;
	}

	/**
	 * @return Returns the parametersValues.
	 */
	protected Map<String, Object> getParametersValues() {
		return parametersValues;
	}
	
	/**
	 * Obtiene el valor de un parámetro del proceso.
	 * @param name Nombre del parámetro.
	 * @param defaultValue Valor retornado en caso de que el parámetro no exista.
	 * @return El valor de parámetro en caso de ser distinto de <code>null</code>, o
	 * el <code>defaultValue</code> en caso contrario.
	 */
	protected Object getParameterValue(String name, Object defaultValue) {
		Object value = getParametersValues().get(name);
		return value != null ? value : defaultValue; 
	}
	
	/**
	 * Organización utilizada para la importación.
	 */
	protected int getAD_Org_ID() {
		return Env.getAD_Org_ID(getCtx());
	}

	/**
	 * @return Returns the clientSQLCheck.
	 */
	protected String getClientSQLCheck() {
		return clientSQLCheck;
	}

	/**
	 * @param clientSQLCheck The clientSQLCheck to set.
	 */
	protected void setClientSQLCheck(String clientSQLCheck) {
		this.clientSQLCheck = clientSQLCheck;
	}

	/**
	 * @return Returns the importedLines.
	 */
	public int getImportedLines() {
		return importedLines;
	}

	/**
	 * @param importedQty The importedLines to set.
	 */
	protected void setImportedLines(int importedLines) {
		this.importedLines = importedLines;
	}

	/**
	 * @return Returns the totalLines.
	 */
	public int getTotalLines() {
		return totalLines;
	}

	/**
	 * @param totalLines The totalLines to set.
	 */
	protected void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
	}

	/**
	 * @return Returns the userSQLCheck.
	 */
	protected String getUserSQLCheck() {
		return userSQLCheck;
	}

	/**
	 * @param userSQLCheck The userSQLCheck to set.
	 */
	protected void setUserSQLCheck(String userSQLCheck) {
		this.userSQLCheck = userSQLCheck;
	}

	/**
	 * @return Retorna el filtro SQL que se utiliza para filtrar los
	 * registros de importación. Por defecto solo se valida la compañía.
	 * Las subclases pueden redefinir este método a fin de personalizar
	 * las validaciones, agregando o quitando filtros.
	 */
	protected String getSecuritySQLCheck() {
		return getClientSQLCheck();
	}
	
	/**
	 * @return Retorna la descripción de un error producido por el sistema, almacenado
	 * en el Logger de la aplicación.
	 */
	protected String getErrorDescription() {
		String msg = null;
		ValueNamePair np = CLogger.retrieveError();
		// Se intenta obtener el mensaje a partir del Logger.
		if (np != null) {

			String name = (np.getName() != null) ? Msg.translate(Env.getCtx(), np.getName()) : "";
			String value = (np.getValue() != null) ? Msg.translate(Env.getCtx(), np.getValue()) : "";
			if (name.length() > 0 && value.length() > 0)
				msg = value + ": " + name;
			else if (name.length() > 0)
				msg = name;
			else if (value.length() > 0)
				msg = value;
		}
		
		return msg;
	}

	/**
	 * @return Returns the errorLines.
	 */
	public int getErrorLines() {
		return errorLines;
	}

	/**
	 * @param errorLines The errorLines to set.
	 */
	protected void setErrorLines(int errorLines) {
		this.errorLines = errorLines;
	}

	/**
	 * @return Returns the importTable.
	 */
	protected M_Table getImportTable() {
		return importTable;
	}

	/**
	 * @param importTable The importTable to set.
	 */
	private void setImportTable(M_Table importTable) {
		this.importTable = importTable;
	}
	
	/**
	 * @return Retorna el nombre de la tabla de importación.
	 */
	public String getImportTableName() {
		String name = "";
		if (getImportTable() != null)
			name = getImportTable().getTableName();
		return name;
	}
}
