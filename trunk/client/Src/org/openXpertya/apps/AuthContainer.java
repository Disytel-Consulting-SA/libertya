package org.openXpertya.apps;

import org.openXpertya.pos.model.User;

/**
 * 
 * @author Equipo de Desarrollo de Disytel
 * 
 */

public interface AuthContainer {

	/**
	 * @param name
	 * @return Retorna mensaje
	 */
	public String getMsg(String name);

	/**
	 * @param userID
	 * @return Retorna usuario
	 */

	public User getUser(int userID);

	/**
	 * 
	 * @return si es para TPV
	 */
	public boolean isForPos();

}
