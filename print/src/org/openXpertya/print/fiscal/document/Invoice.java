package org.openXpertya.print.fiscal.document;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.print.fiscal.exception.DocumentException;

public class Invoice extends Document {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1702265732103996574L;
	
	/** Número del CAI*/
	private String caiNumber;
	/** Número del remito asociado a la factura */
	private String packingSlipNumber;
	/** Lista de pagos realizados */
	private List<Payment> payments;
	/** Indica si la factura a consumidor final no supera el monto
	 * máximo permitido sin identificación del cliente */
	private boolean validCFInvoice;

	public Invoice() {
		super();
		payments = new ArrayList<Payment>();
	}
		
	/**
	 * @return Returns the packingSlipNumber.
	 */
	public String getPackingSlipNumber() {
		return packingSlipNumber;
	}

	/**
	 * @param packingSlipNumber The packingSlipNumber to set.
	 */
	public void setPackingSlipNumber(String packingSlipNumber) {
		this.packingSlipNumber = packingSlipNumber;
	}
	
	/**
	 * @return Retorna verdadero si la factura tiene un 
	 * número de remito asociado.
	 */
	public boolean hasPackingSlipNumber() {
		return getPackingSlipNumber() != null && !getPackingSlipNumber().equals("");
	}

	/**
	 * @return Returns the caiNumber.
	 */
	public String getCAINumber() {
		return caiNumber;
	}

	/**
	 * @param caiNumber The caiNumber to set.
	 */
	public void setCAINumber(String caiNumber) {
		this.caiNumber = caiNumber;
	}
	
	/**
	 * @return Indica si la factura tiene asignado o no un número de CAI.
	 */
	public boolean hasCAINumber() {
		return getCAINumber() != null && !getCAINumber().equals("");
	}

	@Override
	public String getDocumentType() {
		return DT_INVOICE;
	}

	@Override
	public void validate() throws DocumentException {
		super.validate();

		// Se asume que en este momento la factura es válida con respecto al
		// monto límite por Consumidor final ya que la validación se hace en el
		// prepareIt de MInvoice
		setValidCFInvoice(true);
		
		// Validar cantidad de líneas mayor que 0.
		if(getLines().isEmpty()) 
			throw createDocumentException("InvalidDocumentLinesCount", this);
		
		// Se validan los pagos.
		for (Payment payment : getPayments()) {
			payment.validate();
		}
	}
	
	/**
	 * Agrega un pago al documento.
	 * @param payment Pago a agregar.
	 */
	public void addPayment(Payment payment) {
		payments.add(payment);
	}

	/**
	 * Elimina un pago del documento en caso de existir.
	 * @param payment Pago a eliminar.
	 */
	public void removePayment(Payment payment) {
		payments.remove(payment);
	}

	/**
	 * @return Returns the payments.
	 */
	public List<Payment> getPayments() {
		return payments;
	}

	/**
	 * @return Retorna verdadero en caso de que el documento tenga asignado
	 * algun pago.
	 */
	public boolean hasPayments() {
		return !getPayments().isEmpty();
	}

	/**
	 * @return Returns the validCFInvoice.
	 */
	public boolean isValidCFInvoice() {
		return validCFInvoice;
	}

	/**
	 * @param validCFInvoice The validCFInvoice to set.
	 */
	protected void setValidCFInvoice(boolean validCFInvoice) {
		this.validCFInvoice = validCFInvoice;
	}
}
