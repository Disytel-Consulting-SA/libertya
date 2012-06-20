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



package org.openXpertya.apps.wf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AMenu;
import org.openXpertya.apps.AMenuStartItem;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.wf.MWFNode;
import org.openXpertya.wf.MWFNodeNext;
import org.openXpertya.wf.MWorkflow;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFPanel extends CPanel implements PropertyChangeListener,ActionListener,FormPanel {

    /**
     * Constructor de la clase ...
     *
     */

    public WFPanel() {
        this( null );
    }    // WFPanel

    /**
     * Constructor de la clase ...
     *
     *
     * @param menu
     */

    public WFPanel( AMenu menu ) {
        m_menu      = menu;
        m_readWrite = ( menu == null );
        log.info( "RW=" + m_readWrite );

        try {
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"WFPanel",e );
        }
    }    // WFPanel

    /** Descripción de Campos */

    private AMenu m_menu = null;

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private MWorkflow m_wf = null;

    /** Descripción de Campos */

    private Properties m_ctx = Env.getCtx();

    /** Descripción de Campos */

    private WFNode m_activeNode = null;

    /** Descripción de Campos */

    private boolean m_readWrite = false;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    // IO

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private WFContentPanel centerPanel = new WFContentPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private JScrollPane infoScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextPane infoTextPane = new JTextPane();

    /** Descripción de Campos */

    private CPanel buttonPanel = new CPanel();

    /** Descripción de Campos */

    private JButton wfStart = new JButton();

    /** Descripción de Campos */

    private JButton wfBack = new JButton();

    /** Descripción de Campos */

    private JButton wfNext = new JButton();

    /** Descripción de Campos */

    private JButton wfEnd = new JButton();

    //

    /** Descripción de Campos */

    private CPanel loadPanel = new CPanel( new FlowLayout( FlowLayout.LEADING ));

    /** Descripción de Campos */

    private CComboBox workflow = new CComboBox();

    /** Descripción de Campos */

    private CButton bResetLayout = AEnv.getButton( "Reset" );

    /** Descripción de Campos */

    private CButton bSaveLayout = AEnv.getButton( "Save" );

    /** Descripción de Campos */

    private CButton bZoom = AEnv.getButton( "Zoom" );

    /** Descripción de Campos */

    private CButton bIgnore = AEnv.getButton( "Ignore" );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLayout( mainLayout );
        CompiereColor.setBackground( this );
        southPanel.setLayout( southLayout );

        // Center

        this.add( new JScrollPane( centerPanel ),BorderLayout.CENTER );

        // Info

        infoScrollPane.getViewport().add( infoTextPane,null );
        infoScrollPane.setPreferredSize( new Dimension( 200,140 ));
        infoTextPane.setBackground( CompierePLAF.getFieldBackground_Inactive());
        infoTextPane.setEditable( false );
        infoTextPane.setRequestFocusEnabled( false );
        infoTextPane.setContentType( "text/html" );

        // South

        this.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( infoScrollPane,BorderLayout.CENTER );
        southPanel.add( buttonPanel,BorderLayout.SOUTH );

        //

        wfStart.setIcon( Env.getImageIcon( "wfStart24.gif" ));
        wfStart.setMargin( new Insets( 0,10,0,10 ));
        wfStart.setRequestFocusEnabled( false );
        wfStart.addActionListener( this );
        wfStart.setToolTipText( Msg.getMsg( m_ctx,"WFStart" ));
        wfBack.setIcon( Env.getImageIcon( "wfBack24.gif" ));
        wfBack.setMargin( new Insets( 0,15,0,15 ));
        wfBack.setRequestFocusEnabled( false );
        wfBack.addActionListener( this );
        wfBack.setToolTipText( Msg.getMsg( m_ctx,"WFPrevious" ));
        wfNext.setIcon( Env.getImageIcon( "wfNext24.gif" ));
        wfNext.setMargin( new Insets( 0,15,0,15 ));
        wfNext.setRequestFocusEnabled( false );
        wfNext.addActionListener( this );
        wfNext.setToolTipText( Msg.getMsg( m_ctx,"WFNext" ));
        wfEnd.setIcon( Env.getImageIcon( "wfEnd24.gif" ));
        wfEnd.setMargin( new Insets( 0,10,0,10 ));
        wfEnd.setRequestFocusEnabled( false );
        wfEnd.addActionListener( this );
        wfEnd.setToolTipText( Msg.getMsg( m_ctx,"WFExit" ));
        buttonPanel.add( wfStart,null );
        buttonPanel.add( wfBack,null );
        buttonPanel.add( wfNext,null );
        buttonPanel.add( wfEnd,null );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        m_WindowNo = WindowNo;
        m_frame    = frame;

        //

        log.fine( "init" );

        try {
            loadPanel();
            frame.getContentPane().add( loadPanel,BorderLayout.NORTH );
            this.setPreferredSize( new Dimension( 500,500 ));
            frame.getContentPane().add( this,BorderLayout.CENTER );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"init",e );
        }
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     */

    private void loadPanel() {
    	//JOptionPane.showMessageDialog( null,"En WFPAnel MRole.getDefault() =:="+ MRole.getDefault()+" \n Mrole.getAD_User_ID"+MRole.getAD_User_ID(), "-...Fin", JOptionPane.INFORMATION_MESSAGE );
        String sql = "SELECT wrk.AD_Workflow_ID, trl.Name FROM AD_Workflow wrk, ad_workflow_trl trl WHERE wrk.AD_Workflow_ID=trl.AD_Workflow_ID ORDER BY 2"; 
        //JOptionPane.showMessageDialog( null,"En WFPAnel con sql =:="+ sql , "-...Fin", JOptionPane.INFORMATION_MESSAGE );
        KeyNamePair[] pp = DB.getKeyNamePairs( sql,true );

        //

        workflow = new CComboBox( pp );
        loadPanel.add( workflow );
        workflow.addActionListener( this );

        //

        loadPanel.add( bIgnore );
        bIgnore.addActionListener( this );
        loadPanel.add( bResetLayout );
        bResetLayout.addActionListener( this );
        loadPanel.add( bSaveLayout );
        bSaveLayout.addActionListener( this );
        loadPanel.add( bZoom );
        bZoom.addActionListener( this );
    }    // loadPanel

    /**
     * Descripción de Método
     *
     *
     * @param readWrite
     */

    private void load( boolean readWrite ) {
        KeyNamePair pp = ( KeyNamePair )workflow.getSelectedItem();

        if( pp == null ) {
            return;
        }

        load( pp.getKey(),readWrite );
    }    // load

    /**
     * Descripción de Método
     *
     *
     * @param AD_Workflow_ID
     * @param readWrite
     */

    public void load( int AD_Workflow_ID,boolean readWrite ) {
        log.fine( "RW=" + readWrite + " - AD_Workflow_ID=" + AD_Workflow_ID );

        if( AD_Workflow_ID == 0 ) {
            return;
        }

        m_wf = new MWorkflow( Env.getCtx(),AD_Workflow_ID,null );
        centerPanel.removeAll();

        // Add Nodes for Paint

        MWFNode[] nodes = m_wf.getNodes( true );

        for( int i = 0;i < nodes.length;i++ ) {
            WFNode wfn = new WFNode( nodes[ i ] );

            wfn.addPropertyChangeListener( WFNode.PROPERTY_SELECTED,this );
            centerPanel.add( wfn,readWrite );

            // Add Lines

            MWFNodeNext[] nexts = nodes[ i ].getTransitions();

            for( int j = 0;j < nexts.length;j++ ) {
                centerPanel.add( new WFLine( nexts[ j ] ),false );
            }
        }

        // Info Text

        StringBuffer msg = new StringBuffer( "<HTML>" );

        msg.append( "<H2>" ).append( m_wf.getName( true )).append( "</H2>" );

        String s = m_wf.getDescription( true );

        if( (s != null) && (s.length() > 0) ) {
            msg.append( "<B>" ).append( s ).append( "</B>" );
        }

        s = m_wf.getHelp( true );

        if( (s != null) && (s.length() > 0) ) {
            msg.append( "<BR>" ).append( s );
        }

        msg.append( "</HTML>" );
        infoTextPane.setText( msg.toString());
        infoTextPane.setCaretPosition( 0 );

        // Layout

        centerPanel.validate();
        centerPanel.repaint();
        validate();
    }    // load

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void propertyChange( PropertyChangeEvent e ) {
        if( e.getNewValue() == Boolean.TRUE ) {
            start(( WFNode )e.getSource());
        }
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( (m_wf == null) && (e.getSource() != workflow) ) {
            return;
        }

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        // Editing

        if( e.getSource() == bZoom ) {
            zoom();
        } else if( e.getSource() == bIgnore ) {
            load( m_wf.getAD_Workflow_ID(),true );
        } else if( e.getSource() == workflow ) {
            load( true );
        } else if( e.getSource() == bSaveLayout ) {
            m_wf.save();

            MWFNode[] nodes = m_wf.getNodes( false );

            for( int i = 0;i < nodes.length;i++ ) {
                nodes[ i ].save();
            }
        } else if( e.getSource() == bResetLayout ) {
            resetLayout();

            // Buttons

        } else if( (e.getSource() == wfStart) || (m_activeNode == null) ) {
            start( m_wf.getAD_WF_Node_ID());
        } else if( e.getSource() == wfBack ) {
            start( m_wf.getPrevious( m_activeNode.getAD_WF_Node_ID()));
        } else if( e.getSource() == wfNext ) {
            start( m_wf.getNext( m_activeNode.getAD_WF_Node_ID()));
        } else if( e.getSource() == wfEnd ) {
            start( m_wf.getLast( m_activeNode.getAD_WF_Node_ID()));
        }

        //

        setCursor( Cursor.getDefaultCursor());
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param node
     */

    public void start( WFNode node ) {
        log.fine( "Node=" + node );

        MWFNode model = node.getModel();

        // Info Text

        StringBuffer msg = new StringBuffer( "<HTML>" );

        msg.append( "<H2>" ).append( model.getName( true )).append( "</H2>" );

        String s = model.getDescription( true );

        if( (s != null) && (s.length() > 0) ) {
            msg.append( "<B>" ).append( s ).append( "</B>" );
        }

        s = model.getHelp( true );

        if( (s != null) && (s.length() > 0) ) {
            msg.append( "<BR>" ).append( s );
        }

        msg.append( "</HTML>" );
        infoTextPane.setText( msg.toString());
        infoTextPane.setCaretPosition( 0 );

        // Load Window

        if( m_menu != null ) {
            ( new AMenuStartItem( model.getAD_WF_Node_ID(),false,model.getName( true ),m_menu )).start();    // async load
        }

        //

        m_activeNode = node;

        //

        boolean first = m_wf.isFirst( m_activeNode.getAD_WF_Node_ID());
        boolean last  = m_wf.isLast( m_activeNode.getAD_WF_Node_ID());

        wfStart.setEnabled( !first );
        wfBack.setEnabled( !first );
        wfNext.setEnabled( !last );
        wfEnd.setEnabled( !last );
    }    // start

    /**
     * Descripción de Método
     *
     *
     * @param AD_WF_Node_ID
     */

    public void start( int AD_WF_Node_ID ) {
        if( AD_WF_Node_ID == 0 ) {
            return;
        }

        //

        for( int i = 0;i < centerPanel.getComponentCount();i++ ) {
            Component comp = centerPanel.getComponent( i );

            if( comp instanceof WFNode ) {
                WFNode node = ( WFNode )comp;

                if( node.getAD_WF_Node_ID() == AD_WF_Node_ID ) {
                    start( node );

                    return;
                }
            }    // WFNode
        }        // for all components
    }            // start

    /**
     * Descripción de Método
     *
     */

    public void resetLayout() {
        Point p0 = new Point( 0,0 );

        for( int i = 0;i < centerPanel.getComponentCount();i++ ) {
            Component comp = centerPanel.getComponent( i );

            comp.setLocation( p0 );
        }

        centerPanel.doLayout();
    }    // resetLayout

    /**
     * Descripción de Método
     *
     */

    private void zoom() {
        int    AD_Window_ID = 113;
        MQuery query        = null;

        if( m_wf != null ) {
            query = MQuery.getEqualQuery( "AD_Workflow_ID",m_wf.getAD_Workflow_ID());
        }

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,query )) {
            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // zoom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "WFPanel[" );

        if( m_wf != null ) {
            sb.append( m_wf.getAD_Workflow_ID());
        }

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        org.openXpertya.OpenXpertya.startupEnvironment( true );

        JFrame  jf = new JFrame( "WF" );
        WFPanel pp = new WFPanel( null );

        pp.load( 101,true );
        jf.getContentPane().add( pp );
        jf.pack();
        jf.setVisible(true);
    }    // main
}    // WFPanel



/*
 *  @(#)WFPanel.java   02.07.07
 * 
 *  Fin del fichero WFPanel.java
 *  
 *  Versión 2.2
 *
 */
