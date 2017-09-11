/*
 * @(#)M_Table.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.OpenXpertya;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

//~--- Importaciones JDK ------------------------------------------------------

import java.lang.reflect.Constructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

/**
 *      Persistent Table Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: M_Table.java,v 1.27 2005/05/01 07:03:49 jjanke Exp $
 */
public class M_Table extends X_AD_Table {

    /** Cache */
    private static CCache	s_cache	= new CCache("AD_Table", 20);

    /** Static Logger */
    private static CLogger	s_log	= CLogger.getCLogger(M_Table.class);

    /** Packages for Model Classes */
    private static final String[]	s_packages	= new String[] { "openXpertya.model",		// Extensions
            "org.openXpertya.model", "org.openXpertya.wf", "org.openXpertya.print", "org.openXpertya.impexp" };

    /** Special Classes */
    private static final String[]	s_special	= new String[] {

        "AD_Table", "org.openXpertya.model.M_Table", "AD_Column", "org.openXpertya.model.M_Column", "AD_Element", "org.openXpertya.model.M_Element", "AD_Window", "org.openXpertya.model.M_Window", "AD_Tab", "org.openXpertya.model.M_Tab", "AD_Field", "org.openXpertya.model.M_Field", "AD_Registration", "org.openXpertya.model.M_Registration", "AD_Tree", "org.openXpertya.model.MTree_Base", "C_ValidCombination", "org.openXpertya.model.MAccount", "GL_Category", "org.openXpertya.model.MGLCategory"

        // AD_Attribute_Value, AD_TreeNode
    };

    /** Columns */
    private M_Column[]	m_columns	= null;

