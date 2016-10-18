package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Cabecera a imprimir, correspondiente a un conjunto de pagos mediante cheque.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class MCheckPrinting extends X_C_CheckPrinting {
	private static final long serialVersionUID = 1L;

	/** Cantidad de cheques simultaneos por cabecera. */
	public static final int MAX_LINES = 4;

	/** Cheques a imprimir. */
	private MCheckPrintingLines[] m_lines;

	/**
	 * Constructor.
	 * @param ctx
	 * @param C_CheckPrinting_ID
	 * @param trxName
	 */
	public MCheckPrinting(Properties ctx, int C_CheckPrinting_ID, String trxName) {
		super(ctx, C_CheckPrinting_ID, trxName);
	}

	/**
	 * Constructor.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MCheckPrinting(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * @return la cantidad de registros marcados para imprimir (no impresos).
	 */
	public int getLinesReadyToPrint() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	COUNT(c_checkprintinglines_id) AS count ");
		sql.append("FROM ");
		sql.append("	" + MCheckPrintingLines.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_checkprinting_id = ? ");
		sql.append("	AND print = 'Y' ");
		sql.append("	AND printed = 'N' ");

		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = 0;

		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());

			// Parámetros
			ps.setInt(1, getC_Checkprinting_ID());

			rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getLinesReadyToPrint", e);
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
		return result;
	}

	/** @return arreglo de lineas a imprimir */
	public MCheckPrintingLines[] getLines() {
		if (m_lines != null) {
			return m_lines;
		}
		List<MCheckPrintingLines> list = getLinesList();
		MCheckPrintingLines[] lines = new MCheckPrintingLines[list.size()];
		list.toArray(lines);
		m_lines = lines;
		return lines;
	}

	/** @return lista de lineas a imprimir */
	private List<MCheckPrintingLines> getLinesList() {
		List<MCheckPrintingLines> list = new ArrayList<MCheckPrintingLines>();

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MCheckPrintingLines.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_checkprinting_id = ? ");
		sql.append("	AND print = 'Y' ");
		sql.append("	AND printed = 'N' ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());

			// Parámetros
			ps.setInt(1, getC_Checkprinting_ID());

			rs = ps.executeQuery();

			while (rs.next()) {
				MCheckPrintingLines chl = new MCheckPrintingLines(getCtx(), rs, get_TrxName());
				chl.setMCheckPrinting(this);
				list.add(chl);
			}
			rs.close();
			ps.close();
			ps = null;
		} catch (Exception e) {
			log.log(Level.SEVERE, "getLinesList", e);
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
		return list;
	}

}
