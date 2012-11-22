/*
 * @(#)MLookup.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;

/**
 *      An intelligent MutableComboBoxModel, which determines what can be cached.
 *  <pre>
 *      Validated   - SQL is final / not dynamic
 *      AllLoaded   - All Records are loaded
 *
 *              Get Info about Lookup
 *              -       SQL
 *              -       KeyColumn
 *              -       Zoom Target
 *  </pre>
 *      @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *      @version        $Id: MLookup.java,v 1.33 2005/05/14 05:32:16 jjanke Exp $
 */
public final class MLookup extends Lookup implements Serializable {

    /** Inactive Marker Start */
    public static final String	INACTIVE_S	= "~";

    /** Inactive Marker End */
    public static final String	INACTIVE_E	= "~";

    /**
	 * Nombre de la Preference que indica la cantidad máxima de registros a
	 * retornar
	 */
    public static final String LOOKUP_MAX_ROWS_PREFERENCE_NAME = "LookupMaxRowsAllowed";
    
	/**
	 * Number of max rows to load. LookupMaxRowsAllowed en AD_Preference. Valor
	 * por defecto = 1000
	 */
	private int maxRows = 1000; // i.e. Drop Down has max 500 items

    /** Indicator for Null */
    private static Integer	MINUS_ONE	= new Integer(-1);

    /** The Lookup Info Value Object */
    private MLookupInfo	m_info	= null;

    /** Storage of data  Key-NamePair */
    private volatile LinkedHashMap	m_lookup	= new LinkedHashMap();

    /** Inactive records exists */
    private boolean	m_hasInactive	= false;

    /** All Data loaded */
    private boolean	m_allLoaded	= false;

    /* Refreshing - disable cashing */

    /** Descripción de Campo */
    private boolean	m_refreshing	= false;

    /** Save getDirect last return value */
    private HashMap	m_lookupDirect	= null;

    /** Save last unsuccessful */
    private Object	m_directNullKey	= null;

    /** The Data Loader */
    private MLoader	m_loader;

    //

    /**
     *  MLookup Constructor
     *  @param info info
     *  @param TabNo tab no
     */
    public MLookup(MLookupInfo info, int TabNo) {

        super(info.DisplayType, info.WindowNo);
        m_info	= info;
        log.fine(m_info.KeyColumn+"------");

        // load into local lookup, if already cached
        if (MLookupCache.loadFromCache(m_info, m_lookup)) {
            return;
        }

        // Don't load Search or CreatedBy/UpdatedBy
        if ((m_info.DisplayType == DisplayType.Search) || info.IsCreadedUpdatedBy) {
            return;
        }

        // Don't load Parents/Keys
        if (m_info.IsParent || m_info.IsKey) {

            m_hasInactive	= true;		// creates focus listener for dynamic loading

            return;				// required when parent needs to be selected (e.g. price from product)
        }

        //
        m_loader	= new MLoader();

        // if (TabNo != 0)
        // m_loader.setPriority(Thread.MIN_PRIORITY);
        m_loader.start();

        // m_loader.run();

    }		// MLookup

    /**
     *  The Lookup contains the key
     *  @param key key
     *  @return true if key is known
     */
    public boolean containsKey(Object key) {
        return m_lookup.containsKey(key);
    }		// containsKey

    /**
     *  Dispose
     */
    public void dispose() {

        if (m_info != null) {
            log.fine(m_info.KeyColumn + ": dispose");
        }

        if ((m_loader != null) && m_loader.isAlive()) {
            m_loader.interrupt();
        }

        m_loader	= null;

        //
        if (m_lookup != null) {
            m_lookup.clear();
        }

        m_lookup	= null;

        if (m_lookupDirect != null) {
            m_lookupDirect.clear();
        }

        m_lookupDirect	= null;

        //
        m_info	= null;

        //
        super.dispose();

    }		// dispose

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {

        if (obj instanceof MLookup) {

            MLookup	ll	= (MLookup) obj;

            if (ll.m_info.Column_ID == this.m_info.Column_ID) {
                return true;
            }
        }

        return false;

    }		// equals

    /**
     *  Wait until async Load Complete
     */
    public void loadComplete() {

        if ((m_loader != null) && m_loader.isAlive()) {

            try {

                m_loader.join();
                m_loader	= null;

            } catch (InterruptedException ie) {
                log.log(Level.SEVERE, m_info.KeyColumn + ": loadComplete - join interrupted", ie);
            }
        }

    }		// loadComplete

