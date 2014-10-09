package org.openXpertya.print.fiscal.hasar;

import java.math.BigDecimal;

import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.comm.FiscalComm;

public class HasarPrinterP435F extends HasarFiscalPrinter {

	public HasarPrinterP435F() {
		// TODO Auto-generated constructor stub
	}

	public HasarPrinterP435F(FiscalComm fiscalComm) {
		super(fiscalComm);
		// TODO Auto-generated constructor stub
	}

	public String formatAmount(BigDecimal amount) {
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
		return amount.toString();
	}

	public String formatQuantity(BigDecimal quantity) {
		return quantity.toString();
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
	
	@Override
	public FiscalPacket cmdPrintFiscalText(String text, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_FISCAL_TEXT);
		int i = 1;
		cmd.setText(i++, text, 31, false);
		cmd.setNumber(i++, display == null?0:display, true);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdPrintLineItem(String description,
			BigDecimal quantity, BigDecimal price, BigDecimal ivaPercent,
			boolean substract, BigDecimal internalTaxes, boolean basePrice,
			Integer display) {
		// Force display 0 and maxlength 23 to comply with model docs.
		return cmdPrintLineItem(description, quantity, price, ivaPercent,
				substract, internalTaxes, basePrice, 0, 23);
	}
	
	@Override
	public FiscalPacket cmdPrintNonFiscalText(String text, Integer display) {
		FiscalPacket cmd = super.cmdPrintNonFiscalText(text, display);
		cmd.setText(1, text, 45, false);
		return cmd;
	}
	
	public FiscalPacket cmdSetCustomerData(String name,
			String customerDocNumber, String ivaResponsibility, String docType,
			String location) {
		FiscalPacket cmd = super.cmdSetCustomerData(name, customerDocNumber,
				ivaResponsibility, docType, location);
		cmd.setText(1, name, 45, true);
		cmd.setText(5, location, 45, true);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdTotalTender(String description, BigDecimal amount, boolean cancel, Integer display) {
		FiscalPacket cmd = super.cmdTotalTender(description, amount, cancel, 0);
		cmd.setText(1, description, 28, false);
		return cmd;
	}
	
	@Override
	protected FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display,
			String operation, int descMaxLength) {
		descMaxLength = 23;
		return super.cmdReturnRecharge(description, amount, ivaPercent, subtract,
				internalTaxes, baseAmount, 0, operation, descMaxLength);
	}
	
	@Override
	public FiscalPacket cmdOpenDrawer(){
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_DRAWER);
		return cmd;
	}
}
