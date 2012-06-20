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
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBankStatementLine extends X_C_BankStatementLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BankStatementLine_ID
     * @param trxName
     */

    public MBankStatementLine( Properties ctx,int C_BankStatementLine_ID,String trxName ) {
        super( ctx,C_BankStatementLine_ID,trxName );

        if( C_BankStatementLine_ID == 0 ) {

            // setC_BankStatement_ID (0);              //      Parent
            // setC_Charge_ID (0);
            // setC_Currency_ID (0);   //      Bank Acct Currency
            // setLine (0);    // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_BankStatementLine WHERE C_BankStatement_ID=@C_BankStatement_ID@

            setStmtAmt( Env.ZERO );
            setTrxAmt( Env.ZERO );
            setInterestAmt( Env.ZERO );
            setChargeAmt( Env.ZERO );
            setIsReversal( false );

            // setValutaDate (new Timestamp(System.currentTimeMillis()));      // @StatementDate@
            // setDateAcct (new Timestamp(System.currentTimeMillis()));        // @StatementDate@

        }
    }    // MBankStatementLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBankStatementLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBankStatementLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param statement
     */

    public MBankStatementLine( MBankStatement statement ) {
        this( statement.getCtx(),0,statement.get_TrxName());
        setClientOrg( statement );
        setC_BankStatement_ID( statement.getC_BankStatement_ID());
        setStatementLineDate( statement.getStatementDate());
    }    // MBankStatementLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param statement
     * @param lineNo
     */

    public MBankStatementLine( MBankStatement statement,int lineNo ) {
        this( statement );
        setLine( lineNo );
    }    // MBankStatementLine

    /**
     * Descripción de Método
     *
     *
     * @param StatementLineDate
     */

    public void setStatementLineDate( Timestamp StatementLineDate ) {
        super.setStatementLineDate( StatementLineDate );
        setValutaDate( StatementLineDate );
        setDateAcct( StatementLineDate );
    }    // setStatementLineDate

    /**
     * Descripción de Método
     *
     *
     * @param payment
     */

    public void setPayment( MPayment payment ) {
        setC_Payment_ID( payment.getC_Payment_ID());
        
        if(Util.isEmpty(getC_Currency_ID(), true)){
            setC_Currency_ID(payment.getC_Currency_ID());
        }

        BigDecimal amt = payment.getPayAmt( true );

        // Cargo del pago
        if(!Util.isEmpty(payment.getC_Charge_ID(), true)){
            setC_Charge_ID(payment.getC_Charge_ID());
            setChargeAmt(amt);
            setTrxAmt(BigDecimal.ZERO);
        }
        else{
            setTrxAmt( amt );
        }
        setStmtAmt( amt );

        // setDescription( payment.getDescription());
        // Agrega para no borrar la descripción original de la línea
        addDescription(payment.getDescription());
        
        if(Util.isEmpty(getC_BPartner_ID(), true)){
        	setC_BPartner_ID(payment.getC_BPartner_ID());
        }
    }    // setPayment

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     */

    public void setC_Charge_ID() {
        MCharge charge = MCharge.getDefault( getCtx());

        if( charge != null ) {
            super.setC_Charge_ID( charge.getC_Charge_ID());
        }
    }    // setC_Charge_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        log.fine( "beforeSave" );

        // Set Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM C_BankStatementLine WHERE C_BankStatement_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getC_BankStatement_ID());

            setLine( ii );
        }

        // Set References

        if( (getC_Payment_ID() != 0) && (getC_BPartner_ID() == 0) ) {
            MPayment payment = new MPayment( getCtx(),getC_Payment_ID(),get_TrxName());

            setC_BPartner_ID( payment.getC_BPartner_ID());

            if( payment.getC_Invoice_ID() != 0 ) {
                setC_Invoice_ID( payment.getC_Invoice_ID());
            }
        }

        if( (getC_Invoice_ID() != 0) && (getC_BPartner_ID() == 0) ) {
            MInvoice invoice = new MInvoice( getCtx(),getC_Invoice_ID(),get_TrxName());

            setC_BPartner_ID( invoice.getC_BPartner_ID());
        }

        // Calculate Charge = Statement - trx - Interest

        BigDecimal amt = getStmtAmt();

        amt = amt.subtract( getTrxAmt());
        amt = amt.subtract( getInterestAmt());

        if( amt.compareTo( getChargeAmt()) != 0 ) {
            setChargeAmt( amt );
        }

        //

        if( (getChargeAmt().compareTo( Env.ZERO ) != 0) && (getC_Charge_ID() == 0) ) {
            setC_Charge_ID();
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
    }    // afterSave

    /**
     * Descripción de Método
     *
     */

    private void updateHeader() {
        String sql = "UPDATE C_BankStatement bs" + " SET StatementDifference=(SELECT coalesce(SUM(StmtAmt),0) FROM C_BankStatementLine bsl " + "WHERE bsl.C_BankStatement_ID=bs.C_BankStatement_ID AND bsl.IsActive='Y') " + "WHERE C_BankStatement_ID=" + getC_BankStatement_ID();

        DB.executeUpdate( sql,get_TrxName());
        sql = "UPDATE C_BankStatement bs" + " SET EndingBalance=coalesce((BeginningBalance+StatementDifference),0) " + "WHERE C_BankStatement_ID=" + getC_BankStatement_ID();
        DB.executeUpdate( sql,get_TrxName());
    }    // updateHeader
    
    public void setBoletaDeposito(MBoletaDeposito boleta) {
    	BigDecimal amt = boleta.getGrandTotal();
    	
    	setM_BoletaDeposito_ID(boleta.getM_BoletaDeposito_ID());
    	setC_Currency_ID(boleta.getC_Currency_ID());
    	
    	//setDescription("Boleta Deposito " + boleta.getDocumentNo().trim() + " - " + boleta.getFechaDeposito().toString().substring(10) + " $" + amt.toString());
        // Agrega para no borrar la descripción original de la línea
        addDescription("Boleta Deposito " + boleta.getDocumentNo().trim() + " - " + boleta.getFechaDeposito().toString().substring(10) + " $" + amt.toString());
    	
    	setIsReconciled(true);
    }
    
}    // MBankStatementLine



/*
 *  @(#)MBankStatementLine.java   02.07.07
 * 
 *  Fin del fichero MBankStatementLine.java
 *  
 *  Versión 2.2
 *
 */
