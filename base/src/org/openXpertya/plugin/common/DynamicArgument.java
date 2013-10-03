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
	 * 		<code>execute(String param1, String param2, int param3, ArrayList<Integer> param4)</code> <br>
	 * <br>
	 * la invocación <code>execute('foo', 'bar', 43, {x, y, z}) se convierte en</code><br>
	 * <br>
	 * <code>
	 * 	content[0]: param1 = {'foo'}<br>
	 * 	content[1]: param2 = {'bar'}<br>
	 * 	content[2]: param3 = {43}<br>
	 * 	content[3]: param4 = {x, y, z}<br>
	 * </code>
	 */
	public ArrayList<HashMap<String, ArrayList<String>>> content = new ArrayList<HashMap<String, ArrayList<String>>>(); 
	
	/**
	 * Constructor por defecto
	 */
	public DynamicArgument() {
		HashMap<String, ArrayList<String>> emptyArgs = new HashMap<String, ArrayList<String>>();
		content.add(emptyArgs);
	}

	
	public ArrayList<HashMap<String, ArrayList<String>>> getContent() {
		return content;
	}

	public void setContent(ArrayList<HashMap<String, ArrayList<String>>> content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		if (content!=null) {
			for (HashMap<String, ArrayList<String>> params : content) {
				if (params!=null) {
					for (String argName : params.keySet()) {
						if (params.get(argName)!=null) {
							out.append("\n ").append(argName).append(" : ");
							for (String value : params.get(argName)) {
								out.append(value).append(" ");
							}
						}
					}
				}
			}
		}
		return out.toString();
	}
	
}
