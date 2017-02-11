/*
 * @(#)MChangeLog.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;

/**
 *      Change Log Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MChangeLog.java,v 1.16 2005/05/14 05:32:16 jjanke Exp $
 */
public class MChangeLog extends X_AD_ChangeLog {

    /** Change Log */
    private static int[]	s_changeLog	= null;
    
    /** Lista de tablas de log */
    
    private static List<Integer> logTables = new ArrayList<Integer>();;

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MChangeLog.class);

    /** NULL Value */
    public static String	NULL	= "NULL";
    
    /** Insert/Update = X.  Caso especial donde debe intentarse la insercion de no existir el registro, o la actualizacion, en caso de que sí exista */
    public static final String OPERATIONTYPE_InsertionModification = "X";
    
    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MChangeLog(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MChangeLog

    /**
     *      Full Constructor
     *      @param ctx context
     *      @param AD_ChangeLog_ID 0 for new change log
     * @param TrxName
     *      @param AD_Session_ID session
     *      @param AD_Table_ID table
     *      @param AD_Column_ID column
     *      @param Record_ID record
     *      @param AD_Client_ID client
     *      @param AD_Org_ID org
     *      @param OldValue old
     *      @param NewValue new
     */
    public MChangeLog(Properties ctx, int AD_ChangeLog_ID, String TrxName, int AD_Session_ID, int AD_Table_ID, int AD_Column_ID, int Record_ID, int AD_Client_ID, int AD_Org_ID, Object OldValue, Object NewValue, String valueUID, Integer valueComponentVersionID, String operationType, Integer displayType, Integer changeLogGroupID) {

        super(ctx, 0, TrxName);	// out of trx

//        if (AD_ChangeLog_ID == 0) {
//
//            AD_ChangeLog_ID	= DB.getNextID(AD_Client_ID, Table_Name, null);
//
//            if (AD_ChangeLog_ID <= 0) {
//                log.severe("No NextID (" + AD_ChangeLog_ID + ")");
//            }
//        }
//
//        setAD_ChangeLog_ID(AD_ChangeLog_ID);
        setTrxName(TrxName);
        setAD_Session_ID(AD_Session_ID);

        //
        setAD_Table_ID(AD_Table_ID);
        setAD_Column_ID(AD_Column_ID);
        setRecord_ID(Record_ID);

        //
        setClientOrg(AD_Client_ID, AD_Org_ID);

        //
        setOldValue(OldValue);
    	setNewValue(NewValue);
        if(DisplayType.YesNo == displayType){
        	if(OldValue != null && OldValue != NULL){
        		setOldValue(((Boolean)OldValue)?"Y":"N");
        	}
        	if(NewValue != null && NewValue != NULL){
        		setNewValue(((Boolean)NewValue)?"Y":"N");
        	}
        }
        if(DisplayType.Button == displayType){
        	if(OldValue != null && OldValue != NULL){
        		if(OldValue instanceof Boolean){
        			setOldValue(((Boolean)OldValue)?"Y":"N");
        		}
        	}
        	if(NewValue != null && NewValue != NULL){
        		if(NewValue instanceof Boolean){
        			setNewValue(((Boolean)NewValue)?"Y":"N");
        		}
        	}
        }
        if(DisplayType.isLOB(displayType)){
        	setBinaryValue((byte[])NewValue);
        }
        setAD_ComponentObjectUID(valueUID);
        setAD_ComponentVersion_ID(valueComponentVersionID);
        setOperationType(operationType);
        setChangeLogGroup_ID(changeLogGroupID);
    }		// MChangeLog

    /**
     *      Fill Log with tables to be logged
     */
    public static void fillChangeLog() {

        ArrayList<Integer>	list	= new ArrayList<Integer>();
        String		sql	= "SELECT t.AD_Table_ID FROM AD_Table t " + "WHERE t.IsChangeLog='Y'";		// also inactive
//                                  + " OR EXISTS (SELECT * FROM AD_Column c " + "WHERE t.AD_Table_ID=c.AD_Table_ID AND c.ColumnName='EntityType') " + "ORDER BY t.AD_Table_ID";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Integer(rs.getInt(1)));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }
        
        logTables = list;
        // Convert to Array
        s_changeLog	= new int[list.size()];

        for (int i = 0; i < s_changeLog.length; i++) {

            Integer	id	= (Integer) list.get(i);

            s_changeLog[i]	= id.intValue();
        }

        s_log.info("#" + s_changeLog.length);

    }		// fillChangeLog

    //~--- get methods --------------------------------------------------------

    /**
     *      Do we track changes for this table
     *      @param AD_Table_ID table
     *      @return true if changes are tracked
     */
    public static boolean isLogged(int AD_Table_ID) {

    	
//        if ((s_changeLog == null) || (s_changeLog.length == 0)) {
//            fillChangeLog();
//        }
//    	
//
//        //
//        int	index	= Arrays.binarySearch(s_changeLog, AD_Table_ID);
//
//        return index >= 0;
        
    	if (logTables == null || logTables.size() == 0) {
    		fillChangeLog();
    	}
    	
    	return logTables.contains(AD_Table_ID);
    }		// trackChanges

    /**
     *      Is New Value Null
     *      @return true if null
     */
    public boolean isNewNull() {

        String	value	= getNewValue();

        return (value == null) || value.equals(NULL);

    }		// isNewNull

    /**
     *      Is Old Value Null
     *      @return true if null
     */
    public boolean isOldNull() {

        String	value	= getOldValue();

        return (value == null) || value.equals(NULL);

    }		// isOldNull

    //~--- set methods --------------------------------------------------------

    /**
     *      Set New Value
     *      @param NewValue new
     */
    public void setNewValue(Object NewValue) {

        if (NewValue == null) {
            super.setNewValue(NULL);
        } else {
            super.setNewValue(NewValue.toString());
        }

    }		// setNewValue

    /**
     *      Set Old Value
     *      @param OldValue old
     */
    public void setOldValue(Object OldValue) {

        if (OldValue == null) {
            super.setOldValue(NULL);
        } else {
            super.setOldValue(OldValue.toString());
        }

    }		// setOldValue
    
    
    
    /**
     * Ampliacion especial para performance en persistencia de bitacora
     * """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
     * En lugar de utilizar el save() heredado de PO, es posible utilizar 
     * este método a fin de evitar la determinacion dinámica de campos,
     * para el armado del SQL general que brinda PO.  De esta manera se
     * reducen los tiempos de respuesta para la utilización en produccion.
     * 
     * Ejemplo: importacion de I_Bpartner -> 3m45s vs 0m34s
     * 
     * @return true si fue posible guardar el registro o false en caso contrario
     */
    public boolean insertDirect()
    {
    	try  {

    		/* Columnas a rellenar en tabla AD_Changelog */
    		String sql = " INSERT INTO AD_ChangeLog (AD_ChangeLog_ID, AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, AD_Session_ID, AD_Table_ID, Record_ID, AD_Column_ID, oldValue, newValue, isCustomization, redo, undo, trxName, AD_ComponentObjectUID, operationType, AD_ComponentVersion_ID, binaryValue, ChangelogGroup_ID) " +
    		      		 "                   VALUES (?,               ?,            ?,         ?,        ?,       ?,         ?,       ?,         ?,             ?,           ?,         ?,            ?,        ?,        ?,               ?,    ?,    ?,       ?,                     ?,             ?,                      ?,           ?                ) ";
    		
    		/* Parametros para cada una de las entradas de registro */
    		int col = 1;
        	CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName() );
        	pstmt.setInt(col++, DB.getSQLValueRW(get_TrxName(), "SELECT nextval('seq_ad_changelog')"));
        	pstmt.setInt(col++, getAD_Client_ID());
        	pstmt.setInt(col++, getAD_Org_ID()); 
        	pstmt.setString(col++, isActive()?"Y":"N");
        	pstmt.setTimestamp(col++, getCreated());
        	pstmt.setInt(col++, getCreatedBy());
        	pstmt.setTimestamp(col++, getUpdated());
        	pstmt.setInt(col++, getUpdatedBy()); 
        	pstmt.setInt(col++, getAD_Session_ID()); 
        	pstmt.setInt(col++, getAD_Table_ID()); 
        	pstmt.setInt(col++, getRecord_ID()); 
        	pstmt.setInt(col++, getAD_Column_ID()); 
        	pstmt.setString(col++, getOldValue()); 
        	pstmt.setString(col++, getNewValue()); 
        	pstmt.setString(col++, isCustomization()?"Y":"N"); 
        	pstmt.setString(col++, getRedo());
        	pstmt.setString(col++, getUndo());
        	pstmt.setString(col++, getTrxName()); 
        	pstmt.setString(col++, getAD_ComponentObjectUID());
        	pstmt.setString(col++, getOperationType());
        	pstmt.setInt(col++, getAD_ComponentVersion_ID());
        	pstmt.setBytes(col++, getBinaryValue());
        	pstmt.setInt(col++, getChangeLogGroup_ID());

        	/* Ejecutar consulta y retornar ok */
        	pstmt.executeUpdate();
    		return true;
    	}
    	catch (Exception e) 	{
    		/* Se presento alguna excepcion al ejecutar la consulta */
    		log.log(Level.SEVERE, " Imposible guardar en bitacora! - " + " TableID:" + getAD_Table_ID() + " - RecordID:" + getRecord_ID() + " - ColumnID:" + getAD_Column_ID() + " - NewValue:" +  getNewValue());
    		return false;
    	}
    	
    	
    }
}	// MChangeLog



/*
 * @(#)MChangeLog.java   02.jul 2007
 * 
 *  Fin del fichero MChangeLog.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
