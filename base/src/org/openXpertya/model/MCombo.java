package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MCombo extends X_C_Combo {

	/** Logger para métodos static */
	private static CLogger s_log = CLogger.getCLogger(MCombo.class);
	
	/** Líneas de este Combo */
	private List<MComboLine> lines = null;
	
	/**
	 * Constructor de PO
	 * @param ctx
	 * @param C_Combo_ID
	 * @param trxName
	 */
	public MCombo(Properties ctx, int C_Combo_ID, String trxName) {
		super(ctx, C_Combo_ID, trxName);
	}

	/**
	 * Constructor de PO
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MCombo(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Obtiene los combos activos y publicados que son válidos para una
	 * determinada fecha.
	 * 
	 * @param date
	 *            Fecha de consulta de validez.
	 * @param ctx
	 *            Contexto para la instanción de los {@link MCombo}.
	 * @param trxName
	 *            Transacción para la instanciación de los {@link MCombo}.
	 * @param loadLines
	 *            Si es <code>true</code> se cargarán en memoria las líneas de
	 *            cada combo encontrado.
	 * @return Lista de {@link MCombo} con los combos válidos. Si no se
	 *         encuentran combos para esa fecha devuelve una lista vacía.
	 */
	public static List<MCombo> getValidFor(Date date, Properties ctx, String trxName, boolean loadLines) {
		List<MCombo> validCombos = new ArrayList<MCombo>();
		
		PreparedStatement pstmt = null;
		ResultSet         rs    = null;
		
		// Obtiene todos los combos activos y publicados cuyo rango de validez
		// contenga la fecha recibida com parámetro.
		String sql = 
			"SELECT * " +
			"FROM C_Combo " +
			"WHERE AD_Client_ID = ? " +
			  "AND IsActive = 'Y' " +
			  "AND PublishStatus = ? " +
			  "AND ValidFrom <= ? AND (? <= ValidTo OR ValidTo IS NULL) " +
			"ORDER BY ValidFrom ASC";
		
		if (!(date instanceof java.sql.Date)) {
			date = new java.sql.Date(date.getTime());
		}
		if (ctx == null) {
			ctx = Env.getCtx();
		}
		
		try {
			pstmt = DB.prepareStatement(sql, trxName);
			int i = 1;
			pstmt.setInt(i++, Env.getAD_Client_ID(ctx));
			pstmt.setString(i++, MCombo.PUBLISHSTATUS_Published);
			pstmt.setDate(i++, (java.sql.Date)date);
			pstmt.setDate(i++, (java.sql.Date)date);
			
			rs = pstmt.executeQuery();
			MCombo combo = null;
			while (rs.next()) {
				combo = new MCombo(ctx, rs, trxName);
				if (loadLines) {
					combo.loadLines();
				}
				validCombos.add(combo);
			}
			
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "Error getting valid combos for " + date, e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		
		return validCombos; 
	}

	/**
	 * Cantidad de líneas de combo existentes para el combo parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param comboID
	 *            id del combo
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return cantidad de líneas para este combo.
	 */
	public static int getLineCount(Properties ctx, Integer comboID, String trxName){
		// Cantidad de líneas que contiene el combo
		return DB.getSQLValue(trxName,
				"SELECT count(*) FROM c_comboline WHERE c_combo_id = ?",
				comboID);		
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// La fecha final de validez no puede ser anterior a la fecha
		// inicial.
		if (getValidTo() != null && getValidTo().compareTo(getValidFrom()) < 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidDateRange"));
			return false;
		}
		
		// Si está publicado, debe contener al menos una línea de combo
		if(isPublished()){
			if(newRecord){
				log.saveError("SaveError", Msg.translate(getCtx(), "PublishedComboNew"));
				return false;
			}
			else if(getLineCount(getCtx(), getID(), get_TrxName()) <= 0){
				log.saveError("SaveError", Msg.translate(getCtx(), "PublishedComboWithoutLines"));
				return false;
			}
		}
		
		// Si está publicado se lo marca como procesado a fin de que no se pueda
		// editar el encabezado ni las líneas
		setProcessed(isPublished());
	
		return true;
	}
	
	/**
	 * @return Indica si este combo está publicado.
	 */
	public boolean isPublished() {
		return PUBLISHSTATUS_Published.equals(getPublishStatus());
	}
	
	/**
	 * Carga en memoria las líneas de este combo.
	 */
	protected void loadLines() {
		if (lines == null) {
			lines = new ArrayList<MComboLine>();
		}
		lines.clear();
		
		PreparedStatement pstmt = null;
		ResultSet         rs    = null;

		// Obtiene las líneas activas de este combo.
		String sql = 
			"SELECT * FROM C_ComboLine WHERE IsActive = 'Y' AND C_Combo_ID = ?";
		
		try {
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_Combo_ID());
			rs = pstmt.executeQuery();
			MComboLine line = null;
			while (rs.next()) {
				line = new MComboLine(getCtx(), rs, get_TrxName());
				lines.add(line);
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error getting Combo Lines. C_Combo_ID=" + getC_Combo_ID(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}

	/**
	 * Devuelve la lista de líneas de este combo.
	 * 
	 * @param reload
	 *            Si es <code>true</code> realiza la carga nuevamente desde la
	 *            BD y guarda las instancias cargadas en memoria.
	 * @return Lista con las líneas de este combo contenidas en memoria o
	 *         cargadas desde la BD según el valor de <code>reload</code>
	 */
	public List<MComboLine> getLines(boolean reload) {
		if (lines == null || reload) {
			loadLines();
		}
		return lines;
	}

	/**
	 * @return La lista de líneas en memoria de este combo. No realiza la carga
	 *         desde la BD. La invocación a este método es equivalente a
	 *         {@code getLines(false)}.
	 */
	public List<MComboLine> getLines() {
		return getLines(false);
	}

	/**
	 * @return El matching de artículos correspondiente a este combo, generado a
	 *         partir de las líneas activas del mismo.
	 */
	public ProductMatching getProductMatching() {
		ProductMatching productMatching = new ProductMatching();
		for (MComboLine line : getLines()) {
			if (line.isActive()) {
				productMatching.addProduct(line.getM_Product_ID(), line.getQty());
			}
		}
		return productMatching;
	}

	/**
	 * @return Indica si los descuentos generados por este combo se aplican como
	 *         "Descuentos al Precio".
	 */
	public boolean isDiscountToPriceApplication() {
		return DISCOUNTAPPLICATION_DiscountToPrice.equals(getDiscountApplication());
	}
	
	/**
	 * @return Indica si los descuentos generados por este combo se aplican como
	 *         "Bonificaciones".
	 */
	public boolean isBonusApplication() {
		return DISCOUNTAPPLICATION_Bonus.equals(getDiscountApplication());
	}

}
