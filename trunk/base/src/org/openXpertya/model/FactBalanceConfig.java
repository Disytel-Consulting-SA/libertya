package org.openXpertya.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class FactBalanceConfig {

	/** Configuración de columnas para crear el balance contable por esquema contable */
    private static Map<Integer, List<X_Fact_Acct_Balance_Config>> factacctbalance_balanceConfig;
    
    /** Columnas de la configuración del balance contable */
    private static Map<Integer, List<M_Column>> factacctbalance_columns;
    
    /** Columnas a actualizar */
    private static List<String> factacctbalance_updatecolumns;
    
    /** SQL Update template para las columnas a actualizar */
    private static Map<Integer, String> factacctbalance_templateUpdate;
    
    /** SQL Insert template para las columnas a actualizar */
    private static Map<Integer, String> factacctbalance_templateInsert;
    
	/**
	 * Condición SQL EXISTS que se utiliza en el insert y update siempre que no
	 * se deba eliminar los datos de la tabla
	 */
    private static Map<Integer, String> factacctbalance_templateExists;
    
    /**
	 * Group By SQL que se utiliza en el insert 
	 */
    private static Map<Integer, String> factacctbalance_templateGroup;
    
    /** String para actualizar la columna update sql */
    private static String FACTACCTBALANCE_UPDATE_COLUMN_REPLACE_STRING = "<updatecolum>";
    
    
    /**
	 * Inicializa las estructuras y sqls a ejecutar en el proceso
	 * 
	 * @param ctx
	 *            contexto
	 * @param acctSchemaID
	 *            id de esquema contable actual
	 * @param trxName
	 *            nombre de trx
	 */
    public static void initialize(Properties ctx){
		ctx = ctx != null ? ctx : Env.getCtx();
    	// Cargar la configuración del balance contable
        loadBalanceConfig(ctx);
    	// Carga el update completo
    	loadMassiveSQLs();
    }
    
    /**
     * Carga las columnas configuradas para el balance
     */
    private static void loadBalanceConfig(Properties ctx){
    	if(factacctbalance_balanceConfig == null){
			List<PO> configs = PO.find(ctx, X_Fact_Acct_Balance_Config.Table_Name, null, null,
					new String[] { "C_AcctSchema_ID", "SeqNo" }, null);
    		setFactacctbalance_balanceConfig(new HashMap<Integer, List<X_Fact_Acct_Balance_Config>>());
    		X_Fact_Acct_Balance_Config bc;
    		List<X_Fact_Acct_Balance_Config> theConfigs;
    		for (PO c : configs) {
    			bc = (X_Fact_Acct_Balance_Config)c;
    			theConfigs = getFactacctbalance_balanceConfig().get(bc.getC_AcctSchema_ID());
    			if(theConfigs == null){
    				theConfigs = new ArrayList<X_Fact_Acct_Balance_Config>();
    			}
    			theConfigs.add(bc);
    			getFactacctbalance_balanceConfig().put(bc.getC_AcctSchema_ID(), theConfigs);
    		}
    	}
		
		// Carga conjunto de columnas a agrupar
		loadBalanceConfigColumns(ctx);
    }
    
    /**
     * Carga las columnas configuradas, sino deja las default
     */
    private static void loadBalanceConfigColumns(Properties ctx){
    	if(factacctbalance_columns == null){
	    	setFactacctbalance_columns(new HashMap<Integer, List<M_Column>>());
	    	// Se cargan las columnas de la config, sino las default
	    	if(!Util.isEmpty(getFactacctbalance_balanceConfig().keySet())){
	    		List<M_Column> theColumns;
	    		M_Column col;
	    		for (Integer acctSchemaID : getFactacctbalance_balanceConfig().keySet()) {
	    			for (X_Fact_Acct_Balance_Config config : getFactacctbalance_balanceConfig().get(acctSchemaID)) {
						col = M_Column.get(ctx, config.getAD_Column_ID());
						theColumns = getFactacctbalance_columns().get(acctSchemaID);
						if(theColumns == null){
							theColumns = new ArrayList<M_Column>();
						}
						theColumns.add(col);
						getFactacctbalance_columns().put(acctSchemaID, theColumns);
					}
				}	    		
	    	}
    	}
    	
    	// Cargar el conjunto de columnas a actualizar
    	loadBalanceConfigUpdateColumns();
    }
    
    /**
     * Columnas numéricas a actualizar en Fact_Acct_Balance
     */
    private static void loadBalanceConfigUpdateColumns(){
    	setFactacctbalance_updatecolumns(new ArrayList<String>());
    	getFactacctbalance_updatecolumns().add("Qty");
    	getFactacctbalance_updatecolumns().add("AmtAcctDr");
    	getFactacctbalance_updatecolumns().add("AmtAcctCr");
    }

    /**
	 * Inicialización de los sql de actualización masiva del balance contable 
	 */
    private static void loadMassiveSQLs(){
    	// Iterar por las columnas de la configuración y actualizar el balance
    	// --------------------------------------------------------------------
    	// El update es por columna a actualizar, 
    	// la causa: no atorar el procesador ni las conexiones.
		// Por lo tanto, al actualizar se deben ejecutar tantos updates como
		// columnas haya que actualizar.
    	// 
    	// UPDATE Fact_Acct_Balance 
    	// SET <columna1> = (SELECT SUM(<columna1>)
    	// 					FROM Fac_Acct 
    	// 					WHERE columnasdeagrupacion),
    	// 		....,
    	//		<columnaN> = (SELECT SUM(<columnaN>)
    	// 					FROM Fac_Acct 
    	// 					WHERE columnasdeagrupacion),
    	// WHERE condiciones 
    	// --------------------------------------------------------------------
		// El insert es masivo ya que el SELECT del INSERT se ejecuta una vez y
		// no tiene sentido que se divida por columna a actualizar.
    	// 
    	// INSERT INTO Fact_Acct_Balance (columnas)
    	// SELECT columnasdeagrupacion, sum(columna1), ..., sum(columnaN)
    	// FROM Fact_Acct
    	// WHERE condiciones 
    	//			AND NOT EXISTS mismas condiciones
    	// --------------------------------------------------------------------
		if (factacctbalance_templateUpdate == null 
				|| factacctbalance_templateExists == null
				|| factacctbalance_templateInsert == null) {
    		setFactacctbalance_templateUpdate(new HashMap<Integer, String>());
    		setFactacctbalance_templateExists(new HashMap<Integer, String>());
    		setFactacctbalance_templateInsert(new HashMap<Integer, String>());
    		setFactacctbalance_templateGroup(new HashMap<Integer, String>());
    		for (Integer acctSchemaID : getFactacctbalance_columns().keySet()) {
		    	String selectWhere = " WHERE Fact_Acct.C_AcctSchema_ID=Fact_Acct_Balance.C_AcctSchema_ID";
		    	String exists = " EXISTS (SELECT Fact_Acct.fact_acct_id FROM Fact_Acct ";
		    	String group = " AD_Client_ID, C_AcctSchema_ID ";
		    	String groupBy = " AD_Client_ID, C_AcctSchema_ID ";
		    	String sqlUpdate = " UPDATE Fact_Acct_Balance " +  
		        		" SET "+FACTACCTBALANCE_UPDATE_COLUMN_REPLACE_STRING+" = " + 
		        		" (SELECT COALESCE(SUM("+FACTACCTBALANCE_UPDATE_COLUMN_REPLACE_STRING+"),0) " + 
		        		" FROM Fact_Acct ";
		    	String condition;
		    	for (M_Column column : getFactacctbalance_columns().get(acctSchemaID)) {
		    		// Where
		    		condition = " AND ";
					if(DisplayType.isDate(column.getAD_Reference_ID())){
						condition += " date_trunc('day', Fact_Acct."+column.getColumnName()+") = date_trunc('day', Fact_Acct_Balance."+column.getColumnName()+")";
						groupBy += " , date_trunc('day', "+column.getColumnName()+")";
					}
					else if(DisplayType.isNumeric(column.getAD_Reference_ID())){
						condition += " COALESCE(Fact_Acct."+column.getColumnName()+", 0) = COALESCE(Fact_Acct_Balance."+column.getColumnName()+", 0)";
						groupBy += " , coalesce("+column.getColumnName()+",0)";
					}
					else{
						condition += "Fact_Acct."+column.getColumnName()+" = Fact_Acct_Balance."+column.getColumnName();
						groupBy += " , "+column.getColumnName();
					}
					selectWhere += condition;
					group += " , "+column.getColumnName();
				}
		    	sqlUpdate += " "+selectWhere+" ) ";
		    	
		    	getFactacctbalance_templateExists().put(acctSchemaID, " AND NOT "+exists+selectWhere+")");
		    	getFactacctbalance_templateUpdate().put(acctSchemaID,sqlUpdate);		    	
		    	
		    	String sqlInsert = " INSERT INTO Fact_Acct_Balance ( ";
		    	String sqlInsertSelect = " SELECT "+groupBy;
		    	for (String updateColumn : getFactacctbalance_updatecolumns()) {
		    		sqlInsertSelect += " , sum("+updateColumn+")";
					group += " , "+updateColumn;
				}
		    	
		    	sqlInsert += group;
		    	sqlInsert += " ) ";
		    	sqlInsert += sqlInsertSelect;
		    	sqlInsert += " FROM Fact_Acct ";
		    	//sqlInsert += getFactacctbalance_templateExists().get(acctSchemaID);
		    	//sqlInsert += groupBy;
		    	
		    	getFactacctbalance_templateInsert().put(acctSchemaID, sqlInsert);
		    	getFactacctbalance_templateGroup().put(acctSchemaID, groupBy);
    		}
    	}
    }

    /**
	 * Eliminación de registros del balance contable
	 * 
	 * @param balanceVO
	 *            datos propios de esta actualización de balance
	 * @return mensaje del resultado
	 */
    public static String deleteBalance(FactBalanceConfigVO balanceVO) {
        String sql = new String("DELETE FROM Fact_Acct_Balance ");
        sql += balanceVO.getWhereClause();
        int no = DB.executeUpdate(sql, true, balanceVO.trxName, true);
        
    	// Marcar a los registros de fact_acct que ya fueron balanceados
        markFactAcctBalanced(balanceVO, false);
        
        return "@Deleted@ = " + no+".";
    }
    
    /**
	 * Actualiza los registros existentes del balance contable mediante el
	 * template sql, el esquema contable actual y las columnas a actualizar
	 * 
	 * @param balanceVO
	 *            datos propios de esta actualización de balance
	 * @return mensaje del resultado
	 */
    public static String doUpdate(FactBalanceConfigVO balanceVO){
    	String msg = "";
    	String admsg = "@Updated@ @AD_Column_ID@ ";
    	for (String column : getFactacctbalance_updatecolumns()) {
    		if(getFactacctbalance_templateUpdate().get(balanceVO.acctSchemaID) != null){
    			String sql = getFactacctbalance_templateUpdate().get(balanceVO.acctSchemaID)
						.replace(FACTACCTBALANCE_UPDATE_COLUMN_REPLACE_STRING, column);
    			sql += balanceVO.getWhereClause();
    			
				int no = DB.executeUpdate(sql, true, balanceVO.trxName, true);
        		msg += admsg + "@"+column+"@ = "+no+".";
    		}
    		else{
    			msg += admsg + "Esquema contable sin configuración de balance.";
    		}
		}
    	return Msg.parseTranslation(balanceVO.ctx, msg);
    }
    
    /**
	 * Realiza el insert de los registros faltantes en el balance contable
	 * 
	 * @param balanceVO
	 *            datos propios de esta actualización de balance
	 * @returns
	 */
    public static String doInsert(FactBalanceConfigVO balanceVO){
    	String sql = getFactacctbalance_templateInsert().get(balanceVO.acctSchemaID);
    	int no = 0;
    	String msg = "@Inserted@ = ";
    	if(!Util.isEmpty(sql, true)){
    		// Insertar en fact_balance
    		String where = balanceVO.getWhereClause();
        	sql += where;
			sql += " GROUP BY "+getFactacctbalance_templateGroup().get(balanceVO.acctSchemaID);
        	no = DB.executeUpdate(sql, true, balanceVO.trxName, true);
        	
        	// Marcar a los registros de fact_acct que ya fueron balanceados
        	markFactAcctBalanced(balanceVO, true);
    	}
    	
    	msg += no+".";
    	return Msg.parseTranslation(balanceVO.ctx, msg);
    }	
    
    /**
	 * Marca de los registros de fact_acct balanceados o no
	 * 
	 * @param balanceVO
	 *            datos de la actualización actual
	 * @param balanced
	 *            true si se debe marcar, false caso contrario
	 * @return cantidad de registros actualizados
	 */
    public static int markFactAcctBalanced(FactBalanceConfigVO balanceVO, boolean balanced){
    	// Marcar a los registros de fact_acct que ya fueron balanceados  
    	String sql = " UPDATE Fact_Acct SET isfactalreadybalanced = '"+(balanced?"Y":"N")+"' ";
    	sql += balanceVO.getWhereClause();
    	return DB.executeUpdate(sql, true, balanceVO.trxName, true);
    }
    
	public static Map<Integer, List<X_Fact_Acct_Balance_Config>> getFactacctbalance_balanceConfig() {
		if(factacctbalance_balanceConfig == null){
			initialize(null);
		}
		return factacctbalance_balanceConfig;
	}

	public static void setFactacctbalance_balanceConfig(Map<Integer, List<X_Fact_Acct_Balance_Config>> factacctbalance_balanceConfig) {
		FactBalanceConfig.factacctbalance_balanceConfig = factacctbalance_balanceConfig;
	}

	public static Map<Integer, List<M_Column>> getFactacctbalance_columns() {
		if(factacctbalance_columns == null){
			initialize(null);
		}
		return factacctbalance_columns;
	}

	public static void setFactacctbalance_columns(Map<Integer, List<M_Column>> factacctbalance_columns) {
		FactBalanceConfig.factacctbalance_columns = factacctbalance_columns;
	}

	public static List<String> getFactacctbalance_updatecolumns() {
		if(factacctbalance_updatecolumns == null){
			initialize(null);
		}
		return factacctbalance_updatecolumns;
	}

	public static void setFactacctbalance_updatecolumns(List<String> factacctbalance_updatecolumns) {
		FactBalanceConfig.factacctbalance_updatecolumns = factacctbalance_updatecolumns;
	}

	public static Map<Integer, String> getFactacctbalance_templateUpdate() {
		if(factacctbalance_templateUpdate == null){
			initialize(null);
		}
		return factacctbalance_templateUpdate;
	}

	public static void setFactacctbalance_templateUpdate(Map<Integer, String> factacctbalance_templateUpdate) {
		FactBalanceConfig.factacctbalance_templateUpdate = factacctbalance_templateUpdate;
	}

	public static Map<Integer, String> getFactacctbalance_templateInsert() {
		if(factacctbalance_templateInsert == null){
			initialize(null);
		}
		return factacctbalance_templateInsert;
	}

	public static void setFactacctbalance_templateInsert(Map<Integer, String> factacctbalance_templateInsert) {
		FactBalanceConfig.factacctbalance_templateInsert = factacctbalance_templateInsert;
	}

	public static Map<Integer, String> getFactacctbalance_templateExists() {
		if(factacctbalance_templateExists == null){
			initialize(null);
		}
		return factacctbalance_templateExists;
	}

	public static void setFactacctbalance_templateExists(Map<Integer, String> factacctbalance_templateExists) {
		FactBalanceConfig.factacctbalance_templateExists = factacctbalance_templateExists;
	}
	
	public static Map<Integer, String> getFactacctbalance_templateGroup() {
		if(factacctbalance_templateGroup == null){
			initialize(null);
		}
		return factacctbalance_templateGroup;
	}

	public static void setFactacctbalance_templateGroup(Map<Integer, String> factacctbalance_templateGroup) {
		FactBalanceConfig.factacctbalance_templateGroup = factacctbalance_templateGroup;
	}
}
