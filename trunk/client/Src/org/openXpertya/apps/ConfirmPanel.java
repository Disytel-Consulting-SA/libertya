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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.compiere.swing.CButton;
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

public final class ConfirmPanel extends CPanel {

	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_RIGHT = 2;
	
    /**
     * Constructor de la clase ...
     *
     */

    public ConfirmPanel() {
        this( false,false,false,false,false,false,true );
    }    // ConfirmPanel

    /**
     * Constructor de la clase ...
     *
     *
     * @param withCancelButton
     */

    public ConfirmPanel( boolean withCancelButton ) {
        this( withCancelButton,false,false,false,false,false,true );
    }    // ConfirmPanel

    /**
     * Constructor
     * @param withCancelButton
     * @param withRefreshButton
     * @param withResetButton
     * @param withCustomizeButton
     * @param withHistoryButton
     * @param withZoomButton
     * @param withText
     * @param align
     */
    public ConfirmPanel( boolean withCancelButton,boolean withRefreshButton,boolean withResetButton,boolean withCustomizeButton,boolean withHistoryButton,boolean withZoomButton,boolean withText, int align) {
        super();

        BorderLayout mainLayout = new BorderLayout();

        this.setLayout( mainLayout );
        this.setName( "confirmPanel" );

        //

        CPanel okCancel = new CPanel( new FlowLayout( FlowLayout.RIGHT ));

        okCancel.setOpaque( false );
        bCancel = createCancelButton( withText );
        bOK = createOKButton( withText );
        setCancelVisible( withCancelButton );
        this.add( okCancel,BorderLayout.EAST );
 
        if (align == ALIGN_LEFT) {
            okCancel.add( bCancel );
            okCancel.add( bOK );
        } else {
        	m_addlButtons = okCancel;
        }
        //

        if( withRefreshButton ) {
            bRefresh = createRefreshButton( withText );
            addComponent( bRefresh );
        }

        if( withResetButton ) {
            bReset = createResetButton( withText );
            addComponent( bReset );
        }

        if( withCustomizeButton ) {
            bCustomize = createCustomizeButton( withText );
            addComponent( bCustomize );
        }

        if( withHistoryButton ) {
            bHistory = createHistoryButton( withText );
            addComponent( bHistory );
        }

        if( withZoomButton ) {
            bZoom = createZoomButton( withText );
            addComponent( bZoom );
        }
        
        if (align == ALIGN_RIGHT) {
            okCancel.add( bCancel );
            okCancel.add( bOK );
        }
        
    }    // ConfirmPanel
    
    public ConfirmPanel( boolean withCancelButton,boolean withRefreshButton,boolean withResetButton,boolean withCustomizeButton,boolean withHistoryButton,boolean withZoomButton,boolean withText) {
    	this(withCancelButton, withRefreshButton, withResetButton, withCustomizeButton, withHistoryButton, withZoomButton, withText, ALIGN_LEFT);
    }
    

    /**
     * Constructor
     * @param withCancelButton
     * @param withRefreshButton
     * @param withResetButton
     * @param withCustomizeButton
     * @param withHistoryButton
     * @param withZoomButton
     * @param withNewButton
     * @param withText
     * @param align
     */
    public ConfirmPanel( boolean withCancelButton,boolean withRefreshButton,boolean withResetButton,boolean withCustomizeButton,boolean withHistoryButton,boolean withZoomButton, boolean withNewButton, boolean withText, int align ) {
    	this(withCancelButton, withRefreshButton, withResetButton, withCustomizeButton, withHistoryButton, withZoomButton, withText, align);
    	
        if(withNewButton){
        	log.fine("Entro en ConfirmPanel en el newbutton");
        	bNew = createNewButton( withText);
        	addComponent(bNew);
        }
    }    // ConfirmPanel


