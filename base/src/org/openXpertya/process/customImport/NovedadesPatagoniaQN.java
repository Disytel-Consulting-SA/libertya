package org.openXpertya.process.customImport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.model.X_C_AllocationHdr;
import org.openXpertya.model.X_C_BankAccount;
import org.openXpertya.model.X_C_BankList;
import org.openXpertya.model.X_I_PaymentBankNews;
import org.openXpertya.util.DB;

/**
 * Proceso de importación de novedades de pago mediante
 * archo provisto por el Banco Patagonia.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class NovedadesPatagoniaQN extends FileImportProcess {

	private TempLine tempLine;
	private Timestamp newsDate;

	@Override
	protected String doIt() throws Exception {

		int saved = 0;
		int currentLine = 0;
		tempLine = null;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(p_file));
			
			String line; // Linea a procesar
			while ((line = reader.readLine()) != null) {

				// Si es la primera linea, se trata de la cabecera.
				if (currentLine == 0) {
					readHead(line);
				}

				if (line.startsWith("N1")) {
					if(tempLine != null){
						if (!saveRecord()) {
							return errorMsg(null, true);
						} else {
							saved++;
						}
					}
					readPaymentOrderHead(line);
				}

				// Si se lee una linea de detalle, se registra el mensaje
				if (line.startsWith("QN")) {
					readPaymentOrderLine(line);
				}
				
				currentLine++;
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
	 * Guarda un nuevo registro en la base de datos.
	 * @return <code>true</code> si el registro se guardó
	 * correctamente, caso contrario, <code>false</code>
	 */
	private boolean saveRecord() {
		X_I_PaymentBankNews record = new X_I_PaymentBankNews(getCtx(), 0, get_TrxName());

		record.setProcess_Date(tempLine.getProcessDate());
		record.setPayment_Order(tempLine.getPaymentOrder());
		record.setCheckNo(tempLine.getCheckNo());
		record.setPayment_Status_Msg(tempLine.getPaymentStatusMsg());
		record.setPayment_Status_Msg_Description(tempLine.getPaymentStatusMsgDescription());
		record.setReceipt_Number(tempLine.getReceiptNumber());
		record.setC_Bank_ID(tempLine.getC_bank_id());

		return record.save();
	}

	/**
	 * Lee los registros cabecera.
	 * @param line Linea de donde se extraerán los datos.
	 * @throws ParseException Si ocurre un error convirtiendo el string correspondiente a la fecha
	 */
	private void readHead(String line) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date processDate = formatter.parse(read(line, 54, 61));
		newsDate = new Timestamp(processDate.getTime());
	}

	/**
	 * Lee los registros de cabecera de cada orden de pago.
	 * @param line Linea de donde se extraerán los datos.
	 */
	private void readPaymentOrderHead(String line) {
		tempLine = new TempLine();
		tempLine.setProcessDate(newsDate);
		tempLine.setPaymentOrder(read(line, 23, 47));
		tempLine.setReceiptNumber(read(line, 48, 55));

		// Utilizo esta variable porque en el archivo no se respetan
		// los espacios al final para respetar el largo del campo.
		int end = line.length();
		if (end > 73) {
			end = 73;
		}
		tempLine.setCheckNo(read(line, 61, end));  	//Leo dos lineas después porque viene una letra, sin embargo en libertya
													//desde la ventana OP solo deja poner 8 caracteres en el nro. de cheque (a 
													//pesar de que en la base tiene 30 

		// Consulta para identificar el banco al que corresponde la novedad.

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	ba.c_bank_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_AllocationHdr.Table_Name + " ah ");
		sql.append("	INNER JOIN " + X_C_BankList.Table_Name + " bl ");
		sql.append("		ON bl.c_banklist_id = ah.c_banklist_id ");
		sql.append("	INNER JOIN " + X_C_BankAccount.Table_Name + " ba ");
		sql.append("		ON ba.c_bankaccount_id = bl.c_bankaccount_id ");
		sql.append("WHERE ");
		sql.append("	ah.documentno = ? ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());

			ps.setString(1, tempLine.getPaymentOrder());

			rs = ps.executeQuery();

			if (rs.next()) {
				tempLine.setC_bank_id(rs.getInt(1));
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "readPaymentOrderHead", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}

		}
	}

	/**
	 * Lee los detalles de estados y eventos del día para cada Orden de Pago.
	 * @param line Linea de donde se extraerán los datos.
	 */
	private void readPaymentOrderLine(String line) {
		tempLine.setPaymentStatusMsg(read(line, 17, 76));
		tempLine.setPaymentStatusMsgDescription(
				tempLine.getPaymentStatusMsgDescription() == null ? read(line, 80, line.length())
						: tempLine.getPaymentStatusMsgDescription() + "." + read(line, 80, line.length()));
	}

	// ------------------------------------------------------------------------

	/**
	 * Clase que almacena datos temporales, que luego se convertirán
	 * en un registro mas de la tabla <code>I_PaymentBankNews</code>
	 * @author Kevin Feuerschvenger - Sur Software S.H.
	 */
	private class TempLine {

		// Datos de Registro cabecera
		private Timestamp processDate;

		// Datos de Registro cabecera de cada orden de pago
		private String paymentOrder;
		private String checkNo;
		private String receiptNumber;
		private int c_bank_id;

		// Datos de Registro de Estados y Eventos del día para cada Orden de Pago
		private String paymentStatusMsg;
		//Descripción del estado
		private String paymentStatusMsgDescription;
		
		// GETTERS & SETTERS:

		public Timestamp getProcessDate() {
			return processDate;
		}

		public void setProcessDate(Timestamp processDate) {
			this.processDate = processDate;
		}

		public String getPaymentOrder() {
			return paymentOrder;
		}

		public void setPaymentOrder(String paymentOrder) {
			this.paymentOrder = paymentOrder;
		}

		public String getCheckNo() {
			return checkNo;
		}

		public void setCheckNo(String checkNo) {
			this.checkNo = checkNo;
		}

		public String getPaymentStatusMsg() {
			return paymentStatusMsg;
		}

		public void setPaymentStatusMsg(String paymentStatusMsg) {
			this.paymentStatusMsg = paymentStatusMsg;
		}

		public String getReceiptNumber() {
			return receiptNumber;
		}

		public void setReceiptNumber(String receiptNumber) {
			this.receiptNumber = receiptNumber;
		}

		public int getC_bank_id() {
			return c_bank_id;
		}

		public void setC_bank_id(int c_bank_id) {
			this.c_bank_id = c_bank_id;
		}

		public String getPaymentStatusMsgDescription() {
			return paymentStatusMsgDescription;
		}

		public void setPaymentStatusMsgDescription(String paymentStatusMsgDescription) {
			this.paymentStatusMsgDescription = paymentStatusMsgDescription;
		}

	}

}
