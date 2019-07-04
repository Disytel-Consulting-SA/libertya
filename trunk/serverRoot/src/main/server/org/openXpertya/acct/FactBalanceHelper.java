package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.FactBalanceConfig;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.X_Fact_Acct_Balance;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

public class FactBalanceHelper {

	/**
	 * Obtiene la clave hash del helper para ir agrupando los valores a
	 * actualizar
	 * 
	 * @param fl
	 *            linea de fact
	 * @return clave hash
	 */
	public static String getKey(FactLine fl){
		String columnName, keyCar;
		Object value;
		String key = String.valueOf(fl.getC_AcctSchema_ID()); 
		for (M_Column column : FactBalanceConfig.getFactacctbalance_columns().get(fl.getC_AcctSchema_ID())) {
			columnName = column.getColumnName();
			value = fl.get_Value(columnName);
			
			keyCar = "_";
			
			if(DisplayType.isDate(column.getAD_Reference_ID())){
				keyCar += Env.getDateFormatted((Timestamp)value);
			}
			else{
				keyCar += value;
			}
			key += keyCar;
		}
		return key;
	}
	
	/**
	 * Actualizar el balance contable basados en la información parámetro
	 * 
	 * @param información
	 *            del balance contable a actualizar
	 */
    public static synchronized int saveFactBalance(Collection<FactBalanceHelper> helpers) throws Exception{
    	int saveds = 0;
    	try{
    		int no;
    		PreparedStatement ps;
	    	for (FactBalanceHelper factBalance : helpers) {
	    		// Actualizar balance contable
	    		ps = factBalance.getUpdate(null, true);
				no = ps.executeUpdate();
				ps.close();
				
				// Si no se actualizó, lo inserto
				if(no == 0){
					ps = factBalance.getInsert(null, true);
					no = ps.executeUpdate();
					ps.close();
					// Si no se insertó, paso algo
					if(no == 0){
						throw new Exception("Error al actualizar balance contable: "+ps);
					}
				}
				saveds += no;
			}
    	} catch(Exception e){
    		throw e;
    	}
    	return saveds;
    }
	
	/** Valores de las condiciones de agrupación del balance contable */
	private Map<String, Object> factBalanceGroupValues;

	/**
	 * Valores de las columnas a actualizar de agrupación del balance contable
	 */
	private Map<String, BigDecimal> factBalanceUpdateValues;
	
	/** Update generado para esta agrupación */
	private String updateGenerated;

	/** Parámetros del update */
	private List<Object> updateParams;
	
	/** Insert generado para esta agrupación */
	private String insertGenerated;

	/** Parámetros del insert */
	private List<Object> insertParams;
	
	public FactBalanceHelper() {
		factBalanceGroupValues = new HashMap<String, Object>();
		factBalanceUpdateValues = new HashMap<String, BigDecimal>();
	}

	public FactBalanceHelper(FactLine line) {
		this();
		init(line);
	}

	/**
	 * Inicialización de actualización del balance contable para los valores de
	 * esta línea de fact
	 * 
	 * @param line
	 *            linea de fact
	 */
	public void init(FactLine line) {
		updateGenerated = "UPDATE "+X_Fact_Acct_Balance.Table_Name+" SET ";
		insertGenerated = "INSERT INTO "+X_Fact_Acct_Balance.Table_Name+"(AD_Client_ID, C_AcctSchema_ID";
		String columnName, condition, columnSep, set = "", where = " WHERE C_AcctSchema_ID = "+line.getC_AcctSchema_ID();
		String insertValues = " VALUES ("+line.getAD_Client_ID()+","+line.getC_AcctSchema_ID();
		updateParams = new ArrayList<Object>();
		insertParams = new ArrayList<Object>();
		Object value;
		// Importes a actualizar basados en el agrupamiento
		BigDecimal valueB;
		for (String uc : FactBalanceConfig.getFactacctbalance_updatecolumns()) {
			valueB = (BigDecimal) line.get_Value(uc);
			factBalanceUpdateValues.put(uc, valueB);
			
			// Update
			columnSep = "";
			if(set.length() > 0){
				columnSep = ",";
			}

			set += columnSep+uc+" = "+uc+" + ? ";
			updateParams.add(valueB);
			
			// Insert
			insertValues += ",?";
			insertGenerated += ","+uc;
			insertParams.add(valueB);
		}
		
		// Valores de agrupamiento de configuración de columnas de config de
		// balance contable
		factBalanceGroupValues.put("C_AcctSchema_ID", line.getC_AcctSchema_ID());
		for (M_Column column : FactBalanceConfig.getFactacctbalance_columns().get(line.getC_AcctSchema_ID())) {
			columnName = column.getColumnName();
			value = line.get_Value(columnName);
			factBalanceGroupValues.put(columnName, value);
			
			condition = " AND ";
			
			// Update
			if(DisplayType.isDate(column.getAD_Reference_ID())){
				condition += " TRUNC("+columnName+") = TRUNC(?::date) "; 
			}
			else if(DisplayType.isNumeric(column.getAD_Reference_ID())){
				condition += " COALESCE("+columnName+", 0) = COALESCE(?, 0) ";
			}
			else{
				
				condition += columnName+" = ? ";
			}
			where += condition;
			updateParams.add(value);
			
			// Insert
			insertValues += ","+"?";
			insertGenerated += ","+columnName;
			insertParams.add(value);
		}
		
		// Insert
		insertGenerated += ") ";
		insertValues += ") ";
		insertGenerated += insertValues;
		// Update
		updateGenerated += set+where;
	}
	
	
	/**
	 * Obtiene el prepared statement dependiendo los parámetros
	 * 
	 * @param sql
	 *            sql update o insert
	 * @param params
	 *            parámetros a setear
	 * @param trxName
	 *            trx actual
	 * @return prepared statement
	 * @throws Exception
	 */
	private PreparedStatement getPS(String sql, List<Object> params, String trxName, boolean noConvert) throws Exception{
		CPreparedStatement ps = new CPreparedStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE, sql, trxName, noConvert);
		int i = 0;
		for (Object p : params) {
			ps.setObject(++i, p);
		}
		return ps;
	}
	
	/**
	 * @return PS con el update generado
	 */
	public PreparedStatement getUpdate(String trxName, boolean noConvert) throws Exception{
		return getPS(updateGenerated, updateParams, trxName, noConvert);
	}
	
	/**
	 * @return PS con el insert generado
	 */
	public PreparedStatement getInsert(String trxName, boolean noConvert) throws Exception{
		return getPS(insertGenerated, insertParams, trxName, noConvert);
	}
	
	/**
	 * Suma los valores a actualizar del linea sobre este objeto
	 * @param line linea de fact
	 */
	public void add(FactLine line){
		// Iterar por las columnas a actualizar e incrementar sus valores, hay que
		// dejar el update y el insert para el final
		BigDecimal valueB;
		for (String uc : FactBalanceConfig.getFactacctbalance_updatecolumns()) {
			valueB = factBalanceUpdateValues.get(uc);
			if(valueB == null){
				valueB = BigDecimal.ZERO;
			}
			valueB = valueB.add((BigDecimal) line.get_Value(uc));
			factBalanceUpdateValues.put(uc, valueB);
		}
	}
}
