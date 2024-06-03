package org.openXpertya.process;

import java.math.BigDecimal;

import org.openXpertya.util.DB;

public class CintoloUpdateCreditLimit extends SvrProcess {
	private BigDecimal adjustment;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			log.fine("prepare - " + para[i]);

			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else if (name.equalsIgnoreCase("Adjustment")) {
				adjustment = (BigDecimal) para[i].getParameter();
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		System.out.println("El ajuste es: " + adjustment);
		if(adjustment.floatValue() < -100) {
	    	throw new Exception("No se puede aplicar un ajusto menor al 100%");
	    }
		
		DB.executeUpdate(
				" UPDATE c_bpartner SET "
				+ "	cintolo_checks_limit = cintolo_checks_limit * (1 + " + adjustment + " * 0.01), "
				+ "	so_creditlimit = so_creditlimit * (1 + " + adjustment + " * 0.01) ", 
				get_TrxName());
		
		return ("Ajuste aplicado del " + adjustment.floatValue() + "%");
	}

}
