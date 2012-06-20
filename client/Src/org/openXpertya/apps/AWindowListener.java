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

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AWindowListener extends WindowAdapter {

    /**
     * Constructor de la clase ...
     *
     *
     * @param win
     * @param l
     */

    public AWindowListener( Window win,WindowStateListener l ) {
        m_window   = win;
        m_listener = l;
        win.addWindowListener( this );
    }    // AWindowListener

    /** Descripción de Campos */

    private Window m_window;

    /** Descripción de Campos */

    private WindowStateListener m_listener;

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void windowClosed( WindowEvent e ) {
        m_listener.windowStateChanged( e );
    }    // windowClosed
}    // AWindowListenr



/*
 *  @(#)AWindowListener.java   02.07.07
 * 
 *  Fin del fichero AWindowListener.java
 *  
 *  Versión 2.2
 *
 */
