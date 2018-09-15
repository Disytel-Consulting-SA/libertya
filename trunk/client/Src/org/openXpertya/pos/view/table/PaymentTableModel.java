package org.openXpertya.pos.view.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import org.openXpertya.pos.model.Payment;

public class PaymentTableModel extends AbstractPoSTableModel {

	private List<Payment> payments;
	
	public PaymentTableModel() {
		super();
		setPayments(new ArrayList<Payment>());
	}
	
	public PaymentTableModel(List<Payment> payments) {
		super();
		setPayments(payments);
	}

	public Object getValueAt(int row, int col) {
		Payment payment = getPayments().get(row);
		switch (col) {
			case 0: 
				return payment.getTypeName();
			case 1: 
			return (payment.getAmount().compareTo(payment.getRealAmount()) > 0 ? payment.getAmount()
					: payment.getRealAmount())
					.setScale(2, BigDecimal.ROUND_HALF_UP)
					.add(payment.isCreditCardPayment() ? payment.getChangeAmt()
							: BigDecimal.ZERO);
			default: 
				return null;
		}
	}

	@Override
	public List getObjects() {
		return getPayments();
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	
}
