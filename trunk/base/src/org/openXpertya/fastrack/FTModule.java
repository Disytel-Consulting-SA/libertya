package org.openXpertya.fastrack;

import org.openXpertya.util.CLogger;

public abstract class FTModule implements Actionable{

	//Variables de instancia
	
	/** Nombre de la transación */
	
	private String trxName;
	
	/** Log del procedimiento */
	
	private CLogger log = CLogger.getCLogger( this.getClass());
	
	//Constructores
	
	public FTModule(){
		
	}
	
	public FTModule(String trxName){
		this.setTrxName(trxName);
	}
	
	//Getters y seters
	
	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setLog(CLogger log) {
		this.log = log;
	}

	public CLogger getLog() {
		return log;
	}
	
	
	//Métodos varios

	/**
	 * Modifica campos de los elementos distinguidos con el campo entitytype para la versión fast-track. 
	 * En este caso el campo isactive.
	 * @param tableName nombre de la tabla 
	 * @param newState estado nuevo que determina si es fast-traching o desfast-tracking
	 */
	
	public void modEntityType(String tableName, boolean newState) throws Exception{
		/*
		 * SQL a ejecutar para desactivar los componentes que no van a la versión fast-track
		 * ----------------------------------------------------------------------------------
		 * UPDATE "+tableName+" 
		 * SET isactive = "+((newState)?"'N'":"'Y'")+" 
		 * WHERE entitytype = 'D'
		 * ----------------------------------------------------------------------------------
		 */
		String sql = "UPDATE "+tableName+" SET isactive = "+((newState)?"'N'":"'Y'")+" WHERE entitytype = 'D'";
				
		//Ejecuto el primer sql para los componentes a desactivar
		ExecuterSql.executeUpdate(sql, this.getTrxName());
				
		/*
		 * SQL a ejecutar para activar los componentes que van a la versión fast-track
		 * ----------------------------------------------------------------------------------
		 * UPDATE "+tableName+" 
		 * SET isactive = "+((newState)?"'Y'":"'N'")+" 
		 * WHERE (entitytype = 'CUST') OR (entitytype = 'U')
		 * ----------------------------------------------------------------------------------
		 */
		sql = "UPDATE "+tableName+" SET isactive = "+((newState)?"'Y'":"'N'")+" WHERE entitytype = 'CUST'";
				
		//Ejecuto el segundo para los componentes a activar
		ExecuterSql.executeUpdate(sql, this.getTrxName());
		
		//Log de la operación
		this.getLog().info("Modificacion de active mediante el campo entitytype en la tabla "+tableName);
	}
	
	/**
	 * Modifica campos de los elementos distinguidos con el campo entitytype para la versión fast-track. 
	 * En este caso el campo isdisplayed.
	 * @param tableName nombre de la tabla 
	 * @param newState estado nuevo que determina si es fast-traching o desfast-tracking
	 */
	public void hideEntityType(String tableName, boolean newState) throws Exception{
		/*
		 * SQL a ejecutar para desactivar los componentes que no van a la versión fast-track
		 * ----------------------------------------------------------------------------------
		 * UPDATE "+tableName+" 
		 * SET isdisplayed = "+((newState)?"'N'":"'Y'")+" 
		 * WHERE entitytype = 'D'
		 * ----------------------------------------------------------------------------------
		 */
		String sql = "UPDATE "+tableName+" SET isdisplayed = "+((newState)?"'N'":"'Y'")+" WHERE entitytype = 'D'";
				
		//Ejecuto el primer sql para los componentes a desactivar
		ExecuterSql.executeUpdate(sql, this.getTrxName());
				
		/*
		 * SQL a ejecutar para activar los componentes que van a la versión fast-track
		 * ----------------------------------------------------------------------------------
		 * UPDATE "+tableName+" 
		 * SET isactive = "+((newState)?"'Y'":"'N'")+" 
		 * WHERE (entitytype = 'CUST') OR (entitytype = 'U')
		 * ----------------------------------------------------------------------------------
		 */
		sql = "UPDATE "+tableName+" SET isdisplayed = "+((newState)?"'Y'":"'N'")+" WHERE (entitytype = 'CUST')";
				
		//Ejecuto el segundo para los componentes a activar
		ExecuterSql.executeUpdate(sql, this.getTrxName());
		
		//Log de la operación
		this.getLog().info("Modificacion al campo isdisplayed a partir del campo entitytype en la tabla "+tableName);
	}
	
	/**
	 * Modifico el elemento a sólo lectura a partir del nombre la tabla y del id del elemento a cambiar 
	 * @param tableName nombre de la tabla
	 * @param id id del elemento 
	 */
	
	public void setReadOnly(String tableName, int id) throws Exception{
		/*
		 * Script SQL
		 * ---------------------------------
		 * UPDATE "+tableName+" 
		 * SET isreadonly='Y' 
		 * WHERE "+tableName+"_id = "+id
		 * ---------------------------------
		 */
		String sql = "UPDATE "+tableName+" SET isreadonly='Y' WHERE "+tableName+"_id = "+id;
		
		//Ejecución del SQL
		ExecuterSql.executeUpdate(sql, this.getTrxName());
		
		//Log de la operación
		this.getLog().info("Modificacion seteando solo lectura al campo con id "+id+" de la tabla: "+tableName);
	}
	
	/**
	 * Activa o desactiva las tuplas con ids distintos a los pasados como parámetro de la tabla
	 * @param tableName nombre de la tabla 
	 * @param ids arreglo de ids que no se deben tener en cuenta a la hora de modificar 
	 * @param active valor al campo isActive de la tabla
	 */
	
	public void changeActiveByClientIds(String tableName, int[] ids, boolean active) throws Exception{
		/* 
		 * Creo el script sql
		 * ---------------------------------------
		 * UPDATE tableName
		 * SET isactive = ((active)?"'Y'":"'N'"))
		 * WHERE ad_client_id <> id[i]					//Siempre y cuando haya ids en el arreglo
		 * ---------------------------------------
		 *  
		*/
		StringBuffer sql = new StringBuffer("UPDATE "+tableName+" SET isactive = "+((active)?"'Y'":"'N'"));
		
		int total = ids.length;
		
		//Si vienen ids en el arreglo pongo el where
		if(total > 0){
			sql.append(" WHERE ");
		}
			
		//Itero por los id y los agrego al where
		for(int i = 0; i < total ; i++){
			
			//Agrego la condición al where
			sql.append(" ( ad_client_id <> "+ids[i]+") AND ");
		}

		//Elimino el último AND y ejecuto del SQL
		ExecuterSql.executeUpdate(sql.substring(0, sql.lastIndexOf("AND")), this.getTrxName());
		
		//Log de la operación
		this.getLog().info(((active)?"Activacion":"Desactivacion")+" de tuplas de la tabla: "+tableName);
	}
			
}
