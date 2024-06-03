package org.openXpertya.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

public class MFixedTerm extends X_C_FixedTerm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1613959704539598746L;
	
	public MFixedTerm(Properties ctx, int C_FixedTerm_ID, String trxName) {
		super(ctx, C_FixedTerm_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MFixedTerm(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}	

	protected boolean beforeDelete() {
		// No se puede eliminar un plazo fijo ya constituido 
		if (isConstituted()) {
			log.saveError("DeleteError", Msg.translate(getCtx(), "FixedTermConsitutedError"));
			return false;
		}
		
		return true;
	}

	protected boolean beforeSave(boolean newRecord) {
		try {
			//Calculo y seteo el plazo del plazo fijo en días
			int term = TimeUtil.getDiffDays(getTrxDate(), getDueDate());
			setTerm(term);
			
			//Calculo y seteo el rendimiento 
			BigDecimal yearDays = new BigDecimal(365); 
			BigDecimal tnaTerm = new BigDecimal(getTNATerm());
			BigDecimal termDays = new BigDecimal(term);
			BigDecimal tna = getTNA().divide(yearDays, 10, BigDecimal.ROUND_UP).multiply(tnaTerm);
			BigDecimal returnAmt = getInitialAmount().multiply(tna).divide(tnaTerm, 10, BigDecimal.ROUND_UP).multiply(termDays); //.setScale(2, BigDecimal.ROUND_UP) , 4, BigDecimal.ROUND_UP
			setReturnAmt(returnAmt);
			
			//Calculo y seteo el importe de renteciones
			calculateAndSetRetentionAmt();
			
			//Calculo y seteo neto a cobrar 
			BigDecimal netAmt = getInitialAmount().add(returnAmt).subtract(getRetentionAmt());
			setNetAmt(netAmt);
			
			//Calculo y seteo TEA 
			BigDecimal teaAmt = powNumber(BigDecimal.ONE.add(tna), yearDays.divide(tnaTerm, 10, BigDecimal.ROUND_UP)).subtract(BigDecimal.ONE).multiply(HUNDRED);
			setTEA(teaAmt);
		} catch (Exception e) {
			log.saveError("SaveError", Msg.translate(getCtx(), "FixedTermCalculationError"));
			return false;
		}
				
		
		return true;
	}
	
	protected void calculateAndSetRetentionAmt() {
		BigDecimal retentionAmt = BigDecimal.ZERO;
		
		for (MFixedTermRetention retention : getRetentions()) {
			retentionAmt = retentionAmt.add(retention.getRetentionAmt());
		}
		
		setRetentionAmt(retentionAmt);
	}
	
	public MFixedTermRetention[] getRetentions() {
		ArrayList<MFixedTermRetention> list = new ArrayList<MFixedTermRetention>();
		String sql = "SELECT * FROM C_FixedTermRetention WHERE C_FixedTerm_ID=? ";

		PreparedStatement pstmt = null;

		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_FixedTerm_ID());

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				MFixedTermRetention il = new MFixedTermRetention(getCtx(), rs, get_TrxName());
				list.add(il);
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			log.log(Level.SEVERE, "getRetentions", e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}

			pstmt = null;
		}
		
		MFixedTermRetention[] lines = new MFixedTermRetention[list.size()];

		list.toArray(lines);

		return lines;
	}
	
	@Override
	public BigDecimal getTNA() {
		return super.getTNA().divide(HUNDRED);
	}
	
	public static BigDecimal powNumber(BigDecimal base, BigDecimal exponent) {
        BigDecimal result = BigDecimal.ZERO;
        int signOf2 = exponent.signum();

        // Perform X^(A+B)=X^A*X^B (B = remainder)
        double dn1 = base.doubleValue();
        // Compare the same row of digits according to context
        BigDecimal n2 = exponent.multiply(new BigDecimal(signOf2)); // n2 is now positive
        BigDecimal remainderOf2 = n2.remainder(BigDecimal.ONE);
        BigDecimal n2IntPart = n2.subtract(remainderOf2);
        // Calculate big part of the power using context -
        // bigger range and performance but lower accuracy
        BigDecimal intPow = base.pow(n2IntPart.intValueExact());
        BigDecimal doublePow = new BigDecimal(Math.pow(dn1, remainderOf2.doubleValue()));
        result = intPow.multiply(doublePow);

        // Fix negative power
        if (signOf2 == -1)
            result = BigDecimal.ONE.divide(result, RoundingMode.HALF_UP);
        return result;
    }
	
	public X_C_Bank getBank() {
		return new X_C_Bank(getCtx(), getC_Bank_ID(), get_TrxName());
	}
	
	public X_C_BankAccount getBankAccount() {
		return new X_C_BankAccount(getCtx(), getC_BankAccount_ID(), get_TrxName());
	}
	
	public X_C_BankAccount getBankFixedTermAccount() {
		return new X_C_BankAccount(getCtx(), getC_BankAccountFixedTerm_ID(), get_TrxName());
	}
	
}
