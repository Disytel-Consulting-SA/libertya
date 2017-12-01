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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.openXpertya.cc.CurrentAccountDocument;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MCash extends X_C_Cash implements DocAction {

	/**
	 * Asociación entre entidades comerciales (ID) y objetos de retorno del trabajo
	 * adicional realizado por cuentas corrientes, luego eso se utiliza al
	 * procesar el documento al final. En el método afterProcessDocument
	 */
	
	private Map<Integer, Map<PO,Object>> ccBPUpdates = new HashMap<Integer, Map<PO,Object>>();
	
	/** Booleano que determina que estamos ante una caja de una caja diaria */
	private boolean isPOSJournalCash = false;
	
	/** ID del libro de caja reverso */
	private int reverseCashID = 0;
	
    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MCash.class );
    
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Org_ID
     * @param dateAcct
     * @param C_Currency_ID
     * @param trxName
     *
     * @return
     */

    public static MCash get( Properties ctx,int AD_Org_ID,Timestamp dateAcct,int C_Currency_ID,String trxName ) {
        MCash retValue = null;

        // Existing Journal

        String sql = "SELECT * FROM C_Cash c " + "WHERE c.AD_Org_ID=?"                                                                                                                               // #1
                     + " AND extract('day' from c.StatementDate::date) = extract('day' from ?::date) " 
                 	+ " AND extract('month' from c.StatementDate::date) = extract('month' from ?::date) " 
                	+ " AND extract('year' from c.StatementDate::date) = extract('year' from ?::date) "
                     + " AND c.Processed='N'" + " AND EXISTS (SELECT * FROM C_CashBook cb " + "WHERE c.C_CashBook_ID=cb.C_CashBook_ID AND cb.AD_Org_ID=c.AD_Org_ID" + " AND cb.C_Currency_ID=?)";    // #3
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,AD_Org_ID );
            pstmt.setTimestamp( 2,dateAcct );
            pstmt.setTimestamp( 3,dateAcct );
            pstmt.setTimestamp( 4,dateAcct );
            pstmt.setInt( 5,C_Currency_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MCash( ctx,rs,trxName );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( retValue != null ) {
            return retValue;
        }

        // Get CashBook

        MCashBook cb = MCashBook.get( ctx,AD_Org_ID,C_Currency_ID,trxName );

        if( cb == null ) {
            s_log.warning( "No CashBook for AD_Org_ID=" + AD_Org_ID + ", C_Currency_ID=" + C_Currency_ID );

            return null;
        }

        // Create New Journal

        retValue = new MCash( cb,dateAcct );
        retValue.save( trxName );

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_CashBook_ID
     * @param dateAcct
     * @param trxName
     *
     * @return
     */

    public static MCash get( Properties ctx,int C_CashBook_ID,Timestamp dateAcct,String trxName ) {
        MCash retValue = null;

        // Existing Journal

        String            sql   = "SELECT * FROM C_Cash c " + "WHERE c.C_CashBook_ID=?"    // #1
                                  + " AND TRUNC(c.StatementDate)=TRUNC(?::timestamp)"                        // #2
                                  + " AND c.Processed='N'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,C_CashBook_ID );
            pstmt.setTimestamp( 2,dateAcct );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MCash( ctx,rs,trxName );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( retValue != null ) {
            return retValue;
        }

        // Get CashBook

        MCashBook cb = new MCashBook( ctx,C_CashBook_ID,trxName );

        if( cb.getID() == 0 ) {
            s_log.warning( "Not found C_CashBook_ID=" + C_CashBook_ID );

            return null;
        }

        // Create New Journal

        retValue = new MCash( cb,dateAcct );
        retValue.save( trxName );

        return retValue;
    }    // get

    /**
	 * Obtiene un libro de caja sin procesar para la organización, configuración de libro de
	 * caja y fecha parametro
	 * 
	 * @param ctx
	 *            contexto actual
	 * @param AD_Org_ID
	 *            organización, recibir el valor 0 indica buscar para
	 *            organización *. En el caso que no se desee filtrar por
	 *            organización, el parámetro debe ser null.
	 * @param C_CashBook_ID
	 *            configuración del libro de caja
	 * @param dateAcct
	 *            fecha
	 * @param trxName
	 *            transacción actual
	 * @return libro de caja con las condiciones dadas, si se encuentran más de
	 *         uno, entonces se ordena por fecha de creación descendentemente y
	 *         se queda con el primero.
	 */
    public static MCash get( Properties ctx, Integer AD_Org_ID, Integer C_CashBook_ID, Timestamp dateAcct,String trxName ) {
    	String whereClause = "ad_client_id = ? and processed = 'N' ";
    	List<Object> params = new ArrayList<Object>();
    	params.add(Env.getAD_Client_ID(ctx));
    	// Organización
    	if(AD_Org_ID != null){
    		whereClause += " AND AD_Org_ID = ? ";
    		params.add(AD_Org_ID);
    	}
    	// Config de libro de caja
    	if(!Util.isEmpty(C_CashBook_ID, true)){
    		whereClause += " AND C_CashBook_ID = ? ";
    		params.add(C_CashBook_ID);
    	}
    	// Fecha
    	if(dateAcct != null){
        	whereClause += " AND dateacct::date = ?::date ";
        	params.add(dateAcct);
    	}
		return (MCash) PO.findFirst(ctx, Table_Name, whereClause, params.toArray(),
				new String[] { "dateacct desc, created desc" }, trxName, true);
    }
    
    

	/**
	 * @param fromCashID
	 * @param toCashID
	 * @param amount
	 * @param ctx
	 * @param trxName
	 * @throws Exception
	 */
	public static void transferCash(int fromCashID, int toCashID,
			BigDecimal amount, Properties ctx, String trxName) throws Exception {
		transferCash(fromCashID, toCashID, amount, 0, ctx, trxName);
    }
	
	/**
	 * 
	 * @param fromCashID
	 * @param toCashID
	 * @param amount
	 * @param projectID
	 * @param ctx
	 * @param trxName
	 * @throws Exception
	 */
	public static void transferCash(int fromCashID, int toCashID,
			BigDecimal amount, Integer projectID, Properties ctx, String trxName) throws Exception {
		transferCash(fromCashID, toCashID, amount, projectID, false, ctx, trxName);
    }
    
	/**
	 * 
	 * @param fromCashID
	 * @param toCashID
	 * @param amount
	 * @param ignorePOSJournalRelated
	 * @param ctx
	 * @param trxName
	 * @throws Exception
	 */
	public static void transferCash(int fromCashID, int toCashID,
			BigDecimal amount, boolean ignorePOSJournalRelated, Properties ctx, String trxName) throws Exception {
		transferCash(fromCashID, toCashID, amount, 0, ignorePOSJournalRelated, ctx, trxName);
    }
	
	/**
	 * 
	 * @param fromCashID
	 * @param toCashID
	 * @param amount
	 * @param projectID
	 * @param ignorePOSJournalRelated
	 * @param ctx
	 * @param trxName
	 * @throws Exception
	 */
	public static void transferCash(int fromCashID, int toCashID,
			BigDecimal amount, Integer projectID,
			boolean ignorePOSJournalRelated, Properties ctx, String trxName)
			throws Exception {

		// El importe debe ser diferente a cero.
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			throw new Exception(Msg.getMsg(ctx,
					"ValueMustBeGreatherThanZero",
					new Object[] { Msg.getElement(ctx, "Amount") }));
		}
		
		if (fromCashID == toCashID) {
			throw new Exception("@InvalidCashTransfer@");
		}

		// Obtiene libro de caja origen
		MCash fromCash = new MCash(ctx, fromCashID, trxName);
		
		// Crea y guarda la línea de caja en la caja origen indicando que es una transferencia
		// a la caja destino
		MCashLine fromCashLine = new MCashLine(fromCash);
		fromCashLine.setCashType(MCashLine.CASHTYPE_CashTransfer);
		fromCashLine.setTransferCash_ID(toCashID);
		fromCashLine.setAmount(amount.negate());
		Integer realProjectID = projectID;
		if(Util.isEmpty(projectID, true)){
			realProjectID = fromCashLine.getC_Project_ID();
		}
		fromCashLine.setC_Project_ID(realProjectID);
		fromCashLine.setIgnorePOSJournal(ignorePOSJournalRelated);
		if (!DocumentEngine.processAndSave(fromCashLine, MCashLine.ACTION_Complete, true)) {
			throw new Exception("@CashLineCreateError@ (" + fromCash.getName()
					+ "): " + fromCashLine.getProcessMsg());
		}
    }
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Cash_ID
     * @param trxName
     */

    public MCash( Properties ctx,int C_Cash_ID,String trxName ) {
        super( ctx,C_Cash_ID,trxName );

        if( C_Cash_ID == 0 ) {

            // setC_CashBook_ID (0);           //      FK

            setBeginningBalance( Env.ZERO );
            setEndingBalance( Env.ZERO );
            setStatementDifference( Env.ZERO );
            setDocAction( DOCACTION_Complete );
            setDocStatus( DOCSTATUS_Drafted );

            //

            Timestamp today = Env.getTimestamp();

            setStatementDate( today );                                                  // @#Date@
            setDateAcct( today );                                                       // @#Date@
            setName( DisplayType.getDateFormat( DisplayType.Date ).format( today ));    // @#Date@
            setIsApproved( false );
            setPosted( false );    // N
            setProcessed( false );
        }
    }                              // MCash

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCash( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCash

    /**
     * Constructor de la clase ...
     *
     *
     * @param cb
     * @param today
     */

    public MCash( MCashBook cb,Timestamp today ) {
        this( cb.getCtx(),0,cb.get_TrxName());
        setClientOrg( cb );
        setC_CashBook_ID( cb.getC_CashBook_ID());

        if( today != null ) {
            setStatementDate( today );
            setDateAcct( today );
            setName( cb.getName() + " " + DisplayType.getDateFormat( DisplayType.Date ).format( today ));
        }
    }    // MCash

    /** Descripción de Campos */

    private MCashLine[] m_lines = null;

    /** Descripción de Campos */

    private MCashBook m_book = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MCashLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_CashLine WHERE C_Cash_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Cash_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MCashLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_lines = new MCashLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MCashBook getCashBook() {
        if( m_book == null ) {
            m_book = MCashBook.get( getCtx(),getC_CashBook_ID(),get_TrxName());
        }

        return m_book;
    }    // getCashBook

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

    	// Actualización del campo de validación de cajas diarias
    	MClientInfo ci = MClient.get(getCtx()).getInfo();
		setValidatePOSJournal(ci.isPOSJournalActive() &&
				MCashBook.CASHBOOKTYPE_JournalCashBook.equals(getCashBookType()) && 
				(!Util.isEmpty(getC_POSJournal_ID(), true))
					|| (MClientInfo.POSJOURNALAPPLICATION_Both.equals(ci.getPOSJournalApplication())));
    	
    	//Verifico que no haya un libro de caja abierto actualmente
    	
    	//Si es nuevo registro, realizo verificación
    	
    	if(newRecord && Util.isEmpty(getReverseCashID(), true)){
			// La verificación solo se hace para los Libros que son Cajas
			// Generales.
			// Para los libros de Cajas Diarias es posible crear varios libros
			// en borrador (la limitación es que solo se puede completar el
			// libro si el saldo es cero, ver completeIt)
    		if (MCashBook.CASHBOOKTYPE_GeneralCashBook.equals(getCashBookType())) {
				String sql = new String(
						"select count(*)::integer as cant from c_cash where (isactive='Y') and (c_cashbook_id = ?) and (ad_org_id = ?) and (docstatus = 'DR')");
				
				Integer count = (Integer) DB.getSQLObject(
						get_TrxName(),
						sql,
						new Object[] { this.getC_CashBook_ID(),
								this.getAD_Org_ID() });
				
				if(!Util.isEmpty(count, true)){
					log.saveError("NotCompletedCash", "");
					return false;
				}
			}
    		
    		
            // Org Consistency

            if (is_ValueChanged( "C_CashBook_ID" )) {
                MCashBook cb = MCashBook.get( getCtx(),getC_CashBook_ID(),get_TrxName());

                if( cb.getAD_Org_ID() != getAD_Org_ID()) {
                    log.saveError( "Error",Msg.parseTranslation( getCtx(),"@AD_Org_ID@: @C_CashBook_ID@" ));

                    return false;
                }
            }
            
            // Para Cajas Diarias se asigna cero al saldo Inicial.
            if (MCashBook.CASHBOOKTYPE_JournalCashBook.equals(getCashBookType())) {
            	setBeginningBalance(BigDecimal.ZERO);
            }
            
            if(getC_POSJournal_ID() == 0 && MCashBook.CASHBOOKTYPE_JournalCashBook.equals(getCashBookType())){
        		// Caja Diaria. Intenta registrar el libro de caja
                if (isValidatePOSJournal() && !MPOSJournal.registerDocument(this)) {
        			log.saveError("SaveError", Msg.getMsg(getCtx(), "CashPOSJournalRequiredError"));
        			return false;
                }	
            }
    	}

    	// Modificación por Matias Cap
		// Se elimina la condición de la moneda ya que siempre hay que realizar la
		// validación de la modificación de fechas
 		// No es posible modificar las fechas estado de cuenta y/o aplicación si existen líneas de caja que ya fueron procesadas
		if (!newRecord && (is_ValueChanged("StatementDate") || is_ValueChanged("DateAcct"))) {      
            if(containsProcessedLines()) {
                log.saveError( "Error",Msg.translate(getCtx(), "CannotChangeDateStatementAcct"));
                return false;
            }
        }
		
		// Si existe al menos una línea completa, no se puede modificar ninguna fecha de la caja
		
    	    	
    	// Calculate End Balance
    	
    	setEndingBalance( getBeginningBalance().add( getStatementDifference()));

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param processAction
     *
     * @return
     */

    public boolean processIt( String processAction ) {
        m_processMsg = null;

        DocumentEngine engine = new DocumentEngine( this,getDocStatus());

        boolean status = engine.processIt( processAction,getDocAction(),log );
        
        status = afterProcessDocument(engine.getDocAction(), status) && status;
        
        return status;
    }    // process


    /** Descripción de Campos */

    private boolean m_justPrepared = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        log.info( "unlockIt - " + toString());
        setProcessing( false );

        return true;
    }    // unlockIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt() {
        log.info( "invalidateIt - " + toString());
        setDocAction( DOCACTION_Prepare );

        return true;
    }    // invalidateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt() {
        log.info( toString());
        m_processMsg = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_BEFORE_PREPARE );

        if( m_processMsg != null ) {
            return DocAction.STATUS_Invalid;
        }

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getDateAcct(),MDocType.DOCBASETYPE_CashJournal )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        MCashLine[] lines = getLines( false );

		// Para cajas pertenecientes a cajas diarias, no se debe realizar este
		// control ya que podemos tener movimientos que no sean efectivo en la
		// caja diaria
        if( !isPOSJournalCash() && lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Add up Amounts

        BigDecimal difference    = Env.ZERO;
        int        C_Currency_ID = getC_Currency_ID();

        for( int i = 0;i < lines.length;i++ ) {
            MCashLine line = lines[ i ];

            if( !line.isActive()) {
                continue;
            }

            if( C_Currency_ID == line.getC_Currency_ID()) {
                difference = difference.add( line.getAmount());
            } else {
                //BigDecimal amt = MConversionRate.convert( getCtx(),line.getAmount(),line.getC_Currency_ID(),C_Currency_ID,getDateAcct(),0,getAD_Client_ID(),getAD_Org_ID());
            	// Modificado para que soporte la Tasa en cualquier sentido
            	// (así se hace en facturas).
            	BigDecimal amt = MCurrency.currencyConvert(line.getAmount(),
						line.getC_Currency_ID(), C_Currency_ID,
						getDateAcct(), 0, getCtx());
            	
                if( amt == null ) {
                    m_processMsg = "@NoCurrencyConversion@ - @C_CashLine_ID@= " + line.getLine();

                    return DocAction.STATUS_Invalid;
                }

                difference = difference.add( amt );
            }
        }

        setStatementDifference( difference );

        // setEndingBalance(getBeginningBalance().add(getStatementDifference()));
        //

        // Los libros de Cajas Diarias deben tener su saldo en cero para poder
        // ser completados.
        if (MCashBook.CASHBOOKTYPE_JournalCashBook.equals(getCashBookType()) 
        		&& getEndingBalance().compareTo(BigDecimal.ZERO) != 0) {
        	m_processMsg = "@CashJournalBalanceMustBeZero@";
        	return STATUS_Invalid;
        }
        
        m_justPrepared = true;

        if( !DOCACTION_Complete.equals( getDocAction())) {
            setDocAction( DOCACTION_Complete );
        }

        return DocAction.STATUS_InProgress;
    }    // prepareIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        log.info( "approveIt - " + toString());
        setIsApproved( true );

        return true;
    }    // approveIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt() {
        log.info( "rejectIt - " + toString());
        setIsApproved( false );

        return true;
    }    // rejectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String completeIt() {

        // Re-Check

    	if (!m_justPrepared	&& !existsJustPreparedDoc()) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        //

        log.info( "completeIt - " + toString());
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName());
        MCashLine[] lines = getLines( false );
    	boolean error = false;
    	String errorMsg = null;
    	Map<PO,Object> caResults = null;
    	// Completa todas las líneas de la caja que aún permanezcan en estado borrador.
    	// Si alguna falla entonces no es posible continuar con el completado del Libro.
    	for( int i = 0;i < lines.length;i++ ) {
            MCashLine line = lines[ i ];
            if (DOCSTATUS_Drafted.equals(line.getDocStatus())) {
                line.setConfirmAditionalWorks(false);
                if(Util.isEmpty(line.getC_POSJournal_ID(), true)){
                	line.setC_POSJournal_ID(getC_POSJournal_ID());
                }
            	if (!line.processIt(ACTION_Complete)) {
            		errorMsg = line.getProcessMsg();
            		error = true;
            	} else if (!line.save()) {
            		errorMsg = CLogger.retrieveErrorAsString();
            		error = true;
            	}
            	
            	if (error) {
            		m_processMsg = "@CashLineProcessError@ # " + line.getLine() + ": " + errorMsg; 
            		return STATUS_Invalid;
            	}
            	
            	// Verifico si el gestor de cuentas corrientes debe realizar operaciones
        		// antes de completar. 
				// Me guardo las líneas, el resultado de la llamada a la
				// operación y la entidad comercial 
    			if (!Util.isEmpty(line.getC_BPartner_ID(), true)) {
                	MBPartner bp = new MBPartner(getCtx(), line.getC_BPartner_ID(), get_TrxName());
                	// Agrego a la map para que luego se realicen las tareas
    				// correspondientes. 
    	    		if(line.getAditionalWorkResult().get(line) != null){
    		    		// Obtengo la map de la entidad comercial, si no existe creo una
    					if(caResults == null){
    						caResults = new HashMap<PO,Object>();
    					}
    					caResults.put(line,line.getAditionalWorkResult().get(line));
    				}
					// Agrego la entidad comercial a la map para luego realizar
					// las tareas luego de procesar esta transacción
					getCcBPUpdates().put(bp.getID(), caResults);
                }
            }
        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        //

        setProcessed( true );
        setDocAction( DOCACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        log.info( "postIt - " + toString());

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt() {
        // Si el libro de caja pertenece a una caja diaria, no es posible anular
		if (PO.findFirst(getCtx(), X_C_POSJournal.Table_Name, "c_cash_id = ?", new Object[] { getID() }, null,
				get_TrxName()) != null) {
    		setProcessMsg(Msg.getMsg(getCtx(), "CanNotVoidPOSJournalCash"));
    		return false;
    	}
    	
		// Crear el libro de caja reverso
		MCash reverseCash = new MCash(getCtx(), 0, get_TrxName());
		PO.copyValues(this, reverseCash);
		reverseCash.setStatementDate(Env.getDate());
		reverseCash.setDateAcct(reverseCash.getStatementDate());
		reverseCash.setDescription(getName()+"^");
		reverseCash.setBeginningBalance(getBeginningBalance().negate());
		reverseCash.setStatementDifference(BigDecimal.ZERO);
		reverseCash.setReverseCashID(getID());
		if(!reverseCash.save()){
			setProcessMsg(CLogger.retrieveErrorAsString());
			return false;
		}
		
		// Anular cada línea de caja
		setReverseCashID(reverseCash.getID());
		for (MCashLine mCashLine : getLines(true)) {
			mCashLine.setReverseCashID(getReverseCashID());
			if(!DocumentEngine.processAndSave(mCashLine, DOCACTION_Void, false)){
				setProcessMsg("@CashLineProcessError@ #" + mCashLine.getLine() + ": " + mCashLine.getProcessMsg());
				return false;
			}
		}
		
		// Recargar la caja reversa por actualización de importes de cada línea
		reverseCash = new MCash(getCtx(), reverseCash.getID(), get_TrxName());
		reverseCash.setDocStatus( DOCSTATUS_Voided );
		reverseCash.setDocAction( DOCACTION_None );
		if(!reverseCash.save()){
			setProcessMsg(CLogger.retrieveErrorAsString());
			return false;
		}
		
		setDescription("^"+reverseCash.getName());		
		setDocStatus( DOCSTATUS_Voided );
        setDocAction( DOCACTION_None );

        return true;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( "closeIt - " + toString());
        
        // Cierra todas las líneas
        MCashLine[] lines = getLines(false);
        for (MCashLine cashLine : lines) {
			if (!cashLine.processIt(ACTION_Close)) {
				m_processMsg = "@CashLineProcessError@ # " + cashLine.getLine()
						+ ": " + cashLine.getProcessMsg();
			} else if (!cashLine.save()) {
				m_processMsg = "@CashLineProcessError@ # " + cashLine.getLine()
						+ ": " + CLogger.retrieveErrorAsString();
			}
			if (m_processMsg != null) {
				return false;
			}
		}
        
        setDocAction( DOCACTION_None );
        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        log.info( "reverseCorrectIt - " + toString());

        return false;
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( "reverseAccrualIt - " + toString());

        return false;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( "reActivateIt - " + toString());
        setProcessed( false );

        if( reverseCorrectIt()) {
            return true;
        }

        return false;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        String sql = "UPDATE C_CashLine SET Processed='" + ( processed
                ?"Y"
                :"N" ) + "' WHERE C_Cash_ID=" + getC_Cash_ID();
        int noLine = DB.executeUpdate( sql,get_TrxName());

        m_lines = null;
        log.fine( "setProcessed - " + processed + " - Lines=" + noLine );
    }    // setProcessed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        StringBuffer sb = new StringBuffer();

        sb.append( getName());

        // : Total Lines = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"BeginningBalance" )).append( "=" ).append( getBeginningBalance()).append( "," ).append( Msg.translate( getCtx(),"EndingBalance" )).append( "=" ).append( getEndingBalance()).append( " (#" ).append( getLines( false ).length ).append( ")" );

        // - Description

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( " - " ).append( getDescription());
        }

        return sb.toString();
    }    // getSummary


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        return getCreatedBy();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getStatementDifference();
    }    // getApprovalAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        return getCashBook().getC_Currency_ID();
    }    // getC_Currency_ID
    
    
    /**
     * Operaciones luego de procesar el documento
     */
    public boolean afterProcessDocument(String processAction,boolean status){
    	
    	// Setear el crédito
    	
    	// Si pudo completar
    	if(processAction.equals(MCash.DOCACTION_Complete) && status){
			// Guardar la caja con el nuevo estado a fin de recalcular
			// correctamente el credito disponible
    		if(!save()){
				log.severe(CLogger.retrieveErrorAsString());
			}
    		// Actualizar el crédito de las entidades comerciales    
    		Set<Integer> keys = getCcBPUpdates().keySet();
    		Map<PO, Object> aditionalResults;
    		for (Integer bpID : keys) {
    			aditionalResults = getCcBPUpdates().get(bpID);
				MBPartner bp = new MBPartner(getCtx(), bpID, get_TrxName());
				// Obtengo el manager actual
				CurrentAccountManager manager = CurrentAccountManagerFactory.getManager(new CurrentAccountDocument() {
					
					private MBPartner bpartner;
					
					public CurrentAccountDocument init(MBPartner bp){
						bpartner = bp;
						return this;
					}
					
					@Override
					public boolean isSkipCurrentAccount() {
						MDocType cashDT = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_CashJournal, get_TrxName());
						return cashDT != null && cashDT.isSkipCurrentAccounts();
					}
					
					@Override
					public boolean isSOTrx() {
						return bpartner.isCustomer();
					}
				}.init(bp));
				// Actualizo el balance
				CallResult result = new CallResult();
				try{
					result = manager.afterProcessDocument(getCtx(),
							new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()),
									get_TrxName()), bp, aditionalResults, get_TrxName());
				} catch(Exception e){
					result.setMsg(e.getMessage(), true);
				} 
				// Si hubo error, obtengo el mensaje y retorno inválido
				if (result.isError()) {
					log.severe(result.getMsg());
				}
    		}
    	}
    	
    	return true;
    	
    }

	/* (non-Javadoc)
	 * @see org.openXpertya.model.X_C_Cash#getCashBookType()
	 */
	@Override
	public String getCashBookType() {
		return MCashBook.get(getCtx(), getC_CashBook_ID(), get_TrxName())
				.getCashBookType();
	}

	@Override
	protected boolean beforeDelete() {
		if (!isProcessed() && containsProcessedLines()) {
			log.saveError("DeleteError", Msg.translate(getCtx(), "CannotDeleteCashProcessedLinesError"));
			return false;
		}
		return true;
	}
    
    /**
     * @return Indica la cantidad de líneas procesadas que tiene este Libro de Caja.
     */
    private boolean containsProcessedLines() {
    	String sql = "SELECT COUNT(*) FROM C_CashLine WHERE Processed = 'Y' AND C_Cash_ID = ?";
    	int count = DB.getSQLValue(get_TrxName(), sql, getC_Cash_ID());
    	return count > 0;
    }

    protected void setCcBPUpdates(Map<Integer, Map<PO,Object>> ccBPUpdates) {
		this.ccBPUpdates = ccBPUpdates;
	}

	protected Map<Integer, Map<PO,Object>> getCcBPUpdates() {
		return ccBPUpdates;
	}

	public boolean isPOSJournalCash() {
		return isPOSJournalCash;
	}

	public void setPOSJournalCash(boolean isPOSJournalCash) {
		this.isPOSJournalCash = isPOSJournalCash;
	}
	
	public boolean updateAmts() {
		String sql = "UPDATE C_Cash c"
				+ " SET StatementDifference="
				+ "(SELECT COALESCE(SUM(currencyConvert(cl.Amount, cl.C_Currency_ID, cb.C_Currency_ID, c.DateAcct, null, c.AD_Client_ID, c.AD_Org_ID)),0) "
				+ "FROM C_CashLine cl, C_CashBook cb "
				+ "WHERE cb.C_CashBook_ID=c.C_CashBook_ID"
				+ " AND cl.C_Cash_ID=c.C_Cash_ID AND cl.IsActive = 'Y' AND (cl.docstatus NOT IN ('DR','IP'))) " 
				+ "WHERE C_Cash_ID=" + getC_Cash_ID();
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.warning( "updateHeader - Difference #" + no );
        }

        // Ending Balance

        sql = "UPDATE C_Cash" 
        		+ " SET EndingBalance = BeginningBalance + StatementDifference " 
        		+ " WHERE C_Cash_ID=" + getC_Cash_ID();
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.warning( "updateHeader - Balance #" + no );
        }

        return no == 1;
    }    // updateHeader

	public int getReverseCashID() {
		return reverseCashID;
	}

	public void setReverseCashID(int reverseCashID) {
		this.reverseCashID = reverseCashID;
	}
}    // MCash



/*
 *  @(#)MCash.java   02.07.07
 * 
 *  Fin del fichero MCash.java
 *  
 *  Versión 2.2
 *
 */
