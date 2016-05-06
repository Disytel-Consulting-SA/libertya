package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_M_AuthorizationChainDocument;
import org.openXpertya.model.X_M_AuthorizationChainLink;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class AuthorizeChain extends SvrProcess {
	
	/**							*/
	private Integer link_ID;
	
	/** Nombre de Tabla     	*/
	private String tableName;
	
	private BigDecimal maximumAmount= new BigDecimal(99999999);

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("M_AuthorizationChainLink_ID")) {
				setAuthorizationLink_ID(((BigDecimal) para[i].getParameter()).intValue());
			}
		}
		tableName= M_Table.getTableName(getCtx(), getTable_ID());
	}

	@Override
	protected String doIt() throws Exception {
		
		if (Util.isEmpty(getAuthorizationLink_ID(), true))
			return null;
		
		//Obtengo todas los eslabones del documento porque podría autorizar eslabónes con montos menores si no son obligatorios
		X_M_AuthorizationChainLink authChainLink= new X_M_AuthorizationChainLink(getCtx(), getAuthorizationLink_ID(), get_TrxName());

		//Si existe algun eslabón que no esté autorizado, que sea obligatorio y en que el usuario actual no este autorizado
		// no se puede autorizar el documento
		String sql= "SELECT M_AuthorizationChainDocument_ID "
		+ " FROM M_AuthorizationChainDocument acd "
		+ " INNER JOIN "
		+		 "(SELECT M_AuthorizationChainLink_ID,Mandatory,MaximumAmount,authc.C_Currency_ID FROM M_AuthorizationChainLink authcl"
					+ " INNER JOIN M_AuthorizationChain authc ON (authc.M_AuthorizationChain_ID = authcl.M_AuthorizationChain_ID)"
					+ " WHERE authcl.M_AuthorizationChain_ID = " + authChainLink.getM_AuthorizationChain_ID()
				+ ") acl "
		+ " ON (acl.M_AuthorizationChainLink_ID = acd.M_AuthorizationChainLink_ID) " 
		+ " INNER JOIN "+ tableName + " d"
		+ " ON (d." + tableName + "_ID = acd." + tableName + "_ID) "
		+ " WHERE acd." + tableName + "_ID = " + getRecord_ID()
		+ " AND acl.MaximumAmount <= CurrencyConvert("+ getMaximumAmount(authChainLink.getMaximumAmount()) +", d.C_Currency_ID,acl.C_Currency_ID, current_date, d.c_conversiontype_id, acd.ad_client_id, acd.ad_org_id) "
		+ " AND acd.Status= '" + X_M_AuthorizationChainDocument.STATUS_Pending + "' " 
		+ " AND acl.Mandatory= 'Y' " 
		+ " AND NOT EXISTS (SELECT aclu.M_AuthorizationChainLink_ID " 
				+ " FROM m_authorizationchainlinkuser aclu " 
				+ " WHERE aclu.M_AuthorizationChainLink_ID = acl.M_AuthorizationChainLink_ID " 
				+ " AND aclu.AD_User_ID = " + getAD_User_ID()  
				+ " AND (case when aclu.StartDate is null then true else current_date >= aclu.StartDate end) "
				+ " AND (case when aclu.EndDate is null then true else current_date <= aclu.EndDate end)) ";
		if (DB.getSQLValue(get_TrxName(), sql)>0)
			throw new Exception(Msg.getMsg(getCtx(), "Can'tAuthorize"));
		
		//Obtengo todos los eslabones del documento que estén relacionados con los eslabones de la cadena 
		// donde el monto máximo del eslabón sea menor o igual al valor del documento y que no sean obligatorios
		StringBuffer sqlAuthorizationChainLink_ID = new StringBuffer(
						  "SELECT acd.M_AuthorizationChainDocument_ID FROM M_AuthorizationChainDocument acd "
						+ " INNER JOIN "
							+ "(SELECT M_AuthorizationChainLink_ID,MinimumAmount,MaximumAmount,authc.C_Currency_ID FROM M_AuthorizationChainLink authcl"
								+ " INNER JOIN M_AuthorizationChain authc ON (authc.M_AuthorizationChain_ID = authcl.M_AuthorizationChain_ID)"
								+ " WHERE authcl.M_AuthorizationChain_ID = ? "
							+ ") acl "
						+ " ON acd.M_AuthorizationChainLink_ID = acl.M_AuthorizationChainLink_ID "
						+ " INNER JOIN "+ tableName + " d"
						+ " ON (d." + tableName + "_ID = acd." + tableName + "_ID) "
					    + " WHERE acd." + tableName + "_ID = ? "
					    + " AND acl.MinimumAmount <= CurrencyConvert(d.GrandTotal, d.C_Currency_ID,acl.C_Currency_ID, current_date, d.c_conversiontype_id, acd.ad_client_id, acd.ad_org_id) "
					    + " AND acl.MinimumAmount <= ? "
						);

		PreparedStatement pstmt = null;

		pstmt = DB.prepareStatement(sqlAuthorizationChainLink_ID.toString(),
				get_TrxName());
		pstmt.setInt(1, authChainLink.getM_AuthorizationChain_ID());
		pstmt.setInt(2, getRecord_ID());
		pstmt.setBigDecimal(3, getMaximumAmount(authChainLink.getMaximumAmount()));

		ResultSet rs = pstmt.executeQuery();
		
		//Autorizo los documentos
		while (rs.next()) {
			X_M_AuthorizationChainDocument authDocument = new X_M_AuthorizationChainDocument(
					getCtx(), rs.getInt("M_AuthorizationChainDocument_ID"),
					get_TrxName());
			if (authDocument.getStatus().equals(X_M_AuthorizationChainDocument.STATUS_Pending)) {
				authDocument.setStatus(X_M_AuthorizationChainDocument.STATUS_Authorized);
				authDocument.setAuthorizationDate(new Timestamp((new Date())
						.getTime()));
				authDocument.setAD_User_ID(getAD_User_ID());
				if (!authDocument.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		}
						
		//Completo el documento
		M_Table table = M_Table.get(getCtx(), getTable_ID());
		PO document = table.getPO(getRecord_ID(), get_TrxName());
		if (!DocumentEngine.processAndSave((DocAction) document, DocAction.ACTION_Complete, false))
			throw new Exception(document.getProcessMsg());
		
		//Actualizo el viejo grand total
		document.set_Value("OldGrandTotal", document.get_Value("GrandTotal"));
		if (!document.save())
			throw new Exception(CLogger.retrieveErrorAsString());
		
		return null;
	}

	private BigDecimal getMaximumAmount(BigDecimal maxAmount) {
		if (Util.isEmpty(maxAmount, true)){
			return maximumAmount;
		}
		return maxAmount;
	}

	public void setAuthorizationLink_ID(Integer link_ID) {
		this.link_ID = link_ID;
	}

	public Integer getAuthorizationLink_ID() {
		return this.link_ID;
	}
	
}
