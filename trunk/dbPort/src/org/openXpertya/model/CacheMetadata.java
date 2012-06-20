package org.openXpertya.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.openXpertya.util.Env;

/**
 * Cache de metadata de columnas. Singleton. Lógica trasladada de PO.setColToStatement(....) <br>
 * Por las dudas, la cache se dehabilita si el usuario logueado es System (AD_User_ID = 0), ya 
 * que este usuario SI puede requerir generar cambios a nivel de tablas (y por lo tanto
 * esta chache podria generar inconsitencias, ya que jamás se "flushea").
 * 
 * @author Ader Javier
 *
 */
public class CacheMetadata {

	private static CacheMetadata defInst;
	public static CacheMetadata DefInst()
	{
		if (defInst == null)
			defInst = new CacheMetadata();
		return defInst;
	}
	private HashMap<String,ResultColumnMetadata> cache = new HashMap<String,ResultColumnMetadata>();
	
	
	private CacheMetadata(){	}
	
	/**
	 * Obtiene información de tipo de dato y tamaño de columna en base de datos usando funciones
	 * de Metadada JDBC. Bajo postgresSql esto es equivalente a acceder a ciertas tablas
	 * de catalog (pg_XXXX).
	 * 
	 * @param c conexión JDBC sobre la cual ejecutar el getMetadata en caso de que esta info 
	 *  no se encuentre en cache (o esta este deshabilitada) 
	 * @param tableName no pude ser null, no requiere estar capitalizada
	 * @param columnName no pude ser null, no requiere estar capitalizada
	 * @return ResultColumnMetadata representando la metada para la columna o null, si no se encuentra
	 * @throws SQLException
	 */
	public ResultColumnMetadata getForColumn(Connection c, String tableName, String columnName)
																					throws SQLException
	{
		
		tableName = tableName.toLowerCase();
		columnName = columnName.toLowerCase();
		if (cacheDisabled())
			return getFromDBForColumn(c, tableName, columnName);
		
		String key = tableName+"."+columnName ;
		
		ResultColumnMetadata result = cache.get(key);

		if (result != null)
		{
			//HIT!
			return result;
		}
		
		
		result = getFromDBForColumn(c, tableName, columnName);
		if ( result != null)		
		{
			cache.put(key, result);
		}else
		{
			//TODO: log algun tipo de waring,y/o cachear un ResultMetadataColumn "especial"
			//para mostra que no exista el información.
			;
		}
		return result;
	}
	
	private ResultColumnMetadata getFromDBForColumn(Connection c, String tableName, String columnName)
																				throws SQLException
	{
		ResultSet rs = c.getMetaData().getColumns(null,null, tableName,	columnName );
		
		ResultColumnMetadata result = null;
		if (rs.next()) {
			result = new ResultColumnMetadata();
			result.DATA_TYPE = rs.getInt("DATA_TYPE");
			result.COLUMN_SIZE = rs.getInt("COLUMN_SIZE");
		}
		rs.close();
		
		return result;
	}

	/**
	 * Determina si la cache esta desahbilitada
	 * @return por ahora solo true, si el usuario actual es "System"; (id = 0)
	 */
	private boolean cacheDisabled()
	{
		if (Env.getAD_User_ID(Env.getCtx())<= 0)
			return true;
		return false;
	}
	public static class ResultColumnMetadata
	{
		public int DATA_TYPE;
		public int COLUMN_SIZE;
	}
}
