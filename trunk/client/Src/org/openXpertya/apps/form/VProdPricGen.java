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
import org.compiere.swing.CPanel;
import org.compiere.swing.CTabbedPane;
import org.compiere.swing.CTextPane;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MTab;
import org.openXpertya.process.*;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VProdPricGen extends CPanel implements FormPanel,ActionListener,VetoableChangeListener,ChangeListener,TableModelListener,ASyncProcess {

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

    public VProdPricGen( MTab mTab ) {
        m_tab                  = mTab;
        m_PriceList_Version_ID = (( Integer )m_tab.getValue( "M_PriceList_Version_ID" )).intValue();
        m_Client_ID         = (( Integer )m_tab.getValue( "AD_Client_ID" )).intValue();
        m_Org_ID            = (( Integer )m_tab.getValue( "AD_Org_ID" )).intValue();
        m_UpdatedBy         = (( Integer )m_tab.getValue( "UpdatedBy" )).intValue();
        m_DiscountSchema_ID = (( Integer )m_tab.getValue( "M_DiscountSchema_ID" )).intValue();

        if( m_tab.getValue( "M_PriceList_Version_Base_ID" ) != null ) {
            m_PriceList_Version_Base_ID = (( Integer )m_tab.getValue( "M_PriceList_Version_Base_ID" )).intValue();
        } else {
            m_PriceList_Version_Base_ID = 0;
        }

        salir         = false;
        tablatemporal = false;
    }

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VProdPricGen.class );

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

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        log.info( "currupio VProdPricGen.init" );
        
        m_WindowNo = WindowNo;
        m_frame    = frame;
        Env.setContext( Env.getCtx(),m_WindowNo,"IsSOTrx","Y" );
        try {
//            fillPicks();
//            jbInit();
//            dynInit();
//           
//            m_frame.getContentPane().add( tabbedPane,BorderLayout.CENTER );
//            
//            //Añadido por ConSerTi para seleccionar todas las filas
//            m_frame.getContentPane().add(allselectPane,BorderLayout.LINE_END);
//            //Fin añadido
//            
//            m_frame.getContentPane().add( statusBar,BorderLayout.SOUTH );
            doIt();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"VProdPricGen,init " + ex );
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
    //Fin añadido

    // indice inicial de columna seleccionada

    /** Descripción de Campos */

    private int m_keyColumnIndex = -1;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void fillPicks() throws Exception {}

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
    	log.info( "currupio VProdPricGen.jbInit" );
    	
        CompiereColor.setBackground( this );

        //
        
        selPanel.setLayout( selPanelLayout );
        
    
        northPanelLayout.setAlignment( FlowLayout.LEFT );
        tabbedPane.add( selPanel,Msg.getMsg( Env.getCtx(),"Select" ));
        
        selPanel.setName( "selPanel" );
        selPanel.add( confirmPanelSel,BorderLayout.SOUTH );
        //Modificado por ConSerTi para seleccionar una tabla entera
        automatico.setText("Seleccionar Todos");  
        automatico.setSelected(true);
        automatico.addActionListener(this);
        selPanel.add(automatico);
        automatico.setEnabled(true);
        selPanel.add(automatico,BorderLayout.NORTH);
        //Fin Modificacion
        selPanel.add( scrollPane,BorderLayout.CENTER );
        scrollPane.getViewport().add( miniTable,null );
        confirmPanelSel.addActionListener( this );

        //

        tabbedPane.add( genPanel,Msg.getMsg( Env.getCtx(),"Generate" ));
        genPanel.setLayout( genLayout );
        genPanel.add( info,BorderLayout.CENTER );
        genPanel.setEnabled( true );
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
    	log.info( " currupio VProdPricGen.dynInit" );

        // create Columns
        miniTable.addColumn( "M_Product_ID" );
        miniTable.addColumn( "Value" );
        miniTable.addColumn( "Name" );
        miniTable.addColumn( "Previous PriceListr" );
        miniTable.addColumn( "PriceList" );
        miniTable.addColumn( "Stock" );
        miniTable.addColumn( "Actualizar" );
        miniTable.addColumn( "Calcular" );
        miniTable.addColumn( "Desfase" );
        miniTable.setColorColumn( 8 );
        miniTable.setColorCompare( new BigDecimal( 0.01 ));
        miniTable.addColumn( "Precio Seleccionado" );

        //

        miniTable.setMultiSelection( true );
        miniTable.setRowSelectionAllowed( true );

        // set details

        miniTable.setColumnClass( 0,IDColumn.class,true," " );
       

        // sentencia a�adida para que se sepa que IDColumn.class est� en la 0

        m_keyColumnIndex = 0;
        miniTable.setColumnClass( 1,String.class,true,Msg.translate( Env.getCtx(),"Value" ));
        miniTable.setColumnClass( 2,String.class,true,Msg.translate( Env.getCtx(),"Name" ));
        miniTable.setColumnClass( 3,BigDecimal.class,true,Msg.translate( Env.getCtx(),"Previous PriceList" ));
        miniTable.setColumnClass( 4,BigDecimal.class,true,Msg.translate( Env.getCtx(),"PriceList" ));
        miniTable.setColumnClass( 5,BigDecimal.class,true,Msg.translate( Env.getCtx(),"Stock" ));
        miniTable.setColumnClass( 6,Boolean.class,false,Msg.translate( Env.getCtx(),"Actualizar" ));
        miniTable.setColumnClass( 7,Boolean.class,false,Msg.translate( Env.getCtx(),"Calcular" ));
        miniTable.setColumnClass( 8,BigDecimal.class,true,Msg.translate( Env.getCtx(),"Desfase" ));
        miniTable.setColumnClass( 9,BigDecimal.class,false,Msg.translate( Env.getCtx(),"Precio Final" ));

        //

        miniTable.autoSize();
        miniTable.getModel().addTableModelListener( this );

        // Info

        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"MProductPrice" ));
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
        log.info( " currupio VProdPricGen.executeQuery" );

        int row = 0;

        miniTable.setRowCount( row );

        // Create SQL

        StringBuffer sql = new StringBuffer( "SELECT tpp.M_Product_ID, p.Value, p.Name," + " bompriceList(tpp.M_Product_ID, " + m_PriceList_Version_ID + ")," + " tpp.PriceList, sum(bomqtyonhand(p.M_Product_ID,M_Warehouse_ID,0))," + " 'Y' as UpdatePrice, 'Y' as CalculatePrice, 0 as SelectedPrice" + " FROM I_ProductPrice tpp" + " INNER JOIN M_Warehouse w ON (w.AD_Client_ID=" + m_Client_ID + ")" + " INNER JOIN M_Product p ON (p.M_Product_ID=tpp.M_Product_ID AND p.IsActive='Y' AND p.AD_Client_ID=" + m_Client_ID + ")" + " GROUP BY tpp.M_Product_ID, p.Value, p.Name, tpp.PriceList, UpdatePrice, CalculatePrice, SelectedPrice" + " ORDER BY (bompriceList(tpp.M_Product_ID, " + m_PriceList_Version_ID + "))" );

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

                automatico.setSelected(false);
                m_idColumn.setSelected( false );
                miniTable.setValueAt( m_idColumn,row,0 );                 // M_Product_ID
                miniTable.setValueAt( rs.getString( 2 ),row,1 );          // Value
                miniTable.setValueAt( rs.getString( 3 ),row,2 );          // Name
                miniTable.setValueAt( rs.getBigDecimal( 4 ),row,CPA );    // Previous Price
                miniTable.setValueAt( rs.getBigDecimal( 5 ),row,CPN );    // PriceList
                miniTable.setValueAt( rs.getBigDecimal( 6 ),row,CS );    // Stock

                if(( rs.getBigDecimal( 4 ).doubleValue() < rs.getBigDecimal( 9 ).doubleValue()) && ( rs.getBigDecimal( 6 ).doubleValue() > 0 )) {
                    miniTable.setValueAt( new Boolean( false ),row,CA );    // Actualizate
                } else {
                    miniTable.setValueAt( new Boolean( true ),row,CA );
                }

                miniTable.setValueAt( new Boolean( "Y".equals( rs.getString( 8 ))),row,CC );    // Calculate

                if(( rs.getBigDecimal( 4 ).doubleValue() > rs.getBigDecimal( 9 ).doubleValue()) && ( rs.getBigDecimal( 6 ).doubleValue() > 0 )) {
                    double desfase = rs.getBigDecimal( 4 ).doubleValue() * rs.getBigDecimal( 6 ).doubleValue();

                    desfase = desfase - ( rs.getBigDecimal( 9 ).doubleValue() * rs.getBigDecimal( 6 ).doubleValue());
                    miniTable.setValueAt( new BigDecimal( desfase ),row,CDE );
                } else if( (rs.getBigDecimal( 4 ).doubleValue() < rs.getBigDecimal( 9 ).doubleValue()) && ( rs.getBigDecimal( 6 ).doubleValue() <= 0 )) {
                    miniTable.setValueAt( Env.ZERO,row,CDE );
                }

                if(( rs.getBigDecimal( 4 ).doubleValue() < rs.getBigDecimal( 9 ).doubleValue()) && ( rs.getBigDecimal( 6 ).doubleValue() > 0 )) {
                    miniTable.setValueAt( rs.getBigDecimal( 4 ),row,CPS );
                } else {
                    miniTable.setValueAt( rs.getBigDecimal( 9 ),row,CPS );    // Selected Price
                }

                // prepare next

                row++;
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VProductPriceGenerate.executeQuery " + sql.toString(),e );
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
    	log.info( " currupio VProdPricGen.dispose" );
        no = DB.executeUpdate( "DELETE FROM I_ProductPrice" );

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
                      miniTable.setValueAt( new Boolean( automatico.isSelected()),i,CA );

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
        log.info( " currupio VProdPricGen.actionPerformed - " + e.getActionCommand());

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
    		

            if( !salir ) {
                createProductPrices();
            } else {
                dispose();
            }
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    void zoom() {
        log.info( "VProdPricGen.zoom" );

        Integer M_Product_ID = getSelectedRowKey();

        if( M_Product_ID == null ) {
            return;
        }

        AEnv.zoom( MProduct.Table_ID,M_Product_ID.intValue());
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
        log.info( "VProdPricGen.vetoableChange - " + e.getPropertyName() + "=" + e.getNewValue());
    }    // vetoableChange

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
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
            changeSelectedPrice();
            creandoTabla = false;
        }
    }    // tableChanged

    /**
     * Descripción de Método
     *
     */

    public void changeSelectedPrice() {
    	log.info( " currupio VProdPricGen.changeSelecedPrice" );
        boolean upda;
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

        miniTable.autoSize();
    }    // changeSelectedPrice

    /**
     * Descripción de Método
     *
     */

    private void createProductPrices() {
    	log.info( " currupio VProdPricGen.createProductPrices" );

        // Prepare Process

        int proc_ID = DB.getSQLValue( null,"SELECT AD_Process_ID FROM AD_Process WHERE value='M_ProductPriceGen'" );

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

            // Add Parameter - User=x

            ip = new MPInstancePara( instance,40 );
            ip.setParameter( "User",String.valueOf( m_UpdatedBy ));

            if( !ip.save()) {
                String msg = "No Parameter added";    // not translated

                info.setText( msg );
                log.log( Level.SEVERE,msg );

                return;
            }

            // Execute Process
            //JOptionPane.showMessageDialog( null,"En VProducpriceGen.dotIT this= \n"+ this+"\n"+"pi="+pi,"..Fin", JOptionPane.INFORMATION_MESSAGE );
            ProcessCtl worker = new ProcessCtl( this,pi,null );

            worker.start();    // complete tasks in unlockUI / afterCreateProductPrices
        }

        confirmPanelGen.getOKButton().setEnabled( true );
    }

    /**
     * Descripción de Método
     *
     *
     * @param pi
     */

    private void afterCreateProductPrices( ProcessInfo pi ) {
    	log.info( " currupio VProdPricGen.afterCreateProductPrice" );
        try {
            String sql = new String( "DELETE FROM AD_PInstance_Para WHERE AD_PInstance_ID = " + pi.getAD_PInstance_ID());

            DB.executeUpdate( sql.toString());
            no = DB.executeUpdate( "DELETE FROM I_ProductPrice" );
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
//            executeQuery();
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
    	log.info( " currupio VProdPricGen.doIT" );

        // Prepare Process
    																							// en realidad invoca a ProductPriceTemp
        int proc_ID = DB.getSQLValue( null,"SELECT AD_Process_ID FROM AD_Process WHERE value='ProductPriceGen'" );

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
        }
    }

    /**
     * Descripción de Método
     *
     */

    private void updateIt() {
        log.info( " currupio VProdPricGen.updateIt" );

        ArrayList toUpdate    = new ArrayList();
        ArrayList toCalculate = new ArrayList();

        // Get selected entries

        int rows = miniTable.getRowCount();

        for( int i = 0;i < rows;i++ ) {
            IDColumn id = ( IDColumn )miniTable.getValueAt( i,0 );    // ID in column 0
            boolean upda = (( Boolean )miniTable.getValueAt( i,CA )).booleanValue();
            boolean calc = (( Boolean )miniTable.getValueAt( i,CC )).booleanValue();
            BigDecimal selPrice = ( BigDecimal )miniTable.getValueAt( i,CPS );

            if( (id != null) && upda ) {
                toUpdate.add( id.getRecord_ID());
            }

            if( (id != null) && calc ) {
                toCalculate.add( id.getRecord_ID());
            }

            if( id != null ) {
                //StringBuffer sql = new StringBuffer( "UPDATE I_ProductPrice SET SelectedPrice=" );
            	StringBuffer sql = new StringBuffer( "UPDATE I_ProductPrice SET " );

                // sql.append( selPrice );
            	// sql.append( ", updatePrice = " );
                sql.append( " updatePrice = " );

                if( upda ) {
                    sql.append( "'Y', calculatePrice = " );
                } else {
                    sql.append( "'N', calculatePrice = " );
                }

                if( calc ) {
                    sql.append( "'Y' WHERE M_Product_ID =" + id.getRecord_ID());
                } else {
                    sql.append( "'N' WHERE M_Product_ID =" + id.getRecord_ID());
                }
                
                //JOptionPane.showMessageDialog( null,"En VProducpriceGen.updateIT \n"+ sql.toString(),"..Fin", JOptionPane.INFORMATION_MESSAGE );
                DB.executeUpdate( sql.toString());
            }
        }
    }    // saveSelection    
}    // VProdPricGen --> PriceList_Create




/*
 *  @(#)VProdPricGen.java   02.07.07
 * 
 *  Fin del fichero VProdPricGen.java
 *  
 *  Versión 2.2
 *
 */
