package org.openXpertya.process;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MBankList;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MExpFormat;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_BankList_Config;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public abstract class ExportBankList extends ExportProcess {

	/** Contexto local */
	private Properties localCtx;
	/** Nombre de transacción local */
	private String localTrxName;
	/** Lista */
	private MBankList bankList;
	/** Fecha actual */
	protected Timestamp today = Env.getTimestamp();
	/** Formato de fechas yyyyMMdd */
	protected DateFormat dateFormat_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	/** Formato de fechas ddMMyyyy */
	protected DateFormat dateFormat_ddMMyyyy = new SimpleDateFormat("ddMMyyyy");
	/** Formato de fechas HHmmss */
	protected DateFormat dateFormat_HHmmss = new SimpleDateFormat("HHmmss");
	/** Formato de fechas MMyyyy */
	protected DateFormat dateFormat_MMyyyy = new SimpleDateFormat("MMyyyy");
	/** Tipo de Documento de la lista de bancos */
	private MDocType docType;
	/** Configuración de la lista de banco */
	private X_C_BankList_Config bankListConfig;
	/** Prefijo del tipo de documento */
	private String opPrefix = "";
	/** Prefijo del tipo de documento */
	private String opSuffix = "";
	
	public ExportBankList(Properties ctx, MBankList bankList, String trxName) {
		localCtx = ctx;
		localTrxName = trxName;
		setBankList(bankList);
		setExportFormat(getBankListExportFormat());
		setDocType(new MDocType(ctx, getBankList().getC_DocType_ID(), trxName));
		setBankListConfig((X_C_BankList_Config) PO.findFirst(ctx, X_C_BankList_Config.Table_Name,
				"ad_client_id = ? and isactive = 'Y' and c_doctype_id = ?",
				new Object[] { Env.getAD_Client_ID(ctx), bankList.getC_DocType_ID() }, null, trxName));
		MDocType opDocType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago, get_TrxName());
		// Obtener prefijo y sufijo de la secuencia de la OP
		String opPrefix = MSequence.getPrefix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opPrefix = opPrefix == null?"":opPrefix;
		String opSuffix = MSequence.getSuffix(opDocType.getDocNoSequence_ID(), get_TrxName());
		opSuffix = opSuffix == null?"":opSuffix;
		setOpPrefix(opPrefix);
		setOpSuffix(opSuffix);
	}

	protected abstract String getBankListExportFormatValue();
	
	protected abstract String getFileHeader();
	
	protected abstract String getFileFooter();
	
	protected MExpFormat getBankListExportFormat(){
		return (MExpFormat) MExpFormat.findFirst(getCtx(), MExpFormat.Table_Name,
				"value = '" + getBankListExportFormatValue() + "'", null, null, get_TrxName());
	}
	
	protected List<Object> getWhereClauseParams() {
		List<Object> params = new ArrayList<Object>();
		params.add(getBankList().getID());
		return params;
	}
	
	@Override
	protected void fillDocument() throws Exception{
		// Exportar la cabecera del archivo
		write(getFileHeader());
		// Separador de líneas
		writeRowSeparator();
		// Exportar las líneas del archivo
		super.fillDocument();
		// Exportar líneas totalizadoras
		write(getFileFooter());
	}
	
	public String export() throws Exception {
		return super.doIt();
	}
	
	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}

	@Override
	public String get_TrxName() {
		if (!Util.isEmpty(localTrxName, true)) {
			return localTrxName;
		} else {
			return super.get_TrxName();
		}
	}

	protected MBankList getBankList() {
		return bankList;
	}

	protected void setBankList(MBankList bankList) {
		this.bankList = bankList;
	}

	protected MDocType getDocType() {
		return docType;
	}

	protected void setDocType(MDocType docType) {
		this.docType = docType;
	}

	protected X_C_BankList_Config getBankListConfig() {
		return bankListConfig;
	}

	protected void setBankListConfig(X_C_BankList_Config bankListConfig) {
		this.bankListConfig = bankListConfig;
	}

	protected String getOpPrefix() {
		return opPrefix;
	}

	protected void setOpPrefix(String opPrefix) {
		this.opPrefix = opPrefix;
	}

	protected String getOpSuffix() {
		return opSuffix;
	}

	protected void setOpSuffix(String opSuffix) {
		this.opSuffix = opSuffix;
	}
}
