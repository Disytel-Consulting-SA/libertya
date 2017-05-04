package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Util;

/**
 * Data Source que carga los datos del subreporte de Informe
 * de Lotes de Pago: Detalle de pagos por Banco, a partir de una consulta SQL.
 * @author Kevin Feuerschvenger - SurSoftware
 * @date 31/08/2016
 */
public class PaymentBatchBankDetailDataSource extends QueryDataSource {

	/** ID de lote de pago del cual se quiere obtener el reporte. Opcional */
	private Integer paymentBatchpoID;

	/** Contexto */
	private Properties ctx;

	/**
	 * Constructor de la clase.
	 * @param trxName Transacción de BD a utilizar para efectuar la consulta.
	 */
	public PaymentBatchBankDetailDataSource(String trxName) {
		super(trxName);
	}

	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación.
	 * @param trxName Transacción de BD a utilizar para efectuar la consulta.
	 */
	public PaymentBatchBankDetailDataSource(Properties ctx, String trxName) {
		super(trxName);
		setCtx(ctx);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	b.c_bank_id, ");
		sql.append("	b.name AS bank_name, ");
		sql.append("	ba.c_bankaccount_id, ");
		sql.append("	ba.accountno AS account_number, ");
		sql.append("	pbd.paymentdate::date AS bank_payment_date, ");
		sql.append("	SUM(pbd.paymentamount) AS bank_total_amount ");
		sql.append("FROM ");
		sql.append("	c_paymentbatchpodetail pbd ");
		sql.append("	INNER JOIN c_bank b ON b.c_bank_id = pbd.c_bank_id ");
		sql.append("	INNER JOIN c_bankaccount ba ON ba.c_bankaccount_id = pbd.c_bankaccount_id ");

		if (!Util.isEmpty(getPaymentBatchpoID(), true)) {
			sql.append("WHERE ");
			sql.append("	pbd.c_paymentbatchpo_id = ? ");
		}

		sql.append("GROUP BY ");
		sql.append("	b.c_bank_id, ");
		sql.append("	b.name, ");
		sql.append("	ba.c_bankaccount_id, ");
		sql.append("	ba.accountno, ");
		sql.append("	pbd.paymentdate::date ");
		sql.append("ORDER BY ");
		sql.append("	b.name, ");
		sql.append("	ba.accountno, ");
		sql.append("	pbd.paymentdate::date ");

		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();

		if (!Util.isEmpty(getPaymentBatchpoID(), true)) {
			params.add(getPaymentBatchpoID());
		}

		return params.toArray();
	}

	protected boolean isQueryNoConvert(){
		return true;
	}
	
	// Getters & Setters

	public Integer getPaymentBatchpoID() {
		return paymentBatchpoID;
	}

	public void setPaymentBatchpoID(Integer paymentBatchpoID) {
		this.paymentBatchpoID = paymentBatchpoID;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
