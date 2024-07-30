package org.openXpertya.process.customImport.utils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MExternalService;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DBException;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Clases de utilidad para trabajar con JSon
 * 
 * 
 * @author jdreher
 *
 */

public class Utilidades {

	private static Gson gsonUtil = new Gson();
	private static CLogger log = CLogger.getCLogger(Utilidades.class);

	public static JsonObject String2JSon(String jsonLine){
		
		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject  jobject = jelement.getAsJsonObject();
		
		log.info("String2JSon  jsonLine:" + jsonLine);
		
		return jobject;
		
	}
	
	public static Object JSon2Object(JsonObject j, String data) {
		
		Object r = j.get(data);
		
		log.info("JSon2Object  JsonObject:" + j + " Data: " + data);
		
		return r;
	}
	
	public static Object readObject(String jsonS, String data) {
		
		JsonObject json = String2JSon(jsonS);
		
		log.info("readObject JSonObject String:" + jsonS + " Data: " + data);
		
		return json.get(data).getAsString();
	}
	
	public boolean hasJsonObject(JsonObject jsonObject, String property) {
		  return jsonObject.has(property) && !(jsonObject.get(property) instanceof JsonNull);
	}
	
	public static BigDecimal StringToBigDecimal(String number) {
		BigDecimal b = null;
		
		if(number==null || number.isEmpty())
			return null;
		
		try {
			b = new BigDecimal(number.trim());
		}catch(Exception ex) {
			log.warning("Error al convertir a BigDecimal: " + number);
			b = BigDecimal.ZERO;
		}
		
		return b;
	}
	
	public static Timestamp StringToTimestamp(String date) {
		Timestamp b = null;
		
		if(date==null || date.length() < 10)
			return null;
	
		if(date.length() < 18)
			date = date + " 00:00:00.0";
		
		try {
			b = Timestamp.valueOf(date);
		}catch(Exception ex) {
			log.warning("Error al convertir a Timestamp: " + date);
		}
		
		return b;
	}
	
	public static int StringToInt(String number) {
		Integer b = null;
		
		try {
			b = Integer.valueOf(number.trim());
		}catch(Exception ex) {
			log.warning("Error al convertir a Integer: " + number);
		}
		
		return b;
	}
	
	public static Long StringToLong(String number) {
		Long b = null;
		
		try {
			b = Long.valueOf(number.trim());
		}catch(Exception ex) {
			log.warning("Error al convertir a Long: " + number);
		}
		
		return b;
	}
	
	public static String getDataExtraSE(int idSE, String value) {
		String data = null;
		
		String sql = "SELECT Name FROM " +
						" C_ExternalServiceAttributes ea " +
						" WHERE C_ExternalService_ID=? AND value=?";
		
		data = DB.getSQLValueString(null, sql, new Object[]{idSE, value});
		
		return data;
	}
	
	public static String getDataExtraSE(int idSE, String tipo, String value) {
		String data = null;
		
		String sql = "SELECT Name FROM " +
						" C_ExternalServiceAttributes ea " +
						" WHERE C_ExternalService_ID=? AND value=? AND Description=? AND IsActive='Y'";
		
		data = DB.getSQLValueString(null, sql, new Object[]{idSE, value, tipo});
		
		return data;
	}
	
	public static String getDataExtraSEbyName(int idSE, String tipo, String name) {
		String data = null;
		
		String sql = "SELECT Value FROM " +
						" C_ExternalServiceAttributes ea " +
						" WHERE C_ExternalService_ID=? AND name=? AND Description=? AND IsActive='Y'";
		
		data = DB.getSQLValueString(null, sql, new Object[]{idSE, name, tipo});
		
		return data;
	}
	
	/**
	 * Obtiene un objeto de configuración de servicios externos.
	 * @param name Nombre por el cual buscar el registro.
	 * @return ID del registro.
	 */
	public static int getExternalServiceByName(String name) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExternalService_ID ");
		sql.append("FROM ");
		sql.append("	" + MExternalService.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");

		return DB.getSQLValue(null, sql.toString(), name);
	}
	
