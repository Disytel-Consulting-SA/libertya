package org.openXpertya.apps.form;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.form.VModelHelper.ResultItem;
import org.openXpertya.apps.form.VModelHelper.ResultItemTableModel;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.AllocationGenerator.PaymentMediumInfo;
import org.openXpertya.model.AllocationGeneratorException;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MRole;
import org.openXpertya.model.PO;
import org.openXpertya.model.POCRGenerator;
import org.openXpertya.model.POCRGenerator.POCRType;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_DocType;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.GeneratorRetenciones;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

public class VOrdenPagoModel {

	public static final int PROCERROR_OK = 0;
	public static final int PROCERROR_INSUFFICIENT_INVOICES = 1;
	public static final int PROCERROR_PAYMENTS_AMT_MATCH = 2;
	public static final int PROCERROR_PAYMENTS_GENERATION = 3;
	public static final int PROCERROR_NOT_SELECTED_BPARTNER = 4;
	public static final int PROCERROR_DOCUMENTNO_NOT_SET = 5;
	public static final int PROCERROR_DOCUMENTNO_ALREADY_EXISTS = 6;
	public static final int PROCERROR_DOCUMENTTYPE_NOT_SET = 7;
	public static final int PROCERROR_BOTH_EXCHANGE_INVOICES = 8;
	public static final int PROCERROR_DOCUMENTNO_INVALID = 9;
	public static final int PROCERROR_DOCUMENTNO_ALREADY_EXISTS_IN_OTHER_PERIOD = 10;
	public static final int PROCERROR_UNKNOWN = -1;
	
    public static final String MIN_CHECK_DIFF_DAYS_PREFERENCE_NAME = "OP_MinCheckDays";

    public static final String ORG_DEFAULT_VALUE_PREFERENCE_NAME = "OP_AD_Org_ID";
    public static final String DOCTYPE_DEFAULT_VALUE_PREFERENCE_NAME = "OP_DocTypeKey";
    
	protected static CLogger log = CLogger.getCLogger(VOrdenPagoModel.class);

	public abstract class MedioPago {
		public static final String TIPOMEDIOPAGO_EFECTIVO = "E";
		public static final String TIPOMEDIOPAGO_TRANSFERENCIA = "T";
		public static final String TIPOMEDIOPAGO_CHEQUE = "C";
		public static final String TIPOMEDIOPAGO_CREDITORETENCION = "RC";
		public static final String TIPOMEDIOPAGO_CREDITO = "CM";
		public static final String TIPOMEDIOPAGO_PAGOANTICIPADO = "PA";
		public static final String TIPOMEDIOPAGO_EFECTIVOADELANTADO = "EA";
		public static final String TIPOMEDIOPAGO_CHEQUETERCERO = "CT";
		public static final String TIPOMEDIOPAGO_TARJETACREDITO = "TC";

		public abstract String getTipoMP();

		public abstract void setImporte(BigDecimal importe);

		// Este metodo retorna el importe en la moneda que fue cargado el MP
		// (USD, EUR, ARS, etc).
		public abstract BigDecimal getImporteMonedaOriginal();

		public abstract Timestamp getDateTrx();

		public abstract Timestamp getDateAcct();

		public abstract int getBankAccountID();

		public abstract void addToGenerator(POCRGenerator poGenerator);

		private boolean isSOTrx = false;

		private MPOSPaymentMedium paymentMedium;

		/**
		 * Monto real del pago (usable solamente para la funcionalidad de
		 * descuentos)
		 */
		private BigDecimal realAmt;

		/**
		 * ID de descuento interno (usable solamente para la funcionalidad de
		 * descuentos)
		 */
		private Integer discountInternalID;

		private MDiscountSchema discountSchemaToApply;

		public MedioPago() {
			super();
		}

		public MedioPago(boolean isSOTrx) {
			super();
			this.isSOTrx = isSOTrx;
		}

		// Este metodo retorna el importe en la moneda de la Compañia
		public BigDecimal getImporte() {
			// Es correcto que se convierta a la fecha Actual
			return MCurrency.currencyConvert(getImporteMonedaOriginal(),
					getMonedaOriginalID(), C_Currency_ID,
				//	new Timestamp(System.currentTimeMillis()),
					m_fechaTrx,
					Env.getAD_Org_ID(Env.getCtx()), m_ctx);
		}

		public void setProject(Integer projectId) {
			this.projectId = projectId;
		}

		public void setCampaign(Integer campaignId) {
			this.campaignId = campaignId;
		}

		public Integer getProject() {
			return projectId;
		}

		public Integer getCampaign() {
			return campaignId;
		}

		/*
		 * private MedioPago nuevoImporte(BigDecimal importe) { try { MedioPago
		 * nuevo = (MedioPago)clone(); nuevo.setImporte(importe); return nuevo;
		 * } catch (Exception e) {
		 * 
		 * } return null; }
		 */

		public Integer getMonedaOriginalID() {
			return monedaOriginalID;
		}

		public void setMonedaOriginalID(Integer monedaOriginalID) {
			this.monedaOriginalID = monedaOriginalID;
		}

		protected Object payment = null;

		private Integer projectId;
		private Integer campaignId;

		// Almacena el Currency_ID con el que creado el MP (USD, EUR, ARS, etc).
		public Integer monedaOriginalID = C_Currency_ID;

