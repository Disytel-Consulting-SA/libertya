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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPaymentProcessor extends X_C_PaymentProcessor {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param tender
     * @param CCType
     * @param AD_Client_ID
     * @param C_Currency_ID
     * @param Amt
     * @param trxName
     *
     * @return
     */

    protected static MPaymentProcessor[] find( Properties ctx,String tender,String CCType,int AD_Client_ID,int C_Currency_ID,BigDecimal Amt,String trxName ) {
        ArrayList    list = new ArrayList();
        StringBuffer sql  = new StringBuffer( "SELECT * " + "FROM C_PaymentProcessor " + "WHERE AD_Client_ID=? AND IsActive='Y'"    // #1
            + " AND (C_Currency_ID IS NULL OR C_Currency_ID=?)"                      // #2
            + " AND (MinimumAmt IS NULL OR MinimumAmt = 0 OR MinimumAmt <= ?)" );    // #3

        if( MPayment.TENDERTYPE_DirectDeposit.equals( tender )) {
            sql.append( " AND AcceptDirectDeposit='Y'" );
        } else if( MPayment.TENDERTYPE_DirectDebit.equals( tender )) {
            sql.append( " AND AcceptDirectDebit='Y'" );
        } else if( MPayment.TENDERTYPE_Check.equals( tender )) {
            sql.append( " AND AcceptCheck='Y'" );

            // CreditCards

        } else if( MPayment.CREDITCARDTYPE_ATM.equals( CCType )) {
            sql.append( " AND AcceptATM='Y'" );
        } else if( MPayment.CREDITCARDTYPE_Amex.equals( CCType )) {
            sql.append( " AND AcceptAMEX='Y'" );
        } else if( MPayment.CREDITCARDTYPE_Visa.equals( CCType )) {
            sql.append( " AND AcceptVISA='Y'" );
        } else if( MPayment.CREDITCARDTYPE_MasterCard.equals( CCType )) {
            sql.append( " AND AcceptMC='Y'" );
        } else if( MPayment.CREDITCARDTYPE_Diners.equals( CCType )) {
            sql.append( " AND AcceptDiners='Y'" );
        } else if( MPayment.CREDITCARDTYPE_Discover.equals( CCType )) {
            sql.append( " AND AcceptDiscover='Y'" );
        } else if( MPayment.CREDITCARDTYPE_PurchaseCard.equals( CCType )) {
            sql.append( " AND AcceptCORPORATE='Y'" );
        }

        //

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString(),trxName );

            pstmt.setInt( 1,AD_Client_ID );
            pstmt.setInt( 2,C_Currency_ID );
            pstmt.setBigDecimal( 3,Amt );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPaymentProcessor( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"find - " + sql,e );

            return null;
        }

        //

        if( list.size() == 0 ) {
            s_log.warning( "find - not found - AD_Client_ID=" + AD_Client_ID + ", C_Currency_ID=" + C_Currency_ID + ", Amt=" + Amt );
        } else {
            s_log.fine( "find - #" + list.size() + " - AD_Client_ID=" + AD_Client_ID + ", C_Currency_ID=" + C_Currency_ID + ", Amt=" + Amt );
        }

        MPaymentProcessor[] retValue = new MPaymentProcessor[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // find

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPaymentProcessor.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PaymentProcessor_ID
     * @param trxName
     */

    public MPaymentProcessor( Properties ctx,int C_PaymentProcessor_ID,String trxName ) {
        super( ctx,C_PaymentProcessor_ID,trxName );

        if( C_PaymentProcessor_ID == 0 ) {

            // setC_BankAccount_ID (0);                //      Parent
            // setUserID (null);
            // setPassword (null);
            // setHostAddress (null);
            // setHostPort (0);

            setCommission( Env.ZERO );
            setAcceptVisa( false );
            setAcceptMC( false );
            setAcceptAMEX( false );
            setAcceptDiners( false );
            setCostPerTrx( Env.ZERO );
            setAcceptCheck( false );
            setRequireVV( false );
            setAcceptCorporate( false );
            setAcceptDiscover( false );
            setAcceptATM( false );
            setAcceptDirectDeposit( false );
            setAcceptDirectDebit( false );

            // setName (null);

        }
    }    // MPaymentProcessor

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPaymentProcessor( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPaymentProcessor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPaymentProcessor[" ).append( getID()).append( "-" ).append( getName()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param TenderType
     * @param CreditCardType
     *
     * @return
     */

    public boolean accepts( String TenderType,String CreditCardType ) {
        if(( MPayment.TENDERTYPE_DirectDeposit.equals( TenderType ) && isAcceptDirectDeposit()) || ( MPayment.TENDERTYPE_DirectDebit.equals( TenderType ) && isAcceptDirectDebit()) || ( MPayment.TENDERTYPE_Check.equals( TenderType ) && isAcceptCheck())

        //

        || ( MPayment.CREDITCARDTYPE_ATM.equals( CreditCardType ) && isAcceptATM()) || ( MPayment.CREDITCARDTYPE_Amex.equals( CreditCardType ) && isAcceptAMEX()) || ( MPayment.CREDITCARDTYPE_PurchaseCard.equals( CreditCardType ) && isAcceptCorporate()) || ( MPayment.CREDITCARDTYPE_Diners.equals( CreditCardType ) && isAcceptDiners()) || ( MPayment.CREDITCARDTYPE_Discover.equals( CreditCardType ) && isAcceptDiscover()) || ( MPayment.CREDITCARDTYPE_MasterCard.equals( CreditCardType ) && isAcceptMC()) || ( MPayment.CREDITCARDTYPE_Visa.equals( CreditCardType ) && isAcceptVisa())) {
            return true;
        }

        return false;
    }    // accepts
}    // MPaymentProcessor



/*
 *  @(#)MPaymentProcessor.java   02.07.07
 * 
 *  Fin del fichero MPaymentProcessor.java
 *  
 *  Versión 2.2
 *
 */
