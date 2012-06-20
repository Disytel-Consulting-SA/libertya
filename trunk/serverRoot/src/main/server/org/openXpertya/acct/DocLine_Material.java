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



package org.openXpertya.acct;

import java.math.BigDecimal;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocLine_Material extends DocLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param DocumentType
     * @param TrxHeader_ID
     * @param TrxLine_ID
     * @param trxName
     */

    public DocLine_Material( String DocumentType,int TrxHeader_ID,int TrxLine_ID,String trxName ) {
        super( DocumentType,TrxHeader_ID,TrxLine_ID,trxName );
    }    // DocLine_Material

    /** Descripción de Campos */

    private int m_M_Locator_ID = 0;

    /** Descripción de Campos */

    private int m_M_LocatorTo_ID = 0;

    /** Descripción de Campos */

    private int m_C_OrderLine_ID = 0;

    /** Descripción de Campos */

    private boolean m_productionBOM = false;

    /**
     * Descripción de Método
     *
     *
     * @param M_Locator_ID
     */

    public void setM_Locator_ID( int M_Locator_ID ) {
        m_M_Locator_ID = M_Locator_ID;
    }    // setM_Locator_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Locator_ID() {
        return m_M_Locator_ID;
    }    // getM_Locator_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_Locator_ID
     */

    public void setM_LocatorTo_ID( int M_Locator_ID ) {
        m_M_LocatorTo_ID = M_Locator_ID;
    }    // setM_LocatorTo_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_LocatorTo_ID() {
        return m_M_LocatorTo_ID;
    }    // getM_LocatorTo_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_OrderLine_ID
     */

    public void setm_C_OrderLine_ID( int C_OrderLine_ID ) {
        m_C_OrderLine_ID = C_OrderLine_ID;
    }    // setC_OrderLine_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_OrderLine_ID() {
        return m_C_OrderLine_ID;
    }    // getC_OrderLine_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getOrder_AD_Org_ID() {
        if( m_C_OrderLine_ID != 0 ) {
            String sql = "SELECT AD_Org_ID FROM C_OrderLine WHERE C_OrderLine_ID=?";
            int AD_Org_ID = DB.getSQLValue( null,sql,m_C_OrderLine_ID );

            if( AD_Org_ID > 0 ) {
                return AD_Org_ID;
            }
        }

        return getAD_Org_ID();
    }    // getOrder_AD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_UOM_ID() {

        // Trx UOM

        if( p_DocumentType.equals( Doc.DOCTYPE_MatShipment ) || p_DocumentType.equals( Doc.DOCTYPE_MatReceipt )) {
            return super.getC_UOM_ID();
        }

        // Storage UOM

        return p_productInfo.getC_UOM_ID();
    }    // getC_UOM_ID

    /**
     * Descripción de Método
     *
     *
     * @param qty
     * @param isSOTrx
     */

    public void setQty( BigDecimal qty,boolean isSOTrx ) {
        super.setQty( qty,isSOTrx );    // save TrxQty
        p_productInfo.setQty( qty,p_productInfo.getC_UOM_ID());
    }                                   // setQty

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public BigDecimal getProductCosts( MAcctSchema as ) {
        return p_productInfo.getProductCosts( as );
    }    // getProductCosts

    /**
     * Descripción de Método
     *
     *
     * @param AcctType
     * @param as
     *
     * @return
     */

    public MAccount getAccount( int AcctType,MAcctSchema as ) {
        return p_productInfo.getAccount( AcctType,as );
    }    // getAccount

    /**
     * Descripción de Método
     *
     *
     * @param productionBOM
     */

    public void setProductionBOM( boolean productionBOM ) {
        m_productionBOM = productionBOM;
    }    // setProductionBOM

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isProductionBOM() {
        return m_productionBOM;
    }    // isProductionBOM

    // Begin fjv e-Evolution

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param M_Warehouse_ID
     *
     * @return
     */

    public BigDecimal getProductCosts( MAcctSchema as,int M_Warehouse_ID ) {
        return p_productInfo.getProductCosts( as,M_Warehouse_ID );
    }    // getProductCosts

    // end fjv e-Evolution

}    // DocLine_Material



/*
 *  @(#)DocLine_Material.java   24.03.06
 * 
 *  Fin del fichero DocLine_Material.java
 *  
 *  Versión 2.2
 *
 */