    /**
     *      Refresh & return number of items read.
     *      Get get data of parent lookups
     *  @return no of items read
     */
    public int refresh() {
        return refresh(true);
    }		// refresh

    /**
     *      Refresh & return number of items read
     *      @param loadParent get data of parent lookups
     *  @return no of items read
     */
    public int refresh(boolean loadParent) {

        if (!loadParent && m_info.IsParent) {
            return 0;
        }

        log.fine(m_info.KeyColumn + ": refresh - start");
        m_refreshing	= true;
        m_loader	= new MLoader();
        m_loader.start();
        loadComplete();
        log.fine(m_info.KeyColumn + ": refresh - #" + m_lookup.size());
        m_refreshing	= false;

        return m_lookup.size();

    }		// refresh

    /**
     *      Return Size
     *  @return size
     */
    public int size() {
        return m_lookup.size();
    }		// size

    /**
     * @return  a string representation of the object.
     */
    public String toString() {
        return "MLookup[" + m_info.KeyColumn + ",Column_ID=" + m_info.Column_ID + ",Size=" + m_lookup.size() + ",Validated=" + isValidated() + "-" + getValidation() + "]";
    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get value (name) for key.
     *  If not found return null;
     *  @param key key      (Integer for Keys or String for Lists)
     *  @return value
     */
    public NamePair get(Object key) {

        if ((key == null) || MINUS_ONE.equals(key)) {		// indicator for null
            return null;
        }

        // try cache
        NamePair	retValue	= (NamePair) m_lookup.get(key);

        if (retValue != null) {
            return retValue;
        }

        // Not found and waiting for loader
        if ((m_loader != null) && m_loader.isAlive()) {

            log.finer(((m_info.KeyColumn == null)
                       ? "ID=" + m_info.Column_ID
                       : m_info.KeyColumn) + ": get - waiting for Loader");
            loadComplete();

            // is most current
            retValue	= (NamePair) m_lookup.get(key);

            if (retValue != null) {
                return retValue;
            }
        }

        // Always check for parents - not if we SQL was validated and completely loaded
        if (!m_info.IsParent && m_info.IsValidated && m_allLoaded) {

            log.finer(m_info.KeyColumn + ": get <NULL> - " + key	// + "(" + key.getClass()
                      + "; Size=" + m_lookup.size());

            // log.finest( m_lookup.keySet().toString(), "ContainsKey = " + m_lookup.containsKey(key));
            // also for new values and inactive ones
            return getDirect(key, false, true);		// cache locally
        }

        log.finer(m_info.KeyColumn + ": get - " + key + "; Size=" + m_lookup.size() + "; Validated=" + m_info.IsValidated + "; All Loaded=" + m_allLoaded + "; HasInactive=" + m_hasInactive);

        // never loaded
        if (!m_allLoaded && (m_lookup.size() == 0) && (m_info.DisplayType != DisplayType.Search)) {

            m_loader	= new MLoader();
            m_loader.run();	// sync!
            retValue	= (NamePair) m_lookup.get(key);

            if (retValue != null) {
                return retValue;
            }
        }

        // Try to get it directly
        boolean	cacheLocal	= m_info.IsValidated;

        return getDirect(key, false, cacheLocal);	// do NOT cache

    }							// get

    /**
     *  Get Reference Value
     *  @return Reference Value
     */
    public int getAD_Reference_Value_ID() {
        return m_info.AD_Reference_Value_ID;
    }		// getAD_Reference_Value_ID

    /**
     *      Get underlying fully qualified Table.Column Name
     *  @return Key Column
     */
    public String getColumnName() {
        return m_info.KeyColumn;
    }		// g2etColumnName

    /**
     *      Return info as ArrayList containing Value/KeyNamePair
     *  @param onlyValidated only validated
     *      @param loadParent get Data even for parent lookups
     *  @return List
     */
    private ArrayList getData(boolean onlyValidated, boolean loadParent) {

        if ((m_loader != null) && m_loader.isAlive()) {

            log.fine(((m_info.KeyColumn == null)
                      ? "ID=" + m_info.Column_ID
                      : m_info.KeyColumn) + ": getData *-* waiting for Loader");
            loadComplete();
        }

        // Never Loaded (correctly)
        if (!m_allLoaded || (m_lookup.size() == 0)) {
            refresh(loadParent);
        }

        // already validation included
        if (m_info.IsValidated) {
            return new ArrayList(m_lookup.values());
        }

        if (!m_info.IsValidated && onlyValidated) {
        	m_lookup = new LinkedHashMap();
            refresh(loadParent);
            log.fine(m_info.KeyColumn + ": getData Validated - #" + m_lookup.size());
        }

        return new ArrayList(m_lookup.values());

    }		// getData

    /**
     *      Return data as Array containing Value/KeyNamePair
     *  @param mandatory if not mandatory, an additional empty value is inserted
     *  @param onlyValidated only validated
     *  @param onlyActive only active
     *      @param temporary force load for temporary display
     *  @return list
     */
    public ArrayList getData(boolean mandatory, boolean onlyValidated, boolean onlyActive, boolean temporary) {

        // create list
        ArrayList	list	= getData(onlyValidated, temporary);

        // Remove inactive choices
        if (onlyActive && m_hasInactive) {

            // list from the back
            for (int i = list.size(); i > 0; i--) {

                Object	o	= list.get(i - 1);

                if (o != null) {

                    String	s	= o.toString();

                    if (s.startsWith(INACTIVE_S) && s.endsWith(INACTIVE_E)) {
                        list.remove(i - 1);
                    }
                }
            }
        }

        // Add Optional (empty) selection
        if (!mandatory) {

            NamePair	p	= null;

            if ((m_info.KeyColumn != null) && m_info.KeyColumn.endsWith("_ID")) {
                p	= new KeyNamePair(-1, "");
            } else {
                p	= new ValueNamePair("", "");
            }

            list.add(0, p);
        }

        return list;
    }		// getData

    /**
     *      Get Data Direct from Table.
     *  @param key key
     *  @param saveInCache save in cache for r/w
     *      @param cacheLocal cache locally for r/o
     *  @return value
     */
    public NamePair getDirect(Object key, boolean saveInCache, boolean cacheLocal) {
    	log.fine("En NamePair");
        int	AD_Table_ID	= Env.getContextAsInt(Env.getCtx(), m_info.WindowNo, "AD_Table_ID");

        // Nothing to query
        if ((key == null) || (m_info.QueryDirect == null) || (m_info.QueryDirect.length() == 0)) {
            return null;
        }

        if (key.equals(m_directNullKey)) {
            return null;
        }

        //
        NamePair	directValue	= null;

        if (m_lookupDirect != null)	// Lookup cache
        {

            directValue	= (NamePair) m_lookupDirect.get(key);

            if (directValue != null) {
                return directValue;
            }
        }

        log.finer(m_info.KeyColumn + ": getDirect - " + key + ", SaveInCache=" + saveInCache + ",Local=" + cacheLocal);

        boolean	isNumber	= m_info.KeyColumn.endsWith("_ID");

        try {

            UpdateRecord_IDReports	URR	= new UpdateRecord_IDReports();

            /*
             *  Cuidado! Hacemos la consulta a ad_record_idreportnames_v
             * Si la tabla no tiene nombre entonces me puede retornar
             * un name erroneo... (Aunque la id almacenada sea la correcta)
             *
             * Vamos que antes de hacer esta select debo verificar k mi tabla
             * tiene una columna name, y si no es el caso no ejecuto esto.
             */

            if (m_info.QueryDirect.contains("isRecordID")) {

                log.finest("isRecordID detected. deleting 'isRecordID AND' from sql...");
                m_info.QueryDirect	= m_info.QueryDirect.replace("isRecordID AND", "");

                if (URR.HaveNameColumn(AD_Table_ID) == true) {

                    log.finest("isRecordID detected. 'm_info.QueryDirect' changed to:");
                    m_info.QueryDirect	= "SELECT r, NULL, name, 'Y' " + "FROM ad_record_idreportnames_v " + "WHERE r=?" + " and t='" + m_info.TableName + "' ";
                }
            }

            // SELECT Key, Value, Name FROM ...
            log.finest(m_info.QueryDirect);

            PreparedStatement	pstmt	= DB.prepareStatement(m_info.QueryDirect, PluginUtils.getPluginInstallerTrxName());

            try
            {
            	Integer.parseInt(key.toString());
            	pstmt.setInt(1, Integer.parseInt(key.toString()));
            }
            catch (Exception e)
            {
            	pstmt.setString(1, key.toString());	
            }
            
            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                String	name	= rs.getString(3);

                if (isNumber) {

                    int		keyValue	= rs.getInt(1);
                    KeyNamePair	p		= new KeyNamePair(keyValue, name);

                    if (saveInCache) {		// save if
                        m_lookup.put(new Integer(keyValue), p);
                    }

                    directValue	= p;

                } else {

                    String		value	= rs.getString(2);
                    ValueNamePair	p	= new ValueNamePair(value, name);

                    if (saveInCache) {		// save if
                        m_lookup.put(value, p);
                    }

                    directValue	= p;
                }

                if (rs.next()) {
                    log.log(Level.SEVERE, m_info.KeyColumn + ": getDirect - not unique (first returned) for " + key + " SQL=" + m_info.QueryDirect);
                }

            } else {

                m_directNullKey	= key;
                directValue	= null;
            }

            rs.close();
            pstmt.close();

            if (CLogMgt.isLevelFinest()) {
                log.finest(m_info.KeyColumn + ": getDirect - " + directValue + " - " + m_info);
            }

            /*
             * }
             * else
             * {
             *       log.finest("Controled Error by Zarius - isRecordID detected");
             *       directValue = null;
             * }
             */

        } catch (Exception e) {

            log.log(Level.SEVERE, m_info.KeyColumn + ": getDirect - SQL=" + m_info.QueryDirect + "; Key=" + key, e);
            directValue	= null;
        }

        // Cache Local if not added to R/W cache
        if (cacheLocal &&!saveInCache && (directValue != null)) {

            if (m_lookupDirect == null) {
                m_lookupDirect	= new HashMap();
            }

            m_lookupDirect.put(key, directValue);
        }

        m_hasInactive	= true;

        return directValue;

    }		// getDirect

