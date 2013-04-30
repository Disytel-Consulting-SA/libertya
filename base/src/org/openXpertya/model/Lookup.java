/*
 * @(#)Lookup.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.NamePair;
import org.openXpertya.util.ValueNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 *      Base Class for MLookup, MLocator.
 *  as well as for MLocation, MAccount (only single value)
 *  Maintains selectable data as NamePairs in ArrayList
 *  The objects itself may be shared by the lookup implementation (ususally HashMap)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: Lookup.java,v 1.21 2005/03/11 20:28:34 jjanke Exp $
 */
public abstract class Lookup extends AbstractListModel implements MutableComboBoxModel, Serializable {

    /** The List */
    protected volatile ArrayList	p_data	= new ArrayList();

    /** Temporary Data */
    private Object[]	m_tempData	= null;

    /** Logger */
    protected CLogger	log	= CLogger.getCLogger(getClass());

    /** Window No */
    private int	m_WindowNo;

    /** Display Type */
    private int	m_displayType;

    /** The Selected Item */
    private volatile Object	m_selectedObject;

    /**
     *  Lookup
     *      @param displayType display type
     *      @param windowNo window no
     */
    public Lookup(int displayType, int windowNo) {

        m_displayType	= displayType;
        m_WindowNo	= windowNo;

    }		// Lookup

    /**
     *  Add Element at the end
     *  @param anObject object
     */
    public void addElement(Object anObject) {

        p_data.add(anObject);
        fireIntervalAdded(this, p_data.size() - 1, p_data.size() - 1);

        if ((p_data.size() == 1) && (m_selectedObject == null) && (anObject != null)) {
            setSelectedItem(anObject);
        }

    }		// addElement

    /**
     *  The Lookup contains the key
     *  @param key key
     *  @return true if contains key
     */
    public abstract boolean containsKey(Object key);

    /**
     *  Dispose - clear items w/o firing events
     */
    public void dispose() {

        if (p_data != null) {
            p_data.clear();
        }

        p_data			= null;
        m_selectedObject	= null;
        m_tempData		= null;

    }		// dispose

    /**
     *  Fill ComboBox with old saved data (if exists) or all data available
     *  @param restore if true, use saved data - else fill it with all data
     */
    public void fillComboBox(boolean restore) {

        if (restore && (m_tempData != null)) {

            Object	obj	= m_selectedObject;

            p_data.clear();

            // restore old data
            p_data	= new ArrayList(m_tempData.length);

            for (int i = 0; i < m_tempData.length; i++) {
                p_data.add(m_tempData[i]);
            }

            m_tempData	= null;

            // if nothing selected, select first
            if ((obj == null) && (p_data.size() > 0)) {
                obj	= p_data.get(0);
            }

            setSelectedItem(obj);
            fireContentsChanged(this, 0, p_data.size());

            return;
        }

        if (p_data != null) {
            fillComboBox(false, false, false, false);
        }

    }		// fillComboBox

    public void fillComboBox(boolean mandatory, boolean onlyValidated, boolean onlyActive, boolean temporary) {
    	fillComboBox(mandatory, onlyValidated, onlyActive, temporary, true);
    }
    
