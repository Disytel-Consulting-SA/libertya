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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAllocationHdr extends X_C_AllocationHdr implements DocAction {

	// -------------------------------------------------------------------------------
	// Matías Cap - Disytel - 2010/02 
	// Migrado de VDocAction
	// -------------------------------------------------------------------------------
	// Franco Bonafine - Disytel - 2009/03
    // Atributos específicos para el Mapping de Acciónes de Allocations a Estandars. 
	private static Map<String, String> allocActionMapping;
	static {
		allocActionMapping = new HashMap<String, String>();
		// RX -> RC
		allocActionMapping.put
			(MAllocationHdr.ALLOCATIONACTION_RevertAllocation, 
			 DocumentEngine.ACTION_Reverse_Correct);
		// VP -> VO
		allocActionMapping.put
			(MAllocationHdr.ALLOCATIONACTION_VoidPayments, 
			 DocumentEngine.ACTION_Void);
		// VR -> VO
		allocActionMapping.put
			(MAllocationHdr.ALLOCATIONACTION_VoidPaymentsRetentions, 
			 DocumentEngine.ACTION_Void);
	}
	
	/**
	 * @param allocationAction
	 *            acción de allocation
	 * @return el doc action relacionado con el allocation action parámetro
	 */
	public static String getDocActionByAllocationAction(String allocationAction){
		return allocActionMapping.get(allocationAction);
	}
	
	// Variables de instancia
	
	/**
	 * Booleano que determina si al completar esta factura se debe actualizar el
	 * saldo de cuenta corriente del cliente
	 */
	private boolean updateBPBalance = true;
	
	/**
	 * Map de PO con los trabajos adicionales en el caso de haber modificado los
	 * payments, cashlines y/o retenciones del allocations, coo por ejemplo al
	 * anular el allocation
	 */
	private Map<PO, Object> aditionalWorks;
	
	/**
	 * Booleano que determina si se debe confimar el trabajo adicional de cuenta
	 * corriente al procesar el/los documento/s
	 */
	private boolean confirmAditionalWorks = true;
	
	/** Cuenta cuántos allocation hay creados con este pago
	 * 
	 * @param pay Pago
	 * @param trxName
	 * @return True si no hay allocation creadas con este pago.
	 */
	public static int CanCreateAllocation(MPayment pay, String trxName) {
		
		int x = DB.getSQLValue(trxName, 
    			" SELECT COUNT(*) as alloccount " + 
    			" FROM C_AllocationLine AS l " + 
    			" INNER JOIN C_AllocationHdr AS h ON (h.C_AllocationHdr_ID=l.C_AllocationHdr_ID) " + 
    			" WHERE C_Payment_ID = ? AND h.IsActive='Y' ", 
    			pay.getC_Payment_ID() );
		
		return x;
	}

	/** Cuenta cuántos allocation hay creados con esta linea de caja
	 * 
	 * @param line Linea de caja
	 * @param trxName
	 * @return True si no hay allocation creadas con esta linea de caja. 
	 */
	public static int CanCreateAllocation(MCashLine line, String trxName) {
		
		int x = DB.getSQLValue(trxName, 
    			" SELECT COUNT(*) as alloccount " + 
    			" FROM C_AllocationLine AS l " + 
    			" INNER JOIN C_AllocationHdr AS h ON (h.C_AllocationHdr_ID=l.C_AllocationHdr_ID) " + 
    			" WHERE C_CashLine_ID = ? AND h.IsActive='Y' ", 
    			line.getC_CashLine_ID() );
		
    	return x;
	}
	

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Payment_ID
     * @param trxName
     *
     * @return
     */

    public static MAllocationHdr[] getOfPayment( Properties ctx,int C_Payment_ID,String trxName ) {
        String sql = "SELECT * FROM C_AllocationHdr h " + "WHERE IsActive='Y'" + " AND EXISTS (SELECT * FROM C_AllocationLine l " + "WHERE h.C_AllocationHdr_ID=l.C_AllocationHdr_ID AND l.C_Payment_ID=?)";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,C_Payment_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAllocationHdr( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getOfPayment",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MAllocationHdr[] retValue = new MAllocationHdr[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfPayment

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Invoice_ID
     * @param trxName
     *
     * @return
     */

    public static MAllocationHdr[] getOfInvoice( Properties ctx,int C_Invoice_ID,String trxName ) {
        String sql = "SELECT * FROM C_AllocationHdr h " + "WHERE IsActive='Y'" + " AND EXISTS (SELECT * FROM C_AllocationLine l " + "WHERE h.C_AllocationHdr_ID=l.C_AllocationHdr_ID AND l.C_Invoice_ID=?)";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,C_Invoice_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAllocationHdr( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getOfInvoice",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MAllocationHdr[] retValue = new MAllocationHdr[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfInvoice

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MAllocationHdr.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_AllocationHdr_ID
     * @param trxName
     */

    public MAllocationHdr( Properties ctx,int C_AllocationHdr_ID,String trxName ) {
        super( ctx,C_AllocationHdr_ID,trxName );

        if( C_AllocationHdr_ID == 0 ) {

            // setDocumentNo (null);

            setDateTrx( new Timestamp( System.currentTimeMillis()));
            setDateAcct( getDateTrx());
            setDocAction( DOCACTION_Complete );    // CO
            setDocStatus( DOCSTATUS_Drafted );     // DR

            // setC_Currency_ID (0);

            setApprovalAmt( Env.ZERO );
            setIsApproved( false );
            setIsManual( false );

            //

            setPosted( false );
            setProcessed( false );
            setProcessing( false );
        }
    }    // MAllocation

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param IsManual
     * @param DateTrx
     * @param C_Currency_ID
     * @param description
     * @param trxName
     */

    public MAllocationHdr( Properties ctx,boolean IsManual,Timestamp DateTrx,int C_Currency_ID,String description,String trxName ) {
        this( ctx,0,trxName );
        setIsManual( IsManual );

        if( DateTrx != null ) {
            setDateTrx( DateTrx );
            setDateAcct( DateTrx );
        }

        setC_Currency_ID( C_Currency_ID );

        if( description != null ) {
            setDescription( description );
        }
    }    // create Allocation

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAllocationHdr( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAllocation

    /** Descripción de Campos */

    private MAllocationLine[] m_lines = null;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MAllocationLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        //

        String sql = "SELECT * FROM C_AllocationLine WHERE C_AllocationHdr_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_AllocationHdr_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MAllocationLine line = new MAllocationLine( getCtx(),rs,get_TrxName());

                line.setParent( this );
                list.add( line );
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

        //

        m_lines = new MAllocationLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCashTrx() {
        getLines( false );

        for( int i = 0;i < m_lines.length;i++ ) {
            if( m_lines[ i ].isCashTrx()) {
                return true;
            }
        }

        return false;
    }    // isCashTrx

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        if( getID() == 0 ) {
            return;
        }

        String sql = "UPDATE C_AllocationHdr SET Processed='" + ( processed
                ?"Y"
                :"N" ) + "' WHERE C_AllocationHdr_ID=" + getC_AllocationHdr_ID();
        int no = DB.executeUpdate( sql,get_TrxName());

        m_lines = null;
        log.fine( "setProcessed - " + processed + " - #" + no );
    }    // setProcessed

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Changed from Not to Active

        if( !newRecord && is_ValueChanged( "IsActive" ) && isActive()) {
            log.severe( "Cannot Re-Activate deactivated Allocations" );

            return false;
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        String trxName = get_TrxName();

        if( (trxName == null) || (trxName.length() == 0) ) {
            log.warning( "No transaction" );
        }

        if( isPosted()) {
            if( !MPeriod.isOpen( getCtx(),getDateTrx(),MDocType.DOCBASETYPE_PaymentAllocation )) {
                log.warning( "Period Closed" );

                return false;
            }

            setPosted( false );

            if( MFactAcct.delete( Table_ID,getID(),trxName ) < 0 ) {
                return false;
            }
        }

        // Mark as Inactive

        setIsActive( false );

        String sql = "UPDATE C_AllocationHdr SET IsActive='N' WHERE C_AllocationHdr_ID=" + getC_AllocationHdr_ID();

        DB.executeUpdate( sql,trxName );

        // Unlink

        getLines( true );

        for( int i = 0;i < m_lines.length;i++ ) {
            MAllocationLine line = m_lines[ i ];

            if( !line.delete( true,trxName )) {
                return false;
            }
        }

        return true;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        return success;
    }    // afterSave

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
        
        status = this.afterProcessDocument(engine.getDocAction(), status) && status;

		return status;        
    }    // processIt

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

        if( !MPeriod.isOpen( getCtx(),getDateAcct(),MDocType.DOCBASETYPE_PaymentAllocation )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        getLines( false );

        if( m_lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Add up Amounts & validate

        BigDecimal approval = Env.ZERO;

        for( int i = 0;i < m_lines.length;i++ ) {
            MAllocationLine line = m_lines[ i ];

            approval = approval.add( line.getWriteOffAmt()).add( line.getDiscountAmt());

            // Make sure there is BP

            if( line.getC_BPartner_ID() == 0 ) {
                m_processMsg = "No Business Partner";

                return DocAction.STATUS_Invalid;
            }
        }

        setApprovalAmt( approval );

        //

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

        if( !m_justPrepared ) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        log.info( toString());

        // Link

        getLines( false );

        for( int i = 0;i < m_lines.length;i++ ) {
            MAllocationLine line = m_lines[ i ];

            line.set_TrxName( get_TrxName());
            line.processIt( false );    // not reverse
        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        setProcessed( true );
        setDocAction( DOCACTION_Close );

        // Checks invoiceOpen
        MAllocationLine[] lines = getLines(true);
        for (int i = 0; i < lines.length; i++) {
			int c_Invoice_ID = lines[i].getC_Invoice_ID();
			if (c_Invoice_ID != 0) {
				String sql = " SELECT invoiceOpen(C_Invoice_ID, 0) " +
					         " FROM C_Invoice WHERE C_Invoice_ID=?";
				BigDecimal open = DB.getSQLValueBD(get_TrxName(), sql, c_Invoice_ID);

				if ((open != null) && (open.signum() == 0)) {
					sql = " UPDATE C_Invoice SET IsPaid='Y' " +
						  " WHERE C_Invoice_ID=" + c_Invoice_ID;

					DB.executeUpdate(sql, get_TrxName());
				} else {
					log.config("Invoice #" + i + " is not paid - " + open);
				}
			}
		}
        
		// Caja Diaria. Intenta registrar el documento
		if (!MPOSJournal.registerDocument(this)) {
			m_processMsg = MPOSJournal.DOCUMENT_COMPLETE_ERROR_MSG;
			return STATUS_Invalid;
		}
        
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
        log.info( "voidIt - " + toString());

		// Si la asignación fue generada por una línea de caja cuyo tipo de
		// efectivo es Factura (asignación a factura) entonces no se puede anular directamente.
        // (el ProcessMsg lo setea el método en caso de error).
        if (!validateOwnerCashLine()) {
			return false;
        }        
        setAditionalWorks(new HashMap<PO, Object>());
        
        try {
	        // Acción específica de Allocations: Anular Pagos o Anular Pagos y Retenciones
	        // Se deben anular los pagos involucrados en las líneas.
	        if (ALLOCATIONACTION_VoidPayments.equals(getAllocationAction())
	        		|| ALLOCATIONACTION_VoidPaymentsRetentions.equals(getAllocationAction())) 
	        	// Anulación de pagos
	        	voidPayments();
	        
		    // Acción específica de Allocations: Anular Pagos y Retenciones
	        // Se deben anular las retenciones asociadas a esta asignación.
	        if (ALLOCATIONACTION_VoidPaymentsRetentions.equals(getAllocationAction())) 
	        	// Anulación de retenciones
	        	voidRetentions();
	        
	        
	        
        } catch (Exception e) {
        	m_processMsg = e.getMessage();
        	return false;
		}
        
        boolean retValue = reverseIt();

        setDocAction( DOCACTION_None );

        return retValue;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( toString());
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
        log.info( toString());

		// Si la asignación fue generada por una línea de caja cuyo tipo de
		// efectivo es Factura (asignación a factura) entonces no se puede anular directamente.
        // (el ProcessMsg lo setea el método en caso de error).
        if (!validateOwnerCashLine()) {
			return false;
        }
        
        boolean retValue = reverseIt();

        setDocAction( DOCACTION_None );

        return retValue;
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( toString());

        boolean retValue = reverseIt();

        setDocAction( DOCACTION_None );

        return retValue;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( "reActivateIt - " + toString());

		/*
		 * FB - Disytel 
		 * Codificado solo para la utilización de Corrección de Cobros
		 * (MPaymentFix). No está validado para la re-apertura de una asignación
		 * para cualquier otro caso aunque probablemente sea correcta la lógica
		 * igualmente.
		 */
        
        MAllocationLine[] lines = getLines(false);
        // Invierte los cambios realizados al completar la línea
        for (MAllocationLine line : lines) {
			line.processIt(true);
			if (!line.save()) {
				m_processMsg = "@AllocationLineSaveError@: " + CLogger.retrieveErrorAsString();
				return false;
			}
		}
        
        // Borra la contabilidad si es que ya fue generada.
        deletePosting();
        setPosted(false);
        
        setDocAction(DOCACTION_Complete);
        return true;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAllocationHdr[" );

        sb.append( getID()).append( "-" ).append( getSummary()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        StringBuffer sb = new StringBuffer();

        sb.append( getDocumentNo());

        // : Total Lines = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"ApprovalAmt" )).append( "=" ).append( getApprovalAmt()).append( " (#" ).append( getLines( false ).length ).append( ")" );

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

    private boolean reverseIt() {
        if( !isActive()) {
            throw new IllegalStateException( "Allocation already reversed (not active)" );
        }

        // Can we delete posting

        if( !MPeriod.isOpen( getCtx(),getDateTrx(),MPeriodControl.DOCBASETYPE_PaymentAllocation )) {
            throw new IllegalStateException( "@PeriodClosed@" );
        }

        // Set Inactive

        setIsActive( false );
        setDocumentNo( getDocumentNo() + "^" );
        setDocStatus( DOCSTATUS_Reversed );    // for direct calls

        if( !save() || isActive()) {
            throw new IllegalStateException( "Cannot de-activate allocation" );
        }

        // Delete Posting
        deletePosting();

        // Unlink

        getLines( true );

        for( int i = 0;i < m_lines.length;i++ ) {
            MAllocationLine line = m_lines[ i ];

            line.setIsActive( false );
            line.save( get_TrxName());

            if( !line.processIt( true )) {
                return false;
            }
        }

        return true;
    }    // reverse
    
    /**
     * @return Devuelve verdadero si este Allocatios es una OP o RC adelantado.
     */
    public boolean isAdvanced() {
    	return ALLOCATIONTYPE_AdvancedCustomerReceipt.equals(getAllocationType())
    	       || ALLOCATIONTYPE_AdvancedPaymentOrder.equals(getAllocationType());
    }
    
    /**
     * Disytel - Franco Bonafine
     * Anulación de pagos involucrados en esta asignación.
     */
    private void voidPayments() throws Exception {
        MAllocationLine[] lines = getLines(true);
        List<MPayment> pays = new ArrayList<MPayment>();
       
        // Si hay pagos conciliados, no se puede anular.
        MPayment payment = null;
        for (int i = 0; i < lines.length; i++ ) {
        	int C_Payment_ID = lines[i].getC_Payment_ID();
        	if (C_Payment_ID != 0) {
        		payment = new MPayment(getCtx(), C_Payment_ID, get_TrxName());
        		pays.add(payment);
        		if (payment.isReconciled()) 
        			throw new Exception("@PaymentsReconciledError@");
        	}
        }

        // Se comprueba que cada pago no esté asociado con otra asignación que no
    	// sea la que actualmente se está anulando.
    	Integer[] except = new Integer[] { getC_AllocationHdr_ID() };
    	List<MPayment> allocatedPayments = new ArrayList<MPayment>();
    	for (MPayment paym : pays) {
    		if (paym.isInAllocation(except)) {
    			allocatedPayments.add(paym);
    		}
		}
    	// Si la asignación contiene pagos con dicha condición, entonces el proceso
    	// finaliza con error.
    	if (allocatedPayments.size() > 0) {
    		StringBuffer msg = new StringBuffer();
    		msg.append("@PaymentsAllocatedError@ (@Payments@: ");
    		for (Iterator<MPayment> allocPays = allocatedPayments.iterator(); allocPays
					.hasNext();) {
				MPayment paym = (MPayment) allocPays.next();
				msg.append(paym.getDocumentNo());
				msg.append(allocPays.hasNext() ? ", " : ")");
			}
    		throw new Exception(msg.toString());
    	}
    	
    	// Todos los pagos están liberados, se procede a anular cada uno de ellos.
        for (MPayment paym : pays) {
    		String errorMsg = null;
        	// En caso de error en el procesamiento de la anulación o el guardado
    		// de los cambios en el pago, el proceso de anulación de asignación
    		// se aborta.
    		paym.setConfirmAditionalWorks(false);
    		paym.setVoiderAllocationID(getC_AllocationHdr_ID());
    		if (!paym.processIt( DocAction.ACTION_Void ))
    			errorMsg = paym.getProcessMsg();
    		if (!paym.save())
    			errorMsg = CLogger.retrieveErrorAsString();
    		if (errorMsg != null) 
    			throw new Exception("@VoidPaymentError@ (" + paym.getDocumentNo() + "): " + errorMsg);
    		// Registro de los trabajos adicionales al anular el payment
    		if(isUpdateBPBalance()){
    			getAditionalWorks().putAll(paym.getAditionalWorkResult());
    		}
		}
        
        // Se anulan las líneas de caja involucradas en esta asignación.
        voidCashLines();
    }
    
    /**
     * Disytel - Franco Bonafine
     * Anulación de líneas de caja involucrados en esta asignación.
     */
    private void voidCashLines() throws Exception {
        MAllocationLine[] lines = getLines(false);
        List<MCashLine> cashLines = new ArrayList<MCashLine>();

        // Si el libro de caja al que pertenece la línea está procesado
        // no es posible anular.
        for (int i = 0; i < lines.length; i++ ) {
        	MCashLine cashLine = null;
        	int C_CashLine_ID = lines[i].getC_CashLine_ID();
        	if (C_CashLine_ID != 0) {
        		cashLine = new MCashLine(getCtx(), C_CashLine_ID, get_TrxName());
        		cashLines.add(cashLine);
        		String cashStatus = cashLine.getCash().getDocStatus();
        		// No es posible anular líneas de caja cuyo libro se encuentre
        		// procesado.
        		if (STATUS_Completed.equals(cashStatus)
        				|| STATUS_Closed.equals(cashStatus)
        				|| cashLine.getCash().isProcessed()) 
        			throw new Exception("@CashProcessedError@");
        	}
        }
        
        // Se anulan las líneas de caja
        for (MCashLine cashLine : cashLines) {
        	cashLine.setConfirmAditionalWorks(false);
			cashLine.setVoiderAllocationID(getC_AllocationHdr_ID());
			cashLine.setIgnoreInvoiceOpen(true);
        	if (!DocumentEngine.processAndSave(cashLine, MCashLine.ACTION_Void,
					false)) {
				throw new Exception("@VoidCashLineError@ ($ "
						+ cashLine.getAmount() + "): "
						+ cashLine.getProcessMsg());
			}
        	// Registro de los trabajos adicionales al anular el payment
    		if(isUpdateBPBalance()){
    			getAditionalWorks().putAll(cashLine.getAditionalWorkResult());
    		}
		}
    }
    
    /**
     * Disytel - Franco Bonafine
     * Anulación de retenciones involucrados en esta asignación.
     */
    private void voidRetentions() throws Exception {
        // Se obtienen los comprobantes de crédito y débito de cada retención
    	String sql = 
        	" SELECT C_Invoice_ID, C_Invoice_Retenc_ID " +
        	" FROM m_retencion_invoice " +
        	" WHERE C_AllocationHdr_ID = ? ";
        PreparedStatement pstmt = null; 
        ResultSet rs = null;
        
        try {
        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	pstmt.setInt(1, getC_AllocationHdr_ID());
        	rs = pstmt.executeQuery();
        	// Por cada retención se anula tanto el crédito como el débito.
        	while (rs.next()) {
        		MInvoice invoice = new MInvoice(getCtx(), rs.getInt("C_Invoice_ID"), get_TrxName());
        		MInvoice invoiceRetenc = new MInvoice(getCtx(), rs.getInt("C_Invoice_Retenc_ID"), get_TrxName());
        		// Se anula ambos comprobantes de la retención
        		// C_Invoice_ID (Crédito Cliente/Proveedor)
        		voidRetentionInvoice(invoice);
        		// C_Invoice_Retenc_ID (Factura al Fisco)
        		voidRetentionInvoice(invoiceRetenc);
        	}
        } catch (SQLException e) {
        	log.log(Level.SEVERE, "voidIt->voidRetentions", e);
        	throw new Exception();
        } finally {
        	try {
        		if (rs != null) rs.close();
        		if (pstmt != null) pstmt.close();
        	} catch (Exception e) {}
        }
    }

    /**
     * Disytel - Franco Bonafine
     * Anulación de una factura que representa una retención.
     */
    private void voidRetentionInvoice(MInvoice invoice) throws Exception {
    	String errorMsg = null;
    	invoice.setConfirmAditionalWorks(false);
    	invoice.setVoiderAllocationID(getC_AllocationHdr_ID());
		if (!invoice.processIt( DocAction.ACTION_Void ))
			errorMsg = invoice.getProcessMsg();
		if (!invoice.save())
			errorMsg = CLogger.retrieveErrorAsString();
		if (errorMsg != null) 
			throw new Exception("@VoidRetentionError@ (" + invoice.getDocumentNo() + "): " + errorMsg);
		// Registro de los trabajos adicionales al anular el payment
		if(isUpdateBPBalance()){
			getAditionalWorks().putAll(invoice.getAditionalWorkResult());
		}
    }
    
    
	public void setUpdateBPBalance(boolean updateBPBalance) {
		this.updateBPBalance = updateBPBalance;
	}

	public boolean isUpdateBPBalance() {
		return updateBPBalance;
	} 
    
	/**
	 * Operaciones luego de procesar el documento
	 */
	public boolean afterProcessDocument(String processAction, boolean status) {

		// Setear el crédito de la entidad comercial
		if ((getDocStatus().equals(MInvoice.DOCSTATUS_Completed)
				|| getDocStatus().equals(MInvoice.DOCSTATUS_Reversed) || getDocStatus()
				.equals(MInvoice.DOCSTATUS_Voided)) 
				&& status) {
			// Guardar el payment con el nuevo estado a fin de recalcular
			// correctamente el crédito de la entidad comercial
			this.save();
			if (!Util.isEmpty(getC_BPartner_ID(), true) && isUpdateBPBalance()) {
				MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
				// Obtengo el manager actual
				CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
				// Si es completar actualizar el saldo
				if(getDocStatus().equals(MInvoice.DOCSTATUS_Completed)){
					// Actualizo el balance
					CallResult result = manager.updateBalanceAndStatus(getCtx(),
							new MOrg(getCtx(), getAD_Org_ID(), get_TrxName()), bp,
							get_TrxName());				
					// Si hubo error, obtengo el mensaje y retorno inválido
					if (result.isError()) {
						log.severe(result.getMsg());
					}
				}
				// Si es anulaciones iterar por los trabajos adicionales de
				// cuenta corriente en el caso de haber anulado también payments
				// y/o retenciones
				else if(isConfirmAditionalWorks()){
					// Actualizo el balance
					CallResult result = manager.afterProcessDocument(getCtx(),
							new MOrg(getCtx(), getAD_Org_ID(), get_TrxName()),
							bp, getAditionalWorks(), get_TrxName());
					// Si hubo error, obtengo el mensaje y retorno inválido
					if (result.isError()) {
						log.severe(result.getMsg());
					}
				}
			}
		}
		return true;
	}

	/**
	 * @return Devuelve la línea de caja que generó está asignación o
	 *         <code>null</code> si esta asignación no fue generada por el
	 *         procesamiento de una línea de caja.
	 */
	private MCashLine getCashLine() {
		String sql = "SELECT C_CashLine_ID FROM C_CashLine WHERE C_AllocationHdr_ID = ?";
		int cashLineID = DB.getSQLValue(get_TrxName(), sql, getC_AllocationHdr_ID());
		MCashLine cashLine = null;
		if (cashLineID > 0) {
			cashLine = new MCashLine(getCtx(), cashLineID, get_TrxName());
		}
		return cashLine;
	}

	/**
	 * Verfica y valida si la asignación fue generada por una línea de caja cuyo
	 * tipo de efectivo es Factura (asignación a factura) para saber si es
	 * posible anular o no directamente esta asignación
	 * 
	 * @return <code>true</code> si es posible anular, <code>false</code> sino.
	 */
	private boolean validateOwnerCashLine() {
        MCashLine ownerCashLine = getCashLine();
        if (ownerCashLine != null && ownerCashLine.getC_CashLine_ID() != getVoiderCashLineID()) {
			m_processMsg = Msg.getMsg(getCtx(),
					"CashLineGeneratedAllocationVoidError",
					new Object[] { ownerCashLine.getCash().getName() + " - "
							+ Msg.translate(getCtx(), "Line") + " # "
							+ ownerCashLine.getLine() });
			return false;
        }
		return true;
	}
	
	/**
	 * ID de la línea de caja que inicia la anulación de esta asignación. Esto
	 * permite hacer un bypass en la anulación de asignación solo permitiendo
	 * anular la asignación generada por una línea de caja, si es justamente esa
	 * línea de caja la que está intentando anular o revertir la asignación
	 */
	private int voiderCashLineID = 0;

	/**
	 * @return el valor de voiderCashLineID
	 */
	public int getVoiderCashLineID() {
		return voiderCashLineID;
	}

	/**
	 * @param voiderCashLineID el valor de voiderCashLineID a asignar
	 */
	public void setVoiderCashLineID(int voiderCashLineID) {
		this.voiderCashLineID = voiderCashLineID;
	}

	/**
	 * Actualiza el total del allocation en base a la suma de los allocation
	 * lines
	 */
	public void updateTotalByLines(){
		MAllocationLine[] lines = getLines(true);
		BigDecimal amt = BigDecimal.ZERO;
		for (MAllocationLine mAllocationLine : lines) {
			amt = amt.add(mAllocationLine.getAmount());
		}
		setGrandTotal(amt);
	}

	public void setAditionalWorks(Map<PO, Object> aditionalWorks) {
		this.aditionalWorks = aditionalWorks;
	}

	public Map<PO, Object> getAditionalWorks() {
		return aditionalWorks;
	}

	public void setConfirmAditionalWorks(boolean confirmAditionalWorks) {
		this.confirmAditionalWorks = confirmAditionalWorks;
	}

	public boolean isConfirmAditionalWorks() {
		return confirmAditionalWorks;
	}
	
	protected void deletePosting() {
        if (isPosted()) {
			String sql = "DELETE FROM Fact_Acct WHERE AD_Table_ID=" + MAllocationHdr.Table_ID + " AND Record_ID=" + getC_AllocationHdr_ID();
	        int no = DB.executeUpdate( sql,get_TrxName());
	
	        log.fine( "Fact_Acct deleted #" + no );
        }
	}
}    // MAllocation



/*
 *  @(#)MAllocationHdr.java   02.07.07
 * 
 *  Fin del fichero MAllocationHdr.java
 *  
 *  Versión 2.2
 *
 */
