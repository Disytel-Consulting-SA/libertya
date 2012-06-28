package org.openXpertya.apps.form;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.openXpertya.apps.ProcessCtl;
import org.openXpertya.apps.form.VModelHelper.ResultItem;
import org.openXpertya.apps.form.VModelHelper.ResultItemTableModel;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.model.MEntidadFinancieraPlan;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPInstancePara;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.PO;
import org.openXpertya.model.RetencionProcessor;
import org.openXpertya.model.X_C_AllocationHdr;
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
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;

public class VOrdenPagoModel implements TableModelListener {

	public static final int PROCERROR_OK = 0;
	public static final int PROCERROR_INSUFFICIENT_INVOICES = 1;
	public static final int PROCERROR_PAYMENTS_AMT_MATCH = 2;
	public static final int PROCERROR_PAYMENTS_GENERATION = 3;
	public static final int PROCERROR_NOT_SELECTED_BPARTNER = 4;
	public static final int PROCERROR_DOCUMENTNO_NOT_SET = 5;
	public static final int PROCERROR_DOCUMENTNO_ALREADY_EXISTS = 6;
	public static final int PROCERROR_UNKNOWN = -1;
	
	protected static CLogger log = CLogger.getCLogger(VOrdenPagoModel.class);
	
	public class MyTreeNode extends DefaultMutableTreeNode {
		
		protected String m_msg;
		protected boolean m_leaf;
		
		public MyTreeNode(String msg, Object obj, boolean leaf) {
			m_msg = msg;
			m_leaf = leaf;
			userObject = obj;
		}
		
		public void setMsg(String msg) {
			m_msg = msg;
		}
		
		public String toString() {
			return m_msg != null ? m_msg : userObject.toString();
		}
		
		public boolean isLeaf() {
			return m_leaf;
		}
		
		public boolean isMedioPago() {
			return userObject != null && userObject instanceof MedioPago;
		}
		
		public boolean isRetencion() {
			return userObject != null && !isMedioPago();
		}
	}
	
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
		public abstract BigDecimal getImporte();
		public abstract void setImporte(BigDecimal importe);
		public abstract Timestamp getDateTrx();
		public abstract Timestamp getDateAcct();
		public abstract int getBankAccountID();
		public abstract void setAllocationInfo(MAllocationLine allocLine);
				
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
		private MedioPago nuevoImporte(BigDecimal importe) {
			try {
				MedioPago nuevo = (MedioPago)clone();
				nuevo.setImporte(importe);
				return nuevo;
			} catch (Exception e) {
				
			}
			return null;
		}
		*/

		protected Object payment = null;
		
		private Integer projectId;
		private Integer campaignId;
		
