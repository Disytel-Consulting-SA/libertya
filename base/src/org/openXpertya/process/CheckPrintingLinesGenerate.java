package org.openXpertya.process;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MCheckPrinting;
import org.openXpertya.model.MCheckPrintingLines;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Proceso que recupera los cheques disponibles para imprimir.
 * @version 1.0, 05.10.16
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class CheckPrintingLinesGenerate extends SvrProcess {

	/** ID del registro "cabecera" de impresion de cheques. */
	private int p_C_CheckPrinting_ID;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
		p_C_CheckPrinting_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		// Si uno de los cheques ya generados en la impresión se encuentra
		// impreso, entonces no se genera
		if (PO.existRecordFor(getCtx(), MCheckPrintingLines.Table_Name, "c_checkprinting_id = ? and printed = 'Y'",
				new Object[] { p_C_CheckPrinting_ID }, get_TrxName())) {
			throw new Exception(Msg.getMsg(getCtx(), "CheckPrintingAlreadyPrinted"));
		}
		
		MCheckPrinting checkPrinting = new MCheckPrinting(getCtx(), p_C_CheckPrinting_ID, get_TrxName());
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	isactive, ");
		sql.append("	c_bpartner_id, ");
		sql.append("	c_currency_id, ");
		sql.append("	c_payment_id, ");
		sql.append("	checkno, ");
		sql.append("	description, ");
		sql.append("	tendertype, ");
		sql.append("	docstatus, ");
		sql.append("	payamt, ");
		sql.append("	dateemissioncheck, ");
		sql.append("	datetrx ");
		sql.append("FROM ");
		sql.append("	" + MPayment.Table_Name + " ");
		sql.append("WHERE ");
		// Recupero solo los pagos de la cuenta bancaria indicada en la cabecera.
		sql.append("	c_bankaccount_id = ? ");
		// Recupero solo los pagos de tipo cheque.
		sql.append("	AND tendertype = ? ");
		// Recupero solo pagos en estado 'Completo' o 'Cerrado'.
		sql.append("	AND docstatus IN ( ");
		sql.append("		?, ? ");
		sql.append("	) ");
		// Filtro los cheques que ya fueron impresos.
		sql.append("	AND c_payment_id NOT IN ( ");
		sql.append("		SELECT DISTINCT ");
		sql.append("			c_payment_id ");
		sql.append("		FROM ");
		sql.append("			" + MCheckPrintingLines.Table_Name + " ");
		sql.append("		WHERE ");
		sql.append("			printed = 'Y' ");
		sql.append("	) ");
		// Filtro los pagos que ya fueron agregados a la lista.
		sql.append("	AND c_payment_id NOT IN ( ");
		sql.append("		SELECT ");
		sql.append("			c_payment_id ");
		sql.append("		FROM ");
		sql.append("			libertya.c_checkprintinglines ");
		sql.append("		WHERE ");
		sql.append("			c_checkprinting_id != 999 ");
		sql.append("	) ");

		PreparedStatement ps = null;
		ResultSet rs = null;
		int lines = 0;

		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());

			// Parámetros
			ps.setInt(1, checkPrinting.getC_BankAccount_ID());
			ps.setString(2, MPayment.TENDERTYPE_Check);
			ps.setString(3, MPayment.DOCSTATUS_Completed);
			ps.setString(4, MPayment.DOCSTATUS_Closed);

			rs = ps.executeQuery();
			MCheckPrintingLines printingLine;
			while (rs.next()) {

				printingLine = new MCheckPrintingLines(getCtx(), 0, get_TrxName());
				printingLine.setC_Checkprinting_ID(p_C_CheckPrinting_ID);
				printingLine.setIsActive(true);
				printingLine.setPrinted(false);
				printingLine.setPrint(false);

				// Datos a replicar desde la tabla de pagos
				printingLine.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
				printingLine.setC_Currency_ID(rs.getInt("c_currency_id"));
				printingLine.setC_Payment_ID(rs.getInt("c_payment_id"));
				printingLine.setCheckNo(rs.getString("checkno"));
				printingLine.setDescription(rs.getString("description"));
				printingLine.setTenderType(rs.getString("tendertype"));
				printingLine.setDocStatus(rs.getString("docstatus"));
				printingLine.setPayAmt(rs.getBigDecimal("payamt"));
				printingLine.setDateEmissionCheck(rs.getTimestamp("dateemissioncheck"));
				printingLine.setDateTrx(rs.getTimestamp("datetrx"));

				// Guardo el registro.
				if (!printingLine.save()) {
					throw new InvalidParameterException(CLogger.retrieveErrorAsString());
				}
				lines++;
			}
		} catch (InvalidParameterException e) {
			String errorMsg = e.getMessage();
			log.severe(errorMsg);
		} catch (Exception e) {
			log.severe(Msg.getMsg(getCtx(), "CheckPrintingDataBaseError"));
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				log.severe("Cannot close statement or resultset");
			}
		}

		String msg = "";
		if (lines == 0) {
			msg = Msg.getMsg(getCtx(), "NonCheckPrintingGeneratedLines");
		} else {
			msg = Msg.getMsg(getCtx(), "CheckPrintingGeneratedLines") + " " + lines;
		}
		return msg;
	}

}
