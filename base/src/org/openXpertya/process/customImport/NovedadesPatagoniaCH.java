package org.openXpertya.process.customImport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_BankAccount;
import org.openXpertya.model.X_C_BankList;
import org.openXpertya.model.X_I_PaymentBankNews;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class NovedadesPatagoniaCH extends FileImportProcess {

	@Override
	protected String doIt() throws Exception {
		int saved = 0;
		int currentLine = 0;
		BufferedReader reader = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		X_I_PaymentBankNews news;
		String line; // Linea a procesar
		
		try {
			reader = new BufferedReader(new FileReader(p_file));
			while ((line = reader.readLine()) != null) {
				String op = read(line, 1, 25);
				BigDecimal amount = new BigDecimal(read(line, 80, 94)).divide(Env.ONEHUNDRED, 2,
						BigDecimal.ROUND_HALF_UP);
				String nroCheque = read(line, 99, 107);
				Date processDate = formatter.parse(read(line, 56, 63));
				int bankID = getC_Bank_ID(op);
				
				// Guardar la novedad
				news = new X_I_PaymentBankNews(getCtx(), 0, get_TrxName());
				news.setC_Bank_ID(bankID);
				news.setCheckNo(nroCheque);
				news.setPayment_Amount(amount);
				news.setPayment_Order(op);
				news.setProcess_Date(new Timestamp(processDate.getTime()));
				news.setReceipt_Number(nroCheque);
				if(news.save()){
					saved++;
				}
				else{
					return errorMsg(null, true);
				}
				
				currentLine++;
			}
		} catch(Exception e){
			reader.close();
			return errorMsg(e, false);
		} finally {
			reader.close();
		}
		
		Object[] params = new Object[1];
		params[0] = saved;

		return successMsg(params);
	}
	
	private int getC_Bank_ID(String OPDocumentno) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	ba.c_bank_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("	INNER JOIN " + X_C_BankList.Table_Name + " bl ON bl.c_banklist_id = ah.c_banklist_id ");
		sql.append("	INNER JOIN " + X_C_BankAccount.Table_Name + " ba ON ba.c_bankaccount_id = bl.c_bankaccount_id ");
		sql.append("WHERE ");
		sql.append("	ah.documentno = '").append(OPDocumentno).append("'");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "getC_Bank_ID", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
		return 0;
	}
}
