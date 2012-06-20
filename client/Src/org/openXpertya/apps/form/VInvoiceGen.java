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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextPane;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.ADialogDialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.print.ReportCtl;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoUtil;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VInvoiceGen extends CPanel implements FormPanel,ActionListener,VetoableChangeListener,ChangeListener,TableModelListener,ASyncProcess {

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "" );
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );

        try {
            fillPicks();
            jbInit();
            dynInit();
            frame.getContentPane().add( tabbedPane,BorderLayout.CENTER );
//          Añadido por ConSerTi para seleccionar todas las filas
            m_frame.getContentPane().add(allselectPane,BorderLayout.LINE_END);
            //Fin añadido
            frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"init",ex );
        }
    }    // init

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private boolean m_selectionActive = true;

    /** Descripción de Campos */

    private String m_whereClause;

    /** Descripción de Campos */

    private Object m_AD_Org_ID = null;

    /** Descripción de Campos */

    private Object m_C_BPartner_ID = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VInvoiceGen.class );

    //

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();

    /** Descripción de Campos */
//  Añadido por ConSerti, para poder seleccionar todas las filas a la vez.
    private CTabbedPane allselectPane = new CTabbedPane();
    private CPanel selNorthPanel_aux = new CPanel();
    //Fin añadido
    private int m_keyColumnIndex = -1;
    
    private CPanel selPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel selNorthPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout selPanelLayout = new BorderLayout();

    /** Descripción de Campos */

    private CLabel lOrg = new CLabel();

    /** Descripción de Campos */

    private VLookup fOrg;

    /** Descripción de Campos */

    private CLabel lBPartner = new CLabel();

    /** Descripción de Campos */

    private VLookup fBPartner;

    /** Descripción de Campos */

    private FlowLayout northPanelLayout = new FlowLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelSel = new ConfirmPanel( true,false,false,false,false,true,true );

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelGen = new ConfirmPanel( false,true,false,false,false,false,true );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    private CPanel genPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout genLayout = new BorderLayout();

    /** Descripción de Campos */

    private CTextPane info = new CTextPane();

    /** Descripción de Campos */

    private JScrollPane scrollPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable miniTable = new MiniTable();

    //  Añadido por ConSerti, para poder seleccionar todas las filas a la vez. 
    private CCheckBox automatico = new CCheckBox();
    //Fin añadido
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );

        //
        selNorthPanel_aux.setLayout(new BorderLayout());
        selPanel.setLayout( selPanelLayout );
        lOrg.setLabelFor( fOrg );
        lOrg.setText( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
        lBPartner.setLabelFor( fBPartner );
        lBPartner.setText( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        selNorthPanel.setLayout( northPanelLayout );
        northPanelLayout.setAlignment( FlowLayout.LEFT );
        tabbedPane.add( selPanel,Msg.getMsg( Env.getCtx(),"Select" ));
        selPanel.add( selNorthPanel,BorderLayout.NORTH );
        //Añadido por ConSerTi para la posicion de seleccionar todo
        selNorthPanel_aux.add(selNorthPanel,BorderLayout.SOUTH);  
        selPanel.add( selNorthPanel_aux,BorderLayout.NORTH );
        //Fin añadido
        selNorthPanel.add( lOrg,null );
        selNorthPanel.add( fOrg,null );
        selNorthPanel.add( lBPartner,null );
        selNorthPanel.add( fBPartner,null );
    
        selPanel.setName( "selPanel" );
        selPanel.add( confirmPanelSel,BorderLayout.SOUTH );
//Modificado por ConSerTi para seleccionar una tabla entera
        automatico.setText("Seleccionar Todos");  
        automatico.setSelected(true);
        automatico.addActionListener(this);
       // selPanel.add(automatico);
        automatico.setEnabled(true);
        selNorthPanel_aux.add(automatico,BorderLayout.NORTH);
        selPanel.add( selNorthPanel_aux,BorderLayout.NORTH );
//Fin Modificacion
        selPanel.add( scrollPane,BorderLayout.CENTER );
        scrollPane.getViewport().add( miniTable,null );
        confirmPanelSel.addActionListener( this );

        //

        tabbedPane.add( genPanel,Msg.getMsg( Env.getCtx(),"Generate" ));
        genPanel.setLayout( genLayout );
        genPanel.add( info,BorderLayout.CENTER );
        genPanel.setEnabled( false );
        info.setBackground( CompierePLAF.getFieldBackground_Inactive());
        info.setEditable( false );
        genPanel.add( confirmPanelGen,BorderLayout.SOUTH );
        confirmPanelGen.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void fillPicks() throws Exception {
        MLookup orgL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,2163,DisplayType.TableDir );

        fOrg = new VLookup( "AD_Org_ID",false,false,true,orgL );

        // lOrg.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));

        fOrg.addVetoableChangeListener( this );

        //

        MLookup bpL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,2762,DisplayType.Search );

        fBPartner = new VLookup( "C_BPartner_ID",false,false,true,bpL );

        // lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));

        fBPartner.addVetoableChangeListener( this );
    }    // fillPicks

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // create Columns
    	 m_keyColumnIndex = 0;
        miniTable.addColumn( "C_Order_ID" );
        miniTable.addColumn( "AD_Org_ID" );
        miniTable.addColumn( "C_DocType_ID" );
        miniTable.addColumn( "DocumentNo" );
        miniTable.addColumn( "C_BPartner_ID" );
        miniTable.addColumn( "DateOrdered" );
        miniTable.addColumn( "TotalLines" );

        //

        miniTable.setMultiSelection( true );
        miniTable.setRowSelectionAllowed( true );

        // set details

        miniTable.setColumnClass( 0,IDColumn.class,false," " );
        miniTable.setColumnClass( 1,String.class,true,Msg.translate( Env.getCtx(),"AD_Org_ID" ));
        miniTable.setColumnClass( 2,String.class,true,Msg.translate( Env.getCtx(),"C_DocType_ID" ));
        miniTable.setColumnClass( 3,String.class,true,Msg.translate( Env.getCtx(),"DocumentNo" ));
        miniTable.setColumnClass( 4,String.class,true,Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        miniTable.setColumnClass( 5,Timestamp.class,true,Msg.translate( Env.getCtx(),"DateOrdered" ));
        miniTable.setColumnClass( 6,BigDecimal.class,true,Msg.translate( Env.getCtx(),"TotalLines" ));

        //

        miniTable.autoSize();
        miniTable.getModel().addTableModelListener( this );

        // Info

        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"InvGenerateSel" ));
        statusBar.setStatusDB( " " );

        // Tabbed Pane Listener

        tabbedPane.addChangeListener( this );
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void executeQuery() {
        log.info( "" );

        int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT C_Order_ID, o.Name, dt.Name, DocumentNo, bp.Name, DateOrdered, TotalLines " + "FROM C_Invoice_Candidate_v ic, AD_Org o, C_BPartner bp, C_DocType dt " + "WHERE ic.AD_Org_ID=o.AD_Org_ID" + " AND ic.C_BPartner_ID=bp.C_BPartner_ID" + " AND ic.C_DocType_ID=dt.C_DocType_ID" + " AND ic.AD_Client_ID=?" );

        if( m_AD_Org_ID != null ) {
            sql.append( " AND ic.AD_Org_ID=" ).append( m_AD_Org_ID );
        }

        if( m_C_BPartner_ID != null ) {
            sql.append( " AND ic.C_BPartner_ID=" ).append( m_C_BPartner_ID );
        }

        //

        sql.append( " ORDER BY o.Name,bp.Name,DateOrdered" );

        // log.fine( "VInvoiceGen.executeQuery - AD_Client_ID=" + AD_Client_ID, sql.toString());

        // reset table

        int row = 0;

        miniTable.setRowCount( row );

        // Execute

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {

                // extend table

                miniTable.setRowCount( row + 1 );

                // set values

                miniTable.setValueAt( new IDColumn( rs.getInt( 1 )),row,0 );    // C_Order_ID
                miniTable.setValueAt( rs.getString( 2 ),row,1 );       // Org
                miniTable.setValueAt( rs.getString( 3 ),row,2 );       // DocType
                miniTable.setValueAt( rs.getString( 4 ),row,3 );       // Doc No
                miniTable.setValueAt( rs.getString( 5 ),row,4 );       // BPartner
                miniTable.setValueAt( rs.getTimestamp( 6 ),row,5 );    // DateOrdered
                miniTable.setValueAt( rs.getBigDecimal( 7 ),row,6 );    // TotalLines

                // prepare next

                row++;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        }

        //

        miniTable.autoSize();

        // statusBar.setStatusDB(String.valueOf(miniTable.getRowCount()));

    }    // executeQuery

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

