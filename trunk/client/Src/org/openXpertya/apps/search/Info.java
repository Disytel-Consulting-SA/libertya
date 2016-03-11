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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.PrintScreenPainter;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.grid.ed.Calculator;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.X_C_AllocationLine;
import org.openXpertya.model.X_C_POSJournal;
import org.openXpertya.model.X_M_EntidadFinancieraPlan;
import org.openXpertya.plugin.common.PluginInfoUtils;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.pos.view.PoSInfoProduct;
import org.openXpertya.pos.view.PoSInfoProductAttribute;
import org.openXpertya.process.ComponentActivation;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.MeasurableTask;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeStatsLogger;
/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class Info extends CDialog implements ListSelectionListener {

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param modal
     * @param WindowNo
     * @param tableName
     * @param keyColumn
     * @param value
     * @param multiSelection
     * @param whereClause
     *
     * @return
     */

    public static Info create( Frame frame,boolean modal,int WindowNo,String tableName,String keyColumn,String value,boolean multiSelection,String whereClause, boolean addSecurityValidation ) {
        Info info = null;
        
        /**
         * Logica para plugins - Redefinicion de clases Info
         */
        info = PluginInfoUtils.getInfo(tableName, frame, modal, WindowNo, value, multiSelection, whereClause, null, null, null, keyColumn);
        
        if (info!=null)        
        	;
        else if( tableName.equals( "C_BPartner" )) {
        	// System.out.println("creando info c_BPartner");
            info = new InfoBPartner( frame,modal,WindowNo,value,!Env.getContext( Env.getCtx(),"IsSOTrx" ).equals( "N" ),multiSelection,whereClause );
        } else if( tableName.equals( "M_Product" )) {
        	// System.out.println("creando info M_Product");
            info = Info.factoryProduct( frame,modal,WindowNo,0,0,value,multiSelection,whereClause );
        } else if( tableName.equals( "C_Invoice" )) {
        	// System.out.println("creando info C_Invoice");
            info = new InfoInvoice( frame,modal,WindowNo,value,multiSelection,whereClause );
        } else if( tableName.equals( "A_Asset" )) {
        	// System.out.println("creando info A_Asset");
            info = new InfoAsset( frame,modal,WindowNo,0,value,multiSelection,whereClause );
        } else if( tableName.equals( "C_Order" )) {
        	// System.out.println("creando info C_Order");
            info = new InfoOrder( frame,modal,WindowNo,value,multiSelection,whereClause );
        } else if( tableName.equals( "M_InOut" )) {
        	// System.out.println("creando info M_InOutr");
            info = new InfoInOut( frame,modal,WindowNo,value,multiSelection,whereClause );
        } else if( tableName.equals( "C_Payment" )) {
        	// System.out.println("creando info C_Payment");
            info = new InfoPayment( frame,modal,WindowNo,value,multiSelection,whereClause );
        } else if( tableName.equals( "C_CashLine" )) {
        	// System.out.println("creando info C_CashLine");
            info = new InfoCashLine( frame,modal,WindowNo,value,multiSelection,whereClause );
        } else if( tableName.equals( "S_ResourceAssigment" )) {
        	// System.out.println("creando info S_ResourceAssigment");
            info = new InfoAssignment( frame,modal,WindowNo,value,multiSelection,whereClause );
        } else if( tableName.equals( X_C_POSJournal.Table_Name )) {
            info = new InfoPOSJournal( frame,modal,WindowNo,tableName,keyColumn,multiSelection,whereClause );
        } else if( tableName.equals( X_C_AllocationLine.Table_Name )) {
        	info = new InfoAllocationLine(frame, modal, WindowNo, tableName, keyColumn, multiSelection, whereClause);
        } else if( tableName.equals( X_M_EntidadFinancieraPlan.Table_Name )) {
            info = new InfoEntidadFinancieraPlan( frame,modal,WindowNo,tableName,keyColumn,multiSelection,whereClause );
        } else {
        	// System.out.println("creando info otros..");
            info = new InfoGeneral( frame,modal,WindowNo,value,tableName,keyColumn,multiSelection,whereClause );
        }

        //
        info.setAddSecurityValidation(addSecurityValidation);

        AEnv.positionCenterWindow( frame,info );

        return info;
    }    // create

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     */

    public static void showBPartner( Frame frame,int WindowNo ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("C_BPartner", frame, false, WindowNo, "", false, "", null, null, !Env.getContext( Env.getCtx(),"IsSOTrx" ).equals( "N" ), null);
        
        if (info==null)
        	info = new InfoBPartner( frame,false,WindowNo,"",!Env.getContext( Env.getCtx(),"IsSOTrx" ).equals( "N" ),false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showBPartner

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     */

    public static void showAsset( Frame frame,int WindowNo ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("A_Asset", frame, false, WindowNo, "", false, "", null, null, null, null);
        
        if (info==null)
        	info = new InfoAsset( frame,false,WindowNo,0,"",false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showBPartner

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     */

    public static void showProduct( Frame frame,int WindowNo ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("M_Product", frame, false, WindowNo, "", false, "", Env.getContextAsInt( Env.getCtx(),WindowNo,"M_Warehouse_ID" ), Env.getContextAsInt( Env.getCtx(),WindowNo,"M_PriceList_ID" ), null, null);
        
        if (info==null)
        	info = factoryProduct( frame,false,WindowNo,Env.getContextAsInt( Env.getCtx(),WindowNo,"M_Warehouse_ID" ),Env.getContextAsInt( Env.getCtx(),WindowNo,"M_PriceList_ID" ),"",    // value
                                     false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showProduct

    public static InfoProduct factoryProduct(Frame frame, boolean modal, int WindowNo, int M_Warehouse_ID, int M_PriceList_ID, String value, boolean multiSelection, String whereClause, boolean isForPOS) {
    	InfoProduct info = null;
    	boolean attHetActive = MPreference.GetCustomPreferenceValueBool(ComponentActivation.COMP_AtributosHeterogeneos);
    	
    	if (isForPOS && attHetActive) {
    		info = new PoSInfoProductAttribute(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value, multiSelection, whereClause);
    	} else if(isForPOS && !attHetActive) {
    		info = new PoSInfoProduct(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value, multiSelection, whereClause);
    	} else if (attHetActive) {
    		info = new InfoProductAttribute(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value, multiSelection, whereClause);
    	} else {
    		info = new InfoProduct(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value, multiSelection, whereClause);
    	}

    	return info;
    }
    
    public static InfoProduct factoryProduct(Frame frame, boolean modal, int WindowNo, int M_Warehouse_ID, int M_PriceList_ID, String value, boolean multiSelection, String whereClause) {
    	return factoryProduct(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value, multiSelection, whereClause, false);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     * @param value
     */
   
    public static void showOrder( Frame frame,int WindowNo,String value ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("C_Order", frame, false, WindowNo, value, false, "", null, null, null, null);
        
        if (info==null)
        	info = new InfoOrder( frame,false,WindowNo,value,false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showOrder


    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     * @param value
     */

    public static void showInvoice( Frame frame,int WindowNo,String value ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("C_Invoice", frame, false, WindowNo, value, false, "", null, null, null, null);
        
        if (info==null)
        	info = new InfoInvoice( frame,false,WindowNo,value,false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showInvoice

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     * @param value
     */

    public static void showInOut( Frame frame,int WindowNo,String value ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("M_InOut", frame, false, WindowNo, value, false, "", null, null, null, null);
        
        if (info==null)
        	info = new InfoInOut( frame,false,WindowNo,value,false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showInOut

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     * @param value
     */

    public static void showPayment( Frame frame,int WindowNo,String value ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("C_Payment", frame, false, WindowNo, value, false, "", null, null, null, null);
        
        if (info==null)
        	info = new InfoPayment( frame,false,WindowNo,value,false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showPayment

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     * @param value
     */

    public static void showCashLine( Frame frame,int WindowNo,String value ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("C_CashLine", frame, false, WindowNo, value, false, "", null, null, null, null);
        
        if (info==null)
        	info = new InfoCashLine( frame,false,WindowNo,value,false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showCashLine

    /**
     * Descripción de Método
     *
     *
     * @param frame
     * @param WindowNo
     * @param value
     */

    public static void showAssignment( Frame frame,int WindowNo,String value ) {
        /* Logica para plugins - Redefinicion de clases Info  */
        Info info = PluginInfoUtils.getInfo("S_ResourceAssigment", frame, false, WindowNo, value, false, "", null, null, null, null);
        
        if (info==null)
        	info = new InfoAssignment( frame,false,WindowNo,value,false,"" );

        AEnv.showCenterWindow( frame,info );
    }    // showAssignment

    /** Descripción de Campos */

    static final int INFO_WIDTH = 800;

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param modal
     * @param WindowNo
     * @param tableName
     * @param keyColumn
     * @param multiSelection
     * @param whereClause
     */

    protected Info( Frame frame,boolean modal,int WindowNo,String tableName,String keyColumn,boolean multiSelection,String whereClause ) {
        super( frame,modal );
        log.info( "......WinNo=" + p_WindowNo + " WhereClause= " + whereClause +"tableName = "+  tableName);
        p_WindowNo       = WindowNo;
        p_tableName      = tableName;
        p_keyColumn      = keyColumn;
        p_multiSelection = multiSelection;

        if( (whereClause == null) || (whereClause.indexOf( '@' ) == -1) ) {
            p_whereClause = whereClause;
        } else {
            p_whereClause = Env.parseContext( Env.getCtx(),p_WindowNo,whereClause,false,false );

            if( p_whereClause.length() == 0 ) {
                log.log( Level.SEVERE,"Cannot parse context= " + whereClause );
            }
        }

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Info",ex );
        }
    }    // Info

    /** Descripción de Campos */

    protected int p_WindowNo;

    /** Descripción de Campos */

    protected String p_tableName;

    /** Descripción de Campos */

    protected String p_keyColumn;

    /** Descripción de Campos */

    protected boolean p_multiSelection;

    /** Descripción de Campos */

    protected String p_whereClause = "";

    /** Descripción de Campos */

    protected MiniTable p_table = new MiniTable();

    /** Descripción de Campos */

    private int m_keyColumnIndex = -1;
   
    /** Descripción de Campos */
    
    private boolean m_ok = false;

    /** Descripción de Campos */

    private boolean m_cancel = false;

    /** Descripción de Campos */

    private ArrayList m_results = new ArrayList( 3 );

    /** Descripción de Campos */

    protected Info_Column[] p_layout;

    /** Descripción de Campos */

    private String m_sqlMain;

    /** Descripción de Campos */

    private String m_sqlAdd;

    /** Descripción de Campos */

    protected boolean p_loadedOK = false;

    /** Descripción de Campos */

    private int m_SO_Window_ID = -1;

    /** Descripción de Campos */

    private int m_PO_Window_ID = -1;

    /** Descripción de Campos */

    private Worker m_worker = null;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    protected ConfirmPanel confirmPanel = new ConfirmPanel( true,true,true,true,true,true,true );

    /** Descripción de Campos */

    protected StatusBar statusBar = new StatusBar();

    /** Descripción de Campos */

    protected CPanel parameterPanel = new CPanel();

    /** Descripción de Campos */

    private JScrollPane scrollPane = new JScrollPane();
    
 
    
    //

    /** Descripción de Campos */

    private JPopupMenu popup = new JPopupMenu();

    /** Descripción de Campos */

    private JMenuItem calcMenu = new JMenuItem();

    protected JComponent getCenterComponent() {
    	return scrollPane;
    }
    
    /** Add Security Validation */
    private boolean addSecurityValidation = true;
    
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    protected void jbInit() throws Exception {
        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        southPanel.setLayout( southLayout );
        southPanel.add( confirmPanel,BorderLayout.CENTER );
        southPanel.add( statusBar,BorderLayout.SOUTH );
        getContentPane().add( southPanel,BorderLayout.SOUTH );
        this.getContentPane().add( parameterPanel,BorderLayout.NORTH );
        
       	this.getContentPane().add( getCenterComponent(),BorderLayout.CENTER );
        scrollPane.getViewport().add( p_table,null );
        
        //

        confirmPanel.addActionListener( this );
        confirmPanel.getResetButton().setVisible( hasReset());
        confirmPanel.getCustomizeButton().setVisible( hasCustomize());
        confirmPanel.getHistoryButton().setVisible( hasHistory());
        confirmPanel.getZoomButton().setVisible( hasZoom());

        //

        JButton print = ConfirmPanel.createPrintButton( true );
        
        print.addActionListener( this );
        confirmPanel.addButton( print );
        
        //
        
        // Maurix
        
        JButton bCopyToClipboard = ConfirmPanel.createCopiarRegistroButton( Msg.getMsg(Env.getCtx(), "Copy") );
        
        bCopyToClipboard.addActionListener( this );
//      bCopyToClipboard.setIcon(Env.getImageIcon("Copy16.gif"));
//      bCopyToClipboard.setToolTipText(Msg.getMsg(Env.getCtx(), "Copy"));
        confirmPanel.addButton( bCopyToClipboard );
        
        //

        popup.add( calcMenu );
        calcMenu.setText( Msg.getMsg( Env.getCtx(),"Calculator" ));
        calcMenu.setIcon( new ImageIcon( org.openXpertya.OpenXpertya.class.getResource( "images/Calculator16.gif" )));
        calcMenu.addActionListener( this );

        //

        p_table.getSelectionModel().addListSelectionListener( this );
        enableButtons();
        
		parameterPanel.getActionMap().put("gotogrid", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (p_table.getRowCount() > 0) {
					p_table.requestFocus();
					p_table.setRowSelectionInterval(0, 0);
				}
			}
		});
		parameterPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "gotogrid");
		
		p_table.getActionMap().put("saveSelection", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(true);
			}
		});
		p_table.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "saveSelection");
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean loadedOK() {
        return p_loadedOK;
    }    // loadedOK

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param error
     */

    public void setStatusLine( String text,boolean error ) {
        statusBar.setStatusLine( text,error );
        Thread.yield();
    }    // setStatusLine

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setStatusDB( String text ) {
        statusBar.setStatusDB( text );
    }    // setStatusDB

    
    protected StringBuffer mainSelectSentence = null;
    
    /**
     * Descripción de Método
     *
     *
     * @param layout
     * @param from
     * @param staticWhere
     * @param orderBy
     */
    protected void prepareTable( Info_Column[] layout,String from,String staticWhere,String orderBy ) {
        p_layout = layout;

        StringBuffer sql = new StringBuffer( "SELECT " );
        mainSelectSentence = new StringBuffer( "SELECT " );

        for( int i = 0;i < layout.length;i++ ) {
        	log.fine("En prepareTable = layout.campo= "+ layout[i].getColSQL()+"....");
            if( i > 0 ) {
                sql.append( ", " );
                mainSelectSentence.append( ", " );
            }

            sql.append( layout[ i ].getColSQL());
            mainSelectSentence.append( layout[ i ].getColSQL());

            // adding ID column

            if( layout[ i ].isIDcol()) {
                sql.append( "," ).append( layout[ i ].getIDcolSQL());
                mainSelectSentence.append( "," ).append( layout[ i ].getIDcolSQL());
            }

            // add to model

            p_table.addColumn( layout[ i ].getColHeader());

            if( layout[ i ].isColorColumn()) {
                p_table.setColorColumn( i );
            }

            if( layout[ i ].getColClass() == IDColumn.class ) {
                m_keyColumnIndex = i;
            }
        }

        // set editors (two steps)

        for( int i = 0;i < layout.length;i++ ) {
            p_table.setColumnClass( i,layout[ i ].getColClass(),layout[ i ].isReadOnly(),layout[ i ].getColHeader());
        }

        sql.append( " FROM " ).append( from );

        //
        
        sql.append( " WHERE " ).append( staticWhere );
        m_sqlMain = sql.toString();
        m_sqlAdd  = "";
        
       
        if( (orderBy != null) && (orderBy.length() > 0) ) {
            m_sqlAdd = " ORDER BY " + orderBy;
        }

        if( m_keyColumnIndex == -1 ) {
            log.log( Level.SEVERE,"No KeyColumn - " + sql );
        }

        // Table Selection

        p_table.setRowSelectionAllowed( true );
        p_table.addMouseListener( this );
        p_table.setMultiSelection( p_multiSelection );

        // Window Sizing

        parameterPanel.setPreferredSize( new Dimension( getInfoWidth(),parameterPanel.getPreferredSize().height ));
        scrollPane.setPreferredSize( new Dimension( getInfoWidth(),400 ));
        
     // dREHER, poder marcar con espacio o enter, sin necesidad del boton
		p_table.addKeyListener(new KeyListener()
		{

			public void keyPressed(KeyEvent arg0) 
			{
				int keyCode = Integer.valueOf(arg0.getKeyCode());
				if(keyCode==32 || keyCode==10) // espacio o enter
					dispose(true);
			}

			public void keyReleased(KeyEvent arg0) {
			}

			public void keyTyped(KeyEvent arg0) {
			}
			
		});
		
    }    // prepareTable

    /**
     * Descripción de Método
     *
     */

    protected void executeQuery() {

        // ignore when running
        if( (m_worker != null) && m_worker.isAlive()) {
            return;
        }

        m_worker = new Worker();
        m_worker.start();
    }    // executeQuery

    /**
     * Descripción de Método
     *
     */

    protected void saveSelection() {

        // Already disposed

        if( p_table == null ) {
            return;
        }

        log.config( "En saveSelection con OK=" + m_ok );
        if( !m_ok )    // did not press OK
        {
            m_results.clear();
            p_table.removeAll();
            p_table = null;

            return;
        }

        // Multi Selection
        
        if( p_multiSelection ) {}
        else    // singleSelection
        {
        	
            Object data = getSelectedRowKey();
            
            if( data != null ) {
                m_results.add( data );
            }
        }

        log.config("<..<"+ getSelectedSQL());

        // Save Settings of detail info screens

        saveSelectionDetail();
       
        //Clean-up
        p_table.removeAll();
        p_table = null;
    }    // saveSelection

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected Integer getSelectedRowKey() {
    
        int row = p_table.getSelectedRow();

        if( (row != -1) && (m_keyColumnIndex != -1) ) {
            Object data = p_table.getModel().getValueAt( row,m_keyColumnIndex );

            if( data instanceof IDColumn ) {
                data = (( IDColumn )data ).getRecord_ID();
            }

            if( data instanceof Integer ) {
                return( Integer )data;
            }
        }

        return null;
    }    // getSelectedRowKey

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object[] getSelectedKeys() {
        if( !m_ok || (m_results.size() == 0) ) {
            return null;
        }

        return m_results.toArray();
    }    // getSelectedKeys;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getSelectedKey() {
        if( !m_ok || (m_results.size() == 0) ) {
            return null;
        }

        return m_results.get( 0 );
    }    // getSelectedKey

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCancelled() {
        return m_cancel;
    }    // isCancelled

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSelectedSQL() {

        // No results

        Object[] keys = getSelectedKeys();

        if( (keys == null) || (keys.length == 0) ) {
            log.config( "No Results - OK=" + m_ok + ", Cancel=" + m_cancel );

            return "";
        }

        //

        StringBuffer sb = new StringBuffer( getKeyColumn());

        if( keys.length > 1 ) {
            sb.append( " IN (" );
        } else {
            sb.append( "=" );
        }

        // Add elements

        for( int i = 0;i < keys.length;i++ ) {
            if( getKeyColumn().endsWith( "_ID" )) {
                sb.append( keys[ i ].toString()).append( "," );
            } else {
                sb.append( "'" ).append( keys[ i ].toString()).append( "'," );
            }
        }

        sb.replace( sb.length() - 1,sb.length(),"" );

        if( keys.length > 1 ) {
            sb.append( ")" );
        }

        return sb.toString();
    }    // getSelectedSQL;

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // Popup => Calculator

        if( e.getSource().equals( calcMenu )) {
            BigDecimal number = null;
            Object     data   = p_table.getSelectedValue();

            try {
                if( data != null ) {
                    if( data instanceof BigDecimal ) {
                        number = ( BigDecimal )data;
                    } else {
                        number = new BigDecimal( data.toString());
                    }
                }
            } catch( Exception ex ) {
            }

            Calculator c = new Calculator( null,number );

            c.setVisible( true );

            return;
        }    // popup

        // Confirm Panel

        String cmd = e.getActionCommand();

        if( cmd.equals( ConfirmPanel.A_OK )) {
        	//OptionPane.showMessageDialog( null,"En Info, con cmd.equal=confirpanel.a_ok"+"..Fin",null, JOptionPane.INFORMATION_MESSAGE );
            dispose( true );
        } else if( cmd.equals( ConfirmPanel.A_CANCEL )) {
            m_cancel = true;
            dispose( false );
        } else if( cmd.equals( ConfirmPanel.A_HISTORY )) {
            showHistory();
        } else if( cmd.equals( ConfirmPanel.A_CUSTOMIZE )) {
            customize();
        } else if( cmd.equals( ConfirmPanel.A_ZOOM )) {
            zoom();
        } else if( cmd.equals( ConfirmPanel.A_RESET )) {        	
            doReset();
        } else if( cmd.equals( ConfirmPanel.A_COPY )) { /* Maurix */        	
        	copyToClipBoard();
        } else if( cmd.equals( ConfirmPanel.A_PRINT )) {
            PrintScreenPainter.printScreen( this );

        // Default

        } else {
           	executeQuery();
        }
    }   // actionPerformed

    private void copyToClipBoard() { /* Maurix */

    	String valores = p_table.getClipboardData();
    	StringSelection ss = new StringSelection(valores);
    	Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

    }
    
    /**
     * Descripción de Método
     *
     *
     * @param AD_Window_ID
     * @param zoomQuery
     */

    protected void zoom( int AD_Window_ID,MQuery zoomQuery ) {
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        final AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,zoomQuery )) {
            return;
        }

        // Modal Window causes UI lock

        if( isModal()) {
            setModal( false );    // remove modal option has no effect
            dispose();            // VLookup.actionButton - Result = null (not cancelled)
        } else {
            setCursor( Cursor.getDefaultCursor());
        }

        // VLookup gets info after method finishes

        new Thread() {
            public void run() {
                try {
                    sleep( 50 );
                } catch( Exception e ) {
                }

                AEnv.showCenterScreen( frame );
            }
        }.start();
    }    // zoom

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        dispose( false );
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param ok
     */

    public void dispose( boolean ok ) {
        log.config( "OK=" + ok );
        m_ok = ok;

        // End Worker

        if( m_worker != null ) {

            // worker continues, but it does not block UI

            if (m_worker.isAlive()) {
                m_worker.interrupt();
            	try {
            		m_worker.join(30000);
            	} catch (InterruptedException e) {
            		
            	}
            }

            log.config( "Worker alive=" + m_worker.isAlive());
        }

        m_worker = null;

        //

        saveSelection();
        removeAll();
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    String getTableName() {
        return p_tableName;
    }    // getTableName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    String getKeyColumn() {
        return p_keyColumn;
    }    // getKeyColumn

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

        enableButtons();
    }    // calueChanged

    /**
     * Descripción de Método
     *
     */

    protected void enableButtons() {
        boolean enable = p_table.getSelectedRow() != -1;

        confirmPanel.getOKButton().setEnabled( enable );

        if( hasHistory()) {
            confirmPanel.getHistoryButton().setEnabled( enable );
        }

        if( hasZoom()) {
            confirmPanel.getZoomButton().setEnabled( enable );
        }
    }    // enableButtons

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected abstract String getSQLWhere();

    /**
     * Descripción de Método
     *
     *
     * @param pstmt
     *
     * @throws SQLException
     */

    protected abstract void setParameters( PreparedStatement pstmt ) throws SQLException;

    /**
     * Descripción de Método
     *
     */

    void doReset() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasReset() {
        return false;
    }

    /**
     * Descripción de Método
     *
     */

    void showHistory() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasHistory() {
        return false;
    }

    /**
     * Descripción de Método
     *
     */

    void customize() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasCustomize() {
        return false;
    }

    /**
     * Descripción de Método
     *
     */

    void zoom() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    boolean hasZoom() {
        return false;
    }

    /**
     * Descripción de Método
     *
     */

    void saveSelectionDetail() {}
    	
    /**
     * Descripción de Método
     *
     *
     * @param tableName
     * @param isSOTrx
     *
     * @return
     */

    protected int getAD_Window_ID( String tableName,boolean isSOTrx ) {
        if( !isSOTrx && (m_PO_Window_ID > 0) ) {
            return m_PO_Window_ID;
        }

        if( m_SO_Window_ID > 0 ) {
            return m_SO_Window_ID;
        }

        //

        String sql = "SELECT AD_Window_ID, PO_Window_ID FROM AD_Table WHERE TableName=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql, PluginUtils.getPluginInstallerTrxName() );
            pstmt.setString( 1,tableName );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_SO_Window_ID = rs.getInt( 1 );
                m_PO_Window_ID = rs.getInt( 2 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAD_Window_ID",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        if( !isSOTrx && (m_PO_Window_ID > 0) ) {
            return m_PO_Window_ID;
        }

        return m_SO_Window_ID;
    }    // getAD_Window_ID

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {

        // log.fine( "Info.mouseClicked",
        // "ClickCount=" + e.getClickCount() + ", Right=" + SwingUtilities.isRightMouseButton(e)
        // + ", r=" + m_table.getSelectedRow() + ", c=" + m_table.getSelectedColumn());

        // Double click with selected row => exit

        if( (e.getClickCount() > 1) && (p_table.getSelectedRow() != -1) ) {
            dispose( true );    // double_click same as OK
        }

        // Right Click => start Calculator

        else if( SwingUtilities.isRightMouseButton( e )) {
            popup.show( e.getComponent(),e.getX(),e.getY());
        }
    }                           // mouseClicked

    public void queryExecute(){
    	
    }
 
    /**
     * Descripción de Clase
     *
     *
     * @version    2.1, 02.07.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class Worker extends Thread {

    	/** Constantes para la funcionalidad de limitación de resultados */
        public final String MESSAGE_QUERY_EXCEEDS_ROWS_CALC_METHOD	= "TooManyResultsCalculated";
        public final String MESSAGE_QUERY_EXCEEDS_ROWS_EST_METHOD	= "TooManyResultsEstimated";        
    	public final String PREFERENCE_INFO_ROW_COUNT_METHOD 		= "P|InfoRowCountMethod";
    	public final String PREFERENCE_INFO_ROW_COUNT_MAX_ALLOWED 	= "P|InfoMaxRowsAllowed";
        public final String INFO_ROW_COUNT_METHOD_CALCULATED	 	= "C";
        public final String INFO_ROW_COUNT_METHOD_ESTIMATED 		= "E";
        public final int 	INFO_ROW_COUNT_MAX_ALLOWED_DEFAULT 		= 1000;

        
        /**
         * Descripción de Método
         *
         */

        public void run() {
        	statsLogBeginTask(); // Log de tiempos 
        	
        	setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
            setStatusLine( Msg.getMsg( Env.getCtx(),"StartSearch" ),false );

            // Clear Table

            p_table.setRowCount( 0 );

            //

            StringBuffer sql      = new StringBuffer( m_sqlMain );
            String       dynWhere = getSQLWhere();

            if( dynWhere.length() > 0 ) {
                sql.append( dynWhere );    // includes first AND
            }

            sql.append( m_sqlAdd );

            String xSql = Msg.parseTranslation( Env.getCtx(),sql.toString());    // Variables

            // log.finer(xSql);
            if(addSecurityValidation){
            	xSql = MRole.getDefault().addAccessSQL( xSql,getTableName(),MRole.SQL_FULLYQUALIFIED,MRole.SQL_RO );
            }
            log.finer( "------------ xSql = "+xSql );
            
            try {

            	/* 
            	 * ===================== VALIDACION POR VOLUMEN DE DATOS ===================== 
            	 * Si la cantidad de registros demasiado elevada, requerir refinar 
            	 * los criterios de búsqueda, de manera de obtener un resultset 
            	 * razonablemente pequeño.  
            	 * 
            	 * La configuracion especifica puede realizarse en la tabla AD_Preference
            	 * """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
            	 * Es posible especificar el método a utilizar bajo la clave 'InfoRowCountMethod'
            	 * 		(E)stimado: rapido pero no preciso 	(INFO_ROW_COUNT_METHOD_ESTIMATED)
            	 * 		(C)alculado: lento pero exacto 		(INFO_ROW_COUNT_METHOD_CALCULATED)
            	 * Es posible especificar la cantidad maxima de registros bajo la clave 'InfoMaxRowsAllowed'
            	 */
            	
            	// Obtener el numero maximo de filas permitidas (InfoMaxRowsAllowed en AD_Preference)
            	int maxRows = INFO_ROW_COUNT_MAX_ALLOWED_DEFAULT;
            	try {
            		String infoMaxRowsAllowed = Env.getPreference(Env.getCtx(), -1, "InfoMaxRowsAllowed", false);	
            		if (infoMaxRowsAllowed != null && infoMaxRowsAllowed.length() > 0)
            			maxRows = Integer.parseInt(infoMaxRowsAllowed);
            	} catch (Exception e) {}

            	// Si maxRows == 0, entonces no realizar validación alguna
            	if (maxRows > 0)
            	{
	            	// Obtener el metodo a utilizar para determinar los registros (C)alculado o (E)stimado, en AD_Preference
	            	String infoRowCountMethod = INFO_ROW_COUNT_METHOD_CALCULATED; 
	            	try {
	            		String prefMethod = Env.getPreference(Env.getCtx(), -1, "InfoRowCountMethod", false);
	            		if (prefMethod != null && prefMethod.length() > 0)
	            			infoRowCountMethod = prefMethod;
	            	} catch (Exception e) {}
	            	
	            	// Obtener el resultado de las líneas (estimado o calculado)
	            	int count = 0;
	            	String returnMessage = "";
	            	if (INFO_ROW_COUNT_METHOD_ESTIMATED.equalsIgnoreCase(infoRowCountMethod)) 	{
	            		count = estimateRowCount(xSql);
	            		returnMessage = MESSAGE_QUERY_EXCEEDS_ROWS_EST_METHOD;
	            	}
	            	else if (INFO_ROW_COUNT_METHOD_CALCULATED.equalsIgnoreCase(infoRowCountMethod))	{
	            		count = calculateRowCount(xSql);
	            		returnMessage = MESSAGE_QUERY_EXCEEDS_ROWS_CALC_METHOD;
	            	}
	            	
	            	// Validar acordemente
	            	if (count > maxRows)
	            	{
	            		setCursor( Cursor.getDefaultCursor());
	            		setStatusLine( Msg.getMsg( Env.getCtx(), returnMessage, new Object[]{count, maxRows} ),true );
	                    setStatusDB("-");
	                	return;
	            	}
            	}
            	
            	/*
            	 * ===================== FIN VALIDACION POR VOLUMEN DE DATOS ===================== 
            	 */
            	
                PreparedStatement pstmt = DB.prepareStatement( xSql, PluginUtils.getPluginInstallerTrxName() );

                setParameters(pstmt);
   

                 log.fine( "Info.Worker.run - start query pstmt= "+pstmt);

                ResultSet rs = pstmt.executeQuery();

                //ResultSet rs=executeQuery(xSql);   

                 log.fine( "Info.Worker.run - end query");
      
                while( !isInterrupted() && rs.next()) {

                 //!isInterrupted() Condicion quitada del while para prueba
               // while(rs.next()) {
                	//log.fine("Toy en el while");

                    int row = p_table.getRowCount();
                    

                    p_table.setRowCount( row + 1 );

                    int colOffset = 1;    // columns start with 1

                    for( int col = 0;col < p_layout.length;col++ ) {
                        Object data     = null;
                        Class  c        = p_layout[ col ].getColClass();
                        int    colIndex = col + colOffset;

                        if( c == IDColumn.class ) {
                            data = new IDColumn( rs.getInt( colIndex ));
                        } else if( c == Boolean.class ) {
                            data = new Boolean( "Y".equals( rs.getString( colIndex )));
                        } else if( c == Timestamp.class ) {
                            data = rs.getTimestamp( colIndex );
                        } else if( c == BigDecimal.class ) {
                            data = rs.getBigDecimal( colIndex );
                        } else if( c == Double.class ) {
                            data = new Double( rs.getDouble( colIndex ));
                        } else if( c == Integer.class ) {
                            data = new Integer( rs.getInt( colIndex ));
                        } else if( c == KeyNamePair.class ) {
                            String display = rs.getString( colIndex );
                            int    key     = rs.getInt( colIndex + 1 );

                            data = new KeyNamePair( key,display );
                            colOffset++;
                        } else {
                            data = rs.getString( colIndex );
                        }

                        // store

                        p_table.setValueAt( data,row,col );
                        if( p_layout[col].getColHeader().compareTo("Instance")==0){
                        	p_table.getColumnModel().getColumn(col).setPreferredWidth(0);
                            p_table.getColumnModel().getColumn(col).setMinWidth(0);
                            p_table.getColumnModel().getColumn(col).setMaxWidth(0);                            
                        }
                        
                        if( p_layout[col].getColHeader().compareTo(Msg.translate(Env.getCtx(),"PriceListVersion"))==0){
                        	p_table.getColumnModel().getColumn(col).setPreferredWidth(0);
                            p_table.getColumnModel().getColumn(col).setMinWidth(0);
                            p_table.getColumnModel().getColumn(col).setMaxWidth(0);                            
                        }
                        // log.fine( "r=" + row + ", c=" + col + " " + m_layout[col].getColHeader(),
                        // "data=" + data.toString() + " " + data.getClass().getName() + " * " + m_table.getCellRenderer(row, col));

                    }
                }

                log.finer( "Interrupted=" + isInterrupted());
                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,xSql,e );
            }

            p_table.autoSize();
            
            SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {								
					queryExecute();
				}
			}); 

            //

            setCursor( Cursor.getDefaultCursor());

            int no = p_table.getRowCount();
            
            setStatusLine( Integer.toString( no ) + " " + Msg.getMsg( Env.getCtx(),"SearchRows_EnterQuery" ),false );
            setStatusDB( Integer.toString( no ));

            if( no == 0 ) {
                log.fine("tabla sin columnas"+ xSql );
                
            }
            
            statsLogEndTask(); // Fin log de tiempos
            
        }    // run
        
        /**
         * Estima el numero de registros que devolverá la consulta, usando la consulta general.
         * ES IMPORTANTE COMPRENDER QUE ES UN ESTIMADO EN FUNCION DEL EXPLAIN PSQL DEL QUERY.
         * Esto es mucho más rápido que realizar COUNT(1) del sql a ejecutar (la única ganancia 
         * en el caso del COUNT(1) sería la no invocación a funciones procedurales de las columnas). 
         * @param xSql el query general
         */
        protected int estimateRowCount(String xSql)
        {
        	int count = 0;
        	try
        	{
        		// preparar la consulta, pero no ejecutarla, solo cargar los parametros para tener la consulta completa
        		PreparedStatement pstmtCount = DB.prepareStatement( xSql, PluginUtils.getPluginInstallerTrxName() );
        		setParameters(pstmtCount);
        		
        		// Copiar el query a partir del pstmt y obtener el estimado segun funcion count_estimate        		
        		String finalQuery = pstmtCount.toString();
        		finalQuery = finalQuery.substring(finalQuery.indexOf("SELECT"));
        		finalQuery = finalQuery.substring(0, finalQuery.lastIndexOf("]"));
            	count = DB.getSQLValue(PluginUtils.getPluginInstallerTrxName(), " SELECT count_estimate('" + finalQuery.replaceAll("'", "''") + "')" );
            	return count;
        	}
        	catch (Exception e)
        	{
        		// Ante cualquier problema, ignorar la validación y ejecutar normalmente
        		log.warning("Error estimating rowCount - ignoring");
        	}
        	return count;
        }

        
        /**
         * Calcula el numero de registros que devolverá la consulta, realizando modificaciones
         * adicionales sobre el query general. 
         * ES IMPORTANTE COMPRENDER QUE NO ES UN ESTIMADO, SINO UN CALCULO BASADO EN COUNT(1)
         * Esto es más lento que el estimado, pero obviamente es un dato exacto 
         * @param queryWihtoutParams el query general
         */
        protected int calculateRowCount(String queryWihtoutParams)
        {
        	// total de registros del resultset
        	int count = 0;
        	try
        	{
        		// preparar la consulta, pero no ejecutarla, solo cargar los parametros para tener la consulta completa
        		PreparedStatement pstmtCount = DB.prepareStatement( queryWihtoutParams, PluginUtils.getPluginInstallerTrxName() );
        		setParameters(pstmtCount);
        		        		
        		// Copiar el query a partir del pstmt, obteniendo la consulta con parametros cargados
        		// evitar problemas por eventuales espacios dobles (luego no encuentra indexOf)
        		String queryWithParams = pstmtCount.toString().replaceAll("  ", " ");
        		queryWihtoutParams = queryWihtoutParams.replaceAll("  ", " ");
        		// Buscar el inicio del query (antes presenta información del preparedStatement
        		queryWithParams = queryWithParams.substring(queryWithParams.indexOf("SELECT"));
        		
        		/* 
        		 *  Buscar el FROM principal en el query CON parámetros (unicamente tenemos conocimiento
        		 *  de la ubicación de dicho FROM en el query SIN parámetros cargados, pero éste no nos
        		 * es de utilidad para poder ejecutar la consulta dado que le faltan los params).
        		 * Voy a buscar una subcadena del query SIN parámetros en la cadena del query CON params
        		 * a fin de identificar la ubicación del FROM en el query CON params.
        		 */
        		// Posicion donde inicia el FROM principal en la consulta SIN parametros seteados
        		int fromPos = mainSelectSentence.length() + 1;
        		// Primer question mark a partir del FROM principal en la consulta SIN parametros seteados
        		int firstQM = queryWihtoutParams.substring(mainSelectSentence.length()).indexOf('?') + fromPos;
        		// si no hay un ?, tomar hasta el order by, y sino hasta el final
        		if (firstQM == fromPos-1)
        		{
        			firstQM = queryWihtoutParams.substring(mainSelectSentence.length()).lastIndexOf("ORDER BY") + fromPos;
        			if (firstQM == fromPos-1)
        				firstQM = queryWihtoutParams.length()-1;
        		}
        		// Determinar la posicion de inicio del FROM en la consulta CON parametros ya reemplazados
        		int fromPosInFinalQuery = queryWithParams.indexOf(queryWihtoutParams.substring(fromPos, firstQM - 1));
        		 
        		// reemplazar las columnas del select por el COUNT(1) en la consulta CON parametros
        		// (ahora ya sabemos que parte debemos remover del query)
        		queryWithParams = " SELECT COUNT(1) " + queryWithParams.substring(fromPosInFinalQuery); 
        			
        		// Quitar adicionales en la consulta que no son necesarios en este caso
        		if (queryWithParams.lastIndexOf("]") > -1)
        			queryWithParams = queryWithParams.substring(0, queryWithParams.lastIndexOf("]"));
        		if (queryWithParams.lastIndexOf("ORDER BY") > -1)
        			queryWithParams = queryWithParams.substring(0, queryWithParams.lastIndexOf("ORDER BY"));
        		
            	count = DB.getSQLValue(PluginUtils.getPluginInstallerTrxName(), queryWithParams);
            	return count;
	            
        	}
        	catch (Exception e)
        	{
        		// Ante cualquier problema, ignorar la validación y ejecutar normalmente
        		log.warning("Error calculating rowCount - ignoring");
        	}
        	return count;
        }
        
        private void statsLogBeginTask() {
        	if (Info.this instanceof InfoOrder) {
        		TimeStatsLogger.beginTask(MeasurableTask.REFRESH_INFO_ORDER);
        	} else if (Info.this instanceof InfoBPartner) {
        		TimeStatsLogger.beginTask(MeasurableTask.REFRESH_INFO_BPARTNER);
        	}
        }
        
        private void statsLogEndTask() {
        	if (Info.this instanceof InfoOrder) {
            	TimeStatsLogger.endTask(MeasurableTask.REFRESH_INFO_ORDER);
        	} else if (Info.this instanceof InfoBPartner) {
        		TimeStatsLogger.endTask(MeasurableTask.REFRESH_INFO_BPARTNER);
        	}
        }
    }    // Worker
    
    protected int getInfoWidth() {
    	return INFO_WIDTH;
    }

	protected boolean isAddSecurityValidation() {
		return addSecurityValidation;
	}

	protected void setAddSecurityValidation(boolean addSecurityValidation) {
		this.addSecurityValidation = addSecurityValidation;
	}
}        // Info



/*
 *  @(#)Info.java   02.07.07
 * 
 *  Fin del fichero Info.java
 *  
 *  Versión 2.1
 *
 */