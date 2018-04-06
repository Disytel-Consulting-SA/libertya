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

/**
 * Modelo de Promociones.
 */
/**
 * @author usuario
 *
 */
public class MPromotion extends X_C_Promotion {

	/** Logger para métodos static */
	private static CLogger s_log = CLogger.getCLogger(MCombo.class);
	
	/** Esquema de descuento de esta promoción */
	private MDiscountSchema discountSchema = null;
	
	/**
	 * Constructor de PO.
	 * @param ctx
	 * @param C_Promotion_ID
	 * @param trxName
	 */
	public MPromotion(Properties ctx, int C_Promotion_ID, String trxName) {
		super(ctx, C_Promotion_ID, trxName);
	}

	/**
	 * Constructor de PO.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MPromotion(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Obtiene las promociones activas y publicadas que son válidas para una
	 * determinada fecha.
	 * 
	 * @param date
	 *            Fecha de consulta de validez.
	 * @param ctx
	 *            Contexto para la instanción de los {@link MPromotion}.
	 * @param trxName
	 *            Transacción para la instanciación de los {@link MPromotion}.
	 * @param loadDiscountSchema
	 *            Si es <code>true</code> se cargará en memoria el esquema de
	 *            descuento asociado a cada promoción cargada.
	 * @return Lista de {@link MPromotion} con las promociones válidas. Si no se
	 *         encuentran promociones para esa fecha devuelve una lista vacía.
	 */
	public static List<MPromotion> getValidFor(Date date, Properties ctx, String trxName, boolean loadDiscountSchema) {
		List<MPromotion> validPromos = new ArrayList<MPromotion>();
		
		PreparedStatement pstmt = null;
		ResultSet         rs    = null;
		
		// Obtiene todas las promociones activas y publicadas cuyo rango de validez
		// contenga la fecha recibida com parámetro.
		String sql = 
			"SELECT * " +
			"FROM C_Promotion " +
			"WHERE AD_Client_ID = ? " +
			  "AND IsActive = 'Y' " +
			  "AND PublishStatus = ? " +
			  "AND ValidFrom::date <= ?::date AND (ValidTo::date IS NULL OR ?::date <= ValidTo::date) " +
			"ORDER BY ValidFrom ASC";
		
		if (!(date instanceof java.sql.Date)) {
			date = new java.sql.Date(date.getTime());
		}
		if (ctx == null) {
			ctx = Env.getCtx();
		}
		
		try {
			pstmt = DB.prepareStatement(sql, trxName, true);
			int i = 1;
			pstmt.setInt(i++, Env.getAD_Client_ID(ctx));
			pstmt.setString(i++, MCombo.PUBLISHSTATUS_Published);
			pstmt.setDate(i++, (java.sql.Date)date);
			pstmt.setDate(i++, (java.sql.Date)date);
			
			rs = pstmt.executeQuery();
			MPromotion promo = null;
			while (rs.next()) {
				promo = new MPromotion(ctx, rs, trxName);
				if (loadDiscountSchema) {
					promo.getDiscountSchema();
				}
				validPromos.add(promo);
			}
			
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "Error getting valid promotions for " + date, e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		
		return validPromos; 
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// La fecha final de validez no puede ser anterior a la fecha
		// inicial.
		if (getValidTo() != null && getValidTo().compareTo(getValidFrom()) < 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidDateRange"));
			return false;
		}
		
		// Si está publicada se lo marca como procesado a fin de que no se pueda
		// editar.
		setProcessed(isPublished());
		
		return true;
	}
	
	/**
	 * @return Indica si esta promoción está publicada.
	 */
	public boolean isPublished() {
		return PUBLISHSTATUS_Published.equals(getPublishStatus());
	}

	/**
	 * @return El esquema de descuento asociado a esta promoción.
	 */
	public MDiscountSchema getDiscountSchema() {
		if (discountSchema == null) {
			discountSchema = MDiscountSchema.get(getCtx(), getM_DiscountSchema_ID());
		}
		return discountSchema;
	}

}
