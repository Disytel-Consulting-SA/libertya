package org.openXpertya.util;

import java.lang.reflect.Constructor;

public class ClassUtil {

	/**
	 * Obtiene una instancia de la clase parámetro, vía el contructor con los
	 * tipos de datos parámetro y los valores para esos tipos de datos
	 * 
	 * @param className
	 *            nombre de la clase
	 * @param parameterTypes
	 *            tipo de dato de los parámetros
	 * @param initArgs
	 *            argumentos para el constructor
	 * @return la instancia de la clase parámetro en base al constructor y
	 *         argumentos
	 * @throws Exception
	 *             en caso de error
	 */
	public static Object getInstance(String className, Class<?>[] parameterTypes, Object[] initArgs) throws Exception{
		Class<?> processorClass = Class.forName(className);
		Constructor<?> constructor = processorClass.getConstructor(parameterTypes);
		return constructor.newInstance(initArgs);
	}
	
}
