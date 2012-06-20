package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Artículo al cual se puede fraccionar un artículo original 
 * 
 * @author Franco Bonafine - Disytel
 */
public class MProductFraction extends X_M_Product_Fraction {

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MProductFraction.class);
	
	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param M_Product_Fraction_ID
	 * @param trxName
	 */
	public MProductFraction(Properties ctx, int M_Product_Fraction_ID,
			String trxName) {
		super(ctx, M_Product_Fraction_ID, trxName);
	}

	/**
	 * Constructor de la clase
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MProductFraction(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Devuelve el conjunto de artículos destino de fraccionamiento de un determinado
	 * artículo.
	 * @param ctx Contexto de la aplicación
	 * @param productID ID del artículo origen
	 * @param trxName Transacción en la cual se crean los objetos
	 * @return Lista de MProductFraction
	 */
	public static List<MProductFraction> getOfProduct(Properties ctx, int productID, String trxName) {
		List<MProductFraction> productFractions = new ArrayList<MProductFraction>();
		String sql = "SELECT * FROM M_Product_Fraction WHERE M_Product_ID = ? AND IsActive = 'Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1, productID);
			rs = pstmt.executeQuery();
			MProductFraction pf = null; 
			while (rs.next()) {
				pf = new MProductFraction(ctx, rs, trxName);
				productFractions.add(pf);
			}
			
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "MProductFraction Error", e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {	}
		}
		return productFractions;
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// El artículo destino no puede ser el mismo que el origen
		if (getM_Product_ID() == getM_Product_To_ID()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "SplitProductsMustBeDifferent"));
			return false;
		}
		
		// El artículo destino debe ser único, no se puede agregar dos o mas veces
		// el mismo artículo como destino de fraccionamiento de un artículo particular. 
		if (!validateTargetProductExistence()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "SplitTargetProductExistent"));
			return false;
		}
		
		// El artículo origen debe tener al menos una conversión de UM
		if (getProduct().getUOMConversions().isEmpty()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "SplitProductNeedConversions"));
			return false;
		}
		
		// El artículo destino debe ser convertible a al menos una UM a la cual
		// es convertible el artículo origen.
		if (!validateTargetProductUOMs()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidProductSplit"));
			return false;
		}
		
		return true;
	}

	/**
	 * Valida si existen conversiones de UM compatibles entre el artículo
	 * origen y destino
	 */
	private boolean validateTargetProductUOMs() {
		boolean valid = false;
		// Se obtienen las UM de conversión de los artículos origen y destino.
		Set<Integer> productUOMs = getProduct().getUOMConversions().keySet();
		Set<Integer> productToUOMs = getProductTo().getUOMConversions().keySet();
		// Se valida que exista al menos una UM en el producto destino que este
		// contenida en el conjunto de UM definidas en el producto origen.
		for (Integer uomID : productToUOMs) {
			if (productUOMs.contains(uomID)) {
				valid = true;
				break;
			}
		}
		return valid;
	}
	
	/**
	 * Valida si el artículo destino ya se encuentra registrado como un artículo
	 * de fraccionamiento del artículo original.
	 * @return true si el artículo no existe en la lista de artículos de fraccionamiento,
	 * false si ya fue dado de alta en esta tabla.
	 */
	private boolean validateTargetProductExistence() {
		String sql = 
			"SELECT COUNT(*) " +
			"FROM M_Product_Fraction " +
			"WHERE M_Product_ID = ? AND M_Product_To_ID = ? " +
			"  AND M_Product_Fraction_ID <> ?";
		Long count = (Long)DB.getSQLObject(get_TrxName(), sql, 
				new Object[] {getM_Product_ID(), 
							  getM_Product_To_ID(), 
							  getM_Product_Fraction_ID()});
		return count == 0;
	}
	
	/**
	 * @return Devuelve el artículo origen
	 */
	public MProduct getProduct() {
		return MProduct.get(getCtx(), getM_Product_ID());
	}

	/**
	 * @return Devuelve el artículo destino
	 */
	public MProduct getProductTo() {
		return MProduct.get(getCtx(), getM_Product_To_ID());
	}
}
