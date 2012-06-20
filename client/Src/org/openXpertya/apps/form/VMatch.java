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



package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MInOutLine;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MMatchInv;
import org.openXpertya.model.MMatchPO;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MStorage;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VMatch extends CPanel implements FormPanel,ActionListener,TableModelListener,ListSelectionListener {

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
        log.info( "WinNo=" + m_WindowNo + " - AD_Client_ID=" + m_AD_Client_ID + ", AD_Org_ID=" + m_AD_Org_ID + ", By=" + m_by );
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","N" );

        try {

            // UI

            onlyVendor  = VLookup.createBPartner( m_WindowNo );
            onlyProduct = VLookup.createProduct( m_WindowNo );
            jbInit();

            //

            dynInit();
            frame.getContentPane().add( mainPanel,BorderLayout.CENTER );
            frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VMatch.class );

    /** Descripción de Campos */

    private int m_AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());

    /** Descripción de Campos */

    private int m_AD_Org_ID = Env.getAD_Org_ID( Env.getCtx());

    /** Descripción de Campos */

    private int m_by = Env.getAD_User_ID( Env.getCtx());

    /** Descripción de Campos */

    private String[] m_matchOptions = new String[]{ Msg.getElement( Env.getCtx(),"C_Invoice_ID",false ),Msg.getElement( Env.getCtx(),"M_InOut_ID",false ),Msg.getElement( Env.getCtx(),"C_Order_ID",false )};

    /** Descripción de Campos */

    private static final int MATCH_INVOICE = 0;

    /** Descripción de Campos */

    private static final int MATCH_SHIPMENT = 1;

    /** Descripción de Campos */

    private static final int MATCH_ORDER = 2;

    /** Descripción de Campos */

    private String[] m_matchMode = new String[]{ Msg.translate( Env.getCtx(),"CorrespondingNotMatched" ),Msg.translate( Env.getCtx(),"MatchRevert" )};

    /** Descripción de Campos */

    private static final int MODE_NOTMATCHED = 0;

    /** Descripción de Campos */

    private static final int MODE_MATCHED = 1;

    /** Descripción de Campos */

    private static final int I_BPartner = 3;

    /** Descripción de Campos */

    private static final int I_Line = 4;

    /** Descripción de Campos */

    private static final int I_Product = 5;

    /** Descripción de Campos */

    private static final int I_QTY = 6;

    /** Descripción de Campos */

    private static final int I_MATCHED = 7;

    /** Descripción de Campos */

    private StringBuffer m_sql = null;

    /** Descripción de Campos */

    private String m_dateColumn = "";

    /** Descripción de Campos */

    private String m_qtyColumn = "";

    /** Descripción de Campos */

    private String m_groupBy = "";

    /** Descripción de Campos */

    private BigDecimal m_xMatched = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_xMatchedTo = Env.ZERO;

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout northLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel matchFromLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox matchFrom = new CComboBox( m_matchOptions );

    /** Descripción de Campos */

    private CLabel matchToLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox matchTo = new CComboBox();

    /** Descripción de Campos */

    private CLabel matchModeLabel = new CLabel();

    /** Descripción de Campos */

    private CComboBox matchMode = new CComboBox( m_matchMode );

    /** Descripción de Campos */

    private VLookup onlyVendor = null;

    /** Descripción de Campos */

    private VLookup onlyProduct = null;

    /** Descripción de Campos */

    private CLabel onlyVendorLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel onlyProductLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel dateFromLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel dateToLabel = new CLabel();

    /** Descripción de Campos */

    private VDate dateFrom = new VDate( "DateFrom",false,false,true,DisplayType.Date,"DateFrom" );

    /** Descripción de Campos */

    private VDate dateTo = new VDate( "DateTo",false,false,true,DisplayType.Date,"DateTo" );

    /** Descripción de Campos */

    private CButton bSearch = new CButton();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout southLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel xMatchedLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel xMatchedToLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel differenceLabel = new CLabel();

    /** Descripción de Campos */

    private VNumber xMatched = new VNumber( "xMatched",false,true,false,DisplayType.Quantity,"xMatched" );

    /** Descripción de Campos */

    private VNumber xMatchedTo = new VNumber( "xMatchedTo",false,true,false,DisplayType.Quantity,"xMatchedTo" );

    /** Descripción de Campos */

    private VNumber difference = new VNumber( "Difference",false,true,false,DisplayType.Quantity,"Difference" );

    /** Descripción de Campos */

    private CButton bProcess = new CButton();

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout centerLayout = new BorderLayout( 5,5 );

    /** Descripción de Campos */

    private JScrollPane xMatchedScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private TitledBorder xMatchedBorder = new TitledBorder( "xMatched" );

    /** Descripción de Campos */

    private MiniTable xMatchedTable = new MiniTable();

    /** Descripción de Campos */

    private JScrollPane xMatchedToScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private TitledBorder xMatchedToBorder = new TitledBorder( "xMatchedTo" );

    /** Descripción de Campos */

    private MiniTable xMatchedToTable = new MiniTable();

    /** Descripción de Campos */

    private CPanel xPanel = new CPanel();

    /** Descripción de Campos */

    private JCheckBox sameProduct = new JCheckBox();

    /** Descripción de Campos */

    private JCheckBox sameBPartner = new JCheckBox();

    /** Descripción de Campos */

    private JCheckBox sameQty = new JCheckBox();

    /** Descripción de Campos */

    private FlowLayout xLayout = new FlowLayout( FlowLayout.CENTER,10,0 );
    
    private static final String MSG_MATCH  = Msg.translate(Env.getCtx(), "Match");
    private static final String MSG_REVERT = Msg.translate(Env.getCtx(), "Revert");

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        mainPanel.setLayout( mainLayout );
        northPanel.setLayout( northLayout );
        matchFromLabel.setText( Msg.translate( Env.getCtx(),"MatchFrom" ));
        matchToLabel.setText( Msg.translate( Env.getCtx(),"MatchTo" ));
        //matchModeLabel.setText( Msg.translate( Env.getCtx(),"MatchMode" ));
        matchModeLabel.setText( Msg.translate( Env.getCtx(),"Action" ));
        onlyVendorLabel.setText( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        onlyProductLabel.setText( Msg.translate( Env.getCtx(),"M_Product_ID" ));
        dateFromLabel.setText( Msg.translate( Env.getCtx(),"DateFrom" ));
        dateToLabel.setText( Msg.translate( Env.getCtx(),"DateTo" ));
        bSearch.setText( Msg.translate( Env.getCtx(),"Search" ));
        southPanel.setLayout( southLayout );
        xMatchedLabel.setText( Msg.translate( Env.getCtx(),"ToBeMatched" ));
        xMatchedToLabel.setText( Msg.translate( Env.getCtx(),"Matching" ));
        differenceLabel.setText( Msg.translate( Env.getCtx(),"Difference" ));
        bProcess.setText( MSG_MATCH );
        bProcess.setPreferredSize(new Dimension(150,26));
        centerPanel.setLayout( centerLayout );
        xMatchedScrollPane.setBorder( xMatchedBorder );
        xMatchedScrollPane.setPreferredSize( new Dimension( 450,200 ));
        xMatchedToScrollPane.setBorder( xMatchedToBorder );
        xMatchedToScrollPane.setPreferredSize( new Dimension( 450,200 ));
        sameProduct.setSelected( true );
        sameProduct.setText( Msg.translate( Env.getCtx(),"SameProduct" ));
        sameBPartner.setSelected( true );
        sameBPartner.setText( Msg.translate( Env.getCtx(),"SameBPartner" ));
        sameQty.setSelected( false );
        sameQty.setText( Msg.translate( Env.getCtx(),"SameQty" ));
        xPanel.setLayout( xLayout );
        mainPanel.add( northPanel,BorderLayout.NORTH );
        northPanel.add( matchFromLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 12,12,5,5 ),0,0 ));
        northPanel.add( matchFrom,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 12,0,5,0 ),0,0 ));
        northPanel.add( matchToLabel,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 12,5,5,5 ),0,0 ));
        northPanel.add( matchTo,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 12,0,5,0 ),0,0 ));
        northPanel.add( matchModeLabel,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        northPanel.add( matchMode,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,0 ),0,0 ));
        northPanel.add( onlyVendor,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,0 ),0,0 ));
        northPanel.add( onlyProduct,new GridBagConstraints( 3,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,0 ),0,0 ));
        northPanel.add( onlyVendorLabel,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        northPanel.add( onlyProductLabel,new GridBagConstraints( 2,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        northPanel.add( dateFromLabel,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,5 ),0,0 ));
        northPanel.add( dateToLabel,new GridBagConstraints( 2,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,0,5,5 ),0,0 ));
        northPanel.add( dateFrom,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,0 ),0,0 ));
        northPanel.add( dateTo,new GridBagConstraints( 3,3,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 0,0,5,0 ),0,0 ));
        northPanel.add( bSearch,new GridBagConstraints( 4,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 0,12,5,12 ),0,0 ));
        mainPanel.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( xMatchedLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,12,5,5 ),0,0 ));
        southPanel.add( xMatched,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,0 ),0,0 ));
        southPanel.add( xMatchedToLabel,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,10,5,5 ),0,0 ));
        southPanel.add( bProcess,new GridBagConstraints( 6,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,12,5,12 ),0,0 ));
        southPanel.add( differenceLabel,new GridBagConstraints( 4,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,10,5,5 ),0,0 ));
        mainPanel.add( centerPanel,BorderLayout.CENTER );
        centerPanel.add( xMatchedScrollPane,BorderLayout.NORTH );
        xMatchedScrollPane.getViewport().add( xMatchedTable,null );
        centerPanel.add( xMatchedToScrollPane,BorderLayout.SOUTH );
        centerPanel.add( xPanel,BorderLayout.CENTER );
        xPanel.add( sameBPartner,null );
        xPanel.add( sameProduct,null );
        xPanel.add( sameQty,null );
        xMatchedToScrollPane.getViewport().add( xMatchedToTable,null );
        southPanel.add( difference,new GridBagConstraints( 5,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,0 ),0,0 ));
        southPanel.add( xMatchedTo,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,0 ),0,0 ));
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        ColumnInfo[] layout = new ColumnInfo[] {
            new ColumnInfo( " ",".",IDColumn.class,false,false,"" ),
            new ColumnInfo( Msg.translate( Env.getCtx(),"DocumentNo" ),".",String.class ),    // 1
            new ColumnInfo( Msg.translate( Env.getCtx(),"Date" ),".",Timestamp.class ),
            new ColumnInfo( Msg.translate( Env.getCtx(),"C_BPartner_ID" ),".",KeyNamePair.class,"." ),    // 3
            new ColumnInfo( Msg.translate( Env.getCtx(),"Line" ),".",KeyNamePair.class,"." ),
            new ColumnInfo( Msg.translate( Env.getCtx(),"M_Product_ID" ),".",KeyNamePair.class,"." ),    // 5
            new ColumnInfo( Msg.translate( Env.getCtx(),"Qty" ),".",Double.class ),
            new ColumnInfo( Msg.translate( Env.getCtx(),"Matched" ),".",Double.class )
        };

        xMatchedTable.prepareTable( layout,"","",false,"" );
        xMatchedToTable.prepareTable( layout,"","",true,"" );

        // Visual

        CompiereColor.setBackground( this );

        // Listener

        matchFrom.addActionListener( this );
        matchTo.addActionListener( this );
        matchMode.addActionListener( this );
        bSearch.addActionListener( this );
        xMatchedTable.getSelectionModel().addListSelectionListener( this );
        xMatchedToTable.getModel().addTableModelListener( this );
        bProcess.addActionListener( this );
        sameBPartner.addActionListener( this );
        sameProduct.addActionListener( this );
        sameQty.addActionListener( this );

        // Init

        cmd_matchFrom();
        statusBar.setStatusLine( "" );
        statusBar.setStatusDB( 0 );
    }    // dynInit

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
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        if( e.getSource() == matchFrom ) {
            cmd_matchFrom();
        } else if( e.getSource() == matchMode ) {
        	cmd_matchMode();
        } else if( e.getSource() == matchTo ) {
            cmd_matchTo();
        } else if( e.getSource() == bSearch ) {
            cmd_search();
        } else if( e.getSource() == bProcess ) {
            cmd_process();
        } else if( (e.getSource() == sameBPartner) || (e.getSource() == sameProduct) || (e.getSource() == sameQty) ) {
            cmd_searchTo();
        }

        setCursor( Cursor.getDefaultCursor());
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void cmd_matchFrom() {

        // log.fine( "VMatch.cmd_matchFrom");

        String selection = ( String )matchFrom.getSelectedItem();
        Vector vector    = new Vector( 2 );

        if( selection.equals( m_matchOptions[ MATCH_INVOICE ] )) {
            vector.add( m_matchOptions[ MATCH_SHIPMENT ] );
        } else if( selection.equals( m_matchOptions[ MATCH_ORDER ] )) {
            vector.add( m_matchOptions[ MATCH_SHIPMENT ] );
        } else    // shipment
        {
            vector.add( m_matchOptions[ MATCH_INVOICE ] );
            vector.add( m_matchOptions[ MATCH_ORDER ] );
        }

        matchTo.setModel( new DefaultComboBoxModel( vector ));

        // Set Title

        xMatchedBorder.setTitle( selection );
        xMatchedScrollPane.repaint();

        // Reset Table

        xMatchedTable.setRowCount( 0 );

        // sync To

        cmd_matchTo();
    }    // cmd_matchFrom

    /**
     * Descripción de Método
     *
     */

    private void cmd_matchTo() {

        // log.fine( "VMatch.cmd_matchTo");

        String selection = ( String )matchTo.getSelectedItem();

        xMatchedToBorder.setTitle( selection );
        xMatchedToScrollPane.repaint();

        // Reset Table

        xMatchedToTable.setRowCount( 0 );
    }    // cmd_matchTo

    /**
     * Descripción de Método
     *
     */

    private void cmd_search() {
        log.config( "" );

        // ** Create SQL **

        int    display       = matchFrom.getSelectedIndex();
        String matchToString = ( String )matchTo.getSelectedItem();
        int    matchToType   = MATCH_INVOICE;

        if( matchToString.equals( m_matchOptions[ MATCH_SHIPMENT ] )) {
            matchToType = MATCH_SHIPMENT;
        } else if( matchToString.equals( m_matchOptions[ MATCH_ORDER ] )) {
            matchToType = MATCH_ORDER;
        }

        tableInit( display,matchToType );    // sets m_sql

        // ** Add Where Clause **
        // Product

        if( onlyProduct.getValue() != null ) {
            Integer Product = ( Integer )onlyProduct.getValue();

            m_sql.append( " AND lin.M_Product_ID=" ).append( Product );
        }

        // BPartner

        if( onlyVendor.getValue() != null ) {
            Integer Vendor = ( Integer )onlyVendor.getValue();

            m_sql.append( " AND hdr.C_BPartner_ID=" ).append( Vendor );
        }

        // Date

        Timestamp from = ( Timestamp )dateFrom.getValue();
        Timestamp to   = ( Timestamp )dateTo.getValue();

        if( (from != null) && (to != null) ) {
            m_sql.append( " AND " ).append( m_dateColumn ).append( " BETWEEN " ).append( DB.TO_DATE( from )).append( " AND " ).append( DB.TO_DATE( to ));
        } else if( from != null ) {
            m_sql.append( " AND " ).append( m_dateColumn ).append( " >= " ).append( DB.TO_DATE( from ));
        } else if( to != null ) {
            m_sql.append( " AND " ).append( m_dateColumn ).append( " <= " ).append( DB.TO_DATE( to ));
        }

        // ** Load Table **

        tableLoad( xMatchedTable );
        xMatched.setValue( Env.ZERO );

        // Status Info

        statusBar.setStatusLine( matchFrom.getSelectedItem().toString() + "# = " + xMatchedTable.getRowCount(),xMatchedTable.getRowCount() == 0 );
        statusBar.setStatusDB( 0 );
    }    // cmd_search

    /**
     * Descripción de Método
     *
     */

    private void cmd_process() {
        log.config( "" );

        // Matched From

        int matchedRow = xMatchedTable.getSelectedRow();

        if( matchedRow < 0 ) {
            return;
        }

        // KeyNamePair BPartner = (KeyNamePair)xMatchedTable.getValueAt(matchedRow, I_BPartner);

        KeyNamePair lineMatched = ( KeyNamePair )xMatchedTable.getValueAt( matchedRow,I_Line );
        KeyNamePair Product = ( KeyNamePair )xMatchedTable.getValueAt( matchedRow,I_Product );
        int    M_Product_ID = Product.getKey();
        double totalQty     = m_xMatched.doubleValue();

        // Matched To
       
            
        for( int row = 0;row < xMatchedToTable.getRowCount();row++ ) {
            IDColumn id = ( IDColumn )xMatchedToTable.getValueAt( row,0 );

            if( (id != null) && id.isSelected()) {

                // need to be the same product

                KeyNamePair ProductCompare = ( KeyNamePair )xMatchedToTable.getValueAt( row,I_Product );

                if( Product.getKey() != ProductCompare.getKey()) {
                    continue;
                }

                KeyNamePair lineMatchedTo = ( KeyNamePair )xMatchedToTable.getValueAt( row,I_Line );

                // Qty

                double qty = 0.0;

                if( matchMode.getSelectedIndex() == MODE_NOTMATCHED ) {
                    qty = (( Double )xMatchedToTable.getValueAt( row,I_QTY )).doubleValue();    // doc
                }

                qty -= (( Double )xMatchedToTable.getValueAt( row,I_MATCHED )).doubleValue();    // matched

                if( qty > totalQty ) {
                    qty = totalQty;
                }

                totalQty -= qty;

                // Invoice or PO

                boolean invoice = true;

                if( (matchFrom.getSelectedIndex() == MATCH_ORDER) || matchTo.getSelectedItem().equals( m_matchOptions[ MATCH_ORDER ] )) {
                    invoice = false;
                }

                // Get Shipment_ID

                int M_InOutLine_ID = 0;
                int Line_ID        = 0;

                if( matchFrom.getSelectedIndex() == MATCH_SHIPMENT ) {
                    M_InOutLine_ID = lineMatched.getKey();      // upper table
                    Line_ID        = lineMatchedTo.getKey();
                } else {
                    M_InOutLine_ID = lineMatchedTo.getKey();    // lower table
                    Line_ID        = lineMatched.getKey();
                }

                // Create it

                createMatchRecord( invoice,M_InOutLine_ID,Line_ID,new BigDecimal( qty ));
            }
        }

        // requery

        cmd_search();
    }    // cmd_process

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

        // log.config( "VMatch.valueChanged");

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        cmd_searchTo();
        setCursor( Cursor.getDefaultCursor());
    }    // valueChanged

    /**
     * Descripción de Método
     *
     */

    private void cmd_searchTo() {
        int row = xMatchedTable.getSelectedRow();

        log.config( "Llego a cmd_searchto connnn Row=" + row );

        double qty = 0.0;

        if( row < 0 ) {
            xMatchedToTable.setRowCount( 0 );
        } else {

            // ** Create SQL **

            String displayString = ( String )matchTo.getSelectedItem();
            int    display       = MATCH_INVOICE;

            if( displayString.equals( m_matchOptions[ MATCH_SHIPMENT ] )) {
                display = MATCH_SHIPMENT;
            } else if( displayString.equals( m_matchOptions[ MATCH_ORDER ] )) {
                display = MATCH_ORDER;
            }

            int matchToType = matchFrom.getSelectedIndex();

            tableInit( display,matchToType );    // sets m_sql

            // ** Add Where Clause **

            KeyNamePair BPartner = ( KeyNamePair )xMatchedTable.getValueAt( row,I_BPartner );
            KeyNamePair Product = ( KeyNamePair )xMatchedTable.getValueAt( row,I_Product );

            log.fine( "BPartner=" + BPartner + " - Product=" + Product );

            //

            if( sameBPartner.isSelected()) {
                m_sql.append( " AND hdr.C_BPartner_ID=" ).append( BPartner.getKey());
            }

            if( sameProduct.isSelected()) {
                m_sql.append( " AND lin.M_Product_ID=" ).append( Product.getKey());
            }

            // calculate qty

            double docQty = (( Double )xMatchedTable.getValueAt( row,I_QTY )).doubleValue();
            double matchedQty = (( Double )xMatchedTable.getValueAt( row,I_MATCHED )).doubleValue();

            qty = docQty - matchedQty;

            if( sameQty.isSelected()) {
                m_sql.append( " AND " ).append( m_qtyColumn ).append( "=" ).append( docQty );
            }
            // ** Load Table **
            log.fine("Y aqui antes de cargar la jtabla y la msqllllll");
            tableLoad( xMatchedToTable );
        }

        // Display To be Matched Qty

        m_xMatched = new BigDecimal( qty );
        xMatched.setValue( m_xMatched );
        xMatchedTo.setValue( Env.ZERO );
        difference.setValue( m_xMatched );

        // Status Info

        statusBar.setStatusLine( matchFrom.getSelectedItem().toString() + "# = " + xMatchedTable.getRowCount() + " - " + matchTo.getSelectedItem().toString() + "# = " + xMatchedToTable.getRowCount(),xMatchedToTable.getRowCount() == 0 );
        statusBar.setStatusDB( 0 );
    }    // cmd_seachTo

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {
      /*  if( e.getColumn() != 0 ) {
            return;
        }

        log.config( "Row=" + e.getFirstRow() + "-" + e.getLastRow() + ", Col=" + e.getColumn() + ", Type=" + e.getType());
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        // Matched From

        int         matchedRow = xMatchedTable.getSelectedRow();
        KeyNamePair Product    = ( KeyNamePair )xMatchedTable.getValueAt( matchedRow,5 );

        // Matched To

        double qty    = 0.0;
        int    noRows = 0;

        for( int row = 0;row < xMatchedToTable.getRowCount();row++ ) {
            IDColumn id = ( IDColumn )xMatchedToTable.getValueAt( row,0 );

            if( (id != null) && id.isSelected()) {
                KeyNamePair ProductCompare = ( KeyNamePair )xMatchedToTable.getValueAt( row,5 );
                log.fine("Antes del casqueeee, product="+Product+" y productcompare="+ProductCompare+", y rowcount="+xMatchedToTable.getRowCount());
                
                 if( Product.getKey() != ProductCompare.getKey()) {
                    id.setSelected( false );
                } else {
                    if( matchMode.getSelectedIndex() == MODE_NOTMATCHED ) {
                        qty += (( Double )xMatchedToTable.getValueAt( row,I_QTY )).doubleValue();    // doc
                    }

                    qty -= (( Double )xMatchedToTable.getValueAt( row,I_MATCHED )).doubleValue();    // matched
                    noRows++;
                }
            }
        }

        // update qualtities

        m_xMatchedTo = new BigDecimal( qty );
        xMatchedTo.setValue( m_xMatchedTo );
        difference.setValue( m_xMatched.subtract( m_xMatchedTo ));
        bProcess.setEnabled( noRows != 0 );
        setCursor( Cursor.getDefaultCursor());

        // Status

        statusBar.setStatusDB( noRows );*/
    }    // tableChanged

    /**
     * Descripción de Método
     *
     *
     * @param display
     * @param matchToType
     */

    private void tableInit( int display,int matchToType ) {
        boolean matched = matchMode.getSelectedIndex() == MODE_MATCHED;

        log.config( "Display=" + m_matchOptions[ display ] + ", MatchTo=" + m_matchOptions[ matchToType ] + ", Matched=" + matched );
        m_sql = new StringBuffer();

        if( display == MATCH_INVOICE ) {
            m_dateColumn = "hdr.DateInvoiced";
            m_qtyColumn  = "lin.QtyInvoiced";
            m_sql.append( "SELECT hdr.C_Invoice_ID,hdr.DocumentNo, hdr.DateInvoiced, bp.Name,hdr.C_BPartner_ID," + " lin.Line,lin.C_InvoiceLine_ID, p.Name,lin.M_Product_ID," + " lin.QtyInvoiced,SUM(COALESCE(mi.Qty,0)) " + "FROM C_Invoice hdr" + " INNER JOIN C_BPartner bp ON (hdr.C_BPartner_ID=bp.C_BPartner_ID)" + " INNER JOIN C_InvoiceLine lin ON (hdr.C_Invoice_ID=lin.C_Invoice_ID)" + " INNER JOIN M_Product p ON (lin.M_Product_ID=p.M_Product_ID)" + " INNER JOIN C_DocType dt ON (hdr.C_DocType_ID=dt.C_DocType_ID AND dt.DocBaseType IN ('API','APC'))" + " FULL JOIN M_MatchInv mi ON (lin.C_InvoiceLine_ID=mi.C_InvoiceLine_ID) " + "WHERE hdr.DocStatus IN ('CO','CL')" );
            m_groupBy = " GROUP BY hdr.C_Invoice_ID,hdr.DocumentNo,hdr.DateInvoiced,bp.Name,hdr.C_BPartner_ID," + " lin.Line,lin.C_InvoiceLine_ID,p.Name,lin.M_Product_ID,lin.QtyInvoiced " + "HAVING " + ( matched
                    ?"0"
                    :"lin.QtyInvoiced" ) + "<>SUM(COALESCE(mi.Qty,0))";
        } else if( display == MATCH_ORDER ) {
            m_dateColumn = "hdr.DateOrdered";
            m_qtyColumn  = "lin.QtyOrdered";
            m_sql.append( "SELECT hdr.C_Order_ID,hdr.DocumentNo, hdr.DateOrdered, bp.Name,hdr.C_BPartner_ID," + " lin.Line,lin.C_OrderLine_ID, p.Name,lin.M_Product_ID," + " lin.QtyOrdered,SUM(COALESCE(mo.Qty,0)) " + "FROM C_Order hdr" + " INNER JOIN C_BPartner bp ON (hdr.C_BPartner_ID=bp.C_BPartner_ID)" + " INNER JOIN C_OrderLine lin ON (hdr.C_Order_ID=lin.C_Order_ID)" + " INNER JOIN M_Product p ON (lin.M_Product_ID=p.M_Product_ID)" + " INNER JOIN C_DocType dt ON (hdr.C_DocType_ID=dt.C_DocType_ID AND dt.DocBaseType='POO')" + " FULL JOIN M_MatchPO mo ON (lin.C_OrderLine_ID=mo.C_OrderLine_ID) " + "WHERE mo." ).append( (matchToType == MATCH_SHIPMENT)
                    ?"C_InvoiceLine_ID"
                    :"M_InOutLine_ID" ).append( " IS NULL" + " AND hdr.DocStatus IN ('CO','CL')" );
            m_groupBy = " GROUP BY hdr.C_Order_ID,hdr.DocumentNo,hdr.DateOrdered,bp.Name,hdr.C_BPartner_ID," + " lin.Line,lin.C_OrderLine_ID,p.Name,lin.M_Product_ID,lin.QtyOrdered " + "HAVING " + ( matched
                    ?"0"
                    :"lin.QtyOrdered" ) + "<>SUM(COALESCE(mo.Qty,0))";
        } else    // Shipment
        {
            m_dateColumn = "hdr.MovementDate";
            m_qtyColumn  = "lin.MovementQty";
            m_sql.append( "SELECT hdr.M_InOut_ID,hdr.DocumentNo, hdr.MovementDate, bp.Name,hdr.C_BPartner_ID," + " lin.Line,lin.M_InOutLine_ID, p.Name,lin.M_Product_ID," + " lin.MovementQty,SUM(COALESCE(m.Qty,0)) " + "FROM M_InOut hdr" + " INNER JOIN C_BPartner bp ON (hdr.C_BPartner_ID=bp.C_BPartner_ID)" + " INNER JOIN M_InOutLine lin ON (hdr.M_InOut_ID=lin.M_InOut_ID)" + " INNER JOIN M_Product p ON (lin.M_Product_ID=p.M_Product_ID)" + " INNER JOIN C_DocType dt ON (hdr.C_DocType_ID = dt.C_DocType_ID AND dt.DocBaseType='MMR')" + " FULL JOIN " ).append( (matchToType == MATCH_ORDER)
                    ?"M_MatchPO"
                    :"M_MatchInv" ).append( " m ON (lin.M_InOutLine_ID=m.M_InOutLine_ID) " + "WHERE hdr.DocStatus IN ('CO','CL')" );
            m_groupBy = " GROUP BY hdr.M_InOut_ID,hdr.DocumentNo,hdr.MovementDate,bp.Name,hdr.C_BPartner_ID," + " lin.Line,lin.M_InOutLine_ID,p.Name,lin.M_Product_ID,lin.MovementQty " + "HAVING " + ( matched
                    ?"0"
                    :"lin.MovementQty" ) + "<>SUM(COALESCE(m.Qty,0))";
        }

        // Log.trace(7, "VMatch.tableInit", m_sql + "\n" + m_groupBy);

    }    // tableInit

    /**
     * Descripción de Método
     *
     *
     * @param table
     */

    private void tableLoad( MiniTable table ) {
        log.finest( "SQL en tableLoad en VMatch="+m_sql + " - " + m_groupBy );

        String sql = MRole.getDefault().addAccessSQL( m_sql.toString(),"hdr",MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO ) + m_groupBy;

        log.finest("La autentica sql que saca pa la tabla que no carga"+ sql );

        try {
        	//Modificado por ConSerTi
        	PreparedStatement pstmt = null;
        	pstmt = DB.prepareStatement( sql);
          //  Statement stmt = DB.createStatement();
          //  ResultSet rs   = stmt.executeQuery( sql );
        	ResultSet rs   = pstmt.executeQuery();
            table.loadTable( rs );
            //stmt.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }
    }    // tableLoad

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     * @param M_InOutLine_ID
     * @param Line_ID
     * @param qty
     *
     * @return
     */

    private boolean createMatchRecord( boolean invoice,int M_InOutLine_ID,int Line_ID,BigDecimal qty ) {
        if( qty.compareTo( Env.ZERO ) == 0 ) {
            return true;
        }

        log.fine( "IsInvoice=" + invoice + ", M_InOutLine_ID=" + M_InOutLine_ID + ", Line_ID=" + Line_ID + ", Qty=" + qty );

        //

        boolean success = false;

        if( invoice ) {

            // Update Invoice Line

            MInvoiceLine iLine = new MInvoiceLine( Env.getCtx(),Line_ID,null );

            iLine.setM_InOutLine_ID( M_InOutLine_ID );
            iLine.save();

            // Create Shipment - Invoice Link

            if( iLine.getM_Product_ID() != 0 ) {
                MMatchInv match = new MMatchInv( iLine,null,qty );

                match.setM_InOutLine_ID( M_InOutLine_ID );

                if( match.save()) {
                    success = true;
                } else {
                    log.log( Level.SEVERE,"Inv Match not created: " + match );
                }
            } else {
                success = true;
            }

            // Create PO - Invoice Link = corrects PO

            if( (iLine.getC_OrderLine_ID() != 0) && (iLine.getM_Product_ID() != 0) ) {
                MMatchPO matchPO = new MMatchPO( iLine,null,qty );

                matchPO.setM_InOutLine_ID( M_InOutLine_ID );

                if( !matchPO.save()) {
                    log.log( Level.SEVERE,"PO(Inv) Match not created: " + matchPO );
                }
            }
        } else    // Order
        {

            // Update Shipment Line

            MInOutLine sLine = new MInOutLine( Env.getCtx(),M_InOutLine_ID,null );

            sLine.setC_OrderLine_ID( Line_ID );
            sLine.save();

            // Update Order Line

            MOrderLine oLine = new MOrderLine( Env.getCtx(),Line_ID,null );

            if( oLine.getID() != 0 )    // other in MInOut.completeIt
            {
                oLine.setQtyReserved( oLine.getQtyReserved().subtract( qty ));

                if( !oLine.save()) {
                    log.severe( "QtyReserved not updated - C_OrderLine_ID=" + Line_ID );
                }
            }

            // Create PO - Shipment Link

            if( sLine.getM_Product_ID() != 0 ) {
                MMatchPO match = new MMatchPO( sLine,null,qty );

                if( !match.save()) {
                    log.log( Level.SEVERE,"PO Match not created: " + match );
                } else {
                    success = true;

                    // Correct Ordered Qty for Stocked Products (see MOrder.reserveStock / MInOut.processIt)

                    if( (sLine.getProduct() != null) && sLine.getProduct().isStocked()) {
                        success = MStorage.add( Env.getCtx(),sLine.getM_Warehouse_ID(),sLine.getM_Locator_ID(),sLine.getM_Product_ID(),sLine.getM_AttributeSetInstance_ID(),oLine.getM_AttributeSetInstance_ID(),null,null,qty.negate(),null );
                    }
                }
            } else {
                success = true;
            }
        }

        return success;
    }    // createMatchRecord
    
    private void cmd_matchMode() {
    	int modeIndex = matchMode.getSelectedIndex();
    	if (modeIndex == 0)
    		bProcess.setText(MSG_MATCH);
    	else
    		bProcess.setText(MSG_REVERT);
    }
}    // VMatch



/*
 *  @(#)VMatch.java   02.07.07
 * 
 *  Fin del fichero VMatch.java
 *  
 *  Versión 2.2
 *
 */
