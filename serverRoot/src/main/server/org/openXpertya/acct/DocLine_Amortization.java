package org.openXpertya.acct;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;

public class DocLine_Amortization extends DocLine {

	public DocLine_Amortization(String DocumentType, int TrxHeader_ID,
			int TrxLine_ID, String trxName) {
		super(DocumentType, TrxHeader_ID, TrxLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MAccount getAccount( int AcctType,MAcctSchema as ) {
		return p_productInfo.getAccount( AcctType,as );
	}
}