    /**
     *      Get Display value (name).
     *  If not found return key embedded in inactive signs.
     *  @param key key
     *  @return value
     */
    public String getDisplay(Object key) {

        int	AD_Table_ID	= Env.getContextAsInt(Env.getCtx(), m_info.WindowNo, "AD_Table_ID");
        UpdateRecord_IDReports	URR	= new UpdateRecord_IDReports();

        if (key == null) {
            return "";
        }

        // Begin Modified by Zarius - Dataware - 30/08/2006
        
        // Actualizado por Matías Cap - Disytel
        // Se puede dar el caso que el id de la columna parámetro
        // no sea de una columna sino de un parámetro de un proceso
        // por lo que el id de la tabla (AD_Table_ID) = 0 
        if (AD_Table_ID > 0 && isRecordID() && URR.HaveNameColumn(AD_Table_ID)) {
            return setDisplay(key);
        }

        // Ebd Modified by Zarius - Dataware - 30/08/2006
        Object	display	= get(key);

        if (display == null) {
            return "<" + key.toString() + ">";
        }

        return display.toString();

    }		// getDisplay

    /**
     *  Get Validation SQL
     *  @return Validation SQL
     */
    public String getValidation() {
        return m_info.ValidationCode;
    }		// getValidation

    /**
     *      Get Zoom
     *  @return Zoom Window
     */
    public int getZoom() {
        return m_info.ZoomWindow;
    }		// getZoom

