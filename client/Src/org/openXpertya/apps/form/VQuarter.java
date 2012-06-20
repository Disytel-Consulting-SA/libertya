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
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MProductionOrder;
import org.openXpertya.model.MProductionOrderline;
import org.openXpertya.model.MStorage;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.print.ReportCtl;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class VQuarter extends CPanel implements FormPanel,ActionListener,VetoableChangeListener,ChangeListener,TableModelListener,ASyncProcess {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "En VQuartering" );
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );

        try {
            fillPicks();
            jbInit();
            dynInit();
            frame.getContentPane().add( tabbedPane,BorderLayout.CENTER );
            //Añadido por ConSerTi para seleccionar todas las filas
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
    private Object m_M_Product_ID = null;

    private Object m_M_Warehouse_ID = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VInOutGen.class );

    //

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();

    //  Añadido por ConSerti, para poder seleccionar todas las filas a la vez.
    private CTabbedPane allselectPane = new CTabbedPane();
    private CPanel selNorthPanel_aux = new CPanel();
    //Fin añadido
    /** Descripción de Campos */

    private CPanel selPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel selNorthPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout selPanelLayout = new BorderLayout();


    /** Descripción de Campos */
    private CLabel lQuantity = new CLabel();
	private VNumber fQuantity = new VNumber();
	
    private CLabel lM_Product = new CLabel();

    private CLabel lM_Locator = new CLabel();
    /** Descripción de Campos */

    private VLookup fM_Product;

    private VLookup fM_Locator;
    /** Descripción de Campos */

    private FlowLayout northPanelLayout = new FlowLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanelSel = new ConfirmPanel( true );

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
        lM_Product.setLabelFor(fM_Product);
        lM_Locator.setLabelFor(fM_Locator);
        selNorthPanel.setLayout( northPanelLayout );
        northPanelLayout.setAlignment( FlowLayout.LEFT );
        tabbedPane.add( selPanel,Msg.getMsg( Env.getCtx(),"Select" ));
        //Añadido por ConSerTi para la posicion de seleccionar todo
        selNorthPanel_aux.add(selNorthPanel,BorderLayout.SOUTH);
        selPanel.add( selNorthPanel_aux,BorderLayout.NORTH );
        lQuantity.setLabelFor(fQuantity);
		lQuantity.setText("Cantidad");
        //Fin añadido
        selNorthPanel.add( lM_Product,null );
        selNorthPanel.add( fM_Product,null );
        selNorthPanel.add( lM_Locator,null );
        selNorthPanel.add( fM_Locator,null );
        selNorthPanel.add( lQuantity,null );
        selNorthPanel.add( fQuantity,null );
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

        // C_OrderLine.M_Warehouse_ID

        MLookup mprod = MLookupFactory.get(Env.getCtx(),m_WindowNo,0, 1402, DisplayType.TableDir);
        MLookup mloc = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0, 1151, DisplayType.TableDir);
       
        fM_Product = new VLookup( "M_Product_ID",true,false,true,mprod);
        lM_Product.setText( Msg.translate(Env.getCtx(),"M_Product_ID"));
        fM_Product.addVetoableChangeListener(this);
        m_M_Product_ID= fM_Product.getValue();
        
        fM_Locator = new VLookup( "M_Warehouse_ID",true,false,true,mloc);
        lM_Locator.setText(Msg.translate(Env.getCtx(),"M_Warehouse_ID"));
        fM_Locator.addVetoableChangeListener(this);
        m_M_Warehouse_ID= fM_Locator.getValue();

    }    // fillPicks

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // create Columns

        miniTable.addColumn( "M_Product_ID" );    
        miniTable.addColumn( "Name" );
        miniTable.addColumn( "BomQty" );
        //

        miniTable.setMultiSelection( true );
        miniTable.setRowSelectionAllowed( true );

        // set details

        miniTable.setColumnClass( 0,IDColumn.class,false," " );
        miniTable.setColumnClass( 1,String.class,true,Msg.translate( Env.getCtx(),"Name" ));
        miniTable.setColumnClass( 2,BigDecimal.class,true,Msg.translate( Env.getCtx(),"BomQty" ));

        //

        miniTable.autoSize();
        miniTable.getModel().addTableModelListener( this );

        // Info

       // statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"InOutGenerateSel" ));    // @@
        statusBar.setStatusDB( " " );

        // Tabbed Pane Listener

        tabbedPane.addChangeListener( this );
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void executeQuery() {

        log.info( "En ExecuteQuery de VQuarter" );

        int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
        

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT m.m_product_id,m.name, b.bomqty from m_product m ,m_product_bom b where m.m_product_id=b.m_productbom_id and m.ad_client_id ="+AD_Client_ID );

        if( m_M_Product_ID != null){
    	   sql.append(" AND b.m_product_id=").append(m_M_Product_ID);
        }
        
       
        //

        sql.append( " ORDER BY m.Name" );
        log.fine( sql.toString());

        // reset table

        int row = 0;

        miniTable.setRowCount( row );

        // Execute

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {

                // extend table

                miniTable.setRowCount( row + 1 );

                // set values
                int cantidad=rs.getBigDecimal(3).intValue();
                //set products qty
                if(rs.getBigDecimal(3).compareTo(BigDecimal.ONE)==1){
                	for(int i=0;i<cantidad;i++){
                		
                		miniTable.setRowCount( row + 1 );
                		miniTable.setValueAt( new IDColumn( rs.getInt( 1 )),row,0 );    // M_Product_ID
                        miniTable.setValueAt( rs.getString( 2 ),row,1 );       // Name
                        miniTable.setValueAt( BigDecimal.ONE,row,2 );  
                        row++;
                	}
                	miniTable.setRowCount( row + 1 );
                	rs.next();
                }
                miniTable.setValueAt( new IDColumn( rs.getInt( 1 )),row,0 );    // M_Product_ID
                miniTable.setValueAt( rs.getString( 2 ),row,1 );       // Name
                miniTable.setValueAt( rs.getBigDecimal( 3 ),row,2 );    // Qty

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
                     // miniTable.setValueAt( new Boolean( automatico.isSelected()),i,6 );

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
       
        //Añadido por ConSerTi, para seleccionar todas las filas.
        if (e.getSource().equals(automatico))
                     seleccionarTodos();
        //fin Añadido

        if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
            return;
        }
        else if (e.getActionCommand().equals(ConfirmPanel.A_OK))
		{
        	if(m_selectionActive){
        		updateStock();
        	}else{
        		dispose();
        	}
		}
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        log.info("en VetoAbleChange" +e.getPropertyName() + "=" + e.getNewValue());

        if( e.getPropertyName().equals( "M_Product_ID" )) {
            m_M_Product_ID = e.getNewValue();
        }
        if( e.getPropertyName().equals( "M_Warehouse_ID" )) {
        	m_M_Warehouse_ID = e.getNewValue();
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
    	log.info("En stateChanged con index= "+ tabbedPane.getSelectedIndex() );
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

    private void updateStock() {
        log.info( "updateStock - VQuarter" );

        // ID selection may be pending
        
        miniTable.editingStopped( new ChangeEvent( this ));

        // Array of Integers

        ArrayList results = new ArrayList();

        // Get selected entries
     
        int rows = miniTable.getRowCount();
        BigDecimal quantity = null;
        if (fQuantity.getValue() != null){
        	quantity= new BigDecimal(fQuantity.getValue().toString());
        }else{
        	quantity=BigDecimal.ONE;
        }
        
        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0

            if( (id != null) && id.isSelected()) {
                results.add( id.getRecord_ID());
            }
        }
        MProductionOrder proc=null;
        MProductionOrderline lineproc = null;
        //Update Stock
        try {
        	StringBuffer sql;
        	
        	sql = new StringBuffer( "SELECT m_product_id, bomQtyOnHand("+m_M_Product_ID+","+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0) from m_product" +
            		" WHERE  m_product_id="+m_M_Product_ID+" " );
        	
            PreparedStatement pstmt = null;
            ResultSet rs;
            pstmt = DB.prepareStatement( sql.toString());
            rs    = pstmt.executeQuery();
           //Comprobacion stock
            if(rs.next()){
            	if(rs.getBigDecimal(2).subtract(quantity).compareTo(BigDecimal.ZERO)<=0){
            		JOptionPane.showMessageDialog( null,"El producto a despiezar no tiene stock suficiente","Sin Stock", JOptionPane.ERROR_MESSAGE );
            		return;
            	}
            	
            	proc = new MProductionOrder(Env.getCtx(),0,null);
            	proc.save();
            	
            	int M_Locator_ID=0;
                M_Locator_ID = MStorage.getM_Locator_ID( Integer.valueOf(String.valueOf(m_M_Warehouse_ID)).intValue(),Integer.valueOf(String.valueOf(m_M_Product_ID)).intValue(),0,quantity,null);
 
                // Get default Location

                if( M_Locator_ID == 0 ) {
                	 MWarehouse wh = MWarehouse.get( Env.getCtx(),Integer.valueOf(String.valueOf(m_M_Warehouse_ID)).intValue());
                	
                	M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
                }
            	
            	MStorage.add( Env.getCtx(),Integer.valueOf(String.valueOf(m_M_Warehouse_ID)).intValue(),M_Locator_ID,Integer.valueOf(String.valueOf(m_M_Product_ID)).intValue(),0,0,quantity.negate(),null,null,null);
            }
            rs.close();
			pstmt.close();
			pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"ProductionRun - CheckProduction; " + e );
        }
        // Add elements
        int Line=0;

        for( int i = 0;i < results.size();i++ ) {
            log.fine("En saveSelection, esto es lo q hay en results="+results.get(i));																										          
            int product=Integer.valueOf(String.valueOf(results.get(i))).intValue();
            
            try {
            	StringBuffer sql;
            	
            	sql = new StringBuffer( "SELECT m_product_id, bomQtyAvailable("+product+","+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0),bomqty from m_product_bom " +
                		"WHERE  m_productbom_id="+product+" " );
            	
                PreparedStatement pstmt = null;
                ResultSet rs;
                pstmt = DB.prepareStatement( sql.toString());
                rs    = pstmt.executeQuery();
                int M_Locator_ID=0;
                M_Locator_ID = MStorage.getM_Locator_ID( Integer.valueOf(String.valueOf(m_M_Warehouse_ID)).intValue(),Integer.valueOf(String.valueOf(m_M_Product_ID)).intValue(),0,quantity,null);
 
                // Get default Location

                if( M_Locator_ID == 0 ) {
                	MWarehouse wh = MWarehouse.get( Env.getCtx(),Integer.valueOf(String.valueOf(m_M_Warehouse_ID)).intValue());
                	M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
                }
                
                if(rs.next()){
                	if(m_M_Warehouse_ID != null){
                		MStorage.add( Env.getCtx(),Integer.valueOf(String.valueOf(m_M_Warehouse_ID)).intValue(),M_Locator_ID,product,0,0,quantity,null,null,null);
                	}else{
                		JOptionPane.showMessageDialog( null,"Por favor, seleccione una localización","Sin Localización", JOptionPane.ERROR_MESSAGE );
                		return ;
                	}
                }
                
                rs.close();
				pstmt.close();
				pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"ProductionRun - CheckProduction; " + e );
            }
            lineproc = new MProductionOrderline(proc);
            Line=Line+10;
            lineproc.setM_Product_ID(product);
        	lineproc.setLine(Line);
        	lineproc.setQty(quantity);//
        	lineproc.save();
        	
        }
              
        tabbedPane.setSelectedIndex( 1 );
        m_selectionActive = false;    // prevents from being called twice
        statusBar.setStatusLine( "Despiece completado");
        //

        StringBuffer iText = new StringBuffer();

        iText.append( "<b>" ).append( "</b><br>(" ).append( "Despiece completado con éxito ")
        .append( ")<br>");
        info.setText( iText.toString());
        // OK to print quartering
        
        if( ADialog.ask( m_WindowNo,this,"Imprimir Producción" )) {

            // info.append("\n\n" + Msg.getMsg(Env.getCtx(), "PrintShipments"));

            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

            int retValue = ADialogDialog.A_CANCEL;    // see also ProcessDialog.printShipments/Invoices

            do {

                // Loop through all items

                for( int i = 0;i < results.size();i++ ) {
                    int M_InOut_ID = Integer.valueOf(String.valueOf(results.get(i))).intValue();
                    ReportCtl.startDocumentPrint( ReportEngine.ORDER,M_InOut_ID,true );
                }

                ADialogDialog d = new ADialogDialog( m_frame,Env.getHeader( Env.getCtx(),m_WindowNo ),Msg.getMsg( Env.getCtx(),"PrintoutOK?" ),JOptionPane.QUESTION_MESSAGE );

                retValue = d.getReturnCode();
            } while( retValue == ADialogDialog.A_CANCEL );

            setCursor( Cursor.getDefaultCursor());
        }    // OK to print shipments
        this.setEnabled( true );
      
        return;
    }    // saveSelection


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

       // generateShipments_complete( pi );
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
}    // VInOutGen



/*
 *  @(#)VInOutGen.java   02.07.07
 *
 *  Fin del fichero VInOutGen.java
 *
 *  Versión 2.2
 *
 */
