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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MRole;
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

public class InvoiceHistory extends JDialog implements ActionListener,ChangeListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param C_BPartner_ID
     * @param M_Product_ID
     */

    public InvoiceHistory( Dialog frame,int C_BPartner_ID,int M_Product_ID ) {
        super( frame,Msg.getMsg( Env.getCtx(),"PriceHistory" ),true );
        CompiereColor.setBackground( this );
        log.config( "C_BPartner_ID=" + C_BPartner_ID + ", M_Product_ID=" + M_Product_ID );
        m_C_BPartner_ID = C_BPartner_ID;
        m_M_Product_ID  = M_Product_ID;

        try {
            jbInit();
            dynInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"InvoiceHistory",ex );
        }

        mainPanel.setPreferredSize( new Dimension( 600,400 ));
        AEnv.positionCenterWindow( frame,this );
    }    // InvoiceHistory

    /** Descripción de Campos */

    private int m_C_BPartner_ID;

    /** Descripción de Campos */

    private int m_M_Product_ID;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( InvoiceHistory.class );

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private JLabel label = new JLabel();

    /** Descripción de Campos */

    private FlowLayout northLayout = new FlowLayout();

    //

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel();

    /** Descripción de Campos */

    private JTabbedPane centerTabbedPane = new JTabbedPane();

    //

    /** Descripción de Campos */

    private JScrollPane pricePane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable m_tablePrice = new MiniTable();

    /** Descripción de Campos */

    private DefaultTableModel m_modelPrice = null;

    /** Descripción de Campos */

    private JScrollPane reservedPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable m_tableReserved = new MiniTable();

    /** Descripción de Campos */

    private DefaultTableModel m_modelReserved = null;

    /** Descripción de Campos */

    private JScrollPane orderedPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable m_tableOrdered = new MiniTable();

    /** Descripción de Campos */

    private DefaultTableModel m_modelOrdered = null;

    /** Descripción de Campos */

    private JScrollPane unconfirmedPane = new JScrollPane();

    /** Descripción de Campos */

    private MiniTable m_tableUnconfirmed = new MiniTable();

    /** Descripción de Campos */

    private DefaultTableModel m_modelUnconfirmed = null;

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
    	MRole role = MRole.get(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx()));
        this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        mainPanel.setLayout( mainLayout );
        label.setText( "Label" );
        northPanel.setLayout( northLayout );
        northLayout.setAlignment( FlowLayout.LEFT );
        getContentPane().add( mainPanel );
        mainPanel.add( northPanel,BorderLayout.NORTH );
        northPanel.add( label,null );
        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        mainPanel.add( centerTabbedPane,BorderLayout.CENTER );
        centerTabbedPane.addChangeListener( this );
        centerTabbedPane.add( pricePane,Msg.getMsg( Env.getCtx(),"PriceHistory" ));
        if(role != null && role.isAllow_Info_Product_Reserved_Tab()){
        	centerTabbedPane.add( reservedPane,Msg.translate( Env.getCtx(),"QtyReserved" ));
        }
        if(role != null && role.isAllow_Info_Product_Ordered_Tab()){
        	centerTabbedPane.add( orderedPane,Msg.translate( Env.getCtx(),"QtyOrdered" ));
        }
        centerTabbedPane.add( unconfirmedPane,Msg.getMsg( Env.getCtx(),"UnconfirmedQty" ));

        //

        pricePane.getViewport().add( m_tablePrice,null );
        reservedPane.getViewport().add( m_tableReserved,null );
        orderedPane.getViewport().add( m_tableOrdered,null );
        unconfirmedPane.getViewport().add( m_tableUnconfirmed,null );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean dynInit() {

        // Header

        Vector columnNames = new Vector();

        columnNames.add( Msg.translate( Env.getCtx(),(m_C_BPartner_ID == 0)
                ?"C_BPartner_ID"
                :"M_Product_ID" ));
        columnNames.add( Msg.translate( Env.getCtx(),"PriceActual" ));
        columnNames.add( Msg.translate( Env.getCtx(),"QtyInvoiced" ));
       // columnNames.add( Msg.translate( Env.getCtx(),"Discount" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DateInvoiced" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DocumentNo" ));
        
        columnNames.add( Msg.translate( Env.getCtx(),"AD_Org_ID" ));

        // Fill Data

        Vector data = null;

        if( m_C_BPartner_ID == 0 ) {
            data = queryBPartner();    // BPartner of Product
        } else {
            data = queryProduct();     // Product of BPartner
        }

        // Table

        m_modelPrice = new DefaultTableModel( data,columnNames );
        m_tablePrice.setModel( m_modelPrice );

        //

      /*  m_tablePrice.setColumnClass( 0,String.class,true );        // Product/Partner
        m_tablePrice.setColumnClass( 1,Double.class,true );        // Price
        m_tablePrice.setColumnClass( 2,Double.class,true );        // Quantity
        m_tablePrice.setColumnClass( 3,BigDecimal.class,true );    // Discount (%) to limit precision
        m_tablePrice.setColumnClass( 4,String.class,true );       // DocNo
        m_tablePrice.setColumnClass( 5,Timestamp.class,true );    // Date
        m_tablePrice.setColumnClass( 6,String.class,true );       // Org
*/
        //
        m_tablePrice.setColumnClass( 0,String.class,true );        // Product/Partner
        m_tablePrice.setColumnClass( 1,Double.class,true );        // Price
        m_tablePrice.setColumnClass( 2,Double.class,true );        // Quantity
       // m_tablePrice.setColumnClass( 3,BigDecimal.class,true );    // Discount (%) to limit precision
        m_tablePrice.setColumnClass( 3,Timestamp.class,true );	//Date
        m_tablePrice.setColumnClass( 4,String.class,true );       // DocNo
        m_tablePrice.setColumnClass( 5,String.class,true );       // Org
        m_tablePrice.autoSize();

        //

        return data.size() != 0;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private Vector queryProduct() {
        String sql = "SELECT p.Name,l.PriceActual,l.PriceList,l.QtyInvoiced," 
        		+ "i.DateInvoiced,dt.PrintName || ' ' || i.DocumentNo As DocumentNo," 
        		+ "o.Name, i.DateOrdered " 
        		+ "FROM C_Invoice i" 
        		+ " INNER JOIN C_InvoiceLine l ON (i.C_Invoice_ID=l.C_Invoice_ID)" 
        		+ " INNER JOIN C_DocType dt ON (i.C_DocType_ID=dt.C_DocType_ID)" 
        		+ " INNER JOIN AD_Org o ON (i.AD_Org_ID=o.AD_Org_ID)" 
        		+ " INNER JOIN M_Product p  ON (l.M_Product_ID=p.M_Product_ID) " 
        		+ "WHERE i.C_BPartner_ID=? " 
        		+ "AND i.docStatus IN ('CO', 'CL') "
        		+ "ORDER BY i.DateInvoiced DESC";
        Vector data = fillTable( sql,m_C_BPartner_ID );

        sql = "SELECT Name from C_BPartner WHERE C_BPartner_ID=?";
        fillLabel( sql,m_C_BPartner_ID );

        return data;
    }    // queryProduct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private Vector queryBPartner() {
        String sql = "SELECT bp.Name,l.PriceActual,l.PriceList,l.QtyInvoiced,"    // 1,2,3,4
                     + "i.DateInvoiced,dt.PrintName || ' ' || i.DocumentNo As DocumentNo,"    // 5,6
                     + "o.Name,i.DateOrdered " 
                     + "FROM C_Invoice i" 
                     + " INNER JOIN C_InvoiceLine l ON (i.C_Invoice_ID=l.C_Invoice_ID)" 
                     + " INNER JOIN C_DocType dt ON (i.C_DocType_ID=dt.C_DocType_ID)" 
                     + " INNER JOIN AD_Org o ON (i.AD_Org_ID=o.AD_Org_ID)" 
                     + " INNER JOIN C_BPartner bp ON (i.C_BPartner_ID=bp.C_BPartner_ID) " 
                     + "WHERE l.M_Product_ID=? " 
                     + "AND i.docStatus IN ('CO', 'CL') "     
                     + "ORDER BY i.DateInvoiced DESC";
        Vector data = fillTable( sql,m_M_Product_ID );

        sql = "SELECT Name from M_Product WHERE M_Product_ID=?";
        fillLabel( sql,m_M_Product_ID );

        return data;
    }    // qyeryBPartner

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param parameter
     *
     * @return
     */

    private Vector fillTable( String sql,int parameter ) {
        log.fine( sql + "; Parameter=" + parameter );

        Vector data = new Vector();

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,parameter );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                Vector line = new Vector( 6 );
            	
                // 0-Name, 1-PriceActual, 2-QtyInvoiced, 3-Discount, 4-DocumentNo, 5-DateInvoiced

                line.add( rs.getString( 1 ));                 // Name
                line.add( rs.getBigDecimal( 2 ));             // Price
                line.add( new Double( rs.getDouble( 4 )));    // Qty

                BigDecimal discountBD = Env.ZERO;

                try                                           // discoint can be indefinate
                {
                    double discountD = ( rs.getDouble( 3 ) - rs.getDouble( 2 )) / rs.getDouble( 3 ) * 100;

                    discountBD = new BigDecimal( discountD );
                } catch( Exception e ) {
                    discountBD = Env.ZERO;
                }
                line.add(rs.getTimestamp(8));		//DatePromised
               // line.add( discountBD );             // Discount
                line.add( rs.getString( 6 ));       // DocNo
                line.add( rs.getTimestamp( 5 ));    // Date
                line.add( rs.getString( 7 ));       // Org/Warehouse
                data.add( line );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        log.fine( "#" + data.size());

        return data;
    }    // fillTable

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param parameter
     */

    private void fillLabel( String sql,int parameter ) {
        log.fine( sql + "; Parameter=" + parameter );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,parameter );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                label.setText( rs.getString( 1 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }
    }    // fillLabel

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            dispose();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void stateChanged( ChangeEvent e ) {
        if( centerTabbedPane.getSelectedIndex() == 1 ) {
            initReservedOrderedTab( true );
        } else if( centerTabbedPane.getSelectedIndex() == 2 ) {
            initReservedOrderedTab( false );
        } else if( centerTabbedPane.getSelectedIndex() == 3 ) {
            initUnconfirmedTab();
        }
    }    // stateChanged

    /**
     * Descripción de Método
     *
     *
     * @param reserved
     */

    private void initReservedOrderedTab( boolean reserved ) {

        // Done already

        if( reserved && (m_modelReserved != null) ) {
            return;
        }

        if( !reserved && (m_modelOrdered != null) ) {
            return;
        }

        // Header

        Vector columnNames = new Vector();

        columnNames.add( Msg.translate( Env.getCtx(),(m_C_BPartner_ID == 0)
                ?"C_BPartner_ID"
                :"M_Product_ID" ));
        columnNames.add( Msg.translate( Env.getCtx(),"PriceActual" ));
        columnNames.add( Msg.translate( Env.getCtx(),reserved
                ?"QtyReserved"
                :"QtyOrdered" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DatePromised" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DocumentNo" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DateOrdered" ));
        columnNames.add( Msg.translate( Env.getCtx(),"M_Warehouse_ID" ));

        // Fill Data

        Vector data = null;

        if( m_C_BPartner_ID == 0 ) {
            String sql = "SELECT bp.Name, ol.PriceActual,ol.PriceList,ol.QtyReserved," 
            			+ "o.DateOrdered,dt.PrintName || ' ' || o.DocumentNo As DocumentNo, " 
            			+ "w.Name, o.datepromised " 
            			+ "FROM C_Order o" 
            			+ " INNER JOIN C_OrderLine ol ON (o.C_Order_ID=ol.C_Order_ID)" 
            			+ " INNER JOIN C_DocType dt ON (o.C_DocType_ID=dt.C_DocType_ID)" 
            			+ " INNER JOIN M_Warehouse w ON (ol.M_Warehouse_ID=w.M_Warehouse_ID)" 
            			+ " INNER JOIN C_BPartner bp  ON (o.C_BPartner_ID=bp.C_BPartner_ID) " 
            			+ "WHERE ol.QtyReserved<>0" 
            			+ " AND ol.M_Product_ID=?" 
            			+ " AND o.IsSOTrx=" + ( reserved ? "'Y'" : "'N'" )
            			+ " AND o.docStatus IN ('CO', 'CL') "
            			+ " ORDER BY o.DateOrdered";
            data = fillTable( sql,m_M_Product_ID );    // Product By BPartner
        } else {
            String sql = "SELECT p.Name, ol.PriceActual,ol.PriceList,ol.QtyReserved," 
            			+ "o.DateOrdered,dt.PrintName || ' ' || o.DocumentNo As DocumentNo, " 
            			+ "w.Name, o.datepromised " 
            			+ "FROM C_Order o" 
            			+ " INNER JOIN C_OrderLine ol ON (o.C_Order_ID=ol.C_Order_ID)" 
            			+ " INNER JOIN C_DocType dt ON (o.C_DocType_ID=dt.C_DocType_ID)" 
            			+ " INNER JOIN M_Warehouse w ON (ol.M_Warehouse_ID=w.M_Warehouse_ID)" 
            			+ " INNER JOIN M_Product p  ON (ol.M_Product_ID=p.M_Product_ID) " 
            			+ "WHERE ol.QtyReserved<>0" 
            			+ " AND o.C_BPartner_ID=?" 
            			+ " AND o.IsSOTrx=" + ( reserved ? "'Y'" : "'N'" )
            			+ " AND o.docStatus IN ('CO', 'CL') "
            			+ " ORDER BY o.DateOrdered";
            data = fillTable( sql,m_C_BPartner_ID );    // Product of BP
        }

        // Table

        MiniTable table = null;

        if( reserved ) {
            m_modelReserved = new DefaultTableModel( data,columnNames );
            m_tableReserved.setModel( m_modelReserved );
            table = m_tableReserved;
        } else {
            m_modelOrdered = new DefaultTableModel( data,columnNames );
            m_tableOrdered.setModel( m_modelOrdered );
            table = m_tableOrdered;
        }

        //

        table.setColumnClass( 0,String.class,true );        // Product/Partner
        table.setColumnClass( 1,BigDecimal.class,true );    // Price
        table.setColumnClass( 2,Double.class,true );        // Quantity
       // table.setColumnClass( 3,BigDecimal.class,true );    // Discount (%)
        table.setColumnClass( 4,String.class,true );        // DocNo
        table.setColumnClass( 5,Timestamp.class,true );     // Date
        table.setColumnClass( 6,String.class,true );        // Warehouse
        table.setColumnClass( 3, Timestamp.class, true);	//DatePromised
        //

        table.autoSize();
    }    // initReservedOrderedTab

    /**
     * Descripción de Método
     *
     */

    private void initUnconfirmedTab() {

        // Done already

        if( m_modelUnconfirmed != null ) {
            return;
        }

        // Header

        Vector columnNames = new Vector();

        columnNames.add( Msg.translate( Env.getCtx(),(m_C_BPartner_ID == 0)
                ?"C_BPartner_ID"
                :"M_Product_ID" ));
        columnNames.add( Msg.translate( Env.getCtx(),"MovementQty" ));
        columnNames.add( Msg.translate( Env.getCtx(),"MovementDate" ));
        columnNames.add( Msg.translate( Env.getCtx(),"IsSOTrx" ));
        columnNames.add( Msg.translate( Env.getCtx(),"DocumentNo" ));
        columnNames.add( Msg.translate( Env.getCtx(),"M_Warehouse_ID" ));

        // Fill Data

        String sql       = null;
        int    parameter = 0;

        if( m_C_BPartner_ID == 0 ) {
            sql = "SELECT bp.Name," 
            		+ " CASE WHEN io.IsSOTrx='Y' THEN iol.MovementQty*-1 ELSE iol.MovementQty END AS MovementQty," 
            		+ " io.MovementDate,io.IsSOTrx," 
            		+ " dt.PrintName || ' ' || io.DocumentNo As DocumentNo," 
            		+ " w.Name " 
            		+ "FROM M_InOutLine iol" 
            		+ " INNER JOIN M_InOut io ON (iol.M_InOut_ID=io.M_InOut_ID)" 
            		+ " INNER JOIN C_BPartner bp  ON (io.C_BPartner_ID=bp.C_BPartner_ID)" 
            		+ " INNER JOIN C_DocType dt ON (io.C_DocType_ID=dt.C_DocType_ID)" 
            		+ " INNER JOIN M_Warehouse w ON (io.M_Warehouse_ID=w.M_Warehouse_ID)" 
            		+ " INNER JOIN M_InOutLineConfirm lc ON (iol.M_InOutLine_ID=lc.M_InOutLine_ID) " 
            		+ "WHERE iol.M_Product_ID=?" 
            		+ " AND lc.Processed='N' " 
            		+ "ORDER BY io.MovementDate,io.IsSOTrx";
            parameter = m_M_Product_ID;
        } else {
            sql = "SELECT p.Name," 
            		+ " CASE WHEN io.IsSOTrx='Y' THEN iol.MovementQty*-1 ELSE iol.MovementQty END AS MovementQty," 
            		+ " io.MovementDate,io.IsSOTrx," 
            		+ " dt.PrintName || ' ' || io.DocumentNo As DocumentNo," 
            		+ " w.Name " 
            		+ "FROM M_InOutLine iol" 
            		+ " INNER JOIN M_InOut io ON (iol.M_InOut_ID=io.M_InOut_ID)" 
            		+ " INNER JOIN M_Product p  ON (iol.M_Product_ID=p.M_Product_ID)" 
            		+ " INNER JOIN C_DocType dt ON (io.C_DocType_ID=dt.C_DocType_ID)" 
            		+ " INNER JOIN M_Warehouse w ON (io.M_Warehouse_ID=w.M_Warehouse_ID)" 
            		+ " INNER JOIN M_InOutLineConfirm lc ON (iol.M_InOutLine_ID=lc.M_InOutLine_ID) " 
            		+ "WHERE io.C_BPartner_ID=?" 
            		+ " AND lc.Processed='N' " 
            		+ "ORDER BY io.MovementDate,io.IsSOTrx";
            parameter = m_C_BPartner_ID;
        }

        Vector data = new Vector();

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,parameter );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                Vector line = new Vector( 6 );

                // 1-Name, 2-MovementQty, 3-MovementDate, 4-IsSOTrx, 5-DocumentNo

                line.add( rs.getString( 1 ));                               // Name
                line.add( new Double( rs.getDouble( 2 )));                  // Qty
                line.add( rs.getTimestamp( 3 ));                            // Date
                line.add( new Boolean( "Y".equals( rs.getString( 4 ))));    // IsSOTrx
                line.add( rs.getString( 5 ));    // DocNo
                line.add( rs.getString( 6 ));    // Warehouse
                data.add( line );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        log.fine( "#" + data.size());

        // Table

        m_modelUnconfirmed = new DefaultTableModel( data,columnNames );
        m_tableUnconfirmed.setModel( m_modelUnconfirmed );

        MiniTable table = m_tableUnconfirmed;

        //

        table.setColumnClass( 0,String.class,true );       // Product/Partner
        table.setColumnClass( 1,Double.class,true );       // MovementQty
        table.setColumnClass( 2,Timestamp.class,true );    // MovementDate
        table.setColumnClass( 3,Boolean.class,true );      // IsSOTrx
        table.setColumnClass( 4,String.class,true );       // DocNo

        //

        table.autoSize();
    }    // initUnconfirmedTab
}    // InvoiceHistory



/*
 *  @(#)InvoiceHistory.java   02.07.07
 * 
 *  Fin del fichero InvoiceHistory.java
 *  
 *  Versión 2.2
 *
 */
