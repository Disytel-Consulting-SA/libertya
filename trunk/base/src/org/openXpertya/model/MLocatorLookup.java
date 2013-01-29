/*
 * @(#)MLocatorLookup.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Level;

/**
 *      Warehouse Locator Lookup Model.
 *      (Lookup Model is model.Lookup.java)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MLocatorLookup.java,v 1.5 2005/03/11 20:28:36 jjanke Exp $
 */
public final class MLocatorLookup extends Lookup implements Serializable {

    /** Descripción de Campo */
    private static int	s_maxRows	= 100;		// how many rows to read

    /** Only Warehouse */
    private int	m_only_Warehouse_ID	= 0;

    /** Storage of data  MLookups */
    private volatile LinkedHashMap	m_lookup	= new LinkedHashMap();

    /** Descripción de Campo */
    protected int	C_Locator_ID;

    /** Context */
    private Properties	m_ctx;

    /** Descripción de Campo */
    private Loader	m_loader;

    /**
     *      Constructor
     *  @param ctx context
     *  @param WindowNo window no
     */
    public MLocatorLookup(Properties ctx, int WindowNo) {

        super(DisplayType.TableDir, WindowNo);
        m_ctx	= ctx;

        //
        m_loader	= new Loader();
        m_loader.start();

    }		// MLocator

    /**
     *  The Lookup contains the key
     *  @param key key
     *  @return true, if lookup contains key
     */
    public boolean containsKey(Object key) {
        return m_lookup.containsKey(key);
    }		// containsKey

    /**
     *  Dispose
     */
    public void dispose() {

        log.fine("C_Locator_ID=" + C_Locator_ID);

        if (m_loader != null) {

            while (m_loader.isAlive()) {
                m_loader.interrupt();
            }
        }

        m_loader	= null;

        if (m_lookup != null) {
            m_lookup.clear();
        }

        m_lookup	= null;

        //
        super.dispose();

    }		// dispose

    /**
     *  Wait until async Load Complete
     */
    public void loadComplete() {

        if (m_loader != null) {

            try {
                m_loader.join();
            } catch (InterruptedException ie) {
                log.log(Level.SEVERE, "Join interrupted", ie);
            }
        }

    }		// loadComplete

    /**
     *      Refresh Values
     *  @return new size of lookup
     */
    public int refresh() {

        log.fine("start");
        m_loader	= new Loader();
        m_loader.start();

        try {
            m_loader.join();
        } catch (InterruptedException ie) {}

        log.info("#" + m_lookup.size());

        return m_lookup.size();

    }		// refresh

