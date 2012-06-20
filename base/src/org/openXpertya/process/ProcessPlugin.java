package org.openXpertya.process;

import org.openXpertya.model.MComponentVersion;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class ProcessPlugin extends SvrProcess {

	// Variables de instancia
	
	/** Component Version del registro actual */
	
	private MComponentVersion componentVersion;
	
	/*
	 * ----------------------------------------------------
	 *					METODOS HEREDADOS
	 * ----------------------------------------------------
	 */
	
	@Override
	protected void prepare() {
		
	}


	@Override
	protected String doIt() throws Exception {
		// Inicio transacción (chekpoint)
		getTrx(get_TrxName()).start();
		// Inicializar data
		initialize();
		// Verificar validaciones
		validate();
		// Procesar plugin
		processPlugin();
		// Retornar mensaje
		return "Plugin actualizado";
	}
	
	/*
	 * ----------------------------------------------------
	 */
		
	/**
	 * Inicializo datos necesarios
	 */
	private void initialize(){
		setComponentVersion(new MComponentVersion(getCtx(), getRecord_ID(), get_TrxName()));
	}
	
	/**
	 * Realizar las validaciones necesarias antes de realizar la operación
	 * @throws Exception
	 */
	private void validate() throws Exception{
		if(!getComponentVersion().isCurrentDevelopment()){
			String sql = "SELECT count(*) FROM ad_componentversion WHERE currentDevelopment = 'Y'";
			int actives = DB.getSQLValue(get_TrxName(), sql);
			if(actives > 0){
				throw new Exception(Msg.getMsg(getCtx(), "ExistCurrentPlugin"));
			}
		}
	}
	
	/**
	 * Proceso el plugin, activo o desactivo desarrollo y 
	 * coloco las tablas necesarias para loguearlas. 
	 * @throws Exception
	 */
	private void processPlugin() throws Exception{
		boolean toCurrent = !getComponentVersion().isCurrentDevelopment();
		// Actualizar el flag de desarrollo de plugin
		getComponentVersion().setStartDevelopment(toCurrent?MComponentVersion.STARTDEVELOPMENT_EndDevelopment:MComponentVersion.STARTDEVELOPMENT_StartDevelopment);
		getComponentVersion().setCurrentDevelopment(toCurrent);
		if(!getComponentVersion().save()){
			log.severe("Error al modificar el plugin en desarrollo");
			throw new Exception("Error al modificar el plugin en desarrollo");
		}			
		Env.setContext(Env.getCtx(),"#AD_ComponentVersion_ID",toCurrent?getComponentVersion().getID():0);
		getTrx(get_TrxName()).commit();
	}


	// Getters y Setters
	
	private void setComponentVersion(MComponentVersion componentVersion) {
		this.componentVersion = componentVersion;
	}


	private MComponentVersion getComponentVersion() {
		return componentVersion;
	}
}
