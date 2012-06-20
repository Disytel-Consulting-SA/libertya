/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoicePaySchedule;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InvoicePayScheduleValidate extends SvrProcess {

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "C_InvoicePaySchedule_ID=" + getRecord_ID());

        MInvoicePaySchedule[] schedule = MInvoicePaySchedule.getInvoicePaySchedule( getCtx(),0,getRecord_ID(),null );

        if( schedule.length == 0 ) {
            throw new IllegalArgumentException( "InvoicePayScheduleValidate - No Schedule" );
        }

        // Get Invoice

        MInvoice invoice = new MInvoice( getCtx(),schedule[ 0 ].getC_Invoice_ID(),null );

        if( invoice.getID() == 0 ) {
            throw new IllegalArgumentException( "InvoicePayScheduleValidate - No Invoice" );
        }

        //

        BigDecimal total = Env.ZERO;

        for( int i = 0;i < schedule.length;i++ ) {
            BigDecimal due = schedule[ i ].getDueAmt();

            if( due != null ) {
                total = total.add( due );
            }
        }

        boolean valid = invoice.getGrandTotal().compareTo( total ) == 0;

        invoice.setIsPayScheduleValid( valid );
        invoice.save();

        // Schedule

        for( int i = 0;i < schedule.length;i++ ) {
            if( schedule[ i ].isValid() != valid ) {
                schedule[ i ].setIsValid( valid );
                schedule[ i ].save();
            }
        }

        String msg = "@OK@";

        if( !valid ) {
            msg = "@GrandTotal@ = " + invoice.getGrandTotal() + " <> @Total@ = " + total + "  - @Difference@ = " + invoice.getGrandTotal().subtract( total );
        }

        return Msg.parseTranslation( getCtx(),msg );
    }    // doIt
}    // InvoicePayScheduleValidate



/*
 *  @(#)InvoicePayScheduleValidate.java   02.07.07
 * 
 *  Fin del fichero InvoicePayScheduleValidate.java
 *  
 *  Versión 2.2
 *
 */
