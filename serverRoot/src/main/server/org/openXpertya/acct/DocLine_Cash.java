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

public class DocLine_Cash extends DocLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param DocumentType
     * @param TrxHeader_ID
     * @param TrxLine_ID
     * @param trxName
     */

    public DocLine_Cash( String DocumentType,int TrxHeader_ID,int TrxLine_ID,String trxName ) {
        super( DocumentType,TrxHeader_ID,TrxLine_ID,trxName );
    }    // DocLine_Cash

    /** Descripción de Campos */

    private String m_CashType = "";

    // AD_Reference_ID=217

    /** Descripción de Campos */

    public static final String CASHTYPE_CHARGE = "C";

    /** Descripción de Campos */

    public static final String CASHTYPE_DIFFERENCE = "D";

    /** Descripción de Campos */

    public static final String CASHTYPE_EXPENSE = "E";

    /** Descripción de Campos */

    public static final String CASHTYPE_INVOICE = "I";

    /** Descripción de Campos */

    public static final String CASHTYPE_RECEIPT = "R";

    /** Descripción de Campos */

    public static final String CASHTYPE_TRANSFER = "T";
    
    /** Tipo de Efectivo: Transferencia entre Cajas = X */
    public static final String CASHTYPE_CASH_TRANSFER = "X";

    // References

    /** Descripción de Campos */

    private int m_C_BankAccount_ID = 0;

    /** Descripción de Campos */

    private int m_C_Invoice_ID = 0;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;
    
    /** ID de caja de transferencia */
    private int m_TransferCash_ID = 0;
    
    /** ID del libro de la transferencia */
    private int m_TransferCashBook_ID = 0;

    //

    /** Descripción de Campos */

    private int m_C_Currency_ID = -1;

    /** Descripción de Campos */

    private int m_AD_Org_ID = -1;

    // Amounts

    /** Descripción de Campos */

    private BigDecimal m_Amount = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_DiscountAmt = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_WriteOffAmt = Env.ZERO;

    /**
     * Descripción de Método
     *
     *
     * @param CashType
     */

    public void setCashType( String CashType ) {
        if( CashType != null ) {
            m_CashType = CashType;
        }
    }    // setCashType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCashType() {
        return m_CashType;
    }    // getCashType

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     * @param C_Invoice_ID
     */

    public void setReference( int C_BankAccount_ID,int C_Invoice_ID ) {
        m_C_BankAccount_ID = C_BankAccount_ID;
        m_C_Invoice_ID     = C_Invoice_ID;
        setReferenceInfo();
    }    // setReference

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BankAccount_ID() {
        return m_C_BankAccount_ID;
    }    // getC_BankAccount_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Invoice_ID() {
        return m_C_Invoice_ID;
    }    // getC_Invoice_ID

    /**
     * Descripción de Método
     *
     *
     * @param Amount
     * @param DiscountAmt
     * @param WriteOffAmt
     */

    public void setAmount( BigDecimal Amount,BigDecimal DiscountAmt,BigDecimal WriteOffAmt ) {
        if( Amount != null ) {
            m_Amount = Amount;
        }

        if( DiscountAmt != null ) {
            m_DiscountAmt = DiscountAmt;
        }

        if( WriteOffAmt != null ) {
            m_WriteOffAmt = WriteOffAmt;
        }

        //

        setAmount( Amount );
    }    // setAmount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmount() {
        return m_Amount;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getDiscountAmt() {
        return m_DiscountAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getWriteOffAmt() {
        return m_WriteOffAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        if( (m_C_BankAccount_ID == 0) && (m_C_Invoice_ID == 0) ) {
            return super.getC_Currency_ID();
        }

        if( m_C_Currency_ID == -1 ) {
            setReferenceInfo();
        }

        return m_C_Currency_ID;
    }    // getC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Org_ID() {
        if( (m_C_BankAccount_ID == 0) && (m_C_Invoice_ID == 0) ) {
            return super.getAD_Org_ID();
        }

        if( m_AD_Org_ID == -1 ) {
            setReferenceInfo();
        }

        return m_AD_Org_ID;
    }    // getAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        if( m_C_Invoice_ID == 0 ) {
            return super.getC_BPartner_ID();
        }

        if( m_C_BPartner_ID == -1 ) {
            setReferenceInfo();
        }

        return m_C_BPartner_ID;
    }    // getC_BPartner_ID

    /**
     * Descripción de Método
     *
     */

    private void setReferenceInfo() {
        m_C_Currency_ID = 0;
        m_AD_Org_ID     = 0;
        m_C_BPartner_ID = 0;

        String sql       = null;
        int    parameter = 0;

        // Bank Account Info

        if( m_C_BankAccount_ID != 0 ) {
            sql = "SELECT AD_Org_ID, C_Currency_ID, 0 FROM C_BankAccount WHERE C_BankAccount_ID=?";
            parameter = m_C_BankAccount_ID;
        } else if( m_C_Invoice_ID != 0 ) {
            sql = "SELECT AD_Org_ID, C_Currency_ID, C_BPartner_ID FROM C_Invoice WHERE C_Invoice_ID=?";
            parameter = m_C_Invoice_ID;
        } else {
            return;
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,parameter );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_AD_Org_ID     = rs.getInt( 1 );
                m_C_Currency_ID = rs.getInt( 2 );
                m_C_BPartner_ID = rs.getInt( 3 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"setReferenceInfo",e );
        }
    }    // setReferenceInfo

	/**
	 * @return el valor de m_TransferCash_ID
	 */
	public int getTransferCash_ID() {
		return m_TransferCash_ID;
	}

	/**
	 * @param transferCash_ID el valor de TransferCash_ID a asignar
	 */
	public void setTransferCash_ID(int transferCash_ID) {
		this.m_TransferCash_ID = transferCash_ID;
		if (transferCash_ID > 0) {
			String sql = "SELECT C_CashBook_ID FROM C_Cash WHERE C_Cash_ID = ?";
			m_TransferCashBook_ID = DB.getSQLValue(null, sql, transferCash_ID);
		} else {
			m_TransferCashBook_ID = 0;
		}
	}

	/**
	 * @return el valor de TransferCashBook_ID
	 */
	public int getTransferCashBook_ID() {
		return m_TransferCashBook_ID;
	}
    
}    // DocLine_Cash



/*
 *  @(#)DocLine_Cash.java   24.03.06
 * 
 *  Fin del fichero DocLine_Cash.java
 *  
 *  Versión 2.2
 *
 */
