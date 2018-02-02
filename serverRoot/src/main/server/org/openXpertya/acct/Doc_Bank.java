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
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MBankAccount;
import org.openXpertya.model.MBankTransfer;
import org.openXpertya.model.MCharge;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPeriod;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_Bank extends Doc {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_Bank( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_Bank

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType = DOCTYPE_BankStatement;

        try {
            p_vo.DateDoc = rs.getTimestamp( "StatementDate" );

            // Amounts

            p_vo.Amounts[ Doc.AMTTYPE_Gross ] = rs.getBigDecimal( "StatementDifference" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Gross ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Gross ] = Env.ZERO;
            }
            
            //Agregado por Ibrian para setear el documentNo
            p_vo.DocumentNo = rs.getString("name");
            
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        // Set Bank Account Info (Currency)

        MBankAccount ba = MBankAccount.get( getCtx(),p_vo.C_BankAccount_ID );

        p_vo.C_Currency_ID = ba.getC_Currency_ID();
        loadDocumentType();    // lines require doc type

        // Contained Objects

        p_lines = loadLines();
        log.fine( "Lines=" + p_lines.length );

        return true;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private DocLine[] loadLines() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_BankStatementLine WHERE C_BankStatement_ID=? ORDER BY Line";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int          Line_ID = rs.getInt( "C_BankStatementLine_ID" );
                DocLine_Bank docLine = new DocLine_Bank( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );
                docLine.setDateDoc( rs.getTimestamp( "ValutaDate" ));
                docLine.setC_BPartner_ID( rs.getInt( "C_BPartner_ID" ));
                docLine.setC_Payment_ID( rs.getInt( "C_Payment_ID" ));
                docLine.setIsReversal( rs.getString( "IsReversal" ));
                docLine.setAmount( rs.getBigDecimal( "StmtAmt" ),rs.getBigDecimal( "InterestAmt" ),rs.getBigDecimal( "TrxAmt" ));

                //

                MPeriod period = MPeriod.get( getCtx(),docLine.getDateAcct());

                if( (period != null) && period.isOpen( p_vo.DocumentType )) {
                    docLine.setC_Period_ID( period.getC_Period_ID());
                }

                //

                list.add( docLine );
            }

            //

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadLines",e );
        }

        // Return Array

        DocLine[] dl = new DocLine[ list.size()];

        list.toArray( dl );

        return dl;
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getBalance() {
        BigDecimal   retValue = Env.ZERO;
        StringBuffer sb       = new StringBuffer( " [" );

        // Total

        retValue = retValue.add( getAmount( Doc.AMTTYPE_Gross ));
        sb.append( getAmount( Doc.AMTTYPE_Gross ));

        // - Lines

        for( int i = 0;i < p_lines.length;i++ ) {
            BigDecimal lineBalance = (( DocLine_Bank )p_lines[ i ] ).getStmtAmt();

            retValue = retValue.subtract( lineBalance );
            sb.append( "-" ).append( lineBalance );
        }

        sb.append( "]" );

        //

        log.fine( toString() + " Balance=" + retValue + sb.toString());

        return retValue;
    }    // getBalance

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public Fact createFact( MAcctSchema as ) {

        // create Fact Header

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        // Header -- there may be different currency amounts

        FactLine fl = null;

        // Lines

        for( int i = 0;i < p_lines.length;i++ ) {
            DocLine_Bank line          = ( DocLine_Bank )p_lines[ i ];
            int          C_BPartner_ID = line.getC_BPartner_ID();

            // BankAsset       DR      CR  (Statement)

            fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_BankAsset,as ),line.getC_Currency_ID(),line.getStmtAmt());

            if( (fl != null) && (C_BPartner_ID != 0) ) {
                fl.setC_BPartner_ID( C_BPartner_ID );
            }

            // BankInTransit   DR      CR              (Payment)
            /*
             * Utilizo para contabilizar la cuenta asociada al Payment
             * Ya sea la cuenta por defecto o la cuenta de configuracion contable
             */
            MPayment payment = new MPayment(getCtx(), line.getC_Payment_ID(), getTrxName());
            if(payment != null && payment.getACCOUNTING_C_Charge_ID() > 0) {
            	fl = fact.createLine( line, MCharge.getAccount(payment.getACCOUNTING_C_Charge_ID(), as, payment.isReceipt() ? new BigDecimal(-1) : null),line.getC_Currency_ID(),line.getTrxAmt().negate());
            } else {
            	fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_BankInTransit,as ),line.getC_Currency_ID(),line.getTrxAmt().negate());
            }
                      
            if( fl != null ) {
                if( C_BPartner_ID != 0 ) {
                    fl.setC_BPartner_ID( C_BPartner_ID );
                }

                fl.setAD_Org_ID( line.getAD_Org_ID( true ));
            }

            // Charge          DR          (Charge)

            fl = fact.createLine( line,line.getChargeAccount( as,line.getChargeAmt().negate()),line.getC_Currency_ID(),line.getChargeAmt().negate(),null );

            if( (fl != null) && (C_BPartner_ID != 0) ) {
                fl.setC_BPartner_ID( C_BPartner_ID );
            }

            // Interest        DR      CR  (Interest)

            if( line.getInterestAmt().signum() < 0 ) {
                fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_InterestExp,as ),getAccount( Doc.ACCTTYPE_InterestExp,as ),line.getC_Currency_ID(),line.getInterestAmt().negate());
            } else {
                fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_InterestRev,as ),getAccount( Doc.ACCTTYPE_InterestRev,as ),line.getC_Currency_ID(),line.getInterestAmt().negate());
            }

            if( (fl != null) && (C_BPartner_ID != 0) ) {
                fl.setC_BPartner_ID( C_BPartner_ID );
            }

            //
            // fact.createTaxCorrection();

        }

        return fact;
    }    // createFact

	@Override
	public String applyCustomSettings( Fact fact, int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
}    // Doc_Bank



/*
 *  @(#)Doc_Bank.java   24.03.06
 * 
 *  Fin del fichero Doc_Bank.java
 *  
 *  Versión 2.2
 *
 */
