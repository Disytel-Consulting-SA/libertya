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

import org.openXpertya.model.X_C_ElementValue;
import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MDistribution;
import org.openXpertya.model.MDistributionLine;
import org.openXpertya.model.MElementValue;
import org.openXpertya.model.PO;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

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
    
    /** Show log console - dREHER */
    private boolean isDebug = true;
    
    /** permite calcular montos segun una tasa de conversion que puede estar configurada en el documento dREHER Mayo 25 */
	private BigDecimal tasaConversion = null;

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
        
        // dREHER Mayo 25, trasladar la tasa de conversion desde fact a factLine
        line.setTasaConversion(getTasaConversion());

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
        boolean    retValue = balance.compareTo( BigDecimal.ZERO ) == 0;

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
        BigDecimal result = BigDecimal.ZERO;

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
    	return balanceSource(0);
    }
    
    public FactLine balanceSource(int projectID) {
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
        // dREHER Mayo 25, trasladar la tasa de conversion desde Fact a factLine
        line.setTasaConversion(getTasaConversion());

        if( diff.compareTo( BigDecimal.ZERO ) < 0 ) {    // negative balance => DR
            line.setAmtSource( m_docVO.C_Currency_ID,diff.abs(),BigDecimal.ZERO );
        } else {                                  // positive balance => CR
            line.setAmtSource( m_docVO.C_Currency_ID,BigDecimal.ZERO,diff );
        }

        // Account

        line.setAccount( m_acctSchema,m_acctSchema.getSuspenseBalancing_Acct());
        line.setC_Project_ID(projectID);
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

                if( bal.compareTo( BigDecimal.ZERO ) != 0 ) {
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

    // dREHER para mostrar salidas en consola / eclipse
    private void debug(String string) {
    	
    	if(isDebug) {
    		System.out.println("====> Fact. " + string);
    		log.info("====> Fact. " + string);
    	}
	}

    
    /**
     * Descripción de Método
     *
     */

    public void balanceSegments() {
    	balanceSegments(0);
    }
    
    public void balanceSegments(int projectID) {
        MAcctSchemaElement[] elements = m_acctSchema.getAcctSchemaElements();

        // check all balancing segments

        for( int i = 0;i < elements.length;i++ ) {
            MAcctSchemaElement ase = elements[ i ];

            if( ase.isBalanced()) {
                balanceSegment( ase.getElementType(), projectID);
            }
        }
    }    // balanceSegments

    /**
     * Descripción de Método
     *
     *
     * @param elementType
     */
    public FactLine balanceSegment( String elementType ) {
    	return balanceSource(0);
    }

    private void balanceSegment( String elementType, int projectID ) {

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
                 // dREHER Mayo 25, trasladar la tasa de conversion desde Fact a factLine
                    line.setTasaConversion(getTasaConversion());
                    
                    if( difference.compareTo( Env.ZERO ) < 0 ) {
                        line.setAmtSource( m_docVO.C_Currency_ID,difference.abs(),Env.ZERO );
                        line.setAccount( m_acctSchema,m_acctSchema.getDueFrom_Acct( elementType ));
                    } else {
                        line.setAmtSource( m_docVO.C_Currency_ID,Env.ZERO,difference.abs());
                        line.setAccount( m_acctSchema,m_acctSchema.getDueTo_Acct( elementType ));
                    }

                    line.convert();
                    line.setAD_Org_ID( key.intValue());
                    line.setC_Project_ID(projectID);
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
        boolean    retValue = balance.compareTo( BigDecimal.ZERO ) == 0;

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
        BigDecimal result = BigDecimal.ZERO;

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
    	return balanceAccounting(0);
    }
    
    public FactLine balanceAccounting(int projectID) {
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
         // dREHER Mayo 25, trasladar la tasa de conversion desde Fact a factLine
            line.setTasaConversion(getTasaConversion());
            
            line.setAmtSource( m_docVO.C_Currency_ID,BigDecimal.ZERO,BigDecimal.ZERO );
            line.convert();

            if( diff.compareTo( BigDecimal.ZERO ) < 0 ) {
                line.setAmtAcct( diff.abs(),BigDecimal.ZERO );
            } else {
                line.setAmtAcct( BigDecimal.ZERO,diff.abs());
            }

            log.fine( line.toString());
            m_lines.add( line );
        } else

        // Adjust biggest (Balance Sheet) line amount

        {
            BigDecimal BSamount = BigDecimal.ZERO;
            FactLine   BSline   = null;
            BigDecimal PLamount = BigDecimal.ZERO;
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
        line.setC_Project_ID(projectID);
        
        this.isAcctBalanced();

        return line;
    }    // balanceAccounting

    /**
     * Descripción de Método
     *
     *
     * @return CallResult para poder devolver errores mas claros y no solo True/False 
     * dREHER Mayo 25
     */

    public CallResult checkAccounts() {
    	CallResult cr = new CallResult();
    	
        // no lines -> nothing to distribute

        if( m_lines.size() == 0 ) {
            // return true;
        	return cr;
        }

        // For all fact lines

        for( int i = 0;i < m_lines.size();i++ ) {
            FactLine line    = ( FactLine )m_lines.get( i );
            MAccount account = line.getAccount();

            if( account == null ) {
                log.warning( "No Account for " + line );
                debug("No account for " + line);
                cr.setMsg("No account for " + line, true);
                return cr;
            }

            // dREHER - traer instancia custom de la cuenta contable (C_ElementValue)
            // LP_C_ElementValue ev = account.getCustomAccount();
            // En la version web trae problemas para postear asientos...
            
            MElementValue ev = account.getAccount();

            if( ev == null ) {
                log.warning( "No Element Value for " + account + ": " + line );
                debug( "No Element Value for " + account + ": " + line );
                cr.setMsg("No Element Value for " + account + ": " + line, true);
                return cr;
            }

            if( ev.isSummary()) {
                log.warning( "Cannot post to Summary Account " + ev + ": " + line );
                debug( "Cannot post to Summary Account " + ev + ": " + line );
                cr.setMsg("Cannot post to Summary Account " + ev + ": " + line, true);
                return cr;
            }

            if( !ev.isActive()) {
                log.warning( "Cannot post to Inactive Account " + ev + ": " + line );
                debug( "Cannot post to Inactive Account " + ev + ": " + line );
                cr.setMsg("Cannot post to Inactive Account " + ev + ": " + line, true);
                return cr;
            }
            
            /**
             * Si la cuenta requiere que sea obligatorio un proyecto y la linea no lo tiene y el encabezado tampoco 
             * -> excepcion. 
             * Contemplar tambien que si NO es mandatorio, pero utiliza base distributiva y dicha base NO
             * esta validada, tambien excepcion
             * dREHER
             */
            
            // boolean isDistributiveBase = ev.isCintolo_UsesDistributiveBase();
            // boolean isValidatedDistribution = ev.isCintolo_ValidatedDistribution();
            
            boolean isDistributiveBase = ev.get_Value("Cintolo_UsesDistributiveBase")==null?false:
            							(Boolean)ev.get_Value("Cintolo_UsesDistributiveBase");
            boolean isValidatedDistribution = ev.get_Value("Cintolo_ValidatedDistribution")==null?false:
            							(Boolean)ev.get_Value("Cintolo_ValidatedDistribution");
            
            debug("ev.isCintolo_UsesDistributiveBase()..." + isDistributiveBase + " Validated=" + isValidatedDistribution);
            if(isDistributiveBase && !isValidatedDistribution) {
            	log.warning("Falta validar la base distributiva: " + ev);
            	debug("Falta validar la base distributiva: " + ev);
        		cr.setMsg("Falta validar la base distributiva: " + ev, true);
            	return cr;
            }
            
            // boolean isMandatoryCostCenter = ev.isCintolo_MandatoryCostCenter();
            boolean isMandatoryCostCenter = ev.get_Value("Cintolo_MandatoryCostCenter")==null?false:
            								(Boolean)ev.get_Value("Cintolo_MandatoryCostCenter");
            debug("ev.isCintolo_MandatoryCostCenter()..." + isMandatoryCostCenter);
            if(	isMandatoryCostCenter ) {
            	boolean isProyecto = false;

            	PO po = m_doc.getPO();
            	if(po!=null) {
            		Object proyecto = po.get_Value("C_Project_ID");
            		if(proyecto!=null && (Integer)proyecto > 0)
            			isProyecto = true;
            	}else {
            		if(m_docVO.C_Project_ID > 0)
            			isProyecto = true;
            	}
            	
            	debug("Requiere proyecto obligatorio, tiene uno ?" + isProyecto);
            	
            	// Si utiliza base distributiva NO ejecutar este control, ya que luego los proyectos se postean automaticamente segun
            	// la base distributiva configurada
            	if(!isDistributiveBase && line.getC_Project_ID() <= 0 && !isProyecto) {
            		log.warning("Es obligatorio indicar el centro de costos (Proyecto) ev= " + ev);
            		debug("Es obligatorio indicar el centro de costos (Proyecto) ev=" + ev);
            		cr.setMsg("Es obligatorio indicar el centro de costos (Proyecto) ev=" + ev, true);
            		return cr;
            	}
            }
            
            debug("account Ok! account="+account + " line=" + line);
            
        }    // for all lines
        
        debug("end checkAccounts... " + true);
        
        cr.setError(false);
        return cr;
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

                // dREHER Mayo 25, trasladar la tasa de conversion desde Fact a factLine
                factLine.setTasaConversion(getTasaConversion());
                
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

        	// dREHER Mayo 25 - si la linea NO tiene CENTRO DE COSTOS, pero el encabezado SI, tomar ese Centro de Costos tambien para las lineas
    		if(Util.isEmpty(fl.getC_Project_ID(), true) && !Util.isEmpty(m_docVO.C_Project_ID, true)) {
    			// fl.setC_Project_ID(m_docVO.C_Project_ID);
    			// debug("Seteo centro de costos en la linea, tomandolo del encabezado C_Project_ID=" + fl.getC_Project_ID());
    		}
        	
        	if( !fl.save( trxName )) {    // abort on first error
				return false;
			}
        	
        }
        
        debug("Guarda las lineas del asiento contable, verificar si debe generar base distributiva...");
        
        DistributeBase(trxName);

        return true;
    }    // commit
    
    /**
     * Base distributiva si asi se requiere
     * 
     * dREHER
     */

    public void DistributeBase(String trxName) {
    	/**
    	 * Si requiere base distributiva, debo reemplazar la linea original por la distribucion correspondiente
    	 * 
    	 * dREHER
    	 */

    	for( int i = 0;i < m_lines.size();i++ ) {
    		
    		FactLine factLine = ( FactLine )m_lines.get( i );
    		
    		// dREHER Mayo 25 - si la linea TIENE CENTRO DE COSTOS, se toma ese, NO DISTRIBUIR...
    		if(!Util.isEmpty(factLine.getC_Project_ID(), true)) {
    			continue;
    		}
    		
    		// dREHER Mayo 25 - si la linea NO tiene CENTRO DE COSTOS, pero el encabezado SI, tomar ese Centro de Costos tambien para las lineas
    		if(!Util.isEmpty(m_docVO.C_Project_ID, true)) {
    			// factLine.setC_Project_ID(m_docVO.C_Project_ID);
    			// factLine.save(trxName);
    			// debug("Seteo centro de costos en la linea, tomandolo del encabezado C_Project_ID=" + factLine.getC_Project_ID());
    			continue;
    		}
    		

    		MAccount account = factLine.getAccount();

    		// dREHER - traer instancia custom de la cuenta contable (C_ElementValue)
    		// LP_C_ElementValue ev = account.getCustomAccount();
    		MElementValue ev = account.getAccount();
    		boolean isDistributiveBase = ev.get_Value("Cintolo_UsesDistributiveBase")==null?false:
    									(Boolean)ev.get_Value("Cintolo_UsesDistributiveBase");
            boolean isValidatedDistribution = ev.get_Value("Cintolo_ValidatedDistribution")==null?false:
            							(Boolean)ev.get_Value("Cintolo_ValidatedDistribution");
            
            
            
            
            
            debug("afterSave.ev.isCintolo_UsesDistributiveBase()..." + isDistributiveBase + " Validated=" + isValidatedDistribution);
    		String desc = factLine.getDescription();
    		if(isDistributiveBase && isValidatedDistribution
    				&& (desc==null || desc.indexOf("Base Distributiva.") < 0)) { // TODO mejorar esta validacion (por ahora para no hacerlo dos veces)

    			// TODO: Si distribuyo Elimino o Desactivo ?
    			if(factLine.DistribuirLinea(ev, trxName)) {
    				
    				int Fact_Acct_ID = factLine.getFact_Acct_ID();
    				
    				// this.setIsActive(false);
    				// this.delete(true);
    				// int upd = DB.executeUpdate("UPDATE Fact_Acct SET IsActive='N', Description=COALESCE(description,'') || ' Base Distributiva.', updated=now() WHERE Fact_Acct_ID=" + factLine.getFact_Acct_ID(), get_TrxName());
    				// int upd = DB.executeUpdate("DELETE FROM Fact_Acct WHERE Fact_Acct_ID=" + factLine.getFact_Acct_ID(), get_TrxName());
    				
    				debug("Distribuyo linea, desactivar linea original... Fact_Acct_ID= " + Fact_Acct_ID);
    				
    				boolean isDel = factLine.delete(true, trxName);
    				if(isDel)
    					debug("Desactivo Ok linea Fact_Acct_ID=" + Fact_Acct_ID);
    				else
    					debug("NO pudo desactivar linea Fact_Acct_ID=" + Fact_Acct_ID);
    				
    			}

    		}

    	}
    }

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

	public BigDecimal getTasaConversion() {
		return tasaConversion;
	}

	public void setTasaConversion(BigDecimal tasaConversion) {
		this.tasaConversion = tasaConversion;
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