		public String toString() {
			String tipoStr;

			if (getTipoMP().equals(TIPOMEDIOPAGO_EFECTIVO)) { 
				// Efectivo
				tipoStr = VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "B", "name");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_TRANSFERENCIA)) {
				// Transferencia Bancaria
				tipoStr = VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "Tr", "name");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_CHEQUE)) {
				// Cheque
				tipoStr = VModelHelper.GetReferenceValueTrlFromColumn("C_Order", "PaymentRule", "S", "name");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_CREDITO)) { 
				// Credito
				tipoStr = Msg.translate(getCtx(), "Credit");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_PAGOANTICIPADO)) { 
				// Pago Anticipado
				tipoStr = getMsg("AdvancedPayment");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_EFECTIVOADELANTADO)) {
				tipoStr = getMsg("AdvancedPayment") + " (" + getMsg("Cash") + ")";
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_CHEQUETERCERO)) {
				tipoStr = getMsg("ThirdPartyCheck");
			} else if (getTipoMP().equals(TIPOMEDIOPAGO_TARJETACREDITO)) {
				tipoStr = getMsg("CreditCard");
			} else {
				tipoStr = "";
			}

			
			return tipoStr + " " + numberFormat( getImporte() );
		}
		
		/**
		 * @return the isSOTrx
		 */
		public boolean isSOTrx() {
			return isSOTrx;
		}
		/**
		 * @param isSOTrx the isSOTrx to set
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
		public void setDiscountSchemaToApply(MDiscountSchema discountSchemaToApply) {
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

		public MedioPagoEfectivo(int libroCaja_ID, BigDecimal importe) {
			super();
			this.libroCaja_ID = libroCaja_ID;
			this.importe = importe;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_EFECTIVO;
		}

		@Override
		public BigDecimal getImporte() {
			return importe;
		}

		@Override
		public Timestamp getDateTrx() {
			return VModelHelper.getSQLValueTimestamp(null, " select statementdate from c_cash where c_cash_id = ? ", libroCaja_ID);
		}

		@Override
		public Timestamp getDateAcct() {
			return VModelHelper.getSQLValueTimestamp(null, " select dateacct from c_cash where c_cash_id = ? ", libroCaja_ID);
		}
		
		@Override
		public int getBankAccountID() {
			return -1;
		}
		
		public void setCashLine(MCashLine cashLine) {
			this.payment = cashLine;
		}
		
		public MCashLine getCashLine() {
			return (MCashLine)payment;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_CashLine_ID(getCashLine().getC_CashLine_ID());
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

		public MedioPagoTransferencia(int bankAccount_ID, String nroTransf, BigDecimal importe, Timestamp fechaTransf) {
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
		public BigDecimal getImporte() {
			return importe;
		}

		@Override
		public Timestamp getDateTrx() {
			return fechaTransf;
		}

		@Override
		public Timestamp getDateAcct() {
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
			return (MPayment)payment;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_Payment_ID(getPayment().getC_Payment_ID());
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
	
		public MedioPagoCheque(int chequera_ID, String nroCheque, BigDecimal importe, Timestamp fechaEm, Timestamp fechaPago, String laOrden) {
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
		public BigDecimal getImporte() {
			return importe;
		}

		@Override
		public Timestamp getDateTrx() {
			return fechaEm;
		}

		@Override
		public Timestamp getDateAcct() {
			return fechaEm;
		}
		
		@Override
		public int getBankAccountID() {
			// Para cobros de clientes, la chequera_ID tiene el ID de BankAccount.
			if (isSOTrx())
				return chequera_ID;
			else
				return DB.getSQLValue(null, " select C_BankAccount_ID from C_BankAccountDoc where C_BankAccountDoc_ID = ? ", chequera_ID);
		}
		
		public void setPayment(MPayment pay) {
			this.payment = pay;
		}
		
		public MPayment getPayment() {
			return (MPayment)payment;
		}

		@Override
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_Payment_ID(getPayment().getC_Payment_ID());
		}
		
		/**
		 * @return Devuelve la fecha de Pago/Vto del cheque
		 */
		public Timestamp getDueDate() {
			return fechaPago;
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
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_Invoice_Credit_ID(C_Invoice_ID);
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
			return VModelHelper.getSQLValueTimestamp(getTrxName(), " select dateacct from c_invoice where c_invoice_id = ? ", C_invoice_ID);
		}

		@Override
		public Timestamp getDateTrx() {
			return VModelHelper.getSQLValueTimestamp(getTrxName(), " select datetrx from c_invoice where c_invoice_id = ? ", C_invoice_ID);
		}

		@Override
		public BigDecimal getImporte() {
			return importe;
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
		protected int getC_invoice_ID() {
			return C_invoice_ID;
		}

		/**
		 * @param c_invoice_id the c_invoice_ID to set
		 */
		protected void setC_invoice_ID(int c_invoice_id) {
			if (c_invoice_id != C_invoice_ID)
				docTypeName = VModelHelper.getSQLValueString(getTrxName(), 
						"select dt.printname from c_invoice i inner join c_doctype dt on (i.c_doctype_id=dt.c_doctype_id) where i.c_invoice_id = ?", c_invoice_id);

			C_invoice_ID = c_invoice_id;
		}

		/**
		 * @return the availableAmt
		 */
		protected BigDecimal getAvailableAmt() {
			return availableAmt;
		}

		/**
		 * @param availableAmt the availableAmt to set
		 */
		protected void setAvailableAmt(BigDecimal availableAmt) {
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
    			throw new Exception(Msg.getMsg(getCtx(), "POPaymentExistsError", new Object[] { getMsg("Credit"), getMsg("Payment") }));	
		}

		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_Invoice_Credit_ID(getC_invoice_ID());
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
		private Timestamp today = new Timestamp(System.currentTimeMillis());
		private MPayment payment;
		private String bank;
		private String accountName;
		
		@Override
		public int getBankAccountID() {
			return getEntidadFinanciera().getC_BankAccount_ID();
		}

		@Override
		public Timestamp getDateAcct() {
			return today;
		}

		@Override
		public Timestamp getDateTrx() {
			return today;
		}

		@Override
		public BigDecimal getImporte() {
			return amt;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_TARJETACREDITO;
		}

		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_Payment_ID(getPayment().getC_Payment_ID());
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
		
		public void setEntidadFinancieraPlan(MEntidadFinancieraPlan entidadFinancieraPlan) {
			this.entidadFinancieraPlan = entidadFinancieraPlan;
		}

		public MEntidadFinancieraPlan getEntidadFinancieraPlan() {
			return entidadFinancieraPlan;
		}
		
		public void setPaymentMedium(MPOSPaymentMedium paymentMedium){
			super.setPaymentMedium(paymentMedium);
			setEntidadFinanciera(new MEntidadFinanciera(getCtx(), paymentMedium
					.getM_EntidadFinanciera_ID(), getTrxName()));	
		}
				
		public String getCreditCardType(){
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
	}
	
	protected class OpenInvoicesTableModel extends ResultItemTableModel {
		public OpenInvoicesTableModel() {
			VModelHelper.GetInstance().super();
			
    		columnNames = new Vector<String>();

            columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"C_Invoice_ID" ));
            columnNames.add( "#$#" + Msg.getElement( Env.getCtx(),"C_InvoicePaySchedule_ID" ));
            columnNames.add( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
            columnNames.add( Msg.getElement( Env.getCtx(),"DocumentNo" ));
            columnNames.add( Msg.translate( Env.getCtx(),"DueDate" ));
            columnNames.add( Msg.translate( Env.getCtx(),"GrandTotal" ));
            columnNames.add( Msg.translate( Env.getCtx(),"openAmt" ));
            columnNames.add( Msg.translate( Env.getCtx(),"ToPay" ));
		}
		
		public int getOpenAmtColIdx() {
			return 6;
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
			if (column != getColumnCount() - 1)
				return super.isCellEditable(row, column);
			
			return true;
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			if (column != getColumnCount() - 1)
				return super.getValueAt(row, column);
			
			return ((ResultItemFactura) item.get(row)).getManualAmount();
		}
		
		@Override
		public void setValueAt(Object arg0, int row, int column) {
			if (column != getColumnCount() - 1)
				super.setValueAt(arg0, row, column);
			
			((ResultItemFactura) item.get(row)).setManualAmount((BigDecimal)arg0);				
					
			fireTableCellUpdated(row, column);
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex != getColumnCount() - 1)
				return super.getColumnClass(columnIndex);
			
			return BigDecimal.class;
		}
	}
	
	protected class ResultItemFactura extends ResultItem {
		
		private BigDecimal manualAmount = new BigDecimal(0);
		private BigDecimal paymentTermDiscount = new BigDecimal(0);
		
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
		
		public BigDecimal getToPayAmt(boolean withPaymentTermDiscount){
			BigDecimal toPay = (BigDecimal)getItem(m_facturasTableModel.getOpenAmtColIdx());
			if(withPaymentTermDiscount){
				toPay = toPay.subtract(getPaymentTermDiscount());
			}
			return toPay;
		}
	}
	
	// 
	Properties m_ctx = Env.getCtx();
	protected String trxName = null;
	
	// Main
	protected Timestamp m_fechaFacturas = null;
	protected boolean m_allInvoices = true;
	protected int C_BPartner_ID = 0;
	protected int AD_Org_ID = 0;
	protected MBPartner BPartner = null;
    protected int C_Currency_ID = Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" );
    private MCurrency mCurency = MCurrency.get(m_ctx, C_Currency_ID);
    private Integer projectID = 0;
    private Integer campaignID = 0;
    
    protected boolean m_esPagoNormal = true;
    protected BigDecimal m_montoPagoAnticipado = null;
    
    private boolean m_actualizarFacturasAuto = true;
    
	// Tree de Medios de pago
	private DefaultTreeModel m_arbolModel = null;
	private MyTreeNode m_nodoRaiz = null;
	private MyTreeNode m_nodoRetenciones = null;
	private MyTreeNode m_nodoMediosPago = null;

	// Medios de pago
	private Vector<MedioPago> m_mediosPago = new Vector<MedioPago>();
	
	// Retenciones
	private GeneratorRetenciones m_retGen = null;
	private Vector<RetencionProcessor> m_retenciones = new Vector<RetencionProcessor>();
	
	// Table Facturas
	protected OpenInvoicesTableModel m_facturasTableModel;
	// private Vector<BigDecimal> m_facturasManualAmounts = null;
	protected Vector<ResultItem> m_facturas = null;
	// Payments creados
	private HashMap<Integer, MPayment> mpayments = new HashMap<Integer, MPayment>();
	private Map<Integer, MCashLine> mCashLines = new HashMap<Integer, MCashLine>();
	private Map<PO, Object> aditionalWorkResults = new HashMap<PO, Object>();
	protected List<AllocationLine> onlineAllocationLines = new ArrayList<AllocationLine>();
	//
	private int m_newlyCreatedC_AllocationHeader_ID = -1;
	
	private String msgAMostrar;
	
	private Map<String, String> msgMap = new HashMap<String, String>();
	
	private int errorNo = 0;
	
	private boolean retencionIncludedInMedioPago = false;
	
	// Nro de documento seteado en la allocation
	private String documentNo;
	
	private String description="";
	
	public VOrdenPagoModel() {
		getMsgMap().put("TenderType", "TenderType");
		initTrx();
		m_facturasTableModel = getInvoicesTableModel();
		m_facturasTableModel.addTableModelListener(this);
	}
	
	/**
	 * @return un nuevo table model para la tabla de facturas
	 */
	protected OpenInvoicesTableModel getInvoicesTableModel(){
		return new OpenInvoicesTableModel();
	}
	
	public void dispose() {
		closeTrx();
	}
	
	public void initTrx() {
		closeTrx();
		trxName = super.toString() + "_" + Thread.currentThread().getId() +  "_" + System.currentTimeMillis();
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
				} catch (Exception e) {}
		}
	}
	
	public String getTrxName() { 
		return trxName;
		//return m_trx != null ? m_trx.getTrxName() : null;
	}
	
	public TableModel getFacturasTableModel() {
		return VModelHelper.HideColumnsTableModelFactory( m_facturasTableModel );
	}
	
	protected void initTreeModel() {
		String nodoRaizMsg ;
		String nodoRetencionesMsg = Msg.getElement(m_ctx, "C_Withholding_ID");
		String nodoMediosPagoMsg = Msg.translate(m_ctx, getMsgMap().get("TenderType"));
		String bpName = BPartner != null ? BPartner.getName() + " - " : "";
		
		String monto = numberFormat(getSumaTotalPagarFacturas());
		
		nodoRaizMsg = bpName + Msg.getElement(m_ctx, "Amount") + ": " + monto;
		
		// Crear los nodos base o actualizar sus mensajes
		
		if (m_nodoRaiz == null) {
			m_nodoRaiz = new MyTreeNode(nodoRaizMsg, null, false);
		} else {
			m_nodoRaiz.setMsg(nodoRaizMsg);
			m_nodoRaiz.removeAllChildren();
		}
		
		if (m_nodoRetenciones == null)
			m_nodoRetenciones = new MyTreeNode(nodoRetencionesMsg, null, false);
		else
			m_nodoRetenciones.setMsg(nodoRetencionesMsg);
		
		if (m_nodoMediosPago == null)
			m_nodoMediosPago = new MyTreeNode(nodoMediosPagoMsg, null, false);
		else
			m_nodoMediosPago.setMsg(nodoMediosPagoMsg);
		
		// Agrego los hijos del nodo raiz
		
		if (m_retenciones.size() > 0) 
			m_nodoRaiz.add(m_nodoRetenciones);
		
		m_nodoRaiz.add(m_nodoMediosPago);
		
		// Actualizo el Modelo 
		
		if (m_arbolModel == null)
			m_arbolModel = new DefaultTreeModel(m_nodoRaiz);
		else
			m_arbolModel.setRoot(m_nodoRaiz);
	}
	
	protected void updateTreeModel() {
		initTreeModel();
		
		// Agrego las retenciones
		
		if (m_nodoRetenciones != null) {
			m_nodoRetenciones.removeAllChildren();
			
			for (RetencionProcessor r : m_retenciones)
				m_nodoRetenciones.add(new MyTreeNode(r.getRetencionTypeName() + ": " + numberFormat( r.getAmount() ), r, true));
		}
		
		// Agrego los medios de pago
		
		if (m_nodoMediosPago != null) {
			m_nodoMediosPago.removeAllChildren();
			
			for (MedioPago mp : m_mediosPago) 
				m_nodoMediosPago.add(new MyTreeNode(null, mp, true));
		}
		
		m_arbolModel.nodeStructureChanged(m_nodoRaiz);
	}
	
	public TreeModel getMediosPagoTreeModel() {
		updateTreeModel();
		return m_arbolModel;
	}
	
	public void addMedioPago(MedioPago mp) {
		m_mediosPago.add(mp);
		updateAddingMedioPago(mp);
		updateTreeModel();
	}
	
	public void removeMedioPago(MedioPago mp) {
		m_mediosPago.remove(mp);
		updateRemovingMedioPago(mp);
		updateTreeModel();
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
		return " C_Cash.DocStatus = 'DR' ";
	}
	
	public String getChequeChequeraSqlValidation() {
		return " EXISTS (SELECT * FROM C_BankAccount ba55 WHERE ba55.BankAccountType = 'C' AND ba55.C_BankAccount_ID = C_BankAccountDoc.C_BankAccount_ID) AND C_BankAccountDoc.PaymentRule = 'S' ";
	}
	
	public String getCreditSqlValidation() {
		return " C_Invoice.DocStatus IN ('CO','CL') " +
			   " AND C_Invoice.NotExchangeableCredit = 'N' " +
			   " AND EXISTS (SELECT C_DocType_ID FROM C_DocType dt WHERE C_Invoice.C_DocType_ID = dt.C_DocType_ID AND dt.Signo_IsSOTrx = " + (getSignoIsSOTrx() * -1) + ") " +
			   " AND C_Invoice.C_BPartner_ID = @C_BPartner_ID@ ";
	}
	
	public void setFechaTablaFacturas(Timestamp fecha, boolean all) {
		m_fechaFacturas = fecha;
		m_allInvoices = all;
		if (m_actualizarFacturasAuto)
			actualizarFacturas();
	}

	/**
	 * Actualizo la info de la entidad comercial
	 * 
	 * @param bPartnerID
	 *            id de nueva la entidad comercial, null caso que no exista
	 *            alguna
	 */
	public void updateBPartner(Integer bPartnerID){
		if(bPartnerID == null)bPartnerID = 0;
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
			initTrx();
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
	 * @return En caso de pago normal, la suma de los montos a pagar de cada factura. Sino, el monto ingresado manualmente.
	 */
	public BigDecimal getSumaTotalPagarFacturas() {
		BigDecimal suma = new BigDecimal(0);
		
		if (m_esPagoNormal) {
			if (m_facturas != null) {
				for (ResultItem x : m_facturas)
					suma = suma.add(((ResultItemFactura)x).getManualAmount());
			}
		} else {
			suma = m_montoPagoAnticipado;
		}
		
		return suma;
	}
	
	public void setActualizarFacturasAuto(boolean actualizarFacturasAuto) {
		m_actualizarFacturasAuto = actualizarFacturasAuto;
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
		
		sql.append(" SELECT c_invoice_id, 0, orgname, documentno,max(duedate) as duedatemax, sum(convertedamt) as convertedamtsum, sum(openamt) as openAmtSum FROM ");
		sql.append("  (SELECT i.C_Invoice_ID, i.C_InvoicePaySchedule_ID, org.name as orgname, i.DocumentNo, coalesce(duedate,dateinvoiced) as DueDate, "); // ips.duedate
		sql.append("    abs(currencyConvert( i.GrandTotal, i.C_Currency_ID, ?, i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID)) as ConvertedAmt, ");
		sql.append("    currencyConvert( invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)), i.C_Currency_ID, ?, i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID) AS openAmt ");
		sql.append("  FROM c_invoice_v AS i ");
		sql.append("  LEFT JOIN ad_org org ON (org.ad_org_id=i.ad_org_id) ");
		sql.append("  LEFT JOIN c_invoicepayschedule AS ips ON (i.c_invoicepayschedule_id=ips.c_invoicepayschedule_id) ");
		sql.append("  INNER JOIN C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
		sql.append("  WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
		sql.append("    AND i.IsSOTRx = '" + getIsSOTrx() + "' AND GrandTotal <> 0.0 AND C_BPartner_ID = ? ");
		sql.append("    AND dt.signo_issotrx = " + getSignoIsSOTrx());
		
		if (AD_Org_ID != 0) 
			sql.append("  AND i.ad_org_id = ?  ");
		
		sql.append("  ORDER BY org.name ASC, i.c_invoice_id, i.DocumentNo ASC, DueDate ASC ) as openInvoices ");
		sql.append(" GROUP BY c_invoice_id, orgname, documentno,c_invoicepayschedule_id ");
		sql.append(" HAVING sum(openamt) > 0.0 ");
		if (!m_allInvoices)
			sql.append("  AND ( max(duedate) IS NULL OR max(duedate) <= ? ) ");
	
		sql.append(" ORDER BY max(DueDate)");
		
		try {
			
			CPreparedStatement ps = DB.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, getTrxName());
			
			ps.setInt(i++, C_Currency_ID);
			ps.setInt(i++, C_Currency_ID);
			ps.setInt(i++, C_BPartner_ID);
			
			if (AD_Org_ID != 0)
				ps.setInt(i++, AD_Org_ID);
			
			if (!m_allInvoices)
				ps.setTimestamp(i++, m_fechaFacturas);

			ResultSet rs = ps.executeQuery();
			//int ultimaFactura = -1;
			while (rs.next()) {
				ResultItemFactura rif = new ResultItemFactura(rs);
				int facId = ((Integer)rif.getItem(m_facturasTableModel.getIdColIdx()));
				
				//if (facId != ultimaFactura) {
					m_facturas.add(rif);
					//ultimaFactura = facId;
				//}
			}
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		
		m_facturasTableModel.fireChanged(false);
		
	}
	
	// Added by Lucas Hernandez - Kunan
	public boolean buscarPagos(Integer bpartner) {

		boolean res=false;
		int cantidadPagos=0;
				
		for(int i=0; i<3;i++){
		StringBuffer sql = new StringBuffer();
		if(i==0){
			sql.append(" SELECT COUNT(*)");				
			sql.append(" FROM libertya.c_payment i");
			sql.append(" INNER JOIN libertya.C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
			sql.append(" WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
			sql.append(" AND i.C_BPartner_ID = ? ");
			sql.append(" AND dt.DocTypeKey = 'VP'");
			sql.append(" AND libertya.paymentavailable(i.c_payment_id) > 0");
			sql.append(" AND i.ad_org_id = ?  ");
		}
		if(i==1){
			sql.append(" SELECT COUNT(*)");				
			sql.append(" FROM libertya.c_invoice i");
			sql.append(" INNER JOIN libertya.C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
			sql.append(" WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
			sql.append(" AND i.C_BPartner_ID = ? ");
			sql.append(" AND dt.DocBaseType = 'APC'");
			sql.append(" AND libertya.invoiceopen(i.c_invoice_id,null) > 0");
			sql.append(" AND i.ad_org_id = ?  ");
			sql.append(" AND dt.signo_issotrx=1 ");
		}
		if(i==2){
			sql.append(" SELECT COUNT(*)");				
			sql.append(" FROM libertya.c_cashline i");
			sql.append(" WHERE i.IsActive = 'Y' AND i.DocStatus IN ('CO', 'CL') ");
			sql.append(" AND i.C_BPartner_ID = ? ");
			sql.append(" AND SIGN(i.amount)<0 ");
			sql.append(" AND libertya.cashlineavailable(i.c_cashline_id) <> 0 ");
			sql.append(" AND i.ad_org_id = ?  ");
		}
		try {			
			CPreparedStatement ps = DB.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, getTrxName());			
			ps.setInt(1, bpartner);			
			ps.setInt(2, AD_Org_ID);			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				cantidadPagos+=rs.getInt(1);
			}			
		} catch (Exception e) {
			log.log(Level.SEVERE, "", e);
		}
		}		
		
		if(cantidadPagos>0){
			res=true;
		}
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
		
		//suma = suma.subtract(getSumaRetenciones()).subtract(getSumaMediosPago());
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
		
		if(C_BPartner_ID == 0){
			return PROCERROR_NOT_SELECTED_BPARTNER;
		}
		
		if(documentNo.length()==0){
			return PROCERROR_DOCUMENTNO_NOT_SET;
		}	
		
		if (documentNoAlreadyExists()){
			return PROCERROR_DOCUMENTNO_ALREADY_EXISTS;
		}
		
			
		Vector<BigDecimal> manualAmounts = new Vector<BigDecimal>();
		Vector<Integer> facturasProcesar = new Vector<Integer>();
		Vector<ResultItemFactura> resultsProcesar = new Vector<ResultItemFactura>();
		
		for (ResultItem f : m_facturas) {
			ResultItemFactura fac = (ResultItemFactura) f;
			if (fac.getManualAmount().signum() > 0) {
				facturasProcesar.add((Integer)fac.getItem(m_facturasTableModel.getIdColIdx()));
				manualAmounts.add(fac.getManualAmount());
				resultsProcesar.add(fac);
			}
		}

		// Actualización del modelo en base a las facturas efectivas a pagar
		updateInvoicesModel(facturasProcesar, manualAmounts, resultsProcesar);
		// Actualización de información adicional del modelo
		updateAditionalInfo();
		// Calcula las retenciones a aplicarle a la entidad comercial
		if(!isSOTrx() || m_retenciones == null || m_retenciones.size() == 0){
			m_retGen = new GeneratorRetenciones(C_BPartner_ID, facturasProcesar, manualAmounts, total, isSOTrx());
			m_retGen.setTrxName(getTrxName());
			calculateRetencions();
			m_retenciones = m_retGen.getRetenciones();
		}
		
		updateTreeModel();
		
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
				BigDecimal b0 = ((ResultItemFactura)arg0).getManualAmount();
				BigDecimal b1 = ((ResultItemFactura)arg1).getManualAmount();
				
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
			if (f.getManualAmount().signum() > 0) {
				tmp.add(f);
			}
		}

		facturas.clear();
		facturas.addAll(tmp);
	}
	
	public BigDecimal currencyConvert(BigDecimal fromAmt, int fromCurency, int toCurrency, Timestamp convDate) throws Exception {
		// TODO: Si converto varias veces, que hago?
		// return Currency.convert(fromAmt, fromCurency, toCurrency, convDate, 0, Env.getAD_Client_ID(m_ctx), Env.getAD_Org_ID(m_ctx));
		
		// HACK: Can't invoke Currency.convert directly !!
		
		CPreparedStatement pp = DB.prepareStatement(" SELECT currencyConvert(?, ?, ?, ?, ?, ?, ?) ");
		
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
		if (a.compareTo(b) == 0)
			return 0;

		double d = a.subtract(b).doubleValue();
		if (d > -0.01 && d < 0.01)
			return 0;
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
		return Msg.parseTranslation(getCtx(), name + " " + getInvoicesDate().toString().substring(0,10) + " [" + name + " " + getHdrAllocationType() + "]");
	}
		
	/**
	 * Establece el signo (positivo o negativo) a la cantidad pasada como parámetro
	 * 
	 * @param cantidad
	 * @param signo
	 * @return la cantidad pasada como parámetro con el nuevo signo
	 * @author Cacho
	 */
	
	private BigDecimal establecerSigno(BigDecimal cantidad, BigDecimal signo){
		//Multiplico la cantidad por el signo
		return cantidad.multiply(signo);
	}
	
	/** Genera los registros de MPayment o MCashLine, asigna los montos y los completa.
	 * 
	 * @param pagos lista de pagos
	 * @return
	 * @throws Exception
	 */
	private boolean generarPagosDesdeMediosPagos(List<MedioPago> pagos) throws Exception {
		
		String HdrDescription = getAllocHdrDescription();
		boolean saveOk = true;
		String errorMsg = null;
		
		for (MedioPago mp : pagos) {
			
			MPayment pay ;
			MCashLine line ;
			
			BigDecimal convertedAmt = mp.getImporte();
			BigDecimal DiscountAmt = Env.ZERO;
			BigDecimal WriteoffAmt = Env.ZERO;
			BigDecimal OverunderAmt = Env.ZERO;
			
			if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_CREDITO)) {
				// No se debe generar ningún pago dado que es una nota de crédito
				// existente.
			} else if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_EFECTIVO))
			{
				MedioPagoEfectivo mpe = (MedioPagoEfectivo) mp;
				
				// Hay que crear una linea de caja.
				
				MCash cash = new MCash(m_ctx, mpe.libroCaja_ID, getTrxName());
				line = new MCashLine(cash);
				
				line.setDescription(HdrDescription);
				
				/**
				 * 2010-08-05 - Modificado.  Basandose en el campo isGenerated, ahora
				 * es posible indicar una linea de caja Invoice y no setearle el ID de factura.
				 * Es por esto que vuelve a indicarse el tipo a CASHTYPE_Invoice 
				 */
				// Error: al asignar CASHTYPE_Invoice y no setearle el ID de factura, 
				// el beforeSave() de MCashLine asigna GeneralExpense como CashType.
				line.setCashType(MCashLine.CASHTYPE_Invoice);
				// ***
				// line.setCashType(getCashType());
				
				
				 /**
				  * 2010-08-05 - Comentado el siguiente bloque de código: 
				  * Dado que un cashline puede pagar varias deudas, el dato C_Invoice_ID en C_CashLine lleva a confusiones.
				  * Se incorpora entonces una nueva pestaña en linea de caja para visualizar las cancelaciones relacionadas.
				  */												   
				// si es pago normal, debe indicarse en la linea de caja, que la misma es de tipo Factura
//				if ((m_esPagoNormal) && (m_facturas != null)) 
//				{
//					// buscar la primer factura con monto mayor a cero
//					boolean found = false;
//					int invoiceID = -1;
//					for (int i=0; i < m_facturas.size() && !found; i++)
//					{
//						ResultItem x = m_facturas.get(i);
//						if (((ResultItemFactura)x).getManualAmount().compareTo(Env.ZERO) != 0)
//						{
//							found = true;
//							invoiceID = (Integer)((ResultItemFactura)x).getItem(m_facturasTableModel.getIdColIdx());
//						}
//					}
//					line.setCashType(MCashLine.CASHTYPE_Invoice);
//					line.setC_Invoice_ID(invoiceID); 
//				}

				if (mp.getProject() != null)
					line.setC_Project_ID(mp.getProject());				
				
				line.setC_BPartner_ID(C_BPartner_ID);
				
				//Establezco el signo del monto
				convertedAmt = establecerSigno(convertedAmt, new BigDecimal(getSignoIsSOTrx()));
				
				asignarMontoPago(line, C_Currency_ID, convertedAmt, DiscountAmt, WriteoffAmt, OverunderAmt);
				
				line.setIgnoreAllocCreate(true); // Evita la creación del allocation al completar la línea.
				// No actualizo el saldo de la entidad comercial
				line.setUpdateBPBalance(false);
				line.setC_POSPaymentMedium_ID(mp.getPaymentMedium().getID());
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
				
				// Si hay error levanta una excepción con el mensaje del logger para
				// cortar la ejecución de todos los procesos.
				if (!saveOk) {
					errorNo = PROCERROR_PAYMENTS_GENERATION;
					throw new Exception(errorMsg);
				}
				
				// El amount y currency se lo asigno en la segunda vuelta
				
				mpe.setCashLine(line);
				getMCashLines().put(line.getID(), line);
			} else 
			{	
				
				// Crear un nuevo pago a partir del medio de pago (si es anticipado NO debe crearse el pago, ya que el mismo existe)
				if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_PAGOANTICIPADO)
						|| mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_EFECTIVOADELANTADO))
					continue;
				
				pay = new MPayment(m_ctx, 0, getTrxName());
				
				if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) {
					MedioPagoChequeTercero mpct = (MedioPagoChequeTercero)mp;
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
				
				if (!mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) {
					String RoutingNo = VModelHelper.getSQLValueString(null, " select routingno from c_bank inner join c_bankaccount on (c_bank.c_bank_id=c_bankaccount.c_bank_id) where c_bankaccount.c_bankaccount_id = ? ", mp.getBankAccountID() );
					String AccountNo = VModelHelper.getSQLValueString(null, " select AccountNo from c_bankaccount where c_bankaccount.c_bankaccount_id = ? ", mp.getBankAccountID() );
					
					pay.setRoutingNo(RoutingNo);
					pay.setAccountNo(AccountNo);
				}

				if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_TRANSFERENCIA))
				{
					MedioPagoTransferencia mpt = (MedioPagoTransferencia) mp;
					
					pay.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
					
					pay.setCheckNo(mpt.nroTransf); // Numero de cheque
					
					mpt.setPayment(pay);
				}
				else if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_CHEQUE))
				{
					MedioPagoCheque mpc = (MedioPagoCheque) mp;
					String sucursal = VModelHelper.getSQLValueString(null, " select AccountNo from c_bankaccount where c_bankaccount.c_bankaccount_id = ? ", mp.getBankAccountID() );
					
					pay.setCheckNo(mpc.nroCheque); // Numero de cheque
					pay.setMicr(sucursal + ";" + mp.getBankAccountID() + ";" + mpc.nroCheque); // Sucursal; cta; No. cheque
					pay.setA_Name(mpc.aLaOrden); // Nombre
					
					pay.setTenderType(MPayment.TENDERTYPE_Check);
					pay.setA_Bank(mpc.banco);
					pay.setA_CUIT(mpc.cuitLibrador);

					if (mpc.descripcion != null && mpc.descripcion.length() > 0)
						pay.addDescription(mpc.descripcion);
					
					// Fecha Vto
					pay.setDueDate(mpc.getDueDate());
					mpc.setPayment(pay);
				}
				else if (mp.getTipoMP().equals(MedioPago.TIPOMEDIOPAGO_CHEQUETERCERO)) {
					MedioPagoChequeTercero mpct = (MedioPagoChequeTercero)mp;
					if (mpct.description != null && mpct.description.length() > 0)
						pay.addDescription(mpct.description);
					mpct.setPayment(pay);
					// Se cierra el cheque de tercero para que no vuelva a ser utilizado
					MPayment chequeTercero = mpct.getChequeTerceroPayment();
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
				addCustomPaymentInfo(pay,mp);
				// -- 
				
				asignarMontoPago(pay, C_Currency_ID, convertedAmt, DiscountAmt, WriteoffAmt, OverunderAmt);
				
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
				
				// Manejo correcto de errores. Levanta excepción y corta todo, como debe ser.
				// Se asigna el errorNo para matener compatibilidad con la vieja lógica de retornar
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
	
	private void asignarMontoPago(MCashLine line, int pC_Currency_ID, BigDecimal convertedAmt, BigDecimal DiscountAmt, BigDecimal WriteoffAmt, BigDecimal OverunderAmt ) {
		
		line.setC_Currency_ID(pC_Currency_ID);
		line.setAmount(convertedAmt);
		line.setDiscountAmt(DiscountAmt);
		line.setWriteOffAmt(WriteoffAmt);
		line.setIsGenerated(true);
		
	}
	
	private void asignarMontoPago(MPayment pay, int pC_Currency_ID, BigDecimal convertedAmt, BigDecimal DiscountAmt, BigDecimal WriteoffAmt, BigDecimal OverunderAmt ) {
		
		pay.setAmount(pC_Currency_ID, convertedAmt);
		pay.setDiscountAmt(DiscountAmt);
		pay.setWriteOffAmt(WriteoffAmt);
		pay.setOverUnderAmt(OverunderAmt);
		
	}
	
	public int doPostProcesar() {
		
		BigDecimal saldoMediosPago = getSaldoMediosPago(); // Debe ser cero
		
		if (saldoMediosPago.abs().compareTo(VModelHelper.GetRedondeoMoneda(m_ctx, C_Currency_ID)) >= 0)
			return PROCERROR_PAYMENTS_AMT_MATCH;
		
		int ret ;
		
		if (m_esPagoNormal)
			ret = doPostProcesarNormal();
		else
			ret = doPostProcesarAdelantado();
		
		return ret;
	}
	
	private int doPostProcesarAdelantado() {
		errorNo = PROCERROR_OK;
		Trx trx = getTrx();
		MAllocationHdr hdr = null;
		boolean saveOk = true;
		try {
			
			// 1. Ordenar los pagos y devolverlos 
			
			Vector<MedioPago> pagos = new Vector<MedioPago>( ordenarMediosPago() );
			
			// 2. Crear el AllocatoinHdr para la OPA
			hdr = createAllocationHdr();
			saveOk = hdr.save();
			
			// 3. Generar los pagos
			
			if (saveOk && !generarPagosDesdeMediosPagos(pagos) ) {
				saveOk = false;
				errorNo = PROCERROR_PAYMENTS_GENERATION;
			}
			
			// 4. Guardar Retenciones
			m_retGen.setProjectID(getProjectID());
			m_retGen.setCampaignID(getCampaignID());
			m_retGen.save(hdr);
			
			// 5. Agregar retenciones como medio de pago
			agregarRetencionesComoMediosPagos(pagos, hdr);
			
			// 6. Se crean las AllocationLine por cada pago. El monto de la línea
			// se asigna a 0 para que no surta efecto en los pendientes de los documentos.
			for (MedioPago mp : pagos) {
				
				BigDecimal DiscountAmt = Env.ZERO;
				BigDecimal WriteoffAmt = Env.ZERO;
				BigDecimal OverunderAmt = Env.ZERO;
				BigDecimal amount = Env.ZERO;        // Monto en CERO. Requerido en OPA para evitar que se modifiquen los importes pendientes de los pagos.
				
				MAllocationLine allocLine = 
					new MAllocationLine( hdr, amount, DiscountAmt, WriteoffAmt, OverunderAmt);
				
				mp.setAllocationInfo(allocLine);
				allocLine.setAmount(amount);        // Por si el MP cambió el amount.
				allocLine.setC_BPartner_ID(C_BPartner_ID);
				
				// Se guarda el AllocationLine sin asignar ID de Factura C_Invoice_ID, dado
				// esta línea no corresponde a una imputación sino a una línea de la OPA.
				saveOk = saveOk && allocLine.save();
				
				if (!saveOk)
					break;
			}
			
			// 99. Completar HDR
			
			if( saveOk && hdr.getID() != 0 ) {
				saveOk = saveOk && hdr.processIt( DocAction.ACTION_Complete );
				saveOk = saveOk && hdr.save( getTrxName());
	        }
			
			// Realizar las tareas de cuenta corriente previas a terminar el
			// procesamiento
			performAditionalCurrentAccountWork();
			
			// Si estuvo todo bien entonces realizo las tareas posteriores a
			// terminar el procesamiento y confirmación de transacción
			afterProcessDocuments();
			
			// Procesar los pagos y asignarles el monto.
			
			if (saveOk && errorNo == PROCERROR_OK) {
				trx.commit();
				m_newlyCreatedC_AllocationHeader_ID = hdr.getC_AllocationHdr_ID();
			}
				
		} catch (Exception e) {
			log.log(Level.SEVERE, "doPostProcesarAdelantado", e);
			errorNo = PROCERROR_UNKNOWN;
			setMsgAMostrar(e.getMessage());
		}
		
		if (!saveOk || errorNo != PROCERROR_OK)
			trx.rollback();
		
		trx.close();
		
		return errorNo;
	}
	
	private void agregarRetencionesComoMediosPagos(Vector<MedioPago> pagos, MAllocationHdr hdr) throws Exception {
		
		String sql = " SELECT C_Invoice_ID FROM m_retencion_invoice WHERE C_AllocationHdr_ID = ? ";
        CPreparedStatement pp = DB.prepareStatement(sql, getTrxName());
        
    	pp.setInt(1, hdr.getC_AllocationHdr_ID());
    	
    	ResultSet rs = pp.executeQuery();
    	
    	while (rs.next()) {
    		MInvoice invoice = new MInvoice(m_ctx, rs.getInt(1), getTrxName());
    		MedioPagoCreditoRetencion rc = new MedioPagoCreditoRetencion(invoice);
    		
    		pagos.add(rc);
    	}
		
    	pp.close();
    	rs.close();
    	
    	retencionIncludedInMedioPago = true;
	}
	
	private int doPostProcesarNormal() {
		
		Trx trx = getTrx();
		
		MAllocationHdr hdr = null;
		
		// BEGIN //
		boolean saveOk = false;
		errorNo = PROCERROR_UNKNOWN;
		Vector<MedioPago> pays = getMediosPago();

		try 
		{	
			// 1. Crear débitos y créditos customs
			
			makeCustomDebitsCredits(pays);
			
			// 2. Allocation Header
			
			hdr = createAllocationHdr();
			saveOk = hdr.save();

			// 3. Retenciones
			m_retGen.setProjectID(getProjectID());
			m_retGen.setCampaignID(getCampaignID());
			m_retGen.save(hdr);
			
			// 4. Generar los pagos, completos.
			
			if (saveOk && !generarPagosDesdeMediosPagos(pays)) {
				saveOk = false;
				errorNo = PROCERROR_PAYMENTS_GENERATION;
			}
			
			// 5. Agregar las facturas de debito 
			
			agregarRetencionesComoMediosPagos(pays, hdr);
			
			// 6. Guardar allocation lines online creadas
			
			processOnlineAllocationLines(hdr, pays, true);
			
			// 98. Completar HDR
			
			if( saveOk && hdr.getID() != 0 ) {
				saveOk = saveOk && hdr.processIt( DocAction.ACTION_Complete );
				if(!saveOk){
					throw new Exception(hdr.getProcessMsg());
				}
				saveOk = saveOk && hdr.save( getTrxName());
				if(!saveOk){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
	        }
			
			// 99. Realizar operaciones custom al final del procesamiento
			doPostProcesarNormalCustom();
			
			// Realizar las tareas de cuenta corriente previas a terminar el
			// procesamiento
			performAditionalCurrentAccountWork();
			
			// Si estuvo todo bien entonces realizo las tareas posteriores a
			// terminar el procesamiento y confirmación de transacción
			afterProcessDocuments();
			
			if (saveOk)
				saveOk = trx.commit();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "doPostProcesarNormal", e);
			saveOk = false;
			setMsgAMostrar(e.getMessage());
		}

		if (!saveOk) {
			trx = Trx.get(trx.getTrxName(),false);
			trx.rollback();
			trx.close();
		} else {
			if (trx.close()) 
				m_newlyCreatedC_AllocationHeader_ID = hdr.getC_AllocationHdr_ID();
		}
		
		if (saveOk) {
			errorNo = PROCERROR_OK;
		}
		
		return errorNo;
	}

	/**
	 * Realizar operaciones custom luego de completar el allocation, al final
	 * del procesamiento de la orden de pago
	 * 
	 * @throws Exception en caso de error
	 */
	public void doPostProcesarNormalCustom() throws Exception{
		// Por ahora no hace nada aquí
	}
	
	public void mostrarInforme( ASyncProcess asyncProc ) {

		if (m_newlyCreatedC_AllocationHeader_ID <= 0)
			return;
		
        int proc_ID = DB.getSQLValue( null, "SELECT AD_Process_ID FROM AD_Process WHERE value='" + getReportValue()+ "' " );

        if( proc_ID > 0 ) {

        	MPInstance instance = new MPInstance( Env.getCtx(), proc_ID, 0, null );
            if( !instance.save()) {
            	log.log(Level.SEVERE, "Error at mostrarInforme: instance.save()");
                return;
            }

            ProcessInfo pi = new ProcessInfo( "Orden de Pago",proc_ID );
            pi.setAD_PInstance_ID( instance.getAD_PInstance_ID());
            
            MPInstancePara ip;
            
            /*
            ip = new MPInstancePara( instance, 10 );
            ip.setParameter( "C_Allocation_detail_v_ID",String.valueOf( m_newlyCreatedC_AllocationHeader_ID ));
            if( !ip.save()) {
            	log.log(Level.SEVERE, "Error at mostrarInforme: ip.save()");
                return;
            }
            */
            
            ip = new MPInstancePara( instance, 10 );
            ip.setParameter( "C_AllocationHdr_ID",String.valueOf( m_newlyCreatedC_AllocationHeader_ID ));
            if( !ip.save()) {
            	log.log(Level.SEVERE, "Error at mostrarInforme: ip.save()");
                return;
            }
            
            ProcessCtl worker = new ProcessCtl( asyncProc, pi, null );
            worker.start();
        }
        
		// Las subclases deberían poder imprimir los comprobantes que deseen
        printCustomDocuments(asyncProc);
	}
	
	public void tableChanged(TableModelEvent arg0) {
		if (arg0.getColumn() == m_facturasTableModel.getColumnCount() - 1) {
			
			// Se actualizó el monto manual
			
			int amtColIdx = m_facturasTableModel.getOpenAmtColIdx(); 
			
			for (int row = arg0.getFirstRow(); row <= arg0.getLastRow() && row < m_facturas.size(); row++) {
				ResultItemFactura rif = (ResultItemFactura)m_facturas.get(row);
				BigDecimal manualAmt = rif.getManualAmount();
				BigDecimal openAmt = (BigDecimal) m_facturas.get(row).getItem(amtColIdx);
				// Sumar o restar algún monto custom
				openAmt = openAmt.subtract(rif.getPaymentTermDiscount());
				
				if (manualAmt == null || manualAmt.signum() < 0)
					rif.setManualAmount(BigDecimal.ZERO);
				else if (manualAmt.compareTo(openAmt) > 0) 
					rif.setManualAmount(openAmt);
			}
		}	
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
		return new BigDecimal( nf.parse(nn).toString() );
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

	protected String getIsSOTrx() {
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
	protected Map<String, String> getMsgMap() {
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
	
	protected boolean isSOTrx() {
		return "Y".equals(getIsSOTrx());
	}
	
	protected Timestamp getInvoicesDate() {
		return m_fechaFacturas;
	}
	
	protected boolean isNormalPayment()  {
		return m_esPagoNormal;
	}
	
	public Properties getCtx() {
		return m_ctx;
	}
	
	protected String getCashType() {
		return MCashLine.CASHTYPE_GeneralExpense;
	}

	public BigDecimal getCreditAvailableAmt(int invoiceID) {
		try
		{
			BigDecimal AvailableamountToConvert = (BigDecimal)DB.getSQLObject(null, "SELECT invoiceOpen(?, 0)", new Object[] { invoiceID });
			return currencyConvert(AvailableamountToConvert, DB.getSQLValue(null, "SELECT C_Currency_ID From C_Invoice where C_Invoice_ID = " + invoiceID), getC_Currency_ID(), new Timestamp(new java.util.Date().getTime()));
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * @return Retorna la clave de busca del Informe y Proceso que muestra la Orden de pago.
	 */
	protected String getReportValue() {
		return "Orden de Pago";
	}
	
	public class MedioPagoAdelantado extends MedioPago {

		public int C_Payment_ID;
		public BigDecimal importe;
		
		public MedioPagoAdelantado(int payment_ID, BigDecimal importe) {
			super();
			C_Payment_ID = payment_ID;
			this.importe = importe;
		}
		
		public int getC_Payment_ID() {
			return C_Payment_ID;
		}
		
		public void setC_Payment_ID(int payment_ID) {
			C_Payment_ID = payment_ID;
		}
		public BigDecimal getImporte() {
			return importe;
		}
		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		@Override
		public int getBankAccountID() {
			return DB.getSQLValue(null, " SELECT C_BankAccount_ID FROM c_payment WHERE C_Payment_ID = ? ", C_Payment_ID);
		}

		@Override
		public Timestamp getDateAcct() {
			return VModelHelper.getSQLValueTimestamp(null, " SELECT dateacct FROM c_payment WHERE C_Payment_ID = ? ", C_Payment_ID);
		}

		@Override
		public Timestamp getDateTrx() {
			return VModelHelper.getSQLValueTimestamp(null, " SELECT datetrx FROM c_payment WHERE C_Payment_ID = ? ", C_Payment_ID);
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_PAGOANTICIPADO;
		}
		
		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_Payment_ID(getC_Payment_ID());
		}
	}
	
	public String getPagoAdelantadoSqlValidation() {
		// Pagos a proveedores cuyo monto pendiente a alocar sea mayor a cero
		return " IsReceipt = '" + getIsSOTrx() + "' " +
				" AND paymentavailable(C_Payment_ID) > 0 " +
				" AND DocStatus in ('CO', 'CL')" +
				" AND C_Payment.C_BPartner_ID = @C_BPartner_ID@ ";
	}	
	
	/**
	 * Incorpora un cobro adelantado
	 * @param payID
	 * @param amount
	 * @throws Exception
	 */
	public MedioPago addPagoAdelantado(Integer payID, BigDecimal amount, boolean isCash) throws Exception {
		if (payID == null || payID == 0)
			throw new Exception("@FillMandatory@ " + (isCash ? getMsg("Cash") : getMsg("Payment")));
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new Exception("@NoAmountError@");
		if ((isCash && findMedioPago(MedioPagoEfectivoAdelantado.class, payID) != null)
				|| (!isCash && findMedioPago(MedioPagoAdelantado.class, payID) != null))
			throw new Exception(Msg.getMsg(getCtx(), "POPaymentExistsError", new Object[] { getMsg("AdvancedPayment"), getMsg("Payment") }));

		// El disponible del cobro/pago es mayor al monto ingresado?
		BigDecimal payAvailable = (isCash ? getCashAdelantadoAvailableAmt(payID) : getPagoAdelantadoAvailableAmt(payID)); 
		if (payAvailable.compareTo(amount) < 0 )
			throw new Exception("@AmountGreatherThanAvalaibleError@ ("+ payAvailable +")");

		MedioPago mp = null;
		if (isCash)
			mp = new MedioPagoEfectivoAdelantado(payID, amount);
		else
			mp = new MedioPagoAdelantado(payID, amount);
		
		return mp;
	}
	
    public int getC_Currency_ID() {
		return C_Currency_ID;
	}
    
    /**
     * @return Crea y retorna el AllocationHdr que representa el encabezado de una OP u OPA.
     */
    private MAllocationHdr createAllocationHdr() {
		BigDecimal sumaRetenciones = getSumaRetenciones();
		BigDecimal sumaTotalPagar = getAllocationHdrTotalAmt();
		BigDecimal approvalAmt = sumaTotalPagar.subtract(sumaRetenciones);
    	
    	MAllocationHdr hdr = new MAllocationHdr(m_ctx, 0, getTrxName());
		
		// 3. Asignar al hdr: retenciones, total a pagar e importe efectivamente pagado

		// TODO: Siempre va a ser normal !
		
		hdr.setAllocationType(getHdrAllocationType());

		String HdrDescription ="";
		if(description.compareTo("")==0){
			HdrDescription= getAllocHdrDescription();
		}
		else{
			HdrDescription=description;
		}

		hdr.setApprovalAmt(approvalAmt);
		hdr.setGrandTotal(sumaTotalPagar);
		hdr.setRetencion_Amt(sumaRetenciones);

		hdr.setC_BPartner_ID(C_BPartner_ID);
		hdr.setC_Currency_ID(C_Currency_ID);
		
		hdr.setDateAcct(m_fechaFacturas);
		hdr.setDateTrx(m_fechaFacturas);
		
		hdr.setDescription(HdrDescription);
		hdr.setIsManual(false);
		// No actualizar el saldo del entidad comercial ya que se actualiza al
		// final de todo ya que se debe actualizar al final
		hdr.setUpdateBPBalance(false);
		hdr.setDocumentNo(documentNo);
		// hdr.setDocumentNo(); // Lo asigna PO
		// hdr.setDocAction(); // Default
		// hdr.setDocStatus(); // Default
		// hdr.setIsActive(true); // Default
		// hdr.setIsApproved(); // Default
		// hdr.setPosted(); // Default
		// hdr.setProcessed(); // Default
		// hdr.setProcessing(); // Default
		
		return hdr;
    }
    
	protected String getAllocHdrDescriptionMsg() {
		return "@PaymentOrder@";   
	}
	
	public String getCashAnticipadoSqlValidation() {
		return " ABS(cashlineAvailable(C_CashLine_ID)) > 0 " +
			   " AND SIGN(amount) = " + getSignoIsSOTrx() +
			   " AND C_CashLine.C_BPartner_ID = @C_BPartner_ID@ " +
			   " AND C_CashLine.DocStatus IN ('CO','CL') ";
	}
	
	public BigDecimal getPagoAdelantadoAvailableAmt(int paymentID) {
		try
		{
			BigDecimal AvailableamountToConvert = (BigDecimal)DB.getSQLObject(null, "SELECT paymentAvailable(?)", new Object[] { paymentID });
			return currencyConvert(AvailableamountToConvert, DB.getSQLValue(null, "SELECT C_Currency_ID From C_Payment where C_Payment_ID = " + paymentID), getC_Currency_ID(), new Timestamp(new java.util.Date().getTime()));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public BigDecimal getCashAdelantadoAvailableAmt(int cashLineID) {
		try
		{
			BigDecimal AvailableamountToConvert = (BigDecimal)DB.getSQLObject(null, "SELECT cashLineAvailable(?)", new Object[] { cashLineID });
			return currencyConvert(AvailableamountToConvert, DB.getSQLValue(null, "SELECT C_Currency_ID From C_CashLine where C_CashLine_ID = " + cashLineID), getC_Currency_ID(), new Timestamp(new java.util.Date().getTime())).abs();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	protected String getMsg(String name) {
		String msgName = getMsgMap().get(name);
		if (msgName == null || msgName.length() == 0)
			msgName = name;
		return Msg.translate(getCtx(), msgName);
	}

	public class MedioPagoEfectivoAdelantado extends MedioPago {

		public Integer cashLineID;
		public BigDecimal importe;
		
		/**
		 * @param cashLineID
		 * @param importe
		 */
		public MedioPagoEfectivoAdelantado(Integer cashLineID,
				BigDecimal importe) {
			super();
			this.cashLineID = cashLineID;
			this.importe = importe;
		}

		@Override
		public int getBankAccountID() {
			return -1;
		}

		@Override
		public Timestamp getDateAcct() {
			return VModelHelper.getSQLValueTimestamp(null, " SELECT dateacct FROM c_cashline cl INNER JOIN c_cash c ON (cl.c_cash_id = c.c_cash_id) WHERE C_CashLine_ID = ? ", cashLineID );
		}

		@Override
		public Timestamp getDateTrx() {
			return VModelHelper.getSQLValueTimestamp(null, " SELECT statementdate FROM c_cashline cl INNER JOIN c_cash c ON (cl.c_cash_id = c.c_cash_id) WHERE C_CashLine_ID = ? ", cashLineID);
		}

		@Override
		public BigDecimal getImporte() {
			return this.importe;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_EFECTIVOADELANTADO;
		}

		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_CashLine_ID(cashLineID);			
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
		 * @param cashLineID the cashLineID to set
		 */
		public void setCashLineID(Integer cashLineID) {
			this.cashLineID = cashLineID;
		}
	}
	
	private MedioPago findMedioPago(Class clazz, int id) {
		MedioPago medioPago = null;
		Integer tmpID = null;
		for (Iterator<MedioPago> pagos = getMediosPago().iterator(); pagos.hasNext() && medioPago == null;) {
			MedioPago pago = pagos.next();
			if(pago.getClass().equals(clazz)) {
				if (clazz.equals(MedioPagoAdelantado.class))
					tmpID = ((MedioPagoAdelantado)pago).getC_Payment_ID();
				else if (clazz.equals(MedioPagoEfectivoAdelantado.class))
					tmpID = ((MedioPagoEfectivoAdelantado)pago).getCashLineID();
				else if (clazz.equals(MedioPagoCredito.class))
					tmpID = ((MedioPagoCredito)pago).getC_invoice_ID();
				else if (clazz.equals(MedioPagoChequeTercero.class))
					tmpID = ((MedioPagoChequeTercero)pago).getC_Payment_ID();
				
				if (tmpID.equals(id))
					medioPago = pago;
			}
		}
		return medioPago;
	}
	
	public String getChequeTerceroCuentaSqlValidation() {
		return "IsChequesEnCartera = 'Y'";
	}
	
	public String getChequeTerceroSqlValidation() {
		return 
		       // Filtro de cuenta seleccionada por el usuario. En caso de no haber selección se presentan
		       // todos los cheques.
		       " (C_BankAccount_ID = @C_BankAccount_ID@ OR 0 =  @C_BankAccount_ID@) " +
			   // La cuenta bancaria del cheque debe ser una cuenta de cheques en cartera
			   " AND NOT EXISTS (SELECT C_BankAccount_ID " +
			                   " FROM C_BankAccount ba " +
			                   " WHERE ba.C_BankAccount_ID = C_Payment.C_BankAccount_ID " +
			                   " AND ba.IsChequesEnCartera = 'N') " +
			   // Debe ser un cheque recibido
			   " AND IsReceipt = 'Y'" +
			   // Debe ser cheque :)
			   " AND TenderType = 'K'" +
			   // Los cheques de terceros utilizables están en estado CO. Si el 
			   // estado es CL implica que el cheque de tercero ya fue utilizado como medio
			   // de pago y no se encuentra en cartera, con lo cual no se debe permitir
			   // seleccionarlo nuevamente.
			   " AND DocStatus IN ('CO')";
 	}
	
	public BigDecimal getChequeAmt(int paymentID) {
		try {
			BigDecimal amtToConvert = (BigDecimal)DB.getSQLObject(null, "SELECT PayAmt FROM C_Payment WHERE C_Payment_ID = ?", new Object[] { paymentID });
			return currencyConvert(amtToConvert, DB.getSQLValue(null, "SELECT C_Currency_ID From C_Payment where C_Payment_ID = " + paymentID), getC_Currency_ID(), new Timestamp(new java.util.Date().getTime()));
		} catch (Exception e) {
			return null;
		}
	}
	
	public class MedioPagoChequeTercero extends MedioPagoAdelantado {
		
		public String description = null;
		private MPayment chequeTerceroPayment;
		
		public MedioPagoChequeTercero(int payment_ID, BigDecimal importe, String description) {
			super(payment_ID, importe);
			this.description = description;
		}

		@Override
		public String getTipoMP() {
			return TIPOMEDIOPAGO_CHEQUETERCERO;
		}
		
		@Override
		public void setAllocationInfo(MAllocationLine allocLine) {
			allocLine.setC_Payment_ID(getChequeCP().getC_Payment_ID());
		}
		
		@Override
		public Timestamp getDateAcct() {
			return new Timestamp(System.currentTimeMillis()); // Now
		}

		public MPayment getChequeCP() {
			return (MPayment)this.payment;
		}
		
		public void setPayment(MPayment pay) {
			this.payment = pay;
		}
		
		public MPayment getChequeTerceroPayment() {
			if (chequeTerceroPayment == null)
				chequeTerceroPayment = new MPayment(getCtx(),getC_Payment_ID(),getTrxName());
			return chequeTerceroPayment;
		}

	}

	public void addChequeTercero(Integer paymentID, BigDecimal amount, String description) throws Exception {
		if (paymentID == null || paymentID == 0)
			throw new Exception("@FillMandatory@ " + getMsg("Check"));
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new Exception("@NoAmountError@");
		if (findMedioPago(MedioPagoChequeTercero.class, paymentID) != null)
			throw new Exception(Msg.getMsg(getCtx(), "POPaymentExistsError", new Object[] { getMsg("ThirdPartyCheck"), getMsg("Payment") }));

		MedioPagoChequeTercero mpct = new MedioPagoChequeTercero(paymentID, amount, description);
		addMedioPago(mpct);
	}

	
	/**
	 * Realizar tareas adicionales para la gestión de crédito de clientes
	 * @throws Exception en caso de error
	 */
	protected void performAditionalCurrentAccountWork() throws Exception{
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), getTrxName());
		// Obtengo el manager actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager();
		// Realizo las tareas adicionales necesarias
		// Payments
		for (MPayment pay : mpayments.values()) {
			performAditionalCurrentAccountWork(org, getBPartner(), manager, pay, true);
		}
		// Cashlines
		for (MCashLine cashLine : mCashLines.values()) {
			performAditionalCurrentAccountWork(org, getBPartner(), manager, cashLine, true);
		}
		// Adicionales customs de las subclases
		performAditionalCustomCurrentAccountWork(org,manager);
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
			CurrentAccountManager manager, PO po, boolean addToWorkResults) throws Exception {
		// Realizo las tareas adicionales
		CallResult result = manager.performAditionalWork(getCtx(), org, bp, po,
				true, getTrxName());
		// Si es error devuelvo una exception
		if(result.isError()){
			throw new Exception(result.getMsg());
		}
		// Lo agrego a la map si me lo permite el parámetro y tengo un resultado
		if(addToWorkResults && result != null){
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
	protected void performAditionalCustomCurrentAccountWork(MOrg org, CurrentAccountManager manager) throws Exception{
		// No hace nada aquí por ahora
	}
	
	/**
	 * Realizar tareas de gestión de crédito luego de procesar los documentos,
	 * como por ejemplo actualización del crédito de la entidad comercial, etc.
	 */
	private void afterProcessDocuments(){
		MBPartner bp = new MBPartner(getCtx(), getBPartner().getID(), getTrxName());
		MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), getTrxName());
		// Obtengo el manager actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager();
		// Actualizo el crédito
		CallResult result = manager.afterProcessDocument(getCtx(), org, bp,
				getAditionalWorkResults(), getTrxName());
		if(result.isError()){
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
	public BigDecimal getAllocationHdrTotalAmt(){
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
	protected void addCustomPaymentInfo(MPayment pay, MedioPago mp){
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
	protected void updateInvoicesModel(Vector<Integer> facturasProcesar, Vector<BigDecimal> manualAmounts, Vector<ResultItemFactura> resultsProcesar){
		// Por ahora no realiza nada aquí
	}
	
	/**
	 * Realizo acciones adicionales en el modelo 
	 */
	protected void updateAditionalInfo(){
		// Por ahora no realiza nada aquí	
	}

	/**
	 * Actualización de información del modelo luego de agregar un medio de
	 * pago.
	 * 
	 * @param mp
	 *            medio de pago agregado
	 */
	protected void updateAddingMedioPago(MedioPago mp){
		// Por ahora no se hace nada aquí 
	}

	/**
	 * Actualización de información luego de remover un medio de pago.
	 * 
	 * @param mp
	 *            medio de pago removido (ya sea para eliminar o editar)
	 */
	protected void updateRemovingMedioPago(MedioPago mp){
		// Por ahora no se hace nada aquí
	}

	/**
	 * Actualización de información luego de agregar una retención
	 * 
	 * @param processor
	 *            retención
	 */
	protected void updateAddingRetencion(RetencionProcessor processor){
		// Por ahora no se hace nada aquí
	}
	
	/**
	 * Actualización de información luego de remover una retención
	 * 
	 * @param processor
	 *            retención
	 */
	protected void updateRemovingRetencion(RetencionProcessor processor){
		// Por ahora no se hace nada aquí
	}
	
	/**
	 * Actualizo toda la info que tengo relacionada con la organización
	 * 
	 * @param orgID
	 *            id de la organización nueva seleccionada.
	 */
	public void updateOrg(int orgID){
		setOrgId(orgID);
		// Actualizar las facturas de la tabla
		if(!Util.isEmpty(C_BPartner_ID, true)){
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
	protected void makeCustomDebitsCredits(Vector<MedioPago> pays) throws Exception{
		// Por ahora no hace nada aquí, ver subclases.
	}

	/**
	 * Agrego un débito a la lista de facturas de proveedor
	 * 
	 * @param invoice
	 *            débito
	 */
	public void addDebit(MInvoice invoice){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT c_invoice_id, 0, orgname, documentno,max(duedate) as duedatemax, sum(convertedamt) as convertedamtsum, sum(openamt) as openAmtSum FROM ");
		sql.append("  (SELECT i.C_Invoice_ID, i.C_InvoicePaySchedule_ID, org.name as orgname, i.DocumentNo, coalesce(duedate,dateinvoiced) as DueDate, "); // ips.duedate
		sql.append("    abs(currencyConvert( i.GrandTotal, i.C_Currency_ID, ?, i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID)) as ConvertedAmt, ");
		sql.append("    abs(currencyConvert( invoiceOpen(i.C_Invoice_ID, COALESCE(i.C_InvoicePaySchedule_ID, 0)), i.C_Currency_ID, ?, i.DateAcct, null, i.AD_Client_ID, i.AD_Org_ID)) AS openAmt ");
		sql.append("  FROM c_invoice_v AS i ");
		sql.append("  LEFT JOIN ad_org org ON (org.ad_org_id=i.ad_org_id) ");
		sql.append("  LEFT JOIN c_invoicepayschedule AS ips ON (i.c_invoicepayschedule_id=ips.c_invoicepayschedule_id) ");
		sql.append("  INNER JOIN C_DocType AS dt ON (dt.C_DocType_ID=i.C_DocType_ID) ");
		sql.append("  WHERE c_invoice_id = ? ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), getTrxName());
			ps.setInt(1, C_Currency_ID);
			ps.setInt(2, C_Currency_ID);
			ps.setInt(3, invoice.getID());
			rs = ps.executeQuery();
			if(rs.next()){
				ResultItemFactura rif = new ResultItemFactura(rs);
				m_facturas.add(rif);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * Este método genera completamente la lista de allocation line online, o
	 * sea se crean las allocation lines que hasta ahora se deben guardar,
	 * realizando la división de pagos etc. Texto copiado de
	 * doPostProcesarNormal.
	 * 
	 * @param pays
	 *            la lista de medios de pago actual a tener en cuenta, dentro de
	 *            este método se realiza la ordenación
	 */
	public void updateOnlineAllocationLines(Vector<MedioPago> pays){
		// Eliminar todos los allocation anteriores
		onlineAllocationLines = null;
		onlineAllocationLines = new ArrayList<AllocationLine>();
		
		BigDecimal saldoMediosPago = getSaldoMediosPago();
		
		Vector<MedioPago> pagos = new Vector<MedioPago>( pays );
		Vector<ResultItemFactura> facturas = new Vector<ResultItemFactura>(ordenarFacturas());
		
		eliminarFacturasVacias(facturas);
		
		int facIdColIdx = m_facturasTableModel.getIdColIdx();
		
		int payIdx = 0;
		BigDecimal sobraDelPago = null;
		int totalFacturas = facturas.size();
		int totalPagos = pagos.size();
		ResultItemFactura f;
		for (int i = 0; i < totalFacturas && payIdx < totalPagos;i++) {
			f = facturas.get(i);
			// Recorro todas las facturas para pagarlas 
			
			int C_Invoice_ID = ((Integer)f.getItem(facIdColIdx));
			
			BigDecimal deboPagar = f.getManualAmount(); // Esto es lo que tengo que cubrir
			BigDecimal sumaPagos = (sobraDelPago != null) ? sobraDelPago : pagos.get(payIdx).getImporte(); // Inicializar lo que cubro
			ArrayList<MedioPago> subPagos = new ArrayList<MedioPago>();
			ArrayList<BigDecimal> montosSubPagos = new ArrayList<BigDecimal>();
			
			// Puede haber mas de un pago por factura: busco cuales son.
			
			subPagos.add(pagos.get(payIdx));
			montosSubPagos.add(sumaPagos);
			
			// Precondicion: Se asume que en este punto la cantidad de plata alcanza.
			
			// TODO: Redondeos de centavos ?? [(-0,01, 0.01)]
			boolean follow = true;
			while (compararMontos(deboPagar, sumaPagos) > 0 && follow) { // Mientras no haya cubrido
				payIdx++;
				follow = payIdx < totalPagos;
				if(follow){
					BigDecimal importe = pagos.get(payIdx).getImporte();
					
					subPagos.add(pagos.get(payIdx));
					montosSubPagos.add(importe);
					sumaPagos = sumaPagos.add(importe);
				}
			}

			if (compararMontos(deboPagar, sumaPagos) < 0) {
				// Si sobra ...
				int x = montosSubPagos.size() - 1;
				
				sobraDelPago = sumaPagos.subtract(deboPagar);
				
				// Si sobra plata, en el ultimo monto hay plata de más.
				montosSubPagos.set( x, montosSubPagos.get(x).subtract(sobraDelPago) );
			} else {
				payIdx++;
				sobraDelPago = null;
			}
			
			// Aca estoy seguro de que deboPagar.compareTo(sumaPagos) == 0			
			
			for (int subpayIdx = 0; subpayIdx < subPagos.size(); subpayIdx++) {
				
				MedioPago mp = subPagos.get(subpayIdx);
				BigDecimal AppliedAmt = montosSubPagos.get(subpayIdx);
				
				//
				// Si se cambian estos valores, verificar las monedas !!
				//
				
				BigDecimal DiscountAmt = Env.ZERO;
				BigDecimal WriteoffAmt = Env.ZERO;
				BigDecimal OverunderAmt = Env.ZERO;

				if (saldoMediosPago.signum() != 0) {
					WriteoffAmt = saldoMediosPago; // Redondeo de minusculos centavitos.
					saldoMediosPago = BigDecimal.ZERO;
				}
				
				AllocationLine allocLine =	new AllocationLine();
				allocLine.setAmt(AppliedAmt);
				allocLine.setDiscountAmt(DiscountAmt);
				allocLine.setWriteOffAmt(WriteoffAmt);
				allocLine.setOverUnderAmt(OverunderAmt);
				allocLine.setDebitDocumentID(C_Invoice_ID);
				allocLine.setCreditPaymentMedium(mp);
				onlineAllocationLines.add(allocLine);
			}
		}
	}

	
	/**
	 * Este método genera completamente la lista de allocation line online, o
	 * sea se crean las allocation lines que hasta ahora se deben guardar,
	 * realizando la división de pagos etc. Texto copiado de doPostProcesarNormal.
	 */
	public void updateOnlineAllocationLines(){
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
	protected boolean processOnlineAllocationLines(MAllocationHdr hdr, Vector<MedioPago> pays, boolean rebuild) throws Exception{
		// Rearmar las online allocation lines por posibles medios de pagos
		// agregados luego
		if(rebuild) updateOnlineAllocationLines(pays);			
		MAllocationLine allocLine;
		boolean ok = true;
		for (AllocationLine onAllocLine : onlineAllocationLines) {
			// Creo el allocation line desde la cabecera y los datos del
			// allocationline online
			allocLine = new MAllocationLine(hdr, onAllocLine.getAmt(),
					onAllocLine.getDiscountAmt(), onAllocLine.getWriteOffAmt(),
					onAllocLine.getOverUnderAmt());
			// Le pido al medio de pago de crédito que agregue su info a la line
			// creada
			onAllocLine.getCreditPaymentMedium().setAllocationInfo(allocLine);
			// Seteo el id del débito correspondiente
			allocLine.setC_Invoice_ID(onAllocLine.getDebitDocumentID());
			// Guardo
			if(!allocLine.save()){
				ok = false;
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		// Actualizo el total del allocation en base al total de las líneas
		// creadas 
		hdr.updateTotalByLines();
		if(!hdr.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		return ok;
	}

	/**
	 * Resetear la info del modelo para nuevas ordenes de pago
	 */
	public void reset(){
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
	}

	/**
	 * Realiza la impresión de documentos custom, generalmente las subclases
	 * usan este posibilidad
	 * 
	 * @param proceso
	 *            asíncrono
	 */
	public void printCustomDocuments(ASyncProcess asyncProcess){
		// Por ahora no hace nada aquí
	}
	
	/**
	 * 
	 * @param amount
	 * @param currencyID
	 * @param roundingMode
	 * @return
	 */
	protected BigDecimal scaleAmount(BigDecimal amount, int currencyID, int roundingMode) {
		BigDecimal newAmount = amount;
		int scale = mCurency.getStdPrecision();
		newAmount = amount.setScale(scale, roundingMode);
		return newAmount;
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
		public AllocationLine(){
			
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
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * Valida si el numero de documento especificado ya existe.  En ese caso retorna true
	 */
	protected boolean documentNoAlreadyExists()
	{
		// OP/RC Automaticas
		int count = DB.getSQLValue(null, " SELECT count(1) FROM C_AllocationHdr " +
											" WHERE documentNo = '" + Integer.parseInt(documentNo) + "'" +
											" AND AD_Client_ID = " + Env.getAD_Client_ID(m_ctx) +
											" AND allocationtype IN " + getAllocTypes());

		// Si ya existe una automatica, no consultar por las manuales y retornar true
		if (count > 0)
			return true;
		
		// OP/RC Manuales
		count += DB.getSQLValue(null, " SELECT count(1) FROM C_AllocationLine al" +
										" INNER JOIN C_AllocationHdr ah ON al.C_AllocationHdr_ID = ah.C_AllocationHdr_ID" +
										" INNER JOIN C_Invoice i ON al.C_Invoice_ID = i.C_Invoice_ID" +
										" WHERE ah.documentNo = '" + Integer.parseInt(documentNo) + "'" +
										" AND ah.AD_Client_ID = " + Env.getAD_Client_ID(m_ctx) +
										" AND ah.allocationtype = '" + X_C_AllocationHdr.ALLOCATIONTYPE_Manual + "'" +
										" AND i.issotrx = " + (isSOTrx()?"'Y'":"'N'"));
		
		return count > 0;			
	}
	
	protected String getAllocTypes()
	{
		return
			"(" +
			"'" + X_C_AllocationHdr.ALLOCATIONTYPE_PaymentOrder + "'," +
			"'" + X_C_AllocationHdr.ALLOCATIONTYPE_PaymentFromInvoice + "'," +
			"'" + X_C_AllocationHdr.ALLOCATIONTYPE_AdvancedPaymentOrder + "'" +
			")";
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
	
}


