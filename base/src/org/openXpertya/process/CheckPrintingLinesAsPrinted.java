package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.openXpertya.model.MCheckPrintingLines;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Proceso que marca como impresos los cheques marcados para imprimir.
 * @version 1.0, 05.10.16
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class CheckPrintingLinesAsPrinted extends SvrProcess {

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
		int confirmed = 0;
		int removed = 0;
		try {
			// Marca registros como impreso.
			confirmed = confirmPrinted();
		} catch (Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "CheckPrintingLinesUpdateError")); 
		}
		try {
			// Elimina el resto de los registros.
			removed = removeUnused();
		} catch (Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "CheckPrintingLinesDeleteError"));
		}
		String msg = (Msg.getMsg(getCtx(), "CheckPrintingLinesMarkedAsPrinted") + " " + confirmed);
		msg += (", " + Msg.getMsg(getCtx(), "CheckPrintingLinesRemoved") + " " + removed + ".");
		return msg;
	}

	/**
	 * Actualiza los registros marcados para imprimir, marcándolos como impreso.
	 * @return la cantidad de registros actualizados.
	 * @throws SQLException en caso de error.
	 */
	private int confirmPrinted() throws SQLException {
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE ");
		sql.append("	" + MCheckPrintingLines.Table_Name + " ");
		sql.append("SET ");
		sql.append("	printed = 'Y' ");
		sql.append("WHERE ");
		sql.append("	c_checkprinting_id = ? ");
		sql.append("	AND print = 'Y' ");

		int updatedCount = 0;

		PreparedStatement ps = DB.prepareStatement(sql.toString(), get_TrxName());

		// Parámetros
		ps.setInt(1, p_C_CheckPrinting_ID);

		updatedCount = ps.executeUpdate();

		return updatedCount;
	}

	/**
	 * Elimina los registros pendientes de impresión.
	 * @return la cantidad de registros eliminados.
	 * @throws SQLException en caso de error.
	 */
	private int removeUnused() throws SQLException {
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + MCheckPrintingLines.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_checkprinting_id = ? ");
		sql.append("	AND printed = 'N' ");

		int deletedCount = 0;

		PreparedStatement ps = DB.prepareStatement(sql.toString(), get_TrxName());

		// Parámetros
		ps.setInt(1, p_C_CheckPrinting_ID);

		deletedCount = ps.executeUpdate();

		return deletedCount;
	}

}
