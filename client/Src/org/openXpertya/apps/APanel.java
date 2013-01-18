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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.apps.form.VConciliacionExtractoImportado;
import org.openXpertya.apps.form.VInOutMatrixDetail;
import org.openXpertya.apps.form.VInvoiceRemGen;
import org.openXpertya.apps.form.VOrderMatrixDetail;
import org.openXpertya.apps.form.VPriceInstanceMatrix;
import org.openXpertya.apps.form.VProdPricGen;
import org.openXpertya.apps.form.VUpcInstanceMatrix;
import org.openXpertya.apps.search.Find;
import org.openXpertya.grid.APanelTab;
import org.openXpertya.grid.GridController;
import org.openXpertya.grid.RecordAccessDialog;
import org.openXpertya.grid.VCreateFrom;
import org.openXpertya.grid.VOnlyCurrentDays;
import org.openXpertya.grid.VPayment;
import org.openXpertya.grid.VSortTab;
import org.openXpertya.grid.VTabbedPane;
import org.openXpertya.grid.ed.VButton;
import org.openXpertya.grid.ed.VDocAction;
import org.openXpertya.model.DataStatusEvent;
import org.openXpertya.model.DataStatusListener;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MField;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MWindow;
import org.openXpertya.model.MWindowVO;
import org.openXpertya.model.MWorkbench;
import org.openXpertya.model.M_Window;
import org.openXpertya.model.X_C_OrderLine;
import org.openXpertya.model.X_M_InOutLine;
import org.openXpertya.print.AReport;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.openXpertya.utils.LYCloseWindowAdapter;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public final class APanel extends CPanel implements DataStatusListener,ChangeListener,ActionListener,ASyncProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public APanel() {
        super();
        m_ctx = Env.getCtx();

        //

        try {
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"APanel",e );
        }

        createMenu();
    }    // APanel

    /** Descripción de Campos */
    private static int MAX_RECORDS  =10000;
    
    private static CLogger log = CLogger.getCLogger( APanel.class );

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        log.info( "" );

        // ignore changes

        m_disposing = true;

        // close panels

        tabPanel.dispose( this );
        tabPanel = null;

        // All Workbenches

        for( int i = 0;i < m_mWorkbench.getWindowCount();i++ ) {
            m_curWindowNo = m_mWorkbench.getWindowNo( i );
            log.info( "#" + m_curWindowNo );
            Env.setAutoCommit( m_ctx,m_curWindowNo,false );
            m_mWorkbench.dispose( i );
            Env.clearWinContext( m_ctx,m_curWindowNo );
        }    // all Workbenchens

        // Get rid of remaining model

        if( m_mWorkbench != null ) {
            m_mWorkbench.dispose();
        }

        m_mWorkbench = null;

        // MenuBar

        if( menuBar != null ) {
            menuBar.removeAll();
        }

        menuBar = null;

        // ToolBar

        if( toolBar != null ) {
            toolBar.removeAll();
        }

        toolBar = null;

        // Prepare GC

        this.removeAll();
    }    // dispose

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private VTabbedPane tabPanel = new VTabbedPane( true );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private JToolBar toolBar = new JToolBar();

    /** Descripción de Campos */

    private JMenuBar menuBar = new JMenuBar();

    /** Descripción de Campos */

    private FlowLayout northLayout = new FlowLayout();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLocale( Language.getLoginLanguage().getLocale());
        this.setLayout( mainLayout );

        // tabPanel

        mainLayout.setHgap( 2 );
        mainLayout.setVgap( 2 );
        this.add( tabPanel,BorderLayout.CENTER );

        // southPanel

        this.add( statusBar,BorderLayout.SOUTH );

        // northPanel

        this.add( northPanel,BorderLayout.NORTH );
        northPanel.setLayout( northLayout );
        northLayout.setAlignment( FlowLayout.LEFT );
        northPanel.add( toolBar,null );
    }    // jbInit

    /** Descripción de Campos */

    private AppsAction aPrevious,aNext,aParent,aDetail,aFirst,aLast,aNew,aCopy,aDelete,aIgnore,aPrint,aRefresh,aHistory,aAttachment,aMulti,aFind,aWorkflow,aZoomAcross,aRequest,aWinSize,aArchive;

    /** Descripción de Campos */

    public AppsAction aSave,aLock;

    // Local (added to toolbar)

    /** Descripción de Campos */

    private AppsAction aReport,aEnd,aHome,aHelp,aProduct,aAccount,aCalculator,aCalendar,/*aEditor,*/aPreference,/*aScript,*/aOnline,aMailSupport,aAbout,aPrintScr,aScrShot,aExit,aBPartner;

    /**
     * Descripción de Método
     *
     */

    private void createMenu() {

    	MRole currentUserRole = MRole.getDefault(Env.getCtx(), true);
    	
        // menuBar.setHelpMenu();
        // File

        JMenu mFile = AEnv.getMenu( "File" );

        menuBar.add( mFile );
        aPrintScr = addAction( "PrintScreen",mFile,KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,0 ),false );
        aScrShot = addAction( "ScreenShot",mFile,KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,Event.SHIFT_MASK ),false );
        aReport = addAction( "Report",mFile,KeyStroke.getKeyStroke( KeyEvent.VK_P,Event.ALT_MASK ),false );
        aPrint = addAction( "Print",mFile,KeyStroke.getKeyStroke( KeyEvent.VK_P,Event.CTRL_MASK ),false );
        mFile.addSeparator();
        aEnd = addAction( "End",mFile,KeyStroke.getKeyStroke( KeyEvent.VK_X,Event.ALT_MASK ),false );
        aExit = addAction( "Exit",mFile,KeyStroke.getKeyStroke( KeyEvent.VK_X,Event.SHIFT_MASK + Event.ALT_MASK ),false );

        // Edit

        JMenu mEdit = AEnv.getMenu( "Edit" );

        menuBar.add( mEdit );
        aNew = addAction( "New",mEdit,KeyStroke.getKeyStroke( KeyEvent.VK_N,Event.CTRL_MASK ),false );
        aSave = addAction( "Save",mEdit,KeyStroke.getKeyStroke( KeyEvent.VK_S,Event.CTRL_MASK ),false );
        mEdit.addSeparator();
        aCopy = addAction( "Copy",mEdit,KeyStroke.getKeyStroke( KeyEvent.VK_V,Event.CTRL_MASK ),false );
        aDelete = addAction( "Delete",mEdit,KeyStroke.getKeyStroke( KeyEvent.VK_X,Event.CTRL_MASK ),false );
        aIgnore = addAction( "Ignore",mEdit,KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE,0 ),false );
        aRefresh = addAction( "Refresh",mEdit,KeyStroke.getKeyStroke( KeyEvent.VK_F5,0 ),false );
        mEdit.addSeparator();
        aFind = addAction( "Find",mEdit,KeyStroke.getKeyStroke( KeyEvent.VK_F,Event.CTRL_MASK ),true );    // toggle

        if( m_isPersonalLock ) {
            aLock = addAction( "Lock",mEdit,null,true );    // toggle
        }

        // View

        JMenu mView = AEnv.getMenu( "View" );

        menuBar.add( mView );
        if (currentUserRole.isInfoProductAccess())
        	aProduct = addAction( "InfoProduct",mView,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.CTRL_MASK ),false );
        if (currentUserRole.isInfoBPartnerAccess())
        	aBPartner = addAction( "InfoBPartner",mView,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.SHIFT_MASK + Event.CTRL_MASK ),false );
        if( currentUserRole.isShowAcct())
            aAccount = addAction( "InfoAccount",mView,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.ALT_MASK + Event.CTRL_MASK ),false );
      //  AEnv.addMenuItem( "InfoSchedule",null,null,mView,this );
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
        mView.addSeparator();
        aAttachment = addAction( "Attachment",mView,KeyStroke.getKeyStroke( KeyEvent.VK_F7,0 ),true );    // toggle
        aHistory = addAction( "History",mView,KeyStroke.getKeyStroke( KeyEvent.VK_F9,0 ),true );    // toggle
        mView.addSeparator();
        aMulti = addAction( "Multi",mView,KeyStroke.getKeyStroke( KeyEvent.VK_F8,0 ),true );    // toggle

        // Go

        JMenu mGo = AEnv.getMenu( "Go" );

        menuBar.add( mGo );
        aFirst = addAction( "First",mGo,KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP,Event.ALT_MASK ),false );
        aPrevious = addAction( "Previous",mGo,KeyStroke.getKeyStroke( KeyEvent.VK_UP,Event.ALT_MASK ),false );
        aNext = addAction( "Next",mGo,KeyStroke.getKeyStroke( KeyEvent.VK_DOWN,Event.ALT_MASK ),false );
        aLast = addAction( "Last",mGo,KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN,Event.ALT_MASK ),false );
        mGo.addSeparator();
        aParent = addAction( "Parent",mGo,KeyStroke.getKeyStroke( KeyEvent.VK_LEFT,Event.ALT_MASK ),false );
        aDetail = addAction( "Detail",mGo,KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT,Event.ALT_MASK ),false );
        mGo.addSeparator();
        aZoomAcross = addAction( "ZoomAcross",mGo,null,false );
        aRequest    = addAction( "Request",mGo,null,false );
        aArchive    = addAction( "Archive",mGo,null,false );
        aHome       = addAction( "Home",mGo,null,false );

        // Tools

        JMenu mTools = AEnv.getMenu( "Tools" );

        menuBar.add( mTools );
        aCalculator = addAction( "Calculator",mTools,null,false );
        aCalendar   = addAction( "Calendar",mTools,null,false );
      //  aEditor     = addAction( "Editor",mTools,null,false );
      //  aScript     = addAction( "Script",mTools,null,false );

        if( "Y".equals( Env.getContext( m_ctx,"#SysAdmin" ))) {    // set in DB.loginDB
            aWinSize = addAction( "WinSize",mTools,null,false );
        }

        if( AEnv.isWorkflowProcess()) {
            aWorkflow = addAction( "WorkFlow",mTools,null,false );
        }

        if( currentUserRole.isShowPreference()) {
            mTools.addSeparator();
            aPreference = addAction( "Preference",mTools,null,false );
        }

        // Help

        JMenu mHelp = AEnv.getMenu( "Help" );

        menuBar.add( mHelp );
        aHelp = addAction( "Help",mHelp,KeyStroke.getKeyStroke( KeyEvent.VK_F1,0 ),false );
        aOnline      = addAction( "Online",mHelp,null,false );
        aMailSupport = addAction( "EMailSupport",mHelp,null,false );
        aAbout       = addAction( "About",mHelp,null,false );

        int c = CPanel.WHEN_IN_FOCUSED_WINDOW;    // default condition = WHEN_FOCUSED

        // ESC = Ignore

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE,0 ),aIgnore.getName());
        getActionMap().put( aIgnore.getName(),aIgnore );

        // F1 = Help

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F1,0 ),aHelp.getName());
        getActionMap().put( aHelp.getName(),aHelp );

        // F2 = New

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F2,0 ),aNew.getName());
        getActionMap().put( aNew.getName(),aNew );

        // F3 = Delete

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F3,0 ),aDelete.getName());
        getActionMap().put( aDelete.getName(),aDelete );

        // F4 = Save

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F4,0 ),aSave.getName());
        getActionMap().put( aSave.getName(),aSave );

        // F5 = Refresh

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F5,0 ),aRefresh.getName());
        getActionMap().put( aRefresh.getName(),aRefresh );

        // F6 = Find

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F6,0 ),aFind.getName());
        getActionMap().put( aFind.getName(),aFind );

        // F7 = Arrachment

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F7,0 ),aAttachment.getName());
        getActionMap().put( aAttachment.getName(),aAttachment );

        // F8 = Multi

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F8,0 ),aMulti.getName());
        getActionMap().put( aMulti.getName(),aMulti );

        // F9 = History

        getInputMap( c ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F9,0 ),aHistory.getName());
        getActionMap().put( aHistory.getName(),aHistory );
        
        
        toolBar.add( aIgnore.getButton());     // ESC
        toolBar.addSeparator();
       // toolBar.add( aHelp.getButton());       // F1
        toolBar.add( aNew.getButton());
        toolBar.add( aDelete.getButton());
        toolBar.add( aSave.getButton());
        toolBar.addSeparator();
        toolBar.add( aRefresh.getButton());    // F5
        toolBar.add( aFind.getButton());
        toolBar.add( aAttachment.getButton());
        toolBar.add( aMulti.getButton());
        toolBar.addSeparator();
        toolBar.add( aHistory.getButton());    // F9
        toolBar.add( aHome.getButton());       // F10 is Windows Menu Key
        toolBar.add( aParent.getButton());
        toolBar.add( aDetail.getButton());
        toolBar.addSeparator();
        toolBar.add( aFirst.getButton());
        toolBar.add( aPrevious.getButton());
        toolBar.add( aNext.getButton());
        toolBar.add( aLast.getButton());
        toolBar.addSeparator();
       // toolBar.add( aReport.getButton());
       // toolBar.add( aArchive.getButton());
        toolBar.add( aPrint.getButton());
        toolBar.addSeparator();

        if( m_isPersonalLock ) {
            toolBar.add( aLock.getButton());
        }

    //    toolBar.add( aZoomAcross.getButton());

        if( aWorkflow != null ) {
            toolBar.add( aWorkflow.getButton());
        }

        toolBar.add( aRequest.getButton());
