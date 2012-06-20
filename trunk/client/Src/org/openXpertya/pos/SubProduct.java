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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CTextField;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MWarehousePrice;
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

public class SubProduct extends PosSubPanel implements ActionListener,FocusListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubProduct( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubProduct

    /** Descripción de Campos */

    protected CTextField f_name;

    /** Descripción de Campos */

    private CButton f_bSearch;

    /** Descripción de Campos */

    private MProduct m_product = null;

    /** Descripción de Campos */

    private int m_M_Warehouse_ID;

    /** Descripción de Campos */

    private int m_M_PriceList_Version_ID;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubProduct.class );

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( Msg.translate( p_ctx,"M_Product_ID" ));

        setBorder( border );

        // Content

        setLayout( new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = INSETS2;

        // --

        f_name = new CTextField( "" );
        f_name.setName( "Name" );
        f_name.addActionListener( this );
        f_name.addFocusListener( this );
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.anchor  = GridBagConstraints.EAST;
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        add( f_name,gbc );

        //

        f_bSearch = createButtonAction( "Product",KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.CTRL_MASK ));
        gbc.gridx   = 1;
        gbc.gridy   = 0;
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add( f_bSearch,gbc );
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 1;

        return gbc;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( f_name != null ) {
            f_name.removeFocusListener( this );
        }

        removeAll();
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

        log.info( "PosSubProduct - actionPerformed: " + action );

        // Product

        if( action.equals( "Product" )) {
            setParameter();
            p_posPanel.openQuery( p_posPanel.f_queryProduct );
        }

        // Name

        else if( e.getSource() == f_name ) {
            findProduct();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusGained( FocusEvent e ) {}    // focusGained

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusLost( FocusEvent e ) {
        if( e.isTemporary()) {
            return;
        }

        log.info( "PosSubProduct - focusLost" );
        findProduct();
    }    // focusLost

    /**
     * Descripción de Método
     *
     */

    private void setParameter() {

        // What PriceList ?

        m_M_Warehouse_ID         = p_pos.getM_Warehouse_ID();
        m_M_PriceList_Version_ID = p_posPanel.f_bpartner.getM_PriceList_Version_ID();
        p_posPanel.f_queryProduct.setQueryData( m_M_PriceList_Version_ID,m_M_Warehouse_ID );
    }    // setParameter

    /**
     * Descripción de Método
     *
     */

    private void findProduct() {
        String query = f_name.getText();

        if( (query == null) || (query.length() == 0) ) {
            return;
        }

        query = query.toUpperCase();

        // Test Number

        boolean allNumber = true;

        try {
            Integer.parseInt( query );
        } catch( Exception e ) {
            allNumber = false;
        }

        String            Value   = query;
        String            Name    = query;
        String            UPC     = ( allNumber
                                      ?query
                                      :null );
        String            SKU     = ( allNumber
                                      ?query
                                      :null );
        MWarehousePrice[] results = null;

        setParameter();

        //

        results = MWarehousePrice.find( p_ctx,m_M_PriceList_Version_ID,m_M_Warehouse_ID,Value,Name,UPC,SKU,null );

        // Set Result

        if( results.length == 0 ) {
            setM_Product_ID( 0 );
            p_posPanel.f_curLine.setPrice( Env.ZERO );
        } else if( results.length == 1 ) {
            setM_Product_ID( results[ 0 ].getM_Product_ID());
            f_name.setText( results[ 0 ].getName());
            p_posPanel.f_curLine.setPrice( results[ 0 ].getPriceStd());
        } else    // more than one
        {
            p_posPanel.f_queryProduct.setResults( results );
            p_posPanel.openQuery( p_posPanel.f_queryProduct );
        }
    }             // findProduct

    /**
     * Descripción de Método
     *
     */

    public void setPrice() {
        if( m_product == null ) {
            return;
        }

        //

        setParameter();

        MWarehousePrice result = MWarehousePrice.get( m_product,m_M_PriceList_Version_ID,m_M_Warehouse_ID,null );

        if( result != null ) {
            p_posPanel.f_curLine.setPrice( result.getPriceStd());
        }
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     */

    public void setM_Product_ID( int M_Product_ID ) {
        log.fine( "PosSubProduct.setM_Product_ID=" + M_Product_ID );

        if( M_Product_ID <= 0 ) {
            m_product = null;
        } else {
            m_product = MProduct.get( p_ctx,M_Product_ID );

            if( m_product.getID() == 0 ) {
                m_product = null;
            }
        }

        // Set String Info

        if( m_product != null ) {
            f_name.setText( m_product.getName());
            f_name.setToolTipText( m_product.getDescription());
            p_posPanel.f_curLine.setUOM( m_product.getUOMSymbol());
        } else {
            f_name.setText( null );
            f_name.setToolTipText( null );
            p_posPanel.f_curLine.setUOM( null );
        }
    }    // setM_Product_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Product_ID() {
        if( m_product != null ) {
            return m_product.getM_Product_ID();
        }

        return 0;
    }    // getM_Product_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_UOM_ID() {
        if( m_product != null ) {
            return m_product.getC_UOM_ID();
        }

        return 0;
    }    // getC_UOM_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProductName() {
        if( m_product != null ) {
            return m_product.getName();
        }

        return "";
    }    // getProductName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProduct getProduct() {
        return m_product;
    }    // getProduct
}    // PosSubProduct



/*
 *  @(#)SubProduct.java   02.07.07
 * 
 *  Fin del fichero SubProduct.java
 *  
 *  Versión 2.2
 *
 */
