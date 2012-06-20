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



package org.openXpertya.wstore;

import java.math.BigDecimal;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebBasketLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param M_Product_ID
     * @param Name
     * @param Qty
     * @param Price
     */

    public WebBasketLine( int M_Product_ID,String Name,BigDecimal Qty,BigDecimal Price ) {
        setM_Product_ID( M_Product_ID );
        setName( Name );
        setQuantity( Qty );
        setPrice( Price );
    }    // WebBasketLine

    /** Descripción de Campos */

    private int m_line;

    /** Descripción de Campos */

    private int m_M_Product_ID;

    /** Descripción de Campos */

    private String m_Name;

    /** Descripción de Campos */

    private BigDecimal m_Price;

    /** Descripción de Campos */

    private BigDecimal m_Quantity;

    /** Descripción de Campos */

    private BigDecimal m_Total;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toStringX() {
        StringBuffer sb = new StringBuffer( "WebBasketLine[" );

        sb.append( m_line ).append( "-M_Product_ID=" ).append( m_M_Product_ID ).append( ",Qty=" ).append( m_Quantity ).append( ",Price=" ).append( m_Price ).append( ",Total=" ).append( getTotal()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append( m_Quantity ).append( " * " ).append( m_Name ).append( " = " ).append( getTotal());

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getLine() {
        return m_line;
    }    // getLine

    /**
     * Descripción de Método
     *
     *
     * @param line
     */

    protected void setLine( int line ) {
        m_line = line;
    }    // setLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Product_ID() {
        return m_M_Product_ID;
    }    // getM_Product_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     */

    protected void setM_Product_ID( int M_Product_ID ) {
        m_M_Product_ID = M_Product_ID;
    }    // setM_Product_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        if( m_Name == null ) {
            return "-?-";
        }

        return m_Name;
    }    // getName

    /**
     * Descripción de Método
     *
     *
     * @param name
     */

    protected void setName( String name ) {
        m_Name = name;
    }    // setName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPrice() {
        if( m_Price == null ) {
            return Env.ZERO;
        }

        return m_Price;
    }    // getPrice

    /**
     * Descripción de Método
     *
     *
     * @param price
     */

    protected void setPrice( BigDecimal price ) {
        if( price == null ) {
            m_Price = Env.ZERO;
        } else {
            m_Price = price;
        }

        m_Total = null;
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQuantity() {
        if( m_Quantity == null ) {
            return Env.ZERO;
        }

        return m_Quantity;
    }    // getQuantity

    /**
     * Descripción de Método
     *
     *
     * @param quantity
     */

    public void setQuantity( BigDecimal quantity ) {
        if( quantity == null ) {
            m_Quantity = Env.ZERO;
        } else {
            m_Quantity = quantity;
        }

        m_Total = null;
    }    // setQuantity

    /**
     * Descripción de Método
     *
     *
     * @param addedQuantity
     *
     * @return
     */

    public BigDecimal addQuantity( BigDecimal addedQuantity ) {
        if( addedQuantity == null ) {
            return getQuantity();
        }

        //

        m_Quantity = getQuantity();
        m_Quantity = m_Quantity.add( addedQuantity );
        m_Total    = null;

        return m_Quantity;
    }    // addQuantity

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTotal() {
        if( m_Total == null ) {
            m_Total = getQuantity().multiply( getPrice());
        }

        return m_Total;
    }    // getTotal
}    // WebBasketLine



/*
 *  @(#)WebBasketLine.java   12.10.07
 * 
 *  Fin del fichero WebBasketLine.java
 *  
 *  Versión 2.2
 *
 */
