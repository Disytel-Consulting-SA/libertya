package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MRetencionSchema extends X_C_RetencionSchema {

	private Map<String,Object> parameters;
	
	/**
	 * Constructor 
	 */
	public MRetencionSchema(Properties ctx, int C_RetencionSchema_ID, String trxName) {
		super(ctx, C_RetencionSchema_ID, trxName);
	}
	
	/**
	 * Constructor 
	 */
	public MRetencionSchema (Properties ctx, ResultSet rs, String trxName) {
		super (ctx, rs, trxName);
	}
	
	/**
	 * Get schemas of client
	 * @param ctx context
	 * @param trxName trx name
	 * @return list of retencion schemas 
	 */
	public static List<MRetencionSchema> getOfClient(Properties ctx,String trxName){
		//script sql
    	String sql = "SELECT * FROM c_retencionschema WHERE ad_client_id = ? "; 
    		
    	List<MRetencionSchema> list = new ArrayList<MRetencionSchema>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MRetencionSchema(ctx,rs,trxName));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				if(rs != null){
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	/**
	 * @return Retorna el nombre de la clase del procesador asociado al esquema de retención.
	 */
	public String getProcessorClass(){
		X_C_RetencionProcessor process = new X_C_RetencionProcessor(getCtx(),getC_RetencionProcessor_ID(),get_TrxName());
		return process.getnameclass();
	}
	
	/**
	 * Carga los <code>MRetSchemaConfig</code> relacionados con este esquema de
	 * retención.
	 */
	public void loadParameters() {
		parameters = new HashMap<String,Object>();
		PreparedStatement pstmt = null;			
		ResultSet rs = null;
		List<MRetSchemaConfig> padronesConfigs = new ArrayList<MRetSchemaConfig>();
		// Consulta para obtener todos los parámetros del esquema.
		String sql =
				" SELECT * " +
				" FROM C_RetSchema_Config " +
				" WHERE C_RetencionSchema_ID = ? " +
				" ORDER BY orden ";
		
		try {
			// Se ejecuta la consulta.
			pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1,this.getC_RetencionSchema_ID());			
			rs = pstmt.executeQuery();
			
			while (rs.next()){
				// Se carga cada configuración del esquema (Parámetros) 
				MRetSchemaConfig retSchemaConfig = new MRetSchemaConfig(getCtx(), rs, get_TrxName());
				// Para la configuración de padrón puede haber más de un valor
				if(MRetSchemaConfig.NAME_DesdePadron.equals(retSchemaConfig.getName())){
					padronesConfigs = (List<MRetSchemaConfig>) parameters
							.get(MRetSchemaConfig.NAME_DesdePadron);
					if(padronesConfigs == null){
						padronesConfigs = new ArrayList<MRetSchemaConfig>();
					}
					padronesConfigs.add(retSchemaConfig);
					parameters.put(MRetSchemaConfig.NAME_DesdePadron, padronesConfigs);
				}
				else{
					parameters.put(retSchemaConfig.getName(), retSchemaConfig);
				}
			}

		} catch (Exception ex) {
			log.warning("Error: Loading Retencion Schema Parameters. " + ex);
			ex.printStackTrace();
		} finally{
			try{
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return Retorna el Map de parámetros/configuraciones del esquema.
	 */
	public Map<String,Object> getParameters() {
		if (parameters == null)
			loadParameters();
		return parameters;
	}
	
	/**
	 * Retorna una configuración del esquema con determinado nombre (Parámetro).
	 * Si no existe dicha configuración se retorna <code>null</code>
	 * @param paramName Nombre del parámetro/configuración. (name del <code>MRetSchemaConfig</code>)
	 * @return la instancia de <code>MRetSchemaConfig</code> o <code>null</code> en caso
	 * de que no exista.
	 */
	public MRetSchemaConfig getParameter(String paramName) {
		return (MRetSchemaConfig)getParameters().get(paramName);
	}
	
	public BigDecimal getPercentRetencion(BigDecimal amt){
		
		/*  Devuelve el porcentaje asociado a la retencion segun corresponda por el monto a aplicar (amt) */
		
		BigDecimal retValue = Env.ZERO;
		Integer vConfig = 0;
		
		String sql; 
		sql =  " SELECT C_RetSchema_Config_ID ";
		sql += " FROM C_RetSchema_Config ";
		sql += " WHERE C_RetencionSchema_ID = ? ";
		
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1,this.getC_RetencionSchema_ID());			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()){
				vConfig = rs.getInt("C_RetSchema_Config_ID");
			}
			if(pstmt != null)
				pstmt.close();
			if(rs != null)
				rs.close();
		}
		catch (Exception ex)
		{
			log.info("Error: MRetencionSchema - buscar la configuracion del esquema fallado " + ex);
			ex.printStackTrace();
		}
				
		if(vConfig != 0) {
			MRetSchemaConfig schemaConfig = new MRetSchemaConfig(getCtx(),vConfig,get_TrxName());
			retValue = schemaConfig.percentRetencion(amt);
		}
		return retValue;
	} 
	
	public BigDecimal getMinimoImponible() {
		// busca el minimo imponible para una retención
		return this.getBaseAmt();
	}

		/*  Devuelve el porcentaje asociado a la retencion segun corresponda por el monto a aplicar (amt) */
	public BigDecimal getBaseAmt() {
		
		BigDecimal retValue = Env.ZERO;
		Integer vConfig = 0;
		
		String sql; 
		sql =  " SELECT C_RetSchema_Config_ID ";
		sql += " FROM C_RetSchema_Config ";
		sql += " WHERE C_RetencionSchema_ID = ? ";
		
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1,this.getC_RetencionSchema_ID());			
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()){
				vConfig = rs.getInt("C_RetSchema_Config_ID");
			}
			if(pstmt != null)
				pstmt.close();
			if(rs != null)
				rs.close();
		}
		catch (Exception ex)
		{
			log.info("Error: MRetencionSchema - buscar la configuracion del esquema fallado " + ex);
			ex.printStackTrace();
		}
				
		if(vConfig != 0) {
			MRetSchemaConfig schemaConfig = new MRetSchemaConfig(getCtx(),vConfig,get_TrxName());
			retValue = schemaConfig.minimoRetencion();
		} else {
			// si no tiene m�nimo imponible, devuelvo cero
			return Env.ZERO;
		}
		
		return retValue;
	
	} // getBaseAmt
	
	/**
	 * Retorna el id del producto asociado con el tipo de retención del esquema corriente
	 */
	public int getProduct(){
		/*
		 * Consulta para obtener el producto del tipo de retención
		 * -------------------------------------------------------
		 * SELECT m_product_id
		 * FROM c_retenciontype
		 * WHERE c_retenciontype_id = ?
		 * -------------------------------------------------------
		 */
		
		String sql = "SELECT m_product_id FROM c_retenciontype WHERE c_retenciontype_id = ?";
		return DB.getSQLValue(get_TrxName(), sql, getC_RetencionType_ID());		
	}

	/**
	 * Indica si el esquema corresponde a una retención sufrida o no.
	 */
	public boolean isSufferedRetencion() {
		return getRetencionApplication().
					equals(MRetencionSchema.RETENCIONAPPLICATION_SufferedRetencion); 
	}

	/**
	 * @return Retorna el ID del tipo de documento para crear el crédito por el monto
	 * de retención. Busca el tipo de documento configurado en el esquema, luego el
	 * tipo por defecto de la compañía, y finalmente si no existen ninguno de los dos
	 * retorna 0.
	 */
	public int getRetencionCreditDocType() {
		int docTypeID = 0; 
		// 1. Tipo de documento configurado en el esquema.
		if (getC_DocType_Credit_ID() > 0)
			docTypeID = getC_DocType_Credit_ID();
		// 2. Tipo de documento Comprobante de Retención de la Compañía.
		else {
			String docTypeKey;
			if (isSufferedRetencion())
				docTypeKey = MDocType.DOCTYPE_Retencion_ReceiptCustomer;
			else
				docTypeKey = MDocType.DOCTYPE_Retencion_Receipt;
			
			MDocType retencDocType = 
				MDocType.getDocType(Env.getCtx(), docTypeKey, get_TrxName());
			if (retencDocType != null)
				docTypeID = retencDocType.getC_DocType_ID();
			
		}
		// 3. Retorna 0 en caso de que no existan 1 y 2.
		return docTypeID;
	}

	/**
	 * @return Retorna el ID del tipo de documento para crear la factura de recaudador
	 * por el monto de la retención. Busca el tipo de documento configurado en el 
	 * esquema, luego el tipo por defecto de la compañía, y finalmente si no 
	 * existen ninguno de los dos retorna 0.
	 */
	public int getCollectorInvoiceDocType() {
		int docTypeID = 0; 
		// 1. Tipo de documento configurado en el esquema.
		if (getC_DocType_Invoice_ID() > 0)
			docTypeID = getC_DocType_Invoice_ID();
		// 2. Tipo de documento Factura de Retención de la Compañía.
		else {
			String docTypeKey;
			if (isSufferedRetencion())
				docTypeKey = MDocType.DOCTYPE_Retencion_InvoiceCustomer;
			else
				docTypeKey = MDocType.DOCTYPE_Retencion_Invoice;
			
			MDocType retencDocType = 
				MDocType.getDocType(Env.getCtx(), docTypeKey, get_TrxName());
			
			if (retencDocType != null)
				docTypeID = retencDocType.getC_DocType_ID();
			
		}
		// 3. Retorna 0 en caso de que no existan 1 y 2.
		return docTypeID;
	}

	public List<MRetSchemaConfig> getPadronesList(){
		return (List<MRetSchemaConfig>) getParameters().get(
				MRetSchemaConfig.NAME_DesdePadron);
	}
	
	protected boolean beforeSave(boolean newRecord){
		if (getParameters().get(MRetSchemaConfig.NAME_PorRegionOrigenYDestino) != null
				&& getParameter(MRetSchemaConfig.NAME_PorRegionOrigenYDestino).getValor().equals("Y")
				&& getC_Region_ID() == 0){
			log.saveError(Msg.getMsg(getCtx(), "RetentionSchemaWithOutRegion"),"");
			return false;
		}
		return true;
	}
} // MRetencionSchema
