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

import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.logging.Level;

import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MOrderTax;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SubCurrentLine extends PosSubPanel implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubCurrentLine( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubCurrentLine

    /** Descripción de Campos */

    private CButton f_new;

    /** Descripción de Campos */

    private CButton f_reset;

    /** Descripción de Campos */

    private CButton f_plus;

    /** Descripción de Campos */

    private CButton f_minus;

    /** Descripción de Campos */

    private CLabel f_currency;

    /** Descripción de Campos */

    private VNumber f_price;

    /** Descripción de Campos */

    private CLabel f_uom;

    /** Descripción de Campos */

    private VNumber f_quantity;

    /** Descripción de Campos */

    private MOrder m_order = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubCurrentLine.class );

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( Msg.getMsg( Env.getCtx(),"CurrentLine" ));

        setBorder( border );

        // Content

        setLayout( new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = INSETS2;
        gbc.gridy  = 0;

        // --

        f_new = createButtonAction( "New",KeyStroke.getKeyStroke( KeyEvent.VK_INSERT,Event.SHIFT_MASK ));
        gbc.gridx = 0;
        add( f_new,gbc );

        //

        f_reset   = createButtonAction( "Reset",null );
        gbc.gridx = GridBagConstraints.RELATIVE;
        add( f_reset,gbc );

        //

        f_currency  = new CLabel( "---" );
        gbc.anchor  = GridBagConstraints.EAST;
        gbc.weightx = .1;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        add( f_currency,gbc );

        //

        f_price = new VNumber( "PriceActual",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"PriceActual" ));
        f_price.addActionListener( this );
        f_price.setColumns( 10,25 );
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        add( f_price,gbc );
        setPrice( Env.ZERO );

        // --

        f_uom       = new CLabel( "--" );
        gbc.anchor  = GridBagConstraints.EAST;
        gbc.weightx = .1;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        add( f_uom,gbc );

        //

        f_minus     = createButtonAction( "Minus",null );
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        add( f_minus,gbc );

        //

        f_quantity = new VNumber( "QtyOrdered",false,false,true,DisplayType.Quantity,Msg.translate( Env.getCtx(),"QtyOrdered" ));
        f_quantity.addActionListener( this );
        f_quantity.setColumns( 5,25 );
        add( f_quantity,gbc );
        setQty( Env.ONE );

        //

        f_plus = createButtonAction( "Plus",null );
        add( f_plus,gbc );
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 1;

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

        log.info( "SubCurrentLine - actionPerformed: " + action );

        // New / Reset

        if( action.equals( "New" )) {
            saveLine();
        } else if( action.equals( "Reset" )) {
            newLine();

            // Plus

        } else if( action.equals( "Plus" )) {
            f_quantity.plus();

            // Minus

        } else if( action.equals( "Minus" )) {
            f_quantity.minus( 1 );

            // VNumber

        } else if( e.getSource() == f_price ) {
            f_price.setValue( f_price.getValue());
        } else if( e.getSource() == f_quantity ) {
            f_quantity.setValue( f_quantity.getValue());
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param currency
     */

    public void setCurrency( String currency ) {
        if( currency == null ) {
            f_currency.setText( "---" );
        } else {
            f_currency.setText( currency );
        }
    }    // setCurrency

    /**
     * Descripción de Método
     *
     *
     * @param UOM
     */

    public void setUOM( String UOM ) {
        if( UOM == null ) {
            f_uom.setText( "--" );
        } else {
            f_uom.setText( UOM );
        }
    }    // setUOM

    /**
     * Descripción de Método
     *
     *
     * @param price
     */

    public void setPrice( BigDecimal price ) {
        if( price == null ) {
            price = Env.ZERO;
        }

        f_price.setValue( price );

        boolean rw = (Env.ZERO.compareTo( price ) == 0) || p_pos.isModifyPrice();

        f_price.setReadWrite( rw );
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPrice() {
        return( BigDecimal )f_price.getValue();
    }    // getPrice

    /**
     * Descripción de Método
     *
     *
     * @param price
     */

    public void setQty( BigDecimal price ) {
        f_quantity.setValue( price );
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQty() {
        return( BigDecimal )f_quantity.getValue();
    }    // getPrice

    /**
     * Descripción de Método
     *
     */

    public void newLine() {
        p_posPanel.f_product.setM_Product_ID( 0 );
        setQty( Env.ONE );
        setPrice( Env.ZERO );
        p_posPanel.f_lines.updateTable( m_order );
    }    // newLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean saveLine() {
        MProduct product = p_posPanel.f_product.getProduct();

        if( product == null ) {
            return false;
        }

        BigDecimal QtyOrdered  = ( BigDecimal )f_quantity.getValue();
        BigDecimal PriceActual = ( BigDecimal )f_price.getValue();
        MOrderLine line        = createLine( product,QtyOrdered,PriceActual );

        if( line == null ) {
            return false;
        }

        if( !line.save()) {
            return false;
        }

        //

        newLine();

        return true;
    }    // saveLine

    /**
     * Descripción de Método
     *
     *
     * @param row
     */

    public void deleteLine( int row ) {
        if( m_order != null & row != -1 ) {
            MOrderLine[] lineas    = m_order.getLines();
            int          numLineas = lineas.length;

            if( numLineas > row ) {

                // Anulamos la reserva

                lineas[ row ].setQty( Env.ZERO );
                lineas[ row ].setLineNetAmt( Env.ZERO );
                lineas[ row ].save();
                int[] lines = PO.getAllIDs("C_OrderLine", "C_Order_ID = " + m_order.getC_Order_ID(), m_order.get_TrxName() );
                m_order.reserveStock( null,lines);

                // borramos la linea de pedido

                lineas[ row ].delete( true );
            }
        }
    }    // deleteLine

    /**
     * Descripción de Método
     *
     */

    public void deleteOrder() {
        if( m_order != null ) {
            if( m_order.getDocStatus().equals( "DR" )) {
                MOrderLine[] lineas = m_order.getLines( true,null );

                if( lineas != null ) {
                    int numLineas = lineas.length;

                    if( numLineas > 0 ) {
                        for( int i = numLineas - 1;i >= 0;i-- ) {
                            if( lineas[ i ] != null ) {

                                // Anulamos la reserva

                                lineas[ i ].setQty( Env.ZERO );
                                lineas[ i ].setLineNetAmt( Env.ZERO );
                                lineas[ i ].save();
                                int[] lines = PO.getAllIDs("C_OrderLine", "C_Order_ID = " + m_order.getC_Order_ID(), m_order.get_TrxName() );
                                m_order.reserveStock( null,lines);

                                // borramos la linea de pedido

                                lineas[ i ].delete( true );
                            }
                        }
                    }
                }

                MOrderTax[] taxs = m_order.getTaxes( true );

                if( taxs != null ) {
                    int numTax = taxs.length;

                    if( numTax > 0 ) {
                        for( int i = taxs.length - 1;i >= 0;i-- ) {
                            if( taxs[ i ] != null ) {
                                taxs[ i ].delete( true );
                            }

                            taxs[ i ] = null;
                        }
                    }
                }

                m_order.delete( true );
                m_order = null;
            }
        }
    }    // deleteOrder creado por ConSerTi

    /**
     * Descripción de Método
     *
     */

    public void nuevoPedido() {
        m_order = null;
        m_order = getOrder();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MOrder getOrder() {
        if( m_order == null ) {
            m_order = new MOrder( Env.getCtx(),0,null );
            m_order.setAD_Org_ID( p_pos.getAD_Org_ID());
            m_order.setIsSOTrx( true );

            if( p_pos.getC_OrderDocType_ID() != 0 ) {
                m_order.setC_DocTypeTarget_ID( p_pos.getC_OrderDocType_ID());
            } else {
                m_order.setC_DocTypeTarget_ID( MOrder.DocSubTypeSO_POS );
            }

            MBPartner partner = p_posPanel.f_bpartner.getBPartner();

            if( (partner == null) || (partner.getID() == 0) ) {
                partner = p_pos.getBPartner();
            }

            if( (partner == null) || (partner.getID() == 0) ) {
                log.log( Level.SEVERE,"SubCurrentLine.getOrder - no BPartner" );

                return null;
            }

            log.info( "SubCurrentLine.getOrder -" + partner );
            m_order.setBPartner( partner );

            int id = p_posPanel.f_bpartner.getC_BPartner_Location_ID();

            if( id != 0 ) {
                m_order.setC_BPartner_Location_ID( id );
            }

            id = p_posPanel.f_bpartner.getAD_User_ID();

            if( id != 0 ) {
                m_order.setAD_User_ID( id );
            }

            //

            m_order.setM_PriceList_ID( p_pos.getM_PriceList_ID());
            m_order.setM_Warehouse_ID( p_pos.getM_Warehouse_ID());
            m_order.setSalesRep_ID( p_pos.getSalesRep_ID());

            if( !m_order.save()) {
                m_order = null;
            }
        }

        return m_order;
    }    // getHeader

    /**
     * Descripción de Método
     *
     *
     * @param product
     * @param QtyOrdered
     * @param PriceActual
     *
     * @return
     */

    public MOrderLine createLine( MProduct product,BigDecimal QtyOrdered,BigDecimal PriceActual ) {
        MOrder order = getOrder();

        if( order == null ) {
            return null;
        }

        MOrderLine line = new MOrderLine( order );

        line.setProduct( product );
        line.setQty( QtyOrdered );
        line.setPrice();    // sets List/limit
        line.setPrice( PriceActual );
        line.save();

        return line;
    }    // getLine

    /**
     * Descripción de Método
     *
     *
     * @param m_c_order_id
     */

    public void setOldOrder( int m_c_order_id ) {
        deleteOrder();
        m_order = new MOrder( p_ctx,m_c_order_id,null );
        p_posPanel.actualizarInfo();
    }
}    // PosSubCurrentLine



/*
 *  @(#)SubCurrentLine.java   02.07.07
 * 
 *  Fin del fichero SubCurrentLine.java
 *  
 *  Versión 2.1
 *
 */