//  Añadido por ConSerTi para seleccionar todas las columnas
    public void seleccionarTodos() {
        for( int i = 0;i < miniTable.getRowCount();i++ ) {
            IDColumn id = ( IDColumn )miniTable.getModel().getValueAt( i,0 );
                      id.setSelected(automatico.isSelected());
                      //miniTable.setValueAt( new Boolean( automatico.isSelected()),i,6 );

            miniTable.getModel().setValueAt( id,i,0 );
        }
    }
    //Fin añadido.
    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( "Cmd=" + e.getActionCommand());

        info.setText("Este es el evento:"+e);
//Añadido por ConSerTi, para seleccionar todas las filas.
        if (e.getSource().equals(automatico)){
            seleccionarTodos();
        }
//fin Añadido
        if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
            return;
        }
        else if(e.getActionCommand().equals(ConfirmPanel.A_OK))
        {
        	m_whereClause = saveSelection();
        	if( (m_whereClause.length() > 0) && m_selectionActive ) {
        		generateInvoices();
        	} else 
        		dispose();
        }
        else if(e.getActionCommand().equals(ConfirmPanel.A_ZOOM)){
        	zoom();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    void zoom() {
        log.info( "VInvoiceRemGen.zoom" );

        Integer C_Invoice_ID = getSelectedRowKey();
        log.fine("En el zoom y la id que coge es="+C_Invoice_ID);
        if( C_Invoice_ID == null ) {
            return;
        }
       
        AEnv.zoom( MOrder.Table_ID,C_Invoice_ID.intValue());
    }
    
    protected Integer getSelectedRowKey() {
        int row = miniTable.getSelectedRow();

        if( (row != -1) && (m_keyColumnIndex != -1) ) {
            Object data = miniTable.getModel().getValueAt( row,m_keyColumnIndex );

            if( data instanceof IDColumn ) {
                data = (( IDColumn )data ).getRecord_ID();
            }

            if( data instanceof Integer ) {
                return( Integer )data;
            }
        }

        return null;
    }    // aï¿½adido para posibilitar el zoom a pedidos
    
    public void vetoableChange( PropertyChangeEvent e ) {
        log.info( e.getPropertyName() + "=" + e.getNewValue());

        if( e.getPropertyName().equals( "AD_Org_ID" )) {
            m_AD_Org_ID = e.getNewValue();
        }

        if( e.getPropertyName().equals( "C_BPartner_ID" )) {
            m_C_BPartner_ID = e.getNewValue();
            fBPartner.setValue( m_C_BPartner_ID );    // display value
        }

        executeQuery();
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        int index = tabbedPane.getSelectedIndex();

        m_selectionActive = ( index == 0 );
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {
        int rowsSelected = 0;
        int rows         = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0

            if( (id != null) && id.isSelected()) {
                rowsSelected++;
            }
        }

        statusBar.setStatusDB( " " + rowsSelected + " " );
    }    // tableChanged

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String saveSelection() {
        log.info( "En VInvoiceGen-SaveSelection" );

        // ID selection may be pending

        miniTable.editingStopped( new ChangeEvent( this ));

        // Array of Integers

        ArrayList results = new ArrayList();

        // Get selected entries

        int rows = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0

            // log.fine( "Row=" + i + " - " + id);

            if( (id != null) && id.isSelected()) {
                results.add( id.getRecord_ID());
            }
        }

        if( results.size() == 0 ) {
            return "";
        }

        // Query String

        String       keyColumn = "C_Order_ID";
        StringBuffer sb        = new StringBuffer( keyColumn );

        if( results.size() > 1 ) {
            sb.append( " IN (" );
        } else {
            sb.append( "=" );
        }

        // Add elements

        for( int i = 0;i < results.size();i++ ) {
            if( i > 0 ) {
                sb.append( "," );
            }

            if( keyColumn.endsWith( "_ID" )) {
                sb.append( results.get( i ).toString());
            } else {
                sb.append( "'" ).append( results.get( i ).toString());
            }
        }

        if( results.size() > 1 ) {
            sb.append( ")" );
        }

        //

        log.config( sb.toString());

        return sb.toString();
    }    // saveSelection

    /**
     * Descripción de Método
     *
     */

    private void generateInvoices() {
    	log.config("En generateInvoice con m_whereClause= "+m_whereClause);

        // String trxName = Trx.createTrxName("IVG");
        // Trx trx = Trx.get(trxName, true);       trx needs to be committed too

        String trxName = null;
        Trx    trx     = null;

        // Reset Selection

        String sql = "UPDATE C_Order SET IsSelected = 'N' WHERE IsSelected='Y'" + " AND AD_Client_ID=" + Env.getAD_Client_ID( Env.getCtx()) + " AND AD_Org_ID=" + Env.getAD_Org_ID( Env.getCtx());
        int no = DB.executeUpdate( sql,trxName );

        log.config( "Reset=" + no );

        // Set Selection

        sql = "UPDATE C_Order SET IsSelected = 'Y' WHERE " + m_whereClause;
        no  = DB.executeUpdate( sql,trxName );

        if( no == 0 ) {
            String msg = "No Invoices";    // not translated!

            log.config( msg );
            info.setText( msg );

            return;
        }

        log.config( "He Seleccionado=" + no + "facturas");
        m_selectionActive = false;    // prevents from being called twice
        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"InvGenerateGen" ));
        statusBar.setStatusDB( String.valueOf( no ));

        // Prepare Process

        int        AD_Process_ID = 134;    // HARDCODED    C_InvoiceCreate
        MPInstance instance      = new MPInstance( Env.getCtx(),AD_Process_ID,0,null );

        if( !instance.save()) {
            info.setText( Msg.getMsg( Env.getCtx(),"ProcessNoInstance" ));

            return;
        }

        ProcessInfo pi = new ProcessInfo( "",AD_Process_ID );

        pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

        // Add Parameters

        MPInstancePara para = new MPInstancePara( instance,10 );

        para.setParameter( "Selection","Y" );

        if( !para.save()) {
            String msg = "No Selection Parameter added";    // not translated

            info.setText( msg );
            log.log( Level.SEVERE,msg );

            return;
        }

        para = new MPInstancePara( instance,20 );
        para.setParameter( "DocAction","CO" );

        if( !para.save()) {
            String msg = "No DocAction Parameter added";    // not translated

            info.setText( msg );
            log.log( Level.SEVERE,msg );

            return;
        }

        // Execute Process

        ProcessCtl worker = new ProcessCtl( this,pi,trx );

        worker.start();    // complete tasks in unlockUI / generateInvoice_complete
    }                      // generateInvoices

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    private void generateInvoice_complete( ProcessInfo pi ) {

        // Switch Tabs

        tabbedPane.setSelectedIndex(1);

        //

        ProcessInfoUtil.setLogFromDB( pi );

        StringBuffer iText = new StringBuffer();

        iText.append( "<b>" ).append( pi.getSummary()).append( "</b><br>(" ).append( Msg.getMsg( Env.getCtx(),"InvGenerateInfo" ))

        // Invoices are generated depending on the Invoicing Rule selection in the Order

        .append( ")<br>" ).append( pi.getLogInfo( true ));
        info.setText( iText.toString());

        // Reset Selection

        String sql = "UPDATE C_Order SET IsSelected = 'N' WHERE " + m_whereClause;
        int no = DB.executeUpdate( sql );

        log.config( "VInvoiceGen.generateInvoices_complete - Reset=" + no );

        // Get results

        int[] ids = pi.getIDs();

        if( (ids == null) || (ids.length == 0) ) {
            return;
        }

        confirmPanelGen.getOKButton().setEnabled( true );

        // OK to print invoices

        if( ADialog.ask( m_WindowNo,this,"PrintInvoices" )) {

            // info.append("\n\n" + Msg.getMsg(Env.getCtx(), "PrintInvoices"));

            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

            int retValue = ADialogDialog.A_CANCEL;

            do {

                // Loop through all items

                for( int i = 0;i < ids.length;i++ ) {
                    int C_Invoice_ID = ids[ i ];

                    ReportCtl.startDocumentPrint( ReportEngine.INVOICE,C_Invoice_ID,true );
                }

                ADialogDialog d = new ADialogDialog( m_frame,Env.getHeader( Env.getCtx(),m_WindowNo ),Msg.getMsg( Env.getCtx(),"PrintoutOK?" ),JOptionPane.QUESTION_MESSAGE );

                retValue = d.getReturnCode();
            } while( retValue == ADialogDialog.A_CANCEL );

            setCursor( Cursor.getDefaultCursor());
        }    // OK to print invoices

        //

        confirmPanelGen.getOKButton().setEnabled( true );
    }    // generateInvoices_complete

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void lockUI( ProcessInfo pi ) {
        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        this.setEnabled( false );
    }    // lockUI

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void unlockUI( ProcessInfo pi ) {
        this.setEnabled( true );
        this.setCursor( Cursor.getDefaultCursor());

        //

        generateInvoice_complete( pi );
    }    // unlockUI

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isUILocked() {
        return this.isEnabled();
    }    // isUILocked

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    public void executeASync( ProcessInfo pi ) {}    // executeASync
}    // VInvoiceGen



/*
 *  @(#)VInvoiceGen.java   02.07.07
 * 
 *  Fin del fichero VInvoiceGen.java
 *  
 *  Versión 2.2
 *
 */
