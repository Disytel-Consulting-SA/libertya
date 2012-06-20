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
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MTimeExpenseLine extends X_S_TimeExpenseLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param S_TimeExpenseLine_ID
     * @param trxName
     */

    public MTimeExpenseLine( Properties ctx,int S_TimeExpenseLine_ID,String trxName ) {
        super( ctx,S_TimeExpenseLine_ID,trxName );

        if( S_TimeExpenseLine_ID == 0 ) {

            // setS_TimeExpenseLine_ID (0);            //      PK
            // setS_TimeExpense_ID (0);                        //      Parent

            setQty( Env.ONE );
            setQtyInvoiced( Env.ZERO );
            setQtyReimbursed( Env.ZERO );

            //

            setExpenseAmt( Env.ZERO );
            setConvertedAmt( Env.ZERO );
            setPriceReimbursed( Env.ZERO );
            setInvoicePrice( Env.ZERO );
            setPriceInvoiced( Env.ZERO );

            //

            setDateExpense( new Timestamp( System.currentTimeMillis()));
            setIsInvoiced( false );
            setIsTimeReport( false );
            setLine( 10 );
            setProcessed( false );
        }
    }    // MTimeExpenseLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MTimeExpenseLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MTimeExpenseLine

    /** Descripción de Campos */

    private int m_C_Currency_Report_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQtyInvoiced() {
        BigDecimal bd = super.getQtyInvoiced();

        if( Env.ZERO.compareTo( bd ) == 0 ) {
            return getQty();
        }

        return bd;
    }    // getQtyInvoiced

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQtyReimbursed() {
        BigDecimal bd = super.getQtyReimbursed();

        if( Env.ZERO.compareTo( bd ) == 0 ) {
            return getQty();
        }

        return bd;
    }    // getQtyReimbursed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPriceInvoiced() {
        BigDecimal bd = super.getPriceInvoiced();

        if( Env.ZERO.compareTo( bd ) == 0 ) {
            return getInvoicePrice();
        }

        return bd;
    }    // getPriceInvoiced

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getPriceReimbursed() {
        BigDecimal bd = super.getPriceReimbursed();

        if( Env.ZERO.compareTo( bd ) == 0 ) {
            return getConvertedAmt();
        }

        return bd;
    }    // getPriceReimbursed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getQty().multiply( getConvertedAmt());
    }    // getApprovalAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_Report_ID() {
        if( m_C_Currency_Report_ID != 0 ) {
            return m_C_Currency_Report_ID;
        }

        // Get it from header

        MTimeExpense te = new MTimeExpense( getCtx(),getS_TimeExpense_ID(),get_TrxName());

        m_C_Currency_Report_ID = te.getC_Currency_ID();

        return m_C_Currency_Report_ID;
    }    // getC_Currency_Report_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_Currency_ID
     */

    protected void setC_Currency_Report_ID( int C_Currency_ID ) {
        m_C_Currency_Report_ID = C_Currency_ID;
    }    // getC_Currency_Report_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Calculate Converted Amount

        if( newRecord || is_ValueChanged( "ExpenseAmt" ) || is_ValueChanged( "C_Currency_ID" )) {
            if( getC_Currency_ID() == getC_Currency_Report_ID()) {
                setConvertedAmt( getExpenseAmt());
            } else {
                setConvertedAmt( MConversionRate.convert( getCtx(),getExpenseAmt(),getC_Currency_ID(),getC_Currency_Report_ID(),getDateExpense(),0,getAD_Client_ID(),getAD_Org_ID()));
            }
        }

        if( isTimeReport()) {
            setExpenseAmt( Env.ZERO );
            setConvertedAmt( Env.ZERO );
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        updateHeader();

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        updateHeader();

        return success;
    }    // afterDelete

    /**
     * Descripción de Método
     *
     */

    private void updateHeader() {
        String sql = "UPDATE S_TimeExpense te" + " SET ApprovalAmt = " + "(SELECT SUM(Qty*ConvertedAmt) FROM S_TimeExpenseLine tel " + "WHERE te.S_TimeExpense_ID=tel.S_TimeExpense_ID) " + "WHERE S_TimeExpense_ID=" + getS_TimeExpense_ID();
        int no = DB.executeUpdate( sql,get_TrxName());
    }    // updateHeader
}    // MTimeExpenseLine



/*
 *  @(#)MTimeExpenseLine.java   02.07.07
 * 
 *  Fin del fichero MTimeExpenseLine.java
 *  
 *  Versión 2.2
 *
 */
