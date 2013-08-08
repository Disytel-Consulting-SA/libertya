package org.openXpertya.grid;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

public class CreateFromStatementModel extends CreateFromModel {

	// =============================================================================================
	// Logica en comun para la carga de cuentas
	// =============================================================================================

	/**
	 * Consulta para carga de cuentas
	 */
	public StringBuffer loadBankAccountQuery() {
        StringBuffer sql = new StringBuffer(); 
        sql.append("SELECT ")
           .append(   "p.DateTrx, ")
           .append(   "p.C_Payment_ID, ")
           .append(	  "p.DocumentNo, ")
           .append(   "p.C_Currency_ID, ")
           .append(   "c.ISO_Code, ")
           .append(   "p.PayAmt, ")
           .append(   "currencyConvert(p.PayAmt,p.C_Currency_ID,ba.C_Currency_ID,?,null,p.AD_Client_ID,p.AD_Org_ID) AS ConvertedAmt, ")
           .append(   "bp.Name AS BPartnerName ")
           
           .append("FROM C_BankAccount ba ")
           .append("INNER JOIN C_Payment_v p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) ")
           .append("INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) ")
           .append("INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) ")
        		   
           .append("WHERE p.Processed='Y' ")
           .append(  "AND p.IsReconciled='N' ")
           .append(  "AND p.DocStatus IN ('CO','CL','RE') ")
           .append(  "AND p.PayAmt<>0 ")
           .append(  "AND p.C_BankAccount_ID=? ")
           .append(  "AND NOT EXISTS ")
           .append(      "(SELECT * FROM C_BankStatementLine l ")
           // Voided Bank Statements have 0 StmtAmt
           .append(       "WHERE p.C_Payment_ID=l.C_Payment_ID AND l.StmtAmt <> 0)");
        return sql;
	}
	
	
	public void loadPayment(Payment payment, ResultSet rs) throws SQLException {
        payment.selected = false;
        payment.paymentID = rs.getInt("C_Payment_ID");
        payment.dateTrx = rs.getTimestamp("DateTrx");
        payment.documentNo = rs.getString("DocumentNo");
        payment.currencyID = rs.getInt("C_Currency_ID");
        payment.currencyISO = rs.getString("ISO_Code");
        payment.payAmt = rs.getBigDecimal("PayAmt");
        payment.convertedAmt = rs.getBigDecimal("ConvertedAmt");
        payment.bPartnerName = rs.getString("BPartnerName"); 
	}
	
	
    public void save(int C_BankStatement_ID, String trxName, List<? extends SourceEntity> selectedSourceEntities, CreateFromPluginInterface handler) throws CreateFromSaveException {
        log.config( "" );

        // fixed values
        MBankStatement bs = new MBankStatement( Env.getCtx(),C_BankStatement_ID, trxName);
        log.config( bs.toString());

        // Lines
        for (SourceEntity sourceEntity : selectedSourceEntities) {
        	Payment payment = (Payment)sourceEntity;	
            Timestamp trxDate = payment.dateTrx;
            int C_Payment_ID = payment.paymentID;
            int C_Currency_ID = payment.currencyID;
            BigDecimal TrxAmt  = payment.payAmt;
            BigDecimal StmtAmt = payment.convertedAmt;

            //

            log.fine( "Line Date=" + trxDate + ", Payment=" + C_Payment_ID + ", Currency=" + C_Currency_ID + ", Amt=" + TrxAmt );

            //
            MPayment pay = new MPayment( Env.getCtx(),C_Payment_ID, trxName);
            MBankStatementLine bsl = new MBankStatementLine( bs );

            bsl.setStatementLineDate( trxDate );
            bsl.setPayment(pay);
            
            handler.customMethod(pay, null);
            
            if( !bsl.save()) {
                throw new CreateFromSaveException(
             		   "@StatementLineSaveError@ (@C_Paymenty_ID@ # " + payment.documentNo + "):<br>" + 
             		   CLogger.retrieveErrorAsString()
             	);

            }
        }        // for all rows
    }    // save

}
