package org.openXpertya.fastrack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.openXpertya.util.Trx;

public class FastTracker implements Actionable{

	//Variables de instancia
	
	/** Módulos a ejecutar */
	
	private Collection<Actionable> modules; 
	
	/** Datos de configuración del fast-tracker*/
	
	private FTConfiguration ftConfig;
	
	/** Información de salida del proceso */
	
	private StringBuffer info = new StringBuffer("");
	
	//Constructores
	
	/**
	 * Constructor 
	 */

	
	public FastTracker(FTConfiguration ftConfig){
		this.setFtConfig(ftConfig);
		//Inicializo los módulos por defecto a ejecutar
		this.initializeDefaultModules();		
	}
	
	//Getters y Setters
	
	public void setModules(Collection<Actionable> modules) {
		this.modules = modules;
	}

	public Collection<Actionable> getModules() {
		return modules;
	}
	
	public void setFtConfig(FTConfiguration ftConfig) {
		this.ftConfig = ftConfig;
	}

	public FTConfiguration getFtConfig() {
		return ftConfig;
	}
	
	public void setInfo(StringBuffer info) {
		this.info = info;
	}

	public StringBuffer getInfo() {
		return info;
	}
	
	public String getTrxName() {
		return this.getFtConfig().getTrxName();
	}
	
	public int getClientTemplate() {
		return this.getFtConfig().getClient_template_id();
	}

	public int getNewClient() {
		return this.getFtConfig().getNew_client_id();
	}
	
	
	//Métodos varios
	
	//Métodos de colecciones	
	

	/**
	 * Agrega un módulo a la colección de módulo, el mismo será ejecutado o deshecho según sea 
	 * la operación a realizar
	 * @param module el módulo a agregar 
	 */
	
	public void addModule(Actionable module){
		//Agrego el módulo a la colección de módulos
		this.getModules().add(module);
	}

	/**
	 * Elimino un módulo de la colección de módulos, el mismo no será posteriormente utilizado en 
	 * los operaciones 
	 * @param módulo el módulo a eliminar
	 */
	
	public void removeModule(Actionable module){
		//Elimino el módulo de la colección
		this.getModules().remove(module);
	}
	
	//Métodos de transacción
	
	/**
	 * Crea y retorna una transacción
	 * @return una nueva transacción 
	 */
	
	public Trx createTrx(){
		//Creo la transacción
		return Trx.get(this.getTrxName(), true);
	}
	
	/**
	 * Retorna una transacción 
	 * @return la transacción con el nombre contenido en la variable de instancia o una nueva
	 */
	public Trx getTrx(){
		//Me fijo primero si esta la transacción con ese nombre
		Trx trx = Trx.get(this.getTrxName(), false);
		
		//Si no existe, la creo
		if( trx == null){
			trx = createTrx();
		}
		
		return trx;
	}
	
	
	/**
	 * Inicializa los módulos por defecto a ejecutar
	 */
	public void initializeDefaultModules(){
		ArrayList<Actionable> mods = new ArrayList<Actionable>();  
		mods.add(new FTGeneralActivator("ad_window",this.getTrxName()));
		mods.add(new FTGeneralActivator("ad_tab",this.getTrxName()));
		mods.add(new FTGeneralActivator("ad_workflow",this.getTrxName()));
		mods.add(new FTGeneralActivator("ad_process",this.getTrxName()));
		mods.add(new FTGeneralActivator("ad_ref_list",this.getTrxName()));
		mods.add(new FTWindowsZoom(this.getTrxName()));
		mods.add(new FTColumnsValidation(this.getTrxName()));
		mods.add(new FTFieldsEliminate(this.getTrxName()));
		mods.add(new FTFieldsDisplayLogic(this.getTrxName()));
		mods.add(new FTFieldsGroups(this.getTrxName()));
		mods.add(new FTFieldsReadOnly(this.getTrxName()));
		mods.add(new FTFieldsDefaultValue(this.getTrxName()));
		mods.add(new FTFieldsDisplayInGrid(this.getTrxName()));
		mods.add(new FTClient(this.getFtConfig()));
		mods.add(new FTRolesAndUsersEliminate(this.getTrxName(),this.getNewClient()));
	//	mods.add(new FTGenerate(this.getTrxName()));
		this.setModules(mods);
	}
	
	/**
	 * Ejecuta el fast-tracking
	 */
	
	public void ejecutar() {
		//Obtengo el iterador de los módulos para recorrerlos
		Iterator<Actionable> iteraModules = this.getModules().iterator();
		
		this.getTrx().start();
		
		try{
		
			//Recorro los módulos y los ejecuto 
			while(iteraModules.hasNext()){
				iteraModules.next().ejecutar();
			}
			
			//Commiteo y cierro la transacción si todo salió bien
			this.getTrx().commit();
			this.getTrx().close();
			this.getInfo().append("\n\nProceso completo correctamente");
		}catch(Exception e){
			//Se encontraron errores, rollback y cierro la transacción
			this.getTrx().rollback();
			this.getTrx().close();
			this.getInfo().append("\n\nSe produjo un error dentro del proceso");
			this.getInfo().append("\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Deshace el fast-tracking
	 */
	
	public void deshacer() {
		//Obtengo el iterador de los módulos para recorrerlos
		Iterator<Actionable> iteraModules = this.getModules().iterator();
		
		this.getTrx().start();
		
		try{
		
			//Recorro los módulos y los ejecuto 
			while(iteraModules.hasNext()){
				iteraModules.next().deshacer();
			}
			
			//Commiteo y cierro la transacción si todo salió bien
			this.getTrx().commit();
			this.getTrx().close();
			
		}catch(Exception e){
			//Se encontraron errores, rollback y cierro la transacción
			this.getTrx().rollback();
			this.getTrx().close();
		}
	}


	
}
