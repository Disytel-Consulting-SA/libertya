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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDistributionRunLine extends X_M_DistributionRunLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_DistributionRunLine_ID
     * @param trxName
     */

    public MDistributionRunLine( Properties ctx,int M_DistributionRunLine_ID,String trxName ) {
        super( ctx,M_DistributionRunLine_ID,trxName );
    }    // MDistributionRunLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDistributionRunLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDistributionRunLine

    /** Descripción de Campos */

    private MProduct m_product = null;

    /** Descripción de Campos */

    private BigDecimal m_actualQty = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_actualMin = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_actualAllocation = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_lastDifference = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_maxAllocation = Env.ZERO;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getActualQty() {
        return m_actualQty;
    }    // getActualQty

    /**
     * Descripción de Método
     *
     *
     * @param add
     */

    public void addActualQty( BigDecimal add ) {
        m_actualQty = m_actualQty.add( add );
    }    // addActualQty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getActualMin() {
        return m_actualMin;
    }    // getActualMin

    /**
     * Descripción de Método
     *
     *
     * @param add
     */

    public void addActualMin( BigDecimal add ) {
        m_actualMin = m_actualMin.add( add );
    }    // addActualMin

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isActualMinGtTotal() {
        return m_actualMin.compareTo( getTotalQty()) > 0;
    }    // isActualMinGtTotal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getActualAllocation() {
        return m_actualAllocation;
    }    // getActualAllocation

    /**
     * Descripción de Método
     *
     *
     * @param add
     */

    public void addActualAllocation( BigDecimal add ) {
        m_actualAllocation = m_actualAllocation.add( add );
    }    // addActualAllocation

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isActualAllocationEqTotal() {
        return m_actualAllocation.compareTo( getTotalQty()) == 0;
    }    // isActualAllocationEqTotal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getActualAllocationDiff() {
        return getTotalQty().subtract( m_actualAllocation );
    }    // getActualAllocationDiff

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getLastDifference() {
        return m_lastDifference;
    }    // getLastDifference

    /**
     * Descripción de Método
     *
     *
     * @param difference
     */

    public void setLastDifference( BigDecimal difference ) {
        m_lastDifference = difference;
    }    // setLastDifference

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getMaxAllocation() {
        return m_maxAllocation;
    }    // getMaxAllocation

    /**
     * Descripción de Método
     *
     *
     * @param max
     * @param set
     */

    public void setMaxAllocation( BigDecimal max,boolean set ) {
        if( set || (max.compareTo( m_maxAllocation ) > 0) ) {
            m_maxAllocation = max;
        }
    }    // setMaxAllocation

    /**
     * Descripción de Método
     *
     */

    public void resetCalculations() {
        m_actualQty        = Env.ZERO;
        m_actualMin        = Env.ZERO;
        m_actualAllocation = Env.ZERO;

        // m_lastDifference = Env.ZERO;

        m_maxAllocation = Env.ZERO;
    }    // resetCalculations

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProduct getProduct() {
        if( m_product == null ) {
            m_product = MProduct.get( getCtx(),getM_Product_ID());
        }

        return m_product;
    }    // getProduct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getStandardPrecision() {
        return getProduct().getStandardPrecision();
    }    // getStandardPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MDistributionRunLine[" );

        sb.append( getID()).append( getInfo()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfo() {
        StringBuffer sb = new StringBuffer();

        sb.append( "Line=" ).append( getLine()).append( ",TotalQty=" ).append( getTotalQty()).append( ",SumMin=" ).append( getActualMin()).append( ",SumQty=" ).append( getActualQty()).append( ",SumAllocation=" ).append( getActualAllocation()).append( ",MaxAllocation=" ).append( getMaxAllocation()).append( ",LastDiff=" ).append( getLastDifference());

        return sb.toString();
    }    // getInfo
}    // MDistributionRunLine



/*
 *  @(#)MDistributionRunLine.java   02.07.07
 * 
 *  Fin del fichero MDistributionRunLine.java
 *  
 *  Versión 2.2
 *
 */
