package org.openXpertya.apps.search;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.report.NumeroCastellano;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

public class InfoPaymentChequeTerceros extends InfoPayment {

	public InfoPaymentChequeTerceros(Frame frame, boolean modal, int WindowNo, String value, boolean multiSelection,
			String whereClause) {
		super(frame, modal, WindowNo, value, multiSelection, whereClause);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void mouseClicked( MouseEvent e ) {
		if(p_table.getSelectedRow() != -1) {
			if (p_multiSelection /*&& isDoubleClickTogglesSelection()*/)
			{
				int selectedRecords = 0;
				BigDecimal selectedAmount = BigDecimal.ZERO;
				
				for (int i = 0; i < p_table.getRowCount(); i++) {
					IDColumn id = (IDColumn)p_table.getValueAt(i, 0);
					BigDecimal payConvertedAmt = getPaymentAmt(i);
					
					if (id.isSelected()) {
						selectedRecords++;
						selectedAmount = selectedAmount.add(payConvertedAmt); 
					} 
				}
								
				BigDecimal toPayAmt = new BigDecimal(Env.getContext( Env.getCtx(),p_WindowNo,"ToPayAmt"));
				BigDecimal pendingAmt = toPayAmt.subtract(selectedAmount);
				
				DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount);
				
				String status = (selectedRecords > 0 ? (NumeroCastellano.numeroACastellano(selectedRecords) + "registros seleccionados. " + 
								"Suma: $ " + format.format(selectedAmount) + ". ") : "") +
								"Total a pagar: $ " + format.format(toPayAmt) + ". " + 
								"Saldo: $ " + format.format(pendingAmt) + ".";
				
				setStatusDB(status);
			} else      	
				dispose( true );    // double_click same as OK
        }
	}
	
	protected BigDecimal getPaymentAmt(int i) {
		return (BigDecimal)p_table.getValueAt(i, 10);
	}

}
