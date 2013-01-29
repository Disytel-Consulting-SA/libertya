package org.openXpertya.print.fiscal.hasar;

import java.math.BigDecimal;

import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.comm.FiscalComm;

public class HasarPrinterP321F extends HasarFiscalPrinter {

	public HasarPrinterP321F() {
		super();
	}
	
	/**
	 * @param fiscalComm
	 */
	public HasarPrinterP321F(FiscalComm fiscalComm) {
		super(fiscalComm);
	}

	public String formatAmount(BigDecimal amount) {
		amount = amount.setScale(4, BigDecimal.ROUND_HALF_UP);
		return amount.toString();
	}

	public String formatQuantity(BigDecimal quantity) {
		return quantity.toString();
	}

	@Override
	public FiscalPacket cmdSetGeneralConfiguration(boolean printConfigReport, boolean loadDefaultData, BigDecimal finalConsumerLimit, BigDecimal ticketInvoiceLimit, BigDecimal ivaNonInscript, Integer copies, Boolean printChange, Boolean printLabels, String ticketCutType, Boolean printFramework, Boolean reprintDocuments, String balanceText, Boolean paperSound, String paperSize) {
		return super.cmdSetGeneralConfiguration(printConfigReport, loadDefaultData,
				finalConsumerLimit, BigDecimal.ZERO, ivaNonInscript, copies,
				false, false, null, printFramework,
				reprintDocuments, balanceText, paperSound, paperSize);
	}
	
	@Override
	public FiscalPacket cmdPrintFiscalText(String text, Integer display) {
		// El parámetro display no tiene utilidad en este modelo.
		// Siempre se asigna a 0.
		return super.cmdPrintFiscalText(text, 0);
	}
	
	@Override
	public FiscalPacket cmdPrintLineItem(String description, BigDecimal quantity, BigDecimal price, BigDecimal ivaPercent, boolean substract, BigDecimal internalTaxes, boolean basePrice, Integer display) {
		// El parámetro display no tiene utilidad en este modelo.
		// Siempre se asigna a 0.
		return super.cmdPrintLineItem(description, quantity, price, ivaPercent,
				substract, internalTaxes, basePrice, 0);
	}

	@Override
	public FiscalPacket cmdLastItemDiscount(String description, BigDecimal amount, boolean substract, boolean baseAmount, Integer display) {
		// El parámetro display no tiene utilidad en este modelo.
		// Siempre se asigna a 0.
		return super.cmdLastItemDiscount(description, amount, substract, baseAmount, 0);
	}

	@Override
	public FiscalPacket cmdSubtotal(boolean print, Integer display) {
		// El parámetro display no tiene utilidad en este modelo.
		// Siempre se asigna a 0.
		return super.cmdSubtotal(print, 0);
	}

	@Override
	public FiscalPacket cmdTotalTender(String description, BigDecimal amount, boolean cancel, Integer display) {
		// El parámetro display no tiene utilidad en este modelo.
		// Siempre se asigna a 0.
		return super.cmdTotalTender(description, amount, cancel, 0);
	}
	
	@Override
	public FiscalPacket cmdCloseFiscalReceipt(Integer copies) {
		// Este modelo no acepta el parámetro de copias.
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_FISCAL_RECEIPT);
		return cmd; 
	}
	
	@Override
	public FiscalPacket cmdCloseDNFH(Integer copies) {
		// Este modelo no acepta el parámetro de copias.
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_DNFH);
		return cmd; 
	}
	
	@Override
	protected FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display,
			String operation, int descMaxLength) {
		// El parámetro display no tiene utilidad en este modelo.
		// Siempre se asigna a 0.
		return super.cmdReturnRecharge(description, amount, ivaPercent, subtract,
				internalTaxes, baseAmount, 0, operation, descMaxLength);
	}

	@Override
	public FiscalPacket cmdGeneralDiscount(String description,
			BigDecimal amount, boolean substract, boolean baseAmount,
			Integer display) {
		// El parámetro display no tiene utilidad en este modelo.
		// Siempre se asigna a 0.
		return super.cmdGeneralDiscount(description, amount, substract, baseAmount,
				0);
	}
}
