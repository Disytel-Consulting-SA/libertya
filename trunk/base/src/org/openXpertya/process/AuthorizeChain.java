package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.model.MAuthorizationChainLink;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_M_AuthorizationChainDocument;
import org.openXpertya.model.X_M_AuthorizationChainLink;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
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
		X_M_AuthorizationChainLink authChainLink= new X_M_AuthorizationChainLink(getCtx(), getAuthorizationLink_ID(), get_TrxName());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// El usuario actual puede autorizar el eslabón seleccionado?
			String sql = "SELECT m_authorizationchainlinkuser_id "
						+ "FROM m_authorizationchainlinkuser "
						+ "WHERE m_authorizationchainlink_id = "+authChainLink.getID()
						+ " 	and ad_user_id = " + getAD_User_ID()
						+ "		and (startdate is null or startdate::date <= current_date) "
						+ "		and (enddate is null or enddate::date >= current_date)";
			int no = DB.getSQLValue(get_TrxName(), sql, true);
			if(no <= 0){
				throw new Exception(Msg.getMsg(getCtx(), "AuthorizationChainLinkInvalidUser"));
			}
			
			// Controlar que no existan eslabones anteriores obligatorios a autorizar
			sql = "select acl.* "
					+ "from m_authorizationchaindocument  acd "
					+ "inner join m_authorizationchainlink  acl on acl.m_authorizationchainlink_id = acd.m_authorizationchainlink_id "
					+ "inner join "+ tableName + " d ON (d." + tableName + "_ID = acd." + tableName + "_ID) "
					+ "where acd." + tableName + "_ID = ? and acd.status = '"+ X_M_AuthorizationChainDocument.STATUS_Pending +"' and mandatory = 'Y' and linknumber < ? "
					+ "order by linknumber";
			
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getRecord_ID());
			ps.setInt(2, authChainLink.getLinkNumber());
			rs = ps.executeQuery();
			if(rs.next()){
				MAuthorizationChainLink acl = new MAuthorizationChainLink(getCtx(), rs, get_TrxName());
				throw new Exception(Msg.getMsg(getCtx(), "AuthorizationChainLinkMustAuthPrevious",
						new Object[] { acl.getLinkNumber() + " - " + acl.getDescription() }));
			}
		
			
			
			//Si existe algun eslabón que no esté autorizado, que sea obligatorio y en que el usuario actual no este autorizado
			// no se puede autorizar el documento
			sql= "SELECT M_AuthorizationChainDocument_ID "
			+ " FROM M_AuthorizationChainDocument acd "
			+ " INNER JOIN "
			+		 "(SELECT M_AuthorizationChainLink_ID,Mandatory,MaximumAmount,authc.C_Currency_ID, authcl.created, linknumber FROM M_AuthorizationChainLink authcl"
						+ " INNER JOIN M_AuthorizationChain authc ON (authc.M_AuthorizationChain_ID = authcl.M_AuthorizationChain_ID)"
						+ " WHERE authcl.M_AuthorizationChain_ID = " + authChainLink.getM_AuthorizationChain_ID()
					+ ") acl "
			+ " ON (acl.M_AuthorizationChainLink_ID = acd.M_AuthorizationChainLink_ID) " 
			+ " INNER JOIN "+ tableName + " d"
			+ " ON (d." + tableName + "_ID = acd." + tableName + "_ID) "
			+ " WHERE acd." + tableName + "_ID = " + getRecord_ID();
			
			if (authChainLink.isValidateDocumentAmount())
				sql= sql + " AND acl.MaximumAmount <= CurrencyConvert("+ getMaximumAmount(authChainLink.getMaximumAmount()) +", d.C_Currency_ID,acl.C_Currency_ID, current_date, d.c_conversiontype_id, acd.ad_client_id, acd.ad_org_id) ";
			
			sql = sql + " AND acl.linknumber < " + authChainLink.getLinkNumber() ;
			sql = sql + " AND acd.Status= '" + X_M_AuthorizationChainDocument.STATUS_Pending + "' " 
			+ " AND acl.Mandatory= 'Y' " 
			+ " AND acl.M_AuthorizationChainLink_ID <> " + authChainLink.getID()
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
								+ "(SELECT M_AuthorizationChainLink_ID,MinimumAmount,MaximumAmount,authc.C_Currency_ID, authcl.created, linknumber FROM M_AuthorizationChainLink authcl"
									+ " INNER JOIN M_AuthorizationChain authc ON (authc.M_AuthorizationChain_ID = authcl.M_AuthorizationChain_ID)"
									+ " WHERE authcl.M_AuthorizationChain_ID = ? "
								+ ") acl "
							+ " ON acd.M_AuthorizationChainLink_ID = acl.M_AuthorizationChainLink_ID "
							+ " INNER JOIN "+ tableName + " d"
							+ " ON (d." + tableName + "_ID = acd." + tableName + "_ID) "
						    + " WHERE acd." + tableName + "_ID = ? "
						    + " AND status = '"+ X_M_AuthorizationChainDocument.STATUS_Pending +"' "
							+ " AND acl.linknumber <= " + authChainLink.getLinkNumber());
						    if (authChainLink.isValidateDocumentAmount())
						    	sqlAuthorizationChainLink_ID.append(" AND acl.MinimumAmount <= CurrencyConvert(d.GrandTotal, d.C_Currency_ID,acl.C_Currency_ID, current_date, d.c_conversiontype_id, acd.ad_client_id, acd.ad_org_id) "
						    			+ " AND acl.MinimumAmount <= ? ");
							
	
			ps = DB.prepareStatement(sqlAuthorizationChainLink_ID.toString(),
					get_TrxName());
			ps.setInt(1, authChainLink.getM_AuthorizationChain_ID());
			ps.setInt(2, getRecord_ID());
			if (authChainLink.isValidateDocumentAmount())
				ps.setBigDecimal(3, getMaximumAmount(authChainLink.getMaximumAmount()));
	
			rs = ps.executeQuery();
			
			//Autorizo los documentos
			while (rs.next()) {
				X_M_AuthorizationChainDocument authDocument = new X_M_AuthorizationChainDocument(
						getCtx(), rs.getInt("M_AuthorizationChainDocument_ID"),
						get_TrxName());
				if (authDocument.getStatus().equals(X_M_AuthorizationChainDocument.STATUS_Pending)) {
					authDocument.setStatus(X_M_AuthorizationChainDocument.STATUS_Authorized);
					authDocument.setAuthorizationDate(Env.getDate());
					authDocument.setAD_User_ID(getAD_User_ID());
					if (!authDocument.save()) {
						throw new Exception(CLogger.retrieveErrorAsString());
					}
				}
			}
							
			//Completo el documento
			M_Table table = M_Table.get(getCtx(), getTable_ID());
			PO document = table.getPO(getRecord_ID(), get_TrxName());
			
			boolean allAuthorize = DB.getSQLValue(get_TrxName(), 
					"SELECT COUNT(*) FROM M_AuthorizationChainDocument WHERE " + tableName + "_ID = " + getRecord_ID()
					+ " AND status = '"	+ X_M_AuthorizationChainDocument.STATUS_Pending	+ "'" + ((authChainLink.getAD_Org_ID() != 0)? " AND (AD_Org_ID = " + authChainLink.getAD_Org_ID() + " OR AD_Org_ID = 0) " : "" ) 
					+ " AND AD_Client_ID = " + authChainLink.getAD_Client_ID() ) <= 0;
			
			//Actualizo el viejo grand total y el estado de autorización
			document.set_Value("OldGrandTotal", document.get_Value("GrandTotal"));
			document.set_Value("AuthorizationChainStatus", allAuthorize
					? X_M_AuthorizationChainDocument.STATUS_Authorized : X_M_AuthorizationChainDocument.STATUS_Pending);
			if (!document.save())
				throw new Exception(CLogger.retrieveErrorAsString());
			
			// Completo el documento
			if (allAuthorize
					&& !DocAction.STATUS_Completed.equals(((DocAction) document).getDocStatus())
					&& !DocumentEngine.processAndSave((DocAction) document, DocAction.ACTION_Complete, false)) {
				throw new Exception(document.getProcessMsg());
			}
			
		} catch (Exception e) {
			throw e;
		} finally{
			try {
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				throw e2;
			}
		}
		
		return authChainLink.getLinkNumber() + " - " + authChainLink.getDescription() + " "
				+ Msg.getMsg(getCtx(), "Authorized");
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
