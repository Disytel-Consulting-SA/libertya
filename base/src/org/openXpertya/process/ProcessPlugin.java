package org.openXpertya.process;

import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.X_AD_Component;
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
		return responseMessage();
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
	
	/** Mensaje de respuesta al usuario */
	protected String responseMessage() {
		// Verificar si ya existen entradas en el changelog con el prefijo especificado, a fin de brindar 
		// un warning al developer indicando que dicho micro componente ya fue usado previamente
		X_AD_Component component = new X_AD_Component(getCtx(), getComponentVersion().getAD_Component_ID(), null);
		if (component.isMicroComponent() && getComponentVersion().isCurrentDevelopment()) {
			int count = DB.getSQLValue(null, 	" select count(1) " +
												" from ad_changelog " +
												" where substring(changeloguid from 1 for position('-' in changeloguid)-1) = '" + component.getPrefix() + "'");
			if (count>0) {
				return "Plugin actualizado. WARNING! Existen " + count + " entradas en el changelog con el prefijo de microcomponente " + component.getPrefix();
			}
		}
		return "Plugin actualizado";
	}
}
