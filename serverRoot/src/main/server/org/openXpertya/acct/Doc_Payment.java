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
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MCharge;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPayment;
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

public class Doc_Payment extends Doc implements DocProjectSplitterInterface {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_Payment( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }

    /** Descripción de Campos */

    private String m_TenderType = null;

    /** Descripción de Campos */

    private boolean m_Prepayment = false;

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        try {
            p_vo.DateDoc = rs.getTimestamp( "DateTrx" );
            m_TenderType = rs.getString( "TenderType" );
            m_Prepayment = "Y".equals( rs.getString( "IsPrepayment" ));

            // Amount

            p_vo.Amounts[ Doc.AMTTYPE_Gross ] = rs.getBigDecimal( "PayAmt" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Gross ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Gross ] = Env.ZERO;
            }
            
            //Columna de cuenta contable auxiliar
            p_vo.Accounting_C_Charge_ID = rs.getInt("Accounting_C_Charge_ID");
            
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        return false;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getBalance() {
        BigDecimal retValue = Env.ZERO;

        // log.config( toString() + " Balance=" + retValue);

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

        // Cash Transfer

        if( "X".equals( m_TenderType )) {
            return fact;
        }

        // Publicar contabilidad en debe/haber dependiendo el signo del tipo de documento
        MDocType dt = MDocType.get(getCtx(), p_vo.C_DocType_ID, getTrxName());
                
        if( p_vo.DocumentType.equals( DOCTYPE_ARReceipt )) {
        	BigDecimal debitAmt = dt.getsigno_issotrx().equals(MDocType.SIGNO_ISSOTRX_1)?null:getAmount();
        	BigDecimal creditAmt = dt.getsigno_issotrx().equals(MDocType.SIGNO_ISSOTRX_1)?getAmount():null;
                	
        	/*
        	 * Si el pago tiene asociado una cuenta contable (cargo) uso esa,
        	 * si no, uso la cuenta por defecto.
        	 */
        	if(p_vo.Accounting_C_Charge_ID > 0) {
        		fact.createLine( null, MCharge.getAccount(p_vo.Accounting_C_Charge_ID, as, new BigDecimal(-1)),p_vo.C_Currency_ID,debitAmt,creditAmt);
        	} else {
        		fact.createLine( null,getAccount( Doc.ACCTTYPE_BankInTransit,as ),p_vo.C_Currency_ID,debitAmt,creditAmt);
        	}
        	
            MAccount acct = null;

            if( p_vo.C_Charge_ID != 0 ) {
                acct = MCharge.getAccount( p_vo.C_Charge_ID,as,getAmount().negate());
            } else if( m_Prepayment ) {
                acct = getAccount( Doc.ACCTTYPE_C_Prepayment,as );
            } else {
                acct = getAccount( Doc.ACCTTYPE_UnallocatedCash,as );
            }

            fact.createLine( null,acct,p_vo.C_Currency_ID,creditAmt, debitAmt);
        }

        // APP

        else if( p_vo.DocumentType.equals( DOCTYPE_APPayment )) {
            MAccount acct = null;
            BigDecimal debitAmt = dt.getsigno_issotrx().equals(MDocType.SIGNO_ISSOTRX_1)?getAmount():null;
        	BigDecimal creditAmt = dt.getsigno_issotrx().equals(MDocType.SIGNO_ISSOTRX_1)?null:getAmount();

            if( p_vo.C_Charge_ID != 0 ) {
                acct = MCharge.getAccount( p_vo.C_Charge_ID,as,getAmount());
            } else if( m_Prepayment ) {
                acct = getAccount( Doc.ACCTTYPE_V_Prepayment,as );
            } else {
                acct = getAccount( Doc.ACCTTYPE_PaymentSelect,as );
            }

            fact.createLine( null,acct,p_vo.C_Currency_ID,debitAmt,creditAmt);
            // 9/1/09 -> Antonio 
            // La siguiente linea habia sido comentada en la revision: 1342. 
            // Al parecer por error ya que con la linea comentada, el asiento no balancea
            
            /*
        	 * Si el pago tiene asociado una cuenta contable (cargo) uso esa,
        	 * si no, uso la cuenta por defecto.
        	 */
        	if(p_vo.Accounting_C_Charge_ID > 0) {
        		fact.createLine( null, MCharge.getAccount(p_vo.Accounting_C_Charge_ID, as, null),p_vo.C_Currency_ID,creditAmt, debitAmt);
        	} else {
        		fact.createLine( null,getAccount( Doc.ACCTTYPE_BankInTransit,as ),p_vo.C_Currency_ID,creditAmt, debitAmt);
        	}
        	
            
        } else {
            p_vo.Error = "DocumentType unknown: " + p_vo.DocumentType;
            log.log( Level.SEVERE,"createFact - " + p_vo.Error );
            fact = null;
        }

        return fact;
    }    // createFact

