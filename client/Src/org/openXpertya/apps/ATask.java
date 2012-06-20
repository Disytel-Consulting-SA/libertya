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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Task;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ATask extends JFrame implements ActionListener {

    /**
     * Descripción de Método
     *
     *
     * @param title
     * @param command
     */

    static public void start( final String title,final String command ) {
        new Thread() {
            public void run() {
                new ATask( title,command );
            }
        }.start();
    }    // start

    /**
     * Constructor de la clase ...
     *
     */

    public ATask() {
        this( "","" );
    }    // ATask

    /**
     * Constructor de la clase ...
     *
     *
     * @param title
     * @param command
     */

    public ATask( String title,String command ) {
        super( title );
        log.info( title + " - " + command );

        try {
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"ATask",e );
        }

        AEnv.showCenterScreen( this );
        executeCommand( command );
    }    // ATask

    /** Descripción de Campos */

    private Task m_task = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ATask.class );

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private JScrollPane infoScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextArea info = new JTextArea();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        info.setEditable( false );
        info.setBackground( CompierePLAF.getFieldBackground_Inactive());
        infoScrollPane.getViewport().add( info,null );
        infoScrollPane.setPreferredSize( new Dimension( 500,300 ));
        this.getContentPane().add( infoScrollPane,BorderLayout.CENTER );
        this.getContentPane().add( confirmPanel,BorderLayout.SOUTH );

        //

        confirmPanel.addActionListener( this );
        confirmPanel.getOKButton().setEnabled( false );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param command
     */

    public void executeCommand( String command ) {
        String cmd = Msg.parseTranslation( Env.getCtx(),command );

        log.config( cmd );

        if( (command == null) || command.equals( "" )) {
            return;
        }

        if( (m_task != null) && m_task.isAlive()) {
            m_task.interrupt();
        }

        m_task = new Task( cmd );
        m_task.start();

        while( true ) {

            // Give it a bit of time

            try {
                Thread.sleep( 500 );
            } catch( InterruptedException ioe ) {
                log.log( Level.SEVERE,"ATask.executeCommand",ioe );
            }

            // Info to user

            StringBuffer sb = new StringBuffer();

            sb.append( m_task.getOut()).append( "\n-------\n" ).append( m_task.getErr()).append( "\n-------" );
            info.setText( sb.toString());

            // Are we done?

            if( !m_task.isAlive()) {
                confirmPanel.getCancelButton().setEnabled( false );
                confirmPanel.getOKButton().setEnabled( true );

                break;
            }
        }

        log.config( "ATask.executeCommand - done" );
    }    // executeCommand

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( (m_task != null) && m_task.isAlive()) {
            m_task.interrupt();
        }

        dispose();
    }    // actionPerformed
}    // ATask



/*
 *  @(#)ATask.java   02.07.07
 * 
 *  Fin del fichero ATask.java
 *  
 *  Versión 2.2
 *
 */
