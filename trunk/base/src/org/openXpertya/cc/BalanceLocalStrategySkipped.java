package org.openXpertya.cc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.PO;
import org.openXpertya.reflection.CallResult;

public class BalanceLocalStrategySkipped extends CurrentAccountBalanceStrategy {

	public BalanceLocalStrategySkipped() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public CallResult checkInvoiceWithinCreditLimit(Properties ctx, String bPartnerColumnNameUID,
			Object bPartnerColumnValueUID, String orgColumnNameUID, Object orgColumnValueUID, BigDecimal invAmt,
			String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult updateBPBalance(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult checkInvoicePaymentRulesBalance(Properties ctx, String bPartnerColumnNameUID,
			Object bPartnerColumnValueUID, String orgColumnNameUID, Object orgColumnValueUID,
			Map<String, BigDecimal> paymentRules, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult setCurrentAccountStatus(Properties ctx, String bPartnerColumnNameUID,
			Object bPartnerColumnValueUID, String orgColumnNameUID, Object orgColumnValueUID, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult afterProcessDocument(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String docColumnNameUID, Object docColumnValueUID,
			PO doc, Object aditionalWorkResult, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult afterProcessDocument(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, Map<PO, Object> aditionalWorkResults, String trxName)
					throws Exception {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult performAditionalWork(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String docColumnNameUID, Object docColumnValueUID,
			PO doc, boolean processed, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult getTenderTypesToControlStatus(Properties ctx, String bPartnerColumnNameUID,
			Object bPartnerColumnValueUID, String orgColumnNameUID, Object orgColumnValueUID, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult hasZeroBalance(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, boolean underMinimumCreditAmt, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult getCreditLimit(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String paymentRule, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult getTotalOpenBalance(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String paymentRule, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult getCreditStatus(Properties ctx, String bPartnerColumnNameUID, Object bPartnerColumnValueUID,
			String orgColumnNameUID, Object orgColumnValueUID, String paymentRule, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

	@Override
	public CallResult validateCurrentAccountStatus(Properties ctx, String orgColumnNameUID, Object orgColumnValueUID,
			String bPartnerColumnNameUID, Object bPartnerColumnValueUID, String creditStatus, String trxName) {
		// TODO Auto-generated method stub
		return new CallResult();
	}

}
