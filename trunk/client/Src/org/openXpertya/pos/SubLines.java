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



package org.openXpertya.pos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.PO;
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

public class SubLines extends PosSubPanel implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubLines( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubAllLines

    /** Descripción de Campos */

    private MiniTable m_table;

    /** Descripción de Campos */

    private String m_sql;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubLines.class );

    /** Descripción de Campos */

    private CButton f_up;

    /** Descripción de Campos */

    private CButton f_delete;

    /** Descripción de Campos */

    private CButton f_down;

    //

    /** Descripción de Campos */

    private VNumber f_net;

    /** Descripción de Campos */

    private VNumber f_tax;

    /** Descripción de Campos */

    private VNumber f_total;

    /** Descripción de Campos */

    private static ColumnInfo[] s_layout = new ColumnInfo[] {
        new ColumnInfo( " ","C_OrderLine_ID",IDColumn.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Line" ),"Line",Integer.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Qty" ),"QtyOrdered",Double.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"C_UOM_ID" ),"UOMSymbol",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Name" ),"Name",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"PriceActual" ),"PriceActual",BigDecimal.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"LineNetAmt" ),"LineNetAmt",BigDecimal.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"C_Tax_ID" ),"TaxIndicator",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Description" ),"Description",String.class )
    };

    /** Descripción de Campos */

    private static String s_sqlFrom = "C_Order_LineTax_v";

    /** Descripción de Campos */

    private static String s_sqlWhere = "C_Order_ID=? AND LineNetAmt<>0";

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( Msg.translate( Env.getCtx(),"C_OrderLine_ID" ));

        setBorder( border );

        // Content

        setLayout( new BorderLayout( 5,5 ));
        m_table = new MiniTable();

        CScrollPane scroll = new CScrollPane( m_table );

        m_sql = m_table.prepareTable( s_layout,s_sqlFrom,s_sqlWhere,false,"C_Order_LineTax_v" ) + " ORDER BY Line";
        m_table.setRowSelectionAllowed( true );
        m_table.setColumnSelectionAllowed( false );
        m_table.setMultiSelection( false );

        // m_table.addMouseListener(this);
        // m_table.getSelectionModel().addListSelectionListener(this);

        scroll.setPreferredSize( new Dimension( 100,100 ));
        add( scroll,BorderLayout.CENTER );

        // Right side

        CPanel right = new CPanel();

        add( right,BorderLayout.EAST );
        right.setLayout( new BoxLayout( right,BoxLayout.Y_AXIS ));

        //

        right.add( Box.createGlue());
        f_up = createButtonAction( "Previous",KeyStroke.getKeyStroke( KeyEvent.VK_UP,0 ));
        right.add( f_up );
        right.add( Box.createGlue());
        f_delete = createButtonAction( "Delete",KeyStroke.getKeyStroke( KeyEvent.VK_DELETE,Event.SHIFT_MASK ));
        right.add( f_delete );
        right.add( Box.createGlue());
        f_down = createButtonAction( "Next",KeyStroke.getKeyStroke( KeyEvent.VK_DOWN,0 ));
        right.add( f_down );
        right.add( Box.createGlue());

        // Summary

        FlowLayout summaryLayout = new FlowLayout( FlowLayout.LEADING,2,0 );
        CPanel     summary       = new CPanel( summaryLayout );

        add( summary,BorderLayout.SOUTH );

        //

        CLabel lNet = new CLabel( Msg.translate( Env.getCtx(),"TotalLines" ));

        summary.add( lNet );
        f_net = new VNumber( "TotalLines",false,true,false,DisplayType.Amount,"TotalLines" );
        f_net.setColumns( 6,22 );
        lNet.setLabelFor( f_net );
        summary.add( f_net );
        f_net.setValue( Env.ZERO );

        //

        CLabel lTax = new CLabel( Msg.translate( Env.getCtx(),"TaxAmt" ));

        summary.add( lTax );
        f_tax = new VNumber( "TaxAmt",false,true,false,DisplayType.Amount,"TaxAmt" );
        f_tax.setColumns( 6,22 );
        lTax.setLabelFor( f_tax );
        summary.add( f_tax );
        f_tax.setValue( Env.ZERO );

        //

        CLabel lTotal = new CLabel( Msg.translate( Env.getCtx(),"GrandTotal" ));

        summary.add( lTotal );
        f_total = new VNumber( "GrandTotal",false,true,false,DisplayType.Amount,"GrandTotal" );
        f_total.setColumns( 6,22 );
        lTotal.setLabelFor( f_total );
        summary.add( f_total );
        f_total.setValue( Env.ZERO );

        //

        f_delete.setReadWrite( false );
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx   = 0;
        gbc.gridy   = 2;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;

        return gbc;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        String action = e.getActionCommand();

        if( (action == null) || (action.length() == 0) ) {
            return;
        }

        log.info( "PosSubAllLines - actionPerformed: " + action );

        if( "Previous".equalsIgnoreCase( e.getActionCommand())) {
            int rows = m_table.getRowCount();

            if( rows == 0 ) {
                return;
            }

            int row = m_table.getSelectedRow();

            row--;

            if( row < 0 ) {
                row = 0;
            }

            m_table.getSelectionModel().setSelectionInterval( row,row );
            f_delete.setReadWrite( true );
        } else if( "Next".equalsIgnoreCase( e.getActionCommand())) {
            int rows = m_table.getRowCount();

            if( rows == 0 ) {
                return;
            }

            int row = m_table.getSelectedRow();

            row++;

            if( row >= rows ) {
                row = rows - 1;
            }

            m_table.getSelectionModel().setSelectionInterval( row,row );
            f_delete.setReadWrite( true );
        }

        // Delete

        else if( action.equals( "Delete" )) {}
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    public void updateTable( MOrder order ) {
        int C_Order_ID = 0;

        if( order != null ) {
            C_Order_ID = order.getC_Order_ID();
        }

        if( C_Order_ID == 0 ) {
            m_table.loadTable( new PO[ 0 ] );
            setSums( order );
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( m_sql );
            pstmt.setInt( 1,C_Order_ID );

            ResultSet rs = pstmt.executeQuery();

            m_table.loadTable( rs );
            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"updateTable - " + m_sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        setSums( order );
    }    // updateTable

    /**
     * Descripción de Método
     *
     *
     * @param order
     */

    private void setSums( MOrder order ) {
        int noLines = m_table.getRowCount();

        p_posPanel.f_status.setStatusDB( noLines );

        if( order == null ) {
            f_net.setValue( Env.ZERO );
            f_total.setValue( Env.ZERO );
            f_tax.setValue( Env.ZERO );
        } else {
            order.prepareIt();
            f_net.setValue( order.getTotalLines());
            f_total.setValue( order.getGrandTotal());
            f_tax.setValue( order.getGrandTotal().subtract( order.getTotalLines()));
        }
    }    // setSums
}    // PosSubAllLines



/*
 *  @(#)SubLines.java   02.07.07
 * 
 *  Fin del fichero SubLines.java
 *  
 *  Versión 2.2
 *
 */
