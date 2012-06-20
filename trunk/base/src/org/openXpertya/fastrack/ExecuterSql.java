package org.openXpertya.fastrack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.util.DB;

public class ExecuterSql {

	/**
	 * Ejecuta una consulta sql 
	 * @param query consulta sql
	 * @param trxName nombre de la transacción a utilizar
	 * @return ResultSet resultados de la consulta
	 * @throws Exception
	 */
	public static ResultSet executeQuery(String query, String trxName) throws Exception{
		//
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			ps = DB.prepareStatement(query, trxName);
			rs = ps.executeQuery();
		}catch(Exception e){
			throw new Exception("Hubo un error mientras se ejecutaba la siguiente query: \n"+query); 
		}
		
		return rs;
	}
	
	/**
	 * Retorna una lista de listas con todos las tuplas resultado y sus columnas
	 * @param query
	 * @param trxName
	 * @return
	 * @throws Exception
	 */
	public static List<List<Object>> executeQueryToList(String query,String trxName) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<List<Object>> list = new ArrayList<List<Object>>();
		
		try{
			ps = DB.prepareStatement(query, trxName);
			rs = ps.executeQuery();
			list = DB.toList(rs);
		}catch(Exception e){
			throw new Exception("Hubo un error mientras se ejecutaba la siguiente query: \n"+query); 
		}finally{
			try{
				ps.close();
				if(rs != null){
					rs.close();
				}
			}catch(Exception e){
				throw new Exception("Hubo un error mientras se cerraba el PreparedStatement usado en la ejecución de la siguiente query: \n"+query);
			}
		}
		
		return list;
	}
	
	
	/**
	 * Ejecuta una sentencia sql de tipo update con parámetros
	 * @param query
	 * @param trxName
	 * @param params
	 * @throws Exception
	 */
	public static void executeUpdate(String query, String trxName, Object[] params) throws Exception{
		PreparedStatement ps = null;
				
		try{
			ps = DB.prepareStatement(query, trxName);
			
			if (params != null) {
            	for (int i = 0; i < params.length; i++)
            		ps.setObject(i+1, params[i]);
            }
			
			ps.executeUpdate();
		}catch(Exception e){
			throw new Exception("Hubo un error mientras se ejecutaba la siguiente query: \n"+query); 
		}finally{
			try{
				ps.close();
			}catch(Exception e){
				throw new Exception("Hubo un error mientras se cerraba el PreparedStatement usado en la ejecución de la siguiente query: \n"+query);
			}
		}
	}
	
	
	
	/**
	 * Ejecuta una query de tipo update
	 * @param query 
	 * @param trxName nombre de la transacción a utilizar
	 * @throws Exception
	 */
	public static void executeUpdate(String query, String trxName) throws Exception{
		PreparedStatement ps = null;
				
		try{
			ps = DB.prepareStatement(query, trxName);			
			ps.executeUpdate();
		}catch(Exception e){
			throw new Exception("Hubo un error mientras se ejecutaba la siguiente query: \n"+query); 
		}finally{
			try{
				ps.close();
			}catch(Exception e){
				throw new Exception("Hubo un error mientras se cerraba el PreparedStatement usado en la ejecucion de la siguiente query: \n"+query);
			}
		}
	}
		
}
