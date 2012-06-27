package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.M_Table;
import org.openXpertya.model.X_AD_TableReplication;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Crea el SQL correspondiente a los triggers de replicación
 */


public class CreateReplicationTriggerProcess extends SvrProcess {
	
	/* Tipo de alcance de aplicación del proceso */
	public static final String SCOPE_ALL_TABLES = "A";		// todas las tablas de la aplicación
	public static final String SCOPE_CONFIGURED = "C";		// solo las tablas configuradas
	public static final String SCOPE_THIS_RECORD = "R";		// solo este registro
	
	// Si el proceso es disparado desde el arbol de menú, se supone que se desea
	// generar los triggers para TODAS las tablas de la base de datos
	// (la insercion masiva es de utilidad para determinar que tablas se bitacorean en cada circuito)
	protected String p_scope = "";

	// Instancia de la tabla a ser procesada en un momento dado
	protected M_Table table; 

	// Valor de retorno
	protected StringBuffer retValue = new StringBuffer("");
	
	// Columnas reservadas para replicacion, no deberian insertarse en metadatos directamente 
	public static final String COLUMN_RETRIEVEUID = "retrieveUID";
	public static final String COLUMN_REPARRAY = "repArray";
	public static final String COLUMN_DATELASTSENT = "dateLastSentJMS";
	
	// Replication array dummy para relleno unicamente
	public static final String DUMMY_REPARRAY = "0";
	
	
	@Override
	protected void prepare() {

		// Parametro de alcance de la aplicación (scope)
		ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            if( name.equals( "Scope" ))
                p_scope = (String)para[ i ].getParameter();
        }
		
