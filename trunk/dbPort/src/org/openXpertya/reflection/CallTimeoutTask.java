package org.openXpertya.reflection;

import java.util.TimerTask;

/**
 * Tarea a ejecutar cuando ocurre el evento de timeout. Las acciones que realiza
 * son setear el mensaje de timeout dentro del resultado y notificarle al
 * cliente, instancia de la clase ClientCall, del evento timeout.
 * 
 * @author Matías Cap
 * 
 */

public class CallTimeoutTask extends TimerTask {

	// Variables de instancia
	
	/**
	 * Llamador. Cuando se ejecuta este thread, se debe avisar al llamador el
	 * timeout de la llamada
	 */
	
	private ClientCall client;
	
	// Constructores
	
	public CallTimeoutTask(ClientCall clientCall) {
		setClient(clientCall);
	}

	@Override
	public void run() {
		// Instanciar un resultado de llamada con el error de timeout
		CallResult result = new CallResult();
		result.setMsg(getClient().getConfig().getCtx(), "CallTimeout", true);
		// Avisar la condición de timeout y desbloquear al llamador	
		getClient().timeout(result);
	}
	
	// Getters y Setters
	
	private void setClient(ClientCall client) {
		this.client = client;
	}

	private ClientCall getClient() {
		return client;
	}

}
