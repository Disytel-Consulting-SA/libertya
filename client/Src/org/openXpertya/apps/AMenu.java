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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.FocusManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openXpertya.apps.WindowManager;
import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTabbedPane;
import org.openXpertya.OpenXpertya;
import org.openXpertya.apps.wf.WFActivity;
import org.openXpertya.apps.wf.WFPanel;
import org.openXpertya.db.CConnection;
import org.openXpertya.grid.tree.VTreePanel;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MSession;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.utils.Disposable;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.utils.LYCloseWindowAdapter;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Splash;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class AMenu extends JFrame implements ActionListener,PropertyChangeListener,ChangeListener, Disposable {

    /**
     * Constructor de la clase ...
     *
     */

    public AMenu() {
        super();

        Splash splash = Splash.getSplash();

        //

        m_WindowNo = Env.createWindowNo( this );

        
        // Login

        initSystem( splash );                      // login
        splash.setText( Msg.getMsg( m_ctx,"Loading" ));
        OpenXpertya.startupEnvironment( true );    // Load Environment
        MSession.get( Env.getCtx(),true );         // Start Session

        // Preparation

        setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        addWindowListener(new LYCloseWindowAdapter(this, true));
        wfActivity = new WFActivity( this );
        wfPanel    = new WFPanel( this );
        treePanel  = new VTreePanel( m_WindowNo,true,false );    // !editable & hasBar

        try {
            jbInit();
            createMenu();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"AMenu",ex );
        }

        // initialize & load tree

        int AD_Role_ID = Env.getAD_Role_ID( Env.getCtx());
        int AD_Tree_ID = DB.getSQLValue( null,"SELECT COALESCE(r.AD_Tree_Menu_ID, ci.AD_Tree_Menu_ID)" + "FROM AD_ClientInfo ci" + " INNER JOIN AD_Role r ON (ci.AD_Client_ID=r.AD_Client_ID) " + "WHERE AD_Role_ID=?",AD_Role_ID );

        if( AD_Tree_ID <= 0 ) {
            AD_Tree_ID = 10;    // Menu
        }

        treePanel.initTree( AD_Tree_ID );

        // Translate

        Env.setContext( m_ctx,m_WindowNo,"WindowName",Msg.getMsg( m_ctx,"Menu" ));
        setTitle( Env.getHeader( m_ctx,m_WindowNo ));
        progressBar.setString( Msg.getMsg( m_ctx,"SelectProgram" ));

        // Finish UI

        Point loc = Ini.getWindowLocation( 0 );

        if( loc == null ) {
            loc = new Point( 0,0 );
        }

        this.setLocation( loc );
        this.pack();
        this.setVisible( true );
        this.setState( Frame.NORMAL );
        m_AD_User_ID = Env.getContextAsInt( m_ctx,"#AD_User_ID" );
        m_AD_Role_ID = Env.getContextAsInt( m_ctx,"#AD_Role_ID" );
        updateInfo();

        //

        splash.dispose();
        splash = null;
    }    // AMenu

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private Properties m_ctx = Env.getCtx();

    /** Descripción de Campos */

    private boolean m_startingItem = false;

    /** Descripción de Campos */

    private int m_AD_User_ID;

    /** Descripción de Campos */

    private int m_AD_Role_ID;

    // Links

    /** Descripción de Campos */

    private int m_request_Menu_ID = 0;

    /** Descripción de Campos */

    private int m_note_Menu_ID = 0;

    /** Descripción de Campos */

    private String m_requestSQL = null;

    /** Descripción de Campos */

    private DecimalFormat m_memoryFormat = DisplayType.getNumberFormat( DisplayType.Integer );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AMenu.class );

    
    private WindowManager windowManager = new WindowManager();
    
    /**
     * Descripción de Método
     *
     *
     * @param splash
     */

    private void initSystem( Splash splash ) {

        // Default Image

        this.setIconImage( OpenXpertya.getImage16());

        // Focus Traversal

        FocusManager.getCurrentManager().setDefaultFocusTraversalPolicy( AFocusTraversalPolicy.get());
        this.setFocusTraversalPolicy( AFocusTraversalPolicy.get());

        ALogin login = new ALogin( splash );

        if( !login.initLogin())                    // no automatic login
        {

            // Center the window

            try {
                AEnv.showCenterScreen( login );    // HTML load errors
            } catch( Exception ex ) {
            }

            if( !login.isConnected() ||!login.isOKpressed()) {
                AEnv.exit( 1 );
            }
        }

        checkExpirationUserPassword();
        
        // Check DB    (AppsServer Version checked in Login)

        boolean dbOK = DB.isDatabaseOK( m_ctx );

        // if (!dbOK)
        // AEnv.exit(1);

    }    // initSystem

    
    private void checkExpirationUserPassword(){
    	int    AD_Client_ID = Env.getAD_Client_ID( m_ctx );
        int    AD_Org_ID    = Env.getAD_Org_ID(m_ctx);
        int    AD_Role_ID   = Env.getAD_Role_ID( m_ctx );
        int    AD_User_ID   = Env.getAD_User_ID( m_ctx );
    	MClient client = MClient.get(m_ctx, AD_Client_ID);
    	UserValidation userValidation = new UserValidation();
    	userValidation.initialize(client);
    	
    	CallResult result = userValidation.login(AD_Org_ID, AD_Role_ID, AD_User_ID);

        if(result != null && result.isError()) {
        	if(result.isShowError()){
        		ADialog.info( m_WindowNo,this,result.getMsg() );
        	}
        	AEnv.exit( 1 );
        }
    }
    
    // UI

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CTabbedPane centerPane = new CTabbedPane();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private JMenuBar menuBar = new JMenuBar();

    /** Descripción de Campos */

    protected JProgressBar progressBar = new JProgressBar( 0,100 );

    /** Descripción de Campos */

    private CPanel infoPanel = new CPanel();

    /** Descripción de Campos */

    private CButton bNotes = new CButton();

    /** Descripción de Campos */

    private CButton bTasks = new CButton();

    /** Descripción de Campos */

    private GridLayout infoLayout = new GridLayout();

    /** Descripción de Campos */

    private JProgressBar memoryBar = new JProgressBar();

    //

    /** Descripción de Campos */

    private VTreePanel treePanel = null;

    /** Descripción de Campos */

    private WFActivity wfActivity = null;

    /** Descripción de Campos */

    private WFPanel wfPanel = null;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        this.setName( "Menu" );
        this.setLocale( Language.getLoginLanguage().getLocale());
        this.setJMenuBar( menuBar );
        CompiereColor.setBackground( this );

        //

        mainPanel.setLayout( mainLayout );
        mainLayout.setHgap( 0 );
        mainLayout.setVgap( 2 );

        //

        treePanel.addPropertyChangeListener( VTreePanel.NODE_SELECTION,this );

        //

        infoPanel.setLayout( infoLayout );
        infoLayout.setColumns( 2 );
        infoLayout.setHgap( 4 );
        infoLayout.setVgap( 0 );
        bNotes.setRequestFocusEnabled( false );
        bNotes.setToolTipText( "" );
        bNotes.setActionCommand( "Notes" );
        bNotes.addActionListener( this );
        bNotes.setIcon( Env.getImageIcon( "GetMail24.gif" ));
        bNotes.setMargin( new Insets( 0,0,0,0 ));
        bTasks.setRequestFocusEnabled( false );
        bTasks.setActionCommand( "Tasks" );
        bTasks.addActionListener( this );
        bTasks.setIcon( Env.getImageIcon( "Request24.gif" ));
        bTasks.setMargin( new Insets( 0,0,0,0 ));

        //

        southLayout.setHgap( 0 );
        southLayout.setVgap( 1 );

        //

        memoryBar.setStringPainted( true );
        memoryBar.setOpaque( false );
        memoryBar.setBorderPainted( false );
        memoryBar.addMouseListener( new AMenu_MouseAdapter());

        //

        progressBar.setStringPainted( true );
        progressBar.setOpaque( false );

        //

        getContentPane().add( mainPanel );
        mainPanel.add( centerPane,BorderLayout.CENTER );
        mainPanel.add( southPanel,BorderLayout.SOUTH );
        mainPanel.add( Box.createHorizontalStrut( 3 ),BorderLayout.EAST );
        mainPanel.add( Box.createHorizontalStrut( 3 ),BorderLayout.WEST );

        //

        centerPane.add( treePanel,Msg.getMsg( m_ctx,"Menu" ));
        centerPane.add( new CScrollPane( wfActivity ),Msg.translate( m_ctx,"AD_WF_Activity_ID" ) + ": 0" );
        centerPane.add( new CScrollPane( wfPanel ),Msg.translate( m_ctx,"AD_Workflow_ID" ));
        centerPane.addChangeListener( this );

        //

        southPanel.setLayout( southLayout );
        southPanel.add( infoPanel,BorderLayout.NORTH );
        southPanel.add( progressBar,BorderLayout.SOUTH );

        //

        infoPanel.add( bNotes,null );
        infoPanel.add( bTasks,null );
        infoPanel.add( memoryBar,null );

        //

        int loc = Ini.getDividerLocation();

        if( loc > 0 ) {
            treePanel.setDividerLocation( loc );
        }
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Dimension getPreferredSize() {
        Dimension dim = Ini.getWindowDimension( 0 );

        if( dim == null ) {
            dim = new Dimension( 350,500 );
        }

        return dim;
    }    // getPreferredSize

    /**
     * Descripción de Método
     *
     */

    private void createMenu() {

    	MRole currentUserRole = MRole.getDefault(Env.getCtx(), true);
    	
        // File

        JMenu mFile = AEnv.getMenu( "File" );

        menuBar.add( mFile );
        AEnv.addMenuItem( "PrintScreen",null,KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,0 ),mFile,this );
        AEnv.addMenuItem( "ScreenShot",null,KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,KeyEvent.SHIFT_MASK ),mFile,this );

        // AEnv.addMenuItem("Report", null, KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.ALT_MASK), mFile, this);

        // dREHER, opcion para desloguearse sin salir del aplicativo
        AEnv.addMenuItem("Logout", null, KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.SHIFT_MASK+Event.ALT_MASK), mFile, this);
        
        mFile.addSeparator();
        AEnv.addMenuItem( "Exit",null,KeyStroke.getKeyStroke( KeyEvent.VK_X,Event.SHIFT_MASK + Event.ALT_MASK ),mFile,this );

        // View

        JMenu mView = AEnv.getMenu( "View" );

        menuBar.add( mView );
        if (currentUserRole.isInfoProductAccess())
        	AEnv.addMenuItem( "InfoProduct",null,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.CTRL_MASK ),mView,this );
        if (currentUserRole.isInfoBPartnerAccess())
        	AEnv.addMenuItem( "InfoBPartner",null,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.SHIFT_MASK + Event.CTRL_MASK ),mView,this );
        if (currentUserRole.isShowAcct())
            AEnv.addMenuItem( "InfoAccount",null,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.ALT_MASK + Event.CTRL_MASK ),mView,this );
        if (currentUserRole.isInfoScheduleAccess())
        	AEnv.addMenuItem( "InfoSchedule",null,null,mView,this );
        mView.addSeparator();
        if (currentUserRole.isInfoOrderAccess())
        	AEnv.addMenuItem( "InfoOrder","Info",null,mView,this );
        if (currentUserRole.isInfoInvoiceAccess())
        	AEnv.addMenuItem( "InfoInvoice","Info",null,mView,this );
        if (currentUserRole.isInfoInOutAccess())
        	AEnv.addMenuItem( "InfoInOut","Info",null,mView,this );
        if (currentUserRole.isInfoPaymentAccess())
        	AEnv.addMenuItem( "InfoPayment","Info",null,mView,this );
        if (currentUserRole.isInfoCashLineAccess())
        	AEnv.addMenuItem( "InfoCashLine","Info",null,mView,this );
        if (currentUserRole.isInfoAssignmentAccess())
        	AEnv.addMenuItem( "InfoAssignment","Info",null,mView,this );
        if (currentUserRole.isInfoAssetAccess())
        	AEnv.addMenuItem( "InfoAsset","Info",null,mView,this );

        // Tools

        JMenu mTools = AEnv.getMenu( "Tools" );

        menuBar.add( mTools );
        AEnv.addMenuItem( "Calculator",null,null,mTools,this );
        AEnv.addMenuItem( "Calendar",null,null,mTools,this );
     //   AEnv.addMenuItem( "Editor",null,null,mTools,this );
     //   AEnv.addMenuItem( "Script",null,null,mTools,this );

        if( AEnv.isWorkflowProcess()) {
            AEnv.addMenuItem( "WorkFlow",null,null,mTools,this );
        }

        if( currentUserRole.isShowPreference()) {
            mTools.addSeparator();
            AEnv.addMenuItem( "Preference",null,null,mTools,this );
        }

        // Help

        JMenu mHelp = AEnv.getMenu( "Help" );

        menuBar.add( mHelp );
        AEnv.addMenuItem( "Online",null,null,mHelp,this );
        AEnv.addMenuItem( "EMailSupport",null,null,mHelp,this );
        AEnv.addMenuItem( "About",null,null,mHelp,this );
    }    // createMenu

    /**
     * Descripción de Método
     *
     */

    public void dispose() {

        // clean up - close windows

        Ini.setWindowDimension( 0,getSize());
        Ini.setDividerLocation( treePanel.getDividerLocation());
        Ini.setWindowLocation( 0,getLocation());
        Ini.saveProperties( true );
        super.dispose();
        AEnv.exit( 0 );
    }    // dispose
    
    /******
     * dREHER 
     * 
     * permite salir del rol actual sin desloguearse
     *  
     * ***************/
    private void preDispose() {
		//	clean up - save window state
		Ini.setWindowDimension(0, getSize());
		Ini.setDividerLocation(treePanel.getDividerLocation());
		Ini.setWindowLocation(0, getLocation());
		Ini.saveProperties(true);
		//
		/*
		infoUpdater.stop = true;
		try {
			infoUpdaterThread.join(50);
		} catch(InterruptedException ire) {	
		} finally {
			infoUpdaterThread = null;
			infoUpdater = null;
		}*/
	}
	
    // dREHER
	@SuppressWarnings("unchecked")
	public void logout()
	{
		// windowManager.close(); // Adempiere tiene la clase windowManager para administrar las ventanas
		// Cerrar todas las ventanas abiertas
		try{
			
			int windows = Env.getS_windows().size();
			for ( int i=1; i<windows; i++) {
//				System.out.println("window=" + i);
				JFrame w = Env.getWindow(i);
				if(w != null)
					w.dispose();
			}
		}catch(Exception ex){}
		preDispose();
		super.dispose();
		AEnv.logout();
	}
	/*************** permite salir del rol actual sin desloguearse ***************/


    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );

        if( e.getID() == WindowEvent.WINDOW_OPENED ) {
            treePanel.getSearchField().requestFocusInWindow();

            // this.toFront();

        }
    }    // processWindowEvent

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    protected void setBusy( boolean value ) {
        m_startingItem = value;

        if( value ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        } else {
            setCursor( Cursor.getDefaultCursor());
        }

        // setEnabled (!value);        //  causes flicker

    }    // setBusy

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void propertyChange( PropertyChangeEvent e ) {
        MTreeNode nd = ( MTreeNode )e.getNewValue();

        log.info( nd.getNode_ID() + " - " + nd.toString());

        // ignore summary items & when loading

        if( m_startingItem || nd.isSummary()) {
            return;
        }

        String sta = nd.toString();

        progressBar.setString( sta );

        int cmd = nd.getNode_ID();

        ( new AMenuStartItem( cmd,true,sta,this )).start();    // async load
        updateInfo();
    }                                                          // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // Buttons
    	
        if( e.getSource() == bNotes ) {
            gotoNotes();
        } else if( e.getSource() == bTasks ) {
            gotoTasks();
        } else if( !AEnv.actionPerformed( e.getActionCommand(),m_WindowNo,this )) {
            log.log( Level.SEVERE,"unknown action=" + e.getActionCommand());
        }

        updateInfo();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getNotes() {
        int    retValue = 0;
        String sql      = "SELECT COUNT(*) FROM AD_Note " + "WHERE AD_Client_ID=? AND AD_User_ID IN (0,?)" + " AND Processed='N'";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,Env.getAD_Client_ID( Env.getCtx()));
            pstmt.setInt( 2,m_AD_User_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getNotes",e );
        }

        return retValue;
    }    // getNotes

    /**
     * Descripción de Método
     *
     */

    private void gotoNotes() {

        // AD_Table_ID for AD_Note = 389           HARDCODED

        if( m_note_Menu_ID == 0 ) {
            m_note_Menu_ID = DB.getSQLValue( null,"SELECT AD_Menu_ID " + "FROM AD_Menu m" + " INNER JOIN AD_TABLE t ON (t.AD_Window_ID=m.AD_Window_ID) " + "WHERE t.AD_Table_ID=?",389 );
        }

        if( m_note_Menu_ID == 0 ) {
            m_note_Menu_ID = 233;    // fallback HARDCODED
        }

        ( new AMenuStartItem( m_note_Menu_ID,true,Msg.translate( m_ctx,"AD_Note_ID" ),this )).start();    // async load
    }    // gotoMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getRequests() {
        int retValue = 0;

        if( m_requestSQL == null ) {
            m_requestSQL = MRole.getDefault().addAccessSQL( "SELECT COUNT(*) FROM R_Request " + "WHERE (SalesRep_ID=? OR AD_Role_ID=?) AND Processed='N'" + " AND (DateNextAction IS NULL OR TRUNC(DateNextAction) <= TRUNC(SysDate))","R_Request",false,true );    // not qualified - RW
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( m_requestSQL );

            pstmt.setInt( 1,m_AD_User_ID );
            pstmt.setInt( 2,m_AD_Role_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"getRequests",e );
        }

        return retValue;
    }    // getRequests

    /**
     * Descripción de Método
     *
     */

    private void gotoTasks() {

        // AD_Table_ID for R_Request = 417         HARDCODED

        if( m_request_Menu_ID == 0 ) {
            m_request_Menu_ID = DB.getSQLValue( null,"SELECT AD_Menu_ID " + "FROM AD_Menu m" + " INNER JOIN AD_TABLE t ON (t.AD_Window_ID=m.AD_Window_ID) " + "WHERE t.AD_Table_ID=?",417 );
        }

        if( m_request_Menu_ID == 0 ) {
            m_request_Menu_ID = 237;    // fallback HARDCODED
        }

        ( new AMenuStartItem( m_request_Menu_ID,true,Msg.translate( m_ctx,"R_Request_ID" ),this )).start();    // async load
    }    // gotoTasks

    /**
     * Descripción de Método
     *
     */

    public void updateInfo() {
        double total   = Runtime.getRuntime().totalMemory() / 1024;
        double free    = Runtime.getRuntime().freeMemory() / 1024;
        double used    = total - free;
        double percent = used * 100 / total;

        //

        memoryBar.setMaximum(( int )total );
        memoryBar.setValue(( int )used );

        String msg = MessageFormat.format( "{0,number,integer} Mb - {1,number,integer}%",new Object[]{ new BigDecimal( total / 1024 ),new BigDecimal( percent )} );

        memoryBar.setString( msg );

        //
        // msg = MessageFormat.format("Total Memory {0,number,integer} kB - Free {1,number,integer} kB",

        msg = Msg.getMsg( m_ctx,"MemoryInfo",new Object[]{ new BigDecimal( total ),new BigDecimal( free )} );
        memoryBar.setToolTipText( msg );

        // progressBar.repaint();

        //

        if( percent > 50 ) {
            System.gc();
        }

        // Requests

        int requests = getRequests();

        bTasks.setText( Msg.translate( m_ctx,"R_Request_ID" ) + ": " + requests );

        // Memo

        int notes = getNotes();

        bNotes.setText( Msg.translate( m_ctx,"AD_Note_ID" ) + ": " + notes );

        // Activities

        int activities = wfActivity.loadActivities();

        centerPane.setTitleAt( 1,Msg.translate( m_ctx,"AD_WF_Activity_ID" ) + ": " + activities );

        //

        log.config( msg + ", Processors=" + Runtime.getRuntime().availableProcessors() + ", Requests=" + requests + ", Notes=" + notes + ", Activities=" + activities + "," + CConnection.get().getStatus());
    }    // updateInfo

    /**
     * Descripción de Método
     *
     *
     * @param AD_Workflow_ID
     */

    protected void startWorkFlow( int AD_Workflow_ID ) {
        centerPane.setSelectedIndex( 2 );    // switch
        wfPanel.load( AD_Workflow_ID,false );
    }                                        // startWorkFlow

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {

        // show activities

        if( centerPane.getSelectedIndex() == 1 ) {
            wfActivity.display();
        }
    }    // stateChanged

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class AMenu_MouseAdapter extends MouseAdapter {

        /**
         * Descripción de Método
         *
         *
         * @param e
         */

        public void mouseClicked( MouseEvent e ) {
            if( e.getClickCount() > 1 ) {
                System.gc();
                updateInfo();
            }
        }
    }    // AMenu_MouseAdapter


    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        Splash splash = Splash.getSplash();

        OpenXpertya.startup( true );    // needs to be here for UI

        AMenu menu = new AMenu();
    }    // main


	@Override
	public int getWindowNo() {
		return m_WindowNo;
	}


	@Override
	public Container getContainerForMsg() {
		return this;
	}
	
	public WindowManager getWindowManager() {
		return windowManager;
	}

}    // AMenu



/*
 *  @(#)AMenu.java   02.07.07
 * 
 *  Fin del fichero AMenu.java
 *  
 *  Versión 2.2
 *
 */