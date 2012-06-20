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



package org.openXpertya.acct;

import java.awt.*;
import java.awt.datatransfer.StringSelection; /* Modif */
import java.awt.event.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;

import org.openXpertya.apps.*;
import org.openXpertya.apps.ConfirmPanel.DialogButton;
import org.openXpertya.apps.search.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.model.*;
import org.openXpertya.report.core.*;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AcctViewer extends JFrame implements ActionListener,ChangeListener {

    /**
     * Constructor de la clase ...
     *
     */

    public AcctViewer() {
        this( 0,0,0 );
    }    // AcctViewer

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_Client_ID
     * @param AD_Table_ID
     * @param Record_ID
     */

    public AcctViewer( int AD_Client_ID,int AD_Table_ID,int Record_ID ) {
        super( Msg.getMsg( Env.getCtx(),"AcctViewer" ));
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        m_data = new AcctViewerData( Env.getCtx(),Env.createWindowNo( this ),AD_Client_ID,AD_Table_ID );

        try {
            jbInit();
            dynInit( AD_Table_ID,Record_ID );
            AEnv.showCenterScreen( this );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"AcctViewer",e );
            dispose();
        }
    }    // AcctViewer

    /** Descripción de Campos */

    private AcctViewerData m_data = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AcctViewer.class );

    //

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();

    /** Descripción de Campos */

    private CPanel query = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private JScrollPane result = new JScrollPane();

    /** Descripción de Campos */

    private ResultTable table = new ResultTable();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private CButton bQuery = new CButton();

    /** Descripción de Campos */

    private CButton bPrint = new CButton();

    /** Descripción de Campos */

    private CLabel statusLine = new CLabel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private BorderLayout queryLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel selectionPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel displayPanel = new CPanel();

    /** Descripción de Campos */

    private TitledBorder displayBorder;

    /** Descripción de Campos */

    private TitledBorder selectionBorder;

    /** Descripción de Campos */

    private GridBagLayout displayLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CCheckBox displayQty = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox displaySourceAmt = new CCheckBox();

    /** Descripción de Campos */

    private CPanel graphPanel = new CPanel();

    /** Descripción de Campos */

    private CCheckBox displayDocumentInfo = new CCheckBox();

    /** Descripción de Campos */

    private CLabel lSort = new CLabel();

    /** Descripción de Campos */

    private CComboBox sortBy1 = new CComboBox();

    /** Descripción de Campos */

    private CComboBox sortBy2 = new CComboBox();

    /** Descripción de Campos */

    private CComboBox sortBy3 = new CComboBox();

    /** Descripción de Campos */

    private CCheckBox group1 = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox group2 = new CCheckBox();

    /** Descripción de Campos */

    private CCheckBox group3 = new CCheckBox();

    /** Descripción de Campos */

    private CLabel lGroup = new CLabel();

    /** Descripción de Campos */

    private GridBagLayout selectionLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CComboBox selAcctSchema = new CComboBox();

    /** Descripción de Campos */

    private CComboBox selPostingType = new CComboBox();

    /** Descripción de Campos */

    private CCheckBox selDocument = new CCheckBox();

    /** Descripción de Campos */

    private CComboBox selTable = new CComboBox();

    /** Descripción de Campos */

    private CButton selRecord = new CButton();

    /** Descripción de Campos */

    private CLabel lOrg = new CLabel();

    /** Descripción de Campos */

    private CComboBox selOrg = new CComboBox();

    /** Descripción de Campos */

    private CLabel lAcct = new CLabel();

    /** Descripción de Campos */

    private CButton selAcct = new CButton();

    /** Descripción de Campos */

    private CLabel lDate = new CLabel();

    /** Descripción de Campos */

    private CLabel lacctSchema = new CLabel();

    /** Descripción de Campos */

    private CLabel lpostingType = new CLabel();

    /** Descripción de Campos */

    private VDate selDateFrom = new VDate( "DateFrom",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateFrom" ));

    /** Descripción de Campos */

    private VDate selDateTo = new VDate( "DateTo",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"DateTo" ));

    /** Descripción de Campos */

    private CLabel lsel1 = new CLabel();

    /** Descripción de Campos */

    private CLabel lsel2 = new CLabel();

    /** Descripción de Campos */

    private CLabel lsel3 = new CLabel();

    /** Descripción de Campos */

    private CLabel lsel4 = new CLabel();

    /** Descripción de Campos */

    private CLabel lsel5 = new CLabel();

    /** Descripción de Campos */

    private CLabel lsel6 = new CLabel();

    /** Descripción de Campos */

    private CLabel lsel7 = new CLabel();

    /** Descripción de Campos */

    private CLabel lsel8 = new CLabel();

    /** Descripción de Campos */

    private CButton sel1 = new CButton();

    /** Descripción de Campos */

    private CButton sel2 = new CButton();

    /** Descripción de Campos */

    private CButton sel3 = new CButton();

    /** Descripción de Campos */

    private CButton sel4 = new CButton();

    /** Descripción de Campos */

    private CButton sel5 = new CButton();

    /** Descripción de Campos */

    private CButton sel6 = new CButton();

    /** Descripción de Campos */

    private CButton sel7 = new CButton();

    /** Descripción de Campos */

    private CButton sel8 = new CButton();

    /** Descripción de Campos */

    private CButton bRePost = new CButton();

    /** Descripción de Campos */

    private CComboBox sortBy4 = new CComboBox();

    /** Descripción de Campos */

    private CCheckBox group4 = new CCheckBox();

	private DialogButton bZoom = new DialogButton ("Zoom", "Zoom", Env.getImageIcon("Zoom24.gif"), KeyEvent.VK_Z);

	private CButton bCopyToClipboard = new CButton(); /* Agregado */
	
    /**
	 * Static Init.
     *
	 * <pre>
	 * -mainPanel - tabbedPane - query - result - graphPanel
	 * </pre>
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );

        ImageIcon ii = new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/InfoAccount16.gif" ));

        setIconImage( ii.getImage());

        //

        mainLayout.setHgap( 5 );
        mainLayout.setVgap( 5 );
        mainPanel.setLayout( mainLayout );
        selectionPanel.setLayout( selectionLayout );
        this.getContentPane().add( mainPanel,BorderLayout.CENTER );
        mainPanel.add( tabbedPane,BorderLayout.CENTER );

        // Selection

        selectionBorder = new TitledBorder( BorderFactory.createEtchedBorder( Color.white,new Color( 148,145,140 )),Msg.getMsg( Env.getCtx(),"Selection" ));
        selectionPanel.setBorder( selectionBorder );
        lacctSchema.setLabelFor( selAcctSchema );
        lacctSchema.setText( Msg.translate( Env.getCtx(),"C_AcctSchema_ID" ));
        lpostingType.setLabelFor( selPostingType );
        lpostingType.setText( Msg.translate( Env.getCtx(),"PostingType" ));
        selDocument.setText( Msg.getMsg( Env.getCtx(),"SelectDocument" ));
        selDocument.addActionListener( this );
        lOrg.setLabelFor( selOrg );
        lOrg.setText( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
        lAcct.setLabelFor( selAcct );
        lAcct.setText( Msg.translate( Env.getCtx(),"Account_ID" ));
        lDate.setLabelFor( selDateFrom );
        lDate.setText( Msg.translate( Env.getCtx(),"DateAcct" ));
        lsel1.setLabelFor( sel1 );
        lsel2.setLabelFor( sel2 );
        lsel3.setLabelFor( sel3 );
        lsel4.setLabelFor( sel4 );
        lsel5.setLabelFor( sel5 );
        lsel6.setLabelFor( sel6 );
        lsel7.setLabelFor( sel7 );
        lsel8.setLabelFor( sel8 );

        // Display

        displayBorder = new TitledBorder( BorderFactory.createEtchedBorder( Color.white,new Color( 148,145,140 )),Msg.getMsg( Env.getCtx(),"Display" ));
        displayPanel.setBorder( displayBorder );
        displayPanel.setLayout( displayLayout );
        displayQty.setText( Msg.getMsg( Env.getCtx(),"DisplayQty" ));
        displaySourceAmt.setText( Msg.getMsg( Env.getCtx(),"DisplaySourceInfo" ));
        displayDocumentInfo.setText( Msg.getMsg( Env.getCtx(),"DisplayDocumentInfo" ));
        displayDocumentInfo.setValue(true); /* Agregado */
        lSort.setText( Msg.getMsg( Env.getCtx(),"SortBy" ));
        lGroup.setText( Msg.getMsg( Env.getCtx(),"GroupBy" ));

        //

        displayPanel.add( displaySourceAmt,new GridBagConstraints( 0,1,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( displayDocumentInfo,new GridBagConstraints( 0,0,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( lSort,new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( sortBy1,new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( sortBy2,new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( group1,new GridBagConstraints( 1,5,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( group2,new GridBagConstraints( 1,6,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( lGroup,new GridBagConstraints( 1,4,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( displayQty,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        displayPanel.add( sortBy3,new GridBagConstraints( 0,7,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( group3,new GridBagConstraints( 1,7,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( sortBy4,new GridBagConstraints( 0,8,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        displayPanel.add( group4,new GridBagConstraints( 1,8,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));

        //

        selectionPanel.add( lacctSchema,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        selectionPanel.add( selAcctSchema,new GridBagConstraints( 1,0,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        selectionPanel.add( selDocument,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 10,5,10,5 ),0,0 ));
        selectionPanel.add( selTable,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 10,5,10,5 ),0,0 ));
        selectionPanel.add( selRecord,new GridBagConstraints( 2,1,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 10,5,10,5 ),0,0 ));
        selectionPanel.add( lpostingType,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( selPostingType,new GridBagConstraints( 1,2,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lDate,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( selDateFrom,new GridBagConstraints( 1,3,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( selDateTo,new GridBagConstraints( 2,3,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lOrg,new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,0,0,5 ),0,0 ));
        selectionPanel.add( selOrg,new GridBagConstraints( 1,4,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lAcct,new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( selAcct,new GridBagConstraints( 1,5,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel1,new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel2,new GridBagConstraints( 0,7,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel3,new GridBagConstraints( 0,8,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel1,new GridBagConstraints( 1,6,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel2,new GridBagConstraints( 1,7,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel3,new GridBagConstraints( 1,8,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel4,new GridBagConstraints( 0,9,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel4,new GridBagConstraints( 1,9,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel5,new GridBagConstraints( 0,10,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel5,new GridBagConstraints( 1,10,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel6,new GridBagConstraints( 0,11,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel6,new GridBagConstraints( 1,11,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel7,new GridBagConstraints( 0,12,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel7,new GridBagConstraints( 1,12,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( lsel8,new GridBagConstraints( 0,13,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));
        selectionPanel.add( sel8,new GridBagConstraints( 1,13,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,0,5 ),0,0 ));

        //

        queryLayout.setHgap( 5 );
        queryLayout.setVgap( 5 );
        query.setLayout( queryLayout );
        query.add( selectionPanel,BorderLayout.CENTER );
        query.add( displayPanel,BorderLayout.EAST );

        //

//              tabbedPane.add(query,        Msg.getMsg(Env.getCtx(), "ViewerQuery"));
//              tabbedPane.add(result,       Msg.getMsg(Env.getCtx(), "ViewerResult"));
//              tabbedPane.add(graphPanel,   Msg.getMsg(Env.getCtx(), "ViewerGraph"));

        tabbedPane.add( query,Msg.getMsg( Env.getCtx(),"ViewerQuery" ));
        tabbedPane.add( result,Msg.getMsg( Env.getCtx(),"ViewerResult" ));

//              tabbedPane.add(graphPanel,   Msg.getMsg(Env.getCtx(), "ViewerGraph"));

        tabbedPane.addChangeListener( this );
        result.getViewport().add( table,null );

        // South

        southLayout.setHgap( 5 );
        southLayout.setVgap( 5 );
        southPanel.setLayout( southLayout );
        statusLine.setForeground( Color.blue );
        statusLine.setBorder( BorderFactory.createLoweredBevelBorder());
        southPanel.add( statusLine,BorderLayout.CENTER );
        bRePost.setText( Msg.getMsg( Env.getCtx(),"RePost" ));
        bRePost.setToolTipText( Msg.getMsg( Env.getCtx(),"RePostInfo" ));
        bRePost.addActionListener( this );
        bRePost.setVisible( false );
        southPanel.add( bRePost,BorderLayout.WEST );

        //
		bZoom.addActionListener(this);
		
		/* Agregado */
		
		bZoom.setEnabled(false);
		
		bCopyToClipboard.setIcon(Env.getImageIcon("Copy16.gif"));
		bCopyToClipboard.addActionListener(this);
		bCopyToClipboard.setToolTipText(Msg.getMsg(Env.getCtx(), "Copy"));		
		
		/* Agregado */
		
		//
        bQuery.setIcon( Env.getImageIcon( "Refresh16.gif" ));
        bQuery.setToolTipText( Msg.getMsg( Env.getCtx(),"QueryExecute" ));
        bQuery.addActionListener( this );
        bPrint.setIcon( Env.getImageIcon( "Print16.gif" ));
        bPrint.setToolTipText( Msg.getMsg( Env.getCtx(),"Print" ));
        bPrint.addActionListener( this );

        JPanel rightSide = new JPanel( new FlowLayout( FlowLayout.TRAILING,0,0 ));
        rightSide.add(bCopyToClipboard); /* Agregado */
		//
		rightSide.add(bZoom);
		//		
        rightSide.add( bPrint );
        rightSide.add( bQuery );
        southPanel.add( rightSide,BorderLayout.EAST );
        this.getContentPane().add( southPanel,BorderLayout.SOUTH );

        //

    }    // jbInit

    /**
	 * Dynamic Init
     *
     * @param AD_Table_ID
     * @param Record_ID
     */

    private void dynInit( int AD_Table_ID,int Record_ID ) {
        ImageIcon iFind = new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/Find16.gif" ));

        m_data.fillAcctSchema( selAcctSchema );

        //

        m_data.fillTable( selTable );
        selTable.addActionListener( this );
        selRecord.setIcon( iFind );
        selRecord.addActionListener( this );
        selRecord.setText( "" );

        //

        m_data.fillPostingType( selPostingType );

        // Sort Options

        ValueNamePair vn = new ValueNamePair( "","" );

        sortBy1.addItem( vn );
        sortBy2.addItem( vn );
        sortBy3.addItem( vn );
        sortBy4.addItem( vn );
        vn = new ValueNamePair( "DateAcct",Msg.translate( Env.getCtx(),"DateAcct" ));
        sortBy1.addItem( vn );
        sortBy2.addItem( vn );
        sortBy3.addItem( vn );
        sortBy4.addItem( vn );
        vn = new ValueNamePair( "DateTrx",Msg.translate( Env.getCtx(),"DateTrx" ));
        sortBy1.addItem( vn );
        sortBy2.addItem( vn );
        sortBy3.addItem( vn );
        sortBy4.addItem( vn );
        vn = new ValueNamePair( "C_Period_ID",Msg.translate( Env.getCtx(),"C_Period_ID" ));
        sortBy1.addItem( vn );
        sortBy2.addItem( vn );
        sortBy3.addItem( vn );
        sortBy4.addItem( vn );

        // Mandatory Elements

        m_data.fillOrg( selOrg );
        selAcct.setActionCommand( "Account_ID" );
        selAcct.addActionListener( this );
        selAcct.setText( "" );
        selAcct.setIcon( iFind );

        //

        CLabel[]             labels         = new CLabel[] {
            lsel1,lsel2,lsel3,lsel4,lsel5,lsel6,lsel7,lsel8
        };
        CButton[]            buttons        = new CButton[] {
            sel1,sel2,sel3,sel4,sel5,sel6,sel7,sel8
        };
        int                  selectionIndex = 0;
        MAcctSchemaElement[] elements       = m_data.ASchema.getAcctSchemaElements();

        for( int i = 0;(i < elements.length) && (selectionIndex < labels.length);i++ ) {
            MAcctSchemaElement ase        = elements[ i ];
            String             columnName = ase.getColumnName();

            // Add Sort Option

            vn = new ValueNamePair( columnName,Msg.translate( Env.getCtx(),columnName ));
            sortBy1.addItem( vn );
            sortBy2.addItem( vn );
            sortBy3.addItem( vn );
            sortBy4.addItem( vn );

            // Additional Elements

            if( !ase.isElementType( MAcctSchemaElement.ELEMENTTYPE_Org ) &&!ase.isElementType( MAcctSchemaElement.ELEMENTTYPE_Account )) {
                labels[ selectionIndex ].setText( Msg.translate( Env.getCtx(),columnName ));
                buttons[ selectionIndex ].setActionCommand( columnName );
                buttons[ selectionIndex ].addActionListener( this );
                buttons[ selectionIndex ].setIcon( iFind );
                buttons[ selectionIndex ].setText( "" );
                selectionIndex++;
            }
        }

        // don't show remaining

        while( selectionIndex < labels.length ) {
            labels[ selectionIndex ].setVisible( false );
            buttons[ selectionIndex++ ].setVisible( false );
        }

        // Document Select

        boolean haveDoc = (AD_Table_ID != 0) && (Record_ID != 0);

        selDocument.setSelected( haveDoc );
        actionDocument();
        actionTable();
        statusLine.setText( " " + Msg.getMsg( Env.getCtx(),"ViewerOptions" ));

        // Initial Query

        if( haveDoc ) {
            m_data.AD_Table_ID = AD_Table_ID;
            m_data.Record_ID   = Record_ID;
            actionQuery();
        } else
    	{
        	restoreUserPreferences();	/* Agregado */
    	}
    }    // dynInit

    // Pone los ultimos valores que utilizo el usuario en la ventana (Agregado)
    
    private void restoreUserPreferences() {
    	MPreference pref = MPreference.getUserPreference(Env.getCtx(), "AcctViewerFDesde", null);
    	if (pref!=null)
    	{
    		try {
    			selDateFrom.setValue(pref.getValue());
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} 

    	pref = MPreference.getUserPreference(Env.getCtx(), "AcctViewerFHasta", null);
    	if (pref!=null)
    	{
    		try {
    			selDateTo.setValue(pref.getValue());
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}

    	pref = MPreference.getUserPreference(Env.getCtx(), "AcctViewerOrg", null);
    	if (pref!=null)
    	{
    		try {
    			//selOrg.setSelectedItem(pref.getValue());

    			for (int i = 0; i < selOrg.getItemCount(); i++) {
    				KeyNamePair p = (KeyNamePair)selOrg.getItemAt(i); 
    				if (p.getName().equalsIgnoreCase(pref.getValue())) {
    					selOrg.setSelectedIndex(i);
    					break;
    				}
    			}

    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
	 * Dispose
     */

    public void dispose() {
        m_data.dispose();
        m_data = null;
        super.dispose();
    }    // dispose;

    /**
	 * Tab Changed
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {

        // log.info( "AcctViewer.stateChanged");

        bRePost.setVisible( m_data.documentQuery && (tabbedPane.getSelectedIndex() == 1) );
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( e.getActionCommand());

        if( e.getSource() == bQuery ) {
            actionQuery();
        } else if( e.getSource() == selDocument ) {
            actionDocument();
        } else if( e.getSource() == selTable ) {
            actionTable();
        } else if( e.getSource() == bRePost ) {
            actionRePost();
        } else if( e.getSource() == bPrint ) {
            PrintScreenPainter.printScreen( this );
        } else if( e.getSource() == bCopyToClipboard ) { /*Agregado*/
        	copyToClipBoard();            
        } else if (e.getSource() == bZoom)
			zoom();		
        // InfoButtons

        else if( e.getSource() instanceof CButton ) {
            actionButton(( CButton )e.getSource());
        }
    }    // actionPerformed

    private void copyToClipBoard() { /*Agregado*/
    		
    	String valores = table.getClipboardData();
    	StringSelection ss = new StringSelection(valores);
    	Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    	
    }
    
    /**
	 * Query
     */

    private void actionQuery() {

        // Parameter Info

        StringBuffer para = new StringBuffer();

        // Reset Selection Data

        m_data.C_AcctSchema_ID = 0;
        m_data.AD_Org_ID       = 0;

        // Save Selection Choices

        KeyNamePair kp = ( KeyNamePair )selAcctSchema.getSelectedItem();

        if( kp != null ) {
            m_data.C_AcctSchema_ID = kp.getKey();
        }
        
        // Grabar preferencias para futuras busquedas
        // Default values for Fecha desde y hasta
        
        boolean haveDoc = (m_data.AD_Org_ID != 0) && (m_data.Record_ID != 0);
        if (!haveDoc) { 
//        	saveUserPreferences(); /*Agregado*/
        }
        // Fin modificacion

        para.append( "C_AcctSchema_ID=" ).append( m_data.C_AcctSchema_ID );

        //

        ValueNamePair vp = ( ValueNamePair )selPostingType.getSelectedItem();

        m_data.PostingType = vp.getValue();
        para.append( ", PostingType=" ).append( m_data.PostingType );

        // Document

        m_data.documentQuery = selDocument.isSelected();
        para.append( ", DocumentQuery=" ).append( m_data.documentQuery );

        if( selDocument.isSelected()) {
            if( (m_data.AD_Table_ID == 0) || (m_data.Record_ID == 0) ) {
                ADialog.info(0, this, "MustSelectDocument");
            	return;
            }

            para.append( ", AD_Table_ID=" ).append( m_data.AD_Table_ID ).append( ", Record_ID=" ).append( m_data.Record_ID );
        } else {
            m_data.DateFrom = ( Timestamp )selDateFrom.getValue();
            para.append( ", DateFrom=" ).append( m_data.DateFrom );
            m_data.DateTo = ( Timestamp )selDateTo.getValue();
            para.append( ", DateTo=" ).append( m_data.DateTo );
            kp = ( KeyNamePair )selOrg.getSelectedItem();

            if( kp != null ) {
                m_data.AD_Org_ID = kp.getKey();
            }

            para.append( ", AD_Org_ID=" ).append( m_data.AD_Org_ID );

            //

            Iterator it = m_data.whereInfo.values().iterator();

            while( it.hasNext()) {
                para.append( ", " ).append( it.next());
            }
        }

        // Save Display Choices

        m_data.displayQty = displayQty.isSelected();
        para.append( ", Display Qty=" ).append( m_data.displayQty );
        m_data.displaySourceAmt = displaySourceAmt.isSelected();
        para.append( ", Source=" ).append( m_data.displaySourceAmt );
        m_data.displayDocumentInfo = displayDocumentInfo.isSelected();
        para.append( ", Doc=" ).append( m_data.displayDocumentInfo );

        //

        m_data.sortBy1 = (( ValueNamePair )sortBy1.getSelectedItem()).getValue();
        m_data.group1 = group1.isSelected();
        para.append( " - Sorting: " ).append( m_data.sortBy1 ).append( "/" ).append( m_data.group1 );
        m_data.sortBy2 = (( ValueNamePair )sortBy2.getSelectedItem()).getValue();
        m_data.group2 = group2.isSelected();
        para.append( ", " ).append( m_data.sortBy2 ).append( "/" ).append( m_data.group2 );
        m_data.sortBy3 = (( ValueNamePair )sortBy3.getSelectedItem()).getValue();
        m_data.group3 = group3.isSelected();
        para.append( ", " ).append( m_data.sortBy3 ).append( "/" ).append( m_data.group3 );
        m_data.sortBy4 = (( ValueNamePair )sortBy4.getSelectedItem()).getValue();
        m_data.group4 = group4.isSelected();
        para.append( ", " ).append( m_data.sortBy4 ).append( "/" ).append( m_data.group4 );
        bQuery.setEnabled( false );
        statusLine.setText( " " + Msg.getMsg( Env.getCtx(),"Processing" ));
        log.config( "El para, es="+para.toString());
        Thread.yield();

        // Switch to Result pane
        tabbedPane.setSelectedIndex( 1 );

        // Set TableModel with Query
        log.fine("La query que se saca antes es:"+m_data.query().toString());
        table.setModel( m_data.query());
        bQuery.setEnabled( true );
		//
        // habilitado solo si selecciono la opcion de mostrar referencias
        bZoom.setEnabled(m_data.displayDocumentInfo);

		//
        statusLine.setText( " " + Msg.getMsg( Env.getCtx(),"ViewerOptions" ));
    }   // actionQuery

    // Graba las preferencias del usuario para restaurarlas al iniciar la ventana.
    
    private void saveUserPreferences() { /*Agregado*/
    	
    	MPreference pref = MPreference.getUserPreference(Env.getCtx(), "AcctViewerFDesde", null);
    	if (pref==null) {
    		pref = new MPreference(Env.getCtx(), "AcctViewerFDesde", "",null);
    	}
    	pref.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
    	pref.setValue(m_data.DateFrom.toString() );
    	pref.save();

    	pref = MPreference.getUserPreference(Env.getCtx(), "AcctViewerFHasta", null);
    	if (pref==null) {
    		pref = new MPreference(Env.getCtx(), "AcctViewerFHasta", "",null);
    	}
    	pref.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
    	pref.setValue(m_data.DateTo.toString() );
    	pref.save();

    	pref = MPreference.getUserPreference(Env.getCtx(), "AcctViewerOrg", null);
    	if (pref==null) {
    		pref = new MPreference(Env.getCtx(), "AcctViewerOrg", "",null);
    	}
    	pref.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
    	pref.setValue(selOrg.getValue().toString() );
    	pref.save();

    }
    
    /**
	 * Document selection
     */

    private void actionDocument() {
        boolean doc = selDocument.isSelected();

        selTable.setEnabled( doc );
        selRecord.setEnabled( doc );

        //

        selPostingType.setReadWrite( !doc );
        selDateFrom.setReadWrite( !doc );
        selDateTo.setReadWrite( !doc );
        selOrg.setEnabled( !doc );
        selAcct.setEnabled( !doc );
        sel1.setEnabled( !doc );
        sel2.setEnabled( !doc );
        sel3.setEnabled( !doc );
        sel4.setEnabled( !doc );
        sel5.setEnabled( !doc );
        sel6.setEnabled( !doc );
        sel7.setEnabled( !doc );
        sel8.setEnabled( !doc );
    }    // actionDocument

    /**
	 * Save Table selection (reset Record selection)
     */

    private void actionTable() {
        ValueNamePair vp = ( ValueNamePair )selTable.getSelectedItem();

        m_data.AD_Table_ID = (( Integer )m_data.tableInfo.get( vp.getValue())).intValue();
        log.config( vp.getValue() + " = " + m_data.AD_Table_ID );

        // Reset Record

        m_data.Record_ID = 0;
        selRecord.setText( "" );
        selRecord.setActionCommand( vp.getValue() + "_ID" );
    }    // actionTable

    /**
	 * Action Button
     *
     * @param button
	 *            pressed button
	 * @return ID
     */

    private int actionButton( CButton button ) {
        String keyColumn    = button.getActionCommand();
        String lookupColumn = keyColumn;

        if( keyColumn.equals( "Account_ID" ) || keyColumn.startsWith( "User" )) {
            lookupColumn = "C_ElementValue_ID";
        }

        String tableName = lookupColumn.substring( 0,lookupColumn.length() - 3 );
        Info info = Info.create( this,true,m_data.WindowNo,tableName,lookupColumn,"",false,"" );

        if( !info.loadedOK()) {
            info.dispose();
            info = null;
            button.setText( "" );
            m_data.whereInfo.put( keyColumn,"" );

            return 0;
        }

        info.show();

        String  selectSQL = info.getSelectedSQL();    // C_Project_ID=100 or ""
        Integer key       = ( Integer )info.getSelectedKey();

        info = null;

        if( (selectSQL == null) || (selectSQL.length() == 0) || (key == null) ) {
            button.setText( "" );
            m_data.whereInfo.put( keyColumn,"" );    // no query

            return 0;
        }

        // Save for query

        log.config( keyColumn + " - " + key );

        if( button == selRecord ) {    // Record_ID
            m_data.Record_ID = key.intValue();
        } else {
            m_data.whereInfo.put( keyColumn,keyColumn + "=" + key.intValue());
        }

        // Display Selection and resize

        button.setText( m_data.getButtonText( tableName,lookupColumn,selectSQL ));
        pack();

        return key.intValue();
    }    // actionButton

    /**
	 * RePost Record
     */

    private void actionRePost() {
        if( m_data.documentQuery && (m_data.AD_Table_ID != 0) && (m_data.Record_ID != 0) && ADialog.ask( m_data.WindowNo,this,"PostImmediate?" )) {
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
            AEnv.postImmediate( m_data.WindowNo,m_data.AD_Client_ID,m_data.AD_Table_ID,m_data.Record_ID,true );
            setCursor( Cursor.getDefaultCursor());
            actionQuery();
        }
    }    // actionRePost


	void zoom()
	{
		log.log(Level.INFO, "AcctViewer.zoom");
		Integer AD_Table_ID = getSelectedAD_Table_ID();
		Integer recordID = getSelectedRecordID();
		if(AD_Table_ID == null || recordID == null)
		{
			log.log( Level.INFO , "AcctViewer - Debe seleccionar un asiento contable !!");
			return;
		}
		AEnv.zoom(AD_Table_ID.intValue(), recordID.intValue());	//	SO
	}	//	zoom	

	protected Integer getSelectedRecordID()
	{
		int m_keyColumnIndex = table.getModel().getColumnCount()-1;
		int row = table.getSelectedRow();
		if (row != -1)
		{
			Object data = table.getModel().getValueAt(row, m_keyColumnIndex);
			if(data != null)
				return new Integer(data.toString());			
		}
		return null;
	}   //  getSelectedRowKey
	
	protected Integer getSelectedAD_Table_ID()
	{	
		int m_keyColumnIndex = table.getModel().getColumnCount()-2;
		int row = table.getSelectedRow();
		if (row != -1)
		{
			Object data = table.getModel().getValueAt(row, m_keyColumnIndex);
			if(data != null)
			   return new Integer(data.toString());
		}
		return null;
	}   //  getSelectedRowKey	
	

} //  AcctViewer