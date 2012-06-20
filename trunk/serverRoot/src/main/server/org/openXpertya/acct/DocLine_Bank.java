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

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//import org.openXpertya.model.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocLine_Bank extends DocLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param DocumentType
     * @param TrxHeader_ID
     * @param TrxLine_ID
     * @param trxName
     */

    public DocLine_Bank( String DocumentType,int TrxHeader_ID,int TrxLine_ID,String trxName ) {
        super( DocumentType,TrxHeader_ID,TrxLine_ID,trxName );
    }    // DocLine_Bank

    /** Descripción de Campos */

    private boolean m_IsReversal = false;

    /** Descripción de Campos */

    private int m_C_Payment_ID = 0;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private BigDecimal m_TrxAmt = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_StmtAmt = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_InterestAmt = Env.ZERO;

    /**
     * Descripción de Método
     *
     *
     * @param C_Payment_ID
     */

    public void setC_Payment_ID( int C_Payment_ID ) {
        m_C_Payment_ID = C_Payment_ID;
    }    // setC_Payment_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Payment_ID() {
        return m_C_Payment_ID;
    }    // getC_Payment_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        return m_C_BPartner_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param partner_ID
     */

    public void setC_BPartner_ID( int partner_ID ) {
        m_C_BPartner_ID = partner_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param payment
     *
     * @return
     */

    public int getAD_Org_ID( boolean payment ) {
        if( payment && (getC_Payment_ID() != 0) ) {
            String sql = "SELECT AD_Org_ID FROM C_Payment WHERE C_Payment_ID=?";
            int id = DB.getSQLValue( null,sql,getC_Payment_ID());

            if( id > 0 ) {
                return id;
            }
        }

        return super.getAD_Org_ID();
    }    // getAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @param IsReversal
     */

    public void setIsReversal( String IsReversal ) {
        if( (IsReversal != null) && IsReversal.equals( "Y" )) {
            m_IsReversal = true;
        } else {
            m_IsReversal = false;
        }
    }    // setIsReversal

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReversal() {
        return m_IsReversal;
    }    // isReversal

    /**
     * Descripción de Método
     *
     *
     * @param StmtAmt
     * @param InterestAmt
     * @param TrxAmt
     */

    public void setAmount( BigDecimal StmtAmt,BigDecimal InterestAmt,BigDecimal TrxAmt ) {
        if( StmtAmt != null ) {
            m_StmtAmt = StmtAmt;
        }

        if( InterestAmt != null ) {
            m_InterestAmt = InterestAmt;
        }

        if( TrxAmt != null ) {
            m_TrxAmt = TrxAmt;
        }
    }    // setAmount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getInterestAmt() {
        return m_InterestAmt;
    }    // getInterestAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getStmtAmt() {
        return m_StmtAmt;
    }    // getStrmtAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTrxAmt() {
        return m_TrxAmt;
    }    // getTrxAmt
}    // DocLine_Bank



/*
 *  @(#)DocLine_Bank.java   24.03.06
 * 
 *  Fin del fichero DocLine_Bank.java
 *  
 *  Versión 2.2
 *
 */
