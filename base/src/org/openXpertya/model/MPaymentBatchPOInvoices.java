package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MPaymentBatchPOInvoices extends X_C_PaymentBatchPOInvoices {
	
	private BigDecimal oldPaymentAmount = null; 
	
	public MPaymentBatchPOInvoices(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MPaymentBatchPOInvoices(Properties ctx, int C_PaymentBatchPOInvoices_ID, String trxName) {
		super(ctx, C_PaymentBatchPOInvoices_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1377325076495272116L;
	
	protected boolean beforeSave(boolean newRecord) { 
		if (!newRecord)
			oldPaymentAmount = (BigDecimal)get_ValueOld("PaymentAmount");
		
		//Valido que PaySchedule tenga si o si un valor (cuando no se ejecuta la función de la ventana no puedo saberlo
		//y no puede ser obligatorio porque es un campo recuperado por un callout desde la ventana info y el contexto
		if (getC_InvoicePaySchedule_ID() == 0) {
			m_processMsg = Msg.getMsg(getCtx(), "InvoicePayScheduleMandatory");
            return false;
		}
		
		//Recupero Datos
		MPaymentBatchPODetail detail = new MPaymentBatchPODetail(getCtx(), getC_PaymentBatchpoDetail_ID(), get_TrxName());
		MInvoice invoice = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
		MInvoicePaySchedule paySchedule = new MInvoicePaySchedule(getCtx(), getC_InvoicePaySchedule_ID(), get_TrxName());
		MPaymentBatchPO paymentBatch = new MPaymentBatchPO(getCtx(), detail.getC_PaymentBatchPO_ID(), get_TrxName()); 
		
		//Verifico que la factura elegida sea del proveedor seleccionado en el detalle 
	    //(por defecto la ventana info para búsqueda filtra por proveedor)
		if (invoice.getC_BPartner_ID() != detail.getC_BPartner_ID()) {
			m_processMsg = Msg.getMsg(getCtx(), "VendorDifferent");
            return false;
		}
			
		
		//Verifico que la factura elegida no esté ya incorporada en el detalle
		if (invoiceInDetail(getC_Invoice_ID(), getC_PaymentBatchpoDetail_ID())) {
			m_processMsg = Msg.getMsg(getCtx(), "InvoiceAlreadyInDetail"); 
			return false;
		}
		
		//Verifico que tenga importes pendientes y que esté CO o CL
		if (!invoiceIsOk(getC_Invoice_ID(), getC_InvoicePaySchedule_ID())) {
			m_processMsg = Msg.getMsg(getCtx(), "InvoiceWithoutOpenAmountOrNotAuthorized"); 
			return false;
		}
		
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
			m_processMsg = Msg.getMsg(getCtx(), "ConvertionRateInvalid"); 
			return false;
		}
		
		//Actualizo los valores de los campos calculados siempre y cuando el valor previo sea null 
		if (getDocumentNo() == null) setDocumentNo(invoice.getDocumentNo());
		if (getDateInvoiced() == null) setDateInvoiced(invoice.getDateInvoiced());
		if (getDueDate() == null) setDueDate(paySchedule.getDueDate());
        if (getInvoiceAmount() == null) setInvoiceAmount(convertedAmt);
        if (getOpenAmount() == null) setOpenAmount(convertedOpenAmt);
        if (getPaymentAmount() == null) setPaymentAmount(convertedOpenAmt);		
		return true;
	}
	
	public static boolean invoiceIsOk(Integer invoiceId, Integer C_InvoicePaySchedule_ID) {
		//Construyo la query
		String sql = "SELECT count(*) " + 
					 "FROM C_Invoice " +
					 "WHERE " + 
					  "c_invoice_id = ? " +
					  "AND invoiceopen(?, ?) > 0 " +
					  "AND docstatus IN ('CO', 'CL')";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, null);
			
			//Parámetros
			ps.setInt(1, invoiceId);
			ps.setInt(2, invoiceId);
			ps.setInt(3, C_InvoicePaySchedule_ID);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) > 0)
					return true;
				else
					return false;
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
		return false;
	}
	
	public static boolean invoiceInDetail(Integer invoiceId, Integer detailId) {
		//Construyo la query
		String sql = "SELECT count(*) " + 
					 "FROM C_PaymentBatchPOInvoices " +
					 "WHERE " + 
					  "c_invoice_id = ? " +
					  "AND c_paymentbatchpodetail_id = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, null);
			
			//Parámetros
			ps.setInt(1, invoiceId);
			ps.setInt(2, detailId);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) > 0)
					return true;
				else
					return false;
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
		return false;
	}

	protected boolean afterSave(boolean newRecord, boolean success) {
		if( !success ) {
            return success;
        }
		
		if( newRecord || is_ValueChanged( "PaymentAmount" )) {
			updateDetail(newRecord);
		}
		return true;
	}
	
	protected boolean afterDelete( boolean success ) {
        if( !success ) {
            return success;
        }

        updateDetail(false);
        return true;
    }
	
	private void updateDetail(boolean newRecord) {
		MPaymentBatchPODetail detail = new MPaymentBatchPODetail(getCtx(), this.getC_PaymentBatchpoDetail_ID(), get_TrxName());
		
		//Si es una actualización solo recalculo el importe total
		if (oldPaymentAmount != null) {
			if (!oldPaymentAmount.equals(this.getPaymentAmount())) {
				detail.setPaymentAmount(detail.getPaymentAmount().subtract(oldPaymentAmount).add(this.getPaymentAmount()));
			}
		} else {
			//Si es nuevo, sumo el importe y actualizo fechas
			if (newRecord) {
				detail.setPaymentAmount(detail.getPaymentAmount() != null ? detail.getPaymentAmount().add(this.getPaymentAmount()) : this.getPaymentAmount());
				if (detail.getFirstDueDate() == null || detail.getFirstDueDate().compareTo(this.getDueDate()) > 0) //Si la primer fecha es mayor
					detail.setFirstDueDate(this.getDueDate());
				if (detail.getLastDueDate() == null || detail.getLastDueDate().compareTo(this.getDueDate()) < 0) //Si la última fecha es menor
					detail.setLastDueDate(this.getDueDate());
			} else {
				//Sino resto el importe y actualizo fechas
				detail.setPaymentAmount(detail.getPaymentAmount().subtract(this.getPaymentAmount()));
				updateDatesOnDelete(detail);
			}
		}
		if (!detail.save()) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "PaymentBatchPODetailGenerationError") + ": " + detail.getProcessMsg());
		}
	}
	
	private void updateDatesOnDelete(MPaymentBatchPODetail detail) {
		//Construyo la query
		String sql = "SELECT " +
					 "min(duedate) AS firstDueDate, " +
					 "max(duedate) AS lastDueDate " +
					 "FROM " +
					    "c_paymentbatchpoinvoices " +
					 "WHERE " +
					 	"c_paymentbatchpodetail_id = ? " + 
					    "AND c_paymentbatchpoinvoices_id != ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setInt(1, this.getC_PaymentBatchpoDetail_ID());
			ps.setInt(2, this.getID());
			rs = ps.executeQuery();
			while (rs.next()) {
				detail.setFirstDueDate(rs.getTimestamp("firstDueDate"));
				detail.setLastDueDate(rs.getTimestamp("lastDueDate"));
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
	}

}
