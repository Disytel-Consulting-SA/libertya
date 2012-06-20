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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.plaf.CompierePLAFEditor;
import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextArea;
import org.compiere.swing.CToggleButton;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUser;
import org.openXpertya.print.CPrinter;
import org.openXpertya.util.CLogErrorBuffer;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class Preference extends CDialog implements ActionListener,ListSelectionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param WindowNo
     */

    public Preference( Frame frame,int WindowNo ) {
        super( frame,Msg.getMsg( Env.getCtx(),"Preference" ),true );
        log.config( "Preference" );

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Preference - " + ex.getMessage());
        }

        load();

        //

        StringBuffer sta = new StringBuffer( "#" );

        sta.append( Env.getCtx().size()).append( " - " ).append( Msg.translate( Env.getCtx(),"AD_Window_ID" )).append( "=" ).append( WindowNo );
        statusBar.setStatusLine( sta.toString());
        statusBar.setStatusDB( "" );
        AEnv.positionCenterWindow( frame,this );
    }    // Preference

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Preference.class );

    /** Descripción de Campos */

    private CPanel panel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout panelLayout = new BorderLayout();

    /** Descripción de Campos */

    private CTabbedPane tabPane = new CTabbedPane();

    /** Descripción de Campos */

    private CPanel customizePane = new CPanel();

    /** Descripción de Campos */

    private CPanel contextPane = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout customizeLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CCheckBox autoCommit = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox printPreview = new CCheckBox();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private BorderLayout icontextLayout = new BorderLayout();

    /** Descripción de Campos */

    private JList infoList = new JList();

    /** Descripción de Campos */

    private JScrollPane contextListScrollPane = new JScrollPane( infoList );

    /** Descripción de Campos */

    private CPanel contextSouthPanel = new CPanel();

    /** Descripción de Campos */

    private CTextArea contextHeader = new CTextArea( 4,15 );

    /** Descripción de Campos */

    private CTextArea contextDetail = new CTextArea( 4,35 );

    /** Descripción de Campos */

    private CTextArea infoArea = new CTextArea( 5,30 );

    /** Descripción de Campos */

    private BorderLayout contextSouthLayout = new BorderLayout();

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private ConfirmPanel confirm = new ConfirmPanel( true );

    /** Descripción de Campos */

    private CComboBox traceLevel = new CComboBox( CLogMgt.LEVELS );

    /** Descripción de Campos */

    private CLabel traceLabel = new CLabel();

    /** Descripción de Campos */

    private CCheckBox traceFile = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox autoLogin = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox OXPSYS = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox storePassword = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox showTrl = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox showAcct = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox showAdvanced = new CCheckBox();

    /** Descripción de Campos */

    private CButton uiTheme = new CButton();

    /** Descripción de Campos */

    private CLabel lPrinter = new CLabel();

    /** Descripción de Campos */

    private CPrinter fPrinter = new CPrinter();

    /** Descripción de Campos */

    private CLabel lDate = new CLabel();

    /** Descripción de Campos */

    private VDate fDate = new VDate();

    /** Descripción de Campos */

    private CCheckBox serverObjects = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox serverProcess = new CCheckBox();

    /** Descripción de Campos */

    private CPanel errorPane = new CPanel();

    /** Descripción de Campos */

    private BorderLayout errorLayout = new BorderLayout();

    /** Descripción de Campos */

    private JScrollPane errorScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable errorTable = new MiniTable();

    /** Descripción de Campos */

    private CPanel errorPanel = new CPanel( new FlowLayout( FlowLayout.TRAILING ));

    /** Descripción de Campos */

    private CToggleButton bErrorsOnly = new CToggleButton( Msg.getMsg( Env.getCtx(),"ErrorsOnly" ));

    /** Descripción de Campos */

    private CButton bErrorReset = new CButton( Msg.getMsg( Env.getCtx(),"Reset" ));

    /** Descripción de Campos */

    private CButton bErrorEMail = new CButton( Msg.getMsg( Env.getCtx(),"SendEMail" ));

    /** Descripción de Campos */

    private CButton bErrorSave = new CButton( Msg.getMsg( Env.getCtx(),"SaveFile" ));

    /** Descripción de Campos */

    private CButton bRoleInfo = new CButton( Msg.translate( Env.getCtx(),"AD_Role_ID" ));
    
    /** Dialogo para edición de datos del usuario */
    private CButton bUserData = new CButton();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        traceLabel.setRequestFocusEnabled( false );
        traceLabel.setText( Msg.getMsg( Env.getCtx(),"TraceLevel",true ));
        traceLabel.setToolTipText( Msg.getMsg( Env.getCtx(),"TraceLevel",false ));
        traceFile.setText( Msg.getMsg( Env.getCtx(),"TraceFile",true ));
        traceFile.setToolTipText( Msg.getMsg( Env.getCtx(),"TraceFile",false ));
        uiTheme.setText( Msg.getMsg( Env.getCtx(),"UITheme",true ));
        uiTheme.setToolTipText( Msg.getMsg( Env.getCtx(),"UITheme",false ));
        autoCommit.setText( Msg.getMsg( Env.getCtx(),"AutoCommit",true ));
        autoCommit.setToolTipText( Msg.getMsg( Env.getCtx(),"AutoCommit",false ));
        OXPSYS.setText( Msg.getMsg( Env.getCtx(),"OXPSYS",true ));
        OXPSYS.setToolTipText( Msg.getMsg( Env.getCtx(),"OXPSYS",false ));
        printPreview.setText( Msg.getMsg( Env.getCtx(),"AlwaysPrintPreview",true ));
        printPreview.setToolTipText( Msg.getMsg( Env.getCtx(),"AlwaysPrintPreview",false ));
        autoLogin.setText( Msg.getMsg( Env.getCtx(),"AutoLogin",true ));
        autoLogin.setToolTipText( Msg.getMsg( Env.getCtx(),"AutoLogin",false ));
        storePassword.setText( Msg.getMsg( Env.getCtx(),"StorePassword",true ));
        storePassword.setToolTipText( Msg.getMsg( Env.getCtx(),"StorePassword",false ));
        showTrl.setText( Msg.getMsg( Env.getCtx(),"ShowTrlTab",true ));
        showTrl.setToolTipText( Msg.getMsg( Env.getCtx(),"ShowTrlTab",false ));
        showAcct.setText( Msg.getMsg( Env.getCtx(),"ShowAcctTab",true ));
        showAcct.setToolTipText( Msg.getMsg( Env.getCtx(),"ShowAcctTab",false ));
        showAdvanced.setText( Msg.getMsg( Env.getCtx(),"ShowAdvancedTab",true ));
        showAdvanced.setToolTipText( Msg.getMsg( Env.getCtx(),"ShowAdvancedTab",false ));
        serverObjects.setText( Msg.getMsg( Env.getCtx(),"ServerObjects",true ));
        serverObjects.setToolTipText( Msg.getMsg( Env.getCtx(),"ServerObjects",false ));
        serverProcess.setText( Msg.getMsg( Env.getCtx(),"ServerProcess",true ));
        serverProcess.setToolTipText( Msg.getMsg( Env.getCtx(),"ServerProcess",false ));
        lPrinter.setText( Msg.getMsg( Env.getCtx(),"Printer" ));
        lDate.setText( Msg.getMsg( Env.getCtx(),"Date" ));
        infoArea.setReadWrite( false );
        
        bUserData.setText( Msg.getMsg( Env.getCtx(),"ChangePassword",true ));
        bUserData.setToolTipText( Msg.getMsg( Env.getCtx(),"ChangeUserPassword",false ));
        CPanel layPanel = new CPanel();
        layPanel.add(bUserData);
        layPanel.add(bRoleInfo);
        
        getContentPane().add( panel );
        panel.setLayout( panelLayout );
        panel.add( tabPane,BorderLayout.CENTER );

        // Customize
