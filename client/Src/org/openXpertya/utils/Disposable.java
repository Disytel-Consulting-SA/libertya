package org.openXpertya.utils;

import java.awt.Container;

/**
 * Interface que permite realizar dispose sobre los que lo implementan
 * 
 * @author Equipo de Desarrollo de Disytel
 *
 */

public interface Disposable {

	/**
	 * Realiza dispose de la clase que lo implementa
	 */
	public void dispose();
	
	/**
	 * @return nro de la ventana disposable
	 */
	public int getWindowNo();

	/**
	 * @return el container para que el mensaje se imprima dentro de él
	 */
	public Container getContainerForMsg();
}
