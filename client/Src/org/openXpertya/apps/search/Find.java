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



package org.openXpertya.apps.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTable;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VEditor;
import org.openXpertya.grid.ed.VEditorFactory;
import org.openXpertya.model.DataStatusEvent;
import org.openXpertya.model.DataStatusListener;
import org.openXpertya.model.MColumnAccess;
import org.openXpertya.model.MField;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.M_Column;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;
import org.openXpertya.model.X_AD_Search;



/**
 * Descripción de Clase
 *
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public final class Find extends CDialog implements ActionListener,ChangeListener,DataStatusListener {

	/**
	 * Nombre de la preference que permite agregar comodin al final de los
	 * campos Value, Name, Documento y Description
	 */
	
	private static final String ADD_END_WILDCARD_VALUE_PREFERENCE_NAME = "FindAddEndWildcard_Value";
	private static final String ADD_END_WILDCARD_NAME_PREFERENCE_NAME = "FindAddEndWildcard_Name";
	private static final String ADD_END_WILDCARD_DOCUMENTNO_PREFERENCE_NAME = "FindAddEndWildcard_Documentno";
	private static final String ADD_END_WILDCARD_DESCRIPTION_PREFERENCE_NAME = "FindAddEndWildcard_Description";
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param targetWindowNo
     * @param title
     * @param AD_Table_ID
     * @param tableName
     * @param whereExtended
     * @param findFields
     * @param minRecords
     */

    public Find( Frame owner,int targetWindowNo,String title,int AD_Table_ID,String tableName,String whereExtended,MField[] findFields,int minRecords ) {

        super( owner,Msg.getMsg( Env.getCtx(),"Find" ) + ": " + title,true );
        log.info( title );

        //

        m_targetWindowNo = targetWindowNo;
        m_AD_Table_ID    = AD_Table_ID;
        m_tableName      = tableName;
        m_whereExtended  = whereExtended;
        m_findFields     = findFields;

        //

        m_query = new MQuery( tableName );
        m_query.addRestriction( whereExtended );

        // Required for Column Validation

        Env.setContext( Env.getCtx(),m_targetWindowNo,"Find_Table_ID",m_AD_Table_ID );

        // Context for Advanced Search Grid is WINDOW_FIND

        Env.setContext( Env.getCtx(),Env.WINDOW_FIND,"Find_Table_ID",m_AD_Table_ID );

        //

        try {
            jbInit();

            initFind();

            if( m_total < minRecords ) {
                dispose();

                return;
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Find",e );
        }

        //

        this.getRootPane().setDefaultButton( confirmPanelS.getOKButton());
        AEnv.showCenterWindow(owner, this);
    }    // Find

    /** Descripción de Campos */

    private int m_targetWindowNo;

    /** Descripción de Campos */

    private int m_AD_Table_ID;

    /** Descripción de Campos */

    private String m_tableName;

    /** Descripción de Campos */

    private String m_whereExtended;

    /** Descripción de Campos */

    private MField[] m_findFields;

    /** Descripción de Campos */

    private MQuery m_query = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Find.class );

    /** Descripción de Campos */

    private int m_total;

    /** Descripción de Campos */

    private PreparedStatement m_pstmt;

    //

    /** Descripción de Campos */

    private boolean hasValue = false;

    /** Descripción de Campos */

    private boolean hasDocNo = false;

    /** Descripción de Campos */

    private boolean hasName = false;

    /** Descripción de Campos */

    private boolean hasDescription = false;

    /** Descripción de Campos */

    private int m_sLine = 6;

    /** Descripción de Campos */

    private ArrayList m_sEditors = new ArrayList();

    /** Descripción de Campos */

    private Hashtable m_targetFields = new Hashtable();

    /** Descripción de Campos */

    public static final int TABNO = 99;

    /** Descripción de Campos */

    public static final int FIELDLENGTH = 20;

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();

    /** Descripción de Campos */

    private CPanel advancedPanel = new CPanel();

    private BorderLayout advancedLayout = new BorderLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelA = new ConfirmPanel( true,true,false,false,false,false,true );

    /** Descripción de Campos */

    private CButton bIgnore = new CButton();

    /** Descripción de Campos */

    private JToolBar toolBar = new JToolBar();
    private CButton NewFindParameters = new CButton();
    private CLabel advSearch = new CLabel();
    private CComboBox advCName = new CComboBox();
    private CTextField advTName = new CTextField();
     /** Descripción de Campos */

    private CButton bSave = new CButton();

    /** Descripción de Campos */

    private CButton AdvDelete = new CButton();
    /** Descripción de Campos */

    private CButton bNew = new CButton();

    /** Descripción de Campos */

    private CButton bDelete = new CButton();

    /** Descripción de Campos */

    //private GridLayout gridLayout1 = new GridLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelS = new ConfirmPanel( true );

    /** Descripción de Campos */

    private BorderLayout simpleLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel scontentPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout scontentLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CPanel simplePanel = new CPanel();

    /** Descripción de Campos */

    private CLabel valueLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel nameLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel descriptionLabel = new CLabel();

    /** Descripción de Campos */

    private CTextField valueField = new CTextField();

    /** Descripción de Campos */

    private CTextField nameField = new CTextField();

    /** Descripción de Campos */

    private CTextField descriptionField = new CTextField();

    /** Descripción de Campos */

    private CLabel docNoLabel = new CLabel();

    /** Descripción de Campos */

    private CTextField docNoField = new CTextField();

    /** Descripción de Campos */

    private Component spaceE;

    /** Descripción de Campos */

    private Component spaceN;

    /** Descripción de Campos */

    private Component spaceW;

    /** Descripción de Campos */

    private Component spaceS;

    /** Descripción de Campos */

    private JScrollPane advancedScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private CTable advancedTable = new CTable();

    /** Descripción de Campos */

    public static final int INDEX_COLUMNNAME = 0;

    /** Descripción de Campos */

    public static final int INDEX_OPERATOR = 1;

    /** Descripción de Campos */

    public static final int INDEX_VALUE = 2;

    /** Descripción de Campos */

    public static final int INDEX_VALUE2 = 3;

    /** Descripción de Campos */

    public CComboBox columns = null;

    /** Descripción de Campos */

    public CComboBox operators = null;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {

        spaceE = Box.createHorizontalStrut( 8 );
        spaceN = Box.createVerticalStrut( 8 );
        spaceW = Box.createHorizontalStrut( 8 );
        spaceS = Box.createVerticalStrut( 8 );

        bIgnore.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/Ignore24.gif" )));
        bIgnore.setMargin( new Insets( 2,2,2,2 ));
        bIgnore.setToolTipText( Msg.getMsg( Env.getCtx(),"Ignore" ));
        bIgnore.addActionListener( this );

        // Begin Bug 111 Zarius - Dataware - add new button
        NewFindParameters.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/NewFind24.gif" )));
        NewFindParameters.setMargin( new Insets( 2,2,2,2 ));
        NewFindParameters.setToolTipText( "Usar busquedas predefinidas" );
        NewFindParameters.addActionListener( this );
        //End Bug 111 Zarius - Dataware -


        bNew.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/New24.gif" )));
        bNew.setMargin( new Insets( 2,2,2,2 ));
        bNew.setToolTipText( Msg.getMsg( Env.getCtx(),"New" ));
        bNew.addActionListener( this );

        bDelete.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/Delete24.gif" )));
        bDelete.setMargin( new Insets( 2,2,2,2 ));
        bDelete.setToolTipText( Msg.getMsg( Env.getCtx(),"Delete" ));
        bDelete.addActionListener( this );

        bSave.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/SaveFind24.gif" )));
        bSave.setMargin( new Insets( 2,2,2,2 ));
        bSave.setToolTipText(Msg.getMsg( Env.getCtx(),"Update the selected search" ));
        bSave.addActionListener( this );
        //AdvDelete
        AdvDelete.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/DeleteFind24.gif" )));
        AdvDelete.setMargin( new Insets( 2,2,2,2 ));
        AdvDelete.setToolTipText(Msg.getMsg( Env.getCtx(),"Delete the selected search" ));
        AdvDelete.addActionListener( this );


        southPanel.setLayout( southLayout );
        valueLabel.setLabelFor( valueField );
        valueLabel.setText(Msg.translate( Env.getCtx(),"Value" ));
        nameLabel.setLabelFor( nameField );
        nameLabel.setText( Msg.translate( Env.getCtx(),"Name" ));
        descriptionLabel.setLabelFor( descriptionField );
        descriptionLabel.setText( Msg.translate( Env.getCtx(),"Description" ));
        //Begin Bug 111 Zarius - Dataware - add text and combobox
        advTName.setText("");
        advCName.setVisible(true);
        advCName.addActionListener( this );
        advTName.setVisible(false);
        //End Bug 111 Zarius - Dataware -
        valueField.setText( "%" );
        valueField.setColumns( FIELDLENGTH );

        nameField.setText( "%" );
        nameField.setColumns( FIELDLENGTH );

        //Begin Bug 111 Zarius - Dataware - add label
        advSearch.setText( Msg.translate( Env.getCtx(),"Name" ));
        //Begin Bug 111 Zarius - Dataware -

        descriptionField.setText( "%" );
        descriptionField.setColumns( FIELDLENGTH );

        scontentPanel.setToolTipText( Msg.getMsg( Env.getCtx(),"FindTip" ));
        docNoLabel.setLabelFor( docNoField );

        docNoLabel.setText( Msg.translate( Env.getCtx(),"DocumentNo" ));

        docNoField.setText( "%" );
        docNoField.setColumns( FIELDLENGTH );

        advancedScrollPane.setPreferredSize( new Dimension( 450,150 ));
        southPanel.add( statusBar,BorderLayout.SOUTH );

        this.getContentPane().add( southPanel,BorderLayout.SOUTH );

        //

        scontentPanel.setLayout( scontentLayout );

        simplePanel.setLayout( simpleLayout );
        simplePanel.add( confirmPanelS,BorderLayout.SOUTH );
        simplePanel.add( scontentPanel,BorderLayout.CENTER );

        scontentPanel.add( valueLabel,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 7,5,0,5 ),0,0 ));
        scontentPanel.add( nameLabel,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 7,5,0,5 ),0,0 ));
        scontentPanel.add( descriptionLabel,new GridBagConstraints( 1,4,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 7,5,5,5 ),0,0 ));
        scontentPanel.add( valueField,new GridBagConstraints( 2,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        scontentPanel.add( descriptionField,new GridBagConstraints( 2,4,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        scontentPanel.add( docNoLabel,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 7,5,0,5 ),0,0 ));
        scontentPanel.add( nameField,new GridBagConstraints( 2,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));
        scontentPanel.add( docNoField,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,0,5 ),0,0 ));

        //

        scontentPanel.add( spaceE,new GridBagConstraints( 3,3,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 10,10,10,10 ),0,0 ));
        scontentPanel.add( spaceN,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 10,10,10,10 ),0,0 ));
        scontentPanel.add( spaceW,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 10,10,10,10 ),0,0 ));
        scontentPanel.add( spaceS,new GridBagConstraints( 2,15,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 10,10,10,10 ),0,0 ));

        tabbedPane.add( simplePanel,Msg.getMsg( Env.getCtx(),"Find" ));

        Dimension MyDim= new Dimension();
        MyDim.height=20;
        MyDim.width=200;
        advTName.setMaximumSize(MyDim);
        advCName.setMaximumSize(MyDim);

        toolBar.add( bIgnore,null );
        toolBar.addSeparator();
        toolBar.add( bNew,null );
        toolBar.add( bDelete,null );
        toolBar.addSeparator();

        //Begin Bug 111 Zarius - Dataware - add new components to toolbar
        toolBar.add(NewFindParameters, null);
        toolBar.add( AdvDelete,null );
        toolBar.add( bSave,null );

        toolBar.addSeparator();
        toolBar.add(advSearch, null);
        toolBar.addSeparator();
        toolBar.add(advTName, null);
        toolBar.add(advCName, null);
        //End Bug 111 Zarius - Dataware -

        advancedPanel.setLayout( advancedLayout );
        advancedPanel.add( toolBar,BorderLayout.NORTH );

        advancedPanel.add( confirmPanelA,BorderLayout.SOUTH );
        advancedPanel.add( advancedScrollPane,BorderLayout.CENTER );
        advancedScrollPane.getViewport().add( advancedTable,null );

        tabbedPane.add( advancedPanel,Msg.getMsg( Env.getCtx(),"Advanced" ));

        this.getContentPane().add( tabbedPane,BorderLayout.CENTER );

        confirmPanelA.addActionListener( this );
        confirmPanelS.addActionListener( this );

        JButton b = ConfirmPanel.createNewButton( true );

        confirmPanelS.addComponent( b );
        b.addActionListener( this );

    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void initFind() {
        log.config( "" );

        // Get Info from target Tab

        for( int i = 0;i < m_findFields.length;i++ ) {
            MField mField     = m_findFields[ i ];
            String columnName = mField.getColumnName();

            if( columnName.equals( "Value" )) {
                hasValue = true;
            } else if( columnName.equals( "Name" )) {
                hasName = true;
            } else if( columnName.equals( "DocumentNo" )) {
                hasDocNo = true;
            } else if( columnName.equals( "Description" )) {
                hasDescription = true;
            } else if( mField.isSelectionColumn()) {
                addSelectionColumn( mField );
            }
            /* ---------------------------------------------------------------
             * Franco Bonafine - Disytel
             * Esta rama hace que se muestren todas las columnas denominadas
             * "%Name%", lo cual es erróneo ya que puede haber casos en que las 
             * columnas con este formato de nombre no se quieran presentar en
             * la ventana. Para esto, se debe configurar la columna como
             * IsSelectionColumn en sus metadatos.
             * Por el momento se deshabilita esta opción de entrada masiva.
             
            } else if( columnName.indexOf( "Name" ) != -1 ) {
                 addSelectionColumn( mField );
            }
            
            *///--------------------------------------------------------------    

            // TargetFields

            m_targetFields.put( new Integer( mField.getAD_Column_ID()),mField );
        }    // for all target tab fields

        // Disable simple query fields

        valueLabel.setVisible( hasValue );
        valueField.setVisible( hasValue );

        if( hasValue ) {
            valueField.addActionListener( this );
        }

        docNoLabel.setVisible( hasDocNo );
        docNoField.setVisible( hasDocNo );

        if( hasDocNo ) {
            docNoField.addActionListener( this );
        }

        nameLabel.setVisible( hasName );
        nameField.setVisible( hasName );

        if( hasName ) {
            nameField.addActionListener( this );
        }

        descriptionLabel.setVisible( hasDescription );
        descriptionField.setVisible( hasDescription );

        if( hasDescription ) {
            descriptionField.addActionListener( this );
        }

        // Get Total

        //Cambios para que no cuente al mostrar el formulario
        // m_total = getNoOfRecords( null,false );
        m_total=4000;
        statusBar.setVisible(false);
         
         
        setStatusDB( m_total );
        statusBar.setStatusLine("");
        tabbedPane.addChangeListener( this );

        // Better Labels for OK/Cancel

        confirmPanelA.getOKButton().setToolTipText( Msg.getMsg( Env.getCtx(),"QueryEnter" ));
        confirmPanelA.getCancelButton().setToolTipText( Msg.getMsg( Env.getCtx(),"QueryCancel" ));
        confirmPanelS.getOKButton().setToolTipText( Msg.getMsg( Env.getCtx(),"QueryEnter" ));
        confirmPanelS.getCancelButton().setToolTipText( Msg.getMsg( Env.getCtx(),"QueryCancel" ));

    }    // initFind

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    private void addSelectionColumn( MField mField ) {
        log.config( mField.getHeader());

        int displayLength = mField.getDisplayLength();

        if( displayLength > FIELDLENGTH ) {
            mField.setDisplayLength( FIELDLENGTH );
        } else {
            displayLength = 0;
        }

        VEditor editor = VEditorFactory.getEditor( mField,false );

        editor.setMandatory( false );
        editor.setReadWrite( true );

        CLabel label = VEditorFactory.getLabel( mField );

        //

        if( displayLength > 0 ) {    // set it back
            mField.setDisplayLength( displayLength );
        }

        //

        m_sLine++;

        if( label != null ) {    // may be null for Y/N
            scontentPanel.add( label,new GridBagConstraints( 1,m_sLine,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 7,5,5,5 ),0,0 ));
        }

        scontentPanel.add(( Component )editor,new GridBagConstraints( 2,m_sLine,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        m_sEditors.add( editor );
    }    // addSelectionColumn

    /**
     * Descripción de Método
     *
     */

    private void initFindAdvanced() {
        log.config( "" );
        advancedTable.setModel( new DefaultTableModel( 0,4 ));
        advancedTable.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );

        // 0 = Columns

        ArrayList items = new ArrayList();

        for( int c = 0;c < m_findFields.length;c++ ) {
            MField field = m_findFields[ c ];

            if( !field.isKey()) {
                String columnName = field.getColumnName();
                String header     = field.getHeader();

                if( (header == null) || (header.length() == 0) ) {
                    header = Msg.translate( Env.getCtx(),columnName );

                    if( (header == null) || (header.length() == 0) ) {
                        continue;
                    }
                }

                ValueNamePair pp = new ValueNamePair( columnName,header );

                // System.out.println(pp + " = " + field);

                items.add( pp );
            }
        }

        ValueNamePair[] cols = new ValueNamePair[ items.size()];

        items.toArray( cols );
        Arrays.sort( cols );    // sort alpha
        columns = new CComboBox( cols );
        columns.addActionListener( this );

        TableColumn tc = advancedTable.getColumnModel().getColumn( INDEX_COLUMNNAME );

        tc.setPreferredWidth( 150 );
        tc.setCellEditor( new DefaultCellEditor( columns ));
        tc.setHeaderValue( Msg.translate( Env.getCtx(),"AD_Column_ID" ));

        // 1 = Operators

        operators = new CComboBox( MQuery.OPERATORS );
        operators.addActionListener(this);
        tc        = advancedTable.getColumnModel().getColumn( INDEX_OPERATOR );
        tc.setPreferredWidth( 40 );
        tc.setCellEditor( new DefaultCellEditor( operators ));
        tc.setHeaderValue( Msg.getMsg( Env.getCtx(),"Operator" ));

        // 2 = QueryValue

        tc = advancedTable.getColumnModel().getColumn( INDEX_VALUE );
        tc.setCellEditor( new FindValueEditor( this,false ));
        tc.setCellRenderer( new FindValueRenderer( this,false ));
        tc.setHeaderValue( Msg.getMsg( Env.getCtx(),"QueryValue" ));

        // 3 = QueryValue2

        tc = advancedTable.getColumnModel().getColumn( INDEX_VALUE2 );
        tc.setPreferredWidth( 50 );
        tc.setCellEditor( new FindValueEditor( this,true ));
        tc.setCellRenderer( new FindValueRenderer( this,true ));
        tc.setHeaderValue( Msg.getMsg( Env.getCtx(),"QueryValue2" ));

        // No Row - Create one
        AdvancedSearchCombo();
        cmd_new();
    }    // initFindAdvanced

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        log.config( "" );

        // Find SQL

        if( m_pstmt != null ) {
            try {
                m_pstmt.close();
            } catch( SQLException e ) {
            }
        }

        m_pstmt = null;

        // TargetFields

        if( m_targetFields != null ) {
            m_targetFields.clear();
        }

        m_targetFields = null;

        //

        removeAll();
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info("Click");
        //
        
        if( e.getActionCommand() == ConfirmPanel.A_CANCEL ) {
            cmd_cancel();
        } else if( e.getActionCommand() == ConfirmPanel.A_REFRESH ) {
            cmd_refresh();

            //

        } else if( e.getActionCommand() == ConfirmPanel.A_NEW ) {
            m_query = new MQuery( m_tableName );
            m_query.addRestriction( "1=2" );
            m_total = 0;
            dispose();
        }

        //

        else if( e.getSource() == bIgnore ) {
            cmd_ignore();
        } else if( e.getSource() == bNew ) {
            cmd_new();
        }
        //Begin Bug no 111 Added by Zarius Dataware 10/08/06 ---------------------------------------------------------------
        else if( e.getSource() == bSave )
        {
        	//if (advCName.getItemAt(advCName.getSelectedIndex())==null){
        	//	statusBar.setStatusLine("Seleccione un nombre");
        	//}else{
        		if (getfullgrid()==true)
        	        statusBar.setStatusLine(Msg.parseTranslation(Env.getCtx(), "@AtLeastACondition@"));
        		else
        		{
        			//New reg
        			if (advTName.isVisible()==true){

        				if (advTName.getText().length()>0){
        					statusBar.setStatusLine(AdvancedSearchSave());
		        			AdvancedSearchLoad();
		        			AdvancedSearchCombo();
        				}
        				else
        				{
        					statusBar.setStatusLine("'Nombre' es obligatorio");
        				}
        			}
        			//Update
        			else{
        				AdvancedSearchRemove();
        				statusBar.setStatusLine(AdvancedSearchSave());
        				statusBar.setStatusLine("'" + advCName.getItemAt(advCName.getSelectedIndex()).toString() + "' ha sido actualizado correctamente");
	        			AdvancedSearchLoad();
	        			AdvancedSearchCombo();
        			}
        		}
        	//}
        }
        else if( e.getSource() == NewFindParameters ) {

        	//First the Combo apears, if we need to create new push button
        	if (NewFindParameters.getToolTipText().equals("Usar busquedas predefinidas"))
        	{
        		AdvDelete.setVisible(false);
        		bSave.setToolTipText( "Save the advanced search" );
	            advTName.setVisible(true);
	            advCName.setVisible(false);
	            NewFindParameters.setToolTipText("Crear busquedas predefinidas");
	        }
	        else
	        {
	         	AdvDelete.setVisible(true);
	        	bSave.setToolTipText( "Update the selected search" );
	            advTName.setVisible(false);
	            advCName.setVisible(true);
	            NewFindParameters.setToolTipText("Usar busquedas predefinidas");
	        }
            //
        }
        else if(e.getSource() == operators ){
        	/*
        	 * Added by Disytel - Matias Cap
        	 * Evento disparado por los operadores para colocar los comodines (%) cuando
        	 * las operaciones son del tipo LIKE,ILIKE, etc.
        	 */
        	int row = advancedTable.getSelectedRow();
        	int col = advancedTable.getSelectedColumn();
        	if (row >=0 && col >= 0) {
	        	Object op = advancedTable.getValueAt( row, col);
	        	if(op != null){
	        		try{
	        			//Determino el operador real que va a ir en la consulta
	        			String operador = (( ValueNamePair )op ).getValue();
	        			String value = (String)advancedTable.getValueAt(advancedTable.getSelectedRow(),advancedTable.getSelectedColumn()+1);
	        			
	        			//Si es del tipo LIKE,ILIKE,NOT LIKE,NOT ILIKE, coloco los comodines
	        			if(operador.equals(MQuery.ILIKE) || operador.equals(MQuery.LIKE) || operador.equals(MQuery.NOT_ILIKE) || operador.equals(MQuery.NOT_LIKE)){
	                		String newValue = 
	                			(value.startsWith("%") ? "": "%") + 
	                			(value == null ? "":value) + 
	                			(value.endsWith("%") ? "": "%");
	                		advancedTable.setValueAt( newValue,advancedTable.getSelectedRow(),advancedTable.getSelectedColumn()+1 );
	                	}
	                	else {
	                		
	                		value = value.replaceAll("%","");
	                		advancedTable.setValueAt( value,advancedTable.getSelectedRow(),advancedTable.getSelectedColumn()+1 );
	                	}
	        		}
	        		catch(Exception ex){
	        			log.info("Exception al tratar con los operadores");
	        		}
	        	}
        	}
        }
        else if (e.getSource()==AdvDelete){
        	java.awt.Container c=new java.awt.Container();

        	if (advCName.getItemAt(advCName.getSelectedIndex())==null){
        		statusBar.setStatusLine("Seleccione un nombre");
        	}else{
	        	if (ADialog.ask(0,c,Msg.getMsg(Env.getCtx(), "DeleteSearch", new Object[] {advCName.getItemAt(advCName.getSelectedIndex()).toString()}))==true){
	            	AdvancedSearchRemove();
	    			AdvancedSearchLoad();
	    			AdvancedSearchCombo();
	        	}
        	}
        }
        else if( e.getSource() == advCName ){
        	AdvancedSearchLoad();

        }
        //End: Bug no 111 Added by Zarius Dataware 10/08/06 ---------------------------------------------------------------
        else if( e.getSource() == bDelete ) {
            cmd_delete();
        } else if( e.getSource() == columns ) {
            ValueNamePair column = ( ValueNamePair )columns.getSelectedItem();

            if( column != null ) {
                String columnName = column.getValue();
                MField field = getTargetMField(columnName);
                
                log.config( "Column: " + columnName );

                if( columnName.endsWith( "_ID" ) || columnName.endsWith( "_Acct" ) || 
                	DisplayType.isID(field.getDisplayType()) || 
                	DisplayType.isLookup(field.getDisplayType())) {
                    
                	operators.setModel( new DefaultComboBoxModel( MQuery.OPERATORS_ID ));
                } else if( columnName.startsWith( "Is" ) || field.getDisplayType() == DisplayType.YesNo) {
                    operators.setModel( new DefaultComboBoxModel( MQuery.OPERATORS_YN ));
                } else if( DisplayType.isNumeric(field.getDisplayType())) {
                	operators.setModel( new DefaultComboBoxModel( MQuery.OPERATORS_NUM ));
                } else if( DisplayType.isText(field.getDisplayType())) {
                	operators.setModel( new DefaultComboBoxModel( MQuery.OPERATORS_STR ));
                } else {
                    operators.setModel( new DefaultComboBoxModel( MQuery.OPERATORS ));
                }
                if (advancedTable.getSelectedRow() >= 0) {
                	advancedTable.setValueAt( "",advancedTable.getSelectedRow(),INDEX_OPERATOR );
                	advancedTable.setValueAt( "",advancedTable.getSelectedRow(),INDEX_VALUE );
                	advancedTable.setValueAt( "",advancedTable.getSelectedRow(),INDEX_VALUE2 );
                }
            }
        } else
        {
            if( e.getSource() == confirmPanelA.getOKButton()) {
                cmd_ok_Advanced();
            } else {
                cmd_ok_Simple();
            }
        }
    }             // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {

        // log.info( "Find.stateChanged");

        if( tabbedPane.getSelectedIndex() == 0 ) {
            this.getRootPane().setDefaultButton( confirmPanelS.getOKButton());
        } else {
            initFindAdvanced();
            this.getRootPane().setDefaultButton( confirmPanelA.getOKButton());
        }
    }    // stateChanged

    /**
     * @param preferenceName
     * @return true si la preference contiene el valor Y, false caso contrario
     */
    public boolean getEndWildcardFor(String preferenceName){
		String addValueWildcardStr = MPreference.searchCustomPreferenceValue(preferenceName,
				Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()), Env.getAD_User_ID(Env.getCtx()),
				true);
		return !Util.isEmpty(addValueWildcardStr, true) && addValueWildcardStr.equalsIgnoreCase("Y");
    }
    
    /**
     * Descripción de Método
     *
     */

    private void cmd_ok_Simple() {

    	// Levantar las preference para las wildcard al final del string de los campos default
    	// Value
    	boolean addValueWildcard = getEndWildcardFor(ADD_END_WILDCARD_VALUE_PREFERENCE_NAME);
    	// Name
    	boolean addNameWildcard = getEndWildcardFor(ADD_END_WILDCARD_NAME_PREFERENCE_NAME);
    	// Documentno
    	boolean addDocumentnoWildcard = getEndWildcardFor(ADD_END_WILDCARD_DOCUMENTNO_PREFERENCE_NAME);
    	// Description
    	boolean addDescriptionWildcard = getEndWildcardFor(ADD_END_WILDCARD_DESCRIPTION_PREFERENCE_NAME);
    	
        // Create Query String

        m_query = new MQuery( m_tableName );

        if( hasValue &&!valueField.getText().equals( "%" ) && (valueField.getText().length() != 0) ) {
            String value = valueField.getText().toUpperCase();

            // Líneas comentadas para no poner por default el comodin del final del string
            if( !value.endsWith( "%" ) && addValueWildcard) {
                value += "%";
            }

            m_query.addRestriction( "UPPER(Value)",MQuery.LIKE,value,valueLabel.getText(),value );
        }

        //

        if( hasDocNo &&!docNoField.getText().equals( "%" ) && (docNoField.getText().length() != 0) ) {
            String value = docNoField.getText().toUpperCase();

            // Líneas comentadas para no poner por default el comodin del final del string
            if( !value.endsWith( "%" ) && addDocumentnoWildcard) {
                value += "%";
            }

            m_query.addRestriction( "UPPER(DocumentNo)",MQuery.LIKE,value,docNoLabel.getText(),value );
        }

        //

        if(( hasName ) &&!nameField.getText().equals( "%" ) && (nameField.getText().length() != 0) ) {
            String value = nameField.getText().toUpperCase();

            // Líneas comentadas para no poner por default el comodin del final del string
            if( !value.endsWith( "%" ) && addNameWildcard) {
                value += "%";
            }

            m_query.addRestriction( "UPPER(Name)",MQuery.LIKE,value,nameLabel.getText(),value );
        }

        //

        if( hasDescription &&!descriptionField.getText().equals( "%" ) && (descriptionField.getText().length() != 0) ) {
            String value = descriptionField.getText().toUpperCase();

            // Líneas comentadas para no poner por default el comodin del final del string
            if( !value.endsWith( "%" ) && addDescriptionWildcard) {
                value += "%";
            }

            m_query.addRestriction( "UPPER(Description)",MQuery.LIKE,value,descriptionLabel.getText(),value );
        }

        // Special Editors

        for( int i = 0;i < m_sEditors.size();i++ ) {
            VEditor ved   = ( VEditor )m_sEditors.get( i );
            Object  value = ved.getValue();

            if( (value != null) && (value.toString().length() > 0) ) {
                String ColumnName = (( Component )ved ).getName();

                log.fine( ColumnName + "=" + value );

                if( value.toString().indexOf( "%" ) != -1 ) {
                    m_query.addRestriction( ColumnName,MQuery.LIKE,value,ColumnName,ved.getDisplay());
                } else {
                    m_query.addRestriction( ColumnName,MQuery.EQUAL,value,ColumnName,ved.getDisplay());
                }
            }
        }    // editors

        // Test for no records

        if( getNoOfRecords( m_query,true ) != 0 ) {
            dispose();
        }
    }    // cmd_ok_Simple

    /**
     * Descripción de Método
     *
     */

    private void cmd_ok_Advanced() {

        // save pending

        if( bSave.isEnabled()) {
            cmd_save();
        }

        if( getNoOfRecords( m_query,true ) != 0 ) {
            dispose();
        }
    }    // cmd_ok_Advanced

    /**
     * Descripción de Método
     *
     */

    private void cmd_cancel() {
        log.info( "" );
        m_query = null;
        dispose();
    }    // cmd_ok

    /**
     * Descripción de Método
     *
     */

    private void cmd_ignore() {
        log.info( "" );
    }    // cmd_ignore

    /**
     * Descripción de Método
     *
     */

    private void cmd_new() {

        DefaultTableModel model = ( DefaultTableModel )advancedTable.getModel();

        model.addRow( new Object[]{ null,null,null,null } );
    }    // cmd_new

    /**
     * Descripción de Método
     *
     */

    private void cmd_save() {
        log.info( "intentamos grabar..." );
        advancedTable.stopEditor( true );

        //

        m_query = new MQuery( m_tableName );

        for( int row = 0;row < advancedTable.getRowCount();row++ ) {

            Object column = advancedTable.getValueAt( row,INDEX_COLUMNNAME );

            if( column == null ) {
                continue;
            }

            String ColumnName = (( ValueNamePair )column ).getValue();
            String infoName   = column.toString();

            MField field      = getTargetMField( ColumnName );

            Object op = advancedTable.getValueAt( row,INDEX_OPERATOR );

            if( op == null ) {
                continue;
            }

            String Operator = (( ValueNamePair )op ).getValue();

            // Value   ******

            Object value = advancedTable.getValueAt( row,INDEX_VALUE );

            if( value == null ) {
                continue;
            }

            Object parsedValue = parseValue( field,value );

            if( parsedValue == null ) {
                continue;
            }

            String infoDisplay = value.toString();

            if( field.isLookup()) {
                infoDisplay = field.getLookup().getDisplay( value );
            } else if( field.getDisplayType() == DisplayType.YesNo ) {
                infoDisplay = Msg.getMsg( Env.getCtx(),infoDisplay );
            }

            // Value2  ******

            if( MQuery.OPERATORS[ MQuery.BETWEEN_INDEX ].equals( op )) {
                Object value2 = advancedTable.getValueAt( row,INDEX_VALUE2 );

                if( value2 == null ) {
                    continue;
                }

                Object parsedValue2   = parseValue( field,value2 );
                String infoDisplay_to = value2.toString();

                if( parsedValue2 == null ) {
                    continue;
                }
	        	 log.info("1:" + ColumnName + ", " + parsedValue + ", " + parsedValue2.toString() + ", " + infoName + ", " + infoDisplay + ", " + infoDisplay_to );
                m_query.addRangeRestriction( ColumnName,parsedValue,parsedValue2,infoName,infoDisplay,infoDisplay_to );
            } else {
                m_query.addRestriction( ColumnName,Operator,parsedValue,infoName,infoDisplay );
                log.info("2:" + ColumnName + ", " + Operator + ", " + parsedValue + ", " + infoName + ", " + infoDisplay);
            }
        }
    }    // cmd_save

    /**
     * Descripción de Método
     *
     *
     * @param field
     * @param in
     *
     * @return
     */

    private Object parseValue( MField field,Object in ) {
        if( in == null ) {
            return null;
        }

        int dt = field.getDisplayType();

        try {

            // Return Integer

            if( (dt == DisplayType.Integer) || ( DisplayType.isID( dt ) && field.getColumnName().endsWith( "_ID" ))) {
                if( in instanceof Integer ) {
                    return in;
                }

                int i = Integer.parseInt( in.toString());

                return new Integer( i );
            }

            // Return BigDecimal

            else if( DisplayType.isNumeric( dt )) {
                if( in instanceof BigDecimal ) {
                    return in;
                }

                return DisplayType.getNumberFormat( dt ).parse( in.toString());
            }

            // Return Timestamp

            else if( DisplayType.isDate( dt )) {
                if( in instanceof Timestamp ) {
                    return in;
                }

                long time = 0;

                try {
                    time = DisplayType.getDateFormat_JDBC().parse( in.toString()).getTime();

                    return new Timestamp( time );
                } catch( Exception e ) {
                    log.log( Level.SEVERE,in + "(" + in.getClass() + ")" + e );
                    time = DisplayType.getDateFormat( dt ).parse( in.toString()).getTime();
                }

                return new Timestamp( time );
            }

            // Return Y/N for Boolean

            else if( in instanceof Boolean ) {
                return(( Boolean )in ).booleanValue()
                      ?"Y"
                      :"N";
            }
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Object=" + in,ex );

            String error = ex.getLocalizedMessage();

            if( (error == null) || (error.length() == 0) ) {
                error = ex.toString();
            }

            StringBuffer errMsg = new StringBuffer();

            errMsg.append( field.getColumnName()).append( " = " ).append( in ).append( " - " ).append( error );

            //

            ADialog.error( 0,this,"ValidationError",errMsg.toString());

            return null;
        }

        return in;
    }    // parseValue

    /**
     * Descripción de Método
     *
     */

    private void cmd_delete() {
        log.info( "Deleting..." );

        DefaultTableModel model = ( DefaultTableModel )advancedTable.getModel();
        int row = advancedTable.getSelectedRow();

        if( row >= 0 ) {
            model.removeRow( row );
        }

        cmd_refresh();
    }    // cmd_delete

    /**
     * Descripción de Método
     *
     */

    private void cmd_refresh() {
        log.info( "Refreshing..." );

        int records = getNoOfRecords( m_query,true );

        setStatusDB( records );
        statusBar.setStatusLine( "" );
    }    // cmd_refresh

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MQuery getQuery() {
        log.info( "Query=" + m_query );

        return m_query;
    }    // getQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTotalRecords() {
        return m_total;
    }    // getTotalRecords

    /**
     * Descripción de Método
     *
     *
     * @param query
     * @param alertZeroRecords
     *
     * @return
     */

    public int getNoOfRecords( MQuery query,boolean alertZeroRecords ) {
        log.config( "" + query );

        StringBuffer sql = new StringBuffer( "SELECT COUNT(*) FROM " );

        sql.append( m_tableName );

        boolean hasWhere = false;

        if( (m_whereExtended != null) && (m_whereExtended.length() > 0) ) {
            sql.append( " WHERE " ).append( m_whereExtended );
            hasWhere = true;
        }

        if( (query != null) && query.isActive()) {
            if( hasWhere ) {
                sql.append( " AND " );
            } else {
                sql.append( " WHERE " );
            }

            sql.append( query.getWhereClause());
        }

        // Add Access

        String finalSQL = MRole.getDefault().addAccessSQL( sql.toString(),m_tableName,MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        finalSQL = Env.parseContext( Env.getCtx(),m_targetWindowNo,finalSQL,false );
        Env.setContext( Env.getCtx(),m_targetWindowNo,TABNO,"FindSQL",finalSQL );

        int count = 0;

        // Execute Qusery

        try {
            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( finalSQL );

            if( rs.next()) {
                count = rs.getInt( 1 );
            }

            rs.close();
            stmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,finalSQL,e );
        }

        if( (count == 0) && alertZeroRecords ) {
            ADialog.info( m_targetWindowNo,this,"FindZeroRecords" );
        } else {
            log.config( "#" + count );
        }

        if( query != null ) {
            statusBar.setStatusToolTip( query.getWhereClause());
        }

        return count;
    }    // getNoOfRecords

    /**
     * Descripción de Método
     *
     *
     * @param currentCount
     */

    private void setStatusDB( int currentCount ) {
        String text = " " + currentCount + " / " + m_total + " ";

        statusBar.setStatusDB( text );
    }    // setDtatusDB

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void dataStatusChanged( DataStatusEvent e ) {
        log.config( e.getMessage());

        // Action control

        boolean changed = e.isChanged();

        bIgnore.setEnabled( changed );
        bNew.setEnabled( !changed );
        bSave.setEnabled( changed );
        bDelete.setEnabled( !changed );
    }    // statusChanged

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     *
     * @return
     */

    public MField getTargetMField( String columnName ) {
        if( columnName == null ) {
            return null;
        }

        for( int c = 0;c < m_findFields.length;c++ ) {
            MField field = m_findFields[ c ];

            if( columnName.equals( field.getColumnName())) {
                return field;
            }
        }

        return null;
    }    // getTargetMField

	/**
	 * Descripción: Determina si la tabla tiene almenos 1 parámetro
	 *
	 * @author Zarius
	 *
	 * @return boolean
	 */

    public boolean getfullgrid(){

		 for( int row = 0;row < advancedTable.getRowCount();row++ ) {

	            Object column = advancedTable.getValueAt( row,INDEX_COLUMNNAME );
	            if( column == null ) {
	            	return true;
	            }
		 }

		return false;
	}

    /**
     * Descripción de Método: Carga los parámetros adecuados en el combo
     *
     * @status en desarrollo.
     */

    private void AdvancedSearchCombo(){
    	advCName.removeAllItems();
    	String sql="" +
    	"SELECT name " +
    	"FROM ad_search " +
    	"WHERE ad_table_id=? AND " +
    		"ad_org_id=? AND " +
    		"ad_client_id=? AND " +
    		"isactive='Y' " +
    	"GROUP BY name";

    	PreparedStatement pstmt = DB.prepareStatement( sql );

		try {
			pstmt.setInt(1,m_AD_Table_ID);
			pstmt.setInt(2,Env.getAD_Org_ID(Env.getCtx()));
			pstmt.setInt(3,Env.getAD_Client_ID(Env.getCtx()));

			ResultSet rs = pstmt.executeQuery();
			advCName.addItem(null);
			while (rs.next()){
				advCName.addItem(rs.getString(1));
				}
			rs.close();
		}
		catch(Exception e){
			statusBar.setStatusLine("Error: Find.AdvancedSearchCombo()");
		}
    }

    /**
     * Descripción de Método: Carga los parámetros adecuados en la tabla
     *
	 * @author Zarius
     *
     * @status en desarrollo.
     */

    private void AdvancedSearchLoad(){

    	String name="";
        String sql="" +
    	"SELECT val1, val2, val3, op " +
    	"FROM ad_search " +
    	"WHERE ad_table_id=? AND " +
    		"ad_org_id=? AND " +
    		"ad_client_id=? AND " +
    		"isactive='Y' AND name=?";

		if (advCName.getItemAt(advCName.getSelectedIndex())==null)
			name=advTName.getText();
		else
			name=advCName.getItemAt(advCName.getSelectedIndex()).toString();

		log.info(
				"---> SELECT val1, val2, val3, op " +
		    	"FROM ad_search " +
		    	"WHERE ad_table_id=" + m_AD_Table_ID + " AND " +
		    		"ad_org_id=" + Env.getAD_Org_ID(Env.getCtx()) + " AND " +
		    		"ad_client_id=" + Env.getAD_Client_ID(Env.getCtx()) + " AND " +
		    		"isactive='Y' AND name=" + name);

    	PreparedStatement pstmt = DB.prepareStatement( sql );

		try {
			pstmt.setInt(1,m_AD_Table_ID);
			pstmt.setInt(2,Env.getAD_Org_ID(Env.getCtx()));
			pstmt.setInt(3,Env.getAD_Client_ID(Env.getCtx()));
			pstmt.setString(4,name);

			ResultSet rs = pstmt.executeQuery();

			RemoveTableRows();
			//RemoveTableRows();

			while (rs.next()){
				DefaultTableModel model = ( DefaultTableModel )advancedTable.getModel();
				String columnName = rs.getString(1);
                String header     = Msg.translate(Env.getCtx(),columnName);

		        ValueNamePair pp = new ValueNamePair( columnName,header );

		        if (rs.getString(4).equals("between")){
		        	model.addRow( new Object[]{ pp ,MQuery.OPERATORS[ MQuery.BETWEEN_INDEX  ],rs.getString(2),null} );
				}
				else{
					model.addRow( new Object[]{ pp,MQuery.OPERATORS[ FinalOp(rs.getString(4))],rs.getString(2),rs.getString(3)} );
				}
			}
			rs.close();
		}
		catch(Exception e){
			statusBar.setStatusLine("Error: Find.AdvancedSearchLoad()");
		}


    }

    /**
     * Descripción de Método: Salva los parámetros introducidos
     *
	 * @author Zarius
     *
     * @status en desarrollo.
     */

    private String AdvancedSearchSave(){
    	String FinalMessage="";
    	advancedTable.stopEditor( true );
    	int num=0;
    	/*
    	 * Primero verificamos que el nombre introducido no exista previamente.
    	 *
    	 */
    	String name="";

    	if (advTName.getText().length()>0)
			name=advTName.getText();
		else
			name=advCName.getItemAt(advCName.getSelectedIndex()).toString();

    	String sql="" +
    	"SELECT count(*) " +
    	"FROM ad_search " +
    	"WHERE ad_table_id=? AND " +
    		"ad_org_id=? AND " +
    		"ad_client_id=? AND " +
    		"isactive='Y' AND name=?";

    	PreparedStatement pstmt = DB.prepareStatement( sql );

		try {
			pstmt.setInt(1,m_AD_Table_ID);
			pstmt.setInt(2,Env.getAD_Org_ID(Env.getCtx()));
			pstmt.setInt(3,Env.getAD_Client_ID(Env.getCtx()));
			pstmt.setString(4,name);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()){
				num=rs.getInt(1);
				}
			rs.close();
		}
		catch (Exception e){
			FinalMessage= "Error: Find.AdvancedSearchSave()";
		}
    	if (num==0){
	    	//Variables que siempre serán las mismas para la serie de clausulas:

	    	/*
	    	 * Generados por el sistema
	    	 * ad_client_id / ad_org_id / isactive / created / createdby / updated / updatedby
	    	 *
	    	 * Insertados por el programador:
	    	 * ad_table_id / name
	    	 *
	    	 */
	    	int ad_table_id=m_AD_Table_ID;

	    	if (advTName.getText().length()>0 && advCName.isVisible()==false)
				name=advTName.getText();
			else
				name=advCName.getItemAt(advCName.getSelectedIndex()).toString();

	    	//bucle para recorrer cada fila de la tabla de condiciones:
	        for( int row = 0;row < advancedTable.getRowCount();row++ ) {

	        	//nueva fila en la tabla ad_search:
	        	X_AD_Search search = new X_AD_Search(Env.getCtx(), 0, null);

	        	search.setName(name);
	        	search.setAD_Table_ID(ad_table_id);

	        	//Nombre de la columna a la que establecemos una condicion ********************************************
	            Object column = advancedTable.getValueAt( row,INDEX_COLUMNNAME );
	            if( column == null )
	                continue;
	            String ColumnName = (( ValueNamePair )column ).getValue();
	            MField field      = getTargetMField( ColumnName );

	            //Operando ********************************************************************************************
	            Object op = advancedTable.getValueAt( row,INDEX_OPERATOR );

	            if( op == null )
	                continue;

	            String Operator = (( ValueNamePair )op ).getValue();

	            // 1º Valor Asignado **********************************************************************************
	            Object value = advancedTable.getValueAt( row,INDEX_VALUE );

	            if( value == null )
	                continue;

	            Object parsedValue = parseValue( field,value );

	            if( parsedValue == null )
	                continue;

	            String infoDisplay = value.toString();

	            if( field.isLookup()) {
	                infoDisplay = field.getLookup().getDisplay( value );
	            } else if( field.getDisplayType() == DisplayType.YesNo ) {
	                infoDisplay = Msg.getMsg( Env.getCtx(),infoDisplay );
	            }

	            //Es una relacion between? (es la única operación especial)
	            if( MQuery.OPERATORS[ MQuery.BETWEEN_INDEX ].equals( op )) {
		            // 2º Valor *******************************************************************************************
		            Object value2 = advancedTable.getValueAt( row,INDEX_VALUE2 );
		            Object parsedValue2   = parseValue( field,value2 );
	                if( value2 == null )
	                	continue;

	                if( parsedValue2 == null ) {
	                    continue;
	                }
	                search.setval1(ColumnName);
	                search.setval2(parsedValue.toString());
	                search.setop("between");
	                search.setval3(parsedValue2.toString());
		            if (ColumnName==null ||  parsedValue==null || parsedValue2==null){
		            	FinalMessage= "Error: ningun valor puede ser nulo";
		            }
		            else { search.save(); }
		            m_query.addRangeRestriction( ColumnName,parsedValue,parsedValue2);
	            }
	            //Si no es una relación between:
	            else {

	                search.setval1(ColumnName);
	                search.setval2(parsedValue.toString());
	                search.setop(Operator);
	                m_query.addRestriction( ColumnName,Operator,parsedValue);
		            if (ColumnName==null ||  parsedValue==null || Operator==null){
		            	FinalMessage= "Error: Alguno de los valores es nulo";
		            }
		            else { search.save(); }
	            }
	        }
	    	RemoveTableRows();
	    	FinalMessage= "'" + advTName.getText() + "' se ha guardado con éxito";
    	}else{
    		log.info("Ya existe una consulta " + advTName.getText());
        	RemoveTableRows();
        	FinalMessage= "Ya existe una consulta " + advTName.getText();
    	}

		AdvDelete.setVisible(false);
		bSave.setToolTipText( "Save the advanced search" );
        advTName.setVisible(true);
        advCName.setVisible(false);
        NewFindParameters.setToolTipText("Crear busquedas predefinidas");
        return FinalMessage;
    }

    /**
     * Descripción de Método: retorna el int correspondiente al operador seleccionado
     *
	 * @author Zarius
     *
     * @status en desarrollo.
     */

    private int FinalOp(String myop){

    	if (myop.contains("!=")) return 1;
    	if (myop.contains("!~")) return 3;
    	if (myop.contains(">=")) return 5;
    	if (myop.contains("<=")) return 7;
    	if (myop.contains("!=")) return 1;
    	if (myop.contains("!=")) return 1;

    	if (myop.contains("=") && !myop.contains("!")) return 0;
    	if (myop.contains("~") && !myop.contains("!")) return 2;
    	if (myop.contains(">") && !myop.contains("!")) return 4;
    	if (myop.contains("<") && !myop.contains("!")) return 6;

    	return 10;
    }

    /**
     * Descripción de Método: Borra todas las filas
     *
	 * @author Zarius
     *
     * @status en desarrollo.
     */

    private void RemoveTableRows(){
    	advancedTable.stopEditor( true );
    	DefaultTableModel model = ( DefaultTableModel )advancedTable.getModel();
    	model.setRowCount(0);
    	/*
        for( int row = 0;row < model.getRowCount();row++ ) {
        	model.removeRow(row);
        }
        */
    }

    /**
     * Descripción de Método: Borra la búsqueda avanzada predefinida seleccionada
     *
	 * @author Zarius
     *
     * @status en desarrollo.
     */
    private void AdvancedSearchRemove(){
    	String name="";

		Object nameObj = advCName.getItemAt(advCName.getSelectedIndex());
		if (nameObj != null )
			name = nameObj.toString();

		if (name.length()==0){
			statusBar.setStatusLine("Nothing selected");
		}else{

	    	String sql="" +
	    	"DELETE " +
	    	"FROM ad_search " +
	    	"WHERE ad_table_id=" + m_AD_Table_ID + " AND " +
			"ad_org_id=" + Env.getAD_Org_ID(Env.getCtx()) + " AND " +
			"ad_client_id=" + Env.getAD_Client_ID(Env.getCtx()) + " AND " +
			"isactive='Y' AND name='" + name + "'";

			try {
					DB.executeUpdate( sql );
				}
			catch (Exception e){
					log.info("Error while deleting: " + e.toString());
					log.info(
					    	"DELETE " +
					    	"FROM ad_search " +
					    	"WHERE ad_table_id=" + m_AD_Table_ID + " AND " +
							"ad_org_id=" + Env.getAD_Org_ID(Env.getCtx()) + " AND " +
							"ad_client_id=" + Env.getAD_Client_ID(Env.getCtx()) + " AND " +
							"isactive='Y' AND name=" + name
							);
				}
		}

    }
}    // Find
/*
 *  @(#)Find.java   02.07.07
 *
 *  Fin del fichero Find.java
 *
 *  Versión 2.2 by Dataware Sistemas
 *
 */
