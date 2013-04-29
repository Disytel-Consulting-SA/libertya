package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MDocType;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class ResumenVentasDocTypeBaseKeyDataSource extends
		ResumenVentasDocTypeDataSource {

	/** Mensaje por clave de tipo de documento base */
	private static Map<String, String> lineDescriptionByDocTypeBaseKey;
	
	static{
		lineDescriptionByDocTypeBaseKey = new HashMap<String, String>();
		lineDescriptionByDocTypeBaseKey.put(MDocType.DOCTYPE_CustomerInvoice,
				Msg.getMsg(Env.getCtx(), "TotalByInvoicing"));
		lineDescriptionByDocTypeBaseKey.put(MDocType.DOCTYPE_CustomerCreditNote,
				Msg.getMsg(Env.getCtx(), "TotalByCreditNotes"));
		lineDescriptionByDocTypeBaseKey.put(MDocType.DOCTYPE_CustomerDebitNote,
				Msg.getMsg(Env.getCtx(), "TotalByDebitNotes"));
	}
	
	/** Importes por clave de tipo de documento base */
	private Map<String, BigDecimal> amtsByDocTypeBaseKey = new HashMap<String, BigDecimal>();
	
	/** Conjunto de claves de tipo de documento base */
	private Iterator<String> docTypeBaseKeys;
	
	/** Clave de Tipo de documento base actual */
	private String actualDocTypeBaseKey;
	
	public ResumenVentasDocTypeBaseKeyDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		setAmtsByDocTypeBaseKey(new HashMap<String, BigDecimal>());
	}
	
	@Override
	protected String getGroupFields() {
		return "trxtype, c_pospaymentmedium_id, pospaymentmediumname";
	}

	public void loadData() throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			// Se crea la consulta y se asignan los parámetros.
			pstmt = DB.prepareStatement(getQuery(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
					getTrxName(), isQueryNoConvert());
			int i = 1;
			for (Object parameterValue : getParameters()) {
				pstmt.setObject(i++, parameterValue);				
			}
			
			rs = pstmt.executeQuery();
			MDocType docType;
			String docTypeKey;
			while(rs.next()){
				docType = MDocType.get(getCtx(), rs.getInt("c_pospaymentmedium_id"));
				docTypeKey = docType.getBaseKey();
				if(getAmtsByDocTypeBaseKey().get(docTypeKey) == null){
					getAmtsByDocTypeBaseKey().put(docTypeKey, BigDecimal.ZERO);
				}
				getAmtsByDocTypeBaseKey().put(
						docTypeKey,
						getAmtsByDocTypeBaseKey().get(docTypeKey).add(
								rs.getBigDecimal("amount")));
			}
		} catch(Exception e){
			throw e;
		} finally{
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (SQLException e) {}
		}
		setDocTypeKeys(getAmtsByDocTypeBaseKey().keySet().iterator());
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		if(field.getName().toUpperCase().equals("LINE_DESCRIPTION")){
			value = lineDescriptionByDocTypeBaseKey
					.get(getActualDocTypeBaseKey()) == null ? "Otros"
					: lineDescriptionByDocTypeBaseKey
							.get(getActualDocTypeBaseKey());
		}
		else if(field.getName().toUpperCase().equals("AMOUNT")){
			value = getAmtsByDocTypeBaseKey().get(getActualDocTypeBaseKey());
		}
		return value;
	}

	public boolean next() throws JRException {
		if(!getDocTypeBaseKeys().hasNext()){
			return false;
		}
		setActualDocTypeBaseKey(getDocTypeBaseKeys().next());
		return true;
	}
	
	protected Map<String, BigDecimal> getAmtsByDocTypeBaseKey() {
		return amtsByDocTypeBaseKey;
	}

	protected void setAmtsByDocTypeBaseKey(Map<String, BigDecimal> amtsByDocTypeBaseKey) {
		this.amtsByDocTypeBaseKey = amtsByDocTypeBaseKey;
	}

	protected Iterator<String> getDocTypeBaseKeys() {
		return docTypeBaseKeys;
	}

	protected void setDocTypeKeys(Iterator<String> docTypeBaseKeys) {
		this.docTypeBaseKeys = docTypeBaseKeys;
	}

	protected String getActualDocTypeBaseKey() {
		return actualDocTypeBaseKey;
	}

	protected void setActualDocTypeBaseKey(String actualDocTypeBaseKey) {
		this.actualDocTypeBaseKey = actualDocTypeBaseKey;
	}	

}
