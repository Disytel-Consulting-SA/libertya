package org.openXpertya.pos.view;

import java.text.DateFormat;
import java.text.NumberFormat;

import org.compiere.swing.CComboBox;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.pos.ctrl.PoSModel;

public abstract class PoSComponentFactory {

	private int windowNo;
	private PoSModel poSModel;
	
	/**
	 * @param windowNo
	 */
	public PoSComponentFactory(int windowNo, PoSModel poSModel) {
		super();
		this.windowNo = windowNo;
		this.poSModel = poSModel;
	}

	public abstract VLookup createBPartnerSearch();

	public abstract VLookup createCurrencyCombo(); 
	
	public abstract VLookup createBankCombo();
	
	public abstract CComboBox createCreditCardCombo();

	public abstract CComboBox createPoSConfigCombo();
	
	public abstract VLookup createProductSearch();
	
	public abstract VLookup createOrderSearch();
	
	public abstract DateFormat getDateFormat(int displayType);
	
	public abstract NumberFormat getNumberFormat(int displayType);
	
	public abstract CComboBox createPriceListCombo();
	
	public abstract CComboBox createTenderTypeCombo();
	
	public abstract VLookup createBankAccountCombo();
	
	public abstract VLookup createCreditNoteSearch();
		
	public PoSModel getPoSModel() {
		return poSModel;
	}

	public void setPoSModel(PoSModel poSModel) {
		this.poSModel = poSModel;
	}
	
	/**
	 * @return Devuelve windowNo.
	 */
	public int getWindowNo() {
		return windowNo;
	}

	/**
	 * @param windowNo Fija o asigna windowNo.
	 */
	public void setWindowNo(int windowNo) {
		this.windowNo = windowNo;
	}
}
