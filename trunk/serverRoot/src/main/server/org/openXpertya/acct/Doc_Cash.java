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
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_Cash extends Doc implements DocProjectSplitterInterface   {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_Cash( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_Cash

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType = DOCTYPE_CashJournal;

        try {
            p_vo.DateDoc = rs.getTimestamp( "StatementDate" );

            // Amounts

            p_vo.Amounts[ Doc.AMTTYPE_Gross ] = rs.getBigDecimal( "StatementDifference" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Gross ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Gross ] = Env.ZERO;
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        // Set CashBook Org & Currency

        setCashBookInfo();
        loadDocumentType();    // lines require doc type

        // Contained Objects

        p_lines = loadLines();
        log.fine( "Lines=" + p_lines.length );

        return true;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     */

    private void setCashBookInfo() {
        int    retValue = 0;
        String sql      = "SELECT cb.C_CashBook_ID, cb.AD_Org_ID, cb.C_Currency_ID " + "FROM C_Cash c, C_CashBook cb " + "WHERE c.C_CashBook_ID=cb.C_CashBook_ID AND c.C_Cash_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                p_vo.C_CashBook_ID = rs.getInt( 1 );
                p_vo.AD_Org_ID     = rs.getInt( 2 );
                p_vo.C_Currency_ID = rs.getInt( 3 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"setCashBookInfo",e );
        }
    }    // setCashBookInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private DocLine[] loadLines() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_CashLine WHERE C_Cash_ID=? ORDER BY Line";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int          Line_ID = rs.getInt( "C_CashLine_ID" );
                DocLine_Cash docLine = new DocLine_Cash( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );
                docLine.setCashType( rs.getString( "CashType" ));
                docLine.setReference( rs.getInt( "C_BankAccount_ID" ),rs.getInt( "C_Invoice_ID" ));
                docLine.setAmount( rs.getBigDecimal( "Amount" ),rs.getBigDecimal( "DiscountAmt" ),rs.getBigDecimal( "WriteOffAmt" ));
                docLine.setTransferCash_ID(rs.getInt("TransferCash_ID"));
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
            retValue = retValue.subtract( p_lines[ i ].getAmount());
            sb.append( "-" ).append( p_lines[ i ].getAmount());
        }

        sb.append( "]" );

        //

        log.fine( toString() + " Balance=" + retValue + sb.toString());

        // return retValue;

        return Env.ZERO;    // Lines are balanced
    }                       // getBalance

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public Fact createFact( MAcctSchema as ) {

        // Need to have CashBook

        if( p_vo.C_CashBook_ID == 0 ) {
            p_vo.Error = "C_CashBook_ID not set";
            log.log( Level.SEVERE,"createFact - " + p_vo.Error );

            return null;
        }

        // create Fact Header

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        // Header posting amt as Invoices and Transfer could be differenet currency
        // CashAsset Total

        BigDecimal assetAmt = Env.ZERO;

        // Lines

        for( int i = 0;i < p_lines.length;i++ ) {
            DocLine_Cash line     = ( DocLine_Cash )p_lines[ i ];
            String       CashType = line.getCashType();

            if( CashType.equals( DocLine_Cash.CASHTYPE_EXPENSE )) {    // amount is negative

                // CashExpense     DR
                // CashAsset               CR

                fact.createLine( line,getAccount( Doc.ACCTTYPE_CashExpense,as ),p_vo.C_Currency_ID,line.getAmount().negate(),null );

                // fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
                // p_vo.C_Currency_ID, null, line.getAmount().negate());

                assetAmt = assetAmt.subtract( line.getAmount().negate());
            } else if( CashType.equals( DocLine_Cash.CASHTYPE_RECEIPT )) {    // amount is positive

                // CashAsset       DR
                // CashReceipt             CR
                // fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
                // p_vo.C_Currency_ID, line.getAmount(), null);

                assetAmt = assetAmt.add( line.getAmount());
                fact.createLine( line,getAccount( Doc.ACCTTYPE_CashReceipt,as ),p_vo.C_Currency_ID,null,line.getAmount());
            } else if( CashType.equals( DocLine_Cash.CASHTYPE_CHARGE )) {    // amount is negative

                // Charge          DR
                // CashAsset               CR

                fact.createLine( line,line.getChargeAccount( as,getAmount()),p_vo.C_Currency_ID,line.getAmount().negate(),null );

                // fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
                // p_vo.C_Currency_ID, null, line.getAmount().negate());

                assetAmt = assetAmt.subtract( line.getAmount().negate());
            } else if( CashType.equals( DocLine_Cash.CASHTYPE_DIFFERENCE )) {    // amount is pos/neg

                // CashDifference  DR
                // CashAsset               CR

                fact.createLine( line,getAccount( Doc.ACCTTYPE_CashDifference,as ),p_vo.C_Currency_ID,line.getAmount().negate());

                // fact.createLine(line, getAccount(Doc.ACCTTYPE_CashAsset, as),
                // p_vo.C_Currency_ID, line.getAmount());

                assetAmt = assetAmt.add( line.getAmount());
            } else if( CashType.equals( DocLine_Cash.CASHTYPE_INVOICE )) {    // amount is pos/neg

                // CashAsset       DR      dr      --   Invoice is in Invoice Currency !
                // CashTransfer    cr      CR

                if( line.getC_Currency_ID() == p_vo.C_Currency_ID ) {
                    assetAmt = assetAmt.add( line.getAmount());
                } else {
                    fact.createLine( line,getAccount( Doc.ACCTTYPE_CashAsset,as ),line.getC_Currency_ID(),line.getAmount());
                }

                fact.createLine( line,getAccount( Doc.ACCTTYPE_CashTransfer,as ),line.getC_Currency_ID(),line.getAmount().negate());
            } else if( CashType.equals( DocLine_Cash.CASHTYPE_TRANSFER )) {    // amount is pos/neg

                // BankInTransit   DR      dr      --  Transfer is in Bank Account Currency
                // CashAsset       dr      CR

                int temp = p_vo.C_BankAccount_ID;

                p_vo.C_BankAccount_ID = line.getC_BankAccount_ID();
                fact.createLine( line,getAccount( Doc.ACCTTYPE_BankInTransit,as ),line.getC_Currency_ID(),line.getAmount().negate());
                p_vo.C_BankAccount_ID = temp;

                if( line.getC_Currency_ID() == p_vo.C_Currency_ID ) {
                    assetAmt = assetAmt.add( line.getAmount());
                } else {
                    fact.createLine( line,getAccount( Doc.ACCTTYPE_CashAsset,as ),line.getC_Currency_ID(),line.getAmount());
                }
            } else if (CashType.equals(DocLine_Cash.CASHTYPE_CASH_TRANSFER)) {
            	
            	// Si el importe es positivo implica que es la línea entrante de la transferencia.
            	// La cuenta de Transferencia de Efectivo se toma del Libro de Caja Origen de la Trf.
            	MAccount acct = null;
            	if (line.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            		int temp = p_vo.C_CashBook_ID;
            		p_vo.C_CashBook_ID = line.getTransferCashBook_ID();
            		acct = getAccount(Doc.ACCTTYPE_CashTransfer, as);
            		p_vo.C_CashBook_ID = temp;
            	// Si es negativo, es la línea saliente.
            	} else {
            		acct = getAccount(Doc.ACCTTYPE_CashTransfer, as);
            	}
            	
            	fact.createLine( line, acct,line.getC_Currency_ID(),line.getAmount().negate());
            	
            	assetAmt = assetAmt.add( line.getAmount());
            }
        }    // lines

        // Cash Asset

        fact.createLine( null,getAccount( Doc.ACCTTYPE_CashAsset,as ),p_vo.C_Currency_ID,assetAmt );

        return fact;
    }    // createFact

	@Override
	public String applyCustomSettings(Fact fact) {
    	DocProjectSplitter projectSplitter = new DocProjectSplitter(this);
    	if (projectSplitter.splitLinesByProject(fact))
    		return STATUS_Posted;
    	else
    		return STATUS_Error;
	}

	
	/** Implementación de DocProjectSplitterInterface */
	public HashMap<Integer, BigDecimal> getProjectPercentageQuery(FactLine factLine) 
	{
		// Valores a retornar
    	HashMap<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();

    	// Calcular el total (no toma en cuenta el discount amount, ya que éste valor es restado al amount al momento de indicarlo).
    	BigDecimal totalAmt = DB.getSQLValueBD(factLine.get_TrxName(), 	" SELECT sum(abs(currencyConvert(cl.amount, cl.C_Currency_ID, "+getSchemaCurrency(factLine)+", c.dateAcct, null, c.AD_Client_ID, c.AD_Org_ID))) " +
    																	" FROM C_CashLine cl INNER JOIN C_Cash c ON cl.C_Cash_ID = c.C_Cash_ID WHERE cl.C_Cash_ID = ?", factLine.getRecord_ID());
    	
    	// proporcionales por proyecto
    	String sql = "SELECT cl.c_project_id as Project, sum(abs(currencyConvert(cl.amount, cl.C_Currency_ID, "+getSchemaCurrency(factLine)+", c.dateAcct, null, c.AD_Client_ID, c.AD_Org_ID)) / " + totalAmt + ") as Percent FROM C_CashLine cl INNER JOIN C_Cash c ON cl.c_cash_id = c.c_cash_id WHERE cl.C_cash_ID = " + factLine.getRecord_ID() + " GROUP BY cl.c_project_id "; 

    	// Instanciar el CPreparedStatement con true en el ultimo parametro para evitar conversiones y poder utilizar el signo de division
    	PreparedStatement stmt = new CPreparedStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, factLine.get_TrxName(), true);
    	ResultSet rs = null;
    	try 
    	{
    		rs = stmt.executeQuery();
    		while (rs.next())
    			map.put(rs.getInt("Project"), rs.getBigDecimal("Percent"));
			rs.close();
			stmt.close();
			stmt = null;
    	}
    	catch (Exception e)	{
    		return null;
    	}
    	
    	return map;
	}

	
	/** Implementación de DocProjectSplitterInterface */
	public int getProjectsInLinesQuery(FactLine factLine) {
		return DB.getSQLValue(factLine.get_TrxName(), "SELECT COUNT(DISTINCT(COALESCE(C_Project_ID,0))) FROM C_CashLine WHERE C_Cash_ID = ?", factLine.getRecord_ID());
	}

	
	/** Implementación de DocProjectSplitterInterface */
	public boolean requiresSplit(FactLine factLine) {
		// Las líneas que no tienen Line_ID son las totalizadas a splitear
		return  (factLine.getLine_ID() == 0);
	}
}    // Doc_Cash



/*
 *  @(#)Doc_Cash.java   24.03.06
 * 
 *  Fin del fichero Doc_Cash.java
 *  
 *  Versión 2.2
 *
 */
