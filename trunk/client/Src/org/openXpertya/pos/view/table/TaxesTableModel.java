package org.openXpertya.pos.view.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.pos.model.Tax;

public class TaxesTableModel extends AbstractPoSTableModel {

	/** Impuestos */
	private List<Tax> taxes;
	
	public TaxesTableModel() {
		setTaxes(new ArrayList<Tax>());
	}

	public TaxesTableModel(List<Tax> taxes) {
		setTaxes(taxes);
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Tax tax = getTaxes().get(row);
		switch (col) {
			case 0: 
				return tax.getName();
			case 1: 
				return tax.getAmount() == null ? BigDecimal.ZERO : tax.getAmount();
			default: 
				return null;
		}
	}
	
	@Override
	public List getObjects() {
		return getTaxes();
	}

	public List<Tax> getTaxes() {
		return taxes;
	}

	public void setTaxes(List<Tax> taxes) {
		this.taxes = taxes;
	}

}
