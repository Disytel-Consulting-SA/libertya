package org.openXpertya.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.form.VModelHelper.ResultItem;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.model.AllocationGeneratorException;
import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MDiscountConfig;
import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MInvoicePaySchedule;
import org.openXpertya.model.MLetraComprobante;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MPOSJournal;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPaymentTerm;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MTax;
import org.openXpertya.model.POCRGenerator.POCRType;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_Bank;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.rc.Invoice;
import org.openXpertya.rc.ReciboDeCliente;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

public class VOrdenCobroModel extends VOrdenPagoModel {

	/** Locale AR activo? */
	public final boolean LOCALE_AR_ACTIVE = CalloutInvoiceExt.ComprobantesFiscalesActivos();
	
	/** Error por defecto de punto de venta */
	public static final String DEFAULT_POS_ERROR_MSG = "CanGetPOSNumberButEmptyCR";
	
	/** Mensaje de error de punto de venta */
	public static String POS_ERROR_MSG = DEFAULT_POS_ERROR_MSG;
	/**
	 * Medios de pago disponibles para selección. Asociación por tipo de medio
	 * de pago.
	 */
	protected Map<String, List<MPOSPaymentMedium>> paymentMediums;
	
	/** Entidades Financieras y sus planes */
	protected Map<Integer, List<MEntidadFinancieraPlan>> entidadFinancieraPlans;
	
	/** Lista de Bancos */
	protected Map<String, String> banks;
	
	/** Recibo de Cliente con el matching de facturas y discount calculators */
	private ReciboDeCliente reciboDeCliente; 
	
	/** Punto de Venta */
	private Integer POS;

	/** Facturas creadas como débito y/o crédito en base a descuentos/recargos */
	private List<MInvoice> customInvoices = new ArrayList<MInvoice>();
	
	/** Débitos custom generados */
	private List<MInvoice> customDebitInvoices = new ArrayList<MInvoice>();
	
	/**
	 * Asociación entre los créditos custom generados y el medio de pago
	 * correspondiente en el recibo
	 */
	private Map<MInvoice, MedioPago> customCreditInvoices = new HashMap<MInvoice, VOrdenPagoModel.MedioPago>();
	
	
	
	/** Info de la organización actual */
	private MOrgInfo orgInfo;
	
	/** Existe alguna factura vencida? */
	private boolean existsOverdueInvoices = false;
	
	/** ID de caja para efectivo (cajas diarias) */
	private Integer cashID;
			
	public VOrdenCobroModel() {
		super();
		getMsgMap().put("TenderType", "CustomerTenderType");
		getMsgMap().put("AdvancedPayment", "AdvancedCustomerPayment");
		getMsgMap().put("Payment","CustomerPayment");
		reciboDeCliente = new ReciboDeCliente(getCtx(),getTrxName());
	}

	@Override
	protected OpenInvoicesTableModel getInvoicesTableModel(){
		return new OpenInvoicesCustomerReceiptsTableModel();
	}
	
	@Override
	public String getIsSOTrx() {
		return "Y";
	}

	@Override
	protected int getSignoIsSOTrx() {
		return 1;
	}

	@Override
	protected void calculateRetencions() {
		// Para los recibos de clientes, las retenciones no se calculan, 
		// se ingresan manualmente. Aquí no debe realizarse ninguna operación de cálculo.
	}

	@Override
	public String getChequeChequeraSqlValidation() {
		return " C_BankAccount.BankAccountType = 'C' AND C_BankAccount.IsChequesEnCartera = 'Y' AND C_BankAccount.C_Currency_ID = @C_Currency_ID@ ";
	}
	
	public String getRetencionSqlValidation() {
		// Solo esquemas de retenciones sufridas.
		return " C_RetencionSchema.RetencionApplication = 'S' ";
	}

	
	/**
	 * Agrega una retención como medio de cobro.
	 * @param retencionSchemaID Esquema de retención
	 * @param retencionNumber Nro. de la retención
	 * @param amount Monto de la retención
	 * @param retencionDate Fecha de la retención
	 * @throws Exception En caso de que se produzca un error al intentar cargar los datos
	 * del esquema de retención.
	 */
	public void addRetencion(Integer retencionSchemaID, String retencionNumber, BigDecimal amount, Timestamp retencionDate, Integer projectID, Integer campaignID) throws Exception {
		if (retencionSchemaID == null || retencionSchemaID == 0)
			throw new Exception("@FillMandatory@ @C_Withholding_ID@");
		if (retencionNumber == null || retencionNumber.length() == 0)
			throw new Exception("@FillMandatory@ @RetencionNumber@");
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new Exception("@NoAmountError@");
		if (retencionDate == null) 
			throw new Exception("@FillMandatory@ @Date@");
		
		RetencionProcessor retProcessor = getGeneratorRetenciones().addRetencion(retencionSchemaID);
		if (retProcessor == null)
			throw new Exception("@SaveRetencionError@");
		
		retProcessor.setDateTrx(retencionDate);
		retProcessor.setAmount(amount);
		retProcessor.setPaymentRule(getPaymentRule());
		retProcessor.setRetencionNumber(retencionNumber);
		retProcessor.setProjectID(projectID == null?0:projectID);
		retProcessor.setCampaignID(campaignID == null?0:campaignID);
		
		addRetencion(retProcessor);
	}
	
	/**
	 * Quita una retención manual de la lista de retenciones.
	 */
	public void removeRetencion(RetencionProcessor processor) {
		getRetenciones().remove(processor);
		updateRemovingRetencion(processor);
	}
	
	
	public void addRetencion(RetencionProcessor processor) {
//		getRetenciones().add(processor);
		updateAddingRetencion(processor);
	}

	/**
	 * Quita un pago adelantado de la lista de retenciones.
	 */
	public void removePagoAdelantado(MedioPagoAdelantado mpa) {
		getMediosPago().remove(mpa);
	}	

	@Override
	protected String getAllocHdrDescriptionMsg() {
		return "@CustomerReceipt@";   
	}

	@Override
	protected String getHdrAllocationType() {
		if (isNormalPayment())
			return MAllocationHdr.ALLOCATIONTYPE_CustomerReceipt;
		else
			return MAllocationHdr.ALLOCATIONTYPE_AdvancedCustomerReceipt;
	}

	@Override
	protected String getCashType() {
		return MCashLine.CASHTYPE_GeneralReceipts;
	}

	@Override
	public String getReportValue() {
		return "CustomerReceipt";
	}

	/**
	 * Inicializa los medios de pago disponibles por tipo de medio de pago
	 */
	public void initPaymentMediums(int currencyID) {		
		// Obtengo los medios de pago
		List<MPOSPaymentMedium> posPaymentMediums = MPOSPaymentMedium
				.getAvailablePaymentMediums(getCtx(), null,
						MPOSPaymentMedium.CONTEXT_POSOnly, true, getTrxName(), currencyID);
		paymentMediums = new HashMap<String, List<MPOSPaymentMedium>>();
		List<MPOSPaymentMedium> mediums = null;
		for (MPOSPaymentMedium mposPaymentMedium : posPaymentMediums) {
			mediums = paymentMediums.get(mposPaymentMedium.getTenderType());
			if(mediums == null){
				mediums = new ArrayList<MPOSPaymentMedium>();
			}
			mediums.add(mposPaymentMedium);
			paymentMediums.put(mposPaymentMedium.getTenderType(), mediums);
		}
	}

	/**
	 * Inicializa los planes de entidades financieras existentes
	 */
	public void initEntidadFinancieraPlans(){
		// Obtengo los planes
		List<MEntidadFinancieraPlan> eFPlans = MEntidadFinancieraPlan
				.getPlansAvailables(getCtx(), null, getTrxName());
		entidadFinancieraPlans = new HashMap<Integer, List<MEntidadFinancieraPlan>>();
		List<MEntidadFinancieraPlan> plans = null;
		for (MEntidadFinancieraPlan mEntidadFinancieraPlan : eFPlans) {
			plans = entidadFinancieraPlans.get(mEntidadFinancieraPlan
					.getM_EntidadFinanciera_ID());
			if(plans == null){
				plans = new ArrayList<MEntidadFinancieraPlan>();
			}
			plans.add(mEntidadFinancieraPlan);
			entidadFinancieraPlans.put(mEntidadFinancieraPlan
					.getM_EntidadFinanciera_ID(), plans);
		}		
	}
	
	/**
	 * Obtengo los medios de pago de un tipo de medio de pago parámetro
	 * 
	 * @param tenderType
	 *            tipo de medio de pago
	 * @return lista de medios de pago del tipo de medio de pago parámetro
	 */
	public List<MPOSPaymentMedium> getPaymentMediums(String tenderType, int currencyID){
		initPaymentMediums(currencyID);
		return paymentMediums.get(tenderType);
	}