    /**
     *      Get Zoom
     *      @param query query
     *  @return Zoom Window
     */
    public int getZoom(MQuery query) {

        if ((m_info.ZoomWindowPO == 0) || (query == null)) {

            return m_info.ZoomWindow;
        }

        // Se obtiene el valor IsSOTrx de la ventana para utilizarlo en caso de que el
        // registro en el lookup no contenga IsSOTrx como columna.
        boolean wIsSOTrx = "Y".equals(Env.getContext(Env.getCtx(), m_info.WindowNo, "IsSOTrx"));

        // Need to check SO/PO
        boolean	isSOTrx	= DB.isSOTrx(m_info.TableName, query.getWhereClause(false), wIsSOTrx);

        //
        if (!isSOTrx) {
            return m_info.ZoomWindowPO;
        }

        return m_info.ZoomWindow;

    }		// getZoom

    /**
     *      Get Zoom Query String
     *  @return Zoom SQL Where Clause
     */
    public MQuery getZoomQuery() {
        return m_info.ZoomQuery;
    }		// getZoom

    /**
     *  Has inactive elements in list
     *  @return true, if list contains inactive values
     */
    public boolean hasInactive() {
        return m_hasInactive;
    }		// hasInactive

    /**
     *      Is it all loaded
     *  @return true, if all loaded
     */
    public boolean isAllLoaded() {
        return m_allLoaded;
    }		// isAllLoaded