    /**
     * Constructor
     * @param withCancelButton
     * @param withRefreshButton
     * @param withResetButton
     * @param withCustomizeButton
     * @param withHistoryButton
     * @param withZoomButton
     * @param withNewButton
     * @param withText
     */
    public ConfirmPanel( boolean withCancelButton,boolean withRefreshButton,boolean withResetButton,boolean withCustomizeButton,boolean withHistoryButton,boolean withZoomButton, boolean withNewButton, boolean withText ) {
    	this(withCancelButton, withRefreshButton, withResetButton, withCustomizeButton, withHistoryButton, withZoomButton, withNewButton, withText, ALIGN_LEFT);
    }

    /** Descripción de Campos */

    private CPanel m_addlButtons = null;

    /** Descripción de Campos */

    private DialogButton bOK;

    /** Descripción de Campos */

    private DialogButton bCancel;

    //

    /** Descripción de Campos */

    private DialogButton bRefresh;

    /** Descripción de Campos */

    private DialogButton bReset;

    /** Descripción de Campos */

    private DialogButton bCustomize;

    /** Descripción de Campos */

    private DialogButton bHistory;

    /** Descripción de Campos */

    private DialogButton bZoom;
    
    private DialogButton bNew;
    private static CLogger log = CLogger.getCLogger( ConfirmPanel.class );
    /**
     * Descripción de Método
     *
     *
     * @param button
     */

    public void addComponent( Component button ) {
        if( m_addlButtons == null ) {
            m_addlButtons = new CPanel( new FlowLayout( FlowLayout.LEFT ));
            this.add( m_addlButtons, BorderLayout.WEST);
        }

        m_addlButtons.add( button );
    }    // addButton

    /**
     * Descripción de Método
     *
     *
     * @param action
     * @param toolTipText
     * @param icon
     * @param mnemonic
     *
     * @return
     */

    public DialogButton addButton( String action,String toolTipText,Icon icon,int mnemonic ) {
        DialogButton b = new DialogButton( action,toolTipText,icon,mnemonic );

        addComponent( b );

        return b;
    }    // addButton

    /**
     * Descripción de Método
     *
     *
     * @param button
     *
     * @return
     */

    public JButton addButton( JButton button ) {
        addComponent( button );

        return button;
    }    // addButton

    /** Descripción de Campos */

    public static final String A_OK = "OK";

    /** Descripción de Campos */

    public static final String A_CANCEL = "Cancel";

    /** Descripción de Campos */

    public static final String A_REFRESH = "Refresh";

    /** Descripción de Campos */

    public static final String A_RESET = "Reset";

    /** Descripción de Campos */

    public static final String A_CUSTOMIZE = "Customize";

    /** Descripción de Campos */

    public static final String A_HISTORY = "History";

    /** Descripción de Campos */

    public static final String A_ZOOM = "Zoom";

    /** Descripción de Campos */

    public static final String A_PROCESS = "Process";

    /** Descripción de Campos */

    public static final String A_PRINT = "Print";

    /** Descripción de Campos */

    public static final String A_EXPORT = "Export";

    /** Descripción de Campos */

    public static final String A_HELP = "Help";

    /** Descripción de Campos */

    public static final String A_DELETE = "Delete";

    /** Descripción de Campos */

    public static final String A_PATTRIBUTE = "PAttribute";

    /** Descripción de Campos */

    public static final String A_NEW = "New";

    /** Descripción de Campos */

    public static final String A_COPY = "Copy"; /* Maurix */

    /** Descripción de Campos */

    
    public static Insets s_insets = new Insets( 0,10,0,10 );

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createOKButton( String text ) {
        DialogButton okButton = new DialogButton( A_OK,text,Env.getImageIcon( "Ok24.gif" ),KeyEvent.VK_O );

        okButton.setDefaultCapable( true );
        
        okButton.getInputMap( WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F10,0 ),"pressed" );
        okButton.getInputMap( WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F10,0,true ),"released" );

        return okButton;
    }    // createOKButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createOKButton( boolean withText ) {
        if( withText ) {
            return createOKButton( Msg.getMsg( Env.getCtx(),A_OK ));
        }

        return createOKButton( null );
    }    // createOKButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public DialogButton getOKButton() {
        return bOK;
    }    // getOKButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createCancelButton( String text ) {
        DialogButton cancel = new DialogButton( A_CANCEL,text,Env.getImageIcon( "Cancel24.gif" ),KeyEvent.VK_X );

        // ESC = Ignore

        cancel.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE,0 ),"pressed" );
        cancel.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE,0, true ),"released" );
