package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MAuthorizationChainLink extends X_M_AuthorizationChainLink {

	public MAuthorizationChainLink(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAuthorizationChainLink(Properties ctx,
			int M_AuthorizationChainLink_ID, String trxName) {
		super(ctx, M_AuthorizationChainLink_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		//Si está activo el campo "Validar Montos de Documento", controlar que no existan rangos que se pisen con lo este eslabón
		if (isValidateDocumentAmount() && (DB.getSQLValue(
				this.get_TrxName(),
				  " SELECT COUNT(*) FROM M_AuthorizationChainLink "
				+ " WHERE (" + this.getMinimumAmount() + " BETWEEN MinimumAmount AND MaximumAmount "
				+ " OR "+ ((this.getMaximumAmount().equals(BigDecimal.ZERO))?new BigDecimal(99999999):this.getMaximumAmount()) + " BETWEEN MinimumAmount AND MaximumAmount) "
				+ " AND M_AuthorizationChain_ID = " + this.getM_AuthorizationChain_ID()
				+ " AND ValidateDocumentAmount = 'Y' "
				+ " AND M_AuthorizationChainLink_ID <> " + this.getM_AuthorizationChainLink_ID()
				+ " AND AD_Org_ID = " + getAD_Org_ID()
				+ " AND AD_Client_ID = " + getAD_Client_ID()
				) > 0)) {
			log.saveError(Msg.getMsg(getCtx(), "AlreadyExistsARangeOfAmounts"),"");
			return false;
		}
		//No permitir que se cree un eslabón con un número de eslabón existente
		if (DB.getSQLValue(
				this.get_TrxName(),
				  " SELECT COUNT(*) FROM M_AuthorizationChainLink "
				  + " WHERE linknumber = " + this.getLinkNumber()
				  + " AND M_AuthorizationChain_ID = " + this.getM_AuthorizationChain_ID()
				  + " AND M_AuthorizationChainLink_ID <> " + this.getM_AuthorizationChainLink_ID()
				  + " AND AD_Org_ID = " + getAD_Org_ID()
				  + " AND AD_Client_ID = " + getAD_Client_ID()
				  ) > 0){
			log.saveError(Msg.getMsg(getCtx(), "AlreadyExistsLinkNumber"),"");
			return false;
		}
		//Si está activo el campo "Validar Montos de Documento", controlar que el monto máximo sea mayor al monto mínimo
		if (isValidateDocumentAmount() && (getMinimumAmount().compareTo(getMaximumAmount()) > 0)){
			log.saveError(Msg.getMsg(getCtx(), "MaximumMinimumAmountError"),"");
			return false;
		}
		//Si la descripción se deja vacía se debe rellenar con “Autorización #N” donde N es el Número de Eslabón. 
		if ("".equals(this.getDescription()) || (this.getDescription() == null))
			this.setDescription("Autorización #" + getLinkNumber());
		//Si no está activo el campo "Validar Montos de Documento", el campo obligatorio debe ser true
		if (!isValidateDocumentAmount())
			setMandatory(true);
		
		return true;
	}

	@Override
	protected boolean beforeDelete() {
		// Si existe algún documento que tenga asociado un eslabón de la cadena
		// que todavía esté pendiente, no se puedo borrar
		if (DB.getSQLValue(
				this.get_TrxName(),
				"SELECT COUNT(*) M_AuthorizationChainDocument_ID FROM M_AuthorizationChainDocument WHERE Status = '"
						+ X_M_AuthorizationChainDocument.STATUS_Pending
						+ "' AND M_AuthorizationChainLink_ID = "
						+ this.getM_AuthorizationChainLink_ID()
						+ " AND AD_Org_ID = " + getAD_Org_ID()
						+ " AND AD_Client_ID = " + getAD_Client_ID()
						) > 0) {
			log.saveError(
					Msg.getMsg(getCtx(), "AlreadyExistsLinkInPendingState"), "");
			return false;
		}
		return true;
	}
	
}
