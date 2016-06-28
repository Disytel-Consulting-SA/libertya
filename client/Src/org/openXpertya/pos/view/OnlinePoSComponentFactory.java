package org.openXpertya.pos.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.compiere.swing.CComboBox;
import org.openXpertya.apps.form.VComponentsFactory;
import org.openXpertya.grid.ed.VComboBox;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MRefList;
import org.openXpertya.pos.ctrl.PoSConfig;
import org.openXpertya.pos.ctrl.PoSModel;
import org.openXpertya.pos.model.EntidadFinanciera;
import org.openXpertya.pos.model.PriceList;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.ValueNamePair;

public class OnlinePoSComponentFactory extends PoSComponentFactory {

	/**
	 * @param windowNo
	 */
	public OnlinePoSComponentFactory(int windowNo, PoSModel poSModel) {
		super(windowNo,poSModel);
	}

	@Override
	public VLookup createBPartnerSearch() {
		VLookup bPartnerLookup =  VComponentsFactory.VLookupFactory("C_BPartner_ID","C_BPartner",getWindowNo(),DisplayType.Search);
		bPartnerLookup.setMandatory(true);
		bPartnerLookup.setBackground(true);
		bPartnerLookup.addVetoableChangeListener(new VetoableChangeListener() {

			public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
				String pName = event.getPropertyName();
				Object pValue = event.getNewValue();
				VLookup lookup = (VLookup)event.getSource();
				// El valor del componente cambio.
				if(pName == "C_BPartner_ID") {
					lookup.setBackground(false);
				}
			}
			
		});
		return bPartnerLookup;
	}

	@Override
	public VLookup createCurrencyCombo() {
		VLookup currencyLookup =  VComponentsFactory.VLookupFactory("C_Currency_ID","C_Currency",getWindowNo(),DisplayType.Table);
		return currencyLookup;
	}

	public CComboBox createBankComboOld() {
		/*
		 * Mantenido por las dudas pero actualmente no está en uso
		 * Reemplazado por createBankCombo -> VLookup
		 */
		CComboBox bankCombo = new VComboBox();
		// Obtiene los bancos de la referencia
		ValueNamePair[] banks = MRefList.getList(MPOSPaymentMedium.BANK_AD_Reference_ID, false, Env.getCtx());
		// Se ordenan por nombre (por defecto vienen ordenados por value)
		List<ValueNamePair> list = Arrays.asList(banks);
		Collections.sort(list, new Comparator<ValueNamePair>() {
			@Override
			public int compare(ValueNamePair o1, ValueNamePair o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		// Se agregan al combo
		for (ValueNamePair bank : list) {
			bankCombo.addItem(bank);
		}
		return bankCombo;
	}

	@Override
	public VLookup createBankCombo() {
		// Lookup Directo igual al que aparece en la ventana de Medios de Pago 
		VLookup bankLookup =  VComponentsFactory.VLookupFactory("Bank","C_POSPaymentMedium",getWindowNo(),DisplayType.List);
		return bankLookup;
	}
	
	@Override
	public CComboBox createCreditCardCombo() {
		CComboBox cardCombo = new CComboBox();
		List<EntidadFinanciera> eFinancieras = getPoSModel().getEntidadesFinancieras();
		for (EntidadFinanciera financiera : eFinancieras) {
			cardCombo.addItem(financiera);
		}
		return cardCombo;
	}

	@Override
	public CComboBox createPoSConfigCombo() {
		CComboBox configCombo = new CComboBox();
		List<PoSConfig> posConfigs = getPoSModel().getPoSConfigs();
		for (PoSConfig config : posConfigs) {
			configCombo.addItem(config);
		}
		return configCombo;
	}

	@Override
	public VLookup createProductSearch() {
		VLookup productLookup =  VComponentsFactory.VPoSLookupFactory("M_Product_ID","M_Product",getWindowNo(),DisplayType.Search);
		return productLookup;
	}

	@Override
	public VLookup createOrderSearch() {
		VLookup orderLookup = VComponentsFactory
				.VPoSLookupFactory(
						"C_Order_ID",
						"C_Order",
						getWindowNo(),
						DisplayType.Search,
						"C_Order.DocStatus IN ('CO','CL')"
						+ getPoSModel().validateSearchToday() 
						+ " AND C_Order.C_DocType_ID IN (SELECT C_DocType_ID FROM C_DocType WHERE ad_client_id = "
							+ Env.getAD_Client_ID(Env.getCtx())
							+ " AND enabledinpos = 'Y' "
							+ " AND (posenabledue = 'N' OR (posenabledue = 'Y' AND (current_date <= (date_trunc('day',C_Order.dateordered) + cast(posenableduedays||' days' as interval))))))");			
		return orderLookup;
	}

	@Override
	public DateFormat getDateFormat(int displayType) {
		return DisplayType.getDateFormat(displayType);
	}

	@Override
	public NumberFormat getNumberFormat(int displayType) {
		return DisplayType.getNumberFormat(displayType);
	}
	
	@Override
	public CComboBox createPriceListCombo() {
		CComboBox priceListLookup = new CComboBox();
		List<PriceList> priceLists = getPoSModel().getPriceLists();
		for (PriceList priceList : priceLists) {
			priceListLookup.addItem(priceList);
		}
		return priceListLookup;
	}

	@Override
	public CComboBox createTenderTypeCombo() {
		CComboBox tenderTypeCombo = new CComboBox();
		// Obtiene los tipos de pago disponibles a partir del contexto TPV, en
		// realidad se excluye recibos de cliente para obtener solo tpv y ambos 
		List<ValueNamePair> list = MPOSPaymentMedium
				.getTenderTypesByContextOfUse(Env.getCtx(),
						MPOSPaymentMedium.CONTEXT_CustomerReceiptsOnly, true,
						true, null);
		// Se agregan al combo
		for (ValueNamePair tenderType : list) {
			// Si debo crear facturas entonces se agregan todos los medios de
			// pago
			if(getPoSModel().getConfig().isCreateInvoice()){
				tenderTypeCombo.addItem(tenderType);
			}
			// Si no se deben crear facturas no tiene sentido cargar cobros
			// diferentes de A Crédito
			// TODO esto debería parametrizarse en la config del tpv?
			else if (tenderType.getValue().equals(
					MPOSPaymentMedium.TENDERTYPE_Credit)) {
				tenderTypeCombo.addItem(tenderType);
			}
		}
		return tenderTypeCombo;
	}

	@Override
	public VLookup createBankAccountCombo() {
		VLookup bankLookup =  
			VComponentsFactory.VLookupFactory(
					"C_BankAccount_ID",
					"C_BankAccount",
					getWindowNo(),
					DisplayType.TableDir, 
					// Si es un cheque la cuenta tiene que ser de Cheques en Cartera.
					"(('@TenderType@'= 'K' AND C_BankAccount.IsChequesEnCartera = 'Y') " +
					// Si es una transferencia la cuenta no puede ser de Cheques en Cartera
					"OR ('@TenderType@' = 'A' AND C_BankAccount.IsChequesEnCartera = 'N') " +
					// Para el resto de los medios de pago se muestran todas las cuentas
					"OR ('@TenderType@' NOT IN ('K','A')) )"
			);
		return bankLookup;
	}

	@Override
	public VLookup createCreditNoteSearch() {
		VLookup bankLookup =  
			VComponentsFactory.VLookupFactory(
					"C_Invoice_ID",
					"C_Invoice",
					getWindowNo(),
					DisplayType.Search,
					"C_Invoice.DocStatus IN ('CO','CL') " +
					"AND C_Invoice.IsSOTrx='Y' " +
					// Por el momento solo notas de crédito cuya moneda sea igual a la
					// del sistema
					"AND C_Invoice.C_Currency_ID = @$C_Currency_ID@ "+
					// Notas de Crédito canjeables
					"AND C_Invoice.NotExchangeableCredit = 'N' "+
				    // Tipo de documento con signo -1 (débito para la compañía)
					"AND EXISTS (SELECT C_DocType_ID " +
				                 "FROM C_DocType dt " +
				                 "WHERE C_Invoice.C_DocType_ID = dt.C_DocType_ID " +
				                   "AND dt.Signo_IsSOTrx = -1)",
                   true, getPoSModel().addSecurityValidationToCN());
		bankLookup.getM_button().setVisible(getPoSModel().getPoSConfig().isAllowCreditNoteSearch());
		bankLookup.setShowInfo(getPoSModel().getPoSConfig().isAllowCreditNoteSearch());
		return bankLookup;
	}
}
