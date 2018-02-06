package org.openXpertya.print.fiscal.hasar;

import org.openXpertya.print.fiscal.comm.FiscalComm;

/**
 * Clase controladora de la impresora fiscal Hasar P441F. Posee el mismo
 * comportamiento que la P715F versión 2 en adelante. 
 * 
 * @author Disytel
 *
 */
public class HasarPrinterP441F extends HasarPrinterP715F_v2 {

	public HasarPrinterP441F() {
		// TODO Auto-generated constructor stub
	}

	public HasarPrinterP441F(FiscalComm fiscalComm) {
		super(fiscalComm);
		// TODO Auto-generated constructor stub
	}

}
