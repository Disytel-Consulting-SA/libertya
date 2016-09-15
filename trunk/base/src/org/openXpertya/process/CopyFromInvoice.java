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

import javax.swing.JOptionPane;

import org.openXpertya.model.MInvoice;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CopyFromInvoice extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_Invoice_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	//JOptionPane.showMessageDialog( null,"En CopyFromInvoice.prepare() :=","-...Fin", JOptionPane.INFORMATION_MESSAGE );
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_Invoice_ID" )) {
                m_C_Invoice_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
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
    	//JOptionPane.showMessageDialog( null,"En CopyFromInvoice.doit() :=","-...Fin", JOptionPane.INFORMATION_MESSAGE );
        int To_C_Invoice_ID = getRecord_ID();

        log.info( "From C_Invoice_ID=" + m_C_Invoice_ID + " to " + To_C_Invoice_ID );

        if( To_C_Invoice_ID == 0 ) {
            throw new IllegalArgumentException( "Target C_Invoice_ID == 0" );
        }

        if( m_C_Invoice_ID == 0 ) {
            throw new IllegalArgumentException( "Source C_Invoice_ID == 0" );
        }

        MInvoice from = new MInvoice( getCtx(),m_C_Invoice_ID,null );
        MInvoice to   = new MInvoice( getCtx(),To_C_Invoice_ID,null );

        //

        int no = to.copyLinesFrom( from,false,false,false );

        //

        return "@Copied@=" + no;
    }    // doIt
}    // CopyFromInvoice



/*
 *  @(#)CopyFromInvoice.java   02.07.07
 * 
 *  Fin del fichero CopyFromInvoice.java
 *  
 *  Versión 2.2
 *
 */
