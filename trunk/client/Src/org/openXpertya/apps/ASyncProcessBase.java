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



package org.openXpertya.apps;

import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.ASyncWorker;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Splash;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class ASyncProcessBase implements ASyncProcess {

    /**
     * Constructor de la clase ...
     *
     *
     * @param pi
     */

    public ASyncProcessBase( ProcessInfo pi ) {
        m_pi = pi;
    }    // ASyncProcessBase

    /** Descripción de Campos */

    private ProcessInfo m_pi;

    /** Descripción de Campos */

    private boolean m_isLocked = false;

    /** Descripción de Campos */

    private Splash m_splash;

    /**
     * Descripción de Método
     *
     */

    void start() {
        if( isUILocked()) {    // don't start twice
            return;
        }

        ASyncWorker worker = new ASyncWorker( this,m_pi );

        worker.start();    // calls lockUI, executeASync, unlockUI
    }                      // start

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void lockUI( ProcessInfo pi ) {
        m_isLocked = true;
        m_splash   = new Splash( Msg.getMsg( Env.getCtx(),"Processing" ));
        m_splash.toFront();
    }    // lockUI

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void unlockUI( ProcessInfo pi ) {
        m_isLocked = false;
        m_splash.dispose();
        m_splash = null;
    }    // unlockUI

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUILocked() {
        return m_isLocked;
    }    // isLoacked

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public abstract void executeASync( ProcessInfo pi );
}    // ASyncProcessBase



/*
 *  @(#)ASyncProcessBase.java   02.07.07
 * 
 *  Fin del fichero ASyncProcessBase.java
 *  
 *  Versión 2.2
 *
 */
