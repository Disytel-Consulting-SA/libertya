package test.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.PO;
import org.openXpertya.util.Env;

import test.util.TestUtil;

public class MInvoiceTest extends DocumentTest<MInvoice>{
	
	@Override
	protected PO getTestingEntity() {
		
		MInvoice inv = new MInvoice(Env.getCtx(), 0, null);
		inv.setIsSOTrx(true);
		inv.setAD_Org_ID(1010053);
		inv.setC_BPartner_ID(1012145);
		inv.setC_BPartner_Location_ID(1012158);
		inv.setC_Currency_ID(118);
		inv.setC_DocType_ID(1010507);
		inv.setC_DocTypeTarget_ID(1015507);
		inv.setC_PaymentTerm_ID(1010083);
		inv.setDateAcct(Timestamp.valueOf(TEST_DATE));
		inv.setDateAcct(Timestamp.valueOf(TEST_DATE));
		inv.setDateInvoiced(Timestamp.valueOf(TEST_DATE));
		inv.setM_PriceList_ID(1010595);		
		inv.setPaymentRule("S");
		inv.setSalesRep_ID(100);		
		inv.setAD_User_ID(100);
		

		MInvoiceLine line1 = new MInvoiceLine(Env.getCtx(), 0, null);
		line1.setLine(10);
		line1.setAD_Org_ID(1010053);
		line1.setQtyInvoiced(new BigDecimal(2));
		line1.setPriceActual(new BigDecimal(100));

		
		
		MInvoiceLine line2 = new MInvoiceLine(Env.getCtx(), 0, null);
		line2.setLine(20);
		line2.setAD_Org_ID(1010053);
		line2.setQtyInvoiced(new BigDecimal(5));
		line2.setPriceActual(new BigDecimal(30));
		
//		InvoiceDocument doc = new InvoiceDocument();
//		doc.setHeader(inv);
//		doc.addLinesItem(line1);
//		doc.addLinesItem(line2);

		
		return inv;
		
	}

	@Override
	protected MInvoice createNewEntity(Properties ctx, int id, String trxName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MInvoice getModifiedEntity(MInvoice currentEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean getUpdateAssertTrue(MInvoice modifiedEntity) {
		// TODO Auto-generated method stub
		return false;
	}



}