	/**
	 * Obtiene un arreglo de objetos de configuración de servicios externos.
	 * @param tipo Description por el cual buscar el registro.
	 * @return arreglos de atributos del servicio externo.
	 */
	public static ArrayList<X_C_ExternalServiceAttributes> getDataExtraSEFromTipo(int idSE, String Tipo, String trxName) {
		ArrayList<X_C_ExternalServiceAttributes> attr = new ArrayList<X_C_ExternalServiceAttributes>();
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + X_C_ExternalServiceAttributes.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_ExternalService_ID = ? ");
		sql.append("	AND LOWER(description) = LOWER(?) ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, idSE);
			ps.setString(2, Tipo);
			rs = ps.executeQuery();
			while (rs.next()) {
				attr.add(new X_C_ExternalServiceAttributes(Env.getCtx(), rs, trxName));
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "MExternalService.getDataExtraSEFromTipo", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}

		return attr;
		
	}

	/**
	 * Devuelve el nombre de los atributos concidentes con el tipo de atributo enviado
	 * Sobrecargo metodo...
	 * 
	 * @param idSE
	 * @param tipo
	 * @param trxName
	 * @return ArrayList<String> Nombres de los atributos coincidentes
	 */
	public static ArrayList<String> getAttrNames(int idSE, String tipo, String trxName){
		
		return getAttrNames(idSE, tipo, null, trxName);
	}

	
	/**
	 * Devuelve el nombre de los atributos concidentes con el tipo de atributo enviado
	 * 
	 * @param idSE
	 * @param tipo
	 * @param trxName
	 * @return ArrayList<String> Nombres de los atributos coincidentes
	 */
	public static ArrayList<String> getAttrNames(int idSE, String tipo, String value, String trxName){
		
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<X_C_ExternalServiceAttributes> attr = getDataExtraSEFromTipo(idSE, tipo, trxName);
		for(X_C_ExternalServiceAttributes at: attr) {
			if(value!=null)
				if(value.equalsIgnoreCase(at.getValue()))
					ret.add(at.getName());
			else
				ret.add(at.getName());
		}
		
		return ret;
	}
	
	public static Long getSQLValueEx (String trxName, String sql, Object... params) throws DBException
    {
    	Long retValue = -1L;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try
    	{
    		pstmt = DB.prepareStatement(sql, trxName);
    		setParameters(pstmt, params);
    		rs = pstmt.executeQuery();
    		if (rs.next())
    			retValue = rs.getLong(1); 
    		else
    			log.info("No Value " + sql);
    	}
    	catch (SQLException e)
    	{
    		throw new DBException(e, sql);
    	}
    	finally
    	{
    		DB.close(rs, pstmt);
    		rs = null; pstmt = null;
    	}
    	return retValue;
    }
	   /**
		 * Set parameters for given statement
		 * @param stmt statements
		 * @param params parameters array; if null or empty array, no parameters are set
		 */
		public static void setParameters(PreparedStatement stmt, Object[] params)
		throws SQLException
		{
			if (params == null || params.length == 0) {
				return;
			}
			//
			for (int i = 0; i < params.length; i++)
			{
				setParameter(stmt, i+1, params[i]);
			}
		}
		/**
		 * Set PreparedStatement's parameter.
		 * Similar with calling <code>pstmt.setObject(index, param)</code>
		 * @param pstmt
		 * @param index
		 * @param param
		 * @throws SQLException
		 */
		public static void setParameter(PreparedStatement pstmt, int index, Object param)
		throws SQLException
		{
			if (param == null)
				pstmt.setObject(index, null);
			else if (param instanceof String)
				pstmt.setString(index, (String)param);
			else if (param instanceof Integer)
				pstmt.setInt(index, ((Integer)param).intValue());
			else if (param instanceof Long)
				pstmt.setLong(index, (Long)param);
			else if (param instanceof BigDecimal)
				pstmt.setBigDecimal(index, (BigDecimal)param);
			else if (param instanceof Timestamp)
				pstmt.setTimestamp(index, (Timestamp)param);
			else if (param instanceof Boolean)
				pstmt.setString(index, ((Boolean)param).booleanValue() ? "Y" : "N");
			else
				throw new DBException("Unknown parameter type "+index+" - "+param);
		}
		
		/**
		 * Determina el metodo en que se comunica con el servicio web de Fidelius
		 * dREHER
		 */
		public static String getMetodoConnect() {
			String i = MPreference.GetCustomPreferenceValue("MetodoConexionWSFidelius", Env.getAD_Client_ID(Env.getCtx()));
			if(Util.isEmpty(i, true)) {
				i = "1";
			}
			return i;
		}

		/**
		 * A partir del nombre de tarjeta intenta determinar el tipo de tarjeta segun lista de validacion de CreditCardType MPayment
		 * @param tarjeta
		 * @return tipo de tarjeta para MPayment
		 * dREHER
		 */
		public static String getCreditCardType(String tarjeta) {
			String creditCardType = "";
			
			tarjeta = tarjeta.toLowerCase();
			
			if(tarjeta.indexOf("visa") > -1)
				creditCardType = X_C_Payment.CREDITCARDTYPE_Visa;
			else if(tarjeta.indexOf("mastercard") > -1)
				creditCardType = X_C_Payment.CREDITCARDTYPE_MasterCard;
			else if(tarjeta.indexOf("diners") > -1)
				creditCardType = X_C_Payment.CREDITCARDTYPE_Diners;
			else if(tarjeta.indexOf("american") > -1)
				creditCardType = X_C_Payment.CREDITCARDTYPE_Amex;
			else 
				creditCardType = X_C_Payment.CREDITCARDTYPE_PurchaseCard;
			
			return creditCardType;
		}
		
		/**
		 * A partir del nombre de tarjeta intenta determinar el tipo de tarjeta segun lista de validacion de CreditCardType MPayment
		 * @param tarjeta
		 * @return tipo de tarjeta para ubicar el plan de financiacion de la entidad financiera
		 * dREHER
		 */
		public static String getCreditCard(String tarjeta) {
			String creditCardType = "";
			
			tarjeta = tarjeta.toLowerCase();
			boolean isDebito = tarjeta.indexOf("debit") > -1;
			
			if(tarjeta.indexOf("mc") > -1)
				creditCardType = "MA";
			else if(tarjeta.indexOf("visa") > -1)
				creditCardType = "VI";
			else if(tarjeta.indexOf("naranja") > -1)
				creditCardType = "NA";
			else if(tarjeta.indexOf("mastercard") > -1)
				creditCardType = "MA";
			else if(tarjeta.indexOf("nativa") > -1)
				creditCardType = "NA";
			else if(tarjeta.indexOf("cabal") > -1)
				creditCardType = "CA";
			else if(tarjeta.indexOf("confiable") > -1)
				creditCardType = "CO";
			else if(tarjeta.indexOf("patagonia") > -1)
				creditCardType = "PA";
			else if(tarjeta.indexOf("maestro") > -1)
				creditCardType = "MA";
			else 
				creditCardType = X_C_Payment.CREDITCARDTYPE_PurchaseCard;
			
			return creditCardType;
		}
}
