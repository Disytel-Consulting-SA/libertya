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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.compiere.plaf.CompierePLAF;
import org.openXpertya.model.MWindow;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.WebDoc;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Help extends JDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param mWindow
     */

    public Help( Frame frame,String title,MWindow mWindow ) {
        super( frame,title,false );

        try {
            jbInit();
            loadInfo( mWindow );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Help",ex );
        }

        AEnv.positionCenterWindow( frame,this );
    }    // Help

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param url
     */

    public Help( Frame frame,String title,URL url ) {
        super( frame,title,false );

        try {
            jbInit();
            info.setPage( url );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Help",ex );
        }

        AEnv.positionCenterWindow( frame,this );
    }    // Help

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param helpHtml
     */

    public Help( Frame frame,String title,String helpHtml ) {
        super( frame,title,false );

        try {
            jbInit();
            info.setContentType( "text/html" );
            info.setEditable( false );
            info.setBackground( CompierePLAF.getFieldBackground_Inactive());
            info.setText( helpHtml );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Help",ex );
        }

        AEnv.positionCenterWindow( frame,this );
    }    // Help

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Help.class );

    /** Descripción de Campos */

    private JPanel mainPanel = new JPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private OnlineHelp info = new OnlineHelp();

    /** Descripción de Campos */

    private JScrollPane infoPane = new JScrollPane();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        mainPanel.setLayout( mainLayout );
        mainLayout.setHgap( 2 );
        mainLayout.setVgap( 2 );
        infoPane.setBorder( BorderFactory.createLoweredBevelBorder());
        infoPane.setPreferredSize( new Dimension( 500,400 ));
        getContentPane().add( mainPanel );
        mainPanel.add( infoPane,BorderLayout.CENTER );
        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        infoPane.getViewport().add( info,null );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param mWindow
     */

    private void loadInfo( MWindow mWindow ) {
        WebDoc doc = mWindow.getHelpDoc( true );

        info.setText( doc.toString());
    }    // loadInfo

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
}    // Help



/*
 *  @(#)Help.java   02.07.07
 * 
 *  Fin del fichero Help.java
 *  
 *  Versión 2.2
 *
 */
