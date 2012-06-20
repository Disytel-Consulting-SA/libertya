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
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.process.*;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.process.C_RemesaGenerate;
/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VInvoiceRemGen_aux extends CPanel implements FormPanel,ActionListener,VetoableChangeListener,ChangeListener,TableModelListener,ASyncProcess {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor de la clase ...
     *
     *
     * @param mTab
     */

    public VInvoiceRemGen_aux( MTab mTab ) {
        m_tab                  = mTab;
       /* m_PriceList_Version_ID = (( Integer )m_tab.getValue( "M_PriceList_Version_ID" )).intValue();
        m_Client_ID         = (( Integer )m_tab.getValue( "AD_Client_ID" )).intValue();
        m_Org_ID            = (( Integer )m_tab.getValue( "AD_Org_ID" )).intValue();
        m_UpdatedBy         = (( Integer )m_tab.getValue( "UpdatedBy" )).intValue();
        m_DiscountSchema_ID = (( Integer )m_tab.getValue( "M_DiscountSchema_ID" )).intValue();*/

      /*  if( m_tab.getValue( "M_PriceList_Version_Base_ID" ) != null ) {
            m_PriceList_Version_Base_ID = (( Integer )m_tab.getValue( "M_PriceList_Version_Base_ID" )).intValue();
        } else {*/
       // m_Client_ID=Env.getAD_Client_ID(Env.getCtx());
      //  m_Org_ID=Env.getAD_Org_ID(Env.getCtx());
        m_UpdatedBy=0;
        m_PriceList_Version_Base_ID = 0;
      //  }
        Remesa=((Integer) m_tab.getValue("C_Remesa_ID")).intValue();
        salir         = false;
        tablatemporal = false;
    }

    /** Descripción de Campos */
    private Object m_AD_Org_ID = null;
    private Object m_C_BPartner_ID = null;
    private static CLogger log = CLogger.getCLogger( VInvoiceRemGen_aux.class );

    /** Descripción de Campos */

    private MTab m_tab;

    /** Descripción de Campos */

    private int m_PriceList_Version_ID;

    /** Descripción de Campos */

    private int m_Client_ID;

    /** Descripción de Campos */

    private int m_Org_ID;

    /** Descripción de Campos */

    private int m_UpdatedBy;

    /** Descripción de Campos */

    private int m_DiscountSchema_ID;

    /** Descripción de Campos */

    private int m_PriceList_Version_Base_ID;

    /** Descripción de Campos */

    private int no;

    /** Descripción de Campos */

    private boolean creandoTabla;

    /** Descripción de Campos */

    private boolean salir;

    /** Descripción de Campos */

    private boolean tablatemporal;

    /** Descripción de Campos */

    private final int CPA = 3;

    /** Descripción de Campos */

    private final int CPN = 4;

    /** Descripción de Campos */

    private final int CS = 5;

    /** Descripción de Campos */

    private final int CA = 6;

    /** Descripción de Campos */

    private final int CC = 7;

    /** Descripción de Campos */

    private final int CDE = 8;

    /** Descripción de Campos */

    private final int CPS = 9;
    
    private int Remesa=0;
    private MQuery m_query = null;
    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "currupio VInvoiceRemGen_aux.init" );
        
        m_WindowNo = WindowNo;
        m_frame    = frame;
       // Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );
       // Remesa=Env.getContextAsInt( Env.getCtx(),m_WindowNo,"C_Remesa_ID" );
        try {
        	
            fillPicks();
            jbInit();   
            dynInit();
            
            m_frame.getContentPane().add( tabbedPane,BorderLayout.CENTER );            
            //Añadido por ConSerTi para seleccionar todas las filas
            m_frame.getContentPane().add(allselectPane,BorderLayout.LINE_END);
            //Fin añadido            
            m_frame.getContentPane().add( statusBar,BorderLayout.SOUTH );           
            doIt();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VInvoiceRemGen_aux,init " + ex );
        }
    }

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    //

    /** Descripción de Campos */

    private CTabbedPane tabbedPane = new CTabbedPane();
    
    //Añadido por ConSerti, para poder seleccionar todas las filas a la vez.
    private CTabbedPane allselectPane = new CTabbedPane();
    //Fin añadido

    /** Descripción de Campos */

    private CPanel selPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout selPanelLayout = new BorderLayout();

    /** Descripción de Campos */

    private FlowLayout northPanelLayout = new FlowLayout();


    /** Descripción de Campos */

   // private ConfirmPanel confirmPanelSel = new ConfirmPanel( true,false,false,false,false,false,true );

    private ConfirmPanel confirmPanelSel = new ConfirmPanel( true,false,false,false,false,false,true );
    /** Descripción de Campos */

    private ConfirmPanel confirmPanelGen = new ConfirmPanel( false,false,false,false,false,false,true );

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
    
   //Añadido por ConSerti, para poder seleccionar todas las filas a la vez. 
    private CCheckBox automatico = new CCheckBox();
    private CPanel selNorthPanel_aux = new CPanel();
    //Fin añadido
    private CLabel lOrg = new CLabel();
    private VLookup fOrg;
    private CLabel lBPartner = new CLabel();
    private VLookup fBPartner;
    private CPanel selNorthPanel = new CPanel();
    /** Descripción de Campos */

    private int m_keyColumnIndex = -1;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void fillPicks() throws Exception {
    	   MLookup orgL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,2163,DisplayType.TableDir );

           fOrg = new VLookup( "AD_Org_ID",false,false,true,orgL );

           fOrg.addVetoableChangeListener( this );

           MLookup bpL = MLookupFactory.get( Env.getCtx(),m_WindowNo,0,2762,DisplayType.Search );

           fBPartner = new VLookup( "C_BPartner_ID",false,false,true,bpL );

           fBPartner.addVetoableChangeListener( this );
           
          
    }

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
    	log.info( "currupio VInvoiceRemGen_aux.jbInit" );
    	
        CompiereColor.setBackground( this );
        selNorthPanel_aux.setLayout(new BorderLayout());
        selPanel.setLayout( selPanelLayout );
        lOrg.setLabelFor( fOrg );
        lOrg.setText( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
        lBPartner.setLabelFor( fBPartner );
        lBPartner.setText( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
        selNorthPanel.setLayout( northPanelLayout );
  
        northPanelLayout.setAlignment( FlowLayout.LEFT );

        tabbedPane.add( selPanel,"Select" );
  
        selPanel.add( selNorthPanel,BorderLayout.NORTH );
        //Añadido por ConSerTi para la posicion de seleccionar todo
        selNorthPanel_aux.add(selNorthPanel,BorderLayout.SOUTH);  
        selPanel.add( selNorthPanel_aux,BorderLayout.NORTH );
        //Fin añadido
 
    	selNorthPanel.add(lOrg,null);
 
    	selNorthPanel.add(fOrg,null);
    	
    	selNorthPanel.add(lBPartner,null);
    	
    	selNorthPanel.add(fBPartner,null);

        selPanel.setName( "selPanel" );
        selPanel.add( confirmPanelSel,BorderLayout.SOUTH );
    
//Modificado por ConSerTi para seleccionar una tabla entera
        automatico.setText("Seleccionar Todos");  
        automatico.setSelected(true);
        automatico.addActionListener(this);
        automatico.setEnabled(true);
        selNorthPanel_aux.add(automatico,BorderLayout.NORTH);
        selPanel.add( selNorthPanel_aux,BorderLayout.NORTH );
//Fin Modificacion
   
        selPanel.add( scrollPane,BorderLayout.CENTER );
        scrollPane.getViewport().add( miniTable,null );
        confirmPanelSel.addActionListener( this );

        tabbedPane.add( genPanel,"Generate" );
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
     */

    private void dynInit() {
    	log.info( " currupio VInvoiceRemGen_aux.dynInit" );

        // create Columns
    	miniTable.addColumn( "C_InvoicePaySchedule_ID" );
        miniTable.addColumn( "DocumentNo");
        miniTable.addColumn( "C_BPartner");
        miniTable.addColumn( "DateAcct" );
        miniTable.addColumn( "IsPaid" );
        miniTable.addColumn( "TotalLines" );
        miniTable.addColumn( "GrandTotal" );
       // miniTable.addColumn( "IsPaySchedulevalid" );
       // miniTable.addColumn( "DatePrinted" );
        miniTable.addColumn( "DateInvoiced" );
        miniTable.addColumn( "IsIndispute" );
        miniTable.setColorColumn( 8 );
        miniTable.setColorCompare( new BigDecimal( 0.01 ));
       // miniTable.addColumn( "Precio Seleccionado" );

        //

        miniTable.setMultiSelection( true );
        miniTable.setRowSelectionAllowed( true );

        // set details

        miniTable.setColumnClass( 0,IDColumn.class,false," " );
       

        // sentencia a�adida para que se sepa que IDColumn.class est� en la 0

        m_keyColumnIndex = 0;
        miniTable.setColumnClass( 1,Integer.class,true,Msg.translate( Env.getCtx(),"DocumentNo" ));
        miniTable.setColumnClass( 2,String.class,true,Msg.translate( Env.getCtx(),"C_BPartner_ID"));
        miniTable.setColumnClass( 3,Timestamp.class,true,Msg.translate( Env.getCtx(),"DateAcct" ));
        miniTable.setColumnClass( 4,String.class,true,Msg.translate( Env.getCtx(),"IsPaid" ));
        miniTable.setColumnClass( 5,BigDecimal.class,true,Msg.translate( Env.getCtx(),"TotalLines" ));
        miniTable.setColumnClass( 6,BigDecimal.class,true,Msg.translate( Env.getCtx(),"GrandTotal" ));
       // miniTable.setColumnClass( 5,String.class,true,Msg.translate( Env.getCtx(),"IsPaySchedulevalid" ));
       // miniTable.setColumnClass( 6,Timestamp.class,true,Msg.translate( Env.getCtx(),"DatePrinted" ));
        miniTable.setColumnClass( 7,Timestamp.class,true,Msg.translate( Env.getCtx(),"DateInvoiced" ));
        miniTable.setColumnClass( 8,String.class,true,Msg.translate( Env.getCtx(),"IsIndispute" ));
        miniTable.autoSize();
        miniTable.getModel().addTableModelListener( this );

        // Info

        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"MInvoice" ));
        statusBar.setStatusDB( " " );

        // Tabbed Pane Listener

        tabbedPane.addChangeListener( this );
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void executeQuery() {
        creandoTabla = true;
        log.info( " currupio VInvoiceRemGen_aux.executeQuery" );

        int row = 0;
        int norma_id=0;
        miniTable.setRowCount( row );
        String sql2="SELECT c_norma_id from C_Remesa where c_remesa_id="+Remesa+"";
		PreparedStatement pstmt2 = null;
		try
		{
			pstmt2 = DB.prepareStatement(sql2);
			ResultSet rs2 = pstmt2.executeQuery();
			if (rs2.next())
			{
				 norma_id=rs2.getInt(1);
			}
			rs2.close();
			pstmt2.close();
			pstmt2 = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - Prepare", e);
		}
        // Create SQL
        StringBuffer sql = new StringBuffer( "SELECT ips.c_invoicepayschedule_id, inv.documentno, cb.name, duedate, ispaid, dueamt, grandtotal, ispayschedulevalid, dateprinted, dateinvoiced, isindispute FROM c_invoicepayschedule ips, c_invoice inv, c_bpartner cb WHERE ips.c_invoice_id=inv.c_invoice_id AND cb.c_bpartner_id=inv.c_bpartner_id " +
        		" AND c_remesa_id is null AND inv.issotrx=(Select issotrx from c_norma where c_norma_id="+norma_id+") AND inv.paymentrule=(Select paymentrule from c_norma where c_norma_id="+norma_id+")" );

        if( m_AD_Org_ID != null ) {
            sql.append( " AND inv.AD_Org_ID=" ).append( m_AD_Org_ID );
        }

        if( m_C_BPartner_ID != null ) {
            sql.append( " AND inv.C_BPartner_ID=" ).append( m_C_BPartner_ID );
        }

        
        // reset table
        // Execute

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());
            ResultSet         rs    = pstmt.executeQuery();

            //

            while( rs.next()) {

                // extend table

                miniTable.setRowCount( row + 1 );

                // set values

                IDColumn m_idColumn;

                m_idColumn = new IDColumn( rs.getInt( 1 ));
                log.fine("LA ID en executeQuery="+m_idColumn);
                automatico.setSelected(false);
                m_idColumn.setSelected( false );
                miniTable.setValueAt( m_idColumn,row,0 ); 
                miniTable.setValueAt( new Integer( rs.getInt( 2 ) ), row, 1);
                miniTable.setValueAt( rs.getString( 3 ), row, 2);// C_Invoice_ID
                miniTable.setValueAt( rs.getDate( 4 ),row,3 );          // DateAcct
                miniTable.setValueAt( rs.getString( 5 ),row,4 );          // IsPaid
                miniTable.setValueAt( rs.getBigDecimal( 6 ),row,5 );    // TotalLines
                miniTable.setValueAt( rs.getBigDecimal( 7 ),row,6 );    // GrandTotal
                //miniTable.setValueAt( rs.getString( 8 ),row,7 );    // IsPaySchedulevalid
                //miniTable.setValueAt( rs.getDate( 9 ),row,7 );    // DatePrinted
                miniTable.setValueAt( rs.getDate( 10 ),row,7 );    // DateInvoiced
                miniTable.setValueAt( rs.getString( 11 ),row,8 ); //IsIndispute
               
                // prepare next

                row++;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VInvoiceRemGen_aux.executeQuery " + sql.toString(),e );
        }

        //

        miniTable.autoSize();
        statusBar.setStatusDB( String.valueOf( miniTable.getRowCount()));
        creandoTabla = false;
    }    // executeQuery

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
    	log.info( " currupio VInvoiceRemGen_aux.dispose" );

        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     */
    //Añadido por ConSerTi para seleccionar todas las columnas
    public void seleccionarTodos() {
        for( int i = 0;i < miniTable.getRowCount();i++ ) {
            IDColumn id = ( IDColumn )miniTable.getModel().getValueAt( i,0 );
                      id.setSelected(automatico.isSelected());
                     // miniTable.setValueAt( new Boolean( automatico.isSelected()),i,0 );

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
        log.info( " currupio VInvoiceRemGen_aux.actionPerformed - " + e.getActionCommand());

        //Añadido por ConSerTi, para seleccionar todas las filas.
        if (e.getSource().equals(automatico))
                     seleccionarTodos();
        //fin Añadido

        if( e.getActionCommand().equals( ConfirmPanel.A_ZOOM )) {
            zoom();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();

            return;
        } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            updateIt(); 
            dispose();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */
   
    
    void zoom() {
        log.info( "VInvoiceRemGen_aux.zoom" );

        Integer C_Invoice_ID = getSelectedRowKey();

        if( C_Invoice_ID == null ) {
            return;
        }
       
        AEnv.zoom( MInvoice.Table_ID,C_Invoice_ID.intValue());
    }
    

    // a�adido para posibilitar el zoom a pedidos

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasZoom() {
        return true;
    }    // hasZoom

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected Integer getSelectedRowKey() {
        int row = miniTable.getSelectedRow();

        if( (row != -1) && (m_keyColumnIndex != -1) ) {
            Object data = miniTable.getModel().getValueAt( row,m_keyColumnIndex );

            if( data instanceof IDColumn ) {
                data = (( IDColumn )data ).getRecord_ID();
                log.fine("en getSelectedRowKey con data ="+data);
            }

            if( data instanceof Integer ) {
                return( Integer )data;
            }
        }

        return null;
    }    // a�adido para posibilitar el zoom a pedidos

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void vetoableChange( PropertyChangeEvent e ) {
        log.info( "VProdPricGen_aux.vetoableChange - " + e.getPropertyName() + "=" + e.getNewValue());
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
    	 log.fine("Estoy en stateChanged y lo que llega es e="+e.toString());
        int index = tabbedPane.getSelectedIndex();
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void tableChanged( TableModelEvent e ) {
        if( !creandoTabla ) {
            creandoTabla = true;
            log.fine("Estoy en tableChanged y lo que llega es e="+e.toString());
            //changeSelectedPrice();
            creandoTabla = false;
        }
    }    // tableChanged

    /**
     * Descripción de Método
     *
     */

    public void changeSelectedPrice() {
    	log.info( " currupio VInvoiceRemGen_aux.changeSelecedPrice" );
       /* boolean upda;
        boolean calc;

        for( int i = 0;i < miniTable.getRowCount();i++ ) {
            upda = (( Boolean )miniTable.getValueAt( i,CA )).booleanValue();
            calc = (( Boolean )miniTable.getValueAt( i,CC )).booleanValue();

            BigDecimal selpric = ( BigDecimal )miniTable.getValueAt( i,CPS );

            if( !upda && calc ) {
                selpric = ( BigDecimal )miniTable.getValueAt( i,CPA );
            }

            if( upda && calc ) {
                selpric = ( BigDecimal )miniTable.getValueAt( i,CPN );
            }

            miniTable.setValueAt( selpric,i,CPS );
            miniTable.setValueAt( null,i,CDE );

            if((( BigDecimal )miniTable.getValueAt( i,CPA )).doubleValue() > selpric.doubleValue()) {
                double stock = (( BigDecimal )miniTable.getValueAt( i,CS )).doubleValue();

                if( stock > 0 ) {
                    double desfase = (( BigDecimal )miniTable.getValueAt( i,CPA )).doubleValue() * stock;

                    desfase = desfase - ( selpric.doubleValue() * stock );
                    miniTable.setValueAt( new BigDecimal( desfase ),i,CDE );
                }
            }

            if((( BigDecimal )miniTable.getValueAt( i,CPA )).doubleValue() < selpric.doubleValue()) {
                miniTable.setValueAt( Env.ZERO,i,CDE );
            }
        }
        miniTable.autoSize();*/
    }    // changeSelectedPrice



    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    private void afterCreateProductPrices( ProcessInfo pi ) {
    	log.info( " currupio VInvoiceRemGen_aux.afterCreateProductPrice" );
       /* try {
            String sql = new String( "DELETE FROM AD_PInstance_Para WHERE AD_PInstance_ID = " + pi.getAD_PInstance_ID());

            DB.executeUpdate( sql.toString());
            no = DB.executeUpdate( "DELETE FROM T_ProductPricing" );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"\nVProdPricGen.AfterCreateProductPrices" );
        }

        // Switch Tabs

        tabbedPane.setSelectedIndex( 1 );

        //

        ProcessInfoUtil.setLogFromDB( pi );

        StringBuffer iText = new StringBuffer();

        iText.append( "</b><br>" ).append( "<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"2\">" ).append( "<tr>" ).append( Msg.translate( Env.getCtx(),"Product Prices Created and Updated:" )).append( "</tr>" );
        iText.append( "</hr>" );
        iText.append( pi.getSummary());
        iText.append( "</table>" );
        info.setText( iText.toString());

        //
*/
        confirmPanelGen.getOKButton().setEnabled( true );
        salir = true;
    }

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

        if( tablatemporal ) {
            afterCreateProductPrices( pi );
        } else {
            tablatemporal = true;
            executeQuery();
        }
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

    /**
     * Descripción de Método
     *
     */

    private void doIt() {
    	log.info( " currupio VInvoiceRemGen_aux.doIT" );

        // Prepare Process

      /*  int proc_ID = DB.getSQLValue( null,"SELECT AD_Process_ID FROM AD_Process WHERE value='ProductPriceGen'" );

        if( proc_ID != -1 ) {
            MPInstance instance = new MPInstance( Env.getCtx(),proc_ID,0,null );

            if( !instance.save()) {
                info.setText( Msg.getMsg( Env.getCtx(),"ProcessNoInstance" ));

                return;
            }

            ProcessInfo pi = new ProcessInfo( "Crear Precios",proc_ID );

            pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());

            // Add Parameter - M_PriceList_Version_ID=Y

            MPInstancePara ip = new MPInstancePara( instance,10 );

            ip.setParameter( "M_PriceList_Version_ID",String.valueOf( m_PriceList_Version_ID ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Add Parameter - AD_Client_ID=x

            ip = new MPInstancePara( instance,20 );
            ip.setParameter( "AD_Client_ID",String.valueOf( m_Client_ID ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Add Parameter - AD_Org_ID=x

            ip = new MPInstancePara( instance,30 );
            ip.setParameter( "AD_Org_ID",String.valueOf( m_Org_ID ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Add Parameter - M_DiscountSchema_ID=x

            ip = new MPInstancePara( instance,40 );
            ip.setParameter( "M_DiscountSchema_ID",String.valueOf( m_DiscountSchema_ID ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Add Parameter - M_DiscountSchema_ID=x

            ip = new MPInstancePara( instance,50 );
            ip.setParameter( "M_PriceList_Version_Base_ID",String.valueOf( m_PriceList_Version_Base_ID ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Execute Process
            //JOptionPane.showMessageDialog( null,"En VProducpriceGen.dotIT this= \n"+ this+"\n"+"pi="+pi,"..Fin", JOptionPane.INFORMATION_MESSAGE );
            ProcessCtl worker = new ProcessCtl( this,pi,null );

            worker.start();    // complete tasks in unlockUI / afterProductPriceTemp
        }*/
    }

    /**
     * Descripción de Método
     *
     */

    private void updateIt() {
        log.info( " currupio VInvoiceRemGen_aux.updateIt" );


        // Get selected entries

        int rows = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0
               
            if( id != null && id.isSelected() ) {
              StringBuffer sql = new StringBuffer( "UPDATE C_InvoicePaySchedule SET aux='Y' WHERE C_Invoicepayschedule_ID="+ id.getRecord_ID());

               //JOptionPane.showMessageDialog( null,"En VProducpriceGen.updateIT \n"+ sql.toString(),"..Fin", JOptionPane.INFORMATION_MESSAGE );
               DB.executeUpdate( sql.toString());
               
            }
        }
    }    // saveSelection    
}    // VInvoiceRemGen




/*
 *  @(#)VInvoiceRemGen.java   02.07.07
 * 
 *  Fin del fichero VInvoiceRemGen.java
 *  
 *  Versión 2.2
 *
 */
