package org.openXpertya.process;

import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MDocType;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.Msg;

public class BankListConfigFieldsException extends Exception {

	/** Mensaje cabecera del error */
	private static final String HEADER_AD_MESSAGE = "BankListConfigRequiredFields";
	
	/** Contexto */
	private Properties ctx;
	
	/** Campos faltantes */
	private List<String> fields;
	
	/** Tipo de Documento */
	private Integer bankListDocTypeID;
	
	public BankListConfigFieldsException() {
		// TODO Auto-generated constructor stub
	}

	public BankListConfigFieldsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public BankListConfigFieldsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public BankListConfigFieldsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	
	public BankListConfigFieldsException(Properties ctx, Integer bankListDocTypeID, List<String> fields) {
		setCtx(ctx);
		setBankListDocTypeID(bankListDocTypeID);
		setFields(fields);
	}
	
	public void addField(String field){
		getFields().add(field);
	}

	@Override
	public String getMessage() {
		HTMLMsg msg = new HTMLMsg();
		HTMLList fieldList = msg.createList("fields", "ul");
		msg.setHeaderMsg(getADMessage(HEADER_AD_MESSAGE,
				new Object[] { MDocType.get(getCtx(), getBankListDocTypeID()).getName() }));
		for (String field : getFields()) {
			msg.createAndAddListElement(field, getADElement(field), fieldList);
		}
		msg.addList(fieldList);
		return msg.toString();
	}
	
	protected String getADMessage(String msg){
		return Msg.getMsg(getCtx(), msg);
	}
	
	private String getADMessage(String msg, Object[] params){
		return Msg.getMsg(getCtx(), msg, params);
	}

	private String getADElement(String field){
		return Msg.getElement(getCtx(), field);
	}
	
	public List<String> getFields() {
		return fields;
	}

	private void setFields(List<String> fields) {
		this.fields = fields;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Integer getBankListDocTypeID() {
		return bankListDocTypeID;
	}

	public void setBankListDocTypeID(Integer bankListDocTypeID) {
		this.bankListDocTypeID = bankListDocTypeID;
	}
}
