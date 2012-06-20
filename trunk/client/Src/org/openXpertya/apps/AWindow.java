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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.compiere.plaf.CompiereColor;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AWindow extends JFrame {

    /**
     * Constructor de la clase ...
     *
     */

	protected Frame m_realFrame = null;
	
    public AWindow() {
        super();

        //

        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        CompiereColor.setBackground( this );

        // Set UI Components

        this.setIconImage( org.openXpertya.OpenXpertya.getImage16());
        this.getContentPane().add( m_APanel,BorderLayout.CENTER );
        this.setGlassPane( m_glassPane );
    }    // AWindow

    public AWindow(Frame realFrame) {
    	this();
    	
    	this.m_realFrame = realFrame;
    }
    
    /** Descripción de Campos */

    private AGlassPane m_glassPane = new AGlassPane();

    /** Descripción de Campos */

    private APanel m_APanel = new APanel();

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AWindow.class );

    /**
     * Descripción de Método
     *
     *
     * @param AD_Workbench_ID
     *
     * @return
     */

    protected boolean initWorkbench( int AD_Workbench_ID ) {
        this.setName( "AWindow_WB_" + AD_Workbench_ID );

        boolean loadedOK = m_APanel.initPanel( AD_Workbench_ID,0,null );

        //

        commonInit();

        return loadedOK;
    }    // initWorkbench

    /**
     * Descripción de Método
     *
     *
     * @param AD_Window_ID
     * @param query
     *
     * @return
     */

    public boolean initWindow( int AD_Window_ID,MQuery query ) {
        this.setName( "AWindow_" + AD_Window_ID );

        //

        boolean loadedOK = m_APanel.initPanel( 0,AD_Window_ID,query, m_realFrame );

        commonInit();

        return loadedOK;
    }    // initWindow

    /**
     * Descripción de Método
     *
     */

    private void commonInit() {
        this.setJMenuBar( m_APanel.getMenuBar());
        this.setTitle( m_APanel.getTitle());

        //

        Image image = m_APanel.getImage();

        if( image != null ) {
            setIconImage( image );
        }
    }    // commonInit

    /**
     * Descripción de Método
     *
     *
     * @param busy
     */

    public void setBusy( boolean busy ) {
        if( busy == m_glassPane.isVisible()) {
            return;
        }

        log.config( getName() + " - " + busy );
        m_glassPane.setMessage( null );
        m_glassPane.setVisible( busy );

        if( busy ) {
            m_glassPane.requestFocus();
        }
    }    // setBusy

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     */

    public void setBusyMessage( String AD_Message ) {
        m_glassPane.setMessage( AD_Message );
    }    // setBusyMessage

    /**
     * Descripción de Método
     *
     *
     * @param time
     */

    public void setBusyTimer( int time ) {
        m_glassPane.setBusyTimer( time );
    }    // setBusyTimer

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );

//              System.out.println(">> Apps WE_" + e.getID()    // + " Frames=" + getFrames().length
//                      + " " + e);

    }    // processWindowEvent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public APanel getAPanel() {
        return m_APanel;
    }    // getAPanel

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        log.info( "" );

        if( m_APanel != null ) {
            m_APanel.dispose();
        }

        m_APanel = null;
        this.removeAll();
        super.dispose();

        // System.gc();

    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return getName();
    }    // toString

    // funcion a�adida por ConSerti para poder abrir la ventana directamente en detalle
    // abrinedo las lineas

    /**
     * Descripción de Método
     *
     */

    public void irADetalle() {
        m_APanel.cmd_detail();
    }

    /**
     * Descripción de Método
     *
     *
     * @param tabla_hijo
     * @param id_hijo
     */

    public void irATablaRegistro( String tabla_hijo,int id_hijo ) {
        m_APanel.irATablaRegistro( tabla_hijo,id_hijo );
    }
}    // AWindow



/*
 *  @(#)AWindow.java   02.07.07
 * 
 *  Fin del fichero AWindow.java
 *  
 *  Versión 2.2
 *
 */