//              tabPane.add(customizePane,  Msg.getMsg(Env.getCtx(), "Preference"));

        tabPane.add( customizePane,Msg.getMsg( Env.getCtx(),"Preference" ));
        customizePane.setLayout( customizeLayout );
        customizePane.add( infoArea,new GridBagConstraints( 0,0,3,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( uiTheme,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( layPanel,new GridBagConstraints( 2,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( autoCommit,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( OXPSYS,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( autoLogin,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( storePassword,new GridBagConstraints( 2,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( showAcct,new GridBagConstraints( 1,4,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( showTrl,new GridBagConstraints( 2,4,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( showAdvanced,new GridBagConstraints( 1,5,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( serverObjects,new GridBagConstraints( 1,6,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( serverProcess,new GridBagConstraints( 2,6,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        
        customizePane.add( traceLabel,new GridBagConstraints( 0,7,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        
        customizePane.add( traceLevel,new GridBagConstraints( 1,7,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( traceFile,new GridBagConstraints( 2,7,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        
        customizePane.add( lPrinter,new GridBagConstraints( 0,8,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        
        customizePane.add( fPrinter,new GridBagConstraints( 1,8,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        
        customizePane.add( lDate,new GridBagConstraints( 0,9,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        
        customizePane.add( fDate,new GridBagConstraints( 1,9,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        customizePane.add( printPreview,new GridBagConstraints( 2,9,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));

        // Info
//              tabPane.add(contextPane,  Msg.getMsg(Env.getCtx(), "Context"));

        tabPane.add( contextPane,Msg.getMsg( Env.getCtx(),"Context" ));
        contextPane.setLayout( icontextLayout );
        contextPane.add( contextListScrollPane,BorderLayout.CENTER );
        contextListScrollPane.setPreferredSize( new Dimension( 200,300 ));
        infoList.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        infoList.setBackground( CompierePLAF.getFieldBackground_Inactive());
        infoList.addListSelectionListener( this );
        infoList.setFixedCellWidth( 30 );
        contextPane.add( contextSouthPanel,BorderLayout.SOUTH );
        contextSouthPanel.setLayout( contextSouthLayout );
        contextSouthPanel.add( contextHeader,BorderLayout.WEST );
        contextHeader.setBackground( SystemColor.info );
        contextHeader.setReadWrite( false );
        contextHeader.setLineWrap( true );
        contextHeader.setWrapStyleWord( true );
        contextHeader.setBorder( BorderFactory.createLoweredBevelBorder());
        contextSouthPanel.add( contextDetail,BorderLayout.CENTER );
        contextDetail.setBackground( SystemColor.info );
        contextDetail.setReadWrite( false );
        contextDetail.setLineWrap( true );
        contextDetail.setWrapStyleWord( true );
        contextDetail.setBorder( BorderFactory.createLoweredBevelBorder());

        // Error Pane

        errorPane.setLayout( errorLayout );

//              tabPane.add(errorPane,  Msg.getMsg(Env.getCtx(), "Errors"));

        tabPane.add( errorPane,"Errors" );
        errorPane.add( errorScrollPane,BorderLayout.CENTER );
        errorScrollPane.getViewport().add( errorTable,null );

        //

        errorPanel.add( bErrorsOnly );
        errorPanel.add( bErrorReset );
        errorPanel.add( bErrorEMail );
        errorPanel.add( bErrorSave );
        errorPane.add( errorPanel,BorderLayout.SOUTH );

        // South

        panel.add( southPanel,BorderLayout.SOUTH );
        southPanel.setLayout( southLayout );
        southPanel.add( statusBar,BorderLayout.SOUTH );
        southPanel.add( confirm,BorderLayout.CENTER );

        //

        bRoleInfo.addActionListener( this );
        confirm.addActionListener( this );
        bUserData.addActionListener(this);
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void valueChanged( ListSelectionEvent e ) {
        if( e.getValueIsAdjusting()) {
            return;
        }

        String value = ( String )infoList.getSelectedValue();

        if( value == null ) {
            return;
        }

        int pos = value.indexOf( "==" );

        if( pos == -1 ) {
            contextHeader.setText( "" );
            contextDetail.setText( value );
        } else {
            contextHeader.setText( value.substring( 0,pos ).replace( '|','\n' ));
            contextDetail.setText( value.substring( pos + 3 ));
        }
    }    // valueChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // UI Change

        if( e.getSource() == uiTheme ) {
            new CompierePLAFEditor( this,false );
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            cmd_save();

            //

        } else if( e.getSource() == bErrorsOnly ) {
            cmd_displayErrors();
        } else if( e.getSource() == bErrorReset ) {
            cmd_errorReset();
        } else if( e.getSource() == bErrorEMail ) {
            cmd_errorEMail();
        } else if( e.getSource() == bErrorSave ) {
            cmd_errorSave();

            //

        } else if( e.getSource() == bRoleInfo ) {
            ADialog.info( 0,this,"RoleInfo",MRole.getDefault().toStringX( Env.getCtx()));
        } else if( e.getSource() == bUserData ) {
        	UserDataChange dialog = new UserDataChange();
        	dialog.setModal(true);
        	AEnv.showCenterScreen(dialog);
        }

    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void load() {
        log.config( "Preference.load" );
        infoArea.setText( CLogMgt.getInfo( null ).toString());
        infoArea.setCaretPosition( 0 );

        // --      Load Settings   --
        // UI

        uiTheme.addActionListener( this );

        // AutoCommit

        autoCommit.setSelected( Env.isAutoCommit( Env.getCtx()));

        // OXPSYS

        OXPSYS.setSelected( Ini.getPropertyBool( Ini.P_OXPSYS ));

        if( Env.getAD_Client_ID( Env.getCtx()) > 20 ) {
            OXPSYS.setSelected( false );
            OXPSYS.setEnabled( false );
        }

        // AutoLogin

        autoLogin.setSelected( Ini.getPropertyBool( Ini.P_A_LOGIN ));

        // Save Password

        storePassword.setSelected( Ini.getPropertyBool( Ini.P_STORE_PWD ));

        // Show Acct Tab

        if( MRole.getDefault().isShowAcct()) {
            showAcct.setSelected( Ini.getPropertyBool( Ini.P_SHOW_ACCT ));
        } else {
            showAcct.setSelected( false );
            showAcct.setReadWrite( false );
        }

        // Show Trl/Advanced Tab

        showTrl.setSelected( Ini.getPropertyBool( Ini.P_SHOW_TRL ));
        showAdvanced.setSelected( Ini.getPropertyBool( Ini.P_SHOW_ADVANCED ));

        // Server Objects/Process

        serverObjects.setSelected( Ini.getPropertyBool( Ini.P_OBJECTS ));
        serverProcess.setSelected( Ini.getPropertyBool( Ini.P_PROCESS ));

        // Print Preview

        printPreview.setSelected( Ini.getPropertyBool( Ini.P_PRINTPREVIEW ));
        Env.setContext( Env.getCtx(),"#"+Ini.P_PRINTPREVIEW,( printPreview.isSelected()));
        
        // TraceLevel

        traceLevel.setSelectedItem( CLogMgt.getLevel());
        traceFile.setSelected( Ini.getPropertyBool( Ini.P_TRACEFILE ));

        // Printer

        fPrinter.setValue( Env.getContext( Env.getCtx(),"#Printer" ));

        // Date

        fDate.setValue( Env.getContextAsDate( Env.getCtx(),"#Date" ));
        // Disytel - FB - 2010-12-23
		// Ya no se puede editar la fecha. La administra el Env y la mantiene
		// sincronizada con la fecha del servidor de BD.
        fDate.setReadWrite(false);
        // --      Load and sort Context   --

        String[] context = Env.getEntireContext( Env.getCtx());

        Arrays.sort( context );
        infoList.setListData( context );

        // Load Errors
        // CLogMgt mgt = new CLogMgt();            //      creates test trace

        bErrorsOnly.setSelected( true );
        errorTable.setCellSelectionEnabled( true );
        cmd_displayErrors();

        // for (int i = 2; i < 6; i++)
        // errorTable.setColumnReadOnly(i, false);
        //

        bErrorsOnly.addActionListener( this );
        bErrorReset.addActionListener( this );
        bErrorSave.addActionListener( this );
        bErrorEMail.addActionListener( this );
    }    // load

    /**
     * Descripción de Método
     *
     */

    private void cmd_save() {
        log.config( "Preference.cmd_save" );

        // UI
        // AutoCommit

        Ini.setProperty( Ini.P_A_COMMIT,( autoCommit.isSelected()));
        Env.setAutoCommit( Env.getCtx(),autoCommit.isSelected());

        // OXPSYS

        Ini.setProperty( Ini.P_OXPSYS,OXPSYS.isSelected());

        // AutoLogin

        Ini.setProperty( Ini.P_A_LOGIN,( autoLogin.isSelected()));

        // Save Password

        Ini.setProperty( Ini.P_STORE_PWD,( storePassword.isSelected()));

        // Show Acct Tab

        Ini.setProperty( Ini.P_SHOW_ACCT,( showAcct.isSelected()));
        Env.setContext( Env.getCtx(),"#ShowAcct",( showAcct.isSelected()));

        // Show Trl Tab

        Ini.setProperty( Ini.P_SHOW_TRL,( showTrl.isSelected()));
        Env.setContext( Env.getCtx(),"#ShowTrl",( showTrl.isSelected()));

        // Show Advanced Tab

        Ini.setProperty( Ini.P_SHOW_ADVANCED,( showAdvanced.isSelected()));
        Env.setContext( Env.getCtx(),"#ShowAdvanced",( showAdvanced.isSelected()));

        // Server Objects/Process

        Ini.setProperty( Ini.P_OBJECTS,( serverObjects.isSelected()));
        Ini.setProperty( Ini.P_PROCESS,( serverProcess.isSelected()));

        // Print Preview

        Ini.setProperty( Ini.P_PRINTPREVIEW,( printPreview.isSelected()));
        Env.setContext( Env.getCtx(),"#"+Ini.P_PRINTPREVIEW,( printPreview.isSelected()));

        // TraceLevel/File

        Level level = ( Level )traceLevel.getSelectedItem();

        CLogMgt.setLevel( level );
        Ini.setProperty( Ini.P_TRACELEVEL,level.getName());
        Ini.setProperty( Ini.P_TRACEFILE,traceFile.isSelected());

        // Printer

        String printer = ( String )fPrinter.getSelectedItem();

        Env.setContext( Env.getCtx(),"#Printer",printer );
        Ini.setProperty( Ini.P_PRINTER,printer );

        // Disytel - FB - 2010-12-23
        // Ahora la fecha actual #Date se administra internamente en el Env. 
        // El mismo se encarga de obtener la fecha del servidor de BD y setear
        // lavariable #Date con lo cual ya no se hace mas en el login
        // Además, la fecha no será modificable desde el login ni desde
        // la ventana de preferencias.
        /* -->
        // Date (remove seconds)

        java.sql.Timestamp ts = ( java.sql.Timestamp )fDate.getValue();

        if( ts != null ) {
            Env.setContext( Env.getCtx(),"#Date",ts );
        }
        */
        // <-- Fin Disytel - FB - 2010-12-23

        Ini.saveProperties( Ini.isClient());
        dispose();
    }    // cmd_save

    /**
     * Descripción de Método
     *
     */

    private void cmd_displayErrors() {
        Vector data = CLogErrorBuffer.get( true ).getLogData( bErrorsOnly.isSelected());
        Vector columnNames = CLogErrorBuffer.get( true ).getColumnNames( Env.getCtx());
        DefaultTableModel model = new DefaultTableModel( data,columnNames );

        errorTable.setModel( model );

        //

        if( bErrorsOnly.isSelected()) {
            tabPane.setTitleAt( 2,Msg.getMsg( Env.getCtx(),"Errors" ) + " (" + data.size() + ")" );
        } else {
            tabPane.setTitleAt( 2,Msg.getMsg( Env.getCtx(),"TraceInfo" ) + " (" + data.size() + ")" );
        }

        errorTable.autoSize();
    }    // cmd_errorsOnly

    /**
     * Descripción de Método
     *
     */

    private void cmd_errorReset() {
        CLogErrorBuffer.get( true ).resetBuffer( bErrorsOnly.isSelected());
        cmd_displayErrors();
    }    // cmd_errorReset

    /**
     * Descripción de Método
     *
     */

    private void cmd_errorEMail() {
        EMailDialog emd = new EMailDialog( this,"EMail Trace",MUser.get( Env.getCtx()),"",    // to
                                           "OpenXpertya Trace Info",CLogErrorBuffer.get( true ).getErrorInfo( Env.getCtx(),bErrorsOnly.isSelected()),null );
    }    // cmd_errorEMail

    /**
     * Descripción de Método
     *
     */

    private void cmd_errorSave() {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogType( JFileChooser.SAVE_DIALOG );
        chooser.setDialogTitle( "OpenXpertya Trace File" );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setSelectedFile( new File( "traceInfo.log" ));

        int returnVal = chooser.showSaveDialog( this );

        if( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        try {
            File       file   = chooser.getSelectedFile();
            FileWriter writer = new FileWriter( file );

            writer.write( CLogErrorBuffer.get( true ).getErrorInfo( Env.getCtx(),bErrorsOnly.isSelected()));
            writer.flush();
            writer.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }
    }    // cmd_errorSave
}    // Preference



/*
 *  @(#)Preference.java   02.07.07
 * 
 *  Fin del fichero Preference.java
 *  
 *  Versión 2.2
 *
 */