    /**
     * @author Function created by Zarius - Dataware - 30/08/2006
     * @description just return true if the data is Record_ID type
     *
     *  @return boolean
     */
    private boolean isRecordID() {

    	/* 
    	 * Mejora en Performance: Cambiar query por uso de objetos, de modo que use el cache del sistema.
    	 */
    	
    	M_Column col = M_Column.get(Env.getCtx(), m_info.Column_ID);

    	if ((col != null) && (col.getID() > 0) && (col.getColumnName().equalsIgnoreCase("Record_ID")))
    			return true;
    	
    	return false;
    	
    	/*

        String	SQL	= "SELECT columnname " + "FROM ad_column " + "WHERE ad_column_id=?";

        log.finer("// Process by ZariusLi: " + SQL.replace("?", m_info.Column_ID + ""));

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(SQL, PluginUtils.getPluginInstallerTrxName());

            pstmt.setInt(1, m_info.Column_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next() && rs.getString(1).equals("Record_ID")) {
                return true;
            }

            rs.close();

        } catch (Exception e) {
            return false;
        }

        return false;
        */
    }

    /**
     *      Is the List fully Validated
     *  @return true, if validated
     */
    public boolean isValidated() {

        if (m_info == null) {
            return false;
        }

        return m_info.IsValidated;

    }		// isValidated

    //~--- set methods --------------------------------------------------------

    /**
     * @author Function created by Zarius - Dataware - 30/08/2006
     * @description return the name of id value if and only if the field name existe on table, if not, returns key
     *
     *
     * @param key
     *  @return string result
     */
    private String setDisplay(Object key) {

        log.finer("// Process by Zarius: setDisplay(Object key)");

        int	AD_Table_ID	= Env.getContextAsInt(Env.getCtx(), m_info.WindowNo, "AD_Table_ID");
        String	TableName	= "";
        String	Name		= "";
        String	SQL		= "SELECT T.tablename " + "FROM ad_table T, ad_column C " + "WHERE T.ad_table_id=C.ad_table_id " + "and ad_column_id=?";

        try {

            log.finer("// Process by Zarius: " + SQL.replace("?", m_info.Column_ID + ""));

            PreparedStatement	pstmt	= DB.prepareStatement(SQL, PluginUtils.getPluginInstallerTrxName());

            pstmt.setInt(1, m_info.Column_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                TableName	= rs.getString(1);
            }

            rs.close();
            SQL	= "SELECT tablename " + "FROM ad_table " + "WHERE ad_table_id=?";
            log.finer("// Process by Zarius: " + SQL.replace("?", AD_Table_ID + ""));
            pstmt	= DB.prepareStatement(SQL);
            pstmt.setInt(1, AD_Table_ID);
            rs	= pstmt.executeQuery();

            if (rs.next()) {
                Name	= rs.getString(1);
            }

            rs.close();

        } catch (Exception e) {

            log.finer("// Process by Zarius: ----------------- Controled Error -----------------");

            return "<" + key.toString() + ">";
        }

        int	rowID	= Env.getContextAsInt(Env.getCtx(), m_info.WindowNo, "Record_ID");

        // rowID=RecordID now we need to select from ad_table where rowID
        // we need ne table name
        SQL	= "SELECT name " + "FROM " + Name + " " + "WHERE " + Name + "_ID=?";
        log.finer("// Process by Zarius: " + SQL.replace("?", rowID + ""));

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(SQL, PluginUtils.getPluginInstallerTrxName());

            pstmt.setString(1, key.toString());

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            }

