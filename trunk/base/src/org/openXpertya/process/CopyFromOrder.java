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

import org.openXpertya.model.MOrder;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CopyFromOrder extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_Order_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	//JOptionPane.showMessageDialog( null,"En CopyFromORder.prepare() :=","-...Fin", JOptionPane.INFORMATION_MESSAGE );
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_Order_ID" )) {
                p_C_Order_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
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
    	//JOptionPane.showMessageDialog( null,"En CopyFromOrder.doit() :=","-...Fin", JOptionPane.INFORMATION_MESSAGE );
        int To_C_Order_ID = getRecord_ID();

        log.info( "From C_Order_ID=" + p_C_Order_ID + " to " + To_C_Order_ID );

        if( To_C_Order_ID == 0 ) {
            throw new IllegalArgumentException( "Target C_Order_ID == 0" );
        }

        if( p_C_Order_ID == 0 ) {
            throw new IllegalArgumentException( "Source C_Order_ID == 0" );
        }

        MOrder from = new MOrder( getCtx(),p_C_Order_ID,get_TrxName());
        MOrder to   = new MOrder( getCtx(),To_C_Order_ID,get_TrxName());

        //

        int no = to.copyLinesFrom( from,false,false );    // no Attributes

        //

        return "@Copied@=" + no;
    }    // doIt
}    // CopyFromOrder



/*
 *  @(#)CopyFromOrder.java   02.07.07
 * 
 *  Fin del fichero CopyFromOrder.java
 *  
 *  Versión 2.2
 *
 */
