package org.openXpertya.cc;

public interface CurrentAccountDocument {

	/**
	 * @return true si es transacción de ventas, false caso contrario
	 */
	public boolean isSOTrx();
	
	/**
	 * @return true si se debe omitir toda operación relacionada a cuentas
	 *         corrientes, false caso contrario
	 */
	public boolean isSkipCurrentAccount();
	
}
