package org.openXpertya.process.customImport;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_BankAccount;
import org.openXpertya.model.X_C_BankList;
import org.openXpertya.model.X_I_PaymentBankNews;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public abstract class NovedadesGaliciaECheckImportFile {

	/** Contexto */
	private Properties ctx;
	
	/** Transacción actual */
	private String trxName;
	
	/** Prefijo de OP */
	private String opPrefix = "";
	
	/** Sufijo de OP */
	private String opSufix = "";
	
	/** Formateo de Fecha dd/MM/yyyy */
	private DateFormat df;
	
	/** Cantidad de Registros Importados */
	protected int recordsImported = 0;
	
	/**
	 * Inicializar la info necesario para esta importación
	 */
	protected void initialize() {
		MDocType opDocType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago, getTrxName());
		if(opDocType != null && !Util.isEmpty(opDocType.getDocNoSequence_ID(), true)) {
			setOpPrefix(MSequence.getPrefix(opDocType.getDocNoSequence_ID(), getTrxName()));
			setOpSufix(MSequence.getSuffix(opDocType.getDocNoSequence_ID(), getTrxName()));
		}
		setDf(new SimpleDateFormat("dd/MM/yyyy"));
	}
	
	/**
	 * @param OPDocumentno número de documento de la OP
	 * @return id del banco relacionado a la OP desde la lista del banco
	 */
	protected int getC_Bank_ID(String OPDocumentno) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("	ba.c_bank_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("	INNER JOIN " + X_C_BankList.Table_Name + " bl ON bl.c_banklist_id = ah.c_banklist_id ");
		sql.append("	INNER JOIN " + X_C_BankAccount.Table_Name + " ba ON ba.c_bankaccount_id = bl.c_bankaccount_id ");
		sql.append("WHERE ");
		sql.append("	ah.documentno = '").append(OPDocumentno).append("'");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString(), getTrxName());
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}

		}
		return 0;
	}
	
	/**
	 * Lee la cabecera del archivo
	 * @param line línea de cabecera
	 * @throws Exception en caso de error
	 */
	public abstract void readHead(String line) throws Exception;
	
	/**
	 * Setear los campos propios de cada importador
	 */
	protected abstract void setPaymentNewsFields(X_I_PaymentBankNews pbn, String[] fields);
	
	/**
	 * Lee línea de detalle
	 * @param line línea de detalle
	 * @throws Exception en caso de error
	 */
	public void readDetail(String line) throws Exception {
		// Dividir la línea por ;
		String[] fields = line.split(";");
		// Campo 1: Tipo de Pago
		// Campo 2: Nro de Cuenta
		// Campo 3: Importe de Pago
		// Campo 4: Fecha de Emisión
		// Campo 5: Fecha de Pago
		// Campo 6: Razón Social
		// Campo 7: CUIT
		// Campo 8: OP (sólo números)
		// Campo 9: Concepto
		// Campo 10: Motivo
		// Campo 11: Email
		X_I_PaymentBankNews record = new X_I_PaymentBankNews(getCtx(), 0, getTrxName());
		record.setProcess_Date(new Timestamp(getDf().parse(fields[3]).getTime()));
		record.setPayment_Order(getOpPrefix()+fields[7]+getOpSufix());
		record.setPayment_Amount(new BigDecimal(fields[2]));
		record.setC_Bank_ID(getC_Bank_ID(record.getPayment_Order()));
		// Setear los campos propios de cada importador 
		setPaymentNewsFields(record, fields);
		if(!record.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		recordsImported++;
	}

	/**
	 * @return Mensaje final del procesamiento
	 */
	public String getEndMsg() {
		return Msg.getMsg(getCtx(), "CustomImportSuccess", new Object[] {recordsImported});
	}
	
	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	protected String getOpPrefix() {
		if(opPrefix == null) {
			return "";
		}
		return opPrefix;
	}

	protected void setOpPrefix(String opPrefix) {
		this.opPrefix = opPrefix;
	}

	protected String getOpSufix() {
		if(opSufix == null) {
			return "";
		}
		return opSufix;
	}

	protected void setOpSufix(String opSufix) {
		this.opSufix = opSufix;
	}

	protected DateFormat getDf() {
		return df;
	}

	protected void setDf(DateFormat df) {
		this.df = df;
	}
}
