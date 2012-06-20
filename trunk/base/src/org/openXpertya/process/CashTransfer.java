package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Msg;

public class CashTransfer extends SvrProcess {

	/** Libro de Caja origen */
	private int fromCashID = 0;
	/** Libro de Caja destino */
	private int toCashID = 0;
	/** Importe a transferir */
	private BigDecimal amount = BigDecimal.ZERO;
	
	@Override
	protected void prepare() {
        ProcessInfoParameter[] para = getParameter();
        int cashID = 0;
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if (name.equals("C_Cash_ID")) {
                cashID = para[i].getParameterAsInt();
            } else if (name.equals("FromCashID")) {
                fromCashID = para[i].getParameterAsInt();
            } else if (name.equals( "ToCashID")) {
                toCashID = para[i].getParameterAsInt();
            } else if (name.equals("Amount")) {
                amount = (BigDecimal)para[i].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
        
        // Si CashID es mayor que cero implica que se invocó el proceso de Caja
        // Diaria en el cual se debe tomar el Libro de Caja de la Caja Diaria.
        if (cashID > 0) {
        	MPOSJournal journal = new MPOSJournal(getCtx(), getRecord_ID(),get_TrxName());
        	// Si el importe es negativo implica que que se está haciendo una 
        	// transferencia:
        	// Caja Diara -> Caja Parámetro
        	if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        		fromCashID = journal.getC_Cash_ID();
        		toCashID = cashID;
        	// Si el importe es positivo:
        	// Caja Parámetro -> Caja Diaria	
        	} else {
        		fromCashID = cashID;
        		toCashID = journal.getC_Cash_ID();
        	}
        	amount = amount.abs();
        }
        
        
	}

	@Override
	protected String doIt() throws Exception {
		MCash.transferCash(fromCashID, toCashID, amount, getCtx(), get_TrxName());
		return "";
	}

}
