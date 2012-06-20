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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextArea;
import org.openXpertya.OpenXpertya;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class AboutBox extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public AboutBox( JFrame parent ) {
        super( parent,true );

        try {
            jbInit();
        } catch( Exception e ) {
            System.out.println( e.getMessage());
        }

        //

        labelVersion.setText( OpenXpertya.MAIN_VERSION + " @ " + OpenXpertya.DATE_VERSION );
        labelCopyright.setText( OpenXpertya.COPYRIGHT );
        infoArea.setText( CLogMgt.getInfo( null ).toString());

        // create 5 pt border

        Dimension d = imageControl.getPreferredSize();

        imageControl.setPreferredSize( new Dimension( d.width + 10,d.height + 10 ));

        //

        AEnv.positionCenterWindow( parent,this );
    }    // AWindow_AboutBox

    /** Descripción de Campos */

    private CPanel panel = new CPanel();

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private JLabel imageControl = new JLabel();

    /** Descripción de Campos */

    private JLabel labelHeading = new JLabel();

    /** Descripción de Campos */

    private JLabel labelVersion = new JLabel();

    /** Descripción de Campos */

    private JLabel labelCopyright = new JLabel();

    /** Descripción de Campos */

    private JLabel labelDescription = new JLabel();

    /** Descripción de Campos */

    private BorderLayout panelLayout = new BorderLayout();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel headerPanel = new CPanel();

    /** Descripción de Campos */

    private GridLayout headerLayout = new GridLayout();

    /** Descripción de Campos */

    private CTextArea infoArea = new CTextArea();

    /** Descripción de Campos */

    private BorderLayout northLayout = new BorderLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( false );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setTitle( Msg.translate( Env.getCtx(),"About" ));
        CompiereColor.setBackground( this );

        //

        setResizable( false );
        /*
        labelHeading.setFont( new java.awt.Font( "Dialog",1,14 ));
        labelHeading.setForeground( new java.awt.Color( 255, 153, 51 ) );
        labelHeading.setHorizontalAlignment( SwingConstants.CENTER );
        labelHeading.setHorizontalTextPosition( SwingConstants.CENTER );
        labelHeading.setText( "Soluci\u00f3n empresarial global" );
        */
        labelVersion.setHorizontalAlignment( SwingConstants.CENTER );
        labelVersion.setHorizontalTextPosition( SwingConstants.CENTER );
        labelVersion.setText( "." );
        labelCopyright.setHorizontalAlignment( SwingConstants.CENTER );
        labelCopyright.setHorizontalTextPosition( SwingConstants.CENTER );
        labelCopyright.setText( "." );
        /*
        labelDescription.setForeground( Color.blue );
        labelDescription.setHorizontalAlignment( SwingConstants.CENTER );
        labelDescription.setHorizontalTextPosition( SwingConstants.CENTER );
        labelDescription.setText( OpenXpertya.getURL());
        */

        //

        /*
        imageControl.setFont( new java.awt.Font( "Serif",2,10 ));
        imageControl.setForeground( Color.blue );
        */
        imageControl.setFont( new java.awt.Font( "SansSerif",1,14 ));
        imageControl.setForeground( new java.awt.Color( 255, 153, 51 ) );
        imageControl.setAlignmentX(( float )0.5 );
        imageControl.setHorizontalAlignment( SwingConstants.CENTER );
        imageControl.setHorizontalTextPosition( SwingConstants.CENTER );
        imageControl.setIcon( OpenXpertya.getImageIcon32() );
        imageControl.setText( OpenXpertya.getSubtitle());
        imageControl.setVerticalTextPosition( SwingConstants.BOTTOM );

        //

        mainPanel.setLayout( mainLayout );
        mainLayout.setHgap( 10 );
        mainLayout.setVgap( 10 );
        northPanel.setLayout( northLayout );
        northLayout.setHgap( 10 );
        northLayout.setVgap( 10 );
        panel.setLayout( panelLayout );
        panelLayout.setHgap( 10 );
        panelLayout.setVgap( 10 );
        headerPanel.setLayout( headerLayout );
        headerLayout.setColumns( 1 );
        headerLayout.setRows( 4 );

        //

        infoArea.setReadWrite( false );
        this.getContentPane().add( panel,null );
        panel.add( northPanel,BorderLayout.NORTH );
        northPanel.add( imageControl,BorderLayout.WEST );
        northPanel.add( headerPanel,BorderLayout.CENTER );
        headerPanel.add( labelHeading,null );
        headerPanel.add( labelCopyright,null );
        headerPanel.add( labelVersion,null );
        headerPanel.add( labelDescription,null );
        panel.add( mainPanel,BorderLayout.CENTER );
        mainPanel.add( infoArea,BorderLayout.CENTER );
        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            dispose();
        }
    }    // actionPerformed
}    // AboutBox



/*
 *  @(#)AboutBox.java   02.07.07
 * 
 *  Fin del fichero AboutBox.java
 *  
 *  Versión 2.2
 *
 */