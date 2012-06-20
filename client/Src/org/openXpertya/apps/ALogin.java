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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.db.CConnectionEditor;
import org.openXpertya.grid.ed.VComboBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MSystem;
import org.openXpertya.print.CPrinter;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Language;
import org.openXpertya.util.Login;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class ALogin extends JDialog implements ActionListener,ChangeListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public ALogin( Frame parent ) {
        super( parent,"Login",true );    // Modal
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        m_WindowNo = Env.createWindowNo( null );
        res        = ResourceBundle.getBundle( RESOURCE );

        //

        try {
            jbInit();
        } catch( Exception e ) {
            log.severe( e.toString());
        }

        // Focus

        this.setFocusTraversalPolicy( AFocusTraversalPolicy.get());
        this.getRootPane().setDefaultButton( confirmPanel.getOKButton());
        parent.setIconImage( OpenXpertya.getImage16());
    }    // ALogin

    /** Descripción de Campos */

    protected static final String RESOURCE = "org.openXpertya.apps.ALoginRes";

    /** Descripción de Campos */

    private static ResourceBundle res = ResourceBundle.getBundle( RESOURCE );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ALogin.class );

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel( new BorderLayout());

    /** Descripción de Campos */

    private CTabbedPane loginTabPane = new CTabbedPane();

    /** Descripción de Campos */


    /** Descripción de Campos */

    private CPanel connectionPanel = new CPanel();

    /** Descripción de Campos */

    private CLabel hostLabel = new CLabel();

    /** Descripción de Campos */

    private CConnectionEditor hostField = new CConnectionEditor();

    /** Descripción de Campos */

    private CLabel userLabel = new CLabel();

    /** Descripción de Campos */

    private JTextField userTextField = new JTextField();

    /** Descripción de Campos */

    private CLabel passwordLabel = new CLabel();

    /** Descripción de Campos */

    private JPasswordField passwordField = new JPasswordField();

    /** Descripción de Campos */

    private CPanel defaultPanel = new CPanel();

    /** Descripción de Campos */


    /** Descripción de Campos */

    private CLabel clientLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel orgLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel dateLabel = new CLabel();

    /** Descripción de Campos */

    private VDate dateField = new VDate( DisplayType.Date );

    /** Descripción de Campos */

    private VComboBox orgCombo = new VComboBox();

    /** Descripción de Campos */

    private VComboBox clientCombo = new VComboBox();

    /** Descripción de Campos */

    private CLabel warehouseLabel = new CLabel();

    /** Descripción de Campos */

    private VComboBox warehouseCombo = new VComboBox();

    /** Descripción de Campos */

    private CLabel printerLabel = new CLabel();

    /** Descripción de Campos */

    private CPrinter printerField = new CPrinter();

    /** Descripción de Campos */

    private CLabel roleLabel = new CLabel();

    /** Descripción de Campos */

    private VComboBox roleCombo = new VComboBox();

    /** Descripción de Campos */

    private CLabel copy0Label = new CLabel();

    /** Descripción de Campos */

    private CLabel titleLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel versionLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel copy1Label = new CLabel();

    /** Descripción de Campos */

    private GridBagLayout connectionLayout = new GridBagLayout();

    /** Descripción de Campos */

    private GridBagLayout defaultPanelLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel languageLabel = new CLabel();

    /** Descripción de Campos */

    private VComboBox languageCombo = new VComboBox( Language.getNames());

    /** Descripción de Campos */

    private CLabel compileDate = new CLabel();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true,false,false,false,false,false,false );

    /** Descripción de Campos */

    private OnlineHelp onlineHelp = new OnlineHelp( true );

    /** Descripción de Campos */

    private JPanel helpPanel = new JPanel();

    /** Descripción de Campos */

    private JScrollPane helpScollPane = new JScrollPane();

    /** Descripción de Campos */

    private BorderLayout helpLayout = new BorderLayout();

    /** Descripción de Campos */

    private CConnection m_cc;

    /** Descripción de Campos */

    private String m_user;

    /** Descripción de Campos */

    private String m_pwd;

    /** Descripción de Campos */

    private boolean m_comboActive = false;

    /** Descripción de Campos */

    private boolean m_okPressed = false;

    /** Descripción de Campos */

    private boolean m_connectionOK = false;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private Properties m_ctx = Env.getCtx();

    /** Descripción de Campos */

    private Login m_login = null;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setName( "Login" );
        CompiereColor.setBackground( this );
        titleLabel.setFont( new java.awt.Font( "Dialog",2,10 ));
        titleLabel.setRequestFocusEnabled( false );
        titleLabel.setToolTipText( OpenXpertya.getURL());
        titleLabel.setHorizontalTextPosition( SwingConstants.CENTER );
        titleLabel.setIcon( OpenXpertya.getImageIconLogo());
        titleLabel.setText( OpenXpertya.getSubtitle());
        titleLabel.setVerticalTextPosition( SwingConstants.BOTTOM );
        versionLabel.setRequestFocusEnabled( false );
        versionLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        versionLabel.setHorizontalTextPosition( SwingConstants.RIGHT );
        hostLabel.setRequestFocusEnabled( false );
        hostLabel.setLabelFor( hostField );
        hostField.addActionListener( this );
        userLabel.setRequestFocusEnabled( false );
        userLabel.setLabelFor( userTextField );
        passwordLabel.setRequestFocusEnabled( false );
        passwordLabel.setLabelFor( passwordField );
        languageLabel.setLabelFor( languageCombo );
        copy0Label.setFont( new java.awt.Font( "SansSerif",2,10 ));
        copy0Label.setForeground( Color.blue );
        copy0Label.setRequestFocusEnabled( false );
        copy1Label.setRequestFocusEnabled( false );
        roleLabel.setRequestFocusEnabled( false );
        roleLabel.setLabelFor( roleCombo );
        clientLabel.setRequestFocusEnabled( false );
        orgLabel.setRequestFocusEnabled( false );
        dateLabel.setRequestFocusEnabled( false );
        warehouseLabel.setRequestFocusEnabled( false );
        printerLabel.setRequestFocusEnabled( false );
        compileDate.setHorizontalAlignment( SwingConstants.RIGHT );
        compileDate.setHorizontalTextPosition( SwingConstants.RIGHT );
        compileDate.setText( OpenXpertya.DATE_VERSION );
        compileDate.setToolTipText( OpenXpertya.getImplementationVendor());
        southPanel.setLayout( southLayout );
        loginTabPane.addChangeListener( this );
        
        // Disytel - FB - 2010-12-23
		// Ya no se puede editar la fecha. La administra el Env y la mantiene
		// sincronizada con la fecha del servidor de BD.
        dateField.setReadWrite(false);

        // ConnectionTab

        connectionPanel.setLayout( connectionLayout );

        //

        titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
        versionLabel.setText( OpenXpertya.MAIN_VERSION );
        versionLabel.setToolTipText( OpenXpertya.getImplementationVersion());
        hostLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        hostLabel.setText( "Host" );
        connectionPanel.add( hostLabel,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        connectionPanel.add( hostField,new GridBagConstraints( 1,2,3,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,12 ),0,0 ));
        userLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        userLabel.setText( "User" );
        connectionPanel.add( userLabel,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        userTextField.setText( "System" );    // default
        connectionPanel.add( userTextField,new GridBagConstraints( 1,3,3,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,12 ),0,0 ));
        passwordLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        passwordLabel.setText( "Password" );
        connectionPanel.add( passwordLabel,new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        passwordField.setText( "System" );    // default
        connectionPanel.add( passwordField,new GridBagConstraints( 1,4,3,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,12 ),0,0 ));
        languageLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        languageLabel.setText( "Language" );
        connectionPanel.add( languageLabel,new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        languageCombo.addActionListener( this );
        connectionPanel.add( languageCombo,new GridBagConstraints( 1,5,3,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,12 ),0,0 ));
        copy0Label.setHorizontalAlignment( SwingConstants.RIGHT );
        connectionPanel.add( copy0Label,new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        copy1Label.setText( OpenXpertya.COPYRIGHT );
        connectionPanel.add( copy1Label,new GridBagConstraints( 1,6,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,12,12 ),0,0 ));
        connectionPanel.add( compileDate,new GridBagConstraints( 2,1,2,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 2,0,0,12 ),0,0 ));
        connectionPanel.add( titleLabel,new GridBagConstraints( 0,0,2,2,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 12,12,5,5 ),0,0 ));
        connectionPanel.add( versionLabel,new GridBagConstraints( 2,0,2,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 12,5,0,12 ),0,0 ));
        loginTabPane.add( connectionPanel,res.getString( "Connection" ));

        // DefaultTab

        defaultPanel.setLayout( defaultPanelLayout );

        //

        roleLabel.setText( "Role" );
        roleLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        roleCombo.addActionListener( this );
        defaultPanel.add( roleLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 12,12,5,5 ),0,0 ));
        defaultPanel.add( roleCombo,new GridBagConstraints( 1,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 12,0,5,12 ),0,0 ));
        clientLabel.setText( "Client" );
        clientLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        defaultPanel.add( clientLabel,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        clientCombo.addActionListener( this );
        defaultPanel.add( clientCombo,new GridBagConstraints( 1,1,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,12 ),0,0 ));
        orgLabel.setText( "Organization" );
        orgLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        defaultPanel.add( orgLabel,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        orgCombo.addActionListener( this );
        defaultPanel.add( orgCombo,new GridBagConstraints( 1,2,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,12 ),0,0 ));
        dateLabel.setText( "Date" );
        dateLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        defaultPanel.add( printerLabel,new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        defaultPanel.add( printerField,new GridBagConstraints( 1,4,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,12 ),0,0 ));

        //

        warehouseLabel.setText( "Warehouse" );
        warehouseLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        printerLabel.setText( "Printer" );
        printerLabel.setHorizontalAlignment( SwingConstants.RIGHT );
        defaultPanel.add( dateLabel,new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,12,5 ),0,0 ));
        defaultPanel.add( dateField,new GridBagConstraints( 1,5,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,12,12 ),0,0 ));
        defaultPanel.add( warehouseLabel,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        defaultPanel.add( warehouseCombo,new GridBagConstraints( 1,3,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,12 ),0,0 ));

        //

        loginTabPane.add( defaultPanel,res.getString( "Defaults" ));

        // Help

        helpPanel.setLayout( helpLayout );
        helpPanel.setPreferredSize( new Dimension( 500,100 ));
        helpPanel.add( helpScollPane,BorderLayout.CENTER );
        loginTabPane.add( helpPanel,"?" );

        //

        this.getContentPane().add( mainPanel );
        mainPanel.add( loginTabPane,BorderLayout.CENTER );
        mainPanel.setName( "loginMainPanel" );
        mainPanel.add( southPanel,BorderLayout.SOUTH );

        //

        southPanel.add( confirmPanel,BorderLayout.NORTH );
        southPanel.add( statusBar,BorderLayout.SOUTH );
        helpScollPane.getViewport().add( onlineHelp,null );
        confirmPanel.addActionListener( this );
        statusBar.setStatusDB( null );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean initLogin() {
        m_cc = CConnection.get();
        hostField.setValue( m_cc );
        validateConnection();

        // Application/PWD

        userTextField.setText( Ini.getProperty( Ini.P_UID ));

        if( Ini.getPropertyBool( Ini.P_STORE_PWD )) {
            passwordField.setText( Ini.getProperty( Ini.P_PWD ));
        } else {
            passwordField.setText( "" );
        }

        //

        languageCombo.setSelectedItem( Ini.getProperty( Ini.P_LANGUAGE ));
        
        // AutoLogin - assumes that connection is OK

        if( Ini.getPropertyBool( Ini.P_A_LOGIN )) {
            connectionOK();
            defaultsOK();
            
            if( m_connectionOK ) {    // simulate
                m_okPressed = true;
            }

            return m_connectionOK;
        }

        return false;
    }    // initLogin

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    protected void processWindowEvent( WindowEvent e ) {
        super.processWindowEvent( e );

        if( e.getID() == WindowEvent.WINDOW_OPENED ) {
            this.toFront();
            confirmPanel.getOKButton().requestFocusInWindow();
        }
    }    // processWindowEvent

    /**
     * Descripción de Método
     *
     */

    private void validateConnection() {
        m_connectionOK = false;

        //

        m_cc.testAppsServer();
        m_cc.testDatabase();

        //

        hostField.setDisplay();
        
        //
        
        updateLanguageCombo();
        
    }    // validateConnection

    private void updateLanguageCombo() {
    	Set<String> idiomas = new HashSet<String>();

    	for (String n : Language.getNames())
    		idiomas.add(Language.getAD_Language(n));
    	
    	languageCombo.removeAllItems();

    	if (m_cc.isDatabaseOK()) {
        	
        	Connection conn = m_cc.getConnection(true, Connection.TRANSACTION_READ_COMMITTED);
        	PreparedStatement ps = null;
        	ResultSet rs = null;
        	
        	//
        	
        	try {
        		ps = conn.prepareStatement(" SELECT AD_Language FROM AD_Language WHERE IsActive = 'Y' ");
        		rs = ps.executeQuery();
        		
        		idiomas.clear();
        		
        		idiomas.add(Language.getBaseAD_Language());
        		
        		while (rs.next())
        			idiomas.add(rs.getString(1));
        		
        		rs.close();
        		ps.close();
        	} catch (SQLException e) {
        		
        	}
        	
        	//
        	
        	try {
        		conn.close();
        	} catch (SQLException e) {
        		
        	}
        }
    	
    	for (String n : Language.getNames()) {
    		if (idiomas.contains(Language.getLanguage(n).getAD_Language()))
    			languageCombo.addItem(n);
    	}

    	languageCombo.setSelectedItem( Ini.getProperty( Ini.P_LANGUAGE ));
    	
    }
    
    /**
     * Descripción de Método
     *
     */

    private void appExit() {
        m_connectionOK = false;
        dispose();
    }    // appExit_actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isConnected() {
        return m_connectionOK;
    }    // isConnected

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOKpressed() {
        return m_okPressed;
    }    // isOKpressed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            if( loginTabPane.getSelectedIndex() == 0 ) {
                connectionOK();    // first ok
            } else {
                m_okPressed = true;
                defaultsOK();      // disposes
            }
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            appExit();

            //

        } else if( e.getSource() == hostField ) {
            validateConnection();
        } else if( e.getSource() == languageCombo ) {
            languageComboChanged();

            //

        } else if( e.getSource() == roleCombo ) {
            roleComboChanged();
        } else if( e.getSource() == clientCombo ) {
            clientComboChanged();
        } else if( e.getSource() == orgCombo ) {
            orgComboChanged();
        }
    }                              // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void connectionOK() {
        log.info( "" );

        //

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        confirmPanel.getOKButton().setEnabled( false );
        m_connectionOK = tryConnection();

        // Run After Migration

        if( m_connectionOK ) {
            MSystem system = MSystem.get( m_ctx );

            if( system.isJustMigrated()) {
                statusBar.setStatusLine( "Running: After Migration ....",true );
                ADialog.info( m_WindowNo,this,"AfterMigration" );
                Thread.yield();
                DB.afterMigration( m_ctx );
            }
        }

        // Switch to default Tab

        if( m_connectionOK ) {

            // Verify Language & Load Msg

            Language l = Language.getLoginLanguage();

            Env.verifyLanguage( m_ctx,l );
            Env.setContext( m_ctx,Env.LANGUAGE,l.getAD_Language());
            Msg.getMsg( m_ctx,"0" );

            // Set Defaults

            printerField.setValue( Ini.getProperty( Ini.P_PRINTER ));

            // Change Tab

            loginTabPane.setSelectedIndex( 1 );
        }

        confirmPanel.getOKButton().setEnabled( true );
        setCursor( Cursor.getDefaultCursor());
    }    // connectionOK

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        if( loginTabPane.getSelectedIndex() == 2 ) {    // allow access to help
            return;
        }

        if( !( String.valueOf( passwordField.getPassword()).equals( m_pwd ) && userTextField.getText().equals( m_user ))) {
            m_connectionOK = false;
        }

        //

        if( m_connectionOK ) {
            statusBar.setStatusLine( txt_LoggedIn );
        } else {
            statusBar.setStatusLine( txt_NotConnected,true );
            loginTabPane.setSelectedIndex( 0 );
        }

        confirmPanel.getOKButton().requestFocus();
    }    // loginTabPane

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean defaultsOK() {
        log.info( "" );

        KeyNamePair org = ( KeyNamePair )orgCombo.getSelectedItem();

        if( org == null ) {
            return false;
        }

        // Set Properties

        Ini.setProperty( Ini.P_CONNECTION,CConnection.get().toStringLong());
        Ini.setProperty( Ini.P_LANGUAGE,( String )languageCombo.getSelectedItem());

        CallResult result = m_login.validateLogin( org );

        if(result != null && result.isError()) {
        	if(result.isShowError()){
        		ADialog.info( m_WindowNo,this,result.getMsg() );
        	}
            appExit();

            return false;
        }

        // Load Properties and save Ini values

        statusBar.setStatusLine( "Loading Preferences" );

        String msg = m_login.loadPreferences( org,( KeyNamePair )warehouseCombo.getSelectedItem(),dateField.getTimestamp(),printerField.getDisplay());

        if( msg.length() > 0 ) {
            ADialog.info( m_WindowNo,this,msg );
        }

        // Check Apps Server - DB Checked in Menu

        checkVersion();    // exits if conflict

        // Close - we are done

        if( m_connectionOK ) {
            this.dispose();
        }

        return m_connectionOK;
    }    // defaultsOK

    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean tryConnection() {
        m_user = userTextField.getText();
        m_pwd  = new String( passwordField.getPassword());

        // Establish connection

        DB.setDBTarget( CConnection.get());

        if( !DB.isConnected()) {
            statusBar.setStatusLine( txt_NoDatabase,true );
            hostField.setBackground( CompierePLAF.getFieldBackground_Error());

            return false;
        }

        // Reference check

        Ini.setProperty( Ini.P_OXPSYS,"Reference".equalsIgnoreCase( CConnection.get().getDbUid()));

        // Get Roles

        m_login = new Login( m_ctx );

        KeyNamePair[] roles = m_login.getRoles( m_user,m_pwd );

        if( (roles == null) || (roles.length == 0) ) {
            statusBar.setStatusLine( txt_UserPwdError,true );
            userTextField.setBackground( CompierePLAF.getFieldBackground_Error());
            passwordField.setBackground( CompierePLAF.getFieldBackground_Error());

            return false;
        }

        // Delete existing role items

        m_comboActive = true;

        if( roleCombo.getItemCount() > 0 ) {
            roleCombo.removeAllItems();
        }

        // Initial role

        KeyNamePair iniValue   = null;
        String      iniDefault = Ini.getProperty( Ini.P_ROLE );

        // fill roles

        for( int i = 0;i < roles.length;i++ ) {
            roleCombo.addItem( roles[ i ] );

            if( roles[ i ].getName().equals( iniDefault )) {
                iniValue = roles[ i ];
            }
        }

        if( iniValue != null ) {
            roleCombo.setSelectedItem( iniValue );
        }

        userTextField.setBackground( CompierePLAF.getFieldBackground_Normal());
        passwordField.setBackground( CompierePLAF.getFieldBackground_Normal());

        //

        statusBar.setStatusLine( txt_LoggedIn );
        m_comboActive = false;
        roleComboChanged();

        return true;
    }    // tryConnection

    /**
     * Descripción de Método
     *
     */

    private void roleComboChanged() {
        KeyNamePair role = ( KeyNamePair )roleCombo.getSelectedItem();

        if( (role == null) || m_comboActive ) {
            return;
        }

        log.config( ": " + role );
        m_comboActive = true;

        //

        KeyNamePair[] clients = m_login.getClients( role );

        // delete existing client/org items

        if( clientCombo.getItemCount() > 0 ) {
            clientCombo.removeAllItems();
        }

        if( orgCombo.getItemCount() > 0 ) {
            orgCombo.removeAllItems();
        }

        // No Clients

        if( (clients == null) || (clients.length == 0) ) {
            statusBar.setStatusLine( txt_RoleError,true );
            m_comboActive = false;

            return;
        }

        // initial client

        KeyNamePair iniValue   = null;
        String      iniDefault = Ini.getProperty( Ini.P_CLIENT );

        // fill clients

        for( int i = 0;i < clients.length;i++ ) {
            clientCombo.addItem( clients[ i ] );

            if( clients[ i ].getName().equals( iniDefault )) {
                iniValue = clients[ i ];
            }
        }

        // fini

        if( iniValue != null ) {
            clientCombo.setSelectedItem( iniValue );
        }

        //

        m_comboActive = false;
        clientComboChanged();
    }    // roleComboChanged

    /**
     * Descripción de Método
     *
     */

    private void clientComboChanged() {
        KeyNamePair client = ( KeyNamePair )clientCombo.getSelectedItem();

        if( (client == null) || m_comboActive ) {
            return;
        }

        log.config( ": " + client );
        m_comboActive = true;

        //

        KeyNamePair[] orgs = m_login.getOrgs( client );

        // delete existing cleint items

        if( orgCombo.getItemCount() > 0 ) {
            orgCombo.removeAllItems();
        }

        // No Orgs

        if( (orgs == null) || (orgs.length == 0) ) {
            statusBar.setStatusLine( txt_RoleError,true );
            m_comboActive = false;

            return;
        }

        // initial client

        KeyNamePair orgValue   = null;
        KeyNamePair orgValue2  = null;
        String      iniDefault = Ini.getProperty( Ini.P_ORG );

        // fill orgs

        for( int i = 0;i < orgs.length;i++ ) {
            orgCombo.addItem( orgs[ i ] );

            if( orgs[ i ].getName().equals( iniDefault )) {
                orgValue = orgs[ i ];
            }

            if( (orgValue2 == null) && (orgs[ i ].getKey() != 0) ) {
                orgValue2 = orgs[ i ];    // first non-0 org
            }
        }

        // Non-0 Org exists and last login was with 0

        if( (orgValue2 != null) && (orgValue != null) && (orgValue.getKey() == 0) ) {
            orgValue = orgValue2;
        }

        // Last Org

        if( orgValue != null ) {
            orgCombo.setSelectedItem( orgValue );

            // Get first Org

        } else {
            orgValue = ( KeyNamePair )orgCombo.getSelectedItem();
        }

        //

        m_comboActive = false;
        orgComboChanged();
    }    // clientComboChanged

    /**
     * Descripción de Método
     *
     */

    private void orgComboChanged() {
        KeyNamePair org = ( KeyNamePair )orgCombo.getSelectedItem();

        if( (org == null) || m_comboActive ) {
            return;
        }

        log.config( ": " + org );
        m_comboActive = true;

        //

        KeyNamePair[] whs = m_login.getWarehouses( org );

        // Delete existing warehouse items

        if( warehouseCombo.getItemCount() > 0 ) {
            warehouseCombo.removeAllItems();
        }

        // fill warehouses

        if( whs != null ) {

            // initial warehouse

            KeyNamePair iniValue   = null;
            String      iniDefault = Ini.getProperty( Ini.P_WAREHOUSE );

            for( int i = 0;i < whs.length;i++ ) {
                warehouseCombo.addItem( whs[ i ] );

                if( whs[ i ].getName().equals( iniDefault )) {
                    iniValue = whs[ i ];
                }
            }

            if( iniValue != null ) {
                warehouseCombo.setSelectedItem( iniValue );
            }
        }

        m_comboActive = false;
    }    // orgComboChanged

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean checkVersion() {
        boolean retValue = false;

        try {
            String version = AEnv.getServerVersion();

            if( OpenXpertya.DATE_VERSION.equals( version )) {
                log.config( "Server = Client - " + version );
                retValue = true;
            } else if( version != null ) {
                StringBuffer msg = new StringBuffer( ">>\n" );

                msg.append( res.getString( "VersionConflict" )).append( "\n" ).append( res.getString( "VersionInfo" )).append( "\n" );
                msg.append( (version == null)
                            ?"null"
                            :version ).append( " <> " ).append( OpenXpertya.DATE_VERSION ).append( "\n" );
                msg.append( res.getString( "PleaseUpgrade" )).append( "\n<<" );
                JOptionPane.showMessageDialog( null,msg.toString(),OpenXpertya.getName() + " - " + res.getString( "VersionConflict" ),JOptionPane.ERROR_MESSAGE );
                AEnv.exit( 1 );
            }
        } catch( Exception e ) {
            log.severe( "Contact Server failed - " + e.getClass().toString() + ": " + e.getMessage());
        }

        return retValue;
    }    // checkVersion

    /** Descripción de Campos */

    private String txt_Connected,txt_NotConnected,txt_NoDatabase,txt_UserPwdError,txt_RoleError,txt_LoggedIn;

    /**
     * Descripción de Método
     *
     */

    private void languageComboChanged() {
        String langName = ( String )languageCombo.getSelectedItem();

        // log.info( "Language: " + langName);

        Language language = Language.getLanguage( langName );

        Language.setLoginLanguage( language );
        Env.setContext( m_ctx,Env.LANGUAGE,language.getAD_Language());

        // Locales

        Locale loc = language.getLocale();

        Locale.setDefault( loc );
        this.setLocale( loc );
        res = ResourceBundle.getBundle( RESOURCE,loc );

        //

        this.setTitle( res.getString( "Login" ));
        hostLabel.setText( res.getString( "Host" ));
        userLabel.setText( res.getString( "User" ));
        userLabel.setToolTipText( res.getString( "EnterUser" ));
        passwordLabel.setText( res.getString( "Password" ));
        passwordLabel.setToolTipText( res.getString( "EnterPassword" ));
        languageLabel.setText( res.getString( "Language" ));
        languageLabel.setToolTipText( res.getString( "SelectLanguage" ));

        //

        roleLabel.setText( res.getString( "Role" ));
        clientLabel.setText( res.getString( "Client" ));
        orgLabel.setText( res.getString( "Organization" ));
        dateLabel.setText( res.getString( "Date" ));
        warehouseLabel.setText( res.getString( "Warehouse" ));
        printerLabel.setText( res.getString( "Printer" ));
        defaultPanel.setToolTipText( res.getString( "Defaults" ));
        connectionPanel.setToolTipText( res.getString( "Connection" ));

        //

        txt_Connected    = res.getString( "Connected" );
        txt_NotConnected = res.getString( "NotConnected" );
        txt_NoDatabase   = res.getString( "DatabaseNotFound" );
        txt_UserPwdError = res.getString( "UserPwdError" );
        txt_RoleError    = res.getString( "RoleNotFound" );
        txt_LoggedIn     = res.getString( "Authorized" );

        //

        loginTabPane.setTitleAt( 0,res.getString( "Connection" ));
        loginTabPane.setTitleAt( 1,res.getString( "Defaults" ));
        confirmPanel.getOKButton().setToolTipText( res.getString( "Ok" ));
        confirmPanel.getCancelButton().setToolTipText( res.getString( "Cancel" ));

        // DateField with new format

        dateField.setFormat();
        //dateField.setValue( new Timestamp( System.currentTimeMillis()));
        dateField.setValue(Env.getDate());

        //

        if( m_connectionOK ) {
            statusBar.setStatusLine( txt_LoggedIn );
        } else {
            statusBar.setStatusLine( txt_NotConnected,true );
        }
    }    // languageCombo_actionPerformed
}    // ALogin



/*
 *  @(#)ALogin.java   02.07.07
 * 
 *  Fin del fichero ALogin.java
 *  
 *  Versión 2.2
 *
 */