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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import org.compiere.swing.CDialog;
import org.openXpertya.OpenXpertya;
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

public class HTMLEditor extends CDialog {

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param title
     * @param htmlText
     * @param editable
     */

    public HTMLEditor( Frame owner,String title,String htmlText,boolean editable ) {
        super( owner,(title == null)
                     ?Msg.getMsg( Env.getCtx(),"Editor" )
                     :title,true );
        init( owner,htmlText,editable );
    }    // HTMLEditor

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param title
     * @param htmlText
     * @param editable
     */

    public HTMLEditor( Dialog owner,String title,String htmlText,boolean editable ) {
        super( owner,(title == null)
                     ?Msg.getMsg( Env.getCtx(),"Editor" )
                     :title,true );
        init( owner,htmlText,editable );
    }    // HTMLEditor

    /**
     * Descripción de Método
     *
     *
     * @param owner
     * @param htmlText
     * @param editable
     */

    private void init( Window owner,String htmlText,boolean editable ) {
        try {
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"HTMLEditor",e );
        }

        setHtmlText( htmlText );
        editorPane.setEditable( editable );
        AEnv.showCenterWindow( owner,this );
    }    // init

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private String m_text;

    //

    /** Descripción de Campos */

    private JPanel mainPanel = new JPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    //

    /** Descripción de Campos */

    private JScrollPane editorScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextPane editorPane = new JTextPane();

    //

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    // Tool Bar

    /** Descripción de Campos */

    private JToolBar toolBar = new JToolBar();

    /** Descripción de Campos */

    private JButton bImport = AEnv.getButton( "Import" );

    /** Descripción de Campos */

    private JButton bExport = AEnv.getButton( "Export" );

    /** Descripción de Campos */

    private JButton bBold = AEnv.getButton( "Bold" );

    /** Descripción de Campos */

    private JButton bItalic = AEnv.getButton( "Italic" );

    /** Descripción de Campos */

    private JButton bUnderline = AEnv.getButton( "Underline" );

    // Menu Bar

    /** Descripción de Campos */

    private JMenuBar menuBar = new JMenuBar();

    /** Descripción de Campos */

    private static String NAME_SIZE = Msg.getMsg( Env.getCtx(),"Size" );

    /** Descripción de Campos */

    private static String NAME_HEADING = Msg.getMsg( Env.getCtx(),"Heading" );

    // Font Size Sub-Menu

    /** Descripción de Campos */

    private static HTMLEditor_MenuAction[] sizeMenu = new HTMLEditor_MenuAction[] {
        new HTMLEditor_MenuAction( NAME_SIZE + "  8","font-size-8" ),new HTMLEditor_MenuAction( NAME_SIZE + " 10","font-size-10" ),new HTMLEditor_MenuAction( NAME_SIZE + " 12","font-size-12" ),new HTMLEditor_MenuAction( NAME_SIZE + " 14","font-size-14" ),new HTMLEditor_MenuAction( NAME_SIZE + " 16","font-size-16" ),new HTMLEditor_MenuAction( NAME_SIZE + " 18","font-size-18" ),new HTMLEditor_MenuAction( NAME_SIZE + " 24","font-size-24" ),new HTMLEditor_MenuAction( NAME_SIZE + " 36","font-size-36" ),new HTMLEditor_MenuAction( NAME_SIZE + " 48","font-size-48" )
    };

    // Font Family Sub-Menu

    /** Descripción de Campos */

    private static HTMLEditor_MenuAction[] familyMenu = new HTMLEditor_MenuAction[]{ new HTMLEditor_MenuAction( "Sans Serif","font-family-SansSerif" ),new HTMLEditor_MenuAction( "Monospaced","font-family-Monospaced" ),new HTMLEditor_MenuAction( "Serif","font-family-Serif" )};

    // Font Size Sub-Menu

    /** Descripción de Campos */

    private static HTMLEditor_MenuAction[] styleMenu = new HTMLEditor_MenuAction[] {
        new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"Bold" ),"font-bold" ),new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"Italic" ),"font-italic" ),new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"Underline" ),"font-underline" )

        // default-typed?

    };

    // Heading Sub-Menu

    /** Descripción de Campos */

    private static HTMLEditor_MenuAction[] headingMenu = new HTMLEditor_MenuAction[]{ new HTMLEditor_MenuAction( NAME_HEADING + " 1","Heading 1" ),new HTMLEditor_MenuAction( NAME_HEADING + " 2","Heading 2" ),new HTMLEditor_MenuAction( NAME_HEADING + " 3","Heading 3" ),new HTMLEditor_MenuAction( NAME_HEADING + " 4","Heading 4" ),new HTMLEditor_MenuAction( NAME_HEADING + " 5","Heading 5" )};

    // Font Menu

    /** Descripción de Campos */

    private static HTMLEditor_MenuAction[] fontMenu = new HTMLEditor_MenuAction[]{ new HTMLEditor_MenuAction( NAME_SIZE,sizeMenu ),new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"FontFamily" ),familyMenu ),new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"FontStyle" ),styleMenu ),new HTMLEditor_MenuAction( NAME_HEADING,headingMenu )};

    // Alignment Menu

    /** Descripción de Campos */

    private static HTMLEditor_MenuAction[] alignMenu = new HTMLEditor_MenuAction[]{ new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"Left" ),"left-justify" ),new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"Center" ),"center-justify" ),new HTMLEditor_MenuAction( Msg.getMsg( Env.getCtx(),"Right" ),"right-justify" )};

    // Other HTML Menu

    /** Descripción de Campos */

    private static HTMLEditor_MenuAction[] htmlMenu = new HTMLEditor_MenuAction[] {
        new HTMLEditor_MenuAction( "Paragraph","Paragraph" ),new HTMLEditor_MenuAction( "Table","InsertTable" ),new HTMLEditor_MenuAction( "Table Row","InsertTableRow" ),new HTMLEditor_MenuAction( "Table Cell","InsertTableDataCell" ),new HTMLEditor_MenuAction( "Unordered List","InsertUnorderedList" ),new HTMLEditor_MenuAction( "Unordered List Item","InsertUnorderedListItem" ),new HTMLEditor_MenuAction( "Ordered List","InsertOrderedList" ),new HTMLEditor_MenuAction( "Ordered List Item","InsertOrderedListItem" ),new HTMLEditor_MenuAction( "Preformatted Paragraph","InsertPre" ),new HTMLEditor_MenuAction( "Horizontal Rule","InsertHR" )
    };

    // Insert HTML Actions

    /** Descripción de Campos */

    private static HTMLEditorKit.InsertHTMLTextAction[] extraActions = new HTMLEditorKit.InsertHTMLTextAction[] {
        new HTMLEditorKit.InsertHTMLTextAction( "Heading 1","<h1>h1</h1>",HTML.Tag.BODY,HTML.Tag.H1 ),new HTMLEditorKit.InsertHTMLTextAction( "Heading 2","<h2>h2</h2>",HTML.Tag.BODY,HTML.Tag.H2 ),new HTMLEditorKit.InsertHTMLTextAction( "Heading 3","<h2>h3</h2>",HTML.Tag.BODY,HTML.Tag.H3 ),new HTMLEditorKit.InsertHTMLTextAction( "Heading 4","<h2>h4</h2>",HTML.Tag.BODY,HTML.Tag.H4 ),new HTMLEditorKit.InsertHTMLTextAction( "Heading 5","<h2>h5</h2>",HTML.Tag.BODY,HTML.Tag.H5 ),

        //

        new HTMLEditorKit.InsertHTMLTextAction( "Paragraph","<p>p</p>",HTML.Tag.BODY,HTML.Tag.P )
    };

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {

        // ToolBar

        bImport.setToolTipText( Msg.getMsg( Env.getCtx(),"Import" ));
        bImport.addActionListener( this );
        bExport.setToolTipText( Msg.getMsg( Env.getCtx(),"Export" ));
        bExport.addActionListener( this );

        //

        bBold.setToolTipText( Msg.getMsg( Env.getCtx(),"Bold" ));
        bItalic.setToolTipText( Msg.getMsg( Env.getCtx(),"Italic" ));
        bUnderline.setToolTipText( Msg.getMsg( Env.getCtx(),"Underline" ));
        toolBar.add( bImport,null );
        toolBar.add( bExport,null );
        toolBar.addSeparator();
        toolBar.add( bBold,null );
        toolBar.add( bItalic,null );
        toolBar.add( bUnderline,null );
        toolBar.addSeparator();

        // Editor

        editorPane.setContentType( "text/html" );

        // Set Menu (content type must be set)

        setJMenuBar( menuBar );
        createMenuBar();

        // General Layout

        mainPanel.setLayout( mainLayout );
        getContentPane().add( mainPanel,BorderLayout.CENTER );
        mainPanel.add( toolBar,BorderLayout.NORTH );
        mainPanel.add( editorScrollPane,BorderLayout.CENTER );

        // Size 600x600

        editorScrollPane.setPreferredSize( new Dimension( 600,600 ));
        editorScrollPane.getViewport().add( editorPane,null );
        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
    }    // setHTMLText

    /**
     * Descripción de Método
     *
     */

    private void createMenuBar() {

        // Build Lookup

        Action[]  actionArray = editorPane.getActions();
        Hashtable actions     = new Hashtable();

        for( int i = 0;i < actionArray.length;i++ ) {
            Object name = actionArray[ i ].getValue( Action.NAME );

            // System.out.println (name);

            actions.put( name,actionArray[ i ] );
        }

        for( int i = 0;i < extraActions.length;i++ ) {
            Object name = extraActions[ i ].getValue( Action.NAME );

            actions.put( name,extraActions[ i ] );
        }

        // Add the font menu

        JMenu menu = buildMenu( Msg.getMsg( Env.getCtx(),"Font" ),fontMenu,actions );

        if( menu != null ) {
            menuBar.add( menu );
        }

        // Add the alignment menu

        menu = buildMenu( Msg.getMsg( Env.getCtx(),"Align" ),alignMenu,actions );

        if( menu != null ) {
            menuBar.add( menu );
        }

        // Add the HTML menu

        menu = buildMenu( "HTML",htmlMenu,actions );

        if( menu != null ) {
            menuBar.add( menu );
        }

        // Add to Button Actions

        Action targetAction = ( Action )actions.get( "font-bold" );

        bBold.addActionListener( targetAction );
        targetAction = ( Action )actions.get( "font-italic" );
        bItalic.addActionListener( targetAction );
        targetAction = ( Action )actions.get( "font-underline" );
        bUnderline.addActionListener( targetAction );
    }    // createMenuBar

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param menuActions
     * @param actions
     *
     * @return
     */

    private JMenu buildMenu( String name,HTMLEditor_MenuAction[] menuActions,Hashtable actions ) {
        JMenu menu = new JMenu( name );

        for( int i = 0;i < menuActions.length;i++ ) {
            HTMLEditor_MenuAction item = menuActions[ i ];

            if( item.isSubMenu())    // Recurse to handle a sub menu
            {
                JMenu subMenu = buildMenu( item.getName(),item.getSubMenus(),actions );

                if( subMenu != null ) {
                    menu.add( subMenu );
                }
            } else if( item.isAction())    // direct action
            {
                menu.add( item.getAction());
            } else                         // find it
            {
                String actionName   = item.getActionName();
                Action targetAction = ( Action )actions.get( actionName );

                // Create the menu item

                JMenuItem menuItem = menu.add( item.getName());

                if( targetAction != null ) {
                    menuItem.addActionListener( targetAction );
                } else {    // Action not known - disable the menu item
                    menuItem.setEnabled( false );
                }
            }
        }                   // for all actions

        // Return null if nothing was added to the menu.

        if( menu.getMenuComponentCount() == 0 ) {
            menu = null;
        }

        return menu;
    }    // buildMenu

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // log.fine("actionPerformed - Text:" + getHtmlText());
        //

        if( e.getSource() == bImport ) {
            cmd_import();
        } else if( e.getSource() == bExport ) {
            cmd_export();

            //

        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            m_text = editorPane.getText();
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void cmd_import() {
        JFileChooser jc = new JFileChooser();

        jc.setDialogTitle( Msg.getMsg( Env.getCtx(),"Import" ));
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
            log.log( Level.SEVERE,"HTMLEditor.import" + e.getMessage());

            return;
        }

        setHtmlText( sb.toString());
    }    // cmd_import

    /**
     * Descripción de Método
     *
     */

    private void cmd_export() {
        JFileChooser jc = new JFileChooser();

        jc.setDialogTitle( Msg.getMsg( Env.getCtx(),"Export" ));
        jc.setDialogType( JFileChooser.SAVE_DIALOG );
        jc.setFileSelectionMode( JFileChooser.FILES_ONLY );

        //

        if( jc.showSaveDialog( this ) != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        try {
            EditorKit          kit    = editorPane.getEditorKit();
            OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( jc.getSelectedFile()));

            editorPane.write( writer );
            writer.flush();
            writer.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"HTMLEditor.export" + e.getMessage());
        }
    }    // cmd_export

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHtmlText() {
        return m_text;
    }    // getHTMLText

    /**
     * Descripción de Método
     *
     *
     * @param htmlText
     */

    public void setHtmlText( String htmlText ) {
        m_text = htmlText;
        editorPane.setText( htmlText );
    }    // setHTMLText

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        OpenXpertya.startupEnvironment( true );

        JFrame frame = new JFrame( "test" );

        frame.setVisible(true);

        String text = "<html><p>this is a line<br>with <b>bold</> info</html>";
        int    i    = 0;

        while( true ) {
            HTMLEditor ed = new HTMLEditor( frame,"heading " + ++i,text,true );

            text = ed.getHtmlText();
        }
    }    // main
}    // HTMLEditor


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class HTMLEditor_MenuAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param subMenus
     */

    public HTMLEditor_MenuAction( String name,HTMLEditor_MenuAction[] subMenus ) {
        m_name     = name;
        m_subMenus = subMenus;
    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param actionName
     */

    public HTMLEditor_MenuAction( String name,String actionName ) {
        m_name       = name;
        m_actionName = actionName;
    }

    /**
     * Constructor de la clase ...
     *
     *
     * @param name
     * @param action
     */

    public HTMLEditor_MenuAction( String name,Action action ) {
        m_name   = name;
        m_action = action;
    }

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private String m_actionName;

    /** Descripción de Campos */

    private Action m_action;

    /** Descripción de Campos */

    private HTMLEditor_MenuAction[] m_subMenus;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSubMenu() {
        return m_subMenus != null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isAction() {
        return m_action != null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public HTMLEditor_MenuAction[] getSubMenus() {
        return m_subMenus;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getActionName() {
        return m_actionName;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Action getAction() {
        return m_action;
    }
}    // MenuAction



/*
 *  @(#)HTMLEditor.java   02.07.07
 * 
 *  Fin del fichero HTMLEditor.java
 *  
 *  Versión 2.2
 *
 */