            rs.close();

        } catch (Exception e) {

            log.finer("// Process by Zarius: ----------------- Controled Error -----------------");

            return "<" + key.toString() + ">";
        }

        return "<" + key.toString() + ">";
    }

    /**
     *      MLookup Loader
     */
    class MLoader extends Thread implements Serializable {

        /** Descripción de Campo */
        private long	m_startTime	= System.currentTimeMillis();

        /**
         * Constructor ...
         *
         */
        public MLoader() {
            super("MLoader-" + m_info.KeyColumn);
        }	// Loader

        /**
         *      Load Lookup
         */
        public void run() {

            long	startTime	= System.currentTimeMillis();

            MLookupCache.loadStart(m_info);

            String	sql	= m_info.Query;
            log.fine("En Mlookup run con sql= "+ sql);

            maxRows = Util.isEmpty(MPreference
        			.searchCustomPreferenceValue(LOOKUP_MAX_ROWS_PREFERENCE_NAME,
        					Env.getAD_Client_ID(Env.getCtx()),
        					Env.getAD_Org_ID(Env.getCtx()),
        					Env.getAD_User_ID(Env.getCtx()), true)) ? 1000 : Integer
        			.valueOf(MPreference.searchCustomPreferenceValue(
        					LOOKUP_MAX_ROWS_PREFERENCE_NAME, Env.getAD_Client_ID(Env.getCtx()),
        					Env.getAD_Org_ID(Env.getCtx()),
        					Env.getAD_User_ID(Env.getCtx()), true));
            
            sql += " LIMIT "+maxRows+" ";
            
            // not validated
            if (!m_info.IsValidated) {

                String	validation	= Env.parseContext(m_info.ctx, m_info.WindowNo, m_info.ValidationCode, false);
                log.fine("En Mlookup run validation= "+ validation + "m_info.ValidationCode =" + m_info.ValidationCode);

                if ((validation.length() == 0) && (m_info.ValidationCode.length() > 0)) {

                    log.fine(m_info.KeyColumn + ": Loader NOT Validated: " + m_info.ValidationCode);

                    return;
                }

                log.fine(m_info.KeyColumn + ": Loader Validated: " + validation);

                int	posFrom		= sql.lastIndexOf(" FROM ");
                boolean	hasWhere	= sql.indexOf(" WHERE ", posFrom) != -1;

                //
                int	posOrder	= sql.lastIndexOf(" ORDER BY ");

                if (posOrder != -1) {

                    sql	= sql.substring(0, posOrder) + (hasWhere
                            ? " AND "
                            : " WHERE ") + validation + sql.substring(posOrder);

                } else {

                    sql	+= (hasWhere
                            ? " AND "
                            : " WHERE ") + validation;
                }

                if (CLogMgt.isLevelFinest()) {
                    log.fine(m_info.KeyColumn + ": Validation=" + validation);
                }
            }

            // check
            if (isInterrupted()) {

                log.log(Level.SEVERE, m_info.KeyColumn + ": Loader interrupted");

                return;
            }

            //
            if (CLogMgt.isLevelFiner()) {
                Env.setContext(m_info.ctx, Env.WINDOW_MLOOKUP, m_info.Column_ID, m_info.KeyColumn, sql);
            }

            if (CLogMgt.isLevelFinest()) {
                log.fine(m_info.KeyColumn + ": " + sql);
            }

            // Reset
            m_lookup.clear();

            boolean	isNumber	= m_info.KeyColumn.endsWith("_ID");

            m_hasInactive	= false;

            int	rows	= 0;

            try {

                // SELECT Key, Value, Name, IsActive FROM ...
                PreparedStatement	pstmt	= DB.prepareStatement(sql, PluginUtils.getPluginInstallerTrxName());
                ResultSet		rs	= pstmt.executeQuery();

                // Get first ... rows
                m_allLoaded	= true;

                while (rs.next()) {

                    if (rows++ > maxRows) {

                        log.warning(m_info.KeyColumn + ": Loader - Too many records");
                        log.warning(m_info.toString());
                        m_allLoaded	= false;

                        break;
                    }

                    // check for interrupted every 10 rows
                    if ((rows % 20 == 0) && isInterrupted()) {
                        break;
                    }

                    // load data
                    String	name		= rs.getString(3);
                    boolean	isActive	= rs.getString(4).equals("Y");

                    if (!isActive) {

                        name		= INACTIVE_S + name + INACTIVE_E;
                        m_hasInactive	= true;
                    }

                    if (isNumber) {

                        int		key	= rs.getInt(1);
                        KeyNamePair	p	= new KeyNamePair(key, name);

                        m_lookup.put(new Integer(key), p);

                    } else {

                        String		value	= rs.getString(2);
                        ValueNamePair	p	= new ValueNamePair(value, name);

                        m_lookup.put(value, p);
                    }

                    // log.fine( m_info.KeyColumn + ": " + name);
                }

                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                log.log(Level.SEVERE, m_info.KeyColumn + ": Loader - " + sql, e);
            }

            int	size	= m_lookup.size();

            log.finer(m_info.KeyColumn + " (" + m_info.Column_ID + "):"

            // + " ID=" + m_info.AD_Column_ID + " " +
            + " - Loader complete #" + size + " - all=" + m_allLoaded + " - ms=" + String.valueOf(System.currentTimeMillis() - m_startTime) + " (" + String.valueOf(System.currentTimeMillis() - startTime) + ")");

            // if (m_allLoaded)
            MLookupCache.loadEnd(m_info, m_lookup);

        }	// run
    }		// Loader
}		// MLookup



/*
 * @(#)MLookup.java   02.jul 2007
 * 
 *  Fin del fichero MLookup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