	/**
	 * Obtengo los planes de la entidad financiera parámetro
	 * 
	 * @param entidadFinancieraID
	 *            id de la entidad financiera
	 * @return lista de los planes disponibles de la entidad financiera
	 *         parámetro
	 */
	public List<MEntidadFinancieraPlan> getPlans(Integer entidadFinancieraID){
		if(entidadFinancieraPlans == null){
			initEntidadFinancieraPlans();
		}
		return entidadFinancieraPlans.get(entidadFinancieraID);
	}
	
	/**
	 * Inicializo los bancos
	 */
	public void initBanks(){
		ValueNamePair[] bankList = MRefList.getList(MPOSPaymentMedium.BANK_AD_Reference_ID, false);
		banks = new HashMap<String, String>();
		for (int i = 0; i < bankList.length; i++) {
			banks.put(bankList[i].getValue(), bankList[i].getName());
		}
	}
	
	/**
	 * @param value value de la lista de bancos
	 * @return nombre del valor de la lista pasado como parámetro
	 */
	public String getBankName(String value){
		if(banks == null){
			initBanks();
		}
		return banks.get(value);
	}
	
	
	/**
	 * Agrego la tarjeta de crédito como medio de pago
	 * @param paymentMedium 
	 * @param plan
	 * @param creditCardNo
	 * @param couponNo
	 * @param amt
	 * @param bank
	 */
	public void addCreditCard(MPOSPaymentMedium paymentMedium,
			MEntidadFinancieraPlan plan, String creditCardNo, String couponNo,
			BigDecimal amt, String bank, int cuotasCount, BigDecimal cuotaAmt,
			Integer campaignID, Integer projectID, Integer monedaOriginalID) throws Exception {
		// Validaciones iniciales
		if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0)
			throw new Exception("@NoAmountError@");
		if(paymentMedium == null)
			throw new Exception("@FillMandatory@ @ReceiptMedium@");
		if(plan == null)
			throw new Exception("@FillMandatory@ @M_EntidadFinancieraPlan_ID@");
		if(bank == null)
			throw new Exception("@FillMandatory@ @C_Bank_ID@");
		MedioPagoTarjetaCredito tarjetaCredito = new MedioPagoTarjetaCredito();
		tarjetaCredito.setCouponNo(couponNo);
		tarjetaCredito.setCreditCardNo(creditCardNo);
		tarjetaCredito.setPaymentMedium(paymentMedium);
		tarjetaCredito.setEntidadFinancieraPlan(plan);
		tarjetaCredito.setImporte(amt);
		tarjetaCredito.setCuotasCount(cuotasCount);
		tarjetaCredito.setCuotaAmt(cuotaAmt);
		tarjetaCredito.setCampaign(campaignID == null?0:campaignID);
		tarjetaCredito.setProject(projectID == null?0:projectID);
		tarjetaCredito.setSOTrx(true);
		tarjetaCredito.setBank(bank);
		tarjetaCredito.setDiscountSchemaToApply(getCurrentGeneralDiscount());
		tarjetaCredito.setAccountName(getBPartner().getName());
		tarjetaCredito.setMonedaOriginalID(monedaOriginalID);
		addMedioPago(tarjetaCredito);
	}

	/**
	 * Agrega un cheque como medio de pago al árbol
	 * 
	 * @param paymentMedium
	 * @param chequeraID
	 * @param checkNo
	 * @param amount
	 * @param fechaEmi
	 * @param fechaPago
	 * @param chequeALaOrden
	 * @param bankName
	 * @param CUITLibrador
	 * @param checkDescription
	 * @throws Exception
	 */
	public void addCheck(MPOSPaymentMedium paymentMedium, Integer chequeraID,
			String checkNo, BigDecimal amount, Timestamp fechaEmi,
			Timestamp fechaPago, String chequeALaOrden, String bankName,
			String CUITLibrador, String checkDescription, Integer campaignID, 
			Integer projectID, Integer monedaOriginalID, Integer bankID) throws Exception {
		// Validaciones iniciales
		if(Util.isEmpty(chequeraID, true))
			throw new Exception("@FillMandatory@ @C_BankAccount_ID@");
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new Exception("@NoAmountError@");
		if(paymentMedium == null)
			throw new Exception("@FillMandatory@ @ReceiptMedium@");
		if (fechaEmi == null)
			throw new Exception("@FillMandatory@ @EmittingDate@");
		if (fechaPago == null)
			throw new Exception("@FillMandatory@ @PayDate@");
		if (fechaPago.compareTo(fechaEmi) < 0) 
			throw new Exception(getMsg("InvalidCheckDueDate"));
		if (fechaPago.compareTo(getFechaPagoCheque(fechaEmi, paymentMedium)) > 0){
			Date maxDueDate = getFechaPagoCheque(fechaEmi, paymentMedium);
			throw new Exception(getMsg("InvalidCheckDueDateError")
					+ ": "
					+ DisplayType.getDateFormat(DisplayType.Date).format(
							maxDueDate));
		}
		if (checkNo.trim().equals(""))
			throw new Exception("@FillMandatory@ @CheckNo@");
		MedioPagoCheque cheque = new MedioPagoCheque(chequeraID, checkNo,
				amount, fechaEmi, fechaPago, chequeALaOrden);
		cheque.setPaymentMedium(paymentMedium);
		cheque.banco = bankName;
		cheque.bancoID = bankID;
		cheque.cuitLibrador = CUITLibrador;
		cheque.descripcion = checkDescription;
		cheque.setCampaign(campaignID == null?0:campaignID);
		cheque.setProject(projectID == null?0:projectID);
		cheque.setSOTrx(true);
		cheque.setDiscountSchemaToApply(getCurrentGeneralDiscount());
		cheque.setMonedaOriginalID(monedaOriginalID);
		addMedioPago(cheque);
	}
	
	
	@Override
	protected void addCustomPaymentInfo(MPayment pay, MedioPago mp){
		// Si es tarjeta de crédito agrego la info necesaria
		if(mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_TARJETACREDITO)){
			MedioPagoTarjetaCredito tarjeta = (MedioPagoTarjetaCredito)mp;
			pay.setTenderType(MPayment.TENDERTYPE_CreditCard);
			pay.setTrxType(MPayment.TRXTYPE_CreditPayment);
			pay.setCouponNumber(tarjeta.getCouponNo());
			pay.setCreditCardNumber(tarjeta.getCreditCardNo());
			pay.setCreditCardType(tarjeta.getCreditCardType());
			pay.setA_Bank(tarjeta.getBank());
			pay.setA_Name(tarjeta.getAccountName());
			pay.setC_Project_ID(tarjeta.getProject() == null?0:tarjeta.getProject());
			pay.setC_Campaign_ID(tarjeta.getCampaign() == null?0:tarjeta.getCampaign());
			pay.setM_EntidadFinancieraPlan_ID(tarjeta.getEntidadFinancieraPlan().getID());
			tarjeta.setPayment(pay);
		}
		pay.setC_POSPaymentMedium_ID(mp.getPaymentMedium().getID());
	}
	
	@Override
	protected void addCustomCashLineInfo(MCashLine cashLine, MedioPago mp){
		cashLine.setC_POSPaymentMedium_ID(mp.getPaymentMedium().getID());		
	}
	
	
	/** Actualiza el modelo de la tabla de facturas
	 * 
	 * Tambien setea en cero los montos ingresados de cada factura. 
	 *
	 */
	public void actualizarFacturas() {
		
		int i = 1;
		
		if (m_facturas == null) {
			m_facturas = new Vector<ResultItem>();
			m_facturasTableModel.setResultItem(m_facturas);
		}
		m_facturas.clear();
		
				
		if ((!m_esPagoNormal) || (m_fechaFacturas == null || C_BPartner_ID == 0)) {
			
			// Si es pago adelantado, no muestra ninguna factura.
			
			m_facturasTableModel.fireChanged(false);
			return;
		}
		

		// paymenttermduedate
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT c_invoice_id, c_invoicepayschedule_id, orgname, documentno, dateinvoiced, duedate, currencyIso, grandTotal, openTotal, convertedamt, openamt, isexchange, C_Currency_ID, paymentrule FROM ");
		sql.append("  (SELECT i.C_Invoice_ID, i.C_InvoicePaySchedule_ID, org.name as orgname, i.DocumentNo, dateinvoiced, coalesce(i.duedate,dateinvoiced) as DueDate, cu.iso_code as currencyIso, i.grandTotal, invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)) as openTotal, "); // ips.duedate
		sql.append("    abs(currencyConvert( i.GrandTotal, i.C_Currency_ID, ?, '"+ m_fechaTrx +"'::date, null, i.AD_Client_ID, i.AD_Org_ID)) as ConvertedAmt, isexchange, ");
		sql.append("    currencyConvert( invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)), i.C_Currency_ID, ?, '"+ m_fechaTrx +"'::date, null, i.AD_Client_ID, i.AD_Org_ID) AS openAmt, i.C_Currency_ID, i.paymentrule ");
		sql.append("  FROM c_invoice_v AS i ");
		sql.append("  LEFT JOIN ad_org org ON (org.ad_org_id=i.ad_org_id) ");
		sql.append("  LEFT JOIN c_invoicepayschedule AS ips ON (i.c_invoicepayschedule_id=ips.c_invoicepayschedule_id) ");
		sql.append("  INNER JOIN C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
		sql.append("  LEFT JOIN C_Currency cu ON (cu.C_Currency_ID=i.C_Currency_ID) ");
		sql.append("  WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
		sql.append("    AND i.IsSOTRx = '" + getIsSOTrx() + "' AND GrandTotal <> 0.0 AND C_BPartner_ID = ? ");
		sql.append("    AND dt.signo_issotrx = " + getSignoIsSOTrx());
		
		if (AD_Org_ID != 0) 
			sql.append("  AND i.ad_org_id = ?  ");
		
		sql.append(" AND i.paymentRule = '").append(getPaymentRule()).append("' ");
		
		sql.append(" AND i.ispaid = 'N' ");
		sql.append("  ) as openInvoices ");
		sql.append(" GROUP BY c_invoice_id, c_invoicepayschedule_id, orgname, documentno, currencyIso, grandTotal, openTotal, dateinvoiced, duedate, convertedamt, openamt, isexchange, C_Currency_ID, paymentrule ");
		sql.append(" HAVING opentotal > 0.0 ");
		// Si agrupa no se puede filtrar por fecha de vencimiento, se deben traer todas
		if (!m_allInvoices && !getBPartner().isGroupInvoices())
			sql.append("  AND ( duedate IS NULL OR duedate <= ? ) ");
	
		sql.append(" ORDER BY DueDate");
		
		CPreparedStatement ps = null; 
		ResultSet rs = null;
		
		try {
			
			ps = DB.prepareStatement(sql.toString(), getTrxName());
			
			ps.setInt(i++, C_Currency_ID);
			ps.setInt(i++, C_Currency_ID);
			ps.setInt(i++, C_BPartner_ID);
			
			if (AD_Org_ID != 0)
				ps.setInt(i++, AD_Org_ID);
			
			if (!m_allInvoices && !getBPartner().isGroupInvoices())
				ps.setTimestamp(i++, m_fechaFacturas);

			rs = ps.executeQuery();
			//int ultimaFactura = -1;
			while (rs.next()) {
				ResultItemFactura rif = new ResultItemFactura(rs);
				// Actualizar el descuento/recargo del esquema de vencimientos
				updatePaymentTermInfo(rif);
				// Actualizar el campoIsExchange
				updateIsExchangeInfo(rif);
				m_facturas.add(rif);
			}
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error en actualizarFacturas. ", e);
		}
		finally {
			try {
				if (rs!=null)
					rs.close();
				if (ps!=null)
					ps.close();
			}
			catch (Exception e) {
				log.log(Level.SEVERE, "", e);	
			}
		}
		
		applyBPartnerDiscount();
		m_facturasTableModel.fireChanged(false);
		
	}

	/**
	 * Actualización del descuento/recargo de las facturas
	 * 
	 * @param rif
	 *            ítem de factura de la tabla, factura o cuota
	 */
	protected void updatePaymentTermInfo(ResultItemFactura rif){
		// ID de factura
		int invoiceID = (Integer) rif.getItem(m_facturasTableModel
				.getIdColIdx());
		// ID de esquema de pago de factura
		Integer invoicePayScheduleID = (Integer) rif.getItem(m_facturasTableModel
				.getInvoicePayScheduleColIdx());
		// Fecha de Facturación 
		Timestamp dateInvoiced = (Timestamp) rif
				.getItem(((OpenInvoicesCustomerReceiptsTableModel) m_facturasTableModel)
						.getDateInvoicedColIdx());
		// Fecha de vencimiento
		Timestamp dueDate = (Timestamp) rif.getItem(m_facturasTableModel
				.getDueDateColIdx());
		// Open Amt que servirá de base para el cálculo de descuento/recargo a
		// aplicar
		BigDecimal openAmt = (BigDecimal) rif.getItem(m_facturasTableModel
				.getOpenCurrentAmtColIdx()); 
		// ID de esquema de vencimientos de la factura
		int paymentTermID = DB
				.getSQLValue(
						getTrxName(),
						"SELECT c_paymentterm_id FROM c_invoice WHERE c_invoice_id = ?",
						invoiceID);
		// Obtener el esquema de vencimientos
		MPaymentTerm paymentTerm = new MPaymentTerm(getCtx(), paymentTermID,
				getTrxName());
		// Obtengo el esquema de pago de la factura
		MInvoicePaySchedule ips = null;
		if(!Util.isEmpty(invoicePayScheduleID, true)){
			ips = new MInvoicePaySchedule(getCtx(), invoicePayScheduleID,
					getTrxName());
		}
		// Obtengo el descuento y lo seteo dentro del Result Item
		BigDecimal discount = paymentTerm.getDiscount(ips, dateInvoiced,
				dueDate, new Timestamp(System.currentTimeMillis()), openAmt);
		rif.setPaymentTermDiscount(discount);
	}
	
	protected void updateIsExchangeInfo(ResultItemFactura rif){
		// Obtengo el valor del campo isExchange y lo seteo dentro del Result Item
		rif.setIsexchange((String) rif.getItem(((OpenInvoicesCustomerReceiptsTableModel) m_facturasTableModel).getIsExchange()));
	}
	
	@Override
	public void updateBPartner(Integer bPartnerID){
		super.updateBPartner(bPartnerID);
		// Actualizo el descuento
		reciboDeCliente.updateBPartner(bPartnerID);
	}

	/**
	 * Determino si existen facturas vencidas a partir de la fecha de
	 * vencimiento parámetro
	 * 
	 * @param dueDate
	 *            fecha de vencimiento de una factura o cuota
	 */
	protected void setIsOverdueInvoice(Timestamp dueDate){
		if(dueDate == null)return;
		// Si la fecha de vencimiento de la factura se encuentra después de la
		// fecha de login, entonces existen facturas vencidas del cliente
		if(TimeUtil.getDiffDays(dueDate,Env.getDate()) > 0){
			existsOverdueInvoices = true;
		}
	}
		
	/**
	 * Aplica el esquema de descuento a las facturas existentes si pueden ser
	 * aplicables según cada calculator discount propio
	 */
	public void applyBPartnerDiscount(){
		reciboDeCliente.applyBPartnerDiscount();
	}
	
	
	public MDiscountSchema getDiscountFrom(MEntidadFinancieraPlan plan){
		return reciboDeCliente.getDiscountFrom(plan);
	}
	
	public MDiscountSchema getDiscountFrom(MPOSPaymentMedium paymentMedium){
		return reciboDeCliente.getDiscountFrom(paymentMedium);
	}
	
	public MDiscountSchema getDiscount(MPOSPaymentMedium paymentMedium, MEntidadFinancieraPlan plan){
		return reciboDeCliente.getDiscount(paymentMedium, plan);
	}
	
	/**
	 * @param discountSchema
	 * @param baseAmt
	 */
	public void updateGeneralDiscount(MDiscountSchema discountSchema){
		reciboDeCliente.setCurrentGeneralDiscountSchema(discountSchema);
	}

	public MDiscountSchema getCurrentGeneralDiscount(){
		return reciboDeCliente.getCurrentGeneralDiscountSchema();
	}
	
	/**
	 * @param verifyApplication
	 *            true si se debe verificar si es posible aplicar el descuento
	 *            de entidad comercial en base a configuración de descuento,
	 *            false si solo se debe retornar el descuento de entidad
	 *            comercial configurado
	 * @return esquema de descuentos de la entidad comercial o null caso que no
	 *         exista ninguno configurado o no se pueda aplicar (si es true el
	 *         parámetro)
	 */
	public MDiscountSchema getbPartnerDiscountSchema(boolean verifyApplication){
		MDiscountSchema discountSchema = reciboDeCliente.getbPartnerDiscountSchema();
		if(verifyApplication){
			if(discountSchema != null){
				if (!reciboDeCliente.isBPartnerDiscountApplicable(
						discountSchema.getDiscountContextType(), getBPartner()
								.getDiscountContext())) {
					discountSchema = null;
				}
			}
		}
		return discountSchema;
	}
	
	@Override
	protected void updateInvoicesModel(Vector<Integer> facturasProcesar, Vector<BigDecimal> manualAmounts, Vector<BigDecimal> manualAmountsOriginal, Vector<ResultItemFactura> resultsProcesar){
		reciboDeCliente.clear();
		int totalInvoice = facturasProcesar.size();
		BigDecimal paymentTermDiscount;
		for (int i = 0; i < totalInvoice ;i++) {
//			reciboDeCliente.add(AD_Org_ID,facturasProcesar.get(i),manualAmounts.get(i), true);
			paymentTermDiscount = resultsProcesar.get(i).getPaymentTermDiscount();
			reciboDeCliente.add(facturasProcesar.get(i),manualAmounts.get(i),manualAmountsOriginal.get(i), paymentTermDiscount, true);
		}
		
		// Creo la factura global
		reciboDeCliente.makeGlobalInvoice();
	}
	
	
	public BigDecimal getToPayAmount(MDiscountSchema discountSchema){
		BigDecimal toPay = BigDecimal.ZERO;
		if(m_esPagoNormal){
			toPay = reciboDeCliente.getToPayAmt(discountSchema);
		}
		else{
			toPay = getSaldoMediosPago();
		}
		return toPay;
	}
	
	/**
	 * @param discountContextType
	 *            tipo de contexto de descuento
	 * @return Indica si son aplicables los descuentos por medios de pagos en el
	 *         contexto pasado como parámetro
	 */
	public boolean isPaymentMediumDiscountApplicable(String discountContextType) {
		return reciboDeCliente
				.isGeneralDocumentDiscountApplicable(discountContextType);
	}
	
	@Override
	public void updateAditionalInfo(){
		// Actualizo el descuento de entidad comercial
		reciboDeCliente.updateBPDiscountSchema();
	}

	/**
	 * Suma los descuentos realizados al recibo de cliente (a todas las
	 * facturas)
	 * 
	 * @return suma de los descuentos aplicados hasta el momento
	 */
	public BigDecimal getSumaDescuentos(){
		return reciboDeCliente.getTotalDiscountAmt();
	}
	
	@Override
	public void updateAddingMedioPago(MedioPago mp){
		if(getToPayAmount(null).compareTo(BigDecimal.ZERO) != 0){
			// Agregar el medio de pago en el recibo
			reciboDeCliente.addMedioPago(mp);
		}
	}

	@Override
	public void updateRemovingMedioPago(MedioPago mp){
		// Eliminar el medio de pago en el recibo
		reciboDeCliente.removeMedioPago(mp);
	}
	
	@Override
	protected void updateAddingRetencion(RetencionProcessor processor){
		if(getToPayAmount(null).compareTo(BigDecimal.ZERO) != 0){
			// Agregar la retención en el recibo
			reciboDeCliente.addRetencion(processor);
		}
	}

	@Override
	protected void updateRemovingRetencion(RetencionProcessor processor){
		// Eliminar la retencion en el recibo
		reciboDeCliente.removeRetencion(processor);
	}
	
	/**
	 * Actualiza todos los descuentos
	 */
	public void updateDiscounts(){
		// Actualizar descuentos
		reciboDeCliente.updateDiscounts();
	}

	@Override
	public void addDebit(MInvoice invoice){
		reciboDeCliente.addDebit(invoice);
	}
	
	protected List<Invoice> ordenarFacturas(List<Invoice> invoicesToSort) {
		Comparator<Invoice> cmp = new Comparator<Invoice>() {
			public int compare(Invoice arg0, Invoice arg1) {
				BigDecimal b0 = arg0.getManualAmt();
				BigDecimal b1 = arg1.getManualAmt();
				return b0.compareTo(b1);
			}
		};
		Invoice[] invoicesSorted = new Invoice[invoicesToSort.size()];
		invoicesToSort.toArray(invoicesSorted);		
		Arrays.sort(invoicesSorted, cmp);
		return Arrays.asList(invoicesSorted);
	}
	
	@Override
	public void updateOrg(int orgID){
		super.updateOrg(orgID);
		orgID = orgID != 0?orgID:Env.getAD_Org_ID(getCtx());
		orgInfo = MOrgInfo.get(getCtx(), orgID);
		// Tomar la configuración de descuentos de la organización seleccionada
		reciboDeCliente.updateOrg(orgID);
		// Obtener el punto de venta de cajas diarias
		setPOS(getPOSNumber());
		// Obtener la caja de la caja diaria actual
		initCashID();
	}

	/**
	 * @return true si es posible obtener el punto de venta 
	 */
	public boolean autoGettingPOSValidation(){
		return LOCALE_AR_ACTIVE && getPOSNumber() != null;
	}

	/**
	 * @return true si existe la posibilidad de obtener el punto de venta
	 *         automáticamente, false caso contrario
	 */
	public boolean mustGettingPOSNumber(){
		return LOCALE_AR_ACTIVE;
	}

	/**
	 * Obtengo el nro de punto de venta para el usuario actual. Primero verifico
	 * la caja diaria activa, sino obtengo de la configuración del TPV para ese
	 * usuario y organización. Si existe más de una configuración no se coloca
	 * ninguna para que el usuario inserte la adecuada.
	 * 
	 * @return nro de punto de venta para el usuario y organización actual, null
	 *         si no existe ninguna o más de una
	 */
	public Integer getPOSNumber(){
		// Obtenerlo de la caja diaria
		Integer posNro = MPOSJournal.getCurrentPOSNumber(null);
		boolean error = true;
		// Si no existe, directamente el pto de venta del usuario
		if(Util.isEmpty(posNro, true)){
			// Verificar los nros de los puntos de venta, si hay mas de uno
			// dejar vacío para que lo complete el usuario
			List<MPOS> poss = MPOS.get(getCtx(), AD_Org_ID, null, getTrxName());
			if(poss.size() == 1){
				posNro = poss.get(0).getPOSNumber();
			}
			else if(poss.size() > 1){
				POS_ERROR_MSG = "CanGetPOSNumberButMoreThanOne";
				error = false;
			}
		}
		if(Util.isEmpty(posNro, true) && error){
			posNro = null;
			POS_ERROR_MSG = DEFAULT_POS_ERROR_MSG;
		}
		return posNro;
	}
	
	/**
	 * @return el cargo de cobro de facturas vencidas de la organización
	 */
	public BigDecimal getOrgCharge(){
		BigDecimal charge = BigDecimal.ZERO;
		if (existsOverdueInvoices && orgInfo != null) {
			charge = orgInfo.getOverdueInvoicesCharge();
		}
		return charge;
	}
	
	public Integer getPOS(){
		return POS;
	}
	
	public void setPOS(Integer pOS) {
		POS = pOS;
	}

	/**
	 * Este método fue modificado casi en su totalidad para que utilice la funcionalidad
	 * de la clase POCRGenerator.
	 */
	@Override
	public void updateOnlineAllocationLines(Vector<MedioPago> pays) throws AllocationGeneratorException{
		// Eliminar todos los allocation anteriores
		onlineAllocationLines = null;
		onlineAllocationLines = new ArrayList<AllocationLine>();
	
		Vector<MedioPago> credits = new Vector<MedioPago>( pays );
		Vector<Invoice> debits = new Vector<Invoice>(ordenarFacturas(reciboDeCliente.getRealInvoices()));
		
		// Agrego las facturas al generador
		for (Invoice f : debits) {
			int invoiceID = f.getInvoiceID();
			BigDecimal payAmount = f.getManualAmtOriginal().add(f.getTotalPaymentTermDiscountOriginalCurrency());
			poGenerator.addInvoice(invoiceID, payAmount);
		}	
		
		// Agrego los medios de pagos al generador	
		for (MedioPago pago : credits) {
			pago.addToGenerator(poGenerator);
		}
		// Se crean las líneas de imputación entre las facturas y los pagos
		poGenerator.generateLines();
	}
	
	@Override
	protected void makeCustomDebitsCredits(Vector<MedioPago> pays) throws Exception{
		// Crear los débitos para recargos
		// Crear medios de pago de crédito para descuentos
		// Obtener la suma de descuentos agrupada por tasa de impuesto y crear las
		// líneas de factura en la factura que corresponda
//		BigDecimal discountSum = getSumaDescuentos();
//		// FIXME qué pasa cuando se aplicaron descuentos y recargos a la vez
//		// donde el total da 0?
//		if (discountSum.compareTo(BigDecimal.ZERO) == 0
//				&& getTotalPaymentTermDiscount().compareTo(BigDecimal.ZERO) == 0) {
//			return;
//		}
		// Resetear los débitos y créditos custom ya que se generan nuevamente,
		// sacarlos del recibo de cliente si existen
		for (MInvoice customInvoice : customCreditInvoices.keySet()) {
			reciboDeCliente.removeMedioPago(customCreditInvoices.get(customInvoice));
		}
		for (MInvoice customInvoice : customDebitInvoices) {
			reciboDeCliente.removeDebit(customInvoice);
		}
		customCreditInvoices.clear();
		customDebitInvoices.clear();
		customInvoices.clear();
		// Obtener la suma de descuentos/recargos por tipo de descuento
		Map<String, BigDecimal> discountsPerKind = reciboDeCliente.getDiscountsSumPerKind();
		Set<String> kinds = discountsPerKind.keySet();
		MInvoice credit = null;
		MInvoice debit = null;
		MInvoice inv = null; 
		MInvoiceLine invoiceLine = null; 
		BigDecimal amt;
		boolean isCredit;
		boolean createInvoice;
		MTax tax = MTax.getTaxExemptRate(getCtx(),getTrxName());
		for (String discountKind : kinds) {
			amt = discountsPerKind.get(discountKind);
			if(amt!=null && amt.compareTo(BigDecimal.ZERO) != 0){
				isCredit = amt.compareTo(BigDecimal.ZERO) > 0;
				// Si es cargo por organización entonces el monto de cargo viene
				// positivo, no es descuento sino recargo por eso hay que
				// intercambiar el valor 
				if(discountKind.equals(ReciboDeCliente.CHARGE_DISCOUNT)){
					isCredit = !isCredit;
				}
				createInvoice = isCredit?credit == null:debit==null;
				if(createInvoice){
					// Crear la factura
					inv = createCreditDebitInvoice(isCredit);
					inv.setBPartner(getBPartner());
					inv.setC_Project_ID(getProjectID());
					inv.setC_Campaign_ID(getCampaignID());
					if(!inv.save()){
						throw new Exception("Can't create " + (isCredit ? "credit" : "debit")
								+ " document for discounts. Original Error: "+CLogger.retrieveErrorAsString());
					}
					if(isCredit){
						credit = inv;
					}
					else{
						debit = inv;
					}
				}
				// Si es crédito 
				inv = isCredit?credit:debit;
				// Creo la línea de la factura
				invoiceLine = createInvoiceLine(inv,isCredit,amt,tax,discountKind);				
				if(!invoiceLine.save()){
					throw new Exception("Can't create " + (isCredit ? "credit" : "debit")
							+ " document line for discounts. Original Error: "+CLogger.retrieveErrorAsString());  
				}
			}
		}
		// - Si es hay crédito lo guardo como un medio de pago
		// - Si es débito lo guardo donde se encuentran las facturas
		if(credit != null){
			// Refrescar la factura con la de la base 
			credit = refreshInvoice(credit.getCtx(), credit.getC_Invoice_ID(), getTrxName());
			// Completar el crédito en el caso que no requiera impresión fiscal,
			// ya que si requieren se realiza al final del procesamiento
			if(!needFiscalPrint(credit)){
				credit.setSkipAutomaticCreditAllocCreation(true);
				processDocument(credit, MInvoice.DOCACTION_Complete);
			}
			// Asociar como medio de pago
			MedioPagoCredito credito = new MedioPagoCredito(true);
			credito.setC_invoice_ID(credit.getID());
			credito.setImporte(credit.getGrandTotal());
			pays.add(credito);
			// Agregarla a la lista local para después jugar con ellas para la
			// cuenta corriente del cliente
			customCreditInvoices.put(credit, credito);
			customInvoices.add(credit);
		}
		if(debit != null){
			// Refrescar la factura con la de la base 
			debit = refreshInvoice(debit.getCtx(), debit.getC_Invoice_ID(), getTrxName());
			// Completar el crédito en el caso que no requiera impresión fiscal,
			// ya que si requieren se realiza al final del procesamiento
			if(!needFiscalPrint(debit)){
				processDocument(debit, MInvoice.DOCACTION_Complete);
			}
			// Agregarlo como nuevo débito
			addDebit(debit);
			// Agregarla a la lista local para después jugar con ellas para la
			// cuenta corriente del cliente
			customDebitInvoices.add(debit);
			customInvoices.add(debit);
		}
	}

	protected MInvoice refreshInvoice(Properties ctx, Integer invoiceID, String trxName){
		MInvoice invoice = new MInvoice(ctx, invoiceID, trxName);
		// Se indica que no se debe crear una línea de caja al completar la factura ya
		// que es el propio TPV el que se encarga de crear los pagos e imputarlos con
		// la factura (esto soluciona el problema de líneas de caja duplicadas que 
		// se había detectado).
		invoice.setCreateCashLine(false);
		// Seteo el bypass de la factura para que no chequee el saldo del
		// cliente porque ya lo chequea el tpv
		invoice.setCurrentAccountVerified(true);
		// Seteo el bypass para que no actualice el crédito del cliente ya
		// que se realiza luego al finalizar las operaciones
		invoice.setUpdateBPBalance(false);
		return invoice;
	}
	
	/**
	 * Creo una factura como crédito o débito, dependiendo configuración.
	 * 
	 * @param credit
	 *            true si se debe crear un crédito o false si es débito
	 * @return factura creada
	 * @throws Exception en caso de error
	 */
	protected MInvoice createCreditDebitInvoice(boolean credit) throws Exception{
		MInvoice invoice = new MInvoice(getCtx(), 0, getTrxName());
		invoice.setBPartner(getBPartner());
		// Setear el tipo de documento
		invoice = setDocType(invoice, credit);
		
		if(LOCALE_AR_ACTIVE){
			invoice = addLocaleARData(invoice, credit);
		}
		
		// Se indica que no se debe crear una línea de caja al completar la factura ya
		// que es el propio TPV el que se encarga de crear los pagos e imputarlos con
		// la factura (esto soluciona el problema de líneas de caja duplicadas que 
		// se había detectado).
		invoice.setCreateCashLine(false);
		
		// La forma de pago es la del RC
		invoice.setPaymentRule(getPaymentRule());
		
		invoice.setDocAction(MInvoice.DOCACTION_Complete);
		invoice.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		// Seteo el bypass de la factura para que no chequee el saldo del
		// cliente porque ya lo chequea el tpv
		invoice.setCurrentAccountVerified(true);
		// Seteo el bypass para que no actualice el crédito del cliente ya
		// que se realiza luego al finalizar las operaciones
		invoice.setUpdateBPBalance(false);
		return invoice;
	}

	/**
	 * Setea el tipo de documento a la factura parámetro
	 * 
	 * @param invoice
	 *            factura a modificar
	 * @param isCredit
	 *            booleano que determina si lo que estoy creando es un débito o
	 *            un crédito
	 * @return factura con el tipo de doc seteada
	 */
	protected MInvoice setDocType(MInvoice invoice, boolean isCredit) throws Exception{
		// Obtengo el doctype de configuración
		Integer docType = null;
		String generalDocType = null;
		if(isCredit){
			generalDocType = reciboDeCliente.getGeneralCreditDocType();
			if(generalDocType != null && generalDocType.equals(MDiscountConfig.CREDITDOCUMENTTYPE_Other)){
				docType = reciboDeCliente.getCreditDocType();
			}
		}
		else{
			generalDocType = reciboDeCliente.getGeneralDebitDocType();
			if(generalDocType != null && generalDocType.equals(MDiscountConfig.DEBITDOCUMENTTYPE_Other)){
				docType = reciboDeCliente.getDebitDocType();
			}
		}
		// Si el tipo de documento general es null, entonces error
		if(generalDocType == null){
			throw new Exception(Msg.getMsg(getCtx(), "DocTypeNotConfiguredForDiscountRecharge"));
		}
		MDocType documentType = null;
		// Si tengo un tipo de documento real configurado, entonces obtengo ese
		// tipo de doc
		if(docType != null){
			documentType = new MDocType(getCtx(), docType, getTrxName());
		}
		// Si todavía no encontré el tipo de doc, lo busco en base al general y
		// si estamos sobre Locale_Ar activo
		if(documentType == null){
			// Obtener la clave del tipo de documento a general
			String docTypeKey = getRealDocTypeKey(generalDocType, isCredit);
			// Si está activo locale_ar entonces se debe obtener en base al pto de venta y la letra
			if(LOCALE_AR_ACTIVE){
				MLetraComprobante letra = getLetraComprobante();
				if(Util.isEmpty(getPOS(),true)) throw new Exception(getMsg("NotExistPOSNumber"));
				Integer posNumber = Integer.valueOf(getPOS());
				// Se obtiene el tipo de documento para la factura.
				documentType = MDocType.getDocType(getCtx(), invoice.getAD_Org_ID(), docTypeKey,
						letra.getLetra(), posNumber, getTrxName());
				if (documentType == null) {
					throw new Exception(Msg.getMsg(getCtx(),
							"NonexistentPOSDocType", new Object[] { letra,
									posNumber }));
				}
				if(!Util.isEmpty(posNumber,true)){
					invoice.setPuntoDeVenta(posNumber);
				}
			}
			else{
				// Tipo de documento en base a la key
				documentType = MDocType.getDocType(getCtx(), docTypeKey, getTrxName()); 
			}
		}
		invoice.setC_DocTypeTarget_ID(documentType.getID());
		return invoice;
	}

	/**
	 * Obtener la letra del comprobante
	 * 
	 * @return letra del comprobante
	 * @throws Exception
	 *             si la compañía o el cliente no tienen configurado una
	 *             categoría de IVA y si no existe una Letra que los corresponda
	 */
	protected MLetraComprobante getLetraComprobante() throws Exception{
		Integer categoriaIVAclient = CalloutInvoiceExt.darCategoriaIvaClient();
		Integer categoriaIVACustomer = getBPartner().getC_Categoria_Iva_ID();
		// Se validan las categorias de IVA de la compañia y el cliente.
		if (categoriaIVAclient == null || categoriaIVAclient == 0) {
			throw new Exception(getMsg("ClientWithoutIVAError"));
		} else if (categoriaIVACustomer == null || categoriaIVACustomer == 0) {
			throw new Exception(getMsg("BPartnerWithoutIVAError"));
		}
		// Se obtiene el ID de la letra del comprobante a partir de las categorias de IVA.
		Integer letraID = CalloutInvoiceExt.darLetraComprobante(categoriaIVACustomer, categoriaIVAclient);
		if (letraID == null || letraID == 0){
			throw new Exception(getMsg("LetraCalculationError"));
		}
		// Se obtiene el PO de letra del comprobante.
		return new MLetraComprobante(getCtx(), letraID, getTrxName());
	}

	/**
	 * Obtener la clave del tipo de documento real en base al general y si el
	 * comprobante que estoy creando es un crédito o un débito
	 * 
	 * @param generalDocType
	 *            tipo de documento general
	 * @param isCredit
	 *            true si estamos ante un crédito, false caso contrario
	 * @return
	 */
	protected String getRealDocTypeKey(String generalDocType, boolean isCredit){
		// La lista de tipos de documento generales tiene como value los doc
		// type keys de los tipos de documento
		String docTypeKey = generalDocType;
		// Para Locale AR, Abono de Cliente es Nota de Crédito o Nota de Débito
		// dependiendo si estamos creando un crédito o un débito respectivamente 
		if(LOCALE_AR_ACTIVE){
			// Nota de Crédito
			if (isCredit
					&& generalDocType
							.equals(MDiscountConfig.CREDITDOCUMENTTYPE_CreditNote)) {
				docTypeKey = MDocType.DOCTYPE_CustomerCreditNote;
			}
			// Nota de Débito
			if (!isCredit
					&& generalDocType
							.equals(MDiscountConfig.DEBITDOCUMENTTYPE_DebitNote)) {
				docTypeKey = MDocType.DOCTYPE_CustomerDebitNote;
			}
		}
		return docTypeKey;
	}
	
	/**
	 * Agregar la info de locale ar necesaria a la factura con localización
	 * argentina.
	 * 
	 * @param invoice
	 *            factura
	 * @param credit true si estamos ante un crédito, false si es débito
	 * @return factura parámetro con toda la info localeAr necesaria cargada
	 * @throws Exception en caso de error
	 */
	protected MInvoice addLocaleARData(MInvoice invoice, boolean credit) throws Exception{
		MLetraComprobante letraCbte = getLetraComprobante();
		// Se asigna la letra de comprobante, punto de venta y número de comprobante
		// a la factura creada.
		invoice.setC_Letra_Comprobante_ID(letraCbte.getID());
		// Nro de comprobante.
		Integer nroComprobante = CalloutInvoiceExt
				.getNextNroComprobante(invoice.getC_DocTypeTarget_ID());
		if (nroComprobante != null)
			invoice.setNumeroComprobante(nroComprobante);
		
		// Asignación de CUIT en caso de que se requiera.
		String cuit = getBPartner().getTaxID();
		invoice.setCUIT(cuit);
		
		// Setear una factura original al crédito que estamos creando
		if(credit && LOCALE_AR_ACTIVE){
			// Obtengo la primer factura como random (la impresora fiscal puede tirar un error si no existe una factura original seteada)
			Invoice firstInvoice = reciboDeCliente.getRealInvoices().get(0);
			if(firstInvoice != null){
				invoice.setC_Invoice_Orig_ID(firstInvoice.getInvoiceID());
			}
		}
		
		return invoice;
	}

	/**
	 * Crea una línea de factura de la factura y datos parámetro.
	 * 
	 * @param invoice
	 *            factura
	 * @param isCredit
	 *            true si estamos creando un crédito, false caso contrario
	 * @param amt
	 *            monto de la línea
	 * @param tax
	 *            impuesto para la línea
	 * @param discountKind
	 *            tipo de descuento para obtener el artículo correspondiente de
	 *            la configuración de descuentos
	 * @return línea de la factura creada
	 * @throws Excepción
	 *             en caso de error
	 */
	public MInvoiceLine createInvoiceLine(MInvoice invoice, boolean isCredit, BigDecimal amt, MTax tax, String discountKind) throws Exception{
		MInvoiceLine invoiceLine = new MInvoiceLine(invoice);
		invoiceLine.setQty(1);
		// Setear el precio con el monto del descuento
		amt = amt.abs();
		Integer sign = reciboDeCliente.getGeneralDocTypeSign(isCredit);
		amt = amt.multiply(new BigDecimal(sign));
		invoiceLine.setPriceEntered(amt);
		invoiceLine.setPriceActual(amt);
		invoiceLine.setPriceList(amt);
		invoiceLine.setC_Tax_ID(tax.getID());
		invoiceLine.setLineNetAmt();
		invoiceLine.setC_Project_ID(invoice.getC_Project_ID());
		// Setear el artículo
		// Buscar el artículo en la config
		Integer productID = reciboDeCliente.getConfigProductID(isCredit, discountKind);
		if(Util.isEmpty(productID,true)){
			throw new Exception(
					"Falta configuracion de articulos para crear créditos/débitos para descuentos/recargos");
		}
		invoiceLine.setM_Product_ID(productID);
		return invoiceLine;
	}

	/**
	 * Indica si la factura debe ser emitida mediante un controlador fiscal.
	 * @param invoice Factura a evaluar.
	 */
	private boolean needFiscalPrint(MInvoice invoice) {
		return MDocType.isFiscalDocType(invoice.getC_DocTypeTarget_ID())
				&& LOCALE_AR_ACTIVE;
	}
	
	/**
	 * Procesa la factura en base a un docaction parámetro
	 * 
	 * @param invoice
	 *            factura
	 * @param docAction
	 *            acción
	 * @throws Exception
	 *             si hubo error al realizar el procesamiento o al guardar
	 */
	public void processDocument(MInvoice invoice, String docAction) throws Exception{
		// Procesar el documento
		if(!invoice.processIt(docAction)){
			throw new Exception(invoice.getProcessMsg());
		}
		// Guardar
		if(!invoice.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}
	
	@Override
	public BigDecimal getSaldoMediosPago() {
		BigDecimal saldo = super.getSaldoMediosPago();
		saldo = saldo.subtract(getSumaDescuentos());
		return saldo;
	}
		
	@Override
	public BigDecimal getSumaTotalPagarFacturas() {
		BigDecimal suma = new BigDecimal(0);
		
		if (m_esPagoNormal) {
			suma = super.getSumaTotalPagarFacturas();
			suma = suma.add(existsOverdueInvoices ? getOrgCharge()
					: BigDecimal.ZERO); 
		} else {
			suma = m_montoPagoAnticipado;
		}
		
		return suma;
	}
	
	@Override
	public BigDecimal getAllocationHdrTotalAmt(){
		BigDecimal hdrAmt = BigDecimal.ZERO; 
		if(m_esPagoNormal){
			hdrAmt = reciboDeCliente.getTotalAPagar();
		}
		else{
			hdrAmt = m_montoPagoAnticipado;
		}
		return hdrAmt; 
	}
	
	@Override
	protected void performAditionalCustomCurrentAccountWork(MOrg org, CurrentAccountManager manager) throws Exception{
		// Itero por las facturas creadas y realizo las tareas adicionales
		for (MInvoice invoice : customInvoices) {
			performAditionalCurrentAccountWork(org, getBPartner(), manager, invoice, true);
		}
	}
	
	@Override
	public void reset(){
		super.reset();
		reciboDeCliente = new ReciboDeCliente(getCtx(),getTrxName());
		customInvoices = new ArrayList<MInvoice>();
		customDebitInvoices = new ArrayList<MInvoice>();
		customCreditInvoices = new HashMap<MInvoice, VOrdenPagoModel.MedioPago>();
		existsOverdueInvoices = false;
	}
	
	public BigDecimal getSaldoMediosPago(boolean finishing){
		if(!finishing)return getSaldoMediosPago();
		BigDecimal saldo = super.getSaldoMediosPago();
		if (saldo.compareTo(BigDecimal.ZERO) != 0
				&& saldo.abs().compareTo(ReciboDeCliente.ROUND_TOLERANCE) > 0) {
			saldo = saldo.subtract(getSumaDescuentos());
		}
		return saldo;
	}
	
	/**
	 * Verificar si estamos cobrando facturas vencidas
	 */
	public void updateOverdueInvoicesCharge(){
		Timestamp dueDate = null;
		existsOverdueInvoices = false;
		if(m_facturas != null){
			for (ResultItem f : m_facturas) {
				ResultItemFactura fac = (ResultItemFactura) f;
				if (fac.getManualAmtClientCurrency().signum() > 0) {
					dueDate = (Timestamp) fac.getItem(m_facturasTableModel
							.getDueDateColIdx());
					setIsOverdueInvoice(dueDate);
				}
			}
		}
		// Actualizo el recibo con el monto de cargo de la organización
		reciboDeCliente.setOrgCharge(getOrgCharge());
	}

	/**
	 * @return monto equivalente al total abierto de las facturas a fecha de
	 *         vencimiento menor o igual a la actual o del mismo mes + cargo si
	 *         hubiere
	 */
	public BigDecimal getDefaultGroupingValue(){
		BigDecimal defaultValue = BigDecimal.ZERO;
		int totalInvoices = m_facturas.size();
    	ResultItemFactura fac;
    	boolean charged = false;
    	boolean stop = false;
    	Timestamp dueDate;
    	Timestamp loginDate = Env.getDate();
    	Calendar loginCalendar = Calendar.getInstance();
    	loginCalendar.setTimeInMillis(loginDate.getTime());
    	Calendar dueCalendar = Calendar.getInstance();
		// Itero por las facturas y voy asignandoles el monto de agrupación
		// desde la mas antigua a la mas nueva hasta llegar a que la fecha de
		// vencimiento sea mayor a la actual o que se encuentre en el mismo mes 
		for (int i = 0; i < totalInvoices && !stop; i++) {
			fac = (ResultItemFactura) m_facturas.get(i);
			dueDate = (Timestamp)fac.getItem(m_facturasTableModel.getDueDateColIdx());
			if(dueDate != null){
				dueCalendar.setTimeInMillis(dueDate.getTime());
			}
			// Paro cuando encuentro una fecha de vencimiento mayor a la fecha
			// actual o se encuentra en el mismo mes 
			stop = dueDate != null
					&& (TimeUtil.getDiffDays(loginDate, dueDate) > 0 
							||	((loginCalendar
									.get(Calendar.YEAR) == dueCalendar
									.get(Calendar.YEAR)) && (loginCalendar
									.get(Calendar.MONTH) == dueCalendar
									.get(Calendar.MONTH))));
			if(!stop){
				// Verifico la fecha de vencimiento ya que debo incrementar con el
				// cargo de la organización
				if(!charged){
					setIsOverdueInvoice(dueDate);
					if (getOrgCharge().compareTo(BigDecimal.ZERO) > 0) {
						defaultValue = defaultValue.add(getOrgCharge());
						charged = true;
					}
				}
				defaultValue = defaultValue.add(fac.getToPayAmt(true));
			}
		}
		return defaultValue;
	}
	
	
	/**
	 * Actualizar los montos manuales de las facturas prorateando el monto
	 * grupal parámetro
	 * 
	 * @param groupingAmt
	 *            monto de agrupación
	 */
	public void updateGroupingAmtInvoices(BigDecimal groupingAmt){
		// Actualizar los montos manuales
    	BigDecimal amt = groupingAmt;
    	int totalInvoices = m_facturas.size();
    	ResultItemFactura fac;
    	BigDecimal currentManualAmt, toPay;
    	boolean chargeDecremented = false;
		// Itero por las facturas y voy asignandoles el monto de agrupación
		// desde la mas antigua a la mas nueva
    	int i = 0;
		for (; i < totalInvoices && amt.compareTo(BigDecimal.ZERO) > 0; i++) {
			fac = (ResultItemFactura) m_facturas.get(i);
			// Verifico la fecha de vencimiento ya que debo decrementar primero
			// el cargo de la organización
			// CUIDADO: La lista de facturas debe ir ordenada de forma
			// ascendente por la fecha de vencimiento
			if(!chargeDecremented){
				setIsOverdueInvoice((Timestamp) fac.getItem(m_facturasTableModel
						.getDueDateColIdx()));
				if (getOrgCharge().compareTo(BigDecimal.ZERO) > 0) {
					amt = amt.subtract(getOrgCharge());
					chargeDecremented = true;
				}
			}
			// Si el monto es mayor a 0 sigo. Quizás con el decremento del cargo
			// se volvió a 0 o menor. Si se volvió menor habrá un problema con
			// la factura o cuota anterior por eso debe ir ordenado por fecha de
			// vencimiento ascendente 
			if(amt.compareTo(BigDecimal.ZERO) > 0){
				// A pagar (open amt - descuento de payment term)
				toPay = fac.getToPayAmt(true);
				// Si el monto a pagar es mayor a 
				if(toPay.compareTo(amt) > 0){
					currentManualAmt = amt;
				}
				else{
					currentManualAmt = toPay;
				}
				fac.setManualAmtClientCurrency(currentManualAmt);
				actualizarPagarConPagarCurrency(i, fac, (Integer) m_facturas.get(i).getItem(m_facturasTableModel.getCurrencyColIdx()), false);
				amt = amt.subtract(currentManualAmt);
			}
		}
		// Dejar en 0 las facturas restantes
		for (; i < totalInvoices; i++) {
			fac = (ResultItemFactura) m_facturas.get(i);
			fac.setManualAmount(BigDecimal.ZERO);
		}
	}

	/**
	 * Calcula la fecha de pago del cheque a partir del plazo de pago
	 * configurado del medio de pago
	 * 
	 * @param fechaEmi
	 *            fecha de emisión
	 * @param paymentMedium
	 *            medio de pago
	 * @return fecha de pago del cheque
	 */
	public Date getFechaPagoCheque(Date fechaEmi, MPOSPaymentMedium paymentMedium){
		Calendar dueCalendar = Calendar.getInstance();
    	dueCalendar.setTimeInMillis(fechaEmi.getTime());
    	String checkDeadLine = paymentMedium.getCheckDeadLine();
    	dueCalendar.add(Calendar.DATE, Integer.parseInt(checkDeadLine));
    	return dueCalendar.getTime();
	}

	/**
	 * @return el total de descuento/recargo de esquema de vencimientos. Si es
	 *         negativo es recargo, positivo descuento
	 */
	public BigDecimal getTotalPaymentTermDiscount(){
		return reciboDeCliente.getGlobalInvoice().getTotalPaymentTermDiscount();
	}

	/**
	 * @return value del proceso de impresión de facturas
	 */
	protected String getInvoiceReportValue(){
		return "Factura (Impresion)";
	}
	
	@Override
	public void printCustomDocuments(ASyncProcess asyncProcess){
		if(customInvoices == null || customInvoices.size() == 0)return;
		// Traer el id del proceso que se encarga de imprimir los comprobantes
		// facturas
		int defaultProcessID = DB.getSQLValue( null, "SELECT AD_Process_ID FROM AD_Process WHERE value='" + getInvoiceReportValue()+ "' " );
		if(defaultProcessID <= 0)return;
		int tableID = DB.getSQLValue( null, "SELECT ad_table_id FROM AD_Table WHERE tablename = 'C_Invoice'" );
		Integer processID = defaultProcessID;
		MDocType docType;
		ProcessInfo pi;
		MPInstance instance;
		// Imprimir los débitos y créditos creados
		for (MInvoice invoice : customInvoices) {
			// Imprimir la factura actual
			// Si necesita impresión fiscal significa que no se completó
			// anteriormente la factura, por lo que se debe completar y así
			// imprimir
			if (!needFiscalPrint(invoice)) {
	        	docType = new MDocType(m_ctx, invoice.getC_DocTypeTarget_ID(), null);
				processID = Util.isEmpty(docType.getAD_Process_ID(), true) ? defaultProcessID
						: docType.getAD_Process_ID();
				// Crear la instancia del proceso
	        	instance = new MPInstance(Env.getCtx(), processID, 0, null);
	            if( !instance.save()) {
	            	log.log(Level.SEVERE, "Error at mostrarInforme: instance.save()");
	                return;
	            }
	            // Crear el processinfo
	            pi = new ProcessInfo( "Factura",processID );
	            pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());
	            pi.setRecord_ID(invoice.getID());
	            pi.setAD_User_ID(instance.getAD_User_ID());
	            pi.setTable_ID(tableID);
	           
	            ProcessCtl worker = new ProcessCtl( asyncProcess, pi, null );
	            worker.start();
			}
		}
	}

	@Override
	public void doPostProcesarNormalCustom() throws Exception{
		// Completar los documentos custom realizados en el caso que requieran
		// impresión fiscal
		for (MInvoice invoice : customInvoices) {
			if (needFiscalPrint(invoice)) {
				invoice.setSkipAutomaticCreditAllocCreation(true);
				// Completar la factura
				processDocument(invoice, MInvoice.DOCACTION_Complete);
			}
		}
	}
	
	public void setAssumeGeneralDiscountAdded(boolean value) {
		reciboDeCliente.setAssumeGeneralDiscountAdded(value);
	}
	
	/**
	 * Subclasificación del tablemodel de ordenes de pago para adaptar nueva
	 * lógica de descuentos/recargos de esquemas de vencimiento
	 * 
	 * @author Equipo de Desarrollo Libertya - Matías Cap
	 */
	public class OpenInvoicesCustomerReceiptsTableModel extends OpenInvoicesTableModel {
		private boolean allowManualAmtEditable = true;
		public OpenInvoicesCustomerReceiptsTableModel() {
			super();
			
    		columnNames = new Vector<String>();

            columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"C_Invoice_ID" ));
            columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"C_InvoicePaySchedule_ID" ));
            columnNames.add( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
            columnNames.add( Msg.getElement( Env.getCtx(),"DocumentNo" ));
            columnNames.add( Msg.getElement( Env.getCtx(),"DateInvoiced" ));
            columnNames.add( Msg.translate( Env.getCtx(),"DueDate" ));
            columnNames.add( Msg.translate( Env.getCtx(),"Currency" ));
            columnNames.add( Msg.translate( Env.getCtx(),"GrandTotal" ));
            columnNames.add( Msg.translate( Env.getCtx(),"openAmt" ));
            columnNames.add( Msg.translate( Env.getCtx(),"GrandTotal" ).concat(" ").concat(mCurency.getISO_Code()));
            columnNames.add( Msg.translate( Env.getCtx(),"openAmt" ).concat(" ").concat(mCurency.getISO_Code()));
            columnNames.add( Msg.translate( Env.getCtx(),"DiscountSurchargeDue" ));
            columnNames.add( Msg.translate( Env.getCtx(),"IsExchange" ));
            // La columna toPay permite ingresar el monto en la moneda de la factura
            columnNames.add( Msg.translate( Env.getCtx(),"ToPay" ));
            // La columna toPayCurrency permite ingresar el monto en la moneda de la Compañia
            columnNames.add( Msg.translate( Env.getCtx(),"ToPay" ).concat(" ").concat(mCurency.getISO_Code()));
		}
		
		public int getOpenAmtColIdx() {
			return 8;
		}
		
		public int getIsExchange() {
			return 11;
		}
		
		public int getCurrencyColIdx() {
			return 12;
		}
		
		public int getOpenCurrentAmtColIdx() {
			return 10;
		}
		
		public int getIdColIdx() {
			return 0;
		}
		
		public int getInvoicePayScheduleColIdx() {
			return 1;
		}
		
		public int getDueDateColIdx() {
			return 5;
		}
		
		public int getDateInvoicedColIdx() {
			return 4;
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			if (column < getColumnCount() - 3)
				return super.isCellEditable(row, column);
			
			boolean editable = false;
			// Si es toPay o toPayCurrency se marca la columna como editable
			if ( ((column == getColumnCount() - 1) || (column == getColumnCount() - 2)) && (isAllowManualAmtEditable()) ){
				editable = true;
			}
			return editable;
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			if (column < getColumnCount() - 4)
				return super.getValueAt(row, column);
			
			BigDecimal value = null;
			// Si es toPayCurrency
			if(column == getColumnCount()-1){
				value = ((ResultItemFactura) item.get(row)).getManualAmtClientCurrency();
			}
			// Si es toPay
			else if(column == columnNames.size()-2){
				value = ((ResultItemFactura) item.get(row)).getManualAmount();
			}
			// HACK: Para descuento/recargo de esquema de vencimientos
			else if(column == columnNames.size()-4){
				value = ((ResultItemFactura) item.get(row)).getPaymentTermDiscount();
				if(value.compareTo(BigDecimal.ZERO) != 0){
					value = value.negate();
				}
			}
			// HACK: Para columna is exchange
			else if(column == columnNames.size()-3){
				return ((ResultItemFactura) item.get(row)).getIsexchange();
			}
			
			return value; 
		}
		
		@Override
		public void setValueAt(Object arg0, int row, int column) {
			if (column < getColumnCount() - 4)
				super.setValueAt(arg0, row, column);
			// Si es toPayCurrency
			if(column == getColumnCount()-1){
				((ResultItemFactura) item.get(row)).setManualAmtClientCurrency((BigDecimal)arg0);				
			}
			// Si es toPay
			else if(column == columnNames.size()-2){
				((ResultItemFactura) item.get(row)).setManualAmount((BigDecimal)arg0);
			}
			// HACK: Para descuento/recargo de esquema de vencimientos
			else if(column == columnNames.size()-4){
				((ResultItemFactura) item.get(row)).setPaymentTermDiscount((BigDecimal)arg0);
			}
			// HACK: Para columna is exchange
			else if(column == columnNames.size()-3){
				((ResultItemFactura) item.get(row)).setIsexchange(arg0.toString());
			}
			
			fireTableCellUpdated(row, column);
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if ((columnIndex != getColumnCount() - 4) && (columnIndex != getColumnCount() - 3)){
				return super.getColumnClass(columnIndex);
			}
			else{
				// Para columna is exchange
				if (columnIndex == getColumnCount() - 3){
					return String.class;
				}
				// Para columnas toPayCurrency, toPay y paymentTermDiscount
				else{
					return BigDecimal.class;
				}
			}
		}

		public void setAllowManualAmtEditable(boolean allowManualAmtEditable) {
			this.allowManualAmtEditable = allowManualAmtEditable;
		}

		public boolean isAllowManualAmtEditable() {
			return allowManualAmtEditable;
		}
	}

	protected String getAllocTypes()
	{
		return
			"(" +
			"'" + X_C_AllocationHdr.ALLOCATIONTYPE_CustomerReceipt + "'," + 
			"'" + X_C_AllocationHdr.ALLOCATIONTYPE_AdvancedCustomerReceipt + "'," +
			"'" + X_C_AllocationHdr.ALLOCATIONTYPE_SalesTransaction + "'" +
			")";
	}
	
	/**
	 * El metodo retorna true si:
	 * El campo AD_Field de la Tab 263 (Factura de Cliente) que está relacionado con la columna 
	 * llamada IsExchange, está activo y desplegado.
	 * El metodo retorna false en caso contrario.
	 */
	public boolean showColumnChange() {
		StringBuffer sql = new StringBuffer("SELECT * FROM AD_Field f INNER JOIN AD_Column c ON (c.AD_Column_ID = f.AD_Column_ID) WHERE (f.AD_Tab_ID=263) AND (c.columnname = 'IsExchange') AND (f.isactive = 'Y') AND (f.isdisplayed = 'Y')");

		try {
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return true;
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			log.log(Level.SEVERE, sql.toString(), e);
		}
		return false;
	}
	
	/**
	 * Validación utilizada para valores mostrados por el combo DocumentType de la Ventana Recibo de Cliente
	 */
	public String getDocumentTypeSqlValidation() {
		return " ((C_Doctype.ad_Org_ID = 0) OR (C_Doctype.ad_Org_ID = " + Env.getAD_Org_ID(m_ctx) + "))" + 
			   " AND (C_Doctype.IsReceiptSeq = 'Y') " +
			   " AND (C_Doctype.DocBaseType = 'ARR') ";
	}
	

	public String getBank(Integer value) {
		return ((new X_C_Bank(getCtx(), value, getTrxName())).getName()); 
	}
	
	@Override
	protected POCRType getPOCRType() {
		return POCRType.CUSTOMER_RECEIPT;
	}

	public void setCashID(Integer cashID) {
		this.cashID = cashID;
	}

	public Integer getCashID() {
		return cashID;
	}
	
	public void initCashID(){
		MPOSJournal currentPosJournal = MPOSJournal.getCurrent();
		Integer cashJournalID = null;
		if(currentPosJournal != null){
			cashJournalID = currentPosJournal.getC_Cash_ID();
		}
		setCashID(cashJournalID);
	}

}