//        toolBar.add( aProduct.getButton());
        toolBar.addSeparator();
        
        toolBar.add( aEnd.getButton());

        //

        if( CLogMgt.isLevelFinest()) {
            Util.printActionInputMap( this );
        }
    }    // createMenu

    /**
     * Descripción de Método
     *
     *
     * @param action
     * @param menu
     * @param accelerator
     * @param toggle
     *
     * @return
     */

    private AppsAction addAction( String action,JMenu menu,KeyStroke accelerator,boolean toggle ) {
        AppsAction act = new AppsAction( action,accelerator,toggle );
        
        if( menu != null ) {
            menu.add( act.getMenuItem());
        }

        act.setDelegate( this );

        return act;
    }    // addAction

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public JMenuBar getMenuBar() {
        return menuBar;
    }    // getMenuBar

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTitle() {
        if( m_mWorkbench.getWindowCount() > 1 ) {
            StringBuffer sb = new StringBuffer();

            sb.append( m_mWorkbench.getName()).append( "  " ).append( Env.getContext( m_ctx,"#AD_User_Name" )).append( "@" ).append( Env.getContext( m_ctx,"#AD_Client_Name" )).append( "." ).append( Env.getContext( m_ctx,"#AD_Org_Name" )).append( " [" ).append( Env.getContext( m_ctx,"#DB_UID" )).append( "]" );

            return sb.toString();
        }

        return Env.getHeader( m_ctx,m_curWindowNo );
    }    // getTitle

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private MWorkbench m_mWorkbench;

    /** Descripción de Campos */

    private MTab m_curTab;

    /** Descripción de Campos */

    private GridController m_curGC;

    /** Descripción de Campos */

    private JTabbedPane m_curWinTab = null;

    /** Descripción de Campos */

    private int m_curWindowNo;

    /** Descripción de Campos */

    private int m_curTabIndex = -1;

    /** Descripción de Campos */

    private APanelTab m_curAPanelTab = null;

    /** Descripción de Campos */

    private boolean m_disposing = false;

    /** Descripción de Campos */

    private boolean m_errorDisplayed = false;

    /** Descripción de Campos */

    private boolean m_onlyCurrentRows = true;

    /** Descripción de Campos */

    private int m_onlyCurrentDays = 0;

    /** Descripción de Campos */

    private boolean m_isLocked = false;

    /** Descripción de Campos */

    private boolean m_isPersonalLock = MRole.getDefault().isPersonalLock();

    /** Descripción de Campos */

    private int m_lastModifiers;

    /**
     * Descripción de Método
     *
     *
     * @param AD_Workbench_ID
     * @param AD_Window_ID
     * @param query
     *
     * @return
     */

    public boolean initPanel( int AD_Workbench_ID,int AD_Window_ID,MQuery query ) {
    	return initPanel(AD_Workbench_ID, AD_Window_ID, query, null);
    }
    
    public boolean initPanel( int AD_Workbench_ID,int AD_Window_ID,MQuery query, Frame realParent ) {
        log.info( "WB=" + AD_Workbench_ID + ", Win=" + AD_Window_ID + ", Query=" + query );
        this.setName( "APanel" + AD_Window_ID );

        // Single Window
        boolean showWindow=true;

        if( AD_Workbench_ID == 0 ) {
            m_mWorkbench = new MWorkbench( m_ctx,AD_Window_ID );
        } else

        // Workbench

        {

            // m_mWorkbench = new MWorkbench(m_ctx);
            // if (!m_mWorkbench.initWorkbench (AD_Workbench_ID))
            // {
            // log.log(Level.SEVERE, "APanel.initWindow - No Workbench Model");
            // return false;
            // }
            // tabPanel.setWorkbench(true);
            // tabPanel.addChangeListener(this);

            ADialog.warn( 0,this,"","Not implemented yet" );

            return false;
        }

        Dimension windowSize = m_mWorkbench.getWindowSize();

        for( int wb = 0;wb < m_mWorkbench.getWindowCount();wb++ ) {

            // Get/set WindowNo

            m_curWindowNo = Env.createWindowNo( this );    // Timing: ca. 1.5 sec
            m_mWorkbench.setWindowNo( wb,m_curWindowNo );

            // Set AutoCommit/SO for this Window

            Env.setAutoCommit( m_ctx,m_curWindowNo,Env.isAutoCommit( m_ctx ));

            // Workbench Window

            VTabbedPane window = null;

            // just one window

            if( m_mWorkbench.getWindowCount() == 1 ) {
                window = tabPanel;
                window.setWorkbench( false );
            } else {
                VTabbedPane tp = new VTabbedPane( false );

                window = tp;
            }

            // Window Init

            window.addChangeListener( this );

            int wbType = m_mWorkbench.getWindowType( wb );

            if( wbType == MWorkbench.TYPE_WINDOW ) {
                HashMap includedMap = new HashMap( 2 );

                //

                MWindowVO wVO = AEnv.getMWindowVO( m_curWindowNo,m_mWorkbench.getWindowID( wb ),0 );

                if( wVO == null ) {
                    ADialog.error( 0,null,"AccessTableNoView","(No existe el modelo de ventana para este usuario)" );

                    return false;
                }

                MWindow mWindow = new MWindow( wVO );               // Timing: ca. 0.3-1 sec

                Env.setContext( m_ctx,m_curWindowNo,"IsSOTrx",mWindow.isSOTrx()
                        ?"Y"
                        :"N" );
                m_mWorkbench.setMWindow( wb,mWindow );

                if( wb == 0 ) {
                    m_onlyCurrentRows = mWindow.isTransaction();    // default = only current
                }

                if( windowSize == null ) {
                    windowSize = mWindow.getWindowSize();
                }

                int     tabSize     = mWindow.getTabCount();
                boolean goSingleRow = query == null;    // Zoom Query

                for( int tab = 0;tab < tabSize;tab++ ) {
                    boolean included = false;

                    // MTab

                    MTab mTab = m_mWorkbench.getMWindow( wb ).getTab( tab );

                    // Query first tab

                    if( tab == 0 ) {

                        // initial user query for single workbench tab

                        if( m_mWorkbench.getWindowCount() == 1 ) {

                            // Query automatically if high volume and no query

                            if( mTab.isHighVolume() && ( (query == null) ||!query.isActive())) {
                            	int records = 0; //Luciano Disytel  - Portado por JorgeV 2009-03-02
                				Find find = null;
                				do{
                					MField[] findFields = mTab.getFields();
                					find = new Find (Env.getFrame(this), m_curWindowNo, mTab.getName(),
                							mTab.getAD_Table_ID(), mTab.getTableName(), 
                							mTab.getWhereExtended(), findFields, 10);
                					if (find.getQuery()!=null)
                						records = find.getNoOfRecords(find.getQuery(),false);
                					else 
                						records=0;
                					if( records > MAX_RECORDS && find.getQuery() != null)
                						ADialog.info(m_curWindowNo, this, "Resultado de busqueda muy extenso. " +
                								"Cambie o agregue criterios de busqueda");
                					if(find.getQuery() == null) break;
                				}while(records > MAX_RECORDS);
                				query = find.getQuery();
                				find = null;
                				//initialQuery = true;	//	don't switch to single row
                				if(query == null){
                					query = new MQuery(mTab.getTableName());
                					query.addRestriction("1=2");
                				}
                                find = null;
                			}
                        } else if( wb != 0 )

                        // workbench dynamic query for dependent windows

                        {
                            query = m_mWorkbench.getQuery();
                        }

                        // Set initial Query on first tab

                        if( query != null ) {
                            m_onlyCurrentRows = false;    // Query might involve history
                            mTab.setQuery( query );
                        }

                        if( wb == 0 ) {
                            m_curTab = mTab;
                        }
                    }    // query on first tab

                    Component tabElement = null;

                    // GridController

                    if( mTab.isSortTab()) {
                        VSortTab st = new VSortTab( m_curWindowNo,mTab.getAD_Table_ID(),mTab.getAD_ColumnSortOrder_ID(),mTab.getAD_ColumnSortYesNo_ID());

                        st.setTabLevel( mTab.getTabLevel());
                        tabElement = st;
                    } else                                           // normal tab
                    {
                        GridController gc = new GridController();    // Timing: ca. .1 sec
                        CompiereColor cc = mWindow.getColor();

                        if( cc != null ) {
                            gc.setBackgroundColor( cc );    // set color on Window level
                        }

                        gc.initGrid( mTab,false,m_curWindowNo,this,mWindow );    // will set color on Tab level

                        // Timing: ca. 6-7 sec for first .2 for next

                        gc.addDataStatusListener( this );
                        gc.registerESCAction( aIgnore );               // register Escape Key

                        // Set First Tab

                        if( (wb == 0) && (tab == 0) ) {
                            m_curGC = gc;

                            Dimension size = gc.getPreferredSize();    // Screen Sizing

                            size.width  += 4;
                            size.height += 4;
                            gc.setPreferredSize( size );
                        }

                        tabElement = gc;

                        //If we have a zoom query, switch to single row

                        if( (tab == 0) && goSingleRow ) {
                            gc.switchSingleRow();
                        }

                        // Store GC if it has a included Tab

                        if( mTab.getIncluded_Tab_ID() != 0 ) {
                            includedMap.put( new Integer( mTab.getIncluded_Tab_ID()),gc );
                        }

                        // Is this tab included?

                        if( includedMap.size() > 0 ) {
                            GridController parent = ( GridController )includedMap.get( new Integer( mTab.getAD_Tab_ID()));

                            if( parent != null ) {
                                included = parent.includeTab( gc );

                                if( !included ) {
                                    log.log( Level.SEVERE,"Not Included = " + gc );
                                }
                            }
                        }
                    }                  // normal tab

                    if( !included )    // Add to TabbedPane
                    {
                        StringBuffer tabName = new StringBuffer();

/** JDK 1.6_014 Bug: Incorrect tab titles for JTabbedPane if using HTML (BasicTabbedPanelUI problem) */
/*
                        tabName.append( "<html>" );
                        tabName.append("<font color='BLACK'>");
                        if( mTab.isReadOnly()) {
                            tabName.append( "<i>" );
                        }

                        int pos = mTab.getName().indexOf( " " );

                        if( pos == -1 ) {
                            tabName.append( mTab.getName()).append( "" );
                        } else {
                            tabName.append( mTab.getName().substring( 0,pos )).append( "" ).append( mTab.getName().substring( pos + 1 ));
                        }

                        if( mTab.isReadOnly()) {
                            tabName.append( "</i>" );
                        }
                        tabName.append( "</font>" );
                        tabName.append( "</html>" );
*/
                        tabName.append(mTab.getName());
/** Fin JDK 1.6_014 Bug: Incorrect tab titles for JTabbedPane if using HTML (BasicTabbedPanelUI problem) */                        
                        //

                        window.addTab( tabName.toString(),mTab.getIcon(),tabElement,mTab.getDescription());

                        // caused stateChanged for first tab - 3 seconds

                        window.setMnemonicAt( tab,KeyEvent.VK_1 + tab );    // 1,2,3,..
                    }
                }    // Tab Loop

                // Tab background
                // window.setBackgroundColor(new CompiereColor(Color.magenta, Color.green));

            }    // Type-MWindow

            // Single Workbench Window Tab

            if( m_mWorkbench.getWindowCount() == 1 ) {
                window.setToolTipText( m_mWorkbench.getDescription( wb ));
            } else

            // Add Workbench Window Tab

            {
                tabPanel.addTab( m_mWorkbench.getName( wb ),m_mWorkbench.getIcon( wb ),window,m_mWorkbench.getDescription( wb ));
            }

            // Used for Env.getHeader

            Env.setContext( m_ctx,m_curWindowNo,"WindowName",m_mWorkbench.getName( wb ));
        }    // Workbench Loop

        // stateChanged (<->) triggered

        if (showWindow){
        	toolBar.setName( getTitle());
        	m_curTab.getTableModel().setChanged( false );
        	
        // 	Set Detail Button
        	
        	aDetail.setEnabled( 0 != m_curWinTab.getTabCount() - 1 );
        	
        	if( windowSize != null ) {
        		setPreferredSize( windowSize );
        	}
        	
        	Dimension size = getPreferredSize();

        	log.info( "fini - " + size );
        	m_curWinTab.requestFocusInWindow();
        }
        return showWindow;
    }    // initPanel

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getWindowIndex() {

        // only one window

        if( m_mWorkbench.getWindowCount() == 1 ) {
            return 0;
        }

        // workbench

        return tabPanel.getSelectedIndex();
    }    // getWindowIndex

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean isFirstTab() {
        return m_curWinTab.getSelectedIndex() == 0;
    }    // isFirstTab

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Image getImage() {
        return m_mWorkbench.getImage( getWindowIndex());
    }    // getImage

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dataStatusChanged( DataStatusEvent e ) {
        if( m_disposing ) {
            return;
        }

        log.info( e.getMessage());

        String dbInfo = e.getMessage();

        if( (m_curTab != null) && m_curTab.isQueryActive()) {
            dbInfo = "[ " + dbInfo + " ]";
        }

        statusBar.setStatusDB( dbInfo,e );

        // Set Message / Info

        if( (e.getAD_Message() != null) || (e.getInfo() != null) ) {
            StringBuffer sb  = new StringBuffer();
            String       msg = e.getMessage();

            if( (msg != null) && (msg.length() > 0) ) {
                sb.append( Msg.getMsg( m_ctx,e.getAD_Message()));
            }

            String info = e.getInfo();

            if( (info != null) && (info.length() > 0) ) {
                if( (sb.length() > 0) &&!sb.toString().trim().endsWith( ":" )) {
                    sb.append( ": " );
                }

                sb.append( info );
            }

            if( sb.length() > 0 ) {
                int pos = sb.indexOf( "\n" );

                if( pos != -1 ) {    // replace CR/NL
                    sb.replace( pos,pos + 1," - " );
                }

                setStatusLine( sb.toString(),e.isError());
            }
        }

        // Confirm Error

        if( e.isError() &&!e.isConfirmed()) {
            ADialog.error( m_curWindowNo,this,e.getAD_Message(),e.getInfo());
            e.setConfirmed( true );    // show just once - if MTable.setCurrentRow is involved the status event is re-issued
            m_errorDisplayed = true;
        }

        // update Navigation

        // Bug fix: Deshabilitar la navegación cuando estamos insertando datos.
        // Se rompía la aplicación al pasar registros al insertar.
        
        boolean firstRow = e.isFirstRow();
        
        aFirst.setEnabled( !firstRow && !e.isInserting() );
        aPrevious.setEnabled( !firstRow && !e.isInserting() );

        boolean lastRow = e.isLastRow();

        aNext.setEnabled( !lastRow && !e.isInserting() );
        aLast.setEnabled( !lastRow && !e.isInserting() );

        // update Change

        boolean changed      = e.isChanged() || e.isInserting();
        boolean readOnly     = m_curTab.isReadOnly();
        boolean insertRecord = !readOnly;

        if( insertRecord ) {
            insertRecord = m_curTab.isInsertRecord();
        }

        //

        aIgnore.setEnabled( changed &&!readOnly );
        aSave.setEnabled( changed &&!readOnly );

        //

        aNew.setEnabled( !changed && insertRecord );
        aCopy.setEnabled( !changed && insertRecord );
        aDelete.setEnabled( !changed &&!readOnly );
        aRefresh.setEnabled( !changed );

        // No Rows

        if( (e.getTotalRows() == 0) && insertRecord ) {
            aNew.setEnabled( true );
        }

        // Single-Multi

        aMulti.setPressed( !m_curGC.isSingleRow());

        // History (on first Tab only)

        if( isFirstTab()) {
            aHistory.setPressed( !m_curTab.isOnlyCurrentRows());
        }

        // Transaction info

        String trxInfo = m_curTab.getTrxInfo();

        if( trxInfo != null ) {
            statusBar.setInfo( trxInfo );
        }

        // Check Attachment

        boolean canHaveAttachment = m_curTab.canHaveAttachment();    // not single _ID column

        //

        if( canHaveAttachment && e.isLoading() && (m_curTab.getCurrentRow() > e.getLoadedRows())) {
            canHaveAttachment = false;
        }

        if( canHaveAttachment && (m_curTab.getRecord_ID() == -1) ) {    // No Key
            canHaveAttachment = false;
        }

        if( canHaveAttachment ) {
            aAttachment.setEnabled( true );
            aAttachment.setPressed( m_curTab.hasAttachment());
        } else {
            aAttachment.setEnabled( false );
        }

        // Lock Indicator

        if( m_isPersonalLock ) {
            aLock.setPressed( m_curTab.isLocked());
        }

        updateTabsState();
        // log.info( "APanel.dataStatusChanged - fini", e.getMessage());

    }    // dataStatusChanged

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param error
     */

    public void setStatusLine( String text,boolean error ) {
        log.fine( text );
        statusBar.setStatusLine( text,error );
    }    // setStatusLine

    /**
     * Seteo el mensaje para la ventana de dialogo
     * @param dialogMsg
     * @param statusLineMsg
     * @param error
     */
    public void setDialogMsg(String dialogMsg, String statusLineMsg, boolean error){
    	setStatusLine( statusLineMsg, error);
    	statusBar.setDialogMsg(dialogMsg);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param busy
     */

    private void setBusy( boolean busy ) {
        m_isLocked = busy;

        //

        JFrame frame = Env.getFrame( this );

        if( frame == null ) {    // during init
            return;
        }

        if( frame instanceof AWindow ) {
            (( AWindow )frame ).setBusy( busy );
        }

        // String processing = Msg.getMsg(m_ctx, "Processing");

        if( busy ) {

            // setStatusLine(processing);

            this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
            frame.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        } else {
            this.setCursor( Cursor.getDefaultCursor());
            frame.setCursor( Cursor.getDefaultCursor());

            // if (statusBar.getStatusLine().equals(processing))
            // statusBar.setStatusLine("");

        }
    }    // set Busy

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        if( m_disposing ) {
            return;
        }

        log.info( e.toString());
        setBusy( true );

        VTabbedPane tp          = ( VTabbedPane )e.getSource();
        boolean     back        = false;
        boolean     isAPanelTab = false;

        // Workbench Tab Change

        if( tp.isWorkbench()) {
            int WBIndex = tabPanel.getSelectedIndex();

            m_curWindowNo = m_mWorkbench.getWindowNo( WBIndex );

            // Window Change

            log.info( "curWin=" + m_curWindowNo + " - Win=" + tp );

            if( tp.getSelectedComponent() instanceof JTabbedPane ) {
                m_curWinTab = ( JTabbedPane )tp.getSelectedComponent();
            } else {
                throw new java.lang.IllegalArgumentException( "Window does not contain Tabs" );
            }

            if( m_curWinTab.getSelectedComponent() instanceof GridController ) {
                m_curGC = ( GridController )m_curWinTab.getSelectedComponent();

                // else if (m_curWinTab.getSelectedComponent() instanceof APanelTab)
                // isAPanelTab = true;

            } else {
                throw new java.lang.IllegalArgumentException( "Window-Tab does not contain GridControler" );
            }

            // change pointers

            m_curTabIndex = m_curWinTab.getSelectedIndex();
        } else {

            // Just a Tab Change

            log.info( "Tab=" + tp );
            m_curWinTab = tp;

            int tpIndex = m_curWinTab.getSelectedIndex();

            back = tpIndex < m_curTabIndex;

            GridController gc = null;

            if( m_curWinTab.getSelectedComponent() instanceof GridController ) {
                gc = ( GridController )m_curWinTab.getSelectedComponent();
            } else if( m_curWinTab.getSelectedComponent() instanceof APanelTab ) {
                isAPanelTab = true;
            } else {
                throw new java.lang.IllegalArgumentException( "Tab does not contain GridControler" );
            }

            // Save old Tab

            if( m_curGC != null ) {
                m_curGC.stopEditor( true );

                // has anything changed?

                if( m_curTab.needSave( true,false )) {       // do we have real change
                    if( m_curTab.needSave( true,true )) {    // explicitly ask when changing tabs
                        if( ADialog.ask( m_curWindowNo,this,"SaveChanges?",m_curTab.getCommitWarning())) {    // yes we want to save
                            if( !m_curTab.dataSave( true )) {    // there is a problem, so we go back
                                m_curWinTab.setSelectedIndex( m_curTabIndex );
                                setBusy( false );

                                return;
                            }
                        } else {    // Don't save
                            m_curTab.dataIgnore();
                        }
                    } else {        // new record, but nothing changed
                        m_curTab.dataIgnore();
                    }
                }                   // there is a change
            }

            if( m_curAPanelTab != null ) {
                m_curAPanelTab.saveData();
                m_curAPanelTab.unregisterPanel();
                m_curAPanelTab = null;
            }

            // new tab
            // if (m_curTabIndex >= 0)
            // m_curWinTab.setForegroundAt(m_curTabIndex, CompierePLAF.getTextColor_Normal());
            // m_curWinTab.setForegroundAt(tpIndex, CompierePLAF.getTextColor_OK());

            m_curTabIndex = tpIndex;

            if( !isAPanelTab ) {
                m_curGC = gc;
            }
        }

        // Sort Tab Handling

        if( isAPanelTab ) {
            m_curAPanelTab = ( APanelTab )m_curWinTab.getSelectedComponent();
            m_curAPanelTab.registerAPanel( this );
            m_curAPanelTab.loadData();
        } else                                 // Cur Tab Setting
        {
            m_curGC.activate();
            m_curTab = m_curGC.getMTab();

            // Refresh only current row when tab is current

            if( back && m_curTab.isCurrent()) {
                m_curTab.dataRefresh();
            } else {                           // Requery & autoSize
                m_curGC.query( m_onlyCurrentRows,m_onlyCurrentDays );
            }

            // Set initial record

            if( m_curTab.getRowCount() == 0 ) {
                // Automatically create New Record, if none & tab not RO

                  	if( !m_curTab.isReadOnly()) {
                    log.config( "no record - creating new" );
                    m_curTab.dataNew( false );
                    // m_curTab.dataIgnore();           // Disytel: no insertar automaticamente cuando no hay registros 
                    									// (MTable se encargará de realizar la inserción cuando sea necesario)
                    									// Se deja el dataNew y se invoca al dataIgnore a fin de respetar los eventos relacionados
                    									// (Revertido: problema con pestañas incluidas)
                }
                	m_curTab.navigateCurrent();    // updates counter
                	m_curGC.dynamicDisplay( 0 );
                
            } else {
                m_curTab.navigateCurrent();
            }
        }

        // Update <-> Navigation

        aDetail.setEnabled( m_curTabIndex != m_curWinTab.getTabCount() - 1 );
        aParent.setEnabled( (m_curTabIndex != 0) && (m_curWinTab.getTabCount() > 1) );

        // History (on first tab only)

        if( m_mWorkbench.getMWindow( getWindowIndex()).isTransaction()) {
            aHistory.setEnabled( isFirstTab());
        } else {
            aHistory.setPressed( false );
            aHistory.setEnabled( false );
        }

        // Document Print

        aPrint.setEnabled( m_curTab.isPrinted());

        // Query

        aFind.setPressed( m_curTab.isQueryActive());

        // Order Tab

        if( isAPanelTab ) {
            aMulti.setPressed( false );
            aMulti.setEnabled( false );
            aNew.setEnabled( false );
            aDelete.setEnabled( false );
            aFind.setEnabled( false );
            aRefresh.setEnabled( false );
            aAttachment.setEnabled( false );
        } else    // Grid Tab
        {
            aMulti.setEnabled( true );
            aMulti.setPressed( !m_curGC.isSingleRow());
            aFind.setEnabled( true );
            aRefresh.setEnabled( true );
            aAttachment.setEnabled( true );
        }

        //

        m_curWinTab.requestFocusInWindow();
        setBusy( false );
        log.config( "fini" );
    }    // stateChanged

    /**
     * Descripción de Método
     *
     */

    public void cmd_detail() {
        int index = m_curWinTab.getSelectedIndex();

        if( index == m_curWinTab.getTabCount() - 1 ) {
            return;
        }

        m_curGC.getTable().removeEditor();
        m_curWinTab.setSelectedIndex( index + 1 );
    }    // navigateDetail


    /**
     * Descripción de Método
     *
     *
     * @param tabla_hijo
     * @param id_hijo
     */

    public void irATablaRegistro( String tabla_hijo,int id_hijo ) {
        int index = m_curWinTab.getSelectedIndex();

        if( index == m_curWinTab.getTabCount() - 1 ) {
            return;
        }

        m_curGC.getTable().removeEditor();
        
        // buscar la tabla
        while (!tabla_hijo.equals(m_curTab.getTableName()) && m_curWinTab.getSelectedIndex() < m_curWinTab.getTabCount())
        	m_curWinTab.setSelectedIndex( index++ );

        // si no la encontró, moverme a la primer pestaña, primer registro
        if (!tabla_hijo.equals(m_curTab.getTableName()))
        {
        	m_curWinTab.setSelectedIndex(0);
        	return;
        }		
        		
        // buscar el registro
        if( id_hijo != 0 ) {
            while(( m_curTab.getRowCount() > m_curTab.getCurrentRow()) && ( m_curTab.getKeyID( m_curTab.getCurrentRow()) != id_hijo )) {
                m_curGC.getTable().removeEditor();
                m_curTab.navigateRelative( +1 );
            }
        }
    }

    /**
     * Descripción de Método
     *
     */

    private void cmd_parent() {
        int index = m_curWinTab.getSelectedIndex();

        if( index == 0 ) {
            return;
        }

        m_curGC.getTable().removeEditor();
        m_curWinTab.setSelectedIndex( index - 1 );
    }    // navigateParent

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        
        if( m_disposing || isUILocked()) {
            return;
        }

        m_lastModifiers = e.getModifiers();

        String cmd = e.getActionCommand();

        // Do ScreenShot w/o busy

        if( cmd.equals( "ScreenShot" )) {
            AEnv.actionPerformed( e.getActionCommand(),m_curWindowNo,this );

            return;
        }

        // Problem: doubleClick detection - can't disable button as clicking button may change button status

        setBusy( true );

        // Command Buttons

        if( e.getSource() instanceof VButton ) {
            actionButton(( VButton )e.getSource());
            setBusy( false );

            return;
        }

        try {

            // File

            if( cmd.equals( aReport.getName())) {
                cmd_report( e.getSource());
            } else if( cmd.equals( aPrint.getName())) {
                cmd_print( e.getSource());
            } else if( cmd.equals( aEnd.getName())) {
                cmd_end( false );
            } else if( cmd.equals( aExit.getName())) {
                cmd_end( true );

                // Edit

            } else if( cmd.equals( aNew.getName())) {
                cmd_new( false );
            } else if( cmd.equals( aSave.getName())) {
                cmd_save( true );
            } else if( cmd.equals( aCopy.getName())) {
                cmd_new( true );
            } else if( cmd.equals( aDelete.getName())) {
                cmd_delete();
            } else if( cmd.equals( aIgnore.getName())) {
                cmd_ignore();
            } else if( cmd.equals( aRefresh.getName())) {
                cmd_refresh();
            } else if( cmd.equals( aFind.getName())) {
                cmd_find();
            } else if( m_isPersonalLock && cmd.equals( aLock.getName())) {
                cmd_lock();

                // View

            } else if( cmd.equals( aAttachment.getName())) {
                cmd_attachment();
            } else if( cmd.equals( aHistory.getName())) {
                cmd_history();
            } else if( cmd.equals( aMulti.getName())) {
                m_curGC.switchRowPresentation();

                // Go

            } else if( cmd.equals( aFirst.getName())) {       /* cmd_save(false); */
                m_curGC.getTable().removeEditor();
                m_curTab.navigate( 0 );
            } else if( cmd.equals( aPrevious.getName())) {    /* cmd_save(false); */
                m_curGC.getTable().removeEditor();
                m_curTab.navigateRelative( -1 );
            } else if( cmd.equals( aNext.getName())) {    /* cmd_save(false); */
                m_curGC.getTable().removeEditor();
                m_curTab.navigateRelative( +1 );
            } else if( cmd.equals( aLast.getName())) {    /* cmd_save(false); */
                m_curGC.getTable().removeEditor();
                m_curTab.navigate( m_curTab.getRowCount() - 1 );
            } else if( cmd.equals( aParent.getName())) {
                cmd_parent();
            } else if( cmd.equals( aDetail.getName())) {
                cmd_detail();
            } else if( cmd.equals( aZoomAcross.getName())) {
                cmd_zoomAcross();
            } else if( cmd.equals( aRequest.getName())) {
                cmd_request();
            } else if( cmd.equals( aArchive.getName())) {
                cmd_archive();

                // Tools

            } else if( (aWorkflow != null) && cmd.equals( aWorkflow.getName())) {
                if( m_curTab.getRecord_ID() <= 0 ) {
                    ;
                } else if( (m_curTab.getTabNo() == 0) && m_mWorkbench.getMWindow( getWindowIndex()).isTransaction()) {
                    AEnv.startWorkflowProcess( m_curTab.getAD_Table_ID(),m_curTab.getRecord_ID());
                } else {
                    AEnv.startWorkflowProcess( m_curTab.getAD_Table_ID(),m_curTab.getRecord_ID());
                }
            } else if( (aWinSize != null) && cmd.equals( aWinSize.getName())) {
                cmd_winSize();

                // Help

            } else if( cmd.equals( aHelp.getName())) {
                cmd_help();

                // General Commands (Environment)

            } else if( !AEnv.actionPerformed( e.getActionCommand(),m_curWindowNo,this )) {
                log.log( Level.SEVERE,"No action for: " + cmd );
            }
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"ex",ex );

            String msg = ex.getMessage();

            if( (msg == null) || (msg.length() == 0) ) {
                msg = ex.toString();
            }

            msg = Msg.parseTranslation( m_ctx,msg );
            ADialog.error( m_curWindowNo,this,"Error",msg );
        }

        //
        m_curWinTab.requestFocusInWindow();
        requestFocus();
        setBusy( false );
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param copy
     */

    private void cmd_new( boolean copy ) {
        log.config( "copy=" + copy );

        if( !m_curTab.isInsertRecord()) {
            log.warning( "Insert Record disabled for Tab" );

            return;
        }

        cmd_save( false );
        m_curTab.dataNew( copy );
        m_curGC.dynamicDisplay( 0 );

        // m_curTab.getTableModel().setChanged(false);

    }    // cmd_new

    /**
     * Descripción de Método
     *
     */

    private void cmd_delete() {
        if( m_curTab.isReadOnly()) {
            return;
        }

        int keyID = m_curTab.getRecord_ID();

        if( ADialog.ask( m_curWindowNo,this,"DeleteRecord?" )) {
            if( m_curTab.dataDelete()) {
                m_curGC.rowChanged( false,keyID );
                m_curGC.refreshIncludedGC();
            }
        }

        m_curGC.dynamicDisplay( 0 );
    }    // cmd_delete

    /**
     * Descripción de Método
     *
     *
     * @param manualCmd
     *
     * @return
     */

    private boolean cmd_save( boolean manualCmd ) {
        if( m_curAPanelTab != null ) {
            manualCmd = false;
        }

        log.config( "Manual=" + manualCmd );
        m_errorDisplayed = false;
        m_curGC.stopEditor( true );

        boolean saveOK = true;

        if( m_curAPanelTab != null ) {
            m_curAPanelTab.saveData();
            aSave.setEnabled( false );    // set explicitly
        }

        
        if( (m_curTab.getCommitWarning().length() > 0) && m_curTab.needSave( true,false )) {
            if( !ADialog.ask( m_curWindowNo,this,"SaveChanges?",m_curTab.getCommitWarning())) {
                return false;
            }
        }

        // manually initiated

        boolean retValue = m_curTab.dataSave( manualCmd );

        // if there is no previous error

        if( manualCmd &&!retValue &&!m_errorDisplayed ) {
            ADialog.error( m_curWindowNo,this,"SaveIgnored" );
            setStatusLine( Msg.getMsg( m_ctx,"SaveIgnored" ),true );
        }

        m_curGC.rowChanged( true,m_curTab.getRecord_ID());

        if( manualCmd ) {
            m_curGC.dynamicDisplay( 0 );
        }
        
        // Se refresca la pestaña incluida en caso de que exista.
        if(retValue && manualCmd)
        	m_curGC.refreshIncludedGC();
        
        return retValue;
    }    // cmd_save

    /**
     * Descripción de Método
     *
     */

    private void cmd_ignore() {
        m_curGC.stopEditor( false );
        m_curTab.dataIgnore();
        m_curTab.clearCurrentRecordWarning();
        m_curGC.dynamicDisplay( 0 );
    }    // cmd_ignore

    /**
     * Descripción de Método
     *
     */

    private void cmd_refresh() {
        cmd_save( false );
        m_curTab.dataRefreshAll();
        m_curGC.dynamicDisplay( 0 );
    }    // cmd_refresh

    /**
     * Descripción de Método
     *
     *
     * @param source
     */

    private void cmd_report( Object source ) {

        if( !MRole.getDefault().isCanReport( m_curTab.getAD_Table_ID())) {
            ADialog.error( m_curWindowNo,this,"AccessCannotReport" );

            return;
        }

        cmd_save( false );

        // Query

        MQuery query = new MQuery( m_curTab.getTableName());

        // Link for detail records

        String queryColumn = m_curTab.getLinkColumnName();

        // Current row otherwise

        if( queryColumn.length() == 0 ) {
            queryColumn = m_curTab.getKeyColumnName();
        }

        // Find display

        String infoName    = null;
        String infoDisplay = null;

        for( int i = 0;i < m_curTab.getFieldCount();i++ ) {
            MField field = m_curTab.getField( i );

            if( field.isKey()) {
                infoName = field.getHeader();
            }

            if(( field.getColumnName().equals( "Name" ) || field.getColumnName().equals( "DocumentNo" )) && (field.getValue() != null) ) {
                infoDisplay = field.getValue().toString();
            }

            if( (infoName != null) && (infoDisplay != null) ) {
                break;
            }
        }

        if( queryColumn.length() != 0 ) {
            if( queryColumn.endsWith( "_ID" )) {
                query.addRestriction( queryColumn,MQuery.EQUAL,new Integer( Env.getContextAsInt( m_ctx,m_curWindowNo,queryColumn )),infoName,infoDisplay );
            } else {
                query.addRestriction( queryColumn,MQuery.EQUAL,Env.getContext( m_ctx,m_curWindowNo,queryColumn ),infoName,infoDisplay );
            }
        }

        new AReport( m_curTab.getAD_Table_ID(),( JComponent )source,query );
    }    // cmd_report

    /**
     * Descripción de Método
     *
     */

    private void cmd_zoomAcross() {
        int record_ID = m_curTab.getRecord_ID();

        log.info( "ID=" + record_ID );

        if( record_ID <= 0 ) {
            return;
        }

        // Query

        MQuery query = new MQuery();

        // Link for detail records

        String link = m_curTab.getLinkColumnName();

        // Current row otherwise

        if( link.length() == 0 ) {
            link = m_curTab.getKeyColumnName();
        }

        if( link.length() != 0 ) {
            if( link.endsWith( "_ID" )) {
                query.addRestriction( link,MQuery.EQUAL,new Integer( Env.getContextAsInt( m_ctx,m_curWindowNo,link )));
            } else {
                query.addRestriction( link,MQuery.EQUAL,Env.getContext( m_ctx,m_curWindowNo,link ));
            }
        }

        new AZoomAcross( aZoomAcross.getButton(),m_curTab.getTableName(),query );
    }    // cmd_zoom

    /**
     * Descripción de Método
     *
     */

    private void cmd_request() {
        int record_ID = m_curTab.getRecord_ID();

        log.info( "ID=" + record_ID );

        if( record_ID <= 0 ) {
            return;
        }

        int AD_Table_ID = m_curTab.getAD_Table_ID();

        new ARequest( aRequest.getButton(),AD_Table_ID,record_ID );
    }    // cmd_request

    /**
     * Descripción de Método
     *
     */

    private void cmd_archive() {
        int record_ID = m_curTab.getRecord_ID();

        log.info( "ID=" + record_ID );

        if( record_ID <= 0 ) {
            return;
        }

        int AD_Table_ID = m_curTab.getAD_Table_ID();

        new AArchive( aArchive.getButton(),AD_Table_ID,record_ID );
    }    // cmd_archive

    /**
     * Descripción de Método
     *
     *
     * @param source
     */

    private void cmd_print( Object source ) {

    	/* Priorizar informacion en C_DocType para la impresion de reportes - Mauricio Calgaro */
        int AD_Process_ID = 0;
        MField field = null;
        
        // Busco si existe alguna columna que se llame "C_DocTypeTarget_ID" y obtengo el id.
        // Si no encuentro busco si existe alguna columna que se llame "C_DocType_ID" y obtengo el id.        
        field = m_curTab.getField("C_DocTypeTarget_ID");
        if (field == null)
        	field = m_curTab.getField("C_DocType_ID");

        if (field != null)
        {
        	int C_DocType_ID = (Integer)field.getValue();
        	MDocType DocType = new MDocType(m_ctx, C_DocType_ID, null);
       		AD_Process_ID = DocType.getAD_Process_ID();
       	}

        // Get process defined for this tab > Si no encuentra ningún ID de informe asociado, entonces acá obtiene el informe asociado a la pestaña correspondiente.
        if (AD_Process_ID==0) { AD_Process_ID = m_curTab.getAD_Process_ID(); } 

        log.info( "ID=" + AD_Process_ID );

        // No report defined
        
        if( AD_Process_ID == 0 ) {
            cmd_report( source );

            return;
        }

        cmd_save( false );

        //

        int         table_ID  = m_curTab.getAD_Table_ID();
        int         record_ID = m_curTab.getRecord_ID();
        ProcessInfo pi        = new ProcessInfo( getTitle(),AD_Process_ID,table_ID,record_ID );

        ProcessCtl.process( this,m_curWindowNo,pi,null );    // calls lockUI, unlockUI
    }    // cmd_print

    /**
     * Descripción de Método
     *
     */

    private void cmd_find() {
        if( m_curTab == null ) {
            return;
        }

        cmd_save( false );

        // Gets Fields from AD_Field_v

        Find find = null;
        
        MQuery query = null;
        int records=0;
    	do{
    		MField[] findFields = MField.createFields( m_ctx,m_curWindowNo,0,m_curTab.getAD_Tab_ID());
    		find = new Find( Env.getFrame( this ),m_curWindowNo,m_curTab.getName(),m_curTab.getAD_Table_ID(),m_curTab.getTableName(),m_curTab.getWhereExtended(),findFields,1 );
    		query = find.getQuery();	
			if (find.getQuery()!=null)
				records = find.getNoOfRecords(find.getQuery(),false);
			else 
				records=0;
			if( records > MAX_RECORDS && find.getQuery() != null)
				ADialog.info(m_curWindowNo, this, "Resultado de busqueda muy extenso. " +
						"Cambie o agregue criterios de busqueda");			
		}while(records > MAX_RECORDS && m_curTab.isHighVolume() );
		query = find.getQuery();
		find = null;
		//initialQuery = true;	//	don't switch to single row
		     

        find = null;

        // Confirmed query

        if( query != null ) {
            m_onlyCurrentRows = false;                               // search history too
            m_curTab.setQuery( query );
            m_curGC.query( m_onlyCurrentRows,m_onlyCurrentDays );    // autoSize
        }

        aFind.setPressed( m_curTab.isQueryActive());
    }    // cmd_find

    /**
     * Descripción de Método
     *
     */

    private void cmd_attachment() {

        int record_ID = m_curTab.getRecord_ID();

        if( record_ID == -1 )    // No Key
        {
            aAttachment.setEnabled( false );

            return;
        }

        Attachment va = new Attachment( Env.getFrame( this ),m_curWindowNo,m_curTab.getAD_AttachmentID(),m_curTab.getAD_Table_ID(),record_ID,null );

        //

        m_curTab.loadAttachments();    // reload
        aAttachment.setPressed( m_curTab.hasAttachment());
    }                                  // attachment

    /**
     * Descripción de Método
     *
     */

    private void cmd_lock() {
        log.info( "Modifiers=" + m_lastModifiers );

        if( !m_isPersonalLock ) {
            return;
        }

        int record_ID = m_curTab.getRecord_ID();

        if( record_ID == -1 ) {    // No Key
            return;
        }

        // Control Pressed

        if(( m_lastModifiers & InputEvent.CTRL_MASK ) != 0 ) {
            new RecordAccessDialog( Env.getFrame( this ),m_curTab.getAD_Table_ID(),record_ID );
        } else {
            m_curTab.lock( Env.getCtx(),record_ID,aLock.getButton().isSelected());
            m_curTab.loadAttachments();    // reload
        }

        aLock.setPressed( m_curTab.isLocked());
    }    // lock

    /**
     * Descripción de Método
     *
     */

    private void cmd_history() {

        if( m_mWorkbench.getMWindow( getWindowIndex()).isTransaction()) {
            if( m_curTab.needSave( true,true ) &&!cmd_save( false )) {
                return;
            }

            Point pt = new Point( 0,aHistory.getButton().getBounds().height );

            SwingUtilities.convertPointToScreen( pt,aHistory.getButton());

            VOnlyCurrentDays ocd = new VOnlyCurrentDays( Env.getFrame( this ),pt );

            m_onlyCurrentDays = ocd.getCurrentDays();

            if( m_onlyCurrentDays == 1 )    // Day
            {
                m_onlyCurrentRows = true;
                m_onlyCurrentDays = 0;      // no Created restriction
            } else {
                m_onlyCurrentRows = false;
            }

            log.config( "OnlyCurrent=" + m_onlyCurrentRows + ", Days=" + m_onlyCurrentDays );
            m_curGC.query( m_onlyCurrentRows,m_onlyCurrentDays );    // autoSize
        }
    }                                                                // cmd_history

    /**
     * Descripción de Método
     *
     */

    private void cmd_help() {

        Help hlp = new Help( Env.getFrame( this ),this.getTitle(),m_mWorkbench.getMWindow( getWindowIndex()));

        hlp.setVisible( true );
    }    // cmd_help

    /**
     * Descripción de Método
     *
     *
     * @param exit
     */

    private void cmd_end( boolean exit ) {
        boolean exitSystem = false;

        cmd_save( false );

        if( m_curAPanelTab != null ) {
            m_curAPanelTab.unregisterPanel();
            m_curAPanelTab = null;
        }

        if( exit ) {
        	if(Env.closeApp(Env.getCtx())){
        		if(ADialog.ask( m_curWindowNo,this,"ExitApplication?" )){
                    exitSystem = true;
        		}
        	}
        	else{
        		ADialog.info(m_curWindowNo, this, LYCloseWindowAdapter.getMsg());
        		return;
        	}
        }

        Env.getFrame( this ).dispose();    // calls this dispose

        if( exitSystem ) {
            AEnv.exit( 0 );
        }
    }    // cmd_end

    /**
     * Descripción de Método
     *
     */

    private void cmd_winSize() {
        Dimension size = getSize();

        if( !ADialog.ask( m_curWindowNo,this,"WinSizeSet","x=" + size.width + " - y=" + size.height )) {
            setPreferredSize( null );
            SwingUtilities.getWindowAncestor( this ).pack();
            size = new Dimension( 0,0 );
        }

        //

        M_Window win = new M_Window( m_ctx,m_curTab.getAD_Window_ID(),null );

        win.setWindowSize( size );
        win.save();
    }    // cmdWinSize

    /**
     * Descripción de Método
     *
     *
     * @param vButton
     */

    private void actionButton( VButton vButton ) 
    {

        boolean startWOasking = false;
        String  col           = vButton.getColumnName();
        int table_ID = m_curTab.getAD_Table_ID();
        boolean completingFiscalDocument = false;

        // Record_ID

        int record_ID = m_curTab.getRecord_ID();

        // Record_ID - Language Handling

        if( (record_ID == -1) && m_curTab.getKeyColumnName().equals( "AD_Language" ))
        {
            record_ID = Env.getContextAsInt( m_ctx,m_curWindowNo,"AD_Language_ID" );
        }

        // Record_ID - Change Log ID

        if( (record_ID == -1) && (vButton.getProcess_ID() == 306) )
        {
            Integer id = ( Integer )m_curTab.getValue( "AD_ChangeLog_ID" );
            record_ID = id.intValue();
        }

        // Ensure it's saved

        if( (record_ID == -1) && m_curTab.getKeyColumnName().endsWith( "_ID" )) 
        {
            ADialog.error( m_curWindowNo,this,"SaveErrorRowNotFound" );
            return;
        }
     
        if (col.equals("ProcCreate"))
		{
    		
			if (m_curTab.getAD_Table_ID() == MPriceListVersion.Table_ID)
			{
				FormFrame ff = new FormFrame();

				// Validar que el usuario este de acuerdo con la generacion de 
				// entradas en la tabla de importación de listas de precios
				if (!ADialog.ask(m_curWindowNo, this, "ConfirmProductPriceGeneration"))
					return;
				
				try
				{
					//	Create instance w/o parameters
					FormPanel m_panel= null;
					m_panel = new VProdPricGen(m_curTab);
					ff.setFormPanel(m_panel);
					m_panel.init(ff.getWindowNo(), ff);
					//ff.pack();
					//AEnv.showCenterScreen(ff);
					return;
				}
				catch (Exception e)
				{
					log.log(Level.SEVERE,"ProdPricGen.openForm =" + e);
				}
			}

        // Zoom
		}//If (col.equals("ProcCreate"))
       
        if( col.equals( "Record_ID" ))
        {
            int AD_Table_ID = Env.getContextAsInt( m_ctx,m_curWindowNo,"AD_Table_ID" );
            int Record_ID = Env.getContextAsInt( m_ctx,m_curWindowNo,"Record_ID" );

            AEnv.zoom( AD_Table_ID,Record_ID );

            return;
            //Begin JRBV - Dataware - NewFunction
        }
        else if (col.equals("GoParent"))
        {
        	OpenParent OpenNew= new OpenParent();
        	OpenNew.Open(m_ctx,m_curWindowNo,m_curTab);
        }
        	//End JRBV - Dataware - NewFunction

        // save first  ---------------

        if( m_curTab.needSave( true,false ))
        {
            if( !cmd_save( true )) {
                return;
            }
        }

        //

        

        // Pop up Payment Rules
        
        if( col.equals( "PaymentRule" ))
        {
            VPayment vp = new VPayment( m_curWindowNo,m_curTab,vButton );

            if( vp.isInitOK()) {    // may not be allowed
                vp.setVisible( true );
            }
            //Anulado por ConSeti el 16/11/2006
            //vp.dispose();

            if( vp.needSave()) {
                cmd_save( false );
                cmd_refresh();
            }
        }                           // PaymentRule

        // Pop up Document Action
        
        else if( col.equals( "DocAction" ))
        {
        	
            VDocAction vda = new VDocAction( m_curWindowNo,m_curTab,vButton,record_ID );

            // Something to select from?

            if( vda.getNumberOfOptions() == 0 ) {
                vda.dispose();
                log.info( "DocAction - No Options" );

                return;
            } else 
                vda.setVisible(true);
            	
                if( !vda.getStartProcess())
                    return;

                startWOasking = true;
                vda.dispose();
          }// DocAction
           

        // Pop up Create From
        //log.info("comprobamos si es createFrom");
        else if(col.equals( "generate" )){
        	log.fine("En APanel---generate");
        	FormFrame ff = new FormFrame();

			try
			{
				//	Create instance w/o parameters
				FormPanel m_panel= null;
				log.fine("En APanel con la remesa="+Env.getContextAsInt( m_ctx,m_curWindowNo,"C_Remesa_ID" ));
				m_panel = new VInvoiceRemGen(m_curTab);
				ff.setFormPanel(m_panel);
				m_panel.init(ff.getWindowNo(), ff);
				ff.pack();
				AEnv.showCenterScreen(ff);
				return;
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE,"ProdPricGen.openForm =" + e);
			}
            }
        else if (col.equals( "MatchStatement"))
        {
//        	Custom button text
        	Object[] optionsLineas = {Msg.getMsg(Env.getCtx(), "ImportedStatementLines"),
        			Msg.getMsg(Env.getCtx(), "CurrentStatementLines")};
        	
        	int modoLineas = JOptionPane.showOptionDialog(null,
        		Msg.getMsg(Env.getCtx(), "BankStatementMatchingWhat"),
        	    Msg.getMsg(Env.getCtx(), "BankStatementMatching"),
        	    JOptionPane.YES_NO_CANCEL_OPTION,
        	    JOptionPane.QUESTION_MESSAGE,
        	    null,
        	    optionsLineas,
        	    optionsLineas[0]);
        	
        	if (modoLineas == -1)
        		return;
        	
        	int modoPagos = 2;
        	
        	if (modoPagos == -1)
        		return;
        	
       		new VConciliacionExtractoImportado(m_curTab, modoLineas, modoPagos).setVisible(true);
        	
        	return;
        }
        else if( col.equalsIgnoreCase( "OpenMatrix" ))
        {
        	processOpenMatrixButton(getCurrentTab().getTableName());
        	return;
        }
        else if( col.equalsIgnoreCase( "CreateMatrix" ) )
        {
        	if (m_curTab.needSave( true,false )) {
        		ADialog.error(m_curWindowNo, this, "SaveErrorRowNotFound");
        	} else {
        		VPriceInstanceMatrix ff = new VPriceInstanceMatrix(m_curWindowNo, (Frame)getRootPane().getParent(), null, null, false );
	        	AEnv.showCenterScreen( ff );
	        	m_curTab.dataRefreshAll();
        	}
        	
        	return;
        }
        else if( col.equalsIgnoreCase( "CreateUpcMatrix" ) )
        {
        	if (m_curTab.needSave( true,false )) {
        		ADialog.error(m_curWindowNo, this, "SaveErrorRowNotFound");
        	} else {
        		VUpcInstanceMatrix ff = new VUpcInstanceMatrix(m_curWindowNo, (Frame)getRootPane().getParent(), null, false );
	        	AEnv.showCenterScreen( ff );
	        	m_curTab.dataRefreshAll();
        	}
        	
        	return;
        }
        else if( col.equals( "CreateFrom" ))
        {

            // m_curWindowNo

            VCreateFrom vcf = VCreateFrom.create( m_curTab );

            if( vcf != null ) {
                if( vcf.isInitOK()) {
                    vcf.setVisible( true );
                    vcf.dispose();
                    m_curTab.dataRefresh();
                } else {
                    vcf.dispose();
                }

                return;
            }
            // else may start process

        }    // CreateFrom

        // Posting -----
        //log.log("comprobamos si es Posted");
        else if( col.equals( "Posted" ) && MRole.getDefault().isShowAcct())
        {

            // Check Doc Status

            String processed = Env.getContext( m_ctx,m_curWindowNo,"Processed" );

            if( !processed.equals( "Y" )) {
                ADialog.error( m_curWindowNo,this,"PostDocNotComplete" );

                return;
            }

            // Check Post Status

            Object ps = m_curTab.getValue( "Posted" );

            if( (ps != null) && ps.equals( "Y" )) {
                new org.openXpertya.acct.AcctViewer( Env.getContextAsInt( m_ctx,m_curWindowNo,"AD_Client_ID" ),m_curTab.getAD_Table_ID(),m_curTab.getRecord_ID());
            } else {
                if( ADialog.ask( m_curWindowNo,this,"PostImmediate?" )) {
                    AEnv.postImmediate( m_curWindowNo,Env.getContextAsInt( m_ctx,m_curWindowNo,"AD_Client_ID" ),m_curTab.getAD_Table_ID(),m_curTab.getRecord_ID(),true );
                    m_curTab.dataRefresh();
                }
            }

            return;
        }    // Posted

        log.config( "Process_ID=" + vButton.getProcess_ID() + ", Record_ID=" + record_ID +" y la ventana en la que lanza el proceso es="+m_curWindowNo);

        if( vButton.getProcess_ID() == 0 ) {
            return;
        }

        // Save item changed

        if( m_curTab.needSave( true,false )) {
            if( !cmd_save( true )) {
                return;
            }
        }

        // Ask user to start process, if Description and Help is not empty

        if( !startWOasking &&!( vButton.getDescription().equals( "" ) && vButton.getHelp().equals( "" ))) {
            if( !ADialog.ask( m_curWindowNo,this,"StartProcess?",

            // "<b><i>" + vButton.getText() + "</i></b><br>" +

            vButton.getDescription() + "\n" + vButton.getHelp())) {
                return;
            }
        }

        //

        String title = vButton.getDescription();

        if( (title == null) || (title.length() == 0) ) {
        	title = vButton.getText();
        }

        ProcessInfo pi = new ProcessInfo( title,vButton.getProcess_ID(),table_ID,record_ID );

        pi.setAD_User_ID( Env.getAD_User_ID( m_ctx ));
        pi.setAD_Client_ID( Env.getAD_Client_ID( m_ctx ));
        // Linea añadida por Dataware S.L. para poder saber en un proceso la ventana que lo lanza
        pi.setWindowNo(m_curWindowNo);

        // Trx trx = Trx.get(Trx.createTrxName("AppsPanel"), true);

        ProcessCtl.process( this,m_curWindowNo,pi,null );
    }//ActionButton
        // calls lockUI, unlockUI
    // actionButton
    
    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void lockUI( ProcessInfo pi ) {

        // log.fine("" + pi);

        setBusy( true );
    }    // lockUI

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void unlockUI( ProcessInfo pi ) {

        // log.fine("" + pi);

        setBusy( false );

        // Process Result

        if( (pi != null    // refresh if not print
                ) && (pi.getAD_Process_ID() != m_curTab.getAD_Process_ID())) {

            // Refresh data

            m_curTab.dataRefresh();
            m_curGC.dynamicDisplay( 0 );

            // Update Status Line
            // Mejora: La pestaña tiene un campo de configuración  
            // donde se puede mostrar el mensaje de retorno en una ventana
            // al contrario de colocarla en el status bar
            if(!m_curTab.isShowDialogProcessMsg()){
            	setStatusLine( pi.getSummary(),pi.isError());
            }
            else{
            	setDialogMsg(pi.getSummary(), Msg.getMsg(Env.getCtx(), "DoubleClickMoreInformation"), pi.isError());
            	statusBar.showStatusLineDialog();
            }

            // Get Log Info

            ProcessInfoUtil.setLogFromDB( pi );

            String log = pi.getLogInfo();

            if( log.length() > 0 ) {
                ADialog.info( m_curWindowNo,this,Env.getHeader( m_ctx,m_curWindowNo ),pi.getTitle(),log );    // clear text
            }
        }
    }    // unlockUI

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUILocked() {
        return m_isLocked;
    }    // isLoacked

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void executeASync( ProcessInfo pi ) {
        log.config( "-" );
    }    // executeASync

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MTab getCurrentTab() {
        return m_curTab;
    }    // getCurrentTab

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        String s = "APanel[curWindowNo=" + m_curWindowNo;

        if( m_mWorkbench != null ) {
            s += ",WB=" + m_mWorkbench.toString();
        }

        s += "]";

        return s;
    }    // toString
    
    // Habilita/Deshabilia las pestañas de la ventana
    private void updateTabsState() {
   		boolean tabEnabled;
    	// Se itera por cada pestaña...
   		for (int i = 0; i<m_curWinTab.getTabCount(); i++) {
    		// La pestaña se habilita solo si no hay una edición pendiente en la
   			// pestaña actual.
   			tabEnabled = !aSave.isEnabled() || i == m_curTabIndex;
   			m_curWinTab.setEnabledAt(i, tabEnabled);
   			// Se modifica el estilo del título para reflejar el estado de la pestaña.
   			String title = m_curWinTab.getTitleAt(i);
   			if (!tabEnabled)
   				title = title.replaceAll("color='BLACK'", "color='GRAY'"); 
   			else
   				title = title.replaceAll("color='GRAY'", "color='BLACK'");
   			m_curWinTab.setTitleAt(i, title);
   		}
    }
    
    /**
     * Para el boton OpenMatrix, realizar las acciones correspondientes
     * @param tableName
     */
    protected void processOpenMatrixButton(String tableName)
    {
    	// Guardó el registro?
    	if (m_curTab.needSave( true,false )) {
    		ADialog.error(m_curWindowNo, this, "SaveErrorRowNotFound");
    		return;
    	}
    	
    	// Instanciar según corresponda
    	CDialog dialog = null;
    	if (X_C_OrderLine.Table_Name.equalsIgnoreCase(tableName))
    		dialog = new VOrderMatrixDetail(m_curWindowNo, (Frame)getRootPane().getParent() );
    	if (X_M_InOutLine.Table_Name.equalsIgnoreCase(tableName))
    		dialog = new VInOutMatrixDetail(m_curWindowNo, (Frame)getRootPane().getParent() );

    	// Si el botón esta ubicado en cualquier otra tabla, no hacer nada 
    	if (dialog == null)
    		return;
    	
    	// Abrir el dialogo
    	AEnv.showCenterScreen( dialog );
    	m_curTab.dataRefreshAll();
    	m_curTab.processFieldChange(m_curTab.getField("M_AttributeSetInstance_ID"));
    	
    }
}    // APanel



/*
 *  @(#)APanel.java   02.07.07
 *
 *  Fin del fichero APanel.java
 *
 *  Versión 2.2
 *
 */
