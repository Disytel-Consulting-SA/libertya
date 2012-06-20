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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.compiere.swing.CCheckBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
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

public class PAttributeInstance extends CDialog implements ListSelectionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param title
     * @param M_Warehouse_ID
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param C_BPartner_ID
     */

    public PAttributeInstance( JFrame parent,String title,int M_Warehouse_ID,int M_Locator_ID,int M_Product_ID,int C_BPartner_ID,int WindowNo ) {
        super( parent,Msg.getMsg( Env.getCtx(),"PAttributeInstance" ) + title,true );
        init( M_Warehouse_ID,M_Locator_ID,M_Product_ID,C_BPartner_ID,WindowNo );
        AEnv.showCenterWindow( parent,this );
    }    // PAttributeInstance

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param title
     * @param M_Warehouse_ID
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param C_BPartner_ID
     */

    public PAttributeInstance( JDialog parent,String title,int M_Warehouse_ID,int M_Locator_ID,int M_Product_ID,int C_BPartner_ID,int WindowNo ) {
        super( parent,Msg.getMsg( Env.getCtx(),"PAttributeInstance" ) + title,true );
        init( M_Warehouse_ID,M_Locator_ID,M_Product_ID,C_BPartner_ID,WindowNo );
        AEnv.showCenterWindow( parent,this );
    }    // PAttributeInstance

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param C_BPartner_ID
     */

    private void init( int M_Warehouse_ID,int M_Locator_ID,int M_Product_ID,int C_BPartner_ID,int WindowNo ) {
        log.info( "M_Warehouse_ID=" + M_Warehouse_ID + ", M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID );
        m_M_Warehouse_ID = M_Warehouse_ID;
        m_M_Locator_ID   = M_Locator_ID;
        m_M_Product_ID   = M_Product_ID;
        m_WindowNo       = WindowNo;
        m_M_PriceList_Version_ID = 0;
        
        int M_PriceList_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, "M_PriceList_ID");
        
        if (M_PriceList_ID != 0) {
        	MPriceList pl = new MPriceList(Env.getCtx(), M_PriceList_ID, null);
        	MPriceListVersion plv = pl.getPriceListVersion(null);
        	if (plv != null)
        		m_M_PriceList_Version_ID = plv.getM_PriceList_Version_ID();
        }
        
        try {
            jbInit();
            dynInit( C_BPartner_ID );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"PAttributeInstance",e );
        }
    }    // init

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout northLayout = new BorderLayout();

    /** Descripción de Campos */

    private JScrollPane centerScrollPane = new JScrollPane();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private CCheckBox showAll = new CCheckBox( Msg.getMsg( Env.getCtx(),"ShowAll" ));

    //

    /** Descripción de Campos */

    private MiniTable m_table = new MiniTable();

    /** Descripción de Campos */

    private DefaultTableModel m_model;

    // Parameter

    /** Descripción de Campos */

    private int m_M_Warehouse_ID;

    /** Descripción de Campos */

    private int m_M_Locator_ID;

    /** Descripción de Campos */

    private int m_M_Product_ID;

    /** */
    
    private int m_M_PriceList_Version_ID;
    
    
    //

    /** Descripción de Campos */

    private int m_M_AttributeSetInstance_ID = -1;

    /** Descripción de Campos */

    private String m_M_AttributeSetInstanceName = null;

    /** Descripción de Campos */

    private String m_sql;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( PAttributeInstance.class );

    /** */
    
    private int m_WindowNo;
    
    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        mainPanel.setLayout( mainLayout );
        this.getContentPane().add( mainPanel,BorderLayout.CENTER );

        // North

        northPanel.setLayout( northLayout );
        northPanel.add( showAll,BorderLayout.EAST );
        showAll.setSelected(true);
        showAll.addActionListener( this );
        mainPanel.add( northPanel,BorderLayout.NORTH );

        // Center

        mainPanel.add( centerScrollPane,BorderLayout.CENTER );
        centerScrollPane.getViewport().add( m_table,null );

        // South

        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /** Descripción de Campos */

    private static ColumnInfo[] s_layout = new ColumnInfo[] {
        new ColumnInfo( " ","asi.M_AttributeSetInstance_ID",IDColumn.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"Description" ),"asi.Description",String.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"Lot" ),"asi.Lot",String.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"SerNo" ),"asi.SerNo",String.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"GuaranteeDate" ),"asi.GuaranteeDate",Timestamp.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"M_Locator_ID" ),"l.Value",KeyNamePair.class,"s.M_Locator_ID" ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"QtyOnHand" ),"s.QtyOnHand",Double.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"QtyReserved" ),"s.QtyReserved",Double.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"QtyOrdered" ),"s.QtyOrdered",Double.class ),

        new ColumnInfo( Msg.translate( Env.getCtx(),"PriceStd" ),"bomPriceStd(p.M_Product_ID, ?, asi.M_AttributeSetInstance_ID)",String.class,true,true,null ),
        
        // See RV_Storage
        //Modificado por Conserti, sentencia de oracle
        new ColumnInfo( Msg.translate( Env.getCtx(),"GoodForDays" ),"cast((TRUNC(asi.GuaranteeDate)-TRUNC(SysDate))-cast(cast(p.GuaranteeDaysMin as text)|| 'days' as interval)as text)",String.class,true,true,null ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"ShelfLifeDays" ),"cast(TRUNC(asi.GuaranteeDate)-TRUNC(SysDate)as text)",String.class ),
        new ColumnInfo( Msg.translate( Env.getCtx(),"ShelfLifeRemainingPct" ),"CASE WHEN p.GuaranteeDays > 0 THEN cast(TRUNC(((TRUNC(asi.GuaranteeDate)-TRUNC(SysDate))/p.GuaranteeDays)*100)as text) ELSE cast(0 as text) END",String.class ),
    };

    /** Descripción de Campos */

    private String s_sqlFrom = " M_AttributeSetInstance asi INNER JOIN M_Product p ON (p.M_Product_ID = ?) LEFT OUTER JOIN M_Storage s ON (s.M_AttributeSetInstance_ID=asi.M_AttributeSetInstance_ID AND s.M_Product_ID=p.M_Product_ID) LEFT OUTER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID AND l.M_Warehouse_ID=?) ";

    /** Descripción de Campos */

    private String s_sqlWhere = " ( asi.M_AttributeSet_id = p.M_AttributeSet_id ) ";

    /** Descripción de Campos */

    private String m_sqlNonZero = " AND (s.QtyOnHand<>0 OR s.QtyReserved<>0 OR s.QtyOrdered<>0)";

    /** Descripción de Campos */

    private String m_sqlMinLife = "";

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    private void dynInit( int C_BPartner_ID ) {
        log.config( "PAttributeInstance.dynInit - C_BPartner_ID=" + C_BPartner_ID );

        if( C_BPartner_ID != 0 ) {
            int    ShelfLifeMinPct  = 0;
            int    ShelfLifeMinDays = 0;
            String sql              = "SELECT bp.ShelfLifeMinPct, bpp.ShelfLifeMinPct, bpp.ShelfLifeMinDays " + "FROM C_BPartner bp " + " LEFT OUTER JOIN C_BPartner_Product bpp" + " ON (bp.C_BPartner_ID=bpp.C_BPartner_ID AND bpp.M_Product_ID=?) " + "WHERE bp.C_BPartner_ID=?";
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql );
                pstmt.setInt( 1,m_M_Product_ID );
                pstmt.setInt( 2,C_BPartner_ID );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    ShelfLifeMinPct = rs.getInt( 1 );    // BP

                    int pct = rs.getInt( 2 );            // BP_P

                    if( pct > 0 ) {                      // overwrite
                        ShelfLifeMinDays = pct;
                    }

                    ShelfLifeMinDays = rs.getInt( 3 );
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"PAttributeInstance.dynInit",e );
            }

            try {
                if( pstmt != null ) {
                    pstmt.close();
                }

                pstmt = null;
            } catch( Exception e ) {
                pstmt = null;
            }

            if( ShelfLifeMinPct > 0 ) {
                m_sqlMinLife = " AND COALESCE(TRUNC(((TRUNC(asi.GuaranteeDate)-TRUNC(SysDate))/p.GuaranteeDays)*100),0)>=" + ShelfLifeMinPct;
                log.config( "PAttributeInstance.dynInit - ShelfLifeMinPct=" + ShelfLifeMinPct );
            }

            if( ShelfLifeMinDays > 0 ) {
                m_sqlMinLife += " AND COALESCE((TRUNC(asi.GuaranteeDate)-TRUNC(SysDate)),0)>=" + ShelfLifeMinDays;
                log.config( "PAttributeInstance.dynInit - ShelfLifeMinDays=" + ShelfLifeMinDays );
            }
        }    // BPartner != 0

        m_sql = m_table.prepareTable( s_layout,s_sqlFrom,s_sqlWhere,false,"asi",true ) + " ORDER BY asi.GuaranteeDate, s.QtyOnHand";    // oldest, smallest first

        //

        m_table.setRowSelectionAllowed( true );
        m_table.setMultiSelection( false );
        m_table.addMouseListener( this );
        m_table.getSelectionModel().addListSelectionListener( this );

        //

        refresh();
    }    // dynInit

    /**
     * Descripción de Método
     *
     */

    private void refresh() {
        String sql = m_sql;
        int    pos = m_sql.lastIndexOf( " ORDER BY " );

        if( !showAll.isSelected()) {
            sql = m_sql.substring( 0,pos ) + m_sqlNonZero;

            if( m_sqlMinLife.length() > 0 ) {
                sql += m_sqlMinLife;
            }

            sql += m_sql.substring( pos );
        }

        //

        log.finest( sql );

        PreparedStatement pstmt = null;
        
        try {
            pstmt = DB.prepareStatement( sql );

            int pn = 1;
            
            pstmt.setInt( pn++, m_M_PriceList_Version_ID);
            pstmt.setInt( pn++, m_M_Product_ID );
            pstmt.setInt( pn++, m_M_Warehouse_ID );

            ResultSet rs = pstmt.executeQuery();

            m_table.loadTable( rs );
            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"refresh - " + sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        enableButtons();
    }    // refresh

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();
            m_M_AttributeSetInstance_ID  = -1;
            m_M_AttributeSetInstanceName = null;
        } else if( e.getSource() == showAll ) {
            refresh();
        }
    }    // actionPerformed

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
    }    // valueChanged

    /**
     * Descripción de Método
     *
     */

    private void enableButtons() {
        m_M_AttributeSetInstance_ID  = -1;
        m_M_AttributeSetInstanceName = null;

        int     row     = m_table.getSelectedRow();
        boolean enabled = row != -1;

        if( enabled ) {
            Integer ID = m_table.getSelectedRowKey();

            if( ID != null ) {
                m_M_AttributeSetInstance_ID  = ID.intValue();
                m_M_AttributeSetInstanceName = ( String )m_table.getValueAt( row,1 );
            }
        }

        confirmPanel.getOKButton().setEnabled( enabled );
        log.fine( "M_AttributeSetInstance_ID=" + m_M_AttributeSetInstance_ID + " - " + m_M_AttributeSetInstanceName );
    }    // enableButtons

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {

        // Double click with selected row => exit

        if( (e.getClickCount() > 1) && (m_table.getSelectedRow() != -1) ) {
            enableButtons();
            dispose();
        }
    }    // mouseClicked

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_AttributeSetInstance_ID() {
        return m_M_AttributeSetInstance_ID;
    }    // getM_AttributeSetInstance_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getM_AttributeSetInstanceName() {
        return m_M_AttributeSetInstanceName;
    }    // getM_AttributeSetInstanceName
}    // PAttributeInstance



/*
 *  @(#)PAttributeInstance.java   02.07.07
 * 
 *  Fin del fichero PAttributeInstance.java
 *  
 *  Versión 2.2
 *
 */
