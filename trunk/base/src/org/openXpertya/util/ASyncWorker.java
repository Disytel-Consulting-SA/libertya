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



package org.openXpertya.util;

import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.openXpertya.process.ProcessInfo;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ASyncWorker extends Thread {

    /**
     * Descripción de Método
     *
     *
     * @param parent
     * @param pi
     *
     * @return
     */

    public static ProcessInfo executeSync( ASyncProcess parent,ProcessInfo pi ) {
        ASyncWorker worker = new ASyncWorker( parent,pi );

        worker.start();

        try {
            worker.join();
        } catch( InterruptedException e ) {
            log.log( Level.SEVERE,"executeSync",e );
        }

        return worker.getResult();
    }    // executeSync

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ASyncWorker.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param pi
     */

    public ASyncWorker( ASyncProcess parent,ProcessInfo pi ) {
        m_parent = parent;
        m_pi     = pi;
    }    // ASuncWorker

    /** Descripción de Campos */

    private ProcessInfo m_pi;

    /** Descripción de Campos */

    private ASyncProcess m_parent;

    /**
     * Descripción de Método
     *
     */

    public void run() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                m_parent.lockUI( m_pi );
            }
        } );

        //

        m_parent.executeASync( m_pi );

        //

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                m_parent.unlockUI( m_pi );
            }
        } );
    }    // run

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ProcessInfo getResult() {
        return m_pi;
    }    // getResult
}    // ASyncWorker



/*
 *  @(#)ASyncWorker.java   02.07.07
 * 
 *  Fin del fichero ASyncWorker.java
 *  
 *  Versión 2.2
 *
 */
