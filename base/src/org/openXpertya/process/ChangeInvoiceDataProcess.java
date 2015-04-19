package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayUtil;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class ChangeInvoiceDataProcess extends AbstractSvrProcess {

	/** Comprobantes */
	private Set<String> documents = new HashSet<String>();
	
	/** Allocations */
	private Set<String> allocations = new HashSet<String>(); 
	
	/** Líneas de Caja */
	private Set<String> cashLines = new HashSet<String>();
	
	/** Payments */
	private Set<String> payments = new HashSet<String>();
	
	@Override
	protected String doIt() throws Exception {
		// Obtener la factura parámetro
		Integer invoiceID = (Integer)getParametersValues().get("C_INVOICE_ID");
		// Obtener caja diaria 
		Integer posJournalID = (Integer)getParametersValues().get("C_POSJOURNAL_ID");
		MPOSJournal posJournal = new MPOSJournal(getCtx(), posJournalID, get_TrxName());
		// Modificar Factura
		changeInvoice(invoiceID, posJournal.getID(), posJournal.getDateTrx());
		// Cambiar la fecha y caja diaria de los cobros relacionadas a la factura
		changePayments(invoiceID, posJournal, posJournal.getDateTrx());
		
		return getMsg();
	}
	
	/**
	 * Modificar fecha y caja de la factura
	 * @param invoiceID id de la factura
	 * @param posJournalID id de la caja diaria
	 * @param date fecha
	 * @throws Exception
	 */
	protected void changeInvoice(Integer invoiceID, Integer posJournalID, Timestamp date) throws Exception{
		MInvoice invoice = new MInvoice(getCtx(), invoiceID, get_TrxName());
		invoice.setC_POSJournal_ID(posJournalID);
		invoice.setDateAcct(date);
		invoice.setDateInvoiced(date);
		Boolean setFiscalPrinted = ((String) getParametersValues().get(
				"FISCALALREADYPRINTED")).equals("Y");
		invoice.setFiscalAlreadyPrinted(setFiscalPrinted);
		if(!invoice.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		getDocuments().add(invoice.getDocumentNo());
	}
	
	/**
	 * Obtener los allocation del comprobante parámetro
	 * 
	 * @param invoiceID
	 *            id del comprobante
	 * @return lista de allocations
	 * @throws Exception
	 */
	protected List<MAllocationHdr> getAllocationsForInvoice(Integer invoiceID) throws Exception{
		List<MAllocationHdr> allocations = new ArrayList<MAllocationHdr>();
		// Obtener los allocations
		String sql = "SELECT distinct ah.* " +
					 "FROM c_allocationhdr as ah " +
					 "INNER JOIN c_allocationline as al ON al.c_allocationhdr_id = ah.c_allocationhdr_id " +
					 "WHERE al.c_invoice_id = "+invoiceID+
					 " ORDER BY ah.updated desc ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			rs = ps.executeQuery();
			while (rs.next()) {
				allocations.add(new MAllocationHdr(getCtx(), rs, get_TrxName()));
			}
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
		return allocations;
	}
	
	/***
	 * Modifica la caja diaria, fecha y libro de caja para efectivo de los
	 * allocations relacionados al comprobante
	 * 
	 * @param invoiceID
	 *            id del comprobante
	 * @param posJournal
	 *            caja diaria
	 * @param date
	 *            fecha
	 * @throws Exception
	 */
	protected void changePayments(Integer invoiceID, MPOSJournal posJournal, Timestamp date) throws Exception{
		// Obtener los allocations, sus cobros y pagos y cambiarles la fecha y
		// la caja diaria
		// Se modifica la caja diaria y la fecha de los allocations
		// Se modifica la caja diaria y fecha de los payments
		// Se modifica la caja diaria y fecha de las NC
		// Se modifica la caja diaria, la fecha y el libro de caja del efectivo
		Boolean modifyPayments = ((String) getParametersValues().get(
				"CHANGEPAYMENTS")).equals("Y");
		if(modifyPayments){
			// Modificar allocations
			changeAllocations(invoiceID, posJournal, date);
			// Modificar efectivos asociados a la factura que no estén en allocations
			changeNotAllocatedCashlines(invoiceID, posJournal, date);
		}
	}
	
	protected void changeAllocations(Integer invoiceID, MPOSJournal posJournal, Timestamp date) throws Exception{
		List<MAllocationHdr> allocations = getAllocationsForInvoice(invoiceID);
		for (MAllocationHdr mAllocationHdr : allocations) {
			// Cambiar la fecha y caja diaria del allocation
			mAllocationHdr.setC_POSJournal_ID(posJournal.getID());
			mAllocationHdr.setDateAcct(date);
			mAllocationHdr.setDateTrx(date);
			if(!mAllocationHdr.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			getAllocations().add(mAllocationHdr.getDocumentNo());
			// Cambiar la caja y fecha de los payments
			for (MAllocationLine allocationLine : mAllocationHdr.getLines(true)) {
				// Sólo se toma como un allocation válido que las líneas cumplan lo siguiente sobre el campo c_invoice_id: 
				// 1) Que sea null, esto significa que es una imputación unidireccional.
				// ó
				// 2) Que sea el mismo que el parámetro
				// ó
				// 3) Que esté marcado como anulable
				// Si no cumple alguna de esas condiciones, entonces error
				// A su vez, si es anulable, se debe modificar también ese comprobante
				if(!Util.isEmpty(allocationLine.getC_Invoice_ID()) 
						&& allocationLine.getC_Invoice_ID() != invoiceID.intValue()){
					MInvoice invoice = new MInvoice(getCtx(),
							allocationLine.getC_Invoice_ID(), get_TrxName());
					if(!invoice.isVoidable()){
						throw new Exception(Msg.getMsg(getCtx(), "ExistsAnotherDebitsInAllocation"));
					}
					else{
						changeInvoice(invoice.getID(), posJournal.getID(), date);
					}
				}
				// Payment
				if(!Util.isEmpty(allocationLine.getC_Payment_ID(), true)){
					changePayment(allocationLine.getC_Payment_ID(),
							posJournal.getID(), date);
				}
				// CashLine
				else if(!Util.isEmpty(allocationLine.getC_CashLine_ID(), true)){
					changeCashLine(allocationLine.getC_CashLine_ID(),
							posJournal, date);
				}
				// Invoice Credit
				else if(!Util.isEmpty(allocationLine.getC_Invoice_Credit_ID(), true)){
					changeInvoice(
							allocationLine.getC_Invoice_Credit_ID(),
							posJournal.getID(), date);
				}
			}
		}
	}
	
	protected void changePayment(Integer paymentID, Integer posJournalID, Timestamp date) throws Exception{
		changePay(paymentID, posJournalID, date);
		MPayment payment = new MPayment(getCtx(), paymentID, get_TrxName());
		// El pago está anulado o revertido, entonces probablemente posea un
		// contra-pago, el cual seguro está asociado a este cobro en algún
		// allocation
		if (payment.getDocStatus().equals(MPayment.DOCSTATUS_Reversed)
				|| payment.getDocStatus().equals(MPayment.DOCSTATUS_Voided)) {
			// Buscar el allocation donde se encuentra asignado con el contra-pago
			changeVoidedPayment(paymentID, posJournalID, date);
		}
	}
	
	protected void changePay(Integer paymentID, Integer posJournalID, Timestamp date) throws Exception{
		MPayment payment = new MPayment(getCtx(), paymentID, get_TrxName());
		payment.setC_POSJournal_ID(posJournalID);
		payment.setDateAcct(date);
		payment.setDateTrx(date);
		if(!payment.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		getPayments().add(payment.getDocumentNo());
	}
	
	protected void changeCashLine(Integer cashLineID, MPOSJournal posJournal, Timestamp date) throws Exception{
		MCashLine cashLine = new MCashLine(getCtx(), cashLineID, get_TrxName());
		Integer beforeCashID = cashLine.getC_Cash_ID();
		cashLine.setC_POSJournal_ID(posJournal.getID());
		cashLine.setC_Cash_ID(posJournal.getC_Cash_ID());
		if(!cashLine.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Actualizar los totales de la caja anterior del cashline y del cash de
		// la caja diaria
		// Caja de la caja diaria
		updateCashTotals(posJournal.getC_Cash_ID());
		// Caja de la caja diaria es distinta a la caja anterior del cashline,
		// entonces actualizar la caja anterior
		if(posJournal.getC_Cash_ID() != beforeCashID.intValue()){
			updateCashTotals(beforeCashID);
		}
		getCashLines().add(
				DisplayUtil.getDisplayByIdentifiers(getCtx(), cashLine,
						cashLine.get_Table_ID(), get_TrxName()));
	}
	
	protected void updateCashTotals(Integer cashID){
		MCash cash = new MCash(getCtx(), cashID, get_TrxName());
		cash.updateAmts();
	}
	
	protected void changeNotAllocatedCashlines(Integer invoiceID, MPOSJournal posJournal, Timestamp date) throws Exception{
		// Obtener las líneas de caja que están asociadas al comprobante, pero
		// no en un allocation
		String sql = "select distinct cl.c_cashline_id " +
					 "from c_cashline as cl " +
					 "where c_invoice_id = ? and not exists (select c_allocationline_id " +
					 "												from c_allocationline as al " +
					 "												where al.c_cashline_id = cl.c_cashline_id)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, invoiceID);
			rs = ps.executeQuery();
			while (rs.next()) {
				changeCashLine(rs.getInt("c_cashline_id"), posJournal, date);	
			}
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
	
	protected void changeVoidedPayment(Integer paymentID, Integer posJournalID, Timestamp date) throws Exception{
		// Obtener los allocations activos que poseen el payment anulado sin comprobante asociado
		// Los allocations que cumplan eso, son allocations de anulación de payments
		String sql = "select distinct ah.* " +
					 "from c_allocationline as al " +
					 "inner join c_allocationhdr as ah on ah.c_allocationhdr_id = al.c_allocationhdr_id " +
					 "where c_payment_id = ? and (ah.isactive = 'Y' OR ah.docstatus IN ('CO','CL')) and c_invoice_id is null";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, paymentID);
			rs = ps.executeQuery();
			MAllocationHdr allocationHdr;
			while (rs.next()) {
				allocationHdr = new MAllocationHdr(getCtx(), rs, get_TrxName());
				for (MAllocationLine allocationLine : allocationHdr.getLines(true)) {
					if(!Util.isEmpty(allocationLine.getC_Payment_ID(), true) 
							&& allocationLine.getC_Payment_ID() != paymentID){
						changePay(allocationLine.getC_Payment_ID(), posJournalID, date);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
	}
	
	protected String getMsg(){
		HTMLMsg msg = new HTMLMsg();
		addMsgList(msg, getDocuments(), "ArchivedDocuments");
		addMsgList(msg, getAllocations(), "Allocations");
		addMsgList(msg, getPayments(), "Payments");
		addMsgList(msg, getCashLines(), "CashLines");
		return msg.toString();
	}
	
	protected void addMsgList(HTMLMsg msg, Set<String> elements, String adMessageListTitle){
		if(elements.size() > 0){
			HTMLList list = msg.new HTMLList(adMessageListTitle, "ul",
					Msg.getMsg(getCtx(), adMessageListTitle));
			for (String element : elements) {
				msg.createAndAddListElement(element, element, list);
			}
			msg.addList(list);
		}
	}

	protected Set<String> getDocuments() {
		return documents;
	}

	protected void setDocuments(Set<String> documents) {
		this.documents = documents;
	}

	protected Set<String> getAllocations() {
		return allocations;
	}

	protected void setAllocations(Set<String> allocations) {
		this.allocations = allocations;
	}

	protected Set<String> getCashLines() {
		return cashLines;
	}

	protected void setCashLines(Set<String> cashLines) {
		this.cashLines = cashLines;
	}

	protected Set<String> getPayments() {
		return payments;
	}

	protected void setPayments(Set<String> payments) {
		this.payments = payments;
	}
}
