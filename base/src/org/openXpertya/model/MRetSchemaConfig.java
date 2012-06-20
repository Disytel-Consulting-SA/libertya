package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;


public class MRetSchemaConfig extends X_C_RetSchema_Config {


	private static final long serialVersionUID = 1L;
	/**	Logger **/
	protected transient CLogger	log = CLogger.getCLogger (getClass());
	/** Rangos asociados con el parámetro/configuración */
	private List<X_C_RetSchema_Range> ranges;
	
	private BigDecimal monto = Env.ZERO;

	/**
	 * Valor que se debe aplicar del rango de la configuración parámetro a
	 * partir del monto parámetro. La consulta se ordena con el monto inicial
	 * ascendente, o sea, si se definen rangos donde el valor final coincide con
	 * el valor inicial de otro rango, se tomará el rango con valor inicial
	 * menor
	 * 
	 * @param retSchemaConfigID
	 *            id de configuración del esquema de retención
	 * @param amt
	 *            monto de evaluación
	 * @param trxName
	 *            transacción actual
	 * @return el valor a aplicar donde el parámetro amt esté incluído, null en
	 *         caso que el parámetro de configuración sea null o 0, el monto de
	 *         evaluación sea null o no se haya encontrado rango para ese monto
	 */
	public static Object getRangeApplyValue(Integer retSchemaConfigID, BigDecimal amt, String trxName){
		if (Util.isEmpty(retSchemaConfigID, true) || amt == null)
			return null;
		Object valueObj = DB
				.getSQLObject(
						trxName,
						"SELECT value_apply FROM c_retschema_range WHERE c_retschema_config_id = ? AND ? BETWEEN value_from AND value_to ORDER BY value_from",
						new Object[] { retSchemaConfigID, amt });
		return valueObj;
	}
	
	/**
	 * Constructor de la clase
	 * @param ctx Contexto
	 * @param C_RetSchema_Config_ID ID del objeto a cargar
	 * @param trxName Nombre de la transacción
	 */
	public MRetSchemaConfig(Properties ctx, int C_RetSchema_Config_ID, String trxName) {
		super(ctx, C_RetSchema_Config_ID, trxName);		
	}
	
	/**
	 * Constructor de la clase
	 * @param ctx Contexto
	 * @param rs ResultSet con los datos
	 * @param trxName Nombre de la transacción
	 */
	public MRetSchemaConfig(Properties ctx, ResultSet rs, String trxName) {
		super (ctx, rs, trxName);
	}

	protected boolean beforeSave(boolean newRecord){
		// Si es rango, se anula el valor del campo Valor (utilizado para parámetros
		// que solo tienen un valor.
		if(getParamType().equals(PARAMTYPE_Rango)) {
			this.setValor("");
			this.setIs_Range(true);
		} 		
		return true;		
	}

	/**
	 * @return El Valor del parámetro/configuración convertido a <code>BigDecimal</code>.
	 * Si el parámetro es de tipo Rango, entonces retorna <code>null</code>.
	 */
	public BigDecimal getValorBigDecimal() {
		BigDecimal value = null;
		if (getParamType().equals(PARAMTYPE_Valor))
			value = new BigDecimal(getValor().replaceAll(",", "."));
		return value;
	}

	/**
	 * @return El Valor del parámetro/configuración convertido a <code>String</code>.
	 * Si el parámetro es de tipo Rango, entonces retorna <code>null</code>.
	 */
	public String getValorString() {
		String value = null;
		if (getParamType().equals(PARAMTYPE_Valor))
			value = getValor();
		return value;
	}
	
	/**
	 * Carga todos los rangos asociados a este parámetro/configuración y retorna
	 * una lista con todos los rangos asociados. 
	 * @param reload Inidica si se deben releer los datos de la BD.
	 * @return Lista con los rangos. En caso de no existir rangos la lista
	 * estará vacía (<code>isEmpty() == true</code>).
	 */
	public List<X_C_RetSchema_Range> getRanges(boolean reload) {
		if (ranges == null || reload) {
			ranges = new ArrayList<X_C_RetSchema_Range>();
			// Consulta que obtiene todos los rangos para este parámetro/configuración.
			String sql =  
				" SELECT * " +
				" FROM C_RetSchema_Range " + 
				" WHERE C_RetSchema_Config_ID = ? ";
			
			try {
				// Se ejecuta la consulta.
				PreparedStatement pstmt = DB.prepareStatement(sql);
				pstmt.setInt(1, getC_RetSchema_Config_ID());
				ResultSet rs = pstmt.executeQuery();
				// Se carga cada rango en la lista.
				while (rs.next()) {
					X_C_RetSchema_Range retSchemaRange = 
						new X_C_RetSchema_Range(getCtx(), rs, get_TrxName());
					ranges.add(retSchemaRange);
				}
				
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch (Exception ex) {
				log.warning(" MRetSchemaConfig - Error: Loading Config Ranges. " + ex);
				ex.printStackTrace();
			}
		}
		
		return ranges;
	}

	/**
	 * Carga todos los rangos asociados a este parámetro/configuración y retorna
	 * una lista con todos los rangos asociados. Los objetos quedan cacheados con lo cual
	 * si se quiere recargar los rangos desde la BD se debe invocar el método
	 * <code>getRanges(true)</code> 
	 * @return Lista con los rangos. En caso de no existir rangos la lista
	 * estará vacía (<code>isEmpty() == true</code>).
	 */
	public List<X_C_RetSchema_Range> getRanges() {
		return getRanges(false);
	}
	
	public BigDecimal percentRetencion(BigDecimal amt) {
		BigDecimal retValue = Env.ZERO;
		if(amt.compareTo(Env.ZERO)!= 0) {
			// busco cual es el porcentaje en config 
			// o en range segun corresponda
			this.monto = amt;
			if(this.is_Range()){				
				retValue = this.getPercentRange(this.monto);			
				
			} else {
				retValue = new BigDecimal(this.getValor());
			} 
		}
		return retValue;
		
	}

	private BigDecimal getPercentRange(BigDecimal amt) {
		BigDecimal retValue = Env.ZERO;
		String sql;
		
		sql =  " SELECT Value_Apply ";
		sql += " FROM C_RetSchema_Range CRSR ";
		sql += " WHERE C_RetSchema_Config_ID = ? ";
		sql += "       AND ? BETWEEN Value_From AND Value_To ";
		
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1,this.getC_RetSchema_Config_ID());
			pstmt.setBigDecimal(2,amt);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()){
				retValue = rs.getBigDecimal("Value_Apply");
			}
			if(pstmt != null)
				pstmt.close();
			if(rs != null)
				rs.close();
		}
		catch (Exception ex)
		{
			log.info(" MRetSchemaConfig - Error:  buscar el rango de la configuracion del esquema " + ex);
			ex.printStackTrace();
		}
		
		return retValue;		
	}

	public BigDecimal minimoRetencion() {
		BigDecimal retValue = Env.ZERO;
		if (this.getParamType().equals(PARAMTYPE_Rango)) {
			retValue = this.getPercentRange(Env.ZERO);
		} 
		return retValue;
		
	}
}
