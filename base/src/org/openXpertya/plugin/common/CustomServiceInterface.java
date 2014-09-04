package org.openXpertya.plugin.common;

import java.util.Properties;

/**
 * Soporte para invocaciones no explicitas, por ejemplo desde LYWS
 */
public interface CustomServiceInterface {

	/** Nombre de metodo a invocar por defecto */
	public static final String DEFAULT_METHOD_NAME = "execute";
	
	/**
	 * Metodo dinámico a implementar para dar soporte genérico
	 * 
	 * @param 
	 * 	args es la serie de parámetros necesarios para la invocación
	 * @param
	 * 	ctx es el contexto
	 * @param
	 * 	trxName el nombre de la transacción
	 * @return 
	 * 	el resultado correspondiente de la invocación, en la que se debe cargar
	 * 	no solo los valores resultantes, sino tambien los valores isError y errorMsg
	 */
	public DynamicResult execute(DynamicArgument args, Properties ctx, String trxName);
	
}
