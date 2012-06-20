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

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInOutConfirm;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InOutCreateConfirm extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_InOut_ID = 0;

    /** Descripción de Campos */

    private String p_ConfirmType = null;

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
            } else if( name.equals( "ConfirmType" )) {
                p_ConfirmType = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_M_InOut_ID = getRecord_ID();
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
        log.info( "M_InOut_ID=" + p_M_InOut_ID + ", Type=" + p_ConfirmType );

        MInOut shipment = new MInOut( getCtx(),p_M_InOut_ID,null );

        if( shipment.getID() == 0 ) {
            throw new IllegalArgumentException( "Not found M_InOut_ID=" + p_M_InOut_ID );
        }

        //

        MInOutConfirm confirm = MInOutConfirm.create( shipment,p_ConfirmType,true );

        if( confirm == null ) {
            throw new Exception( "Cannot create Confirmation for " + shipment.getDocumentNo());
        }

        //

        return confirm.getDocumentNo();
    }    // doIt
}    // InOutCreateConfirm



/*
 *  @(#)InOutCreateConfirm.java   02.07.07
 * 
 *  Fin del fichero InOutCreateConfirm.java
 *  
 *  Versión 2.2
 *
 */
