package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MDiscountConfig extends X_M_DiscountConfig {

	// Constantes de clase de descuentos
	/** General Discount Schema = G */
	public static final String DISCOUNT_GeneralDiscountSchema = DOCUMENTDISCOUNT1_GeneralDiscountSchema;
	/** BPartner Discount Schema = B */
	public static final String DISCOUNT_BPartnerDiscountSchema = DOCUMENTDISCOUNT1_BPartnerDiscountSchema;
	/** Products Combo = C */
	public static final String DISCOUNT_ProductsCombo = DOCUMENTDISCOUNT1_ProductsCombo;
	/** Promotion = P */
	public static final String DISCOUNT_Promotion = DOCUMENTDISCOUNT1_Promotion;
	
	/** Logger para métodos static */
	private static CLogger s_log = CLogger.getCLogger(MDiscountConfig.class);
	
	/** Constante utilizada para asignar valores vacíos a los descuentos */
	private static final String EMPTY_DISCOUNT = "";
	
	/**
	 * Constructor de PO
	 * @param ctx
	 * @param M_DiscountConfig_ID
	 * @param trxName
	 */
	public MDiscountConfig(Properties ctx, int M_DiscountConfig_ID,
			String trxName) {
		super(ctx, M_DiscountConfig_ID, trxName);
	}

	/**
	 * Constructor de PO
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MDiscountConfig(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Busca una configuración de descuentos para una organización.
	 * 
	 * @param orgID
	 *            ID de organización.
	 * @return La configuración de la organización parámetro, si no existe
	 *         devuelve la configuración de la organización * y si esta última
	 *         tampoco existe devuelve <code>null</code>.
	 */
	public static MDiscountConfig get(int orgID) {
		MDiscountConfig config = null;
		Properties ctx = Env.getCtx();
		StringBuffer sql = new StringBuffer();
		// Obtiene la configuración para la organización o la config para la
		// organización 0. Tiene prioridad la organización parámetro por eso se
		// asigna el orden descendente por AD_Org_ID.
		sql.append("SELECT * ")
		   .append("FROM M_DiscountConfig ")
		   .append("WHERE IsActive = 'Y' ")
		   .append(  "AND AD_Client_ID = ? ")
		   .append(  "AND AD_Org_ID IN (?, 0) ")
		   .append("ORDER BY AD_Org_ID DESC ");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString());
			pstmt.setInt(1, Env.getAD_Client_ID(ctx));
			pstmt.setInt(2, orgID);
			
			rs = pstmt.executeQuery();
			if (rs.next()) {
				config = new MDiscountConfig(ctx, rs, null);
			}
		} catch (SQLException e) {
			s_log.log(Level.SEVERE,
					"Error getting discount configuration for Org " + orgID, e); 
		}
		return config;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		if (newRecord && duplicatedOrgConfig()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "DuplicatedDiscountConfigError"));
			return false;
		}
		
		// Validación de configuraciones de descuentos a nivel documento.
		if (!validateDocumentDiscountConfig()) {
			return false;
		}
		
		// Validación de configuraciones de descuentos a nivel línea.
		if (!validateLineDiscountConfig()) {
			return false;
		}
		
		return true;
	}

	/**
	 * Valida las configuraciones para descuentos a nivel documento.
	 * 
	 * @return <code>true</code> si es válida. <code>false</code> si es
	 *         incorrecta y guarda en el log el mensaje de error.
	 */
	private boolean validateDocumentDiscountConfig() {
		
		// Si existe al menos una aplicación de descuento entonces el cargo es
		// requerido.
		boolean chargeRequired = isApplyAllDocumentDiscount()
				|| getDocumentDiscount1() != null;
		if (chargeRequired && getDocumentDiscountCharge_ID() == 0) {
			log.saveError("SaveError", Msg.translate(getCtx(), "DocumentDiscountChargeMandatory"));
			return false;
		}
		
		// Si fueron configurados los descuentos 1 y 2 entonces debe ser diferentes.
		if (getDocumentDiscount1() != null && getDocumentDiscount2() != null &&
				getDocumentDiscount1().equals(getDocumentDiscount2())) {
			log.saveError("SaveError", Msg.getMsg(getCtx(),
					"InvalidLevelDiscountConfig",
					new Object[] { Msg.translate(getCtx(), "Document") }));
			return false;
		}
		
		// Si aplica todo, se borran los descuentos que hayan quedado configurados
		if (isApplyAllDocumentDiscount()) {
			setDocumentDiscount1(null);
			setDocumentDiscount2(null);
		}

		// Si no se configuró el descuento con prioridad 1 tampoco existe el 2.
		if (getDocumentDiscount1() == null) {
			setDocumentDiscount2(null);
		}
		
		return true;
	}
	
	/**
	 * Valida las configuraciones para descuentos a nivel línea
	 * 
	 * @return <code>true</code> si es válida. <code>false</code> si es
	 *         incorrecta y guarda en el log el mensaje de error.
	 */
	private boolean validateLineDiscountConfig() {
		// Si no se configura el descuento 1, entonces el 2 y 3 tampoco deben
		// estar configurados.
		if (getLineDiscount1() == null) {
			setLineDiscount2(null);
			setLineDiscount3(null);
		// Si no se configura el descuento 2, entonces el  3 tampoco debe
		// estar configurado.
		} else if (getLineDiscount2() == null) {
			setLineDiscount3(null);
		}

		// Se buscan clases de descuento repetidas en los 3 campos de prioridad. Si hay
		// repetición devuelve error.
		String[] discounts = new String[] { getLineDiscount1(),
				getLineDiscount2(), getLineDiscount3() };
		List<String> discountsList = new ArrayList<String>();
		Set<String> discountsSet = new HashSet<String>();
		for (String discount : discounts) {
			if (discount != null) {
				discountsList.add(discount);
				discountsSet.add(discount);
			}
		}
		if (discountsList.size() != discountsSet.size()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(),
					"InvalidLevelDiscountConfig",
					new Object[] { Msg.translate(getCtx(), "Line") }));
			return false;
		}

		return true;
	}

	/**
	 * Verifica si existe una configuración para la organización de esta config.
	 * 
	 * @return <code>false</code> si no existe, <code>true</code> si ya hay una
	 *         config con esta organización.
	 */
	private boolean duplicatedOrgConfig() {
		String sql = 
			"SELECT COUNT(*) " +
			"FROM M_DiscountConfig " +
			"WHERE AD_Client_ID = ? AND AD_Org_ID = ?";
		Long count = (Long) DB.getSQLObject(get_TrxName(), sql, new Object[] {
				getAD_Client_ID(), getAD_Org_ID() });
		return count > 0;
	}

	/**
	 * @return La lista de descuentos a nivel de línea en el orden de prioridad
	 *         de aplicación indicado. Si no se ha configurado la aplicación de
	 *         ningún descuento a nivel de línea devuelve una lista vacía.
	 */
	public List<String> getLineDiscountsList() {
		List<String> list = new ArrayList<String>();
		if (getLineDiscount1() != null) {
			list.add(getLineDiscount1());
			if (getLineDiscount2() != null) {
				list.add(getLineDiscount2());
				if (getLineDiscount3() != null) {
					list.add(getLineDiscount3());
				}
			}
		}
		return list;
	}

	/**
	 * Convierte valores de Lista vacíos a null
	 * 
	 * @param discount
	 *            Valor del descuento (la interfaz envía "" para representar
	 *            null)
	 * @return
	 */
	private String empty2null(String discount) {
		return discount != null && discount.isEmpty() ? null : discount;
	}

	@Override
	public String getDocumentDiscount1() {
		return empty2null(super.getDocumentDiscount1());
	}

	@Override
	public String getDocumentDiscount2() {
		return empty2null(super.getDocumentDiscount2());
	}

	@Override
	public String getLineDiscount1() {
		return empty2null(super.getLineDiscount1());
	}

	@Override
	public String getLineDiscount2() {
		return empty2null(super.getLineDiscount2());
	}

	@Override
	public String getLineDiscount3() {
		return empty2null(super.getLineDiscount3());
	}

}
