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

import java.util.logging.Level;

import org.openXpertya.model.MCommission;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CommissionCopy extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_Commission_ID = 0;

    /** Descripción de Campos */

    private int p_C_CommissionTo_ID = 0;

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
            } else if( name.equals( "C_Commission_ID" )) {
                p_C_Commission_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_C_CommissionTo_ID = getRecord_ID();
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
        log.info( "doIt - C_Commission_ID=" + p_C_Commission_ID + " - copy to " + p_C_CommissionTo_ID );

        MCommission comFrom = new MCommission( getCtx(),p_C_Commission_ID,get_TrxName());

        if( comFrom.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "No From Commission" );
        }

        MCommission comTo = new MCommission( getCtx(),p_C_CommissionTo_ID,get_TrxName());

        if( comTo.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "No To Commission" );
        }

        //

        int no = comTo.copyLinesFrom( comFrom );

        return "@Copied@: " + no;
    }    // doIt
}    // CommissionCopy



/*
 *  @(#)CommissionCopy.java   02.07.07
 * 
 *  Fin del fichero CommissionCopy.java
 *  
 *  Versión 2.2
 *
 */
