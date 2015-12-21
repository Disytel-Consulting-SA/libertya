package org.openXpertya.model;

import java.math.BigDecimal;



/**
 * 
 * @author Equipo de Desarrollo de Disytel
 * 
 */

public interface Authorization {

	/**
	 * @return getAuthorizationID
	 */
	public int getAuthorizationID();
	
	/**
	 * @param 
	 * @return 
	 */
	public int getID();
	
	public String get_TableName();

	public void setDocumentID(X_M_AuthorizationChainDocument authDocument);
	
	public Integer getDocTypeID();

	public BigDecimal getGrandTotal();
	
	public int getC_Currency_ID();
	
	public void setOldGrandTotal (BigDecimal OldGrandTotal);
}
