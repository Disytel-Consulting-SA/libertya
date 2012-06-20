package org.openXpertya.fastrack;

public class ValidationGenerate {

	private static String msg = null;
	
	//Getters y Setters 
	
	public static void setMsg(String msg) {
		ValidationGenerate.msg = msg;
	}

	public static String getMsg() {
		return msg;
	}

	/**
	 * Dependiendo la versión, determina si se puede completar o no
	 * @return true si está permitido, false cc
	 */
	public static boolean validate() {
		//Creo el módulo de verificación del fast-track
		FTGenerate gen = new FTGenerate();
		
		try {
			
			boolean retorno = gen.validate();
			
			ValidationGenerate.setMsg(gen.getInfo());
			
			//retorno las validaciones de las condiciones del sistema
			return retorno;
			
		}catch(Exception e){
			ValidationGenerate.setMsg(gen.getInfo());
			return false;
		}
	}
}
