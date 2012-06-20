package org.openXpertya.pos.view;

import java.text.DateFormat;
import java.text.NumberFormat;

import org.compiere.swing.CComboBox;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.pos.ctrl.PoSModel;

public class OfflinePoSComponentFactory extends PoSComponentFactory {


	/**
	 * @param windowNo
	 */
	public OfflinePoSComponentFactory(int windowNo, PoSModel poSModel) {
		super(windowNo, poSModel);
	}

	@Override
	public VLookup createBPartnerSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VLookup createCurrencyCombo() {
		// TODO AuSto-generated method stub
		return null;
	}

	@Override
	public VLookup createBankCombo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CComboBox createCreditCardCombo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CComboBox createPoSConfigCombo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VLookup createProductSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VLookup createOrderSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateFormat getDateFormat(int displayType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NumberFormat getNumberFormat(int displayType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CComboBox createPriceListCombo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CComboBox createTenderTypeCombo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VLookup createBankAccountCombo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VLookup createCreditNoteSearch() {
		// TODO Auto-generated method stub
		return null;
	}
}