//        cancel.getActionMap().put( A_CANCEL,cancel.getAction());

        return cancel;
    }    // createCancelButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createCancelButton( boolean withText ) {
        if( withText ) {
            return createCancelButton( Msg.getMsg( Env.getCtx(),A_CANCEL ));
        }

        return createCancelButton( null );
    }    // createCancelButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public DialogButton getCancelButton() {
        return bCancel;
    }    // getCancelButton

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setOKVisible( boolean value ) {
        bOK.setVisible( value );
        bOK.setEnabled( value );
    }    // setOKVisible

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOKVisible() {
        return bOK.isVisible();
    }    // isOKVisible

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setCancelVisible( boolean value ) {
        bCancel.setVisible( value );
        bCancel.setEnabled( value );
    }    // setCancelVisible

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCancelVisible() {
        return bCancel.isVisible();
    }    // isCancelVisible

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createRefreshButton( String text ) {
        DialogButton refresh = new DialogButton( A_REFRESH,text,Env.getImageIcon( "Refresh24.gif" ),0 );

        // F5 = Refresh

        refresh.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F5,0 ),"pressed" );
        refresh.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F5,0, true ),"released" );
//        refresh.getActionMap().put( A_REFRESH,refresh.getAction());

        return refresh;
    }    // createRefreshButton

   /*Maurix*/
    
    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */
    
    public static final DialogButton createCopiarRegistroButton( String text ) {
        return new DialogButton( A_COPY,text,Env.getImageIcon("Copy24.gif"),0 );
    }    // createCopiarRegistroButton

    
    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createRefreshButton( boolean withText ) {
        if( withText ) {
            return createRefreshButton( Msg.getMsg( Env.getCtx(),A_REFRESH ));
        }

        return createRefreshButton( null );
    }    // createRefreshButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public DialogButton getRefreshButton() {
        return bRefresh;
    }    // getRefreshButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createResetButton( String text ) {
        return new DialogButton( A_RESET,text,Env.getImageIcon( "Reset24.gif" ),0 );
    }    // createResetButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createResetButton( boolean withText ) {
        if( withText ) {
            return createResetButton( Msg.getMsg( Env.getCtx(),A_RESET ));
        }

        return createResetButton( null );
    }    // createRefreshButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public DialogButton getResetButton() {
        return bReset;
    }    // getResetButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createCustomizeButton( String text ) {
        return new DialogButton( A_CUSTOMIZE,text,Env.getImageIcon( "Preference24.gif" ),0 );
    }    // createCustomizeButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createCustomizeButton( boolean withText ) {
        if( withText ) {
            return createCustomizeButton( Msg.getMsg( Env.getCtx(),A_CUSTOMIZE ));
        }

        return createCustomizeButton( null );
    }    // createCustomizeButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public DialogButton getCustomizeButton() {
        return bCustomize;
    }    // getCustomizeButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createHistoryButton( String text ) {
        DialogButton history = new DialogButton( A_HISTORY,text,Env.getImageIcon( "HistoryX24.gif" ),0 );

        // F9 = History

        history.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F9,0 ),"pressed" );
        history.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F9,0,true ),"released" );