    /** 
     * Cache de owners
     *		Clave: tableName
     *		Valor: [0] coreLevel - [1] owner (package) 
     */
    private static final HashMap<String, String[]> owners_cache = new HashMap<String, String[]>();
    
    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param AD_Table_ID id
     * @param trxName
     */
    public M_Table(Properties ctx, int AD_Table_ID, String trxName) {

        super(ctx, AD_Table_ID, trxName);

        if (AD_Table_ID == 0) {

            // setName (null);
            // setTableName (null);
            setAccessLevel(ACCESSLEVEL_SystemOnly);		// 4
            setEntityType(ENTITYTYPE_UserMaintained);		// U
            setIsChangeLog(false);
            setIsDeleteable(false);
            setIsHighVolume(false);
            setIsSecurityEnabled(false);
            setIsView(false);					// N
            setReplicationType(REPLICATIONTYPE_Local);
        }

    }								// M_Table

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public M_Table(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// M_Table

    /**
     *      After Save
     *      @param newRecord new
     *      @param success success
     *      @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        // Sync Table ID
        if (newRecord) {
            MSequence.createTableSequence(getCtx(), getTableName(), get_TrxName());
        } else {

            MSequence	seq	= MSequence.get(getCtx(), getTableName(), true, null);

            if ((seq == null) || (seq.getID() == 0)) {
                MSequence.createTableSequence(getCtx(), getTableName(), get_TrxName());
            } else if (!seq.getName().equals(getTableName())) {

                seq.setName(getTableName());
                seq.save();
            }
        }

        return success;
    }		// afterSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get M_Table from Cache
     *      @param ctx context
     *      @param AD_Table_ID id
     *      @return M_Table
     */
    public static M_Table get(Properties ctx, int AD_Table_ID) {

        Integer	key		= new Integer(AD_Table_ID);
        M_Table	retValue	= (M_Table) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new M_Table(ctx, AD_Table_ID, null);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    /**
     *      Get M_Table from Cache
     *      @param ctx context
     *      @param tableName case insensitive table name
     *      @return Table
     */
    public static M_Table get(Properties ctx, String tableName) {

        if (tableName == null) {
            return null;
        }

        Iterator	it	= s_cache.values().iterator();

        while (it.hasNext()) {

            M_Table	retValue	= (M_Table) it.next();

            if (tableName.equalsIgnoreCase(retValue.getTableName())) {
                return retValue;
            }
        }

        //
        M_Table			retValue	= null;
        String			sql		= "SELECT * FROM AD_Table WHERE UPPER(TableName)=?";
        PreparedStatement	pstmt		= null;

        try {

            pstmt	= DB.prepareStatement(sql, null);
            pstmt.setString(1, tableName.toUpperCase());

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= new M_Table(ctx, rs, null);
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

        if (retValue != null) {

            Integer	key	= new Integer(retValue.getAD_Table_ID());

            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get

    
    public static String standardTableNameToClassName(String tableName){
    	String	className	= tableName;

        if (className.startsWith("AD_")) {
            className	= className.substring(3);
        } else if (className.startsWith("C_")) {
            className	= className.substring(2);
        } else if (className.startsWith("M_")) {
            className	= className.substring(2);
        } else if (className.startsWith("GL_")){
    		className = className.substring(3);
        } else if (className.startsWith("K_")) {
            className	= className.substring(2);
        } else if (className.startsWith("PA_")) {
            className	= className.substring(3);
        } else if (className.startsWith("R_")) {
            className	= className.substring(2);
        } else if (className.startsWith("S_")) {
            className	= className.substring(2);
        } else if (className.startsWith("W_")) {
            className	= className.substring(2);
        }
        else if (className.startsWith("I_")) {
            className = className.substring(2);
        }

        return Util.replace(className, "_", "");
    }
    
    /**
     * Backward compatability overload 
     */
    public static Class getClass(String tableName)
    {
    	return getClass(tableName, null);
    }
    
    /**
     *      Get Persistency Class for Table
     *      If a packageName is indicated, search for the correspondent component in the package
     *      @param tableName table name
     *      @return class or null
     */
    public static Class getClass(String tableName, String packageName) {

        // s_log.warning("en getClass con tableName= " + tableName);

        // Not supported
//        if ((tableName == null) || tableName.endsWith("_Trl")) {
//            return null;
//        }

    	  if (tableName == null) {
            return null;
          }
    		  
        // Import Tables (Name conflict)
  /*      if (tableName.startsWith("I_")) {

            Class	clazz	= getPOclass("org.openXpertya.model.X_" + tableName);

            if (clazz != null) {
                return clazz;
            }

            // s_log.warning("Not found for " + tableName);

            return null;
        }
*/
        // Special Naming
        for (int i = 0; i < s_special.length; i++) {

            // s_log.warning("en special Naming con s_especial= " + s_special[i++]);

            if (s_special[i++].equals(tableName)) {

                Class	clazz	= getPOclass(s_special[i]);

                if (clazz != null) {
                    return clazz;
                }

                break;
            }
        }

        String	className	= standardTableNameToClassName(tableName);

        // Presuponer packageName como existente, y sobreescribir en caso contrario
        String[] packages = {packageName + "." + PluginConstants.PACKAGE_NAME_MODEL};
        
        // si packageName es nulo, estamos determinando la subclase de PO a instanciar 
        // (todavía no estamos determinando instancias de plugins)
        boolean isPluginTable = false;
       	// determinar el 'component owner' de la tabla, si es una tabla especifica de plugin, setear el package
    	String tableOwnerPackage = getTableOwnerPackage(tableName);
    	if (tableOwnerPackage != null)
    	{
    		// si estamos determinando el PO, setear el package del owner de la tabla
    		if (packageName == null)
    		{
    			String[] newPackage = { tableOwnerPackage + "." + PluginConstants.PACKAGE_NAME_MODEL };
   				packages = newPackage;
    		}
    		isPluginTable = true;
    	}

        // Para los siguientes casos reseteamos el conjunto de packages:
        // 		1) Si estamos determinando subclase de PO, y no es una tabla exclusiva de plugin
        //  	2) Si estamos determinando instancias de plugins, y es una tabla de plugin, pero es el mismo package que el del owner 
        if ((packageName == null && !isPluginTable) || (packageName != null && isPluginTable && packageName.equals(tableOwnerPackage)) )
        	packages = s_packages;
        
        // Search packages
        for (int i = 0; i < packages.length; i++) {

            StringBuffer	name;

			name = new StringBuffer(packages[i]).append(".M").append((tableName.startsWith("I_") ? "I" : ""))
					.append(className);

            /* Forzar o no la validacion de PO stricta segun sea un plugin o no (recibido packageName o no) */
            Class<?>	clazz	= getPOclass(name.toString(), packageName == null);

            if (clazz != null) {
                return clazz;
            }

            // si es una tabla exclusiva de un plugin, buscar la posible superclase LP_ en caso de no existir la M 
            if (isPluginTable)
            {
	            name = new StringBuffer(packages[i]).append(".LP_").append(tableName);
	
	            /* Forzar o no la validacion de PO stricta segun sea un plugin o no (recibido packageName o no) */
	            clazz	= getPOclass(name.toString(), packageName == null);
	
	            if (clazz != null) {
	                return clazz;
	            }
            }
            
        }

        // Default Extension
        Class	clazz	= getPOclass("openXpertya.model.X_" + tableName);

        if (clazz != null) {

            // s_log.warning("en Default Extension  ");

            return clazz;
        }

        // Default
        clazz	= getPOclass("org.openXpertya.model.X_" + tableName);

        if (clazz != null) {

            // s_log.warning("en sDefault= ");

            return clazz;
        }

        return null;

    }		// getClass

    /**
     *      Get Column
     *      @param columnName (case insensitive)
     *      @return column if found
     */
    public M_Column getColumn(String columnName) {

        if ((columnName == null) || (columnName.length() == 0)) {
            return null;
        }

        getColumns(false);

        //
        for (int i = 0; i < m_columns.length; i++) {

            if (columnName.equalsIgnoreCase(m_columns[i].getColumnName())) {
                return m_columns[i];
            }
        }

        return null;

    }		// getColumn

    /**
     *      Get Columns
     *      @param requery requery
     *      @return array of columns
     */
    public M_Column[] getColumns(boolean requery) {

        if ((m_columns != null) &&!requery) {
            return m_columns;
        }

        String	sql	= "SELECT * FROM AD_Column WHERE AD_Table_ID=? ORDER BY ColumnName";
        ArrayList		list	= new ArrayList();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, get_TrxName());
            pstmt.setInt(1, getAD_Table_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new M_Column(getCtx(), rs, get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        //
        m_columns	= new M_Column[list.size()];
        list.toArray(m_columns);

        return m_columns;

    }		// getColumns

    /**
     * Obtengo las columnas clave de la tabla
     * @return columnas clave de la tabla
     */
    public List<String> getKeyColumns(){
    	M_Column[] columns = getColumns(false);
    	List<String> keyColumns = new ArrayList<String>(), parentKeyColumns = new ArrayList<String>();
    	for (int i = 0; i < columns.length; i++) {
			if(columns[i].isKey()){
				keyColumns.add(columns[i].getColumnName());
			}
			if(columns[i].isParent()){
				parentKeyColumns.add(columns[i].getColumnName());
			}
		}
    	if(keyColumns.size() > 0){
    		return keyColumns;
    	}
    	else{
    		return parentKeyColumns;
    	}
    }
    
    
    /**
	 * 	Get Key Columns of Table
	 *	@return key columns
	 */
	public String[] getKeyColumnsAsArray()
	{
		getColumns(false);
		ArrayList<String> list = new ArrayList<String>();
		//
		for (int i = 0; i < m_columns.length; i++)
		{
			M_Column column = m_columns[i];
			if (column.isKey())
				return new String[]{column.getColumnName()};
			if (column.isParent())
				list.add(column.getColumnName());
		}
		String[] retValue = new String[list.size()];
		retValue = list.toArray(retValue);
		return retValue;
	}	//	getKeyColumns
    
    /**
     *      Get PO Class Instance
     *      @param Record_ID record
     *      @param trxName
     *      @return PO for Record or null
     */
    public PO getPO(int Record_ID, String trxName) {

        String	tableName	= getTableName();
        Class	clazz		= getClass(tableName);

        if (clazz == null) {

            log.log(Level.INFO, "(id) - Class not found for " + tableName);

            return null;
        }

        boolean	errorLogged	= false;

        try {

//          if (trxName != null)
//          {
            try {

                Constructor	constructor	= clazz.getDeclaredConstructor(new Class[] { Properties.class, int.class, String.class });
                PO	po	= (PO) constructor.newInstance(new Object[] { getCtx(), new Integer(Record_ID), trxName });

                return po;

            } catch (Exception e) {
                log.warning("No transaction Constructor for " + clazz + " (" + e.getMessage() + ")");
            }

//          }
//          Constructor constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class});
//          PO po = (PO)constructor.newInstance(new Object[] {getCtx(), new Integer(Record_ID)});
//          return po;
        } catch (Exception e) {

            if (e.getCause() != null) {

                Throwable	t	= e.getCause();

                log.log(Level.SEVERE, "(id) - Table=" + tableName + ",Class=" + clazz, t);
                errorLogged	= true;

                if (t instanceof Exception) {
                    log.saveError("Error", (Exception) e.getCause());
                } else {
                    log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
                }

            } else {

                log.log(Level.SEVERE, "(id) - Table=" + tableName + ",Class=" + clazz, e);
                errorLogged	= true;
                log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
            }
        }

        if (!errorLogged) {
            log.log(Level.SEVERE, "(id) - Not found - Table=" + tableName + ", Record_ID=" + Record_ID);
        }

        return null;

    }		// getPO

    /**
     *      Get PO Class Instance
     *      @param rs result set
     * @param trxName
     *      @return PO for Record or null
     */
    public PO getPO(ResultSet rs, String trxName) {

        String	tableName	= getTableName();
        Class	clazz		= getClass(tableName);

        if (clazz == null) {

            log.log(Level.INFO, "(rs) - Class not found for " + tableName);

            return null;
        }

        boolean	errorLogged	= false;

        try {

            Constructor	constructor	= clazz.getDeclaredConstructor(new Class[] { Properties.class, ResultSet.class, String.class });
            PO	po	= (PO) constructor.newInstance(new Object[] { getCtx(), rs, trxName });

            return po;

        } catch (Exception e) {

            log.log(Level.SEVERE, "(rs) - Table=" + tableName + ",Class=" + clazz, e);
            errorLogged	= true;
            log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
        }

        if (!errorLogged) {
            log.log(Level.SEVERE, "(rs) - Not found - Table=" + tableName);
        }

        return null;

    }		// getPO

    /**
     *      Get PO Class Instance
     *      @param whereClause where clause
     *      @param trxName transaction
     *      @return PO for Record or null
     */
    public PO getPO(String whereClause, String trxName) {

        if ((whereClause == null) || (whereClause.length() == 0)) {
            return null;
        }

        //
        PO	po	= null;
        String	sql	= "SELECT * FROM " + getTableName() + " WHERE " + whereClause;
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                po	= getPO(rs, trxName);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {

            log.log(Level.SEVERE, sql, e);
            log.saveError("Error", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return po;

    }		// getPO
    
    /**
     * General PO para tablas que no tienen PO para ese record ID
     * @param recorID
     * @param trxName
     * @return
     */
    public PO getGeneralPO(int recorID, String trxName){
    	return new GeneralPO(getCtx(), 
    							recorID, 
    							null, 
    							getTableID(getTableName()), 
    							trxName);
    }

    public PO getGeneralPO(String whereClause, String trxName){
    	PO	po	= null;
        String	sql	= "SELECT * FROM " + getTableName() + " WHERE " + whereClause;
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                po	= new GeneralPO(getCtx(), -1, rs, getTableID(getTableName()), trxName);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
            log.saveError("Error", e);
        }
        return po;
    }
    
    
    private static Class getPOclass(String className) {
    	return getPOclass(className, true);
    }
    /**
     *      Get PO class
     *      @param className fully qualified class name
     *      @return class or null
     */
    private static Class getPOclass(String className, boolean strictPO) {

        s_log.fine("en getPoClass con  className= " + className);

        try {

            Class	clazz	= Class.forName(className);
            if (!strictPO)
            	return clazz;

            // Make sure that it is a PO class
            Class	superClazz	= clazz.getSuperclass();

            while (superClazz != null) {

                if (superClazz == PO.class) {

                    s_log.fine("Found:--> " + className);

                    return clazz;
                }

                superClazz	= superClazz.getSuperclass();
            }

        } catch (Exception e) {
            s_log.finest("Not found: <-->" + className);
        }

        return null;

    }		// getPOclass

    /**
     *      Get SQL Create
     *      @return create table DDL
     */
    public String getSQLCreate() {

        StringBuffer	sb	= new StringBuffer("CREATE TABLE ").append(getTableName()).append(" (");

        //
        boolean		hasPK		= false;
        boolean		hasParents	= false;
        StringBuffer	constraints	= new StringBuffer();

        getColumns(true);

        for (int i = 0; i < m_columns.length; i++) {

            if (i > 0) {
                sb.append(", ");
            }

            M_Column	column	= m_columns[i];

            sb.append(column.getSQLDDL());

            //
            if (column.isKey()) {
                hasPK	= true;
            }

            if (column.isParent()) {
                hasParents	= true;
            }

            String	constraint	= column.getConstraint(getTableName());

            if ((constraint != null) && (constraint.length() > 0)) {
                constraints.append(", ").append(constraint);
            }
        }

        // Multi Column PK
        if (!hasPK && hasParents) {

            StringBuffer	cols	= new StringBuffer();

            for (int i = 0; i < m_columns.length; i++) {

                M_Column	column	= m_columns[i];

                if (!column.isParent()) {
                    continue;
                }

                if (cols.length() > 0) {
                    cols.append(", ");
                }

                cols.append(column.getColumnName());
            }

            sb.append(", CONSTRAINT ").append(getTableName()).append("_Key PRIMARY KEY (").append(cols).append(")");
        }

        sb.append(constraints).append(")");

        return sb.toString();

    }		// getSQLCreate
    
    
    public static int getTableID(String tablename)
    {
//    	return DB.getSQLValue(null, " SELECT AD_Table_ID FROM AD_Table WHERE tablename = ? ", tablename);
    	return getTableID(tablename, PluginUtils.getPluginInstallerTrxName());
    }
    
    public static int getTableID(String tablename, String trxName)
    {
    	/* Workaround contabilidad de Facturas
    	 * """""""""""""""""""""""""""""""""""
    	 * 
    	 * Problema: 	Al iniciar el servidor Libertya, en particular el EjbModule openXpertya/Status, por algún motivo (todavía no determinado)
    	 * 				no se obtiene una conexión a BBDD para cuando el classloader carga la clase X_C_Invoice y por consiguiente la resolución 
    	 * 				del TableID para la tabla C_Invoice queda en -1.  Esto luego impide la aplicación contable de factuaras dado que 
    	 * 				X_C_Invoice.Table_ID queda en -1 y por lo tanto en la clase Doc, línea 247, la condición (AD_Table_ID == MInvoice.Table_ID)  
    	 * 				siempre es falsa, impidiendo contabilizar facturas.
    	 * 
    	 *  Workaround:	Esta solución temporal implica validar si efectivamente existe una conexión establecida a la BBDD y en caso de no ser así
    	 *  			se realiza la misma.  Considerando que este problema únicamente se encuentra ligado con el inicio del servidor Libertya,
    	 *  			el startupEnvironment se realiza con el argumento isClient en false. Para el cliente swing esta lógica es transparente,
    	 *  			dado que en esos casos no existen problemas en la gestión de la conexión de la BBDD.	 
    	 */
    	if (!org.openXpertya.util.DB.isConnected()) {
    		OpenXpertya.startupEnvironment(false);
    	}
    	return DB.getSQLValue(trxName, " SELECT AD_Table_ID FROM AD_Table WHERE upper(trim(tablename)) = upper(trim(?)) ", tablename);
    }
    
    /**
     * Determina el nombre del package de una tabla, según su component (owner)
     * @param tableName
     * @return el nombre del package, o null si es corelevel 0 (System Core)
     */
    private static String getTableOwnerPackage(String tableName)
    {
    	/* en la cache? */
    	String[] owner = owners_cache.get(tableName);
    	if (owner != null)
    	{
    		// si está en la cache y el corelevel NO es cero, retornar el package name del owner de la tabla
    		// en caso contrario retornar null
    		if (!"0".equals(owner[0]))
    			return owner[1];
    		else
    			return null;
    	}

    	String aPackageName = null;
    	String sql = " select corelevel, packageName from ad_table t inner join ad_componentversion cv on t.ad_componentversion_id = cv.ad_componentversion_id inner join ad_component c on cv.ad_component_id = c.ad_component_id where tablename = '" + tableName + "'";

    	try
    	{
    		PreparedStatement stmt = DB.prepareStatement(sql, PluginUtils.getPluginInstallerTrxName());
    		ResultSet rs = stmt.executeQuery();
    		if (rs.next())
    		{
    			int coreLevel = rs.getInt(1);
    			if (coreLevel != 0)
    				aPackageName = rs.getString(2);
    			
    			/* guardar en cache */
    			owners_cache.put(tableName, new String[]{Integer.toString(coreLevel), aPackageName});
    		}
    	}
    	catch (Exception e)
    	{
    		s_log.log(Level.SEVERE, sql, e);
    		e.printStackTrace();
    	}
    	return aPackageName;
    }
    
    /** Cache para getIdentifierColumns() */
    static HashMap<String, Vector<String>> columns = null;
    /**
     * Retorna un vector con las columnas marcadas como identificador, para una tabla dada
     * @param trxName
     * @return
     */
    public static Vector<String> getIdentifierColumns(String trxName, String tableName)
    {
    	// en cache?
		if (columns == null || columns.get(tableName) == null)
		{
			
			columns = new HashMap<String, Vector<String>>();
			Vector<String> tempCols = new Vector<String>();
			try
			{
				String sql =
					" select c.columnname " +
					" from ad_table t  " +
					" inner join ad_column c on t.ad_table_id = c.ad_table_id " +
					" where t.tablename = ? " +
					" and c.isidentifier = 'Y' " +
					" order by seqno ASC ";
				
				PreparedStatement stmt = DB.prepareStatement(sql, trxName);
				stmt.setString(1, tableName);
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next())
					tempCols.add(rs.getString(1));
				
				columns.put(tableName, tempCols);
			}
			catch (Exception e)	{
				e.printStackTrace();
				return columns.get(tableName);
			}
		}
		return columns.get(tableName);
    }

	/**
	 * Get PO class instance
	 * @param whereClause
	 * @param params
	 * @param trxName
	 * @return
	 */
	public PO getPO(String whereClause, Object[] params, String trxName)
	{
		if (whereClause == null || whereClause.length() == 0)
			return null;
		//
		PO po = null;
		POInfo info = POInfo.getPOInfo(getCtx(), getAD_Table_ID(), trxName);
		if (info == null) return null;
		StringBuffer sqlBuffer = info.buildSelect();
		sqlBuffer.append(" WHERE ").append(whereClause);
		String sql = sqlBuffer.toString(); 
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			if (params != null && params.length > 0) 
			{
				for (int i = 0; i < params.length; i++)
				{
					pstmt.setObject(i+1, params[i]);
				}
			}
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				po = getPO(rs, trxName);
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
			log.saveError("Error", e);
		}
		try
		{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		
		return po;
	}

	//TODO Hernandez
	public static String getTableName (Properties ctx, int AD_Table_ID)
	{
		return M_Table.get(ctx, AD_Table_ID).getTableName();
	}	//	getTableName
    
}	// M_Table



/*
 * @(#)M_Table.java   02.jul 2007
 * 
 *  Fin del fichero M_Table.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
