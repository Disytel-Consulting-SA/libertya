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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AuxiliarDTO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MCashLine extends X_C_CashLine implements DocAction {

	/** Bypass para que el completeIt no cree el allocation ya que el cliente se encargará
	 *  de crearlo. Si se usa esta opción quien cree y complete la línea será el responsable
	 *  de crear el allocation correctamente, sino quedará inconsistente. */
	private boolean ignoreAllocCreate = false;
		
	/**
	 * Resultado de la llamada de cuenta corriente que realiza trabajo adicional
	 * al procesar un documento. Al anular un cashline y crearse un cashline
	 * reverso, se debe guardar dentro de esta map también.
	 */	
	private Map<PO, Object> aditionalWorkResult;
	
	/**
	 * Booleano que determina si se debe confimar el trabajo adicional de cuenta
	 * corriente al procesar el/los documento/s
	 */
	private boolean confirmAditionalWorks = true;
	
	/**
	 * ID de la asignación que intenta anular esta línea de caja. En el caso de
	 * que desde una asignación se quiera anular una línea de caja, es necesario
	 * que esta línea sepa cual es la asignación que lo está anulando para
	 * evitar la validación de asignaciones realizada por
	 * {@link #validateAllocations()}, de modo que la asignación anuladora no se
	 * tenga en cuenta en la validación.
	 */
    private Integer voiderAllocationID = 0;
    
	/**
	 * Boolean que determina si hay que ignorar la validación de monto pendiente
	 * de la factura al completar la línea del libro de caja
	 */
    private boolean ignoreInvoiceOpen = false;

	/**
	 * Boolean que determina si se deben ignorar las tareas relacionadas con
	 * cajas diarias
	 */
    private boolean ignorePOSJournal = false;
	
    /**
	 * Caja diaria a asignar al contra-documento que se genera al anular este
	 * documento
	 */
	private Integer voidPOSJournalID = 0;

	/**
	 * Control que se agrega para obligatoriedad de apertura de la caja diaria
	 * asignada al contra-documento. Es decir que si este control se debe
	 * realizar y existe un valor en la caja a asignar para el contra-documento,
	 * entonces esa caja diaria debe estar abierta, sino error
	 */
	private boolean voidPOSJournalMustBeOpen = false; 
    
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_CashLine_ID
     * @param trxName
     */

    public MCashLine( Properties ctx,int C_CashLine_ID,String trxName ) {
        super( ctx,C_CashLine_ID,trxName );

        if( C_CashLine_ID == 0 ) {

            // setLine (0);
            // setCashType (CASHTYPE_GeneralExpense);

            setAmount( Env.ZERO );
            setDiscountAmt( Env.ZERO );
            setWriteOffAmt( Env.ZERO );
            setIsGenerated( false );
            
            setDocStatus(DOCSTATUS_Drafted);
            setDocAction(DOCACTION_Complete);
        }
    }    // MCashLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCashLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCashLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param cash
     */

    public MCashLine( MCash cash ) {
        this( cash.getCtx(),0,cash.get_TrxName());
        setClientOrg( cash );
        setC_Cash_ID( cash.getC_Cash_ID());
    }    // MCashLine

    /** Descripción de Campos */

    private Integer m_cashC_Currency_ID = null;

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     */

    public void setInvoice( MInvoice invoice ) {
        setC_Invoice_ID( invoice.getC_Invoice_ID());
        setCashType( CASHTYPE_Invoice );
        setC_Currency_ID( invoice.getC_Currency_ID());
        setC_Project_ID(invoice.getC_Project_ID());
        
        // Amount

        MDocType   dt  = MDocType.get( getCtx(),invoice.getC_DocType_ID());
        BigDecimal amt = invoice.getGrandTotal();

        if( MDocType.DOCBASETYPE_APInvoice.equals( dt.getDocBaseType()) || MDocType.DOCBASETYPE_ARCreditMemo.equals( dt.getDocBaseType())) {
            amt = amt.negate();
        }

        setAmount( amt );

        //

        setDiscountAmt( Env.ZERO );
        setWriteOffAmt( Env.ZERO );
        setIsGenerated( true );
        setC_POSPaymentMedium_ID(invoice.getC_POSPaymentMedium_ID());
    }    // setInvoiceLine

    /**
     * Descripción de Método
     *
     *
     * @param order
     * @param trxName
     */

    public void setOrder( MOrder order,String trxName ) {
        setCashType( CASHTYPE_Invoice );
        setC_Currency_ID( order.getC_Currency_ID());

        // Amount

        BigDecimal amt = order.getGrandTotal();

        setAmount( amt );
        setDiscountAmt( Env.ZERO );
        setWriteOffAmt( Env.ZERO );
        setIsGenerated( true );

        //

        if( MOrder.DOCSTATUS_WaitingPayment.equals( order.getDocStatus())) {
            save( trxName );
            order.setC_CashLine_ID( getC_CashLine_ID());
            order.processIt( MOrder.ACTION_WaitComplete );
            order.save( trxName );

            // Set Invoice

            MInvoice[] invoices = order.getInvoices();
            int        length   = invoices.length;

            if( length > 0 ) {    // get last invoice
                setC_Invoice_ID( invoices[ length - 1 ].getC_Invoice_ID());
            }
        }
    }                             // setInvoiceLine

    private MCash m_cash = null;
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MCash getCash() {
        if (m_cash == null) {
        	m_cash = new MCash( getCtx(),getC_Cash_ID(),get_TrxName());
        }
        return m_cash;
    }    // getCash

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStatementDate() {
        return getCash().getStatementDate();
    }    // getStatementDate

	/**
	 * @return Crea y devuelve una línea de caja inversa a esta línea de modo
	 *         que corrija el saldo del libro y la contabilidad de la misma
	 */
    public MCashLine createReversal(int cashID) {
        MCashLine reversal = new MCashLine( getCtx(),0,get_TrxName());

        reversal.setClientOrg( this );
        reversal.setC_Cash_ID( cashID );
        reversal.setC_BankAccount_ID( getC_BankAccount_ID());
        reversal.setC_Charge_ID( getC_Charge_ID());
        reversal.setC_Currency_ID( getC_Currency_ID());
        reversal.setC_Invoice_ID( getC_Invoice_ID());
        reversal.setCashType( getCashType());
        reversal.setDescription( getDescription());
        reversal.setIsGenerated( true );
        reversal.setC_BPartner_ID(getC_BPartner_ID());
        reversal.setC_Project_ID(getC_Project_ID());
        reversal.setIsAllocated(isAllocated());
        reversal.setIgnoreAllocCreate(true);
        reversal.setTransferCash_ID(getTransferCash_ID());
        //

        reversal.setAmount( getAmount().negate());

        if( getDiscountAmt() == null ) {
            setDiscountAmt(BigDecimal.ZERO);
        } else {
            reversal.setDiscountAmt(getDiscountAmt().negate());
        }

        if( getWriteOffAmt() == null ) {
            setWriteOffAmt(BigDecimal.ZERO);
        } else {
            reversal.setWriteOffAmt( getWriteOffAmt().negate());
        }

        return reversal;
    }    // reverse

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getCashC_Currency_ID() {
        if( m_cashC_Currency_ID != null ) {
            return m_cashC_Currency_ID.intValue();
        }

        String sql = "SELECT cb.C_Currency_ID " + "FROM C_CashBook cb INNER JOIN C_Cash c ON (cb.C_CashBook_ID=c.C_CashBook_ID) " + "WHERE c.C_Cash_ID=?";
        int C_Currency_ID = DB.getSQLValue( get_TrxName(),sql,getC_Cash_ID());

        m_cashC_Currency_ID = new Integer( C_Currency_ID );

        //

        return m_cashC_Currency_ID.intValue();
    }    // getCashC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {

        // Cannot Delete generated Invoices

        Boolean generated = ( Boolean )get_ValueOld( "IsGenerated" );

        if( (generated != null) && generated.booleanValue()) {
            if( get_ValueOld( "C_Invoice_ID" ) != null ) {
                log.warning( "Cannot delete line with generated Invoice" );

                return false;
            }
        }

        return true;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( !success ) {
            return success;
        }

        // A partir del agregado de estados a la línea, la actualización del header
        // se hace luego de completar la línea.
        //return updateHeader();
        return true;
    }    // afterDelete

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
  	
    	// Cannot change generated Invoices

        if( is_ValueChanged( "C_Invoice_ID" )) {
            Object generated = get_ValueOld( "IsGenerated" );

            if( (generated != null) && (( Boolean )generated ).booleanValue()) {
                log.warning( "Cannot change line with generated Invoice" );

                return false;
            }
        }

        // ------------------------------
        // Added by Matías Cap - Disytel
        // La entidad comercial en una transferencia bancaria debe ser obligatoria
        if( CASHTYPE_BankAccountTransfer.equals(getCashType())){
        	if(getC_BPartner_ID() == 0){
        		log.saveError("NotSelectedBPartner","");
        		return false;
        	}
        }
        // ------------------------------
        
        // Verify CashType

        if( CASHTYPE_Invoice.equals( getCashType())  ) {
        	if (getC_Invoice_ID() == 0 && !isGenerated()) {	// si es generada, entonces permitir C_Invoice_ID vacio
        		setCashType( CASHTYPE_GeneralExpense );
        	}
        	if (getWriteOffAmt().compareTo(Env.ZERO) != 0)
        	{
        		log.warning( "CashLine Type: Invoice - Setting WriteOffAmt to Zero" );
        		setWriteOffAmt(Env.ZERO);  // Impedir carga Importe del Ajuste para entradas de tipo Factura (posteriormente genera un error en la contabilización)
        	}
        }

        if( CASHTYPE_BankAccountTransfer.equals( getCashType()) && (getC_BankAccount_ID() == 0) ) {
            setCashType( CASHTYPE_GeneralExpense );
        }

        if( CASHTYPE_Charge.equals( getCashType()) && (getC_Charge_ID() == 0) ) {
            setCashType( CASHTYPE_GeneralExpense );
        }
        
        // Transferencia entre cajas
        if (CASHTYPE_CashTransfer.equals(getCashType())) {
        	// Debe ingresar la caja destino
        	if (getTransferCash_ID() == 0) {
	        	log.saveError("SaveError", Msg.translate(getCtx(), "NeedTransferCashError"));
	        	return false;
        	}
        	
        	// La caja destino no puede ser la misma que la que contiene esta línea
        	// (la transferencia se hace entre libros diferentes si o si)
        	if (getCash().getC_Cash_ID() == getTransferCash_ID()) {
        		log.saveError("SaveError", Msg.translate(getCtx(), "InvalidCashTransfer"));
        		return false;
        	}
        	
        	// Las monedas del libro origen y destino deben ser iguales.
        	if (!validateCashTransferCurrency()) {
        		log.saveError("SaveError", Msg.translate(getCtx(), "CashTransferInvalidCurrency"));
        		return false;
        	}
        }

        // Verify Currency

        int C_Currency_ID = getC_Currency_ID();

        if( C_Currency_ID == 0 ) {
            if( CASHTYPE_BankAccountTransfer.equals( getCashType())) {
                C_Currency_ID = DB.getSQLValue( null,"SELECT C_Currency_ID FROM C_BankAccount WHERE C_BankAccount_ID=?",getC_BankAccount_ID());
            } else if( CASHTYPE_Invoice.equals( getCashType())) {
                C_Currency_ID = DB.getSQLValue( get_TrxName(),"SELECT C_Currency_ID FROM C_Invoice WHERE C_Invoice_ID=?",getC_Invoice_ID());
            } else {    // Cash
                C_Currency_ID = getCashC_Currency_ID();
            }

            //

            if( C_Currency_ID > 0 ) {
                setC_Currency_ID( C_Currency_ID );
            } else {
                log.severe( "beforeSave = No currency" );

                return false;
            }
        }

        // Get Line No

        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_CashLine WHERE C_Cash_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getC_Cash_ID());

            setLine( ii );
        }

        /* Si el project no está seteado, tomar el de la cabecera */
        if (getC_Project_ID() == 0)
        	setC_Project_ID(DB.getSQLValue(get_TrxName(), " SELECT C_Project_ID FROM C_Cash WHERE C_Cash_ID = " + getC_Cash_ID()));
                
        return true;
    }    // beforeSave

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
        if( !success ) {
            return success;
        }

        // A partir del agregado de estados a la línea, la actualización del header
        // se hace luego de completar la línea.
        //return updateHeader();
        return true;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updateHeader() {
		String sql = "UPDATE C_Cash c"
				+ " SET StatementDifference="
				+ "(SELECT COALESCE(SUM(currencyConvert(cl.Amount, cl.C_Currency_ID, cb.C_Currency_ID, c.DateAcct, null, c.AD_Client_ID, c.AD_Org_ID)),0) "
				+ "FROM C_CashLine cl, C_CashBook cb "
				+ "WHERE cb.C_CashBook_ID=c.C_CashBook_ID"
				+ " AND cl.C_Cash_ID=c.C_Cash_ID AND cl.IsActive = 'Y' AND (cl.docstatus NOT IN ('DR','IP') OR cl.c_cashline_id = "
				+ getID() + ")) " + "WHERE C_Cash_ID=" + getC_Cash_ID();
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.warning( "updateHeader - Difference #" + no );
        }

        // Ending Balance

        sql = "UPDATE C_Cash" + " SET EndingBalance = BeginningBalance + StatementDifference " + "WHERE C_Cash_ID=" + getC_Cash_ID();
        no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.warning( "updateHeader - Balance #" + no );
        }

        return no == 1;
    }    // updateHeader

	@Override
	public boolean processIt(String action) {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		boolean status = engine.processIt(action, getDocAction(), log);
		status = afterProcessDocument(engine.getDocAction(), status) && status;
		return status;
	}

	@Override
	public String prepareIt() {
		
		// Transferencia a Caja. La Caja destino debe estar en borrador
		MCash transferCash = new MCash(getCtx(), getTransferCash_ID(), get_TrxName());
		if (!DOCSTATUS_Drafted.equals(transferCash.getDocStatus())) {
			m_processMsg = "@TransferCashInvalidStatus@";
			return STATUS_Invalid;
		}

		// Asigna la moneda del Libro
		setC_CashCurrency_ID(getCash().getC_Currency_ID());
		
		// Si la moneda del la línea es diferente a la del libro entonces se
		// valida que haya conversión.
		if (getC_Currency_ID() != getC_CashCurrency_ID() 
			 && MCurrency.currencyConvert(getAmount(),
						getC_Currency_ID(), getC_CashCurrency_ID(),
						getCash().getDateAcct(), 0, getCtx()) == null) {
            m_processMsg = "@NoCurrencyConversion@";
            return DocAction.STATUS_Invalid;
		}
		
		// Validar los montos pendientes de Factura
		// Bypass para que no realice esta validación
		if (!isIgnoreInvoiceOpen() && CASHTYPE_Invoice.equals(getCashType())
				&& getC_Invoice_ID() > 0) {
			BigDecimal open = (BigDecimal)DB.getSQLObject(get_TrxName(), "SELECT invoiceOpen(?,0)", new Object[] {getC_Invoice_ID()});
			if (getAmount().compareTo(open) > 0) {
				//FIXME: pasar a ADMessage
				m_processMsg = "El Importe no puede ser mayor al importe pendiente de la factura. Pendiente: "
						+ open.setScale(2).toString()
						+ ". Si la diferencia es una diferencia de cambio debe primero generar una Nota de Débito o Crédito (según corresponda) e imputar el pago correspondiente a la diferencia ese documento.";
				return STATUS_Invalid;
			}
		}
		
		// Si la moneda del documento es diferente a la de la compañia:
 		// Se valida que exista una tasa de conversión entre las monedas para la fecha de aplicación del documento.
		if (!validateCashLineCurrencyConvert()){
			m_processMsg = "@NoConversionRateDateAcct@";
			return DocAction.STATUS_Invalid;
		}
		
		setDocAction(DOCACTION_Complete);
		return STATUS_InProgress;
	}

	@Override
	public String completeIt() {
        
		try {
			// Caja Diaria y validaciones relacionadas
			if(!isIgnorePOSJournal()){
				posJournalRelated();
			}
			
			// Generación de documentos adicionales según el tipo de Efectivo de la línea
			
			// 1. Pago de Factura
			if (CASHTYPE_Invoice.equals(getCashType())) {
				completeInvoiceCashLine();
	
			// 2. Transferencia a Cuenta Bancaria	
	        } else if (CASHTYPE_BankAccountTransfer.equals(getCashType())) {
	        	completeBankAccountTransferCashLine();
	        
	        // 3. Transferencia a Caja
	        } else if (CASHTYPE_CashTransfer.equals(getCashType())) {
	        	completeCashTransferCashLine();
	        }
			
			// Actualiza los datos del Libro
			updateHeader();
			
			// Verifico si el gestor de cuentas corrientes debe realizar operaciones
			// antes de completar. 
			// **************************************************************
			// IMPORTANTE : Sería conveniente dejar estas líneas a lo último
			// del completar para no generar inconsistencias
			// **************************************************************
			performCreditWork();
			// **************************************************************		
			
			setProcessed(true);
	        setDocAction(DOCACTION_Close);
	        setDocStatus(STATUS_Completed);
			return DOCSTATUS_Completed;
		
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return STATUS_Invalid;
		}
	}

	/**
	 * Realiza las tareas relacionadas con la caja diaria
	 * @throws Exception
	 */
	private void posJournalRelated() throws Exception{
		if(MCashBook.CASHBOOKTYPE_JournalCashBook.equals(getCash().getCashBookType())){
			setC_POSJournal_ID(getVoidPOSJournalID() != 0 ? getVoidPOSJournalID()
					: getC_POSJournal_ID());
			// Si ya tenía una asignada, verificar si está abierta o en verificación
			// Si no se encuentra en ninguno de los dos estados, entonces se setea a
			// 0 para que se asigne la caja diaria actual
			if (getC_POSJournal_ID() != 0
					&& !MPOSJournal.isPOSJournalOpened(getCtx(),
							getC_POSJournal_ID(), get_TrxName())) {
				// Si se debe realizar el control obligatorio de apertura y la caja
				// diaria de anulación está seteada, entonces error
				if(getVoidPOSJournalID() != 0 && isVoidPOSJournalMustBeOpen()){
					throw new Exception(MPOSJournal.POS_JOURNAL_VOID_CLOSED_ERROR_MSG);
				}
				log.severe("POS Journal assigned with ID "+getC_POSJournal_ID()+" is closed");
				setC_POSJournal_ID(0);			
			}
			// Registrar caja diaria para esta línea si no está registrada
			if (getC_POSJournal_ID() == 0 && !MPOSJournal.registerDocument(this)) {
				throw new Exception(MPOSJournal.DOCUMENT_COMPLETE_ERROR_MSG);			
			}
			
			// Si la caja diaria registrada para esta línea no concuerda con la
			// caja diaria que posee el libro de caja, entonces error
			if (MPOSJournal.isActivated()
					&& getCash().getC_POSJournal_ID() != getC_POSJournal_ID()) {
				throw new Exception("@CashPOSJournalDifferentOfCashLine@");
			}
		}
	}
	
	/**
	 * Realiza las tareas particulares de completado de una línea de caja cuyo
	 * tipo es Imputación a una Factura.
	 * 
	 * @throws Exception
	 *             cuando se produce algún error en el guardado o procesado de
	 *             los documentos adicionales generados.
	 */
	private void completeInvoiceCashLine() throws Exception {
    	int allocCount = MAllocationHdr.CanCreateAllocation(this, get_TrxName());
    	
    	if (allocCount == 0 && getC_Invoice_ID() > 0 && !isIgnoreAllocCreate()) {
    		MCash cash = getCash();	
			MAllocationHdr alloc = new MAllocationHdr(getCtx(), false,
					cash.getDateAcct(), getC_Currency_ID(), Msg.translate(
							getCtx(), "C_Cash_ID") + ": " + cash.getName(),
					get_TrxName());

			// ------------------------------
            // Added by Matías Cap - Disytel
            // Se seteaba la organización de login en vez de la de línea
            // lo cual tiraba un error al crear el allocation hdr
            alloc.setClientOrg(getAD_Client_ID(), getAD_Org_ID());
            // ------------------------------
            if (!alloc.save(get_TrxName())) {
				throw new Exception("@AllocationSaveError@: "
						+ CLogger.retrieveErrorAsString());
            }

			MAllocationLine aLine = new MAllocationLine(alloc, getAmount()
					.abs(), getDiscountAmt(), getWriteOffAmt(), BigDecimal.ZERO);

            aLine.setC_Invoice_ID(getC_Invoice_ID());
            aLine.setC_CashLine_ID(getC_CashLine_ID());

            if (!aLine.save( get_TrxName())) {
				throw new Exception("@AllocationLineSaveError@: "
						+ CLogger.retrieveErrorAsString());
            }
            
			// Bypass para que no actualicen el crédito de la entidad comercial
			// porque lo realiza el completado de la línea
            alloc.setUpdateBPBalance(false);
            
            // Should start WF

            if (!alloc.processIt( DocAction.ACTION_Complete )) {
            	throw new Exception("@AllocationProcessError@: " + alloc.getProcessMsg());
            } else if (!alloc.save(get_TrxName())) {
				throw new Exception("@AllocationSaveError@: "
						+ CLogger.retrieveErrorAsString());
            }
            
			// Guarda la referencia al allocation creado para futuros
			// procesamientos. (anulaciones, etc).
            setC_AllocationHdr_ID(alloc.getC_AllocationHdr_ID());
    	}
	}

	/**
	 * Realiza las tareas particulares de completado de una línea de caja cuyo
	 * tipo es una Transferencia a una Cuenta Bancaria.
	 * 
	 * @throws Exception
	 *             cuando se produce algún error en el guardado o procesado de
	 *             los documentos adicionales generados.
	 */
	private void completeBankAccountTransferCashLine() throws Exception {
        // Payment just as intermediate info
		MCash cash = getCash();       
        MPayment pay        = new MPayment( getCtx(),0,get_TrxName());
        String   documentNo = Msg.translate( getCtx(),"C_Cash_ID" ) + ": " + cash.getName();
        
        // ------------------------------
        // Added by Matías Cap - Disytel
        // Se seteaba la organización de login en vez de la de línea
        // lo cual tiraba un error al crear el payment
        pay.setClientOrg(getAD_Client_ID(), getAD_Org_ID());
        // ------------------------------
        pay.setR_PnRef( documentNo );      // sets DocNo, but just 20char
        pay.setDocumentNo( documentNo );
        // TODO: Libertya STD - Matías Cap 
        // Este trxType se filtra en las ventanas Medios de Pago y Medios de Cobro
        // por lo que no se puede ver en ninguna ventana este payment.
        // Ver en qué ventana o creando una nueva mostrar este payment
        pay.set_Value( "TrxType","X" );    // Transfer
        pay.set_Value( "TenderType","X" );

        //
        pay.setC_BPartner_ID(getC_BPartner_ID());
        pay.setC_BankAccount_ID(getC_BankAccount_ID());
        pay.setC_DocType_ID(true);                                  // Receipt
        pay.setDateTrx(cash.getStatementDate());
        pay.setDateAcct(cash.getDateAcct());
        pay.setAmount(getC_Currency_ID(), getAmount().negate());    // Transfer
        pay.setDescription(getDescription());
        // Added by Matías Cap - Disytel
        // El estado cerrado lo debe marcar para no permitir la anulación del payment
        // Porque sino te queda la línea de caja inconsistente
        pay.setDocStatus(MPayment.DOCSTATUS_Closed );
        pay.setDocAction(MPayment.DOCACTION_None );
        // Added by Matías Cap - Disytel
        // Setea posted = true porque la contabilización del libro de caja 
        // realiza las contabilizaciones correspondientes a la cuenta bancaria
        pay.setPosted( true );
        pay.setIsAllocated( true );    // Has No Allocation!
        pay.setProcessed( true );
        pay.setUpdateBPBalance(false);
        pay.setC_Project_ID(getC_Project_ID());

        if (!pay.save( get_TrxName())) {
        	throw new Exception("@PaymentError@: " + CLogger.retrieveErrorAsString());
        }
        
        // Guarda la referencia al pago creado para futuros procesamientos. (anulaciones, etc).
        setC_Payment_ID(pay.getC_Payment_ID());
	}
	
	/**
	 * Realiza las tareas particulares de completado de una línea de caja cuyo
	 * tipo es una Transferencia a otra Caja
	 * 
	 * @throws Exception
	 *             cuando se produce algún error en el guardado o procesado de
	 *             los documentos adicionales generados.
	 */
	private void completeCashTransferCashLine() throws Exception {
		// Si ya tiene una línea de caja generada entonces se ignora la creación
		// Este if evita que al completar una línea por transferencia se genere un
		// loops de completeIt entre las dos líneas involucradas.
		if (getTransferCashLine_ID() > 0) {
			return;
		}
		
		MCash targetCash = new MCash(getCtx(), getTransferCash_ID(), get_TrxName());
		
		// El libro de caja destino debe estar en borrador
		if (!MCash.DOCSTATUS_Drafted.equals(targetCash.getDocStatus())) {
			throw new Exception("@TransferCashInvalidStatus@");
		}
		
		// Crea y guarda la línea de caja en la caja destino (aumenta el saldo)
		BigDecimal amount = getAmount().negate();
		MCashLine targetCashLine = new MCashLine(targetCash);
		// La línea destino también es una transferencia a caja.
		targetCashLine.setCashType(MCashLine.CASHTYPE_CashTransfer);
		// El Libro de Caja asociado a la línea creada es justamente el libro de caja
		// que contiene a esta línea.
		targetCashLine.setTransferCash_ID(getCash().getC_Cash_ID());
		// Asigna la línea generada por transferencia para evitar loops en los completeIt
		targetCashLine.setTransferCashLine_ID(getC_CashLine_ID());
		targetCashLine.setAmount(amount);
		targetCashLine.setDescription(Msg.parseTranslation(getCtx(),
				"@CashTransferGeneratedLine@: " + getCash().getName()));
		targetCashLine.setUpdateBPBalance(false);
		targetCashLine.setC_Project_ID(getC_Project_ID());
		// Se setea la caja diaria de la caja destino para que concuerden
		targetCashLine.setC_POSJournal_ID(targetCash.getC_POSJournal_ID());
		// Ignora lo que tiene que ver con cajas diarias sólo si la caja origen
		// lo ignora
		targetCashLine.setIgnorePOSJournal(isIgnorePOSJournal());
		// Completa y guarda la línea de caja destino.
		if (!DocumentEngine.processAndSave(targetCashLine, ACTION_Complete, true)) {
			throw new Exception("@CashTransferLineGenerateError@: " + targetCashLine.getProcessMsg());
		}
		
		// Guarda la referencia a la línea generada para futuras operaciones
		setTransferCashLine_ID(targetCashLine.getC_CashLine_ID());
	}

	@Override
	public boolean voidIt() {
		
		// No es posible cancelar una línea que está en Borrador.
		// Hay que eliminarla directamente.
		if (!isProcessed()) {
			m_processMsg = "@CannotVoidDraftedCashLine@";
			return false;
		}
		
		// No se pueden anular líneas de una caja completada
		if (getCash().isProcessed()) {
			m_processMsg = "@CannotVoidLineOfProcessedCash@";
			return false;
		}

		// Valida que sea posible la anulación de la línea según los allocations
		// que se hayan creado para esta línea
		if (!validateAllocations()) {
			// El ProcessMsg lo setea el validateAllocations.
			return false;
		}
		
		// Acciones
		try {
			
			// Si esta asociada a una factura primero pre-procesa la línea, anulando el allocation.
			// Si no se hace esto en este momento luego falla el reversal en caso de tratarse de una
			// línea de caja generada por una NC.
			if (CASHTYPE_Invoice.equals(getCashType())) {
				processInvoiceVoid(null);
			}
			
			// Según el tipo de efectivo se crean y/o revierten ciertos documentos
			// y líneas de caja.
			MCashLine reversalCashLine = null;
			
			// Crea una línea de caja invertida que corrije el saldo y la
			// contabilidad del libro de caja al cual pertenece esta línea.
			// Si la anulación contiene la caja diaria a la cual asociarse,
			// entonces el libro de caja de esta línea es la de la caja diaria
			reversalCashLine = createReversal(getVoidPOSJournalID() == 0 ? getC_Cash_ID()
					: MPOSJournal.getCashID(getCtx(), getVoidPOSJournalID(),
							get_TrxName()));
			
			// Setea una descripción a la línea inversa generada.
			reversalCashLine.setDescription(Msg.parseTranslation(getCtx(),
					"@GeneratedReveresalCashLine@ " + getLine()));
			
			reversalCashLine.setConfirmAditionalWorks(false);
			
			reversalCashLine.setIgnoreInvoiceOpen(isIgnoreInvoiceOpen());
			
			reversalCashLine.setC_POSPaymentMedium_ID(getC_POSPaymentMedium_ID());
			
			// Se asigna la misma caja diaria del documento a anular
			reversalCashLine.setVoidPOSJournalID(getVoidPOSJournalID());
			reversalCashLine.setVoidPOSJournalMustBeOpen(isVoidPOSJournalMustBeOpen());
			reversalCashLine.setC_POSJournal_ID(getC_POSJournal_ID());		
			
			
			// Guarda y completa la línea de caja inversa
			if (!DocumentEngine.processAndSave(reversalCashLine,
					DOCACTION_Complete, true)) {
				throw new Exception("@ReversalCashLineCreateError@:"
						+ reversalCashLine.getProcessMsg());
			};
			
			// Procesmiento adicional sobre la línea inversa según el tipo de efectivo
			// de esta línea.
			
			// - Transferencia a Cuenta Bancaria
			if (CASHTYPE_BankAccountTransfer.equals(getCashType())) {
				processBankAccountTransferVoid(reversalCashLine);
			// Esto ya no se hace mas aquí, se hace antes de crear la línea reversa.
			//} else if (CASHTYPE_Invoice.equals(getCashType())) {
				//processInvoiceVoid(reversalCashLine);
			} else if (CASHTYPE_CashTransfer.equals(getCashType())) {
				processCashTransferVoid(reversalCashLine);
			}
			
			// Setea la línea como Anulada y sin acción para que la misma no pueda ser
			// utilizada en imputaciones ni visualizada en informes.
			reversalCashLine.setDocStatus(DOCSTATUS_Voided);
			reversalCashLine.setDocAction(DOCACTION_None);
			if (!reversalCashLine.save()) {
				throw new Exception("@ReversalCashLineCreateError@:"
						+ CLogger.retrieveErrorAsString());
			}
			
			performCreditWork();
			getAditionalWorkResult().put(
					reversalCashLine,
					reversalCashLine.getAditionalWorkResult().get(
							reversalCashLine));

			setDocAction(ACTION_None);
			setDocStatus(STATUS_Voided);
			
			return true;
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return false;
		}
	}

	/**
	 * Realiza las tareas necesarias para la anulación de una línea de caja que
	 * es una Transferencia a una Cuenta Bancaria a partir de la línea inversa
	 * generada previamente.
	 * 
	 * @param reversalCashLine
	 *            Línea inversa generada por la anulación de esta línea
	 * @throws Exception
	 *             cuando se produce algún error en el guardado o procesado de
	 *             los documentos adicionales generados o editados.
	 */ 
	private void processBankAccountTransferVoid(MCashLine reversalCashLine) throws Exception {
		/*
		 * Cambia el estado de los C_Payments generados por esta línea de caja y
		 * la línea inversa poniéndolos en Anulado de modo que no aparezcan en
		 * informes.
		 */
		
		// Primero para el pago generado por esta línea
		MPayment payment = new MPayment(getCtx(), getC_Payment_ID(), get_TrxName());
		payment.setDocStatus(MPayment.STATUS_Voided);
		payment.setDocAction(MPayment.ACTION_None);
		
		if (!payment.save()) {
			throw new Exception("@VoidPaymentError@: " + CLogger.retrieveErrorAsString());
		}
		
		// Luego para el pago generado por la línea inversa
		MPayment reversalPayment = new MPayment(getCtx(), reversalCashLine.getC_Payment_ID(), get_TrxName());
		reversalPayment.setDocStatus(MPayment.STATUS_Voided);
		reversalPayment.setDocAction(MPayment.ACTION_None);

		if (!reversalPayment.save()) {
			throw new Exception("@VoidPaymentError@: " + CLogger.retrieveErrorAsString());
		}
	}

	/**
	 * Verifica las asignaciones de esta línea de caja y determina si es posible
	 * realizar o no una anulación de la misma.
	 * 
	 * @return <code>true</code> si es posible anular, <code>false</code> sino.
	 */
	private boolean validateAllocations() {
		boolean valid = true;
		// Consulta: obtiene todos los IDs de asignaciones activas en donde se ha imputado
		// esta línea de caja.
		String sql = 
			"SELECT h.C_AllocationHdr_ID  " +
			"FROM C_AllocationHdr h " +
			"INNER JOIN C_AllocationLine l ON (h.C_AllocationHdr_ID = l.C_AllocationHdr_ID) " + 
			"WHERE h.IsActive = 'Y' " +
			  "AND l.IsActive = 'Y' " +
			  "AND l.C_CashLine_ID = ? ";
		
		Set<Integer> allocations = new HashSet<Integer>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// Ejecuta la consulta y agrega todos los IDs de allocations al
			// conjunto de IDs
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_CashLine_ID());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				allocations.add(rs.getInt("C_AllocationHdr_ID"));
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error getting Allocations", e);
			m_processMsg = "@Error@ @SeeTheLog@";
			valid = false;
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		
		// Si hay mas de una asignación (cualquiera sea el tipo de efectivo de esta línea),
		// no es posible anular la línea.
		if (allocations.size() > 1
				// Si hay solo una asignación para esta línea, solo se puede anular si
				// es la asignación generada por una línea que es un pago de factura
				// (getC_AllocationHdr_ID), o si es la asignación que está anulando esta
				// línea de caja (OP/RC)
				|| (allocations.size() == 1 
					&& !allocations.contains(getVoiderAllocationID()) 
					&& !allocations.contains(getC_AllocationHdr_ID()))
				// Si hay al menos una asignación y esta línea no es un Pago a Factura, no es
				// válida la anulación de la línea (primero hay que revertir las asignaciones).
				|| (allocations.size() > 0 && !CASHTYPE_Invoice.equals(getCashType()))) {
			
			m_processMsg = "@CashLineWithAllocationsVoidError@";
			valid = false;
		}
		return valid;
	}

	/**
	 * Realiza las tareas necesarias para la anulación de una línea de caja que
	 * es un Pago a Factura a partir de la línea inversa generada previamente.
	 * 
	 * @param reversalCashLine
	 *            Línea inversa generada por la anulación de esta línea
	 * @throws Exception
	 *             cuando se produce algún error en el guardado o procesado de
	 *             los documentos adicionales generados o editados.
	 */ 
	private void processInvoiceVoid(MCashLine reversalCashLine) throws Exception {
		/*
		 * Precondición: el método validateAllocations ya validó que esta línea
		 * solo tiene una única asignación y que esa asignación es justamente la
		 * que se generó al completar esta línea y que cuyo ID es
		 * getC_AllocationHdr_ID.
		 * 
		 * Precondición: si getVoiderAllocationID() contiene un ID mayor a cero
		 * implica que la anulación de esta línea fue disparada desde una
		 * anulación de Asignación (Manual, OP, RC) con lo cual no hay que
		 * anular la asignación asociada a esta línea (de hecho debe ser 0).
		 * 
		 * Aquí entonces se revierte la asignación generada, para actualizar el
		 * pendiente de la factura y dejarlo tal como estaba antes de completar
		 * esta línea de caja.
		 */
		
		// Ignora si es una anulación externa (OP/RC)
		if (getVoiderAllocationID() > 0 && getC_AllocationHdr_ID() == 0) {
			return;
		}
		
		MAllocationHdr allocationHdr = new MAllocationHdr(getCtx(),
				getC_AllocationHdr_ID(), get_TrxName());
		
		// Solo revierte la asignación.
		allocationHdr
				.setAllocationAction(MAllocationHdr.ALLOCATIONACTION_RevertAllocation);
		// Indica que la anulación la realiza esta línea de caja
		allocationHdr.setVoiderCashLineID(getC_CashLine_ID());
		allocationHdr.setUpdateBPBalance(false);
		// Procesa la anulación
		if (!DocumentEngine.processAndSave(allocationHdr, MAllocationHdr.ACTION_Void, true)) {
			throw new Exception("@AllocationVoidError@: " + allocationHdr.getProcessMsg());
		}
	}

	/**
	 * Realiza las tareas necesarias para la anulación de una línea de caja que
	 * es un Transferencia a otro Libro de caja, a partir de la línea inversa
	 * generada previamente.
	 * 
	 * @param reversalCashLine
	 *            Línea inversa generada por la anulación de esta línea
	 * @throws Exception
	 *             cuando se produce algún error en el guardado o procesado de
	 *             los documentos adicionales generados o editados.
	 */ 
	private void processCashTransferVoid(MCashLine reversalCashLine) throws Exception {
		/*
		 * Al crear y completar la línea de caja inversa se ha creado la línea
		 * de caja (inversa también) en el libro de caja destino de la
		 * transferencia. Es por esto que aquí no se crea ninguna línea de caja
		 * adicional, pero sí se ponen como Anuladas ambas líneas (la original y
		 * la inversa) del libro de caja destino.
		 */
		
		// Línea original del libro de caja destino.
		MCashLine originalTransferLine = new MCashLine(getCtx(),
				getTransferCashLine_ID(), get_TrxName());
		
		originalTransferLine.setDocStatus(STATUS_Voided);
		originalTransferLine.setDocAction(ACTION_None);
		
		if (!originalTransferLine.save()) {
			throw new Exception("@TransferCashUpdateError@: "
					+ CLogger.retrieveErrorAsString());
		}
		
		// Línea inversa del libro de caja destino
		MCashLine reversalTransferLine = new MCashLine(getCtx(),
				reversalCashLine.getTransferCashLine_ID(), get_TrxName());
		
		reversalTransferLine.setDocStatus(STATUS_Voided);
		reversalTransferLine.setDocAction(ACTION_None);
		
		if (!reversalTransferLine.save()) {
			throw new Exception("@TransferCashUpdateError@: "
					+ CLogger.retrieveErrorAsString());
		}
	}

	/* (non-Javadoc)
	 * @see org.openXpertya.process.DocAction#closeIt()
	 */
	@Override
	public boolean closeIt() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * @return el valor de ignoreAllocCreate
	 */
	public boolean isIgnoreAllocCreate() {
		return ignoreAllocCreate;
	}
	
	/**
	 * @param ignoreAllocCreate el valor de ignoreAllocCreate a asignar
	 */
	public void setIgnoreAllocCreate(boolean ignoreAllocCreate) {
		this.ignoreAllocCreate = ignoreAllocCreate;
	}
	
	// 
    // Métodos de DocAction que no aplican para CashLines
    // 

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public boolean approveIt() {
		return true;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public boolean postIt() {
		return false;
	}

	@Override
	public boolean reverseCorrectIt() {
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {
		return false;
	}

	@Override
	public String getSummary() {
		return "";
	}

	@Override
	public int getDoc_User_ID() {
		return getCreatedBy();
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return getAmount();
	}

	/**
	 * @return true si el cash line está en algún allocation line completo o
	 *         cerrado, activo e imputado a alguna factura. El cashline también
	 *         debe estar completo o cerrado. False caso contrario
	 */
	public boolean isAllocated() {
		String sql = "SELECT cl.c_cashline_id " +
					 "FROM c_cashline as cl " +
					 "INNER JOIN c_allocationline as al ON cl.c_cashline_id = al.c_cashline_id " +
					 "INNER JOIN c_allocationhdr as ah ON al.c_allocationhdr_id = ah.c_allocationhdr_id " +
					 "WHERE cl.c_cashline_id = ? AND al.c_invoice_id is not null AND ah.IsActive='Y' AND al.IsActive='Y' AND ah.docstatus IN ('CO','CL') AND cl.docstatus IN ('CO','CL')";
		int cashlineID = DB.getSQLValue(get_TrxName(), sql, getID());
        return cashlineID > 0;
    }
	
	@Override
	public void setAuxiliarInfo(AuxiliarDTO auxDTO, boolean processed){
		MCash cash = getCash();
		auxDTO.setAuthCode(getAuthCode());
		// Monto convertido
		BigDecimal payAmt = MConversionRate.convertBase(
				getCtx(),
				getAmount().abs(), // CM adjusted
				getC_Currency_ID(), cash.getDateAcct(), 0, getAD_Client_ID(),
				getAD_Org_ID());
		auxDTO.setAmt(payAmt);
		auxDTO.setDateTrx(cash.getStatementDate());
		auxDTO.setDocType(MCentralAux.DOCTYPE_PaymentReceipt);
		auxDTO.setTenderType(MPOSPaymentMedium.TENDERTYPE_Cash);
		auxDTO.setDocStatus(processed ? getDocStatus() : getDocAction());
		// Signo en base al receipt
		// Si el monto es positivo es porque es línea de caja de entidad
		// comercial cliente, por lo que el signo debe ir negado, ya que si el
		// monto es positivo al ser de cliente, entonces debe decrementar el
		// crédito en la tabla auxiliar 
		auxDTO.setSign(getAmount().compareTo(BigDecimal.ZERO) >= 0?-1:1);
		auxDTO
				.setTransactionType(auxDTO.getSign() < 0 ? MCentralAux.TRANSACTIONTYPE_Customer
						: MCentralAux.TRANSACTIONTYPE_Vendor);
		auxDTO.setPrepayment(!isAllocated());
		// HACK: EL matching de autorización se setea falso porque después se
		// realiza en la eliminación de transacciones
		setAuthMatch(false);
	}
	
	public void setAditionalWorkResult(Map<PO, Object> aditionalWorkResult) {
		this.aditionalWorkResult = aditionalWorkResult;
	}

	public Map<PO, Object> getAditionalWorkResult() {
		return aditionalWorkResult;
	}

	/**
	 * Realizar las tareas de actualización de crédito antes de completar
	 * 
	 * @throws Exception
	 *             si hubo error
	 */
	protected void performCreditWork() throws Exception{
		setAditionalWorkResult(new HashMap<PO, Object>());
		// Si existe entidad comercial configurada en la línea y es necesario
		// actualizar su saldo, realizo las tareas correspondientes
		if (!Util.isEmpty(getC_BPartner_ID(), true) && isUpdateBPBalance()) {
			MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(),get_TrxName());
			CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
			// Actualizo el balance
			CallResult result = manager.performAditionalWork(getCtx(), new MOrg(
					getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()), bp, this,
					false, get_TrxName());
			// Si hubo error, obtengo el mensaje y retorno inválido
			if (result.isError()) {
				throw new Exception(result.getMsg());
			}
			// Me guardo el resultado en la variable de instancia para luego
			// utilizarla en afterProcessDocument
			getAditionalWorkResult().put(this, result.getResult());
		}
	}
	
	/**
	 * Operaciones luego de procesar el documento
	 */
	public boolean afterProcessDocument(String processAction, boolean status) {

		// Setear el crédito

		if ((processAction.equals(MInvoice.DOCACTION_Complete)
				|| processAction.equals(MInvoice.DOCACTION_Reverse_Correct) || processAction
				.equals(MInvoice.DOCACTION_Void))
				&& status) {

			// Guardar la línea de caja para contener los datos reales a find e
			// actualizar el crédito
			save();

			// Actualiza el crédito de la entidad comercial
			if (!Util.isEmpty(getC_BPartner_ID(), true) && isUpdateBPBalance()
					&& isConfirmAditionalWorks()) {
				MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
				// Obtengo el manager actual
				CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
				// Actualizo el saldo
				CallResult result = new CallResult();
				try{
					result = manager.afterProcessDocument(getCtx(),
							new MOrg(getCtx(), getAD_Org_ID(), get_TrxName()), bp,
							getAditionalWorkResult(), get_TrxName());
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
	
	/**
	 * @return the voiderAllocationID
	 */
	public Integer getVoiderAllocationID() {
		return voiderAllocationID;
	}

	/**
	 * @param voiderAllocationID the voiderAllocationID to set
	 */
	public void setVoiderAllocationID(Integer voiderAllocationID) {
		this.voiderAllocationID = voiderAllocationID;
	}

	/**
	 * @return <code>true</code> si la moneda del libro de transferencia es
	 *         igual al libro de esta línea, <code>false</code> caso contrario.
	 */
	private boolean validateCashTransferCurrency() {
		// Obtiene la moneda del libro de caja destino de la transferencia
		// y la compara con la moneda de este libro de caja. Deben
		// ser iguales para que la transferencia sea válida.
		String sql = 
			"SELECT cb.C_Currency_ID " +
			"FROM C_Cash c " +
			"INNER JOIN C_CashBook cb ON (c.C_CashBook_ID = cb.C_CashBook_ID) " +
			"WHERE c.C_Cash_ID = ?";
		
		int targetCurrencyID = DB.getSQLValue(get_TrxName(), sql, getTransferCash_ID());
		return getCash().getC_Currency_ID() == targetCurrencyID;
	}

	public void setConfirmAditionalWorks(boolean confirmAditionalWorks) {
		this.confirmAditionalWorks = confirmAditionalWorks;
	}

	public boolean isConfirmAditionalWorks() {
		return confirmAditionalWorks;
	}

	public void setIgnoreInvoiceOpen(boolean ignoreInvoiceOpen) {
		this.ignoreInvoiceOpen = ignoreInvoiceOpen;
	}

	public boolean isIgnoreInvoiceOpen() {
		return ignoreInvoiceOpen;
	}
	
	private boolean validateCashLineCurrencyConvert() {
		int currecy_Client = Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" );
		if (getC_Currency_ID() != currecy_Client) {
			return (MCurrency.currencyConvert(new BigDecimal(1), currecy_Client, getC_Currency_ID(), getCash().getDateAcct(), getAD_Org_ID(), getCtx()) != null);
		}
		return true;
	}

	public void setIgnorePOSJournal(boolean ignorePOSJournal) {
		this.ignorePOSJournal = ignorePOSJournal;
	}

	public boolean isIgnorePOSJournal() {
		return ignorePOSJournal;
	}

	public void setVoidPOSJournalID(Integer voidPOSJournalID) {
		this.voidPOSJournalID = voidPOSJournalID;
	}

	public Integer getVoidPOSJournalID() {
		return voidPOSJournalID;
	}

	public void setVoidPOSJournalMustBeOpen(boolean voidPOSJournalMustBeOpen) {
		this.voidPOSJournalMustBeOpen = voidPOSJournalMustBeOpen;
	}

	public boolean isVoidPOSJournalMustBeOpen() {
		return voidPOSJournalMustBeOpen;
	}

	public MCashBook getCashBook()
	{
		if (m_cashBook == null)
			m_cashBook = MCashBook.get(getCtx(), getParent().getC_CashBook_ID());
		return m_cashBook;
	}	//	getCashBook
	/** Cash Book				*/
	private MCashBook 		m_cashBook = null;
	/**
	 * 	Get Cash (parent)
	 *	@return cash
	 */
	public MCash getParent()
	{
		if (m_parent == null)
			m_parent = new MCash (getCtx(), getC_Cash_ID(), get_TrxName());
		return m_parent;
	}	//	getCash
	/** Parent					*/
	private MCash			m_parent = null;

	
}    // MCashLine



/*
 *  @(#)MCashLine.java   02.07.07
 * 
 *  Fin del fichero MCashLine.java
 *  
 *  Versión 2.2
 *
 */
