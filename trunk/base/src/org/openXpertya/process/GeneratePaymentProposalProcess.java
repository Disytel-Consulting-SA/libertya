package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBankAccount;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoicePaySchedule;
import org.openXpertya.model.MPaymentBatchPO;
import org.openXpertya.model.MPaymentBatchPODetail;
import org.openXpertya.model.MPaymentBatchPOInvoices;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class GeneratePaymentProposalProcess extends SvrProcess {
	
	Timestamp dueDate = null;
	String batchPaymentRule = null;
	
	MPaymentBatchPO paymentBatch = null;
	
	/** Organización de los comprobantes a tomar */
	private Integer orgID = null;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();	
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );

			String name = para[ i ].getParameterName();

			if( para[ i ].getParameter() == null ) {
				;
			} else if( name.equalsIgnoreCase( "dueDate" )) {
				dueDate = (Timestamp)para[i].getParameter();
            } else if( name.equalsIgnoreCase( "batchPaymentRule" )) {
            	batchPaymentRule = (String)para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "AD_Org_ID" )) {
            	orgID = para[ i ].getParameterAsInt();            	
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
		}
		
		//Lote de pagos
		paymentBatch = new MPaymentBatchPO(getCtx(), getRecord_ID(), get_TrxName());
	}
	
	private void validateParameters() throws Exception {
		if (dueDate == null) {
            log.log( Level.SEVERE,"Se produjo un error al obtener los Parámetros del reporte." );
            throw new Exception("@DueDateRequired@");
        }
	}

	@Override
	protected String doIt() throws Exception {
		//1-Valido parámetros
		validateParameters();
		
		//2-Obtengo las facturas según parámetros agrupadas por proveedor
		Map<Integer, List<MInvoicePaySchedule>> invoices = getInvoices();
		
		//3-Genero un detalle de pagos por proveedor
		for (Entry<Integer, List<MInvoicePaySchedule>> data : invoices.entrySet()) {
			generateBatchDetails(data.getKey(), data.getValue());
		}
		
		//4-Modifico valor del campo asociado al botón que dispara este proceso para activar
		//  la lógica de solo lectura y no poder ejecutarlo nuevmente si no se eliminan los detalles generados
		paymentBatch.setGeneratePaymentProposal("Y");
		if (!paymentBatch.save()) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "PaymentBatchPODetailGenerationError") + ": " + paymentBatch.getProcessMsg());
		}
		
		return Msg.getMsg(getCtx(), "PaymentBatchPODetailGenerationOK");
	}

	/**
	 * Retorna todas los C_InvoicePaySchedule según parámetros para generar el detalle de pagos
	 * agrupados en un map por proveedor
	 * @return
	 */
	private Map<Integer, List<MInvoicePaySchedule>> getInvoices() {
		Map<Integer, List<MInvoicePaySchedule>> map = new HashMap<Integer, List<MInvoicePaySchedule>>();
		
		//Construyo la query
		String sql = "SELECT ps.* " + 
					 "FROM C_InvoicePaySchedule ps " +
					 "INNER JOIN C_Invoice i ON ps.C_Invoice_ID = i.C_Invoice_ID " +
					 "INNER JOIN C_BPartner bp ON i.C_BPartner_ID = bp.C_BPartner_ID " +
					 "WHERE " + 
					  " ps.duedate <= ? " +
					  " AND i.docstatus IN ('CO', 'CL') " +  //Considerando autorizadas las facturas que están completas o cerradas
					  " AND (i.m_authorizationchain_id is null OR i.authorizationchainstatus = '"
					  + MInvoice.AUTHORIZATIONCHAINSTATUS_Authorized + "') " +
					  " AND bp.batch_payment_rule IS NOT NULL " +
					  " AND bp.C_BankAccount_ID IS NOT NULL " +
					  " AND invoiceopen(i.C_Invoice_ID, ps.c_InvoicePaySchedule_ID) > 0 ";
		
		if(!Util.isEmpty(getOrgID(), true)){
			sql += " AND ps.ad_org_id = ? ";
		}
		
		if (batchPaymentRule != null)
			sql += " AND bp.batch_payment_rule = ? ";
		
		sql += "ORDER BY ps.duedate ASC";
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			int p = 1;
			ps.setTimestamp(p++, dueDate);
			if(!Util.isEmpty(getOrgID(), true)){
				ps.setInt(p++, getOrgID());
			}
			if (batchPaymentRule != null)
				ps.setString(p++, batchPaymentRule);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				MInvoicePaySchedule paySchedule = new MInvoicePaySchedule(getCtx(), rs, get_TrxName());
				MInvoice invoice = new MInvoice(getCtx(), paySchedule.getC_Invoice_ID(), get_TrxName());
				if (map.get(invoice.getC_BPartner_ID()) != null) {
					map.get(invoice.getC_BPartner_ID()).add(paySchedule);
				} else {
					List<MInvoicePaySchedule> list = new ArrayList<MInvoicePaySchedule>();
					list.add(paySchedule);
					map.put(invoice.getC_BPartner_ID(), list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	private void generateBatchDetails(Integer cBPartnerId, List<MInvoicePaySchedule> payList) {
		//Proveedor
		MBPartner vendor = new MBPartner(getCtx(), cBPartnerId, get_TrxName());
		MBankAccount backAccount = new MBankAccount(getCtx(), vendor.getC_BankAccount_ID(), get_TrxName());
		
		//Campos calculados
		Timestamp firstDueDate = null;
		Timestamp lastDueDate = null;
//		BigDecimal totalAmount = new BigDecimal(0);
		Timestamp total = null;
		Timestamp avg = null; 
		
		for (MInvoicePaySchedule paySchedule : payList) {
			if (firstDueDate == null || firstDueDate.compareTo(paySchedule.getDueDate()) > 0)
				firstDueDate = new Timestamp(paySchedule.getDueDate().getTime());
			if (lastDueDate == null || lastDueDate.compareTo(paySchedule.getDueDate()) < 0)
				lastDueDate = new Timestamp(paySchedule.getDueDate().getTime());
//			totalAmount = totalAmount.add(paySchedule.getOpenAmount());
			if (total == null)
				total = new Timestamp(paySchedule.getDueDate().getTime());
			else 
				total.setTime(total.getTime() + paySchedule.getDueDate().getTime());
		}
		
		avg = new Timestamp(total.getTime() / payList.size());
		
		//Creo el Detalle de Pagos
		MPaymentBatchPODetail detail = new MPaymentBatchPODetail(getCtx(), 0, get_TrxName());
		detail.setC_PaymentBatchPO_ID(getRecord_ID());
		detail.setC_BPartner_ID(cBPartnerId);
		detail.setBatch_Payment_Rule(vendor.getBatch_Payment_Rule());
		detail.setC_BankAccount_ID(vendor.getC_BankAccount_ID());
		detail.setC_Bank_ID(backAccount.getC_Bank_ID());
		detail.setFirstDueDate(firstDueDate);
		detail.setLastDueDate(lastDueDate);
		if (paymentBatch.getPaymentDateRule().equals("U")) {
			detail.setPaymentDate(
					new Timestamp(
							lastDueDate.getTime() + (
									new Timestamp(
											paymentBatch.getAddDays() * 24 * 60 * 60 * 1000
									).getTime()
							)
					)
			);
		}
		if (paymentBatch.getPaymentDateRule().equals("F")) {
			detail.setPaymentDate(paymentBatch.getPaymentDate());
		}
		if (paymentBatch.getPaymentDateRule().equals("P")) {
			detail.setPaymentDate(avg);
		}
		detail.setPaymentAmount(new BigDecimal(0));
		
		//Verifico que la fecha de pago no sea menor a la del lote
		if (detail.getPaymentDate().compareTo(paymentBatch.getBatchDate()) < 0)
			detail.setPaymentDate(paymentBatch.getBatchDate());
		
		if (!detail.save()) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "PaymentBatchPODetailGenerationError") + ": " + CLogger.retrieveErrorAsString());
		}
		
		//Creo las "Facturas" asociadas al detalle de Pagos
		for (MInvoicePaySchedule paySchedule : payList) {
			MPaymentBatchPOInvoices detailInvoices = new MPaymentBatchPOInvoices(getCtx(), 0, get_TrxName());
			detailInvoices.setC_PaymentBatchpoDetail_ID(detail.getID());
			detailInvoices.setC_Invoice_ID(paySchedule.getC_Invoice_ID());
			MInvoice invoice = new MInvoice(getCtx(), paySchedule.getC_Invoice_ID(), get_TrxName());
			detailInvoices.setDocumentNo(invoice.getDocumentNo());
			detailInvoices.setDateInvoiced(invoice.getDateInvoiced());
			detailInvoices.setDueDate(paySchedule.getDueDate());
			
			//Importe en pesos de la factura 
			BigDecimal convertedAmt = MCurrency.currencyConvert(
					paySchedule.getDueAmt(), invoice.getC_Currency_ID(),
					paymentBatch.getC_Currency_ID(), paymentBatch.getBatchDate(), invoice.getAD_Org_ID(),
					getCtx());
			
			//Importe pendiente en pesos de la factura
			BigDecimal convertedOpenAmt = MCurrency.currencyConvert(
					paySchedule.getOpenAmount(), invoice.getC_Currency_ID(),
					paymentBatch.getC_Currency_ID(), paymentBatch.getBatchDate(), invoice.getAD_Org_ID(),
					getCtx());
			if (convertedOpenAmt == null || convertedAmt == null) {
				throw new IllegalArgumentException(Msg.getMsg(getCtx(), "ConvertionRateInvalid"));
			}
			detailInvoices.setInvoiceAmount(convertedAmt);
			detailInvoices.setOpenAmount(convertedOpenAmt);
			detailInvoices.setPaymentAmount(convertedOpenAmt);
			detailInvoices.setC_InvoicePaySchedule_ID(paySchedule.getID());
			if (!detailInvoices.save()) {
				throw new IllegalArgumentException(Msg.getMsg(getCtx(), "PaymentBatchPODetailGenerationError") + ": " + CLogger.retrieveErrorAsString());
			}
		}
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}
	
}
