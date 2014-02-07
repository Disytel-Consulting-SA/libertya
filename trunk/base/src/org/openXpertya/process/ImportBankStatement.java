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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MBankAccount;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.X_I_BankStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportBankStatement extends SvrProcess {

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int m_C_BankAccount_ID = 0;

    /** Descripción de Campos */

    private boolean m_deleteOldImported = false;
    
    private boolean doImport = false;

    /** Descripción de Campos */

    private Properties m_ctx;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Client_ID" )) {
                m_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "AD_Org_ID" )) {
                m_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_BankAccount_ID" )) {
                m_C_BankAccount_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DeleteOldImported" )) {
                m_deleteOldImported = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "DoImport" )) {
            	doImport = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        m_ctx = Env.getCtx();
    }    // prepare

    /**
     * Importador de estados de cuenta
     * Campos usados para la importacion de extractos bancarios:
     *
     *   ISO_CODE = Asignar el codigo iso de la moneda de la linea del extracto. Si queda en null usará la moneda de la cuenta bancaria. 
     *   BankAccountNo = numero de cuenta del extracto. 
     *   TrxAmt  = importe de la transaccion
     *   ChargeAmt  = importe del cargo
     *   InterestAmt  = Importe del interés
     *   StmtAmt = Importe de la linea de estado de cuenta
     *
     * Se debe cumplir la funcion
     *      TrxAmt + ChargeAmt + InterestAmt =StmtAmt
     * El importador crea un extracto por fecha de operación, por lo que si el extracto tiene los minutos y los segundos en la fecha,
     * el importador creará varios extractos bancarios en vez de uno por dia por cuenta. 
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
    	String       clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;
    	String		 clientCheckToCharge = " AND I_BankStatement.AD_Client_ID=" + m_AD_Client_ID;
    	prepareImportation(clientCheck, clientCheckToCharge);
    	String res = "";
    	if(doImport){
    		res = importStatements(clientCheck);
    	}
    	return res;
    }
    	
    protected String prepareImportation(String clientCheck, String clientCheckToCharge) throws java.lang.Exception {
        log.info( "LoadBankStatement.doIt" );

        StringBuffer sql         = null;
        int          no          = 0;
        

        // ****    Prepare ****

        // Delete Old Imported

        if( m_deleteOldImported ) {
            sql = new StringBuffer( "DELETE I_BankStatement " + "WHERE I_IsImported='Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString());
            log.fine( "doIt - Delete Old Impored =" + no );
        }

        // Set Client, Org, IsActive, Created/Updated

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET AD_Client_ID = COALESCE (AD_Client_ID," ).append( m_AD_Client_ID ).append( ")," + " AD_Org_ID = COALESCE (AD_Org_ID," ).append( m_AD_Org_ID ).append( ")," );
        sql.append( " IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = NULL," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString());
        log.info( "doIt - Reset=" + no );
        sql = new StringBuffer( "UPDATE I_BankStatement b " 
        		+ " SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Org, ' " 
        		+ "WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0" 
        		+ " OR EXISTS (SELECT * FROM AD_Org oo WHERE oo.AD_Org_ID=b.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))" 
        		+ " AND I_IsImported<>'Y'" ).append( clientCheck );
        
        
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Org=" + no );
        }

        // Set Bank Account

        sql = new StringBuffer( "UPDATE I_BankStatement i " 
        		+ "SET C_BankAccount_ID=" 
        		+ "( " + " SELECT C_BankAccount_ID " 
        		+ " FROM C_BankAccount a, C_Bank b " 
        		+ " WHERE b.IsOwnBank='Y' " 
        		+ " AND a.AD_Client_ID=i.AD_Client_ID " 
        		+ " AND a.C_Bank_ID=b.C_Bank_ID " 
        		+ " AND a.AccountNo=i.BankAccountNo " 
        		+ " AND b.RoutingNo=i.RoutingNo " 
        		+ " OR b.SwiftCode=i.RoutingNo )" 
        		+ " WHERE i.C_BankAccount_ID IS NULL "
        		+ " AND i.I_IsImported<>'Y' " 
        		+ " OR i.I_IsImported IS NULL" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Bank Account (With Routing No)=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_BankStatement i " 
        		+ "SET C_BankAccount_ID=" + "( " + " SELECT C_BankAccount_ID " 
        		+ " FROM C_BankAccount a, C_Bank b " + " WHERE b.IsOwnBank='Y' " 
        		+ " AND a.C_Bank_ID=b.C_Bank_ID " + " AND a.AccountNo=i.BankAccountNo " 
        		+ " AND a.AD_Client_ID=i.AD_Client_ID  ) " 
        		+ "WHERE i.C_BankAccount_ID IS NULL " 
        		+ "AND i.I_isImported<>'Y' "
        		+ "OR i.I_isImported IS NULL" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Bank Account (Without Routing No)=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_BankStatement i " 
        		+ "SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount a WHERE a.C_BankAccount_ID=" ).append( m_C_BankAccount_ID );
        sql.append( " and a.AD_Client_ID=i.AD_Client_ID) " 
        		+ "WHERE i.C_BankAccount_ID IS NULL " 
        		+ "AND i.BankAccountNo IS NULL " 
        		+ "AND i.I_isImported<>'Y' " 
        		+ "OR i.I_isImported IS NULL" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Bank Account=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_BankStatement " 
        		+ "SET I_isImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Bank Account, ' " 
        		+ "WHERE C_BankAccount_ID IS NULL " 
        		+ "AND I_isImported<>'Y' " 
        		+ "OR I_isImported IS NULL" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Bank Account=" + no );
        }

        // Set Currency

        sql = new StringBuffer( "UPDATE I_BankStatement i " 
        		+ "SET C_Currency_ID=(SELECT C_Currency_ID FROM C_Currency c" 
        		+ " WHERE i.ISO_Code=c.ISO_Code AND c.AD_Client_ID IN (0,i.AD_Client_ID)) " 
        		+ "WHERE C_Currency_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt- Set Currency=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_BankStatement i " 
        		+ "SET C_Currency_ID=(SELECT C_Currency_ID FROM C_BankAccount WHERE C_BankAccount_ID=i.C_BankAccount_ID) " 
        		+ "WHERE i.C_Currency_ID IS NULL " 
        		+ "AND i.ISO_Code IS NULL" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Set Currency=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_BankStatement " 
        		+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Currency,' " 
        		+ "WHERE C_Currency_ID IS NULL " + "AND I_IsImported<>'E' " 
        		+ " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.warning( "doIt - Invalid Currency=" + no );
        }

        // Set Amount

        sql = new StringBuffer( "UPDATE I_BankStatement " 
        		+ "SET ChargeAmt=0 " 
        		+ "WHERE ChargeAmt IS NULL " 
        		+ "AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Charge Amount=" + no );
        }
        
        // Set c_charge_id
        
        sql = new StringBuffer( "UPDATE I_BankStatement " 
        		+ " SET C_Charge_ID=C_Charge.C_Charge_ID" 
        		+ " FROM C_Charge"
        		+ " WHERE I_BankStatement.ChargeValue IS NOT NULL"
        		+ " AND I_BankStatement.ChargeValue=C_Charge.name"
        		+ " AND I_BankStatement.I_IsImported<>'Y'" ).append( clientCheckToCharge );

        
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Charge ID =" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_BankStatement " 
        		+ "SET InterestAmt=0 " 
        		+ "WHERE InterestAmt IS NULL " 
        		+ "AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Interest Amount=" + no );
        }
		
        // Set StmtAmt

        sql = new StringBuffer( "UPDATE I_BankStatement " 
        		+ "SET StmtAmt=haber-debe " 
        		+ "WHERE StmtAmt IS NULL OR StmtAmt = 0" 
        		+ "AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Stmt Amount=" + no );
        }
        
        //

        sql = new StringBuffer( "UPDATE I_BankStatement " 
        		+ "SET TrxAmt=StmtAmt - InterestAmt - ChargeAmt " 
        		+ "WHERE TrxAmt IS NULL OR TrxAmt = 0 " + "AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Transaction Amount=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET I_isImported='E', I_ErrorMsg=I_ErrorMsg||'Err=Invalid Amount, ' " + "WHERE TrxAmt + ChargeAmt + InterestAmt <> StmtAmt " + "AND I_isImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Invaid Amount=" + no );
        }

        // Set Valuta Date

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET ValutaDate=StatementLineDate " + "WHERE ValutaDate IS NULL " + "AND I_isImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Valuta Date=" + no );
        }

        // Check Payment<->Invoice combination

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'Err=Invalid Payment<->Invoive, ' " + "WHERE I_BankStatement_ID IN " + "( " + " SELECT I_BankStatement_ID " + " FROM I_BankStatement i, C_Payment p " + " WHERE i.C_Invoice_ID IS NOT NULL " + " AND i.C_Payment_ID IS NOT NULL " + " AND p.C_Invoice_ID IS NOT NULL " + " AND p.C_Invoice_ID<>i.C_Invoice_ID " + " GROUP BY I_BankStatement_ID " + ")" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Payment<->Invoice Mismatch=" + no );
        }

        // Check Payment<->BPartner combination

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'Err=Invalid Payment<->BPartner, ' " + "WHERE I_BankStatement_ID IN " + "( " + " SELECT I_BankStatement_ID " + " FROM I_BankStatement i, C_Payment p " + " WHERE i.C_Payment_ID IS NOT NULL " + " AND i.C_BPartner_ID IS NOT NULL " + " AND p.C_BPartner_ID IS NOT NULL " + " AND p.C_BPartner_ID<>i.C_BPartner_ID " + " GROUP BY I_BankStatement_ID " + ")" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Payment<->BPartner Mismatch=" + no );
        }

        // Check Invoice<->BPartner combination

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'Err=Invalid Invoice<->BPartner, ' " + "WHERE I_BankStatement_ID IN " + "( " + " SELECT I_BankStatement_ID " + " FROM I_BankStatement i, C_Invoice v " + " WHERE i.C_BPartner_ID IS NOT NULL " + " AND i.C_Invoice_ID IS NOT NULL " + " AND v.C_BPartner_ID IS NOT NULL " + " AND v.C_BPartner_ID<>i.C_BPartner_ID " + " GROUP BY I_BankStatement_ID " + ")" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Invoice<->BPartner Mismatch=" + no );
        }

        // Check Invoice.BPartner<->Payment.BPartner combination

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'Err=Invalid Invoice.BPartner<->Payment.BPartner, ' " + "WHERE I_BankStatement_ID IN " + "( " + " SELECT I_BankStatement_ID " + " FROM I_BankStatement i, C_Invoice v, C_Payment p " + " WHERE p.C_Invoice_ID<>v.C_Invoice_ID" + " AND i.C_Invoice_ID IS NOT NULL " + " AND i.C_Payment_ID IS NOT NULL " + " AND v.C_BPartner_ID IS NOT NULL " + " AND p.C_BPartner_ID IS NOT NULL " + " AND v.C_BPartner_ID<>p.C_BPartner_ID " + " GROUP BY I_BankStatement_ID " + ")" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString());

        if( no != 0 ) {
            log.info( "doIt - Invoice.BPartner<->Payment.BPartner Mismatch=" + no );
        }

        // Detect Duplicates

        sql = new StringBuffer( "SELECT i.I_BankStatement_ID, l.C_BankStatementLine_ID, i.EftTrxID " + "FROM I_BankStatement i, C_BankStatement s, C_BankStatementLine l " + "WHERE i.I_isImported='N' " + "AND s.C_BankStatement_ID=l.C_BankStatement_ID " + "AND i.EftTrxID IS NOT NULL AND "

        // Concatinate EFT Info

        + "(l.EftTrxID||l.EftAmt||l.EftStatementLineDate||l.EftValutaDate||l.EftTrxType||l.EftCurrency||l.EftReference||s.EftStatementReference " + "||l.EftCheckNo||l.EftMemo||l.EftPayee||l.EftPayeeAccount) " + "= " + "(i.EftTrxID||i.EftAmt||i.EftStatementLineDate||i.EftValutaDate||i.EftTrxType||i.EftCurrency||i.EftReference||i.EftStatementReference " + "||i.EftCheckNo||i.EftMemo||i.EftPayee||i.EftPayeeAccount) " );

        StringBuffer updateSql = new StringBuffer( "UPDATE I_Bankstatement " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'Err=Duplicate['||?||']' " + "WHERE I_BankStatement_ID=?" ).append( clientCheck );
        PreparedStatement pupdt           = DB.prepareStatement( updateSql.toString());
        PreparedStatement pstmtDuplicates = null;

        no = 0;

        try {
            pstmtDuplicates = DB.prepareStatement( sql.toString());

            ResultSet rs = pstmtDuplicates.executeQuery();

            while( rs.next()) {
                String info = "Line_ID=" + rs.getInt( 2 )    // l.C_BankStatementLine_ID
                              + ",EDTTrxID=" + rs.getString( 3 );    // i.EftTrxID

                pupdt.setString( 1,info );
                pupdt.setInt( 2,rs.getInt( 1 ));                     // i.I_BankStatement_ID
                pupdt.executeUpdate();
                no++;
            }

            rs.close();
            pstmtDuplicates.close();
            pupdt.close();
            rs              = null;
            pstmtDuplicates = null;
            pupdt           = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - DetectDuplicates " + e.getMessage());
        }

        if( no != 0 ) {
            log.info( "doIt - Duplicates=" + no );
        }
        return "";
        // Import Bank Statement
    }
    
    
    
    
    private String importStatements(String clientCheck)
    {
        StringBuffer sql = new StringBuffer( "SELECT * FROM I_BankStatement" 
        		+ " WHERE I_IsImported='N'" 
        		+ " ORDER BY C_BankAccount_ID, Name, EftStatementDate, EftStatementReference" );

        MBankStatement    statement    = null;
        MBankAccount      account      = null;
        PreparedStatement pstmt        = null;
        int               lineNo       = 10;
        int               noInsert     = 0;
        int               noInsertLine = 0;

        try {
            pstmt = DB.prepareStatement( sql.toString());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                X_I_BankStatement imp = new X_I_BankStatement( m_ctx,rs,null );

                // Get the bank account for the first statement

                if( account == null ) {
                    account = new MBankAccount( m_ctx,imp.getC_BankAccount_ID(),null );
                    statement = null;
                    log.info( "doIt - New Statement, Account=" + account.getAccountNo());
                }

                // Create a new Bank Statement for every account

                else if( account.getC_BankAccount_ID() != imp.getC_BankAccount_ID()) {
                    account = new MBankAccount( m_ctx,imp.getC_BankAccount_ID(),null );
                    statement = null;
                    log.info( "doIt - New Statement, Account=" + account.getAccountNo());
                }

                // Create a new Bank Statement for every statement name

                else if(( statement.getName() != null ) && ( imp.getName() != null )) {
                    if( !statement.getName().equals( imp.getName())) {
                        statement = null;
                        log.info( "doIt - New Statement, Statement Name=" + imp.getName());
                    }
                }

                // Create a new Bank Statement for every statement reference

                else if(( statement.getEftStatementReference() != null ) && ( imp.getEftStatementReference() != null )) {
                    if( !statement.getEftStatementReference().equals( imp.getEftStatementReference())) {
                        statement = null;
                        log.info( "doIt - New Statement, Statement Reference=" + imp.getEftStatementReference());
                    }
                }

                // Create a new Bank Statement for every statement date

                else if(( statement.getStatementDate() != null ) && ( imp.getStatementDate() != null )) {
                    if( !statement.getStatementDate().equals( imp.getStatementDate())) {
                        statement = null;
                        log.info( "doIt - New Statement, Statement Date=" + imp.getStatementDate());
                    }
                }

                // New Statement

                if( statement == null ) {
                    statement = new MBankStatement( account );
                    statement.setEndingBalance( Env.ZERO );

                    // Copy statement data

                    if( imp.getName() != null ) {
                        statement.setName( imp.getName());
                    }

                    if( imp.getStatementDate() != null ) {
                        statement.setStatementDate( imp.getStatementDate());
                    }

                    statement.setDescription( imp.getDescription());
                    statement.setEftStatementReference( imp.getEftStatementReference());
                    statement.setEftStatementDate( imp.getEftStatementDate());

                    if( statement.save()) {
                        noInsert++;
                    }

                    lineNo = 10;
                }

                // New StatementLine

                MBankStatementLine line = new MBankStatementLine( statement,lineNo );

                // Copy statement line data
                // line.setC_BPartner_ID(imp.getC_BPartner_ID());
                // line.setC_Invoice_ID(imp.getC_Invoice_ID());

                if (assignImpToLine(statement, line, imp)) {
                    noInsertLine++;
                    lineNo += 10;
                }

                line = null;
            }

            // Close database connection

            rs.close();
            pstmt.close();
            rs    = null;
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - CreateBankStatement",e );
        }

        // Set Error to indicator to not imported

        sql = new StringBuffer( "UPDATE I_BankStatement " + "SET I_IsImported='N', Updated=SysDate " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
        int no = DB.executeUpdate( sql.toString());
        addLog( 0,null,new BigDecimal( no ),"@Errors@" );

        //

        addLog( 0,null,new BigDecimal( noInsert ),"@C_BankStatement_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noInsertLine ),"@C_BankStatementLine_ID@: @Inserted@" );

        return "";
    }    // doIt
    
    
    
    
    public static boolean assignImpToLine(MBankStatement statement, MBankStatementLine line, X_I_BankStatement imp) throws Exception {
    	line.setReferenceNo( imp.getReferenceNo());
        line.setDescription( imp.getLineDescription());
        line.setStatementLineDate( imp.getStatementLineDate());
        line.setDateAcct( imp.getStatementLineDate());
		line.setValutaDate(imp.getValutaDate());
        line.setIsReversal( imp.isReversal());
		line.setC_Currency_ID(imp.getC_Currency_ID());
        line.setTrxAmt( imp.getTrxAmt());
        line.setStmtAmt( imp.getStmtAmt());

        if( imp.getC_Charge_ID() != 0 ) {
            line.setC_Charge_ID( imp.getC_Charge_ID());
        }

        line.setInterestAmt( imp.getInterestAmt());
        line.setChargeAmt( imp.getChargeAmt());
        line.setMemo( imp.getMemo());

        if( imp.getC_Payment_ID() != 0 ) {
            line.setC_Payment_ID( imp.getC_Payment_ID());
        }

        // Copy statement line reference data

        line.setEftTrxID( imp.getEftTrxID());
        line.setEftTrxType( imp.getEftTrxType());
        line.setEftCheckNo( imp.getEftCheckNo());
        line.setEftReference( imp.getEftReference());
        line.setEftMemo( imp.getEftMemo());
        line.setEftPayee( imp.getEftPayee());
        line.setEftPayeeAccount( imp.getEftPayeeAccount());
        line.setEftStatementLineDate( imp.getEftStatementLineDate());
        line.setEftValutaDate( imp.getEftValutaDate());
        line.setEftCurrency( imp.getEftCurrency());
        line.setEftAmt( imp.getEftAmt());

        // Save statement line

        boolean ret = false;
        
        if( line.save()) {
            imp.setC_BankStatement_ID( statement.getC_BankStatement_ID());
            imp.setC_BankStatementLine_ID( line.getC_BankStatementLine_ID());
            imp.setI_IsImported( true );
            imp.setProcessed( true );
            imp.save();
            ret = true;
        }
        
        return ret;
    }
}    // ImportBankStatement



/*
 *  @(#)ImportBankStatement.java   02.07.07
 * 
 *  Fin del fichero ImportBankStatement.java
 *  
 *  Versión 2.2
 *
 */
