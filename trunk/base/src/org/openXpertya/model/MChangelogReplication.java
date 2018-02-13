package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;

/**
* Genera la entrada en la bitacora de replicación
* -----------------------------------------------------------------
* IMPORTANTE!
* -----------------------------------------------------------------
* ACTUALMENTE SIN USO: LAS ENTRADAS SE GENERAN A PARTIR DE TRIGGERS
* LOS CUALES SON CREADOS DESDE LA VENTANA DE REPLICACION POR TABLA
* -----------------------------------------------------------------
*/


public class MChangelogReplication extends X_AD_Changelog_Replication {
	
	/** Constantes para la generacion del XML */
	public static final String XML_DOBLE_QUOTE = "\"";
	public static final String XML_COLUMN_TAG = "column";
	public static final String XML_COL_ID_ATT = "id";
	public static final String XML_VALUE_ATT = "value";
	public static final String XML_NULL_ATT = "null";
	public static final String XML_BINARY_ATT = "binary";
	public static final String XML_NULL_Y_ATT = XML_NULL_ATT+"="+XML_DOBLE_QUOTE+"Y"+XML_DOBLE_QUOTE;
	public static final String XML_HEADER_TAG = "<?xml version="+XML_DOBLE_QUOTE+"1.0"+XML_DOBLE_QUOTE+" encoding="+XML_DOBLE_QUOTE+"UTF-8"+XML_DOBLE_QUOTE+"?>";
	public static final String XML_OPEN_TAG = "<columns>";
	public static final String XML_CLOSE_TAG = "</columns>";
	
	
//    /** Esquema de tablas usadas para replicacion */
//    // public static final String REPLICATION_SCHEMA_NAME = "Replicacion"; 
//    
//    /** Lista de tablas de log para replicación */
//    private static List<Integer> logReplicationTables = new ArrayList<Integer>();
//    
//    /** Logger */
//    private static CLogger	s_log	= CLogger.getCLogger(MChangeLog.class);
//
//    /** Change Log */
//    private static int[]	s_changeLog	= null;
    
	public MChangelogReplication(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MChangelogReplication(Properties ctx,
			int AD_Changelog_Replication_ID, String trxName) {
		super(ctx, AD_Changelog_Replication_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
//	/**
//	 * Constructor que recibe el String xml y el String operandType
//	 */
//	public MChangelogReplication(Properties ctx, String trxName, int tableID, String recordUID, HashMap<Integer, Object> map, String operandType)
//	{
//		super(ctx, 0, trxName);
//		setOperationType(operandType);
//		setAD_Table_ID(tableID);
//		setRecordUID(recordUID);
//		setReplicationArray(MTableReplication.getReplicationArray(tableID, trxName));
//		
//		// Si map esta en null (operacion de borrado), directamente cargar "" en value 
//		setColumnValues(map!=null?processMap(map):"");
//	}
//	
//	/**
//	 * Genera el XML a persistir en la tabla de logging para replicación
//	 * a partir del conjunto de datos recibido por parametro
//	 */
//	protected String processMap(HashMap<Integer, Object> map)
//	{
//		// Valor a retornar
//		StringBuffer retValue = new StringBuffer();
//		
//		// Especificacion XML
//		retValue.append(XML_HEADER_TAG);
//		
//		// Agrupador de apertura
//		retValue.append(XML_OPEN_TAG);
//		
//		// Iterar por cada columna a persistir
//		Iterator<Integer> it = map.keySet().iterator();
//		Integer tempCol = -1;
//		Object tempVal = "";
//		boolean isNull = false;
//		while (it.hasNext())
//		{
//			tempCol = it.next();
//			tempVal = map.get(tempCol);
//			isNull = (tempVal == null);
//			
//			// Nombre de columna
//			retValue.append("<").append(XML_COLUMN_TAG).append(" ").append(XML_COL_ID_ATT).append("=").append(XML_DOBLE_QUOTE).append(tempCol).append(XML_DOBLE_QUOTE);
//			
//			// Valor de columna
//			retValue.append(" ").append(XML_VALUE_ATT).append("=").append(XML_DOBLE_QUOTE).append(isNull?"":tempVal).append(XML_DOBLE_QUOTE);
//			
//			// Indicar si es valor nulo
//			retValue.append(" ").append(isNull?XML_NULL_Y_ATT:"");
//			
//			// Cierre de columna
//			retValue.append("/>");
//		}
//				
//		// Agrupador de apertura
//		retValue.append(XML_CLOSE_TAG);
//		
//		return retValue.toString();
//	}
//	
//	
//    /**
//     * Replicacion: determina si la tabla en cuestion requiere se replicada
//     * @param AD_Table_ID tabla a analizar
//     * @return true si la tabla debe ser replicada o false en cc
//     */
//    public static boolean isReplicationLogged(int AD_Table_ID) {
//      
//  	if (logReplicationTables == null || logReplicationTables.size() == 0) {
//  		fillReplicationChangeLog();
//  	}
//  	
//  	return logReplicationTables.contains(AD_Table_ID);
//  }		// trackChanges
//	
//	
//	
//    /**
//     * Rellena la nomina de tablas a loggear para replicación
//     */
//    public static void fillReplicationChangeLog() {
//
//        ArrayList<Integer>	list	= new ArrayList<Integer>();
//        String		sql	= " SELECT tsl.ad_table_id FROM ad_tableschemaline tsl INNER JOIN ad_tableschema ts ON ts.ad_tableschema_id = tsl.ad_tableschema_id WHERE ts.name = ?";
//        PreparedStatement	pstmt	= null;
//
//        try {
//
//            pstmt	= DB.prepareStatement(sql);
//            pstmt.setString(1, REPLICATION_SCHEMA_NAME);
//            ResultSet	rs	= pstmt.executeQuery();
//
//            while (rs.next()) {
//                list.add(new Integer(rs.getInt(1)));
//            }
//
//            rs.close();
//            pstmt.close();
//            pstmt	= null;
//
//        } catch (Exception e) {
//            s_log.log(Level.SEVERE, sql, e);
//        }
//
//        try {
//            if (pstmt != null) {
//                pstmt.close();
//            }
//            pstmt	= null;
//        } catch (Exception e) {
//            pstmt	= null;
//        }
//        
//        logReplicationTables = list;
//        // Convert to Array
//        s_changeLog	= new int[list.size()];
//
//        for (int i = 0; i < s_changeLog.length; i++) {
//            Integer	id	= (Integer) list.get(i);
//            s_changeLog[i]	= id.intValue();
//        }
//    }		// fillChangeLog    

}
