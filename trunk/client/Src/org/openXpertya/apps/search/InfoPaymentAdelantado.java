package org.openXpertya.apps.search;

import java.awt.Frame;
import java.math.BigDecimal;

public class InfoPaymentAdelantado extends InfoPaymentChequeTerceros {

	public InfoPaymentAdelantado(Frame frame, boolean modal, int WindowNo, String value, boolean multiSelection,
			String whereClause) {
		super(frame, modal, WindowNo, value, multiSelection, whereClause);
		// TODO Auto-generated constructor stub
	}
	
	protected BigDecimal getPaymentAmt(int i) {
		return (BigDecimal)p_table.getValueAt(i, 11);
	}

}