//        history.getActionMap().put( A_HISTORY,history.getAction());

        return history;
    }    // createHistoryButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createHistoryButton( boolean withText ) {
        if( withText ) {
            return createHistoryButton( Msg.getMsg( Env.getCtx(),A_HISTORY ));
        }

        return createHistoryButton( null );
    }    // createHistoryButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public DialogButton getHistoryButton() {
        return bHistory;
    }    // getHistoryButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createZoomButton( String text ) {
        return new DialogButton( A_ZOOM,text,Env.getImageIcon( "Zoom24.gif" ),KeyEvent.VK_Z );
    }    // createZoomButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createZoomButton( boolean withText ) {
        if( withText ) {
            return createZoomButton( Msg.getMsg( Env.getCtx(),A_ZOOM ));
        }

        return createZoomButton( null );
    }    // createZoomButton

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public DialogButton getZoomButton() {
        return bZoom;
    }    // getZoomyButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createProcessButton( String text ) {
        return new DialogButton( A_PROCESS,text,Env.getImageIcon( "Process24.gif" ),0 );
    }    // createProcessButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createProcessButton( boolean withText ) {
        if( withText ) {
            return createProcessButton( Msg.getMsg( Env.getCtx(),A_PROCESS ));
        }

        return createProcessButton( null );
    }    // createProcessButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createPrintButton( String text ) {
        return new DialogButton( A_PRINT,text,Env.getImageIcon( "Print24.gif" ),KeyEvent.VK_P );
    }    // createPrintButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createPrintButton( boolean withText ) {
        if( withText ) {
            return createPrintButton( Msg.getMsg( Env.getCtx(),A_PRINT ));
        }

        return createPrintButton( null );
    }    // createPrintButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createHelpButton( String text ) {
        DialogButton help = new DialogButton( A_HELP,text,Env.getImageIcon( "Help24.gif" ),KeyEvent.VK_H );

        // F1 = Help

        help.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F1,0 ),"pressed" );
        help.getInputMap( CButton.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F1,0,true ),"released" );
