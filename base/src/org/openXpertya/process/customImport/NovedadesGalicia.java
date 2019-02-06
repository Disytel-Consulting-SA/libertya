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

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MSequence;
import org.openXpertya.model.X_C_BankAccount;
import org.openXpertya.model.X_C_BankList;
import org.openXpertya.model.X_I_PaymentBankNews;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Proceso de importación de novedades de pago mediante
 * archo provisto por el Banco Galicia.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class NovedadesGalicia extends FileImportProcess {

	@Override
	protected String doIt() throws Exception {

		int saved = 0;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(p_file));
			String line; // Linea a procesar

			String listType = "";
			String listValue = "";

			// Primera linea, cabecera.
			if ((line = reader.readLine()) != null) {
				listType = read(line, 3, 3);
				listValue = read(line, 4, 11);
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			// Confeccionar nro de OP
			MDocType opDocType = MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Orden_De_Pago, get_TrxName());
			if(opDocType == null){
				throw new Exception("Tipo de Documento OP no existe");
			}
			String opPrefix = MSequence.getPrefix(opDocType.getDocNoSequence_ID(), get_TrxName());
			
			// El resto de las lineas del archivo son el detalle.
			while ((line = reader.readLine()) != null) {

				String registerNumber = read(line, 3, 5);

				String checkNo = read(line, 7, 15);
				String paymentStatus = read(line, 26, 27);
				String receiptNumber = read(line, 162, 176);
				String paymentOrder = read(line, 179, 188).replaceFirst ("^0*", "");
				// Agregar el prefijo del tipo de documento OP si es que no lo tiene
				if(!paymentOrder.startsWith(opPrefix)){
					paymentOrder = opPrefix+paymentOrder;
				}
				
				String documentNo = read(line, 251, 258);

				Date updateDate = formatter.parse(read(line, 275, 282));

				// El importe viene en centavos
				BigDecimal amount = (new BigDecimal(read(line, 234, 250))).divide(Env.ONEHUNDRED, 2,
						BigDecimal.ROUND_HALF_UP);
				
				X_I_PaymentBankNews record = new X_I_PaymentBankNews(getCtx(), 0, get_TrxName());
				record.setList_Type(listType);
				record.setList_Value(listValue);
				record.setPayment_Order(paymentOrder);
				record.setCheckNo(checkNo);
				record.setPayment_Status(paymentStatus);
				record.setReceipt_Number(receiptNumber);
				record.setRegister_Number(registerNumber);
				record.setC_Bank_ID(getC_Bank_ID(documentNo));
				record.setProcess_Date(new Timestamp(updateDate.getTime()));
				record.setPayment_Amount(amount);
				
				if (!record.save()) {
					return errorMsg(null, true);
				} else {
					saved++;
				}
			}

			reader.close();

		} catch (Exception e) {
			reader.close();
			return errorMsg(e, false);
		} finally {
			reader.close();
		}

		Object[] params = new Object[1];
		params[0] = saved;

		return successMsg(params);
	}

	/**
	 * Consulta para identificar el banco al que corresponde la novedad.
	 * @param documentno Identificador de lista.
	 * @return Id de banco correspondiente.
	 */
	private int getC_Bank_ID(String documentno) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	ba.c_bank_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_BankList.Table_Name + " bl ");
		sql.append("	INNER JOIN " + X_C_BankAccount.Table_Name + " ba ");
		sql.append("		ON ba.c_bankaccount_id = bl.c_bankaccount_id ");
		sql.append("WHERE ");
		sql.append("	bl.documentno = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setString(1, documentno);

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
