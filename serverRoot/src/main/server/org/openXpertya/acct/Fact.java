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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MDistribution;
import org.openXpertya.model.MDistributionLine;
import org.openXpertya.model.MElementValue;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class Fact {

    /**
     * Constructor de la clase ...
     *
     *
     * @param document
     * @param acctSchema
     * @param defaultPostingType
     */

    public Fact( Doc document,MAcctSchema acctSchema,String defaultPostingType ) {
        m_doc         = document;
        m_acctSchema  = acctSchema;
        m_postingType = defaultPostingType;
        m_docVO       = m_doc.p_vo;

        //

        log.config( toString());
    }    // Fact

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Doc m_doc = null;

    /** Descripción de Campos */

    private MAcctSchema m_acctSchema = null;

    /** Descripción de Campos */

    private String m_trxName;

    /** Descripción de Campos */

    private String m_postingType = null;

    /** Descripción de Campos */

    public static final String POST_Actual = "A";

    /** Descripción de Campos */

    public static final String POST_Budget = "B";

    /** Descripción de Campos */

    public static final String POST_Commitment = "C";

    /** Descripción de Campos */

    private DocVO m_docVO = null;

    /** Descripción de Campos */

    private boolean m_converted = false;

    /** Descripción de Campos */

    private ArrayList m_lines = new ArrayList();

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_lines.clear();
        m_lines = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param docLine
     * @param account
     * @param C_Currency_ID
     * @param debitAmt
     * @param creditAmt
     *
     * @return
     */

    public FactLine createLine( DocLine docLine,MAccount account,int C_Currency_ID,BigDecimal debitAmt,BigDecimal creditAmt ) {

        // log.fine("createLine - " + account      + " - Dr=" + debitAmt + ", Cr=" + creditAmt);

        // Data Check

        if( account == null ) {
            log.info( "No account for " + docLine + ": Amt=" + debitAmt + "/" + creditAmt + " - " + toString());

            return null;
        }

        //

        FactLine line = new FactLine( m_doc.getCtx(),m_doc.getAD_Table_ID(),m_doc.getRecord_ID(),(docLine == null)
                ?0
                :docLine.getTrxLine_ID(),m_trxName );

        // Set Info & Account

        line.setDocumentInfo( m_docVO,docLine );
        line.setPostingType( m_postingType );
        line.setAccount( m_acctSchema,account );

        // Amounts - one needs to not zero

        if( !line.setAmtSource( C_Currency_ID,debitAmt,creditAmt )) {
            if( (docLine == null) || (docLine.getQty() == null) || (docLine.getQty().signum() == 0) ) {
                log.fine( "Both amounts & qty = 0/Null - " + docLine + " - " + toString());

                return null;
            }

            log.fine( "Both amounts = 0/Null, Qty=" + docLine.getQty() + " - " + docLine + " - " + toString());
        }

        // Convert

        line.convert();

        // Optionally overwrite Acct Amount

        if( (docLine != null) && ( (docLine.getAmtAcctDr() != null) || (docLine.getAmtAcctCr() != null) ) ) {
            line.setAmtAcct( docLine.getAmtAcctDr(),docLine.getAmtAcctCr());
        }

        //

        log.fine( line.toString());
        add( line );

        return line;
    }    // createLine

    /**
     * Descripción de Método
     *
     *
     * @param line
     */

    void add( FactLine line ) {
        m_lines.add( line );
    }    // add

    /**
     * Descripción de Método
     *
     *
     * @param docLine
     * @param accountDr
     * @param accountCr
     * @param C_Currency_ID
     * @param Amt
     *
     * @return
     */

    public FactLine createLine( DocLine docLine,MAccount accountDr,MAccount accountCr,int C_Currency_ID,BigDecimal Amt ) {
        if( Amt.compareTo( Env.ZERO ) < 0 ) {
            return createLine( docLine,accountCr,C_Currency_ID,null,Amt.abs());
        } else {
            return createLine( docLine,accountDr,C_Currency_ID,Amt,null );
        }
    }    // createLine

    /**
     * Descripción de Método
     *
     *
     * @param docLine
     * @param account
     * @param C_Currency_ID
     * @param Amt
     *
     * @return
     */

    public FactLine createLine( DocLine docLine,MAccount account,int C_Currency_ID,BigDecimal Amt ) {
        if( Amt.compareTo( Env.ZERO ) < 0 ) {
            return createLine( docLine,account,C_Currency_ID,null,Amt.abs());
        } else {
            return createLine( docLine,account,C_Currency_ID,Amt,null );
        }
    }    // createLine

    /**
     * Descripción de Método
     *
     *
     * @param PostingType
     *
     * @return
     */

    public boolean isPostingType( String PostingType ) {
        return m_postingType.equals( PostingType );
    }    // isPostingType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isConverted() {
        return m_converted;
    }    // isConverted

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAcctSchema getAcctSchema() {
        return m_acctSchema;
    }    // getAcctSchema

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSourceBalanced() {

        // No lines -> balanded

        if( m_lines.size() == 0 ) {
            return true;
        }

        BigDecimal balance  = getSourceBalance();
        boolean    retValue = balance.compareTo( Env.ZERO ) == 0;

        if( retValue ) {
            log.finer( toString());
        } else {
            log.warning( "NO - Diff=" + balance + " - " + toString());
        }

        return retValue;
    }    // isSourceBalanced

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected BigDecimal getSourceBalance() {
        BigDecimal result = Env.ZERO;

        for( int i = 0;i < m_lines.size();i++ ) {
            FactLine line = ( FactLine )m_lines.get( i );

            result = result.add( line.getSourceBalance());
        }

        // log.fine("getSourceBalance - " + result.toString());

        return result;
    }    // getSourceBalance

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public FactLine balanceSource() {
        if( !m_acctSchema.isSuspenseBalancing() || m_docVO.MultiCurrency ) {
            return null;
        }

        BigDecimal diff = getSourceBalance();

        log.finer( "Diff=" + diff );

        // new line

        FactLine line = new FactLine( m_doc.getCtx(),m_doc.getAD_Table_ID(),m_doc.getRecord_ID(),0,m_trxName );

        line.setDocumentInfo( m_docVO,null );
        line.setPostingType( m_postingType );

        // Amount

        if( diff.compareTo( Env.ZERO ) < 0 ) {    // negative balance => DR
            line.setAmtSource( m_docVO.C_Currency_ID,diff.abs(),Env.ZERO );
        } else {                                  // positive balance => CR
            line.setAmtSource( m_docVO.C_Currency_ID,Env.ZERO,diff );
        }

        // Account

        line.setAccount( m_acctSchema,m_acctSchema.getSuspenseBalancing_Acct());

        // Convert

        line.convert();

        //

        log.fine( line.toString());
        m_lines.add( line );

        return line;
    }    // balancingSource

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSegmentBalanced() {
        if( m_lines.size() == 0 ) {
            return true;
        }

        MAcctSchemaElement[] elements = m_acctSchema.getAcctSchemaElements();

        // check all balancing segments

        for( int i = 0;i < elements.length;i++ ) {
            MAcctSchemaElement ase = elements[ i ];

            if( ase.isBalanced() &&!isSegmentBalanced( ase.getElementType())) {
                return false;
            }
        }

        return true;
    }    // isSegmentBalanced

    /**
     * Descripción de Método
     *
     *
     * @param segmentType
     *
     * @return
     */

    public boolean isSegmentBalanced( String segmentType ) {
        if( segmentType.equals( MAcctSchemaElement.ELEMENTTYPE_Org )) {
            HashMap map = new HashMap();

            // Add up values by key

            for( int i = 0;i < m_lines.size();i++ ) {
                FactLine   line   = ( FactLine )m_lines.get( i );
                Integer    key    = new Integer( line.getAD_Org_ID());
                BigDecimal bal    = line.getSourceBalance();
                BigDecimal oldBal = ( BigDecimal )map.get( key );

                if( oldBal != null ) {
                    bal = bal.add( oldBal );
                }

                map.put( key,bal );

                // System.out.println("Add Key=" + key + ", Bal=" + bal + " <- " + line);

            }

            // check if all keys are zero

            Iterator values = map.values().iterator();

            while( values.hasNext()) {
                BigDecimal bal = ( BigDecimal )values.next();

                if( bal.compareTo( Env.ZERO ) != 0 ) {
                    map.clear();
                    log.warning( "(" + segmentType + ") NO - " + toString() + ", Balance=" + bal );

                    return false;
                }
            }

            map.clear();
            log.finer( "(" + segmentType + ") - " + toString());

            return true;
        }

        log.finer( "(" + segmentType + ") (not checked) - " + toString());

        return true;
    }    // isSegmentBalanced

    /**
     * Descripción de Método
     *
     */

    public void balanceSegments() {
        MAcctSchemaElement[] elements = m_acctSchema.getAcctSchemaElements();

        // check all balancing segments

        for( int i = 0;i < elements.length;i++ ) {
            MAcctSchemaElement ase = elements[ i ];

            if( ase.isBalanced()) {
                balanceSegment( ase.getElementType());
            }
        }
    }    // balanceSegments

    /**
     * Descripción de Método
     *
     *
     * @param elementType
     */

    private void balanceSegment( String elementType ) {

        // no lines -> balanced

        if( m_lines.size() == 0 ) {
            return;
        }

        log.fine( "(" + elementType + ") - " + toString());

        // Org

        if( elementType.equals( MAcctSchemaElement.ELEMENTTYPE_Org )) {
            HashMap map = new HashMap();

            // Add up values by key

            for( int i = 0;i < m_lines.size();i++ ) {
                FactLine   line       = ( FactLine )m_lines.get( i );
                Integer    key        = new Integer( line.getAD_Org_ID());
                BigDecimal balance    = line.getSourceBalance();
                BigDecimal oldBalance = ( BigDecimal )map.get( key );

                if( oldBalance != null ) {
                    balance = balance.add( oldBalance );
                }

                map.put( key,balance );

                // log.info ("balanceSegment - Key=" + key + ", Balance=" + balance + " - " + line);

            }

            // Create entry for non-zero element

            Iterator keys = map.keySet().iterator();

            while( keys.hasNext()) {
                Integer    key        = ( Integer )keys.next();
                BigDecimal difference = ( BigDecimal )map.get( key );

                // log.info ("balanceSegment - " + elementType + "=" + key + ", Difference=" + difference);
                //

                if( difference.compareTo( Env.ZERO ) != 0 ) {

                    // Create Balancing Entry

                    FactLine line = new FactLine( m_doc.getCtx(),m_doc.getAD_Table_ID(),m_doc.getRecord_ID(),0,m_trxName );

                    line.setDocumentInfo( m_docVO,null );
                    line.setPostingType( m_postingType );

                    // Amount & Account

                    if( difference.compareTo( Env.ZERO ) < 0 ) {
                        line.setAmtSource( m_docVO.C_Currency_ID,difference.abs(),Env.ZERO );
                        line.setAccount( m_acctSchema,m_acctSchema.getDueFrom_Acct( elementType ));
                    } else {
                        line.setAmtSource( m_docVO.C_Currency_ID,Env.ZERO,difference.abs());
                        line.setAccount( m_acctSchema,m_acctSchema.getDueTo_Acct( elementType ));
                    }

                    line.convert();
                    line.setAD_Org_ID( key.intValue());

                    //

                    m_lines.add( line );
                    log.fine( "(" + elementType + ") - " + line );
                }
            }

            map.clear();
        }
    }    // balanceSegment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isAcctBalanced() {

        // no lines -> balanced

        if( m_lines.size() == 0 ) {
            return true;
        }

        BigDecimal balance  = getAcctBalance();
        boolean    retValue = balance.compareTo( Env.ZERO ) == 0;

        if( retValue ) {
            log.finer( toString());
        } else {
            log.warning( "NO - Diff=" + balance + " - " + toString());
        }

        return retValue;
    }    // isAcctBalanced

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected BigDecimal getAcctBalance() {
        BigDecimal result = Env.ZERO;

        for( int i = 0;i < m_lines.size();i++ ) {
            FactLine line = ( FactLine )m_lines.get( i );

            result = result.add( line.getAcctBalance());
        }

        // log.fine(result.toString());

        return result;
    }    // getAcctBalance

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public FactLine balanceAccounting() {
        BigDecimal diff = getAcctBalance();

        log.fine( "Balance=" + diff + ", CurrBal=" + m_acctSchema.isCurrencyBalancing() + " - " + toString());

        FactLine line = null;

        // Create Currency Entry

        if( m_acctSchema.isCurrencyBalancing()) {
            line = new FactLine( m_doc.getCtx(),m_doc.getAD_Table_ID(),m_doc.getRecord_ID(),0,m_trxName );
            line.setDocumentInfo( m_docVO,null );
            line.setPostingType( m_postingType );
            line.setAccount( m_acctSchema,m_acctSchema.getCurrencyBalancing_Acct());

            // Amount

            line.setAmtSource( m_docVO.C_Currency_ID,Env.ZERO,Env.ZERO );
            line.convert();

            if( diff.compareTo( Env.ZERO ) < 0 ) {
                line.setAmtAcct( diff.abs(),Env.ZERO );
            } else {
                line.setAmtAcct( Env.ZERO,diff.abs());
            }

            log.fine( line.toString());
            m_lines.add( line );
        } else

        // Adjust biggest (Balance Sheet) line amount

        {
            BigDecimal BSamount = Env.ZERO;
            FactLine   BSline   = null;
            BigDecimal PLamount = Env.ZERO;
            FactLine   PLline   = null;

            // Find line

            for( int i = 0;i < m_lines.size();i++ ) {
                FactLine   l   = ( FactLine )m_lines.get( i );
                BigDecimal amt = l.getAcctBalance().abs();

                if( l.isBalanceSheet() && (amt.compareTo( BSamount ) > 0) ) {
                    BSamount = amt;
                    BSline   = l;
                } else if( !l.isBalanceSheet() && (amt.compareTo( PLamount ) > 0) ) {
                    PLamount = amt;
                    PLline   = l;
                }
            }

            if( BSline != null ) {
                line = BSline;
            } else {
                line = PLline;
            }

            if( line == null ) {
                log.severe( "No Line found" );
            } else {
                log.fine( "Adjusting Amt=" + diff.toString() + "; Line=" + line.toString());
                line.currencyCorrect( diff );
                log.fine( line.toString());
            }
        }    // correct biggest amount

        // Debug info only

        this.isAcctBalanced();

        return line;
    }    // balanceAccounting

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean checkAccounts() {

        // no lines -> nothing to distribute

        if( m_lines.size() == 0 ) {
            return true;
        }

        // For all fact lines

        for( int i = 0;i < m_lines.size();i++ ) {
            FactLine line    = ( FactLine )m_lines.get( i );
            MAccount account = line.getAccount();

            if( account == null ) {
                log.warning( "No Account for " + line );

                return false;
            }

            MElementValue ev = account.getAccount();

            if( ev == null ) {
                log.warning( "No Element Value for " + account + ": " + line );

                return false;
            }

            if( ev.isSummary()) {
                log.warning( "Cannot post to Summary Account " + ev + ": " + line );

                return false;
            }

            if( !ev.isActive()) {
                log.warning( "Cannot post to Inactive Account " + ev + ": " + line );

                return false;
            }
        }    // for all lines

        return true;
    }    // checkAccounts

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean distribute() {

        // no lines -> nothing to distribute

        if( m_lines.size() == 0 ) {
            return true;
        }

        ArrayList newLines = new ArrayList();

        // For all fact lines

        for( int i = 0;i < m_lines.size();i++ ) {
            FactLine        dLine         = ( FactLine )m_lines.get( i );
            MDistribution[] distributions = MDistribution.get( dLine.getAccount(),m_postingType,m_docVO.C_DocType_ID );

            // No Distribution for this line

            if( (distributions == null) || (distributions.length == 0) ) {
                continue;
            }

            // Just the first

            if( distributions.length > 1 ) {
                log.warning( "More then one Distributiion for " + dLine.getAccount());
            }

            MDistribution distribution = distributions[ 0 ];

            // Add Reversal

            FactLine reversal = dLine.reverse( distribution.getName());

            log.info( "Reversal=" + reversal );
            newLines.add( reversal );    // saved in postCommit

            // Prepare

            distribution.distribute( dLine.getAccount(),dLine.getSourceBalance(),dLine.getC_Currency_ID());

            MDistributionLine[] lines = distribution.getLines( false );

            for( int j = 0;j < lines.length;j++ ) {
                MDistributionLine dl = lines[ j ];

                if( !dl.isActive() || (dl.getAmt().compareTo( Env.ZERO ) == 0) ) {
                    continue;
                }

                FactLine factLine = new FactLine( m_doc.getCtx(),m_doc.getAD_Table_ID(),m_doc.getRecord_ID(),0,m_trxName );

                // Set Info & Account

                factLine.setDocumentInfo( m_docVO,dLine.getDocLine());
                factLine.setAccount( m_acctSchema,dl.getAccount());
                factLine.setPostingType( m_postingType );

                if( dl.isOverwriteOrg()) {    // set Org explicitly
                    factLine.setAD_Org_ID( dl.getOrg_ID());
                }

                //

                if( dl.getAmt().compareTo( Env.ZERO ) < 0 ) {
                    factLine.setAmtSource( dLine.getC_Currency_ID(),null,dl.getAmt().abs());
                } else {
                    factLine.setAmtSource( dLine.getC_Currency_ID(),dl.getAmt(),null );
                }

                // Convert

                factLine.convert();

                //

                String description = distribution.getName() + " #" + dl.getLine();

                if( dl.getDescription() != null ) {
                    description += " - " + dl.getDescription();
                }

                factLine.addDescription( description );

                //

                log.info( factLine.toString());
                newLines.add( factLine );
            }
        }    // for all lines

        // Add Lines

        for( int i = 0;i < newLines.size();i++ ) {
            m_lines.add( newLines.get( i ));
        }

        return true;
    }    // distribute

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Fact[" );

        sb.append( m_doc.toString());
        sb.append( "," ).append( m_acctSchema.toString());
        sb.append( ",PostType=" ).append( m_postingType );
        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public FactLine[] getLines() {
        FactLine[] temp = new FactLine[ m_lines.size()];

        m_lines.toArray( temp );

        return temp;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param trxName
     *
     * @return
     */

    public boolean save( String trxName ) {

        // save Lines

        for( int i = 0;i < m_lines.size();i++ ) {
            FactLine fl = ( FactLine )m_lines.get( i );

            // log.fine("save - " + fl);

            if( !fl.save( trxName )) {    // abort on first error
                return false;
            }
        }

        return true;
    }    // commit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String get_TrxName() {
        return m_trxName;
    }    // getTrxName

    /**
     * Descripción de Método
     *
     *
     * @param trxName
     */

    private void set_TrxName( String trxName ) {
        m_trxName = trxName;
    }    // set_TrxName

	/**
	 * Corrige los importes de los asientos en caso de que alguno contenga un
	 * valor negativo en el Debe o Haber. El que tiene un importe negativo lo
	 * convierte a cero, y lo pasa en positivo a la columna complementaria.<br>
	 * Ej:<br>
	 * Debe: -100 | Haber: 0<br>
	 * Se convierte a:<br>
	 * Debe: 0 | Haber: 100
	 */
    public void correctNegativeAmounts() {
    	
        for( int i = 0;i < m_lines.size();i++ ) {
            FactLine factLine = ( FactLine )m_lines.get( i );
            
            // Invierte el Source Credit y Debit si alguno de los dos es negativo.
            if (factLine.getAmtSourceDr().compareTo(BigDecimal.ZERO) < 0) {
            	factLine.setAmtSourceCr(factLine.getAmtSourceDr().negate());
            	factLine.setAmtSourceDr(BigDecimal.ZERO);
            } else if (factLine.getAmtSourceCr().compareTo(BigDecimal.ZERO) < 0) {
            	factLine.setAmtSourceDr(factLine.getAmtSourceCr().negate());
            	factLine.setAmtSourceCr(BigDecimal.ZERO);
            }
            
            // Invierte el Acct Credit y Debit si alguno de los dos es negativo.
            if (factLine.getAmtAcctDr().compareTo(BigDecimal.ZERO) < 0) {
            	factLine.setAmtAcctCr(factLine.getAmtAcctDr().negate());
            	factLine.setAmtAcctDr(BigDecimal.ZERO);
            } else if (factLine.getAmtAcctCr().compareTo(BigDecimal.ZERO) < 0) {
            	factLine.setAmtAcctDr(factLine.getAmtAcctCr().negate());
            	factLine.setAmtAcctCr(BigDecimal.ZERO);
            }

        }
    }
}    // Fact



/*
 *  @(#)Fact.java   24.03.06
 * 
 *  Fin del fichero Fact.java
 *  
 *  Versión 2.2
 *
 */