//        help.getActionMap().put( A_HELP,help.getAction());

        return help;
    }    // createHelpButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createHelpButton( boolean withText ) {
        if( withText ) {
            return createHelpButton( Msg.getMsg( Env.getCtx(),A_HELP ));
        }

        return createHelpButton( null );
    }    // createHelpButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createExportButton( String text ) {
        return new DialogButton( A_EXPORT,text,Env.getImageIcon( "Export24.gif" ),0 );
    }    // createExportButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createExportButton( boolean withText ) {
        if( withText ) {
            return createExportButton( Msg.getMsg( Env.getCtx(),A_EXPORT ));
        }

        return createExportButton( null );
    }    // createExportButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createDeleteButton( String text ) {
        DialogButton dButton = new DialogButton( A_DELETE,text,Env.getImageIcon( "Delete24.gif" ),KeyEvent.VK_O );

        return dButton;
    }    // createDeleteButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createDeleteButton( boolean withText ) {
        if( withText ) {
            return createDeleteButton( Msg.getMsg( Env.getCtx(),A_DELETE ));
        }

        return createDeleteButton( null );
    }    // createDeleteButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createPAttributeButton( boolean withText ) {
        if( withText ) {
            return createPAttributeButton( Msg.getMsg( Env.getCtx(),A_PATTRIBUTE ));
        }

        return createPAttributeButton( null );
    }    // createPAttributeButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createPAttributeButton( String text ) {
        DialogButton button = new DialogButton( A_PATTRIBUTE,text,Env.getImageIcon( "PAttribute24.gif" ),0 );

        return button;
    }    // createPAttributeButton

    /**
     * Descripción de Método
     *
     *
     * @param withText
     *
     * @return
     */

    public static final DialogButton createNewButton( boolean withText ) {
        if( withText ) {
            return createNewButton( Msg.getMsg( Env.getCtx(),A_NEW ));
        }

        return createNewButton( null );
    }    // createNewButton

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static final DialogButton createNewButton( String text ) {
        DialogButton button = new DialogButton( A_NEW,text,Env.getImageIcon( "New24.gif" ),0 );

        return button;
    }    // createNewButton

    /**
     * Descripción de Método
     *
     *
     * @param al
     */

    public void addActionListener( ActionListener al ) {
        bOK.addActionListener( al );
        bCancel.addActionListener( al );

        //

        if( bRefresh != null ) {
            bRefresh.addActionListener( al );
        }

        if( bReset != null ) {
            bReset.addActionListener( al );
        }

        if( bCustomize != null ) {
            bCustomize.addActionListener( al );
        }

        if( bHistory != null ) {
            bHistory.addActionListener( al );
        }

        if( bZoom != null ) {
            bZoom.addActionListener( al );
        }

        if( bNew !=null){
        	log.fine("Llego al addactionlistener de confirmpanel");
        	bNew.addActionListener( al );
        }
        // Set OK as default Button

        JRootPane rootpane = null;

        if( al instanceof JDialog ) {
            rootpane = (( JDialog )al ).getRootPane();
        } else if( al instanceof JFrame ) {
            rootpane = (( JFrame )al ).getRootPane();
        }

        if( rootpane != null ) {
            rootpane.setDefaultButton( bOK );

            // Log.print("DefaultButton set");

        }
    }    // addActionListener

    /**
     * Descripción de Método
     *
     *
     * @param enabled
     */

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        bOK.setEnabled( enabled );
        bCancel.setEnabled( enabled );

        //

        if( bRefresh != null ) {
            bRefresh.setEnabled( enabled );
        }

        if( bCustomize != null ) {
            bCustomize.setEnabled( enabled );
        }

        if( bHistory != null ) {
            bHistory.setEnabled( enabled );
        }

        if( bZoom != null ) {
            bZoom.setEnabled( enabled );
        }
        
        if( bNew != null ) {
            bNew.setEnabled( enabled );
        }
    }    // setEnabled

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    public static class DialogButton extends CButton {

        /**
         * Constructor de la clase ...
         *
         *
         * @param action
         * @param toolTipText
         * @param icon
         * @param mnemonic
         */

        public DialogButton( String action,String toolTipText,Icon icon,int mnemonic ) {
            super( new DialogAction( action,toolTipText,icon,mnemonic ));
            super.setMargin( s_insets );
            super.setDefaultCapable( false );
        }    // DialogButton

        /**
         * Descripción de Método
         *
         *
         * @param al
         */

        public void addActionListener( ActionListener al ) {

            // if delegate is set and action listener is set, target.actionPerformed is
            // called twice.  If only delegate is set, nothing happens.
            // It seems that the button is not 'registered' with the Action.
            // if (getAction() != null)
            // ((DialogAction)getAction()).setDelegate(al);

            super.addActionListener( al );
        }    // addActionListener
    }    // DialogButton


    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    public static class DialogAction extends AbstractAction {

        /**
         * Constructor de la clase ...
         *
         *
         * @param action
         * @param toolTipText
         * @param icon
         * @param mnemonic
         */

        public DialogAction( String action,String toolTipText,Icon icon,int mnemonic ) {
            super();

            // Attributes
            // putValue(Action.NAME, text);                                                //      Display

            putValue( Action.SMALL_ICON,icon );
            putValue( Action.SHORT_DESCRIPTION,toolTipText );    // Tooltip
            putValue( Action.ACTION_COMMAND_KEY,action );

            if( mnemonic != 0 ) {
                putValue( Action.MNEMONIC_KEY,new Integer( mnemonic ));
            }
        }    // DialogAction

        /** Descripción de Campos */

        private ActionListener m_delegate = null;

        /**
         * Descripción de Método
         *
         *
         * @param al
         */

        public void setDelegate( ActionListener al ) {
            m_delegate = al;
        }    // setDelegate

        /**
         * Descripción de Método
         *
         *
         * @param e
         */

        public void actionPerformed( ActionEvent e ) {

            // System.out.println(">>> Action: actionPerformed");

            if( m_delegate != null ) {
                m_delegate.actionPerformed( e );
            }
        }    // actionPerformed
    }    // DialogAction
}        // ConfirmPanel



/*
 *  @(#)ConfirmPanel.java   02.07.07
 * 
 *  Fin del fichero ConfirmPanel.java
 *  
 *  Versión 2.2
 *
 */
