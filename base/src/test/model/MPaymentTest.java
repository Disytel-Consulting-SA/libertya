package test.model;

import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.model.MPayment;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MPaymentTest extends GenericTest<MPayment>{
	
	@Override
	protected PO getTestingEntity() {
		
		// Se requiere una cuenta bancaria para las pruebas
        DB.executeUpdate("INSERT INTO libertya.c_bankaccount " +
                "(c_bankaccount_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, c_bank_id, c_currency_id, bankaccounttype, accountno, currentbalance, creditlimit, isdefault, description, iban, bban, cc, dc, oficina, sucursal, ischequesencartera, ad_componentobjectuid, c_bankaccount_location_id, electronicpaymentsaccount) " +
                "VALUES(9999999, 1010016, 1010053, 'Y', now(), 100, now(), 100, 1010112, 118, 'C', '123', 0.000000, 0.000000, 'N', '123', NULL, NULL, NULL, NULL, NULL, '321', 'N', NULL, NULL, 'N'); ");

		MPayment payment = new MPayment(Env.getCtx(), 0, null);
		
		payment.setIsSOTrx(true);
        payment.setAD_Org_ID(1010053);
        payment.setC_BPartner_ID(1012145);
        payment.setC_Currency_ID(118);
        payment.setC_DocType_ID(1010512);
        payment.setDateAcct(Timestamp.valueOf(TEST_DATE));
        payment.setBank_Payment_Date(Timestamp.valueOf(TEST_DATE));
        
        
        
//        payment.setPayamt(new BigDecimal(350));
//        payment.setTendertype("K");
//        payment.setDateemissioncheck(TEST_DATE);
//        payment.setCBankaccountId(9999999);
		
        return payment;
	}

	@Override
	protected MPayment createNewEntity(Properties ctx, int id, String trxName) {
		return new MPayment(ctx, id, trxName);
	}


	@Override
	protected MPayment getModifiedEntity(MPayment currentEntity) {
		//
		return currentEntity;
	}


	@Override
	protected boolean getUpdateAssertTrue(MPayment modifiedEntity) {
		//
//		return modifiedEntity.getName().equalsIgnoreCase("MODIFIED PAYMENT");
		return false;
	}

}
