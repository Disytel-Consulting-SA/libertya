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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class DocTax {

    /**
     * Constructor de la clase ...
     *
     *
     * @param C_Tax_ID
     * @param name
     * @param rate
     * @param taxBaseAmt
     * @param amount
     */

    public DocTax( int C_Tax_ID,String name,BigDecimal rate,BigDecimal taxBaseAmt,BigDecimal amount ) {
        m_C_Tax_ID = C_Tax_ID;
        m_name     = name;
        m_rate     = rate;
        m_amount   = amount;
    }    // DocTax

    /** Descripción de Campos */

    private int m_C_Tax_ID = 0;

    /** Descripción de Campos */

    private BigDecimal m_amount = null;

    /** Descripción de Campos */

    private BigDecimal m_rate = null;

    /** Descripción de Campos */

    private String m_name = null;

    /** Descripción de Campos */

    private BigDecimal m_taxBaseAmt = null;

    /** Descripción de Campos */

    private BigDecimal m_includedTax = Env.ZERO;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( DocTax.class );

    /** Descripción de Campos */

    public static final int ACCTTYPE_TaxDue = 0;

    /** Descripción de Campos */

    public static final int ACCTTYPE_TaxLiability = 1;

    /** Descripción de Campos */

    public static final int ACCTTYPE_TaxCredit = 2;

    /** Descripción de Campos */

    public static final int ACCTTYPE_TaxReceivables = 3;

    /** Descripción de Campos */

    public static final int ACCTTYPE_TaxExpense = 4;

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
        if( (AcctType < 0) || (AcctType > 4) ) {
            return null;
        }

        //

        String sql = "SELECT T_Due_Acct, T_Liability_Acct, T_Credit_Acct, T_Receivables_Acct, T_Expense_Acct " + "FROM C_Tax_Acct WHERE C_Tax_ID=? AND C_AcctSchema_ID=?";
        int validCombination_ID = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_C_Tax_ID );
            pstmt.setInt( 2,as.getC_AcctSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                validCombination_ID = rs.getInt( AcctType + 1 );    // 1..5
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"Tax.getAccount",e );
        }

        if( validCombination_ID == 0 ) {
            return null;
        }

        return MAccount.get( as.getCtx(),validCombination_ID );
    }    // getAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmount() {
        return m_amount;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTaxBaseAmt() {
        return m_taxBaseAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getRate() {
        return m_rate;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Tax_ID() {
        return m_C_Tax_ID;
    }    // getC_Tax_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_name + " " + m_taxBaseAmt.toString();
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @param amt
     */

    public void addIncludedTax( BigDecimal amt ) {
        m_includedTax = m_includedTax.add( amt );
    }    // addIncludedTax

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getIncludedTax() {
        return m_includedTax;
    }    // getIncludedTax

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getIncludedTaxDifference() {
        return m_amount.subtract( m_includedTax );
    }    // getIncludedTaxDifference

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isIncludedTaxDifference() {
        return Env.ZERO.compareTo( getIncludedTaxDifference()) != 0;
    }    // isIncludedTaxDifference

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Tax=(" );

        sb.append( m_name );
        sb.append( " Amt=" ).append( m_amount );
        sb.append( ")" );

        return sb.toString();
    }    // toString
}    // DocTax



/*
 *  @(#)DocTax.java   24.03.06
 * 
 *  Fin del fichero DocTax.java
 *  
 *  Versión 2.2
 *
 */
