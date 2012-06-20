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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class QueryTicket extends PosSubPanel implements ActionListener,MouseListener,ListSelectionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public QueryTicket( PosPanel posPanel ) {
        super( posPanel );
    }    // PosQueryProduct

    /** Descripción de Campos */

    private MiniTable m_table;

    /** Descripción de Campos */

    private CPanel northPanel;

    /** Descripción de Campos */

    private CScrollPane centerScroll;

    /** Descripción de Campos */

    private ConfirmPanel confirm;

    /** Descripción de Campos */

    private CTextField f_c_order_id;

    /** Descripción de Campos */

    private CTextField f_documentno;

    /** Descripción de Campos */

    private VDate f_date;

    /** Descripción de Campos */

    private CButton f_up;

    /** Descripción de Campos */

    private CButton f_down;

    /** Descripción de Campos */

    private int m_c_order_id;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( QueryTicket.class );

    /** Descripción de Campos */

    private static ColumnInfo[] s_layout = new ColumnInfo[]{ new ColumnInfo( " ","C_Order_ID",IDColumn.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"DocumentNo" ),"DocumentNo",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"TotalLines" ),"TotalLines",BigDecimal.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"GrandTotal" ),"GrandTotal",BigDecimal.class )};

    /**
     * Descripción de Método
     *
     */

    protected void init() {
        setLayout( new BorderLayout( 5,5 ));
        setVisible( false );

        // North

        northPanel = new CPanel( new GridBagLayout());
        add( northPanel,BorderLayout.NORTH );
        northPanel.setBorder( new TitledBorder( Msg.getMsg( p_ctx,"Query" )));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = PosSubPanel.INSETS2;

        //

        gbc.gridy = 0;
        gbc.gridx = GridBagConstraints.RELATIVE;

        CLabel lorder_id = new CLabel( Msg.translate( p_ctx,"C_Order_ID" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( lorder_id,gbc );
        f_c_order_id = new CTextField( 20 );
        lorder_id.setLabelFor( f_c_order_id );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_c_order_id,gbc );
        f_c_order_id.addActionListener( this );

        //

        CLabel ldoc = new CLabel( Msg.translate( p_ctx,"DocumentNo" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( ldoc,gbc );
        f_documentno = new CTextField( 15 );
        ldoc.setLabelFor( f_documentno );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_documentno,gbc );
        f_documentno.addActionListener( this );

        //

        gbc.gridy = 1;

        CLabel ldate = new CLabel( Msg.translate( p_ctx,"DateOrdered" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( ldate,gbc );
        f_date = new VDate();
        f_date.setValue( Env.getContextAsDate( Env.getCtx(),"#Date" ));
        ldate.setLabelFor( f_date );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_date,gbc );
        f_date.addActionListener( this );

        //

        gbc.gridy      = 0;
        gbc.gridheight = 2;
        gbc.anchor     = GridBagConstraints.EAST;
        gbc.weightx    = .1;
        f_up           = createButtonAction( "Previous",KeyStroke.getKeyStroke( KeyEvent.VK_UP,0 ));
        northPanel.add( f_up,gbc );
        gbc.weightx = 0;
        f_down      = createButtonAction( "Next",KeyStroke.getKeyStroke( KeyEvent.VK_DOWN,0 ));
        northPanel.add( f_down,gbc );

        // Confirm

        confirm = new ConfirmPanel( true,true,true,false,false,false,false );
        add( confirm,BorderLayout.SOUTH );
        confirm.addActionListener( this );

        // Center

        m_table = new MiniTable();

        String sql = m_table.prepareTable( s_layout,"C_Order","C_DocTypeTarget_ID" + p_pos.getC_OrderDocType_ID(),false,"C_Order" ) + " ORDER BY Margin, QtyAvailable";

        m_table.setRowSelectionAllowed( true );
        m_table.setColumnSelectionAllowed( false );
        m_table.setMultiSelection( false );
        m_table.addMouseListener( this );
        m_table.getSelectionModel().addListSelectionListener( this );
        enableButtons();
        centerScroll = new CScrollPane( m_table );
        add( centerScroll,BorderLayout.CENTER );
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx     = 0;
        gbc.gridy     = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 2;    // GridBagConstraints.REMAINDER;
        gbc.fill      = GridBagConstraints.BOTH;
        gbc.weightx   = 0.1;
        gbc.weighty   = 0.5;

        return gbc;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        removeAll();
        northPanel   = null;
        centerScroll = null;
        confirm      = null;
        m_table      = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param aFlag
     */

    public void setVisible( boolean aFlag ) {
        super.setVisible( aFlag );

        if( aFlag ) {
            f_c_order_id.requestFocus();
        }
    }    // setVisible

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( "PosQueryProduct.actionPerformed - " + e.getActionCommand());

        if( "Refresh".equals( e.getActionCommand()) || (e.getSource() == f_c_order_id) || (e.getSource() == f_documentno) || (e.getSource() == f_date) ) {
            setResults( p_ctx,f_c_order_id.getText(),f_documentno.getText(),f_date.getTimestamp());

            return;
        } else if( "Reset".equals( e.getActionCommand())) {
            reset();

            return;
        } else if( "Previous".equalsIgnoreCase( e.getActionCommand())) {
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

            return;
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

            return;
        }

        // Exit

        close();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    public void reset() {
        f_c_order_id.setText( null );
        f_documentno.setText( null );
        f_date.setValue( Env.getContextAsDate( Env.getCtx(),"#Date" ));
        setResults( p_ctx,f_c_order_id.getText(),f_documentno.getText(),f_date.getTimestamp());
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param id
     * @param doc
     * @param date
     */

    public void setResults( Properties ctx,String id,String doc,Timestamp date ) {
        String sql = "";

        try {
            sql = "SELECT o.C_Order_ID, o.DocumentNo, o.TotalLines, o.GrandTotal FROM C_Order o WHERE o.C_DocTypeTarget_ID = " + p_pos.getC_OrderDocType_ID();

            if( (id != null) &&!id.equalsIgnoreCase( "" )) {
                sql += " AND o.C_Order_ID = " + id;
            }

            if( (doc != null) &&!doc.equalsIgnoreCase( "" )) {
                sql += " AND o.DocumentNo = '" + doc + "'";
            }

            sql += " AND o.DateOrdered = ?";

            PreparedStatement pstm = DB.prepareStatement( sql );

            pstm.setTimestamp( 1,date );

            ResultSet rs = pstm.executeQuery();

            m_table.loadTable( rs );
            enableButtons();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"QueryTicket.setResults: " + e + " -> " + sql );
        }
    }    // setResults

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
        m_c_order_id = -1;

        int     row     = m_table.getSelectedRow();
        boolean enabled = row != -1;

        if( enabled ) {
            Integer ID = m_table.getSelectedRowKey();

            if( ID != null ) {
                m_c_order_id = ID.intValue();
            }
        }

        confirm.getOKButton().setEnabled( enabled );
        log.finer( "PosQueryTicket.enableButtons ID=" + m_c_order_id );
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
            close();
        }
    }    // mouseClicked

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseEntered( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseExited( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mousePressed( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseReleased( MouseEvent e ) {}

    /**
     * Descripción de Método
     *
     */

    private void close() {
        log.finer( "PosQueryTicket.close C_Order_ID=" + m_c_order_id );

        if( m_c_order_id > 0 ) {
            p_posPanel.f_curLine.setOldOrder( m_c_order_id );
        }

        p_posPanel.closeQuery( this );
    }    // close
}    // PosQueryProduct



/*
 *  @(#)QueryTicket.java   02.07.07
 * 
 *  Fin del fichero QueryTicket.java
 *  
 *  Versión 2.1
 *
 */
