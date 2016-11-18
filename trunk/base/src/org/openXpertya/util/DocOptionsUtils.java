package org.openXpertya.util;

public class DocOptionsUtils {

	/**
	 * Incorpora una nueva acción al array de acciones, unicamente si la nueva acción no se encuentra en el array
	 * @param options array de acciones
	 * @param option nueva accion a incorporar
	 * @param index numero actual de acciones
	 * @return el index actualizado con la nueva opcion.  el valor será el mismo si la acción ya se encuentra en el array de acciones
	 */
	public static int addAction(String[] options, String option, int index) {
		for (String anOption : options) {
			if (anOption != null && anOption.equals(option))
				return index;
		}
		options[index++] = option;
		return index;
	}
	
	/**
	 * Remueve una acción de la lista de acciones, modificando el array pasado como argumento.
	 * No realiza cambios si no se encuentra la opción a remover.
	 * @param options array de acciones
	 * @param removeOption accion a eliminar
	 * @param index numero actual de acciones 
	 * @return el index actualizado con la opcion eliminada.  el valor será el mismo si la acción no se encontraba en el array de acciones
	 */
	public static int removeAction(String[] options, String removeOption, int index) {
		// Se realiza una copia simple del array, dado que la longitud del mismo no debe variar debido a eventuales incorporaciones de opciones adicionales
		int i = 0;
		String[] newOptions = new String[options.length];
		for (String anOption : options) {
			if (anOption!= null && anOption.equals(removeOption)) {
				index--;
			} else {
				newOptions[i++] = anOption;
			}
		}
		// copiar datos al array pasado como argumento del metodo invocante (pasaje por valor del array, pero por referencia de los elementos del mismo)
		for (int j=0; j<newOptions.length; j++)
			options[j] = newOptions[j];
		return index;
	}
}
