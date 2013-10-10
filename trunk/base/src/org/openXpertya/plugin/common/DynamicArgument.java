package org.openXpertya.plugin.common;

import java.util.ArrayList;
import java.util.HashMap;

public class DynamicArgument {

	/** 
	 * Nomina dinámica de argumentos. 
	 * 	La misma es una lista que contiene una map con el nombre del parametro y su valor, 
	 * 	el cual puede bien ser un unico valor o una lista, dependiendo el caso <br>
	 * <br>
	 * Ejemplo: para el metodo con los siguientes parámetros:<br>	
	 * <br>
	 * 		<code>execute(String param1, String param2, int param3, Integer[] param4)</code> <br>
	 * <br>
	 * la invocación <code>execute('foo', 'bar', 43, {9, 8, 7}) se convierte en</code><br>
	 * <br>
	 * <code>
	 * 	param1 = {'foo'}<br>
	 * 	param2 = {'bar'}<br>
	 * 	param3 = {'43'}<br>
	 * 	param4 = {'9', '8', '7'}<br>
	 * </code>
	 */
	public HashMap<String, ArrayList<String>> content = new HashMap<String, ArrayList<String>>(); 
	
	
	public HashMap<String, ArrayList<String>> getContent() {
		return content;
	}

	public void setContent(HashMap<String, ArrayList<String>> content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		if (content!=null) {
			for (String argName : content.keySet()) {
				if (content.get(argName)!=null) {
					out.append("\n ").append(argName).append(" : ");
					for (String value : content.get(argName)) {
						out.append(value).append(" ");
					}
				}
			}
		}
		return out.toString();
	}
	
}
