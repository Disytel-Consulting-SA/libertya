package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import net.sf.jasperreports.engine.JREmptyDataSource;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OrdenPagoDataSource;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MRegion;
import org.openXpertya.model.MRetencionSchema;
import org.openXpertya.model.MRetencionType;
import org.openXpertya.model.X_M_Retencion_Invoice;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class LaunchOrdenPago extends SvrProcess {

	/** Contexto de la aplicación */
	private Properties ctx = Env.getCtx();
	/** ID de la cabecera de la imputación de la Orden de Pago */
	private int p_C_AllocationHdr_ID = 0;
	/** ID del informe jasper */
	private int m_AD_JasperReport_ID = 0;

	/** Instancia de la Orden de Pago: Allocation Header que representa la OP */
	private MAllocationHdr mAllocationHdr;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			Object pp = para[i].getParameter();
			String name = para[i].getParameterName();

			if (pp != null) {

				if (name.equalsIgnoreCase("C_AllocationHdr_ID")) {
					p_C_AllocationHdr_ID = para[i].getParameterAsInt();
				} else {
					log.log(Level.SEVERE, "prepare - Unknown Parameter: "
							+ name);
				}

			}
		}

		if (p_C_AllocationHdr_ID == 0)
			p_C_AllocationHdr_ID = getRecord_ID();

		ProcessInfo base_pi = getProcessInfo();
		int AD_Process_ID = base_pi.getAD_Process_ID();
		MProcess proceso = MProcess.get(Env.getCtx(), AD_Process_ID);
		if (proceso.isJasperReport() != true)
			return;
		m_AD_JasperReport_ID = proceso.getAD_JasperReport_ID();
	}

	@Override
	protected String doIt() throws Exception {
		MJasperReport jasperWrapper = new MJasperReport(getCtx(),
				m_AD_JasperReport_ID, get_TrxName());
		OrdenPagoDataSource opDataSource = getDataSource();

		// /////////////////////////////////////
		// Subreporte de Comprobantes pagados.
		MJasperReport documentsSubreport = getDocumentsSubreport();
		// Se agrega el informe compilado como parámetro.
		jasperWrapper.addParameter("COMPILED_SUBREPORT_DOCS",
				new ByteArrayInputStream(documentsSubreport.getBinaryData()));
		// Se agrega el datasource del subreporte.
		jasperWrapper.addParameter("SUBREPORT_DOCS_DATASOURCE",
				opDataSource.getDocumentsDataSource());

		// /////////////////////////////////////
		// Subreporte de cheques.
		MJasperReport checksSubreport = getChecksSubreport();
		// Se agrega el informe compilado como parámetro.
		jasperWrapper.addParameter("COMPILED_SUBREPORT_CHECKS",
				new ByteArrayInputStream(checksSubreport.getBinaryData()));
		// Se agrega el datasource del subreporte.
		jasperWrapper.addParameter("SUBREPORT_CHECKS_DATASOURCE",
				opDataSource.getChecksDataSource());

		// /////////////////////////////////////
		// Subreporte de Otros Medios de Pago.
		MJasperReport otherPayments = getOtherPaymentsSubreport();
		// Se agrega el informe compilado como parámetro.
		jasperWrapper.addParameter("COMPILED_SUBREPORT_OTHER_PAYMENTS",
				new ByteArrayInputStream(otherPayments.getBinaryData()));
		// Se agrega el datasource del subreporte.
		jasperWrapper.addParameter("SUBREPORT_OTHER_PAYMENTS_DATASOURCE",
				opDataSource.getOtherPaymentsDataSource());

		// /////////////////////////////////////
		// Subreporte de Comprobantes de retenciones.
		MJasperReport comprobanteRetencion = getComprobanteRetencion();
		// Se agrega el informe compilado como parámetro.
		jasperWrapper.addParameter("COMPILED_SUBREPORT_COMPROBANTE_RETENCION",
				new ByteArrayInputStream(comprobanteRetencion.getBinaryData()));

		// Se agrega el/los datasource del subreporte.
		String sql = "select c_invoice_id from m_retencion_invoice r where r.c_allocationhdr_id="
				+ +getAllocationHdrID() + "group by c_invoice_id";
		PreparedStatement pstmt = DB.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		int i = 1;
		while (rs.next()) {
			// Se agrega el datasource del subreporte.
			jasperWrapper.addParameter(
					"SUBREPORT_COMPROBANTE_RETENCION_DATASOURCE_" + i,
					opDataSource.getComprobanteRetenciones(rs
							.getInt("c_invoice_id")));
			i++;
		}
		// Se cargan los parámetros del reporte.
		loadReportParameters(jasperWrapper);

		try {
			jasperWrapper.fillReport(new JREmptyDataSource());
			jasperWrapper.showReport(getProcessInfo());
		} catch (RuntimeException e) {
			throw new Exception("No se han podido rellenar el informe.", e);
		}
		return "doIt";
	}

	private MJasperReport getComprobanteRetencion() throws Exception {
		// TODO Auto-generated method stub
		return getJasperReport("Comprobante de Retención");
	}

	/**
	 * @return Retorna el MJasperReport del subreporte de comprobantes.
	 */
	protected MJasperReport getDocumentsSubreport() throws Exception {
		return getJasperReport("OrdenPago-Cbantes");
	}

	/**
	 * @return Retorna el MJasperReport del subreporte de cheques.
	 */
	protected MJasperReport getChecksSubreport() throws Exception {
		return getJasperReport("OrdenPago-Cheques");
	}

	/**
	 * @return Retorna el MJasperReport del subreporte de otros pagos.
	 */
	protected MJasperReport getOtherPaymentsSubreport() throws Exception {
		return getJasperReport("OrdenPago-OtrosMedios");
	}

	/**
	 * @return Retorna el MJasperReport con el nombre indicado.
	 */
	private MJasperReport getJasperReport(String name) throws Exception {
		Integer jasperReport_ID = (Integer) DB
				.getSQLObject(
						get_TrxName(),
						"SELECT AD_JasperReport_ID FROM AD_JasperReport WHERE Name ilike ?",
						new Object[] { name });
		if (jasperReport_ID == null || jasperReport_ID == 0)
			throw new Exception("Jasper Report not found - " + name);

		MJasperReport jasperReport = new MJasperReport(ctx, jasperReport_ID,
				get_TrxName());
		return jasperReport;
	}

	/**
	 * @return FactoryMethod: Retorna el data source del reporte.
	 */
	protected OrdenPagoDataSource getDataSource() {
		return new OrdenPagoDataSource(p_C_AllocationHdr_ID);
	}

	/**
	 * Carga los parámetros del reporte.
	 * 
	 * @param jasperWrapper
	 *            Reporte Jasper
	 */
	protected void loadReportParameters(MJasperReport jasperWrapper) {
		// Se obtienen los parámetros necesarios del reporte.
		MAllocationHdr op = getAllocationHdr();
		String clientName = DB.getSQLValueString(get_TrxName(),
				"SELECT Name FROM AD_Client WHERE Ad_Client_ID = ?",
				op.getAD_Client_ID());
		String orgName = DB.getSQLValueString(get_TrxName(),
				"SELECT Name FROM AD_Org WHERE AD_Org_ID = ?",
				op.getAD_Org_ID());
		String cityName = DB
				.getSQLValueString(
						get_TrxName(),
						"SELECT city FROM ad_orginfo oi join c_location l on oi.c_location_id = l.c_location_id WHERE oi.ad_org_id = ?",
						op.getAD_Org_ID());
		Timestamp opDate = op.getDateTrx();
		String opNumber = op.getDocumentNo();
		String bPartner = DB.getSQLValueString(get_TrxName(),
				"SELECT Name FROM C_BPartner WHERE C_BPartner_ID = ?",
				op.getC_BPartner_ID());
		// Monto de notas de crédito
		BigDecimal ncAmount = getCreditsAmount(op);
		// El monto total debe incluir de manera discriminada las notas de
		// crédito
		BigDecimal opAmount = op.getGrandTotal().subtract(ncAmount);
		BigDecimal retencionesAmount = op.getRetencion_Amt();

		// Se asignan los parámetros al wrapper.
		jasperWrapper.addParameter("CLIENT_NAME", clientName);
		jasperWrapper.addParameter("ORG_NAME", orgName);
		jasperWrapper.addParameter("CITY_NAME", cityName);
		jasperWrapper.addParameter("OP_DATE", opDate);
		jasperWrapper.addParameter("OP_NUMBER", opNumber);
		jasperWrapper.addParameter("BPARTNER", bPartner);
		jasperWrapper.addParameter("OP_AMOUNT", opAmount);
		jasperWrapper.addParameter("CREDITO_AMOUNT", ncAmount);
		jasperWrapper.addParameter("RETENCIONES_AMOUNT", retencionesAmount);

		String sql = "select c_invoice_id from m_retencion_invoice r where r.c_allocationhdr_id="
				+ +getAllocationHdrID() + "group by c_invoice_id";

		PreparedStatement pstmt = DB.prepareStatement(sql);
		try {
			ResultSet rs = pstmt.executeQuery();
			int j = 0;
			BigDecimal netOP = getPayNetAmt(getAllocationHdrID());
			while (rs.next()) {

				MInvoice invoice = new MInvoice(Env.getCtx(),
						rs.getInt("c_invoice_id"), null);
				MClient client = JasperReportsUtil.getClient(getCtx(),
						invoice.getAD_Client_ID());
				MClientInfo clientInfo = JasperReportsUtil.getClientInfo(
						getCtx(), invoice.getAD_Client_ID(), get_TrxName());
				MBPartner bpartner = new MBPartner(getCtx(),
						invoice.getC_BPartner_ID(), null);
				MBPartnerLocation BPLocation = new MBPartnerLocation(getCtx(),
						invoice.getC_BPartner_Location_ID(), null);
				MLocation location = new MLocation(getCtx(),
						BPLocation.getC_Location_ID(), null);

				jasperWrapper.addParameter("FECHA_" + j,
						invoice.getDateInvoiced());
				jasperWrapper.addParameter("RAZONSOCIAL", JasperReportsUtil
						.coalesce(invoice.getNombreCli(), bpartner.getName()));
				jasperWrapper.addParameter("DIRECCION_" + j, JasperReportsUtil
						.coalesce(invoice.getInvoice_Adress(),
								JasperReportsUtil.formatLocation(getCtx(),
										location.getID(), false)));
				jasperWrapper.addParameter("CUIT_" + j,
						JasperReportsUtil.coalesce(bpartner.getTaxID(), ""));
				if (!Util.isEmpty(invoice.getC_Order_ID(), true)) {
					jasperWrapper.addParameter("NRO_OC_" + j, JasperReportsUtil
							.coalesce(
									(new MOrder(getCtx(), invoice
											.getC_Order_ID(), get_TrxName()))
											.getDocumentNo(), ""));
				}

				// Direccción de la Organización asociada a la Factura
				MOrg org = MOrg.get(getCtx(), invoice.getAD_Org_ID());
				MLocation locationOrg = new MLocation(getCtx(), org.getInfo()
						.getC_Location_ID(), null);
				MRegion regionOrg = null;
				if (locationOrg.getC_Region_ID() > 0)
					regionOrg = new MRegion(getCtx(),
							locationOrg.getC_Region_ID(), null);
				jasperWrapper.addParameter(
						"DIRECCION_ORG_" + j,
						JasperReportsUtil.coalesce(locationOrg.getAddress1(),
								"")
								+ ". "
								+ JasperReportsUtil.coalesce(
										locationOrg.getCity(), "")
								+ ". ("
								+ JasperReportsUtil.coalesce(
										locationOrg.getPostal(), "")
								+ "). "
								+ JasperReportsUtil.coalesce(
										regionOrg == null ? "" : regionOrg
												.getName(), ""));

				jasperWrapper.addParameter("CLIENT_" + j, client.getName());
				jasperWrapper.addParameter("CLIENT_CUIT_" + j,
						client.getCUIT(invoice.getAD_Org_ID()));
				// Parámetros de la retención aplicada a la Factura
				X_M_Retencion_Invoice retencion_invoice = getM_Retencion_Invoice(invoice);
				if (retencion_invoice != null) {
					MRetencionSchema retencionSchema = new MRetencionSchema(
							getCtx(),
							retencion_invoice.getC_RetencionSchema_ID(), null);
					// Nombre del Esquema de Retención
					jasperWrapper.addParameter("RET_SCHEMA_NAME_" + j,
							(retencionSchema.getName()));
					// Nombre del Tipo de Retención
					jasperWrapper.addParameter(
							"RET_RETENTION_TYPE_NAME_" + j,
							(new MRetencionType(getCtx(), retencionSchema
									.getC_RetencionType_ID(), null).getName()));
					// Monto de la Retención
					jasperWrapper.addParameter("RET_AMOUNT_" + j,netOP);
							//invoice.getNetAmount());
					// retencion_invoice.getamt_retenc());

					MAllocationHdr allocation = new MAllocationHdr(getCtx(),
							retencion_invoice.getC_AllocationHdr_ID(), null);
					// Monto del Recibo
					jasperWrapper.addParameter("RET_ALLOC_AMOUNT_" + j,
							invoice.getNetAmount());
							//allocation.getGrandTotal());
					// Comprobante/s que origina/n la retención. (Números de
					// Documento de las facturas en el Recibo)
					jasperWrapper.addParameter("RET_ALLOC_INVOICES_" + j,
							get_Retencion_Invoices(allocation));
					j++;
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(
					"No se puede ejecutar la consulta para crear las lineas del informe.");
		}
	}

	private BigDecimal getPayNetAmt(int c_AllocationHdr_ID) throws SQLException {
		String sqlAllocationLine = " SELECT c_allocationline_id, al.c_invoice_id "
				+ " FROM c_allocationline al "
				+ " INNER JOIN c_invoice i ON (i.c_invoice_id = al.c_invoice_id) "
				+ " WHERE al.c_allocationhdr_id= " + +c_AllocationHdr_ID;

		PreparedStatement pstmtAllocationLine = DB
				.prepareStatement(sqlAllocationLine);
		ResultSet rsAllocationLine = pstmtAllocationLine.executeQuery();
		BigDecimal netTotal = Env.ZERO;
		BigDecimal totalLines, grandTotal;
		while (rsAllocationLine.next()) {
			MAllocationLine allocationline = new MAllocationLine(getCtx(),
					rsAllocationLine.getInt("c_allocationline_id"),
					get_TrxName());
			MInvoice invoiceOrig = new MInvoice(getCtx(),
					rsAllocationLine.getInt("c_invoice_id"), get_TrxName());
			totalLines = invoiceOrig.getTotalLinesNet();
			grandTotal = invoiceOrig.getGrandTotal();
			netTotal = netTotal.add(totalLines.multiply(
					allocationline.getAmount()).divide(grandTotal, 2,
					BigDecimal.ROUND_HALF_EVEN));
		}
		return netTotal;
	}

	protected int getAllocationHdrID() {
		return p_C_AllocationHdr_ID;
	}

	/**
	 * @return the AllocationHdr
	 */
	protected MAllocationHdr getAllocationHdr() {
		if (mAllocationHdr == null)
			mAllocationHdr = new MAllocationHdr(ctx, p_C_AllocationHdr_ID,
					get_TrxName());
		return mAllocationHdr;
	}

	/*
	 * El método retorna una Retencion_Invoice a partir del la factura
	 */
	private X_M_Retencion_Invoice getM_Retencion_Invoice(MInvoice invoice) {
		try {
			int m_Retencion_InvoiceID = 0;
			PreparedStatement stmt = DB
					.prepareStatement("SELECT m_retencion_invoice_id FROM M_Retencion_Invoice WHERE c_invoice_id = ? ORDER BY m_retencion_invoice_id DESC");
			stmt.setInt(1, invoice.getC_Invoice_ID());
			ResultSet rs = stmt.executeQuery();
			if (!rs.next() || rs.getInt(1) == 0)
				return null;

			m_Retencion_InvoiceID = rs.getInt(1);
			X_M_Retencion_Invoice m_Retencion_Invoice = new X_M_Retencion_Invoice(
					getCtx(), m_Retencion_InvoiceID, null);
			return m_Retencion_Invoice;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * El método retorna todos los DocumentNo. de las facturas asociadas al
	 * recibo recibido por parámetro.
	 */
	private String get_Retencion_Invoices(MAllocationHdr allocation) {
		try {
			PreparedStatement stmt = DB
					.prepareStatement("SELECT DISTINCT factura FROM C_Allocation_Detail_V WHERE C_AllocationHdr_ID = ? ORDER BY factura DESC");
			stmt.setInt(1, allocation.getC_AllocationHdr_ID());
			ResultSet rs = stmt.executeQuery();

			String invoices = "- ";
			while (rs.next()) {
				invoices = invoices.concat(rs.getString(1).concat(" - "));
			}

			return invoices;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retorna el monto total involucrado en Notas de Credito para este
	 * Allocation.
	 */
	protected BigDecimal getCreditsAmount(MAllocationHdr allocation) {
		// En OP por el momento no se discrimina el total del Allocation y las
		// notas de crédito
		return BigDecimal.ZERO;
	}
}
