package org.openXpertya.cc;

import java.math.BigDecimal;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrg;

public abstract class CurrentAccountBalanceData {

	// Variables de instancia
	
	/** Saldo (invoiceopen de todas las facturas a crédito - pagos anticipados) */
	
	private BigDecimal balance;
	
	/** Crédito usado (invoiceopen de las facturas a cliente a crédito) */
	
	private BigDecimal creditUsed;
	
	/** Suma de los totales de las facturas a crédito */
	
	private BigDecimal actualLifeTimeValue; 
		
	/** Forma de pago */
	
	private String paymentRule;
	
	/** Entidad Comercial */
	
	private MBPartner bpartner;

	/**
	 * Obtengo la instancia a partir del tipo de medio de pago y entidad
	 * comercial.
	 * 
	 * @param org
	 *            organización
	 * @param bp
	 *            entidad comercial
	 * @param paymentRule
	 *            tipo de medio de pago
	 * @return la instancia correspondiente o null caso que no se adecue el tipo
	 *         de medio de pago a ninguna gestión de crédito
	 */
	public static CurrentAccountBalanceData getInstance(MOrg org, MBPartner bp, String paymentRule){
		CurrentAccountBalanceData balanceData = null;
		// A Crédito
		if(paymentRule.equals(MInvoice.PAYMENTRULE_OnCredit)){
			balanceData = new OnCreditCurrentAccountBalanceData(paymentRule);
		}
		// Si es distinto de null, le seteo el bpartner
		if(balanceData != null){
			balanceData.setBpartner(bp);
		}
		return balanceData;
	}
	
	// Constructores
	
	protected CurrentAccountBalanceData(String paymentRule) {
		setPaymentRule(paymentRule);
		setActualLifeTimeValue(BigDecimal.ZERO);
		setBalance(BigDecimal.ZERO);
		setCreditUsed(BigDecimal.ZERO);
	}
	
	/*
	 * ****************************************************
	 * 				MÉTODOS ABSTRACTOS
	 * ****************************************************
	 */

	/**
	 * Cada subclase debe carga la información de saldo, crédito usado, etc.
	 * dependiendo de la forma de pago y entidad comercial
	 */
	public abstract void loadBalanceData();

	/*
	 * ****************************************************
	 */
	
	// Getters y Setters
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setCreditUsed(BigDecimal creditUsed) {
		this.creditUsed = creditUsed;
	}

	public BigDecimal getCreditUsed() {
		return creditUsed;
	}

	public void setActualLifeTimeValue(BigDecimal actualLifeTimeValue) {
		this.actualLifeTimeValue = actualLifeTimeValue;
	}

	public BigDecimal getActualLifeTimeValue() {
		return actualLifeTimeValue;
	}

	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}


	public String getPaymentRule() {
		return paymentRule;
	}


	public void setBpartner(MBPartner bpartner) {
		this.bpartner = bpartner;
	}


	public MBPartner getBpartner() {
		return bpartner;
	}
}
