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



package org.openXpertya.grid.ed;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextArea;
import org.compiere.swing.CTextPane;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
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

public class Editor extends CDialog implements ChangeListener,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     */

    public Editor( Frame frame ) {
        this( frame,Msg.getMsg( Env.getCtx(),"Editor" ),"",true );
    }    // Editor

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param header
     * @param text
     * @param editable
     */

    public Editor( Frame frame,String header,String text,boolean editable ) {
        super( frame,header,frame != null );
        log.config( "Editor" );

        try {
            jbInit();
            setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Editor",ex );
        }

        // Set Text

        m_text = text;
        textArea.setText( m_text );
        textArea.setEditable( editable );

        if( editable ) {
            textArea.setBackground( CompierePLAF.getFieldBackground_Normal());
        } else {
            textArea.setBackground( CompierePLAF.getFieldBackground_Inactive());
        }

        textPane.setBackground( CompierePLAF.getFieldBackground_Inactive());
    }    // Editor

    /**
     * Constructor de la clase ...
     *
     */

    public Editor() {
        this( null );
    }    // Editor

    /** Descripción de Campos */

    private String m_text;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Editor.class );

    /** Descripción de Campos */

    private CPanel panel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout panelLayout = new BorderLayout();

    /** Descripción de Campos */

    private JTabbedPane tabbedPane = new JTabbedPane();

    /** Descripción de Campos */

    private CTextArea textArea = new CTextArea();

    /** Descripción de Campos */

    private CTextPane textPane = new CTextPane();

    /** Descripción de Campos */

    private JMenuBar menuBar = new JMenuBar();

    /** Descripción de Campos */

    private JMenu mFile = new JMenu();

    /** Descripción de Campos */

    private JMenuItem mImport = new JMenuItem();

    /** Descripción de Campos */

    private JMenuItem mExport = new JMenuItem();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        panel.setLayout( panelLayout );
        this.setJMenuBar( menuBar );

        // Text Tab

        textArea.setPreferredSize( new Dimension( 300,300 ));
        textArea.setWrapStyleWord( true );
        textArea.setLineWrap( true );
        tabbedPane.add( textArea,"Text" );

        // HTML Tab

        textPane.setContentType( "text/html" );
        textPane.setEditable( false );
        tabbedPane.add( textPane,"HTML" );

        //

        mFile.setText( "File" );
        mImport.setText( "Import" );
        mImport.addActionListener( this );
        mExport.setText( "Export" );
        mExport.addActionListener( this );
        tabbedPane.addChangeListener( this );
        getContentPane().add( panel );
        panel.add( tabbedPane,BorderLayout.CENTER );
        this.getContentPane().add( confirmPanel,BorderLayout.SOUTH );
        menuBar.add( mFile );
        mFile.add( mImport );
        mFile.add( mExport );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param jc
     * @param header
     * @param text
     * @param editable
     *
     * @return
     */

    public static String startEditor( Container jc,String header,String text,boolean editable ) {

        // Find frame

        JFrame frame = Env.getFrame( jc );
        String hdr   = header;

        if( (hdr == null) || (hdr.length() == 0) ) {
            hdr = Msg.getMsg( Env.getCtx(),"Editor" );
        }

        // Start it

        Editor ed = new Editor( frame,hdr,text,editable );

        AEnv.showCenterWindow( frame,ed );

        String s = ed.getText();

        ed = null;

        return s;
    }    // startEditor

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            m_text = textArea.getText();
            log.fine( "Editor.actionPerformed - OK - length=" + m_text.length());
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        } else if( e.getSource() == mImport ) {
            importText();
        } else if( e.getSource() == mExport ) {
            exportText();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getText() {
        return m_text;
    }    // getText

    /**
     * Descripción de Método
     *
     */

    private void importText() {
        JFileChooser jc = new JFileChooser();

        jc.setDialogTitle( Msg.getMsg( Env.getCtx(),"ImportText" ));
        jc.setDialogType( JFileChooser.OPEN_DIALOG );
        jc.setFileSelectionMode( JFileChooser.FILES_ONLY );

        //

        if( jc.showOpenDialog( this ) != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        StringBuffer sb = new StringBuffer();

        try {
            InputStreamReader in = new InputStreamReader( new FileInputStream( jc.getSelectedFile()));
            char[] cbuf = new char[ 1024 ];
            int    count;

            while(( count = in.read( cbuf )) > 0 ) {
                sb.append( cbuf,0,count );
            }

            in.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Editor.importText" + e.getMessage());

            return;
        }

        textArea.setText( sb.toString());
    }    // importText

    /**
     * Descripción de Método
     *
     */

    private void exportText() {
        JFileChooser jc = new JFileChooser();

        jc.setDialogTitle( Msg.getMsg( Env.getCtx(),"ExportText" ));
        jc.setDialogType( JFileChooser.SAVE_DIALOG );
        jc.setFileSelectionMode( JFileChooser.FILES_ONLY );

        //

        if( jc.showSaveDialog( this ) != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        try {
            BufferedWriter bout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( jc.getSelectedFile())));

            bout.write( textArea.getText());
            bout.flush();
            bout.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Editor.exportText" + e.getMessage());
        }
    }    // exportText

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        if( tabbedPane.getSelectedIndex() == 1 ) {    // switch to HTML
            textPane.setText( textArea.getText());
        }
    }                                                 // stateChanged
}    // Editor



/*
 *  @(#)Editor.java   02.07.07
 * 
 *  Fin del fichero Editor.java
 *  
 *  Versión 2.2
 *
 */