		retValue = new StringBuffer(" - Resultados de la ejecucion - \n");
		
	}

	
	@Override
	protected String doIt() throws Exception 
	{
		
		// Query según el tipo de alcance
		String scopeClause = "";
		if (SCOPE_ALL_TABLES.equalsIgnoreCase(p_scope))
			scopeClause = " AND substr(lower(lt.tablename), 1, 2) != 'ad' AND substr(lower(lt.tablename), 1, 1) NOT IN ('t', 'i')";
		if (SCOPE_CONFIGURED.equalsIgnoreCase(p_scope))
			scopeClause = " AND lt.AD_Table_ID IN " + getConfiguredTablesQuery();
		if (SCOPE_THIS_RECORD.equalsIgnoreCase(p_scope))
		{
			X_AD_TableReplication tableReplication = new X_AD_TableReplication(getCtx(), getRecord_ID(), get_TrxName());
			table = new M_Table(getCtx(), tableReplication.getAD_Table_ID(), get_TrxName());
			scopeClause = " AND lt.AD_Table_ID = " + table.getAD_Table_ID();
		}
		
		/* Recuperar los IDs de todas las tablas (sin contemplar tablas AD_ ya que las mismas son de metadatos para el caso allTables) */
		PreparedStatement pstmt = DB.prepareStatement( 	" SELECT lt.ad_table_id " +
														" FROM information_schema.tables pt " +
														" INNER JOIN ad_table lt ON lower(pt.table_name) = lower(lt.tablename) " +
														" WHERE pt.table_schema = 'libertya' " +
														" AND pt.table_type = 'BASE TABLE' " + scopeClause, get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		
		while (rs.next())
		{
			// Recuperar el ID de la tabla
			table = new M_Table(getCtx(), rs.getInt("AD_Table_ID"), get_TrxName());
			
			// Si estamos realizando la insercion masiva, entonces tambien generar todas las entradas en AD_TableReplication
			if (SCOPE_ALL_TABLES.equalsIgnoreCase(p_scope))
			{
				// verificar si no existe una entrada previa
				int exists = DB.getSQLValue(get_TrxName(), " SELECT count(1) FROM AD_TableReplication " +
															" WHERE AD_Client_ID = " + Env.getAD_Client_ID(getCtx()) + 
															" AND AD_Table_ID = " + table.getAD_Table_ID());
				
				// si no hay entrada, entonces aplicar a la tabla
				if (exists == 0)
				{
					// Genera una entrada dummy para cada tabla en la tabla de replicación por tabla, 
					// de esta manera, la bitacora sera creada en cada caso 
					// (dado que los triggers validan la existencia en esta tabla)
					System.out.print(".");
					X_AD_TableReplication tr = new X_AD_TableReplication(getCtx(), 0, get_TrxName());
					tr.setClientOrg(getAD_Client_ID(), Env.getAD_Org_ID(getCtx()));
					tr.set_ValueNoCheck("AD_Table_ID", table.getAD_Table_ID());
					tr.setReplicationArray(DUMMY_REPARRAY); // <- Un valor base que se deberá cambiar segun sea necesario
					tr.save();
				}
			}
			else
				retValue.append("\n [" + table.getTableName() + "] \n");
			
			// Query resultante
			StringBuffer sql = new StringBuffer(""); 

			// Campos necesarios para replicación
			appendNewColumns(sql);
			
			// Rellena la columna retrieveUID con valor general para registros existentes (se supone que son registros comunes a todas las sucursales)
			appendSQLFillRetrieveUID(sql);

			// Rellena la columna retrieveUID con valor general para registros existentes (se supone que son registros comunes a todas las sucursales)
			appendSQLReplicationSequence(sql);
			
			// Creación de los triggers que invocan al procedure replication_event
			appendTriggerCreation(sql);
			
			// Impactar en base de datos
			DB.executeUpdate( sql.toString(), false , get_TrxName(), true );	
		}
				
		/* Si se ejecuta sobre todas las tablas, solo mostrar OK */
		return SCOPE_ALL_TABLES.equalsIgnoreCase(p_scope) ? "OK" : retValue.toString();
	}

	
	/**
	 *  Devuelve un String con el query que selecciona el conjunto de tablas que se 
	 *  encuentran configuradas para replicación, dentro de la tabla AD_TableReplication.
	 *  Unicamente las tablas que tienen un replicationArray valido (diferente a 0)
	 */
	protected String getConfiguredTablesQuery() throws Exception
	{
		int count = 0;
		StringBuffer values = new StringBuffer("(");
		PreparedStatement pstmt = DB.prepareStatement(" SELECT AD_Table_ID FROM AD_TableReplication " +
														" WHERE AD_Client_ID = " + getAD_Client_ID() + 
														" AND replicationArray <> '" + DUMMY_REPARRAY + "' ", 
														get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			count++;
			values.append(rs.getInt("AD_Table_ID")).append(",");
		}
		values.replace(values.length()-1, values.length()-1, ")");
		
		return count == 0 ? "()" : values.toString().substring(0, values.toString().length()-1);
	}
	
	
	/**
	 * Incorpora las nuevas columnas a la tabla
	 */
	protected void appendNewColumns(StringBuffer sql)
	{
		// Columna retrieveUID
		if (!existColumnInTable(COLUMN_RETRIEVEUID, table.getTableName()))
		{
			append (sql, " ALTER TABLE " + table.getTableName() + " ADD COLUMN " + COLUMN_RETRIEVEUID + " varchar(100);" );
			append (sql, " CREATE INDEX " + table.getTableName() + "_" + COLUMN_RETRIEVEUID + " ON " + table.getTableName() +  "(" + COLUMN_RETRIEVEUID +");");
			retValue.append(" - Creada columna: " + COLUMN_RETRIEVEUID + " \n");
		}
		else
			retValue.append(" - Columna " + COLUMN_RETRIEVEUID + " ya existe en la tabla" + " \n");
		
		// Columna repArray
		if (!existColumnInTable(COLUMN_REPARRAY, table.getTableName()))
		{
			append (sql, " ALTER TABLE " + table.getTableName() + " ADD COLUMN " + COLUMN_REPARRAY + " varchar DEFAULT '0';" );
			retValue.append(" - Creada columna: " + COLUMN_REPARRAY + " \n");
		}
		else
			retValue.append(" - Columna " + COLUMN_REPARRAY + " ya existe en la tabla" + " \n");
		
		// Columna dateLastSent
		if (!existColumnInTable(COLUMN_DATELASTSENT, table.getTableName()))
		{
			append (sql, " ALTER TABLE " + table.getTableName() + " ADD COLUMN " + COLUMN_DATELASTSENT + " timestamp null;" );
			retValue.append(" - Creada columna: " + COLUMN_DATELASTSENT + " \n");
		}
		else
			retValue.append(" - Columna " + COLUMN_DATELASTSENT + " ya existe en la tabla" + " \n");
	}
	
	
	/**
	 * Rellena el campo retrieveUID para las entradas ya existentes en la tabla (se suponen valores comunes a todas las sucursales)
	 * Para determinar esto, nos fijamos si existe en columna AD_ComponentObjectUID.  Todo registro que sea parte de un componente
	 * (o del core) debe contar con la información de esta columna (o debería) para futuras referencias.  
	 * @param sql
	 */
	protected void appendSQLFillRetrieveUID(StringBuffer sql)
	{
		// existe la column AD_ComponentObjectUID? (ciertas tablas lo tienen, pero otras como C_Invoice no lo tienen)
		// si existe, entonces es probable que exista información de core preexistente comun a todos los LY,
		// setearlo con el mismo valor que el AD_ComponentObjectUID (el cual se sabe es comun en todas las distribuciones)
		if (existColumnInTable("AD_ComponentObjectUID", table.getTableName()))
		{
			append (sql, " UPDATE " + table.getTableName() + " SET " + COLUMN_RETRIEVEUID + " = AD_ComponentObjectUID ");
			append (sql, " WHERE " + COLUMN_RETRIEVEUID + " IS NULL AND (AD_Client_ID = " + getAD_Client_ID() + " OR AD_Client_ID = 0) ");
			append (sql, " AND AD_ComponentObjectUID IS NOT NULL; ");
			
			retValue.append(" - Actualizadas entradas ya existentes en tabla (via AD_ComponentObjectUID): " + table.getTableName() + " \n");
		}
		else
		// En algunos casos esta columna puede no llegar a existir, por ejemplo en C_CashBook no está seteado.  Aunque podría
		// solucionarse simplemente para ese caso agregando la columna, puede presentarse en otras ocasiones, con lo cual
		// la solucion pasa simplemente en normalizar los registros preexistentes de manera similar que ad_componentobjectuid
		{
			// Verificamos si existe la columna NombreDeTabla_ID (ejemplo C_Invoice => C_Invoice_ID)
			if (existColumnInTable(table.getTableName()+"_ID", table.getTableName()))
				append (sql, " UPDATE " + table.getTableName() + " SET " + COLUMN_RETRIEVEUID + " = '" + table.getTableName() + "-' || " + table.getTableName() + "_ID::varchar ");
			else
				// No hay forma de generar un UID de manera sencilla sin NombreDeTabla_ID (ejemplo: c_acctschema_gl). Utilizar Tabla-AD_Client-AD_Org-Created
				append (sql, " UPDATE " + table.getTableName() + " SET " + COLUMN_RETRIEVEUID + " = '" + table.getTableName() + "-' || AD_Client_ID::varchar || '-' || AD_Org_ID::varchar || '-' || Created::varchar ");

			append (sql, " WHERE " + COLUMN_RETRIEVEUID + " IS NULL AND (AD_Client_ID = " + getAD_Client_ID() + " OR AD_Client_ID = 0); ");
			retValue.append(" - Actualizadas entradas ya existentes en tabla (via Tabla_RecordID): " + table.getTableName() + " \n");	
		}
			
	}
	

	/**
	 * Crea la secuencia a ser utilizada como parte del campo retrieveUID
	 * @param sql
	 * @throws Exception
	 */
	protected void appendSQLReplicationSequence(StringBuffer sql) throws Exception
	{
		// Verificar si la secuencia ya existe para la tabla dada
		int existe = DB.getSQLValue(get_TrxName(), 	" SELECT COUNT(1) FROM pg_class " +
													" WHERE relkind = 'S' and lower(relname) = 'repseq_" + 
													table.getTableName().toLowerCase() + "'");
		// Si la secuencia no existe, entonces crearla
		if (existe == 0)
		{
			append (sql, " CREATE SEQUENCE repseq_" + table.getTableName());
			append (sql, " INCREMENT 1 MAXVALUE 9223372036854775807 START 1; ");
			
			retValue.append(" - Creada la secuencia de replicación: repseq_" + table.getTableName().toLowerCase() + " \n");
		}
		else
			retValue.append(" - La secuencia: repseq_" + table.getTableName().toLowerCase() + " ya existe en BBDD \n");
	}
	
	
	/**
	 * Triggers para insert, update y delete
	 */
	protected void appendTriggerCreation(StringBuffer sql) throws Exception
	{
		// Trigger
		append( sql,  " DROP TRIGGER IF EXISTS replication_event ON " + table.getTableName() + ";" );
		append( sql,  " CREATE TRIGGER replication_event BEFORE INSERT OR UPDATE OR DELETE ON " + table.getTableName() + 
					  " FOR EACH ROW EXECUTE PROCEDURE replication_event(" + table.getAD_Table_ID() + ", '" + table.getTableName().toLowerCase() + "'); ");
		
		retValue.append(" - Creado el trigger: replication_event() para tabla: " + table.get_TableName() + " \n");
	}
	
	
	/**
	 * Concatena
	 */
	protected void append(StringBuffer sql, String sentence)
	{
		sql.append(sentence + "\n"); // .append(BR);
	}
	

	/**
	 * Retorna 1 si la tabla ya posee la columna correspondiente
	 */
	protected boolean existColumnInTable(String column, String table)
	{
		int cant = DB.getSQLValue(get_TrxName(), " select COUNT(1) from information_schema.columns " +
												 " where lower(table_name) = '" + table.toLowerCase() + "'" +
												 " and lower(column_name) ='" + column.toLowerCase() + "'");
		
		return cant > 0;
	}
	
	
	/**
	 * Elimina cualquier trigger previo relacionado con replicacion
	 * @throws Exception
	 */
	public static void dropPreviousTriggers(String trxName) throws Exception
	{
		PreparedStatement pstmt = DB.prepareStatement( 	" SELECT lt.tablename " +
														" FROM information_schema.tables pt " +
														" INNER JOIN ad_table lt ON lower(pt.table_name) = lower(lt.tablename) " +
														" WHERE pt.table_schema = 'libertya' " +
														" AND pt.table_type = 'BASE TABLE' ", trxName);
		StringBuffer query = new StringBuffer("");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			query.append(" DROP TRIGGER IF EXISTS replication_event on ").append(rs.getString("tablename")).append(";");
			query.append(" DROP SEQUENCE IF EXISTS repseq_").append(rs.getString("tablename").toLowerCase()).append(";");
		}
		DB.executeUpdate(query.toString(), trxName);
	}

}

