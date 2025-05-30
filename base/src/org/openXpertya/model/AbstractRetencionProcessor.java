package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public abstract class AbstractRetencionProcessor implements RetencionProcessor {

	/** Logger */
	protected CLogger log = CLogger.getCLogger(SvrProcess.class);
	/** Esquema de retención utilizado para obtener el monto a retener. */
	private MRetencionSchema m_Esquema = null;
	/** Nombre del Tipo de la retención */
	private String retencionTypeName = null;
	/** Nombre de la transacción en la que se realizan las operaciones. */
	private String m_trxName = "";
	/** Compañía actual */
	private int m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
	/** Fecha de la transacción. Utilizada para el cálculo de excepciones. */
	private Timestamp dateTrx = Env.getContextAsDate(Env.getCtx(), "#Date");
	/** Orden de Pago */
	private MAllocationHdr m_Alloc = null;
	/** Entidad Comercial a la que se le aplica la retención */
	private MBPartner m_C_BPartner = null;
	/** Moneda en la que se realizan los cálculos. */
	private MCurrency m_currency;
	/** Importe de la retención */
	protected BigDecimal amount;
	/** [EP] Importe total de/l pago/s. */
	private BigDecimal payTotalAmount = Env.ZERO;
	/** Nro. de Retención manual */
	private String retencionNumber;
	/** Proyecto */
	private Integer projectID = 0;
	/** Campaña */
	private Integer campaignID = 0;
	
	/** Lista de facturas pagadas */
	private List<MInvoice> m_List_Invoice = new ArrayList<MInvoice>();
	/** Lista de importes pagados de cada factura */
	private List<BigDecimal> m_List_Amount = new ArrayList<BigDecimal>();

	/**
	 * Indicador de Trx de venta. Los comprobantes de retenciones serán
	 * generados con este valor de IsSOTrx
	 */
	private boolean isSOTrx = true;

	/**
	 * Indica si es necesario recalcular el monto de la retención dado que se
	 * han modificado algunas de sus configuraciones.
	 */
	private boolean recalculateAmount = true;
	
	/** Forma de Pago de los comprobantes a crear */
	private String paymentRule;

	public void setRetencionSchema(MRetencionSchema schema) {
		this.m_Esquema = schema;
	}

	public MRetencionSchema getRetencionSchema() {
		return m_Esquema;
	}

	public void setTrxName(String trxName) {
		m_trxName = trxName;
	}

	public String getTrxName() {
		return m_trxName;
	}

	public void setAllocationHrd(MAllocationHdr allocHdr) {
		this.m_Alloc = allocHdr;
	}

	public MAllocationHdr getAllocationHrd() {
		return m_Alloc;
	}

	public void setBPartner(MBPartner bPartner) {
		this.m_C_BPartner = bPartner;
		recalculateAmount = true;
	}

	public MBPartner getBPartner() {
		return m_C_BPartner;
	}

	/**
	 * @return Retorna el ID de la compañía actual.
	 */
	protected int getAD_Client_ID() {
		return m_AD_Client_ID;
	}

	public void setCurrency(MCurrency currency) {
		this.m_currency = currency;
		recalculateAmount = true;
	}

	public MCurrency getCurrency() {
		return m_currency;
	}

	/**
	 * @return Retorna la lista de facturas pagadas.
	 */
	public List<MInvoice> getInvoiceList() {
		return m_List_Invoice;
	}

	/**
	 * @return Retorna la lista de importes pagados por factura.
	 */
	public List<BigDecimal> getAmountList() {
		return m_List_Amount;
	}
	
	/**
	 * Setea la lista de facturas
	 * @param invoices nueva lista de facturas
	 */
	public void setInvoiceList(List<MInvoice> invoices) {
		m_List_Invoice = invoices;
	}

	/**
	 * Setea la lista de importes 
	 * @param amts nueva lista de importes
	 */
	public void setAmountList(List<BigDecimal> amts) {
		m_List_Amount = amts;
	}

	/**
	 * @return Returns the dateTrx.
	 */
	public Timestamp getDateTrx() {
		return dateTrx;
	}

	public BigDecimal getAmount() {
		// Las retenciones sufridas no deben ser calculadas.
		if (!getRetencionSchema().isSufferedRetencion()
				&& (amount == null || amount.compareTo(BigDecimal.ZERO) == 0 || isRecalculateAmount())) {
			// Calcula el monto de la retención.
			BigDecimal amt = calculateAmount();
			// Calcula las excepciones.
			amt = calculateExceptions(amt);
			// Se indica que no se debe recalcular el monto.
			setRecalculateAmount(false);
			// Se asigna el nuevo monto de la retencion.
			this.amount = amt;
		}
		return amount;
	}

	/**
	 * Realiza el cálculo de la retención.
	 * 
	 * @return Un <code>BigDecimal</code> con el importe de la retención.
	 */
	protected abstract BigDecimal calculateAmount();

	/**
	 * Asigna el monto de la retención.
	 */
	protected void setAmmount(BigDecimal ammount) {
		this.amount = ammount;
	}

	/**
	 * @param dateTrx
	 *            The dateTrx to set.
	 */
	public void setDateTrx(Timestamp dateTrx) {
		this.dateTrx = dateTrx;
		recalculateAmount = true;
	}

	public String getRetencionTypeName() {
		// Obtiene el nombre del tipo de la retención a partir del esquema
		// de retención que se utiliza para este cálculo.
		if (retencionTypeName == null) {
			X_C_RetencionType vTipo = new X_C_RetencionType(Env.getCtx(),
					getRetencionSchema().getC_RetencionType_ID(), getTrxName());
			retencionTypeName = vTipo.getName();
		}
		return retencionTypeName;
	}

	public String getRetencionSchemaName() {
		return getRetencionSchema().getName();
	}

	/**
	 * Obtiene el valor de un parámetro del esquema convertido a
	 * <code>String</code>
	 * 
	 * @param paramName
	 *            Nombre del parámetro a buscar.
	 * @return Retorna el valor del parámetro en caso de existir,
	 *         <code>null</code> en caso contrario.
	 */
	protected String getParamValueString(String paramName) {
		return getParamValueString(paramName, null);
	}

	/**
	 * Obtiene el valor de un parámetro del esquema convertido a
	 * <code>String</code>
	 * 
	 * @param paramName
	 *            Nombre del parámetro a buscar.
	 * @param defaultValue
	 *            Valor retornado en caso de que el <code>paramName</code> no
	 *            exista en el esquema.
	 * @return Retorna el valor del parámetro en caso de existir,
	 *         <code>defaultValue</code> en caso contrario.
	 */
	protected String getParamValueString(String paramName, String defaultValue) {
		return getParamString(paramName, defaultValue, null);
	}

	/**
	 * Obtiene el valor de un parámetro del esquema convertido a
	 * <code>BigDecimal</code>
	 * 
	 * @param paramName
	 *            Nombre del parámetro a buscar.
	 * @return Retorna el valor del parámetro en caso de existir,
	 *         <code>null</code> en caso contrario.
	 */
	protected BigDecimal getParamValueBigDecimal(String paramName) {
		return getParamValueBigDecimal(paramName, null);
	}

	/**
	 * Obtiene el valor de un parámetro del esquema convertido a
	 * <code>BigDecimal</code>
	 * 
	 * @param paramName
	 *            Nombre del parámetro a buscar.
	 * @param defaultValue
	 *            Valor retornado en caso de que el <code>paramName</code> no
	 *            exista en el esquema.
	 * @return Retorna el valor del parámetro en caso de existir,
	 *         <code>defaultValue</code> en caso contrario.
	 */
	protected BigDecimal getParamValueBigDecimal(String paramName,
			BigDecimal defaultValue) {
		return getParamBigDecimal(paramName, defaultValue, null);
	}

	
	protected String getParamString(String paramName, String defaultValue, BigDecimal baseImponible){
		return (String)getParamValue(paramName, defaultValue, baseImponible);
	}
	
	protected BigDecimal getParamBigDecimal(String paramName, BigDecimal defaultValue, BigDecimal baseImponible){
		Object value = getParamValue(paramName, defaultValue, baseImponible);
		BigDecimal valueDecimal = null;
		if(value != null){
			if(value instanceof BigDecimal){
				valueDecimal = (BigDecimal)value;
			}
			else{
				valueDecimal = new BigDecimal(((String)value).replaceAll(",", "."));
			}
		}
		return valueDecimal;
	}
	
	
	protected Object getParamValue(String paramName, Object defaultValue, BigDecimal baseImponible){
		Object value = null;
		// Obtiene el MRetSchemaConfig con el nombre de parámetro pasado.
		MRetSchemaConfig param = getRetencionSchema().getParameter(paramName);
		// Si hay configuración...
		if (param != null) {
			// Si es rango busco el rango que incluye al monto, sino obtengo el
			// valor del parámetro
			if(param.is_Range()){
				value = MRetSchemaConfig.getRangeApplyValue(param.getID(),
						baseImponible, getTrxName());
			}
			else{
				value = param.getValor();
			}
		}
		// Se retorna el valor o el defaultValue en caso de que el value sea
		// null.
		return (value == null ? defaultValue : value);
	}
	
	protected List<String> getPadronTypesParamsValues(){
		List<MRetSchemaConfig> params = getRetencionSchema().getPadronesList();
		List<String> padrones = new ArrayList<String>();
		if (params == null)
			return padrones;
		for (MRetSchemaConfig paramPadron : params) {
			padrones.add(paramPadron.getPadronType());
		}
		return padrones;
	}
	
	/**
	 * Re calcula el monto de la retención aplicando descuentos de porcentajes
	 * de excepción asociados a la EC.
	 */
	protected BigDecimal calculateExceptions(BigDecimal ammount) {
		BigDecimal newAmt = ammount;
		// Se obtiene el porcentaje de excepción que tiene la EC.
		BigDecimal exceptionPercent = getBPartner().getRetencionExenPercent(
				getRetencionSchema().getC_RetencionSchema_ID(), getDateTrx());

		// Si el porcentaje es mayor que cero entonces se reduce el monto de la
		// retención
		// acorde a este porcentaje.
		if (exceptionPercent.compareTo(Env.ZERO) > 0) {
			BigDecimal rate = Env.ONE.subtract(exceptionPercent
					.divide(Env.ONEHUNDRED, 2, BigDecimal.ROUND_HALF_EVEN));
			newAmt = ammount.multiply(rate);
		}

		return newAmt;
	}

	public boolean clearAll() {
		// Limpia la lista de facturas pagadas junto con la de importes.
		getInvoiceList().clear();
		getAmountList().clear();
		setAmmount(null);
		setPayTotalAmount(Env.ZERO);
		recalculateAmount = true;
		return true;
	}

	public void addInvoice(MInvoice inv, BigDecimal payamt) throws Exception {
		// Existe la factura, el pago se para dicha factura.
		if (inv != null) {
			// Se agrega la factura y el pago a las listas.
			getInvoiceList().add(inv);
			getAmountList().add(payamt);
			addPayAmount(payamt, inv.getC_Currency_ID());
		} else {
			getAmountList().add(payamt);
			addPayAmount(payamt);
		}
	} // addInvoice

	/**
	 * Suma un importe de pago al importe total del pago actual.
	 * 
	 * @param payamt
	 *            Importe a sumar.
	 * @param currencyID
	 *            Moneda en la que se ecnuentra el importe.
	 * @throws Exception
	 *             cuando se produce algún error en el intento de conversión de
	 *             la moneda del pago.
	 */
	protected void addPayAmount(BigDecimal payamt, int currencyID)
			throws Exception {
		// Si la moneda del pago es diferente a la moneda en que se calcula la
		// retención
		// se convierte el monto del pago hacia esta moneda.
		if (getCurrency().getC_Currency_ID() != currencyID)
			payamt = currencyConvert(payamt, getCurrency().getC_Currency_ID(),
					currencyID, getDateTrx());

		// Se suma el importe al total.
		setPayTotalAmount(getPayTotalAmount().add(payamt));
	}

	/**
	 * Suma un importe de pago al importe total del pago actual.
	 * 
	 * @param payamt
	 *            Importe a sumar.
	 * @throws Exception
	 *             cuando se produce algún error en el intento de conversión de
	 *             la moneda del pago.
	 */
	protected void addPayAmount(BigDecimal amt) throws Exception {
		addPayAmount(amt, getCurrency().getC_Currency_ID());
	}

	/**
	 * @param payTotalAmount
	 *            The payTotalAmount to set.
	 */
	protected void setPayTotalAmount(BigDecimal payTotalAmount) {
		this.payTotalAmount = payTotalAmount;
	}

	/**
	 * @return Returns the payTotalAmount.
	 */
	public BigDecimal getPayTotalAmount() {
		return payTotalAmount;
	}

	protected BigDecimal currencyConvert(BigDecimal fromAmt, int fromCurency,
			int toCurrency, Timestamp convDate) throws Exception {
		// return Currency.convert(fromAmt, fromCurency, toCurrency, convDate,
		// 0, Env.getAD_Client_ID(m_ctx), Env.getAD_Org_ID(m_ctx));

		// HACK: Can't invoke Currency.convert directly !!

		CPreparedStatement pp = DB
				.prepareStatement(" SELECT currencyConvert(?, ?, ?, ?, ?, ?, ?) ");

		try {
			pp.setBigDecimal(1, fromAmt);
			pp.setInt(2, fromCurency);
			if (toCurrency != 0) {
				pp.setInt(3, toCurrency);
			} else {
				pp.setInt(3, fromCurency);
			}
			pp.setTimestamp(4, (java.sql.Timestamp) convDate);
			pp.setInt(5, 0);
			pp.setInt(6, Env.getAD_Client_ID(Env.getCtx()));
			pp.setInt(7, Env.getAD_Org_ID(Env.getCtx()));

			ResultSet rs = pp.executeQuery();

			if (rs.next())
				return rs.getBigDecimal(1);
			
			// dREHER cierre de conexion
			DB.close(rs, pp);

		} catch (Exception e) {
			log.log(Level.SEVERE, "currencyConvert", e);
		}

		return null;
	}

	/**
	 * @return Returns the recalculateAmount.
	 */
	protected boolean isRecalculateAmount() {
		return recalculateAmount;
	}

	/**
	 * @param recalculateAmount
	 *            The recalculateAmount to set.
	 */
	protected void setRecalculateAmount(boolean recalculateAmount) {
		this.recalculateAmount = recalculateAmount;
	}

	public void setAmount(BigDecimal amount) {
		if (getRetencionSchema().isSufferedRetencion())
			this.amount = amount;
	}

	/**
	 * @return the retencionNumber
	 */
	public String getRetencionNumber() {
		return retencionNumber;
	}

	/**
	 * @param retencionNumber
	 *            the retencionNumber to set
	 */
	public void setRetencionNumber(String retencionNumber) {
		this.retencionNumber = retencionNumber;
	}

	@Override
	public String toString() {
		return getRetencionSchemaName() + " " + getAmount().setScale(2);
	}

	public boolean isSOTrx() {
		return this.isSOTrx;
	}

	/**
	 * @param isSOTrx
	 *            the isSOTrx to set
	 */
	public void setIsSOTrx(boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}

	/*
	 * public BigDecimal getPayTotalAmountByLetra(String letra, boolean
	 * excluye){ BigDecimal total = Env.ZERO; List<MInvoice> invoices =
	 * getInvoiceList(); for(int i=0;i<invoices.size();i++){ int
	 * C_LetraComprobante_ID = invoices.get(i).getC_Letra_Comprobante_ID();
	 * if(C_LetraComprobante_ID != 0){ MLetraComprobante letracomp = new
	 * MLetraComprobante(Env.getCtx(),C_LetraComprobante_ID,getTrxName());
	 * if(excluye){ if(!letracomp.getLetra().equals(letra)) total =
	 * total.add(getAmountList().get(i)); } else{//incluye
	 * if(letracomp.getLetra().equals(letra)) total =
	 * total.add(getAmountList().get(i)); } } else//sin letra, se agrega de
	 * todos modos total = total.add(getAmountList().get(i)); } return total; }
	 */

	public BigDecimal getInvoicesTaxesAmount() {
		List<MInvoice> invoices = getInvoiceList();
		BigDecimal amount = Env.ZERO;
		for (int i = 0; i < invoices.size(); i++) {
			MInvoiceLine[] lines = invoices.get(i).getLines();
			for (int j = 0; j < lines.length; j++) {
				amount = amount.add(lines[j].getTaxAmt());
			}
		}
		return amount;
	}

	/**
	 * Obtener el neto del pago de la factura
	 * 
	 * @param invoice
	 *            factura
	 * @param amt
	 *            importe pago
	 * @return neto del pago
	 */
	public BigDecimal getPayNetAmt(MInvoice invoice, BigDecimal amt){
		BigDecimal net = invoice.getNetAmount();
		BigDecimal grandTotal = invoice.getGrandTotal();
		// Decrementar los impuestos manuales
		/*
		BigDecimal manualTaxesAmt = BigDecimal.ZERO;
		try{
			List<MInvoiceTax> manualTaxes = MInvoiceTax.getTaxesFromInvoice(invoice, true);
			for (MInvoiceTax mInvoiceTax : manualTaxes) {
				manualTaxesAmt = manualTaxesAmt.add(mInvoiceTax.getTaxAmt());
			}
			grandTotal = grandTotal.subtract(manualTaxesAmt);
		} catch(Exception e){
			e.printStackTrace();
		}
		amt = amt.subtract(manualTaxesAmt);
		*/
		return net.multiply(amt).divide(grandTotal, 2, BigDecimal.ROUND_HALF_EVEN);
	}
	
	/**
	 * Obtengo el neto de los montos para cada factura. Los montos y las
	 * facturas parámetros deben ir en orden.
	 * 
	 * @param invoices
	 *            facturas
	 * @param amounts
	 *            montos imputados a cada factura
	 * @return total neto de los montos de las facturas
	 */
	public BigDecimal getPayNetAmt(List<MInvoice> invoices,
			List<BigDecimal> amounts) {
		BigDecimal netTotal = Env.ZERO;
		Integer invoicesSize = invoices.size();
		Integer amountsSize = amounts.size();
		if(invoicesSize == 0 && amountsSize > 0){
			// Itero por los montos ya que no existen facturas, pero sí montos
			for (BigDecimal amount : amounts) {
				// FIXME el monto debería ser el neto del pago
				netTotal = netTotal.add(amount);
			}
		}
		else{
			for (int i = 0; i < invoices.size(); i++) {
				netTotal = netTotal.add(getPayNetAmt(invoices.get(i),amounts.get(i)));
			}
		}
		return netTotal;
	}
	
	public BigDecimal getPayNetAmt() {
		return getPayNetAmt(getInvoiceList(), getAmountList());
	}
	
	/**
	 * Obtener el importe de impuestos manuales de las facturas
	 * 
	 * @param invoice
	 *            factura
	 * @param amt
	 *            importe pago
	 * @return neto del pago
	 */
	public BigDecimal getManualTaxesAmt(MInvoice invoice, BigDecimal amt){
		BigDecimal grandTotal = invoice.getGrandTotal();
		// Decrementar los impuestos manuales
		BigDecimal manualTaxesAmt = BigDecimal.ZERO;
		try{
			List<MInvoiceTax> manualTaxes = MInvoiceTax.getTaxesFromInvoice(invoice, true);
			for (MInvoiceTax mInvoiceTax : manualTaxes) {
				manualTaxesAmt = manualTaxesAmt.add(mInvoiceTax.getTaxAmt());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return manualTaxesAmt.multiply(amt).divide(grandTotal, 2, BigDecimal.ROUND_HALF_EVEN);
	}
	
	/**
	 * Obtengo el neto de los montos para cada factura. Los montos y las
	 * facturas parámetros deben ir en orden.
	 * 
	 * @param invoices
	 *            facturas
	 * @param amounts
	 *            montos imputados a cada factura
	 * @return total neto de los montos de las facturas
	 */
	public BigDecimal getManualTaxesAmt(List<MInvoice> invoices, List<BigDecimal> amounts) {
		BigDecimal manualTaxesAmt = Env.ZERO;
		if(invoices.size() > 0){
			for (int i = 0; i < invoices.size(); i++) {
				manualTaxesAmt = manualTaxesAmt.add(getManualTaxesAmt(invoices.get(i),amounts.get(i)));
			}
		}
		return manualTaxesAmt;
	}
	
	public BigDecimal getManualTaxesAmt() {
		return getManualTaxesAmt(getInvoiceList(), getAmountList());
	}
	
	public Map<MInvoice, BigDecimal> getAllocatedInvoicesAmts(
			String allocationLineCreditColumn, Integer creditID, 
			boolean calculateRetencionPorcentaje, boolean filterByApplyRetencionFlag)
			throws Exception {
		Map<MInvoice, BigDecimal> allocatedAmts = new HashMap<MInvoice, BigDecimal>();
		// Query que permite obtener las facturas (débitos) y el monto imputado
		// del crédito parámetro
		// TODO: conversión del monto imputado a la moneda del crédito, la fecha
		// de conversión es la fecha del crédito o la de imputación?
		BigDecimal amountRet = calculateRetencionPorcentaje
				? getRetencionPorcentaje(allocationLineCreditColumn, creditID) : BigDecimal.ZERO;
		String sql = "SELECT DISTINCT c_allocationline_id, al.c_invoice_id, amount "
				+ "FROM c_allocationhdr as ah "
				+ "INNER JOIN c_allocationline as al ON al.c_allocationhdr_id = ah.c_allocationhdr_id "
				+ "INNER JOIN c_invoice as i on i.c_invoice_id = al.c_invoice_id "
				+ "INNER JOIN c_doctype as dt on dt.c_doctype_id = i.c_doctype_id "
				+ "WHERE ah.isactive = 'Y' "
				+ "AND dt.applyretention = 'Y' "
				+ "AND ah.docstatus in ('CO','CL') AND al."
				+ allocationLineCreditColumn
				+ " = "
				+ creditID
				+ " AND allocationtype <> 'OPA'";
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			allocatedAmts.put(
					new MInvoice(Env.getCtx(), rs.getInt("c_invoice_id"),
							getTrxName()), rs.getBigDecimal("amount").add(amountRet));
		}
		return allocatedAmts;
	}	
	
	public Map<MInvoice, BigDecimal> getAllocatedInvoicesAmts(
			String allocationLineCreditColumn, Integer creditID, 
			boolean calculateRetencionPorcentaje)
			throws Exception {
		Map<MInvoice, BigDecimal> allocatedAmts = new HashMap<MInvoice, BigDecimal>();
		// Query que permite obtener las facturas (débitos) y el monto imputado
		// del crédito parámetro
		// TODO: conversión del monto imputado a la moneda del crédito, la fecha
		// de conversión es la fecha del crédito o la de imputación?
		BigDecimal amountRet = calculateRetencionPorcentaje
				? getRetencionPorcentaje(allocationLineCreditColumn, creditID) : BigDecimal.ZERO;
		String sql = "SELECT DISTINCT c_allocationline_id, al.c_invoice_id, amount "
				+ "FROM c_allocationhdr as ah "
				+ "INNER JOIN c_allocationline as al ON al.c_allocationhdr_id = ah.c_allocationhdr_id "
				+ "WHERE ah.isactive = 'Y' "
				+ "AND ah.docstatus in ('CO','CL') AND al."
				+ allocationLineCreditColumn
				+ " = "
				+ creditID
				+ " AND allocationtype <> 'OPA'";
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			allocatedAmts.put(
					new MInvoice(Env.getCtx(), rs.getInt("c_invoice_id"),
							getTrxName()), rs.getBigDecimal("amount").add(amountRet));
		}
		
		// dREHER cierre de conexion
		DB.close(rs, ps);
		
		return allocatedAmts;
	}
	
	public BigDecimal getRetencionPorcentaje(String allocationLineCreditColumn, Integer creditID) throws SQLException{
		// de conversión es la fecha del crédito o la de imputación?
		String sql = "SELECT DISTINCT ah.c_allocationhdr_id "
				+ "FROM c_allocationhdr as ah "
				+ "INNER JOIN c_allocationline as al ON al.c_allocationhdr_id = ah.c_allocationhdr_id "
				+ "WHERE ah.isactive = 'Y' AND ah.docstatus in ('CO','CL') AND al."
				+ allocationLineCreditColumn
				+ " = "
				+ creditID;
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName());
		ResultSet rs = ps.executeQuery();
		int c_allocationhdr_id = 0;
		if (rs.next()) {
			c_allocationhdr_id = rs.getInt("c_allocationhdr_id");
		}
		
		sql = "	SELECT coalesce(( " +
			  " (SELECT SUM(amount) " +
			  " FROM C_AllocationLine al " +
			  "	INNER JOIN C_Invoice i ON (i.C_Invoice_ID = al.C_Invoice_Credit_ID) " +
			  "	WHERE C_AllocationHdr_ID = ? AND (i.C_Doctype_ID IN (SELECT C_Doctype_ID FROM C_Doctype WHERE doctypekey IN ('RTR', 'RCR', 'RCI', 'RTI')))" +
			  "	)/(" +
			  "	SELECT COUNT(c_allocationlINE_ID)" +
			  "	FROM C_AllocationLine al" +
			  "	LEFT JOIN C_Invoice i ON (i.C_Invoice_ID = al.C_Invoice_Credit_ID)" +
			  "	WHERE C_AllocationHdr_ID = ? AND ((i.C_Doctype_ID NOT IN (SELECT C_Doctype_ID FROM C_Doctype WHERE doctypekey IN ('RTR', 'RCR', 'RCI', 'RTI'))) OR (al.C_Invoice_Credit_ID IS NULL)))" +
			  "),0) AS amountRetention";
		ps = DB.prepareStatement(sql, getTrxName());
		int i = 1;
		ps.setInt(i++, c_allocationhdr_id);
		ps.setInt(i++, c_allocationhdr_id);
		rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getBigDecimal("amountRetention");
		}
		
		// dREHER cierre de conexion
		DB.close(rs, ps);
		
		return BigDecimal.ZERO;
	}
	
	/**
	 * Obtiene todos los C_BPartner_ID que tengan el mismo CUIT
	 * y sean del mismo TIPO 
	 * @param taxID
	 *            	CUIT
	 * @return ArrayList
	 * 				id de entidades comerciales coincidentes
	 * @author dREHER
	 */
	public ArrayList<Integer> getC_BPartner_IDMismoCUIT(String taxID){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		String sql = "SELECT C_BPartner_ID " +
				" FROM C_BPartner " +
				" WHERE IsActive='Y' " +
				" AND REPLACE(TaxID,'-','') = ?" +
				" AND IsVendor='Y'";

		PreparedStatement ps = DB.prepareStatement(sql, getTrxName());
		ResultSet rs = null;
		try {
			ps.setString(1, taxID.replace("-", ""));	
			rs = ps.executeQuery();
			while(rs.next()) {
				ids.add(rs.getInt("C_BPartner_ID"));
			}
		} catch (SQLException e) {
			log.warning(e.getLocalizedMessage());
		}finally {
			DB.close(rs, ps);
		}

		return ids;
	}
	
	/**
	 * 
	 * @param ids
	 * 			Identificadores para utilizar en clausula IN ...
	 * @return Clausula IN formateada
	 * @author dREHER
	 */
	public String getIN(ArrayList<Integer> ids) {
		String s = "(";
		
		for(Integer i : ids)
			s+= (s.length()>1?",":"") + i.toString();
		
		s+= ")";
		debug("Busqueda contemplando multiples CUIT's -> " + s);
		return s;
	}
 
	// dREHER salida por consola
	protected void debug(String string) {
		System.out.println("==> AbstractRetencionProcessor. " + string);
	}

	/**
	 * Calcula el importe total del retenciones realizadas en el mes al
	 * proveedor, Asigna el resultado al atributo
	 * <code>retencionesAnteriores</code>.
	 * 
	 * @return Retorna un <code>BigDecimal</code> con el importe total retenido.
	 * 
	 * dREHER se agrega metodo en la clase superior, para ser utilizada en los calculos
	 * de retenciones que lo requieran, si bien esta como parametro filtrarEsquema
	 * TODO: asegurar que SIEMPRE se filtre para no tomar cualquier tipo de retencion
	 */
	protected BigDecimal calculateRetencionesMensualAcumuladas(boolean filtrarEsquema) {
		Timestamp vFecha = Env.getContextAsDate(Env.getCtx(), "#Date");
		
		// dREHER leer la fecha de la OP y no la del dia
		if(getDateTrx()!=null)
			vFecha = getDateTrx();
				
		Timestamp vDesde = (Timestamp) DB.getSQLObject(getTrxName(),
						"select date_trunc('month',?::timestamp)",
						new Object[] { vFecha });
		
		
		// dREHER traigo los ID de todos los proveedores con el mismo CUIT
		String C_BParner_IDIN = getIN(getC_BPartner_IDMismoCUIT(getBPartner().getTaxID()));

		BigDecimal total = Env.ZERO;
		String sql;

		sql = " SELECT SUM(amt_retenc) as total "
				+ " FROM m_retencion_invoice mri ";
		
		if(filtrarEsquema)
			sql += " WHERE mri.c_retencionschema_id = ? AND ";
				
				
		sql +=	" EXISTS( SELECT c_invoice_id "
				+ "               FROM c_invoice ci "
				+ "               WHERE mri.c_invoice_id = ci.c_invoice_id AND "
				+ "                     c_bpartner_id IN " +	C_BParner_IDIN + " AND " // original c_bpartner_id=?
				+ "                     ci.DocStatus IN ('CO','CL') AND "
				+ "                     date_trunc('day',dateInvoiced) BETWEEN ?::date AND ?::date)";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			pstmt = DB.prepareStatement(sql, null, true);
			int i = 1;
			if(filtrarEsquema)
				pstmt.setInt(i++, getRetencionSchema().getID());
			
			// dREHER reemplazado para traer las retenciones acumuladas a este mismo CUIT de todos los proveedores que lo comparten
			// pstmt.setInt(i++, getBPartner().getC_BPartner_ID());
			pstmt.setTimestamp(i++, vDesde);
			pstmt.setTimestamp(i++, vFecha);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getBigDecimal("total") != null) {
					total = rs.getBigDecimal("total");
				}
			}
			if (pstmt != null)
				pstmt.close();
			if (rs != null)
				rs.close();

		} catch (Exception ex) {
			log.info("Error al buscar el total de retenciones acumuladas en el mes !!!! ");
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		
		return total;
	}

	

	/**
	 * Obtener la suma de los pagos adelantados anteriores dentro del rango de
	 * fechas parámetro. Los pagos adelantados son aquellos que no se encuentran
	 * en ningún allocation completa o cerrada o que se encuentran en
	 * allocations de tipo OPA (Orden de Pago adelantada)
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 */
	public Map<Integer, BigDecimal> getSumaPagosAdelantadosAnteriores(
			MBPartner bpartner, Integer clientID, Timestamp dateFrom,
			Timestamp dateTo, MRetencionSchema retSchema) throws Exception {
		
		// dREHER traigo los ID de todos los proveedores con el mismo CUIT
		String C_BParner_IDIN = getIN(getC_BPartner_IDMismoCUIT(bpartner.getTaxID()));
		
		
		Map<Integer, BigDecimal> pays = new HashMap<Integer, BigDecimal>();
		String sql = "SELECT DISTINCT p.c_payment_id, currencybase(p.payamt, p.c_currency_id, p.datetrx, p.ad_client_id, p.ad_org_id) as prepayamt "
				+ "FROM c_payment as p "
				+ "WHERE p.docstatus IN ('CO','CL') AND "
				+ "			p.c_bpartner_id IN " +	C_BParner_IDIN + " AND " 
				+ "			p.isreceipt = 'N' AND "
				+ "			p.AD_Client_ID = ? AND ";
		// Fecha desde
		if (dateFrom != null) {
			sql += "			p.DateTrx::date >= ?::date AND ";
		}
		// Fecha hasta
		if (dateTo != null) {
			sql += "			p.DateTrx::date <= ?::date AND ";
		}
		sql += "			(NOT EXISTS (SELECT al.c_payment_id "
				+ "							FROM c_allocationhdr AS ah "
				+ "							INNER JOIN c_allocationline AS al ON al.c_allocationhdr_id = ah.c_allocationhdr_id "
				+ "							WHERE ah.c_bpartner_id IN " + C_BParner_IDIN + " AND ah.isactive = 'Y' AND ah.docstatus in ('CO','CL') AND al.c_payment_id = p.c_payment_id) "
				+ "			OR EXISTS (SELECT c_payment_id "
				+ "						FROM c_allocationhdr as ah "
				+ "						INNER JOIN c_allocationline as al ON al.c_allocationhdr_id = ah.c_allocationhdr_id "
				+ "						WHERE ah.c_bpartner_id IN " + C_BParner_IDIN + " AND ah.isactive = 'Y' AND ah.docstatus in ('CO','CL') AND al.c_payment_id = p.c_payment_id AND allocationtype = 'OPA')) AND "
				+ "			NOT EXISTS(SELECT bpr.C_BPartner_Retencion_ID "
				+ "						FROM C_BPartner_Retencion bpr "
				+ "                  	INNER JOIN C_BPartner_Retexenc exc ON bpr.C_BPartner_Retencion_ID = exc.C_BPartner_Retencion_ID "
				+ "                  	WHERE bpr.C_RetencionSchema_ID = ? AND "
				+ "                    			bpr.C_BPartner_ID = p.C_BPartner_ID AND "
				+ "                        		bpr.IsActive = 'Y' AND "
				+ "                        		exc.IsActive = 'Y' AND "
				+ "                        		p.DateTrx BETWEEN exc.Date_From AND exc.Date_To)";
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName(), true);
		int i = 1;
		// Entidad Comercial
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Si el parámetro de compañía es null o 0 entonces tomo el de la
		// entidad comercial
		if (Util.isEmpty(clientID, true)) {
			clientID = bpartner.getAD_Client_ID();
		}
		// Compañía
		ps.setInt(i++, clientID);
		// Fecha desde
		if (dateFrom != null) {
			ps.setTimestamp(i++, dateFrom);
		}
		// Fecha hasta
		if (dateTo != null) {
			ps.setTimestamp(i++, dateTo);
		}
		
		// ps.setInt(i++, bpartner.getID());
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Esquema de retención en subconsulta
		ps.setInt(i++, retSchema.getID());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			pays.put(rs.getInt("c_payment_id"), rs.getBigDecimal("prepayamt"));
		}
		
		// dREHER cierre de conexion
		DB.close(rs, ps);
		
		return pays;
	}

	/**
	 * Obtener los pagos imputados en algún allocation que no sean del tipo OPA.
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 */
	public Map<Integer, BigDecimal> getSumaPagosImputadosAnteriores(
			MBPartner bpartner, Integer clientID, Timestamp dateFrom,
			Timestamp dateTo, MRetencionSchema retSchema) throws Exception {
		Map<Integer, BigDecimal> pays = new HashMap<Integer, BigDecimal>();
		
		// dREHER traigo los ID de todos los proveedores con el mismo CUIT
		String C_BParner_IDIN = getIN(getC_BPartner_IDMismoCUIT(bpartner.getTaxID()));
		
		String sql = "SELECT DISTINCT p.c_payment_id, p.isallocated "
				+ "FROM c_payment p  "
				+ "WHERE p.docstatus in ('CO','CL') AND "
				+ "			p.c_bpartner_id IN " + C_BParner_IDIN + " AND " 
				+ "			p.isreceipt = 'N' AND "
				+ "			p.AD_Client_ID = ? AND ";
		// Fecha desde
		if (dateFrom != null) {
			sql += "			p.DateTrx::date >= ?::date AND ";
		}
		// Fecha hasta
		if (dateTo != null) {
			sql += "			p.DateTrx::date <= ?::date AND ";
		}
		sql += "				p.c_payment_id IN (SELECT al.c_payment_id "
				+ "							FROM c_allocationhdr as ah "
				+ "							INNER JOIN c_allocationline as al ON al.c_allocationhdr_id = ah.c_allocationhdr_id "
				+ "							INNER JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id "
				+ "							INNER JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id "	
				+ "							WHERE ah.c_bpartner_id IN " + C_BParner_IDIN + " AND ah.isactive = 'Y' AND ah.docstatus in ('CO','CL') AND al.c_payment_id = p.c_payment_id AND allocationtype <> 'OPA' AND dt.applyretention = 'Y') AND "
				+ "			NOT EXISTS(SELECT bpr.C_BPartner_Retencion_ID "
				+ "						FROM C_BPartner_Retencion bpr "
				+ "		                INNER JOIN C_BPartner_Retexenc exc ON bpr.C_BPartner_Retencion_ID = exc.C_BPartner_Retencion_ID "
				+ "		                WHERE bpr.C_RetencionSchema_ID = ? AND "
				+ "                    			bpr.C_BPartner_ID = p.C_BPartner_ID AND "
				+ "                        		bpr.IsActive = 'Y' AND "
				+ "                        		exc.IsActive = 'Y' AND "
				+ "                        		p.DateTrx BETWEEN exc.Date_From AND exc.Date_To)";
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName(), true);
		
		int i = 1;
		// Entidad Comercial
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		
		// Si el parámetro de compañía es null o 0 entonces tomo el de la
		// entidad comercial
		if (Util.isEmpty(clientID, true)) {
			clientID = bpartner.getAD_Client_ID();
		}
		// Compañía
		ps.setInt(i++, clientID);
		// Fecha desde
		if (dateFrom != null) {
			ps.setTimestamp(i++, dateFrom);
		}
		// Fecha hasta
		if (dateTo != null) {
			ps.setTimestamp(i++, dateTo);
		}
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Esquema de retención en la subconsulta
		ps.setInt(i++, retSchema.getID());
		ResultSet rs = ps.executeQuery();
		Map<MInvoice, BigDecimal> allocatedAmts;
		BigDecimal netTotal;
		Integer paymentID;
		while (rs.next()) {
			paymentID = rs.getInt("c_payment_id");
			// Determinar las facturas imputadas al pago y obtener su neto
			allocatedAmts = getAllocatedInvoicesAmts("c_payment_id", paymentID, false, true);
			netTotal = getPayNetAmt(
					new ArrayList<MInvoice>(allocatedAmts.keySet()),
					new ArrayList<BigDecimal>(allocatedAmts.values()));
			// Si no está completamente imputado el pago entonces el residual a
			// imputar del pago se toma total
			if (rs.getString("isallocated").equals("N")) {
				netTotal = netTotal.add(DB.getSQLValueBD(getTrxName(),
						"select paymentavailable(?)", paymentID));
			}
			pays.put(paymentID, netTotal);
		}
		
		// dREHER cierre de conexion
		DB.close(rs, ps);
		
		return pays;
	}

	/**
	 * Obtener la suma de los cashlines adelantados anteriores dentro del rango
	 * de fechas parámetro. Los cashlines adelantados se determinan por no estar
	 * en ningún allocation completo o cerrado o están en algún allocation de
	 * tipo OPA
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 */
	public Map<Integer, BigDecimal> getSumaCashLinesAdelantadosAnteriores(
			MBPartner bpartner, Integer clientID, Timestamp dateFrom,
			Timestamp dateTo, MRetencionSchema retSchema) throws Exception {
		Map<Integer, BigDecimal> cashlines = new HashMap<Integer, BigDecimal>();
		
		// dREHER traigo los ID de todos los proveedores con el mismo CUIT
		String C_BParner_IDIN = getIN(getC_BPartner_IDMismoCUIT(bpartner.getTaxID()));
		
		String sql = "SELECT DISTINCT cl.c_cashline_id, currencybase(abs(cl.amount), cl.c_currency_id, c.statementdate, cl.ad_client_id, cl.ad_org_id) as precashlineamt "
				+ "FROM c_cashline as cl "
				+ "INNER JOIN c_cash as c ON c.c_cash_id = cl.c_cash_id "
				+ "WHERE cl.docstatus IN ('CO','CL') AND "
				+ "			cl.amount < 0 AND "
				+ "			cl.c_bpartner_id IN " + C_BParner_IDIN + " AND "
				+ "			cl.AD_Client_ID = ? AND ";
		// Fecha desde
		if (dateFrom != null) {
			sql += "			c.statementDate::date >= ?::date AND ";
		}
		// Fecha hasta
		if (dateTo != null) {
			sql += "			c.statementDate::date <= ?::date AND ";
		}
		sql += "			(NOT EXISTS (SELECT al.c_cashline_id "
				+ "						FROM c_allocationline AS al "
				+ "						INNER JOIN c_allocationhdr AS ah ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "						WHERE ah.c_bpartner_id IN " + C_BParner_IDIN + "AND al.c_cashline_id = cl.c_cashline_id AND docstatus IN ('CO','CL') AND ah.isactive = 'Y') "
				+ "			OR EXISTS (SELECT al.c_cashline_id "
				+ "						FROM c_allocationline as al "
				+ "						INNER JOIN c_allocationhdr as ah ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "						WHERE ah.c_bpartner_id IN " + C_BParner_IDIN + " AND al.c_cashline_id = cl.c_cashline_id AND docstatus IN ('CO','CL') AND ah.isactive = 'Y' AND allocationtype = 'OPA')) AND "
				+ "			NOT EXISTS(SELECT bpr.C_BPartner_Retencion_ID "
				+ "						FROM C_BPartner_Retencion bpr "
				+ "                  	INNER JOIN C_BPartner_Retexenc exc ON bpr.C_BPartner_Retencion_ID = exc.C_BPartner_Retencion_ID "
				+ "                  	WHERE bpr.C_RetencionSchema_ID = ? AND "
				+ "                    			bpr.C_BPartner_ID = cl.C_BPartner_ID AND "
				+ "                        		bpr.IsActive = 'Y' AND "
				+ "                        		exc.IsActive = 'Y' AND "
				+ "                        		c.statementdate BETWEEN exc.Date_From AND exc.Date_To)";
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName(), true);
		int i = 1;
		// Entidad Comercial
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Si el parámetro de compañía es null o 0 entonces tomo el de la
		// entidad comercial
		if (Util.isEmpty(clientID, true)) {
			clientID = bpartner.getAD_Client_ID();
		}
		// Compañía
		ps.setInt(i++, clientID);
		// Fecha desde
		if (dateFrom != null) {
			ps.setTimestamp(i++, dateFrom);
		}
		// Fecha hasta
		if (dateTo != null) {
			ps.setTimestamp(i++, dateTo);
		}
		// ps.setInt(i++, bpartner.getID());
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Esquema de retención
		ps.setInt(i++, retSchema.getID());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			cashlines.put(rs.getInt("c_cashline_id"),
					rs.getBigDecimal("precashlineamt"));
		}
		
		// dREHER cierre de conexion
		DB.close(rs, ps);
		
		return cashlines;
	}

	/**
	 * Obtener los cashlines imputados en algún allocation que no sean del tipo
	 * OPA.
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 */
	public Map<Integer, BigDecimal> getSumaCashLinesImputadosAnteriores(
			MBPartner bpartner, Integer clientID, Timestamp dateFrom,
			Timestamp dateTo, MRetencionSchema retSchema) throws Exception {
		Map<Integer, BigDecimal> cashlines = new HashMap<Integer, BigDecimal>();
		
		// dREHER traigo los ID de todos los proveedores con el mismo CUIT
		String C_BParner_IDIN = getIN(getC_BPartner_IDMismoCUIT(bpartner.getTaxID()));
		
		String sql = "SELECT DISTINCT cl.c_cashline_id, currencybase(abs(cl.amount), cl.c_currency_id, c.statementdate, cl.ad_client_id, cl.ad_org_id) as precashlineamt "
				+ "FROM c_cashline as cl "
				+ "INNER JOIN c_cash as c ON c.c_cash_id = cl.c_cash_id "
				+ "LEFT JOIN c_invoice as i ON cl.c_invoice_id = i.c_invoice_id "
				+ "WHERE cl.docstatus IN ('CO','CL') AND "
				+ "			cl.amount < 0 AND "
				+ "			(cl.c_bpartner_id IN  " + C_BParner_IDIN
				+ "			OR (cl.c_bpartner_id is null AND i.c_bpartner_id is not null AND i.c_bpartner_id IN " + C_BParner_IDIN + " )) AND "
				+ "			cl.AD_Client_ID = ? AND ";
		// Fecha desde
		if (dateFrom != null) {
			sql += "			c.statementDate::date >= ?::date AND ";
		}
		// Fecha hasta
		if (dateTo != null) {
			sql += "			c.statementDate::date <= ?::date AND ";
		}
		sql += "			EXISTS (SELECT al.c_cashline_id "
				+ "						FROM c_allocationline as al "
				+ "						INNER JOIN c_allocationhdr as ah ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "						INNER JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id "
				+ "						INNER JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctypetarget_id "
				+ "						WHERE ah.c_bpartner_id IN " + C_BParner_IDIN + " AND al.c_cashline_id = cl.c_cashline_id AND ah.docstatus in ('CO','CL') AND ah.isactive = 'Y' AND ah.allocationtype <> 'OPA' AND dt.applyretention = 'Y') AND "
				+ "			NOT EXISTS(SELECT bpr.C_BPartner_Retencion_ID "
				+ "						FROM C_BPartner_Retencion bpr "
				+ "                  	INNER JOIN C_BPartner_Retexenc exc ON bpr.C_BPartner_Retencion_ID = exc.C_BPartner_Retencion_ID "
				+ "                  	WHERE bpr.C_RetencionSchema_ID = ? AND "
				+ "                    			(bpr.C_BPartner_ID = cl.C_BPartner_ID "
				+ "								OR (cl.C_BPartner_ID is null AND i.C_BPartner_ID is not null AND bpr.C_BPartner_ID = i.C_BPartner_ID)) AND "
				+ "                        		bpr.IsActive = 'Y' AND "
				+ "                        		exc.IsActive = 'Y' AND "
				+ "                        		c.statementdate BETWEEN exc.Date_From AND exc.Date_To)";
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName(), true);
		int i = 1;
		// Entidad Comercial
		// ps.setInt(i++, bpartner.getID());
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Si el parámetro de compañía es null o 0 entonces tomo el de la
		// entidad comercial
		if (Util.isEmpty(clientID, true)) {
			clientID = bpartner.getAD_Client_ID();
		}
		// Compañía
		ps.setInt(i++, clientID);
		// Fecha desde
		if (dateFrom != null) {
			ps.setTimestamp(i++, dateFrom);
		}
		// Fecha hasta
		if (dateTo != null) {
			ps.setTimestamp(i++, dateTo);
		}
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Esquema de retención
		ps.setInt(i++, retSchema.getID());
		ResultSet rs = ps.executeQuery();
		Map<MInvoice, BigDecimal> allocatedAmts;
		BigDecimal netTotal;
		Integer cashlineID;
		while (rs.next()) {
			cashlineID = rs.getInt("c_cashline_id");
			// Determinar las facturas imputadas al cashline y obtener su neto
			allocatedAmts = getAllocatedInvoicesAmts("c_cashline_id", cashlineID, false, true);
			netTotal = getPayNetAmt(
					new ArrayList<MInvoice>(allocatedAmts.keySet()),
					new ArrayList<BigDecimal>(allocatedAmts.values()));
			// Si no está completamente imputado el cashline entonces el
			// residual a
			// imputar del cashline se toma total
			netTotal = netTotal.add(DB.getSQLValueBD(getTrxName(),
					"select cashlineavailable(?)", cashlineID));
			netTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			cashlines.put(cashlineID, netTotal);
		}
		
		// dREHER cierre de conexion
		DB.close(rs, ps);
		
		return cashlines;
	}

	/**
	 * Obtener los pagos anteriores, adelantados e imputados.
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 */
	public Map<Integer, BigDecimal> getSumaPagosAnteriores(MBPartner bpartner,
			Integer clientID, Timestamp dateFrom, Timestamp dateTo,
			MRetencionSchema retSchema) throws Exception {
		Map<Integer, BigDecimal> payments = new HashMap<Integer, BigDecimal>();
		// Pagos adelantados
		payments.putAll(getSumaPagosAdelantadosAnteriores(bpartner, clientID,
				dateFrom, dateTo, retSchema));
		// Pagos normales (imputados). Los pagos imputados que también son
		// adelantados (en una OPA) se pisan con el neto + paymentavailable
		payments.putAll(getSumaPagosImputadosAnteriores(bpartner, clientID,
				dateFrom, dateTo, retSchema));
		return payments;
	}

	/**
	 * Obtener los cashlines anteriores, adelantados e imputados.
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 */
	public Map<Integer, BigDecimal> getSumaCashLinesAnteriores(
			MBPartner bpartner, Integer clientID, Timestamp dateFrom,
			Timestamp dateTo, MRetencionSchema retSchema) throws Exception {
		Map<Integer, BigDecimal> cashlines = new HashMap<Integer, BigDecimal>();
		// Cashlines adelantados
		cashlines.putAll(getSumaCashLinesAdelantadosAnteriores(bpartner,
				clientID, dateFrom, dateTo, retSchema));
		// Cashlines normales (imputados). Los cashlines imputados que también
		// son
		// adelantados (en una OPA) se pisan con el neto + cashlineavailable
		cashlines.putAll(getSumaCashLinesImputadosAnteriores(bpartner,
				clientID, dateFrom, dateTo, retSchema));
		return cashlines;
	}

	public Map<Integer, BigDecimal> getSumaRetencionesPagosAnteriores(
			MBPartner bpartner, Integer clientID, Timestamp dateFrom,
			Timestamp dateTo, MRetencionSchema retSchema) throws Exception {
		return getSumaRetencionesPagosAnteriores(bpartner, clientID, dateFrom, dateTo, retSchema, null);
	}
	
	/**
	 * Obtener las retenciones anteriores tomadas como pagos, las retenciones
	 * aplicadas son siempre al neto.
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @param srcInvoiceID comprobante original
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 */
	public Map<Integer, BigDecimal> getSumaRetencionesPagosAnteriores(
			MBPartner bpartner, Integer clientID, Timestamp dateFrom,
			Timestamp dateTo, MRetencionSchema retSchema, 
			Integer srcInvoiceID) throws Exception {
		Map<Integer, BigDecimal> retenciones = new HashMap<Integer, BigDecimal>();
		
		// dREHER traigo los ID de todos los proveedores con el mismo CUIT
		String C_BParner_IDIN = getIN(getC_BPartner_IDMismoCUIT(bpartner.getTaxID()));
				
		String sql = "SELECT DISTINCT ri.c_invoice_id, currencybase(ri.amt_retenc, ri.c_currency_id, i.dateinvoiced, ri.ad_client_id, ri.ad_org_id) as retamt "
				+ "FROM m_retencion_invoice ri "
				+ "INNER JOIN c_invoice i ON ri.c_invoice_id = i.c_invoice_id "
				+ "WHERE i.docstatus in ('CO','CL') AND "
				+ "			i.c_bpartner_id IN " + C_BParner_IDIN + " AND " 
				+ "			i.ad_client_id = ? AND ";
		if(!Util.isEmpty(srcInvoiceID, true)){
			sql += "		ri.c_invoice_src_id = "+srcInvoiceID+" AND ";
		}
		// Fecha desde
		if (dateFrom != null) {
			sql += "			i.dateinvoiced::date >= to_date(?,'YYYY-mm-dd') AND ";
		}
		// Fecha hasta
		if (dateTo != null) {
			sql += "			i.dateinvoiced::date <= ?::date AND ";
		}
		sql += "			NOT EXISTS(SELECT bpr.C_BPartner_Retencion_ID "
				+ "						FROM C_BPartner_Retencion bpr "
				+ "                  	INNER JOIN C_BPartner_Retexenc exc ON bpr.C_BPartner_Retencion_ID = exc.C_BPartner_Retencion_ID "
				+ "                  	WHERE bpr.C_RetencionSchema_ID = ? AND "
				+ "                    			bpr.C_BPartner_ID = i.C_BPartner_ID AND "
				+ "                        		bpr.IsActive = 'Y' AND "
				+ "                        		exc.IsActive = 'Y' AND "
				+ "                        		i.dateinvoiced BETWEEN exc.Date_From AND exc.Date_To)";
		PreparedStatement ps = DB.prepareStatement(sql, getTrxName(), true);
		int i = 1;
		// Entidad Comercial
		// ps.setInt(i++, bpartner.getID());
		// dREHER reemplazo por si hay varias entidades con el mismo CUIT
		
		// Si el parámetro de compañía es null o 0 entonces tomo el de la
		// entidad comercial
		if (Util.isEmpty(clientID, true)) {
			clientID = bpartner.getAD_Client_ID();
		}
		// Compañía
		ps.setInt(i++, clientID);
		// Fecha desde
		if (dateFrom != null) {
			ps.setTimestamp(i++, dateFrom);
		}
		// Fecha hasta
		if (dateTo != null) {
			ps.setTimestamp(i++, dateTo);
		}
		// Esquema de retención en subconsulta
		ps.setInt(i++, retSchema.getID());
		ResultSet rs = ps.executeQuery();
		Map<MInvoice, BigDecimal> allocatedAmts;
		BigDecimal netTotal;
		Integer retencionCreditID;		
		while (rs.next()) {
			retencionCreditID = rs.getInt("c_invoice_id");
			// Determinar las facturas imputadas al cashline y obtener su neto
			allocatedAmts = getAllocatedInvoicesAmts("c_invoice_credit_id",
					retencionCreditID, false, true);
			netTotal = getPayNetAmt(
					new ArrayList<MInvoice>(allocatedAmts.keySet()),
					new ArrayList<BigDecimal>(allocatedAmts.values()));
			// Si no está completamente imputado el crédito, entonces se toma el
			// total que falta
			netTotal = netTotal.add(DB.getSQLValueBD(getTrxName(),
					"select invoiceopen(?,0)", retencionCreditID));
			netTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			retenciones.put(retencionCreditID, netTotal);
		}
		
		// dREHER cierre de conexion
		DB.close(rs, ps);
		
		return retenciones;
	}

	/**
	 * Suma de la lista de montos parámetro
	 * 
	 * @param amts
	 *            lista de montos
	 * @return la suma total de toda la lista
	 */
	public BigDecimal getSumAmts(List<BigDecimal> amts) {
		BigDecimal total = BigDecimal.ZERO;
		for (BigDecimal amt : amts) {
			total = total.add(amt);
		}
		return total;
	}

	/**
	 * Obtener el total neto de pagos anteriores. Los pagos anteriores engloban
	 * payments anticipados e imputados parcial o completamente, cashlines
	 * anticipados e imputados parcial o completamente y retenciones aplicadas
	 * con anterioridad
	 * 
	 * @param bpartner
	 *            entidad comercial
	 * @param clientID
	 *            compañía, si es null se toma el de la entidad comercial
	 * @param dateFrom
	 *            fecha de inicio de rango de payments, null si no se debe
	 *            filtrar por fecha de inicio
	 * @param dateTo
	 *            fecha de fin de rango de payments, null si no se debe filtrar
	 *            por fecha de fin
	 * @param retSchema
	 *            esquema de retención
	 * @return map con clave id de payment y valor el monto total del pago
	 *         convertido
	 * @throws Exception
	 *             en caso de error
	 * @return suma de todos los pagos anteriores
	 */
	public BigDecimal getTotalPagosAnteriores(MBPartner bpartner,
			Integer clientID, Timestamp dateFrom, Timestamp dateTo,
			MRetencionSchema retSchema) {
		BigDecimal total = BigDecimal.ZERO;

		// 1) Payments
		// Para los pagos adelantados se toma el total porque no se puede
		// obtener
		// el neto. Los pagos adelantados son los que no se encuentran en ningún
		// allocation o en algún allocation de tipo OPA. TODO: se debería crear
		// un campo en el pago registrando el neto de ese pago.
		// Para los pagos imputados en algún allocation se debe ponderar la
		// factura con el pago para obtener el neto del pago. Si el pago
		// isAllocated = Y, se determina el neto a partir de todos los amts de
		// los allocations ya que determinan los montos imputados. Encontrar
		// todas las facturas y ponderar el neto. Si el pago isAllocated = 'N' y
		// estamos en esta instancia significa que el pago está imputado en
		// algún allocation que no es OPA. Puede ser que se repitan un pago en
		// un allocation OPA y en otro allocation, por lo que para no interferir
		// se guarda en una map con clave c_payment y valor amount, donde amount
		// puede ser: 1) el valor total del pago para pagos adelantados
		// solamente; 2) El paymentAvailable del pago que se tomará como pago
		// adelantado. 3) Los netos de los pagos asignados a las facturas.

		// 2) CashLines
		// Ídem payments. Pero el campo isAllocated por lo visto no se utiliza
		// como en C_Payment, por lo tanto si queda resto del pago va al total,
		// no se calcula el neto, para esto TODO se debería crear un campo para
		// registrar el neto de cashline.

		// 3) Retenciones
		// Se tomará el total de las retenciones anteriores ya que las
		// retenciones se registran por el neto.

		try {
			// 1) Payments
			Map<Integer, BigDecimal> payments = getSumaPagosAnteriores(
					getBPartner(), clientID, dateFrom, dateTo,
					getRetencionSchema());
			total = total.add(getSumAmts(new ArrayList<BigDecimal>(payments
					.values())));
			// 2) Cashlines
			Map<Integer, BigDecimal> cashlines = getSumaCashLinesAnteriores(
					getBPartner(), clientID, dateFrom, dateTo,
					getRetencionSchema());
			total = total.add(getSumAmts(new ArrayList<BigDecimal>(cashlines
					.values())));
			// 3) Retenciones
			Map<Integer, BigDecimal> retenciones = getSumaRetencionesPagosAnteriores(
					getBPartner(), clientID, dateFrom, dateTo,
					getRetencionSchema());
			total = total.add(getSumAmts(new ArrayList<BigDecimal>(retenciones
					.values())));
		} catch (Exception ex) {
			log.info("Error al buscar el total del monto pagado en el mes !!!! ");
			ex.printStackTrace();
		}
		return total;
	}
	
	/**
	 * Devuelve el porcentaje del padrón correspondiente dependiendo los tipos
	 * de padrón configurados, si no existe ninguno entonces el valor por
	 * defecto parámetro
	 * 
	 * @param padronTypes
	 *            tipos de padrón
	 * @param defaultPorcentaje
	 *            porcentaje devuelto por defecto
	 * @return primer porcentaje de la lista de padrones parámetro o el valor
	 *         por defecto si no encuentra ninguno
	 */
	protected BigDecimal getPorcentajePadron(List<String> padronTypes, BigDecimal defaultPorcentaje){
		BigDecimal porcentaje = null;
		for (int i = 0; i < padronTypes.size() && porcentaje == null; i++) {
			// dREHER fecha de la OP y no del dia
			porcentaje = MBPartnerPadronBsAs.getBPartnerPerc("retencion",
					getBPartner().getTaxID(), (getDateTrx()!=null?getDateTrx():Env.getDate()), padronTypes.get(i),
					getTrxName());
		}
		// Si no lo encuentra, entonces el valor por defecto parámetro
		if(porcentaje == null){
			porcentaje = defaultPorcentaje;
		}
		if(porcentaje == null){
			porcentaje = BigDecimal.ZERO;
		}
		return porcentaje;
	}

	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}

	public Integer getProjectID() {
		return projectID;
	}

	public void setCampaignID(Integer campaignID) {
		this.campaignID = campaignID;
	}

	public Integer getCampaignID() {
		return campaignID;
	}

	@Override
	public String getPaymentRule() {
		return paymentRule;
	}

	@Override
	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}
}
