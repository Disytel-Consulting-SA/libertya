package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;

/**
 * Configuración de Servicios Externos.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class MExternalService extends X_C_ExternalService {
	private static final long serialVersionUID = 1L;

	public MExternalService(Properties ctx, int C_ExternalService_ID, String trxName) {
		super(ctx, C_ExternalService_ID, trxName);
	}

	public MExternalService(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/** @return un Map con todos los atributos de una configuración de servicios externos. */
	public Map<String, X_C_ExternalServiceAttributes> getAttributesMap() {
		Map<String, X_C_ExternalServiceAttributes> result = new HashMap<String, X_C_ExternalServiceAttributes>();
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + X_C_ExternalServiceAttributes.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_ExternalService_ID = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getC_ExternalService_ID());
			rs = ps.executeQuery();
			while (rs.next()) {
				result.put(rs.getString("value"), new X_C_ExternalServiceAttributes(getCtx(), rs, get_TrxName()));
			}
			return result;
		} catch (Exception e) {
			log.log(Level.SEVERE, "MExternalService.getAttributes", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return null;
	}

	/**
	 * Obtiene un atributo de la configuración por nombre.
	 * @param attributeValue Clave de búsqueda del atributo a recuperar.
	 * @return Atributo correspondiente, caso contrario, null.
	 */
	public X_C_ExternalServiceAttributes getAttributeByName(String attributeValue) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + X_C_ExternalServiceAttributes.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_ExternalService_ID = ? ");
		sql.append("	AND value = ? ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getC_ExternalService_ID());
			ps.setString(2, attributeValue);
			rs = ps.executeQuery();
			if (rs.next()) {
				return new X_C_ExternalServiceAttributes(getCtx(), rs, get_TrxName());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "MExternalService.getAttributeByName", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return null;
	}

}