	@Override
	public String applyCustomSettings( Fact fact, int index ) {
    	DocProjectSplitter projectSplitter = new DocProjectSplitter(this);
    	if (!projectSplitter.splitLinesByProject(fact))
    		return STATUS_Error;
    	
    	// Realizar nuevamente el balanceo
    	doBalancing(index, projectSplitter.getLastProjectID());
    	
    	return STATUS_Posted;
	}

	@Override
	public HashMap<Integer, BigDecimal> getProjectPercentageQuery(FactLine factLine) 
	{
		// Valores a retornar
    	HashMap<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();

    	// Proporcional en proyectos de facturas donde el pago esta involucrado  
		String sql = 	
			" SELECT  Project, sum(al2.amount * percentage / currencyConvert(p.payamt, p.C_Currency_ID, "+getSchemaCurrency(factLine)+", p.DateAcct, null, p.AD_Client_ID, p.AD_Org_ID)) as Percent    " +
			" FROM C_AllocationLine al2, " +
			" 	   C_Payment p, " +
			" 	( " +
			" 		SELECT 	al.c_allocationline_id as alloc_line, " +
			"				al.amount as amount, " +
			"				il.c_project_id as Project, " +
			"				al.c_invoice_id, sum(currencyConvert(il.linenetamount, i.C_Currency_ID, "+getSchemaCurrency(factLine)+", i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID) * (1 + (t.rate + COALESCE((SELECT SUM (COALESCE(ita.rate,ta.rate)) FROM C_InvoiceTax it INNER JOIN C_Tax ta ON (ta.C_tax_ID = it.C_tax_ID) INNER JOIN C_InvoiceTax ita ON (ta.C_tax_ID = ita.C_tax_ID) AND (ita.C_Invoice_ID = il.C_Invoice_ID) WHERE it.C_Invoice_ID = il.C_Invoice_ID AND ta.ispercepcion = 'Y'),0)) / 100) / currencyConvert(i.grandtotal, i.C_Currency_ID, "+getSchemaCurrency(factLine)+", i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID))  as Percentage " +
			" 		FROM C_InvoiceLine il   " +
			" 		INNER JOIN C_Invoice i ON il.c_invoice_id = i.c_invoice_id " +  
			" 		INNER JOIN C_Tax t ON il.C_Tax_ID = t.C_Tax_ID  " +
			" 		INNER JOIN C_AllocationLine al on i.C_Invoice_ID = al.C_Invoice_ID " +
			" 		WHERE al.C_Payment_ID = " + factLine.getRecord_ID() +
			" 		GROUP BY al.c_invoice_id, il.c_project_id, al.c_allocationline_id, al.amount " +
			" 	) AS FOO " +
			" WHERE al2.C_AllocationLine_ID = alloc_line " +
			" AND al2.C_Payment_ID = p.C_Payment_ID " +
			" GROUP BY Project ";
		
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

	@Override
	public int getProjectsInLinesQuery(FactLine factLine) {
		// Cantidad de referencias del pago en allocations que realmente cancelan deudas 
		return DB.getSQLValue(factLine.get_TrxName(), " SELECT COUNT(DISTINCT(COALESCE(il.C_Project_ID,0))) FROM C_AllocationLine al INNER JOIN C_Invoice i ON al.C_Invoice_ID = i.C_Invoice_ID INNER JOIN C_InvoiceLine il ON i.C_Invoice_ID = il.C_Invoice_ID WHERE al.C_Invoice_ID IS NOT NULL AND al.C_Payment_ID = " + factLine.getRecord_ID());
	}

	@Override
	public boolean requiresSplit(FactLine factLine) {
		// Solo pagos allocados... ? (los adelantados no tienen proporcional y ya deberían llevar su proyecto asignado)
		return (0 < DB.getSQLValue(factLine.get_TrxName(), " SELECT COUNT(1) FROM C_AllocationLine WHERE C_Invoice_ID IS NOT NULL AND C_Payment_ID = " + factLine.getRecord_ID()));
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
}    // Doc_Payment



/*
 *  @(#)Doc_Payment.java   24.03.06
 * 
 *  Fin del fichero Doc_Payment.java
 *  
 *  Versión 2.2
 *
 */
