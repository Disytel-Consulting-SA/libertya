package org.openXpertya.reflection;


/**
 * La llamada propiamente dicha. Esta clase extiende de thread y es la encargada
 * de realizar la llamada al método. Una vez que finalizar el timeout
 * configurado este thread ya no tiene efecto. Tener en cuenta si dentro del
 * método llamado se manejan transacciones propias. Al finalizar esta llamada a
 * método, se retorna al que realiza esta llamada un objeto de la clase
 * CallResult con la información del resultado como ser si fue error o no, un
 * mensaje y/o el objeto de retorno del método.
 * 
 * @author Matías Cap
 * 
 */

public abstract class Call extends Thread {

	// Variables de instancia
	
	/**
	 * Llamador. Cuando finaliza la llamada se debe avisar al llamador para que
	 * siga su ejecución
	 */
	
	private ClientCall client;
	
	
	// Constructores
	
	public Call(){
		
	}
	
	public Call(ClientCall clientCall){
		setClient(clientCall);
	}

	/**
	 * Obtengo el mensaje de la excepción parámetro
	 * 
	 * @param e
	 *            e excepción
	 * @return mensaje de la excepción o vacío en caso que no exista ninguno
	 */
	protected String getMsgFrom(Exception e){
		String msg = "";
		if(e.getMessage() != null){
			msg = e.getMessage();
		}
		else if(e.getCause() != null){
			msg = e.getCause().getMessage();
		}
		return msg;
	}
	
	// Getters y Setters 
		
	public void setClient(ClientCall client) {
		this.client = client;
	}

	public ClientCall getClient() {
		return client;
	}
	
}
