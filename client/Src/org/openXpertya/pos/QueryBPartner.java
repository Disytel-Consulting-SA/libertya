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
import org.openXpertya.minigrid.ColumnInfo;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MBPartnerInfo;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class QueryBPartner extends PosSubPanel implements ActionListener,MouseListener,ListSelectionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public QueryBPartner( PosPanel posPanel ) {
        super( posPanel );
    }    // PosQueryBPartner

    /** Descripción de Campos */

    private MiniTable m_table;

    /** Descripción de Campos */

    private CPanel northPanel;

    /** Descripción de Campos */

    private CScrollPane centerScroll;

    /** Descripción de Campos */

    private ConfirmPanel confirm;

    /** Descripción de Campos */

    private CTextField f_value;

    /** Descripción de Campos */

    private CTextField f_name;

    /** Descripción de Campos */

    private CTextField f_contact;

    /** Descripción de Campos */

    private CTextField f_email;

    /** Descripción de Campos */

    private CTextField f_phone;

    /** Descripción de Campos */

    private CTextField f_city;

    /** Descripción de Campos */

    private CButton f_up;

    /** Descripción de Campos */

    private CButton f_down;

    /** Descripción de Campos */

    private int m_C_BPartner_ID;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( QueryBPartner.class );

    /** Descripción de Campos */

    private static ColumnInfo[] s_layout = new ColumnInfo[] {
        new ColumnInfo( " ","C_BPartner_ID",IDColumn.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Value" ),"Value",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Name" ),"Name",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Contact" ),"Contact",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Email" ),"Email",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Phone" ),"Phone",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"Postal" ),"Postal",String.class ),new ColumnInfo( Msg.translate( Env.getCtx(),"City" ),"City",String.class )
    };

    /** Descripción de Campos */

    private static String s_sqlFrom = "RV_BPartner";

    /** Descripción de Campos */

    private static String s_sqlWhere = "IsActive='Y'";

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

        CLabel lvalue = new CLabel( Msg.translate( p_ctx,"Value" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( lvalue,gbc );
        f_value = new CTextField( 10 );
        lvalue.setLabelFor( f_value );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_value,gbc );
        f_value.addActionListener( this );

        //

        CLabel lcontact = new CLabel( Msg.translate( p_ctx,"Contact" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( lcontact,gbc );
        f_contact = new CTextField( 10 );
        lcontact.setLabelFor( f_contact );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_contact,gbc );
        f_contact.addActionListener( this );

        //

        CLabel lphone = new CLabel( Msg.translate( p_ctx,"Phone" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( lphone,gbc );
        f_phone = new CTextField( 10 );
        lphone.setLabelFor( f_phone );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_phone,gbc );
        f_phone.addActionListener( this );

        //

        gbc.gridy = 1;

        CLabel lname = new CLabel( Msg.translate( p_ctx,"Name" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( lname,gbc );
        f_name = new CTextField( 10 );
        lname.setLabelFor( f_name );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_name,gbc );
        f_name.addActionListener( this );

        //

        CLabel lemail = new CLabel( Msg.translate( p_ctx,"Email" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( lemail,gbc );
        f_email = new CTextField( 10 );
        lemail.setLabelFor( f_email );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_email,gbc );
        f_email.addActionListener( this );

        //

        CLabel lcity = new CLabel( Msg.translate( p_ctx,"City" ));

        gbc.anchor = GridBagConstraints.EAST;
        northPanel.add( lcity,gbc );
        f_city = new CTextField( 10 );
        lcity.setLabelFor( f_city );
        gbc.anchor = GridBagConstraints.WEST;
        northPanel.add( f_city,gbc );
        f_city.addActionListener( this );

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

        String sql = m_table.prepareTable( s_layout,s_sqlFrom,s_sqlWhere,false,"RV_BPartner" ) + " ORDER BY Value";

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
            f_value.requestFocus();
        }
    }    // setVisible

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        log.info( e.getActionCommand());

        if( "Refresh".equals( e.getActionCommand()) || (e.getSource() == f_value    // || e.getSource() == f_upc
                ) || (e.getSource() == f_name    // || e.getSource() == f_sku
                    ) ) {
            setResults( MBPartnerInfo.find( p_ctx,f_value.getText(),f_name.getText(),f_contact.getText(),f_email.getText(),f_phone.getText(),f_city.getText()));

            return;
        } else if( "Reset".equals( e.getActionCommand())) {
            f_value.setText( null );
            f_name.setText( null );
            f_contact.setText( null );
            f_email.setText( null );
            f_phone.setText( null );
            f_city.setText( null );
            setResults( new MBPartnerInfo[ 0 ] );

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
     *
     * @param results
     */

    public void setResults( MBPartnerInfo[] results ) {
        m_table.loadTable( results );
        enableButtons();
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
        m_C_BPartner_ID = -1;

        int     row     = m_table.getSelectedRow();
        boolean enabled = row != -1;

        if( enabled ) {
            Integer ID = m_table.getSelectedRowKey();

            if( ID != null ) {
                m_C_BPartner_ID = ID.intValue();

                // m_BPartnerName = (String)m_table.getValueAt(row, 2);
                // m_Price = (BigDecimal)m_table.getValueAt(row, 7);

            }
        }

        confirm.getOKButton().setEnabled( enabled );
        log.fine( "C_BPartner_ID=" + m_C_BPartner_ID );
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
        log.fine( "C_BPartner_ID=" + m_C_BPartner_ID );

        if( m_C_BPartner_ID > 0 ) {
            p_posPanel.f_bpartner.setC_BPartner_ID( m_C_BPartner_ID );

            // p_posPanel.f_curLine.setCurrency(m_Price);

        } else {
            p_posPanel.f_bpartner.setC_BPartner_ID( 0 );

            // p_posPanel.f_curLine.setPrice(Env.ZERO);

        }

        p_posPanel.closeQuery( this );
    }    // close
}    // PosQueryBPartner



/*
 *  @(#)QueryBPartner.java   02.07.07
 * 
 *  Fin del fichero QueryBPartner.java
 *  
 *  Versión 2.2
 *
 */
