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
import java.util.ArrayList;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebBasket {

    /**
     * Constructor de la clase ...
     *
     */

    public WebBasket() {}    // WebBasket

    /** Descripción de Campos */

    public static final String NAME = "webBasket";

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private ArrayList m_lines = new ArrayList();

    /** Descripción de Campos */

    private BigDecimal m_total;

    /** Descripción de Campos */

    private int m_lineNo = 0;

    /** Descripción de Campos */

    private int m_PriceList_Version_ID = -1;

    /** Descripción de Campos */

    private int m_PriceList_ID = -1;

    /** Descripción de Campos */

    private int m_SalesRep_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "WebBasket[Lines=" );

        sb.append( m_lines.size()).append( ",Total=" ).append( m_total ).append( ",M_PriceList_ID=" + m_PriceList_ID ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTotal() {
        return getTotal( false );
    }    // getTotal

    /**
     * Descripción de Método
     *
     *
     * @param recalc
     *
     * @return
     */

    public BigDecimal getTotal( boolean recalc ) {
        if( recalc ) {
            m_total = Env.ZERO;

            for( int i = 0;i < m_lines.size();i++ ) {
                WebBasketLine wbl = ( WebBasketLine )m_lines.get( i );

                m_total = m_total.add( wbl.getTotal());
            }
        }

        if( m_total == null ) {
            return Env.ZERO;
        }

        return m_total;
    }    // getTotal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getLineCount() {
        return m_lines.size();
    }    // getLineCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getLines() {
        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param wbl
     *
     * @return
     */

    public WebBasketLine add( WebBasketLine wbl ) {
        wbl.setLine( m_lineNo++ );
        m_lines.add( wbl );
        getTotal( true );

        return wbl;
    }    // add

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     * @param Name
     * @param Qty
     * @param Price
     *
     * @return
     */

    public WebBasketLine add( int M_Product_ID,String Name,BigDecimal Qty,BigDecimal Price ) {

        // try adding to existing line

        for( int i = 0;i < m_lines.size();i++ ) {
            WebBasketLine wbl = ( WebBasketLine )m_lines.get( i );

            if( wbl.getM_Product_ID() == M_Product_ID ) {
                wbl.addQuantity( Qty );
                getTotal( true );

                return wbl;
            }
        }

        // new line

        WebBasketLine wbl = new WebBasketLine( M_Product_ID,Name,Qty,Price );

        return add( wbl );
    }    // add

    /**
     * Descripción de Método
     *
     *
     * @param no
     */

    public void delete( int no ) {
        for( int i = 0;i < m_lines.size();i++ ) {
            WebBasketLine wbl = ( WebBasketLine )m_lines.get( i );

            if( wbl.getLine() == no ) {
                m_lines.remove( i );
                getTotal( true );

                break;
            }
        }
    }    // delete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_PriceList_Version_ID() {
        return m_PriceList_Version_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param PriceList_Version_ID
     */

    public void setM_PriceList_Version_ID( int PriceList_Version_ID ) {
        if( PriceList_Version_ID > 0 ) {
            m_PriceList_Version_ID = PriceList_Version_ID;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_PriceList_ID() {
        return m_PriceList_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param PriceList_ID
     */

    public void setM_PriceList_ID( int PriceList_ID ) {
        if( PriceList_ID > 0 ) {
            m_PriceList_ID = PriceList_ID;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSalesRep_ID() {
        return m_SalesRep_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param salesRep_ID
     */

    public void setSalesRep_ID( int salesRep_ID ) {
        m_SalesRep_ID = salesRep_ID;
    }
}    // WebBasket



/*
 *  @(#)WebBasket.java   12.10.07
 * 
 *  Fin del fichero WebBasket.java
 *  
 *  Versión 2.2
 *
 */
