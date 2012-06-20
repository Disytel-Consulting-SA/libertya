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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextPane;
import org.openXpertya.model.MRole;
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

public final class ADialogDialog extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param message
     * @param messageType
     */

    public ADialogDialog( Frame frame,String title,String message,int messageType ) {
        super( frame,title,frame != null );
        common( message,messageType );
        AEnv.showCenterWindow( frame,this );
    }    // ADialogDialog

    /**
     * Constructor de la clase ...
     *
     *
     * @param dialog
     * @param title
     * @param message
     * @param messageType
     */

    public ADialogDialog( Dialog dialog,String title,String message,int messageType ) {
        super( dialog,title,dialog != null );
        common( message,messageType );
        AEnv.showCenterWindow( dialog,this );
    }    // ADialogDialog

    /**
     * Descripción de Método
     *
     *
     * @param message
     * @param messageType
     */

    private void common( String message,int messageType ) {

        // log.config( "ADialogDialog");

        CompiereColor.setBackground( this );

        try {
            setInfoMessage( message );
            jbInit();
            setInfoIcon( messageType );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"ADialogDialog.common - " + ex.getMessage());
        }

        // Default Button

        this.getRootPane().setDefaultButton( confirmPanel.getOKButton());
    }    // common

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );

        if( e.getID() == WindowEvent.WINDOW_OPENED ) {
            confirmPanel.getOKButton().requestFocusInWindow();
        }
    }    // processWindowEvent

    /** Descripción de Campos */

    public static int A_OK = 0;

    /** Descripción de Campos */

    public static int A_CANCEL = 1;

    /** Descripción de Campos */

    public static int A_CLOSE = -1;

    /** Descripción de Campos */

    private int m_returnCode = A_CLOSE;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ADialogDialog.class );

    /** Descripción de Campos */

    private static Icon i_inform = Env.getImageIcon( "Inform32.gif" );

    /** Descripción de Campos */

    private static Icon i_warn = Env.getImageIcon( "Warn32.gif" );

    /** Descripción de Campos */

    private static Icon i_question = Env.getImageIcon( "Question32.gif" );

    /** Descripción de Campos */

    private static Icon i_error = Env.getImageIcon( "Error32.gif" );

    /** Descripción de Campos */

    private JMenuBar menuBar = new JMenuBar();

    /** Descripción de Campos */

    private JMenu mFile = AEnv.getMenu( "File" );

    /** Descripción de Campos */

    private JMenuItem mEMail = new JMenuItem();

    /** Descripción de Campos */

    private JMenuItem mPrintScreen = new JMenuItem();

    /** Descripción de Campos */

    private JMenuItem mScreenShot = new JMenuItem();

    /** Descripción de Campos */

    private JMenuItem mEnd = new JMenuItem();

    /** Descripción de Campos */

    private JMenuItem mPreference = new JMenuItem();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private CPanel westPanel = new CPanel();

    /** Descripción de Campos */

    private CLabel iconLabel = new CLabel();

    /** Descripción de Campos */

    private GridBagLayout westLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CTextPane info = new CTextPane();

    /** Descripción de Campos */

    private GridBagLayout infoLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CPanel infoPanel = new CPanel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setJMenuBar( menuBar );

        //

        mEMail.setIcon( Env.getImageIcon( "EMailSupport16.gif" ));
        mEMail.setText( Msg.getMsg( Env.getCtx(),"EMailSupport" ));
        mEMail.addActionListener( this );
        mPrintScreen.setIcon( Env.getImageIcon( "PrintScreen16.gif" ));
        mPrintScreen.setText( Msg.getMsg( Env.getCtx(),"PrintScreen" ));
        mPrintScreen.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,0 ));
        mPrintScreen.addActionListener( this );
        mScreenShot.setIcon( Env.getImageIcon( "ScreenShot16.gif" ));
        mScreenShot.setText( Msg.getMsg( Env.getCtx(),"ScreenShot" ));
        mScreenShot.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,Event.SHIFT_MASK ));
        mScreenShot.addActionListener( this );
        mPreference.setIcon( Env.getImageIcon( "Preference16.gif" ));
        mPreference.setText( Msg.getMsg( Env.getCtx(),"Preference" ));
        mPreference.addActionListener( this );
        mEnd.setIcon( Env.getImageIcon( "End16.gif" ));
        mEnd.setText( Msg.getMsg( Env.getCtx(),"End" ));
        mEnd.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X,Event.ALT_MASK ));
        mEnd.addActionListener( this );

        //

        westPanel.setLayout( westLayout );
        westPanel.setName( "westPanel" );
        westPanel.setRequestFocusEnabled( false );
        infoPanel.setLayout( infoLayout );
        infoPanel.setName( "infoPanel" );
        infoPanel.setRequestFocusEnabled( false );
        this.getContentPane().add( confirmPanel,BorderLayout.SOUTH );
        this.getContentPane().add( westPanel,BorderLayout.WEST );
        westPanel.add( iconLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets( 10,10,10,10 ),0,0 ));
        this.getContentPane().add( infoPanel,BorderLayout.CENTER );
        infoPanel.add( info,new GridBagConstraints( 0,1,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets( 10,10,10,10 ),0,0 ));

        //

        menuBar.add( mFile );
        mFile.add( mPrintScreen );
        mFile.add( mScreenShot );
        mFile.addSeparator();
        mFile.add( mEMail );

        if( MRole.getDefault().isShowPreference()) {
            mFile.add( mPreference );
        }

        mFile.addSeparator();
        mFile.add( mEnd );

        //

        confirmPanel.addActionListener( this );
    }    // jbinit

    /**
     * Descripción de Método
     *
     *
     * @param message
     */

    private void setInfoMessage( String message ) {
        StringBuffer sb = new StringBuffer( message.length() + 20 );

        sb.append( "<b>" );

        char[]  chars = message.toCharArray();
        boolean first = true;
        int     paras = 0;

        for( int i = 0;i < chars.length;i++ ) {
            char c = chars[ i ];

            if( c == '\n' ) {
                if( first ) {
                    sb.append( "</b>" );
                    first = false;
                }

                if( paras > 1 ) {
                    sb.append( "<br>" );
                } else {
                    sb.append( "<p>" );
                }

                paras++;
            } else {
                sb.append( c );
            }
        }

        info.setText( sb.toString());

        Dimension size = info.getPreferredSize();

        size.width  = 450;
        size.height = ( Math.max( paras,message.length() / 60 ) + 1 ) * 30;
        size.height = Math.min( size.height,600 );
        info.setPreferredSize( size );

        // Log.print("Para=" + paras + " - " + info.getPreferredSize());

        info.setRequestFocusEnabled( false );
        info.setReadWrite( false );
        info.setOpaque( false );
        info.setBorder( null );

        //

        info.setCaretPosition( 0 );
    }    // calculateSize

    /**
     * Descripción de Método
     *
     *
     * @param messageType
     */

    private void setInfoIcon( int messageType ) {
        confirmPanel.getCancelButton().setVisible( false );

        //

        switch( messageType ) {
        case JOptionPane.ERROR_MESSAGE:
            iconLabel.setIcon( i_error );

            break;
        case JOptionPane.INFORMATION_MESSAGE:
            iconLabel.setIcon( i_inform );

            break;
        case JOptionPane.QUESTION_MESSAGE:
            confirmPanel.getCancelButton().setVisible( true );
            iconLabel.setIcon( i_question );

            break;
        case JOptionPane.WARNING_MESSAGE:
            iconLabel.setIcon( i_warn );

            break;
        case JOptionPane.PLAIN_MESSAGE:
        default:
            break;
        }    // switch
    }        // setInfo

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // log.finest( "ADialogDialog.actionPerformed - " + e);

        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            m_returnCode = A_OK;
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL ) || (e.getSource() == mEnd) ) {
            m_returnCode = A_CANCEL;
            dispose();
        } else if( e.getSource() == mPrintScreen ) {
            printScreen();
        } else if( e.getSource() == mEMail ) {
            String title = getTitle();
            String text  = info.getText();

            dispose();    // otherwise locking
            ADialog.createSupportEMail( this,title,text );
        } else if( e.getSource() == mPreference ) {
            if( MRole.getDefault().isShowPreference()) {
                Preference p = new Preference( null,0 );

                p.setVisible(true);
            }
        }
    }                     // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getReturnCode() {
        return m_returnCode;
    }    // getReturnCode

    /**
     * Descripción de Método
     *
     */

    private void printScreen() {
        PrintScreenPainter.printScreen( this );
    }    // printScreen
}    // ADialogDialog



/*
 *  @(#)ADialogDialog.java   02.07.07
 * 
 *  Fin del fichero ADialogDialog.java
 *  
 *  Versión 2.2
 *
 */