    /**
     *  Fill ComboBox with lookup data (async using Worker).
     *  - try to maintain selected item
     *  @param mandatory  has mandatory data only (i.e. no "null" selection)
     *  @param onlyValidated only validated
     *  @param onlyActive onlt active
     *  @param temporary  save current values - restore via fillComboBox (true)
     */
    public void fillComboBox(boolean mandatory, boolean onlyValidated, boolean onlyActive, boolean temporary, boolean fireContentsChanged) {

        long	startTime	= System.currentTimeMillis();

        // Save current data
        if (temporary) {

            int	size	= p_data.size();

            m_tempData	= new Object[size];

            // We need to do a deep copy, so store it in Array
            p_data.toArray(m_tempData);

            // for (int i = 0; i < size; i++)
            // m_tempData[i] = p_data.get(i);
        }

        Object	obj	= m_selectedObject;

        p_data.clear();

        // may cause delay *** The Actual Work ***
        p_data	= getData(mandatory, onlyValidated, onlyActive, temporary);

        // Selected Object changed
        if (obj != m_selectedObject) {

            log.finest(getColumnName() + ": fillComboBox - SelectedValue Changed=" + obj + "->" + m_selectedObject);
            obj	= m_selectedObject;
        }

        // if nothing selected & mandatory, select first
        if ((obj == null) && mandatory && (p_data.size() > 0)) {

            obj			= p_data.get(0);
            m_selectedObject	= obj;
            log.finest(getColumnName() + ": fillComboBox - SelectedValue SetToFirst=" + obj);

            // fireContentsChanged(this, -1, -1);
        }

        if (fireContentsChanged)
        	fireContentsChanged(this, 0, p_data.size());

        if (p_data.size() == 0) {
            log.fine(getColumnName() + ": fillComboBox - #0 - ms=" + String.valueOf(System.currentTimeMillis() - startTime));
        } else {
            log.fine(getColumnName() + ": fillComboBox - #" + p_data.size() + " - ms=" + String.valueOf(System.currentTimeMillis() - startTime));
        }

    }		// fillComboBox

    /**
     *  Insert Element At
     *  @param anObject object
     *  @param index index
     */
    public void insertElementAt(Object anObject, int index) {

        p_data.add(index, anObject);
        fireIntervalAdded(this, index, index);

    }		// insertElementAt

    /**
     *  Wait until async Load Complete
     */
    public void loadComplete() {}	// loadComplete

    /**
     *      Put Value
     *  @param key key
     *  @param value value
     */
    public void put(int key, String value) {

        NamePair	pp	= new KeyNamePair(key, value);

        addElement(pp);

    }		// put

    /**
     *      Put Value
     *  @param key key
     *  @param value value
     */
    public void put(String key, String value) {

        NamePair	pp	= new ValueNamePair(key, value);

        addElement(pp);

    }		// put

    /**
     *      Refresh Values - default implementation
     *  @return size
     */
    public int refresh() {
        return 0;
    }		// refresh

    /**
     *  Empties the list.
     */
    public void removeAllElements() {

        if (p_data.size() > 0) {

            int	firstIndex	= 0;
            int	lastIndex	= p_data.size() - 1;

            p_data.clear();
            m_selectedObject	= null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
        }

    }		// removeAllElements

    /**
     *  Remove Item
     *  @param anObject object
     */
    public void removeElement(Object anObject) {

        int	index	= p_data.indexOf(anObject);

        if (index != -1) {
            removeElementAt(index);
        }

    }		// removeItem

    /**
     *  Remove Item at index
     *  @param index index
     */
    public void removeElementAt(int index) {

        if (getElementAt(index) == m_selectedObject) {

            if (index == 0) {

                setSelectedItem((getSize() == 1)
                                ? null
                                : getElementAt(index + 1));

            } else {
                setSelectedItem(getElementAt(index - 1));
            }
        }

        p_data.remove(index);
        fireIntervalRemoved(this, index, index);

    }		// removeElementAt

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Object of Key Value
     *  @param key key
     *  @return Object or null
     */
    public abstract NamePair get(Object key);

    /**
     *      Get underlying fully qualified Table.Column Name.
     *      Used for VLookup.actionButton (Zoom)
     *  @return column name
     */
    public abstract String getColumnName();

    /**
     *  Fill ComboBox with Data (Value/KeyNamePair)
     *  @param mandatory  has mandatory data only (i.e. no "null" selection)
     *  @param onlyValidated only validated
     *  @param onlyActive only active
     *      @param temporary force load for temporary display
     *  @return ArrayList
     */
    public abstract ArrayList getData(boolean mandatory, boolean onlyValidated, boolean onlyActive, boolean temporary);