    /**
     * @return  a string representation of the object.
     */
    public String toString() {
        return "MLocatorLookup[Size=" + m_lookup.size() + "]";
    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get value
     *  @param key key
     *  @return value value
     */
    public NamePair get(Object key) {

        if (key == null) {
            return null;
        }

        // try cache
        MLocator	loc	= (MLocator) m_lookup.get(key);

        if (loc != null) {
            return new KeyNamePair(loc.getM_Locator_ID(), loc.toString());
        }

        // Not found and waiting for loader
        if (m_loader.isAlive()) {

            log.fine("Waiting for Loader");
            loadComplete();

            // is most current
            loc	= (MLocator) m_lookup.get(key);
        }

        if (loc != null) {
            return new KeyNamePair(loc.getM_Locator_ID(), loc.toString());
        }

        // Try to get it directly
        return getDirect(key, true, null);

    }		// get

    /**
     *      Get underlying fully qualified Table.Column Name
     *  @return Table.ColumnName
     */
    public String getColumnName() {
        return "M_Locator.M_Locator_ID";
    }		// getColumnName

    /**
     *      Return info as ArrayList containing Locator, waits for the loader to finish
     *  @return Collection of lookup values
     */
    public Collection getData() {

        if (m_loader.isAlive()) {

            log.fine("Waiting for Loader");

            try {
                m_loader.join();
            } catch (InterruptedException ie) {
                log.severe("Join interrupted - " + ie.getMessage());
            }
        }

        return m_lookup.values();

    }		// getData

    /**
     *      Return data as sorted ArrayList
     *  @param mandatory mandatory
     *  @param onlyValidated only validated
     *  @param onlyActive only active
     *      @param temporary force load for temporary display
     *  @return ArrayList of lookup values
     */
    public ArrayList getData(boolean mandatory, boolean onlyValidated, boolean onlyActive, boolean temporary) {

        // create list
        Collection	collection	= getData();
        ArrayList	list		= new ArrayList(collection.size());
        Iterator	it		= collection.iterator();

        while (it.hasNext()) {

            MLocator	loc	= (MLocator) it.next();

            if (isValid(loc)) {		// only valid warehouses
                list.add(loc);
            }
        }

        /**
         *     Sort Data
         * MLocator l = new MLocator (m_ctx, 0);
         * if (!mandatory)
         *       list.add (l);
         * Collections.sort (list, l);
         */
        return list;
    }		// getArray

    /**
     *      Get Data Direct from Table
     *  @param keyValue integer key value
     *  @param saveInCache save in cache
     * @param trxName
     *  @return Object directly loaded
     */
    public NamePair getDirect(Object keyValue, boolean saveInCache, String trxName) {

        MLocator	loc	= getMLocator(keyValue, trxName);

        if (loc == null) {
            return null;
        }

        //
        int	key	= loc.getM_Locator_ID();

        if (saveInCache) {
            m_lookup.put(new Integer(key), loc);
        }

        NamePair	retValue	= new KeyNamePair(key, loc.toString());

        return retValue;

    }		// getDirect

    /**
     *      Get Display value
     *  @param value value
     *  @return String to display
     */
    public String getDisplay(Object value) {

        if (value == null) {
            return "";
        }

        //
        NamePair	display	= get(value);

        if (display == null) {
            return "<" + value.toString() + ">";
        }

        return display.toString();

    }		// getDisplay

    /**
     *      Get Data Direct from Table
     *  @param keyValue integer key value
     * @param trxName
     *  @return Object directly loaded
     */
    public MLocator getMLocator(Object keyValue, String trxName) {

        // log.fine( "MLocatorLookup.getDirect " + keyValue.getClass() + "=" + keyValue);
        int	M_Locator_ID	= -1;

        try {
            M_Locator_ID	= Integer.parseInt(keyValue.toString());
        } catch (Exception e) {}

        if (M_Locator_ID == -1) {

            log.log(Level.SEVERE, "Invalid key=" + keyValue);

            return null;
        }

        //
        return new MLocator(m_ctx, M_Locator_ID, trxName);
    }		// getMLocator

    /**
     *      Get Only Wahrehouse
     *      @return warehouse
     */
    public int getOnly_Warehouse_ID() {
        return m_only_Warehouse_ID;
    }		// getOnly_Warehouse_ID

    /**
     *      Is Locator with key valid (Warehouse)
     *      @param locator locator
     *      @return true if valid
     */
    public boolean isValid(MLocator locator) {

        if ((locator == null) || (m_only_Warehouse_ID == 0)) {
            return true;
        }

        return m_only_Warehouse_ID == locator.getM_Warehouse_ID();

    }		// isValid

    /**
     *      Is Locator with key valid (Warehouse)
     *      @param key key
     *      @return true if valid
     */
    public boolean isValid(Object key) {

        if (key == null) {
            return true;
        }

        // try cache
        MLocator	loc	= (MLocator) m_lookup.get(key);

        if (loc == null) {
            loc	= getMLocator(key, null);
        }

        return isValid(loc);

    }		// isValid

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Warehouse restriction
     *      @param only_Warehouse_ID wahrehouse
     */
    public void setOnly_Warehouse_ID(int only_Warehouse_ID) {
        m_only_Warehouse_ID	= only_Warehouse_ID;
    }		// setOnly_Warehouse_ID

    /**
     *      Loader
     */
    class Loader extends Thread implements Serializable {

        /**
         * Constructor ...
         *
         */
        public Loader() {
            super("MLocatorLookup");
        }	// Loader

        /**
         *      Load Lookup
         */
        public void run() {

            // log.config( "MLocatorLookup Loader.run " + m_AD_Column_ID);
            // Set Info
            StringBuffer	sql	= new StringBuffer("SELECT * FROM M_Locator ").append(" WHERE IsActive='Y'");

            if (m_only_Warehouse_ID != 0) {
                sql.append("AND M_Warehouse_ID=").append(m_only_Warehouse_ID);
            }

            String	finalSql	= MRole.getDefault(m_ctx, false).addAccessSQL(sql.toString(), "M_Locator", MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);

            if (isInterrupted()) {

                log.log(Level.SEVERE, "Interrupted");

                return;
            }

            // Reset
            m_lookup.clear();

            int	rows	= 0;

            try {

                PreparedStatement	pstmt	= DB.prepareStatement(finalSql);
                ResultSet		rs	= pstmt.executeQuery();

                // Get first 100 rows
                while (rs.next() && (rows++ < s_maxRows)) {

                    MLocator	loc		= new MLocator(m_ctx, rs, null);
                    int		M_Locator_ID	= loc.getM_Locator_ID();

                    m_lookup.put(new Integer(M_Locator_ID), loc);
                }

                rs.close();
                pstmt.close();

            } catch (SQLException e) {
                log.log(Level.SEVERE, sql.toString(), e);
            }

            log.fine("Complete #" + m_lookup.size());
        }	// run
    }		// Loader
    
	/**
	 * 	Set Product restriction
	 *	@param only_Product_ID Product
	 */
	public void setOnly_Product_ID (int only_Product_ID)
	{
		m_only_Product_ID = only_Product_ID;
	}	//	setOnly_Product_ID
	/**	Only Product					*/
	private int					m_only_Product_ID = 0;

}		// MLocatorLookup



/*
 * @(#)MLocatorLookup.java   02.jul 2007
 * 
 *  Fin del fichero MLocatorLookup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
