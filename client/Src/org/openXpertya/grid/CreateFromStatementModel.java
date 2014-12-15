package org.openXpertya.grid;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
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
	
	
    public void save(int C_BankStatement_ID, String trxName, List<? extends SourceEntity> selectedSourceEntities, CreateFromPluginInterface handler, Integer nroLote) throws CreateFromSaveException {
        log.config( "" );

        // fixed values
        MBankStatement bs = new MBankStatement( Env.getCtx(),C_BankStatement_ID, trxName);
        log.config( bs.toString());
        
        selectedSourceEntities= ungroup(selectedSourceEntities,trxName,C_BankStatement_ID,nroLote,bs.getStatementDate(),bs.getC_BankAccount_ID());

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

	/**
	 * Consulta para carga de cuentas
	 */
	public StringBuffer loadBankAccountQueryWithFilter() {
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
           .append("INNER JOIN C_Payment p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) ")
           .append("INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) ")
           .append("INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) ")
        		   
           .append("WHERE p.Processed='Y' ")
           .append(  "AND p.IsReconciled='N' ")
           .append(  "AND p.DocStatus IN ('CO','CL','RE') ")
           .append(  "AND p.PayAmt<>0 ")
           .append(  "AND p.C_BankAccount_ID=? ")
           .append(  "AND p.couponbatchnumber=? ")
           .append(  "AND NOT EXISTS ")
           .append(      "(SELECT * FROM C_BankStatementLine l ")
           // Voided Bank Statements have 0 StmtAmt
           .append(       "WHERE p.C_Payment_ID=l.C_Payment_ID AND l.StmtAmt <> 0)");
        return sql;
	}
	
	/**
	 * Consulta para carga de cuentas
	 */
	public StringBuffer loadBankAccountGrouped() {
        StringBuffer sql = new StringBuffer(); 
        sql.append("SELECT ")
           .append(   "p.DateTrx, ")
           .append(   "NULL AS C_Payment_ID, ")
           .append(	  "NULL AS DocumentNo, ")
           .append(   "p.C_Currency_ID, ")
           .append(   "c.ISO_Code, ")
           .append(   "SUM(p.PayAmt) as PayAmt , ")
           .append(   "SUM(currencyConvert(p.PayAmt,p.C_Currency_ID,ba.C_Currency_ID,?,null,p.AD_Client_ID,p.AD_Org_ID)) AS ConvertedAmt, ")
           .append(   "bp.Name AS BPartnerName ")
           
           .append("FROM C_BankAccount ba ")
           .append("INNER JOIN C_Payment p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) ")
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
           .append(       "WHERE p.C_Payment_ID=l.C_Payment_ID AND l.StmtAmt <> 0) ")
           .append(  "GROUP BY DateTrx,p.C_Currency_ID,ISO_Code,BPartnerName ");
        return sql;
	}

	/**
	 * Consulta para carga de cuentas
	 */
	public StringBuffer loadBankAccountWithFilterGrouped() {
        StringBuffer sql = new StringBuffer(); 
        sql.append("SELECT ")
           .append(   "p.DateTrx, ")
           .append(   "NULL AS C_Payment_ID, ")
           .append(	  "NULL AS DocumentNo, ")
           .append(   "p.C_Currency_ID, ")
           .append(   "c.ISO_Code, ")
           .append(   "SUM(p.PayAmt) as PayAmt , ")
           .append(   "SUM(currencyConvert(p.PayAmt,p.C_Currency_ID,ba.C_Currency_ID,?,null,p.AD_Client_ID,p.AD_Org_ID)) AS ConvertedAmt, ")
           .append(   "bp.Name AS BPartnerName ")
           
           .append("FROM C_BankAccount ba ")
           .append("INNER JOIN C_Payment p ON (p.C_BankAccount_ID=ba.C_BankAccount_ID) ")
           .append("INNER JOIN C_Currency c ON (p.C_Currency_ID=c.C_Currency_ID) ")
           .append("INNER JOIN C_BPartner bp ON (p.C_BPartner_ID=bp.C_BPartner_ID) ")
        		   
           .append("WHERE p.Processed='Y' ")
           .append(  "AND p.IsReconciled='N' ")
           .append(  "AND p.DocStatus IN ('CO','CL','RE') ")
           .append(  "AND p.PayAmt<>0 ")
           .append(  "AND p.C_BankAccount_ID=? ")
           .append(  "AND p.couponbatchnumber=? ")
           .append(  "AND NOT EXISTS ")
           .append(      "(SELECT * FROM C_BankStatementLine l ")
           // Voided Bank Statements have 0 StmtAmt
           .append(       "WHERE p.C_Payment_ID=l.C_Payment_ID AND l.StmtAmt <> 0)")
           .append(  "GROUP BY DateTrx,p.C_Currency_ID,ISO_Code,BPartnerName ");
        return sql;
	}
	
    
	private List<? extends SourceEntity> ungroup(
			List<? extends SourceEntity> selectedSourceEntities, String trxName, int c_BankStatement_ID, Integer nroLote, Timestamp ts, int C_BanckAccount_ID)
			{

		List<SourceEntity> selectedSourceEntitiesReturn = new ArrayList<SourceEntity>();
		
		for (SourceEntity sourceEntity : selectedSourceEntities) {
			Payment payment = (Payment) sourceEntity;
			if (payment.paymentID == 0) {
				StringBuffer sql = null;
				if (nroLote == null)
					sql = loadBankAccountQuery();
				else
					sql = loadBankAccountQueryWithFilter();
				sql.append(" AND datetrx = '" + payment.dateTrx +"'");
				if (ts == null) {
					ts = new Timestamp(System.currentTimeMillis());
				}

				PreparedStatement pstmt = null;
				ResultSet rs = null;

				try {
					pstmt = DB.prepareStatement(sql.toString());
					pstmt.setTimestamp(1, ts);
					pstmt.setInt(2, C_BanckAccount_ID);
					if (nroLote != null)
						pstmt.setString(3, nroLote.toString());
					rs = pstmt.executeQuery();

					while (rs.next()) {
						Payment pay = new Payment();
						loadPayment(pay, rs);
						selectedSourceEntitiesReturn.add(pay);
					}

					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					log.log(Level.SEVERE, sql.toString(), e);
				} finally {
					try {
						if (rs != null)
							rs.close();
						if (pstmt != null)
							pstmt.close();
					} catch (Exception e) {
					}
				}
			}else
				selectedSourceEntitiesReturn.add(sourceEntity);
		}
		return selectedSourceEntitiesReturn;
	}

}
