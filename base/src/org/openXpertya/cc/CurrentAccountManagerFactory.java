package org.openXpertya.cc;


/**
 * Esta clase está creada para usar un nexo de creación de managers de cuentas
 * corrientes de una entidad comercial determinada.
 * 
 * @author Matías Cap
 * 
 */

public class CurrentAccountManagerFactory {
	
	// Variables estáticas
	
	/** Manager de cuenta corriente actual */
	private static CurrentAccountManager manager = null;

	
	// Métodos estáticos
	
	/**
	 * @return Obtiene el manager actual, si no existe ninguno se crea en base a
	 *         los parámetro de la configuración del control de cuenta corriente
	 *         centralizado
	 */
	public static CurrentAccountManager getManager(CurrentAccountDocument document){
		clearCache();
		if(manager == null){
			manager = createLocalManager(document);
		}
		return manager;
	}
	
	/**
	 * Obtengo un manager de cuenta corriente con estrategias locales
	 * @return un manager con configuraciones locales
	 */
	public static CurrentAccountManager createLocalManager(CurrentAccountDocument document){
		CurrentAccountManager cam = new CurrentAccountLocalManager();
		cam.setSkipCurrentAccount(document.isSkipCurrentAccount());
		if(document.isSkipCurrentAccount()){
			cam.setBalanceStrategy(new BalanceLocalStrategySkipped()); 
		}
		return cam;
	}
	
	/**
	 * Limpiar cache con instancia actual
	 */
	public static void clearCache(){
		manager = null;
	}
}
