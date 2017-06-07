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
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CButton;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Waiting extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param text
     * @param canNotWait
     * @param timer
     */

    public Waiting( Frame owner,String text,boolean canNotWait,int timer, ProcessCtl ctlOwner ) {
        super( owner,Msg.getMsg( Env.getCtx(),"Processing" ));
        this.ctlOwner = ctlOwner; 
        init( text,canNotWait,timer );
    }    // Waiting

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param text
     * @param canNotWait
     * @param timer
     */

    public Waiting( Dialog owner,String text,boolean canNotWait,int timer ) {
        super( owner,Msg.getMsg( Env.getCtx(),"Processing" ));
        init( text,canNotWait,timer );
    }    // Waiting

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param canNotWait
     * @param timer
     */

    private void init( String text,boolean canNotWait,int timer ) {
        log.fine( text + " - Sec=" + timer );

        // don't show if 1 sec average

        if( timer == 1 ) {
            return;
        }

        try {
            jbInit();
            setText( text );

            if( !canNotWait ) {
                bDoNotWait.setVisible( false );
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Waiting",e );
        }

        // set progress Bar

        progressBar.setMinimum( 0 );
        progressBar.setMaximum( (timer < 5)
                                ?10
                                :timer );    // min 2 seconds

        // Timer

        m_timer = new Timer( 1000,this );    // every second
        m_timer.start();
        AEnv.showCenterWindow( getOwner(),this );
    }    // init

    /** Descripción de Campos */

    private int m_timervalue = 0;

    /** Descripción de Campos */

    private Timer m_timer;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Waiting.class );

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private CButton bDoNotWait = new CButton();

    /** Descripción de Campos */

    private CLabel infoLabel = new CLabel();

    /** Descripción de Campos */

    private FlowLayout southLayout = new FlowLayout();

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private JProgressBar progressBar = new JProgressBar();
    
    private CButton cancel = new CButton(Msg.getMsg(Env.getCtx(), "Cancel"));
    
    private ProcessCtl ctlOwner;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        this.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        this.setResizable( false );
        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        this.getContentPane().add( Box.createVerticalStrut( 8 ),BorderLayout.NORTH );
        this.getContentPane().add( Box.createHorizontalStrut( 8 ),BorderLayout.WEST );
        this.getContentPane().add( Box.createVerticalStrut( 8 ),BorderLayout.SOUTH );
        this.getContentPane().add( Box.createHorizontalStrut( 8 ),BorderLayout.EAST );
        mainPanel.setLayout( new BorderLayout( 5,5 ));
        this.getContentPane().add( mainPanel,BorderLayout.CENTER );

        //

        infoLabel.setFont( new java.awt.Font( "Dialog",3,14 ));
        infoLabel.setHorizontalAlignment( SwingConstants.CENTER );
        infoLabel.setHorizontalTextPosition( SwingConstants.RIGHT );
        infoLabel.setIcon( Env.getImageIcon( "OXP10030.gif" ));
        infoLabel.setIconTextGap( 10 );
        mainPanel.add( infoLabel,BorderLayout.NORTH );
        mainPanel.add( progressBar,BorderLayout.CENTER );
        
        cancel.addActionListener(this);
       	mainPanel.add( cancel,BorderLayout.SOUTH);

        

        //
//              bDoNotWait.setText(Msg.getMsg(Env.getCtx(), "DoNotWait"));
//              bDoNotWait.setToolTipText(Msg.getMsg(Env.getCtx(), "DoNotWaitInfo"));
//              bDoNotWait.addActionListener(this);
//              southPanel.setLayout(southLayout);
//              southPanel.add(bDoNotWait, null);
//              mainPanel.add(southPanel, BorderLayout.SOUTH);

    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setText( String text ) {
        infoLabel.setText( text );
    }    // setText

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
    	if (e.getSource() == cancel) {
    		ctlOwner.getProcessCallInstance().cancelProcess();
    	}
    	
        if( e.getSource() == bDoNotWait ) {
            doNotWait();
        }

        //

        progressBar.setValue( m_timervalue++ );

        if( m_timervalue > progressBar.getMaximum()) {
            m_timervalue = progressBar.getMinimum();
        }



    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param max
     */

    public void setTimerEstimate( int max ) {
        progressBar.setMaximum( max );
    }    // setMaximum

    /**
     * Descripción de Método
     *
     */

    public void doNotWait() {
        dispose();
    }    // doNotWait

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_timer != null ) {
            m_timer.stop();
        }

        m_timer = null;
        super.dispose();
    }    // dispose
    
    public void setCancelable(boolean cancelable) {
    	cancel.setVisible(cancelable);
		pack();
    }
}    // Waiting



/*
 *  @(#)Waiting.java   02.07.07
 * 
 *  Fin del fichero Waiting.java
 *  
 *  Versión 2.2
 *
 */