    /**
     *      Get Data Direct from Table.
     *      Default implementation - does not requery
     *  @param key key
     *  @param saveInCache save in cache for r/w
     *      @param cacheLocal cache locally for r/o
     *  @return value
     */
    public NamePair getDirect(Object key, boolean saveInCache, boolean cacheLocal) {
        return get(key);
    }		// getDirect

    /**
     *      Get Display of Key Value
     *  @param key key
     *  @return String
     */
    public abstract String getDisplay(Object key);

    /**
     *      Get Display Type
     *      @return display type
     */
    public int getDisplayType() {
        return m_displayType;
    }		// getDisplayType

    /**
     *  Get Element at Index
     *  @param index index
     *  @return value
     */
    public Object getElementAt(int index) {
        return p_data.get(index);
    }		// getElementAt

    /**
     * Returns the index-position of the specified object in the list.
     *
     * @param anObject object
     * @return an int representing the index position, where 0 is
     *         the first position
     */
    public int getIndexOf(Object anObject) {
        return p_data.indexOf(anObject);
    }		// getIndexOf

    /**
     *  Return previously selected Item
     *  @return value
     */
    public Object getSelectedItem() {
        return m_selectedObject;
    }		// getSelectedItem

    /**
     *  Get Size of Model
     *  @return size
     */
    public int getSize() {
        return p_data.size();
    }		// getSize

    /**
     *  Get dynamic Validation SQL (none)
     *  @return validation
     */
    public String getValidation() {
        return "";
    }		// getValidation

    /**
     *      Get Window No
     *      @return Window No
     */
    public int getWindowNo() {
        return m_WindowNo;
    }		// getWindowNo

    /**
     *      Get Zoom - default implementation
     *  @return Zoom Window
     */
    public int getZoom() {
        return 0;
    }		// getZoom

    /**
     *      Get Zoom - default implementation
     *
     * @param query
     *  @return Zoom Window
     */
    public int getZoom(MQuery query) {
        return 0;
    }		// getZoom

    /**
     *      Get Zoom Query String - default implementation
     *  @return Zoom Query
     */
    public MQuery getZoomQuery() {
        return null;
    }		// getZoomQuery

    /**
     *  Has Inactive records - default implementation
     *  @return true if inactive
     */
    public boolean hasInactive() {
        return false;
    }

    /**
     *      Is Validated - default implementation
     *  @return true if validated
     */
    public boolean isValidated() {
        return true;
    }		// isValidated

    //~--- set methods --------------------------------------------------------

    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
    public void setSelectedItem(Object anObject) {

        if (((m_selectedObject != null) &&!m_selectedObject.equals(anObject)) || ((m_selectedObject == null) && (anObject != null))) {

            if (p_data.contains(anObject) || (anObject == null)) {

                m_selectedObject	= anObject;

                // Log.trace(s_ll, "Lookup.setSelectedItem", anObject);

            } else {

                m_selectedObject	= null;
                log.fine(getColumnName() + ": setSelectedItem - Set to NULL");
            }

            // if (m_worker == null || !m_worker.isAlive())
            fireContentsChanged(this, -1, -1);
        }

    }		// setSelectedItem
    
	/**
	 * Is this lookup model populated
	 * @return boolean
	 */
	public boolean isLoaded() 
	{
		return m_loaded;
	}
	private boolean					m_loaded;

	/**
	 * Set lookup model as mandatory, use in loading data
	 * @param flag
	 */
	public void setMandatory(boolean flag)
	{
		m_mandatory = flag;
	}
	private boolean 				m_mandatory;

	/**
	 * Is lookup model mandatory
	 * @return boolean
	 */
	public boolean isMandatory()
	{
		return m_mandatory;
	}

	
}	// Lookup



/*
 * @(#)Lookup.java   02.jul 2007
 * 
 *  Fin del fichero Lookup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
