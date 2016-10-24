package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperEmptyDataSource;
import org.openXpertya.JasperReport.DataSource.PaymentBatchBankDetailDataSource;
import org.openXpertya.JasperReport.DataSource.PaymentBatchDataSource;
import org.openXpertya.JasperReport.DataSource.QueryDataSource;
import org.openXpertya.model.MPaymentBatchPO;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.Util;

public class LaunchPaymentBatchPO extends JasperReportLaunch {
	private static final String COMPILED_SUBREPORT_BANK_DETAIL_PARAM_NAME = "COMPILED_SUBREPORT_BANK_DETAIL";
	private static final String SUBREPORT_BANK_DETAIL_DATASOURCE_PARAM_NAME = "SUBREPORT_BANK_DETAIL_DATASOURCE";

	private static final String SUBREPORT_BANK_DETAIL_JASPER_REPORT_NAME = "LoteDePago-Subreport-PagosPorBanco";

	private MPaymentBatchPO paymentBatchPO;
	
	/** @return título del reporte */
	protected String getTitle() {
		return "Informe de Lotes de Pago";
	}

	@Override
	protected void loadReportParameters() throws Exception {
		initialize();
		// Agrego los subreportes
		addSubreports();
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		try {
			return getPaymentBatchDataSource();
		} catch (Exception e) {
			e.printStackTrace();
			return new OXPJasperEmptyDataSource();
		}
	}

	protected void initialize() {
		if (!Util.isEmpty(getTable_ID(), true) && !Util.isEmpty(getRecord_ID(), true)) {
			M_Table table = M_Table.get(getCtx(), getTable_ID());
			setPaymentBatchPO((MPaymentBatchPO) table.getPO(getRecord_ID(), get_TrxName()));
		}
	}

	/**
	 * Agrega los subreportes al reporte
	 * @throws Exception
	 */
	protected void addSubreports() throws Exception {
		// Data Source de Ventas
		PaymentBatchBankDetailDataSource paymentBatchDS = getPaymentBatchBankDetailDataSource();
		// Subreporte de detalle de pagos por banco
		MJasperReport trxSubreport = getBankDetailSubreport();
		// Se agrega el informe compilado como parámetro.
		addReportParameter(COMPILED_SUBREPORT_BANK_DETAIL_PARAM_NAME, new ByteArrayInputStream(trxSubreport.getBinaryData()));
		// Se agregan los datasources de los subreportes
		addReportParameter(SUBREPORT_BANK_DETAIL_DATASOURCE_PARAM_NAME, paymentBatchDS);
	}

	protected PaymentBatchDataSource getPaymentBatchDataSource() throws Exception {
		PaymentBatchDataSource paymentBatchDS = null;
		if (isLoadPaymentBatchDataSource()) {
			paymentBatchDS = new PaymentBatchDataSource(getCtx(), get_TrxName());
			if (paymentBatchPO != null) {
				paymentBatchDS.setPaymentBatchpoID(paymentBatchPO.getC_PaymentBatchPO_ID());
			}
		}
		return paymentBatchDS;
	}

	protected PaymentBatchBankDetailDataSource getPaymentBatchBankDetailDataSource() throws Exception {
		PaymentBatchBankDetailDataSource paymentBatchBankDetailDS = null;
		if (isLoadPaymentBatchBankDetailDataSource()) {
			paymentBatchBankDetailDS = new PaymentBatchBankDetailDataSource(getCtx(), get_TrxName());
			if (paymentBatchPO != null) {
				paymentBatchBankDetailDS.setPaymentBatchpoID(paymentBatchPO.getC_PaymentBatchPO_ID());
			}
			// Carga los datos para el subreporte.
			paymentBatchBankDetailDS = (PaymentBatchBankDetailDataSource) loadDSData(paymentBatchBankDetailDS);
		}
		return paymentBatchBankDetailDS;
	}

	protected QueryDataSource loadDSData(QueryDataSource ds) throws Exception {
		ds.loadData();
		return ds;
	}

	// Métodos flags para la carga de data sources

	protected boolean isLoadPaymentBatchDataSource() {
		return true;
	}

	protected boolean isLoadPaymentBatchBankDetailDataSource() {
		return true;
	}

	// Getters & Setters

	/** @return el subreporte de detalle de pagos por banco */
	protected MJasperReport getBankDetailSubreport() throws Exception {
		return getJasperReport(getCtx(), SUBREPORT_BANK_DETAIL_JASPER_REPORT_NAME, get_TrxName());
	}

	public MPaymentBatchPO getPaymentBatchPO() {
		return paymentBatchPO;
	}

	public void setPaymentBatchPO(MPaymentBatchPO paymentBatchPO) {
		this.paymentBatchPO = paymentBatchPO;
	}

}
