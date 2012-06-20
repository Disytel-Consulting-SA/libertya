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



package org.openXpertya.pos;

import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PosKeyboardFocusManager extends DefaultKeyboardFocusManager implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public PosKeyboardFocusManager() {
        super();
    }    // PosKeyboardFocusManager

    /** Descripción de Campos */

    private LinkedList m_fifo = new LinkedList();

    /** Descripción de Campos */

    private long m_lastWhen = 0;

    /** Descripción de Campos */

    private javax.swing.Timer m_timer = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( PosKeyboardFocusManager.class );

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_timer != null ) {
            m_timer.stop();
        }

        m_timer = null;

        if( m_fifo != null ) {
            m_fifo.clear();
        }

        m_fifo = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     */

    public void start() {

        // Unqueue time - 200 ms

        int delay = 200;

        log.fine( "PosKeyboardFocusManager.start - " + delay );

        if( m_timer == null ) {
            m_timer = new javax.swing.Timer( delay,this );
        }

        if( !m_timer.isRunning()) {
            m_timer.start();
        }
    }    // start

    /**
     * Descripción de Método
     *
     */

    public void stop() {
        log.fine( "PosKeyboardFocusManager.stop - " + m_timer );

        if( m_timer != null ) {
            m_timer.stop();
        }
    }    // stop

    /**
     * Descripción de Método
     *
     *
     * @param event
     *
     * @return
     */

    public boolean dispatchKeyEvent( KeyEvent event ) {
        if( event.getID() == KeyEvent.KEY_PRESSED ) {

            // Keyboard Repeat: 485 - then 31
            // log.fine( "PosKeyboardFocusManager.dispatchKeyEvent - "
            // + event.getWhen() + " - " + (event.getWhen() - m_lastWhen));

            m_lastWhen = event.getWhen();
        }

        if( m_timer == null ) {
            super.dispatchKeyEvent( event );
        } else {
            m_fifo.add( event );
        }

        return true;
    }    // displatchEvent

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( m_timer == null ) {
            return;
        }

        // log.fine( "actionPerformed - " + m_fifo.size());

        while( m_fifo.size() > 0 ) {
            KeyEvent event = ( KeyEvent )m_fifo.removeFirst();

            super.dispatchKeyEvent( event );
        }
    }    // actionPerformed
}    // PosKeyboardFocusManager



/*
 *  @(#)PosKeyboardFocusManager.java   02.07.07
 * 
 *  Fin del fichero PosKeyboardFocusManager.java
 *  
 *  Versión 2.2
 *
 */
