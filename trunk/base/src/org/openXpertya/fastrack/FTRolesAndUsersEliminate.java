package org.openXpertya.fastrack;

import java.util.ArrayList;


public class FTRolesAndUsersEliminate extends FTModule {

	//Variables de instancia
	
	/** Nombre de la tabla */
	
	private String[] tablesNames = {"ad_role","ad_user","ad_user_roles"};
	
	/** Ids de compañía que no se deben eliminar los roles */
	
	private ArrayList<Integer> ids = new ArrayList<Integer>();
	
	
	//Constructores
		
	public FTRolesAndUsersEliminate() {
	}
	
	
	public FTRolesAndUsersEliminate(String trxName){
		this.setTrxName(trxName);
	}
	
	
	public FTRolesAndUsersEliminate(String trxName, int id){
		this.setTrxName(trxName);
		
		//Compañía pasada como parámetro
		this.addId(id);
		
		//Compañía 0 -- System
		this.addId(0);
	}
	
	public FTRolesAndUsersEliminate(String trxName, int ids[]){
		this.setTrxName(trxName);
		this.initializeClients(ids);
	}
	
	
	public FTRolesAndUsersEliminate(String trxName, ArrayList<Integer> ids){
		this.setTrxName(trxName);
		this.setIds(ids);
	}
	
	
	//Getters y Setters
	
	public void setTablesNames(String[] tablesNames) {
		this.tablesNames = tablesNames;
	}

	public String[] getTablesNames() {
		return tablesNames;
	}	
	
	public void setIds(ArrayList<Integer> ids) {
		this.ids = ids;
	}

	public ArrayList<Integer> getIds() {
		return ids;
	}
	
	
	//Mátodos varios	
	
	/**
	 * Inicializo los ids de los clientes desde un arreglo de ids
	 * @param ids ids de copañías
	 */
	
	public void initializeClients(int ids[]){
		int total = ids.length;
		ArrayList<Integer> lista = new ArrayList<Integer>();
		
		for(int i = 0; i < total ; i++){
			lista.add(ids[i]);
		}
		
		this.setIds(lista);
	}
	
	/**
	 * Resuelve los ids en enteros
	 * @return arreglo con los ids primitivos
	 */
	public int[] idsToPrimitive(){
		//Obtengo el array desde el array list
		int total = this.getIds().size();
		Integer[] codes = new Integer[total];
		this.getIds().toArray(codes);
		
		int[] idsRetorno = new int[total];
		
		//Itero por los objetos Integer y obtengo su valor entero colocandolo luego en el array de retorno
		for(int i = 0; i < total ; i++){
			idsRetorno[i] = codes[i].intValue();
		}
		
		return idsRetorno;
	}
	
	/**
	 * Agrego el id de compañía al arreglo de ids
	 * @param id id a agregar en el arreglo
	 */
	public void addId(int id){
		//Agrego el id a la lista de ids de compañías
		this.getIds().add(id);
	}
	
	
	/**
	 * Elimina los roles WideFast-Track Admin y WideFast-Track User 
	 */
	public void delRolesStandard() throws Exception{
		//Creo el script sql
		String sql = "DELETE FROM ad_role WHERE (name = 'WideFast-Track Admin') OR (name = 'WideFast-Track User')";
		
		//Ejecuto
		ExecuterSql.executeUpdate(sql,this.getTrxName());
	}
	
	/**
	 * Desactiva los usuarios, roles y roles de usuarios a partir de los id de compañía
	 */
	public void ejecutar() throws Exception{
		//Cambio activos a los ids de compañía a los tableName del arreglo
		int total = this.getTablesNames().length;
		
		//Itero por las tablas y modifico
		for(int i = 0; i < total ; i++){
			this.changeActiveByClientIds(this.getTablesNames()[i], this.idsToPrimitive(), false);
		}
		
		//Elimina los roles standard (WideFast-Track Admin y WideFast-Track User)
		this.delRolesStandard();
	}
	
	public void deshacer() {
		
	}
}