		public String toString() {
			String tipoStr;

			if (getTipoMP().equals(TIPOMEDIOPAGO_EFECTIVO)) {
				// Efectivo
				tipoStr = VModelHelper.GetReferenceValueTrlFromColumn(
						"C_Order", "PaymentRule", "B", "name");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_TRANSFERENCIA)) {
				// Transferencia Bancaria
				tipoStr = VModelHelper.GetReferenceValueTrlFromColumn(
						"C_Order", "PaymentRule", "Tr", "name");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_CHEQUE)) {
				// Cheque
				tipoStr = VModelHelper.GetReferenceValueTrlFromColumn(
						"C_Order", "PaymentRule", "S", "name");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_CREDITO)) {
				// Credito
				tipoStr = Msg.translate(getCtx(), "Credit");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_PAGOANTICIPADO)) {
				// Pago Anticipado
				tipoStr = getMsg("AdvancedPayment");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_EFECTIVOADELANTADO)) {
				tipoStr = getMsg("AdvancedPayment") + " (" + getMsg("Cash")
						+ ")";
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_CHEQUETERCERO)) {
				tipoStr = getMsg("ThirdPartyCheck");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_TARJETACREDITO)) {
				tipoStr = getMsg("CreditCard");
			} else {
				tipoStr = "";
			}

			tipoStr = tipoStr + " " + numberFormat(getImporte());
			if (getMonedaOriginalID() != C_Currency_ID) {
				tipoStr = tipoStr
						+ " ("
						+ numberFormat(getImporteMonedaOriginal())
						+ " "
						+ (new MCurrency(m_ctx, getMonedaOriginalID(), trxName))
								.getISO_Code() + " )";
			}
			return tipoStr;
		}

		/**
		 * @return the isSOTrx
		 */
		public boolean isSOTrx() {
			return isSOTrx;
		}

		/**
		 * @param isSOTrx
		 *            the isSOTrx to set
		 */
		public void setSOTrx(boolean isSOTrx) {
			this.isSOTrx = isSOTrx;
		}

		public void setPaymentMedium(MPOSPaymentMedium paymentMedium) {
			this.paymentMedium = paymentMedium;
		}

		public MPOSPaymentMedium getPaymentMedium() {
			return paymentMedium;
		}

		public void setRealAmt(BigDecimal realAmt) {
			this.realAmt = realAmt;
		}

		public BigDecimal getRealAmt() {
			return realAmt;
		}

		public void setDiscountInternalID(Integer discountInternalID) {
			this.discountInternalID = discountInternalID;
		}

		public Integer getDiscountInternalID() {
			return discountInternalID;
		}

		public void setDiscountSchemaToApply(
				MDiscountSchema discountSchemaToApply) {
			this.discountSchemaToApply = discountSchemaToApply;
		}

		public MDiscountSchema getDiscountSchemaToApply() {
			return discountSchemaToApply;
		}
	}

	public class MedioPagoEfectivo extends MedioPago {
		public int libroCaja_ID;
		public BigDecimal importe;

		public MedioPagoEfectivo() {

		}

		/**
		 * @param isSOTrx
		 */
		public MedioPagoEfectivo(boolean isSOTrx) {
			super(isSOTrx);
		}

		public MedioPagoEfectivo(int libroCaja_ID, BigDecimal importe,
				int monedaOriginalID) {
			super();
			this.libroCaja_ID = libroCaja_ID;
			this.importe = importe;
			this.monedaOriginalID = monedaOriginalID;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_EFECTIVO;
		}

		@Override
		public Timestamp getDateTrx() {
			/*
			return VModelHelper.getSQLValueTimestamp(null,
					" select statementdate from c_cash where c_cash_id = ? ",
					libroCaja_ID);
					*/
			return m_fechaTrx;
		}

		@Override
		public Timestamp getDateAcct() {
			return VModelHelper.getSQLValueTimestamp(null,
					" select dateacct from c_cash where c_cash_id = ? ",
					libroCaja_ID);
		}

		@Override
		public int getBankAccountID() {
			return -1;
		}

		public void setCashLine(MCashLine cashLine) {
			this.payment = cashLine;
		}

		public MCashLine getCashLine() {
			return (MCashLine) payment;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addCashLinePaymentMedium(getCashLine()
					.getC_CashLine_ID(), getImporteMonedaOriginal());
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return importe;
		}

	}

	public class MedioPagoTransferencia extends MedioPago {
		public int C_BankAccount_ID;
		public String nroTransf;
		public BigDecimal importe;
		public Timestamp fechaTransf;

		public MedioPagoTransferencia() {

		}

		/**
		 * @param isSOTrx
		 */
		public MedioPagoTransferencia(boolean isSOTrx) {
			super(isSOTrx);
		}

		public MedioPagoTransferencia(int bankAccount_ID, String nroTransf,
				BigDecimal importe, Timestamp fechaTransf) {
			super();
			C_BankAccount_ID = bankAccount_ID;
			this.nroTransf = nroTransf;
			this.importe = importe;
			this.fechaTransf = fechaTransf;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_TRANSFERENCIA;
		}

		@Override
		public Timestamp getDateTrx() {
			return fechaTransf;
		}

		@Override
		public Timestamp getDateAcct() {
			return m_fechaTrx;
		}
		
		public Timestamp getFechaTransf() {
			return fechaTransf;
		}

		@Override
		public int getBankAccountID() {
			return C_BankAccount_ID;
		}

		public void setPayment(MPayment pay) {
			this.payment = pay;
		}

		public MPayment getPayment() {
			return (MPayment) payment;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addPaymentPaymentMedium(getPayment().getC_Payment_ID(),
					getImporteMonedaOriginal());
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return importe;
		}

	}

	public class MedioPagoCheque extends MedioPago {

		public int chequera_ID;
		public String nroCheque;
		public BigDecimal importe;
		public Timestamp fechaEm;
		public Timestamp fechaPago;
		public String aLaOrden;
		public String banco;
		public Integer bancoID;
		public String cuitLibrador;
		public String descripcion;
		

		public MedioPagoCheque() {

		}

		/**
		 * @param isSOTrx
		 */
		public MedioPagoCheque(boolean isSOTrx) {
			super(isSOTrx);
		}

		public MedioPagoCheque(int chequera_ID, String nroCheque,
				BigDecimal importe, Timestamp fechaEm, Timestamp fechaPago,
				String laOrden) {
			super();
			this.chequera_ID = chequera_ID;
			this.nroCheque = nroCheque;
			this.importe = importe;
			this.fechaEm = fechaEm;
			this.fechaPago = fechaPago;
			aLaOrden = laOrden;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_CHEQUE;
		}

		@Override
		public Timestamp getDateTrx() {
			return m_fechaTrx;
		}

		@Override
		public Timestamp getDateAcct() {
			return m_fechaTrx;
		}

		@Override
		public int getBankAccountID() {
			// Para cobros de clientes, la chequera_ID tiene el ID de
			// BankAccount.
			if (isSOTrx())
				return chequera_ID;
			else
				return DB
						.getSQLValue(
								null,
								" select C_BankAccount_ID from C_BankAccountDoc where C_BankAccountDoc_ID = ? ",
								chequera_ID);
		}

		public void setPayment(MPayment pay) {
			this.payment = pay;
		}

		public MPayment getPayment() {
			return (MPayment) payment;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addPaymentPaymentMedium(getPayment().getC_Payment_ID(),
					getImporteMonedaOriginal());
		}

		/**
		 * @return Devuelve la fecha de Pago/Vto del cheque
		 */
		public Timestamp getDueDate() {
			return fechaPago;
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return importe;
		}

	}

	public class MedioPagoCreditoRetencion extends MedioPago {
		public int C_Invoice_ID = 0; // Credito_proveedor
		private MInvoice Credito_proveedor = null;

		public MedioPagoCreditoRetencion(MInvoice invoice) {
			this.C_Invoice_ID = invoice.getC_Invoice_ID();
			this.Credito_proveedor = invoice;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_CREDITORETENCION;
		}

		@Override
		public BigDecimal getImporte() {
			return Credito_proveedor.getTotalLines();
		}

		@Override
		public void setImporte(BigDecimal importe) {

		}

		@Override
		public Timestamp getDateTrx() {
			return Credito_proveedor.getDateInvoiced();
		}

		@Override
		public Timestamp getDateAcct() {
			return Credito_proveedor.getDateAcct();
		}

		@Override
		public int getBankAccountID() {
			return -1;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addInvoicePaymentMedium(C_Invoice_ID,
					getImporteMonedaOriginal());
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return getImporte();
		}

	}

	public class MedioPagoCredito extends MedioPago {

		private int C_invoice_ID;
		private BigDecimal importe = BigDecimal.ZERO;
		private BigDecimal availableAmt = BigDecimal.ZERO;
		private String docTypeName = null;

		public MedioPagoCredito() {
			super();
		}

		public MedioPagoCredito(boolean isSOTrx) {
			super(isSOTrx);
		}

		@Override
		public int getBankAccountID() {
			return -1;
		}

		@Override
		public Timestamp getDateAcct() {
			return VModelHelper.getSQLValueTimestamp(getTrxName(),
					" select dateacct from c_invoice where c_invoice_id = ? ",
					C_invoice_ID);
		}

		@Override
		public Timestamp getDateTrx() {
		/*	return VModelHelper.getSQLValueTimestamp(getTrxName(),
					" select datetrx from c_invoice where c_invoice_id = ? ",
					C_invoice_ID);
					*/
			return m_fechaTrx;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_CREDITO;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		/**
		 * @return the c_invoice_ID
		 */
		public int getC_invoice_ID() {
			return C_invoice_ID;
		}

		/**
		 * @param c_invoice_id
		 *            the c_invoice_ID to set
		 */
		public void setC_invoice_ID(int c_invoice_id) {
			if (c_invoice_id != C_invoice_ID)
				docTypeName = VModelHelper
						.getSQLValueString(
								getTrxName(),
								"select dt.printname from c_invoice i inner join c_doctype dt on (i.c_doctype_id=dt.c_doctype_id) where i.c_invoice_id = ?",
								c_invoice_id);

			C_invoice_ID = c_invoice_id;
		}

		/**
		 * @return the availableAmt
		 */
		public BigDecimal getAvailableAmt() {
			return availableAmt;
		}

		/**
		 * @param availableAmt
		 *            the availableAmt to set
		 */
		public void setAvailableAmt(BigDecimal availableAmt) {
			this.availableAmt = availableAmt;
		}

		@Override
		public String toString() {
			return docTypeName + " " + numberFormat(getImporte());
		}

		public void validate() throws Exception {
			if (getImporte().compareTo(BigDecimal.ZERO) <= 0)
				throw new Exception("@NoAmountError@");
			if (getImporte().compareTo(getAvailableAmt()) > 0)
				throw new Exception("@AmountGreatherThanAvalaibleError@");
			if (findMedioPago(MedioPagoCredito.class, getC_invoice_ID()) != null)
				throw new Exception(Msg.getMsg(getCtx(),
						"POPaymentExistsError", new Object[] {
								getMsg("Credit"), getMsg("Payment") }));
			// No se puede agregar un crédito cuando no concuerdan los paymentrule de los débitos
			String invoicePaymentRule = getInvoicePaymentRule();
			if(!getPaymentRule().equals(invoicePaymentRule))
				throw new InterruptedException(Msg.getMsg(getCtx(), "NotAllowedAllocateCreditDiffPaymentRule",
						new Object[] {
								MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
										invoicePaymentRule),
								MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
										getPaymentRule()) }));
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addInvoicePaymentMedium(getC_invoice_ID(),
					getImporteMonedaOriginal());
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return importe;
		}
		
		public String getInvoicePaymentRule() {
			return VModelHelper.getSQLValueString(getTrxName(),
					" select paymentrule from c_invoice where c_invoice_id = ? ",
					C_invoice_ID);
		}

	}

	public class MedioPagoTarjetaCredito extends MedioPago {
		private MEntidadFinanciera entidadFinanciera;
		private MEntidadFinancieraPlan entidadFinancieraPlan;
		private BigDecimal amt;
		private String couponNo;
		private String creditCardNo;
		private int cuotasCount;
		private BigDecimal cuotaAmt;
		private MPayment payment;
		private String bank;
		private String accountName;

		@Override
		public int getBankAccountID() {
			return getEntidadFinanciera().getC_BankAccount_ID();
		}

		@Override
		public Timestamp getDateAcct() {
			return m_fechaTrx;
		}

		@Override
		public Timestamp getDateTrx() {
			//return today;
			return m_fechaTrx;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_TARJETACREDITO;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addPaymentPaymentMedium(getPayment().getC_Payment_ID(),
					getImporteMonedaOriginal());
		}

		@Override
		public void setImporte(BigDecimal importe) {
			amt = importe;
		}

		public MEntidadFinanciera getEntidadFinanciera() {
			return entidadFinanciera;
		}

		private void setEntidadFinanciera(MEntidadFinanciera entidadFinanciera) {
			this.entidadFinanciera = entidadFinanciera;
		}

		public void setEntidadFinancieraPlan(
				MEntidadFinancieraPlan entidadFinancieraPlan) {
			this.entidadFinancieraPlan = entidadFinancieraPlan;
		}

		public MEntidadFinancieraPlan getEntidadFinancieraPlan() {
			return entidadFinancieraPlan;
		}

		public void setPaymentMedium(MPOSPaymentMedium paymentMedium) {
			super.setPaymentMedium(paymentMedium);
			setEntidadFinanciera(new MEntidadFinanciera(getCtx(),
					paymentMedium.getM_EntidadFinanciera_ID(), getTrxName()));
		}

		public String getCreditCardType() {
			return entidadFinanciera.getCreditCardType();
		}

		public void setCouponNo(String couponNo) {
			this.couponNo = couponNo;
		}

		public String getCouponNo() {
			return couponNo;
		}

		public void setCreditCardNo(String creditCardNo) {
			this.creditCardNo = creditCardNo;
		}

		public String getCreditCardNo() {
			return creditCardNo;
		}

		public void setCuotasCount(int cuotasCount) {
			this.cuotasCount = cuotasCount;
		}

		public int getCuotasCount() {
			return cuotasCount;
		}

		public void setCuotaAmt(BigDecimal cuotaAmt) {
			this.cuotaAmt = cuotaAmt;
		}

		public BigDecimal getCuotaAmt() {
			return cuotaAmt;
		}

		public void setPayment(MPayment payment) {
			this.payment = payment;
		}

		public MPayment getPayment() {
			return payment;
		}

		public void setBank(String bank) {
			this.bank = bank;
		}

		public String getBank() {
			return bank;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		public String getAccountName() {
			return accountName;
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return amt;
		}

	}

	public class OpenInvoicesTableModel extends ResultItemTableModel {
		public OpenInvoicesTableModel() {
			VModelHelper.GetInstance().super();

			columnNames = new Vector<String>();

			columnNames.add("#$#"
					+ Msg.getElement(Env.getCtx(), "C_Invoice_ID"));
			columnNames.add("#$#"
					+ Msg.getElement(Env.getCtx(), "C_InvoicePaySchedule_ID"));
			columnNames.add(Msg.translate(Env.getCtx(), "AD_Org_ID"));
			columnNames.add(Msg.getElement(Env.getCtx(), "DocumentNo"));
			columnNames.add(Msg.translate(Env.getCtx(), "DueDate"));
			columnNames.add(Msg.translate(Env.getCtx(), "Currency"));
			columnNames.add(Msg.translate(Env.getCtx(), "GrandTotal"));
			columnNames.add(Msg.translate(Env.getCtx(), "openAmt"));
			columnNames.add(Msg.translate(Env.getCtx(), "GrandTotal")
					.concat(" ").concat(mCurency.getISO_Code()));
			columnNames.add(Msg.translate(Env.getCtx(), "openAmt").concat(" ")
					.concat(mCurency.getISO_Code()));
			// La columna toPay permite ingresar el monto en la moneda de la
			// factura
			columnNames.add(Msg.translate(Env.getCtx(), "ToPay"));
			// La columna toPayCurrency permite ingresar el monto en la moneda
			// de la Compañia
			columnNames.add(Msg.translate(Env.getCtx(), "ToPay").concat(" ")
					.concat(mCurency.getISO_Code()));
		}

		public int getOpenAmtColIdx() {
			return 7;
		}

		public int getOpenCurrentAmtColIdx() {
			return 9;
		}

		public int getCurrencyColIdx() {
			return 11;
		}

		public int getIdColIdx() {
			return 0;
		}

		public int getInvoicePayScheduleColIdx() {
			return 1;
		}

		public int getDueDateColIdx() {
			return 4;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if ((column != getColumnCount() - 1)
					&& (column != getColumnCount() - 2))
				return super.isCellEditable(row, column);
			// Si es toPay o toPayCurrency se marca la columna como editable
			return true;
		}

		@Override
		public Object getValueAt(int row, int column) {
			if ((column != getColumnCount() - 1)
					&& (column != getColumnCount() - 2))
				return super.getValueAt(row, column);
			else {
				// Si es toPay
				if (column == getColumnCount() - 2) {
					return ((ResultItemFactura) item.get(row))
							.getManualAmount();
				}
				// Si es toPayCurrency
				else {
					return ((ResultItemFactura) item.get(row))
							.getManualAmtClientCurrency();
				}
			}
		}

		@Override
		public void setValueAt(Object arg0, int row, int column) {
			if ((column != getColumnCount() - 1)
					&& (column != getColumnCount() - 2))
				super.setValueAt(arg0, row, column);
			else {
				// Si es toPay
				if (column == getColumnCount() - 2) {
					((ResultItemFactura) item.get(row))
							.setManualAmount((BigDecimal) arg0);
				}
				// Si es toPayCurrency
				else {
					((ResultItemFactura) item.get(row))
							.setManualAmtClientCurrency((BigDecimal) arg0);
				}
			}
			fireTableCellUpdated(row, column);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if ((columnIndex != getColumnCount() - 1)
					&& (columnIndex != getColumnCount() - 2))
				return super.getColumnClass(columnIndex);
			// Para columnas toPayCurrency y toPay
			return BigDecimal.class;
		}
	}

	public class ResultItemFactura extends ResultItem {

		private BigDecimal manualAmount = new BigDecimal(0);
		private BigDecimal manualAmtClientCurrency = new BigDecimal(0);
		private BigDecimal paymentTermDiscount = new BigDecimal(0);

		private String isexchange = "N";

		public ResultItemFactura(ResultSet rs) throws Exception {
			VModelHelper.GetInstance().super(rs);
		}

		public BigDecimal getManualAmount() {
			return manualAmount;
		}

		public void setManualAmount(BigDecimal manualAmount) {
			this.manualAmount = manualAmount;
		}

		public void setPaymentTermDiscount(BigDecimal paymentTermDiscount) {
			this.paymentTermDiscount = paymentTermDiscount;
		}

		public BigDecimal getPaymentTermDiscount() {
			return paymentTermDiscount;
		}

		public String getIsexchange() {
			return isexchange;
		}

		public void setIsexchange(String isexchange) {
			this.isexchange = isexchange;
		}

		public BigDecimal getManualAmtClientCurrency() {
			return manualAmtClientCurrency;
		}

		public void setManualAmtClientCurrency(
				BigDecimal manualAmtClientCurrency) {
			this.manualAmtClientCurrency = manualAmtClientCurrency;
		}

		public BigDecimal getToPayAmt(boolean withPaymentTermDiscount) {
			BigDecimal toPay = (BigDecimal) getItem(m_facturasTableModel
					.getOpenCurrentAmtColIdx());
			if (withPaymentTermDiscount) {
				toPay = toPay.subtract(getPaymentTermDiscount());
			}
			return toPay;
		}
	}

	// Se recorren las facturas y verificando que exista una tasa de cambio
	// existente.
	// En caso de no existir la tasa se retorna false
	public boolean validateConversionRate() {
		if (m_facturas != null) {
			for (ResultItem x : m_facturas) {
				if (((ResultItemFactura) x).getManualAmtClientCurrency()
						.compareTo(BigDecimal.ZERO) > 0
						|| ((ResultItemFactura) x).getManualAmount().compareTo(
								BigDecimal.ZERO) > 0) {
					int currency_ID_To = (Integer) ((ResultItemFactura) x)
							.getItem(m_facturasTableModel.getCurrencyColIdx());
					if (MCurrency.currencyConvert(new BigDecimal(1),
							C_Currency_ID, currency_ID_To,
							//new Timestamp(System.currentTimeMillis()), 0,
							m_fechaTrx,0,
							getCtx()) == null) {
						((ResultItemFactura) x)
								.setManualAmount(BigDecimal.ZERO);
						((ResultItemFactura) x)
								.setManualAmtClientCurrency(BigDecimal.ZERO);
						return false;
					}
				}
			}
		}
		return true;
	}

	//
	Properties m_ctx = Env.getCtx();
	protected String trxName = null;

	// Main
	public Timestamp m_fechaFacturas = null;
	protected Timestamp m_fechaTrx = Env.getTimestamp();
	public boolean m_allInvoices = true;
	public int C_BPartner_ID = 0;
	public int AD_Org_ID = 0;
	public MBPartner BPartner = null;
	private Integer paymentAmount = 0;
	public int C_Currency_ID = Env.getContextAsInt(Env.getCtx(),
			"$C_Currency_ID");
	public MCurrency mCurency = MCurrency.get(m_ctx, C_Currency_ID);
	private Integer projectID = 0;
	private Integer campaignID = 0;
	private BigDecimal exchangeDifference = BigDecimal.ZERO;

	protected boolean m_esPagoNormal = true;
	protected BigDecimal m_montoPagoAnticipado = null;

	private boolean m_actualizarFacturasAuto = true;

	// Medios de pago
	public Vector<MedioPago> m_mediosPago = new Vector<MedioPago>();
	protected POCRGenerator poGenerator;

	// Retenciones
	private GeneratorRetenciones m_retGen = null;
	public Vector<RetencionProcessor> m_retenciones = new Vector<RetencionProcessor>();

	// Table Facturas
	public OpenInvoicesTableModel m_facturasTableModel;
	// private Vector<BigDecimal> m_facturasManualAmounts = null;
	public Vector<ResultItem> m_facturas = null;
	// Payments creados
	private HashMap<Integer, MPayment> mpayments = new HashMap<Integer, MPayment>();
	private Map<Integer, MCashLine> mCashLines = new HashMap<Integer, MCashLine>();
	private Map<PO, Object> aditionalWorkResults = new HashMap<PO, Object>();
	protected List<AllocationLine> onlineAllocationLines = new ArrayList<AllocationLine>();
	//
	public int m_newlyCreatedC_AllocationHeader_ID = -1;

	private String msgAMostrar;

	private Map<String, String> msgMap = new HashMap<String, String>();

	private int errorNo = 0;

	private boolean retencionIncludedInMedioPago = false;

	// Nro de documento seteado en la allocation
	private String documentNo;

	// Tipo de documento seteado en la allocation
	private Integer documentType;

	private String description = "";
	
	/** Perfil actual */
	private MRole role;
	
	private DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	/** Condición de comprobantes */
	private String paymentRule;
	
	public VOrdenPagoModel() {
		getMsgMap().put("TenderType", "TenderType");
		// initTrx(); <-- COMENTADO: La trx debe iniciarse al confirmar el pago
		// unicamente
		m_facturasTableModel = getInvoicesTableModel();
		setPoGenerator(new POCRGenerator(getCtx(), getPOCRType(), getTrxName()));
		setRole(MRole.get(getCtx(), Env.getAD_Role_ID(getCtx())));
	}

	/**
	 * @return un nuevo table model para la tabla de facturas
	 */
	protected OpenInvoicesTableModel getInvoicesTableModel() {
		return new OpenInvoicesTableModel();
	}

	public void dispose() {
		closeTrx();
	}

	public void initTrx() {
		closeTrx();
		trxName = super.toString() + "_" + Thread.currentThread().getId() + "_"
				+ System.currentTimeMillis();
		getTrx().start();
	}

	private void closeTrx() {
		// Se obtiene la transaccion actual en caso de exitir. Se indica que NO
		// se cree una nueva transacción.
		if (getTrxName() != null) {
			Trx trx = Trx.get(getTrxName(), false);
			if (trx != null)
				try {
					trx.close();
				} catch (Exception e) {
				}
		}
	}

	public String getTrxName() {
		return trxName;
		// return m_trx != null ? m_trx.getTrxName() : null;
	}

	public MedioPago getMedioPago(int idx) {
		return m_mediosPago.get(idx);
	}

	public MedioPagoEfectivo getNuevoMedioPagoEfectivo() {
		return new MedioPagoEfectivo(isSOTrx());
	}

	public MedioPagoTransferencia getNuevoMedioPagoTransferencia() {
		return new MedioPagoTransferencia(isSOTrx());
	}

	public MedioPagoCheque getNuevoMedioPagoCheque() {
		return new MedioPagoCheque(isSOTrx());
	}

	public MedioPagoCredito getNuevoMedioPagoCredito() {
		return new MedioPagoCredito(isSOTrx());
	}

	public String getEfectivoLibroCajaSqlValidation() {
		// Se permite agregar efectivo de cualquier caja, sin importar la moneda
		// Se agrega condición para agregar efectivo solo de cajas con misma fecha que la OP
		//return " C_Cash.DocStatus = 'DR' AND (C_Cash.C_Cashbook_ID IN (SELECT C_Cashbook_ID FROM C_Cashbook cb WHERE cb.C_Currency_ID = @C_Currency_ID@ AND isactive = 'Y')) ";
		return " C_Cash.DocStatus = 'DR' AND date_trunc('day', C_Cash.DateAcct) = date_trunc('day', '@Date@'::timestamp)";
	}

	public String getTransfCtaBancariaSqlValidation() {
		return " C_Currency_ID = @C_Currency_ID@ ";
	}

	public String getChequeChequeraSqlValidation() {
		return " EXISTS (SELECT * FROM C_BankAccount ba55 WHERE ba55.BankAccountType = 'C' AND ba55.C_Currency_ID = @C_Currency_ID@ AND ba55.C_BankAccount_ID = C_BankAccountDoc.C_BankAccount_ID) AND C_BankAccountDoc.PaymentRule = 'S' ";
	}

	public String getCreditSqlValidation() {
		return " C_Invoice.DocStatus IN ('CO','CL') "
				+ " AND C_Invoice.NotExchangeableCredit = 'N' "
				+ " AND EXISTS (SELECT C_DocType_ID FROM C_DocType dt WHERE C_Invoice.C_DocType_ID = dt.C_DocType_ID AND dt.Signo_IsSOTrx = "
				+ (getSignoIsSOTrx() * -1) + ") "
				+ " AND C_Invoice.C_BPartner_ID = @C_BPartner_ID@ "
				+ " AND C_Invoice.C_Currency_ID = @C_Currency_ID@ "
				+ " AND invoiceOpen(C_Invoice.C_Invoice_ID, null) > 0 "
				+ " AND C_Invoice.PaymentRule = '@PaymentRule@' ";
	}

	public String getCurrencySqlValidation() {
		return "c_currency_id IN "
				+ "(SELECT c_currency_id FROM C_Conversion_Rate cr WHERE (isActive = 'Y') AND (ad_client_id = "
				+ Env.getAD_Client_ID(m_ctx)
				+ ") AND ((ad_org_id = "
				+ Env.getAD_Org_ID(m_ctx)
				+ ") OR (ad_org_id = 0)) AND ((c_currency_id = "
				+ C_Currency_ID
				+ ") OR (c_currency_id_to = "
				+ C_Currency_ID
				+ "))"
				+ "UNION "
				+ "SELECT c_currency_id_to FROM C_Conversion_Rate cr WHERE (isActive = 'Y') AND (ad_client_id = "
				+ Env.getAD_Client_ID(m_ctx)
				+ ") AND ((ad_org_id = "
				+ Env.getAD_Org_ID(m_ctx)
				+ ") OR (ad_org_id = 0)) AND ((c_currency_id = "
				+ C_Currency_ID
				+ ") OR (c_currency_id_to = "
				+ C_Currency_ID
				+ "))) ";
	}
	
	public boolean validateCurrentConversionRate(Integer c_currency_id) {
		boolean ret = false;
		String sql = "SELECT " + c_currency_id + " IN (SELECT " + C_Currency_ID + " UNION "
				+ "SELECT c_currency_id FROM C_Conversion_Rate cr WHERE (isActive = 'Y') AND (ad_client_id = "
				+ Env.getAD_Client_ID(m_ctx)
				+ ") AND ((ad_org_id = "
				+ Env.getAD_Org_ID(m_ctx)
				+ ") OR (ad_org_id = 0)) AND ((c_currency_id = "
				+ C_Currency_ID
				+ ") OR (c_currency_id_to = "
				+ C_Currency_ID
				+ ")) AND (validfrom <= '" + m_fechaTrx + "') AND (validto >= '" + m_fechaTrx + "') "
				+ "UNION "
				+ "SELECT c_currency_id_to FROM C_Conversion_Rate cr WHERE (isActive = 'Y') AND (ad_client_id = "
				+ Env.getAD_Client_ID(m_ctx)
				+ ") AND ((ad_org_id = "
				+ Env.getAD_Org_ID(m_ctx)
				+ ") OR (ad_org_id = 0)) AND ((c_currency_id = "
				+ C_Currency_ID
				+ ") OR (c_currency_id_to = "
				+ C_Currency_ID
				+ ")) AND (validfrom <= '" + m_fechaTrx + "') AND (validto >= '" + m_fechaTrx + "')) ";
		
		CPreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), getTrxName());
			rs = ps.executeQuery();
			while (rs.next()) {
				ret = rs.getBoolean(1);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error al buscar pagos. ", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.log(Level.SEVERE, "", e);
			}
		}
		return ret;
		
	}

	public void setFechaTablaFacturas(Timestamp fecha, boolean all) {
		m_fechaFacturas = fecha;
		m_allInvoices = all;
		if (m_actualizarFacturasAuto)
			actualizarFacturas();
	}
	
	public void setFechaOP(Timestamp fecha) {
				m_fechaTrx = fecha;
	}
	
	public Timestamp getFechaOP() {
		return m_fechaTrx;
	}

	/**
	 * Actualizo la info de la entidad comercial
	 * 
	 * @param bPartnerID
	 *            id de nueva la entidad comercial, null caso que no exista
	 *            alguna
	 */
	public void updateBPartner(Integer bPartnerID) {
		if (bPartnerID == null)
			bPartnerID = 0;
		// Actualizo las facturas
		setBPartnerFacturas(bPartnerID);
	}

	public void setBPartnerFacturas(int C_BPartner_ID) {
		this.C_BPartner_ID = C_BPartner_ID;

		if (C_BPartner_ID > 0) {
			this.BPartner = new MBPartner(m_ctx, C_BPartner_ID, getTrxName());
		} else {
			this.BPartner = null;
			m_facturas.clear();
			m_mediosPago.clear();
			m_retenciones.clear();
			reset();
			// initTrx(); <-- COMENTADO: La trx debe iniciarse al confirmar el
			// pago unicamente
		}

		if (m_actualizarFacturasAuto)
			actualizarFacturas();
	}

	public void setOrgId(int AD_Org_ID) {
		this.AD_Org_ID = AD_Org_ID;
	}

	public void setPagoNormal(boolean pagoNormal, BigDecimal montoAnticipado) {
		m_esPagoNormal = pagoNormal;
		m_montoPagoAnticipado = montoAnticipado;
	}

	/**
	 * 
	 * @return En caso de pago normal, la suma de los montos a pagar de cada
	 *         factura. Sino, el monto ingresado manualmente.
	 */
	public BigDecimal getSumaTotalPagarFacturas() {
		BigDecimal suma = new BigDecimal(0);

		if (m_esPagoNormal) {
			if (m_facturas != null) {
				for (ResultItem x : m_facturas)
					suma = suma.add(((ResultItemFactura) x)
							.getManualAmtClientCurrency());
			}
		} else {
			suma = m_montoPagoAnticipado;
		}

		return suma;
	}

	public void setActualizarFacturasAuto(boolean actualizarFacturasAuto) {
		m_actualizarFacturasAuto = actualizarFacturasAuto;
	}

	/**
	 * Actualiza el modelo de la tabla de facturas
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

		if ((!m_esPagoNormal)
				|| (m_fechaFacturas == null || C_BPartner_ID == 0)) {

			// Si es pago adelantado, no muestra ninguna factura.

			m_facturasTableModel.fireChanged(false);
			return;
		}

		// paymenttermduedate

		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT c_invoice_id, 0, orgname, documentno,max(duedate) as duedatemax, currencyIso, grandTotal, openTotal,  sum(convertedamt) as convertedamtsum, sum(openamt) as openAmtSum, isexchange, C_Currency_ID, paymentrule FROM ");
		sql.append("  (SELECT i.C_Invoice_ID, i.C_InvoicePaySchedule_ID, org.name as orgname, i.DocumentNo, coalesce(i.duedate,dateinvoiced) as DueDate, cu.iso_code as currencyIso, i.grandTotal, invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)) as openTotal, "); // ips.duedate
		sql.append("    abs(currencyConvert( i.GrandTotal, i.C_Currency_ID, ?, '"+ m_fechaTrx +"'::date, null, i.AD_Client_ID, i.AD_Org_ID)) as ConvertedAmt, isexchange, ");
		sql.append("    currencyConvert( invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)), i.C_Currency_ID, ?, '"+ m_fechaTrx +"'::date, null, i.AD_Client_ID, i.AD_Org_ID) AS openAmt, i.C_Currency_ID, i.paymentrule ");
		sql.append("  FROM c_invoice_v AS i ");
		sql.append("  LEFT JOIN ad_org org ON (org.ad_org_id=i.ad_org_id) ");
		sql.append("  LEFT JOIN c_invoicepayschedule AS ips ON (i.c_invoicepayschedule_id=ips.c_invoicepayschedule_id) ");
		sql.append("  INNER JOIN C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
		sql.append("  LEFT JOIN C_Currency cu ON (cu.C_Currency_ID=i.C_Currency_ID) ");
		sql.append("  WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
		//sql.append("    AND i.IsSOTRx = '" + getIsSOTrx()+ "' ");
		sql.append("	AND GrandTotal <> 0.0 ");
		sql.append("	AND C_BPartner_ID = ? ");
		sql.append("    AND dt.signo_issotrx = " + getSignoIsSOTrx());

		if (AD_Org_ID != 0)
			sql.append("  AND i.ad_org_id = ?  ");
		
		sql.append(" AND i.paymentRule = '").append(getPaymentRule()).append("' ");

		// Cadenas de autorización
		sql.append(" AND (i.authorizationchainstatus is null OR i.authorizationchainstatus = '")
				.append(MInvoice.AUTHORIZATIONCHAINSTATUS_Authorized).append("') ");
		
		sql.append("  ORDER BY org.name ASC, i.c_invoice_id, i.DocumentNo ASC, DueDate ASC ) as openInvoices ");
		sql.append(" GROUP BY c_invoice_id, orgname, documentno, currencyIso, grandTotal, openTotal, c_invoicepayschedule_id, isexchange, C_Currency_ID, paymentrule ");
		sql.append(" HAVING sum(opentotal) > 0.0 ");
		if (!m_allInvoices)
			sql.append("  AND ( max(duedate) IS NULL OR max(duedate) <= ? ) ");

		sql.append(" ORDER BY max(DueDate)");

		CPreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = DB.prepareStatement(sql.toString(), getTrxName());

			ps.setInt(i++, C_Currency_ID);
			ps.setInt(i++, C_Currency_ID);
			ps.setInt(i++, C_BPartner_ID);

			if (AD_Org_ID != 0)
				ps.setInt(i++, AD_Org_ID);

			if (!m_allInvoices)
				ps.setTimestamp(i++, m_fechaFacturas);

			rs = ps.executeQuery();
			// int ultimaFactura = -1;
			while (rs.next()) {
				ResultItemFactura rif = new ResultItemFactura(rs);
				int facId = ((Integer) rif.getItem(m_facturasTableModel
						.getIdColIdx()));
				// if (facId != ultimaFactura) {
				m_facturas.add(rif);
				// ultimaFactura = facId;
				// }
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error al actualizar facturas. ", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				log.log(Level.SEVERE, "", e);
			}
		}

		m_facturasTableModel.fireChanged(false);

	}

	// Added by Lucas Hernandez - Kunan
	public boolean buscarPagos(Integer bpartner) {

		boolean res = false;
		int cantidadPagos = 0;

		if(BPartner != null && !BPartner.isSearchUnallocatedPayments()){
			return res;
		}
		
		for (int i = 0; i < 4; i++) {
			StringBuffer sql = new StringBuffer();
			if (i == 0) {
				sql.append(" SELECT COUNT(*)");
				sql.append(" FROM libertya.c_payment i");
				sql.append(" INNER JOIN libertya.C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
				sql.append(" WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
				sql.append(" AND i.C_BPartner_ID = ? ");
				sql.append(" AND dt.DocTypeKey = 'VP'");
				sql.append(" AND libertya.paymentavailable(i.c_payment_id) > 0 ");
				//sql.append(" AND i.ad_org_id = ?  ");
			}
			if (i == 1) {
				sql.append(" SELECT COUNT(*)");
				sql.append(" FROM libertya.c_invoice i");
				sql.append(" INNER JOIN libertya.C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
				sql.append(" WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
				sql.append(" AND i.C_BPartner_ID = ? ");
				sql.append(" AND dt.DocBaseType = 'APC'");
				sql.append(" AND libertya.invoiceopen(i.c_invoice_id,null) > 0");
				//sql.append(" AND i.ad_org_id = ?  ");
				sql.append(" AND dt.signo_issotrx=1 ");
			}
			if (i == 2) {
				sql.append(" SELECT COUNT(*)");
				sql.append(" FROM libertya.c_cashline i");
				sql.append(" WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
				sql.append(" AND i.C_BPartner_ID = ? ");
				sql.append(" AND SIGN(i.amount)<0 ");
				sql.append(" AND libertya.cashlineavailable(i.c_cashline_id) <> 0 ");
				//sql.append(" AND i.ad_org_id = ?  ");
			}
			if (i==3){
				// Se agrega consulta para saber si existen notas de débitos de cliente a proveedor sin cancelar
				sql.append(" SELECT COUNT(*) ");
				sql.append(" FROM libertya.c_invoice i ");
				sql.append(" INNER JOIN libertya.C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
				sql.append(" WHERE i.DocStatus IN ('CO', 'CL') ");
				sql.append(" AND i.C_BPartner_ID = ? ");
				sql.append(" AND dt.DocBaseType = 'ARI' ");
				sql.append(" AND dt.DocTypeKey like 'CDN%' ");
				sql.append(" AND libertya.invoiceopen(i.c_invoice_id,null) > 0");
				//sql.append(" AND i.ad_org_id = ?  ");
			}
			CPreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = DB.prepareStatement(sql.toString(), getTrxName());
				ps.setInt(1, bpartner);
				//ps.setInt(2, AD_Org_ID);
				rs = ps.executeQuery();
				while (rs.next()) {
					cantidadPagos += rs.getInt(1);
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error al buscar pagos. ", e);
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (ps != null)
						ps.close();
				} catch (Exception e) {
					log.log(Level.SEVERE, "", e);
				}
			}
		}

		if (cantidadPagos > 0) {
			res = true;
		}
		setPaymentAmount(cantidadPagos);
		return res;
	}

	/**
	 * 
	 * @return la suma de todas las retenciones
	 */
	public BigDecimal getSumaRetenciones() {
		BigDecimal suma = new BigDecimal(0);

		for (RetencionProcessor r : m_retenciones)
			suma = suma.add(r.getAmount());

		return suma;
	}

	/**
	 * 
	 * @return la suma de todos los medios de pago
	 */
	public BigDecimal getSumaMediosPago() {
		BigDecimal suma = new BigDecimal(0);

		for (MedioPago mp : m_mediosPago)
			suma = suma.add(mp.getImporte());

		return suma;
	}

	/**
	 * 
	 * @return Total a pagar - retenciones - suma medios de pago
	 */
	public BigDecimal getSaldoMediosPago() {
		BigDecimal suma = getSumaTotalPagarFacturas();
		suma = suma == null ? BigDecimal.ZERO : suma;

		// suma =
		// suma.subtract(getSumaRetenciones()).subtract(getSumaMediosPago());
		suma = suma.subtract(getSumaMediosPago());
		if (!retencionIncludedInMedioPago) {
			suma = suma.subtract(getSumaRetenciones());
		}

		return suma;
	}

	public void setMsgAMostrar(String msgAMostrar) {
		this.msgAMostrar = msgAMostrar;
	}

	public String getMsgAMostrar() {
		return msgAMostrar;
	}

	public int doPreProcesar() {

		BigDecimal total = getSumaTotalPagarFacturas();

		// Tengo que poder pagar algo al menos

		if (total.signum() <= 0) {
			return PROCERROR_INSUFFICIENT_INVOICES;
		}

		if (C_BPartner_ID == 0) {
			return PROCERROR_NOT_SELECTED_BPARTNER;
		}
		
		if (documentType == null) {
			return PROCERROR_DOCUMENTTYPE_NOT_SET;
		}

		if (documentNo.length() == 0) {
			try{
				documentNo = getPoGenerator().getDocumentNo();
			} catch(Exception e){
				return PROCERROR_DOCUMENTNO_NOT_SET;
			}
			if(documentNo.length() == 0){
				return PROCERROR_DOCUMENTNO_NOT_SET;
			}
		}
		
		Boolean isReuseDocumentNo = false;
		if (getDocumentType() > 0) {
        	X_C_DocType docType = new X_C_DocType(getCtx(),getDocumentType(), getTrxName());
        	isReuseDocumentNo = docType.isReuseDocumentNo();
		}
		
		// Verificar que no exista el documentNo
		if (MAllocationHdr.documentNoAlreadyExists(null, documentNo, m_fechaTrx, getDocumentType(), getAllocTypes(), isSOTrx(), isReuseDocumentNo, m_ctx)) {
			// Si el Nro. de Documento existe, pero el tipo de documento permite la reutilización, consultamos la existencia de un recibo anulado pero fuera del período actual.
			if (isReuseDocumentNo && MAllocationHdr.documentNoAlreadyExistsInOtherPeriod(null, documentNo, m_fechaTrx, getDocumentType(), getAllocTypes(), getCtx())) {
    			return PROCERROR_DOCUMENTNO_ALREADY_EXISTS_IN_OTHER_PERIOD;
    		}
			
			return PROCERROR_DOCUMENTNO_ALREADY_EXISTS;
		}
		
		if (!MDocType.validateSequenceLength(documentType, documentNo, m_ctx, trxName)) {
			return PROCERROR_DOCUMENTNO_INVALID;
		}
				

		Vector<BigDecimal> manualAmounts = new Vector<BigDecimal>();
		Vector<BigDecimal> manualAmountsOriginal = new Vector<BigDecimal>();
		Vector<Integer> facturasProcesar = new Vector<Integer>();
		Vector<ResultItemFactura> resultsProcesar = new Vector<ResultItemFactura>();

		Vector<ResultItem> m_facturasAux = new Vector<ResultItem>();
		;
		ResultItemFactura fac;
		for (ResultItem f : m_facturas) {
			fac = (ResultItemFactura) f;
			if (fac.getManualAmtClientCurrency().signum() > 0) {
				m_facturasAux.add(fac);
			}
		}

		if (!m_facturasAux.isEmpty()) {
			fac = (ResultItemFactura) m_facturasAux.get(0);
			String isExchange = fac.getIsexchange();
			for (ResultItem f : m_facturasAux) {
				fac = (ResultItemFactura) f;
				if ((fac.getManualAmtClientCurrency().signum() > 0)
						&& (fac.getIsexchange().compareToIgnoreCase(isExchange) != 0)) {
					return PROCERROR_BOTH_EXCHANGE_INVOICES;
				}
			}
		}

		for (ResultItem f : m_facturas) {
			fac = (ResultItemFactura) f;
			if (fac.getManualAmtClientCurrency().signum() > 0) {
				facturasProcesar.add((Integer) fac.getItem(m_facturasTableModel
						.getIdColIdx()));
				manualAmounts.add(fac.getManualAmtClientCurrency());
				manualAmountsOriginal.add(fac.getManualAmount());
				resultsProcesar.add(fac);
			}
		}

		// Actualización del modelo en base a las facturas efectivas a pagar
		updateInvoicesModel(facturasProcesar, manualAmounts,
				manualAmountsOriginal, resultsProcesar);
		// Actualización de información adicional del modelo
		updateAditionalInfo();
		// Calcula las retenciones a aplicarle a la entidad comercial
		if (!isSOTrx() || m_retenciones == null || m_retenciones.size() == 0) {
		/*	m_retGen = new GeneratorRetenciones(C_BPartner_ID,
					facturasProcesar, manualAmounts, total, isSOTrx());
		*/
			m_retGen = new GeneratorRetenciones(C_BPartner_ID, facturasProcesar, manualAmounts, total, isSOTrx(), m_fechaTrx, getPaymentRule());
			m_retGen.setTrxName(getTrxName());
			calculateRetencions();
			m_retenciones = m_retGen.getRetenciones();
		}

		return PROCERROR_OK;
	}

	protected List<MedioPago> ordenarMediosPago() {
		Comparator<MedioPago> cmp = new Comparator<MedioPago>() {
			public int compare(MedioPago arg0, MedioPago arg1) {
				return arg0.getImporte().compareTo(arg1.getImporte());
			}
		};

		MedioPago[] mps = new MedioPago[m_mediosPago.size()];

		m_mediosPago.toArray(mps);

		Arrays.sort(mps, cmp);

		return Arrays.asList(mps);
	}

	protected List<ResultItemFactura> ordenarFacturas() {
		Comparator<ResultItem> cmp = new Comparator<ResultItem>() {
			public int compare(ResultItem arg0, ResultItem arg1) {
				BigDecimal b0 = ((ResultItemFactura) arg0)
						.getManualAmtClientCurrency();
				BigDecimal b1 = ((ResultItemFactura) arg1)
						.getManualAmtClientCurrency();

				return b0.compareTo(b1);
			}
		};

		ResultItemFactura[] rifs = new ResultItemFactura[m_facturas.size()];

		m_facturas.toArray(rifs);

		Arrays.sort(rifs, cmp);

		return Arrays.asList(rifs);
	}

	private void eliminarFacturasVacias(List<ResultItemFactura> facturas) {

		Vector<ResultItemFactura> tmp = new Vector<ResultItemFactura>();

		for (ResultItemFactura f : facturas) {
			if (f.getManualAmtClientCurrency().signum() > 0) {
				tmp.add(f);
			}
		}

		facturas.clear();
		facturas.addAll(tmp);
	}

	public BigDecimal currencyConvert(BigDecimal fromAmt, int fromCurency,
			int toCurrency, Timestamp convDate) throws Exception {
		// TODO: Si converto varias veces, que hago?
		// return Currency.convert(fromAmt, fromCurency, toCurrency, convDate,
		// 0, Env.getAD_Client_ID(m_ctx), Env.getAD_Org_ID(m_ctx));

		// HACK: Can't invoke Currency.convert directly !!

		CPreparedStatement pp = DB
				.prepareStatement(" SELECT currencyConvert(?, ?, ?, ?, ?, ?, ?) ");

		try {
			pp.setBigDecimal(1, fromAmt);
			pp.setInt(2, fromCurency);
			pp.setInt(3, toCurrency);
			pp.setTimestamp(4, convDate);
			pp.setInt(5, 0);
			pp.setInt(6, Env.getAD_Client_ID(m_ctx));
			pp.setInt(7, Env.getAD_Org_ID(m_ctx));

			ResultSet rs = pp.executeQuery();

			if (rs.next())
				return rs.getBigDecimal(1);

		} catch (Exception e) {
			log.log(Level.SEVERE, "currencyConvert", e);
		}

		return null;
	}

	protected int compararMontos(BigDecimal a, BigDecimal b) {
		/*
		 * plainly wrong, i suppose.
		 * 
		 * if (a.compareTo(b) == 0) return 0;
		 * 
		 * double d = a.subtract(b).doubleValue(); if (d > -0.01 && d < 0.01)
		 * return 0;
		 */

		return a.compareTo(b);
	}

	protected String getHdrAllocationType() {
		if (m_esPagoNormal)
			return (MAllocationHdr.ALLOCATIONTYPE_PaymentOrder);
		else
			return (MAllocationHdr.ALLOCATIONTYPE_AdvancedPaymentOrder);
	}

	protected String getAllocHdrDescription() {
		String name = getAllocHdrDescriptionMsg();
		return Msg.parseTranslation(getCtx(), name + " "
				+ getInvoicesDate().toString().substring(0, 10) + " [" + name
				+ " " + getHdrAllocationType() + "]");
	}

	/**
	 * Establece el signo (positivo o negativo) a la cantidad pasada como
	 * parámetro
	 * 
	 * @param cantidad
	 * @param signo
	 * @return la cantidad pasada como parámetro con el nuevo signo
	 * @author Cacho
	 */

	private BigDecimal establecerSigno(BigDecimal cantidad, BigDecimal signo) {
		// Multiplico la cantidad por el signo
		return cantidad.multiply(signo);
	}

	/**
	 * Genera los registros de MPayment o MCashLine, asigna los montos y los
	 * completa.
	 * 
	 * @param pagos
	 *            lista de pagos
	 * @return
	 * @throws Exception
	 */
	private boolean generarPagosDesdeMediosPagos(List<MedioPago> pagos,
			MAllocationHdr hdr) throws Exception {

		String HdrDescription = getAllocHdrDescription();
		boolean saveOk = true;
		String errorMsg = null;

		for (MedioPago mp : pagos) {

			MPayment pay;
			MCashLine line;

			BigDecimal convertedAmt = mp.getImporteMonedaOriginal();
			BigDecimal DiscountAmt = Env.ZERO;
			BigDecimal WriteoffAmt = Env.ZERO;
			BigDecimal OverunderAmt = Env.ZERO;

			if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_CREDITO)) {
				// No se debe generar ningún pago dado que es una nota de
				// crédito existente.
				MedioPagoCredito mpc = (MedioPagoCredito) mp;
				// No se puede agregar un crédito cuando no concuerdan los paymentrule de los débitos
				String invoicePaymentRule = mpc.getInvoicePaymentRule();
				if(!getPaymentRule().equals(invoicePaymentRule)){
					errorNo = PROCERROR_PAYMENTS_GENERATION;
					errorMsg = Msg.getMsg(getCtx(), "NotAllowedAllocateCreditDiffPaymentRule",
							new Object[] {
									MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
											invoicePaymentRule),
									MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
											getPaymentRule()) });
					throw new Exception(errorMsg);
				}
				setAllocationHDRaRetencionManual(hdr, mpc.C_invoice_ID);
			} else if (mp.getTipoMP().equals(
					MedioPago.TIPOMEDIOPAGO_CREDITORETENCION)) {
				// No se debe generar ningún pago dado que es un crédito de
				// retención
			} else if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_EFECTIVO)) {
				MedioPagoEfectivo mpe = (MedioPagoEfectivo) mp;

				// Hay que crear una linea de caja.

				MCash cash = new MCash(m_ctx, mpe.libroCaja_ID, getTrxName());
				
				// La fecha de la caja debe ser la misma fecha que la operación
				if(!TimeUtil.isSameDay(m_fechaTrx, cash.getDateAcct())){
					errorNo = PROCERROR_PAYMENTS_GENERATION;
					errorMsg = Msg.parseTranslation(m_ctx,
							"@NotAllowedCashWithDiferentDate@: \n - @DateTrx@ "
									+ getSimpleDateFormat().format(m_fechaTrx) + " \n - @C_Cash_ID@ " + cash.getName() + ". @Date@ "
									+ getSimpleDateFormat().format(cash.getDateAcct()) + ". @Amt@ = " + mpe.getImporte());
					throw new Exception(errorMsg);
				}
				
				line = new MCashLine(cash);

				line.setDescription(HdrDescription);

				/**
				 * 2010-08-05 - Modificado. Basandose en el campo isGenerated,
				 * ahora es posible indicar una linea de caja Invoice y no
				 * setearle el ID de factura. Es por esto que vuelve a indicarse
				 * el tipo a CASHTYPE_Invoice
				 */
				// Error: al asignar CASHTYPE_Invoice y no setearle el ID de
				// factura,
				// el beforeSave() de MCashLine asigna GeneralExpense como
				// CashType.
				line.setCashType(MCashLine.CASHTYPE_Invoice);
				// ***
				// line.setCashType(getCashType());

				/**
				 * 2010-08-05 - Comentado el siguiente bloque de código: Dado
				 * que un cashline puede pagar varias deudas, el dato
				 * C_Invoice_ID en C_CashLine lleva a confusiones. Se incorpora
				 * entonces una nueva pestaña en linea de caja para visualizar
				 * las cancelaciones relacionadas.
				 */
				// si es pago normal, debe indicarse en la linea de caja, que la
				// misma es de tipo Factura
				// if ((m_esPagoNormal) && (m_facturas != null))
				// {
				// // buscar la primer factura con monto mayor a cero
				// boolean found = false;
				// int invoiceID = -1;
				// for (int i=0; i < m_facturas.size() && !found; i++)
				// {
				// ResultItem x = m_facturas.get(i);
				// if
				// (((ResultItemFactura)x).getManualAmount().compareTo(Env.ZERO)
				// != 0)
				// {
				// found = true;
				// invoiceID =
				// (Integer)((ResultItemFactura)x).getItem(m_facturasTableModel.getIdColIdx());
				// }
				// }
				// line.setCashType(MCashLine.CASHTYPE_Invoice);
				// line.setC_Invoice_ID(invoiceID);
				// }

				if (mp.getProject() != null)
					line.setC_Project_ID(mp.getProject());

				line.setC_BPartner_ID(C_BPartner_ID);

				// Establezco el signo del monto
				convertedAmt = establecerSigno(convertedAmt, new BigDecimal(
						getSignoIsSOTrx()));

				asignarMontoPago(line, mp.getMonedaOriginalID(), convertedAmt,
						DiscountAmt, WriteoffAmt, OverunderAmt);

				line.setIgnoreAllocCreate(true); // Evita la creación del
													// allocation al completar
													// la línea.
				// No actualizo el saldo de la entidad comercial
				line.setUpdateBPBalance(false);

				addCustomCashLineInfo(line, mpe);

				// Guarda la linea de caja
				if (!line.save()) {
					errorMsg = CLogger.retrieveErrorAsString();
					saveOk = false;
					// Completa la línea
				} else if (!line.processIt(DocAction.ACTION_Complete)) {
					errorMsg = line.getProcessMsg();
					saveOk = false;
					// Guarda los cambios del procesamiento
				} else if (!line.save()) {
					errorMsg = CLogger.retrieveErrorAsString();
					saveOk = false;
				}

				// Si hay error levanta una excepción con el mensaje del logger
				// para
				// cortar la ejecución de todos los procesos.
				if (!saveOk) {
					errorNo = PROCERROR_PAYMENTS_GENERATION;
					throw new Exception(errorMsg);
				}

				// El amount y currency se lo asigno en la segunda vuelta

				mpe.setCashLine(line);
				getMCashLines().put(line.getID(), line);
			} else {

				// Crear un nuevo pago a partir del medio de pago (si es
				// anticipado NO debe crearse el pago, ya que el mismo existe)
				if (mp.getTipoMP().equals(
						MedioPago.TIPOMEDIOPAGO_PAGOANTICIPADO)
						|| mp.getTipoMP().equals(
								MedioPago.TIPOMEDIOPAGO_EFECTIVOADELANTADO))
					continue;

				pay = new MPayment(m_ctx, 0, getTrxName());

				if (mp.getTipoMP()
						.equals(MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) {
					MedioPagoChequeTercero mpct = (MedioPagoChequeTercero) mp;
					MPayment.copyValues(mpct.getChequeTerceroPayment(), pay);
					pay.setDocStatus(DocAction.STATUS_Drafted);
					pay.setDocAction(DocAction.ACTION_Complete);
				}

				//

				if (mp.getProject() != null)
					pay.setC_Project_ID(mp.getProject());

				if (mp.getCampaign() != null)
					pay.setC_Campaign_ID(mp.getCampaign());

				//

				pay.setDescription(HdrDescription);
				pay.setIsReceipt(false);
				pay.setC_DocType_ID(isSOTrx());
				pay.setC_BPartner_ID(C_BPartner_ID);

				// El amount y currency se lo asigno en la segunda vuelta
				// setAmount(C_Currency_ID, mp.getImporte());

				pay.setDateTrx(mp.getDateTrx());
				pay.setDateAcct(mp.getDateAcct());

				pay.setC_BankAccount_ID(mp.getBankAccountID());

				//

				if (!mp.getTipoMP().equals(
						MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) {
					String RoutingNo = VModelHelper
							.getSQLValueString(
									null,
									" select routingno from c_bank inner join c_bankaccount on (c_bank.c_bank_id=c_bankaccount.c_bank_id) where c_bankaccount.c_bankaccount_id = ? ",
									mp.getBankAccountID());
					String AccountNo = VModelHelper
							.getSQLValueString(
									null,
									" select AccountNo from c_bankaccount where c_bankaccount.c_bankaccount_id = ? ",
									mp.getBankAccountID());

					pay.setRoutingNo(RoutingNo);
					pay.setAccountNo(AccountNo);
				}

				if (mp.getTipoMP()
						.equals(MedioPago.TIPOMEDIOPAGO_TRANSFERENCIA)) {
					MedioPagoTransferencia mpt = (MedioPagoTransferencia) mp;

					pay.setTenderType(MPayment.TENDERTYPE_DirectDeposit);

					pay.setCheckNo(mpt.nroTransf); // Numero de cheque
					
					mpt.setPayment(pay);
				} else if (mp.getTipoMP()
						.equals(MedioPago.TIPOMEDIOPAGO_CHEQUE)) {
					MedioPagoCheque mpc = (MedioPagoCheque) mp;
					String sucursal = VModelHelper
							.getSQLValueString(
									null,
									" select AccountNo from c_bankaccount where c_bankaccount.c_bankaccount_id = ? ",
									mp.getBankAccountID());

					pay.setCheckNo(mpc.nroCheque); // Numero de cheque
					pay.setDateEmissionCheck(mpc.fechaEm); // Fecha de Emision
															// de Cheque
					pay.setMicr(sucursal + ";" + mp.getBankAccountID() + ";"
							+ mpc.nroCheque); // Sucursal; cta; No. cheque
					pay.setA_Name(mpc.aLaOrden); // Nombre

					pay.setTenderType(MPayment.TENDERTYPE_Check);
					pay.setA_Bank(mpc.banco);
					pay.setA_CUIT(mpc.cuitLibrador);

					if (mpc.descripcion != null && mpc.descripcion.length() > 0)
						pay.addDescription(mpc.descripcion);

					// Fecha Vto
					pay.setDueDate(mpc.getDueDate());
					mpc.setPayment(pay);
				} else if (mp.getTipoMP().equals(
						MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) {
					MedioPagoChequeTercero mpct = (MedioPagoChequeTercero) mp;
					if (mpct.description != null
							&& mpct.description.length() > 0)
						pay.addDescription(mpct.description);
					mpct.setPayment(pay);
					// Se cierra el cheque de tercero para que no vuelva a ser
					// utilizado
					MPayment chequeTercero = mpct.getChequeTerceroPayment();
					// Se relaciona el cheque de tercero en la columna de pago
					// original de la copia
					pay.setOriginal_Ref_Payment_ID(chequeTercero.getID());
					if (!chequeTercero.processIt(DocAction.ACTION_Close)) {
						errorNo = PROCERROR_PAYMENTS_GENERATION;
						throw new Exception(chequeTercero.getProcessMsg());
					} else if (!chequeTercero.save()) {
						errorNo = PROCERROR_PAYMENTS_GENERATION;
						throw new Exception(CLogger.retrieveErrorAsString());
					}
				}
				// Agregar información al payment en creación con el medio de
				// pago actual
				addCustomPaymentInfo(pay, mp);
				// --

				asignarMontoPago(pay, mp.getMonedaOriginalID(), convertedAmt,
						DiscountAmt, WriteoffAmt, OverunderAmt);

				// No actualizo el saldo de la entidad comercial
				pay.setUpdateBPBalance(false);
				// Guarda el pago
				if (!pay.save()) {
					errorMsg = CLogger.retrieveErrorAsString();
					saveOk = false;
					// Completa el pago
				} else if (!pay.processIt(DocAction.ACTION_Complete)) {
					errorMsg = pay.getProcessMsg();
					saveOk = false;
					// Guarda los cambios del procesamiento
				} else if (!pay.save()) {
					errorMsg = CLogger.retrieveErrorAsString();
					saveOk = false;
				}

				// Manejo correcto de errores. Levanta excepción y corta todo,
				// como debe ser.
				// Se asigna el errorNo para matener compatibilidad con la vieja
				// lógica de retornar
				// un nro de error en doPosProcesarXXX.
				if (!saveOk) {
					errorNo = PROCERROR_PAYMENTS_GENERATION;
					throw new Exception(errorMsg);
				}

				mpayments.put(pay.getID(), pay);
			}
		}

		return saveOk;
	}

	private void asignarMontoPago(MCashLine line, int pC_Currency_ID,
			BigDecimal convertedAmt, BigDecimal DiscountAmt,
			BigDecimal WriteoffAmt, BigDecimal OverunderAmt) {

		line.setC_Currency_ID(pC_Currency_ID);
		line.setAmount(convertedAmt);
		line.setDiscountAmt(DiscountAmt);
		line.setWriteOffAmt(WriteoffAmt);
		line.setIsGenerated(true);

	}

	private void asignarMontoPago(MPayment pay, int pC_Currency_ID,
			BigDecimal convertedAmt, BigDecimal DiscountAmt,
			BigDecimal WriteoffAmt, BigDecimal OverunderAmt) {

		pay.setAmount(pC_Currency_ID, convertedAmt);
		pay.setDiscountAmt(DiscountAmt);
		pay.setWriteOffAmt(WriteoffAmt);
		pay.setOverUnderAmt(OverunderAmt);

	}

	public int doPostProcesar() {

		BigDecimal saldoMediosPago = getSaldoMediosPago(); // Debe ser cero

		if (saldoMediosPago.abs().compareTo(
				new BigDecimal(MPreference
						.GetCustomPreferenceValue("AllowExchangeDifference"))) > 0)
			return PROCERROR_PAYMENTS_AMT_MATCH;

		int ret;

		if (m_esPagoNormal)
			ret = doPostProcesarNormal();
		else
			ret = doPostProcesarAdelantado();

		return ret;
	}

	private int doPostProcesarAdelantado() {
		errorNo = PROCERROR_OK;
		initTrx();
		Trx trx = getTrx();
		MAllocationHdr hdr = null;
		boolean saveOk = true;

		try {

			// 1. Ordenar los pagos y devolverlos
			Vector<MedioPago> pagos = new Vector<MedioPago>(ordenarMediosPago());

			// 2. Crear el generador de OPA y actualizar el encabezado de la
			// asignación creada
			getPoGenerator().setTrxName(getTrxName());
			hdr = getPoGenerator().createAllocationHdr();
			updateAllocationHdr(hdr);

			// 3. Generar los pagos
			if (saveOk && !generarPagosDesdeMediosPagos(pagos, hdr)) {
				saveOk = false;
				errorNo = PROCERROR_PAYMENTS_GENERATION;
			} else {
				// 4. Guardar Retenciones
				m_retGen.setTrxName(getTrxName());
				m_retGen.setProjectID(getProjectID());
				m_retGen.setCampaignID(getCampaignID());
				m_retGen.save(hdr);

				// 5. Agregar retenciones como medio de pago
				agregarRetencionesComoMediosPagos(pagos, hdr);
				// Agrego los medios de pagos al generador
				for (MedioPago pago : pagos) {
					pago.addToGenerator(getPoGenerator());
				}

				// Se crean las líneas de imputación entre las facturas y los
				// pagos
				getPoGenerator().generateLines();

				// 99. Completar HDR
				getPoGenerator().completeAllocation();

				// Realizar las tareas de cuenta corriente previas a terminar el
				// procesamiento
				performAditionalCurrentAccountWork();

				// Si estuvo todo bien entonces realizo las tareas posteriores a
				// terminar el procesamiento y confirmación de transacción
				afterProcessDocuments();

				if (saveOk && errorNo == PROCERROR_OK) {
					trx.commit();
					m_newlyCreatedC_AllocationHeader_ID = hdr
							.getC_AllocationHdr_ID();
				}
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "doPostProcesarAdelantado", e);
			errorNo = PROCERROR_UNKNOWN;
			setMsgAMostrar(e.getMessage());
		}

		if (!saveOk || errorNo != PROCERROR_OK) {
			getPoGenerator().reset();
			retencionIncludedInMedioPago = false;
			trx.rollback();
		}
		trx.close();
		trxName = null;
		return errorNo;
	}

	private void agregarRetencionesComoMediosPagos(Vector<MedioPago> pagos,
			MAllocationHdr hdr) throws Exception {

		String sql = " SELECT C_Invoice_ID FROM m_retencion_invoice WHERE C_AllocationHdr_ID = ? ";
		CPreparedStatement pp = DB.prepareStatement(sql, getTrxName());

		pp.setInt(1, hdr.getC_AllocationHdr_ID());

		ResultSet rs = pp.executeQuery();

		while (rs.next()) {
			MInvoice invoice = new MInvoice(m_ctx, rs.getInt(1), getTrxName());
			//SUR SOFTWARE - MODIFICACION PARA CONTABILIZAR EL COMPROBANTE DE RETENCION CON LA FECHA DEL RECIBO // O.PAGO 
			invoice.setDateAcct(this.m_fechaTrx);
			if(!invoice.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
			MedioPagoCreditoRetencion rc = new MedioPagoCreditoRetencion(
					invoice);
			Vector<MedioPago> mpe = new Vector<VOrdenPagoModel.MedioPago>();
			// Elimino el crédito si es Retención Manual.
			for (MedioPago mp : pagos) {
				if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_CREDITO)) {
					MedioPagoCredito mpc = (MedioPagoCredito) mp;
					if (mpc.C_invoice_ID == invoice.getC_Invoice_ID()) {
						// pagos.remove(mpc);
						mpe.add(mpc);
					}
				}
			}
			for (MedioPago mpc : mpe) {
				pagos.remove(mpc);
			}
			pagos.add(rc);
		}

		pp.close();
		rs.close();

		retencionIncludedInMedioPago = true;
	}

	private int doPostProcesarNormal() {
		initTrx();
		Trx trx = getTrx();
		MAllocationHdr hdr = null;

		boolean saveOk = true;
		errorNo = PROCERROR_UNKNOWN;
		Vector<MedioPago> pays = new Vector<VOrdenPagoModel.MedioPago>(
				getMediosPago());

		try {
			// 1. Crear débitos y créditos customs
			makeCustomDebitsCredits(pays);

			// 2. Se crea el generador de orden de pago.
			getPoGenerator().setTrxName(getTrxName());
			hdr = getPoGenerator().createAllocationHdr();

			// Se setean las propiedades del encabezado
			updateAllocationHdr(hdr);

			// 3. Retenciones
			m_retGen.setTrxName(getTrxName());
			m_retGen.setProjectID(getProjectID());
			m_retGen.setCampaignID(getCampaignID());
			m_retGen.save(hdr);

			// 4. Generar los pagos, completos.
			if (saveOk && !generarPagosDesdeMediosPagos(pays, hdr)) {
				saveOk = false;
				errorNo = PROCERROR_PAYMENTS_GENERATION;
			} else {
				// 5. Agregar las facturas de debito
				agregarRetencionesComoMediosPagos(pays, hdr);

				// 6. Guardar allocation lines online creadas
				processOnlineAllocationLines(hdr, pays, true);

				// 98. Completar HDR
				getPoGenerator().completeAllocation();

				// 99. Realizar operaciones custom al final del procesamiento
				doPostProcesarNormalCustom();

				// Realizar las tareas de cuenta corriente previas a terminar el
				// procesamiento
				performAditionalCurrentAccountWork();

				// Si estuvo todo bien entonces realizo las tareas posteriores a
				// terminar el procesamiento y confirmación de transacción
				afterProcessDocuments();
			}

			if (saveOk)
				saveOk = trx.commit();
		} catch (Exception e) {
			log.log(Level.SEVERE, "doPostProcesarNormal", e);
			saveOk = false;
			setMsgAMostrar(e.getMessage());
		}

		if (!saveOk) {
			getPoGenerator().reset();
			retencionIncludedInMedioPago = false;
			trx.rollback();
			trx.close();
		} else {
			if (trx.close())
				m_newlyCreatedC_AllocationHeader_ID = hdr
						.getC_AllocationHdr_ID();
		}
		trxName = null;
		if (saveOk) {
			errorNo = PROCERROR_OK;
		}

		return errorNo;
	}

	/**
	 * Realizar operaciones custom luego de completar el allocation, al final
	 * del procesamiento de la orden de pago
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	public void doPostProcesarNormalCustom() throws Exception {
		// Por ahora no hace nada aquí
	}

	public void mostrarInforme(ASyncProcess asyncProc, boolean printRetenciones) {

		if (m_newlyCreatedC_AllocationHeader_ID <= 0)
			return;

		int proc_ID = DB.getSQLValue(null,
				"SELECT AD_Process_ID FROM AD_Process WHERE value='"
						+ getReportValue() + "' ");

		if (proc_ID > 0) {

			MPInstance instance = new MPInstance(Env.getCtx(), proc_ID, 0, null);
			if (!instance.save()) {
				log.log(Level.SEVERE,
						"Error at mostrarInforme: instance.save()");
				return;
			}

			ProcessInfo pi = new ProcessInfo("Orden de Pago", proc_ID);
			pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());

			MPInstancePara ip;

			/*
			 * ip = new MPInstancePara( instance, 10 ); ip.setParameter(
			 * "C_Allocation_detail_v_ID",String.valueOf(
			 * m_newlyCreatedC_AllocationHeader_ID )); if( !ip.save()) {
			 * log.log(Level.SEVERE, "Error at mostrarInforme: ip.save()");
			 * return; }
			 */

			ip = new MPInstancePara(instance, 10);
			ip.setParameter("C_AllocationHdr_ID",
					String.valueOf(m_newlyCreatedC_AllocationHeader_ID));
			if (!ip.save()) {
				log.log(Level.SEVERE, CLogger.retrieveErrorAsString());
				return;
			}

			ip = new MPInstancePara(instance, 20);
			ip.setParameter("PrintRetentions", printRetenciones ? "Y" : "N");
			if (!ip.save()) {
				log.log(Level.SEVERE, CLogger.retrieveErrorAsString());
				return;
			}
			
			ProcessCtl worker = new ProcessCtl(asyncProc, pi, null);
			worker.start();
		}

		// Las subclases deberían poder imprimir los comprobantes que deseen
		printCustomDocuments(asyncProc);
	}

	private void setAllocationHDRaRetencionManual(MAllocationHdr hdr,
			int c_invoice_id) throws SQLException {
		// Seteo el allocationhdr de la factura pasada por parámetro.
		String sql;

		sql = "update m_retencion_invoice  " + " set c_allocationhdr_id = "
				+ hdr.getC_AllocationHdr_ID() + " where c_invoice_id = "
				+ c_invoice_id;
		DB.executeUpdate(sql, getTrxName());
	}

	public void actualizarPagarConPagarCurrency(int row, ResultItemFactura rif,
			int currency_ID_To, boolean useOpenAmtIfZero) {
		BigDecimal manualAmtClientCurrency = rif.getManualAmtClientCurrency();
		BigDecimal openAmt = ((m_facturas.get(row).getItem(m_facturasTableModel
				.getOpenCurrentAmtColIdx())) == null) ? BigDecimal.ZERO
				: (BigDecimal) m_facturas.get(row).getItem(
						m_facturasTableModel.getOpenCurrentAmtColIdx());
		// Sumar o restar algún monto custom
		openAmt = openAmt.subtract(rif.getPaymentTermDiscount());
		// Setear el monto pendiente de la factura si se indicó 0
//		Comentado: si al ser cero completa con el pendiente, nunca puede volver a setearse cero!
//		if (useOpenAmtIfZero && manualAmtClientCurrency.compareTo(BigDecimal.ZERO) == 0)
//			rif.setManualAmtClientCurrency(openAmt);
			
		if (manualAmtClientCurrency == null
				|| manualAmtClientCurrency.signum() < 0)
			rif.setManualAmtClientCurrency(BigDecimal.ZERO);
		else if (manualAmtClientCurrency.compareTo(openAmt) > 0)
			rif.setManualAmtClientCurrency(openAmt);
		BigDecimal manualAmt = MCurrency.currencyConvert(
				rif.getManualAmtClientCurrency(), C_Currency_ID,
			//	currency_ID_To, new Timestamp(System.currentTimeMillis()), 0,
					currency_ID_To, m_fechaTrx, 0,
				getCtx());
		rif.setManualAmount(manualAmt);
	}

	public void actualizarPagarCurrencyConPagar(int row, ResultItemFactura rif,
			int currency_ID_To, boolean useOpenAmtIfZero) {
		BigDecimal manualAmt = rif.getManualAmount();
		BigDecimal openAmt = (BigDecimal) m_facturas.get(row).getItem(
				m_facturasTableModel.getOpenAmtColIdx());
		// Sumar o restar algún monto custom
		BigDecimal paymentTermDiscount = MCurrency.currencyConvert(
				rif.getPaymentTermDiscount(), C_Currency_ID, currency_ID_To,
				new Timestamp(System.currentTimeMillis()), 0, getCtx());
		//	m_fechaTrx, 0, getCtx());
		openAmt = openAmt.subtract(paymentTermDiscount);
		// Setear el monto pendiente de la factura si se indicó 0
//		Comentado: si al ser cero completa con el pendiente, nunca puede volver a setearse cero!
//		if (useOpenAmtIfZero && manualAmt.compareTo(BigDecimal.ZERO) == 0)
//			rif.setManualAmount(openAmt);
		
		if (manualAmt == null || manualAmt.signum() < 0)
			rif.setManualAmount(BigDecimal.ZERO);
		else if (manualAmt.compareTo(openAmt) > 0)
			rif.setManualAmount(openAmt);
		BigDecimal manualAmtClientCurrency = MCurrency.currencyConvert(
				rif.getManualAmount(), currency_ID_To, C_Currency_ID,
				//new Timestamp(System.currentTimeMillis()), 0, getCtx());
				m_fechaTrx, 0, getCtx());
		rif.setManualAmtClientCurrency(manualAmtClientCurrency);
	}

	public NumberFormat getNumberFormat() {
		NumberFormat nf = NumberFormat.getNumberInstance();

		nf.setMinimumFractionDigits(2);
		nf.setRoundingMode(RoundingMode.HALF_EVEN);
		if (mCurency != null)
			nf.setMinimumFractionDigits(mCurency.getStdPrecision());

		return nf;
	}

	public String numberFormat(BigDecimal nn) throws IllegalArgumentException {
		if (nn != null) {
			NumberFormat nf = getNumberFormat();
			return nf.format(nn);
		}
		return "";
	}

	public BigDecimal numberParse(String nn) throws ParseException {
		NumberFormat nf = getNumberFormat();
		return new BigDecimal(nf.parse(nn).toString());
	}

	public BigDecimal numberParseOrZero(String nn) {
		try {
			return numberParse(nn);
		} catch (Exception e) {
			return new BigDecimal(0);
		}
	}

	protected Trx getTrx() {
		return Trx.get(getTrxName(), true);
	}

	public String getIsSOTrx() {
		return "N";
	}

	protected int getSignoIsSOTrx() {
		return -1;
	}

	protected void calculateRetencions() {
		m_retGen.evaluarRetencion();
	}

	/**
	 * @return the bPartner
	 */
	public MBPartner getBPartner() {
		return BPartner;
	}

	/**
	 * @return the msgMap
	 */
	public Map<String, String> getMsgMap() {
		return msgMap;
	}

	protected GeneratorRetenciones getGeneratorRetenciones() {
		return m_retGen;
	}

	/**
	 * @return the m_retenciones
	 */
	protected Vector<RetencionProcessor> getRetenciones() {
		return m_retenciones;
	}

	/**
	 * @return the m_retenciones
	 */
	protected Vector<MedioPago> getMediosPago() {
		return m_mediosPago;
	}

	public boolean isSOTrx() {
		return "Y".equals(getIsSOTrx());
	}

	protected Timestamp getInvoicesDate() {
		return m_fechaFacturas;
	}

	public boolean isNormalPayment() {
		return m_esPagoNormal;
	}

	public Properties getCtx() {
		return m_ctx;
	}

	protected String getCashType() {
		return MCashLine.CASHTYPE_GeneralExpense;
	}

	public BigDecimal getCreditAvailableAmt(int invoiceID) {
		try {
			BigDecimal AvailableamountToConvert = (BigDecimal) DB.getSQLObject(
					null, "SELECT invoiceOpen(?, 0)",
					new Object[] { invoiceID });
			return currencyConvert(AvailableamountToConvert, DB.getSQLValue(
					null,
					"SELECT C_Currency_ID From C_Invoice where C_Invoice_ID = "
							+ invoiceID), getC_Currency_ID(),
							//new Timestamp(new java.util.Date().getTime()));
							m_fechaTrx);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return Retorna la clave de busca del Informe y Proceso que muestra la
	 *         Orden de pago.
	 */
	public String getReportValue() {
		return "Orden de Pago";
	}

	public class MedioPagoAdelantado extends MedioPago {
		public int C_Payment_ID;
		public BigDecimal importe;

		public MedioPagoAdelantado(int payment_ID, BigDecimal importe,
				int monedaOriginalID) {
			super();
			C_Payment_ID = payment_ID;
			this.importe = importe;
			this.monedaOriginalID = monedaOriginalID;
		}

		public int getC_Payment_ID() {
			return C_Payment_ID;
		}

		public void setC_Payment_ID(int payment_ID) {
			C_Payment_ID = payment_ID;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public int getBankAccountID() {
			return DB
					.getSQLValue(
							null,
							" SELECT C_BankAccount_ID FROM c_payment WHERE C_Payment_ID = ? ",
							C_Payment_ID);
		}

		@Override
		public Timestamp getDateAcct() {
			return VModelHelper.getSQLValueTimestamp(null,
					" SELECT dateacct FROM c_payment WHERE C_Payment_ID = ? ",
					C_Payment_ID);
		}

		@Override
		public Timestamp getDateTrx() {
		/*	return VModelHelper.getSQLValueTimestamp(null,
					" SELECT datetrx FROM c_payment WHERE C_Payment_ID = ? ",
					C_Payment_ID);*/
			return m_fechaTrx;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_PAGOANTICIPADO;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addPaymentPaymentMedium(getC_Payment_ID(),
					getImporteMonedaOriginal());
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return importe;
		}

	}

	public String getPagoAdelantadoSqlValidation() {
		// Pagos a proveedores cuyo monto pendiente a alocar sea mayor a cero
		return " IsReceipt = '" + getIsSOTrx() + "' "
				+ " AND paymentavailable(C_Payment_ID) > 0 "
				+ " AND DocStatus in ('CO', 'CL')"
				+ " AND C_Payment.C_BPartner_ID = @C_BPartner_ID@ "
				+ " AND C_Payment.C_Currency_ID = @C_Currency_ID@ ";
	}

	/**
	 * Incorpora un cobro adelantado
	 * 
	 * @param payID
	 * @param amount
	 * @throws Exception
	 */
	public MedioPago addPagoAdelantado(Integer payID, BigDecimal amount,
			boolean isCash, Integer monedaOriginalID) throws Exception {
		if (payID == null || payID == 0)
			throw new Exception("@FillMandatory@ "
					+ (isCash ? getMsg("Cash") : getMsg("Payment")));
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new Exception("@NoAmountError@");
		if ((isCash && findMedioPago(MedioPagoEfectivoAdelantado.class, payID) != null)
				|| (!isCash && findMedioPago(MedioPagoAdelantado.class, payID) != null))
			throw new Exception(
					Msg.getMsg(getCtx(), "POPaymentExistsError", new Object[] {
							getMsg("AdvancedPayment"), getMsg("Payment") }));

		// El disponible del cobro/pago es mayor al monto ingresado?
		BigDecimal payAvailable = (isCash ? getCashAdelantadoAvailableAmt(payID)
				: getPagoAdelantadoAvailableAmt(payID));
		if (payAvailable.compareTo(amount) < 0)
			throw new Exception("@AmountGreatherThanAvalaibleError@ ("
					+ payAvailable + ")");

		MedioPago mp = null;
		if (isCash)
			mp = new MedioPagoEfectivoAdelantado(payID, amount,
					monedaOriginalID);
		else
			mp = new MedioPagoAdelantado(payID, amount, monedaOriginalID);

		return mp;
	}

	public int getC_Currency_ID() {
		return C_Currency_ID;
	}

	/**
	 * Actualiza el AllocationHdr que representa el encabezado de una OP u OPA.
	 */
	private void updateAllocationHdr(MAllocationHdr hdr) {
		String HdrDescription = getAllocHdrDescription();

		hdr.setC_BPartner_ID(C_BPartner_ID);
		hdr.setC_Currency_ID(C_Currency_ID);

		//hdr.setDateAcct(m_fechaFacturas);
		//hdr.setDateTrx(m_fechaFacturas);
		hdr.setDateAcct(m_fechaTrx);
		hdr.setDateTrx(m_fechaTrx);

		hdr.setDescription(HdrDescription);
		hdr.setIsManual(false);
		
		// Detalle es una variable String inicializada en vacio que nunca se
		// modifica.
		// hdr.setdetalle(detalle);
	}

	protected String getAllocHdrDescriptionMsg() {
		return "@PaymentOrder@";
	}

	public String getCashAnticipadoSqlValidation() {
		return " ABS(cashlineAvailable(C_CashLine_ID)) > 0 "
				+ " AND SIGN(amount) = " + getSignoIsSOTrx()
				+ " AND C_CashLine.C_BPartner_ID = @C_BPartner_ID@ "
				+ " AND C_CashLine.DocStatus IN ('CO','CL') "
				+ " AND C_CashLine.C_Currency_ID = @C_Currency_ID@ ";
	}

	public BigDecimal getPagoAdelantadoAvailableAmt(int paymentID) {
		try {
			BigDecimal AvailableamountToConvert = (BigDecimal) DB.getSQLObject(
					null, "SELECT paymentAvailable(?)",
					new Object[] { paymentID });
			return currencyConvert(AvailableamountToConvert, DB.getSQLValue(
					null,
					"SELECT C_Currency_ID From C_Payment where C_Payment_ID = "
							+ paymentID), getC_Currency_ID(), 
							//new Timestamp(new java.util.Date().getTime()));
							m_fechaTrx);
		} catch (Exception e) {
			return null;
		}
	}

	public BigDecimal getCashAdelantadoAvailableAmt(int cashLineID) {
		try {
			BigDecimal AvailableamountToConvert = (BigDecimal) DB.getSQLObject(
					null, "SELECT abs(cashLineAvailable(?))",
					new Object[] { cashLineID });
			return currencyConvert(
					AvailableamountToConvert,
					DB.getSQLValue(null,
							"SELECT C_Currency_ID From C_CashLine where C_CashLine_ID = "
									+ cashLineID), getC_Currency_ID(),
					//new Timestamp(new java.util.Date().getTime())).abs();
									m_fechaTrx);
		} catch (Exception e) {
			return null;
		}
	}

	protected String getMsg(String name) {
		String msgName = getMsgMap().get(name);
		if (msgName == null || msgName.length() == 0)
			msgName = name;
		return Msg.translate(getCtx(), msgName);
	}
	
	public String getMsg(String name, Object[] params) {
		return Msg.getMsg(getCtx(), name, params);
	}

	public class MedioPagoEfectivoAdelantado extends MedioPago {
		public Integer cashLineID;
		public BigDecimal importe;

		/**
		 * @param cashLineID
		 * @param importe
		 */
		public MedioPagoEfectivoAdelantado(Integer cashLineID,
				BigDecimal importe, int monedaOriginalID) {
			super();
			this.cashLineID = cashLineID;
			this.importe = importe;
			this.monedaOriginalID = monedaOriginalID;
		}

		@Override
		public int getBankAccountID() {
			return -1;
		}

		@Override
		public Timestamp getDateAcct() {
			return VModelHelper
					.getSQLValueTimestamp(
							null,
							" SELECT dateacct FROM c_cashline cl INNER JOIN c_cash c ON (cl.c_cash_id = c.c_cash_id) WHERE C_CashLine_ID = ? ",
							cashLineID);
		}

		@Override
		public Timestamp getDateTrx() {
			/*return VModelHelper
					.getSQLValueTimestamp(
							null,
							" SELECT statementdate FROM c_cashline cl INNER JOIN c_cash c ON (cl.c_cash_id = c.c_cash_id) WHERE C_CashLine_ID = ? ",
							cashLineID);
							*/
			return m_fechaTrx;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_EFECTIVOADELANTADO;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		/**
		 * @return the cashLineID
		 */
		public Integer getCashLineID() {
			return cashLineID;
		}

		/**
		 * @param cashLineID
		 *            the cashLineID to set
		 */
		public void setCashLineID(Integer cashLineID) {
			this.cashLineID = cashLineID;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator.addCashLinePaymentMedium(getCashLineID(),
					getImporteMonedaOriginal());
		}

		@Override
		public BigDecimal getImporteMonedaOriginal() {
			return importe;
		}

	}

	private MedioPago findMedioPago(Class clazz, int id) {
		MedioPago medioPago = null;
		Integer tmpID = null;
		for (Iterator<MedioPago> pagos = getMediosPago().iterator(); pagos
				.hasNext() && medioPago == null;) {
			MedioPago pago = pagos.next();
			if (pago.getClass().equals(clazz)) {
				if (clazz.equals(MedioPagoAdelantado.class))
					tmpID = ((MedioPagoAdelantado) pago).getC_Payment_ID();
				else if (clazz.equals(MedioPagoEfectivoAdelantado.class))
					tmpID = ((MedioPagoEfectivoAdelantado) pago)
							.getCashLineID();
				else if (clazz.equals(MedioPagoCredito.class))
					tmpID = ((MedioPagoCredito) pago).getC_invoice_ID();
				else if (clazz.equals(MedioPagoChequeTercero.class))
					tmpID = ((MedioPagoChequeTercero) pago).getC_Payment_ID();

				if (tmpID.equals(id))
					medioPago = pago;
			}
		}
		return medioPago;
	}

	public String getChequeTerceroCuentaSqlValidation() {
		return "IsChequesEnCartera = 'Y' AND C_Currency_ID = @C_Currency_ID@ ";
	}

	public String getChequeTerceroSqlValidation() {
		return
		// Filtro de cuenta seleccionada por el usuario. En caso de no haber
		// selección se presentan
		// todos los cheques.
		" (C_BankAccount_ID = @C_BankAccount_ID@ OR 0 =  @C_BankAccount_ID@) "
				+
				// La cuenta bancaria del cheque debe ser una cuenta de cheques
				// en cartera
				" AND NOT EXISTS (SELECT C_BankAccount_ID "
				+ " FROM C_BankAccount ba "
				+ " WHERE ba.C_BankAccount_ID = C_Payment.C_BankAccount_ID "
				+ " AND ba.IsChequesEnCartera = 'N') "
				+
				// Debe ser un cheque recibido
				" AND IsReceipt = 'Y'"
				+
				// Debe ser cheque :)
				" AND TenderType = 'K'"
				+
				// Los cheques de terceros utilizables están en estado CO. Si el
				// estado es CL implica que el cheque de tercero ya fue
				// utilizado como medio
				// de pago y no se encuentra en cartera, con lo cual no se debe
				// permitir
				// seleccionarlo nuevamente.
				" AND DocStatus IN ('CO')"
				+ " AND C_Currency_ID = @C_Currency_ID@ ";
	}

	public BigDecimal getChequeAmt(int paymentID) {
		try {
			BigDecimal amtToConvert = (BigDecimal) DB.getSQLObject(null,
					"SELECT PayAmt FROM C_Payment WHERE C_Payment_ID = ?",
					new Object[] { paymentID });
			return currencyConvert(amtToConvert, DB.getSQLValue(null,
					"SELECT C_Currency_ID From C_Payment where C_Payment_ID = "
							+ paymentID), getC_Currency_ID(), 
							//new Timestamp(new java.util.Date().getTime()));
							m_fechaTrx);
		} catch (Exception e) {
			return null;
		}
	}

	public class MedioPagoChequeTercero extends MedioPagoAdelantado {

		public String description = null;
		private MPayment chequeTerceroPayment;

		public MedioPagoChequeTercero(int payment_ID, BigDecimal importe,
				String description, int monedaOriginalID) {
			super(payment_ID, importe, monedaOriginalID);
			this.description = description;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_CHEQUETERCERO;
		}

		@Override
		public void addToGenerator(POCRGenerator poGenerator) {
			poGenerator
					.addPaymentPaymentMedium(getChequeCP().getC_Payment_ID(),
							getImporteMonedaOriginal());
		}

		@Override
		public Timestamp getDateAcct() {
			return m_fechaTrx;
		}

		public MPayment getChequeCP() {
			return (MPayment) this.payment;
		}

		public void setPayment(MPayment pay) {
			this.payment = pay;
		}

		public MPayment getChequeTerceroPayment() {
			if (chequeTerceroPayment == null)
				chequeTerceroPayment = new MPayment(getCtx(),
						getC_Payment_ID(), getTrxName());
			return chequeTerceroPayment;
		}

	}

	public void addChequeTercero(Integer paymentID, BigDecimal amount,
			String description, Integer monedaOriginalID) throws Exception {
		if (paymentID == null || paymentID == 0)
			throw new Exception("@FillMandatory@ " + getMsg("Check"));
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new Exception("@NoAmountError@");
		if (findMedioPago(MedioPagoChequeTercero.class, paymentID) != null)
			throw new Exception(
					Msg.getMsg(getCtx(), "POPaymentExistsError", new Object[] {
							getMsg("ThirdPartyCheck"), getMsg("Payment") }));

		MedioPagoChequeTercero mpct = new MedioPagoChequeTercero(paymentID,
				amount, description, monedaOriginalID);
		addMedioPago(mpct);
	}

	public void addMedioPago(MedioPago mp) {
		m_mediosPago.add(mp);
		updateAddingMedioPago(mp);
	}

	public void removeMedioPago(MedioPago mp) {
		m_mediosPago.remove(mp);
		updateRemovingMedioPago(mp);
	}

	/**
	 * Realizar tareas adicionales para la gestión de crédito de clientes
	 * 
	 * @throws Exception
	 *             en caso de error
	 */
	protected void performAditionalCurrentAccountWork() throws Exception {
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), getTrxName());
		// Obtengo el manager actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager(getPoGenerator().getAllocationHdr());
		// Realizo las tareas adicionales necesarias
		// Payments
		for (MPayment pay : mpayments.values()) {
			performAditionalCurrentAccountWork(org, getBPartner(), manager,
					pay, true);
		}
		// Cashlines
		for (MCashLine cashLine : mCashLines.values()) {
			performAditionalCurrentAccountWork(org, getBPartner(), manager,
					cashLine, true);
		}
		// Adicionales customs de las subclases
		performAditionalCustomCurrentAccountWork(org, manager);
	}

	/**
	 * Realiza las tareas adicionales en base a los paramétros.
	 * 
	 * @param org
	 *            organización actual
	 * @param bp
	 *            entidad comercial de la venta actual
	 * @param manager
	 *            manager de cuentas corrientes actual
	 * @param po
	 *            documento o transacción involucrada, puede ser Invoice,
	 *            Payment o Cashline.
	 * @param addToWorkResults
	 *            true si el resultado de estas tareas se debe colocar dentro de
	 *            la map de resultados (variable de instancia
	 *            aditionalWorkResults)
	 * @return el resultado de las tareas adicionales si es que existe
	 * @throws Exception
	 *             si hubo algún error dentro de la ejecución de esas tareas
	 */
	protected Object performAditionalCurrentAccountWork(MOrg org, MBPartner bp,
			CurrentAccountManager manager, PO po, boolean addToWorkResults)
			throws Exception {
		// Realizo las tareas adicionales
		CallResult result = manager.performAditionalWork(getCtx(), org, bp, po,
				true, getTrxName());
		// Si es error devuelvo una exception
		if (result.isError()) {
			throw new Exception(result.getMsg());
		}
		// Lo agrego a la map si me lo permite el parámetro y tengo un resultado
		if (addToWorkResults && result != null) {
			getAditionalWorkResults().put(po, result.getResult());
		}
		return result.getResult();
	}

	/**
	 * Realizar tareas de cuenta corriente del cliente adicionales en las
	 * subclases
	 * 
	 * @param org
	 *            organización
	 * @param manager
	 *            manager de cuentas corrientes de clientes
	 * @throws Exception
	 *             si hubo error
	 */
	protected void performAditionalCustomCurrentAccountWork(MOrg org,
			CurrentAccountManager manager) throws Exception {
		// No hace nada aquí por ahora
	}

	/**
	 * Realizar tareas de gestión de crédito luego de procesar los documentos,
	 * como por ejemplo actualización del crédito de la entidad comercial, etc.
	 */
	private void afterProcessDocuments() {
		MBPartner bp = new MBPartner(getCtx(), getBPartner().getID(),
				getTrxName());
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), getTrxName());
		// Obtengo el manager actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager(getPoGenerator().getAllocationHdr());
		// Actualizo el crédito
		CallResult result = new CallResult();
		try {
			result = manager.afterProcessDocument(getCtx(), org, bp,
					getAditionalWorkResults(), getTrxName());
		} catch (Exception e) {
			result.setMsg(e.getMessage(), true);
		}
		if (result.isError()) {
			log.severe(result.getMsg());
		}
	}

	public void setMCashLines(Map<Integer, MCashLine> cashLines) {
		this.mCashLines = cashLines;
	}

	public Map<Integer, MCashLine> getMCashLines() {
		return mCashLines;
	}

	public void setAditionalWorkResults(Map<PO, Object> aditionalWorkResults) {
		this.aditionalWorkResults = aditionalWorkResults;
	}

	public Map<PO, Object> getAditionalWorkResults() {
		return aditionalWorkResults;
	}

	/**
	 * @return el monto total del allocation hdr
	 */
	public BigDecimal getAllocationHdrTotalAmt() {
		return getSumaTotalPagarFacturas();
	}

	/**
	 * Realizar tareas custom a medida que vamos creando los medios de pago.
	 * Deben implementar las subclases
	 * 
	 * @param pay
	 *            payment en creación
	 * @param mp
	 *            medio de pago actual relacionado
	 */
	protected void addCustomPaymentInfo(MPayment pay, MedioPago mp) {
		// Por ahora no realiza nada
	}

	/**
	 * Realizar tareas custom a medida que vamos creando los medios de pago.
	 * Deben implementar las subclases
	 * 
	 * @param cashLine
	 *            línea de caja en creación
	 * @param mp
	 *            medio de pago actual relacionado
	 */
	protected void addCustomCashLineInfo(MCashLine cashLine, MedioPago mp) {
		// Por ahora no realiza nada
	}

	/**
	 * Realizo acciones en el modelo debido a la elección de las facturas a
	 * pagar. Los parámetros se encuentran ordenados, o sea, el primer monto
	 * manual corresponde a la primer factura, como así también del primer
	 * resultado
	 * 
	 * @param facturasProcesar
	 *            facturas a cobrar (monto manual > 0)
	 * @param manualAmounts
	 *            montos manuales a cobrar de las facturas
	 * @param resultsProcesar
	 *            resultados de la tabla de facturas
	 */
	protected void updateInvoicesModel(Vector<Integer> facturasProcesar,
			Vector<BigDecimal> manualAmounts,
			Vector<BigDecimal> manualAmountsOriginal,
			Vector<ResultItemFactura> resultsProcesar) {
		// Por ahora no realiza nada aquí
	}

	/**
	 * Realizo acciones adicionales en el modelo
	 */
	protected void updateAditionalInfo() {
		// Por ahora no realiza nada aquí
	}

	/**
	 * Actualización de información del modelo luego de agregar un medio de
	 * pago.
	 * 
	 * @param mp
	 *            medio de pago agregado
	 */
	public void updateAddingMedioPago(MedioPago mp) {
		// Por ahora no se hace nada aquí
	}

	/**
	 * Actualización de información luego de remover un medio de pago.
	 * 
	 * @param mp
	 *            medio de pago removido (ya sea para eliminar o editar)
	 */
	public void updateRemovingMedioPago(MedioPago mp) {
		// Por ahora no se hace nada aquí
	}

	/**
	 * Actualización de información luego de agregar una retención
	 * 
	 * @param processor
	 *            retención
	 */
	protected void updateAddingRetencion(RetencionProcessor processor) {
		// Por ahora no se hace nada aquí
	}

	/**
	 * Actualización de información luego de remover una retención
	 * 
	 * @param processor
	 *            retención
	 */
	protected void updateRemovingRetencion(RetencionProcessor processor) {
		// Por ahora no se hace nada aquí
	}

	/**
	 * Actualizo toda la info que tengo relacionada con la organización
	 * 
	 * @param orgID
	 *            id de la organización nueva seleccionada.
	 */
	public void updateOrg(int orgID) {
		setOrgId(orgID);
		// Actualizar las facturas de la tabla
		if (!Util.isEmpty(C_BPartner_ID, true)) {
			actualizarFacturas();
		}
	}

	/**
	 * Crear débitos y créditos específicos. Los documentos o medios de pago
	 * creados en este método tienen que estar al alcance para la creación del
	 * allocation y así poder realizar la asignación en los alocationlines.
	 * Obviamente el saldo del allocation debe dar 0, tener cuidado con saldo
	 * distinto de 0 por agregación de débito o crédito innecesario.
	 * 
	 * @param pays
	 *            vector con los créditos actuales
	 * @throws Exception
	 *             si hubo errores
	 */
	protected void makeCustomDebitsCredits(Vector<MedioPago> pays)
			throws Exception {
		// Por ahora no hace nada aquí, ver subclases.
	}

	/** Sobrecarga por compatibilidad */
	public void addDebit(MInvoice invoice) {
		addDebit(invoice, null);
	}

	/**
	 * Agrego un débito a la lista de facturas de proveedor
	 * 
	 * @param invoice
	 *            débito
	 */
	public void addDebit(MInvoice invoice, BigDecimal amount) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT c_invoice_id, 0, orgname, documentno,max(duedate) as duedatemax, sum(convertedamt) as convertedamtsum, sum(openamt) as openAmtSum FROM ");
		sql.append("  (SELECT i.C_Invoice_ID, i.C_InvoicePaySchedule_ID, org.name as orgname, i.DocumentNo, coalesce(i.duedate,dateinvoiced) as DueDate, "); // ips.duedate
		sql.append("    abs(currencyConvert( i.GrandTotal, i.C_Currency_ID, ?, i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID)) as ConvertedAmt, ");
		sql.append("    abs(currencyConvert( invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)), i.C_Currency_ID, ?, i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID)) AS openAmt ");
		sql.append("  FROM c_invoice_v AS i ");
		sql.append("  LEFT JOIN ad_org org ON (org.ad_org_id=i.ad_org_id) ");
		sql.append("  LEFT JOIN c_invoicepayschedule AS ips ON (i.c_invoicepayschedule_id=ips.c_invoicepayschedule_id) ");
		sql.append("  INNER JOIN C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
		sql.append("  WHERE i.c_invoice_id = ? ");
		sql.append("  ) as foo ");
		sql.append("  group by foo.c_invoice_id, foo.orgname, foo.documentno ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), getTrxName());
			ps.setInt(1, C_Currency_ID);
			ps.setInt(2, C_Currency_ID);
			ps.setInt(3, invoice.getID());
			rs = ps.executeQuery();
			if (rs.next()) {
				ResultItemFactura rif = new ResultItemFactura(rs);
				if (amount != null) {
					BigDecimal manualAmtClientCurrency = MCurrency
							.currencyConvert(amount,
									invoice.getC_Currency_ID(), C_Currency_ID,
								//	new Timestamp(System.currentTimeMillis()),
									m_fechaTrx,
									0, getCtx());
					rif.setManualAmtClientCurrency(manualAmtClientCurrency);
					rif.setManualAmount(amount);
				}
				m_facturas.add(rif);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Este método fue modificado casi en su totalidad para que utilice la
	 * funcionalidad de la clase POCRGenerator.
	 * 
	 * @param pays
	 *            la lista de medios de pago actual a tener en cuenta, dentro de
	 *            este método se realiza la ordenación
	 * @throws AllocationGeneratorException
	 */
	public void updateOnlineAllocationLines(Vector<MedioPago> pays)
			throws AllocationGeneratorException {
		// Eliminar todos los allocation anteriores
		onlineAllocationLines = null;
		onlineAllocationLines = new ArrayList<AllocationLine>();

		Vector<MedioPago> pagos = new Vector<MedioPago>(pays);
		Vector<ResultItemFactura> facturas = new Vector<ResultItemFactura>(
				ordenarFacturas());

		int facIdColIdx = m_facturasTableModel.getIdColIdx();

		eliminarFacturasVacias(facturas);

		// Agrego las facturas al generador
		for (ResultItemFactura f : facturas) {
			int invoiceID = ((Integer) f.getItem(facIdColIdx));
			// Es el importe en la moneda original (no el de la Compañia)
			BigDecimal payAmount = f.getManualAmount();
			getPoGenerator().addInvoice(invoiceID, payAmount);
		}
		// Agrego los medios de pagos al generador
		for (MedioPago pago : pagos) {
			pago.addToGenerator(getPoGenerator());
		}
		// Se crean las líneas de imputación entre las facturas y los pagos
		getPoGenerator().generateLines();
	}

	/**
	 * Este método genera completamente la lista de allocation line online, o
	 * sea se crean las allocation lines que hasta ahora se deben guardar,
	 * realizando la división de pagos etc. Texto copiado de
	 * doPostProcesarNormal.
	 * 
	 * @throws AllocationGeneratorException
	 */
	public void updateOnlineAllocationLines()
			throws AllocationGeneratorException {
		updateOnlineAllocationLines(m_mediosPago);
	}

	/**
	 * Crea las MAllocationLine desde las allocation line online y las guarda.
	 * 
	 * @param hdr
	 *            allocationhdr a asociar todas las lines
	 * @param pays
	 *            medios de pago
	 * @param rebuild
	 *            true si se debe reconstruir nuevamente toda la estructura de
	 *            online allocation lines antes de crear las M, esto quizás
	 *            sirve para créditos agregados antes de completar toda la
	 *            operación, false si se crean las M con la estructura actual
	 * @return true si no hubo problemas, false cc
	 * @throws Exception
	 *             en caso de error en la creación de las MAllocationLines
	 *             basadas en las allocation lines online
	 */
	protected boolean processOnlineAllocationLines(MAllocationHdr hdr,
			Vector<MedioPago> pays, boolean rebuild) throws Exception {
		// Rearmar las online allocation lines por posibles medios de pagos
		// agregados luego
		if (rebuild)
			updateOnlineAllocationLines(pays);
		boolean ok = true;

		// Actualizo el total del allocation en base al total de las líneas
		// creadas
		hdr.updateTotalByLines();
		if (!hdr.save()) {
			ok = false;
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		return ok;
	}

	/**
	 * Resetear la info del modelo para nuevas ordenes de pago
	 */
	public void reset() {
		m_mediosPago = new Vector<MedioPago>();
		m_retGen = null;
		m_retenciones = new Vector<RetencionProcessor>();
		mpayments = new HashMap<Integer, MPayment>();
		mCashLines = new HashMap<Integer, MCashLine>();
		aditionalWorkResults = new HashMap<PO, Object>();
		onlineAllocationLines = new ArrayList<AllocationLine>();
		m_newlyCreatedC_AllocationHeader_ID = -1;
		retencionIncludedInMedioPago = false;
		setProjectID(0);
		setCampaignID(0);
		getPoGenerator().reset();
	}

	/**
	 * Realiza la impresión de documentos custom, generalmente las subclases
	 * usan este posibilidad
	 * 
	 * @param proceso
	 *            asíncrono
	 */
	public void printCustomDocuments(ASyncProcess asyncProcess) {
		// Por ahora no hace nada aquí
	}

	/**
	 * 
	 * @param amount
	 * @param currencyID
	 * @param roundingMode
	 * @return
	 */
	public BigDecimal scaleAmount(BigDecimal amount, int currencyID,
			int roundingMode) {
		BigDecimal newAmount = amount;
		int scale = mCurency.getStdPrecision();
		newAmount = amount.setScale(scale, roundingMode);
		return newAmount;
	}

	public BigDecimal updatePayAllInvoices(boolean payAll, boolean toPayMoment) {
		if (m_facturas == null) {
			return BigDecimal.ZERO;
		}
		int totalInvoices = m_facturas.size();
		ResultItemFactura fac;
		BigDecimal amt;
		BigDecimal totalAmt = BigDecimal.ZERO;
		int i = 0;
		for (; i < totalInvoices; i++) {
			fac = (ResultItemFactura) m_facturas.get(i);
			amt = payAll ? fac.getToPayAmt(true) : BigDecimal.ZERO;
			// Solamente cambio el valor cuando no estamos en el momento de
			// pagar
			if (!toPayMoment) {
				fac.setManualAmtClientCurrency(amt);
				actualizarPagarConPagarCurrency(
						i,
						fac,
						(Integer) m_facturas.get(i).getItem(
								m_facturasTableModel.getCurrencyColIdx()), payAll);
				totalAmt = totalAmt.add(amt);
			}
		}
		return totalAmt;
	}
	
	public BigDecimal updatePayInvoice(boolean pay, int row, boolean toPayMoment) {
		BigDecimal totalAmt = BigDecimal.ZERO;
		ResultItemFactura fac = (ResultItemFactura) m_facturas.get(row);
		BigDecimal amt = pay ? fac.getToPayAmt(true) : BigDecimal.ZERO;
		// Solamente cambio el valor cuando no estamos en el momento de
		// pagar
		if (!toPayMoment) {
			fac.setManualAmtClientCurrency(amt);
			actualizarPagarConPagarCurrency(
					row,
					fac,
					(Integer) m_facturas.get(row).getItem(
							m_facturasTableModel.getCurrencyColIdx()), pay);
			totalAmt = totalAmt.add(amt);
		}
		return totalAmt;
	}

	/**
	 * Clase que permite tener creados allocation lines online para la OP/RC
	 * actual
	 * 
	 * @author Equipo de Desarrollo Disytel
	 * 
	 */
	public class AllocationLine {
		private BigDecimal amt;
		private BigDecimal discountAmt;
		private BigDecimal overUnderAmt;
		private BigDecimal writeOffAmt;
		private MedioPago creditPaymentMedium;
		private Integer debitDocumentID;

		// Constructores
		public AllocationLine() {

		}

		public void setAmt(BigDecimal amt) {
			this.amt = amt;
		}

		public BigDecimal getAmt() {
			return amt;
		}

		public void setDiscountAmt(BigDecimal discountAmt) {
			this.discountAmt = discountAmt;
		}

		public BigDecimal getDiscountAmt() {
			return discountAmt;
		}

		public void setOverUnderAmt(BigDecimal overUnderAmt) {
			this.overUnderAmt = overUnderAmt;
		}

		public BigDecimal getOverUnderAmt() {
			return overUnderAmt;
		}

		public void setWriteOffAmt(BigDecimal writeOffAmt) {
			this.writeOffAmt = writeOffAmt;
		}

		public BigDecimal getWriteOffAmt() {
			return writeOffAmt;
		}

		public void setCreditPaymentMedium(MedioPago creditPaymentMedium) {
			this.creditPaymentMedium = creditPaymentMedium;
		}

		public MedioPago getCreditPaymentMedium() {
			return creditPaymentMedium;
		}

		public void setDebitDocumentID(Integer debitDocumentID) {
			this.debitDocumentID = debitDocumentID;
		}

		public Integer getDebitDocumentID() {
			return debitDocumentID;
		}
	}

	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
		getPoGenerator().setDocumentNo(documentNo);
	}
	
	public String getDocumentNo(){
		return this.documentNo;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public Integer getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Integer documentType) {
		this.documentType = documentType;
		getPoGenerator().setDocType(documentType);
	}

	protected String getAllocTypes() {
		return "(" + "'" + X_C_AllocationHdr.ALLOCATIONTYPE_PaymentOrder + "',"
				+ "'" + X_C_AllocationHdr.ALLOCATIONTYPE_PaymentFromInvoice
				+ "'," + "'"
				+ X_C_AllocationHdr.ALLOCATIONTYPE_AdvancedPaymentOrder + "'"
				+ ")";
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

	public void setExchangeDifference(BigDecimal exchangeDifference) {
		this.exchangeDifference = exchangeDifference;
	}

	public BigDecimal getExchangeDifference() {
		return exchangeDifference;
	}

	/**
	 * Validación utilizada para valores mostrados por el combo DocumentType de
	 * la Ventana Orden de Pago
	 */
	public String getDocumentTypeSqlValidation() {
		return " ((C_Doctype.ad_Org_ID = 0) OR (C_Doctype.ad_Org_ID = "
				+ Env.getAD_Org_ID(m_ctx) + "))"
				+ " AND (C_Doctype.IsPaymentOrderSeq = 'Y') "
				+ " AND (C_Doctype.DocBaseType = 'APP') ";
	}

	public String getBank(Integer value) {
		// TODO Auto-generated method stub
		return null;
	}

	protected POCRType getPOCRType() {
		return POCRType.PAYMENT_ORDER;
	}

	public POCRGenerator getPoGenerator() {
		return poGenerator;
	}

	public void setPoGenerator(POCRGenerator poGenerator) {
		this.poGenerator = poGenerator;
	}

	public BigDecimal calculateExchangeDifference() {
		HashMap<Integer, BigDecimal> facts = new HashMap<Integer, BigDecimal>();
		if (m_facturas != null) {
			for (ResultItem x : m_facturas) {
				if (((ResultItemFactura) x).getManualAmount().compareTo(
						BigDecimal.ZERO) > 0) {
					facts.put(((Integer) ((ResultItemFactura) x)
							.getItem(m_facturasTableModel.getIdColIdx())),
							((ResultItemFactura) x).getManualAmount());
				}
			}
		}

		ArrayList<PaymentMediumInfo> pays = new ArrayList<PaymentMediumInfo>();

		if (m_mediosPago != null) {
			for (MedioPago mp : m_mediosPago) {
				PaymentMediumInfo payMedInfo = getPoGenerator().new PaymentMediumInfo(
						mp.getImporteMonedaOriginal(),
						mp.getMonedaOriginalID(), mp.getDateAcct());
				pays.add(payMedInfo);
			}
		}
		return AllocationGenerator.getExchangeDifference(facts, pays, getCtx(),
				getTrxName(),this.m_fechaTrx);
	}

	protected MRole getRole() {
		return role;
	}

	protected void setRole(MRole role) {
		this.role = role;
	}

	public boolean addSecurityValidationToNC(){
		return getRole().isAddSecurityValidation_OPRC_NC();
	}

	public boolean isAllowAdvancedPayment() {
		boolean allowOPA = isAllowAdvanced();
		if (BPartner != null) {
			allowOPA = allowOPA && BPartner.isAllowAdvancedPaymentReceipts();
		} 
		return allowOPA;
	}
	
	public boolean isAllowAdvanced(){
		return getRole().isAllowOPA();
	}
	
	public boolean getPartialPayment() {
		if ((m_esPagoNormal) && (BPartner != null && !BPartner.isAllowPartialPayment())){
			if (m_facturas != null) {
				for (ResultItem x : m_facturas) {
					ResultItemFactura rif = (ResultItemFactura) x;
					if (rif.getManualAmtClientCurrency().compareTo(
							BigDecimal.ZERO) == 1) {
						if (rif.getManualAmtClientCurrency().compareTo(
								rif.getToPayAmt(true)) == -1)
							return true;
					}
				}
			}
		}
		return false;
	}

	public Integer getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Integer paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Integer getAmountAdvancedPayment() {
		Integer cantidad=0;
		for (MedioPago pago : getMediosPago()) {
			if (pago.getTipoMP().equals(
					MedioPago.TIPOMEDIOPAGO_EFECTIVOADELANTADO)
					|| pago.getTipoMP().equals(
							MedioPago.TIPOMEDIOPAGO_PAGOANTICIPADO)
							|| pago.getTipoMP().equals(
									MedioPago.TIPOMEDIOPAGO_CREDITO))
				cantidad++;
		}
		return cantidad;
	}

	public boolean isAuthorizations() {
		return (MOrgInfo.get(getCtx(), Env.getAD_Org_ID(getCtx()))).isAuthorizations();
	}
	
	public List<ValueNamePair> getPaymentRulesList(){
		List<ValueNamePair> list = new ArrayList<ValueNamePair>();
		list.add(new ValueNamePair(MInvoice.PAYMENTRULE_Cash,
				MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID, MInvoice.PAYMENTRULE_Cash)));
		list.add(new ValueNamePair(MInvoice.PAYMENTRULE_OnCredit,
				MRefList.getListName(getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID, MInvoice.PAYMENTRULE_OnCredit)));
		return list;
	}
	
	public String getPaymentRuleValidation(){
		return "Value in ('" + MInvoice.PAYMENTRULE_Cash + "','" + MInvoice.PAYMENTRULE_OnCredit + "')";
	}
	
	public String getDefaultPaymentRule(){
		return MInvoice.PAYMENTRULE_OnCredit;
	}

	public DateFormat getSimpleDateFormat() {
		return simpleDateFormat;
	}

	public void setSimpleDateFormat(DateFormat simpleDateFormat) {
		this.simpleDateFormat = simpleDateFormat;
	}

	public String getPaymentRule() {
		return paymentRule;
	}

	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
		getPoGenerator().setPaymentRule(paymentRule);
	}
	
	/**
	 * @return el próximo número de documento de la secuencia del tipo de
	 *         documento
	 * @throws Exception
	 *             en caso de error
	 */
	public String nextDocumentNo() throws Exception{
		return getPoGenerator().getDocumentNo();
	}
}
