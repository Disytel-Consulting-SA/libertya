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

import java.io.File;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.cc.CurrentAccountBalanceStrategy;
import org.openXpertya.cc.CurrentAccountDocument;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.electronicInvoice.ElectronicInvoiceInterface;
import org.openXpertya.electronicInvoice.ElectronicInvoiceProvider;
import org.openXpertya.model.DiscountCalculator.ICreditDocument;
import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.print.ReportEngine;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.process.ProcessorWSFE;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AuxiliarDTO;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.MProductCache;
import org.openXpertya.util.MeasurableTask;
import org.openXpertya.util.Msg;
import org.openXpertya.util.SalesUtil;
import org.openXpertya.util.StringUtil;
import org.openXpertya.util.TimeStatsLogger;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 * 
 * 
 * @version 2.1, 02.07.07
 * @author Equipo de Desarrollo de openXpertya
 */

public class MInvoice extends X_C_Invoice implements DocAction,Authorization, CurrentAccountDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String VOID_FISCAL_DESCRIPTION_PREFERENCE_NAME = "FiscalDescription_VoidedDocument";

	// Adder: POSSimple - Omision de pre y before Save
	public boolean skipAfterAndBeforeSave = false;

	/**
	 * Bypass para actualización del descuento manual general de la factura
	 * (Sólo para Facturas de Cliente, no TPV)
	 */
	private boolean skipManualGeneralDiscount = false;

	/**
	 * Bypass para aplicación del esquema de vencimientos
	 */
	private boolean skipApplyPaymentTerm = false;

	private boolean isTPVInstance = false;

	private boolean dragDocumentDiscountAmts = false;
	private boolean dragDocumentSurchargesAmts = false;

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
	 * Bypass para no crear la imputación automática para notas de crédito
	 * automáticas
	 */
	private boolean skipAutomaticCreditAllocCreation = false;
	
	/** Bypass para no setear cadena de autorización */
	private boolean skipAuthorizationChain = false;

	/**
	 * Tipos de documento excluídos en la creación de nota de crédito automática
	 */
	private static List<String> automaticCreditDocTypesExcluded = null;

	static {
		automaticCreditDocTypesExcluded = new ArrayList<String>();
		automaticCreditDocTypesExcluded.add(MDocType.DOCTYPE_Retencion_Receipt);
		automaticCreditDocTypesExcluded
				.add(MDocType.DOCTYPE_Retencion_ReceiptCustomer);
	}

	/**
	 * Elevar Exception en cancelación en el momento de chequeo de estado de
	 * impresora fiscal
	 */
	private boolean throwExceptionInCancelCheckStatus = false;

	/** Config de caja diaria de anulación desde el proceso de anulación */
	private String voidPOSJournalConfig = null;

	/**
	 * Bypass para no validar al momento de modificar el descuento manual
	 * general
	 */
	private boolean skipManualGeneralDiscountValidation = false;

	/***
	 * Boolean que determina si es posible setear la lista de precio del pedido
	 * al setearlo
	 */
	private boolean allowSetOrderPriceList = true;
	
	/**
	 * Boolean que determina si se deben omitir validaciones soporte a extensiones
	 */
	private boolean skipExtraValidations = false;
	
	/**
	 * Boolean que determina si se deben omitir las validaciones de modelo
	 */
	private boolean skipModelValidations = false;

	/** Boolean que determina si se está ejecutando una anulación. */
	private boolean voidProcess = false;

	/** Boolean que determina si se debe controlar el último número impreso fiscalmente */
	private boolean skipLastFiscalDocumentNoValidation = false;
	
	/**
	 * Anulación: Asociación entre las líneas del comprobante a anular y el
	 * contra documento
	 */
	private Map<Integer, Integer> reversalInvoiceLinesAssociation = null;
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param ctx
	 * @param C_BPartner_ID
	 * @param trxName
	 * 
	 * @return
	 */

	public static MInvoice[] getOfBPartner(Properties ctx, int C_BPartner_ID,
			String trxName) {
		ArrayList list = new ArrayList();
		String sql = "SELECT * FROM C_Invoice WHERE C_BPartner_ID=?";
		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, C_BPartner_ID);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(new MInvoice(ctx, rs, trxName));
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "getOfBPartner", e);
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}

			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}

		//

		MInvoice[] retValue = new MInvoice[list.size()];

		list.toArray(retValue);

		return retValue;
	} // getOfBPartner

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param from
	 * @param dateDoc
	 * @param C_DocTypeTarget_ID
	 * @param isSOTrx
	 * @param counter
	 * @param trxName
	 * @param setOrder
	 * 
	 * @return
	 */

	public static MInvoice copyFrom(MInvoice from, Timestamp dateDoc,
			int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
			String trxName, boolean setOrder, boolean setInOut) {
		return copyFrom(from, dateDoc, C_DocTypeTarget_ID, isSOTrx, counter,
				trxName, setOrder, setInOut, false, !isSOTrx);
	} // copyFrom

	public static MInvoice copyFrom(MInvoice from, Timestamp dateDoc,
			int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
			String trxName, boolean setOrder, boolean setInOut, 
			boolean copyDocumentDiscounts, boolean copyManualInvoiceTaxes) {
		return copyFrom(from, dateDoc, C_DocTypeTarget_ID, isSOTrx, counter,
				trxName, setOrder, setInOut, copyDocumentDiscounts, copyManualInvoiceTaxes, 
				false);
	}
	
	/**
	 * 
	 * @param from
	 * @param dateDoc
	 * @param C_DocTypeTarget_ID
	 * @param isSOTrx
	 * @param counter
	 * @param trxName
	 * @param setOrder
	 * @param copyDocumentDiscounts
	 * @return
	 */
	public static MInvoice copyFrom(MInvoice from, Timestamp dateDoc,
			int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
			String trxName, boolean setOrder, boolean setInOut, 
			boolean copyDocumentDiscounts, boolean copyManualInvoiceTaxes, 
			boolean voidProcess) {
		MInvoice to = new MInvoice(from.getCtx(), 0, trxName);

		PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
		to.setC_Invoice_ID(0);
		to.set_ValueNoCheck("DocumentNo", null);
		to.setIsCopy(true);
		to.setVoidProcess(voidProcess);

		/*
		* Ponger en null el cae y el vto cae del documento copiado para evitar errores
		* en la emisión de facturas electrónicas por verificaciones en el cae
		*/
		to.setcae(null);
		to.setvtocae(null);

		to.setDocStatus(DOCSTATUS_Drafted); // Draft
		to.setDocAction(DOCACTION_Complete);

		//

		to.setC_DocType_ID(0);
		to.setC_DocTypeTarget_ID(C_DocTypeTarget_ID);
		to.setIsSOTrx(isSOTrx);
		to.setManualDocumentNo(false);
		//

		to.setDateInvoiced(dateDoc);
		to.setDateAcct(dateDoc);
		to.setDatePrinted(null);
		to.setIsPrinted(false);
		to.setPaymentRule(from.getPaymentRule());
		to.setCurrentAccountVerified(true);
		to.setSkipExtraValidations(from.isSkipExtraValidations());
		to.setSkipModelValidations(from.isSkipModelValidations());
		//

		to.setIsApproved(false);
		to.setC_Payment_ID(0);
		to.setC_CashLine_ID(0);
		to.setIsPaid(false);
		to.setIsInDispute(false);

		//
		// Amounts are updated by trigger when adding lines

		to.setGrandTotal(Env.ZERO);
		to.setTotalLines(Env.ZERO);

		//

		to.setIsTransferred(false);
		to.setPosted(false);
		to.setProcessed(false);

		to.setManageDragOrderDiscounts(false);
		to.setManageDragOrderSurcharges(false);

		// delete references

		to.setIsSelfService(false);

		if (!setOrder) {
			to.setC_Order_ID(0);
		}

		if (counter) {
			to.setRef_Invoice_ID(from.getC_Invoice_ID());

			// Try to find Order link

			if (from.getC_Order_ID() != 0) {
				MOrder peer = new MOrder(from.getCtx(), from.getC_Order_ID(),
						from.get_TrxName());

				if (peer.getRef_Order_ID() != 0) {
					to.setC_Order_ID(peer.getRef_Order_ID());
				}
			}
		} else {
			to.setRef_Invoice_ID(0);
		}

		if (!to.save(trxName)) {
			throw new IllegalStateException("Could not create Invoice: " + CLogger.retrieveErrorAsString());
		}

		if (counter) {
			from.setRef_Invoice_ID(to.getC_Invoice_ID());
		}

		// Lines

		if (to.copyLinesFrom(from, counter, setOrder, setInOut) == 0) {
			throw new IllegalStateException("Could not create Invoice Lines");
		}

		
		try {
			to.copyAutomaticInvoiceTaxes(from);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		}
		
		// Impuestos manuales de la factura
		if (copyManualInvoiceTaxes || voidProcess) {
			try {
				to.copyManualInvoiceTaxes(from);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
		}

		// Copiar los document discounts
		if (copyDocumentDiscounts) {
			try {
				to.copyDocumentDiscounts(from);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage());
			}
		}
		
		// Calcular totales
		try {
			to.calculateTotalAmounts();
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		}

		return to;
	} // copyFrom

	/**
	 * @param ctx
	 * @param bpartnerID
	 * @param isSOTrx
	 * @param creditNotesExcluded
	 * @param trxName
	 * @return true si la entidad comercial parámetro posee créditos abiertos
	 *         todavía excluyendo los créditos y montos parámetro, false caso
	 *         contrario
	 */
	public static boolean hasCreditsOpen(Properties ctx, Integer bpartnerID,
			boolean isSOTrx, Map<Integer, BigDecimal> creditNotesExcluded,
			String trxName) {
		String docTypesOut = isSOTrx ? "'"
				+ MDocType.DOCTYPE_Retencion_ReceiptCustomer + "'" : "'"
				+ MDocType.DOCTYPE_Retencion_Receipt + "'";
		String docBaseTypeCredit = isSOTrx ? "'"
				+ MDocType.DOCBASETYPE_ARCreditMemo + "'" : "'"
				+ MDocType.DOCBASETYPE_APCreditMemo + "'";
		StringBuffer sql = new StringBuffer(
				"SELECT sum(coalesce(invoiceopen(i.c_invoice_id,0),0)) as open "
						+ "FROM c_invoice as i "
						+ "INNER JOIN c_doctype as dt ON dt.c_doctype_id = i.c_doctypetarget_id "
						+ "WHERE i.ad_client_id = ? "
						+ "		AND i.c_bpartner_id = ? "
						+ "		AND dt.docbasetype = " + docBaseTypeCredit
						+ "		AND dt.doctypekey NOT IN (" + docTypesOut + ") ");
		BigDecimal excludedAmt = BigDecimal.ZERO;
		if (creditNotesExcluded != null && creditNotesExcluded.size() > 0) {
			StringBuffer excludedCredits = new StringBuffer();
			for (Integer creditID : creditNotesExcluded.keySet()) {
				excludedAmt = excludedAmt
						.add(creditNotesExcluded.get(creditID));
				excludedCredits.append(String.valueOf(creditID)).append(",");
			}
			excludedCredits = new StringBuffer(excludedCredits.substring(0,
					excludedCredits.lastIndexOf(",") - 1));
			sql.append(" AND i.c_invoice_id NOT IN (").append(excludedCredits)
					.append(")");
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal open = BigDecimal.ZERO;
		try {
			ps = DB.prepareStatement(sql.toString(), trxName);
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			ps.setInt(2, bpartnerID);
			rs = ps.executeQuery();
			if (rs.next()) {
				open = rs.getBigDecimal("open") != null ? rs
						.getBigDecimal("open") : BigDecimal.ZERO;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return open.subtract(excludedAmt).compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 * @return el id de proceso que corresponde con la impresión de la salida
	 *         por depósito
	 */
	public static Integer getWarehouseDeliverDocumentProcessID(String trxName) {
		return DB
				.getSQLValue(
						trxName,
						"SELECT ad_process_id FROM ad_process WHERE ad_componentobjectuid = 'CORE-AD_Process-1010274'");
	}

	/**
	 * @return el id de proceso que corresponde con la impresión cliente en
	 *         cuenta corriente
	 */
	public static Integer getCurrentAccountDocumentProcessID(String trxName) {
		return DB
				.getSQLValue(
						trxName,
						"SELECT ad_process_id FROM ad_process WHERE ad_componentobjectuid = 'CORE-AD_Process-1010286'");
	}

	/**
	 * @param ctx
	 * @param docTypeID
	 * @param trxName
	 * @return el último nro de documento impreso fiscalmente del tipo de
	 *         documento parámetro
	 */
	public static Integer getLastFiscalDocumentNumeroComprobantePrinted(
			Properties ctx, Integer docTypeID, Integer excludedInvoiceID,
			String trxName) {
		return DB
				.getSQLValue(
						trxName,
						"select coalesce(max(numerocomprobante),0) as maxnumerocomprobante from c_invoice where fiscalalreadyprinted = 'Y' and c_doctypetarget_id = ?"
								+ (Util.isEmpty(excludedInvoiceID, true) ? ""
										: " AND c_invoice_id <> "
												+ excludedInvoiceID), docTypeID);
	}
	
	/**
	 * Obtener la fecha de facturación del último comprobante emitido
	 * electrónicamente
	 * 
	 * @param ctx
	 * @param docTypeID
	 * @param excludedInvoiceID
	 * @param trxName
	 * @return
	 */
	public static Timestamp getLastFEDateIssued(
			Properties ctx, Integer docTypeID, Integer excludedInvoiceID,
			String trxName) {
		// La fecha del comprobante no puede ser mayor al último electrónico
		String sql = "select max(i.dateinvoiced) "
				+ "from c_invoice i "
				+ "inner join c_doctype dt on dt.c_doctype_id = i.c_doctypetarget_id "
				+ "where i.c_doctypetarget_id = " + docTypeID
				+ "			and dt.iselectronic = 'Y' "
				+ "			and i.docstatus in ('CO','CL','VO','RE') "
				+ "			and i.cae is not null "
				+ "			and length(trim(i.cae)) > 0 "
				+ (Util.isEmpty(excludedInvoiceID, true) ? "" : " AND i.c_invoice_id <> " + excludedInvoiceID);
		
		return DB.getSQLValueTimestamp( trxName,sql);
	}

	public void copyAutomaticInvoiceTaxes(MInvoice from) throws Exception {
		List<MInvoiceTax> invoiceTaxes = MInvoiceTax.getTaxesFromInvoice(from, false);
		MInvoiceTax newInvoiceTax;
		for (MInvoiceTax mInvoiceTax : invoiceTaxes) {
			newInvoiceTax = new MInvoiceTax(getCtx(), 0, get_TrxName());
			newInvoiceTax.setC_Invoice_ID(getID());
			newInvoiceTax.setC_Tax_ID(mInvoiceTax.getC_Tax_ID());
			newInvoiceTax.setTaxAmt(mInvoiceTax.getTaxAmt());
			newInvoiceTax.setTaxBaseAmt(mInvoiceTax.getTaxBaseAmt());
			if (!newInvoiceTax.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
	}
	
	public void copyManualInvoiceTaxes(MInvoice from) throws Exception {
		List<MInvoiceTax> invoiceTaxes = MInvoiceTax.getTaxesFromInvoice(from, true);
		MInvoiceTax newInvoiceTax;
		for (MInvoiceTax mInvoiceTax : invoiceTaxes) {
			newInvoiceTax = new MInvoiceTax(getCtx(), 0, get_TrxName());
			newInvoiceTax.setC_Invoice_ID(getID());
			newInvoiceTax.setC_Tax_ID(mInvoiceTax.getC_Tax_ID());
			newInvoiceTax.setTaxAmt(mInvoiceTax.getTaxAmt());
			newInvoiceTax.setTaxBaseAmt(mInvoiceTax.getTaxBaseAmt());
			if (!newInvoiceTax.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
	}

	/**
	 * Copia los descuentos arrastrados desde el pedido desde el comprobante
	 * parámetro al actual
	 * 
	 * @param from
	 * @throws Exception
	 */
	public void copyDocumentDiscounts(MInvoice from) throws Exception {
		// Copio primero los descuentos padre para luego asignarselos a los
		// restantes (de línea y por tasa) 
		String filter = "C_Invoice_ID = ? AND C_DocumentDiscount_Parent_ID IS NULL";
		List<MDocumentDiscount> discounts = MDocumentDiscount.get(filter, new Object[] { from.getID() },
				"C_DocumentDiscount_Parent_ID", getCtx(), get_TrxName()); 
		MDocumentDiscount newDocumentDiscount;
		Map<Integer, Integer> parentsDocumentDiscounts = new HashMap<Integer, Integer>();
		for (MDocumentDiscount mDocumentDiscount : discounts) {
			newDocumentDiscount = new MDocumentDiscount(getCtx(), 0, get_TrxName());
			PO.copyValues(mDocumentDiscount, newDocumentDiscount);
			newDocumentDiscount.setC_Invoice_ID(getID());
			newDocumentDiscount.setC_Order_ID(0);
			if (!newDocumentDiscount.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			parentsDocumentDiscounts.put(mDocumentDiscount.getID(),
					newDocumentDiscount.getID());
		}
		// Itero por los restantes document discount ya con los padres creados
		// en la factura actual
		filter = "C_Invoice_ID = ? AND C_DocumentDiscount_Parent_ID IS NOT NULL";
		discounts = MDocumentDiscount.get(filter, new Object[] { from.getID() },
				"TaxRate DESC", getCtx(), get_TrxName());
		for (MDocumentDiscount mDocumentDiscount : discounts) {
			newDocumentDiscount = new MDocumentDiscount(getCtx(), 0, get_TrxName());
			PO.copyValues(mDocumentDiscount, newDocumentDiscount);
			newDocumentDiscount.setC_Invoice_ID(getID());
			newDocumentDiscount.setC_Order_ID(0);
			newDocumentDiscount.setC_OrderLine_ID(0);
			newDocumentDiscount.setC_InvoiceLine_ID(reversalInvoiceLinesAssociation != null
					&& reversalInvoiceLinesAssociation.containsKey(mDocumentDiscount.getC_InvoiceLine_ID())
							? reversalInvoiceLinesAssociation.get(mDocumentDiscount.getC_InvoiceLine_ID()) 
									: 0);
			newDocumentDiscount.setC_DocumentDiscount_Parent_ID(
					parentsDocumentDiscounts.get(mDocumentDiscount.getC_DocumentDiscount_Parent_ID()));
			if (!newDocumentDiscount.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param documentDir
	 * @param C_Invoice_ID
	 * 
	 * @return
	 */

	public static String getPDFFileName(String documentDir, int C_Invoice_ID) {
		StringBuffer sb = new StringBuffer(documentDir);

		if (sb.length() == 0) {
			sb.append(".");
		}

		if (!sb.toString().endsWith(File.separator)) {
			sb.append(File.separator);
		}

		sb.append("C_Invoice_ID_").append(C_Invoice_ID).append(".pdf");

		return sb.toString();
	} // getPDFFileName

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

	public static MInvoice get(Properties ctx, int C_Invoice_ID, String trxName) {
		Integer key = new Integer(C_Invoice_ID);
		MInvoice retValue = (MInvoice) s_cache.get(key);

		if (retValue != null) {
			return retValue;
		}

		retValue = new MInvoice(ctx, C_Invoice_ID, trxName);

		if (retValue.getID() != 0) {
			s_cache.put(key, retValue);
		}

		return retValue;
	} // get

	/**
	 * @param invoiceID
	 * @param trxName
	 * @return Monto sin saldar de la factura parámetro
	 */
	public static BigDecimal invoiceOpen(Integer invoiceID, String trxName) {
		return DB.getSQLValueBD(trxName, "SELECT invoiceopen(?,0)", invoiceID);
	}

	/**
	 * A partir de una factura de crédito se imputan a las facturas más viejas.
	 * Si el pedido parámetro es distinto de null o cero, entonces sólo se
	 * buscan las facturas sólo de ese pedido
	 * 
	 * @param creditInvoice
	 * @param isCreditForReturn
	 * @throws Exception
	 */
	public static void createAutomaticCreditAllocations(MInvoice creditInvoice,
			Integer orderID) throws Exception {
		String docTypesOut = creditInvoice.isSOTrx() ? "'"
				+ MDocType.DOCTYPE_Retencion_InvoiceCustomer + "'" : "'"
				+ MDocType.DOCTYPE_Retencion_Invoice + "'";
		String docBaseTypeDebit = creditInvoice.isSOTrx() ? "'"
				+ MDocType.DOCBASETYPE_ARInvoice + "'" : "'"
				+ MDocType.DOCBASETYPE_APInvoice + "'";
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT c_invoice_id, open "
				+ "FROM (SELECT i.c_invoice_id, "
				+ "		currencyconvert(invoiceopen(i.c_invoice_id, 0), i.c_currency_id, ?, ?::date, 0, ?, ?) as open"
				+ "		FROM c_invoice as i "
				+ "		INNER JOIN c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id "
				+ "		WHERE c_bpartner_id = ? "
				+ "				AND i.paymentrule = '" + PAYMENTRULE_OnCredit + "' " 
				+ "				AND dt.docbasetype = "
				+ docBaseTypeDebit + "				AND dt.doctypekey NOT IN ("
				+ docTypesOut + ") " + "				AND i.docstatus IN ('CO','CL') ");
		// Si tenemos una factura original en el crédito, entonces tomo esa
		if (CalloutInvoiceExt.ComprobantesFiscalesActivos()
				&& !Util.isEmpty(creditInvoice.getC_Invoice_Orig_ID(), true)) {
			if (!Util.isEmpty(orderID, true)) {
				sql.append(" AND (i.c_invoice_id = ? OR c_order_id = ?)");
			} else {
				sql.append(" AND i.c_invoice_id = ? ");
			}
		} else if (!Util.isEmpty(orderID, true)) {
			sql.append(" AND c_order_id = ? ");
		}
		sql.append("		ORDER BY dateinvoiced) as o " + "WHERE open > 0");
		PreparedStatement ps = DB.prepareStatement(sql.toString(),
				creditInvoice.get_TrxName());
		int i = 1;
		ps.setInt(i++, creditInvoice.getC_Currency_ID());
		ps.setTimestamp(i++, creditInvoice.getDateInvoiced());
		ps.setInt(i++, creditInvoice.getAD_Client_ID());
		ps.setInt(i++, creditInvoice.getAD_Org_ID());
		ps.setInt(i++, creditInvoice.getC_BPartner_ID());
		if (CalloutInvoiceExt.ComprobantesFiscalesActivos()
				&& !Util.isEmpty(creditInvoice.getC_Invoice_Orig_ID(), true)) {
			if (!Util.isEmpty(orderID, true)) {
				ps.setInt(i++, creditInvoice.getC_Invoice_Orig_ID());
				ps.setInt(i++, orderID);
			} else {
				ps.setInt(i++, creditInvoice.getC_Invoice_Orig_ID());
			}
		} else if (!Util.isEmpty(orderID, true)) {
			ps.setInt(i++, orderID);
		}
		ResultSet rs = ps.executeQuery();
		// Crear la cabecera de allocation
		MAllocationHdr hdr = new MAllocationHdr(creditInvoice.getCtx(), 0,
				creditInvoice.get_TrxName());
		hdr.setAllocationType(MAllocationHdr.ALLOCATIONTYPE_Manual);
		hdr.setRetencion_Amt(BigDecimal.ZERO);

		hdr.setC_BPartner_ID(creditInvoice.getC_BPartner_ID());
		hdr.setC_Currency_ID(creditInvoice.getC_Currency_ID());
		Timestamp date = Env.getDate();
		hdr.setDateAcct(date);
		hdr.setDateTrx(date);

		hdr.setDescription("Imputacion NC Automatica");
		hdr.setIsManual(false);
		hdr.setDocAction(MAllocationHdr.DOCACTION_Complete);
		hdr.setDocStatus(MAllocationHdr.DOCSTATUS_Drafted);
		boolean hdrSaved = false;
		BigDecimal amt = invoiceOpen(creditInvoice.getID(),
				creditInvoice.get_TrxName());
		BigDecimal auxAmt, allocAmt, totalAllocAmt = BigDecimal.ZERO;
		MAllocationLine allocationLine;
		while (amt.compareTo(BigDecimal.ZERO) > 0 && rs.next()) {
			auxAmt = amt.subtract(rs.getBigDecimal("open"));
			allocAmt = auxAmt.compareTo(BigDecimal.ZERO) <= 0 ? amt : rs
					.getBigDecimal("open");
			if (!hdrSaved) {
				if (!hdr.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				hdrSaved = true;
			}
			allocationLine = new MAllocationLine(hdr);
			allocationLine.setAmount(allocAmt);
			allocationLine.setC_Invoice_ID(rs.getInt("c_invoice_id"));
			allocationLine.setC_Invoice_Credit_ID(creditInvoice.getID());
			if (!allocationLine.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			totalAllocAmt = totalAllocAmt.add(allocAmt);
			amt = amt.subtract(allocAmt);
		}
		// Si se guardó la cabecera del allocation, entonces se guarda el total
		if (hdrSaved) {
			hdr.setApprovalAmt(totalAllocAmt);
			hdr.setGrandTotal(totalAllocAmt);
			// Completar el allocation
			if (!DocumentEngine.processAndSave(hdr,
					MAllocationHdr.DOCACTION_Complete, true)) {
				throw new Exception(hdr.getProcessMsg());
			}
			// Si tengo un remanente en la NC luego de buscar del pedido, busco
			// por otros pedidos
			if (!Util.isEmpty(orderID, true)
					&& amt.compareTo(BigDecimal.ZERO) > 0) {
				createAutomaticCreditAllocations(creditInvoice, null);
			}
		}
		rs.close();
		ps.close();
	}

	/**
	 * Obtiene el débito relacionado al pedido del crédito parámetro
	 * 
	 * @param creditInvoice
	 *            factura de crédito
	 * @return
	 */
	public static MInvoice getDebitFor(MInvoice creditInvoice) {
		MInvoice invoice = null;
		// Esta variable booleana permite determinar si es posible buscar un
		// débito relacionado con este crédito por alguno de los campos de
		// documentos ya que sino busca cualquier débito de la EC lo cual no es
		// correcto
		boolean canSearchByDocument = false;
		String docBaseTypeDebit = creditInvoice.isSOTrx() ? "'"
				+ MDocType.DOCBASETYPE_ARInvoice + "'" : "'"
				+ MDocType.DOCBASETYPE_APInvoice + "'";
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT i.c_invoice_id "
				+ "FROM c_invoice as i "
				+ "INNER JOIN c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id "
				+ "WHERE c_bpartner_id = ? " + "		AND dt.docbasetype = "
				+ docBaseTypeDebit + "		AND i.docstatus IN ('CO','CL') ");
		// Si tenemos una factura original en el crédito, entonces tomo esa
		if (CalloutInvoiceExt.ComprobantesFiscalesActivos()
				&& !Util.isEmpty(creditInvoice.getC_Invoice_Orig_ID(), true)) {
			if (!Util.isEmpty(creditInvoice.getC_Order_ID(), true)) {
				sql.append(" AND (i.c_invoice_id = ? OR c_order_id = ?)");
				canSearchByDocument = true;
			} else {
				sql.append(" AND i.c_invoice_id = ? ");
				canSearchByDocument = true;
			}
		} else if (!Util.isEmpty(creditInvoice.getC_Order_ID(), true)) {
			sql.append(" AND c_order_id = ? ");
			canSearchByDocument = true;
		}
		// Si no se puede buscar por ninguna relación de comprobante, entonces
		// salgo
		if (!canSearchByDocument) {
			return invoice;
		}
		sql.append(" ORDER BY dateinvoiced desc ");
		sql.append("LIMIT 1");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(),
					creditInvoice.get_TrxName());
			int i = 1;
			ps.setInt(i++, creditInvoice.getC_BPartner_ID());
			if (CalloutInvoiceExt.ComprobantesFiscalesActivos()
					&& !Util.isEmpty(creditInvoice.getC_Invoice_Orig_ID(), true)) {
				if (!Util.isEmpty(creditInvoice.getC_Order_ID(), true)) {
					ps.setInt(i++, creditInvoice.getC_Invoice_Orig_ID());
					ps.setInt(i++, creditInvoice.getC_Order_ID());
				} else {
					ps.setInt(i++, creditInvoice.getC_Invoice_Orig_ID());
				}
			} else if (!Util.isEmpty(creditInvoice.getC_Order_ID(), true)) {
				ps.setInt(i++, creditInvoice.getC_Order_ID());
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				invoice = new MInvoice(creditInvoice.getCtx(),
						rs.getInt("c_invoice_id"), creditInvoice.get_TrxName());
			}
		} catch (Exception e) {
			s_log.severe("ERROR getting debit for " + creditInvoice.toString());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e2) {
				s_log.severe("ERROR getting debit for "
						+ creditInvoice.toString());
				e2.printStackTrace();
			}
		}
		return invoice;
	}

	public static boolean existInvoiceFiscalPrinted(Properties ctx,
			String letter, Integer ptoVenta, Integer nroComprobante,
			Integer doctypeID, Integer excludeInvoiceID, String trxName) {
		// Armar el nro de documento
		String documentNo = CalloutInvoiceExt.GenerarNumeroDeDocumento(
				ptoVenta, nroComprobante, letter, true, false);
		// Buscar esa factura
		return PO
				.findFirst(
						ctx,
						Table_Name,
						"fiscalalreadyprinted = 'Y' and documentno = '"
								+ documentNo
								+ "' and c_doctypetarget_id = ? and c_invoice_id <> ? and ad_org_id = ?",
						new Object[] { doctypeID, excludeInvoiceID,
								Env.getAD_Org_ID(ctx) }, null, trxName) != null;
	}

	/** Descripción de Campos */

	private static CCache s_cache = new CCache("C_Invoice", 20, 2); // 2 minutes

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param ctx
	 * @param C_Invoice_ID
	 * @param trxName
	 */

	public MInvoice(Properties ctx, int C_Invoice_ID, String trxName) {
		super(ctx, C_Invoice_ID, trxName);

		if (C_Invoice_ID == 0) {
			setDocStatus(DOCSTATUS_Drafted); // Draft
			setDocAction(DOCACTION_Complete);

			//

			setPaymentRule(PAYMENTRULE_OnCredit); // Payment Terms
			setDateInvoiced(Env.getTimestamp());
			setDateAcct(getDateInvoiced());

			//

			setChargeAmt(Env.ZERO);
			setTotalLines(Env.ZERO);
			setGrandTotal(Env.ZERO);

			//

			setIsSOTrx(true);
			setIsTaxIncluded(false);
			setIsApproved(false);
			setIsDiscountPrinted(false);
			setIsPaid(false);
			setSendEMail(false);
			setIsPrinted(false);
			setIsTransferred(false);
			setIsSelfService(false);
			setIsPayScheduleValid(false);
			setIsInDispute(false);
			setPosted(false);
			super.setProcessed(false);
			setProcessing(false);
		}
	} // MInvoice

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */

	public MInvoice(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	} // MInvoice

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param order
	 * @param C_DocTypeTarget_ID
	 * @param invoiceDate
	 */

	public MInvoice(MOrder order, int C_DocTypeTarget_ID, Timestamp invoiceDate) {
		this(order.getCtx(), 0, order.get_TrxName());
		setClientOrg(order);
		setOrder(order); // set base settings

		//

		if (C_DocTypeTarget_ID == 0) {
			C_DocTypeTarget_ID = DB
					.getSQLValue(
							null,
							"SELECT C_DocTypeInvoice_ID FROM C_DocType WHERE C_DocType_ID=?",
							order.getC_DocType_ID());
		}

		setC_DocTypeTarget_ID(C_DocTypeTarget_ID);

		if (invoiceDate != null) {
			setDateInvoiced(invoiceDate);
		}

		setDateAcct(getDateInvoiced());

		//

		setSalesRep_ID(order.getSalesRep_ID());

		//

		setC_BPartner_ID(order.getBill_BPartner_ID());
		setC_BPartner_Location_ID(order.getBill_Location_ID());
		setAD_User_ID(order.getBill_User_ID());
	} // MInvoice

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param ship
	 * @param invoiceDate
	 */

	public MInvoice(MInOut ship, Timestamp invoiceDate) {
		this(ship.getCtx(), 0, ship.get_TrxName());
		setClientOrg(ship);
		setShipment(ship); // set base settings

		//

		setC_DocTypeTarget_ID();

		if (invoiceDate != null) {
			setDateInvoiced(invoiceDate);
		}

		setDateAcct(getDateInvoiced());

		//

		setSalesRep_ID(ship.getSalesRep_ID());
		setAD_User_ID(ship.getAD_User_ID());
	} // MInvoice

	/**
	 * Constructor de la clase ...
	 * 
	 * 
	 * @param batch
	 * @param line
	 */

	public MInvoice(MInvoiceBatch batch, MInvoiceBatchLine line) {
		this(line.getCtx(), 0, line.get_TrxName());
		setClientOrg(line);
		setDocumentNo(line.getDocumentNo());

		//

		setIsSOTrx(batch.isSOTrx());

		MBPartner bp = new MBPartner(line.getCtx(), line.getC_BPartner_ID(),
				line.get_TrxName());

		setBPartner(bp); // defaults

		//

		setIsTaxIncluded(line.isTaxIncluded());

		// May conflict with default price list

		setC_Currency_ID(batch.getC_Currency_ID());
		setC_ConversionType_ID(batch.getC_ConversionType_ID());

		//
		// setPaymentRule(order.getPaymentRule());
		// setC_PaymentTerm_ID(order.getC_PaymentTerm_ID());
		// setPOReference("");

		setDescription(batch.getDescription());

		// setDateOrdered(order.getDateOrdered());
		//

		setAD_OrgTrx_ID(line.getAD_OrgTrx_ID());
		setC_Project_ID(line.getC_Project_ID());

		// setC_Campaign_ID(line.getC_Campaign_ID());

		setC_Activity_ID(line.getC_Activity_ID());
		setUser1_ID(line.getUser1_ID());
		setUser2_ID(line.getUser2_ID());

		//

		setC_DocTypeTarget_ID(line.getC_DocType_ID());
		setDateInvoiced(line.getDateInvoiced());
		setDateAcct(line.getDateAcct());

		//

		setSalesRep_ID(batch.getSalesRep_ID());

		//

		setC_BPartner_ID(line.getC_BPartner_ID());
		setC_BPartner_Location_ID(line.getC_BPartner_Location_ID());
		setAD_User_ID(line.getAD_User_ID());
	} // MInvoice

	/** Descripción de Campos */

	private BigDecimal m_openAmt = null;

	/** Descripción de Campos */

	private MInvoiceLine[] m_lines;

	/** Descripción de Campos */

	private MInvoiceTax[] m_taxes;

	/** Descripción de Campos */

	private static CLogger s_log = CLogger.getCLogger(MInvoice.class);

	private static Map<String, String> reverseDocTypes;

	/**
	 * Booleano que determina si esta factura ya fue verificada a nivel cuenta
	 * corriente de la entidad comercial
	 */
	private boolean isCurrentAccountVerified = false;

	/**
	 * Booleano que determina si al completar esta factura se debe actualizar el
	 * saldo de cuenta corriente del cliente
	 */
	private boolean updateBPBalance = true;

	/**
	 * Resultado de la llamada de cuenta corriente que realiza trabajo adicional
	 * al procesar un documento. Al anular un invoice y crearse un documento
	 * reverso, se debe guardar dentro de esta map también.
	 */
	private Map<PO, Object> aditionalWorkResult;

	/**
	 * Booleano que determina si se debe confimar el trabajo adicional de cuenta
	 * corriente al procesar el/los documento/s
	 */
	private boolean confirmAditionalWorks = true;

	static {
		// Se inicializan los tabla de tipos de documentos para la cancelación
		// de documentos en la localización argentina.
		reverseDocTypes = new HashMap<String, String>();
		reverseDocTypes.put(MDocType.DOCTYPE_CustomerInvoice,
				MDocType.DOCTYPE_CustomerCreditNote);
		reverseDocTypes.put(MDocType.DOCTYPE_CustomerDebitNote,
				MDocType.DOCTYPE_CustomerCreditNote);
		reverseDocTypes.put(MDocType.DOCTYPE_CustomerCreditNote,
				MDocType.DOCTYPE_CustomerDebitNote);
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param AD_Client_ID
	 * @param AD_Org_ID
	 */

	public void setClientOrg(int AD_Client_ID, int AD_Org_ID) {
		super.setClientOrg(AD_Client_ID, AD_Org_ID);
	} // setClientOrg

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param bp
	 */

	public void setBPartner(MBPartner bp) {
		if (bp == null) {
			return;
		}

		setC_BPartner_ID(bp.getC_BPartner_ID());

		// Set Defaults

		int ii = 0;

		if (isSOTrx()) {
			ii = bp.getC_PaymentTerm_ID();
		} else {
			ii = bp.getPO_PaymentTerm_ID();
		}

		if (ii != 0) {
			setC_PaymentTerm_ID(ii);
		}

		//

		if (isSOTrx()) {
			ii = bp.getM_PriceList_ID();
		} else {
			ii = bp.getPO_PriceList_ID();
		}

		if (ii != 0) {
			setM_PriceList_ID(ii);
		}

		//

		String ss = bp.getPaymentRule();

		if (!Util.isEmpty(ss, true)) {
			setPaymentRule(ss);
		}

		// Set Locations

		MBPartnerLocation[] locs = bp.getLocations(false);

		if (locs != null) {
			for (int i = 0; i < locs.length; i++) {
				if ((locs[i].isBillTo() && isSOTrx())
						|| (locs[i].isPayFrom() && !isSOTrx())) {
					setC_BPartner_Location_ID(locs[i]
							.getC_BPartner_Location_ID());
				}
			}

			// set to first

			if ((getC_BPartner_Location_ID() == 0) && (locs.length > 0)) {
				setC_BPartner_Location_ID(locs[0].getC_BPartner_Location_ID());
			}
		}

		if (getC_BPartner_Location_ID() == 0) {
			//log.log(Level.SEVERE, "Has no To Address: " + bp);
			log.saveError("Error", Msg.getMsg(getCtx(), "NoBPartnerLocationError"));
		}

		// Set Contact

		MUser[] contacts = bp.getContacts(false);

		if ((contacts != null) && (contacts.length > 0)) { // get first User
			setAD_User_ID(contacts[0].getAD_User_ID());
		}
	} // setBPartner

	public void setOrder(MOrder order) {
		setOrder(order, false);
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param order
	 */

	public void setOrder(MOrder order, boolean preserveInvoiceCurrency) {
		if (order == null) {
			return;
		}

		setC_Order_ID(order.getC_Order_ID());
		setIsSOTrx(order.isSOTrx());
		setIsDiscountPrinted(order.isDiscountPrinted());
		setIsSelfService(order.isSelfService());
		setSendEMail(order.isSendEMail());

		//
		// Preserva la moneda ya que al cambiar la tarifa cambia la moneda
		// tambien
		int currentCurrencyID = getC_Currency_ID();
		if(isAllowSetOrderPriceList()){
			setM_PriceList_ID(order.getM_PriceList_ID());
		}
		setC_Currency_ID(currentCurrencyID);

		setIsTaxIncluded(order.isTaxIncluded());
		// Preserva la moneda de la factura si así se indicó en el parámetro
		// (utilizado para el Crear Desde).
		if (!preserveInvoiceCurrency) {
			setC_Currency_ID(order.getC_Currency_ID());
			setC_ConversionType_ID(order.getC_ConversionType_ID());
		}

		//

		setPaymentRule(order.getPaymentRule());
		setC_PaymentTerm_ID(order.getC_PaymentTerm_ID());
		setPOReference(order.getPOReference());
		setDescription(order.getDescription());
		setDateOrdered(order.getDateOrdered());

		//

		setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
		setC_Project_ID(order.getC_Project_ID());
		setC_Campaign_ID(order.getC_Campaign_ID());
		setC_Activity_ID(order.getC_Activity_ID());
		setUser1_ID(order.getUser1_ID());
		setUser2_ID(order.getUser2_ID());

		if (isDragDocumentDiscountAmts() || isDragDocumentSurchargesAmts()) {
			setC_Charge_ID(order.getC_Charge_ID());
			setChargeAmt(order.getChargeAmt());
		}
	} // setOrder

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param ship
	 */

	public void setShipment(MInOut ship) {
		if (ship == null) {
			return;
		}

		setIsSOTrx(ship.isSOTrx());

		//

		MBPartner bp = new MBPartner(getCtx(), ship.getC_BPartner_ID(), null);

		setBPartner(bp);

		//

		setSendEMail(ship.isSendEMail());

		//

		setPOReference(ship.getPOReference());
		setDescription(ship.getDescription());
		setDateOrdered(ship.getDateOrdered());

		//

		setAD_OrgTrx_ID(ship.getAD_OrgTrx_ID());
		setC_Project_ID(ship.getC_Project_ID());
		setC_Campaign_ID(ship.getC_Campaign_ID());
		setC_Activity_ID(ship.getC_Activity_ID());
		setUser1_ID(ship.getUser1_ID());
		setUser2_ID(ship.getUser2_ID());

		//

		if (ship.getC_Order_ID() != 0) {
			setC_Order_ID(ship.getC_Order_ID());

			MOrder order = new MOrder(getCtx(), ship.getC_Order_ID(),
					get_TrxName());

			setIsDiscountPrinted(order.isDiscountPrinted());
			setM_PriceList_ID(order.getM_PriceList_ID());
			setIsTaxIncluded(order.isTaxIncluded());
			setC_Currency_ID(order.getC_Currency_ID());
			setC_ConversionType_ID(order.getC_ConversionType_ID());
			setPaymentRule(order.getPaymentRule());
			setC_PaymentTerm_ID(order.getC_PaymentTerm_ID());

			//

			MDocType dt = MDocType.get(getCtx(), order.getC_DocType_ID());

			setC_DocTypeTarget_ID(dt.getC_DocTypeInvoice_ID());

			// Overwrite Invoice Address

			setC_BPartner_Location_ID(order.getBill_Location_ID());
		}
	} // setOrder

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param DocBaseType
	 */

	public void setC_DocTypeTarget_ID(String DocBaseType) {
		String sql = "SELECT C_DocType_ID FROM C_DocType "
				+ "WHERE AD_Client_ID=? AND DocBaseType=? "
				+ "ORDER BY IsDefault DESC";
		int C_DocType_ID = DB.getSQLValue(null, sql, getAD_Client_ID(),
				DocBaseType);

		if (C_DocType_ID <= 0) {
			log.log(Level.SEVERE,
					"setC_DocTypeTarget_ID - Not found for AC_Client_ID="
							+ getAD_Client_ID() + " - " + DocBaseType);
		} else {
			log.fine("setC_DocTypeTarget_ID - " + DocBaseType);
			setC_DocTypeTarget_ID(C_DocType_ID);

			boolean isSOTrx = MDocType.DOCBASETYPE_ARInvoice
					.equals(DocBaseType)
					|| MDocType.DOCBASETYPE_ARCreditMemo.equals(DocBaseType);

			setIsSOTrx(isSOTrx);
		}
	} // setC_DocTypeTarget_ID

	/**
	 * Descripción de Método
	 * 
	 */

	public void setC_DocTypeTarget_ID() {
		if (getC_DocTypeTarget_ID() > 0) {
			return;
		}

		if (isSOTrx()) {
			setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_ARInvoice);
		} else {
			setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APInvoice);
		}
	} // setC_DocTypeTarget_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param creditMemoAdjusted
	 * 
	 * @return
	 */

	public BigDecimal getGrandTotal(boolean creditMemoAdjusted) {
		if (!creditMemoAdjusted) {
			return super.getGrandTotal();
		}

		//

		BigDecimal amt = getGrandTotal();

		if (isCreditMemo()) {
			return amt.negate();
		}

		return amt;
	} // getGrandTotal

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param whereClause
	 * 
	 * @return
	 */

	private MInvoiceLine[] getLines(String whereClause) {
		ArrayList list = new ArrayList();
		String sql = "SELECT * FROM C_InvoiceLine WHERE C_Invoice_ID=? ";

		if (whereClause != null) {
			sql += whereClause;
		}

		sql += " ORDER BY Line";

		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_Invoice_ID());

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				MInvoiceLine il = new MInvoiceLine(getCtx(), rs, get_TrxName());

				il.setInvoice(this);
				list.add(il);
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			log.log(Level.SEVERE, "getLines", e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}

			pstmt = null;
		}

		//

		MInvoiceLine[] lines = new MInvoiceLine[list.size()];

		list.toArray(lines);

		return lines;
	} // getLines

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param requery
	 * 
	 * @return
	 */

	public MInvoiceLine[] getLines(boolean requery) {
		if ((m_lines == null) || (m_lines.length == 0) || requery) {
			m_lines = getLines(null);
		}

		return m_lines;
	} // getLines

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public MInvoiceLine[] getLines() {
		return getLines(false);
	} // getLines

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param step
	 */

	public void renumberLines(int step) {
		int number = step;
		MInvoiceLine[] lines = getLines(false);

		for (int i = 0; i < lines.length; i++) {
			MInvoiceLine line = lines[i];

			line.setLine(number);
			line.save();
			number += step;
		}

		m_lines = null;
	} // renumberLines

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param otherInvoice
	 * @param counter
	 * @param setOrder
	 * 
	 * @return
	 */

	public int copyLinesFrom(MInvoice otherInvoice, boolean counter,
			boolean setOrder, boolean setInOut) {
		if (isProcessed() || isPosted() || (otherInvoice == null)) {
			return 0;
		}

		MInvoiceLine[] fromLines = otherInvoice.getLines(false);
		int count = 0;
		reversalInvoiceLinesAssociation = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < fromLines.length; i++) {
			MInvoiceLine line = new MInvoiceLine(getCtx(), 0, get_TrxName());

			PO.copyValues(fromLines[i], line, line.getAD_Client_ID(), line.getAD_Org_ID());
			line.setC_Invoice_ID(getC_Invoice_ID());
			line.setInvoice(this);
			line.setC_InvoiceLine_ID(0); // new

			// Reset

			if (!setOrder) {
				line.setC_OrderLine_ID(0);
			}

			if(!setInOut){
				line.setM_InOutLine_ID(0);
			}
			
			line.setRef_InvoiceLine_ID(0);
			line.setA_Asset_ID(0);
			line.setM_AttributeSetInstance_ID(0);
			line.setS_ResourceAssignment_ID(0);

			// New Tax

			if (getC_BPartner_ID() != otherInvoice.getC_BPartner_ID()) {
				line.setTax(); // recalculate
			}

			//

			if (counter) {
				line.setRef_InvoiceLine_ID(fromLines[i].getC_InvoiceLine_ID());

				if (fromLines[i].getC_OrderLine_ID() != 0) {
					MOrderLine peer = new MOrderLine(getCtx(),
							fromLines[i].getC_OrderLine_ID(), get_TrxName());

					if (peer.getRef_OrderLine_ID() != 0) {
						line.setC_OrderLine_ID(peer.getRef_OrderLine_ID());
					}
				}

				line.setM_InOutLine_ID(0);

				if (fromLines[i].getM_InOutLine_ID() != 0) {
					MInOutLine peer = new MInOutLine(getCtx(),
							fromLines[i].getM_InOutLine_ID(), get_TrxName());

					if (peer.getRef_InOutLine_ID() != 0) {
						line.setM_InOutLine_ID(peer.getRef_InOutLine_ID());
					}
				}
			}

			//

			line.setProcessed(false);

			if (line.save(get_TrxName())) {
				count++;
			}

			// Cross Link

			if (counter) {
				fromLines[i].setRef_InvoiceLine_ID(line.getC_InvoiceLine_ID());
				fromLines[i].save(get_TrxName());
			}
			
			// Asociación de las líneas del comprobante a anular con el contra
			// documento
			reversalInvoiceLinesAssociation.put(fromLines[i].getID(), line.getID());
		}

		if (fromLines.length != count) {
			log.log(Level.SEVERE, "copyLinesFrom - Line difference - From="
					+ fromLines.length + " <> Saved=" + count);
		}

		return count;
	} // copyLinesFrom

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param requery
	 * 
	 * @return
	 */

	public MInvoiceTax[] getTaxes(boolean requery) {
		if ((m_taxes != null) && !requery) {
			return m_taxes;
		}

		String sql = "SELECT * FROM C_InvoiceTax WHERE C_Invoice_ID=?";
		ArrayList list = new ArrayList();
		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_Invoice_ID());

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(new MInvoiceTax(getCtx(), rs, get_TrxName()));
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			log.log(Level.SEVERE, "getTaxes", e);
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}

			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}

		m_taxes = new MInvoiceTax[list.size()];
		list.toArray(m_taxes);

		return m_taxes;
	} // getTaxes

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param description
	 */

	public void addDescription(String description) {
		String desc = getDescription();

		if (desc == null) {
			setDescription(description);
		} else {
			setDescription(desc + " | " + description);
		}
	} // addDescription

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean isCreditMemo() {
		MDocType dt = MDocType.get(getCtx(),
				(getC_DocType_ID() == 0) ? getC_DocTypeTarget_ID()
						: getC_DocType_ID());

		return MDocType.DOCBASETYPE_APCreditMemo.equals(dt.getDocBaseType())
				|| MDocType.DOCBASETYPE_ARCreditMemo
						.equals(dt.getDocBaseType());
	} // isCreditMemo

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param processed
	 */

	public void setProcessed(boolean processed) {
		super.setProcessed(processed);

		if (getID() == 0) {
			return;
		}

		String set = "SET Processed='" + (processed ? "Y" : "N")
				+ "' WHERE C_Invoice_ID=" + getC_Invoice_ID();
		int noLine = DB.executeUpdate("UPDATE C_InvoiceLine " + set,
				get_TrxName());
		int noTax = DB.executeUpdate("UPDATE C_InvoiceTax " + set,
				get_TrxName());

		m_lines = null;
		m_taxes = null;
		log.fine(processed + " - Lines=" + noLine + ", Tax=" + noTax);
	} // setProcessed

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean validatePaySchedule() {
		MInvoicePaySchedule[] schedule = MInvoicePaySchedule
				.getInvoicePaySchedule(getCtx(), getC_Invoice_ID(), 0,
						get_TrxName());

		log.fine("#" + schedule.length);

		if (schedule.length == 0) {
			setIsPayScheduleValid(false);

			return false;
		}

		// Add up due amounts

		BigDecimal total = Env.ZERO;

		for (int i = 0; i < schedule.length; i++) {
			schedule[i].setParent(this);

			BigDecimal due = schedule[i].getDueAmt();

			if (due != null) {
				total = total.add(due);
			}
		}

		boolean valid = getGrandTotal().compareTo(total) == 0;

		setIsPayScheduleValid(valid);

		// Update Schedule Lines

		for (int i = 0; i < schedule.length; i++) {
			if (schedule[i].isValid() != valid) {
				schedule[i].setIsValid(valid);
				schedule[i].save(get_TrxName());
			}
		}

		return valid;
	} // validatePaySchedule

	private boolean completarPuntoLetraNumeroDoc() {
		HashMap<String, Object> hm = CalloutInvoiceExt.DividirDocumentNo(
				getAD_Client_ID(), getDocumentNo());

		if (is_ValueChanged("NumeroComprobante"))
			hm.put("NumeroComprobante", getNumeroComprobante());

		// Si hay una letra de comprobante elegida es gracias al callout (ya que
		// el campo es de solo
		// lectura para el usuario), o un valor especificado manualmente.
		// Verificar si se corresponde
		// el valor elegido con el que tiene asociada la secuencia del tipo de
		// documento.

		if (getC_Letra_Comprobante_ID() != 0
				&& (Integer) hm.get("C_Letra_Comprobante_ID") != getC_Letra_Comprobante_ID()) {
			log.saveError("SaveError", Msg.translate(Env.getCtx(),
					"DiferentDocTypeLetraComprobanteError"));
			return false;
		}

		for (String k : hm.keySet()) {
			Object v = hm.get(k);

			if (v == null) {
				log.saveError("SaveError", Msg.translate(Env.getCtx(),
						"InvalidDocTypeFormatError"));
				return false;
			}

			set_Value(k, v);
		}

		return true;
	}

	public boolean isCredit() {
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		return docType.getDocBaseType().equals(
				MDocType.DOCBASETYPE_ARCreditMemo)
				&& !docType.getDocBaseType().equals(
						MDocType.DOCBASETYPE_APCreditMemo);
	}

	public boolean isDebit() {
		return !isCredit();
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param newRecord
	 * 
	 * @return
	 */

	protected boolean beforeSave(boolean newRecord) {
		// POSSimple:
		if (this.skipAfterAndBeforeSave)
			return true;

		log.fine("");

		// Disytel: Si ya se incorporaron lineas, no permitir el cambio de la
		// tarifa
		if (is_ValueChanged("M_PriceList_ID") && getLines(true).length > 0) {
			log.saveError("Error",
					Msg.getMsg(getCtx(), "PriceListChangedLinesAlreadyLoaded"));
			return false;
		}

		// Disytel: Si ya se incorporaron lineas, no permitir el cambio de la
		// moneda destino
		if (is_ValueChanged("C_Currency_ID") && getLines(true).length > 0) {
			log.saveError("Error",
					Msg.getMsg(getCtx(), "CurrencyChangedLinesAlreadyLoaded"));
			return false;
		}

		// No Partner Info - set Template

		if (getC_BPartner_ID() == 0) {
			setBPartner(MBPartner.getTemplate(getCtx(), getAD_Client_ID()));
		}

		MBPartner partner = new MBPartner(getCtx(), getC_BPartner_ID(),
				get_TrxName());

		if (getC_BPartner_Location_ID() == 0) {
			setBPartner(partner);
		}

		// Price List

		if (getM_PriceList_ID() == 0) {
			int ii = Env.getContextAsInt(getCtx(), "#M_PriceList_ID");

			if (ii != 0) {
				setM_PriceList_ID(ii);
			} else {
				String sql = "SELECT M_PriceList_ID FROM M_PriceList WHERE AD_Client_ID=? AND IsDefault='Y'";

				ii = DB.getSQLValue(null, sql, getAD_Client_ID());

				if (ii != 0) {
					setM_PriceList_ID(ii);
				}
			}
		}

		// Currency

		if (getC_Currency_ID() == 0) {
			String sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?";
			int ii = DB.getSQLValue(null, sql, getM_PriceList_ID());

			if (ii != 0) {
				setC_Currency_ID(ii);
			} else {
				setC_Currency_ID(Env
						.getContextAsInt(getCtx(), "#C_Currency_ID"));
			}
		}

		// Sales Rep

		if (getSalesRep_ID() == 0) {
			int ii = Env.getContextAsInt(getCtx(), "#SalesRep_ID");

			if (ii != 0) {
				setSalesRep_ID(ii);
			}
		}

		// Document Type

		if (getC_DocType_ID() == 0) {
			setC_DocType_ID(0); // make sure it's set to 0
		}

		boolean locale_ar = CalloutInvoiceExt.ComprobantesFiscalesActivos();

		/*
		 * Matias Cap - Disytel
		 * ------------------------------------------------------------------
		 * Se comenta este código porque para determinar letra y realizar
		 * validaciones de categorías de iva, el tipo de documento debe ser
		 * fiscal
		 * ------------------------------------------------------------------
		 * dREHER - Setea la letra correspondiente, luego se encarga la
		 * misma // clase de verificar si corresponde // con el tipo segun IVA
		 * Cliente E IVA Compa#ia if (locale_ar && getC_Letra_Comprobante_ID()
		 * <= 0 && ) {
		 * 
		 * // dREHER - Llamo pasando como parametro la organizacion del //
		 * documento Integer categoriaIvaClient = CalloutInvoiceExt
		 * .darCategoriaIvaClient(getAD_Org_ID());
		 * 
		 * // TODO: despues eliminar comentario log.fine(
		 * "Trajo condicion IVA de organizacion como =" + categoriaIvaClient);
		 * 
		 * categoriaIvaClient = categoriaIvaClient == null ? 0 :
		 * categoriaIvaClient; int categoriaIvaPartner =
		 * partner.getC_Categoria_Iva_ID();
		 * 
		 * // Algunas de las categorias de iva no esta asignada if
		 * (categoriaIvaClient == 0 || categoriaIvaPartner == 0) { String
		 * errorDesc = (categoriaIvaClient == 0 ? "@ClientWithoutIVAError@" :
		 * "@BPartnerWithoutIVAError@"); log.saveError(
		 * "InvalidInvoiceLetraSaveError", Msg.parseTranslation(getCtx(),
		 * errorDesc + ". @CompleteBPandClientCateoriaIVA@")); return false; }
		 * 
		 * if (isSOTrx()) { // partner -> customer, empresa -> vendor Integer
		 * letra = CalloutInvoiceExt.darLetraComprobante( categoriaIvaPartner,
		 * categoriaIvaClient); setC_Letra_Comprobante_ID(letra == null ? 0 :
		 * letra);
		 * 
		 * log.fine("Iva cliente=" + categoriaIvaPartner + " iva compa#ia=" +
		 * categoriaIvaClient);
		 * 
		 * // chequear aca que letra trae y que condiciones de iva envia
		 * 
		 * } else { // empresa -> customer, partner -> vendor Integer letra =
		 * CalloutInvoiceExt.darLetraComprobante( categoriaIvaClient,
		 * categoriaIvaPartner); setC_Letra_Comprobante_ID(letra == null ? 0 :
		 * letra); } }
		 */

		// Si el Tipo de Documento Destino es 0, se calcula a partir del Nro de
		// Punto de Venta y el Tipo de Comprobante (FC, NC, ND)
		if (locale_ar && getC_DocTypeTarget_ID() == 0 && getPuntoDeVenta() > 0 && !Util.isEmpty(getLetra(), true)) {
			String docTypeBaseKey = getDocTypeBaseKey(getTipoComprobante());
			if (!Util.isEmpty(docTypeBaseKey, true)) {
				MDocType docType = MDocType.getDocType(getCtx(),
						getAD_Org_ID(),
						getDocTypeBaseKey(getTipoComprobante()), getLetra(),
						getPuntoDeVenta(), get_TrxName());
				if (docType != null) {
					setC_DocTypeTarget_ID(docType.getC_DocType_ID());
				} else {
					log.saveError("Error",
							Msg.getMsg(getCtx(), "DocTypeTargetError"));
					return false;
				}
			}
		} else if(!locale_ar && getC_DocTypeTarget_ID() == 0){
			setC_DocTypeTarget_ID(isSOTrx() ? MDocType.DOCBASETYPE_ARInvoice
					: MDocType.DOCBASETYPE_APInvoice);
		}

		// Payment Term

		if (getC_PaymentTerm_ID() == 0) {
			int ii = Env.getContextAsInt(getCtx(), "#C_PaymentTerm_ID");

			if (ii != 0) {
				setC_PaymentTerm_ID(ii);
			} else {
				String sql = "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y'";

				ii = DB.getSQLValue(get_TrxName(), sql, getAD_Client_ID());

				if (ii != 0) {
					setC_PaymentTerm_ID(ii);
				}
			}
		}

		// Disytel: Si no hay conversion, no permitir seleccionar moneda destino
		MPriceList priceList = new MPriceList(getCtx(), getM_PriceList_ID(), get_TrxName());
		int priceListCurrency = priceList.getC_Currency_ID();
		if ((priceListCurrency != getC_Currency_ID() && MCurrency
				.currencyConvert(new BigDecimal(1), priceListCurrency,
						getC_Currency_ID(), getDateInvoiced(), getAD_Org_ID(),
						getCtx()) == null)
				|| !validateOrderCurrencyConvert()) {
			log.saveError("Error", Msg.getMsg(getCtx(), "NoCurrencyConversion"));
			return false;
		}

		// Si la Tarifa es mayor a 0 setear el Impuesto Incluido a partir de la
		// tarifa
		if (getM_PriceList_ID() > 0) {
			setIsTaxIncluded(new MPriceList(getCtx(), getM_PriceList_ID(), null)
					.isTaxIncluded());
		}

		// Si está seteado que debe registrar el nro de documento manual y no lo
		// setea, entonces error
		if (isManualDocumentNo() && Util.isEmpty(getDocumentNo(), true)) {
			log.saveError("Error", Msg.getMsg(getCtx(), "NoDocumentNo"));
			return false;
		}

		// Se obtiene el tipo de documento para determinar si es fiscal o no.
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		boolean isDebit = !docType.getDocBaseType().equals(
				MDocType.DOCBASETYPE_ARCreditMemo)
				&& !docType.getDocBaseType().equals(
						MDocType.DOCBASETYPE_APCreditMemo);
		/*
		 * // Indicador de documento fiscal. boolean fiscalDocType = // Factura
		 * de Retención no requiere validaciones fiscales
		 * !docType.getDocTypeKey().equals(MDocType.DOCTYPE_Retencion_Invoice)
		 * && // Recibo de retención no requiere validaciones fiscales.
		 * !docType.getDocTypeKey().equals(MDocType.DOCTYPE_Retencion_Receipt);
		 */
		if (locale_ar && docType.isFiscalDocument()) {
			boolean IsSOTrx = isSOTrx();

			// CUIT - si no está seteado, setearlo a partir del BPartner

			String cuit = getCUIT();
			if (Util.isEmpty(cuit)) {
				cuit = partner.getTaxID();
				setCUIT(cuit);
			}
			MCategoriaIva bpCategoriaIva = new MCategoriaIva(getCtx(),
					partner.getC_Categoria_Iva_ID(), get_TrxName());

			if (bpCategoriaIva.isRequiereCUIT()
					&& !CalloutInvoiceExt.ValidarCUIT(cuit)) {
				log.saveError("InvalidCUIT", "");
				return false;
			}

			// Nombre, Identificacion y Domicilio de cliente

			try {
				MBPartnerLocation loc = new MBPartnerLocation(getCtx(),
						getC_BPartner_Location_ID(), get_TrxName());
				if (loc.getID() < 1)
					return false;

				if (IsSOTrx) {

					// Solo se setea el nombre y el domicilio si no es
					// CONSUMIDOR FINAL y si no tiene nada cargado de antemano
					if (bpCategoriaIva.getCodigo() != MCategoriaIva.CONSUMIDOR_FINAL) {
						String location = loc.getLocation(true).toString();
						setNombreCli((Util.isEmpty(getNombreCli(), true) || (is_ValueChanged("C_BPartner_ID") && !is_ValueChanged("NombreCli"))) ? partner
								.getName() : getNombreCli());
						setInvoice_Adress((Util.isEmpty(getInvoice_Adress(),
								true) || (is_ValueChanged("C_BPartner_ID") && !is_ValueChanged("Invoice_Adress"))) ? location
								: getInvoice_Adress());
						setNroIdentificCliente((Util.isEmpty(
								getNroIdentificCliente(), true) || (is_ValueChanged("C_BPartner_ID") && !is_ValueChanged("NroIdentificCliente"))) ? partner
								.getTaxID() : getNroIdentificCliente());
					}
				}

			} catch (Exception e) {
				log.saveError("SaveError", e);
				e.printStackTrace();
				return false;
			}

			// Definir la Letra del documento automáticamente

			try {
				if (getDocumentNo() == null && getC_Letra_Comprobante_ID() == 0) {
					Integer letraId;

					// dREHER - Llamo pasando como parametro la organizacion del
					// documento
					Integer categoriaIvaClient = CalloutInvoiceExt
							.darCategoriaIvaClient(getAD_Org_ID());

					// Integer categoriaIvaClient = CalloutInvoiceExt
					// .darCategoriaIvaClient();
					categoriaIvaClient = categoriaIvaClient == null ? 0
							: categoriaIvaClient;
					int categoriaIvaPartner = partner.getC_Categoria_Iva_ID();

					// Algunas de las categorias de iva no esta asignada
					if (categoriaIvaClient == 0 || categoriaIvaPartner == 0) {
						String errorDesc = (categoriaIvaClient == 0 ? "@ClientWithoutIVAError@"
								: "@BPartnerWithoutIVAError@");
						log.saveError(
								"InvalidInvoiceLetraSaveError",
								Msg.parseTranslation(getCtx(), errorDesc
										+ ". @CompleteBPandClientCateoriaIVA@"));
						return false;
					}

					if (IsSOTrx) { // partner -> customer, empresa -> vendor
						letraId = CalloutInvoiceExt.darLetraComprobante(
								categoriaIvaPartner, categoriaIvaClient);
					} else { // empresa -> customer, partner -> vendor
						letraId = CalloutInvoiceExt.darLetraComprobante(
								categoriaIvaClient, categoriaIvaPartner);
					}

					// No fué posible calcular la letra de comprobante a partir
					// de las categorías de
					// IVA de la entidad comercial y la compañía.
					if (letraId == null) {
						log.saveError("InvalidInvoiceLetraSaveError", Msg
								.translate(getCtx(), "LetraCalculationError"));
						return false;
					}

					setC_Letra_Comprobante_ID(letraId);
				}
			} catch (Exception e) {
				log.saveError("SaveError", e);
				e.printStackTrace();
				return false;
			}

			// Numero de documento

			try {
				if (isSOTrx()) {
					if (getDocumentNo() == null)
						// Se calcula el nro de documento a partir de Pto.Vta,
						// letra y
						// siguiente Comprobante del Tipo de Documento.
						setDocumentNo();

					if (!completarPuntoLetraNumeroDoc())
						return false;

					/**
					 * Aceptar la modificacion manual del nro de comprobante
					 * para generar el numero de documento Las facturas de
					 * cliente deben poder modificarse en caso que el usuario
					 * indique un numero de comprobante diferente al de la
					 * secuencia. Se debe contemplar el uso de <> para
					 * posteriores usos de la secuencia, en los casos en que el
					 * usuario no indique un valor diferente al sugerido
					 * */
					if (newRecord) {
						// Recuperar el valor sugerido y compararlo con el
						// indicado por el usuario
						String nroCompr = getDocumentNo().replace("<", "")
								.replace(">", "");
						int nroC = Integer.parseInt(nroCompr.substring(5,
								nroCompr.length()));
						setDocumentNo(CalloutInvoiceExt
								.GenerarNumeroDeDocumento(
										getPuntoDeVenta(),
										getNumeroComprobante(),
										getLetra(),
										IsSOTrx,
										(isCopy() || getNumeroComprobante() == nroC)
												&& !isManualDocumentNo()));
					} else
						// Si no es un nuevo registro, siempre usar el indicado
						// (no usar secuencia)
						setDocumentNo(CalloutInvoiceExt
								.GenerarNumeroDeDocumento(getPuntoDeVenta(),
										getNumeroComprobante(), getLetra(),
										IsSOTrx, false));

				} else {
					if ((getPuntoDeVenta() == 0 || getNumeroComprobante() == 0)
							&& isManualDocumentNo()
							&& !completarPuntoLetraNumeroDoc()) {
						return false;
					}
					setDocumentNo(CalloutInvoiceExt.GenerarNumeroDeDocumento(
							getPuntoDeVenta(), getNumeroComprobante(),
							getLetra(), IsSOTrx));
				}

				setNumeroDeDocumento(getDocumentNo());

			} catch (Exception e) {
				log.saveError("SaveError", e);
				e.printStackTrace();
				return false;
			}

			// Letra de comprobante = categoria iva
			if ((this.isSOTrx()) && (newRecord)) {
				/*
				 * valido que la categoria de impuesto del cliente este correcta
				 * con la letra que tiene la factura
				 */
				if (!this.validarLetraComprobante()) {
					// log.saveError("Error",
					// "No es correcta la letra de comprobante, para el cliente");
					return false;
				}
			}

			// Punto de Venta y Numero de comprobante - Validacion de rango.
			// Dado que los rangos que se pueden configurar en los metadatos
			// de la columna no producen error (solo agregando una nota al log),
			// se hace dicha validación manualmente aquí.
			if (!(getPuntoDeVenta() > 0 && getPuntoDeVenta() < 10000)) {
				log.saveError("SaveError", Msg.getMsg(getCtx(),
						"FieldValueOutOfRange",
						new Object[] { Msg.translate(getCtx(), "PuntoDeVenta"),
								1, 9999 }));
				return false;
			}

			if (!(getNumeroComprobante() > 0 && getNumeroComprobante() < 1000000000)) {
				log.saveError("SaveError", Msg.getMsg(
						getCtx(),
						"FieldValueOutOfRange",
						new Object[] {
								Msg.translate(getCtx(), "NumeroComprobante"),
								1, 99999999 }));
				return false;
			}
			
			// Validar CAI Obligatorio
			if(!isSOTrx() && partner.isMandatoryCAI() && Util.isEmpty(getCAI())){
				log.saveError("MandatoryCAIValidationMsg", "");
				return false;
			}
			
			// Fecha del CAI
			if (getCAI() != null && !getCAI().equals("")
					&& getDateCAI() == null) {
				log.saveError("InvalidCAIDate", "");
				return false;
			}

			// Fecha del CAI > que fecha de facturacion
			if (getDateCAI() != null
					&& getDateInvoiced().compareTo(getDateCAI()) > 0 
					&& !TimeUtil.isSameDay(getDateInvoiced(), getDateCAI())){
				log.saveError("InvoicedDateAfterCAIDate", "");
				return false;
			}
			
		}

		// Quito punto de venta y letra en caso que el tipo de doc no sea fiscal
		if(locale_ar && !docType.isFiscalDocument() && (getPuntoDeVenta() > 0 || getC_Letra_Comprobante_ID() > 0)){
			setPuntoDeVenta(0);
			setC_Letra_Comprobante_ID(0);
		}
		
		// Si es un débito, se aplican las percepciones
		if (isDebit && !isProcessed()) {
			setApplyPercepcion(true);
		}
		
		//Está separada la condición a propósito, porque sino no funciona combinado con las otras condiciones
		if (isDiffCambio()) {
			setApplyPercepcion(false);
		}

		/*
		 * Comprobar si el documento base denota crédito (generalmente una nota
		 * de crédito). Si es, verificar que la factura o comprobante son de las
		 * mismas entidades comerciales
		 */

		// Si el documento base denota un crédito (generalmente una nota de
		// crédito) para el cliente

		if (docType.getDocBaseType().equals("ARC")) {
			// Si tiene una factura o comprobante original
			if (this.getC_Invoice_Orig_ID() != 0) {

				// Obtengo la factura o comprobante original
				MInvoice origin = MInvoice.get(this.getCtx(),
						this.getC_Invoice_Orig_ID(), this.get_TrxName());

				// Si las entidades comerciales del documento y de la factura
				// original no coinciden ----> Error
				if (this.getC_BPartner_ID() != origin.getC_BPartner_ID()) {
					log.saveError("BPartnerInvoiceCustomerCreditNotSame", "");
					return false;
				}
			}
		}

		// No se puede aplicar descuentos manuales generales cuando el documento
		// arrastra descuentos del pedido, siempre y cuando en el pedido se
		// hayan aplicado descuentos
		if (!newRecord && is_ValueChanged("ManualGeneralDiscount")) {
			if (isManageDragOrderDiscountsSurcharges(false)
					&& !isSkipManualGeneralDiscountValidation()) {
				log.saveError("ManualGeneralDiscountNotAllowed", "");
				return false;
			}
			// Actualización de las líneas en base al descuento de la cabecera
			// cuando cambia ese dato (No para TPV)
			if (!updateManualGeneralDiscount()) {
				log.saveError("", CLogger.retrieveErrorAsString());
				return false;
			}
		}

		// Canje
		if ((this.isSOTrx()) && (this.getC_Order_ID() != 0)) {
			MOrder order = new MOrder(getCtx(), this.getC_Order_ID(),
					get_TrxName());
			/*
			 * Valido que si el pedido asociado a la factura no tenia el check
			 * Canje marcado la factura tampoco lo tenga.
			 */
			if ((!order.isExchange()) && (this.isExchange())) {
				log.saveError("OrderWithoutExchange",
						Msg.translate(getCtx(), "OrderWithoutExchange"));
				return false;
			} else {
				/*
				 * Valido que si el pedido asociado a la factura tenia el check
				 * Canje marcado la factura tambien lo tenga.
				 */
				if ((order.isExchange()) && (!this.isExchange())) {
					log.saveError("InvoiceWithoutExchange",
							Msg.translate(getCtx(), "InvoiceWithoutExchange"));
					return false;
				}
			}
		}

		// Si no tiene hora ni minutos ni segundos, significa que es un date sin
		// hora y hay que setearle la hora de ahora
		Calendar dateInvoicedCalendar = Calendar.getInstance();
		dateInvoicedCalendar.setTimeInMillis(getDateInvoiced().getTime());
		if (dateInvoicedCalendar.get(Calendar.HOUR_OF_DAY) == 0
				&& dateInvoicedCalendar.get(Calendar.MINUTE) == 0
				&& dateInvoicedCalendar.get(Calendar.SECOND) == 0) {
			Calendar nowCalendar = Calendar.getInstance();
			dateInvoicedCalendar.set(Calendar.HOUR_OF_DAY,
					nowCalendar.get(Calendar.HOUR_OF_DAY));
			dateInvoicedCalendar.set(Calendar.MINUTE,
					nowCalendar.get(Calendar.MINUTE));
			dateInvoicedCalendar.set(Calendar.SECOND,
					nowCalendar.get(Calendar.SECOND));
			setDateInvoiced(new Timestamp(
					dateInvoicedCalendar.getTimeInMillis()));
		}
		
		// Compras
		if (!isSkipAuthorizationChain()){
			//Se determina la cadena de autorización para la factura de proveedor
			setM_AuthorizationChain_ID(DB.getSQLValue(get_TrxName(), 
					"SELECT audt.M_AuthorizationChain_ID FROM M_AuthorizationChainDocumentType audt "
					+ " INNER JOIN M_AuthorizationChain au ON au.M_AuthorizationChain_ID = audt.M_AuthorizationChain_ID "
					+ " WHERE audt.C_DocType_ID = ? "
					+ ((getAD_Org_ID() != 0)? " AND (audt.AD_Org_ID = " + getAD_Org_ID() + " OR audt.AD_Org_ID = 0) " : "" ) 
					+ " AND au.isActive = 'Y' "
					+ " ORDER BY audt.AD_Org_ID desc LIMIT 1 ", 
					((getC_DocTypeTarget_ID()!=0)?getC_DocTypeTarget_ID():getC_DocType_ID()), 
					false));
			// Se verifica si está repetido el comprobante para compras
			if(isRepeatInvoice(newRecord)){
				log.saveError("RepeatInvoice", getRepeatInvoiceMsg());
				return false;
			}
		} 
		
		if (!isSOTrx()) {
			// Si la Tarifa de la Factura tiene activo el campo “Actualizar Precios con Factura de Compra” y 
			// si la Moneda de la Factura de Proveedor es diferente a la moneda de la Tarifa seleccionada para la Factura
			// El campo Fecha de TC para Actualizar Precios debe ser obligatorio
			if(getC_Currency_ID() != priceListCurrency && priceList.isActualizarPreciosConFacturaDeCompra() && getFechadeTCparaActualizarPrecios()==null){
				log.saveError("Error", Msg.translate(getCtx(), "FechadeTCparaActualizarPreciosMandatory"));
				return false;
			}
			
			// Si se modificó el esquema de vencimientos y la factura se encuentra
			// en un lote de pagos, entonces error
			if(!newRecord && is_ValueChanged("C_PaymentTerm_ID")){
				MPaymentBatchPO paymentBatch = MPaymentBatchPO.getFromInvoice(getCtx(), getID(), get_TrxName());
				if(paymentBatch != null){
					log.saveError("SaveError", Msg.getMsg(getCtx(), "InvoiceInPaymentBatchPO",
							new Object[] { paymentBatch.getDocumentNo() }));
					return false;
				}
			}
		}
		
		return true;
	} // beforeSave

	/**
	 * Actualiza el descuento manual general
	 * 
	 * @return true si fue posible la actualización, false caso contrario
	 */
	public boolean updateManualGeneralDiscount() {
		if (isSkipManualGeneralDiscount())
			return true;

		int stdPrecision = MPriceList.getStandardPrecision(getCtx(),
				getM_PriceList_ID());
		try {
			// Actualización del descuento de líneas
			updateManualGeneralDiscountToLines(stdPrecision);
		} catch (Exception e) {
			log.saveError("", !Util.isEmpty(e.getMessage()) ? e.getMessage()
					: e.getCause() != null ? e.getCause().getMessage() : "");
			return false;
		}
		return true;
	}

	/**
	 * Actualización de líneas en base al descuento cargado en la cabecera
	 * 
	 * @param scale
	 * @throws Exception
	 */
	public void updateManualGeneralDiscountToLines(int scale) throws Exception {
		for (MInvoiceLine invoiceLine : getLines()) {
			invoiceLine.updateGeneralManualDiscount(getManualGeneralDiscount(),
					scale);
			invoiceLine.setSkipManualGeneralDiscount(true);
			if (!invoiceLine.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
	}

	/**
	 * Actualizo el porcentaje de descuento de la cabecera con la suma de los
	 * descuentos de las líneas
	 * 
	 * @param scale
	 */
	public void updateManualGeneralDiscountByLines(int scale) {
		if (getGrandTotal().compareTo(BigDecimal.ZERO) == 0)
			return;
		BigDecimal totalLineDiscountAmt = getSumColumnLines("LineDiscountAmt");
		// Obtengo el porcentaje de descuento en base al grandtotal y a la suma
		// de los descuentos
		BigDecimal discountManualPerc = totalLineDiscountAmt.multiply(
				new BigDecimal(100)).divide(getGrandTotal(), scale,
				BigDecimal.ROUND_HALF_UP);
		setManualGeneralDiscount(discountManualPerc);
	}

	/**
	 * Obtengo la suma de una columna numérica de las líneas de esta factura
	 * 
	 * @param numericColumnName
	 *            nombre de la columna numérica de la línea
	 * @return la suma de esa columna numérica de las líneas de la factura
	 */
	protected BigDecimal getSumColumnLines(String numericColumnName) {
		// Obtengo la suma de los descuentos de las líneas
		String sql = "SELECT sum(" + numericColumnName
				+ ") FROM c_invoiceline WHERE c_invoice_id = ?";
		BigDecimal totalLineAmt = DB.getSQLValueBD(get_TrxName(), sql, getID());
		totalLineAmt = totalLineAmt == null ? BigDecimal.ZERO : totalLineAmt;
		return totalLineAmt;
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	private boolean validarLetraComprobante() {
		// Parametros

		MClient vCompania = new MClient(getCtx(),
				Env.getAD_Client_ID(getCtx()), get_TrxName());
		// Integer vCategoriaIva = vCompania.getCategoriaIva();

		// dREHER, traer categoria de iva de la compania, respetando orden
		// jerarquico org hija, padre, compania
		// Llamo pasando como parametro la organizacion del documento
		Integer vCategoriaIva = vCompania.getCategoriaIva(getAD_Org_ID());

		MBPartner vCliente = new MBPartner(getCtx(), this.getC_BPartner_ID(),
				get_TrxName());
		boolean value = false;

		// dREHER, debug TODO: quitar luego...
		log.fine("MInvoice.validarLetraComprobante = ***** Dio algun error de compatibilidad entre ambas cat de iva compania="
				+ vCategoriaIva
				+ "\n"
				+ "cliente="
				+ vCliente.getC_Categoria_Iva_ID()
				+ "\n"
				+ "letra="
				+ this.getC_Letra_Comprobante_ID());

		//
		if (vCategoriaIva == 0) {
			// no existen alguno de los datos a validar, devuelvo verdadero
			log.saveError("SaveError",
					Msg.translate(Env.getCtx(), "ClientWithoutIVAError"));
			value = false;
		} else if (vCliente.getC_Categoria_Iva_ID() == 0) {
			log.saveError("SaveError",
					Msg.translate(Env.getCtx(), "BPartnerWithoutIVAError"));
			value = false;
		} else {
			// todos los parametros de la busqueda existe, busco a ver si es
			// correcta la clasificacion del iva
			StringBuffer sql = new StringBuffer("SELECT * "
					+ "	FROM C_Letra_Acepta_IVA "
					+ "   WHERE categoria_vendor = ? "
					+ "         AND categoria_customer = ? "
					+ "         AND c_letra_comprobante_Id = ? ");

			try {
				PreparedStatement pstmt = DB.prepareStatement(sql.toString());
				pstmt.setInt(1, vCategoriaIva);
				pstmt.setInt(2, vCliente.getC_Categoria_Iva_ID());
				pstmt.setInt(3, this.getC_Letra_Comprobante_ID());
				ResultSet rs = pstmt.executeQuery();

				value = rs.next();

				if (!value) {
					log.saveError("SaveError", Msg.translate(Env.getCtx(),
							"InvalidLetraComprobanteError"));
				}

				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, sql.toString(), e);
			}
		}
		return value;
	}

	/**
	 * Verifica si la factura se encuentra registrada en el sistema, lo que
	 * provoca facturas repetidas. El criterio para verificar unicidad se
	 * realiza en base a los siguientes campos:
	 * <ul>
	 * <li>CUIT del bpartner relacionado con la factura</li>
	 * <li>Punto de Venta</li>
	 * <li>Número de Factura</li>
	 * <li>Letra de la factura</li>
	 * </ul>
	 * 
	 * @return true si existe una factura con los mismos datos, false cc
	 */
	private boolean isRepeatInvoice() {
		return isRepeatInvoice(false);
	}

	/**
	 * Verifica si la factura se encuentra registrada en el sistema, lo que
	 * provoca facturas repetidas. El criterio para verificar unicidad se
	 * realiza en base a los siguientes campos:
	 * <ul>
	 * <li>CUIT del bpartner relacionado con la factura</li>
	 * <li>Punto de Venta</li>
	 * <li>Número de Factura</li>
	 * <li>Letra de la factura</li>
	 * </ul>
	 * 
	 * @param newRecord registro nuevo
	 * @return true si existe una factura con los mismos datos, false cc
	 */
	private boolean isRepeatInvoice(boolean newRecord) {
		/*
		 * Si la factura posee monto negativo -> es una contrafactura por
		 * anulación. Permitir repetido
		 */
		if (getGrandTotal().compareTo(Env.ZERO) < 0)
			return false;

		/* Si la factura posee contra-documento, omitir validacion */
		if (getRef_Invoice_ID() > 0)
			return false;

		/* Si la factura es una copia de un original, omitir validación */
		if (isCopy())
			return false;

		// Para facturas de venta, si está activo locale ar y el tipo de
		// documento requiere impresión fiscal entonces no debo controlar
		// factura repetida
		if (isSOTrx() && (requireFiscalPrint() || isElectronicInvoice())
				&& !isManualDocumentNo()) {
			return false;
		}

		// No controlar cuando son comprobantes de retención
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		if (docType.getDocTypeKey().equals(MDocType.DOCTYPE_Retencion_Receipt)
				|| docType.getDocTypeKey().equals(
						MDocType.DOCTYPE_Retencion_ReceiptCustomer)) {
			return false;
		}
		
		// Condiciones comunes entre issotrx=Y y issotrx=N
		StringBuffer whereClause = new StringBuffer();
		String invoiceIDWC = newRecord?"":" AND (c_invoice_id <> ?) ";
		whereClause.append(" (issotrx = ?) AND (documentno = ?) AND (c_doctypetarget_id = ?) ");
		whereClause.append(invoiceIDWC);
		// Con el tema de nros de documento manuales en realidad un documento
		// anulado impreso fiscalmente existe físicamente por lo tanto también
		// se debe tener en cuenta en la validación
		if (isSOTrx() && requireFiscalPrint() && isManualDocumentNo()) {
			whereClause
					.append(" AND (docStatus in ('CO', 'CL', 'VO', 'RE') AND fiscalalreadyprinted = 'Y') ");
		} else {
			whereClause.append(" AND docStatus in ('CO', 'CL') ");
		}
		List<Object> whereParams = new ArrayList<Object>();
		whereParams.add(isSOTrx() ? "Y" : "N");
		whereParams.add(getDocumentNo());
		whereParams.add(getC_DocTypeTarget_ID());
		if(!newRecord){
			whereParams.add(getID());
		}
		if (!isSOTrx()) {
			// Si locale_ar, entonces validamos por cuit
			if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
				whereClause.append(" AND (cuit = ?) ");
				// TODO: Sacar la instanciación de la entidad para obtener el
				// cuit, se dieron casos en que no se seteaba el cuit de la
				// factura, verificar si eso no pasa mas, por las dudas la
				// obtenemos de la entidad comercial
				MBPartner bpartner = new MBPartner(getCtx(),
						getC_BPartner_ID(), get_TrxName());
				whereParams.add(bpartner.getTaxID());
			} else {
				whereClause.append(" AND (c_bpartner_id = ?) ");
				whereParams.add(getC_BPartner_ID());
			}
		}
		// Armar el sql
		String sql = "SELECT c_invoice_id FROM c_invoice WHERE "
				+ whereClause.toString();

		Object res = DB.getSQLObject(this.get_TrxName(), sql,
				whereParams.toArray());

		// true si existe una factura
		return res != null;
	}
	
	protected String getRepeatInvoiceMsg(){
		StringBuffer msgParams = new StringBuffer(" \n\n ");
		msgParams.append(" " + Msg.translate(getCtx(), "DocumentNo") + " ")
				.append("\n");
		msgParams.append(" " + Msg.translate(getCtx(), "C_DocType_ID")
				+ " ");
		if (!isSOTrx()) {
			if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
				msgParams.append("\n").append(" CUIT ");
			} else {
				msgParams.append("\n").append(
						" " + Msg.translate(getCtx(), "C_BPartner_ID")
								+ " ");
			}
		}
		return msgParams.toString();
	}
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public String toString() {
		StringBuffer sb = new StringBuffer("MInvoice[").append(getID())
				.append("-").append(getDocumentNo()).append(",GrandTotal=")
				.append(getGrandTotal());

		if (m_lines != null) {
			sb.append(" (#").append(m_lines.length).append(")");
		}

		sb.append("]");

		return sb.toString();
	} // toString

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param newRecord
	 * @param success
	 * 
	 * @return
	 */

	protected boolean afterSave(boolean newRecord, boolean success) {
		// POSSImple:
		if (this.skipAfterAndBeforeSave)
			return success;

		if (!success || newRecord) {
			return success;
		}

		boolean recalculateIPS = false;

		if (is_ValueChanged("AD_Org_ID")) {
			String sql = "UPDATE C_InvoiceLine ol"
					+ " SET AD_Org_ID ="
					+ "(SELECT AD_Org_ID"
					+ " FROM C_Invoice o WHERE ol.C_Invoice_ID=o.C_Invoice_ID) "
					+ "WHERE C_Invoice_ID=" + getC_Order_ID();
			int no = DB.executeUpdate(sql, get_TrxName());

			log.fine("Lines -> #" + no);
		}

		if (!isTPVInstance() && 
				!isVoidProcess() && 
				(is_ValueChanged("AD_Org_ID") 
					|| is_ValueChanged("C_BPartner_ID")
					|| is_ValueChanged("ApplyPercepcion")
					|| is_ValueChanged("C_Invoice_Orig_ID"))) {
			try {

				recalculatePercepciones();
				updateGrandTotal(get_TrxName());
				recalculateIPS = true;
			} catch (Exception e) {
				log.severe("ERROR generating percepciones. " + e.getMessage());
				e.printStackTrace();
			}
		}

		// Esquemas de pagos
		// Si se modificó el campo del esquema de vencimientos entonces
		// actualizo el esquema de pagos de la factura
		if (!isSkipApplyPaymentTerm()
				&& (is_ValueChanged("C_PaymentTerm_ID")
						|| is_ValueChanged("DateRecepted") 
						|| recalculateIPS)) {
			// Vuelvo a cargar la factura desde BD
			MInvoice invoiceUpdated = new MInvoice(getCtx(), getID(),
					get_TrxName());
			MPaymentTerm pt = new MPaymentTerm(getCtx(),
					invoiceUpdated.getC_PaymentTerm_ID(),
					invoiceUpdated.get_TrxName());
			return pt.apply(invoiceUpdated);
		}

		return true;
	} // afterSave

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param M_PriceList_ID
	 */

	public void setM_PriceList_ID(int M_PriceList_ID) {
		String sql = "SELECT M_PriceList_ID, C_Currency_ID "
				+ "FROM M_PriceList WHERE M_PriceList_ID=?";
		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, M_PriceList_ID);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				super.setM_PriceList_ID(rs.getInt(1));
				setC_Currency_ID(rs.getInt(2));
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			log.log(Level.SEVERE, "setM_PriceList_ID", e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}

			pstmt = null;
		}
	} // setM_PriceList_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public BigDecimal getAllocatedAmt() {
		return getAllocatedAmt(false, false, false, null);
	} // getAllocatedAmt

	public BigDecimal getAllocatedAmt(boolean inCredit, boolean inCash,
			boolean inPayment, String paymentTenderType) {
		BigDecimal retValue = null;
		StringBuffer sql = new StringBuffer(
				"SELECT SUM(currencyConvert(al.Amount+al.DiscountAmt+al.WriteOffAmt,"
						+ "ah.C_Currency_ID, i.C_Currency_ID,ah.DateTrx,i.C_ConversionType_ID, al.AD_Client_ID,al.AD_Org_ID)) "
						+ "FROM C_AllocationLine al"
						+ " INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID=ah.C_AllocationHdr_ID)"
						+ " INNER JOIN C_Invoice i ON (al.C_Invoice_ID=i.C_Invoice_ID) "
						+ "WHERE al.C_Invoice_ID=?"
						+ (!CounterAllocationManager.isCounterAllocationActive(getCtx()) ?
									" AND ah.IsActive='Y' AND al.IsActive='Y'" // Logica tradicional 
								:
									/* Validación de Allocation anulado. 
									 * A partir de la generación del contra allocation el encabezado y las líneas no quedan en IsActive='N'. 
									 * De todas formas es necesario mantener la condición para los allocation previos. */
									" AND ((ah.IsActive='Y' AND al.IsActive='Y') AND (ah.docaction NOT IN ('VO', 'RE') AND ah.docstatus NOT IN ('VO', 'RE')))"		
						)
				);
		boolean inAPaymentType = inCredit || inCash || inPayment;
		sql.append(inAPaymentType ? " AND ( " : "");
		StringBuffer inAPaymentWhereCondition = new StringBuffer();
		if (inCredit) {
			inAPaymentWhereCondition
					.append("  (al.c_invoice_credit_id is not null AND al.c_invoice_credit_id > 0) ");
		}
		if (inCash) {
			inAPaymentWhereCondition
					.append(inAPaymentWhereCondition.length() > 0 ? " OR " : "")
					.append("  (al.c_cashline_id is not null AND al.c_cashline_id > 0) ");
		}
		if (inPayment) {
			inAPaymentWhereCondition
					.append(inAPaymentWhereCondition.length() > 0 ? " OR " : "")
					.append("  (al.c_payment_id is not null AND al.c_payment_id > 0 AND EXISTS (SELECT p.c_payment_id FROM c_payment p WHERE p.c_payment_id = al.c_payment_id AND p.tendertype = '"
							+ paymentTenderType + "')) ");
		}
		sql.append(inAPaymentWhereCondition);
		sql.append(inAPaymentType ? " ) " : "");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, getC_Invoice_ID());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				retValue = rs.getBigDecimal(1);
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "getAllocatedAmt", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();

				rs = null;
				pstmt = null;
			} catch (Exception e) {
				rs = null;
				pstmt = null;
			}
		}

		return retValue;
	}

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean testAllocation() {
		BigDecimal alloc = getAllocatedAmt(); // absolute

		if (alloc == null) {
			alloc = Env.ZERO;
		}

		BigDecimal total = getGrandTotal();

		if (!isSOTrx()) {
			total = total.negate();
		}

		if (isCreditMemo()) {
			total = total.negate();
		}

		boolean test = total.abs().compareTo(alloc.abs()) <= 0;
		boolean change = test != isPaid();

		if (change) {
			setIsPaid(test);
		}

		log.fine("testAllocation - Paid=" + test + " (" + alloc + "=" + total
				+ ")");

		return change;
	} // testAllocation

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public BigDecimal getOpenAmt() {
		return getOpenAmt(true, null);
	} // getOpenAmt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param creditMemoAdjusted
	 * @param paymentDate
	 * 
	 * @return
	 */

	public BigDecimal getOpenAmt(boolean creditMemoAdjusted,
			Timestamp paymentDate) {
		if (isPaid()) {
			return Env.ZERO;
		}

		//

		if (m_openAmt == null) {
			m_openAmt = getGrandTotal();

			if (paymentDate != null) {

				// Payment Discount
				// Payment Schedule

			}

			BigDecimal allocated = getAllocatedAmt();

			if (allocated != null) {
				allocated = allocated.abs(); // is absolute
				m_openAmt = m_openAmt.subtract(allocated);
			}
		}

		//

		if (!creditMemoAdjusted) {
			return m_openAmt;
		}

		if (isCreditMemo()) {
			return m_openAmt.negate();
		}

		return m_openAmt;
	} // getOpenAmt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public String getDocStatusName() {
		return MRefList.getListName(getCtx(),
				MInvoice.DOCSTATUS_AD_Reference_ID, getDocStatus());
	} // getDocStatusName

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public File createPDF() {
		return createPDF(null);
	} // getPDF

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param file
	 * 
	 * @return
	 */

	public File createPDF(File file) {
		ReportEngine re = ReportEngine.get(getCtx(), ReportEngine.INVOICE,
				getC_Invoice_ID());

		if (re == null) {
			return null;
		}

		return re.getPDF(file);
	} // getPDF

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param documentDir
	 * 
	 * @return
	 */

	public String getPDFFileName(String documentDir) {
		return getPDFFileName(documentDir, getC_Invoice_ID());
	} // getPDFFileName

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public String getCurrencyISO() {
		return MCurrency.getISO_Code(getCtx(), getC_Currency_ID());
	} // getCurrencyISO

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public int getPrecision() {
		return MCurrency.getStdPrecision(getCtx(), getC_Currency_ID());
	} // getPrecision

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param processAction
	 * 
	 * @return
	 */

	public boolean processIt(String processAction) {
		m_processMsg = null;

		DocumentEngine engine = new DocumentEngine(this, getDocStatus());

		boolean status = engine.processIt(processAction, getDocAction(), log);

		status = this.afterProcessDocument(engine.getDocAction(), status)
				&& status;

		return status;
	} // process

	/** Descripción de Campos */

	private boolean m_justPrepared = false;

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean unlockIt() {
		log.info("unlockIt - " + toString());
		setProcessing(false);

		return true;
	} // unlockIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean invalidateIt() {
		log.info("invalidateIt - " + toString());
		setDocAction(DOCACTION_Prepare);

		return true;
	} // invalidateIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public String prepareIt() {
		log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,
				ModelValidator.TIMING_BEFORE_PREPARE);

		if (m_processMsg != null) {
			return DocAction.STATUS_Invalid;
		}

		MDocType dt = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		boolean isDebit = !dt.getDocBaseType().equals(
				MDocType.DOCBASETYPE_ARCreditMemo)
				&& !dt.getDocBaseType().equals(
						MDocType.DOCBASETYPE_APCreditMemo);

		// Std Period open?

		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), dt)) {
			m_processMsg = "@PeriodClosed@";

			return DocAction.STATUS_Invalid;
		}

		// Si la moneda del documento es diferente a la de la compañia:
		// Se valida que exista una tasa de conversión entre las monedas para la
		// fecha de aplicación del documento.
		if (!validateInvoiceCurrencyConvert()) {
			m_processMsg = "@NoConversionRateDateAcct@";
			return DocAction.STATUS_Invalid;
		}

		// Si la Tarifa es mayor a 0 setear el Impuesto Incluido a partir de la
		// tarifa
		if (getM_PriceList_ID() > 0) {
			setIsTaxIncluded(new MPriceList(getCtx(), getM_PriceList_ID(), null)
					.isTaxIncluded());
		}

		// Lines

		MInvoiceLine[] lines = getLines(true);

		if (lines.length == 0) {
			m_processMsg = "@NoLines@";

			return DocAction.STATUS_Invalid;
		}

		if (PAYMENTRULE_Cash.equals(getPaymentRule())
				&& isCreateCashLine()
				&& (MCashBook.get(getCtx(), getAD_Org_ID(), getC_Currency_ID(),
						null) == null)) {
			m_processMsg = "@NoCashBook@";

			return DocAction.STATUS_Invalid;
		}

		// Convert/Check DocType

		if (getC_DocType_ID() != getC_DocTypeTarget_ID()) {
			setC_DocType_ID(getC_DocTypeTarget_ID());
		}

		if (getC_DocType_ID() == 0) {
			m_processMsg = "No Document Type";

			return DocAction.STATUS_Invalid;
		}

		explodeBOM();

		if (!calculateTaxTotal()) // setTotals
		{
			m_processMsg = "Error calculating Tax";

			return DocAction.STATUS_Invalid;
		}

		// Actualiza el neto para corregirlo
		setNetAmount(getNetAmount(get_TrxName()));

		createPaySchedule();

		// Modified by Matías Cap - Disytel
		// ---------------------------------------------------------------
		// Las validaciones de crédito se realizan en las nuevas clases
		// encargadas de eso.
		// Obtener el cliente
		MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(),
				get_TrxName());
		// Obtener la organización
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName());
		if (!isCurrentAccountVerified && isSOTrx() && isDebit
				&& getPaymentRule().equals(MInvoice.PAYMENTRULE_OnCredit)) {
			// Obtengo el manager actual
			CurrentAccountManager manager = CurrentAccountManagerFactory
					.getManager(this);
			// Seteo el estado actual del cliente y lo obtengo
			CallResult result = new CallResult();
			try {
				result = manager.setCurrentAccountStatus(getCtx(), bp, org,
						get_TrxName());
			} catch (Exception e) {
				result.setMsg(e.getMessage(), true);
			}
			// Si hubo error, obtengo el mensaje y retorno inválido
			if (result.isError()) {
				m_processMsg = result.getMsg();
				return DocAction.STATUS_Invalid;
			}
			// Verificar la situación de crédito de la entidad comercial
			try {
				result = manager.validateCurrentAccountStatus(getCtx(), org,
						bp, (String) result.getResult(), get_TrxName());
			} catch (Exception e) {
				result.setMsg(e.getMessage(), true);
			}
			// Si hubo error, obtengo el mensaje y retorno inválido
			if (result.isError()) {
				m_processMsg = result.getMsg();
				return DocAction.STATUS_Invalid;
			}
		}
		// ---------------------------------------------------------------

		// - Validaciones generales (AR)
		if (!validateInvoice(bp)) {
			m_processMsg = CLogger.retrieveErrorAsString();
			return DocAction.STATUS_Invalid;
		}
		// -

		// Add up Amounts

		m_justPrepared = true;

		if (!DOCACTION_Complete.equals(getDocAction())) {
			setDocAction(DOCACTION_Complete);
		}

		// Verificar si las líneas están relacionadas con un pedido y
		// dependiendo el tipo de documento se deben controlar las cantidades
		// que no se excedan. Para débitos se debe controlar la cantidad
		// facturada con la pedida, para créditos la cantidad reservada con la
		// pedida
		MInvoiceLine line;
		MOrderLine orderLine;
		for (int i = 0; i < lines.length; i++) {
			line = lines[i];
			if (line.getC_OrderLine_ID() != 0) {
				// Ader: NO LEER cosas de manera innecesria...
				// orderLine = new MOrderLine(getCtx(),
				// line.getC_OrderLine_ID(), get_TrxName());
				if (!isDebit) {
					// ADER: fix temporal hacer la lectura de las linea aca;
					// igual
					// en general no tiene sentido hacer este chequeo con N
					// accesos...
					// se tiene que poder hacer con un solo select
					// Mas alla de esto hay un error conceptual al usar
					// qtyEntered en vez
					// de QtyInvoiced. Otro error esta en NO todas las lineas
					// de un pedido son afectadas en el campo QtyReserved... ver
					// MOrder.reserverStock
					// o MOrder.reserveStockII
					orderLine = new MOrderLine(getCtx(),
							line.getC_OrderLine_ID(), get_TrxName());

					// Se debe controlar que la cantidad ingresada no supere la
					// cantidad pendiente del pedido o la facturada del pedido,
					// esto depende de cuál es la cantidad menor.
					// Si la cantidad facturada es menor a la cantidad
					// pendiente, se controla la cantidad facturada, sino la
					// pendiente.
					// Las cantidades de Devoluciones de Cliente sin Notas de
					// Crédito asociadas no se pueden sacar sobre notas de
					// crédito por mercadería no retirada ya que luego no
					// concuerdan los movimientos de mercadería con lo facturado
					BigDecimal dcNoNC = isUpdateOrderQty() ? MInOut
							.getDCMovementQty(orderLine.getID(),
									get_TrxName()) : null;
					dcNoNC = dcNoNC != null && dcNoNC.compareTo(BigDecimal.ZERO) > 0?dcNoNC:BigDecimal.ZERO;
					BigDecimal pending = orderLine.getPendingDeliveredQty().subtract(dcNoNC);
					boolean reservedGreater = pending.compareTo(orderLine.getQtyInvoiced()) > 0;
					BigDecimal qtyToCompare = reservedGreater ? orderLine
							.getQtyInvoiced() : pending;
					String lastMsgDescription = reservedGreater ? "Invoiced"
							: "Reserved";
					if (line.getQtyInvoiced().compareTo(qtyToCompare) > 0) {
						m_processMsg = "@InvoiceLineExceedsQty"
								+ lastMsgDescription + "@";
						return DocAction.STATUS_Invalid;
					}
				}
				// Si es débito se debe verificar la suma de la cantidad
				// ingresada con la cantidad facturada.
				// FIXME Por ahora comentado, será necesario?? Es mucha
				// restricción colocar esta validación?
				/*
				 * else{ orderLine = new MOrderLine(getCtx(),
				 * line.getC_OrderLine_ID(), get_TrxName()); qty =
				 * orderLine.getQtyInvoiced().add(line.getQtyEntered());
				 * if(qty.compareTo(orderLine.getQtyOrdered()) > 0){
				 * m_processMsg = "@InvoiceLineExceedsQtyOrdered@"; return
				 * DocAction.STATUS_Invalid; } }
				 */
			}
		}

		// Verificar factura repetida
		if (isRepeatInvoice()) {
			m_processMsg = "@RepeatInvoice@" + getRepeatInvoiceMsg();
			return DocAction.STATUS_Invalid;
		}

		// === Lógica adicional para evitar doble notificación a AFIP. ===
		if (CalloutInvoiceExt.ComprobantesFiscalesActivos() && MDocType.isElectronicDocType(getC_DocTypeTarget_ID())) {
			// Si la factura se encuentra en estado En Proceso...
			// ¿Se debe informar al usuario que la factura se encontraba en IP y que no tenía un CAE (requiere validar en AFIP si está registrada),
			// o bien debe dejar continuar (skip de la validacion dado que ya fue gestionada por el usuario) para que genere el CAE al completar?
			if ((getcae() == null || getcae().length() == 0) && DocAction.STATUS_InProgress.equals(getDocStatus()) && !isSkipIPNoCaeValidation()) {
				m_processMsg = "Factura de tipo electrónica con estado en proceso.  Validar registración de la misma en AFIP y posteriormente utilizar funcionalidad Gestionar Factura Electronica";
				log.log(Level.SEVERE, m_processMsg);
				return DocAction.STATUS_Invalid;
			}
			
			// Si en el CompleteIt ya tenemos CAE asignado, validar que sea único no permitiendo completar la factura si ya existe otra Completa / Cerrada con el mismo CAE
			// Esta validación logicamente no aplica para facturas "nuevas" ya que previo al completeIt todavía no tienen CAE
			if (!Util.isEmpty(getcae(), true)) {
				String documentNo = DB.getSQLValueString(get_TrxName(), " SELECT documentNo " +
																		" FROM C_Invoice " +
																		" WHERE cae = '" + getcae() + "' " +
																		" AND C_Invoice_ID <> " + getC_Invoice_ID() +
																		" AND AD_Client_ID = " + getAD_Client_ID() +
																		" AND DocStatus IN ('CO', 'CL') "
														);
				if (documentNo != null) {
					m_processMsg = "Ya existe una factura con CAE " + getcae() + ", registrado en la factura " + documentNo;
					log.log(Level.SEVERE, m_processMsg);
					return DocAction.STATUS_Invalid;
				}
			}
			
			// Si el usuario utilizó "Gestionar Factura Electrónica", validar unicidad del Nro de Documento.
			// Para el mismo Tipo de Documento no pueden existir 2 facturas Completas / Cerradas con el mismo DocumentNo.
			if (!Util.isEmpty(getcaeerror()) && getcaeerror().startsWith("Factura electronica editada manualmente")) {
				int count = DB.getSQLValue(get_TrxName(), 	" SELECT count(1) " +
															" FROM C_Invoice " +
															" WHERE documentno = '" + getDocumentNo() + "' " +
															" AND C_Invoice_ID <> " + getC_Invoice_ID() +
															" AND AD_Client_ID = " + getAD_Client_ID() +
															" AND C_DocTypeTarget_ID = " + getC_DocTypeTarget_ID() +
															" AND DocStatus IN ('CO', 'CL') "
											);
				if (count > 0) {
					m_processMsg = "Ya existe una factura con el numero de documento " + getDocumentNo();
					log.log(Level.SEVERE, m_processMsg);
					return DocAction.STATUS_Invalid;
				}
			}
			
		}
		
		// Fecha del CAI > que fecha de facturacion
		if (getDateCAI() != null
				&& getDateInvoiced().compareTo(getDateCAI()) > 0 
				&& !TimeUtil.isSameDay(getDateInvoiced(), getDateCAI())){
			setProcessMsg("@InvoicedDateAfterCAIDate@");
			return DocAction.STATUS_Invalid;
		}
		
		// Si el tipo de doc es electrónico, las fechas de comprobante y
		// contable no pueden diferir. 
		if(dt.iselectronic()){
			if(!TimeUtil.isSameDay(getDateAcct(), getDateInvoiced())){
				setProcessMsg("@DateInvoicedDateAcctNE@");
				return DocAction.STATUS_Invalid;
			}
			
			// Controlar que la fecha de facturación sea mayor o igual que la última emitida
			// Sino error
			Timestamp lastDateFE = getLastFEDateIssued(getCtx(), getC_DocTypeTarget_ID(), getID(), get_TrxName());
			if (lastDateFE != null && lastDateFE.after(getDateInvoiced())
					&& !TimeUtil.isSameDay(lastDateFE, getDateInvoiced())) {
				setProcessMsg(
						Msg.getMsg(getCtx(), "LastFEDateGreater", new Object[] { Env.getDateFormatted(lastDateFE) }));
				return DocAction.STATUS_Invalid;
			}
		}
		
		return DocAction.STATUS_InProgress;
	} // prepareIt

	/**
	 * Descripción de Método
	 * 
	 */

	private void explodeBOM() {
		String where = "AND IsActive='Y' AND EXISTS "
				+ "(SELECT * FROM M_Product p WHERE C_InvoiceLine.M_Product_ID=p.M_Product_ID"
				+ " AND p.IsBOM='Y' AND p.IsVerified='Y' AND p.IsStocked='N')";

		//

		String sql = "SELECT COUNT(*) FROM C_InvoiceLine "
				+ "WHERE C_Invoice_ID=? " + where;
		int count = DB.getSQLValue(get_TrxName(), sql, getC_Invoice_ID());

		while (count != 0) {
			renumberLines(100);

			// Order Lines with non-stocked BOMs

			MInvoiceLine[] lines = getLines(where);

			for (int i = 0; i < lines.length; i++) {
				MInvoiceLine line = lines[i];
				MProduct product = MProduct.get(getCtx(),
						line.getM_Product_ID());

				log.fine(product.getName());

				// New Lines

				int lineNo = line.getLine();
				MProductBOM[] boms = MProductBOM.getBOMLines(product);

				for (int j = 0; j < boms.length; j++) {
					MProductBOM bom = boms[j];
					MInvoiceLine newLine = new MInvoiceLine(this);

					newLine.setLine(++lineNo);
					newLine.setM_Product_ID(bom.getProduct().getM_Product_ID(),
							bom.getProduct().getC_UOM_ID());
					newLine.setQty(line.getQtyInvoiced().multiply(
							bom.getBOMQty())); // Invoiced/Entered

					if (bom.getDescription() != null) {
						newLine.setDescription(bom.getDescription());
					}

					//

					newLine.setPrice();
					newLine.save(get_TrxName());
				}

				// Convert into Comment Line

				line.setM_Product_ID(0);
				line.setM_AttributeSetInstance_ID(0);
				line.setPriceEntered(Env.ZERO);
				line.setPriceActual(Env.ZERO);
				line.setPriceLimit(Env.ZERO);
				line.setPriceList(Env.ZERO);
				line.setLineNetAmt(Env.ZERO);

				//

				String description = product.getName();

				if (product.getDescription() != null) {
					description += " " + product.getDescription();
				}

				if (line.getDescription() != null) {
					description += " " + line.getDescription();
				}

				line.setDescription(description);
				line.save(get_TrxName());
			} // for all lines with BOM

			m_lines = null;
			count = DB.getSQLValue(get_TrxName(), sql, getC_Invoice_ID());
			renumberLines(10);
		} // while count != 0
	} // explodeBOM

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean calculateTaxTotal() {
		log.fine("");

		if(isTPVInstance() || isVoidProcess()){
			return true;
		}
		// Delete Taxes

		if (isSOTrx() && !isTPVInstance()) {
			DB.executeUpdate(
					"DELETE FROM C_InvoiceTax WHERE "
							+ " C_Tax_ID IN (SELECT c_tax_id FROM C_Tax ct left join C_TaxCategory ctc on ct.c_taxcategory_id = ctc.c_taxcategory_Id WHERE ctc.isManual = 'N') AND"
							+ " C_Invoice_ID=" + getC_Invoice_ID(),
					get_TrxName());
		}
		m_taxes = null;

		// Lines

		BigDecimal totalLines = Env.ZERO;
		ArrayList taxList = new ArrayList();
		MInvoiceLine[] lines = getLines(true);

		for (int i = 0; i < lines.length; i++) {
			MInvoiceLine line = lines[i];

			// Sync ownership for SO

			if (isSOTrx() && (line.getAD_Org_ID() != getAD_Org_ID())) {
				line.setAD_Org_ID(getAD_Org_ID());
				line.save(get_TrxName());
			}

			Integer taxID = new Integer(line.getC_Tax_ID());

			if (!taxList.contains(taxID) && !isTPVInstance()) {

				MInvoiceTax iTax = MInvoiceTax.get(line, getPrecision(), false,
						get_TrxName()); // current Tax
				MTax cTax = new MTax(getCtx(), taxID, get_TrxName());

				if ((iTax != null) && (!cTax.isCategoriaManual())) {
					iTax.setIsTaxIncluded(isTaxIncluded());

					if (!iTax.calculateTaxFromLines()) {
						return false;
					}

					if (!iTax.save()) {
						return false;
					}

					taxList.add(taxID);
				}
			}

			totalLines = totalLines.add(line.getLineNetAmt());
		}

		// Taxes

		BigDecimal grandTotal = totalLines;
		MInvoiceTax[] taxes = getTaxes(true);

		for (int i = 0; i < taxes.length; i++) {
			MInvoiceTax iTax = taxes[i];
			MTax tax = iTax.getTax();

			if (tax.isSummary()) {
				MTax[] cTaxes = tax.getChildTaxes(false); // Multiple taxes

				for (int j = 0; j < cTaxes.length; j++) {
					MTax cTax = cTaxes[j];
					BigDecimal taxAmt = cTax.calculateTax(iTax.getTaxBaseAmt(),
							isTaxIncluded(), getPrecision());

					// aca tambien cambio por

					if (!cTax.isCategoriaManual()) {

						MInvoiceTax newITax = new MInvoiceTax(getCtx(), 0,
								get_TrxName());
						// aca tambien cambio por

						newITax.setClientOrg(this);
						newITax.setC_Invoice_ID(getC_Invoice_ID());
						newITax.setC_Tax_ID(cTax.getC_Tax_ID());
						newITax.setPrecision(getPrecision());
						newITax.setIsTaxIncluded(isTaxIncluded());
						newITax.setTaxBaseAmt(iTax.getTaxBaseAmt());
						newITax.setTaxAmt(taxAmt);

						if (!newITax.save(get_TrxName())) {
							return false;
						}
					}
					//

					if (!isTaxIncluded()) {
						grandTotal = grandTotal.add(taxAmt);
					}
				}

				if (!iTax.delete(true, get_TrxName())) {
					return false;
				}
			} else {
				if (!isTaxIncluded() || tax.isCategoriaManual()) {
					grandTotal = grandTotal.add(iTax.getTaxAmt());
				}
			}
		}

		// Recalculo el total a partir del importe del cargo
		grandTotal = grandTotal.add(getChargeAmt());

		setTotalLines(totalLines);
		setGrandTotal(grandTotal);

		// Calcular las percepciones
		try {
			if (!isTPVInstance()) {
				recalculatePercepciones();
			}
		} catch (Exception e) {
			log.severe("ERROR generating percepciones. " + e.getMessage());
			e.printStackTrace();
		}

		return true;
	} // calculateTaxTotal

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	private BigDecimal totalTax(boolean onlymanualTaxes) {

		BigDecimal amount = new BigDecimal(0);
		String id = String.valueOf(getC_Invoice_ID());
		StringBuffer sql = new StringBuffer(
				"SELECT taxamt FROM C_InvoiceTax it ");
		if (onlymanualTaxes) {
			sql.append(" inner join c_tax t on t.c_tax_id = it.c_tax_id inner join c_taxcategory tc on tc.c_taxcategory_id = t.c_taxcategory_id ");
		}
		sql.append(" WHERE it.isActive = 'Y' AND it.C_Invoice_ID = " + id);
		if (onlymanualTaxes) {
			sql.append(" and tc.ismanual = 'Y' ");
		}

		try {
			PreparedStatement pstmt = DB.prepareStatement(sql.toString(),
					get_TrxName());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				BigDecimal value = rs.getBigDecimal(1);
				amount = amount.add(value);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "totalTax", e);
		}
		return amount;
	} // totalTax

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	private boolean createPaySchedule() {
		if (getC_PaymentTerm_ID() == 0) {
			return false;
		}

		MPaymentTerm pt = new MPaymentTerm(getCtx(), getC_PaymentTerm_ID(),
				null);

		log.fine(pt.toString());

		return pt.apply(this); // calls validate pay schedule
	} // createPaySchedule

	/**
	 * Operaciones luego de procesar el documento
	 */
	public boolean afterProcessDocument(String processAction, boolean status) {

		// Setear el crédito

		if ((MInvoice.DOCACTION_Complete.equals(processAction)
				|| MInvoice.DOCACTION_Reverse_Correct.equals(processAction) || MInvoice.DOCACTION_Void
					.equals(processAction)) && status) {

			// Guardar la factura con el nuevo estado a fin de recalcular
			// correctamente el credito disponible
			if(!save()){
				log.severe(CLogger.retrieveErrorAsString());
			}

			// Si es pedido de ventas y se paga a crédito, setear el crédito
			// recalculado
			if (isUpdateBPBalance() && isConfirmAditionalWorks()) {
				MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(),
						get_TrxName());
				// Obtengo el manager actual
				CurrentAccountManager manager = CurrentAccountManagerFactory
						.getManager(this);
				// Actualizo el balance
				CallResult result = new CallResult();
				try {
					result = manager.afterProcessDocument(getCtx(), new MOrg(
							getCtx(), getAD_Org_ID(), get_TrxName()), bp,
							getAditionalWorkResult(), get_TrxName());
				} catch (Exception e) {
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
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean approveIt() {
		log.info(toString());
		setIsApproved(true);

		return true;
	} // approveIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean rejectIt() {
		log.info(toString());
		setIsApproved(false);

		return true;
	} // rejectIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public String completeIt() {

		if (!Util.isEmpty(this.getM_AuthorizationChain_ID(), true) && !isSkipAuthorizationChain()) {
			AuthorizationChainManager authorizationChainManager = new AuthorizationChainManager(
					this, getCtx(), get_TrxName());

			try {
				String notAuthorizeDocStatus = authorizationChainManager.loadAuthorizationChain(reactiveInvoice());
				if (notAuthorizeDocStatus != null && !DOCSTATUS_Completed.equals(notAuthorizeDocStatus)) {
					m_processMsg = Msg.getMsg(getCtx(), "AlreadyExistsAuthorizationChainLink");
					setProcessed(true);
					return notAuthorizeDocStatus;
				}
			} catch (Exception e) {
				m_processMsg = e.getMessage();
				return DocAction.STATUS_Invalid;
			}
		}

		setAditionalWorkResult(new HashMap<PO, Object>());
		boolean localeARActive = CalloutInvoiceExt
				.ComprobantesFiscalesActivos();
		// Re-Check

		if (!m_justPrepared && !existsJustPreparedDoc()) {
			String status = prepareIt();

			if (!DocAction.STATUS_InProgress.equals(status)) {
				return status;
			}
		}

		// Valida si el documento ya fue impreso mediante un controlador
		// fiscal, solamente si no se debe ingresar manualmente el nro de
		// documento
		if (isFiscalAlreadyPrinted()) {
			if (!isManualDocumentNo()) {
				m_processMsg = "@FiscalAlreadyPrintedError@";
				return DocAction.STATUS_Invalid;
			}
			setIgnoreFiscalPrint(true);
		}

		// Implicit Approval

		if (!isApproved()) {
			approveIt();
		}

		log.info(toString());

		StringBuffer info = new StringBuffer();

		MDocType docType = new MDocType(getCtx(), getC_DocTypeTarget_ID(),
				get_TrxName());
		boolean isDebit = !docType.getDocBaseType().equals(
				MDocType.DOCBASETYPE_ARCreditMemo)
				&& !docType.getDocBaseType().equals(
						MDocType.DOCBASETYPE_APCreditMemo);
		if (isDebit && !isTPVInstance()) {
			setInitialCurrentAccountAmt(getGrandTotal());
		}

		// Create Cash

		if (PAYMENTRULE_Cash.equals(getPaymentRule()) && isCreateCashLine()) {
			boolean posJournalActivated = MPOSJournal.isActivated();
			MCash cash = null;
			if (posJournalActivated) {
				Integer cashID = MPOSJournal.getCurrentCashID();
				cash = cashID != null ? new MCash(getCtx(), cashID,
						get_TrxName()) : null;
			} else {
				cash = MCash.get(getCtx(), getAD_Org_ID(), getDateInvoiced(),
						getC_Currency_ID(), get_TrxName());
			}

			if ((cash == null) || (cash.getID() == 0)) {
				m_processMsg = posJournalActivated ? "@NoJournalCashForCurrentUserDate@"
						: "@NoCashForCurrentDate@";

				return DocAction.STATUS_Invalid;
			}

			MCashLine cl = new MCashLine(cash);

			cl.setInvoice(this);
			// 1. Crea la línea en la BD
			if (!cl.save()) {
				m_processMsg = "@CashLineCreateError@: "
						+ CLogger.retrieveErrorAsString();
				// 2. Completa la línea
			} else if (!cl.processIt(MCashLine.ACTION_Complete)) {
				m_processMsg = "@CashLineCreateError@: " + cl.getProcessMsg();
				// 3. Guarda los cambios
			} else if (!cl.save(get_TrxName())) {
				m_processMsg = "@CashLineCreateError@: "
						+ CLogger.retrieveErrorAsString();
			}
			if (m_processMsg != null) {
				return STATUS_Invalid;
			}

			info.append("@C_Cash_ID@: " + cash.getName() + " #" + cl.getLine());
			setC_CashLine_ID(cl.getC_CashLine_ID());

			setInitialCurrentAccountAmt(BigDecimal.ZERO);
		} // CashBook

		// Update Order & Match

		int matchInv = 0;
		int matchPO = 0;
		MInvoiceLine[] lines = getLines(false);
		MOrderLine orderLine;
		// MOrder order = null;

		// Si el flag de actualizar cantidades del pedido está marcado y no es
		// un débito, entonces se debe reabrir el pedido, modificar la línea y
		// completarlo
		Map<Integer, BigDecimal> orderLinesToUpdate = new HashMap<Integer, BigDecimal>();
		BigDecimal orderLineToUpdateQty = null;
		// Ader: mejoras de logica de documentos; por ahora solo se trata
		// el caso de facturas de clientes normales; el codigo siguiente al else
		// trata los siguiente casos como antes. Esta optimización reemplaza,
		// para
		// facturas creadas a partir de peidods N*6 accesos por solo 1 (N siendo
		// la cantidad de lineas).
		if (isSOTrx() && isDebit) {
			boolean ok = updateOrderIsSOTrxDebit(lines, orderLinesToUpdate);
			if (!ok) {
				m_processMsg = "Could not update Order Line";
				return DocAction.STATUS_Invalid;
			}
		} else { // se deja el siguiente codigo tal como estaba, para tratar los
					// demas casos
			for (int i = 0; i < lines.length; i++) {
				MInvoiceLine line = lines[i];

				// Chequeo del producto para determinar si está habilitado para comercializar
		        if(line.getM_Product_ID() != 0) {
		        	MProduct product = new MProduct(getCtx(), line.getM_Product_ID(),get_TrxName());
		        	if(product.ismarketingblocked()) {
		        		m_processMsg = product.getmarketingblockeddescr() + " Product: "+product.getName();
						return DocAction.STATUS_Invalid;		        		
		        	}
		        }
		        
				// Update Order Line

				// performance: no instanciar los M, ejecutar UPDATE directo
				// MOrderLine ol = null;

				if (line.getC_OrderLine_ID() != 0) {
					// Si es débito verificar issotrx y realizar las operaciones
					// necesarias para incrementar la cantidad facturada
					orderLine = new MOrderLine(getCtx(),
							line.getC_OrderLine_ID(), get_TrxName());
					// if (order == null
					// || orderLine.getC_Order_ID() != order
					// .getC_Order_ID()) {
					// order = new MOrder(getCtx(), orderLine.getC_Order_ID(),
					// get_TrxName());
					// }
					if (isSOTrx() || (line.getM_Product_ID() == 0)) {
						BigDecimal qtyInvoiced = line.getQtyInvoiced();
						if (!isDebit) {
							// Si hay que actualizar todo el pedido entonces
							// guardarlo en la hash que luego servirá para
							// realizar esta operación
							if (isUpdateOrderQty()) {
								addToMap(qtyInvoiced, orderLine.getID(), orderLinesToUpdate);
							}
							qtyInvoiced = qtyInvoiced.negate();
						}
						orderLine.setQtyInvoiced(orderLine.getQtyInvoiced()
								.add(qtyInvoiced));
						orderLine.setControlStock(false);
						orderLine.setUpdatePriceInSave(false);
						if(!isAllowSetOrderPriceList()){
							orderLine.m_M_PriceList_ID = getM_PriceList_ID();
						}
						if (!orderLine.save()) {
							m_processMsg = CLogger.retrieveErrorAsString();
							return DocAction.STATUS_Invalid;
						}
					}

					if (!isSOTrx() && (line.getM_Product_ID() != 0)) {
						// MatchPO is created also from MInOut when Invoice
						// exists
						// before Shipment
						BigDecimal matchQty = line.getQtyInvoiced();
						MMatchPO po = new MMatchPO(line, getDateInvoiced(),
								matchQty);
						if (!po.save(get_TrxName())) {
							m_processMsg = CLogger.retrieveErrorAsString();
							return DocAction.STATUS_Invalid;
						} else {
							matchPO++;
						}
					}
				}

				// Matching - Inv-Shipment

				if (!isSOTrx() && (line.getM_InOutLine_ID() != 0)
						&& (line.getM_Product_ID() != 0)) {
					BigDecimal matchQty = line.getQtyInvoiced();
					MMatchInv inv = new MMatchInv(line, getDateInvoiced(),
							matchQty);

					if (!inv.save(get_TrxName())) {
						m_processMsg = CLogger.retrieveErrorAsString();

						return DocAction.STATUS_Invalid;
					} else {
						matchInv++;
					}
				}
			} // for all lines
		}// fin else

		// Reabrir el pedido en el caso que esté marcada para actualizar
		// cantidades
		if (isUpdateOrderQty() && orderLinesToUpdate.size() > 0) {
			// Reactivar el pedido
			MOrder order = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
			if (!order.processIt(MOrder.DOCACTION_Re_Activate)) {
				setProcessMsg("Error reactivating order to update qty: "
						+ order.getProcessMsg());
				return DOCSTATUS_Invalid;
			}
			if(!order.save()){
				setProcessMsg(CLogger.retrieveErrorAsString());
				return DOCSTATUS_Invalid;
			}
			// Actualizar las cantidades
			BigDecimal sign = new BigDecimal(docType.getsigno_issotrx());
			BigDecimal qtySign;
			MOrderLine orderLineToUpdate;
			for (Integer orderLineID : orderLinesToUpdate.keySet()) {
				orderLineToUpdate = new MOrderLine(getCtx(), orderLineID, get_TrxName());
				qtySign = orderLinesToUpdate.get(orderLineID).multiply(sign);
				orderLineToUpdate.setQtyEntered(orderLineToUpdate
						.getQtyEntered().add(qtySign));
				orderLineToUpdate.setQtyOrdered(orderLineToUpdate
						.getQtyOrdered().add(qtySign));
				if (!orderLineToUpdate.save()) {
					setProcessMsg(CLogger.retrieveErrorAsString());
					return DOCSTATUS_Invalid;
				}
			}
			// Recargar el pedido
			order = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
			// Completarlo
			if (!order.processIt(MOrder.DOCACTION_Complete)) {
				setProcessMsg("Error reactivating order to update qty: "
						+ order.getProcessMsg());
				return DOCSTATUS_Invalid;
			}
			if(!order.save()){
				setProcessMsg(CLogger.retrieveErrorAsString());
				return DOCSTATUS_Invalid;
			}
		}

		// Update BP Statistics

		MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(),
				get_TrxName());

		// Update total revenue and balance / credit limit (reversed on
		// AllocationLine.processIt)

		BigDecimal invAmt = MConversionRate.convertBase(
				getCtx(),
				getGrandTotal(true), // CM adjusted
				getC_Currency_ID(), getDateAcct(), 0, getAD_Client_ID(),
				getAD_Org_ID());

		// Modified by Matías Cap
		// Las consultas y validaciones de cuenta corriente se deben manejar por
		// las nuevas clases que tienen esta implementación centralizada.
		// Verifico estado de crédito con la información de la factura
		if (!isCurrentAccountVerified && isSOTrx() && isDebit
				&& getPaymentRule().equals(PAYMENTRULE_OnCredit)) {
			// Obtengo el manager actual
			CurrentAccountManager manager = CurrentAccountManagerFactory
					.getManager(this);
			// Verificar el crédito con la factura y pedido asociado
			CallResult result = new CallResult();
			try {
				result = manager.invoiceWithinCreditLimit(getCtx(), new MOrg(
						getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()),
						bp, invAmt, get_TrxName());
			} catch (Exception e) {
				result.setMsg(e.getMessage(), true);
			}
			// Si hubo error, obtengo el mensaje y retorno inválido
			if (result.isError()) {
				m_processMsg = result.getMsg();
				return DocAction.STATUS_Invalid;
			}
		}

		// User - Last Result/Contact

		if (getAD_User_ID() != 0) {
			MUser user = new MUser(getCtx(), getAD_User_ID(), get_TrxName());

			user.setLastContact(new Timestamp(System.currentTimeMillis()));
			user.setLastResult(Msg.translate(getCtx(), "C_Invoice_ID") + ": "
					+ getDocumentNo());

			if (!user.save(get_TrxName())) {
				m_processMsg = CLogger.retrieveErrorAsString();

				return DocAction.STATUS_Invalid;
			}
		} // user

		// Update Project

		if (isSOTrx() && (getC_Project_ID() != 0)) {
			MProject project = new MProject(getCtx(), getC_Project_ID(),
					get_TrxName());
			BigDecimal amt = getGrandTotal(true);
			int C_CurrencyTo_ID = project.getC_Currency_ID();

			if (C_CurrencyTo_ID != getC_Currency_ID()) {
				amt = MConversionRate.convert(getCtx(), amt,
						getC_Currency_ID(), C_CurrencyTo_ID, getDateAcct(), 0,
						getAD_Client_ID(), getAD_Org_ID());
				// NO existe conversión entre la moneda original y la del proyecto
				if(amt == null){
					m_processMsg = Msg.getMsg(getCtx(), "NoCurrenciesConversion",
							new Object[] { MCurrency.getISO_Code(getCtx(), getC_Currency_ID()),
									MCurrency.getISO_Code(getCtx(), C_CurrencyTo_ID),
									Env.getDateFormatted(getDateAcct()) });
					return DocAction.STATUS_Invalid;
				}
			}

			BigDecimal newAmt = project.getInvoicedAmt();

			if (newAmt == null) {
				newAmt = amt;
			} else {
				newAmt = newAmt.add(amt);
			}

			log.fine("GrandTotal=" + getGrandTotal(true) + "(" + amt
					+ ") Project " + project.getName() + " - Invoiced="
					+ project.getInvoicedAmt() + "->" + newAmt);
			project.setInvoicedAmt(newAmt);

			if (!project.save(get_TrxName())) {
				m_processMsg = CLogger.retrieveErrorAsString();

				return DocAction.STATUS_Invalid;
			}
		} // project

		// User Validation

		String valid = ModelValidationEngine.get().fireDocValidate(this,
				ModelValidator.TIMING_AFTER_COMPLETE);

		if (valid != null) {
			m_processMsg = valid;

			return DocAction.STATUS_Invalid;
		}

		// Crear el document discount a partir del descuento manual general de
		// la cabecera
		if (isSOTrx()
				&& getManualGeneralDiscount().compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal totalPriceListLines = getSumColumnLines("PriceList * QtyInvoiced");
			BigDecimal manualGeneralDiscountAmt = getSumColumnLines("LineDiscountAmt");
			MDocumentDiscount documentDiscount = createDocumentDiscount(
					totalPriceListLines, manualGeneralDiscountAmt, null,
					MDocumentDiscount.CUMULATIVELEVEL_Document,
					MDocumentDiscount.DISCOUNTAPPLICATION_DiscountToPrice,
					MDocumentDiscount.DISCOUNTKIND_ManualGeneralDiscount, null, null);
			// Si no se puede guardar aborta la operación
			if (!documentDiscount.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				return DocAction.STATUS_Invalid;
			}
		}

		// Arrastre de descuentos del pedido
		try{ 
			saveDraggedDiscounts();
		} catch(Exception e){
			setProcessMsg(e.getMessage());
			return DocAction.STATUS_Invalid; 
		}

		// Counter Documents

		MInvoice counter = createCounterDoc();

		if (counter != null) {
			info.append(" - @CounterDoc@: @C_Invoice_ID@=").append(
					counter.getDocumentNo());
		}

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
			if (getVoidPOSJournalID() != 0 && isVoidPOSJournalMustBeOpen()) {
				m_processMsg = MPOSJournal.POS_JOURNAL_VOID_CLOSED_ERROR_MSG;
				return STATUS_Invalid;
			}
			log.severe("POS Journal assigned with ID " + getC_POSJournal_ID()
					+ " is closed");
			setC_POSJournal_ID(0);
		}

		// Caja Diaria. Intenta registrar la factura
		if (getC_POSJournal_ID() == 0 && !MPOSJournal.registerDocument(this, true, isSOTrx())) {
			m_processMsg = MPOSJournal.DOCUMENT_COMPLETE_ERROR_MSG;
			return STATUS_Invalid;
		}

		// Si es nota de crédito automática se asigna a la factura más vieja y
		// si es por devolución a la relacionada con el pedido
		if (!isSkipAutomaticCreditAllocCreation()
				&& bp.isAutomaticCreditNotes()
				&& !isDebit
				&& PAYMENTRULE_OnCredit.equals(getPaymentRule())
				&& !automaticCreditDocTypesExcluded.contains(docType
						.getDocTypeKey())) {
			try {
				createAutomaticCreditAllocations(this, getC_Order_ID());
			} catch (Exception e) {
				m_processMsg = e.getMessage();
				return DocAction.STATUS_Invalid;
			}
		}

		// Verifico si el gestor de cuentas corrientes debe realizar operaciones
		// antes de completar y eventualmente disparar la impresión fiscal
		// Obtengo el manager actual
		if (isUpdateBPBalance()) {
			CurrentAccountManager manager = CurrentAccountManagerFactory
					.getManager(this);
			// Actualizo el balance
			CallResult result = new CallResult();
			try {
				result = manager.performAditionalWork(getCtx(), new MOrg(
						getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()),
						bp, this, false, get_TrxName());
			} catch (Exception e) {
				result.setMsg(e.getMessage(), true);
			}
			// Si hubo error, obtengo el mensaje y retorno inválido
			if (result.isError()) {
				m_processMsg = result.getMsg();
				return DocAction.STATUS_Invalid;
			}
			// Me guardo el resultado en la variable de instancia para luego
			// utilizarla en afterProcessDocument
			getAditionalWorkResult().put(this, result.getResult());
		}

		// LOCALIZACION ARGENTINA
		// Emisión de la factura por controlador fiscal
		if (requireFiscalPrint() && !isIgnoreFiscalPrint()) {
			CallResult callResult = doFiscalPrint();
			if (callResult.isError()) {
				m_processMsg = callResult.getMsg();
				return STATUS_Invalid;
			}
		}

		/**
		 * @agregado: Horacio Alvarez - Servicios Digitales S.A.
		 * @fecha: 2009-06-16
		 * @fecha: 2011-06-25 modificado para soportar WSFEv1.0
		 * 
		 */
		if (localeARActive
				& MDocType.isElectronicDocType(getC_DocTypeTarget_ID())) {
			
			// === Lógica adicional para evitar doble notificación a AFIP. ===
			// Si tiene CAE asignado, no debe generarlo nuevamente
			if ((getcae() == null || getcae().length() == 0) && getcaecbte() != getNumeroComprobante()) {
				// Se intenta obtener un proveedor de WSFE, en caso de no encontrarlo se utiliza la vieja version (via pyafipws) 
				ElectronicInvoiceInterface processor = ElectronicInvoiceProvider.getImplementation(this);
				if (processor==null) {
					processor = new ProcessorWSFE(this);
				} 
				String errorMsg = processor.generateCAE();
				if (Util.isEmpty(processor.getCAE())) {
					setcaeerror(errorMsg);
					m_processMsg = errorMsg;
					log.log(Level.SEVERE, "CAE Error: " + errorMsg);
					return DocAction.STATUS_Invalid;
				} else {
					setcae(processor.getCAE());
					setvtocae(processor.getDateCae());
					setcaeerror(errorMsg);
					int nroCbte = Integer.parseInt(processor.getNroCbte());
					boolean updateDocumentNo = getNumeroComprobante()!= nroCbte && this.skipAfterAndBeforeSave;
					this.setNumeroComprobante(nroCbte);
					
					if(updateDocumentNo)
						setDocumentNo(CalloutInvoiceExt
								.GenerarNumeroDeDocumento(getPuntoDeVenta(),
										getNumeroComprobante(), getLetra(),
										isSOTrx(), false));

					// Actualizar la secuencia del tipo de documento de la
					// factura en función del valor recibido en el WS de AFIP
					MDocType dt = MDocType.get(getCtx(), getC_DocType_ID(),
							get_TrxName());
					MSequence.setFiscalDocTypeNextNroComprobante(
							dt.getDocNoSequence_ID(), nroCbte + 1,
							get_TrxName());

					log.log(Level.SEVERE, "CAE: " + processor.getCAE());
					log.log(Level.SEVERE, "DATE CAE: " + processor.getDateCae());
				}
			}
		}
		MPriceList pl = new MPriceList(getCtx(), getM_PriceList_ID(),
				get_TrxName());
		if (pl.isActualizarPreciosConFacturaDeCompra()) {
			//Actualizo los precios de productos a partir del precio de la factura
			MInvoiceLine[] l = getLines();
			for (int i = 0; i < l.length; i++) {
				int plv_id = DB
						.getSQLValue(
								get_TrxName(),
								"SELECT M_PriceList_Version_ID FROM M_PriceList_Version WHERE M_PriceList_ID = "
										+ pl.getM_PriceList_ID());
				MProductPrice pp;
				if (plv_id != 0) {
					//Si existe MProductoPrice la instancio, sino creo una nueva
					pp = MProductPrice.get(getCtx(), plv_id,
							l[i].getM_Product_ID(), get_TrxName());
					if (pp == null) {
						pp = new MProductPrice(getCtx(), 0, get_TrxName());
						pp.setM_PriceList_Version_ID(plv_id);
						pp.setM_Product_ID(l[i].getM_Product_ID());
					}
					//Hago el currencyConvert y actualizo nuevos precios
					BigDecimal priceAct = MCurrency.currencyConvert(
							l[i].getPriceActual(), getC_Currency_ID(),
							pl.getC_Currency_ID(),
							getFechadeTCparaActualizarPrecios(),
							getAD_Org_ID(), getCtx());
					pp.setPriceList(priceAct);
					pp.setPriceStd(priceAct);
					pp.save();
				}
			}
		}

		m_processMsg = info.toString().trim();
		setProcessed(true);
		setDocAction(DOCACTION_Close);

		return DocAction.STATUS_Completed;
	} // completeIt

	private Boolean reactiveInvoice() {
		return !Util.isEmpty(getOldGrandTotal(), true) && (!this.getGrandTotal().equals(this.getOldGrandTotal()));
	}

	/**
	 * Crea un document discount con los datos parámetro. No guarda el PO.
	 * @param baseAmt
	 * @param discountAmt
	 * @param taxRate
	 * @param cumulativeLevel
	 * @param discountApplication
	 * @param discountKind
	 * @param description
	 * @return 
	 */
	private MDocumentDiscount createDocumentDiscount(BigDecimal baseAmt,
			BigDecimal discountAmt, BigDecimal taxRate, String cumulativeLevel,
			String discountApplication, String discountKind, String description, 
			Integer discountSchemaID) {
		MDocumentDiscount documentDiscount = new MDocumentDiscount(getCtx(), 0,
				get_TrxName());
		// Asigna las referencias al documento
		documentDiscount.setC_Invoice_ID(getID());
		// Asigna los importes y demás datos del descuento
		documentDiscount.setDiscountBaseAmt(baseAmt);
		documentDiscount.setDiscountAmt(discountAmt);
		documentDiscount.setCumulativeLevel(cumulativeLevel);
		documentDiscount.setDiscountApplication(discountApplication);
		documentDiscount.setTaxRate(taxRate);
		documentDiscount.setDiscountKind(discountKind);
		documentDiscount.setDescription(description);
		documentDiscount.setM_DiscountSchema_ID(discountSchemaID);
		return documentDiscount;
	}
	
	/**
	 * Este método guarda los descuentos a nivel de cabecera, por impuesto y los
	 * asocia como padre a todos ellos. Posee soporte para versiones anteriores
	 * de arrastre de descuentos (sin línea)
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	private void saveDraggedDiscounts() throws Exception{
		if(!isManageDragOrderDiscounts()){
			return;
		}
		// Obtener los descuentos aplicados a cada línea de la factura 
		String sql = "select dd.m_discountschema_id, dd.cumulativelevel, dd.discountapplication, dd.discountkind, t.rate, dd.discountbaseamt, dd.discountamt, dd.c_invoiceline_id, dd.c_documentdiscount_id, dd.description "
					+ "from c_documentdiscount dd "
					+ "join c_invoiceline il on il.c_invoiceline_id = dd.c_invoiceline_id "
					+ "join c_tax t on t.c_tax_id = il.c_tax_id "
					+ "where dd.c_invoice_id = ? "
					+ "order by dd.m_discountschema_id, dd.cumulativelevel, dd.discountapplication, dd.discountkind, t.rate";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getID());
			rs = ps.executeQuery();
			if(rs.next()){
				String controlKey = null;
				Map<String, MDocumentDiscount> documentDiscountParents = new HashMap<String, MDocumentDiscount>();
				Map<String, List<Integer>> documentDiscountChildsIDs = new HashMap<String, List<Integer>>();
				Map<String, Map<BigDecimal, MDocumentDiscount>> documentDiscountByTaxRate = new HashMap<String, Map<BigDecimal,MDocumentDiscount>>();
				MDocumentDiscount dd;
				String description, cumulativeLevel, discountApplication, discountKind;
				BigDecimal discountBaseAmt, discountAmt, rate;
				Integer documentDiscountID, discountSchemaID;
				String keySeparator = "_";
				do {
					documentDiscountID = rs.getInt("c_documentdiscount_id");
					description = rs.getString("description");
					discountBaseAmt = rs.getBigDecimal("discountbaseamt");
					discountAmt = rs.getBigDecimal("discountamt");
					discountSchemaID = rs.getInt("m_discountschema_id");
					cumulativeLevel = rs.getString("cumulativelevel");
					discountApplication = rs.getString("discountapplication");
					discountKind = rs.getString("discountkind");
					rate = rs.getBigDecimal("rate");
					controlKey = (StringUtil.valueOrDefault(discountSchemaID, "0") + keySeparator
							+ StringUtil.valueOrDefault(cumulativeLevel, "") + keySeparator
							+ StringUtil.valueOrDefault(discountApplication, "") + keySeparator
							+ StringUtil.valueOrDefault(discountKind, ""));
					
					// Sumar descuentos al parent
					dd = documentDiscountParents.get(controlKey);
					if(dd == null){
						dd = createDocumentDiscount(BigDecimal.ZERO, BigDecimal.ZERO, null, cumulativeLevel,
								discountApplication, discountKind, description, discountSchemaID);
					}
					dd.setDiscountBaseAmt(dd.getDiscountBaseAmt().add(discountBaseAmt));
					dd.setDiscountAmt(dd.getDiscountAmt().add(discountAmt));
					documentDiscountParents.put(controlKey, dd);
					
					// Busco el documentdiscount de la tasa de impuesto actual
					if(!documentDiscountByTaxRate.containsKey(controlKey)){
						documentDiscountByTaxRate.put(controlKey, new HashMap<BigDecimal, MDocumentDiscount>());
					}
					dd = documentDiscountByTaxRate.get(controlKey).get(rate);
					if(dd == null){
						dd = createDocumentDiscount(BigDecimal.ZERO, BigDecimal.ZERO, rate, cumulativeLevel,
								discountApplication, discountKind, description, discountSchemaID);
					}
					dd.setDiscountBaseAmt(dd.getDiscountBaseAmt().add(discountBaseAmt));
					dd.setDiscountAmt(dd.getDiscountAmt().add(discountAmt));
					documentDiscountByTaxRate.get(controlKey).put(rate, dd);
					
					// Agrego el id del document discount a actualizar luego con el padre
					if(!documentDiscountChildsIDs.containsKey(controlKey)){
						documentDiscountChildsIDs.put(controlKey, new ArrayList<Integer>());
					}
					documentDiscountChildsIDs.get(controlKey).add(documentDiscountID);
				} while (rs.next());
				
				/*	// Sumar descuentos al parent
					dd = documentDiscountParents.get(controlKey);
					if(dd == null){
						dd = createDocumentDiscount(BigDecimal.ZERO, BigDecimal.ZERO, null, cumulativeLevel,
								discountApplication, discountKind, description, discountSchemaID);
					}
					dd.setDiscountBaseAmt(dd.getDiscountBaseAmt().add(discountBaseAmt));
					dd.setDiscountAmt(dd.getDiscountAmt().add(discountAmt));
					documentDiscountParents.put(controlKey, dd);
					
					// Busco el documentdiscount de la tasa de impuesto actual
					if(!documentDiscountByTaxRate.containsKey(controlKey)){
						documentDiscountByTaxRate.put(controlKey, new HashMap<BigDecimal, MDocumentDiscount>());
					}
					dd = documentDiscountByTaxRate.get(controlKey).get(rate);
					if(dd == null){
						dd = createDocumentDiscount(BigDecimal.ZERO, BigDecimal.ZERO, rate, cumulativeLevel,
								discountApplication, discountKind, description, discountSchemaID);
					}
					dd.setDiscountBaseAmt(dd.getDiscountBaseAmt().add(discountBaseAmt));
					dd.setDiscountAmt(dd.getDiscountAmt().add(discountAmt));
					documentDiscountByTaxRate.get(controlKey).put(rate, dd);
					
					// Agrego el id del document discount a actualizar luego con el padre
					if(!documentDiscountChildsIDs.containsKey(auxControlKey)){
						documentDiscountChildsIDs.put(auxControlKey, new ArrayList<Integer>());
					}
					documentDiscountChildsIDs.get(auxControlKey).add(documentDiscountID);
				}*/
				
				// Guardar todos los document discounts
				MDocumentDiscount parent;
				// 1) Descuentos de documento (padres)
				for (String key : documentDiscountParents.keySet()) {
					parent = documentDiscountParents.get(key);
					if(!parent.save()){
						throw new Exception(CLogger.retrieveErrorAsString());
					}
					// 2) Descuento por iva
					for (BigDecimal taxRate : documentDiscountByTaxRate.get(key).keySet()) {
						dd = documentDiscountByTaxRate.get(key).get(taxRate);
						dd.setC_DocumentDiscount_Parent_ID(parent.getID());
						if(!dd.save()){
							throw new Exception(CLogger.retrieveErrorAsString());
						}	
					}
					// 3) Asociar a las líneas de descuento els descuento padre
					DB.executeUpdate(
							"UPDATE " + MDocumentDiscount.Table_Name + " SET c_documentdiscount_parent_id = "
									+ parent.getID() + " WHERE c_documentdiscount_id IN "
									+ StringUtil.implodeForUnion(documentDiscountChildsIDs.get(key)),
							get_TrxName());
				}
			}
			// Si no se debe realizar con el nuevo método, debemos tener soporte
			// para la forma anterior
			else{
				saveDraggedDiscountsOld();
			}
		} finally {
			try {
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * Método anterior de arrastre de descuentos de pedido basado en los
	 * importes de descuentos de las líneas del comprobante
	 * 
	 * @deprecated
	 * @throws Exception
	 */
	private void saveDraggedDiscountsOld() throws Exception{
		if (!isManageDragOrderDiscounts()) {
			return;
		}
		// Crear los DocumentDiscount de los descuentos cuando se arrastran
		// desde el pedido
		MDocumentDiscount documentDiscount = null;
		MDocumentDiscount documentDiscountFather = null;
		
		// Crear los document discount en base a los descuentos de las líneas
		// Cabecera e impuestos de cada descuento. 
		
		// Suma de los descuentos a nivel de documento y de la base,
		// separado por impuesto
		Map<BigDecimal, BigDecimal> documentDiscountBaseAmtsByTaxRate = new HashMap<BigDecimal, BigDecimal>();
		Map<BigDecimal, BigDecimal> documentDiscountAmtsByTaxRate = new HashMap<BigDecimal, BigDecimal>();

		// Itero por todas las líneas del la factura y agrego un document
		// discount por cada descuento de línea y bonificación
		BigDecimal lineDiscountAmt, lineBonusAmt, baseAmt, documentDiscountBaseAmt, documentDiscountAmt, taxRate, manualGeneralAmt;
		String discountDescription;
		for (MInvoiceLine mInvoiceLine : getLines()) {
			// Se debe crear un document discount por descuento de línea y
			// bonificación
			baseAmt = mInvoiceLine.getTotalPriceListWithTax();
			discountDescription = mInvoiceLine.getProductName();
			taxRate = mInvoiceLine.getTaxRate();
			// Descuento por línea al precio
			lineDiscountAmt = mInvoiceLine.getLineDiscountAmt();
			if (lineDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
				if (lineDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
					// Descuento de línea padre
					documentDiscountFather = createDocumentDiscount(
							baseAmt,
							lineDiscountAmt,
							null,
							MDocumentDiscount.CUMULATIVELEVEL_Line,
							MDocumentDiscount.DISCOUNTAPPLICATION_DiscountToPrice,
							MDocumentDiscount.DISCOUNTKIND_DiscountLine,
							discountDescription, null);
					// Si no se puede guardar aborta la operación
					if (!documentDiscountFather.save()) {
						throw new Exception(CLogger.retrieveErrorAsString()); 
					}
					// Descuento de bonificación de iva
					documentDiscount = createDocumentDiscount(
							baseAmt,
							lineDiscountAmt,
							taxRate,
							MDocumentDiscount.CUMULATIVELEVEL_Line,
							MDocumentDiscount.DISCOUNTAPPLICATION_DiscountToPrice,
							MDocumentDiscount.DISCOUNTKIND_DiscountLine,
							discountDescription, null);
					documentDiscount
							.setC_DocumentDiscount_Parent_ID(documentDiscountFather
									.getID());
					// Si no se puede guardar aborta la operación
					if (!documentDiscount.save()) {
						throw new Exception(CLogger.retrieveErrorAsString());
					}
				}
			}
			// Descuento de bonificación
			lineBonusAmt = mInvoiceLine.getLineBonusAmt();
			if (lineBonusAmt.compareTo(BigDecimal.ZERO) != 0) {
				// Descuento de bonificación padre
				documentDiscountFather = createDocumentDiscount(
						baseAmt, 
						lineBonusAmt,
						null,
						MDocumentDiscount.CUMULATIVELEVEL_Line,
						MDocumentDiscount.DISCOUNTAPPLICATION_Bonus,
						MDocumentDiscount.DISCOUNTKIND_DiscountLine,
						discountDescription, null);
				// Si no se puede guardar aborta la operación
				if (!documentDiscountFather.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				// Descuento de bonificación de iva
				documentDiscount = createDocumentDiscount(baseAmt,
						lineBonusAmt,
						taxRate,
						MDocumentDiscount.CUMULATIVELEVEL_Line,
						MDocumentDiscount.DISCOUNTAPPLICATION_Bonus,
						MDocumentDiscount.DISCOUNTKIND_DiscountLine,
						discountDescription, null);
				documentDiscount
						.setC_DocumentDiscount_Parent_ID(documentDiscountFather
								.getID());
				// Si no se puede guardar aborta la operación
				if (!documentDiscount.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
			// Actualizar el monto base y de descuento de la map para los
			// descuentos de documento
			// Monto base
			documentDiscountBaseAmt = documentDiscountBaseAmtsByTaxRate.get(taxRate);
			if (documentDiscountBaseAmt == null) {
				documentDiscountBaseAmt = BigDecimal.ZERO;
			}
			documentDiscountBaseAmtsByTaxRate.put(taxRate, documentDiscountBaseAmt.add(baseAmt));
			// Monto de descuento
			documentDiscountAmt = documentDiscountAmtsByTaxRate.get(taxRate);
			if (documentDiscountAmt == null) {
				documentDiscountAmt = BigDecimal.ZERO;
			}
			documentDiscountAmtsByTaxRate.put(taxRate, documentDiscountAmt.add(mInvoiceLine.getDocumentDiscountAmt()));
		}
		// Itero por todas las tasas que posee esta factura y creo el
		// descuento a nivel de documento en base a la suma de ellos
		BigDecimal totalDocumentDiscountAmt = BigDecimal.ZERO;
		BigDecimal totalDocumentDiscountBaseAmt = BigDecimal.ZERO;
		BigDecimal discountAmt = BigDecimal.ZERO;
		BigDecimal discountBaseAmt = BigDecimal.ZERO;
		List<Integer> documentDiscounts = new ArrayList<Integer>();
		documentDiscount = null;
		MDocumentDiscount documentDiscountByTax = null;
		String documentDiscountDescription = Msg.getMsg(getCtx(),"DiscountChargeShort");
		for (BigDecimal baseTaxRate : documentDiscountBaseAmtsByTaxRate.keySet()) {
			discountBaseAmt = documentDiscountBaseAmtsByTaxRate.get(baseTaxRate);
			discountAmt = documentDiscountAmtsByTaxRate.get(baseTaxRate);
			if (discountAmt.compareTo(BigDecimal.ZERO) != 0) {
				// Crear el descuento por impuesto
				documentDiscountByTax = createDocumentDiscount(
						discountBaseAmt,
						discountAmt,
						baseTaxRate,
						MDocumentDiscount.CUMULATIVELEVEL_Document,
						null,
						MDocumentDiscount.DISCOUNTKIND_DocumentDiscount,
						documentDiscountDescription + " " + baseTaxRate, null);
				// Si no se puede guardar aborta la operación
				if (!documentDiscountByTax.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				documentDiscounts.add(documentDiscountByTax.getID());
				totalDocumentDiscountBaseAmt = totalDocumentDiscountBaseAmt.add(discountBaseAmt);
				totalDocumentDiscountAmt = totalDocumentDiscountAmt.add(discountAmt);
			}
		}
		
		// Creo el descuento de documento si hubo efectivamente uno
		if (totalDocumentDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
			documentDiscount = createDocumentDiscount(
					totalDocumentDiscountBaseAmt, totalDocumentDiscountAmt,
					null, MDocumentDiscount.CUMULATIVELEVEL_Document, null,
					MDocumentDiscount.DISCOUNTKIND_DocumentDiscount,
					"Total " + documentDiscountDescription, null);
			// Si no se puede guardar aborta la operación
			if (!documentDiscount.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}

		// Actualizar todos los descuentos de documento por tasa con el
		// descuento padre
		if (documentDiscounts.size() > 0 && documentDiscount != null
				&& documentDiscount.getID() > 0) {
			DB.executeUpdate("UPDATE "
					+ X_C_DocumentDiscount.Table_Name
					+ " SET c_documentdiscount_parent_id = "
					+ documentDiscount.getID()
					+ " WHERE c_documentdiscount_id IN "
					+ StringUtil.implodeForUnion(documentDiscounts), get_TrxName());
		}
	}
	
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	private MInvoice createCounterDoc() {

		// Is this a counter doc ?

		if (getRef_Invoice_ID() != 0) {
			return null;
		}

		// Org Must be linked to BPartner

		MOrg org = MOrg.get(getCtx(), getAD_Org_ID());
		int counterC_BPartner_ID = org.getLinkedC_BPartner_ID();

		if (counterC_BPartner_ID == 0) {
			return null;
		}

		// Business Partner needs to be linked to Org

		MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(), null);
		int counterAD_Org_ID = bp.getAD_OrgBP_ID_Int();

		if (counterAD_Org_ID == 0) {
			return null;
		}

		MBPartner counterBP = new MBPartner(getCtx(), counterC_BPartner_ID,
				null);
		MOrgInfo counterOrgInfo = MOrgInfo.get(getCtx(), counterAD_Org_ID);

		log.info("Counter BP=" + counterBP.getName());

		// Document Type

		int C_DocTypeTarget_ID = 0;
		MDocTypeCounter counterDT = MDocTypeCounter.getCounterDocType(getCtx(),
				getC_DocType_ID());

		if (counterDT != null) {
			log.fine(counterDT.toString());

			if (!counterDT.isCreateCounter() || !counterDT.isValid()) {
				return null;
			}

			C_DocTypeTarget_ID = counterDT.getCounter_C_DocType_ID();
		} else // indirect
		{
			C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocType_ID(getCtx(),
					getC_DocType_ID());
			log.fine("Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID);

			if (C_DocTypeTarget_ID <= 0) {
				return null;
			}
		}

		// Deep Copy

		MInvoice counter = copyFrom(this, getDateInvoiced(),
				C_DocTypeTarget_ID, !isSOTrx(), true, get_TrxName(), true, false);

		//

		counter.setAD_Org_ID(counterAD_Org_ID);

		// counter.setM_Warehouse_ID(counterOrgInfo.getM_Warehouse_ID());
		//

		counter.setBPartner(counterBP);

		// Refernces (Should not be required

		counter.setSalesRep_ID(getSalesRep_ID());
		counter.save(get_TrxName());

		// Update copied lines

		MInvoiceLine[] counterLines = counter.getLines(true);

		for (int i = 0; i < counterLines.length; i++) {
			MInvoiceLine counterLine = counterLines[i];

			counterLine.setInvoice(counter); // copies header values (BP,
			// etc.)
			counterLine.setPrice();
			counterLine.setTax();

			//

			counterLine.save(get_TrxName());
		}

		log.fine(counter.toString());

		// Document Action

		if (counterDT != null) {
			if (counterDT.getDocAction() != null) {
				// Bypass para validaciones de crédito de entidad comercial
				counter.setCurrentAccountVerified(true);
				// Bypass para actualización de crédito de entidad comercial
				counter.setUpdateBPBalance(false);
				counter.setDocAction(counterDT.getDocAction());
				counter.processIt(counterDT.getDocAction());
				counter.save(get_TrxName());
			}
		}

		return counter;
	} // createCounterDoc

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean postIt() {
		log.info(toString());

		return false;
	} // postIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean voidIt() {
		log.info(toString());
		boolean result = true;
		
		if (DOCSTATUS_Closed.equals(getDocStatus())
				|| DOCSTATUS_Reversed.equals(getDocStatus())
				|| DOCSTATUS_Voided.equals(getDocStatus())) {
			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);

			return false;
		}
		
		// Not Processed

		if (!isSOTrx() 
				|| DOCSTATUS_Drafted.equals(getDocStatus())
				|| DOCSTATUS_Invalid.equals(getDocStatus())
				|| DOCSTATUS_InProgress.equals(getDocStatus())
				|| DOCSTATUS_Approved.equals(getDocStatus())
				|| DOCSTATUS_NotApproved.equals(getDocStatus())) {

			// Set lines to 0

			MInvoiceLine[] lines = getLines(false);
			MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
			boolean isCredit = docType.getDocBaseType().equals(
					MDocType.DOCBASETYPE_ARCreditMemo);
			
			// Controlar si existe en algún allocation 
			if (isInAllocation(getVoiderAllocationID())) {
				m_processMsg = "@FreeInvoiceNeededError@";
				return false;
			}
			
			// Si el período está cerrado no se puede anular un comprobante de compras
			if (!isSOTrx() && !MPeriod.isOpen(getCtx(), getDateAcct(), docType.getDocBaseType(), docType)) {
				m_processMsg = "@PeriodClosed@";
				return false;
			}
			
			// Si está contabilizado elimino la contabilidad 
			if (isPosted()) {
				MFactAcct.delete(Table_ID, getID(), get_TrxName());
			}
			
			for (int i = 0; i < lines.length; i++) {
				MInvoiceLine line = lines[i];
				BigDecimal old = line.getQtyInvoiced();

				if (old.compareTo(Env.ZERO) != 0) {
					line.setQty(Env.ZERO);
					line.setPrice(Env.ZERO);
					line.setTaxAmt(Env.ZERO);
					line.setLineNetAmt(Env.ZERO);
					line.setLineNetAmount(Env.ZERO);
					line.setLineTotalAmt(Env.ZERO);
					line.addDescription(Msg.getMsg(getCtx(), "Voided") + " ("
							+ old + ")");

					// Unlink Shipment
					
					if (!isCredit && line.getM_InOutLine_ID() != 0) {
						MInOutLine ioLine = new MInOutLine(getCtx(),
								line.getM_InOutLine_ID(), get_TrxName());

						ioLine.setIsInvoiced(false);
						if(!ioLine.save(get_TrxName())){
							setProcessMsg(CLogger.retrieveErrorAsString());
							return false;
						}
						line.setM_InOutLine_ID(0);
					}

					if(!line.save(get_TrxName())){
						setProcessMsg(CLogger.retrieveErrorAsString());
						return false;
					}
				}
			}

			setTotalLines(BigDecimal.ZERO);
			setGrandTotal(BigDecimal.ZERO);
			addDescription(Msg.getMsg(getCtx(), "Voided"));
			setIsPaid(true);
			setC_Payment_ID(0);
			setM_AuthorizationChain_ID(0);
			setAuthorizationChainStatus(null);
			setSkipAuthorizationChain(true);
		} else {
			voidProcess = true;
			result = reverseCorrectIt();
		}

		return result;
	} // voidIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean closeIt() {
		log.info(toString());
		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	} // closeIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean reverseCorrectIt() {
		setAditionalWorkResult(new HashMap<PO, Object>());
		log.info(toString());
		
		// Disytel - Franco Bonafine
		// No es posible anular o revertir facturas que se encuentran en alguna
		// asignación.
		// Primero se deben revertir las asignaciones y luego anular la factura.
		// En caso de que exista, se ignora la asignación que causó la anulación
		// de este pago
		if (isInAllocation(getVoiderAllocationID())) {
			m_processMsg = "@FreeInvoiceNeededError@";
			return false;
		}

		boolean localeARActive = CalloutInvoiceExt
				.ComprobantesFiscalesActivos();
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		MDocType reversalDocType = docType;
		boolean isCredit = docType.getDocBaseType().equals(
				MDocType.DOCBASETYPE_ARCreditMemo);
		
		// ////////////////////////////////////////////////////////////////
		// LOCALIZACIÓN ARGENTINA
		// Para la localización argentina es necesario contemplar el tipo
		// de documento a anular a fin de determinar el tipo de documento
		// del documento generado (reversal).
		if (localeARActive & isSOTrx()) {
			// Se obtiene la clave base del tipo de documento.
			String reversalDocTypeBaseKey = reverseDocTypes.get(docType
					.getBaseKey());
			// Si el tipo de documento de esta MInvoice tiene inverso, se cambia
			// el tipo de documento del contramovimiento por el indicado.
			if (reversalDocTypeBaseKey != null) {
				// Se obtiene el tipo de documento del contramovimiento.
				// Obtener el punto de venta:
				// 1) Desde la caja diaria, priorizando la personalización de
				// punto de venta por letra de la config del tpv asociada a ella
				// 2) Desde la config de TPV, si es que posee una sola,
				// priorizando la personalización de punto de venta por letra
				// 3) Desde la factura a anular
				Integer ptoVenta = null;
				String letra = getLetra();
				// 1)
				if (MPOSJournal.isActivated()) {
					ptoVenta = MPOSJournal.getCurrentPOSNumber(letra);
				}
				// 2)
				if (Util.isEmpty(ptoVenta, true)) {
					List<MPOS> pos = MPOS.get(getCtx(),
							Env.getAD_Org_ID(getCtx()),
							Env.getAD_User_ID(getCtx()), get_TrxName());
					if (pos.size() == 1) {
						Map<String, Integer> letters = MPOSLetter
								.getPOSLetters(pos.get(0).getID(),
										get_TrxName());
						ptoVenta = letters.get(letra) != null ? letters
								.get(letra) : pos.get(0).getPOSNumber();
					}
				}
				// 3)
				if (Util.isEmpty(ptoVenta, true)) {
					ptoVenta = getPuntoDeVenta();
				}
				// Se obtiene el tipo de documento del contramovimiento.
				reversalDocType = MDocType.getDocType(getCtx(), getAD_Org_ID(),
						reversalDocTypeBaseKey, getLetra(), ptoVenta,
						get_TrxName());
			}
		}

		// Si se especificó el tipo de documento de anulacion a nivel C_DocType,
		// utilizar éste, redefiniendo cualquier logica anterior
		if (docType.getC_ReverseDocType_ID() > 0)
			reversalDocType = new MDocType(getCtx(),
					docType.getC_ReverseDocType_ID(), get_TrxName());

		setSkipExtraValidations(true);
		
		// Deep Copy
		Timestamp dateDoc = Env.getDate();
		
		reversalDocType = reversalDocType != null ? reversalDocType : docType;

		MInvoice reversal = copyFrom(this, dateDoc,
				reversalDocType.getC_DocType_ID(), isSOTrx(), false,
				get_TrxName(), true, true, true, !isSOTrx(), true);

		if (reversal == null) {
			m_processMsg = "Could not create Invoice Reversal";

			return false;
		}
		// Se agregan los listeners de DocAction que tiene esta Invoice
		// a la reversal.
		reversal.copyDocActionStatusListeners(this);

		reversal.setSkipAuthorizationChain(true);
		setSkipAuthorizationChain(true);		
		
		if (getAuthorizationChainStatus() != null
				&& !getAuthorizationChainStatus().equals(AUTHORIZATIONCHAINSTATUS_Authorized)) {
			setM_AuthorizationChain_ID(0);
			setAuthorizationChainStatus(null);
			reversal.setM_AuthorizationChain_ID(0);
			reversal.setAuthorizationChainStatus(null);
		}
		
		// Seteo la bandera que indica si se trata de una anulación.
		reversal.setVoidProcess(voidProcess);

		// ////////////////////////////////////////////////////////////////
		// LOCALIZACIÓN ARGENTINA
		// Para la localización argentina es necesario contemplar el tipo
		// de documento a anular a fin de determinar el tipo de documento
		// del documento generado (reversal).
		if (localeARActive & isSOTrx()) {
			// Se asigna el tipo de documento nuevo.
			reversal.setC_DocTypeTarget_ID(reversalDocType.getC_DocType_ID());
			reversal.setC_DocType_ID(reversalDocType.getC_DocType_ID());

			reversal.setC_Invoice_Orig_ID(getC_Invoice_ID());

			reversal.setFiscalAlreadyPrinted(false);
			// Se ignora la impresión fiscal en este punto ya que puede haber un
			// error en la imputación entre ellas lo que provoca es que se
			// imprima fiscalmente, pero no exista en la base de datos por
			// alg+un error eventual
			reversal.setIgnoreFiscalPrint(true);
		}

		// Si el docType = reversalDocType, entonces se utilizará el mismo
		// docType. Invertir montos
		// Ademas, para la localización argentina no hay que invertir las
		// cantidades ni los montos (usa distinto doctype)
		if (!localeARActive || (docType == reversalDocType)) {

			// Reverse Line Qty
			MInvoiceLine[] rLines = reversal.getLines(false);

			for (int i = 0; i < rLines.length; i++) {
				MInvoiceLine rLine = rLines[i];

				rLine.setQtyEntered(rLine.getQtyEntered().negate());
				rLine.setQtyInvoiced(rLine.getQtyInvoiced().negate());
				rLine.setLineNetAmt(rLine.getLineNetAmt().negate());
				rLine.setLineBonusAmt(rLine.getLineBonusAmt().negate());
				rLine.setLineDiscountAmt(rLine.getLineDiscountAmt().negate());
				rLine.setDocumentDiscountAmt(rLine.getDocumentDiscountAmt().negate());

				if ((rLine.getTaxAmt() != null)
						&& (rLine.getTaxAmt().compareTo(Env.ZERO) != 0)) {
					rLine.setTaxAmt(rLine.getTaxAmt().negate());
				}

				if ((rLine.getLineTotalAmt() != null)
						&& (rLine.getLineTotalAmt().compareTo(Env.ZERO) != 0)) {
					rLine.setLineTotalAmt(rLine.getLineTotalAmt().negate());
				}

				if (!rLine.save(get_TrxName())) {
					m_processMsg = CLogger.retrieveErrorAsString();
					return false;
				}
			}

			// Invertir los montos de los impuestos manuales
			if (!isSOTrx()) {
				try {
					List<MInvoiceTax> manualInvoiceTaxes = MInvoiceTax
							.getTaxesFromInvoice(reversal, true);
					for (MInvoiceTax mInvoiceTax : manualInvoiceTaxes) {
						mInvoiceTax.setTaxBaseAmt(mInvoiceTax.getTaxBaseAmt()
								.negate());
						mInvoiceTax.setTaxAmt(mInvoiceTax.getTaxAmt().negate());
						if (!mInvoiceTax.save(get_TrxName())) {
							throw new Exception(CLogger.retrieveErrorAsString());
						}
					}
				} catch (Exception e) {
					m_processMsg = "Could not create Reversal Manual Invoice Taxes";
					return false;
				}
			}
		} // !localeARActive

		reversal.setC_Order_ID(getC_Order_ID());
		reversal.addDescription("{->" + getDocumentNo() + ")");
		reversal.setFiscalAlreadyPrinted(false);
		// No confirmo el trabajo adicional de cuentas corrientes porque se debe
		// realizar luego de anular la factura
		reversal.setConfirmAditionalWorks(false);
		reversal.setCurrentAccountVerified(true);
		// Se asigna la misma caja diaria del documento a anular
		reversal.setVoidPOSJournalID(getVoidPOSJournalID());
		reversal.setVoidPOSJournalMustBeOpen(isVoidPOSJournalMustBeOpen());
		reversal.setC_POSJournal_ID(getC_POSJournal_ID());
		reversal.setSkipAutomaticCreditAllocCreation(true);
		// Descripción fiscal. Para los documentos generados por anulación se
		// setea el contenido de la preference asociada
		String voidDocumentFiscalDesc = MPreference
				.searchCustomPreferenceValue(
						VOID_FISCAL_DESCRIPTION_PREFERENCE_NAME,
						getAD_Client_ID(), getAD_Org_ID(),
						Env.getAD_User_ID(getCtx()), true);
		reversal.setFiscalDescription(Util
				.isEmpty(voidDocumentFiscalDesc, true) ? null
				: voidDocumentFiscalDesc);
		if (!reversal.processIt(DocAction.ACTION_Complete)) {
			m_processMsg = "@ReversalError@: " + reversal.getProcessMsg();

			return false;
		}
		// Me traigo el trabajo adicional de cuentas corrientes y lo confirmo
		// después
		getAditionalWorkResult().put(reversal,
				reversal.getAditionalWorkResult().get(reversal));

		reversal.setC_Payment_ID(0);
		reversal.setIsPaid(true);
		reversal.closeIt();
		// Disytel - FB
		// Dejamos como Revertido el documento inverso a fin de mantener la
		// consistencia
		// de visibilidad con el documento revertido, de modo que ambos
		// documentos aparezcan
		// en el mismo lugar
		// reversal.setDocStatus( DOCSTATUS_Closed );
		reversal.setProcessed(true);
		reversal.setDocStatus(isVoidProcess()?DOCSTATUS_Voided:DOCSTATUS_Reversed);

		reversal.setDocAction(DOCACTION_None);
		if(!reversal.save(get_TrxName())){
			setProcessMsg(CLogger.retrieveErrorAsString());
			return false;
		}

		addDescription("(" + reversal.getDocumentNo() + "<-)");

		// Clean up Reversed (this) [thanks Victor!]
		// Modificado por Matías Cap
		// Sólo para tipos de doc que no son créditos ya que serían devoluciones
		// y no se marcan como facturados. Tampoco se debería perder la
		// asociación con la devolución
		if(!isCredit){
			MInvoiceLine[] iLines = getLines(false);
	
			for (int i = 0; i < iLines.length; i++) {
				MInvoiceLine iLine = iLines[i];
	
				if (iLine.getM_InOutLine_ID() != 0) {
					MInOutLine ioLine = new MInOutLine(getCtx(),
							iLine.getM_InOutLine_ID(), get_TrxName());
	
					ioLine.setIsInvoiced(false);
					if(!ioLine.save(get_TrxName())){
						setProcessMsg(CLogger.retrieveErrorAsString());
						return false;
					}
	
					// Reconsiliation
	
					iLine.setM_InOutLine_ID(0);
					if(!iLine.save(get_TrxName())){
						setProcessMsg(CLogger.retrieveErrorAsString());
						return false;
					}
				}
			}
		}

		// Si se debe actualizar el saldo de la entidad comercial entonces
		// realizar el trabajo adicional para esta factura
		if (isUpdateBPBalance()) {
			MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(),
					get_TrxName());
			CurrentAccountManager manager = CurrentAccountManagerFactory
					.getManager(this);
			// Actualizo el balance
			CallResult result = new CallResult();
			try {
				result = manager.performAditionalWork(getCtx(), new MOrg(
						getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()),
						bp, this, false, get_TrxName());
			} catch (Exception e) {
				result.setMsg(e.getMessage(), true);
			}
			// Si hubo error, obtengo el mensaje y retorno inválido
			if (result.isError()) {
				m_processMsg = result.getMsg();
				return false;
			}
			// Me guardo el resultado en la variable de instancia para luego
			// utilizarla en afterProcessDocument
			getAditionalWorkResult().put(this, result.getResult());
		}

		setProcessed(true);
		setDocStatus(isVoidProcess()?DOCSTATUS_Voided:DOCSTATUS_Reversed);
		setDocAction(DOCACTION_None);

		StringBuffer info = new StringBuffer(reversal.getDocumentNo());

		// Reverse existing Allocations

		if(!save()){
			setProcessMsg(CLogger.retrieveErrorAsString());
			return false;
		}

		// Disytel FB - Ya no se desasignan automáticamente los pagos. Se debe
		// revertir la asignación manualmente.
		/*
		 * --------------> MAllocationHdr[] allocations =
		 * MAllocationHdr.getOfInvoice(getCtx(), getC_Invoice_ID(),
		 * get_TrxName());
		 * 
		 * for (int i = 0; i < allocations.length; i++) {
		 * allocations[i].setDocAction(DocAction.ACTION_Reverse_Correct);
		 * allocations[i].reverseCorrectIt();
		 * allocations[i].save(get_TrxName()); }
		 * 
		 * load(get_TrxName()); // reload allocation reversal info
		 * <-----------------
		 */
		//
		
		m_processMsg = info.toString();
		reversal.setC_Payment_ID(0);
		reversal.setIsPaid(true);

		// ////////////////////////////////////////////////////////////////
		// LOCALIZACIÓN ARGENTINA
		// Se crea una imputación entre el documento anulado y el generado.
		if (localeARActive && isSOTrx()) {
			// Se crea el la imputación con la fecha de facturación.
			MAllocationHdr allocHdr = new MAllocationHdr(getCtx(), false,
					Env.getDate(), getC_Currency_ID(), "Anulación de "
							+ docType.getPrintName() + " número "
							+ getDocumentNo(), get_TrxName());
			// Seteo la caja diaria del documento reverso
			allocHdr.setC_POSJournal_ID(reversal.getC_POSJournal_ID());

			if (!allocHdr.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				return false;
			}
			// Se crea la línea de imputación.
			MAllocationLine allocLine = new MAllocationLine(allocHdr);
			allocLine.setC_Invoice_ID(getC_Invoice_ID());
			allocLine.setC_Invoice_Credit_ID(reversal.getC_Invoice_ID());
			allocLine.setAmount(getGrandTotal());
			allocLine.setWriteOffAmt(BigDecimal.ZERO);
			allocLine.setDiscountAmt(BigDecimal.ZERO);
			allocLine.setOverUnderAmt(BigDecimal.ZERO);
			allocLine.setC_BPartner_ID(getC_BPartner_ID());

			if (!allocLine.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				return false;
			}
			allocHdr.setUpdateBPBalance(false);
			// Se completa la imputación.
			allocHdr.processIt(MAllocationHdr.ACTION_Complete);
			if(!allocHdr.save()){
				m_processMsg = CLogger.retrieveErrorAsString();
				return false;
			}
		}

		// Imprimir fiscalmente el documento reverso si es que así lo requiere y
		// si el documento a revertir también se imprimió fiscalmente
		if (localeARActive && isSOTrx() && isFiscalAlreadyPrinted()) {
			CallResult callResult = reversal.doFiscalPrint();
			if (callResult.isError()) {
				m_processMsg = callResult.getMsg();
				return false;
			}
		}
		
		return true;
	} // reverseCorrectIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean reverseAccrualIt() {
		log.info(toString());

		return false;
	} // reverseAccrualIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public boolean reActivateIt() {
		log.info(toString());
		calculateTaxTotal();
		return false;
	} // reActivateIt

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public String getSummary() {
		StringBuffer sb = new StringBuffer();

		sb.append(getDocumentNo());

		// : Grand Total = 123.00 (#1)

		sb.append(": ").append(Msg.translate(getCtx(), "GrandTotal"))
				.append("=").append(getGrandTotal()).append(" (#")
				.append(getLines(false).length).append(")");

		// - Description

		if ((getDescription() != null) && (getDescription().length() > 0)) {
			sb.append(" - ").append(getDescription());
		}

		return sb.toString();
	} // getSummary

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public int getDoc_User_ID() {
		return getSalesRep_ID();
	} // getDoc_User_ID

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 */

	public BigDecimal getApprovalAmt() {
		return getGrandTotal();
	} // getApprovalAmt

	public void calculateTotal() {
		setGrandTotal(getTotalLines().add(getChargeAmt()).add(
				totalTax(isTaxIncluded())));
	}

	public String getLetra() {
		String letra = null;
		if (getC_Letra_Comprobante_ID() != 0) {
			MLetraComprobante mLetraComprobante = new MLetraComprobante(
					Env.getCtx(), getC_Letra_Comprobante_ID(), null);
			letra = mLetraComprobante.getLetra();
		}
		return letra;
	}

	private boolean validateInvoice(MBPartner bp) {
		try {
			// Valida el límite de consumidor final
			FiscalDocumentPrint.validateInvoiceCFLimit(getCtx(), bp, this,
					get_TrxName());

			// Valida que si el documento es manual y el tipo de documento se
			// imprime fiscalmente entonces no sobrepase el último nro de
			// comprobante impreso fiscalmente
			if (!isSkipLastFiscalDocumentNoValidation() && requireFiscalPrint() && isManualDocumentNo()) {
				// FIXME Se comenta por ahora porque no se usa el último
				// comprobante
				// impreso por una cuestión de performance
				// Integer lastFiscalPrintedNumeroComprobante = MDocType
				// .getLastFiscalDocumentNumeroComprobantePrinted(getCtx(),
				// getC_DocTypeTarget_ID(), get_TrxName());
				Integer lastFiscalPrintedNumeroComprobante = getLastFiscalDocumentNumeroComprobantePrinted(
						getCtx(), getC_DocTypeTarget_ID(), getID(),
						get_TrxName());
				if (lastFiscalPrintedNumeroComprobante != null
						&& getNumeroComprobante() > lastFiscalPrintedNumeroComprobante
								.intValue() + 1) {
					throw new Exception(
							Msg.getMsg(
									getCtx(),
									"NroCompGreaterThanLastFiscalPrinted",
									new Object[] { lastFiscalPrintedNumeroComprobante }));
				}
				// TODO hay que guardar el último comprobante impreso
				// fiscalmente si éste es mayor al último
			}
		} catch (Exception e) {
			log.saveError("", e.getMessage());
			return false;
		}
		return true;
	}

	private void setDocumentNo() {
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		int posNumber = docType.getPosNumber();
		String letra = docType.getLetter();
		int nroComprobante = CalloutInvoiceExt.getNextNroComprobante(docType
				.getC_DocType_ID());
		setDocumentNo(CalloutInvoiceExt.GenerarNumeroDeDocumento(posNumber,
				nroComprobante, letra, isSOTrx()));
	}

	/**
	 * Verifica si la factura se encuentra en alguna asignación válida del
	 * sistema.
	 * 
	 * @param exceptAllocIDs
	 *            IDs de asignaciones que se deben ignorar para determinar la
	 *            condición de existencia.
	 * @return Verdadero en caso de que exista al menos una asignación en estado
	 *         CO o CL que contenga una línea activa cuya factura de débito o
	 *         crédito es esta factura.
	 */
	protected boolean isInAllocation(Integer[] exceptAllocIDs) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(*) ");
		sql.append(" FROM C_AllocationLine al ");
		sql.append(" INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID = ah.C_AllocationHdr_ID) ");
		sql.append(" WHERE al.IsActive = 'Y' AND ah.DocStatus IN ('CO','CL') ");
		sql.append("   AND (al.C_Invoice_ID = ? OR al.C_Invoice_Credit_ID = ?) ");
		if (exceptAllocIDs.length > 0) {
			sql.append(" AND ah.C_AllocationHdr_ID NOT IN (");
			for (int i = 0; i < exceptAllocIDs.length; i++) {
				Integer allocID = exceptAllocIDs[i];
				sql.append(allocID);
				sql.append(i == exceptAllocIDs.length - 1 ? ")" : ",");
			}
		}
		long allocCount = (Long) DB.getSQLObject(get_TrxName(), sql.toString(),
				new Object[] { getC_Invoice_ID(), getC_Invoice_ID() });
		return allocCount > 0;
	}

	/**
	 * Verifica si la factura se encuentra en alguna asignación válida del
	 * sistema.
	 * 
	 * @param exceptThisAllocID
	 *            ID de asignacion que se deben ignorar para determinar la
	 *            condición de existencia. Si el parámetro es NULL entonces no
	 *            se ignora ninguna asignación.
	 * @return Verdadero en caso de que exista al menos una asignación en estado
	 *         CO o CL que contenga una línea activa cuya factura de débito o
	 *         crédito es esta factura.
	 */
	protected boolean isInAllocation(Integer exceptThisAllocID) {
		Integer[] exceptAllocs;
		if (exceptThisAllocID != null)
			exceptAllocs = new Integer[] { exceptThisAllocID };
		else
			exceptAllocs = new Integer[] {};
		return isInAllocation(exceptAllocs);
	}

	/**
	 * Verifica si la factura se encuentra en alguna asignación válida del
	 * sistema.
	 * 
	 * @return Verdadero en caso de que exista al menos una asignación en estado
	 *         CO o CL que contenga una línea activa cuya factura de débito o
	 *         crédito es esta factura.
	 */
	protected boolean isInAllocation() {
		return isInAllocation(new Integer[] {});
	}

	/**
	 * ID de la asignación que intenta anular esta factura. En el caso de que
	 * desde una asignación se quiera anular una factura, es necesario que este
	 * factura sepa cual es la asignación que la está anulando para evitar la
	 * validación de asignaciones de facturas, de modo que la asignación
	 * anuladora no se tenga en cuenta en la validación.
	 */
	private Integer voiderAllocationID = null;

	/**
	 * @return the voiderAllocationID
	 */
	public Integer getVoiderAllocationID() {
		return voiderAllocationID;
	}

	/**
	 * @param voiderAllocationID
	 *            the voiderAllocationID to set
	 */
	public void setVoiderAllocationID(Integer voiderAllocationID) {
		this.voiderAllocationID = voiderAllocationID;
	}

	public BigDecimal getTaxesAmt() {
		MInvoiceTax[] taxes = getTaxes(false);
		BigDecimal total = Env.ZERO;
		for (int i = 0; i < taxes.length; i++) {
			total = total.add(taxes[i].getTaxAmt());
		}
		return total;
	}

	public BigDecimal getTotalLinesNet() {
		BigDecimal total = Env.ZERO;
		for (MInvoiceLine invoiceLine : getLines(true)) {
			// Total de líneas sin impuestos
			total = total.add(invoiceLine.getTotalPriceEnteredNet());
		}
		return total;
	}

	public BigDecimal getTotalLinesNetPerceptionIncluded() {
		BigDecimal total = Env.ZERO;
		for (MInvoiceLine invoiceLine : getLines(true)) {
			// Total de líneas sin impuestos
			total = total.add(invoiceLine.getLineNetAmount());
		}
		return total;
	}

	public BigDecimal getTotalLinesNetWithoutDocumentDiscount() {
		BigDecimal total = Env.ZERO;
		for (MInvoiceLine invoiceLine : getLines(true)) {
			// Total de líneas sin impuestos
			total = total.add(invoiceLine.getTotalPriceEnteredNet()).subtract(
					invoiceLine.getTotalDocumentDiscountUnityAmtNet());
		}
		return total;
	}

	public BigDecimal getTotalLinesNetPerceptionIncludedWithoutDocumentDiscount() {
		BigDecimal total = Env.ZERO;
		for (MInvoiceLine invoiceLine : getLines(true)) {
			// Total de líneas sin impuestos
			total = total.add(invoiceLine.getLineNetAmount()).subtract(
					invoiceLine.getTotalDocumentDiscountUnityAmtNet());
		}
		return total;
	}

	@Override
	public BigDecimal getChargeAmt() {
		return super.getChargeAmt() == null ? BigDecimal.ZERO : super
				.getChargeAmt();
	}

	private List<MDocumentDiscount> discounts = null;

	/**
	 * @return Los descuentos calculados para esta factura. Si no tiene
	 *         descuentos devuelve una lista vacía.
	 */
	public List<MDocumentDiscount> getDiscounts() {
		if (discounts == null) {
			discounts = MDocumentDiscount.getOfInvoice(getC_Invoice_ID(),
					getCtx(), get_TrxName());
		}
		return discounts;
	}

	/**
	 * @return la suma de todos los descuentos aplicados. Los descuentos se
	 *         toman del método {@link MInvoice#getDiscounts()}.
	 */
	public BigDecimal getDiscountsAmt() {
		BigDecimal discountAmt = BigDecimal.ZERO;
		for (MDocumentDiscount discount : getDiscounts()) {
			discountAmt = discountAmt.add(discount.getDiscountAmt());
		}
		return discountAmt;
	}

	public MBPartnerLocation getBPartnerLocation() {
		return new MBPartnerLocation(getCtx(), getC_BPartner_Location_ID(),
				get_TrxName());
	}

	public void setCurrentAccountVerified(boolean isCurrentAccountVerified) {
		this.isCurrentAccountVerified = isCurrentAccountVerified;
	}

	public boolean isCurrentAccountVerified() {
		return isCurrentAccountVerified;
	}

	public void setUpdateBPBalance(boolean updateBPBalance) {
		this.updateBPBalance = updateBPBalance;
	}

	public boolean isUpdateBPBalance() {
		return updateBPBalance;
	}

	/**
	 * Determina si la factura parámetro en la fecha parámetro está vencida e
	 * impaga (si el parámetro así lo requiera). La factura está vencida e
	 * impaga cuando:
	 * <ul>
	 * <li>Los esquemas de pago de la factura vencidos (a fecha parámetro)
	 * tienen monto pendiente.</li>
	 * <li>Si no tiene esquema de pago, determino la configuración adicional del
	 * esquema de vencimiento relacionado. Esta configuración puede ser:
	 * Siguiente fecha hábil, Días Neto, Fecha de vencimiento fija. Este es el
	 * orden por el cual se determina el vencimiento.</li>
	 * </ul>
	 * 
	 * @param ctx
	 *            contexto
	 * @param invoice
	 *            factura
	 * @param compareDate
	 *            fecha de comparación de vencimiento
	 * @param alsoUnpaided
	 *            verifica también además que este impago
	 * @param trxName
	 *            nombre de la transacción
	 * @return la diferencia de días entre la fecha de comparación y la fecha de
	 *         vencimiento, mayor a 0 está vencida e impaga dependiendo, 0
	 *         estamos en la fecha de vencimiento y menor a 0
	 */
	public static Integer isPastDue(Properties ctx, MInvoice invoice,
			Date compareDate, boolean alsoUnpaided, String trxName) {
		boolean pastDue = false;
		Integer diffDays = 0;
		// Si tiene un esquema de vencimientos asignado la factura parámetro lo
		// verifico
		if (invoice.getC_PaymentTerm_ID() != 0) {
			MInvoicePaySchedule[] invPaySchedules = MInvoicePaySchedule
					.getInvoicePaySchedule(ctx, invoice.getID(), 0, trxName);
			// Si hay esquema pago de factura verifico con la fecha de
			// vencimiento y acumulo el monto para luego verificar que esté
			// saldada completamente
			if (invPaySchedules != null && invPaySchedules.length > 0) {
				// Itero por el esquema de pagos de la factura
				for (int i = 0; i < invPaySchedules.length && !pastDue; i++) {
					// Determino la cantidad de días de diferencia entre la
					// fecha de comparación parámetro y la fecha de vencimiento
					// del pay schedule
					diffDays = invPaySchedules[i].diffDueDays(compareDate);
					// Si la diferencia es mayor a 0 significa que está vencida
					pastDue = diffDays > 0;
					// Si el parámetro alsoUnpaided es true, entonces verifico
					// también que no estén pagos los esquemas de pagos de
					// facturas
					if (pastDue && alsoUnpaided) {
						try {
							// Obtengo el invoiceopen
							BigDecimal invoiceopen = (BigDecimal) DB
									.getSQLObject(
											trxName,
											"SELECT currencyBase(invoiceopen(?,?),?,?,?,?)",
											new Object[] { invoice.getID(),
													invPaySchedules[i].getID(),
													invoice.getC_Currency_ID(),
													invoice.getDateInvoiced(),
													invoice.getAD_Client_ID(),
													invoice.getAD_Org_ID() });
							// Pendiente del esquema de pago actual
							boolean unpaid = invoiceopen
									.compareTo(BigDecimal.ZERO) > 0;
							if (!unpaid) {
								diffDays = 0;
							}
							pastDue = pastDue && unpaid;
						} catch (Exception e) {
							s_log.severe("Error in isPastDue method from MInvoice");
							e.printStackTrace();
						}
					}
				}
			}
		}
		return diffDays;
	}

	@Override
	public void setAuxiliarInfo(AuxiliarDTO auxDTO, boolean processed) {
		auxDTO.setAuthCode(getAuthCode());
		// Monto convertido
		BigDecimal invAmt = MConversionRate.convertBase(
				getCtx(),
				getGrandTotal(true), // CM adjusted
				getC_Currency_ID(), getDateAcct(), 0, getAD_Client_ID(),
				getAD_Org_ID());
		auxDTO.setAmt(invAmt);
		auxDTO.setDateTrx(getDateInvoiced());
		auxDTO.setDocType(MCentralAux.DOCTYPE_Invoice);
		auxDTO.setDocumentNo(getDocumentNo());
		auxDTO.setPaymentRule(getPaymentRule());
		auxDTO.setTenderType(CurrentAccountBalanceStrategy
				.getTenderTypeEquivalent(getPaymentRule()));
		// Signo en base al tipo de doc
		MDocType docType = new MDocType(getCtx(), getC_DocTypeTarget_ID(),
				get_TrxName());
		auxDTO.setDocTypeKey(docType.getDocTypeKey());
		auxDTO.setSign(Integer.parseInt(docType.getsigno_issotrx()));
		auxDTO.setTransactionType(isSOTrx() ? MCentralAux.TRANSACTIONTYPE_Customer
				: MCentralAux.TRANSACTIONTYPE_Vendor);
		auxDTO.setDocStatus(processed ? getDocStatus() : getDocAction());
		// HACK: EL matching de autorización se setea falso porque después se
		// realiza en la eliminación de transacciones
		setAuthMatch(false);
		// Fecha de vencimiento (Si tiene varias cual paso?)
		// auxDTO.setDueDate();
	}

	public void setAditionalWorkResult(Map<PO, Object> aditionalWorkResult) {
		this.aditionalWorkResult = aditionalWorkResult;
	}

	public Map<PO, Object> getAditionalWorkResult() {
		return aditionalWorkResult;
	}

	public void setConfirmAditionalWorks(boolean confirmAditionalWorks) {
		this.confirmAditionalWorks = confirmAditionalWorks;
	}

	public boolean isConfirmAditionalWorks() {
		return confirmAditionalWorks;
	}

	/**
	 * @return Indica si esta factura requiere ser emitida por un controlador
	 *         fiscal
	 */
	public boolean requireFiscalPrint() {
		return CalloutInvoiceExt.ComprobantesFiscalesActivos()
				&& MDocType.isFiscalDocType(getC_DocTypeTarget_ID());
	}

	/**
	 * @return true en caso que el tipo de documento para esta factura sea
	 *         electrónico, o false en caso contrario
	 */
	public boolean isElectronicInvoice() {
		return CalloutInvoiceExt.ComprobantesFiscalesActivos(false)
				&& MDocType.isElectronicDocType(getC_DocTypeTarget_ID());
	}

	/**
	 * Realiza la emisión fiscal de la factura mediante el controlador fiscal
	 * configurado en su tipo de documento
	 * 
	 * @param askAllowed
	 *            flag que determina si está permitido preguntar en caso de
	 *            error
	 * @return <code>null</code> si la impresión se realizó correctamente o el
	 *         mensaje de error si hubo algún error.
	 */
	public CallResult doFiscalPrint(boolean askAllowed) {
		CallResult printResult = new CallResult();
		// ////////////////////////////////////////////////////////////////
		// LOCALIZACIÓN ARGENTINA
		// Para la localización Argentina, si el tipo de documento está
		// configurado para imprimirse mediante un controlador fiscal,
		// se manda a emitir el comprobante a la impresora.
		if (requireFiscalPrint()) {
			// Aquí finaliza el guardado de documentos para TPV dado que a
			// partir de aquí se emite el comprobante mediante el controlador
			// fiscal. Si esta factura no está siendo completada por el TPV la
			// siguiente sentencia no produce ningún efecto.
			TimeStatsLogger.endTask(MeasurableTask.POS_SAVE_DOCUMENTS);

			TimeStatsLogger.beginTask(MeasurableTask.PRINT_FISCAL_INVOICE);

			// Impresor de comprobantes.
			printResult = FiscalPrintManager.printDocument(getCtx(), this,
					true, askAllowed, get_TrxName());
			if (printResult.isError()) {
				printResult
						.setMsg(!Util.isEmpty(printResult.getMsg()) ? printResult
								.getMsg() : Msg.getMsg(getCtx(),
								"PrintFiscalDocumentError"));
			}

			// Impresor de comprobantes.
			// FiscalDocumentPrint fdp = new FiscalDocumentPrint();
			// fdp.setTrx(Trx.get(get_TrxName(), false));
			// fireDocActionStatusChanged(new DocActionStatusEvent(this,
			// DocActionStatusEvent.ST_FISCAL_PRINT_DOCUMENT,
			// new Object[] { fdp }));
			// if (!fdp.printDocument(this)) {
			// m_processMsg = fdp.getErrorMsg();
			// return DocAction.STATUS_Invalid;
			// }

			TimeStatsLogger.endTask(MeasurableTask.PRINT_FISCAL_INVOICE);
		}
		return printResult;
	}

	public CallResult doFiscalPrint() {
		return doFiscalPrint(false);
	}

	private boolean ignoreFiscalPrint = false;

	/**
	 * @return el valor de ignoreFiscalPrint
	 */
	public boolean isIgnoreFiscalPrint() {
		return ignoreFiscalPrint;
	}

	/**
	 * @param ignoreFiscalPrint
	 *            el valor de ignoreFiscalPrint a asignar
	 */
	public void setIgnoreFiscalPrint(boolean ignoreFiscalPrint) {
		this.ignoreFiscalPrint = ignoreFiscalPrint;
	}

	/**
	 * @return true si la lista de precios asociada tiene el impuesto incluído
	 *         en el precio, false caso contrario
	 */
	public boolean isTaxIncluded() {
		MPriceList pl = MPriceList.get(getCtx(), getM_PriceList_ID(),
				get_TrxName());
		return pl.isTaxIncluded();
	}

	/**
	 * @return true si la lista de precios asociada tiene el impuesto de
	 *         percepciones incluído en el precio, false caso contrario
	 */
	public boolean isPerceptionsIncluded() {
		MPriceList pl = MPriceList.get(getCtx(), getM_PriceList_ID(),
				get_TrxName());
		return pl.isPerceptionsIncluded();
	}

	/**
	 * Método para tratar la actulización de MOrderLines asociadas en el caso de
	 * que la factura sea de "debito" (el caso normal) y pertenezca al circuito
	 * de ventas (esto es, es una factura a cliente). La función de este método
	 * es incrementar la cantidad facturada (C_OrderLine.QtyInvoiced) en las
	 * lineas de pedido asociadas a las lineas de factura. Genera un solo acceso
	 * a DB si es necesario.
	 * 
	 * @param lines
	 *            lienas de factura a partir de las cuales
	 * @return false si no la actualizacion fallo por algun motivo
	 */
	private boolean updateOrderIsSOTrxDebit(MInvoiceLine[] lines,
			Map<Integer, BigDecimal> orderLinesToUpdate) {
		// Ok, teoricamente no deberia poder haber 2 MInvoiceLIne de la misma
		// factura refiriendo a la misma MOrderLine; auqneu no parece
		// ser un restricción muy importante, se va a permitir esto (el codigo
		// orinal tambien los permitira

		// C_OrderLine_ID -> incremeto en QtyInvoiced
		HashMap<Integer, BigDecimal> molQtyInvoiced = new HashMap<Integer, BigDecimal>();
		BigDecimal orderLineToUpdateQty = null;
		for (int i = 0; i < lines.length; i++) {
			MInvoiceLine il = lines[i];
			if (il.getC_OrderLine_ID() <= 0)
				continue;
			int C_OrderLine_ID = il.getC_OrderLine_ID();
			BigDecimal qtyInvoiced = il.getQtyInvoiced();
			if (molQtyInvoiced.containsKey(C_OrderLine_ID)) {// en general no
																// deberia
																// pasar; sig
																// que dos
																// lineas de la
																// misma factura
																// a la misma
																// linea de
																// pedido
																// se suman las
																// cantidades
				qtyInvoiced = qtyInvoiced.add(molQtyInvoiced
						.get(C_OrderLine_ID));
				molQtyInvoiced.put(C_OrderLine_ID, qtyInvoiced);
			} else {
				molQtyInvoiced.put(C_OrderLine_ID, qtyInvoiced);
			}

			if (isUpdateOrderQty()) {
				// Cantidades
				addToMap(qtyInvoiced, il.getC_OrderLine_ID(), orderLinesToUpdate);
			}

		}

		// se crea la sentencia update

		StringBuffer sb = new StringBuffer();
		List<Integer> listIds = new ArrayList<Integer>();

		sb.append("UPDATE C_OrderLine SET QtyInvoiced  = QtyInvoiced + ( ");
		sb.append(" CASE C_OrderLine_ID ");

		for (Integer id : molQtyInvoiced.keySet()) {
			// este es el incremento para esta MOrderLine
			BigDecimal qtyInvoiced = molQtyInvoiced.get(id);
			if (qtyInvoiced.compareTo(BigDecimal.ZERO) == 0)
				continue; // 0 incremento
			listIds.add(id);

			sb.append(" WHEN ").append(id).append(" THEN ").append(qtyInvoiced);

		}
		sb.append(" END ) WHERE C_OrderLine_ID IN ");

		if (listIds.size() <= 0) // no hay ninguna c_orderLine que actualizar
			return true;

		sb.append(StringUtil.implodeForUnion(listIds)); // (12,2,4,89)
		String queryUpdate = sb.toString();

		int qtyUpdated = DB.executeUpdate(queryUpdate, get_TrxName());

		if (qtyUpdated != listIds.size())
			return false; // algo raro paso...

		return true;
	}

	private void addToMap(BigDecimal amt, Integer key, Map<Integer, BigDecimal> mapeo){
		BigDecimal number = mapeo.get(key);
		if (number == null) {
			number = BigDecimal.ZERO;
		}
		number = number.add(amt);
		mapeo.put(key, number);
	}
	
	// Ader: Manjejo de caches mutidocumentos, tambien de utilidad por ej, para
	// que reportes
	// no tarden tanto
	private MProductCache m_prodCache;

	public void initCaches() {
		MInvoiceLine[] lines = getLines();
		initCacheProdFromLines(lines);
		for (MInvoiceLine il : lines) {
			// propaga las caches a las lineas
			// esto probablemente se podria hacer tambine en getLines()
			// pero habria que hacerlo nuevametne aca
			il.setProductCache(m_prodCache);
		}
	}

	/**
	 * Carga en la cache de productos aquellos en la lineas (lines) que no se
	 * encuentran en la misma. Un solo acceso a DB como máximo y solo si hay al
	 * menos un producto no actualmete cacheado.
	 * 
	 * @param lines
	 *            MInvoiceLines's a partir de la cual obtener los ids de los
	 *            productos a cargar
	 * @return false si al menos un producto no se pudo cargar en cache (en
	 *         genral no deberia pasar)
	 */
	private boolean initCacheProdFromLines(MInvoiceLine[] lines) {
		if (m_prodCache == null)
			m_prodCache = new MProductCache(getCtx(), get_TrxName());

		List<Integer> newIds = new ArrayList<Integer>();

		for (int i = 0; i < lines.length; i++) {
			MInvoiceLine il = lines[i];
			int M_Product_ID = il.getM_Product_ID();
			if (M_Product_ID <= 0)
				continue;
			if (m_prodCache.contains(M_Product_ID))
				continue;
			if (newIds.contains(M_Product_ID))
				continue;
			newIds.add(M_Product_ID);
		}

		if (newIds.size() <= 0)
			return true; // nada para cargar, todos ya cacheados

		// carga masiva en cache; un solo acceso a DB
		int qtyCached = m_prodCache.loadMasive(newIds);

		if (qtyCached != newIds.size())
			return false; // algunos no se cargaron....

		return true;
	}

	public void setSkipManualGeneralDiscount(boolean skipManualGeneralDiscount) {
		this.skipManualGeneralDiscount = skipManualGeneralDiscount;
	}

	public boolean isSkipManualGeneralDiscount() {
		return skipManualGeneralDiscount;
	}

	public void setSkipApplyPaymentTerm(boolean skipApplyPaymentTerm) {
		this.skipApplyPaymentTerm = skipApplyPaymentTerm;
	}

	public boolean isSkipApplyPaymentTerm() {
		return skipApplyPaymentTerm;
	}

	private boolean validateOrderCurrencyConvert() {
		boolean valid = true;
		if (getC_Order_ID() > 0) {
			int orderCurrencyID = DB.getSQLValue(get_TrxName(),
					"SELECT C_Currency_ID FROM C_Order WHERE C_Order_ID = ?",
					getC_Order_ID());
			valid = orderCurrencyID == getC_Currency_ID()
					|| MCurrency.currencyConvert(new BigDecimal(1),
							orderCurrencyID, getC_Currency_ID(),
							getDateInvoiced(), getAD_Org_ID(), getCtx()) != null;
		}
		return valid;
	}

	private boolean validateInvoiceCurrencyConvert() {
		int currecy_Client = Env
				.getContextAsInt(Env.getCtx(), "$C_Currency_ID");
		if (getC_Currency_ID() != currecy_Client) {
			return (MCurrency.currencyConvert(new BigDecimal(1),
					currecy_Client, getC_Currency_ID(), getDateAcct(),
					getAD_Org_ID(), getCtx()) != null);
		}
		return true;
	}

	/**
	 * @return El wrapper de este pedido para ser utilizado en un calculador de
	 *         descuentos {@link DiscountCalculator}.
	 */
	public IDocument getDiscountableWrapper() {
		IDocument document = null;
		if (!isCreditMemo()) {
			document = new DiscountableMInvoiceWrapper();
		} else {
			document = new DiscountableMInvoiceCreditWrapper();
		}
		return document;
	}

	/**
	 * @return lista de percepciones aplicadas a esta factura
	 */
	public List<MInvoiceTax> getAppliedPercepciones() {
		String sql = "select it.* " + "from c_invoicetax as it "
				+ "inner join c_tax as t on t.c_tax_id = it.c_tax_id "
				+ "where it.c_invoice_id = ? AND t.ispercepcion = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MInvoiceTax> percepciones = new ArrayList<MInvoiceTax>();
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getID());
			rs = ps.executeQuery();
			while (rs.next()) {
				percepciones.add(new MInvoiceTax(getCtx(), rs, get_TrxName()));
			}
		} catch (Exception e) {
			log.severe("ERROR getting percepciones");
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e2) {
				log.severe("ERROR getting percepciones");
				e2.printStackTrace();
			}
		}
		return percepciones;
	}

	/**
	 * @return lista de percepciones aplicadas a esta factura
	 */
	public List<DocumentTax> getDocumentAppliedPercepciones() {
		List<MInvoiceTax> invoiceTaxes = getAppliedPercepciones();
		List<DocumentTax> documentTaxes = new ArrayList<DocumentTax>();
		DocumentTax doctax;
		for (MInvoiceTax invoiceTax : invoiceTaxes) {
			// Se debe determinar el porcentaje del impuesto que se aplicó
			// en el documento, esto se determina con el importe base y el
			// monto del impuesto
			doctax = new DocumentTax();
			doctax.setTaxID(invoiceTax.getC_Tax_ID());
			doctax.setTaxAmt(invoiceTax.getTaxAmt());
			doctax.setTaxBaseAmt(invoiceTax.getTaxBaseAmt());
			doctax.setTaxRate();
			documentTaxes.add(doctax);
		}
		return documentTaxes;
	}

	public BigDecimal getPercepcionesTotalAmt() {
		String sql = "select sum(taxamt)" + "from c_invoicetax as it "
				+ "inner join c_tax as t on t.c_tax_id = it.c_tax_id "
				+ "where it.c_invoice_id = ? AND t.ispercepcion = 'Y'";
		BigDecimal percepcionAmt = DB
				.getSQLValueBD(get_TrxName(), sql, getID());
		return percepcionAmt == null ? BigDecimal.ZERO : percepcionAmt;
	}

	public void calculatePercepciones() throws Exception {
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		if (docType.isFiscalDocument()
				&& docType.isApplyPerception() 
				&& MOrgPercepcion.existsOrgPercepcion(getCtx(), getAD_Org_ID(), get_TrxName()) 
				&& ((docType.getDocBaseType().equals("ARC") && !MPreference.GetCustomPreferenceValueBool("SinPercepcionNCManual")) 
						|| !docType.getDocBaseType().equals("ARC") 
						|| isVoidProcess())) {
			GeneratorPercepciones generator = new GeneratorPercepciones(
					getCtx(), getDiscountableWrapper(), get_TrxName());
			generator.calculatePercepciones(this);
		}
	}

	public void recalculatePercepciones() throws Exception {
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		if (docType.isFiscalDocument()
				&& docType.isApplyPerception()
				&& MOrgPercepcion.existsOrgPercepcion(getCtx(), getAD_Org_ID(),	get_TrxName()) 
				&& ((docType.getDocBaseType().equals("ARC") && !MPreference.GetCustomPreferenceValueBool("SinPercepcionNCManual")) 
						|| !docType.getDocBaseType().equals("ARC") 
						|| isVoidProcess())) {
			GeneratorPercepciones generator = new GeneratorPercepciones(
					getCtx(), getDiscountableWrapper(), get_TrxName());
			generator.recalculatePercepciones(this);
		}
	}

	/**
	 * Obtiene el monto de descuento a nivel de documento a partir de las líneas
	 * 
	 * @param monto
	 *            de descuento a nivel de documento a partir de las líneas
	 * @param set
	 *            true si se debe setear en la instancia actual, false caso
	 *            contrario
	 */
	public BigDecimal getTotalDocumentDiscountAmtFromLines(boolean set) {
		String sql = "select sum(documentdiscountamt) as documentdiscountamt from c_invoiceline where c_invoice_id = ?";
		BigDecimal amt = DB.getSQLValueBD(get_TrxName(), sql, getID());
		amt = Util.isEmpty(amt, false) ? BigDecimal.ZERO : amt;
		if (set) {
			setChargeAmt(amt.negate());
		}
		return amt;
	}

	public boolean isTPVInstance() {
		return isTPVInstance;
	}

	public void setTPVInstance(boolean isTPVInstance) {
		this.isTPVInstance = isTPVInstance;
	}

	public void setDragDocumentDiscountAmts(boolean dragDocumentDiscountAmts) {
		this.dragDocumentDiscountAmts = dragDocumentDiscountAmts;
	}

	public boolean isDragDocumentDiscountAmts() {
		return dragDocumentDiscountAmts;
	}

	public void updateTotalDocumentDiscount() throws Exception {
		getTotalDocumentDiscountAmtFromLines(true);
		calculateTaxTotal();
		if (!save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		}
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

	public boolean isSkipAutomaticCreditAllocCreation() {
		return skipAutomaticCreditAllocCreation;
	}

	public void setSkipAutomaticCreditAllocCreation(
			boolean skipAutomaticCreditAllocCreation) {
		this.skipAutomaticCreditAllocCreation = skipAutomaticCreditAllocCreation;
	}

	public boolean isThrowExceptionInCancelCheckStatus() {
		return throwExceptionInCancelCheckStatus;
	}

	public void setThrowExceptionInCancelCheckStatus(
			boolean throwExceptionInCancelCheckStatus) {
		this.throwExceptionInCancelCheckStatus = throwExceptionInCancelCheckStatus;
	}

	public Integer updateGrandTotal(String trxName) {
		String sql = null;
		if (isTaxIncluded()) {
			// El total es la suma del neto (con impuesto tmb está en el neto),
			// del monto del cargo y de la suma de los montos de los impuestos
			// manuales ya que no se tienen en cuenta en el monto con impuesto
			// incluído
			sql = "UPDATE C_Invoice i "
					+ " SET GrandTotal=TotalLines + "
					+ "(SELECT COALESCE(SUM(TaxAmt),0) FROM C_InvoiceTax it inner join c_tax t on t.c_tax_id = it.c_tax_id inner join c_taxcategory tc on tc.c_taxcategory_id = t.c_taxcategory_id WHERE i.C_Invoice_ID=it.C_Invoice_ID and tc.ismanual = 'Y')"
					+ " + ChargeAmt " + "WHERE C_Invoice_ID="
					+ getC_Invoice_ID();
		} else {
			// El total es la suma del neto, del monto del cargo y de la suma de
			// los montos de todos los impuestos relacionados
			sql = "UPDATE C_Invoice i "
					+ " SET GrandTotal=TotalLines+"
					+ "(SELECT COALESCE(SUM(TaxAmt),0) FROM C_InvoiceTax it inner join c_tax t on t.c_tax_id = it.c_tax_id WHERE i.C_Invoice_ID=it.C_Invoice_ID"
					+ (isPerceptionsIncluded() ? " and t.ispercepcion <> 'Y')"
							: ")") + " + ChargeAmt " + "WHERE C_Invoice_ID="
					+ getC_Invoice_ID();
		}

		return DB.executeUpdate(sql, trxName);
	}

	/**
	 * El monto neto de la factura es: la suma de todos los TaxBaseAmt para los
	 * cuales la categoría de impuesto no es manual. Esto descarta las
	 * percepciones.
	 * 
	 * @param trxName
	 * @return monto neto
	 */
	public BigDecimal getNetAmount(String trxName) {
		String sql = null;
		// El monto neto de la factura es:
		// la suma de todos los TaxBaseAmt para los cuales la categoría de
		// impuesto no es manual. Esto descarta las percepciones.
		sql = "SELECT COALESCE(SUM(TaxBaseAmt),0) "
				+ "FROM C_Tax t "
				+ "INNER JOIN C_TaxCategory tc ON (tc.C_TaxCategory_ID = t.C_TaxCategory_ID) "
				+ "INNER JOIN C_InvoiceTax it ON (it.C_Tax_ID = t.C_Tax_ID) "
				+ "INNER JOIN C_Invoice i ON (i.C_Invoice_ID = it.C_Invoice_ID) "
				+ "WHERE (i.C_Invoice_ID=?) AND (tc.IsManual = 'N')";

		return DB.getSQLValueBD(trxName, sql, getC_Invoice_ID());
	}

	public Integer updateNetAmount(String trxName) {
		String sql = null;
		// El monto neto de la factura es:
		// la suma de todos los TaxBaseAmt para los cuales la categoría de
		// impuesto no es manual. Esto descarta las percepciones.
		sql = "UPDATE C_Invoice i "
				+ " SET NetAmount="
				+ "(SELECT COALESCE(SUM(TaxBaseAmt),0) "
				+ "FROM C_Tax t "
				+ "INNER JOIN C_TaxCategory tc ON (tc.C_TaxCategory_ID = t.C_TaxCategory_ID) "
				+ "INNER JOIN C_InvoiceTax it ON (it.C_Tax_ID = t.C_Tax_ID) "
				+ "WHERE (tc.IsManual = 'N') AND (i.C_Invoice_ID=it.C_Invoice_ID) ) "
				+ "WHERE (C_Invoice_ID=" + getC_Invoice_ID() + ")";

		return DB.executeUpdate(sql, trxName);
	}

	public BigDecimal calculateNetAmount(String trxName) {
		return DB
				.getSQLValueBD(
						trxName,
						"SELECT COALESCE(SUM(TaxBaseAmt),0) FROM C_Tax t INNER JOIN C_TaxCategory tc ON (tc.C_TaxCategory_ID = t.C_TaxCategory_ID) INNER JOIN C_InvoiceTax it ON (it.C_Tax_ID = t.C_Tax_ID) WHERE (tc.IsManual = 'N') AND (it.C_Invoice_ID=?)",
						getC_Invoice_ID());
	}

	/**
	 * Wrapper de {@link MInvoice} para cálculo de descuentos.
	 */
	private class DiscountableMInvoiceWrapper extends DiscountableDocument {

		@Override
		protected List<? extends Object> getOriginalLines() {
			return Arrays.asList(getLines(false));
		}

		@Override
		protected IDocumentLine createDocumentLine(Object originalLine) {
			return ((MInvoiceLine) originalLine)
					.createDiscountableWrapper(this);
		}

		@Override
		public Date getDate() {
			return getDateInvoiced();
		}

		/*@Override
		public BigDecimal getLinesTotalAmt(boolean includeOtherTaxesAmt) {
			BigDecimal totalAmt = BigDecimal.ZERO;
			for (IDocumentLine line : getDocumentLines()) {
				totalAmt = totalAmt.add(line.getTotalAmt());
			}
			if(includeOtherTaxesAmt){
				totalAmt = totalAmt.add(MInvoice.this.getPercepcionesTotalAmt());
			}
			return totalAmt;
		}*/
		
		@Override
		public void setTotalDocumentDiscount(BigDecimal discountAmount) {
			// En la factura se invierte el signo del descuento ya que un valor
			// positivo representa un cargo al encabezado de la factura.
			setChargeAmt(discountAmount.negate());
			if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
				setC_Charge_ID(0);
			}
		}

		@Override
		public boolean isCalculateNetDiscount() {
			return true;
		}

		@Override
		public void setDocumentReferences(MDocumentDiscount documentDiscount) {
			documentDiscount.setC_Order_ID(getC_Order_ID());
			documentDiscount.setC_Invoice_ID(getID());
		}

		@Override
		public void setDocumentDiscountChargeID(int chargeID) {
			setC_Charge_ID(chargeID);
		}

		@Override
		public void setTotalBPartnerDiscount(BigDecimal discountAmount) {
			// TODO por ahora no es necesario ya que esta clase se creó para el
			// cálculo de percepciones
		}

		@Override
		public void setTotalManualGeneralDiscount(BigDecimal discountAmount) {
			// TODO por ahora no es necesario ya que esta clase se creó para el
			// cálculo de percepciones
		}

		@Override
		public Integer getOrgID() {
			return MInvoice.this.getAD_Org_ID();
		}

		@Override
		public Integer getBPartnerID() {
			return MInvoice.this.getC_BPartner_ID();
		}

		@Override
		public Integer getDocTypeID() {
			return MInvoice.this.getC_DocTypeTarget_ID();
		}

		@Override
		public boolean isSOTrx() {
			return MInvoice.this.isSOTrx();
		}

		@Override
		public boolean isApplyPercepcion() {
			return MInvoice.this.isApplyPercepcion();
		}

		@Override
		public List<DocumentTax> getAppliedPercepciones() {
			return MInvoice.this.getDocumentAppliedPercepciones();
		}
	}

	private class DiscountableMInvoiceCreditWrapper extends
			DiscountableMInvoiceWrapper implements ICreditDocument {

		@Override
		public IDocument getDebitRelatedDocument() {
			// Determinar el débito relacionado al crédito
			IDocument debitDocument = null;
			MInvoice debitInvoice = MInvoice.getDebitFor(MInvoice.this);
			if (debitInvoice != null) {
				debitDocument = debitInvoice.getDiscountableWrapper();
			}
			return debitDocument;
		}

		/**
		 * @return lista de percepciones a aplicar al documento
		 */
		@Override
		public List<MTax> getApplyPercepcion(GeneratorPercepciones generator)
				throws Exception {
			return generator.getCreditApplyPercepciones();
		}
	}

	private static String getDocTypeBaseKey(String tipoComprobante) {
		if (tipoComprobante != null) {
			if (MInvoice.TIPOCOMPROBANTE_Factura
					.compareToIgnoreCase(tipoComprobante) == 0)
				return MDocType.DOCTYPE_CustomerInvoice;
			else if (MInvoice.TIPOCOMPROBANTE_NotaDeDébito
					.compareToIgnoreCase(tipoComprobante) == 0)
				return MDocType.DOCTYPE_CustomerDebitNote;
			else if (MInvoice.TIPOCOMPROBANTE_NotaDeCrédito
					.compareToIgnoreCase(tipoComprobante) == 0)
				return MDocType.DOCTYPE_CustomerCreditNote;
		}

		return "";
	}

	public String getVoidPOSJournalConfig() {
		return voidPOSJournalConfig;
	}

	public void setVoidPOSJournalConfig(String voidPOSJournalConfig) {
		this.voidPOSJournalConfig = voidPOSJournalConfig;
	}

	public boolean isSkipManualGeneralDiscountValidation() {
		return skipManualGeneralDiscountValidation;
	}

	public void setSkipManualGeneralDiscountValidation(
			boolean skipManualGeneralDiscountValidation) {
		this.skipManualGeneralDiscountValidation = skipManualGeneralDiscountValidation;
	}

	public boolean isAllowSetOrderPriceList() {
		return allowSetOrderPriceList;
	}

	public void setAllowSetOrderPriceList(boolean allowSetOrderPriceList) {
		this.allowSetOrderPriceList = allowSetOrderPriceList;
	}

	@Override
	public int getAuthorizationID() {
		return this.getM_AuthorizationChain_ID();
	}

	@Override
	public void setDocumentID(X_M_AuthorizationChainDocument authDocument) {
		authDocument.setC_Invoice_ID(this.getC_Invoice_ID());
	}

	@Override
	public Integer getDocTypeID() {
		return MInvoice.this.getC_DocTypeTarget_ID();
	}

	public boolean isSkipExtraValidations() {
		return skipExtraValidations;
	}

	public void setSkipExtraValidations(boolean skipExtraValidations) {
		this.skipExtraValidations = skipExtraValidations;
	}

	public boolean isSkipModelValidations() {
		return skipModelValidations;
	}

	public void setSkipModelValidations(boolean skipModelValidations) {
		this.skipModelValidations = skipModelValidations;
	}

	@Override
	public void copyInstanceValues(PO to){
		super.copyInstanceValues(to);
		((MInvoice)to).setSkipExtraValidations(isSkipExtraValidations());
		((MInvoice)to).setSkipModelValidations(isSkipModelValidations());
	}

	public boolean isVoidProcess() {
		return voidProcess;
	}

	public void setVoidProcess(boolean voidProcess) {
		this.voidProcess = voidProcess;
	}

	public boolean isSkipLastFiscalDocumentNoValidation() {
		return skipLastFiscalDocumentNoValidation;
	}

	public void setSkipLastFiscalDocumentNoValidation(boolean skipLastFiscalDocumentNoValidation) {
		this.skipLastFiscalDocumentNoValidation = skipLastFiscalDocumentNoValidation;
	}

	@Override
	public boolean isSkipCurrentAccount() {
		return MDocType.get(getCtx(), getC_DocTypeTarget_ID()).isSkipCurrentAccounts();
	}

	public boolean isSkipAuthorizationChain() {
		return skipAuthorizationChain;
	}

	public void setSkipAuthorizationChain(boolean skipAuthorizationChain) {
		this.skipAuthorizationChain = skipAuthorizationChain;
	}

	/**
	 * @return true si el tipo de documento es de alguno de los tipo de
	 *         retención (débito o crédito), false caso contrario
	 */
	public boolean isRetencion(){
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		return docType.getDocTypeKey().equals(MDocType.DOCTYPE_Retencion_Receipt)
				|| docType.getDocTypeKey().equals(MDocType.DOCTYPE_Retencion_ReceiptCustomer)
				|| docType.getDocTypeKey().equals(MDocType.DOCTYPE_Retencion_Invoice)
				|| docType.getDocTypeKey().equals(MDocType.DOCTYPE_Retencion_InvoiceCustomer);
	}
	
	/**
	 * @return true si el tipo de documento es de alguno de los tipo de
	 *         documento de saldo, false caso contrario
	 */
	public boolean isBalanceDocType(){
		MDocType docType = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
		return docType.getDocTypeKey().equals(MDocType.DOCTYPE_Saldo_Inicial_Cliente)
				|| docType.getDocTypeKey().equals(MDocType.DOCTYPE_Saldo_Inicial_Cliente_Credito)
				|| docType.getDocTypeKey().equals(MDocType.DOCTYPE_Saldo_Inicial_Proveedor)
				|| docType.getDocTypeKey().equals(MDocType.DOCTYPE_Saldo_Inicial_Proveedor_Credito);
	}

	public boolean isDiffCambio() {
		String valueProductDiffCambio = MPreference.GetCustomPreferenceValue("DIF_CAMBIO_ARTICULO");
        String valueProductDiffCambioDeb = MPreference.GetCustomPreferenceValue("DIF_CAMBIO_ARTICULO_DEB");
        String valueProductDiffCambioCred = MPreference.GetCustomPreferenceValue("DIF_CAMBIO_ARTICULO_CRED");
        
		int count = DB.getSQLValue(get_TrxName(), " SELECT count(1) FROM c_invoiceline il " +
				" INNER JOIN m_product p ON il.m_product_id = p.m_product_id " +
				" WHERE il.c_invoice_id = " + getID() +
				" AND p.value IN ('" + valueProductDiffCambio + "',"
						      + " '" + valueProductDiffCambioDeb + "',"
							  + " '" + valueProductDiffCambioCred + "')");
		
		return count > 0;
	}
	
	public BigDecimal getNetTaxBaseAmt(){
		BigDecimal total = Env.ZERO;
		for (MInvoiceLine invoiceLine : getLines()) {
			// Total de líneas sin impuestos
			total = total.add(invoiceLine.getNetTaxBaseAmt());
		}
		return total;
	}
	
	/**
	 * Calcula y setea los importes totales del documento en base a
	 * c_invoicetax: Neto, TotalLines, Grandtotal.
	 */
	public void calculateTotalAmounts() throws Exception{
		// Obtener la suma de los impuestos automáticos y manuales para luego
		// desde ahi setear los totales
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal taxBaseAmt = BigDecimal.ZERO;
		BigDecimal automaticTaxesAmt = BigDecimal.ZERO;
		BigDecimal manualTaxesAmt = BigDecimal.ZERO;
		try {
			// Impuestos automáticos
			ps = DB.prepareStatement(
					SalesUtil.getSQLTaxAmountsForTotals(X_C_InvoiceTax.Table_Name, "c_invoice_id", getID(), false),
					get_TrxName());
			rs = ps.executeQuery();
			if(rs.next()){
				taxBaseAmt = rs.getBigDecimal("taxbaseamt");
				automaticTaxesAmt = automaticTaxesAmt.add(rs.getBigDecimal("taxamt"));
			}
			
			// Impuestos manuales
			ps = DB.prepareStatement(
					SalesUtil.getSQLTaxAmountsForTotals(X_C_InvoiceTax.Table_Name, "c_invoice_id", getID(), true),
					get_TrxName());
			rs = ps.executeQuery();
			if(rs.next()){
				manualTaxesAmt = manualTaxesAmt.add(rs.getBigDecimal("taxamt"));
			}
			
			// Actualizar totales
			setNetAmount(taxBaseAmt);
			setTotalLines(taxBaseAmt.add(automaticTaxesAmt));
			setGrandTotal(taxBaseAmt.add(automaticTaxesAmt).add(manualTaxesAmt));
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
	
	/**
	 * @param bothMandatory
	 *            true si se debe controlar ambas gestiones de descuentos y
	 *            recargos
	 * @return true si se debe gestionar descuentos y/o recargos, dependiendo
	 *         del parámetro
	 */
	public boolean isManageDragOrderDiscountsSurcharges(boolean bothMandatory){
		return bothMandatory?isManageDragOrderDiscounts() && isManageDragOrderSurcharges():
			isManageDragOrderDiscounts() || isManageDragOrderSurcharges();
	}

	public boolean isDragDocumentSurchargesAmts() {
		return dragDocumentSurchargesAmts;
	}

	public void setDragDocumentSurchargesAmts(boolean dragDocumentSurchargesAmts) {
		this.dragDocumentSurchargesAmts = dragDocumentSurchargesAmts;
	}

} // MInvoice

/*
 * @(#)MInvoice.java 02.07.07
 * 
 * Fin del fichero MInvoice.java
 * 
 * Versión 2.1
 */
