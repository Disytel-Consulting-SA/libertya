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

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MTax;
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

public class Doc_Invoice extends Doc implements DocProjectSplitterInterface {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_Invoice( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }

    /** Descripción de Campos */

    private DocTax[] m_taxes = null;

    /** Descripción de Campos */

    private int C_BPartner_Location_ID = 0;

    /** Descripción de Campos */

    private int m_precision = -1;

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
            p_vo.DateDoc           = rs.getTimestamp( "DateInvoiced" );
            p_vo.TaxIncluded       = rs.getString( "IsTaxIncluded" ).equals( "Y" );
            C_BPartner_Location_ID = rs.getInt( "C_BPartner_Location_ID" );

            // Amounts

            p_vo.Amounts[ Doc.AMTTYPE_Gross ] = rs.getBigDecimal( "GrandTotal" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Gross ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Gross ] = Env.ZERO;
            }

            p_vo.Amounts[ Doc.AMTTYPE_Net ] = rs.getBigDecimal( "TotalLines" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Net ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Net ] = Env.ZERO;
            }

            p_vo.Amounts[ Doc.AMTTYPE_Charge ] = rs.getBigDecimal( "ChargeAmt" );

            if( p_vo.Amounts[ Doc.AMTTYPE_Charge ] == null ) {
                p_vo.Amounts[ Doc.AMTTYPE_Charge ] = Env.ZERO;
            }

            // No DocType (e.g. voided) - assumes that document is priperly voided

            if( p_vo.C_DocType_ID == 0 ) {
                p_vo.C_DocType_ID = rs.getInt( "C_DocTypeTarget_ID" );
            }
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        loadDocumentType();    // lines require doc type

        // Contained Objects

        m_taxes = loadTaxes();
        p_lines = loadLines();

        if( (m_taxes == null) || (p_lines == null) ) {
            return false;
        }

        log.fine( "Lines=" + p_lines.length + ", Taxes=" + m_taxes.length );

        return true;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private DocTax[] loadTaxes() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT it.C_Tax_ID, t.Name, t.Rate, it.TaxBaseAmt, it.TaxAmt " + "FROM C_Tax t, C_InvoiceTax it " + "WHERE t.C_Tax_ID=it.C_Tax_ID AND it.C_Invoice_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int        C_Tax_ID   = rs.getInt( 1 );
                String     name       = rs.getString( 2 );
                BigDecimal rate       = rs.getBigDecimal( 3 );
                BigDecimal taxBaseAmt = rs.getBigDecimal( 4 );
                BigDecimal amount     = rs.getBigDecimal( 5 );

                //

                DocTax taxLine = new DocTax( C_Tax_ID,name,rate,taxBaseAmt,amount );

                log.fine( taxLine.toString());
                list.add( taxLine );
            }

            //

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadTaxes",e );

            return null;
        }

        // Return Array

        DocTax[] tl = new DocTax[ list.size()];

        list.toArray( tl );

        return tl;
    }    // loadTaxes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private DocLine[] loadLines() {
        ArrayList<DocLine_Invoice> list = new ArrayList<DocLine_Invoice>();
        String    sql  = "SELECT * FROM C_InvoiceLine WHERE C_Invoice_ID=? ORDER BY Line";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int             Line_ID = rs.getInt( "C_InvoiceLine_ID" );
                DocLine_Invoice docLine = new DocLine_Invoice( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );

                BigDecimal Qty = rs.getBigDecimal( "QtyInvoiced" );

                if( p_vo.DocumentType.equals( DOCTYPE_ARCredit ) || p_vo.DocumentType.equals( DOCTYPE_APCredit )) {
                    Qty = Qty.negate();
                }

                boolean isSOTrx = p_vo.DocumentType.equals( DOCTYPE_ARInvoice ) || p_vo.DocumentType.equals( DOCTYPE_ARCredit ) || p_vo.DocumentType.equals( DOCTYPE_ARProForma );

                docLine.setQty( Qty,isSOTrx );

                //

                BigDecimal LineNetAmt = rs.getBigDecimal( "LineNetAmt" );
                BigDecimal PriceList  = rs.getBigDecimal( "PriceList" );

                // Correct included Tax

                if( p_vo.TaxIncluded ) {
                    int C_Tax_ID = docLine.getC_Tax_ID();

                    MTax tax = C_Tax_ID > 0 ? MTax.get( getCtx(),C_Tax_ID,m_trxName ) : null;
                    
                    // Solo se recalculan importes si el impuesto existe y su tasa es mayor que cero.
                    if( tax != null && !tax.isZeroTax()) {

                        BigDecimal LineNetAmtTax = tax.calculateTax( LineNetAmt,true,getStdPercision());

                        log.fine( "LineNetAmt=" + LineNetAmt + " - Tax=" + LineNetAmtTax );
                        LineNetAmt = LineNetAmt.subtract( LineNetAmtTax );

                        for( int i = 0;i < m_taxes.length;i++ ) {
                            if( m_taxes[ i ].getC_Tax_ID() == C_Tax_ID ) {
                                m_taxes[ i ].addIncludedTax( LineNetAmtTax );

                                break;
                            }
                        }

                        BigDecimal PriceListTax = tax.calculateTax( PriceList,true,getStdPercision());

                        PriceList = PriceList.subtract( PriceListTax );
                    }

                }    // correct included Tax

                docLine.setAmount( LineNetAmt,PriceList,Qty );

                //

                log.fine( docLine.toString());
                list.add( docLine );
            }

            //

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadLines",e );

            return null;
        }

        // Convert to Array

        DocLine_Invoice[] dls = new DocLine_Invoice[ list.size()];

        list.toArray( dls );

        // Included Tax - make sure that no difference

        if( p_vo.TaxIncluded ) {
            for( int i = 0;i < m_taxes.length;i++ ) {
                if( m_taxes[ i ].isIncludedTaxDifference()) {
                    BigDecimal diff = m_taxes[ i ].getIncludedTaxDifference();

                    for( int j = 0;j < dls.length;j++ ) {
                        if( dls[ j ].getC_Tax_ID() == m_taxes[ i ].getC_Tax_ID()) {
                            dls[ j ].setLineNetAmtDifference( diff );

                            break;
                        }
                    }    // for all lines
                }        // tax difference
            }            // for all taxes
        }                // Included Tax difference

        // Return Array

        return dls;
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getStdPercision() {
        if( m_precision == -1 ) {
            m_precision = MCurrency.getStdPrecision( getCtx(),p_vo.C_Currency_ID );
        }

        return m_precision;
    }    // getPrecision

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

        // - Header Charge

        retValue = retValue.subtract( getAmount( Doc.AMTTYPE_Charge ));
        sb.append( "-" ).append( getAmount( Doc.AMTTYPE_Charge ));

        // - Tax

        for( int i = 0;i < m_taxes.length;i++ ) {
            retValue = retValue.subtract( m_taxes[ i ].getAmount());
            sb.append( "-" ).append( m_taxes[ i ].getAmount());
        }

        // - Lines

        for( int i = 0;i < p_lines.length;i++ ) {
            retValue = retValue.subtract( p_lines[ i ].getAmount());
            sb.append( "-" ).append( p_lines[ i ].getAmount());
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

        // Cash based accounting

        if( !as.isAccrual()) {
            p_vo.Error = "Not Accrual";
            log.log( Level.SEVERE,p_vo.Error );

            return null;
        }

        // ARI, ARF
        
        int accttypeIsChange = getIsExtrangeQuery();

        if( p_vo.DocumentType.equals( DOCTYPE_ARInvoice ) || p_vo.DocumentType.equals( DOCTYPE_ARProForma )) {

            // Receivables     DR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_C_Receivable,as ),p_vo.C_Currency_ID,getAmount( Doc.AMTTYPE_Gross ),null );

            // Header Charge           CR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_Charge,as ),p_vo.C_Currency_ID,null,getAmount( Doc.AMTTYPE_Charge ));

            // TaxDue                  CR

            for( int i = 0;i < m_taxes.length;i++ ) {
                FactLine tl = fact.createLine( null,m_taxes[ i ].getAccount( DocTax.ACCTTYPE_TaxDue,as ),p_vo.C_Currency_ID,null,m_taxes[ i ].getAmount());

                if( tl != null ) {
                    tl.setC_Tax_ID( m_taxes[ i ].getC_Tax_ID());
                }
            }

            // Revenue                 CR

            for( int i = 0;i < p_lines.length;i++ ) {
            	fact.createLine( p_lines[ i ],(( DocLine_Invoice )p_lines[ i ] ).getAccount( accttypeIsChange,as ),p_vo.C_Currency_ID,null,p_lines[ i ].getAmount());
            }

            // Set Locations

            FactLine[] fLines = fact.getLines();

            for( int i = 0;i < fLines.length;i++ ) {
                if( fLines[ i ] != null ) {
                    fLines[ i ].setLocationFromOrg( fLines[ i ].getAD_Org_ID(),true );    // from Loc
                    fLines[ i ].setLocationFromBPartner( C_BPartner_Location_ID,false );    // to Loc
                }
            }
        }

        // ARC

        else if( p_vo.DocumentType.equals( DOCTYPE_ARCredit )) {

            // Receivables             CR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_C_Receivable,as ),p_vo.C_Currency_ID,null,getAmount( Doc.AMTTYPE_Gross ));

            // Header Charge   DR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_Charge,as ),p_vo.C_Currency_ID,getAmount( Doc.AMTTYPE_Charge ),null );

            // TaxDue          DR

            for( int i = 0;i < m_taxes.length;i++ ) {
                FactLine tl = fact.createLine( null,m_taxes[ i ].getAccount( DocTax.ACCTTYPE_TaxDue,as ),p_vo.C_Currency_ID,m_taxes[ i ].getAmount(),null );

                if( tl != null ) {
                    tl.setC_Tax_ID( m_taxes[ i ].getC_Tax_ID());
                }
            }

            // Revenue         CR

            for( int i = 0;i < p_lines.length;i++ ) {
                fact.createLine( p_lines[ i ],(( DocLine_Invoice )p_lines[ i ] ).getAccount( accttypeIsChange,as ),p_vo.C_Currency_ID,p_lines[ i ].getAmount(),null );
            }

            // Set Locations

            FactLine[] fLines = fact.getLines();

            for( int i = 0;i < fLines.length;i++ ) {
                if( fLines[ i ] != null ) {
                    fLines[ i ].setLocationFromOrg( fLines[ i ].getAD_Org_ID(),true );    // from Loc
                    fLines[ i ].setLocationFromBPartner( C_BPartner_Location_ID,false );    // to Loc
                }
            }
        }

        // API

        else if( p_vo.DocumentType.equals( DOCTYPE_APInvoice )) {

            // Liability               CR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_V_Liability,as ),p_vo.C_Currency_ID,null,getAmount( Doc.AMTTYPE_Gross ));

            // Charge          DR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_Charge,as ),p_vo.C_Currency_ID,getAmount( Doc.AMTTYPE_Charge ),null );

            // TaxCredit       DR

            for( int i = 0;i < m_taxes.length;i++ ) {
                FactLine tl = fact.createLine( null,m_taxes[ i ].getAccount( DocTax.ACCTTYPE_TaxCredit,as ),p_vo.C_Currency_ID,m_taxes[ i ].getAmount(),null );

                if( tl != null ) {
                    tl.setC_Tax_ID( m_taxes[ i ].getC_Tax_ID());
                }
            }

            // Expense         DR

            for( int i = 0;i < p_lines.length;i++ ) {
                fact.createLine( p_lines[ i ],(( DocLine_Invoice )p_lines[ i ] ).getAccount( ProductInfo.ACCTTYPE_P_Expense,as ),p_vo.C_Currency_ID,p_lines[ i ].getAmount(),null );
            }

            // Set Locations

            FactLine[] fLines = fact.getLines();

            for( int i = 0;i < fLines.length;i++ ) {
                if( fLines[ i ] != null ) {
                    fLines[ i ].setLocationFromBPartner( C_BPartner_Location_ID,true );    // from Loc
                    fLines[ i ].setLocationFromOrg( fLines[ i ].getAD_Org_ID(),false );    // to Loc
                }
            }

            updateProductInfo( as.getC_AcctSchema_ID());    // only API
        }

        // APC

        else if( p_vo.DocumentType.equals( DOCTYPE_APCredit )) {

            // Liability       DR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_V_Liability,as ),p_vo.C_Currency_ID,getAmount( Doc.AMTTYPE_Gross ),null );

            // Charge                  CR

            fact.createLine( null,getAccount( Doc.ACCTTYPE_Charge,as ),p_vo.C_Currency_ID,null,getAmount( Doc.AMTTYPE_Charge ));

            // TaxCredit               CR

            for( int i = 0;i < m_taxes.length;i++ ) {
                FactLine tl = fact.createLine( null,m_taxes[ i ].getAccount( DocTax.ACCTTYPE_TaxCredit,as ),p_vo.C_Currency_ID,null,m_taxes[ i ].getAmount());

                if( tl != null ) {
                    tl.setC_Tax_ID( m_taxes[ i ].getC_Tax_ID());
                }
            }

            // Expense                 CR

            for( int i = 0;i < p_lines.length;i++ ) {
                fact.createLine( p_lines[ i ],(( DocLine_Invoice )p_lines[ i ] ).getAccount( ProductInfo.ACCTTYPE_P_Expense,as ),p_vo.C_Currency_ID,null,p_lines[ i ].getAmount());
            }

            // Set Locations

            FactLine[] fLines = fact.getLines();

            for( int i = 0;i < fLines.length;i++ ) {
                if( fLines[ i ] != null ) {
                    fLines[ i ].setLocationFromBPartner( C_BPartner_Location_ID,true );    // from Loc
                    fLines[ i ].setLocationFromOrg( fLines[ i ].getAD_Org_ID(),false );    // to Loc
                }
            }
        } else {
            p_vo.Error = "DocumentType unknown: " + p_vo.DocumentType;
            log.log( Level.SEVERE,p_vo.Error );
            fact = null;
        }

        return fact;
    }    // createFact

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     */

    private void updateProductInfo( int C_AcctSchema_ID ) {
        log.fine( "C_Invoice_ID=" + getRecord_ID());

        // update Product PO info
        // should only be once, but here for every AcctSchema
        // ignores multiple lines with same product - just uses first

        StringBuffer sql = new StringBuffer( "UPDATE M_Product_PO po " + "SET PriceLastInv = "

        // select

        + "(SELECT currencyConvert(il.PriceActual,i.C_Currency_ID,po.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID) " + "FROM C_Invoice i, C_InvoiceLine il " + 
        "WHERE i.C_Invoice_ID=il.C_Invoice_ID" +
        " AND po.M_Product_ID=il.M_Product_ID AND po.C_BPartner_ID=i.C_BPartner_ID" + 
        " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append( " LIMIT 1 )" )

        // update

        .append( "WHERE EXISTS (SELECT * " + "FROM C_Invoice i, C_InvoiceLine il " + "WHERE i.C_Invoice_ID=il.C_Invoice_ID" + " AND po.M_Product_ID=il.M_Product_ID AND po.C_BPartner_ID=i.C_BPartner_ID" + " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append( ")" );
        int no = DB.executeUpdate( sql.toString(),getTrxName());
        //JOptionPane.showMessageDialog( null,"En Doc_Invoice.java, con sql= "+sql.toString(),"..Fin", JOptionPane.INFORMATION_MESSAGE );
        log.fine("M_Product_Po-La original= "+ sql.toString());
        log.fine( "M_Product_PO - Updated=" + no );

        // update Product Costing
        // requires existence of currency conversion !!
        // if there are multiple lines of the same product last price uses first
        // -> TotalInvAmt is sometimes NULL !! -> error

        sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET PriceLastInv = "

        // select

        + "(SELECT currencyConvert(il.PriceActual,i.C_Currency_ID,a.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID) " + "FROM C_Invoice i, C_InvoiceLine il, C_AcctSchema a " + "WHERE i.C_Invoice_ID=il.C_Invoice_ID" + " AND pc.M_Product_ID=il.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID  AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append( " limit 1) " )

        // update

        .append( "WHERE EXISTS (SELECT * " + "FROM C_Invoice i, C_InvoiceLine il, C_AcctSchema a " + "WHERE i.C_Invoice_ID=il.C_Invoice_ID" + " AND pc.M_Product_ID=il.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + " AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append( ")" );
        no = DB.executeUpdate( sql.toString(),getTrxName());
        

        sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET TotalInvAmt = "

                // select		
		+ "(SELECT  currencyConvert(il.LineNetAmt,i.C_Currency_ID,a.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID) " + "FROM C_Invoice i, C_InvoiceLine il, C_AcctSchema a " + "WHERE i.C_Invoice_ID=il.C_Invoice_ID" + " AND pc.M_Product_ID=il.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append( " limit 1 ) " )
		
		// update
		
		.append( "WHERE EXISTS (SELECT * " + "FROM C_Invoice i, C_InvoiceLine il, C_AcctSchema a " + "WHERE i.C_Invoice_ID=il.C_Invoice_ID" + " AND pc.M_Product_ID=il.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + " AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append( ")" );
		no = DB.executeUpdate( sql.toString(),getTrxName());
		
		
		sql = new StringBuffer( "UPDATE M_Product_Costing pc " + "SET TotalInvQty = "
		
		// select
		
		+ "(SELECT il.QtyInvoiced FROM C_Invoice i, C_InvoiceLine il, C_AcctSchema a " + "WHERE i.C_Invoice_ID=il.C_Invoice_ID" + " AND pc.M_Product_ID=il.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID  AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append(  " limit 1 ) " )		
		// update
		
		.append( "WHERE EXISTS (SELECT * " + "FROM C_Invoice i, C_InvoiceLine il, C_AcctSchema a " + "WHERE i.C_Invoice_ID=il.C_Invoice_ID" + " AND pc.M_Product_ID=il.M_Product_ID AND pc.C_AcctSchema_ID=a.C_AcctSchema_ID" + " AND pc.C_AcctSchema_ID=" ).append( C_AcctSchema_ID ).append( " AND i.C_Invoice_ID=" ).append( getRecord_ID()).append( ")" );
        no = DB.executeUpdate( sql.toString(),getTrxName());

        
        
        
        log.fine( "M_Product_Costing - Updated=" + no );
    }    // updateProductInfo
    
    
    
    @Override
    public String applyCustomSettings( Fact fact, int index )
    {
    	DocProjectSplitter projectSplitter = new DocProjectSplitter(this);
    	if (!projectSplitter.splitLinesByProject(fact))
    		return STATUS_Error;
    	
    	// Realizar nuevamente el balanceo
    	doBalancing(index, projectSplitter.getLastProjectID());
    	
    	return STATUS_Posted;
    }
    

    /** Implementación de DocProjectSplitterInterface */
    public HashMap<Integer, BigDecimal> getProjectPercentageQuery(FactLine factLine)
    {
    	return calculateProjectPercentageQuery(factLine.getRecord_ID(), factLine.get_TrxName(), getSchemaCurrency(factLine));
    }
    
    
    /** Implementación de DocProjectSplitterInterface */
    public int getProjectsInLinesQuery(FactLine factLine)
    {
    	return DB.getSQLValue(factLine.get_TrxName(), "SELECT COUNT(DISTINCT(COALESCE(C_Project_ID,0))) FROM C_InvoiceLine WHERE C_Invoice_ID = ?", factLine.getRecord_ID());
    }

    
    /** Implementación de DocProjectSplitterInterface */
	public boolean requiresSplit(FactLine factLine) {
		// Deberá splitear si son lineas totalizadas o de iva
		return  (factLine.getLine_ID() == 0);
	}

	
	/**
	 * Para una factura dada, calcula los montos proporcionales segun los proyectos especificados en las lineas
	 * @param recordId C_Invoice_ID a procesar
	 * @param trxName nombre de la transaccion
	 * @return
	 */
	public static HashMap<Integer, BigDecimal> calculateProjectPercentageQuery(int recordId, String trxName, int currencyID)
	{
		// Valores a retornar
    	HashMap<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();
    	 
    	// utilizo "* pow" en lugar de "/" debido a las traducciones de DB
    	String sql = 	" SELECT 	il.c_project_id as Project, " +
    					"			sum( currencyConvert(il.linenetamount, i.C_Currency_ID, " + currencyID + ", i.dateAcct, null, i.AD_Client_ID, i.AD_Org_ID) * (1 + (t.rate + COALESCE((SELECT SUM (COALESCE(ita.rate,ta.rate)) FROM C_InvoiceTax it INNER JOIN C_Tax ta ON (ta.C_tax_ID = it.C_tax_ID) INNER JOIN C_InvoiceTax ita ON (ta.C_tax_ID = ita.C_tax_ID) AND (ita.C_Invoice_ID = il.C_Invoice_ID) WHERE it.C_Invoice_ID = il.C_Invoice_ID AND ta.ispercepcion = 'Y'),0)) / 100) / currencyConvert(i.grandtotal, i.C_Currency_ID, " + currencyID + ", i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID)) as Percent " +
    					" FROM C_InvoiceLine il INNER JOIN C_Invoice i ON il.c_invoice_id = i.c_invoice_id INNER JOIN C_Tax t ON il.C_Tax_ID = t.C_Tax_ID WHERE il.C_Invoice_ID = " + recordId + " GROUP BY il.c_project_id "; 

    	// Instanciar el CPreparedStatement con true en el ultimo parametro para evitar conversiones y poder utilizar el signo de division
    	PreparedStatement stmt = new CPreparedStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, trxName, true);
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
	
	/** Retorna la constante ACCTTYPE_P_RevenueExchange cuando el campo Canje de C_Invoice es true y ACCTTYPE_P_Revenue en caso contrario */
	public int getIsExtrangeQuery() {
		String sql = "SELECT IsExchange FROM C_Invoice WHERE C_Invoice_ID=?";

		try {
			PreparedStatement pstmt = DB.prepareStatement(sql, m_trxName);
			pstmt.setInt(1, getRecord_ID());
			ResultSet rs = pstmt.executeQuery();
			//
			if (rs.next()) {
				String isExchange = rs.getString("IsExchange");
				return (isExchange.equalsIgnoreCase("Y") ? ProductInfo.ACCTTYPE_P_RevenueExchange
						: ProductInfo.ACCTTYPE_P_Revenue);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "isExchange", e);
		}
		return 0;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
	
}    // Doc_Invoice



/*
 *  @(#)Doc_Invoice.java   24.03.06
 * 
 *  Fin del fichero Doc_Invoice.java
 *  
 *  Versión 2.2
 *
 */
